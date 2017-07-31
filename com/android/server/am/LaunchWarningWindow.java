package com.android.server.am;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.util.TypedValue;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public final class LaunchWarningWindow
  extends Dialog
{
  public LaunchWarningWindow(Context paramContext, ActivityRecord paramActivityRecord1, ActivityRecord paramActivityRecord2)
  {
    super(paramContext, 16974977);
    requestWindowFeature(3);
    getWindow().setType(2003);
    getWindow().addFlags(24);
    setContentView(17367160);
    setTitle(paramContext.getText(17040311));
    TypedValue localTypedValue = new TypedValue();
    getContext().getTheme().resolveAttribute(16843605, localTypedValue, true);
    getWindow().setFeatureDrawableResource(3, localTypedValue.resourceId);
    ((ImageView)findViewById(16909200)).setImageDrawable(paramActivityRecord2.info.applicationInfo.loadIcon(paramContext.getPackageManager()));
    ((TextView)findViewById(16909201)).setText(paramContext.getResources().getString(17040312, new Object[] { paramActivityRecord2.info.applicationInfo.loadLabel(paramContext.getPackageManager()).toString() }));
    ((ImageView)findViewById(16909202)).setImageDrawable(paramActivityRecord1.info.applicationInfo.loadIcon(paramContext.getPackageManager()));
    ((TextView)findViewById(16909203)).setText(paramContext.getResources().getString(17040313, new Object[] { paramActivityRecord1.info.applicationInfo.loadLabel(paramContext.getPackageManager()).toString() }));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/LaunchWarningWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */