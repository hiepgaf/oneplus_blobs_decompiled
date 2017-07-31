package android.service.notification;

import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public abstract class ConditionProviderService
  extends Service
{
  public static final String EXTRA_RULE_ID = "android.service.notification.extra.RULE_ID";
  public static final String META_DATA_CONFIGURATION_ACTIVITY = "android.service.zen.automatic.configurationActivity";
  public static final String META_DATA_RULE_INSTANCE_LIMIT = "android.service.zen.automatic.ruleInstanceLimit";
  public static final String META_DATA_RULE_TYPE = "android.service.zen.automatic.ruleType";
  public static final String SERVICE_INTERFACE = "android.service.notification.ConditionProviderService";
  private final String TAG = ConditionProviderService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";
  private final H mHandler = new H(null);
  private INotificationManager mNoMan;
  private Provider mProvider;
  
  private final INotificationManager getNotificationInterface()
  {
    if (this.mNoMan == null) {
      this.mNoMan = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    }
    return this.mNoMan;
  }
  
  private boolean isBound()
  {
    if (this.mProvider == null)
    {
      Log.w(this.TAG, "Condition provider service not yet bound.");
      return false;
    }
    return true;
  }
  
  public final void notifyCondition(Condition paramCondition)
  {
    if (paramCondition == null) {
      return;
    }
    notifyConditions(new Condition[] { paramCondition });
  }
  
  public final void notifyConditions(Condition... paramVarArgs)
  {
    if ((!isBound()) || (paramVarArgs == null)) {
      return;
    }
    try
    {
      getNotificationInterface().notifyConditions(getPackageName(), this.mProvider, paramVarArgs);
      return;
    }
    catch (RemoteException paramVarArgs)
    {
      Log.v(this.TAG, "Unable to contact notification manager", paramVarArgs);
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    if (this.mProvider == null) {
      this.mProvider = new Provider(null);
    }
    return this.mProvider;
  }
  
  public abstract void onConnected();
  
  public void onRequestConditions(int paramInt) {}
  
  public abstract void onSubscribe(Uri paramUri);
  
  public abstract void onUnsubscribe(Uri paramUri);
  
  private final class H
    extends Handler
  {
    private static final int ON_CONNECTED = 1;
    private static final int ON_SUBSCRIBE = 3;
    private static final int ON_UNSUBSCRIBE = 4;
    
    private H() {}
    
    public void handleMessage(Message paramMessage)
    {
      String str = null;
      try
      {
        switch (paramMessage.what)
        {
        case 1: 
          str = "onConnected";
          ConditionProviderService.this.onConnected();
          return;
        }
      }
      catch (Throwable paramMessage)
      {
        Log.w(ConditionProviderService.-get0(ConditionProviderService.this), "Error running " + str, paramMessage);
        return;
      }
      str = "onSubscribe";
      ConditionProviderService.this.onSubscribe((Uri)paramMessage.obj);
      return;
      str = "onUnsubscribe";
      ConditionProviderService.this.onUnsubscribe((Uri)paramMessage.obj);
      return;
    }
  }
  
  private final class Provider
    extends IConditionProvider.Stub
  {
    private Provider() {}
    
    public void onConnected()
    {
      ConditionProviderService.-get1(ConditionProviderService.this).obtainMessage(1).sendToTarget();
    }
    
    public void onSubscribe(Uri paramUri)
    {
      ConditionProviderService.-get1(ConditionProviderService.this).obtainMessage(3, paramUri).sendToTarget();
    }
    
    public void onUnsubscribe(Uri paramUri)
    {
      ConditionProviderService.-get1(ConditionProviderService.this).obtainMessage(4, paramUri).sendToTarget();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/notification/ConditionProviderService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */