package com.amap.api.maps2d.model;

import android.os.RemoteException;
import com.amap.api.mapcore2d.cj;
import com.amap.api.mapcore2d.x;

public final class Circle
{
  private final x a;
  
  public Circle(x paramx)
  {
    this.a = paramx;
  }
  
  public boolean contains(LatLng paramLatLng)
  {
    try
    {
      if (this.a != null)
      {
        boolean bool = this.a.b(paramLatLng);
        return bool;
      }
      return false;
    }
    catch (RemoteException paramLatLng)
    {
      cj.a(paramLatLng, "Circle", "contains");
      throw new RuntimeRemoteException(paramLatLng);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Circle)) {}
    try
    {
      if (this.a != null)
      {
        boolean bool = this.a.a(((Circle)paramObject).a);
        return bool;
        return false;
      }
      return false;
    }
    catch (RemoteException paramObject)
    {
      cj.a((Throwable)paramObject, "Circle", "equals");
      throw new RuntimeRemoteException((RemoteException)paramObject);
    }
  }
  
  public LatLng getCenter()
  {
    try
    {
      if (this.a != null)
      {
        LatLng localLatLng = this.a.g();
        return localLatLng;
      }
      return null;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Circle", "getCenter");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public int getFillColor()
  {
    try
    {
      if (this.a != null)
      {
        int i = this.a.k();
        return i;
      }
      return 0;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Circle", "getFillColor");
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
      cj.a(localRemoteException, "Circle", "getId");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public double getRadius()
  {
    try
    {
      if (this.a != null)
      {
        double d = this.a.h();
        return d;
      }
      return 0.0D;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Circle", "getRadius");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public int getStrokeColor()
  {
    try
    {
      if (this.a != null)
      {
        int i = this.a.j();
        return i;
      }
      return 0;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Circle", "getStrokeColor");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public float getStrokeWidth()
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
      cj.a(localRemoteException, "Circle", "getStrokeWidth");
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
      cj.a(localRemoteException, "Circle", "getZIndex");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public int hashCode()
  {
    try
    {
      if (this.a != null)
      {
        int i = this.a.f();
        return i;
      }
      return 0;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Circle", "hashCode");
      throw new RuntimeRemoteException(localRemoteException);
    }
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
      cj.a(localRemoteException, "Circle", "isVisible");
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
      cj.a(localRemoteException, "Circle", "remove");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setCenter(LatLng paramLatLng)
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
      cj.a(paramLatLng, "Circle", "setCenter");
      throw new RuntimeRemoteException(paramLatLng);
    }
  }
  
  public void setFillColor(int paramInt)
  {
    try
    {
      if (this.a != null)
      {
        this.a.b(paramInt);
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Circle", "setFillColor");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setRadius(double paramDouble)
  {
    try
    {
      if (this.a != null)
      {
        this.a.a(paramDouble);
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Circle", "setRadius");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setStrokeColor(int paramInt)
  {
    try
    {
      if (this.a != null)
      {
        this.a.a(paramInt);
        return;
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Circle", "setStrokeColor");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setStrokeWidth(float paramFloat)
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
      cj.a(localRemoteException, "Circle", "setStrokeWidth");
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
      cj.a(localRemoteException, "Circle", "setVisible");
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
      cj.a(localRemoteException, "Circle", "setZIndex");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/Circle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */