package com.android.server.notification;

import android.content.Context;

public abstract interface NotificationSignalExtractor
{
  public abstract void initialize(Context paramContext, NotificationUsageStats paramNotificationUsageStats);
  
  public abstract RankingReconsideration process(NotificationRecord paramNotificationRecord);
  
  public abstract void setConfig(RankingConfig paramRankingConfig);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/NotificationSignalExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */