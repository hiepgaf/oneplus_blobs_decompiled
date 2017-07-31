package android.app;

import android.content.pm.IPackageManager;

public class AppGlobals
{
  public static Application getInitialApplication()
  {
    return ActivityThread.currentApplication();
  }
  
  public static String getInitialPackage()
  {
    return ActivityThread.currentPackageName();
  }
  
  public static int getIntCoreSetting(String paramString, int paramInt)
  {
    ActivityThread localActivityThread = ActivityThread.currentActivityThread();
    if (localActivityThread != null) {
      return localActivityThread.getIntCoreSetting(paramString, paramInt);
    }
    return paramInt;
  }
  
  public static IPackageManager getPackageManager()
  {
    return ActivityThread.getPackageManager();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/AppGlobals.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */