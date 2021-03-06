package com.amap.api.maps2d;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.amap.api.mapcore2d.aq;
import com.amap.api.mapcore2d.cj;
import com.amap.api.mapcore2d.z;
import com.amap.api.maps2d.model.RuntimeRemoteException;

public class MapFragment
  extends Fragment
{
  private AMap a;
  private z b;
  
  public static MapFragment newInstance()
  {
    return newInstance(new AMapOptions());
  }
  
  public static MapFragment newInstance(AMapOptions paramAMapOptions)
  {
    MapFragment localMapFragment = new MapFragment();
    Bundle localBundle = new Bundle();
    try
    {
      Parcel localParcel = Parcel.obtain();
      paramAMapOptions.writeToParcel(localParcel, 0);
      localBundle.putByteArray("MapOptions", localParcel.marshall());
      localMapFragment.setArguments(localBundle);
      return localMapFragment;
    }
    catch (Throwable paramAMapOptions)
    {
      for (;;)
      {
        paramAMapOptions.printStackTrace();
      }
    }
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
        if (this.a == null) {
          break label54;
        }
        return this.a;
      }
      catch (RemoteException localRemoteException)
      {
        cj.a(localRemoteException, "MapFragment", "getMap");
        throw new RuntimeRemoteException(localRemoteException);
      }
      return null;
      label52:
      return null;
      label54:
      this.a = new AMap(localRemoteException);
    }
  }
  
  protected z getMapFragmentDelegate()
  {
    if (this.b != null) {
      if (getActivity() != null) {
        break label33;
      }
    }
    for (;;)
    {
      return this.b;
      this.b = new aq();
      break;
      label33:
      this.b.a(getActivity());
    }
  }
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    try
    {
      getMapFragmentDelegate().a(paramBundle);
      return;
    }
    catch (RemoteException paramBundle)
    {
      cj.a(paramBundle, "MapFragment", "onCreate");
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    if (paramBundle != null) {}
    for (;;)
    {
      try
      {
        return getMapFragmentDelegate().a(paramLayoutInflater, paramViewGroup, paramBundle);
      }
      catch (RemoteException paramLayoutInflater)
      {
        cj.a(paramLayoutInflater, "MapFragment", "onCreateView");
      }
      paramBundle = getArguments();
    }
    return null;
  }
  
  public void onDestroy()
  {
    try
    {
      getMapFragmentDelegate().e();
      super.onDestroy();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        cj.a(localRemoteException, "MapFragment", "onDestroy");
      }
    }
  }
  
  public void onDestroyView()
  {
    try
    {
      getMapFragmentDelegate().d();
      super.onDestroyView();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        cj.a(localRemoteException, "MapFragment", "onDestroyView");
      }
    }
  }
  
  public void onInflate(Activity paramActivity, AttributeSet paramAttributeSet, Bundle paramBundle)
  {
    super.onInflate(paramActivity, paramAttributeSet, paramBundle);
    try
    {
      getMapFragmentDelegate().a(paramActivity, new AMapOptions(), paramBundle);
      return;
    }
    catch (RemoteException paramActivity)
    {
      cj.a(paramActivity, "MapFragment", "onInflate");
    }
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    try
    {
      getMapFragmentDelegate().f();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "MapFragment", "onLowMemory");
    }
  }
  
  public void onPause()
  {
    super.onPause();
    try
    {
      getMapFragmentDelegate().c();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "MapFragment", "onPause");
    }
  }
  
  public void onResume()
  {
    super.onResume();
    try
    {
      getMapFragmentDelegate().b();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "MapFragment", "onResume");
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    try
    {
      getMapFragmentDelegate().b(paramBundle);
      super.onSaveInstanceState(paramBundle);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        cj.a(localRemoteException, "MapFragment", "onSaveInstanceState");
      }
    }
  }
  
  public void setArguments(Bundle paramBundle)
  {
    super.setArguments(paramBundle);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/MapFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */