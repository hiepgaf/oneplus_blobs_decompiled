package com.android.server.statusbar;

import android.graphics.Rect;
import android.os.Bundle;
import com.android.server.notification.NotificationDelegate;

public abstract interface StatusBarManagerInternal
{
  public abstract void appTransitionCancelled();
  
  public abstract void appTransitionFinished();
  
  public abstract void appTransitionPending();
  
  public abstract void appTransitionStarting(long paramLong1, long paramLong2);
  
  public abstract void buzzBeepBlinked();
  
  public abstract void cancelPreloadRecentApps();
  
  public abstract void dismissKeyboardShortcutsMenu();
  
  public abstract void hideRecentApps(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void notificationLightOff();
  
  public abstract void notificationLightPulse(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void onCameraLaunchGestureDetected(int paramInt);
  
  public abstract void preloadRecentApps();
  
  public abstract void setCurrentUser(int paramInt);
  
  public abstract void setNotificationDelegate(NotificationDelegate paramNotificationDelegate);
  
  public abstract void setSystemUiVisibility(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rect paramRect1, Rect paramRect2, String paramString);
  
  public abstract void setWindowState(int paramInt1, int paramInt2);
  
  public abstract void showAssistDisclosure();
  
  public abstract void showRecentApps(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void showScreenPinningRequest(int paramInt);
  
  public abstract void showTvPictureInPictureMenu();
  
  public abstract void startAssist(Bundle paramBundle);
  
  public abstract void toggleKeyboardShortcutsMenu(int paramInt);
  
  public abstract void toggleRecentApps();
  
  public abstract void toggleSplitScreen();
  
  public abstract void topAppWindowChanged(boolean paramBoolean);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/statusbar/StatusBarManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */