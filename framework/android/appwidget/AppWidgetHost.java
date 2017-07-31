package android.appwidget;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ParceledListSlice;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViews.OnClickHandler;
import com.android.internal.appwidget.IAppWidgetHost.Stub;
import com.android.internal.appwidget.IAppWidgetService;
import com.android.internal.appwidget.IAppWidgetService.Stub;
import java.lang.ref.WeakReference;
import java.util.List;

public class AppWidgetHost
{
  static final int HANDLE_PROVIDERS_CHANGED = 3;
  static final int HANDLE_PROVIDER_CHANGED = 2;
  static final int HANDLE_UPDATE = 1;
  static final int HANDLE_VIEW_DATA_CHANGED = 4;
  static IAppWidgetService sService;
  static final Object sServiceLock = new Object();
  private final Callbacks mCallbacks;
  private String mContextOpPackageName;
  private DisplayMetrics mDisplayMetrics;
  private final Handler mHandler;
  private final int mHostId;
  private RemoteViews.OnClickHandler mOnClickHandler;
  private final SparseArray<AppWidgetHostView> mViews = new SparseArray();
  
  public AppWidgetHost(Context paramContext, int paramInt)
  {
    this(paramContext, paramInt, null, paramContext.getMainLooper());
  }
  
  public AppWidgetHost(Context paramContext, int paramInt, RemoteViews.OnClickHandler paramOnClickHandler, Looper paramLooper)
  {
    this.mContextOpPackageName = paramContext.getOpPackageName();
    this.mHostId = paramInt;
    this.mOnClickHandler = paramOnClickHandler;
    this.mHandler = new UpdateHandler(paramLooper);
    this.mCallbacks = new Callbacks(this.mHandler);
    this.mDisplayMetrics = paramContext.getResources().getDisplayMetrics();
    bindService();
  }
  
  private static void bindService()
  {
    synchronized (sServiceLock)
    {
      if (sService == null) {
        sService = IAppWidgetService.Stub.asInterface(ServiceManager.getService("appwidget"));
      }
      return;
    }
  }
  
  public static void deleteAllHosts()
  {
    try
    {
      sService.deleteAllHosts();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("system server dead?", localRemoteException);
    }
  }
  
  public int allocateAppWidgetId()
  {
    try
    {
      int i = sService.allocateAppWidgetId(this.mContextOpPackageName, this.mHostId);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("system server dead?", localRemoteException);
    }
  }
  
  protected void clearViews()
  {
    synchronized (this.mViews)
    {
      this.mViews.clear();
      return;
    }
  }
  
  public final AppWidgetHostView createView(Context paramContext, int paramInt, AppWidgetProviderInfo arg3)
  {
    paramContext = onCreateView(paramContext, paramInt, ???);
    paramContext.setOnClickHandler(this.mOnClickHandler);
    paramContext.setAppWidget(paramInt, ???);
    synchronized (this.mViews)
    {
      this.mViews.put(paramInt, paramContext);
    }
  }
  
  public void deleteAppWidgetId(int paramInt)
  {
    synchronized (this.mViews)
    {
      this.mViews.remove(paramInt);
      try
      {
        sService.deleteAppWidgetId(this.mContextOpPackageName, paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException("system server dead?", localRemoteException);
      }
    }
  }
  
  public void deleteHost()
  {
    try
    {
      sService.deleteHost(this.mContextOpPackageName, this.mHostId);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("system server dead?", localRemoteException);
    }
  }
  
  public int[] getAppWidgetIds()
  {
    try
    {
      if (sService == null) {
        bindService();
      }
      int[] arrayOfInt = sService.getAppWidgetIdsForHost(this.mContextOpPackageName, this.mHostId);
      return arrayOfInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("system server dead?", localRemoteException);
    }
  }
  
  protected AppWidgetHostView onCreateView(Context paramContext, int paramInt, AppWidgetProviderInfo paramAppWidgetProviderInfo)
  {
    return new AppWidgetHostView(paramContext, this.mOnClickHandler);
  }
  
  protected void onProviderChanged(int paramInt, AppWidgetProviderInfo paramAppWidgetProviderInfo)
  {
    paramAppWidgetProviderInfo.minWidth = TypedValue.complexToDimensionPixelSize(paramAppWidgetProviderInfo.minWidth, this.mDisplayMetrics);
    paramAppWidgetProviderInfo.minHeight = TypedValue.complexToDimensionPixelSize(paramAppWidgetProviderInfo.minHeight, this.mDisplayMetrics);
    paramAppWidgetProviderInfo.minResizeWidth = TypedValue.complexToDimensionPixelSize(paramAppWidgetProviderInfo.minResizeWidth, this.mDisplayMetrics);
    paramAppWidgetProviderInfo.minResizeHeight = TypedValue.complexToDimensionPixelSize(paramAppWidgetProviderInfo.minResizeHeight, this.mDisplayMetrics);
    synchronized (this.mViews)
    {
      AppWidgetHostView localAppWidgetHostView = (AppWidgetHostView)this.mViews.get(paramInt);
      if (localAppWidgetHostView != null) {
        localAppWidgetHostView.resetAppWidget(paramAppWidgetProviderInfo);
      }
      return;
    }
  }
  
  protected void onProvidersChanged() {}
  
  public final void startAppWidgetConfigureActivityForResult(Activity paramActivity, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle)
  {
    try
    {
      IntentSender localIntentSender = sService.createAppWidgetConfigIntentSender(this.mContextOpPackageName, paramInt1, paramInt2);
      if (localIntentSender != null)
      {
        paramActivity.startIntentSenderForResult(localIntentSender, paramInt3, null, 0, 0, 0, paramBundle);
        return;
      }
      throw new ActivityNotFoundException();
    }
    catch (IntentSender.SendIntentException paramActivity)
    {
      throw new ActivityNotFoundException();
    }
    catch (RemoteException paramActivity)
    {
      throw new RuntimeException("system server dead?", paramActivity);
    }
  }
  
  public void startListening()
  {
    int j;
    Object localObject2;
    int i;
    synchronized (this.mViews)
    {
      j = this.mViews.size();
      localObject2 = new int[j];
      i = 0;
      while (i < j)
      {
        localObject2[i] = this.mViews.keyAt(i);
        i += 1;
      }
    }
    for (;;)
    {
      try
      {
        ??? = sService.startListening(this.mCallbacks, this.mContextOpPackageName, this.mHostId, (int[])localObject2).getList();
        j = ((List)???).size();
        i = 0;
        if (i >= j) {
          break;
        }
        localObject2 = (PendingHostUpdate)((List)???).get(i);
        switch (((PendingHostUpdate)localObject2).type)
        {
        default: 
          i += 1;
          continue;
          localObject3 = finally;
          throw ((Throwable)localObject3);
        }
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException("system server dead?", localRemoteException);
      }
      updateAppWidgetView(((PendingHostUpdate)localObject3).appWidgetId, ((PendingHostUpdate)localObject3).views);
      continue;
      onProviderChanged(((PendingHostUpdate)localObject3).appWidgetId, ((PendingHostUpdate)localObject3).widgetInfo);
      continue;
      viewDataChanged(((PendingHostUpdate)localObject3).appWidgetId, ((PendingHostUpdate)localObject3).viewId);
    }
  }
  
  public void stopListening()
  {
    try
    {
      sService.stopListening(this.mContextOpPackageName, this.mHostId);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("system server dead?", localRemoteException);
    }
  }
  
  void updateAppWidgetView(int paramInt, RemoteViews paramRemoteViews)
  {
    synchronized (this.mViews)
    {
      AppWidgetHostView localAppWidgetHostView = (AppWidgetHostView)this.mViews.get(paramInt);
      if (localAppWidgetHostView != null) {
        localAppWidgetHostView.updateAppWidget(paramRemoteViews);
      }
      return;
    }
  }
  
  void viewDataChanged(int paramInt1, int paramInt2)
  {
    synchronized (this.mViews)
    {
      AppWidgetHostView localAppWidgetHostView = (AppWidgetHostView)this.mViews.get(paramInt1);
      if (localAppWidgetHostView != null) {
        localAppWidgetHostView.viewDataChanged(paramInt2);
      }
      return;
    }
  }
  
  static class Callbacks
    extends IAppWidgetHost.Stub
  {
    private final WeakReference<Handler> mWeakHandler;
    
    public Callbacks(Handler paramHandler)
    {
      this.mWeakHandler = new WeakReference(paramHandler);
    }
    
    private static boolean isLocalBinder()
    {
      return Process.myPid() == Binder.getCallingPid();
    }
    
    public void providerChanged(int paramInt, AppWidgetProviderInfo paramAppWidgetProviderInfo)
    {
      AppWidgetProviderInfo localAppWidgetProviderInfo = paramAppWidgetProviderInfo;
      if (isLocalBinder())
      {
        localAppWidgetProviderInfo = paramAppWidgetProviderInfo;
        if (paramAppWidgetProviderInfo != null) {
          localAppWidgetProviderInfo = paramAppWidgetProviderInfo.clone();
        }
      }
      paramAppWidgetProviderInfo = (Handler)this.mWeakHandler.get();
      if (paramAppWidgetProviderInfo == null) {
        return;
      }
      paramAppWidgetProviderInfo.obtainMessage(2, paramInt, 0, localAppWidgetProviderInfo).sendToTarget();
    }
    
    public void providersChanged()
    {
      Handler localHandler = (Handler)this.mWeakHandler.get();
      if (localHandler == null) {
        return;
      }
      localHandler.obtainMessage(3).sendToTarget();
    }
    
    public void updateAppWidget(int paramInt, RemoteViews paramRemoteViews)
    {
      RemoteViews localRemoteViews = paramRemoteViews;
      if (isLocalBinder())
      {
        localRemoteViews = paramRemoteViews;
        if (paramRemoteViews != null) {
          localRemoteViews = paramRemoteViews.clone();
        }
      }
      paramRemoteViews = (Handler)this.mWeakHandler.get();
      if (paramRemoteViews == null) {
        return;
      }
      paramRemoteViews.obtainMessage(1, paramInt, 0, localRemoteViews).sendToTarget();
    }
    
    public void viewDataChanged(int paramInt1, int paramInt2)
    {
      Handler localHandler = (Handler)this.mWeakHandler.get();
      if (localHandler == null) {
        return;
      }
      localHandler.obtainMessage(4, paramInt1, paramInt2).sendToTarget();
    }
  }
  
  class UpdateHandler
    extends Handler
  {
    public UpdateHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        AppWidgetHost.this.updateAppWidgetView(paramMessage.arg1, (RemoteViews)paramMessage.obj);
        return;
      case 2: 
        AppWidgetHost.this.onProviderChanged(paramMessage.arg1, (AppWidgetProviderInfo)paramMessage.obj);
        return;
      case 3: 
        AppWidgetHost.this.onProvidersChanged();
        return;
      }
      AppWidgetHost.this.viewDataChanged(paramMessage.arg1, paramMessage.arg2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/appwidget/AppWidgetHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */