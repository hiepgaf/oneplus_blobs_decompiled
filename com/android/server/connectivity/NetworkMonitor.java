package com.android.server.connectivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.CaptivePortal;
import android.net.ICaptivePortal.Stub;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.ProxyInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.metrics.IpConnectivityLog;
import android.net.metrics.NetworkEvent;
import android.net.metrics.ValidationProbeEvent;
import android.net.util.Stopwatch;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.LocalLog.ReadOnlyLocalLog;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NetworkMonitor
  extends StateMachine
{
  public static final String ACTION_NETWORK_CONDITIONS_MEASURED = "android.net.conn.NETWORK_CONDITIONS_MEASURED";
  private static final int BASE = 532480;
  private static final int BLAME_FOR_EVALUATION_ATTEMPTS = 5;
  private static final int CAPTIVE_PORTAL_REEVALUATE_DELAY_MS = 600000;
  private static final int CMD_CAPTIVE_PORTAL_APP_FINISHED = 532489;
  private static final int CMD_CAPTIVE_PORTAL_RECHECK = 532492;
  public static final int CMD_FORCE_REEVALUATION = 532488;
  private static final int CMD_LAUNCH_CAPTIVE_PORTAL_APP = 532491;
  public static final int CMD_NETWORK_CONNECTED = 532481;
  public static final int CMD_NETWORK_DISCONNECTED = 532487;
  private static final int CMD_REEVALUATE = 532486;
  private static final boolean DBG = false;
  private static final String DEFAULT_CN_HTTP_URL = "http://g.cn/generate_204";
  private static final String DEFAULT_FALLBACK_URL = "http://www.google.com/gen_204";
  private static final String DEFAULT_HTTPS_URL = "https://www.google.com/generate_204";
  private static final String DEFAULT_HTTP_URL = "http://connectivitycheck.gstatic.com/generate_204";
  private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36";
  public static final int EVENT_NETWORK_TESTED = 532482;
  public static final int EVENT_PROVISIONING_NOTIFICATION = 532490;
  public static final String EXTRA_BSSID = "extra_bssid";
  public static final String EXTRA_CELL_ID = "extra_cellid";
  public static final String EXTRA_CONNECTIVITY_TYPE = "extra_connectivity_type";
  public static final String EXTRA_IS_CAPTIVE_PORTAL = "extra_is_captive_portal";
  public static final String EXTRA_NETWORK_TYPE = "extra_network_type";
  public static final String EXTRA_REQUEST_TIMESTAMP_MS = "extra_request_timestamp_ms";
  public static final String EXTRA_RESPONSE_RECEIVED = "extra_response_received";
  public static final String EXTRA_RESPONSE_TIMESTAMP_MS = "extra_response_timestamp_ms";
  public static final String EXTRA_SSID = "extra_ssid";
  private static final int IGNORE_REEVALUATE_ATTEMPTS = 5;
  private static final int INITIAL_REEVALUATE_DELAY_MS = 1000;
  private static final int INVALID_UID = -1;
  private static final int MAX_REEVALUATE_DELAY_MS = 600000;
  public static final int NETWORK_TEST_RESULT_INVALID = 1;
  public static final int NETWORK_TEST_RESULT_VALID = 0;
  private static final String PERMISSION_ACCESS_NETWORK_CONDITIONS = "android.permission.ACCESS_NETWORK_CONDITIONS";
  private static final int PROBE_TIMEOUT_MS = 3000;
  private static final String PROP_SIM_COUNTRY = "gsm.sim.operator.iso-country";
  private static final int SOCKET_TIMEOUT_MS = 10000;
  private static final String TAG = NetworkMonitor.class.getSimpleName();
  private static String mSucceedUrl = null;
  private final AlarmManager mAlarmManager;
  private final State mCaptivePortalState = new CaptivePortalState(null);
  private final Handler mConnectivityServiceHandler;
  private final Context mContext;
  private final NetworkRequest mDefaultRequest;
  private final State mDefaultState = new DefaultState(null);
  private boolean mDontDisplaySigninNotification = false;
  private final State mEvaluatingState = new EvaluatingState(null);
  private final Stopwatch mEvaluationTimer = new Stopwatch();
  private boolean mIsCaptivePortalCheckEnabled;
  private boolean mIsPortalNotificationEnabled = false;
  private CaptivePortalProbeResult mLastPortalProbeResult = CaptivePortalProbeResult.FAILED;
  private CustomIntentReceiver mLaunchCaptivePortalAppBroadcastReceiver = null;
  private final State mMaybeNotifyState = new MaybeNotifyState(null);
  private final IpConnectivityLog mMetricsLog;
  private final int mNetId;
  private final NetworkAgentInfo mNetworkAgentInfo;
  private int mReevaluateToken = 0;
  private int mRetryCount = 0;
  private final TelephonyManager mTelephonyManager;
  private int mUidResponsibleForReeval = -1;
  private boolean mUseHttps;
  private boolean mUserDoesNotWant = false;
  private final State mValidatedState = new ValidatedState(null);
  private final WifiManager mWifiManager;
  public boolean systemReady = false;
  private final LocalLog validationLogs = new LocalLog(20);
  
  public NetworkMonitor(Context paramContext, Handler paramHandler, NetworkAgentInfo paramNetworkAgentInfo, NetworkRequest paramNetworkRequest)
  {
    this(paramContext, paramHandler, paramNetworkAgentInfo, paramNetworkRequest, new IpConnectivityLog());
  }
  
  protected NetworkMonitor(Context paramContext, Handler paramHandler, NetworkAgentInfo paramNetworkAgentInfo, NetworkRequest paramNetworkRequest, IpConnectivityLog paramIpConnectivityLog)
  {
    super(TAG + paramNetworkAgentInfo.name());
    this.mContext = paramContext;
    this.mMetricsLog = paramIpConnectivityLog;
    this.mConnectivityServiceHandler = paramHandler;
    this.mNetworkAgentInfo = paramNetworkAgentInfo;
    this.mNetId = this.mNetworkAgentInfo.network.netId;
    this.mTelephonyManager = ((TelephonyManager)paramContext.getSystemService("phone"));
    this.mWifiManager = ((WifiManager)paramContext.getSystemService("wifi"));
    this.mAlarmManager = ((AlarmManager)paramContext.getSystemService("alarm"));
    this.mDefaultRequest = paramNetworkRequest;
    addState(this.mDefaultState);
    addState(this.mValidatedState, this.mDefaultState);
    addState(this.mMaybeNotifyState, this.mDefaultState);
    addState(this.mEvaluatingState, this.mMaybeNotifyState);
    addState(this.mCaptivePortalState, this.mMaybeNotifyState);
    setInitialState(this.mDefaultState);
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "captive_portal_detection_enabled", 1) == 1)
    {
      bool1 = true;
      this.mIsCaptivePortalCheckEnabled = bool1;
      if (Settings.Global.getInt(this.mContext.getContentResolver(), "captive_portal_use_https", 1) != 1) {
        break label406;
      }
      bool1 = true;
      label361:
      this.mUseHttps = bool1;
      if (Settings.Global.getInt(this.mContext.getContentResolver(), "portal_notification_enable", 0) != 1) {
        break label412;
      }
    }
    label406:
    label412:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mIsPortalNotificationEnabled = bool1;
      start();
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label361;
    }
  }
  
  private static String getCaptivePortalFallbackUrl(Context paramContext)
  {
    return getSetting(paramContext, "captive_portal_fallback_url", "http://www.google.com/gen_204");
  }
  
  private static String getCaptivePortalServerCnHttpUrl(Context paramContext)
  {
    return getSetting(paramContext, "captive_portal_cn_http_url", "http://g.cn/generate_204");
  }
  
  private static String getCaptivePortalServerGlobalHttpUrl(Context paramContext)
  {
    return getSetting(paramContext, "captive_portal_http_url", "http://connectivitycheck.gstatic.com/generate_204");
  }
  
  public static String getCaptivePortalServerHttpUrl(Context paramContext)
  {
    if (mSucceedUrl != null) {
      return mSucceedUrl;
    }
    return getSetting(paramContext, "captive_portal_http_url", "http://connectivitycheck.gstatic.com/generate_204");
  }
  
  private static String getCaptivePortalServerHttpsUrl(Context paramContext)
  {
    return getSetting(paramContext, "captive_portal_https_url", "https://www.google.com/generate_204");
  }
  
  private static String getCaptivePortalUserAgent(Context paramContext)
  {
    return getSetting(paramContext, "captive_portal_user_agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36");
  }
  
  private static String getSetting(Context paramContext, String paramString1, String paramString2)
  {
    paramContext = Settings.Global.getString(paramContext.getContentResolver(), paramString1);
    if (paramContext != null) {
      return paramContext;
    }
    return paramString2;
  }
  
  private boolean isCountryChina()
  {
    String str = SystemProperties.get("gsm.sim.operator.iso-country", "");
    if (str.length() >= 2) {
      return str.equalsIgnoreCase("CN");
    }
    return false;
  }
  
  private void logNetworkEvent(int paramInt)
  {
    this.mMetricsLog.log(new NetworkEvent(this.mNetId, paramInt));
  }
  
  private void logValidationProbe(long paramLong, int paramInt1, int paramInt2)
  {
    this.mMetricsLog.log(new ValidationProbeEvent(this.mNetId, paramLong, paramInt1, paramInt2));
  }
  
  private URL makeURL(String paramString)
  {
    if (paramString != null) {
      try
      {
        URL localURL = new URL(paramString);
        return localURL;
      }
      catch (MalformedURLException localMalformedURLException)
      {
        validationLog("Bad URL: " + paramString);
      }
    }
    return null;
  }
  
  private void maybeLogEvaluationResult(int paramInt)
  {
    if (this.mEvaluationTimer.isRunning())
    {
      this.mMetricsLog.log(new NetworkEvent(this.mNetId, paramInt, this.mEvaluationTimer.stop()));
      this.mEvaluationTimer.reset();
    }
  }
  
  private void sendNetworkConditionsBroadcast(boolean paramBoolean1, boolean paramBoolean2, long paramLong1, long paramLong2)
  {
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "wifi_scan_always_enabled", 0) == 0) {
      return;
    }
    if (!this.systemReady) {
      return;
    }
    Intent localIntent = new Intent("android.net.conn.NETWORK_CONDITIONS_MEASURED");
    Object localObject;
    switch (this.mNetworkAgentInfo.networkInfo.getType())
    {
    default: 
      return;
    case 1: 
      localObject = this.mWifiManager.getConnectionInfo();
      if (localObject != null)
      {
        localIntent.putExtra("extra_ssid", ((WifiInfo)localObject).getSSID());
        localIntent.putExtra("extra_bssid", ((WifiInfo)localObject).getBSSID());
      }
      break;
    }
    for (;;)
    {
      localIntent.putExtra("extra_connectivity_type", this.mNetworkAgentInfo.networkInfo.getType());
      localIntent.putExtra("extra_response_received", paramBoolean1);
      localIntent.putExtra("extra_request_timestamp_ms", paramLong1);
      if (paramBoolean1)
      {
        localIntent.putExtra("extra_is_captive_portal", paramBoolean2);
        localIntent.putExtra("extra_response_timestamp_ms", paramLong2);
      }
      this.mContext.sendBroadcastAsUser(localIntent, UserHandle.CURRENT, "android.permission.ACCESS_NETWORK_CONDITIONS");
      return;
      return;
      localIntent.putExtra("extra_network_type", this.mTelephonyManager.getNetworkType());
      localObject = this.mTelephonyManager.getAllCellInfo();
      if (localObject == null) {
        return;
      }
      int i = 0;
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        CellInfo localCellInfo = (CellInfo)((Iterator)localObject).next();
        if (localCellInfo.isRegistered())
        {
          i += 1;
          if (i > 1)
          {
            log("more than one registered CellInfo.  Can't tell which is active.  Bailing.");
            return;
          }
          if ((localCellInfo instanceof CellInfoCdma))
          {
            localIntent.putExtra("extra_cellid", ((CellInfoCdma)localCellInfo).getCellIdentity());
          }
          else if ((localCellInfo instanceof CellInfoGsm))
          {
            localIntent.putExtra("extra_cellid", ((CellInfoGsm)localCellInfo).getCellIdentity());
          }
          else if ((localCellInfo instanceof CellInfoLte))
          {
            localIntent.putExtra("extra_cellid", ((CellInfoLte)localCellInfo).getCellIdentity());
          }
          else
          {
            if (!(localCellInfo instanceof CellInfoWcdma)) {
              return;
            }
            localIntent.putExtra("extra_cellid", ((CellInfoWcdma)localCellInfo).getCellIdentity());
          }
        }
      }
    }
  }
  
  private CaptivePortalProbeResult sendParallelHttpProbes(final URL paramURL1, final URL paramURL2, final URL paramURL3, URL paramURL4)
  {
    if (paramURL1 == null) {}
    final Object localObject2;
    Thread local1ProbeThread;
    Object localObject3;
    for (int i = 2;; i = 3)
    {
      localObject2 = new CountDownLatch(i);
      local1ProbeThread = new Thread()
      {
        public static final int SEND_HTTP = 1;
        public static final int SEND_HTTPS = 0;
        public static final int SEND_HTTP_CN = 2;
        private final boolean mIsChinaProbe;
        private final boolean mIsHttps;
        private volatile NetworkMonitor.CaptivePortalProbeResult mResult = NetworkMonitor.CaptivePortalProbeResult.FAILED;
        
        public NetworkMonitor.CaptivePortalProbeResult result()
        {
          return this.mResult;
        }
        
        public void run()
        {
          if (this.mIsHttps) {
            this.mResult = NetworkMonitor.this.sendHttpProbe(paramURL1, 2);
          }
          while (((this.mIsHttps) && (this.mResult.isSuccessful())) || ((!this.mIsHttps) && (this.mResult.isPortal())) || ((this.mIsChinaProbe) && ((this.mResult.isPortal()) || (this.mResult.isSuccessful()))))
          {
            if ((!NetworkMonitor.-wrap0(NetworkMonitor.this)) || (this.mIsChinaProbe)) {}
            for (;;)
            {
              if (localObject2.getCount() > 0L)
              {
                localObject2.countDown();
                continue;
                if (this.mIsChinaProbe)
                {
                  this.mResult = NetworkMonitor.this.sendHttpProbe(paramURL3, 1);
                  break;
                }
                this.mResult = NetworkMonitor.this.sendHttpProbe(paramURL2, 1);
                break;
                NetworkMonitor.-wrap6(NetworkMonitor.this, Process.myTid() + " china SIM, waiting for probe result");
              }
            }
          }
          localObject2.countDown();
        }
      };
      localObject3 = new Thread()
      {
        public static final int SEND_HTTP = 1;
        public static final int SEND_HTTPS = 0;
        public static final int SEND_HTTP_CN = 2;
        private final boolean mIsChinaProbe;
        private final boolean mIsHttps;
        private volatile NetworkMonitor.CaptivePortalProbeResult mResult = NetworkMonitor.CaptivePortalProbeResult.FAILED;
        
        public NetworkMonitor.CaptivePortalProbeResult result()
        {
          return this.mResult;
        }
        
        public void run()
        {
          if (this.mIsHttps) {
            this.mResult = NetworkMonitor.this.sendHttpProbe(paramURL1, 2);
          }
          while (((this.mIsHttps) && (this.mResult.isSuccessful())) || ((!this.mIsHttps) && (this.mResult.isPortal())) || ((this.mIsChinaProbe) && ((this.mResult.isPortal()) || (this.mResult.isSuccessful()))))
          {
            if ((!NetworkMonitor.-wrap0(NetworkMonitor.this)) || (this.mIsChinaProbe)) {}
            for (;;)
            {
              if (localObject2.getCount() > 0L)
              {
                localObject2.countDown();
                continue;
                if (this.mIsChinaProbe)
                {
                  this.mResult = NetworkMonitor.this.sendHttpProbe(paramURL3, 1);
                  break;
                }
                this.mResult = NetworkMonitor.this.sendHttpProbe(paramURL2, 1);
                break;
                NetworkMonitor.-wrap6(NetworkMonitor.this, Process.myTid() + " china SIM, waiting for probe result");
              }
            }
          }
          localObject2.countDown();
        }
      };
      Object localObject1 = new Thread()
      {
        public static final int SEND_HTTP = 1;
        public static final int SEND_HTTPS = 0;
        public static final int SEND_HTTP_CN = 2;
        private final boolean mIsChinaProbe;
        private final boolean mIsHttps;
        private volatile NetworkMonitor.CaptivePortalProbeResult mResult = NetworkMonitor.CaptivePortalProbeResult.FAILED;
        
        public NetworkMonitor.CaptivePortalProbeResult result()
        {
          return this.mResult;
        }
        
        public void run()
        {
          if (this.mIsHttps) {
            this.mResult = NetworkMonitor.this.sendHttpProbe(paramURL1, 2);
          }
          while (((this.mIsHttps) && (this.mResult.isSuccessful())) || ((!this.mIsHttps) && (this.mResult.isPortal())) || ((this.mIsChinaProbe) && ((this.mResult.isPortal()) || (this.mResult.isSuccessful()))))
          {
            if ((!NetworkMonitor.-wrap0(NetworkMonitor.this)) || (this.mIsChinaProbe)) {}
            for (;;)
            {
              if (localObject2.getCount() > 0L)
              {
                localObject2.countDown();
                continue;
                if (this.mIsChinaProbe)
                {
                  this.mResult = NetworkMonitor.this.sendHttpProbe(paramURL3, 1);
                  break;
                }
                this.mResult = NetworkMonitor.this.sendHttpProbe(paramURL2, 1);
                break;
                NetworkMonitor.-wrap6(NetworkMonitor.this, Process.myTid() + " china SIM, waiting for probe result");
              }
            }
          }
          localObject2.countDown();
        }
      };
      if (paramURL1 != null) {}
      try
      {
        local1ProbeThread.start();
        ((1ProbeThread)localObject3).start();
        ((1ProbeThread)localObject1).start();
        ((CountDownLatch)localObject2).await(3000L, TimeUnit.MILLISECONDS);
        localObject2 = local1ProbeThread.result();
        localObject3 = ((1ProbeThread)localObject3).result();
        localObject1 = ((1ProbeThread)localObject1).result();
        if ((!((CaptivePortalProbeResult)localObject1).isPortal()) && (!((CaptivePortalProbeResult)localObject1).isSuccessful())) {
          break;
        }
        validationLog("use g.cn result");
        mSucceedUrl = paramURL3.getHost();
        return (CaptivePortalProbeResult)localObject1;
      }
      catch (InterruptedException paramURL1)
      {
        validationLog("Error: probes wait interrupted!");
        return CaptivePortalProbeResult.FAILED;
      }
    }
    if (((CaptivePortalProbeResult)localObject3).isPortal())
    {
      validationLog("use connectivitycheck.gstatic.com result");
      mSucceedUrl = paramURL2.getHost();
      return (CaptivePortalProbeResult)localObject3;
    }
    if ((((CaptivePortalProbeResult)localObject2).isPortal()) || (((CaptivePortalProbeResult)localObject2).isSuccessful()))
    {
      validationLog("use https result");
      mSucceedUrl = paramURL1.getHost();
      return (CaptivePortalProbeResult)localObject2;
    }
    if (paramURL4 != null)
    {
      paramURL1 = sendHttpProbe(paramURL4, 4);
      if (paramURL1.isPortal())
      {
        mSucceedUrl = paramURL4.getHost();
        return paramURL1;
      }
    }
    try
    {
      local1ProbeThread.join();
      return local1ProbeThread.result();
    }
    catch (InterruptedException paramURL1)
    {
      validationLog("Error: https probe wait interrupted!");
    }
    return CaptivePortalProbeResult.FAILED;
  }
  
  private void validationLog(String paramString)
  {
    this.validationLogs.log(paramString);
  }
  
  public LocalLog.ReadOnlyLocalLog getValidationLogs()
  {
    return this.validationLogs.readOnlyLocalLog();
  }
  
  protected CaptivePortalProbeResult isCaptivePortal()
  {
    if (!this.mIsCaptivePortalCheckEnabled)
    {
      if (!this.mIsPortalNotificationEnabled) {
        return new CaptivePortalProbeResult(204);
      }
      this.mConnectivityServiceHandler.sendMessage(obtainMessage(532482, 0, this.mNetId, null));
    }
    localObject1 = null;
    localURL3 = null;
    localObject3 = null;
    localURL1 = null;
    localURL2 = null;
    localObject4 = this.mNetworkAgentInfo.linkProperties.getHttpProxy();
    Object localObject2 = localObject1;
    if (localObject4 != null)
    {
      if (Uri.EMPTY.equals(((ProxyInfo)localObject4).getPacFileUrl())) {
        localObject2 = localObject1;
      }
    }
    else
    {
      if (localObject2 != null) {
        break label336;
      }
      localURL3 = makeURL(getCaptivePortalServerHttpsUrl(this.mContext));
      localObject3 = makeURL(getCaptivePortalServerGlobalHttpUrl(this.mContext));
      localURL2 = makeURL(getCaptivePortalServerCnHttpUrl(this.mContext));
      localURL1 = makeURL(getCaptivePortalFallbackUrl(this.mContext));
      localObject1 = localObject3;
      if (this.systemReady)
      {
        localObject1 = localObject3;
        if (localObject3 != null)
        {
          localObject1 = localObject3;
          if (((URL)localObject3).getHost().contains("qualcomm"))
          {
            localObject1 = this.mContext.getResources().getString(84541440);
            if (localObject1 != null) {
              break label295;
            }
            localObject1 = "http://connectivitycheck.gstatic.com/generate_204";
            label218:
            localObject1 = makeURL((String)localObject1);
            validationLog("replace qualcomm server to " + ((URL)localObject1).getHost());
          }
        }
      }
      if ((localObject1 != null) && (localURL3 != null)) {
        break label327;
      }
    }
    label295:
    label327:
    while (localURL2 == null)
    {
      return CaptivePortalProbeResult.FAILED;
      localObject1 = makeURL(((ProxyInfo)localObject4).getPacFileUrl().toString());
      localObject2 = localObject1;
      if (localObject1 != null) {
        break;
      }
      return CaptivePortalProbeResult.FAILED;
      localObject1 = "http://" + (String)localObject1 + "/generate_204";
      break label218;
    }
    localObject3 = localObject1;
    label336:
    long l2 = SystemClock.elapsedRealtime();
    if (localObject2 != null) {
      localObject1 = ((URL)localObject2).getHost();
    }
    for (;;)
    {
      if (TextUtils.isEmpty((CharSequence)localObject1)) {
        break label588;
      }
      localObject4 = ValidationProbeEvent.getProbeName(0);
      Stopwatch localStopwatch = new Stopwatch().start();
      try
      {
        InetAddress[] arrayOfInetAddress = this.mNetworkAgentInfo.network.getAllByName((String)localObject1);
        int j = 1;
        l1 = localStopwatch.stop();
        StringBuffer localStringBuffer = new StringBuffer(", " + (String)localObject1 + "=");
        i = 0;
        int k = arrayOfInetAddress.length;
        for (;;)
        {
          if (i < k)
          {
            InetAddress localInetAddress = arrayOfInetAddress[i];
            localStringBuffer.append(localInetAddress.getHostAddress());
            if (localInetAddress != arrayOfInetAddress[(arrayOfInetAddress.length - 1)]) {
              localStringBuffer.append(",");
            }
            i += 1;
            continue;
            if (localObject4 != null)
            {
              localObject1 = ((ProxyInfo)localObject4).getHost();
              break;
            }
            if (isCountryChina())
            {
              localObject1 = localURL2.getHost();
              break;
            }
            localObject1 = ((URL)localObject3).getHost();
            break;
          }
        }
        validationLog((String)localObject4 + " OK " + l1 + "ms" + localStringBuffer);
        i = j;
      }
      catch (UnknownHostException localUnknownHostException)
      {
        for (;;)
        {
          label588:
          int i = 0;
          long l1 = localStopwatch.stop();
          validationLog((String)localObject4 + " FAIL " + l1 + "ms, " + (String)localObject1);
          continue;
          if (this.mUseHttps) {
            localObject1 = sendParallelHttpProbes(localURL3, (URL)localObject3, localURL2, localURL1);
          } else {
            localObject1 = sendParallelHttpProbes(null, (URL)localObject3, localURL2, localURL1);
          }
        }
      }
    }
    logValidationProbe(l1, 0, i);
    if (localObject2 != null)
    {
      localObject1 = sendHttpProbe((URL)localObject2, 3);
      l1 = SystemClock.elapsedRealtime();
      sendNetworkConditionsBroadcast(true, ((CaptivePortalProbeResult)localObject1).isPortal(), l2, l1);
      return (CaptivePortalProbeResult)localObject1;
    }
  }
  
  protected void log(String paramString) {}
  
  protected CaptivePortalProbeResult sendHttpProbe(URL paramURL, int paramInt)
  {
    localObject2 = null;
    localObject1 = null;
    j = 599;
    String str2 = null;
    Stopwatch localStopwatch = new Stopwatch().start();
    i = j;
    str1 = str2;
    for (;;)
    {
      try
      {
        HttpURLConnection localHttpURLConnection = (HttpURLConnection)this.mNetworkAgentInfo.network.openConnection(paramURL);
        if (paramInt != 3) {
          continue;
        }
        bool = true;
        i = j;
        str1 = str2;
        localObject1 = localHttpURLConnection;
        localObject2 = localHttpURLConnection;
        localHttpURLConnection.setInstanceFollowRedirects(bool);
        i = j;
        str1 = str2;
        localObject1 = localHttpURLConnection;
        localObject2 = localHttpURLConnection;
        localHttpURLConnection.setConnectTimeout(10000);
        i = j;
        str1 = str2;
        localObject1 = localHttpURLConnection;
        localObject2 = localHttpURLConnection;
        localHttpURLConnection.setReadTimeout(10000);
        i = j;
        str1 = str2;
        localObject1 = localHttpURLConnection;
        localObject2 = localHttpURLConnection;
        localHttpURLConnection.setUseCaches(false);
        i = j;
        str1 = str2;
        localObject1 = localHttpURLConnection;
        localObject2 = localHttpURLConnection;
        String str3 = getCaptivePortalUserAgent(this.mContext);
        if (str3 != null)
        {
          i = j;
          str1 = str2;
          localObject1 = localHttpURLConnection;
          localObject2 = localHttpURLConnection;
          localHttpURLConnection.setRequestProperty("User-Agent", str3);
        }
        i = j;
        str1 = str2;
        localObject1 = localHttpURLConnection;
        localObject2 = localHttpURLConnection;
        long l1 = SystemClock.elapsedRealtime();
        i = j;
        str1 = str2;
        localObject1 = localHttpURLConnection;
        localObject2 = localHttpURLConnection;
        k = localHttpURLConnection.getResponseCode();
        i = k;
        str1 = str2;
        localObject1 = localHttpURLConnection;
        localObject2 = localHttpURLConnection;
        str2 = localHttpURLConnection.getHeaderField("location");
        i = k;
        str1 = str2;
        localObject1 = localHttpURLConnection;
        localObject2 = localHttpURLConnection;
        long l2 = SystemClock.elapsedRealtime();
        i = k;
        str1 = str2;
        localObject1 = localHttpURLConnection;
        localObject2 = localHttpURLConnection;
        validationLog(ValidationProbeEvent.getProbeName(paramInt) + " " + paramURL + " time=" + (l2 - l1) + "ms" + " ret=" + k + " headers=" + localHttpURLConnection.getHeaderFields());
        j = k;
        if (k == 200)
        {
          j = k;
          i = k;
          str1 = str2;
          localObject1 = localHttpURLConnection;
          localObject2 = localHttpURLConnection;
          if (localHttpURLConnection.getContentLength() == 0)
          {
            i = k;
            str1 = str2;
            localObject1 = localHttpURLConnection;
            localObject2 = localHttpURLConnection;
            validationLog("Empty 200 response interpreted as 204 response.");
            j = 204;
          }
        }
        i = j;
        if (j == 200)
        {
          i = j;
          if (paramInt == 3)
          {
            i = j;
            str1 = str2;
            localObject1 = localHttpURLConnection;
            localObject2 = localHttpURLConnection;
            validationLog("PAC fetch 200 response interpreted as 204 response.");
            i = 204;
          }
        }
        k = i;
        localObject2 = str2;
        if (localHttpURLConnection != null)
        {
          localHttpURLConnection.disconnect();
          localObject2 = str2;
          k = i;
        }
      }
      catch (IOException localIOException)
      {
        boolean bool;
        localObject2 = localObject1;
        validationLog("Probably not a portal: exception " + localIOException);
        j = i;
        if (i != 599) {
          continue;
        }
        localObject2 = localObject1;
        validationLog("CaptivePortal = " + this.mIsCaptivePortalCheckEnabled + ", PortalNotification = " + this.mIsPortalNotificationEnabled + ", mRetryCount = " + this.mRetryCount);
        j = i;
        localObject2 = localObject1;
        if (this.mIsCaptivePortalCheckEnabled) {
          continue;
        }
        j = i;
        localObject2 = localObject1;
        if (!this.mIsPortalNotificationEnabled) {
          continue;
        }
        localObject2 = localObject1;
        int k = this.mRetryCount;
        j = i;
        if (k < 5) {
          continue;
        }
        j = 204;
        k = j;
        localObject2 = str1;
        if (localObject1 == null) {
          continue;
        }
        ((HttpURLConnection)localObject1).disconnect();
        k = j;
        localObject2 = str1;
        continue;
      }
      finally
      {
        if (localObject2 == null) {
          continue;
        }
        ((HttpURLConnection)localObject2).disconnect();
      }
      logValidationProbe(localStopwatch.stop(), paramInt, k);
      return new CaptivePortalProbeResult(k, (String)localObject2, paramURL.toString());
      bool = false;
    }
  }
  
  public static final class CaptivePortalProbeResult
  {
    static final CaptivePortalProbeResult FAILED = new CaptivePortalProbeResult(599);
    final String detectUrl;
    private final int mHttpResponseCode;
    final String redirectUrl;
    
    public CaptivePortalProbeResult(int paramInt)
    {
      this(paramInt, null, null);
    }
    
    public CaptivePortalProbeResult(int paramInt, String paramString1, String paramString2)
    {
      this.mHttpResponseCode = paramInt;
      this.redirectUrl = paramString1;
      this.detectUrl = paramString2;
    }
    
    boolean isPortal()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (!isSuccessful())
      {
        bool1 = bool2;
        if (this.mHttpResponseCode >= 200)
        {
          bool1 = bool2;
          if (this.mHttpResponseCode <= 399) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    
    boolean isSuccessful()
    {
      return this.mHttpResponseCode == 204;
    }
  }
  
  private class CaptivePortalState
    extends State
  {
    private static final String ACTION_LAUNCH_CAPTIVE_PORTAL_APP = "android.net.netmon.launchCaptivePortalApp";
    
    private CaptivePortalState() {}
    
    public void enter()
    {
      NetworkMonitor.-wrap2(NetworkMonitor.this, 4);
      if (NetworkMonitor.-get4(NetworkMonitor.this)) {
        return;
      }
      if (NetworkMonitor.-get9(NetworkMonitor.this) == null) {
        NetworkMonitor.-set2(NetworkMonitor.this, new NetworkMonitor.CustomIntentReceiver(NetworkMonitor.this, "android.net.netmon.launchCaptivePortalApp", new Random().nextInt(), 532491));
      }
      Message localMessage = NetworkMonitor.this.obtainMessage(532490, 1, NetworkMonitor.-get11(NetworkMonitor.this).network.netId, NetworkMonitor.-get9(NetworkMonitor.this).getPendingIntent());
      NetworkMonitor.-get1(NetworkMonitor.this).sendMessage(localMessage);
      NetworkMonitor.this.sendMessageDelayed(532492, 0, 600000L);
    }
    
    public void exit()
    {
      NetworkMonitor.-wrap4(NetworkMonitor.this, 532492);
    }
  }
  
  private class CustomIntentReceiver
    extends BroadcastReceiver
  {
    private final String mAction;
    private final int mToken;
    private final int mWhat;
    
    CustomIntentReceiver(String paramString, int paramInt1, int paramInt2)
    {
      this.mToken = paramInt1;
      this.mWhat = paramInt2;
      this.mAction = (paramString + "_" + NetworkMonitor.-get11(NetworkMonitor.this).network.netId + "_" + paramInt1);
      NetworkMonitor.-get2(NetworkMonitor.this).registerReceiver(this, new IntentFilter(this.mAction));
    }
    
    public PendingIntent getPendingIntent()
    {
      Intent localIntent = new Intent(this.mAction);
      localIntent.setPackage(NetworkMonitor.-get2(NetworkMonitor.this).getPackageName());
      return PendingIntent.getBroadcast(NetworkMonitor.-get2(NetworkMonitor.this), 0, localIntent, 0);
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (paramIntent.getAction().equals(this.mAction)) {
        NetworkMonitor.this.sendMessage(NetworkMonitor.this.obtainMessage(this.mWhat, this.mToken));
      }
    }
  }
  
  private class DefaultState
    extends State
  {
    private DefaultState() {}
    
    public boolean processMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return true;
      case 532481: 
        NetworkMonitor.-wrap1(NetworkMonitor.this, 1);
        NetworkMonitor.-wrap5(NetworkMonitor.this, NetworkMonitor.-get5(NetworkMonitor.this));
        return true;
      case 532487: 
        NetworkMonitor.-wrap1(NetworkMonitor.this, 7);
        if (NetworkMonitor.-get9(NetworkMonitor.this) != null)
        {
          NetworkMonitor.-get2(NetworkMonitor.this).unregisterReceiver(NetworkMonitor.-get9(NetworkMonitor.this));
          NetworkMonitor.-set2(NetworkMonitor.this, null);
        }
        NetworkMonitor.-wrap3(NetworkMonitor.this);
        return true;
      case 532488: 
      case 532492: 
        NetworkMonitor.this.log("Forcing reevaluation for UID " + paramMessage.arg1);
        NetworkMonitor.-set5(NetworkMonitor.this, paramMessage.arg1);
        NetworkMonitor.-wrap5(NetworkMonitor.this, NetworkMonitor.-get5(NetworkMonitor.this));
        return true;
      }
      NetworkMonitor.this.log("CaptivePortal App responded with " + paramMessage.arg1);
      NetworkMonitor.-set6(NetworkMonitor.this, false);
      switch (paramMessage.arg1)
      {
      default: 
        return true;
      case 0: 
        NetworkMonitor.this.sendMessage(532488, 0, 0);
        return true;
      case 2: 
        NetworkMonitor.-set0(NetworkMonitor.this, true);
        NetworkMonitor.-wrap5(NetworkMonitor.this, NetworkMonitor.-get15(NetworkMonitor.this));
        return true;
      }
      NetworkMonitor.-set0(NetworkMonitor.this, true);
      NetworkMonitor.-set7(NetworkMonitor.this, true);
      if (!NetworkMonitor.-get7(NetworkMonitor.this)) {
        NetworkMonitor.-get1(NetworkMonitor.this).sendMessage(NetworkMonitor.this.obtainMessage(532482, 0, NetworkMonitor.-get10(NetworkMonitor.this), null));
      }
      for (;;)
      {
        NetworkMonitor.-set5(NetworkMonitor.this, 0);
        NetworkMonitor.-wrap5(NetworkMonitor.this, NetworkMonitor.-get5(NetworkMonitor.this));
        return true;
        NetworkMonitor.-get1(NetworkMonitor.this).sendMessage(NetworkMonitor.this.obtainMessage(532482, 1, NetworkMonitor.-get10(NetworkMonitor.this), null));
      }
    }
  }
  
  private class EvaluatingState
    extends State
  {
    private int mAttempts;
    private int mReevaluateDelayMs;
    
    private EvaluatingState() {}
    
    public void enter()
    {
      if (!NetworkMonitor.-get6(NetworkMonitor.this).isStarted()) {
        NetworkMonitor.-get6(NetworkMonitor.this).start();
      }
      NetworkMonitor localNetworkMonitor1 = NetworkMonitor.this;
      NetworkMonitor localNetworkMonitor2 = NetworkMonitor.this;
      localNetworkMonitor1.sendMessage(532486, NetworkMonitor.-set3(localNetworkMonitor2, NetworkMonitor.-get12(localNetworkMonitor2) + 1), 0);
      if (NetworkMonitor.-get13(NetworkMonitor.this) != -1)
      {
        TrafficStats.setThreadStatsUid(NetworkMonitor.-get13(NetworkMonitor.this));
        NetworkMonitor.-set5(NetworkMonitor.this, -1);
      }
      this.mReevaluateDelayMs = 1000;
      this.mAttempts = 0;
      NetworkMonitor.-set4(NetworkMonitor.this, 0);
    }
    
    public void exit() {}
    
    public boolean processMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      case 532487: 
      default: 
        return false;
      case 532486: 
        if ((paramMessage.arg1 != NetworkMonitor.-get12(NetworkMonitor.this)) || (NetworkMonitor.-get14(NetworkMonitor.this))) {
          return true;
        }
        if (!NetworkMonitor.-get3(NetworkMonitor.this).networkCapabilities.satisfiedByNetworkCapabilities(NetworkMonitor.-get11(NetworkMonitor.this).networkCapabilities))
        {
          NetworkMonitor.-wrap6(NetworkMonitor.this, "Network would not satisfy default request, not validating");
          NetworkMonitor.-wrap5(NetworkMonitor.this, NetworkMonitor.-get15(NetworkMonitor.this));
          return true;
        }
        this.mAttempts += 1;
        NetworkMonitor.-set4(NetworkMonitor.this, this.mAttempts);
        paramMessage = NetworkMonitor.this.isCaptivePortal();
        if (paramMessage.isSuccessful())
        {
          NetworkMonitor.-wrap5(NetworkMonitor.this, NetworkMonitor.-get15(NetworkMonitor.this));
          return true;
        }
        if (paramMessage.isPortal())
        {
          if (!NetworkMonitor.-get7(NetworkMonitor.this)) {
            NetworkMonitor.-get1(NetworkMonitor.this).sendMessage(NetworkMonitor.this.obtainMessage(532482, 0, NetworkMonitor.-get10(NetworkMonitor.this), paramMessage.redirectUrl));
          }
          for (;;)
          {
            NetworkMonitor.-set1(NetworkMonitor.this, paramMessage);
            NetworkMonitor.-wrap5(NetworkMonitor.this, NetworkMonitor.-get0(NetworkMonitor.this));
            return true;
            NetworkMonitor.-get1(NetworkMonitor.this).sendMessage(NetworkMonitor.this.obtainMessage(532482, 1, NetworkMonitor.-get10(NetworkMonitor.this), paramMessage.redirectUrl));
          }
        }
        Object localObject = NetworkMonitor.this;
        NetworkMonitor localNetworkMonitor = NetworkMonitor.this;
        localObject = ((NetworkMonitor)localObject).obtainMessage(532486, NetworkMonitor.-set3(localNetworkMonitor, NetworkMonitor.-get12(localNetworkMonitor) + 1), 0);
        NetworkMonitor.this.sendMessageDelayed((Message)localObject, this.mReevaluateDelayMs);
        NetworkMonitor.-wrap1(NetworkMonitor.this, 3);
        if (!NetworkMonitor.-get7(NetworkMonitor.this)) {
          NetworkMonitor.-get1(NetworkMonitor.this).sendMessage(NetworkMonitor.this.obtainMessage(532482, 0, NetworkMonitor.-get10(NetworkMonitor.this), paramMessage.redirectUrl));
        }
        for (;;)
        {
          if (this.mAttempts >= 5) {
            TrafficStats.clearThreadStatsUid();
          }
          this.mReevaluateDelayMs *= 2;
          if (this.mReevaluateDelayMs <= 600000) {
            break;
          }
          this.mReevaluateDelayMs = 600000;
          return true;
          NetworkMonitor.-get1(NetworkMonitor.this).sendMessage(NetworkMonitor.this.obtainMessage(532482, 1, NetworkMonitor.-get10(NetworkMonitor.this), paramMessage.redirectUrl));
        }
      }
      return this.mAttempts < 5;
    }
  }
  
  private class MaybeNotifyState
    extends State
  {
    private MaybeNotifyState() {}
    
    public void exit()
    {
      Message localMessage = NetworkMonitor.this.obtainMessage(532490, 0, NetworkMonitor.-get11(NetworkMonitor.this).network.netId, null);
      NetworkMonitor.-get1(NetworkMonitor.this).sendMessage(localMessage);
    }
    
    public boolean processMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return false;
      }
      paramMessage = new Intent("android.net.conn.CAPTIVE_PORTAL");
      paramMessage.putExtra("android.net.extra.NETWORK", NetworkMonitor.-get11(NetworkMonitor.this).network);
      paramMessage.putExtra("android.net.extra.CAPTIVE_PORTAL", new CaptivePortal(new ICaptivePortal.Stub()
      {
        public void appResponse(int paramAnonymousInt)
        {
          if (paramAnonymousInt == 2) {
            NetworkMonitor.-get2(NetworkMonitor.this).enforceCallingPermission("android.permission.CONNECTIVITY_INTERNAL", "CaptivePortal");
          }
          NetworkMonitor.this.sendMessage(532489, paramAnonymousInt);
        }
      }));
      paramMessage.putExtra("android.net.extra.CAPTIVE_PORTAL_URL", NetworkMonitor.-get8(NetworkMonitor.this).detectUrl);
      paramMessage.setFlags(272629760);
      NetworkMonitor.-get2(NetworkMonitor.this).startActivityAsUser(paramMessage, UserHandle.CURRENT);
      return true;
    }
  }
  
  private class ValidatedState
    extends State
  {
    private ValidatedState() {}
    
    public void enter()
    {
      NetworkMonitor.-wrap2(NetworkMonitor.this, 2);
      NetworkMonitor.-get1(NetworkMonitor.this).sendMessage(NetworkMonitor.this.obtainMessage(532482, 0, NetworkMonitor.-get11(NetworkMonitor.this).network.netId, null));
    }
    
    public boolean processMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return false;
      }
      NetworkMonitor.-wrap5(NetworkMonitor.this, NetworkMonitor.-get15(NetworkMonitor.this));
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/NetworkMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */