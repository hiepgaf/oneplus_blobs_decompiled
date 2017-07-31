package com.amap.api.mapcore2d;

import android.graphics.Point;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.CameraPosition.Builder;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;

public class l
{
  a a = a.a;
  float b;
  float c;
  float d;
  float e;
  CameraPosition f;
  LatLngBounds g;
  int h;
  int i;
  int j;
  Point k = null;
  boolean l = false;
  private float m;
  private float n;
  private ad o;
  
  public static l a()
  {
    return new l();
  }
  
  public static l a(float paramFloat)
  {
    l locall = a();
    locall.a = a.f;
    locall.d = paramFloat;
    return locall;
  }
  
  public static l a(float paramFloat1, float paramFloat2)
  {
    l locall = a();
    locall.a = a.h;
    locall.b = paramFloat1;
    locall.c = paramFloat2;
    return locall;
  }
  
  public static l a(float paramFloat, Point paramPoint)
  {
    l locall = a();
    locall.a = a.g;
    locall.e = paramFloat;
    locall.k = paramPoint;
    return locall;
  }
  
  static l a(ad paramad, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    l locall = a();
    locall.a = a.l;
    locall.o = paramad;
    locall.d = paramFloat1;
    locall.n = paramFloat2;
    locall.m = paramFloat3;
    return locall;
  }
  
  public static l a(CameraPosition paramCameraPosition)
  {
    l locall = a();
    locall.a = a.i;
    locall.f = paramCameraPosition;
    return locall;
  }
  
  public static l a(LatLng paramLatLng)
  {
    l locall = a();
    locall.a = a.c;
    locall.f = new CameraPosition(paramLatLng, 0.0F, 0.0F, 0.0F);
    return locall;
  }
  
  public static l a(LatLng paramLatLng, float paramFloat)
  {
    return a(CameraPosition.builder().target(paramLatLng).zoom(paramFloat).build());
  }
  
  public static l a(LatLng paramLatLng, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return a(CameraPosition.builder().target(paramLatLng).zoom(paramFloat1).bearing(paramFloat2).tilt(paramFloat3).build());
  }
  
  public static l a(LatLngBounds paramLatLngBounds, int paramInt)
  {
    l locall = a();
    locall.a = a.j;
    locall.g = paramLatLngBounds;
    locall.h = paramInt;
    return locall;
  }
  
  public static l a(LatLngBounds paramLatLngBounds, int paramInt1, int paramInt2, int paramInt3)
  {
    l locall = a();
    locall.a = a.k;
    locall.g = paramLatLngBounds;
    locall.h = paramInt3;
    locall.i = paramInt1;
    locall.j = paramInt2;
    return locall;
  }
  
  public static l b()
  {
    l locall = a();
    locall.a = a.b;
    return locall;
  }
  
  public static l b(float paramFloat)
  {
    return a(paramFloat, null);
  }
  
  public static l b(LatLng paramLatLng)
  {
    return a(CameraPosition.builder().target(paramLatLng).build());
  }
  
  public static l c()
  {
    l locall = a();
    locall.a = a.e;
    return locall;
  }
  
  static enum a
  {
    private a() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/l.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */