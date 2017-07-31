package com.android.server.wm;

import android.content.Context;
import android.os.Trace;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.Choreographer.FrameCallback;
import android.view.SurfaceControl;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerPolicy;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import java.io.PrintWriter;
import java.util.ArrayList;

public class WindowAnimator
{
  static final int KEYGUARD_ANIMATING_OUT = 2;
  private static final long KEYGUARD_ANIM_TIMEOUT_MS = 1000L;
  static final int KEYGUARD_NOT_SHOWN = 0;
  static final int KEYGUARD_SHOWN = 1;
  private static final String TAG = "WindowManager";
  private int mAnimTransactionSequence;
  private boolean mAnimating;
  final Choreographer.FrameCallback mAnimationFrameCallback;
  boolean mAppWindowAnimating;
  int mBulkUpdateParams = 0;
  final Context mContext;
  long mCurrentTime;
  SparseArray<DisplayContentsAnimator> mDisplayContentsAnimators = new SparseArray(2);
  int mForceHiding = 0;
  boolean mInitialized = false;
  boolean mKeyguardGoingAway;
  int mKeyguardGoingAwayFlags;
  private WindowState mLastShowWinWhenLocked;
  Object mLastWindowFreezeSource;
  final WindowManagerPolicy mPolicy;
  Animation mPostKeyguardExitAnimation;
  private boolean mRemoveReplacedWindows = false;
  final WindowManagerService mService;
  private final AppTokenList mTmpExitingAppTokens = new AppTokenList();
  WindowState mWindowDetachedWallpaper = null;
  private final WindowSurfacePlacer mWindowPlacerLocked;
  
  WindowAnimator(WindowManagerService paramWindowManagerService)
  {
    this.mService = paramWindowManagerService;
    this.mContext = paramWindowManagerService.mContext;
    this.mPolicy = paramWindowManagerService.mPolicy;
    this.mWindowPlacerLocked = paramWindowManagerService.mWindowPlacerLocked;
    this.mAnimationFrameCallback = new Choreographer.FrameCallback()
    {
      public void doFrame(long paramAnonymousLong)
      {
        synchronized (WindowAnimator.this.mService.mWindowMap)
        {
          WindowAnimator.this.mService.mAnimationScheduled = false;
          WindowAnimator.-wrap0(WindowAnimator.this, paramAnonymousLong);
          return;
        }
      }
    };
  }
  
  private void animateLocked(long paramLong)
  {
    if (!this.mInitialized) {
      return;
    }
    this.mCurrentTime = (paramLong / 1000000L);
    this.mBulkUpdateParams = 8;
    boolean bool2 = this.mAnimating;
    setAnimating(false);
    this.mAppWindowAnimating = false;
    if (WindowManagerDebugConfig.DEBUG_WINDOW_TRACE) {
      Slog.i(TAG, "!!! animate: entry time=" + this.mCurrentTime);
    }
    if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
      Slog.i(TAG, ">>> OPEN TRANSACTION animateLocked");
    }
    SurfaceControl.openTransaction();
    SurfaceControl.setAnimationTransaction();
    int i;
    int j;
    try
    {
      k = this.mDisplayContentsAnimators.size();
      i = 0;
    }
    catch (RuntimeException localRuntimeException)
    {
      int k;
      Object localObject1;
      ScreenRotationAnimation localScreenRotationAnimation2;
      int m;
      label244:
      Slog.wtf(TAG, "Unhandled exception in Window Manager", localRuntimeException);
      SurfaceControl.closeTransaction();
      if (!WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        break label338;
      }
      Slog.i(TAG, "<<< CLOSE TRANSACTION animateLocked");
      for (;;)
      {
        j = 0;
        k = this.mService.mDisplayContents.size();
        i = 0;
        while (i < k)
        {
          m = getPendingLayoutChanges(((DisplayContent)this.mService.mDisplayContents.valueAt(i)).getDisplayId());
          if ((m & 0x4) != 0) {
            this.mBulkUpdateParams |= 0x20;
          }
          if (m != 0) {
            j = 1;
          }
          i += 1;
        }
        i += 1;
        break;
        i = 0;
        if (i < k)
        {
          j = this.mDisplayContentsAnimators.keyAt(i);
          testTokenMayBeDrawnLocked(j);
          localScreenRotationAnimation1 = ((DisplayContentsAnimator)this.mDisplayContentsAnimators.valueAt(i)).mScreenRotationAnimation;
          if (localScreenRotationAnimation1 != null) {
            localScreenRotationAnimation1.updateSurfacesInTransaction();
          }
          orAnimating(this.mService.getDisplayContentLocked(j).animateDimLayers());
          orAnimating(this.mService.getDisplayContentLocked(j).getDockedDividerController().animate(this.mCurrentTime));
          if ((this.mService.mAccessibilityController == null) || (j != 0)) {
            break label865;
          }
          this.mService.mAccessibilityController.drawMagnifiedRegionBorderIfNeededLocked();
          break label865;
        }
        if (this.mService.mDragState != null) {
          this.mAnimating |= this.mService.mDragState.stepAnimationLocked(this.mCurrentTime);
        }
        if (this.mAnimating) {
          this.mService.scheduleAnimationLocked();
        }
        if (this.mService.mWatermark != null) {
          this.mService.mWatermark.drawIfNeeded();
        }
        SurfaceControl.closeTransaction();
        if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
          Slog.i(TAG, "<<< CLOSE TRANSACTION animateLocked");
        }
      }
    }
    finally
    {
      SurfaceControl.closeTransaction();
      if (!WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        break label649;
      }
      Slog.i(TAG, "<<< CLOSE TRANSACTION animateLocked");
    }
    if (i < k)
    {
      j = this.mDisplayContentsAnimators.keyAt(i);
      updateAppWindowsLocked(j);
      localObject1 = (DisplayContentsAnimator)this.mDisplayContentsAnimators.valueAt(i);
      localScreenRotationAnimation2 = ((DisplayContentsAnimator)localObject1).mScreenRotationAnimation;
      if ((localScreenRotationAnimation2 != null) && (localScreenRotationAnimation2.isAnimating()))
      {
        if (!localScreenRotationAnimation2.stepAnimationLocked(this.mCurrentTime)) {
          break label244;
        }
        setAnimating(true);
      }
      for (;;)
      {
        updateWindowsLocked(j);
        updateWallpaperLocked(j);
        localObject1 = this.mService.getWindowListLocked(j);
        m = ((WindowList)localObject1).size();
        j = 0;
        while (j < m)
        {
          ((WindowState)((WindowList)localObject1).get(j)).mWinAnimator.prepareSurfaceLocked(true);
          j += 1;
        }
        this.mBulkUpdateParams |= 0x1;
        localScreenRotationAnimation2.kill();
        ((DisplayContentsAnimator)localObject1).mScreenRotationAnimation = null;
        if ((this.mService.mAccessibilityController != null) && (j == 0)) {
          this.mService.mAccessibilityController.onRotationChangedLocked(this.mService.getDefaultDisplayContentLocked(), this.mService.mRotation);
        }
      }
    }
    for (;;)
    {
      label338:
      ScreenRotationAnimation localScreenRotationAnimation1;
      label649:
      boolean bool1 = false;
      if (this.mBulkUpdateParams != 0) {
        bool1 = this.mWindowPlacerLocked.copyAnimToLayoutParamsLocked();
      }
      if ((j != 0) || (bool1)) {
        this.mWindowPlacerLocked.requestTraversal();
      }
      if ((!this.mAnimating) || (bool2)) {}
      for (;;)
      {
        if ((!this.mAnimating) && (bool2))
        {
          this.mWindowPlacerLocked.requestTraversal();
          if (Trace.isTagEnabled(32L)) {
            Trace.asyncTraceEnd(32L, "animating", 0);
          }
        }
        if (this.mRemoveReplacedWindows) {
          removeReplacedWindowsLocked();
        }
        this.mService.stopUsingSavedSurfaceLocked();
        this.mService.destroyPreservedSurfaceLocked();
        this.mService.mWindowPlacerLocked.destroyPendingSurfaces();
        if (WindowManagerDebugConfig.DEBUG_WINDOW_TRACE) {
          Slog.i(TAG, "!!! animate: exit mAnimating=" + this.mAnimating + " mBulkUpdateParams=" + Integer.toHexString(this.mBulkUpdateParams) + " mPendingLayoutChanges(DEFAULT_DISPLAY)=" + Integer.toHexString(getPendingLayoutChanges(0)));
        }
        return;
        if (Trace.isTagEnabled(32L)) {
          Trace.asyncTraceBegin(32L, "animating", 0);
        }
      }
      label865:
      i += 1;
    }
  }
  
  private static String bulkUpdateParamsToString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    if ((paramInt & 0x1) != 0) {
      localStringBuilder.append(" UPDATE_ROTATION");
    }
    if ((paramInt & 0x2) != 0) {
      localStringBuilder.append(" WALLPAPER_MAY_CHANGE");
    }
    if ((paramInt & 0x4) != 0) {
      localStringBuilder.append(" FORCE_HIDING_CHANGED");
    }
    if ((paramInt & 0x8) != 0) {
      localStringBuilder.append(" ORIENTATION_CHANGE_COMPLETE");
    }
    if ((paramInt & 0x10) != 0) {
      localStringBuilder.append(" TURN_ON_SCREEN");
    }
    return localStringBuilder.toString();
  }
  
  private String forceHidingToString()
  {
    switch (this.mForceHiding)
    {
    default: 
      return "KEYGUARD STATE UNKNOWN " + this.mForceHiding;
    case 0: 
      return "KEYGUARD_NOT_SHOWN";
    case 1: 
      return "KEYGUARD_SHOWN";
    }
    return "KEYGUARD_ANIMATING_OUT";
  }
  
  private DisplayContentsAnimator getDisplayContentsAnimatorLocked(int paramInt)
  {
    DisplayContentsAnimator localDisplayContentsAnimator2 = (DisplayContentsAnimator)this.mDisplayContentsAnimators.get(paramInt);
    DisplayContentsAnimator localDisplayContentsAnimator1 = localDisplayContentsAnimator2;
    if (localDisplayContentsAnimator2 == null)
    {
      localDisplayContentsAnimator1 = new DisplayContentsAnimator(null);
      this.mDisplayContentsAnimators.put(paramInt, localDisplayContentsAnimator1);
    }
    return localDisplayContentsAnimator1;
  }
  
  private WindowState getWinShowWhenLockedOrAnimating()
  {
    WindowState localWindowState = (WindowState)this.mPolicy.getWinShowWhenLockedLw();
    if (localWindowState != null) {
      return localWindowState;
    }
    if ((this.mLastShowWinWhenLocked != null) && (this.mLastShowWinWhenLocked.isOnScreen()) && (this.mLastShowWinWhenLocked.isAnimatingLw()) && ((this.mLastShowWinWhenLocked.mAttrs.flags & 0x80000) != 0)) {
      return this.mLastShowWinWhenLocked;
    }
    return null;
  }
  
  private void removeReplacedWindowsLocked()
  {
    if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
      Slog.i(TAG, ">>> OPEN TRANSACTION removeReplacedWindows");
    }
    SurfaceControl.openTransaction();
    try
    {
      int i = this.mService.mDisplayContents.size() - 1;
      while (i >= 0)
      {
        Object localObject1 = (DisplayContent)this.mService.mDisplayContents.valueAt(i);
        localObject1 = this.mService.getWindowListLocked(((DisplayContent)localObject1).getDisplayId());
        int j = ((WindowList)localObject1).size() - 1;
        while (j >= 0)
        {
          ((WindowState)((WindowList)localObject1).get(j)).maybeRemoveReplacedWindow();
          j -= 1;
        }
        i -= 1;
      }
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        Slog.i(TAG, "<<< CLOSE TRANSACTION removeReplacedWindows");
      }
      this.mRemoveReplacedWindows = false;
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        Slog.i(TAG, "<<< CLOSE TRANSACTION removeReplacedWindows");
      }
    }
  }
  
  private boolean shouldForceHide(WindowState paramWindowState)
  {
    boolean bool5 = false;
    WindowState localWindowState = this.mService.mInputMethodTarget;
    boolean bool1;
    Object localObject;
    label75:
    label88:
    boolean bool4;
    if ((localWindowState != null) && (localWindowState.isVisibleNow())) {
      if ((localWindowState.getAttrs().flags & 0x80000) == 0)
      {
        if (!this.mPolicy.canBeForceHidden(localWindowState, localWindowState.mAttrs)) {
          break label244;
        }
        bool1 = false;
        localObject = getWinShowWhenLockedOrAnimating();
        if (localObject != null) {
          break label254;
        }
        localObject = null;
        if ((!paramWindowState.mIsImWindow) && (localWindowState != paramWindowState)) {
          break label264;
        }
        if ((paramWindowState.mAttrs.flags & 0x80000) == 0) {
          break label269;
        }
        bool4 = paramWindowState.mTurnOnScreen;
        label108:
        boolean bool3 = bool1 | bool4;
        boolean bool2 = bool3;
        if (localObject != null)
        {
          if ((localObject != paramWindowState.mAppToken) && ((paramWindowState.mAttrs.flags & 0x80000) == 0)) {
            break label275;
          }
          bool1 = true;
          label147:
          bool2 = bool3 | bool1;
        }
        if ((paramWindowState.mAttrs.flags & 0x400000) != 0) {
          bool5 = this.mPolicy.canShowDismissingWindowWhileLockedLw();
        }
        if (!this.mPolicy.isKeyguardShowingOrOccluded()) {
          break label304;
        }
        if (this.mForceHiding == 2) {
          break label299;
        }
        bool1 = true;
        label199:
        if (paramWindowState.mAttrs.type != 2034) {
          break label315;
        }
        if (paramWindowState.getDisplayContent().getDockedStackLocked() != null) {
          break label309;
        }
        bool4 = true;
        label225:
        if ((bool1) && (!(bool2 | bool5))) {
          break label321;
        }
      }
    }
    label244:
    label254:
    label264:
    label269:
    label275:
    label299:
    label304:
    label309:
    label315:
    label321:
    while (paramWindowState.getDisplayId() != 0)
    {
      return bool4;
      bool1 = true;
      break;
      bool1 = true;
      break;
      bool1 = false;
      break;
      localObject = ((WindowState)localObject).mAppToken;
      break label75;
      bool1 = false;
      break label88;
      bool4 = false;
      break label108;
      if ((paramWindowState.mAttrs.privateFlags & 0x100) != 0)
      {
        bool1 = true;
        break label147;
      }
      bool1 = false;
      break label147;
      bool1 = false;
      break label199;
      bool1 = false;
      break label199;
      bool4 = false;
      break label225;
      bool4 = false;
      break label225;
    }
    return true;
  }
  
  private void testTokenMayBeDrawnLocked(int paramInt)
  {
    ArrayList localArrayList = this.mService.getDisplayContentLocked(paramInt).getTasks();
    int k = localArrayList.size();
    int i = 0;
    while (i < k)
    {
      AppTokenList localAppTokenList = ((Task)localArrayList.get(i)).mAppTokens;
      int m = localAppTokenList.size();
      int j = 0;
      if (j < m)
      {
        AppWindowToken localAppWindowToken = (AppWindowToken)localAppTokenList.get(j);
        AppWindowAnimator localAppWindowAnimator = localAppWindowToken.mAppAnimator;
        boolean bool = localAppWindowToken.allDrawn;
        if (bool != localAppWindowAnimator.allDrawn)
        {
          localAppWindowAnimator.allDrawn = bool;
          if (bool)
          {
            if (!localAppWindowAnimator.freezingScreen) {
              break label209;
            }
            localAppWindowAnimator.showAllWindowsLocked();
            this.mService.unsetAppFreezingScreenLocked(localAppWindowToken, false, true);
            if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
              Slog.i(TAG, "Setting mOrientationChangeComplete=true because wtoken " + localAppWindowToken + " numInteresting=" + localAppWindowToken.numInterestingWindows + " numDrawn=" + localAppWindowToken.numDrawnWindows);
            }
            setAppLayoutChanges(localAppWindowAnimator, 4, "testTokenMayBeDrawnLocked: freezingScreen", paramInt);
          }
        }
        for (;;)
        {
          j += 1;
          break;
          label209:
          setAppLayoutChanges(localAppWindowAnimator, 8, "testTokenMayBeDrawnLocked", paramInt);
          if (!this.mService.mOpeningApps.contains(localAppWindowToken)) {
            orAnimating(localAppWindowAnimator.showAllWindowsLocked());
          }
        }
      }
      i += 1;
    }
  }
  
  private void updateAppWindowsLocked(int paramInt)
  {
    ArrayList localArrayList = this.mService.getDisplayContentLocked(paramInt).getStacks();
    int i = localArrayList.size() - 1;
    while (i >= 0)
    {
      TaskStack localTaskStack = (TaskStack)localArrayList.get(i);
      Object localObject = localTaskStack.getTasks();
      int j = ((ArrayList)localObject).size() - 1;
      while (j >= 0)
      {
        AppTokenList localAppTokenList = ((Task)((ArrayList)localObject).get(j)).mAppTokens;
        k = localAppTokenList.size() - 1;
        if (k >= 0)
        {
          AppWindowAnimator localAppWindowAnimator = ((AppWindowToken)localAppTokenList.get(k)).mAppAnimator;
          localAppWindowAnimator.wasAnimating = localAppWindowAnimator.animating;
          if (localAppWindowAnimator.stepAnimationLocked(this.mCurrentTime, paramInt))
          {
            localAppWindowAnimator.animating = true;
            setAnimating(true);
            this.mAppWindowAnimating = true;
          }
          for (;;)
          {
            k -= 1;
            break;
            if (localAppWindowAnimator.wasAnimating)
            {
              setAppLayoutChanges(localAppWindowAnimator, 4, "appToken " + localAppWindowAnimator.mAppToken + " done", paramInt);
              if (WindowManagerDebugConfig.DEBUG_ANIM) {
                Slog.v(TAG, "updateWindowsApps...: done animating " + localAppWindowAnimator.mAppToken);
              }
            }
          }
        }
        j -= 1;
      }
      this.mTmpExitingAppTokens.clear();
      this.mTmpExitingAppTokens.addAll(localTaskStack.mExitingAppTokens);
      int k = this.mTmpExitingAppTokens.size();
      j = 0;
      if (j < k)
      {
        localObject = ((AppWindowToken)this.mTmpExitingAppTokens.get(j)).mAppAnimator;
        if (!localTaskStack.mExitingAppTokens.contains(localObject)) {}
        for (;;)
        {
          j += 1;
          break;
          ((AppWindowAnimator)localObject).wasAnimating = ((AppWindowAnimator)localObject).animating;
          if (((AppWindowAnimator)localObject).stepAnimationLocked(this.mCurrentTime, paramInt))
          {
            setAnimating(true);
            this.mAppWindowAnimating = true;
          }
          else if (((AppWindowAnimator)localObject).wasAnimating)
          {
            setAppLayoutChanges((AppWindowAnimator)localObject, 4, "exiting appToken " + ((AppWindowAnimator)localObject).mAppToken + " done", paramInt);
            if (WindowManagerDebugConfig.DEBUG_ANIM) {
              Slog.v(TAG, "updateWindowsApps...: done animating exiting " + ((AppWindowAnimator)localObject).mAppToken);
            }
          }
        }
      }
      i -= 1;
    }
  }
  
  private void updateWallpaperLocked(int paramInt)
  {
    this.mService.getDisplayContentLocked(paramInt).resetAnimationBackgroundAnimator();
    WindowList localWindowList = this.mService.getWindowListLocked(paramInt);
    Object localObject1 = null;
    paramInt = localWindowList.size() - 1;
    while (paramInt >= 0)
    {
      WindowState localWindowState = (WindowState)localWindowList.get(paramInt);
      WindowStateAnimator localWindowStateAnimator = localWindowState.mWinAnimator;
      Object localObject3 = localObject1;
      if (localWindowStateAnimator.mSurfaceController != null)
      {
        localObject3 = localObject1;
        if (localWindowStateAnimator.hasSurface())
        {
          int i = localWindowState.mAttrs.flags;
          Object localObject2 = localObject1;
          if (localWindowStateAnimator.mAnimating)
          {
            localObject3 = localObject1;
            if (localWindowStateAnimator.mAnimation != null)
            {
              localObject2 = localObject1;
              if ((i & 0x100000) != 0)
              {
                localObject2 = localObject1;
                if (localWindowStateAnimator.mAnimation.getDetachWallpaper()) {
                  localObject2 = localWindowState;
                }
              }
              int j = localWindowStateAnimator.mAnimation.getBackgroundColor();
              localObject3 = localObject2;
              if (j != 0)
              {
                localObject1 = localWindowState.getStack();
                localObject3 = localObject2;
                if (localObject1 != null)
                {
                  ((TaskStack)localObject1).setAnimationBackground(localWindowStateAnimator, j);
                  localObject3 = localObject2;
                }
              }
            }
            setAnimating(true);
            localObject2 = localObject3;
          }
          AppWindowAnimator localAppWindowAnimator = localWindowStateAnimator.mAppAnimator;
          localObject3 = localObject2;
          if (localAppWindowAnimator != null)
          {
            localObject3 = localObject2;
            if (localAppWindowAnimator.animation != null)
            {
              localObject3 = localObject2;
              if (localAppWindowAnimator.animating)
              {
                localObject1 = localObject2;
                if ((i & 0x100000) != 0)
                {
                  localObject1 = localObject2;
                  if (localAppWindowAnimator.animation.getDetachWallpaper()) {
                    localObject1 = localWindowState;
                  }
                }
                i = localAppWindowAnimator.animation.getBackgroundColor();
                localObject3 = localObject1;
                if (i != 0)
                {
                  localObject2 = localWindowState.getStack();
                  localObject3 = localObject1;
                  if (localObject2 != null)
                  {
                    ((TaskStack)localObject2).setAnimationBackground(localWindowStateAnimator, i);
                    localObject3 = localObject1;
                  }
                }
              }
            }
          }
        }
      }
      paramInt -= 1;
      localObject1 = localObject3;
    }
    if (this.mWindowDetachedWallpaper != localObject1)
    {
      if (WindowManagerDebugConfig.DEBUG_WALLPAPER) {
        Slog.v(TAG, "Detached wallpaper changed from " + this.mWindowDetachedWallpaper + " to " + localObject1);
      }
      this.mWindowDetachedWallpaper = ((WindowState)localObject1);
      this.mBulkUpdateParams |= 0x2;
    }
  }
  
  private void updateWindowsLocked(int paramInt)
  {
    this.mAnimTransactionSequence += 1;
    WindowList localWindowList = this.mService.getWindowListLocked(paramInt);
    boolean bool1;
    int i2;
    label44:
    boolean bool2;
    if ((this.mKeyguardGoingAwayFlags & 0x1) != 0)
    {
      bool1 = true;
      if ((this.mKeyguardGoingAwayFlags & 0x2) == 0) {
        break label116;
      }
      i2 = 1;
      if ((this.mKeyguardGoingAwayFlags & 0x4) == 0) {
        break label122;
      }
      bool2 = true;
    }
    label56:
    label116:
    label122:
    Object localObject2;
    for (;;)
    {
      if (this.mKeyguardGoingAway)
      {
        i = localWindowList.size() - 1;
        for (;;)
        {
          if (i >= 0)
          {
            localObject1 = (WindowState)localWindowList.get(i);
            if (!this.mPolicy.isKeyguardHostWindow(((WindowState)localObject1).mAttrs))
            {
              i -= 1;
              continue;
              bool1 = false;
              break;
              i2 = 0;
              break label44;
              bool2 = false;
              break label56;
            }
            localObject2 = ((WindowState)localObject1).mWinAnimator;
            if ((((WindowState)localObject1).mAttrs.privateFlags & 0x400) == 0) {
              break label932;
            }
            if (!((WindowStateAnimator)localObject2).mAnimating)
            {
              if (WindowManagerDebugConfig.DEBUG_KEYGUARD) {
                Slog.d(TAG, "updateWindowsLocked: creating delay animation");
              }
              ((WindowStateAnimator)localObject2).mAnimation = new AlphaAnimation(1.0F, 1.0F);
              ((WindowStateAnimator)localObject2).mAnimation.setDuration(1000L);
              ((WindowStateAnimator)localObject2).mAnimationIsEntrance = false;
              ((WindowStateAnimator)localObject2).mAnimationStartTime = -1L;
              ((WindowStateAnimator)localObject2).mKeyguardGoingAwayAnimation = true;
              ((WindowStateAnimator)localObject2).mKeyguardGoingAwayWithWallpaper = bool2;
            }
          }
        }
      }
    }
    this.mForceHiding = 0;
    int i = 0;
    int j = 0;
    Object localObject1 = null;
    Object localObject4 = null;
    WallpaperController localWallpaperController = this.mService.mWallpaperControllerLocked;
    int i3 = localWindowList.size() - 1;
    label259:
    boolean bool3;
    label421:
    label539:
    int k;
    int m;
    label734:
    Object localObject3;
    if (i3 >= 0)
    {
      WindowState localWindowState = (WindowState)localWindowList.get(i3);
      WindowStateAnimator localWindowStateAnimator = localWindowState.mWinAnimator;
      int i5 = localWindowState.mAttrs.flags;
      bool3 = this.mPolicy.canBeForceHidden(localWindowState, localWindowState.mAttrs);
      boolean bool4 = shouldForceHide(localWindowState);
      boolean bool6;
      if (localWindowStateAnimator.hasSurface())
      {
        boolean bool5 = localWindowStateAnimator.mWasAnimating;
        bool6 = localWindowStateAnimator.stepAnimationLocked(this.mCurrentTime);
        localWindowStateAnimator.mWasAnimating = bool6;
        orAnimating(bool6);
        if (WindowManagerDebugConfig.DEBUG_WALLPAPER) {
          Slog.v(TAG, localWindowState + ": wasAnimating=" + bool5 + ", nowAnimating=" + bool6);
        }
        if ((!bool5) || (localWindowStateAnimator.mAnimating))
        {
          if (!this.mPolicy.isForceHiding(localWindowState.mAttrs)) {
            break label1078;
          }
          if ((bool5) || (!bool6)) {
            break label1011;
          }
          if ((WindowManagerDebugConfig.DEBUG_KEYGUARD) || (WindowManagerDebugConfig.DEBUG_ANIM) || (WindowManagerDebugConfig.DEBUG_VISIBILITY)) {
            Slog.v(TAG, "Animation started that could impact force hide: " + localWindowState);
          }
          this.mBulkUpdateParams |= 0x4;
          setPendingLayoutChanges(paramInt, 4);
          if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
            this.mWindowPlacerLocked.debugLayoutRepeats("updateWindowsAndWallpaperLocked 3", getPendingLayoutChanges(paramInt));
          }
          this.mService.mFocusMayChange = true;
          if (localWindowState.isReadyForDisplay())
          {
            if ((!bool6) || (!localWindowState.mWinAnimator.mKeyguardGoingAwayAnimation)) {
              break label1052;
            }
            this.mForceHiding = 2;
          }
          if (!WindowManagerDebugConfig.DEBUG_KEYGUARD)
          {
            k = j;
            localObject2 = localObject1;
            m = i;
            if (!WindowManagerDebugConfig.DEBUG_VISIBILITY) {}
          }
          else
          {
            Slog.v(TAG, "Force hide " + forceHidingToString() + " hasSurface=" + localWindowState.mHasSurface + " policyVis=" + localWindowState.mPolicyVisibility + " destroying=" + localWindowState.mDestroying + " attHidden=" + localWindowState.mAttachedHidden + " vis=" + localWindowState.mViewVisibility + " hidden=" + localWindowState.mRootToken.hidden + " anim=" + localWindowState.mWinAnimator.mAnimation);
            m = i;
            localObject2 = localObject1;
            k = j;
          }
        }
      }
      for (;;)
      {
        localObject1 = localWindowState.mAppToken;
        if ((localWindowStateAnimator.mDrawState == 3) && ((localObject1 == null) || (((AppWindowToken)localObject1).allDrawn)) && (localWindowStateAnimator.performShowLocked()))
        {
          setPendingLayoutChanges(paramInt, 8);
          if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
            this.mWindowPlacerLocked.debugLayoutRepeats("updateWindowsAndWallpaperLocked 5", getPendingLayoutChanges(paramInt));
          }
        }
        localObject1 = localWindowStateAnimator.mAppAnimator;
        if ((localObject1 != null) && (((AppWindowAnimator)localObject1).thumbnail != null))
        {
          if (((AppWindowAnimator)localObject1).thumbnailTransactionSeq != this.mAnimTransactionSequence)
          {
            ((AppWindowAnimator)localObject1).thumbnailTransactionSeq = this.mAnimTransactionSequence;
            ((AppWindowAnimator)localObject1).thumbnailLayer = 0;
          }
          if (((AppWindowAnimator)localObject1).thumbnailLayer < localWindowStateAnimator.mAnimLayer) {
            ((AppWindowAnimator)localObject1).thumbnailLayer = localWindowStateAnimator.mAnimLayer;
          }
        }
        int i1 = k;
        localObject3 = localObject2;
        Object localObject5 = localObject4;
        int i4 = m;
        if (localWindowState.mIsWallpaper)
        {
          localObject5 = localWindowState;
          i4 = m;
          localObject3 = localObject2;
          i1 = k;
        }
        label932:
        label1011:
        label1052:
        label1078:
        do
        {
          i3 -= 1;
          j = i1;
          localObject1 = localObject3;
          localObject4 = localObject5;
          i = i4;
          break label259;
          if (WindowManagerDebugConfig.DEBUG_KEYGUARD) {
            Slog.d(TAG, "updateWindowsLocked: StatusBar is no longer keyguard");
          }
          this.mKeyguardGoingAway = false;
          ((WindowStateAnimator)localObject2).clearAnimation();
          break;
          if (!localWallpaperController.isWallpaperTarget(localWindowState)) {
            break label421;
          }
          this.mBulkUpdateParams |= 0x2;
          setPendingLayoutChanges(0, 4);
          if (!WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
            break label421;
          }
          this.mWindowPlacerLocked.debugLayoutRepeats("updateWindowsAndWallpaperLocked 2", getPendingLayoutChanges(0));
          break label421;
          if ((!this.mKeyguardGoingAway) || (bool6)) {
            break label539;
          }
          Slog.e(TAG, "Timeout waiting for animation to startup");
          this.mPolicy.startKeyguardExitAnimation(0L, 0L);
          this.mKeyguardGoingAway = false;
          break label539;
          if (localWindowState.isDrawnLw()) {}
          for (k = 1;; k = 0)
          {
            this.mForceHiding = k;
            break;
          }
          k = j;
          localObject2 = localObject1;
          m = i;
          if (!bool3) {
            break label734;
          }
          if (!bool4) {
            break label1267;
          }
          i1 = j;
          localObject3 = localObject1;
          localObject5 = localObject4;
          i4 = i;
        } while (!localWindowState.hideLw(false, false));
        int n;
        if (!WindowManagerDebugConfig.DEBUG_KEYGUARD)
        {
          n = j;
          localObject3 = localObject1;
          i1 = i;
          if (!WindowManagerDebugConfig.DEBUG_VISIBILITY) {}
        }
        else
        {
          Slog.v(TAG, "Now policy hidden: " + localWindowState);
          i1 = i;
          localObject3 = localObject1;
          n = j;
        }
        k = n;
        localObject2 = localObject3;
        m = i1;
        if ((0x100000 & i5) != 0)
        {
          this.mBulkUpdateParams |= 0x2;
          setPendingLayoutChanges(0, 4);
          k = n;
          localObject2 = localObject3;
          m = i1;
          if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS)
          {
            this.mWindowPlacerLocked.debugLayoutRepeats("updateWindowsAndWallpaperLocked 4", getPendingLayoutChanges(0));
            k = n;
            localObject2 = localObject3;
            m = i1;
            continue;
            label1267:
            if ((this.mPostKeyguardExitAnimation == null) || (this.mPostKeyguardExitAnimation.hasEnded()))
            {
              label1284:
              n = 0;
              label1287:
              if ((!localWindowState.showLw(false, false)) && (n == 0)) {
                break label1383;
              }
            }
            for (;;)
            {
              if (localWindowState.isVisibleNow()) {
                break label1481;
              }
              localWindowState.hideLw(false, false);
              i1 = j;
              localObject3 = localObject1;
              localObject5 = localObject4;
              i4 = i;
              break;
              if ((localWindowStateAnimator.mKeyguardGoingAwayAnimation) || (!localWindowState.hasDrawnLw()) || (localWindowState.mAttachedWindow != null) || (localWindowState.mIsImWindow)) {
                break label1284;
              }
              if (paramInt == 0)
              {
                n = 1;
                break label1287;
              }
              n = 0;
              break label1287;
              label1383:
              i1 = j;
              localObject3 = localObject1;
              localObject5 = localObject4;
              i4 = i;
              if (!this.mKeyguardGoingAway) {
                break;
              }
              i1 = j;
              localObject3 = localObject1;
              localObject5 = localObject4;
              i4 = i;
              if (this.mForceHiding != 2) {
                break;
              }
              Slog.v(TAG, "Keyguard is going away and animating win=" + localWindowState + " isVisibleNow=" + localWindowState.isVisibleNow());
              this.mBulkUpdateParams |= 0x4;
            }
            label1481:
            if ((WindowManagerDebugConfig.DEBUG_KEYGUARD) || (WindowManagerDebugConfig.DEBUG_VISIBILITY)) {
              Slog.v(TAG, "Now policy shown: " + localWindowState);
            }
            if (((this.mBulkUpdateParams & 0x4) != 0) && (localWindowState.mAttachedWindow == null))
            {
              localObject3 = localObject1;
              if (localObject1 == null) {
                localObject3 = new ArrayList();
              }
              ((ArrayList)localObject3).add(localWindowStateAnimator);
              if ((0x100000 & i5) != 0) {
                i = 1;
              }
              m = j;
              localObject2 = localObject3;
              k = i;
              if (localWindowState.mAttrs.type == 3)
              {
                m = 1;
                k = i;
                localObject2 = localObject3;
              }
            }
            for (;;)
            {
              localObject1 = this.mService.mCurrentFocus;
              if (localObject1 != null)
              {
                n = m;
                localObject3 = localObject2;
                i1 = k;
                if (((WindowState)localObject1).mLayer >= localWindowState.mLayer) {
                  break;
                }
              }
              if (WindowManagerDebugConfig.DEBUG_FOCUS_LIGHT) {
                Slog.v(TAG, "updateWindowsLocked: setting mFocusMayChange true");
              }
              this.mService.mFocusMayChange = true;
              n = m;
              localObject3 = localObject2;
              i1 = k;
              break;
              m = j;
              localObject2 = localObject1;
              k = i;
              if (n != 0)
              {
                if (WindowManagerDebugConfig.DEBUG_KEYGUARD) {
                  Slog.v(TAG, "Applying existing Keyguard exit animation to new window: win=" + localWindowState);
                }
                localWindowStateAnimator.setAnimation(this.mPolicy.createForceHideEnterAnimation(false, bool1), this.mPostKeyguardExitAnimation.getStartTime(), 1);
                localWindowStateAnimator.mKeyguardGoingAwayAnimation = true;
                localWindowStateAnimator.mKeyguardGoingAwayWithWallpaper = bool2;
                m = j;
                localObject2 = localObject1;
                k = i;
              }
            }
            k = j;
            localObject2 = localObject1;
            m = i;
            if (bool3) {
              if (bool4)
              {
                localWindowState.hideLw(false, false);
                k = j;
                localObject2 = localObject1;
                m = i;
              }
              else
              {
                localWindowState.showLw(false, false);
                k = j;
                localObject2 = localObject1;
                m = i;
              }
            }
          }
        }
      }
    }
    if (localObject1 != null)
    {
      if (i2 == 0)
      {
        k = 1;
        paramInt = ((ArrayList)localObject1).size() - 1;
        if (paramInt >= 0)
        {
          localObject2 = (WindowStateAnimator)((ArrayList)localObject1).get(paramInt);
          localObject3 = this.mPolicy;
          if ((i == 0) || (j != 0)) {}
          for (bool3 = false;; bool3 = true)
          {
            localObject3 = ((WindowManagerPolicy)localObject3).createForceHideEnterAnimation(bool3, bool1);
            m = k;
            if (localObject3 != null)
            {
              if (WindowManagerDebugConfig.DEBUG_KEYGUARD) {
                Slog.v(TAG, "Starting keyguard exit animation on window " + ((WindowStateAnimator)localObject2).mWin);
              }
              ((WindowStateAnimator)localObject2).setAnimation((Animation)localObject3, 1);
              ((WindowStateAnimator)localObject2).mKeyguardGoingAwayAnimation = true;
              ((WindowStateAnimator)localObject2).mKeyguardGoingAwayWithWallpaper = bool2;
              m = k;
              if (k != 0)
              {
                this.mPostKeyguardExitAnimation = ((Animation)localObject3);
                this.mPostKeyguardExitAnimation.setStartTime(this.mCurrentTime);
                m = 0;
              }
            }
            paramInt -= 1;
            k = m;
            break;
          }
        }
      }
      else if (this.mKeyguardGoingAway)
      {
        this.mPolicy.startKeyguardExitAnimation(this.mCurrentTime, 0L);
        this.mKeyguardGoingAway = false;
      }
      if ((i == 0) && (localObject4 != null) && (i2 == 0)) {}
    }
    else if (this.mPostKeyguardExitAnimation != null)
    {
      if (!this.mKeyguardGoingAway) {
        break label2186;
      }
      this.mPolicy.startKeyguardExitAnimation(this.mCurrentTime + this.mPostKeyguardExitAnimation.getStartOffset(), this.mPostKeyguardExitAnimation.getDuration());
      this.mKeyguardGoingAway = false;
    }
    for (;;)
    {
      localObject1 = (WindowState)this.mPolicy.getWinShowWhenLockedLw();
      if (localObject1 != null) {
        this.mLastShowWinWhenLocked = ((WindowState)localObject1);
      }
      return;
      if (WindowManagerDebugConfig.DEBUG_KEYGUARD) {
        Slog.d(TAG, "updateWindowsLocked: wallpaper animating away");
      }
      localObject1 = this.mPolicy.createForceHideWallpaperExitAnimation(bool1);
      if (localObject1 == null) {
        break;
      }
      ((WindowState)localObject4).mWinAnimator.setAnimation((Animation)localObject1);
      break;
      label2186:
      if ((this.mPostKeyguardExitAnimation.hasEnded()) || (this.mCurrentTime - this.mPostKeyguardExitAnimation.getStartTime() > this.mPostKeyguardExitAnimation.getDuration()))
      {
        if (WindowManagerDebugConfig.DEBUG_KEYGUARD) {
          Slog.v(TAG, "Done with Keyguard exit animations.");
        }
        this.mPostKeyguardExitAnimation = null;
      }
    }
  }
  
  void addDisplayLocked(int paramInt)
  {
    getDisplayContentsAnimatorLocked(paramInt);
    if (paramInt == 0) {
      this.mInitialized = true;
    }
  }
  
  public void dumpLocked(PrintWriter paramPrintWriter, String paramString, boolean paramBoolean)
  {
    String str1 = "  " + paramString;
    String str2 = "  " + str1;
    int i = 0;
    if (i < this.mDisplayContentsAnimators.size())
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("DisplayContentsAnimator #");
      paramPrintWriter.print(this.mDisplayContentsAnimators.keyAt(i));
      paramPrintWriter.println(":");
      DisplayContentsAnimator localDisplayContentsAnimator = (DisplayContentsAnimator)this.mDisplayContentsAnimators.valueAt(i);
      WindowList localWindowList = this.mService.getWindowListLocked(this.mDisplayContentsAnimators.keyAt(i));
      int k = localWindowList.size();
      int j = 0;
      while (j < k)
      {
        WindowStateAnimator localWindowStateAnimator = ((WindowState)localWindowList.get(j)).mWinAnimator;
        paramPrintWriter.print(str1);
        paramPrintWriter.print("Window #");
        paramPrintWriter.print(j);
        paramPrintWriter.print(": ");
        paramPrintWriter.println(localWindowStateAnimator);
        j += 1;
      }
      if (localDisplayContentsAnimator.mScreenRotationAnimation != null)
      {
        paramPrintWriter.print(str1);
        paramPrintWriter.println("mScreenRotationAnimation:");
        localDisplayContentsAnimator.mScreenRotationAnimation.printTo(str2, paramPrintWriter);
      }
      for (;;)
      {
        paramPrintWriter.println();
        i += 1;
        break;
        if (paramBoolean)
        {
          paramPrintWriter.print(str1);
          paramPrintWriter.println("no ScreenRotationAnimation ");
        }
      }
    }
    paramPrintWriter.println();
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mAnimTransactionSequence=");
      paramPrintWriter.print(this.mAnimTransactionSequence);
      paramPrintWriter.print(" mForceHiding=");
      paramPrintWriter.println(forceHidingToString());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCurrentTime=");
      paramPrintWriter.println(TimeUtils.formatUptime(this.mCurrentTime));
    }
    if (this.mBulkUpdateParams != 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mBulkUpdateParams=0x");
      paramPrintWriter.print(Integer.toHexString(this.mBulkUpdateParams));
      paramPrintWriter.println(bulkUpdateParamsToString(this.mBulkUpdateParams));
    }
    if (this.mWindowDetachedWallpaper != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mWindowDetachedWallpaper=");
      paramPrintWriter.println(this.mWindowDetachedWallpaper);
    }
  }
  
  int getPendingLayoutChanges(int paramInt)
  {
    int i = 0;
    if (paramInt < 0) {
      return 0;
    }
    DisplayContent localDisplayContent = this.mService.getDisplayContentLocked(paramInt);
    paramInt = i;
    if (localDisplayContent != null) {
      paramInt = localDisplayContent.pendingLayoutChanges;
    }
    return paramInt;
  }
  
  ScreenRotationAnimation getScreenRotationAnimationLocked(int paramInt)
  {
    if (paramInt < 0) {
      return null;
    }
    return getDisplayContentsAnimatorLocked(paramInt).mScreenRotationAnimation;
  }
  
  boolean isAnimating()
  {
    return this.mAnimating;
  }
  
  void orAnimating(boolean paramBoolean)
  {
    this.mAnimating |= paramBoolean;
  }
  
  void removeDisplayLocked(int paramInt)
  {
    DisplayContentsAnimator localDisplayContentsAnimator = (DisplayContentsAnimator)this.mDisplayContentsAnimators.get(paramInt);
    if ((localDisplayContentsAnimator != null) && (localDisplayContentsAnimator.mScreenRotationAnimation != null))
    {
      localDisplayContentsAnimator.mScreenRotationAnimation.kill();
      localDisplayContentsAnimator.mScreenRotationAnimation = null;
    }
    this.mDisplayContentsAnimators.delete(paramInt);
  }
  
  void requestRemovalOfReplacedWindows(WindowState paramWindowState)
  {
    this.mRemoveReplacedWindows = true;
  }
  
  void setAnimating(boolean paramBoolean)
  {
    this.mAnimating = paramBoolean;
  }
  
  void setAppLayoutChanges(AppWindowAnimator paramAppWindowAnimator, int paramInt1, String paramString, int paramInt2)
  {
    paramAppWindowAnimator = paramAppWindowAnimator.mAppToken.allAppWindows;
    int i = paramAppWindowAnimator.size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        if (paramInt2 != ((WindowState)paramAppWindowAnimator.get(i)).getDisplayId()) {
          break label66;
        }
        setPendingLayoutChanges(paramInt2, paramInt1);
        if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
          this.mWindowPlacerLocked.debugLayoutRepeats(paramString, getPendingLayoutChanges(paramInt2));
        }
      }
      return;
      label66:
      i -= 1;
    }
  }
  
  void setPendingLayoutChanges(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      return;
    }
    DisplayContent localDisplayContent = this.mService.getDisplayContentLocked(paramInt1);
    if (localDisplayContent != null) {
      localDisplayContent.pendingLayoutChanges |= paramInt2;
    }
  }
  
  void setScreenRotationAnimationLocked(int paramInt, ScreenRotationAnimation paramScreenRotationAnimation)
  {
    if (paramInt >= 0) {
      getDisplayContentsAnimatorLocked(paramInt).mScreenRotationAnimation = paramScreenRotationAnimation;
    }
  }
  
  private class DisplayContentsAnimator
  {
    ScreenRotationAnimation mScreenRotationAnimation = null;
    
    private DisplayContentsAnimator() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/WindowAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */