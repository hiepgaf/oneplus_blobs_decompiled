package com.android.server.location;

import android.content.Context;
import android.location.Address;
import android.location.GeocoderParams;
import android.location.IGeocodeProvider;
import android.location.IGeocodeProvider.Stub;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.android.server.ServiceWatcher;
import java.util.List;

public class GeocoderProxy
{
  private static final String SERVICE_ACTION = "com.android.location.service.GeocodeProvider";
  private static final String TAG = "GeocoderProxy";
  private final Context mContext;
  private final ServiceWatcher mServiceWatcher;
  
  private GeocoderProxy(Context paramContext, int paramInt1, int paramInt2, int paramInt3, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mServiceWatcher = new ServiceWatcher(this.mContext, "GeocoderProxy", "com.android.location.service.GeocodeProvider", paramInt1, paramInt2, paramInt3, null, paramHandler);
  }
  
  private boolean bind()
  {
    return this.mServiceWatcher.start();
  }
  
  public static GeocoderProxy createAndBind(Context paramContext, int paramInt1, int paramInt2, int paramInt3, Handler paramHandler)
  {
    paramContext = new GeocoderProxy(paramContext, paramInt1, paramInt2, paramInt3, paramHandler);
    if (paramContext.bind()) {
      return paramContext;
    }
    return null;
  }
  
  private IGeocodeProvider getService()
  {
    return IGeocodeProvider.Stub.asInterface(this.mServiceWatcher.getBinder());
  }
  
  public String getConnectedPackageName()
  {
    return this.mServiceWatcher.getBestPackageName();
  }
  
  public String getFromLocation(double paramDouble1, double paramDouble2, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
  {
    IGeocodeProvider localIGeocodeProvider = getService();
    if (localIGeocodeProvider != null) {
      try
      {
        paramGeocoderParams = localIGeocodeProvider.getFromLocation(paramDouble1, paramDouble2, paramInt, paramGeocoderParams, paramList);
        return paramGeocoderParams;
      }
      catch (RemoteException paramGeocoderParams)
      {
        Log.w("GeocoderProxy", paramGeocoderParams);
      }
    }
    return "Service not Available";
  }
  
  public String getFromLocationName(String paramString, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt, GeocoderParams paramGeocoderParams, List<Address> paramList)
  {
    IGeocodeProvider localIGeocodeProvider = getService();
    if (localIGeocodeProvider != null) {
      try
      {
        paramString = localIGeocodeProvider.getFromLocationName(paramString, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramInt, paramGeocoderParams, paramList);
        return paramString;
      }
      catch (RemoteException paramString)
      {
        Log.w("GeocoderProxy", paramString);
      }
    }
    return "Service not Available";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/GeocoderProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */