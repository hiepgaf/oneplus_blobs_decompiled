package com.android.server.connectivity;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.util.MessageUtils;
import java.util.HashMap;

public class LingerMonitor
{
  public static final Intent CELLULAR_SETTINGS = new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
  private static final boolean DBG = true;
  public static final int DEFAULT_NOTIFICATION_DAILY_LIMIT = 3;
  public static final long DEFAULT_NOTIFICATION_RATE_LIMIT_MILLIS = 60000L;
  public static final int NOTIFY_TYPE_NONE = 0;
  public static final int NOTIFY_TYPE_NOTIFICATION = 1;
  public static final int NOTIFY_TYPE_TOAST = 2;
  private static final String TAG = LingerMonitor.class.getSimpleName();
  private static final HashMap<String, Integer> TRANSPORT_NAMES = makeTransportToNameMap();
  private static final boolean VDBG = false;
  private static SparseArray<String> sNotifyTypeNames = MessageUtils.findMessageNames(new Class[] { LingerMonitor.class }, new String[] { "NOTIFY_TYPE_" });
  private final Context mContext;
  private final int mDailyLimit;
  private final SparseBooleanArray mEverNotified = new SparseBooleanArray();
  private long mFirstNotificationMillis;
  private long mLastNotificationMillis;
  private int mNotificationCounter;
  private final SparseIntArray mNotifications = new SparseIntArray();
  private final NetworkNotificationManager mNotifier;
  private final long mRateLimitMillis;
  
  public LingerMonitor(Context paramContext, NetworkNotificationManager paramNetworkNotificationManager, int paramInt, long paramLong)
  {
    this.mContext = paramContext;
    this.mNotifier = paramNetworkNotificationManager;
    this.mDailyLimit = paramInt;
    this.mRateLimitMillis = paramLong;
  }
  
  private boolean everNotified(NetworkAgentInfo paramNetworkAgentInfo)
  {
    return this.mEverNotified.get(paramNetworkAgentInfo.network.netId, false);
  }
  
  private int getNotificationSource(NetworkAgentInfo paramNetworkAgentInfo)
  {
    int i = 0;
    while (i < this.mNotifications.size())
    {
      if (this.mNotifications.valueAt(i) == paramNetworkAgentInfo.network.netId) {
        return this.mNotifications.keyAt(i);
      }
      i += 1;
    }
    return 0;
  }
  
  private static boolean hasTransport(NetworkAgentInfo paramNetworkAgentInfo, int paramInt)
  {
    return paramNetworkAgentInfo.networkCapabilities.hasTransport(paramInt);
  }
  
  private boolean isAboveDailyLimit(long paramLong)
  {
    if (this.mFirstNotificationMillis == 0L) {
      this.mFirstNotificationMillis = paramLong;
    }
    if (paramLong - this.mFirstNotificationMillis > 86400000L)
    {
      this.mNotificationCounter = 0;
      this.mFirstNotificationMillis = 0L;
    }
    if (this.mNotificationCounter >= this.mDailyLimit) {
      return true;
    }
    this.mNotificationCounter += 1;
    return false;
  }
  
  private boolean isRateLimited(long paramLong)
  {
    if (paramLong - this.mLastNotificationMillis < this.mRateLimitMillis) {
      return true;
    }
    this.mLastNotificationMillis = paramLong;
    return false;
  }
  
  private static HashMap<String, Integer> makeTransportToNameMap()
  {
    SparseArray localSparseArray = MessageUtils.findMessageNames(new Class[] { NetworkCapabilities.class }, new String[] { "TRANSPORT_" });
    HashMap localHashMap = new HashMap();
    int i = 0;
    while (i < localSparseArray.size())
    {
      localHashMap.put((String)localSparseArray.valueAt(i), Integer.valueOf(localSparseArray.keyAt(i)));
      i += 1;
    }
    return localHashMap;
  }
  
  private void maybeStopNotifying(NetworkAgentInfo paramNetworkAgentInfo)
  {
    int i = getNotificationSource(paramNetworkAgentInfo);
    if (i != 0)
    {
      this.mNotifications.delete(i);
      this.mNotifier.clearNotification(i);
    }
  }
  
  private void notify(NetworkAgentInfo paramNetworkAgentInfo1, NetworkAgentInfo paramNetworkAgentInfo2, boolean paramBoolean)
  {
    int j = this.mContext.getResources().getInteger(17694736);
    int i = j;
    if (j == 1)
    {
      i = j;
      if (paramBoolean) {
        i = 2;
      }
    }
    switch (i)
    {
    default: 
      Log.e(TAG, "Unknown notify type " + i);
      return;
    case 0: 
      return;
    case 1: 
      showNotification(paramNetworkAgentInfo1, paramNetworkAgentInfo2);
    }
    for (;;)
    {
      Log.d(TAG, "Notifying switch from=" + paramNetworkAgentInfo1.name() + " to=" + paramNetworkAgentInfo2.name() + " type=" + (String)sNotifyTypeNames.get(i, new StringBuilder().append("unknown(").append(i).append(")").toString()));
      this.mNotifications.put(paramNetworkAgentInfo1.network.netId, paramNetworkAgentInfo2.network.netId);
      this.mEverNotified.put(paramNetworkAgentInfo1.network.netId, true);
      return;
      this.mNotifier.showToast(paramNetworkAgentInfo1, paramNetworkAgentInfo2);
    }
  }
  
  private void showNotification(NetworkAgentInfo paramNetworkAgentInfo1, NetworkAgentInfo paramNetworkAgentInfo2)
  {
    this.mNotifier.showNotification(paramNetworkAgentInfo1.network.netId, NetworkNotificationManager.NotificationType.NETWORK_SWITCH, paramNetworkAgentInfo1, paramNetworkAgentInfo2, createNotificationIntent(), true);
  }
  
  protected PendingIntent createNotificationIntent()
  {
    return PendingIntent.getActivityAsUser(this.mContext, 0, CELLULAR_SETTINGS, 268435456, null, UserHandle.CURRENT);
  }
  
  public boolean isNotificationEnabled(NetworkAgentInfo paramNetworkAgentInfo1, NetworkAgentInfo paramNetworkAgentInfo2)
  {
    String[] arrayOfString1 = this.mContext.getResources().getStringArray(17235988);
    int j = arrayOfString1.length;
    int i = 0;
    if (i < j)
    {
      String str = arrayOfString1[i];
      if (TextUtils.isEmpty(str)) {}
      label98:
      int k;
      int m;
      do
      {
        String[] arrayOfString2;
        for (;;)
        {
          i += 1;
          break;
          arrayOfString2 = str.split("-", 2);
          if (arrayOfString2.length == 2) {
            break label98;
          }
          Log.e(TAG, "Invalid network switch notification configuration: " + str);
        }
        k = ((Integer)TRANSPORT_NAMES.get("TRANSPORT_" + arrayOfString2[0])).intValue();
        m = ((Integer)TRANSPORT_NAMES.get("TRANSPORT_" + arrayOfString2[1])).intValue();
      } while ((!hasTransport(paramNetworkAgentInfo1, k)) || (!hasTransport(paramNetworkAgentInfo2, m)));
      return true;
    }
    return false;
  }
  
  public void noteDisconnect(NetworkAgentInfo paramNetworkAgentInfo)
  {
    this.mNotifications.delete(paramNetworkAgentInfo.network.netId);
    this.mEverNotified.delete(paramNetworkAgentInfo.network.netId);
    maybeStopNotifying(paramNetworkAgentInfo);
  }
  
  public void noteLingerDefaultNetwork(NetworkAgentInfo paramNetworkAgentInfo1, NetworkAgentInfo paramNetworkAgentInfo2)
  {
    maybeStopNotifying(paramNetworkAgentInfo1);
    if (!paramNetworkAgentInfo1.everValidated) {
      return;
    }
    boolean bool = paramNetworkAgentInfo1.networkCapabilities.hasCapability(17);
    if (everNotified(paramNetworkAgentInfo1)) {
      return;
    }
    if (paramNetworkAgentInfo1.lastValidated) {
      return;
    }
    if (!isNotificationEnabled(paramNetworkAgentInfo1, paramNetworkAgentInfo2)) {
      return;
    }
    long l = SystemClock.elapsedRealtime();
    if ((isRateLimited(l)) || (isAboveDailyLimit(l))) {
      return;
    }
    notify(paramNetworkAgentInfo1, paramNetworkAgentInfo2, bool);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/LingerMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */