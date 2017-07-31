package com.android.server.am;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public final class CompatModeDialog
  extends Dialog
{
  final CheckBox mAlwaysShow;
  final ApplicationInfo mAppInfo;
  final Switch mCompatEnabled;
  final View mHint;
  final ActivityManagerService mService;
  
  public CompatModeDialog(ActivityManagerService paramActivityManagerService, Context paramContext, ApplicationInfo paramApplicationInfo)
  {
    super(paramContext, 16973936);
    setCancelable(true);
    setCanceledOnTouchOutside(true);
    getWindow().requestFeature(1);
    getWindow().setType(2002);
    getWindow().setGravity(81);
    this.mService = paramActivityManagerService;
    this.mAppInfo = paramApplicationInfo;
    setContentView(17367090);
    this.mCompatEnabled = ((Switch)findViewById(16909111));
    this.mCompatEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CompoundButton arg1, boolean paramAnonymousBoolean)
      {
        synchronized (CompatModeDialog.this.mService)
        {
          ActivityManagerService.boostPriorityForLockedSection();
          CompatModePackages localCompatModePackages = CompatModeDialog.this.mService.mCompatModePackages;
          String str = CompatModeDialog.this.mAppInfo.packageName;
          if (CompatModeDialog.this.mCompatEnabled.isChecked())
          {
            i = 1;
            localCompatModePackages.setPackageScreenCompatModeLocked(str, i);
            CompatModeDialog.this.updateControls();
            ActivityManagerService.resetPriorityAfterLockedSection();
            return;
          }
          int i = 0;
        }
      }
    });
    this.mAlwaysShow = ((CheckBox)findViewById(16909112));
    this.mAlwaysShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CompoundButton arg1, boolean paramAnonymousBoolean)
      {
        synchronized (CompatModeDialog.this.mService)
        {
          ActivityManagerService.boostPriorityForLockedSection();
          CompatModeDialog.this.mService.mCompatModePackages.setPackageAskCompatModeLocked(CompatModeDialog.this.mAppInfo.packageName, CompatModeDialog.this.mAlwaysShow.isChecked());
          CompatModeDialog.this.updateControls();
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
      }
    });
    this.mHint = findViewById(16909113);
    updateControls();
  }
  
  void updateControls()
  {
    boolean bool = true;
    int i = 0;
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      int j = this.mService.mCompatModePackages.computeCompatModeLocked(this.mAppInfo);
      Object localObject1 = this.mCompatEnabled;
      if (j == 1)
      {
        ((Switch)localObject1).setChecked(bool);
        bool = this.mService.mCompatModePackages.getPackageAskCompatModeLocked(this.mAppInfo.packageName);
        this.mAlwaysShow.setChecked(bool);
        localObject1 = this.mHint;
        if (bool) {
          i = 4;
        }
        ((View)localObject1).setVisibility(i);
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
      bool = false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/CompatModeDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */