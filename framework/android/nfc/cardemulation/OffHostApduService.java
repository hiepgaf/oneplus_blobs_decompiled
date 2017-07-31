package android.nfc.cardemulation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public abstract class OffHostApduService
  extends Service
{
  public static final String SERVICE_INTERFACE = "android.nfc.cardemulation.action.OFF_HOST_APDU_SERVICE";
  public static final String SERVICE_META_DATA = "android.nfc.cardemulation.off_host_apdu_service";
  
  public abstract IBinder onBind(Intent paramIntent);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/cardemulation/OffHostApduService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */