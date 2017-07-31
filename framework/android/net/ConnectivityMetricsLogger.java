package android.net;

import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public class ConnectivityMetricsLogger
{
  public static final int COMPONENT_TAG_BLUETOOTH = 1;
  public static final int COMPONENT_TAG_CONNECTIVITY = 0;
  public static final int COMPONENT_TAG_TELECOM = 3;
  public static final int COMPONENT_TAG_TELEPHONY = 4;
  public static final int COMPONENT_TAG_WIFI = 2;
  public static final String CONNECTIVITY_METRICS_LOGGER_SERVICE = "connectivity_metrics_logger";
  public static final String DATA_KEY_EVENTS_COUNT = "count";
  private static final boolean DBG = true;
  public static final int NUMBER_OF_COMPONENTS = 5;
  private static String TAG = "ConnectivityMetricsLogger";
  public static final int TAG_SKIPPED_EVENTS = -1;
  private int mNumSkippedEvents;
  protected IConnectivityMetricsLogger mService;
  protected volatile long mServiceUnblockedTimestampMillis;
  
  public ConnectivityMetricsLogger()
  {
    this(IConnectivityMetricsLogger.Stub.asInterface(ServiceManager.getService("connectivity_metrics_logger")));
  }
  
  public ConnectivityMetricsLogger(IConnectivityMetricsLogger paramIConnectivityMetricsLogger)
  {
    this.mService = paramIConnectivityMetricsLogger;
  }
  
  protected boolean checkLoggerService()
  {
    if (this.mService != null) {
      return true;
    }
    this.mService = IConnectivityMetricsLogger.Stub.asInterface(ServiceManager.getService("connectivity_metrics_logger"));
    return this.mService != null;
  }
  
  public ConnectivityMetricsEvent[] getEvents(ConnectivityMetricsEvent.Reference paramReference)
  {
    try
    {
      paramReference = this.mService.getEvents(paramReference);
      return paramReference;
    }
    catch (RemoteException paramReference)
    {
      Log.e(TAG, "IConnectivityMetricsLogger.getEvents", paramReference);
    }
    return null;
  }
  
  public void logEvent(long paramLong, int paramInt1, int paramInt2, Parcelable paramParcelable)
  {
    if (this.mService == null)
    {
      Log.d(TAG, "logEvent(" + paramInt1 + "," + paramInt2 + ") Service not ready");
      return;
    }
    if ((this.mServiceUnblockedTimestampMillis > 0L) && (System.currentTimeMillis() < this.mServiceUnblockedTimestampMillis))
    {
      this.mNumSkippedEvents += 1;
      return;
    }
    Object localObject = null;
    if (this.mNumSkippedEvents > 0)
    {
      localObject = new Bundle();
      ((Bundle)localObject).putInt("count", this.mNumSkippedEvents);
      localObject = new ConnectivityMetricsEvent(this.mServiceUnblockedTimestampMillis, paramInt1, -1, (Parcelable)localObject);
      this.mServiceUnblockedTimestampMillis = 0L;
    }
    paramParcelable = new ConnectivityMetricsEvent(paramLong, paramInt1, paramInt2, paramParcelable);
    if (localObject == null) {}
    try
    {
      for (paramLong = this.mService.logEvent(paramParcelable); paramLong == 0L; paramLong = this.mService.logEvents(new ConnectivityMetricsEvent[] { localObject, paramParcelable }))
      {
        this.mNumSkippedEvents = 0;
        return;
      }
      this.mNumSkippedEvents += 1;
      if (paramLong > 0L)
      {
        this.mServiceUnblockedTimestampMillis = paramLong;
        return;
      }
    }
    catch (RemoteException paramParcelable)
    {
      Log.e(TAG, "Error logging event", paramParcelable);
    }
  }
  
  public boolean register(PendingIntent paramPendingIntent)
  {
    try
    {
      boolean bool = this.mService.register(paramPendingIntent);
      return bool;
    }
    catch (RemoteException paramPendingIntent)
    {
      Log.e(TAG, "IConnectivityMetricsLogger.register", paramPendingIntent);
    }
    return false;
  }
  
  public boolean unregister(PendingIntent paramPendingIntent)
  {
    try
    {
      this.mService.unregister(paramPendingIntent);
      return true;
    }
    catch (RemoteException paramPendingIntent)
    {
      Log.e(TAG, "IConnectivityMetricsLogger.unregister", paramPendingIntent);
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ConnectivityMetricsLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */