package com.android.server;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.TextView;

public class PermissionDialog
  extends BasePermissionDialog
{
  static final int ALLOWED_REQ = 2;
  static final int IGNORED_REQ = 4;
  static final int IGNORED_REQ_TIMEOUT = 8;
  private static final String TAG = "PermInfo";
  static final long TIMEOUT_WAIT = 15000L;
  private CheckBox checkSta;
  private Context contId;
  private int inputId;
  private final String inputPackage;
  private final int mDef;
  final CharSequence[] mOpLabels;
  private final Handler myHandle = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      boolean bool = PermissionDialog.-get0(PermissionDialog.this).isChecked();
      int i;
      switch (paramAnonymousMessage.what)
      {
      case 3: 
      default: 
        i = 1;
        bool = false;
      }
      for (;;)
      {
        PermissionDialog.-get4(PermissionDialog.this).notifyOperation(PermissionDialog.-get3(PermissionDialog.this), PermissionDialog.-get1(PermissionDialog.this), PermissionDialog.-get2(PermissionDialog.this), i, bool);
        PermissionDialog.this.dismiss();
        return;
        i = 0;
        continue;
        i = 1;
      }
    }
  };
  private final AppOpsService opsServ;
  private View viewId;
  
  public PermissionDialog(Context paramContext, AppOpsService paramAppOpsService, int paramInt1, int paramInt2, String paramString)
  {
    super(paramContext);
    this.opsServ = paramAppOpsService;
    this.inputPackage = paramString;
    this.contId = paramContext;
    this.mDef = paramInt1;
    paramContext = paramContext.getResources();
    this.inputId = paramInt2;
    this.mOpLabels = paramContext.getTextArray(17235982);
    setCancelable(false);
    setButton(-1, paramContext.getString(17040931), this.myHandle.obtainMessage(2));
    setButton(-2, paramContext.getString(17040932), this.myHandle.obtainMessage(4));
    setTitle(paramContext.getString(17040930));
    paramContext = getWindow().getAttributes();
    paramContext.setTitle("Permission: " + getAppName(this.inputPackage));
    paramContext.privateFlags |= 0x110;
    getWindow().setAttributes(paramContext);
    this.viewId = getLayoutInflater().inflate(17367199, null);
    paramString = (TextView)this.viewId.findViewById(16909262);
    this.checkSta = ((CheckBox)this.viewId.findViewById(16909264));
    paramContext = (TextView)this.viewId.findViewById(16909265);
    this.checkSta.setVisibility(4);
    paramContext.setVisibility(4);
    paramAppOpsService = getAppName(this.inputPackage);
    paramContext = paramAppOpsService;
    if (paramAppOpsService == null) {
      paramContext = this.inputPackage;
    }
    paramString.setText(paramContext + ": " + this.mOpLabels[(this.mDef - 64)]);
    setView(this.viewId);
    this.myHandle.sendMessageDelayed(this.myHandle.obtainMessage(8), 15000L);
  }
  
  private String getAppName(String paramString)
  {
    PackageManager localPackageManager = this.contId.getPackageManager();
    try
    {
      paramString = localPackageManager.getApplicationInfo(paramString, 8704);
      if (paramString != null) {
        return (String)localPackageManager.getApplicationLabel(paramString);
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      return null;
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/PermissionDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */