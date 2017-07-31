package com.android.server.appwidget;

import android.content.Context;
import com.android.server.AppWidgetBackupBridge;
import com.android.server.SystemService;

public class AppWidgetService
  extends SystemService
{
  private final AppWidgetServiceImpl mImpl;
  
  public AppWidgetService(Context paramContext)
  {
    super(paramContext);
    this.mImpl = new AppWidgetServiceImpl(paramContext);
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 550) {
      this.mImpl.setSafeMode(isSafeMode());
    }
  }
  
  public void onStart()
  {
    publishBinderService("appwidget", this.mImpl);
    AppWidgetBackupBridge.register(this.mImpl);
  }
  
  public void onStopUser(int paramInt)
  {
    this.mImpl.onUserStopped(paramInt);
  }
  
  public void onSwitchUser(int paramInt)
  {
    this.mImpl.reloadWidgetsMaskedStateForGroup(paramInt);
  }
  
  public void onUnlockUser(int paramInt)
  {
    this.mImpl.onUserUnlocked(paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/appwidget/AppWidgetService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */