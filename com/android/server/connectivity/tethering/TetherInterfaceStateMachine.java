package com.android.server.connectivity.tethering;

import android.net.INetworkStatsService;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.MessageUtils;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;

public class TetherInterfaceStateMachine
  extends StateMachine
{
  private static final int BASE_IFACE = 327780;
  public static final int CMD_INTERFACE_DOWN = 327784;
  public static final int CMD_IPV6_TETHER_UPDATE = 327793;
  public static final int CMD_IP_FORWARDING_DISABLE_ERROR = 327788;
  public static final int CMD_IP_FORWARDING_ENABLE_ERROR = 327787;
  public static final int CMD_SET_DNS_FORWARDERS_ERROR = 327791;
  public static final int CMD_START_TETHERING_ERROR = 327789;
  public static final int CMD_STOP_TETHERING_ERROR = 327790;
  public static final int CMD_TETHER_CONNECTION_CHANGED = 327792;
  public static final int CMD_TETHER_REQUESTED = 327782;
  public static final int CMD_TETHER_UNREQUESTED = 327783;
  private static final boolean DBG = false;
  private static final String TAG = "TetherInterfaceSM";
  private static final String USB_NEAR_IFACE_ADDR = "192.168.42.129";
  private static final int USB_PREFIX_LENGTH = 24;
  private static final boolean VDBG = false;
  private static final String WIFI_HOST_IFACE_ADDR = "192.168.43.1";
  private static final int WIFI_HOST_IFACE_PREFIX_LENGTH = 24;
  private static final String WIGIG_HOST_IFACE_ADDR = "192.168.50.1";
  private static final int WIGIG_HOST_IFACE_PREFIX_LENGTH = 24;
  private static final Class[] messageClasses = { TetherInterfaceStateMachine.class };
  private static final SparseArray<String> sMagicDecoderRing = MessageUtils.findMessageNames(messageClasses);
  private final IPv6TetheringInterfaceServices mIPv6TetherSvc;
  private final String mIfaceName;
  private final State mInitialState;
  private final int mInterfaceType;
  private boolean mIpv6TetheringEnabled;
  private int mLastError;
  private String mMyUpstreamIfaceName;
  private final INetworkManagementService mNMService;
  private final INetworkStatsService mStatsService;
  private final IControlsTethering mTetherController;
  private final State mTetheredState;
  private final State mUnavailableState;
  
  public TetherInterfaceStateMachine(String paramString, Looper paramLooper, int paramInt, INetworkManagementService paramINetworkManagementService, INetworkStatsService paramINetworkStatsService, IControlsTethering paramIControlsTethering)
  {
    super(paramString, paramLooper);
    this.mNMService = paramINetworkManagementService;
    this.mStatsService = paramINetworkStatsService;
    this.mTetherController = paramIControlsTethering;
    this.mIfaceName = paramString;
    this.mInterfaceType = paramInt;
    this.mIPv6TetherSvc = new IPv6TetheringInterfaceServices(this.mIfaceName, this.mNMService);
    this.mIpv6TetheringEnabled = false;
    this.mLastError = 0;
    this.mInitialState = new InitialState();
    addState(this.mInitialState);
    this.mTetheredState = new TetheredState();
    addState(this.mTetheredState);
    this.mUnavailableState = new UnavailableState();
    addState(this.mUnavailableState);
    setInitialState(this.mInitialState);
  }
  
  public TetherInterfaceStateMachine(String paramString, Looper paramLooper, int paramInt, INetworkManagementService paramINetworkManagementService, INetworkStatsService paramINetworkStatsService, IControlsTethering paramIControlsTethering, boolean paramBoolean)
  {
    this(paramString, paramLooper, paramInt, paramINetworkManagementService, paramINetworkStatsService, paramIControlsTethering);
    this.mIpv6TetheringEnabled = paramBoolean;
  }
  
  private boolean configureIfaceIp(boolean paramBoolean)
  {
    String str;
    if (this.mInterfaceType == 1) {
      str = "192.168.42.129";
    }
    try
    {
      InterfaceConfiguration localInterfaceConfiguration = this.mNMService.getInterfaceConfig(this.mIfaceName);
      if (localInterfaceConfiguration != null)
      {
        localInterfaceConfiguration.setLinkAddress(new LinkAddress(NetworkUtils.numericToInetAddress(str), 24));
        if (!paramBoolean) {
          break label105;
        }
        localInterfaceConfiguration.setInterfaceUp();
      }
      for (;;)
      {
        localInterfaceConfiguration.clearFlag("running");
        this.mNMService.setInterfaceConfig(this.mIfaceName, localInterfaceConfiguration);
        return true;
        if (this.mInterfaceType == 0)
        {
          str = "192.168.43.1";
          break;
        }
        if (this.mInterfaceType == 3)
        {
          str = "192.168.50.1";
          break;
        }
        return true;
        label105:
        localInterfaceConfiguration.setInterfaceDown();
      }
      return false;
    }
    catch (Exception localException)
    {
      Log.e("TetherInterfaceSM", "Error configuring interface " + this.mIfaceName, localException);
    }
  }
  
  private void maybeLogMessage(State paramState, int paramInt) {}
  
  public int interfaceType()
  {
    return this.mInterfaceType;
  }
  
  class InitialState
    extends State
  {
    InitialState() {}
    
    public void enter()
    {
      TetherInterfaceStateMachine.-get8(TetherInterfaceStateMachine.this).notifyInterfaceStateChange(TetherInterfaceStateMachine.-get1(TetherInterfaceStateMachine.this), TetherInterfaceStateMachine.this, 1, TetherInterfaceStateMachine.-get4(TetherInterfaceStateMachine.this));
    }
    
    public boolean processMessage(Message paramMessage)
    {
      TetherInterfaceStateMachine.-wrap1(TetherInterfaceStateMachine.this, this, paramMessage.what);
      switch (paramMessage.what)
      {
      default: 
        return false;
      case 327782: 
        TetherInterfaceStateMachine.-set0(TetherInterfaceStateMachine.this, 0);
        TetherInterfaceStateMachine.-wrap2(TetherInterfaceStateMachine.this, TetherInterfaceStateMachine.-get9(TetherInterfaceStateMachine.this));
        return true;
      case 327784: 
        TetherInterfaceStateMachine.-wrap2(TetherInterfaceStateMachine.this, TetherInterfaceStateMachine.-get10(TetherInterfaceStateMachine.this));
        return true;
      }
      TetherInterfaceStateMachine.-get0(TetherInterfaceStateMachine.this).updateUpstreamIPv6LinkProperties((LinkProperties)paramMessage.obj);
      return true;
    }
  }
  
  class TetheredState
    extends State
  {
    TetheredState() {}
    
    private void cleanupUpstream()
    {
      if (TetherInterfaceStateMachine.-get5(TetherInterfaceStateMachine.this) != null) {}
      try
      {
        TetherInterfaceStateMachine.-get7(TetherInterfaceStateMachine.this).forceUpdate();
        try
        {
          TetherInterfaceStateMachine.-get6(TetherInterfaceStateMachine.this).stopInterfaceForwarding(TetherInterfaceStateMachine.-get1(TetherInterfaceStateMachine.this), TetherInterfaceStateMachine.-get5(TetherInterfaceStateMachine.this));
          try
          {
            TetherInterfaceStateMachine.-get6(TetherInterfaceStateMachine.this).disableNat(TetherInterfaceStateMachine.-get1(TetherInterfaceStateMachine.this), TetherInterfaceStateMachine.-get5(TetherInterfaceStateMachine.this));
            TetherInterfaceStateMachine.-set1(TetherInterfaceStateMachine.this, null);
            return;
          }
          catch (Exception localException1)
          {
            for (;;) {}
          }
        }
        catch (Exception localException2)
        {
          for (;;) {}
        }
      }
      catch (Exception localException3)
      {
        for (;;) {}
      }
    }
    
    public void enter()
    {
      if (!TetherInterfaceStateMachine.-wrap0(TetherInterfaceStateMachine.this, true))
      {
        TetherInterfaceStateMachine.-set0(TetherInterfaceStateMachine.this, 10);
        TetherInterfaceStateMachine.-wrap2(TetherInterfaceStateMachine.this, TetherInterfaceStateMachine.-get2(TetherInterfaceStateMachine.this));
        return;
      }
      for (;;)
      {
        try
        {
          TetherInterfaceStateMachine.-get6(TetherInterfaceStateMachine.this).tetherInterface(TetherInterfaceStateMachine.-get1(TetherInterfaceStateMachine.this));
          if ((!TetherInterfaceStateMachine.-get3(TetherInterfaceStateMachine.this)) || (TetherInterfaceStateMachine.-get0(TetherInterfaceStateMachine.this).start()))
          {
            TetherInterfaceStateMachine.-get8(TetherInterfaceStateMachine.this).notifyInterfaceStateChange(TetherInterfaceStateMachine.-get1(TetherInterfaceStateMachine.this), TetherInterfaceStateMachine.this, 2, TetherInterfaceStateMachine.-get4(TetherInterfaceStateMachine.this));
            return;
          }
        }
        catch (Exception localException)
        {
          Log.e("TetherInterfaceSM", "Error Tethering: " + localException.toString());
          TetherInterfaceStateMachine.-set0(TetherInterfaceStateMachine.this, 6);
          TetherInterfaceStateMachine.-wrap2(TetherInterfaceStateMachine.this, TetherInterfaceStateMachine.-get2(TetherInterfaceStateMachine.this));
          return;
        }
        Log.e("TetherInterfaceSM", "Failed to start IPv6TetheringInterfaceServices");
      }
    }
    
    public void exit()
    {
      TetherInterfaceStateMachine.-get0(TetherInterfaceStateMachine.this).stop();
      cleanupUpstream();
      try
      {
        TetherInterfaceStateMachine.-get6(TetherInterfaceStateMachine.this).untetherInterface(TetherInterfaceStateMachine.-get1(TetherInterfaceStateMachine.this));
        TetherInterfaceStateMachine.-wrap0(TetherInterfaceStateMachine.this, false);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          TetherInterfaceStateMachine.-set0(TetherInterfaceStateMachine.this, 7);
          Log.e("TetherInterfaceSM", "Failed to untether interface: " + localException.toString());
        }
      }
    }
    
    public boolean processMessage(Message paramMessage)
    {
      TetherInterfaceStateMachine.-wrap1(TetherInterfaceStateMachine.this, this, paramMessage.what);
      boolean bool2 = true;
      boolean bool1;
      switch (paramMessage.what)
      {
      case 327785: 
      case 327786: 
      default: 
        bool1 = false;
      case 327783: 
      case 327784: 
      case 327792: 
        do
        {
          do
          {
            return bool1;
            TetherInterfaceStateMachine.-wrap2(TetherInterfaceStateMachine.this, TetherInterfaceStateMachine.-get2(TetherInterfaceStateMachine.this));
            return true;
            TetherInterfaceStateMachine.-wrap2(TetherInterfaceStateMachine.this, TetherInterfaceStateMachine.-get10(TetherInterfaceStateMachine.this));
            return true;
            paramMessage = (String)paramMessage.obj;
            if (TetherInterfaceStateMachine.-get5(TetherInterfaceStateMachine.this) != null) {
              break;
            }
            bool1 = bool2;
          } while (paramMessage == null);
          if (TetherInterfaceStateMachine.-get5(TetherInterfaceStateMachine.this) == null) {
            break;
          }
          bool1 = bool2;
        } while (TetherInterfaceStateMachine.-get5(TetherInterfaceStateMachine.this).equals(paramMessage));
        cleanupUpstream();
        if (paramMessage != null) {}
        try
        {
          TetherInterfaceStateMachine.-get6(TetherInterfaceStateMachine.this).enableNat(TetherInterfaceStateMachine.-get1(TetherInterfaceStateMachine.this), paramMessage);
          TetherInterfaceStateMachine.-get6(TetherInterfaceStateMachine.this).startInterfaceForwarding(TetherInterfaceStateMachine.-get1(TetherInterfaceStateMachine.this), paramMessage);
          TetherInterfaceStateMachine.-set1(TetherInterfaceStateMachine.this, paramMessage);
          return true;
        }
        catch (Exception paramMessage)
        {
          Log.e("TetherInterfaceSM", "Exception enabling Nat: " + paramMessage.toString());
          TetherInterfaceStateMachine.-set0(TetherInterfaceStateMachine.this, 8);
          TetherInterfaceStateMachine.-wrap2(TetherInterfaceStateMachine.this, TetherInterfaceStateMachine.-get2(TetherInterfaceStateMachine.this));
          return true;
        }
      case 327793: 
        TetherInterfaceStateMachine.-get0(TetherInterfaceStateMachine.this).updateUpstreamIPv6LinkProperties((LinkProperties)paramMessage.obj);
        return true;
      }
      TetherInterfaceStateMachine.-set0(TetherInterfaceStateMachine.this, 5);
      TetherInterfaceStateMachine.-wrap2(TetherInterfaceStateMachine.this, TetherInterfaceStateMachine.-get2(TetherInterfaceStateMachine.this));
      return true;
    }
  }
  
  class UnavailableState
    extends State
  {
    UnavailableState() {}
    
    public void enter()
    {
      TetherInterfaceStateMachine.-set0(TetherInterfaceStateMachine.this, 0);
      TetherInterfaceStateMachine.-get8(TetherInterfaceStateMachine.this).notifyInterfaceStateChange(TetherInterfaceStateMachine.-get1(TetherInterfaceStateMachine.this), TetherInterfaceStateMachine.this, 0, TetherInterfaceStateMachine.-get4(TetherInterfaceStateMachine.this));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/tethering/TetherInterfaceStateMachine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */