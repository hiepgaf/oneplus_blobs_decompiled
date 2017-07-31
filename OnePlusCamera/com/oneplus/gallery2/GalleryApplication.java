package com.oneplus.gallery2;

import com.oneplus.base.BaseApplication;
import com.oneplus.base.EventKey;
import com.oneplus.base.Log;
import com.oneplus.base.ThreadMonitor;

public abstract class GalleryApplication
  extends BaseApplication
{
  public static final EventKey<ActivityLaunchEventArgs> EVENT_ACTIVITY_LAUNCHED = new EventKey("ActivityLaunched", ActivityLaunchEventArgs.class, GalleryApplication.class);
  
  public static GalleryApplication current()
  {
    return (GalleryApplication)BaseApplication.current();
  }
  
  public abstract Gallery createGallery();
  
  final void notifyActivityLaunched(GalleryActivity paramGalleryActivity, ActivityLaunchType paramActivityLaunchType)
  {
    raise(EVENT_ACTIVITY_LAUNCHED, new ActivityLaunchEventArgs(paramGalleryActivity, paramActivityLaunchType));
  }
  
  public void onCreate()
  {
    ThreadMonitor.prepare();
    ThreadMonitor.startMonitorCurrentThread();
    Log.w(this.TAG, "onCreate() - Starting media content thread");
    try
    {
      MediaContentThread.startSync();
      Log.w(this.TAG, "onCreate() - Media content thread started");
      super.onCreate();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new RuntimeException("Fail to start media content thread", localInterruptedException);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/GalleryApplication.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */