package android.net.util;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.util.Slog;

public class AvoidBadWifiTracker
{
  private static String TAG = AvoidBadWifiTracker.class.getSimpleName();
  private volatile boolean mAvoidBadWifi = true;
  private final Context mContext;
  private final Handler mHandler;
  private final Runnable mReevaluateRunnable;
  private final SettingObserver mSettingObserver;
  
  public AvoidBadWifiTracker(Context paramContext, Handler paramHandler)
  {
    this(paramContext, paramHandler, null);
  }
  
  public AvoidBadWifiTracker(Context paramContext, Handler paramHandler, Runnable paramRunnable)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.mReevaluateRunnable = new -void__init__android_content_Context_ctx_android_os_Handler_handler_java_lang_Runnable_cb_LambdaImpl0(paramRunnable);
    this.mSettingObserver = new SettingObserver();
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.CONFIGURATION_CHANGED");
    this.mContext.registerReceiverAsUser(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        AvoidBadWifiTracker.this.reevaluate();
      }
    }, UserHandle.ALL, paramContext, null, null);
    update();
  }
  
  public boolean configRestrictsAvoidBadWifi()
  {
    boolean bool = false;
    if (this.mContext.getResources().getInteger(17694737) == 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean currentValue()
  {
    Slog.d(TAG, "force disable bad wifi tracker");
    return false;
  }
  
  public String getSettingsValue()
  {
    return Settings.Global.getString(this.mContext.getContentResolver(), "network_avoid_bad_wifi");
  }
  
  public void reevaluate()
  {
    this.mHandler.post(this.mReevaluateRunnable);
  }
  
  public boolean shouldNotifyWifiUnvalidated()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (configRestrictsAvoidBadWifi())
    {
      bool1 = bool2;
      if (getSettingsValue() == null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean update()
  {
    boolean bool1 = "1".equals(getSettingsValue());
    boolean bool2 = this.mAvoidBadWifi;
    if ((!bool1) && (configRestrictsAvoidBadWifi())) {}
    for (bool1 = false;; bool1 = true)
    {
      this.mAvoidBadWifi = bool1;
      if (this.mAvoidBadWifi == bool2) {
        break;
      }
      return true;
    }
    return false;
  }
  
  private class SettingObserver
    extends ContentObserver
  {
    private final Uri mUri = Settings.Global.getUriFor("network_avoid_bad_wifi");
    
    public SettingObserver()
    {
      super();
      AvoidBadWifiTracker.-get1(AvoidBadWifiTracker.this).getContentResolver().registerContentObserver(this.mUri, false, this);
    }
    
    public void onChange(boolean paramBoolean)
    {
      Slog.wtf(AvoidBadWifiTracker.-get0(), "Should never be reached.");
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if (!this.mUri.equals(paramUri)) {
        return;
      }
      AvoidBadWifiTracker.this.reevaluate();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/util/AvoidBadWifiTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */