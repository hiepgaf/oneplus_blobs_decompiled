package com.android.server.notification;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.service.notification.StatusBarNotification;
import android.service.notification.ZenModeConfig;
import android.telecom.TelecomManager;
import android.util.ArrayMap;
import android.util.Slog;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Objects;

public class ZenModeFiltering
{
  private static final boolean DEBUG = ZenModeHelper.DEBUG;
  static final RepeatCallers REPEAT_CALLERS = new RepeatCallers(null);
  private static final String TAG = "ZenModeHelper";
  private final Context mContext;
  private ComponentName mDefaultPhoneApp;
  
  public ZenModeFiltering(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private static boolean audienceMatches(int paramInt, float paramFloat)
  {
    switch (paramInt)
    {
    default: 
      Slog.w("ZenModeHelper", "Encountered unknown source: " + paramInt);
      return true;
    case 0: 
      return true;
    case 1: 
      return paramFloat >= 0.5F;
    }
    return paramFloat >= 1.0F;
  }
  
  private static Bundle extras(NotificationRecord paramNotificationRecord)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (paramNotificationRecord != null)
    {
      localObject1 = localObject2;
      if (paramNotificationRecord.sbn != null)
      {
        localObject1 = localObject2;
        if (paramNotificationRecord.sbn.getNotification() != null) {
          localObject1 = paramNotificationRecord.sbn.getNotification().extras;
        }
      }
    }
    return (Bundle)localObject1;
  }
  
  private static boolean isAlarm(NotificationRecord paramNotificationRecord)
  {
    if ((!paramNotificationRecord.isCategory("alarm")) && (!paramNotificationRecord.isAudioStream(4))) {
      return paramNotificationRecord.isAudioAttributesUsage(4);
    }
    return true;
  }
  
  private boolean isDefaultMessagingApp(NotificationRecord paramNotificationRecord)
  {
    int i = paramNotificationRecord.getUserId();
    if ((i == 55536) || (i == -1)) {
      return false;
    }
    return Objects.equals(Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "sms_default_application", i), paramNotificationRecord.sbn.getPackageName());
  }
  
  private boolean isDefaultPhoneApp(String paramString)
  {
    ComponentName localComponentName = null;
    if (this.mDefaultPhoneApp == null)
    {
      TelecomManager localTelecomManager = (TelecomManager)this.mContext.getSystemService("telecom");
      if (localTelecomManager != null) {
        localComponentName = localTelecomManager.getDefaultPhoneApp();
      }
      this.mDefaultPhoneApp = localComponentName;
      if (DEBUG) {
        Slog.d("ZenModeHelper", "Default phone app: " + this.mDefaultPhoneApp);
      }
    }
    if ((paramString != null) && (this.mDefaultPhoneApp != null)) {
      return paramString.equals(this.mDefaultPhoneApp.getPackageName());
    }
    return false;
  }
  
  private static boolean isEvent(NotificationRecord paramNotificationRecord)
  {
    return paramNotificationRecord.isCategory("event");
  }
  
  private boolean isMessage(NotificationRecord paramNotificationRecord)
  {
    if (!paramNotificationRecord.isCategory("msg")) {
      return isDefaultMessagingApp(paramNotificationRecord);
    }
    return true;
  }
  
  private static boolean isReminder(NotificationRecord paramNotificationRecord)
  {
    return paramNotificationRecord.isCategory("reminder");
  }
  
  private static boolean isSystem(NotificationRecord paramNotificationRecord)
  {
    return paramNotificationRecord.isCategory("sys");
  }
  
  public static boolean matchesCallFilter(Context paramContext, int paramInt1, ZenModeConfig paramZenModeConfig, UserHandle paramUserHandle, Bundle paramBundle, ValidateNotificationPeople paramValidateNotificationPeople, int paramInt2, float paramFloat)
  {
    if (paramInt1 == 2) {
      return false;
    }
    if (paramInt1 == 3) {
      return false;
    }
    if (paramInt1 == 1)
    {
      if ((paramZenModeConfig.allowRepeatCallers) && (RepeatCallers.-wrap0(REPEAT_CALLERS, paramContext, paramBundle))) {
        return true;
      }
      if (!paramZenModeConfig.allowCalls) {
        return false;
      }
      if (paramValidateNotificationPeople != null)
      {
        paramFloat = paramValidateNotificationPeople.getContactAffinity(paramUserHandle, paramBundle, paramInt2, paramFloat);
        return audienceMatches(paramZenModeConfig.allowCallsFrom, paramFloat);
      }
    }
    return true;
  }
  
  private static boolean shouldInterceptAudience(int paramInt, NotificationRecord paramNotificationRecord)
  {
    if (!audienceMatches(paramInt, paramNotificationRecord.getContactAffinity()))
    {
      ZenLog.traceIntercepted(paramNotificationRecord, "!audienceMatches");
      return true;
    }
    return false;
  }
  
  private static String ts(long paramLong)
  {
    return new Date(paramLong) + " (" + paramLong + ")";
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mDefaultPhoneApp=");
    paramPrintWriter.println(this.mDefaultPhoneApp);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("RepeatCallers.mThresholdMinutes=");
    paramPrintWriter.println(RepeatCallers.-get1(REPEAT_CALLERS));
    synchronized (REPEAT_CALLERS)
    {
      if (!RepeatCallers.-get0(REPEAT_CALLERS).isEmpty())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("RepeatCallers.mCalls=");
        int i = 0;
        while (i < RepeatCallers.-get0(REPEAT_CALLERS).size())
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  ");
          paramPrintWriter.print((String)RepeatCallers.-get0(REPEAT_CALLERS).keyAt(i));
          paramPrintWriter.print(" at ");
          paramPrintWriter.println(ts(((Long)RepeatCallers.-get0(REPEAT_CALLERS).valueAt(i)).longValue()));
          i += 1;
        }
      }
      return;
    }
  }
  
  public boolean isCall(NotificationRecord paramNotificationRecord)
  {
    if (paramNotificationRecord != null)
    {
      if (!isDefaultPhoneApp(paramNotificationRecord.sbn.getPackageName())) {
        return paramNotificationRecord.isCategory("call");
      }
      return true;
    }
    return false;
  }
  
  protected void recordCall(NotificationRecord paramNotificationRecord)
  {
    RepeatCallers.-wrap1(REPEAT_CALLERS, this.mContext, extras(paramNotificationRecord));
  }
  
  public boolean shouldIntercept(int paramInt, ZenModeConfig paramZenModeConfig, NotificationRecord paramNotificationRecord)
  {
    if (isSystem(paramNotificationRecord)) {
      return false;
    }
    switch (paramInt)
    {
    default: 
      return false;
    case 2: 
      ZenLog.traceIntercepted(paramNotificationRecord, "none");
      return true;
    case 3: 
      if (isAlarm(paramNotificationRecord))
      {
        if (Settings.System.getInt(this.mContext.getContentResolver(), "oem_zen_alarms_silent_switch", 0) != 1)
        {
          if (DEBUG) {
            Slog.d("ZenModeHelper", " Alarms only mode!!! ");
          }
          return false;
        }
        if (DEBUG) {
          Slog.d("ZenModeHelper", " Alarms only mode (Silent Mode enabled)!!! ");
        }
        return true;
      }
      ZenLog.traceIntercepted(paramNotificationRecord, "alarmsOnly");
      return true;
    }
    if (isAlarm(paramNotificationRecord))
    {
      if (Settings.System.getInt(this.mContext.getContentResolver(), "oem_zen_alarms_priority_switch", 0) != 1)
      {
        if (DEBUG) {
          Slog.d("ZenModeHelper", " Priority interuptions only mode!!! ");
        }
        return false;
      }
      if (DEBUG) {
        Slog.d("ZenModeHelper", " Priority interuptions only mode(Silent Mode enabled)!!! ");
      }
      return true;
    }
    if (paramNotificationRecord.getPackagePriority() == 2)
    {
      ZenLog.traceNotIntercepted(paramNotificationRecord, "priorityApp");
      return false;
    }
    if (isCall(paramNotificationRecord))
    {
      if ((paramZenModeConfig.allowRepeatCallers) && (RepeatCallers.-wrap0(REPEAT_CALLERS, this.mContext, extras(paramNotificationRecord))))
      {
        ZenLog.traceNotIntercepted(paramNotificationRecord, "repeatCaller");
        return false;
      }
      if (!paramZenModeConfig.allowCalls)
      {
        ZenLog.traceIntercepted(paramNotificationRecord, "!allowCalls");
        return true;
      }
      return shouldInterceptAudience(paramZenModeConfig.allowCallsFrom, paramNotificationRecord);
    }
    if (isMessage(paramNotificationRecord))
    {
      if (!paramZenModeConfig.allowMessages)
      {
        ZenLog.traceIntercepted(paramNotificationRecord, "!allowMessages");
        return true;
      }
      return shouldInterceptAudience(paramZenModeConfig.allowMessagesFrom, paramNotificationRecord);
    }
    if (isEvent(paramNotificationRecord))
    {
      if (!paramZenModeConfig.allowEvents)
      {
        ZenLog.traceIntercepted(paramNotificationRecord, "!allowEvents");
        return true;
      }
      return false;
    }
    if (isReminder(paramNotificationRecord))
    {
      if (!paramZenModeConfig.allowReminders)
      {
        ZenLog.traceIntercepted(paramNotificationRecord, "!allowReminders");
        return true;
      }
      return false;
    }
    ZenLog.traceIntercepted(paramNotificationRecord, "!priority");
    return true;
  }
  
  private static class RepeatCallers
  {
    private final ArrayMap<String, Long> mCalls = new ArrayMap();
    private int mThresholdMinutes;
    
    private void cleanUp(ArrayMap<String, Long> paramArrayMap, long paramLong)
    {
      try
      {
        int i = paramArrayMap.size() - 1;
        while (i >= 0)
        {
          long l = ((Long)this.mCalls.valueAt(i)).longValue();
          if ((l > paramLong) || (paramLong - l > this.mThresholdMinutes * 1000 * 60)) {
            paramArrayMap.removeAt(i);
          }
          i -= 1;
        }
        return;
      }
      finally {}
    }
    
    private boolean isRepeat(Context paramContext, Bundle paramBundle)
    {
      try
      {
        setThresholdMinutes(paramContext);
        int i = this.mThresholdMinutes;
        if ((i <= 0) || (paramBundle == null)) {
          return false;
        }
        paramContext = peopleString(paramBundle);
        if (paramContext == null) {
          return false;
        }
        long l = System.currentTimeMillis();
        cleanUp(this.mCalls, l);
        boolean bool = this.mCalls.containsKey(paramContext);
        return bool;
      }
      finally {}
    }
    
    private static String peopleString(Bundle paramBundle)
    {
      paramBundle = ValidateNotificationPeople.getExtraPeople(paramBundle);
      if ((paramBundle == null) || (paramBundle.length == 0)) {
        return null;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      int i = 0;
      if (i < paramBundle.length)
      {
        String str = paramBundle[i];
        if (str == null) {}
        for (;;)
        {
          i += 1;
          break;
          str = str.trim();
          if (!str.isEmpty())
          {
            if (localStringBuilder.length() > 0) {
              localStringBuilder.append('|');
            }
            localStringBuilder.append(str);
          }
        }
      }
      if (localStringBuilder.length() == 0) {
        return null;
      }
      return localStringBuilder.toString();
    }
    
    private void recordCall(Context paramContext, Bundle paramBundle)
    {
      try
      {
        setThresholdMinutes(paramContext);
        int i = this.mThresholdMinutes;
        if ((i <= 0) || (paramBundle == null)) {
          return;
        }
        paramContext = peopleString(paramBundle);
        if (paramContext == null) {
          return;
        }
        long l = System.currentTimeMillis();
        cleanUp(this.mCalls, l);
        this.mCalls.put(paramContext, Long.valueOf(l));
        return;
      }
      finally {}
    }
    
    private void setThresholdMinutes(Context paramContext)
    {
      if (this.mThresholdMinutes <= 0) {
        this.mThresholdMinutes = paramContext.getResources().getInteger(17694874);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/ZenModeFiltering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */