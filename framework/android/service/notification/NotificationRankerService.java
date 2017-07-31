package android.service.notification;

import android.app.INotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.os.SomeArgs;
import java.util.List;

public abstract class NotificationRankerService
  extends NotificationListenerService
{
  public static final int REASON_APP_CANCEL = 8;
  public static final int REASON_APP_CANCEL_ALL = 9;
  public static final int REASON_DELEGATE_CANCEL = 2;
  public static final int REASON_DELEGATE_CANCEL_ALL = 3;
  public static final int REASON_DELEGATE_CLICK = 1;
  public static final int REASON_DELEGATE_ERROR = 4;
  public static final int REASON_GROUP_OPTIMIZATION = 13;
  public static final int REASON_GROUP_SUMMARY_CANCELED = 12;
  public static final int REASON_LISTENER_CANCEL = 10;
  public static final int REASON_LISTENER_CANCEL_ALL = 11;
  public static final int REASON_PACKAGE_BANNED = 7;
  public static final int REASON_PACKAGE_CHANGED = 5;
  public static final int REASON_PACKAGE_SUSPENDED = 14;
  public static final int REASON_PROFILE_TURNED_OFF = 15;
  public static final int REASON_UNAUTOBUNDLED = 16;
  public static final int REASON_USER_STOPPED = 6;
  public static final String SERVICE_INTERFACE = "android.service.notification.NotificationRankerService";
  private static final String TAG = "NotificationRankers";
  private Handler mHandler;
  
  public final void adjustNotification(Adjustment paramAdjustment)
  {
    if (!isBound()) {
      return;
    }
    try
    {
      getNotificationInterface().applyAdjustmentFromRankerService(this.mWrapper, paramAdjustment);
      return;
    }
    catch (RemoteException paramAdjustment)
    {
      Log.v("NotificationRankers", "Unable to contact notification manager", paramAdjustment);
    }
  }
  
  public final void adjustNotifications(List<Adjustment> paramList)
  {
    if (!isBound()) {
      return;
    }
    try
    {
      getNotificationInterface().applyAdjustmentsFromRankerService(this.mWrapper, paramList);
      return;
    }
    catch (RemoteException paramList)
    {
      Log.v("NotificationRankers", "Unable to contact notification manager", paramList);
    }
  }
  
  protected void attachBaseContext(Context paramContext)
  {
    super.attachBaseContext(paramContext);
    this.mHandler = new MyHandler(getContext().getMainLooper());
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    if (this.mWrapper == null) {
      this.mWrapper = new NotificationRankingServiceWrapper(null);
    }
    return this.mWrapper;
  }
  
  public void onNotificationActionClick(String paramString, long paramLong, int paramInt) {}
  
  public void onNotificationClick(String paramString, long paramLong) {}
  
  public abstract Adjustment onNotificationEnqueued(StatusBarNotification paramStatusBarNotification, int paramInt, boolean paramBoolean);
  
  public void onNotificationRemoved(String paramString, long paramLong, int paramInt) {}
  
  public void onNotificationVisibilityChanged(String paramString, long paramLong, boolean paramBoolean) {}
  
  public void registerAsSystemService(Context paramContext, ComponentName paramComponentName, int paramInt)
  {
    throw new UnsupportedOperationException("the ranker lifecycle is managed by the system.");
  }
  
  public void unregisterAsSystemService()
  {
    throw new UnsupportedOperationException("the ranker lifecycle is managed by the system.");
  }
  
  private final class MyHandler
    extends Handler
  {
    public static final int MSG_ON_NOTIFICATION_ACTION_CLICK = 4;
    public static final int MSG_ON_NOTIFICATION_CLICK = 3;
    public static final int MSG_ON_NOTIFICATION_ENQUEUED = 1;
    public static final int MSG_ON_NOTIFICATION_REMOVED_REASON = 5;
    public static final int MSG_ON_NOTIFICATION_VISIBILITY_CHANGED = 2;
    
    public MyHandler(Looper paramLooper)
    {
      super(null, false);
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool;
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localObject = (StatusBarNotification)paramMessage.arg1;
        i = paramMessage.argi1;
        if (paramMessage.argi2 == 1) {}
        for (bool = true;; bool = false)
        {
          paramMessage.recycle();
          paramMessage = NotificationRankerService.this.onNotificationEnqueued((StatusBarNotification)localObject, i, bool);
          if (paramMessage == null) {
            break;
          }
          NotificationRankerService.this.adjustNotification(paramMessage);
          return;
        }
      case 2: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localObject = (String)paramMessage.arg1;
        l = ((Long)paramMessage.arg2).longValue();
        if (paramMessage.argi1 == 1) {}
        for (bool = true;; bool = false)
        {
          paramMessage.recycle();
          NotificationRankerService.this.onNotificationVisibilityChanged((String)localObject, l, bool);
          return;
        }
      case 3: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localObject = (String)paramMessage.arg1;
        l = ((Long)paramMessage.arg2).longValue();
        paramMessage.recycle();
        NotificationRankerService.this.onNotificationClick((String)localObject, l);
        return;
      case 4: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localObject = (String)paramMessage.arg1;
        l = ((Long)paramMessage.arg2).longValue();
        i = paramMessage.argi1;
        paramMessage.recycle();
        NotificationRankerService.this.onNotificationActionClick((String)localObject, l, i);
        return;
      }
      paramMessage = (SomeArgs)paramMessage.obj;
      Object localObject = (String)paramMessage.arg1;
      long l = ((Long)paramMessage.arg2).longValue();
      int i = paramMessage.argi1;
      paramMessage.recycle();
      NotificationRankerService.this.onNotificationRemoved((String)localObject, l, i);
    }
  }
  
  private class NotificationRankingServiceWrapper
    extends NotificationListenerService.NotificationListenerWrapper
  {
    private NotificationRankingServiceWrapper()
    {
      super();
    }
    
    public void onNotificationActionClick(String paramString, long paramLong, int paramInt)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramString;
      localSomeArgs.arg2 = Long.valueOf(paramLong);
      localSomeArgs.argi1 = paramInt;
      NotificationRankerService.-get0(NotificationRankerService.this).obtainMessage(4, localSomeArgs).sendToTarget();
    }
    
    public void onNotificationClick(String paramString, long paramLong)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramString;
      localSomeArgs.arg2 = Long.valueOf(paramLong);
      NotificationRankerService.-get0(NotificationRankerService.this).obtainMessage(3, localSomeArgs).sendToTarget();
    }
    
    public void onNotificationEnqueued(IStatusBarNotificationHolder paramIStatusBarNotificationHolder, int paramInt, boolean paramBoolean)
    {
      for (;;)
      {
        try
        {
          paramIStatusBarNotificationHolder = paramIStatusBarNotificationHolder.get();
          SomeArgs localSomeArgs = SomeArgs.obtain();
          localSomeArgs.arg1 = paramIStatusBarNotificationHolder;
          localSomeArgs.argi1 = paramInt;
          if (paramBoolean)
          {
            paramInt = 1;
            localSomeArgs.argi2 = paramInt;
            NotificationRankerService.-get0(NotificationRankerService.this).obtainMessage(1, localSomeArgs).sendToTarget();
            return;
          }
        }
        catch (RemoteException paramIStatusBarNotificationHolder)
        {
          Log.w("NotificationRankers", "onNotificationEnqueued: Error receiving StatusBarNotification", paramIStatusBarNotificationHolder);
          return;
        }
        paramInt = 0;
      }
    }
    
    public void onNotificationRemovedReason(String paramString, long paramLong, int paramInt)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramString;
      localSomeArgs.arg2 = Long.valueOf(paramLong);
      localSomeArgs.argi1 = paramInt;
      NotificationRankerService.-get0(NotificationRankerService.this).obtainMessage(5, localSomeArgs).sendToTarget();
    }
    
    public void onNotificationVisibilityChanged(String paramString, long paramLong, boolean paramBoolean)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramString;
      localSomeArgs.arg2 = Long.valueOf(paramLong);
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localSomeArgs.argi1 = i;
        NotificationRankerService.-get0(NotificationRankerService.this).obtainMessage(2, localSomeArgs).sendToTarget();
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/notification/NotificationRankerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */