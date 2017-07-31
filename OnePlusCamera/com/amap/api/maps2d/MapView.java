package com.amap.api.maps2d;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import com.amap.api.mapcore2d.aq;
import com.amap.api.mapcore2d.cj;
import com.amap.api.mapcore2d.z;
import com.amap.api.maps2d.model.RuntimeRemoteException;

public class MapView
  extends FrameLayout
{
  private z a;
  private AMap b;
  
  public MapView(Context paramContext)
  {
    super(paramContext);
    getMapFragmentDelegate().a(paramContext);
  }
  
  public MapView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    getMapFragmentDelegate().a(paramContext);
  }
  
  public MapView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    getMapFragmentDelegate().a(paramContext);
  }
  
  public MapView(Context paramContext, AMapOptions paramAMapOptions)
  {
    super(paramContext);
    getMapFragmentDelegate().a(paramContext);
    getMapFragmentDelegate().a(paramAMapOptions);
  }
  
  public AMap getMap()
  {
    Object localObject = getMapFragmentDelegate();
    if (localObject != null) {}
    for (;;)
    {
      try
      {
        localObject = ((z)localObject).a();
        if (localObject == null) {
          break label52;
        }
        if (this.b == null) {
          break label54;
        }
        return this.b;
      }
      catch (RemoteException localRemoteException)
      {
        cj.a(localRemoteException, "MapView", "getMap");
        throw new RuntimeRemoteException(localRemoteException);
      }
      return null;
      label52:
      return null;
      label54:
      this.b = new AMap(localRemoteException);
    }
  }
  
  protected z getMapFragmentDelegate()
  {
    if (this.a != null) {}
    for (;;)
    {
      return this.a;
      this.a = new aq();
    }
  }
  
  public final void onCreate(Bundle paramBundle)
  {
    try
    {
      addView(getMapFragmentDelegate().a(null, null, paramBundle), new ViewGroup.LayoutParams(-1, -1));
      return;
    }
    catch (RemoteException paramBundle)
    {
      cj.a(paramBundle, "MapView", "onCreate");
      return;
    }
    catch (Throwable paramBundle)
    {
      cj.a(paramBundle, "MapView", "onCreate");
    }
  }
  
  public final void onDestroy()
  {
    try
    {
      getMapFragmentDelegate().e();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "MapView", "onDestroy");
    }
  }
  
  public final void onLowMemory()
  {
    try
    {
      getMapFragmentDelegate().f();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "MapView", "onLowMemory");
    }
  }
  
  public final void onPause()
  {
    try
    {
      getMapFragmentDelegate().c();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "MapView", "onPause");
    }
  }
  
  public final void onResume()
  {
    try
    {
      getMapFragmentDelegate().b();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "MapView", "onResume");
    }
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
  {
    try
    {
      getMapFragmentDelegate().b(paramBundle);
      return;
    }
    catch (RemoteException paramBundle)
    {
      cj.a(paramBundle, "MapView", "onSaveInstanceState");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/MapView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */