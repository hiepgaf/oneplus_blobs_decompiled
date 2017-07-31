package android.security;

import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.service.gatekeeper.IGateKeeperService;
import android.service.gatekeeper.IGateKeeperService.Stub;

public abstract class GateKeeper
{
  public static long getSecureUserId()
    throws IllegalStateException
  {
    try
    {
      long l = getService().getSecureUserId(UserHandle.myUserId());
      return l;
    }
    catch (RemoteException localRemoteException)
    {
      throw new IllegalStateException("Failed to obtain secure user ID from gatekeeper", localRemoteException);
    }
  }
  
  public static IGateKeeperService getService()
  {
    IGateKeeperService localIGateKeeperService = IGateKeeperService.Stub.asInterface(ServiceManager.getService("android.service.gatekeeper.IGateKeeperService"));
    if (localIGateKeeperService == null) {
      throw new IllegalStateException("Gatekeeper service not available");
    }
    return localIGateKeeperService;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/GateKeeper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */