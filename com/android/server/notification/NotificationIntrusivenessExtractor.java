package com.android.server.notification;

import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.util.Slog;

public class NotificationIntrusivenessExtractor
  implements NotificationSignalExtractor
{
  private static final boolean DBG = Log.isLoggable("IntrusivenessExtractor", 3);
  private static final long HANG_TIME_MS = 10000L;
  private static final String TAG = "IntrusivenessExtractor";
  
  public void initialize(Context paramContext, NotificationUsageStats paramNotificationUsageStats)
  {
    if (DBG) {
      Slog.d("IntrusivenessExtractor", "Initializing  " + getClass().getSimpleName() + ".");
    }
  }
  
  public RankingReconsideration process(NotificationRecord paramNotificationRecord)
  {
    if ((paramNotificationRecord == null) || (paramNotificationRecord.getNotification() == null))
    {
      if (DBG) {
        Slog.d("IntrusivenessExtractor", "skipping empty notification");
      }
      return null;
    }
    Notification localNotification;
    if (paramNotificationRecord.getImportance() >= 3)
    {
      localNotification = paramNotificationRecord.getNotification();
      if (((localNotification.defaults & 0x2) == 0) && (localNotification.vibrate == null)) {
        break label77;
      }
    }
    for (;;)
    {
      paramNotificationRecord.setRecentlyIntrusive(true);
      label77:
      do
      {
        new RankingReconsideration(paramNotificationRecord.getKey(), 10000L)
        {
          public void applyChangesLocked(NotificationRecord paramAnonymousNotificationRecord)
          {
            paramAnonymousNotificationRecord.setRecentlyIntrusive(false);
          }
          
          public void work() {}
        };
        if (((localNotification.defaults & 0x1) != 0) || (localNotification.sound != null)) {
          break;
        }
      } while (localNotification.fullScreenIntent == null);
    }
  }
  
  public void setConfig(RankingConfig paramRankingConfig) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/NotificationIntrusivenessExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */