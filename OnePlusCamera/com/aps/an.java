package com.aps;

import android.location.GpsStatus.NmeaListener;

final class an
  implements GpsStatus.NmeaListener
{
  private an(ak paramak) {}
  
  public final void onNmeaReceived(long paramLong, String paramString)
  {
    try
    {
      ak.c(this.a, paramLong);
      ak.a(this.a, paramString);
      return;
    }
    catch (Exception paramString) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/an.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */