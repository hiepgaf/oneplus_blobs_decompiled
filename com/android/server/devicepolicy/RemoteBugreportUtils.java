package com.android.server.devicepolicy;

import android.annotation.IntDef;
import android.app.Notification;
import android.app.Notification.Action.Builder;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class RemoteBugreportUtils
{
  static final String BUGREPORT_MIMETYPE = "application/vnd.android.bugreport";
  static final String CTL_STOP = "ctl.stop";
  static final int NOTIFICATION_ID = 678432343;
  static final String REMOTE_BUGREPORT_SERVICE = "bugreportremote";
  static final long REMOTE_BUGREPORT_TIMEOUT_MILLIS = 600000L;
  
  static Notification buildNotification(Context paramContext, int paramInt)
  {
    Object localObject = new Intent("android.settings.SHOW_REMOTE_BUGREPORT_DIALOG");
    ((Intent)localObject).addFlags(268468224);
    ((Intent)localObject).putExtra("android.app.extra.bugreport_notification_type", paramInt);
    localObject = PendingIntent.getActivityAsUser(paramContext, paramInt, (Intent)localObject, 0, null, UserHandle.CURRENT);
    localObject = new Notification.Builder(paramContext).setSmallIcon(17303263).setOngoing(true).setLocalOnly(true).setPriority(1).setContentIntent((PendingIntent)localObject).setColor(paramContext.getColor(17170523));
    if (paramInt == 2) {
      ((Notification.Builder)localObject).setContentTitle(paramContext.getString(17040437)).setProgress(0, 0, true);
    }
    for (;;)
    {
      return ((Notification.Builder)localObject).build();
      if (paramInt == 1)
      {
        ((Notification.Builder)localObject).setContentTitle(paramContext.getString(17040435)).setProgress(0, 0, true);
      }
      else if (paramInt == 3)
      {
        PendingIntent localPendingIntent1 = PendingIntent.getBroadcast(paramContext, 678432343, new Intent("com.android.server.action.BUGREPORT_SHARING_ACCEPTED"), 268435456);
        PendingIntent localPendingIntent2 = PendingIntent.getBroadcast(paramContext, 678432343, new Intent("com.android.server.action.BUGREPORT_SHARING_DECLINED"), 268435456);
        ((Notification.Builder)localObject).addAction(new Notification.Action.Builder(null, paramContext.getString(17040440), localPendingIntent2).build()).addAction(new Notification.Action.Builder(null, paramContext.getString(17040439), localPendingIntent1).build()).setContentTitle(paramContext.getString(17040436)).setContentText(paramContext.getString(17040438)).setStyle(new Notification.BigTextStyle().bigText(paramContext.getString(17040438)));
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({1L, 2L, 3L})
  static @interface RemoteBugreportNotificationType {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/devicepolicy/RemoteBugreportUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */