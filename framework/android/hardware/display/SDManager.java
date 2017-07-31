package android.hardware.display;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;

public class SDManager
{
  public static final String SMART_DISPLAY_SERVICE = "smartdisplay";
  private static final String TAG = "SDManager";
  private static ISDService sService;
  
  public SDManager(Context paramContext)
  {
    init();
  }
  
  private static ISDService init()
  {
    if (sService != null) {
      return sService;
    }
    sService = ISDService.Stub.asInterface(ServiceManager.getService("smartdisplay"));
    if (sService == null) {
      Slog.e("SDManager", "smartdisplay service is null!");
    }
    return sService;
  }
  
  public void SetUsrColorBalanceConfig(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    try
    {
      if (sService != null) {
        sService.SetUsrColorBalanceConfig(paramDouble1, paramDouble2, paramDouble3);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("SDManager", "smartdisplay service is unavailable");
    }
  }
  
  public void SetUsrSharpness(int paramInt)
  {
    try
    {
      if (sService != null) {
        sService.SetUsrSharpness(paramInt);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("SDManager", "smartdisplay service is unavailable");
    }
  }
  
  public void enableColorBalance(int paramInt)
  {
    try
    {
      if (sService != null) {
        sService.enableColorBalance(paramInt);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("SDManager", "smartdisplay service is unavailable");
    }
  }
  
  public void enableMode(int paramInt)
  {
    try
    {
      if (sService != null) {
        sService.enableMode(paramInt);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("SDManager", "smartdisplay service is unavailable");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/SDManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */