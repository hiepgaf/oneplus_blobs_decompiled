package android.location;

import android.app.Service;
import android.content.Intent;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public abstract class SettingInjectorService
  extends Service
{
  public static final String ACTION_INJECTED_SETTING_CHANGED = "android.location.InjectedSettingChanged";
  public static final String ACTION_SERVICE_INTENT = "android.location.SettingInjectorService";
  public static final String ATTRIBUTES_NAME = "injected-location-setting";
  public static final String ENABLED_KEY = "enabled";
  public static final String MESSENGER_KEY = "messenger";
  public static final String META_DATA_NAME = "android.location.SettingInjectorService";
  private static final String TAG = "SettingInjectorService";
  private final String mName;
  
  public SettingInjectorService(String paramString)
  {
    this.mName = paramString;
  }
  
  private void onHandleIntent(Intent paramIntent)
  {
    try
    {
      boolean bool = onGetEnabled();
      sendStatus(paramIntent, bool);
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      sendStatus(paramIntent, true);
      throw localRuntimeException;
    }
  }
  
  private void sendStatus(Intent paramIntent, boolean paramBoolean)
  {
    Message localMessage = Message.obtain();
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("enabled", paramBoolean);
    localMessage.setData(localBundle);
    if (Log.isLoggable("SettingInjectorService", 3)) {
      Log.d("SettingInjectorService", this.mName + ": received " + paramIntent + ", enabled=" + paramBoolean + ", sending message: " + localMessage);
    }
    paramIntent = (Messenger)paramIntent.getParcelableExtra("messenger");
    try
    {
      paramIntent.send(localMessage);
      return;
    }
    catch (RemoteException paramIntent)
    {
      Log.e("SettingInjectorService", this.mName + ": sending dynamic status failed", paramIntent);
    }
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  protected abstract boolean onGetEnabled();
  
  @Deprecated
  protected abstract String onGetSummary();
  
  public final void onStart(Intent paramIntent, int paramInt)
  {
    super.onStart(paramIntent, paramInt);
  }
  
  public final int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    onHandleIntent(paramIntent);
    stopSelf(paramInt2);
    return 2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/SettingInjectorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */