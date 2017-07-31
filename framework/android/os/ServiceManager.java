package android.os;

import android.util.Log;
import com.android.internal.os.BinderInternal;
import java.util.HashMap;
import java.util.Map;

public final class ServiceManager
{
  private static final String TAG = "ServiceManager";
  private static HashMap<String, IBinder> sCache = new HashMap();
  private static IServiceManager sServiceManager;
  
  public static void addService(String paramString, IBinder paramIBinder)
  {
    try
    {
      getIServiceManager().addService(paramString, paramIBinder, false);
      return;
    }
    catch (RemoteException paramString)
    {
      Log.e("ServiceManager", "error in addService", paramString);
    }
  }
  
  public static void addService(String paramString, IBinder paramIBinder, boolean paramBoolean)
  {
    try
    {
      getIServiceManager().addService(paramString, paramIBinder, paramBoolean);
      return;
    }
    catch (RemoteException paramString)
    {
      Log.e("ServiceManager", "error in addService", paramString);
    }
  }
  
  public static IBinder checkService(String paramString)
  {
    try
    {
      IBinder localIBinder = (IBinder)sCache.get(paramString);
      if (localIBinder != null) {
        return localIBinder;
      }
      paramString = getIServiceManager().checkService(paramString);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      Log.e("ServiceManager", "error in checkService", paramString);
    }
    return null;
  }
  
  private static IServiceManager getIServiceManager()
  {
    if (sServiceManager != null) {
      return sServiceManager;
    }
    sServiceManager = ServiceManagerNative.asInterface(BinderInternal.getContextObject());
    return sServiceManager;
  }
  
  public static IBinder getService(String paramString)
  {
    try
    {
      IBinder localIBinder = (IBinder)sCache.get(paramString);
      if (localIBinder != null) {
        return localIBinder;
      }
      paramString = getIServiceManager().getService(paramString);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      Log.e("ServiceManager", "error in getService", paramString);
    }
    return null;
  }
  
  public static void initServiceCache(Map<String, IBinder> paramMap)
  {
    if (sCache.size() != 0) {
      throw new IllegalStateException("setServiceCache may only be called once");
    }
    sCache.putAll(paramMap);
  }
  
  public static String[] listServices()
  {
    try
    {
      String[] arrayOfString = getIServiceManager().listServices();
      return arrayOfString;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("ServiceManager", "error in listServices", localRemoteException);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/ServiceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */