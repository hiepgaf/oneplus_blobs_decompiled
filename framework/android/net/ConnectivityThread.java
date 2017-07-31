package android.net;

import android.os.HandlerThread;
import android.os.Looper;

public final class ConnectivityThread
  extends HandlerThread
{
  private static ConnectivityThread sInstance;
  
  private ConnectivityThread()
  {
    super("ConnectivityThread");
  }
  
  public static ConnectivityThread get()
  {
    return getInstance();
  }
  
  private static ConnectivityThread getInstance()
  {
    try
    {
      if (sInstance == null)
      {
        sInstance = new ConnectivityThread();
        sInstance.start();
      }
      ConnectivityThread localConnectivityThread = sInstance;
      return localConnectivityThread;
    }
    finally {}
  }
  
  public static Looper getInstanceLooper()
  {
    return getInstance().getLooper();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ConnectivityThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */