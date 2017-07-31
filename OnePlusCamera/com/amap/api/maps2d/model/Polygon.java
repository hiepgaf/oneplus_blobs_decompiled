package com.amap.api.maps2d.model;

import android.os.RemoteException;
import com.amap.api.mapcore2d.ae;
import com.amap.api.mapcore2d.cj;
import java.util.List;

public final class Polygon
{
  private ae a;
  
  public Polygon(ae paramae)
  {
    this.a = paramae;
  }
  
  public boolean contains(LatLng paramLatLng)
  {
    try
    {
      if (this.a != null)
      {
        boolean bool = this.a.a(paramLatLng);
        return bool;
      }
      return false;
    }
    catch (RemoteException paramLatLng)
    {
      cj.a(paramLatLng, "Polygon", "contains");
      throw new RuntimeRemoteException(paramLatLng);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Polygon)) {}
    try
    {
      if (this.a != null)
      {
        boolean bool = this.a.a(((Polygon)paramObject).a);
        return bool;
        return false;
      }
      return false;
    }
    catch (RemoteException paramObject)
    {
      cj.a((Throwable)paramObject, "Polygon", "equeals");
    }
    return false;
  }
  
  public int getFillColor()
  {
    try
    {
      if (this.a != null)
      {
        int i = this.a.h();
        return i;
      }
      return 0;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Polygon", "getFillColor");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public String getId()
  {
    try
    {
      if (this.a != null)
      {
        String str = this.a.c();
        return str;
      }
      return null;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Polygon", "getId");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public List<LatLng> getPoints()
  {
    try
    {
      if (this.a != null)
      {
        List localList = this.a.i();
        return localList;
      }
      return null;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Polygon", "getPoints");
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
      cj.a(localRemoteException, "Polygon", "getStrokeColor");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public float getStrokeWidth()
  {
    try
    {
      if (this.a != null)
      {
        float f = this.a.g();
        return f;
      }
      return 0.0F;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Polygon", "getStrokeWidth");
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
      cj.a(localRemoteException, "Polygon", "getZIndex");
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
      cj.a(localRemoteException, "Polygon", "hashCode");
    }
    return super.hashCode();
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
      return true;
    }
    catch (RemoteException localRemoteException)
    {
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
      cj.a(localRemoteException, "Polygon", "remove");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setFillColor(int paramInt)
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
      cj.a(localRemoteException, "Polygon", "setFillColor");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setPoints(List<LatLng> paramList)
  {
    try
    {
      if (this.a != null)
      {
        this.a.a(paramList);
        return;
      }
      return;
    }
    catch (RemoteException paramList)
    {
      cj.a(paramList, "Polygon", "setPoints");
      throw new RuntimeRemoteException(paramList);
    }
  }
  
  public void setStrokeColor(int paramInt)
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
      cj.a(localRemoteException, "Polygon", "setStrokeColor");
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
      cj.a(localRemoteException, "Polygon", "setStrokeWidth");
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
      cj.a(localRemoteException, "Polygon", "setVisible");
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
      cj.a(localRemoteException, "Polygon", "setZIndex");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/Polygon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */