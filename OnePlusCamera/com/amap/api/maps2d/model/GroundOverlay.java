package com.amap.api.maps2d.model;

import android.os.RemoteException;
import com.amap.api.mapcore2d.cj;
import com.amap.api.mapcore2d.y;

public final class GroundOverlay
{
  private y a;
  
  public GroundOverlay(y paramy)
  {
    this.a = paramy;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof GroundOverlay)) {
      try
      {
        throw new RemoteException();
      }
      catch (RemoteException paramObject)
      {
        cj.a((Throwable)paramObject, "GroundOverlay", "equals");
        throw new RuntimeRemoteException((RemoteException)paramObject);
      }
    }
    return false;
  }
  
  public float getBearing()
  {
    try
    {
      if (this.a != null)
      {
        float f = this.a.m();
        return f;
      }
      return 0.0F;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "getBearing");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public LatLngBounds getBounds()
  {
    try
    {
      if (this.a != null)
      {
        LatLngBounds localLatLngBounds = this.a.k();
        return localLatLngBounds;
      }
      return null;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "getBounds");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public float getHeight()
  {
    try
    {
      if (this.a != null)
      {
        float f = this.a.j();
        return f;
      }
      return 0.0F;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "getHeight");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public String getId()
  {
    try
    {
      if (this.a != null) {
        return this.a.c();
      }
      return "";
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "getId");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public LatLng getPosition()
  {
    try
    {
      if (this.a != null)
      {
        LatLng localLatLng = this.a.h();
        return localLatLng;
      }
      return null;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "getPosition");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public float getTransparency()
  {
    try
    {
      if (this.a != null)
      {
        float f = this.a.n();
        return f;
      }
      return 0.0F;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "getTransparency");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public float getWidth()
  {
    try
    {
      if (this.a != null)
      {
        float f = this.a.i();
        return f;
      }
      return 0.0F;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "getWidth");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public float getZIndex()
  {
    try
    {
      if (this.a != null)
      {
        float f = this.a.d();
        return f;
      }
      return 0.0F;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "getZIndex");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public int hashCode()
  {
    if (this.a != null) {
      return this.a.hashCode();
    }
    return 0;
  }
  
  public boolean isVisible()
  {
    try
    {
      if (this.a != null)
      {
        boolean bool = this.a.e();
        return bool;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "isVisible");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void remove()
  {
    try
    {
      if (this.a != null)
      {
        this.a.b();
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "remove");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setBearing(float paramFloat)
  {
    try
    {
      if (this.a != null)
      {
        this.a.c(paramFloat);
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "setBearing");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setDimensions(float paramFloat)
  {
    try
    {
      if (this.a != null)
      {
        this.a.b(paramFloat);
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "setDimensions");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setDimensions(float paramFloat1, float paramFloat2)
  {
    try
    {
      if (this.a != null)
      {
        this.a.a(paramFloat1, paramFloat2);
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "setDimensions");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setImage(BitmapDescriptor paramBitmapDescriptor)
  {
    try
    {
      if (this.a != null)
      {
        this.a.a(paramBitmapDescriptor);
        return;
      }
      return;
    }
    catch (RemoteException paramBitmapDescriptor)
    {
      cj.a(paramBitmapDescriptor, "GroundOverlay", "setImage");
      throw new RuntimeRemoteException(paramBitmapDescriptor);
    }
  }
  
  public void setPosition(LatLng paramLatLng)
  {
    try
    {
      if (this.a != null)
      {
        this.a.a(paramLatLng);
        return;
      }
      return;
    }
    catch (RemoteException paramLatLng)
    {
      cj.a(paramLatLng, "GroundOverlay", "setPosition");
      throw new RuntimeRemoteException(paramLatLng);
    }
  }
  
  public void setPositionFromBounds(LatLngBounds paramLatLngBounds)
  {
    try
    {
      if (this.a != null)
      {
        this.a.a(paramLatLngBounds);
        return;
      }
      return;
    }
    catch (RemoteException paramLatLngBounds)
    {
      cj.a(paramLatLngBounds, "GroundOverlay", "setPositionFromBounds");
      throw new RuntimeRemoteException(paramLatLngBounds);
    }
  }
  
  public void setTransparency(float paramFloat)
  {
    try
    {
      if (this.a != null)
      {
        this.a.d(paramFloat);
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "setTransparency");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setVisible(boolean paramBoolean)
  {
    try
    {
      if (this.a != null)
      {
        this.a.a(paramBoolean);
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "setVisible");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setZIndex(float paramFloat)
  {
    try
    {
      if (this.a != null)
      {
        this.a.a(paramFloat);
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "GroundOverlay", "setZIndex");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/GroundOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */