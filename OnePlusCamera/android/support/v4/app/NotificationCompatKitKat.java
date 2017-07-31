package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;

class NotificationCompatKitKat
{
  public static NotificationCompatBase.Action getAction(Notification paramNotification, int paramInt, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    Object localObject = null;
    Notification.Action localAction = paramNotification.actions[paramInt];
    paramNotification = paramNotification.extras.getSparseParcelableArray("android.support.actionExtras");
    if (paramNotification == null) {}
    for (paramNotification = (Notification)localObject;; paramNotification = (Bundle)paramNotification.get(paramInt)) {
      return NotificationCompatJellybean.readAction(paramFactory, paramFactory1, localAction.icon, localAction.title, localAction.actionIntent, paramNotification);
    }
  }
  
  public static int getActionCount(Notification paramNotification)
  {
    if (paramNotification.actions == null) {
      return 0;
    }
    return paramNotification.actions.length;
  }
  
  public static Bundle getExtras(Notification paramNotification)
  {
    return paramNotification.extras;
  }
  
  public static String getGroup(Notification paramNotification)
  {
    return paramNotification.extras.getString("android.support.groupKey");
  }
  
  public static boolean getLocalOnly(Notification paramNotification)
  {
    return paramNotification.extras.getBoolean("android.support.localOnly");
  }
  
  public static String getSortKey(Notification paramNotification)
  {
    return paramNotification.extras.getString("android.support.sortKey");
  }
  
  public static boolean isGroupSummary(Notification paramNotification)
  {
    return paramNotification.extras.getBoolean("android.support.isGroupSummary");
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    private List<Bundle> mActionExtrasList = new ArrayList();
    private Bundle mExtras;
    
    public Builder(Context paramContext, Notification paramNotification, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean4, ArrayList<String> paramArrayList, Bundle paramBundle, String paramString1, boolean paramBoolean5, String paramString2)
    {
      paramContext = new Notification.Builder(paramContext).setWhen(paramNotification.when).setShowWhen(paramBoolean2).setSmallIcon(paramNotification.icon, paramNotification.iconLevel).setContent(paramNotification.contentView).setTicker(paramNotification.tickerText, paramRemoteViews).setSound(paramNotification.sound, paramNotification.audioStreamType).setVibrate(paramNotification.vibrate).setLights(paramNotification.ledARGB, paramNotification.ledOnMS, paramNotification.ledOffMS);
      if ((paramNotification.flags & 0x2) == 0)
      {
        paramBoolean2 = false;
        paramContext = paramContext.setOngoing(paramBoolean2);
        if ((paramNotification.flags & 0x8) != 0) {
          break label291;
        }
        paramBoolean2 = false;
        label128:
        paramContext = paramContext.setOnlyAlertOnce(paramBoolean2);
        if ((paramNotification.flags & 0x10) != 0) {
          break label297;
        }
        paramBoolean2 = false;
        label148:
        paramContext = paramContext.setAutoCancel(paramBoolean2).setDefaults(paramNotification.defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(paramNotification.deleteIntent);
        if ((paramNotification.flags & 0x80) != 0) {
          break label303;
        }
        paramBoolean2 = false;
        label207:
        this.b = paramContext.setFullScreenIntent(paramPendingIntent2, paramBoolean2).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean3).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1);
        this.mExtras = new Bundle();
        if (paramBundle != null) {
          break label309;
        }
        label264:
        if (paramArrayList != null) {
          break label321;
        }
        label269:
        if (paramBoolean4) {
          break label357;
        }
        label274:
        if (paramString1 != null) {
          break label370;
        }
      }
      for (;;)
      {
        if (paramString2 != null) {
          break label412;
        }
        return;
        paramBoolean2 = true;
        break;
        label291:
        paramBoolean2 = true;
        break label128;
        label297:
        paramBoolean2 = true;
        break label148;
        label303:
        paramBoolean2 = true;
        break label207;
        label309:
        this.mExtras.putAll(paramBundle);
        break label264;
        label321:
        if (paramArrayList.isEmpty()) {
          break label269;
        }
        this.mExtras.putStringArray("android.people", (String[])paramArrayList.toArray(new String[paramArrayList.size()]));
        break label269;
        label357:
        this.mExtras.putBoolean("android.support.localOnly", true);
        break label274;
        label370:
        this.mExtras.putString("android.support.groupKey", paramString1);
        if (!paramBoolean5) {
          this.mExtras.putBoolean("android.support.useSideChannel", true);
        } else {
          this.mExtras.putBoolean("android.support.isGroupSummary", true);
        }
      }
      label412:
      this.mExtras.putString("android.support.sortKey", paramString2);
    }
    
    public void addAction(NotificationCompatBase.Action paramAction)
    {
      this.mActionExtrasList.add(NotificationCompatJellybean.writeActionAndGetExtras(this.b, paramAction));
    }
    
    public Notification build()
    {
      SparseArray localSparseArray = NotificationCompatJellybean.buildActionExtrasMap(this.mActionExtrasList);
      if (localSparseArray == null) {}
      for (;;)
      {
        this.b.setExtras(this.mExtras);
        return this.b.build();
        this.mExtras.putSparseParcelableArray("android.support.actionExtras", localSparseArray);
      }
    }
    
    public Notification.Builder getBuilder()
    {
      return this.b;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/NotificationCompatKitKat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */