package android.app;

import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Bundle;
import java.util.ArrayList;

public class Application
  extends ContextWrapper
  implements ComponentCallbacks2
{
  private ArrayList<ActivityLifecycleCallbacks> mActivityLifecycleCallbacks = new ArrayList();
  private ArrayList<OnProvideAssistDataListener> mAssistCallbacks = null;
  private ArrayList<ComponentCallbacks> mComponentCallbacks = new ArrayList();
  public LoadedApk mLoadedApk;
  
  public Application()
  {
    super(null);
  }
  
  private Object[] collectActivityLifecycleCallbacks()
  {
    Object[] arrayOfObject = null;
    synchronized (this.mActivityLifecycleCallbacks)
    {
      if (this.mActivityLifecycleCallbacks.size() > 0) {
        arrayOfObject = this.mActivityLifecycleCallbacks.toArray();
      }
      return arrayOfObject;
    }
  }
  
  private Object[] collectComponentCallbacks()
  {
    Object[] arrayOfObject = null;
    synchronized (this.mComponentCallbacks)
    {
      if (this.mComponentCallbacks.size() > 0) {
        arrayOfObject = this.mComponentCallbacks.toArray();
      }
      return arrayOfObject;
    }
  }
  
  final void attach(Context paramContext)
  {
    attachBaseContext(paramContext);
    this.mLoadedApk = ContextImpl.getImpl(paramContext).mPackageInfo;
  }
  
  void dispatchActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    Object[] arrayOfObject = collectActivityLifecycleCallbacks();
    if (arrayOfObject != null)
    {
      int i = 0;
      while (i < arrayOfObject.length)
      {
        ((ActivityLifecycleCallbacks)arrayOfObject[i]).onActivityCreated(paramActivity, paramBundle);
        i += 1;
      }
    }
  }
  
  void dispatchActivityDestroyed(Activity paramActivity)
  {
    Object[] arrayOfObject = collectActivityLifecycleCallbacks();
    if (arrayOfObject != null)
    {
      int i = 0;
      while (i < arrayOfObject.length)
      {
        ((ActivityLifecycleCallbacks)arrayOfObject[i]).onActivityDestroyed(paramActivity);
        i += 1;
      }
    }
  }
  
  void dispatchActivityPaused(Activity paramActivity)
  {
    Object[] arrayOfObject = collectActivityLifecycleCallbacks();
    if (arrayOfObject != null)
    {
      int i = 0;
      while (i < arrayOfObject.length)
      {
        ((ActivityLifecycleCallbacks)arrayOfObject[i]).onActivityPaused(paramActivity);
        i += 1;
      }
    }
  }
  
  void dispatchActivityResumed(Activity paramActivity)
  {
    Object[] arrayOfObject = collectActivityLifecycleCallbacks();
    if (arrayOfObject != null)
    {
      int i = 0;
      while (i < arrayOfObject.length)
      {
        ((ActivityLifecycleCallbacks)arrayOfObject[i]).onActivityResumed(paramActivity);
        i += 1;
      }
    }
  }
  
  void dispatchActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle)
  {
    Object[] arrayOfObject = collectActivityLifecycleCallbacks();
    if (arrayOfObject != null)
    {
      int i = 0;
      while (i < arrayOfObject.length)
      {
        ((ActivityLifecycleCallbacks)arrayOfObject[i]).onActivitySaveInstanceState(paramActivity, paramBundle);
        i += 1;
      }
    }
  }
  
  void dispatchActivityStarted(Activity paramActivity)
  {
    Object[] arrayOfObject = collectActivityLifecycleCallbacks();
    if (arrayOfObject != null)
    {
      int i = 0;
      while (i < arrayOfObject.length)
      {
        ((ActivityLifecycleCallbacks)arrayOfObject[i]).onActivityStarted(paramActivity);
        i += 1;
      }
    }
  }
  
  void dispatchActivityStopped(Activity paramActivity)
  {
    Object[] arrayOfObject = collectActivityLifecycleCallbacks();
    if (arrayOfObject != null)
    {
      int i = 0;
      while (i < arrayOfObject.length)
      {
        ((ActivityLifecycleCallbacks)arrayOfObject[i]).onActivityStopped(paramActivity);
        i += 1;
      }
    }
  }
  
  void dispatchOnProvideAssistData(Activity paramActivity, Bundle paramBundle)
  {
    try
    {
      Object localObject = this.mAssistCallbacks;
      if (localObject == null) {
        return;
      }
      localObject = this.mAssistCallbacks.toArray();
      if (localObject != null)
      {
        int i = 0;
        while (i < localObject.length)
        {
          ((OnProvideAssistDataListener)localObject[i]).onProvideAssistData(paramActivity, paramBundle);
          i += 1;
        }
      }
      return;
    }
    finally {}
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    Object[] arrayOfObject = collectComponentCallbacks();
    if (arrayOfObject != null)
    {
      int i = 0;
      while (i < arrayOfObject.length)
      {
        ((ComponentCallbacks)arrayOfObject[i]).onConfigurationChanged(paramConfiguration);
        i += 1;
      }
    }
  }
  
  public void onCreate() {}
  
  public void onLowMemory()
  {
    Object[] arrayOfObject = collectComponentCallbacks();
    if (arrayOfObject != null)
    {
      int i = 0;
      while (i < arrayOfObject.length)
      {
        ((ComponentCallbacks)arrayOfObject[i]).onLowMemory();
        i += 1;
      }
    }
  }
  
  public void onTerminate() {}
  
  public void onTrimMemory(int paramInt)
  {
    Object[] arrayOfObject = collectComponentCallbacks();
    if (arrayOfObject != null)
    {
      int i = 0;
      while (i < arrayOfObject.length)
      {
        Object localObject = arrayOfObject[i];
        if ((localObject instanceof ComponentCallbacks2)) {
          ((ComponentCallbacks2)localObject).onTrimMemory(paramInt);
        }
        i += 1;
      }
    }
  }
  
  public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks paramActivityLifecycleCallbacks)
  {
    synchronized (this.mActivityLifecycleCallbacks)
    {
      this.mActivityLifecycleCallbacks.add(paramActivityLifecycleCallbacks);
      return;
    }
  }
  
  public void registerComponentCallbacks(ComponentCallbacks paramComponentCallbacks)
  {
    synchronized (this.mComponentCallbacks)
    {
      this.mComponentCallbacks.add(paramComponentCallbacks);
      return;
    }
  }
  
  public void registerOnProvideAssistDataListener(OnProvideAssistDataListener paramOnProvideAssistDataListener)
  {
    try
    {
      if (this.mAssistCallbacks == null) {
        this.mAssistCallbacks = new ArrayList();
      }
      this.mAssistCallbacks.add(paramOnProvideAssistDataListener);
      return;
    }
    finally {}
  }
  
  public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks paramActivityLifecycleCallbacks)
  {
    synchronized (this.mActivityLifecycleCallbacks)
    {
      this.mActivityLifecycleCallbacks.remove(paramActivityLifecycleCallbacks);
      return;
    }
  }
  
  public void unregisterComponentCallbacks(ComponentCallbacks paramComponentCallbacks)
  {
    synchronized (this.mComponentCallbacks)
    {
      this.mComponentCallbacks.remove(paramComponentCallbacks);
      return;
    }
  }
  
  public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener paramOnProvideAssistDataListener)
  {
    try
    {
      if (this.mAssistCallbacks != null) {
        this.mAssistCallbacks.remove(paramOnProvideAssistDataListener);
      }
      return;
    }
    finally
    {
      paramOnProvideAssistDataListener = finally;
      throw paramOnProvideAssistDataListener;
    }
  }
  
  public static abstract interface ActivityLifecycleCallbacks
  {
    public abstract void onActivityCreated(Activity paramActivity, Bundle paramBundle);
    
    public abstract void onActivityDestroyed(Activity paramActivity);
    
    public abstract void onActivityPaused(Activity paramActivity);
    
    public abstract void onActivityResumed(Activity paramActivity);
    
    public abstract void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle);
    
    public abstract void onActivityStarted(Activity paramActivity);
    
    public abstract void onActivityStopped(Activity paramActivity);
  }
  
  public static abstract interface OnProvideAssistDataListener
  {
    public abstract void onProvideAssistData(Activity paramActivity, Bundle paramBundle);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/Application.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */