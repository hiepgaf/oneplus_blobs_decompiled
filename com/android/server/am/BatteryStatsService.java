package com.android.server.am;

import android.bluetooth.BluetoothActivityEnergyInfo;
import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.IWifiManager;
import android.net.wifi.WifiActivityEnergyInfo;
import android.os.BatteryStats.Uid;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.PowerManagerInternal;
import android.os.PowerManagerInternal.LowPowerModeListener;
import android.os.Process;
import android.os.ServiceManager;
import android.os.SynchronousResultReceiver;
import android.os.SynchronousResultReceiver.Result;
import android.os.SystemClock;
import android.os.WorkSource;
import android.os.health.HealthStatsParceler;
import android.os.health.HealthStatsWriter;
import android.os.health.UidHealthStats;
import android.telephony.ModemActivityInfo;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.IntArray;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.IBatteryStats.Stub;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.os.BatteryStatsImpl.ExternalStatsSync;
import com.android.internal.os.BatteryStatsImpl.PlatformIdleStateCallback;
import com.android.internal.os.PowerProfile;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public final class BatteryStatsService
  extends IBatteryStats.Stub
  implements PowerManagerInternal.LowPowerModeListener, BatteryStatsImpl.PlatformIdleStateCallback
{
  private static final long EXTERNAL_STATS_SYNC_TIMEOUT_MILLIS = 2000L;
  private static final int MAX_LOW_POWER_STATS_SIZE = 512;
  private static final long MAX_WIFI_STATS_SAMPLE_ERROR_MILLIS = 750L;
  static final String TAG = "BatteryStatsService";
  private static IBatteryStats sService;
  private Context mContext;
  private CharsetDecoder mDecoderStat = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith("?");
  private final Object mExternalStatsLock = new Object();
  private final BatteryStatsHandler mHandler;
  @GuardedBy("mExternalStatsLock")
  private WifiActivityEnergyInfo mLastInfo = new WifiActivityEnergyInfo(0L, 0, 0L, new long[] { 0L }, 0L, 0L, 0L);
  final BatteryStatsImpl mStats;
  private TelephonyManager mTelephony;
  private CharBuffer mUtf16BufferStat = CharBuffer.allocate(512);
  private ByteBuffer mUtf8BufferStat = ByteBuffer.allocateDirect(512);
  private IWifiManager mWifiManager;
  
  BatteryStatsService(File paramFile, Handler paramHandler)
  {
    ServiceThread localServiceThread = new ServiceThread("batterystats-sync", 0, true);
    localServiceThread.start();
    this.mHandler = new BatteryStatsHandler(localServiceThread.getLooper());
    this.mStats = new BatteryStatsImpl(paramFile, paramHandler, this.mHandler, this);
  }
  
  private static <T extends Parcelable> T awaitControllerInfo(SynchronousResultReceiver paramSynchronousResultReceiver)
    throws TimeoutException
  {
    if (paramSynchronousResultReceiver == null) {
      return null;
    }
    paramSynchronousResultReceiver = paramSynchronousResultReceiver.awaitResult(2000L);
    if (paramSynchronousResultReceiver.bundle != null)
    {
      paramSynchronousResultReceiver.bundle.setDefusable(true);
      paramSynchronousResultReceiver = paramSynchronousResultReceiver.bundle.getParcelable("controller_activity");
      if (paramSynchronousResultReceiver != null) {
        return paramSynchronousResultReceiver;
      }
    }
    Slog.e("BatteryStatsService", "no controller energy info supplied");
    return null;
  }
  
  private int doEnableOrDisable(PrintWriter arg1, int paramInt, String[] paramArrayOfString, boolean paramBoolean)
  {
    paramInt += 1;
    if (paramInt >= paramArrayOfString.length)
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Missing option argument for ");
      if (paramBoolean) {}
      for (paramArrayOfString = "--enable";; paramArrayOfString = "--disable")
      {
        ???.println(paramArrayOfString);
        dumpHelp(???);
        return -1;
      }
    }
    if (("full-wake-history".equals(paramArrayOfString[paramInt])) || ("full-history".equals(paramArrayOfString[paramInt]))) {}
    for (;;)
    {
      synchronized (this.mStats)
      {
        this.mStats.setRecordAllHistoryLocked(paramBoolean);
        return paramInt;
      }
      if ("no-auto-reset".equals(paramArrayOfString[paramInt])) {}
      synchronized (this.mStats)
      {
        this.mStats.setNoAutoReset(paramBoolean);
      }
    }
    dumpHelp(???);
    return -1;
  }
  
  private void dumpHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("Battery stats (batterystats) dump options:");
    paramPrintWriter.println("  [--checkin] [--history] [--history-start] [--charged] [-c]");
    paramPrintWriter.println("  [--daily] [--reset] [--write] [--new-daily] [--read-daily] [-h] [<package.name>]");
    paramPrintWriter.println("  --checkin: generate output for a checkin report; will write (and clear) the");
    paramPrintWriter.println("             last old completed stats when they had been reset.");
    paramPrintWriter.println("  -c: write the current stats in checkin format.");
    paramPrintWriter.println("  --history: show only history data.");
    paramPrintWriter.println("  --history-start <num>: show only history data starting at given time offset.");
    paramPrintWriter.println("  --charged: only output data since last charged.");
    paramPrintWriter.println("  --daily: only output full daily data.");
    paramPrintWriter.println("  --reset: reset the stats, clearing all current data.");
    paramPrintWriter.println("  --write: force write current collected stats to disk.");
    paramPrintWriter.println("  --new-daily: immediately create and write new daily stats record.");
    paramPrintWriter.println("  --read-daily: read-load last written daily stats.");
    paramPrintWriter.println("  <package.name>: optional name of package to filter output by.");
    paramPrintWriter.println("  -h: print this help text.");
    paramPrintWriter.println("Battery stats (batterystats) commands:");
    paramPrintWriter.println("  enable|disable <option>");
    paramPrintWriter.println("    Enable or disable a running option.  Option state is not saved across boots.");
    paramPrintWriter.println("    Options are:");
    paramPrintWriter.println("      full-history: include additional detailed events in battery history:");
    paramPrintWriter.println("          wake_lock_in, alarms and proc events");
    paramPrintWriter.println("      no-auto-reset: don't automatically reset stats when unplugged");
  }
  
  private WifiActivityEnergyInfo extractDelta(WifiActivityEnergyInfo paramWifiActivityEnergyInfo)
  {
    long l8 = paramWifiActivityEnergyInfo.mTimestamp - this.mLastInfo.mTimestamp;
    long l3 = this.mLastInfo.mControllerIdleTimeMs;
    long l9 = this.mLastInfo.mControllerTxTimeMs;
    long l10 = this.mLastInfo.mControllerRxTimeMs;
    long l4 = this.mLastInfo.mControllerEnergyUsed;
    WifiActivityEnergyInfo localWifiActivityEnergyInfo = this.mLastInfo;
    localWifiActivityEnergyInfo.mTimestamp = paramWifiActivityEnergyInfo.getTimeStamp();
    localWifiActivityEnergyInfo.mStackState = paramWifiActivityEnergyInfo.getStackState();
    long l5 = paramWifiActivityEnergyInfo.mControllerTxTimeMs - l9;
    long l6 = paramWifiActivityEnergyInfo.mControllerRxTimeMs - l10;
    long l7 = paramWifiActivityEnergyInfo.mControllerIdleTimeMs;
    if ((l5 < 0L) || (l6 < 0L))
    {
      localWifiActivityEnergyInfo.mControllerEnergyUsed = paramWifiActivityEnergyInfo.mControllerEnergyUsed;
      localWifiActivityEnergyInfo.mControllerRxTimeMs = paramWifiActivityEnergyInfo.mControllerRxTimeMs;
      localWifiActivityEnergyInfo.mControllerTxTimeMs = paramWifiActivityEnergyInfo.mControllerTxTimeMs;
      localWifiActivityEnergyInfo.mControllerIdleTimeMs = paramWifiActivityEnergyInfo.mControllerIdleTimeMs;
      Slog.v("BatteryStatsService", "WiFi energy data was reset, new WiFi energy data is " + localWifiActivityEnergyInfo);
      this.mLastInfo = paramWifiActivityEnergyInfo;
      return localWifiActivityEnergyInfo;
    }
    long l11 = l5 + l6;
    long l2;
    if (l11 > l8)
    {
      l2 = 0L;
      l1 = l2;
      if (l11 > 750L + l8)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Total Active time ");
        TimeUtils.formatDuration(l11, localStringBuilder);
        localStringBuilder.append(" is longer than sample period ");
        TimeUtils.formatDuration(l8, localStringBuilder);
        localStringBuilder.append(".\n");
        localStringBuilder.append("Previous WiFi snapshot: ").append("idle=");
        TimeUtils.formatDuration(l3, localStringBuilder);
        localStringBuilder.append(" rx=");
        TimeUtils.formatDuration(l10, localStringBuilder);
        localStringBuilder.append(" tx=");
        TimeUtils.formatDuration(l9, localStringBuilder);
        localStringBuilder.append(" e=").append(l4);
        localStringBuilder.append("\n");
        localStringBuilder.append("Current WiFi snapshot: ").append("idle=");
        TimeUtils.formatDuration(paramWifiActivityEnergyInfo.mControllerIdleTimeMs, localStringBuilder);
        localStringBuilder.append(" rx=");
        TimeUtils.formatDuration(paramWifiActivityEnergyInfo.mControllerRxTimeMs, localStringBuilder);
        localStringBuilder.append(" tx=");
        TimeUtils.formatDuration(paramWifiActivityEnergyInfo.mControllerTxTimeMs, localStringBuilder);
        localStringBuilder.append(" e=").append(paramWifiActivityEnergyInfo.mControllerEnergyUsed);
        Slog.wtf("BatteryStatsService", localStringBuilder.toString());
      }
    }
    for (long l1 = l2;; l1 = l8 - l11)
    {
      localWifiActivityEnergyInfo.mControllerTxTimeMs = l5;
      localWifiActivityEnergyInfo.mControllerRxTimeMs = l6;
      localWifiActivityEnergyInfo.mControllerIdleTimeMs = Math.min(l1, Math.max(0L, l7 - l3));
      localWifiActivityEnergyInfo.mControllerEnergyUsed = Math.max(0L, paramWifiActivityEnergyInfo.mControllerEnergyUsed - l4);
      break;
    }
  }
  
  private native int getPlatformLowPowerStats(ByteBuffer paramByteBuffer);
  
  public static IBatteryStats getService()
  {
    if (sService != null) {
      return sService;
    }
    sService = asInterface(ServiceManager.getService("batterystats"));
    return sService;
  }
  
  private static native int nativeWaitWakeup(ByteBuffer paramByteBuffer);
  
  private static boolean onlyCaller(int[] paramArrayOfInt)
  {
    int j = Binder.getCallingUid();
    int k = paramArrayOfInt.length;
    int i = 0;
    while (i < k)
    {
      if (paramArrayOfInt[i] != j) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  void addIsolatedUid(int paramInt1, int paramInt2)
  {
    synchronized (this.mStats)
    {
      this.mStats.addIsolatedUidLocked(paramInt1, paramInt2);
      return;
    }
  }
  
  public long computeBatteryTimeRemaining()
  {
    synchronized (this.mStats)
    {
      long l2 = this.mStats.computeBatteryTimeRemaining(SystemClock.elapsedRealtime());
      long l1 = l2;
      if (l2 >= 0L) {
        l1 = l2 / 1000L;
      }
      return l1;
    }
  }
  
  public long computeChargeTimeRemaining()
  {
    synchronized (this.mStats)
    {
      long l2 = this.mStats.computeChargeTimeRemaining(SystemClock.elapsedRealtime());
      long l1 = l2;
      if (l2 >= 0L) {
        l1 = l2 / 1000L;
      }
      return l1;
    }
  }
  
  /* Error */
  protected void dump(java.io.FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] arg3)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 417	com/android/server/am/BatteryStatsService:mContext	Landroid/content/Context;
    //   4: ldc_w 419
    //   7: invokevirtual 425	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   10: ifeq +54 -> 64
    //   13: aload_2
    //   14: new 197	java/lang/StringBuilder
    //   17: dup
    //   18: invokespecial 198	java/lang/StringBuilder:<init>	()V
    //   21: ldc_w 427
    //   24: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   27: invokestatic 430	android/os/Binder:getCallingPid	()I
    //   30: invokevirtual 433	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   33: ldc_w 435
    //   36: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   39: invokestatic 388	android/os/Binder:getCallingUid	()I
    //   42: invokevirtual 433	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   45: ldc_w 437
    //   48: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: ldc_w 419
    //   54: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   60: invokevirtual 216	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   63: return
    //   64: iconst_0
    //   65: istore 11
    //   67: iconst_0
    //   68: istore 4
    //   70: iconst_0
    //   71: istore 14
    //   73: iconst_0
    //   74: istore 8
    //   76: iconst_0
    //   77: istore 13
    //   79: iconst_0
    //   80: istore 7
    //   82: iconst_0
    //   83: istore 16
    //   85: iconst_0
    //   86: istore 5
    //   88: iconst_0
    //   89: istore 15
    //   91: iconst_0
    //   92: istore 6
    //   94: ldc2_w 438
    //   97: lstore 17
    //   99: iconst_m1
    //   100: istore 9
    //   102: lload 17
    //   104: lstore 19
    //   106: iload 9
    //   108: istore 12
    //   110: aload_3
    //   111: ifnull +665 -> 776
    //   114: iconst_0
    //   115: istore 10
    //   117: iload 4
    //   119: istore 11
    //   121: lload 17
    //   123: lstore 19
    //   125: iload 9
    //   127: istore 12
    //   129: iload 7
    //   131: istore 13
    //   133: iload 5
    //   135: istore 16
    //   137: iload 8
    //   139: istore 14
    //   141: iload 6
    //   143: istore 15
    //   145: iload 10
    //   147: aload_3
    //   148: arraylength
    //   149: if_icmpge +627 -> 776
    //   152: aload_3
    //   153: iload 10
    //   155: aaload
    //   156: astore_1
    //   157: ldc_w 441
    //   160: aload_1
    //   161: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   164: ifeq +18 -> 182
    //   167: iconst_1
    //   168: istore 8
    //   170: iconst_1
    //   171: istore 7
    //   173: iload 10
    //   175: iconst_1
    //   176: iadd
    //   177: istore 10
    //   179: goto -62 -> 117
    //   182: ldc_w 443
    //   185: aload_1
    //   186: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   189: ifeq +13 -> 202
    //   192: iload 4
    //   194: bipush 8
    //   196: ior
    //   197: istore 4
    //   199: goto -26 -> 173
    //   202: ldc_w 445
    //   205: aload_1
    //   206: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   209: ifeq +51 -> 260
    //   212: iload 4
    //   214: bipush 8
    //   216: ior
    //   217: istore 4
    //   219: iload 10
    //   221: iconst_1
    //   222: iadd
    //   223: istore 10
    //   225: iload 10
    //   227: aload_3
    //   228: arraylength
    //   229: if_icmplt +16 -> 245
    //   232: aload_2
    //   233: ldc_w 447
    //   236: invokevirtual 216	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   239: aload_0
    //   240: aload_2
    //   241: invokespecial 220	com/android/server/am/BatteryStatsService:dumpHelp	(Ljava/io/PrintWriter;)V
    //   244: return
    //   245: aload_3
    //   246: iload 10
    //   248: aaload
    //   249: invokestatic 453	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   252: lstore 17
    //   254: iconst_1
    //   255: istore 6
    //   257: goto -84 -> 173
    //   260: ldc_w 455
    //   263: aload_1
    //   264: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   267: ifeq +16 -> 283
    //   270: iconst_1
    //   271: istore 8
    //   273: iload 4
    //   275: bipush 16
    //   277: ior
    //   278: istore 4
    //   280: goto -107 -> 173
    //   283: ldc_w 457
    //   286: aload_1
    //   287: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   290: ifeq +12 -> 302
    //   293: iload 4
    //   295: iconst_2
    //   296: ior
    //   297: istore 4
    //   299: goto -126 -> 173
    //   302: ldc_w 459
    //   305: aload_1
    //   306: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   309: ifeq +12 -> 321
    //   312: iload 4
    //   314: iconst_4
    //   315: ior
    //   316: istore 4
    //   318: goto -145 -> 173
    //   321: ldc_w 461
    //   324: aload_1
    //   325: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   328: ifeq +46 -> 374
    //   331: aload_0
    //   332: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   335: astore_1
    //   336: aload_1
    //   337: monitorenter
    //   338: aload_0
    //   339: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   342: invokevirtual 464	com/android/internal/os/BatteryStatsImpl:resetAllStatsCmdLocked	()V
    //   345: aload_2
    //   346: ldc_w 466
    //   349: invokevirtual 216	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   352: iconst_1
    //   353: istore 5
    //   355: aload_1
    //   356: monitorexit
    //   357: aload_0
    //   358: ldc_w 467
    //   361: bipush 15
    //   363: invokevirtual 471	com/android/server/am/BatteryStatsService:updateExternalStatsSync	(Ljava/lang/String;I)V
    //   366: goto -193 -> 173
    //   369: astore_2
    //   370: aload_1
    //   371: monitorexit
    //   372: aload_2
    //   373: athrow
    //   374: ldc_w 473
    //   377: aload_1
    //   378: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   381: ifeq +46 -> 427
    //   384: aload_0
    //   385: ldc_w 467
    //   388: bipush 15
    //   390: invokevirtual 471	com/android/server/am/BatteryStatsService:updateExternalStatsSync	(Ljava/lang/String;I)V
    //   393: aload_0
    //   394: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   397: astore_1
    //   398: aload_1
    //   399: monitorenter
    //   400: aload_0
    //   401: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   404: invokevirtual 476	com/android/internal/os/BatteryStatsImpl:writeSyncLocked	()V
    //   407: aload_2
    //   408: ldc_w 478
    //   411: invokevirtual 216	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   414: iconst_1
    //   415: istore 5
    //   417: aload_1
    //   418: monitorexit
    //   419: goto -246 -> 173
    //   422: astore_2
    //   423: aload_1
    //   424: monitorexit
    //   425: aload_2
    //   426: athrow
    //   427: ldc_w 480
    //   430: aload_1
    //   431: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   434: ifeq +37 -> 471
    //   437: aload_0
    //   438: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   441: astore_1
    //   442: aload_1
    //   443: monitorenter
    //   444: aload_0
    //   445: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   448: invokevirtual 483	com/android/internal/os/BatteryStatsImpl:recordDailyStatsLocked	()V
    //   451: aload_2
    //   452: ldc_w 485
    //   455: invokevirtual 216	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   458: iconst_1
    //   459: istore 5
    //   461: aload_1
    //   462: monitorexit
    //   463: goto -290 -> 173
    //   466: astore_2
    //   467: aload_1
    //   468: monitorexit
    //   469: aload_2
    //   470: athrow
    //   471: ldc_w 487
    //   474: aload_1
    //   475: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   478: ifeq +37 -> 515
    //   481: aload_0
    //   482: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   485: astore_1
    //   486: aload_1
    //   487: monitorenter
    //   488: aload_0
    //   489: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   492: invokevirtual 490	com/android/internal/os/BatteryStatsImpl:readDailyStatsLocked	()V
    //   495: aload_2
    //   496: ldc_w 492
    //   499: invokevirtual 216	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   502: iconst_1
    //   503: istore 5
    //   505: aload_1
    //   506: monitorexit
    //   507: goto -334 -> 173
    //   510: astore_2
    //   511: aload_1
    //   512: monitorexit
    //   513: aload_2
    //   514: athrow
    //   515: ldc -50
    //   517: aload_1
    //   518: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   521: ifne +13 -> 534
    //   524: ldc_w 494
    //   527: aload_1
    //   528: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   531: ifeq +48 -> 579
    //   534: aload_0
    //   535: aload_2
    //   536: iload 10
    //   538: aload_3
    //   539: iconst_1
    //   540: invokespecial 496	com/android/server/am/BatteryStatsService:doEnableOrDisable	(Ljava/io/PrintWriter;I[Ljava/lang/String;Z)I
    //   543: istore 4
    //   545: iload 4
    //   547: ifge +4 -> 551
    //   550: return
    //   551: aload_2
    //   552: new 197	java/lang/StringBuilder
    //   555: dup
    //   556: invokespecial 198	java/lang/StringBuilder:<init>	()V
    //   559: ldc_w 498
    //   562: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   565: aload_3
    //   566: iload 4
    //   568: aaload
    //   569: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   572: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   575: invokevirtual 216	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   578: return
    //   579: ldc -34
    //   581: aload_1
    //   582: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   585: ifne +13 -> 598
    //   588: ldc_w 500
    //   591: aload_1
    //   592: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   595: ifeq +48 -> 643
    //   598: aload_0
    //   599: aload_2
    //   600: iload 10
    //   602: aload_3
    //   603: iconst_0
    //   604: invokespecial 496	com/android/server/am/BatteryStatsService:doEnableOrDisable	(Ljava/io/PrintWriter;I[Ljava/lang/String;Z)I
    //   607: istore 4
    //   609: iload 4
    //   611: ifge +4 -> 615
    //   614: return
    //   615: aload_2
    //   616: new 197	java/lang/StringBuilder
    //   619: dup
    //   620: invokespecial 198	java/lang/StringBuilder:<init>	()V
    //   623: ldc_w 502
    //   626: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   629: aload_3
    //   630: iload 4
    //   632: aaload
    //   633: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   636: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   639: invokevirtual 216	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   642: return
    //   643: ldc_w 504
    //   646: aload_1
    //   647: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   650: ifeq +9 -> 659
    //   653: aload_0
    //   654: aload_2
    //   655: invokespecial 220	com/android/server/am/BatteryStatsService:dumpHelp	(Ljava/io/PrintWriter;)V
    //   658: return
    //   659: ldc_w 506
    //   662: aload_1
    //   663: invokevirtual 230	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   666: ifeq +13 -> 679
    //   669: iload 4
    //   671: bipush 32
    //   673: ior
    //   674: istore 4
    //   676: goto -503 -> 173
    //   679: aload_1
    //   680: invokevirtual 509	java/lang/String:length	()I
    //   683: ifle +43 -> 726
    //   686: aload_1
    //   687: iconst_0
    //   688: invokevirtual 513	java/lang/String:charAt	(I)C
    //   691: bipush 45
    //   693: if_icmpne +33 -> 726
    //   696: aload_2
    //   697: new 197	java/lang/StringBuilder
    //   700: dup
    //   701: invokespecial 198	java/lang/StringBuilder:<init>	()V
    //   704: ldc_w 515
    //   707: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   710: aload_1
    //   711: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   714: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   717: invokevirtual 216	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   720: aload_0
    //   721: aload_2
    //   722: invokespecial 220	com/android/server/am/BatteryStatsService:dumpHelp	(Ljava/io/PrintWriter;)V
    //   725: return
    //   726: aload_0
    //   727: getfield 417	com/android/server/am/BatteryStatsService:mContext	Landroid/content/Context;
    //   730: invokevirtual 519	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   733: aload_1
    //   734: invokestatic 524	android/os/UserHandle:getCallingUserId	()I
    //   737: invokevirtual 530	android/content/pm/PackageManager:getPackageUidAsUser	(Ljava/lang/String;I)I
    //   740: istore 9
    //   742: goto -569 -> 173
    //   745: astore_3
    //   746: aload_2
    //   747: new 197	java/lang/StringBuilder
    //   750: dup
    //   751: invokespecial 198	java/lang/StringBuilder:<init>	()V
    //   754: ldc_w 532
    //   757: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   760: aload_1
    //   761: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   764: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   767: invokevirtual 216	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   770: aload_0
    //   771: aload_2
    //   772: invokespecial 220	com/android/server/am/BatteryStatsService:dumpHelp	(Ljava/io/PrintWriter;)V
    //   775: return
    //   776: iload 16
    //   778: ifeq +4 -> 782
    //   781: return
    //   782: invokestatic 535	android/os/Binder:clearCallingIdentity	()J
    //   785: lstore 17
    //   787: iload 11
    //   789: istore 4
    //   791: aload_0
    //   792: getfield 417	com/android/server/am/BatteryStatsService:mContext	Landroid/content/Context;
    //   795: invokestatic 541	com/android/internal/os/BatteryStatsHelper:checkWifiOnly	(Landroid/content/Context;)Z
    //   798: ifeq +10 -> 808
    //   801: iload 11
    //   803: bipush 64
    //   805: ior
    //   806: istore 4
    //   808: aload_0
    //   809: ldc_w 467
    //   812: bipush 15
    //   814: invokevirtual 471	com/android/server/am/BatteryStatsService:updateExternalStatsSync	(Ljava/lang/String;I)V
    //   817: lload 17
    //   819: invokestatic 545	android/os/Binder:restoreCallingIdentity	(J)V
    //   822: iload 4
    //   824: istore 5
    //   826: iload 12
    //   828: iflt +24 -> 852
    //   831: iload 4
    //   833: istore 5
    //   835: iload 4
    //   837: bipush 10
    //   839: iand
    //   840: ifne +12 -> 852
    //   843: iload 4
    //   845: iconst_2
    //   846: ior
    //   847: bipush -17
    //   849: iand
    //   850: istore 5
    //   852: iload 14
    //   854: ifeq +248 -> 1102
    //   857: aload_0
    //   858: getfield 417	com/android/server/am/BatteryStatsService:mContext	Landroid/content/Context;
    //   861: invokevirtual 519	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   864: ldc_w 546
    //   867: invokevirtual 550	android/content/pm/PackageManager:getInstalledApplications	(I)Ljava/util/List;
    //   870: astore_1
    //   871: iload 13
    //   873: ifeq +176 -> 1049
    //   876: aload_0
    //   877: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   880: getfield 554	com/android/internal/os/BatteryStatsImpl:mCheckinFile	Lcom/android/internal/os/AtomicFile;
    //   883: astore_3
    //   884: aload_3
    //   885: monitorenter
    //   886: aload_0
    //   887: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   890: getfield 554	com/android/internal/os/BatteryStatsImpl:mCheckinFile	Lcom/android/internal/os/AtomicFile;
    //   893: invokevirtual 560	com/android/internal/os/AtomicFile:exists	()Z
    //   896: istore 21
    //   898: iload 21
    //   900: ifeq +147 -> 1047
    //   903: aload_0
    //   904: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   907: getfield 554	com/android/internal/os/BatteryStatsImpl:mCheckinFile	Lcom/android/internal/os/AtomicFile;
    //   910: invokevirtual 564	com/android/internal/os/AtomicFile:readFully	()[B
    //   913: astore 23
    //   915: aload 23
    //   917: ifnull +130 -> 1047
    //   920: invokestatic 570	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   923: astore 22
    //   925: aload 22
    //   927: aload 23
    //   929: iconst_0
    //   930: aload 23
    //   932: arraylength
    //   933: invokevirtual 574	android/os/Parcel:unmarshall	([BII)V
    //   936: aload 22
    //   938: iconst_0
    //   939: invokevirtual 578	android/os/Parcel:setDataPosition	(I)V
    //   942: new 149	com/android/internal/os/BatteryStatsImpl
    //   945: dup
    //   946: aconst_null
    //   947: aload_0
    //   948: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   951: getfield 581	com/android/internal/os/BatteryStatsImpl:mHandler	Lcom/android/internal/os/BatteryStatsImpl$MyHandler;
    //   954: aconst_null
    //   955: invokespecial 584	com/android/internal/os/BatteryStatsImpl:<init>	(Ljava/io/File;Landroid/os/Handler;Lcom/android/internal/os/BatteryStatsImpl$ExternalStatsSync;)V
    //   958: astore 23
    //   960: aload 23
    //   962: aload 22
    //   964: invokevirtual 588	com/android/internal/os/BatteryStatsImpl:readSummaryFromParcel	(Landroid/os/Parcel;)V
    //   967: aload 22
    //   969: invokevirtual 591	android/os/Parcel:recycle	()V
    //   972: aload 23
    //   974: aload_0
    //   975: getfield 417	com/android/server/am/BatteryStatsService:mContext	Landroid/content/Context;
    //   978: aload_2
    //   979: aload_1
    //   980: iload 5
    //   982: lload 19
    //   984: invokevirtual 595	com/android/internal/os/BatteryStatsImpl:dumpCheckinLocked	(Landroid/content/Context;Ljava/io/PrintWriter;Ljava/util/List;IJ)V
    //   987: aload_0
    //   988: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   991: getfield 554	com/android/internal/os/BatteryStatsImpl:mCheckinFile	Lcom/android/internal/os/AtomicFile;
    //   994: invokevirtual 598	com/android/internal/os/AtomicFile:delete	()V
    //   997: aload_3
    //   998: monitorexit
    //   999: return
    //   1000: astore_1
    //   1001: lload 17
    //   1003: invokestatic 545	android/os/Binder:restoreCallingIdentity	(J)V
    //   1006: aload_1
    //   1007: athrow
    //   1008: astore 22
    //   1010: ldc 30
    //   1012: new 197	java/lang/StringBuilder
    //   1015: dup
    //   1016: invokespecial 198	java/lang/StringBuilder:<init>	()V
    //   1019: ldc_w 600
    //   1022: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1025: aload_0
    //   1026: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   1029: getfield 554	com/android/internal/os/BatteryStatsImpl:mCheckinFile	Lcom/android/internal/os/AtomicFile;
    //   1032: invokevirtual 604	com/android/internal/os/AtomicFile:getBaseFile	()Ljava/io/File;
    //   1035: invokevirtual 321	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1038: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1041: aload 22
    //   1043: invokestatic 608	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   1046: pop
    //   1047: aload_3
    //   1048: monitorexit
    //   1049: aload_0
    //   1050: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   1053: astore_3
    //   1054: aload_3
    //   1055: monitorenter
    //   1056: aload_0
    //   1057: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   1060: aload_0
    //   1061: getfield 417	com/android/server/am/BatteryStatsService:mContext	Landroid/content/Context;
    //   1064: aload_2
    //   1065: aload_1
    //   1066: iload 5
    //   1068: lload 19
    //   1070: invokevirtual 595	com/android/internal/os/BatteryStatsImpl:dumpCheckinLocked	(Landroid/content/Context;Ljava/io/PrintWriter;Ljava/util/List;IJ)V
    //   1073: aload_3
    //   1074: astore_1
    //   1075: iload 15
    //   1077: ifeq +12 -> 1089
    //   1080: aload_0
    //   1081: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   1084: invokevirtual 611	com/android/internal/os/BatteryStatsImpl:writeAsyncLocked	()V
    //   1087: aload_3
    //   1088: astore_1
    //   1089: aload_1
    //   1090: monitorexit
    //   1091: return
    //   1092: astore_1
    //   1093: aload_3
    //   1094: monitorexit
    //   1095: aload_1
    //   1096: athrow
    //   1097: astore_1
    //   1098: aload_3
    //   1099: monitorexit
    //   1100: aload_1
    //   1101: athrow
    //   1102: aload_0
    //   1103: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   1106: astore_3
    //   1107: aload_3
    //   1108: monitorenter
    //   1109: aload_0
    //   1110: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   1113: aload_0
    //   1114: getfield 417	com/android/server/am/BatteryStatsService:mContext	Landroid/content/Context;
    //   1117: aload_2
    //   1118: iload 5
    //   1120: iload 12
    //   1122: lload 19
    //   1124: invokevirtual 615	com/android/internal/os/BatteryStatsImpl:dumpLocked	(Landroid/content/Context;Ljava/io/PrintWriter;IIJ)V
    //   1127: aload_3
    //   1128: astore_1
    //   1129: iload 15
    //   1131: ifeq -42 -> 1089
    //   1134: aload_0
    //   1135: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   1138: invokevirtual 611	com/android/internal/os/BatteryStatsImpl:writeAsyncLocked	()V
    //   1141: aload_3
    //   1142: astore_1
    //   1143: goto -54 -> 1089
    //   1146: astore_1
    //   1147: aload_3
    //   1148: monitorexit
    //   1149: aload_1
    //   1150: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1151	0	this	BatteryStatsService
    //   0	1151	1	paramFileDescriptor	java.io.FileDescriptor
    //   0	1151	2	paramPrintWriter	PrintWriter
    //   68	779	4	i	int
    //   86	1033	5	j	int
    //   92	164	6	k	int
    //   80	92	7	m	int
    //   74	198	8	n	int
    //   100	641	9	i1	int
    //   115	486	10	i2	int
    //   65	741	11	i3	int
    //   108	1013	12	i4	int
    //   77	795	13	i5	int
    //   71	782	14	i6	int
    //   89	1041	15	i7	int
    //   83	694	16	i8	int
    //   97	905	17	l1	long
    //   104	1019	19	l2	long
    //   896	3	21	bool	boolean
    //   923	45	22	localParcel	Parcel
    //   1008	34	22	localIOException	IOException
    //   913	60	23	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   338	352	369	finally
    //   400	414	422	finally
    //   444	458	466	finally
    //   488	502	510	finally
    //   726	742	745	android/content/pm/PackageManager$NameNotFoundException
    //   791	801	1000	finally
    //   808	817	1000	finally
    //   903	915	1008	java/io/IOException
    //   903	915	1008	android/os/ParcelFormatException
    //   920	997	1008	java/io/IOException
    //   920	997	1008	android/os/ParcelFormatException
    //   886	898	1092	finally
    //   903	915	1092	finally
    //   920	997	1092	finally
    //   1010	1047	1092	finally
    //   1056	1073	1097	finally
    //   1080	1087	1097	finally
    //   1109	1127	1146	finally
    //   1134	1141	1146	finally
  }
  
  public void enforceCallingPermission()
  {
    if (Binder.getCallingPid() == Process.myPid()) {
      return;
    }
    this.mContext.enforcePermission("android.permission.UPDATE_DEVICE_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null);
  }
  
  public BatteryStatsImpl getActiveStatistics()
  {
    return this.mStats;
  }
  
  public long getAwakeTimeBattery()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.BATTERY_STATS", null);
    return this.mStats.getAwakeTimeBattery();
  }
  
  public long getAwakeTimePlugged()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.BATTERY_STATS", null);
    return this.mStats.getAwakeTimePlugged();
  }
  
  HealthStatsParceler getHealthStatsForUidLocked(int paramInt)
  {
    HealthStatsBatteryStatsWriter localHealthStatsBatteryStatsWriter = new HealthStatsBatteryStatsWriter();
    HealthStatsWriter localHealthStatsWriter = new HealthStatsWriter(UidHealthStats.CONSTANTS);
    BatteryStats.Uid localUid = (BatteryStats.Uid)this.mStats.getUidStats().get(paramInt);
    if (localUid != null) {
      localHealthStatsBatteryStatsWriter.writeUid(localHealthStatsWriter, this.mStats, localUid);
    }
    return new HealthStatsParceler(localHealthStatsWriter);
  }
  
  public String getPlatformLowPowerStats()
  {
    this.mUtf8BufferStat.clear();
    this.mUtf16BufferStat.clear();
    this.mDecoderStat.reset();
    int i = getPlatformLowPowerStats(this.mUtf8BufferStat);
    if (i < 0) {
      return null;
    }
    if (i == 0) {
      return "Empty";
    }
    this.mUtf8BufferStat.limit(i);
    this.mDecoderStat.decode(this.mUtf8BufferStat, this.mUtf16BufferStat, true);
    this.mUtf16BufferStat.flip();
    return this.mUtf16BufferStat.toString();
  }
  
  public byte[] getStatistics()
  {
    this.mContext.enforceCallingPermission("android.permission.BATTERY_STATS", null);
    Parcel localParcel = Parcel.obtain();
    updateExternalStatsSync("get-stats", 15);
    synchronized (this.mStats)
    {
      this.mStats.writeToParcel(localParcel, 0);
      ??? = localParcel.marshall();
      localParcel.recycle();
      return (byte[])???;
    }
  }
  
  public ParcelFileDescriptor getStatisticsStream()
  {
    this.mContext.enforceCallingPermission("android.permission.BATTERY_STATS", null);
    Object localObject1 = Parcel.obtain();
    updateExternalStatsSync("get-stats", 15);
    synchronized (this.mStats)
    {
      this.mStats.writeToParcel((Parcel)localObject1, 0);
      ??? = ((Parcel)localObject1).marshall();
      ((Parcel)localObject1).recycle();
    }
    return null;
  }
  
  public void initPowerManagement()
  {
    PowerManagerInternal localPowerManagerInternal = (PowerManagerInternal)LocalServices.getService(PowerManagerInternal.class);
    localPowerManagerInternal.registerLowPowerModeObserver(this);
    this.mStats.notePowerSaveMode(localPowerManagerInternal.getLowPowerModeEnabled());
    new WakeupReasonThread().start();
  }
  
  public boolean isCharging()
  {
    synchronized (this.mStats)
    {
      boolean bool = this.mStats.isCharging();
      return bool;
    }
  }
  
  public boolean isOnBattery()
  {
    return this.mStats.isOnBattery();
  }
  
  public void noteAlarmFinish(String paramString, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteAlarmFinishLocked(paramString, paramInt);
      return;
    }
  }
  
  public void noteAlarmStart(String paramString, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteAlarmStartLocked(paramString, paramInt);
      return;
    }
  }
  
  public void noteBleScanStarted(WorkSource paramWorkSource)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteBluetoothScanStartedFromSourceLocked(paramWorkSource);
      return;
    }
  }
  
  public void noteBleScanStopped(WorkSource paramWorkSource)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteBluetoothScanStoppedFromSourceLocked(paramWorkSource);
      return;
    }
  }
  
  public void noteBluetoothControllerActivity(BluetoothActivityEnergyInfo paramBluetoothActivityEnergyInfo)
  {
    enforceCallingPermission();
    if ((paramBluetoothActivityEnergyInfo != null) && (paramBluetoothActivityEnergyInfo.isValid())) {}
    synchronized (this.mStats)
    {
      this.mStats.updateBluetoothStateLocked(paramBluetoothActivityEnergyInfo);
      return;
      Slog.e("BatteryStatsService", "invalid bluetooth data given: " + paramBluetoothActivityEnergyInfo);
      return;
    }
  }
  
  public void noteChangeWakelockFromSource(WorkSource paramWorkSource1, int paramInt1, String paramString1, String paramString2, int paramInt2, WorkSource paramWorkSource2, int paramInt3, String paramString3, String paramString4, int paramInt4, boolean paramBoolean)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteChangeWakelockFromSourceLocked(paramWorkSource1, paramInt1, paramString1, paramString2, paramInt2, paramWorkSource2, paramInt3, paramString3, paramString4, paramInt4, paramBoolean);
      return;
    }
  }
  
  public void noteConnectivityChanged(int paramInt, String paramString)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteConnectivityChangedLocked(paramInt, paramString);
      return;
    }
  }
  
  public void noteDeviceIdleMode(int paramInt1, String paramString, int paramInt2)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteDeviceIdleModeLocked(paramInt1, paramString, paramInt2);
      return;
    }
  }
  
  public void noteEvent(int paramInt1, String paramString, int paramInt2)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteEventLocked(paramInt1, paramString, paramInt2);
      return;
    }
  }
  
  public void noteFlashlightOff(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteFlashlightOffLocked(paramInt);
      return;
    }
  }
  
  public void noteFlashlightOn(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteFlashlightOnLocked(paramInt);
      return;
    }
  }
  
  public void noteFullWifiLockAcquired(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteFullWifiLockAcquiredLocked(paramInt);
      return;
    }
  }
  
  public void noteFullWifiLockAcquiredFromSource(WorkSource paramWorkSource)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteFullWifiLockAcquiredFromSourceLocked(paramWorkSource);
      return;
    }
  }
  
  public void noteFullWifiLockReleased(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteFullWifiLockReleasedLocked(paramInt);
      return;
    }
  }
  
  public void noteFullWifiLockReleasedFromSource(WorkSource paramWorkSource)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteFullWifiLockReleasedFromSourceLocked(paramWorkSource);
      return;
    }
  }
  
  public void noteInteractive(boolean paramBoolean)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteInteractiveLocked(paramBoolean);
      return;
    }
  }
  
  public void noteJobFinish(String paramString, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteJobFinishLocked(paramString, paramInt);
      return;
    }
  }
  
  public void noteJobStart(String paramString, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteJobStartLocked(paramString, paramInt);
      return;
    }
  }
  
  public void noteLongPartialWakelockFinish(String paramString1, String paramString2, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteLongPartialWakelockFinish(paramString1, paramString2, paramInt);
      return;
    }
  }
  
  public void noteLongPartialWakelockStart(String paramString1, String paramString2, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteLongPartialWakelockStart(paramString1, paramString2, paramInt);
      return;
    }
  }
  
  public void noteMobileRadioPowerState(int paramInt1, long paramLong, int paramInt2)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteMobileRadioPowerState(paramInt1, paramLong, paramInt2);
      return;
    }
  }
  
  public void noteModemControllerActivity(ModemActivityInfo paramModemActivityInfo)
  {
    enforceCallingPermission();
    if ((paramModemActivityInfo != null) && (paramModemActivityInfo.isValid())) {}
    synchronized (this.mStats)
    {
      this.mStats.updateMobileRadioStateLocked(SystemClock.elapsedRealtime(), paramModemActivityInfo);
      return;
      Slog.e("BatteryStatsService", "invalid modem data given: " + paramModemActivityInfo);
      return;
    }
  }
  
  public void noteNetworkInterfaceType(String paramString, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteNetworkInterfaceTypeLocked(paramString, paramInt);
      return;
    }
  }
  
  public void noteNetworkStatsEnabled()
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteNetworkStatsEnabledLocked();
      return;
    }
  }
  
  public void notePackageInstalled(String paramString, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.notePackageInstalledLocked(paramString, paramInt);
      return;
    }
  }
  
  public void notePackageUninstalled(String paramString)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.notePackageUninstalledLocked(paramString);
      return;
    }
  }
  
  public void notePhoneDataConnectionState(int paramInt, boolean paramBoolean)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.notePhoneDataConnectionStateLocked(paramInt, paramBoolean);
      return;
    }
  }
  
  public void notePhoneOff()
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.notePhoneOffLocked();
      return;
    }
  }
  
  public void notePhoneOn()
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.notePhoneOnLocked();
      return;
    }
  }
  
  public void notePhoneSignalStrength(SignalStrength paramSignalStrength)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.notePhoneSignalStrengthLocked(paramSignalStrength);
      return;
    }
  }
  
  public void notePhoneState(int paramInt)
  {
    enforceCallingPermission();
    int i = TelephonyManager.getDefault().getSimState();
    synchronized (this.mStats)
    {
      this.mStats.notePhoneStateLocked(paramInt, i);
      return;
    }
  }
  
  void noteProcessAnr(String paramString, int paramInt)
  {
    synchronized (this.mStats)
    {
      this.mStats.noteProcessAnrLocked(paramString, paramInt);
      return;
    }
  }
  
  void noteProcessCrash(String paramString, int paramInt)
  {
    synchronized (this.mStats)
    {
      this.mStats.noteProcessCrashLocked(paramString, paramInt);
      return;
    }
  }
  
  void noteProcessFinish(String paramString, int paramInt)
  {
    synchronized (this.mStats)
    {
      this.mStats.noteProcessFinishLocked(paramString, paramInt);
      return;
    }
  }
  
  void noteProcessStart(String paramString, int paramInt)
  {
    synchronized (this.mStats)
    {
      this.mStats.noteProcessStartLocked(paramString, paramInt);
      return;
    }
  }
  
  public void noteResetAudio()
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteResetAudioLocked();
      return;
    }
  }
  
  public void noteResetBleScan()
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteResetBluetoothScanLocked();
      return;
    }
  }
  
  public void noteResetCamera()
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteResetCameraLocked();
      return;
    }
  }
  
  public void noteResetFlashlight()
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteResetFlashlightLocked();
      return;
    }
  }
  
  public void noteResetVideo()
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteResetVideoLocked();
      return;
    }
  }
  
  public void noteScreenBrightness(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteScreenBrightnessLocked(paramInt);
      return;
    }
  }
  
  public void noteScreenState(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteScreenStateLocked(paramInt);
      return;
    }
  }
  
  public void noteStartAudio(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteAudioOnLocked(paramInt);
      return;
    }
  }
  
  public void noteStartCamera(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteCameraOnLocked(paramInt);
      return;
    }
  }
  
  public void noteStartGps(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteStartGpsLocked(paramInt);
      return;
    }
  }
  
  public void noteStartSensor(int paramInt1, int paramInt2)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteStartSensorLocked(paramInt1, paramInt2);
      return;
    }
  }
  
  public void noteStartVideo(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteVideoOnLocked(paramInt);
      return;
    }
  }
  
  public void noteStartWakelock(int paramInt1, int paramInt2, String paramString1, String paramString2, int paramInt3, boolean paramBoolean)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteStartWakeLocked(paramInt1, paramInt2, paramString1, paramString2, paramInt3, paramBoolean, SystemClock.elapsedRealtime(), SystemClock.uptimeMillis());
      return;
    }
  }
  
  public void noteStartWakelockFromSource(WorkSource paramWorkSource, int paramInt1, String paramString1, String paramString2, int paramInt2, boolean paramBoolean)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteStartWakeFromSourceLocked(paramWorkSource, paramInt1, paramString1, paramString2, paramInt2, paramBoolean);
      return;
    }
  }
  
  public void noteStopAudio(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteAudioOffLocked(paramInt);
      return;
    }
  }
  
  public void noteStopCamera(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteCameraOffLocked(paramInt);
      return;
    }
  }
  
  public void noteStopGps(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteStopGpsLocked(paramInt);
      return;
    }
  }
  
  public void noteStopSensor(int paramInt1, int paramInt2)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteStopSensorLocked(paramInt1, paramInt2);
      return;
    }
  }
  
  public void noteStopVideo(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteVideoOffLocked(paramInt);
      return;
    }
  }
  
  public void noteStopWakelock(int paramInt1, int paramInt2, String paramString1, String paramString2, int paramInt3)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteStopWakeLocked(paramInt1, paramInt2, paramString1, paramString2, paramInt3, SystemClock.elapsedRealtime(), SystemClock.uptimeMillis());
      return;
    }
  }
  
  public void noteStopWakelockFromSource(WorkSource paramWorkSource, int paramInt1, String paramString1, String paramString2, int paramInt2)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteStopWakeFromSourceLocked(paramWorkSource, paramInt1, paramString1, paramString2, paramInt2);
      return;
    }
  }
  
  public void noteSyncFinish(String paramString, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteSyncFinishLocked(paramString, paramInt);
      return;
    }
  }
  
  public void noteSyncStart(String paramString, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteSyncStartLocked(paramString, paramInt);
      return;
    }
  }
  
  void noteUidProcessState(int paramInt1, int paramInt2)
  {
    synchronized (this.mStats)
    {
      this.mStats.noteUidProcessStateLocked(paramInt1, paramInt2);
      return;
    }
  }
  
  public void noteUserActivity(int paramInt1, int paramInt2)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteUserActivityLocked(paramInt1, paramInt2);
      return;
    }
  }
  
  public void noteVibratorOff(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteVibratorOffLocked(paramInt);
      return;
    }
  }
  
  public void noteVibratorOn(int paramInt, long paramLong)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteVibratorOnLocked(paramInt, paramLong);
      return;
    }
  }
  
  public void noteWakeUp(String paramString, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWakeUpLocked(paramString, paramInt);
      return;
    }
  }
  
  public void noteWifiBatchedScanStartedFromSource(WorkSource paramWorkSource, int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiBatchedScanStartedFromSourceLocked(paramWorkSource, paramInt);
      return;
    }
  }
  
  public void noteWifiBatchedScanStoppedFromSource(WorkSource paramWorkSource)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiBatchedScanStoppedFromSourceLocked(paramWorkSource);
      return;
    }
  }
  
  public void noteWifiControllerActivity(WifiActivityEnergyInfo paramWifiActivityEnergyInfo)
  {
    enforceCallingPermission();
    if ((paramWifiActivityEnergyInfo != null) && (paramWifiActivityEnergyInfo.isValid())) {}
    synchronized (this.mStats)
    {
      this.mStats.updateWifiStateLocked(paramWifiActivityEnergyInfo);
      return;
      Slog.e("BatteryStatsService", "invalid wifi data given: " + paramWifiActivityEnergyInfo);
      return;
    }
  }
  
  public void noteWifiMulticastDisabled(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiMulticastDisabledLocked(paramInt);
      return;
    }
  }
  
  public void noteWifiMulticastDisabledFromSource(WorkSource paramWorkSource)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiMulticastDisabledFromSourceLocked(paramWorkSource);
      return;
    }
  }
  
  public void noteWifiMulticastEnabled(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiMulticastEnabledLocked(paramInt);
      return;
    }
  }
  
  public void noteWifiMulticastEnabledFromSource(WorkSource paramWorkSource)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiMulticastEnabledFromSourceLocked(paramWorkSource);
      return;
    }
  }
  
  public void noteWifiOff()
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiOffLocked();
      return;
    }
  }
  
  public void noteWifiOn()
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiOnLocked();
      return;
    }
  }
  
  public void noteWifiRadioPowerState(int paramInt1, long paramLong, int paramInt2)
  {
    enforceCallingPermission();
    for (;;)
    {
      synchronized (this.mStats)
      {
        if (this.mStats.isOnBattery())
        {
          if (paramInt1 == 3) {
            break label96;
          }
          if (paramInt1 == 2)
          {
            break label96;
            this.mHandler.scheduleSync("wifi-data: " + str1, 2);
          }
        }
        else
        {
          this.mStats.noteWifiRadioPowerState(paramInt1, paramLong, paramInt2);
          return;
        }
        String str1 = "inactive";
      }
      label96:
      String str2 = "active";
    }
  }
  
  public void noteWifiRssiChanged(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiRssiChangedLocked(paramInt);
      return;
    }
  }
  
  public void noteWifiRunning(WorkSource paramWorkSource)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiRunningLocked(paramWorkSource);
      return;
    }
  }
  
  public void noteWifiRunningChanged(WorkSource paramWorkSource1, WorkSource paramWorkSource2)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiRunningChangedLocked(paramWorkSource1, paramWorkSource2);
      return;
    }
  }
  
  public void noteWifiScanStarted(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiScanStartedLocked(paramInt);
      return;
    }
  }
  
  public void noteWifiScanStartedFromSource(WorkSource paramWorkSource)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiScanStartedFromSourceLocked(paramWorkSource);
      return;
    }
  }
  
  public void noteWifiScanStopped(int paramInt)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiScanStoppedLocked(paramInt);
      return;
    }
  }
  
  public void noteWifiScanStoppedFromSource(WorkSource paramWorkSource)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiScanStoppedFromSourceLocked(paramWorkSource);
      return;
    }
  }
  
  public void noteWifiState(int paramInt, String paramString)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiStateLocked(paramInt, paramString);
      return;
    }
  }
  
  public void noteWifiStopped(WorkSource paramWorkSource)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiStoppedLocked(paramWorkSource);
      return;
    }
  }
  
  public void noteWifiSupplicantStateChanged(int paramInt, boolean paramBoolean)
  {
    enforceCallingPermission();
    synchronized (this.mStats)
    {
      this.mStats.noteWifiSupplicantStateChangedLocked(paramInt, paramBoolean);
      return;
    }
  }
  
  public void onLowPowerModeChanged(boolean paramBoolean)
  {
    synchronized (this.mStats)
    {
      this.mStats.notePowerSaveMode(paramBoolean);
      return;
    }
  }
  
  public void publish(Context paramContext)
  {
    this.mContext = paramContext;
    this.mStats.setRadioScanningTimeout(this.mContext.getResources().getInteger(17694734) * 1000L);
    this.mStats.setPowerProfile(new PowerProfile(paramContext));
    ServiceManager.addService("batterystats", asBinder());
  }
  
  void removeIsolatedUid(int paramInt1, int paramInt2)
  {
    synchronized (this.mStats)
    {
      this.mStats.scheduleRemoveIsolatedUidLocked(paramInt1, paramInt2);
      return;
    }
  }
  
  void removeUid(int paramInt)
  {
    synchronized (this.mStats)
    {
      this.mStats.removeUidStatsLocked(paramInt);
      return;
    }
  }
  
  public void scheduleWriteToDisk()
  {
    this.mHandler.sendEmptyMessage(2);
  }
  
  public void setBatteryState(final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4, final int paramInt5, final int paramInt6, final int paramInt7)
  {
    enforceCallingPermission();
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        synchronized (BatteryStatsService.this.mStats)
        {
          if (paramInt3 == 0) {}
          for (int i = 1; BatteryStatsService.this.mStats.isOnBattery() == i; i = 0)
          {
            BatteryStatsService.this.mStats.setBatteryStateLocked(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
            return;
          }
          BatteryStatsService.this.updateExternalStatsSync("battery-state", 15);
        }
        synchronized (BatteryStatsService.this.mStats)
        {
          BatteryStatsService.this.mStats.setBatteryStateLocked(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
          return;
          localObject1 = finally;
          throw ((Throwable)localObject1);
        }
      }
    });
  }
  
  public void shutdown()
  {
    Slog.w("BatteryStats", "Writing battery stats before shutdown...");
    updateExternalStatsSync("shutdown", 15);
    synchronized (this.mStats)
    {
      this.mStats.shutdownLocked();
      this.mHandler.getLooper().quit();
      return;
    }
  }
  
  /* Error */
  public HealthStatsParceler takeUidSnapshot(int paramInt)
  {
    // Byte code:
    //   0: iload_1
    //   1: invokestatic 388	android/os/Binder:getCallingUid	()I
    //   4: if_icmpeq +14 -> 18
    //   7: aload_0
    //   8: getfield 417	com/android/server/am/BatteryStatsService:mContext	Landroid/content/Context;
    //   11: ldc_w 632
    //   14: aconst_null
    //   15: invokevirtual 636	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   18: invokestatic 535	android/os/Binder:clearCallingIdentity	()J
    //   21: lstore_2
    //   22: aload_0
    //   23: ldc_w 1218
    //   26: bipush 15
    //   28: invokevirtual 471	com/android/server/am/BatteryStatsService:updateExternalStatsSync	(Ljava/lang/String;I)V
    //   31: aload_0
    //   32: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   35: astore 4
    //   37: aload 4
    //   39: monitorenter
    //   40: aload_0
    //   41: iload_1
    //   42: invokevirtual 1220	com/android/server/am/BatteryStatsService:getHealthStatsForUidLocked	(I)Landroid/os/health/HealthStatsParceler;
    //   45: astore 5
    //   47: aload 4
    //   49: monitorexit
    //   50: lload_2
    //   51: invokestatic 545	android/os/Binder:restoreCallingIdentity	(J)V
    //   54: aload 5
    //   56: areturn
    //   57: astore 5
    //   59: aload 4
    //   61: monitorexit
    //   62: aload 5
    //   64: athrow
    //   65: astore 4
    //   67: ldc 30
    //   69: new 197	java/lang/StringBuilder
    //   72: dup
    //   73: invokespecial 198	java/lang/StringBuilder:<init>	()V
    //   76: ldc_w 1222
    //   79: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   82: iload_1
    //   83: invokevirtual 433	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   86: ldc_w 1224
    //   89: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   92: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   95: aload 4
    //   97: invokestatic 1227	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   100: pop
    //   101: aload 4
    //   103: athrow
    //   104: astore 4
    //   106: lload_2
    //   107: invokestatic 545	android/os/Binder:restoreCallingIdentity	(J)V
    //   110: aload 4
    //   112: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	113	0	this	BatteryStatsService
    //   0	113	1	paramInt	int
    //   21	86	2	l	long
    //   65	37	4	localException	Exception
    //   104	7	4	localObject1	Object
    //   45	10	5	localHealthStatsParceler	HealthStatsParceler
    //   57	6	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   40	47	57	finally
    //   22	40	65	java/lang/Exception
    //   47	50	65	java/lang/Exception
    //   59	65	65	java/lang/Exception
    //   22	40	104	finally
    //   47	50	104	finally
    //   59	65	104	finally
    //   67	104	104	finally
  }
  
  /* Error */
  public HealthStatsParceler[] takeUidSnapshots(int[] paramArrayOfInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 1231	com/android/server/am/BatteryStatsService:onlyCaller	([I)Z
    //   4: ifne +14 -> 18
    //   7: aload_0
    //   8: getfield 417	com/android/server/am/BatteryStatsService:mContext	Landroid/content/Context;
    //   11: ldc_w 632
    //   14: aconst_null
    //   15: invokevirtual 636	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   18: invokestatic 535	android/os/Binder:clearCallingIdentity	()J
    //   21: lstore 6
    //   23: iconst_m1
    //   24: istore 4
    //   26: iload 4
    //   28: istore_2
    //   29: aload_0
    //   30: ldc_w 1233
    //   33: bipush 15
    //   35: invokevirtual 471	com/android/server/am/BatteryStatsService:updateExternalStatsSync	(Ljava/lang/String;I)V
    //   38: iload 4
    //   40: istore_2
    //   41: aload_0
    //   42: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   45: astore 8
    //   47: iload 4
    //   49: istore_2
    //   50: aload 8
    //   52: monitorenter
    //   53: iload 4
    //   55: istore_3
    //   56: aload_1
    //   57: arraylength
    //   58: istore 5
    //   60: iload 4
    //   62: istore_3
    //   63: iload 5
    //   65: anewarray 675	android/os/health/HealthStatsParceler
    //   68: astore 9
    //   70: iconst_0
    //   71: istore_2
    //   72: iload_2
    //   73: iload 5
    //   75: if_icmpge +23 -> 98
    //   78: iload_2
    //   79: istore_3
    //   80: aload 9
    //   82: iload_2
    //   83: aload_0
    //   84: aload_1
    //   85: iload_2
    //   86: iaload
    //   87: invokevirtual 1220	com/android/server/am/BatteryStatsService:getHealthStatsForUidLocked	(I)Landroid/os/health/HealthStatsParceler;
    //   90: aastore
    //   91: iload_2
    //   92: iconst_1
    //   93: iadd
    //   94: istore_2
    //   95: goto -23 -> 72
    //   98: aload 8
    //   100: monitorexit
    //   101: lload 6
    //   103: invokestatic 545	android/os/Binder:restoreCallingIdentity	(J)V
    //   106: aload 9
    //   108: areturn
    //   109: astore 9
    //   111: iload_3
    //   112: istore_2
    //   113: aload 8
    //   115: monitorexit
    //   116: iload_3
    //   117: istore_2
    //   118: aload 9
    //   120: athrow
    //   121: astore 8
    //   123: ldc 30
    //   125: new 197	java/lang/StringBuilder
    //   128: dup
    //   129: invokespecial 198	java/lang/StringBuilder:<init>	()V
    //   132: ldc_w 1235
    //   135: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: aload_1
    //   139: invokestatic 1240	java/util/Arrays:toString	([I)Ljava/lang/String;
    //   142: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   145: ldc_w 1242
    //   148: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: iload_2
    //   152: invokevirtual 433	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   155: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   158: aload 8
    //   160: invokestatic 1227	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   163: pop
    //   164: aload 8
    //   166: athrow
    //   167: astore_1
    //   168: lload 6
    //   170: invokestatic 545	android/os/Binder:restoreCallingIdentity	(J)V
    //   173: aload_1
    //   174: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	175	0	this	BatteryStatsService
    //   0	175	1	paramArrayOfInt	int[]
    //   28	124	2	i	int
    //   55	62	3	j	int
    //   24	37	4	k	int
    //   58	18	5	m	int
    //   21	148	6	l	long
    //   45	69	8	localBatteryStatsImpl	BatteryStatsImpl
    //   121	44	8	localException	Exception
    //   68	39	9	arrayOfHealthStatsParceler	HealthStatsParceler[]
    //   109	10	9	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   56	60	109	finally
    //   63	70	109	finally
    //   80	91	109	finally
    //   29	38	121	java/lang/Exception
    //   41	47	121	java/lang/Exception
    //   50	53	121	java/lang/Exception
    //   98	101	121	java/lang/Exception
    //   113	116	121	java/lang/Exception
    //   118	121	121	java/lang/Exception
    //   29	38	167	finally
    //   41	47	167	finally
    //   50	53	167	finally
    //   98	101	167	finally
    //   113	116	167	finally
    //   118	121	167	finally
    //   123	167	167	finally
  }
  
  /* Error */
  void updateExternalStatsSync(String paramString, int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: aconst_null
    //   7: astore 6
    //   9: aload_0
    //   10: getfield 70	com/android/server/am/BatteryStatsService:mExternalStatsLock	Ljava/lang/Object;
    //   13: astore 9
    //   15: aload 9
    //   17: monitorenter
    //   18: aload_0
    //   19: getfield 417	com/android/server/am/BatteryStatsService:mContext	Landroid/content/Context;
    //   22: astore_3
    //   23: aload_3
    //   24: ifnonnull +7 -> 31
    //   27: aload 9
    //   29: monitorexit
    //   30: return
    //   31: aload 4
    //   33: astore_3
    //   34: iload_2
    //   35: iconst_2
    //   36: iand
    //   37: ifeq +55 -> 92
    //   40: aload_0
    //   41: getfield 1246	com/android/server/am/BatteryStatsService:mWifiManager	Landroid/net/wifi/IWifiManager;
    //   44: ifnonnull +16 -> 60
    //   47: aload_0
    //   48: ldc_w 1248
    //   51: invokestatic 377	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   54: invokestatic 1253	android/net/wifi/IWifiManager$Stub:asInterface	(Landroid/os/IBinder;)Landroid/net/wifi/IWifiManager;
    //   57: putfield 1246	com/android/server/am/BatteryStatsService:mWifiManager	Landroid/net/wifi/IWifiManager;
    //   60: aload_0
    //   61: getfield 1246	com/android/server/am/BatteryStatsService:mWifiManager	Landroid/net/wifi/IWifiManager;
    //   64: astore 7
    //   66: aload 4
    //   68: astore_3
    //   69: aload 7
    //   71: ifnull +21 -> 92
    //   74: new 160	android/os/SynchronousResultReceiver
    //   77: dup
    //   78: invokespecial 1254	android/os/SynchronousResultReceiver:<init>	()V
    //   81: astore_3
    //   82: aload_0
    //   83: getfield 1246	com/android/server/am/BatteryStatsService:mWifiManager	Landroid/net/wifi/IWifiManager;
    //   86: aload_3
    //   87: invokeinterface 1260 2 0
    //   92: aload 5
    //   94: astore 4
    //   96: iload_2
    //   97: bipush 8
    //   99: iand
    //   100: ifeq +33 -> 133
    //   103: invokestatic 1266	android/bluetooth/BluetoothAdapter:getDefaultAdapter	()Landroid/bluetooth/BluetoothAdapter;
    //   106: astore 7
    //   108: aload 5
    //   110: astore 4
    //   112: aload 7
    //   114: ifnull +19 -> 133
    //   117: new 160	android/os/SynchronousResultReceiver
    //   120: dup
    //   121: invokespecial 1254	android/os/SynchronousResultReceiver:<init>	()V
    //   124: astore 4
    //   126: aload 7
    //   128: aload 4
    //   130: invokevirtual 1269	android/bluetooth/BluetoothAdapter:requestControllerActivityEnergyInfo	(Landroid/os/ResultReceiver;)V
    //   133: aload 6
    //   135: astore 5
    //   137: iload_2
    //   138: iconst_4
    //   139: iand
    //   140: ifeq +50 -> 190
    //   143: aload_0
    //   144: getfield 1271	com/android/server/am/BatteryStatsService:mTelephony	Landroid/telephony/TelephonyManager;
    //   147: ifnonnull +14 -> 161
    //   150: aload_0
    //   151: aload_0
    //   152: getfield 417	com/android/server/am/BatteryStatsService:mContext	Landroid/content/Context;
    //   155: invokestatic 1275	android/telephony/TelephonyManager:from	(Landroid/content/Context;)Landroid/telephony/TelephonyManager;
    //   158: putfield 1271	com/android/server/am/BatteryStatsService:mTelephony	Landroid/telephony/TelephonyManager;
    //   161: aload 6
    //   163: astore 5
    //   165: aload_0
    //   166: getfield 1271	com/android/server/am/BatteryStatsService:mTelephony	Landroid/telephony/TelephonyManager;
    //   169: ifnull +21 -> 190
    //   172: new 160	android/os/SynchronousResultReceiver
    //   175: dup
    //   176: invokespecial 1254	android/os/SynchronousResultReceiver:<init>	()V
    //   179: astore 5
    //   181: aload_0
    //   182: getfield 1271	com/android/server/am/BatteryStatsService:mTelephony	Landroid/telephony/TelephonyManager;
    //   185: aload 5
    //   187: invokevirtual 1278	android/telephony/TelephonyManager:requestModemActivityInfo	(Landroid/os/ResultReceiver;)V
    //   190: aconst_null
    //   191: astore 8
    //   193: aconst_null
    //   194: astore 7
    //   196: aconst_null
    //   197: astore 6
    //   199: aload_3
    //   200: invokestatic 1280	com/android/server/am/BatteryStatsService:awaitControllerInfo	(Landroid/os/SynchronousResultReceiver;)Landroid/os/Parcelable;
    //   203: checkcast 72	android/net/wifi/WifiActivityEnergyInfo
    //   206: astore_3
    //   207: aload 4
    //   209: invokestatic 1280	com/android/server/am/BatteryStatsService:awaitControllerInfo	(Landroid/os/SynchronousResultReceiver;)Landroid/os/Parcelable;
    //   212: checkcast 777	android/bluetooth/BluetoothActivityEnergyInfo
    //   215: astore 4
    //   217: aload 5
    //   219: invokestatic 1280	com/android/server/am/BatteryStatsService:awaitControllerInfo	(Landroid/os/SynchronousResultReceiver;)Landroid/os/Parcelable;
    //   222: checkcast 855	android/telephony/ModemActivityInfo
    //   225: astore 5
    //   227: aload_0
    //   228: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   231: astore 6
    //   233: aload 6
    //   235: monitorenter
    //   236: aload_0
    //   237: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   240: invokestatic 399	android/os/SystemClock:elapsedRealtime	()J
    //   243: invokestatic 978	android/os/SystemClock:uptimeMillis	()J
    //   246: bipush 14
    //   248: aload_1
    //   249: iconst_0
    //   250: invokevirtual 1284	com/android/internal/os/BatteryStatsImpl:addHistoryEventLocked	(JJILjava/lang/String;I)V
    //   253: aload_0
    //   254: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   257: invokevirtual 1287	com/android/internal/os/BatteryStatsImpl:updateCpuTimeLocked	()V
    //   260: aload_0
    //   261: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   264: invokevirtual 1290	com/android/internal/os/BatteryStatsImpl:updateKernelWakelocksLocked	()V
    //   267: aload_3
    //   268: ifnull +22 -> 290
    //   271: aload_3
    //   272: invokevirtual 1059	android/net/wifi/WifiActivityEnergyInfo:isValid	()Z
    //   275: ifeq +127 -> 402
    //   278: aload_0
    //   279: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   282: aload_0
    //   283: aload_3
    //   284: invokespecial 1292	com/android/server/am/BatteryStatsService:extractDelta	(Landroid/net/wifi/WifiActivityEnergyInfo;)Landroid/net/wifi/WifiActivityEnergyInfo;
    //   287: invokevirtual 1062	com/android/internal/os/BatteryStatsImpl:updateWifiStateLocked	(Landroid/net/wifi/WifiActivityEnergyInfo;)V
    //   290: aload 4
    //   292: ifnull +20 -> 312
    //   295: aload 4
    //   297: invokevirtual 780	android/bluetooth/BluetoothActivityEnergyInfo:isValid	()Z
    //   300: ifeq +137 -> 437
    //   303: aload_0
    //   304: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   307: aload 4
    //   309: invokevirtual 783	com/android/internal/os/BatteryStatsImpl:updateBluetoothStateLocked	(Landroid/bluetooth/BluetoothActivityEnergyInfo;)V
    //   312: aload 5
    //   314: ifnull +23 -> 337
    //   317: aload 5
    //   319: invokevirtual 856	android/telephony/ModemActivityInfo:isValid	()Z
    //   322: ifeq +145 -> 467
    //   325: aload_0
    //   326: getfield 154	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
    //   329: invokestatic 399	android/os/SystemClock:elapsedRealtime	()J
    //   332: aload 5
    //   334: invokevirtual 860	com/android/internal/os/BatteryStatsImpl:updateMobileRadioStateLocked	(JLandroid/telephony/ModemActivityInfo;)V
    //   337: aload 6
    //   339: monitorexit
    //   340: aload 9
    //   342: monitorexit
    //   343: return
    //   344: astore_3
    //   345: ldc 30
    //   347: ldc_w 1294
    //   350: invokestatic 1203	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   353: pop
    //   354: aload 8
    //   356: astore_3
    //   357: goto -150 -> 207
    //   360: astore_1
    //   361: aload 9
    //   363: monitorexit
    //   364: aload_1
    //   365: athrow
    //   366: astore 4
    //   368: ldc 30
    //   370: ldc_w 1296
    //   373: invokestatic 1203	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   376: pop
    //   377: aload 7
    //   379: astore 4
    //   381: goto -164 -> 217
    //   384: astore 5
    //   386: ldc 30
    //   388: ldc_w 1298
    //   391: invokestatic 1203	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   394: pop
    //   395: aload 6
    //   397: astore 5
    //   399: goto -172 -> 227
    //   402: ldc 30
    //   404: new 197	java/lang/StringBuilder
    //   407: dup
    //   408: invokespecial 198	java/lang/StringBuilder:<init>	()V
    //   411: ldc_w 1300
    //   414: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   417: aload_3
    //   418: invokevirtual 321	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   421: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   424: invokestatic 190	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   427: pop
    //   428: goto -138 -> 290
    //   431: astore_1
    //   432: aload 6
    //   434: monitorexit
    //   435: aload_1
    //   436: athrow
    //   437: ldc 30
    //   439: new 197	java/lang/StringBuilder
    //   442: dup
    //   443: invokespecial 198	java/lang/StringBuilder:<init>	()V
    //   446: ldc_w 1302
    //   449: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   452: aload 4
    //   454: invokevirtual 321	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   457: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   460: invokestatic 190	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   463: pop
    //   464: goto -152 -> 312
    //   467: ldc 30
    //   469: new 197	java/lang/StringBuilder
    //   472: dup
    //   473: invokespecial 198	java/lang/StringBuilder:<init>	()V
    //   476: ldc_w 1304
    //   479: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   482: aload 5
    //   484: invokevirtual 321	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   487: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   490: invokestatic 190	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   493: pop
    //   494: goto -157 -> 337
    //   497: astore_1
    //   498: goto -137 -> 361
    //   501: astore_1
    //   502: goto -141 -> 361
    //   505: astore_1
    //   506: goto -145 -> 361
    //   509: astore_3
    //   510: aload 4
    //   512: astore_3
    //   513: goto -421 -> 92
    //   516: astore 4
    //   518: goto -426 -> 92
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	521	0	this	BatteryStatsService
    //   0	521	1	paramString	String
    //   0	521	2	paramInt	int
    //   22	262	3	localObject1	Object
    //   344	1	3	localTimeoutException1	TimeoutException
    //   356	62	3	localObject2	Object
    //   509	1	3	localRemoteException1	android.os.RemoteException
    //   512	1	3	localObject3	Object
    //   1	307	4	localObject4	Object
    //   366	1	4	localTimeoutException2	TimeoutException
    //   379	132	4	localObject5	Object
    //   516	1	4	localRemoteException2	android.os.RemoteException
    //   4	329	5	localObject6	Object
    //   384	1	5	localTimeoutException3	TimeoutException
    //   397	86	5	localObject7	Object
    //   64	314	7	localObject8	Object
    //   191	164	8	localObject9	Object
    //   13	349	9	localObject10	Object
    // Exception table:
    //   from	to	target	type
    //   199	207	344	java/util/concurrent/TimeoutException
    //   18	23	360	finally
    //   40	60	360	finally
    //   60	66	360	finally
    //   74	82	360	finally
    //   103	108	360	finally
    //   117	126	360	finally
    //   143	161	360	finally
    //   165	181	360	finally
    //   199	207	360	finally
    //   207	217	360	finally
    //   217	227	360	finally
    //   227	236	360	finally
    //   337	340	360	finally
    //   345	354	360	finally
    //   368	377	360	finally
    //   386	395	360	finally
    //   432	437	360	finally
    //   207	217	366	java/util/concurrent/TimeoutException
    //   217	227	384	java/util/concurrent/TimeoutException
    //   236	267	431	finally
    //   271	290	431	finally
    //   295	312	431	finally
    //   317	337	431	finally
    //   402	428	431	finally
    //   437	464	431	finally
    //   467	494	431	finally
    //   82	92	497	finally
    //   126	133	501	finally
    //   181	190	505	finally
    //   74	82	509	android/os/RemoteException
    //   82	92	516	android/os/RemoteException
  }
  
  class BatteryStatsHandler
    extends Handler
    implements BatteryStatsImpl.ExternalStatsSync
  {
    public static final int MSG_SYNC_EXTERNAL_STATS = 1;
    public static final int MSG_WRITE_TO_DISK = 2;
    private IntArray mUidsToRemove = new IntArray();
    private int mUpdateFlags = 0;
    
    public BatteryStatsHandler(Looper paramLooper)
    {
      super();
    }
    
    private void scheduleSyncLocked(String paramString, int paramInt)
    {
      if (this.mUpdateFlags == 0) {
        sendMessage(Message.obtain(this, 1, paramString));
      }
      this.mUpdateFlags |= paramInt;
    }
    
    /* Error */
    public void handleMessage(Message arg1)
    {
      // Byte code:
      //   0: aload_1
      //   1: getfield 53	android/os/Message:what	I
      //   4: tableswitch	default:+24->28, 1:+25->29, 2:+142->146
      //   28: return
      //   29: aload_0
      //   30: monitorenter
      //   31: aload_0
      //   32: iconst_1
      //   33: invokevirtual 57	com/android/server/am/BatteryStatsService$BatteryStatsHandler:removeMessages	(I)V
      //   36: aload_0
      //   37: getfield 28	com/android/server/am/BatteryStatsService$BatteryStatsHandler:mUpdateFlags	I
      //   40: istore_2
      //   41: aload_0
      //   42: iconst_0
      //   43: putfield 28	com/android/server/am/BatteryStatsService$BatteryStatsHandler:mUpdateFlags	I
      //   46: aload_0
      //   47: monitorexit
      //   48: aload_0
      //   49: getfield 23	com/android/server/am/BatteryStatsService$BatteryStatsHandler:this$0	Lcom/android/server/am/BatteryStatsService;
      //   52: aload_1
      //   53: getfield 61	android/os/Message:obj	Ljava/lang/Object;
      //   56: checkcast 63	java/lang/String
      //   59: iload_2
      //   60: invokevirtual 66	com/android/server/am/BatteryStatsService:updateExternalStatsSync	(Ljava/lang/String;I)V
      //   63: aload_0
      //   64: getfield 23	com/android/server/am/BatteryStatsService$BatteryStatsHandler:this$0	Lcom/android/server/am/BatteryStatsService;
      //   67: getfield 70	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
      //   70: astore_1
      //   71: aload_1
      //   72: monitorenter
      //   73: aload_0
      //   74: monitorenter
      //   75: aload_0
      //   76: getfield 35	com/android/server/am/BatteryStatsService$BatteryStatsHandler:mUidsToRemove	Landroid/util/IntArray;
      //   79: invokevirtual 74	android/util/IntArray:size	()I
      //   82: istore_3
      //   83: iconst_0
      //   84: istore_2
      //   85: iload_2
      //   86: iload_3
      //   87: if_icmpge +33 -> 120
      //   90: aload_0
      //   91: getfield 23	com/android/server/am/BatteryStatsService$BatteryStatsHandler:this$0	Lcom/android/server/am/BatteryStatsService;
      //   94: getfield 70	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
      //   97: aload_0
      //   98: getfield 35	com/android/server/am/BatteryStatsService$BatteryStatsHandler:mUidsToRemove	Landroid/util/IntArray;
      //   101: iload_2
      //   102: invokevirtual 78	android/util/IntArray:get	(I)I
      //   105: invokevirtual 83	com/android/internal/os/BatteryStatsImpl:removeIsolatedUidLocked	(I)V
      //   108: iload_2
      //   109: iconst_1
      //   110: iadd
      //   111: istore_2
      //   112: goto -27 -> 85
      //   115: astore_1
      //   116: aload_0
      //   117: monitorexit
      //   118: aload_1
      //   119: athrow
      //   120: aload_0
      //   121: monitorexit
      //   122: aload_0
      //   123: getfield 35	com/android/server/am/BatteryStatsService$BatteryStatsHandler:mUidsToRemove	Landroid/util/IntArray;
      //   126: invokevirtual 86	android/util/IntArray:clear	()V
      //   129: aload_1
      //   130: monitorexit
      //   131: return
      //   132: astore 4
      //   134: aload_0
      //   135: monitorexit
      //   136: aload 4
      //   138: athrow
      //   139: astore 4
      //   141: aload_1
      //   142: monitorexit
      //   143: aload 4
      //   145: athrow
      //   146: aload_0
      //   147: getfield 23	com/android/server/am/BatteryStatsService$BatteryStatsHandler:this$0	Lcom/android/server/am/BatteryStatsService;
      //   150: ldc 88
      //   152: bipush 15
      //   154: invokevirtual 66	com/android/server/am/BatteryStatsService:updateExternalStatsSync	(Ljava/lang/String;I)V
      //   157: aload_0
      //   158: getfield 23	com/android/server/am/BatteryStatsService$BatteryStatsHandler:this$0	Lcom/android/server/am/BatteryStatsService;
      //   161: getfield 70	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
      //   164: astore_1
      //   165: aload_1
      //   166: monitorenter
      //   167: aload_0
      //   168: getfield 23	com/android/server/am/BatteryStatsService$BatteryStatsHandler:this$0	Lcom/android/server/am/BatteryStatsService;
      //   171: getfield 70	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
      //   174: invokevirtual 91	com/android/internal/os/BatteryStatsImpl:writeAsyncLocked	()V
      //   177: goto -48 -> 129
      //   180: astore 4
      //   182: aload_1
      //   183: monitorexit
      //   184: aload 4
      //   186: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	187	0	this	BatteryStatsHandler
      //   40	72	2	i	int
      //   82	6	3	j	int
      //   132	5	4	localObject1	Object
      //   139	5	4	localObject2	Object
      //   180	5	4	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   31	46	115	finally
      //   75	83	132	finally
      //   90	108	132	finally
      //   73	75	139	finally
      //   120	129	139	finally
      //   134	139	139	finally
      //   167	177	180	finally
    }
    
    public void scheduleCpuSyncDueToRemovedUid(int paramInt)
    {
      try
      {
        scheduleSyncLocked("remove-uid", 1);
        this.mUidsToRemove.add(paramInt);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void scheduleSync(String paramString, int paramInt)
    {
      try
      {
        scheduleSyncLocked(paramString, paramInt);
        return;
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
  }
  
  final class WakeupReasonThread
    extends Thread
  {
    private static final int MAX_REASON_SIZE = 512;
    private CharsetDecoder mDecoder;
    private CharBuffer mUtf16Buffer;
    private ByteBuffer mUtf8Buffer;
    
    WakeupReasonThread()
    {
      super();
    }
    
    private String waitWakeup()
    {
      this.mUtf8Buffer.clear();
      this.mUtf16Buffer.clear();
      this.mDecoder.reset();
      int i = BatteryStatsService.-wrap0(this.mUtf8Buffer);
      if (i < 0) {
        return null;
      }
      if (i == 0) {
        return "unknown";
      }
      this.mUtf8Buffer.limit(i);
      this.mDecoder.decode(this.mUtf8Buffer, this.mUtf16Buffer, true);
      this.mUtf16Buffer.flip();
      return this.mUtf16Buffer.toString();
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: bipush -2
      //   2: invokestatic 81	android/os/Process:setThreadPriority	(I)V
      //   5: aload_0
      //   6: getstatic 87	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
      //   9: invokevirtual 92	java/nio/charset/Charset:newDecoder	()Ljava/nio/charset/CharsetDecoder;
      //   12: getstatic 98	java/nio/charset/CodingErrorAction:REPLACE	Ljava/nio/charset/CodingErrorAction;
      //   15: invokevirtual 102	java/nio/charset/CharsetDecoder:onMalformedInput	(Ljava/nio/charset/CodingErrorAction;)Ljava/nio/charset/CharsetDecoder;
      //   18: getstatic 98	java/nio/charset/CodingErrorAction:REPLACE	Ljava/nio/charset/CodingErrorAction;
      //   21: invokevirtual 105	java/nio/charset/CharsetDecoder:onUnmappableCharacter	(Ljava/nio/charset/CodingErrorAction;)Ljava/nio/charset/CharsetDecoder;
      //   24: ldc 107
      //   26: invokevirtual 111	java/nio/charset/CharsetDecoder:replaceWith	(Ljava/lang/String;)Ljava/nio/charset/CharsetDecoder;
      //   29: putfield 45	com/android/server/am/BatteryStatsService$WakeupReasonThread:mDecoder	Ljava/nio/charset/CharsetDecoder;
      //   32: aload_0
      //   33: sipush 512
      //   36: invokestatic 115	java/nio/ByteBuffer:allocateDirect	(I)Ljava/nio/ByteBuffer;
      //   39: putfield 32	com/android/server/am/BatteryStatsService$WakeupReasonThread:mUtf8Buffer	Ljava/nio/ByteBuffer;
      //   42: aload_0
      //   43: sipush 512
      //   46: invokestatic 119	java/nio/CharBuffer:allocate	(I)Ljava/nio/CharBuffer;
      //   49: putfield 40	com/android/server/am/BatteryStatsService$WakeupReasonThread:mUtf16Buffer	Ljava/nio/CharBuffer;
      //   52: aload_0
      //   53: invokespecial 121	com/android/server/am/BatteryStatsService$WakeupReasonThread:waitWakeup	()Ljava/lang/String;
      //   56: astore_2
      //   57: aload_2
      //   58: ifnull +39 -> 97
      //   61: aload_0
      //   62: getfield 22	com/android/server/am/BatteryStatsService$WakeupReasonThread:this$0	Lcom/android/server/am/BatteryStatsService;
      //   65: getfield 125	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
      //   68: astore_1
      //   69: aload_1
      //   70: monitorenter
      //   71: aload_0
      //   72: getfield 22	com/android/server/am/BatteryStatsService$WakeupReasonThread:this$0	Lcom/android/server/am/BatteryStatsService;
      //   75: getfield 125	com/android/server/am/BatteryStatsService:mStats	Lcom/android/internal/os/BatteryStatsImpl;
      //   78: aload_2
      //   79: invokevirtual 130	com/android/internal/os/BatteryStatsImpl:noteWakeupReasonLocked	(Ljava/lang/String;)V
      //   82: aload_1
      //   83: monitorexit
      //   84: goto -32 -> 52
      //   87: astore_1
      //   88: ldc -124
      //   90: ldc -122
      //   92: aload_1
      //   93: invokestatic 140	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   96: pop
      //   97: return
      //   98: astore_2
      //   99: aload_1
      //   100: monitorexit
      //   101: aload_2
      //   102: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	103	0	this	WakeupReasonThread
      //   87	13	1	localRuntimeException	RuntimeException
      //   56	23	2	str	String
      //   98	4	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   52	57	87	java/lang/RuntimeException
      //   61	71	87	java/lang/RuntimeException
      //   82	84	87	java/lang/RuntimeException
      //   99	103	87	java/lang/RuntimeException
      //   71	82	98	finally
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/BatteryStatsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */