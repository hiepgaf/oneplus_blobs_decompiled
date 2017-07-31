package com.android.server.notification;

import java.util.Comparator;

public class GlobalSortKeyComparator
  implements Comparator<NotificationRecord>
{
  public int compare(NotificationRecord paramNotificationRecord1, NotificationRecord paramNotificationRecord2)
  {
    if (paramNotificationRecord1.getGlobalSortKey() == null) {
      throw new IllegalStateException("Missing left global sort key: " + paramNotificationRecord1);
    }
    if (paramNotificationRecord2.getGlobalSortKey() == null) {
      throw new IllegalStateException("Missing right global sort key: " + paramNotificationRecord2);
    }
    return paramNotificationRecord1.getGlobalSortKey().compareTo(paramNotificationRecord2.getGlobalSortKey());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/GlobalSortKeyComparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */