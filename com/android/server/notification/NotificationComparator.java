package com.android.server.notification;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import java.util.Comparator;

public class NotificationComparator
  implements Comparator<NotificationRecord>
{
  public int compare(NotificationRecord paramNotificationRecord1, NotificationRecord paramNotificationRecord2)
  {
    int i = paramNotificationRecord1.getImportance();
    int j = paramNotificationRecord2.getImportance();
    if (i != j) {
      return Integer.compare(i, j) * -1;
    }
    i = paramNotificationRecord1.getPackagePriority();
    j = paramNotificationRecord2.getPackagePriority();
    if (i != j) {
      return Integer.compare(i, j) * -1;
    }
    i = paramNotificationRecord1.sbn.getNotification().priority;
    j = paramNotificationRecord2.sbn.getNotification().priority;
    if (i != j) {
      return Integer.compare(i, j) * -1;
    }
    float f1 = paramNotificationRecord1.getContactAffinity();
    float f2 = paramNotificationRecord2.getContactAffinity();
    if (f1 != f2) {
      return Float.compare(f1, f2) * -1;
    }
    return Long.compare(paramNotificationRecord1.getRankingTimeMs(), paramNotificationRecord2.getRankingTimeMs()) * -1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/NotificationComparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */