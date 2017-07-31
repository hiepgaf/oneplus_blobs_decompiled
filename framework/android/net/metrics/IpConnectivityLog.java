package android.net.metrics;

import android.net.ConnectivityMetricsEvent;
import android.net.IIpConnectivityMetrics;
import android.net.IIpConnectivityMetrics.Stub;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public class IpConnectivityLog
{
  private static final boolean DBG = false;
  public static final String SERVICE_NAME = "connmetrics";
  private static final String TAG = IpConnectivityLog.class.getSimpleName();
  private IIpConnectivityMetrics mService;
  
  public IpConnectivityLog() {}
  
  public IpConnectivityLog(IIpConnectivityMetrics paramIIpConnectivityMetrics)
  {
    this.mService = paramIIpConnectivityMetrics;
  }
  
  private boolean checkLoggerService()
  {
    if (this.mService != null) {
      return true;
    }
    IIpConnectivityMetrics localIIpConnectivityMetrics = IIpConnectivityMetrics.Stub.asInterface(ServiceManager.getService("connmetrics"));
    if (localIIpConnectivityMetrics == null) {
      return false;
    }
    this.mService = localIIpConnectivityMetrics;
    return true;
  }
  
  public void log(Parcelable paramParcelable)
  {
    log(System.currentTimeMillis(), paramParcelable);
  }
  
  public boolean log(long paramLong, Parcelable paramParcelable)
  {
    if (!checkLoggerService()) {
      return false;
    }
    try
    {
      int i = this.mService.logEvent(new ConnectivityMetricsEvent(paramLong, 0, 0, paramParcelable));
      return i >= 0;
    }
    catch (RemoteException paramParcelable)
    {
      Log.e(TAG, "Error logging event", paramParcelable);
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/IpConnectivityLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */