package com.android.server.am;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

final class AppWaitingForDebuggerDialog
  extends BaseErrorDialog
{
  private CharSequence mAppName;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      AppWaitingForDebuggerDialog.this.mService.killAppAtUsersRequest(AppWaitingForDebuggerDialog.this.mProc, AppWaitingForDebuggerDialog.this);
    }
  };
  final ProcessRecord mProc;
  final ActivityManagerService mService;
  
  public AppWaitingForDebuggerDialog(ActivityManagerService paramActivityManagerService, Context paramContext, ProcessRecord paramProcessRecord)
  {
    super(paramContext);
    this.mService = paramActivityManagerService;
    this.mProc = paramProcessRecord;
    this.mAppName = paramContext.getPackageManager().getApplicationLabel(paramProcessRecord.info);
    setCancelable(false);
    paramActivityManagerService = new StringBuilder();
    if ((this.mAppName != null) && (this.mAppName.length() > 0))
    {
      paramActivityManagerService.append("Application ");
      paramActivityManagerService.append(this.mAppName);
      paramActivityManagerService.append(" (process ");
      paramActivityManagerService.append(paramProcessRecord.processName);
      paramActivityManagerService.append(")");
    }
    for (;;)
    {
      paramActivityManagerService.append(" is waiting for the debugger to attach.");
      setMessage(paramActivityManagerService.toString());
      setButton(-1, "Force Close", this.mHandler.obtainMessage(1, paramProcessRecord));
      setTitle("Waiting For Debugger");
      paramActivityManagerService = getWindow().getAttributes();
      paramActivityManagerService.setTitle("Waiting For Debugger: " + paramProcessRecord.info.processName);
      getWindow().setAttributes(paramActivityManagerService);
      return;
      paramActivityManagerService.append("Process ");
      paramActivityManagerService.append(paramProcessRecord.processName);
    }
  }
  
  public void onStop() {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/AppWaitingForDebuggerDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */