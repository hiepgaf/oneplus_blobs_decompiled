package android.service.vr;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public abstract class VrListenerService
  extends Service
{
  private static final int MSG_ON_CURRENT_VR_ACTIVITY_CHANGED = 1;
  public static final String SERVICE_INTERFACE = "android.service.vr.VrListenerService";
  private final IVrListener.Stub mBinder = new IVrListener.Stub()
  {
    public void focusedActivityChanged(ComponentName paramAnonymousComponentName)
    {
      VrListenerService.-get0(VrListenerService.this).obtainMessage(1, paramAnonymousComponentName).sendToTarget();
    }
  };
  private final Handler mHandler = new VrListenerHandler(Looper.getMainLooper());
  
  public static final boolean isVrModePackageEnabled(Context paramContext, ComponentName paramComponentName)
  {
    paramContext = (ActivityManager)paramContext.getSystemService(ActivityManager.class);
    if (paramContext == null) {
      return false;
    }
    return paramContext.isVrModePackageEnabled(paramComponentName);
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }
  
  public void onCurrentVrActivityChanged(ComponentName paramComponentName) {}
  
  private final class VrListenerHandler
    extends Handler
  {
    public VrListenerHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      VrListenerService.this.onCurrentVrActivityChanged((ComponentName)paramMessage.obj);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/vr/VrListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */