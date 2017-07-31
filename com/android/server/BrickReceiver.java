package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemService;
import android.util.Slog;

public class BrickReceiver
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    Slog.w("BrickReceiver", "!!! BRICKING DEVICE !!!");
    SystemService.start("brick");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/BrickReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */