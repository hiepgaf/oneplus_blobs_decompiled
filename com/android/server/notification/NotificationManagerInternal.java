package com.android.server.notification;

import android.app.Notification;

public abstract interface NotificationManagerInternal
{
  public abstract void enqueueNotification(String paramString1, String paramString2, int paramInt1, int paramInt2, String paramString3, int paramInt3, Notification paramNotification, int[] paramArrayOfInt, int paramInt4);
  
  public abstract void removeForegroundServiceFlagFromNotification(String paramString, int paramInt1, int paramInt2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/NotificationManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */