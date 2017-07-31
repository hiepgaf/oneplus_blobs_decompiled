package com.android.server.notification;

import android.content.ComponentName;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.ZenModeConfig;
import android.util.Slog;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ZenLog
{
  private static final boolean DEBUG = Build.IS_DEBUGGABLE;
  private static final SimpleDateFormat FORMAT;
  private static final String[] MSGS;
  private static final int SIZE;
  private static final String TAG = "ZenLog";
  private static final long[] TIMES;
  private static final int[] TYPES;
  private static final int TYPE_ALLOW_DISABLE = 2;
  private static final int TYPE_CONFIG = 11;
  private static final int TYPE_DISABLE_EFFECTS = 13;
  private static final int TYPE_DOWNTIME = 5;
  private static final int TYPE_EXIT_CONDITION = 8;
  private static final int TYPE_INTERCEPTED = 1;
  private static final int TYPE_LISTENER_HINTS_CHANGED = 15;
  private static final int TYPE_NOT_INTERCEPTED = 12;
  private static final int TYPE_SET_RINGER_MODE_EXTERNAL = 3;
  private static final int TYPE_SET_RINGER_MODE_INTERNAL = 4;
  private static final int TYPE_SET_ZEN_MODE = 6;
  private static final int TYPE_SUBSCRIBE = 9;
  private static final int TYPE_SUPPRESSOR_CHANGED = 14;
  private static final int TYPE_UNSUBSCRIBE = 10;
  private static final int TYPE_UPDATE_ZEN_MODE = 7;
  private static int sNext;
  private static int sSize;
  
  static
  {
    if (Build.IS_DEBUGGABLE) {}
    for (int i = 100;; i = 20)
    {
      SIZE = i;
      TIMES = new long[SIZE];
      TYPES = new int[SIZE];
      MSGS = new String[SIZE];
      FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
      return;
    }
  }
  
  private static void append(int paramInt, String paramString)
  {
    synchronized (MSGS)
    {
      TIMES[sNext] = System.currentTimeMillis();
      TYPES[sNext] = paramInt;
      MSGS[sNext] = paramString;
      sNext = (sNext + 1) % SIZE;
      if (sSize < SIZE) {
        sSize += 1;
      }
      if (DEBUG) {
        Slog.d("ZenLog", typeToString(paramInt) + ": " + paramString);
      }
      return;
    }
  }
  
  private static String componentListToString(List<ComponentName> paramList)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramList.size())
    {
      if (i > 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(componentToString((ComponentName)paramList.get(i)));
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private static String componentToString(ComponentName paramComponentName)
  {
    String str = null;
    if (paramComponentName != null) {
      str = paramComponentName.toShortString();
    }
    return str;
  }
  
  public static void dump(PrintWriter paramPrintWriter, String paramString)
  {
    synchronized (MSGS)
    {
      int j = sNext;
      int k = sSize;
      int m = SIZE;
      int n = SIZE;
      int i = 0;
      while (i < sSize)
      {
        int i1 = ((j - k + m) % n + i) % SIZE;
        paramPrintWriter.print(paramString);
        paramPrintWriter.print(FORMAT.format(new Date(TIMES[i1])));
        paramPrintWriter.print(' ');
        paramPrintWriter.print(typeToString(TYPES[i1]));
        paramPrintWriter.print(": ");
        paramPrintWriter.println(MSGS[i1]);
        i += 1;
      }
      return;
    }
  }
  
  private static String hintsToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "none";
    }
    return "disable_effects";
  }
  
  private static String ringerModeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown";
    case 0: 
      return "silent";
    case 1: 
      return "vibrate";
    }
    return "normal";
  }
  
  private static String subscribeResult(IConditionProvider paramIConditionProvider, RemoteException paramRemoteException)
  {
    if (paramIConditionProvider == null) {
      return "no provider";
    }
    if (paramRemoteException != null) {
      return paramRemoteException.getMessage();
    }
    return "ok";
  }
  
  public static void traceConfig(String paramString, ZenModeConfig paramZenModeConfig1, ZenModeConfig paramZenModeConfig2)
  {
    Object localObject = null;
    StringBuilder localStringBuilder = new StringBuilder().append(paramString).append(",");
    paramString = (String)localObject;
    if (paramZenModeConfig2 != null) {
      paramString = paramZenModeConfig2.toString();
    }
    append(11, paramString + "," + ZenModeConfig.diff(paramZenModeConfig1, paramZenModeConfig2));
  }
  
  public static void traceDisableEffects(NotificationRecord paramNotificationRecord, String paramString)
  {
    append(13, paramNotificationRecord.getKey() + "," + paramString);
  }
  
  public static void traceDowntimeAutotrigger(String paramString)
  {
    append(5, paramString);
  }
  
  public static void traceEffectsSuppressorChanged(List<ComponentName> paramList1, List<ComponentName> paramList2, long paramLong)
  {
    append(14, "suppressed effects:" + paramLong + "," + componentListToString(paramList1) + "->" + componentListToString(paramList2));
  }
  
  public static void traceExitCondition(Condition paramCondition, ComponentName paramComponentName, String paramString)
  {
    append(8, paramCondition + "," + componentToString(paramComponentName) + "," + paramString);
  }
  
  public static void traceIntercepted(NotificationRecord paramNotificationRecord, String paramString)
  {
    if ((paramNotificationRecord != null) && (paramNotificationRecord.isIntercepted())) {
      return;
    }
    append(1, paramNotificationRecord.getKey() + "," + paramString);
  }
  
  public static void traceListenerHintsChanged(int paramInt1, int paramInt2, int paramInt3)
  {
    append(15, hintsToString(paramInt1) + "->" + hintsToString(paramInt2) + ",listeners=" + paramInt3);
  }
  
  public static void traceNotIntercepted(NotificationRecord paramNotificationRecord, String paramString)
  {
    if ((paramNotificationRecord != null) && (paramNotificationRecord.isUpdate)) {
      return;
    }
    append(12, paramNotificationRecord.getKey() + "," + paramString);
  }
  
  public static void traceSetRingerModeExternal(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4)
  {
    append(3, paramString + ",e:" + ringerModeToString(paramInt1) + "->" + ringerModeToString(paramInt2) + ",i:" + ringerModeToString(paramInt3) + "->" + ringerModeToString(paramInt4));
  }
  
  public static void traceSetRingerModeInternal(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4)
  {
    append(4, paramString + ",i:" + ringerModeToString(paramInt1) + "->" + ringerModeToString(paramInt2) + ",e:" + ringerModeToString(paramInt3) + "->" + ringerModeToString(paramInt4));
  }
  
  public static void traceSetZenMode(int paramInt, String paramString)
  {
    append(6, zenModeToString(paramInt) + "," + paramString);
  }
  
  public static void traceSubscribe(Uri paramUri, IConditionProvider paramIConditionProvider, RemoteException paramRemoteException)
  {
    append(9, paramUri + "," + subscribeResult(paramIConditionProvider, paramRemoteException));
  }
  
  public static void traceUnsubscribe(Uri paramUri, IConditionProvider paramIConditionProvider, RemoteException paramRemoteException)
  {
    append(10, paramUri + "," + subscribeResult(paramIConditionProvider, paramRemoteException));
  }
  
  public static void traceUpdateZenMode(int paramInt1, int paramInt2)
  {
    append(7, zenModeToString(paramInt1) + " -> " + zenModeToString(paramInt2));
  }
  
  private static String typeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown";
    case 1: 
      return "intercepted";
    case 2: 
      return "allow_disable";
    case 3: 
      return "set_ringer_mode_external";
    case 4: 
      return "set_ringer_mode_internal";
    case 5: 
      return "downtime";
    case 6: 
      return "set_zen_mode";
    case 7: 
      return "update_zen_mode";
    case 8: 
      return "exit_condition";
    case 9: 
      return "subscribe";
    case 10: 
      return "unsubscribe";
    case 11: 
      return "config";
    case 12: 
      return "not_intercepted";
    case 13: 
      return "disable_effects";
    case 14: 
      return "suppressor_changed";
    }
    return "listener_hints_changed";
  }
  
  private static String zenModeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown";
    case 0: 
      return "off";
    case 1: 
      return "important_interruptions";
    case 3: 
      return "alarms";
    }
    return "no_interruptions";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/ZenLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */