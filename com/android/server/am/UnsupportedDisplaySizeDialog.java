package com.android.server.am;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class UnsupportedDisplaySizeDialog
{
  private final AlertDialog mDialog;
  private final String mPackageName;
  
  public UnsupportedDisplaySizeDialog(ActivityManagerService paramActivityManagerService, Context paramContext, ApplicationInfo paramApplicationInfo)
  {
    this.mPackageName = paramApplicationInfo.packageName;
    paramApplicationInfo = paramContext.getString(17040317, new Object[] { paramApplicationInfo.loadSafeLabel(paramContext.getPackageManager()) });
    this.mDialog = new AlertDialog.Builder(paramContext).setPositiveButton(17039370, null).setMessage(paramApplicationInfo).setView(17367301).create();
    this.mDialog.create();
    paramContext = this.mDialog.getWindow();
    paramContext.setType(2002);
    paramContext.getAttributes().setTitle("UnsupportedDisplaySizeDialog");
    paramContext = (CheckBox)this.mDialog.findViewById(16909112);
    paramContext.setChecked(true);
    paramContext.setOnCheckedChangeListener(new -void__init__com_android_server_am_ActivityManagerService_service_android_content_Context_context_android_content_pm_ApplicationInfo_appInfo_LambdaImpl0(paramActivityManagerService));
  }
  
  public void dismiss()
  {
    this.mDialog.dismiss();
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public void show()
  {
    this.mDialog.show();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/UnsupportedDisplaySizeDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */