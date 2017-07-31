package com.android.server.wm;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.Slog;
import android.view.DisplayInfo;
import android.view.IWindow;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerPolicy;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

class WallpaperController
{
  private static final String TAG = "WindowManager";
  private static final int WALLPAPER_DRAW_NORMAL = 0;
  private static final int WALLPAPER_DRAW_PENDING = 1;
  private static final long WALLPAPER_DRAW_PENDING_TIMEOUT_DURATION = 500L;
  private static final int WALLPAPER_DRAW_TIMEOUT = 2;
  private static final long WALLPAPER_TIMEOUT = 150L;
  private static final long WALLPAPER_TIMEOUT_RECOVERY = 10000L;
  private WindowState mDeferredHideWallpaper = null;
  private final FindWallpaperTargetResult mFindResults = new FindWallpaperTargetResult(null);
  private int mLastWallpaperDisplayOffsetX = Integer.MIN_VALUE;
  private int mLastWallpaperDisplayOffsetY = Integer.MIN_VALUE;
  private long mLastWallpaperTimeoutTime;
  private float mLastWallpaperX = -1.0F;
  private float mLastWallpaperXStep = -1.0F;
  private float mLastWallpaperY = -1.0F;
  private float mLastWallpaperYStep = -1.0F;
  private WindowState mLowerWallpaperTarget = null;
  private final WindowManagerService mService;
  private WindowState mUpperWallpaperTarget = null;
  WindowState mWaitingOnWallpaper;
  private int mWallpaperAnimLayerAdjustment;
  private int mWallpaperDrawState = 0;
  private WindowState mWallpaperTarget = null;
  private final ArrayList<WindowToken> mWallpaperTokens = new ArrayList();
  
  public WallpaperController(WindowManagerService paramWindowManagerService)
  {
    this.mService = paramWindowManagerService;
  }
  
  private int findLowestWindowOnScreen(WindowList paramWindowList)
  {
    int j = paramWindowList.size();
    int i = 0;
    while (i < j)
    {
      if (((WindowState)paramWindowList.get(i)).isOnScreen()) {
        return i;
      }
      i += 1;
    }
    return Integer.MAX_VALUE;
  }
  
  private void findWallpaperTarget(WindowList paramWindowList, FindWallpaperTargetResult paramFindWallpaperTargetResult)
  {
    WindowAnimator localWindowAnimator = this.mService.mAnimator;
    paramFindWallpaperTargetResult.reset();
    WindowState localWindowState = null;
    int j = -1;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool4 = false;
    boolean bool3 = false;
    int i = paramWindowList.size() - 1;
    boolean bool5 = bool2;
    boolean bool6 = bool3;
    boolean bool7 = bool4;
    Object localObject = localWindowState;
    if (i >= 0)
    {
      localWindowState = (WindowState)paramWindowList.get(i);
      boolean bool8;
      int k;
      if (localWindowState.mAttrs.type == 2013) {
        if (paramFindWallpaperTargetResult.topWallpaper != null)
        {
          bool6 = bool2;
          bool7 = bool3;
          bool8 = bool4;
          bool5 = bool1;
          k = j;
          if (!bool1) {}
        }
        else
        {
          paramFindWallpaperTargetResult.setTopWallpaper(localWindowState, i);
          bool5 = false;
          k = j;
          bool8 = bool4;
          bool7 = bool3;
          bool6 = bool2;
        }
      }
      for (;;)
      {
        i -= 1;
        bool2 = bool6;
        bool3 = bool7;
        bool4 = bool8;
        bool1 = bool5;
        j = k;
        break;
        boolean bool9 = true;
        if ((localWindowState != localWindowAnimator.mWindowDetachedWallpaper) && (localWindowState.mAppToken != null) && (localWindowState.mAppToken.hidden) && (localWindowState.mAppToken.mAppAnimator.animation == null))
        {
          bool6 = bool2;
          bool7 = bool3;
          bool8 = bool4;
          bool5 = bool9;
          k = j;
          if (WindowManagerDebugConfig.DEBUG_WALLPAPER)
          {
            Slog.v(TAG, "Skipping hidden and not animating token: " + localWindowState);
            bool6 = bool2;
            bool7 = bool3;
            bool8 = bool4;
            bool5 = bool9;
            k = j;
          }
        }
        else
        {
          if (WindowManagerDebugConfig.DEBUG_WALLPAPER) {
            Slog.v(TAG, "Win #" + i + " " + localWindowState + ": isOnScreen=" + localWindowState.isOnScreen() + " mDrawState=" + localWindowState.mWinAnimator.mDrawState);
          }
          bool1 = bool2;
          label394:
          boolean bool10;
          if (!bool2)
          {
            localObject = localWindowState.getStack();
            if ((localObject != null) && (((TaskStack)localObject).mStackId == 2)) {
              bool1 = true;
            }
          }
          else
          {
            bool2 = bool4 | localWindowState.mWillReplaceWindow;
            if (localWindowState.mAppToken == null) {
              break label661;
            }
            bool10 = localWindowState.mWinAnimator.mKeyguardGoingAwayWithWallpaper;
            label422:
            bool3 |= bool10;
            if ((localWindowState.mAttrs.flags & 0x100000) == 0) {
              break label667;
            }
          }
          label661:
          label667:
          for (bool4 = true;; bool4 = false)
          {
            if ((!bool4) || (!localWindowState.isOnScreen()) || ((this.mWallpaperTarget != localWindowState) && (!localWindowState.isDrawFinishedLw()))) {
              break label673;
            }
            if (WindowManagerDebugConfig.DEBUG_WALLPAPER) {
              Slog.v(TAG, "Found wallpaper target: #" + i + "=" + localWindowState);
            }
            paramFindWallpaperTargetResult.setWallpaperTarget(localWindowState, i);
            bool5 = bool1;
            bool6 = bool3;
            bool7 = bool2;
            localObject = localWindowState;
            if (localWindowState != this.mWallpaperTarget) {
              break label724;
            }
            bool5 = bool1;
            bool6 = bool3;
            bool7 = bool2;
            localObject = localWindowState;
            if (!localWindowState.mWinAnimator.isAnimationSet()) {
              break label724;
            }
            bool6 = bool1;
            bool7 = bool3;
            bool8 = bool2;
            bool5 = bool9;
            k = j;
            if (!WindowManagerDebugConfig.DEBUG_WALLPAPER) {
              break;
            }
            Slog.v(TAG, "Win " + localWindowState + ": token animating, looking behind.");
            bool6 = bool1;
            bool7 = bool3;
            bool8 = bool2;
            bool5 = bool9;
            k = j;
            break;
            bool1 = false;
            break label394;
            bool10 = false;
            break label422;
          }
          label673:
          bool6 = bool1;
          bool7 = bool3;
          bool8 = bool2;
          bool5 = bool9;
          k = j;
          if (localWindowState == localWindowAnimator.mWindowDetachedWallpaper)
          {
            k = i;
            bool6 = bool1;
            bool7 = bool3;
            bool8 = bool2;
            bool5 = bool9;
          }
        }
      }
    }
    label724:
    if (paramFindWallpaperTargetResult.wallpaperTarget != null) {
      return;
    }
    if (j >= 0)
    {
      if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) {
        Slog.v(TAG, "Found animating detached wallpaper activity: #" + j + "=" + localObject);
      }
      paramFindWallpaperTargetResult.setWallpaperTarget((WindowState)localObject, j);
    }
    do
    {
      return;
      if ((bool5) || ((bool7) && (this.mWallpaperTarget != null)))
      {
        paramFindWallpaperTargetResult.setWallpaperTarget(paramFindWallpaperTargetResult.topWallpaper, paramFindWallpaperTargetResult.topWallpaperIndex);
        return;
      }
    } while (!bool6);
    paramFindWallpaperTargetResult.setWallpaperTarget(paramFindWallpaperTargetResult.topWallpaper, paramFindWallpaperTargetResult.topWallpaperIndex);
  }
  
  private boolean isWallpaperVisible(WindowState paramWindowState)
  {
    Object localObject;
    if (WindowManagerDebugConfig.DEBUG_WALLPAPER)
    {
      String str = TAG;
      StringBuilder localStringBuilder = new StringBuilder().append("Wallpaper vis: target ").append(paramWindowState).append(", obscured=");
      if (paramWindowState != null)
      {
        localObject = Boolean.toString(paramWindowState.mObscured);
        localStringBuilder = localStringBuilder.append((String)localObject).append(" anim=");
        if ((paramWindowState == null) || (paramWindowState.mAppToken == null)) {
          break label163;
        }
        localObject = paramWindowState.mAppToken.mAppAnimator.animation;
        label83:
        Slog.v(str, localObject + " upper=" + this.mUpperWallpaperTarget + " lower=" + this.mLowerWallpaperTarget);
      }
    }
    else
    {
      if ((paramWindowState == null) || ((paramWindowState.mObscured) && ((paramWindowState.mAppToken == null) || (paramWindowState.mAppToken.mAppAnimator.animation == null)))) {
        break label168;
      }
    }
    label163:
    label168:
    while (this.mUpperWallpaperTarget != null)
    {
      return true;
      localObject = "??";
      break;
      localObject = null;
      break label83;
    }
    return this.mLowerWallpaperTarget != null;
  }
  
  private boolean updateWallpaperWindowsTarget(WindowList paramWindowList, FindWallpaperTargetResult paramFindWallpaperTargetResult)
  {
    boolean bool2 = false;
    WindowState localWindowState1 = paramFindWallpaperTargetResult.wallpaperTarget;
    int i = paramFindWallpaperTargetResult.wallpaperTargetIndex;
    WindowState localWindowState2;
    boolean bool4;
    boolean bool1;
    Object localObject;
    int j;
    boolean bool3;
    if ((this.mWallpaperTarget != localWindowState1) && ((this.mLowerWallpaperTarget == null) || (this.mLowerWallpaperTarget != localWindowState1)))
    {
      if ((WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) || (WindowManagerDebugConfig.DEBUG_ONEPLUS)) {
        Slog.v(TAG, "New wallpaper target: " + localWindowState1 + " oldTarget: " + this.mWallpaperTarget);
      }
      this.mLowerWallpaperTarget = null;
      this.mUpperWallpaperTarget = null;
      localWindowState2 = this.mWallpaperTarget;
      this.mWallpaperTarget = localWindowState1;
      bool4 = true;
      bool1 = bool4;
      localObject = localWindowState1;
      j = i;
      if (localWindowState1 != null)
      {
        bool1 = bool4;
        localObject = localWindowState1;
        j = i;
        if (localWindowState2 != null)
        {
          bool2 = localWindowState2.isAnimatingLw();
          bool3 = localWindowState1.isAnimatingLw();
          if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) {
            Slog.v(TAG, "New animation: " + bool3 + " old animation: " + bool2);
          }
          bool1 = bool4;
          localObject = localWindowState1;
          j = i;
          if (bool3)
          {
            bool1 = bool4;
            localObject = localWindowState1;
            j = i;
            if (bool2)
            {
              int k = paramWindowList.indexOf(localWindowState2);
              if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) {
                Slog.v(TAG, "New i: " + i + " old i: " + k);
              }
              bool1 = bool4;
              localObject = localWindowState1;
              j = i;
              if (k >= 0)
              {
                if (localWindowState1.mAppToken == null) {
                  break label535;
                }
                bool2 = localWindowState1.mAppToken.hiddenRequested;
                if (localWindowState2.mAppToken == null) {
                  break label541;
                }
                bool3 = localWindowState2.mAppToken.hiddenRequested;
                label344:
                if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) {
                  Slog.v(TAG, "Animating wallpapers: old#" + k + "=" + localWindowState2 + " hidden=" + bool3 + " new#" + i + "=" + localWindowState1 + " hidden=" + bool2);
                }
                if (i <= k) {
                  break label547;
                }
                if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) {
                  Slog.v(TAG, "Found target above old target.");
                }
                this.mUpperWallpaperTarget = localWindowState1;
                this.mLowerWallpaperTarget = localWindowState2;
                paramWindowList = localWindowState2;
                i = k;
                label470:
                if ((bool2) && (!bool3)) {
                  break label581;
                }
                bool1 = bool4;
                localObject = paramWindowList;
                j = i;
                if (bool2 == bool3)
                {
                  if (!this.mService.mOpeningApps.contains(paramWindowList.mAppToken)) {
                    break label616;
                  }
                  j = i;
                  localObject = paramWindowList;
                  bool1 = bool4;
                }
              }
            }
          }
        }
      }
    }
    for (;;)
    {
      paramFindWallpaperTargetResult.setWallpaperTarget((WindowState)localObject, j);
      return bool1;
      label535:
      bool2 = false;
      break;
      label541:
      bool3 = false;
      break label344;
      label547:
      if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) {
        Slog.v(TAG, "Found target below old target.");
      }
      this.mUpperWallpaperTarget = localWindowState2;
      this.mLowerWallpaperTarget = localWindowState1;
      paramWindowList = localWindowState1;
      break label470;
      label581:
      if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) {
        Slog.v(TAG, "Old wallpaper still the target.");
      }
      this.mWallpaperTarget = localWindowState2;
      bool1 = bool4;
      localObject = paramWindowList;
      j = i;
      continue;
      label616:
      if (!this.mService.mOpeningApps.contains(localWindowState2.mAppToken))
      {
        bool1 = bool4;
        localObject = paramWindowList;
        j = i;
        if (!this.mService.mClosingApps.contains(localWindowState2.mAppToken)) {}
      }
      else
      {
        this.mWallpaperTarget = localWindowState2;
        bool1 = bool4;
        localObject = paramWindowList;
        j = i;
        continue;
        bool1 = bool2;
        localObject = localWindowState1;
        j = i;
        if (this.mLowerWallpaperTarget != null) {
          if (this.mLowerWallpaperTarget.isAnimatingLw())
          {
            bool1 = bool2;
            localObject = localWindowState1;
            j = i;
            if (this.mUpperWallpaperTarget.isAnimatingLw()) {}
          }
          else
          {
            if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) {
              Slog.v(TAG, "No longer animating wallpaper targets!");
            }
            this.mLowerWallpaperTarget = null;
            this.mUpperWallpaperTarget = null;
            this.mWallpaperTarget = localWindowState1;
            bool1 = true;
            localObject = localWindowState1;
            j = i;
          }
        }
      }
    }
  }
  
  void addWallpaperToken(WindowToken paramWindowToken)
  {
    this.mWallpaperTokens.add(paramWindowToken);
  }
  
  boolean adjustWallpaperWindows()
  {
    this.mService.mWindowPlacerLocked.mWallpaperMayChange = false;
    WindowList localWindowList = this.mService.getDefaultWindowListLocked();
    findWallpaperTarget(localWindowList, this.mFindResults);
    boolean bool1 = updateWallpaperWindowsTarget(localWindowList, this.mFindResults);
    boolean bool2 = updateWallpaperWindowsTargetByLayer(localWindowList, this.mFindResults);
    WindowState localWindowState = this.mFindResults.wallpaperTarget;
    int i = this.mFindResults.wallpaperTargetIndex;
    if ((localWindowState == null) && (this.mFindResults.topWallpaper != null))
    {
      localWindowState = this.mFindResults.topWallpaper;
      i = this.mFindResults.topWallpaperIndex + 1;
    }
    for (;;)
    {
      if (bool2)
      {
        if (this.mWallpaperTarget.mWallpaperX >= 0.0F)
        {
          this.mLastWallpaperX = this.mWallpaperTarget.mWallpaperX;
          this.mLastWallpaperXStep = this.mWallpaperTarget.mWallpaperXStep;
        }
        if (this.mWallpaperTarget.mWallpaperY >= 0.0F)
        {
          this.mLastWallpaperY = this.mWallpaperTarget.mWallpaperY;
          this.mLastWallpaperYStep = this.mWallpaperTarget.mWallpaperYStep;
        }
        if (this.mWallpaperTarget.mWallpaperDisplayOffsetX != Integer.MIN_VALUE) {
          this.mLastWallpaperDisplayOffsetX = this.mWallpaperTarget.mWallpaperDisplayOffsetX;
        }
        if (this.mWallpaperTarget.mWallpaperDisplayOffsetY != Integer.MIN_VALUE) {
          this.mLastWallpaperDisplayOffsetY = this.mWallpaperTarget.mWallpaperDisplayOffsetY;
        }
      }
      bool2 = updateWallpaperWindowsPlacement(localWindowList, localWindowState, i, bool2);
      if ((bool1) && (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT)) {
        Slog.d(TAG, "New wallpaper: target=" + this.mWallpaperTarget + " lower=" + this.mLowerWallpaperTarget + " upper=" + this.mUpperWallpaperTarget);
      }
      return bool2;
      if (i > 0) {
        localWindowState = (WindowState)localWindowList.get(i - 1);
      } else {
        localWindowState = null;
      }
    }
  }
  
  void clearLastWallpaperTimeoutTime()
  {
    this.mLastWallpaperTimeoutTime = 0L;
  }
  
  void dispatchWallpaperVisibility(WindowState paramWindowState, boolean paramBoolean)
  {
    if ((paramWindowState.mWallpaperVisible != paramBoolean) && ((this.mDeferredHideWallpaper == null) || (paramBoolean))) {
      paramWindowState.mWallpaperVisible = paramBoolean;
    }
    try
    {
      if ((WindowManagerDebugConfig.DEBUG_VISIBILITY) || (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT)) {
        Slog.v(TAG, "Updating vis of wallpaper " + paramWindowState + ": " + paramBoolean + " from:\n" + Debug.getCallers(4, "  "));
      }
      paramWindowState.mClient.dispatchAppVisibility(paramBoolean);
      return;
    }
    catch (RemoteException paramWindowState) {}
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mWallpaperTarget=");
    paramPrintWriter.println(this.mWallpaperTarget);
    if ((this.mLowerWallpaperTarget != null) || (this.mUpperWallpaperTarget != null))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mLowerWallpaperTarget=");
      paramPrintWriter.println(this.mLowerWallpaperTarget);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mUpperWallpaperTarget=");
      paramPrintWriter.println(this.mUpperWallpaperTarget);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mLastWallpaperX=");
    paramPrintWriter.print(this.mLastWallpaperX);
    paramPrintWriter.print(" mLastWallpaperY=");
    paramPrintWriter.println(this.mLastWallpaperY);
    if ((this.mLastWallpaperDisplayOffsetX != Integer.MIN_VALUE) || (this.mLastWallpaperDisplayOffsetY != Integer.MIN_VALUE))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mLastWallpaperDisplayOffsetX=");
      paramPrintWriter.print(this.mLastWallpaperDisplayOffsetX);
      paramPrintWriter.print(" mLastWallpaperDisplayOffsetY=");
      paramPrintWriter.println(this.mLastWallpaperDisplayOffsetY);
    }
  }
  
  void dumpTokens(PrintWriter paramPrintWriter, String paramString, boolean paramBoolean)
  {
    if (!this.mWallpaperTokens.isEmpty())
    {
      paramPrintWriter.println();
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Wallpaper tokens:");
      int i = this.mWallpaperTokens.size() - 1;
      if (i >= 0)
      {
        WindowToken localWindowToken = (WindowToken)this.mWallpaperTokens.get(i);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("Wallpaper #");
        paramPrintWriter.print(i);
        paramPrintWriter.print(' ');
        paramPrintWriter.print(localWindowToken);
        if (paramBoolean)
        {
          paramPrintWriter.println(':');
          localWindowToken.dump(paramPrintWriter, "    ");
        }
        for (;;)
        {
          i -= 1;
          break;
          paramPrintWriter.println();
        }
      }
    }
  }
  
  int getAnimLayerAdjustment()
  {
    return this.mWallpaperAnimLayerAdjustment;
  }
  
  WindowState getLowerWallpaperTarget()
  {
    return this.mLowerWallpaperTarget;
  }
  
  WindowState getUpperWallpaperTarget()
  {
    return this.mUpperWallpaperTarget;
  }
  
  WindowState getWallpaperTarget()
  {
    return this.mWallpaperTarget;
  }
  
  void hideDeferredWallpapersIfNeeded()
  {
    if (this.mDeferredHideWallpaper != null)
    {
      hideWallpapers(this.mDeferredHideWallpaper);
      this.mDeferredHideWallpaper = null;
    }
  }
  
  void hideWallpapers(WindowState paramWindowState)
  {
    if ((this.mWallpaperTarget != null) && ((this.mWallpaperTarget != paramWindowState) || (this.mLowerWallpaperTarget != null))) {
      return;
    }
    if (this.mService.mAppTransition.isRunning())
    {
      this.mDeferredHideWallpaper = paramWindowState;
      return;
    }
    if (this.mDeferredHideWallpaper == paramWindowState) {}
    int j;
    WindowToken localWindowToken;
    for (int i = 1;; i = 0)
    {
      j = this.mWallpaperTokens.size() - 1;
      if (j < 0) {
        return;
      }
      localWindowToken = (WindowToken)this.mWallpaperTokens.get(j);
      int k = localWindowToken.windows.size() - 1;
      while (k >= 0)
      {
        Object localObject = (WindowState)localWindowToken.windows.get(k);
        WindowStateAnimator localWindowStateAnimator = ((WindowState)localObject).mWinAnimator;
        if ((!localWindowStateAnimator.mLastHidden) || (i != 0))
        {
          localWindowStateAnimator.hide("hideWallpapers");
          dispatchWallpaperVisibility((WindowState)localObject, false);
          localObject = ((WindowState)localObject).getDisplayContent();
          if (localObject != null) {
            ((DisplayContent)localObject).pendingLayoutChanges |= 0x4;
          }
        }
        k -= 1;
      }
    }
    if ((!WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) || (localWindowToken.hidden)) {}
    for (;;)
    {
      localWindowToken.hidden = true;
      j -= 1;
      break;
      Slog.d(TAG, "Hiding wallpaper " + localWindowToken + " from " + paramWindowState + " target=" + this.mWallpaperTarget + " lower=" + this.mLowerWallpaperTarget + "\n" + Debug.getCallers(5, "  "));
    }
  }
  
  boolean isBelowWallpaperTarget(WindowState paramWindowState)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mWallpaperTarget != null)
    {
      bool1 = bool2;
      if (this.mWallpaperTarget.mLayer >= paramWindowState.mBaseLayer) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  boolean isWallpaperTarget(WindowState paramWindowState)
  {
    return paramWindowState == this.mWallpaperTarget;
  }
  
  boolean isWallpaperTargetAnimating()
  {
    return (this.mWallpaperTarget != null) && (this.mWallpaperTarget.mWinAnimator.isAnimationSet()) && (!this.mWallpaperTarget.mWinAnimator.isDummyAnimation());
  }
  
  boolean isWallpaperVisible()
  {
    return isWallpaperVisible(this.mWallpaperTarget);
  }
  
  boolean processWallpaperDrawPendingTimeout()
  {
    if (this.mWallpaperDrawState == 1)
    {
      this.mWallpaperDrawState = 2;
      if ((WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) || (WindowManagerDebugConfig.DEBUG_WALLPAPER)) {
        Slog.v(TAG, "*** WALLPAPER DRAW TIMEOUT");
      }
      return true;
    }
    return false;
  }
  
  void removeWallpaperToken(WindowToken paramWindowToken)
  {
    this.mWallpaperTokens.remove(paramWindowToken);
  }
  
  Bundle sendWindowWallpaperCommand(WindowState paramWindowState, String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean)
  {
    if ((paramWindowState == this.mWallpaperTarget) || (paramWindowState == this.mLowerWallpaperTarget)) {}
    int i;
    while (paramWindowState == this.mUpperWallpaperTarget)
    {
      i = this.mWallpaperTokens.size() - 1;
      boolean bool = paramBoolean;
      if (i < 0) {
        break label127;
      }
      paramWindowState = ((WindowToken)this.mWallpaperTokens.get(i)).windows;
      int j = paramWindowState.size() - 1;
      while (j >= 0)
      {
        WindowState localWindowState = (WindowState)paramWindowState.get(j);
        try
        {
          localWindowState.mClient.dispatchWallpaperCommand(paramString, paramInt1, paramInt2, paramInt3, paramBundle, bool);
          bool = false;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
        j -= 1;
      }
    }
    for (;;)
    {
      return null;
      i -= 1;
      break;
      label127:
      if (!paramBoolean) {}
    }
  }
  
  void setAnimLayerAdjustment(WindowState paramWindowState, int paramInt)
  {
    if ((paramWindowState != this.mWallpaperTarget) || (this.mLowerWallpaperTarget != null)) {
      return;
    }
    if ((WindowManagerDebugConfig.DEBUG_LAYERS) || (WindowManagerDebugConfig.DEBUG_WALLPAPER)) {
      Slog.v(TAG, "Setting wallpaper layer adj to " + paramInt);
    }
    this.mWallpaperAnimLayerAdjustment = paramInt;
    int i = this.mWallpaperTokens.size() - 1;
    while (i >= 0)
    {
      paramWindowState = ((WindowToken)this.mWallpaperTokens.get(i)).windows;
      int j = paramWindowState.size() - 1;
      while (j >= 0)
      {
        WindowState localWindowState = (WindowState)paramWindowState.get(j);
        localWindowState.mWinAnimator.mAnimLayer = (localWindowState.mLayer + paramInt);
        if ((WindowManagerDebugConfig.DEBUG_LAYERS) || (WindowManagerDebugConfig.DEBUG_WALLPAPER)) {
          Slog.v(TAG, "setWallpaper win " + localWindowState + " anim layer: " + localWindowState.mWinAnimator.mAnimLayer);
        }
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void setWindowWallpaperDisplayOffset(WindowState paramWindowState, int paramInt1, int paramInt2)
  {
    if ((paramWindowState.mWallpaperDisplayOffsetX != paramInt1) || (paramWindowState.mWallpaperDisplayOffsetY != paramInt2))
    {
      paramWindowState.mWallpaperDisplayOffsetX = paramInt1;
      paramWindowState.mWallpaperDisplayOffsetY = paramInt2;
      updateWallpaperOffsetLocked(paramWindowState, true);
    }
  }
  
  void setWindowWallpaperPosition(WindowState paramWindowState, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if ((paramWindowState.mWallpaperX != paramFloat1) || (paramWindowState.mWallpaperY != paramFloat2))
    {
      paramWindowState.mWallpaperX = paramFloat1;
      paramWindowState.mWallpaperY = paramFloat2;
      paramWindowState.mWallpaperXStep = paramFloat3;
      paramWindowState.mWallpaperYStep = paramFloat4;
      updateWallpaperOffsetLocked(paramWindowState, true);
    }
  }
  
  boolean updateWallpaperOffset(WindowState paramWindowState, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = 0;
    float f1;
    label28:
    float f2;
    label43:
    label77:
    int i;
    if (paramWindowState.isRtl())
    {
      f1 = 1.0F;
      if (this.mLastWallpaperX < 0.0F) {
        break label633;
      }
      f1 = this.mLastWallpaperX;
      if (this.mLastWallpaperXStep < 0.0F) {
        break label636;
      }
      f2 = this.mLastWallpaperXStep;
      paramInt1 = paramWindowState.mFrame.right - paramWindowState.mFrame.left - paramInt1;
      if (paramInt1 <= 0) {
        break label643;
      }
      paramInt1 = -(int)(paramInt1 * f1 + 0.5F);
      i = paramInt1;
      if (this.mLastWallpaperDisplayOffsetX != Integer.MIN_VALUE) {
        i = paramInt1 + this.mLastWallpaperDisplayOffsetX;
      }
      if (paramWindowState.mXOffset == i) {
        break label648;
      }
      bool = true;
      label109:
      if (bool)
      {
        if (WindowManagerDebugConfig.DEBUG_WALLPAPER) {
          Slog.v(TAG, "Update wallpaper " + paramWindowState + " x: " + i);
        }
        paramWindowState.mXOffset = i;
      }
      if (paramWindowState.mWallpaperX == f1)
      {
        paramInt1 = j;
        if (paramWindowState.mWallpaperXStep == f2) {}
      }
      else
      {
        paramWindowState.mWallpaperX = f1;
        paramWindowState.mWallpaperXStep = f2;
        paramInt1 = 1;
      }
      if (this.mLastWallpaperY < 0.0F) {
        break label654;
      }
      f1 = this.mLastWallpaperY;
      label216:
      if (this.mLastWallpaperYStep < 0.0F) {
        break label662;
      }
      f2 = this.mLastWallpaperYStep;
      label231:
      paramInt2 = paramWindowState.mFrame.bottom - paramWindowState.mFrame.top - paramInt2;
      if (paramInt2 <= 0) {
        break label669;
      }
    }
    label557:
    label633:
    label636:
    label643:
    label648:
    label654:
    label662:
    label669:
    for (paramInt2 = -(int)(paramInt2 * f1 + 0.5F);; paramInt2 = 0)
    {
      i = paramInt2;
      if (this.mLastWallpaperDisplayOffsetY != Integer.MIN_VALUE) {
        i = paramInt2 + this.mLastWallpaperDisplayOffsetY;
      }
      if (paramWindowState.mYOffset != i)
      {
        if (WindowManagerDebugConfig.DEBUG_WALLPAPER) {
          Slog.v(TAG, "Update wallpaper " + paramWindowState + " y: " + i);
        }
        bool = true;
        paramWindowState.mYOffset = i;
      }
      if ((paramWindowState.mWallpaperY != f1) || (paramWindowState.mWallpaperYStep != f2))
      {
        paramWindowState.mWallpaperY = f1;
        paramWindowState.mWallpaperYStep = f2;
        paramInt1 = 1;
      }
      if ((paramInt1 != 0) && ((paramWindowState.mAttrs.privateFlags & 0x4) != 0)) {}
      try
      {
        if (WindowManagerDebugConfig.DEBUG_WALLPAPER) {
          Slog.v(TAG, "Report new wp offset " + paramWindowState + " x=" + paramWindowState.mWallpaperX + " y=" + paramWindowState.mWallpaperY);
        }
        if (paramBoolean) {
          this.mWaitingOnWallpaper = paramWindowState;
        }
        paramWindowState.mClient.dispatchWallpaperOffsets(paramWindowState.mWallpaperX, paramWindowState.mWallpaperY, paramWindowState.mWallpaperXStep, paramWindowState.mWallpaperYStep, paramBoolean);
        if ((paramBoolean) && (this.mWaitingOnWallpaper != null))
        {
          l1 = SystemClock.uptimeMillis();
          long l2 = this.mLastWallpaperTimeoutTime;
          if (l2 + 10000L >= l1) {}
        }
      }
      catch (RemoteException paramWindowState)
      {
        long l1;
        return bool;
      }
      try
      {
        if (WindowManagerDebugConfig.DEBUG_WALLPAPER) {
          Slog.v(TAG, "Waiting for offset complete...");
        }
        this.mService.mWindowMap.wait(150L);
      }
      catch (InterruptedException localInterruptedException)
      {
        break label557;
      }
      if (WindowManagerDebugConfig.DEBUG_WALLPAPER) {
        Slog.v(TAG, "Offset complete!");
      }
      if (150L + l1 < SystemClock.uptimeMillis())
      {
        Slog.i(TAG, "Timeout waiting for wallpaper to offset: " + paramWindowState);
        this.mLastWallpaperTimeoutTime = l1;
      }
      this.mWaitingOnWallpaper = null;
      return bool;
      f1 = 0.0F;
      break;
      break label28;
      f2 = -1.0F;
      break label43;
      paramInt1 = 0;
      break label77;
      bool = false;
      break label109;
      f1 = 0.5F;
      break label216;
      f2 = -1.0F;
      break label231;
    }
  }
  
  void updateWallpaperOffsetLocked(WindowState paramWindowState, boolean paramBoolean)
  {
    Object localObject = paramWindowState.getDisplayContent();
    if (localObject == null) {
      return;
    }
    localObject = ((DisplayContent)localObject).getDisplayInfo();
    int k = ((DisplayInfo)localObject).logicalWidth;
    int m = ((DisplayInfo)localObject).logicalHeight;
    localObject = this.mWallpaperTarget;
    label82:
    label101:
    label120:
    label139:
    label158:
    int i;
    if (localObject != null)
    {
      if (((WindowState)localObject).mWallpaperX >= 0.0F)
      {
        this.mLastWallpaperX = ((WindowState)localObject).mWallpaperX;
        if (((WindowState)localObject).mWallpaperY < 0.0F) {
          break label285;
        }
        this.mLastWallpaperY = ((WindowState)localObject).mWallpaperY;
        if (((WindowState)localObject).mWallpaperDisplayOffsetX == Integer.MIN_VALUE) {
          break label305;
        }
        this.mLastWallpaperDisplayOffsetX = ((WindowState)localObject).mWallpaperDisplayOffsetX;
        if (((WindowState)localObject).mWallpaperDisplayOffsetY == Integer.MIN_VALUE) {
          break label325;
        }
        this.mLastWallpaperDisplayOffsetY = ((WindowState)localObject).mWallpaperDisplayOffsetY;
        if (((WindowState)localObject).mWallpaperXStep < 0.0F) {
          break label345;
        }
        this.mLastWallpaperXStep = ((WindowState)localObject).mWallpaperXStep;
        if (((WindowState)localObject).mWallpaperYStep < 0.0F) {
          break label365;
        }
        this.mLastWallpaperYStep = ((WindowState)localObject).mWallpaperYStep;
      }
    }
    else {
      i = this.mWallpaperTokens.size() - 1;
    }
    for (;;)
    {
      if (i < 0) {
        return;
      }
      paramWindowState = ((WindowToken)this.mWallpaperTokens.get(i)).windows;
      int j = paramWindowState.size() - 1;
      for (;;)
      {
        if (j >= 0)
        {
          localObject = (WindowState)paramWindowState.get(j);
          boolean bool = paramBoolean;
          if (updateWallpaperOffset((WindowState)localObject, k, m, paramBoolean))
          {
            WindowStateAnimator localWindowStateAnimator = ((WindowState)localObject).mWinAnimator;
            localWindowStateAnimator.computeShownFrameLocked();
            localWindowStateAnimator.setWallpaperOffset(((WindowState)localObject).mShownPosition);
            bool = false;
          }
          j -= 1;
          paramBoolean = bool;
          continue;
          if (paramWindowState.mWallpaperX < 0.0F) {
            break;
          }
          this.mLastWallpaperX = paramWindowState.mWallpaperX;
          break;
          label285:
          if (paramWindowState.mWallpaperY < 0.0F) {
            break label82;
          }
          this.mLastWallpaperY = paramWindowState.mWallpaperY;
          break label82;
          label305:
          if (paramWindowState.mWallpaperDisplayOffsetX == Integer.MIN_VALUE) {
            break label101;
          }
          this.mLastWallpaperDisplayOffsetX = paramWindowState.mWallpaperDisplayOffsetX;
          break label101;
          label325:
          if (paramWindowState.mWallpaperDisplayOffsetY == Integer.MIN_VALUE) {
            break label120;
          }
          this.mLastWallpaperDisplayOffsetY = paramWindowState.mWallpaperDisplayOffsetY;
          break label120;
          label345:
          if (paramWindowState.mWallpaperXStep < 0.0F) {
            break label139;
          }
          this.mLastWallpaperXStep = paramWindowState.mWallpaperXStep;
          break label139;
          label365:
          if (paramWindowState.mWallpaperYStep < 0.0F) {
            break label158;
          }
          this.mLastWallpaperYStep = paramWindowState.mWallpaperYStep;
          break label158;
        }
      }
      i -= 1;
    }
  }
  
  void updateWallpaperVisibility()
  {
    DisplayContent localDisplayContent = this.mWallpaperTarget.getDisplayContent();
    if (localDisplayContent == null) {
      return;
    }
    boolean bool2 = isWallpaperVisible(this.mWallpaperTarget);
    Object localObject = localDisplayContent.getDisplayInfo();
    int k = ((DisplayInfo)localObject).logicalWidth;
    int m = ((DisplayInfo)localObject).logicalHeight;
    int i = this.mWallpaperTokens.size() - 1;
    while (i >= 0)
    {
      localObject = (WindowToken)this.mWallpaperTokens.get(i);
      if (((WindowToken)localObject).hidden == bool2) {
        if (!bool2) {
          break label164;
        }
      }
      label164:
      for (boolean bool1 = false;; bool1 = true)
      {
        ((WindowToken)localObject).hidden = bool1;
        localDisplayContent.layoutNeeded = true;
        localObject = ((WindowToken)localObject).windows;
        int j = ((WindowList)localObject).size() - 1;
        while (j >= 0)
        {
          WindowState localWindowState = (WindowState)((WindowList)localObject).get(j);
          if (bool2) {
            updateWallpaperOffset(localWindowState, k, m, false);
          }
          dispatchWallpaperVisibility(localWindowState, bool2);
          j -= 1;
        }
      }
      i -= 1;
    }
  }
  
  boolean updateWallpaperWindowsPlacement(WindowList paramWindowList, WindowState paramWindowState, int paramInt, boolean paramBoolean)
  {
    Object localObject1 = this.mService.getDefaultDisplayContentLocked().getDisplayInfo();
    int n = ((DisplayInfo)localObject1).logicalWidth;
    int i1 = ((DisplayInfo)localObject1).logicalHeight;
    boolean bool1 = false;
    int j = this.mWallpaperTokens.size() - 1;
    while (j >= 0)
    {
      localObject1 = (WindowToken)this.mWallpaperTokens.get(j);
      Object localObject2;
      boolean bool2;
      label138:
      int k;
      int i;
      if (((WindowToken)localObject1).hidden == paramBoolean)
      {
        if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT)
        {
          localObject2 = TAG;
          StringBuilder localStringBuilder = new StringBuilder().append("Wallpaper token ").append(localObject1).append(" hidden=");
          if (paramBoolean)
          {
            bool2 = false;
            Slog.d((String)localObject2, bool2);
          }
        }
        else
        {
          if (!paramBoolean) {
            break label336;
          }
          bool2 = false;
          ((WindowToken)localObject1).hidden = bool2;
          this.mService.getDefaultDisplayContentLocked().layoutNeeded = true;
        }
      }
      else
      {
        localObject1 = ((WindowToken)localObject1).windows;
        k = ((WindowList)localObject1).size() - 1;
        i = paramInt;
        label175:
        if (k < 0) {
          break label605;
        }
        localObject2 = (WindowState)((WindowList)localObject1).get(k);
        if (paramBoolean) {
          updateWallpaperOffset((WindowState)localObject2, n, i1, false);
        }
        dispatchWallpaperVisibility((WindowState)localObject2, paramBoolean);
        ((WindowState)localObject2).mWinAnimator.mAnimLayer = (((WindowState)localObject2).mLayer + this.mWallpaperAnimLayerAdjustment);
        if ((WindowManagerDebugConfig.DEBUG_LAYERS) || (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT)) {
          Slog.v(TAG, "adjustWallpaper win " + localObject2 + " anim layer: " + ((WindowState)localObject2).mWinAnimator.mAnimLayer);
        }
        if (localObject2 != paramWindowState) {
          break label347;
        }
        paramInt = i - 1;
        if (paramInt <= 0) {
          break label342;
        }
        paramWindowState = (WindowState)paramWindowList.get(paramInt - 1);
      }
      for (;;)
      {
        k -= 1;
        i = paramInt;
        break label175;
        bool2 = true;
        break;
        label336:
        bool2 = true;
        break label138;
        label342:
        paramWindowState = null;
        continue;
        label347:
        int i2 = paramWindowList.indexOf(localObject2);
        paramInt = i;
        if (i2 >= 0)
        {
          if (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) {
            Slog.v(TAG, "Wallpaper removing at " + i2 + ": " + localObject2);
          }
          paramWindowList.remove(i2);
          this.mService.mWindowsChanged = true;
          paramInt = i;
          if (i2 < i) {
            paramInt = i - 1;
          }
        }
        int m = 0;
        i = m;
        if (paramBoolean)
        {
          i = m;
          if (paramWindowState != null)
          {
            int i3 = paramWindowState.mAttrs.type;
            if ((paramWindowState.mAttrs.privateFlags & 0x400) == 0)
            {
              i = m;
              if (i3 != 2029) {}
            }
            else
            {
              i = Math.min(paramWindowList.indexOf(paramWindowState), findLowestWindowOnScreen(paramWindowList));
            }
          }
        }
        if ((WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) || (WindowManagerDebugConfig.DEBUG_WINDOW_MOVEMENT) || ((WindowManagerDebugConfig.DEBUG_ADD_REMOVE) && (i2 != i))) {
          Slog.v(TAG, "Moving wallpaper " + localObject2 + " from " + i2 + " to " + i);
        }
        paramWindowList.add(i, localObject2);
        this.mService.mWindowsChanged = true;
        bool1 = true;
      }
      label605:
      j -= 1;
      paramInt = i;
    }
    return bool1;
  }
  
  boolean updateWallpaperWindowsTargetByLayer(WindowList paramWindowList, FindWallpaperTargetResult paramFindWallpaperTargetResult)
  {
    int k = 0;
    Object localObject1 = paramFindWallpaperTargetResult.wallpaperTarget;
    int i = paramFindWallpaperTargetResult.wallpaperTargetIndex;
    boolean bool1;
    int j;
    label125:
    boolean bool2;
    Object localObject2;
    WindowState localWindowState;
    if (localObject1 != null)
    {
      bool1 = true;
      if (!bool1) {
        break label295;
      }
      bool1 = isWallpaperVisible((WindowState)localObject1);
      if (WindowManagerDebugConfig.DEBUG_WALLPAPER) {
        Slog.v(TAG, "Wallpaper visibility: " + bool1);
      }
      j = k;
      if (this.mLowerWallpaperTarget == null)
      {
        j = k;
        if (((WindowState)localObject1).mAppToken != null) {
          j = ((WindowState)localObject1).mAppToken.mAppAnimator.animLayerAdjustment;
        }
      }
      this.mWallpaperAnimLayerAdjustment = j;
      k = this.mService.mPolicy.getMaxWallpaperLayer();
      bool2 = bool1;
      localObject2 = localObject1;
      j = i;
      if (i > 0)
      {
        localWindowState = (WindowState)paramWindowList.get(i - 1);
        if ((localWindowState.mBaseLayer >= k * 10000 + 1000) || (localWindowState.mAttachedWindow == localObject1) || ((((WindowState)localObject1).mAttachedWindow != null) && (localWindowState.mAttachedWindow == ((WindowState)localObject1).mAttachedWindow))) {
          break label284;
        }
        bool2 = bool1;
        localObject2 = localObject1;
        j = i;
        if (localWindowState.mAttrs.type == 3)
        {
          if (((WindowState)localObject1).mToken != null) {
            break label260;
          }
          j = i;
          localObject2 = localObject1;
          bool2 = bool1;
        }
      }
    }
    for (;;)
    {
      paramFindWallpaperTargetResult.setWallpaperTarget((WindowState)localObject2, j);
      return bool2;
      bool1 = false;
      break;
      label260:
      bool2 = bool1;
      localObject2 = localObject1;
      j = i;
      if (localWindowState.mToken == ((WindowState)localObject1).mToken)
      {
        label284:
        localObject1 = localWindowState;
        i -= 1;
        break label125;
        label295:
        bool2 = bool1;
        localObject2 = localObject1;
        j = i;
        if (WindowManagerDebugConfig.DEBUG_WALLPAPER)
        {
          Slog.v(TAG, "No wallpaper target");
          bool2 = bool1;
          localObject2 = localObject1;
          j = i;
        }
      }
    }
  }
  
  void wallpaperCommandComplete(IBinder paramIBinder)
  {
    if ((this.mWaitingOnWallpaper != null) && (this.mWaitingOnWallpaper.mClient.asBinder() == paramIBinder))
    {
      this.mWaitingOnWallpaper = null;
      this.mService.mWindowMap.notifyAll();
    }
  }
  
  void wallpaperOffsetsComplete(IBinder paramIBinder)
  {
    if ((this.mWaitingOnWallpaper != null) && (this.mWaitingOnWallpaper.mClient.asBinder() == paramIBinder))
    {
      this.mWaitingOnWallpaper = null;
      this.mService.mWindowMap.notifyAll();
    }
  }
  
  boolean wallpaperTransitionReady()
  {
    boolean bool1 = true;
    int j = 1;
    int i = this.mWallpaperTokens.size() - 1;
    while ((i >= 0) && (j != 0))
    {
      WindowToken localWindowToken = (WindowToken)this.mWallpaperTokens.get(i);
      int m = localWindowToken.windows.size() - 1;
      boolean bool2;
      int k;
      for (;;)
      {
        bool2 = bool1;
        k = j;
        if (m < 0) {
          break label206;
        }
        WindowState localWindowState = (WindowState)localWindowToken.windows.get(m);
        if ((localWindowState.mWallpaperVisible) && (!localWindowState.isDrawnLw())) {
          break;
        }
        m -= 1;
      }
      j = 0;
      if (this.mWallpaperDrawState != 2) {
        bool1 = false;
      }
      if (this.mWallpaperDrawState == 0)
      {
        this.mWallpaperDrawState = 1;
        this.mService.mH.removeMessages(39);
        this.mService.mH.sendEmptyMessageDelayed(39, 500L);
      }
      if (!WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS)
      {
        bool2 = bool1;
        k = j;
        if (!WindowManagerDebugConfig.DEBUG_WALLPAPER) {}
      }
      else
      {
        Slog.v(TAG, "Wallpaper should be visible but has not been drawn yet. mWallpaperDrawState=" + this.mWallpaperDrawState);
        k = j;
        bool2 = bool1;
      }
      label206:
      i -= 1;
      bool1 = bool2;
      j = k;
    }
    if (j != 0)
    {
      this.mWallpaperDrawState = 0;
      this.mService.mH.removeMessages(39);
    }
    return bool1;
  }
  
  private static final class FindWallpaperTargetResult
  {
    WindowState topWallpaper = null;
    int topWallpaperIndex = 0;
    WindowState wallpaperTarget = null;
    int wallpaperTargetIndex = 0;
    
    void reset()
    {
      this.topWallpaperIndex = 0;
      this.topWallpaper = null;
      this.wallpaperTargetIndex = 0;
      this.wallpaperTarget = null;
    }
    
    void setTopWallpaper(WindowState paramWindowState, int paramInt)
    {
      this.topWallpaper = paramWindowState;
      this.topWallpaperIndex = paramInt;
    }
    
    void setWallpaperTarget(WindowState paramWindowState, int paramInt)
    {
      this.wallpaperTarget = paramWindowState;
      this.wallpaperTargetIndex = paramInt;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/WallpaperController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */