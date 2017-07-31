package com.android.server.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.EventInfo;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventConditionProvider
  extends SystemConditionProviderService
{
  private static final String ACTION_EVALUATE = SIMPLE_NAME + ".EVALUATE";
  private static final long CHANGE_DELAY = 2000L;
  public static final ComponentName COMPONENT;
  private static final boolean DEBUG = Log.isLoggable("ConditionProviders", 3);
  private static final String EXTRA_TIME = "time";
  private static final String NOT_SHOWN = "...";
  private static final int REQUEST_CODE_EVALUATE = 1;
  private static final String SIMPLE_NAME;
  private static final String TAG = "ConditionProviders.ECP";
  private boolean mBootComplete;
  private boolean mConnected;
  private final Context mContext = this;
  private final Runnable mEvaluateSubscriptionsW = new Runnable()
  {
    public void run()
    {
      EventConditionProvider.-wrap0(EventConditionProvider.this);
    }
  };
  private long mNextAlarmTime;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (EventConditionProvider.-get0()) {
        Slog.d("ConditionProviders.ECP", "onReceive " + paramAnonymousIntent.getAction());
      }
      EventConditionProvider.-wrap1(EventConditionProvider.this);
    }
  };
  private boolean mRegistered;
  private final ArraySet<Uri> mSubscriptions = new ArraySet();
  private final HandlerThread mThread;
  private final CalendarTracker.Callback mTrackerCallback = new CalendarTracker.Callback()
  {
    public void onChanged()
    {
      if (EventConditionProvider.-get0()) {
        Slog.d("ConditionProviders.ECP", "mTrackerCallback.onChanged");
      }
      EventConditionProvider.-get2(EventConditionProvider.this).removeCallbacks(EventConditionProvider.-get1(EventConditionProvider.this));
      EventConditionProvider.-get2(EventConditionProvider.this).postDelayed(EventConditionProvider.-get1(EventConditionProvider.this), 2000L);
    }
  };
  private final SparseArray<CalendarTracker> mTrackers = new SparseArray();
  private final Handler mWorker;
  
  static
  {
    COMPONENT = new ComponentName("android", EventConditionProvider.class.getName());
    SIMPLE_NAME = EventConditionProvider.class.getSimpleName();
  }
  
  public EventConditionProvider()
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.ECP", "new " + SIMPLE_NAME + "()");
    }
    this.mThread = new HandlerThread("ConditionProviders.ECP", 10);
    this.mThread.start();
    this.mWorker = new Handler(this.mThread.getLooper());
  }
  
  private Condition createCondition(Uri paramUri, int paramInt)
  {
    return new Condition(paramUri, "...", "...", "...", 0, paramInt, 2);
  }
  
  private void evaluateSubscriptions()
  {
    if (!this.mWorker.hasCallbacks(this.mEvaluateSubscriptionsW)) {
      this.mWorker.post(this.mEvaluateSubscriptionsW);
    }
  }
  
  private void evaluateSubscriptionsW()
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.ECP", "evaluateSubscriptions");
    }
    if (!this.mBootComplete)
    {
      if (DEBUG) {
        Slog.d("ConditionProviders.ECP", "Skipping evaluate before boot complete");
      }
      return;
    }
    long l3 = System.currentTimeMillis();
    ArrayList localArrayList = new ArrayList();
    ArraySet localArraySet = this.mSubscriptions;
    int i = 0;
    Object localObject3;
    long l1;
    Uri localUri;
    ZenModeConfig.EventInfo localEventInfo;
    for (;;)
    {
      try
      {
        if (i < this.mTrackers.size())
        {
          localObject3 = (CalendarTracker)this.mTrackers.valueAt(i);
          CalendarTracker.Callback localCallback;
          if (this.mSubscriptions.isEmpty())
          {
            localCallback = null;
            ((CalendarTracker)localObject3).setCallback(localCallback);
            i += 1;
          }
          else
          {
            localCallback = this.mTrackerCallback;
          }
        }
        else
        {
          if (this.mSubscriptions.isEmpty())
          {
            bool = false;
            setRegistered(bool);
            l1 = 0L;
            Iterator localIterator = this.mSubscriptions.iterator();
            if (!localIterator.hasNext()) {
              break label487;
            }
            localUri = (Uri)localIterator.next();
            localEventInfo = ZenModeConfig.tryParseEventConditionId(localUri);
            if (localEventInfo != null) {
              break;
            }
            localArrayList.add(createCondition(localUri, 0));
            continue;
          }
          boolean bool = true;
        }
      }
      finally {}
    }
    Object localObject2 = null;
    if (localEventInfo.calendar == null) {
      i = 0;
    }
    for (;;)
    {
      localObject3 = localObject2;
      if (i < this.mTrackers.size())
      {
        localObject3 = ((CalendarTracker)this.mTrackers.valueAt(i)).checkEvent(localEventInfo, l3);
        if (localObject2 == null)
        {
          localObject2 = localObject3;
          break label580;
        }
        ((CalendarTracker.CheckEventResult)localObject2).inEvent |= ((CalendarTracker.CheckEventResult)localObject3).inEvent;
        ((CalendarTracker.CheckEventResult)localObject2).recheckAt = Math.min(((CalendarTracker.CheckEventResult)localObject2).recheckAt, ((CalendarTracker.CheckEventResult)localObject3).recheckAt);
        break label580;
        i = ZenModeConfig.EventInfo.resolveUserId(localEventInfo.userId);
        localObject2 = (CalendarTracker)this.mTrackers.get(i);
        if (localObject2 == null)
        {
          Slog.w("ConditionProviders.ECP", "No calendar tracker found for user " + i);
          localArrayList.add(createCondition(localUri, 0));
          break;
        }
        localObject3 = ((CalendarTracker)localObject2).checkEvent(localEventInfo, l3);
      }
      long l2 = l1;
      if (((CalendarTracker.CheckEventResult)localObject3).recheckAt != 0L) {
        if (l1 != 0L)
        {
          l2 = l1;
          if (((CalendarTracker.CheckEventResult)localObject3).recheckAt >= l1) {}
        }
        else
        {
          l2 = ((CalendarTracker.CheckEventResult)localObject3).recheckAt;
        }
      }
      if (!((CalendarTracker.CheckEventResult)localObject3).inEvent)
      {
        localArrayList.add(createCondition(localUri, 0));
        l1 = l2;
        break;
      }
      localArrayList.add(createCondition(localUri, 1));
      l1 = l2;
      break;
      label487:
      rescheduleAlarm(l3, l1);
      localObject2 = localArrayList.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Condition)((Iterator)localObject2).next();
        if (localObject3 != null) {
          notifyCondition((Condition)localObject3);
        }
      }
      if (DEBUG) {
        Slog.d("ConditionProviders.ECP", "evaluateSubscriptions took " + (System.currentTimeMillis() - l3));
      }
      return;
      label580:
      i += 1;
    }
  }
  
  private static Context getContextForUser(Context paramContext, UserHandle paramUserHandle)
  {
    try
    {
      paramContext = paramContext.createPackageContextAsUser(paramContext.getPackageName(), 0, paramUserHandle);
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return null;
  }
  
  private void reloadTrackers()
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.ECP", "reloadTrackers");
    }
    int i = 0;
    while (i < this.mTrackers.size())
    {
      ((CalendarTracker)this.mTrackers.valueAt(i)).setCallback(null);
      i += 1;
    }
    this.mTrackers.clear();
    Iterator localIterator = UserManager.get(this.mContext).getUserProfiles().iterator();
    while (localIterator.hasNext())
    {
      UserHandle localUserHandle = (UserHandle)localIterator.next();
      if (localUserHandle.isSystem()) {}
      for (Context localContext = this.mContext;; localContext = getContextForUser(this.mContext, localUserHandle))
      {
        if (localContext != null) {
          break label156;
        }
        Slog.w("ConditionProviders.ECP", "Unable to create context for user " + localUserHandle.getIdentifier());
        break;
      }
      label156:
      this.mTrackers.put(localUserHandle.getIdentifier(), new CalendarTracker(this.mContext, localContext));
    }
    evaluateSubscriptions();
  }
  
  private void rescheduleAlarm(long paramLong1, long paramLong2)
  {
    this.mNextAlarmTime = paramLong2;
    Object localObject1 = (AlarmManager)this.mContext.getSystemService("alarm");
    Object localObject2 = PendingIntent.getBroadcast(this.mContext, 1, new Intent(ACTION_EVALUATE).addFlags(268435456).putExtra("time", paramLong2), 134217728);
    ((AlarmManager)localObject1).cancel((PendingIntent)localObject2);
    if ((paramLong2 == 0L) || (paramLong2 < paramLong1))
    {
      if (DEBUG)
      {
        localObject2 = new StringBuilder().append("Not scheduling evaluate: ");
        if (paramLong2 != 0L) {
          break label123;
        }
      }
      label123:
      for (localObject1 = "no time specified";; localObject1 = "specified time in the past")
      {
        Slog.d("ConditionProviders.ECP", (String)localObject1);
        return;
      }
    }
    if (DEBUG) {
      Slog.d("ConditionProviders.ECP", String.format("Scheduling evaluate for %s, in %s, now=%s", new Object[] { ts(paramLong2), formatDuration(paramLong2 - paramLong1), ts(paramLong1) }));
    }
    ((AlarmManager)localObject1).setExact(0, paramLong2, (PendingIntent)localObject2);
  }
  
  private void setRegistered(boolean paramBoolean)
  {
    if (this.mRegistered == paramBoolean) {
      return;
    }
    if (DEBUG) {
      Slog.d("ConditionProviders.ECP", "setRegistered " + paramBoolean);
    }
    this.mRegistered = paramBoolean;
    if (this.mRegistered)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.TIME_SET");
      localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
      localIntentFilter.addAction(ACTION_EVALUATE);
      registerReceiver(this.mReceiver, localIntentFilter);
      return;
    }
    unregisterReceiver(this.mReceiver);
  }
  
  public IConditionProvider asInterface()
  {
    return (IConditionProvider)onBind(null);
  }
  
  public void attachBase(Context paramContext)
  {
    attachBaseContext(paramContext);
  }
  
  public void dump(PrintWriter paramPrintWriter, NotificationManagerService.DumpFilter arg2)
  {
    paramPrintWriter.print("    ");
    paramPrintWriter.print(SIMPLE_NAME);
    paramPrintWriter.println(":");
    paramPrintWriter.print("      mConnected=");
    paramPrintWriter.println(this.mConnected);
    paramPrintWriter.print("      mRegistered=");
    paramPrintWriter.println(this.mRegistered);
    paramPrintWriter.print("      mBootComplete=");
    paramPrintWriter.println(this.mBootComplete);
    dumpUpcomingTime(paramPrintWriter, "mNextAlarmTime", this.mNextAlarmTime, System.currentTimeMillis());
    synchronized (this.mSubscriptions)
    {
      paramPrintWriter.println("      mSubscriptions=");
      Iterator localIterator = this.mSubscriptions.iterator();
      if (localIterator.hasNext())
      {
        Uri localUri = (Uri)localIterator.next();
        paramPrintWriter.print("        ");
        paramPrintWriter.println(localUri);
      }
    }
    paramPrintWriter.println("      mTrackers=");
    int i = 0;
    while (i < this.mTrackers.size())
    {
      paramPrintWriter.print("        user=");
      paramPrintWriter.println(this.mTrackers.keyAt(i));
      ((CalendarTracker)this.mTrackers.valueAt(i)).dump("          ", paramPrintWriter);
      i += 1;
    }
  }
  
  public ComponentName getComponent()
  {
    return COMPONENT;
  }
  
  public boolean isValidConditionId(Uri paramUri)
  {
    return ZenModeConfig.isValidEventConditionId(paramUri);
  }
  
  public void onBootComplete()
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.ECP", "onBootComplete");
    }
    if (this.mBootComplete) {
      return;
    }
    this.mBootComplete = true;
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
    localIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
    this.mContext.registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        EventConditionProvider.-wrap2(EventConditionProvider.this);
      }
    }, localIntentFilter);
    reloadTrackers();
  }
  
  public void onConnected()
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.ECP", "onConnected");
    }
    this.mConnected = true;
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (DEBUG) {
      Slog.d("ConditionProviders.ECP", "onDestroy");
    }
    this.mConnected = false;
  }
  
  public void onSubscribe(Uri paramUri)
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.ECP", "onSubscribe " + paramUri);
    }
    if (!ZenModeConfig.isValidEventConditionId(paramUri))
    {
      notifyCondition(createCondition(paramUri, 0));
      return;
    }
    synchronized (this.mSubscriptions)
    {
      if (this.mSubscriptions.add(paramUri)) {
        evaluateSubscriptions();
      }
      return;
    }
  }
  
  public void onUnsubscribe(Uri paramUri)
  {
    if (DEBUG) {
      Slog.d("ConditionProviders.ECP", "onUnsubscribe " + paramUri);
    }
    synchronized (this.mSubscriptions)
    {
      if (this.mSubscriptions.remove(paramUri)) {
        evaluateSubscriptions();
      }
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/EventConditionProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */