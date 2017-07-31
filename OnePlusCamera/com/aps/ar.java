package com.aps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

final class ar
  extends BroadcastReceiver
{
  ar(y paramy) {}
  
  public final void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent == null) {}
    for (;;)
    {
      return;
      try
      {
        if (paramIntent.getAction().equals("android.location.GPS_FIX_CHANGE"))
        {
          y.b = false;
          return;
        }
      }
      catch (Exception paramContext) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */