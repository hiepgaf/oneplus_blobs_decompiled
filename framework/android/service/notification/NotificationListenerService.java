package android.service.notification;

import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ParceledListSlice;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.widget.RemoteViews;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.SomeArgs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class NotificationListenerService
  extends Service
{
  public static final int HINT_HOST_DISABLE_CALL_EFFECTS = 4;
  public static final int HINT_HOST_DISABLE_EFFECTS = 1;
  public static final int HINT_HOST_DISABLE_NOTIFICATION_EFFECTS = 2;
  public static final int INTERRUPTION_FILTER_ALARMS = 4;
  public static final int INTERRUPTION_FILTER_ALL = 1;
  public static final int INTERRUPTION_FILTER_NONE = 3;
  public static final int INTERRUPTION_FILTER_PRIORITY = 2;
  public static final int INTERRUPTION_FILTER_UNKNOWN = 0;
  public static final String SERVICE_INTERFACE = "android.service.notification.NotificationListenerService";
  public static final int SUPPRESSED_EFFECT_SCREEN_OFF = 1;
  public static final int SUPPRESSED_EFFECT_SCREEN_ON = 2;
  public static final int TRIM_FULL = 0;
  public static final int TRIM_LIGHT = 1;
  private final String TAG = NotificationListenerService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";
  private boolean isConnected = false;
  protected int mCurrentUser;
  private Handler mHandler;
  private final Object mLock = new Object();
  private INotificationManager mNoMan;
  @GuardedBy("mLock")
  private RankingMap mRankingMap;
  protected Context mSystemContext;
  protected NotificationListenerWrapper mWrapper = null;
  
  private void applyUpdateLocked(NotificationRankingUpdate paramNotificationRankingUpdate)
  {
    this.mRankingMap = new RankingMap(paramNotificationRankingUpdate, null);
  }
  
  private void createLegacyIconExtras(Notification paramNotification)
  {
    Object localObject = paramNotification.getSmallIcon();
    Icon localIcon = paramNotification.getLargeIcon();
    if ((localObject != null) && (((Icon)localObject).getType() == 2))
    {
      paramNotification.extras.putInt("android.icon", ((Icon)localObject).getResId());
      paramNotification.icon = ((Icon)localObject).getResId();
    }
    if (localIcon != null)
    {
      localObject = localIcon.loadDrawable(getContext());
      if ((localObject != null) && ((localObject instanceof BitmapDrawable)))
      {
        localObject = ((BitmapDrawable)localObject).getBitmap();
        paramNotification.extras.putParcelable("android.largeIcon", (Parcelable)localObject);
        paramNotification.largeIcon = ((Bitmap)localObject);
      }
    }
  }
  
  private void maybePopulateRemoteViews(Notification paramNotification)
  {
    if (getContext().getApplicationInfo().targetSdkVersion < 24)
    {
      Object localObject = Notification.Builder.recoverBuilder(getContext(), paramNotification);
      RemoteViews localRemoteViews1 = ((Notification.Builder)localObject).createContentView();
      RemoteViews localRemoteViews2 = ((Notification.Builder)localObject).createBigContentView();
      localObject = ((Notification.Builder)localObject).createHeadsUpContentView();
      paramNotification.contentView = localRemoteViews1;
      paramNotification.bigContentView = localRemoteViews2;
      paramNotification.headsUpContentView = ((RemoteViews)localObject);
    }
  }
  
  public static void requestRebind(ComponentName paramComponentName)
  {
    INotificationManager localINotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    try
    {
      localINotificationManager.requestBindListener(paramComponentName);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  protected void attachBaseContext(Context paramContext)
  {
    super.attachBaseContext(paramContext);
    this.mHandler = new MyHandler(getMainLooper());
  }
  
  public final void cancelAllNotifications()
  {
    cancelNotifications(null);
  }
  
  public final void cancelNotification(String paramString)
  {
    if (!isBound()) {
      return;
    }
    try
    {
      getNotificationInterface().cancelNotificationsFromListener(this.mWrapper, new String[] { paramString });
      return;
    }
    catch (RemoteException paramString)
    {
      Log.v(this.TAG, "Unable to contact notification manager", paramString);
    }
  }
  
  public final void cancelNotification(String paramString1, String paramString2, int paramInt)
  {
    if (!isBound()) {
      return;
    }
    try
    {
      getNotificationInterface().cancelNotificationFromListener(this.mWrapper, paramString1, paramString2, paramInt);
      return;
    }
    catch (RemoteException paramString1)
    {
      Log.v(this.TAG, "Unable to contact notification manager", paramString1);
    }
  }
  
  public final void cancelNotifications(String[] paramArrayOfString)
  {
    if (!isBound()) {
      return;
    }
    try
    {
      getNotificationInterface().cancelNotificationsFromListener(this.mWrapper, paramArrayOfString);
      return;
    }
    catch (RemoteException paramArrayOfString)
    {
      Log.v(this.TAG, "Unable to contact notification manager", paramArrayOfString);
    }
  }
  
  public StatusBarNotification[] getActiveNotifications()
  {
    return getActiveNotifications(null, 0);
  }
  
  public StatusBarNotification[] getActiveNotifications(int paramInt)
  {
    return getActiveNotifications(null, paramInt);
  }
  
  public StatusBarNotification[] getActiveNotifications(String[] paramArrayOfString)
  {
    return getActiveNotifications(paramArrayOfString, 0);
  }
  
  public StatusBarNotification[] getActiveNotifications(String[] paramArrayOfString, int paramInt)
  {
    if (!isBound()) {
      return null;
    }
    List localList;
    try
    {
      localList = getNotificationInterface().getActiveNotificationsFromListener(this.mWrapper, paramArrayOfString, paramInt).getList();
      paramArrayOfString = null;
      int i = localList.size();
      paramInt = 0;
      for (;;)
      {
        if (paramInt < i)
        {
          StatusBarNotification localStatusBarNotification = (StatusBarNotification)localList.get(paramInt);
          Notification localNotification = localStatusBarNotification.getNotification();
          try
          {
            createLegacyIconExtras(localNotification);
            maybePopulateRemoteViews(localNotification);
            paramInt += 1;
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
            for (;;)
            {
              Object localObject = paramArrayOfString;
              if (paramArrayOfString == null) {
                localObject = new ArrayList(i);
              }
              ((ArrayList)localObject).add(localStatusBarNotification);
              Log.w(this.TAG, "onNotificationPosted: can't rebuild notification from " + localStatusBarNotification.getPackageName());
              paramArrayOfString = (String[])localObject;
            }
          }
        }
      }
      if (paramArrayOfString == null) {
        break label178;
      }
    }
    catch (RemoteException paramArrayOfString)
    {
      Log.v(this.TAG, "Unable to contact notification manager", paramArrayOfString);
      return null;
    }
    localList.removeAll(paramArrayOfString);
    label178:
    paramArrayOfString = (StatusBarNotification[])localList.toArray(new StatusBarNotification[localList.size()]);
    return paramArrayOfString;
  }
  
  protected Context getContext()
  {
    if (this.mSystemContext != null) {
      return this.mSystemContext;
    }
    return this;
  }
  
  public final int getCurrentInterruptionFilter()
  {
    if (!isBound()) {
      return 0;
    }
    try
    {
      int i = getNotificationInterface().getInterruptionFilterFromListener(this.mWrapper);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.v(this.TAG, "Unable to contact notification manager", localRemoteException);
    }
    return 0;
  }
  
  public final int getCurrentListenerHints()
  {
    if (!isBound()) {
      return 0;
    }
    try
    {
      int i = getNotificationInterface().getHintsFromListener(this.mWrapper);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.v(this.TAG, "Unable to contact notification manager", localRemoteException);
    }
    return 0;
  }
  
  public RankingMap getCurrentRanking()
  {
    synchronized (this.mLock)
    {
      RankingMap localRankingMap = this.mRankingMap;
      return localRankingMap;
    }
  }
  
  protected final INotificationManager getNotificationInterface()
  {
    if (this.mNoMan == null) {
      this.mNoMan = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    }
    return this.mNoMan;
  }
  
  protected boolean isBound()
  {
    if (this.mWrapper == null)
    {
      Log.w(this.TAG, "Notification listener service not yet bound.");
      return false;
    }
    return true;
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    if (this.mWrapper == null) {
      this.mWrapper = new NotificationListenerWrapper();
    }
    return this.mWrapper;
  }
  
  public void onDestroy()
  {
    onListenerDisconnected();
    super.onDestroy();
  }
  
  public void onInterruptionFilterChanged(int paramInt) {}
  
  public void onListenerConnected() {}
  
  public void onListenerDisconnected() {}
  
  public void onListenerHintsChanged(int paramInt) {}
  
  public void onNotificationPosted(StatusBarNotification paramStatusBarNotification) {}
  
  public void onNotificationPosted(StatusBarNotification paramStatusBarNotification, RankingMap paramRankingMap)
  {
    onNotificationPosted(paramStatusBarNotification);
  }
  
  public void onNotificationRankingUpdate(RankingMap paramRankingMap) {}
  
  public void onNotificationRemoved(StatusBarNotification paramStatusBarNotification) {}
  
  public void onNotificationRemoved(StatusBarNotification paramStatusBarNotification, RankingMap paramRankingMap)
  {
    onNotificationRemoved(paramStatusBarNotification);
  }
  
  public void onNotificationRemovedFail() {}
  
  public void registerAsSystemService(Context paramContext, ComponentName paramComponentName, int paramInt)
    throws RemoteException
  {
    if (this.mWrapper == null) {
      this.mWrapper = new NotificationListenerWrapper();
    }
    this.mSystemContext = paramContext;
    INotificationManager localINotificationManager = getNotificationInterface();
    this.mHandler = new MyHandler(paramContext.getMainLooper());
    this.mCurrentUser = paramInt;
    localINotificationManager.registerListener(this.mWrapper, paramComponentName, paramInt);
  }
  
  public final void requestInterruptionFilter(int paramInt)
  {
    if (!isBound()) {
      return;
    }
    try
    {
      getNotificationInterface().requestInterruptionFilterFromListener(this.mWrapper, paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.v(this.TAG, "Unable to contact notification manager", localRemoteException);
    }
  }
  
  public final void requestListenerHints(int paramInt)
  {
    if (!isBound()) {
      return;
    }
    try
    {
      getNotificationInterface().requestHintsFromListener(this.mWrapper, paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.v(this.TAG, "Unable to contact notification manager", localRemoteException);
    }
  }
  
  public final void requestUnbind()
  {
    INotificationManager localINotificationManager;
    if (this.mWrapper != null) {
      localINotificationManager = getNotificationInterface();
    }
    try
    {
      localINotificationManager.requestUnbindListener(this.mWrapper);
      this.isConnected = false;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public final void setNotificationsShown(String[] paramArrayOfString)
  {
    if (!isBound()) {
      return;
    }
    try
    {
      getNotificationInterface().setNotificationsShownFromListener(this.mWrapper, paramArrayOfString);
      return;
    }
    catch (RemoteException paramArrayOfString)
    {
      Log.v(this.TAG, "Unable to contact notification manager", paramArrayOfString);
    }
  }
  
  public final void setOnNotificationPostedTrim(int paramInt)
  {
    if (!isBound()) {
      return;
    }
    try
    {
      getNotificationInterface().setOnNotificationPostedTrimFromListener(this.mWrapper, paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.v(this.TAG, "Unable to contact notification manager", localRemoteException);
    }
  }
  
  public void unregisterAsSystemService()
    throws RemoteException
  {
    if (this.mWrapper != null) {
      getNotificationInterface().unregisterListener(this.mWrapper, this.mCurrentUser);
    }
  }
  
  private final class MyHandler
    extends Handler
  {
    public static final int MSG_ON_INTERRUPTION_FILTER_CHANGED = 6;
    public static final int MSG_ON_LISTENER_CONNECTED = 3;
    public static final int MSG_ON_LISTENER_HINTS_CHANGED = 5;
    public static final int MSG_ON_NOTIFICATION_POSTED = 1;
    public static final int MSG_ON_NOTIFICATION_RANKING_UPDATE = 4;
    public static final int MSG_ON_NOTIFICATION_REMOVED = 2;
    public static final int MSG_ON_NOTIFICATION_REMOVED_FAIL = 7;
    
    public MyHandler(Looper paramLooper)
    {
      super(null, false);
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (!NotificationListenerService.-get1(NotificationListenerService.this)) {
        return;
      }
      StatusBarNotification localStatusBarNotification;
      NotificationListenerService.RankingMap localRankingMap;
      int i;
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localStatusBarNotification = (StatusBarNotification)paramMessage.arg1;
        localRankingMap = (NotificationListenerService.RankingMap)paramMessage.arg2;
        paramMessage.recycle();
        NotificationListenerService.this.onNotificationPosted(localStatusBarNotification, localRankingMap);
        return;
      case 2: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localStatusBarNotification = (StatusBarNotification)paramMessage.arg1;
        localRankingMap = (NotificationListenerService.RankingMap)paramMessage.arg2;
        paramMessage.recycle();
        NotificationListenerService.this.onNotificationRemoved(localStatusBarNotification, localRankingMap);
        return;
      case 3: 
        NotificationListenerService.this.onListenerConnected();
        return;
      case 4: 
        paramMessage = (NotificationListenerService.RankingMap)paramMessage.obj;
        NotificationListenerService.this.onNotificationRankingUpdate(paramMessage);
        return;
      case 5: 
        i = paramMessage.arg1;
        NotificationListenerService.this.onListenerHintsChanged(i);
        return;
      case 6: 
        i = paramMessage.arg1;
        NotificationListenerService.this.onInterruptionFilterChanged(i);
        return;
      }
      NotificationListenerService.this.onNotificationRemovedFail();
    }
  }
  
  protected class NotificationListenerWrapper
    extends INotificationListener.Stub
  {
    protected NotificationListenerWrapper() {}
    
    public void onInterruptionFilterChanged(int paramInt)
      throws RemoteException
    {
      NotificationListenerService.-get2(NotificationListenerService.this).obtainMessage(6, paramInt, 0).sendToTarget();
    }
    
    public void onListenerConnected(NotificationRankingUpdate paramNotificationRankingUpdate)
    {
      synchronized (NotificationListenerService.-get3(NotificationListenerService.this))
      {
        NotificationListenerService.-wrap0(NotificationListenerService.this, paramNotificationRankingUpdate);
        NotificationListenerService.-set0(NotificationListenerService.this, true);
        NotificationListenerService.-get2(NotificationListenerService.this).obtainMessage(3).sendToTarget();
        return;
      }
    }
    
    public void onListenerHintsChanged(int paramInt)
      throws RemoteException
    {
      NotificationListenerService.-get2(NotificationListenerService.this).obtainMessage(5, paramInt, 0).sendToTarget();
    }
    
    public void onNotificationActionClick(String paramString, long paramLong, int paramInt)
      throws RemoteException
    {}
    
    public void onNotificationClick(String paramString, long paramLong)
      throws RemoteException
    {}
    
    public void onNotificationEnqueued(IStatusBarNotificationHolder paramIStatusBarNotificationHolder, int paramInt, boolean paramBoolean)
      throws RemoteException
    {}
    
    /* Error */
    public void onNotificationPosted(IStatusBarNotificationHolder paramIStatusBarNotificationHolder, NotificationRankingUpdate paramNotificationRankingUpdate)
    {
      // Byte code:
      //   0: aload_1
      //   1: invokeinterface 71 1 0
      //   6: astore_1
      //   7: aload_0
      //   8: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   11: aload_1
      //   12: invokevirtual 77	android/service/notification/StatusBarNotification:getNotification	()Landroid/app/Notification;
      //   15: invokestatic 81	android/service/notification/NotificationListenerService:-wrap1	(Landroid/service/notification/NotificationListenerService;Landroid/app/Notification;)V
      //   18: aload_0
      //   19: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   22: aload_1
      //   23: invokevirtual 77	android/service/notification/StatusBarNotification:getNotification	()Landroid/app/Notification;
      //   26: invokestatic 84	android/service/notification/NotificationListenerService:-wrap2	(Landroid/service/notification/NotificationListenerService;Landroid/app/Notification;)V
      //   29: aload_0
      //   30: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   33: invokestatic 43	android/service/notification/NotificationListenerService:-get3	(Landroid/service/notification/NotificationListenerService;)Ljava/lang/Object;
      //   36: astore_3
      //   37: aload_3
      //   38: monitorenter
      //   39: aload_0
      //   40: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   43: aload_2
      //   44: invokestatic 47	android/service/notification/NotificationListenerService:-wrap0	(Landroid/service/notification/NotificationListenerService;Landroid/service/notification/NotificationRankingUpdate;)V
      //   47: aload_1
      //   48: ifnull +96 -> 144
      //   51: invokestatic 90	com/android/internal/os/SomeArgs:obtain	()Lcom/android/internal/os/SomeArgs;
      //   54: astore_2
      //   55: aload_2
      //   56: aload_1
      //   57: putfield 94	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
      //   60: aload_2
      //   61: aload_0
      //   62: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   65: invokestatic 98	android/service/notification/NotificationListenerService:-get4	(Landroid/service/notification/NotificationListenerService;)Landroid/service/notification/NotificationListenerService$RankingMap;
      //   68: putfield 101	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
      //   71: aload_0
      //   72: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   75: invokestatic 25	android/service/notification/NotificationListenerService:-get2	(Landroid/service/notification/NotificationListenerService;)Landroid/os/Handler;
      //   78: iconst_1
      //   79: aload_2
      //   80: invokevirtual 104	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
      //   83: invokevirtual 36	android/os/Message:sendToTarget	()V
      //   86: aload_3
      //   87: monitorexit
      //   88: return
      //   89: astore_1
      //   90: aload_0
      //   91: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   94: invokestatic 108	android/service/notification/NotificationListenerService:-get0	(Landroid/service/notification/NotificationListenerService;)Ljava/lang/String;
      //   97: ldc 110
      //   99: aload_1
      //   100: invokestatic 116	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   103: pop
      //   104: return
      //   105: astore_3
      //   106: aload_0
      //   107: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   110: invokestatic 108	android/service/notification/NotificationListenerService:-get0	(Landroid/service/notification/NotificationListenerService;)Ljava/lang/String;
      //   113: new 118	java/lang/StringBuilder
      //   116: dup
      //   117: invokespecial 119	java/lang/StringBuilder:<init>	()V
      //   120: ldc 121
      //   122: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   125: aload_1
      //   126: invokevirtual 129	android/service/notification/StatusBarNotification:getPackageName	()Ljava/lang/String;
      //   129: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   132: invokevirtual 132	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   135: invokestatic 135	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   138: pop
      //   139: aconst_null
      //   140: astore_1
      //   141: goto -112 -> 29
      //   144: aload_0
      //   145: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   148: invokestatic 25	android/service/notification/NotificationListenerService:-get2	(Landroid/service/notification/NotificationListenerService;)Landroid/os/Handler;
      //   151: iconst_4
      //   152: aload_0
      //   153: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   156: invokestatic 98	android/service/notification/NotificationListenerService:-get4	(Landroid/service/notification/NotificationListenerService;)Landroid/service/notification/NotificationListenerService$RankingMap;
      //   159: invokevirtual 104	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
      //   162: invokevirtual 36	android/os/Message:sendToTarget	()V
      //   165: goto -79 -> 86
      //   168: astore_1
      //   169: aload_3
      //   170: monitorexit
      //   171: aload_1
      //   172: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	173	0	this	NotificationListenerWrapper
      //   0	173	1	paramIStatusBarNotificationHolder	IStatusBarNotificationHolder
      //   0	173	2	paramNotificationRankingUpdate	NotificationRankingUpdate
      //   105	65	3	localIllegalArgumentException	IllegalArgumentException
      // Exception table:
      //   from	to	target	type
      //   0	7	89	android/os/RemoteException
      //   7	29	105	java/lang/IllegalArgumentException
      //   39	47	168	finally
      //   51	86	168	finally
      //   144	165	168	finally
    }
    
    public void onNotificationRankingUpdate(NotificationRankingUpdate paramNotificationRankingUpdate)
      throws RemoteException
    {
      synchronized (NotificationListenerService.-get3(NotificationListenerService.this))
      {
        NotificationListenerService.-wrap0(NotificationListenerService.this, paramNotificationRankingUpdate);
        NotificationListenerService.-get2(NotificationListenerService.this).obtainMessage(4, NotificationListenerService.-get4(NotificationListenerService.this)).sendToTarget();
        return;
      }
    }
    
    /* Error */
    public void onNotificationRemoved(IStatusBarNotificationHolder arg1, NotificationRankingUpdate paramNotificationRankingUpdate)
    {
      // Byte code:
      //   0: aload_1
      //   1: invokeinterface 71 1 0
      //   6: astore_3
      //   7: aload_0
      //   8: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   11: invokestatic 43	android/service/notification/NotificationListenerService:-get3	(Landroid/service/notification/NotificationListenerService;)Ljava/lang/Object;
      //   14: astore_1
      //   15: aload_1
      //   16: monitorenter
      //   17: aload_0
      //   18: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   21: aload_2
      //   22: invokestatic 47	android/service/notification/NotificationListenerService:-wrap0	(Landroid/service/notification/NotificationListenerService;Landroid/service/notification/NotificationRankingUpdate;)V
      //   25: invokestatic 90	com/android/internal/os/SomeArgs:obtain	()Lcom/android/internal/os/SomeArgs;
      //   28: astore_2
      //   29: aload_2
      //   30: aload_3
      //   31: putfield 94	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
      //   34: aload_2
      //   35: aload_0
      //   36: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   39: invokestatic 98	android/service/notification/NotificationListenerService:-get4	(Landroid/service/notification/NotificationListenerService;)Landroid/service/notification/NotificationListenerService$RankingMap;
      //   42: putfield 101	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
      //   45: aload_0
      //   46: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   49: invokestatic 25	android/service/notification/NotificationListenerService:-get2	(Landroid/service/notification/NotificationListenerService;)Landroid/os/Handler;
      //   52: iconst_2
      //   53: aload_2
      //   54: invokevirtual 104	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
      //   57: invokevirtual 36	android/os/Message:sendToTarget	()V
      //   60: aload_1
      //   61: monitorexit
      //   62: return
      //   63: astore_1
      //   64: aload_0
      //   65: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   68: invokestatic 108	android/service/notification/NotificationListenerService:-get0	(Landroid/service/notification/NotificationListenerService;)Ljava/lang/String;
      //   71: ldc -117
      //   73: aload_1
      //   74: invokestatic 116	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   77: pop
      //   78: aload_0
      //   79: getfield 13	android/service/notification/NotificationListenerService$NotificationListenerWrapper:this$0	Landroid/service/notification/NotificationListenerService;
      //   82: invokestatic 25	android/service/notification/NotificationListenerService:-get2	(Landroid/service/notification/NotificationListenerService;)Landroid/os/Handler;
      //   85: bipush 7
      //   87: invokevirtual 54	android/os/Handler:obtainMessage	(I)Landroid/os/Message;
      //   90: invokevirtual 36	android/os/Message:sendToTarget	()V
      //   93: return
      //   94: astore_2
      //   95: aload_1
      //   96: monitorexit
      //   97: aload_2
      //   98: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	99	0	this	NotificationListenerWrapper
      //   0	99	2	paramNotificationRankingUpdate	NotificationRankingUpdate
      //   6	25	3	localStatusBarNotification	StatusBarNotification
      // Exception table:
      //   from	to	target	type
      //   0	7	63	android/os/RemoteException
      //   17	60	94	finally
    }
    
    public void onNotificationRemovedReason(String paramString, long paramLong, int paramInt)
      throws RemoteException
    {}
    
    public void onNotificationVisibilityChanged(String paramString, long paramLong, boolean paramBoolean)
      throws RemoteException
    {}
  }
  
  public static class Ranking
  {
    public static final int IMPORTANCE_DEFAULT = 3;
    public static final int IMPORTANCE_GOINGTO_UNSPECIFIED = 1000;
    public static final int IMPORTANCE_HIGH = 4;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_MAX = 5;
    public static final int IMPORTANCE_MIN = 1;
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_UNSPECIFIED = -1000;
    public static final int OPLEVEL_BLOCK = 2;
    public static final int OPLEVEL_DENOISE = 1;
    public static final int OPLEVEL_RESET = 0;
    public static final int VISIBILITY_NO_OVERRIDE = -1000;
    private int mImportance;
    private CharSequence mImportanceExplanation;
    private boolean mIsAmbient;
    private String mKey;
    private boolean mMatchesInterruptionFilter;
    private String mOverrideGroupKey;
    private int mRank = -1;
    private int mSuppressedVisualEffects;
    private int mVisibilityOverride;
    
    public static String importanceToString(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return "UNKNOWN(" + String.valueOf(paramInt) + ")";
      case -1000: 
        return "UNSPECIFIED";
      case 0: 
        return "NONE";
      case 1: 
        return "MIN";
      case 2: 
        return "LOW";
      case 3: 
        return "DEFAULT";
      case 4: 
        return "HIGH";
      }
      return "MAX";
    }
    
    public static String opLevelToString(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return "UNKNOWN(" + String.valueOf(paramInt) + ")";
      case 0: 
        return "RESET";
      case 1: 
        return "DENOISE";
      }
      return "BLOCK";
    }
    
    private void populate(String paramString1, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, CharSequence paramCharSequence, String paramString2)
    {
      this.mKey = paramString1;
      this.mRank = paramInt1;
      if (paramInt4 < 2) {}
      for (boolean bool = true;; bool = false)
      {
        this.mIsAmbient = bool;
        this.mMatchesInterruptionFilter = paramBoolean;
        this.mVisibilityOverride = paramInt2;
        this.mSuppressedVisualEffects = paramInt3;
        this.mImportance = paramInt4;
        this.mImportanceExplanation = paramCharSequence;
        this.mOverrideGroupKey = paramString2;
        return;
      }
    }
    
    public int getImportance()
    {
      return this.mImportance;
    }
    
    public CharSequence getImportanceExplanation()
    {
      return this.mImportanceExplanation;
    }
    
    public String getKey()
    {
      return this.mKey;
    }
    
    public String getOverrideGroupKey()
    {
      return this.mOverrideGroupKey;
    }
    
    public int getRank()
    {
      return this.mRank;
    }
    
    public int getSuppressedVisualEffects()
    {
      return this.mSuppressedVisualEffects;
    }
    
    public int getVisibilityOverride()
    {
      return this.mVisibilityOverride;
    }
    
    public boolean isAmbient()
    {
      return this.mIsAmbient;
    }
    
    public boolean matchesInterruptionFilter()
    {
      return this.mMatchesInterruptionFilter;
    }
  }
  
  public static class RankingMap
    implements Parcelable
  {
    public static final Parcelable.Creator<RankingMap> CREATOR = new Parcelable.Creator()
    {
      public NotificationListenerService.RankingMap createFromParcel(Parcel paramAnonymousParcel)
      {
        return new NotificationListenerService.RankingMap((NotificationRankingUpdate)paramAnonymousParcel.readParcelable(null), null);
      }
      
      public NotificationListenerService.RankingMap[] newArray(int paramAnonymousInt)
      {
        return new NotificationListenerService.RankingMap[paramAnonymousInt];
      }
    };
    private ArrayMap<String, Integer> mImportance;
    private ArrayMap<String, String> mImportanceExplanation;
    private ArraySet<Object> mIntercepted;
    private ArrayMap<String, String> mOverrideGroupKeys;
    private final NotificationRankingUpdate mRankingUpdate;
    private ArrayMap<String, Integer> mRanks;
    private ArrayMap<String, Integer> mSuppressedVisualEffects;
    private ArrayMap<String, Integer> mVisibilityOverrides;
    
    private RankingMap(NotificationRankingUpdate paramNotificationRankingUpdate)
    {
      this.mRankingUpdate = paramNotificationRankingUpdate;
    }
    
    private void buildImportanceExplanationLocked()
    {
      Bundle localBundle = this.mRankingUpdate.getImportanceExplanation();
      this.mImportanceExplanation = new ArrayMap(localBundle.size());
      Iterator localIterator = localBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        this.mImportanceExplanation.put(str, localBundle.getString(str));
      }
    }
    
    private void buildImportanceLocked()
    {
      String[] arrayOfString = this.mRankingUpdate.getOrderedKeys();
      int[] arrayOfInt = this.mRankingUpdate.getImportance();
      this.mImportance = new ArrayMap(arrayOfString.length);
      int i = 0;
      while (i < arrayOfString.length)
      {
        String str = arrayOfString[i];
        this.mImportance.put(str, Integer.valueOf(arrayOfInt[i]));
        i += 1;
      }
    }
    
    private void buildInterceptedSetLocked()
    {
      String[] arrayOfString = this.mRankingUpdate.getInterceptedKeys();
      this.mIntercepted = new ArraySet(arrayOfString.length);
      Collections.addAll(this.mIntercepted, arrayOfString);
    }
    
    private void buildOverrideGroupKeys()
    {
      Bundle localBundle = this.mRankingUpdate.getOverrideGroupKeys();
      this.mOverrideGroupKeys = new ArrayMap(localBundle.size());
      Iterator localIterator = localBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        this.mOverrideGroupKeys.put(str, localBundle.getString(str));
      }
    }
    
    private void buildRanksLocked()
    {
      String[] arrayOfString = this.mRankingUpdate.getOrderedKeys();
      this.mRanks = new ArrayMap(arrayOfString.length);
      int i = 0;
      while (i < arrayOfString.length)
      {
        String str = arrayOfString[i];
        this.mRanks.put(str, Integer.valueOf(i));
        i += 1;
      }
    }
    
    private void buildSuppressedVisualEffectsLocked()
    {
      Bundle localBundle = this.mRankingUpdate.getSuppressedVisualEffects();
      this.mSuppressedVisualEffects = new ArrayMap(localBundle.size());
      Iterator localIterator = localBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        this.mSuppressedVisualEffects.put(str, Integer.valueOf(localBundle.getInt(str)));
      }
    }
    
    private void buildVisibilityOverridesLocked()
    {
      Bundle localBundle = this.mRankingUpdate.getVisibilityOverrides();
      this.mVisibilityOverrides = new ArrayMap(localBundle.size());
      Iterator localIterator = localBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        this.mVisibilityOverrides.put(str, Integer.valueOf(localBundle.getInt(str)));
      }
    }
    
    private int getImportance(String paramString)
    {
      try
      {
        if (this.mImportance == null) {
          buildImportanceLocked();
        }
        paramString = (Integer)this.mImportance.get(paramString);
        if (paramString == null) {
          return 3;
        }
      }
      finally {}
      return paramString.intValue();
    }
    
    private String getImportanceExplanation(String paramString)
    {
      try
      {
        if (this.mImportanceExplanation == null) {
          buildImportanceExplanationLocked();
        }
        return (String)this.mImportanceExplanation.get(paramString);
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    private String getOverrideGroupKey(String paramString)
    {
      try
      {
        if (this.mOverrideGroupKeys == null) {
          buildOverrideGroupKeys();
        }
        return (String)this.mOverrideGroupKeys.get(paramString);
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    private int getRank(String paramString)
    {
      try
      {
        if (this.mRanks == null) {
          buildRanksLocked();
        }
        paramString = (Integer)this.mRanks.get(paramString);
        if (paramString != null) {
          return paramString.intValue();
        }
      }
      finally {}
      return -1;
    }
    
    private int getSuppressedVisualEffects(String paramString)
    {
      try
      {
        if (this.mSuppressedVisualEffects == null) {
          buildSuppressedVisualEffectsLocked();
        }
        paramString = (Integer)this.mSuppressedVisualEffects.get(paramString);
        if (paramString == null) {
          return 0;
        }
      }
      finally {}
      return paramString.intValue();
    }
    
    private int getVisibilityOverride(String paramString)
    {
      try
      {
        if (this.mVisibilityOverrides == null) {
          buildVisibilityOverridesLocked();
        }
        paramString = (Integer)this.mVisibilityOverrides.get(paramString);
        if (paramString == null) {
          return 64536;
        }
      }
      finally {}
      return paramString.intValue();
    }
    
    private boolean isIntercepted(String paramString)
    {
      try
      {
        if (this.mIntercepted == null) {
          buildInterceptedSetLocked();
        }
        return this.mIntercepted.contains(paramString);
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String[] getOrderedKeys()
    {
      return this.mRankingUpdate.getOrderedKeys();
    }
    
    public boolean getRanking(String paramString, NotificationListenerService.Ranking paramRanking)
    {
      int i = getRank(paramString);
      if (isIntercepted(paramString)) {}
      for (boolean bool = false;; bool = true)
      {
        NotificationListenerService.Ranking.-wrap0(paramRanking, paramString, i, bool, getVisibilityOverride(paramString), getSuppressedVisualEffects(paramString), getImportance(paramString), getImportanceExplanation(paramString), getOverrideGroupKey(paramString));
        if (i < 0) {
          break;
        }
        return true;
      }
      return false;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeParcelable(this.mRankingUpdate, paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/notification/NotificationListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */