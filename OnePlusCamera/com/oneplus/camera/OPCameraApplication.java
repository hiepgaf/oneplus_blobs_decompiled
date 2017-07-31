package com.oneplus.camera;

import com.oneplus.base.Log;
import com.oneplus.base.PermissionManagerBuilder;
import com.oneplus.base.ThreadMonitor;
import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.gallery2.MediaContentThread;
import com.oneplus.gallery2.media.ContentObserverBuilder;
import com.oneplus.gallery2.media.MediaStoreDirectoryManagerBuilder;
import com.oneplus.gallery2.media.MediaStoreMediaSourceBuilder;
import com.oneplus.io.StorageManagerBuilder;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class OPCameraApplication
  extends CameraApplication
{
  public static final boolean DEBUG = true;
  private static final ComponentBuilder[] DEFAULT_COMPONENT_BUILDERS = { new ContentObserverBuilder(), new MediaStoreDirectoryManagerBuilder(), new MediaStoreMediaSourceBuilder(), new PermissionManagerBuilder(), new StorageManagerBuilder() };
  private static final String TAG = "CameraApplication";
  private static final List<WeakReference<CameraActivity>> m_InstanceRefs = new ArrayList();
  
  public OPCameraApplication()
  {
    addComponentBuilders(DEFAULT_COMPONENT_BUILDERS);
  }
  
  static void notifyInstanceCreated(CameraActivity paramCameraActivity)
  {
    int i = m_InstanceRefs.size() - 1;
    while (i >= 0)
    {
      if (((WeakReference)m_InstanceRefs.get(i)).get() == null) {
        m_InstanceRefs.remove(i);
      }
      i -= 1;
    }
    m_InstanceRefs.add(new WeakReference(paramCameraActivity));
    Log.v("CameraApplication", "notifyInstanceCreated() - Instance count : ", Integer.valueOf(m_InstanceRefs.size()));
  }
  
  static void notifyInstanceDestroyed(CameraActivity paramCameraActivity) {}
  
  public void onCreate()
  {
    Log.v("CameraApplication", "onCreate()");
    try
    {
      MediaContentThread.startSync();
      super.onCreate();
      ThreadMonitor.prepare();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;)
      {
        Log.e("CameraApplication", "onCreate() - Error while starting media content thread", localInterruptedException);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/OPCameraApplication.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */