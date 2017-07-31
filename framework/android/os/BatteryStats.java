package android.os;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.telephony.SignalStrength;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.MutableBoolean;
import android.util.Pair;
import android.util.Printer;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;
import com.android.internal.os.PowerProfile;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class BatteryStats
  implements Parcelable
{
  private static final String APK_DATA = "apk";
  private static final String AUDIO_DATA = "aud";
  public static final int AUDIO_TURNED_ON = 15;
  private static final String BATTERY_DATA = "bt";
  private static final String BATTERY_DISCHARGE_DATA = "dc";
  private static final String BATTERY_LEVEL_DATA = "lv";
  private static final int BATTERY_STATS_CHECKIN_VERSION = 9;
  private static final String BLUETOOTH_CONTROLLER_DATA = "ble";
  private static final String BLUETOOTH_MISC_DATA = "blem";
  public static final int BLUETOOTH_SCAN_ON = 19;
  private static final long BYTES_PER_GB = 1073741824L;
  private static final long BYTES_PER_KB = 1024L;
  private static final long BYTES_PER_MB = 1048576L;
  private static final String CAMERA_DATA = "cam";
  public static final int CAMERA_TURNED_ON = 17;
  private static final String CHARGE_STEP_DATA = "csd";
  private static final String CHARGE_TIME_REMAIN_DATA = "ctr";
  static final String CHECKIN_VERSION = "19";
  private static final String CPU_DATA = "cpu";
  public static final int DATA_CONNECTION_1xRTT = 7;
  public static final int DATA_CONNECTION_CDMA = 4;
  private static final String DATA_CONNECTION_COUNT_DATA = "dcc";
  public static final int DATA_CONNECTION_EDGE = 2;
  public static final int DATA_CONNECTION_EHRPD = 14;
  public static final int DATA_CONNECTION_EVDO_0 = 5;
  public static final int DATA_CONNECTION_EVDO_A = 6;
  public static final int DATA_CONNECTION_EVDO_B = 12;
  public static final int DATA_CONNECTION_GPRS = 1;
  public static final int DATA_CONNECTION_HSDPA = 8;
  public static final int DATA_CONNECTION_HSPA = 10;
  public static final int DATA_CONNECTION_HSPAP = 15;
  public static final int DATA_CONNECTION_HSUPA = 9;
  public static final int DATA_CONNECTION_IDEN = 11;
  public static final int DATA_CONNECTION_LTE = 13;
  static final String[] DATA_CONNECTION_NAMES;
  public static final int DATA_CONNECTION_NONE = 0;
  public static final int DATA_CONNECTION_OTHER = 16;
  private static final String DATA_CONNECTION_TIME_DATA = "dct";
  public static final int DATA_CONNECTION_UMTS = 3;
  public static final int DEVICE_IDLE_MODE_DEEP = 2;
  public static final int DEVICE_IDLE_MODE_LIGHT = 1;
  public static final int DEVICE_IDLE_MODE_OFF = 0;
  private static final String DISCHARGE_STEP_DATA = "dsd";
  private static final String DISCHARGE_TIME_REMAIN_DATA = "dtr";
  public static final int DUMP_CHARGED_ONLY = 2;
  public static final int DUMP_DAILY_ONLY = 4;
  public static final int DUMP_DEVICE_WIFI_ONLY = 64;
  public static final int DUMP_HISTORY_ONLY = 8;
  public static final int DUMP_INCLUDE_HISTORY = 16;
  public static final int DUMP_VERBOSE = 32;
  private static final String FLASHLIGHT_DATA = "fla";
  public static final int FLASHLIGHT_TURNED_ON = 16;
  public static final int FOREGROUND_ACTIVITY = 10;
  private static final String FOREGROUND_DATA = "fg";
  public static final int FULL_WIFI_LOCK = 5;
  private static final String GLOBAL_BLUETOOTH_CONTROLLER_DATA = "gble";
  private static final String GLOBAL_MODEM_CONTROLLER_DATA = "gmcd";
  private static final String GLOBAL_NETWORK_DATA = "gn";
  private static final String GLOBAL_WIFI_CONTROLLER_DATA = "gwfcd";
  private static final String GLOBAL_WIFI_DATA = "gwfl";
  private static final String HISTORY_DATA = "h";
  public static final String[] HISTORY_EVENT_CHECKIN_NAMES;
  public static final String[] HISTORY_EVENT_NAMES;
  public static final BitDescription[] HISTORY_STATE2_DESCRIPTIONS;
  public static final BitDescription[] HISTORY_STATE_DESCRIPTIONS;
  private static final String HISTORY_STRING_POOL = "hsp";
  public static final int JOB = 14;
  private static final String JOB_DATA = "jb";
  private static final String KERNEL_WAKELOCK_DATA = "kwl";
  private static final boolean LOCAL_LOGV = false;
  private static final String MISC_DATA = "m";
  private static final String MODEM_CONTROLLER_DATA = "mcd";
  public static final int NETWORK_BT_RX_DATA = 4;
  public static final int NETWORK_BT_TX_DATA = 5;
  private static final String NETWORK_DATA = "nt";
  public static final int NETWORK_MOBILE_RX_DATA = 0;
  public static final int NETWORK_MOBILE_TX_DATA = 1;
  public static final int NETWORK_WIFI_RX_DATA = 2;
  public static final int NETWORK_WIFI_TX_DATA = 3;
  public static final int NUM_DATA_CONNECTION_TYPES = 17;
  public static final int NUM_NETWORK_ACTIVITY_TYPES = 6;
  public static final int NUM_SCREEN_BRIGHTNESS_BINS = 5;
  public static final int NUM_WIFI_SIGNAL_STRENGTH_BINS = 5;
  public static final int NUM_WIFI_STATES = 8;
  public static final int NUM_WIFI_SUPPL_STATES = 13;
  private static final String POWER_USE_ITEM_DATA = "pwi";
  private static final String POWER_USE_SUMMARY_DATA = "pws";
  private static final String PROCESS_DATA = "pr";
  public static final int PROCESS_STATE = 12;
  public static final String RESULT_RECEIVER_CONTROLLER_KEY = "controller_activity";
  public static final int SCREEN_BRIGHTNESS_BRIGHT = 4;
  public static final int SCREEN_BRIGHTNESS_DARK = 0;
  private static final String SCREEN_BRIGHTNESS_DATA = "br";
  public static final int SCREEN_BRIGHTNESS_DIM = 1;
  public static final int SCREEN_BRIGHTNESS_LIGHT = 3;
  public static final int SCREEN_BRIGHTNESS_MEDIUM = 2;
  static final String[] SCREEN_BRIGHTNESS_NAMES;
  static final String[] SCREEN_BRIGHTNESS_SHORT_NAMES;
  public static final int SENSOR = 3;
  private static final String SENSOR_DATA = "sr";
  public static final String SERVICE_NAME = "batterystats";
  private static final String SIGNAL_SCANNING_TIME_DATA = "sst";
  private static final String SIGNAL_STRENGTH_COUNT_DATA = "sgc";
  private static final String SIGNAL_STRENGTH_TIME_DATA = "sgt";
  private static final String STATE_TIME_DATA = "st";
  public static final int STATS_CURRENT = 1;
  public static final int STATS_SINCE_CHARGED = 0;
  public static final int STATS_SINCE_UNPLUGGED = 2;
  private static final String[] STAT_NAMES = { "l", "c", "u" };
  public static final long STEP_LEVEL_INITIAL_MODE_MASK = 71776119061217280L;
  public static final int STEP_LEVEL_INITIAL_MODE_SHIFT = 48;
  public static final long STEP_LEVEL_LEVEL_MASK = 280375465082880L;
  public static final int STEP_LEVEL_LEVEL_SHIFT = 40;
  public static final int[] STEP_LEVEL_MODES_OF_INTEREST;
  public static final int STEP_LEVEL_MODE_DEVICE_IDLE = 8;
  public static final String[] STEP_LEVEL_MODE_LABELS = { "screen off", "screen off power save", "screen off device idle", "screen on", "screen on power save", "screen doze", "screen doze power save", "screen doze-suspend", "screen doze-suspend power save", "screen doze-suspend device idle" };
  public static final int STEP_LEVEL_MODE_POWER_SAVE = 4;
  public static final int STEP_LEVEL_MODE_SCREEN_STATE = 3;
  public static final int[] STEP_LEVEL_MODE_VALUES;
  public static final long STEP_LEVEL_MODIFIED_MODE_MASK = -72057594037927936L;
  public static final int STEP_LEVEL_MODIFIED_MODE_SHIFT = 56;
  public static final long STEP_LEVEL_TIME_MASK = 1099511627775L;
  public static final int SYNC = 13;
  private static final String SYNC_DATA = "sy";
  private static final String TAG = "BatteryStats";
  private static final String UID_DATA = "uid";
  private static final String USER_ACTIVITY_DATA = "ua";
  private static final String VERSION_DATA = "vers";
  private static final String VIBRATOR_DATA = "vib";
  public static final int VIBRATOR_ON = 9;
  private static final String VIDEO_DATA = "vid";
  public static final int VIDEO_TURNED_ON = 8;
  private static final String WAKELOCK_DATA = "wl";
  private static final String WAKEUP_ALARM_DATA = "wua";
  private static final String WAKEUP_REASON_DATA = "wr";
  public static final int WAKE_TYPE_DRAW = 18;
  public static final int WAKE_TYPE_FULL = 1;
  public static final int WAKE_TYPE_PARTIAL = 0;
  public static final int WAKE_TYPE_WINDOW = 2;
  public static final int WIFI_BATCHED_SCAN = 11;
  private static final String WIFI_CONTROLLER_DATA = "wfcd";
  private static final String WIFI_DATA = "wfl";
  public static final int WIFI_MULTICAST_ENABLED = 7;
  public static final int WIFI_RUNNING = 4;
  public static final int WIFI_SCAN = 6;
  private static final String WIFI_SIGNAL_STRENGTH_COUNT_DATA = "wsgc";
  private static final String WIFI_SIGNAL_STRENGTH_TIME_DATA = "wsgt";
  private static final String WIFI_STATE_COUNT_DATA = "wsc";
  static final String[] WIFI_STATE_NAMES;
  public static final int WIFI_STATE_OFF = 0;
  public static final int WIFI_STATE_OFF_SCANNING = 1;
  public static final int WIFI_STATE_ON_CONNECTED_P2P = 5;
  public static final int WIFI_STATE_ON_CONNECTED_STA = 4;
  public static final int WIFI_STATE_ON_CONNECTED_STA_P2P = 6;
  public static final int WIFI_STATE_ON_DISCONNECTED = 3;
  public static final int WIFI_STATE_ON_NO_NETWORKS = 2;
  public static final int WIFI_STATE_SOFT_AP = 7;
  private static final String WIFI_STATE_TIME_DATA = "wst";
  public static final int WIFI_SUPPL_STATE_ASSOCIATED = 7;
  public static final int WIFI_SUPPL_STATE_ASSOCIATING = 6;
  public static final int WIFI_SUPPL_STATE_AUTHENTICATING = 5;
  public static final int WIFI_SUPPL_STATE_COMPLETED = 10;
  private static final String WIFI_SUPPL_STATE_COUNT_DATA = "wssc";
  public static final int WIFI_SUPPL_STATE_DISCONNECTED = 1;
  public static final int WIFI_SUPPL_STATE_DORMANT = 11;
  public static final int WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE = 8;
  public static final int WIFI_SUPPL_STATE_GROUP_HANDSHAKE = 9;
  public static final int WIFI_SUPPL_STATE_INACTIVE = 3;
  public static final int WIFI_SUPPL_STATE_INTERFACE_DISABLED = 2;
  public static final int WIFI_SUPPL_STATE_INVALID = 0;
  static final String[] WIFI_SUPPL_STATE_NAMES;
  public static final int WIFI_SUPPL_STATE_SCANNING = 4;
  static final String[] WIFI_SUPPL_STATE_SHORT_NAMES;
  private static final String WIFI_SUPPL_STATE_TIME_DATA = "wsst";
  public static final int WIFI_SUPPL_STATE_UNINITIALIZED = 12;
  private final StringBuilder mFormatBuilder = new StringBuilder(32);
  private final Formatter mFormatter = new Formatter(this.mFormatBuilder);
  
  static
  {
    SCREEN_BRIGHTNESS_NAMES = new String[] { "dark", "dim", "medium", "light", "bright" };
    SCREEN_BRIGHTNESS_SHORT_NAMES = new String[] { "0", "1", "2", "3", "4" };
    DATA_CONNECTION_NAMES = new String[] { "none", "gprs", "edge", "umts", "cdma", "evdo_0", "evdo_A", "1xrtt", "hsdpa", "hsupa", "hspa", "iden", "evdo_b", "lte", "ehrpd", "hspap", "other" };
    WIFI_SUPPL_STATE_NAMES = new String[] { "invalid", "disconn", "disabled", "inactive", "scanning", "authenticating", "associating", "associated", "4-way-handshake", "group-handshake", "completed", "dormant", "uninit" };
    WIFI_SUPPL_STATE_SHORT_NAMES = new String[] { "inv", "dsc", "dis", "inact", "scan", "auth", "ascing", "asced", "4-way", "group", "compl", "dorm", "uninit" };
    HISTORY_STATE_DESCRIPTIONS = new BitDescription[] { new BitDescription(Integer.MIN_VALUE, "running", "r"), new BitDescription(1073741824, "wake_lock", "w"), new BitDescription(8388608, "sensor", "s"), new BitDescription(536870912, "gps", "g"), new BitDescription(268435456, "wifi_full_lock", "Wl"), new BitDescription(134217728, "wifi_scan", "Ws"), new BitDescription(65536, "wifi_multicast", "Wm"), new BitDescription(67108864, "wifi_radio", "Wr"), new BitDescription(33554432, "mobile_radio", "Pr"), new BitDescription(2097152, "phone_scanning", "Psc"), new BitDescription(4194304, "audio", "a"), new BitDescription(1048576, "screen", "S"), new BitDescription(524288, "plugged", "BP"), new BitDescription(15872, 9, "data_conn", "Pcn", DATA_CONNECTION_NAMES, DATA_CONNECTION_NAMES), new BitDescription(448, 6, "phone_state", "Pst", new String[] { "in", "out", "emergency", "off" }, new String[] { "in", "out", "em", "off" }), new BitDescription(56, 3, "phone_signal_strength", "Pss", SignalStrength.SIGNAL_STRENGTH_NAMES, new String[] { "0", "1", "2", "3", "4" }), new BitDescription(7, 0, "brightness", "Sb", SCREEN_BRIGHTNESS_NAMES, SCREEN_BRIGHTNESS_SHORT_NAMES) };
    HISTORY_STATE2_DESCRIPTIONS = new BitDescription[] { new BitDescription(Integer.MIN_VALUE, "power_save", "ps"), new BitDescription(1073741824, "video", "v"), new BitDescription(536870912, "wifi_running", "Ww"), new BitDescription(268435456, "wifi", "W"), new BitDescription(134217728, "flashlight", "fl"), new BitDescription(100663296, 25, "device_idle", "di", new String[] { "off", "light", "full", "???" }, new String[] { "off", "light", "full", "???" }), new BitDescription(16777216, "charging", "ch"), new BitDescription(8388608, "phone_in_call", "Pcl"), new BitDescription(4194304, "bluetooth", "b"), new BitDescription(112, 4, "wifi_signal_strength", "Wss", new String[] { "0", "1", "2", "3", "4" }, new String[] { "0", "1", "2", "3", "4" }), new BitDescription(15, 0, "wifi_suppl", "Wsp", WIFI_SUPPL_STATE_NAMES, WIFI_SUPPL_STATE_SHORT_NAMES), new BitDescription(2097152, "camera", "ca"), new BitDescription(1048576, "ble_scan", "bles") };
    HISTORY_EVENT_NAMES = new String[] { "null", "proc", "fg", "top", "sync", "wake_lock_in", "job", "user", "userfg", "conn", "active", "pkginst", "pkgunin", "alarm", "stats", "inactive", "active", "tmpwhitelist", "screenwake", "wakeupap", "longwake" };
    HISTORY_EVENT_CHECKIN_NAMES = new String[] { "Enl", "Epr", "Efg", "Etp", "Esy", "Ewl", "Ejb", "Eur", "Euf", "Ecn", "Eac", "Epi", "Epu", "Eal", "Est", "Eai", "Eaa", "Etw", "Esw", "Ewa", "Elw" };
    WIFI_STATE_NAMES = new String[] { "off", "scanning", "no_net", "disconn", "sta", "p2p", "sta_p2p", "soft_ap" };
    STEP_LEVEL_MODES_OF_INTEREST = new int[] { 7, 15, 11, 7, 7, 7, 7, 7, 15, 11 };
    STEP_LEVEL_MODE_VALUES = new int[] { 0, 4, 8, 1, 5, 2, 6, 3, 7, 11 };
  }
  
  private static long computeWakeLock(Timer paramTimer, long paramLong, int paramInt)
  {
    if (paramTimer != null) {
      return (500L + paramTimer.getTotalTimeLocked(paramLong, paramInt)) / 1000L;
    }
    return 0L;
  }
  
  private static boolean controllerActivityHasData(ControllerActivityCounter paramControllerActivityCounter, int paramInt)
  {
    if (paramControllerActivityCounter == null) {
      return false;
    }
    if ((paramControllerActivityCounter.getIdleTimeCounter().getCountLocked(paramInt) != 0L) || (paramControllerActivityCounter.getRxTimeCounter().getCountLocked(paramInt) != 0L)) {}
    while (paramControllerActivityCounter.getPowerCounter().getCountLocked(paramInt) != 0L) {
      return true;
    }
    paramControllerActivityCounter = paramControllerActivityCounter.getTxTimeCounters();
    int j = paramControllerActivityCounter.length;
    int i = 0;
    while (i < j)
    {
      if (paramControllerActivityCounter[i].getCountLocked(paramInt) != 0L) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private static final void dumpControllerActivityLine(PrintWriter paramPrintWriter, int paramInt1, String paramString1, String paramString2, ControllerActivityCounter paramControllerActivityCounter, int paramInt2)
  {
    if (!controllerActivityHasData(paramControllerActivityCounter, paramInt2)) {
      return;
    }
    dumpLineHeader(paramPrintWriter, paramInt1, paramString1, paramString2);
    paramPrintWriter.print(",");
    paramPrintWriter.print(paramControllerActivityCounter.getIdleTimeCounter().getCountLocked(paramInt2));
    paramPrintWriter.print(",");
    paramPrintWriter.print(paramControllerActivityCounter.getRxTimeCounter().getCountLocked(paramInt2));
    paramPrintWriter.print(",");
    paramPrintWriter.print(paramControllerActivityCounter.getPowerCounter().getCountLocked(paramInt2) / 3600000L);
    paramString1 = paramControllerActivityCounter.getTxTimeCounters();
    paramInt1 = 0;
    int i = paramString1.length;
    while (paramInt1 < i)
    {
      paramString2 = paramString1[paramInt1];
      paramPrintWriter.print(",");
      paramPrintWriter.print(paramString2.getCountLocked(paramInt2));
      paramInt1 += 1;
    }
    paramPrintWriter.println();
  }
  
  private void dumpDailyLevelStepSummary(PrintWriter paramPrintWriter, String paramString1, String paramString2, LevelStepTracker paramLevelStepTracker, StringBuilder paramStringBuilder, int[] paramArrayOfInt)
  {
    if (paramLevelStepTracker == null) {
      return;
    }
    long l = paramLevelStepTracker.computeTimeEstimate(0L, 0L, paramArrayOfInt);
    if (l >= 0L)
    {
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print(paramString2);
      paramPrintWriter.print(" total time: ");
      paramStringBuilder.setLength(0);
      formatTimeMs(paramStringBuilder, l);
      paramPrintWriter.print(paramStringBuilder);
      paramPrintWriter.print(" (from ");
      paramPrintWriter.print(paramArrayOfInt[0]);
      paramPrintWriter.println(" steps)");
    }
    int i = 0;
    while (i < STEP_LEVEL_MODES_OF_INTEREST.length)
    {
      l = paramLevelStepTracker.computeTimeEstimate(STEP_LEVEL_MODES_OF_INTEREST[i], STEP_LEVEL_MODE_VALUES[i], paramArrayOfInt);
      if (l > 0L)
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print(paramString2);
        paramPrintWriter.print(" ");
        paramPrintWriter.print(STEP_LEVEL_MODE_LABELS[i]);
        paramPrintWriter.print(" time: ");
        paramStringBuilder.setLength(0);
        formatTimeMs(paramStringBuilder, l);
        paramPrintWriter.print(paramStringBuilder);
        paramPrintWriter.print(" (from ");
        paramPrintWriter.print(paramArrayOfInt[0]);
        paramPrintWriter.println(" steps)");
      }
      i += 1;
    }
  }
  
  private void dumpDailyPackageChanges(PrintWriter paramPrintWriter, String paramString, ArrayList<PackageChange> paramArrayList)
  {
    if (paramArrayList == null) {
      return;
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("Package changes:");
    int i = 0;
    if (i < paramArrayList.size())
    {
      PackageChange localPackageChange = (PackageChange)paramArrayList.get(i);
      if (localPackageChange.mUpdate)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  Update ");
        paramPrintWriter.print(localPackageChange.mPackageName);
        paramPrintWriter.print(" vers=");
        paramPrintWriter.println(localPackageChange.mVersionCode);
      }
      for (;;)
      {
        i += 1;
        break;
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  Uninstall ");
        paramPrintWriter.println(localPackageChange.mPackageName);
      }
    }
  }
  
  private static boolean dumpDurationSteps(PrintWriter paramPrintWriter, String paramString1, String paramString2, LevelStepTracker paramLevelStepTracker, boolean paramBoolean)
  {
    if (paramLevelStepTracker == null) {
      return false;
    }
    int m = paramLevelStepTracker.mNumStepDurations;
    if (m <= 0) {
      return false;
    }
    if (!paramBoolean) {
      paramPrintWriter.println(paramString2);
    }
    String[] arrayOfString = new String[5];
    int k = 0;
    if (k < m)
    {
      long l1 = paramLevelStepTracker.getDurationAt(k);
      int i = paramLevelStepTracker.getLevelAt(k);
      long l2 = paramLevelStepTracker.getInitModeAt(k);
      long l3 = paramLevelStepTracker.getModModeAt(k);
      if (paramBoolean)
      {
        arrayOfString[0] = Long.toString(l1);
        arrayOfString[1] = Integer.toString(i);
        if ((0x3 & l3) == 0L) {
          switch ((int)(0x3 & l2) + 1)
          {
          default: 
            arrayOfString[2] = "?";
            label159:
            if ((0x4 & l3) == 0L) {
              if ((0x4 & l2) != 0L)
              {
                str = "p+";
                label186:
                arrayOfString[3] = str;
                label192:
                if ((0x8 & l3) != 0L) {
                  break label321;
                }
                if ((0x8 & l2) == 0L) {
                  break label313;
                }
                str = "i+";
                label219:
                arrayOfString[4] = str;
              }
            }
            break;
          }
        }
        for (;;)
        {
          dumpLine(paramPrintWriter, 0, "i", paramString2, arrayOfString);
          k += 1;
          break;
          arrayOfString[2] = "s-";
          break label159;
          arrayOfString[2] = "s+";
          break label159;
          arrayOfString[2] = "sd";
          break label159;
          arrayOfString[2] = "sds";
          break label159;
          arrayOfString[2] = "";
          break label159;
          str = "p-";
          break label186;
          arrayOfString[3] = "";
          break label192;
          label313:
          str = "i-";
          break label219;
          label321:
          arrayOfString[4] = "";
        }
      }
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("#");
      paramPrintWriter.print(k);
      paramPrintWriter.print(": ");
      TimeUtils.formatDuration(l1, paramPrintWriter);
      paramPrintWriter.print(" to ");
      paramPrintWriter.print(i);
      int j = 0;
      if ((0x3 & l3) == 0L) {
        paramPrintWriter.print(" (");
      }
      switch ((int)(0x3 & l2) + 1)
      {
      default: 
        paramPrintWriter.print("screen-?");
        label443:
        j = 1;
        i = j;
        if ((0x4 & l3) == 0L)
        {
          if (j != 0)
          {
            str = ", ";
            label471:
            paramPrintWriter.print(str);
            if ((0x4 & l2) == 0L) {
              break label625;
            }
            str = "power-save-on";
            label493:
            paramPrintWriter.print(str);
            i = 1;
          }
        }
        else
        {
          j = i;
          if ((0x8 & l3) == 0L)
          {
            if (i == 0) {
              break label633;
            }
            str = ", ";
            label527:
            paramPrintWriter.print(str);
            if ((0x8 & l2) == 0L) {
              break label641;
            }
          }
        }
        break;
      }
      label625:
      label633:
      label641:
      for (String str = "device-idle-on";; str = "device-idle-off")
      {
        paramPrintWriter.print(str);
        j = 1;
        if (j != 0) {
          paramPrintWriter.print(")");
        }
        paramPrintWriter.println();
        break;
        paramPrintWriter.print("screen-off");
        break label443;
        paramPrintWriter.print("screen-on");
        break label443;
        paramPrintWriter.print("screen-doze");
        break label443;
        paramPrintWriter.print("screen-doze-suspend");
        break label443;
        str = " (";
        break label471;
        str = "power-save-off";
        break label493;
        str = " (";
        break label527;
      }
    }
    return true;
  }
  
  private void dumpHistoryLocked(PrintWriter paramPrintWriter, int paramInt, long paramLong, boolean paramBoolean)
  {
    HistoryPrinter localHistoryPrinter = new HistoryPrinter();
    HistoryItem localHistoryItem = new HistoryItem();
    long l1 = -1L;
    long l3 = -1L;
    int i = 0;
    Object localObject1 = null;
    while (getNextHistoryLocked(localHistoryItem))
    {
      long l2 = localHistoryItem.time;
      long l4 = l3;
      if (l3 < 0L) {
        l4 = l2;
      }
      l3 = l4;
      l1 = l2;
      if (localHistoryItem.time >= paramLong)
      {
        int j = i;
        Object localObject2 = localObject1;
        if (paramLong >= 0L)
        {
          if (i != 0)
          {
            localObject2 = localObject1;
            j = i;
          }
        }
        else {
          label110:
          if ((paramInt & 0x20) == 0) {
            break label600;
          }
        }
        label205:
        label393:
        label405:
        label423:
        label460:
        label570:
        label576:
        label600:
        for (boolean bool = true;; bool = false)
        {
          localHistoryPrinter.printNextItem(paramPrintWriter, localHistoryItem, l4, paramBoolean, bool);
          l3 = l4;
          l1 = l2;
          i = j;
          localObject1 = localObject2;
          break;
          if ((localHistoryItem.cmd == 5) || (localHistoryItem.cmd == 7))
          {
            i = 1;
            if ((paramInt & 0x20) == 0) {
              break label393;
            }
            bool = true;
            localHistoryPrinter.printNextItem(paramPrintWriter, localHistoryItem, l4, paramBoolean, bool);
            localHistoryItem.cmd = 0;
            j = i;
            localObject2 = localObject1;
            if (0 == 0) {
              break label110;
            }
            if (localHistoryItem.cmd != 0) {
              if ((paramInt & 0x20) == 0) {
                break label405;
              }
            }
          }
          int m;
          for (bool = true;; bool = false)
          {
            localHistoryPrinter.printNextItem(paramPrintWriter, localHistoryItem, l4, paramBoolean, bool);
            localHistoryItem.cmd = 0;
            m = localHistoryItem.eventCode;
            localObject2 = localHistoryItem.eventTag;
            localHistoryItem.eventTag = new HistoryTag();
            j = 0;
            for (;;)
            {
              if (j >= 21) {
                break label576;
              }
              localObject3 = ((HistoryEventTracker)localObject1).getStateForEvent(j);
              if (localObject3 != null) {
                break;
              }
              j += 1;
            }
            if ((localHistoryItem.cmd == 4) || (localHistoryItem.cmd == 8)) {
              break;
            }
            if (localHistoryItem.currentTime == 0L) {
              break label205;
            }
            i = 1;
            byte b = localHistoryItem.cmd;
            localHistoryItem.cmd = 5;
            if ((paramInt & 0x20) != 0) {}
            for (bool = true;; bool = false)
            {
              localHistoryPrinter.printNextItem(paramPrintWriter, localHistoryItem, l4, paramBoolean, bool);
              localHistoryItem.cmd = b;
              break label205;
              bool = false;
              break;
            }
          }
          Object localObject3 = ((HashMap)localObject3).entrySet().iterator();
          int k;
          if (((Iterator)localObject3).hasNext())
          {
            Map.Entry localEntry = (Map.Entry)((Iterator)localObject3).next();
            SparseIntArray localSparseIntArray = (SparseIntArray)localEntry.getValue();
            k = 0;
            if (k < localSparseIntArray.size())
            {
              localHistoryItem.eventCode = j;
              localHistoryItem.eventTag.string = ((String)localEntry.getKey());
              localHistoryItem.eventTag.uid = localSparseIntArray.keyAt(k);
              localHistoryItem.eventTag.poolIdx = localSparseIntArray.valueAt(k);
              if ((paramInt & 0x20) == 0) {
                break label570;
              }
            }
          }
          for (bool = true;; bool = false)
          {
            localHistoryPrinter.printNextItem(paramPrintWriter, localHistoryItem, l4, paramBoolean, bool);
            localHistoryItem.wakeReasonTag = null;
            localHistoryItem.wakelockTag = null;
            k += 1;
            break label460;
            break label423;
            break;
          }
          localHistoryItem.eventCode = m;
          localHistoryItem.eventTag = ((HistoryTag)localObject2);
          localObject2 = null;
          j = i;
          break label110;
        }
      }
    }
    if (paramLong >= 0L)
    {
      commitCurrentHistoryBatchLocked();
      if (!paramBoolean) {
        break label641;
      }
    }
    label641:
    for (localObject1 = "NEXT: ";; localObject1 = "  NEXT: ")
    {
      paramPrintWriter.print((String)localObject1);
      paramPrintWriter.println(1L + l1);
      return;
    }
  }
  
  private static final void dumpLine(PrintWriter paramPrintWriter, int paramInt, String paramString1, String paramString2, Object... paramVarArgs)
  {
    dumpLineHeader(paramPrintWriter, paramInt, paramString1, paramString2);
    paramInt = 0;
    int i = paramVarArgs.length;
    while (paramInt < i)
    {
      paramString1 = paramVarArgs[paramInt];
      paramPrintWriter.print(',');
      paramPrintWriter.print(paramString1);
      paramInt += 1;
    }
    paramPrintWriter.println();
  }
  
  private static final void dumpLineHeader(PrintWriter paramPrintWriter, int paramInt, String paramString1, String paramString2)
  {
    paramPrintWriter.print(9);
    paramPrintWriter.print(',');
    paramPrintWriter.print(paramInt);
    paramPrintWriter.print(',');
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print(',');
    paramPrintWriter.print(paramString2);
  }
  
  private static boolean dumpTimeEstimate(PrintWriter paramPrintWriter, String paramString1, String paramString2, String paramString3, long paramLong)
  {
    if (paramLong < 0L) {
      return false;
    }
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print(paramString2);
    paramPrintWriter.print(paramString3);
    paramString1 = new StringBuilder(64);
    formatTimeMs(paramString1, paramLong);
    paramPrintWriter.print(paramString1);
    paramPrintWriter.println();
    return true;
  }
  
  private static final void dumpTimer(PrintWriter paramPrintWriter, int paramInt1, String paramString1, String paramString2, Timer paramTimer, long paramLong, int paramInt2)
  {
    if (paramTimer != null)
    {
      paramLong = (paramTimer.getTotalTimeLocked(paramLong, paramInt2) + 500L) / 1000L;
      paramInt2 = paramTimer.getCountLocked(paramInt2);
      if (paramLong != 0L) {
        dumpLine(paramPrintWriter, paramInt1, paramString1, paramString2, new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) });
      }
    }
  }
  
  public static final void formatTimeMs(StringBuilder paramStringBuilder, long paramLong)
  {
    long l = paramLong / 1000L;
    formatTimeRaw(paramStringBuilder, l);
    paramStringBuilder.append(paramLong - 1000L * l);
    paramStringBuilder.append("ms ");
  }
  
  public static final void formatTimeMsNoSpace(StringBuilder paramStringBuilder, long paramLong)
  {
    long l = paramLong / 1000L;
    formatTimeRaw(paramStringBuilder, l);
    paramStringBuilder.append(paramLong - 1000L * l);
    paramStringBuilder.append("ms");
  }
  
  private static final void formatTimeRaw(StringBuilder paramStringBuilder, long paramLong)
  {
    long l1 = paramLong / 86400L;
    if (l1 != 0L)
    {
      paramStringBuilder.append(l1);
      paramStringBuilder.append("d ");
    }
    l1 = 60L * l1 * 60L * 24L;
    long l2 = (paramLong - l1) / 3600L;
    if ((l2 != 0L) || (l1 != 0L))
    {
      paramStringBuilder.append(l2);
      paramStringBuilder.append("h ");
    }
    l1 += 60L * l2 * 60L;
    l2 = (paramLong - l1) / 60L;
    if ((l2 != 0L) || (l1 != 0L))
    {
      paramStringBuilder.append(l2);
      paramStringBuilder.append("m ");
    }
    l1 += 60L * l2;
    if ((paramLong != 0L) || (l1 != 0L))
    {
      paramStringBuilder.append(paramLong - l1);
      paramStringBuilder.append("s ");
    }
  }
  
  static void printBitDescriptions(PrintWriter paramPrintWriter, int paramInt1, int paramInt2, HistoryTag paramHistoryTag, BitDescription[] paramArrayOfBitDescription, boolean paramBoolean)
  {
    int k = paramInt1 ^ paramInt2;
    if (k == 0) {
      return;
    }
    int i = 0;
    paramInt1 = 0;
    if (paramInt1 < paramArrayOfBitDescription.length)
    {
      BitDescription localBitDescription = paramArrayOfBitDescription[paramInt1];
      int j = i;
      String str;
      if ((localBitDescription.mask & k) != 0)
      {
        if (!paramBoolean) {
          break label186;
        }
        str = " ";
        label54:
        paramPrintWriter.print(str);
        if (localBitDescription.shift >= 0) {
          break label223;
        }
        if ((localBitDescription.mask & paramInt2) == 0) {
          break label194;
        }
        str = "+";
        label83:
        paramPrintWriter.print(str);
        if (!paramBoolean) {
          break label202;
        }
        str = localBitDescription.name;
        label101:
        paramPrintWriter.print(str);
        j = i;
        if (localBitDescription.mask == 1073741824)
        {
          j = i;
          if (paramHistoryTag != null)
          {
            j = 1;
            paramPrintWriter.print("=");
            if (!paramBoolean) {
              break label212;
            }
            UserHandle.formatUid(paramPrintWriter, paramHistoryTag.uid);
            paramPrintWriter.print(":\"");
            paramPrintWriter.print(paramHistoryTag.string);
            paramPrintWriter.print("\"");
          }
        }
      }
      for (;;)
      {
        paramInt1 += 1;
        i = j;
        break;
        label186:
        str = ",";
        break label54;
        label194:
        str = "-";
        break label83;
        label202:
        str = localBitDescription.shortName;
        break label101;
        label212:
        paramPrintWriter.print(paramHistoryTag.poolIdx);
        continue;
        label223:
        if (paramBoolean)
        {
          str = localBitDescription.name;
          label235:
          paramPrintWriter.print(str);
          paramPrintWriter.print("=");
          j = (localBitDescription.mask & paramInt2) >> localBitDescription.shift;
          if ((localBitDescription.values == null) || (j < 0) || (j >= localBitDescription.values.length)) {
            break label338;
          }
          if (!paramBoolean) {
            break label325;
          }
        }
        label325:
        for (str = localBitDescription.values[j];; str = localBitDescription.shortValues[j])
        {
          paramPrintWriter.print(str);
          j = i;
          break;
          str = localBitDescription.shortName;
          break label235;
        }
        label338:
        paramPrintWriter.print(j);
        j = i;
      }
    }
    if ((i == 0) && (paramHistoryTag != null)) {
      if (!paramBoolean) {
        break label412;
      }
    }
    label412:
    for (paramArrayOfBitDescription = " wake_lock=";; paramArrayOfBitDescription = ",w=")
    {
      paramPrintWriter.print(paramArrayOfBitDescription);
      if (!paramBoolean) {
        break;
      }
      UserHandle.formatUid(paramPrintWriter, paramHistoryTag.uid);
      paramPrintWriter.print(":\"");
      paramPrintWriter.print(paramHistoryTag.string);
      paramPrintWriter.print("\"");
      return;
    }
    paramPrintWriter.print(paramHistoryTag.poolIdx);
  }
  
  private final void printControllerActivity(PrintWriter paramPrintWriter, StringBuilder paramStringBuilder, String paramString1, String paramString2, ControllerActivityCounter paramControllerActivityCounter, int paramInt)
  {
    long l3 = paramControllerActivityCounter.getIdleTimeCounter().getCountLocked(paramInt);
    long l4 = paramControllerActivityCounter.getRxTimeCounter().getCountLocked(paramInt);
    long l2 = paramControllerActivityCounter.getPowerCounter().getCountLocked(paramInt);
    long l1 = 0L;
    LongCounter[] arrayOfLongCounter = paramControllerActivityCounter.getTxTimeCounters();
    int i = 0;
    int j = arrayOfLongCounter.length;
    while (i < j)
    {
      l1 += arrayOfLongCounter[i].getCountLocked(paramInt);
      i += 1;
    }
    long l5 = l3 + l4 + l1;
    paramStringBuilder.setLength(0);
    paramStringBuilder.append(paramString1);
    paramStringBuilder.append("  ");
    paramStringBuilder.append(paramString2);
    paramStringBuilder.append(" Idle time:   ");
    formatTimeMs(paramStringBuilder, l3);
    paramStringBuilder.append("(");
    paramStringBuilder.append(formatRatioLocked(l3, l5));
    paramStringBuilder.append(")");
    paramPrintWriter.println(paramStringBuilder.toString());
    paramStringBuilder.setLength(0);
    paramStringBuilder.append(paramString1);
    paramStringBuilder.append("  ");
    paramStringBuilder.append(paramString2);
    paramStringBuilder.append(" Rx time:     ");
    formatTimeMs(paramStringBuilder, l4);
    paramStringBuilder.append("(");
    paramStringBuilder.append(formatRatioLocked(l4, l5));
    paramStringBuilder.append(")");
    paramPrintWriter.println(paramStringBuilder.toString());
    paramStringBuilder.setLength(0);
    paramStringBuilder.append(paramString1);
    paramStringBuilder.append("  ");
    paramStringBuilder.append(paramString2);
    paramStringBuilder.append(" Tx time:     ");
    formatTimeMs(paramStringBuilder, l1);
    paramStringBuilder.append("(");
    paramStringBuilder.append(formatRatioLocked(l1, l5));
    paramStringBuilder.append(")");
    paramPrintWriter.println(paramStringBuilder.toString());
    j = paramControllerActivityCounter.getTxTimeCounters().length;
    if (j > 1)
    {
      i = 0;
      while (i < j)
      {
        l3 = paramControllerActivityCounter.getTxTimeCounters()[i].getCountLocked(paramInt);
        paramStringBuilder.setLength(0);
        paramStringBuilder.append(paramString1);
        paramStringBuilder.append("    [");
        paramStringBuilder.append(i);
        paramStringBuilder.append("] ");
        formatTimeMs(paramStringBuilder, l3);
        paramStringBuilder.append("(");
        paramStringBuilder.append(formatRatioLocked(l3, l1));
        paramStringBuilder.append(")");
        paramPrintWriter.println(paramStringBuilder.toString());
        i += 1;
      }
    }
    paramStringBuilder.setLength(0);
    paramStringBuilder.append(paramString1);
    paramStringBuilder.append("  ");
    paramStringBuilder.append(paramString2);
    paramStringBuilder.append(" Power drain: ").append(BatteryStatsHelper.makemAh(l2 / 3600000.0D));
    paramStringBuilder.append("mAh");
    paramPrintWriter.println(paramStringBuilder.toString());
  }
  
  private final void printControllerActivityIfInteresting(PrintWriter paramPrintWriter, StringBuilder paramStringBuilder, String paramString1, String paramString2, ControllerActivityCounter paramControllerActivityCounter, int paramInt)
  {
    if (controllerActivityHasData(paramControllerActivityCounter, paramInt)) {
      printControllerActivity(paramPrintWriter, paramStringBuilder, paramString1, paramString2, paramControllerActivityCounter, paramInt);
    }
  }
  
  private void printSizeValue(PrintWriter paramPrintWriter, long paramLong)
  {
    float f2 = (float)paramLong;
    String str = "";
    float f1 = f2;
    if (f2 >= 10240.0F)
    {
      str = "KB";
      f1 = f2 / 1024.0F;
    }
    f2 = f1;
    if (f1 >= 10240.0F)
    {
      str = "MB";
      f2 = f1 / 1024.0F;
    }
    f1 = f2;
    if (f2 >= 10240.0F)
    {
      str = "GB";
      f1 = f2 / 1024.0F;
    }
    f2 = f1;
    if (f1 >= 10240.0F)
    {
      str = "TB";
      f2 = f1 / 1024.0F;
    }
    f1 = f2;
    if (f2 >= 10240.0F)
    {
      str = "PB";
      f1 = f2 / 1024.0F;
    }
    paramPrintWriter.print((int)f1);
    paramPrintWriter.print(str);
  }
  
  private static final boolean printTimer(PrintWriter paramPrintWriter, StringBuilder paramStringBuilder, Timer paramTimer, long paramLong, int paramInt, String paramString1, String paramString2)
  {
    if (paramTimer != null)
    {
      long l = (paramTimer.getTotalTimeLocked(paramLong, paramInt) + 500L) / 1000L;
      paramInt = paramTimer.getCountLocked(paramInt);
      if (l != 0L)
      {
        paramStringBuilder.setLength(0);
        paramStringBuilder.append(paramString1);
        paramStringBuilder.append("    ");
        paramStringBuilder.append(paramString2);
        paramStringBuilder.append(": ");
        formatTimeMs(paramStringBuilder, l);
        paramStringBuilder.append("realtime (");
        paramStringBuilder.append(paramInt);
        paramStringBuilder.append(" times)");
        l = paramTimer.getMaxDurationMsLocked(paramLong / 1000L);
        if (l >= 0L)
        {
          paramStringBuilder.append(" max=");
          paramStringBuilder.append(l);
        }
        if (paramTimer.isRunningLocked())
        {
          paramLong = paramTimer.getCurrentDurationMsLocked(paramLong / 1000L);
          if (paramLong < 0L) {
            break label188;
          }
          paramStringBuilder.append(" (running for ");
          paramStringBuilder.append(paramLong);
          paramStringBuilder.append("ms)");
        }
        for (;;)
        {
          paramPrintWriter.println(paramStringBuilder.toString());
          return true;
          label188:
          paramStringBuilder.append(" (running)");
        }
      }
    }
    return false;
  }
  
  private static final String printWakeLock(StringBuilder paramStringBuilder, Timer paramTimer, long paramLong, String paramString1, int paramInt, String paramString2)
  {
    if (paramTimer != null)
    {
      long l = computeWakeLock(paramTimer, paramLong, paramInt);
      paramInt = paramTimer.getCountLocked(paramInt);
      if (l != 0L)
      {
        paramStringBuilder.append(paramString2);
        formatTimeMs(paramStringBuilder, l);
        if (paramString1 != null)
        {
          paramStringBuilder.append(paramString1);
          paramStringBuilder.append(' ');
        }
        paramStringBuilder.append('(');
        paramStringBuilder.append(paramInt);
        paramStringBuilder.append(" times)");
        l = paramTimer.getMaxDurationMsLocked(paramLong / 1000L);
        if (l >= 0L)
        {
          paramStringBuilder.append(" max=");
          paramStringBuilder.append(l);
        }
        if (paramTimer.isRunningLocked())
        {
          paramLong = paramTimer.getCurrentDurationMsLocked(paramLong / 1000L);
          if (paramLong < 0L) {
            break label164;
          }
          paramStringBuilder.append(" (running for ");
          paramStringBuilder.append(paramLong);
          paramStringBuilder.append("ms)");
        }
        for (;;)
        {
          return ", ";
          label164:
          paramStringBuilder.append(" (running)");
        }
      }
    }
    return paramString2;
  }
  
  private static final String printWakeLockCheckin(StringBuilder paramStringBuilder, Timer paramTimer, long paramLong, String paramString1, int paramInt, String paramString2)
  {
    long l3 = 0L;
    int i = 0;
    long l2 = -1L;
    long l1 = -1L;
    if (paramTimer != null)
    {
      l3 = paramTimer.getTotalTimeLocked(paramLong, paramInt);
      i = paramTimer.getCountLocked(paramInt);
      l1 = paramTimer.getCurrentDurationMsLocked(paramLong / 1000L);
      l2 = paramTimer.getMaxDurationMsLocked(paramLong / 1000L);
    }
    paramStringBuilder.append(paramString2);
    paramStringBuilder.append((500L + l3) / 1000L);
    paramStringBuilder.append(',');
    if (paramString1 != null) {}
    for (paramTimer = paramString1 + ",";; paramTimer = "")
    {
      paramStringBuilder.append(paramTimer);
      paramStringBuilder.append(i);
      paramStringBuilder.append(',');
      paramStringBuilder.append(l1);
      paramStringBuilder.append(',');
      paramStringBuilder.append(l2);
      return ",";
    }
  }
  
  private void printmAh(PrintWriter paramPrintWriter, double paramDouble)
  {
    paramPrintWriter.print(BatteryStatsHelper.makemAh(paramDouble));
  }
  
  private void printmAh(StringBuilder paramStringBuilder, double paramDouble)
  {
    paramStringBuilder.append(BatteryStatsHelper.makemAh(paramDouble));
  }
  
  public abstract void commitCurrentHistoryBatchLocked();
  
  public abstract long computeBatteryRealtime(long paramLong, int paramInt);
  
  public abstract long computeBatteryScreenOffRealtime(long paramLong, int paramInt);
  
  public abstract long computeBatteryScreenOffUptime(long paramLong, int paramInt);
  
  public abstract long computeBatteryTimeRemaining(long paramLong);
  
  public abstract long computeBatteryUptime(long paramLong, int paramInt);
  
  public abstract long computeChargeTimeRemaining(long paramLong);
  
  public abstract long computeRealtime(long paramLong, int paramInt);
  
  public abstract long computeUptime(long paramLong, int paramInt);
  
  public final void dumpCheckinLocked(Context paramContext, PrintWriter paramPrintWriter, int paramInt1, int paramInt2)
  {
    dumpCheckinLocked(paramContext, paramPrintWriter, paramInt1, paramInt2, BatteryStatsHelper.checkWifiOnly(paramContext));
  }
  
  public final void dumpCheckinLocked(Context paramContext, PrintWriter paramPrintWriter, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    long l7 = SystemClock.uptimeMillis() * 1000L;
    long l5 = SystemClock.elapsedRealtime() * 1000L;
    long l6 = getBatteryUptime(l7);
    long l1 = computeBatteryUptime(l7, paramInt1);
    long l2 = computeBatteryRealtime(l5, paramInt1);
    long l3 = computeBatteryScreenOffUptime(l7, paramInt1);
    long l4 = computeBatteryScreenOffRealtime(l5, paramInt1);
    long l17 = computeRealtime(l5, paramInt1);
    long l18 = computeUptime(l7, paramInt1);
    l7 = getScreenOnTime(l5, paramInt1);
    long l8 = getInteractiveTime(l5, paramInt1);
    long l9 = getPowerSaveModeEnabledTime(l5, paramInt1);
    long l10 = getDeviceIdleModeTime(1, l5, paramInt1);
    long l11 = getDeviceIdleModeTime(2, l5, paramInt1);
    long l12 = getDeviceIdlingTime(1, l5, paramInt1);
    long l13 = getDeviceIdlingTime(2, l5, paramInt1);
    int k = getNumConnectivityChange(paramInt1);
    long l14 = getPhoneOnTime(l5, paramInt1);
    long l15 = getDischargeCoulombCounter().getCountLocked(paramInt1);
    long l16 = getDischargeScreenOffCoulombCounter().getCountLocked(paramInt1);
    StringBuilder localStringBuilder = new StringBuilder(128);
    SparseArray localSparseArray = getUidStats();
    int n = localSparseArray.size();
    String str = STAT_NAMES[paramInt1];
    if (paramInt1 == 0)
    {
      localObject1 = Integer.valueOf(getStartCount());
      dumpLine(paramPrintWriter, 0, str, "bt", new Object[] { localObject1, Long.valueOf(l2 / 1000L), Long.valueOf(l1 / 1000L), Long.valueOf(l17 / 1000L), Long.valueOf(l18 / 1000L), Long.valueOf(getStartClockTime()), Long.valueOf(l4 / 1000L), Long.valueOf(l3 / 1000L), Integer.valueOf(getEstimatedBatteryCapacity()) });
      l2 = 0L;
      l1 = 0L;
      i = 0;
    }
    int j;
    Object localObject2;
    Object localObject3;
    for (;;)
    {
      if (i >= n) {
        break label501;
      }
      localObject1 = ((Uid)localSparseArray.valueAt(i)).getWakelockStats();
      j = ((ArrayMap)localObject1).size() - 1;
      l3 = l2;
      for (;;)
      {
        if (j >= 0)
        {
          localObject2 = (BatteryStats.Uid.Wakelock)((ArrayMap)localObject1).valueAt(j);
          localObject3 = ((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(1);
          l2 = l3;
          if (localObject3 != null) {
            l2 = l3 + ((Timer)localObject3).getTotalTimeLocked(l5, paramInt1);
          }
          localObject2 = ((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(0);
          l4 = l1;
          if (localObject2 != null) {
            l4 = l1 + ((Timer)localObject2).getTotalTimeLocked(l5, paramInt1);
          }
          j -= 1;
          l3 = l2;
          l1 = l4;
          continue;
          localObject1 = "N/A";
          break;
        }
      }
      i += 1;
      l2 = l3;
    }
    label501:
    dumpLine(paramPrintWriter, 0, str, "gn", new Object[] { Long.valueOf(getNetworkActivityBytes(0, paramInt1)), Long.valueOf(getNetworkActivityBytes(1, paramInt1)), Long.valueOf(getNetworkActivityBytes(2, paramInt1)), Long.valueOf(getNetworkActivityBytes(3, paramInt1)), Long.valueOf(getNetworkActivityPackets(0, paramInt1)), Long.valueOf(getNetworkActivityPackets(1, paramInt1)), Long.valueOf(getNetworkActivityPackets(2, paramInt1)), Long.valueOf(getNetworkActivityPackets(3, paramInt1)), Long.valueOf(getNetworkActivityBytes(4, paramInt1)), Long.valueOf(getNetworkActivityBytes(5, paramInt1)) });
    dumpControllerActivityLine(paramPrintWriter, 0, str, "gmcd", getModemControllerActivity(), paramInt1);
    l3 = getWifiOnTime(l5, paramInt1);
    l4 = getGlobalWifiRunningTime(l5, paramInt1);
    dumpLine(paramPrintWriter, 0, str, "gwfl", new Object[] { Long.valueOf(l3 / 1000L), Long.valueOf(l4 / 1000L), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0) });
    dumpControllerActivityLine(paramPrintWriter, 0, str, "gwfcd", getWifiControllerActivity(), paramInt1);
    dumpControllerActivityLine(paramPrintWriter, 0, str, "gble", getBluetoothControllerActivity(), paramInt1);
    dumpLine(paramPrintWriter, 0, str, "m", new Object[] { Long.valueOf(l7 / 1000L), Long.valueOf(l14 / 1000L), Long.valueOf(l2 / 1000L), Long.valueOf(l1 / 1000L), Long.valueOf(getMobileRadioActiveTime(l5, paramInt1) / 1000L), Long.valueOf(getMobileRadioActiveAdjustedTime(paramInt1) / 1000L), Long.valueOf(l8 / 1000L), Long.valueOf(l9 / 1000L), Integer.valueOf(k), Long.valueOf(l11 / 1000L), Integer.valueOf(getDeviceIdleModeCount(2, paramInt1)), Long.valueOf(l13 / 1000L), Integer.valueOf(getDeviceIdlingCount(2, paramInt1)), Integer.valueOf(getMobileRadioActiveCount(paramInt1)), Long.valueOf(getMobileRadioActiveUnknownTime(paramInt1) / 1000L), Long.valueOf(l10 / 1000L), Integer.valueOf(getDeviceIdleModeCount(1, paramInt1)), Long.valueOf(l12 / 1000L), Integer.valueOf(getDeviceIdlingCount(1, paramInt1)), Long.valueOf(getLongestDeviceIdleModeTime(1)), Long.valueOf(getLongestDeviceIdleModeTime(2)) });
    Object localObject1 = new Object[5];
    int i = 0;
    while (i < 5)
    {
      localObject1[i] = Long.valueOf(getScreenBrightnessTime(i, l5, paramInt1) / 1000L);
      i += 1;
    }
    dumpLine(paramPrintWriter, 0, str, "br", (Object[])localObject1);
    localObject1 = new Object[5];
    i = 0;
    while (i < 5)
    {
      localObject1[i] = Long.valueOf(getPhoneSignalStrengthTime(i, l5, paramInt1) / 1000L);
      i += 1;
    }
    dumpLine(paramPrintWriter, 0, str, "sgt", (Object[])localObject1);
    dumpLine(paramPrintWriter, 0, str, "sst", new Object[] { Long.valueOf(getPhoneSignalScanningTime(l5, paramInt1) / 1000L) });
    i = 0;
    while (i < 5)
    {
      localObject1[i] = Integer.valueOf(getPhoneSignalStrengthCount(i, paramInt1));
      i += 1;
    }
    dumpLine(paramPrintWriter, 0, str, "sgc", (Object[])localObject1);
    localObject1 = new Object[17];
    i = 0;
    while (i < 17)
    {
      localObject1[i] = Long.valueOf(getPhoneDataConnectionTime(i, l5, paramInt1) / 1000L);
      i += 1;
    }
    dumpLine(paramPrintWriter, 0, str, "dct", (Object[])localObject1);
    i = 0;
    while (i < 17)
    {
      localObject1[i] = Integer.valueOf(getPhoneDataConnectionCount(i, paramInt1));
      i += 1;
    }
    dumpLine(paramPrintWriter, 0, str, "dcc", (Object[])localObject1);
    localObject1 = new Object[8];
    i = 0;
    while (i < 8)
    {
      localObject1[i] = Long.valueOf(getWifiStateTime(i, l5, paramInt1) / 1000L);
      i += 1;
    }
    dumpLine(paramPrintWriter, 0, str, "wst", (Object[])localObject1);
    i = 0;
    while (i < 8)
    {
      localObject1[i] = Integer.valueOf(getWifiStateCount(i, paramInt1));
      i += 1;
    }
    dumpLine(paramPrintWriter, 0, str, "wsc", (Object[])localObject1);
    localObject1 = new Object[13];
    i = 0;
    while (i < 13)
    {
      localObject1[i] = Long.valueOf(getWifiSupplStateTime(i, l5, paramInt1) / 1000L);
      i += 1;
    }
    dumpLine(paramPrintWriter, 0, str, "wsst", (Object[])localObject1);
    i = 0;
    while (i < 13)
    {
      localObject1[i] = Integer.valueOf(getWifiSupplStateCount(i, paramInt1));
      i += 1;
    }
    dumpLine(paramPrintWriter, 0, str, "wssc", (Object[])localObject1);
    localObject1 = new Object[5];
    i = 0;
    while (i < 5)
    {
      localObject1[i] = Long.valueOf(getWifiSignalStrengthTime(i, l5, paramInt1) / 1000L);
      i += 1;
    }
    dumpLine(paramPrintWriter, 0, str, "wsgt", (Object[])localObject1);
    i = 0;
    while (i < 5)
    {
      localObject1[i] = Integer.valueOf(getWifiSignalStrengthCount(i, paramInt1));
      i += 1;
    }
    dumpLine(paramPrintWriter, 0, str, "wsgc", (Object[])localObject1);
    if (paramInt1 == 2) {
      dumpLine(paramPrintWriter, 0, str, "lv", new Object[] { Integer.valueOf(getDischargeStartLevel()), Integer.valueOf(getDischargeCurrentLevel()) });
    }
    if (paramInt1 == 2) {
      dumpLine(paramPrintWriter, 0, str, "dc", new Object[] { Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel()), Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel()), Integer.valueOf(getDischargeAmountScreenOn()), Integer.valueOf(getDischargeAmountScreenOff()), Long.valueOf(l15 / 1000L), Long.valueOf(l16 / 1000L) });
    }
    while (paramInt2 < 0)
    {
      localObject1 = getKernelWakelockStats();
      if (((Map)localObject1).size() > 0)
      {
        localObject1 = ((Map)localObject1).entrySet().iterator();
        for (;;)
        {
          if (((Iterator)localObject1).hasNext())
          {
            localObject2 = (Map.Entry)((Iterator)localObject1).next();
            localStringBuilder.setLength(0);
            printWakeLockCheckin(localStringBuilder, (Timer)((Map.Entry)localObject2).getValue(), l5, null, paramInt1, "");
            dumpLine(paramPrintWriter, 0, str, "kwl", new Object[] { ((Map.Entry)localObject2).getKey(), localStringBuilder.toString() });
            continue;
            dumpLine(paramPrintWriter, 0, str, "dc", new Object[] { Integer.valueOf(getLowDischargeAmountSinceCharge()), Integer.valueOf(getHighDischargeAmountSinceCharge()), Integer.valueOf(getDischargeAmountScreenOnSinceCharge()), Integer.valueOf(getDischargeAmountScreenOffSinceCharge()), Long.valueOf(l15 / 1000L), Long.valueOf(l16 / 1000L) });
            break;
          }
        }
      }
      localObject1 = getWakeupReasonStats();
      if (((Map)localObject1).size() > 0)
      {
        localObject1 = ((Map)localObject1).entrySet().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Map.Entry)((Iterator)localObject1).next();
          l1 = ((Timer)((Map.Entry)localObject2).getValue()).getTotalTimeLocked(l5, paramInt1);
          i = ((Timer)((Map.Entry)localObject2).getValue()).getCountLocked(paramInt1);
          dumpLine(paramPrintWriter, 0, str, "wr", new Object[] { "\"" + (String)((Map.Entry)localObject2).getKey() + "\"", Long.valueOf((500L + l1) / 1000L), Integer.valueOf(i) });
        }
      }
    }
    paramContext = new BatteryStatsHelper(paramContext, false, paramBoolean);
    paramContext.create(this);
    paramContext.refreshStats(paramInt1, -1);
    localObject1 = paramContext.getUsageList();
    if ((localObject1 != null) && (((List)localObject1).size() > 0))
    {
      dumpLine(paramPrintWriter, 0, str, "pws", new Object[] { BatteryStatsHelper.makemAh(paramContext.getPowerProfile().getBatteryCapacity()), BatteryStatsHelper.makemAh(paramContext.getComputedPower()), BatteryStatsHelper.makemAh(paramContext.getMinDrainedPower()), BatteryStatsHelper.makemAh(paramContext.getMaxDrainedPower()) });
      j = 0;
      if (j < ((List)localObject1).size())
      {
        localObject2 = (BatterySipper)((List)localObject1).get(j);
        i = 0;
        switch (-getcom-android-internal-os-BatterySipper$DrainTypeSwitchesValues()[localObject2.drainType.ordinal()])
        {
        default: 
          paramContext = "???";
        }
        for (;;)
        {
          dumpLine(paramPrintWriter, i, str, "pwi", new Object[] { paramContext, BatteryStatsHelper.makemAh(((BatterySipper)localObject2).totalPowerMah) });
          j += 1;
          break;
          paramContext = "idle";
          continue;
          paramContext = "cell";
          continue;
          paramContext = "phone";
          continue;
          paramContext = "wifi";
          continue;
          paramContext = "blue";
          continue;
          paramContext = "scrn";
          continue;
          paramContext = "flashlight";
          continue;
          i = ((BatterySipper)localObject2).uidObj.getUid();
          paramContext = "uid";
          continue;
          i = UserHandle.getUid(((BatterySipper)localObject2).userId, 0);
          paramContext = "user";
          continue;
          paramContext = "unacc";
          continue;
          paramContext = "over";
          continue;
          paramContext = "camera";
        }
      }
    }
    i = 0;
    if (i < n)
    {
      int i1 = localSparseArray.keyAt(i);
      if ((paramInt2 >= 0) && (i1 != paramInt2)) {}
      for (;;)
      {
        i += 1;
        break;
        localObject2 = (Uid)localSparseArray.valueAt(i);
        l1 = ((Uid)localObject2).getNetworkActivityBytes(0, paramInt1);
        l2 = ((Uid)localObject2).getNetworkActivityBytes(1, paramInt1);
        l3 = ((Uid)localObject2).getNetworkActivityBytes(2, paramInt1);
        l4 = ((Uid)localObject2).getNetworkActivityBytes(3, paramInt1);
        l7 = ((Uid)localObject2).getNetworkActivityPackets(0, paramInt1);
        l8 = ((Uid)localObject2).getNetworkActivityPackets(1, paramInt1);
        l9 = ((Uid)localObject2).getMobileRadioActiveTime(paramInt1);
        j = ((Uid)localObject2).getMobileRadioActiveCount(paramInt1);
        l10 = ((Uid)localObject2).getMobileRadioApWakeupCount(paramInt1);
        l11 = ((Uid)localObject2).getNetworkActivityPackets(2, paramInt1);
        l12 = ((Uid)localObject2).getNetworkActivityPackets(3, paramInt1);
        l13 = ((Uid)localObject2).getWifiRadioApWakeupCount(paramInt1);
        l14 = ((Uid)localObject2).getNetworkActivityBytes(4, paramInt1);
        l15 = ((Uid)localObject2).getNetworkActivityBytes(5, paramInt1);
        if ((l1 > 0L) || (l2 > 0L))
        {
          dumpLine(paramPrintWriter, i1, str, "nt", new Object[] { Long.valueOf(l1), Long.valueOf(l2), Long.valueOf(l3), Long.valueOf(l4), Long.valueOf(l7), Long.valueOf(l8), Long.valueOf(l11), Long.valueOf(l12), Long.valueOf(l9), Integer.valueOf(j), Long.valueOf(l14), Long.valueOf(l15), Long.valueOf(l10), Long.valueOf(l13) });
          label2823:
          dumpControllerActivityLine(paramPrintWriter, i1, str, "mcd", ((Uid)localObject2).getModemControllerActivity(), paramInt1);
          l1 = ((Uid)localObject2).getFullWifiLockTime(l5, paramInt1);
          l2 = ((Uid)localObject2).getWifiScanTime(l5, paramInt1);
          j = ((Uid)localObject2).getWifiScanCount(paramInt1);
          l3 = ((Uid)localObject2).getWifiRunningTime(l5, paramInt1);
          if ((l1 == 0L) && (l2 == 0L)) {
            break label3158;
          }
        }
        label2891:
        int m;
        for (;;)
        {
          dumpLine(paramPrintWriter, i1, str, "wfl", new Object[] { Long.valueOf(l1), Long.valueOf(l2), Long.valueOf(l3), Integer.valueOf(j), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0) });
          label3158:
          do
          {
            dumpControllerActivityLine(paramPrintWriter, i1, str, "wfcd", ((Uid)localObject2).getWifiControllerActivity(), paramInt1);
            dumpTimer(paramPrintWriter, i1, str, "blem", ((Uid)localObject2).getBluetoothScanTimer(), l5, paramInt1);
            dumpControllerActivityLine(paramPrintWriter, i1, str, "ble", ((Uid)localObject2).getBluetoothControllerActivity(), paramInt1);
            if (!((Uid)localObject2).hasUserActivity()) {
              break label3190;
            }
            paramContext = new Object[4];
            k = 0;
            j = 0;
            while (j < 4)
            {
              m = ((Uid)localObject2).getUserActivityCount(j, paramInt1);
              paramContext[j] = Integer.valueOf(m);
              if (m != 0) {
                k = 1;
              }
              j += 1;
            }
            if ((l3 > 0L) || (l4 > 0L) || (l7 > 0L) || (l8 > 0L) || (l11 > 0L) || (l12 > 0L) || (l9 > 0L) || (j > 0) || (l14 > 0L) || (l15 > 0L) || (l10 > 0L)) {
              break;
            }
            if (l13 <= 0L) {
              break label2823;
            }
            break;
            if (j != 0) {
              break label2891;
            }
          } while (l3 == 0L);
        }
        if (k != 0) {
          dumpLine(paramPrintWriter, i1, str, "ua", paramContext);
        }
        label3190:
        localObject3 = ((Uid)localObject2).getWakelockStats();
        j = ((ArrayMap)localObject3).size() - 1;
        while (j >= 0)
        {
          paramContext = (BatteryStats.Uid.Wakelock)((ArrayMap)localObject3).valueAt(j);
          localStringBuilder.setLength(0);
          localObject1 = printWakeLockCheckin(localStringBuilder, paramContext.getWakeTime(1), l5, "f", paramInt1, "");
          localObject1 = printWakeLockCheckin(localStringBuilder, paramContext.getWakeTime(0), l5, "p", paramInt1, (String)localObject1);
          printWakeLockCheckin(localStringBuilder, paramContext.getWakeTime(2), l5, "w", paramInt1, (String)localObject1);
          if (localStringBuilder.length() > 0)
          {
            localObject1 = (String)((ArrayMap)localObject3).keyAt(j);
            paramContext = (Context)localObject1;
            if (((String)localObject1).indexOf(',') >= 0) {
              paramContext = ((String)localObject1).replace(',', '_');
            }
            dumpLine(paramPrintWriter, i1, str, "wl", new Object[] { paramContext, localStringBuilder.toString() });
          }
          j -= 1;
        }
        paramContext = ((Uid)localObject2).getSyncStats();
        j = paramContext.size() - 1;
        while (j >= 0)
        {
          localObject1 = (Timer)paramContext.valueAt(j);
          l1 = (((Timer)localObject1).getTotalTimeLocked(l5, paramInt1) + 500L) / 1000L;
          k = ((Timer)localObject1).getCountLocked(paramInt1);
          if (l1 != 0L) {
            dumpLine(paramPrintWriter, i1, str, "sy", new Object[] { paramContext.keyAt(j), Long.valueOf(l1), Integer.valueOf(k) });
          }
          j -= 1;
        }
        paramContext = ((Uid)localObject2).getJobStats();
        j = paramContext.size() - 1;
        while (j >= 0)
        {
          localObject1 = (Timer)paramContext.valueAt(j);
          l1 = (((Timer)localObject1).getTotalTimeLocked(l5, paramInt1) + 500L) / 1000L;
          k = ((Timer)localObject1).getCountLocked(paramInt1);
          if (l1 != 0L) {
            dumpLine(paramPrintWriter, i1, str, "jb", new Object[] { paramContext.keyAt(j), Long.valueOf(l1), Integer.valueOf(k) });
          }
          j -= 1;
        }
        dumpTimer(paramPrintWriter, i1, str, "fla", ((Uid)localObject2).getFlashlightTurnedOnTimer(), l5, paramInt1);
        dumpTimer(paramPrintWriter, i1, str, "cam", ((Uid)localObject2).getCameraTurnedOnTimer(), l5, paramInt1);
        dumpTimer(paramPrintWriter, i1, str, "vid", ((Uid)localObject2).getVideoTurnedOnTimer(), l5, paramInt1);
        dumpTimer(paramPrintWriter, i1, str, "aud", ((Uid)localObject2).getAudioTurnedOnTimer(), l5, paramInt1);
        paramContext = ((Uid)localObject2).getSensorStats();
        k = paramContext.size();
        j = 0;
        int i2;
        while (j < k)
        {
          localObject1 = (BatteryStats.Uid.Sensor)paramContext.valueAt(j);
          m = paramContext.keyAt(j);
          localObject1 = ((BatteryStats.Uid.Sensor)localObject1).getSensorTime();
          if (localObject1 != null)
          {
            l1 = (((Timer)localObject1).getTotalTimeLocked(l5, paramInt1) + 500L) / 1000L;
            i2 = ((Timer)localObject1).getCountLocked(paramInt1);
            if (l1 != 0L) {
              dumpLine(paramPrintWriter, i1, str, "sr", new Object[] { Integer.valueOf(m), Long.valueOf(l1), Integer.valueOf(i2) });
            }
          }
          j += 1;
        }
        dumpTimer(paramPrintWriter, i1, str, "vib", ((Uid)localObject2).getVibratorOnTimer(), l5, paramInt1);
        dumpTimer(paramPrintWriter, i1, str, "fg", ((Uid)localObject2).getForegroundActivityTimer(), l5, paramInt1);
        paramContext = new Object[6];
        l1 = 0L;
        j = 0;
        while (j < 6)
        {
          l2 = ((Uid)localObject2).getProcessStateTime(j, l5, paramInt1);
          l1 += l2;
          paramContext[j] = Long.valueOf((500L + l2) / 1000L);
          j += 1;
        }
        if (l1 > 0L) {
          dumpLine(paramPrintWriter, i1, str, "st", paramContext);
        }
        l1 = ((Uid)localObject2).getUserCpuTimeUs(paramInt1);
        l2 = ((Uid)localObject2).getSystemCpuTimeUs(paramInt1);
        l3 = ((Uid)localObject2).getCpuPowerMaUs(paramInt1);
        if ((l1 > 0L) || (l2 > 0L))
        {
          dumpLine(paramPrintWriter, i1, str, "cpu", new Object[] { Long.valueOf(l1 / 1000L), Long.valueOf(l2 / 1000L), Long.valueOf(l3 / 1000L) });
          label4005:
          paramContext = ((Uid)localObject2).getProcessStats();
          j = paramContext.size() - 1;
          label4019:
          if (j < 0) {
            break label4215;
          }
          localObject1 = (BatteryStats.Uid.Proc)paramContext.valueAt(j);
          l1 = ((BatteryStats.Uid.Proc)localObject1).getUserTime(paramInt1);
          l2 = ((BatteryStats.Uid.Proc)localObject1).getSystemTime(paramInt1);
          l3 = ((BatteryStats.Uid.Proc)localObject1).getForegroundTime(paramInt1);
          k = ((BatteryStats.Uid.Proc)localObject1).getStarts(paramInt1);
          m = ((BatteryStats.Uid.Proc)localObject1).getNumCrashes(paramInt1);
          i2 = ((BatteryStats.Uid.Proc)localObject1).getNumAnrs(paramInt1);
          if ((l1 == 0L) && (l2 == 0L)) {
            break label4190;
          }
        }
        for (;;)
        {
          label4097:
          dumpLine(paramPrintWriter, i1, str, "pr", new Object[] { paramContext.keyAt(j), Long.valueOf(l1), Long.valueOf(l2), Long.valueOf(l3), Integer.valueOf(k), Integer.valueOf(i2), Integer.valueOf(m) });
          label4190:
          do
          {
            j -= 1;
            break label4019;
            if (l3 <= 0L) {
              break label4005;
            }
            break;
            if ((l3 != 0L) || (k != 0) || (i2 != 0)) {
              break label4097;
            }
          } while (m == 0);
        }
        label4215:
        paramContext = ((Uid)localObject2).getPackageStats();
        j = paramContext.size() - 1;
        while (j >= 0)
        {
          localObject1 = (BatteryStats.Uid.Pkg)paramContext.valueAt(j);
          k = 0;
          localObject2 = ((BatteryStats.Uid.Pkg)localObject1).getWakeupAlarmStats();
          m = ((ArrayMap)localObject2).size() - 1;
          while (m >= 0)
          {
            i2 = ((Counter)((ArrayMap)localObject2).valueAt(m)).getCountLocked(paramInt1);
            k += i2;
            dumpLine(paramPrintWriter, i1, str, "wua", new Object[] { ((String)((ArrayMap)localObject2).keyAt(m)).replace(',', '_'), Integer.valueOf(i2) });
            m -= 1;
          }
          localObject1 = ((BatteryStats.Uid.Pkg)localObject1).getServiceStats();
          m = ((ArrayMap)localObject1).size() - 1;
          if (m >= 0)
          {
            localObject2 = (BatteryStats.Uid.Pkg.Serv)((ArrayMap)localObject1).valueAt(m);
            l1 = ((BatteryStats.Uid.Pkg.Serv)localObject2).getStartTime(l6, paramInt1);
            i2 = ((BatteryStats.Uid.Pkg.Serv)localObject2).getStarts(paramInt1);
            int i3 = ((BatteryStats.Uid.Pkg.Serv)localObject2).getLaunches(paramInt1);
            if ((l1 != 0L) || (i2 != 0)) {}
            for (;;)
            {
              dumpLine(paramPrintWriter, i1, str, "apk", new Object[] { Integer.valueOf(k), paramContext.keyAt(j), ((ArrayMap)localObject1).keyAt(m), Long.valueOf(l1 / 1000L), Integer.valueOf(i2), Integer.valueOf(i3) });
              do
              {
                m -= 1;
                break;
              } while (i3 == 0);
            }
          }
          j -= 1;
        }
      }
    }
  }
  
  public void dumpCheckinLocked(Context paramContext, PrintWriter paramPrintWriter, List<ApplicationInfo> paramList, int paramInt, long paramLong)
  {
    prepareForDumpLocked();
    dumpLine(paramPrintWriter, 0, "i", "vers", new Object[] { "19", Integer.valueOf(getParcelVersion()), getStartPlatformVersion(), getEndPlatformVersion() });
    getHistoryBaseTime();
    SystemClock.elapsedRealtime();
    if ((paramInt & 0xE) != 0) {}
    for (int i = 1;; i = 0)
    {
      int j;
      if ((((paramInt & 0x10) != 0) || ((paramInt & 0x8) != 0)) && (startIteratingHistoryLocked())) {
        j = 0;
      }
      try
      {
        while (j < getHistoryStringPoolSize())
        {
          paramPrintWriter.print(9);
          paramPrintWriter.print(',');
          paramPrintWriter.print("hsp");
          paramPrintWriter.print(',');
          paramPrintWriter.print(j);
          paramPrintWriter.print(",");
          paramPrintWriter.print(getHistoryTagPoolUid(j));
          paramPrintWriter.print(",\"");
          paramPrintWriter.print(getHistoryTagPoolString(j).replace("\\", "\\\\").replace("\"", "\\\""));
          paramPrintWriter.print("\"");
          paramPrintWriter.println();
          j += 1;
        }
        dumpHistoryLocked(paramPrintWriter, paramInt, paramLong, true);
        finishIteratingHistoryLocked();
        if ((i != 0) && ((paramInt & 0x6) == 0)) {
          return;
        }
      }
      finally
      {
        finishIteratingHistoryLocked();
      }
      if (paramList != null)
      {
        SparseArray localSparseArray = new SparseArray();
        j = 0;
        Pair localPair;
        while (j < paramList.size())
        {
          ApplicationInfo localApplicationInfo = (ApplicationInfo)paramList.get(j);
          localPair = (Pair)localSparseArray.get(UserHandle.getAppId(localApplicationInfo.uid));
          localObject = localPair;
          if (localPair == null)
          {
            localObject = new Pair(new ArrayList(), new MutableBoolean(false));
            localSparseArray.put(UserHandle.getAppId(localApplicationInfo.uid), localObject);
          }
          ((ArrayList)((Pair)localObject).first).add(localApplicationInfo.packageName);
          j += 1;
        }
        paramList = getUidStats();
        int m = paramList.size();
        Object localObject = new String[2];
        j = 0;
        if (j < m)
        {
          int n = UserHandle.getAppId(paramList.keyAt(j));
          localPair = (Pair)localSparseArray.get(n);
          if ((localPair == null) || (((MutableBoolean)localPair.second).value)) {}
          for (;;)
          {
            j += 1;
            break;
            ((MutableBoolean)localPair.second).value = true;
            int k = 0;
            while (k < ((ArrayList)localPair.first).size())
            {
              localObject[0] = Integer.toString(n);
              localObject[1] = ((String)((ArrayList)localPair.first).get(k));
              dumpLine(paramPrintWriter, 0, "i", "uid", (Object[])localObject);
              k += 1;
            }
          }
        }
      }
      if ((i == 0) || ((paramInt & 0x2) != 0))
      {
        dumpDurationSteps(paramPrintWriter, "", "dsd", getDischargeLevelStepTracker(), true);
        paramList = new String[1];
        paramLong = computeBatteryTimeRemaining(SystemClock.elapsedRealtime());
        if (paramLong >= 0L)
        {
          paramList[0] = Long.toString(paramLong);
          dumpLine(paramPrintWriter, 0, "i", "dtr", paramList);
        }
        dumpDurationSteps(paramPrintWriter, "", "csd", getChargeLevelStepTracker(), true);
        paramLong = computeChargeTimeRemaining(SystemClock.elapsedRealtime());
        if (paramLong >= 0L)
        {
          paramList[0] = Long.toString(paramLong);
          dumpLine(paramPrintWriter, 0, "i", "ctr", paramList);
        }
        if ((paramInt & 0x40) == 0) {
          break label671;
        }
      }
      label671:
      for (boolean bool = true;; bool = false)
      {
        dumpCheckinLocked(paramContext, paramPrintWriter, 0, -1, bool);
        return;
      }
    }
  }
  
  public void dumpLocked(Context paramContext, PrintWriter paramPrintWriter, int paramInt1, int paramInt2, long paramLong)
  {
    prepareForDumpLocked();
    if ((paramInt1 & 0xE) != 0) {}
    for (int j = 1; ((paramInt1 & 0x8) == 0) && (j != 0); j = 0)
    {
      if ((j == 0) || ((paramInt1 & 0x6) != 0)) {
        break label288;
      }
      return;
    }
    long l1 = getHistoryTotalSize();
    long l2 = getHistoryUsedSize();
    if (startIteratingHistoryLocked()) {}
    for (;;)
    {
      Object localObject1;
      Object localObject2;
      try
      {
        paramPrintWriter.print("Battery History (");
        paramPrintWriter.print(100L * l2 / l1);
        paramPrintWriter.print("% used, ");
        printSizeValue(paramPrintWriter, l2);
        paramPrintWriter.print(" used of ");
        printSizeValue(paramPrintWriter, l1);
        paramPrintWriter.print(", ");
        paramPrintWriter.print(getHistoryStringPoolSize());
        paramPrintWriter.print(" strings using ");
        printSizeValue(paramPrintWriter, getHistoryStringPoolBytes());
        paramPrintWriter.println("):");
        dumpHistoryLocked(paramPrintWriter, paramInt1, paramLong, false);
        paramPrintWriter.println();
        finishIteratingHistoryLocked();
        if (!startIteratingOldHistoryLocked()) {
          break;
        }
        try
        {
          localObject1 = new HistoryItem();
          paramPrintWriter.println("Old battery History:");
          localObject2 = new HistoryPrinter();
          paramLong = -1L;
          if (!getNextOldHistoryLocked((HistoryItem)localObject1)) {
            break label277;
          }
          l1 = paramLong;
          if (paramLong >= 0L) {
            break label1320;
          }
          l1 = ((HistoryItem)localObject1).time;
        }
        finally
        {
          label237:
          finishIteratingOldHistoryLocked();
        }
        ((HistoryPrinter)localObject2).printNextItem(paramPrintWriter, (HistoryItem)localObject1, l1, false, bool);
        paramLong = l1;
        continue;
        bool = false;
      }
      finally
      {
        finishIteratingHistoryLocked();
      }
      label277:
      label288:
      label895:
      label1050:
      label1223:
      label1314:
      label1320:
      do
      {
        break label237;
        paramPrintWriter.println();
        finishIteratingOldHistoryLocked();
        break;
        int i;
        Object localObject3;
        if (j == 0)
        {
          localObject1 = getUidStats();
          int i1 = ((SparseArray)localObject1).size();
          i = 0;
          l1 = SystemClock.elapsedRealtime();
          int k = 0;
          while (k < i1)
          {
            localObject2 = ((Uid)((SparseArray)localObject1).valueAt(k)).getPidStats();
            int n = i;
            if (localObject2 != null)
            {
              int m = 0;
              n = i;
              if (m < ((SparseArray)localObject2).size())
              {
                localObject3 = (BatteryStats.Uid.Pid)((SparseArray)localObject2).valueAt(m);
                n = i;
                if (i == 0)
                {
                  paramPrintWriter.println("Per-PID Stats:");
                  n = 1;
                }
                l2 = ((BatteryStats.Uid.Pid)localObject3).mWakeSumMs;
                if (((BatteryStats.Uid.Pid)localObject3).mWakeNesting > 0) {}
                for (paramLong = l1 - ((BatteryStats.Uid.Pid)localObject3).mWakeStartMs;; paramLong = 0L)
                {
                  paramPrintWriter.print("  PID ");
                  paramPrintWriter.print(((SparseArray)localObject2).keyAt(m));
                  paramPrintWriter.print(" wake time: ");
                  TimeUtils.formatDuration(l2 + paramLong, paramPrintWriter);
                  paramPrintWriter.println("");
                  m += 1;
                  i = n;
                  break;
                }
              }
            }
            k += 1;
            i = n;
          }
          if (i != 0) {
            paramPrintWriter.println();
          }
        }
        if ((j == 0) || ((paramInt1 & 0x2) != 0))
        {
          if (dumpDurationSteps(paramPrintWriter, "  ", "Discharge step durations:", getDischargeLevelStepTracker(), false))
          {
            paramLong = computeBatteryTimeRemaining(SystemClock.elapsedRealtime());
            if (paramLong >= 0L)
            {
              paramPrintWriter.print("  Estimated discharge time remaining: ");
              TimeUtils.formatDuration(paramLong / 1000L, paramPrintWriter);
              paramPrintWriter.println();
            }
            localObject1 = getDischargeLevelStepTracker();
            i = 0;
            while (i < STEP_LEVEL_MODES_OF_INTEREST.length)
            {
              dumpTimeEstimate(paramPrintWriter, "  Estimated ", STEP_LEVEL_MODE_LABELS[i], " time: ", ((LevelStepTracker)localObject1).computeTimeEstimate(STEP_LEVEL_MODES_OF_INTEREST[i], STEP_LEVEL_MODE_VALUES[i], null));
              i += 1;
            }
            paramPrintWriter.println();
          }
          if (dumpDurationSteps(paramPrintWriter, "  ", "Charge step durations:", getChargeLevelStepTracker(), false))
          {
            paramLong = computeChargeTimeRemaining(SystemClock.elapsedRealtime());
            if (paramLong >= 0L)
            {
              paramPrintWriter.print("  Estimated charge time remaining: ");
              TimeUtils.formatDuration(paramLong / 1000L, paramPrintWriter);
              paramPrintWriter.println();
            }
            paramPrintWriter.println();
          }
        }
        if ((j == 0) || ((paramInt1 & 0x6) != 0))
        {
          paramPrintWriter.println("Daily stats:");
          paramPrintWriter.print("  Current start time: ");
          paramPrintWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getCurrentDailyStartTime()).toString());
          paramPrintWriter.print("  Next min deadline: ");
          paramPrintWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getNextMinDailyDeadline()).toString());
          paramPrintWriter.print("  Next max deadline: ");
          paramPrintWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getNextMaxDailyDeadline()).toString());
          localObject1 = new StringBuilder(64);
          localObject2 = new int[1];
          localObject3 = getDailyDischargeLevelStepTracker();
          LevelStepTracker localLevelStepTracker = getDailyChargeLevelStepTracker();
          ArrayList localArrayList = getDailyPackageChanges();
          if ((((LevelStepTracker)localObject3).mNumStepDurations > 0) || (localLevelStepTracker.mNumStepDurations > 0))
          {
            if (((paramInt1 & 0x4) != 0) || (j == 0)) {
              break label1050;
            }
            paramPrintWriter.println("  Current daily steps:");
            dumpDailyLevelStepSummary(paramPrintWriter, "    ", "Discharge", (LevelStepTracker)localObject3, (StringBuilder)localObject1, (int[])localObject2);
            dumpDailyLevelStepSummary(paramPrintWriter, "    ", "Charge", localLevelStepTracker, (StringBuilder)localObject1, (int[])localObject2);
            i = 0;
          }
          for (;;)
          {
            localObject3 = getDailyItemLocked(i);
            if (localObject3 == null) {
              break label1223;
            }
            i += 1;
            if ((paramInt1 & 0x4) != 0) {
              paramPrintWriter.println();
            }
            paramPrintWriter.print("  Daily from ");
            paramPrintWriter.print(DateFormat.format("yyyy-MM-dd-HH-mm-ss", ((DailyItem)localObject3).mStartTime).toString());
            paramPrintWriter.print(" to ");
            paramPrintWriter.print(DateFormat.format("yyyy-MM-dd-HH-mm-ss", ((DailyItem)localObject3).mEndTime).toString());
            paramPrintWriter.println(":");
            if (((paramInt1 & 0x4) == 0) && (j != 0))
            {
              dumpDailyLevelStepSummary(paramPrintWriter, "    ", "Discharge", ((DailyItem)localObject3).mDischargeSteps, (StringBuilder)localObject1, (int[])localObject2);
              dumpDailyLevelStepSummary(paramPrintWriter, "    ", "Charge", ((DailyItem)localObject3).mChargeSteps, (StringBuilder)localObject1, (int[])localObject2);
              continue;
              if (localArrayList == null) {
                break label895;
              }
              break;
              if (dumpDurationSteps(paramPrintWriter, "    ", "  Current daily discharge step durations:", (LevelStepTracker)localObject3, false)) {
                dumpDailyLevelStepSummary(paramPrintWriter, "      ", "Discharge", (LevelStepTracker)localObject3, (StringBuilder)localObject1, (int[])localObject2);
              }
              if (dumpDurationSteps(paramPrintWriter, "    ", "  Current daily charge step durations:", localLevelStepTracker, false)) {
                dumpDailyLevelStepSummary(paramPrintWriter, "      ", "Charge", localLevelStepTracker, (StringBuilder)localObject1, (int[])localObject2);
              }
              dumpDailyPackageChanges(paramPrintWriter, "    ", localArrayList);
              break label895;
            }
            if (dumpDurationSteps(paramPrintWriter, "      ", "    Discharge step durations:", ((DailyItem)localObject3).mDischargeSteps, false)) {
              dumpDailyLevelStepSummary(paramPrintWriter, "        ", "Discharge", ((DailyItem)localObject3).mDischargeSteps, (StringBuilder)localObject1, (int[])localObject2);
            }
            if (dumpDurationSteps(paramPrintWriter, "      ", "    Charge step durations:", ((DailyItem)localObject3).mChargeSteps, false)) {
              dumpDailyLevelStepSummary(paramPrintWriter, "        ", "Charge", ((DailyItem)localObject3).mChargeSteps, (StringBuilder)localObject1, (int[])localObject2);
            }
            dumpDailyPackageChanges(paramPrintWriter, "    ", ((DailyItem)localObject3).mPackageChanges);
          }
          paramPrintWriter.println();
        }
        if ((j == 0) || ((paramInt1 & 0x2) != 0))
        {
          paramPrintWriter.println("Statistics since last charge:");
          paramPrintWriter.println("  System starts: " + getStartCount() + ", currently on battery: " + getIsOnBattery());
          if ((paramInt1 & 0x40) == 0) {
            break label1314;
          }
        }
        for (bool = true;; bool = false)
        {
          dumpLocked(paramContext, paramPrintWriter, "", 0, paramInt2, bool);
          paramPrintWriter.println();
          return;
        }
      } while ((paramInt1 & 0x20) == 0);
      boolean bool = true;
    }
  }
  
  public final void dumpLocked(Context paramContext, PrintWriter paramPrintWriter, String paramString, int paramInt1, int paramInt2)
  {
    dumpLocked(paramContext, paramPrintWriter, paramString, paramInt1, paramInt2, BatteryStatsHelper.checkWifiOnly(paramContext));
  }
  
  public final void dumpLocked(Context paramContext, PrintWriter paramPrintWriter, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    long l4 = SystemClock.uptimeMillis() * 1000L;
    long l5 = SystemClock.elapsedRealtime() * 1000L;
    long l6 = getBatteryUptime(l4);
    long l1 = computeBatteryUptime(l4, paramInt1);
    long l7 = computeBatteryRealtime(l5, paramInt1);
    long l2 = computeRealtime(l5, paramInt1);
    long l3 = computeUptime(l4, paramInt1);
    l4 = computeBatteryScreenOffUptime(l4, paramInt1);
    long l8 = computeBatteryScreenOffRealtime(l5, paramInt1);
    long l9 = computeBatteryTimeRemaining(l5);
    long l10 = computeChargeTimeRemaining(l5);
    StringBuilder localStringBuilder = new StringBuilder(128);
    SparseArray localSparseArray = getUidStats();
    int i4 = localSparseArray.size();
    int i = getEstimatedBatteryCapacity();
    if (i > 0)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Estimated battery capacity: ");
      localStringBuilder.append(BatteryStatsHelper.makemAh(i));
      localStringBuilder.append(" mAh");
      paramPrintWriter.println(localStringBuilder.toString());
    }
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Time on battery: ");
    formatTimeMs(localStringBuilder, l7 / 1000L);
    localStringBuilder.append("(");
    localStringBuilder.append(formatRatioLocked(l7, l2));
    localStringBuilder.append(") realtime, ");
    formatTimeMs(localStringBuilder, l1 / 1000L);
    localStringBuilder.append("(");
    localStringBuilder.append(formatRatioLocked(l1, l2));
    localStringBuilder.append(") uptime");
    paramPrintWriter.println(localStringBuilder.toString());
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Time on battery screen off: ");
    formatTimeMs(localStringBuilder, l8 / 1000L);
    localStringBuilder.append("(");
    localStringBuilder.append(formatRatioLocked(l8, l2));
    localStringBuilder.append(") realtime, ");
    formatTimeMs(localStringBuilder, l4 / 1000L);
    localStringBuilder.append("(");
    localStringBuilder.append(formatRatioLocked(l4, l2));
    localStringBuilder.append(") uptime");
    paramPrintWriter.println(localStringBuilder.toString());
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Total run time: ");
    formatTimeMs(localStringBuilder, l2 / 1000L);
    localStringBuilder.append("realtime, ");
    formatTimeMs(localStringBuilder, l3 / 1000L);
    localStringBuilder.append("uptime");
    paramPrintWriter.println(localStringBuilder.toString());
    if (l9 >= 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Battery time remaining: ");
      formatTimeMs(localStringBuilder, l9 / 1000L);
      paramPrintWriter.println(localStringBuilder.toString());
    }
    if (l10 >= 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Charge time remaining: ");
      formatTimeMs(localStringBuilder, l10 / 1000L);
      paramPrintWriter.println(localStringBuilder.toString());
    }
    l1 = getDischargeCoulombCounter().getCountLocked(paramInt1);
    if (l1 >= 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Discharge: ");
      localStringBuilder.append(BatteryStatsHelper.makemAh(l1 / 1000.0D));
      localStringBuilder.append(" mAh");
      paramPrintWriter.println(localStringBuilder.toString());
    }
    l2 = getDischargeScreenOffCoulombCounter().getCountLocked(paramInt1);
    if (l2 >= 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Screen off discharge: ");
      localStringBuilder.append(BatteryStatsHelper.makemAh(l2 / 1000.0D));
      localStringBuilder.append(" mAh");
      paramPrintWriter.println(localStringBuilder.toString());
    }
    l1 -= l2;
    if (l1 >= 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Screen on discharge: ");
      localStringBuilder.append(BatteryStatsHelper.makemAh(l1 / 1000.0D));
      localStringBuilder.append(" mAh");
      paramPrintWriter.println(localStringBuilder.toString());
    }
    paramPrintWriter.print("  Start clock time: ");
    paramPrintWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getStartClockTime()).toString());
    l1 = getScreenOnTime(l5, paramInt1);
    long l13 = getInteractiveTime(l5, paramInt1);
    l2 = getPowerSaveModeEnabledTime(l5, paramInt1);
    l3 = getDeviceIdleModeTime(1, l5, paramInt1);
    l4 = getDeviceIdleModeTime(2, l5, paramInt1);
    l8 = getDeviceIdlingTime(1, l5, paramInt1);
    long l11 = getDeviceIdlingTime(2, l5, paramInt1);
    long l12 = getPhoneOnTime(l5, paramInt1);
    l9 = getGlobalWifiRunningTime(l5, paramInt1);
    l10 = getWifiOnTime(l5, paramInt1);
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Screen on: ");
    formatTimeMs(localStringBuilder, l1 / 1000L);
    localStringBuilder.append("(");
    localStringBuilder.append(formatRatioLocked(l1, l7));
    localStringBuilder.append(") ");
    localStringBuilder.append(getScreenOnCount(paramInt1));
    localStringBuilder.append("x, Interactive: ");
    formatTimeMs(localStringBuilder, l13 / 1000L);
    localStringBuilder.append("(");
    localStringBuilder.append(formatRatioLocked(l13, l7));
    localStringBuilder.append(")");
    paramPrintWriter.println(localStringBuilder.toString());
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Screen brightnesses:");
    int m = 0;
    i = 0;
    if (i < 5)
    {
      l13 = getScreenBrightnessTime(i, l5, paramInt1);
      if (l13 == 0L) {}
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append("\n    ");
        localStringBuilder.append(paramString);
        m = 1;
        localStringBuilder.append(SCREEN_BRIGHTNESS_NAMES[i]);
        localStringBuilder.append(" ");
        formatTimeMs(localStringBuilder, l13 / 1000L);
        localStringBuilder.append("(");
        localStringBuilder.append(formatRatioLocked(l13, l1));
        localStringBuilder.append(")");
      }
    }
    if (m == 0) {
      localStringBuilder.append(" (no activity)");
    }
    paramPrintWriter.println(localStringBuilder.toString());
    if (l2 != 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Power save mode enabled: ");
      formatTimeMs(localStringBuilder, l2 / 1000L);
      localStringBuilder.append("(");
      localStringBuilder.append(formatRatioLocked(l2, l7));
      localStringBuilder.append(")");
      paramPrintWriter.println(localStringBuilder.toString());
    }
    if (l8 != 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Device light idling: ");
      formatTimeMs(localStringBuilder, l8 / 1000L);
      localStringBuilder.append("(");
      localStringBuilder.append(formatRatioLocked(l8, l7));
      localStringBuilder.append(") ");
      localStringBuilder.append(getDeviceIdlingCount(1, paramInt1));
      localStringBuilder.append("x");
      paramPrintWriter.println(localStringBuilder.toString());
    }
    if (l3 != 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Idle mode light time: ");
      formatTimeMs(localStringBuilder, l3 / 1000L);
      localStringBuilder.append("(");
      localStringBuilder.append(formatRatioLocked(l3, l7));
      localStringBuilder.append(") ");
      localStringBuilder.append(getDeviceIdleModeCount(1, paramInt1));
      localStringBuilder.append("x");
      localStringBuilder.append(" -- longest ");
      formatTimeMs(localStringBuilder, getLongestDeviceIdleModeTime(1));
      paramPrintWriter.println(localStringBuilder.toString());
    }
    if (l11 != 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Device full idling: ");
      formatTimeMs(localStringBuilder, l11 / 1000L);
      localStringBuilder.append("(");
      localStringBuilder.append(formatRatioLocked(l11, l7));
      localStringBuilder.append(") ");
      localStringBuilder.append(getDeviceIdlingCount(2, paramInt1));
      localStringBuilder.append("x");
      paramPrintWriter.println(localStringBuilder.toString());
    }
    if (l4 != 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Idle mode full time: ");
      formatTimeMs(localStringBuilder, l4 / 1000L);
      localStringBuilder.append("(");
      localStringBuilder.append(formatRatioLocked(l4, l7));
      localStringBuilder.append(") ");
      localStringBuilder.append(getDeviceIdleModeCount(2, paramInt1));
      localStringBuilder.append("x");
      localStringBuilder.append(" -- longest ");
      formatTimeMs(localStringBuilder, getLongestDeviceIdleModeTime(2));
      paramPrintWriter.println(localStringBuilder.toString());
    }
    if (l12 != 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Active phone call: ");
      formatTimeMs(localStringBuilder, l12 / 1000L);
      localStringBuilder.append("(");
      localStringBuilder.append(formatRatioLocked(l12, l7));
      localStringBuilder.append(") ");
      localStringBuilder.append(getPhoneOnCount(paramInt1));
      localStringBuilder.append("x");
    }
    i = getNumConnectivityChange(paramInt1);
    if (i != 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  Connectivity changes: ");
      paramPrintWriter.println(i);
    }
    l1 = 0L;
    l2 = 0L;
    Object localObject1 = new ArrayList();
    i = 0;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Timer localTimer;
    while (i < i4)
    {
      localObject2 = (Uid)localSparseArray.valueAt(i);
      localObject3 = ((Uid)localObject2).getWakelockStats();
      m = ((ArrayMap)localObject3).size() - 1;
      l4 = l1;
      while (m >= 0)
      {
        localObject4 = (BatteryStats.Uid.Wakelock)((ArrayMap)localObject3).valueAt(m);
        localTimer = ((BatteryStats.Uid.Wakelock)localObject4).getWakeTime(1);
        l1 = l4;
        if (localTimer != null) {
          l1 = l4 + localTimer.getTotalTimeLocked(l5, paramInt1);
        }
        localObject4 = ((BatteryStats.Uid.Wakelock)localObject4).getWakeTime(0);
        l3 = l2;
        if (localObject4 != null)
        {
          l4 = ((Timer)localObject4).getTotalTimeLocked(l5, paramInt1);
          l3 = l2;
          if (l4 > 0L)
          {
            if (paramInt2 < 0) {
              ((ArrayList)localObject1).add(new TimerEntry((String)((ArrayMap)localObject3).keyAt(m), ((Uid)localObject2).getUid(), (Timer)localObject4, l4));
            }
            l3 = l2 + l4;
          }
        }
        m -= 1;
        l4 = l1;
        l2 = l3;
      }
      i += 1;
      l1 = l4;
    }
    l8 = getNetworkActivityBytes(0, paramInt1);
    long l15 = getNetworkActivityBytes(1, paramInt1);
    l11 = getNetworkActivityBytes(2, paramInt1);
    l12 = getNetworkActivityBytes(3, paramInt1);
    long l16 = getNetworkActivityPackets(0, paramInt1);
    long l17 = getNetworkActivityPackets(1, paramInt1);
    l13 = getNetworkActivityPackets(2, paramInt1);
    long l14 = getNetworkActivityPackets(3, paramInt1);
    l3 = getNetworkActivityBytes(4, paramInt1);
    l4 = getNetworkActivityBytes(5, paramInt1);
    if (l1 != 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Total full wakelock time: ");
      formatTimeMsNoSpace(localStringBuilder, (500L + l1) / 1000L);
      paramPrintWriter.println(localStringBuilder.toString());
    }
    if (l2 != 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Total partial wakelock time: ");
      formatTimeMsNoSpace(localStringBuilder, (500L + l2) / 1000L);
      paramPrintWriter.println(localStringBuilder.toString());
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  Mobile total received: ");
    paramPrintWriter.print(formatBytesLocked(l8));
    paramPrintWriter.print(", sent: ");
    paramPrintWriter.print(formatBytesLocked(l15));
    paramPrintWriter.print(" (packets received ");
    paramPrintWriter.print(l16);
    paramPrintWriter.print(", sent ");
    paramPrintWriter.print(l17);
    paramPrintWriter.println(")");
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Phone signal levels:");
    m = 0;
    i = 0;
    if (i < 5)
    {
      l1 = getPhoneSignalStrengthTime(i, l5, paramInt1);
      if (l1 == 0L) {}
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append("\n    ");
        localStringBuilder.append(paramString);
        m = 1;
        localStringBuilder.append(SignalStrength.SIGNAL_STRENGTH_NAMES[i]);
        localStringBuilder.append(" ");
        formatTimeMs(localStringBuilder, l1 / 1000L);
        localStringBuilder.append("(");
        localStringBuilder.append(formatRatioLocked(l1, l7));
        localStringBuilder.append(") ");
        localStringBuilder.append(getPhoneSignalStrengthCount(i, paramInt1));
        localStringBuilder.append("x");
      }
    }
    if (m == 0) {
      localStringBuilder.append(" (no activity)");
    }
    paramPrintWriter.println(localStringBuilder.toString());
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Signal scanning time: ");
    formatTimeMsNoSpace(localStringBuilder, getPhoneSignalScanningTime(l5, paramInt1) / 1000L);
    paramPrintWriter.println(localStringBuilder.toString());
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Radio types:");
    m = 0;
    i = 0;
    if (i < 17)
    {
      l1 = getPhoneDataConnectionTime(i, l5, paramInt1);
      if (l1 == 0L) {}
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append("\n    ");
        localStringBuilder.append(paramString);
        m = 1;
        localStringBuilder.append(DATA_CONNECTION_NAMES[i]);
        localStringBuilder.append(" ");
        formatTimeMs(localStringBuilder, l1 / 1000L);
        localStringBuilder.append("(");
        localStringBuilder.append(formatRatioLocked(l1, l7));
        localStringBuilder.append(") ");
        localStringBuilder.append(getPhoneDataConnectionCount(i, paramInt1));
        localStringBuilder.append("x");
      }
    }
    if (m == 0) {
      localStringBuilder.append(" (no activity)");
    }
    paramPrintWriter.println(localStringBuilder.toString());
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Mobile radio active time: ");
    l8 = getMobileRadioActiveTime(l5, paramInt1);
    formatTimeMs(localStringBuilder, l8 / 1000L);
    localStringBuilder.append("(");
    localStringBuilder.append(formatRatioLocked(l8, l7));
    localStringBuilder.append(") ");
    localStringBuilder.append(getMobileRadioActiveCount(paramInt1));
    localStringBuilder.append("x");
    paramPrintWriter.println(localStringBuilder.toString());
    l1 = getMobileRadioActiveUnknownTime(paramInt1);
    if (l1 != 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Mobile radio active unknown time: ");
      formatTimeMs(localStringBuilder, l1 / 1000L);
      localStringBuilder.append("(");
      localStringBuilder.append(formatRatioLocked(l1, l7));
      localStringBuilder.append(") ");
      localStringBuilder.append(getMobileRadioActiveUnknownCount(paramInt1));
      localStringBuilder.append("x");
      paramPrintWriter.println(localStringBuilder.toString());
    }
    l1 = getMobileRadioActiveAdjustedTime(paramInt1);
    if (l1 != 0L)
    {
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Mobile radio active adjusted time: ");
      formatTimeMs(localStringBuilder, l1 / 1000L);
      localStringBuilder.append("(");
      localStringBuilder.append(formatRatioLocked(l1, l7));
      localStringBuilder.append(")");
      paramPrintWriter.println(localStringBuilder.toString());
    }
    printControllerActivity(paramPrintWriter, localStringBuilder, paramString, "Radio", getModemControllerActivity(), paramInt1);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  Wi-Fi total received: ");
    paramPrintWriter.print(formatBytesLocked(l11));
    paramPrintWriter.print(", sent: ");
    paramPrintWriter.print(formatBytesLocked(l12));
    paramPrintWriter.print(" (packets received ");
    paramPrintWriter.print(l13);
    paramPrintWriter.print(", sent ");
    paramPrintWriter.print(l14);
    paramPrintWriter.println(")");
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Wifi on: ");
    formatTimeMs(localStringBuilder, l10 / 1000L);
    localStringBuilder.append("(");
    localStringBuilder.append(formatRatioLocked(l10, l7));
    localStringBuilder.append("), Wifi running: ");
    formatTimeMs(localStringBuilder, l9 / 1000L);
    localStringBuilder.append("(");
    localStringBuilder.append(formatRatioLocked(l9, l7));
    localStringBuilder.append(")");
    paramPrintWriter.println(localStringBuilder.toString());
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Wifi states:");
    m = 0;
    i = 0;
    if (i < 8)
    {
      l1 = getWifiStateTime(i, l5, paramInt1);
      if (l1 == 0L) {}
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append("\n    ");
        m = 1;
        localStringBuilder.append(WIFI_STATE_NAMES[i]);
        localStringBuilder.append(" ");
        formatTimeMs(localStringBuilder, l1 / 1000L);
        localStringBuilder.append("(");
        localStringBuilder.append(formatRatioLocked(l1, l7));
        localStringBuilder.append(") ");
        localStringBuilder.append(getWifiStateCount(i, paramInt1));
        localStringBuilder.append("x");
      }
    }
    if (m == 0) {
      localStringBuilder.append(" (no activity)");
    }
    paramPrintWriter.println(localStringBuilder.toString());
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Wifi supplicant states:");
    m = 0;
    i = 0;
    if (i < 13)
    {
      l1 = getWifiSupplStateTime(i, l5, paramInt1);
      if (l1 == 0L) {}
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append("\n    ");
        m = 1;
        localStringBuilder.append(WIFI_SUPPL_STATE_NAMES[i]);
        localStringBuilder.append(" ");
        formatTimeMs(localStringBuilder, l1 / 1000L);
        localStringBuilder.append("(");
        localStringBuilder.append(formatRatioLocked(l1, l7));
        localStringBuilder.append(") ");
        localStringBuilder.append(getWifiSupplStateCount(i, paramInt1));
        localStringBuilder.append("x");
      }
    }
    if (m == 0) {
      localStringBuilder.append(" (no activity)");
    }
    paramPrintWriter.println(localStringBuilder.toString());
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Wifi signal levels:");
    m = 0;
    i = 0;
    if (i < 5)
    {
      l1 = getWifiSignalStrengthTime(i, l5, paramInt1);
      if (l1 == 0L) {}
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append("\n    ");
        localStringBuilder.append(paramString);
        m = 1;
        localStringBuilder.append("level(");
        localStringBuilder.append(i);
        localStringBuilder.append(") ");
        formatTimeMs(localStringBuilder, l1 / 1000L);
        localStringBuilder.append("(");
        localStringBuilder.append(formatRatioLocked(l1, l7));
        localStringBuilder.append(") ");
        localStringBuilder.append(getWifiSignalStrengthCount(i, paramInt1));
        localStringBuilder.append("x");
      }
    }
    if (m == 0) {
      localStringBuilder.append(" (no activity)");
    }
    paramPrintWriter.println(localStringBuilder.toString());
    printControllerActivity(paramPrintWriter, localStringBuilder, paramString, "WiFi", getWifiControllerActivity(), paramInt1);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  Bluetooth total received: ");
    paramPrintWriter.print(formatBytesLocked(l3));
    paramPrintWriter.print(", sent: ");
    paramPrintWriter.println(formatBytesLocked(l4));
    l1 = getBluetoothScanTime(l5, paramInt1) / 1000L;
    localStringBuilder.setLength(0);
    localStringBuilder.append(paramString);
    localStringBuilder.append("  Bluetooth scan time: ");
    formatTimeMs(localStringBuilder, l1);
    paramPrintWriter.println(localStringBuilder.toString());
    printControllerActivity(paramPrintWriter, localStringBuilder, paramString, "Bluetooth", getBluetoothControllerActivity(), paramInt1);
    paramPrintWriter.println();
    if (paramInt1 == 2) {
      if (getIsOnBattery())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("  Device is currently unplugged");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    Discharge cycle start level: ");
        paramPrintWriter.println(getDischargeStartLevel());
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    Discharge cycle current level: ");
        paramPrintWriter.println(getDischargeCurrentLevel());
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    Amount discharged while screen on: ");
        paramPrintWriter.println(getDischargeAmountScreenOn());
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    Amount discharged while screen off: ");
        paramPrintWriter.println(getDischargeAmountScreenOff());
        paramPrintWriter.println(" ");
        label4154:
        paramContext = new BatteryStatsHelper(paramContext, false, paramBoolean);
        paramContext.create(this);
        paramContext.refreshStats(paramInt1, -1);
        localObject2 = paramContext.getUsageList();
        if ((localObject2 == null) || (((List)localObject2).size() <= 0)) {
          break label5049;
        }
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("  Estimated power use (mAh):");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    Capacity: ");
        printmAh(paramPrintWriter, paramContext.getPowerProfile().getBatteryCapacity());
        paramPrintWriter.print(", Computed drain: ");
        printmAh(paramPrintWriter, paramContext.getComputedPower());
        paramPrintWriter.print(", actual drain: ");
        printmAh(paramPrintWriter, paramContext.getMinDrainedPower());
        if (paramContext.getMinDrainedPower() != paramContext.getMaxDrainedPower())
        {
          paramPrintWriter.print("-");
          printmAh(paramPrintWriter, paramContext.getMaxDrainedPower());
        }
        paramPrintWriter.println();
        i = 0;
        label4302:
        if (i >= ((List)localObject2).size()) {
          break label5045;
        }
        localObject3 = (BatterySipper)((List)localObject2).get(i);
        paramPrintWriter.print(paramString);
        switch (-getcom-android-internal-os-BatterySipper$DrainTypeSwitchesValues()[localObject3.drainType.ordinal()])
        {
        default: 
          paramPrintWriter.print("    ???: ");
        }
      }
    }
    for (;;)
    {
      printmAh(paramPrintWriter, ((BatterySipper)localObject3).totalPowerMah);
      if (((BatterySipper)localObject3).usagePowerMah != ((BatterySipper)localObject3).totalPowerMah)
      {
        paramPrintWriter.print(" (");
        if (((BatterySipper)localObject3).usagePowerMah != 0.0D)
        {
          paramPrintWriter.print(" usage=");
          printmAh(paramPrintWriter, ((BatterySipper)localObject3).usagePowerMah);
        }
        if (((BatterySipper)localObject3).cpuPowerMah != 0.0D)
        {
          paramPrintWriter.print(" cpu=");
          printmAh(paramPrintWriter, ((BatterySipper)localObject3).cpuPowerMah);
        }
        if (((BatterySipper)localObject3).wakeLockPowerMah != 0.0D)
        {
          paramPrintWriter.print(" wake=");
          printmAh(paramPrintWriter, ((BatterySipper)localObject3).wakeLockPowerMah);
        }
        if (((BatterySipper)localObject3).mobileRadioPowerMah != 0.0D)
        {
          paramPrintWriter.print(" radio=");
          printmAh(paramPrintWriter, ((BatterySipper)localObject3).mobileRadioPowerMah);
        }
        if (((BatterySipper)localObject3).wifiPowerMah != 0.0D)
        {
          paramPrintWriter.print(" wifi=");
          printmAh(paramPrintWriter, ((BatterySipper)localObject3).wifiPowerMah);
        }
        if (((BatterySipper)localObject3).bluetoothPowerMah != 0.0D)
        {
          paramPrintWriter.print(" bt=");
          printmAh(paramPrintWriter, ((BatterySipper)localObject3).bluetoothPowerMah);
        }
        if (((BatterySipper)localObject3).gpsPowerMah != 0.0D)
        {
          paramPrintWriter.print(" gps=");
          printmAh(paramPrintWriter, ((BatterySipper)localObject3).gpsPowerMah);
        }
        if (((BatterySipper)localObject3).sensorPowerMah != 0.0D)
        {
          paramPrintWriter.print(" sensor=");
          printmAh(paramPrintWriter, ((BatterySipper)localObject3).sensorPowerMah);
        }
        if (((BatterySipper)localObject3).cameraPowerMah != 0.0D)
        {
          paramPrintWriter.print(" camera=");
          printmAh(paramPrintWriter, ((BatterySipper)localObject3).cameraPowerMah);
        }
        if (((BatterySipper)localObject3).flashlightPowerMah != 0.0D)
        {
          paramPrintWriter.print(" flash=");
          printmAh(paramPrintWriter, ((BatterySipper)localObject3).flashlightPowerMah);
        }
        paramPrintWriter.print(" )");
      }
      paramPrintWriter.println();
      i += 1;
      break label4302;
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("  Device is currently plugged into power");
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("    Last discharge cycle start level: ");
      paramPrintWriter.println(getDischargeStartLevel());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("    Last discharge cycle end level: ");
      paramPrintWriter.println(getDischargeCurrentLevel());
      break;
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("  Device battery use since last full charge");
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("    Amount discharged (lower bound): ");
      paramPrintWriter.println(getLowDischargeAmountSinceCharge());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("    Amount discharged (upper bound): ");
      paramPrintWriter.println(getHighDischargeAmountSinceCharge());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("    Amount discharged while screen on: ");
      paramPrintWriter.println(getDischargeAmountScreenOnSinceCharge());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("    Amount discharged while screen off: ");
      paramPrintWriter.println(getDischargeAmountScreenOffSinceCharge());
      paramPrintWriter.println();
      break label4154;
      paramPrintWriter.print("    Idle: ");
      continue;
      paramPrintWriter.print("    Cell standby: ");
      continue;
      paramPrintWriter.print("    Phone calls: ");
      continue;
      paramPrintWriter.print("    Wifi: ");
      continue;
      paramPrintWriter.print("    Bluetooth: ");
      continue;
      paramPrintWriter.print("    Screen: ");
      continue;
      paramPrintWriter.print("    Flashlight: ");
      continue;
      paramPrintWriter.print("    Uid ");
      UserHandle.formatUid(paramPrintWriter, ((BatterySipper)localObject3).uidObj.getUid());
      paramPrintWriter.print(": ");
      continue;
      paramPrintWriter.print("    User ");
      paramPrintWriter.print(((BatterySipper)localObject3).userId);
      paramPrintWriter.print(": ");
      continue;
      paramPrintWriter.print("    Unaccounted: ");
      continue;
      paramPrintWriter.print("    Over-counted: ");
      continue;
      paramPrintWriter.print("    Camera: ");
    }
    label5045:
    paramPrintWriter.println();
    label5049:
    paramContext = paramContext.getMobilemsppList();
    if ((paramContext != null) && (paramContext.size() > 0))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("  Per-app mobile ms per packet:");
      l1 = 0L;
      i = 0;
      while (i < paramContext.size())
      {
        localObject2 = (BatterySipper)paramContext.get(i);
        localStringBuilder.setLength(0);
        localStringBuilder.append(paramString);
        localStringBuilder.append("    Uid ");
        UserHandle.formatUid(localStringBuilder, ((BatterySipper)localObject2).uidObj.getUid());
        localStringBuilder.append(": ");
        localStringBuilder.append(BatteryStatsHelper.makemAh(((BatterySipper)localObject2).mobilemspp));
        localStringBuilder.append(" (");
        localStringBuilder.append(((BatterySipper)localObject2).mobileRxPackets + ((BatterySipper)localObject2).mobileTxPackets);
        localStringBuilder.append(" packets over ");
        formatTimeMsNoSpace(localStringBuilder, ((BatterySipper)localObject2).mobileActive);
        localStringBuilder.append(") ");
        localStringBuilder.append(((BatterySipper)localObject2).mobileActiveCount);
        localStringBuilder.append("x");
        paramPrintWriter.println(localStringBuilder.toString());
        l1 += ((BatterySipper)localObject2).mobileActive;
        i += 1;
      }
      localStringBuilder.setLength(0);
      localStringBuilder.append(paramString);
      localStringBuilder.append("    TOTAL TIME: ");
      formatTimeMs(localStringBuilder, l1);
      localStringBuilder.append("(");
      localStringBuilder.append(formatRatioLocked(l1, l7));
      localStringBuilder.append(")");
      paramPrintWriter.println(localStringBuilder.toString());
      paramPrintWriter.println();
    }
    paramContext = new Comparator()
    {
      public int compare(BatteryStats.TimerEntry paramAnonymousTimerEntry1, BatteryStats.TimerEntry paramAnonymousTimerEntry2)
      {
        long l1 = paramAnonymousTimerEntry1.mTime;
        long l2 = paramAnonymousTimerEntry2.mTime;
        if (l1 < l2) {
          return 1;
        }
        if (l1 > l2) {
          return -1;
        }
        return 0;
      }
    };
    if (paramInt2 < 0)
    {
      localObject3 = getKernelWakelockStats();
      if (((Map)localObject3).size() > 0)
      {
        localObject2 = new ArrayList();
        localObject3 = ((Map)localObject3).entrySet().iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (Map.Entry)((Iterator)localObject3).next();
          localTimer = (Timer)((Map.Entry)localObject4).getValue();
          l1 = computeWakeLock(localTimer, l5, paramInt1);
          if (l1 > 0L) {
            ((ArrayList)localObject2).add(new TimerEntry((String)((Map.Entry)localObject4).getKey(), 0, localTimer, l1));
          }
        }
        if (((ArrayList)localObject2).size() > 0)
        {
          Collections.sort((List)localObject2, paramContext);
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("  All kernel wake locks:");
          i = 0;
          while (i < ((ArrayList)localObject2).size())
          {
            localObject3 = (TimerEntry)((ArrayList)localObject2).get(i);
            localStringBuilder.setLength(0);
            localStringBuilder.append(paramString);
            localStringBuilder.append("  Kernel Wake lock ");
            localStringBuilder.append(((TimerEntry)localObject3).mName);
            if (!printWakeLock(localStringBuilder, ((TimerEntry)localObject3).mTimer, l5, null, paramInt1, ": ").equals(": "))
            {
              localStringBuilder.append(" realtime");
              paramPrintWriter.println(localStringBuilder.toString());
            }
            i += 1;
          }
          paramPrintWriter.println();
        }
      }
      if (((ArrayList)localObject1).size() > 0)
      {
        Collections.sort((List)localObject1, paramContext);
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("  All partial wake locks:");
        i = 0;
        while (i < ((ArrayList)localObject1).size())
        {
          localObject2 = (TimerEntry)((ArrayList)localObject1).get(i);
          localStringBuilder.setLength(0);
          localStringBuilder.append("  Wake lock ");
          UserHandle.formatUid(localStringBuilder, ((TimerEntry)localObject2).mId);
          localStringBuilder.append(" ");
          localStringBuilder.append(((TimerEntry)localObject2).mName);
          printWakeLock(localStringBuilder, ((TimerEntry)localObject2).mTimer, l5, null, paramInt1, ": ");
          localStringBuilder.append(" realtime");
          paramPrintWriter.println(localStringBuilder.toString());
          i += 1;
        }
        ((ArrayList)localObject1).clear();
        paramPrintWriter.println();
      }
      localObject2 = getWakeupReasonStats();
      if (((Map)localObject2).size() > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("  All wakeup reasons:");
        localObject1 = new ArrayList();
        localObject2 = ((Map)localObject2).entrySet().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (Map.Entry)((Iterator)localObject2).next();
          localObject4 = (Timer)((Map.Entry)localObject3).getValue();
          ((ArrayList)localObject1).add(new TimerEntry((String)((Map.Entry)localObject3).getKey(), 0, (Timer)localObject4, ((Timer)localObject4).getCountLocked(paramInt1)));
        }
        Collections.sort((List)localObject1, paramContext);
        i = 0;
        while (i < ((ArrayList)localObject1).size())
        {
          paramContext = (TimerEntry)((ArrayList)localObject1).get(i);
          localStringBuilder.setLength(0);
          localStringBuilder.append(paramString);
          localStringBuilder.append("  Wakeup reason ");
          localStringBuilder.append(paramContext.mName);
          printWakeLock(localStringBuilder, paramContext.mTimer, l5, null, paramInt1, ": ");
          localStringBuilder.append(" realtime");
          paramPrintWriter.println(localStringBuilder.toString());
          i += 1;
        }
        paramPrintWriter.println();
      }
    }
    int i1 = 0;
    if (i1 < i4)
    {
      i = localSparseArray.keyAt(i1);
      if ((paramInt2 >= 0) && (i != paramInt2) && (i != 1000)) {}
      for (;;)
      {
        i1 += 1;
        break;
        paramContext = (Uid)localSparseArray.valueAt(i1);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  ");
        UserHandle.formatUid(paramPrintWriter, i);
        paramPrintWriter.println(":");
        l1 = paramContext.getNetworkActivityBytes(0, paramInt1);
        l2 = paramContext.getNetworkActivityBytes(1, paramInt1);
        l3 = paramContext.getNetworkActivityBytes(2, paramInt1);
        l4 = paramContext.getNetworkActivityBytes(3, paramInt1);
        l9 = paramContext.getNetworkActivityBytes(4, paramInt1);
        l10 = paramContext.getNetworkActivityBytes(5, paramInt1);
        long l19 = paramContext.getNetworkActivityPackets(0, paramInt1);
        long l20 = paramContext.getNetworkActivityPackets(1, paramInt1);
        l11 = paramContext.getNetworkActivityPackets(2, paramInt1);
        l12 = paramContext.getNetworkActivityPackets(3, paramInt1);
        l13 = paramContext.getMobileRadioActiveTime(paramInt1);
        i = paramContext.getMobileRadioActiveCount(paramInt1);
        l14 = paramContext.getFullWifiLockTime(l5, paramInt1);
        l15 = paramContext.getWifiScanTime(l5, paramInt1);
        m = paramContext.getWifiScanCount(paramInt1);
        l16 = paramContext.getWifiRunningTime(l5, paramInt1);
        l17 = paramContext.getMobileRadioApWakeupCount(paramInt1);
        long l18 = paramContext.getWifiRadioApWakeupCount(paramInt1);
        label6335:
        label6593:
        label6665:
        label6679:
        label6888:
        label7063:
        int i3;
        if ((l1 > 0L) || (l2 > 0L))
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("    Mobile network: ");
          paramPrintWriter.print(formatBytesLocked(l1));
          paramPrintWriter.print(" received, ");
          paramPrintWriter.print(formatBytesLocked(l2));
          paramPrintWriter.print(" sent (packets ");
          paramPrintWriter.print(l19);
          paramPrintWriter.print(" received, ");
          paramPrintWriter.print(l20);
          paramPrintWriter.println(" sent)");
          if ((l13 > 0L) || (i > 0))
          {
            localStringBuilder.setLength(0);
            localStringBuilder.append(paramString);
            localStringBuilder.append("    Mobile radio active: ");
            formatTimeMs(localStringBuilder, l13 / 1000L);
            localStringBuilder.append("(");
            localStringBuilder.append(formatRatioLocked(l13, l8));
            localStringBuilder.append(") ");
            localStringBuilder.append(i);
            localStringBuilder.append("x");
            l2 = l19 + l20;
            l1 = l2;
            if (l2 == 0L) {
              l1 = 1L;
            }
            localStringBuilder.append(" @ ");
            localStringBuilder.append(BatteryStatsHelper.makemAh(l13 / 1000L / l1));
            localStringBuilder.append(" mspp");
            paramPrintWriter.println(localStringBuilder.toString());
          }
          if (l17 > 0L)
          {
            localStringBuilder.setLength(0);
            localStringBuilder.append(paramString);
            localStringBuilder.append("    Mobile radio AP wakeups: ");
            localStringBuilder.append(l17);
            paramPrintWriter.println(localStringBuilder.toString());
          }
          printControllerActivityIfInteresting(paramPrintWriter, localStringBuilder, paramString + "  ", "Modem", paramContext.getModemControllerActivity(), paramInt1);
          if ((l3 <= 0L) && (l4 <= 0L)) {
            break label7174;
          }
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("    Wi-Fi network: ");
          paramPrintWriter.print(formatBytesLocked(l3));
          paramPrintWriter.print(" received, ");
          paramPrintWriter.print(formatBytesLocked(l4));
          paramPrintWriter.print(" sent (packets ");
          paramPrintWriter.print(l11);
          paramPrintWriter.print(" received, ");
          paramPrintWriter.print(l12);
          paramPrintWriter.println(" sent)");
          if ((l14 == 0L) && (l15 == 0L)) {
            break label7191;
          }
          localStringBuilder.setLength(0);
          localStringBuilder.append(paramString);
          localStringBuilder.append("    Wifi Running: ");
          formatTimeMs(localStringBuilder, l16 / 1000L);
          localStringBuilder.append("(");
          localStringBuilder.append(formatRatioLocked(l16, l7));
          localStringBuilder.append(")\n");
          localStringBuilder.append(paramString);
          localStringBuilder.append("    Full Wifi Lock: ");
          formatTimeMs(localStringBuilder, l14 / 1000L);
          localStringBuilder.append("(");
          localStringBuilder.append(formatRatioLocked(l14, l7));
          localStringBuilder.append(")\n");
          localStringBuilder.append(paramString);
          localStringBuilder.append("    Wifi Scan: ");
          formatTimeMs(localStringBuilder, l15 / 1000L);
          localStringBuilder.append("(");
          localStringBuilder.append(formatRatioLocked(l15, l7));
          localStringBuilder.append(") ");
          localStringBuilder.append(m);
          localStringBuilder.append("x");
          paramPrintWriter.println(localStringBuilder.toString());
          if (l18 > 0L)
          {
            localStringBuilder.setLength(0);
            localStringBuilder.append(paramString);
            localStringBuilder.append("    WiFi AP wakeups: ");
            localStringBuilder.append(l18);
            paramPrintWriter.println(localStringBuilder.toString());
          }
          printControllerActivityIfInteresting(paramPrintWriter, localStringBuilder, paramString + "  ", "WiFi", paramContext.getWifiControllerActivity(), paramInt1);
          if ((l9 > 0L) || (l10 > 0L))
          {
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("    Bluetooth network: ");
            paramPrintWriter.print(formatBytesLocked(l9));
            paramPrintWriter.print(" received, ");
            paramPrintWriter.print(formatBytesLocked(l10));
            paramPrintWriter.println(" sent");
          }
          paramBoolean = printTimer(paramPrintWriter, localStringBuilder, paramContext.getBluetoothScanTimer(), l5, paramInt1, paramString, "Bluetooth Scan");
          if (!paramContext.hasUserActivity()) {
            break label7232;
          }
          i = 0;
          m = 0;
          if (m >= 4) {
            break label7218;
          }
          i3 = paramContext.getUserActivityCount(m, paramInt1);
          i2 = i;
          if (i3 != 0)
          {
            if (i != 0) {
              break label7206;
            }
            localStringBuilder.setLength(0);
            localStringBuilder.append("    User activity: ");
            i = 1;
          }
        }
        for (;;)
        {
          localStringBuilder.append(i3);
          localStringBuilder.append(" ");
          localStringBuilder.append(Uid.USER_ACTIVITY_TYPES[m]);
          i2 = i;
          m += 1;
          i = i2;
          break label7063;
          if (l19 > 0L) {
            break;
          }
          if (l20 <= 0L) {
            break label6335;
          }
          break;
          label7174:
          if (l11 > 0L) {
            break label6593;
          }
          if (l12 <= 0L) {
            break label6665;
          }
          break label6593;
          label7191:
          if (m != 0) {
            break label6679;
          }
          if (l16 == 0L) {
            break label6888;
          }
          break label6679;
          label7206:
          localStringBuilder.append(", ");
        }
        label7218:
        if (i != 0) {
          paramPrintWriter.println(localStringBuilder.toString());
        }
        label7232:
        localObject1 = paramContext.getWakelockStats();
        l4 = 0L;
        l2 = 0L;
        l1 = 0L;
        l3 = 0L;
        m = 0;
        i = ((ArrayMap)localObject1).size() - 1;
        while (i >= 0)
        {
          localObject2 = (BatteryStats.Uid.Wakelock)((ArrayMap)localObject1).valueAt(i);
          localStringBuilder.setLength(0);
          localStringBuilder.append(paramString);
          localStringBuilder.append("    Wake lock ");
          localStringBuilder.append((String)((ArrayMap)localObject1).keyAt(i));
          localObject3 = printWakeLock(localStringBuilder, ((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(1), l5, "full", paramInt1, ": ");
          localObject3 = printWakeLock(localStringBuilder, ((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(0), l5, "partial", paramInt1, (String)localObject3);
          localObject3 = printWakeLock(localStringBuilder, ((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(2), l5, "window", paramInt1, (String)localObject3);
          printWakeLock(localStringBuilder, ((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(18), l5, "draw", paramInt1, (String)localObject3);
          localStringBuilder.append(" realtime");
          paramPrintWriter.println(localStringBuilder.toString());
          paramBoolean = true;
          m += 1;
          l4 += computeWakeLock(((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(1), l5, paramInt1);
          l2 += computeWakeLock(((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(0), l5, paramInt1);
          l1 += computeWakeLock(((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(2), l5, paramInt1);
          l3 += computeWakeLock(((BatteryStats.Uid.Wakelock)localObject2).getWakeTime(18), l5, paramInt1);
          i -= 1;
        }
        if (m > 1)
        {
          if ((l4 != 0L) || (l2 != 0L))
          {
            localStringBuilder.setLength(0);
            localStringBuilder.append(paramString);
            localStringBuilder.append("    TOTAL wake: ");
            m = 0;
            if (l4 != 0L)
            {
              m = 1;
              formatTimeMs(localStringBuilder, l4);
              localStringBuilder.append("full");
            }
            i = m;
            if (l2 != 0L)
            {
              if (m != 0) {
                localStringBuilder.append(", ");
              }
              i = 1;
              formatTimeMs(localStringBuilder, l2);
              localStringBuilder.append("partial");
            }
            m = i;
            if (l1 != 0L)
            {
              if (i != 0) {
                localStringBuilder.append(", ");
              }
              m = 1;
              formatTimeMs(localStringBuilder, l1);
              localStringBuilder.append("window");
            }
            if (l3 != 0L)
            {
              if (m != 0) {
                localStringBuilder.append(",");
              }
              formatTimeMs(localStringBuilder, l3);
              localStringBuilder.append("draw");
            }
            localStringBuilder.append(" realtime");
            paramPrintWriter.println(localStringBuilder.toString());
          }
        }
        else
        {
          label7729:
          localObject1 = paramContext.getSyncStats();
          i = ((ArrayMap)localObject1).size() - 1;
          label7744:
          if (i < 0) {
            break label7919;
          }
          localObject2 = (Timer)((ArrayMap)localObject1).valueAt(i);
          l1 = (((Timer)localObject2).getTotalTimeLocked(l5, paramInt1) + 500L) / 1000L;
          m = ((Timer)localObject2).getCountLocked(paramInt1);
          localStringBuilder.setLength(0);
          localStringBuilder.append(paramString);
          localStringBuilder.append("    Sync ");
          localStringBuilder.append((String)((ArrayMap)localObject1).keyAt(i));
          localStringBuilder.append(": ");
          if (l1 == 0L) {
            break label7907;
          }
          formatTimeMs(localStringBuilder, l1);
          localStringBuilder.append("realtime (");
          localStringBuilder.append(m);
          localStringBuilder.append(" times)");
        }
        for (;;)
        {
          paramPrintWriter.println(localStringBuilder.toString());
          paramBoolean = true;
          i -= 1;
          break label7744;
          if (l1 == 0L) {
            break label7729;
          }
          break;
          label7907:
          localStringBuilder.append("(not used)");
        }
        label7919:
        localObject1 = paramContext.getJobStats();
        i = ((ArrayMap)localObject1).size() - 1;
        if (i >= 0)
        {
          localObject2 = (Timer)((ArrayMap)localObject1).valueAt(i);
          l1 = (((Timer)localObject2).getTotalTimeLocked(l5, paramInt1) + 500L) / 1000L;
          m = ((Timer)localObject2).getCountLocked(paramInt1);
          localStringBuilder.setLength(0);
          localStringBuilder.append(paramString);
          localStringBuilder.append("    Job ");
          localStringBuilder.append((String)((ArrayMap)localObject1).keyAt(i));
          localStringBuilder.append(": ");
          if (l1 != 0L)
          {
            formatTimeMs(localStringBuilder, l1);
            localStringBuilder.append("realtime (");
            localStringBuilder.append(m);
            localStringBuilder.append(" times)");
          }
          for (;;)
          {
            paramPrintWriter.println(localStringBuilder.toString());
            paramBoolean = true;
            i -= 1;
            break;
            localStringBuilder.append("(not used)");
          }
        }
        boolean bool = paramBoolean | printTimer(paramPrintWriter, localStringBuilder, paramContext.getFlashlightTurnedOnTimer(), l5, paramInt1, paramString, "Flashlight") | printTimer(paramPrintWriter, localStringBuilder, paramContext.getCameraTurnedOnTimer(), l5, paramInt1, paramString, "Camera") | printTimer(paramPrintWriter, localStringBuilder, paramContext.getVideoTurnedOnTimer(), l5, paramInt1, paramString, "Video") | printTimer(paramPrintWriter, localStringBuilder, paramContext.getAudioTurnedOnTimer(), l5, paramInt1, paramString, "Audio");
        localObject1 = paramContext.getSensorStats();
        int i2 = ((SparseArray)localObject1).size();
        i = 0;
        if (i < i2)
        {
          localObject2 = (BatteryStats.Uid.Sensor)((SparseArray)localObject1).valueAt(i);
          ((SparseArray)localObject1).keyAt(i);
          localStringBuilder.setLength(0);
          localStringBuilder.append(paramString);
          localStringBuilder.append("    Sensor ");
          n = ((BatteryStats.Uid.Sensor)localObject2).getHandle();
          if (n == 55536)
          {
            localStringBuilder.append("GPS");
            label8268:
            localStringBuilder.append(": ");
            localObject2 = ((BatteryStats.Uid.Sensor)localObject2).getSensorTime();
            if (localObject2 == null) {
              break label8401;
            }
            l1 = (((Timer)localObject2).getTotalTimeLocked(l5, paramInt1) + 500L) / 1000L;
            n = ((Timer)localObject2).getCountLocked(paramInt1);
            if (l1 == 0L) {
              break label8389;
            }
            formatTimeMs(localStringBuilder, l1);
            localStringBuilder.append("realtime (");
            localStringBuilder.append(n);
            localStringBuilder.append(" times)");
          }
          for (;;)
          {
            paramPrintWriter.println(localStringBuilder.toString());
            n = 1;
            i += 1;
            break;
            localStringBuilder.append(n);
            break label8268;
            label8389:
            localStringBuilder.append("(not used)");
            continue;
            label8401:
            localStringBuilder.append("(not used)");
          }
        }
        int j = n | printTimer(paramPrintWriter, localStringBuilder, paramContext.getVibratorOnTimer(), l5, paramInt1, paramString, "Vibrator") | printTimer(paramPrintWriter, localStringBuilder, paramContext.getForegroundActivityTimer(), l5, paramInt1, paramString, "Foreground activities");
        l1 = 0L;
        int n = 0;
        while (n < 6)
        {
          l3 = paramContext.getProcessStateTime(n, l5, paramInt1);
          l2 = l1;
          if (l3 > 0L)
          {
            l2 = l1 + l3;
            localStringBuilder.setLength(0);
            localStringBuilder.append(paramString);
            localStringBuilder.append("    ");
            localStringBuilder.append(Uid.PROCESS_STATE_NAMES[n]);
            localStringBuilder.append(" for: ");
            formatTimeMs(localStringBuilder, (500L + l3) / 1000L);
            paramPrintWriter.println(localStringBuilder.toString());
            j = 1;
          }
          n += 1;
          l1 = l2;
        }
        if (l1 > 0L)
        {
          localStringBuilder.setLength(0);
          localStringBuilder.append(paramString);
          localStringBuilder.append("    Total running: ");
          formatTimeMs(localStringBuilder, (500L + l1) / 1000L);
          paramPrintWriter.println(localStringBuilder.toString());
        }
        l1 = paramContext.getUserCpuTimeUs(paramInt1);
        l2 = paramContext.getSystemCpuTimeUs(paramInt1);
        l3 = paramContext.getCpuPowerMaUs(paramInt1);
        label8765:
        int i5;
        int i6;
        if ((l1 > 0L) || (l2 > 0L))
        {
          localStringBuilder.setLength(0);
          localStringBuilder.append(paramString);
          localStringBuilder.append("    Total cpu time: u=");
          formatTimeMs(localStringBuilder, l1 / 1000L);
          localStringBuilder.append("s=");
          formatTimeMs(localStringBuilder, l2 / 1000L);
          localStringBuilder.append("p=");
          printmAh(localStringBuilder, l3 / 3.6E9D);
          localStringBuilder.append("mAh");
          paramPrintWriter.println(localStringBuilder.toString());
          localObject1 = paramContext.getProcessStats();
          n = ((ArrayMap)localObject1).size() - 1;
          if (n < 0) {
            break label9368;
          }
          localObject2 = (BatteryStats.Uid.Proc)((ArrayMap)localObject1).valueAt(n);
          l1 = ((BatteryStats.Uid.Proc)localObject2).getUserTime(paramInt1);
          l2 = ((BatteryStats.Uid.Proc)localObject2).getSystemTime(paramInt1);
          l3 = ((BatteryStats.Uid.Proc)localObject2).getForegroundTime(paramInt1);
          i3 = ((BatteryStats.Uid.Proc)localObject2).getStarts(paramInt1);
          i5 = ((BatteryStats.Uid.Proc)localObject2).getNumCrashes(paramInt1);
          i6 = ((BatteryStats.Uid.Proc)localObject2).getNumAnrs(paramInt1);
          if (paramInt1 != 0) {
            break label9284;
          }
          i2 = ((BatteryStats.Uid.Proc)localObject2).countExcessivePowers();
          label8863:
          if ((l1 == 0L) && (l2 == 0L)) {
            break label9290;
          }
        }
        label8997:
        label9129:
        label9190:
        int k;
        label9284:
        label9290:
        while ((l3 != 0L) || (i3 != 0) || (i2 != 0) || (i5 != 0) || (i6 != 0))
        {
          localStringBuilder.setLength(0);
          localStringBuilder.append(paramString);
          localStringBuilder.append("    Proc ");
          localStringBuilder.append((String)((ArrayMap)localObject1).keyAt(n));
          localStringBuilder.append(":\n");
          localStringBuilder.append(paramString);
          localStringBuilder.append("      CPU: ");
          formatTimeMs(localStringBuilder, l1);
          localStringBuilder.append("usr + ");
          formatTimeMs(localStringBuilder, l2);
          localStringBuilder.append("krn ; ");
          formatTimeMs(localStringBuilder, l3);
          localStringBuilder.append("fg");
          if ((i3 == 0) && (i5 == 0)) {
            break label9326;
          }
          localStringBuilder.append("\n");
          localStringBuilder.append(paramString);
          localStringBuilder.append("      ");
          j = 0;
          if (i3 != 0)
          {
            j = 1;
            localStringBuilder.append(i3);
            localStringBuilder.append(" starts");
          }
          i3 = j;
          if (i5 != 0)
          {
            if (j != 0) {
              localStringBuilder.append(", ");
            }
            i3 = 1;
            localStringBuilder.append(i5);
            localStringBuilder.append(" crashes");
          }
          if (i6 != 0)
          {
            if (i3 != 0) {
              localStringBuilder.append(", ");
            }
            localStringBuilder.append(i6);
            localStringBuilder.append(" anrs");
          }
          paramPrintWriter.println(localStringBuilder.toString());
          j = 0;
          for (;;)
          {
            if (j >= i2) {
              break label9362;
            }
            localObject3 = ((BatteryStats.Uid.Proc)localObject2).getExcessivePower(j);
            if (localObject3 != null)
            {
              paramPrintWriter.print(paramString);
              paramPrintWriter.print("      * Killed for ");
              if (((BatteryStats.Uid.Proc.ExcessivePower)localObject3).type != 1) {
                break;
              }
              paramPrintWriter.print("wake lock");
              paramPrintWriter.print(" use: ");
              TimeUtils.formatDuration(((BatteryStats.Uid.Proc.ExcessivePower)localObject3).usedTime, paramPrintWriter);
              paramPrintWriter.print(" over ");
              TimeUtils.formatDuration(((BatteryStats.Uid.Proc.ExcessivePower)localObject3).overTime, paramPrintWriter);
              if (((BatteryStats.Uid.Proc.ExcessivePower)localObject3).overTime != 0L)
              {
                paramPrintWriter.print(" (");
                paramPrintWriter.print(((BatteryStats.Uid.Proc.ExcessivePower)localObject3).usedTime * 100L / ((BatteryStats.Uid.Proc.ExcessivePower)localObject3).overTime);
                paramPrintWriter.println("%)");
              }
            }
            j += 1;
          }
          if (l3 <= 0L) {
            break label8765;
          }
          break;
          i2 = 0;
          break label8863;
        }
        for (;;)
        {
          n -= 1;
          break;
          label9326:
          if (i6 == 0) {
            break label9129;
          }
          break label8997;
          if (((BatteryStats.Uid.Proc.ExcessivePower)localObject3).type == 2)
          {
            paramPrintWriter.print("cpu");
            break label9190;
          }
          paramPrintWriter.print("unknown");
          break label9190;
          label9362:
          k = 1;
        }
        label9368:
        paramContext = paramContext.getPackageStats();
        n = paramContext.size() - 1;
        while (n >= 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("    Apk ");
          paramPrintWriter.print((String)paramContext.keyAt(n));
          paramPrintWriter.println(":");
          k = 0;
          localObject1 = (BatteryStats.Uid.Pkg)paramContext.valueAt(n);
          localObject2 = ((BatteryStats.Uid.Pkg)localObject1).getWakeupAlarmStats();
          i2 = ((ArrayMap)localObject2).size() - 1;
          while (i2 >= 0)
          {
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("      Wakeup alarm ");
            paramPrintWriter.print((String)((ArrayMap)localObject2).keyAt(i2));
            paramPrintWriter.print(": ");
            paramPrintWriter.print(((Counter)((ArrayMap)localObject2).valueAt(i2)).getCountLocked(paramInt1));
            paramPrintWriter.println(" times");
            k = 1;
            i2 -= 1;
          }
          localObject1 = ((BatteryStats.Uid.Pkg)localObject1).getServiceStats();
          i2 = ((ArrayMap)localObject1).size() - 1;
          if (i2 >= 0)
          {
            localObject2 = (BatteryStats.Uid.Pkg.Serv)((ArrayMap)localObject1).valueAt(i2);
            l1 = ((BatteryStats.Uid.Pkg.Serv)localObject2).getStartTime(l6, paramInt1);
            i3 = ((BatteryStats.Uid.Pkg.Serv)localObject2).getStarts(paramInt1);
            i5 = ((BatteryStats.Uid.Pkg.Serv)localObject2).getLaunches(paramInt1);
            if ((l1 != 0L) || (i3 != 0)) {}
            for (;;)
            {
              localStringBuilder.setLength(0);
              localStringBuilder.append(paramString);
              localStringBuilder.append("      Service ");
              localStringBuilder.append((String)((ArrayMap)localObject1).keyAt(i2));
              localStringBuilder.append(":\n");
              localStringBuilder.append(paramString);
              localStringBuilder.append("        Created for: ");
              formatTimeMs(localStringBuilder, l1 / 1000L);
              localStringBuilder.append("uptime\n");
              localStringBuilder.append(paramString);
              localStringBuilder.append("        Starts: ");
              localStringBuilder.append(i3);
              localStringBuilder.append(", launches: ");
              localStringBuilder.append(i5);
              paramPrintWriter.println(localStringBuilder.toString());
              k = 1;
              do
              {
                i2 -= 1;
                break;
              } while (i5 == 0);
            }
          }
          if (k == 0)
          {
            paramPrintWriter.print(paramString);
            paramPrintWriter.println("      (nothing executed)");
          }
          k = 1;
          n -= 1;
        }
        if (k == 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("    (nothing executed)");
        }
      }
    }
  }
  
  public abstract void finishIteratingHistoryLocked();
  
  public abstract void finishIteratingOldHistoryLocked();
  
  final String formatBytesLocked(long paramLong)
  {
    this.mFormatBuilder.setLength(0);
    if (paramLong < 1024L) {
      return paramLong + "B";
    }
    if (paramLong < 1048576L)
    {
      this.mFormatter.format("%.2fKB", new Object[] { Double.valueOf(paramLong / 1024.0D) });
      return this.mFormatBuilder.toString();
    }
    if (paramLong < 1073741824L)
    {
      this.mFormatter.format("%.2fMB", new Object[] { Double.valueOf(paramLong / 1048576.0D) });
      return this.mFormatBuilder.toString();
    }
    this.mFormatter.format("%.2fGB", new Object[] { Double.valueOf(paramLong / 1.073741824E9D) });
    return this.mFormatBuilder.toString();
  }
  
  public final String formatRatioLocked(long paramLong1, long paramLong2)
  {
    if (paramLong2 == 0L) {
      return "--%";
    }
    float f = (float)paramLong1 / (float)paramLong2;
    this.mFormatBuilder.setLength(0);
    this.mFormatter.format("%.1f%%", new Object[] { Float.valueOf(f * 100.0F) });
    return this.mFormatBuilder.toString();
  }
  
  public abstract long getBatteryRealtime(long paramLong);
  
  public abstract long getBatteryUptime(long paramLong);
  
  public abstract ControllerActivityCounter getBluetoothControllerActivity();
  
  public abstract long getBluetoothScanTime(long paramLong, int paramInt);
  
  public abstract long getCameraOnTime(long paramLong, int paramInt);
  
  public abstract LevelStepTracker getChargeLevelStepTracker();
  
  public abstract long getCurrentDailyStartTime();
  
  public abstract LevelStepTracker getDailyChargeLevelStepTracker();
  
  public abstract LevelStepTracker getDailyDischargeLevelStepTracker();
  
  public abstract DailyItem getDailyItemLocked(int paramInt);
  
  public abstract ArrayList<PackageChange> getDailyPackageChanges();
  
  public abstract int getDeviceIdleModeCount(int paramInt1, int paramInt2);
  
  public abstract long getDeviceIdleModeTime(int paramInt1, long paramLong, int paramInt2);
  
  public abstract int getDeviceIdlingCount(int paramInt1, int paramInt2);
  
  public abstract long getDeviceIdlingTime(int paramInt1, long paramLong, int paramInt2);
  
  public abstract int getDischargeAmount(int paramInt);
  
  public abstract int getDischargeAmountScreenOff();
  
  public abstract int getDischargeAmountScreenOffSinceCharge();
  
  public abstract int getDischargeAmountScreenOn();
  
  public abstract int getDischargeAmountScreenOnSinceCharge();
  
  public abstract LongCounter getDischargeCoulombCounter();
  
  public abstract int getDischargeCurrentLevel();
  
  public abstract LevelStepTracker getDischargeLevelStepTracker();
  
  public abstract LongCounter getDischargeScreenOffCoulombCounter();
  
  public abstract int getDischargeStartLevel();
  
  public abstract String getEndPlatformVersion();
  
  public abstract int getEstimatedBatteryCapacity();
  
  public abstract long getFlashlightOnCount(int paramInt);
  
  public abstract long getFlashlightOnTime(long paramLong, int paramInt);
  
  public abstract long getGlobalWifiRunningTime(long paramLong, int paramInt);
  
  public abstract int getHighDischargeAmountSinceCharge();
  
  public abstract long getHistoryBaseTime();
  
  public abstract int getHistoryStringPoolBytes();
  
  public abstract int getHistoryStringPoolSize();
  
  public abstract String getHistoryTagPoolString(int paramInt);
  
  public abstract int getHistoryTagPoolUid(int paramInt);
  
  public abstract int getHistoryTotalSize();
  
  public abstract int getHistoryUsedSize();
  
  public abstract long getInteractiveTime(long paramLong, int paramInt);
  
  public abstract boolean getIsOnBattery();
  
  public abstract Map<String, ? extends Timer> getKernelWakelockStats();
  
  public abstract long getLongestDeviceIdleModeTime(int paramInt);
  
  public abstract int getLowDischargeAmountSinceCharge();
  
  public abstract long getMobileRadioActiveAdjustedTime(int paramInt);
  
  public abstract int getMobileRadioActiveCount(int paramInt);
  
  public abstract long getMobileRadioActiveTime(long paramLong, int paramInt);
  
  public abstract int getMobileRadioActiveUnknownCount(int paramInt);
  
  public abstract long getMobileRadioActiveUnknownTime(int paramInt);
  
  public abstract ControllerActivityCounter getModemControllerActivity();
  
  public abstract long getNetworkActivityBytes(int paramInt1, int paramInt2);
  
  public abstract long getNetworkActivityPackets(int paramInt1, int paramInt2);
  
  public abstract boolean getNextHistoryLocked(HistoryItem paramHistoryItem);
  
  public abstract long getNextMaxDailyDeadline();
  
  public abstract long getNextMinDailyDeadline();
  
  public abstract boolean getNextOldHistoryLocked(HistoryItem paramHistoryItem);
  
  public abstract int getNumConnectivityChange(int paramInt);
  
  public abstract int getParcelVersion();
  
  public abstract int getPhoneDataConnectionCount(int paramInt1, int paramInt2);
  
  public abstract long getPhoneDataConnectionTime(int paramInt1, long paramLong, int paramInt2);
  
  public abstract int getPhoneOnCount(int paramInt);
  
  public abstract long getPhoneOnTime(long paramLong, int paramInt);
  
  public abstract long getPhoneSignalScanningTime(long paramLong, int paramInt);
  
  public abstract int getPhoneSignalStrengthCount(int paramInt1, int paramInt2);
  
  public abstract long getPhoneSignalStrengthTime(int paramInt1, long paramLong, int paramInt2);
  
  public abstract int getPowerSaveModeEnabledCount(int paramInt);
  
  public abstract long getPowerSaveModeEnabledTime(long paramLong, int paramInt);
  
  public abstract long getScreenBrightnessTime(int paramInt1, long paramLong, int paramInt2);
  
  public abstract int getScreenOnCount(int paramInt);
  
  public abstract long getScreenOnTime(long paramLong, int paramInt);
  
  public abstract long getStartClockTime();
  
  public abstract int getStartCount();
  
  public abstract String getStartPlatformVersion();
  
  public abstract SparseArray<? extends Uid> getUidStats();
  
  public abstract Map<String, ? extends Timer> getWakeupReasonStats();
  
  public abstract ControllerActivityCounter getWifiControllerActivity();
  
  public abstract long getWifiOnTime(long paramLong, int paramInt);
  
  public abstract int getWifiSignalStrengthCount(int paramInt1, int paramInt2);
  
  public abstract long getWifiSignalStrengthTime(int paramInt1, long paramLong, int paramInt2);
  
  public abstract int getWifiStateCount(int paramInt1, int paramInt2);
  
  public abstract long getWifiStateTime(int paramInt1, long paramLong, int paramInt2);
  
  public abstract int getWifiSupplStateCount(int paramInt1, int paramInt2);
  
  public abstract long getWifiSupplStateTime(int paramInt1, long paramLong, int paramInt2);
  
  public abstract boolean hasBluetoothActivityReporting();
  
  public abstract boolean hasModemActivityReporting();
  
  public abstract boolean hasWifiActivityReporting();
  
  public void prepareForDumpLocked() {}
  
  public abstract boolean startIteratingHistoryLocked();
  
  public abstract boolean startIteratingOldHistoryLocked();
  
  public abstract void writeToParcelWithoutUids(Parcel paramParcel, int paramInt);
  
  public static final class BitDescription
  {
    public final int mask;
    public final String name;
    public final int shift;
    public final String shortName;
    public final String[] shortValues;
    public final String[] values;
    
    public BitDescription(int paramInt1, int paramInt2, String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2)
    {
      this.mask = paramInt1;
      this.shift = paramInt2;
      this.name = paramString1;
      this.shortName = paramString2;
      this.values = paramArrayOfString1;
      this.shortValues = paramArrayOfString2;
    }
    
    public BitDescription(int paramInt, String paramString1, String paramString2)
    {
      this.mask = paramInt;
      this.shift = -1;
      this.name = paramString1;
      this.shortName = paramString2;
      this.values = null;
      this.shortValues = null;
    }
  }
  
  public static abstract class ControllerActivityCounter
  {
    public abstract BatteryStats.LongCounter getIdleTimeCounter();
    
    public abstract BatteryStats.LongCounter getPowerCounter();
    
    public abstract BatteryStats.LongCounter getRxTimeCounter();
    
    public abstract BatteryStats.LongCounter[] getTxTimeCounters();
  }
  
  public static abstract class Counter
  {
    public abstract int getCountLocked(int paramInt);
    
    public abstract void logState(Printer paramPrinter, String paramString);
  }
  
  public static final class DailyItem
  {
    public BatteryStats.LevelStepTracker mChargeSteps;
    public BatteryStats.LevelStepTracker mDischargeSteps;
    public long mEndTime;
    public ArrayList<BatteryStats.PackageChange> mPackageChanges;
    public long mStartTime;
  }
  
  public static final class HistoryEventTracker
  {
    private final HashMap<String, SparseIntArray>[] mActiveEvents = new HashMap[21];
    
    public HashMap<String, SparseIntArray> getStateForEvent(int paramInt)
    {
      return this.mActiveEvents[paramInt];
    }
    
    public void removeEvents(int paramInt)
    {
      this.mActiveEvents[(paramInt & 0xFFFF3FFF)] = null;
    }
    
    public boolean updateState(int paramInt1, String paramString, int paramInt2, int paramInt3)
    {
      Object localObject2;
      Object localObject1;
      if ((0x8000 & paramInt1) != 0)
      {
        paramInt1 &= 0xFFFF3FFF;
        localObject2 = this.mActiveEvents[paramInt1];
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = new HashMap();
          this.mActiveEvents[paramInt1] = localObject1;
        }
        SparseIntArray localSparseIntArray = (SparseIntArray)((HashMap)localObject1).get(paramString);
        localObject2 = localSparseIntArray;
        if (localSparseIntArray == null)
        {
          localObject2 = new SparseIntArray();
          ((HashMap)localObject1).put(paramString, localObject2);
        }
        if (((SparseIntArray)localObject2).indexOfKey(paramInt2) >= 0) {
          return false;
        }
        ((SparseIntArray)localObject2).put(paramInt2, paramInt3);
      }
      for (;;)
      {
        return true;
        if ((paramInt1 & 0x4000) != 0)
        {
          localObject1 = this.mActiveEvents[(paramInt1 & 0xFFFF3FFF)];
          if (localObject1 == null) {
            return false;
          }
          localObject2 = (SparseIntArray)((HashMap)localObject1).get(paramString);
          if (localObject2 == null) {
            return false;
          }
          paramInt1 = ((SparseIntArray)localObject2).indexOfKey(paramInt2);
          if (paramInt1 < 0) {
            return false;
          }
          ((SparseIntArray)localObject2).removeAt(paramInt1);
          if (((SparseIntArray)localObject2).size() <= 0) {
            ((HashMap)localObject1).remove(paramString);
          }
        }
      }
    }
  }
  
  public static final class HistoryItem
    implements Parcelable
  {
    public static final byte CMD_CURRENT_TIME = 5;
    public static final byte CMD_NULL = -1;
    public static final byte CMD_OVERFLOW = 6;
    public static final byte CMD_RESET = 7;
    public static final byte CMD_SHUTDOWN = 8;
    public static final byte CMD_START = 4;
    public static final byte CMD_UPDATE = 0;
    public static final int EVENT_ACTIVE = 10;
    public static final int EVENT_ALARM = 13;
    public static final int EVENT_ALARM_FINISH = 16397;
    public static final int EVENT_ALARM_START = 32781;
    public static final int EVENT_COLLECT_EXTERNAL_STATS = 14;
    public static final int EVENT_CONNECTIVITY_CHANGED = 9;
    public static final int EVENT_COUNT = 21;
    public static final int EVENT_FLAG_FINISH = 16384;
    public static final int EVENT_FLAG_START = 32768;
    public static final int EVENT_FOREGROUND = 2;
    public static final int EVENT_FOREGROUND_FINISH = 16386;
    public static final int EVENT_FOREGROUND_START = 32770;
    public static final int EVENT_JOB = 6;
    public static final int EVENT_JOB_FINISH = 16390;
    public static final int EVENT_JOB_START = 32774;
    public static final int EVENT_LONG_WAKE_LOCK = 20;
    public static final int EVENT_LONG_WAKE_LOCK_FINISH = 16404;
    public static final int EVENT_LONG_WAKE_LOCK_START = 32788;
    public static final int EVENT_NONE = 0;
    public static final int EVENT_PACKAGE_ACTIVE = 16;
    public static final int EVENT_PACKAGE_INACTIVE = 15;
    public static final int EVENT_PACKAGE_INSTALLED = 11;
    public static final int EVENT_PACKAGE_UNINSTALLED = 12;
    public static final int EVENT_PROC = 1;
    public static final int EVENT_PROC_FINISH = 16385;
    public static final int EVENT_PROC_START = 32769;
    public static final int EVENT_SCREEN_WAKE_UP = 18;
    public static final int EVENT_SYNC = 4;
    public static final int EVENT_SYNC_FINISH = 16388;
    public static final int EVENT_SYNC_START = 32772;
    public static final int EVENT_TEMP_WHITELIST = 17;
    public static final int EVENT_TEMP_WHITELIST_FINISH = 16401;
    public static final int EVENT_TEMP_WHITELIST_START = 32785;
    public static final int EVENT_TOP = 3;
    public static final int EVENT_TOP_FINISH = 16387;
    public static final int EVENT_TOP_START = 32771;
    public static final int EVENT_TYPE_MASK = -49153;
    public static final int EVENT_USER_FOREGROUND = 8;
    public static final int EVENT_USER_FOREGROUND_FINISH = 16392;
    public static final int EVENT_USER_FOREGROUND_START = 32776;
    public static final int EVENT_USER_RUNNING = 7;
    public static final int EVENT_USER_RUNNING_FINISH = 16391;
    public static final int EVENT_USER_RUNNING_START = 32775;
    public static final int EVENT_WAKEUP_AP = 19;
    public static final int EVENT_WAKE_LOCK = 5;
    public static final int EVENT_WAKE_LOCK_FINISH = 16389;
    public static final int EVENT_WAKE_LOCK_START = 32773;
    public static final int MOST_INTERESTING_STATES = 1572864;
    public static final int MOST_INTERESTING_STATES2 = -1749024768;
    public static final int SETTLE_TO_ZERO_STATES = -1638400;
    public static final int SETTLE_TO_ZERO_STATES2 = 1748959232;
    public static final int STATE2_BLUETOOTH_ON_FLAG = 4194304;
    public static final int STATE2_BLUETOOTH_SCAN_FLAG = 1048576;
    public static final int STATE2_CAMERA_FLAG = 2097152;
    public static final int STATE2_CHARGING_FLAG = 16777216;
    public static final int STATE2_DEVICE_IDLE_MASK = 100663296;
    public static final int STATE2_DEVICE_IDLE_SHIFT = 25;
    public static final int STATE2_FLASHLIGHT_FLAG = 134217728;
    public static final int STATE2_PHONE_IN_CALL_FLAG = 8388608;
    public static final int STATE2_POWER_SAVE_FLAG = Integer.MIN_VALUE;
    public static final int STATE2_VIDEO_ON_FLAG = 1073741824;
    public static final int STATE2_WIFI_ON_FLAG = 268435456;
    public static final int STATE2_WIFI_RUNNING_FLAG = 536870912;
    public static final int STATE2_WIFI_SIGNAL_STRENGTH_MASK = 112;
    public static final int STATE2_WIFI_SIGNAL_STRENGTH_SHIFT = 4;
    public static final int STATE2_WIFI_SUPPL_STATE_MASK = 15;
    public static final int STATE2_WIFI_SUPPL_STATE_SHIFT = 0;
    public static final int STATE_AUDIO_ON_FLAG = 4194304;
    public static final int STATE_BATTERY_PLUGGED_FLAG = 524288;
    public static final int STATE_BRIGHTNESS_MASK = 7;
    public static final int STATE_BRIGHTNESS_SHIFT = 0;
    public static final int STATE_CPU_RUNNING_FLAG = Integer.MIN_VALUE;
    public static final int STATE_DATA_CONNECTION_MASK = 15872;
    public static final int STATE_DATA_CONNECTION_SHIFT = 9;
    public static final int STATE_GPS_ON_FLAG = 536870912;
    public static final int STATE_MOBILE_RADIO_ACTIVE_FLAG = 33554432;
    public static final int STATE_PHONE_SCANNING_FLAG = 2097152;
    public static final int STATE_PHONE_SIGNAL_STRENGTH_MASK = 56;
    public static final int STATE_PHONE_SIGNAL_STRENGTH_SHIFT = 3;
    public static final int STATE_PHONE_STATE_MASK = 448;
    public static final int STATE_PHONE_STATE_SHIFT = 6;
    private static final int STATE_RESERVED_0 = 16777216;
    public static final int STATE_SCREEN_ON_FLAG = 1048576;
    public static final int STATE_SENSOR_ON_FLAG = 8388608;
    public static final int STATE_WAKE_LOCK_FLAG = 1073741824;
    public static final int STATE_WIFI_FULL_LOCK_FLAG = 268435456;
    public static final int STATE_WIFI_MULTICAST_ON_FLAG = 65536;
    public static final int STATE_WIFI_RADIO_ACTIVE_FLAG = 67108864;
    public static final int STATE_WIFI_SCAN_FLAG = 134217728;
    public int batteryChargeUAh;
    public byte batteryHealth;
    public byte batteryLevel;
    public byte batteryPlugType;
    public byte batteryStatus;
    public short batteryTemperature;
    public char batteryVoltage;
    public byte cmd = -1;
    public long currentTime;
    public int eventCode;
    public BatteryStats.HistoryTag eventTag;
    public final BatteryStats.HistoryTag localEventTag = new BatteryStats.HistoryTag();
    public final BatteryStats.HistoryTag localWakeReasonTag = new BatteryStats.HistoryTag();
    public final BatteryStats.HistoryTag localWakelockTag = new BatteryStats.HistoryTag();
    public HistoryItem next;
    public int numReadInts;
    public int states;
    public int states2;
    public BatteryStats.HistoryStepDetails stepDetails;
    public long time;
    public BatteryStats.HistoryTag wakeReasonTag;
    public BatteryStats.HistoryTag wakelockTag;
    
    public HistoryItem() {}
    
    public HistoryItem(long paramLong, Parcel paramParcel)
    {
      this.time = paramLong;
      this.numReadInts = 2;
      readFromParcel(paramParcel);
    }
    
    private void setToCommon(HistoryItem paramHistoryItem)
    {
      this.batteryLevel = paramHistoryItem.batteryLevel;
      this.batteryStatus = paramHistoryItem.batteryStatus;
      this.batteryHealth = paramHistoryItem.batteryHealth;
      this.batteryPlugType = paramHistoryItem.batteryPlugType;
      this.batteryTemperature = paramHistoryItem.batteryTemperature;
      this.batteryVoltage = paramHistoryItem.batteryVoltage;
      this.batteryChargeUAh = paramHistoryItem.batteryChargeUAh;
      this.states = paramHistoryItem.states;
      this.states2 = paramHistoryItem.states2;
      if (paramHistoryItem.wakelockTag != null)
      {
        this.wakelockTag = this.localWakelockTag;
        this.wakelockTag.setTo(paramHistoryItem.wakelockTag);
        if (paramHistoryItem.wakeReasonTag == null) {
          break label175;
        }
        this.wakeReasonTag = this.localWakeReasonTag;
        this.wakeReasonTag.setTo(paramHistoryItem.wakeReasonTag);
        label124:
        this.eventCode = paramHistoryItem.eventCode;
        if (paramHistoryItem.eventTag == null) {
          break label183;
        }
        this.eventTag = this.localEventTag;
        this.eventTag.setTo(paramHistoryItem.eventTag);
      }
      for (;;)
      {
        this.currentTime = paramHistoryItem.currentTime;
        return;
        this.wakelockTag = null;
        break;
        label175:
        this.wakeReasonTag = null;
        break label124;
        label183:
        this.eventTag = null;
      }
    }
    
    public void clear()
    {
      this.time = 0L;
      this.cmd = -1;
      this.batteryLevel = 0;
      this.batteryStatus = 0;
      this.batteryHealth = 0;
      this.batteryPlugType = 0;
      this.batteryTemperature = 0;
      this.batteryVoltage = '\000';
      this.batteryChargeUAh = 0;
      this.states = 0;
      this.states2 = 0;
      this.wakelockTag = null;
      this.wakeReasonTag = null;
      this.eventCode = 0;
      this.eventTag = null;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean isDeltaData()
    {
      boolean bool = false;
      if (this.cmd == 0) {
        bool = true;
      }
      return bool;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      int i = paramParcel.dataPosition();
      int j = paramParcel.readInt();
      this.cmd = ((byte)(j & 0xFF));
      this.batteryLevel = ((byte)(j >> 8 & 0xFF));
      this.batteryStatus = ((byte)(j >> 16 & 0xF));
      this.batteryHealth = ((byte)(j >> 20 & 0xF));
      this.batteryPlugType = ((byte)(j >> 24 & 0xF));
      int k = paramParcel.readInt();
      this.batteryTemperature = ((short)(k & 0xFFFF));
      this.batteryVoltage = ((char)(k >> 16 & 0xFFFF));
      this.batteryChargeUAh = paramParcel.readInt();
      this.states = paramParcel.readInt();
      this.states2 = paramParcel.readInt();
      if ((0x10000000 & j) != 0)
      {
        this.wakelockTag = this.localWakelockTag;
        this.wakelockTag.readFromParcel(paramParcel);
        if ((0x20000000 & j) == 0) {
          break label252;
        }
        this.wakeReasonTag = this.localWakeReasonTag;
        this.wakeReasonTag.readFromParcel(paramParcel);
        label170:
        if ((0x40000000 & j) == 0) {
          break label260;
        }
        this.eventCode = paramParcel.readInt();
        this.eventTag = this.localEventTag;
        this.eventTag.readFromParcel(paramParcel);
        label201:
        if ((this.cmd != 5) && (this.cmd != 7)) {
          break label273;
        }
      }
      label252:
      label260:
      label273:
      for (this.currentTime = paramParcel.readLong();; this.currentTime = 0L)
      {
        this.numReadInts += (paramParcel.dataPosition() - i) / 4;
        return;
        this.wakelockTag = null;
        break;
        this.wakeReasonTag = null;
        break label170;
        this.eventCode = 0;
        this.eventTag = null;
        break label201;
      }
    }
    
    public boolean same(HistoryItem paramHistoryItem)
    {
      if ((!sameNonEvent(paramHistoryItem)) || (this.eventCode != paramHistoryItem.eventCode)) {
        return false;
      }
      if (this.wakelockTag != paramHistoryItem.wakelockTag)
      {
        if ((this.wakelockTag == null) || (paramHistoryItem.wakelockTag == null)) {
          return false;
        }
        if (!this.wakelockTag.equals(paramHistoryItem.wakelockTag)) {
          return false;
        }
      }
      if (this.wakeReasonTag != paramHistoryItem.wakeReasonTag)
      {
        if ((this.wakeReasonTag == null) || (paramHistoryItem.wakeReasonTag == null)) {
          return false;
        }
        if (!this.wakeReasonTag.equals(paramHistoryItem.wakeReasonTag)) {
          return false;
        }
      }
      if (this.eventTag != paramHistoryItem.eventTag)
      {
        if ((this.eventTag == null) || (paramHistoryItem.eventTag == null)) {
          return false;
        }
        if (!this.eventTag.equals(paramHistoryItem.eventTag)) {
          return false;
        }
      }
      return true;
    }
    
    public boolean sameNonEvent(HistoryItem paramHistoryItem)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.batteryLevel == paramHistoryItem.batteryLevel)
      {
        bool1 = bool2;
        if (this.batteryStatus == paramHistoryItem.batteryStatus)
        {
          bool1 = bool2;
          if (this.batteryHealth == paramHistoryItem.batteryHealth)
          {
            bool1 = bool2;
            if (this.batteryPlugType == paramHistoryItem.batteryPlugType)
            {
              bool1 = bool2;
              if (this.batteryTemperature == paramHistoryItem.batteryTemperature)
              {
                bool1 = bool2;
                if (this.batteryVoltage == paramHistoryItem.batteryVoltage)
                {
                  bool1 = bool2;
                  if (this.batteryChargeUAh == paramHistoryItem.batteryChargeUAh)
                  {
                    bool1 = bool2;
                    if (this.states == paramHistoryItem.states)
                    {
                      bool1 = bool2;
                      if (this.states2 == paramHistoryItem.states2)
                      {
                        bool1 = bool2;
                        if (this.currentTime == paramHistoryItem.currentTime) {
                          bool1 = true;
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
      return bool1;
    }
    
    public void setTo(long paramLong, byte paramByte, HistoryItem paramHistoryItem)
    {
      this.time = paramLong;
      this.cmd = paramByte;
      setToCommon(paramHistoryItem);
    }
    
    public void setTo(HistoryItem paramHistoryItem)
    {
      this.time = paramHistoryItem.time;
      this.cmd = paramHistoryItem.cmd;
      setToCommon(paramHistoryItem);
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int k = 0;
      paramParcel.writeLong(this.time);
      int m = this.cmd;
      int n = this.batteryLevel;
      int i1 = this.batteryStatus;
      int i2 = this.batteryHealth;
      int i3 = this.batteryPlugType;
      int i;
      if (this.wakelockTag != null)
      {
        i = 268435456;
        if (this.wakeReasonTag == null) {
          break label266;
        }
      }
      label266:
      for (int j = 536870912;; j = 0)
      {
        if (this.eventCode != 0) {
          k = 1073741824;
        }
        paramParcel.writeInt(j | i3 << 24 & 0xF000000 | m & 0xFF | n << 8 & 0xFF00 | i1 << 16 & 0xF0000 | i2 << 20 & 0xF00000 | i | k);
        paramParcel.writeInt(this.batteryTemperature & 0xFFFF | this.batteryVoltage << '\020' & 0xFFFF0000);
        paramParcel.writeInt(this.batteryChargeUAh);
        paramParcel.writeInt(this.states);
        paramParcel.writeInt(this.states2);
        if (this.wakelockTag != null) {
          this.wakelockTag.writeToParcel(paramParcel, paramInt);
        }
        if (this.wakeReasonTag != null) {
          this.wakeReasonTag.writeToParcel(paramParcel, paramInt);
        }
        if (this.eventCode != 0)
        {
          paramParcel.writeInt(this.eventCode);
          this.eventTag.writeToParcel(paramParcel, paramInt);
        }
        if ((this.cmd == 5) || (this.cmd == 7)) {
          paramParcel.writeLong(this.currentTime);
        }
        return;
        i = 0;
        break;
      }
    }
  }
  
  public static class HistoryPrinter
  {
    long lastTime = -1L;
    int oldChargeMAh = -1;
    int oldHealth = -1;
    int oldLevel = -1;
    int oldPlug = -1;
    int oldState = 0;
    int oldState2 = 0;
    int oldStatus = -1;
    int oldTemp = -1;
    int oldVolt = -1;
    
    private void printStepCpuUidCheckinDetails(PrintWriter paramPrintWriter, int paramInt1, int paramInt2, int paramInt3)
    {
      paramPrintWriter.print('/');
      paramPrintWriter.print(paramInt1);
      paramPrintWriter.print(":");
      paramPrintWriter.print(paramInt2);
      paramPrintWriter.print(":");
      paramPrintWriter.print(paramInt3);
    }
    
    private void printStepCpuUidDetails(PrintWriter paramPrintWriter, int paramInt1, int paramInt2, int paramInt3)
    {
      UserHandle.formatUid(paramPrintWriter, paramInt1);
      paramPrintWriter.print("=");
      paramPrintWriter.print(paramInt2);
      paramPrintWriter.print("u+");
      paramPrintWriter.print(paramInt3);
      paramPrintWriter.print("s");
    }
    
    public void printNextItem(PrintWriter paramPrintWriter, BatteryStats.HistoryItem paramHistoryItem, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
    {
      if (!paramBoolean1)
      {
        paramPrintWriter.print("  ");
        TimeUtils.formatDuration(paramHistoryItem.time - paramLong, paramPrintWriter, 19);
        paramPrintWriter.print(" (");
        paramPrintWriter.print(paramHistoryItem.numReadInts);
        paramPrintWriter.print(") ");
        if (paramHistoryItem.cmd == 4)
        {
          if (paramBoolean1) {
            paramPrintWriter.print(":");
          }
          paramPrintWriter.println("START");
          reset();
        }
      }
      else
      {
        paramPrintWriter.print(9);
        paramPrintWriter.print(',');
        paramPrintWriter.print("h");
        paramPrintWriter.print(',');
        if (this.lastTime < 0L) {
          paramPrintWriter.print(paramHistoryItem.time - paramLong);
        }
        for (;;)
        {
          this.lastTime = paramHistoryItem.time;
          break;
          paramPrintWriter.print(paramHistoryItem.time - this.lastTime);
        }
      }
      if ((paramHistoryItem.cmd == 5) || (paramHistoryItem.cmd == 7))
      {
        if (paramBoolean1) {
          paramPrintWriter.print(":");
        }
        if (paramHistoryItem.cmd == 7)
        {
          paramPrintWriter.print("RESET:");
          reset();
        }
        paramPrintWriter.print("TIME:");
        if (paramBoolean1)
        {
          paramPrintWriter.println(paramHistoryItem.currentTime);
          return;
        }
        paramPrintWriter.print(" ");
        paramPrintWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", paramHistoryItem.currentTime).toString());
        return;
      }
      if (paramHistoryItem.cmd == 8)
      {
        if (paramBoolean1) {
          paramPrintWriter.print(":");
        }
        paramPrintWriter.println("SHUTDOWN");
        return;
      }
      if (paramHistoryItem.cmd == 6)
      {
        if (paramBoolean1) {
          paramPrintWriter.print(":");
        }
        paramPrintWriter.println("*OVERFLOW*");
        return;
      }
      label350:
      label361:
      Object localObject;
      label389:
      label468:
      label556:
      label636:
      label678:
      int i;
      if (!paramBoolean1) {
        if (paramHistoryItem.batteryLevel < 10)
        {
          paramPrintWriter.print("00");
          paramPrintWriter.print(paramHistoryItem.batteryLevel);
          if (paramBoolean2)
          {
            paramPrintWriter.print(" ");
            if (paramHistoryItem.states >= 0)
            {
              if (paramHistoryItem.states >= 16) {
                break label1457;
              }
              paramPrintWriter.print("0000000");
            }
            paramPrintWriter.print(Integer.toHexString(paramHistoryItem.states));
          }
          if (this.oldStatus != paramHistoryItem.batteryStatus)
          {
            this.oldStatus = paramHistoryItem.batteryStatus;
            if (!paramBoolean1) {
              break label1613;
            }
            localObject = ",Bs=";
            paramPrintWriter.print((String)localObject);
          }
          switch (this.oldStatus)
          {
          default: 
            paramPrintWriter.print(this.oldStatus);
            if (this.oldHealth != paramHistoryItem.batteryHealth)
            {
              this.oldHealth = paramHistoryItem.batteryHealth;
              if (paramBoolean1)
              {
                localObject = ",Bh=";
                paramPrintWriter.print((String)localObject);
              }
            }
            else
            {
              switch (this.oldHealth)
              {
              default: 
                paramPrintWriter.print(this.oldHealth);
                if (this.oldPlug != paramHistoryItem.batteryPlugType)
                {
                  this.oldPlug = paramHistoryItem.batteryPlugType;
                  if (paramBoolean1)
                  {
                    localObject = ",Bp=";
                    paramPrintWriter.print((String)localObject);
                  }
                }
                else
                {
                  switch (this.oldPlug)
                  {
                  case 3: 
                  default: 
                    paramPrintWriter.print(this.oldPlug);
                    if (this.oldTemp != paramHistoryItem.batteryTemperature)
                    {
                      this.oldTemp = paramHistoryItem.batteryTemperature;
                      if (paramBoolean1)
                      {
                        localObject = ",Bt=";
                        paramPrintWriter.print((String)localObject);
                        paramPrintWriter.print(this.oldTemp);
                      }
                    }
                    else
                    {
                      if (this.oldVolt != paramHistoryItem.batteryVoltage)
                      {
                        this.oldVolt = paramHistoryItem.batteryVoltage;
                        if (!paramBoolean1) {
                          break label2076;
                        }
                        localObject = ",Bv=";
                        paramPrintWriter.print((String)localObject);
                        paramPrintWriter.print(this.oldVolt);
                      }
                      i = paramHistoryItem.batteryChargeUAh / 1000;
                      if (this.oldChargeMAh != i)
                      {
                        this.oldChargeMAh = i;
                        if (!paramBoolean1) {
                          break label2084;
                        }
                        localObject = ",Bcc=";
                        label726:
                        paramPrintWriter.print((String)localObject);
                        paramPrintWriter.print(this.oldChargeMAh);
                      }
                      i = this.oldState;
                      int j = paramHistoryItem.states;
                      localObject = paramHistoryItem.wakelockTag;
                      BatteryStats.BitDescription[] arrayOfBitDescription = BatteryStats.HISTORY_STATE_DESCRIPTIONS;
                      if (!paramBoolean1) {
                        break label2092;
                      }
                      paramBoolean2 = false;
                      label771:
                      BatteryStats.printBitDescriptions(paramPrintWriter, i, j, (BatteryStats.HistoryTag)localObject, arrayOfBitDescription, paramBoolean2);
                      i = this.oldState2;
                      j = paramHistoryItem.states2;
                      localObject = BatteryStats.HISTORY_STATE2_DESCRIPTIONS;
                      if (!paramBoolean1) {
                        break label2098;
                      }
                      paramBoolean2 = false;
                      label810:
                      BatteryStats.printBitDescriptions(paramPrintWriter, i, j, null, (BatteryStats.BitDescription[])localObject, paramBoolean2);
                      if (paramHistoryItem.wakeReasonTag != null)
                      {
                        if (!paramBoolean1) {
                          break label2104;
                        }
                        paramPrintWriter.print(",wr=");
                        paramPrintWriter.print(paramHistoryItem.wakeReasonTag.poolIdx);
                      }
                      label852:
                      if (paramHistoryItem.eventCode != 0)
                      {
                        if (!paramBoolean1) {
                          break label2150;
                        }
                        localObject = ",";
                        label868:
                        paramPrintWriter.print((String)localObject);
                        if ((paramHistoryItem.eventCode & 0x8000) == 0) {
                          break label2157;
                        }
                        paramPrintWriter.print("+");
                        label890:
                        if (!paramBoolean1) {
                          break label2178;
                        }
                        localObject = BatteryStats.HISTORY_EVENT_CHECKIN_NAMES;
                        label900:
                        i = paramHistoryItem.eventCode & 0xFFFF3FFF;
                        if ((i < 0) || (i >= localObject.length)) {
                          break label2186;
                        }
                        paramPrintWriter.print(localObject[i]);
                        paramPrintWriter.print("=");
                        if (!paramBoolean1) {
                          break label2219;
                        }
                        paramPrintWriter.print(paramHistoryItem.eventTag.poolIdx);
                      }
                      label953:
                      paramPrintWriter.println();
                      if (paramHistoryItem.stepDetails != null)
                      {
                        if (paramBoolean1) {
                          break label2258;
                        }
                        paramPrintWriter.print("                 Details: cpu=");
                        paramPrintWriter.print(paramHistoryItem.stepDetails.userTime);
                        paramPrintWriter.print("u+");
                        paramPrintWriter.print(paramHistoryItem.stepDetails.systemTime);
                        paramPrintWriter.print("s");
                        if (paramHistoryItem.stepDetails.appCpuUid1 >= 0)
                        {
                          paramPrintWriter.print(" (");
                          printStepCpuUidDetails(paramPrintWriter, paramHistoryItem.stepDetails.appCpuUid1, paramHistoryItem.stepDetails.appCpuUTime1, paramHistoryItem.stepDetails.appCpuSTime1);
                          if (paramHistoryItem.stepDetails.appCpuUid2 >= 0)
                          {
                            paramPrintWriter.print(", ");
                            printStepCpuUidDetails(paramPrintWriter, paramHistoryItem.stepDetails.appCpuUid2, paramHistoryItem.stepDetails.appCpuUTime2, paramHistoryItem.stepDetails.appCpuSTime2);
                          }
                          if (paramHistoryItem.stepDetails.appCpuUid3 >= 0)
                          {
                            paramPrintWriter.print(", ");
                            printStepCpuUidDetails(paramPrintWriter, paramHistoryItem.stepDetails.appCpuUid3, paramHistoryItem.stepDetails.appCpuUTime3, paramHistoryItem.stepDetails.appCpuSTime3);
                          }
                          paramPrintWriter.print(')');
                        }
                        paramPrintWriter.println();
                        paramPrintWriter.print("                          /proc/stat=");
                        paramPrintWriter.print(paramHistoryItem.stepDetails.statUserTime);
                        paramPrintWriter.print(" usr, ");
                        paramPrintWriter.print(paramHistoryItem.stepDetails.statSystemTime);
                        paramPrintWriter.print(" sys, ");
                        paramPrintWriter.print(paramHistoryItem.stepDetails.statIOWaitTime);
                        paramPrintWriter.print(" io, ");
                        paramPrintWriter.print(paramHistoryItem.stepDetails.statIrqTime);
                        paramPrintWriter.print(" irq, ");
                        paramPrintWriter.print(paramHistoryItem.stepDetails.statSoftIrqTime);
                        paramPrintWriter.print(" sirq, ");
                        paramPrintWriter.print(paramHistoryItem.stepDetails.statIdlTime);
                        paramPrintWriter.print(" idle");
                        i = paramHistoryItem.stepDetails.statUserTime + paramHistoryItem.stepDetails.statSystemTime + paramHistoryItem.stepDetails.statIOWaitTime + paramHistoryItem.stepDetails.statIrqTime + paramHistoryItem.stepDetails.statSoftIrqTime;
                        j = i + paramHistoryItem.stepDetails.statIdlTime;
                        if (j > 0)
                        {
                          paramPrintWriter.print(" (");
                          paramPrintWriter.print(String.format("%.1f%%", new Object[] { Float.valueOf(i / j * 100.0F) }));
                          paramPrintWriter.print(" of ");
                          localObject = new StringBuilder(64);
                          BatteryStats.formatTimeMsNoSpace((StringBuilder)localObject, j * 10);
                          paramPrintWriter.print(localObject);
                          paramPrintWriter.print(")");
                        }
                        paramPrintWriter.print(", PlatformIdleStat ");
                        paramPrintWriter.print(paramHistoryItem.stepDetails.statPlatformIdleState);
                        paramPrintWriter.println();
                      }
                    }
                    break;
                  }
                }
                break;
              }
            }
            break;
          }
        }
      }
      for (;;)
      {
        this.oldState = paramHistoryItem.states;
        this.oldState2 = paramHistoryItem.states2;
        return;
        if (paramHistoryItem.batteryLevel >= 100) {
          break;
        }
        paramPrintWriter.print("0");
        break;
        label1457:
        if (paramHistoryItem.states < 256)
        {
          paramPrintWriter.print("000000");
          break label350;
        }
        if (paramHistoryItem.states < 4096)
        {
          paramPrintWriter.print("00000");
          break label350;
        }
        if (paramHistoryItem.states < 65536)
        {
          paramPrintWriter.print("0000");
          break label350;
        }
        if (paramHistoryItem.states < 1048576)
        {
          paramPrintWriter.print("000");
          break label350;
        }
        if (paramHistoryItem.states < 16777216)
        {
          paramPrintWriter.print("00");
          break label350;
        }
        if (paramHistoryItem.states >= 268435456) {
          break label350;
        }
        paramPrintWriter.print("0");
        break label350;
        if (this.oldLevel == paramHistoryItem.batteryLevel) {
          break label361;
        }
        this.oldLevel = paramHistoryItem.batteryLevel;
        paramPrintWriter.print(",Bl=");
        paramPrintWriter.print(paramHistoryItem.batteryLevel);
        break label361;
        label1613:
        localObject = " status=";
        break label389;
        if (paramBoolean1) {}
        for (localObject = "?";; localObject = "unknown")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "c";; localObject = "charging")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "d";; localObject = "discharging")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "n";; localObject = "not-charging")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "f";; localObject = "full")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        localObject = " health=";
        break label468;
        if (paramBoolean1) {}
        for (localObject = "?";; localObject = "unknown")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "g";; localObject = "good")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "h";; localObject = "overheat")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "d";; localObject = "dead")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "v";; localObject = "over-voltage")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "f";; localObject = "failure")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "c";; localObject = "cold")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        localObject = " plug=";
        break label556;
        if (paramBoolean1) {}
        for (localObject = "n";; localObject = "none")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "a";; localObject = "ac")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "u";; localObject = "usb")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        if (paramBoolean1) {}
        for (localObject = "w";; localObject = "wireless")
        {
          paramPrintWriter.print((String)localObject);
          break;
        }
        localObject = " temp=";
        break label636;
        label2076:
        localObject = " volt=";
        break label678;
        label2084:
        localObject = " charge=";
        break label726;
        label2092:
        paramBoolean2 = true;
        break label771;
        label2098:
        paramBoolean2 = true;
        break label810;
        label2104:
        paramPrintWriter.print(" wake_reason=");
        paramPrintWriter.print(paramHistoryItem.wakeReasonTag.uid);
        paramPrintWriter.print(":\"");
        paramPrintWriter.print(paramHistoryItem.wakeReasonTag.string);
        paramPrintWriter.print("\"");
        break label852;
        label2150:
        localObject = " ";
        break label868;
        label2157:
        if ((paramHistoryItem.eventCode & 0x4000) == 0) {
          break label890;
        }
        paramPrintWriter.print("-");
        break label890;
        label2178:
        localObject = BatteryStats.HISTORY_EVENT_NAMES;
        break label900;
        label2186:
        if (paramBoolean1) {}
        for (localObject = "Ev";; localObject = "event")
        {
          paramPrintWriter.print((String)localObject);
          paramPrintWriter.print(i);
          break;
        }
        label2219:
        UserHandle.formatUid(paramPrintWriter, paramHistoryItem.eventTag.uid);
        paramPrintWriter.print(":\"");
        paramPrintWriter.print(paramHistoryItem.eventTag.string);
        paramPrintWriter.print("\"");
        break label953;
        label2258:
        paramPrintWriter.print(9);
        paramPrintWriter.print(',');
        paramPrintWriter.print("h");
        paramPrintWriter.print(",0,Dcpu=");
        paramPrintWriter.print(paramHistoryItem.stepDetails.userTime);
        paramPrintWriter.print(":");
        paramPrintWriter.print(paramHistoryItem.stepDetails.systemTime);
        if (paramHistoryItem.stepDetails.appCpuUid1 >= 0)
        {
          printStepCpuUidCheckinDetails(paramPrintWriter, paramHistoryItem.stepDetails.appCpuUid1, paramHistoryItem.stepDetails.appCpuUTime1, paramHistoryItem.stepDetails.appCpuSTime1);
          if (paramHistoryItem.stepDetails.appCpuUid2 >= 0) {
            printStepCpuUidCheckinDetails(paramPrintWriter, paramHistoryItem.stepDetails.appCpuUid2, paramHistoryItem.stepDetails.appCpuUTime2, paramHistoryItem.stepDetails.appCpuSTime2);
          }
          if (paramHistoryItem.stepDetails.appCpuUid3 >= 0) {
            printStepCpuUidCheckinDetails(paramPrintWriter, paramHistoryItem.stepDetails.appCpuUid3, paramHistoryItem.stepDetails.appCpuUTime3, paramHistoryItem.stepDetails.appCpuSTime3);
          }
        }
        paramPrintWriter.println();
        paramPrintWriter.print(9);
        paramPrintWriter.print(',');
        paramPrintWriter.print("h");
        paramPrintWriter.print(",0,Dpst=");
        paramPrintWriter.print(paramHistoryItem.stepDetails.statUserTime);
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramHistoryItem.stepDetails.statSystemTime);
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramHistoryItem.stepDetails.statIOWaitTime);
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramHistoryItem.stepDetails.statIrqTime);
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramHistoryItem.stepDetails.statSoftIrqTime);
        paramPrintWriter.print(',');
        paramPrintWriter.print(paramHistoryItem.stepDetails.statIdlTime);
        paramPrintWriter.print(',');
        if (paramHistoryItem.stepDetails.statPlatformIdleState != null) {
          paramPrintWriter.print(paramHistoryItem.stepDetails.statPlatformIdleState);
        }
        paramPrintWriter.println();
      }
    }
    
    void reset()
    {
      this.oldState2 = 0;
      this.oldState = 0;
      this.oldLevel = -1;
      this.oldStatus = -1;
      this.oldHealth = -1;
      this.oldPlug = -1;
      this.oldTemp = -1;
      this.oldVolt = -1;
      this.oldChargeMAh = -1;
    }
  }
  
  public static final class HistoryStepDetails
  {
    public int appCpuSTime1;
    public int appCpuSTime2;
    public int appCpuSTime3;
    public int appCpuUTime1;
    public int appCpuUTime2;
    public int appCpuUTime3;
    public int appCpuUid1;
    public int appCpuUid2;
    public int appCpuUid3;
    public int statIOWaitTime;
    public int statIdlTime;
    public int statIrqTime;
    public String statPlatformIdleState;
    public int statSoftIrqTime;
    public int statSystemTime;
    public int statUserTime;
    public int systemTime;
    public int userTime;
    
    public HistoryStepDetails()
    {
      clear();
    }
    
    public void clear()
    {
      this.systemTime = 0;
      this.userTime = 0;
      this.appCpuUid3 = -1;
      this.appCpuUid2 = -1;
      this.appCpuUid1 = -1;
      this.appCpuSTime3 = 0;
      this.appCpuUTime3 = 0;
      this.appCpuSTime2 = 0;
      this.appCpuUTime2 = 0;
      this.appCpuSTime1 = 0;
      this.appCpuUTime1 = 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.userTime = paramParcel.readInt();
      this.systemTime = paramParcel.readInt();
      this.appCpuUid1 = paramParcel.readInt();
      this.appCpuUTime1 = paramParcel.readInt();
      this.appCpuSTime1 = paramParcel.readInt();
      this.appCpuUid2 = paramParcel.readInt();
      this.appCpuUTime2 = paramParcel.readInt();
      this.appCpuSTime2 = paramParcel.readInt();
      this.appCpuUid3 = paramParcel.readInt();
      this.appCpuUTime3 = paramParcel.readInt();
      this.appCpuSTime3 = paramParcel.readInt();
      this.statUserTime = paramParcel.readInt();
      this.statSystemTime = paramParcel.readInt();
      this.statIOWaitTime = paramParcel.readInt();
      this.statIrqTime = paramParcel.readInt();
      this.statSoftIrqTime = paramParcel.readInt();
      this.statIdlTime = paramParcel.readInt();
      this.statPlatformIdleState = paramParcel.readString();
    }
    
    public void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeInt(this.userTime);
      paramParcel.writeInt(this.systemTime);
      paramParcel.writeInt(this.appCpuUid1);
      paramParcel.writeInt(this.appCpuUTime1);
      paramParcel.writeInt(this.appCpuSTime1);
      paramParcel.writeInt(this.appCpuUid2);
      paramParcel.writeInt(this.appCpuUTime2);
      paramParcel.writeInt(this.appCpuSTime2);
      paramParcel.writeInt(this.appCpuUid3);
      paramParcel.writeInt(this.appCpuUTime3);
      paramParcel.writeInt(this.appCpuSTime3);
      paramParcel.writeInt(this.statUserTime);
      paramParcel.writeInt(this.statSystemTime);
      paramParcel.writeInt(this.statIOWaitTime);
      paramParcel.writeInt(this.statIrqTime);
      paramParcel.writeInt(this.statSoftIrqTime);
      paramParcel.writeInt(this.statIdlTime);
      paramParcel.writeString(this.statPlatformIdleState);
    }
  }
  
  public static final class HistoryTag
  {
    public int poolIdx;
    public String string;
    public int uid;
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (HistoryTag)paramObject;
      if (this.uid != ((HistoryTag)paramObject).uid) {
        return false;
      }
      return this.string.equals(((HistoryTag)paramObject).string);
    }
    
    public int hashCode()
    {
      return this.string.hashCode() * 31 + this.uid;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.string = paramParcel.readString();
      this.uid = paramParcel.readInt();
      this.poolIdx = -1;
    }
    
    public void setTo(HistoryTag paramHistoryTag)
    {
      this.string = paramHistoryTag.string;
      this.uid = paramHistoryTag.uid;
      this.poolIdx = paramHistoryTag.poolIdx;
    }
    
    public void setTo(String paramString, int paramInt)
    {
      this.string = paramString;
      this.uid = paramInt;
      this.poolIdx = -1;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.string);
      paramParcel.writeInt(this.uid);
    }
  }
  
  public static final class LevelStepTracker
  {
    public long mLastStepTime = -1L;
    public int mNumStepDurations;
    public final long[] mStepDurations;
    
    public LevelStepTracker(int paramInt)
    {
      this.mStepDurations = new long[paramInt];
    }
    
    public LevelStepTracker(int paramInt, long[] paramArrayOfLong)
    {
      this.mNumStepDurations = paramInt;
      this.mStepDurations = new long[paramInt];
      System.arraycopy(paramArrayOfLong, 0, this.mStepDurations, 0, paramInt);
    }
    
    private void appendHex(long paramLong, int paramInt, StringBuilder paramStringBuilder)
    {
      int i = 0;
      while (paramInt >= 0)
      {
        int k = (int)(paramLong >> paramInt & 0xF);
        int j = paramInt - 4;
        if (i == 0)
        {
          paramInt = j;
          if (k == 0) {}
        }
        else
        {
          i = 1;
          if ((k >= 0) && (k <= 9))
          {
            paramStringBuilder.append((char)(k + 48));
            paramInt = j;
          }
          else
          {
            paramStringBuilder.append((char)(k + 97 - 10));
            paramInt = j;
          }
        }
      }
    }
    
    public void addLevelSteps(int paramInt, long paramLong1, long paramLong2)
    {
      int j = this.mNumStepDurations;
      long l1 = this.mLastStepTime;
      int i = j;
      if (l1 >= 0L)
      {
        i = j;
        if (paramInt > 0)
        {
          long[] arrayOfLong = this.mStepDurations;
          l1 = paramLong2 - l1;
          i = 0;
          while (i < paramInt)
          {
            System.arraycopy(arrayOfLong, 0, arrayOfLong, 1, arrayOfLong.length - 1);
            long l2 = l1 / (paramInt - i);
            long l3 = l1 - l2;
            l1 = l2;
            if (l2 > 1099511627775L) {
              l1 = 1099511627775L;
            }
            arrayOfLong[0] = (l1 | paramLong1);
            i += 1;
            l1 = l3;
          }
          paramInt = j + paramInt;
          i = paramInt;
          if (paramInt > arrayOfLong.length) {
            i = arrayOfLong.length;
          }
        }
      }
      this.mNumStepDurations = i;
      this.mLastStepTime = paramLong2;
    }
    
    public void clearTime()
    {
      this.mLastStepTime = -1L;
    }
    
    public long computeTimeEstimate(long paramLong1, long paramLong2, int[] paramArrayOfInt)
    {
      long[] arrayOfLong = this.mStepDurations;
      int m = this.mNumStepDurations;
      if (m <= 0) {
        return -1L;
      }
      long l1 = 0L;
      int j = 0;
      int i = 0;
      while (i < m)
      {
        long l3 = arrayOfLong[i];
        int k = j;
        long l2 = l1;
        if (((arrayOfLong[i] & 0xFF00000000000000) >> 56 & paramLong1) == 0L)
        {
          k = j;
          l2 = l1;
          if (((l3 & 0xFF000000000000) >> 48 & paramLong1) == paramLong2)
          {
            k = j + 1;
            l2 = l1 + (arrayOfLong[i] & 0xFFFFFFFFFF);
          }
        }
        i += 1;
        j = k;
        l1 = l2;
      }
      if (j <= 0) {
        return -1L;
      }
      if (paramArrayOfInt != null) {
        paramArrayOfInt[0] = j;
      }
      return l1 / j * 100L;
    }
    
    public long computeTimePerLevel()
    {
      long[] arrayOfLong = this.mStepDurations;
      int j = this.mNumStepDurations;
      if (j <= 0) {
        return -1L;
      }
      long l = 0L;
      int i = 0;
      while (i < j)
      {
        l += (arrayOfLong[i] & 0xFFFFFFFFFF);
        i += 1;
      }
      return l / j;
    }
    
    public void decodeEntryAt(int paramInt, String paramString)
    {
      int k = paramString.length();
      int i = 0;
      long l1 = 0L;
      int j;
      while (i < k)
      {
        j = paramString.charAt(i);
        if (j == 45) {
          break;
        }
        i += 1;
        switch (j)
        {
        default: 
          break;
        case 68: 
          l1 |= 0x200000000000000;
          break;
        case 102: 
          l1 |= 0L;
          break;
        case 111: 
          l1 |= 0x1000000000000;
          break;
        case 100: 
          l1 |= 0x2000000000000;
          break;
        case 122: 
          l1 |= 0x3000000000000;
          break;
        case 112: 
          l1 |= 0x4000000000000;
          break;
        case 105: 
          l1 |= 0x8000000000000;
          break;
        case 70: 
          l1 |= 0L;
          break;
        case 79: 
          l1 |= 0x100000000000000;
          break;
        case 90: 
          l1 |= 0x300000000000000;
          break;
        case 80: 
          l1 |= 0x400000000000000;
          break;
        case 73: 
          l1 |= 0x800000000000000;
        }
      }
      i += 1;
      long l2 = 0L;
      int m;
      while (i < k)
      {
        m = paramString.charAt(i);
        if (m == 45) {
          break;
        }
        j = i + 1;
        l3 = l2 << 4;
        if ((m >= 48) && (m <= 57))
        {
          l2 = l3 + (m - 48);
          i = j;
        }
        else if ((m >= 97) && (m <= 102))
        {
          l2 = l3 + (m - 97 + 10);
          i = j;
        }
        else
        {
          i = j;
          l2 = l3;
          if (m >= 65)
          {
            i = j;
            l2 = l3;
            if (m <= 70)
            {
              l2 = l3 + (m - 65 + 10);
              i = j;
            }
          }
        }
      }
      i += 1;
      long l3 = 0L;
      while (i < k)
      {
        m = paramString.charAt(i);
        if (m == 45) {
          break;
        }
        j = i + 1;
        long l4 = l3 << 4;
        if ((m >= 48) && (m <= 57))
        {
          l3 = l4 + (m - 48);
          i = j;
        }
        else if ((m >= 97) && (m <= 102))
        {
          l3 = l4 + (m - 97 + 10);
          i = j;
        }
        else
        {
          l3 = l4;
          i = j;
          if (m >= 65)
          {
            l3 = l4;
            i = j;
            if (m <= 70)
            {
              l3 = l4 + (m - 65 + 10);
              i = j;
            }
          }
        }
      }
      this.mStepDurations[paramInt] = (0xFFFFFFFFFF & l3 | l1 | l2 << 40 & 0xFF0000000000);
    }
    
    public void encodeEntryAt(int paramInt, StringBuilder paramStringBuilder)
    {
      long l = this.mStepDurations[paramInt];
      paramInt = (int)((0xFF0000000000 & l) >> 40);
      int i = (int)((0xFF000000000000 & l) >> 48);
      int j = (int)((0xFF00000000000000 & l) >> 56);
      switch ((i & 0x3) + 1)
      {
      default: 
        if ((i & 0x4) != 0) {
          paramStringBuilder.append('p');
        }
        if ((i & 0x8) != 0) {
          paramStringBuilder.append('i');
        }
        switch ((j & 0x3) + 1)
        {
        }
        break;
      }
      for (;;)
      {
        if ((j & 0x4) != 0) {
          paramStringBuilder.append('P');
        }
        if ((j & 0x8) != 0) {
          paramStringBuilder.append('I');
        }
        paramStringBuilder.append('-');
        appendHex(paramInt, 4, paramStringBuilder);
        paramStringBuilder.append('-');
        appendHex(l & 0xFFFFFFFFFF, 36, paramStringBuilder);
        return;
        paramStringBuilder.append('f');
        break;
        paramStringBuilder.append('o');
        break;
        paramStringBuilder.append('d');
        break;
        paramStringBuilder.append('z');
        break;
        paramStringBuilder.append('F');
        continue;
        paramStringBuilder.append('O');
        continue;
        paramStringBuilder.append('D');
        continue;
        paramStringBuilder.append('Z');
      }
    }
    
    public long getDurationAt(int paramInt)
    {
      return this.mStepDurations[paramInt] & 0xFFFFFFFFFF;
    }
    
    public int getInitModeAt(int paramInt)
    {
      return (int)((this.mStepDurations[paramInt] & 0xFF000000000000) >> 48);
    }
    
    public int getLevelAt(int paramInt)
    {
      return (int)((this.mStepDurations[paramInt] & 0xFF0000000000) >> 40);
    }
    
    public int getModModeAt(int paramInt)
    {
      return (int)((this.mStepDurations[paramInt] & 0xFF00000000000000) >> 56);
    }
    
    public void init()
    {
      this.mLastStepTime = -1L;
      this.mNumStepDurations = 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      int j = paramParcel.readInt();
      if (j > this.mStepDurations.length) {
        throw new ParcelFormatException("more step durations than available: " + j);
      }
      this.mNumStepDurations = j;
      int i = 0;
      while (i < j)
      {
        this.mStepDurations[i] = paramParcel.readLong();
        i += 1;
      }
    }
    
    public void writeToParcel(Parcel paramParcel)
    {
      int j = this.mNumStepDurations;
      paramParcel.writeInt(j);
      int i = 0;
      while (i < j)
      {
        paramParcel.writeLong(this.mStepDurations[i]);
        i += 1;
      }
    }
  }
  
  public static abstract class LongCounter
  {
    public abstract long getCountLocked(int paramInt);
    
    public abstract void logState(Printer paramPrinter, String paramString);
  }
  
  public static final class PackageChange
  {
    public String mPackageName;
    public boolean mUpdate;
    public int mVersionCode;
  }
  
  public static abstract class Timer
  {
    public abstract int getCountLocked(int paramInt);
    
    public long getCurrentDurationMsLocked(long paramLong)
    {
      return -1L;
    }
    
    public long getMaxDurationMsLocked(long paramLong)
    {
      return -1L;
    }
    
    public abstract long getTimeSinceMarkLocked(long paramLong);
    
    public abstract long getTotalTimeLocked(long paramLong, int paramInt);
    
    public boolean isRunningLocked()
    {
      return false;
    }
    
    public abstract void logState(Printer paramPrinter, String paramString);
  }
  
  static final class TimerEntry
  {
    final int mId;
    final String mName;
    final long mTime;
    final BatteryStats.Timer mTimer;
    
    TimerEntry(String paramString, int paramInt, BatteryStats.Timer paramTimer, long paramLong)
    {
      this.mName = paramString;
      this.mId = paramInt;
      this.mTimer = paramTimer;
      this.mTime = paramLong;
    }
  }
  
  public static abstract class Uid
  {
    public static final int NUM_PROCESS_STATE = 6;
    public static final int NUM_USER_ACTIVITY_TYPES = 4;
    public static final int NUM_WIFI_BATCHED_SCAN_BINS = 5;
    public static final int PROCESS_STATE_BACKGROUND = 4;
    public static final int PROCESS_STATE_CACHED = 5;
    public static final int PROCESS_STATE_FOREGROUND = 3;
    public static final int PROCESS_STATE_FOREGROUND_SERVICE = 1;
    static final String[] PROCESS_STATE_NAMES = { "Top", "Fg Service", "Top Sleeping", "Foreground", "Background", "Cached" };
    public static final int PROCESS_STATE_TOP = 0;
    public static final int PROCESS_STATE_TOP_SLEEPING = 2;
    static final String[] USER_ACTIVITY_TYPES = { "other", "button", "touch", "accessibility" };
    
    public abstract BatteryStats.Timer getAudioTurnedOnTimer();
    
    public abstract BatteryStats.ControllerActivityCounter getBluetoothControllerActivity();
    
    public abstract BatteryStats.Timer getBluetoothScanTimer();
    
    public abstract BatteryStats.Timer getCameraTurnedOnTimer();
    
    public abstract long getCpuPowerMaUs(int paramInt);
    
    public abstract BatteryStats.Timer getFlashlightTurnedOnTimer();
    
    public abstract BatteryStats.Timer getForegroundActivityTimer();
    
    public abstract long getFullWifiLockTime(long paramLong, int paramInt);
    
    public abstract ArrayMap<String, ? extends BatteryStats.Timer> getJobStats();
    
    public abstract int getMobileRadioActiveCount(int paramInt);
    
    public abstract long getMobileRadioActiveTime(int paramInt);
    
    public abstract long getMobileRadioApWakeupCount(int paramInt);
    
    public abstract BatteryStats.ControllerActivityCounter getModemControllerActivity();
    
    public abstract long getNetworkActivityBytes(int paramInt1, int paramInt2);
    
    public abstract long getNetworkActivityPackets(int paramInt1, int paramInt2);
    
    public abstract ArrayMap<String, ? extends Pkg> getPackageStats();
    
    public abstract SparseArray<? extends Pid> getPidStats();
    
    public abstract long getProcessStateTime(int paramInt1, long paramLong, int paramInt2);
    
    public abstract BatteryStats.Timer getProcessStateTimer(int paramInt);
    
    public abstract ArrayMap<String, ? extends Proc> getProcessStats();
    
    public abstract SparseArray<? extends Sensor> getSensorStats();
    
    public abstract ArrayMap<String, ? extends BatteryStats.Timer> getSyncStats();
    
    public abstract long getSystemCpuTimeUs(int paramInt);
    
    public abstract long getTimeAtCpuSpeed(int paramInt1, int paramInt2, int paramInt3);
    
    public abstract int getUid();
    
    public abstract int getUserActivityCount(int paramInt1, int paramInt2);
    
    public abstract long getUserCpuTimeUs(int paramInt);
    
    public abstract BatteryStats.Timer getVibratorOnTimer();
    
    public abstract BatteryStats.Timer getVideoTurnedOnTimer();
    
    public abstract ArrayMap<String, ? extends Wakelock> getWakelockStats();
    
    public abstract int getWifiBatchedScanCount(int paramInt1, int paramInt2);
    
    public abstract long getWifiBatchedScanTime(int paramInt1, long paramLong, int paramInt2);
    
    public abstract BatteryStats.ControllerActivityCounter getWifiControllerActivity();
    
    public abstract long getWifiMulticastTime(long paramLong, int paramInt);
    
    public abstract long getWifiRadioApWakeupCount(int paramInt);
    
    public abstract long getWifiRunningTime(long paramLong, int paramInt);
    
    public abstract int getWifiScanCount(int paramInt);
    
    public abstract long getWifiScanTime(long paramLong, int paramInt);
    
    public abstract boolean hasNetworkActivity();
    
    public abstract boolean hasUserActivity();
    
    public abstract void noteActivityPausedLocked(long paramLong);
    
    public abstract void noteActivityResumedLocked(long paramLong);
    
    public abstract void noteFullWifiLockAcquiredLocked(long paramLong);
    
    public abstract void noteFullWifiLockReleasedLocked(long paramLong);
    
    public abstract void noteUserActivityLocked(int paramInt);
    
    public abstract void noteWifiBatchedScanStartedLocked(int paramInt, long paramLong);
    
    public abstract void noteWifiBatchedScanStoppedLocked(long paramLong);
    
    public abstract void noteWifiMulticastDisabledLocked(long paramLong);
    
    public abstract void noteWifiMulticastEnabledLocked(long paramLong);
    
    public abstract void noteWifiRunningLocked(long paramLong);
    
    public abstract void noteWifiScanStartedLocked(long paramLong);
    
    public abstract void noteWifiScanStoppedLocked(long paramLong);
    
    public abstract void noteWifiStoppedLocked(long paramLong);
    
    public class Pid
    {
      public int mWakeNesting;
      public long mWakeStartMs;
      public long mWakeSumMs;
      
      public Pid() {}
    }
    
    public static abstract class Pkg
    {
      public abstract ArrayMap<String, ? extends Serv> getServiceStats();
      
      public abstract ArrayMap<String, ? extends BatteryStats.Counter> getWakeupAlarmStats();
      
      public static abstract class Serv
      {
        public abstract int getLaunches(int paramInt);
        
        public abstract long getStartTime(long paramLong, int paramInt);
        
        public abstract int getStarts(int paramInt);
      }
    }
    
    public static abstract class Proc
    {
      public abstract int countExcessivePowers();
      
      public abstract ExcessivePower getExcessivePower(int paramInt);
      
      public abstract long getForegroundTime(int paramInt);
      
      public abstract int getNumAnrs(int paramInt);
      
      public abstract int getNumCrashes(int paramInt);
      
      public abstract int getStarts(int paramInt);
      
      public abstract long getSystemTime(int paramInt);
      
      public abstract long getUserTime(int paramInt);
      
      public abstract boolean isActive();
      
      public static class ExcessivePower
      {
        public static final int TYPE_CPU = 2;
        public static final int TYPE_WAKE = 1;
        public long overTime;
        public int type;
        public long usedTime;
      }
    }
    
    public static abstract class Sensor
    {
      public static final int GPS = -10000;
      
      public abstract int getHandle();
      
      public abstract BatteryStats.Timer getSensorTime();
    }
    
    public static abstract class Wakelock
    {
      public abstract BatteryStats.Timer getWakeTime(int paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/BatteryStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */