package com.aps;

import android.telephony.CellLocation;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

public final class ba
{
  int a = Integer.MAX_VALUE;
  int b = Integer.MAX_VALUE;
  int c = Integer.MAX_VALUE;
  int d = Integer.MAX_VALUE;
  int e = Integer.MAX_VALUE;
  
  ba(CellLocation paramCellLocation)
  {
    if (paramCellLocation == null) {}
    do
    {
      return;
      if ((paramCellLocation instanceof GsmCellLocation)) {
        break;
      }
    } while (!(paramCellLocation instanceof CdmaCellLocation));
    paramCellLocation = (CdmaCellLocation)paramCellLocation;
    this.c = paramCellLocation.getBaseStationId();
    this.b = paramCellLocation.getNetworkId();
    this.a = paramCellLocation.getSystemId();
    return;
    paramCellLocation = (GsmCellLocation)paramCellLocation;
    this.e = paramCellLocation.getCid();
    this.d = paramCellLocation.getLac();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ba.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */