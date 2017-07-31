package com.android.server.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.ZenModeConfig;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Slog;
import java.io.PrintWriter;

public class CountdownConditionProvider
  extends SystemConditionProviderService
{
  private static final String ACTION = CountdownConditionProvider.class.getName();
  public static final ComponentName COMPONENT;
  private static final boolean DEBUG = Log.isLoggable("ConditionProviders", 3);
  private static final String EXTRA_CONDITION_ID = "condition_id";
  private static final int REQUEST_CODE = 100;
  private static final String TAG = "ConditionProviders.CCP";
  private boolean mConnected;
  private final Context mContext = this;
  private final Receiver mReceiver = new Receiver(null);
  private long mTime;
  
  static
  {
    COMPONENT = new ComponentName("android", CountdownConditionProvider.class.getName());
  }
  
  public CountdownConditionProvider()
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.CCP", "new CountdownConditionProvider()");
    }
  }
  
  private static final Condition newCondition(long paramLong, int paramInt)
  {
    return new Condition(ZenModeConfig.toCountdownConditionId(paramLong), "", "", "", 0, paramInt, 1);
  }
  
  public static String tryParseDescription(Uri paramUri)
  {
    long l1 = ZenModeConfig.tryParseCountdownConditionId(paramUri);
    if (l1 == 0L) {
      return null;
    }
    long l2 = System.currentTimeMillis();
    paramUri = DateUtils.getRelativeTimeSpanString(l1, l2, 60000L);
    return String.format("Scheduled for %s, %s in the future (%s), now=%s", new Object[] { ts(l1), Long.valueOf(l1 - l2), paramUri, ts(l2) });
  }
  
  public IConditionProvider asInterface()
  {
    return (IConditionProvider)onBind(null);
  }
  
  public void attachBase(Context paramContext)
  {
    attachBaseContext(paramContext);
  }
  
  public void dump(PrintWriter paramPrintWriter, NotificationManagerService.DumpFilter paramDumpFilter)
  {
    paramPrintWriter.println("    CountdownConditionProvider:");
    paramPrintWriter.print("      mConnected=");
    paramPrintWriter.println(this.mConnected);
    paramPrintWriter.print("      mTime=");
    paramPrintWriter.println(this.mTime);
  }
  
  public ComponentName getComponent()
  {
    return COMPONENT;
  }
  
  public boolean isValidConditionId(Uri paramUri)
  {
    return ZenModeConfig.isValidCountdownConditionId(paramUri);
  }
  
  public void onBootComplete() {}
  
  public void onConnected()
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.CCP", "onConnected");
    }
    this.mContext.registerReceiver(this.mReceiver, new IntentFilter(ACTION));
    this.mConnected = true;
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (DEBUG) {
      Slog.d("ConditionProviders.CCP", "onDestroy");
    }
    if (this.mConnected) {
      this.mContext.unregisterReceiver(this.mReceiver);
    }
    this.mConnected = false;
  }
  
  public void onSubscribe(Uri paramUri)
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.CCP", "onSubscribe " + paramUri);
    }
    this.mTime = ZenModeConfig.tryParseCountdownConditionId(paramUri);
    AlarmManager localAlarmManager = (AlarmManager)this.mContext.getSystemService("alarm");
    paramUri = new Intent(ACTION).putExtra("condition_id", paramUri).setFlags(1073741824);
    paramUri = PendingIntent.getBroadcast(this.mContext, 100, paramUri, 134217728);
    localAlarmManager.cancel(paramUri);
    long l;
    CharSequence localCharSequence;
    if (this.mTime > 0L)
    {
      l = System.currentTimeMillis();
      localCharSequence = DateUtils.getRelativeTimeSpanString(this.mTime, l, 60000L);
      if (this.mTime > l) {
        break label222;
      }
      notifyCondition(newCondition(this.mTime, 0));
      if (DEBUG) {
        if (this.mTime > l) {
          break label236;
        }
      }
    }
    label222:
    label236:
    for (paramUri = "Not scheduling";; paramUri = "Scheduling")
    {
      Slog.d("ConditionProviders.CCP", String.format("%s %s for %s, %s in the future (%s), now=%s", new Object[] { paramUri, ACTION, ts(this.mTime), Long.valueOf(this.mTime - l), localCharSequence, ts(l) }));
      return;
      localAlarmManager.setExact(0, this.mTime, paramUri);
      break;
    }
  }
  
  public void onUnsubscribe(Uri paramUri) {}
  
  private final class Receiver
    extends BroadcastReceiver
  {
    private Receiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (CountdownConditionProvider.-get0().equals(paramIntent.getAction()))
      {
        paramContext = (Uri)paramIntent.getParcelableExtra("condition_id");
        long l = ZenModeConfig.tryParseCountdownConditionId(paramContext);
        if (CountdownConditionProvider.-get1()) {
          Slog.d("ConditionProviders.CCP", "Countdown condition fired: " + paramContext);
        }
        if (l > 0L) {
          CountdownConditionProvider.this.notifyCondition(CountdownConditionProvider.-wrap0(l, 0));
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/CountdownConditionProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */