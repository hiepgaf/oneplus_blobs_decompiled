package com.android.server;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.util.Log;
import android.util.NtpTrustedTime;
import android.util.OpFeatures;
import android.util.TimeUtils;
import android.util.TrustedTime;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Calendar;

public class NetworkTimeUpdateService
  extends Binder
{
  private static final String ACTION_POLL = "com.android.server.NetworkTimeUpdateService.action.POLL";
  private static final boolean DBG = true;
  private static final String DECRYPT_STATE = "trigger_restart_framework";
  private static final int EVENT_AUTO_TIME_CHANGED = 1;
  private static final int EVENT_NETWORK_CHANGED = 3;
  private static final int EVENT_POLL_NETWORK_TIME = 2;
  private static final int NETWORK_CHANGE_EVENT_DELAY_MS = 1000;
  private static final long NOT_SET = -1L;
  private static int POLL_REQUEST = 0;
  private static final String TAG = "NetworkTimeUpdateService";
  private AlarmManager mAlarmManager;
  private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramAnonymousIntent.getAction()))
      {
        Log.d("NetworkTimeUpdateService", "Received CONNECTIVITY_ACTION ");
        paramAnonymousContext = NetworkTimeUpdateService.-get0(NetworkTimeUpdateService.this).obtainMessage(3);
        NetworkTimeUpdateService.-get0(NetworkTimeUpdateService.this).sendMessageDelayed(paramAnonymousContext, 1000L);
      }
    }
  };
  private Context mContext;
  private Handler mHandler;
  private long mLastNtpFetchTime = -1L;
  private BroadcastReceiver mNitzReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      Log.d("NetworkTimeUpdateService", "Received " + paramAnonymousContext);
      if ("android.intent.action.NETWORK_SET_TIME".equals(paramAnonymousContext)) {
        NetworkTimeUpdateService.-set0(NetworkTimeUpdateService.this, SystemClock.elapsedRealtime());
      }
      while (!"android.intent.action.NETWORK_SET_TIMEZONE".equals(paramAnonymousContext)) {
        return;
      }
      NetworkTimeUpdateService.-set1(NetworkTimeUpdateService.this, SystemClock.elapsedRealtime());
    }
  };
  private long mNitzTimeSetTime = -1L;
  private long mNitzZoneSetTime = -1L;
  private PendingIntent mPendingPollIntent;
  private final long mPollingIntervalMs;
  private final long mPollingIntervalShorterMs;
  private SettingsObserver mSettingsObserver;
  private TrustedTime mTime;
  private final int mTimeErrorThresholdMs;
  private int mTryAgainCounter;
  private final int mTryAgainTimesMax;
  private final PowerManager.WakeLock mWakeLock;
  
  public NetworkTimeUpdateService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mTime = NtpTrustedTime.getInstance(paramContext);
    this.mAlarmManager = ((AlarmManager)this.mContext.getSystemService("alarm"));
    Intent localIntent = new Intent("com.android.server.NetworkTimeUpdateService.action.POLL", null);
    this.mPendingPollIntent = PendingIntent.getBroadcast(this.mContext, POLL_REQUEST, localIntent, 0);
    this.mPollingIntervalMs = this.mContext.getResources().getInteger(17694850);
    this.mPollingIntervalShorterMs = this.mContext.getResources().getInteger(17694851);
    this.mTryAgainTimesMax = this.mContext.getResources().getInteger(17694852);
    this.mTimeErrorThresholdMs = this.mContext.getResources().getInteger(17694853);
    this.mWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "NetworkTimeUpdateService");
  }
  
  private void checkSystemTime()
  {
    boolean bool = "1".equals(SystemProperties.get("persist.sys.device_first_boot", "1"));
    if (bool)
    {
      Object localObject = SystemProperties.get("vold.decrypt", "");
      Log.d("NetworkTimeUpdateService", "decryptState:" + (String)localObject);
      String str = SystemProperties.get("ro.crypto.type", "");
      Log.d("NetworkTimeUpdateService", "cryptoType:" + str);
      if ((!"block".equals(str)) || ("trigger_restart_framework".equals(localObject)))
      {
        SystemProperties.set("persist.sys.device_first_boot", "0");
        long l1 = System.currentTimeMillis();
        localObject = Calendar.getInstance();
        ((Calendar)localObject).set(2017, 2, 1, 0, 0, 0);
        long l2 = ((Calendar)localObject).getTimeInMillis();
        Log.w("NetworkTimeUpdateService", "cur [" + l1 + "]" + "  destinationTime [" + l2 + "]");
        if (l1 < l2) {
          SystemClock.setCurrentTimeMillis(SystemClock.elapsedRealtime() + l2);
        }
        Log.w("NetworkTimeUpdateService", "reset system time here, isFirstBoot = [" + bool + "]");
      }
    }
  }
  
  private boolean isAutomaticTimeRequested()
  {
    boolean bool = false;
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "auto_time", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void onPollNetworkTime(int paramInt)
  {
    if (!isAutomaticTimeRequested())
    {
      Log.i("NetworkTimeUpdateService", "Automatic date & time is disable, skip polling time");
      return;
    }
    this.mWakeLock.acquire();
    try
    {
      onPollNetworkTimeUnderWakeLock(paramInt);
      return;
    }
    finally
    {
      this.mWakeLock.release();
    }
  }
  
  private void onPollNetworkTimeUnderWakeLock(int paramInt)
  {
    long l2 = SystemClock.elapsedRealtime();
    if (OpFeatures.isSupport(new int[] { 0 }))
    {
      if ((this.mNitzTimeSetTime != -1L) && (l2 - this.mNitzTimeSetTime < this.mPollingIntervalMs) && (paramInt != 1)) {
        resetAlarm(this.mPollingIntervalMs);
      }
    }
    else if ((this.mNitzTimeSetTime != -1L) && (l2 - this.mNitzTimeSetTime < this.mPollingIntervalMs))
    {
      resetAlarm(this.mPollingIntervalMs);
      return;
    }
    long l1 = System.currentTimeMillis();
    Log.d("NetworkTimeUpdateService", "System time = " + l1 + " event = " + paramInt);
    if ((this.mLastNtpFetchTime == -1L) || (l2 >= this.mLastNtpFetchTime + this.mPollingIntervalMs))
    {
      Log.d("NetworkTimeUpdateService", "Before Ntp fetch");
      if (!OpFeatures.isSupport(new int[] { 0 })) {
        break label407;
      }
      if (((this.mTime.getCacheAge() >= this.mPollingIntervalMs) || (paramInt == 1)) && (!this.mTime.forceRefresh())) {
        Log.d("NetworkTimeUpdateService", "forceRefresh failed !");
      }
      label224:
      if (this.mTime.getCacheAge() >= this.mPollingIntervalMs) {
        break label478;
      }
      l2 = this.mTime.currentTimeMillis();
      this.mTryAgainCounter = 0;
      if ((Math.abs(l2 - l1) <= this.mTimeErrorThresholdMs) && (this.mLastNtpFetchTime != -1L)) {
        break label448;
      }
      if ((this.mLastNtpFetchTime == -1L) && (Math.abs(l2 - l1) <= this.mTimeErrorThresholdMs)) {
        Log.d("NetworkTimeUpdateService", "For initial setup, rtc = " + l1);
      }
      Log.d("NetworkTimeUpdateService", "Ntp time to be set = " + l2);
      if (l2 / 1000L < 2147483647L) {
        SystemClock.setCurrentTimeMillis(l2);
      }
    }
    for (;;)
    {
      this.mLastNtpFetchTime = SystemClock.elapsedRealtime();
      do
      {
        resetAlarm(this.mPollingIntervalMs);
        return;
      } while (paramInt != 1);
      break;
      label407:
      if ((this.mTime.getCacheAge() < this.mPollingIntervalMs) || (this.mTime.forceRefresh())) {
        break label224;
      }
      Log.d("NetworkTimeUpdateService", "forceRefresh failed !");
      break label224;
      label448:
      Log.d("NetworkTimeUpdateService", "Ntp time is close enough = " + l2);
    }
    label478:
    this.mTryAgainCounter += 1;
    if ((this.mTryAgainTimesMax < 0) || (this.mTryAgainCounter <= this.mTryAgainTimesMax))
    {
      resetAlarm(this.mPollingIntervalShorterMs);
      return;
    }
    this.mTryAgainCounter = 0;
    resetAlarm(this.mPollingIntervalMs);
  }
  
  private void registerForAlarms()
  {
    this.mContext.registerReceiver(new BroadcastReceiver()new IntentFilter
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        NetworkTimeUpdateService.-get0(NetworkTimeUpdateService.this).obtainMessage(2).sendToTarget();
      }
    }, new IntentFilter("com.android.server.NetworkTimeUpdateService.action.POLL"));
  }
  
  private void registerForConnectivityIntents()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    this.mContext.registerReceiver(this.mConnectivityReceiver, localIntentFilter);
  }
  
  private void registerForTelephonyIntents()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.NETWORK_SET_TIME");
    localIntentFilter.addAction("android.intent.action.NETWORK_SET_TIMEZONE");
    this.mContext.registerReceiver(this.mNitzReceiver, localIntentFilter);
  }
  
  private void resetAlarm(long paramLong)
  {
    this.mAlarmManager.cancel(this.mPendingPollIntent);
    long l = SystemClock.elapsedRealtime();
    this.mAlarmManager.set(3, l + paramLong, this.mPendingPollIntent);
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump NetworkTimeUpdateService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + "android.permission.DUMP");
      return;
    }
    paramPrintWriter.print("PollingIntervalMs: ");
    TimeUtils.formatDuration(this.mPollingIntervalMs, paramPrintWriter);
    paramPrintWriter.print("\nPollingIntervalShorterMs: ");
    TimeUtils.formatDuration(this.mPollingIntervalShorterMs, paramPrintWriter);
    paramPrintWriter.println("\nTryAgainTimesMax: " + this.mTryAgainTimesMax);
    paramPrintWriter.print("TimeErrorThresholdMs: ");
    TimeUtils.formatDuration(this.mTimeErrorThresholdMs, paramPrintWriter);
    paramPrintWriter.println("\nTryAgainCounter: " + this.mTryAgainCounter);
    paramPrintWriter.print("LastNtpFetchTime: ");
    TimeUtils.formatDuration(this.mLastNtpFetchTime, paramPrintWriter);
    paramPrintWriter.println();
  }
  
  public void systemRunning()
  {
    registerForTelephonyIntents();
    registerForAlarms();
    registerForConnectivityIntents();
    HandlerThread localHandlerThread = new HandlerThread("NetworkTimeUpdateService");
    localHandlerThread.start();
    this.mHandler = new MyHandler(localHandlerThread.getLooper());
    this.mHandler.obtainMessage(2).sendToTarget();
    checkSystemTime();
    this.mSettingsObserver = new SettingsObserver(this.mHandler, 1);
    this.mSettingsObserver.observe(this.mContext);
  }
  
  private class MyHandler
    extends Handler
  {
    public MyHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      NetworkTimeUpdateService.-wrap0(NetworkTimeUpdateService.this, paramMessage.what);
    }
  }
  
  private static class SettingsObserver
    extends ContentObserver
  {
    private Handler mHandler;
    private int mMsg;
    
    SettingsObserver(Handler paramHandler, int paramInt)
    {
      super();
      this.mHandler = paramHandler;
      this.mMsg = paramInt;
    }
    
    void observe(Context paramContext)
    {
      paramContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("auto_time"), false, this);
    }
    
    public void onChange(boolean paramBoolean)
    {
      this.mHandler.obtainMessage(this.mMsg).sendToTarget();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/NetworkTimeUpdateService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */