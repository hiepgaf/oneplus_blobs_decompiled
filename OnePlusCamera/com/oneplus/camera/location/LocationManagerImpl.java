package com.oneplus.camera.location;

import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.ComponentOwner;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.ProxyComponent;
import java.util.List;

final class LocationManagerImpl
  extends ProxyComponent<LocationManager>
  implements LocationManager
{
  LocationManagerImpl(CameraThread paramCameraThread)
  {
    super("Location Manager (Camera Thread)", paramCameraThread, (ComponentOwner)paramCameraThread.getContext(), LocationManager.class);
  }
  
  protected void onBindingToTargetProperties(List<PropertyKey<?>> paramList)
  {
    super.onBindingToTargetProperties(paramList);
    paramList.add(PROP_IS_LOCATION_LISTENER_STARTED);
    paramList.add(PROP_LOCATION);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/location/LocationManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */