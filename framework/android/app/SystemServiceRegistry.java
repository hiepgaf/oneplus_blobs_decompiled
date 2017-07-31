package android.app;

import android.accounts.AccountManager;
import android.accounts.IAccountManager.Stub;
import android.app.admin.DevicePolicyManager;
import android.app.job.IJobScheduler.Stub;
import android.app.job.JobScheduler;
import android.app.trust.TrustManager;
import android.app.usage.IUsageStatsManager;
import android.app.usage.IUsageStatsManager.Stub;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStatsManager;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IRestrictionsManager.Stub;
import android.content.RestrictionsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.hardware.ConsumerIrManager;
import android.hardware.ISerialManager.Stub;
import android.hardware.SensorManager;
import android.hardware.SerialManager;
import android.hardware.SystemSensorManager;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.SDManager;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.IFingerprintService;
import android.hardware.fingerprint.IFingerprintService.Stub;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.IHdmiControlService.Stub;
import android.hardware.input.InputManager;
import android.hardware.location.ContextHubManager;
import android.hardware.radio.RadioManager;
import android.hardware.usb.IUsbManager.Stub;
import android.hardware.usb.UsbManager;
import android.location.CountryDetector;
import android.location.ICountryDetector.Stub;
import android.location.ILocationManager.Stub;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.media.midi.IMidiManager.Stub;
import android.media.midi.MidiManager;
import android.media.projection.MediaProjectionManager;
import android.media.session.MediaSessionManager;
import android.media.soundtrigger.SoundTriggerManager;
import android.media.tv.ITvInputManager.Stub;
import android.media.tv.TvInputManager;
import android.net.ConnectivityManager;
import android.net.ConnectivityThread;
import android.net.EthernetManager;
import android.net.IConnectivityManager.Stub;
import android.net.IEthernetManager;
import android.net.IEthernetManager.Stub;
import android.net.INetworkPolicyManager.Stub;
import android.net.NetworkPolicyManager;
import android.net.NetworkScoreManager;
import android.net.nsd.INsdManager;
import android.net.nsd.INsdManager.Stub;
import android.net.nsd.NsdManager;
import android.net.wifi.IRttManager;
import android.net.wifi.IRttManager.Stub;
import android.net.wifi.IWifiManager;
import android.net.wifi.IWifiManager.Stub;
import android.net.wifi.IWifiScanner;
import android.net.wifi.IWifiScanner.Stub;
import android.net.wifi.RttManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiScanner;
import android.net.wifi.nan.IWifiNanManager;
import android.net.wifi.nan.IWifiNanManager.Stub;
import android.net.wifi.nan.WifiNanManager;
import android.net.wifi.p2p.IWifiP2pManager.Stub;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.DropBoxManager;
import android.os.Handler;
import android.os.HardwarePropertiesManager;
import android.os.IHardwarePropertiesManager;
import android.os.IHardwarePropertiesManager.Stub;
import android.os.IPowerManager;
import android.os.IPowerManager.Stub;
import android.os.IRecoverySystem.Stub;
import android.os.IUserManager.Stub;
import android.os.PowerManager;
import android.os.Process;
import android.os.RecoverySystem;
import android.os.ServiceManager;
import android.os.SystemVibrator;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.os.health.SystemHealthManager;
import android.os.storage.StorageManager;
import android.print.IPrintManager;
import android.print.IPrintManager.Stub;
import android.print.PrintManager;
import android.service.persistentdata.IPersistentDataBlockService;
import android.service.persistentdata.IPersistentDataBlockService.Stub;
import android.service.persistentdata.PersistentDataBlockManager;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManagerImpl;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.CaptioningManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextServicesManager;
import com.android.internal.app.IAppOpsService.Stub;
import com.android.internal.app.ISoundTriggerService.Stub;
import com.android.internal.appwidget.IAppWidgetService.Stub;
import com.android.internal.os.IDropBoxManagerService;
import com.android.internal.os.IDropBoxManagerService.Stub;
import com.android.internal.policy.PhoneLayoutInflater;
import com.oem.os.OnePlusNfcManager;
import com.oem.os.ThreeKeyManager;
import com.oneplus.longshot.LongScreenshotManager;
import java.util.HashMap;

final class SystemServiceRegistry
{
  private static final HashMap<String, ServiceFetcher<?>> SYSTEM_SERVICE_FETCHERS;
  private static final HashMap<Class<?>, String> SYSTEM_SERVICE_NAMES = new HashMap();
  private static final String TAG = "SystemServiceRegistry";
  private static int sServiceCacheSize;
  
  static
  {
    SYSTEM_SERVICE_FETCHERS = new HashMap();
    registerService("accessibility", AccessibilityManager.class, new CachedServiceFetcher()
    {
      public AccessibilityManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return AccessibilityManager.getInstance(paramAnonymousContextImpl);
      }
    });
    registerService("captioning", CaptioningManager.class, new CachedServiceFetcher()
    {
      public CaptioningManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new CaptioningManager(paramAnonymousContextImpl);
      }
    });
    registerService("account", AccountManager.class, new CachedServiceFetcher()
    {
      public AccountManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new AccountManager(paramAnonymousContextImpl, IAccountManager.Stub.asInterface(ServiceManager.getService("account")));
      }
    });
    registerService("activity", ActivityManager.class, new CachedServiceFetcher()
    {
      public ActivityManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new ActivityManager(paramAnonymousContextImpl.getOuterContext(), paramAnonymousContextImpl.mMainThread.getHandler());
      }
    });
    registerService("alarm", AlarmManager.class, new CachedServiceFetcher()
    {
      public AlarmManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new AlarmManager(IAlarmManager.Stub.asInterface(ServiceManager.getService("alarm")), paramAnonymousContextImpl);
      }
    });
    registerService("audio", AudioManager.class, new CachedServiceFetcher()
    {
      public AudioManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new AudioManager(paramAnonymousContextImpl);
      }
    });
    registerService("media_router", MediaRouter.class, new CachedServiceFetcher()
    {
      public MediaRouter createService(ContextImpl paramAnonymousContextImpl)
      {
        return new MediaRouter(paramAnonymousContextImpl);
      }
    });
    registerService("bluetooth", BluetoothManager.class, new CachedServiceFetcher()
    {
      public BluetoothManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new BluetoothManager(paramAnonymousContextImpl);
      }
    });
    registerService("hdmi_control", HdmiControlManager.class, new StaticServiceFetcher()
    {
      public HdmiControlManager createService()
      {
        return new HdmiControlManager(IHdmiControlService.Stub.asInterface(ServiceManager.getService("hdmi_control")));
      }
    });
    registerService("clipboard", android.content.ClipboardManager.class, new CachedServiceFetcher()
    {
      public android.content.ClipboardManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new android.content.ClipboardManager(paramAnonymousContextImpl.getOuterContext(), paramAnonymousContextImpl.mMainThread.getHandler());
      }
    });
    SYSTEM_SERVICE_NAMES.put(android.text.ClipboardManager.class, "clipboard");
    registerService("connectivity", ConnectivityManager.class, new StaticApplicationContextServiceFetcher()
    {
      public ConnectivityManager createService(Context paramAnonymousContext)
      {
        return new ConnectivityManager(paramAnonymousContext, IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity")));
      }
    });
    registerService("country_detector", CountryDetector.class, new StaticServiceFetcher()
    {
      public CountryDetector createService()
      {
        return new CountryDetector(ICountryDetector.Stub.asInterface(ServiceManager.getService("country_detector")));
      }
    });
    registerService("device_policy", DevicePolicyManager.class, new CachedServiceFetcher()
    {
      public DevicePolicyManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return DevicePolicyManager.create(paramAnonymousContextImpl);
      }
    });
    registerService("download", DownloadManager.class, new CachedServiceFetcher()
    {
      public DownloadManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new DownloadManager(paramAnonymousContextImpl);
      }
    });
    registerService("batterymanager", BatteryManager.class, new StaticServiceFetcher()
    {
      public BatteryManager createService()
      {
        return new BatteryManager();
      }
    });
    registerService("nfc", NfcManager.class, new CachedServiceFetcher()
    {
      public NfcManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new NfcManager(paramAnonymousContextImpl);
      }
    });
    registerService("dropbox", DropBoxManager.class, new CachedServiceFetcher()
    {
      public DropBoxManager createService(ContextImpl paramAnonymousContextImpl)
      {
        IDropBoxManagerService localIDropBoxManagerService = IDropBoxManagerService.Stub.asInterface(ServiceManager.getService("dropbox"));
        if (localIDropBoxManagerService == null) {
          return null;
        }
        return new DropBoxManager(paramAnonymousContextImpl, localIDropBoxManagerService);
      }
    });
    registerService("input", InputManager.class, new StaticServiceFetcher()
    {
      public InputManager createService()
      {
        return InputManager.getInstance();
      }
    });
    registerService("display", DisplayManager.class, new CachedServiceFetcher()
    {
      public DisplayManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new DisplayManager(paramAnonymousContextImpl.getOuterContext());
      }
    });
    registerService("smartdisplay", SDManager.class, new CachedServiceFetcher()
    {
      public SDManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new SDManager(paramAnonymousContextImpl);
      }
    });
    registerService("input_method", InputMethodManager.class, new StaticServiceFetcher()
    {
      public InputMethodManager createService()
      {
        return InputMethodManager.getInstance();
      }
    });
    registerService("textservices", TextServicesManager.class, new StaticServiceFetcher()
    {
      public TextServicesManager createService()
      {
        return TextServicesManager.getInstance();
      }
    });
    registerService("keyguard", KeyguardManager.class, new StaticServiceFetcher()
    {
      public KeyguardManager createService()
      {
        return new KeyguardManager();
      }
    });
    registerService("layout_inflater", LayoutInflater.class, new CachedServiceFetcher()
    {
      public LayoutInflater createService(ContextImpl paramAnonymousContextImpl)
      {
        return new PhoneLayoutInflater(paramAnonymousContextImpl.getOuterContext());
      }
    });
    registerService("location", LocationManager.class, new CachedServiceFetcher()
    {
      public LocationManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new LocationManager(paramAnonymousContextImpl, ILocationManager.Stub.asInterface(ServiceManager.getService("location")));
      }
    });
    registerService("netpolicy", NetworkPolicyManager.class, new CachedServiceFetcher()
    {
      public NetworkPolicyManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new NetworkPolicyManager(paramAnonymousContextImpl, INetworkPolicyManager.Stub.asInterface(ServiceManager.getService("netpolicy")));
      }
    });
    registerService("notification", NotificationManager.class, new CachedServiceFetcher()
    {
      public NotificationManager createService(ContextImpl paramAnonymousContextImpl)
      {
        Context localContext = paramAnonymousContextImpl.getOuterContext();
        return new NotificationManager(new ContextThemeWrapper(localContext, Resources.selectSystemTheme(0, localContext.getApplicationInfo().targetSdkVersion, 16973835, 16973935, 16974126, 16974130)), paramAnonymousContextImpl.mMainThread.getHandler());
      }
    });
    registerService("servicediscovery", NsdManager.class, new CachedServiceFetcher()
    {
      public NsdManager createService(ContextImpl paramAnonymousContextImpl)
      {
        INsdManager localINsdManager = INsdManager.Stub.asInterface(ServiceManager.getService("servicediscovery"));
        return new NsdManager(paramAnonymousContextImpl.getOuterContext(), localINsdManager);
      }
    });
    registerService("power", PowerManager.class, new CachedServiceFetcher()
    {
      public PowerManager createService(ContextImpl paramAnonymousContextImpl)
      {
        IPowerManager localIPowerManager = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
        if (localIPowerManager == null) {
          Log.wtf("SystemServiceRegistry", "Failed to get power manager service.");
        }
        return new PowerManager(paramAnonymousContextImpl.getOuterContext(), localIPowerManager, paramAnonymousContextImpl.mMainThread.getHandler());
      }
    });
    registerService("recovery", RecoverySystem.class, new CachedServiceFetcher()
    {
      public RecoverySystem createService(ContextImpl paramAnonymousContextImpl)
      {
        paramAnonymousContextImpl = IRecoverySystem.Stub.asInterface(ServiceManager.getService("recovery"));
        if (paramAnonymousContextImpl == null) {
          Log.wtf("SystemServiceRegistry", "Failed to get recovery service.");
        }
        return new RecoverySystem(paramAnonymousContextImpl);
      }
    });
    registerService("search", SearchManager.class, new CachedServiceFetcher()
    {
      public SearchManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new SearchManager(paramAnonymousContextImpl.getOuterContext(), paramAnonymousContextImpl.mMainThread.getHandler());
      }
    });
    registerService("sensor", SensorManager.class, new CachedServiceFetcher()
    {
      public SensorManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new SystemSensorManager(paramAnonymousContextImpl.getOuterContext(), paramAnonymousContextImpl.mMainThread.getHandler().getLooper());
      }
    });
    registerService("statusbar", StatusBarManager.class, new CachedServiceFetcher()
    {
      public StatusBarManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new StatusBarManager(paramAnonymousContextImpl.getOuterContext());
      }
    });
    registerService("storage", StorageManager.class, new CachedServiceFetcher()
    {
      public StorageManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new StorageManager(paramAnonymousContextImpl, paramAnonymousContextImpl.mMainThread.getHandler().getLooper());
      }
    });
    registerService("phone", TelephonyManager.class, new CachedServiceFetcher()
    {
      public TelephonyManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new TelephonyManager(paramAnonymousContextImpl.getOuterContext());
      }
    });
    registerService("telephony_subscription_service", SubscriptionManager.class, new CachedServiceFetcher()
    {
      public SubscriptionManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new SubscriptionManager(paramAnonymousContextImpl.getOuterContext());
      }
    });
    registerService("carrier_config", CarrierConfigManager.class, new CachedServiceFetcher()
    {
      public CarrierConfigManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new CarrierConfigManager();
      }
    });
    registerService("telecom", TelecomManager.class, new CachedServiceFetcher()
    {
      public TelecomManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new TelecomManager(paramAnonymousContextImpl.getOuterContext());
      }
    });
    registerService("uimode", UiModeManager.class, new CachedServiceFetcher()
    {
      public UiModeManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new UiModeManager();
      }
    });
    registerService("usb", UsbManager.class, new CachedServiceFetcher()
    {
      public UsbManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new UsbManager(paramAnonymousContextImpl, IUsbManager.Stub.asInterface(ServiceManager.getService("usb")));
      }
    });
    registerService("serial", SerialManager.class, new CachedServiceFetcher()
    {
      public SerialManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new SerialManager(paramAnonymousContextImpl, ISerialManager.Stub.asInterface(ServiceManager.getService("serial")));
      }
    });
    registerService("vibrator", Vibrator.class, new CachedServiceFetcher()
    {
      public Vibrator createService(ContextImpl paramAnonymousContextImpl)
      {
        return new SystemVibrator(paramAnonymousContextImpl);
      }
    });
    registerService("wallpaper", WallpaperManager.class, new CachedServiceFetcher()
    {
      public WallpaperManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new WallpaperManager(paramAnonymousContextImpl.getOuterContext(), paramAnonymousContextImpl.mMainThread.getHandler());
      }
    });
    registerService("wifi", WifiManager.class, new CachedServiceFetcher()
    {
      public WifiManager createService(ContextImpl paramAnonymousContextImpl)
      {
        IWifiManager localIWifiManager = IWifiManager.Stub.asInterface(ServiceManager.getService("wifi"));
        return new WifiManager(paramAnonymousContextImpl.getOuterContext(), localIWifiManager, ConnectivityThread.getInstanceLooper());
      }
    });
    registerService("wifip2p", WifiP2pManager.class, new StaticServiceFetcher()
    {
      public WifiP2pManager createService()
      {
        return new WifiP2pManager(IWifiP2pManager.Stub.asInterface(ServiceManager.getService("wifip2p")));
      }
    });
    registerService("wifinan", WifiNanManager.class, new StaticServiceFetcher()
    {
      public WifiNanManager createService()
      {
        IWifiNanManager localIWifiNanManager = IWifiNanManager.Stub.asInterface(ServiceManager.getService("wifinan"));
        if (localIWifiNanManager == null) {
          return null;
        }
        return new WifiNanManager(localIWifiNanManager);
      }
    });
    registerService("wifiscanner", WifiScanner.class, new CachedServiceFetcher()
    {
      public WifiScanner createService(ContextImpl paramAnonymousContextImpl)
      {
        IWifiScanner localIWifiScanner = IWifiScanner.Stub.asInterface(ServiceManager.getService("wifiscanner"));
        return new WifiScanner(paramAnonymousContextImpl.getOuterContext(), localIWifiScanner, ConnectivityThread.getInstanceLooper());
      }
    });
    registerService("rttmanager", RttManager.class, new CachedServiceFetcher()
    {
      public RttManager createService(ContextImpl paramAnonymousContextImpl)
      {
        IRttManager localIRttManager = IRttManager.Stub.asInterface(ServiceManager.getService("rttmanager"));
        return new RttManager(paramAnonymousContextImpl.getOuterContext(), localIRttManager, ConnectivityThread.getInstanceLooper());
      }
    });
    registerService("ethernet", EthernetManager.class, new CachedServiceFetcher()
    {
      public EthernetManager createService(ContextImpl paramAnonymousContextImpl)
      {
        IEthernetManager localIEthernetManager = IEthernetManager.Stub.asInterface(ServiceManager.getService("ethernet"));
        return new EthernetManager(paramAnonymousContextImpl.getOuterContext(), localIEthernetManager);
      }
    });
    registerService("window", WindowManager.class, new CachedServiceFetcher()
    {
      public WindowManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new WindowManagerImpl(paramAnonymousContextImpl);
      }
    });
    registerService("user", UserManager.class, new CachedServiceFetcher()
    {
      public UserManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new UserManager(paramAnonymousContextImpl, IUserManager.Stub.asInterface(ServiceManager.getService("user")));
      }
    });
    registerService("appops", AppOpsManager.class, new CachedServiceFetcher()
    {
      public AppOpsManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new AppOpsManager(paramAnonymousContextImpl, IAppOpsService.Stub.asInterface(ServiceManager.getService("appops")));
      }
    });
    registerService("camera", CameraManager.class, new CachedServiceFetcher()
    {
      public CameraManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new CameraManager(paramAnonymousContextImpl);
      }
    });
    registerService("launcherapps", LauncherApps.class, new CachedServiceFetcher()
    {
      public LauncherApps createService(ContextImpl paramAnonymousContextImpl)
      {
        return new LauncherApps(paramAnonymousContextImpl);
      }
    });
    registerService("restrictions", RestrictionsManager.class, new CachedServiceFetcher()
    {
      public RestrictionsManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new RestrictionsManager(paramAnonymousContextImpl, IRestrictionsManager.Stub.asInterface(ServiceManager.getService("restrictions")));
      }
    });
    registerService("print", PrintManager.class, new CachedServiceFetcher()
    {
      public PrintManager createService(ContextImpl paramAnonymousContextImpl)
      {
        IPrintManager localIPrintManager = IPrintManager.Stub.asInterface(ServiceManager.getService("print"));
        return new PrintManager(paramAnonymousContextImpl.getOuterContext(), localIPrintManager, UserHandle.myUserId(), UserHandle.getAppId(Process.myUid()));
      }
    });
    registerService("consumer_ir", ConsumerIrManager.class, new CachedServiceFetcher()
    {
      public ConsumerIrManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new ConsumerIrManager(paramAnonymousContextImpl);
      }
    });
    registerService("media_session", MediaSessionManager.class, new CachedServiceFetcher()
    {
      public MediaSessionManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new MediaSessionManager(paramAnonymousContextImpl);
      }
    });
    registerService("trust", TrustManager.class, new StaticServiceFetcher()
    {
      public TrustManager createService()
      {
        return new TrustManager(ServiceManager.getService("trust"));
      }
    });
    registerService("fingerprint", FingerprintManager.class, new CachedServiceFetcher()
    {
      public FingerprintManager createService(ContextImpl paramAnonymousContextImpl)
      {
        IFingerprintService localIFingerprintService = IFingerprintService.Stub.asInterface(ServiceManager.getService("fingerprint"));
        return new FingerprintManager(paramAnonymousContextImpl.getOuterContext(), localIFingerprintService);
      }
    });
    registerService("tv_input", TvInputManager.class, new StaticServiceFetcher()
    {
      public TvInputManager createService()
      {
        return new TvInputManager(ITvInputManager.Stub.asInterface(ServiceManager.getService("tv_input")), UserHandle.myUserId());
      }
    });
    registerService("network_score", NetworkScoreManager.class, new CachedServiceFetcher()
    {
      public NetworkScoreManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new NetworkScoreManager(paramAnonymousContextImpl);
      }
    });
    registerService("usagestats", UsageStatsManager.class, new CachedServiceFetcher()
    {
      public UsageStatsManager createService(ContextImpl paramAnonymousContextImpl)
      {
        IUsageStatsManager localIUsageStatsManager = IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats"));
        return new UsageStatsManager(paramAnonymousContextImpl.getOuterContext(), localIUsageStatsManager);
      }
    });
    registerService("netstats", NetworkStatsManager.class, new CachedServiceFetcher()
    {
      public NetworkStatsManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new NetworkStatsManager(paramAnonymousContextImpl.getOuterContext());
      }
    });
    registerService("jobscheduler", JobScheduler.class, new StaticServiceFetcher()
    {
      public JobScheduler createService()
      {
        return new JobSchedulerImpl(IJobScheduler.Stub.asInterface(ServiceManager.getService("jobscheduler")));
      }
    });
    registerService("persistent_data_block", PersistentDataBlockManager.class, new StaticServiceFetcher()
    {
      public PersistentDataBlockManager createService()
      {
        IPersistentDataBlockService localIPersistentDataBlockService = IPersistentDataBlockService.Stub.asInterface(ServiceManager.getService("persistent_data_block"));
        if (localIPersistentDataBlockService != null) {
          return new PersistentDataBlockManager(localIPersistentDataBlockService);
        }
        return null;
      }
    });
    registerService("media_projection", MediaProjectionManager.class, new CachedServiceFetcher()
    {
      public MediaProjectionManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new MediaProjectionManager(paramAnonymousContextImpl);
      }
    });
    registerService("appwidget", AppWidgetManager.class, new CachedServiceFetcher()
    {
      public AppWidgetManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new AppWidgetManager(paramAnonymousContextImpl, IAppWidgetService.Stub.asInterface(ServiceManager.getService("appwidget")));
      }
    });
    registerService("midi", MidiManager.class, new CachedServiceFetcher()
    {
      public MidiManager createService(ContextImpl paramAnonymousContextImpl)
      {
        paramAnonymousContextImpl = ServiceManager.getService("midi");
        if (paramAnonymousContextImpl == null) {
          return null;
        }
        return new MidiManager(IMidiManager.Stub.asInterface(paramAnonymousContextImpl));
      }
    });
    registerService("radio", RadioManager.class, new CachedServiceFetcher()
    {
      public RadioManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new RadioManager(paramAnonymousContextImpl);
      }
    });
    registerService("threekey", ThreeKeyManager.class, new CachedServiceFetcher()
    {
      public ThreeKeyManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new ThreeKeyManager(paramAnonymousContextImpl);
      }
    });
    registerService("oneplus_nfc_service", OnePlusNfcManager.class, new CachedServiceFetcher()
    {
      public OnePlusNfcManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new OnePlusNfcManager(paramAnonymousContextImpl);
      }
    });
    registerService("hardware_properties", HardwarePropertiesManager.class, new CachedServiceFetcher()
    {
      public HardwarePropertiesManager createService(ContextImpl paramAnonymousContextImpl)
      {
        IHardwarePropertiesManager localIHardwarePropertiesManager = IHardwarePropertiesManager.Stub.asInterface(ServiceManager.getService("hardware_properties"));
        if (localIHardwarePropertiesManager == null)
        {
          Log.wtf("SystemServiceRegistry", "Failed to get hardwareproperties service.");
          return null;
        }
        return new HardwarePropertiesManager(paramAnonymousContextImpl, localIHardwarePropertiesManager);
      }
    });
    registerService("soundtrigger", SoundTriggerManager.class, new CachedServiceFetcher()
    {
      public SoundTriggerManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new SoundTriggerManager(paramAnonymousContextImpl, ISoundTriggerService.Stub.asInterface(ServiceManager.getService("soundtrigger")));
      }
    });
    registerService("shortcut", ShortcutManager.class, new CachedServiceFetcher()
    {
      public ShortcutManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new ShortcutManager(paramAnonymousContextImpl);
      }
    });
    registerService("systemhealth", SystemHealthManager.class, new CachedServiceFetcher()
    {
      public SystemHealthManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new SystemHealthManager();
      }
    });
    registerService("contexthub", ContextHubManager.class, new CachedServiceFetcher()
    {
      public ContextHubManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return new ContextHubManager(paramAnonymousContextImpl.getOuterContext(), paramAnonymousContextImpl.mMainThread.getHandler().getLooper());
      }
    });
    registerService("longshot", LongScreenshotManager.class, new CachedServiceFetcher()
    {
      public LongScreenshotManager createService(ContextImpl paramAnonymousContextImpl)
      {
        return LongScreenshotManager.getInstance();
      }
    });
  }
  
  public static Object[] createServiceCache()
  {
    return new Object[sServiceCacheSize];
  }
  
  public static Object getSystemService(ContextImpl paramContextImpl, String paramString)
  {
    Object localObject = null;
    ServiceFetcher localServiceFetcher = (ServiceFetcher)SYSTEM_SERVICE_FETCHERS.get(paramString);
    paramString = (String)localObject;
    if (localServiceFetcher != null) {
      paramString = localServiceFetcher.getService(paramContextImpl);
    }
    return paramString;
  }
  
  public static String getSystemServiceName(Class<?> paramClass)
  {
    return (String)SYSTEM_SERVICE_NAMES.get(paramClass);
  }
  
  private static <T> void registerService(String paramString, Class<T> paramClass, ServiceFetcher<T> paramServiceFetcher)
  {
    SYSTEM_SERVICE_NAMES.put(paramClass, paramString);
    SYSTEM_SERVICE_FETCHERS.put(paramString, paramServiceFetcher);
  }
  
  static abstract class CachedServiceFetcher<T>
    implements SystemServiceRegistry.ServiceFetcher<T>
  {
    private final int mCacheIndex;
    
    public CachedServiceFetcher()
    {
      int i = SystemServiceRegistry.-get0();
      SystemServiceRegistry.-set0(i + 1);
      this.mCacheIndex = i;
    }
    
    public abstract T createService(ContextImpl paramContextImpl);
    
    public final T getService(ContextImpl paramContextImpl)
    {
      synchronized (paramContextImpl.mServiceCache)
      {
        Object localObject2 = ???[this.mCacheIndex];
        Object localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = createService(paramContextImpl);
          ???[this.mCacheIndex] = localObject1;
        }
        return (T)localObject1;
      }
    }
  }
  
  static abstract interface ServiceFetcher<T>
  {
    public abstract T getService(ContextImpl paramContextImpl);
  }
  
  static abstract class StaticApplicationContextServiceFetcher<T>
    implements SystemServiceRegistry.ServiceFetcher<T>
  {
    private T mCachedInstance;
    
    public abstract T createService(Context paramContext);
    
    /* Error */
    public final T getService(ContextImpl paramContextImpl)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 24	android/app/SystemServiceRegistry$StaticApplicationContextServiceFetcher:mCachedInstance	Ljava/lang/Object;
      //   6: ifnonnull +23 -> 29
      //   9: aload_1
      //   10: invokevirtual 30	android/app/ContextImpl:getApplicationContext	()Landroid/content/Context;
      //   13: astore_2
      //   14: aload_2
      //   15: ifnull +23 -> 38
      //   18: aload_2
      //   19: astore_1
      //   20: aload_0
      //   21: aload_0
      //   22: aload_1
      //   23: invokevirtual 32	android/app/SystemServiceRegistry$StaticApplicationContextServiceFetcher:createService	(Landroid/content/Context;)Ljava/lang/Object;
      //   26: putfield 24	android/app/SystemServiceRegistry$StaticApplicationContextServiceFetcher:mCachedInstance	Ljava/lang/Object;
      //   29: aload_0
      //   30: getfield 24	android/app/SystemServiceRegistry$StaticApplicationContextServiceFetcher:mCachedInstance	Ljava/lang/Object;
      //   33: astore_1
      //   34: aload_0
      //   35: monitorexit
      //   36: aload_1
      //   37: areturn
      //   38: goto -18 -> 20
      //   41: astore_1
      //   42: aload_0
      //   43: monitorexit
      //   44: aload_1
      //   45: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	46	0	this	StaticApplicationContextServiceFetcher
      //   0	46	1	paramContextImpl	ContextImpl
      //   13	6	2	localContext	Context
      // Exception table:
      //   from	to	target	type
      //   2	14	41	finally
      //   20	29	41	finally
      //   29	34	41	finally
    }
  }
  
  static abstract class StaticServiceFetcher<T>
    implements SystemServiceRegistry.ServiceFetcher<T>
  {
    private T mCachedInstance;
    
    public abstract T createService();
    
    public final T getService(ContextImpl paramContextImpl)
    {
      try
      {
        if (this.mCachedInstance == null) {
          this.mCachedInstance = createService();
        }
        paramContextImpl = this.mCachedInstance;
        return paramContextImpl;
      }
      finally {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/SystemServiceRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */