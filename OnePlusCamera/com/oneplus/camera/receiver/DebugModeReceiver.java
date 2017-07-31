package com.oneplus.camera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.oneplus.camera.OPCameraActivity;

public class DebugModeReceiver
  extends BroadcastReceiver
{
  private static final String INTENT_ACTION_SECRET_CODE = "android.provider.Telephony.SECRET_CODE";
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent.getAction().equals("android.provider.Telephony.SECRET_CODE"))
    {
      paramIntent = new Intent("android.intent.action.MAIN");
      paramIntent.setClass(paramContext, OPCameraActivity.class).setFlags(268435456);
      paramIntent.putExtra("CameraActivity.IsDebugMode", true);
      paramContext.startActivity(paramIntent);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/receiver/DebugModeReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */