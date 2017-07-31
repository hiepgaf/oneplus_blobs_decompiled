package com.amap.api.maps2d;

import android.os.RemoteException;
import com.amap.api.mapcore2d.ak;
import com.amap.api.mapcore2d.cj;
import com.amap.api.maps2d.model.RuntimeRemoteException;

public final class UiSettings
{
  private final ak a;
  
  UiSettings(ak paramak)
  {
    this.a = paramak;
  }
  
  public int getLogoPosition()
  {
    try
    {
      int i = this.a.g();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "getLogoPosition");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public int getZoomPosition()
  {
    try
    {
      int i = this.a.h();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "getZoomPosition");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public boolean isCompassEnabled()
  {
    try
    {
      boolean bool = this.a.c();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "isCompassEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public boolean isMyLocationButtonEnabled()
  {
    try
    {
      boolean bool = this.a.d();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "isMyLocationButtonEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public boolean isScaleControlsEnabled()
  {
    try
    {
      boolean bool = this.a.a();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "isScaleControlsEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public boolean isScrollGesturesEnabled()
  {
    try
    {
      boolean bool = this.a.e();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "isScrollGestureEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public boolean isZoomControlsEnabled()
  {
    try
    {
      boolean bool = this.a.b();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "isZoomControlsEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public boolean isZoomGesturesEnabled()
  {
    try
    {
      boolean bool = this.a.f();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "isZoomGesturesEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setAllGesturesEnabled(boolean paramBoolean)
  {
    try
    {
      this.a.g(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "setAllGesturesEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setCompassEnabled(boolean paramBoolean)
  {
    try
    {
      this.a.c(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "setCompassEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setLogoPosition(int paramInt)
  {
    try
    {
      this.a.a(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "setLogoPosition");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setMyLocationButtonEnabled(boolean paramBoolean)
  {
    try
    {
      this.a.d(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "setMyLocationButtonEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setScaleControlsEnabled(boolean paramBoolean)
  {
    try
    {
      this.a.a(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "setScaleControlsEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setScrollGesturesEnabled(boolean paramBoolean)
  {
    try
    {
      this.a.e(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "setScrollGesturesEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setZoomControlsEnabled(boolean paramBoolean)
  {
    try
    {
      this.a.b(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "setZoomControlsEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setZoomGesturesEnabled(boolean paramBoolean)
  {
    try
    {
      this.a.f(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "setZoomGesturesEnabled");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setZoomPosition(int paramInt)
  {
    try
    {
      this.a.b(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "UiSettings", "setZoomPosition");
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/UiSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */