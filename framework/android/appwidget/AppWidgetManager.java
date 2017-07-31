package android.appwidget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;
import com.android.internal.appwidget.IAppWidgetService;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AppWidgetManager
{
  public static final String ACTION_APPWIDGET_BIND = "android.appwidget.action.APPWIDGET_BIND";
  public static final String ACTION_APPWIDGET_CONFIGURE = "android.appwidget.action.APPWIDGET_CONFIGURE";
  public static final String ACTION_APPWIDGET_DELETED = "android.appwidget.action.APPWIDGET_DELETED";
  public static final String ACTION_APPWIDGET_DISABLED = "android.appwidget.action.APPWIDGET_DISABLED";
  public static final String ACTION_APPWIDGET_ENABLED = "android.appwidget.action.APPWIDGET_ENABLED";
  public static final String ACTION_APPWIDGET_HOST_RESTORED = "android.appwidget.action.APPWIDGET_HOST_RESTORED";
  public static final String ACTION_APPWIDGET_OPTIONS_CHANGED = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS";
  public static final String ACTION_APPWIDGET_PICK = "android.appwidget.action.APPWIDGET_PICK";
  public static final String ACTION_APPWIDGET_RESTORED = "android.appwidget.action.APPWIDGET_RESTORED";
  public static final String ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
  public static final String ACTION_KEYGUARD_APPWIDGET_PICK = "android.appwidget.action.KEYGUARD_APPWIDGET_PICK";
  public static final String EXTRA_APPWIDGET_ID = "appWidgetId";
  public static final String EXTRA_APPWIDGET_IDS = "appWidgetIds";
  public static final String EXTRA_APPWIDGET_OLD_IDS = "appWidgetOldIds";
  public static final String EXTRA_APPWIDGET_OPTIONS = "appWidgetOptions";
  public static final String EXTRA_APPWIDGET_PROVIDER = "appWidgetProvider";
  public static final String EXTRA_APPWIDGET_PROVIDER_PROFILE = "appWidgetProviderProfile";
  public static final String EXTRA_CATEGORY_FILTER = "categoryFilter";
  public static final String EXTRA_CUSTOM_EXTRAS = "customExtras";
  public static final String EXTRA_CUSTOM_INFO = "customInfo";
  public static final String EXTRA_CUSTOM_SORT = "customSort";
  public static final String EXTRA_HOST_ID = "hostId";
  public static final int INVALID_APPWIDGET_ID = 0;
  public static final String META_DATA_APPWIDGET_PROVIDER = "android.appwidget.provider";
  public static final String OPTION_APPWIDGET_HOST_CATEGORY = "appWidgetCategory";
  public static final String OPTION_APPWIDGET_MAX_HEIGHT = "appWidgetMaxHeight";
  public static final String OPTION_APPWIDGET_MAX_WIDTH = "appWidgetMaxWidth";
  public static final String OPTION_APPWIDGET_MIN_HEIGHT = "appWidgetMinHeight";
  public static final String OPTION_APPWIDGET_MIN_WIDTH = "appWidgetMinWidth";
  private final DisplayMetrics mDisplayMetrics;
  private final String mPackageName;
  private final IAppWidgetService mService;
  
  public AppWidgetManager(Context paramContext, IAppWidgetService paramIAppWidgetService)
  {
    this.mPackageName = paramContext.getOpPackageName();
    this.mService = paramIAppWidgetService;
    this.mDisplayMetrics = paramContext.getResources().getDisplayMetrics();
  }
  
  private boolean bindAppWidgetIdIfAllowed(int paramInt1, int paramInt2, ComponentName paramComponentName, Bundle paramBundle)
  {
    if (this.mService == null) {
      return false;
    }
    try
    {
      boolean bool = this.mService.bindAppWidgetId(this.mPackageName, paramInt1, paramInt2, paramComponentName, paramBundle);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  private void convertSizesToPixels(AppWidgetProviderInfo paramAppWidgetProviderInfo)
  {
    paramAppWidgetProviderInfo.minWidth = TypedValue.complexToDimensionPixelSize(paramAppWidgetProviderInfo.minWidth, this.mDisplayMetrics);
    paramAppWidgetProviderInfo.minHeight = TypedValue.complexToDimensionPixelSize(paramAppWidgetProviderInfo.minHeight, this.mDisplayMetrics);
    paramAppWidgetProviderInfo.minResizeWidth = TypedValue.complexToDimensionPixelSize(paramAppWidgetProviderInfo.minResizeWidth, this.mDisplayMetrics);
    paramAppWidgetProviderInfo.minResizeHeight = TypedValue.complexToDimensionPixelSize(paramAppWidgetProviderInfo.minResizeHeight, this.mDisplayMetrics);
  }
  
  public static AppWidgetManager getInstance(Context paramContext)
  {
    return (AppWidgetManager)paramContext.getSystemService("appwidget");
  }
  
  public void bindAppWidgetId(int paramInt, ComponentName paramComponentName)
  {
    if (this.mService == null) {
      return;
    }
    bindAppWidgetId(paramInt, paramComponentName, null);
  }
  
  public void bindAppWidgetId(int paramInt, ComponentName paramComponentName, Bundle paramBundle)
  {
    if (this.mService == null) {
      return;
    }
    bindAppWidgetIdIfAllowed(paramInt, Process.myUserHandle(), paramComponentName, paramBundle);
  }
  
  public boolean bindAppWidgetIdIfAllowed(int paramInt, ComponentName paramComponentName)
  {
    if (this.mService == null) {
      return false;
    }
    return bindAppWidgetIdIfAllowed(paramInt, UserHandle.myUserId(), paramComponentName, null);
  }
  
  public boolean bindAppWidgetIdIfAllowed(int paramInt, ComponentName paramComponentName, Bundle paramBundle)
  {
    if (this.mService == null) {
      return false;
    }
    return bindAppWidgetIdIfAllowed(paramInt, UserHandle.myUserId(), paramComponentName, paramBundle);
  }
  
  public boolean bindAppWidgetIdIfAllowed(int paramInt, UserHandle paramUserHandle, ComponentName paramComponentName, Bundle paramBundle)
  {
    if (this.mService == null) {
      return false;
    }
    return bindAppWidgetIdIfAllowed(paramInt, paramUserHandle.getIdentifier(), paramComponentName, paramBundle);
  }
  
  public void bindRemoteViewsService(String paramString, int paramInt, Intent paramIntent, IBinder paramIBinder)
  {
    if (this.mService == null) {
      return;
    }
    try
    {
      this.mService.bindRemoteViewsService(paramString, paramInt, paramIntent, paramIBinder);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int[] getAppWidgetIds(ComponentName paramComponentName)
  {
    if (this.mService == null) {
      return new int[0];
    }
    try
    {
      paramComponentName = this.mService.getAppWidgetIds(paramComponentName);
      return paramComponentName;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public AppWidgetProviderInfo getAppWidgetInfo(int paramInt)
  {
    if (this.mService == null) {
      return null;
    }
    try
    {
      AppWidgetProviderInfo localAppWidgetProviderInfo = this.mService.getAppWidgetInfo(this.mPackageName, paramInt);
      if (localAppWidgetProviderInfo != null) {
        convertSizesToPixels(localAppWidgetProviderInfo);
      }
      return localAppWidgetProviderInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Bundle getAppWidgetOptions(int paramInt)
  {
    if (this.mService == null) {
      return Bundle.EMPTY;
    }
    try
    {
      Bundle localBundle = this.mService.getAppWidgetOptions(this.mPackageName, paramInt);
      return localBundle;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<AppWidgetProviderInfo> getInstalledProviders()
  {
    if (this.mService == null) {
      return Collections.emptyList();
    }
    return getInstalledProvidersForProfile(1, null);
  }
  
  public List<AppWidgetProviderInfo> getInstalledProviders(int paramInt)
  {
    if (this.mService == null) {
      return Collections.emptyList();
    }
    return getInstalledProvidersForProfile(paramInt, null);
  }
  
  public List<AppWidgetProviderInfo> getInstalledProvidersForProfile(int paramInt, UserHandle paramUserHandle)
  {
    if (this.mService == null) {
      return Collections.emptyList();
    }
    Object localObject = paramUserHandle;
    if (paramUserHandle == null) {
      localObject = Process.myUserHandle();
    }
    try
    {
      paramUserHandle = this.mService.getInstalledProvidersForProfile(paramInt, ((UserHandle)localObject).getIdentifier());
      if (paramUserHandle == null) {
        return Collections.emptyList();
      }
      localObject = paramUserHandle.getList().iterator();
      while (((Iterator)localObject).hasNext()) {
        convertSizesToPixels((AppWidgetProviderInfo)((Iterator)localObject).next());
      }
      paramUserHandle = paramUserHandle.getList();
    }
    catch (RemoteException paramUserHandle)
    {
      throw paramUserHandle.rethrowFromSystemServer();
    }
    return paramUserHandle;
  }
  
  public List<AppWidgetProviderInfo> getInstalledProvidersForProfile(UserHandle paramUserHandle)
  {
    if (this.mService == null) {
      return Collections.emptyList();
    }
    return getInstalledProvidersForProfile(1, paramUserHandle);
  }
  
  public boolean hasBindAppWidgetPermission(String paramString)
  {
    if (this.mService == null) {
      return false;
    }
    try
    {
      boolean bool = this.mService.hasBindAppWidgetPermission(paramString, UserHandle.myUserId());
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean hasBindAppWidgetPermission(String paramString, int paramInt)
  {
    if (this.mService == null) {
      return false;
    }
    try
    {
      boolean bool = this.mService.hasBindAppWidgetPermission(paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean isBoundWidgetPackage(String paramString, int paramInt)
  {
    if (this.mService == null) {
      return false;
    }
    try
    {
      boolean bool = this.mService.isBoundWidgetPackage(paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void notifyAppWidgetViewDataChanged(int paramInt1, int paramInt2)
  {
    if (this.mService == null) {
      return;
    }
    notifyAppWidgetViewDataChanged(new int[] { paramInt1 }, paramInt2);
  }
  
  public void notifyAppWidgetViewDataChanged(int[] paramArrayOfInt, int paramInt)
  {
    if (this.mService == null) {
      return;
    }
    try
    {
      this.mService.notifyAppWidgetViewDataChanged(this.mPackageName, paramArrayOfInt, paramInt);
      return;
    }
    catch (RemoteException paramArrayOfInt)
    {
      throw paramArrayOfInt.rethrowFromSystemServer();
    }
  }
  
  public void partiallyUpdateAppWidget(int paramInt, RemoteViews paramRemoteViews)
  {
    if (this.mService == null) {
      return;
    }
    partiallyUpdateAppWidget(new int[] { paramInt }, paramRemoteViews);
  }
  
  public void partiallyUpdateAppWidget(int[] paramArrayOfInt, RemoteViews paramRemoteViews)
  {
    if (this.mService == null) {
      return;
    }
    try
    {
      this.mService.partiallyUpdateAppWidgetIds(this.mPackageName, paramArrayOfInt, paramRemoteViews);
      return;
    }
    catch (RemoteException paramArrayOfInt)
    {
      throw paramArrayOfInt.rethrowFromSystemServer();
    }
  }
  
  public void setBindAppWidgetPermission(String paramString, int paramInt, boolean paramBoolean)
  {
    if (this.mService == null) {
      return;
    }
    try
    {
      this.mService.setBindAppWidgetPermission(paramString, paramInt, paramBoolean);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setBindAppWidgetPermission(String paramString, boolean paramBoolean)
  {
    if (this.mService == null) {
      return;
    }
    setBindAppWidgetPermission(paramString, UserHandle.myUserId(), paramBoolean);
  }
  
  public void unbindRemoteViewsService(String paramString, int paramInt, Intent paramIntent)
  {
    if (this.mService == null) {
      return;
    }
    try
    {
      this.mService.unbindRemoteViewsService(paramString, paramInt, paramIntent);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void updateAppWidget(int paramInt, RemoteViews paramRemoteViews)
  {
    if (this.mService == null) {
      return;
    }
    updateAppWidget(new int[] { paramInt }, paramRemoteViews);
  }
  
  public void updateAppWidget(ComponentName paramComponentName, RemoteViews paramRemoteViews)
  {
    if (this.mService == null) {
      return;
    }
    try
    {
      this.mService.updateAppWidgetProvider(paramComponentName, paramRemoteViews);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void updateAppWidget(int[] paramArrayOfInt, RemoteViews paramRemoteViews)
  {
    if (this.mService == null) {
      return;
    }
    try
    {
      this.mService.updateAppWidgetIds(this.mPackageName, paramArrayOfInt, paramRemoteViews);
      return;
    }
    catch (RemoteException paramArrayOfInt)
    {
      throw paramArrayOfInt.rethrowFromSystemServer();
    }
  }
  
  public void updateAppWidgetOptions(int paramInt, Bundle paramBundle)
  {
    if (this.mService == null) {
      return;
    }
    try
    {
      this.mService.updateAppWidgetOptions(this.mPackageName, paramInt, paramBundle);
      return;
    }
    catch (RemoteException paramBundle)
    {
      throw paramBundle.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/appwidget/AppWidgetManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */