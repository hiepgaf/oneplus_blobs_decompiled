package com.android.server;

import android.app.IActivityController;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.server.am.ActivityManagerService;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Watchdog
  extends Thread
{
  public static final long CHECK_INTERVAL = 30000L;
  static final int COMPLETED = 0;
  static final boolean DB = false;
  public static final long DEFAULT_TIMEOUT = 60000L;
  public static final String[] NATIVE_STACKS_OF_INTEREST = { "/system/bin/audioserver", "/system/bin/cameraserver", "/system/bin/drmserver", "/system/bin/mediadrmserver", "/system/bin/mediaserver", "/system/bin/sdcard", "/system/bin/surfaceflinger", "media.codec", "media.extractor", "com.android.bluetooth" };
  static final int OVERDUE = 3;
  static final boolean RECORD_KERNEL_THREADS = true;
  static final String TAG = "Watchdog";
  static final int WAITED_HALF = 2;
  static final int WAITING = 1;
  static Watchdog sWatchdog;
  ActivityManagerService mActivity;
  boolean mAllowRestart = true;
  IActivityController mController;
  final ArrayList<HandlerChecker> mHandlerCheckers = new ArrayList();
  final HandlerChecker mMonitorChecker = new HandlerChecker(FgThread.getHandler(), "foreground thread", 60000L);
  int mPhonePid;
  ContentResolver mResolver;
  SimpleDateFormat mTraceDateFormat = new SimpleDateFormat("dd_MMM_HH_mm_ss.SSS");
  
  private Watchdog()
  {
    super("watchdog");
    this.mHandlerCheckers.add(this.mMonitorChecker);
    this.mHandlerCheckers.add(new HandlerChecker(new Handler(Looper.getMainLooper()), "main thread", 60000L));
    this.mHandlerCheckers.add(new HandlerChecker(UiThread.getHandler(), "ui thread", 60000L));
    this.mHandlerCheckers.add(new HandlerChecker(IoThread.getHandler(), "i/o thread", 60000L));
    this.mHandlerCheckers.add(new HandlerChecker(DisplayThread.getHandler(), "display thread", 60000L));
    addMonitor(new BinderThreadMonitor(null));
  }
  
  private void cleanupProcesses(final long paramLong)
  {
    Thread local2 = new Thread("watchdogKillerThread")
    {
      public void run()
      {
        if (paramLong == 30000L)
        {
          Watchdog.this.mActivity.cleanupProcesses(30000L);
          return;
        }
        Watchdog.this.mActivity.cleanupProcesses(60000L);
      }
    };
    local2.start();
    try
    {
      local2.join(1000L);
      SystemClock.sleep(1000L);
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;) {}
    }
  }
  
  private String describeCheckersLocked(ArrayList<HandlerChecker> paramArrayList)
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    int i = 0;
    while (i < paramArrayList.size())
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(((HandlerChecker)paramArrayList.get(i)).describeBlockedStateLocked());
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private void doSysRq(char paramChar)
  {
    try
    {
      FileWriter localFileWriter = new FileWriter("/proc/sysrq-trigger");
      localFileWriter.write(paramChar);
      localFileWriter.close();
      return;
    }
    catch (IOException localIOException)
    {
      Slog.w("Watchdog", "Failed to write to /proc/sysrq-trigger", localIOException);
    }
  }
  
  private File dumpKernelStackTraces()
  {
    String str = SystemProperties.get("dalvik.vm.stack-trace-file", null);
    if ((str == null) || (str.length() == 0)) {
      return null;
    }
    native_dumpKernelStacks(str);
    return new File(str);
  }
  
  private int evaluateCheckerCompletionLocked()
  {
    int j = 0;
    int i = 0;
    while (i < this.mHandlerCheckers.size())
    {
      j = Math.max(j, ((HandlerChecker)this.mHandlerCheckers.get(i)).getCompletionStateLocked());
      i += 1;
    }
    return j;
  }
  
  private ArrayList<HandlerChecker> getBlockedCheckersLocked()
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < this.mHandlerCheckers.size())
    {
      HandlerChecker localHandlerChecker = (HandlerChecker)this.mHandlerCheckers.get(i);
      if (localHandlerChecker.isOverdueLocked()) {
        localArrayList.add(localHandlerChecker);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  public static Watchdog getInstance()
  {
    if (sWatchdog == null) {
      sWatchdog = new Watchdog();
    }
    return sWatchdog;
  }
  
  private native void native_dumpKernelStacks(String paramString);
  
  public void addMonitor(Monitor paramMonitor)
  {
    try
    {
      if (isAlive()) {
        throw new RuntimeException("Monitors can't be added once the Watchdog is running");
      }
    }
    finally {}
    this.mMonitorChecker.addMonitor(paramMonitor);
  }
  
  public void addThread(Handler paramHandler)
  {
    addThread(paramHandler, 60000L);
  }
  
  public void addThread(Handler paramHandler, long paramLong)
  {
    try
    {
      if (isAlive()) {
        throw new RuntimeException("Threads can't be added once the Watchdog is running");
      }
    }
    finally {}
    String str = paramHandler.getLooper().getThread().getName();
    this.mHandlerCheckers.add(new HandlerChecker(paramHandler, str, paramLong));
  }
  
  public void init(Context paramContext, ActivityManagerService paramActivityManagerService)
  {
    this.mResolver = paramContext.getContentResolver();
    this.mActivity = paramActivityManagerService;
    paramContext.registerReceiver(new RebootRequestReceiver(), new IntentFilter("android.intent.action.REBOOT"), "android.permission.REBOOT", null);
  }
  
  public void processStarted(String paramString, int paramInt)
  {
    try
    {
      if ("com.android.phone".equals(paramString)) {
        this.mPhonePid = paramInt;
      }
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  void rebootSystem(String paramString)
  {
    Slog.i("Watchdog", "Rebooting system because: " + paramString);
    IPowerManager localIPowerManager = (IPowerManager)ServiceManager.getService("power");
    try
    {
      localIPowerManager.reboot(false, paramString, false);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: iconst_0
    //   3: istore_3
    //   4: aload_0
    //   5: monitorenter
    //   6: ldc2_w 23
    //   9: lstore 5
    //   11: iconst_0
    //   12: istore_2
    //   13: iload_2
    //   14: aload_0
    //   15: getfield 101	com/android/server/Watchdog:mHandlerCheckers	Ljava/util/ArrayList;
    //   18: invokevirtual 195	java/util/ArrayList:size	()I
    //   21: if_icmpge +24 -> 45
    //   24: aload_0
    //   25: getfield 101	com/android/server/Watchdog:mHandlerCheckers	Ljava/util/ArrayList;
    //   28: iload_2
    //   29: invokevirtual 208	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   32: checkcast 13	com/android/server/Watchdog$HandlerChecker
    //   35: invokevirtual 367	com/android/server/Watchdog$HandlerChecker:scheduleCheckLocked	()V
    //   38: iload_2
    //   39: iconst_1
    //   40: iadd
    //   41: istore_2
    //   42: goto -29 -> 13
    //   45: invokestatic 371	android/os/SystemClock:uptimeMillis	()J
    //   48: lstore 7
    //   50: iload_3
    //   51: istore_2
    //   52: lload 5
    //   54: lconst_0
    //   55: lcmp
    //   56: ifle +64 -> 120
    //   59: invokestatic 376	android/os/Debug:isDebuggerConnected	()Z
    //   62: istore 9
    //   64: iload 9
    //   66: ifeq +5 -> 71
    //   69: iconst_2
    //   70: istore_2
    //   71: aload_0
    //   72: lload 5
    //   74: invokevirtual 379	com/android/server/Watchdog:wait	(J)V
    //   77: invokestatic 376	android/os/Debug:isDebuggerConnected	()Z
    //   80: ifeq +5 -> 85
    //   83: iconst_2
    //   84: istore_2
    //   85: ldc2_w 23
    //   88: invokestatic 371	android/os/SystemClock:uptimeMillis	()J
    //   91: lload 7
    //   93: lsub
    //   94: lsub
    //   95: lstore 5
    //   97: goto -45 -> 52
    //   100: astore 11
    //   102: ldc 42
    //   104: aload 11
    //   106: invokestatic 385	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/Throwable;)I
    //   109: pop
    //   110: goto -33 -> 77
    //   113: astore 11
    //   115: aload_0
    //   116: monitorexit
    //   117: aload 11
    //   119: athrow
    //   120: aload_0
    //   121: invokespecial 387	com/android/server/Watchdog:evaluateCheckerCompletionLocked	()I
    //   124: istore 4
    //   126: iload 4
    //   128: ifne +12 -> 140
    //   131: iconst_0
    //   132: istore_3
    //   133: aload_0
    //   134: monitorexit
    //   135: iload_3
    //   136: istore_1
    //   137: goto -135 -> 2
    //   140: iload_1
    //   141: istore_3
    //   142: iload 4
    //   144: iconst_1
    //   145: if_icmpeq -12 -> 133
    //   148: iload 4
    //   150: iconst_2
    //   151: if_icmpne +68 -> 219
    //   154: iload_1
    //   155: istore_3
    //   156: iload_1
    //   157: ifne -24 -> 133
    //   160: new 97	java/util/ArrayList
    //   163: dup
    //   164: invokespecial 99	java/util/ArrayList:<init>	()V
    //   167: astore 11
    //   169: aload 11
    //   171: invokestatic 392	android/os/Process:myPid	()I
    //   174: invokestatic 398	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   177: invokevirtual 127	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   180: pop
    //   181: iconst_1
    //   182: aload 11
    //   184: aconst_null
    //   185: aconst_null
    //   186: getstatic 88	com/android/server/Watchdog:NATIVE_STACKS_OF_INTEREST	[Ljava/lang/String;
    //   189: invokestatic 404	com/android/server/am/ActivityManagerService:dumpStackTraces	(ZLjava/util/ArrayList;Lcom/android/internal/os/ProcessCpuTracker;Landroid/util/SparseArray;[Ljava/lang/String;)Ljava/io/File;
    //   192: pop
    //   193: iconst_1
    //   194: istore_1
    //   195: iload_1
    //   196: istore_3
    //   197: ldc_w 406
    //   200: iconst_0
    //   201: invokestatic 410	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   204: ifne -71 -> 133
    //   207: aload_0
    //   208: ldc2_w 23
    //   211: invokespecial 412	com/android/server/Watchdog:cleanupProcesses	(J)V
    //   214: iload_1
    //   215: istore_3
    //   216: goto -83 -> 133
    //   219: aload_0
    //   220: invokespecial 414	com/android/server/Watchdog:getBlockedCheckersLocked	()Ljava/util/ArrayList;
    //   223: astore 13
    //   225: aload_0
    //   226: aload 13
    //   228: invokespecial 416	com/android/server/Watchdog:describeCheckersLocked	(Ljava/util/ArrayList;)Ljava/lang/String;
    //   231: astore 14
    //   233: aload_0
    //   234: getfield 103	com/android/server/Watchdog:mAllowRestart	Z
    //   237: istore 10
    //   239: aload_0
    //   240: monitorexit
    //   241: sipush 2802
    //   244: aload 14
    //   246: invokestatic 422	android/util/EventLog:writeEvent	(ILjava/lang/String;)I
    //   249: pop
    //   250: new 97	java/util/ArrayList
    //   253: dup
    //   254: invokespecial 99	java/util/ArrayList:<init>	()V
    //   257: astore 11
    //   259: aload 11
    //   261: invokestatic 392	android/os/Process:myPid	()I
    //   264: invokestatic 398	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   267: invokevirtual 127	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   270: pop
    //   271: aload_0
    //   272: getfield 339	com/android/server/Watchdog:mPhonePid	I
    //   275: ifle +16 -> 291
    //   278: aload 11
    //   280: aload_0
    //   281: getfield 339	com/android/server/Watchdog:mPhonePid	I
    //   284: invokestatic 398	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   287: invokevirtual 127	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   290: pop
    //   291: iload_1
    //   292: ifeq +306 -> 598
    //   295: iconst_0
    //   296: istore 9
    //   298: iload 9
    //   300: aload 11
    //   302: aconst_null
    //   303: aconst_null
    //   304: getstatic 88	com/android/server/Watchdog:NATIVE_STACKS_OF_INTEREST	[Ljava/lang/String;
    //   307: invokestatic 404	com/android/server/am/ActivityManagerService:dumpStackTraces	(ZLjava/util/ArrayList;Lcom/android/internal/os/ProcessCpuTracker;Landroid/util/SparseArray;[Ljava/lang/String;)Ljava/io/File;
    //   310: pop
    //   311: ldc2_w 423
    //   314: invokestatic 184	android/os/SystemClock:sleep	(J)V
    //   317: aload_0
    //   318: invokespecial 426	com/android/server/Watchdog:dumpKernelStackTraces	()Ljava/io/File;
    //   321: pop
    //   322: ldc -12
    //   324: aconst_null
    //   325: invokestatic 249	android/os/SystemProperties:get	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   328: astore 12
    //   330: new 188	java/lang/StringBuilder
    //   333: dup
    //   334: invokespecial 343	java/lang/StringBuilder:<init>	()V
    //   337: ldc_w 428
    //   340: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   343: aload_0
    //   344: getfield 110	com/android/server/Watchdog:mTraceDateFormat	Ljava/text/SimpleDateFormat;
    //   347: new 430	java/util/Date
    //   350: dup
    //   351: invokespecial 431	java/util/Date:<init>	()V
    //   354: invokevirtual 435	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
    //   357: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   360: invokevirtual 215	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   363: astore 16
    //   365: aload 12
    //   367: astore 11
    //   369: aload 12
    //   371: ifnull +91 -> 462
    //   374: aload 12
    //   376: astore 11
    //   378: aload 12
    //   380: invokevirtual 250	java/lang/String:length	()I
    //   383: ifeq +79 -> 462
    //   386: new 255	java/io/File
    //   389: dup
    //   390: aload 12
    //   392: invokespecial 256	java/io/File:<init>	(Ljava/lang/String;)V
    //   395: astore 15
    //   397: aload 12
    //   399: ldc_w 437
    //   402: invokevirtual 441	java/lang/String:lastIndexOf	(Ljava/lang/String;)I
    //   405: istore_1
    //   406: iconst_m1
    //   407: iload_1
    //   408: if_icmpeq +196 -> 604
    //   411: new 188	java/lang/StringBuilder
    //   414: dup
    //   415: invokespecial 343	java/lang/StringBuilder:<init>	()V
    //   418: aload 12
    //   420: iconst_0
    //   421: iload_1
    //   422: invokevirtual 445	java/lang/String:substring	(II)Ljava/lang/String;
    //   425: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   428: aload 16
    //   430: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   433: aload 12
    //   435: iload_1
    //   436: invokevirtual 448	java/lang/String:substring	(I)Ljava/lang/String;
    //   439: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   442: invokevirtual 215	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   445: astore 11
    //   447: aload 15
    //   449: new 255	java/io/File
    //   452: dup
    //   453: aload 11
    //   455: invokespecial 256	java/io/File:<init>	(Ljava/lang/String;)V
    //   458: invokevirtual 452	java/io/File:renameTo	(Ljava/io/File;)Z
    //   461: pop
    //   462: new 6	com/android/server/Watchdog$1
    //   465: dup
    //   466: aload_0
    //   467: ldc_w 454
    //   470: aload 14
    //   472: new 255	java/io/File
    //   475: dup
    //   476: aload 11
    //   478: invokespecial 256	java/io/File:<init>	(Ljava/lang/String;)V
    //   481: invokespecial 457	com/android/server/Watchdog$1:<init>	(Lcom/android/server/Watchdog;Ljava/lang/String;Ljava/lang/String;Ljava/io/File;)V
    //   484: astore 11
    //   486: aload 11
    //   488: invokevirtual 174	java/lang/Thread:start	()V
    //   491: aload 11
    //   493: ldc2_w 423
    //   496: invokevirtual 179	java/lang/Thread:join	(J)V
    //   499: ldc_w 459
    //   502: iconst_0
    //   503: invokestatic 410	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   506: ifeq +36 -> 542
    //   509: ldc 42
    //   511: ldc_w 461
    //   514: invokestatic 464	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   517: pop
    //   518: aload_0
    //   519: bipush 119
    //   521: invokespecial 466	com/android/server/Watchdog:doSysRq	(C)V
    //   524: aload_0
    //   525: bipush 108
    //   527: invokespecial 466	com/android/server/Watchdog:doSysRq	(C)V
    //   530: ldc2_w 467
    //   533: invokestatic 184	android/os/SystemClock:sleep	(J)V
    //   536: aload_0
    //   537: bipush 99
    //   539: invokespecial 466	com/android/server/Watchdog:doSysRq	(C)V
    //   542: aload_0
    //   543: monitorenter
    //   544: aload_0
    //   545: getfield 470	com/android/server/Watchdog:mController	Landroid/app/IActivityController;
    //   548: astore 11
    //   550: aload_0
    //   551: monitorexit
    //   552: aload 11
    //   554: ifnull +89 -> 643
    //   557: ldc 42
    //   559: ldc_w 472
    //   562: invokestatic 349	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   565: pop
    //   566: ldc_w 474
    //   569: invokestatic 479	android/os/Binder:setDumpDisabled	(Ljava/lang/String;)V
    //   572: aload 11
    //   574: aload 14
    //   576: invokeinterface 484 2 0
    //   581: iflt +62 -> 643
    //   584: ldc 42
    //   586: ldc_w 486
    //   589: invokestatic 349	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   592: pop
    //   593: iconst_0
    //   594: istore_1
    //   595: goto -593 -> 2
    //   598: iconst_1
    //   599: istore 9
    //   601: goto -303 -> 298
    //   604: new 188	java/lang/StringBuilder
    //   607: dup
    //   608: invokespecial 343	java/lang/StringBuilder:<init>	()V
    //   611: aload 12
    //   613: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   616: aload 16
    //   618: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   621: invokevirtual 215	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   624: astore 11
    //   626: goto -179 -> 447
    //   629: astore 11
    //   631: goto -132 -> 499
    //   634: astore 11
    //   636: aload_0
    //   637: monitorexit
    //   638: aload 11
    //   640: athrow
    //   641: astore 11
    //   643: invokestatic 376	android/os/Debug:isDebuggerConnected	()Z
    //   646: ifeq +5 -> 651
    //   649: iconst_2
    //   650: istore_2
    //   651: iload_2
    //   652: iconst_2
    //   653: if_icmplt +17 -> 670
    //   656: ldc 42
    //   658: ldc_w 488
    //   661: invokestatic 490	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   664: pop
    //   665: iconst_0
    //   666: istore_1
    //   667: goto -665 -> 2
    //   670: iload_2
    //   671: ifle +15 -> 686
    //   674: ldc 42
    //   676: ldc_w 492
    //   679: invokestatic 490	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   682: pop
    //   683: goto -18 -> 665
    //   686: iload 10
    //   688: ifne +15 -> 703
    //   691: ldc 42
    //   693: ldc_w 494
    //   696: invokestatic 490	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   699: pop
    //   700: goto -35 -> 665
    //   703: ldc 42
    //   705: new 188	java/lang/StringBuilder
    //   708: dup
    //   709: invokespecial 343	java/lang/StringBuilder:<init>	()V
    //   712: ldc_w 496
    //   715: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   718: aload 14
    //   720: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   723: invokevirtual 215	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   726: invokestatic 490	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   729: pop
    //   730: iconst_0
    //   731: istore_1
    //   732: iload_1
    //   733: aload 13
    //   735: invokevirtual 195	java/util/ArrayList:size	()I
    //   738: if_icmpge +115 -> 853
    //   741: ldc 42
    //   743: new 188	java/lang/StringBuilder
    //   746: dup
    //   747: invokespecial 343	java/lang/StringBuilder:<init>	()V
    //   750: aload 13
    //   752: iload_1
    //   753: invokevirtual 208	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   756: checkcast 13	com/android/server/Watchdog$HandlerChecker
    //   759: invokevirtual 497	com/android/server/Watchdog$HandlerChecker:getName	()Ljava/lang/String;
    //   762: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   765: ldc_w 499
    //   768: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   771: invokevirtual 215	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   774: invokestatic 490	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   777: pop
    //   778: aload 13
    //   780: iload_1
    //   781: invokevirtual 208	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   784: checkcast 13	com/android/server/Watchdog$HandlerChecker
    //   787: invokevirtual 500	com/android/server/Watchdog$HandlerChecker:getThread	()Ljava/lang/Thread;
    //   790: invokevirtual 504	java/lang/Thread:getStackTrace	()[Ljava/lang/StackTraceElement;
    //   793: astore 11
    //   795: iconst_0
    //   796: istore_2
    //   797: aload 11
    //   799: arraylength
    //   800: istore_3
    //   801: iload_2
    //   802: iload_3
    //   803: if_icmpge +43 -> 846
    //   806: aload 11
    //   808: iload_2
    //   809: aaload
    //   810: astore 12
    //   812: ldc 42
    //   814: new 188	java/lang/StringBuilder
    //   817: dup
    //   818: invokespecial 343	java/lang/StringBuilder:<init>	()V
    //   821: ldc_w 506
    //   824: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   827: aload 12
    //   829: invokevirtual 509	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   832: invokevirtual 215	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   835: invokestatic 490	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   838: pop
    //   839: iload_2
    //   840: iconst_1
    //   841: iadd
    //   842: istore_2
    //   843: goto -42 -> 801
    //   846: iload_1
    //   847: iconst_1
    //   848: iadd
    //   849: istore_1
    //   850: goto -118 -> 732
    //   853: ldc_w 406
    //   856: iconst_0
    //   857: invokestatic 410	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   860: ifeq +8 -> 868
    //   863: iconst_0
    //   864: istore_1
    //   865: goto -863 -> 2
    //   868: aload_0
    //   869: ldc2_w 31
    //   872: invokespecial 412	com/android/server/Watchdog:cleanupProcesses	(J)V
    //   875: aload_0
    //   876: invokespecial 387	com/android/server/Watchdog:evaluateCheckerCompletionLocked	()I
    //   879: ifeq +26 -> 905
    //   882: ldc 42
    //   884: ldc_w 511
    //   887: invokestatic 490	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   890: pop
    //   891: invokestatic 392	android/os/Process:myPid	()I
    //   894: invokestatic 514	android/os/Process:killProcess	(I)V
    //   897: bipush 10
    //   899: invokestatic 519	java/lang/System:exit	(I)V
    //   902: goto -237 -> 665
    //   905: ldc 42
    //   907: ldc_w 521
    //   910: invokestatic 490	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   913: pop
    //   914: goto -249 -> 665
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	917	0	this	Watchdog
    //   1	864	1	i	int
    //   12	831	2	j	int
    //   3	801	3	k	int
    //   124	28	4	m	int
    //   9	87	5	l1	long
    //   48	44	7	l2	long
    //   62	538	9	bool1	boolean
    //   237	450	10	bool2	boolean
    //   100	5	11	localInterruptedException1	InterruptedException
    //   113	5	11	localObject1	Object
    //   167	458	11	localObject2	Object
    //   629	1	11	localInterruptedException2	InterruptedException
    //   634	5	11	localObject3	Object
    //   641	1	11	localRemoteException	RemoteException
    //   793	14	11	arrayOfStackTraceElement	StackTraceElement[]
    //   328	500	12	localObject4	Object
    //   223	556	13	localArrayList	ArrayList
    //   231	488	14	str1	String
    //   395	53	15	localFile	File
    //   363	254	16	str2	String
    // Exception table:
    //   from	to	target	type
    //   71	77	100	java/lang/InterruptedException
    //   13	38	113	finally
    //   45	50	113	finally
    //   59	64	113	finally
    //   71	77	113	finally
    //   77	83	113	finally
    //   85	97	113	finally
    //   102	110	113	finally
    //   120	126	113	finally
    //   160	193	113	finally
    //   197	214	113	finally
    //   219	239	113	finally
    //   491	499	629	java/lang/InterruptedException
    //   544	550	634	finally
    //   566	593	641	android/os/RemoteException
  }
  
  public void setActivityController(IActivityController paramIActivityController)
  {
    try
    {
      this.mController = paramIActivityController;
      return;
    }
    finally
    {
      paramIActivityController = finally;
      throw paramIActivityController;
    }
  }
  
  public void setAllowRestart(boolean paramBoolean)
  {
    try
    {
      this.mAllowRestart = paramBoolean;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private static final class BinderThreadMonitor
    implements Watchdog.Monitor
  {
    public void monitor() {}
  }
  
  public final class HandlerChecker
    implements Runnable
  {
    private boolean mCompleted;
    private Watchdog.Monitor mCurrentMonitor;
    private final Handler mHandler;
    private final ArrayList<Watchdog.Monitor> mMonitors = new ArrayList();
    private final String mName;
    private long mStartTime;
    private final long mWaitMax;
    
    HandlerChecker(Handler paramHandler, String paramString, long paramLong)
    {
      this.mHandler = paramHandler;
      this.mName = paramString;
      this.mWaitMax = paramLong;
      this.mCompleted = true;
    }
    
    public void addMonitor(Watchdog.Monitor paramMonitor)
    {
      this.mMonitors.add(paramMonitor);
    }
    
    public String describeBlockedStateLocked()
    {
      if (this.mCurrentMonitor == null) {
        return "Blocked in handler on " + this.mName + " (" + getThread().getName() + ")";
      }
      return "Blocked in monitor " + this.mCurrentMonitor.getClass().getName() + " on " + this.mName + " (" + getThread().getName() + ")";
    }
    
    public int getCompletionStateLocked()
    {
      if (this.mCompleted) {
        return 0;
      }
      long l = SystemClock.uptimeMillis() - this.mStartTime;
      if (l < this.mWaitMax / 2L) {
        return 1;
      }
      if (l < this.mWaitMax) {
        return 2;
      }
      return 3;
    }
    
    public String getName()
    {
      return this.mName;
    }
    
    public Thread getThread()
    {
      return this.mHandler.getLooper().getThread();
    }
    
    public boolean isOverdueLocked()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (!this.mCompleted)
      {
        bool1 = bool2;
        if (SystemClock.uptimeMillis() > this.mStartTime + this.mWaitMax) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public void run()
    {
      int j = this.mMonitors.size();
      int i = 0;
      for (;;)
      {
        if (i < j) {}
        synchronized (Watchdog.this)
        {
          this.mCurrentMonitor = ((Watchdog.Monitor)this.mMonitors.get(i));
          this.mCurrentMonitor.monitor();
          i += 1;
        }
      }
    }
    
    public void scheduleCheckLocked()
    {
      if ((this.mMonitors.size() == 0) && (this.mHandler.getLooper().getQueue().isPolling()))
      {
        this.mCompleted = true;
        return;
      }
      if (!this.mCompleted) {
        return;
      }
      this.mCompleted = false;
      this.mCurrentMonitor = null;
      this.mStartTime = SystemClock.uptimeMillis();
      this.mHandler.postAtFrontOfQueue(this);
    }
  }
  
  public static abstract interface Monitor
  {
    public abstract void monitor();
  }
  
  final class RebootRequestReceiver
    extends BroadcastReceiver
  {
    RebootRequestReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (paramIntent.getIntExtra("nowait", 0) != 0)
      {
        Watchdog.this.rebootSystem("Received ACTION_REBOOT broadcast");
        return;
      }
      Slog.w("Watchdog", "Unsupported ACTION_REBOOT broadcast: " + paramIntent);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/Watchdog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */