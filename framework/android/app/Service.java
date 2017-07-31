package android.app;

import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class Service
  extends ContextWrapper
  implements ComponentCallbacks2
{
  public static final int START_CONTINUATION_MASK = 15;
  public static final int START_FLAG_REDELIVERY = 1;
  public static final int START_FLAG_RETRY = 2;
  public static final int START_NOT_STICKY = 2;
  public static final int START_REDELIVER_INTENT = 3;
  public static final int START_STICKY = 1;
  public static final int START_STICKY_COMPATIBILITY = 0;
  public static final int START_TASK_REMOVED_COMPLETE = 1000;
  public static final int STOP_FOREGROUND_DETACH = 2;
  public static final int STOP_FOREGROUND_REMOVE = 1;
  private static final String TAG = "Service";
  private IActivityManager mActivityManager = null;
  private Application mApplication = null;
  private String mClassName = null;
  private boolean mStartCompatibility = false;
  private ActivityThread mThread = null;
  private IBinder mToken = null;
  
  public Service()
  {
    super(null);
  }
  
  public final void attach(Context paramContext, ActivityThread paramActivityThread, String paramString, IBinder paramIBinder, Application paramApplication, Object paramObject)
  {
    attachBaseContext(paramContext);
    this.mThread = paramActivityThread;
    this.mClassName = paramString;
    this.mToken = paramIBinder;
    this.mApplication = paramApplication;
    this.mActivityManager = ((IActivityManager)paramObject);
    if (getApplicationInfo().targetSdkVersion < 5) {}
    for (boolean bool = true;; bool = false)
    {
      this.mStartCompatibility = bool;
      return;
    }
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("nothing to dump");
  }
  
  public final Application getApplication()
  {
    return this.mApplication;
  }
  
  final String getClassName()
  {
    return this.mClassName;
  }
  
  public abstract IBinder onBind(Intent paramIntent);
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public void onCreate() {}
  
  public void onDestroy() {}
  
  public void onLowMemory() {}
  
  public void onRebind(Intent paramIntent) {}
  
  @Deprecated
  public void onStart(Intent paramIntent, int paramInt) {}
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    onStart(paramIntent, paramInt2);
    if (this.mStartCompatibility) {
      return 0;
    }
    return 1;
  }
  
  public void onTaskRemoved(Intent paramIntent) {}
  
  public void onTrimMemory(int paramInt) {}
  
  public boolean onUnbind(Intent paramIntent)
  {
    return false;
  }
  
  @Deprecated
  public final void setForeground(boolean paramBoolean)
  {
    Log.w("Service", "setForeground: ignoring old API call on " + getClass().getName());
  }
  
  public final void startForeground(int paramInt, Notification paramNotification)
  {
    try
    {
      this.mActivityManager.setServiceForeground(new ComponentName(this, this.mClassName), this.mToken, paramInt, paramNotification, 0);
      return;
    }
    catch (RemoteException paramNotification) {}
  }
  
  public final void stopForeground(int paramInt)
  {
    try
    {
      this.mActivityManager.setServiceForeground(new ComponentName(this, this.mClassName), this.mToken, 0, null, paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public final void stopForeground(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      stopForeground(i);
      return;
    }
  }
  
  public final void stopSelf()
  {
    stopSelf(-1);
  }
  
  public final void stopSelf(int paramInt)
  {
    if (this.mActivityManager == null) {
      return;
    }
    try
    {
      this.mActivityManager.stopServiceToken(new ComponentName(this, this.mClassName), this.mToken, paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public final boolean stopSelfResult(int paramInt)
  {
    if (this.mActivityManager == null) {
      return false;
    }
    try
    {
      boolean bool = this.mActivityManager.stopServiceToken(new ComponentName(this, this.mClassName), this.mToken, paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/Service.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */