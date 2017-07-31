package com.android.server;

import android.app.ActivityManagerNative;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.BatteryManagerInternal;
import android.os.BatteryProperties;
import android.os.Binder;
import android.os.Handler;
import android.os.IBatteryPropertiesListener.Stub;
import android.os.IBatteryPropertiesRegistrar;
import android.os.IBatteryPropertiesRegistrar.Stub;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UEventObserver;
import android.os.UEventObserver.UEvent;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.util.EventLog;
import android.util.Slog;
import com.android.internal.app.IBatteryStats;
import com.android.server.am.BatteryStatsService;
import com.android.server.lights.Light;
import com.android.server.lights.LightsManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

public final class BatteryService
  extends SystemService
{
  private static final int BATTERY_PLUGGED_NONE = 0;
  private static final int BATTERY_SCALE = 100;
  private static final boolean DEBUG = false;
  private static final boolean DEBUG_LED = false;
  private static final String[] DUMPSYS_ARGS = { "--checkin", "--unplugged" };
  private static final String DUMPSYS_DATA_PATH = "/data/system/";
  private static final String TAG = BatteryService.class.getSimpleName();
  private static final String TAG_LED = "BatteryLed";
  private static int mWeakChgSocCheckStarted = 0;
  private boolean mBatteryLevelCritical;
  private boolean mBatteryLevelLow;
  private boolean mBatteryLowHint;
  private BatteryProperties mBatteryProps;
  private final IBatteryStats mBatteryStats;
  BinderService mBinderService;
  private boolean mChargingHint;
  private final Context mContext;
  private int mCriticalBatteryLevel;
  private int mDefLowBatteryWarningLevel;
  private int mDischargeStartLevel;
  private long mDischargeStartTime;
  private boolean mFastChargeStatus = false;
  private final Handler mHandler;
  private boolean mInitiateShutdown = false;
  private int mInvalidCharger;
  private boolean mIsFastChargeSupport = false;
  private int mLastBatteryHealth;
  private int mLastBatteryLevel;
  private boolean mLastBatteryLevelCritical;
  private boolean mLastBatteryPresent;
  private final BatteryProperties mLastBatteryProps = new BatteryProperties();
  private int mLastBatteryStatus;
  private int mLastBatteryTemperature;
  private int mLastBatteryVoltage;
  private int mLastChargeCounter;
  private boolean mLastFastChargeStatus = false;
  private int mLastInvalidCharger;
  private int mLastMaxChargingCurrent;
  private int mLastMaxChargingVoltage;
  private int mLastPlugType = -1;
  private Led mLed;
  private final Object mLock = new Object();
  private int mLowBatteryCloseWarningLevel;
  private int mLowBatteryWarningLevel;
  private int mPlugType;
  private boolean mSentLowBatteryBroadcast = false;
  private int mShutdownBatteryTemperature;
  private boolean mUpdatesStopped;
  private final int mVbattSamplingIntervalMsec = 30000;
  private File mVoltageNowFile = null;
  private final int mWeakChgCutoffVoltageMv;
  private final int mWeakChgMaxShutdownIntervalMsecs = 300000;
  private Runnable runnable = new Runnable()
  {
    public void run()
    {
      synchronized (BatteryService.-get10(BatteryService.this))
      {
        if (BatteryService.-get12(BatteryService.this).exists())
        {
          BatteryService.-wrap3(BatteryService.this);
          return;
        }
        BatteryService.-wrap2(BatteryService.this);
      }
    }
  };
  
  public BatteryService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mHandler = new Handler(true);
    this.mLed = new Led(paramContext, (LightsManager)getLocalService(LightsManager.class));
    this.mBatteryStats = BatteryStatsService.getService();
    this.mWeakChgCutoffVoltageMv = SystemProperties.getInt("ro.cutoff_voltage_mv", 0);
    if (this.mWeakChgCutoffVoltageMv > 2700) {
      this.mVoltageNowFile = new File("/sys/class/power_supply/battery/voltage_now");
    }
    this.mCriticalBatteryLevel = this.mContext.getResources().getInteger(17694806);
    this.mLowBatteryWarningLevel = this.mContext.getResources().getInteger(17694808);
    this.mLowBatteryCloseWarningLevel = (this.mLowBatteryWarningLevel + this.mContext.getResources().getInteger(17694809));
    this.mShutdownBatteryTemperature = this.mContext.getResources().getInteger(17694807);
    this.mDefLowBatteryWarningLevel = this.mContext.getResources().getInteger(17694808);
    if (new File("/sys/devices/virtual/switch/invalid_charger/state").exists()) {
      new UEventObserver()
      {
        public void onUEvent(UEventObserver.UEvent arg1)
        {
          int i;
          if ("1".equals(???.get("SWITCH_STATE"))) {
            i = 1;
          }
          synchronized (BatteryService.-get10(BatteryService.this))
          {
            if (BatteryService.-get8(BatteryService.this) != i) {
              BatteryService.-set2(BatteryService.this, i);
            }
            return;
            i = 0;
          }
        }
      }.startObserving("DEVPATH=/devices/virtual/switch/invalid_charger");
    }
    if (new File("/sys/class/power_supply/battery/fastchg_status").exists()) {
      this.mIsFastChargeSupport = true;
    }
  }
  
  static void dumpHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("Battery service (battery) commands:");
    paramPrintWriter.println("  help");
    paramPrintWriter.println("    Print this help text.");
    paramPrintWriter.println("  set [ac|usb|wireless|status|level|invalid] <value>");
    paramPrintWriter.println("    Force a battery property value, freezing battery state.");
    paramPrintWriter.println("  unplug");
    paramPrintWriter.println("    Force battery unplugged, freezing battery state.");
    paramPrintWriter.println("  reset");
    paramPrintWriter.println("    Unfreeze battery state, returning to current hardware values.");
  }
  
  /* Error */
  private void dumpInternal(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 140	com/android/server/BatteryService:mLock	Ljava/lang/Object;
    //   4: astore 4
    //   6: aload 4
    //   8: monitorenter
    //   9: aload_3
    //   10: ifnull +8 -> 18
    //   13: aload_3
    //   14: arraylength
    //   15: ifne +425 -> 440
    //   18: aload_2
    //   19: ldc_w 372
    //   22: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   25: aload_0
    //   26: getfield 374	com/android/server/BatteryService:mUpdatesStopped	Z
    //   29: ifeq +10 -> 39
    //   32: aload_2
    //   33: ldc_w 376
    //   36: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   39: aload_2
    //   40: new 378	java/lang/StringBuilder
    //   43: dup
    //   44: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   47: ldc_w 381
    //   50: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: aload_0
    //   54: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   57: getfield 388	android/os/BatteryProperties:chargerAcOnline	Z
    //   60: invokevirtual 391	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   63: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   69: aload_2
    //   70: new 378	java/lang/StringBuilder
    //   73: dup
    //   74: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   77: ldc_w 396
    //   80: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: aload_0
    //   84: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   87: getfield 399	android/os/BatteryProperties:chargerUsbOnline	Z
    //   90: invokevirtual 391	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   93: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   96: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   99: aload_2
    //   100: new 378	java/lang/StringBuilder
    //   103: dup
    //   104: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   107: ldc_w 401
    //   110: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   113: aload_0
    //   114: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   117: getfield 404	android/os/BatteryProperties:chargerWirelessOnline	Z
    //   120: invokevirtual 391	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   123: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   126: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   129: aload_2
    //   130: new 378	java/lang/StringBuilder
    //   133: dup
    //   134: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   137: ldc_w 406
    //   140: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: aload_0
    //   144: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   147: getfield 409	android/os/BatteryProperties:maxChargingCurrent	I
    //   150: invokevirtual 412	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   153: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   156: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   159: aload_2
    //   160: new 378	java/lang/StringBuilder
    //   163: dup
    //   164: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   167: ldc_w 414
    //   170: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   173: aload_0
    //   174: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   177: getfield 417	android/os/BatteryProperties:maxChargingVoltage	I
    //   180: invokevirtual 412	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   183: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   186: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   189: aload_2
    //   190: new 378	java/lang/StringBuilder
    //   193: dup
    //   194: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   197: ldc_w 419
    //   200: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   203: aload_0
    //   204: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   207: getfield 422	android/os/BatteryProperties:batteryChargeCounter	I
    //   210: invokevirtual 412	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   213: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   216: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   219: aload_2
    //   220: new 378	java/lang/StringBuilder
    //   223: dup
    //   224: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   227: ldc_w 424
    //   230: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   233: aload_0
    //   234: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   237: getfield 427	android/os/BatteryProperties:batteryStatus	I
    //   240: invokevirtual 412	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   243: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   246: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   249: aload_2
    //   250: new 378	java/lang/StringBuilder
    //   253: dup
    //   254: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   257: ldc_w 429
    //   260: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   263: aload_0
    //   264: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   267: getfield 432	android/os/BatteryProperties:batteryHealth	I
    //   270: invokevirtual 412	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   273: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   276: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   279: aload_2
    //   280: new 378	java/lang/StringBuilder
    //   283: dup
    //   284: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   287: ldc_w 434
    //   290: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   293: aload_0
    //   294: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   297: getfield 437	android/os/BatteryProperties:batteryPresent	Z
    //   300: invokevirtual 391	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   303: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   306: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   309: aload_2
    //   310: new 378	java/lang/StringBuilder
    //   313: dup
    //   314: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   317: ldc_w 439
    //   320: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   323: aload_0
    //   324: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   327: getfield 442	android/os/BatteryProperties:batteryLevel	I
    //   330: invokevirtual 412	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   333: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   336: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   339: aload_2
    //   340: ldc_w 444
    //   343: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   346: aload_2
    //   347: new 378	java/lang/StringBuilder
    //   350: dup
    //   351: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   354: ldc_w 446
    //   357: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   360: aload_0
    //   361: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   364: getfield 449	android/os/BatteryProperties:batteryVoltage	I
    //   367: invokevirtual 412	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   370: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   373: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   376: aload_2
    //   377: new 378	java/lang/StringBuilder
    //   380: dup
    //   381: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   384: ldc_w 451
    //   387: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   390: aload_0
    //   391: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   394: getfield 454	android/os/BatteryProperties:batteryTemperature	I
    //   397: invokevirtual 412	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   400: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   403: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   406: aload_2
    //   407: new 378	java/lang/StringBuilder
    //   410: dup
    //   411: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   414: ldc_w 456
    //   417: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   420: aload_0
    //   421: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   424: getfield 459	android/os/BatteryProperties:batteryTechnology	Ljava/lang/String;
    //   427: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   430: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   433: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   436: aload 4
    //   438: monitorexit
    //   439: return
    //   440: ldc_w 461
    //   443: aload_3
    //   444: iconst_0
    //   445: aaload
    //   446: invokevirtual 465	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   449: ifne -431 -> 18
    //   452: new 53	com/android/server/BatteryService$Shell
    //   455: dup
    //   456: aload_0
    //   457: invokespecial 466	com/android/server/BatteryService$Shell:<init>	(Lcom/android/server/BatteryService;)V
    //   460: aload_0
    //   461: getfield 468	com/android/server/BatteryService:mBinderService	Lcom/android/server/BatteryService$BinderService;
    //   464: aconst_null
    //   465: aload_1
    //   466: aconst_null
    //   467: aload_3
    //   468: new 470	android/os/ResultReceiver
    //   471: dup
    //   472: aconst_null
    //   473: invokespecial 473	android/os/ResultReceiver:<init>	(Landroid/os/Handler;)V
    //   476: invokevirtual 477	com/android/server/BatteryService$Shell:exec	(Landroid/os/Binder;Ljava/io/FileDescriptor;Ljava/io/FileDescriptor;Ljava/io/FileDescriptor;[Ljava/lang/String;Landroid/os/ResultReceiver;)I
    //   479: pop
    //   480: goto -44 -> 436
    //   483: astore_1
    //   484: aload 4
    //   486: monitorexit
    //   487: aload_1
    //   488: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	489	0	this	BatteryService
    //   0	489	1	paramFileDescriptor	FileDescriptor
    //   0	489	2	paramPrintWriter	PrintWriter
    //   0	489	3	paramArrayOfString	String[]
    //   4	481	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   13	18	483	finally
    //   18	39	483	finally
    //   39	436	483	finally
    //   440	480	483	finally
  }
  
  private int getIconLocked(int paramInt)
  {
    if (this.mBatteryProps.batteryStatus == 2) {
      return 17303278;
    }
    if (this.mBatteryProps.batteryStatus == 3) {
      return 17303264;
    }
    if ((this.mBatteryProps.batteryStatus == 4) || (this.mBatteryProps.batteryStatus == 5))
    {
      if ((isPoweredLocked(7)) && (this.mBatteryProps.batteryLevel >= 100)) {
        return 17303278;
      }
      return 17303264;
    }
    return 17303292;
  }
  
  private boolean isFastCharge()
  {
    Object localObject = new File("/sys/class/power_supply/battery/fastchg_status");
    try
    {
      localObject = new FileReader((File)localObject);
      BufferedReader localBufferedReader = new BufferedReader((Reader)localObject);
      if (localBufferedReader.readLine().equals("1")) {}
      for (boolean bool = true;; bool = false)
      {
        localBufferedReader.close();
        ((FileReader)localObject).close();
        return bool;
      }
      return false;
    }
    catch (IOException localIOException)
    {
      Slog.e(TAG, "Failure in reading charger type", localIOException);
      return false;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Slog.e(TAG, "Failure in reading charger type", localFileNotFoundException);
    }
  }
  
  private boolean isPoweredLocked(int paramInt)
  {
    if (this.mBatteryProps.batteryStatus == 1) {
      return true;
    }
    if (((paramInt & 0x1) != 0) && (this.mBatteryProps.chargerAcOnline)) {
      return true;
    }
    if (((paramInt & 0x2) != 0) && (this.mBatteryProps.chargerUsbOnline)) {
      return true;
    }
    return ((paramInt & 0x4) != 0) && (this.mBatteryProps.chargerWirelessOnline);
  }
  
  /* Error */
  private void logBatteryStatsLocked()
  {
    // Byte code:
    //   0: ldc_w 518
    //   3: invokestatic 523	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   6: astore 9
    //   8: aload 9
    //   10: ifnonnull +4 -> 14
    //   13: return
    //   14: aload_0
    //   15: getfield 162	com/android/server/BatteryService:mContext	Landroid/content/Context;
    //   18: ldc_w 525
    //   21: invokevirtual 529	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   24: checkcast 531	android/os/DropBoxManager
    //   27: astore 10
    //   29: aload 10
    //   31: ifnull +102 -> 133
    //   34: aload 10
    //   36: ldc_w 533
    //   39: invokevirtual 537	android/os/DropBoxManager:isTagEnabled	(Ljava/lang/String;)Z
    //   42: ifeq +91 -> 133
    //   45: aconst_null
    //   46: astore 8
    //   48: aconst_null
    //   49: astore_2
    //   50: aconst_null
    //   51: astore 7
    //   53: aconst_null
    //   54: astore 6
    //   56: aconst_null
    //   57: astore_3
    //   58: aconst_null
    //   59: astore 5
    //   61: aconst_null
    //   62: astore 4
    //   64: new 300	java/io/File
    //   67: dup
    //   68: ldc_w 539
    //   71: invokespecial 305	java/io/File:<init>	(Ljava/lang/String;)V
    //   74: astore_1
    //   75: new 541	java/io/FileOutputStream
    //   78: dup
    //   79: aload_1
    //   80: invokespecial 542	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   83: astore_2
    //   84: aload 9
    //   86: aload_2
    //   87: invokevirtual 546	java/io/FileOutputStream:getFD	()Ljava/io/FileDescriptor;
    //   90: getstatic 230	com/android/server/BatteryService:DUMPSYS_ARGS	[Ljava/lang/String;
    //   93: invokeinterface 552 3 0
    //   98: aload_2
    //   99: invokestatic 558	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   102: pop
    //   103: aload 10
    //   105: ldc_w 533
    //   108: aload_1
    //   109: iconst_2
    //   110: invokevirtual 562	android/os/DropBoxManager:addFile	(Ljava/lang/String;Ljava/io/File;I)V
    //   113: aload_2
    //   114: ifnull +7 -> 121
    //   117: aload_2
    //   118: invokevirtual 563	java/io/FileOutputStream:close	()V
    //   121: aload_1
    //   122: ifnull +10 -> 132
    //   125: aload_1
    //   126: invokevirtual 566	java/io/File:delete	()Z
    //   129: ifeq +19 -> 148
    //   132: return
    //   133: return
    //   134: astore_2
    //   135: getstatic 131	com/android/server/BatteryService:TAG	Ljava/lang/String;
    //   138: ldc_w 568
    //   141: invokestatic 571	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   144: pop
    //   145: goto -24 -> 121
    //   148: getstatic 131	com/android/server/BatteryService:TAG	Ljava/lang/String;
    //   151: new 378	java/lang/StringBuilder
    //   154: dup
    //   155: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   158: ldc_w 573
    //   161: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: aload_1
    //   165: invokevirtual 576	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   168: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   171: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   174: invokestatic 571	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   177: pop
    //   178: goto -46 -> 132
    //   181: astore 5
    //   183: aload 7
    //   185: astore_1
    //   186: aload_1
    //   187: astore_2
    //   188: aload 4
    //   190: astore_3
    //   191: getstatic 131	com/android/server/BatteryService:TAG	Ljava/lang/String;
    //   194: ldc_w 578
    //   197: aload 5
    //   199: invokestatic 513	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   202: pop
    //   203: aload 4
    //   205: ifnull +8 -> 213
    //   208: aload 4
    //   210: invokevirtual 563	java/io/FileOutputStream:close	()V
    //   213: aload_1
    //   214: ifnull -82 -> 132
    //   217: aload_1
    //   218: invokevirtual 566	java/io/File:delete	()Z
    //   221: ifne -89 -> 132
    //   224: getstatic 131	com/android/server/BatteryService:TAG	Ljava/lang/String;
    //   227: new 378	java/lang/StringBuilder
    //   230: dup
    //   231: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   234: ldc_w 573
    //   237: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   240: aload_1
    //   241: invokevirtual 576	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   244: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   247: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   250: invokestatic 571	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   253: pop
    //   254: return
    //   255: astore_2
    //   256: getstatic 131	com/android/server/BatteryService:TAG	Ljava/lang/String;
    //   259: ldc_w 568
    //   262: invokestatic 571	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   265: pop
    //   266: goto -53 -> 213
    //   269: astore 5
    //   271: aload 6
    //   273: astore 4
    //   275: aload 8
    //   277: astore_1
    //   278: aload_1
    //   279: astore_2
    //   280: aload 4
    //   282: astore_3
    //   283: getstatic 131	com/android/server/BatteryService:TAG	Ljava/lang/String;
    //   286: ldc_w 580
    //   289: aload 5
    //   291: invokestatic 513	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   294: pop
    //   295: aload 4
    //   297: ifnull +8 -> 305
    //   300: aload 4
    //   302: invokevirtual 563	java/io/FileOutputStream:close	()V
    //   305: aload_1
    //   306: ifnull -174 -> 132
    //   309: aload_1
    //   310: invokevirtual 566	java/io/File:delete	()Z
    //   313: ifne -181 -> 132
    //   316: getstatic 131	com/android/server/BatteryService:TAG	Ljava/lang/String;
    //   319: new 378	java/lang/StringBuilder
    //   322: dup
    //   323: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   326: ldc_w 573
    //   329: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   332: aload_1
    //   333: invokevirtual 576	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   336: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   339: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   342: invokestatic 571	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   345: pop
    //   346: return
    //   347: astore_2
    //   348: getstatic 131	com/android/server/BatteryService:TAG	Ljava/lang/String;
    //   351: ldc_w 568
    //   354: invokestatic 571	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   357: pop
    //   358: goto -53 -> 305
    //   361: astore_1
    //   362: aload_3
    //   363: ifnull +7 -> 370
    //   366: aload_3
    //   367: invokevirtual 563	java/io/FileOutputStream:close	()V
    //   370: aload_2
    //   371: ifnull +10 -> 381
    //   374: aload_2
    //   375: invokevirtual 566	java/io/File:delete	()Z
    //   378: ifeq +19 -> 397
    //   381: aload_1
    //   382: athrow
    //   383: astore_3
    //   384: getstatic 131	com/android/server/BatteryService:TAG	Ljava/lang/String;
    //   387: ldc_w 568
    //   390: invokestatic 571	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   393: pop
    //   394: goto -24 -> 370
    //   397: getstatic 131	com/android/server/BatteryService:TAG	Ljava/lang/String;
    //   400: new 378	java/lang/StringBuilder
    //   403: dup
    //   404: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   407: ldc_w 573
    //   410: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   413: aload_2
    //   414: invokevirtual 576	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   417: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   420: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   423: invokestatic 571	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   426: pop
    //   427: goto -46 -> 381
    //   430: astore 4
    //   432: aload_1
    //   433: astore_2
    //   434: aload 5
    //   436: astore_3
    //   437: aload 4
    //   439: astore_1
    //   440: goto -78 -> 362
    //   443: astore 4
    //   445: aload_2
    //   446: astore_3
    //   447: aload_1
    //   448: astore_2
    //   449: aload 4
    //   451: astore_1
    //   452: goto -90 -> 362
    //   455: astore 5
    //   457: aload 6
    //   459: astore 4
    //   461: goto -183 -> 278
    //   464: astore 5
    //   466: aload_2
    //   467: astore 4
    //   469: goto -191 -> 278
    //   472: astore 5
    //   474: goto -288 -> 186
    //   477: astore 5
    //   479: aload_2
    //   480: astore 4
    //   482: goto -296 -> 186
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	485	0	this	BatteryService
    //   74	259	1	localObject1	Object
    //   361	72	1	localObject2	Object
    //   439	13	1	localObject3	Object
    //   49	69	2	localFileOutputStream	java.io.FileOutputStream
    //   134	1	2	localIOException1	IOException
    //   187	1	2	localObject4	Object
    //   255	1	2	localIOException2	IOException
    //   279	1	2	localObject5	Object
    //   347	67	2	localIOException3	IOException
    //   433	47	2	localObject6	Object
    //   57	310	3	localObject7	Object
    //   383	1	3	localIOException4	IOException
    //   436	11	3	localObject8	Object
    //   62	239	4	localObject9	Object
    //   430	8	4	localObject10	Object
    //   443	7	4	localObject11	Object
    //   459	22	4	localObject12	Object
    //   59	1	5	localObject13	Object
    //   181	17	5	localIOException5	IOException
    //   269	166	5	localRemoteException1	RemoteException
    //   455	1	5	localRemoteException2	RemoteException
    //   464	1	5	localRemoteException3	RemoteException
    //   472	1	5	localIOException6	IOException
    //   477	1	5	localIOException7	IOException
    //   54	404	6	localObject14	Object
    //   51	133	7	localObject15	Object
    //   46	230	8	localObject16	Object
    //   6	79	9	localIBinder	IBinder
    //   27	77	10	localDropBoxManager	android.os.DropBoxManager
    // Exception table:
    //   from	to	target	type
    //   117	121	134	java/io/IOException
    //   64	75	181	java/io/IOException
    //   208	213	255	java/io/IOException
    //   64	75	269	android/os/RemoteException
    //   300	305	347	java/io/IOException
    //   64	75	361	finally
    //   191	203	361	finally
    //   283	295	361	finally
    //   366	370	383	java/io/IOException
    //   75	84	430	finally
    //   84	113	443	finally
    //   75	84	455	android/os/RemoteException
    //   84	113	464	android/os/RemoteException
    //   75	84	472	java/io/IOException
    //   84	113	477	java/io/IOException
  }
  
  private void logOutlierLocked(long paramLong)
  {
    Object localObject = this.mContext.getContentResolver();
    String str = Settings.Global.getString((ContentResolver)localObject, "battery_discharge_threshold");
    localObject = Settings.Global.getString((ContentResolver)localObject, "battery_discharge_duration_threshold");
    if ((str != null) && (localObject != null)) {}
    try
    {
      long l = Long.parseLong((String)localObject);
      int i = Integer.parseInt(str);
      if ((paramLong <= l) && (this.mDischargeStartLevel - this.mBatteryProps.batteryLevel >= i)) {
        logBatteryStatsLocked();
      }
      return;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Slog.e(TAG, "Invalid DischargeThresholds GService string: " + (String)localObject + " or " + str);
    }
  }
  
  private void processValuesLocked(boolean paramBoolean)
  {
    int j = 0;
    long l2 = 0L;
    boolean bool;
    if (this.mBatteryProps.batteryLevel <= this.mCriticalBatteryLevel) {
      bool = true;
    }
    for (;;)
    {
      this.mBatteryLevelCritical = bool;
      if (this.mBatteryProps.chargerAcOnline)
      {
        this.mPlugType = 1;
        label43:
        if (!this.mIsFastChargeSupport) {
          break label852;
        }
        this.mFastChargeStatus = isFastCharge();
      }
      try
      {
        label58:
        this.mBatteryStats.setBatteryState(this.mBatteryProps.batteryStatus, this.mBatteryProps.batteryHealth, this.mPlugType, this.mBatteryProps.batteryLevel, this.mBatteryProps.batteryTemperature, this.mBatteryProps.batteryVoltage, this.mBatteryProps.batteryChargeCounter);
        if ((this.mBatteryProps.batteryLevel == 0) && (mWeakChgSocCheckStarted == 0) && (this.mWeakChgCutoffVoltageMv > 0) && (this.mPlugType != 0))
        {
          mWeakChgSocCheckStarted = 1;
          this.mHandler.removeCallbacks(this.runnable);
          this.mHandler.postDelayed(this.runnable, 30000L);
        }
        shutdownIfNoPowerLocked();
        shutdownIfOverTempLocked();
        label213:
        long l1;
        int i;
        if ((paramBoolean) || (this.mBatteryProps.batteryStatus != this.mLastBatteryStatus) || (this.mBatteryProps.batteryHealth != this.mLastBatteryHealth))
        {
          l1 = l2;
          i = j;
          if (this.mPlugType != this.mLastPlugType)
          {
            if (this.mLastPlugType != 0) {
              break label994;
            }
            new Thread(new Runnable()
            {
              public void run()
              {
                ((Vibrator)BatteryService.-get5(BatteryService.this).getSystemService("vibrator")).vibrate(new long[] { 0L, 10L, 150L, 12L }, -1);
              }
            }).start();
            l1 = l2;
            i = j;
            if (this.mDischargeStartTime != 0L)
            {
              l1 = l2;
              i = j;
              if (this.mDischargeStartLevel != this.mBatteryProps.batteryLevel)
              {
                l1 = SystemClock.elapsedRealtime() - this.mDischargeStartTime;
                i = 1;
                EventLog.writeEvent(2730, new Object[] { Long.valueOf(l1), Integer.valueOf(this.mDischargeStartLevel), Integer.valueOf(this.mBatteryProps.batteryLevel) });
                this.mDischargeStartTime = 0L;
              }
            }
          }
          label349:
          if ((this.mBatteryProps.batteryStatus == this.mLastBatteryStatus) && (this.mBatteryProps.batteryHealth == this.mLastBatteryHealth)) {
            break label1034;
          }
          label377:
          int k = this.mBatteryProps.batteryStatus;
          int m = this.mBatteryProps.batteryHealth;
          if (!this.mBatteryProps.batteryPresent) {
            break label1062;
          }
          j = 1;
          label407:
          EventLog.writeEvent(2723, new Object[] { Integer.valueOf(k), Integer.valueOf(m), Integer.valueOf(j), Integer.valueOf(this.mPlugType), this.mBatteryProps.batteryTechnology });
          label461:
          if (this.mBatteryProps.batteryLevel != this.mLastBatteryLevel) {
            EventLog.writeEvent(2722, new Object[] { Integer.valueOf(this.mBatteryProps.batteryLevel), Integer.valueOf(this.mBatteryProps.batteryVoltage), Integer.valueOf(this.mBatteryProps.batteryTemperature) });
          }
          l2 = l1;
          j = i;
          if (this.mBatteryLevelCritical)
          {
            if (!this.mLastBatteryLevelCritical) {
              break label1067;
            }
            j = i;
            l2 = l1;
          }
          label551:
          if (this.mBatteryLevelLow) {
            break label1095;
          }
          if ((this.mPlugType == 0) && (this.mBatteryProps.batteryLevel <= this.mLowBatteryWarningLevel)) {
            this.mBatteryLevelLow = true;
          }
          label584:
          sendIntentLocked();
          if ((this.mPlugType == 0) || (this.mLastPlugType != 0)) {
            break label1158;
          }
          this.mHandler.post(new Runnable()
          {
            public void run()
            {
              Intent localIntent = new Intent("android.intent.action.ACTION_POWER_CONNECTED");
              localIntent.setFlags(67108864);
              BatteryService.-get5(BatteryService.this).sendBroadcastAsUser(localIntent, UserHandle.ALL);
            }
          });
          label618:
          if (!shouldSendBatteryLowLocked()) {
            break label1191;
          }
          this.mSentLowBatteryBroadcast = true;
          this.mHandler.post(new Runnable()
          {
            public void run()
            {
              Intent localIntent = new Intent("android.intent.action.BATTERY_LOW");
              localIntent.setFlags(67108864);
              BatteryService.-get5(BatteryService.this).sendBroadcastAsUser(localIntent, UserHandle.ALL);
            }
          });
        }
        for (;;)
        {
          this.mLed.updateLightsLocked();
          if ((j != 0) && (l2 != 0L)) {
            logOutlierLocked(l2);
          }
          this.mLastBatteryStatus = this.mBatteryProps.batteryStatus;
          this.mLastBatteryHealth = this.mBatteryProps.batteryHealth;
          this.mLastBatteryPresent = this.mBatteryProps.batteryPresent;
          this.mLastBatteryLevel = this.mBatteryProps.batteryLevel;
          this.mLastPlugType = this.mPlugType;
          this.mLastBatteryVoltage = this.mBatteryProps.batteryVoltage;
          this.mLastBatteryTemperature = this.mBatteryProps.batteryTemperature;
          this.mLastMaxChargingCurrent = this.mBatteryProps.maxChargingCurrent;
          this.mLastMaxChargingVoltage = this.mBatteryProps.maxChargingVoltage;
          this.mLastChargeCounter = this.mBatteryProps.batteryChargeCounter;
          this.mLastBatteryLevelCritical = this.mBatteryLevelCritical;
          this.mLastInvalidCharger = this.mInvalidCharger;
          this.mLastFastChargeStatus = this.mFastChargeStatus;
          label852:
          do
          {
            return;
            bool = false;
            break;
            if (this.mBatteryProps.chargerUsbOnline)
            {
              this.mPlugType = 2;
              break label43;
            }
            if (this.mBatteryProps.chargerWirelessOnline)
            {
              this.mPlugType = 4;
              break label43;
            }
            this.mPlugType = 0;
            break label43;
            this.mFastChargeStatus = false;
            break label58;
            if ((this.mBatteryProps.batteryPresent != this.mLastBatteryPresent) || (this.mBatteryProps.batteryLevel != this.mLastBatteryLevel) || (this.mPlugType != this.mLastPlugType) || (this.mBatteryProps.batteryVoltage != this.mLastBatteryVoltage) || (this.mBatteryProps.batteryTemperature != this.mLastBatteryTemperature) || (this.mBatteryProps.maxChargingCurrent != this.mLastMaxChargingCurrent) || (this.mBatteryProps.maxChargingVoltage != this.mLastMaxChargingVoltage) || (this.mBatteryProps.batteryChargeCounter != this.mLastChargeCounter) || (this.mInvalidCharger != this.mLastInvalidCharger)) {
              break label213;
            }
          } while (this.mFastChargeStatus == this.mLastFastChargeStatus);
          break label213;
          label994:
          l1 = l2;
          i = j;
          if (this.mPlugType != 0) {
            break label349;
          }
          this.mDischargeStartTime = SystemClock.elapsedRealtime();
          this.mDischargeStartLevel = this.mBatteryProps.batteryLevel;
          l1 = l2;
          i = j;
          break label349;
          label1034:
          if (this.mBatteryProps.batteryPresent != this.mLastBatteryPresent) {
            break label377;
          }
          if (this.mPlugType == this.mLastPlugType) {
            break label461;
          }
          break label377;
          label1062:
          j = 0;
          break label407;
          label1067:
          l2 = l1;
          j = i;
          if (this.mPlugType != 0) {
            break label551;
          }
          l2 = SystemClock.elapsedRealtime() - this.mDischargeStartTime;
          j = 1;
          break label551;
          label1095:
          if (this.mPlugType != 0)
          {
            this.mBatteryLevelLow = false;
            break label584;
          }
          if (this.mBatteryProps.batteryLevel >= this.mLowBatteryCloseWarningLevel)
          {
            this.mBatteryLevelLow = false;
            break label584;
          }
          if ((!paramBoolean) || (this.mBatteryProps.batteryLevel < this.mLowBatteryWarningLevel)) {
            break label584;
          }
          this.mBatteryLevelLow = false;
          break label584;
          label1158:
          if ((this.mPlugType != 0) || (this.mLastPlugType == 0)) {
            break label618;
          }
          this.mHandler.post(new Runnable()
          {
            public void run()
            {
              Intent localIntent = new Intent("android.intent.action.ACTION_POWER_DISCONNECTED");
              localIntent.setFlags(67108864);
              BatteryService.-get5(BatteryService.this).sendBroadcastAsUser(localIntent, UserHandle.ALL);
            }
          });
          break label618;
          label1191:
          if ((this.mSentLowBatteryBroadcast) && (this.mLastBatteryLevel >= this.mLowBatteryCloseWarningLevel))
          {
            this.mSentLowBatteryBroadcast = false;
            this.mHandler.post(new Runnable()
            {
              public void run()
              {
                Intent localIntent = new Intent("android.intent.action.BATTERY_OKAY");
                localIntent.setFlags(67108864);
                BatteryService.-get5(BatteryService.this).sendBroadcastAsUser(localIntent, UserHandle.ALL);
              }
            });
          }
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  private void sendIntentLocked()
  {
    final Intent localIntent = new Intent("android.intent.action.BATTERY_CHANGED");
    localIntent.addFlags(1610612736);
    int i = getIconLocked(this.mBatteryProps.batteryLevel);
    localIntent.putExtra("status", this.mBatteryProps.batteryStatus);
    localIntent.putExtra("health", this.mBatteryProps.batteryHealth);
    localIntent.putExtra("present", this.mBatteryProps.batteryPresent);
    localIntent.putExtra("level", this.mBatteryProps.batteryLevel);
    localIntent.putExtra("scale", 100);
    localIntent.putExtra("icon-small", i);
    localIntent.putExtra("plugged", this.mPlugType);
    localIntent.putExtra("voltage", this.mBatteryProps.batteryVoltage);
    localIntent.putExtra("temperature", this.mBatteryProps.batteryTemperature);
    localIntent.putExtra("technology", this.mBatteryProps.batteryTechnology);
    localIntent.putExtra("invalid_charger", this.mInvalidCharger);
    localIntent.putExtra("max_charging_current", this.mBatteryProps.maxChargingCurrent);
    localIntent.putExtra("max_charging_voltage", this.mBatteryProps.maxChargingVoltage);
    localIntent.putExtra("charge_counter", this.mBatteryProps.batteryChargeCounter);
    localIntent.putExtra("fastcharge_status", this.mFastChargeStatus);
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        ActivityManagerNative.broadcastStickyIntent(localIntent, null, -1);
      }
    });
  }
  
  private boolean shouldSendBatteryLowLocked()
  {
    int i;
    int j;
    if (this.mPlugType != 0)
    {
      i = 1;
      if (this.mLastPlugType == 0) {
        break label69;
      }
      j = 1;
    }
    for (;;)
    {
      if ((i == 0) && (this.mBatteryProps.batteryStatus != 1) && (this.mBatteryProps.batteryLevel <= this.mLowBatteryWarningLevel))
      {
        if ((j != 0) || (this.mLastBatteryLevel > this.mLowBatteryWarningLevel))
        {
          return true;
          i = 0;
          break;
          label69:
          j = 0;
          continue;
        }
        return false;
      }
    }
    return false;
  }
  
  private void shutdownIfNoPowerLocked()
  {
    if ((this.mBatteryProps.batteryLevel != 0) || (isPoweredLocked(7))) {
      return;
    }
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        if (ActivityManagerNative.isSystemReady())
        {
          Slog.w(BatteryService.-get0(), "No power, try to shutdown.");
          Intent localIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
          localIntent.putExtra("android.intent.extra.KEY_CONFIRM", false);
          localIntent.setFlags(268435456);
          BatteryService.-get5(BatteryService.this).startActivityAsUser(localIntent, UserHandle.CURRENT);
        }
      }
    });
  }
  
  private void shutdownIfOverTempLocked()
  {
    if (this.mBatteryProps.batteryTemperature > this.mShutdownBatteryTemperature) {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          if (ActivityManagerNative.isSystemReady())
          {
            Slog.i(BatteryService.-get0(), "Tbat=" + BatteryService.-get3(BatteryService.this).batteryTemperature);
            Slog.w(BatteryService.-get0(), "Battery over temperature, try to shutdown.");
            Intent localIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
            localIntent.putExtra("android.intent.extra.KEY_CONFIRM", false);
            localIntent.setFlags(268435456);
            BatteryService.-get5(BatteryService.this).startActivityAsUser(localIntent, UserHandle.CURRENT);
          }
        }
      });
    }
  }
  
  private void shutdownIfWeakChargerEmptySOCLocked()
  {
    if (this.mBatteryProps.batteryLevel == 0)
    {
      if (this.mInitiateShutdown)
      {
        if (ActivityManagerNative.isSystemReady())
        {
          Slog.e(TAG, "silent_reboot shutdownIfWeakChargerEmptySOCLocked");
          Intent localIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
          localIntent.putExtra("android.intent.extra.KEY_CONFIRM", false);
          localIntent.setFlags(268435456);
          this.mContext.startActivityAsUser(localIntent, UserHandle.CURRENT);
        }
        return;
      }
      this.mInitiateShutdown = true;
      this.mHandler.removeCallbacks(this.runnable);
      this.mHandler.postDelayed(this.runnable, 300000L);
      return;
    }
    this.mInitiateShutdown = false;
    mWeakChgSocCheckStarted = 0;
  }
  
  private void shutdownIfWeakChargerVoltageCheckLocked()
  {
    int m = 0;
    int k = 0;
    int i = k;
    int j = m;
    try
    {
      localObject = new FileReader(this.mVoltageNowFile);
      i = k;
      j = m;
      BufferedReader localBufferedReader = new BufferedReader((Reader)localObject);
      i = k;
      j = m;
      k = Integer.parseInt(localBufferedReader.readLine());
      i = k;
      j = k;
      k /= 1000;
      i = k;
      j = k;
      localBufferedReader.close();
      i = k;
      j = k;
      ((FileReader)localObject).close();
      i = k;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Object localObject;
        Slog.e(TAG, "Failure in reading battery voltage", localIOException);
      }
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      for (;;)
      {
        Slog.e(TAG, "Failure in reading battery voltage", localFileNotFoundException);
        i = j;
      }
      this.mHandler.removeCallbacks(this.runnable);
      this.mHandler.postDelayed(this.runnable, 30000L);
      return;
    }
    if (this.mBatteryProps.batteryLevel == 0) {
      if (i <= this.mWeakChgCutoffVoltageMv)
      {
        if (ActivityManagerNative.isSystemReady())
        {
          Slog.e(TAG, "silent_reboot shutdownIfWeakChargerVoltageCheckLocked");
          localObject = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
          ((Intent)localObject).putExtra("android.intent.extra.KEY_CONFIRM", false);
          ((Intent)localObject).setFlags(268435456);
          this.mContext.startActivityAsUser((Intent)localObject, UserHandle.CURRENT);
        }
        return;
      }
    }
    mWeakChgSocCheckStarted = 0;
  }
  
  private void update(BatteryProperties paramBatteryProperties)
  {
    synchronized (this.mLock)
    {
      if (!this.mUpdatesStopped)
      {
        this.mBatteryProps = paramBatteryProperties;
        processValuesLocked(false);
        return;
      }
      this.mLastBatteryProps.set(paramBatteryProperties);
    }
  }
  
  private void updateBatteryLedColors()
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    int i = Settings.System.getInt(localContentResolver, "battery_light_low_color", this.mContext.getResources().getInteger(17694812));
    int j = Settings.System.getInt(localContentResolver, "battery_light_medium_color", this.mContext.getResources().getInteger(17694813));
    int k = Settings.System.getInt(localContentResolver, "battery_light_full_color", this.mContext.getResources().getInteger(17694814));
    this.mLed.setLedColors(i, j, k);
    this.mLed.updateLightsLocked();
  }
  
  private void updateBatteryWarningLevelLocked()
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    int i = this.mContext.getResources().getInteger(17694808);
    this.mLowBatteryWarningLevel = Settings.Global.getInt(localContentResolver, "low_power_trigger_level", i);
    if (this.mLowBatteryWarningLevel == 0) {
      this.mLowBatteryWarningLevel = i;
    }
    if (this.mLowBatteryWarningLevel < this.mCriticalBatteryLevel) {
      this.mLowBatteryWarningLevel = this.mCriticalBatteryLevel;
    }
    this.mLowBatteryCloseWarningLevel = (this.mLowBatteryWarningLevel + this.mContext.getResources().getInteger(17694809));
    processValuesLocked(true);
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 550) {}
    for (;;)
    {
      synchronized (this.mLock)
      {
        Object localObject2 = new ContentObserver(this.mHandler)
        {
          public void onChange(boolean paramAnonymousBoolean)
          {
            synchronized (BatteryService.-get10(BatteryService.this))
            {
              BatteryService.-wrap5(BatteryService.this);
              return;
            }
          }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("low_power_trigger_level"), false, (ContentObserver)localObject2, -1);
        updateBatteryWarningLevelLocked();
        localObject2 = new Handler();
        new SettingsObserver(this, (Handler)localObject2, "battery_led_low_power")
        {
          void onUpdate(boolean paramAnonymousBoolean)
          {
            if (BatteryService.-get2(jdField_this) != paramAnonymousBoolean)
            {
              BatteryService.-set0(jdField_this, paramAnonymousBoolean);
              BatteryService.-get9(jdField_this).updateLightsLocked();
            }
          }
        };
        new SettingsObserver(this, (Handler)localObject2, "battery_led_charging")
        {
          void onUpdate(boolean paramAnonymousBoolean)
          {
            if (BatteryService.-get4(jdField_this) != paramAnonymousBoolean)
            {
              BatteryService.-set1(jdField_this, paramAnonymousBoolean);
              BatteryService.-get9(jdField_this).updateLightsLocked();
            }
          }
        };
        return;
      }
      if (paramInt != 1000) {
        continue;
      }
      synchronized (this.mLock)
      {
        ContentObserver local6 = new ContentObserver(this.mHandler)
        {
          public void onChange(boolean paramAnonymousBoolean)
          {
            synchronized (BatteryService.-get10(BatteryService.this))
            {
              BatteryService.-wrap4(BatteryService.this);
              return;
            }
          }
        };
        ContentResolver localContentResolver = this.mContext.getContentResolver();
        localContentResolver.registerContentObserver(Settings.System.getUriFor("battery_light_low_color"), false, local6, -1);
        localContentResolver.registerContentObserver(Settings.System.getUriFor("battery_light_medium_color"), false, local6, -1);
        localContentResolver.registerContentObserver(Settings.System.getUriFor("battery_light_full_color"), false, local6, -1);
        updateBatteryLedColors();
      }
    }
  }
  
  /* Error */
  int onShellCommand(Shell paramShell, String paramString)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 5
    //   3: iconst_1
    //   4: istore 6
    //   6: iconst_1
    //   7: istore 4
    //   9: aload_2
    //   10: ifnonnull +9 -> 19
    //   13: aload_1
    //   14: aload_2
    //   15: invokevirtual 860	com/android/server/BatteryService$Shell:handleDefaultCommands	(Ljava/lang/String;)I
    //   18: ireturn
    //   19: aload_1
    //   20: invokevirtual 864	com/android/server/BatteryService$Shell:getOutPrintWriter	()Ljava/io/PrintWriter;
    //   23: astore 9
    //   25: aload_2
    //   26: ldc_w 866
    //   29: invokevirtual 465	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   32: ifeq +78 -> 110
    //   35: aload_0
    //   36: invokevirtual 870	com/android/server/BatteryService:getContext	()Landroid/content/Context;
    //   39: ldc_w 872
    //   42: aconst_null
    //   43: invokevirtual 876	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   46: aload_0
    //   47: getfield 374	com/android/server/BatteryService:mUpdatesStopped	Z
    //   50: ifne +14 -> 64
    //   53: aload_0
    //   54: getfield 245	com/android/server/BatteryService:mLastBatteryProps	Landroid/os/BatteryProperties;
    //   57: aload_0
    //   58: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   61: invokevirtual 808	android/os/BatteryProperties:set	(Landroid/os/BatteryProperties;)V
    //   64: aload_0
    //   65: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   68: iconst_0
    //   69: putfield 388	android/os/BatteryProperties:chargerAcOnline	Z
    //   72: aload_0
    //   73: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   76: iconst_0
    //   77: putfield 399	android/os/BatteryProperties:chargerUsbOnline	Z
    //   80: aload_0
    //   81: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   84: iconst_0
    //   85: putfield 404	android/os/BatteryProperties:chargerWirelessOnline	Z
    //   88: invokestatic 881	android/os/Binder:clearCallingIdentity	()J
    //   91: lstore 7
    //   93: aload_0
    //   94: iconst_1
    //   95: putfield 374	com/android/server/BatteryService:mUpdatesStopped	Z
    //   98: aload_0
    //   99: iconst_0
    //   100: invokespecial 805	com/android/server/BatteryService:processValuesLocked	(Z)V
    //   103: lload 7
    //   105: invokestatic 884	android/os/Binder:restoreCallingIdentity	(J)V
    //   108: iconst_0
    //   109: ireturn
    //   110: aload_2
    //   111: ldc_w 885
    //   114: invokevirtual 465	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   117: ifeq +33 -> 150
    //   120: aload_0
    //   121: invokevirtual 870	com/android/server/BatteryService:getContext	()Landroid/content/Context;
    //   124: ldc_w 872
    //   127: aconst_null
    //   128: invokevirtual 876	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   131: aload_1
    //   132: invokevirtual 888	com/android/server/BatteryService$Shell:getNextArg	()Ljava/lang/String;
    //   135: astore_2
    //   136: aload_2
    //   137: ifnonnull +82 -> 219
    //   140: aload 9
    //   142: ldc_w 890
    //   145: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   148: iconst_m1
    //   149: ireturn
    //   150: aload_2
    //   151: ldc_w 892
    //   154: invokevirtual 465	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   157: ifeq +368 -> 525
    //   160: aload_0
    //   161: invokevirtual 870	com/android/server/BatteryService:getContext	()Landroid/content/Context;
    //   164: ldc_w 872
    //   167: aconst_null
    //   168: invokevirtual 876	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   171: invokestatic 881	android/os/Binder:clearCallingIdentity	()J
    //   174: lstore 7
    //   176: aload_0
    //   177: getfield 374	com/android/server/BatteryService:mUpdatesStopped	Z
    //   180: ifeq +24 -> 204
    //   183: aload_0
    //   184: iconst_0
    //   185: putfield 374	com/android/server/BatteryService:mUpdatesStopped	Z
    //   188: aload_0
    //   189: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   192: aload_0
    //   193: getfield 245	com/android/server/BatteryService:mLastBatteryProps	Landroid/os/BatteryProperties;
    //   196: invokevirtual 808	android/os/BatteryProperties:set	(Landroid/os/BatteryProperties;)V
    //   199: aload_0
    //   200: iconst_0
    //   201: invokespecial 805	com/android/server/BatteryService:processValuesLocked	(Z)V
    //   204: lload 7
    //   206: invokestatic 884	android/os/Binder:restoreCallingIdentity	(J)V
    //   209: iconst_0
    //   210: ireturn
    //   211: astore_1
    //   212: lload 7
    //   214: invokestatic 884	android/os/Binder:restoreCallingIdentity	(J)V
    //   217: aload_1
    //   218: athrow
    //   219: aload_1
    //   220: invokevirtual 888	com/android/server/BatteryService$Shell:getNextArg	()Ljava/lang/String;
    //   223: astore_1
    //   224: aload_1
    //   225: ifnonnull +13 -> 238
    //   228: aload 9
    //   230: ldc_w 894
    //   233: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   236: iconst_m1
    //   237: ireturn
    //   238: aload_0
    //   239: getfield 374	com/android/server/BatteryService:mUpdatesStopped	Z
    //   242: ifne +14 -> 256
    //   245: aload_0
    //   246: getfield 245	com/android/server/BatteryService:mLastBatteryProps	Landroid/os/BatteryProperties;
    //   249: aload_0
    //   250: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   253: invokevirtual 808	android/os/BatteryProperties:set	(Landroid/os/BatteryProperties;)V
    //   256: iconst_1
    //   257: istore_3
    //   258: aload_2
    //   259: ldc_w 896
    //   262: invokevirtual 465	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   265: ifeq +75 -> 340
    //   268: aload_0
    //   269: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   272: astore_2
    //   273: aload_1
    //   274: invokestatic 610	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   277: ifeq +254 -> 531
    //   280: aload_2
    //   281: iload 4
    //   283: putfield 388	android/os/BatteryProperties:chargerAcOnline	Z
    //   286: iload_3
    //   287: ifeq -179 -> 108
    //   290: invokestatic 881	android/os/Binder:clearCallingIdentity	()J
    //   293: lstore 7
    //   295: aload_0
    //   296: iconst_1
    //   297: putfield 374	com/android/server/BatteryService:mUpdatesStopped	Z
    //   300: aload_0
    //   301: iconst_0
    //   302: invokespecial 805	com/android/server/BatteryService:processValuesLocked	(Z)V
    //   305: lload 7
    //   307: invokestatic 884	android/os/Binder:restoreCallingIdentity	(J)V
    //   310: iconst_0
    //   311: ireturn
    //   312: astore_2
    //   313: aload 9
    //   315: new 378	java/lang/StringBuilder
    //   318: dup
    //   319: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   322: ldc_w 898
    //   325: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   328: aload_1
    //   329: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   332: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   335: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   338: iconst_m1
    //   339: ireturn
    //   340: aload_2
    //   341: ldc_w 900
    //   344: invokevirtual 465	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   347: ifeq +28 -> 375
    //   350: aload_0
    //   351: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   354: astore_2
    //   355: aload_1
    //   356: invokestatic 610	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   359: ifeq +178 -> 537
    //   362: iload 5
    //   364: istore 4
    //   366: aload_2
    //   367: iload 4
    //   369: putfield 399	android/os/BatteryProperties:chargerUsbOnline	Z
    //   372: goto -86 -> 286
    //   375: aload_2
    //   376: ldc_w 902
    //   379: invokevirtual 465	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   382: ifeq +28 -> 410
    //   385: aload_0
    //   386: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   389: astore_2
    //   390: aload_1
    //   391: invokestatic 610	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   394: ifeq +149 -> 543
    //   397: iload 6
    //   399: istore 4
    //   401: aload_2
    //   402: iload 4
    //   404: putfield 404	android/os/BatteryProperties:chargerWirelessOnline	Z
    //   407: goto -121 -> 286
    //   410: aload_2
    //   411: ldc_w 729
    //   414: invokevirtual 465	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   417: ifeq +17 -> 434
    //   420: aload_0
    //   421: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   424: aload_1
    //   425: invokestatic 610	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   428: putfield 427	android/os/BatteryProperties:batteryStatus	I
    //   431: goto -145 -> 286
    //   434: aload_2
    //   435: ldc_w 742
    //   438: invokevirtual 465	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   441: ifeq +17 -> 458
    //   444: aload_0
    //   445: getfield 155	com/android/server/BatteryService:mBatteryProps	Landroid/os/BatteryProperties;
    //   448: aload_1
    //   449: invokestatic 610	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   452: putfield 442	android/os/BatteryProperties:batteryLevel	I
    //   455: goto -169 -> 286
    //   458: aload_2
    //   459: ldc_w 904
    //   462: invokevirtual 465	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   465: ifeq +14 -> 479
    //   468: aload_0
    //   469: aload_1
    //   470: invokestatic 610	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   473: putfield 171	com/android/server/BatteryService:mInvalidCharger	I
    //   476: goto -190 -> 286
    //   479: aload 9
    //   481: new 378	java/lang/StringBuilder
    //   484: dup
    //   485: invokespecial 379	java/lang/StringBuilder:<init>	()V
    //   488: ldc_w 906
    //   491: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   494: aload_2
    //   495: invokevirtual 385	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   498: invokevirtual 394	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   501: invokevirtual 354	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   504: iconst_0
    //   505: istore_3
    //   506: goto -220 -> 286
    //   509: astore_2
    //   510: lload 7
    //   512: invokestatic 884	android/os/Binder:restoreCallingIdentity	(J)V
    //   515: aload_2
    //   516: athrow
    //   517: astore_1
    //   518: lload 7
    //   520: invokestatic 884	android/os/Binder:restoreCallingIdentity	(J)V
    //   523: aload_1
    //   524: athrow
    //   525: aload_1
    //   526: aload_2
    //   527: invokevirtual 860	com/android/server/BatteryService$Shell:handleDefaultCommands	(Ljava/lang/String;)I
    //   530: ireturn
    //   531: iconst_0
    //   532: istore 4
    //   534: goto -254 -> 280
    //   537: iconst_0
    //   538: istore 4
    //   540: goto -174 -> 366
    //   543: iconst_0
    //   544: istore 4
    //   546: goto -145 -> 401
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	549	0	this	BatteryService
    //   0	549	1	paramShell	Shell
    //   0	549	2	paramString	String
    //   257	249	3	i	int
    //   7	538	4	bool1	boolean
    //   1	362	5	bool2	boolean
    //   4	394	6	bool3	boolean
    //   91	428	7	l	long
    //   23	457	9	localPrintWriter	PrintWriter
    // Exception table:
    //   from	to	target	type
    //   93	103	211	finally
    //   238	256	312	java/lang/NumberFormatException
    //   258	280	312	java/lang/NumberFormatException
    //   280	286	312	java/lang/NumberFormatException
    //   290	295	312	java/lang/NumberFormatException
    //   305	310	312	java/lang/NumberFormatException
    //   340	362	312	java/lang/NumberFormatException
    //   366	372	312	java/lang/NumberFormatException
    //   375	397	312	java/lang/NumberFormatException
    //   401	407	312	java/lang/NumberFormatException
    //   410	431	312	java/lang/NumberFormatException
    //   434	455	312	java/lang/NumberFormatException
    //   458	476	312	java/lang/NumberFormatException
    //   479	504	312	java/lang/NumberFormatException
    //   510	517	312	java/lang/NumberFormatException
    //   295	305	509	finally
    //   176	204	517	finally
  }
  
  public void onStart()
  {
    IBinder localIBinder = ServiceManager.getService("batteryproperties");
    IBatteryPropertiesRegistrar localIBatteryPropertiesRegistrar2 = IBatteryPropertiesRegistrar.Stub.asInterface(localIBinder);
    IBatteryPropertiesRegistrar localIBatteryPropertiesRegistrar1 = localIBatteryPropertiesRegistrar2;
    if (localIBatteryPropertiesRegistrar2 == null) {
      SystemProperties.set("ctl.restart", "healthd");
    }
    try
    {
      Thread.sleep(1000L);
      Slog.e(TAG, "restart healthd services to stop system_server crash");
      localIBatteryPropertiesRegistrar1 = IBatteryPropertiesRegistrar.Stub.asInterface(localIBinder);
    }
    catch (InterruptedException localInterruptedException)
    {
      try
      {
        localIBatteryPropertiesRegistrar1.registerListener(new BatteryListener(null));
        this.mBinderService = new BinderService(null);
        publishBinderService("battery", this.mBinderService);
        publishLocalService(BatteryManagerInternal.class, new LocalService(null));
        return;
        localInterruptedException = localInterruptedException;
        localInterruptedException.printStackTrace();
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  private final class BatteryListener
    extends IBatteryPropertiesListener.Stub
  {
    private BatteryListener() {}
    
    public void batteryPropertiesChanged(BatteryProperties paramBatteryProperties)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        BatteryService.-wrap6(BatteryService.this, paramBatteryProperties);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  private final class BinderService
    extends Binder
  {
    private BinderService() {}
    
    protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if (BatteryService.-get5(BatteryService.this).checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramPrintWriter.println("Permission Denial: can't dump Battery service from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      BatteryService.-wrap1(BatteryService.this, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
    {
      new BatteryService.Shell(BatteryService.this).exec(this, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver);
    }
  }
  
  private final class Led
  {
    private static final int DELAY_UPDATE_LIGHT = 500;
    private static final int MSG_UPDATE_LIGHT = 1;
    private int mBatteryFastChargeARGB;
    private int mBatteryFullARGB;
    private final int mBatteryLedOff;
    private final int mBatteryLedOn;
    private final Light mBatteryLight;
    private int mBatteryLowARGB;
    private int mBatteryMediumARGB;
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        int i = 0;
        paramAnonymousContext = paramAnonymousIntent.getAction();
        if (paramAnonymousContext.equals("android.intent.action.SCREEN_ON"))
        {
          BatteryService.Led.-set0(BatteryService.Led.this, true);
          BatteryService.Led.-get0(BatteryService.Led.this).removeMessages(1);
          BatteryService.Led.this.updateLightsLocked();
        }
        while (!paramAnonymousContext.equals("android.intent.action.SCREEN_OFF")) {
          return;
        }
        BatteryService.Led.-set0(BatteryService.Led.this, false);
        if (BatteryService.-get11(BatteryService.this) != 0) {
          i = 1;
        }
        if (i != 0)
        {
          BatteryService.Led.-get0(BatteryService.Led.this).sendEmptyMessageDelayed(1, 500L);
          return;
        }
        BatteryService.Led.-get0(BatteryService.Led.this).sendEmptyMessage(1);
      }
    };
    private Handler mLightHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        BatteryService.Led.this.updateLightsLocked();
      }
    };
    private boolean mScreenOn;
    
    public Led(Context paramContext, LightsManager paramLightsManager)
    {
      this.mBatteryLight = paramLightsManager.getLight(3);
      this.mBatteryLowARGB = paramContext.getResources().getInteger(17694812);
      this.mBatteryMediumARGB = paramContext.getResources().getInteger(17694813);
      this.mBatteryFullARGB = paramContext.getResources().getInteger(17694814);
      this.mBatteryLedOn = paramContext.getResources().getInteger(17694815);
      this.mBatteryLedOff = paramContext.getResources().getInteger(17694816);
      this.mBatteryFastChargeARGB = paramContext.getResources().getInteger(84475914);
      this$1 = new IntentFilter();
      BatteryService.this.addAction("android.intent.action.SCREEN_ON");
      BatteryService.this.addAction("android.intent.action.SCREEN_OFF");
      paramContext.registerReceiver(this.mIntentReceiver, BatteryService.this);
      this.mScreenOn = true;
    }
    
    private boolean isHvdcpPresent()
    {
      Object localObject = new File("/sys/class/power_supply/usb/type");
      try
      {
        localObject = new FileReader((File)localObject);
        BufferedReader localBufferedReader = new BufferedReader((Reader)localObject);
        if (localBufferedReader.readLine().regionMatches(true, 0, "USB_HVDCP", 0, 9)) {}
        for (boolean bool = true;; bool = false)
        {
          localBufferedReader.close();
          ((FileReader)localObject).close();
          return bool;
        }
        return false;
      }
      catch (IOException localIOException)
      {
        Slog.e(BatteryService.-get0(), "Failure in reading charger type", localIOException);
        return false;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        Slog.e(BatteryService.-get0(), "Failure in reading charger type", localFileNotFoundException);
      }
    }
    
    public void setLedColors(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mBatteryLowARGB = paramInt1;
      this.mBatteryMediumARGB = paramInt2;
      this.mBatteryFullARGB = paramInt3;
      Slog.d("BatteryLed", "mBatteryLowARGB = " + this.mBatteryLowARGB + "mBatteryMediumARGB = " + this.mBatteryMediumARGB + "mBatteryFullARGB = " + this.mBatteryFullARGB);
    }
    
    public void updateLightsLocked()
    {
      if (BatteryService.-get3(BatteryService.this) == null) {
        return;
      }
      int j;
      Light localLight;
      if ((BatteryService.-get2(BatteryService.this)) && (BatteryService.-get4(BatteryService.this)))
      {
        i = BatteryService.-get3(BatteryService.this).batteryLevel;
        j = BatteryService.-get3(BatteryService.this).batteryStatus;
        if (i > BatteryService.-get6(BatteryService.this)) {
          break label147;
        }
        if (j != 2) {
          break label111;
        }
        localLight = this.mBatteryLight;
        if (!BatteryService.-get7(BatteryService.this)) {
          break label103;
        }
      }
      label103:
      for (int i = this.mBatteryFastChargeARGB;; i = this.mBatteryMediumARGB)
      {
        localLight.setColor(i);
        return;
        this.mBatteryLight.turnOff();
        return;
      }
      label111:
      if (this.mScreenOn)
      {
        this.mBatteryLight.turnOff();
        return;
      }
      this.mBatteryLight.setFlashing(this.mBatteryLowARGB, 1, this.mBatteryLedOn, this.mBatteryLedOff);
      return;
      label147:
      if ((j == 2) || (j == 5))
      {
        if ((j == 5) || (i > 99))
        {
          this.mBatteryLight.setColor(this.mBatteryFullARGB);
          return;
        }
        if (isHvdcpPresent())
        {
          this.mBatteryLight.setFlashing(this.mBatteryMediumARGB, 1, this.mBatteryLedOn, this.mBatteryLedOn);
          return;
        }
        localLight = this.mBatteryLight;
        if (BatteryService.-get7(BatteryService.this)) {}
        for (i = this.mBatteryFastChargeARGB;; i = this.mBatteryMediumARGB)
        {
          localLight.setColor(i);
          return;
        }
      }
      this.mBatteryLight.turnOff();
    }
  }
  
  private final class LocalService
    extends BatteryManagerInternal
  {
    private LocalService() {}
    
    public int getBatteryLevel()
    {
      synchronized (BatteryService.-get10(BatteryService.this))
      {
        int i = BatteryService.-get3(BatteryService.this).batteryLevel;
        return i;
      }
    }
    
    public boolean getBatteryLevelLow()
    {
      synchronized (BatteryService.-get10(BatteryService.this))
      {
        boolean bool = BatteryService.-get1(BatteryService.this);
        return bool;
      }
    }
    
    public int getInvalidCharger()
    {
      synchronized (BatteryService.-get10(BatteryService.this))
      {
        int i = BatteryService.-get8(BatteryService.this);
        return i;
      }
    }
    
    public int getPlugType()
    {
      synchronized (BatteryService.-get10(BatteryService.this))
      {
        int i = BatteryService.-get11(BatteryService.this);
        return i;
      }
    }
    
    public boolean isPowered(int paramInt)
    {
      synchronized (BatteryService.-get10(BatteryService.this))
      {
        boolean bool = BatteryService.-wrap0(BatteryService.this, paramInt);
        return bool;
      }
    }
  }
  
  abstract class SettingsObserver
    extends ContentObserver
  {
    private String mTarget;
    
    public SettingsObserver(Handler paramHandler, String paramString)
    {
      super();
      this.mTarget = paramString;
      BatteryService.-get5(BatteryService.this).getContentResolver().registerContentObserver(Settings.System.getUriFor(this.mTarget), false, this);
      update();
    }
    
    public void onChange(boolean paramBoolean)
    {
      update();
    }
    
    abstract void onUpdate(boolean paramBoolean);
    
    public void update()
    {
      boolean bool = true;
      if (Settings.System.getInt(BatteryService.-get5(BatteryService.this).getContentResolver(), this.mTarget, 1) != 0) {}
      for (;;)
      {
        onUpdate(bool);
        return;
        bool = false;
      }
    }
  }
  
  class Shell
    extends ShellCommand
  {
    Shell() {}
    
    public int onCommand(String paramString)
    {
      return BatteryService.this.onShellCommand(this, paramString);
    }
    
    public void onHelp()
    {
      BatteryService.dumpHelp(getOutPrintWriter());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/BatteryService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */