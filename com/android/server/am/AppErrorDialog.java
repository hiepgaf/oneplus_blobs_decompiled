package com.android.server.am;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Global;
import android.text.BidiFormatter;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

final class AppErrorDialog
  extends BaseErrorDialog
  implements View.OnClickListener
{
  static int ALREADY_SHOWING = -3;
  static int BACKGROUND_USER = 0;
  static final int CANCEL = 7;
  static int CANT_SHOW = -1;
  static final long DISMISS_TIMEOUT = 300000L;
  static final int FORCE_QUIT = 1;
  static final int FORCE_QUIT_AND_REPORT = 2;
  static final int MUTE = 5;
  static final int RESTART = 3;
  static final int TIMEOUT = 6;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message arg1)
    {
      int i = ???.what;
      synchronized (AppErrorDialog.-get2(AppErrorDialog.this))
      {
        ActivityManagerService.boostPriorityForLockedSection();
        if ((AppErrorDialog.-get0(AppErrorDialog.this) != null) && (AppErrorDialog.-get0(AppErrorDialog.this).crashDialog == AppErrorDialog.this)) {
          AppErrorDialog.-get0(AppErrorDialog.this).crashDialog = null;
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        AppErrorDialog.-get1(AppErrorDialog.this).set(i);
        removeMessages(6);
        AppErrorDialog.this.dismiss();
        return;
      }
    }
  };
  private final boolean mIsRestartable;
  private CharSequence mName;
  private final ProcessRecord mProc;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(paramAnonymousIntent.getAction())) {
        AppErrorDialog.this.cancel();
      }
    }
  };
  private final boolean mRepeating;
  private final AppErrorResult mResult;
  private final ActivityManagerService mService;
  
  static
  {
    BACKGROUND_USER = -2;
  }
  
  public AppErrorDialog(Context paramContext, ActivityManagerService paramActivityManagerService, Data paramData)
  {
    super(paramContext);
    Resources localResources = paramContext.getResources();
    this.mService = paramActivityManagerService;
    this.mProc = paramData.proc;
    this.mResult = paramData.result;
    this.mRepeating = paramData.repeating;
    boolean bool;
    if (paramData.task == null)
    {
      bool = paramData.isRestartableForService;
      this.mIsRestartable = bool;
      paramActivityManagerService = BidiFormatter.getInstance();
      if (this.mProc.pkgList.size() != 1) {
        break label316;
      }
      paramContext = paramContext.getPackageManager().getApplicationLabel(this.mProc.info);
      this.mName = paramContext;
      if (paramContext == null) {
        break label316;
      }
      if (!this.mRepeating) {
        break label309;
      }
    }
    label309:
    for (int i = 17040294;; i = 17040292)
    {
      setTitle(localResources.getString(i, new Object[] { paramActivityManagerService.unicodeWrap(this.mName.toString()), paramActivityManagerService.unicodeWrap(this.mProc.info.processName) }));
      setCancelable(true);
      setCancelMessage(this.mHandler.obtainMessage(7));
      paramContext = getWindow().getAttributes();
      paramContext.setTitle("Application Error: " + this.mProc.info.processName);
      paramContext.privateFlags |= 0x110;
      getWindow().setAttributes(paramContext);
      if (this.mProc.persistent) {
        getWindow().setType(2010);
      }
      this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(6), 300000L);
      return;
      bool = true;
      break;
    }
    label316:
    this.mName = this.mProc.processName;
    if (this.mRepeating) {}
    for (i = 17040295;; i = 17040293)
    {
      setTitle(localResources.getString(i, new Object[] { paramActivityManagerService.unicodeWrap(this.mName.toString()) }));
      break;
    }
  }
  
  public void dismiss()
  {
    if (!this.mResult.mHasResult) {
      this.mResult.set(1);
    }
    super.dismiss();
  }
  
  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    case 16909115: 
    default: 
      return;
    case 16909117: 
      this.mHandler.obtainMessage(3).sendToTarget();
      return;
    case 16909116: 
      this.mHandler.obtainMessage(2).sendToTarget();
      return;
    case 16909114: 
      this.mHandler.obtainMessage(1).sendToTarget();
      return;
    }
    this.mHandler.obtainMessage(5).sendToTarget();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    int k = 8;
    super.onCreate(paramBundle);
    Object localObject = (FrameLayout)findViewById(16908331);
    paramBundle = getContext();
    LayoutInflater.from(paramBundle).inflate(17367092, (ViewGroup)localObject, true);
    boolean bool;
    label65:
    int j;
    if (!this.mRepeating)
    {
      bool = this.mIsRestartable;
      if (this.mProc.errorReportReceiver == null) {
        break label227;
      }
      i = 1;
      localObject = (TextView)findViewById(16909117);
      ((TextView)localObject).setOnClickListener(this);
      if (!bool) {
        break label232;
      }
      j = 0;
      label90:
      ((TextView)localObject).setVisibility(j);
      localObject = (TextView)findViewById(16909116);
      ((TextView)localObject).setOnClickListener(this);
      if (i == 0) {
        break label238;
      }
      i = 0;
      label120:
      ((TextView)localObject).setVisibility(i);
      localObject = (TextView)findViewById(16909114);
      if (bool) {
        break label244;
      }
      i = 0;
      label145:
      ((TextView)localObject).setVisibility(i);
      ((TextView)localObject).setOnClickListener(this);
      if ((ActivityManagerService.IS_USER_BUILD) || (Settings.Global.getInt(paramBundle.getContentResolver(), "development_settings_enabled", 0) == 0)) {
        break label250;
      }
    }
    label227:
    label232:
    label238:
    label244:
    label250:
    for (int i = 1;; i = 0)
    {
      paramBundle = (TextView)findViewById(16909118);
      paramBundle.setOnClickListener(this);
      j = k;
      if (i != 0) {
        j = 0;
      }
      paramBundle.setVisibility(j);
      findViewById(16909100).setVisibility(0);
      return;
      bool = false;
      break;
      i = 0;
      break label65;
      j = 8;
      break label90;
      i = 8;
      break label120;
      i = 8;
      break label145;
    }
  }
  
  public void onStart()
  {
    super.onStart();
    getContext().registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
  }
  
  protected void onStop()
  {
    super.onStop();
    getContext().unregisterReceiver(this.mReceiver);
  }
  
  static class Data
  {
    boolean isRestartableForService;
    ProcessRecord proc;
    boolean repeating;
    AppErrorResult result;
    TaskRecord task;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/AppErrorDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */