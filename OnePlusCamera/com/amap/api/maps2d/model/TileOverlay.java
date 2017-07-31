package com.amap.api.maps2d.model;

import com.amap.api.mapcore2d.aj;

public final class TileOverlay
{
  private aj a;
  
  public TileOverlay(aj paramaj)
  {
    this.a = paramaj;
  }
  
  public void clearTileCache()
  {
    this.a.b();
  }
  
  public boolean equals(Object paramObject)
  {
    return this.a.a(this.a);
  }
  
  public String getId()
  {
    return this.a.c();
  }
  
  public float getZIndex()
  {
    return this.a.d();
  }
  
  public int hashCode()
  {
    return this.a.f();
  }
  
  public boolean isVisible()
  {
    return this.a.e();
  }
  
  public void remove()
  {
    this.a.a();
  }
  
  public void setVisible(boolean paramBoolean)
  {
    this.a.a(paramBoolean);
  }
  
  public void setZIndex(float paramFloat)
  {
    this.a.a(paramFloat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/TileOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */