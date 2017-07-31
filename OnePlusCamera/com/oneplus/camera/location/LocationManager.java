package com.oneplus.camera.location;

import android.location.Location;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface LocationManager
  extends Component
{
  public static final PropertyKey<Boolean> PROP_IS_LOCATION_LISTENER_STARTED = new PropertyKey("IsLocationListenerStarted", Boolean.class, LocationManager.class, Boolean.valueOf(false));
  public static final PropertyKey<Location> PROP_LOCATION = new PropertyKey("Location", Location.class, LocationManager.class, 1, null);
  public static final String SETTINGS_KEY_SAVE_LOCATION = "Location.Save";
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/location/LocationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */