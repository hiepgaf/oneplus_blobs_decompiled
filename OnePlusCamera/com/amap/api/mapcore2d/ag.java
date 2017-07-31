package com.amap.api.mapcore2d;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.RemoteException;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.VisibleRegion;

public abstract interface ag
{
  public abstract Point a(LatLng paramLatLng)
    throws RemoteException;
  
  public abstract LatLng a(Point paramPoint)
    throws RemoteException;
  
  public abstract VisibleRegion a()
    throws RemoteException;
  
  public abstract PointF b(LatLng paramLatLng)
    throws RemoteException;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */