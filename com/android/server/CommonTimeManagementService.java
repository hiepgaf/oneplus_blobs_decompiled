package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.INetworkManagementEventObserver;
import android.net.InterfaceConfiguration;
import android.os.Binder;
import android.os.CommonTimeConfig;
import android.os.CommonTimeConfig.OnServerDiedListener;
import android.os.Handler;
import android.os.INetworkManagementService;
import android.os.INetworkManagementService.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import com.android.server.net.BaseNetworkObserver;
import java.io.FileDescriptor;
import java.io.PrintWriter;

class CommonTimeManagementService
  extends Binder
{
  private static final boolean ALLOW_WIFI;
  private static final String ALLOW_WIFI_PROP = "ro.common_time.allow_wifi";
  private static final boolean AUTO_DISABLE;
  private static final String AUTO_DISABLE_PROP = "ro.common_time.auto_disable";
  private static final byte BASE_SERVER_PRIO;
  private static final InterfaceScoreRule[] IFACE_SCORE_RULES;
  private static final int NATIVE_SERVICE_RECONNECT_TIMEOUT = 5000;
  private static final int NO_INTERFACE_TIMEOUT;
  private static final String NO_INTERFACE_TIMEOUT_PROP = "ro.common_time.no_iface_timeout";
  private static final String SERVER_PRIO_PROP = "ro.common_time.server_prio";
  private static final String TAG = CommonTimeManagementService.class.getSimpleName();
  private CommonTimeConfig mCTConfig;
  private CommonTimeConfig.OnServerDiedListener mCTServerDiedListener = new CommonTimeConfig.OnServerDiedListener()
  {
    public void onServerDied()
    {
      CommonTimeManagementService.-wrap3(CommonTimeManagementService.this);
    }
  };
  private BroadcastReceiver mConnectivityMangerObserver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      CommonTimeManagementService.-wrap2(CommonTimeManagementService.this);
    }
  };
  private final Context mContext;
  private String mCurIface;
  private boolean mDetectedAtStartup = false;
  private byte mEffectivePrio = BASE_SERVER_PRIO;
  private INetworkManagementEventObserver mIfaceObserver = new BaseNetworkObserver()
  {
    public void interfaceAdded(String paramAnonymousString)
    {
      CommonTimeManagementService.-wrap2(CommonTimeManagementService.this);
    }
    
    public void interfaceLinkStateChanged(String paramAnonymousString, boolean paramAnonymousBoolean)
    {
      CommonTimeManagementService.-wrap2(CommonTimeManagementService.this);
    }
    
    public void interfaceRemoved(String paramAnonymousString)
    {
      CommonTimeManagementService.-wrap2(CommonTimeManagementService.this);
    }
    
    public void interfaceStatusChanged(String paramAnonymousString, boolean paramAnonymousBoolean)
    {
      CommonTimeManagementService.-wrap2(CommonTimeManagementService.this);
    }
  };
  private Object mLock = new Object();
  private INetworkManagementService mNetMgr;
  private Handler mNoInterfaceHandler = new Handler();
  private Runnable mNoInterfaceRunnable = new Runnable()
  {
    public void run()
    {
      CommonTimeManagementService.-wrap1(CommonTimeManagementService.this);
    }
  };
  private Handler mReconnectHandler = new Handler();
  private Runnable mReconnectRunnable = new Runnable()
  {
    public void run()
    {
      CommonTimeManagementService.-wrap0(CommonTimeManagementService.this);
    }
  };
  
  static
  {
    boolean bool;
    label34:
    int i;
    if (SystemProperties.getInt("ro.common_time.auto_disable", 1) != 0)
    {
      bool = true;
      AUTO_DISABLE = bool;
      if (SystemProperties.getInt("ro.common_time.allow_wifi", 0) == 0) {
        break label109;
      }
      bool = true;
      ALLOW_WIFI = bool;
      i = SystemProperties.getInt("ro.common_time.server_prio", 1);
      NO_INTERFACE_TIMEOUT = SystemProperties.getInt("ro.common_time.no_iface_timeout", 60000);
      if (i >= 1) {
        break label114;
      }
      BASE_SERVER_PRIO = 1;
    }
    for (;;)
    {
      if (!ALLOW_WIFI) {
        break label136;
      }
      IFACE_SCORE_RULES = new InterfaceScoreRule[] { new InterfaceScoreRule("wlan", 1), new InterfaceScoreRule("eth", 2) };
      return;
      bool = false;
      break;
      label109:
      bool = false;
      break label34;
      label114:
      if (i > 30) {
        BASE_SERVER_PRIO = 30;
      } else {
        BASE_SERVER_PRIO = (byte)i;
      }
    }
    label136:
    IFACE_SCORE_RULES = new InterfaceScoreRule[] { new InterfaceScoreRule("eth", 2) };
  }
  
  public CommonTimeManagementService(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private void cleanupTimeConfig()
  {
    this.mReconnectHandler.removeCallbacks(this.mReconnectRunnable);
    this.mNoInterfaceHandler.removeCallbacks(this.mNoInterfaceRunnable);
    if (this.mCTConfig != null)
    {
      this.mCTConfig.release();
      this.mCTConfig = null;
    }
  }
  
  private void connectToTimeConfig()
  {
    cleanupTimeConfig();
    try
    {
      synchronized (this.mLock)
      {
        this.mCTConfig = new CommonTimeConfig();
        this.mCTConfig.setServerDiedListener(this.mCTServerDiedListener);
        this.mCurIface = this.mCTConfig.getInterfaceBinding();
        this.mCTConfig.setAutoDisable(AUTO_DISABLE);
        this.mCTConfig.setMasterElectionPriority(this.mEffectivePrio);
        if (NO_INTERFACE_TIMEOUT >= 0) {
          this.mNoInterfaceHandler.postDelayed(this.mNoInterfaceRunnable, NO_INTERFACE_TIMEOUT);
        }
        reevaluateServiceState();
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      scheduleTimeConfigReconnect();
    }
  }
  
  private void handleNoInterfaceTimeout()
  {
    if (this.mCTConfig != null)
    {
      Log.i(TAG, "Timeout waiting for interface to come up.  Forcing networkless master mode.");
      if (-7 == this.mCTConfig.forceNetworklessMasterMode()) {
        scheduleTimeConfigReconnect();
      }
    }
  }
  
  private void reevaluateServiceState()
  {
    Object localObject3 = null;
    Object localObject1 = null;
    int i = -1;
    int j = i;
    int m;
    int n;
    label78:
    int k;
    try
    {
      String[] arrayOfString = this.mNetMgr.listInterfaces();
      j = i;
      if (arrayOfString == null) {
        break label184;
      }
      j = i;
      int i2 = arrayOfString.length;
      m = 0;
      j = i;
      localObject3 = localObject1;
      if (m >= i2) {
        break label184;
      }
      str2 = arrayOfString[m];
      int i1 = -1;
      j = i;
      localObject3 = IFACE_SCORE_RULES;
      n = 0;
      j = i;
      int i3 = localObject3.length;
      k = i1;
      if (n >= i3) {
        break label453;
      }
      localInterfaceConfiguration = localObject3[n];
      j = i;
      if (!str2.contains(localInterfaceConfiguration.mPrefix)) {
        break label480;
      }
      j = i;
      k = localInterfaceConfiguration.mScore;
    }
    catch (RemoteException localRemoteException)
    {
      String str2;
      InterfaceConfiguration localInterfaceConfiguration;
      label123:
      boolean bool;
      localObject3 = null;
      i = 1;
      localObject2 = this.mLock;
      if (localObject3 == null) {
        break label303;
      }
    }
    j = i;
    localInterfaceConfiguration = this.mNetMgr.getInterfaceConfig(str2);
    j = i;
    localObject3 = localObject1;
    if (localInterfaceConfiguration != null)
    {
      j = i;
      bool = localInterfaceConfiguration.isActive();
      j = i;
      localObject3 = localObject1;
      if (bool)
      {
        localObject3 = str2;
        j = k;
      }
    }
    for (;;)
    {
      label184:
      Object localObject2;
      byte b;
      try
      {
        if (this.mCurIface == null)
        {
          Log.e(TAG, String.format("Binding common time service to %s.", new Object[] { localObject3 }));
          this.mCurIface = ((String)localObject3);
          if ((i != 0) && (this.mCTConfig != null))
          {
            if (j <= 0) {
              break label405;
            }
            b = (byte)(BASE_SERVER_PRIO * j);
            if (b != this.mEffectivePrio)
            {
              this.mEffectivePrio = b;
              this.mCTConfig.setMasterElectionPriority(this.mEffectivePrio);
            }
            if (this.mCTConfig.setNetworkBinding(this.mCurIface) == 0) {
              break label412;
            }
            scheduleTimeConfigReconnect();
          }
          return;
        }
        label303:
        if ((localObject3 == null) && (this.mCurIface != null))
        {
          Log.e(TAG, "Unbinding common time service.");
          this.mCurIface = null;
          continue;
        }
        if (str1 == null) {
          break label489;
        }
      }
      finally {}
      if ((this.mCurIface != null) && (!str1.equals(this.mCurIface)))
      {
        Log.e(TAG, String.format("Switching common time service binding from %s to %s.", new Object[] { this.mCurIface, str1 }));
        this.mCurIface = str1;
        continue;
        label405:
        b = BASE_SERVER_PRIO;
        continue;
        label412:
        if (NO_INTERFACE_TIMEOUT < 0) {
          continue;
        }
        this.mNoInterfaceHandler.removeCallbacks(this.mNoInterfaceRunnable);
        if (this.mCurIface != null) {
          continue;
        }
        this.mNoInterfaceHandler.postDelayed(this.mNoInterfaceRunnable, NO_INTERFACE_TIMEOUT);
        return;
        label453:
        if (k > i) {
          break label123;
        }
        Object localObject4 = localObject2;
        j = i;
        m += 1;
        i = j;
        localObject2 = localObject4;
        break;
        label480:
        n += 1;
        break label78;
      }
      label489:
      i = 0;
    }
  }
  
  private void scheduleTimeConfigReconnect()
  {
    cleanupTimeConfig();
    Log.w(TAG, String.format("Native service died, will reconnect in %d mSec", new Object[] { Integer.valueOf(5000) }));
    this.mReconnectHandler.postDelayed(this.mReconnectRunnable, 5000L);
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] arg3)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println(String.format("Permission Denial: can't dump CommonTimeManagement service from from pid=%d, uid=%d", new Object[] { Integer.valueOf(Binder.getCallingPid()), Integer.valueOf(Binder.getCallingUid()) }));
      return;
    }
    if (!this.mDetectedAtStartup)
    {
      paramPrintWriter.println("Native Common Time service was not detected at startup.  Service is unavailable");
      return;
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        paramPrintWriter.println("Current Common Time Management Service Config:");
        if (this.mCTConfig == null)
        {
          paramFileDescriptor = "reconnecting";
          paramPrintWriter.println(String.format("  Native service     : %s", new Object[] { paramFileDescriptor }));
          if (this.mCurIface == null)
          {
            paramFileDescriptor = "unbound";
            paramPrintWriter.println(String.format("  Bound interface    : %s", new Object[] { paramFileDescriptor }));
            if (!ALLOW_WIFI) {
              break label266;
            }
            paramFileDescriptor = "yes";
            paramPrintWriter.println(String.format("  Allow WiFi         : %s", new Object[] { paramFileDescriptor }));
            if (!AUTO_DISABLE) {
              continue;
            }
            paramFileDescriptor = "yes";
            paramPrintWriter.println(String.format("  Allow Auto Disable : %s", new Object[] { paramFileDescriptor }));
            paramPrintWriter.println(String.format("  Server Priority    : %d", new Object[] { Byte.valueOf(this.mEffectivePrio) }));
            paramPrintWriter.println(String.format("  No iface timeout   : %d", new Object[] { Integer.valueOf(NO_INTERFACE_TIMEOUT) }));
          }
        }
        else
        {
          paramFileDescriptor = "alive";
          continue;
        }
        paramFileDescriptor = this.mCurIface;
        continue;
        paramFileDescriptor = "no";
      }
      label266:
      paramFileDescriptor = "no";
    }
  }
  
  void systemRunning()
  {
    if (ServiceManager.checkService("common_time.config") == null)
    {
      Log.i(TAG, "No common time service detected on this platform.  Common time services will be unavailable.");
      return;
    }
    this.mDetectedAtStartup = true;
    this.mNetMgr = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
    try
    {
      this.mNetMgr.registerObserver(this.mIfaceObserver);
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
      this.mContext.registerReceiver(this.mConnectivityMangerObserver, localIntentFilter);
      connectToTimeConfig();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  private static class InterfaceScoreRule
  {
    public final String mPrefix;
    public final byte mScore;
    
    public InterfaceScoreRule(String paramString, byte paramByte)
    {
      this.mPrefix = paramString;
      this.mScore = paramByte;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/CommonTimeManagementService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */