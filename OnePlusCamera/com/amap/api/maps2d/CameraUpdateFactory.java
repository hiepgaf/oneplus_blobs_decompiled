package com.amap.api.maps2d;

import android.graphics.Point;
import com.amap.api.mapcore2d.l;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;

public final class CameraUpdateFactory
{
  public static CameraUpdate changeLatLng(LatLng paramLatLng)
  {
    return new CameraUpdate(l.a(paramLatLng));
  }
  
  public static CameraUpdate newCameraPosition(CameraPosition paramCameraPosition)
  {
    return new CameraUpdate(l.a(paramCameraPosition));
  }
  
  public static CameraUpdate newLatLng(LatLng paramLatLng)
  {
    return new CameraUpdate(l.b(paramLatLng));
  }
  
  public static CameraUpdate newLatLngBounds(LatLngBounds paramLatLngBounds, int paramInt)
  {
    return new CameraUpdate(l.a(paramLatLngBounds, paramInt));
  }
  
  public static CameraUpdate newLatLngBounds(LatLngBounds paramLatLngBounds, int paramInt1, int paramInt2, int paramInt3)
  {
    return new CameraUpdate(l.a(paramLatLngBounds, paramInt1, paramInt2, paramInt3));
  }
  
  public static CameraUpdate newLatLngZoom(LatLng paramLatLng, float paramFloat)
  {
    return new CameraUpdate(l.a(paramLatLng, paramFloat));
  }
  
  public static CameraUpdate scrollBy(float paramFloat1, float paramFloat2)
  {
    return new CameraUpdate(l.a(paramFloat1, paramFloat2));
  }
  
  public static CameraUpdate zoomBy(float paramFloat)
  {
    return new CameraUpdate(l.b(paramFloat));
  }
  
  public static CameraUpdate zoomBy(float paramFloat, Point paramPoint)
  {
    return new CameraUpdate(l.a(paramFloat, paramPoint));
  }
  
  public static CameraUpdate zoomIn()
  {
    return new CameraUpdate(l.b());
  }
  
  public static CameraUpdate zoomOut()
  {
    return new CameraUpdate(l.c());
  }
  
  public static CameraUpdate zoomTo(float paramFloat)
  {
    return new CameraUpdate(l.a(paramFloat));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/CameraUpdateFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */