package com.android.server.notification;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.provider.Settings.Secure;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ScheduleInfo;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

public class ScheduleConditionProvider
  extends SystemConditionProviderService
{
  private static final String ACTION_EVALUATE = SIMPLE_NAME + ".EVALUATE";
  public static final ComponentName COMPONENT;
  static final boolean DEBUG = true;
  private static final String EXTRA_TIME = "time";
  private static final String NOT_SHOWN = "...";
  private static final int REQUEST_CODE_EVALUATE = 1;
  private static final String SCP_SETTING = "snoozed_schedule_condition_provider";
  private static final String SEPARATOR = ";";
  private static final String SIMPLE_NAME;
  static final String TAG = "ConditionProviders.SCP";
  private AlarmManager mAlarmManager;
  private boolean mConnected;
  private final Context mContext = this;
  private long mNextAlarmTime;
  private BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      if (ScheduleConditionProvider.DEBUG) {
        Slog.d("ConditionProviders.SCP", "onReceive " + paramAnonymousIntent.getAction());
      }
      synchronized (ScheduleConditionProvider.-get0(ScheduleConditionProvider.this))
      {
        if ("android.intent.action.TIMEZONE_CHANGED".equals(paramAnonymousIntent.getAction()))
        {
          paramAnonymousIntent = ScheduleConditionProvider.-get0(ScheduleConditionProvider.this).keySet().iterator();
          while (paramAnonymousIntent.hasNext())
          {
            Object localObject = (Uri)paramAnonymousIntent.next();
            localObject = (ScheduleCalendar)ScheduleConditionProvider.-get0(ScheduleConditionProvider.this).get(localObject);
            if (localObject != null) {
              ((ScheduleCalendar)localObject).setTimeZone(Calendar.getInstance().getTimeZone());
            }
          }
        }
      }
      ScheduleConditionProvider.-wrap0(ScheduleConditionProvider.this);
    }
  };
  private boolean mRegistered;
  private ArraySet<Uri> mSnoozed = new ArraySet();
  private final ArrayMap<Uri, ScheduleCalendar> mSubscriptions = new ArrayMap();
  
  static
  {
    COMPONENT = new ComponentName("android", ScheduleConditionProvider.class.getName());
    SIMPLE_NAME = ScheduleConditionProvider.class.getSimpleName();
  }
  
  public ScheduleConditionProvider()
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.SCP", "new " + SIMPLE_NAME + "()");
    }
  }
  
  private void addSnoozed(Uri paramUri)
  {
    synchronized (this.mSnoozed)
    {
      this.mSnoozed.add(paramUri);
      saveSnoozedLocked();
      return;
    }
  }
  
  private boolean conditionSnoozed(Uri paramUri)
  {
    synchronized (this.mSnoozed)
    {
      boolean bool = this.mSnoozed.contains(paramUri);
      return bool;
    }
  }
  
  private Condition createCondition(Uri paramUri, int paramInt)
  {
    return new Condition(paramUri, "...", "...", "...", 0, paramInt, 2);
  }
  
  private void evaluateSubscriptions()
  {
    if (this.mAlarmManager == null) {
      this.mAlarmManager = ((AlarmManager)this.mContext.getSystemService("alarm"));
    }
    long l1 = System.currentTimeMillis();
    label186:
    label198:
    label309:
    for (;;)
    {
      long l2;
      Uri localUri;
      ScheduleCalendar localScheduleCalendar;
      synchronized (this.mSubscriptions)
      {
        if (this.mSubscriptions.isEmpty())
        {
          bool = false;
          setRegistered(bool);
          this.mNextAlarmTime = 0L;
          l2 = getNextAlarm();
          Iterator localIterator = this.mSubscriptions.keySet().iterator();
          if (!localIterator.hasNext()) {
            break;
          }
          localUri = (Uri)localIterator.next();
          localScheduleCalendar = (ScheduleCalendar)this.mSubscriptions.get(localUri);
          if ((localScheduleCalendar == null) || (!localScheduleCalendar.isInSchedule(l1))) {
            break label198;
          }
          if ((!conditionSnoozed(localUri)) && (!localScheduleCalendar.shouldExitForAlarm(l1))) {
            break label186;
          }
          notifyCondition(localUri, 0, "alarmCanceled");
          addSnoozed(localUri);
          localScheduleCalendar.maybeSetNextAlarm(l1, l2);
        }
      }
      boolean bool = true;
      continue;
      notifyCondition(localUri, 1, "meetsSchedule");
      continue;
      notifyCondition(localUri, 0, "!meetsSchedule");
      removeSnoozed(localUri);
      if (l2 == 0L) {
        localScheduleCalendar.maybeSetNextAlarm(l1, l2);
      }
      for (;;)
      {
        if (localScheduleCalendar == null) {
          break label309;
        }
        long l3 = localScheduleCalendar.getNextChangeTime(l1);
        if ((l3 <= 0L) || (l3 <= l1) || ((this.mNextAlarmTime != 0L) && (l3 >= this.mNextAlarmTime))) {
          break;
        }
        this.mNextAlarmTime = l3;
        break;
        notifyCondition(localUri, 0, "!meetsSchedule");
        if ((localScheduleCalendar != null) && (l2 == 0L)) {
          localScheduleCalendar.maybeSetNextAlarm(l1, l2);
        }
      }
    }
    updateAlarm(l1, this.mNextAlarmTime);
  }
  
  private boolean meetsSchedule(ScheduleCalendar paramScheduleCalendar, long paramLong)
  {
    if (paramScheduleCalendar != null) {
      return paramScheduleCalendar.isInSchedule(paramLong);
    }
    return false;
  }
  
  private void notifyCondition(Uri paramUri, int paramInt, String paramString)
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.SCP", "notifyCondition " + paramUri + " " + Condition.stateToString(paramInt) + " reason=" + paramString);
    }
    notifyCondition(createCondition(paramUri, paramInt));
  }
  
  private void removeSnoozed(Uri paramUri)
  {
    synchronized (this.mSnoozed)
    {
      this.mSnoozed.remove(paramUri);
      saveSnoozedLocked();
      return;
    }
  }
  
  private void setRegistered(boolean paramBoolean)
  {
    if (this.mRegistered == paramBoolean) {
      return;
    }
    if (DEBUG) {
      Slog.d("ConditionProviders.SCP", "setRegistered " + paramBoolean);
    }
    this.mRegistered = paramBoolean;
    if (this.mRegistered)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.TIME_SET");
      localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
      localIntentFilter.addAction(ACTION_EVALUATE);
      localIntentFilter.addAction("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
      registerReceiver(this.mReceiver, localIntentFilter);
      return;
    }
    unregisterReceiver(this.mReceiver);
  }
  
  private static ScheduleCalendar toScheduleCalendar(Uri paramUri)
  {
    paramUri = ZenModeConfig.tryParseScheduleConditionId(paramUri);
    if ((paramUri == null) || (paramUri.days == null)) {}
    while (paramUri.days.length == 0) {
      return null;
    }
    ScheduleCalendar localScheduleCalendar = new ScheduleCalendar();
    localScheduleCalendar.setSchedule(paramUri);
    localScheduleCalendar.setTimeZone(TimeZone.getDefault());
    return localScheduleCalendar;
  }
  
  private void updateAlarm(long paramLong1, long paramLong2)
  {
    AlarmManager localAlarmManager = (AlarmManager)this.mContext.getSystemService("alarm");
    PendingIntent localPendingIntent = PendingIntent.getBroadcast(this.mContext, 1, new Intent(ACTION_EVALUATE).addFlags(268435456).putExtra("time", paramLong2), 134217728);
    localAlarmManager.cancel(localPendingIntent);
    if (paramLong2 > paramLong1)
    {
      if (DEBUG) {
        Slog.d("ConditionProviders.SCP", String.format("Scheduling evaluate for %s, in %s, now=%s", new Object[] { ts(paramLong2), formatDuration(paramLong2 - paramLong1), ts(paramLong1) }));
      }
      localAlarmManager.setExact(0, paramLong2, localPendingIntent);
    }
    while (!DEBUG) {
      return;
    }
    Slog.d("ConditionProviders.SCP", "Not scheduling evaluate");
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
    paramPrintWriter.print("    ");
    paramPrintWriter.print(SIMPLE_NAME);
    paramPrintWriter.println(":");
    paramPrintWriter.print("      mConnected=");
    paramPrintWriter.println(this.mConnected);
    paramPrintWriter.print("      mRegistered=");
    paramPrintWriter.println(this.mRegistered);
    paramPrintWriter.println("      mSubscriptions=");
    long l = System.currentTimeMillis();
    for (;;)
    {
      synchronized (this.mSubscriptions)
      {
        Iterator localIterator = this.mSubscriptions.keySet().iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        Uri localUri = (Uri)localIterator.next();
        paramPrintWriter.print("        ");
        if (meetsSchedule((ScheduleCalendar)this.mSubscriptions.get(localUri), l))
        {
          paramDumpFilter = "* ";
          paramPrintWriter.print(paramDumpFilter);
          paramPrintWriter.println(localUri);
          paramPrintWriter.print("            ");
          paramPrintWriter.println(((ScheduleCalendar)this.mSubscriptions.get(localUri)).toString());
        }
      }
      paramDumpFilter = "  ";
    }
    paramPrintWriter.println("      snoozed due to alarm: " + TextUtils.join(";", this.mSnoozed));
    dumpUpcomingTime(paramPrintWriter, "mNextAlarmTime", this.mNextAlarmTime, l);
  }
  
  public ComponentName getComponent()
  {
    return COMPONENT;
  }
  
  public long getNextAlarm()
  {
    AlarmManager.AlarmClockInfo localAlarmClockInfo = this.mAlarmManager.getNextAlarmClock(ActivityManager.getCurrentUser());
    if (localAlarmClockInfo != null) {
      return localAlarmClockInfo.getTriggerTime();
    }
    return 0L;
  }
  
  public boolean isValidConditionId(Uri paramUri)
  {
    return ZenModeConfig.isValidScheduleConditionId(paramUri);
  }
  
  public void onBootComplete() {}
  
  public void onConnected()
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.SCP", "onConnected");
    }
    this.mConnected = true;
    readSnoozed();
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (DEBUG) {
      Slog.d("ConditionProviders.SCP", "onDestroy");
    }
    this.mConnected = false;
  }
  
  public void onSubscribe(Uri paramUri)
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.SCP", "onSubscribe " + paramUri);
    }
    if (!ZenModeConfig.isValidScheduleConditionId(paramUri))
    {
      notifyCondition(paramUri, 0, "badCondition");
      return;
    }
    synchronized (this.mSubscriptions)
    {
      this.mSubscriptions.put(paramUri, toScheduleCalendar(paramUri));
      evaluateSubscriptions();
      return;
    }
  }
  
  public void onUnsubscribe(Uri paramUri)
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.SCP", "onUnsubscribe " + paramUri);
    }
    synchronized (this.mSubscriptions)
    {
      this.mSubscriptions.remove(paramUri);
      removeSnoozed(paramUri);
      evaluateSubscriptions();
      return;
    }
  }
  
  public void readSnoozed()
  {
    for (;;)
    {
      long l;
      int i;
      synchronized (this.mSnoozed)
      {
        l = Binder.clearCallingIdentity();
        try
        {
          Object localObject1 = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "snoozed_schedule_condition_provider", ActivityManager.getCurrentUser());
          if (localObject1 != null)
          {
            String[] arrayOfString = ((String)localObject1).split(";");
            i = 0;
            if (i < arrayOfString.length)
            {
              String str = arrayOfString[i];
              localObject1 = str;
              if (str != null) {
                localObject1 = str.trim();
              }
              if (TextUtils.isEmpty((CharSequence)localObject1)) {
                break label127;
              }
              this.mSnoozed.add(Uri.parse((String)localObject1));
            }
          }
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
      Binder.restoreCallingIdentity(l);
      return;
      label127:
      i += 1;
    }
  }
  
  public void saveSnoozedLocked()
  {
    String str = TextUtils.join(";", this.mSnoozed);
    int i = ActivityManager.getCurrentUser();
    Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "snoozed_schedule_condition_provider", str, i);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/ScheduleConditionProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */