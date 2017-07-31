package com.android.server.notification;

import com.android.internal.statusbar.NotificationVisibility;

public abstract interface NotificationDelegate
{
  public abstract void clearEffects();
  
  public abstract void onClearAll(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void onNotificationActionClick(int paramInt1, int paramInt2, String paramString, int paramInt3);
  
  public abstract void onNotificationClear(int paramInt1, int paramInt2, String paramString1, String paramString2, int paramInt3, int paramInt4);
  
  public abstract void onNotificationClick(int paramInt1, int paramInt2, String paramString);
  
  public abstract void onNotificationError(int paramInt1, int paramInt2, String paramString1, String paramString2, int paramInt3, int paramInt4, int paramInt5, String paramString3, int paramInt6);
  
  public abstract void onNotificationExpansionChanged(String paramString, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void onNotificationVisibilityChanged(NotificationVisibility[] paramArrayOfNotificationVisibility1, NotificationVisibility[] paramArrayOfNotificationVisibility2);
  
  public abstract void onPanelHidden();
  
  public abstract void onPanelRevealed(boolean paramBoolean, int paramInt);
  
  public abstract void onSetDisabled(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/NotificationDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */