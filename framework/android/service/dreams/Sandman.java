package android.service.dreams;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.util.Slog;

public final class Sandman
{
  private static final ComponentName SOMNAMBULATOR_COMPONENT = new ComponentName("com.android.systemui", "com.android.systemui.Somnambulator");
  private static final String TAG = "Sandman";
  
  private static boolean isScreenSaverActivatedOnDock(Context paramContext)
  {
    boolean bool = false;
    if (paramContext.getResources().getBoolean(17956976)) {}
    for (int i = 1;; i = 0)
    {
      if (Settings.Secure.getIntForUser(paramContext.getContentResolver(), "screensaver_activate_on_dock", i, -2) != 0) {
        bool = true;
      }
      return bool;
    }
  }
  
  private static boolean isScreenSaverEnabled(Context paramContext)
  {
    boolean bool = false;
    if (paramContext.getResources().getBoolean(17956975)) {}
    for (int i = 1;; i = 0)
    {
      if (Settings.Secure.getIntForUser(paramContext.getContentResolver(), "screensaver_enabled", i, -2) != 0) {
        bool = true;
      }
      return bool;
    }
  }
  
  public static boolean shouldStartDockApp(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.resolveActivity(paramContext.getPackageManager());
    return (paramContext != null) && (!paramContext.equals(SOMNAMBULATOR_COMPONENT));
  }
  
  private static void startDream(Context paramContext, boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        IDreamManager localIDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
        if (localIDreamManager == null) {
          break;
        }
        if (localIDreamManager.isDreaming()) {
          return;
        }
        if (paramBoolean)
        {
          Slog.i("Sandman", "Activating dream while docked.");
          ((PowerManager)paramContext.getSystemService("power")).wakeUp(SystemClock.uptimeMillis(), "android.service.dreams:DREAM");
          localIDreamManager.dream();
          return;
        }
      }
      catch (RemoteException paramContext)
      {
        Slog.e("Sandman", "Could not start dream when docked.", paramContext);
        return;
      }
      Slog.i("Sandman", "Activating dream by user request.");
    }
  }
  
  public static void startDreamByUserRequest(Context paramContext)
  {
    startDream(paramContext, false);
  }
  
  public static void startDreamWhenDockedIfAppropriate(Context paramContext)
  {
    if ((isScreenSaverEnabled(paramContext)) && (isScreenSaverActivatedOnDock(paramContext)))
    {
      startDream(paramContext, true);
      return;
    }
    Slog.i("Sandman", "Dreams currently disabled for docks.");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/dreams/Sandman.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */