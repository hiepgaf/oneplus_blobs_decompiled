package com.amap.api.mapcore2d;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.AMapOptionsCreator;
import com.amap.api.maps2d.model.CameraPosition;

public class aq
  implements z
{
  public static volatile Context a;
  private w b;
  private AMapOptions c;
  
  public View a(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    throws RemoteException
  {
    if (this.b != null) {}
    for (;;)
    {
      try
      {
        if (this.c == null) {
          continue;
        }
        b(this.c);
        ch.a("MapFragmentDelegateImp", "onCreateView", 113);
      }
      catch (Throwable paramLayoutInflater)
      {
        paramLayoutInflater.printStackTrace();
        continue;
      }
      return this.b.e();
      if (a != null)
      {
        if (a != null)
        {
          g();
          this.b = new b(a);
        }
      }
      else
      {
        if (paramLayoutInflater == null) {
          continue;
        }
        a = paramLayoutInflater.getContext().getApplicationContext();
        continue;
      }
      throw new NullPointerException("Context 为 null 请在地图调用之前 使用 MapsInitializer.initialize(Context paramContext) 来设置Context");
      if (paramBundle != null)
      {
        paramLayoutInflater = paramBundle.getByteArray("MapOptions");
        if (paramLayoutInflater != null)
        {
          paramViewGroup = Parcel.obtain();
          paramViewGroup.unmarshall(paramLayoutInflater, 0, paramLayoutInflater.length);
          paramViewGroup.setDataPosition(0);
          this.c = AMapOptions.CREATOR.createFromParcel(paramViewGroup);
        }
      }
    }
  }
  
  public w a()
    throws RemoteException
  {
    if (this.b != null) {}
    for (;;)
    {
      return this.b;
      if (a == null) {
        break;
      }
      g();
      this.b = new b(a);
    }
    throw new NullPointerException("Context 为 null 请在地图调用之前 使用 MapsInitializer.initialize(Context paramContext) 来设置Context");
  }
  
  public void a(Activity paramActivity, AMapOptions paramAMapOptions, Bundle paramBundle)
    throws RemoteException
  {
    a = paramActivity.getApplicationContext();
    this.c = paramAMapOptions;
  }
  
  public void a(Context paramContext)
  {
    if (paramContext == null) {
      return;
    }
    a = paramContext.getApplicationContext();
  }
  
  public void a(Bundle paramBundle)
    throws RemoteException
  {
    ch.a("MapFragmentDelegateImp", "onCreate", 113);
  }
  
  public void a(AMapOptions paramAMapOptions)
  {
    this.c = paramAMapOptions;
  }
  
  public void b()
    throws RemoteException
  {
    if (this.b == null) {
      return;
    }
    this.b.y();
  }
  
  public void b(Bundle paramBundle)
    throws RemoteException
  {
    if (this.b == null) {
      return;
    }
    if (this.c != null) {}
    for (;;)
    {
      this.c = this.c.camera(a().g());
      if (paramBundle == null) {
        break;
      }
      try
      {
        Parcel localParcel = Parcel.obtain();
        this.c.writeToParcel(localParcel, 0);
        paramBundle.putByteArray("MapOptions", localParcel.marshall());
        return;
      }
      catch (Throwable paramBundle)
      {
        paramBundle.printStackTrace();
        return;
      }
      this.c = new AMapOptions();
    }
  }
  
  void b(AMapOptions paramAMapOptions)
    throws RemoteException
  {
    if (paramAMapOptions == null) {}
    while (this.b == null) {
      return;
    }
    Object localObject = paramAMapOptions.getCamera();
    if (localObject == null) {}
    for (;;)
    {
      localObject = this.b.q();
      ((ak)localObject).e(paramAMapOptions.getScrollGesturesEnabled().booleanValue());
      ((ak)localObject).b(paramAMapOptions.getZoomControlsEnabled().booleanValue());
      ((ak)localObject).f(paramAMapOptions.getZoomGesturesEnabled().booleanValue());
      ((ak)localObject).c(paramAMapOptions.getCompassEnabled().booleanValue());
      ((ak)localObject).a(paramAMapOptions.getScaleControlsEnabled().booleanValue());
      ((ak)localObject).a(paramAMapOptions.getLogoPosition());
      this.b.a(paramAMapOptions.getMapType());
      this.b.a(paramAMapOptions.getZOrderOnTop().booleanValue());
      return;
      this.b.a(l.a(((CameraPosition)localObject).target, ((CameraPosition)localObject).zoom, ((CameraPosition)localObject).bearing, ((CameraPosition)localObject).tilt));
    }
  }
  
  public void c()
    throws RemoteException
  {
    if (this.b == null) {
      return;
    }
    this.b.z();
  }
  
  public void d()
    throws RemoteException
  {}
  
  public void e()
    throws RemoteException
  {
    if (a() == null) {
      return;
    }
    a().k();
    a().v();
  }
  
  public void f()
    throws RemoteException
  {
    Log.d("onLowMemory", "onLowMemory run");
  }
  
  void g()
  {
    int i = a.getResources().getDisplayMetrics().densityDpi;
    p.l = i;
    if (i <= 320) {
      p.j = 256;
    }
    while (i > 120)
    {
      if (i <= 160) {
        break label103;
      }
      if (i <= 240) {
        break label110;
      }
      if (i <= 320) {
        break label117;
      }
      if (i <= 480) {
        break label122;
      }
      p.a = 1.8F;
      return;
      if (i > 480) {
        p.j = 512;
      } else {
        p.j = 384;
      }
    }
    p.a = 0.5F;
    return;
    label103:
    p.a = 0.6F;
    return;
    label110:
    p.a = 0.87F;
    return;
    label117:
    p.a = 1.0F;
    return;
    label122:
    p.a = 1.5F;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/aq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */