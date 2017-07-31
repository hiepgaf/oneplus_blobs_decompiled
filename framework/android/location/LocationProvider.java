package android.location;

import com.android.internal.location.ProviderProperties;

public class LocationProvider
{
  public static final int AVAILABLE = 2;
  public static final String BAD_CHARS_REGEX = "[^a-zA-Z0-9]";
  public static final int OUT_OF_SERVICE = 0;
  public static final int TEMPORARILY_UNAVAILABLE = 1;
  private final String mName;
  private final ProviderProperties mProperties;
  
  public LocationProvider(String paramString, ProviderProperties paramProviderProperties)
  {
    if (paramString.matches("[^a-zA-Z0-9]")) {
      throw new IllegalArgumentException("provider name contains illegal character: " + paramString);
    }
    this.mName = paramString;
    this.mProperties = paramProviderProperties;
  }
  
  public static boolean propertiesMeetCriteria(String paramString, ProviderProperties paramProviderProperties, Criteria paramCriteria)
  {
    if ("passive".equals(paramString)) {
      return false;
    }
    if (paramProviderProperties == null) {
      return false;
    }
    if ((paramCriteria.getAccuracy() != 0) && (paramCriteria.getAccuracy() < paramProviderProperties.mAccuracy)) {
      return false;
    }
    if ((paramCriteria.getPowerRequirement() != 0) && (paramCriteria.getPowerRequirement() < paramProviderProperties.mPowerRequirement)) {
      return false;
    }
    if ((!paramCriteria.isAltitudeRequired()) || (paramProviderProperties.mSupportsAltitude))
    {
      if ((!paramCriteria.isSpeedRequired()) || (paramProviderProperties.mSupportsSpeed))
      {
        if ((paramCriteria.isBearingRequired()) && (!paramProviderProperties.mSupportsBearing)) {
          break label119;
        }
        if ((paramCriteria.isCostAllowed()) || (!paramProviderProperties.mHasMonetaryCost)) {
          break label121;
        }
        return false;
      }
    }
    else {
      return false;
    }
    return false;
    label119:
    return false;
    label121:
    return true;
  }
  
  public int getAccuracy()
  {
    return this.mProperties.mAccuracy;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int getPowerRequirement()
  {
    return this.mProperties.mPowerRequirement;
  }
  
  public boolean hasMonetaryCost()
  {
    return this.mProperties.mHasMonetaryCost;
  }
  
  public boolean meetsCriteria(Criteria paramCriteria)
  {
    return propertiesMeetCriteria(this.mName, this.mProperties, paramCriteria);
  }
  
  public boolean requiresCell()
  {
    return this.mProperties.mRequiresCell;
  }
  
  public boolean requiresNetwork()
  {
    return this.mProperties.mRequiresNetwork;
  }
  
  public boolean requiresSatellite()
  {
    return this.mProperties.mRequiresSatellite;
  }
  
  public boolean supportsAltitude()
  {
    return this.mProperties.mSupportsAltitude;
  }
  
  public boolean supportsBearing()
  {
    return this.mProperties.mSupportsBearing;
  }
  
  public boolean supportsSpeed()
  {
    return this.mProperties.mSupportsSpeed;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/LocationProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */