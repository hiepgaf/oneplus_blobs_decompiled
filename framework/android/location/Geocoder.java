package android.location;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Geocoder
{
  private static final String TAG = "Geocoder";
  private GeocoderParams mParams;
  private ILocationManager mService;
  
  public Geocoder(Context paramContext)
  {
    this(paramContext, Locale.getDefault());
  }
  
  public Geocoder(Context paramContext, Locale paramLocale)
  {
    if (paramLocale == null) {
      throw new NullPointerException("locale == null");
    }
    this.mParams = new GeocoderParams(paramContext, paramLocale);
    this.mService = ILocationManager.Stub.asInterface(ServiceManager.getService("location"));
  }
  
  public static boolean isPresent()
  {
    ILocationManager localILocationManager = ILocationManager.Stub.asInterface(ServiceManager.getService("location"));
    try
    {
      boolean bool = localILocationManager.geocoderIsPresent();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("Geocoder", "isPresent: got RemoteException", localRemoteException);
    }
    return false;
  }
  
  public List<Address> getFromLocation(double paramDouble1, double paramDouble2, int paramInt)
    throws IOException
  {
    if ((paramDouble1 < -90.0D) || (paramDouble1 > 90.0D)) {
      throw new IllegalArgumentException("latitude == " + paramDouble1);
    }
    if ((paramDouble2 < -180.0D) || (paramDouble2 > 180.0D)) {
      throw new IllegalArgumentException("longitude == " + paramDouble2);
    }
    try
    {
      ArrayList localArrayList = new ArrayList();
      String str = this.mService.getFromLocation(paramDouble1, paramDouble2, paramInt, this.mParams, localArrayList);
      if (str != null) {
        throw new IOException(str);
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("Geocoder", "getFromLocation: got RemoteException", localRemoteException);
      return null;
    }
    return localRemoteException;
  }
  
  public List<Address> getFromLocationName(String paramString, int paramInt)
    throws IOException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("locationName == null");
    }
    ArrayList localArrayList;
    try
    {
      localArrayList = new ArrayList();
      paramString = this.mService.getFromLocationName(paramString, 0.0D, 0.0D, 0.0D, 0.0D, paramInt, this.mParams, localArrayList);
      if (paramString != null) {
        throw new IOException(paramString);
      }
    }
    catch (RemoteException paramString)
    {
      Log.e("Geocoder", "getFromLocationName: got RemoteException", paramString);
      return null;
    }
    return localArrayList;
  }
  
  public List<Address> getFromLocationName(String paramString, int paramInt, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    throws IOException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("locationName == null");
    }
    if ((paramDouble1 < -90.0D) || (paramDouble1 > 90.0D)) {
      throw new IllegalArgumentException("lowerLeftLatitude == " + paramDouble1);
    }
    if ((paramDouble2 < -180.0D) || (paramDouble2 > 180.0D)) {
      throw new IllegalArgumentException("lowerLeftLongitude == " + paramDouble2);
    }
    if ((paramDouble3 < -90.0D) || (paramDouble3 > 90.0D)) {
      throw new IllegalArgumentException("upperRightLatitude == " + paramDouble3);
    }
    if ((paramDouble4 < -180.0D) || (paramDouble4 > 180.0D)) {
      throw new IllegalArgumentException("upperRightLongitude == " + paramDouble4);
    }
    ArrayList localArrayList;
    try
    {
      localArrayList = new ArrayList();
      paramString = this.mService.getFromLocationName(paramString, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramInt, this.mParams, localArrayList);
      if (paramString != null) {
        throw new IOException(paramString);
      }
    }
    catch (RemoteException paramString)
    {
      Log.e("Geocoder", "getFromLocationName: got RemoteException", paramString);
      return null;
    }
    return localArrayList;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/Geocoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */