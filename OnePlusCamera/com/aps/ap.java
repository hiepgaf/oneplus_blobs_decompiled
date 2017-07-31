package com.aps;

import android.net.wifi.WifiManager;
import java.util.TimerTask;

final class ap
  extends TimerTask
{
  ap(ao paramao) {}
  
  public final void run()
  {
    try
    {
      if (!bd.a) {
        return;
      }
      if (ak.c(this.a.a) != null)
      {
        ak.c(this.a.a).startScan();
        return;
      }
    }
    catch (Exception localException) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */