package android.permissionpresenterservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.permission.IRuntimePermissionPresenter.Stub;
import android.content.pm.permission.RuntimePermissionPresentationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallback;
import com.android.internal.os.SomeArgs;
import java.util.List;

public abstract class RuntimePermissionPresenterService
  extends Service
{
  public static final String SERVICE_INTERFACE = "android.permissionpresenterservice.RuntimePermissionPresenterService";
  private Handler mHandler;
  
  public final void attachBaseContext(Context paramContext)
  {
    super.attachBaseContext(paramContext);
    this.mHandler = new MyHandler(paramContext.getMainLooper());
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    new IRuntimePermissionPresenter.Stub()
    {
      public void getAppPermissions(String paramAnonymousString, RemoteCallback paramAnonymousRemoteCallback)
      {
        SomeArgs localSomeArgs = SomeArgs.obtain();
        localSomeArgs.arg1 = paramAnonymousString;
        localSomeArgs.arg2 = paramAnonymousRemoteCallback;
        RuntimePermissionPresenterService.-get0(RuntimePermissionPresenterService.this).obtainMessage(1, localSomeArgs).sendToTarget();
      }
      
      public void getAppsUsingPermissions(boolean paramAnonymousBoolean, RemoteCallback paramAnonymousRemoteCallback)
      {
        Handler localHandler = RuntimePermissionPresenterService.-get0(RuntimePermissionPresenterService.this);
        if (paramAnonymousBoolean) {}
        for (int i = 1;; i = 0)
        {
          localHandler.obtainMessage(2, i, 0, paramAnonymousRemoteCallback).sendToTarget();
          return;
        }
      }
    };
  }
  
  public abstract List<RuntimePermissionPresentationInfo> onGetAppPermissions(String paramString);
  
  public abstract List<ApplicationInfo> onGetAppsUsingPermissions(boolean paramBoolean);
  
  private final class MyHandler
    extends Handler
  {
    public static final int MSG_GET_APPS_USING_PERMISSIONS = 2;
    public static final int MSG_GET_APP_PERMISSIONS = 1;
    
    public MyHandler(Looper paramLooper)
    {
      super(null, false);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        localObject1 = (SomeArgs)paramMessage.obj;
        localObject2 = (String)((SomeArgs)localObject1).arg1;
        paramMessage = (RemoteCallback)((SomeArgs)localObject1).arg2;
        ((SomeArgs)localObject1).recycle();
        localObject1 = RuntimePermissionPresenterService.this.onGetAppPermissions((String)localObject2);
        if ((localObject1 == null) || (((List)localObject1).isEmpty()))
        {
          paramMessage.sendResult(null);
          return;
        }
        localObject2 = new Bundle();
        ((Bundle)localObject2).putParcelableList("android.content.pm.permission.RuntimePermissionPresenter.key.result", (List)localObject1);
        paramMessage.sendResult((Bundle)localObject2);
        return;
      }
      Object localObject1 = (RemoteCallback)paramMessage.obj;
      if (paramMessage.arg1 == 1) {}
      for (boolean bool = true;; bool = false)
      {
        paramMessage = RuntimePermissionPresenterService.this.onGetAppsUsingPermissions(bool);
        if ((paramMessage != null) && (!paramMessage.isEmpty())) {
          break;
        }
        ((RemoteCallback)localObject1).sendResult(null);
        return;
      }
      Object localObject2 = new Bundle();
      ((Bundle)localObject2).putParcelableList("android.content.pm.permission.RuntimePermissionPresenter.key.result", paramMessage);
      ((RemoteCallback)localObject1).sendResult((Bundle)localObject2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/permissionpresenterservice/RuntimePermissionPresenterService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */