package android.service.chooser;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import java.util.List;

public abstract class ChooserTargetService
  extends Service
{
  public static final String BIND_PERMISSION = "android.permission.BIND_CHOOSER_TARGET_SERVICE";
  private static final boolean DEBUG = false;
  public static final String META_DATA_NAME = "android.service.chooser.chooser_target_service";
  public static final String SERVICE_INTERFACE = "android.service.chooser.ChooserTargetService";
  private final String TAG = ChooserTargetService.class.getSimpleName() + '[' + getClass().getSimpleName() + ']';
  private IChooserTargetServiceWrapper mWrapper = null;
  
  public IBinder onBind(Intent paramIntent)
  {
    if (!"android.service.chooser.ChooserTargetService".equals(paramIntent.getAction())) {
      return null;
    }
    if (this.mWrapper == null) {
      this.mWrapper = new IChooserTargetServiceWrapper(null);
    }
    return this.mWrapper;
  }
  
  public abstract List<ChooserTarget> onGetChooserTargets(ComponentName paramComponentName, IntentFilter paramIntentFilter);
  
  private class IChooserTargetServiceWrapper
    extends IChooserTargetService.Stub
  {
    private IChooserTargetServiceWrapper() {}
    
    public void getChooserTargets(ComponentName paramComponentName, IntentFilter paramIntentFilter, IChooserTargetResult paramIChooserTargetResult)
      throws RemoteException
    {
      long l = clearCallingIdentity();
      try
      {
        paramComponentName = ChooserTargetService.this.onGetChooserTargets(paramComponentName, paramIntentFilter);
        restoreCallingIdentity(l);
        paramIChooserTargetResult.sendResult(paramComponentName);
        return;
      }
      finally
      {
        restoreCallingIdentity(l);
        paramIChooserTargetResult.sendResult(null);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/chooser/ChooserTargetService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */