package com.aps;

import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;

final class am
  extends PhoneStateListener
{
  private am(ak paramak) {}
  
  public final void onCellLocationChanged(CellLocation paramCellLocation)
  {
    try
    {
      ak.b(this.a, System.currentTimeMillis());
      ak.a(this.a, paramCellLocation);
      super.onCellLocationChanged(paramCellLocation);
      return;
    }
    catch (Exception paramCellLocation) {}
  }
  
  public final void onServiceStateChanged(ServiceState paramServiceState)
  {
    try
    {
      if (paramServiceState.getState() != 0) {
        ak.a(this.a, false);
      }
      for (;;)
      {
        super.onServiceStateChanged(paramServiceState);
        return;
        ak.a(this.a, true);
        String[] arrayOfString = ak.a(ak.f(this.a));
        ak.a(this.a, Integer.parseInt(arrayOfString[0]));
        ak.b(this.a, Integer.parseInt(arrayOfString[1]));
      }
      return;
    }
    catch (Exception paramServiceState) {}
  }
  
  public final void onSignalStrengthsChanged(SignalStrength paramSignalStrength)
  {
    try
    {
      if (!ak.g(this.a))
      {
        ak.c(this.a, paramSignalStrength.getGsmSignalStrength());
        if (ak.h(this.a) == 99) {
          break label75;
        }
        ak.c(this.a, ak.h(this.a) * 2 - 113);
      }
      for (;;)
      {
        super.onSignalStrengthsChanged(paramSignalStrength);
        return;
        ak.c(this.a, paramSignalStrength.getCdmaDbm());
        continue;
        label75:
        ak.c(this.a, -1);
      }
      return;
    }
    catch (Exception paramSignalStrength) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/am.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */