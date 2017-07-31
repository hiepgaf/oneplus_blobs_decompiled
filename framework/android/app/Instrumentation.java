package android.app;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;
import android.os.PerformanceCollector;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.util.SeempLog;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import com.android.internal.content.ReferrerIntent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Instrumentation
{
  public static final String REPORT_KEY_IDENTIFIER = "id";
  public static final String REPORT_KEY_STREAMRESULT = "stream";
  private static final String TAG = "Instrumentation";
  private List<ActivityMonitor> mActivityMonitors;
  private Context mAppContext;
  private boolean mAutomaticPerformanceSnapshots = false;
  private ComponentName mComponent;
  private Context mInstrContext;
  private MessageQueue mMessageQueue = null;
  private Bundle mPerfMetrics = new Bundle();
  private PerformanceCollector mPerformanceCollector;
  private Thread mRunner;
  private final Object mSync = new Object();
  private ActivityThread mThread = null;
  private UiAutomation mUiAutomation;
  private IUiAutomationConnection mUiAutomationConnection;
  private List<ActivityWaiter> mWaitingActivities;
  private IInstrumentationWatcher mWatcher;
  
  private void addValue(String paramString, int paramInt, Bundle paramBundle)
  {
    if (paramBundle.containsKey(paramString))
    {
      paramString = paramBundle.getIntegerArrayList(paramString);
      if (paramString != null) {
        paramString.add(Integer.valueOf(paramInt));
      }
      return;
    }
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(Integer.valueOf(paramInt));
    paramBundle.putIntegerArrayList(paramString, localArrayList);
  }
  
  public static void checkStartActivityResult(int paramInt, Object paramObject)
  {
    if (paramInt >= 0) {
      return;
    }
    switch (paramInt)
    {
    case -8: 
    default: 
      throw new AndroidRuntimeException("Unknown error code " + paramInt + " when starting " + paramObject);
    case -2: 
    case -1: 
      if (((paramObject instanceof Intent)) && (((Intent)paramObject).getComponent() != null)) {
        throw new ActivityNotFoundException("Unable to find explicit activity class " + ((Intent)paramObject).getComponent().toShortString() + "; have you declared this activity in your AndroidManifest.xml?");
      }
      throw new ActivityNotFoundException("No Activity found to handle " + paramObject);
    case -4: 
      throw new SecurityException("Not allowed to start activity " + paramObject);
    case -3: 
      throw new AndroidRuntimeException("FORWARD_RESULT_FLAG used while also requesting a result");
    case -5: 
      throw new IllegalArgumentException("PendingIntent is not an activity");
    case -7: 
      throw new SecurityException("Starting under voice control not allowed for: " + paramObject);
    case -9: 
      throw new IllegalStateException("Session calling startVoiceActivity does not match active session");
    case -10: 
      throw new IllegalStateException("Cannot start voice activity on a hidden session");
    }
    throw new AndroidRuntimeException("Activity could not be started for " + paramObject);
  }
  
  public static Application newApplication(Class<?> paramClass, Context paramContext)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    paramClass = (Application)paramClass.newInstance();
    paramClass.attach(paramContext);
    return paramClass;
  }
  
  private void postPerformCreate(Activity paramActivity)
  {
    if (this.mActivityMonitors != null) {}
    synchronized (this.mSync)
    {
      int j = this.mActivityMonitors.size();
      int i = 0;
      while (i < j)
      {
        ((ActivityMonitor)this.mActivityMonitors.get(i)).match(paramActivity, paramActivity, paramActivity.getIntent());
        i += 1;
      }
      return;
    }
  }
  
  private void prePerformCreate(Activity paramActivity)
  {
    if (this.mWaitingActivities != null) {}
    synchronized (this.mSync)
    {
      int j = this.mWaitingActivities.size();
      int i = 0;
      while (i < j)
      {
        ActivityWaiter localActivityWaiter = (ActivityWaiter)this.mWaitingActivities.get(i);
        if (localActivityWaiter.intent.filterEquals(paramActivity.getIntent()))
        {
          localActivityWaiter.activity = paramActivity;
          this.mMessageQueue.addIdleHandler(new ActivityGoing(localActivityWaiter));
        }
        i += 1;
      }
      return;
    }
  }
  
  private final void validateNotAppThread()
  {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      throw new RuntimeException("This method can not be called from the main application thread");
    }
  }
  
  public ActivityMonitor addMonitor(IntentFilter paramIntentFilter, ActivityResult paramActivityResult, boolean paramBoolean)
  {
    paramIntentFilter = new ActivityMonitor(paramIntentFilter, paramActivityResult, paramBoolean);
    addMonitor(paramIntentFilter);
    return paramIntentFilter;
  }
  
  public ActivityMonitor addMonitor(String paramString, ActivityResult paramActivityResult, boolean paramBoolean)
  {
    paramString = new ActivityMonitor(paramString, paramActivityResult, paramBoolean);
    addMonitor(paramString);
    return paramString;
  }
  
  public void addMonitor(ActivityMonitor paramActivityMonitor)
  {
    synchronized (this.mSync)
    {
      if (this.mActivityMonitors == null) {
        this.mActivityMonitors = new ArrayList();
      }
      this.mActivityMonitors.add(paramActivityMonitor);
      return;
    }
  }
  
  public void callActivityOnCreate(Activity paramActivity, Bundle paramBundle)
  {
    prePerformCreate(paramActivity);
    paramActivity.performCreate(paramBundle);
    postPerformCreate(paramActivity);
  }
  
  public void callActivityOnCreate(Activity paramActivity, Bundle paramBundle, PersistableBundle paramPersistableBundle)
  {
    prePerformCreate(paramActivity);
    paramActivity.performCreate(paramBundle, paramPersistableBundle);
    postPerformCreate(paramActivity);
  }
  
  public void callActivityOnDestroy(Activity paramActivity)
  {
    paramActivity.performDestroy();
    if (this.mActivityMonitors != null) {}
    synchronized (this.mSync)
    {
      int j = this.mActivityMonitors.size();
      int i = 0;
      while (i < j)
      {
        ((ActivityMonitor)this.mActivityMonitors.get(i)).match(paramActivity, paramActivity, paramActivity.getIntent());
        i += 1;
      }
      return;
    }
  }
  
  public void callActivityOnNewIntent(Activity paramActivity, Intent paramIntent)
  {
    long l = SystemClock.uptimeMillis();
    paramActivity.onNewIntent(paramIntent);
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " onNewIntent in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callActivityOnNewIntent(Activity paramActivity, ReferrerIntent paramReferrerIntent)
  {
    Intent localIntent = null;
    String str = paramActivity.mReferrer;
    if (paramReferrerIntent != null) {}
    try
    {
      paramActivity.mReferrer = paramReferrerIntent.mReferrer;
      if (paramReferrerIntent != null) {
        localIntent = new Intent(paramReferrerIntent);
      }
      callActivityOnNewIntent(paramActivity, localIntent);
      return;
    }
    finally
    {
      paramActivity.mReferrer = str;
    }
  }
  
  public void callActivityOnPause(Activity paramActivity)
  {
    long l = SystemClock.uptimeMillis();
    paramActivity.performPause();
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " performPause in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callActivityOnPostCreate(Activity paramActivity, Bundle paramBundle)
  {
    long l = SystemClock.uptimeMillis();
    paramActivity.onPostCreate(paramBundle);
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " onPostCreate in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callActivityOnPostCreate(Activity paramActivity, Bundle paramBundle, PersistableBundle paramPersistableBundle)
  {
    long l = SystemClock.uptimeMillis();
    paramActivity.onPostCreate(paramBundle, paramPersistableBundle);
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " onPostCreate2 in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callActivityOnRestart(Activity paramActivity)
  {
    long l = SystemClock.uptimeMillis();
    paramActivity.onRestart();
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " onRestart in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callActivityOnRestoreInstanceState(Activity paramActivity, Bundle paramBundle)
  {
    long l = SystemClock.uptimeMillis();
    paramActivity.performRestoreInstanceState(paramBundle);
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " performRestoreInstanceState in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callActivityOnRestoreInstanceState(Activity paramActivity, Bundle paramBundle, PersistableBundle paramPersistableBundle)
  {
    long l = SystemClock.uptimeMillis();
    paramActivity.performRestoreInstanceState(paramBundle, paramPersistableBundle);
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " performRestoreInstanceState2 in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callActivityOnResume(Activity paramActivity)
  {
    paramActivity.mResumed = true;
    long l = SystemClock.uptimeMillis();
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " onResume begin");
    }
    paramActivity.onResume();
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " onResume in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
    if (this.mActivityMonitors != null) {}
    synchronized (this.mSync)
    {
      int j = this.mActivityMonitors.size();
      int i = 0;
      while (i < j)
      {
        ((ActivityMonitor)this.mActivityMonitors.get(i)).match(paramActivity, paramActivity, paramActivity.getIntent());
        i += 1;
      }
      return;
    }
  }
  
  public void callActivityOnSaveInstanceState(Activity paramActivity, Bundle paramBundle)
  {
    long l = SystemClock.uptimeMillis();
    paramActivity.performSaveInstanceState(paramBundle);
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " performSaveInstanceState in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callActivityOnSaveInstanceState(Activity paramActivity, Bundle paramBundle, PersistableBundle paramPersistableBundle)
  {
    long l = SystemClock.uptimeMillis();
    paramActivity.performSaveInstanceState(paramBundle, paramPersistableBundle);
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " performSaveInstanceState2 in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callActivityOnStart(Activity paramActivity)
  {
    long l = SystemClock.uptimeMillis();
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " onStart begin");
    }
    paramActivity.onStart();
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " onStart in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callActivityOnStop(Activity paramActivity)
  {
    long l = SystemClock.uptimeMillis();
    paramActivity.onStop();
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " onStop in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callActivityOnUserLeaving(Activity paramActivity)
  {
    long l = SystemClock.uptimeMillis();
    paramActivity.performUserLeaving();
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramActivity + " performUserLeaving in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public void callApplicationOnCreate(Application paramApplication)
  {
    long l = SystemClock.uptimeMillis();
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramApplication + " onCreate begin");
    }
    paramApplication.onCreate();
    if (ActivityThread.DEBUG_ONEPLUS) {
      Log.i("Instrumentation", paramApplication + " onCreate in " + (SystemClock.uptimeMillis() - l) + "ms");
    }
  }
  
  public boolean checkMonitorHit(ActivityMonitor paramActivityMonitor, int paramInt)
  {
    waitForIdleSync();
    synchronized (this.mSync)
    {
      int i = paramActivityMonitor.getHits();
      if (i < paramInt) {
        return false;
      }
      this.mActivityMonitors.remove(paramActivityMonitor);
      return true;
    }
  }
  
  public void endPerformanceSnapshot()
  {
    if (!isProfiling()) {
      this.mPerfMetrics = this.mPerformanceCollector.endSnapshot();
    }
  }
  
  public void execStartActivities(Context paramContext, IBinder paramIBinder1, IBinder paramIBinder2, Activity paramActivity, Intent[] paramArrayOfIntent, Bundle paramBundle)
  {
    execStartActivitiesAsUser(paramContext, paramIBinder1, paramIBinder2, paramActivity, paramArrayOfIntent, paramBundle, UserHandle.myUserId());
  }
  
  public void execStartActivitiesAsUser(Context paramContext, IBinder paramIBinder1, IBinder paramIBinder2, Activity arg4, Intent[] paramArrayOfIntent, Bundle paramBundle, int paramInt)
  {
    SeempLog.record_str(378, paramArrayOfIntent.toString());
    paramIBinder1 = (IApplicationThread)paramIBinder1;
    int i;
    if (this.mActivityMonitors != null) {
      synchronized (this.mSync)
      {
        int j = this.mActivityMonitors.size();
        i = 0;
        while (i < j)
        {
          ActivityMonitor localActivityMonitor = (ActivityMonitor)this.mActivityMonitors.get(i);
          if (localActivityMonitor.match(paramContext, null, paramArrayOfIntent[0]))
          {
            localActivityMonitor.mHits += 1;
            boolean bool = localActivityMonitor.isBlocking();
            if (!bool) {
              break;
            }
            return;
          }
          i += 1;
        }
      }
    }
    try
    {
      ??? = new String[paramArrayOfIntent.length];
      i = 0;
      while (i < paramArrayOfIntent.length)
      {
        paramArrayOfIntent[i].migrateExtraStreamToClipData();
        paramArrayOfIntent[i].prepareToLeaveProcess(paramContext);
        ???[i] = paramArrayOfIntent[i].resolveTypeIfNeeded(paramContext.getContentResolver());
        i += 1;
        continue;
        paramContext = finally;
        throw paramContext;
      }
      checkStartActivityResult(ActivityManagerNative.getDefault().startActivities(paramIBinder1, paramContext.getBasePackageName(), paramArrayOfIntent, ???, paramIBinder2, paramBundle, paramInt), paramArrayOfIntent[0]);
      return;
    }
    catch (RemoteException paramContext)
    {
      throw new RuntimeException("Failure from system", paramContext);
    }
  }
  
  /* Error */
  public ActivityResult execStartActivity(Context paramContext, IBinder paramIBinder1, IBinder paramIBinder2, Activity paramActivity, Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    // Byte code:
    //   0: sipush 377
    //   3: aload 5
    //   5: invokevirtual 532	android/content/Intent:toString	()Ljava/lang/String;
    //   8: invokestatic 480	android/util/SeempLog:record_str	(ILjava/lang/String;)I
    //   11: pop
    //   12: iconst_1
    //   13: newarray <illegal type>
    //   15: dup
    //   16: iconst_0
    //   17: bipush 12
    //   19: iastore
    //   20: invokestatic 538	android/util/OpFeatures:isSupport	([I)Z
    //   23: ifeq +97 -> 120
    //   26: aload 5
    //   28: ifnull +92 -> 120
    //   31: aload 5
    //   33: invokevirtual 541	android/content/Intent:getAction	()Ljava/lang/String;
    //   36: ifnull +84 -> 120
    //   39: aconst_null
    //   40: astore 10
    //   42: aload 5
    //   44: invokevirtual 541	android/content/Intent:getAction	()Ljava/lang/String;
    //   47: ldc_w 543
    //   50: invokevirtual 546	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   53: ifeq +31 -> 84
    //   56: ldc_w 548
    //   59: astore 10
    //   61: aload 10
    //   63: ifnull +57 -> 120
    //   66: new 550	android/util/Permission
    //   69: dup
    //   70: aload_1
    //   71: invokespecial 552	android/util/Permission:<init>	(Landroid/content/Context;)V
    //   74: aload 10
    //   76: invokevirtual 555	android/util/Permission:requestPermissionAuto	(Ljava/lang/String;)Z
    //   79: ifne +41 -> 120
    //   82: aconst_null
    //   83: areturn
    //   84: aload 5
    //   86: invokevirtual 541	android/content/Intent:getAction	()Ljava/lang/String;
    //   89: ldc_w 557
    //   92: invokevirtual 546	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   95: ifne +17 -> 112
    //   98: aload 5
    //   100: invokevirtual 541	android/content/Intent:getAction	()Ljava/lang/String;
    //   103: ldc_w 559
    //   106: invokevirtual 546	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   109: ifeq -48 -> 61
    //   112: ldc_w 561
    //   115: astore 10
    //   117: goto -56 -> 61
    //   120: aload_2
    //   121: checkcast 482	android/app/IApplicationThread
    //   124: astore 10
    //   126: aload 4
    //   128: ifnull +121 -> 249
    //   131: aload 4
    //   133: invokevirtual 565	android/app/Activity:onProvideReferrer	()Landroid/net/Uri;
    //   136: astore_2
    //   137: aload_2
    //   138: ifnull +13 -> 151
    //   141: aload 5
    //   143: ldc_w 567
    //   146: aload_2
    //   147: invokevirtual 571	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   150: pop
    //   151: aload_0
    //   152: getfield 228	android/app/Instrumentation:mActivityMonitors	Ljava/util/List;
    //   155: ifnull +115 -> 270
    //   158: aload_0
    //   159: getfield 83	android/app/Instrumentation:mSync	Ljava/lang/Object;
    //   162: astore_2
    //   163: aload_2
    //   164: monitorenter
    //   165: aload_0
    //   166: getfield 228	android/app/Instrumentation:mActivityMonitors	Ljava/util/List;
    //   169: invokeinterface 232 1 0
    //   174: istore 9
    //   176: iconst_0
    //   177: istore 8
    //   179: iload 8
    //   181: iload 9
    //   183: if_icmpge +85 -> 268
    //   186: aload_0
    //   187: getfield 228	android/app/Instrumentation:mActivityMonitors	Ljava/util/List;
    //   190: iload 8
    //   192: invokeinterface 236 2 0
    //   197: checkcast 15	android/app/Instrumentation$ActivityMonitor
    //   200: astore 11
    //   202: aload 11
    //   204: aload_1
    //   205: aconst_null
    //   206: aload 5
    //   208: invokevirtual 246	android/app/Instrumentation$ActivityMonitor:match	(Landroid/content/Context;Landroid/app/Activity;Landroid/content/Intent;)Z
    //   211: ifeq +48 -> 259
    //   214: aload 11
    //   216: aload 11
    //   218: getfield 486	android/app/Instrumentation$ActivityMonitor:mHits	I
    //   221: iconst_1
    //   222: iadd
    //   223: putfield 486	android/app/Instrumentation$ActivityMonitor:mHits	I
    //   226: aload 11
    //   228: invokevirtual 489	android/app/Instrumentation$ActivityMonitor:isBlocking	()Z
    //   231: ifeq +37 -> 268
    //   234: iload 6
    //   236: iflt +18 -> 254
    //   239: aload 11
    //   241: invokevirtual 575	android/app/Instrumentation$ActivityMonitor:getResult	()Landroid/app/Instrumentation$ActivityResult;
    //   244: astore_1
    //   245: aload_2
    //   246: monitorexit
    //   247: aload_1
    //   248: areturn
    //   249: aconst_null
    //   250: astore_2
    //   251: goto -114 -> 137
    //   254: aconst_null
    //   255: astore_1
    //   256: goto -11 -> 245
    //   259: iload 8
    //   261: iconst_1
    //   262: iadd
    //   263: istore 8
    //   265: goto -86 -> 179
    //   268: aload_2
    //   269: monitorexit
    //   270: aload 5
    //   272: invokevirtual 494	android/content/Intent:migrateExtraStreamToClipData	()Z
    //   275: pop
    //   276: aload 5
    //   278: aload_1
    //   279: invokevirtual 497	android/content/Intent:prepareToLeaveProcess	(Landroid/content/Context;)V
    //   282: invokestatic 513	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   285: astore_2
    //   286: aload_1
    //   287: invokevirtual 516	android/content/Context:getBasePackageName	()Ljava/lang/String;
    //   290: astore 11
    //   292: aload 5
    //   294: aload_1
    //   295: invokevirtual 503	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   298: invokevirtual 507	android/content/Intent:resolveTypeIfNeeded	(Landroid/content/ContentResolver;)Ljava/lang/String;
    //   301: astore 12
    //   303: aload 4
    //   305: ifnull +43 -> 348
    //   308: aload 4
    //   310: getfield 578	android/app/Activity:mEmbeddedID	Ljava/lang/String;
    //   313: astore_1
    //   314: aload_2
    //   315: aload 10
    //   317: aload 11
    //   319: aload 5
    //   321: aload 12
    //   323: aload_3
    //   324: aload_1
    //   325: iload 6
    //   327: iconst_0
    //   328: aconst_null
    //   329: aload 7
    //   331: invokeinterface 582 11 0
    //   336: aload 5
    //   338: invokestatic 524	android/app/Instrumentation:checkStartActivityResult	(ILjava/lang/Object;)V
    //   341: aconst_null
    //   342: areturn
    //   343: astore_1
    //   344: aload_2
    //   345: monitorexit
    //   346: aload_1
    //   347: athrow
    //   348: aconst_null
    //   349: astore_1
    //   350: goto -36 -> 314
    //   353: astore_1
    //   354: new 280	java/lang/RuntimeException
    //   357: dup
    //   358: ldc_w 526
    //   361: aload_1
    //   362: invokespecial 529	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   365: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	366	0	this	Instrumentation
    //   0	366	1	paramContext	Context
    //   0	366	2	paramIBinder1	IBinder
    //   0	366	3	paramIBinder2	IBinder
    //   0	366	4	paramActivity	Activity
    //   0	366	5	paramIntent	Intent
    //   0	366	6	paramInt	int
    //   0	366	7	paramBundle	Bundle
    //   177	87	8	i	int
    //   174	10	9	j	int
    //   40	276	10	localObject1	Object
    //   200	118	11	localObject2	Object
    //   301	21	12	str	String
    // Exception table:
    //   from	to	target	type
    //   165	176	343	finally
    //   186	234	343	finally
    //   239	245	343	finally
    //   270	303	353	android/os/RemoteException
    //   308	314	353	android/os/RemoteException
    //   314	341	353	android/os/RemoteException
  }
  
  /* Error */
  public ActivityResult execStartActivity(Context paramContext, IBinder paramIBinder1, IBinder paramIBinder2, Activity paramActivity, Intent paramIntent, int paramInt, Bundle paramBundle, UserHandle paramUserHandle)
  {
    // Byte code:
    //   0: sipush 377
    //   3: aload 5
    //   5: invokevirtual 532	android/content/Intent:toString	()Ljava/lang/String;
    //   8: invokestatic 480	android/util/SeempLog:record_str	(ILjava/lang/String;)I
    //   11: pop
    //   12: aload_2
    //   13: checkcast 482	android/app/IApplicationThread
    //   16: astore_2
    //   17: aload_0
    //   18: getfield 228	android/app/Instrumentation:mActivityMonitors	Ljava/util/List;
    //   21: ifnull +114 -> 135
    //   24: aload_0
    //   25: getfield 83	android/app/Instrumentation:mSync	Ljava/lang/Object;
    //   28: astore 11
    //   30: aload 11
    //   32: monitorenter
    //   33: aload_0
    //   34: getfield 228	android/app/Instrumentation:mActivityMonitors	Ljava/util/List;
    //   37: invokeinterface 232 1 0
    //   42: istore 10
    //   44: iconst_0
    //   45: istore 9
    //   47: iload 9
    //   49: iload 10
    //   51: if_icmpge +81 -> 132
    //   54: aload_0
    //   55: getfield 228	android/app/Instrumentation:mActivityMonitors	Ljava/util/List;
    //   58: iload 9
    //   60: invokeinterface 236 2 0
    //   65: checkcast 15	android/app/Instrumentation$ActivityMonitor
    //   68: astore 12
    //   70: aload 12
    //   72: aload_1
    //   73: aconst_null
    //   74: aload 5
    //   76: invokevirtual 246	android/app/Instrumentation$ActivityMonitor:match	(Landroid/content/Context;Landroid/app/Activity;Landroid/content/Intent;)Z
    //   79: ifeq +44 -> 123
    //   82: aload 12
    //   84: aload 12
    //   86: getfield 486	android/app/Instrumentation$ActivityMonitor:mHits	I
    //   89: iconst_1
    //   90: iadd
    //   91: putfield 486	android/app/Instrumentation$ActivityMonitor:mHits	I
    //   94: aload 12
    //   96: invokevirtual 489	android/app/Instrumentation$ActivityMonitor:isBlocking	()Z
    //   99: ifeq +33 -> 132
    //   102: iload 6
    //   104: iflt +14 -> 118
    //   107: aload 12
    //   109: invokevirtual 575	android/app/Instrumentation$ActivityMonitor:getResult	()Landroid/app/Instrumentation$ActivityResult;
    //   112: astore_1
    //   113: aload 11
    //   115: monitorexit
    //   116: aload_1
    //   117: areturn
    //   118: aconst_null
    //   119: astore_1
    //   120: goto -7 -> 113
    //   123: iload 9
    //   125: iconst_1
    //   126: iadd
    //   127: istore 9
    //   129: goto -82 -> 47
    //   132: aload 11
    //   134: monitorexit
    //   135: aload 5
    //   137: invokevirtual 494	android/content/Intent:migrateExtraStreamToClipData	()Z
    //   140: pop
    //   141: aload 5
    //   143: aload_1
    //   144: invokevirtual 497	android/content/Intent:prepareToLeaveProcess	(Landroid/content/Context;)V
    //   147: invokestatic 513	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   150: astore 11
    //   152: aload_1
    //   153: invokevirtual 516	android/content/Context:getBasePackageName	()Ljava/lang/String;
    //   156: astore 12
    //   158: aload 5
    //   160: aload_1
    //   161: invokevirtual 503	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   164: invokevirtual 507	android/content/Intent:resolveTypeIfNeeded	(Landroid/content/ContentResolver;)Ljava/lang/String;
    //   167: astore 13
    //   169: aload 4
    //   171: ifnull +49 -> 220
    //   174: aload 4
    //   176: getfield 578	android/app/Activity:mEmbeddedID	Ljava/lang/String;
    //   179: astore_1
    //   180: aload 11
    //   182: aload_2
    //   183: aload 12
    //   185: aload 5
    //   187: aload 13
    //   189: aload_3
    //   190: aload_1
    //   191: iload 6
    //   193: iconst_0
    //   194: aconst_null
    //   195: aload 7
    //   197: aload 8
    //   199: invokevirtual 586	android/os/UserHandle:getIdentifier	()I
    //   202: invokeinterface 590 12 0
    //   207: aload 5
    //   209: invokestatic 524	android/app/Instrumentation:checkStartActivityResult	(ILjava/lang/Object;)V
    //   212: aconst_null
    //   213: areturn
    //   214: astore_1
    //   215: aload 11
    //   217: monitorexit
    //   218: aload_1
    //   219: athrow
    //   220: aconst_null
    //   221: astore_1
    //   222: goto -42 -> 180
    //   225: astore_1
    //   226: new 280	java/lang/RuntimeException
    //   229: dup
    //   230: ldc_w 526
    //   233: aload_1
    //   234: invokespecial 529	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   237: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	238	0	this	Instrumentation
    //   0	238	1	paramContext	Context
    //   0	238	2	paramIBinder1	IBinder
    //   0	238	3	paramIBinder2	IBinder
    //   0	238	4	paramActivity	Activity
    //   0	238	5	paramIntent	Intent
    //   0	238	6	paramInt	int
    //   0	238	7	paramBundle	Bundle
    //   0	238	8	paramUserHandle	UserHandle
    //   45	83	9	i	int
    //   42	10	10	j	int
    //   28	188	11	localObject1	Object
    //   68	116	12	localObject2	Object
    //   167	21	13	str	String
    // Exception table:
    //   from	to	target	type
    //   33	44	214	finally
    //   54	102	214	finally
    //   107	113	214	finally
    //   135	169	225	android/os/RemoteException
    //   174	180	225	android/os/RemoteException
    //   180	212	225	android/os/RemoteException
  }
  
  public ActivityResult execStartActivity(Context paramContext, IBinder arg2, IBinder paramIBinder2, String paramString, Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    SeempLog.record_str(377, paramIntent.toString());
    IApplicationThread localIApplicationThread = (IApplicationThread)???;
    if (this.mActivityMonitors != null) {}
    synchronized (this.mSync)
    {
      int j = this.mActivityMonitors.size();
      int i = 0;
      while (i < j)
      {
        ActivityMonitor localActivityMonitor = (ActivityMonitor)this.mActivityMonitors.get(i);
        if (localActivityMonitor.match(paramContext, null, paramIntent))
        {
          localActivityMonitor.mHits += 1;
          if (!localActivityMonitor.isBlocking()) {
            break;
          }
          if (paramInt >= 0) {}
          for (paramContext = localActivityMonitor.getResult();; paramContext = null) {
            return paramContext;
          }
        }
        i += 1;
      }
    }
  }
  
  /* Error */
  public ActivityResult execStartActivityAsCaller(Context paramContext, IBinder paramIBinder1, IBinder paramIBinder2, Activity paramActivity, Intent paramIntent, int paramInt1, Bundle paramBundle, boolean paramBoolean, int paramInt2)
  {
    // Byte code:
    //   0: sipush 379
    //   3: aload 5
    //   5: invokevirtual 532	android/content/Intent:toString	()Ljava/lang/String;
    //   8: invokestatic 480	android/util/SeempLog:record_str	(ILjava/lang/String;)I
    //   11: pop
    //   12: aload_2
    //   13: checkcast 482	android/app/IApplicationThread
    //   16: astore_2
    //   17: aload_0
    //   18: getfield 228	android/app/Instrumentation:mActivityMonitors	Ljava/util/List;
    //   21: ifnull +114 -> 135
    //   24: aload_0
    //   25: getfield 83	android/app/Instrumentation:mSync	Ljava/lang/Object;
    //   28: astore 12
    //   30: aload 12
    //   32: monitorenter
    //   33: aload_0
    //   34: getfield 228	android/app/Instrumentation:mActivityMonitors	Ljava/util/List;
    //   37: invokeinterface 232 1 0
    //   42: istore 11
    //   44: iconst_0
    //   45: istore 10
    //   47: iload 10
    //   49: iload 11
    //   51: if_icmpge +81 -> 132
    //   54: aload_0
    //   55: getfield 228	android/app/Instrumentation:mActivityMonitors	Ljava/util/List;
    //   58: iload 10
    //   60: invokeinterface 236 2 0
    //   65: checkcast 15	android/app/Instrumentation$ActivityMonitor
    //   68: astore 13
    //   70: aload 13
    //   72: aload_1
    //   73: aconst_null
    //   74: aload 5
    //   76: invokevirtual 246	android/app/Instrumentation$ActivityMonitor:match	(Landroid/content/Context;Landroid/app/Activity;Landroid/content/Intent;)Z
    //   79: ifeq +44 -> 123
    //   82: aload 13
    //   84: aload 13
    //   86: getfield 486	android/app/Instrumentation$ActivityMonitor:mHits	I
    //   89: iconst_1
    //   90: iadd
    //   91: putfield 486	android/app/Instrumentation$ActivityMonitor:mHits	I
    //   94: aload 13
    //   96: invokevirtual 489	android/app/Instrumentation$ActivityMonitor:isBlocking	()Z
    //   99: ifeq +33 -> 132
    //   102: iload 6
    //   104: iflt +14 -> 118
    //   107: aload 13
    //   109: invokevirtual 575	android/app/Instrumentation$ActivityMonitor:getResult	()Landroid/app/Instrumentation$ActivityResult;
    //   112: astore_1
    //   113: aload 12
    //   115: monitorexit
    //   116: aload_1
    //   117: areturn
    //   118: aconst_null
    //   119: astore_1
    //   120: goto -7 -> 113
    //   123: iload 10
    //   125: iconst_1
    //   126: iadd
    //   127: istore 10
    //   129: goto -82 -> 47
    //   132: aload 12
    //   134: monitorexit
    //   135: aload 5
    //   137: invokevirtual 494	android/content/Intent:migrateExtraStreamToClipData	()Z
    //   140: pop
    //   141: aload 5
    //   143: aload_1
    //   144: invokevirtual 497	android/content/Intent:prepareToLeaveProcess	(Landroid/content/Context;)V
    //   147: invokestatic 513	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   150: astore 12
    //   152: aload_1
    //   153: invokevirtual 516	android/content/Context:getBasePackageName	()Ljava/lang/String;
    //   156: astore 13
    //   158: aload 5
    //   160: aload_1
    //   161: invokevirtual 503	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   164: invokevirtual 507	android/content/Intent:resolveTypeIfNeeded	(Landroid/content/ContentResolver;)Ljava/lang/String;
    //   167: astore 14
    //   169: aload 4
    //   171: ifnull +48 -> 219
    //   174: aload 4
    //   176: getfield 578	android/app/Activity:mEmbeddedID	Ljava/lang/String;
    //   179: astore_1
    //   180: aload 12
    //   182: aload_2
    //   183: aload 13
    //   185: aload 5
    //   187: aload 14
    //   189: aload_3
    //   190: aload_1
    //   191: iload 6
    //   193: iconst_0
    //   194: aconst_null
    //   195: aload 7
    //   197: iload 8
    //   199: iload 9
    //   201: invokeinterface 597 13 0
    //   206: aload 5
    //   208: invokestatic 524	android/app/Instrumentation:checkStartActivityResult	(ILjava/lang/Object;)V
    //   211: aconst_null
    //   212: areturn
    //   213: astore_1
    //   214: aload 12
    //   216: monitorexit
    //   217: aload_1
    //   218: athrow
    //   219: aconst_null
    //   220: astore_1
    //   221: goto -41 -> 180
    //   224: astore_1
    //   225: new 280	java/lang/RuntimeException
    //   228: dup
    //   229: ldc_w 526
    //   232: aload_1
    //   233: invokespecial 529	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   236: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	237	0	this	Instrumentation
    //   0	237	1	paramContext	Context
    //   0	237	2	paramIBinder1	IBinder
    //   0	237	3	paramIBinder2	IBinder
    //   0	237	4	paramActivity	Activity
    //   0	237	5	paramIntent	Intent
    //   0	237	6	paramInt1	int
    //   0	237	7	paramBundle	Bundle
    //   0	237	8	paramBoolean	boolean
    //   0	237	9	paramInt2	int
    //   45	83	10	i	int
    //   42	10	11	j	int
    //   28	187	12	localObject1	Object
    //   68	116	13	localObject2	Object
    //   167	21	14	str	String
    // Exception table:
    //   from	to	target	type
    //   33	44	213	finally
    //   54	102	213	finally
    //   107	113	213	finally
    //   135	169	224	android/os/RemoteException
    //   174	180	224	android/os/RemoteException
    //   180	211	224	android/os/RemoteException
  }
  
  public void execStartActivityFromAppTask(Context paramContext, IBinder arg2, IAppTask paramIAppTask, Intent paramIntent, Bundle paramBundle)
  {
    SeempLog.record_str(380, paramIntent.toString());
    IApplicationThread localIApplicationThread = (IApplicationThread)???;
    if (this.mActivityMonitors != null) {}
    synchronized (this.mSync)
    {
      int j = this.mActivityMonitors.size();
      int i = 0;
      while (i < j)
      {
        ActivityMonitor localActivityMonitor = (ActivityMonitor)this.mActivityMonitors.get(i);
        if (localActivityMonitor.match(paramContext, null, paramIntent))
        {
          localActivityMonitor.mHits += 1;
          boolean bool = localActivityMonitor.isBlocking();
          if (!bool) {
            break;
          }
          return;
        }
        i += 1;
      }
    }
  }
  
  public void finish(int paramInt, Bundle paramBundle)
  {
    if (this.mAutomaticPerformanceSnapshots) {
      endPerformanceSnapshot();
    }
    Bundle localBundle = paramBundle;
    if (this.mPerfMetrics != null)
    {
      localBundle = paramBundle;
      if (paramBundle == null) {
        localBundle = new Bundle();
      }
      localBundle.putAll(this.mPerfMetrics);
    }
    if ((this.mUiAutomation == null) || (this.mUiAutomation.isDestroyed())) {}
    for (;;)
    {
      this.mThread.finishInstrumentation(paramInt, localBundle);
      return;
      this.mUiAutomation.disconnect();
      this.mUiAutomation = null;
    }
  }
  
  public Bundle getAllocCounts()
  {
    Bundle localBundle = new Bundle();
    localBundle.putLong("global_alloc_count", Debug.getGlobalAllocCount());
    localBundle.putLong("global_alloc_size", Debug.getGlobalAllocSize());
    localBundle.putLong("global_freed_count", Debug.getGlobalFreedCount());
    localBundle.putLong("global_freed_size", Debug.getGlobalFreedSize());
    localBundle.putLong("gc_invocation_count", Debug.getGlobalGcInvocationCount());
    return localBundle;
  }
  
  public Bundle getBinderCounts()
  {
    Bundle localBundle = new Bundle();
    localBundle.putLong("sent_transactions", Debug.getBinderSentTransactions());
    localBundle.putLong("received_transactions", Debug.getBinderReceivedTransactions());
    return localBundle;
  }
  
  public ComponentName getComponentName()
  {
    return this.mComponent;
  }
  
  public Context getContext()
  {
    return this.mInstrContext;
  }
  
  public Context getTargetContext()
  {
    return this.mAppContext;
  }
  
  public UiAutomation getUiAutomation()
  {
    return getUiAutomation(0);
  }
  
  public UiAutomation getUiAutomation(int paramInt)
  {
    boolean bool;
    if (this.mUiAutomation != null) {
      bool = this.mUiAutomation.isDestroyed();
    }
    while (this.mUiAutomationConnection != null) {
      if ((!bool) && (this.mUiAutomation.getFlags() == paramInt))
      {
        return this.mUiAutomation;
        bool = true;
      }
      else
      {
        if (bool) {
          this.mUiAutomation = new UiAutomation(getTargetContext().getMainLooper(), this.mUiAutomationConnection);
        }
        for (;;)
        {
          this.mUiAutomation.connect(paramInt);
          return this.mUiAutomation;
          this.mUiAutomation.disconnect();
        }
      }
    }
    return null;
  }
  
  final void init(ActivityThread paramActivityThread, Context paramContext1, Context paramContext2, ComponentName paramComponentName, IInstrumentationWatcher paramIInstrumentationWatcher, IUiAutomationConnection paramIUiAutomationConnection)
  {
    this.mThread = paramActivityThread;
    this.mThread.getLooper();
    this.mMessageQueue = Looper.myQueue();
    this.mInstrContext = paramContext1;
    this.mAppContext = paramContext2;
    this.mComponent = paramComponentName;
    this.mWatcher = paramIInstrumentationWatcher;
    this.mUiAutomationConnection = paramIUiAutomationConnection;
  }
  
  public boolean invokeContextMenuAction(Activity paramActivity, int paramInt1, int paramInt2)
  {
    validateNotAppThread();
    sendKeySync(new KeyEvent(0, 23));
    waitForIdleSync();
    try
    {
      Thread.sleep(ViewConfiguration.getLongPressTimeout());
      sendKeySync(new KeyEvent(1, 23));
      waitForIdleSync();
      paramActivity = new Runnable()
      {
        private final Activity activity;
        private final int flags;
        private final int identifier;
        boolean returnValue;
        
        public void run()
        {
          this.returnValue = this.activity.getWindow().performContextMenuIdentifierAction(this.identifier, this.flags);
        }
      };
      runOnMainSync(paramActivity);
      return paramActivity.returnValue;
    }
    catch (InterruptedException paramActivity)
    {
      Log.e("Instrumentation", "Could not sleep for long press timeout", paramActivity);
    }
    return false;
  }
  
  public boolean invokeMenuActionSync(Activity paramActivity, int paramInt1, int paramInt2)
  {
    paramActivity = new Runnable()
    {
      private final Activity activity;
      private final int flags;
      private final int identifier;
      boolean returnValue;
      
      public void run()
      {
        this.returnValue = this.activity.getWindow().performPanelIdentifierAction(0, this.identifier, this.flags);
      }
    };
    runOnMainSync(paramActivity);
    return paramActivity.returnValue;
  }
  
  public boolean isProfiling()
  {
    return this.mThread.isProfiling();
  }
  
  public Activity newActivity(Class<?> paramClass, Context paramContext, IBinder paramIBinder, Application paramApplication, Intent paramIntent, ActivityInfo paramActivityInfo, CharSequence paramCharSequence, Activity paramActivity, String paramString, Object paramObject)
    throws InstantiationException, IllegalAccessException
  {
    paramClass = (Activity)paramClass.newInstance();
    paramClass.attach(paramContext, null, this, paramIBinder, 0, paramApplication, paramIntent, paramActivityInfo, paramCharSequence, paramActivity, paramString, (Activity.NonConfigurationInstances)paramObject, new Configuration(), null, null, null);
    return paramClass;
  }
  
  public Activity newActivity(ClassLoader paramClassLoader, String paramString, Intent paramIntent)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    return (Activity)paramClassLoader.loadClass(paramString).newInstance();
  }
  
  public Application newApplication(ClassLoader paramClassLoader, String paramString, Context paramContext)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    return newApplication(paramClassLoader.loadClass(paramString), paramContext);
  }
  
  public void onCreate(Bundle paramBundle) {}
  
  public void onDestroy() {}
  
  public boolean onException(Object paramObject, Throwable paramThrowable)
  {
    return false;
  }
  
  public void onStart() {}
  
  public void removeMonitor(ActivityMonitor paramActivityMonitor)
  {
    synchronized (this.mSync)
    {
      this.mActivityMonitors.remove(paramActivityMonitor);
      return;
    }
  }
  
  public void runOnMainSync(Runnable paramRunnable)
  {
    validateNotAppThread();
    paramRunnable = new SyncRunnable(paramRunnable);
    this.mThread.getHandler().post(paramRunnable);
    paramRunnable.waitForComplete();
  }
  
  public void sendCharacterSync(int paramInt)
  {
    sendKeySync(new KeyEvent(0, paramInt));
    sendKeySync(new KeyEvent(1, paramInt));
  }
  
  public void sendKeyDownUpSync(int paramInt)
  {
    sendKeySync(new KeyEvent(0, paramInt));
    sendKeySync(new KeyEvent(1, paramInt));
  }
  
  public void sendKeySync(KeyEvent paramKeyEvent)
  {
    validateNotAppThread();
    long l3 = paramKeyEvent.getDownTime();
    long l2 = paramKeyEvent.getEventTime();
    int k = paramKeyEvent.getAction();
    int m = paramKeyEvent.getKeyCode();
    int n = paramKeyEvent.getRepeatCount();
    int i1 = paramKeyEvent.getMetaState();
    int i2 = paramKeyEvent.getDeviceId();
    int i3 = paramKeyEvent.getScanCode();
    int j = paramKeyEvent.getSource();
    int i4 = paramKeyEvent.getFlags();
    int i = j;
    if (j == 0) {
      i = 257;
    }
    long l1 = l2;
    if (l2 == 0L) {
      l1 = SystemClock.uptimeMillis();
    }
    l2 = l3;
    if (l3 == 0L) {
      l2 = l1;
    }
    paramKeyEvent = new KeyEvent(l2, l1, k, m, n, i1, i2, i3, i4 | 0x8, i);
    InputManager.getInstance().injectInputEvent(paramKeyEvent, 2);
  }
  
  public void sendPointerSync(MotionEvent paramMotionEvent)
  {
    validateNotAppThread();
    if ((paramMotionEvent.getSource() & 0x2) == 0) {
      paramMotionEvent.setSource(4098);
    }
    InputManager.getInstance().injectInputEvent(paramMotionEvent, 2);
  }
  
  public void sendStatus(int paramInt, Bundle paramBundle)
  {
    if (this.mWatcher != null) {}
    try
    {
      this.mWatcher.instrumentationStatus(this.mComponent, paramInt, paramBundle);
      return;
    }
    catch (RemoteException paramBundle)
    {
      this.mWatcher = null;
    }
  }
  
  public void sendStringSync(String paramString)
  {
    if (paramString == null) {
      return;
    }
    paramString = KeyCharacterMap.load(-1).getEvents(paramString.toCharArray());
    if (paramString != null)
    {
      int i = 0;
      while (i < paramString.length)
      {
        sendKeySync(KeyEvent.changeTimeRepeat(paramString[i], SystemClock.uptimeMillis(), 0));
        i += 1;
      }
    }
  }
  
  public void sendTrackballEventSync(MotionEvent paramMotionEvent)
  {
    validateNotAppThread();
    if ((paramMotionEvent.getSource() & 0x4) == 0) {
      paramMotionEvent.setSource(65540);
    }
    InputManager.getInstance().injectInputEvent(paramMotionEvent, 2);
  }
  
  public void setAutomaticPerformanceSnapshots()
  {
    this.mAutomaticPerformanceSnapshots = true;
    this.mPerformanceCollector = new PerformanceCollector();
  }
  
  public void setInTouchMode(boolean paramBoolean)
  {
    try
    {
      IWindowManager.Stub.asInterface(ServiceManager.getService("window")).setInTouchMode(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void start()
  {
    if (this.mRunner != null) {
      throw new RuntimeException("Instrumentation already started");
    }
    this.mRunner = new InstrumentationThread("Instr: " + getClass().getName());
    this.mRunner.start();
  }
  
  public Activity startActivitySync(Intent paramIntent)
  {
    SeempLog.record_str(376, paramIntent.toString());
    validateNotAppThread();
    Object localObject2;
    synchronized (this.mSync)
    {
      paramIntent = new Intent(paramIntent);
      try
      {
        localObject2 = paramIntent.resolveActivityInfo(getTargetContext().getPackageManager(), 0);
        if (localObject2 != null) {
          break label81;
        }
        throw new RuntimeException("Unable to resolve activity for: " + paramIntent);
      }
      finally {}
      throw paramIntent;
      label81:
      String str = this.mThread.getProcessName();
      if (!((ActivityInfo)localObject2).processName.equals(str)) {
        throw new RuntimeException("Intent in process " + str + " resolved to different process " + ((ActivityInfo)localObject2).processName + ": " + paramIntent);
      }
      paramIntent.setComponent(new ComponentName(((ActivityInfo)localObject2).applicationInfo.packageName, ((ActivityInfo)localObject2).name));
      localObject2 = new ActivityWaiter(paramIntent);
      if (this.mWaitingActivities == null) {
        this.mWaitingActivities = new ArrayList();
      }
      this.mWaitingActivities.add(localObject2);
      getTargetContext().startActivity(paramIntent);
    }
  }
  
  @Deprecated
  public void startAllocCounting()
  {
    Runtime.getRuntime().gc();
    Runtime.getRuntime().runFinalization();
    Runtime.getRuntime().gc();
    Debug.resetAllCounts();
    Debug.startAllocCounting();
  }
  
  public void startPerformanceSnapshot()
  {
    if (!isProfiling()) {
      this.mPerformanceCollector.beginSnapshot(null);
    }
  }
  
  public void startProfiling()
  {
    if (this.mThread.isProfiling())
    {
      File localFile = new File(this.mThread.getProfileFilePath());
      localFile.getParentFile().mkdirs();
      Debug.startMethodTracing(localFile.toString(), 8388608);
    }
  }
  
  @Deprecated
  public void stopAllocCounting()
  {
    Runtime.getRuntime().gc();
    Runtime.getRuntime().runFinalization();
    Runtime.getRuntime().gc();
    Debug.stopAllocCounting();
  }
  
  public void stopProfiling()
  {
    if (this.mThread.isProfiling()) {
      Debug.stopMethodTracing();
    }
  }
  
  public void waitForIdle(Runnable paramRunnable)
  {
    this.mMessageQueue.addIdleHandler(new Idler(paramRunnable));
    this.mThread.getHandler().post(new EmptyRunnable(null));
  }
  
  public void waitForIdleSync()
  {
    validateNotAppThread();
    Idler localIdler = new Idler(null);
    this.mMessageQueue.addIdleHandler(localIdler);
    this.mThread.getHandler().post(new EmptyRunnable(null));
    localIdler.waitForIdle();
  }
  
  public Activity waitForMonitor(ActivityMonitor paramActivityMonitor)
  {
    Activity localActivity = paramActivityMonitor.waitForActivity();
    synchronized (this.mSync)
    {
      this.mActivityMonitors.remove(paramActivityMonitor);
      return localActivity;
    }
  }
  
  public Activity waitForMonitorWithTimeout(ActivityMonitor paramActivityMonitor, long paramLong)
  {
    Activity localActivity = paramActivityMonitor.waitForActivityWithTimeout(paramLong);
    synchronized (this.mSync)
    {
      this.mActivityMonitors.remove(paramActivityMonitor);
      return localActivity;
    }
  }
  
  private final class ActivityGoing
    implements MessageQueue.IdleHandler
  {
    private final Instrumentation.ActivityWaiter mWaiter;
    
    public ActivityGoing(Instrumentation.ActivityWaiter paramActivityWaiter)
    {
      this.mWaiter = paramActivityWaiter;
    }
    
    public final boolean queueIdle()
    {
      synchronized (Instrumentation.-get1(Instrumentation.this))
      {
        Instrumentation.-get2(Instrumentation.this).remove(this.mWaiter);
        Instrumentation.-get1(Instrumentation.this).notifyAll();
        return false;
      }
    }
  }
  
  public static class ActivityMonitor
  {
    private final boolean mBlock;
    private final String mClass;
    int mHits = 0;
    Activity mLastActivity = null;
    private final Instrumentation.ActivityResult mResult;
    private final IntentFilter mWhich;
    
    public ActivityMonitor(IntentFilter paramIntentFilter, Instrumentation.ActivityResult paramActivityResult, boolean paramBoolean)
    {
      this.mWhich = paramIntentFilter;
      this.mClass = null;
      this.mResult = paramActivityResult;
      this.mBlock = paramBoolean;
    }
    
    public ActivityMonitor(String paramString, Instrumentation.ActivityResult paramActivityResult, boolean paramBoolean)
    {
      this.mWhich = null;
      this.mClass = paramString;
      this.mResult = paramActivityResult;
      this.mBlock = paramBoolean;
    }
    
    public final IntentFilter getFilter()
    {
      return this.mWhich;
    }
    
    public final int getHits()
    {
      return this.mHits;
    }
    
    public final Activity getLastActivity()
    {
      return this.mLastActivity;
    }
    
    public final Instrumentation.ActivityResult getResult()
    {
      return this.mResult;
    }
    
    public final boolean isBlocking()
    {
      return this.mBlock;
    }
    
    final boolean match(Context paramContext, Activity paramActivity, Intent paramIntent)
    {
      try
      {
        if (this.mWhich != null)
        {
          int i = this.mWhich.match(paramContext.getContentResolver(), paramIntent, true, "Instrumentation");
          if (i < 0) {
            return false;
          }
        }
        if (this.mClass != null)
        {
          paramContext = null;
          if (paramActivity == null) {
            break label88;
          }
          paramContext = paramActivity.getClass().getName();
        }
        while ((paramContext != null) && (this.mClass.equals(paramContext)))
        {
          if (paramActivity != null)
          {
            this.mLastActivity = paramActivity;
            notifyAll();
          }
          return true;
          label88:
          if (paramIntent.getComponent() != null) {
            paramContext = paramIntent.getComponent().getClassName();
          }
        }
        return false;
      }
      finally {}
    }
    
    public final Activity waitForActivity()
    {
      try
      {
        for (;;)
        {
          Activity localActivity1 = this.mLastActivity;
          if (localActivity1 != null) {
            break;
          }
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException) {}
        }
        Activity localActivity2 = this.mLastActivity;
        this.mLastActivity = null;
        return localActivity2;
      }
      finally {}
    }
    
    /* Error */
    public final Activity waitForActivityWithTimeout(long paramLong)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 28	android/app/Instrumentation$ActivityMonitor:mLastActivity	Landroid/app/Activity;
      //   6: astore_3
      //   7: aload_3
      //   8: ifnonnull +8 -> 16
      //   11: aload_0
      //   12: lload_1
      //   13: invokevirtual 104	java/lang/Object:wait	(J)V
      //   16: aload_0
      //   17: getfield 28	android/app/Instrumentation$ActivityMonitor:mLastActivity	Landroid/app/Activity;
      //   20: astore_3
      //   21: aload_3
      //   22: ifnonnull +11 -> 33
      //   25: aload_0
      //   26: monitorexit
      //   27: aconst_null
      //   28: areturn
      //   29: astore_3
      //   30: goto -14 -> 16
      //   33: aload_0
      //   34: getfield 28	android/app/Instrumentation$ActivityMonitor:mLastActivity	Landroid/app/Activity;
      //   37: astore_3
      //   38: aload_0
      //   39: aconst_null
      //   40: putfield 28	android/app/Instrumentation$ActivityMonitor:mLastActivity	Landroid/app/Activity;
      //   43: aload_0
      //   44: monitorexit
      //   45: aload_3
      //   46: areturn
      //   47: astore_3
      //   48: aload_0
      //   49: monitorexit
      //   50: aload_3
      //   51: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	52	0	this	ActivityMonitor
      //   0	52	1	paramLong	long
      //   6	16	3	localActivity1	Activity
      //   29	1	3	localInterruptedException	InterruptedException
      //   37	9	3	localActivity2	Activity
      //   47	4	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   11	16	29	java/lang/InterruptedException
      //   2	7	47	finally
      //   11	16	47	finally
      //   16	21	47	finally
      //   33	43	47	finally
    }
  }
  
  public static final class ActivityResult
  {
    private final int mResultCode;
    private final Intent mResultData;
    
    public ActivityResult(int paramInt, Intent paramIntent)
    {
      this.mResultCode = paramInt;
      this.mResultData = paramIntent;
    }
    
    public int getResultCode()
    {
      return this.mResultCode;
    }
    
    public Intent getResultData()
    {
      return this.mResultData;
    }
  }
  
  private static final class ActivityWaiter
  {
    public Activity activity;
    public final Intent intent;
    
    public ActivityWaiter(Intent paramIntent)
    {
      this.intent = paramIntent;
    }
  }
  
  private static final class EmptyRunnable
    implements Runnable
  {
    public void run() {}
  }
  
  private static final class Idler
    implements MessageQueue.IdleHandler
  {
    private final Runnable mCallback;
    private boolean mIdle;
    
    public Idler(Runnable paramRunnable)
    {
      this.mCallback = paramRunnable;
      this.mIdle = false;
    }
    
    public final boolean queueIdle()
    {
      if (this.mCallback != null) {
        this.mCallback.run();
      }
      try
      {
        this.mIdle = true;
        notifyAll();
        return false;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void waitForIdle()
    {
      try
      {
        for (;;)
        {
          boolean bool = this.mIdle;
          if (bool) {
            break;
          }
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException) {}
        }
        return;
      }
      finally {}
    }
  }
  
  private final class InstrumentationThread
    extends Thread
  {
    public InstrumentationThread(String paramString)
    {
      super();
    }
    
    public void run()
    {
      try
      {
        Process.setThreadPriority(-8);
        if (Instrumentation.-get0(Instrumentation.this)) {
          Instrumentation.this.startPerformanceSnapshot();
        }
        Instrumentation.this.onStart();
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        for (;;)
        {
          Log.w("Instrumentation", "Exception setting priority of instrumentation thread " + Process.myTid(), localRuntimeException);
        }
      }
    }
  }
  
  private static final class SyncRunnable
    implements Runnable
  {
    private boolean mComplete;
    private final Runnable mTarget;
    
    public SyncRunnable(Runnable paramRunnable)
    {
      this.mTarget = paramRunnable;
    }
    
    public void run()
    {
      this.mTarget.run();
      try
      {
        this.mComplete = true;
        notifyAll();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void waitForComplete()
    {
      try
      {
        for (;;)
        {
          boolean bool = this.mComplete;
          if (bool) {
            break;
          }
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException) {}
        }
        return;
      }
      finally {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/Instrumentation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */