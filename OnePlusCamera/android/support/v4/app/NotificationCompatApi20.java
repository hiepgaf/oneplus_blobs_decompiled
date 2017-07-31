package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.Action.Builder;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.RemoteViews;
import java.util.ArrayList;

class NotificationCompatApi20
{
  public static void addAction(Notification.Builder paramBuilder, NotificationCompatBase.Action paramAction)
  {
    Notification.Action.Builder localBuilder = new Notification.Action.Builder(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent());
    if (paramAction.getRemoteInputs() == null) {
      if (paramAction.getExtras() != null) {
        break label83;
      }
    }
    for (;;)
    {
      paramBuilder.addAction(localBuilder.build());
      return;
      RemoteInput[] arrayOfRemoteInput = RemoteInputCompatApi20.fromCompat(paramAction.getRemoteInputs());
      int j = arrayOfRemoteInput.length;
      int i = 0;
      while (i < j)
      {
        localBuilder.addRemoteInput(arrayOfRemoteInput[i]);
        i += 1;
      }
      break;
      label83:
      localBuilder.addExtras(paramAction.getExtras());
    }
  }
  
  public static NotificationCompatBase.Action getAction(Notification paramNotification, int paramInt, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    return getActionCompatFromAction(paramNotification.actions[paramInt], paramFactory, paramFactory1);
  }
  
  private static NotificationCompatBase.Action getActionCompatFromAction(Notification.Action paramAction, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    paramFactory1 = RemoteInputCompatApi20.toCompat(paramAction.getRemoteInputs(), paramFactory1);
    return paramFactory.build(paramAction.icon, paramAction.title, paramAction.actionIntent, paramAction.getExtras(), paramFactory1);
  }
  
  private static Notification.Action getActionFromActionCompat(NotificationCompatBase.Action paramAction)
  {
    Notification.Action.Builder localBuilder = new Notification.Action.Builder(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent()).addExtras(paramAction.getExtras());
    paramAction = paramAction.getRemoteInputs();
    if (paramAction == null) {}
    for (;;)
    {
      return localBuilder.build();
      paramAction = RemoteInputCompatApi20.fromCompat(paramAction);
      int j = paramAction.length;
      int i = 0;
      while (i < j)
      {
        localBuilder.addRemoteInput(paramAction[i]);
        i += 1;
      }
    }
  }
  
  public static NotificationCompatBase.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    NotificationCompatBase.Action[] arrayOfAction;
    int i;
    if (paramArrayList != null)
    {
      arrayOfAction = paramFactory.newArray(paramArrayList.size());
      i = 0;
    }
    for (;;)
    {
      if (i >= arrayOfAction.length)
      {
        return arrayOfAction;
        return null;
      }
      arrayOfAction[i] = getActionCompatFromAction((Notification.Action)paramArrayList.get(i), paramFactory, paramFactory1);
      i += 1;
    }
  }
  
  public static String getGroup(Notification paramNotification)
  {
    return paramNotification.getGroup();
  }
  
  public static boolean getLocalOnly(Notification paramNotification)
  {
    return (paramNotification.flags & 0x100) != 0;
  }
  
  public static ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompatBase.Action[] paramArrayOfAction)
  {
    ArrayList localArrayList;
    int j;
    int i;
    if (paramArrayOfAction != null)
    {
      localArrayList = new ArrayList(paramArrayOfAction.length);
      j = paramArrayOfAction.length;
      i = 0;
    }
    for (;;)
    {
      if (i >= j)
      {
        return localArrayList;
        return null;
      }
      localArrayList.add(getActionFromActionCompat(paramArrayOfAction[i]));
      i += 1;
    }
  }
  
  public static String getSortKey(Notification paramNotification)
  {
    return paramNotification.getSortKey();
  }
  
  public static boolean isGroupSummary(Notification paramNotification)
  {
    return (paramNotification.flags & 0x200) != 0;
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    private Bundle mExtras;
    
    public Builder(Context paramContext, Notification paramNotification, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean4, ArrayList<String> paramArrayList, Bundle paramBundle, String paramString1, boolean paramBoolean5, String paramString2)
    {
      paramContext = new Notification.Builder(paramContext).setWhen(paramNotification.when).setShowWhen(paramBoolean2).setSmallIcon(paramNotification.icon, paramNotification.iconLevel).setContent(paramNotification.contentView).setTicker(paramNotification.tickerText, paramRemoteViews).setSound(paramNotification.sound, paramNotification.audioStreamType).setVibrate(paramNotification.vibrate).setLights(paramNotification.ledARGB, paramNotification.ledOnMS, paramNotification.ledOffMS);
      if ((paramNotification.flags & 0x2) == 0)
      {
        paramBoolean2 = false;
        paramContext = paramContext.setOngoing(paramBoolean2);
        if ((paramNotification.flags & 0x8) != 0) {
          break label285;
        }
        paramBoolean2 = false;
        label117:
        paramContext = paramContext.setOnlyAlertOnce(paramBoolean2);
        if ((paramNotification.flags & 0x10) != 0) {
          break label291;
        }
        paramBoolean2 = false;
        label137:
        paramContext = paramContext.setAutoCancel(paramBoolean2).setDefaults(paramNotification.defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(paramNotification.deleteIntent);
        if ((paramNotification.flags & 0x80) != 0) {
          break label297;
        }
        paramBoolean2 = false;
        label196:
        this.b = paramContext.setFullScreenIntent(paramPendingIntent2, paramBoolean2).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean3).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1).setLocalOnly(paramBoolean4).setGroup(paramString1).setGroupSummary(paramBoolean5).setSortKey(paramString2);
        this.mExtras = new Bundle();
        if (paramBundle != null) {
          break label303;
        }
        label273:
        if (paramArrayList != null) {
          break label315;
        }
      }
      label285:
      label291:
      label297:
      label303:
      label315:
      while (paramArrayList.isEmpty())
      {
        return;
        paramBoolean2 = true;
        break;
        paramBoolean2 = true;
        break label117;
        paramBoolean2 = true;
        break label137;
        paramBoolean2 = true;
        break label196;
        this.mExtras.putAll(paramBundle);
        break label273;
      }
      this.mExtras.putStringArray("android.people", (String[])paramArrayList.toArray(new String[paramArrayList.size()]));
    }
    
    public void addAction(NotificationCompatBase.Action paramAction)
    {
      NotificationCompatApi20.addAction(this.b, paramAction);
    }
    
    public Notification build()
    {
      this.b.setExtras(this.mExtras);
      return this.b.build();
    }
    
    public Notification.Builder getBuilder()
    {
      return this.b;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/NotificationCompatApi20.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */