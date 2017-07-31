package android.os;

import android.app.IAlarmManager;
import android.app.IAlarmManager.Stub;
import android.util.Slog;

public final class SystemClock
{
  private static final String TAG = "SystemClock";
  
  public static native long currentThreadTimeMicro();
  
  public static native long currentThreadTimeMillis();
  
  public static native long currentTimeMicro();
  
  public static native long elapsedRealtime();
  
  public static native long elapsedRealtimeNanos();
  
  public static boolean setCurrentTimeMillis(long paramLong)
  {
    IAlarmManager localIAlarmManager = IAlarmManager.Stub.asInterface(ServiceManager.getService("alarm"));
    if (localIAlarmManager == null) {
      return false;
    }
    try
    {
      boolean bool = localIAlarmManager.setTime(paramLong);
      return bool;
    }
    catch (SecurityException localSecurityException)
    {
      Slog.e("SystemClock", "Unable to set RTC", localSecurityException);
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("SystemClock", "Unable to set RTC", localRemoteException);
    }
    return false;
  }
  
  public static void sleep(long paramLong)
  {
    long l3 = uptimeMillis();
    long l1 = paramLong;
    int i = 0;
    do
    {
      try
      {
        Thread.sleep(l1);
        j = i;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          long l2;
          int j = 1;
        }
      }
      l2 = l3 + paramLong - uptimeMillis();
      l1 = l2;
      i = j;
    } while (l2 > 0L);
    if (j != 0) {
      Thread.currentThread().interrupt();
    }
  }
  
  public static native long uptimeMillis();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/SystemClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */