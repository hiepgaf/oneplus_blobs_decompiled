package com.android.server.notification;

import android.app.AppOpsManager;
import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.app.NotificationManager.Policy;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioManagerInternal;
import android.media.AudioManagerInternal.RingerModeDelegate;
import android.media.VolumePolicy;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.EventInfo;
import android.service.notification.ZenModeConfig.Migration;
import android.service.notification.ZenModeConfig.ScheduleInfo;
import android.service.notification.ZenModeConfig.XmlV1;
import android.service.notification.ZenModeConfig.ZenRule;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.logging.MetricsLogger;
import com.android.server.LocalServices;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class ZenModeHelper
{
  static final boolean DEBUG = Log.isLoggable("ZenModeHelper", 3);
  private static final int RULE_INSTANCE_GRACE_PERIOD = 259200000;
  public static final long SUPPRESSED_EFFECT_ALL = 3L;
  public static final long SUPPRESSED_EFFECT_CALLS = 2L;
  public static final long SUPPRESSED_EFFECT_NOTIFICATIONS = 1L;
  static final String TAG = "ZenModeHelper";
  private final AppOpsManager mAppOps;
  private AudioManagerInternal mAudioManager;
  private final ArrayList<Callback> mCallbacks = new ArrayList();
  private final ZenModeConditions mConditions;
  private ZenModeConfig mConfig;
  private final ZenModeConfig.Migration mConfigMigration = new ZenModeConfig.Migration()
  {
    public ZenModeConfig migrate(ZenModeConfig.XmlV1 paramAnonymousXmlV1)
    {
      int i = 1;
      if (paramAnonymousXmlV1 == null) {
        return null;
      }
      ZenModeConfig localZenModeConfig = new ZenModeConfig();
      localZenModeConfig.allowCalls = paramAnonymousXmlV1.allowCalls;
      localZenModeConfig.allowEvents = paramAnonymousXmlV1.allowEvents;
      localZenModeConfig.allowCallsFrom = paramAnonymousXmlV1.allowFrom;
      localZenModeConfig.allowMessages = paramAnonymousXmlV1.allowMessages;
      localZenModeConfig.allowMessagesFrom = paramAnonymousXmlV1.allowFrom;
      localZenModeConfig.allowReminders = paramAnonymousXmlV1.allowReminders;
      Object localObject = ZenModeConfig.XmlV1.tryParseDays(paramAnonymousXmlV1.sleepMode);
      if ((localObject != null) && (localObject.length > 0))
      {
        Log.i("ZenModeHelper", "Migrating existing V1 downtime to single schedule");
        ZenModeConfig.ScheduleInfo localScheduleInfo = new ZenModeConfig.ScheduleInfo();
        localScheduleInfo.days = ((int[])localObject);
        localScheduleInfo.startHour = paramAnonymousXmlV1.sleepStartHour;
        localScheduleInfo.startMinute = paramAnonymousXmlV1.sleepStartMinute;
        localScheduleInfo.endHour = paramAnonymousXmlV1.sleepEndHour;
        localScheduleInfo.endMinute = paramAnonymousXmlV1.sleepEndMinute;
        localObject = new ZenModeConfig.ZenRule();
        ((ZenModeConfig.ZenRule)localObject).enabled = true;
        ((ZenModeConfig.ZenRule)localObject).name = ZenModeHelper.-get0(ZenModeHelper.this).getResources().getString(17040856);
        ((ZenModeConfig.ZenRule)localObject).conditionId = ZenModeConfig.toScheduleConditionId(localScheduleInfo);
        if (paramAnonymousXmlV1.sleepNone) {
          i = 2;
        }
        ((ZenModeConfig.ZenRule)localObject).zenMode = i;
        ((ZenModeConfig.ZenRule)localObject).component = ScheduleConditionProvider.COMPONENT;
        localZenModeConfig.automaticRules.put(ZenModeConfig.newRuleId(), localObject);
      }
      for (;;)
      {
        ZenModeHelper.-wrap1(ZenModeHelper.this, localZenModeConfig);
        return localZenModeConfig;
        Log.i("ZenModeHelper", "No existing V1 downtime found, generating default schedules");
        ZenModeHelper.-wrap2(ZenModeHelper.this, localZenModeConfig);
      }
    }
  };
  private final SparseArray<ZenModeConfig> mConfigs = new SparseArray();
  private final Context mContext;
  private final ZenModeConfig mDefaultConfig;
  private final ZenModeFiltering mFiltering;
  private final H mHandler;
  private final Metrics mMetrics = new Metrics(null);
  private PackageManager mPm;
  private final RingerModeDelegate mRingerModeDelegate = new RingerModeDelegate(null);
  private final ManagedServices.Config mServiceConfig;
  private final SettingsObserver mSettingsObserver;
  private long mSuppressedEffects;
  private int mUser = 0;
  private boolean mVibrateFlag = false;
  private int mZenMode;
  
  public ZenModeHelper(Context paramContext, Looper paramLooper, ConditionProviders paramConditionProviders)
  {
    this.mContext = paramContext;
    this.mHandler = new H(paramLooper, null);
    addCallback(this.mMetrics);
    this.mAppOps = ((AppOpsManager)paramContext.getSystemService("appops"));
    this.mDefaultConfig = readDefaultConfig(paramContext.getResources());
    appendDefaultScheduleRules(this.mDefaultConfig);
    appendDefaultEventRules(this.mDefaultConfig);
    this.mConfig = this.mDefaultConfig;
    this.mConfigs.put(0, this.mConfig);
    this.mSettingsObserver = new SettingsObserver(this.mHandler);
    this.mSettingsObserver.observe();
    this.mFiltering = new ZenModeFiltering(this.mContext);
    this.mConditions = new ZenModeConditions(this, paramConditionProviders);
    this.mServiceConfig = paramConditionProviders.getConfig();
    if (Settings.System.getInt(this.mContext.getContentResolver(), "oem_vibrate_under_silent", 0) == 1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mVibrateFlag = bool;
      return;
    }
  }
  
  private void appendDefaultEventRules(ZenModeConfig paramZenModeConfig)
  {
    if (paramZenModeConfig == null) {
      return;
    }
    ZenModeConfig.EventInfo localEventInfo = new ZenModeConfig.EventInfo();
    localEventInfo.calendar = null;
    localEventInfo.reply = 1;
    ZenModeConfig.ZenRule localZenRule = new ZenModeConfig.ZenRule();
    localZenRule.enabled = false;
    localZenRule.name = this.mContext.getResources().getString(17040859);
    localZenRule.conditionId = ZenModeConfig.toEventConditionId(localEventInfo);
    localZenRule.zenMode = 3;
    localZenRule.component = EventConditionProvider.COMPONENT;
    localZenRule.id = ZenModeConfig.newRuleId();
    localZenRule.creationTime = System.currentTimeMillis();
    paramZenModeConfig.automaticRules.put(localZenRule.id, localZenRule);
  }
  
  private void appendDefaultScheduleRules(ZenModeConfig paramZenModeConfig)
  {
    if (paramZenModeConfig == null) {
      return;
    }
    ZenModeConfig.ScheduleInfo localScheduleInfo = new ZenModeConfig.ScheduleInfo();
    localScheduleInfo.days = ZenModeConfig.WEEKNIGHT_DAYS;
    localScheduleInfo.startHour = 22;
    localScheduleInfo.endHour = 7;
    ZenModeConfig.ZenRule localZenRule = new ZenModeConfig.ZenRule();
    localZenRule.enabled = false;
    localZenRule.name = this.mContext.getResources().getString(17040857);
    localZenRule.conditionId = ZenModeConfig.toScheduleConditionId(localScheduleInfo);
    localZenRule.zenMode = 3;
    localZenRule.component = ScheduleConditionProvider.COMPONENT;
    localZenRule.id = ZenModeConfig.newRuleId();
    localZenRule.creationTime = System.currentTimeMillis();
    paramZenModeConfig.automaticRules.put(localZenRule.id, localZenRule);
    localScheduleInfo = new ZenModeConfig.ScheduleInfo();
    localScheduleInfo.days = ZenModeConfig.WEEKEND_DAYS;
    localScheduleInfo.startHour = 23;
    localScheduleInfo.startMinute = 30;
    localScheduleInfo.endHour = 10;
    localZenRule = new ZenModeConfig.ZenRule();
    localZenRule.enabled = false;
    localZenRule.name = this.mContext.getResources().getString(17040858);
    localZenRule.conditionId = ZenModeConfig.toScheduleConditionId(localScheduleInfo);
    localZenRule.zenMode = 3;
    localZenRule.component = ScheduleConditionProvider.COMPONENT;
    localZenRule.id = ZenModeConfig.newRuleId();
    localZenRule.creationTime = System.currentTimeMillis();
    paramZenModeConfig.automaticRules.put(localZenRule.id, localZenRule);
  }
  
  private void applyConfig(ZenModeConfig paramZenModeConfig, String paramString, boolean paramBoolean)
  {
    String str = Integer.toString(paramZenModeConfig.hashCode());
    Settings.Global.putString(this.mContext.getContentResolver(), "zen_mode_config_etag", str);
    if (!evaluateZenMode(paramString, paramBoolean)) {
      applyRestrictions();
    }
    this.mConditions.evaluateConfig(paramZenModeConfig, true);
  }
  
  private void applyOnePlusZenToRingerMode(int paramInt)
  {
    if (this.mAudioManager == null)
    {
      Log.e("ZenModeHelper", "[applyOnePlusZenToRingerMode] audio manager is not initialized");
      return;
    }
    int j = paramInt;
    int i;
    switch (this.mZenMode)
    {
    default: 
      i = j;
    }
    for (;;)
    {
      if (i != -1) {
        this.mAudioManager.setRingerModeInternal(i, "ZenModeHelper");
      }
      return;
      i = j;
      if (this.mVibrateFlag)
      {
        i = j;
        if (paramInt != 1)
        {
          setPreviousRingerModeSetting(Integer.valueOf(paramInt));
          i = 1;
          continue;
          i = j;
          if (this.mVibrateFlag)
          {
            i = j;
            if (paramInt != 0)
            {
              i = getPreviousRingerModeSetting();
              setPreviousRingerModeSetting(null);
            }
          }
        }
      }
    }
  }
  
  private void applyRestrictions()
  {
    int j;
    int k;
    label19:
    int i;
    label36:
    label54:
    label69:
    boolean bool1;
    label80:
    label92:
    int n;
    int i1;
    boolean bool2;
    if (this.mZenMode != 0)
    {
      j = 1;
      if (this.mZenMode != 3) {
        break label148;
      }
      k = 1;
      if (k != 0) {
        break label153;
      }
      if ((this.mSuppressedEffects & 1L) == 0L) {
        break label158;
      }
      i = 1;
      if (k != 0) {
        break label173;
      }
      if ((j != 0) && (!this.mConfig.allowCalls)) {
        break label163;
      }
      if ((this.mSuppressedEffects & 0x2) == 0L) {
        break label178;
      }
      j = 1;
      if (this.mZenMode != 2) {
        break label183;
      }
      bool1 = true;
      int[] arrayOfInt = AudioAttributes.SDK_USAGES;
      int m = arrayOfInt.length;
      k = 0;
      if (k >= m) {
        return;
      }
      n = arrayOfInt[k];
      i1 = AudioAttributes.SUPPRESSIBLE_USAGES.get(n);
      if (i1 != 1) {
        break label195;
      }
      if (i != 0) {
        break label189;
      }
      bool2 = bool1;
      label128:
      applyRestrictions(bool2, n);
    }
    for (;;)
    {
      k += 1;
      break label92;
      j = 0;
      break;
      label148:
      k = 0;
      break label19;
      label153:
      i = 1;
      break label36;
      label158:
      i = 0;
      break label36;
      label163:
      if (this.mConfig.allowRepeatCallers) {
        break label54;
      }
      label173:
      j = 1;
      break label69;
      label178:
      j = 0;
      break label69;
      label183:
      bool1 = false;
      break label80;
      label189:
      bool2 = true;
      break label128;
      label195:
      if (i1 == 2)
      {
        if (j == 0) {}
        for (bool2 = bool1;; bool2 = true)
        {
          applyRestrictions(bool2, n);
          break;
        }
      }
      applyRestrictions(bool1, n);
    }
  }
  
  private void applyRestrictions(boolean paramBoolean, int paramInt)
  {
    int j = 1;
    AppOpsManager localAppOpsManager = this.mAppOps;
    if (paramBoolean)
    {
      i = 1;
      localAppOpsManager.setRestriction(3, paramInt, i, null);
      localAppOpsManager = this.mAppOps;
      if (!paramBoolean) {
        break label71;
      }
    }
    label71:
    for (int i = j;; i = 0)
    {
      localAppOpsManager.setRestriction(28, paramInt, i, null);
      if (isOnePlusVibrateInSlientMode()) {
        this.mAppOps.setRestriction(3, paramInt, 0, null);
      }
      return;
      i = 0;
      break;
    }
  }
  
  private void applyZenToRingerMode()
  {
    if (this.mAudioManager == null) {
      return;
    }
    int j = this.mAudioManager.getRingerModeInternal();
    int i = j;
    if (this.mVibrateFlag)
    {
      applyOnePlusZenToRingerMode(j);
      return;
    }
    switch (this.mZenMode)
    {
    }
    for (;;)
    {
      if (i != -1) {
        this.mAudioManager.setRingerModeInternal(i, "ZenModeHelper");
      }
      return;
      if (j != 0)
      {
        setPreviousRingerModeSetting(Integer.valueOf(j));
        i = 0;
        continue;
        if (j == 0)
        {
          i = getPreviousRingerModeSetting();
          setPreviousRingerModeSetting(null);
        }
      }
    }
  }
  
  private void cleanUpZenRules()
  {
    long l1 = System.currentTimeMillis();
    synchronized (this.mConfig)
    {
      ZenModeConfig localZenModeConfig2 = this.mConfig.copy();
      if (localZenModeConfig2.automaticRules != null)
      {
        int i = localZenModeConfig2.automaticRules.size() - 1;
        for (;;)
        {
          if (i >= 0)
          {
            ZenModeConfig.ZenRule localZenRule = (ZenModeConfig.ZenRule)localZenModeConfig2.automaticRules.get(localZenModeConfig2.automaticRules.keyAt(i));
            long l2 = localZenRule.creationTime;
            if (259200000L < l1 - l2) {}
            try
            {
              if (localZenRule.component != null) {
                this.mPm.getPackageInfo(localZenRule.component.getPackageName(), 8192);
              }
              i -= 1;
            }
            catch (PackageManager.NameNotFoundException localNameNotFoundException)
            {
              for (;;)
              {
                localZenModeConfig2.automaticRules.removeAt(i);
              }
            }
          }
        }
      }
    }
    setConfigLocked(localZenModeConfig3, "cleanUpZenRules");
  }
  
  private int computeZenMode()
  {
    synchronized (this.mConfig)
    {
      Object localObject1 = this.mConfig;
      if (localObject1 == null) {
        return 0;
      }
      if (this.mConfig.manualRule != null)
      {
        i = this.mConfig.manualRule.zenMode;
        return i;
      }
      int i = 0;
      localObject1 = this.mConfig.automaticRules.values().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        ZenModeConfig.ZenRule localZenRule = (ZenModeConfig.ZenRule)((Iterator)localObject1).next();
        if ((localZenRule.isAutomaticActive()) && (zenSeverity(localZenRule.zenMode) > zenSeverity(i))) {
          i = localZenRule.zenMode;
        }
      }
      return i;
    }
  }
  
  private AutomaticZenRule createAutomaticZenRule(ZenModeConfig.ZenRule paramZenRule)
  {
    return new AutomaticZenRule(paramZenRule.name, paramZenRule.component, paramZenRule.conditionId, NotificationManager.zenModeToInterruptionFilter(paramZenRule.zenMode), paramZenRule.enabled, paramZenRule.creationTime);
  }
  
  private void dispatchOnConfigChanged()
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((Callback)localIterator.next()).onConfigChanged();
    }
  }
  
  private void dispatchOnPolicyChanged()
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((Callback)localIterator.next()).onPolicyChanged();
    }
  }
  
  private void dispatchOnZenModeChanged()
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((Callback)localIterator.next()).onZenModeChanged();
    }
  }
  
  private static void dump(PrintWriter paramPrintWriter, String paramString1, String paramString2, ZenModeConfig paramZenModeConfig)
  {
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print(paramString2);
    paramPrintWriter.print('=');
    if (paramZenModeConfig == null)
    {
      paramPrintWriter.println(paramZenModeConfig);
      return;
    }
    paramPrintWriter.printf("allow(calls=%s,callsFrom=%s,repeatCallers=%s,messages=%s,messagesFrom=%s,events=%s,reminders=%s,whenScreenOff,whenScreenOn=%s)\n", new Object[] { Boolean.valueOf(paramZenModeConfig.allowCalls), ZenModeConfig.sourceToString(paramZenModeConfig.allowCallsFrom), Boolean.valueOf(paramZenModeConfig.allowRepeatCallers), Boolean.valueOf(paramZenModeConfig.allowMessages), ZenModeConfig.sourceToString(paramZenModeConfig.allowMessagesFrom), Boolean.valueOf(paramZenModeConfig.allowEvents), Boolean.valueOf(paramZenModeConfig.allowReminders), Boolean.valueOf(paramZenModeConfig.allowWhenScreenOff), Boolean.valueOf(paramZenModeConfig.allowWhenScreenOn) });
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print("  manualRule=");
    paramPrintWriter.println(paramZenModeConfig.manualRule);
    if (paramZenModeConfig.automaticRules.isEmpty()) {
      return;
    }
    int j = paramZenModeConfig.automaticRules.size();
    int i = 0;
    if (i < j)
    {
      paramPrintWriter.print(paramString1);
      if (i == 0) {}
      for (paramString2 = "  automaticRules=";; paramString2 = "                 ")
      {
        paramPrintWriter.print(paramString2);
        paramPrintWriter.println(paramZenModeConfig.automaticRules.valueAt(i));
        i += 1;
        break;
      }
    }
  }
  
  private boolean evaluateZenMode(String paramString, boolean paramBoolean)
  {
    if (DEBUG) {
      Log.d("ZenModeHelper", "evaluateZenMode");
    }
    int i = this.mZenMode;
    int j = computeZenMode();
    ZenLog.traceSetZenMode(j, paramString);
    this.mZenMode = j;
    updateRingerModeAffectedStreams();
    setZenModeSetting(this.mZenMode);
    if (paramBoolean) {
      applyZenToRingerMode();
    }
    applyRestrictions();
    if (j != i) {
      H.-wrap0(this.mHandler);
    }
    return true;
  }
  
  private static NotificationManager.Policy getNotificationPolicy(ZenModeConfig paramZenModeConfig)
  {
    if (paramZenModeConfig == null) {
      return null;
    }
    return paramZenModeConfig.toNotificationPolicy();
  }
  
  private int getPreviousRingerModeSetting()
  {
    return Settings.Global.getInt(this.mContext.getContentResolver(), "zen_mode_ringer_level", 2);
  }
  
  private ServiceInfo getServiceInfo(ComponentName paramComponentName)
  {
    Object localObject = new Intent();
    ((Intent)localObject).setComponent(paramComponentName);
    paramComponentName = this.mPm.queryIntentServicesAsUser((Intent)localObject, 132, UserHandle.getCallingUserId());
    if (paramComponentName != null)
    {
      int i = 0;
      int j = paramComponentName.size();
      while (i < j)
      {
        localObject = ((ResolveInfo)paramComponentName.get(i)).serviceInfo;
        if (this.mServiceConfig.bindPermission.equals(((ServiceInfo)localObject).permission)) {
          return (ServiceInfo)localObject;
        }
        i += 1;
      }
    }
    return null;
  }
  
  private int getZenModeSetting()
  {
    return Settings.Global.getInt(this.mContext.getContentResolver(), "zen_mode", 0);
  }
  
  private boolean isOnePlusVibrateInSlientMode()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mVibrateFlag)
    {
      bool1 = bool2;
      if (this.mZenMode == 3) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private boolean isSystemRule(AutomaticZenRule paramAutomaticZenRule)
  {
    return "android".equals(paramAutomaticZenRule.getOwner().getPackageName());
  }
  
  private void loadConfigForUser(int paramInt, String paramString)
  {
    if ((this.mUser == paramInt) || (paramInt < 0)) {
      return;
    }
    this.mUser = paramInt;
    if (DEBUG) {
      Log.d("ZenModeHelper", paramString + " u=" + paramInt);
    }
    ??? = (ZenModeConfig)this.mConfigs.get(paramInt);
    Object localObject1 = ???;
    if (??? == null)
    {
      if (DEBUG) {
        Log.d("ZenModeHelper", paramString + " generating default config for user " + paramInt);
      }
      localObject1 = this.mDefaultConfig.copy();
      ((ZenModeConfig)localObject1).user = paramInt;
    }
    if (this.mZenMode != 0)
    {
      ??? = new ZenModeConfig.ZenRule();
      ((ZenModeConfig.ZenRule)???).enabled = true;
      ((ZenModeConfig.ZenRule)???).zenMode = this.mZenMode;
      ((ZenModeConfig)localObject1).manualRule = ((ZenModeConfig.ZenRule)???);
    }
    synchronized (this.mConfig)
    {
      setConfigLocked((ZenModeConfig)localObject1, paramString);
      cleanUpZenRules();
      return;
      ((ZenModeConfig)localObject1).manualRule = null;
    }
  }
  
  private void populateZenRule(AutomaticZenRule paramAutomaticZenRule, ZenModeConfig.ZenRule paramZenRule, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramZenRule.id = ZenModeConfig.newRuleId();
      paramZenRule.creationTime = System.currentTimeMillis();
      paramZenRule.component = paramAutomaticZenRule.getOwner();
    }
    if (paramZenRule.enabled != paramAutomaticZenRule.isEnabled()) {
      paramZenRule.snoozing = false;
    }
    paramZenRule.name = paramAutomaticZenRule.getName();
    paramZenRule.condition = null;
    paramZenRule.conditionId = paramAutomaticZenRule.getConditionId();
    paramZenRule.enabled = paramAutomaticZenRule.isEnabled();
    paramZenRule.zenMode = NotificationManager.zenModeFromInterruptionFilter(paramAutomaticZenRule.getInterruptionFilter(), 0);
  }
  
  private ZenModeConfig readDefaultConfig(Resources paramResources)
  {
    localObject = null;
    localResources = null;
    try
    {
      paramResources = paramResources.getXml(17891333);
      ZenModeConfig localZenModeConfig;
      do
      {
        localResources = paramResources;
        localObject = paramResources;
        if (paramResources.next() == 1) {
          break;
        }
        localResources = paramResources;
        localObject = paramResources;
        localZenModeConfig = ZenModeConfig.readXml(paramResources, this.mConfigMigration);
      } while (localZenModeConfig == null);
      IoUtils.closeQuietly(paramResources);
      return localZenModeConfig;
      IoUtils.closeQuietly(paramResources);
    }
    catch (Exception paramResources)
    {
      for (;;)
      {
        localObject = localResources;
        Log.w("ZenModeHelper", "Error reading default zen mode config from resource", paramResources);
        IoUtils.closeQuietly(localResources);
      }
    }
    finally
    {
      IoUtils.closeQuietly((AutoCloseable)localObject);
    }
    return new ZenModeConfig();
  }
  
  /* Error */
  private boolean setConfigLocked(ZenModeConfig paramZenModeConfig, String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: invokestatic 774	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore 5
    //   5: aload_1
    //   6: ifnull +75 -> 81
    //   9: aload_1
    //   10: invokevirtual 777	android/service/notification/ZenModeConfig:isValid	()Z
    //   13: ifeq +68 -> 81
    //   16: aload_1
    //   17: getfield 712	android/service/notification/ZenModeConfig:user	I
    //   20: aload_0
    //   21: getfield 181	com/android/server/notification/ZenModeHelper:mUser	I
    //   24: if_icmpeq +90 -> 114
    //   27: aload_0
    //   28: getfield 176	com/android/server/notification/ZenModeHelper:mConfigs	Landroid/util/SparseArray;
    //   31: aload_1
    //   32: getfield 712	android/service/notification/ZenModeConfig:user	I
    //   35: aload_1
    //   36: invokevirtual 222	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   39: getstatic 157	com/android/server/notification/ZenModeHelper:DEBUG	Z
    //   42: ifeq +32 -> 74
    //   45: ldc 43
    //   47: new 694	java/lang/StringBuilder
    //   50: dup
    //   51: invokespecial 695	java/lang/StringBuilder:<init>	()V
    //   54: ldc_w 779
    //   57: invokevirtual 699	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: aload_1
    //   61: getfield 712	android/service/notification/ZenModeConfig:user	I
    //   64: invokevirtual 704	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   67: invokevirtual 706	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   70: invokestatic 612	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   73: pop
    //   74: lload 5
    //   76: invokestatic 783	android/os/Binder:restoreCallingIdentity	(J)V
    //   79: iconst_1
    //   80: ireturn
    //   81: ldc 43
    //   83: new 694	java/lang/StringBuilder
    //   86: dup
    //   87: invokespecial 695	java/lang/StringBuilder:<init>	()V
    //   90: ldc_w 785
    //   93: invokevirtual 699	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   96: aload_1
    //   97: invokevirtual 788	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   100: invokevirtual 706	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   103: invokestatic 790	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   106: pop
    //   107: lload 5
    //   109: invokestatic 783	android/os/Binder:restoreCallingIdentity	(J)V
    //   112: iconst_0
    //   113: ireturn
    //   114: aload_0
    //   115: getfield 244	com/android/server/notification/ZenModeHelper:mConditions	Lcom/android/server/notification/ZenModeConditions;
    //   118: aload_1
    //   119: iconst_0
    //   120: invokevirtual 391	com/android/server/notification/ZenModeConditions:evaluateConfig	(Landroid/service/notification/ZenModeConfig;Z)V
    //   123: aload_0
    //   124: getfield 176	com/android/server/notification/ZenModeHelper:mConfigs	Landroid/util/SparseArray;
    //   127: aload_1
    //   128: getfield 712	android/service/notification/ZenModeConfig:user	I
    //   131: aload_1
    //   132: invokevirtual 222	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   135: ldc 43
    //   137: new 694	java/lang/StringBuilder
    //   140: dup
    //   141: invokespecial 695	java/lang/StringBuilder:<init>	()V
    //   144: ldc_w 792
    //   147: invokevirtual 699	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   150: aload_2
    //   151: invokevirtual 699	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   154: invokevirtual 706	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   157: new 794	java/lang/Throwable
    //   160: dup
    //   161: invokespecial 795	java/lang/Throwable:<init>	()V
    //   164: invokestatic 797	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   167: pop
    //   168: aload_2
    //   169: aload_0
    //   170: getfield 218	com/android/server/notification/ZenModeHelper:mConfig	Landroid/service/notification/ZenModeConfig;
    //   173: aload_1
    //   174: invokestatic 801	com/android/server/notification/ZenLog:traceConfig	(Ljava/lang/String;Landroid/service/notification/ZenModeConfig;Landroid/service/notification/ZenModeConfig;)V
    //   177: aload_0
    //   178: getfield 218	com/android/server/notification/ZenModeHelper:mConfig	Landroid/service/notification/ZenModeConfig;
    //   181: invokestatic 803	com/android/server/notification/ZenModeHelper:getNotificationPolicy	(Landroid/service/notification/ZenModeConfig;)Landroid/app/NotificationManager$Policy;
    //   184: aload_1
    //   185: invokestatic 803	com/android/server/notification/ZenModeHelper:getNotificationPolicy	(Landroid/service/notification/ZenModeConfig;)Landroid/app/NotificationManager$Policy;
    //   188: invokestatic 808	java/util/Objects:equals	(Ljava/lang/Object;Ljava/lang/Object;)Z
    //   191: ifeq +49 -> 240
    //   194: iconst_0
    //   195: istore 4
    //   197: aload_1
    //   198: aload_0
    //   199: getfield 218	com/android/server/notification/ZenModeHelper:mConfig	Landroid/service/notification/ZenModeConfig;
    //   202: invokevirtual 809	android/service/notification/ZenModeConfig:equals	(Ljava/lang/Object;)Z
    //   205: ifne +7 -> 212
    //   208: aload_0
    //   209: invokespecial 811	com/android/server/notification/ZenModeHelper:dispatchOnConfigChanged	()V
    //   212: iload 4
    //   214: ifeq +7 -> 221
    //   217: aload_0
    //   218: invokespecial 813	com/android/server/notification/ZenModeHelper:dispatchOnPolicyChanged	()V
    //   221: aload_0
    //   222: aload_1
    //   223: putfield 218	com/android/server/notification/ZenModeHelper:mConfig	Landroid/service/notification/ZenModeConfig;
    //   226: aload_0
    //   227: aload_1
    //   228: aload_2
    //   229: iload_3
    //   230: invokespecial 120	com/android/server/notification/ZenModeHelper:applyConfig	(Landroid/service/notification/ZenModeConfig;Ljava/lang/String;Z)V
    //   233: lload 5
    //   235: invokestatic 783	android/os/Binder:restoreCallingIdentity	(J)V
    //   238: iconst_1
    //   239: ireturn
    //   240: iconst_1
    //   241: istore 4
    //   243: goto -46 -> 197
    //   246: astore_1
    //   247: lload 5
    //   249: invokestatic 783	android/os/Binder:restoreCallingIdentity	(J)V
    //   252: aload_1
    //   253: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	254	0	this	ZenModeHelper
    //   0	254	1	paramZenModeConfig	ZenModeConfig
    //   0	254	2	paramString	String
    //   0	254	3	paramBoolean	boolean
    //   195	47	4	i	int
    //   3	245	5	l	long
    // Exception table:
    //   from	to	target	type
    //   9	74	246	finally
    //   81	107	246	finally
    //   114	194	246	finally
    //   197	212	246	finally
    //   217	221	246	finally
    //   221	233	246	finally
  }
  
  private void setManualZenMode(int paramInt, Uri paramUri, String paramString1, String paramString2, boolean paramBoolean)
  {
    for (;;)
    {
      ZenModeConfig localZenModeConfig2;
      int i;
      synchronized (this.mConfig)
      {
        localZenModeConfig2 = this.mConfig;
        if (localZenModeConfig2 == null) {
          return;
        }
        boolean bool = Settings.Global.isValidZenMode(paramInt);
        if (!bool) {
          return;
        }
        if (DEBUG) {
          Log.d("ZenModeHelper", "setManualZenMode " + Settings.Global.zenModeToString(paramInt) + " conditionId=" + paramUri + " reason=" + paramString1 + " setRingerMode=" + paramBoolean);
        }
        int j = this.mConfigs.size();
        i = 0;
        if (i >= j) {
          break;
        }
        int k = this.mConfigs.keyAt(i);
        localZenModeConfig2 = ((ZenModeConfig)this.mConfigs.get(k)).copy();
        if (paramInt == 0)
        {
          localZenModeConfig2.manualRule = null;
          localObject = localZenModeConfig2.automaticRules.values().iterator();
          if (!((Iterator)localObject).hasNext()) {
            break label263;
          }
          ZenModeConfig.ZenRule localZenRule = (ZenModeConfig.ZenRule)((Iterator)localObject).next();
          if (!localZenRule.isAutomaticActive()) {
            continue;
          }
          localZenRule.snoozing = true;
        }
      }
      Object localObject = new ZenModeConfig.ZenRule();
      ((ZenModeConfig.ZenRule)localObject).enabled = true;
      ((ZenModeConfig.ZenRule)localObject).zenMode = paramInt;
      ((ZenModeConfig.ZenRule)localObject).conditionId = paramUri;
      ((ZenModeConfig.ZenRule)localObject).enabler = paramString2;
      localZenModeConfig2.manualRule = ((ZenModeConfig.ZenRule)localObject);
      label263:
      setConfigLocked(localZenModeConfig2, paramString1, paramBoolean);
      i += 1;
    }
  }
  
  private void setPreviousRingerModeSetting(Integer paramInteger)
  {
    Object localObject = null;
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramInteger == null) {}
    for (paramInteger = (Integer)localObject;; paramInteger = Integer.toString(paramInteger.intValue()))
    {
      Settings.Global.putString(localContentResolver, "zen_mode_ringer_level", paramInteger);
      return;
    }
  }
  
  private void setZenModeSetting(int paramInt)
  {
    Settings.Global.putInt(this.mContext.getContentResolver(), "zen_mode", paramInt);
  }
  
  private void updateRingerModeAffectedStreams()
  {
    if (this.mAudioManager != null) {
      this.mAudioManager.updateRingerModeAffectedStreamsInternal();
    }
  }
  
  private static int zenSeverity(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 1: 
      return 1;
    case 3: 
      return 2;
    }
    return 3;
  }
  
  public String addAutomaticZenRule(AutomaticZenRule paramAutomaticZenRule, String paramString)
  {
    if (!isSystemRule(paramAutomaticZenRule))
    {
      ??? = getServiceInfo(paramAutomaticZenRule.getOwner());
      if (??? == null) {
        throw new IllegalArgumentException("Owner is not a condition provider service");
      }
      int i = -1;
      if (((ServiceInfo)???).metaData != null) {
        i = ((ServiceInfo)???).metaData.getInt("android.service.zen.automatic.ruleInstanceLimit", -1);
      }
      if ((i > 0) && (i < getCurrentInstanceCount(paramAutomaticZenRule.getOwner()) + 1)) {
        throw new IllegalArgumentException("Rule instance limit exceeded");
      }
    }
    synchronized (this.mConfig)
    {
      if (this.mConfig == null) {
        throw new AndroidRuntimeException("Could not create rule");
      }
    }
    if (DEBUG) {
      Log.d("ZenModeHelper", "addAutomaticZenRule rule= " + paramAutomaticZenRule + " reason=" + paramString);
    }
    ZenModeConfig localZenModeConfig = this.mConfig.copy();
    ZenModeConfig.ZenRule localZenRule = new ZenModeConfig.ZenRule();
    populateZenRule(paramAutomaticZenRule, localZenRule, true);
    localZenModeConfig.automaticRules.put(localZenRule.id, localZenRule);
    if (setConfigLocked(localZenModeConfig, paramString, true))
    {
      paramAutomaticZenRule = localZenRule.id;
      return paramAutomaticZenRule;
    }
    throw new AndroidRuntimeException("Could not create rule");
  }
  
  public void addCallback(Callback paramCallback)
  {
    this.mCallbacks.add(paramCallback);
  }
  
  public boolean canManageAutomaticZenRule(ZenModeConfig.ZenRule paramZenRule)
  {
    int i = Binder.getCallingUid();
    if ((i == 0) || (i == 1000)) {
      return true;
    }
    if (this.mContext.checkCallingPermission("android.permission.MANAGE_NOTIFICATIONS") == 0) {
      return true;
    }
    String[] arrayOfString = this.mPm.getPackagesForUid(Binder.getCallingUid());
    if (arrayOfString != null)
    {
      int j = arrayOfString.length;
      i = 0;
      while (i < j)
      {
        if (arrayOfString[i].equals(paramZenRule.component.getPackageName())) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mZenMode=");
    paramPrintWriter.println(Settings.Global.zenModeToString(this.mZenMode));
    dump(paramPrintWriter, paramString, "mDefaultConfig", this.mDefaultConfig);
    int j = this.mConfigs.size();
    int i = 0;
    while (i < j)
    {
      dump(paramPrintWriter, paramString, "mConfigs[u=" + this.mConfigs.keyAt(i) + "]", (ZenModeConfig)this.mConfigs.valueAt(i));
      i += 1;
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mUser=");
    paramPrintWriter.println(this.mUser);
    synchronized (this.mConfig)
    {
      dump(paramPrintWriter, paramString, "mConfig", this.mConfig);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSuppressedEffects=");
      paramPrintWriter.println(this.mSuppressedEffects);
      this.mFiltering.dump(paramPrintWriter, paramString);
      this.mConditions.dump(paramPrintWriter, paramString);
      return;
    }
  }
  
  public AutomaticZenRule getAutomaticZenRule(String paramString)
  {
    synchronized (this.mConfig)
    {
      ZenModeConfig localZenModeConfig2 = this.mConfig;
      if (localZenModeConfig2 == null) {
        return null;
      }
      paramString = (ZenModeConfig.ZenRule)this.mConfig.automaticRules.get(paramString);
      if (paramString == null) {
        return null;
      }
    }
    if (canManageAutomaticZenRule(paramString)) {
      return createAutomaticZenRule(paramString);
    }
    return null;
  }
  
  public ZenModeConfig getConfig()
  {
    synchronized (this.mConfig)
    {
      ZenModeConfig localZenModeConfig2 = this.mConfig.copy();
      return localZenModeConfig2;
    }
  }
  
  public int getCurrentInstanceCount(ComponentName paramComponentName)
  {
    int i = 0;
    synchronized (this.mConfig)
    {
      Iterator localIterator = this.mConfig.automaticRules.values().iterator();
      while (localIterator.hasNext())
      {
        ZenModeConfig.ZenRule localZenRule = (ZenModeConfig.ZenRule)localIterator.next();
        if (localZenRule.component != null)
        {
          boolean bool = localZenRule.component.equals(paramComponentName);
          if (bool) {
            i += 1;
          }
        }
      }
      return i;
    }
  }
  
  public Looper getLooper()
  {
    return this.mHandler.getLooper();
  }
  
  public NotificationManager.Policy getNotificationPolicy()
  {
    return getNotificationPolicy(this.mConfig);
  }
  
  public long getSuppressedEffects()
  {
    return this.mSuppressedEffects;
  }
  
  public int getZenMode()
  {
    return this.mZenMode;
  }
  
  public int getZenModeListenerInterruptionFilter()
  {
    return NotificationManager.zenModeToInterruptionFilter(this.mZenMode);
  }
  
  public List<ZenModeConfig.ZenRule> getZenRules()
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (this.mConfig)
    {
      Object localObject = this.mConfig;
      if (localObject == null) {
        return localArrayList;
      }
      localObject = this.mConfig.automaticRules.values().iterator();
      while (((Iterator)localObject).hasNext())
      {
        ZenModeConfig.ZenRule localZenRule = (ZenModeConfig.ZenRule)((Iterator)localObject).next();
        if (canManageAutomaticZenRule(localZenRule)) {
          localArrayList.add(localZenRule);
        }
      }
    }
    return localList;
  }
  
  public void initZenMode()
  {
    if (DEBUG) {
      Log.d("ZenModeHelper", "initZenMode");
    }
    evaluateZenMode("init", true);
  }
  
  public boolean isCall(NotificationRecord paramNotificationRecord)
  {
    return this.mFiltering.isCall(paramNotificationRecord);
  }
  
  public boolean matchesCallFilter(UserHandle paramUserHandle, Bundle paramBundle, ValidateNotificationPeople paramValidateNotificationPeople, int paramInt, float paramFloat)
  {
    synchronized (this.mConfig)
    {
      boolean bool = isOnePlusVibrateInSlientMode();
      if (bool) {
        return true;
      }
      bool = ZenModeFiltering.matchesCallFilter(this.mContext, this.mZenMode, this.mConfig, paramUserHandle, paramBundle, paramValidateNotificationPeople, paramInt, paramFloat);
      return bool;
    }
  }
  
  public void onSystemReady()
  {
    if (DEBUG) {
      Log.d("ZenModeHelper", "onSystemReady");
    }
    this.mAudioManager = ((AudioManagerInternal)LocalServices.getService(AudioManagerInternal.class));
    if (this.mAudioManager != null) {
      this.mAudioManager.setRingerModeDelegate(this.mRingerModeDelegate);
    }
    this.mPm = this.mContext.getPackageManager();
    H.-wrap1(this.mHandler);
    cleanUpZenRules();
    evaluateZenMode("onSystemReady", true);
  }
  
  public void onUserRemoved(int paramInt)
  {
    if (paramInt < 0) {
      return;
    }
    if (DEBUG) {
      Log.d("ZenModeHelper", "onUserRemoved u=" + paramInt);
    }
    this.mConfigs.remove(paramInt);
  }
  
  public void onUserSwitched(int paramInt)
  {
    loadConfigForUser(paramInt, "onUserSwitched");
  }
  
  public void onUserUnlocked(int paramInt)
  {
    loadConfigForUser(paramInt, "onUserUnlocked");
  }
  
  public void readXml(XmlPullParser paramXmlPullParser, boolean paramBoolean)
    throws XmlPullParserException, IOException
  {
    paramXmlPullParser = ZenModeConfig.readXml(paramXmlPullParser, this.mConfigMigration);
    if (paramXmlPullParser != null)
    {
      if (paramBoolean)
      {
        if (paramXmlPullParser.user != 0) {
          return;
        }
        paramXmlPullParser.manualRule = null;
        long l = System.currentTimeMillis();
        if (paramXmlPullParser.automaticRules != null)
        {
          ??? = paramXmlPullParser.automaticRules.values().iterator();
          while (((Iterator)???).hasNext())
          {
            ZenModeConfig.ZenRule localZenRule = (ZenModeConfig.ZenRule)((Iterator)???).next();
            localZenRule.snoozing = false;
            localZenRule.condition = null;
            localZenRule.creationTime = l;
          }
        }
      }
      if (DEBUG) {
        Log.d("ZenModeHelper", "readXml");
      }
    }
    synchronized (this.mConfig)
    {
      setConfigLocked(paramXmlPullParser, "readXml");
      return;
    }
  }
  
  public void recordCaller(NotificationRecord paramNotificationRecord)
  {
    this.mFiltering.recordCall(paramNotificationRecord);
  }
  
  public boolean removeAutomaticZenRule(String paramString1, String paramString2)
  {
    synchronized (this.mConfig)
    {
      ZenModeConfig localZenModeConfig2 = this.mConfig;
      if (localZenModeConfig2 == null) {
        return false;
      }
      localZenModeConfig2 = this.mConfig.copy();
      ZenModeConfig.ZenRule localZenRule = (ZenModeConfig.ZenRule)localZenModeConfig2.automaticRules.get(paramString1);
      if (localZenRule == null) {
        return false;
      }
      if (canManageAutomaticZenRule(localZenRule))
      {
        localZenModeConfig2.automaticRules.remove(paramString1);
        if (DEBUG) {
          Log.d("ZenModeHelper", "removeZenRule zenRule=" + paramString1 + " reason=" + paramString2);
        }
        boolean bool = setConfigLocked(localZenModeConfig2, paramString2, true);
        return bool;
      }
      throw new SecurityException("Cannot delete rules not owned by your condition provider");
    }
  }
  
  public boolean removeAutomaticZenRules(String paramString1, String paramString2)
  {
    for (;;)
    {
      ZenModeConfig localZenModeConfig2;
      int i;
      ZenModeConfig.ZenRule localZenRule;
      synchronized (this.mConfig)
      {
        localZenModeConfig2 = this.mConfig;
        if (localZenModeConfig2 == null) {
          return false;
        }
        localZenModeConfig2 = this.mConfig.copy();
        i = localZenModeConfig2.automaticRules.size() - 1;
        if (i < 0) {
          break label198;
        }
        localZenRule = (ZenModeConfig.ZenRule)localZenModeConfig2.automaticRules.get(localZenModeConfig2.automaticRules.keyAt(i));
        if ((localZenRule != null) && (localZenRule.component != null) && (localZenRule.component.getPackageName() != null) && (localZenRule.component.getPackageName().equals(paramString1)) && (canManageAutomaticZenRule(localZenRule))) {
          localZenModeConfig2.automaticRules.removeAt(i);
        } else if (localZenRule == null) {
          Log.e("ZenModeHelper", "removeAutomaticZenRules rule is null");
        }
      }
      if (localZenRule.component == null)
      {
        Log.e("ZenModeHelper", "removeAutomaticZenRules rule.component is null");
      }
      else if (localZenRule.component.getPackageName() == null)
      {
        Log.e("ZenModeHelper", "removeAutomaticZenRules rule.component.getPackageName() is null");
        break label214;
        label198:
        boolean bool = setConfigLocked(localZenModeConfig2, paramString2, true);
        return bool;
      }
      label214:
      i -= 1;
    }
  }
  
  public void removeCallback(Callback paramCallback)
  {
    this.mCallbacks.remove(paramCallback);
  }
  
  public void requestFromListener(ComponentName paramComponentName, int paramInt)
  {
    paramInt = NotificationManager.zenModeFromInterruptionFilter(paramInt, -1);
    String str;
    StringBuilder localStringBuilder;
    if (paramInt != -1)
    {
      if (paramComponentName == null) {
        break label61;
      }
      str = paramComponentName.getPackageName();
      localStringBuilder = new StringBuilder().append("listener:");
      if (paramComponentName == null) {
        break label66;
      }
    }
    label61:
    label66:
    for (paramComponentName = paramComponentName.flattenToShortString();; paramComponentName = null)
    {
      setManualZenMode(paramInt, null, str, paramComponentName);
      return;
      str = null;
      break;
    }
  }
  
  public void setConfig(ZenModeConfig paramZenModeConfig, String paramString)
  {
    synchronized (this.mConfig)
    {
      setConfigLocked(paramZenModeConfig, paramString);
      return;
    }
  }
  
  public boolean setConfigLocked(ZenModeConfig paramZenModeConfig, String paramString)
  {
    return setConfigLocked(paramZenModeConfig, paramString, true);
  }
  
  public void setManualZenMode(int paramInt, Uri paramUri, String paramString1, String paramString2)
  {
    setManualZenMode(paramInt, paramUri, paramString2, paramString1, true);
  }
  
  public void setNotificationPolicy(NotificationManager.Policy paramPolicy)
  {
    if ((paramPolicy == null) || (this.mConfig == null)) {
      return;
    }
    synchronized (this.mConfig)
    {
      ZenModeConfig localZenModeConfig2 = this.mConfig.copy();
      localZenModeConfig2.applyNotificationPolicy(paramPolicy);
      setConfigLocked(localZenModeConfig2, "setNotificationPolicy");
      return;
    }
  }
  
  public void setOnePlusVibrateInSilentMode(boolean paramBoolean)
  {
    if (this.mAudioManager == null)
    {
      Log.e("ZenModeHelper", "[setOnePlusVibrateInSilentMode] audio manager is not initialized");
      return;
    }
    if (this.mZenMode == 3)
    {
      int i = getPreviousRingerModeSetting();
      setPreviousRingerModeSetting(null);
      if (i != -1) {
        this.mAudioManager.setRingerModeInternal(i, "ZenModeHelper");
      }
    }
    this.mVibrateFlag = paramBoolean;
  }
  
  public void setSuppressedEffects(long paramLong)
  {
    if (this.mSuppressedEffects == paramLong) {
      return;
    }
    this.mSuppressedEffects = paramLong;
    applyRestrictions();
  }
  
  public boolean shouldIntercept(NotificationRecord paramNotificationRecord)
  {
    synchronized (this.mConfig)
    {
      boolean bool = this.mFiltering.shouldIntercept(this.mZenMode, this.mConfig, paramNotificationRecord);
      return bool;
    }
  }
  
  public boolean shouldSuppressWhenScreenOff()
  {
    synchronized (this.mConfig)
    {
      boolean bool = this.mConfig.allowWhenScreenOff;
      if (bool)
      {
        bool = false;
        return bool;
      }
      bool = true;
    }
  }
  
  public boolean shouldSuppressWhenScreenOn()
  {
    synchronized (this.mConfig)
    {
      boolean bool = this.mConfig.allowWhenScreenOn;
      if (bool)
      {
        bool = false;
        return bool;
      }
      bool = true;
    }
  }
  
  public String toString()
  {
    return "ZenModeHelper";
  }
  
  public boolean updateAutomaticZenRule(String paramString1, AutomaticZenRule paramAutomaticZenRule, String paramString2)
  {
    ZenModeConfig localZenModeConfig2;
    synchronized (this.mConfig)
    {
      localZenModeConfig2 = this.mConfig;
      if (localZenModeConfig2 == null) {
        return false;
      }
      if (DEBUG) {
        Log.d("ZenModeHelper", "updateAutomaticZenRule zenRule=" + paramAutomaticZenRule + " reason=" + paramString2);
      }
      localZenModeConfig2 = this.mConfig.copy();
      if (paramString1 == null) {
        throw new IllegalArgumentException("Rule doesn't exist");
      }
    }
    ZenModeConfig.ZenRule localZenRule = (ZenModeConfig.ZenRule)localZenModeConfig2.automaticRules.get(paramString1);
    if ((localZenRule != null) && (canManageAutomaticZenRule(localZenRule)))
    {
      populateZenRule(paramAutomaticZenRule, localZenRule, false);
      localZenModeConfig2.automaticRules.put(paramString1, localZenRule);
      boolean bool = setConfigLocked(localZenModeConfig2, paramString2, true);
      return bool;
    }
    throw new SecurityException("Cannot update rules not owned by your condition provider");
  }
  
  public void writeXml(XmlSerializer paramXmlSerializer, boolean paramBoolean)
    throws IOException
  {
    int j = this.mConfigs.size();
    int i = 0;
    if (i < j)
    {
      if ((paramBoolean) && (this.mConfigs.keyAt(i) != 0)) {}
      for (;;)
      {
        i += 1;
        break;
        ((ZenModeConfig)this.mConfigs.valueAt(i)).writeXml(paramXmlSerializer);
      }
    }
  }
  
  public static class Callback
  {
    void onConfigChanged() {}
    
    void onPolicyChanged() {}
    
    void onZenModeChanged() {}
  }
  
  private final class H
    extends Handler
  {
    private static final long METRICS_PERIOD_MS = 21600000L;
    private static final int MSG_APPLY_CONFIG = 4;
    private static final int MSG_DISPATCH = 1;
    private static final int MSG_METRICS = 2;
    
    private H(Looper paramLooper)
    {
      super();
    }
    
    private void postApplyConfig(ZenModeConfig paramZenModeConfig, String paramString, boolean paramBoolean)
    {
      sendMessage(obtainMessage(4, new ConfigMessageData(paramZenModeConfig, paramString, paramBoolean)));
    }
    
    private void postDispatchOnZenModeChanged()
    {
      removeMessages(1);
      sendEmptyMessage(1);
    }
    
    private void postMetricsTimer()
    {
      removeMessages(2);
      sendEmptyMessageDelayed(2, 21600000L);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      case 3: 
      default: 
        return;
      case 1: 
        ZenModeHelper.-wrap5(ZenModeHelper.this);
        return;
      case 2: 
        ZenModeHelper.Metrics.-wrap0(ZenModeHelper.-get2(ZenModeHelper.this));
        return;
      }
      paramMessage = (ConfigMessageData)paramMessage.obj;
      ZenModeHelper.-wrap3(ZenModeHelper.this, paramMessage.config, paramMessage.reason, paramMessage.setRingerMode);
    }
    
    private final class ConfigMessageData
    {
      public final ZenModeConfig config;
      public final String reason;
      public final boolean setRingerMode;
      
      ConfigMessageData(ZenModeConfig paramZenModeConfig, String paramString, boolean paramBoolean)
      {
        this.config = paramZenModeConfig;
        this.reason = paramString;
        this.setRingerMode = paramBoolean;
      }
    }
  }
  
  private final class Metrics
    extends ZenModeHelper.Callback
  {
    private static final String COUNTER_PREFIX = "dnd_mode_";
    private static final long MINIMUM_LOG_PERIOD_MS = 60000L;
    private long mBeginningMs = 0L;
    private int mPreviousZenMode = -1;
    
    private Metrics() {}
    
    private void emit()
    {
      ZenModeHelper.H.-wrap1(ZenModeHelper.-get1(ZenModeHelper.this));
      long l1 = SystemClock.elapsedRealtime();
      long l2 = l1 - this.mBeginningMs;
      if ((this.mPreviousZenMode != ZenModeHelper.-get3(ZenModeHelper.this)) || (l2 > 60000L))
      {
        if (this.mPreviousZenMode != -1) {
          MetricsLogger.count(ZenModeHelper.-get0(ZenModeHelper.this), "dnd_mode_" + this.mPreviousZenMode, (int)l2);
        }
        this.mPreviousZenMode = ZenModeHelper.-get3(ZenModeHelper.this);
        this.mBeginningMs = l1;
      }
    }
    
    void onZenModeChanged()
    {
      emit();
    }
  }
  
  private final class RingerModeDelegate
    implements AudioManagerInternal.RingerModeDelegate
  {
    private RingerModeDelegate() {}
    
    public boolean canVolumeDownEnterSilent()
    {
      boolean bool = false;
      if (ZenModeHelper.-get3(ZenModeHelper.this) == 0) {
        bool = true;
      }
      return bool;
    }
    
    public int getRingerModeAffectedStreams(int paramInt)
    {
      paramInt |= 0x26;
      if (ZenModeHelper.-get3(ZenModeHelper.this) == 2) {
        return paramInt | 0x18;
      }
      return paramInt & 0xFFFFFFE7;
    }
    
    public int onSetRingerModeExternal(int paramInt1, int paramInt2, String paramString, int paramInt3, VolumePolicy paramVolumePolicy)
    {
      int k = paramInt2;
      int i;
      int m;
      label20:
      int j;
      if (paramInt1 != paramInt2)
      {
        i = 1;
        if (paramInt3 != 1) {
          break label95;
        }
        m = 1;
        j = -1;
        switch (paramInt2)
        {
        default: 
          i = k;
        }
      }
      for (;;)
      {
        if (j != -1) {
          ZenModeHelper.-wrap6(ZenModeHelper.this, j, null, "ringerModeExternal", paramString, false);
        }
        ZenLog.traceSetRingerModeExternal(paramInt1, paramInt2, paramString, paramInt3, i);
        return i;
        i = 0;
        break;
        label95:
        m = 0;
        break label20;
        if (i != 0)
        {
          if (ZenModeHelper.-get3(ZenModeHelper.this) == 0) {
            j = 3;
          }
          if (m != 0) {
            i = 1;
          } else {
            i = 0;
          }
        }
        else
        {
          i = paramInt3;
          continue;
          i = k;
          if (ZenModeHelper.-get3(ZenModeHelper.this) != 0)
          {
            j = 0;
            i = k;
          }
        }
      }
    }
    
    public int onSetRingerModeInternal(int paramInt1, int paramInt2, String paramString, int paramInt3, VolumePolicy paramVolumePolicy)
    {
      int j;
      int k;
      int m;
      int n;
      int i;
      if (paramInt1 != paramInt2)
      {
        j = 1;
        k = paramInt2;
        m = -1;
        switch (paramInt2)
        {
        default: 
          n = k;
          i = m;
          label48:
          if (i != -1) {
            ZenModeHelper.-wrap6(ZenModeHelper.this, i, null, "ringerModeInternal", null, false);
          }
          if ((j == 0) && (i == -1)) {
            break;
          }
        }
      }
      for (;;)
      {
        ZenLog.traceSetRingerModeInternal(paramInt1, paramInt2, paramString, paramInt3, n);
        do
        {
          return n;
          j = 0;
          break;
          i = m;
          n = k;
          if (j == 0) {
            break label48;
          }
          i = m;
          n = k;
          if (!paramVolumePolicy.doNotDisturbWhenSilent) {
            break label48;
          }
          i = m;
          if (ZenModeHelper.-get3(ZenModeHelper.this) != 2)
          {
            i = m;
            if (ZenModeHelper.-get3(ZenModeHelper.this) != 3) {
              i = 3;
            }
          }
          ZenModeHelper.-wrap7(ZenModeHelper.this, Integer.valueOf(paramInt1));
          n = k;
          break label48;
          if ((j != 0) && (paramInt1 == 0) && ((ZenModeHelper.-get3(ZenModeHelper.this) == 2) || (ZenModeHelper.-get3(ZenModeHelper.this) == 3)))
          {
            i = 0;
            n = k;
            break label48;
          }
          i = m;
          n = k;
          if (ZenModeHelper.-get3(ZenModeHelper.this) == 0) {
            break label48;
          }
          n = 0;
          i = m;
          break label48;
        } while (paramInt3 == n);
      }
    }
    
    public String toString()
    {
      return "ZenModeHelper";
    }
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    private final Uri ZEN_MODE = Settings.Global.getUriFor("zen_mode");
    
    public SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    public void observe()
    {
      ZenModeHelper.-get0(ZenModeHelper.this).getContentResolver().registerContentObserver(this.ZEN_MODE, false, this);
      update(null);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      update(paramUri);
    }
    
    public void update(Uri paramUri)
    {
      if (this.ZEN_MODE.equals(paramUri))
      {
        int i = ZenModeHelper.-wrap0(ZenModeHelper.this);
        if (ZenModeHelper.-get3(ZenModeHelper.this) != i)
        {
          if (ZenModeHelper.DEBUG) {
            Log.d("ZenModeHelper", "Fixing zen mode setting");
          }
          ZenModeHelper.-set0(ZenModeHelper.this, i);
          ZenModeHelper.-wrap8(ZenModeHelper.this, i);
          ZenModeHelper.-wrap4(ZenModeHelper.this);
          ZenModeHelper.-wrap5(ZenModeHelper.this);
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/ZenModeHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */