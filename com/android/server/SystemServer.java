package com.android.server;

import android.app.ActivityThread;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.FactoryTest;
import android.os.FileUtils;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.os.RegionalizationEnvironment;
import com.android.internal.os.SamplingProfilerIntegration;
import com.android.server.am.ActivityManagerService;
import com.android.server.am.ActivityManagerService.Lifecycle;
import com.android.server.display.DisplayManagerService;
import com.android.server.display.SDService;
import com.android.server.input.InputManagerService;
import com.android.server.lights.LightsService;
import com.android.server.media.MediaRouterService;
import com.android.server.net.NetworkPolicyManagerService;
import com.android.server.net.NetworkStatsService;
import com.android.server.os.RegionalizationService;
import com.android.server.pm.Installer;
import com.android.server.pm.OtaDexoptService;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.UserManagerService.LifeCycle;
import com.android.server.power.PowerManagerService;
import com.android.server.power.ShutdownThread;
import com.android.server.usage.UsageStatsService;
import com.android.server.webkit.WebViewUpdateService;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public final class SystemServer
{
  private static final String ACCOUNT_SERVICE_CLASS = "com.android.server.accounts.AccountManagerService$Lifecycle";
  private static final String APPWIDGET_SERVICE_CLASS = "com.android.server.appwidget.AppWidgetService";
  private static final String BACKUP_MANAGER_SERVICE_CLASS = "com.android.server.backup.BackupManagerService$Lifecycle";
  private static final String BLOCK_MAP_FILE = "/cache/recovery/block.map";
  private static final String CONTENT_SERVICE_CLASS = "com.android.server.content.ContentService$Lifecycle";
  private static final int DEFAULT_SYSTEM_THEME = 16975000;
  private static final long EARLIEST_SUPPORTED_TIME = 86400000L;
  private static final String ENCRYPTED_STATE = "1";
  private static final String ENCRYPTING_STATE = "trigger_restart_min_framework";
  private static final String ETHERNET_SERVICE_CLASS = "com.android.server.ethernet.EthernetService";
  private static final String JOB_SCHEDULER_SERVICE_CLASS = "com.android.server.job.JobSchedulerService";
  private static final String LOCK_SETTINGS_SERVICE_CLASS = "com.android.server.LockSettingsService$Lifecycle";
  private static final String MIDI_SERVICE_CLASS = "com.android.server.midi.MidiService$Lifecycle";
  private static final String MOUNT_SERVICE_CLASS = "com.android.server.MountService$Lifecycle";
  private static final String PERSISTENT_DATA_BLOCK_PROP = "ro.frp.pst";
  private static final String PRINT_MANAGER_SERVICE_CLASS = "com.android.server.print.PrintManagerService";
  private static final String SEARCH_MANAGER_SERVICE_CLASS = "com.android.server.search.SearchManagerService$Lifecycle";
  private static final long SNAPSHOT_INTERVAL = 3600000L;
  private static final String TAG = "SystemServer";
  private static final String THERMAL_OBSERVER_CLASS = "com.google.android.clockwork.ThermalObserver";
  private static final String UNCRYPT_PACKAGE_FILE = "/cache/recovery/uncrypt_file";
  private static final String USB_SERVICE_CLASS = "com.android.server.usb.UsbService$Lifecycle";
  private static final String VOICE_RECOGNITION_MANAGER_SERVICE_CLASS = "com.android.server.voiceinteraction.VoiceInteractionManagerService";
  private static final String WALLPAPER_SERVICE_CLASS = "com.android.server.wallpaper.WallpaperManagerService$Lifecycle";
  private static final String WEAR_BLUETOOTH_SERVICE_CLASS = "com.google.android.clockwork.bluetooth.WearBluetoothService";
  private static final String WEAR_TIME_SERVICE_CLASS = "com.google.android.clockwork.time.WearTimeService";
  private static final String WEAR_WIFI_MEDIATOR_SERVICE_CLASS = "com.google.android.clockwork.wifi.WearWifiMediatorService";
  private static final String WIFI_NAN_SERVICE_CLASS = "com.android.server.wifi.nan.WifiNanService";
  private static final String WIFI_P2P_SERVICE_CLASS = "com.android.server.wifi.p2p.WifiP2pService";
  private static final String WIFI_SERVICE_CLASS = "com.android.server.wifi.WifiService";
  private static final int sMaxBinderThreads = 31;
  private ActivityManagerService mActivityManagerService;
  private ContentResolver mContentResolver;
  private DisplayManagerService mDisplayManagerService;
  private EntropyMixer mEntropyMixer;
  private final int mFactoryTestMode = FactoryTest.getMode();
  private boolean mFirstBoot;
  private boolean mIsAlarmBoot;
  private boolean mOnlyCore;
  private PackageManager mPackageManager;
  private PackageManagerService mPackageManagerService;
  private PowerManagerService mPowerManagerService;
  private Timer mProfilerSnapshotTimer;
  private Context mSystemContext;
  private SystemServiceManager mSystemServiceManager;
  private WebViewUpdateService mWebViewUpdateService;
  
  private void createSystemContext()
  {
    this.mSystemContext = ActivityThread.systemMain().getSystemContext();
    this.mSystemContext.setTheme(16975000);
  }
  
  public static void main(String[] paramArrayOfString)
  {
    new SystemServer().run();
  }
  
  private void performPendingShutdown()
  {
    String str = SystemProperties.get("sys.shutdown.requested", "");
    if ((str != null) && (str.length() > 0))
    {
      boolean bool;
      if (str.charAt(0) == '1')
      {
        bool = true;
        if (str.length() <= 1) {
          break label133;
        }
      }
      label133:
      for (str = str.substring(1, str.length());; str = null)
      {
        if (!"recovery-update".equals(str)) {
          break label153;
        }
        Object localObject2 = new File("/cache/recovery/uncrypt_file");
        if (!((File)localObject2).exists()) {
          break label153;
        }
        Object localObject1 = null;
        try
        {
          localObject2 = FileUtils.readTextFile((File)localObject2, 0, null);
          localObject1 = localObject2;
        }
        catch (IOException localIOException)
        {
          for (;;)
          {
            Slog.e("SystemServer", "Error reading uncrypt package file", localIOException);
          }
        }
        if ((localObject1 == null) || (!((String)localObject1).startsWith("/data")) || (new File("/cache/recovery/block.map").exists())) {
          break label153;
        }
        Slog.e("SystemServer", "Can't find block map file, uncrypt failed or unexpected runtime restart?");
        return;
        bool = false;
        break;
      }
      label153:
      ShutdownThread.rebootOrShutdown(null, bool, str);
    }
  }
  
  private void reportWtf(String paramString, Throwable paramThrowable)
  {
    Slog.w("SystemServer", "***********************************************");
    Slog.wtf("SystemServer", "BOOT FAILURE " + paramString, paramThrowable);
  }
  
  /* Error */
  private void run()
  {
    // Byte code:
    //   0: ldc2_w 285
    //   3: ldc_w 288
    //   6: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   9: invokestatic 300	java/lang/System:currentTimeMillis	()J
    //   12: ldc2_w 30
    //   15: lcmp
    //   16: ifge +19 -> 35
    //   19: ldc 67
    //   21: ldc_w 302
    //   24: invokestatic 266	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   27: pop
    //   28: ldc2_w 30
    //   31: invokestatic 308	android/os/SystemClock:setCurrentTimeMillis	(J)Z
    //   34: pop
    //   35: ldc_w 310
    //   38: invokestatic 313	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   41: invokevirtual 316	java/lang/String:isEmpty	()Z
    //   44: ifne +39 -> 83
    //   47: ldc_w 318
    //   50: invokestatic 324	java/util/Locale:getDefault	()Ljava/util/Locale;
    //   53: invokevirtual 327	java/util/Locale:toLanguageTag	()Ljava/lang/String;
    //   56: invokestatic 331	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   59: ldc_w 310
    //   62: ldc -60
    //   64: invokestatic 331	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   67: ldc_w 333
    //   70: ldc -60
    //   72: invokestatic 331	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   75: ldc_w 335
    //   78: ldc -60
    //   80: invokestatic 331	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   83: ldc 67
    //   85: ldc_w 337
    //   88: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   91: pop
    //   92: sipush 3010
    //   95: invokestatic 343	android/os/SystemClock:uptimeMillis	()J
    //   98: invokestatic 349	android/util/EventLog:writeEvent	(IJ)I
    //   101: pop
    //   102: ldc_w 351
    //   105: invokestatic 357	dalvik/system/VMRuntime:getRuntime	()Ldalvik/system/VMRuntime;
    //   108: invokevirtual 360	dalvik/system/VMRuntime:vmLibrary	()Ljava/lang/String;
    //   111: invokestatic 331	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   114: invokestatic 365	com/android/internal/os/SamplingProfilerIntegration:isEnabled	()Z
    //   117: ifeq +38 -> 155
    //   120: invokestatic 368	com/android/internal/os/SamplingProfilerIntegration:start	()V
    //   123: aload_0
    //   124: new 370	java/util/Timer
    //   127: dup
    //   128: invokespecial 371	java/util/Timer:<init>	()V
    //   131: putfield 373	com/android/server/SystemServer:mProfilerSnapshotTimer	Ljava/util/Timer;
    //   134: aload_0
    //   135: getfield 373	com/android/server/SystemServer:mProfilerSnapshotTimer	Ljava/util/Timer;
    //   138: new 6	com/android/server/SystemServer$1
    //   141: dup
    //   142: aload_0
    //   143: invokespecial 376	com/android/server/SystemServer$1:<init>	(Lcom/android/server/SystemServer;)V
    //   146: ldc2_w 63
    //   149: ldc2_w 63
    //   152: invokevirtual 380	java/util/Timer:schedule	(Ljava/util/TimerTask;JJ)V
    //   155: invokestatic 357	dalvik/system/VMRuntime:getRuntime	()Ldalvik/system/VMRuntime;
    //   158: invokevirtual 383	dalvik/system/VMRuntime:clearGrowthLimit	()V
    //   161: invokestatic 357	dalvik/system/VMRuntime:getRuntime	()Ldalvik/system/VMRuntime;
    //   164: ldc_w 384
    //   167: invokevirtual 388	dalvik/system/VMRuntime:setTargetHeapUtilization	(F)F
    //   170: pop
    //   171: invokestatic 393	android/os/Build:ensureFingerprintProperty	()V
    //   174: iconst_1
    //   175: invokestatic 399	android/os/Environment:setUserRequired	(Z)V
    //   178: iconst_1
    //   179: invokestatic 404	android/os/BaseBundle:setShouldDefuse	(Z)V
    //   182: iconst_1
    //   183: invokestatic 409	com/android/internal/os/BinderInternal:disableBackgroundScheduling	(Z)V
    //   186: bipush 31
    //   188: invokestatic 412	com/android/internal/os/BinderInternal:setMaxThreads	(I)V
    //   191: bipush -2
    //   193: invokestatic 417	android/os/Process:setThreadPriority	(I)V
    //   196: iconst_0
    //   197: invokestatic 420	android/os/Process:setCanSelfBackground	(Z)V
    //   200: invokestatic 425	android/os/Looper:prepareMainLooper	()V
    //   203: ldc_w 427
    //   206: invokestatic 430	java/lang/System:loadLibrary	(Ljava/lang/String;)V
    //   209: aload_0
    //   210: invokespecial 432	com/android/server/SystemServer:performPendingShutdown	()V
    //   213: aload_0
    //   214: invokespecial 434	com/android/server/SystemServer:createSystemContext	()V
    //   217: aload_0
    //   218: new 436	com/android/server/SystemServiceManager
    //   221: dup
    //   222: aload_0
    //   223: getfield 177	com/android/server/SystemServer:mSystemContext	Landroid/content/Context;
    //   226: invokespecial 439	com/android/server/SystemServiceManager:<init>	(Landroid/content/Context;)V
    //   229: putfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   232: ldc_w 436
    //   235: aload_0
    //   236: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   239: invokestatic 445	com/android/server/LocalServices:addService	(Ljava/lang/Class;Ljava/lang/Object;)V
    //   242: ldc2_w 285
    //   245: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   248: ldc2_w 285
    //   251: ldc_w 451
    //   254: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   257: aload_0
    //   258: invokespecial 454	com/android/server/SystemServer:startBootstrapServices	()V
    //   261: aload_0
    //   262: invokespecial 457	com/android/server/SystemServer:startCoreServices	()V
    //   265: aload_0
    //   266: invokespecial 460	com/android/server/SystemServer:startOtherServices	()V
    //   269: ldc2_w 285
    //   272: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   275: invokestatic 465	android/os/StrictMode:conditionallyEnableDebugLogging	()Z
    //   278: ifeq +12 -> 290
    //   281: ldc 67
    //   283: ldc_w 467
    //   286: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   289: pop
    //   290: invokestatic 470	android/os/Looper:loop	()V
    //   293: new 472	java/lang/RuntimeException
    //   296: dup
    //   297: ldc_w 474
    //   300: invokespecial 475	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   303: athrow
    //   304: astore_1
    //   305: ldc2_w 285
    //   308: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   311: aload_1
    //   312: athrow
    //   313: astore_1
    //   314: ldc_w 477
    //   317: ldc_w 479
    //   320: invokestatic 250	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   323: pop
    //   324: ldc_w 477
    //   327: ldc_w 481
    //   330: aload_1
    //   331: invokestatic 255	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   334: pop
    //   335: aload_1
    //   336: athrow
    //   337: astore_1
    //   338: ldc2_w 285
    //   341: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   344: aload_1
    //   345: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	346	0	this	SystemServer
    //   304	8	1	localObject1	Object
    //   313	23	1	localThrowable	Throwable
    //   337	8	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   0	35	304	finally
    //   35	83	304	finally
    //   83	155	304	finally
    //   155	242	304	finally
    //   248	269	313	java/lang/Throwable
    //   248	269	337	finally
    //   314	337	337	finally
  }
  
  private void startBootstrapServices()
  {
    Installer localInstaller = (Installer)this.mSystemServiceManager.startService(Installer.class);
    this.mActivityManagerService = ((ActivityManagerService.Lifecycle)this.mSystemServiceManager.startService(ActivityManagerService.Lifecycle.class)).getService();
    this.mActivityManagerService.setSystemServiceManager(this.mSystemServiceManager);
    this.mActivityManagerService.setInstaller(localInstaller);
    this.mPowerManagerService = ((PowerManagerService)this.mSystemServiceManager.startService(PowerManagerService.class));
    Trace.traceBegin(524288L, "InitPowerManagement");
    this.mActivityManagerService.initPowerManagement();
    Trace.traceEnd(524288L);
    this.mSystemServiceManager.startService(LightsService.class);
    this.mDisplayManagerService = ((DisplayManagerService)this.mSystemServiceManager.startService(DisplayManagerService.class));
    this.mSystemServiceManager.startBootPhase(100);
    Object localObject2 = SystemProperties.get("vold.decrypt");
    this.mIsAlarmBoot = SystemProperties.getBoolean("ro.alarm_boot", false);
    if ("trigger_restart_min_framework".equals(localObject2))
    {
      Slog.w("SystemServer", "Detected encryption in progress - only parsing core apps");
      this.mOnlyCore = true;
      if (RegionalizationEnvironment.isSupported())
      {
        Slog.i("SystemServer", "Regionalization Service");
        ServiceManager.addService("regionalization", new RegionalizationService());
      }
      traceBeginAndSlog("StartPackageManagerService");
      localObject2 = this.mSystemContext;
      if (this.mFactoryTestMode == 0) {
        break label383;
      }
    }
    label383:
    for (boolean bool = true;; bool = false)
    {
      this.mPackageManagerService = PackageManagerService.main((Context)localObject2, localInstaller, bool, this.mOnlyCore);
      this.mFirstBoot = this.mPackageManagerService.isFirstBoot();
      this.mPackageManager = this.mSystemContext.getPackageManager();
      Trace.traceEnd(524288L);
      if ((!this.mOnlyCore) && (!SystemProperties.getBoolean("config.disable_otadexopt", false))) {
        traceBeginAndSlog("StartOtaDexOptService");
      }
      try
      {
        OtaDexoptService.main(this.mSystemContext, this.mPackageManagerService);
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          reportWtf("starting OtaDexOptService", localThrowable);
          Trace.traceEnd(524288L);
        }
      }
      finally
      {
        Trace.traceEnd(524288L);
      }
      traceBeginAndSlog("StartUserManagerService");
      this.mSystemServiceManager.startService(UserManagerService.LifeCycle.class);
      Trace.traceEnd(524288L);
      AttributeCache.init(this.mSystemContext);
      this.mActivityManagerService.setSystemProcess();
      startSensorService();
      return;
      if ("1".equals(localObject2))
      {
        Slog.w("SystemServer", "Device encrypted - only parsing core apps");
        this.mOnlyCore = true;
        break;
      }
      if (!this.mIsAlarmBoot) {
        break;
      }
      this.mOnlyCore = true;
      break;
    }
  }
  
  private void startCoreServices()
  {
    this.mSystemServiceManager.startService(BatteryService.class);
    this.mSystemServiceManager.startService(UsageStatsService.class);
    this.mActivityManagerService.setUsageStatsManager((UsageStatsManagerInternal)LocalServices.getService(UsageStatsManagerInternal.class));
    this.mWebViewUpdateService = ((WebViewUpdateService)this.mSystemServiceManager.startService(WebViewUpdateService.class));
  }
  
  /* Error */
  private void startOtherServices()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 177	com/android/server/SystemServer:mSystemContext	Landroid/content/Context;
    //   4: astore 73
    //   6: aconst_null
    //   7: astore 21
    //   9: aconst_null
    //   10: astore 31
    //   12: aconst_null
    //   13: astore 69
    //   15: aconst_null
    //   16: astore 66
    //   18: aconst_null
    //   19: astore 67
    //   21: aconst_null
    //   22: astore 30
    //   24: aconst_null
    //   25: astore 55
    //   27: aconst_null
    //   28: astore 62
    //   30: aconst_null
    //   31: astore 68
    //   33: aconst_null
    //   34: astore 53
    //   36: aconst_null
    //   37: astore 64
    //   39: aconst_null
    //   40: astore 27
    //   42: aconst_null
    //   43: astore 48
    //   45: aconst_null
    //   46: astore 65
    //   48: aconst_null
    //   49: astore 26
    //   51: aconst_null
    //   52: astore 56
    //   54: aconst_null
    //   55: astore 63
    //   57: aconst_null
    //   58: astore 24
    //   60: aconst_null
    //   61: astore 23
    //   63: aconst_null
    //   64: astore 38
    //   66: aconst_null
    //   67: astore 42
    //   69: aconst_null
    //   70: astore 43
    //   72: aconst_null
    //   73: astore 41
    //   75: aconst_null
    //   76: astore 28
    //   78: aconst_null
    //   79: astore 22
    //   81: aconst_null
    //   82: astore 29
    //   84: aconst_null
    //   85: astore 61
    //   87: aconst_null
    //   88: astore 39
    //   90: aconst_null
    //   91: astore 50
    //   93: aconst_null
    //   94: astore 52
    //   96: aconst_null
    //   97: astore 59
    //   99: aconst_null
    //   100: astore 40
    //   102: aconst_null
    //   103: astore 49
    //   105: aconst_null
    //   106: astore 51
    //   108: aconst_null
    //   109: astore 70
    //   111: aconst_null
    //   112: astore 32
    //   114: aconst_null
    //   115: astore 25
    //   117: ldc_w 620
    //   120: iconst_0
    //   121: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   124: istore 4
    //   126: ldc_w 622
    //   129: iconst_0
    //   130: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   133: istore 5
    //   135: ldc_w 624
    //   138: iconst_0
    //   139: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   142: istore 6
    //   144: ldc_w 626
    //   147: iconst_0
    //   148: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   151: istore 7
    //   153: ldc_w 628
    //   156: iconst_0
    //   157: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   160: istore 8
    //   162: ldc_w 630
    //   165: iconst_0
    //   166: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   169: istore 9
    //   171: ldc_w 632
    //   174: iconst_0
    //   175: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   178: istore 10
    //   180: ldc_w 634
    //   183: iconst_0
    //   184: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   187: istore 11
    //   189: ldc_w 636
    //   192: iconst_0
    //   193: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   196: istore 12
    //   198: ldc_w 638
    //   201: iconst_0
    //   202: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   205: istore 13
    //   207: ldc_w 640
    //   210: iconst_0
    //   211: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   214: istore 14
    //   216: ldc_w 642
    //   219: iconst_0
    //   220: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   223: istore 15
    //   225: ldc_w 644
    //   228: iconst_0
    //   229: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   232: istore 16
    //   234: ldc_w 646
    //   237: iconst_0
    //   238: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   241: istore 17
    //   243: ldc_w 648
    //   246: invokestatic 313	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   249: ldc 34
    //   251: invokevirtual 221	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   254: istore 18
    //   256: ldc_w 650
    //   259: iconst_0
    //   260: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   263: istore_3
    //   264: ldc 67
    //   266: ldc_w 652
    //   269: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   272: pop
    //   273: invokestatic 658	com/android/server/SystemConfig:getInstance	()Lcom/android/server/SystemConfig;
    //   276: pop
    //   277: ldc_w 660
    //   280: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   283: ldc_w 662
    //   286: new 664	com/android/server/os/SchedulingPolicyService
    //   289: dup
    //   290: invokespecial 665	com/android/server/os/SchedulingPolicyService:<init>	()V
    //   293: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   296: ldc2_w 285
    //   299: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   302: aload_0
    //   303: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   306: ldc_w 667
    //   309: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   312: pop
    //   313: ldc_w 669
    //   316: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   319: new 671	com/android/server/TelephonyRegistry
    //   322: dup
    //   323: aload 73
    //   325: invokespecial 672	com/android/server/TelephonyRegistry:<init>	(Landroid/content/Context;)V
    //   328: astore 19
    //   330: ldc_w 674
    //   333: aload 19
    //   335: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   338: ldc2_w 285
    //   341: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   344: ldc_w 676
    //   347: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   350: aload_0
    //   351: new 678	com/android/server/EntropyMixer
    //   354: dup
    //   355: aload 73
    //   357: invokespecial 679	com/android/server/EntropyMixer:<init>	(Landroid/content/Context;)V
    //   360: putfield 681	com/android/server/SystemServer:mEntropyMixer	Lcom/android/server/EntropyMixer;
    //   363: ldc2_w 285
    //   366: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   369: aload_0
    //   370: aload 73
    //   372: invokevirtual 685	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   375: putfield 687	com/android/server/SystemServer:mContentResolver	Landroid/content/ContentResolver;
    //   378: ldc 67
    //   380: ldc_w 689
    //   383: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   386: pop
    //   387: aload_0
    //   388: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   391: ldc_w 691
    //   394: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   397: pop
    //   398: ldc_w 693
    //   401: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   404: aload_0
    //   405: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   408: ldc 12
    //   410: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   413: pop
    //   414: ldc2_w 285
    //   417: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   420: ldc_w 698
    //   423: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   426: aload_0
    //   427: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   430: ldc 24
    //   432: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   435: pop
    //   436: ldc2_w 285
    //   439: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   442: ldc_w 700
    //   445: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   448: aload_0
    //   449: getfield 133	com/android/server/SystemServer:mActivityManagerService	Lcom/android/server/am/ActivityManagerService;
    //   452: invokevirtual 703	com/android/server/am/ActivityManagerService:installSystemProviders	()V
    //   455: ldc2_w 285
    //   458: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   461: ldc_w 705
    //   464: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   467: new 707	com/android/server/VibratorService
    //   470: dup
    //   471: aload 73
    //   473: invokespecial 708	com/android/server/VibratorService:<init>	(Landroid/content/Context;)V
    //   476: astore 20
    //   478: ldc_w 710
    //   481: aload 20
    //   483: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   486: ldc2_w 285
    //   489: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   492: ldc_w 712
    //   495: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   498: new 714	com/android/server/ConsumerIrService
    //   501: dup
    //   502: aload 73
    //   504: invokespecial 715	com/android/server/ConsumerIrService:<init>	(Landroid/content/Context;)V
    //   507: astore 21
    //   509: ldc_w 717
    //   512: aload 21
    //   514: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   517: ldc2_w 285
    //   520: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   523: ldc_w 719
    //   526: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   529: aload_0
    //   530: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   533: ldc_w 721
    //   536: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   539: pop
    //   540: ldc2_w 285
    //   543: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   546: ldc_w 723
    //   549: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   552: invokestatic 728	com/android/server/Watchdog:getInstance	()Lcom/android/server/Watchdog;
    //   555: aload 73
    //   557: aload_0
    //   558: getfield 133	com/android/server/SystemServer:mActivityManagerService	Lcom/android/server/am/ActivityManagerService;
    //   561: invokevirtual 731	com/android/server/Watchdog:init	(Landroid/content/Context;Lcom/android/server/am/ActivityManagerService;)V
    //   564: ldc2_w 285
    //   567: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   570: ldc_w 733
    //   573: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   576: new 735	com/android/server/input/InputManagerService
    //   579: dup
    //   580: aload 73
    //   582: invokespecial 736	com/android/server/input/InputManagerService:<init>	(Landroid/content/Context;)V
    //   585: astore 21
    //   587: aload 24
    //   589: astore 23
    //   591: ldc2_w 285
    //   594: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   597: aload 24
    //   599: astore 23
    //   601: ldc_w 738
    //   604: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   607: aload 24
    //   609: astore 23
    //   611: aload_0
    //   612: getfield 164	com/android/server/SystemServer:mFactoryTestMode	I
    //   615: iconst_1
    //   616: if_icmpeq +3093 -> 3709
    //   619: iconst_1
    //   620: istore_1
    //   621: aload 24
    //   623: astore 23
    //   625: aload_0
    //   626: getfield 567	com/android/server/SystemServer:mFirstBoot	Z
    //   629: ifeq +3085 -> 3714
    //   632: iconst_0
    //   633: istore_2
    //   634: aload 24
    //   636: astore 23
    //   638: aload 73
    //   640: aload 21
    //   642: iload_1
    //   643: iload_2
    //   644: aload_0
    //   645: getfield 138	com/android/server/SystemServer:mOnlyCore	Z
    //   648: invokestatic 743	com/android/server/wm/WindowManagerService:main	(Landroid/content/Context;Lcom/android/server/input/InputManagerService;ZZZ)Lcom/android/server/wm/WindowManagerService;
    //   651: astore 22
    //   653: aload 22
    //   655: astore 23
    //   657: ldc_w 745
    //   660: aload 22
    //   662: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   665: aload 22
    //   667: astore 23
    //   669: ldc_w 747
    //   672: aload 21
    //   674: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   677: aload 22
    //   679: astore 23
    //   681: ldc2_w 285
    //   684: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   687: aload 22
    //   689: astore 23
    //   691: new 749	com/android/server/display/SDService
    //   694: dup
    //   695: aload 73
    //   697: invokespecial 750	com/android/server/display/SDService:<init>	(Landroid/content/Context;)V
    //   700: astore 24
    //   702: ldc_w 752
    //   705: aload 24
    //   707: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   710: ldc_w 754
    //   713: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   716: aload_0
    //   717: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   720: ldc_w 756
    //   723: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   726: pop
    //   727: ldc2_w 285
    //   730: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   733: aload_0
    //   734: getfield 133	com/android/server/SystemServer:mActivityManagerService	Lcom/android/server/am/ActivityManagerService;
    //   737: aload 22
    //   739: invokevirtual 760	com/android/server/am/ActivityManagerService:setWindowManager	(Lcom/android/server/wm/WindowManagerService;)V
    //   742: aload 21
    //   744: aload 22
    //   746: invokevirtual 764	com/android/server/wm/WindowManagerService:getInputMonitor	()Lcom/android/server/wm/InputMonitor;
    //   749: invokevirtual 768	com/android/server/input/InputManagerService:setWindowManagerCallbacks	(Lcom/android/server/input/InputManagerService$WindowManagerCallbacks;)V
    //   752: aload 21
    //   754: invokevirtual 769	com/android/server/input/InputManagerService:start	()V
    //   757: aload_0
    //   758: getfield 518	com/android/server/SystemServer:mDisplayManagerService	Lcom/android/server/display/DisplayManagerService;
    //   761: invokevirtual 772	com/android/server/display/DisplayManagerService:windowManagerAndInputReady	()V
    //   764: iload 18
    //   766: ifeq +2953 -> 3719
    //   769: ldc 67
    //   771: ldc_w 774
    //   774: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   777: pop
    //   778: ldc_w 776
    //   781: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   784: aload_0
    //   785: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   788: ldc_w 778
    //   791: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   794: pop
    //   795: ldc2_w 285
    //   798: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   801: ldc_w 780
    //   804: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   807: aload_0
    //   808: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   811: ldc_w 782
    //   814: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   817: pop
    //   818: ldc2_w 285
    //   821: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   824: ldc_w 784
    //   827: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   830: aload_0
    //   831: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   834: ldc_w 786
    //   837: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   840: pop
    //   841: ldc2_w 285
    //   844: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   847: ldc 67
    //   849: ldc_w 788
    //   852: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   855: pop
    //   856: ldc_w 790
    //   859: aload 73
    //   861: invokestatic 795	com/oneplus/longshot/LongScreenshotManagerService:getInstance	(Landroid/content/Context;)Lcom/oneplus/longshot/LongScreenshotManagerService;
    //   864: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   867: aload 24
    //   869: astore 34
    //   871: aload 22
    //   873: astore 36
    //   875: aload 20
    //   877: astore 37
    //   879: aload 19
    //   881: astore 35
    //   883: aload 21
    //   885: astore 33
    //   887: aconst_null
    //   888: astore 22
    //   890: aconst_null
    //   891: astore 57
    //   893: aconst_null
    //   894: astore 58
    //   896: aconst_null
    //   897: astore 23
    //   899: aconst_null
    //   900: astore 54
    //   902: aconst_null
    //   903: astore 60
    //   905: aconst_null
    //   906: astore 72
    //   908: aconst_null
    //   909: astore 21
    //   911: aconst_null
    //   912: astore 71
    //   914: aconst_null
    //   915: astore 19
    //   917: aconst_null
    //   918: astore 46
    //   920: aconst_null
    //   921: astore 47
    //   923: aconst_null
    //   924: astore 24
    //   926: aconst_null
    //   927: astore 44
    //   929: aconst_null
    //   930: astore 45
    //   932: aload_0
    //   933: getfield 164	com/android/server/SystemServer:mFactoryTestMode	I
    //   936: iconst_1
    //   937: if_icmpeq +41 -> 978
    //   940: aload_0
    //   941: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   944: ldc_w 797
    //   947: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   950: pop
    //   951: ldc_w 799
    //   954: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   957: ldc_w 801
    //   960: new 803	com/android/server/accessibility/AccessibilityManagerService
    //   963: dup
    //   964: aload 73
    //   966: invokespecial 804	com/android/server/accessibility/AccessibilityManagerService:<init>	(Landroid/content/Context;)V
    //   969: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   972: ldc2_w 285
    //   975: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   978: aload 36
    //   980: invokevirtual 807	com/android/server/wm/WindowManagerService:displayReady	()V
    //   983: aload 31
    //   985: astore 29
    //   987: aload_0
    //   988: getfield 164	com/android/server/SystemServer:mFactoryTestMode	I
    //   991: iconst_1
    //   992: if_icmpeq +31 -> 1023
    //   995: aload 31
    //   997: astore 29
    //   999: iload 4
    //   1001: ifne +22 -> 1023
    //   1004: ldc_w 809
    //   1007: ldc_w 811
    //   1010: invokestatic 313	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   1013: invokevirtual 221	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1016: ifeq +2875 -> 3891
    //   1019: aload 31
    //   1021: astore 29
    //   1023: aload_0
    //   1024: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   1027: ldc_w 813
    //   1030: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   1033: pop
    //   1034: aload_0
    //   1035: getfield 138	com/android/server/SystemServer:mOnlyCore	Z
    //   1038: ifne +25 -> 1063
    //   1041: ldc2_w 285
    //   1044: ldc_w 815
    //   1047: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   1050: aload_0
    //   1051: getfield 562	com/android/server/SystemServer:mPackageManagerService	Lcom/android/server/pm/PackageManagerService;
    //   1054: invokevirtual 818	com/android/server/pm/PackageManagerService:updatePackagesIfNeeded	()V
    //   1057: ldc2_w 285
    //   1060: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   1063: ldc2_w 285
    //   1066: ldc_w 820
    //   1069: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   1072: aload_0
    //   1073: getfield 562	com/android/server/SystemServer:mPackageManagerService	Lcom/android/server/pm/PackageManagerService;
    //   1076: invokevirtual 823	com/android/server/pm/PackageManagerService:performFstrimIfNeeded	()V
    //   1079: ldc2_w 285
    //   1082: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   1085: ldc_w 825
    //   1088: ldc -60
    //   1090: invokestatic 202	android/os/SystemProperties:get	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   1093: astore 20
    //   1095: ldc_w 827
    //   1098: ldc -60
    //   1100: invokestatic 202	android/os/SystemProperties:get	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   1103: astore 25
    //   1105: aload_0
    //   1106: getfield 562	com/android/server/SystemServer:mPackageManagerService	Lcom/android/server/pm/PackageManagerService;
    //   1109: invokevirtual 830	com/android/server/pm/PackageManagerService:isUpgrade	()Z
    //   1112: ifne +13 -> 1125
    //   1115: aload_0
    //   1116: getfield 562	com/android/server/SystemServer:mPackageManagerService	Lcom/android/server/pm/PackageManagerService;
    //   1119: invokevirtual 565	com/android/server/pm/PackageManagerService:isFirstBoot	()Z
    //   1122: ifeq +13 -> 1135
    //   1125: ldc -60
    //   1127: aload 20
    //   1129: invokevirtual 221	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1132: ifeq +2829 -> 3961
    //   1135: aload 19
    //   1137: astore 31
    //   1139: aload 28
    //   1141: astore 19
    //   1143: aload 27
    //   1145: astore 20
    //   1147: aload 23
    //   1149: astore 27
    //   1151: aload 22
    //   1153: astore 28
    //   1155: aload 70
    //   1157: astore 22
    //   1159: aload 32
    //   1161: astore 23
    //   1163: aload 24
    //   1165: astore 32
    //   1167: aload 69
    //   1169: astore 24
    //   1171: aload 68
    //   1173: astore 25
    //   1175: aload_0
    //   1176: getfield 164	com/android/server/SystemServer:mFactoryTestMode	I
    //   1179: iconst_1
    //   1180: if_icmpeq +2003 -> 3183
    //   1183: aload 72
    //   1185: astore 21
    //   1187: iload 8
    //   1189: ifne +82 -> 1271
    //   1192: ldc_w 832
    //   1195: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   1198: aload_0
    //   1199: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   1202: ldc 46
    //   1204: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   1207: pop
    //   1208: ldc_w 834
    //   1211: invokestatic 837	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   1214: invokestatic 843	com/android/internal/widget/ILockSettings$Stub:asInterface	(Landroid/os/IBinder;)Lcom/android/internal/widget/ILockSettings;
    //   1217: astore 21
    //   1219: ldc2_w 285
    //   1222: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   1225: ldc 55
    //   1227: invokestatic 313	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   1230: ldc -60
    //   1232: invokevirtual 221	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1235: ifne +14 -> 1249
    //   1238: aload_0
    //   1239: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   1242: ldc_w 845
    //   1245: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   1248: pop
    //   1249: aload_0
    //   1250: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   1253: ldc_w 847
    //   1256: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   1259: pop
    //   1260: aload_0
    //   1261: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   1264: ldc_w 849
    //   1267: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   1270: pop
    //   1271: iload 7
    //   1273: ifne +36 -> 1309
    //   1276: ldc_w 851
    //   1279: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   1282: new 853	com/android/server/statusbar/StatusBarManagerService
    //   1285: dup
    //   1286: aload 73
    //   1288: aload 36
    //   1290: invokespecial 856	com/android/server/statusbar/StatusBarManagerService:<init>	(Landroid/content/Context;Lcom/android/server/wm/WindowManagerService;)V
    //   1293: astore 19
    //   1295: ldc_w 858
    //   1298: aload 19
    //   1300: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   1303: ldc2_w 285
    //   1306: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   1309: iload 8
    //   1311: ifne +30 -> 1341
    //   1314: ldc_w 860
    //   1317: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   1320: ldc_w 862
    //   1323: new 864	com/android/server/clipboard/ClipboardService
    //   1326: dup
    //   1327: aload 73
    //   1329: invokespecial 865	com/android/server/clipboard/ClipboardService:<init>	(Landroid/content/Context;)V
    //   1332: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   1335: ldc2_w 285
    //   1338: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   1341: aload 67
    //   1343: astore 22
    //   1345: iload 9
    //   1347: ifne +46 -> 1393
    //   1350: ldc_w 867
    //   1353: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   1356: aload 66
    //   1358: astore 19
    //   1360: aload 73
    //   1362: invokestatic 873	com/android/server/NetworkManagementService:create	(Landroid/content/Context;)Lcom/android/server/NetworkManagementService;
    //   1365: astore 20
    //   1367: aload 20
    //   1369: astore 19
    //   1371: ldc_w 875
    //   1374: aload 20
    //   1376: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   1379: aload 20
    //   1381: astore 19
    //   1383: ldc2_w 285
    //   1386: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   1389: aload 19
    //   1391: astore 22
    //   1393: iload 8
    //   1395: ifne +8 -> 1403
    //   1398: iload 16
    //   1400: ifeq +2640 -> 4040
    //   1403: aload 65
    //   1405: astore 19
    //   1407: aload 64
    //   1409: astore 20
    //   1411: aload 63
    //   1413: astore 23
    //   1415: aload 62
    //   1417: astore 24
    //   1419: aload 61
    //   1421: astore 25
    //   1423: aload 59
    //   1425: astore 26
    //   1427: iload 9
    //   1429: ifne +546 -> 1975
    //   1432: ldc_w 877
    //   1435: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   1438: new 879	com/android/server/NetworkScoreService
    //   1441: dup
    //   1442: aload 73
    //   1444: invokespecial 880	com/android/server/NetworkScoreService:<init>	(Landroid/content/Context;)V
    //   1447: astore 19
    //   1449: ldc_w 882
    //   1452: aload 19
    //   1454: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   1457: aload 19
    //   1459: astore 23
    //   1461: ldc2_w 285
    //   1464: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   1467: ldc_w 884
    //   1470: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   1473: aload 55
    //   1475: astore 24
    //   1477: aload 73
    //   1479: aload 22
    //   1481: invokestatic 889	com/android/server/net/NetworkStatsService:create	(Landroid/content/Context;Landroid/os/INetworkManagementService;)Lcom/android/server/net/NetworkStatsService;
    //   1484: astore 19
    //   1486: aload 19
    //   1488: astore 24
    //   1490: ldc_w 891
    //   1493: aload 19
    //   1495: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   1498: aload 19
    //   1500: astore 24
    //   1502: ldc2_w 285
    //   1505: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   1508: ldc_w 893
    //   1511: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   1514: new 895	com/android/server/net/NetworkPolicyManagerService
    //   1517: dup
    //   1518: aload 73
    //   1520: aload_0
    //   1521: getfield 133	com/android/server/SystemServer:mActivityManagerService	Lcom/android/server/am/ActivityManagerService;
    //   1524: aload 24
    //   1526: aload 22
    //   1528: invokespecial 898	com/android/server/net/NetworkPolicyManagerService:<init>	(Landroid/content/Context;Landroid/app/IActivityManager;Landroid/net/INetworkStatsService;Landroid/os/INetworkManagementService;)V
    //   1531: astore 19
    //   1533: ldc_w 900
    //   1536: aload 19
    //   1538: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   1541: aload 19
    //   1543: astore 27
    //   1545: ldc2_w 285
    //   1548: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   1551: aload 73
    //   1553: invokevirtual 571	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   1556: ldc_w 902
    //   1559: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   1562: ifeq +2542 -> 4104
    //   1565: aload_0
    //   1566: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   1569: ldc 94
    //   1571: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   1574: pop
    //   1575: aload_0
    //   1576: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   1579: ldc 97
    //   1581: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   1584: pop
    //   1585: aload_0
    //   1586: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   1589: ldc 100
    //   1591: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   1594: pop
    //   1595: aload_0
    //   1596: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   1599: ldc_w 909
    //   1602: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   1605: pop
    //   1606: iload 11
    //   1608: ifne +14 -> 1622
    //   1611: aload_0
    //   1612: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   1615: ldc_w 911
    //   1618: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   1621: pop
    //   1622: aload 52
    //   1624: astore 19
    //   1626: aload 51
    //   1628: astore 20
    //   1630: iload_3
    //   1631: ifeq +214 -> 1845
    //   1634: aload 50
    //   1636: astore 19
    //   1638: aload 49
    //   1640: astore 20
    //   1642: ldc 67
    //   1644: ldc_w 913
    //   1647: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   1650: pop
    //   1651: aload 50
    //   1653: astore 19
    //   1655: aload 49
    //   1657: astore 20
    //   1659: new 915	dalvik/system/PathClassLoader
    //   1662: dup
    //   1663: ldc_w 917
    //   1666: ldc_w 919
    //   1669: aload_0
    //   1670: invokevirtual 923	com/android/server/SystemServer:getClass	()Ljava/lang/Class;
    //   1673: invokevirtual 929	java/lang/Class:getClassLoader	()Ljava/lang/ClassLoader;
    //   1676: invokespecial 932	dalvik/system/PathClassLoader:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V
    //   1679: astore 26
    //   1681: aload 50
    //   1683: astore 19
    //   1685: aload 49
    //   1687: astore 20
    //   1689: aload 26
    //   1691: ldc_w 934
    //   1694: invokevirtual 938	dalvik/system/PathClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   1697: iconst_1
    //   1698: anewarray 925	java/lang/Class
    //   1701: dup
    //   1702: iconst_0
    //   1703: ldc -77
    //   1705: aastore
    //   1706: invokevirtual 942	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   1709: iconst_1
    //   1710: anewarray 4	java/lang/Object
    //   1713: dup
    //   1714: iconst_0
    //   1715: aload 73
    //   1717: aastore
    //   1718: invokevirtual 948	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   1721: astore 25
    //   1723: aload 25
    //   1725: astore 19
    //   1727: aload 49
    //   1729: astore 20
    //   1731: ldc 67
    //   1733: ldc_w 950
    //   1736: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   1739: pop
    //   1740: aload 25
    //   1742: astore 19
    //   1744: aload 49
    //   1746: astore 20
    //   1748: ldc_w 952
    //   1751: aload 25
    //   1753: checkcast 954	android/os/IBinder
    //   1756: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   1759: aload 25
    //   1761: astore 19
    //   1763: aload 49
    //   1765: astore 20
    //   1767: aload 26
    //   1769: ldc_w 956
    //   1772: invokevirtual 938	dalvik/system/PathClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   1775: iconst_1
    //   1776: anewarray 925	java/lang/Class
    //   1779: dup
    //   1780: iconst_0
    //   1781: ldc -77
    //   1783: aastore
    //   1784: invokevirtual 942	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   1787: iconst_1
    //   1788: anewarray 4	java/lang/Object
    //   1791: dup
    //   1792: iconst_0
    //   1793: aload 73
    //   1795: aastore
    //   1796: invokevirtual 948	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   1799: astore 26
    //   1801: aload 25
    //   1803: astore 19
    //   1805: aload 26
    //   1807: astore 20
    //   1809: ldc 67
    //   1811: ldc_w 958
    //   1814: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   1817: pop
    //   1818: aload 25
    //   1820: astore 19
    //   1822: aload 26
    //   1824: astore 20
    //   1826: ldc_w 960
    //   1829: aload 26
    //   1831: checkcast 954	android/os/IBinder
    //   1834: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   1837: aload 26
    //   1839: astore 20
    //   1841: aload 25
    //   1843: astore 19
    //   1845: aload_0
    //   1846: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   1849: ldc_w 962
    //   1852: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   1855: ifne +16 -> 1871
    //   1858: aload_0
    //   1859: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   1862: ldc_w 964
    //   1865: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   1868: ifeq +13 -> 1881
    //   1871: aload_0
    //   1872: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   1875: ldc 40
    //   1877: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   1880: pop
    //   1881: ldc_w 966
    //   1884: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   1887: new 968	com/android/server/ConnectivityService
    //   1890: dup
    //   1891: aload 73
    //   1893: aload 22
    //   1895: aload 24
    //   1897: aload 27
    //   1899: invokespecial 971	com/android/server/ConnectivityService:<init>	(Landroid/content/Context;Landroid/os/INetworkManagementService;Landroid/net/INetworkStatsService;Landroid/net/INetworkPolicyManager;)V
    //   1902: astore 25
    //   1904: ldc_w 973
    //   1907: aload 25
    //   1909: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   1912: aload 24
    //   1914: aload 25
    //   1916: invokevirtual 977	com/android/server/net/NetworkStatsService:bindConnectivityManager	(Landroid/net/IConnectivityManager;)V
    //   1919: aload 27
    //   1921: aload 25
    //   1923: invokevirtual 978	com/android/server/net/NetworkPolicyManagerService:bindConnectivityManager	(Landroid/net/IConnectivityManager;)V
    //   1926: aload 25
    //   1928: astore 28
    //   1930: ldc2_w 285
    //   1933: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   1936: ldc_w 980
    //   1939: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   1942: ldc_w 982
    //   1945: aload 73
    //   1947: invokestatic 987	com/android/server/NsdService:create	(Landroid/content/Context;)Lcom/android/server/NsdService;
    //   1950: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   1953: ldc2_w 285
    //   1956: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   1959: aload 20
    //   1961: astore 26
    //   1963: aload 19
    //   1965: astore 25
    //   1967: aload 27
    //   1969: astore 20
    //   1971: aload 28
    //   1973: astore 19
    //   1975: iload 8
    //   1977: ifne +30 -> 2007
    //   1980: ldc_w 989
    //   1983: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   1986: ldc_w 991
    //   1989: new 993	com/android/server/UpdateLockService
    //   1992: dup
    //   1993: aload 73
    //   1995: invokespecial 994	com/android/server/UpdateLockService:<init>	(Landroid/content/Context;)V
    //   1998: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2001: ldc2_w 285
    //   2004: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2007: iload 8
    //   2009: ifne +14 -> 2023
    //   2012: aload_0
    //   2013: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2016: ldc_w 996
    //   2019: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2022: pop
    //   2023: aload 29
    //   2025: ifnull +10 -> 2035
    //   2028: aload_0
    //   2029: getfield 138	com/android/server/SystemServer:mOnlyCore	Z
    //   2032: ifeq +2144 -> 4176
    //   2035: aload_0
    //   2036: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2039: ldc_w 998
    //   2042: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2045: pop
    //   2046: aload 20
    //   2048: ldc_w 1000
    //   2051: invokestatic 837	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   2054: invokestatic 1005	android/app/INotificationManager$Stub:asInterface	(Landroid/os/IBinder;)Landroid/app/INotificationManager;
    //   2057: invokevirtual 1009	com/android/server/net/NetworkPolicyManagerService:bindNotificationManager	(Landroid/app/INotificationManager;)V
    //   2060: aload_0
    //   2061: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2064: ldc_w 1011
    //   2067: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2070: pop
    //   2071: aload 60
    //   2073: astore 27
    //   2075: aload 58
    //   2077: astore 28
    //   2079: iload 6
    //   2081: ifne +69 -> 2150
    //   2084: ldc_w 1013
    //   2087: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   2090: new 1015	com/android/server/LocationManagerService
    //   2093: dup
    //   2094: aload 73
    //   2096: invokespecial 1016	com/android/server/LocationManagerService:<init>	(Landroid/content/Context;)V
    //   2099: astore 27
    //   2101: ldc_w 1018
    //   2104: aload 27
    //   2106: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2109: aload 27
    //   2111: astore 28
    //   2113: ldc2_w 285
    //   2116: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2119: ldc_w 1020
    //   2122: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   2125: new 1022	com/android/server/CountryDetectorService
    //   2128: dup
    //   2129: aload 73
    //   2131: invokespecial 1023	com/android/server/CountryDetectorService:<init>	(Landroid/content/Context;)V
    //   2134: astore 27
    //   2136: ldc_w 1025
    //   2139: aload 27
    //   2141: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2144: ldc2_w 285
    //   2147: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2150: iload 8
    //   2152: ifne +8 -> 2160
    //   2155: iload 14
    //   2157: ifeq +2085 -> 4242
    //   2160: aload_0
    //   2161: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2164: ldc_w 1027
    //   2167: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2170: pop
    //   2171: iload 8
    //   2173: ifne +24 -> 2197
    //   2176: aload 73
    //   2178: invokevirtual 1031	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   2181: ldc_w 1032
    //   2184: invokevirtual 1037	android/content/res/Resources:getBoolean	(I)Z
    //   2187: ifeq +10 -> 2197
    //   2190: aload_0
    //   2191: getfield 531	com/android/server/SystemServer:mIsAlarmBoot	Z
    //   2194: ifeq +2087 -> 4281
    //   2197: ldc_w 1039
    //   2200: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   2203: aload_0
    //   2204: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2207: ldc_w 1041
    //   2210: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2213: pop
    //   2214: ldc2_w 285
    //   2217: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2220: iload 8
    //   2222: ifne +28 -> 2250
    //   2225: aload_0
    //   2226: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2229: ldc_w 1043
    //   2232: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2235: pop
    //   2236: aload 73
    //   2238: invokevirtual 571	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   2241: ldc_w 1045
    //   2244: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   2247: ifeq +3 -> 2250
    //   2250: ldc_w 1047
    //   2253: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   2256: aload 33
    //   2258: new 1049	com/android/server/WiredAccessoryManager
    //   2261: dup
    //   2262: aload 73
    //   2264: aload 33
    //   2266: invokespecial 1052	com/android/server/WiredAccessoryManager:<init>	(Landroid/content/Context;Lcom/android/server/input/InputManagerService;)V
    //   2269: invokevirtual 1056	com/android/server/input/InputManagerService:setWiredAccessoryCallbacks	(Lcom/android/server/input/InputManagerService$WiredAccessoryCallbacks;)V
    //   2272: ldc2_w 285
    //   2275: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2278: iload 8
    //   2280: ifne +147 -> 2427
    //   2283: aload_0
    //   2284: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   2287: ldc_w 1058
    //   2290: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   2293: ifeq +13 -> 2306
    //   2296: aload_0
    //   2297: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2300: ldc 49
    //   2302: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   2305: pop
    //   2306: aload_0
    //   2307: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   2310: ldc_w 964
    //   2313: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   2316: ifne +16 -> 2332
    //   2319: aload_0
    //   2320: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   2323: ldc_w 1060
    //   2326: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   2329: ifeq +28 -> 2357
    //   2332: ldc2_w 285
    //   2335: ldc_w 1062
    //   2338: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   2341: aload_0
    //   2342: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2345: ldc 76
    //   2347: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   2350: pop
    //   2351: ldc2_w 285
    //   2354: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2357: iload 13
    //   2359: ifne +34 -> 2393
    //   2362: ldc_w 1064
    //   2365: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   2368: new 1066	com/android/server/SerialService
    //   2371: dup
    //   2372: aload 73
    //   2374: invokespecial 1067	com/android/server/SerialService:<init>	(Landroid/content/Context;)V
    //   2377: astore 29
    //   2379: ldc_w 1069
    //   2382: aload 29
    //   2384: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2387: ldc2_w 285
    //   2390: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2393: ldc2_w 285
    //   2396: ldc_w 1071
    //   2399: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   2402: new 1073	com/android/server/HardwarePropertiesManagerService
    //   2405: dup
    //   2406: aload 73
    //   2408: invokespecial 1074	com/android/server/HardwarePropertiesManagerService:<init>	(Landroid/content/Context;)V
    //   2411: astore 29
    //   2413: ldc_w 1076
    //   2416: aload 29
    //   2418: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2421: ldc2_w 285
    //   2424: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2427: aload_0
    //   2428: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2431: ldc_w 1078
    //   2434: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2437: pop
    //   2438: aload 73
    //   2440: invokestatic 1084	com/android/internal/app/NightDisplayController:isAvailable	(Landroid/content/Context;)Z
    //   2443: ifeq +14 -> 2457
    //   2446: aload_0
    //   2447: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2450: ldc_w 1086
    //   2453: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2456: pop
    //   2457: aload_0
    //   2458: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2461: ldc_w 1088
    //   2464: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2467: pop
    //   2468: aload_0
    //   2469: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2472: ldc_w 1090
    //   2475: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2478: pop
    //   2479: iload 8
    //   2481: ifne +139 -> 2620
    //   2484: aload_0
    //   2485: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   2488: ldc_w 1092
    //   2491: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   2494: ifeq +13 -> 2507
    //   2497: aload_0
    //   2498: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2501: ldc 18
    //   2503: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   2506: pop
    //   2507: aload_0
    //   2508: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   2511: ldc_w 1094
    //   2514: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   2517: ifne +17 -> 2534
    //   2520: aload 73
    //   2522: invokevirtual 1031	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   2525: ldc_w 1095
    //   2528: invokevirtual 1037	android/content/res/Resources:getBoolean	(I)Z
    //   2531: ifeq +13 -> 2544
    //   2534: aload_0
    //   2535: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2538: ldc 15
    //   2540: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   2543: pop
    //   2544: aload_0
    //   2545: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   2548: ldc_w 1097
    //   2551: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   2554: ifeq +13 -> 2567
    //   2557: aload_0
    //   2558: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2561: ldc 79
    //   2563: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   2566: pop
    //   2567: aload 73
    //   2569: invokevirtual 1031	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   2572: invokestatic 1103	com/android/server/GestureLauncherService:isGestureLauncherEnabled	(Landroid/content/res/Resources;)Z
    //   2575: ifeq +23 -> 2598
    //   2578: ldc 67
    //   2580: ldc_w 1105
    //   2583: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   2586: pop
    //   2587: aload_0
    //   2588: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2591: ldc_w 1099
    //   2594: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2597: pop
    //   2598: aload_0
    //   2599: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2602: ldc_w 1107
    //   2605: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2608: pop
    //   2609: aload_0
    //   2610: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2613: ldc_w 1109
    //   2616: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2619: pop
    //   2620: ldc_w 1111
    //   2623: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   2626: ldc_w 1113
    //   2629: new 1115	com/android/server/DiskStatsService
    //   2632: dup
    //   2633: aload 73
    //   2635: invokespecial 1116	com/android/server/DiskStatsService:<init>	(Landroid/content/Context;)V
    //   2638: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2641: ldc2_w 285
    //   2644: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2647: iload 17
    //   2649: ifne +30 -> 2679
    //   2652: ldc_w 1118
    //   2655: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   2658: ldc_w 1120
    //   2661: new 1122	com/android/server/SamplingProfilerService
    //   2664: dup
    //   2665: aload 73
    //   2667: invokespecial 1123	com/android/server/SamplingProfilerService:<init>	(Landroid/content/Context;)V
    //   2670: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2673: ldc2_w 285
    //   2676: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2679: aload 43
    //   2681: astore 30
    //   2683: iload 9
    //   2685: ifne +12 -> 2697
    //   2688: iload 10
    //   2690: ifeq +1690 -> 4380
    //   2693: aload 43
    //   2695: astore 30
    //   2697: ldc_w 1125
    //   2700: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   2703: new 1127	com/android/server/CommonTimeManagementService
    //   2706: dup
    //   2707: aload 73
    //   2709: invokespecial 1128	com/android/server/CommonTimeManagementService:<init>	(Landroid/content/Context;)V
    //   2712: astore 29
    //   2714: ldc_w 1130
    //   2717: aload 29
    //   2719: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2722: ldc2_w 285
    //   2725: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2728: iload 9
    //   2730: ifne +25 -> 2755
    //   2733: ldc_w 1132
    //   2736: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   2739: new 1134	com/android/server/CertBlacklister
    //   2742: dup
    //   2743: aload 73
    //   2745: invokespecial 1135	com/android/server/CertBlacklister:<init>	(Landroid/content/Context;)V
    //   2748: pop
    //   2749: ldc2_w 285
    //   2752: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2755: iload 9
    //   2757: ifne +8 -> 2765
    //   2760: iload 8
    //   2762: ifeq +1706 -> 4468
    //   2765: iload 8
    //   2767: ifne +14 -> 2781
    //   2770: aload_0
    //   2771: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2774: ldc_w 1137
    //   2777: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2780: pop
    //   2781: aload 47
    //   2783: astore 31
    //   2785: iload 8
    //   2787: ifne +34 -> 2821
    //   2790: ldc_w 1139
    //   2793: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   2796: new 1141	com/android/server/AssetAtlasService
    //   2799: dup
    //   2800: aload 73
    //   2802: invokespecial 1142	com/android/server/AssetAtlasService:<init>	(Landroid/content/Context;)V
    //   2805: astore 31
    //   2807: ldc_w 1144
    //   2810: aload 31
    //   2812: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2815: ldc2_w 285
    //   2818: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   2821: iload 8
    //   2823: ifne +18 -> 2841
    //   2826: ldc_w 1146
    //   2829: new 1148	com/android/server/GraphicsStatsService
    //   2832: dup
    //   2833: aload 73
    //   2835: invokespecial 1149	com/android/server/GraphicsStatsService:<init>	(Landroid/content/Context;)V
    //   2838: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2841: new 1151	com/android/server/OemExService
    //   2844: dup
    //   2845: aload 73
    //   2847: invokespecial 1152	com/android/server/OemExService:<init>	(Landroid/content/Context;)V
    //   2850: astore 41
    //   2852: ldc_w 1154
    //   2855: aload 41
    //   2857: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2860: new 1156	com/android/server/OnePlusNfcService
    //   2863: dup
    //   2864: aload 73
    //   2866: invokespecial 1157	com/android/server/OnePlusNfcService:<init>	(Landroid/content/Context;)V
    //   2869: astore 42
    //   2871: ldc_w 1159
    //   2874: aload 42
    //   2876: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   2879: aload_0
    //   2880: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   2883: ldc_w 1161
    //   2886: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   2889: ifeq +13 -> 2902
    //   2892: aload_0
    //   2893: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2896: ldc 58
    //   2898: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   2901: pop
    //   2902: aload_0
    //   2903: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2906: ldc_w 1163
    //   2909: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2912: pop
    //   2913: aload_0
    //   2914: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2917: ldc_w 1165
    //   2920: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2923: pop
    //   2924: aload_0
    //   2925: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   2928: ldc_w 1167
    //   2931: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   2934: ifeq +14 -> 2948
    //   2937: aload_0
    //   2938: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2941: ldc_w 1169
    //   2944: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2947: pop
    //   2948: aload_0
    //   2949: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   2952: ldc_w 1171
    //   2955: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   2958: ifeq +14 -> 2972
    //   2961: aload_0
    //   2962: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2965: ldc_w 1173
    //   2968: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2971: pop
    //   2972: aload_0
    //   2973: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   2976: ldc_w 1175
    //   2979: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   2982: ifeq +14 -> 2996
    //   2985: aload_0
    //   2986: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   2989: ldc_w 1177
    //   2992: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   2995: pop
    //   2996: aload_0
    //   2997: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   3000: ldc_w 1179
    //   3003: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   3006: ifeq +14 -> 3020
    //   3009: aload_0
    //   3010: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3013: ldc_w 1181
    //   3016: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   3019: pop
    //   3020: aload 45
    //   3022: astore 32
    //   3024: iload 8
    //   3026: ifne +91 -> 3117
    //   3029: ldc_w 1183
    //   3032: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   3035: new 1185	com/android/server/media/MediaRouterService
    //   3038: dup
    //   3039: aload 73
    //   3041: invokespecial 1186	com/android/server/media/MediaRouterService:<init>	(Landroid/content/Context;)V
    //   3044: astore 32
    //   3046: ldc_w 1188
    //   3049: aload 32
    //   3051: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   3054: ldc2_w 285
    //   3057: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   3060: iload 15
    //   3062: ifne +14 -> 3076
    //   3065: aload_0
    //   3066: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3069: ldc_w 1190
    //   3072: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   3075: pop
    //   3076: aload_0
    //   3077: getfield 573	com/android/server/SystemServer:mPackageManager	Landroid/content/pm/PackageManager;
    //   3080: ldc_w 1192
    //   3083: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   3086: ifeq +14 -> 3100
    //   3089: aload_0
    //   3090: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3093: ldc_w 1194
    //   3096: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   3099: pop
    //   3100: ldc_w 1196
    //   3103: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   3106: aload 73
    //   3108: invokestatic 1200	com/android/server/pm/BackgroundDexOptService:schedule	(Landroid/content/Context;)V
    //   3111: ldc2_w 285
    //   3114: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   3117: aload_0
    //   3118: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3121: ldc_w 1202
    //   3124: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   3127: pop
    //   3128: aload_0
    //   3129: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3132: ldc_w 1204
    //   3135: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   3138: pop
    //   3139: aload 26
    //   3141: astore 40
    //   3143: aload 25
    //   3145: astore 39
    //   3147: aload 30
    //   3149: astore 38
    //   3151: aload 24
    //   3153: astore 30
    //   3155: aload 23
    //   3157: astore 26
    //   3159: aload 20
    //   3161: astore 25
    //   3163: aload 22
    //   3165: astore 24
    //   3167: aload 42
    //   3169: astore 23
    //   3171: aload 41
    //   3173: astore 22
    //   3175: aload 19
    //   3177: astore 20
    //   3179: aload 29
    //   3181: astore 19
    //   3183: iload 8
    //   3185: ifne +8 -> 3193
    //   3188: iload 12
    //   3190: ifeq +1342 -> 4532
    //   3193: aload 73
    //   3195: invokevirtual 571	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   3198: ldc_w 1045
    //   3201: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   3204: ifeq +3 -> 3207
    //   3207: aload 36
    //   3209: invokevirtual 1207	com/android/server/wm/WindowManagerService:detectSafeMode	()Z
    //   3212: istore_1
    //   3213: iload_1
    //   3214: ifeq +1332 -> 4546
    //   3217: aload_0
    //   3218: getfield 133	com/android/server/SystemServer:mActivityManagerService	Lcom/android/server/am/ActivityManagerService;
    //   3221: invokevirtual 1210	com/android/server/am/ActivityManagerService:enterSafeMode	()V
    //   3224: invokestatic 357	dalvik/system/VMRuntime:getRuntime	()Ldalvik/system/VMRuntime;
    //   3227: invokevirtual 1213	dalvik/system/VMRuntime:disableJitCompilation	()V
    //   3230: aload_0
    //   3231: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3234: ldc_w 1215
    //   3237: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   3240: checkcast 1215	com/android/server/MmsServiceBroker
    //   3243: astore 29
    //   3245: aload_0
    //   3246: getfield 687	com/android/server/SystemServer:mContentResolver	Landroid/content/ContentResolver;
    //   3249: ldc_w 1217
    //   3252: iconst_0
    //   3253: invokestatic 1223	android/provider/Settings$Global:getInt	(Landroid/content/ContentResolver;Ljava/lang/String;I)I
    //   3256: ifeq +13 -> 3269
    //   3259: aload_0
    //   3260: getfield 177	com/android/server/SystemServer:mSystemContext	Landroid/content/Context;
    //   3263: invokestatic 1228	android/os/UserManager:isDeviceInDemoMode	(Landroid/content/Context;)Z
    //   3266: ifeq +14 -> 3280
    //   3269: aload_0
    //   3270: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3273: ldc_w 1230
    //   3276: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   3279: pop
    //   3280: ldc2_w 285
    //   3283: ldc_w 1232
    //   3286: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   3289: aload 37
    //   3291: invokevirtual 1235	com/android/server/VibratorService:systemReady	()V
    //   3294: ldc2_w 285
    //   3297: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   3300: ldc2_w 285
    //   3303: ldc_w 1237
    //   3306: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   3309: aload 21
    //   3311: ifnull +10 -> 3321
    //   3314: aload 21
    //   3316: invokeinterface 1240 1 0
    //   3321: ldc2_w 285
    //   3324: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   3327: aload_0
    //   3328: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3331: sipush 480
    //   3334: invokevirtual 521	com/android/server/SystemServiceManager:startBootPhase	(I)V
    //   3337: aload_0
    //   3338: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3341: sipush 500
    //   3344: invokevirtual 521	com/android/server/SystemServiceManager:startBootPhase	(I)V
    //   3347: ldc2_w 285
    //   3350: ldc_w 1242
    //   3353: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   3356: iload_3
    //   3357: ifeq +100 -> 3457
    //   3360: ldc 67
    //   3362: ldc_w 1244
    //   3365: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   3368: pop
    //   3369: aload 39
    //   3371: invokevirtual 1245	java/lang/Object:getClass	()Ljava/lang/Class;
    //   3374: ldc_w 1247
    //   3377: iconst_1
    //   3378: anewarray 925	java/lang/Class
    //   3381: dup
    //   3382: iconst_0
    //   3383: getstatic 1253	java/lang/Integer:TYPE	Ljava/lang/Class;
    //   3386: aastore
    //   3387: invokevirtual 1257	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   3390: aload 39
    //   3392: iconst_1
    //   3393: anewarray 4	java/lang/Object
    //   3396: dup
    //   3397: iconst_0
    //   3398: new 1249	java/lang/Integer
    //   3401: dup
    //   3402: sipush 500
    //   3405: invokespecial 1259	java/lang/Integer:<init>	(I)V
    //   3408: aastore
    //   3409: invokevirtual 1265	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   3412: pop
    //   3413: aload 40
    //   3415: invokevirtual 1245	java/lang/Object:getClass	()Ljava/lang/Class;
    //   3418: ldc_w 1247
    //   3421: iconst_1
    //   3422: anewarray 925	java/lang/Class
    //   3425: dup
    //   3426: iconst_0
    //   3427: getstatic 1253	java/lang/Integer:TYPE	Ljava/lang/Class;
    //   3430: aastore
    //   3431: invokevirtual 1257	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   3434: aload 40
    //   3436: iconst_1
    //   3437: anewarray 4	java/lang/Object
    //   3440: dup
    //   3441: iconst_0
    //   3442: new 1249	java/lang/Integer
    //   3445: dup
    //   3446: sipush 500
    //   3449: invokespecial 1259	java/lang/Integer:<init>	(I)V
    //   3452: aastore
    //   3453: invokevirtual 1265	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   3456: pop
    //   3457: aload 36
    //   3459: invokevirtual 1266	com/android/server/wm/WindowManagerService:systemReady	()V
    //   3462: ldc2_w 285
    //   3465: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   3468: iload_1
    //   3469: ifeq +10 -> 3479
    //   3472: aload_0
    //   3473: getfield 133	com/android/server/SystemServer:mActivityManagerService	Lcom/android/server/am/ActivityManagerService;
    //   3476: invokevirtual 1269	com/android/server/am/ActivityManagerService:showSafeModeOverlay	()V
    //   3479: aload 36
    //   3481: invokevirtual 1273	com/android/server/wm/WindowManagerService:computeNewConfiguration	()Landroid/content/res/Configuration;
    //   3484: astore 21
    //   3486: new 1275	android/util/DisplayMetrics
    //   3489: dup
    //   3490: invokespecial 1276	android/util/DisplayMetrics:<init>	()V
    //   3493: astore 36
    //   3495: aload 73
    //   3497: ldc_w 745
    //   3500: invokevirtual 1280	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   3503: checkcast 1282	android/view/WindowManager
    //   3506: invokeinterface 1286 1 0
    //   3511: aload 36
    //   3513: invokevirtual 1292	android/view/Display:getMetrics	(Landroid/util/DisplayMetrics;)V
    //   3516: aload 73
    //   3518: invokevirtual 1031	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   3521: aload 21
    //   3523: aload 36
    //   3525: invokevirtual 1296	android/content/res/Resources:updateConfiguration	(Landroid/content/res/Configuration;Landroid/util/DisplayMetrics;)V
    //   3528: aload 73
    //   3530: invokevirtual 1300	android/content/Context:getTheme	()Landroid/content/res/Resources$Theme;
    //   3533: astore 21
    //   3535: aload 21
    //   3537: invokevirtual 1305	android/content/res/Resources$Theme:getChangingConfigurations	()I
    //   3540: ifeq +8 -> 3548
    //   3543: aload 21
    //   3545: invokevirtual 1308	android/content/res/Resources$Theme:rebase	()V
    //   3548: ldc2_w 285
    //   3551: ldc_w 1310
    //   3554: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   3557: aload_0
    //   3558: getfield 507	com/android/server/SystemServer:mPowerManagerService	Lcom/android/server/power/PowerManagerService;
    //   3561: aload_0
    //   3562: getfield 133	com/android/server/SystemServer:mActivityManagerService	Lcom/android/server/am/ActivityManagerService;
    //   3565: invokevirtual 1314	com/android/server/am/ActivityManagerService:getAppOpsService	()Lcom/android/internal/app/IAppOpsService;
    //   3568: invokevirtual 1317	com/android/server/power/PowerManagerService:systemReady	(Lcom/android/internal/app/IAppOpsService;)V
    //   3571: ldc2_w 285
    //   3574: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   3577: ldc2_w 285
    //   3580: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   3583: ldc2_w 285
    //   3586: ldc_w 1319
    //   3589: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   3592: aload_0
    //   3593: getfield 562	com/android/server/SystemServer:mPackageManagerService	Lcom/android/server/pm/PackageManagerService;
    //   3596: invokevirtual 1320	com/android/server/pm/PackageManagerService:systemReady	()V
    //   3599: ldc2_w 285
    //   3602: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   3605: ldc2_w 285
    //   3608: ldc_w 1322
    //   3611: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   3614: aload_0
    //   3615: getfield 518	com/android/server/SystemServer:mDisplayManagerService	Lcom/android/server/display/DisplayManagerService;
    //   3618: iload_1
    //   3619: aload_0
    //   3620: getfield 138	com/android/server/SystemServer:mOnlyCore	Z
    //   3623: invokevirtual 1325	com/android/server/display/DisplayManagerService:systemReady	(ZZ)V
    //   3626: ldc2_w 285
    //   3629: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   3632: aload_0
    //   3633: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3636: iload_1
    //   3637: invokevirtual 1328	com/android/server/SystemServiceManager:setSafeMode	(Z)V
    //   3640: aload_0
    //   3641: getfield 133	com/android/server/SystemServer:mActivityManagerService	Lcom/android/server/am/ActivityManagerService;
    //   3644: new 8	com/android/server/SystemServer$2
    //   3647: dup
    //   3648: aload_0
    //   3649: aload 73
    //   3651: aload 26
    //   3653: aload 24
    //   3655: aload 30
    //   3657: aload 25
    //   3659: aload 20
    //   3661: aload 28
    //   3663: aload 27
    //   3665: aload 38
    //   3667: aload 19
    //   3669: aload 31
    //   3671: aload 33
    //   3673: aload 35
    //   3675: aload 32
    //   3677: aload 29
    //   3679: aload 22
    //   3681: aload 23
    //   3683: aload 34
    //   3685: invokespecial 1331	com/android/server/SystemServer$2:<init>	(Lcom/android/server/SystemServer;Landroid/content/Context;Lcom/android/server/NetworkScoreService;Lcom/android/server/NetworkManagementService;Lcom/android/server/net/NetworkStatsService;Lcom/android/server/net/NetworkPolicyManagerService;Lcom/android/server/ConnectivityService;Lcom/android/server/LocationManagerService;Lcom/android/server/CountryDetectorService;Lcom/android/server/NetworkTimeUpdateService;Lcom/android/server/CommonTimeManagementService;Lcom/android/server/AssetAtlasService;Lcom/android/server/input/InputManagerService;Lcom/android/server/TelephonyRegistry;Lcom/android/server/media/MediaRouterService;Lcom/android/server/MmsServiceBroker;Lcom/android/server/OemExService;Lcom/android/server/OnePlusNfcService;Lcom/android/server/display/SDService;)V
    //   3688: invokevirtual 1334	com/android/server/am/ActivityManagerService:systemReady	(Ljava/lang/Runnable;)V
    //   3691: ldc_w 1336
    //   3694: iconst_0
    //   3695: invokestatic 529	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   3698: ifeq +10 -> 3708
    //   3701: aload_0
    //   3702: getfield 133	com/android/server/SystemServer:mActivityManagerService	Lcom/android/server/am/ActivityManagerService;
    //   3705: invokevirtual 1339	com/android/server/am/ActivityManagerService:getPCBNumber	()V
    //   3708: return
    //   3709: iconst_0
    //   3710: istore_1
    //   3711: goto -3090 -> 621
    //   3714: iconst_1
    //   3715: istore_2
    //   3716: goto -3082 -> 634
    //   3719: aload_0
    //   3720: getfield 164	com/android/server/SystemServer:mFactoryTestMode	I
    //   3723: iconst_1
    //   3724: if_icmpne +82 -> 3806
    //   3727: ldc 67
    //   3729: ldc_w 1341
    //   3732: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   3735: pop
    //   3736: goto -2958 -> 778
    //   3739: astore 29
    //   3741: aload 21
    //   3743: astore 25
    //   3745: aload 22
    //   3747: astore 23
    //   3749: aload 20
    //   3751: astore 21
    //   3753: aload 25
    //   3755: astore 22
    //   3757: aload 29
    //   3759: astore 20
    //   3761: ldc_w 477
    //   3764: ldc_w 479
    //   3767: invokestatic 250	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   3770: pop
    //   3771: ldc_w 477
    //   3774: ldc_w 1343
    //   3777: aload 20
    //   3779: invokestatic 255	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   3782: pop
    //   3783: aload 22
    //   3785: astore 33
    //   3787: aload 24
    //   3789: astore 34
    //   3791: aload 19
    //   3793: astore 35
    //   3795: aload 21
    //   3797: astore 37
    //   3799: aload 23
    //   3801: astore 36
    //   3803: goto -2916 -> 887
    //   3806: aload 73
    //   3808: invokevirtual 571	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   3811: ldc_w 1345
    //   3814: invokevirtual 907	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   3817: ifne +15 -> 3832
    //   3820: ldc 67
    //   3822: ldc_w 1347
    //   3825: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   3828: pop
    //   3829: goto -3051 -> 778
    //   3832: iload 5
    //   3834: ifeq +15 -> 3849
    //   3837: ldc 67
    //   3839: ldc_w 1349
    //   3842: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   3845: pop
    //   3846: goto -3068 -> 778
    //   3849: aload_0
    //   3850: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3853: ldc_w 1351
    //   3856: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   3859: pop
    //   3860: goto -3082 -> 778
    //   3863: astore 20
    //   3865: aload_0
    //   3866: ldc_w 1353
    //   3869: aload 20
    //   3871: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3874: goto -2902 -> 972
    //   3877: astore 20
    //   3879: aload_0
    //   3880: ldc_w 1355
    //   3883: aload 20
    //   3885: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3888: goto -2905 -> 983
    //   3891: aload_0
    //   3892: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   3895: ldc 52
    //   3897: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   3900: pop
    //   3901: ldc_w 1357
    //   3904: invokestatic 837	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   3907: invokestatic 1362	android/os/storage/IMountService$Stub:asInterface	(Landroid/os/IBinder;)Landroid/os/storage/IMountService;
    //   3910: astore 29
    //   3912: goto -2889 -> 1023
    //   3915: astore 20
    //   3917: aload_0
    //   3918: ldc_w 1364
    //   3921: aload 20
    //   3923: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3926: aload 31
    //   3928: astore 29
    //   3930: goto -2907 -> 1023
    //   3933: astore 20
    //   3935: aload_0
    //   3936: ldc_w 1366
    //   3939: aload 20
    //   3941: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3944: goto -2887 -> 1057
    //   3947: astore 20
    //   3949: aload_0
    //   3950: ldc_w 1368
    //   3953: aload 20
    //   3955: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3958: goto -2879 -> 1079
    //   3961: ldc_w 827
    //   3964: aload 20
    //   3966: invokestatic 331	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   3969: ldc_w 1370
    //   3972: aload 25
    //   3974: invokestatic 331	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   3977: goto -2842 -> 1135
    //   3980: astore 19
    //   3982: aload_0
    //   3983: ldc_w 1372
    //   3986: aload 19
    //   3988: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3991: aload 71
    //   3993: astore 21
    //   3995: goto -2776 -> 1219
    //   3998: astore 19
    //   4000: aload_0
    //   4001: ldc_w 1374
    //   4004: aload 19
    //   4006: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4009: goto -2706 -> 1303
    //   4012: astore 19
    //   4014: aload_0
    //   4015: ldc_w 1376
    //   4018: aload 19
    //   4020: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4023: goto -2688 -> 1335
    //   4026: astore 20
    //   4028: aload_0
    //   4029: ldc_w 1378
    //   4032: aload 20
    //   4034: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4037: goto -2654 -> 1383
    //   4040: aload_0
    //   4041: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   4044: ldc_w 1380
    //   4047: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   4050: pop
    //   4051: goto -2648 -> 1403
    //   4054: astore 19
    //   4056: aload 56
    //   4058: astore 23
    //   4060: aload_0
    //   4061: ldc_w 1382
    //   4064: aload 19
    //   4066: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4069: goto -2608 -> 1461
    //   4072: astore 19
    //   4074: aload_0
    //   4075: ldc_w 1384
    //   4078: aload 19
    //   4080: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4083: goto -2581 -> 1502
    //   4086: astore 19
    //   4088: aload 53
    //   4090: astore 27
    //   4092: aload_0
    //   4093: ldc_w 1386
    //   4096: aload 19
    //   4098: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4101: goto -2556 -> 1545
    //   4104: ldc 67
    //   4106: ldc_w 1388
    //   4109: invokestatic 340	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   4112: pop
    //   4113: goto -2538 -> 1575
    //   4116: astore 25
    //   4118: aload_0
    //   4119: ldc_w 1390
    //   4122: aload 25
    //   4124: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4127: goto -2282 -> 1845
    //   4130: astore 25
    //   4132: aload 48
    //   4134: astore 28
    //   4136: aload_0
    //   4137: ldc_w 1392
    //   4140: aload 25
    //   4142: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4145: goto -2215 -> 1930
    //   4148: astore 25
    //   4150: aload_0
    //   4151: ldc_w 1394
    //   4154: aload 25
    //   4156: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4159: goto -2206 -> 1953
    //   4162: astore 27
    //   4164: aload_0
    //   4165: ldc_w 1396
    //   4168: aload 27
    //   4170: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4173: goto -2172 -> 2001
    //   4176: ldc2_w 285
    //   4179: ldc_w 1398
    //   4182: invokestatic 294	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   4185: aload 29
    //   4187: invokeinterface 1403 1 0
    //   4192: ldc2_w 285
    //   4195: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   4198: goto -2163 -> 2035
    //   4201: astore 27
    //   4203: goto -11 -> 4192
    //   4206: astore 27
    //   4208: aload 57
    //   4210: astore 28
    //   4212: aload_0
    //   4213: ldc_w 1405
    //   4216: aload 27
    //   4218: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4221: goto -2108 -> 2113
    //   4224: astore 29
    //   4226: aload 54
    //   4228: astore 27
    //   4230: aload_0
    //   4231: ldc_w 1407
    //   4234: aload 29
    //   4236: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4239: goto -2095 -> 2144
    //   4242: ldc_w 1409
    //   4245: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   4248: aload_0
    //   4249: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   4252: ldc 61
    //   4254: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   4257: pop
    //   4258: ldc2_w 285
    //   4261: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   4264: goto -2104 -> 2160
    //   4267: astore 29
    //   4269: aload_0
    //   4270: ldc_w 1411
    //   4273: aload 29
    //   4275: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4278: goto -20 -> 4258
    //   4281: ldc_w 1413
    //   4284: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   4287: aload_0
    //   4288: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   4291: ldc 82
    //   4293: invokevirtual 696	com/android/server/SystemServiceManager:startService	(Ljava/lang/String;)Lcom/android/server/SystemService;
    //   4296: pop
    //   4297: ldc2_w 285
    //   4300: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   4303: goto -2106 -> 2197
    //   4306: astore 29
    //   4308: aload_0
    //   4309: ldc_w 1415
    //   4312: aload 29
    //   4314: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4317: goto -2045 -> 2272
    //   4320: astore 29
    //   4322: ldc 67
    //   4324: ldc_w 1417
    //   4327: aload 29
    //   4329: invokestatic 255	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   4332: pop
    //   4333: goto -1946 -> 2387
    //   4336: astore 29
    //   4338: ldc 67
    //   4340: ldc_w 1419
    //   4343: aload 29
    //   4345: invokestatic 255	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   4348: pop
    //   4349: goto -1928 -> 2421
    //   4352: astore 29
    //   4354: aload_0
    //   4355: ldc_w 1421
    //   4358: aload 29
    //   4360: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4363: goto -1722 -> 2641
    //   4366: astore 29
    //   4368: aload_0
    //   4369: ldc_w 1423
    //   4372: aload 29
    //   4374: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4377: goto -1704 -> 2673
    //   4380: ldc_w 1425
    //   4383: invokestatic 555	com/android/server/SystemServer:traceBeginAndSlog	(Ljava/lang/String;)V
    //   4386: new 1427	com/android/server/NetworkTimeUpdateService
    //   4389: dup
    //   4390: aload 73
    //   4392: invokespecial 1428	com/android/server/NetworkTimeUpdateService:<init>	(Landroid/content/Context;)V
    //   4395: astore 29
    //   4397: ldc_w 1430
    //   4400: aload 29
    //   4402: invokestatic 550	android/os/ServiceManager:addService	(Ljava/lang/String;Landroid/os/IBinder;)V
    //   4405: ldc2_w 285
    //   4408: invokestatic 449	android/os/Trace:traceEnd	(J)V
    //   4411: aload 29
    //   4413: astore 30
    //   4415: goto -1718 -> 2697
    //   4418: astore 30
    //   4420: aload 42
    //   4422: astore 29
    //   4424: aload_0
    //   4425: ldc_w 1432
    //   4428: aload 30
    //   4430: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4433: goto -28 -> 4405
    //   4436: astore 31
    //   4438: aload 41
    //   4440: astore 29
    //   4442: aload_0
    //   4443: ldc_w 1434
    //   4446: aload 31
    //   4448: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4451: goto -1729 -> 2722
    //   4454: astore 31
    //   4456: aload_0
    //   4457: ldc_w 1436
    //   4460: aload 31
    //   4462: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4465: goto -1716 -> 2749
    //   4468: aload_0
    //   4469: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   4472: ldc_w 1438
    //   4475: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   4478: pop
    //   4479: goto -1714 -> 2765
    //   4482: astore 32
    //   4484: aload 46
    //   4486: astore 31
    //   4488: aload_0
    //   4489: ldc_w 1440
    //   4492: aload 32
    //   4494: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4497: goto -1682 -> 2815
    //   4500: astore 38
    //   4502: aload 44
    //   4504: astore 32
    //   4506: aload_0
    //   4507: ldc_w 1442
    //   4510: aload 38
    //   4512: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4515: goto -1461 -> 3054
    //   4518: astore 38
    //   4520: aload_0
    //   4521: ldc_w 1444
    //   4524: aload 38
    //   4526: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4529: goto -1418 -> 3111
    //   4532: aload_0
    //   4533: getfield 142	com/android/server/SystemServer:mSystemServiceManager	Lcom/android/server/SystemServiceManager;
    //   4536: ldc_w 1446
    //   4539: invokevirtual 487	com/android/server/SystemServiceManager:startService	(Ljava/lang/Class;)Lcom/android/server/SystemService;
    //   4542: pop
    //   4543: goto -1350 -> 3193
    //   4546: invokestatic 357	dalvik/system/VMRuntime:getRuntime	()Ldalvik/system/VMRuntime;
    //   4549: invokevirtual 1449	dalvik/system/VMRuntime:startJitCompilation	()V
    //   4552: goto -1322 -> 3230
    //   4555: astore 37
    //   4557: aload_0
    //   4558: ldc_w 1451
    //   4561: aload 37
    //   4563: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4566: goto -1272 -> 3294
    //   4569: astore 21
    //   4571: aload_0
    //   4572: ldc_w 1453
    //   4575: aload 21
    //   4577: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4580: goto -1259 -> 3321
    //   4583: astore 21
    //   4585: aload_0
    //   4586: ldc_w 1455
    //   4589: aload 21
    //   4591: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4594: goto -1137 -> 3457
    //   4597: astore 21
    //   4599: aload_0
    //   4600: ldc_w 1457
    //   4603: aload 21
    //   4605: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4608: goto -1146 -> 3462
    //   4611: astore 21
    //   4613: aload_0
    //   4614: ldc_w 1459
    //   4617: aload 21
    //   4619: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4622: goto -1045 -> 3577
    //   4625: astore 21
    //   4627: aload_0
    //   4628: ldc_w 1461
    //   4631: aload 21
    //   4633: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4636: goto -1037 -> 3599
    //   4639: astore 21
    //   4641: aload_0
    //   4642: ldc_w 1463
    //   4645: aload 21
    //   4647: invokespecial 152	com/android/server/SystemServer:reportWtf	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   4650: goto -1024 -> 3626
    //   4653: astore 38
    //   4655: goto -149 -> 4506
    //   4658: astore 32
    //   4660: goto -172 -> 4488
    //   4663: astore 31
    //   4665: goto -223 -> 4442
    //   4668: astore 30
    //   4670: goto -246 -> 4424
    //   4673: astore 29
    //   4675: goto -337 -> 4338
    //   4678: astore 29
    //   4680: goto -358 -> 4322
    //   4683: astore 29
    //   4685: goto -455 -> 4230
    //   4688: astore 29
    //   4690: aload 27
    //   4692: astore 28
    //   4694: aload 29
    //   4696: astore 27
    //   4698: goto -486 -> 4212
    //   4701: astore 26
    //   4703: aload 25
    //   4705: astore 28
    //   4707: aload 26
    //   4709: astore 25
    //   4711: goto -575 -> 4136
    //   4714: astore 20
    //   4716: aload 19
    //   4718: astore 27
    //   4720: aload 20
    //   4722: astore 19
    //   4724: goto -632 -> 4092
    //   4727: astore 20
    //   4729: aload 19
    //   4731: astore 23
    //   4733: aload 20
    //   4735: astore 19
    //   4737: goto -677 -> 4060
    //   4740: astore 19
    //   4742: goto -742 -> 4000
    //   4745: astore 20
    //   4747: aload 25
    //   4749: astore 24
    //   4751: aload 29
    //   4753: astore 19
    //   4755: goto -994 -> 3761
    //   4758: astore 20
    //   4760: aload 25
    //   4762: astore 24
    //   4764: goto -1003 -> 3761
    //   4767: astore 24
    //   4769: aload 20
    //   4771: astore 21
    //   4773: aload 24
    //   4775: astore 20
    //   4777: aload 25
    //   4779: astore 24
    //   4781: goto -1020 -> 3761
    //   4784: astore 24
    //   4786: aload 20
    //   4788: astore 21
    //   4790: aload 24
    //   4792: astore 20
    //   4794: aload 25
    //   4796: astore 24
    //   4798: goto -1037 -> 3761
    //   4801: astore 24
    //   4803: aload 21
    //   4805: astore 22
    //   4807: aload 20
    //   4809: astore 21
    //   4811: aload 24
    //   4813: astore 20
    //   4815: aload 25
    //   4817: astore 24
    //   4819: goto -1058 -> 3761
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	4822	0	this	SystemServer
    //   620	3091	1	bool1	boolean
    //   633	3083	2	bool2	boolean
    //   263	3094	3	bool3	boolean
    //   124	876	4	bool4	boolean
    //   133	3700	5	bool5	boolean
    //   142	1938	6	bool6	boolean
    //   151	1121	7	bool7	boolean
    //   160	3024	8	bool8	boolean
    //   169	2587	9	bool9	boolean
    //   178	2511	10	bool10	boolean
    //   187	1420	11	bool11	boolean
    //   196	2993	12	bool12	boolean
    //   205	2153	13	bool13	boolean
    //   214	1942	14	bool14	boolean
    //   223	2838	15	bool15	boolean
    //   232	1167	16	bool16	boolean
    //   241	2407	17	bool17	boolean
    //   254	511	18	bool18	boolean
    //   328	3464	19	localObject1	Object
    //   3980	7	19	localThrowable1	Throwable
    //   3998	7	19	localThrowable2	Throwable
    //   4012	7	19	localThrowable3	Throwable
    //   4054	11	19	localThrowable4	Throwable
    //   4072	7	19	localThrowable5	Throwable
    //   4086	631	19	localThrowable6	Throwable
    //   4722	14	19	localObject2	Object
    //   4740	1	19	localThrowable7	Throwable
    //   4753	1	19	localThrowable8	Throwable
    //   476	3302	20	localObject3	Object
    //   3863	7	20	localThrowable9	Throwable
    //   3877	7	20	localThrowable10	Throwable
    //   3915	7	20	localThrowable11	Throwable
    //   3933	7	20	localThrowable12	Throwable
    //   3947	18	20	localThrowable13	Throwable
    //   4026	7	20	localThrowable14	Throwable
    //   4714	7	20	localThrowable15	Throwable
    //   4727	7	20	localThrowable16	Throwable
    //   4745	1	20	localRuntimeException1	RuntimeException
    //   4758	12	20	localRuntimeException2	RuntimeException
    //   4775	39	20	localRuntimeException3	RuntimeException
    //   7	3987	21	localObject4	Object
    //   4569	7	21	localThrowable17	Throwable
    //   4583	7	21	localThrowable18	Throwable
    //   4597	7	21	localThrowable19	Throwable
    //   4611	7	21	localThrowable20	Throwable
    //   4625	7	21	localThrowable21	Throwable
    //   4639	7	21	localThrowable22	Throwable
    //   4771	39	21	localObject5	Object
    //   79	4727	22	localObject6	Object
    //   61	4671	23	localObject7	Object
    //   58	4705	24	localObject8	Object
    //   4767	7	24	localRuntimeException4	RuntimeException
    //   4779	1	24	localObject9	Object
    //   4784	7	24	localRuntimeException5	RuntimeException
    //   4796	1	24	localObject10	Object
    //   4801	11	24	localRuntimeException6	RuntimeException
    //   4817	1	24	localObject11	Object
    //   115	3858	25	localObject12	Object
    //   4116	7	25	localThrowable23	Throwable
    //   4130	11	25	localThrowable24	Throwable
    //   4148	556	25	localThrowable25	Throwable
    //   4709	107	25	localThrowable26	Throwable
    //   49	3603	26	localObject13	Object
    //   4701	7	26	localThrowable27	Throwable
    //   40	4051	27	localObject14	Object
    //   4162	7	27	localThrowable28	Throwable
    //   4201	1	27	localRemoteException	android.os.RemoteException
    //   4206	11	27	localThrowable29	Throwable
    //   4228	491	27	localObject15	Object
    //   76	4630	28	localObject16	Object
    //   82	3596	29	localObject17	Object
    //   3739	19	29	localRuntimeException7	RuntimeException
    //   3910	276	29	localObject18	Object
    //   4224	11	29	localThrowable30	Throwable
    //   4267	7	29	localThrowable31	Throwable
    //   4306	7	29	localThrowable32	Throwable
    //   4320	8	29	localThrowable33	Throwable
    //   4336	8	29	localThrowable34	Throwable
    //   4352	7	29	localThrowable35	Throwable
    //   4366	7	29	localThrowable36	Throwable
    //   4395	46	29	localObject19	Object
    //   4673	1	29	localThrowable37	Throwable
    //   4678	1	29	localThrowable38	Throwable
    //   4683	1	29	localThrowable39	Throwable
    //   4688	64	29	localThrowable40	Throwable
    //   22	4392	30	localObject20	Object
    //   4418	11	30	localThrowable41	Throwable
    //   4668	1	30	localThrowable42	Throwable
    //   10	3917	31	localObject21	Object
    //   4436	11	31	localThrowable43	Throwable
    //   4454	7	31	localThrowable44	Throwable
    //   4486	1	31	localObject22	Object
    //   4663	1	31	localThrowable45	Throwable
    //   112	3564	32	localObject23	Object
    //   4482	11	32	localThrowable46	Throwable
    //   4504	1	32	localObject24	Object
    //   4658	1	32	localThrowable47	Throwable
    //   885	2901	33	localObject25	Object
    //   869	2921	34	localObject26	Object
    //   881	2913	35	localObject27	Object
    //   873	2929	36	localObject28	Object
    //   877	2921	37	localObject29	Object
    //   4555	7	37	localThrowable48	Throwable
    //   64	3602	38	localObject30	Object
    //   4500	11	38	localThrowable49	Throwable
    //   4518	7	38	localThrowable50	Throwable
    //   4653	1	38	localThrowable51	Throwable
    //   88	3303	39	localObject31	Object
    //   100	3335	40	localObject32	Object
    //   73	4366	41	localOemExService	OemExService
    //   67	4354	42	localOnePlusNfcService	OnePlusNfcService
    //   70	2624	43	localObject33	Object
    //   927	3576	44	localObject34	Object
    //   930	2091	45	localObject35	Object
    //   918	3567	46	localObject36	Object
    //   921	1861	47	localObject37	Object
    //   43	4090	48	localObject38	Object
    //   103	1661	49	localObject39	Object
    //   91	1591	50	localObject40	Object
    //   106	1521	51	localObject41	Object
    //   94	1529	52	localObject42	Object
    //   34	4055	53	localObject43	Object
    //   900	3327	54	localObject44	Object
    //   25	1449	55	localObject45	Object
    //   52	4005	56	localObject46	Object
    //   891	3318	57	localObject47	Object
    //   894	1182	58	localObject48	Object
    //   97	1327	59	localObject49	Object
    //   903	1169	60	localObject50	Object
    //   85	1335	61	localObject51	Object
    //   28	1388	62	localObject52	Object
    //   55	1357	63	localObject53	Object
    //   37	1371	64	localObject54	Object
    //   46	1358	65	localObject55	Object
    //   16	1341	66	localObject56	Object
    //   19	1323	67	localObject57	Object
    //   31	1141	68	localObject58	Object
    //   13	1155	69	localObject59	Object
    //   109	1047	70	localObject60	Object
    //   912	3080	71	localObject61	Object
    //   906	278	72	localObject62	Object
    //   4	4387	73	localContext	Context
    // Exception table:
    //   from	to	target	type
    //   702	764	3739	java/lang/RuntimeException
    //   769	778	3739	java/lang/RuntimeException
    //   778	867	3739	java/lang/RuntimeException
    //   3719	3736	3739	java/lang/RuntimeException
    //   3806	3829	3739	java/lang/RuntimeException
    //   3837	3846	3739	java/lang/RuntimeException
    //   3849	3860	3739	java/lang/RuntimeException
    //   957	972	3863	java/lang/Throwable
    //   978	983	3877	java/lang/Throwable
    //   3891	3912	3915	java/lang/Throwable
    //   1050	1057	3933	java/lang/Throwable
    //   1072	1079	3947	java/lang/Throwable
    //   1198	1219	3980	java/lang/Throwable
    //   1282	1295	3998	java/lang/Throwable
    //   1320	1335	4012	java/lang/Throwable
    //   1360	1367	4026	java/lang/Throwable
    //   1371	1379	4026	java/lang/Throwable
    //   1438	1449	4054	java/lang/Throwable
    //   1477	1486	4072	java/lang/Throwable
    //   1490	1498	4072	java/lang/Throwable
    //   1514	1533	4086	java/lang/Throwable
    //   1642	1651	4116	java/lang/Throwable
    //   1659	1681	4116	java/lang/Throwable
    //   1689	1723	4116	java/lang/Throwable
    //   1731	1740	4116	java/lang/Throwable
    //   1748	1759	4116	java/lang/Throwable
    //   1767	1801	4116	java/lang/Throwable
    //   1809	1818	4116	java/lang/Throwable
    //   1826	1837	4116	java/lang/Throwable
    //   1887	1904	4130	java/lang/Throwable
    //   1942	1953	4148	java/lang/Throwable
    //   1986	2001	4162	java/lang/Throwable
    //   4185	4192	4201	android/os/RemoteException
    //   2090	2101	4206	java/lang/Throwable
    //   2125	2136	4224	java/lang/Throwable
    //   4248	4258	4267	java/lang/Throwable
    //   2256	2272	4306	java/lang/Throwable
    //   2368	2379	4320	java/lang/Throwable
    //   2402	2413	4336	java/lang/Throwable
    //   2626	2641	4352	java/lang/Throwable
    //   2658	2673	4366	java/lang/Throwable
    //   4386	4397	4418	java/lang/Throwable
    //   2703	2714	4436	java/lang/Throwable
    //   2739	2749	4454	java/lang/Throwable
    //   2796	2807	4482	java/lang/Throwable
    //   3035	3046	4500	java/lang/Throwable
    //   3106	3111	4518	java/lang/Throwable
    //   3289	3294	4555	java/lang/Throwable
    //   3314	3321	4569	java/lang/Throwable
    //   3360	3457	4583	java/lang/Throwable
    //   3457	3462	4597	java/lang/Throwable
    //   3557	3577	4611	java/lang/Throwable
    //   3592	3599	4625	java/lang/Throwable
    //   3614	3626	4639	java/lang/Throwable
    //   3046	3054	4653	java/lang/Throwable
    //   2807	2815	4658	java/lang/Throwable
    //   2714	2722	4663	java/lang/Throwable
    //   4397	4405	4668	java/lang/Throwable
    //   2413	2421	4673	java/lang/Throwable
    //   2379	2387	4678	java/lang/Throwable
    //   2136	2144	4683	java/lang/Throwable
    //   2101	2109	4688	java/lang/Throwable
    //   1904	1926	4701	java/lang/Throwable
    //   1533	1541	4714	java/lang/Throwable
    //   1449	1457	4727	java/lang/Throwable
    //   1295	1303	4740	java/lang/Throwable
    //   264	330	4745	java/lang/RuntimeException
    //   330	478	4758	java/lang/RuntimeException
    //   478	509	4767	java/lang/RuntimeException
    //   509	587	4784	java/lang/RuntimeException
    //   591	597	4801	java/lang/RuntimeException
    //   601	607	4801	java/lang/RuntimeException
    //   611	619	4801	java/lang/RuntimeException
    //   625	632	4801	java/lang/RuntimeException
    //   638	653	4801	java/lang/RuntimeException
    //   657	665	4801	java/lang/RuntimeException
    //   669	677	4801	java/lang/RuntimeException
    //   681	687	4801	java/lang/RuntimeException
    //   691	702	4801	java/lang/RuntimeException
  }
  
  private static native void startSensorService();
  
  static final void startSystemUi(Context paramContext)
  {
    Intent localIntent = new Intent();
    localIntent.setComponent(new ComponentName("com.android.systemui", "com.android.systemui.SystemUIService"));
    localIntent.addFlags(256);
    paramContext.startServiceAsUser(localIntent, UserHandle.SYSTEM);
  }
  
  private static void traceBeginAndSlog(String paramString)
  {
    Trace.traceBegin(524288L, paramString);
    Slog.i("SystemServer", paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/SystemServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */