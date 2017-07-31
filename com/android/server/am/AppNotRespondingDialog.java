package com.android.server.am;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.BidiFormatter;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;

final class AppNotRespondingDialog
  extends BaseErrorDialog
  implements View.OnClickListener
{
  public static final int ALREADY_SHOWING = -2;
  public static final int CANT_SHOW = -1;
  static final int FORCE_CLOSE = 1;
  private static final String TAG = "AppNotRespondingDialog";
  static final int WAIT = 2;
  static final int WAIT_AND_REPORT = 3;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      Object localObject = null;
      ??? = null;
      MetricsLogger.action(AppNotRespondingDialog.this.getContext(), 317, paramAnonymousMessage.what);
      switch (paramAnonymousMessage.what)
      {
      default: 
        localObject = ???;
      }
      for (;;)
      {
        if (localObject != null) {}
        try
        {
          AppNotRespondingDialog.this.getContext().startActivity((Intent)localObject);
          AppNotRespondingDialog.this.dismiss();
          return;
          AppNotRespondingDialog.-get1(AppNotRespondingDialog.this).killAppAtUsersRequest(AppNotRespondingDialog.-get0(AppNotRespondingDialog.this), AppNotRespondingDialog.this);
          localObject = ???;
          continue;
          synchronized (AppNotRespondingDialog.-get1(AppNotRespondingDialog.this))
          {
            ActivityManagerService.boostPriorityForLockedSection();
            ProcessRecord localProcessRecord = AppNotRespondingDialog.-get0(AppNotRespondingDialog.this);
            if (paramAnonymousMessage.what == 3) {
              localObject = AppNotRespondingDialog.-get1(AppNotRespondingDialog.this).mAppErrors.createAppErrorIntentLocked(localProcessRecord, System.currentTimeMillis(), null);
            }
            localProcessRecord.notResponding = false;
            localProcessRecord.notRespondingReport = null;
            if (localProcessRecord.anrDialog == AppNotRespondingDialog.this) {
              localProcessRecord.anrDialog = null;
            }
            AppNotRespondingDialog.-get1(AppNotRespondingDialog.this).mServices.scheduleServiceTimeoutLocked(localProcessRecord);
            ActivityManagerService.resetPriorityAfterLockedSection();
          }
        }
        catch (ActivityNotFoundException paramAnonymousMessage)
        {
          for (;;)
          {
            Slog.w("AppNotRespondingDialog", "bug report receiver dissappeared", paramAnonymousMessage);
          }
        }
      }
    }
  };
  private final ProcessRecord mProc;
  private final ActivityManagerService mService;
  
  public AppNotRespondingDialog(ActivityManagerService paramActivityManagerService, Context paramContext, ProcessRecord paramProcessRecord, ActivityRecord paramActivityRecord, boolean paramBoolean)
  {
    super(paramContext);
    this.mService = paramActivityManagerService;
    this.mProc = paramProcessRecord;
    Resources localResources = paramContext.getResources();
    setCancelable(false);
    int i;
    if (paramActivityRecord != null)
    {
      paramActivityRecord = paramActivityRecord.info.loadLabel(paramContext.getPackageManager());
      paramActivityManagerService = null;
      if (paramProcessRecord.pkgList.size() != 1) {
        break label255;
      }
      paramContext = paramContext.getPackageManager().getApplicationLabel(paramProcessRecord.info);
      paramActivityManagerService = paramContext;
      if (paramContext == null) {
        break label255;
      }
      if (paramActivityRecord == null) {
        break label240;
      }
      i = 17040303;
      paramActivityManagerService = paramContext;
      label99:
      paramContext = BidiFormatter.getInstance();
      if (paramActivityManagerService == null) {
        break label285;
      }
      paramActivityManagerService = localResources.getString(i, new Object[] { paramContext.unicodeWrap(paramActivityRecord.toString()), paramContext.unicodeWrap(paramActivityManagerService.toString()) });
      label146:
      setTitle(paramActivityManagerService);
      if (paramBoolean) {
        getWindow().setType(2010);
      }
      paramActivityManagerService = getWindow().getAttributes();
      if (!"system".equals(paramProcessRecord.info.processName)) {
        break label314;
      }
      paramActivityManagerService.setTitle("Application Not Responding: " + paramProcessRecord.info.packageName);
    }
    for (;;)
    {
      paramActivityManagerService.privateFlags = 272;
      getWindow().setAttributes(paramActivityManagerService);
      return;
      paramActivityRecord = null;
      break;
      label240:
      paramActivityManagerService = paramProcessRecord.processName;
      i = 17040305;
      paramActivityRecord = paramContext;
      break label99;
      label255:
      if (paramActivityRecord != null)
      {
        paramActivityManagerService = paramProcessRecord.processName;
        i = 17040304;
        break label99;
      }
      paramActivityRecord = paramProcessRecord.processName;
      i = 17040306;
      break label99;
      label285:
      paramActivityManagerService = localResources.getString(i, new Object[] { paramContext.unicodeWrap(paramActivityRecord.toString()) });
      break label146;
      label314:
      paramActivityManagerService.setTitle("Application Not Responding: " + paramProcessRecord.info.processName);
    }
  }
  
  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    default: 
      return;
    case 16909116: 
      this.mHandler.obtainMessage(3).sendToTarget();
      return;
    case 16909114: 
      this.mHandler.obtainMessage(1).sendToTarget();
      return;
    }
    this.mHandler.obtainMessage(2).sendToTarget();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    int i = 1;
    super.onCreate(paramBundle);
    paramBundle = (FrameLayout)findViewById(16908331);
    LayoutInflater.from(getContext()).inflate(17367091, paramBundle, true);
    paramBundle = (TextView)findViewById(16909116);
    paramBundle.setOnClickListener(this);
    if (this.mProc.errorReportReceiver != null) {
      if (i == 0) {
        break label110;
      }
    }
    label110:
    for (i = 0;; i = 8)
    {
      paramBundle.setVisibility(i);
      ((TextView)findViewById(16909114)).setOnClickListener(this);
      ((TextView)findViewById(16909115)).setOnClickListener(this);
      findViewById(16909100).setVisibility(0);
      return;
      i = 0;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/AppNotRespondingDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */