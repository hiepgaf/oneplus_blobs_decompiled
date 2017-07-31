package com.amap.api.maps2d;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.RemoteException;
import com.amap.api.mapcore2d.ag;
import com.amap.api.mapcore2d.cj;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.RuntimeRemoteException;
import com.amap.api.maps2d.model.VisibleRegion;

public class Projection
{
  private final ag a;
  
  Projection(ag paramag)
  {
    this.a = paramag;
  }
  
  public LatLng fromScreenLocation(Point paramPoint)
  {
    try
    {
      paramPoint = this.a.a(paramPoint);
      return paramPoint;
    }
    catch (RemoteException paramPoint)
    {
      cj.a(paramPoint, "Projection", "fromScreenLocation");
      throw new RuntimeRemoteException(paramPoint);
    }
  }
  
  public VisibleRegion getVisibleRegion()
  {
    try
    {
      VisibleRegion localVisibleRegion = this.a.a();
      return localVisibleRegion;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Projection", "getVisibleRegion");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public PointF toMapLocation(LatLng paramLatLng)
  {
    try
    {
      paramLatLng = this.a.b(paramLatLng);
      return paramLatLng;
    }
    catch (RemoteException paramLatLng)
    {
      cj.a(paramLatLng, "Projection", "toMapLocation");
      throw new RuntimeRemoteException(paramLatLng);
    }
  }
  
  public Point toScreenLocation(LatLng paramLatLng)
  {
    try
    {
      paramLatLng = this.a.a(paramLatLng);
      return paramLatLng;
    }
    catch (RemoteException paramLatLng)
    {
      cj.a(paramLatLng, "Projection", "toScreenLocation");
      throw new RuntimeRemoteException(paramLatLng);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/Projection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */