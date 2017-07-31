package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.Notification.InboxStyle;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class NotificationCompatJellybean
{
  static final String EXTRA_ACTION_EXTRAS = "android.support.actionExtras";
  static final String EXTRA_GROUP_KEY = "android.support.groupKey";
  static final String EXTRA_GROUP_SUMMARY = "android.support.isGroupSummary";
  static final String EXTRA_LOCAL_ONLY = "android.support.localOnly";
  static final String EXTRA_REMOTE_INPUTS = "android.support.remoteInputs";
  static final String EXTRA_SORT_KEY = "android.support.sortKey";
  static final String EXTRA_USE_SIDE_CHANNEL = "android.support.useSideChannel";
  private static final String KEY_ACTION_INTENT = "actionIntent";
  private static final String KEY_EXTRAS = "extras";
  private static final String KEY_ICON = "icon";
  private static final String KEY_REMOTE_INPUTS = "remoteInputs";
  private static final String KEY_TITLE = "title";
  public static final String TAG = "NotificationCompat";
  private static Class<?> sActionClass;
  private static Field sActionIconField;
  private static Field sActionIntentField;
  private static Field sActionTitleField;
  private static boolean sActionsAccessFailed;
  private static Field sActionsField;
  private static final Object sActionsLock = new Object();
  private static Field sExtrasField;
  private static boolean sExtrasFieldAccessFailed;
  private static final Object sExtrasLock = new Object();
  
  public static void addBigPictureStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence1, boolean paramBoolean1, CharSequence paramCharSequence2, Bitmap paramBitmap1, Bitmap paramBitmap2, boolean paramBoolean2)
  {
    paramNotificationBuilderWithBuilderAccessor = new Notification.BigPictureStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(paramCharSequence1).bigPicture(paramBitmap1);
    if (!paramBoolean2) {}
    while (!paramBoolean1)
    {
      return;
      paramNotificationBuilderWithBuilderAccessor.bigLargeIcon(paramBitmap2);
    }
    paramNotificationBuilderWithBuilderAccessor.setSummaryText(paramCharSequence2);
  }
  
  public static void addBigTextStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence1, boolean paramBoolean, CharSequence paramCharSequence2, CharSequence paramCharSequence3)
  {
    paramNotificationBuilderWithBuilderAccessor = new Notification.BigTextStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(paramCharSequence1).bigText(paramCharSequence3);
    if (!paramBoolean) {
      return;
    }
    paramNotificationBuilderWithBuilderAccessor.setSummaryText(paramCharSequence2);
  }
  
  public static void addInboxStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence1, boolean paramBoolean, CharSequence paramCharSequence2, ArrayList<CharSequence> paramArrayList)
  {
    paramNotificationBuilderWithBuilderAccessor = new Notification.InboxStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(paramCharSequence1);
    if (!paramBoolean) {
      paramCharSequence1 = paramArrayList.iterator();
    }
    for (;;)
    {
      if (!paramCharSequence1.hasNext())
      {
        return;
        paramNotificationBuilderWithBuilderAccessor.setSummaryText(paramCharSequence2);
        break;
      }
      paramNotificationBuilderWithBuilderAccessor.addLine((CharSequence)paramCharSequence1.next());
    }
  }
  
  public static SparseArray<Bundle> buildActionExtrasMap(List<Bundle> paramList)
  {
    Object localObject = null;
    int j = paramList.size();
    int i = 0;
    Bundle localBundle;
    for (;;)
    {
      if (i >= j) {
        return (SparseArray<Bundle>)localObject;
      }
      localBundle = (Bundle)paramList.get(i);
      if (localBundle != null) {
        break;
      }
      i += 1;
    }
    if (localObject != null) {}
    for (;;)
    {
      ((SparseArray)localObject).put(i, localBundle);
      break;
      localObject = new SparseArray();
    }
  }
  
  private static boolean ensureActionReflectionReadyLocked()
  {
    if (!sActionsAccessFailed) {}
    for (;;)
    {
      try
      {
        Field localField = sActionsField;
        if (localField == null) {
          continue;
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Log.e("NotificationCompat", "Unable to access notification actions", localClassNotFoundException);
        sActionsAccessFailed = true;
        continue;
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        Log.e("NotificationCompat", "Unable to access notification actions", localNoSuchFieldException);
        sActionsAccessFailed = true;
        continue;
      }
      if (!sActionsAccessFailed) {
        break;
      }
      return false;
      return false;
      sActionClass = Class.forName("android.app.Notification$Action");
      sActionIconField = sActionClass.getDeclaredField("icon");
      sActionTitleField = sActionClass.getDeclaredField("title");
      sActionIntentField = sActionClass.getDeclaredField("actionIntent");
      sActionsField = Notification.class.getDeclaredField("actions");
      sActionsField.setAccessible(true);
    }
    return true;
  }
  
  public static NotificationCompatBase.Action getAction(Notification paramNotification, int paramInt, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    synchronized (sActionsLock)
    {
      try
      {
        Object localObject2 = getActionObjectsLocked(paramNotification)[paramInt];
        paramNotification = getExtras(paramNotification);
        if (paramNotification == null) {
          paramNotification = null;
        }
        for (;;)
        {
          paramNotification = readAction(paramFactory, paramFactory1, sActionIconField.getInt(localObject2), (CharSequence)sActionTitleField.get(localObject2), (PendingIntent)sActionIntentField.get(localObject2), paramNotification);
          return paramNotification;
          paramNotification = paramNotification.getSparseParcelableArray("android.support.actionExtras");
          if (paramNotification == null) {
            paramNotification = null;
          } else {
            paramNotification = (Bundle)paramNotification.get(paramInt);
          }
        }
        paramNotification = finally;
      }
      catch (IllegalAccessException paramNotification)
      {
        Log.e("NotificationCompat", "Unable to access notification actions", paramNotification);
        sActionsAccessFailed = true;
        return null;
      }
    }
  }
  
  public static int getActionCount(Notification paramNotification)
  {
    synchronized (sActionsLock)
    {
      paramNotification = getActionObjectsLocked(paramNotification);
      if (paramNotification == null)
      {
        i = 0;
        return i;
      }
      int i = paramNotification.length;
    }
  }
  
  private static NotificationCompatBase.Action getActionFromBundle(Bundle paramBundle, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    return paramFactory.build(paramBundle.getInt("icon"), paramBundle.getCharSequence("title"), (PendingIntent)paramBundle.getParcelable("actionIntent"), paramBundle.getBundle("extras"), RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(paramBundle, "remoteInputs"), paramFactory1));
  }
  
  private static Object[] getActionObjectsLocked(Notification paramNotification)
  {
    synchronized (sActionsLock)
    {
      boolean bool = ensureActionReflectionReadyLocked();
      if (bool) {}
      try
      {
        paramNotification = (Object[])sActionsField.get(paramNotification);
        return paramNotification;
      }
      catch (IllegalAccessException paramNotification)
      {
        Log.e("NotificationCompat", "Unable to access notification actions", paramNotification);
        sActionsAccessFailed = true;
        return null;
      }
      return null;
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
      arrayOfAction[i] = getActionFromBundle((Bundle)paramArrayList.get(i), paramFactory, paramFactory1);
      i += 1;
    }
  }
  
  private static Bundle getBundleForAction(NotificationCompatBase.Action paramAction)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("icon", paramAction.getIcon());
    localBundle.putCharSequence("title", paramAction.getTitle());
    localBundle.putParcelable("actionIntent", paramAction.getActionIntent());
    localBundle.putBundle("extras", paramAction.getExtras());
    localBundle.putParcelableArray("remoteInputs", RemoteInputCompatJellybean.toBundleArray(paramAction.getRemoteInputs()));
    return localBundle;
  }
  
  public static Bundle getExtras(Notification paramNotification)
  {
    label45:
    label113:
    synchronized (sExtrasLock)
    {
      boolean bool = sExtrasFieldAccessFailed;
      if (!bool) {}
      try
      {
        if (sExtrasField == null) {
          break label45;
        }
        localObject1 = (Bundle)sExtrasField.get(paramNotification);
        if (localObject1 == null) {
          break label113;
        }
        paramNotification = (Notification)localObject1;
      }
      catch (IllegalAccessException paramNotification)
      {
        for (;;)
        {
          Log.e("NotificationCompat", "Unable to access notification extras", paramNotification);
          sExtrasFieldAccessFailed = true;
          return null;
          Log.e("NotificationCompat", "Notification.extras field is not of type Bundle");
          sExtrasFieldAccessFailed = true;
          return null;
          Object localObject1 = new Bundle();
          sExtrasField.set(paramNotification, localObject1);
          paramNotification = (Notification)localObject1;
        }
      }
      catch (NoSuchFieldException paramNotification)
      {
        for (;;)
        {
          Log.e("NotificationCompat", "Unable to access notification extras", paramNotification);
        }
      }
      return paramNotification;
      return null;
      localObject1 = Notification.class.getDeclaredField("extras");
      if (Bundle.class.isAssignableFrom(((Field)localObject1).getType()))
      {
        ((Field)localObject1).setAccessible(true);
        sExtrasField = (Field)localObject1;
      }
    }
  }
  
  public static String getGroup(Notification paramNotification)
  {
    return getExtras(paramNotification).getString("android.support.groupKey");
  }
  
  public static boolean getLocalOnly(Notification paramNotification)
  {
    return getExtras(paramNotification).getBoolean("android.support.localOnly");
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
      localArrayList.add(getBundleForAction(paramArrayOfAction[i]));
      i += 1;
    }
  }
  
  public static String getSortKey(Notification paramNotification)
  {
    return getExtras(paramNotification).getString("android.support.sortKey");
  }
  
  public static boolean isGroupSummary(Notification paramNotification)
  {
    return getExtras(paramNotification).getBoolean("android.support.isGroupSummary");
  }
  
  public static NotificationCompatBase.Action readAction(NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1, int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle)
  {
    Object localObject = null;
    if (paramBundle == null) {}
    for (paramFactory1 = (RemoteInputCompatBase.RemoteInput.Factory)localObject;; paramFactory1 = RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(paramBundle, "android.support.remoteInputs"), paramFactory1)) {
      return paramFactory.build(paramInt, paramCharSequence, paramPendingIntent, paramBundle, paramFactory1);
    }
  }
  
  public static Bundle writeActionAndGetExtras(Notification.Builder paramBuilder, NotificationCompatBase.Action paramAction)
  {
    paramBuilder.addAction(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent());
    paramBuilder = new Bundle(paramAction.getExtras());
    if (paramAction.getRemoteInputs() == null) {
      return paramBuilder;
    }
    paramBuilder.putParcelableArray("android.support.remoteInputs", RemoteInputCompatJellybean.toBundleArray(paramAction.getRemoteInputs()));
    return paramBuilder;
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    private List<Bundle> mActionExtrasList = new ArrayList();
    private final Bundle mExtras;
    
    public Builder(Context paramContext, Notification paramNotification, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean3, Bundle paramBundle, String paramString1, boolean paramBoolean4, String paramString2)
    {
      paramContext = new Notification.Builder(paramContext).setWhen(paramNotification.when).setSmallIcon(paramNotification.icon, paramNotification.iconLevel).setContent(paramNotification.contentView).setTicker(paramNotification.tickerText, paramRemoteViews).setSound(paramNotification.sound, paramNotification.audioStreamType).setVibrate(paramNotification.vibrate).setLights(paramNotification.ledARGB, paramNotification.ledOnMS, paramNotification.ledOffMS);
      boolean bool;
      if ((paramNotification.flags & 0x2) == 0)
      {
        bool = false;
        paramContext = paramContext.setOngoing(bool);
        if ((paramNotification.flags & 0x8) != 0) {
          break label281;
        }
        bool = false;
        label123:
        paramContext = paramContext.setOnlyAlertOnce(bool);
        if ((paramNotification.flags & 0x10) != 0) {
          break label287;
        }
        bool = false;
        label143:
        paramContext = paramContext.setAutoCancel(bool).setDefaults(paramNotification.defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(paramNotification.deleteIntent);
        if ((paramNotification.flags & 0x80) != 0) {
          break label293;
        }
        bool = false;
        label202:
        this.b = paramContext.setFullScreenIntent(paramPendingIntent2, bool).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean2).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1);
        this.mExtras = new Bundle();
        if (paramBundle != null) {
          break label299;
        }
        label259:
        if (paramBoolean3) {
          break label311;
        }
        label264:
        if (paramString1 != null) {
          break label324;
        }
      }
      for (;;)
      {
        if (paramString2 != null) {
          break label366;
        }
        return;
        bool = true;
        break;
        label281:
        bool = true;
        break label123;
        label287:
        bool = true;
        break label143;
        label293:
        bool = true;
        break label202;
        label299:
        this.mExtras.putAll(paramBundle);
        break label259;
        label311:
        this.mExtras.putBoolean("android.support.localOnly", true);
        break label264;
        label324:
        this.mExtras.putString("android.support.groupKey", paramString1);
        if (!paramBoolean4) {
          this.mExtras.putBoolean("android.support.useSideChannel", true);
        } else {
          this.mExtras.putBoolean("android.support.isGroupSummary", true);
        }
      }
      label366:
      this.mExtras.putString("android.support.sortKey", paramString2);
    }
    
    public void addAction(NotificationCompatBase.Action paramAction)
    {
      this.mActionExtrasList.add(NotificationCompatJellybean.writeActionAndGetExtras(this.b, paramAction));
    }
    
    public Notification build()
    {
      Notification localNotification = this.b.build();
      Object localObject = NotificationCompatJellybean.getExtras(localNotification);
      Bundle localBundle = new Bundle(this.mExtras);
      Iterator localIterator = this.mExtras.keySet().iterator();
      for (;;)
      {
        if (!localIterator.hasNext())
        {
          ((Bundle)localObject).putAll(localBundle);
          localObject = NotificationCompatJellybean.buildActionExtrasMap(this.mActionExtrasList);
          if (localObject != null) {
            break;
          }
          return localNotification;
        }
        String str = (String)localIterator.next();
        if (((Bundle)localObject).containsKey(str)) {
          localBundle.remove(str);
        }
      }
      NotificationCompatJellybean.getExtras(localNotification).putSparseParcelableArray("android.support.actionExtras", (SparseArray)localObject);
      return localNotification;
    }
    
    public Notification.Builder getBuilder()
    {
      return this.b;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/NotificationCompatJellybean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */