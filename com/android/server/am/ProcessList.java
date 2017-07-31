package com.android.server.am;

import android.content.res.Resources;
import android.graphics.Point;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.LocalSocketAddress.Namespace;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.internal.util.MemInfoReader;
import com.android.server.wm.WindowManagerService;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class ProcessList
{
  static final int BACKUP_APP_ADJ = 300;
  static final int CACHED_APP_MAX_ADJ = 906;
  static final int CACHED_APP_MIN_ADJ = 900;
  static final int EMPTY_APP_PERCENT;
  static final int FOREGROUND_APP_ADJ = 0;
  static final int HEAVY_WEIGHT_APP_ADJ = 400;
  static final int HOME_APP_ADJ = 600;
  static final int INVALID_ADJ = -10000;
  static final byte LMK_PROCPRIO = 1;
  static final byte LMK_PROCREMOVE = 2;
  static final byte LMK_TARGET = 0;
  static final int MAX_CACHED_APPS;
  private static final int MAX_EMPTY_APPS;
  static final long MAX_EMPTY_TIME = 1800000L;
  static final int MIN_CACHED_APPS = 2;
  static final int MIN_CRASH_INTERVAL = 60000;
  static final int NATIVE_ADJ = -1000;
  static final int PAGE_SIZE = 4096;
  static final int PERCEPTIBLE_APP_ADJ = 200;
  static final int PERSISTENT_PROC_ADJ = -800;
  static final int PERSISTENT_SERVICE_ADJ = -700;
  static final int PREVIOUS_APP_ADJ = 700;
  public static final int PROC_MEM_CACHED = 4;
  public static final int PROC_MEM_IMPORTANT = 2;
  public static final int PROC_MEM_PERSISTENT = 0;
  public static final int PROC_MEM_SERVICE = 3;
  public static final int PROC_MEM_TOP = 1;
  public static final int PSS_ALL_INTERVAL = 600000;
  private static final int PSS_FIRST_BACKGROUND_INTERVAL = 20000;
  private static final int PSS_FIRST_CACHED_INTERVAL = 30000;
  private static final int PSS_FIRST_TOP_INTERVAL = 10000;
  public static final int PSS_MAX_INTERVAL = 1800000;
  public static final int PSS_MIN_TIME_FROM_STATE_CHANGE = 15000;
  public static final int PSS_SAFE_TIME_FROM_STATE_CHANGE = 1000;
  private static final int PSS_SAME_CACHED_INTERVAL = 1800000;
  private static final int PSS_SAME_IMPORTANT_INTERVAL = 900000;
  private static final int PSS_SAME_SERVICE_INTERVAL = 1200000;
  private static final int PSS_SHORT_INTERVAL = 120000;
  private static final int PSS_TEST_FIRST_BACKGROUND_INTERVAL = 5000;
  private static final int PSS_TEST_FIRST_TOP_INTERVAL = 3000;
  public static final int PSS_TEST_MIN_TIME_FROM_STATE_CHANGE = 10000;
  private static final int PSS_TEST_SAME_BACKGROUND_INTERVAL = 15000;
  private static final int PSS_TEST_SAME_IMPORTANT_INTERVAL = 10000;
  static final int SCHED_GROUP_BACKGROUND = 0;
  static final int SCHED_GROUP_DEFAULT = 1;
  static final int SCHED_GROUP_TOP_APP = 2;
  static final int SCHED_GROUP_TOP_APP_BOUND = 3;
  static final int SERVICE_ADJ = 500;
  static final int SERVICE_B_ADJ = 800;
  static final int SYSTEM_ADJ = -900;
  private static final String TAG = "ActivityManager";
  static final int TRIM_CACHED_APPS;
  static final int TRIM_CACHE_PERCENT;
  static final int TRIM_CRITICAL_THRESHOLD = 3;
  static final int TRIM_EMPTY_APPS;
  static final int TRIM_EMPTY_PERCENT;
  static final long TRIM_ENABLE_MEMORY;
  static final int TRIM_LOW_THRESHOLD = 5;
  static final int UNKNOWN_ADJ = 1001;
  static final boolean USE_TRIM_SETTINGS;
  static final int VISIBLE_APP_ADJ = 100;
  static final int VISIBLE_APP_LAYER_MAX = 99;
  private static final long[] sFirstAwakePssTimes = { 120000L, 120000L, 10000L, 20000L, 20000L, 20000L, 20000L, 20000L, 20000L, 20000L, 20000L, 30000L, 30000L, 30000L, 30000L, 30000L, 30000L };
  private static OutputStream sLmkdOutputStream;
  private static LocalSocket sLmkdSocket;
  private static final int[] sProcStateToProcMem;
  private static final long[] sSameAwakePssTimes = { 900000L, 900000L, 120000L, 900000L, 900000L, 900000L, 900000L, 900000L, 900000L, 900000L, 1200000L, 1200000L, 1800000L, 1800000L, 1800000L, 1800000L, 1800000L };
  private static final long[] sTestFirstAwakePssTimes = { 3000L, 3000L, 3000L, 20000L, 20000L, 20000L, 5000L, 5000L, 5000L, 5000L, 5000L, 5000L, 5000L, 5000L, 5000L, 5000L, 5000L };
  private static final long[] sTestSameAwakePssTimes = { 15000L, 15000L, 10000L, 10000L, 10000L, 10000L, 10000L, 10000L, 10000L, 10000L, 15000L, 15000L, 15000L, 15000L, 15000L, 15000L, 15000L };
  private long mCachedRestoreLevel;
  private boolean mHaveDisplaySize;
  private final int[] mOomAdj = { 0, 100, 200, 300, 900, 906 };
  private final int[] mOomMinFree = new int[this.mOomAdj.length];
  private final int[] mOomMinFreeHigh = { 73728, 92160, 110592, 129024, 147456, 184320 };
  private final int[] mOomMinFreeLow = { 12288, 18432, 24576, 36864, 43008, 49152 };
  private final long mTotalMemMb;
  
  static
  {
    MAX_CACHED_APPS = SystemProperties.getInt("ro.sys.fw.bg_apps_limit", 32);
    USE_TRIM_SETTINGS = SystemProperties.getBoolean("ro.sys.fw.use_trim_settings", true);
    EMPTY_APP_PERCENT = SystemProperties.getInt("ro.sys.fw.empty_app_percent", 50);
    TRIM_EMPTY_PERCENT = SystemProperties.getInt("ro.sys.fw.trim_empty_percent", 100);
    TRIM_CACHE_PERCENT = SystemProperties.getInt("ro.sys.fw.trim_cache_percent", 100);
    TRIM_ENABLE_MEMORY = SystemProperties.getLong("ro.sys.fw.trim_enable_memory", 1073741824L);
    MAX_EMPTY_APPS = computeEmptyProcessLimit(MAX_CACHED_APPS);
    TRIM_EMPTY_APPS = computeTrimEmptyApps();
    TRIM_CACHED_APPS = computeTrimCachedApps();
    sProcStateToProcMem = new int[] { 0, 0, 1, 2, 2, 1, 2, 2, 2, 2, 3, 4, 4, 4, 4, 4, 4 };
  }
  
  ProcessList()
  {
    MemInfoReader localMemInfoReader = new MemInfoReader();
    localMemInfoReader.readMemInfo();
    this.mTotalMemMb = (localMemInfoReader.getTotalSize() / 1048576L);
    updateOomLevels(0, 0, false);
  }
  
  public static boolean allowTrim()
  {
    return Process.getTotalMemory() < TRIM_ENABLE_MEMORY;
  }
  
  public static void appendRamKb(StringBuilder paramStringBuilder, long paramLong)
  {
    int j = 0;
    int i = 10;
    while (j < 6)
    {
      if (paramLong < i) {
        paramStringBuilder.append(' ');
      }
      j += 1;
      i *= 10;
    }
    paramStringBuilder.append(paramLong);
  }
  
  private static String buildOomTag(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2)
    {
      if (paramString2 == null) {
        return paramString1;
      }
      return paramString1 + "  ";
    }
    return paramString1 + "+" + Integer.toString(paramInt1 - paramInt2);
  }
  
  public static int computeEmptyProcessLimit(int paramInt)
  {
    if ((USE_TRIM_SETTINGS) && (allowTrim())) {
      return EMPTY_APP_PERCENT * paramInt / 100;
    }
    return paramInt / 2;
  }
  
  public static long computeNextPssTime(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, long paramLong)
  {
    long[] arrayOfLong;
    if (paramBoolean2) {
      if (paramBoolean1) {
        arrayOfLong = sTestFirstAwakePssTimes;
      }
    }
    for (;;)
    {
      return arrayOfLong[paramInt] + paramLong;
      arrayOfLong = sTestSameAwakePssTimes;
      continue;
      if (paramBoolean1) {
        arrayOfLong = sFirstAwakePssTimes;
      } else {
        arrayOfLong = sSameAwakePssTimes;
      }
    }
  }
  
  public static int computeTrimCachedApps()
  {
    if ((USE_TRIM_SETTINGS) && (allowTrim())) {
      return MAX_CACHED_APPS * TRIM_CACHE_PERCENT / 100;
    }
    return (MAX_CACHED_APPS - MAX_EMPTY_APPS) / 3;
  }
  
  public static int computeTrimEmptyApps()
  {
    if ((USE_TRIM_SETTINGS) && (allowTrim())) {
      return MAX_EMPTY_APPS * TRIM_EMPTY_PERCENT / 100;
    }
    return MAX_EMPTY_APPS / 2;
  }
  
  public static String makeOomAdjString(int paramInt)
  {
    if (paramInt >= 900) {
      return buildOomTag("cch", "  ", paramInt, 900);
    }
    if (paramInt >= 800) {
      return buildOomTag("svcb ", null, paramInt, 800);
    }
    if (paramInt >= 700) {
      return buildOomTag("prev ", null, paramInt, 700);
    }
    if (paramInt >= 600) {
      return buildOomTag("home ", null, paramInt, 600);
    }
    if (paramInt >= 500) {
      return buildOomTag("svc  ", null, paramInt, 500);
    }
    if (paramInt >= 400) {
      return buildOomTag("hvy  ", null, paramInt, 400);
    }
    if (paramInt >= 300) {
      return buildOomTag("bkup ", null, paramInt, 300);
    }
    if (paramInt >= 200) {
      return buildOomTag("prcp ", null, paramInt, 200);
    }
    if (paramInt >= 100) {
      return buildOomTag("vis  ", null, paramInt, 100);
    }
    if (paramInt >= 0) {
      return buildOomTag("fore ", null, paramInt, 0);
    }
    if (paramInt >= 64836) {
      return buildOomTag("psvc ", null, paramInt, 64836);
    }
    if (paramInt >= 64736) {
      return buildOomTag("pers ", null, paramInt, 64736);
    }
    if (paramInt >= 64636) {
      return buildOomTag("sys  ", null, paramInt, 64636);
    }
    if (paramInt >= 64536) {
      return buildOomTag("ntv  ", null, paramInt, 64536);
    }
    return Integer.toString(paramInt);
  }
  
  public static String makeProcStateString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "??";
    case -1: 
      return "N ";
    case 0: 
      return "P ";
    case 1: 
      return "PU";
    case 2: 
      return "T ";
    case 3: 
      return "SB";
    case 4: 
      return "SF";
    case 5: 
      return "TS";
    case 6: 
      return "IF";
    case 7: 
      return "IB";
    case 8: 
      return "BU";
    case 9: 
      return "HW";
    case 10: 
      return "S ";
    case 11: 
      return "R ";
    case 12: 
      return "HO";
    case 13: 
      return "LA";
    case 14: 
      return "CA";
    case 15: 
      return "Ca";
    }
    return "CE";
  }
  
  public static long minTimeFromStateChange(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 10000;; i = 15000) {
      return i;
    }
  }
  
  private static boolean openLmkdSocket()
  {
    try
    {
      sLmkdSocket = new LocalSocket(3);
      sLmkdSocket.connect(new LocalSocketAddress("lmkd", LocalSocketAddress.Namespace.RESERVED));
      sLmkdOutputStream = sLmkdSocket.getOutputStream();
      return true;
    }
    catch (IOException localIOException)
    {
      Slog.w(TAG, "lowmemorykiller daemon socket open failed");
      sLmkdSocket = null;
    }
    return false;
  }
  
  public static boolean procStatesDifferForMem(int paramInt1, int paramInt2)
  {
    return sProcStateToProcMem[paramInt1] != sProcStateToProcMem[paramInt2];
  }
  
  public static final void remove(int paramInt)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(8);
    localByteBuffer.putInt(2);
    localByteBuffer.putInt(paramInt);
    writeLmkd(localByteBuffer);
  }
  
  public static final void setOomAdj(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt3 == 1001) {
      return;
    }
    long l1 = SystemClock.elapsedRealtime();
    ByteBuffer localByteBuffer = ByteBuffer.allocate(16);
    localByteBuffer.putInt(1);
    localByteBuffer.putInt(paramInt1);
    localByteBuffer.putInt(paramInt2);
    localByteBuffer.putInt(paramInt3);
    writeLmkd(localByteBuffer);
    long l2 = SystemClock.elapsedRealtime();
    if (l2 - l1 > 250L) {
      Slog.w("ActivityManager", "SLOW OOM ADJ: " + (l2 - l1) + "ms for pid " + paramInt1 + " = " + paramInt3);
    }
  }
  
  private void updateOomLevels(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    float f1 = (float)(this.mTotalMemMb - 350L) / 350.0F;
    float f2 = (paramInt1 * paramInt2 - 384000.0F) / 640000;
    label48:
    int n;
    int i1;
    int j;
    label80:
    int k;
    label83:
    int i2;
    int m;
    if (f1 > f2)
    {
      if (f1 >= 0.0F) {
        break label171;
      }
      f2 = 0.0F;
      n = Resources.getSystem().getInteger(17694729);
      i1 = Resources.getSystem().getInteger(17694728);
      if (Build.SUPPORTED_64_BIT_ABIS.length <= 0) {
        break label188;
      }
      j = 1;
      k = 0;
      if (k >= this.mOomAdj.length) {
        break label216;
      }
      i2 = this.mOomMinFreeLow[k];
      m = this.mOomMinFreeHigh[k];
      i = m;
      if (j != 0)
      {
        if (k != 4) {
          break label194;
        }
        i = m * 3 / 2;
      }
    }
    for (;;)
    {
      this.mOomMinFree[k] = ((int)(i2 + (i - i2) * f2));
      k += 1;
      break label83;
      f1 = f2;
      break;
      label171:
      f2 = f1;
      if (f1 <= 1.0F) {
        break label48;
      }
      f2 = 1.0F;
      break label48;
      label188:
      j = 0;
      break label80;
      label194:
      i = m;
      if (k == 5) {
        i = m * 7 / 4;
      }
    }
    label216:
    if (i1 >= 0)
    {
      i = 0;
      while (i < this.mOomAdj.length)
      {
        this.mOomMinFree[i] = ((int)(i1 * this.mOomMinFree[i] / this.mOomMinFree[(this.mOomAdj.length - 1)]));
        i += 1;
      }
    }
    Object localObject;
    if (n != 0)
    {
      i = 0;
      while (i < this.mOomAdj.length)
      {
        localObject = this.mOomMinFree;
        localObject[i] += (int)(n * this.mOomMinFree[i] / this.mOomMinFree[(this.mOomAdj.length - 1)]);
        if (this.mOomMinFree[i] < 0) {
          this.mOomMinFree[i] = 0;
        }
        i += 1;
      }
    }
    this.mCachedRestoreLevel = (getMemLevel(906) / 1024L / 3L);
    paramInt2 = paramInt1 * paramInt2 * 4 * 3 / 1024;
    int i = Resources.getSystem().getInteger(17694731);
    paramInt1 = Resources.getSystem().getInteger(17694730);
    if (paramInt1 >= 0) {
      paramInt2 = paramInt1;
    }
    paramInt1 = paramInt2;
    if (i != 0)
    {
      paramInt2 += i;
      paramInt1 = paramInt2;
      if (paramInt2 < 0) {
        paramInt1 = 0;
      }
    }
    if (paramBoolean)
    {
      localObject = ByteBuffer.allocate((this.mOomAdj.length * 2 + 1) * 4);
      ((ByteBuffer)localObject).putInt(0);
      paramInt2 = 0;
      while (paramInt2 < this.mOomAdj.length)
      {
        ((ByteBuffer)localObject).putInt(this.mOomMinFree[paramInt2] * 1024 / 4096);
        ((ByteBuffer)localObject).putInt(this.mOomAdj[paramInt2]);
        paramInt2 += 1;
      }
      writeLmkd((ByteBuffer)localObject);
      SystemProperties.set("sys.sysctl.extra_free_kbytes", Integer.toString(paramInt1));
    }
  }
  
  private static void writeLmkd(ByteBuffer paramByteBuffer)
  {
    int i = 0;
    if (i < 3) {
      if ((sLmkdSocket != null) || (openLmkdSocket())) {}
    }
    for (;;)
    {
      try
      {
        Thread.sleep(1000L);
        i += 1;
      }
      catch (InterruptedException localInterruptedException)
      {
        continue;
      }
      try
      {
        sLmkdOutputStream.write(paramByteBuffer.array(), 0, paramByteBuffer.position());
        return;
      }
      catch (IOException localIOException1)
      {
        Slog.w(TAG, "Error writing to lowmemorykiller socket");
      }
      try
      {
        sLmkdSocket.close();
        sLmkdSocket = null;
        continue;
        return;
      }
      catch (IOException localIOException2)
      {
        for (;;) {}
      }
    }
  }
  
  void applyDisplaySize(WindowManagerService paramWindowManagerService)
  {
    if (!this.mHaveDisplaySize)
    {
      Point localPoint = new Point();
      paramWindowManagerService.getBaseDisplaySize(0, localPoint);
      if ((localPoint.x != 0) && (localPoint.y != 0))
      {
        updateOomLevels(localPoint.x, localPoint.y, true);
        this.mHaveDisplaySize = true;
      }
    }
  }
  
  long getCachedRestoreThresholdKb()
  {
    return this.mCachedRestoreLevel;
  }
  
  long getMemLevel(int paramInt)
  {
    int i = 0;
    while (i < this.mOomAdj.length)
    {
      if (paramInt <= this.mOomAdj[i]) {
        return this.mOomMinFree[i] * 1024;
      }
      i += 1;
    }
    return this.mOomMinFree[(this.mOomAdj.length - 1)] * 1024;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ProcessList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */