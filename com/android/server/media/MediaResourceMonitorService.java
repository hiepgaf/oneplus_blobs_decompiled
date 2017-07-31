package com.android.server.media;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.IMediaResourceMonitor.Stub;
import android.os.Binder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.util.Slog;
import com.android.server.SystemService;
import java.util.Iterator;

public class MediaResourceMonitorService
  extends SystemService
{
  private static final boolean DEBUG = Log.isLoggable("MediaResourceMonitor", 3);
  private static final String SERVICE_NAME = "media_resource_monitor";
  private static final String TAG = "MediaResourceMonitor";
  private final MediaResourceMonitorImpl mMediaResourceMonitorImpl = new MediaResourceMonitorImpl();
  
  public MediaResourceMonitorService(Context paramContext)
  {
    super(paramContext);
  }
  
  public void onStart()
  {
    publishBinderService("media_resource_monitor", this.mMediaResourceMonitorImpl);
  }
  
  class MediaResourceMonitorImpl
    extends IMediaResourceMonitor.Stub
  {
    MediaResourceMonitorImpl() {}
    
    private String[] getPackageNamesFromPid(int paramInt)
    {
      try
      {
        Object localObject = ActivityManagerNative.getDefault().getRunningAppProcesses().iterator();
        while (((Iterator)localObject).hasNext())
        {
          ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)((Iterator)localObject).next();
          if (localRunningAppProcessInfo.pid == paramInt)
          {
            localObject = localRunningAppProcessInfo.pkgList;
            return (String[])localObject;
          }
        }
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("MediaResourceMonitor", "ActivityManager.getRunningAppProcesses() failed");
      }
      return null;
    }
    
    public void notifyResourceGranted(int paramInt1, int paramInt2)
      throws RemoteException
    {
      int i = 0;
      if (MediaResourceMonitorService.-get0()) {
        Slog.d("MediaResourceMonitor", "notifyResourceGranted(pid=" + paramInt1 + ", type=" + paramInt2 + ")");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        String[] arrayOfString = getPackageNamesFromPid(paramInt1);
        if (arrayOfString == null) {
          return;
        }
        int[] arrayOfInt = ((UserManager)MediaResourceMonitorService.this.getContext().getSystemService("user")).getEnabledProfileIds(ActivityManager.getCurrentUser());
        if (arrayOfInt != null)
        {
          paramInt1 = arrayOfInt.length;
          if (paramInt1 != 0) {}
        }
        else
        {
          return;
        }
        Intent localIntent = new Intent("android.intent.action.MEDIA_RESOURCE_GRANTED");
        localIntent.putExtra("android.intent.extra.PACKAGES", arrayOfString);
        localIntent.putExtra("android.intent.extra.MEDIA_RESOURCE_TYPE", paramInt2);
        paramInt2 = arrayOfInt.length;
        paramInt1 = i;
        while (paramInt1 < paramInt2)
        {
          i = arrayOfInt[paramInt1];
          MediaResourceMonitorService.this.getContext().sendBroadcastAsUser(localIntent, UserHandle.of(i), "android.permission.RECEIVE_MEDIA_RESOURCE_USAGE");
          paramInt1 += 1;
        }
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/media/MediaResourceMonitorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */