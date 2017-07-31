package com.android.server.statusbar;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.IStatusBarService.Stub;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.server.LocalServices;
import com.android.server.notification.NotificationDelegate;
import com.android.server.wm.WindowManagerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatusBarManagerService
  extends IStatusBarService.Stub
{
  private static final boolean SPEW = false;
  private static final String TAG = "StatusBarManagerService";
  private volatile IStatusBar mBar;
  private final Context mContext;
  private int mCurrentUserId;
  private final ArrayList<DisableRecord> mDisableRecords = new ArrayList();
  private int mDisabled1 = 0;
  private int mDisabled2 = 0;
  private final Rect mDockedStackBounds = new Rect();
  private int mDockedStackSysUiVisibility;
  private final Rect mFullscreenStackBounds = new Rect();
  private int mFullscreenStackSysUiVisibility;
  private Handler mHandler = new Handler();
  private ArrayMap<String, StatusBarIcon> mIcons = new ArrayMap();
  private int mImeBackDisposition;
  private IBinder mImeToken = null;
  private int mImeWindowVis = 0;
  private final StatusBarManagerInternal mInternalService = new StatusBarManagerInternal()
  {
    private boolean mNotificationLightOn;
    
    public void appTransitionCancelled()
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).appTransitionCancelled();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void appTransitionFinished()
    {
      StatusBarManagerService.-wrap0(StatusBarManagerService.this);
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).appTransitionFinished();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void appTransitionPending()
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).appTransitionPending();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void appTransitionStarting(long paramAnonymousLong1, long paramAnonymousLong2)
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).appTransitionStarting(paramAnonymousLong1, paramAnonymousLong2);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void buzzBeepBlinked()
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).buzzBeepBlinked();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void cancelPreloadRecentApps()
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).cancelPreloadRecentApps();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void dismissKeyboardShortcutsMenu()
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).dismissKeyboardShortcutsMenu();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void hideRecentApps(boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).hideRecentApps(paramAnonymousBoolean1, paramAnonymousBoolean2);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void notificationLightOff()
    {
      if (this.mNotificationLightOn)
      {
        this.mNotificationLightOn = false;
        if (StatusBarManagerService.-get0(StatusBarManagerService.this) == null) {}
      }
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).notificationLightOff();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void notificationLightPulse(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      this.mNotificationLightOn = true;
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).notificationLightPulse(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void onCameraLaunchGestureDetected(int paramAnonymousInt)
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).onCameraLaunchGestureDetected(paramAnonymousInt);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void preloadRecentApps()
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).preloadRecentApps();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void setCurrentUser(int paramAnonymousInt)
    {
      StatusBarManagerService.-set0(StatusBarManagerService.this, paramAnonymousInt);
    }
    
    public void setNotificationDelegate(NotificationDelegate paramAnonymousNotificationDelegate)
    {
      StatusBarManagerService.-set1(StatusBarManagerService.this, paramAnonymousNotificationDelegate);
    }
    
    public void setSystemUiVisibility(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, Rect paramAnonymousRect1, Rect paramAnonymousRect2, String paramAnonymousString)
    {
      StatusBarManagerService.-wrap1(StatusBarManagerService.this, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4, paramAnonymousRect1, paramAnonymousRect2, paramAnonymousString);
    }
    
    public void setWindowState(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).setWindowState(paramAnonymousInt1, paramAnonymousInt2);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void showAssistDisclosure()
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).showAssistDisclosure();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void showRecentApps(boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).showRecentApps(paramAnonymousBoolean1, paramAnonymousBoolean2);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void showScreenPinningRequest(int paramAnonymousInt)
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).showScreenPinningRequest(paramAnonymousInt);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void showTvPictureInPictureMenu()
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).showTvPictureInPictureMenu();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void startAssist(Bundle paramAnonymousBundle)
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).startAssist(paramAnonymousBundle);
        return;
      }
      catch (RemoteException paramAnonymousBundle) {}
    }
    
    public void toggleKeyboardShortcutsMenu(int paramAnonymousInt)
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).toggleKeyboardShortcutsMenu(paramAnonymousInt);
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void toggleRecentApps()
    {
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).toggleRecentApps();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void toggleSplitScreen()
    {
      StatusBarManagerService.-wrap0(StatusBarManagerService.this);
      if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
      try
      {
        StatusBarManagerService.-get0(StatusBarManagerService.this).toggleSplitScreen();
        return;
      }
      catch (RemoteException localRemoteException) {}
    }
    
    public void topAppWindowChanged(boolean paramAnonymousBoolean)
    {
      StatusBarManagerService.-wrap2(StatusBarManagerService.this, paramAnonymousBoolean);
    }
  };
  private final Object mLock = new Object();
  private boolean mMenuVisible = false;
  private NotificationDelegate mNotificationDelegate;
  private boolean mShowImeSwitcher;
  private IBinder mSysUiVisToken = new Binder();
  private int mSystemUiVisibility = 0;
  private final WindowManagerService mWindowManager;
  
  public StatusBarManagerService(Context paramContext, WindowManagerService paramWindowManagerService)
  {
    this.mContext = paramContext;
    this.mWindowManager = paramWindowManagerService;
    LocalServices.addService(StatusBarManagerInternal.class, this.mInternalService);
  }
  
  private void disableLocked(final int paramInt1, int paramInt2, IBinder paramIBinder, String paramString, int paramInt3)
  {
    manageDisableListLocked(paramInt1, paramInt2, paramIBinder, paramString, paramInt3);
    paramInt1 = gatherDisableActionsLocked(this.mCurrentUserId, 1);
    paramInt3 = gatherDisableActionsLocked(this.mCurrentUserId, 2);
    if ((paramInt1 != this.mDisabled1) || (paramInt3 != this.mDisabled2))
    {
      this.mDisabled1 = paramInt1;
      this.mDisabled2 = paramInt3;
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          StatusBarManagerService.-get1(StatusBarManagerService.this).onSetDisabled(paramInt1);
        }
      });
      if (this.mBar == null) {}
    }
    try
    {
      Log.i("StatusBarManagerService", "pkg = " + paramString + ", mDisabled1 = " + Integer.toHexString(this.mDisabled1) + ", mDisabled2 =" + Integer.toHexString(this.mDisabled2) + ",what =" + paramInt2);
      this.mBar.disable(paramInt1, paramInt3);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      Log.w("StatusBarManagerService", "error:", paramIBinder);
    }
  }
  
  private void enforceExpandStatusBar()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.EXPAND_STATUS_BAR", "StatusBarManagerService");
  }
  
  private void enforceStatusBar()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR", "StatusBarManagerService");
  }
  
  private void enforceStatusBarOrShell()
  {
    if (Binder.getCallingUid() == 2000) {
      return;
    }
    enforceStatusBar();
  }
  
  private void enforceStatusBarService()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "StatusBarManagerService");
  }
  
  private void setSystemUiVisibility(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rect paramRect1, Rect paramRect2, String paramString)
  {
    enforceStatusBarService();
    synchronized (this.mLock)
    {
      updateUiVisibilityLocked(paramInt1, paramInt2, paramInt3, paramInt4, paramRect1, paramRect2);
      disableLocked(this.mCurrentUserId, paramInt1 & 0x3FF0000, this.mSysUiVisToken, paramString, 1);
      return;
    }
  }
  
  private void topAppWindowChanged(final boolean paramBoolean)
  {
    enforceStatusBar();
    synchronized (this.mLock)
    {
      this.mMenuVisible = paramBoolean;
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
          try
          {
            StatusBarManagerService.-get0(StatusBarManagerService.this).topAppWindowChanged(paramBoolean);
            return;
          }
          catch (RemoteException localRemoteException) {}
        }
      });
      return;
    }
  }
  
  private void updateUiVisibilityLocked(final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4, final Rect paramRect1, final Rect paramRect2)
  {
    if ((this.mSystemUiVisibility != paramInt1) || (this.mFullscreenStackSysUiVisibility != paramInt2)) {}
    while ((this.mDockedStackSysUiVisibility != paramInt3) || (!this.mFullscreenStackBounds.equals(paramRect1)) || (!this.mDockedStackBounds.equals(paramRect2)))
    {
      this.mSystemUiVisibility = paramInt1;
      this.mFullscreenStackSysUiVisibility = paramInt2;
      this.mDockedStackSysUiVisibility = paramInt3;
      this.mFullscreenStackBounds.set(paramRect1);
      this.mDockedStackBounds.set(paramRect2);
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
          try
          {
            StatusBarManagerService.-get0(StatusBarManagerService.this).setSystemUiVisibility(paramInt1, paramInt2, paramInt3, paramInt4, paramRect1, paramRect2);
            return;
          }
          catch (RemoteException localRemoteException) {}
        }
      });
      return;
    }
  }
  
  public void addTile(ComponentName paramComponentName)
  {
    enforceStatusBarOrShell();
    if (this.mBar != null) {}
    try
    {
      this.mBar.addQsTile(paramComponentName);
      return;
    }
    catch (RemoteException paramComponentName) {}
  }
  
  public void clearNotificationEffects()
    throws RemoteException
  {
    enforceStatusBarService();
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mNotificationDelegate.clearEffects();
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void clickTile(ComponentName paramComponentName)
  {
    enforceStatusBarOrShell();
    if (this.mBar != null) {}
    try
    {
      this.mBar.clickQsTile(paramComponentName);
      return;
    }
    catch (RemoteException paramComponentName) {}
  }
  
  public void collapsePanels()
  {
    enforceExpandStatusBar();
    if (this.mBar != null) {}
    try
    {
      this.mBar.animateCollapsePanels();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void disable(int paramInt, IBinder paramIBinder, String paramString)
  {
    disableForUser(paramInt, paramIBinder, paramString, this.mCurrentUserId);
  }
  
  public void disable2(int paramInt, IBinder paramIBinder, String paramString)
  {
    disable2ForUser(paramInt, paramIBinder, paramString, this.mCurrentUserId);
  }
  
  public void disable2ForUser(int paramInt1, IBinder paramIBinder, String paramString, int paramInt2)
  {
    enforceStatusBar();
    synchronized (this.mLock)
    {
      disableLocked(paramInt2, paramInt1, paramIBinder, paramString, 2);
      return;
    }
  }
  
  public void disableForUser(int paramInt1, IBinder paramIBinder, String paramString, int paramInt2)
  {
    enforceStatusBar();
    synchronized (this.mLock)
    {
      disableLocked(paramInt2, paramInt1, paramIBinder, paramString, 1);
      return;
    }
  }
  
  protected void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump StatusBar from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    synchronized (this.mLock)
    {
      paramPrintWriter.println("  mDisabled1=0x" + Integer.toHexString(this.mDisabled1));
      paramPrintWriter.println("  mDisabled2=0x" + Integer.toHexString(this.mDisabled2));
      int j = this.mDisableRecords.size();
      paramPrintWriter.println("  mDisableRecords.size=" + j);
      int i = 0;
      while (i < j)
      {
        paramArrayOfString = (DisableRecord)this.mDisableRecords.get(i);
        paramPrintWriter.println("    [" + i + "] userId=" + paramArrayOfString.userId + " what1=0x" + Integer.toHexString(paramArrayOfString.what1) + " what2=0x" + Integer.toHexString(paramArrayOfString.what2) + " pkg=" + paramArrayOfString.pkg + " token=" + paramArrayOfString.token);
        i += 1;
      }
      paramPrintWriter.println("  mCurrentUserId=" + this.mCurrentUserId);
      paramPrintWriter.println("  mIcons=");
      paramArrayOfString = this.mIcons.keySet().iterator();
      if (paramArrayOfString.hasNext())
      {
        Object localObject = (String)paramArrayOfString.next();
        paramPrintWriter.println("    ");
        paramPrintWriter.print((String)localObject);
        paramPrintWriter.print(" -> ");
        localObject = (StatusBarIcon)this.mIcons.get(localObject);
        paramPrintWriter.print(localObject);
        if (!TextUtils.isEmpty(((StatusBarIcon)localObject).contentDescription))
        {
          paramPrintWriter.print(" \"");
          paramPrintWriter.print(((StatusBarIcon)localObject).contentDescription);
          paramPrintWriter.print("\"");
        }
        paramPrintWriter.println();
      }
    }
  }
  
  public void expandNotificationsPanel(int paramInt)
  {
    enforceExpandStatusBar();
    if (this.mBar != null) {}
    try
    {
      this.mBar.animateExpandNotificationsPanel(paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void expandSettingsPanel(String paramString)
  {
    enforceExpandStatusBar();
    if (this.mBar != null) {}
    try
    {
      this.mBar.animateExpandSettingsPanel(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  int gatherDisableActionsLocked(int paramInt1, int paramInt2)
  {
    int m = this.mDisableRecords.size();
    int j = 0;
    int i = 0;
    if (i < m)
    {
      DisableRecord localDisableRecord = (DisableRecord)this.mDisableRecords.get(i);
      int k = j;
      if (localDisableRecord.userId == paramInt1) {
        if (paramInt2 != 1) {
          break label76;
        }
      }
      label76:
      for (k = localDisableRecord.what1;; k = localDisableRecord.what2)
      {
        k = j | k;
        i += 1;
        j = k;
        break;
      }
    }
    return j;
  }
  
  public List<String> getLockedPackageList()
  {
    if (this.mBar != null) {
      try
      {
        List localList = this.mBar.getLockedPackageList();
        return localList;
      }
      catch (RemoteException localRemoteException) {}
    }
    return null;
  }
  
  public void handleSystemNavigationKey(int paramInt)
    throws RemoteException
  {
    enforceExpandStatusBar();
    if (this.mBar != null) {}
    try
    {
      this.mBar.handleSystemNavigationKey(paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  void manageDisableListLocked(int paramInt1, int paramInt2, IBinder paramIBinder, String paramString, int paramInt3)
  {
    if (Build.DEBUG_ONEPLUS) {
      Slog.d("StatusBarManagerService", "manageDisableList userId=" + paramInt1 + " what=0x" + Integer.toHexString(paramInt2) + " pkg=" + paramString + " token=" + paramIBinder + " which=" + paramInt3);
    }
    int j = this.mDisableRecords.size();
    Object localObject2 = null;
    int i = 0;
    Object localObject1 = localObject2;
    if (i < j)
    {
      localObject1 = (DisableRecord)this.mDisableRecords.get(i);
      if ((((DisableRecord)localObject1).token != paramIBinder) || (((DisableRecord)localObject1).userId != paramInt1)) {}
    }
    else
    {
      if ((paramInt2 == 0) || (!paramIBinder.isBinderAlive())) {
        break label228;
      }
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = new DisableRecord(null);
        ((DisableRecord)localObject2).userId = paramInt1;
      }
    }
    for (;;)
    {
      try
      {
        paramIBinder.linkToDeath((IBinder.DeathRecipient)localObject2, 0);
        this.mDisableRecords.add(localObject2);
        if (paramInt3 != 1) {
          break label260;
        }
        ((DisableRecord)localObject2).what1 = paramInt2;
        ((DisableRecord)localObject2).token = paramIBinder;
        ((DisableRecord)localObject2).pkg = paramString;
        return;
      }
      catch (RemoteException paramIBinder)
      {
        label228:
        return;
      }
      i += 1;
      break;
      if (localObject1 != null)
      {
        this.mDisableRecords.remove(i);
        ((DisableRecord)localObject1).token.unlinkToDeath((IBinder.DeathRecipient)localObject1, 0);
        return;
        label260:
        ((DisableRecord)localObject2).what2 = paramInt2;
      }
    }
  }
  
  public void onClearAllNotifications(int paramInt)
  {
    enforceStatusBarService();
    int i = Binder.getCallingUid();
    int j = Binder.getCallingPid();
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mNotificationDelegate.onClearAll(i, j, paramInt);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void onNotificationActionClick(String paramString, int paramInt)
  {
    enforceStatusBarService();
    int i = Binder.getCallingUid();
    int j = Binder.getCallingPid();
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mNotificationDelegate.onNotificationActionClick(i, j, paramString, paramInt);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void onNotificationClear(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    enforceStatusBarService();
    int i = Binder.getCallingUid();
    int j = Binder.getCallingPid();
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mNotificationDelegate.onNotificationClear(i, j, paramString1, paramString2, paramInt1, paramInt2);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void onNotificationClick(String paramString)
  {
    enforceStatusBarService();
    int i = Binder.getCallingUid();
    int j = Binder.getCallingPid();
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mNotificationDelegate.onNotificationClick(i, j, paramString);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void onNotificationError(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, String paramString3, int paramInt4)
  {
    enforceStatusBarService();
    int i = Binder.getCallingUid();
    int j = Binder.getCallingPid();
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mNotificationDelegate.onNotificationError(i, j, paramString1, paramString2, paramInt1, paramInt2, paramInt3, paramString3, paramInt4);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void onNotificationExpansionChanged(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException
  {
    enforceStatusBarService();
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mNotificationDelegate.onNotificationExpansionChanged(paramString, paramBoolean1, paramBoolean2);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void onNotificationVisibilityChanged(NotificationVisibility[] paramArrayOfNotificationVisibility1, NotificationVisibility[] paramArrayOfNotificationVisibility2)
    throws RemoteException
  {
    enforceStatusBarService();
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mNotificationDelegate.onNotificationVisibilityChanged(paramArrayOfNotificationVisibility1, paramArrayOfNotificationVisibility2);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void onPanelHidden()
    throws RemoteException
  {
    enforceStatusBarService();
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mNotificationDelegate.onPanelHidden();
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void onPanelRevealed(boolean paramBoolean, int paramInt)
  {
    enforceStatusBarService();
    long l = Binder.clearCallingIdentity();
    try
    {
      this.mNotificationDelegate.onPanelRevealed(paramBoolean, paramInt);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
    throws RemoteException
  {
    new StatusBarShellCommand(this).exec(this, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver);
  }
  
  public void registerStatusBar(IStatusBar arg1, List<String> paramList, List<StatusBarIcon> paramList1, int[] paramArrayOfInt, List<IBinder> paramList2, Rect paramRect1, Rect paramRect2)
  {
    enforceStatusBarService();
    Slog.i("StatusBarManagerService", "registerStatusBar bar=" + ???);
    this.mBar = ???;
    synchronized (this.mIcons)
    {
      Iterator localIterator = this.mIcons.keySet().iterator();
      if (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        paramList.add(str);
        paramList1.add((StatusBarIcon)this.mIcons.get(str));
      }
    }
    synchronized (this.mLock)
    {
      paramArrayOfInt[0] = gatherDisableActionsLocked(this.mCurrentUserId, 1);
      paramArrayOfInt[1] = this.mSystemUiVisibility;
      if (this.mMenuVisible) {}
      for (int i = 1;; i = 0)
      {
        paramArrayOfInt[2] = i;
        paramArrayOfInt[3] = this.mImeWindowVis;
        paramArrayOfInt[4] = this.mImeBackDisposition;
        if (!this.mShowImeSwitcher) {
          break;
        }
        i = 1;
        paramArrayOfInt[5] = i;
        paramArrayOfInt[6] = gatherDisableActionsLocked(this.mCurrentUserId, 2);
        paramArrayOfInt[7] = this.mFullscreenStackSysUiVisibility;
        paramArrayOfInt[8] = this.mDockedStackSysUiVisibility;
        paramList2.add(this.mImeToken);
        paramRect1.set(this.mFullscreenStackBounds);
        paramRect2.set(this.mDockedStackBounds);
        return;
      }
      i = 0;
    }
  }
  
  public void remTile(ComponentName paramComponentName)
  {
    enforceStatusBarOrShell();
    if (this.mBar != null) {}
    try
    {
      this.mBar.remQsTile(paramComponentName);
      return;
    }
    catch (RemoteException paramComponentName) {}
  }
  
  public void removeIcon(String paramString)
  {
    enforceStatusBar();
    synchronized (this.mIcons)
    {
      this.mIcons.remove(paramString);
      IStatusBar localIStatusBar = this.mBar;
      if (localIStatusBar == null) {}
    }
    try
    {
      this.mBar.removeIcon(paramString);
      return;
      paramString = finally;
      throw paramString;
    }
    catch (RemoteException paramString)
    {
      for (;;) {}
    }
  }
  
  public void setIcon(String paramString1, String paramString2, int paramInt1, int paramInt2, String paramString3)
  {
    enforceStatusBar();
    synchronized (this.mIcons)
    {
      paramString2 = new StatusBarIcon(paramString2, UserHandle.SYSTEM, paramInt1, paramInt2, 0, paramString3);
      this.mIcons.put(paramString1, paramString2);
      paramString3 = this.mBar;
      if (paramString3 == null) {}
    }
    try
    {
      this.mBar.setIcon(paramString1, paramString2);
      return;
      paramString1 = finally;
      throw paramString1;
    }
    catch (RemoteException paramString1)
    {
      for (;;) {}
    }
  }
  
  public void setIconVisibility(String paramString, boolean paramBoolean)
  {
    enforceStatusBar();
    StatusBarIcon localStatusBarIcon;
    synchronized (this.mIcons)
    {
      localStatusBarIcon = (StatusBarIcon)this.mIcons.get(paramString);
      if (localStatusBarIcon == null) {
        return;
      }
      if (localStatusBarIcon.visible != paramBoolean)
      {
        localStatusBarIcon.visible = paramBoolean;
        IStatusBar localIStatusBar = this.mBar;
        if (localIStatusBar == null) {}
      }
    }
    try
    {
      this.mBar.setIcon(paramString, localStatusBarIcon);
      return;
      paramString = finally;
      throw paramString;
    }
    catch (RemoteException paramString)
    {
      for (;;) {}
    }
  }
  
  public void setImeWindowStatus(final IBinder paramIBinder, final int paramInt1, final int paramInt2, final boolean paramBoolean)
  {
    enforceStatusBar();
    synchronized (this.mLock)
    {
      this.mImeWindowVis = paramInt1;
      this.mImeBackDisposition = paramInt2;
      this.mImeToken = paramIBinder;
      this.mShowImeSwitcher = paramBoolean;
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          if (StatusBarManagerService.-get0(StatusBarManagerService.this) != null) {}
          try
          {
            StatusBarManagerService.-get0(StatusBarManagerService.this).setImeWindowStatus(paramIBinder, paramInt1, paramInt2, paramBoolean);
            return;
          }
          catch (RemoteException localRemoteException) {}
        }
      });
      return;
    }
  }
  
  public void setSystemUiVisibility(int paramInt1, int paramInt2, String paramString)
  {
    setSystemUiVisibility(paramInt1, 0, 0, paramInt2, this.mFullscreenStackBounds, this.mDockedStackBounds, paramString);
  }
  
  private class DisableRecord
    implements IBinder.DeathRecipient
  {
    String pkg;
    IBinder token;
    int userId;
    int what1;
    int what2;
    
    private DisableRecord() {}
    
    public void binderDied()
    {
      Slog.i("StatusBarManagerService", "binder died for pkg=" + this.pkg);
      StatusBarManagerService.this.disableForUser(0, this.token, this.pkg, this.userId);
      StatusBarManagerService.this.disable2ForUser(0, this.token, this.pkg, this.userId);
      this.token.unlinkToDeath(this, 0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/statusbar/StatusBarManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */