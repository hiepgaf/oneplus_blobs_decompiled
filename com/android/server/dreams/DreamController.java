package com.android.server.dreams;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IRemoteCallback;
import android.os.IRemoteCallback.Stub;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.dreams.IDreamService;
import android.service.dreams.IDreamService.Stub;
import android.util.Slog;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import java.io.PrintWriter;

final class DreamController
{
  private static final int DREAM_CONNECTION_TIMEOUT = 5000;
  private static final int DREAM_FINISH_TIMEOUT = 5000;
  private static final String TAG = "DreamController";
  private final Intent mCloseNotificationShadeIntent;
  private final Context mContext;
  private DreamRecord mCurrentDream;
  private long mDreamStartTime;
  private final Intent mDreamingStartedIntent = new Intent("android.intent.action.DREAMING_STARTED").addFlags(1073741824);
  private final Intent mDreamingStoppedIntent = new Intent("android.intent.action.DREAMING_STOPPED").addFlags(1073741824);
  private final Handler mHandler;
  private final IWindowManager mIWindowManager;
  private final Listener mListener;
  private final Runnable mStopStubbornDreamRunnable = new Runnable()
  {
    public void run()
    {
      Slog.w("DreamController", "Stubborn dream did not finish itself in the time allotted");
      DreamController.this.stopDream(true);
    }
  };
  private final Runnable mStopUnconnectedDreamRunnable = new Runnable()
  {
    public void run()
    {
      if ((DreamController.-get0(DreamController.this) == null) || (!DreamController.-get0(DreamController.this).mBound) || (DreamController.-get0(DreamController.this).mConnected)) {
        return;
      }
      Slog.w("DreamController", "Bound dream did not connect in the time allotted");
      DreamController.this.stopDream(true);
    }
  };
  
  public DreamController(Context paramContext, Handler paramHandler, Listener paramListener)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.mListener = paramListener;
    this.mIWindowManager = WindowManagerGlobal.getWindowManagerService();
    this.mCloseNotificationShadeIntent = new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS");
    this.mCloseNotificationShadeIntent.putExtra("reason", "dream");
  }
  
  private void attach(IDreamService paramIDreamService)
  {
    try
    {
      paramIDreamService.asBinder().linkToDeath(this.mCurrentDream, 0);
      paramIDreamService.attach(this.mCurrentDream.mToken, this.mCurrentDream.mCanDoze, this.mCurrentDream.mDreamingStartedCallback);
      this.mCurrentDream.mService = paramIDreamService;
      if (!this.mCurrentDream.mIsTest)
      {
        this.mContext.sendBroadcastAsUser(this.mDreamingStartedIntent, UserHandle.ALL);
        this.mCurrentDream.mSentStartBroadcast = true;
      }
      return;
    }
    catch (RemoteException paramIDreamService)
    {
      Slog.e("DreamController", "The dream service died unexpectedly.", paramIDreamService);
      stopDream(true);
    }
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("Dreamland:");
    if (this.mCurrentDream != null)
    {
      paramPrintWriter.println("  mCurrentDream:");
      paramPrintWriter.println("    mToken=" + this.mCurrentDream.mToken);
      paramPrintWriter.println("    mName=" + this.mCurrentDream.mName);
      paramPrintWriter.println("    mIsTest=" + this.mCurrentDream.mIsTest);
      paramPrintWriter.println("    mCanDoze=" + this.mCurrentDream.mCanDoze);
      paramPrintWriter.println("    mUserId=" + this.mCurrentDream.mUserId);
      paramPrintWriter.println("    mBound=" + this.mCurrentDream.mBound);
      paramPrintWriter.println("    mService=" + this.mCurrentDream.mService);
      paramPrintWriter.println("    mSentStartBroadcast=" + this.mCurrentDream.mSentStartBroadcast);
      paramPrintWriter.println("    mWakingGently=" + this.mCurrentDream.mWakingGently);
      return;
    }
    paramPrintWriter.println("  mCurrentDream: null");
  }
  
  /* Error */
  public void startDream(Binder paramBinder, ComponentName paramComponentName, boolean paramBoolean1, boolean paramBoolean2, int paramInt, PowerManager.WakeLock paramWakeLock)
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_1
    //   2: invokevirtual 189	com/android/server/dreams/DreamController:stopDream	(Z)V
    //   5: ldc2_w 258
    //   8: ldc_w 260
    //   11: invokestatic 266	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   14: aload_0
    //   15: getfield 106	com/android/server/dreams/DreamController:mContext	Landroid/content/Context;
    //   18: aload_0
    //   19: getfield 118	com/android/server/dreams/DreamController:mCloseNotificationShadeIntent	Landroid/content/Intent;
    //   22: getstatic 168	android/os/UserHandle:ALL	Landroid/os/UserHandle;
    //   25: invokevirtual 174	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
    //   28: ldc 35
    //   30: new 202	java/lang/StringBuilder
    //   33: dup
    //   34: invokespecial 203	java/lang/StringBuilder:<init>	()V
    //   37: ldc_w 268
    //   40: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   43: aload_2
    //   44: invokevirtual 212	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   47: ldc_w 270
    //   50: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: iload_3
    //   54: invokevirtual 227	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   57: ldc_w 272
    //   60: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: iload 4
    //   65: invokevirtual 227	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   68: ldc_w 274
    //   71: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: iload 5
    //   76: invokevirtual 237	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   79: invokevirtual 216	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   82: invokestatic 278	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   85: pop
    //   86: aload_0
    //   87: new 12	com/android/server/dreams/DreamController$DreamRecord
    //   90: dup
    //   91: aload_0
    //   92: aload_1
    //   93: aload_2
    //   94: iload_3
    //   95: iload 4
    //   97: iload 5
    //   99: aload 6
    //   101: invokespecial 281	com/android/server/dreams/DreamController$DreamRecord:<init>	(Lcom/android/server/dreams/DreamController;Landroid/os/Binder;Landroid/content/ComponentName;ZZILandroid/os/PowerManager$WakeLock;)V
    //   104: putfield 58	com/android/server/dreams/DreamController:mCurrentDream	Lcom/android/server/dreams/DreamController$DreamRecord;
    //   107: aload_0
    //   108: invokestatic 287	android/os/SystemClock:elapsedRealtime	()J
    //   111: putfield 289	com/android/server/dreams/DreamController:mDreamStartTime	J
    //   114: aload_0
    //   115: getfield 106	com/android/server/dreams/DreamController:mContext	Landroid/content/Context;
    //   118: astore 6
    //   120: aload_0
    //   121: getfield 58	com/android/server/dreams/DreamController:mCurrentDream	Lcom/android/server/dreams/DreamController$DreamRecord;
    //   124: getfield 148	com/android/server/dreams/DreamController$DreamRecord:mCanDoze	Z
    //   127: ifeq +118 -> 245
    //   130: sipush 223
    //   133: istore 7
    //   135: aload 6
    //   137: iload 7
    //   139: invokestatic 295	com/android/internal/logging/MetricsLogger:visible	(Landroid/content/Context;I)V
    //   142: aload_0
    //   143: getfield 114	com/android/server/dreams/DreamController:mIWindowManager	Landroid/view/IWindowManager;
    //   146: aload_1
    //   147: sipush 2023
    //   150: invokeinterface 301 3 0
    //   155: new 80	android/content/Intent
    //   158: dup
    //   159: ldc_w 303
    //   162: invokespecial 85	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   165: astore_1
    //   166: aload_1
    //   167: aload_2
    //   168: invokevirtual 307	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
    //   171: pop
    //   172: aload_1
    //   173: ldc_w 308
    //   176: invokevirtual 90	android/content/Intent:addFlags	(I)Landroid/content/Intent;
    //   179: pop
    //   180: aload_0
    //   181: getfield 106	com/android/server/dreams/DreamController:mContext	Landroid/content/Context;
    //   184: aload_1
    //   185: aload_0
    //   186: getfield 58	com/android/server/dreams/DreamController:mCurrentDream	Lcom/android/server/dreams/DreamController$DreamRecord;
    //   189: ldc_w 309
    //   192: new 164	android/os/UserHandle
    //   195: dup
    //   196: iload 5
    //   198: invokespecial 312	android/os/UserHandle:<init>	(I)V
    //   201: invokevirtual 316	android/content/Context:bindServiceAsUser	(Landroid/content/Intent;Landroid/content/ServiceConnection;ILandroid/os/UserHandle;)Z
    //   204: ifne +112 -> 316
    //   207: ldc 35
    //   209: new 202	java/lang/StringBuilder
    //   212: dup
    //   213: invokespecial 203	java/lang/StringBuilder:<init>	()V
    //   216: ldc_w 318
    //   219: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   222: aload_1
    //   223: invokevirtual 212	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   226: invokevirtual 216	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   229: invokestatic 320	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   232: pop
    //   233: aload_0
    //   234: iconst_1
    //   235: invokevirtual 189	com/android/server/dreams/DreamController:stopDream	(Z)V
    //   238: ldc2_w 258
    //   241: invokestatic 324	android/os/Trace:traceEnd	(J)V
    //   244: return
    //   245: sipush 222
    //   248: istore 7
    //   250: goto -115 -> 135
    //   253: astore_1
    //   254: ldc 35
    //   256: ldc_w 326
    //   259: aload_1
    //   260: invokestatic 185	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   263: pop
    //   264: aload_0
    //   265: iconst_1
    //   266: invokevirtual 189	com/android/server/dreams/DreamController:stopDream	(Z)V
    //   269: ldc2_w 258
    //   272: invokestatic 324	android/os/Trace:traceEnd	(J)V
    //   275: return
    //   276: astore_2
    //   277: ldc 35
    //   279: new 202	java/lang/StringBuilder
    //   282: dup
    //   283: invokespecial 203	java/lang/StringBuilder:<init>	()V
    //   286: ldc_w 318
    //   289: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   292: aload_1
    //   293: invokevirtual 212	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   296: invokevirtual 216	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   299: aload_2
    //   300: invokestatic 185	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   303: pop
    //   304: aload_0
    //   305: iconst_1
    //   306: invokevirtual 189	com/android/server/dreams/DreamController:stopDream	(Z)V
    //   309: ldc2_w 258
    //   312: invokestatic 324	android/os/Trace:traceEnd	(J)V
    //   315: return
    //   316: aload_0
    //   317: getfield 58	com/android/server/dreams/DreamController:mCurrentDream	Lcom/android/server/dreams/DreamController$DreamRecord;
    //   320: iconst_1
    //   321: putfield 242	com/android/server/dreams/DreamController$DreamRecord:mBound	Z
    //   324: aload_0
    //   325: getfield 63	com/android/server/dreams/DreamController:mHandler	Landroid/os/Handler;
    //   328: aload_0
    //   329: getfield 101	com/android/server/dreams/DreamController:mStopUnconnectedDreamRunnable	Ljava/lang/Runnable;
    //   332: ldc2_w 327
    //   335: invokevirtual 334	android/os/Handler:postDelayed	(Ljava/lang/Runnable;J)Z
    //   338: pop
    //   339: ldc2_w 258
    //   342: invokestatic 324	android/os/Trace:traceEnd	(J)V
    //   345: return
    //   346: astore_1
    //   347: ldc2_w 258
    //   350: invokestatic 324	android/os/Trace:traceEnd	(J)V
    //   353: aload_1
    //   354: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	355	0	this	DreamController
    //   0	355	1	paramBinder	Binder
    //   0	355	2	paramComponentName	ComponentName
    //   0	355	3	paramBoolean1	boolean
    //   0	355	4	paramBoolean2	boolean
    //   0	355	5	paramInt	int
    //   0	355	6	paramWakeLock	PowerManager.WakeLock
    //   133	116	7	i	int
    // Exception table:
    //   from	to	target	type
    //   142	155	253	android/os/RemoteException
    //   180	238	276	java/lang/SecurityException
    //   14	130	346	finally
    //   135	142	346	finally
    //   142	155	346	finally
    //   155	180	346	finally
    //   180	238	346	finally
    //   254	269	346	finally
    //   277	309	346	finally
    //   316	339	346	finally
  }
  
  /* Error */
  public void stopDream(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 58	com/android/server/dreams/DreamController:mCurrentDream	Lcom/android/server/dreams/DreamController$DreamRecord;
    //   4: ifnonnull +4 -> 8
    //   7: return
    //   8: ldc2_w 258
    //   11: ldc_w 337
    //   14: invokestatic 266	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   17: iload_1
    //   18: ifne +75 -> 93
    //   21: aload_0
    //   22: getfield 58	com/android/server/dreams/DreamController:mCurrentDream	Lcom/android/server/dreams/DreamController$DreamRecord;
    //   25: getfield 251	com/android/server/dreams/DreamController$DreamRecord:mWakingGently	Z
    //   28: istore_1
    //   29: iload_1
    //   30: ifeq +10 -> 40
    //   33: ldc2_w 258
    //   36: invokestatic 324	android/os/Trace:traceEnd	(J)V
    //   39: return
    //   40: aload_0
    //   41: getfield 58	com/android/server/dreams/DreamController:mCurrentDream	Lcom/android/server/dreams/DreamController$DreamRecord;
    //   44: getfield 159	com/android/server/dreams/DreamController$DreamRecord:mService	Landroid/service/dreams/IDreamService;
    //   47: ifnull +46 -> 93
    //   50: aload_0
    //   51: getfield 58	com/android/server/dreams/DreamController:mCurrentDream	Lcom/android/server/dreams/DreamController$DreamRecord;
    //   54: iconst_1
    //   55: putfield 251	com/android/server/dreams/DreamController$DreamRecord:mWakingGently	Z
    //   58: aload_0
    //   59: getfield 58	com/android/server/dreams/DreamController:mCurrentDream	Lcom/android/server/dreams/DreamController$DreamRecord;
    //   62: getfield 159	com/android/server/dreams/DreamController$DreamRecord:mService	Landroid/service/dreams/IDreamService;
    //   65: invokeinterface 340 1 0
    //   70: aload_0
    //   71: getfield 63	com/android/server/dreams/DreamController:mHandler	Landroid/os/Handler;
    //   74: aload_0
    //   75: getfield 104	com/android/server/dreams/DreamController:mStopStubbornDreamRunnable	Ljava/lang/Runnable;
    //   78: ldc2_w 327
    //   81: invokevirtual 334	android/os/Handler:postDelayed	(Ljava/lang/Runnable;J)Z
    //   84: pop
    //   85: ldc2_w 258
    //   88: invokestatic 324	android/os/Trace:traceEnd	(J)V
    //   91: return
    //   92: astore_3
    //   93: aload_0
    //   94: getfield 58	com/android/server/dreams/DreamController:mCurrentDream	Lcom/android/server/dreams/DreamController$DreamRecord;
    //   97: astore 4
    //   99: aload_0
    //   100: aconst_null
    //   101: putfield 58	com/android/server/dreams/DreamController:mCurrentDream	Lcom/android/server/dreams/DreamController$DreamRecord;
    //   104: ldc 35
    //   106: new 202	java/lang/StringBuilder
    //   109: dup
    //   110: invokespecial 203	java/lang/StringBuilder:<init>	()V
    //   113: ldc_w 342
    //   116: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   119: aload 4
    //   121: getfield 222	com/android/server/dreams/DreamController$DreamRecord:mName	Landroid/content/ComponentName;
    //   124: invokevirtual 212	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   127: ldc_w 270
    //   130: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   133: aload 4
    //   135: getfield 162	com/android/server/dreams/DreamController$DreamRecord:mIsTest	Z
    //   138: invokevirtual 227	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   141: ldc_w 272
    //   144: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   147: aload 4
    //   149: getfield 148	com/android/server/dreams/DreamController$DreamRecord:mCanDoze	Z
    //   152: invokevirtual 227	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   155: ldc_w 274
    //   158: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   161: aload 4
    //   163: getfield 234	com/android/server/dreams/DreamController$DreamRecord:mUserId	I
    //   166: invokevirtual 237	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   169: invokevirtual 216	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   172: invokestatic 278	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   175: pop
    //   176: aload_0
    //   177: getfield 106	com/android/server/dreams/DreamController:mContext	Landroid/content/Context;
    //   180: astore_3
    //   181: aload 4
    //   183: getfield 148	com/android/server/dreams/DreamController$DreamRecord:mCanDoze	Z
    //   186: ifeq +199 -> 385
    //   189: sipush 223
    //   192: istore_2
    //   193: aload_3
    //   194: iload_2
    //   195: invokestatic 345	com/android/internal/logging/MetricsLogger:hidden	(Landroid/content/Context;I)V
    //   198: aload_0
    //   199: getfield 106	com/android/server/dreams/DreamController:mContext	Landroid/content/Context;
    //   202: astore 5
    //   204: aload 4
    //   206: getfield 148	com/android/server/dreams/DreamController$DreamRecord:mCanDoze	Z
    //   209: ifeq +183 -> 392
    //   212: ldc_w 347
    //   215: astore_3
    //   216: aload 5
    //   218: aload_3
    //   219: invokestatic 287	android/os/SystemClock:elapsedRealtime	()J
    //   222: aload_0
    //   223: getfield 289	com/android/server/dreams/DreamController:mDreamStartTime	J
    //   226: lsub
    //   227: ldc2_w 348
    //   230: ldiv
    //   231: l2i
    //   232: invokestatic 353	com/android/internal/logging/MetricsLogger:histogram	(Landroid/content/Context;Ljava/lang/String;I)V
    //   235: aload_0
    //   236: getfield 63	com/android/server/dreams/DreamController:mHandler	Landroid/os/Handler;
    //   239: aload_0
    //   240: getfield 101	com/android/server/dreams/DreamController:mStopUnconnectedDreamRunnable	Ljava/lang/Runnable;
    //   243: invokevirtual 357	android/os/Handler:removeCallbacks	(Ljava/lang/Runnable;)V
    //   246: aload_0
    //   247: getfield 63	com/android/server/dreams/DreamController:mHandler	Landroid/os/Handler;
    //   250: aload_0
    //   251: getfield 104	com/android/server/dreams/DreamController:mStopStubbornDreamRunnable	Ljava/lang/Runnable;
    //   254: invokevirtual 357	android/os/Handler:removeCallbacks	(Ljava/lang/Runnable;)V
    //   257: aload 4
    //   259: getfield 177	com/android/server/dreams/DreamController$DreamRecord:mSentStartBroadcast	Z
    //   262: ifeq +17 -> 279
    //   265: aload_0
    //   266: getfield 106	com/android/server/dreams/DreamController:mContext	Landroid/content/Context;
    //   269: aload_0
    //   270: getfield 96	com/android/server/dreams/DreamController:mDreamingStoppedIntent	Landroid/content/Intent;
    //   273: getstatic 168	android/os/UserHandle:ALL	Landroid/os/UserHandle;
    //   276: invokevirtual 174	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
    //   279: aload 4
    //   281: getfield 159	com/android/server/dreams/DreamController$DreamRecord:mService	Landroid/service/dreams/IDreamService;
    //   284: astore_3
    //   285: aload_3
    //   286: ifnull +38 -> 324
    //   289: aload 4
    //   291: getfield 159	com/android/server/dreams/DreamController$DreamRecord:mService	Landroid/service/dreams/IDreamService;
    //   294: invokeinterface 360 1 0
    //   299: aload 4
    //   301: getfield 159	com/android/server/dreams/DreamController$DreamRecord:mService	Landroid/service/dreams/IDreamService;
    //   304: invokeinterface 134 1 0
    //   309: aload 4
    //   311: iconst_0
    //   312: invokeinterface 364 3 0
    //   317: pop
    //   318: aload 4
    //   320: aconst_null
    //   321: putfield 159	com/android/server/dreams/DreamController$DreamRecord:mService	Landroid/service/dreams/IDreamService;
    //   324: aload 4
    //   326: getfield 242	com/android/server/dreams/DreamController$DreamRecord:mBound	Z
    //   329: ifeq +12 -> 341
    //   332: aload_0
    //   333: getfield 106	com/android/server/dreams/DreamController:mContext	Landroid/content/Context;
    //   336: aload 4
    //   338: invokevirtual 368	android/content/Context:unbindService	(Landroid/content/ServiceConnection;)V
    //   341: aload 4
    //   343: invokevirtual 371	com/android/server/dreams/DreamController$DreamRecord:releaseWakeLockIfNeeded	()V
    //   346: aload_0
    //   347: getfield 114	com/android/server/dreams/DreamController:mIWindowManager	Landroid/view/IWindowManager;
    //   350: aload 4
    //   352: getfield 144	com/android/server/dreams/DreamController$DreamRecord:mToken	Landroid/os/Binder;
    //   355: invokeinterface 375 2 0
    //   360: aload_0
    //   361: getfield 63	com/android/server/dreams/DreamController:mHandler	Landroid/os/Handler;
    //   364: new 10	com/android/server/dreams/DreamController$3
    //   367: dup
    //   368: aload_0
    //   369: aload 4
    //   371: invokespecial 378	com/android/server/dreams/DreamController$3:<init>	(Lcom/android/server/dreams/DreamController;Lcom/android/server/dreams/DreamController$DreamRecord;)V
    //   374: invokevirtual 382	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   377: pop
    //   378: ldc2_w 258
    //   381: invokestatic 324	android/os/Trace:traceEnd	(J)V
    //   384: return
    //   385: sipush 222
    //   388: istore_2
    //   389: goto -196 -> 193
    //   392: ldc_w 384
    //   395: astore_3
    //   396: goto -180 -> 216
    //   399: astore_3
    //   400: ldc 35
    //   402: ldc_w 386
    //   405: aload_3
    //   406: invokestatic 389	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   409: pop
    //   410: goto -50 -> 360
    //   413: astore_3
    //   414: ldc2_w 258
    //   417: invokestatic 324	android/os/Trace:traceEnd	(J)V
    //   420: aload_3
    //   421: athrow
    //   422: astore_3
    //   423: goto -105 -> 318
    //   426: astore_3
    //   427: goto -128 -> 299
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	430	0	this	DreamController
    //   0	430	1	paramBoolean	boolean
    //   192	197	2	i	int
    //   92	1	3	localRemoteException1	RemoteException
    //   180	216	3	localObject1	Object
    //   399	7	3	localRemoteException2	RemoteException
    //   413	8	3	localObject2	Object
    //   422	1	3	localNoSuchElementException	java.util.NoSuchElementException
    //   426	1	3	localRemoteException3	RemoteException
    //   97	273	4	localDreamRecord	DreamRecord
    //   202	15	5	localContext	Context
    // Exception table:
    //   from	to	target	type
    //   58	85	92	android/os/RemoteException
    //   346	360	399	android/os/RemoteException
    //   21	29	413	finally
    //   40	58	413	finally
    //   58	85	413	finally
    //   93	189	413	finally
    //   193	212	413	finally
    //   216	279	413	finally
    //   279	285	413	finally
    //   289	299	413	finally
    //   299	318	413	finally
    //   318	324	413	finally
    //   324	341	413	finally
    //   341	346	413	finally
    //   346	360	413	finally
    //   360	378	413	finally
    //   400	410	413	finally
    //   299	318	422	java/util/NoSuchElementException
    //   289	299	426	android/os/RemoteException
  }
  
  private final class DreamRecord
    implements IBinder.DeathRecipient, ServiceConnection
  {
    public boolean mBound;
    public final boolean mCanDoze;
    public boolean mConnected;
    final IRemoteCallback mDreamingStartedCallback = new IRemoteCallback.Stub()
    {
      public void sendResult(Bundle paramAnonymousBundle)
        throws RemoteException
      {
        DreamController.-get1(DreamController.this).post(DreamController.DreamRecord.this.mReleaseWakeLockIfNeeded);
      }
    };
    public final boolean mIsTest;
    public final ComponentName mName;
    final Runnable mReleaseWakeLockIfNeeded = new -void__init__com_android_server_dreams_DreamController_this.0_android_os_Binder_token_android_content_ComponentName_name_boolean_isTest_boolean_canDoze_int_userId_android_os_PowerManager.WakeLock_wakeLock_LambdaImpl0();
    public boolean mSentStartBroadcast;
    public IDreamService mService;
    public final Binder mToken;
    public final int mUserId;
    public PowerManager.WakeLock mWakeLock;
    public boolean mWakingGently;
    
    public DreamRecord(Binder paramBinder, ComponentName paramComponentName, boolean paramBoolean1, boolean paramBoolean2, int paramInt, PowerManager.WakeLock paramWakeLock)
    {
      this.mToken = paramBinder;
      this.mName = paramComponentName;
      this.mIsTest = paramBoolean1;
      this.mCanDoze = paramBoolean2;
      this.mUserId = paramInt;
      this.mWakeLock = paramWakeLock;
      this.mWakeLock.acquire();
      DreamController.-get1(DreamController.this).postDelayed(this.mReleaseWakeLockIfNeeded, 10000L);
    }
    
    public void binderDied()
    {
      DreamController.-get1(DreamController.this).post(new Runnable()
      {
        public void run()
        {
          DreamController.DreamRecord.this.mService = null;
          if (DreamController.-get0(DreamController.this) == DreamController.DreamRecord.this) {
            DreamController.this.stopDream(true);
          }
        }
      });
    }
    
    public void onServiceConnected(ComponentName paramComponentName, final IBinder paramIBinder)
    {
      DreamController.-get1(DreamController.this).post(new Runnable()
      {
        public void run()
        {
          DreamController.DreamRecord.this.mConnected = true;
          if ((DreamController.-get0(DreamController.this) == DreamController.DreamRecord.this) && (DreamController.DreamRecord.this.mService == null))
          {
            DreamController.-wrap0(DreamController.this, IDreamService.Stub.asInterface(paramIBinder));
            return;
          }
          DreamController.DreamRecord.this.releaseWakeLockIfNeeded();
        }
      });
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      DreamController.-get1(DreamController.this).post(new Runnable()
      {
        public void run()
        {
          DreamController.DreamRecord.this.mService = null;
          if (DreamController.-get0(DreamController.this) == DreamController.DreamRecord.this) {
            DreamController.this.stopDream(true);
          }
        }
      });
    }
    
    void releaseWakeLockIfNeeded()
    {
      if (this.mWakeLock != null)
      {
        this.mWakeLock.release();
        this.mWakeLock = null;
        DreamController.-get1(DreamController.this).removeCallbacks(this.mReleaseWakeLockIfNeeded);
      }
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onDreamStopped(Binder paramBinder);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/dreams/DreamController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */