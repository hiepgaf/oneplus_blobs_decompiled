package android.app;

import android.os.RemoteException;
import android.os.ServiceManager;

public class UiModeManager
{
  public static String ACTION_ENTER_CAR_MODE = "android.app.action.ENTER_CAR_MODE";
  public static String ACTION_ENTER_DESK_MODE = "android.app.action.ENTER_DESK_MODE";
  public static String ACTION_EXIT_CAR_MODE = "android.app.action.EXIT_CAR_MODE";
  public static String ACTION_EXIT_DESK_MODE = "android.app.action.EXIT_DESK_MODE";
  public static final int DISABLE_CAR_MODE_GO_HOME = 1;
  public static final int ENABLE_CAR_MODE_ALLOW_SLEEP = 2;
  public static final int ENABLE_CAR_MODE_GO_CAR_HOME = 1;
  public static final int MODE_NIGHT_AUTO = 0;
  public static final int MODE_NIGHT_NO = 1;
  public static final int MODE_NIGHT_YES = 2;
  private static final String TAG = "UiModeManager";
  private IUiModeManager mService = IUiModeManager.Stub.asInterface(ServiceManager.getService("uimode"));
  
  public void disableCarMode(int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.disableCarMode(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void enableCarMode(int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.enableCarMode(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getCurrentModeType()
  {
    if (this.mService != null) {
      try
      {
        int i = this.mService.getCurrentModeType();
        return i;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return 1;
  }
  
  public int getNightMode()
  {
    if (this.mService != null) {
      try
      {
        int i = this.mService.getNightMode();
        return i;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return -1;
  }
  
  public boolean isNightModeLocked()
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isNightModeLocked();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return true;
  }
  
  public boolean isUiModeLocked()
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isUiModeLocked();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return true;
  }
  
  public void setNightMode(int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setNightMode(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/UiModeManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */