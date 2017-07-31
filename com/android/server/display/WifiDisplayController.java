package com.android.server.display;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplaySessionInfo;
import android.media.RemoteDisplay;
import android.media.RemoteDisplay.Listener;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pWfdInfo;
import android.os.Handler;
import android.os.Parcelable.Creator;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.util.Slog;
import android.view.Surface;
import com.android.internal.util.DumpUtils.Dump;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import libcore.util.Objects;

final class WifiDisplayController
  implements DumpUtils.Dump
{
  private static final int CONNECTION_TIMEOUT_SECONDS = 30;
  private static final int CONNECT_MAX_RETRIES = 3;
  private static final int CONNECT_RETRY_DELAY_MILLIS = 500;
  private static final boolean DEBUG = SystemProperties.getBoolean("persist.debug.wfdcdbg", false);
  private static final boolean DEBUGV = SystemProperties.getBoolean("persist.debug.wfdcdbgv", false);
  private static final int DEFAULT_CONTROL_PORT = 7236;
  private static final int DISCOVER_PEERS_INTERVAL_MILLIS = 10000;
  private static final int MAX_THROUGHPUT = 50;
  private static final int RTSP_TIMEOUT_SECONDS = 30;
  private static final int RTSP_TIMEOUT_SECONDS_CERT_MODE = 120;
  private static final String TAG = "WifiDisplayController";
  private WifiDisplay mAdvertisedDisplay;
  private int mAdvertisedDisplayFlags;
  private int mAdvertisedDisplayHeight;
  private Surface mAdvertisedDisplaySurface;
  private int mAdvertisedDisplayWidth;
  private final ArrayList<WifiP2pDevice> mAvailableWifiDisplayPeers = new ArrayList();
  private WifiP2pDevice mCancelingDevice;
  private WifiP2pDevice mConnectedDevice;
  private WifiP2pGroup mConnectedDeviceGroupInfo;
  private WifiP2pDevice mConnectingDevice;
  private int mConnectionRetriesLeft;
  private final Runnable mConnectionTimeout = new Runnable()
  {
    public void run()
    {
      if ((WifiDisplayController.-get5(WifiDisplayController.this) != null) && (WifiDisplayController.-get5(WifiDisplayController.this) == WifiDisplayController.-get8(WifiDisplayController.this)))
      {
        Slog.i("WifiDisplayController", "Timed out waiting for Wifi display connection after 30 seconds: " + WifiDisplayController.-get5(WifiDisplayController.this).deviceName);
        WifiDisplayController.-wrap8(WifiDisplayController.this, true);
      }
    }
  };
  private final Context mContext;
  private WifiP2pDevice mDesiredDevice;
  private WifiP2pDevice mDisconnectingDevice;
  private final Runnable mDiscoverPeers = new Runnable()
  {
    public void run()
    {
      WifiDisplayController.-wrap15(WifiDisplayController.this);
    }
  };
  private boolean mDiscoverPeersInProgress;
  private Object mExtRemoteDisplay;
  private final Handler mHandler;
  private final Listener mListener;
  private NetworkInfo mNetworkInfo;
  private RemoteDisplay mRemoteDisplay;
  private boolean mRemoteDisplayConnected;
  private String mRemoteDisplayInterface;
  private final Runnable mRtspTimeout = new Runnable()
  {
    public void run()
    {
      if ((WifiDisplayController.-get3(WifiDisplayController.this) == null) || ((WifiDisplayController.-get14(WifiDisplayController.this) == null) && (WifiDisplayController.-get11(WifiDisplayController.this) == null)) || (WifiDisplayController.-get15(WifiDisplayController.this))) {
        return;
      }
      Slog.i("WifiDisplayController", "Timed out waiting for Wifi display RTSP connection after 30 seconds: " + WifiDisplayController.-get3(WifiDisplayController.this).deviceName);
      WifiDisplayController.-wrap8(WifiDisplayController.this, true);
    }
  };
  private boolean mScanRequested;
  private WifiP2pDevice mThisDevice;
  private boolean mWfdEnabled;
  private boolean mWfdEnabling;
  private boolean mWifiDisplayCertMode;
  private boolean mWifiDisplayOnSetting;
  private int mWifiDisplayWpsConfig = 4;
  private final WifiP2pManager.Channel mWifiP2pChannel;
  private boolean mWifiP2pEnabled;
  private final WifiP2pManager mWifiP2pManager;
  private final BroadcastReceiver mWifiP2pReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      boolean bool;
      if (paramAnonymousContext.equals("android.net.wifi.p2p.STATE_CHANGED")) {
        if (paramAnonymousIntent.getIntExtra("wifi_p2p_state", 1) == 2)
        {
          bool = true;
          if (WifiDisplayController.-get0()) {
            Slog.d("WifiDisplayController", "Received WIFI_P2P_STATE_CHANGED_ACTION: enabled=" + bool);
          }
          WifiDisplayController.-wrap11(WifiDisplayController.this, bool);
        }
      }
      do
      {
        do
        {
          return;
          bool = false;
          break;
          if (paramAnonymousContext.equals("android.net.wifi.p2p.PEERS_CHANGED"))
          {
            if (WifiDisplayController.-get0()) {
              Slog.d("WifiDisplayController", "Received WIFI_P2P_PEERS_CHANGED_ACTION.");
            }
            WifiDisplayController.-wrap9(WifiDisplayController.this);
            return;
          }
          if (paramAnonymousContext.equals("android.net.wifi.p2p.CONNECTION_STATE_CHANGE"))
          {
            paramAnonymousContext = (NetworkInfo)paramAnonymousIntent.getParcelableExtra("networkInfo");
            if (WifiDisplayController.-get0()) {
              Slog.d("WifiDisplayController", "Received WIFI_P2P_CONNECTION_CHANGED_ACTION: networkInfo=" + paramAnonymousContext);
            }
            WifiDisplayController.-wrap7(WifiDisplayController.this, paramAnonymousContext);
            return;
          }
        } while (!paramAnonymousContext.equals("android.net.wifi.p2p.THIS_DEVICE_CHANGED"));
        WifiDisplayController.-set8(WifiDisplayController.this, (WifiP2pDevice)paramAnonymousIntent.getParcelableExtra("wifiP2pDevice"));
      } while (!WifiDisplayController.-get0());
      Slog.d("WifiDisplayController", "Received WIFI_P2P_THIS_DEVICE_CHANGED_ACTION: mThisDevice= " + WifiDisplayController.-get17(WifiDisplayController.this));
    }
  };
  
  public WifiDisplayController(Context paramContext, Handler paramHandler, Listener paramListener)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.mListener = paramListener;
    this.mWifiP2pManager = ((WifiP2pManager)paramContext.getSystemService("wifip2p"));
    this.mWifiP2pChannel = this.mWifiP2pManager.initialize(paramContext, paramHandler.getLooper(), null);
    paramHandler = new IntentFilter();
    paramHandler.addAction("android.net.wifi.p2p.STATE_CHANGED");
    paramHandler.addAction("android.net.wifi.p2p.PEERS_CHANGED");
    paramHandler.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
    paramHandler.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
    paramContext.registerReceiver(this.mWifiP2pReceiver, paramHandler, null, this.mHandler);
    paramContext = new ContentObserver(this.mHandler)
    {
      public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
      {
        WifiDisplayController.-wrap18(WifiDisplayController.this);
      }
    };
    paramHandler = this.mContext.getContentResolver();
    paramHandler.registerContentObserver(Settings.Global.getUriFor("wifi_display_on"), false, paramContext);
    paramHandler.registerContentObserver(Settings.Global.getUriFor("wifi_display_certification_on"), false, paramContext);
    paramHandler.registerContentObserver(Settings.Global.getUriFor("wifi_display_wps_config"), false, paramContext);
    updateSettings();
  }
  
  private void advertiseDisplay(final WifiDisplay paramWifiDisplay, final Surface paramSurface, final int paramInt1, final int paramInt2, final int paramInt3)
  {
    if ((!Objects.equal(this.mAdvertisedDisplay, paramWifiDisplay)) || (this.mAdvertisedDisplaySurface != paramSurface)) {
      break label85;
    }
    for (;;)
    {
      final WifiDisplay localWifiDisplay = this.mAdvertisedDisplay;
      final Surface localSurface = this.mAdvertisedDisplaySurface;
      this.mAdvertisedDisplay = paramWifiDisplay;
      this.mAdvertisedDisplaySurface = paramSurface;
      this.mAdvertisedDisplayWidth = paramInt1;
      this.mAdvertisedDisplayHeight = paramInt2;
      this.mAdvertisedDisplayFlags = paramInt3;
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          if ((localSurface != null) && (paramSurface != localSurface))
          {
            WifiDisplayController.-get13(WifiDisplayController.this).onDisplayDisconnected();
            if (paramWifiDisplay != null)
            {
              if (paramWifiDisplay.hasSameAddress(localWifiDisplay)) {
                break label154;
              }
              WifiDisplayController.-get13(WifiDisplayController.this).onDisplayConnecting(paramWifiDisplay);
            }
          }
          for (;;)
          {
            if ((paramSurface != null) && (paramSurface != localSurface)) {
              WifiDisplayController.-get13(WifiDisplayController.this).onDisplayConnected(paramWifiDisplay, paramSurface, paramInt1, paramInt2, paramInt3);
            }
            return;
            if ((localWifiDisplay == null) || (localWifiDisplay.hasSameAddress(paramWifiDisplay))) {
              break;
            }
            WifiDisplayController.-get13(WifiDisplayController.this).onDisplayConnectionFailed();
            break;
            label154:
            if (!paramWifiDisplay.equals(localWifiDisplay)) {
              WifiDisplayController.-get13(WifiDisplayController.this).onDisplayChanged(paramWifiDisplay);
            }
          }
        }
      });
      label85:
      return;
      if ((this.mAdvertisedDisplayWidth == paramInt1) && (this.mAdvertisedDisplayHeight == paramInt2)) {
        if (this.mAdvertisedDisplayFlags == paramInt3) {
          break;
        }
      }
    }
  }
  
  private int computeFeatureState()
  {
    if (!this.mWifiP2pEnabled) {
      return 1;
    }
    if (this.mWifiDisplayOnSetting) {
      return 3;
    }
    return 2;
  }
  
  private void connect(WifiP2pDevice paramWifiP2pDevice)
  {
    if ((this.mDesiredDevice == null) || (this.mDesiredDevice.deviceAddress.equals(paramWifiP2pDevice.deviceAddress))) {
      if ((this.mConnectedDevice != null) && (!this.mConnectedDevice.deviceAddress.equals(paramWifiP2pDevice.deviceAddress))) {
        break label121;
      }
    }
    while (!this.mWfdEnabled)
    {
      Slog.i("WifiDisplayController", "Ignoring request to connect to Wifi display because the  feature is currently disabled: " + paramWifiP2pDevice.deviceName);
      return;
      if (DEBUG) {
        Slog.d("WifiDisplayController", "connect: nothing to do, already connecting to " + describeWifiP2pDevice(paramWifiP2pDevice));
      }
      return;
      label121:
      if (this.mDesiredDevice == null)
      {
        if (DEBUG) {
          Slog.d("WifiDisplayController", "connect: nothing to do, already connected to " + describeWifiP2pDevice(paramWifiP2pDevice) + " and not part way through " + "connecting to a different device.");
        }
        return;
      }
    }
    this.mDesiredDevice = paramWifiP2pDevice;
    this.mConnectionRetriesLeft = 3;
    updateConnection();
  }
  
  private static WifiDisplay createWifiDisplay(WifiP2pDevice paramWifiP2pDevice)
  {
    return new WifiDisplay(paramWifiP2pDevice.deviceAddress, paramWifiP2pDevice.deviceName, null, true, paramWifiP2pDevice.wfdInfo.isSessionAvailable(), false);
  }
  
  private static String describeWifiP2pDevice(WifiP2pDevice paramWifiP2pDevice)
  {
    if (paramWifiP2pDevice != null) {
      return paramWifiP2pDevice.toString().replace('\n', ',');
    }
    return "null";
  }
  
  private static String describeWifiP2pGroup(WifiP2pGroup paramWifiP2pGroup)
  {
    if (paramWifiP2pGroup != null) {
      return paramWifiP2pGroup.toString().replace('\n', ',');
    }
    return "null";
  }
  
  private void disconnect()
  {
    this.mDesiredDevice = null;
    updateConnection();
  }
  
  private void dump()
  {
    Slog.d("WifiDisplayController", "mWifiDisplayOnSetting=" + this.mWifiDisplayOnSetting);
    Slog.d("WifiDisplayController", "mWifiP2pEnabled=" + this.mWifiP2pEnabled);
    Slog.d("WifiDisplayController", "mWfdEnabled=" + this.mWfdEnabled);
    Slog.d("WifiDisplayController", "mWfdEnabling=" + this.mWfdEnabling);
    Slog.d("WifiDisplayController", "mNetworkInfo=" + this.mNetworkInfo);
    Slog.d("WifiDisplayController", "mScanRequested=" + this.mScanRequested);
    Slog.d("WifiDisplayController", "mDiscoverPeersInProgress=" + this.mDiscoverPeersInProgress);
    Slog.d("WifiDisplayController", "mDesiredDevice=" + describeWifiP2pDevice(this.mDesiredDevice));
    Slog.d("WifiDisplayController", "mConnectingDisplay=" + describeWifiP2pDevice(this.mConnectingDevice));
    Slog.d("WifiDisplayController", "mDisconnectingDisplay=" + describeWifiP2pDevice(this.mDisconnectingDevice));
    Slog.d("WifiDisplayController", "mCancelingDisplay=" + describeWifiP2pDevice(this.mCancelingDevice));
    Slog.d("WifiDisplayController", "mConnectedDevice=" + describeWifiP2pDevice(this.mConnectedDevice));
    Slog.d("WifiDisplayController", "mConnectionRetriesLeft=" + this.mConnectionRetriesLeft);
    Slog.d("WifiDisplayController", "mRemoteDisplay=" + this.mRemoteDisplay);
    Slog.d("WifiDisplayController", "mRemoteDisplayInterface=" + this.mRemoteDisplayInterface);
    Slog.d("WifiDisplayController", "mRemoteDisplayConnected=" + this.mRemoteDisplayConnected);
    Slog.d("WifiDisplayController", "mAdvertisedDisplay=" + this.mAdvertisedDisplay);
    Slog.d("WifiDisplayController", "mAdvertisedDisplaySurface=" + this.mAdvertisedDisplaySurface);
    Slog.d("WifiDisplayController", "mAdvertisedDisplayWidth=" + this.mAdvertisedDisplayWidth);
    Slog.d("WifiDisplayController", "mAdvertisedDisplayHeight=" + this.mAdvertisedDisplayHeight);
    Slog.d("WifiDisplayController", "mAdvertisedDisplayFlags=" + this.mAdvertisedDisplayFlags);
    Slog.d("WifiDisplayController", "mAvailableWifiDisplayPeers: size=" + this.mAvailableWifiDisplayPeers.size());
    Iterator localIterator = this.mAvailableWifiDisplayPeers.iterator();
    while (localIterator.hasNext())
    {
      WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)localIterator.next();
      Slog.d("WifiDisplayController", "  " + describeWifiP2pDevice(localWifiP2pDevice));
    }
  }
  
  private static Inet4Address getInterfaceAddress(WifiP2pGroup paramWifiP2pGroup)
  {
    try
    {
      Object localObject = NetworkInterface.getByName(paramWifiP2pGroup.getInterface());
      localObject = ((NetworkInterface)localObject).getInetAddresses();
      while (((Enumeration)localObject).hasMoreElements())
      {
        InetAddress localInetAddress = (InetAddress)((Enumeration)localObject).nextElement();
        if ((localInetAddress instanceof Inet4Address)) {
          return (Inet4Address)localInetAddress;
        }
      }
    }
    catch (SocketException localSocketException)
    {
      Slog.w("WifiDisplayController", "Could not obtain address of network interface " + paramWifiP2pGroup.getInterface(), localSocketException);
      return null;
    }
    Slog.w("WifiDisplayController", "Could not obtain address of network interface " + paramWifiP2pGroup.getInterface() + " because it had no IPv4 addresses.");
    return null;
  }
  
  private static int getPortNumber(WifiP2pDevice paramWifiP2pDevice)
  {
    if ((paramWifiP2pDevice.deviceName.startsWith("DIRECT-")) && (paramWifiP2pDevice.deviceName.endsWith("Broadcom"))) {
      return 8554;
    }
    return 7236;
  }
  
  private WifiDisplaySessionInfo getSessionInfo(WifiP2pGroup paramWifiP2pGroup, int paramInt)
  {
    if ((paramWifiP2pGroup == null) || (paramWifiP2pGroup.getOwner() == null)) {
      return null;
    }
    Inet4Address localInet4Address = getInterfaceAddress(paramWifiP2pGroup);
    boolean bool;
    String str1;
    String str2;
    if (paramWifiP2pGroup.getOwner().deviceAddress.equals(this.mThisDevice.deviceAddress))
    {
      bool = false;
      str1 = paramWifiP2pGroup.getOwner().deviceAddress + " " + paramWifiP2pGroup.getNetworkName();
      str2 = paramWifiP2pGroup.getPassphrase();
      if (localInet4Address == null) {
        break label131;
      }
    }
    label131:
    for (paramWifiP2pGroup = localInet4Address.getHostAddress();; paramWifiP2pGroup = "")
    {
      paramWifiP2pGroup = new WifiDisplaySessionInfo(bool, paramInt, str1, str2, paramWifiP2pGroup);
      if (DEBUG) {
        Slog.d("WifiDisplayController", paramWifiP2pGroup.toString());
      }
      return paramWifiP2pGroup;
      bool = true;
      break;
    }
  }
  
  private void handleConnectionChanged(NetworkInfo paramNetworkInfo)
  {
    this.mNetworkInfo = paramNetworkInfo;
    if ((this.mWfdEnabled) && (paramNetworkInfo.isConnected())) {
      if ((this.mDesiredDevice != null) || (this.mWifiDisplayCertMode)) {
        this.mWifiP2pManager.requestGroupInfo(this.mWifiP2pChannel, new WifiP2pManager.GroupInfoListener()
        {
          public void onGroupInfoAvailable(WifiP2pGroup paramAnonymousWifiP2pGroup)
          {
            boolean bool = false;
            if (paramAnonymousWifiP2pGroup == null) {
              return;
            }
            if (WifiDisplayController.-get0()) {
              Slog.d("WifiDisplayController", "Received group info: " + WifiDisplayController.-wrap4(paramAnonymousWifiP2pGroup));
            }
            if ((WifiDisplayController.-get5(WifiDisplayController.this) == null) || (paramAnonymousWifiP2pGroup.contains(WifiDisplayController.-get5(WifiDisplayController.this))))
            {
              if ((WifiDisplayController.-get8(WifiDisplayController.this) != null) && (!paramAnonymousWifiP2pGroup.contains(WifiDisplayController.-get8(WifiDisplayController.this)))) {
                break label349;
              }
              if (WifiDisplayController.-get19(WifiDisplayController.this))
              {
                if (paramAnonymousWifiP2pGroup.getOwner() != null) {
                  bool = paramAnonymousWifiP2pGroup.getOwner().deviceAddress.equals(WifiDisplayController.-get17(WifiDisplayController.this).deviceAddress);
                }
                if ((!bool) || (!paramAnonymousWifiP2pGroup.getClientList().isEmpty())) {
                  break label357;
                }
                WifiDisplayController.-set3(WifiDisplayController.this, WifiDisplayController.-set5(WifiDisplayController.this, null));
                WifiDisplayController.-set2(WifiDisplayController.this, paramAnonymousWifiP2pGroup);
                WifiDisplayController.-wrap16(WifiDisplayController.this);
              }
            }
            label349:
            label357:
            while ((WifiDisplayController.-get5(WifiDisplayController.this) != null) || (WifiDisplayController.-get8(WifiDisplayController.this) != null))
            {
              if ((WifiDisplayController.-get5(WifiDisplayController.this) != null) && (WifiDisplayController.-get5(WifiDisplayController.this) == WifiDisplayController.-get8(WifiDisplayController.this)))
              {
                Slog.i("WifiDisplayController", "Connected to Wifi display: " + WifiDisplayController.-get5(WifiDisplayController.this).deviceName);
                WifiDisplayController.-get12(WifiDisplayController.this).removeCallbacks(WifiDisplayController.-get7(WifiDisplayController.this));
                WifiDisplayController.-set2(WifiDisplayController.this, paramAnonymousWifiP2pGroup);
                WifiDisplayController.-set1(WifiDisplayController.this, WifiDisplayController.-get5(WifiDisplayController.this));
                WifiDisplayController.-set3(WifiDisplayController.this, null);
                WifiDisplayController.-wrap16(WifiDisplayController.this);
              }
              return;
              Slog.i("WifiDisplayController", "Aborting connection to Wifi display because the current P2P group does not contain the device we expected to find: " + WifiDisplayController.-get5(WifiDisplayController.this).deviceName + ", group info was: " + WifiDisplayController.-wrap4(paramAnonymousWifiP2pGroup));
              WifiDisplayController.-wrap8(WifiDisplayController.this, false);
              return;
              WifiDisplayController.-wrap6(WifiDisplayController.this);
              return;
            }
            WifiDisplayController localWifiDisplayController1 = WifiDisplayController.this;
            WifiDisplayController localWifiDisplayController2 = WifiDisplayController.this;
            if (bool) {}
            for (WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)paramAnonymousWifiP2pGroup.getClientList().iterator().next();; localWifiP2pDevice = paramAnonymousWifiP2pGroup.getOwner())
            {
              WifiDisplayController.-set3(localWifiDisplayController1, WifiDisplayController.-set5(localWifiDisplayController2, localWifiP2pDevice));
              break;
            }
          }
        });
      }
    }
    do
    {
      return;
      this.mConnectedDeviceGroupInfo = null;
      if ((this.mConnectingDevice != null) || (this.mConnectedDevice != null)) {
        disconnect();
      }
    } while (!this.mWfdEnabled);
    requestPeers();
  }
  
  private void handleConnectionFailure(boolean paramBoolean)
  {
    int i = 0;
    Slog.i("WifiDisplayController", "Wifi display connection failed!");
    final Object localObject;
    Handler localHandler;
    if (this.mDesiredDevice != null)
    {
      if (this.mConnectionRetriesLeft <= 0) {
        break label69;
      }
      localObject = this.mDesiredDevice;
      localHandler = this.mHandler;
      localObject = new Runnable()
      {
        public void run()
        {
          if ((WifiDisplayController.-get8(WifiDisplayController.this) == localObject) && (WifiDisplayController.-get6(WifiDisplayController.this) > 0))
          {
            WifiDisplayController localWifiDisplayController = WifiDisplayController.this;
            WifiDisplayController.-set4(localWifiDisplayController, WifiDisplayController.-get6(localWifiDisplayController) - 1);
            Slog.i("WifiDisplayController", "Retrying Wifi display connection.  Retries left: " + WifiDisplayController.-get6(WifiDisplayController.this));
            WifiDisplayController.-wrap14(WifiDisplayController.this);
          }
        }
      };
      if (!paramBoolean) {
        break label62;
      }
    }
    for (;;)
    {
      localHandler.postDelayed((Runnable)localObject, i);
      return;
      label62:
      i = 500;
    }
    label69:
    disconnect();
  }
  
  private void handlePeersChanged()
  {
    requestPeers();
  }
  
  private void handleScanFinished()
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        WifiDisplayController.-get13(WifiDisplayController.this).onScanFinished();
      }
    });
  }
  
  private void handleScanResults()
  {
    int j = this.mAvailableWifiDisplayPeers.size();
    final WifiDisplay[] arrayOfWifiDisplay = (WifiDisplay[])WifiDisplay.CREATOR.newArray(j);
    int i = 0;
    while (i < j)
    {
      WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)this.mAvailableWifiDisplayPeers.get(i);
      arrayOfWifiDisplay[i] = createWifiDisplay(localWifiP2pDevice);
      updateDesiredDevice(localWifiP2pDevice);
      i += 1;
    }
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        WifiDisplayController.-get13(WifiDisplayController.this).onScanResults(arrayOfWifiDisplay);
      }
    });
  }
  
  private void handleScanStarted()
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        WifiDisplayController.-get13(WifiDisplayController.this).onScanStarted();
      }
    });
  }
  
  private void handleStateChanged(boolean paramBoolean)
  {
    this.mWifiP2pEnabled = paramBoolean;
    updateWfdEnableState();
  }
  
  private static boolean isPrimarySinkDeviceType(int paramInt)
  {
    return (paramInt == 1) || (paramInt == 3);
  }
  
  private static boolean isWifiDisplay(WifiP2pDevice paramWifiP2pDevice)
  {
    if ((paramWifiP2pDevice.wfdInfo != null) && (paramWifiP2pDevice.wfdInfo.isWfdEnabled())) {
      return isPrimarySinkDeviceType(paramWifiP2pDevice.wfdInfo.getDeviceType());
    }
    return false;
  }
  
  private void readvertiseDisplay(WifiDisplay paramWifiDisplay)
  {
    advertiseDisplay(paramWifiDisplay, this.mAdvertisedDisplaySurface, this.mAdvertisedDisplayWidth, this.mAdvertisedDisplayHeight, this.mAdvertisedDisplayFlags);
  }
  
  private void reportFeatureState()
  {
    final int i = computeFeatureState();
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        WifiDisplayController.-get13(WifiDisplayController.this).onFeatureStateChanged(i);
      }
    });
  }
  
  private void requestPeers()
  {
    this.mWifiP2pManager.requestPeers(this.mWifiP2pChannel, new WifiP2pManager.PeerListListener()
    {
      public void onPeersAvailable(WifiP2pDeviceList paramAnonymousWifiP2pDeviceList)
      {
        if (WifiDisplayController.-get0()) {
          Slog.d("WifiDisplayController", "Received list of peers.");
        }
        WifiDisplayController.-get1(WifiDisplayController.this).clear();
        paramAnonymousWifiP2pDeviceList = paramAnonymousWifiP2pDeviceList.getDeviceList().iterator();
        while (paramAnonymousWifiP2pDeviceList.hasNext())
        {
          WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)paramAnonymousWifiP2pDeviceList.next();
          if (WifiDisplayController.-get0()) {
            Slog.d("WifiDisplayController", "  " + WifiDisplayController.-wrap3(localWifiP2pDevice));
          }
          if (WifiDisplayController.-wrap2(localWifiP2pDevice)) {
            WifiDisplayController.-get1(WifiDisplayController.this).add(localWifiP2pDevice);
          }
        }
        if (WifiDisplayController.-get10(WifiDisplayController.this)) {
          WifiDisplayController.-wrap10(WifiDisplayController.this);
        }
      }
    });
  }
  
  private void retryConnection()
  {
    this.mDesiredDevice = new WifiP2pDevice(this.mDesiredDevice);
    updateConnection();
  }
  
  private void stopPeerDiscovery()
  {
    this.mWifiP2pManager.stopPeerDiscovery(this.mWifiP2pChannel, new WifiP2pManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        if (WifiDisplayController.-get0()) {
          Slog.d("WifiDisplayController", "Stop peer discovery failed with reason " + paramAnonymousInt + ".");
        }
      }
      
      public void onSuccess()
      {
        if (WifiDisplayController.-get0()) {
          Slog.d("WifiDisplayController", "Stop peer discovery succeeded.");
        }
      }
    });
  }
  
  private void tryDiscoverPeers()
  {
    this.mWifiP2pManager.discoverPeers(this.mWifiP2pChannel, new WifiP2pManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        if (WifiDisplayController.-get0()) {
          Slog.d("WifiDisplayController", "Discover peers failed with reason " + paramAnonymousInt + ".");
        }
      }
      
      public void onSuccess()
      {
        if (WifiDisplayController.-get0()) {
          Slog.d("WifiDisplayController", "Discover peers succeeded.  Requesting peers now.");
        }
        if (WifiDisplayController.-get10(WifiDisplayController.this)) {
          WifiDisplayController.-wrap13(WifiDisplayController.this);
        }
      }
    });
    this.mHandler.postDelayed(this.mDiscoverPeers, 10000L);
  }
  
  private void unadvertiseDisplay()
  {
    advertiseDisplay(null, null, 0, 0, 0);
  }
  
  private void updateConnection()
  {
    if (DEBUGV)
    {
      localObject1 = Thread.currentThread().getStackTrace();
      i = 2;
      while ((i < localObject1.length) && (i < 5))
      {
        Slog.i("WifiDisplayController", localObject1[i].toString());
        i += 1;
      }
      dump();
    }
    updateScanState();
    if (((this.mRemoteDisplay != null) || (this.mExtRemoteDisplay != null)) && (this.mConnectedDevice != this.mDesiredDevice))
    {
      Slog.i("WifiDisplayController", "Stopped listening for RTSP connection on " + this.mRemoteDisplayInterface);
      if (this.mRemoteDisplay == null) {
        break label191;
      }
      this.mRemoteDisplay.dispose();
    }
    for (;;)
    {
      this.mExtRemoteDisplay = null;
      this.mRemoteDisplay = null;
      this.mRemoteDisplayInterface = null;
      this.mHandler.removeCallbacks(this.mRtspTimeout);
      this.mWifiP2pManager.setMiracastMode(0);
      unadvertiseDisplay();
      do
      {
        if ((!this.mRemoteDisplayConnected) && (this.mDisconnectingDevice == null)) {
          break;
        }
        return;
      } while ((this.mRemoteDisplayInterface == null) || (this.mConnectedDevice != null));
      break;
      label191:
      if (this.mExtRemoteDisplay != null) {
        ExtendedRemoteDisplayHelper.dispose(this.mExtRemoteDisplay);
      }
    }
    if ((this.mConnectedDevice != null) && (this.mConnectedDevice != this.mDesiredDevice))
    {
      Slog.i("WifiDisplayController", "Disconnecting from Wifi display: " + this.mConnectedDevice.deviceName);
      this.mDisconnectingDevice = this.mConnectedDevice;
      this.mConnectedDevice = null;
      this.mConnectedDeviceGroupInfo = null;
      unadvertiseDisplay();
      localObject1 = this.mDisconnectingDevice;
      this.mWifiP2pManager.removeGroup(this.mWifiP2pChannel, new WifiP2pManager.ActionListener()
      {
        private void next()
        {
          if (WifiDisplayController.-get9(WifiDisplayController.this) == localObject1)
          {
            WifiDisplayController.-set6(WifiDisplayController.this, null);
            WifiDisplayController.-wrap16(WifiDisplayController.this);
          }
        }
        
        public void onFailure(int paramAnonymousInt)
        {
          Slog.i("WifiDisplayController", "Failed to disconnect from Wifi display: " + localObject1.deviceName + ", reason=" + paramAnonymousInt);
          next();
        }
        
        public void onSuccess()
        {
          Slog.i("WifiDisplayController", "Disconnected from Wifi display: " + localObject1.deviceName);
          next();
        }
      });
      return;
    }
    if (this.mCancelingDevice != null) {
      return;
    }
    if ((this.mConnectingDevice != null) && (this.mConnectingDevice != this.mDesiredDevice))
    {
      Slog.i("WifiDisplayController", "Canceling connection to Wifi display: " + this.mConnectingDevice.deviceName);
      this.mCancelingDevice = this.mConnectingDevice;
      this.mConnectingDevice = null;
      unadvertiseDisplay();
      this.mHandler.removeCallbacks(this.mConnectionTimeout);
      localObject1 = this.mCancelingDevice;
      this.mWifiP2pManager.cancelConnect(this.mWifiP2pChannel, new WifiP2pManager.ActionListener()
      {
        private void next()
        {
          if (WifiDisplayController.-get2(WifiDisplayController.this) == localObject1)
          {
            WifiDisplayController.-set0(WifiDisplayController.this, null);
            WifiDisplayController.-wrap16(WifiDisplayController.this);
          }
        }
        
        public void onFailure(int paramAnonymousInt)
        {
          Slog.i("WifiDisplayController", "Failed to cancel connection to Wifi display: " + localObject1.deviceName + ", reason=" + paramAnonymousInt);
          next();
        }
        
        public void onSuccess()
        {
          Slog.i("WifiDisplayController", "Canceled connection to Wifi display: " + localObject1.deviceName);
          next();
        }
      });
      return;
    }
    if (this.mDesiredDevice == null)
    {
      if (this.mWifiDisplayCertMode) {
        this.mListener.onDisplaySessionInfo(getSessionInfo(this.mConnectedDeviceGroupInfo, 0));
      }
      unadvertiseDisplay();
      return;
    }
    final Object localObject1 = new RemoteDisplay.Listener()
    {
      public void onDisplayConnected(Surface paramAnonymousSurface, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        if ((WifiDisplayController.-get3(WifiDisplayController.this) != this.val$oldDevice) || (WifiDisplayController.-get15(WifiDisplayController.this))) {
          return;
        }
        Slog.i("WifiDisplayController", "Opened RTSP connection with Wifi display: " + WifiDisplayController.-get3(WifiDisplayController.this).deviceName);
        WifiDisplayController.-set7(WifiDisplayController.this, true);
        WifiDisplayController.-get12(WifiDisplayController.this).removeCallbacks(WifiDisplayController.-get16(WifiDisplayController.this));
        if (WifiDisplayController.-get19(WifiDisplayController.this)) {
          WifiDisplayController.-get13(WifiDisplayController.this).onDisplaySessionInfo(WifiDisplayController.-wrap0(WifiDisplayController.this, WifiDisplayController.-get4(WifiDisplayController.this), paramAnonymousInt4));
        }
        WifiDisplay localWifiDisplay = WifiDisplayController.-wrap1(WifiDisplayController.-get3(WifiDisplayController.this));
        WifiDisplayController.-wrap5(WifiDisplayController.this, localWifiDisplay, paramAnonymousSurface, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
      }
      
      public void onDisplayDisconnected()
      {
        if (WifiDisplayController.-get3(WifiDisplayController.this) == this.val$oldDevice)
        {
          Slog.i("WifiDisplayController", "Closed RTSP connection with Wifi display: " + WifiDisplayController.-get3(WifiDisplayController.this).deviceName);
          WifiDisplayController.-get12(WifiDisplayController.this).removeCallbacks(WifiDisplayController.-get16(WifiDisplayController.this));
          WifiDisplayController.-set7(WifiDisplayController.this, false);
          WifiDisplayController.-wrap6(WifiDisplayController.this);
        }
      }
      
      public void onDisplayError(int paramAnonymousInt)
      {
        if (WifiDisplayController.-get3(WifiDisplayController.this) == this.val$oldDevice)
        {
          Slog.i("WifiDisplayController", "Lost RTSP connection with Wifi display due to error " + paramAnonymousInt + ": " + WifiDisplayController.-get3(WifiDisplayController.this).deviceName);
          WifiDisplayController.-get12(WifiDisplayController.this).removeCallbacks(WifiDisplayController.-get16(WifiDisplayController.this));
          WifiDisplayController.-wrap8(WifiDisplayController.this, false);
        }
      }
    };
    Object localObject2;
    if ((this.mConnectedDevice == null) && (this.mConnectingDevice == null))
    {
      Slog.i("WifiDisplayController", "Connecting to Wifi display: " + this.mDesiredDevice.deviceName);
      this.mConnectingDevice = this.mDesiredDevice;
      localObject2 = new WifiP2pConfig();
      Object localObject3 = new WpsInfo();
      if (this.mWifiDisplayWpsConfig != 4) {
        ((WpsInfo)localObject3).setup = this.mWifiDisplayWpsConfig;
      }
      for (;;)
      {
        ((WifiP2pConfig)localObject2).wps = ((WpsInfo)localObject3);
        ((WifiP2pConfig)localObject2).deviceAddress = this.mConnectingDevice.deviceAddress;
        ((WifiP2pConfig)localObject2).groupOwnerIntent = 14;
        advertiseDisplay(createWifiDisplay(this.mConnectingDevice), null, 0, 0, 0);
        if ((ExtendedRemoteDisplayHelper.isAvailable()) && (this.mExtRemoteDisplay == null))
        {
          i = getPortNumber(this.mDesiredDevice);
          localObject3 = "255.255.255.255:" + i;
          this.mRemoteDisplayInterface = ((String)localObject3);
          Slog.i("WifiDisplayController", "Listening for RTSP connection on " + (String)localObject3 + " from Wifi display: " + this.mDesiredDevice.deviceName);
          this.mExtRemoteDisplay = ExtendedRemoteDisplayHelper.listen((String)localObject3, (RemoteDisplay.Listener)localObject1, this.mHandler, this.mContext);
        }
        localObject1 = this.mDesiredDevice;
        this.mWifiP2pManager.connect(this.mWifiP2pChannel, (WifiP2pConfig)localObject2, new WifiP2pManager.ActionListener()
        {
          public void onFailure(int paramAnonymousInt)
          {
            if (WifiDisplayController.-get5(WifiDisplayController.this) == localObject1)
            {
              Slog.i("WifiDisplayController", "Failed to initiate connection to Wifi display: " + localObject1.deviceName + ", reason=" + paramAnonymousInt);
              WifiDisplayController.-set3(WifiDisplayController.this, null);
              WifiDisplayController.-wrap8(WifiDisplayController.this, false);
            }
          }
          
          public void onSuccess()
          {
            Slog.i("WifiDisplayController", "Initiated connection to Wifi display: " + localObject1.deviceName);
            WifiDisplayController.-get12(WifiDisplayController.this).postDelayed(WifiDisplayController.-get7(WifiDisplayController.this), 30000L);
          }
        });
        return;
        if (this.mConnectingDevice.wpsPbcSupported()) {
          ((WpsInfo)localObject3).setup = 0;
        } else if (this.mConnectingDevice.wpsDisplaySupported()) {
          ((WpsInfo)localObject3).setup = 2;
        } else {
          ((WpsInfo)localObject3).setup = 1;
        }
      }
    }
    if ((this.mConnectedDevice != null) && (this.mRemoteDisplay == null))
    {
      localObject2 = getInterfaceAddress(this.mConnectedDeviceGroupInfo);
      if (localObject2 == null)
      {
        Slog.i("WifiDisplayController", "Failed to get local interface address for communicating with Wifi display: " + this.mConnectedDevice.deviceName);
        handleConnectionFailure(false);
        return;
      }
      this.mWifiP2pManager.setMiracastMode(1);
      i = getPortNumber(this.mConnectedDevice);
      localObject2 = ((Inet4Address)localObject2).getHostAddress() + ":" + i;
      this.mRemoteDisplayInterface = ((String)localObject2);
      if (!ExtendedRemoteDisplayHelper.isAvailable())
      {
        Slog.i("WifiDisplayController", "Listening for RTSP connection on " + (String)localObject2 + " from Wifi display: " + this.mConnectedDevice.deviceName);
        this.mRemoteDisplay = RemoteDisplay.listen((String)localObject2, (RemoteDisplay.Listener)localObject1, this.mHandler, this.mContext.getOpPackageName());
      }
      if (!this.mWifiDisplayCertMode) {
        break label988;
      }
    }
    label988:
    for (int i = 120;; i = 30)
    {
      this.mHandler.postDelayed(this.mRtspTimeout, i * 1000);
      return;
    }
  }
  
  private void updateDesiredDevice(WifiP2pDevice paramWifiP2pDevice)
  {
    String str = paramWifiP2pDevice.deviceAddress;
    if ((this.mDesiredDevice != null) && (this.mDesiredDevice.deviceAddress.equals(str)))
    {
      if (DEBUG) {
        Slog.d("WifiDisplayController", "updateDesiredDevice: new information " + describeWifiP2pDevice(paramWifiP2pDevice));
      }
      this.mDesiredDevice.update(paramWifiP2pDevice);
      if ((this.mAdvertisedDisplay != null) && (this.mAdvertisedDisplay.getDeviceAddress().equals(str))) {
        readvertiseDisplay(createWifiDisplay(this.mDesiredDevice));
      }
    }
  }
  
  private void updateScanState()
  {
    if ((this.mScanRequested) && (this.mWfdEnabled) && (this.mDesiredDevice == null) && (this.mConnectedDevice == null) && (this.mDisconnectingDevice == null)) {
      if (!this.mDiscoverPeersInProgress)
      {
        Slog.i("WifiDisplayController", "Starting Wifi display scan.");
        this.mDiscoverPeersInProgress = true;
        handleScanStarted();
        tryDiscoverPeers();
      }
    }
    do
    {
      do
      {
        return;
      } while (!this.mDiscoverPeersInProgress);
      this.mHandler.removeCallbacks(this.mDiscoverPeers);
    } while ((this.mDesiredDevice != null) && (this.mDesiredDevice != this.mConnectedDevice));
    Slog.i("WifiDisplayController", "Stopping Wifi display scan.");
    this.mDiscoverPeersInProgress = false;
    stopPeerDiscovery();
    handleScanFinished();
  }
  
  private void updateSettings()
  {
    boolean bool2 = true;
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (Settings.Global.getInt(localContentResolver, "wifi_display_on", 0) != 0)
    {
      bool1 = true;
      this.mWifiDisplayOnSetting = bool1;
      if (Settings.Global.getInt(localContentResolver, "wifi_display_certification_on", 0) == 0) {
        break label80;
      }
    }
    label80:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mWifiDisplayCertMode = bool1;
      this.mWifiDisplayWpsConfig = 4;
      if (this.mWifiDisplayCertMode) {
        this.mWifiDisplayWpsConfig = Settings.Global.getInt(localContentResolver, "wifi_display_wps_config", 4);
      }
      updateWfdEnableState();
      return;
      bool1 = false;
      break;
    }
  }
  
  private void updateWfdEnableState()
  {
    WifiP2pWfdInfo localWifiP2pWfdInfo;
    if ((this.mWifiDisplayOnSetting) && (this.mWifiP2pEnabled))
    {
      if ((this.mWfdEnabled) || (this.mWfdEnabling)) {
        return;
      }
      this.mWfdEnabling = true;
      localWifiP2pWfdInfo = new WifiP2pWfdInfo();
      localWifiP2pWfdInfo.setWfdEnabled(true);
      localWifiP2pWfdInfo.setDeviceType(0);
      localWifiP2pWfdInfo.setSessionAvailable(true);
      localWifiP2pWfdInfo.setControlPort(7236);
      localWifiP2pWfdInfo.setMaxThroughput(50);
      this.mWifiP2pManager.setWFDInfo(this.mWifiP2pChannel, localWifiP2pWfdInfo, new WifiP2pManager.ActionListener()
      {
        public void onFailure(int paramAnonymousInt)
        {
          if (WifiDisplayController.-get0()) {
            Slog.d("WifiDisplayController", "Failed to set WFD info with reason " + paramAnonymousInt + ".");
          }
          WifiDisplayController.-set10(WifiDisplayController.this, false);
        }
        
        public void onSuccess()
        {
          if (WifiDisplayController.-get0()) {
            Slog.d("WifiDisplayController", "Successfully set WFD info.");
          }
          if (WifiDisplayController.-get18(WifiDisplayController.this))
          {
            WifiDisplayController.-set10(WifiDisplayController.this, false);
            WifiDisplayController.-set9(WifiDisplayController.this, true);
            WifiDisplayController.-wrap12(WifiDisplayController.this);
            WifiDisplayController.-wrap17(WifiDisplayController.this);
          }
        }
      });
      return;
    }
    if ((this.mWfdEnabled) || (this.mWfdEnabling))
    {
      localWifiP2pWfdInfo = new WifiP2pWfdInfo();
      localWifiP2pWfdInfo.setWfdEnabled(false);
      this.mWifiP2pManager.setWFDInfo(this.mWifiP2pChannel, localWifiP2pWfdInfo, new WifiP2pManager.ActionListener()
      {
        public void onFailure(int paramAnonymousInt)
        {
          if (WifiDisplayController.-get0()) {
            Slog.d("WifiDisplayController", "Failed to set WFD info with reason " + paramAnonymousInt + ".");
          }
        }
        
        public void onSuccess()
        {
          if (WifiDisplayController.-get0()) {
            Slog.d("WifiDisplayController", "Successfully set WFD info.");
          }
        }
      });
    }
    this.mWfdEnabling = false;
    this.mWfdEnabled = false;
    reportFeatureState();
    updateScanState();
    disconnect();
  }
  
  private void wifiConcurrencyUpdate()
  {
    WifiManager localWifiManager = (WifiManager)this.mContext.getSystemService("wifi");
    if (localWifiManager != null)
    {
      Slog.d("WifiDisplayController", "update for ap/p2p concurrency");
      localWifiManager.requestRunningP2p();
    }
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println("mWifiDisplayOnSetting=" + this.mWifiDisplayOnSetting);
    paramPrintWriter.println("mWifiP2pEnabled=" + this.mWifiP2pEnabled);
    paramPrintWriter.println("mWfdEnabled=" + this.mWfdEnabled);
    paramPrintWriter.println("mWfdEnabling=" + this.mWfdEnabling);
    paramPrintWriter.println("mNetworkInfo=" + this.mNetworkInfo);
    paramPrintWriter.println("mScanRequested=" + this.mScanRequested);
    paramPrintWriter.println("mDiscoverPeersInProgress=" + this.mDiscoverPeersInProgress);
    paramPrintWriter.println("mDesiredDevice=" + describeWifiP2pDevice(this.mDesiredDevice));
    paramPrintWriter.println("mConnectingDisplay=" + describeWifiP2pDevice(this.mConnectingDevice));
    paramPrintWriter.println("mDisconnectingDisplay=" + describeWifiP2pDevice(this.mDisconnectingDevice));
    paramPrintWriter.println("mCancelingDisplay=" + describeWifiP2pDevice(this.mCancelingDevice));
    paramPrintWriter.println("mConnectedDevice=" + describeWifiP2pDevice(this.mConnectedDevice));
    paramPrintWriter.println("mConnectionRetriesLeft=" + this.mConnectionRetriesLeft);
    paramPrintWriter.println("mRemoteDisplay=" + this.mRemoteDisplay);
    paramPrintWriter.println("mRemoteDisplayInterface=" + this.mRemoteDisplayInterface);
    paramPrintWriter.println("mRemoteDisplayConnected=" + this.mRemoteDisplayConnected);
    paramPrintWriter.println("mAdvertisedDisplay=" + this.mAdvertisedDisplay);
    paramPrintWriter.println("mAdvertisedDisplaySurface=" + this.mAdvertisedDisplaySurface);
    paramPrintWriter.println("mAdvertisedDisplayWidth=" + this.mAdvertisedDisplayWidth);
    paramPrintWriter.println("mAdvertisedDisplayHeight=" + this.mAdvertisedDisplayHeight);
    paramPrintWriter.println("mAdvertisedDisplayFlags=" + this.mAdvertisedDisplayFlags);
    paramPrintWriter.println("mAvailableWifiDisplayPeers: size=" + this.mAvailableWifiDisplayPeers.size());
    paramString = this.mAvailableWifiDisplayPeers.iterator();
    while (paramString.hasNext())
    {
      WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)paramString.next();
      paramPrintWriter.println("  " + describeWifiP2pDevice(localWifiP2pDevice));
    }
  }
  
  public void requestConnect(String paramString)
  {
    Iterator localIterator = this.mAvailableWifiDisplayPeers.iterator();
    while (localIterator.hasNext())
    {
      WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)localIterator.next();
      if (localWifiP2pDevice.deviceAddress.equals(paramString)) {
        connect(localWifiP2pDevice);
      }
    }
  }
  
  public void requestDisconnect()
  {
    disconnect();
  }
  
  public void requestPause()
  {
    if (this.mRemoteDisplay != null) {
      this.mRemoteDisplay.pause();
    }
  }
  
  public void requestResume()
  {
    if (this.mRemoteDisplay != null) {
      this.mRemoteDisplay.resume();
    }
  }
  
  public void requestStartScan()
  {
    wifiConcurrencyUpdate();
    if (!this.mScanRequested)
    {
      this.mScanRequested = true;
      updateScanState();
    }
  }
  
  public void requestStopScan()
  {
    if (this.mScanRequested)
    {
      this.mScanRequested = false;
      updateScanState();
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onDisplayChanged(WifiDisplay paramWifiDisplay);
    
    public abstract void onDisplayConnected(WifiDisplay paramWifiDisplay, Surface paramSurface, int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void onDisplayConnecting(WifiDisplay paramWifiDisplay);
    
    public abstract void onDisplayConnectionFailed();
    
    public abstract void onDisplayDisconnected();
    
    public abstract void onDisplaySessionInfo(WifiDisplaySessionInfo paramWifiDisplaySessionInfo);
    
    public abstract void onFeatureStateChanged(int paramInt);
    
    public abstract void onScanFinished();
    
    public abstract void onScanResults(WifiDisplay[] paramArrayOfWifiDisplay);
    
    public abstract void onScanStarted();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/WifiDisplayController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */