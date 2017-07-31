package com.android.server.notification;

import android.content.Context;
import android.service.notification.StatusBarNotification;

public class PriorityExtractor
  implements NotificationSignalExtractor
{
  private static final boolean DBG = false;
  private static final String TAG = "ImportantTopicExtractor";
  private RankingConfig mConfig;
  
  public void initialize(Context paramContext, NotificationUsageStats paramNotificationUsageStats) {}
  
  public RankingReconsideration process(NotificationRecord paramNotificationRecord)
  {
    if ((paramNotificationRecord == null) || (paramNotificationRecord.getNotification() == null)) {
      return null;
    }
    if (this.mConfig == null) {
      return null;
    }
    paramNotificationRecord.setPackagePriority(this.mConfig.getPriority(paramNotificationRecord.sbn.getPackageName(), paramNotificationRecord.sbn.getUid()));
    return null;
  }
  
  public void setConfig(RankingConfig paramRankingConfig)
  {
    this.mConfig = paramRankingConfig;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/PriorityExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */