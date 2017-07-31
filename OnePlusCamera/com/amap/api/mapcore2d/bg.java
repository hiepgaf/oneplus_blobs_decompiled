package com.amap.api.mapcore2d;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.RemoteException;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.LatLngBounds.Builder;
import com.amap.api.maps2d.model.VisibleRegion;

class bg
  implements ag
{
  private String a = "ProjectionDelegateImp";
  private w b;
  
  public bg(w paramw)
  {
    this.b = paramw;
  }
  
  public Point a(LatLng paramLatLng)
    throws RemoteException
  {
    ad localad = new ad();
    this.b.b(paramLatLng.latitude, paramLatLng.longitude, localad);
    return new Point(localad.a, localad.b);
  }
  
  public LatLng a(Point paramPoint)
    throws RemoteException
  {
    r localr = new r();
    this.b.a(paramPoint.x, paramPoint.y, localr);
    return new LatLng(localr.b, localr.a);
  }
  
  public VisibleRegion a()
    throws RemoteException
  {
    for (;;)
    {
      try
      {
        int i = this.b.c();
        int j = this.b.d();
        localObject3 = a(new Point(0, 0));
        LatLngBounds localLatLngBounds;
        Object localObject1;
        cj.a(localThrowable1, this.a, "getVisibleRegion");
      }
      catch (Throwable localThrowable1)
      {
        try
        {
          localLatLng3 = a(new Point(i, 0));
        }
        catch (Throwable localThrowable2)
        {
          for (;;)
          {
            Object localObject3;
            Object localObject2;
            localLatLng1 = null;
            localLatLng2 = null;
            LatLng localLatLng3 = null;
          }
        }
        try
        {
          localLatLng2 = a(new Point(0, j));
        }
        catch (Throwable localThrowable3)
        {
          localLatLng1 = null;
          localLatLng2 = null;
          break label144;
        }
        try
        {
          localLatLng1 = a(new Point(i, j));
        }
        catch (Throwable localThrowable4)
        {
          localLatLng1 = null;
          break label144;
        }
        try
        {
          localLatLngBounds = LatLngBounds.builder().include(localLatLng2).include(localLatLng1).include((LatLng)localObject3).include(localLatLng3).build();
          localObject1 = localObject3;
          localObject3 = localLatLngBounds;
          return new VisibleRegion(localLatLng2, localLatLng1, (LatLng)localObject1, localLatLng3, (LatLngBounds)localObject3);
        }
        catch (Throwable localThrowable5)
        {
          break label144;
        }
        localThrowable1 = localThrowable1;
        localLatLng1 = null;
        localLatLng2 = null;
        localLatLng3 = null;
        localObject3 = null;
      }
      label144:
      localObject2 = localObject3;
      localObject3 = null;
    }
  }
  
  public PointF b(LatLng paramLatLng)
    throws RemoteException
  {
    r localr = new r();
    this.b.a(paramLatLng.latitude, paramLatLng.longitude, localr);
    return new PointF((float)localr.a, (float)localr.b);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */