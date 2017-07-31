package com.oneplus.gallery2.location;

import com.oneplus.base.BaseAppComponentBuilder;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentCreationPriority;

public final class LocationManagerBuilder
  extends BaseAppComponentBuilder
{
  public LocationManagerBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, LocationManager.class);
  }
  
  protected Component create(BaseApplication paramBaseApplication)
  {
    return new LocationManagerImpl(paramBaseApplication);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/location/LocationManagerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */