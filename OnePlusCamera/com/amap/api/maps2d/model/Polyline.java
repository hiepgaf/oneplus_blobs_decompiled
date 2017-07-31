package com.amap.api.maps2d.model;

import android.os.RemoteException;
import com.amap.api.mapcore2d.af;
import com.amap.api.mapcore2d.cj;
import java.util.List;

public class Polyline
{
  private final af a;
  
  public Polyline(af paramaf)
  {
    this.a = paramaf;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Polyline)) {}
    try
    {
      if (this.a != null)
      {
        boolean bool = this.a.a(((Polyline)paramObject).a);
        return bool;
        return false;
      }
      return false;
    }
    catch (RemoteException paramObject)
    {
      cj.a((Throwable)paramObject, "Polyline", "equals");
      throw new RuntimeRemoteException((RemoteException)paramObject);
    }
  }
  
  public int getColor()
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
      cj.a(localRemoteException, "Polyline", "getColor");
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
      cj.a(localRemoteException, "Polyline", "getId");
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
      cj.a(localRemoteException, "Polyline", "getPoints");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public float getWidth()
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
      cj.a(localRemoteException, "Polyline", "getWidth");
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
      cj.a(localRemoteException, "Polyline", "getZIndex");
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
      cj.a(localRemoteException, "Polyline", "hashCode");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public boolean isDottedLine()
  {
    if (this.a != null) {
      return this.a.j();
    }
    return false;
  }
  
  public boolean isGeodesic()
  {
    if (this.a != null) {
      return this.a.k();
    }
    return false;
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
      cj.a(localRemoteException, "Polyline", "isVisible");
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
      cj.a(localRemoteException, "Polyline", "remove");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setColor(int paramInt)
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
      cj.a(localRemoteException, "Polyline", "setColor");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setDottedLine(boolean paramBoolean)
  {
    if (this.a != null)
    {
      this.a.b(paramBoolean);
      return;
    }
  }
  
  public void setGeodesic(boolean paramBoolean)
  {
    try
    {
      if (this.a != null)
      {
        if (this.a.k() == paramBoolean) {
          return;
        }
        List localList = getPoints();
        this.a.c(paramBoolean);
        setPoints(localList);
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Polyline", "setGeodesic");
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
      cj.a(paramList, "Polyline", "setPoints");
      throw new RuntimeRemoteException(paramList);
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
      cj.a(localRemoteException, "Polyline", "setVisible");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setWidth(float paramFloat)
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
      cj.a(localRemoteException, "Polyline", "setWidth");
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
      cj.a(localRemoteException, "Polyline", "setZIndex");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/Polyline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */