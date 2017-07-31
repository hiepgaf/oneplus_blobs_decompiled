package com.android.server.am;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryStats.Uid;
import android.os.Build;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatterySipper.DrainType;
import com.android.internal.os.BatteryStatsHelper;
import com.android.internal.os.BatteryStatsImpl;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.oneplus.odm.insight.tracker.OSTracker;

class OnePlusPowerConsumptionDetector
{
  private static final String ACTION_TEST = "action.opcd.test";
  public static boolean DEBUG = false;
  public static boolean DISK = false;
  public static boolean ENABLE = true;
  public static final int FAKE_UID_APP = -92;
  public static final int FAKE_UID_BASE = -100;
  public static final int FAKE_UID_BLUETOOTH = -95;
  public static final int FAKE_UID_CAMERA = -88;
  public static final int FAKE_UID_CELL = -98;
  public static final int FAKE_UID_DEFAULT = -87;
  public static final int FAKE_UID_END = -86;
  public static final int FAKE_UID_FLASHLIGHT = -93;
  public static final int FAKE_UID_IDLE = -99;
  public static final int FAKE_UID_OVERCOUNTED = -89;
  public static final int FAKE_UID_PHONE = -97;
  public static final int FAKE_UID_SCREEN = -94;
  public static final int FAKE_UID_UNACCOUNTED = -90;
  public static final int FAKE_UID_USER = -91;
  public static final int FAKE_UID_WIFI = -96;
  public static final int FLAG_UPDATE_ALL_POWER = 3;
  public static final int FLAG_UPDATE_HW_POWER = 2;
  public static final int FLAG_UPDATE_SOFT_POWER = 1;
  public static final double HIGH_DRAIN_INTERVAL_PERCENT_THRESHOLD = 0.2D;
  public static final double HIGH_DRAIN_TOTAL_PERCENT_THRESHOLD = 0.1D;
  public static boolean LITE = false;
  public static boolean MDM = false;
  public static final String OPCD_RECORD_FILE = "/data/system/opcd.log";
  public static final String OPCD_RECORD_OLD_FILE = "/data/system/opcd.log.old";
  private static final String PROP_DEBUG = "persist.sys.opcd.debug";
  private static final String PROP_DISK = "persist.sys.opcd.disk";
  private static final String PROP_ENABLE = "persist.sys.opcd.enable";
  private static final String PROP_LITE = "persist.sys.opcd.lite";
  private static final String PROP_MDM = "persist.sys.opcd.mdm";
  public static final String TAG = "OPCD";
  public static final String TRACK_KEY_CUMULATIVE_DRAIN = "app";
  public static final String TRACK_KEY_DRAIN_TO_CHARGE = "total";
  public static final String TRACK_TAG_OPCD = "OPCD";
  public static final int VERSION = 16101101;
  private static ActivityManagerService mAms;
  private static BatteryStatsService mBatteryStatsService;
  private static Context mContext;
  private static long mScreenOffTriggerTime = 0L;
  private static long mScreenOnTriggerTime;
  private static long mTotalScreenOnTime;
  private ChargingRecord mchargingRecord;
  
  static
  {
    DEBUG = Build.DEBUG_ONEPLUS;
    LITE = true;
    MDM = true;
    DISK = false;
    mTotalScreenOnTime = 0L;
    mScreenOnTriggerTime = 0L;
  }
  
  public OnePlusPowerConsumptionDetector(ActivityManagerService paramActivityManagerService, Context paramContext, BatteryStatsService paramBatteryStatsService)
  {
    ENABLE = SystemProperties.getBoolean("persist.sys.opcd.enable", ENABLE);
    DEBUG = SystemProperties.getBoolean("persist.sys.opcd.debug", DEBUG);
    LITE = SystemProperties.getBoolean("persist.sys.opcd.lite", LITE);
    MDM = SystemProperties.getBoolean("persist.sys.opcd.mdm", MDM);
    DISK = SystemProperties.getBoolean("persist.sys.opcd.disk", DISK);
    if (!ENABLE)
    {
      Slog.e("OPCD", "disabled");
      return;
    }
    myLog("OnePlusPowerConsumptionDetector()--constructor");
    mAms = paramActivityManagerService;
    mContext = paramContext;
    mBatteryStatsService = paramBatteryStatsService;
    init();
  }
  
  public static String formatDouble(double paramDouble)
  {
    if (paramDouble == 0.0D) {
      return "0";
    }
    String str;
    if (paramDouble < 0.001D) {
      str = "%.6f";
    }
    for (;;)
    {
      return String.format(Locale.ENGLISH, str, new Object[] { Double.valueOf(paramDouble) });
      if (paramDouble < 0.01D) {
        str = "%.5f";
      } else if (paramDouble < 0.1D) {
        str = "%.4f";
      } else if (paramDouble < 1.0D) {
        str = "%.3f";
      } else if (paramDouble < 10.0D) {
        str = "%.2f";
      } else if (paramDouble < 100.0D) {
        str = "%.1f";
      } else {
        str = "%.0f";
      }
    }
  }
  
  private static String formatTime(long paramLong)
  {
    int i = (int)(paramLong % 60L);
    int j = (int)(paramLong / 60L % 60L);
    int k = (int)(paramLong / 3600L);
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(k);
    localStringBuilder.append("h:");
    localStringBuilder.append(j);
    localStringBuilder.append("m:");
    localStringBuilder.append(i);
    localStringBuilder.append("s");
    return localStringBuilder.toString();
  }
  
  private static String getDrainTypeByFakeUid(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown";
    case -99: 
      return "idle";
    case -98: 
      return "cell";
    case -97: 
      return "phone";
    case -96: 
      return "wifi";
    case -95: 
      return "bluetooth";
    case -94: 
      return "screen";
    case -93: 
      return "flashlight";
    case -92: 
      return "app";
    case -91: 
      return "user";
    case -90: 
      return "unaccounted";
    case -89: 
      return "overcounted";
    case -88: 
      return "camera";
    }
    return "default";
  }
  
  private static int getFakeUidByDrainType(BatterySipper.DrainType paramDrainType)
  {
    switch (-getcom-android-internal-os-BatterySipper$DrainTypeSwitchesValues()[paramDrainType.ordinal()])
    {
    default: 
      return -87;
    case 6: 
      return -99;
    case 4: 
      return -98;
    case 8: 
      return -97;
    case 12: 
      return -96;
    case 2: 
      return -95;
    case 9: 
      return -94;
    case 5: 
      return -93;
    case 1: 
      return -92;
    case 11: 
      return -91;
    case 10: 
      return -90;
    case 7: 
      return -89;
    }
    return -88;
  }
  
  private static List<PowerConsumptionSpeed> getHighSipperFromCumulativeDrain(double paramDouble)
  {
    if (DEBUG) {
      myLog("#getHighSipperFromCumulativeDrain # percentThreshold=" + paramDouble);
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = new BatteryStatsHelper(mContext, false, false);
    ((BatteryStatsHelper)localObject1).create(mBatteryStatsService.getActiveStatistics());
    ((BatteryStatsHelper)localObject1).refreshStats(0, -1);
    Object localObject2 = ((BatteryStatsHelper)localObject1).getUsageList();
    Object localObject3;
    if ((localObject2 != null) && (((List)localObject2).size() > 0))
    {
      double d1 = 0.0D;
      int i = 0;
      if (i < ((List)localObject2).size())
      {
        localObject1 = (BatterySipper)((List)localObject2).get(i);
        if (DEBUG)
        {
          localObject3 = new StringBuilder().append(i).append(" # bs # drainType=").append(((BatterySipper)localObject1).drainType).append(", uid=");
          if (((BatterySipper)localObject1).uidObj == null) {
            break label239;
          }
        }
        label239:
        for (int j = ((BatterySipper)localObject1).uidObj.getUid();; j = -1)
        {
          myLog(j + ", totalPowerMah=" + ((BatterySipper)localObject1).totalPowerMah + ", bs.sumPower()=" + ((BatterySipper)localObject1).sumPower());
          d1 += ((BatterySipper)localObject1).totalPowerMah;
          i += 1;
          break;
        }
      }
      if (d1 > 1.0D)
      {
        i = 0;
        if (i < ((List)localObject2).size())
        {
          localObject3 = (BatterySipper)((List)localObject2).get(i);
          double d2 = ((BatterySipper)localObject3).totalPowerMah / d1;
          if (d2 >= paramDouble) {
            switch (-getcom-android-internal-os-BatterySipper$DrainTypeSwitchesValues()[localObject3.drainType.ordinal()])
            {
            }
          }
          for (localObject1 = new PowerConsumptionSpeed(Integer.valueOf(getFakeUidByDrainType(((BatterySipper)localObject3).drainType)));; localObject1 = new PowerConsumptionSpeed(Integer.valueOf(((BatterySipper)localObject3).uidObj.getUid())))
          {
            ((PowerConsumptionSpeed)localObject1).percent = d2;
            ((PowerConsumptionSpeed)localObject1).updatePowerItems((BatterySipper)localObject3);
            ((PowerConsumptionSpeed)localObject1).initPkgNameByUid();
            localArrayList.add(localObject1);
            if (DEBUG) {
              myLog("getHighSipperFromCumulativeDrain # pcs=" + localObject1);
            }
            i += 1;
            break;
          }
        }
      }
    }
    if ((localArrayList != null) && (localArrayList.size() > 0))
    {
      new StringBuilder();
      localObject1 = new StringBuilder();
      localObject2 = ChargingRecord.sTraceDateFormat.format(new Date());
      localObject3 = localArrayList.iterator();
      while (((Iterator)localObject3).hasNext())
      {
        PowerConsumptionSpeed localPowerConsumptionSpeed = (PowerConsumptionSpeed)((Iterator)localObject3).next();
        if (MDM) {
          trackPowerData("OPCD", "app", localPowerConsumptionSpeed);
        }
        if (DISK)
        {
          ((StringBuilder)localObject1).append((String)localObject2);
          ((StringBuilder)localObject1).append(" ");
          ((StringBuilder)localObject1).append("app");
          ((StringBuilder)localObject1).append(" ");
          ((StringBuilder)localObject1).append(localPowerConsumptionSpeed.toString());
          ((StringBuilder)localObject1).append("\n");
        }
      }
      if (DISK) {
        persistToDisk(((StringBuilder)localObject1).toString());
      }
    }
    return localArrayList;
  }
  
  public static void myLog(String paramString)
  {
    if (DEBUG) {
      Slog.d("OPCD", paramString);
    }
  }
  
  /* Error */
  private static boolean persistToDisk(String paramString)
  {
    // Byte code:
    //   0: getstatic 233	com/android/server/am/OnePlusPowerConsumptionDetector:DISK	Z
    //   3: ifne +5 -> 8
    //   6: iconst_0
    //   7: ireturn
    //   8: aload_0
    //   9: ifnonnull +5 -> 14
    //   12: iconst_0
    //   13: ireturn
    //   14: new 507	java/io/File
    //   17: dup
    //   18: ldc 75
    //   20: invokespecial 509	java/io/File:<init>	(Ljava/lang/String;)V
    //   23: astore_1
    //   24: aload_1
    //   25: invokevirtual 513	java/io/File:getParentFile	()Ljava/io/File;
    //   28: astore_2
    //   29: aload_2
    //   30: invokevirtual 516	java/io/File:exists	()Z
    //   33: ifne +8 -> 41
    //   36: aload_2
    //   37: invokevirtual 519	java/io/File:mkdirs	()Z
    //   40: pop
    //   41: aload_1
    //   42: invokevirtual 522	java/io/File:length	()J
    //   45: ldc2_w 523
    //   48: lcmp
    //   49: ifge +31 -> 80
    //   52: new 526	java/io/FileOutputStream
    //   55: dup
    //   56: aload_1
    //   57: iconst_1
    //   58: invokespecial 529	java/io/FileOutputStream:<init>	(Ljava/io/File;Z)V
    //   61: astore_1
    //   62: aload_1
    //   63: aload_0
    //   64: invokevirtual 533	java/lang/String:getBytes	()[B
    //   67: invokevirtual 537	java/io/FileOutputStream:write	([B)V
    //   70: aload_1
    //   71: ifnull +7 -> 78
    //   74: aload_1
    //   75: invokevirtual 540	java/io/FileOutputStream:close	()V
    //   78: iconst_1
    //   79: ireturn
    //   80: new 507	java/io/File
    //   83: dup
    //   84: ldc 78
    //   86: invokespecial 509	java/io/File:<init>	(Ljava/lang/String;)V
    //   89: astore_2
    //   90: aload_1
    //   91: invokevirtual 516	java/io/File:exists	()Z
    //   94: ifeq +14 -> 108
    //   97: aload_2
    //   98: invokevirtual 543	java/io/File:delete	()Z
    //   101: pop
    //   102: aload_1
    //   103: aload_2
    //   104: invokevirtual 547	java/io/File:renameTo	(Ljava/io/File;)Z
    //   107: pop
    //   108: new 526	java/io/FileOutputStream
    //   111: dup
    //   112: aload_1
    //   113: invokespecial 550	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   116: astore_1
    //   117: goto -55 -> 62
    //   120: astore_0
    //   121: aload_0
    //   122: invokevirtual 553	java/io/FileNotFoundException:printStackTrace	()V
    //   125: iconst_0
    //   126: ireturn
    //   127: astore_0
    //   128: aload_0
    //   129: invokevirtual 554	java/io/IOException:printStackTrace	()V
    //   132: goto -54 -> 78
    //   135: astore_0
    //   136: aload_0
    //   137: invokevirtual 554	java/io/IOException:printStackTrace	()V
    //   140: aload_1
    //   141: ifnull +7 -> 148
    //   144: aload_1
    //   145: invokevirtual 540	java/io/FileOutputStream:close	()V
    //   148: iconst_0
    //   149: ireturn
    //   150: astore_0
    //   151: aload_0
    //   152: invokevirtual 554	java/io/IOException:printStackTrace	()V
    //   155: goto -7 -> 148
    //   158: astore_0
    //   159: aload_1
    //   160: ifnull +7 -> 167
    //   163: aload_1
    //   164: invokevirtual 540	java/io/FileOutputStream:close	()V
    //   167: aload_0
    //   168: athrow
    //   169: astore_1
    //   170: aload_1
    //   171: invokevirtual 554	java/io/IOException:printStackTrace	()V
    //   174: goto -7 -> 167
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	177	0	paramString	String
    //   23	141	1	localObject	Object
    //   169	2	1	localIOException	java.io.IOException
    //   28	76	2	localFile	java.io.File
    // Exception table:
    //   from	to	target	type
    //   41	62	120	java/io/FileNotFoundException
    //   80	108	120	java/io/FileNotFoundException
    //   108	117	120	java/io/FileNotFoundException
    //   74	78	127	java/io/IOException
    //   62	70	135	java/io/IOException
    //   144	148	150	java/io/IOException
    //   62	70	158	finally
    //   136	140	158	finally
    //   163	167	169	java/io/IOException
  }
  
  private void registerBatteryReceiver()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
    localIntentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
    localIntentFilter.addAction("android.intent.action.SCREEN_ON");
    localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
    localIntentFilter.addAction("action.opcd.test");
    mContext.registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        long l = SystemClock.elapsedRealtime();
        OnePlusPowerConsumptionDetector.-wrap4(OnePlusPowerConsumptionDetector.this, paramAnonymousIntent);
        Slog.d("OPCD", "# total elapsed " + (SystemClock.elapsedRealtime() - l) + " ms");
      }
    }, localIntentFilter);
  }
  
  private void responseBroadcast(Intent paramIntent)
  {
    if (DEBUG) {
      myLog("# onReceive # action=" + paramIntent.getAction());
    }
    if ("android.intent.action.ACTION_POWER_CONNECTED".equals(paramIntent.getAction())) {
      this.mchargingRecord.updateRecord(1);
    }
    do
    {
      do
      {
        do
        {
          do
          {
            return;
            if ("android.intent.action.ACTION_POWER_DISCONNECTED".equals(paramIntent.getAction()))
            {
              this.mchargingRecord.updateRecord(2);
              return;
            }
            if ("android.intent.action.SCREEN_ON".equals(paramIntent.getAction()))
            {
              mScreenOnTriggerTime = SystemClock.elapsedRealtime();
              return;
            }
            if (!"android.intent.action.SCREEN_OFF".equals(paramIntent.getAction())) {
              break;
            }
            mScreenOffTriggerTime = SystemClock.elapsedRealtime();
          } while (mScreenOffTriggerTime <= mScreenOnTriggerTime);
          mTotalScreenOnTime += mScreenOffTriggerTime - mScreenOnTriggerTime;
          return;
        } while (!"action.opcd.test".equals(paramIntent.getAction()));
        paramIntent = paramIntent.getStringExtra("code");
        myLog("# onReceive # code = " + paramIntent);
      } while ("dump".equals(paramIntent));
      if ("prop_using_on".equals(paramIntent))
      {
        ENABLE = true;
        SystemProperties.set("persist.sys.opcd.enable", "true");
        return;
      }
      if ("prop_using_off".equals(paramIntent))
      {
        ENABLE = false;
        SystemProperties.set("persist.sys.opcd.enable", "false");
        return;
      }
      if ("prop_debug_on".equals(paramIntent))
      {
        DEBUG = true;
        SystemProperties.set("persist.sys.opcd.debug", "true");
        return;
      }
      if ("prop_debug_off".equals(paramIntent))
      {
        DEBUG = false;
        SystemProperties.set("persist.sys.opcd.debug", "false");
        return;
      }
      if ("prop_lite_on".equals(paramIntent))
      {
        LITE = true;
        SystemProperties.set("persist.sys.opcd.lite", "true");
        return;
      }
      if ("prop_lite_off".equals(paramIntent))
      {
        LITE = false;
        SystemProperties.set("persist.sys.opcd.lite", "false");
        return;
      }
      if ("prop_mdm_on".equals(paramIntent))
      {
        MDM = true;
        SystemProperties.set("persist.sys.opcd.mdm", "true");
        return;
      }
      if ("prop_mdm_off".equals(paramIntent))
      {
        MDM = false;
        SystemProperties.set("persist.sys.opcd.mdm", "false");
        return;
      }
      if ("prop_disk_on".equals(paramIntent))
      {
        DISK = true;
        SystemProperties.set("persist.sys.opcd.disk", "true");
        return;
      }
    } while (!"prop_disk_off".equals(paramIntent));
    DISK = false;
    SystemProperties.set("persist.sys.opcd.disk", "false");
  }
  
  private static void trackPowerData(String paramString1, String paramString2, Object paramObject)
  {
    if (!MDM) {
      return;
    }
    if (paramObject == null) {
      return;
    }
    HashMap localHashMap = new HashMap();
    if ("total".equals(paramString2))
    {
      paramObject = (RecordItem)paramObject;
      localHashMap.put("Power", Long.toString(((RecordItem)paramObject).diffLevel));
      localHashMap.put("Time", Long.toString(((RecordItem)paramObject).diffTime));
      localHashMap.put("TotalRate", Long.toString(((RecordItem)paramObject).diffTime * 100L / ((RecordItem)paramObject).diffLevel));
      localHashMap.put("ScnOnTime", Long.toString(((RecordItem)paramObject).screenOnTime));
      localHashMap.put("ScnOnRate", Long.toString(((RecordItem)paramObject).screenOnTime * 100L / ((RecordItem)paramObject).diffLevel));
    }
    for (;;)
    {
      trackPowerData(paramString1 + "_" + paramString2, localHashMap);
      return;
      if (!"app".equals(paramString2)) {
        break;
      }
      paramObject = (PowerConsumptionSpeed)paramObject;
      localHashMap.put("pkg", ((PowerConsumptionSpeed)paramObject).pkgName);
      localHashMap.put("Power", Long.toString((((PowerConsumptionSpeed)paramObject).percent * 100.0D)));
    }
  }
  
  private static void trackPowerData(String paramString, Map<String, String> paramMap)
  {
    if (!MDM) {
      return;
    }
    OSTracker localOSTracker = new OSTracker(mContext);
    if ((paramMap != null) && (paramMap.size() > 0)) {
      localOSTracker.onEvent(paramString, paramMap);
    }
  }
  
  void init()
  {
    registerBatteryReceiver();
    this.mchargingRecord = new ChargingRecord();
  }
  
  static class ChargingRecord
  {
    static final int DiffLevelThreshold = SystemProperties.getInt("persist.sys.opcd.threshold", 50);
    public static final int TYPE_CONNECTED = 1;
    public static final int TYPE_DISCONNECTED = 2;
    static SimpleDateFormat sTraceDateFormat = new SimpleDateFormat("yyyyMMdd_HH:mm");
    int connectedBatLevel = -100;
    String connectedDate;
    long connectedTime = 0L;
    int disconnectedBatLevel = -100;
    String disconnectedDate;
    long disconnectedTime = 0L;
    private ArrayList<OnePlusPowerConsumptionDetector.RecordItem> mRecordItemList = new ArrayList();
    
    public void updateRecord(int paramInt)
    {
      if (OnePlusPowerConsumptionDetector.DEBUG) {
        OnePlusPowerConsumptionDetector.myLog("updateRecord # type=" + paramInt);
      }
      if (paramInt == 2)
      {
        this.disconnectedDate = sTraceDateFormat.format(new Date());
        this.disconnectedTime = SystemClock.elapsedRealtime();
        this.disconnectedBatLevel = OnePlusPowerConsumptionDetector.-get0().getActiveStatistics().getDischargeCurrentLevelLocked();
        if (!OnePlusPowerConsumptionDetector.DEBUG) {}
      }
      long l;
      do
      {
        do
        {
          Slog.d("OPCD", "updateRecord # disconnectedBatLevel=" + this.disconnectedBatLevel);
          do
          {
            return;
          } while (paramInt != 1);
          this.connectedDate = sTraceDateFormat.format(new Date());
          this.connectedTime = SystemClock.elapsedRealtime();
        } while (this.disconnectedTime <= 0L);
        this.connectedBatLevel = OnePlusPowerConsumptionDetector.-get0().getActiveStatistics().getDischargeCurrentLevelLocked();
        l = this.connectedTime - this.disconnectedTime;
        paramInt = this.disconnectedBatLevel - this.connectedBatLevel;
        if (OnePlusPowerConsumptionDetector.DEBUG) {
          Slog.d("OPCD", "updateRecord # connectedBatLevel=" + this.connectedBatLevel);
        }
        if (OnePlusPowerConsumptionDetector.DEBUG) {
          Slog.d("OPCD", "updateRecord # diffLevel=" + paramInt + ", diffTime=" + l);
        }
      } while ((l <= 0L) || (paramInt < DiffLevelThreshold));
      Object localObject = new OnePlusPowerConsumptionDetector.RecordItem(l, paramInt, OnePlusPowerConsumptionDetector.-get2(), this.disconnectedDate, this.connectedDate);
      OnePlusPowerConsumptionDetector.-set0(0L);
      if (OnePlusPowerConsumptionDetector.MDM) {
        OnePlusPowerConsumptionDetector.-wrap5("OPCD", "total", localObject);
      }
      if (OnePlusPowerConsumptionDetector.DISK)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(sTraceDateFormat.format(new Date()));
        localStringBuilder.append(" ");
        localStringBuilder.append("total");
        localStringBuilder.append(" ");
        localStringBuilder.append(((OnePlusPowerConsumptionDetector.RecordItem)localObject).toString());
        localStringBuilder.append("\n");
        OnePlusPowerConsumptionDetector.-wrap0(localStringBuilder.toString());
      }
      if (OnePlusPowerConsumptionDetector.DEBUG)
      {
        OnePlusPowerConsumptionDetector.myLog(((OnePlusPowerConsumptionDetector.RecordItem)localObject).toString());
        this.mRecordItemList.add(localObject);
        if (this.mRecordItemList.size() > 5)
        {
          localObject = this.mRecordItemList.iterator();
          while (((Iterator)localObject).hasNext()) {
            OnePlusPowerConsumptionDetector.myLog(((OnePlusPowerConsumptionDetector.RecordItem)((Iterator)localObject).next()).toString());
          }
          this.mRecordItemList.clear();
        }
      }
      OnePlusPowerConsumptionDetector.-wrap3(0.1D);
    }
  }
  
  static class PowerConsumptionSpeed
    implements Comparable<PowerConsumptionSpeed>
  {
    public double cameraPowerMah = 0.0D;
    public double cpuPowerMah = 0.0D;
    public double diffMah = 0.0D;
    public long diffTime = 0L;
    public String drainType = null;
    public double flashlightPowerMah = 0.0D;
    public double gpsPowerMah = 0.0D;
    public double mobileRadioPowerMah = 0.0D;
    public double percent = 0.0D;
    public String pkgName = null;
    public String[] pkgNames = null;
    public double sensorPowerMah = 0.0D;
    public boolean simplifyToString = true;
    public Integer uid = null;
    public double wakeLockPowerMah = 0.0D;
    public double wifiPowerMah = 0.0D;
    
    public PowerConsumptionSpeed(Integer paramInteger)
    {
      this.uid = paramInteger;
      initDrainType();
    }
    
    private String formatStringArray(String[] paramArrayOfString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
      {
        int i = 0;
        while (i < paramArrayOfString.length)
        {
          localStringBuilder.append(paramArrayOfString[i]).append("@");
          i += 1;
        }
      }
      return localStringBuilder.toString();
    }
    
    private void initDrainType()
    {
      if ((this.uid.intValue() < -86) && (this.uid.intValue() > -100))
      {
        this.drainType = OnePlusPowerConsumptionDetector.-wrap2(this.uid.intValue());
        this.pkgName = this.drainType;
        return;
      }
      this.drainType = "app";
      this.pkgName = ("app_" + this.uid);
    }
    
    public int compareTo(PowerConsumptionSpeed paramPowerConsumptionSpeed)
    {
      if (this.diffMah > paramPowerConsumptionSpeed.diffMah) {
        return -1;
      }
      if (this.diffMah < paramPowerConsumptionSpeed.diffMah) {
        return 1;
      }
      return 0;
    }
    
    public String getItemDetails()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("(cpu:").append(OnePlusPowerConsumptionDetector.formatDouble(this.cpuPowerMah));
      localStringBuilder.append(",wake:").append(OnePlusPowerConsumptionDetector.formatDouble(this.wakeLockPowerMah));
      localStringBuilder.append(",radio:").append(OnePlusPowerConsumptionDetector.formatDouble(this.mobileRadioPowerMah));
      localStringBuilder.append(",wifi:").append(OnePlusPowerConsumptionDetector.formatDouble(this.wifiPowerMah));
      localStringBuilder.append(",gps:").append(OnePlusPowerConsumptionDetector.formatDouble(this.gpsPowerMah));
      localStringBuilder.append(",sensor:").append(OnePlusPowerConsumptionDetector.formatDouble(this.sensorPowerMah));
      localStringBuilder.append(",camera:").append(OnePlusPowerConsumptionDetector.formatDouble(this.cameraPowerMah));
      localStringBuilder.append(",flash:").append(OnePlusPowerConsumptionDetector.formatDouble(this.flashlightPowerMah));
      localStringBuilder.append(")");
      return localStringBuilder.toString();
    }
    
    public void initPkgNameByUid()
    {
      if ((this.uid.intValue() < -86) && (this.uid.intValue() > -100)) {}
      do
      {
        return;
        this.pkgNames = OnePlusPowerConsumptionDetector.-get1().getPackageManager().getPackagesForUid(this.uid.intValue());
      } while ((this.pkgNames == null) || (this.pkgNames.length != 1));
      this.pkgName = this.pkgNames[0];
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("PowerConsumptionSpeed{");
      localStringBuilder.append("uid:");
      localStringBuilder.append(this.uid);
      localStringBuilder.append(",drainType:");
      localStringBuilder.append(this.drainType);
      localStringBuilder.append(",pkgName:");
      localStringBuilder.append(this.pkgName);
      if ((this.uid.intValue() != 1000) && (this.pkgNames != null) && (this.pkgNames.length > 1))
      {
        localStringBuilder.append(",pkgNames:");
        localStringBuilder.append(formatStringArray(this.pkgNames));
      }
      localStringBuilder.append(",diffMah:");
      localStringBuilder.append(OnePlusPowerConsumptionDetector.formatDouble(this.diffMah));
      localStringBuilder.append(",percent:");
      localStringBuilder.append(String.format(Locale.ENGLISH, "%.3f", new Object[] { Double.valueOf(this.percent) }));
      localStringBuilder.append(",diffTime:");
      localStringBuilder.append(this.diffTime);
      localStringBuilder.append(",detail:");
      localStringBuilder.append(getItemDetails());
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    public void updatePowerItems(BatterySipper paramBatterySipper)
    {
      if (paramBatterySipper != null)
      {
        this.cpuPowerMah = paramBatterySipper.cpuPowerMah;
        this.wakeLockPowerMah = paramBatterySipper.wakeLockPowerMah;
        this.mobileRadioPowerMah = paramBatterySipper.mobileRadioPowerMah;
        this.wifiPowerMah = paramBatterySipper.wifiPowerMah;
        this.gpsPowerMah = paramBatterySipper.gpsPowerMah;
        this.sensorPowerMah = paramBatterySipper.sensorPowerMah;
        this.cameraPowerMah = paramBatterySipper.cameraPowerMah;
        this.flashlightPowerMah = paramBatterySipper.flashlightPowerMah;
      }
    }
  }
  
  static class RecordItem
  {
    public String connectedDate;
    public int diffLevel;
    public long diffTime;
    public String disconnectedDate;
    public long screenOnTime;
    
    RecordItem(long paramLong1, int paramInt, long paramLong2, String paramString1, String paramString2)
    {
      this.diffTime = paramLong1;
      this.diffLevel = paramInt;
      this.screenOnTime = paramLong2;
      this.disconnectedDate = paramString1;
      this.connectedDate = paramString2;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("RecordItem{start:");
      localStringBuilder.append(this.disconnectedDate);
      localStringBuilder.append(",end:");
      localStringBuilder.append(this.connectedDate);
      localStringBuilder.append(",drained:");
      localStringBuilder.append(this.diffLevel);
      localStringBuilder.append("%,kept:");
      localStringBuilder.append(OnePlusPowerConsumptionDetector.-wrap1(this.diffTime / 1000L));
      localStringBuilder.append(",screenon:");
      localStringBuilder.append(OnePlusPowerConsumptionDetector.-wrap1(this.screenOnTime / 1000L));
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/OnePlusPowerConsumptionDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */