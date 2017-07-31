package com.oneplus.gallery2.location;

import android.location.Address;
import android.location.Location;
import com.oneplus.base.Handle;
import com.oneplus.base.component.Component;
import java.util.Locale;
import java.util.Map;

public abstract interface LocationManager
  extends Component
{
  public abstract AddressClassifier createAddressClassifier(int paramInt);
  
  public abstract Handle getAddresses(Map<?, Location> paramMap, Locale paramLocale, AddressCallback paramAddressCallback, int paramInt);
  
  public static abstract class AddressCallback
  {
    public abstract void onAddressesObtained(Handle paramHandle, Locale paramLocale, Map<?, Address> paramMap, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/location/LocationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */