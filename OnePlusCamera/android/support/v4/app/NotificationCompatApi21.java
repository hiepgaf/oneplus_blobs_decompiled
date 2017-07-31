package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.RemoteInput.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;

class NotificationCompatApi21
{
  public static final String CATEGORY_ALARM = "alarm";
  public static final String CATEGORY_CALL = "call";
  public static final String CATEGORY_EMAIL = "email";
  public static final String CATEGORY_ERROR = "err";
  public static final String CATEGORY_EVENT = "event";
  public static final String CATEGORY_MESSAGE = "msg";
  public static final String CATEGORY_PROGRESS = "progress";
  public static final String CATEGORY_PROMO = "promo";
  public static final String CATEGORY_RECOMMENDATION = "recommendation";
  public static final String CATEGORY_SERVICE = "service";
  public static final String CATEGORY_SOCIAL = "social";
  public static final String CATEGORY_STATUS = "status";
  public static final String CATEGORY_SYSTEM = "sys";
  public static final String CATEGORY_TRANSPORT = "transport";
  private static final String KEY_AUTHOR = "author";
  private static final String KEY_MESSAGES = "messages";
  private static final String KEY_ON_READ = "on_read";
  private static final String KEY_ON_REPLY = "on_reply";
  private static final String KEY_PARTICIPANTS = "participants";
  private static final String KEY_REMOTE_INPUT = "remote_input";
  private static final String KEY_TEXT = "text";
  private static final String KEY_TIMESTAMP = "timestamp";
  
  private static RemoteInput fromCompatRemoteInput(RemoteInputCompatBase.RemoteInput paramRemoteInput)
  {
    return new RemoteInput.Builder(paramRemoteInput.getResultKey()).setLabel(paramRemoteInput.getLabel()).setChoices(paramRemoteInput.getChoices()).setAllowFreeFormInput(paramRemoteInput.getAllowFreeFormInput()).addExtras(paramRemoteInput.getExtras()).build();
  }
  
  static Bundle getBundleForUnreadConversation(NotificationCompatBase.UnreadConversation paramUnreadConversation)
  {
    int i = 0;
    Object localObject = null;
    Bundle localBundle1;
    Parcelable[] arrayOfParcelable;
    if (paramUnreadConversation != null)
    {
      localBundle1 = new Bundle();
      if (paramUnreadConversation.getParticipants() != null) {
        break label101;
      }
      arrayOfParcelable = new Parcelable[paramUnreadConversation.getMessages().length];
      label33:
      if (i < arrayOfParcelable.length) {
        break label120;
      }
      localBundle1.putParcelableArray("messages", arrayOfParcelable);
      localObject = paramUnreadConversation.getRemoteInput();
      if (localObject != null) {
        break label163;
      }
    }
    for (;;)
    {
      localBundle1.putParcelable("on_reply", paramUnreadConversation.getReplyPendingIntent());
      localBundle1.putParcelable("on_read", paramUnreadConversation.getReadPendingIntent());
      localBundle1.putStringArray("participants", paramUnreadConversation.getParticipants());
      localBundle1.putLong("timestamp", paramUnreadConversation.getLatestTimestamp());
      return localBundle1;
      return null;
      label101:
      if (paramUnreadConversation.getParticipants().length <= 1) {
        break;
      }
      localObject = paramUnreadConversation.getParticipants()[0];
      break;
      label120:
      Bundle localBundle2 = new Bundle();
      localBundle2.putString("text", paramUnreadConversation.getMessages()[i]);
      localBundle2.putString("author", (String)localObject);
      arrayOfParcelable[i] = localBundle2;
      i += 1;
      break label33;
      label163:
      localBundle1.putParcelable("remote_input", fromCompatRemoteInput((RemoteInputCompatBase.RemoteInput)localObject));
    }
  }
  
  public static String getCategory(Notification paramNotification)
  {
    return paramNotification.category;
  }
  
  static NotificationCompatBase.UnreadConversation getUnreadConversationFromBundle(Bundle paramBundle, NotificationCompatBase.UnreadConversation.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    int k = 0;
    Object localObject;
    String[] arrayOfString1;
    PendingIntent localPendingIntent;
    RemoteInput localRemoteInput;
    String[] arrayOfString2;
    if (paramBundle != null)
    {
      localObject = paramBundle.getParcelableArray("messages");
      if (localObject != null) {
        break label73;
      }
      arrayOfString1 = null;
      localObject = (PendingIntent)paramBundle.getParcelable("on_read");
      localPendingIntent = (PendingIntent)paramBundle.getParcelable("on_reply");
      localRemoteInput = (RemoteInput)paramBundle.getParcelable("remote_input");
      arrayOfString2 = paramBundle.getStringArray("participants");
      if (arrayOfString2 != null) {
        break label151;
      }
    }
    label73:
    label151:
    while (arrayOfString2.length != 1)
    {
      return null;
      return null;
      arrayOfString1 = new String[localObject.length];
      int i = 0;
      for (;;)
      {
        int j;
        if (i >= arrayOfString1.length) {
          j = 1;
        }
        do
        {
          do
          {
            if (j != 0) {
              break;
            }
            return null;
            j = k;
          } while (!(localObject[i] instanceof Bundle));
          arrayOfString1[i] = ((Bundle)localObject[i]).getString("text");
          j = k;
        } while (arrayOfString1[i] == null);
        i += 1;
      }
      break;
    }
    if (localRemoteInput == null) {}
    for (paramFactory1 = null;; paramFactory1 = toCompatRemoteInput(localRemoteInput, paramFactory1)) {
      return paramFactory.build(arrayOfString1, paramFactory1, localPendingIntent, (PendingIntent)localObject, arrayOfString2, paramBundle.getLong("timestamp"));
    }
  }
  
  private static RemoteInputCompatBase.RemoteInput toCompatRemoteInput(RemoteInput paramRemoteInput, RemoteInputCompatBase.RemoteInput.Factory paramFactory)
  {
    return paramFactory.build(paramRemoteInput.getResultKey(), paramRemoteInput.getLabel(), paramRemoteInput.getChoices(), paramRemoteInput.getAllowFreeFormInput(), paramRemoteInput.getExtras());
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    
    public Builder(Context paramContext, Notification paramNotification1, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean4, String paramString1, ArrayList<String> paramArrayList, Bundle paramBundle, int paramInt5, int paramInt6, Notification paramNotification2, String paramString2, boolean paramBoolean5, String paramString3)
    {
      paramContext = new Notification.Builder(paramContext).setWhen(paramNotification1.when).setShowWhen(paramBoolean2).setSmallIcon(paramNotification1.icon, paramNotification1.iconLevel).setContent(paramNotification1.contentView).setTicker(paramNotification1.tickerText, paramRemoteViews).setSound(paramNotification1.sound, paramNotification1.audioStreamType).setVibrate(paramNotification1.vibrate).setLights(paramNotification1.ledARGB, paramNotification1.ledOnMS, paramNotification1.ledOffMS);
      if ((paramNotification1.flags & 0x2) == 0)
      {
        paramBoolean2 = false;
        paramContext = paramContext.setOngoing(paramBoolean2);
        if ((paramNotification1.flags & 0x8) != 0) {
          break label304;
        }
        paramBoolean2 = false;
        label117:
        paramContext = paramContext.setOnlyAlertOnce(paramBoolean2);
        if ((paramNotification1.flags & 0x10) != 0) {
          break label310;
        }
        paramBoolean2 = false;
        label137:
        paramContext = paramContext.setAutoCancel(paramBoolean2).setDefaults(paramNotification1.defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(paramNotification1.deleteIntent);
        if ((paramNotification1.flags & 0x80) != 0) {
          break label316;
        }
        paramBoolean2 = false;
        label196:
        this.b = paramContext.setFullScreenIntent(paramPendingIntent2, paramBoolean2).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean3).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1).setLocalOnly(paramBoolean4).setExtras(paramBundle).setGroup(paramString2).setGroupSummary(paramBoolean5).setSortKey(paramString3).setCategory(paramString1).setColor(paramInt5).setVisibility(paramInt6).setPublicVersion(paramNotification2);
        paramContext = paramArrayList.iterator();
      }
      for (;;)
      {
        if (!paramContext.hasNext())
        {
          return;
          paramBoolean2 = true;
          break;
          label304:
          paramBoolean2 = true;
          break label117;
          label310:
          paramBoolean2 = true;
          break label137;
          label316:
          paramBoolean2 = true;
          break label196;
        }
        paramNotification1 = (String)paramContext.next();
        this.b.addPerson(paramNotification1);
      }
    }
    
    public void addAction(NotificationCompatBase.Action paramAction)
    {
      NotificationCompatApi20.addAction(this.b, paramAction);
    }
    
    public Notification build()
    {
      return this.b.build();
    }
    
    public Notification.Builder getBuilder()
    {
      return this.b;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/NotificationCompatApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */