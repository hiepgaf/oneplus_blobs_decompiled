package android.service.carrier;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.telephony.ITelephonyRegistry;
import com.android.internal.telephony.ITelephonyRegistry.Stub;

public abstract class CarrierService
  extends Service
{
  public static final String CARRIER_SERVICE_INTERFACE = "android.service.carrier.CarrierService";
  private static ITelephonyRegistry sRegistry;
  private final ICarrierService.Stub mStubWrapper = new ICarrierServiceWrapper(null);
  
  public CarrierService()
  {
    if (sRegistry == null) {
      sRegistry = ITelephonyRegistry.Stub.asInterface(ServiceManager.getService("telephony.registry"));
    }
  }
  
  public final void notifyCarrierNetworkChange(boolean paramBoolean)
  {
    try
    {
      if (sRegistry != null) {
        sRegistry.notifyCarrierNetworkChange(paramBoolean);
      }
      return;
    }
    catch (RemoteException|NullPointerException localRemoteException) {}
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return this.mStubWrapper;
  }
  
  public abstract PersistableBundle onLoadConfig(CarrierIdentifier paramCarrierIdentifier);
  
  private class ICarrierServiceWrapper
    extends ICarrierService.Stub
  {
    private ICarrierServiceWrapper() {}
    
    public PersistableBundle getCarrierConfig(CarrierIdentifier paramCarrierIdentifier)
    {
      return CarrierService.this.onLoadConfig(paramCarrierIdentifier);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/carrier/CarrierService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */