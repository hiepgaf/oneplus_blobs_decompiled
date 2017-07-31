package com.amap.api.maps2d.model;

import android.os.RemoteException;
import com.amap.api.mapcore2d.aa;
import com.amap.api.mapcore2d.cj;
import java.util.ArrayList;

public final class Marker
{
  aa a;
  
  public Marker(aa paramaa)
  {
    this.a = paramaa;
  }
  
  public Marker(MarkerOptions paramMarkerOptions) {}
  
  public void destroy()
  {
    try
    {
      if (this.a == null) {
        return;
      }
      this.a.l();
      return;
    }
    catch (Exception localException)
    {
      cj.a(localException, "Marker", "destroy");
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Marker)) {
      return this.a.a(((Marker)paramObject).a);
    }
    return false;
  }
  
  public ArrayList<BitmapDescriptor> getIcons()
  {
    try
    {
      ArrayList localArrayList = this.a.p();
      return localArrayList;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Marker", "getIcons");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public String getId()
  {
    return this.a.d();
  }
  
  public Object getObject()
  {
    if (this.a == null) {
      return null;
    }
    return this.a.u();
  }
  
  public int getPeriod()
  {
    try
    {
      int i = this.a.o();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Marker", "getPeriod");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public LatLng getPosition()
  {
    return this.a.t();
  }
  
  public String getSnippet()
  {
    return this.a.g();
  }
  
  public String getTitle()
  {
    return this.a.f();
  }
  
  public float getZIndex()
  {
    return this.a.r();
  }
  
  public int hashCode()
  {
    return this.a.m();
  }
  
  public void hideInfoWindow()
  {
    this.a.j();
  }
  
  public boolean isDraggable()
  {
    return this.a.h();
  }
  
  public boolean isInfoWindowShown()
  {
    return this.a.k();
  }
  
  public boolean isVisible()
  {
    return this.a.s();
  }
  
  public void remove()
  {
    try
    {
      this.a.a();
      return;
    }
    catch (Exception localException)
    {
      cj.a(localException, "Marker", "remove");
    }
  }
  
  public void setAnchor(float paramFloat1, float paramFloat2)
  {
    this.a.a(paramFloat1, paramFloat2);
  }
  
  public void setDraggable(boolean paramBoolean)
  {
    this.a.a(paramBoolean);
  }
  
  public void setIcon(BitmapDescriptor paramBitmapDescriptor)
  {
    if (paramBitmapDescriptor == null) {
      return;
    }
    this.a.a(paramBitmapDescriptor);
  }
  
  public void setIcons(ArrayList<BitmapDescriptor> paramArrayList)
  {
    try
    {
      this.a.a(paramArrayList);
      return;
    }
    catch (RemoteException paramArrayList)
    {
      cj.a(paramArrayList, "Marker", "setIcons");
      throw new RuntimeRemoteException(paramArrayList);
    }
  }
  
  public void setObject(Object paramObject)
  {
    this.a.a(paramObject);
  }
  
  public void setPeriod(int paramInt)
  {
    try
    {
      this.a.a(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Marker", "setPeriod");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setPosition(LatLng paramLatLng)
  {
    this.a.b(paramLatLng);
  }
  
  public void setPositionByPixels(int paramInt1, int paramInt2)
  {
    try
    {
      this.a.a(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Marker", "setPositionByPixels");
      localRemoteException.printStackTrace();
    }
  }
  
  public void setRotateAngle(float paramFloat)
  {
    try
    {
      this.a.a(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "Marker", "setRotateAngle");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setSnippet(String paramString)
  {
    this.a.b(paramString);
  }
  
  public void setTitle(String paramString)
  {
    this.a.a(paramString);
  }
  
  public void setVisible(boolean paramBoolean)
  {
    this.a.b(paramBoolean);
  }
  
  public void setZIndex(float paramFloat)
  {
    this.a.b(paramFloat);
  }
  
  public void showInfoWindow()
  {
    if (this.a == null) {
      return;
    }
    this.a.i();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/Marker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */