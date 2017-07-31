package com.android.server.notification;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.AutomaticZenRule;
import android.app.IActivityManager;
import android.app.INotificationManager.Stub;
import android.app.ITransientNotification;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.NotificationManager.Policy;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.AudioManagerInternal;
import android.media.AudioSystem;
import android.media.IRingtonePlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.service.notification.Adjustment;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.INotificationListener;
import android.service.notification.INotificationListener.Stub;
import android.service.notification.IStatusBarNotificationHolder.Stub;
import android.service.notification.NotificationRankingUpdate;
import android.service.notification.StatusBarNotification;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ZenRule;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import android.view.WindowManagerInternal;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.Preconditions;
import com.android.server.DeviceIdleController.LocalService;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.am.OnePlusAppBootManager;
import com.android.server.am.OnePlusProcessManager;
import com.android.server.lights.Light;
import com.android.server.lights.LightsManager;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.vr.VrManagerInternal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class NotificationManagerService
  extends SystemService
{
  private static final String ATTR_NAME = "name";
  private static final String ATTR_VERSION = "version";
  static final boolean DBG = Log.isLoggable("NotificationService", 3);
  private static final int DB_VERSION = 1;
  private static boolean DEBUG_ONEPLUS = false;
  static final float DEFAULT_MAX_NOTIFICATION_ENQUEUE_RATE = 10.0F;
  static final int DEFAULT_STREAM_TYPE = 5;
  static final long[] DEFAULT_VIBRATE_PATTERN;
  static final boolean ENABLE_BLOCKED_NOTIFICATIONS = true;
  static final boolean ENABLE_BLOCKED_TOASTS = true;
  public static final boolean ENABLE_CHILD_NOTIFICATIONS = SystemProperties.getBoolean("debug.child_notifs", true);
  private static final int EVENTLOG_ENQUEUE_STATUS_IGNORED = 2;
  private static final int EVENTLOG_ENQUEUE_STATUS_NEW = 0;
  private static final int EVENTLOG_ENQUEUE_STATUS_UPDATE = 1;
  static final int LONG_DELAY = 3500;
  static final int MATCHES_CALL_FILTER_CONTACTS_TIMEOUT_MS = 3000;
  static final float MATCHES_CALL_FILTER_TIMEOUT_AFFINITY = 1.0F;
  static final int MAX_PACKAGE_NOTIFICATIONS = 50;
  static final int MESSAGE_LISTENER_HINTS_CHANGED = 5;
  static final int MESSAGE_LISTENER_NOTIFICATION_FILTER_CHANGED = 6;
  private static final int MESSAGE_RANKING_SORT = 1001;
  private static final int MESSAGE_RECONSIDER_RANKING = 1000;
  static final int MESSAGE_SAVE_NOTIFICATION_LED_POLICY_FILE = 7;
  static final int MESSAGE_SAVE_POLICY_FILE = 3;
  static final int MESSAGE_SEND_RANKING_UPDATE = 4;
  static final int MESSAGE_TIMEOUT = 2;
  private static final long MIN_PACKAGE_OVERRATE_LOG_INTERVAL = 5000L;
  private static final int MY_PID = Process.myPid();
  private static final int MY_UID;
  private static final List<String> NON_BLOCKABLE_PKGS = Arrays.asList(new String[] { "com.oneplus.deskclock", "com.android.dialer", "com.google.android.calendar", "com.oneplus.calendar" });
  private static final long[][] OP_DEFAULT_VIBRATION_PATTERN = { { -1L, 0L, 100L, 150L, 100L }, { -2L, 0L, 100L, 150L, 100L }, { -3L, 0L, 100L, 150L, 100L } };
  static final int SHORT_DELAY = 2000;
  static final String TAG = "NotificationService";
  private static final String TAG_BLOCKED_PKGS = "blocked-packages";
  private static final String TAG_NOTIFICATION_POLICY = "notification-policy";
  private static final String TAG_PACKAGE = "package";
  static final int VIBRATE_PATTERN_MAXLEN = 17;
  private IActivityManager mAm;
  private AppOpsManager mAppOps;
  private UsageStatsManagerInternal mAppUsageStats;
  private Archive mArchive;
  Light mAttentionLight;
  AudioManager mAudioManager;
  AudioManagerInternal mAudioManagerInternal;
  final ArrayMap<Integer, ArrayMap<String, String>> mAutobundledSummaries = new ArrayMap();
  private final Runnable mBuzzBeepBlinked = new Runnable()
  {
    public void run()
    {
      if (NotificationManagerService.this.mStatusBar != null) {
        NotificationManagerService.this.mStatusBar.buzzBeepBlinked();
      }
    }
  };
  private int mCallState;
  private PersistableBundle mCarrierConfig;
  private ConditionProviders mConditionProviders;
  private CarrierConfigManager mConfigManager;
  private int mDefLowBatteryWarningLevel;
  private int mDefaultNotificationColor;
  private int mDefaultNotificationLedOff;
  private int mDefaultNotificationLedOn;
  private long[] mDefaultVibrationPattern;
  private boolean mDisableNotificationEffects;
  private List<ComponentName> mEffectsSuppressors = new ArrayList();
  private long[] mFallbackVibrationPattern;
  final IBinder mForegroundToken = new Binder();
  private Handler mHandler;
  private boolean mInCall = false;
  private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getAction();
      if (str.equals("android.intent.action.SCREEN_ON"))
      {
        NotificationManagerService.-set7(NotificationManagerService.this, true);
        NotificationManagerService.-wrap37(NotificationManagerService.this);
      }
      do
      {
        do
        {
          int i;
          do
          {
            do
            {
              do
              {
                return;
                if (str.equals("android.intent.action.SCREEN_OFF"))
                {
                  NotificationManagerService.-set7(NotificationManagerService.this, false);
                  NotificationManagerService.-wrap37(NotificationManagerService.this);
                  return;
                }
                if (str.equals("android.intent.action.PHONE_STATE"))
                {
                  NotificationManagerService.-set4(NotificationManagerService.this, TelephonyManager.EXTRA_STATE_OFFHOOK.equals(paramAnonymousIntent.getStringExtra("state")));
                  NotificationManagerService.-wrap37(NotificationManagerService.this);
                  return;
                }
                if (!str.equals("android.intent.action.USER_STOPPED")) {
                  break;
                }
                i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", -1);
              } while (i < 0);
              NotificationManagerService.this.cancelAllNotificationsInt(NotificationManagerService.-get2(), NotificationManagerService.-get1(), null, 0, 0, true, i, 6, null);
              return;
              if (!str.equals("android.intent.action.MANAGED_PROFILE_UNAVAILABLE")) {
                break;
              }
              i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", -1);
            } while (i < 0);
            NotificationManagerService.this.cancelAllNotificationsInt(NotificationManagerService.-get2(), NotificationManagerService.-get1(), null, 0, 0, true, i, 15, null);
            return;
            if (!str.equals("android.intent.action.USER_PRESENT")) {
              break;
            }
            NotificationManagerService.-get16(NotificationManagerService.this).turnOff();
          } while (NotificationManagerService.this.mStatusBar == null);
          NotificationManagerService.this.mStatusBar.notificationLightOff();
          return;
          if (str.equals("android.intent.action.USER_SWITCHED"))
          {
            i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
            NotificationManagerService.-get22(NotificationManagerService.this).update(null);
            NotificationManagerService.-get24(NotificationManagerService.this).updateCache(paramAnonymousContext);
            NotificationManagerService.-get7(NotificationManagerService.this).onUserSwitched(i);
            NotificationManagerService.-get14(NotificationManagerService.this).onUserSwitched(i);
            NotificationManagerService.-get19(NotificationManagerService.this).onUserSwitched(i);
            NotificationManagerService.-get26(NotificationManagerService.this).onUserSwitched(i);
            return;
          }
          if (str.equals("android.intent.action.USER_ADDED"))
          {
            NotificationManagerService.-get24(NotificationManagerService.this).updateCache(paramAnonymousContext);
            return;
          }
          if (str.equals("android.intent.action.USER_REMOVED"))
          {
            i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
            NotificationManagerService.-get26(NotificationManagerService.this).onUserRemoved(i);
            return;
          }
          if (str.equals("android.intent.action.USER_UNLOCKED"))
          {
            i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
            NotificationManagerService.-get7(NotificationManagerService.this).onUserUnlocked(i);
            NotificationManagerService.-get14(NotificationManagerService.this).onUserUnlocked(i);
            NotificationManagerService.-get19(NotificationManagerService.this).onUserUnlocked(i);
            NotificationManagerService.-get26(NotificationManagerService.this).onUserUnlocked(i);
            return;
          }
          if (str.equals("android.telephony.action.CARRIER_CONFIG_CHANGED"))
          {
            NotificationManagerService.-set1(NotificationManagerService.this, NotificationManagerService.-get8(NotificationManagerService.this).getConfig());
            return;
          }
        } while ((!str.equals("action.appboot.notification_listener_update")) || (!OnePlusAppBootManager.IN_USING));
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("pkg");
        Slog.v("OnePlusAppBootManager", "ACTION_NOTIFICATION_LISTENER_UPDATE: pkgName=" + paramAnonymousContext);
      } while ((paramAnonymousContext == null) || (paramAnonymousContext.length() <= 0));
      NotificationManagerService.-get14(NotificationManagerService.this).onPackagesChanged(false, new String[] { paramAnonymousContext });
    }
  };
  private final NotificationManagerInternal mInternalService = new NotificationManagerInternal()
  {
    public void enqueueNotification(String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString3, int paramAnonymousInt3, Notification paramAnonymousNotification, int[] paramAnonymousArrayOfInt, int paramAnonymousInt4)
    {
      NotificationManagerService.this.enqueueNotificationInternal(paramAnonymousString1, paramAnonymousString2, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousString3, paramAnonymousInt3, paramAnonymousNotification, paramAnonymousArrayOfInt, paramAnonymousInt4);
    }
    
    public void removeForegroundServiceFlagFromNotification(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        int i = NotificationManagerService.this.indexOfNotificationLocked(paramAnonymousString, null, paramAnonymousInt1, paramAnonymousInt2);
        if (i < 0)
        {
          Log.d("NotificationService", "stripForegroundServiceFlag: Could not find notification with pkg=" + paramAnonymousString + " / id=" + paramAnonymousInt1 + " / userId=" + paramAnonymousInt2);
          return;
        }
        paramAnonymousString = (NotificationRecord)NotificationManagerService.this.mNotificationList.get(i);
        StatusBarNotification localStatusBarNotification = paramAnonymousString.sbn;
        localStatusBarNotification.getNotification().flags = (paramAnonymousString.mOriginalFlags & 0xFFFFFFBF);
        NotificationManagerService.-get21(NotificationManagerService.this).sort(NotificationManagerService.this.mNotificationList);
        NotificationManagerService.-get14(NotificationManagerService.this).notifyPostedLocked(localStatusBarNotification, localStatusBarNotification);
        return;
      }
    }
  };
  private int mInterruptionFilter = 0;
  private long mLastOverRateLogTime;
  ArrayList<String> mLights = new ArrayList();
  private int mListenerHints;
  private NotificationListeners mListeners;
  private final SparseArray<ArraySet<ManagedServices.ManagedServiceInfo>> mListenersDisablingEffects = new SparseArray();
  private float mMaxPackageEnqueueRate = 10.0F;
  private final NotificationDelegate mNotificationDelegate = new NotificationDelegate()
  {
    public void clearEffects()
    {
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        if (NotificationManagerService.DBG) {
          Slog.d("NotificationService", "clearEffects");
        }
        NotificationManagerService.-wrap19(NotificationManagerService.this);
        NotificationManagerService.-wrap20(NotificationManagerService.this);
        NotificationManagerService.-wrap18(NotificationManagerService.this);
        return;
      }
    }
    
    public void onClearAll(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        NotificationManagerService.this.cancelAllLocked(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, 3, null, true);
        return;
      }
    }
    
    public void onNotificationActionClick(int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString, int paramAnonymousInt3)
    {
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        NotificationRecord localNotificationRecord = (NotificationRecord)NotificationManagerService.this.mNotificationsByKey.get(paramAnonymousString);
        if (localNotificationRecord == null)
        {
          Log.w("NotificationService", "No notification with key: " + paramAnonymousString);
          return;
        }
        long l = System.currentTimeMillis();
        EventLogTags.writeNotificationActionClicked(paramAnonymousString, paramAnonymousInt3, localNotificationRecord.getLifespanMs(l), localNotificationRecord.getFreshnessMs(l), localNotificationRecord.getExposureMs(l));
        return;
      }
    }
    
    public void onNotificationClear(int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt3, int paramAnonymousInt4)
    {
      NotificationManagerService.this.cancelNotification(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousString1, paramAnonymousString2, paramAnonymousInt3, 0, 66, true, paramAnonymousInt4, 2, null);
    }
    
    public void onNotificationClick(int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString)
    {
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        NotificationRecord localNotificationRecord = (NotificationRecord)NotificationManagerService.this.mNotificationsByKey.get(paramAnonymousString);
        if (localNotificationRecord == null)
        {
          Log.w("NotificationService", "No notification with key: " + paramAnonymousString);
          return;
        }
        long l = System.currentTimeMillis();
        EventLogTags.writeNotificationClicked(paramAnonymousString, localNotificationRecord.getLifespanMs(l), localNotificationRecord.getFreshnessMs(l), localNotificationRecord.getExposureMs(l));
        paramAnonymousString = localNotificationRecord.sbn;
        NotificationManagerService.this.cancelNotification(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousString.getPackageName(), paramAnonymousString.getTag(), paramAnonymousString.getId(), 16, 64, false, localNotificationRecord.getUserId(), 1, null);
        return;
      }
    }
    
    public void onNotificationError(int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, String paramAnonymousString3, int paramAnonymousInt6)
    {
      Slog.d("NotificationService", "onNotification error pkg=" + paramAnonymousString1 + " tag=" + paramAnonymousString2 + " id=" + paramAnonymousInt3 + "; will crashApplication(uid=" + paramAnonymousInt4 + ", pid=" + paramAnonymousInt5 + ")");
      NotificationManagerService.this.cancelNotification(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousString1, paramAnonymousString2, paramAnonymousInt3, 0, 0, false, paramAnonymousInt6, 4, null);
      long l = Binder.clearCallingIdentity();
      try
      {
        ActivityManagerNative.getDefault().crashApplication(paramAnonymousInt4, paramAnonymousInt5, paramAnonymousString1, "Bad notification posted from package " + paramAnonymousString1 + ": " + paramAnonymousString3);
        Binder.restoreCallingIdentity(l);
        return;
      }
      catch (RemoteException paramAnonymousString1)
      {
        for (;;) {}
      }
    }
    
    public void onNotificationExpansionChanged(String paramAnonymousString, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
    {
      int j = 1;
      for (;;)
      {
        synchronized (NotificationManagerService.this.mNotificationList)
        {
          NotificationRecord localNotificationRecord = (NotificationRecord)NotificationManagerService.this.mNotificationsByKey.get(paramAnonymousString);
          if (localNotificationRecord != null)
          {
            localNotificationRecord.stats.onExpansionChanged(paramAnonymousBoolean1, paramAnonymousBoolean2);
            long l = System.currentTimeMillis();
            if (paramAnonymousBoolean1)
            {
              i = 1;
              break label112;
              EventLogTags.writeNotificationExpansion(paramAnonymousString, i, j, localNotificationRecord.getLifespanMs(l), localNotificationRecord.getFreshnessMs(l), localNotificationRecord.getExposureMs(l));
            }
          }
          else
          {
            return;
          }
          int i = 0;
          break label112;
          j = 0;
        }
        label112:
        if (!paramAnonymousBoolean2) {}
      }
    }
    
    public void onNotificationVisibilityChanged(NotificationVisibility[] paramAnonymousArrayOfNotificationVisibility1, NotificationVisibility[] paramAnonymousArrayOfNotificationVisibility2)
    {
      int j = 0;
      Object localObject;
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        k = paramAnonymousArrayOfNotificationVisibility1.length;
        i = 0;
        if (i < k)
        {
          localObject = paramAnonymousArrayOfNotificationVisibility1[i];
          NotificationRecord localNotificationRecord = (NotificationRecord)NotificationManagerService.this.mNotificationsByKey.get(((NotificationVisibility)localObject).key);
          if (localNotificationRecord == null) {
            break label150;
          }
          localNotificationRecord.setVisibility(true, ((NotificationVisibility)localObject).rank);
          ((NotificationVisibility)localObject).recycle();
        }
      }
      int k = paramAnonymousArrayOfNotificationVisibility2.length;
      int i = j;
      for (;;)
      {
        if (i < k)
        {
          paramAnonymousArrayOfNotificationVisibility1 = paramAnonymousArrayOfNotificationVisibility2[i];
          localObject = (NotificationRecord)NotificationManagerService.this.mNotificationsByKey.get(paramAnonymousArrayOfNotificationVisibility1.key);
          if (localObject != null)
          {
            ((NotificationRecord)localObject).setVisibility(false, paramAnonymousArrayOfNotificationVisibility1.rank);
            paramAnonymousArrayOfNotificationVisibility1.recycle();
          }
        }
        else
        {
          return;
          label150:
          i += 1;
          break;
        }
        i += 1;
      }
    }
    
    public void onPanelHidden() {}
    
    public void onPanelRevealed(boolean paramAnonymousBoolean, int paramAnonymousInt)
    {
      EventLogTags.writeNotificationPanelRevealed(paramAnonymousInt);
      if (paramAnonymousBoolean) {
        clearEffects();
      }
    }
    
    public void onSetDisabled(int paramAnonymousInt)
    {
      boolean bool = false;
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        Object localObject1 = NotificationManagerService.this;
        if ((0x40000 & paramAnonymousInt) != 0) {
          bool = true;
        }
        NotificationManagerService.-set3((NotificationManagerService)localObject1, bool);
        if (NotificationManagerService.-wrap9(NotificationManagerService.this, null) != null) {
          l = Binder.clearCallingIdentity();
        }
        try
        {
          localObject1 = NotificationManagerService.this.mAudioManager.getRingtonePlayer();
          if (localObject1 != null) {
            ((IRingtonePlayer)localObject1).stopAsync();
          }
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            localRemoteException = localRemoteException;
            Binder.restoreCallingIdentity(l);
          }
          localObject2 = finally;
          throw ((Throwable)localObject2);
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        l = Binder.clearCallingIdentity();
      }
    }
  };
  private HashSet<String> mNotificationLedBlockedPackages = new HashSet();
  private AtomicFile mNotificationLedPolicyFile;
  private Light mNotificationLight;
  final ArrayList<NotificationRecord> mNotificationList = new ArrayList();
  private boolean mNotificationPulseEnabled;
  final ArrayMap<String, NotificationRecord> mNotificationsByKey = new ArrayMap();
  private final BroadcastReceiver mPackageIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (paramAnonymousContext == null) {
        return;
      }
      boolean bool4 = false;
      boolean bool1 = false;
      boolean bool6 = false;
      int k = 1;
      int i = 1;
      int j = 5;
      boolean bool3 = bool6;
      boolean bool2 = bool4;
      if (!paramAnonymousContext.equals("android.intent.action.PACKAGE_ADDED"))
      {
        boolean bool5 = paramAnonymousContext.equals("android.intent.action.PACKAGE_REMOVED");
        bool3 = bool6;
        bool1 = bool5;
        bool2 = bool4;
        if (!bool5)
        {
          bool3 = bool6;
          bool1 = bool5;
          bool2 = bool4;
          if (!paramAnonymousContext.equals("android.intent.action.PACKAGE_RESTARTED"))
          {
            bool6 = paramAnonymousContext.equals("android.intent.action.PACKAGE_CHANGED");
            bool3 = bool6;
            bool1 = bool5;
            bool2 = bool4;
            if (!bool6)
            {
              bool4 = paramAnonymousContext.equals("android.intent.action.QUERY_PACKAGE_RESTART");
              bool3 = bool6;
              bool1 = bool5;
              bool2 = bool4;
              if (!bool4)
              {
                bool3 = bool6;
                bool1 = bool5;
                bool2 = bool4;
                if (!paramAnonymousContext.equals("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE"))
                {
                  if (!paramAnonymousContext.equals("android.intent.action.PACKAGES_SUSPENDED")) {
                    break label668;
                  }
                  bool2 = bool4;
                  bool1 = bool5;
                  bool3 = bool6;
                }
              }
            }
          }
        }
      }
      int m = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", -1);
      if (bool1) {
        if (paramAnonymousIntent.getBooleanExtra("android.intent.extra.REPLACING", false))
        {
          bool1 = false;
          if (NotificationManagerService.DBG) {
            Slog.i("NotificationService", "action=" + paramAnonymousContext + " removing=" + bool1);
          }
          if (!paramAnonymousContext.equals("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE")) {
            break label363;
          }
          paramAnonymousContext = paramAnonymousIntent.getStringArrayExtra("android.intent.extra.changed_package_list");
        }
      }
      for (;;)
      {
        label266:
        int n;
        Object localObject;
        if ((paramAnonymousContext != null) && (paramAnonymousContext.length > 0))
        {
          n = paramAnonymousContext.length;
          k = 0;
          for (;;)
          {
            if (k < n)
            {
              paramAnonymousIntent = paramAnonymousContext[k];
              if (i != 0)
              {
                localObject = NotificationManagerService.this;
                int i1 = NotificationManagerService.-get2();
                int i2 = NotificationManagerService.-get1();
                if (bool2)
                {
                  bool3 = false;
                  label322:
                  ((NotificationManagerService)localObject).cancelAllNotificationsInt(i1, i2, paramAnonymousIntent, 0, 0, bool3, m, j, null);
                }
              }
              else
              {
                k += 1;
                continue;
                bool1 = true;
                break;
                bool1 = false;
                break;
                label363:
                if (paramAnonymousContext.equals("android.intent.action.PACKAGES_SUSPENDED"))
                {
                  paramAnonymousContext = paramAnonymousIntent.getStringArrayExtra("android.intent.extra.changed_package_list");
                  j = 14;
                  break label266;
                }
                if (bool2)
                {
                  paramAnonymousContext = paramAnonymousIntent.getStringArrayExtra("android.intent.extra.PACKAGES");
                  break label266;
                }
                localObject = paramAnonymousIntent.getData();
                if (localObject == null) {
                  return;
                }
                localObject = ((Uri)localObject).getSchemeSpecificPart();
                if (localObject == null) {
                  return;
                }
                i = k;
                if (!bool3) {}
              }
            }
          }
        }
        try
        {
          IPackageManager localIPackageManager = AppGlobals.getPackageManager();
          if (m != -1) {}
          for (i = m;; i = 0)
          {
            n = localIPackageManager.getApplicationEnabledSetting((String)localObject, i);
            if (n != 1)
            {
              i = k;
              if (n != 0) {}
            }
            else
            {
              i = 0;
            }
            bool3 = paramAnonymousIntent.getBooleanExtra("android.intent.extra.REPLACING", false);
            if ((paramAnonymousContext.equals("android.intent.action.PACKAGE_ADDED")) && (!bool3)) {
              break label544;
            }
            paramAnonymousContext = new String[1];
            paramAnonymousContext[0] = localObject;
            break;
          }
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          for (;;)
          {
            i = k;
            if (NotificationManagerService.DBG)
            {
              Slog.i("NotificationService", "Exception trying to look up app enabled setting", localIllegalArgumentException);
              i = k;
              continue;
              if (NotificationManagerService.-get0()) {
                Log.d("NotificationService", "pkg " + (String)localObject + " added, replacing " + bool3);
              }
              NotificationManagerService.-get21(NotificationManagerService.this).setDefaultOPLevel(m, (String)localObject);
              NotificationManagerService.this.savePolicyFile();
            }
          }
          bool3 = true;
          break label322;
          NotificationManagerService.-get14(NotificationManagerService.this).onPackagesChanged(bool1, paramAnonymousContext);
          NotificationManagerService.-get19(NotificationManagerService.this).onPackagesChanged(bool1, paramAnonymousContext);
          NotificationManagerService.-get7(NotificationManagerService.this).onPackagesChanged(bool1, paramAnonymousContext);
          NotificationManagerService.-get21(NotificationManagerService.this).onPackagesChanged(bool1, paramAnonymousContext);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            label544:
            label668:
            i = k;
          }
        }
      }
    }
  };
  final PolicyAccess mPolicyAccess = new PolicyAccess(null);
  private AtomicFile mPolicyFile;
  private String mRankerServicePackageName;
  private NotificationRankers mRankerServices;
  private RankingHandler mRankingHandler;
  private RankingHelper mRankingHelper;
  private final HandlerThread mRankingThread = new HandlerThread("ranker", 10);
  private boolean mScreenOn = true;
  private final IBinder mService = new INotificationManager.Stub()
  {
    private void cancelNotificationFromListenerLocked(ManagedServices.ManagedServiceInfo paramAnonymousManagedServiceInfo, int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt3, int paramAnonymousInt4)
    {
      NotificationManagerService.this.cancelNotification(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousString1, paramAnonymousString2, paramAnonymousInt3, 0, 66, true, paramAnonymousInt4, 10, paramAnonymousManagedServiceInfo);
    }
    
    private boolean checkPackagePolicyAccess(String paramAnonymousString)
    {
      return NotificationManagerService.this.mPolicyAccess.isPackageGranted(paramAnonymousString);
    }
    
    private boolean checkPolicyAccess(String paramAnonymousString)
    {
      boolean bool = true;
      try
      {
        int i = ActivityManager.checkComponentPermission("android.permission.MANAGE_NOTIFICATIONS", NotificationManagerService.this.getContext().getPackageManager().getPackageUidAsUser(paramAnonymousString, UserHandle.getCallingUserId()), -1, true);
        if (i == 0) {
          return true;
        }
      }
      catch (PackageManager.NameNotFoundException paramAnonymousString)
      {
        return false;
      }
      if (!checkPackagePolicyAccess(paramAnonymousString)) {
        bool = NotificationManagerService.-get14(NotificationManagerService.this).isComponentEnabledForPackage(paramAnonymousString);
      }
      return bool;
    }
    
    private void enforcePolicyAccess(int paramAnonymousInt, String paramAnonymousString)
    {
      if (NotificationManagerService.this.getContext().checkCallingPermission("android.permission.MANAGE_NOTIFICATIONS") == 0) {
        return;
      }
      int i = 0;
      String[] arrayOfString = NotificationManagerService.this.getContext().getPackageManager().getPackagesForUid(paramAnonymousInt);
      int j = arrayOfString.length;
      paramAnonymousInt = 0;
      while (paramAnonymousInt < j)
      {
        if (checkPolicyAccess(arrayOfString[paramAnonymousInt])) {
          i = 1;
        }
        paramAnonymousInt += 1;
      }
      if (i == 0)
      {
        Slog.w("NotificationService", "Notification policy access denied calling " + paramAnonymousString);
        throw new SecurityException("Notification policy access denied");
      }
    }
    
    private void enforcePolicyAccess(String paramAnonymousString1, String paramAnonymousString2)
    {
      if (NotificationManagerService.this.getContext().checkCallingPermission("android.permission.MANAGE_NOTIFICATIONS") == 0) {
        return;
      }
      NotificationManagerService.-wrap15(paramAnonymousString1);
      if (!checkPolicyAccess(paramAnonymousString1))
      {
        Slog.w("NotificationService", "Notification policy access denied calling " + paramAnonymousString2);
        throw new SecurityException("Notification policy access denied");
      }
    }
    
    private void enforceSystemOrSystemUI(String paramAnonymousString)
    {
      if (NotificationManagerService.-wrap1()) {
        return;
      }
      NotificationManagerService.this.getContext().enforceCallingPermission("android.permission.STATUS_BAR_SERVICE", paramAnonymousString);
    }
    
    private void enforceSystemOrSystemUIOrSamePackage(String paramAnonymousString1, String paramAnonymousString2)
    {
      try
      {
        NotificationManagerService.-wrap16(paramAnonymousString1);
        return;
      }
      catch (SecurityException paramAnonymousString1)
      {
        NotificationManagerService.this.getContext().enforceCallingPermission("android.permission.STATUS_BAR_SERVICE", paramAnonymousString2);
      }
    }
    
    private void enforceSystemOrSystemUIOrVolume(String paramAnonymousString)
    {
      if (NotificationManagerService.this.mAudioManagerInternal != null)
      {
        int i = NotificationManagerService.this.mAudioManagerInternal.getVolumeControllerUid();
        if ((i > 0) && (Binder.getCallingUid() == i)) {
          return;
        }
      }
      enforceSystemOrSystemUI(paramAnonymousString);
    }
    
    public String addAutomaticZenRule(AutomaticZenRule paramAnonymousAutomaticZenRule)
      throws RemoteException
    {
      Preconditions.checkNotNull(paramAnonymousAutomaticZenRule, "automaticZenRule is null");
      Preconditions.checkNotNull(paramAnonymousAutomaticZenRule.getName(), "Name is null");
      Preconditions.checkNotNull(paramAnonymousAutomaticZenRule.getOwner(), "Owner is null");
      Preconditions.checkNotNull(paramAnonymousAutomaticZenRule.getConditionId(), "ConditionId is null");
      enforcePolicyAccess(Binder.getCallingUid(), "addAutomaticZenRule");
      return NotificationManagerService.-get26(NotificationManagerService.this).addAutomaticZenRule(paramAnonymousAutomaticZenRule, "addAutomaticZenRule");
    }
    
    /* Error */
    public void applyAdjustmentFromRankerService(INotificationListener paramAnonymousINotificationListener, Adjustment paramAnonymousAdjustment)
      throws RemoteException
    {
      // Byte code:
      //   0: invokestatic 204	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore_3
      //   4: aload_0
      //   5: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   8: getfield 208	com/android/server/notification/NotificationManagerService:mNotificationList	Ljava/util/ArrayList;
      //   11: astore 5
      //   13: aload 5
      //   15: monitorenter
      //   16: aload_0
      //   17: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   20: invokestatic 212	com/android/server/notification/NotificationManagerService:-get19	(Lcom/android/server/notification/NotificationManagerService;)Lcom/android/server/notification/NotificationManagerService$NotificationRankers;
      //   23: aload_1
      //   24: invokevirtual 218	com/android/server/notification/NotificationManagerService$NotificationRankers:checkServiceTokenLocked	(Landroid/os/IInterface;)Lcom/android/server/notification/ManagedServices$ManagedServiceInfo;
      //   27: pop
      //   28: aload_0
      //   29: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   32: aload_2
      //   33: invokestatic 222	com/android/server/notification/NotificationManagerService:-wrap11	(Lcom/android/server/notification/NotificationManagerService;Landroid/service/notification/Adjustment;)V
      //   36: aload 5
      //   38: monitorexit
      //   39: aload_0
      //   40: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   43: aload_2
      //   44: invokestatic 225	com/android/server/notification/NotificationManagerService:-wrap31	(Lcom/android/server/notification/NotificationManagerService;Landroid/service/notification/Adjustment;)V
      //   47: aload_0
      //   48: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   51: invokestatic 229	com/android/server/notification/NotificationManagerService:-get20	(Lcom/android/server/notification/NotificationManagerService;)Lcom/android/server/notification/RankingHandler;
      //   54: invokeinterface 234 1 0
      //   59: lload_3
      //   60: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   63: return
      //   64: astore_1
      //   65: aload 5
      //   67: monitorexit
      //   68: aload_1
      //   69: athrow
      //   70: astore_1
      //   71: lload_3
      //   72: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   75: aload_1
      //   76: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	77	0	this	5
      //   0	77	1	paramAnonymousINotificationListener	INotificationListener
      //   0	77	2	paramAnonymousAdjustment	Adjustment
      //   3	69	3	l	long
      // Exception table:
      //   from	to	target	type
      //   16	36	64	finally
      //   4	16	70	finally
      //   36	59	70	finally
      //   65	70	70	finally
    }
    
    public void applyAdjustmentsFromRankerService(INotificationListener paramAnonymousINotificationListener, List<Adjustment> paramAnonymousList)
      throws RemoteException
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        synchronized (NotificationManagerService.this.mNotificationList)
        {
          NotificationManagerService.-get19(NotificationManagerService.this).checkServiceTokenLocked(paramAnonymousINotificationListener);
          paramAnonymousINotificationListener = paramAnonymousList.iterator();
          if (paramAnonymousINotificationListener.hasNext())
          {
            Adjustment localAdjustment = (Adjustment)paramAnonymousINotificationListener.next();
            NotificationManagerService.-wrap11(NotificationManagerService.this, localAdjustment);
          }
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      paramAnonymousINotificationListener = paramAnonymousList.iterator();
      while (paramAnonymousINotificationListener.hasNext())
      {
        paramAnonymousList = (Adjustment)paramAnonymousINotificationListener.next();
        NotificationManagerService.-wrap31(NotificationManagerService.this, paramAnonymousList);
      }
      NotificationManagerService.-get20(NotificationManagerService.this).requestSort();
      Binder.restoreCallingIdentity(l);
    }
    
    public void applyRestore(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt)
    {
      String str = null;
      if (NotificationManagerService.DBG)
      {
        StringBuilder localStringBuilder = new StringBuilder().append("applyRestore u=").append(paramAnonymousInt).append(" payload=");
        if (paramAnonymousArrayOfByte != null) {
          str = new String(paramAnonymousArrayOfByte, StandardCharsets.UTF_8);
        }
        Slog.d("NotificationService", str);
      }
      if (paramAnonymousArrayOfByte == null)
      {
        Slog.w("NotificationService", "applyRestore: no payload to restore for user " + paramAnonymousInt);
        return;
      }
      if (paramAnonymousInt != 0)
      {
        Slog.w("NotificationService", "applyRestore: cannot restore policy for user " + paramAnonymousInt);
        return;
      }
      paramAnonymousArrayOfByte = new ByteArrayInputStream(paramAnonymousArrayOfByte);
      try
      {
        NotificationManagerService.-wrap32(NotificationManagerService.this, paramAnonymousArrayOfByte, true);
        NotificationManagerService.this.savePolicyFile();
        return;
      }
      catch (NumberFormatException|XmlPullParserException|IOException paramAnonymousArrayOfByte)
      {
        Slog.w("NotificationService", "applyRestore: error reading payload", paramAnonymousArrayOfByte);
      }
    }
    
    public boolean areNotificationsEnabled(String paramAnonymousString)
    {
      return areNotificationsEnabledForPackage(paramAnonymousString, Binder.getCallingUid());
    }
    
    public boolean areNotificationsEnabledForPackage(String paramAnonymousString, int paramAnonymousInt)
    {
      NotificationManagerService.-wrap16(paramAnonymousString);
      return (NotificationManagerService.-get3(NotificationManagerService.this).checkOpNoThrow(11, paramAnonymousInt, paramAnonymousString) == 0) && (!NotificationManagerService.-wrap2(NotificationManagerService.this, paramAnonymousString, paramAnonymousInt));
    }
    
    public void cancelAllNotifications(String paramAnonymousString, int paramAnonymousInt)
    {
      NotificationManagerService.-wrap16(paramAnonymousString);
      paramAnonymousInt = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramAnonymousInt, true, false, "cancelAllNotifications", paramAnonymousString);
      NotificationManagerService.this.cancelAllNotificationsInt(Binder.getCallingUid(), Binder.getCallingPid(), paramAnonymousString, 0, 64, true, paramAnonymousInt, 9, null);
    }
    
    /* Error */
    public void cancelNotificationFromListener(INotificationListener paramAnonymousINotificationListener, String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt)
    {
      // Byte code:
      //   0: invokestatic 152	android/os/Binder:getCallingUid	()I
      //   3: istore 5
      //   5: invokestatic 337	android/os/Binder:getCallingPid	()I
      //   8: istore 6
      //   10: invokestatic 204	android/os/Binder:clearCallingIdentity	()J
      //   13: lstore 7
      //   15: aload_0
      //   16: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   19: getfield 208	com/android/server/notification/NotificationManagerService:mNotificationList	Ljava/util/ArrayList;
      //   22: astore 9
      //   24: aload 9
      //   26: monitorenter
      //   27: aload_0
      //   28: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   31: invokestatic 74	com/android/server/notification/NotificationManagerService:-get14	(Lcom/android/server/notification/NotificationManagerService;)Lcom/android/server/notification/NotificationManagerService$NotificationListeners;
      //   34: aload_1
      //   35: invokevirtual 349	com/android/server/notification/NotificationManagerService$NotificationListeners:checkServiceTokenLocked	(Landroid/os/IInterface;)Lcom/android/server/notification/ManagedServices$ManagedServiceInfo;
      //   38: astore_1
      //   39: aload_1
      //   40: invokevirtual 354	com/android/server/notification/ManagedServices$ManagedServiceInfo:supportsProfiles	()Z
      //   43: ifeq +47 -> 90
      //   46: ldc 93
      //   48: new 95	java/lang/StringBuilder
      //   51: dup
      //   52: invokespecial 96	java/lang/StringBuilder:<init>	()V
      //   55: ldc_w 356
      //   58: invokevirtual 102	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   61: aload_1
      //   62: getfield 360	com/android/server/notification/ManagedServices$ManagedServiceInfo:component	Landroid/content/ComponentName;
      //   65: invokevirtual 363	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   68: ldc_w 365
      //   71: invokevirtual 102	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   74: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   77: invokestatic 370	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   80: pop
      //   81: aload 9
      //   83: monitorexit
      //   84: lload 7
      //   86: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   89: return
      //   90: aload_0
      //   91: aload_1
      //   92: iload 5
      //   94: iload 6
      //   96: aload_2
      //   97: aload_3
      //   98: iload 4
      //   100: aload_1
      //   101: getfield 374	com/android/server/notification/ManagedServices$ManagedServiceInfo:userid	I
      //   104: invokespecial 376	com/android/server/notification/NotificationManagerService$5:cancelNotificationFromListenerLocked	(Lcom/android/server/notification/ManagedServices$ManagedServiceInfo;IILjava/lang/String;Ljava/lang/String;II)V
      //   107: goto -26 -> 81
      //   110: astore_1
      //   111: aload 9
      //   113: monitorexit
      //   114: aload_1
      //   115: athrow
      //   116: astore_1
      //   117: lload 7
      //   119: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   122: aload_1
      //   123: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	124	0	this	5
      //   0	124	1	paramAnonymousINotificationListener	INotificationListener
      //   0	124	2	paramAnonymousString1	String
      //   0	124	3	paramAnonymousString2	String
      //   0	124	4	paramAnonymousInt	int
      //   3	90	5	i	int
      //   8	87	6	j	int
      //   13	105	7	l	long
      // Exception table:
      //   from	to	target	type
      //   27	81	110	finally
      //   90	107	110	finally
      //   15	27	116	finally
      //   81	84	116	finally
      //   111	116	116	finally
    }
    
    public void cancelNotificationWithTag(String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      NotificationManagerService.-wrap16(paramAnonymousString1);
      int j = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramAnonymousInt2, true, false, "cancelNotificationWithTag", paramAnonymousString1);
      NotificationManagerService localNotificationManagerService = NotificationManagerService.this;
      int k = Binder.getCallingUid();
      int m = Binder.getCallingPid();
      if (Binder.getCallingUid() == 1000)
      {
        paramAnonymousInt2 = 0;
        if (Binder.getCallingUid() != 1000) {
          break label95;
        }
      }
      label95:
      for (int i = 0;; i = 1024)
      {
        localNotificationManagerService.cancelNotification(k, m, paramAnonymousString1, paramAnonymousString2, paramAnonymousInt1, 0, paramAnonymousInt2 | i, false, j, 8, null);
        return;
        paramAnonymousInt2 = 64;
        break;
      }
    }
    
    public void cancelNotificationsFromListener(INotificationListener paramAnonymousINotificationListener, String[] paramAnonymousArrayOfString)
    {
      int j = Binder.getCallingUid();
      int k = Binder.getCallingPid();
      long l = Binder.clearCallingIdentity();
      for (;;)
      {
        int i;
        try
        {
          synchronized (NotificationManagerService.this.mNotificationList)
          {
            paramAnonymousINotificationListener = NotificationManagerService.-get14(NotificationManagerService.this).checkServiceTokenLocked(paramAnonymousINotificationListener);
            if (paramAnonymousArrayOfString == null) {
              break label204;
            }
            int m = paramAnonymousArrayOfString.length;
            i = 0;
            if (i >= m) {
              break label226;
            }
            NotificationRecord localNotificationRecord = (NotificationRecord)NotificationManagerService.this.mNotificationsByKey.get(paramAnonymousArrayOfString[i]);
            if (localNotificationRecord == null) {
              break label235;
            }
            int n = localNotificationRecord.sbn.getUserId();
            if ((n == paramAnonymousINotificationListener.userid) || (n == -1) || (NotificationManagerService.-get24(NotificationManagerService.this).isCurrentProfile(n))) {
              cancelNotificationFromListenerLocked(paramAnonymousINotificationListener, j, k, localNotificationRecord.sbn.getPackageName(), localNotificationRecord.sbn.getTag(), localNotificationRecord.sbn.getId(), n);
            }
          }
          throw new SecurityException("Disallowed call from listener: " + paramAnonymousINotificationListener.service);
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        label204:
        NotificationManagerService.this.cancelAllLocked(j, k, paramAnonymousINotificationListener.userid, 11, paramAnonymousINotificationListener, paramAnonymousINotificationListener.supportsProfiles());
        label226:
        Binder.restoreCallingIdentity(l);
        return;
        label235:
        i += 1;
      }
    }
    
    public void cancelToast(String paramAnonymousString, ITransientNotification paramAnonymousITransientNotification)
    {
      Slog.i("NotificationService", "cancelToast pkg=" + paramAnonymousString + " callback=" + paramAnonymousITransientNotification);
      if ((paramAnonymousString == null) || (paramAnonymousITransientNotification == null))
      {
        Slog.e("NotificationService", "Not cancelling notification. pkg=" + paramAnonymousString + " callback=" + paramAnonymousITransientNotification);
        return;
      }
      synchronized (NotificationManagerService.this.mToastQueue)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          int i = NotificationManagerService.this.indexOfToastLocked(paramAnonymousString, paramAnonymousITransientNotification);
          if (i >= 0) {
            NotificationManagerService.this.cancelToastLocked(i);
          }
          for (;;)
          {
            Binder.restoreCallingIdentity(l);
            return;
            Slog.w("NotificationService", "Toast already cancelled. pkg=" + paramAnonymousString + " callback=" + paramAnonymousITransientNotification);
          }
          paramAnonymousString = finally;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
    }
    
    protected void dump(FileDescriptor paramAnonymousFileDescriptor, PrintWriter paramAnonymousPrintWriter, String[] paramAnonymousArrayOfString)
    {
      if (NotificationManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramAnonymousPrintWriter.println("Permission Denial: can't dump NotificationManager from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      paramAnonymousFileDescriptor = NotificationManagerService.DumpFilter.parseFromArguments(paramAnonymousArrayOfString);
      if ((paramAnonymousFileDescriptor != null) && (paramAnonymousFileDescriptor.stats))
      {
        NotificationManagerService.-wrap21(NotificationManagerService.this, paramAnonymousPrintWriter, paramAnonymousFileDescriptor);
        return;
      }
      NotificationManagerService.this.dumpImpl(paramAnonymousPrintWriter, paramAnonymousFileDescriptor);
    }
    
    public void enqueueNotificationWithTag(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, int paramAnonymousInt1, Notification paramAnonymousNotification, int[] paramAnonymousArrayOfInt, int paramAnonymousInt2)
      throws RemoteException
    {
      NotificationManagerService.this.enqueueNotificationInternal(paramAnonymousString1, paramAnonymousString2, Binder.getCallingUid(), Binder.getCallingPid(), paramAnonymousString3, paramAnonymousInt1, paramAnonymousNotification, paramAnonymousArrayOfInt, paramAnonymousInt2);
    }
    
    public void enqueueToast(String paramAnonymousString, ITransientNotification paramAnonymousITransientNotification, int paramAnonymousInt)
    {
      if (NotificationManagerService.DBG) {
        Slog.i("NotificationService", "enqueueToast pkg=" + paramAnonymousString + " callback=" + paramAnonymousITransientNotification + " duration=" + paramAnonymousInt);
      }
      if ((paramAnonymousString == null) || (paramAnonymousITransientNotification == null))
      {
        Slog.e("NotificationService", "Not doing toast. pkg=" + paramAnonymousString + " callback=" + paramAnonymousITransientNotification);
        return;
      }
      boolean bool1;
      if (!NotificationManagerService.-wrap1())
      {
        bool1 = "android".equals(paramAnonymousString);
        boolean bool2 = NotificationManagerService.-wrap2(NotificationManagerService.this, paramAnonymousString, Binder.getCallingUid());
        if (((NotificationManagerService.-wrap5(NotificationManagerService.this, paramAnonymousString, Binder.getCallingUid())) && (!bool2)) || (bool1)) {
          break label204;
        }
        paramAnonymousITransientNotification = new StringBuilder().append("Suppressing toast from package ").append(paramAnonymousString);
        if (!bool2) {
          break label197;
        }
      }
      label197:
      for (paramAnonymousString = " due to package suspended by administrator.";; paramAnonymousString = " by user request.")
      {
        Slog.e("NotificationService", paramAnonymousString);
        return;
        bool1 = true;
        break;
      }
      for (;;)
      {
        label204:
        Binder localBinder;
        synchronized (NotificationManagerService.this.mToastQueue)
        {
          int m = Binder.getCallingPid();
          int n = Binder.getCallingUid();
          long l = Binder.clearCallingIdentity();
          try
          {
            int i = NotificationManagerService.this.indexOfToastLocked(paramAnonymousString, paramAnonymousITransientNotification);
            if (i >= 0)
            {
              ((NotificationManagerService.ToastRecord)NotificationManagerService.this.mToastQueue.get(i)).update(paramAnonymousInt);
              paramAnonymousInt = i;
              if (paramAnonymousInt == 0) {
                NotificationManagerService.this.showNextToastLocked();
              }
              Binder.restoreCallingIdentity(l);
              return;
            }
            if (!bool1)
            {
              int k = 0;
              int i1 = NotificationManagerService.this.mToastQueue.size();
              i = 0;
              if (i < i1)
              {
                int j = k;
                if (((NotificationManagerService.ToastRecord)NotificationManagerService.this.mToastQueue.get(i)).pkg.equals(paramAnonymousString))
                {
                  k += 1;
                  j = k;
                  if (k >= 50)
                  {
                    Slog.e("NotificationService", "Package has already posted " + k + " toasts. Not showing more. Package=" + paramAnonymousString);
                    Binder.restoreCallingIdentity(l);
                    return;
                  }
                }
                i += 1;
                k = j;
                continue;
              }
            }
            localBinder = new Binder();
            if ("com.oneplus.screenshot".equals(paramAnonymousString))
            {
              NotificationManagerService.-get25(NotificationManagerService.this).addWindowToken(localBinder, 2303);
              paramAnonymousString = new NotificationManagerService.ToastRecord(m, paramAnonymousString, paramAnonymousITransientNotification, paramAnonymousInt, localBinder, n);
              NotificationManagerService.this.mToastQueue.add(paramAnonymousString);
              paramAnonymousInt = NotificationManagerService.this.mToastQueue.size() - 1;
              NotificationManagerService.this.keepProcessAliveIfNeededLocked(m);
              continue;
              paramAnonymousString = finally;
            }
          }
          finally
          {
            Binder.restoreCallingIdentity(l);
          }
        }
        NotificationManagerService.-get25(NotificationManagerService.this).addWindowToken(localBinder, 2005);
      }
    }
    
    public StatusBarNotification[] getActiveNotifications(String arg1)
    {
      NotificationManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.ACCESS_NOTIFICATIONS", "NotificationManagerService.getActiveNotifications");
      StatusBarNotification[] arrayOfStatusBarNotification = null;
      int i = Binder.getCallingUid();
      if (NotificationManagerService.-get3(NotificationManagerService.this).noteOpNoThrow(25, i, ???) == 0) {}
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        arrayOfStatusBarNotification = new StatusBarNotification[NotificationManagerService.this.mNotificationList.size()];
        int j = NotificationManagerService.this.mNotificationList.size();
        i = 0;
        while (i < j)
        {
          arrayOfStatusBarNotification[i] = ((NotificationRecord)NotificationManagerService.this.mNotificationList.get(i)).sbn;
          i += 1;
        }
        return arrayOfStatusBarNotification;
      }
    }
    
    public ParceledListSlice<StatusBarNotification> getActiveNotificationsFromListener(INotificationListener paramAnonymousINotificationListener, String[] paramAnonymousArrayOfString, int paramAnonymousInt)
    {
      for (;;)
      {
        int i;
        ArrayList localArrayList2;
        int k;
        synchronized (NotificationManagerService.this.mNotificationList)
        {
          ManagedServices.ManagedServiceInfo localManagedServiceInfo = NotificationManagerService.-get14(NotificationManagerService.this).checkServiceTokenLocked(paramAnonymousINotificationListener);
          if (paramAnonymousArrayOfString == null) {
            break label196;
          }
          i = 1;
          int j;
          if (i != 0)
          {
            j = paramAnonymousArrayOfString.length;
            localArrayList2 = new ArrayList(j);
            k = 0;
            if (k >= j) {
              break label168;
            }
            if (i != 0)
            {
              paramAnonymousINotificationListener = (NotificationRecord)NotificationManagerService.this.mNotificationsByKey.get(paramAnonymousArrayOfString[k]);
              break label183;
            }
          }
          else
          {
            j = NotificationManagerService.this.mNotificationList.size();
            continue;
          }
          paramAnonymousINotificationListener = (NotificationRecord)NotificationManagerService.this.mNotificationList.get(k);
          break label183;
          paramAnonymousINotificationListener = paramAnonymousINotificationListener.sbn;
          if (!NotificationManagerService.-wrap4(NotificationManagerService.this, paramAnonymousINotificationListener, localManagedServiceInfo)) {
            break label187;
          }
          if (paramAnonymousInt == 0) {
            localArrayList2.add(paramAnonymousINotificationListener);
          }
        }
        paramAnonymousINotificationListener = paramAnonymousINotificationListener.cloneLight();
        continue;
        label168:
        paramAnonymousINotificationListener = new ParceledListSlice(localArrayList2);
        return paramAnonymousINotificationListener;
        label183:
        if (paramAnonymousINotificationListener == null)
        {
          label187:
          k += 1;
          continue;
          label196:
          i = 0;
        }
      }
    }
    
    public ParceledListSlice<StatusBarNotification> getAppActiveNotifications(String paramAnonymousString, int paramAnonymousInt)
    {
      NotificationManagerService.-wrap16(paramAnonymousString);
      int i = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramAnonymousInt, true, false, "getAppActiveNotifications", paramAnonymousString);
      ArrayList localArrayList2 = new ArrayList(NotificationManagerService.this.mNotificationList.size());
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        int j = NotificationManagerService.this.mNotificationList.size();
        paramAnonymousInt = 0;
        while (paramAnonymousInt < j)
        {
          StatusBarNotification localStatusBarNotification = ((NotificationRecord)NotificationManagerService.this.mNotificationList.get(paramAnonymousInt)).sbn;
          if ((localStatusBarNotification.getPackageName().equals(paramAnonymousString)) && (localStatusBarNotification.getUserId() == i) && ((localStatusBarNotification.getNotification().flags & 0x400) == 0)) {
            localArrayList2.add(new StatusBarNotification(localStatusBarNotification.getPackageName(), localStatusBarNotification.getOpPkg(), localStatusBarNotification.getId(), localStatusBarNotification.getTag(), localStatusBarNotification.getUid(), localStatusBarNotification.getInitialPid(), 0, localStatusBarNotification.getNotification().clone(), localStatusBarNotification.getUser(), localStatusBarNotification.getPostTime()));
          }
          paramAnonymousInt += 1;
        }
        return new ParceledListSlice(localArrayList2);
      }
    }
    
    public AutomaticZenRule getAutomaticZenRule(String paramAnonymousString)
      throws RemoteException
    {
      Preconditions.checkNotNull(paramAnonymousString, "Id is null");
      enforcePolicyAccess(Binder.getCallingUid(), "getAutomaticZenRule");
      return NotificationManagerService.-get26(NotificationManagerService.this).getAutomaticZenRule(paramAnonymousString);
    }
    
    public byte[] getBackupPayload(int paramAnonymousInt)
    {
      if (NotificationManagerService.DBG) {
        Slog.d("NotificationService", "getBackupPayload u=" + paramAnonymousInt);
      }
      if (paramAnonymousInt != 0)
      {
        Slog.w("NotificationService", "getBackupPayload: cannot backup policy for user " + paramAnonymousInt);
        return null;
      }
      Object localObject = new ByteArrayOutputStream();
      try
      {
        NotificationManagerService.-wrap38(NotificationManagerService.this, (OutputStream)localObject, true);
        localObject = ((ByteArrayOutputStream)localObject).toByteArray();
        return (byte[])localObject;
      }
      catch (IOException localIOException)
      {
        Slog.w("NotificationService", "getBackupPayload: error writing payload for user " + paramAnonymousInt, localIOException);
      }
      return null;
    }
    
    public ComponentName getEffectsSuppressor()
    {
      enforceSystemOrSystemUIOrVolume("INotificationManager.getEffectsSuppressor");
      if (!NotificationManagerService.-get10(NotificationManagerService.this).isEmpty()) {
        return (ComponentName)NotificationManagerService.-get10(NotificationManagerService.this).get(0);
      }
      return null;
    }
    
    public int getHintsFromListener(INotificationListener arg1)
    {
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        int i = NotificationManagerService.-get13(NotificationManagerService.this);
        return i;
      }
    }
    
    public StatusBarNotification[] getHistoricalNotifications(String paramAnonymousString, int paramAnonymousInt)
    {
      NotificationManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.ACCESS_NOTIFICATIONS", "NotificationManagerService.getHistoricalNotifications");
      ??? = null;
      int i = Binder.getCallingUid();
      if (NotificationManagerService.-get3(NotificationManagerService.this).noteOpNoThrow(25, i, paramAnonymousString) == 0) {}
      synchronized (NotificationManagerService.-get5(NotificationManagerService.this))
      {
        paramAnonymousString = NotificationManagerService.-get5(NotificationManagerService.this).getArray(paramAnonymousInt);
        ??? = paramAnonymousString;
        return (StatusBarNotification[])???;
      }
    }
    
    public int getImportance(String paramAnonymousString, int paramAnonymousInt)
    {
      enforceSystemOrSystemUI("Caller not system or systemui");
      return NotificationManagerService.-get21(NotificationManagerService.this).getImportance(paramAnonymousString, paramAnonymousInt);
    }
    
    public int getInterruptionFilterFromListener(INotificationListener arg1)
      throws RemoteException
    {
      synchronized (NotificationManagerService.-get16(NotificationManagerService.this))
      {
        int i = NotificationManagerService.-get12(NotificationManagerService.this);
        return i;
      }
    }
    
    public NotificationManager.Policy getNotificationPolicy(String paramAnonymousString)
    {
      enforcePolicyAccess(paramAnonymousString, "getNotificationPolicy");
      long l = Binder.clearCallingIdentity();
      try
      {
        paramAnonymousString = NotificationManagerService.-get26(NotificationManagerService.this).getNotificationPolicy();
        return paramAnonymousString;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public int getOPLevel(String paramAnonymousString, int paramAnonymousInt)
    {
      enforceSystemOrSystemUI("Caller not system or systemui");
      return NotificationManagerService.-get21(NotificationManagerService.this).getOPLevel(paramAnonymousString, paramAnonymousInt);
    }
    
    public int getOnePlusPackagePriority(String paramAnonymousString, int paramAnonymousInt)
    {
      return NotificationManagerService.-get21(NotificationManagerService.this).getPriority(paramAnonymousString, paramAnonymousInt);
    }
    
    public int getPackageImportance(String paramAnonymousString)
    {
      NotificationManagerService.-wrap16(paramAnonymousString);
      return NotificationManagerService.-get21(NotificationManagerService.this).getImportance(paramAnonymousString, Binder.getCallingUid());
    }
    
    public String[] getPackagesRequestingNotificationPolicyAccess()
      throws RemoteException
    {
      enforceSystemOrSystemUI("request policy access packages");
      long l = Binder.clearCallingIdentity();
      try
      {
        String[] arrayOfString = NotificationManagerService.this.mPolicyAccess.getRequestingPackages();
        return arrayOfString;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public int getPriority(String paramAnonymousString, int paramAnonymousInt)
    {
      NotificationManagerService.-wrap17();
      return NotificationManagerService.-get21(NotificationManagerService.this).getPriority(paramAnonymousString, paramAnonymousInt);
    }
    
    public int getRuleInstanceCount(ComponentName paramAnonymousComponentName)
      throws RemoteException
    {
      Preconditions.checkNotNull(paramAnonymousComponentName, "Owner is null");
      enforceSystemOrSystemUI("getRuleInstanceCount");
      return NotificationManagerService.-get26(NotificationManagerService.this).getCurrentInstanceCount(paramAnonymousComponentName);
    }
    
    public int getVisibilityOverride(String paramAnonymousString, int paramAnonymousInt)
    {
      NotificationManagerService.-wrap17();
      return NotificationManagerService.-get21(NotificationManagerService.this).getVisibilityOverride(paramAnonymousString, paramAnonymousInt);
    }
    
    public int getZenMode()
    {
      return NotificationManagerService.-get26(NotificationManagerService.this).getZenMode();
    }
    
    public ZenModeConfig getZenModeConfig()
    {
      enforceSystemOrSystemUIOrVolume("INotificationManager.getZenModeConfig");
      return NotificationManagerService.-get26(NotificationManagerService.this).getConfig();
    }
    
    public List<ZenModeConfig.ZenRule> getZenRules()
      throws RemoteException
    {
      enforcePolicyAccess(Binder.getCallingUid(), "getAutomaticZenRules");
      return NotificationManagerService.-get26(NotificationManagerService.this).getZenRules();
    }
    
    public boolean isNotificationLedEnabled(String paramAnonymousString)
    {
      return NotificationManagerService.this.isNotificationLedEnabledImpl(paramAnonymousString);
    }
    
    public boolean isNotificationPolicyAccessGranted(String paramAnonymousString)
    {
      return checkPolicyAccess(paramAnonymousString);
    }
    
    public boolean isNotificationPolicyAccessGrantedForPackage(String paramAnonymousString)
    {
      enforceSystemOrSystemUIOrSamePackage(paramAnonymousString, "request policy access status for another package");
      return checkPolicyAccess(paramAnonymousString);
    }
    
    public boolean isSystemConditionProviderEnabled(String paramAnonymousString)
    {
      enforceSystemOrSystemUIOrVolume("INotificationManager.isSystemConditionProviderEnabled");
      return NotificationManagerService.-get7(NotificationManagerService.this).isSystemProviderEnabled(paramAnonymousString);
    }
    
    public boolean matchesCallFilter(Bundle paramAnonymousBundle)
    {
      enforceSystemOrSystemUI("INotificationManager.matchesCallFilter");
      return NotificationManagerService.-get26(NotificationManagerService.this).matchesCallFilter(Binder.getCallingUserHandle(), paramAnonymousBundle, (ValidateNotificationPeople)NotificationManagerService.-get21(NotificationManagerService.this).findExtractor(ValidateNotificationPeople.class), 3000, 1.0F);
    }
    
    public void notifyConditions(final String paramAnonymousString, final IConditionProvider paramAnonymousIConditionProvider, final Condition[] paramAnonymousArrayOfCondition)
    {
      paramAnonymousIConditionProvider = NotificationManagerService.-get7(NotificationManagerService.this).checkServiceToken(paramAnonymousIConditionProvider);
      NotificationManagerService.-wrap16(paramAnonymousString);
      NotificationManagerService.-get11(NotificationManagerService.this).post(new Runnable()
      {
        public void run()
        {
          NotificationManagerService.-get7(NotificationManagerService.this).notifyConditions(paramAnonymousString, paramAnonymousIConditionProvider, paramAnonymousArrayOfCondition);
        }
      });
    }
    
    public void registerListener(INotificationListener paramAnonymousINotificationListener, ComponentName paramAnonymousComponentName, int paramAnonymousInt)
    {
      enforceSystemOrSystemUI("INotificationManager.registerListener");
      NotificationManagerService.-get14(NotificationManagerService.this).registerService(paramAnonymousINotificationListener, paramAnonymousComponentName, paramAnonymousInt);
    }
    
    public boolean removeAutomaticZenRule(String paramAnonymousString)
      throws RemoteException
    {
      Preconditions.checkNotNull(paramAnonymousString, "Id is null");
      enforcePolicyAccess(Binder.getCallingUid(), "removeAutomaticZenRule");
      return NotificationManagerService.-get26(NotificationManagerService.this).removeAutomaticZenRule(paramAnonymousString, "removeAutomaticZenRule");
    }
    
    public boolean removeAutomaticZenRules(String paramAnonymousString)
      throws RemoteException
    {
      Preconditions.checkNotNull(paramAnonymousString, "Package name is null");
      enforceSystemOrSystemUI("removeAutomaticZenRules");
      return NotificationManagerService.-get26(NotificationManagerService.this).removeAutomaticZenRules(paramAnonymousString, "removeAutomaticZenRules");
    }
    
    /* Error */
    public void requestBindListener(ComponentName paramAnonymousComponentName)
    {
      // Byte code:
      //   0: aload_1
      //   1: invokevirtual 842	android/content/ComponentName:getPackageName	()Ljava/lang/String;
      //   4: invokestatic 137	com/android/server/notification/NotificationManagerService:-wrap16	(Ljava/lang/String;)V
      //   7: invokestatic 204	android/os/Binder:clearCallingIdentity	()J
      //   10: lstore_2
      //   11: aload_0
      //   12: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   15: invokestatic 212	com/android/server/notification/NotificationManagerService:-get19	(Lcom/android/server/notification/NotificationManagerService;)Lcom/android/server/notification/NotificationManagerService$NotificationRankers;
      //   18: aload_1
      //   19: invokevirtual 846	com/android/server/notification/NotificationManagerService$NotificationRankers:isComponentEnabledForCurrentProfiles	(Landroid/content/ComponentName;)Z
      //   22: ifeq +24 -> 46
      //   25: aload_0
      //   26: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   29: invokestatic 212	com/android/server/notification/NotificationManagerService:-get19	(Lcom/android/server/notification/NotificationManagerService;)Lcom/android/server/notification/NotificationManagerService$NotificationRankers;
      //   32: astore 4
      //   34: aload 4
      //   36: aload_1
      //   37: iconst_1
      //   38: invokevirtual 852	com/android/server/notification/ManagedServices:setComponentState	(Landroid/content/ComponentName;Z)V
      //   41: lload_2
      //   42: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   45: return
      //   46: aload_0
      //   47: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   50: invokestatic 74	com/android/server/notification/NotificationManagerService:-get14	(Lcom/android/server/notification/NotificationManagerService;)Lcom/android/server/notification/NotificationManagerService$NotificationListeners;
      //   53: astore 4
      //   55: goto -21 -> 34
      //   58: astore_1
      //   59: lload_2
      //   60: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   63: aload_1
      //   64: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	65	0	this	5
      //   0	65	1	paramAnonymousComponentName	ComponentName
      //   10	50	2	l	long
      //   32	22	4	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   11	34	58	finally
      //   34	41	58	finally
      //   46	55	58	finally
    }
    
    /* Error */
    public void requestHintsFromListener(INotificationListener paramAnonymousINotificationListener, int paramAnonymousInt)
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore_3
      //   2: invokestatic 204	android/os/Binder:clearCallingIdentity	()J
      //   5: lstore 4
      //   7: aload_0
      //   8: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   11: getfield 208	com/android/server/notification/NotificationManagerService:mNotificationList	Ljava/util/ArrayList;
      //   14: astore 6
      //   16: aload 6
      //   18: monitorenter
      //   19: aload_0
      //   20: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   23: invokestatic 74	com/android/server/notification/NotificationManagerService:-get14	(Lcom/android/server/notification/NotificationManagerService;)Lcom/android/server/notification/NotificationManagerService$NotificationListeners;
      //   26: aload_1
      //   27: invokevirtual 349	com/android/server/notification/NotificationManagerService$NotificationListeners:checkServiceTokenLocked	(Landroid/os/IInterface;)Lcom/android/server/notification/ManagedServices$ManagedServiceInfo;
      //   30: astore_1
      //   31: iload_2
      //   32: bipush 7
      //   34: iand
      //   35: ifeq +5 -> 40
      //   38: iconst_1
      //   39: istore_3
      //   40: iload_3
      //   41: ifeq +35 -> 76
      //   44: aload_0
      //   45: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   48: aload_1
      //   49: iload_2
      //   50: invokestatic 858	com/android/server/notification/NotificationManagerService:-wrap10	(Lcom/android/server/notification/NotificationManagerService;Lcom/android/server/notification/ManagedServices$ManagedServiceInfo;I)V
      //   53: aload_0
      //   54: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   57: invokestatic 861	com/android/server/notification/NotificationManagerService:-wrap36	(Lcom/android/server/notification/NotificationManagerService;)V
      //   60: aload_0
      //   61: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   64: invokestatic 864	com/android/server/notification/NotificationManagerService:-wrap34	(Lcom/android/server/notification/NotificationManagerService;)V
      //   67: aload 6
      //   69: monitorexit
      //   70: lload 4
      //   72: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   75: return
      //   76: aload_0
      //   77: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   80: aload_1
      //   81: iload_2
      //   82: invokestatic 868	com/android/server/notification/NotificationManagerService:-wrap7	(Lcom/android/server/notification/NotificationManagerService;Lcom/android/server/notification/ManagedServices$ManagedServiceInfo;I)Z
      //   85: pop
      //   86: goto -33 -> 53
      //   89: astore_1
      //   90: aload 6
      //   92: monitorexit
      //   93: aload_1
      //   94: athrow
      //   95: astore_1
      //   96: lload 4
      //   98: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   101: aload_1
      //   102: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	103	0	this	5
      //   0	103	1	paramAnonymousINotificationListener	INotificationListener
      //   0	103	2	paramAnonymousInt	int
      //   1	40	3	i	int
      //   5	92	4	l	long
      // Exception table:
      //   from	to	target	type
      //   19	31	89	finally
      //   44	53	89	finally
      //   53	67	89	finally
      //   76	86	89	finally
      //   7	19	95	finally
      //   67	70	95	finally
      //   90	95	95	finally
    }
    
    /* Error */
    public void requestInterruptionFilterFromListener(INotificationListener paramAnonymousINotificationListener, int paramAnonymousInt)
      throws RemoteException
    {
      // Byte code:
      //   0: invokestatic 204	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore_3
      //   4: aload_0
      //   5: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   8: getfield 208	com/android/server/notification/NotificationManagerService:mNotificationList	Ljava/util/ArrayList;
      //   11: astore 5
      //   13: aload 5
      //   15: monitorenter
      //   16: aload_0
      //   17: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   20: invokestatic 74	com/android/server/notification/NotificationManagerService:-get14	(Lcom/android/server/notification/NotificationManagerService;)Lcom/android/server/notification/NotificationManagerService$NotificationListeners;
      //   23: aload_1
      //   24: invokevirtual 349	com/android/server/notification/NotificationManagerService$NotificationListeners:checkServiceTokenLocked	(Landroid/os/IInterface;)Lcom/android/server/notification/ManagedServices$ManagedServiceInfo;
      //   27: astore_1
      //   28: aload_0
      //   29: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   32: invokestatic 192	com/android/server/notification/NotificationManagerService:-get26	(Lcom/android/server/notification/NotificationManagerService;)Lcom/android/server/notification/ZenModeHelper;
      //   35: aload_1
      //   36: getfield 360	com/android/server/notification/ManagedServices$ManagedServiceInfo:component	Landroid/content/ComponentName;
      //   39: iload_2
      //   40: invokevirtual 873	com/android/server/notification/ZenModeHelper:requestFromListener	(Landroid/content/ComponentName;I)V
      //   43: aload_0
      //   44: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   47: invokestatic 876	com/android/server/notification/NotificationManagerService:-wrap35	(Lcom/android/server/notification/NotificationManagerService;)V
      //   50: aload 5
      //   52: monitorexit
      //   53: lload_3
      //   54: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   57: return
      //   58: astore_1
      //   59: aload 5
      //   61: monitorexit
      //   62: aload_1
      //   63: athrow
      //   64: astore_1
      //   65: lload_3
      //   66: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   69: aload_1
      //   70: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	71	0	this	5
      //   0	71	1	paramAnonymousINotificationListener	INotificationListener
      //   0	71	2	paramAnonymousInt	int
      //   3	63	3	l	long
      // Exception table:
      //   from	to	target	type
      //   16	50	58	finally
      //   4	16	64	finally
      //   50	53	64	finally
      //   59	64	64	finally
    }
    
    public void requestUnbindListener(INotificationListener paramAnonymousINotificationListener)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        paramAnonymousINotificationListener = NotificationManagerService.-get14(NotificationManagerService.this).checkServiceTokenLocked(paramAnonymousINotificationListener);
        paramAnonymousINotificationListener.getOwner().setComponentState(paramAnonymousINotificationListener.component, false);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void setImportance(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      boolean bool = false;
      enforceSystemOrSystemUI("Caller not system or systemui");
      NotificationManagerService localNotificationManagerService = NotificationManagerService.this;
      if (paramAnonymousInt2 != 0) {
        bool = true;
      }
      localNotificationManagerService.setNotificationsEnabledForPackageImpl(paramAnonymousString, paramAnonymousInt1, bool);
      NotificationManagerService.-get21(NotificationManagerService.this).setImportance(paramAnonymousString, paramAnonymousInt1, paramAnonymousInt2);
      NotificationManagerService.this.savePolicyFile();
    }
    
    public void setInterruptionFilter(String paramAnonymousString, int paramAnonymousInt)
      throws RemoteException
    {
      enforcePolicyAccess(paramAnonymousString, "setInterruptionFilter");
      int i = NotificationManager.zenModeFromInterruptionFilter(paramAnonymousInt, -1);
      if (i == -1) {
        throw new IllegalArgumentException("Invalid filter: " + paramAnonymousInt);
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        NotificationManagerService.-get26(NotificationManagerService.this).setManualZenMode(i, null, paramAnonymousString, "setInterruptionFilter");
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void setNotificationLedStatus(String paramAnonymousString, boolean paramAnonymousBoolean)
    {
      NotificationManagerService.this.setNotificationLedStatusImpl(paramAnonymousString, paramAnonymousBoolean);
    }
    
    public void setNotificationPolicy(String paramAnonymousString, NotificationManager.Policy paramAnonymousPolicy)
    {
      enforcePolicyAccess(paramAnonymousString, "setNotificationPolicy");
      long l = Binder.clearCallingIdentity();
      try
      {
        NotificationManagerService.-get26(NotificationManagerService.this).setNotificationPolicy(paramAnonymousPolicy);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public void setNotificationPolicyAccessGranted(String paramAnonymousString, boolean paramAnonymousBoolean)
      throws RemoteException
    {
      // Byte code:
      //   0: aload_0
      //   1: ldc_w 920
      //   4: invokespecial 154	com/android/server/notification/NotificationManagerService$5:enforceSystemOrSystemUI	(Ljava/lang/String;)V
      //   7: invokestatic 204	android/os/Binder:clearCallingIdentity	()J
      //   10: lstore_3
      //   11: aload_0
      //   12: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   15: getfield 208	com/android/server/notification/NotificationManagerService:mNotificationList	Ljava/util/ArrayList;
      //   18: astore 5
      //   20: aload 5
      //   22: monitorenter
      //   23: aload_0
      //   24: getfield 14	com/android/server/notification/NotificationManagerService$5:this$0	Lcom/android/server/notification/NotificationManagerService;
      //   27: getfield 30	com/android/server/notification/NotificationManagerService:mPolicyAccess	Lcom/android/server/notification/NotificationManagerService$PolicyAccess;
      //   30: aload_1
      //   31: iload_2
      //   32: invokevirtual 923	com/android/server/notification/NotificationManagerService$PolicyAccess:put	(Ljava/lang/String;Z)V
      //   35: aload 5
      //   37: monitorexit
      //   38: lload_3
      //   39: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   42: return
      //   43: astore_1
      //   44: aload 5
      //   46: monitorexit
      //   47: aload_1
      //   48: athrow
      //   49: astore_1
      //   50: lload_3
      //   51: invokestatic 238	android/os/Binder:restoreCallingIdentity	(J)V
      //   54: aload_1
      //   55: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	56	0	this	5
      //   0	56	1	paramAnonymousString	String
      //   0	56	2	paramAnonymousBoolean	boolean
      //   10	41	3	l	long
      // Exception table:
      //   from	to	target	type
      //   23	35	43	finally
      //   11	23	49	finally
      //   35	38	49	finally
      //   44	49	49	finally
    }
    
    public void setNotificationsEnabledForPackage(String paramAnonymousString, int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      NotificationManagerService.-wrap17();
      NotificationManagerService.this.setNotificationsEnabledForPackageImpl(paramAnonymousString, paramAnonymousInt, paramAnonymousBoolean);
      NotificationManagerService.-get21(NotificationManagerService.this).setEnabled(paramAnonymousString, paramAnonymousInt, paramAnonymousBoolean);
      NotificationManagerService.this.savePolicyFile();
    }
    
    public void setNotificationsShownFromListener(INotificationListener paramAnonymousINotificationListener, String[] paramAnonymousArrayOfString)
    {
      long l = Binder.clearCallingIdentity();
      for (;;)
      {
        int i;
        try
        {
          synchronized (NotificationManagerService.this.mNotificationList)
          {
            paramAnonymousINotificationListener = NotificationManagerService.-get14(NotificationManagerService.this).checkServiceTokenLocked(paramAnonymousINotificationListener);
            if (paramAnonymousArrayOfString == null) {
              break label249;
            }
            int m = paramAnonymousArrayOfString.length;
            i = 0;
            if (i >= m) {
              break label249;
            }
            NotificationRecord localNotificationRecord = (NotificationRecord)NotificationManagerService.this.mNotificationsByKey.get(paramAnonymousArrayOfString[i]);
            if (localNotificationRecord == null) {
              break label258;
            }
            int k = localNotificationRecord.sbn.getUserId();
            if ((k == paramAnonymousINotificationListener.userid) || (k == -1) || (NotificationManagerService.-get24(NotificationManagerService.this).isCurrentProfile(k)))
            {
              if (localNotificationRecord.isSeen()) {
                break label258;
              }
              if (NotificationManagerService.DBG) {
                Slog.d("NotificationService", "Marking notification as seen " + paramAnonymousArrayOfString[i]);
              }
              UsageStatsManagerInternal localUsageStatsManagerInternal = NotificationManagerService.-get4(NotificationManagerService.this);
              String str = localNotificationRecord.sbn.getPackageName();
              int j = k;
              if (k == -1) {
                j = 0;
              }
              localUsageStatsManagerInternal.reportEvent(str, j, 7);
              localNotificationRecord.setSeen();
            }
          }
          throw new SecurityException("Disallowed call from listener: " + paramAnonymousINotificationListener.service);
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        label249:
        Binder.restoreCallingIdentity(l);
        return;
        label258:
        i += 1;
      }
    }
    
    public void setOPLevel(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      enforceSystemOrSystemUI("Caller not system or systemui");
      NotificationManagerService localNotificationManagerService = NotificationManagerService.this;
      if (paramAnonymousInt2 != 2) {}
      for (boolean bool = true;; bool = false)
      {
        localNotificationManagerService.setNotificationsEnabledForPackageImpl(paramAnonymousString, paramAnonymousInt1, bool);
        NotificationManagerService.-get21(NotificationManagerService.this).setOPLevel(paramAnonymousString, paramAnonymousInt1, paramAnonymousInt2);
        NotificationManagerService.this.savePolicyFile();
        return;
      }
    }
    
    public void setOnNotificationPostedTrimFromListener(INotificationListener paramAnonymousINotificationListener, int paramAnonymousInt)
      throws RemoteException
    {
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        paramAnonymousINotificationListener = NotificationManagerService.-get14(NotificationManagerService.this).checkServiceTokenLocked(paramAnonymousINotificationListener);
        if (paramAnonymousINotificationListener == null) {
          return;
        }
        NotificationManagerService.-get14(NotificationManagerService.this).setOnNotificationPostedTrimLocked(paramAnonymousINotificationListener, paramAnonymousInt);
        return;
      }
    }
    
    public void setOnePlusVibrateInSilentMode(boolean paramAnonymousBoolean)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        NotificationManagerService.-get26(NotificationManagerService.this).setOnePlusVibrateInSilentMode(paramAnonymousBoolean);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void setPriority(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      NotificationManagerService.-wrap17();
      NotificationManagerService.-get21(NotificationManagerService.this).setPriority(paramAnonymousString, paramAnonymousInt1, paramAnonymousInt2);
      NotificationManagerService.this.savePolicyFile();
    }
    
    public void setVisibilityOverride(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      NotificationManagerService.-wrap17();
      NotificationManagerService.-get21(NotificationManagerService.this).setVisibilityOverride(paramAnonymousString, paramAnonymousInt1, paramAnonymousInt2);
      NotificationManagerService.this.savePolicyFile();
    }
    
    public void setZenMode(int paramAnonymousInt, Uri paramAnonymousUri, String paramAnonymousString)
      throws RemoteException
    {
      enforceSystemOrSystemUIOrVolume("INotificationManager.setZenMode");
      long l = Binder.clearCallingIdentity();
      try
      {
        NotificationManagerService.-get26(NotificationManagerService.this).setManualZenMode(paramAnonymousInt, paramAnonymousUri, null, paramAnonymousString);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void unregisterListener(INotificationListener paramAnonymousINotificationListener, int paramAnonymousInt)
    {
      NotificationManagerService.-get14(NotificationManagerService.this).unregisterService(paramAnonymousINotificationListener, paramAnonymousInt);
    }
    
    public boolean updateAutomaticZenRule(String paramAnonymousString, AutomaticZenRule paramAnonymousAutomaticZenRule)
      throws RemoteException
    {
      Preconditions.checkNotNull(paramAnonymousAutomaticZenRule, "automaticZenRule is null");
      Preconditions.checkNotNull(paramAnonymousAutomaticZenRule.getName(), "Name is null");
      Preconditions.checkNotNull(paramAnonymousAutomaticZenRule.getOwner(), "Owner is null");
      Preconditions.checkNotNull(paramAnonymousAutomaticZenRule.getConditionId(), "ConditionId is null");
      enforcePolicyAccess(Binder.getCallingUid(), "updateAutomaticZenRule");
      return NotificationManagerService.-get26(NotificationManagerService.this).updateAutomaticZenRule(paramAnonymousString, paramAnonymousAutomaticZenRule, "updateAutomaticZenRule");
    }
  };
  private SettingsObserver mSettingsObserver;
  private String mSoundNotificationKey;
  StatusBarManagerInternal mStatusBar;
  final ArrayMap<String, NotificationRecord> mSummaryByGroupKey = new ArrayMap();
  private String mSystemNotificationSound;
  boolean mSystemReady;
  final ArrayList<ToastRecord> mToastQueue = new ArrayList();
  private NotificationUsageStats mUsageStats;
  private boolean mUseAttentionLight;
  private final ManagedServices.UserProfiles mUserProfiles = new ManagedServices.UserProfiles();
  private int mVibrateIntensity = 1;
  private String mVibrateNotificationKey;
  private int mVibrateWhenMute = 1;
  Vibrator mVibrator;
  private VrManagerInternal mVrManagerInternal;
  private WindowManagerInternal mWindowManagerInternal;
  private ZenModeHelper mZenModeHelper;
  
  static
  {
    DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
    DEFAULT_VIBRATE_PATTERN = new long[] { 0L, 250L, 250L, 250L };
    MY_UID = Process.myUid();
  }
  
  public NotificationManagerService(Context paramContext)
  {
    super(paramContext);
  }
  
  private void addDisabledHint(ManagedServices.ManagedServiceInfo paramManagedServiceInfo, int paramInt)
  {
    if (this.mListenersDisablingEffects.indexOfKey(paramInt) < 0) {
      this.mListenersDisablingEffects.put(paramInt, new ArraySet());
    }
    ((ArraySet)this.mListenersDisablingEffects.get(paramInt)).add(paramManagedServiceInfo);
  }
  
  private void addDisabledHints(ManagedServices.ManagedServiceInfo paramManagedServiceInfo, int paramInt)
  {
    if ((paramInt & 0x1) != 0) {
      addDisabledHint(paramManagedServiceInfo, 1);
    }
    if ((paramInt & 0x2) != 0) {
      addDisabledHint(paramManagedServiceInfo, 2);
    }
    if ((paramInt & 0x4) != 0) {
      addDisabledHint(paramManagedServiceInfo, 4);
    }
  }
  
  private void applyAdjustmentLocked(Adjustment paramAdjustment)
  {
    maybeClearAutobundleSummaryLocked(paramAdjustment);
    NotificationRecord localNotificationRecord = (NotificationRecord)this.mNotificationsByKey.get(paramAdjustment.getKey());
    if (localNotificationRecord == null) {
      return;
    }
    if (paramAdjustment.getImportance() != 0) {
      localNotificationRecord.setImportance(paramAdjustment.getImportance(), paramAdjustment.getExplanation());
    }
    String str;
    if (paramAdjustment.getSignals() != null)
    {
      Bundle.setDefusable(paramAdjustment.getSignals(), true);
      str = paramAdjustment.getSignals().getString("group_key_override", null);
      if (str != null) {
        break label92;
      }
      EventLogTags.writeNotificationUnautogrouped(paramAdjustment.getKey());
    }
    for (;;)
    {
      localNotificationRecord.sbn.setOverrideGroupKey(str);
      return;
      label92:
      EventLogTags.writeNotificationAutogrouped(paramAdjustment.getKey());
    }
  }
  
  private void applyZenModeLocked(NotificationRecord paramNotificationRecord)
  {
    int j = 0;
    paramNotificationRecord.setIntercepted(this.mZenModeHelper.shouldIntercept(paramNotificationRecord));
    if (paramNotificationRecord.isIntercepted()) {
      if (!this.mZenModeHelper.shouldSuppressWhenScreenOff()) {
        break label54;
      }
    }
    label54:
    for (int i = 1;; i = 0)
    {
      if (this.mZenModeHelper.shouldSuppressWhenScreenOn()) {
        j = 2;
      }
      paramNotificationRecord.setSuppressedVisualEffects(i | j);
      return;
    }
  }
  
  private static AudioAttributes audioAttributesForNotification(Notification paramNotification)
  {
    if ((paramNotification.audioAttributes == null) || (Notification.AUDIO_ATTRIBUTES_DEFAULT.equals(paramNotification.audioAttributes)))
    {
      if ((paramNotification.audioStreamType >= 0) && (paramNotification.audioStreamType < AudioSystem.getNumStreamTypes())) {
        return new AudioAttributes.Builder().setInternalLegacyStreamType(paramNotification.audioStreamType).build();
      }
    }
    else {
      return paramNotification.audioAttributes;
    }
    if (paramNotification.audioStreamType == -1) {
      return Notification.AUDIO_ATTRIBUTES_DEFAULT;
    }
    Log.w("NotificationService", String.format("Invalid stream type: %d", new Object[] { Integer.valueOf(paramNotification.audioStreamType) }));
    return Notification.AUDIO_ATTRIBUTES_DEFAULT;
  }
  
  private int calculateHints()
  {
    int j = 0;
    int i = this.mListenersDisablingEffects.size() - 1;
    while (i >= 0)
    {
      int m = this.mListenersDisablingEffects.keyAt(i);
      int k = j;
      if (!((ArraySet)this.mListenersDisablingEffects.valueAt(i)).isEmpty()) {
        k = j | m;
      }
      i -= 1;
      j = k;
    }
    return j;
  }
  
  private long calculateSuppressedEffects()
  {
    int i = calculateHints();
    long l2 = 0L;
    if ((i & 0x1) != 0) {
      l2 = 3L;
    }
    long l1 = l2;
    if ((i & 0x2) != 0) {
      l1 = l2 | 1L;
    }
    l2 = l1;
    if ((i & 0x4) != 0) {
      l2 = l1 | 0x2;
    }
    return l2;
  }
  
  private static String callStateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "CALL_STATE_UNKNOWN_" + paramInt;
    case 0: 
      return "CALL_STATE_IDLE";
    case 1: 
      return "CALL_STATE_RINGING";
    }
    return "CALL_STATE_OFFHOOK";
  }
  
  private void cancelGroupChildrenLocked(NotificationRecord paramNotificationRecord, int paramInt1, int paramInt2, String paramString, int paramInt3, boolean paramBoolean)
  {
    if (!paramNotificationRecord.getNotification().isGroupSummary()) {
      return;
    }
    String str = paramNotificationRecord.sbn.getPackageName();
    int j = paramNotificationRecord.getUserId();
    if (str == null)
    {
      if (DBG) {
        Log.e("NotificationService", "No package for group summary: " + paramNotificationRecord.getKey());
      }
      return;
    }
    int i = this.mNotificationList.size() - 1;
    if (i >= 0)
    {
      NotificationRecord localNotificationRecord = (NotificationRecord)this.mNotificationList.get(i);
      StatusBarNotification localStatusBarNotification = localNotificationRecord.sbn;
      if ((!localStatusBarNotification.isGroup()) || (localStatusBarNotification.getNotification().isGroupSummary())) {}
      for (;;)
      {
        i -= 1;
        break;
        if (localNotificationRecord.getGroupKey().equals(paramNotificationRecord.getGroupKey()))
        {
          EventLogTags.writeNotificationCancel(paramInt1, paramInt2, str, localStatusBarNotification.getId(), localStatusBarNotification.getTag(), j, 0, 0, paramInt3, paramString);
          this.mNotificationList.remove(i);
          cancelNotificationLocked(localNotificationRecord, paramBoolean, paramInt3);
        }
      }
    }
  }
  
  private void cancelNotificationLocked(NotificationRecord paramNotificationRecord, boolean paramBoolean, int paramInt)
  {
    recordCallerLocked(paramNotificationRecord);
    if ((paramBoolean) && (paramNotificationRecord.getNotification().deleteIntent != null)) {}
    for (;;)
    {
      try
      {
        paramNotificationRecord.getNotification().deleteIntent.send();
        if (paramNotificationRecord.getNotification().getSmallIcon() != null)
        {
          paramNotificationRecord.isCanceled = true;
          this.mListeners.notifyRemovedLocked(paramNotificationRecord.sbn);
        }
        str = paramNotificationRecord.getKey();
        if (str.equals(this.mSoundNotificationKey))
        {
          this.mSoundNotificationKey = null;
          l = Binder.clearCallingIdentity();
        }
        try
        {
          localObject = this.mAudioManager.getRingtonePlayer();
          if (localObject != null) {
            ((IRingtonePlayer)localObject).stopAsync();
          }
        }
        catch (RemoteException localRemoteException)
        {
          Object localObject;
          NotificationRecord localNotificationRecord;
          Binder.restoreCallingIdentity(l);
          continue;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        if (str.equals(this.mVibrateNotificationKey))
        {
          this.mVibrateNotificationKey = null;
          l = Binder.clearCallingIdentity();
        }
      }
      catch (PendingIntent.CanceledException localCanceledException)
      {
        try
        {
          String str;
          this.mVibrator.cancel();
          Binder.restoreCallingIdentity(l);
          this.mLights.remove(str);
          switch (paramInt)
          {
          case 4: 
          case 5: 
          case 6: 
          case 7: 
          default: 
            this.mNotificationsByKey.remove(paramNotificationRecord.sbn.getKey());
            localObject = paramNotificationRecord.getGroupKey();
            localNotificationRecord = (NotificationRecord)this.mSummaryByGroupKey.get(localObject);
            if ((localNotificationRecord != null) && (localNotificationRecord.getKey().equals(paramNotificationRecord.getKey()))) {
              this.mSummaryByGroupKey.remove(localObject);
            }
            localObject = (ArrayMap)this.mAutobundledSummaries.get(Integer.valueOf(paramNotificationRecord.sbn.getUserId()));
            if ((localObject != null) && (paramNotificationRecord.sbn.getKey().equals(((ArrayMap)localObject).get(paramNotificationRecord.sbn.getPackageName())))) {
              ((ArrayMap)localObject).remove(paramNotificationRecord.sbn.getPackageName());
            }
            this.mArchive.record(paramNotificationRecord.sbn);
            l = System.currentTimeMillis();
            EventLogTags.writeNotificationCanceled(str, paramInt, paramNotificationRecord.getLifespanMs(l), paramNotificationRecord.getFreshnessMs(l), paramNotificationRecord.getExposureMs(l));
            return;
          }
        }
        finally
        {
          long l;
          Binder.restoreCallingIdentity(l);
        }
        localCanceledException = localCanceledException;
        Slog.w("NotificationService", "canceled PendingIntent for " + paramNotificationRecord.sbn.getPackageName(), localCanceledException);
        continue;
      }
      this.mUsageStats.registerDismissedByUser(paramNotificationRecord);
      continue;
      this.mUsageStats.registerRemovedByApp(paramNotificationRecord);
    }
  }
  
  private static void checkCallerIsSameApp(String paramString)
  {
    int i = Binder.getCallingUid();
    try
    {
      ApplicationInfo localApplicationInfo = AppGlobals.getPackageManager().getApplicationInfo(paramString, 0, UserHandle.getCallingUserId());
      if (localApplicationInfo == null) {
        throw new SecurityException("Unknown package " + paramString);
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw new SecurityException("Unknown package " + paramString + "\n" + localRemoteException);
    }
    if (!UserHandle.isSameApp(localRemoteException.uid, i)) {
      throw new SecurityException("Calling uid " + i + " gave package" + paramString + " which is owned by uid " + localRemoteException.uid);
    }
  }
  
  private static void checkCallerIsSystem()
  {
    if (isCallerSystem()) {
      return;
    }
    throw new SecurityException("Disallowed call for uid " + Binder.getCallingUid());
  }
  
  private static void checkCallerIsSystemOrSameApp(String paramString)
  {
    if (isCallerSystem()) {
      return;
    }
    checkCallerIsSameApp(paramString);
  }
  
  private boolean checkNotificationOp(String paramString, int paramInt)
  {
    return (this.mAppOps.checkOp(11, paramInt, paramString) == 0) && (!isPackageSuspendedForUser(paramString, paramInt));
  }
  
  static int clamp(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 < paramInt2) {
      return paramInt2;
    }
    if (paramInt1 > paramInt3) {
      return paramInt3;
    }
    return paramInt1;
  }
  
  private void clearLightsLocked()
  {
    this.mLights.clear();
    updateLightsLocked();
  }
  
  private void clearSoundLocked()
  {
    this.mSoundNotificationKey = null;
    long l = Binder.clearCallingIdentity();
    try
    {
      IRingtonePlayer localIRingtonePlayer = this.mAudioManager.getRingtonePlayer();
      if (localIRingtonePlayer != null) {
        localIRingtonePlayer.stopAsync();
      }
      return;
    }
    catch (RemoteException localRemoteException) {}finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void clearVibrateLocked()
  {
    this.mVibrateNotificationKey = null;
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mVibrator.cancel();
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private String disableNotificationEffects(NotificationRecord paramNotificationRecord)
  {
    if (this.mDisableNotificationEffects) {
      return "booleanState";
    }
    if ((this.mListenerHints & 0x1) != 0) {
      return "listenerHints";
    }
    if ((this.mCallState == 0) || (this.mZenModeHelper.isCall(paramNotificationRecord))) {
      return null;
    }
    return "callState";
  }
  
  private void dumpJson(PrintWriter paramPrintWriter, DumpFilter paramDumpFilter)
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("service", "Notification Manager");
      localJSONObject.put("bans", this.mRankingHelper.dumpBansJson(paramDumpFilter));
      localJSONObject.put("ranking", this.mRankingHelper.dumpJson(paramDumpFilter));
      localJSONObject.put("stats", this.mUsageStats.dumpJson(paramDumpFilter));
      paramPrintWriter.println(localJSONObject);
      return;
    }
    catch (JSONException paramDumpFilter)
    {
      for (;;)
      {
        paramDumpFilter.printStackTrace();
      }
    }
  }
  
  private int findNotificationRecordIndexLocked(NotificationRecord paramNotificationRecord)
  {
    return this.mRankingHelper.indexOf(this.mNotificationList, paramNotificationRecord);
  }
  
  static long[] getLongArray(Resources paramResources, int paramInt1, int paramInt2, long[] paramArrayOfLong)
  {
    paramResources = paramResources.getIntArray(paramInt1);
    if (paramResources == null) {
      return paramArrayOfLong;
    }
    if (paramResources.length > paramInt2) {}
    for (paramInt1 = paramInt2;; paramInt1 = paramResources.length)
    {
      paramArrayOfLong = new long[paramInt1];
      paramInt2 = 0;
      while (paramInt2 < paramInt1)
      {
        paramArrayOfLong[paramInt2] = paramResources[paramInt2];
        paramInt2 += 1;
      }
    }
    return paramArrayOfLong;
  }
  
  private ArrayList<ComponentName> getSuppressors()
  {
    ArrayList localArrayList = new ArrayList();
    int i = this.mListenersDisablingEffects.size() - 1;
    while (i >= 0)
    {
      Iterator localIterator = ((ArraySet)this.mListenersDisablingEffects.valueAt(i)).iterator();
      while (localIterator.hasNext()) {
        localArrayList.add(((ManagedServices.ManagedServiceInfo)localIterator.next()).component);
      }
      i -= 1;
    }
    return localArrayList;
  }
  
  private void handleGroupedNotificationLocked(NotificationRecord paramNotificationRecord1, NotificationRecord paramNotificationRecord2, int paramInt1, int paramInt2)
  {
    Object localObject2 = paramNotificationRecord1.sbn;
    Object localObject1 = ((StatusBarNotification)localObject2).getNotification();
    String str;
    boolean bool2;
    label56:
    label69:
    boolean bool1;
    if ((!((Notification)localObject1).isGroupSummary()) || (((StatusBarNotification)localObject2).isAppGroup()))
    {
      str = ((StatusBarNotification)localObject2).getGroupKey();
      bool2 = ((Notification)localObject1).isGroupSummary();
      if (paramNotificationRecord2 == null) {
        break label211;
      }
      localObject2 = paramNotificationRecord2.sbn.getNotification();
      if (paramNotificationRecord2 == null) {
        break label217;
      }
      localObject1 = paramNotificationRecord2.sbn.getGroupKey();
      if (paramNotificationRecord2 == null) {
        break label223;
      }
      bool1 = ((Notification)localObject2).isGroupSummary();
      label80:
      if (bool1)
      {
        localObject2 = (NotificationRecord)this.mSummaryByGroupKey.remove(localObject1);
        if (localObject2 != paramNotificationRecord2) {
          if (localObject2 == null) {
            break label229;
          }
        }
      }
    }
    label211:
    label217:
    label223:
    label229:
    for (localObject2 = ((NotificationRecord)localObject2).getKey();; localObject2 = "<null>")
    {
      Slog.w("NotificationService", "Removed summary didn't match old notification: old=" + paramNotificationRecord2.getKey() + ", removed=" + (String)localObject2);
      if (bool2) {
        this.mSummaryByGroupKey.put(str, paramNotificationRecord1);
      }
      if ((bool1) && ((!bool2) || (!((String)localObject1).equals(str)))) {
        break label237;
      }
      return;
      ((Notification)localObject1).flags &= 0xFDFF;
      break;
      localObject2 = null;
      break label56;
      localObject1 = null;
      break label69;
      bool1 = false;
      break label80;
    }
    label237:
    cancelGroupChildrenLocked(paramNotificationRecord2, paramInt1, paramInt2, null, 12, false);
  }
  
  private void handleListenerHintsChanged(int paramInt)
  {
    synchronized (this.mNotificationList)
    {
      this.mListeners.notifyListenerHintsChangedLocked(paramInt);
      return;
    }
  }
  
  private void handleListenerInterruptionFilterChanged(int paramInt)
  {
    synchronized (this.mNotificationList)
    {
      this.mListeners.notifyInterruptionFilterChanged(paramInt);
      return;
    }
  }
  
  private void handleRankingReconsideration(Message arg1)
  {
    if (!(???.obj instanceof RankingReconsideration)) {
      return;
    }
    RankingReconsideration localRankingReconsideration = (RankingReconsideration)???.obj;
    localRankingReconsideration.run();
    synchronized (this.mNotificationList)
    {
      NotificationRecord localNotificationRecord = (NotificationRecord)this.mNotificationsByKey.get(localRankingReconsideration.getKey());
      if (localNotificationRecord == null) {
        return;
      }
      int i = findNotificationRecordIndexLocked(localNotificationRecord);
      boolean bool1 = localNotificationRecord.isIntercepted();
      int j = localNotificationRecord.getPackageVisibilityOverride();
      localRankingReconsideration.applyChangesLocked(localNotificationRecord);
      applyZenModeLocked(localNotificationRecord);
      this.mRankingHelper.sort(this.mNotificationList);
      int k = findNotificationRecordIndexLocked(localNotificationRecord);
      boolean bool2 = localNotificationRecord.isIntercepted();
      int m = localNotificationRecord.getPackageVisibilityOverride();
      if ((i != k) || (bool1 != bool2)) {
        i = 1;
      }
      while ((!bool1) || (bool2))
      {
        if (i != 0) {
          scheduleSendRankingUpdate();
        }
        return;
        if (j != m) {
          i = 1;
        } else {
          i = 0;
        }
      }
      buzzBeepBlinkLocked(localNotificationRecord);
    }
  }
  
  private void handleRankingSort()
  {
    synchronized (this.mNotificationList)
    {
      int j = this.mNotificationList.size();
      ArrayList localArrayList2 = new ArrayList(j);
      ArrayList localArrayList3 = new ArrayList(j);
      int[] arrayOfInt1 = new int[j];
      int[] arrayOfInt2 = new int[j];
      int i = 0;
      NotificationRecord localNotificationRecord;
      while (i < j)
      {
        localNotificationRecord = (NotificationRecord)this.mNotificationList.get(i);
        localArrayList2.add(localNotificationRecord.getKey());
        localArrayList3.add(localNotificationRecord.sbn.getGroupKey());
        arrayOfInt1[i] = localNotificationRecord.getPackageVisibilityOverride();
        arrayOfInt2[i] = localNotificationRecord.getImportance();
        this.mRankingHelper.extractSignals(localNotificationRecord);
        i += 1;
      }
      this.mRankingHelper.sort(this.mNotificationList);
      i = 0;
      while (i < j)
      {
        localNotificationRecord = (NotificationRecord)this.mNotificationList.get(i);
        if ((!((String)localArrayList2.get(i)).equals(localNotificationRecord.getKey())) || (arrayOfInt1[i] != localNotificationRecord.getPackageVisibilityOverride())) {}
        boolean bool;
        do
        {
          do
          {
            scheduleSendRankingUpdate();
            return;
          } while (arrayOfInt2[i] != localNotificationRecord.getImportance());
          bool = ((String)localArrayList3.get(i)).equals(localNotificationRecord.sbn.getGroupKey());
        } while (!bool);
        i += 1;
      }
      return;
    }
  }
  
  /* Error */
  private void handleSaveNotificationLedPolicyFile()
  {
    // Byte code:
    //   0: ldc -116
    //   2: ldc_w 1282
    //   5: invokestatic 1285	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   8: pop
    //   9: aload_0
    //   10: getfield 1287	com/android/server/notification/NotificationManagerService:mNotificationLedPolicyFile	Landroid/util/AtomicFile;
    //   13: astore_1
    //   14: aload_1
    //   15: monitorenter
    //   16: aload_0
    //   17: getfield 1287	com/android/server/notification/NotificationManagerService:mNotificationLedPolicyFile	Landroid/util/AtomicFile;
    //   20: invokevirtual 1293	android/util/AtomicFile:startWrite	()Ljava/io/FileOutputStream;
    //   23: astore_2
    //   24: new 1295	com/android/internal/util/FastXmlSerializer
    //   27: dup
    //   28: invokespecial 1296	com/android/internal/util/FastXmlSerializer:<init>	()V
    //   31: astore_3
    //   32: aload_3
    //   33: aload_2
    //   34: ldc_w 1298
    //   37: invokeinterface 1304 3 0
    //   42: aload_3
    //   43: aconst_null
    //   44: iconst_1
    //   45: invokestatic 1309	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   48: invokeinterface 1313 3 0
    //   53: aload_3
    //   54: aconst_null
    //   55: ldc -110
    //   57: invokeinterface 1317 3 0
    //   62: pop
    //   63: aload_3
    //   64: aconst_null
    //   65: ldc 81
    //   67: iconst_1
    //   68: invokestatic 1319	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   71: invokeinterface 1323 4 0
    //   76: pop
    //   77: aload_3
    //   78: aconst_null
    //   79: ldc -113
    //   81: invokeinterface 1317 3 0
    //   86: pop
    //   87: aload_0
    //   88: getfield 709	com/android/server/notification/NotificationManagerService:mNotificationLedBlockedPackages	Ljava/util/HashSet;
    //   91: invokeinterface 1202 1 0
    //   96: astore 4
    //   98: aload 4
    //   100: invokeinterface 1207 1 0
    //   105: ifeq +90 -> 195
    //   108: aload 4
    //   110: invokeinterface 1211 1 0
    //   115: checkcast 628	java/lang/String
    //   118: astore 5
    //   120: aload_3
    //   121: aconst_null
    //   122: ldc -107
    //   124: invokeinterface 1317 3 0
    //   129: pop
    //   130: aload_3
    //   131: aconst_null
    //   132: ldc 78
    //   134: aload 5
    //   136: invokeinterface 1323 4 0
    //   141: pop
    //   142: aload_3
    //   143: aconst_null
    //   144: ldc -107
    //   146: invokeinterface 1326 3 0
    //   151: pop
    //   152: goto -54 -> 98
    //   155: astore_3
    //   156: ldc -116
    //   158: ldc_w 1328
    //   161: aload_3
    //   162: invokestatic 1063	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   165: pop
    //   166: aload_2
    //   167: ifnull +11 -> 178
    //   170: aload_0
    //   171: getfield 1287	com/android/server/notification/NotificationManagerService:mNotificationLedPolicyFile	Landroid/util/AtomicFile;
    //   174: aload_2
    //   175: invokevirtual 1332	android/util/AtomicFile:failWrite	(Ljava/io/FileOutputStream;)V
    //   178: aload_1
    //   179: monitorexit
    //   180: return
    //   181: astore_2
    //   182: ldc -116
    //   184: ldc_w 1334
    //   187: aload_2
    //   188: invokestatic 1063	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   191: pop
    //   192: aload_1
    //   193: monitorexit
    //   194: return
    //   195: aload_3
    //   196: aconst_null
    //   197: ldc -113
    //   199: invokeinterface 1326 3 0
    //   204: pop
    //   205: aload_3
    //   206: aconst_null
    //   207: ldc -110
    //   209: invokeinterface 1326 3 0
    //   214: pop
    //   215: aload_3
    //   216: invokeinterface 1337 1 0
    //   221: aload_0
    //   222: getfield 1287	com/android/server/notification/NotificationManagerService:mNotificationLedPolicyFile	Landroid/util/AtomicFile;
    //   225: aload_2
    //   226: invokevirtual 1340	android/util/AtomicFile:finishWrite	(Ljava/io/FileOutputStream;)V
    //   229: goto -51 -> 178
    //   232: astore_2
    //   233: aload_1
    //   234: monitorexit
    //   235: aload_2
    //   236: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	237	0	this	NotificationManagerService
    //   13	221	1	localAtomicFile	AtomicFile
    //   23	152	2	localFileOutputStream	java.io.FileOutputStream
    //   181	45	2	localIOException1	IOException
    //   232	4	2	localObject	Object
    //   31	112	3	localFastXmlSerializer	FastXmlSerializer
    //   155	61	3	localIOException2	IOException
    //   96	13	4	localIterator	Iterator
    //   118	17	5	str	String
    // Exception table:
    //   from	to	target	type
    //   24	98	155	java/io/IOException
    //   98	152	155	java/io/IOException
    //   195	229	155	java/io/IOException
    //   16	24	181	java/io/IOException
    //   16	24	232	finally
    //   24	98	232	finally
    //   98	152	232	finally
    //   156	166	232	finally
    //   170	178	232	finally
    //   182	192	232	finally
    //   195	229	232	finally
  }
  
  /* Error */
  private void handleSavePolicyFile()
  {
    // Byte code:
    //   0: getstatic 601	com/android/server/notification/NotificationManagerService:DBG	Z
    //   3: ifeq +12 -> 15
    //   6: ldc -116
    //   8: ldc_w 1341
    //   11: invokestatic 1285	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   14: pop
    //   15: aload_0
    //   16: getfield 1343	com/android/server/notification/NotificationManagerService:mPolicyFile	Landroid/util/AtomicFile;
    //   19: astore_1
    //   20: aload_1
    //   21: monitorenter
    //   22: aload_0
    //   23: getfield 1343	com/android/server/notification/NotificationManagerService:mPolicyFile	Landroid/util/AtomicFile;
    //   26: invokevirtual 1293	android/util/AtomicFile:startWrite	()Ljava/io/FileOutputStream;
    //   29: astore_2
    //   30: aload_0
    //   31: aload_2
    //   32: iconst_0
    //   33: invokespecial 561	com/android/server/notification/NotificationManagerService:writePolicyXml	(Ljava/io/OutputStream;Z)V
    //   36: aload_0
    //   37: getfield 1343	com/android/server/notification/NotificationManagerService:mPolicyFile	Landroid/util/AtomicFile;
    //   40: aload_2
    //   41: invokevirtual 1340	android/util/AtomicFile:finishWrite	(Ljava/io/FileOutputStream;)V
    //   44: aload_1
    //   45: monitorexit
    //   46: aload_0
    //   47: invokevirtual 1347	com/android/server/notification/NotificationManagerService:getContext	()Landroid/content/Context;
    //   50: invokevirtual 1350	android/content/Context:getPackageName	()Ljava/lang/String;
    //   53: invokestatic 1355	android/app/backup/BackupManager:dataChanged	(Ljava/lang/String;)V
    //   56: return
    //   57: astore_2
    //   58: ldc -116
    //   60: ldc_w 1334
    //   63: aload_2
    //   64: invokestatic 1063	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   67: pop
    //   68: aload_1
    //   69: monitorexit
    //   70: return
    //   71: astore_3
    //   72: ldc -116
    //   74: ldc_w 1328
    //   77: aload_3
    //   78: invokestatic 1063	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   81: pop
    //   82: aload_0
    //   83: getfield 1343	com/android/server/notification/NotificationManagerService:mPolicyFile	Landroid/util/AtomicFile;
    //   86: aload_2
    //   87: invokevirtual 1332	android/util/AtomicFile:failWrite	(Ljava/io/FileOutputStream;)V
    //   90: goto -46 -> 44
    //   93: astore_2
    //   94: aload_1
    //   95: monitorexit
    //   96: aload_2
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	NotificationManagerService
    //   19	76	1	localAtomicFile	AtomicFile
    //   29	12	2	localFileOutputStream	java.io.FileOutputStream
    //   57	30	2	localIOException1	IOException
    //   93	4	2	localObject	Object
    //   71	7	3	localIOException2	IOException
    // Exception table:
    //   from	to	target	type
    //   22	30	57	java/io/IOException
    //   30	44	71	java/io/IOException
    //   22	30	93	finally
    //   30	44	93	finally
    //   58	68	93	finally
    //   72	90	93	finally
  }
  
  private void handleSendRankingUpdate()
  {
    synchronized (this.mNotificationList)
    {
      this.mListeners.notifyRankingUpdateLocked();
      return;
    }
  }
  
  private void handleTimeout(ToastRecord paramToastRecord)
  {
    if (DBG) {
      Slog.d("NotificationService", "Timeout pkg=" + paramToastRecord.pkg + " callback=" + paramToastRecord.callback);
    }
    synchronized (this.mToastQueue)
    {
      int i = indexOfToastLocked(paramToastRecord.pkg, paramToastRecord.callback);
      if (i >= 0) {
        cancelToastLocked(i);
      }
      return;
    }
  }
  
  private static boolean isCallerSystem()
  {
    return isUidSystem(Binder.getCallingUid());
  }
  
  private boolean isPackageSuspendedForUser(String paramString, int paramInt)
  {
    paramInt = UserHandle.getUserId(paramInt);
    try
    {
      boolean bool = AppGlobals.getPackageManager().isPackageSuspendedForUser(paramString, paramInt);
      return bool;
    }
    catch (IllegalArgumentException paramString)
    {
      return false;
    }
    catch (RemoteException paramString)
    {
      throw new SecurityException("Could not talk to package manager service");
    }
  }
  
  private static boolean isUidSystem(int paramInt)
  {
    int i = UserHandle.getAppId(paramInt);
    if ((i == 1000) || (i == 1001)) {}
    while (paramInt == 0) {
      return true;
    }
    return false;
  }
  
  private boolean isVisibleToListener(StatusBarNotification paramStatusBarNotification, ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
  {
    return paramManagedServiceInfo.enabledAndUserMatches(paramStatusBarNotification.getUserId());
  }
  
  private void listenForCallState()
  {
    TelephonyManager.from(getContext()).listen(new PhoneStateListener()
    {
      public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString)
      {
        if (NotificationManagerService.-get6(NotificationManagerService.this) == paramAnonymousInt) {
          return;
        }
        if (NotificationManagerService.DBG) {
          Slog.d("NotificationService", "Call state changed: " + NotificationManagerService.-wrap8(paramAnonymousInt));
        }
        NotificationManagerService.-set0(NotificationManagerService.this, paramAnonymousInt);
      }
    }, 32);
  }
  
  private void loadNotificationLedPolicyFile()
  {
    synchronized (this.mNotificationLedPolicyFile)
    {
      this.mNotificationLedBlockedPackages.clear();
      localObject6 = null;
      localObject7 = null;
      localObject8 = null;
      Object localObject1 = null;
      localObject9 = null;
      do
      {
        do
        {
          for (;;)
          {
            try
            {
              localFileInputStream = this.mNotificationLedPolicyFile.openRead();
              localObject9 = localFileInputStream;
              localObject6 = localFileInputStream;
              localObject7 = localFileInputStream;
              localObject8 = localFileInputStream;
              localObject1 = localFileInputStream;
              localXmlPullParser = Xml.newPullParser();
              localObject9 = localFileInputStream;
              localObject6 = localFileInputStream;
              localObject7 = localFileInputStream;
              localObject8 = localFileInputStream;
              localObject1 = localFileInputStream;
              localXmlPullParser.setInput(localFileInputStream, null);
            }
            catch (FileNotFoundException localFileNotFoundException)
            {
              FileInputStream localFileInputStream;
              XmlPullParser localXmlPullParser;
              int i;
              String str;
              IoUtils.closeQuietly((AutoCloseable)localObject9);
              return;
              localObject9 = localFileInputStream;
              localObject6 = localFileInputStream;
              localObject7 = localFileInputStream;
              localObject8 = localFileInputStream;
              Object localObject2 = localFileInputStream;
              boolean bool = "blocked-packages".equals(str);
              if ((!bool) || (i != 3)) {
                continue;
              }
              continue;
              IoUtils.closeQuietly(localFileInputStream);
              continue;
              localObject3 = finally;
              throw ((Throwable)localObject3);
            }
            catch (XmlPullParserException localXmlPullParserException)
            {
              localObject4 = localObject6;
              Log.wtf("NotificationService", "Unable to parse notification led policy", localXmlPullParserException);
              IoUtils.closeQuietly((AutoCloseable)localObject6);
              continue;
            }
            catch (NumberFormatException localNumberFormatException)
            {
              localObject4 = localObject7;
              Log.wtf("NotificationService", "Unable to parse notification led policy", localNumberFormatException);
              IoUtils.closeQuietly((AutoCloseable)localObject7);
              continue;
            }
            catch (IOException localIOException)
            {
              localObject4 = localObject8;
              Log.wtf("NotificationService", "Unable to read notification led policy", localIOException);
              IoUtils.closeQuietly((AutoCloseable)localObject8);
              continue;
            }
            finally
            {
              Object localObject4;
              IoUtils.closeQuietly((AutoCloseable)localObject4);
            }
            localObject9 = localFileInputStream;
            localObject6 = localFileInputStream;
            localObject7 = localFileInputStream;
            localObject8 = localFileInputStream;
            localObject1 = localFileInputStream;
            i = localXmlPullParser.next();
            if (i == 1) {
              continue;
            }
            localObject9 = localFileInputStream;
            localObject6 = localFileInputStream;
            localObject7 = localFileInputStream;
            localObject8 = localFileInputStream;
            localObject1 = localFileInputStream;
            str = localXmlPullParser.getName();
            if (i == 2)
            {
              localObject9 = localFileInputStream;
              localObject6 = localFileInputStream;
              localObject7 = localFileInputStream;
              localObject8 = localFileInputStream;
              localObject1 = localFileInputStream;
              if (!"notification-policy".equals(str)) {
                continue;
              }
              localObject9 = localFileInputStream;
              localObject6 = localFileInputStream;
              localObject7 = localFileInputStream;
              localObject8 = localFileInputStream;
              localObject1 = localFileInputStream;
              Integer.parseInt(localXmlPullParser.getAttributeValue(null, "version"));
            }
          }
          localObject9 = localFileInputStream;
          localObject6 = localFileInputStream;
          localObject7 = localFileInputStream;
          localObject8 = localFileInputStream;
          localObject1 = localFileInputStream;
        } while (!"blocked-packages".equals(str));
        localObject9 = localFileInputStream;
        localObject6 = localFileInputStream;
        localObject7 = localFileInputStream;
        localObject8 = localFileInputStream;
        localObject1 = localFileInputStream;
        i = localXmlPullParser.next();
      } while (i == 1);
      localObject9 = localFileInputStream;
      localObject6 = localFileInputStream;
      localObject7 = localFileInputStream;
      localObject8 = localFileInputStream;
      localObject1 = localFileInputStream;
      str = localXmlPullParser.getName();
      localObject9 = localFileInputStream;
      localObject6 = localFileInputStream;
      localObject7 = localFileInputStream;
      localObject8 = localFileInputStream;
      localObject1 = localFileInputStream;
      if ("package".equals(str))
      {
        localObject9 = localFileInputStream;
        localObject6 = localFileInputStream;
        localObject7 = localFileInputStream;
        localObject8 = localFileInputStream;
        localObject1 = localFileInputStream;
        this.mNotificationLedBlockedPackages.add(localXmlPullParser.getAttributeValue(null, "name"));
      }
    }
  }
  
  private void loadPolicyFile()
  {
    if (DBG) {
      Slog.d("NotificationService", "loadPolicyFile");
    }
    AtomicFile localAtomicFile = this.mPolicyFile;
    Object localObject8 = null;
    Object localObject9 = null;
    Object localObject7 = null;
    Object localObject1 = null;
    Object localObject6 = null;
    for (;;)
    {
      try
      {
        FileInputStream localFileInputStream = this.mPolicyFile.openRead();
        localObject6 = localFileInputStream;
        localObject8 = localFileInputStream;
        localObject9 = localFileInputStream;
        localObject7 = localFileInputStream;
        localObject1 = localFileInputStream;
        readPolicyXml(localFileInputStream, false);
        Object localObject3;
        List localList;
        int j;
        int i;
        i += 1;
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        localXmlPullParserException = localXmlPullParserException;
        localObject1 = localObject6;
        Log.wtf("NotificationService", "Unable to parse notification policy", localXmlPullParserException);
        IoUtils.closeQuietly((AutoCloseable)localObject6);
        continue;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        localObject3 = localObject8;
        Log.wtf("NotificationService", "Unable to parse notification policy", localNumberFormatException);
        IoUtils.closeQuietly((AutoCloseable)localObject8);
        continue;
      }
      catch (IOException localIOException)
      {
        localObject3 = localObject9;
        Log.wtf("NotificationService", "Unable to read notification policy", localIOException);
        IoUtils.closeQuietly((AutoCloseable)localObject9);
        continue;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        localObject4 = localObject7;
        if (!OpFeatures.isSupport(new int[] { 0 })) {
          break label322;
        }
        localObject4 = localObject7;
        localList = this.mRankingHelper.getDefaultDenoiseList();
        localObject4 = localObject7;
        j = localList.size();
        i = 0;
        if (i >= j) {
          break label315;
        }
        localObject4 = localObject7;
        localObject6 = UserManager.get(getContext()).getUsers().iterator();
        localObject4 = localObject7;
        if (((Iterator)localObject6).hasNext())
        {
          localObject4 = localObject7;
          localObject8 = (UserInfo)((Iterator)localObject6).next();
          localObject4 = localObject7;
          ((UserInfo)localObject8).getUserHandle().getIdentifier();
          localObject4 = localObject7;
          this.mRankingHelper.setDefaultOPLevel(((UserInfo)localObject8).getUserHandle().getIdentifier(), (String)localList.get(i));
          continue;
        }
      }
      finally
      {
        IoUtils.closeQuietly((AutoCloseable)localObject4);
      }
      continue;
      label315:
      Object localObject4 = localObject7;
      savePolicyFile();
      label322:
      IoUtils.closeQuietly((AutoCloseable)localObject7);
    }
  }
  
  private NotificationRankingUpdate makeRankingUpdateLocked(ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
  {
    int j = this.mNotificationList.size();
    Object localObject2 = new ArrayList(j);
    Object localObject1 = new ArrayList(j);
    ArrayList localArrayList = new ArrayList(j);
    Bundle localBundle1 = new Bundle();
    Bundle localBundle2 = new Bundle();
    Bundle localBundle3 = new Bundle();
    Bundle localBundle4 = new Bundle();
    int i = 0;
    if (i < j)
    {
      NotificationRecord localNotificationRecord = (NotificationRecord)this.mNotificationList.get(i);
      if (!isVisibleToListener(localNotificationRecord.sbn, paramManagedServiceInfo)) {}
      for (;;)
      {
        i += 1;
        break;
        String str = localNotificationRecord.sbn.getKey();
        ((ArrayList)localObject2).add(str);
        localArrayList.add(Integer.valueOf(localNotificationRecord.getImportance()));
        if (localNotificationRecord.getImportanceExplanation() != null) {
          localBundle4.putCharSequence(str, localNotificationRecord.getImportanceExplanation());
        }
        if (localNotificationRecord.isIntercepted()) {
          ((ArrayList)localObject1).add(str);
        }
        localBundle3.putInt(str, localNotificationRecord.getSuppressedVisualEffects());
        if (localNotificationRecord.getPackageVisibilityOverride() != 64536) {
          localBundle2.putInt(str, localNotificationRecord.getPackageVisibilityOverride());
        }
        localBundle1.putString(str, localNotificationRecord.sbn.getOverrideGroupKey());
      }
    }
    j = ((ArrayList)localObject2).size();
    paramManagedServiceInfo = (String[])((ArrayList)localObject2).toArray(new String[j]);
    localObject1 = (String[])((ArrayList)localObject1).toArray(new String[((ArrayList)localObject1).size()]);
    localObject2 = new int[j];
    i = 0;
    while (i < j)
    {
      localObject2[i] = ((Integer)localArrayList.get(i)).intValue();
      i += 1;
    }
    return new NotificationRankingUpdate(paramManagedServiceInfo, (String[])localObject1, localBundle2, localBundle3, (int[])localObject2, localBundle4, localBundle1);
  }
  
  private void maybeAddAutobundleSummary(Adjustment paramAdjustment)
  {
    String str;
    Object localObject3;
    if (paramAdjustment.getSignals() != null)
    {
      Bundle.setDefusable(paramAdjustment.getSignals(), true);
      if (paramAdjustment.getSignals().getBoolean("autogroup_needed", false))
      {
        str = paramAdjustment.getSignals().getString("group_key_override", null);
        localObject3 = null;
      }
    }
    Object localObject1;
    int i;
    Object localObject2;
    synchronized (this.mNotificationList)
    {
      localObject1 = (NotificationRecord)this.mNotificationsByKey.get(paramAdjustment.getKey());
      if (localObject1 == null) {
        return;
      }
      StatusBarNotification localStatusBarNotification = ((NotificationRecord)localObject1).sbn;
      i = localStatusBarNotification.getUser().getIdentifier();
      localObject2 = (ArrayMap)this.mAutobundledSummaries.get(Integer.valueOf(i));
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new ArrayMap();
      }
      this.mAutobundledSummaries.put(Integer.valueOf(i), localObject1);
      localObject2 = localObject3;
      if (!((ArrayMap)localObject1).containsKey(paramAdjustment.getPackage()))
      {
        localObject2 = localObject3;
        if (str != null)
        {
          localObject3 = (ApplicationInfo)localStatusBarNotification.getNotification().extras.getParcelable("android.appInfo");
          localObject2 = new Bundle();
          ((Bundle)localObject2).putParcelable("android.appInfo", (Parcelable)localObject3);
          localObject3 = new Notification.Builder(getContext()).setSmallIcon(localStatusBarNotification.getNotification().getSmallIcon()).setGroupSummary(true).setGroup(str).setFlag(1024, true).setFlag(512, true).setColor(localStatusBarNotification.getNotification().color).setLocalOnly(true).build();
          ((Notification)localObject3).extras.putAll((Bundle)localObject2);
          localObject2 = getContext().getPackageManager().getLaunchIntentForPackage(paramAdjustment.getPackage());
          if (localObject2 != null) {
            ((Notification)localObject3).contentIntent = PendingIntent.getActivityAsUser(getContext(), 0, (Intent)localObject2, 0, null, UserHandle.of(i));
          }
          localObject3 = new StatusBarNotification(localStatusBarNotification.getPackageName(), localStatusBarNotification.getOpPkg(), Integer.MAX_VALUE, "group_key_override", localStatusBarNotification.getUid(), localStatusBarNotification.getInitialPid(), (Notification)localObject3, localStatusBarNotification.getUser(), str, System.currentTimeMillis());
          localObject2 = new NotificationRecord(getContext(), (StatusBarNotification)localObject3);
        }
      }
    }
    throw paramAdjustment;
  }
  
  private void maybeClearAutobundleSummaryLocked(Adjustment paramAdjustment)
  {
    if (paramAdjustment.getSignals() != null)
    {
      Bundle.setDefusable(paramAdjustment.getSignals(), true);
      if ((paramAdjustment.getSignals().containsKey("autogroup_needed")) && (!paramAdjustment.getSignals().getBoolean("autogroup_needed", false))) {
        break label44;
      }
    }
    label44:
    do
    {
      ArrayMap localArrayMap;
      do
      {
        return;
        localArrayMap = (ArrayMap)this.mAutobundledSummaries.get(Integer.valueOf(paramAdjustment.getUser()));
      } while ((localArrayMap == null) || (!localArrayMap.containsKey(paramAdjustment.getPackage())));
      paramAdjustment = (NotificationRecord)this.mNotificationsByKey.get(localArrayMap.remove(paramAdjustment.getPackage()));
    } while (paramAdjustment == null);
    this.mNotificationList.remove(paramAdjustment);
    cancelNotificationLocked(paramAdjustment, false, 16);
  }
  
  private boolean noteNotificationOp(String paramString, int paramInt)
  {
    if (this.mAppOps.noteOpNoThrow(11, paramInt, paramString) != 0)
    {
      Slog.v("NotificationService", "notifications are disabled by AppOps for " + paramString);
      return false;
    }
    return true;
  }
  
  private boolean notificationMatchesCurrentProfiles(NotificationRecord paramNotificationRecord, int paramInt)
  {
    if (!notificationMatchesUserId(paramNotificationRecord, paramInt)) {
      return this.mUserProfiles.isCurrentProfile(paramNotificationRecord.getUserId());
    }
    return true;
  }
  
  private boolean notificationMatchesUserId(NotificationRecord paramNotificationRecord, int paramInt)
  {
    if ((paramInt == -1) || (paramNotificationRecord.getUserId() == -1)) {}
    while (paramNotificationRecord.getUserId() == paramInt) {
      return true;
    }
    return false;
  }
  
  private void readPolicyXml(InputStream paramInputStream, boolean paramBoolean)
    throws XmlPullParserException, NumberFormatException, IOException
  {
    XmlPullParser localXmlPullParser = Xml.newPullParser();
    localXmlPullParser.setInput(paramInputStream, StandardCharsets.UTF_8.name());
    while (localXmlPullParser.next() != 1)
    {
      this.mZenModeHelper.readXml(localXmlPullParser, paramBoolean);
      this.mRankingHelper.readXml(localXmlPullParser, paramBoolean, NON_BLOCKABLE_PKGS);
    }
  }
  
  private void recordCallerLocked(NotificationRecord paramNotificationRecord)
  {
    if (this.mZenModeHelper.isCall(paramNotificationRecord)) {
      this.mZenModeHelper.recordCaller(paramNotificationRecord);
    }
  }
  
  private boolean removeDisabledHints(ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
  {
    return removeDisabledHints(paramManagedServiceInfo, 0);
  }
  
  private boolean removeDisabledHints(ManagedServices.ManagedServiceInfo paramManagedServiceInfo, int paramInt)
  {
    boolean bool2 = false;
    int i = this.mListenersDisablingEffects.size() - 1;
    if (i >= 0)
    {
      int j = this.mListenersDisablingEffects.keyAt(i);
      ArraySet localArraySet = (ArraySet)this.mListenersDisablingEffects.valueAt(i);
      if (paramInt != 0)
      {
        bool1 = bool2;
        if ((j & paramInt) != j) {}
      }
      else
      {
        if (bool2) {
          break label81;
        }
      }
      label81:
      for (boolean bool1 = localArraySet.remove(paramManagedServiceInfo);; bool1 = true)
      {
        i -= 1;
        bool2 = bool1;
        break;
      }
    }
    return bool2;
  }
  
  private void scheduleInterruptionFilterChanged(int paramInt)
  {
    this.mHandler.removeMessages(6);
    this.mHandler.obtainMessage(6, paramInt, 0).sendToTarget();
  }
  
  private void scheduleListenerHintsChanged(int paramInt)
  {
    this.mHandler.removeMessages(5);
    this.mHandler.obtainMessage(5, paramInt, 0).sendToTarget();
  }
  
  private void scheduleSendRankingUpdate()
  {
    if (!this.mHandler.hasMessages(4))
    {
      Message localMessage = Message.obtain(this.mHandler, 4);
      this.mHandler.sendMessage(localMessage);
    }
  }
  
  private void scheduleTimeoutLocked(ToastRecord paramToastRecord)
  {
    this.mHandler.removeCallbacksAndMessages(paramToastRecord);
    Message localMessage = Message.obtain(this.mHandler, 2, paramToastRecord);
    if (paramToastRecord.duration == 1) {}
    for (int i = 3500;; i = 2000)
    {
      long l = i;
      this.mHandler.sendMessageDelayed(localMessage, l);
      return;
    }
  }
  
  private void sendRegisteredOnlyBroadcast(String paramString)
  {
    getContext().sendBroadcastAsUser(new Intent(paramString).addFlags(1073741824), UserHandle.ALL, null);
  }
  
  private void syncBlockDb()
  {
    loadPolicyFile();
    Object localObject1 = this.mRankingHelper.getPackageBans();
    Object localObject2 = ((Map)localObject1).entrySet().iterator();
    Object localObject3;
    int i;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Map.Entry)((Iterator)localObject2).next();
      i = ((Integer)((Map.Entry)localObject3).getKey()).intValue();
      setNotificationsEnabledForPackageImpl((String)((Map.Entry)localObject3).getValue(), i, false);
    }
    ((Map)localObject1).clear();
    localObject2 = UserManager.get(getContext()).getUsers().iterator();
    for (;;)
    {
      int j;
      List localList;
      int k;
      if (((Iterator)localObject2).hasNext())
      {
        j = ((UserInfo)((Iterator)localObject2).next()).getUserHandle().getIdentifier();
        localObject3 = getContext().getPackageManager();
        localList = ((PackageManager)localObject3).getInstalledPackagesAsUser(0, j);
        k = localList.size();
        i = 0;
      }
      while (i < k)
      {
        String str = ((PackageInfo)localList.get(i)).packageName;
        try
        {
          int m = ((PackageManager)localObject3).getPackageUidAsUser(str, j);
          if (checkNotificationOp(str, m)) {
            break label358;
          }
          if (NON_BLOCKABLE_PKGS.contains(str))
          {
            this.mAppOps.setMode(11, m, str, 0);
            Slog.v("NotificationService", "Reset importance to default, enable notifications for " + str);
          }
          else
          {
            ((Map)localObject1).put(Integer.valueOf(m), str);
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
        localObject1 = ((Map)localObject1).entrySet().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Map.Entry)((Iterator)localObject1).next();
          this.mRankingHelper.setImportance((String)((Map.Entry)localObject2).getValue(), ((Integer)((Map.Entry)localObject2).getKey()).intValue(), 0);
        }
        savePolicyFile();
        return;
        label358:
        i += 1;
      }
    }
  }
  
  private void updateEffectsSuppressorLocked()
  {
    long l = calculateSuppressedEffects();
    if (l == this.mZenModeHelper.getSuppressedEffects()) {
      return;
    }
    ArrayList localArrayList = getSuppressors();
    ZenLog.traceEffectsSuppressorChanged(this.mEffectsSuppressors, localArrayList, l);
    this.mEffectsSuppressors = localArrayList;
    this.mZenModeHelper.setSuppressedEffects(l);
    sendRegisteredOnlyBroadcast("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
  }
  
  private void updateInterruptionFilterLocked()
  {
    int i = this.mZenModeHelper.getZenModeListenerInterruptionFilter();
    if (i == this.mInterruptionFilter) {
      return;
    }
    this.mInterruptionFilter = i;
    scheduleInterruptionFilterChanged(i);
  }
  
  private void updateListenerHintsLocked()
  {
    int i = calculateHints();
    if (i == this.mListenerHints) {
      return;
    }
    ZenLog.traceListenerHintsChanged(this.mListenerHints, i, this.mEffectsSuppressors.size());
    this.mListenerHints = i;
    scheduleListenerHintsChanged(i);
  }
  
  private void updateNotificationPulse()
  {
    synchronized (this.mNotificationList)
    {
      updateLightsLocked();
      return;
    }
  }
  
  private void writePolicyXml(OutputStream paramOutputStream, boolean paramBoolean)
    throws IOException
  {
    FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
    localFastXmlSerializer.setOutput(paramOutputStream, StandardCharsets.UTF_8.name());
    localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
    localFastXmlSerializer.startTag(null, "notification-policy");
    localFastXmlSerializer.attribute(null, "version", Integer.toString(1));
    this.mZenModeHelper.writeXml(localFastXmlSerializer, paramBoolean);
    this.mRankingHelper.writeXml(localFastXmlSerializer, paramBoolean);
    localFastXmlSerializer.endTag(null, "notification-policy");
    localFastXmlSerializer.endDocument();
  }
  
  /* Error */
  void buzzBeepBlinkLocked(NotificationRecord paramNotificationRecord)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 12
    //   3: iconst_0
    //   4: istore 15
    //   6: iconst_0
    //   7: istore 14
    //   9: iconst_0
    //   10: istore 13
    //   12: iconst_0
    //   13: istore 11
    //   15: aload_1
    //   16: getfield 803	com/android/server/notification/NotificationRecord:sbn	Landroid/service/notification/StatusBarNotification;
    //   19: invokevirtual 953	android/service/notification/StatusBarNotification:getNotification	()Landroid/app/Notification;
    //   22: astore 24
    //   24: aload_1
    //   25: invokevirtual 944	com/android/server/notification/NotificationRecord:getKey	()Ljava/lang/String;
    //   28: astore 25
    //   30: aload_0
    //   31: getfield 340	com/android/server/notification/NotificationManagerService:mZenModeHelper	Lcom/android/server/notification/ZenModeHelper;
    //   34: invokevirtual 1824	com/android/server/notification/ZenModeHelper:getZenMode	()I
    //   37: iconst_3
    //   38: if_icmpne +857 -> 895
    //   41: aload_0
    //   42: getfield 375	com/android/server/notification/NotificationManagerService:mVibrateWhenMute	I
    //   45: iconst_1
    //   46: if_icmpne +849 -> 895
    //   49: iconst_1
    //   50: istore 16
    //   52: aload_1
    //   53: invokevirtual 1276	com/android/server/notification/NotificationRecord:getImportance	()I
    //   56: iconst_3
    //   57: if_icmplt +844 -> 901
    //   60: iconst_1
    //   61: istore 6
    //   63: iload 6
    //   65: ifeq +848 -> 913
    //   68: aload_1
    //   69: invokevirtual 824	com/android/server/notification/NotificationRecord:isIntercepted	()Z
    //   72: ifeq +835 -> 907
    //   75: iload 16
    //   77: istore 17
    //   79: getstatic 601	com/android/server/notification/NotificationManagerService:DBG	Z
    //   82: ifne +10 -> 92
    //   85: aload_1
    //   86: invokevirtual 824	com/android/server/notification/NotificationRecord:isIntercepted	()Z
    //   89: ifeq +59 -> 148
    //   92: ldc -116
    //   94: new 909	java/lang/StringBuilder
    //   97: dup
    //   98: invokespecial 910	java/lang/StringBuilder:<init>	()V
    //   101: ldc_w 1826
    //   104: invokevirtual 916	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   107: aload_1
    //   108: getfield 803	com/android/server/notification/NotificationRecord:sbn	Landroid/service/notification/StatusBarNotification;
    //   111: invokevirtual 938	android/service/notification/StatusBarNotification:getPackageName	()Ljava/lang/String;
    //   114: invokevirtual 916	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   117: ldc_w 1828
    //   120: invokevirtual 916	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   123: iload 17
    //   125: invokevirtual 1831	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   128: ldc_w 1833
    //   131: invokevirtual 916	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   134: aload_1
    //   135: invokevirtual 824	com/android/server/notification/NotificationRecord:isIntercepted	()Z
    //   138: invokevirtual 1831	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   141: invokevirtual 922	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   144: invokestatic 1649	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   147: pop
    //   148: invokestatic 1002	android/os/Binder:clearCallingIdentity	()J
    //   151: lstore 21
    //   153: invokestatic 1838	android/app/ActivityManager:getCurrentUser	()I
    //   156: istore 8
    //   158: lload 21
    //   160: invokestatic 1019	android/os/Binder:restoreCallingIdentity	(J)V
    //   163: aload_0
    //   164: aload_1
    //   165: invokespecial 593	com/android/server/notification/NotificationManagerService:disableNotificationEffects	(Lcom/android/server/notification/NotificationRecord;)Ljava/lang/String;
    //   168: astore 23
    //   170: aload 23
    //   172: ifnull +9 -> 181
    //   175: aload_1
    //   176: aload 23
    //   178: invokestatic 1842	com/android/server/notification/ZenLog:traceDisableEffects	(Lcom/android/server/notification/NotificationRecord;Ljava/lang/String;)V
    //   181: aload 25
    //   183: ifnull +744 -> 927
    //   186: aload 25
    //   188: aload_0
    //   189: getfield 999	com/android/server/notification/NotificationManagerService:mSoundNotificationKey	Ljava/lang/String;
    //   192: invokevirtual 957	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   195: istore 18
    //   197: aload 25
    //   199: ifnull +734 -> 933
    //   202: aload 25
    //   204: aload_0
    //   205: getfield 1021	com/android/server/notification/NotificationManagerService:mVibrateNotificationKey	Ljava/lang/String;
    //   208: invokevirtual 957	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   211: istore 19
    //   213: iconst_0
    //   214: istore_2
    //   215: iconst_0
    //   216: istore 4
    //   218: iconst_0
    //   219: istore_3
    //   220: iconst_0
    //   221: istore 20
    //   223: aload_0
    //   224: getfield 372	com/android/server/notification/NotificationManagerService:mCarrierConfig	Landroid/os/PersistableBundle;
    //   227: ifnull +15 -> 242
    //   230: aload_0
    //   231: getfield 372	com/android/server/notification/NotificationManagerService:mCarrierConfig	Landroid/os/PersistableBundle;
    //   234: ldc_w 1844
    //   237: invokevirtual 1848	android/os/PersistableBundle:getBoolean	(Ljava/lang/String;)Z
    //   240: istore 20
    //   242: aload 23
    //   244: ifnull +45 -> 289
    //   247: iload 15
    //   249: istore 7
    //   251: iload 12
    //   253: istore 5
    //   255: iload 4
    //   257: istore 9
    //   259: iload_2
    //   260: istore 10
    //   262: iload 20
    //   264: ifeq +493 -> 757
    //   267: iload 15
    //   269: istore 7
    //   271: iload 12
    //   273: istore 5
    //   275: iload 4
    //   277: istore 9
    //   279: iload_2
    //   280: istore 10
    //   282: aload_0
    //   283: getfield 383	com/android/server/notification/NotificationManagerService:mInCall	Z
    //   286: ifeq +471 -> 757
    //   289: aload_1
    //   290: invokevirtual 941	com/android/server/notification/NotificationRecord:getUserId	()I
    //   293: iconst_m1
    //   294: if_icmpeq +12 -> 306
    //   297: aload_1
    //   298: invokevirtual 941	com/android/server/notification/NotificationRecord:getUserId	()I
    //   301: iload 8
    //   303: if_icmpne +636 -> 939
    //   306: iload 15
    //   308: istore 7
    //   310: iload 12
    //   312: istore 5
    //   314: iload 4
    //   316: istore 9
    //   318: iload_2
    //   319: istore 10
    //   321: iload 17
    //   323: ifeq +434 -> 757
    //   326: iload 15
    //   328: istore 7
    //   330: iload 12
    //   332: istore 5
    //   334: iload 4
    //   336: istore 9
    //   338: iload_2
    //   339: istore 10
    //   341: aload_0
    //   342: getfield 1850	com/android/server/notification/NotificationManagerService:mSystemReady	Z
    //   345: ifeq +412 -> 757
    //   348: iload 15
    //   350: istore 7
    //   352: iload 12
    //   354: istore 5
    //   356: iload 4
    //   358: istore 9
    //   360: iload_2
    //   361: istore 10
    //   363: aload_0
    //   364: getfield 1004	com/android/server/notification/NotificationManagerService:mAudioManager	Landroid/media/AudioManager;
    //   367: ifnull +390 -> 757
    //   370: getstatic 601	com/android/server/notification/NotificationManagerService:DBG	Z
    //   373: ifeq +12 -> 385
    //   376: ldc -116
    //   378: ldc_w 1852
    //   381: invokestatic 1649	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   384: pop
    //   385: aload 24
    //   387: getfield 1855	android/app/Notification:defaults	I
    //   390: iconst_1
    //   391: iand
    //   392: ifne +579 -> 971
    //   395: getstatic 1861	android/provider/Settings$System:DEFAULT_NOTIFICATION_URI	Landroid/net/Uri;
    //   398: aload 24
    //   400: getfield 1864	android/app/Notification:sound	Landroid/net/Uri;
    //   403: invokevirtual 1867	android/net/Uri:equals	(Ljava/lang/Object;)Z
    //   406: istore 17
    //   408: aconst_null
    //   409: astore 23
    //   411: iload 17
    //   413: ifeq +569 -> 982
    //   416: getstatic 1861	android/provider/Settings$System:DEFAULT_NOTIFICATION_URI	Landroid/net/Uri;
    //   419: astore 23
    //   421: aload_0
    //   422: getfield 393	com/android/server/notification/NotificationManagerService:mSystemNotificationSound	Ljava/lang/String;
    //   425: ifnull +552 -> 977
    //   428: iconst_1
    //   429: istore_2
    //   430: aload 24
    //   432: getfield 1870	android/app/Notification:vibrate	[J
    //   435: ifnull +589 -> 1024
    //   438: iconst_1
    //   439: istore_3
    //   440: iload_3
    //   441: ifne +594 -> 1035
    //   444: iload_2
    //   445: ifeq +590 -> 1035
    //   448: aload_0
    //   449: getfield 1004	com/android/server/notification/NotificationManagerService:mAudioManager	Landroid/media/AudioManager;
    //   452: invokevirtual 1873	android/media/AudioManager:getRingerModeInternal	()I
    //   455: iconst_1
    //   456: if_icmpeq +573 -> 1029
    //   459: iload 16
    //   461: istore 17
    //   463: aload 24
    //   465: getfield 1855	android/app/Notification:defaults	I
    //   468: iconst_2
    //   469: iand
    //   470: ifeq +571 -> 1041
    //   473: iconst_1
    //   474: istore 8
    //   476: iload 8
    //   478: ifne +569 -> 1047
    //   481: iload 17
    //   483: ifne +564 -> 1047
    //   486: aload_1
    //   487: getfield 1876	com/android/server/notification/NotificationRecord:isUpdate	Z
    //   490: ifeq +568 -> 1058
    //   493: aload 24
    //   495: getfield 1235	android/app/Notification:flags	I
    //   498: bipush 8
    //   500: iand
    //   501: ifeq +551 -> 1052
    //   504: iconst_1
    //   505: istore 4
    //   507: iload 15
    //   509: istore 7
    //   511: iload 12
    //   513: istore 5
    //   515: iload_2
    //   516: istore 9
    //   518: iload_3
    //   519: istore 10
    //   521: iload 4
    //   523: ifne +234 -> 757
    //   526: aload_0
    //   527: aload 24
    //   529: aload_1
    //   530: getfield 803	com/android/server/notification/NotificationRecord:sbn	Landroid/service/notification/StatusBarNotification;
    //   533: invokevirtual 938	android/service/notification/StatusBarNotification:getPackageName	()Ljava/lang/String;
    //   536: invokevirtual 1880	com/android/server/notification/NotificationManagerService:sendAccessibilityEvent	(Landroid/app/Notification;Ljava/lang/CharSequence;)V
    //   539: iload 13
    //   541: istore 4
    //   543: iload_2
    //   544: ifeq +62 -> 606
    //   547: aload 24
    //   549: getfield 1235	android/app/Notification:flags	I
    //   552: iconst_4
    //   553: iand
    //   554: ifeq +510 -> 1064
    //   557: iconst_1
    //   558: istore 20
    //   560: aload 24
    //   562: invokestatic 1882	com/android/server/notification/NotificationManagerService:audioAttributesForNotification	(Landroid/app/Notification;)Landroid/media/AudioAttributes;
    //   565: astore 26
    //   567: aload_0
    //   568: aload 25
    //   570: putfield 999	com/android/server/notification/NotificationManagerService:mSoundNotificationKey	Ljava/lang/String;
    //   573: iload 13
    //   575: istore 4
    //   577: aload_0
    //   578: getfield 1004	com/android/server/notification/NotificationManagerService:mAudioManager	Landroid/media/AudioManager;
    //   581: aload 26
    //   583: invokestatic 1886	android/media/AudioAttributes:toLegacyStreamType	(Landroid/media/AudioAttributes;)I
    //   586: invokevirtual 1889	android/media/AudioManager:getStreamVolume	(I)I
    //   589: ifeq +17 -> 606
    //   592: aload_0
    //   593: getfield 1004	com/android/server/notification/NotificationManagerService:mAudioManager	Landroid/media/AudioManager;
    //   596: invokevirtual 1892	android/media/AudioManager:isAudioFocusExclusive	()Z
    //   599: ifeq +471 -> 1070
    //   602: iload 13
    //   604: istore 4
    //   606: iload 4
    //   608: istore 7
    //   610: iload 12
    //   612: istore 5
    //   614: iload_2
    //   615: istore 9
    //   617: iload_3
    //   618: istore 10
    //   620: iload_3
    //   621: ifeq +136 -> 757
    //   624: aload_0
    //   625: getfield 1004	com/android/server/notification/NotificationManagerService:mAudioManager	Landroid/media/AudioManager;
    //   628: invokevirtual 1873	android/media/AudioManager:getRingerModeInternal	()I
    //   631: ifne +22 -> 653
    //   634: iload 4
    //   636: istore 7
    //   638: iload 12
    //   640: istore 5
    //   642: iload_2
    //   643: istore 9
    //   645: iload_3
    //   646: istore 10
    //   648: iload 16
    //   650: ifeq +107 -> 757
    //   653: aload_0
    //   654: aload 25
    //   656: putfield 1021	com/android/server/notification/NotificationManagerService:mVibrateNotificationKey	Ljava/lang/String;
    //   659: iload 8
    //   661: ifne +8 -> 669
    //   664: iload 17
    //   666: ifeq +538 -> 1204
    //   669: invokestatic 1002	android/os/Binder:clearCallingIdentity	()J
    //   672: lstore 21
    //   674: aload_0
    //   675: getfield 1023	com/android/server/notification/NotificationManagerService:mVibrator	Landroid/os/Vibrator;
    //   678: astore 23
    //   680: aload_1
    //   681: getfield 803	com/android/server/notification/NotificationRecord:sbn	Landroid/service/notification/StatusBarNotification;
    //   684: invokevirtual 1618	android/service/notification/StatusBarNotification:getUid	()I
    //   687: istore 7
    //   689: aload_1
    //   690: getfield 803	com/android/server/notification/NotificationRecord:sbn	Landroid/service/notification/StatusBarNotification;
    //   693: invokevirtual 1614	android/service/notification/StatusBarNotification:getOpPkg	()Ljava/lang/String;
    //   696: astore 26
    //   698: getstatic 657	com/android/server/notification/NotificationManagerService:OP_DEFAULT_VIBRATION_PATTERN	[[J
    //   701: aload_0
    //   702: getfield 396	com/android/server/notification/NotificationManagerService:mVibrateIntensity	I
    //   705: aaload
    //   706: astore 27
    //   708: aload 24
    //   710: getfield 1235	android/app/Notification:flags	I
    //   713: iconst_4
    //   714: iand
    //   715: ifeq +475 -> 1190
    //   718: iconst_0
    //   719: istore 5
    //   721: aload 23
    //   723: iload 7
    //   725: aload 26
    //   727: aload 27
    //   729: iload 5
    //   731: aload 24
    //   733: invokestatic 1882	com/android/server/notification/NotificationManagerService:audioAttributesForNotification	(Landroid/app/Notification;)Landroid/media/AudioAttributes;
    //   736: invokevirtual 1895	android/os/Vibrator:vibrate	(ILjava/lang/String;[JILandroid/media/AudioAttributes;)V
    //   739: iconst_1
    //   740: istore 5
    //   742: lload 21
    //   744: invokestatic 1019	android/os/Binder:restoreCallingIdentity	(J)V
    //   747: iload_3
    //   748: istore 10
    //   750: iload_2
    //   751: istore 9
    //   753: iload 4
    //   755: istore 7
    //   757: iload 18
    //   759: ifeq +8 -> 767
    //   762: iload 9
    //   764: ifeq +551 -> 1315
    //   767: iload 19
    //   769: ifeq +8 -> 777
    //   772: iload 10
    //   774: ifeq +548 -> 1322
    //   777: aload_0
    //   778: getfield 704	com/android/server/notification/NotificationManagerService:mLights	Ljava/util/ArrayList;
    //   781: aload 25
    //   783: invokevirtual 1030	java/util/ArrayList:remove	(Ljava/lang/Object;)Z
    //   786: istore 16
    //   788: aload 24
    //   790: getfield 1235	android/app/Notification:flags	I
    //   793: iconst_1
    //   794: iand
    //   795: ifeq +534 -> 1329
    //   798: iload 6
    //   800: ifeq +529 -> 1329
    //   803: aload_1
    //   804: invokevirtual 1506	com/android/server/notification/NotificationRecord:getSuppressedVisualEffects	()I
    //   807: iconst_1
    //   808: iand
    //   809: ifne +520 -> 1329
    //   812: aload_0
    //   813: aload_1
    //   814: getfield 803	com/android/server/notification/NotificationRecord:sbn	Landroid/service/notification/StatusBarNotification;
    //   817: invokevirtual 938	android/service/notification/StatusBarNotification:getPackageName	()Ljava/lang/String;
    //   820: invokevirtual 1898	com/android/server/notification/NotificationManagerService:isNotificationLedEnabledImpl	(Ljava/lang/String;)Z
    //   823: ifeq +506 -> 1329
    //   826: aload_0
    //   827: getfield 704	com/android/server/notification/NotificationManagerService:mLights	Ljava/util/ArrayList;
    //   830: aload 25
    //   832: invokevirtual 1218	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   835: pop
    //   836: aload_0
    //   837: invokevirtual 1134	com/android/server/notification/NotificationManagerService:updateLightsLocked	()V
    //   840: aload_0
    //   841: getfield 1900	com/android/server/notification/NotificationManagerService:mUseAttentionLight	Z
    //   844: ifeq +10 -> 854
    //   847: aload_0
    //   848: getfield 1902	com/android/server/notification/NotificationManagerService:mAttentionLight	Lcom/android/server/lights/Light;
    //   851: invokevirtual 1907	com/android/server/lights/Light:pulse	()V
    //   854: iconst_1
    //   855: istore_2
    //   856: iload 5
    //   858: ifne +12 -> 870
    //   861: iload 7
    //   863: ifne +7 -> 870
    //   866: iload_2
    //   867: ifeq +27 -> 894
    //   870: aload_1
    //   871: invokevirtual 1506	com/android/server/notification/NotificationRecord:getSuppressedVisualEffects	()I
    //   874: iconst_1
    //   875: iand
    //   876: ifeq +471 -> 1347
    //   879: getstatic 601	com/android/server/notification/NotificationManagerService:DBG	Z
    //   882: ifeq +12 -> 894
    //   885: ldc -116
    //   887: ldc_w 1909
    //   890: invokestatic 1649	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   893: pop
    //   894: return
    //   895: iconst_0
    //   896: istore 16
    //   898: goto -846 -> 52
    //   901: iconst_0
    //   902: istore 6
    //   904: goto -841 -> 63
    //   907: iconst_1
    //   908: istore 17
    //   910: goto -831 -> 79
    //   913: iconst_0
    //   914: istore 17
    //   916: goto -837 -> 79
    //   919: astore_1
    //   920: lload 21
    //   922: invokestatic 1019	android/os/Binder:restoreCallingIdentity	(J)V
    //   925: aload_1
    //   926: athrow
    //   927: iconst_0
    //   928: istore 18
    //   930: goto -733 -> 197
    //   933: iconst_0
    //   934: istore 19
    //   936: goto -723 -> 213
    //   939: iload 15
    //   941: istore 7
    //   943: iload 12
    //   945: istore 5
    //   947: iload 4
    //   949: istore 9
    //   951: iload_2
    //   952: istore 10
    //   954: aload_0
    //   955: getfield 332	com/android/server/notification/NotificationManagerService:mUserProfiles	Lcom/android/server/notification/ManagedServices$UserProfiles;
    //   958: aload_1
    //   959: invokevirtual 941	com/android/server/notification/NotificationRecord:getUserId	()I
    //   962: invokevirtual 1657	com/android/server/notification/ManagedServices$UserProfiles:isCurrentProfile	(I)Z
    //   965: ifeq -208 -> 757
    //   968: goto -662 -> 306
    //   971: iconst_1
    //   972: istore 17
    //   974: goto -566 -> 408
    //   977: iconst_0
    //   978: istore_2
    //   979: goto -549 -> 430
    //   982: iload_3
    //   983: istore_2
    //   984: aload 24
    //   986: getfield 1864	android/app/Notification:sound	Landroid/net/Uri;
    //   989: ifnull -559 -> 430
    //   992: aload_0
    //   993: invokevirtual 1347	com/android/server/notification/NotificationManagerService:getContext	()Landroid/content/Context;
    //   996: aload 24
    //   998: getfield 1864	android/app/Notification:sound	Landroid/net/Uri;
    //   1001: ldc_w 1911
    //   1004: invokestatic 1917	android/media/RingtoneManager:validForSound	(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   1007: astore 23
    //   1009: aload 23
    //   1011: ifnull +8 -> 1019
    //   1014: iconst_1
    //   1015: istore_2
    //   1016: goto -586 -> 430
    //   1019: iconst_0
    //   1020: istore_2
    //   1021: goto -591 -> 430
    //   1024: iconst_0
    //   1025: istore_3
    //   1026: goto -586 -> 440
    //   1029: iconst_1
    //   1030: istore 17
    //   1032: goto -569 -> 463
    //   1035: iconst_0
    //   1036: istore 17
    //   1038: goto -575 -> 463
    //   1041: iconst_0
    //   1042: istore 8
    //   1044: goto -568 -> 476
    //   1047: iconst_1
    //   1048: istore_3
    //   1049: goto -563 -> 486
    //   1052: iconst_0
    //   1053: istore 4
    //   1055: goto -548 -> 507
    //   1058: iconst_0
    //   1059: istore 4
    //   1061: goto -554 -> 507
    //   1064: iconst_0
    //   1065: istore 20
    //   1067: goto -507 -> 560
    //   1070: invokestatic 1002	android/os/Binder:clearCallingIdentity	()J
    //   1073: lstore 21
    //   1075: aload_0
    //   1076: getfield 1004	com/android/server/notification/NotificationManagerService:mAudioManager	Landroid/media/AudioManager;
    //   1079: invokevirtual 1010	android/media/AudioManager:getRingtonePlayer	()Landroid/media/IRingtonePlayer;
    //   1082: astore 27
    //   1084: iload 14
    //   1086: istore 4
    //   1088: aload 27
    //   1090: ifnull +70 -> 1160
    //   1093: getstatic 601	com/android/server/notification/NotificationManagerService:DBG	Z
    //   1096: ifeq +41 -> 1137
    //   1099: ldc -116
    //   1101: new 909	java/lang/StringBuilder
    //   1104: dup
    //   1105: invokespecial 910	java/lang/StringBuilder:<init>	()V
    //   1108: ldc_w 1919
    //   1111: invokevirtual 916	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1114: aload 23
    //   1116: invokevirtual 1102	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1119: ldc_w 1921
    //   1122: invokevirtual 916	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1125: aload 26
    //   1127: invokevirtual 1102	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1130: invokevirtual 922	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1133: invokestatic 1649	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1136: pop
    //   1137: aload 27
    //   1139: aload 23
    //   1141: aload_1
    //   1142: getfield 803	com/android/server/notification/NotificationRecord:sbn	Landroid/service/notification/StatusBarNotification;
    //   1145: invokevirtual 1536	android/service/notification/StatusBarNotification:getUser	()Landroid/os/UserHandle;
    //   1148: iload 20
    //   1150: aload 26
    //   1152: invokeinterface 1925 5 0
    //   1157: iconst_1
    //   1158: istore 4
    //   1160: lload 21
    //   1162: invokestatic 1019	android/os/Binder:restoreCallingIdentity	(J)V
    //   1165: goto -559 -> 606
    //   1168: astore 23
    //   1170: lload 21
    //   1172: invokestatic 1019	android/os/Binder:restoreCallingIdentity	(J)V
    //   1175: iload 13
    //   1177: istore 4
    //   1179: goto -573 -> 606
    //   1182: astore_1
    //   1183: lload 21
    //   1185: invokestatic 1019	android/os/Binder:restoreCallingIdentity	(J)V
    //   1188: aload_1
    //   1189: athrow
    //   1190: iconst_m1
    //   1191: istore 5
    //   1193: goto -472 -> 721
    //   1196: astore_1
    //   1197: lload 21
    //   1199: invokestatic 1019	android/os/Binder:restoreCallingIdentity	(J)V
    //   1202: aload_1
    //   1203: athrow
    //   1204: iload 4
    //   1206: istore 7
    //   1208: iload 12
    //   1210: istore 5
    //   1212: iload_2
    //   1213: istore 9
    //   1215: iload_3
    //   1216: istore 10
    //   1218: aload 24
    //   1220: getfield 1870	android/app/Notification:vibrate	[J
    //   1223: arraylength
    //   1224: iconst_1
    //   1225: if_icmple -468 -> 757
    //   1228: aload_0
    //   1229: getfield 1023	com/android/server/notification/NotificationManagerService:mVibrator	Landroid/os/Vibrator;
    //   1232: astore 23
    //   1234: aload_1
    //   1235: getfield 803	com/android/server/notification/NotificationRecord:sbn	Landroid/service/notification/StatusBarNotification;
    //   1238: invokevirtual 1618	android/service/notification/StatusBarNotification:getUid	()I
    //   1241: istore 7
    //   1243: aload_1
    //   1244: getfield 803	com/android/server/notification/NotificationRecord:sbn	Landroid/service/notification/StatusBarNotification;
    //   1247: invokevirtual 1614	android/service/notification/StatusBarNotification:getOpPkg	()Ljava/lang/String;
    //   1250: astore 26
    //   1252: getstatic 657	com/android/server/notification/NotificationManagerService:OP_DEFAULT_VIBRATION_PATTERN	[[J
    //   1255: aload_0
    //   1256: getfield 396	com/android/server/notification/NotificationManagerService:mVibrateIntensity	I
    //   1259: aaload
    //   1260: astore 27
    //   1262: aload 24
    //   1264: getfield 1235	android/app/Notification:flags	I
    //   1267: iconst_4
    //   1268: iand
    //   1269: ifeq +40 -> 1309
    //   1272: iconst_0
    //   1273: istore 5
    //   1275: aload 23
    //   1277: iload 7
    //   1279: aload 26
    //   1281: aload 27
    //   1283: iload 5
    //   1285: aload 24
    //   1287: invokestatic 1882	com/android/server/notification/NotificationManagerService:audioAttributesForNotification	(Landroid/app/Notification;)Landroid/media/AudioAttributes;
    //   1290: invokevirtual 1895	android/os/Vibrator:vibrate	(ILjava/lang/String;[JILandroid/media/AudioAttributes;)V
    //   1293: iconst_1
    //   1294: istore 5
    //   1296: iload 4
    //   1298: istore 7
    //   1300: iload_2
    //   1301: istore 9
    //   1303: iload_3
    //   1304: istore 10
    //   1306: goto -549 -> 757
    //   1309: iconst_m1
    //   1310: istore 5
    //   1312: goto -37 -> 1275
    //   1315: aload_0
    //   1316: invokespecial 459	com/android/server/notification/NotificationManagerService:clearSoundLocked	()V
    //   1319: goto -552 -> 767
    //   1322: aload_0
    //   1323: invokespecial 469	com/android/server/notification/NotificationManagerService:clearVibrateLocked	()V
    //   1326: goto -549 -> 777
    //   1329: iload 11
    //   1331: istore_2
    //   1332: iload 16
    //   1334: ifeq -478 -> 856
    //   1337: aload_0
    //   1338: invokevirtual 1134	com/android/server/notification/NotificationManagerService:updateLightsLocked	()V
    //   1341: iload 11
    //   1343: istore_2
    //   1344: goto -488 -> 856
    //   1347: iload 5
    //   1349: ifeq +41 -> 1390
    //   1352: iconst_1
    //   1353: istore_3
    //   1354: iload 7
    //   1356: ifeq +39 -> 1395
    //   1359: iconst_1
    //   1360: istore 4
    //   1362: iload_2
    //   1363: ifeq +38 -> 1401
    //   1366: iconst_1
    //   1367: istore_2
    //   1368: aload 25
    //   1370: iload_3
    //   1371: iload 4
    //   1373: iload_2
    //   1374: invokestatic 1929	com/android/server/EventLogTags:writeNotificationAlert	(Ljava/lang/String;III)V
    //   1377: aload_0
    //   1378: getfield 278	com/android/server/notification/NotificationManagerService:mHandler	Landroid/os/Handler;
    //   1381: aload_0
    //   1382: getfield 725	com/android/server/notification/NotificationManagerService:mBuzzBeepBlinked	Ljava/lang/Runnable;
    //   1385: invokevirtual 1636	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   1388: pop
    //   1389: return
    //   1390: iconst_0
    //   1391: istore_3
    //   1392: goto -38 -> 1354
    //   1395: iconst_0
    //   1396: istore 4
    //   1398: goto -36 -> 1362
    //   1401: iconst_0
    //   1402: istore_2
    //   1403: goto -35 -> 1368
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1406	0	this	NotificationManagerService
    //   0	1406	1	paramNotificationRecord	NotificationRecord
    //   214	1189	2	i	int
    //   219	1173	3	j	int
    //   216	1181	4	k	int
    //   253	1095	5	m	int
    //   61	842	6	n	int
    //   249	1106	7	i1	int
    //   156	887	8	i2	int
    //   257	1045	9	i3	int
    //   260	1045	10	i4	int
    //   13	1329	11	i5	int
    //   1	1208	12	i6	int
    //   10	1166	13	i7	int
    //   7	1078	14	i8	int
    //   4	936	15	i9	int
    //   50	1283	16	bool1	boolean
    //   77	960	17	bool2	boolean
    //   195	734	18	bool3	boolean
    //   211	724	19	bool4	boolean
    //   221	928	20	bool5	boolean
    //   151	1047	21	l	long
    //   168	972	23	localObject1	Object
    //   1168	1	23	localRemoteException	RemoteException
    //   1232	44	23	localVibrator	Vibrator
    //   22	1264	24	localNotification	Notification
    //   28	1341	25	str	String
    //   565	715	26	localObject2	Object
    //   706	576	27	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   153	158	919	finally
    //   1075	1084	1168	android/os/RemoteException
    //   1093	1137	1168	android/os/RemoteException
    //   1137	1157	1168	android/os/RemoteException
    //   1075	1084	1182	finally
    //   1093	1137	1182	finally
    //   1137	1157	1182	finally
    //   674	718	1196	finally
    //   721	739	1196	finally
  }
  
  void cancelAllLocked(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ManagedServices.ManagedServiceInfo paramManagedServiceInfo, boolean paramBoolean)
  {
    String str;
    int i;
    label35:
    NotificationRecord localNotificationRecord;
    Object localObject;
    if (paramManagedServiceInfo == null)
    {
      str = null;
      EventLogTags.writeNotificationCancelAll(paramInt1, paramInt2, null, paramInt3, 0, 0, paramInt4, str);
      paramManagedServiceInfo = null;
      i = this.mNotificationList.size() - 1;
      if (i < 0) {
        break label176;
      }
      localNotificationRecord = (NotificationRecord)this.mNotificationList.get(i);
      if (!paramBoolean) {
        break label99;
      }
      if (notificationMatchesCurrentProfiles(localNotificationRecord, paramInt3)) {
        break label113;
      }
      localObject = paramManagedServiceInfo;
    }
    for (;;)
    {
      i -= 1;
      paramManagedServiceInfo = (ManagedServices.ManagedServiceInfo)localObject;
      break label35;
      str = paramManagedServiceInfo.component.toShortString();
      break;
      label99:
      localObject = paramManagedServiceInfo;
      if (notificationMatchesUserId(localNotificationRecord, paramInt3))
      {
        label113:
        localObject = paramManagedServiceInfo;
        if ((localNotificationRecord.getFlags() & 0x22) == 0)
        {
          this.mNotificationList.remove(i);
          cancelNotificationLocked(localNotificationRecord, true, paramInt4);
          localObject = paramManagedServiceInfo;
          if (paramManagedServiceInfo == null) {
            localObject = new ArrayList();
          }
          ((ArrayList)localObject).add(localNotificationRecord);
        }
      }
    }
    label176:
    if (paramManagedServiceInfo != null) {}
    for (paramInt3 = paramManagedServiceInfo.size();; paramInt3 = 0)
    {
      paramInt4 = 0;
      while (paramInt4 < paramInt3)
      {
        cancelGroupChildrenLocked((NotificationRecord)paramManagedServiceInfo.get(paramInt4), paramInt1, paramInt2, str, 12, false);
        paramInt4 += 1;
      }
    }
    updateLightsLocked();
  }
  
  boolean cancelAllNotificationsInt(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, boolean paramBoolean, int paramInt5, int paramInt6, ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
  {
    String str;
    if (paramManagedServiceInfo == null) {
      str = null;
    }
    for (;;)
    {
      EventLogTags.writeNotificationCancelAll(paramInt1, paramInt2, paramString, paramInt5, paramInt3, paramInt4, paramInt6, str);
      synchronized (this.mNotificationList)
      {
        int i = this.mNotificationList.size();
        paramManagedServiceInfo = null;
        i -= 1;
        label51:
        if (i >= 0)
        {
          NotificationRecord localNotificationRecord = (NotificationRecord)this.mNotificationList.get(i);
          boolean bool = notificationMatchesUserId(localNotificationRecord, paramInt5);
          if (!bool) {
            localObject = paramManagedServiceInfo;
          }
          label132:
          do
          {
            do
            {
              do
              {
                do
                {
                  i -= 1;
                  paramManagedServiceInfo = (ManagedServices.ManagedServiceInfo)localObject;
                  break label51;
                  str = paramManagedServiceInfo.component.toShortString();
                  break;
                  if (localNotificationRecord.getUserId() != -1) {
                    break label132;
                  }
                  localObject = paramManagedServiceInfo;
                } while (paramString == null);
                localObject = paramManagedServiceInfo;
              } while ((localNotificationRecord.getFlags() & paramInt3) != paramInt3);
              localObject = paramManagedServiceInfo;
            } while ((localNotificationRecord.getFlags() & paramInt4) != 0);
            if (paramString == null) {
              break label187;
            }
            localObject = paramManagedServiceInfo;
          } while (!localNotificationRecord.sbn.getPackageName().equals(paramString));
          label187:
          Object localObject = paramManagedServiceInfo;
          if (paramManagedServiceInfo == null) {
            localObject = new ArrayList();
          }
          ((ArrayList)localObject).add(localNotificationRecord);
          if (!paramBoolean) {
            return true;
          }
          this.mNotificationList.remove(i);
          cancelNotificationLocked(localNotificationRecord, false, paramInt6);
        }
      }
    }
    if ((paramBoolean) && (paramManagedServiceInfo != null))
    {
      paramInt4 = paramManagedServiceInfo.size();
      paramInt3 = 0;
      while (paramInt3 < paramInt4)
      {
        cancelGroupChildrenLocked((NotificationRecord)paramManagedServiceInfo.get(paramInt3), paramInt1, paramInt2, str, 12, false);
        paramInt3 += 1;
      }
    }
    if (paramManagedServiceInfo != null) {
      updateLightsLocked();
    }
    if (paramManagedServiceInfo != null) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
  
  void cancelNotification(final int paramInt1, final int paramInt2, final String paramString1, final String paramString2, final int paramInt3, final int paramInt4, final int paramInt5, final boolean paramBoolean, final int paramInt6, final int paramInt7, final ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        String str;
        if (paramManagedServiceInfo == null) {
          str = null;
        }
        for (;;)
        {
          if (NotificationManagerService.DBG) {
            EventLogTags.writeNotificationCancel(paramInt1, paramInt2, paramString1, paramInt3, paramString2, paramInt6, paramInt4, paramInt5, paramInt7, str);
          }
          synchronized (NotificationManagerService.this.mNotificationList)
          {
            int i = NotificationManagerService.this.indexOfNotificationLocked(paramString1, paramString2, paramInt3, paramInt6);
            if (i >= 0)
            {
              NotificationRecord localNotificationRecord = (NotificationRecord)NotificationManagerService.this.mNotificationList.get(i);
              if (paramInt7 == 1) {
                NotificationManagerService.-get23(NotificationManagerService.this).registerClickedByUser(localNotificationRecord);
              }
              int j = localNotificationRecord.getNotification().flags;
              int k = paramInt4;
              int m = paramInt4;
              if ((j & k) != m)
              {
                return;
                str = paramManagedServiceInfo.component.toShortString();
                continue;
              }
              j = localNotificationRecord.getNotification().flags;
              k = paramInt5;
              if ((j & k) != 0) {
                return;
              }
              NotificationManagerService.this.mNotificationList.remove(i);
              NotificationManagerService.-wrap14(NotificationManagerService.this, localNotificationRecord, paramBoolean, paramInt7);
              NotificationManagerService.-wrap13(NotificationManagerService.this, localNotificationRecord, paramInt1, paramInt2, str, 12, paramBoolean);
              NotificationManagerService.this.updateLightsLocked();
            }
            return;
          }
        }
      }
    });
  }
  
  void cancelToastLocked(int paramInt)
  {
    ToastRecord localToastRecord1 = (ToastRecord)this.mToastQueue.get(paramInt);
    if ((OnePlusProcessManager.isSupportFrozenApp()) && (localToastRecord1 != null)) {
      OnePlusProcessManager.resumeProcessByUID_out(localToastRecord1.uid, "cancelToastLocked");
    }
    try
    {
      localToastRecord1.callback.hide();
      ToastRecord localToastRecord2 = (ToastRecord)this.mToastQueue.remove(paramInt);
      this.mWindowManagerInternal.removeWindowToken(localToastRecord2.token, true);
      keepProcessAliveIfNeededLocked(localToastRecord1.pid);
      if (this.mToastQueue.size() > 0) {
        showNextToastLocked();
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("NotificationService", "Object died trying to hide notification " + localToastRecord1.callback + " in package " + localToastRecord1.pkg);
      }
    }
  }
  
  void dumpImpl(PrintWriter paramPrintWriter, DumpFilter paramDumpFilter)
  {
    paramPrintWriter.print("Current Notification Manager state");
    if (paramDumpFilter.filtered)
    {
      paramPrintWriter.print(" (filtered to ");
      paramPrintWriter.print(paramDumpFilter);
      paramPrintWriter.print(")");
    }
    paramPrintWriter.println(':');
    boolean bool;
    int j;
    int i;
    Object localObject1;
    label558:
    Object localObject2;
    label690:
    int k;
    if (paramDumpFilter.filtered)
    {
      bool = paramDumpFilter.zen;
      if (!bool) {}
      synchronized (this.mToastQueue)
      {
        j = this.mToastQueue.size();
        if (j > 0)
        {
          paramPrintWriter.println("  Toast Queue:");
          i = 0;
          while (i < j)
          {
            ((ToastRecord)this.mToastQueue.get(i)).dump(paramPrintWriter, "    ", paramDumpFilter);
            i += 1;
          }
          paramPrintWriter.println("  ");
        }
        ??? = this.mNotificationList;
        if (bool) {
          break label690;
        }
      }
      try
      {
        j = this.mNotificationList.size();
        if (j > 0)
        {
          paramPrintWriter.println("  Notification List:");
          i = 0;
          while (i < j)
          {
            localObject1 = (NotificationRecord)this.mNotificationList.get(i);
            if ((!paramDumpFilter.filtered) || (paramDumpFilter.matches(((NotificationRecord)localObject1).sbn))) {
              ((NotificationRecord)localObject1).dump(paramPrintWriter, "    ", getContext(), paramDumpFilter.redact);
            }
            i += 1;
            continue;
            paramPrintWriter = finally;
            throw paramPrintWriter;
          }
          paramPrintWriter.println("  ");
        }
        if (paramDumpFilter.filtered) {
          break label558;
        }
        j = this.mLights.size();
        if (j > 0)
        {
          paramPrintWriter.println("  Lights List:");
          i = 0;
          if (i < j)
          {
            if (i == j - 1) {
              paramPrintWriter.print("  > ");
            }
            for (;;)
            {
              paramPrintWriter.println((String)this.mLights.get(i));
              i += 1;
              break;
              paramPrintWriter.print("    ");
            }
          }
          paramPrintWriter.println("  ");
        }
      }
      finally {}
      paramPrintWriter.println("  mUseAttentionLight=" + this.mUseAttentionLight);
      paramPrintWriter.println("  mNotificationPulseEnabled=" + this.mNotificationPulseEnabled);
      paramPrintWriter.println("  mSoundNotificationKey=" + this.mSoundNotificationKey);
      paramPrintWriter.println("  mVibrateNotificationKey=" + this.mVibrateNotificationKey);
      paramPrintWriter.println("  mDisableNotificationEffects=" + this.mDisableNotificationEffects);
      paramPrintWriter.println("  mCallState=" + callStateToString(this.mCallState));
      paramPrintWriter.println("  mSystemReady=" + this.mSystemReady);
      paramPrintWriter.println("  mMaxPackageEnqueueRate=" + this.mMaxPackageEnqueueRate);
      paramPrintWriter.println("  mArchive=" + this.mArchive.toString());
      localObject1 = this.mArchive.descendingIterator();
      i = 0;
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (StatusBarNotification)((Iterator)localObject1).next();
        if ((paramDumpFilter == null) || (paramDumpFilter.matches((StatusBarNotification)localObject2)))
        {
          paramPrintWriter.println("    " + localObject2);
          j = i + 1;
          i = j;
          if (j >= 5) {
            if (((Iterator)localObject1).hasNext()) {
              paramPrintWriter.println("    ...");
            }
          }
        }
      }
      if (!bool)
      {
        paramPrintWriter.println("\n  Usage Stats:");
        this.mUsageStats.dump(paramPrintWriter, "    ", paramDumpFilter);
      }
      if ((!paramDumpFilter.filtered) || (bool))
      {
        paramPrintWriter.println("\n  Zen Mode:");
        paramPrintWriter.print("    mInterruptionFilter=");
        paramPrintWriter.println(this.mInterruptionFilter);
        this.mZenModeHelper.dump(paramPrintWriter, "    ");
        paramPrintWriter.println("\n  Zen Log:");
        ZenLog.dump(paramPrintWriter, "    ");
      }
      if (!bool)
      {
        paramPrintWriter.println("\n  Ranking Config:");
        this.mRankingHelper.dump(paramPrintWriter, "    ", paramDumpFilter);
        paramPrintWriter.println("\n  Notification listeners:");
        this.mListeners.dump(paramPrintWriter, paramDumpFilter);
        paramPrintWriter.print("    mListenerHints: ");
        paramPrintWriter.println(this.mListenerHints);
        paramPrintWriter.print("    mListenersDisablingEffects: (");
        k = this.mListenersDisablingEffects.size();
        i = 0;
      }
    }
    for (;;)
    {
      if (i < k)
      {
        j = this.mListenersDisablingEffects.keyAt(i);
        if (i > 0) {
          paramPrintWriter.print(';');
        }
        paramPrintWriter.print("hint[" + j + "]:");
        localObject1 = (ArraySet)this.mListenersDisablingEffects.valueAt(i);
        int m = ((ArraySet)localObject1).size();
        j = 0;
        while (j < m)
        {
          if (i > 0) {
            paramPrintWriter.print(',');
          }
          paramPrintWriter.print(((ManagedServices.ManagedServiceInfo)((ArraySet)localObject1).valueAt(i)).component);
          j += 1;
        }
      }
      paramPrintWriter.println(')');
      paramPrintWriter.println("\n  mRankerServicePackageName: " + this.mRankerServicePackageName);
      paramPrintWriter.println("\n  Notification ranker services:");
      this.mRankerServices.dump(paramPrintWriter, paramDumpFilter);
      paramPrintWriter.println("\n  Policy access:");
      paramPrintWriter.print("    mPolicyAccess: ");
      paramPrintWriter.println(this.mPolicyAccess);
      paramPrintWriter.println("\n  Condition providers:");
      this.mConditionProviders.dump(paramPrintWriter, paramDumpFilter);
      paramPrintWriter.println("\n  Group summaries:");
      localObject1 = this.mSummaryByGroupKey.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        NotificationRecord localNotificationRecord = (NotificationRecord)((Map.Entry)localObject2).getValue();
        paramPrintWriter.println("    " + (String)((Map.Entry)localObject2).getKey() + " -> " + localNotificationRecord.getKey());
        if (this.mNotificationsByKey.get(localNotificationRecord.getKey()) != localNotificationRecord)
        {
          paramPrintWriter.println("!!!!!!LEAK: Record not found in mNotificationsByKey.");
          localNotificationRecord.dump(paramPrintWriter, "      ", getContext(), paramDumpFilter.redact);
        }
      }
      return;
      bool = false;
      break;
      i += 1;
    }
  }
  
  void enqueueNotificationInternal(String paramString1, String paramString2, int paramInt1, int paramInt2, String paramString3, int paramInt3, Notification paramNotification, int[] paramArrayOfInt, int paramInt4)
  {
    if (DBG) {
      Slog.v("NotificationService", "enqueueNotificationInternal: pkg=" + paramString1 + " id=" + paramInt3 + " notification=" + paramNotification);
    }
    checkCallerIsSystemOrSameApp(paramString1);
    boolean bool1;
    if (!isUidSystem(paramInt1)) {
      bool1 = "android".equals(paramString1);
    }
    boolean bool3;
    int i;
    Object localObject2;
    for (;;)
    {
      bool3 = NotificationListeners.-wrap0(this.mListeners, paramString1);
      i = ActivityManager.handleIncomingUser(paramInt2, paramInt1, paramInt4, true, false, "enqueueNotification", paramString1);
      ??? = new UserHandle(i);
      try
      {
        localObject2 = getContext().getPackageManager();
        if (i == -1) {}
        for (paramInt4 = 0;; paramInt4 = i)
        {
          Notification.addFieldsFromContext(((PackageManager)localObject2).getApplicationInfoAsUser(paramString1, 268435456, paramInt4), i, paramNotification);
          this.mUsageStats.registerEnqueuedByApp(paramString1);
          if ((paramString1 != null) && (paramNotification != null)) {
            break label239;
          }
          throw new IllegalArgumentException("null not allowed: pkg=" + paramString1 + " id=" + paramInt3 + " notification=" + paramNotification);
          bool1 = true;
          break;
        }
        paramString2 = new StatusBarNotification(paramString1, paramString2, paramInt3, paramString3, paramInt1, paramInt2, 0, paramNotification, (UserHandle)???);
      }
      catch (PackageManager.NameNotFoundException paramString1)
      {
        Slog.e("NotificationService", "Cannot create a context for sending app", paramString1);
        return;
      }
    }
    label239:
    boolean bool2 = false;
    ??? = paramNotification.extras;
    if (??? != null) {
      bool2 = ((Bundle)???).getBoolean("oneplus.mustShow", false);
    }
    if ((bool1) || (bool3)) {}
    while (paramNotification.allPendingIntents != null)
    {
      paramInt2 = paramNotification.allPendingIntents.size();
      if (paramInt2 > 0)
      {
        paramString1 = (ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class);
        long l = ((DeviceIdleController.LocalService)LocalServices.getService(DeviceIdleController.LocalService.class)).getNotificationWhitelistDuration();
        paramInt1 = 0;
        for (;;)
        {
          if (paramInt1 < paramInt2)
          {
            paramString3 = (PendingIntent)paramNotification.allPendingIntents.valueAt(paramInt1);
            if (paramString3 != null) {
              paramString1.setPendingIntentWhitelistDuration(paramString3.getTarget(), l);
            }
            paramInt1 += 1;
            continue;
            if (bool2) {
              break;
            }
            synchronized (this.mNotificationList)
            {
              if (this.mNotificationsByKey.get(paramString2.getKey()) != null)
              {
                float f = this.mUsageStats.getAppEnqueueRate(paramString1);
                if (f > this.mMaxPackageEnqueueRate)
                {
                  this.mUsageStats.registerOverRateQuota(paramString1);
                  l = SystemClock.elapsedRealtime();
                  if (l - this.mLastOverRateLogTime > 5000L)
                  {
                    Slog.e("NotificationService", "Package enqueue rate is " + f + ". Shedding events. package=" + paramString1);
                    this.mLastOverRateLogTime = l;
                  }
                  return;
                }
              }
              paramInt4 = 0;
              int j = this.mNotificationList.size();
              paramInt1 = 0;
              if (paramInt1 < j)
              {
                localObject2 = (NotificationRecord)this.mNotificationList.get(paramInt1);
                paramInt2 = paramInt4;
                if (!((NotificationRecord)localObject2).sbn.getPackageName().equals(paramString1)) {
                  break label685;
                }
                paramInt2 = paramInt4;
                if (((NotificationRecord)localObject2).sbn.getUserId() != i) {
                  break label685;
                }
                if (((NotificationRecord)localObject2).sbn.getId() != paramInt3) {
                  break label619;
                }
                bool1 = TextUtils.equals(((NotificationRecord)localObject2).sbn.getTag(), paramString3);
                if (bool1) {
                  break;
                }
              }
              label619:
              paramInt4 += 1;
              paramInt2 = paramInt4;
              if (paramInt4 >= 50)
              {
                this.mUsageStats.registerOverCountQuota(paramString1);
                Slog.e("NotificationService", "Package has already posted " + paramInt4 + " notifications.  Not showing more.  package=" + paramString1);
                return;
              }
              label685:
              paramInt1 += 1;
              paramInt4 = paramInt2;
            }
          }
        }
      }
    }
    paramNotification.priority = clamp(paramNotification.priority, -2, 2);
    paramString1 = new NotificationRecord(getContext(), paramString2);
    this.mHandler.post(new EnqueueNotificationRunnable(i, paramString1));
    paramArrayOfInt[0] = paramInt3;
  }
  
  int indexOfNotificationLocked(String paramString)
  {
    int j = this.mNotificationList.size();
    int i = 0;
    while (i < j)
    {
      if (paramString.equals(((NotificationRecord)this.mNotificationList.get(i)).getKey())) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  int indexOfNotificationLocked(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = this.mNotificationList;
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      NotificationRecord localNotificationRecord = (NotificationRecord)localArrayList.get(i);
      if ((notificationMatchesUserId(localNotificationRecord, paramInt2)) && (localNotificationRecord.sbn.getId() == paramInt1) && (TextUtils.equals(localNotificationRecord.sbn.getTag(), paramString2)) && (localNotificationRecord.sbn.getPackageName().equals(paramString1))) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  int indexOfToastLocked(String paramString, ITransientNotification paramITransientNotification)
  {
    paramITransientNotification = paramITransientNotification.asBinder();
    ArrayList localArrayList = this.mToastQueue;
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      ToastRecord localToastRecord = (ToastRecord)localArrayList.get(i);
      if ((localToastRecord.pkg.equals(paramString)) && (localToastRecord.callback.asBinder() == paramITransientNotification)) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public boolean isNotificationLedEnabledImpl(String paramString)
  {
    return !this.mNotificationLedBlockedPackages.contains(paramString);
  }
  
  void keepProcessAliveIfNeededLocked(int paramInt)
  {
    boolean bool = false;
    int j = 0;
    Object localObject = this.mToastQueue;
    int m = ((ArrayList)localObject).size();
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (((ToastRecord)((ArrayList)localObject).get(i)).pid == paramInt) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    try
    {
      localObject = this.mAm;
      IBinder localIBinder = this.mForegroundToken;
      if (j > 0) {
        bool = true;
      }
      ((IActivityManager)localObject).setProcessForeground(localIBinder, paramInt, bool);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500)
    {
      this.mSystemReady = true;
      this.mAudioManager = ((AudioManager)getContext().getSystemService("audio"));
      this.mAudioManagerInternal = ((AudioManagerInternal)getLocalService(AudioManagerInternal.class));
      this.mVrManagerInternal = ((VrManagerInternal)getLocalService(VrManagerInternal.class));
      this.mWindowManagerInternal = ((WindowManagerInternal)LocalServices.getService(WindowManagerInternal.class));
      this.mZenModeHelper.onSystemReady();
    }
    while (paramInt != 600) {
      return;
    }
    this.mSettingsObserver.observe();
    this.mListeners.onBootPhaseAppsCanStart();
    this.mRankerServices.onBootPhaseAppsCanStart();
    this.mConditionProviders.onBootPhaseAppsCanStart();
  }
  
  public void onStart()
  {
    Resources localResources = getContext().getResources();
    this.mMaxPackageEnqueueRate = Settings.Global.getFloat(getContext().getContentResolver(), "max_notification_enqueue_rate", 10.0F);
    this.mAm = ActivityManagerNative.getDefault();
    this.mAppOps = ((AppOpsManager)getContext().getSystemService("appops"));
    this.mVibrator = ((Vibrator)getContext().getSystemService("vibrator"));
    this.mAppUsageStats = ((UsageStatsManagerInternal)LocalServices.getService(UsageStatsManagerInternal.class));
    this.mRankerServicePackageName = getContext().getPackageManager().getServicesSystemSharedLibraryPackageName();
    this.mHandler = new WorkerHandler(null);
    this.mRankingThread.start();
    try
    {
      Object localObject = localResources.getStringArray(17236027);
      this.mUsageStats = new NotificationUsageStats(getContext());
      this.mRankingHandler = new RankingHandlerWorker(this.mRankingThread.getLooper());
      this.mRankingHelper = new RankingHelper(getContext(), this.mRankingHandler, this.mUsageStats, (String[])localObject);
      this.mConditionProviders = new ConditionProviders(getContext(), this.mHandler, this.mUserProfiles);
      this.mZenModeHelper = new ZenModeHelper(getContext(), this.mHandler.getLooper(), this.mConditionProviders);
      this.mZenModeHelper.addCallback(new ZenModeHelper.Callback()
      {
        public void onConfigChanged()
        {
          NotificationManagerService.this.savePolicyFile();
        }
        
        void onPolicyChanged()
        {
          NotificationManagerService.-wrap33(NotificationManagerService.this, "android.app.action.NOTIFICATION_POLICY_CHANGED");
        }
        
        void onZenModeChanged()
        {
          NotificationManagerService.-wrap33(NotificationManagerService.this, "android.app.action.INTERRUPTION_FILTER_CHANGED");
          NotificationManagerService.this.getContext().sendBroadcastAsUser(new Intent("android.app.action.INTERRUPTION_FILTER_CHANGED_INTERNAL").addFlags(67108864), UserHandle.ALL, "android.permission.MANAGE_NOTIFICATIONS");
          synchronized (NotificationManagerService.this.mNotificationList)
          {
            NotificationManagerService.-wrap35(NotificationManagerService.this);
            return;
          }
        }
      });
      localObject = new File(Environment.getDataDirectory(), "system");
      this.mPolicyFile = new AtomicFile(new File((File)localObject, "notification_policy.xml"));
      syncBlockDb();
      this.mNotificationLedPolicyFile = new AtomicFile(new File((File)localObject, "notification_led_policy.xml"));
      loadNotificationLedPolicyFile();
      this.mListeners = new NotificationListeners();
      this.mRankerServices = new NotificationRankers();
      this.mRankerServices.registerRanker();
      this.mStatusBar = ((StatusBarManagerInternal)getLocalService(StatusBarManagerInternal.class));
      if (this.mStatusBar != null) {
        this.mStatusBar.setNotificationDelegate(this.mNotificationDelegate);
      }
      localObject = (LightsManager)getLocalService(LightsManager.class);
      this.mNotificationLight = ((LightsManager)localObject).getLight(4);
      this.mAttentionLight = ((LightsManager)localObject).getLight(5);
      this.mDefaultNotificationColor = localResources.getColor(17170697);
      this.mDefaultNotificationLedOn = localResources.getInteger(17694810);
      this.mDefaultNotificationLedOff = localResources.getInteger(17694811);
      this.mDefaultVibrationPattern = getLongArray(localResources, 17236023, 17, DEFAULT_VIBRATE_PATTERN);
      this.mFallbackVibrationPattern = getLongArray(localResources, 17236024, 17, DEFAULT_VIBRATE_PATTERN);
      this.mUseAttentionLight = localResources.getBoolean(17956904);
      this.mDefLowBatteryWarningLevel = localResources.getInteger(17694808);
      if (Settings.Global.getInt(getContext().getContentResolver(), "device_provisioned", 0) == 0) {
        this.mDisableNotificationEffects = true;
      }
      this.mZenModeHelper.initZenMode();
      this.mInterruptionFilter = this.mZenModeHelper.getZenModeListenerInterruptionFilter();
      this.mUserProfiles.updateCache(getContext());
      listenForCallState();
      localObject = new IntentFilter();
      ((IntentFilter)localObject).addAction("android.intent.action.SCREEN_ON");
      ((IntentFilter)localObject).addAction("android.intent.action.SCREEN_OFF");
      ((IntentFilter)localObject).addAction("android.intent.action.PHONE_STATE");
      ((IntentFilter)localObject).addAction("android.intent.action.USER_PRESENT");
      ((IntentFilter)localObject).addAction("android.intent.action.USER_STOPPED");
      ((IntentFilter)localObject).addAction("android.intent.action.USER_SWITCHED");
      ((IntentFilter)localObject).addAction("android.intent.action.USER_ADDED");
      ((IntentFilter)localObject).addAction("android.intent.action.USER_REMOVED");
      ((IntentFilter)localObject).addAction("android.intent.action.USER_UNLOCKED");
      ((IntentFilter)localObject).addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
      ((IntentFilter)localObject).addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
      if (OnePlusAppBootManager.IN_USING) {
        ((IntentFilter)localObject).addAction("action.appboot.notification_listener_update");
      }
      getContext().registerReceiver(this.mIntentReceiver, (IntentFilter)localObject);
      localObject = new IntentFilter();
      ((IntentFilter)localObject).addAction("android.intent.action.PACKAGE_ADDED");
      ((IntentFilter)localObject).addAction("android.intent.action.PACKAGE_REMOVED");
      ((IntentFilter)localObject).addAction("android.intent.action.PACKAGE_CHANGED");
      ((IntentFilter)localObject).addAction("android.intent.action.PACKAGE_RESTARTED");
      ((IntentFilter)localObject).addAction("android.intent.action.QUERY_PACKAGE_RESTART");
      ((IntentFilter)localObject).addDataScheme("package");
      getContext().registerReceiverAsUser(this.mPackageIntentReceiver, UserHandle.ALL, (IntentFilter)localObject, null, null);
      localObject = new IntentFilter();
      ((IntentFilter)localObject).addAction("android.intent.action.PACKAGES_SUSPENDED");
      getContext().registerReceiverAsUser(this.mPackageIntentReceiver, UserHandle.ALL, (IntentFilter)localObject, null, null);
      localObject = new IntentFilter("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
      getContext().registerReceiverAsUser(this.mPackageIntentReceiver, UserHandle.ALL, (IntentFilter)localObject, null, null);
      this.mSettingsObserver = new SettingsObserver(this.mHandler);
      this.mArchive = new Archive(localResources.getInteger(17694817));
      publishBinderService("notification", this.mService);
      publishLocalService(NotificationManagerInternal.class, this.mInternalService);
      this.mConfigManager = ((CarrierConfigManager)getContext().getSystemService("carrier_config"));
      return;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      for (;;)
      {
        String[] arrayOfString = new String[0];
      }
    }
  }
  
  public void saveNotificationLedPolicyFile()
  {
    this.mHandler.removeMessages(7);
    this.mHandler.sendEmptyMessage(7);
  }
  
  public void savePolicyFile()
  {
    this.mHandler.removeMessages(3);
    this.mHandler.sendEmptyMessage(3);
  }
  
  void sendAccessibilityEvent(Notification paramNotification, CharSequence paramCharSequence)
  {
    AccessibilityManager localAccessibilityManager = AccessibilityManager.getInstance(getContext());
    if (!localAccessibilityManager.isEnabled()) {
      return;
    }
    AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(64);
    localAccessibilityEvent.setPackageName(paramCharSequence);
    localAccessibilityEvent.setClassName(Notification.class.getName());
    localAccessibilityEvent.setParcelableData(paramNotification);
    paramNotification = paramNotification.tickerText;
    if (!TextUtils.isEmpty(paramNotification)) {
      localAccessibilityEvent.getText().add(paramNotification);
    }
    localAccessibilityManager.sendAccessibilityEvent(localAccessibilityEvent);
  }
  
  void setAudioManager(AudioManager paramAudioManager)
  {
    this.mAudioManager = paramAudioManager;
  }
  
  void setHandler(Handler paramHandler)
  {
    this.mHandler = paramHandler;
  }
  
  public void setNotificationLedStatusImpl(String paramString, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mNotificationLedBlockedPackages.remove(paramString);
    }
    for (;;)
    {
      saveNotificationLedPolicyFile();
      return;
      this.mNotificationLedBlockedPackages.add(paramString);
    }
  }
  
  void setNotificationsEnabledForPackageImpl(String paramString, int paramInt, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Object localObject;
    if (paramBoolean)
    {
      localObject = "en";
      Slog.v("NotificationService", (String)localObject + "abling notifications for " + paramString);
      localObject = this.mAppOps;
      if (!paramBoolean) {
        break label81;
      }
    }
    label81:
    for (int i = 0;; i = 1)
    {
      ((AppOpsManager)localObject).setMode(11, paramInt, paramString, i);
      if (!paramBoolean) {
        break label87;
      }
      return;
      localObject = "dis";
      break;
    }
    label87:
    cancelAllNotificationsInt(MY_UID, MY_PID, paramString, 0, 0, true, UserHandle.getUserId(paramInt), 7, null);
  }
  
  void setSystemNotificationSound(String paramString)
  {
    this.mSystemNotificationSound = paramString;
  }
  
  void setSystemReady(boolean paramBoolean)
  {
    this.mSystemReady = paramBoolean;
  }
  
  void setVibrator(Vibrator paramVibrator)
  {
    this.mVibrator = paramVibrator;
  }
  
  void showNextToastLocked()
  {
    ToastRecord localToastRecord = (ToastRecord)this.mToastQueue.get(0);
    while (localToastRecord != null)
    {
      if (DBG) {
        Slog.d("NotificationService", "Show pkg=" + localToastRecord.pkg + " callback=" + localToastRecord.callback);
      }
      if ((OnePlusProcessManager.isSupportFrozenApp()) && (localToastRecord != null)) {
        OnePlusProcessManager.resumeProcessByUID_out(localToastRecord.uid, "showNextToastLocked");
      }
      try
      {
        localToastRecord.callback.show(localToastRecord.token);
        scheduleTimeoutLocked(localToastRecord);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("NotificationService", "Object died trying to show notification " + localToastRecord.callback + " in package " + localToastRecord.pkg);
        int i = this.mToastQueue.indexOf(localToastRecord);
        if (i >= 0) {
          this.mToastQueue.remove(i);
        }
        keepProcessAliveIfNeededLocked(localToastRecord.pid);
        if (this.mToastQueue.size() > 0) {
          localToastRecord = (ToastRecord)this.mToastQueue.get(0);
        } else {
          localToastRecord = null;
        }
      }
    }
  }
  
  void updateLightsLocked()
  {
    Object localObject1 = null;
    int i;
    label48:
    Object localObject2;
    int j;
    int k;
    label107:
    label113:
    Object localObject3;
    if ((localObject1 != null) || (this.mLights.isEmpty()))
    {
      if (this.mZenModeHelper.getZenMode() != 1) {
        break label440;
      }
      if (Settings.System.getInt(getContext().getContentResolver(), "oem_allow_led_light", 1) != 0) {
        break label435;
      }
      i = 1;
      localObject2 = getContext().registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
      j = 0;
      k = 0;
      if (localObject2 != null)
      {
        j = ((Intent)localObject2).getIntExtra("level", -1);
        k = ((Intent)localObject2).getIntExtra("plugged", 0);
        if (j > this.mDefLowBatteryWarningLevel) {
          break label445;
        }
        j = 1;
        if (k == 0) {
          break label450;
        }
        k = 1;
      }
      if ((localObject1 == null) || (this.mInCall) || (this.mScreenOn) || (i != 0) || ((j != 0) && (k == 0))) {
        break label455;
      }
      localObject2 = ((NotificationRecord)localObject1).sbn.getPackageName();
      localObject3 = ((NotificationRecord)localObject1).sbn.getNotification();
      i = ((Notification)localObject3).ledARGB;
      k = ((Notification)localObject3).ledOnMS;
      j = ((Notification)localObject3).ledOffMS;
      if ((((Notification)localObject3).defaults & 0x4) != 0)
      {
        i = this.mDefaultNotificationColor;
        k = this.mDefaultNotificationLedOn;
        j = this.mDefaultNotificationLedOff;
      }
      if ((this.mNotificationPulseEnabled) && (isNotificationLedEnabledImpl((String)localObject2))) {
        if (DEBUG_ONEPLUS)
        {
          localObject2 = new StringBuilder().append((String)localObject2).append(", ").append(((NotificationRecord)localObject1).sbn.getId()).append(", ");
          if ((((Notification)localObject3).defaults & 0x4) == 0) {
            break label479;
          }
        }
      }
    }
    label435:
    label440:
    label445:
    label450:
    label455:
    label479:
    for (localObject1 = "default";; localObject1 = "app defined")
    {
      Log.d("NotificationService", (String)localObject1 + " LED color " + Integer.toHexString(i) + " flashing");
      this.mNotificationLight.setFlashing(i, 1, k, j);
      if (this.mStatusBar != null) {
        this.mStatusBar.notificationLightPulse(i, k, j);
      }
      do
      {
        return;
        localObject3 = (String)this.mLights.get(this.mLights.size() - 1);
        localObject2 = (NotificationRecord)this.mNotificationsByKey.get(localObject3);
        localObject1 = localObject2;
        if (localObject2 != null) {
          break;
        }
        Slog.wtfStack("NotificationService", "LED Notification does not exist: " + (String)localObject3);
        this.mLights.remove(localObject3);
        localObject1 = localObject2;
        break;
        i = 0;
        break label48;
        i = 0;
        break label48;
        j = 0;
        break label107;
        k = 0;
        break label113;
        this.mNotificationLight.turnOff();
      } while (this.mStatusBar == null);
      this.mStatusBar.notificationLightOff();
      return;
    }
  }
  
  private static class Archive
  {
    final ArrayDeque<StatusBarNotification> mBuffer;
    final int mBufferSize;
    
    public Archive(int paramInt)
    {
      this.mBufferSize = paramInt;
      this.mBuffer = new ArrayDeque(this.mBufferSize);
    }
    
    public Iterator<StatusBarNotification> descendingIterator()
    {
      return this.mBuffer.descendingIterator();
    }
    
    public StatusBarNotification[] getArray(int paramInt)
    {
      int i = paramInt;
      if (paramInt == 0) {
        i = this.mBufferSize;
      }
      StatusBarNotification[] arrayOfStatusBarNotification = new StatusBarNotification[Math.min(i, this.mBuffer.size())];
      Iterator localIterator = descendingIterator();
      paramInt = 0;
      while ((localIterator.hasNext()) && (paramInt < i))
      {
        arrayOfStatusBarNotification[paramInt] = ((StatusBarNotification)localIterator.next());
        paramInt += 1;
      }
      return arrayOfStatusBarNotification;
    }
    
    public void record(StatusBarNotification paramStatusBarNotification)
    {
      if (this.mBuffer.size() == this.mBufferSize) {
        this.mBuffer.removeFirst();
      }
      this.mBuffer.addLast(paramStatusBarNotification.cloneLight());
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      int i = this.mBuffer.size();
      localStringBuilder.append("Archive (");
      localStringBuilder.append(i);
      localStringBuilder.append(" notification");
      if (i == 1) {}
      for (String str = ")";; str = "s)")
      {
        localStringBuilder.append(str);
        return localStringBuilder.toString();
      }
    }
  }
  
  public static final class DumpFilter
  {
    public boolean filtered = false;
    public String pkgFilter;
    public boolean redact = true;
    public long since;
    public boolean stats;
    public boolean zen;
    
    public static DumpFilter parseFromArguments(String[] paramArrayOfString)
    {
      DumpFilter localDumpFilter = new DumpFilter();
      int i = 0;
      if (i < paramArrayOfString.length)
      {
        String str = paramArrayOfString[i];
        int j;
        if (("--noredact".equals(str)) || ("--reveal".equals(str)))
        {
          localDumpFilter.redact = false;
          j = i;
        }
        for (;;)
        {
          i = j + 1;
          break;
          if (("p".equals(str)) || ("pkg".equals(str)) || ("--package".equals(str)))
          {
            j = i;
            if (i < paramArrayOfString.length - 1)
            {
              j = i + 1;
              localDumpFilter.pkgFilter = paramArrayOfString[j].trim().toLowerCase();
              if (localDumpFilter.pkgFilter.isEmpty()) {
                localDumpFilter.pkgFilter = null;
              } else {
                localDumpFilter.filtered = true;
              }
            }
          }
          else if (("--zen".equals(str)) || ("zen".equals(str)))
          {
            localDumpFilter.filtered = true;
            localDumpFilter.zen = true;
            j = i;
          }
          else
          {
            j = i;
            if ("--stats".equals(str))
            {
              localDumpFilter.stats = true;
              if (i < paramArrayOfString.length - 1)
              {
                j = i + 1;
                localDumpFilter.since = Long.valueOf(paramArrayOfString[j]).longValue();
              }
              else
              {
                localDumpFilter.since = 0L;
                j = i;
              }
            }
          }
        }
      }
      return localDumpFilter;
    }
    
    public boolean matches(ComponentName paramComponentName)
    {
      if (!this.filtered) {
        return true;
      }
      if (this.zen) {
        return true;
      }
      if (paramComponentName != null) {
        return matches(paramComponentName.getPackageName());
      }
      return false;
    }
    
    public boolean matches(StatusBarNotification paramStatusBarNotification)
    {
      if (!this.filtered) {
        return true;
      }
      if (this.zen) {}
      do
      {
        return true;
        if (paramStatusBarNotification == null) {
          break;
        }
      } while (matches(paramStatusBarNotification.getPackageName()));
      return matches(paramStatusBarNotification.getOpPkg());
      return false;
    }
    
    public boolean matches(String paramString)
    {
      if (!this.filtered) {
        return true;
      }
      if (this.zen) {
        return true;
      }
      if (paramString != null) {
        return paramString.toLowerCase().contains(this.pkgFilter);
      }
      return false;
    }
    
    public String toString()
    {
      if (this.stats) {
        return "stats";
      }
      if (this.zen) {
        return "zen";
      }
      return '\'' + this.pkgFilter + '\'';
    }
  }
  
  private class EnqueueNotificationRunnable
    implements Runnable
  {
    private final NotificationRecord r;
    private final int userId;
    
    EnqueueNotificationRunnable(int paramInt, NotificationRecord paramNotificationRecord)
    {
      this.userId = paramInt;
      this.r = paramNotificationRecord;
    }
    
    public void run()
    {
      StatusBarNotification localStatusBarNotification;
      Notification localNotification;
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        localStatusBarNotification = this.r.sbn;
        if (NotificationManagerService.DBG) {
          Slog.d("NotificationService", "EnqueueNotificationRunnable.run for: " + localStatusBarNotification.getKey());
        }
        NotificationRecord localNotificationRecord = (NotificationRecord)NotificationManagerService.this.mNotificationsByKey.get(localStatusBarNotification.getKey());
        if (localNotificationRecord != null) {
          this.r.copyRankingInformation(localNotificationRecord);
        }
        int j = localStatusBarNotification.getUid();
        int k = localStatusBarNotification.getInitialPid();
        localNotification = localStatusBarNotification.getNotification();
        String str1 = localStatusBarNotification.getPackageName();
        int m = localStatusBarNotification.getId();
        String str2 = localStatusBarNotification.getTag();
        if (!NotificationManagerService.-wrap3(j)) {}
        for (boolean bool1 = "android".equals(str1);; bool1 = true)
        {
          NotificationManagerService.-wrap22(NotificationManagerService.this, this.r, localNotificationRecord, j, k);
          if (!str1.equals("com.android.providers.downloads")) {
            break label695;
          }
          if (Log.isLoggable("DownloadManager", 2))
          {
            break label695;
            EventLogTags.writeNotificationEnqueue(j, k, str1, m, str2, this.userId, localNotification.toString(), i);
          }
          NotificationManagerService.-get21(NotificationManagerService.this).extractSignals(this.r);
          boolean bool2 = NotificationManagerService.-wrap2(NotificationManagerService.this, str1, j);
          if (((this.r.getImportance() != 0) && (NotificationManagerService.-wrap5(NotificationManagerService.this, str1, j)) && (!bool2)) || (bool1)) {
            break label333;
          }
          if (!bool2) {
            break;
          }
          Slog.e("NotificationService", "Suppressing notification from package due to package suspended by administrator.");
          NotificationManagerService.-get23(NotificationManagerService.this).registerSuspendedByAdmin(this.r);
          return;
        }
        Slog.e("NotificationService", "Suppressing notification from package by user request.");
        NotificationManagerService.-get23(NotificationManagerService.this).registerBlocked(this.r);
      }
      label333:
      if (NotificationManagerService.-get19(NotificationManagerService.this).isEnabled()) {
        NotificationManagerService.-get19(NotificationManagerService.this).onNotificationEnqueued(this.r);
      }
      int i = NotificationManagerService.this.indexOfNotificationLocked(localStatusBarNotification.getKey());
      if (i < 0)
      {
        NotificationManagerService.this.mNotificationList.add(this.r);
        NotificationManagerService.-get23(NotificationManagerService.this).registerPostedByApp(this.r);
        label406:
        NotificationManagerService.this.mNotificationsByKey.put(localStatusBarNotification.getKey(), this.r);
        if ((localNotification.flags & 0x40) != 0) {
          localNotification.flags |= 0x22;
        }
        NotificationManagerService.-wrap12(NotificationManagerService.this, this.r);
        NotificationManagerService.-get21(NotificationManagerService.this).sort(NotificationManagerService.this.mNotificationList);
        if (localNotification.getSmallIcon() == null) {
          break label608;
        }
        if (localObject1 == null) {
          break label707;
        }
      }
      label608:
      label695:
      label707:
      for (Object localObject2 = ((NotificationRecord)localObject1).sbn;; localObject2 = null)
      {
        NotificationManagerService.-get14(NotificationManagerService.this).notifyPostedLocked(localStatusBarNotification, (StatusBarNotification)localObject2);
        NotificationManagerService.this.buzzBeepBlinkLocked(this.r);
        return;
        localObject2 = (NotificationRecord)NotificationManagerService.this.mNotificationList.get(i);
        NotificationManagerService.this.mNotificationList.set(i, this.r);
        NotificationManagerService.-get23(NotificationManagerService.this).registerUpdatedByApp(this.r, (NotificationRecord)localObject2);
        localNotification.flags |= ((NotificationRecord)localObject2).getNotification().flags & 0x40;
        this.r.isUpdate = true;
        break label406;
        Slog.e("NotificationService", "Not posting notification without small icon: " + localNotification);
        if ((localObject2 == null) || (((NotificationRecord)localObject2).isCanceled)) {}
        for (;;)
        {
          Slog.e("NotificationService", "WARNING: In a future release this will crash the app: " + localStatusBarNotification.getPackageName());
          break;
          NotificationManagerService.-get14(NotificationManagerService.this).notifyRemovedLocked(localStatusBarNotification);
        }
        i = 0;
        if (localObject2 == null) {
          break;
        }
        i = 1;
        break;
      }
    }
  }
  
  public class NotificationListeners
    extends ManagedServices
  {
    private final ArraySet<ManagedServices.ManagedServiceInfo> mLightTrimListeners = new ArraySet();
    
    public NotificationListeners()
    {
      super(NotificationManagerService.-get11(NotificationManagerService.this), NotificationManagerService.this.mNotificationList, NotificationManagerService.-get24(NotificationManagerService.this));
    }
    
    private boolean isListenerPackage(String paramString)
    {
      if (paramString == null) {
        return false;
      }
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        Iterator localIterator = this.mServices.iterator();
        while (localIterator.hasNext())
        {
          boolean bool = paramString.equals(((ManagedServices.ManagedServiceInfo)localIterator.next()).component.getPackageName());
          if (bool) {
            return true;
          }
        }
        return false;
      }
    }
    
    private void notifyInterruptionFilterChanged(ManagedServices.ManagedServiceInfo paramManagedServiceInfo, int paramInt)
    {
      paramManagedServiceInfo = (INotificationListener)paramManagedServiceInfo.service;
      try
      {
        paramManagedServiceInfo.onInterruptionFilterChanged(paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e(this.TAG, "unable to notify listener (interruption filter): " + paramManagedServiceInfo, localRemoteException);
      }
    }
    
    private void notifyListenerHintsChanged(ManagedServices.ManagedServiceInfo paramManagedServiceInfo, int paramInt)
    {
      paramManagedServiceInfo = (INotificationListener)paramManagedServiceInfo.service;
      try
      {
        paramManagedServiceInfo.onListenerHintsChanged(paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e(this.TAG, "unable to notify listener (listener hints): " + paramManagedServiceInfo, localRemoteException);
      }
    }
    
    private void notifyPosted(ManagedServices.ManagedServiceInfo paramManagedServiceInfo, StatusBarNotification paramStatusBarNotification, NotificationRankingUpdate paramNotificationRankingUpdate)
    {
      paramManagedServiceInfo = (INotificationListener)paramManagedServiceInfo.service;
      paramStatusBarNotification = new NotificationManagerService.StatusBarNotificationHolder(paramStatusBarNotification);
      try
      {
        paramManagedServiceInfo.onNotificationPosted(paramStatusBarNotification, paramNotificationRankingUpdate);
        return;
      }
      catch (RemoteException paramStatusBarNotification)
      {
        Log.e(this.TAG, "unable to notify listener (posted): " + paramManagedServiceInfo, paramStatusBarNotification);
      }
    }
    
    private void notifyRankingUpdate(ManagedServices.ManagedServiceInfo paramManagedServiceInfo, NotificationRankingUpdate paramNotificationRankingUpdate)
    {
      paramManagedServiceInfo = (INotificationListener)paramManagedServiceInfo.service;
      try
      {
        paramManagedServiceInfo.onNotificationRankingUpdate(paramNotificationRankingUpdate);
        return;
      }
      catch (RemoteException paramNotificationRankingUpdate)
      {
        Log.e(this.TAG, "unable to notify listener (ranking update): " + paramManagedServiceInfo, paramNotificationRankingUpdate);
      }
    }
    
    private void notifyRemoved(ManagedServices.ManagedServiceInfo paramManagedServiceInfo, StatusBarNotification paramStatusBarNotification, NotificationRankingUpdate paramNotificationRankingUpdate)
    {
      if (!paramManagedServiceInfo.enabledAndUserMatches(paramStatusBarNotification.getUserId())) {
        return;
      }
      paramManagedServiceInfo = (INotificationListener)paramManagedServiceInfo.service;
      paramStatusBarNotification = new NotificationManagerService.StatusBarNotificationHolder(paramStatusBarNotification);
      try
      {
        paramManagedServiceInfo.onNotificationRemoved(paramStatusBarNotification, paramNotificationRankingUpdate);
        return;
      }
      catch (RemoteException paramStatusBarNotification)
      {
        Log.e(this.TAG, "unable to notify listener (removed): " + paramManagedServiceInfo, paramStatusBarNotification);
      }
    }
    
    protected IInterface asInterface(IBinder paramIBinder)
    {
      return INotificationListener.Stub.asInterface(paramIBinder);
    }
    
    protected boolean checkType(IInterface paramIInterface)
    {
      return paramIInterface instanceof INotificationListener;
    }
    
    protected ManagedServices.Config getConfig()
    {
      ManagedServices.Config localConfig = new ManagedServices.Config();
      localConfig.caption = "notification listener";
      localConfig.serviceInterface = "android.service.notification.NotificationListenerService";
      localConfig.secureSettingName = "enabled_notification_listeners";
      localConfig.bindPermission = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE";
      localConfig.settingsAction = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
      localConfig.clientLabel = 17040517;
      return localConfig;
    }
    
    public int getOnNotificationPostedTrim(ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
    {
      if (this.mLightTrimListeners.contains(paramManagedServiceInfo)) {
        return 1;
      }
      return 0;
    }
    
    public void notifyInterruptionFilterChanged(final int paramInt)
    {
      Iterator localIterator = this.mServices.iterator();
      while (localIterator.hasNext())
      {
        final ManagedServices.ManagedServiceInfo localManagedServiceInfo = (ManagedServices.ManagedServiceInfo)localIterator.next();
        if (localManagedServiceInfo.isEnabledForCurrentProfiles()) {
          NotificationManagerService.-get11(NotificationManagerService.this).post(new Runnable()
          {
            public void run()
            {
              NotificationManagerService.NotificationListeners.-wrap1(NotificationManagerService.NotificationListeners.this, localManagedServiceInfo, paramInt);
            }
          });
        }
      }
    }
    
    public void notifyListenerHintsChangedLocked(final int paramInt)
    {
      Iterator localIterator = this.mServices.iterator();
      while (localIterator.hasNext())
      {
        final ManagedServices.ManagedServiceInfo localManagedServiceInfo = (ManagedServices.ManagedServiceInfo)localIterator.next();
        if (localManagedServiceInfo.isEnabledForCurrentProfiles()) {
          NotificationManagerService.-get11(NotificationManagerService.this).post(new Runnable()
          {
            public void run()
            {
              NotificationManagerService.NotificationListeners.-wrap2(NotificationManagerService.NotificationListeners.this, localManagedServiceInfo, paramInt);
            }
          });
        }
      }
    }
    
    public void notifyPostedLocked(StatusBarNotification paramStatusBarNotification1, StatusBarNotification paramStatusBarNotification2)
    {
      NotificationManagerService.TrimCache localTrimCache = new NotificationManagerService.TrimCache(NotificationManagerService.this, paramStatusBarNotification1);
      Iterator localIterator = this.mServices.iterator();
      while (localIterator.hasNext())
      {
        final ManagedServices.ManagedServiceInfo localManagedServiceInfo = (ManagedServices.ManagedServiceInfo)localIterator.next();
        boolean bool2 = NotificationManagerService.-wrap4(NotificationManagerService.this, paramStatusBarNotification1, localManagedServiceInfo);
        if (paramStatusBarNotification2 != null) {}
        final NotificationRankingUpdate localNotificationRankingUpdate;
        for (boolean bool1 = NotificationManagerService.-wrap4(NotificationManagerService.this, paramStatusBarNotification2, localManagedServiceInfo);; bool1 = false)
        {
          if ((!bool1) && (!bool2)) {
            break label143;
          }
          localNotificationRankingUpdate = NotificationManagerService.-wrap0(NotificationManagerService.this, localManagedServiceInfo);
          if ((bool1) && (!bool2)) {
            break label145;
          }
          localStatusBarNotification = localTrimCache.ForListener(localManagedServiceInfo);
          NotificationManagerService.-get11(NotificationManagerService.this).post(new Runnable()
          {
            public void run()
            {
              NotificationManagerService.NotificationListeners.-wrap3(NotificationManagerService.NotificationListeners.this, localManagedServiceInfo, localStatusBarNotification, localNotificationRankingUpdate);
            }
          });
          break;
        }
        label143:
        continue;
        label145:
        final StatusBarNotification localStatusBarNotification = paramStatusBarNotification2.cloneLight();
        NotificationManagerService.-get11(NotificationManagerService.this).post(new Runnable()
        {
          public void run()
          {
            NotificationManagerService.NotificationListeners.-wrap5(NotificationManagerService.NotificationListeners.this, localManagedServiceInfo, localStatusBarNotification, localNotificationRankingUpdate);
          }
        });
      }
    }
    
    public void notifyRankingUpdateLocked()
    {
      Iterator localIterator = this.mServices.iterator();
      while (localIterator.hasNext())
      {
        final ManagedServices.ManagedServiceInfo localManagedServiceInfo = (ManagedServices.ManagedServiceInfo)localIterator.next();
        if (localManagedServiceInfo.isEnabledForCurrentProfiles())
        {
          final NotificationRankingUpdate localNotificationRankingUpdate = NotificationManagerService.-wrap0(NotificationManagerService.this, localManagedServiceInfo);
          NotificationManagerService.-get11(NotificationManagerService.this).post(new Runnable()
          {
            public void run()
            {
              NotificationManagerService.NotificationListeners.-wrap4(NotificationManagerService.NotificationListeners.this, localManagedServiceInfo, localNotificationRankingUpdate);
            }
          });
        }
      }
    }
    
    public void notifyRemovedLocked(StatusBarNotification paramStatusBarNotification)
    {
      final StatusBarNotification localStatusBarNotification = paramStatusBarNotification.cloneLight();
      Iterator localIterator = this.mServices.iterator();
      while (localIterator.hasNext())
      {
        final ManagedServices.ManagedServiceInfo localManagedServiceInfo = (ManagedServices.ManagedServiceInfo)localIterator.next();
        if (NotificationManagerService.-wrap4(NotificationManagerService.this, paramStatusBarNotification, localManagedServiceInfo))
        {
          final NotificationRankingUpdate localNotificationRankingUpdate = NotificationManagerService.-wrap0(NotificationManagerService.this, localManagedServiceInfo);
          NotificationManagerService.-get11(NotificationManagerService.this).post(new Runnable()
          {
            public void run()
            {
              NotificationManagerService.NotificationListeners.-wrap5(NotificationManagerService.NotificationListeners.this, localManagedServiceInfo, localStatusBarNotification, localNotificationRankingUpdate);
            }
          });
        }
      }
    }
    
    public void onServiceAdded(ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
    {
      INotificationListener localINotificationListener = (INotificationListener)paramManagedServiceInfo.service;
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        paramManagedServiceInfo = NotificationManagerService.-wrap0(NotificationManagerService.this, paramManagedServiceInfo);
      }
    }
    
    protected void onServiceRemovedLocked(ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
    {
      if (NotificationManagerService.-wrap6(NotificationManagerService.this, paramManagedServiceInfo))
      {
        NotificationManagerService.-wrap36(NotificationManagerService.this);
        NotificationManagerService.-wrap34(NotificationManagerService.this);
      }
      this.mLightTrimListeners.remove(paramManagedServiceInfo);
    }
    
    public void setOnNotificationPostedTrimLocked(ManagedServices.ManagedServiceInfo paramManagedServiceInfo, int paramInt)
    {
      if (paramInt == 1)
      {
        this.mLightTrimListeners.add(paramManagedServiceInfo);
        return;
      }
      this.mLightTrimListeners.remove(paramManagedServiceInfo);
    }
  }
  
  public class NotificationRankers
    extends ManagedServices
  {
    public NotificationRankers()
    {
      super(NotificationManagerService.-get11(NotificationManagerService.this), NotificationManagerService.this.mNotificationList, NotificationManagerService.-get24(NotificationManagerService.this));
    }
    
    private void notifyEnqueued(ManagedServices.ManagedServiceInfo paramManagedServiceInfo, StatusBarNotification paramStatusBarNotification, int paramInt, boolean paramBoolean)
    {
      paramManagedServiceInfo = (INotificationListener)paramManagedServiceInfo.service;
      paramStatusBarNotification = new NotificationManagerService.StatusBarNotificationHolder(paramStatusBarNotification);
      try
      {
        paramManagedServiceInfo.onNotificationEnqueued(paramStatusBarNotification, paramInt, paramBoolean);
        return;
      }
      catch (RemoteException paramStatusBarNotification)
      {
        Log.e(this.TAG, "unable to notify ranker (enqueued): " + paramManagedServiceInfo, paramStatusBarNotification);
      }
    }
    
    protected IInterface asInterface(IBinder paramIBinder)
    {
      return INotificationListener.Stub.asInterface(paramIBinder);
    }
    
    protected boolean checkType(IInterface paramIInterface)
    {
      return paramIInterface instanceof INotificationListener;
    }
    
    protected ManagedServices.Config getConfig()
    {
      ManagedServices.Config localConfig = new ManagedServices.Config();
      localConfig.caption = "notification ranker service";
      localConfig.serviceInterface = "android.service.notification.NotificationRankerService";
      localConfig.secureSettingName = null;
      localConfig.bindPermission = "android.permission.BIND_NOTIFICATION_RANKER_SERVICE";
      localConfig.settingsAction = "android.settings.MANAGE_DEFAULT_APPS_SETTINGS";
      localConfig.clientLabel = 17040520;
      return localConfig;
    }
    
    public boolean isEnabled()
    {
      return !this.mServices.isEmpty();
    }
    
    public void onNotificationEnqueued(NotificationRecord paramNotificationRecord)
    {
      StatusBarNotification localStatusBarNotification1 = paramNotificationRecord.sbn;
      NotificationManagerService.TrimCache localTrimCache = new NotificationManagerService.TrimCache(NotificationManagerService.this, localStatusBarNotification1);
      Iterator localIterator = this.mServices.iterator();
      while (localIterator.hasNext())
      {
        final ManagedServices.ManagedServiceInfo localManagedServiceInfo = (ManagedServices.ManagedServiceInfo)localIterator.next();
        if (NotificationManagerService.-wrap4(NotificationManagerService.this, localStatusBarNotification1, localManagedServiceInfo))
        {
          final int i = paramNotificationRecord.getImportance();
          final boolean bool = paramNotificationRecord.isImportanceFromUser();
          final StatusBarNotification localStatusBarNotification2 = localTrimCache.ForListener(localManagedServiceInfo);
          NotificationManagerService.-get11(NotificationManagerService.this).post(new Runnable()
          {
            public void run()
            {
              NotificationManagerService.NotificationRankers.-wrap0(NotificationManagerService.NotificationRankers.this, localManagedServiceInfo, localStatusBarNotification2, i, bool);
            }
          });
        }
      }
    }
    
    public void onPackagesChanged(boolean paramBoolean, String[] paramArrayOfString)
    {
      Object localObject = null;
      String str;
      StringBuilder localStringBuilder;
      if (this.DEBUG)
      {
        str = this.TAG;
        localStringBuilder = new StringBuilder().append("onPackagesChanged removingPackage=").append(paramBoolean).append(" pkgList=");
        if (paramArrayOfString != null) {
          break label70;
        }
      }
      for (;;)
      {
        Slog.d(str, localObject);
        if (NotificationManagerService.-get18(NotificationManagerService.this) != null) {
          break;
        }
        return;
        label70:
        localObject = Arrays.asList(paramArrayOfString);
      }
      if ((paramArrayOfString == null) || (paramArrayOfString.length <= 0) || (paramBoolean)) {}
      for (;;)
      {
        return;
        int j = paramArrayOfString.length;
        int i = 0;
        while (i < j)
        {
          localObject = paramArrayOfString[i];
          if (NotificationManagerService.-get18(NotificationManagerService.this).equals(localObject)) {
            registerRanker();
          }
          i += 1;
        }
      }
    }
    
    protected void onServiceAdded(ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
    {
      NotificationManagerService.-get14(NotificationManagerService.this).registerGuestService(paramManagedServiceInfo);
    }
    
    protected void onServiceRemovedLocked(ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
    {
      NotificationManagerService.-get14(NotificationManagerService.this).unregisterService(paramManagedServiceInfo.service, paramManagedServiceInfo.userid);
    }
    
    public void onUserSwitched(int paramInt)
    {
      synchronized (NotificationManagerService.this.mNotificationList)
      {
        int i;
        for (paramInt = this.mServices.size() - 1;; paramInt = i)
        {
          i = paramInt - 1;
          if (paramInt <= 0) {
            break;
          }
          ManagedServices.ManagedServiceInfo localManagedServiceInfo = (ManagedServices.ManagedServiceInfo)this.mServices.get(i);
          unregisterService(localManagedServiceInfo.service, localManagedServiceInfo.userid);
        }
        registerRanker();
        return;
      }
    }
    
    protected void registerRanker()
    {
      if (NotificationManagerService.-get18(NotificationManagerService.this) == null)
      {
        Slog.w(this.TAG, "could not start ranker service: no package specified!");
        return;
      }
      Set localSet = queryPackageForServices(NotificationManagerService.-get18(NotificationManagerService.this), 0);
      Iterator localIterator = localSet.iterator();
      if (localIterator.hasNext())
      {
        ComponentName localComponentName = (ComponentName)localIterator.next();
        if (localIterator.hasNext())
        {
          Slog.e(this.TAG, "found multiple ranker services:" + localSet);
          return;
        }
        registerSystemService(localComponentName, 0);
        return;
      }
      Slog.w(this.TAG, "could not start ranker service: none found");
    }
  }
  
  private final class PolicyAccess
  {
    private static final String SEPARATOR = ":";
    private final String[] PERM = { "android.permission.ACCESS_NOTIFICATION_POLICY" };
    
    private PolicyAccess() {}
    
    public ArraySet<String> getGrantedPackages()
    {
      ArraySet localArraySet = new ArraySet();
      long l = Binder.clearCallingIdentity();
      for (;;)
      {
        int i;
        try
        {
          Object localObject1 = Settings.Secure.getStringForUser(NotificationManagerService.this.getContext().getContentResolver(), "enabled_notification_policy_access_packages", ActivityManager.getCurrentUser());
          if (localObject1 != null)
          {
            String[] arrayOfString = ((String)localObject1).split(":");
            i = 0;
            if (i < arrayOfString.length)
            {
              String str = arrayOfString[i];
              localObject1 = str;
              if (str != null) {
                localObject1 = str.trim();
              }
              if (TextUtils.isEmpty((CharSequence)localObject1)) {
                break label116;
              }
              localArraySet.add(localObject1);
            }
          }
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        Binder.restoreCallingIdentity(l);
        return localArraySet;
        label116:
        i += 1;
      }
    }
    
    public String[] getRequestingPackages()
      throws RemoteException
    {
      List localList = AppGlobals.getPackageManager().getPackagesHoldingPermissions(this.PERM, 0, ActivityManager.getCurrentUser()).getList();
      if ((localList == null) || (localList.isEmpty())) {
        return new String[0];
      }
      int j = localList.size();
      String[] arrayOfString = new String[j];
      int i = 0;
      while (i < j)
      {
        arrayOfString[i] = ((PackageInfo)localList.get(i)).packageName;
        i += 1;
      }
      return arrayOfString;
    }
    
    public boolean isPackageGranted(String paramString)
    {
      if (paramString != null) {
        return getGrantedPackages().contains(paramString);
      }
      return false;
    }
    
    public void put(String paramString, boolean paramBoolean)
    {
      if (paramString == null) {
        return;
      }
      Object localObject = getGrantedPackages();
      if (paramBoolean) {}
      for (paramBoolean = ((ArraySet)localObject).add(paramString); !paramBoolean; paramBoolean = ((ArraySet)localObject).remove(paramString)) {
        return;
      }
      localObject = TextUtils.join(":", (Iterable)localObject);
      int i = ActivityManager.getCurrentUser();
      Settings.Secure.putStringForUser(NotificationManagerService.this.getContext().getContentResolver(), "enabled_notification_policy_access_packages", (String)localObject, i);
      NotificationManagerService.this.getContext().sendBroadcastAsUser(new Intent("android.app.action.NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED").setPackage(paramString).addFlags(1073741824), new UserHandle(i), null);
    }
  }
  
  private final class RankingHandlerWorker
    extends Handler
    implements RankingHandler
  {
    public RankingHandlerWorker(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      do
      {
        return;
        NotificationManagerService.-wrap25(NotificationManagerService.this, paramMessage);
        return;
        NotificationManagerService.-wrap26(NotificationManagerService.this);
      } while (!NotificationManagerService.-get21(NotificationManagerService.this).correctImportance());
      NotificationManagerService.this.savePolicyFile();
    }
    
    public void requestReconsideration(RankingReconsideration paramRankingReconsideration)
    {
      sendMessageDelayed(Message.obtain(this, 1000, paramRankingReconsideration), paramRankingReconsideration.getDelay(TimeUnit.MILLISECONDS));
    }
    
    public void requestSort()
    {
      removeMessages(1001);
      sendEmptyMessage(1001);
    }
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    private final Uri NOTIFICATION_LIGHT_PULSE_COLOR_URI = Settings.System.getUriFor("notification_light_pulse_color");
    private final Uri NOTIFICATION_LIGHT_PULSE_URI = Settings.System.getUriFor("notification_light_pulse");
    private final Uri NOTIFICATION_RATE_LIMIT_URI = Settings.Global.getUriFor("max_notification_enqueue_rate");
    private final Uri NOTIFICATION_SOUND_URI = Settings.System.getUriFor("notification_sound");
    private final Uri NOTIFICATION_VIBRATE_INTENSITY = Settings.System.getUriFor("notice_vibrate_intensity");
    private final Uri NOTIFICATION_VIBRATE_WHEN_MUTE = Settings.System.getUriFor("oem_vibrate_under_silent");
    private final String VIBRATE_INTENSITY = "notice_vibrate_intensity";
    
    SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    void observe()
    {
      ContentResolver localContentResolver = NotificationManagerService.this.getContext().getContentResolver();
      localContentResolver.registerContentObserver(this.NOTIFICATION_LIGHT_PULSE_URI, false, this, -1);
      localContentResolver.registerContentObserver(this.NOTIFICATION_SOUND_URI, false, this, -1);
      localContentResolver.registerContentObserver(this.NOTIFICATION_RATE_LIMIT_URI, false, this, -1);
      localContentResolver.registerContentObserver(this.NOTIFICATION_LIGHT_PULSE_COLOR_URI, false, this, -1);
      localContentResolver.registerContentObserver(this.NOTIFICATION_VIBRATE_WHEN_MUTE, false, this, -1);
      localContentResolver.registerContentObserver(this.NOTIFICATION_VIBRATE_INTENSITY, false, this, -1);
      update(null);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      update(paramUri);
    }
    
    public void update(Uri paramUri)
    {
      ContentResolver localContentResolver = NotificationManagerService.this.getContext().getContentResolver();
      if ((paramUri == null) || (this.NOTIFICATION_LIGHT_PULSE_URI.equals(paramUri))) {
        if (Settings.System.getIntForUser(localContentResolver, "notification_light_pulse", 0, -2) == 0) {
          break label242;
        }
      }
      label242:
      for (boolean bool = true;; bool = false)
      {
        if (NotificationManagerService.-get17(NotificationManagerService.this) != bool)
        {
          NotificationManagerService.-set6(NotificationManagerService.this, bool);
          NotificationManagerService.-wrap37(NotificationManagerService.this);
        }
        if ((paramUri == null) || (this.NOTIFICATION_RATE_LIMIT_URI.equals(paramUri))) {
          NotificationManagerService.-set5(NotificationManagerService.this, Settings.Global.getFloat(localContentResolver, "max_notification_enqueue_rate", NotificationManagerService.-get15(NotificationManagerService.this)));
        }
        if ((paramUri == null) || (this.NOTIFICATION_SOUND_URI.equals(paramUri))) {
          NotificationManagerService.-set8(NotificationManagerService.this, Settings.System.getString(localContentResolver, "notification_sound"));
        }
        if ((paramUri == null) || (this.NOTIFICATION_LIGHT_PULSE_COLOR_URI.equals(paramUri)))
        {
          NotificationManagerService.-set2(NotificationManagerService.this, Settings.System.getIntForUser(localContentResolver, "notification_light_pulse_color", NotificationManagerService.-get9(NotificationManagerService.this), -2));
          NotificationManagerService.-wrap37(NotificationManagerService.this);
        }
        if ((paramUri == null) || (this.NOTIFICATION_VIBRATE_WHEN_MUTE.equals(paramUri))) {
          NotificationManagerService.-set10(NotificationManagerService.this, Settings.System.getIntForUser(localContentResolver, "oem_vibrate_under_silent", 1, -2));
        }
        if ((paramUri == null) || (this.NOTIFICATION_VIBRATE_INTENSITY.equals(paramUri))) {
          NotificationManagerService.-set9(NotificationManagerService.this, Settings.System.getIntForUser(localContentResolver, "notice_vibrate_intensity", 1, -2));
        }
        return;
      }
    }
  }
  
  private static final class StatusBarNotificationHolder
    extends IStatusBarNotificationHolder.Stub
  {
    private StatusBarNotification mValue;
    
    public StatusBarNotificationHolder(StatusBarNotification paramStatusBarNotification)
    {
      this.mValue = paramStatusBarNotification;
    }
    
    public StatusBarNotification get()
    {
      StatusBarNotification localStatusBarNotification = this.mValue;
      this.mValue = null;
      return localStatusBarNotification;
    }
  }
  
  private static final class ToastRecord
  {
    final ITransientNotification callback;
    int duration;
    final int pid;
    final String pkg;
    Binder token;
    final int uid;
    
    ToastRecord(int paramInt1, String paramString, ITransientNotification paramITransientNotification, int paramInt2, Binder paramBinder, int paramInt3)
    {
      this.pid = paramInt1;
      this.uid = paramInt3;
      this.pkg = paramString;
      this.callback = paramITransientNotification;
      this.duration = paramInt2;
      this.token = paramBinder;
    }
    
    void dump(PrintWriter paramPrintWriter, String paramString, NotificationManagerService.DumpFilter paramDumpFilter)
    {
      if ((paramDumpFilter == null) || (paramDumpFilter.matches(this.pkg)))
      {
        paramPrintWriter.println(paramString + this);
        return;
      }
    }
    
    public final String toString()
    {
      return "ToastRecord{" + Integer.toHexString(System.identityHashCode(this)) + " pkg=" + this.pkg + " callback=" + this.callback + " duration=" + this.duration;
    }
    
    void update(int paramInt)
    {
      this.duration = paramInt;
    }
  }
  
  private class TrimCache
  {
    StatusBarNotification heavy;
    StatusBarNotification sbnClone;
    StatusBarNotification sbnCloneLight;
    
    TrimCache(StatusBarNotification paramStatusBarNotification)
    {
      this.heavy = paramStatusBarNotification;
    }
    
    StatusBarNotification ForListener(ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
    {
      if (NotificationManagerService.-get14(NotificationManagerService.this).getOnNotificationPostedTrim(paramManagedServiceInfo) == 1)
      {
        if (this.sbnCloneLight == null) {
          this.sbnCloneLight = this.heavy.cloneLight();
        }
        return this.sbnCloneLight;
      }
      if (this.sbnClone == null) {
        this.sbnClone = this.heavy.clone();
      }
      return this.sbnClone;
    }
  }
  
  private final class WorkerHandler
    extends Handler
  {
    private WorkerHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 2: 
        NotificationManagerService.-wrap30(NotificationManagerService.this, (NotificationManagerService.ToastRecord)paramMessage.obj);
        return;
      case 3: 
        NotificationManagerService.-wrap28(NotificationManagerService.this);
        return;
      case 4: 
        NotificationManagerService.-wrap29(NotificationManagerService.this);
        return;
      case 5: 
        NotificationManagerService.-wrap23(NotificationManagerService.this, paramMessage.arg1);
        return;
      case 6: 
        NotificationManagerService.-wrap24(NotificationManagerService.this, paramMessage.arg1);
        return;
      }
      NotificationManagerService.-wrap27(NotificationManagerService.this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/NotificationManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */