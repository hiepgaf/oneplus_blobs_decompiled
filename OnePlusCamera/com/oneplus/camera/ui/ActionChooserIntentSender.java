package com.oneplus.camera.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ActionChooserIntentSender
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = ActionChooser.getActiveChooser(paramIntent.getLongExtra("ActionChooserId", 0L));
    if (paramContext == null) {
      return;
    }
    paramIntent = (ComponentName)paramIntent.getParcelableExtra("android.intent.extra.CHOSEN_COMPONENT");
    if (paramIntent == null) {
      return;
    }
    paramContext.notifyActivitySelected(paramIntent);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ActionChooserIntentSender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */