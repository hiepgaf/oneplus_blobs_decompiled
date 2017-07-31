package android.net.util;

import android.net.INetd;
import android.net.INetd.Stub;
import android.os.ServiceManager;
import android.util.Log;

public class NetdService
{
  private static final String NETD_SERVICE_NAME = "netd";
  private static final String TAG = NetdService.class.getSimpleName();
  
  public static INetd getInstance()
  {
    INetd localINetd = INetd.Stub.asInterface(ServiceManager.getService("netd"));
    if (localINetd == null) {
      Log.w(TAG, "WARNING: returning null INetd instance.");
    }
    return localINetd;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/util/NetdService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */