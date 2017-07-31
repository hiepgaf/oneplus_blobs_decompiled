package android.content.pm.permission;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallback;
import android.os.RemoteCallback.OnResultListener;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.SomeArgs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RuntimePermissionPresenter
{
  public static final String KEY_RESULT = "android.content.pm.permission.RuntimePermissionPresenter.key.result";
  private static final String TAG = "RuntimePermPresenter";
  @GuardedBy("sLock")
  private static RuntimePermissionPresenter sInstance;
  private static final Object sLock = new Object();
  private final RemoteService mRemoteService;
  
  private RuntimePermissionPresenter(Context paramContext)
  {
    this.mRemoteService = new RemoteService(paramContext);
  }
  
  public static RuntimePermissionPresenter getInstance(Context paramContext)
  {
    synchronized (sLock)
    {
      if (sInstance == null) {
        sInstance = new RuntimePermissionPresenter(paramContext.getApplicationContext());
      }
      paramContext = sInstance;
      return paramContext;
    }
  }
  
  public void getAppPermissions(String paramString, OnResultCallback paramOnResultCallback, Handler paramHandler)
  {
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.arg1 = paramString;
    localSomeArgs.arg2 = paramOnResultCallback;
    localSomeArgs.arg3 = paramHandler;
    paramString = this.mRemoteService.obtainMessage(1, localSomeArgs);
    this.mRemoteService.processMessage(paramString);
  }
  
  public void getAppsUsingPermissions(boolean paramBoolean, OnResultCallback paramOnResultCallback, Handler paramHandler)
  {
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.arg1 = paramOnResultCallback;
    localSomeArgs.arg2 = paramHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localSomeArgs.argi1 = i;
      paramOnResultCallback = this.mRemoteService.obtainMessage(2, localSomeArgs);
      this.mRemoteService.processMessage(paramOnResultCallback);
      return;
    }
  }
  
  public static abstract class OnResultCallback
  {
    public void getAppsUsingPermissions(boolean paramBoolean, List<ApplicationInfo> paramList) {}
    
    public void onGetAppPermissions(List<RuntimePermissionPresentationInfo> paramList) {}
  }
  
  private static final class RemoteService
    extends Handler
    implements ServiceConnection
  {
    public static final int MSG_GET_APPS_USING_PERMISSIONS = 2;
    public static final int MSG_GET_APP_PERMISSIONS = 1;
    public static final int MSG_UNBIND = 3;
    private static final long UNBIND_TIMEOUT_MILLIS = 10000L;
    @GuardedBy("mLock")
    private boolean mBound;
    private final Context mContext;
    private final Object mLock = new Object();
    @GuardedBy("mLock")
    private final List<Message> mPendingWork = new ArrayList();
    @GuardedBy("mLock")
    private IRuntimePermissionPresenter mRemoteInstance;
    
    public RemoteService(Context paramContext)
    {
      super(null, false);
      this.mContext = paramContext;
    }
    
    private void scheduleNextMessageIfNeededLocked()
    {
      if ((!this.mBound) || (this.mRemoteInstance == null) || (this.mPendingWork.isEmpty())) {
        return;
      }
      sendMessage((Message)this.mPendingWork.remove(0));
    }
    
    private void scheduleUnbind()
    {
      removeMessages(3);
      sendEmptyMessageDelayed(3, 10000L);
    }
    
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      }
      synchronized (this.mLock)
      {
        for (;;)
        {
          scheduleNextMessageIfNeededLocked();
          return;
          ??? = (SomeArgs)???.obj;
          ??? = (String)((SomeArgs)???).arg1;
          final Object localObject1 = (RuntimePermissionPresenter.OnResultCallback)((SomeArgs)???).arg2;
          ??? = (Handler)((SomeArgs)???).arg3;
          ((SomeArgs)???).recycle();
          IRuntimePermissionPresenter localIRuntimePermissionPresenter;
          synchronized (this.mLock)
          {
            localIRuntimePermissionPresenter = this.mRemoteInstance;
            if (localIRuntimePermissionPresenter == null) {
              return;
            }
          }
          try
          {
            localIRuntimePermissionPresenter.getAppPermissions(???, new RemoteCallback(new RemoteCallback.OnResultListener()
            {
              public void onResult(final Bundle paramAnonymousBundle)
              {
                ArrayList localArrayList = null;
                if (paramAnonymousBundle != null) {
                  localArrayList = paramAnonymousBundle.getParcelableArrayList("android.content.pm.permission.RuntimePermissionPresenter.key.result");
                }
                paramAnonymousBundle = localArrayList;
                if (localArrayList == null) {
                  paramAnonymousBundle = Collections.emptyList();
                }
                if (localObject4 != null)
                {
                  localObject4.post(new Runnable()
                  {
                    public void run()
                    {
                      this.val$callback.onGetAppPermissions(paramAnonymousBundle);
                    }
                  });
                  return;
                }
                localObject1.onGetAppPermissions(paramAnonymousBundle);
              }
            }, this));
            scheduleUnbind();
          }
          catch (RemoteException ???)
          {
            for (;;)
            {
              Log.e("RuntimePermPresenter", "Error getting app permissions", ???);
            }
          }
          ??? = (SomeArgs)???.obj;
          ??? = (RuntimePermissionPresenter.OnResultCallback)((SomeArgs)???).arg1;
          localObject1 = (Handler)((SomeArgs)???).arg2;
          final boolean bool;
          if (((SomeArgs)???).argi1 == 1) {
            bool = true;
          }
          for (;;)
          {
            ((SomeArgs)???).recycle();
            synchronized (this.mLock)
            {
              ??? = this.mRemoteInstance;
              if (??? == null)
              {
                return;
                bool = false;
              }
            }
          }
          try
          {
            ((IRuntimePermissionPresenter)???).getAppsUsingPermissions(bool, new RemoteCallback(new RemoteCallback.OnResultListener()
            {
              public void onResult(final Bundle paramAnonymousBundle)
              {
                ArrayList localArrayList = null;
                if (paramAnonymousBundle != null) {
                  localArrayList = paramAnonymousBundle.getParcelableArrayList("android.content.pm.permission.RuntimePermissionPresenter.key.result");
                }
                paramAnonymousBundle = localArrayList;
                if (localArrayList == null) {
                  paramAnonymousBundle = Collections.emptyList();
                }
                if (localObject1 != null)
                {
                  localObject1.post(new Runnable()
                  {
                    public void run()
                    {
                      this.val$callback.getAppsUsingPermissions(this.val$system, paramAnonymousBundle);
                    }
                  });
                  return;
                }
                paramMessage.getAppsUsingPermissions(bool, paramAnonymousBundle);
              }
            }, this));
            scheduleUnbind();
          }
          catch (RemoteException ???)
          {
            for (;;)
            {
              Log.e("RuntimePermPresenter", "Error getting apps using permissions", ???);
            }
          }
          synchronized (this.mLock)
          {
            if (this.mBound)
            {
              this.mContext.unbindService(this);
              this.mBound = false;
            }
            this.mRemoteInstance = null;
          }
        }
      }
    }
    
    public void onServiceConnected(ComponentName arg1, IBinder paramIBinder)
    {
      synchronized (this.mLock)
      {
        this.mRemoteInstance = IRuntimePermissionPresenter.Stub.asInterface(paramIBinder);
        scheduleNextMessageIfNeededLocked();
        return;
      }
    }
    
    public void onServiceDisconnected(ComponentName arg1)
    {
      synchronized (this.mLock)
      {
        this.mRemoteInstance = null;
        return;
      }
    }
    
    public void processMessage(Message paramMessage)
    {
      synchronized (this.mLock)
      {
        if (!this.mBound)
        {
          Intent localIntent = new Intent("android.permissionpresenterservice.RuntimePermissionPresenterService");
          localIntent.setPackage(this.mContext.getPackageManager().getPermissionControllerPackageName());
          this.mBound = this.mContext.bindService(localIntent, this, 1);
        }
        this.mPendingWork.add(paramMessage);
        scheduleNextMessageIfNeededLocked();
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/permission/RuntimePermissionPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */