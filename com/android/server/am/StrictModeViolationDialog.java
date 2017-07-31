package com.android.server.am;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.view.Window;

final class StrictModeViolationDialog
  extends BaseErrorDialog
{
  static final int ACTION_OK = 0;
  static final int ACTION_OK_AND_REPORT = 1;
  static final long DISMISS_TIMEOUT = 60000L;
  private static final String TAG = "StrictModeViolationDialog";
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      synchronized (StrictModeViolationDialog.-get2(StrictModeViolationDialog.this))
      {
        ActivityManagerService.boostPriorityForLockedSection();
        if ((StrictModeViolationDialog.-get0(StrictModeViolationDialog.this) != null) && (StrictModeViolationDialog.-get0(StrictModeViolationDialog.this).crashDialog == StrictModeViolationDialog.this)) {
          StrictModeViolationDialog.-get0(StrictModeViolationDialog.this).crashDialog = null;
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        StrictModeViolationDialog.-get1(StrictModeViolationDialog.this).set(paramAnonymousMessage.what);
        StrictModeViolationDialog.this.dismiss();
        return;
      }
    }
  };
  private final ProcessRecord mProc;
  private final AppErrorResult mResult;
  private final ActivityManagerService mService;
  
  public StrictModeViolationDialog(Context paramContext, ActivityManagerService paramActivityManagerService, AppErrorResult paramAppErrorResult, ProcessRecord paramProcessRecord)
  {
    super(paramContext);
    Resources localResources = paramContext.getResources();
    this.mService = paramActivityManagerService;
    this.mProc = paramProcessRecord;
    this.mResult = paramAppErrorResult;
    if (paramProcessRecord.pkgList.size() == 1)
    {
      paramContext = paramContext.getPackageManager().getApplicationLabel(paramProcessRecord.info);
      if (paramContext != null) {
        setMessage(localResources.getString(17040319, new Object[] { paramContext.toString(), paramProcessRecord.info.processName }));
      }
    }
    for (;;)
    {
      setCancelable(false);
      setButton(-1, localResources.getText(17040425), this.mHandler.obtainMessage(0));
      if (paramProcessRecord.errorReportReceiver != null) {
        setButton(-2, localResources.getText(17040308), this.mHandler.obtainMessage(1));
      }
      getWindow().addPrivateFlags(256);
      getWindow().setTitle("Strict Mode Violation: " + paramProcessRecord.info.processName);
      this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0), 60000L);
      return;
      setMessage(localResources.getString(17040320, new Object[] { paramProcessRecord.processName.toString() }));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/StrictModeViolationDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */