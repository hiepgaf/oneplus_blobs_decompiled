package com.android.server.am;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver.Stub;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.util.ProgressReporter;
import com.android.server.UiThread;
import java.util.List;

public abstract class PreBootBroadcaster
  extends IIntentReceiver.Stub
{
  private static final int MSG_HIDE = 2;
  private static final int MSG_SHOW = 1;
  private static final String TAG = "PreBootBroadcaster";
  private Handler mHandler = new Handler(UiThread.get().getLooper(), null, true)
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      Context localContext = PreBootBroadcaster.-get0(PreBootBroadcaster.this).mContext;
      NotificationManager localNotificationManager = (NotificationManager)localContext.getSystemService(NotificationManager.class);
      int i = paramAnonymousMessage.arg1;
      int j = paramAnonymousMessage.arg2;
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        CharSequence localCharSequence = localContext.getText(17040324);
        paramAnonymousMessage = new Intent();
        paramAnonymousMessage.setClassName("com.android.settings", "com.android.settings.HelpTrampoline");
        paramAnonymousMessage.putExtra("android.intent.extra.TEXT", "help_url_upgrading");
        if (localContext.getPackageManager().resolveActivity(paramAnonymousMessage, 0) != null) {}
        for (paramAnonymousMessage = PendingIntent.getActivity(localContext, 0, paramAnonymousMessage, 0);; paramAnonymousMessage = null)
        {
          localNotificationManager.notifyAsUser("PreBootBroadcaster", 0, new Notification.Builder(PreBootBroadcaster.-get0(PreBootBroadcaster.this).mContext).setSmallIcon(17303263).setWhen(0L).setOngoing(true).setTicker(localCharSequence).setDefaults(0).setPriority(2).setColor(localContext.getColor(17170523)).setContentTitle(localCharSequence).setContentIntent(paramAnonymousMessage).setVisibility(1).setProgress(i, j, false).build(), UserHandle.of(PreBootBroadcaster.-get1(PreBootBroadcaster.this)));
          return;
        }
      }
      localNotificationManager.cancelAsUser("PreBootBroadcaster", 0, UserHandle.of(PreBootBroadcaster.-get1(PreBootBroadcaster.this)));
    }
  };
  private int mIndex = 0;
  private final Intent mIntent;
  private final ProgressReporter mProgress;
  private final boolean mQuiet;
  private final ActivityManagerService mService;
  private final List<ResolveInfo> mTargets;
  private final int mUserId;
  
  public PreBootBroadcaster(ActivityManagerService paramActivityManagerService, int paramInt, ProgressReporter paramProgressReporter, boolean paramBoolean)
  {
    this.mService = paramActivityManagerService;
    this.mUserId = paramInt;
    this.mProgress = paramProgressReporter;
    this.mQuiet = paramBoolean;
    this.mIntent = new Intent("android.intent.action.PRE_BOOT_COMPLETED");
    this.mIntent.addFlags(33554688);
    this.mTargets = this.mService.mContext.getPackageManager().queryBroadcastReceiversAsUser(this.mIntent, 1048576, UserHandle.of(paramInt));
  }
  
  public abstract void onFinished();
  
  public void performReceive(Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
  {
    sendNext();
  }
  
  public void sendNext()
  {
    if (this.mIndex >= this.mTargets.size())
    {
      this.mHandler.obtainMessage(2).sendToTarget();
      onFinished();
      return;
    }
    if (!this.mService.isUserRunning(this.mUserId, 0))
    {
      Slog.i("PreBootBroadcaster", "User " + this.mUserId + " is no longer running; skipping remaining receivers");
      this.mHandler.obtainMessage(2).sendToTarget();
      onFinished();
      return;
    }
    if (!this.mQuiet) {
      this.mHandler.obtainMessage(1, this.mTargets.size(), this.mIndex).sendToTarget();
    }
    Object localObject1 = this.mTargets;
    int i = this.mIndex;
    this.mIndex = (i + 1);
    Object localObject2 = (ResolveInfo)((List)localObject1).get(i);
    localObject1 = ((ResolveInfo)localObject2).activityInfo.getComponentName();
    if (this.mProgress != null)
    {
      localObject2 = ((ResolveInfo)localObject2).activityInfo.loadLabel(this.mService.mContext.getPackageManager());
      this.mProgress.setProgress(this.mIndex, this.mTargets.size(), this.mService.mContext.getString(17040328, new Object[] { localObject2 }));
    }
    Slog.i("PreBootBroadcaster", "Pre-boot of " + ((ComponentName)localObject1).toShortString() + " for user " + this.mUserId);
    EventLogTags.writeAmPreBoot(this.mUserId, ((ComponentName)localObject1).getPackageName());
    this.mIntent.setComponent((ComponentName)localObject1);
    this.mService.broadcastIntentLocked(null, null, this.mIntent, null, this, 0, null, null, null, -1, null, true, false, ActivityManagerService.MY_PID, 1000, this.mUserId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/PreBootBroadcaster.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */