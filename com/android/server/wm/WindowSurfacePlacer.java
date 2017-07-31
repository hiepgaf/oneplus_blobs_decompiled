package com.android.server.wm;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.display.DisplayManagerInternal;
import android.os.Build;
import android.os.Debug;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.provider.Settings.Global;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.IWindow;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceControl;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerPolicy;
import android.view.WindowManagerPolicy.WindowState;
import android.view.animation.Animation;
import com.android.server.am.OnePlusProcessManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class WindowSurfacePlacer
{
  static final int SET_FORCE_HIDING_CHANGED = 4;
  static final int SET_ORIENTATION_CHANGE_COMPLETE = 8;
  static final int SET_TURN_ON_SCREEN = 16;
  static final int SET_UPDATE_ROTATION = 1;
  static final int SET_WALLPAPER_ACTION_PENDING = 32;
  static final int SET_WALLPAPER_MAY_CHANGE = 2;
  private static final String TAG = "WindowManager";
  private float mButtonBrightness = -1.0F;
  private int mDeferDepth = 0;
  private boolean mDisplayHasContent = false;
  private Session mHoldScreen = null;
  WindowState mHoldScreenWindow = null;
  private boolean mInLayout = false;
  private Object mLastWindowFreezeSource = null;
  private int mLayoutRepeatCount;
  private boolean mObscureApplicationContentOnSecondaryDisplays = false;
  private boolean mObscured = false;
  WindowState mObsuringWindow = null;
  boolean mOrientationChangeComplete = true;
  private final ArrayList<SurfaceControl> mPendingDestroyingSurfaces = new ArrayList();
  private int mPreferredModeId = 0;
  private float mPreferredRefreshRate = 0.0F;
  private float mScreenBrightness = -1.0F;
  private final WindowManagerService mService;
  private boolean mSustainedPerformanceModeCurrent = false;
  private boolean mSustainedPerformanceModeEnabled = false;
  private boolean mSyswin = false;
  private final Rect mTmpContentRect = new Rect();
  private final LayerAndToken mTmpLayerAndToken = new LayerAndToken(null);
  private final Rect mTmpStartRect = new Rect();
  private boolean mTraversalScheduled;
  private boolean mUpdateRotation = false;
  private long mUserActivityTimeout = -1L;
  boolean mWallpaperActionPending = false;
  private final WallpaperController mWallpaperControllerLocked;
  private boolean mWallpaperForceHidingChanged = false;
  boolean mWallpaperMayChange = false;
  
  public WindowSurfacePlacer(WindowManagerService paramWindowManagerService)
  {
    this.mService = paramWindowManagerService;
    this.mWallpaperControllerLocked = this.mService.mWallpaperControllerLocked;
  }
  
  private void applySurfaceChangesTransaction(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.mService.mWatermark != null) {
      this.mService.mWatermark.positionSurface(paramInt2, paramInt3);
    }
    if (this.mService.mStrictModeFlash != null) {
      this.mService.mStrictModeFlash.positionSurface(paramInt2, paramInt3);
    }
    if (this.mService.mCircularDisplayMask != null) {
      this.mService.mCircularDisplayMask.positionSurface(paramInt2, paramInt3, this.mService.mRotation);
    }
    if (this.mService.mEmulatorDisplayOverlay != null) {
      this.mService.mEmulatorDisplayOverlay.positionSurface(paramInt2, paramInt3, this.mService.mRotation);
    }
    paramInt3 = 0;
    int j = 0;
    for (;;)
    {
      DisplayContent localDisplayContent;
      int i;
      WindowList localWindowList;
      int i2;
      int n;
      int i1;
      int k;
      label210:
      int m;
      label237:
      label268:
      label284:
      WindowState localWindowState;
      Object localObject;
      label323:
      WindowStateAnimator localWindowStateAnimator;
      boolean bool;
      if (j < paramInt1)
      {
        localDisplayContent = (DisplayContent)this.mService.mDisplayContents.valueAt(j);
        i = 0;
        localWindowList = localDisplayContent.getWindowList();
        DisplayInfo localDisplayInfo = localDisplayContent.getDisplayInfo();
        i2 = localDisplayContent.getDisplayId();
        n = localDisplayInfo.logicalWidth;
        i1 = localDisplayInfo.logicalHeight;
        paramInt2 = localDisplayInfo.appWidth;
        paramInt2 = localDisplayInfo.appHeight;
        if (i2 == 0)
        {
          k = 1;
          this.mDisplayHasContent = false;
          this.mPreferredRefreshRate = 0.0F;
          this.mPreferredModeId = 0;
          paramInt2 = 0;
          m = paramInt2 + 1;
          if (m <= 6) {
            break label1440;
          }
          Slog.w(TAG, "Animation repeat aborted after too many iterations");
          localDisplayContent.layoutNeeded = false;
          this.mObscured = false;
          this.mSyswin = false;
          localDisplayContent.resetDimming();
          if (!this.mService.mLosingFocus.isEmpty()) {
            break label1760;
          }
          m = 0;
          i1 = localWindowList.size() - 1;
          paramInt2 = i;
          n = paramInt3;
          if (i1 < 0) {
            break label1989;
          }
          localWindowState = (WindowState)localWindowList.get(i1);
          localObject = localWindowState.getTask();
          if (localWindowState.mObscured == this.mObscured) {
            break label1766;
          }
          paramInt3 = 1;
          localWindowState.mObscured = this.mObscured;
          if (!this.mObscured) {
            handleNotObscuredLocked(localWindowState, localDisplayInfo);
          }
          localWindowState.applyDimLayerIfNeeded();
          if ((k != 0) && (paramInt3 != 0) && (this.mWallpaperControllerLocked.isWallpaperTarget(localWindowState)) && (localWindowState.isVisibleLw())) {
            this.mWallpaperControllerLocked.updateWallpaperVisibility();
          }
          localWindowStateAnimator = localWindowState.mWinAnimator;
          if (localWindowState.hasMoved())
          {
            paramInt3 = localWindowState.mFrame.left;
            i = localWindowState.mFrame.top;
            if (localObject == null) {
              break label1778;
            }
            if (((Task)localObject).mStack.isAdjustedForMinimizedDockedStack()) {
              break label1772;
            }
            bool = ((Task)localObject).mStack.isAdjustedForIme();
            label450:
            if ((this.mService.okToDisplay()) && ((localWindowState.mAttrs.privateFlags & 0x40) == 0) && (!localWindowState.isDragResizing())) {
              break label1784;
            }
            label482:
            if ((this.mService.mAccessibilityController != null) && (i2 == 0)) {
              this.mService.mAccessibilityController.onSomeWindowResizedOrMovedLocked();
            }
            if (OnePlusProcessManager.isSupportFrozenApp()) {
              OnePlusProcessManager.resumeProcessByUID_out(localWindowState.mOwnerUid, "moved");
            }
          }
        }
      }
      try
      {
        localWindowState.mClient.moved(paramInt3, i);
        localWindowState.mMovedByResize = false;
        localWindowState.mContentChanged = false;
        if (localWindowState.mHasSurface)
        {
          bool = localWindowStateAnimator.commitFinishDrawingLocked();
          if ((k != 0) && (bool))
          {
            if (localWindowState.mAttrs.type == 2023)
            {
              localDisplayContent.pendingLayoutChanges |= 0x1;
              if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
                debugLayoutRepeats("dream and commitFinishDrawingLocked true", localDisplayContent.pendingLayoutChanges);
              }
            }
            if ((localWindowState.mAttrs.flags & 0x100000) != 0)
            {
              if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) {
                Slog.v(TAG, "First draw done in potential wallpaper target " + localWindowState);
              }
              this.mWallpaperMayChange = true;
              localDisplayContent.pendingLayoutChanges |= 0x4;
              if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
                debugLayoutRepeats("wallpaper and commitFinishDrawingLocked true", localDisplayContent.pendingLayoutChanges);
              }
            }
          }
          if ((localWindowStateAnimator.isAnimationStarting()) || (localWindowStateAnimator.isWaitingForOpening())) {
            label719:
            localWindowStateAnimator.setSurfaceBoundariesLocked(paramBoolean);
          }
        }
        else
        {
          localObject = localWindowState.mAppToken;
          if ((WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) && (localObject != null) && (localWindowState == ((AppWindowToken)localObject).startingWindow)) {
            Slog.d(TAG, "updateWindows: starting " + localWindowState + " isOnScreen=" + localWindowState.isOnScreen() + " allDrawn=" + ((AppWindowToken)localObject).allDrawn + " freezingScreen=" + ((AppWindowToken)localObject).mAppAnimator.freezingScreen);
          }
          paramInt3 = paramInt2;
          if (localObject != null) {
            if ((((AppWindowToken)localObject).allDrawn) && (((AppWindowToken)localObject).allDrawnExcludingSaved))
            {
              paramInt3 = paramInt2;
              if (!((AppWindowToken)localObject).mAppAnimator.freezingScreen) {}
            }
            else
            {
              if (((AppWindowToken)localObject).lastTransactionSequence != this.mService.mTransactionSequence)
              {
                ((AppWindowToken)localObject).lastTransactionSequence = this.mService.mTransactionSequence;
                ((AppWindowToken)localObject).numDrawnWindows = 0;
                ((AppWindowToken)localObject).numInterestingWindows = 0;
                ((AppWindowToken)localObject).numInterestingWindowsExcludingSaved = 0;
                ((AppWindowToken)localObject).numDrawnWindowsExclusingSaved = 0;
                ((AppWindowToken)localObject).startingDisplayed = false;
              }
              i = paramInt2;
              if (!((AppWindowToken)localObject).allDrawn)
              {
                i = paramInt2;
                if (localWindowState.mightAffectAllDrawn(false))
                {
                  if ((WindowManagerDebugConfig.DEBUG_VISIBILITY) || (WindowManagerDebugConfig.DEBUG_ORIENTATION))
                  {
                    Slog.v(TAG, "Eval win " + localWindowState + ": isDrawn=" + localWindowState.isDrawnLw() + ", isAnimationSet=" + localWindowStateAnimator.isAnimationSet());
                    if (!localWindowState.isDrawnLw()) {
                      Slog.v(TAG, "Not displayed: s=" + localWindowStateAnimator.mSurfaceController + " pv=" + localWindowState.mPolicyVisibility + " mDrawState=" + localWindowStateAnimator.drawStateToString() + " ah=" + localWindowState.mAttachedHidden + " th=" + ((AppWindowToken)localObject).hiddenRequested + " a=" + localWindowStateAnimator.mAnimating);
                    }
                  }
                  if (localWindowState == ((AppWindowToken)localObject).startingWindow) {
                    break label1839;
                  }
                  i = paramInt2;
                  if (localWindowState.isInteresting())
                  {
                    ((AppWindowToken)localObject).numInterestingWindows += 1;
                    i = paramInt2;
                    if (localWindowState.isDrawnLw())
                    {
                      ((AppWindowToken)localObject).numDrawnWindows += 1;
                      if ((WindowManagerDebugConfig.DEBUG_VISIBILITY) || (WindowManagerDebugConfig.DEBUG_ORIENTATION)) {
                        Slog.v(TAG, "tokenMayBeDrawn: " + localObject + " w=" + localWindowState + " numInteresting=" + ((AppWindowToken)localObject).numInterestingWindows + " freezingScreen=" + ((AppWindowToken)localObject).mAppAnimator.freezingScreen + " mAppFreezing=" + localWindowState.mAppFreezing);
                      }
                      i = 1;
                    }
                  }
                }
              }
              label1279:
              paramInt3 = i;
              if (!((AppWindowToken)localObject).allDrawnExcludingSaved)
              {
                paramInt3 = i;
                if (localWindowState.mightAffectAllDrawn(true))
                {
                  paramInt3 = i;
                  if (localWindowState != ((AppWindowToken)localObject).startingWindow)
                  {
                    paramInt3 = i;
                    if (localWindowState.isInteresting())
                    {
                      ((AppWindowToken)localObject).numInterestingWindowsExcludingSaved += 1;
                      paramInt3 = i;
                      if (localWindowState.isDrawnLw()) {
                        if (!localWindowState.isAnimatingWithSavedSurface()) {
                          break label1875;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        for (paramInt3 = i;; paramInt3 = 1)
        {
          paramInt2 = n;
          if (k != 0)
          {
            paramInt2 = n;
            if (m != 0)
            {
              paramInt2 = n;
              if (localWindowState == this.mService.mCurrentFocus)
              {
                paramInt2 = n;
                if (localWindowState.isDisplayedLw()) {
                  paramInt2 = 1;
                }
              }
            }
          }
          this.mService.updateResizingWindows(localWindowState);
          i1 -= 1;
          n = paramInt2;
          paramInt2 = paramInt3;
          break label284;
          k = 0;
          break;
          label1440:
          if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
            debugLayoutRepeats("On entry to LockedInner", localDisplayContent.pendingLayoutChanges);
          }
          if (((localDisplayContent.pendingLayoutChanges & 0x4) != 0) && (this.mWallpaperControllerLocked.adjustWallpaperWindows()))
          {
            this.mService.mLayersController.assignLayersLocked(localWindowList);
            localDisplayContent.layoutNeeded = true;
          }
          if ((k != 0) && ((localDisplayContent.pendingLayoutChanges & 0x2) != 0))
          {
            if (WindowManagerDebugConfig.DEBUG_LAYOUT) {
              Slog.v(TAG, "Computing new config from layout");
            }
            if (this.mService.updateOrientationFromAppTokensLocked(true))
            {
              localDisplayContent.layoutNeeded = true;
              this.mService.mH.sendEmptyMessage(18);
            }
          }
          if ((localDisplayContent.pendingLayoutChanges & 0x1) != 0) {
            localDisplayContent.layoutNeeded = true;
          }
          if (m < 4) {
            if (m == 1)
            {
              bool = true;
              performLayoutLockedInner(localDisplayContent, bool, false);
            }
          }
          for (;;)
          {
            localDisplayContent.pendingLayoutChanges = 0;
            if (k == 0) {
              break label1746;
            }
            this.mService.mPolicy.beginPostLayoutPolicyLw(n, i1);
            paramInt2 = localWindowList.size() - 1;
            while (paramInt2 >= 0)
            {
              localWindowState = (WindowState)localWindowList.get(paramInt2);
              if (localWindowState.mHasSurface) {
                this.mService.mPolicy.applyPostLayoutPolicyLw(localWindowState, localWindowState.mAttrs, localWindowState.mAttachedWindow);
              }
              paramInt2 -= 1;
            }
            bool = false;
            break;
            Slog.w(TAG, "Layout repeat skipped after too many iterations");
          }
          localDisplayContent.pendingLayoutChanges |= this.mService.mPolicy.finishPostLayoutPolicyLw();
          if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
            debugLayoutRepeats("after finishPostLayoutPolicyLw", localDisplayContent.pendingLayoutChanges);
          }
          label1746:
          if (localDisplayContent.pendingLayoutChanges == 0) {
            break label237;
          }
          paramInt2 = m;
          break label210;
          label1760:
          m = 1;
          break label268;
          label1766:
          paramInt3 = 0;
          break label323;
          label1772:
          bool = true;
          break label450;
          label1778:
          bool = false;
          break label450;
          label1784:
          if ((bool) || ((localObject != null) && (!localWindowState.getTask().mStack.hasMovementAnimations())) || (localWindowState.mWinAnimator.mLastHidden)) {
            break label482;
          }
          localWindowStateAnimator.setMoveAnimation(paramInt3, i);
          break label482;
          localWindowStateAnimator.computeShownFrameLocked();
          break label719;
          label1839:
          i = paramInt2;
          if (!localWindowState.isDrawnLw()) {
            break label1279;
          }
          this.mService.mH.sendEmptyMessage(50);
          ((AppWindowToken)localObject).startingDisplayed = true;
          i = paramInt2;
          break label1279;
          label1875:
          ((AppWindowToken)localObject).numDrawnWindowsExclusingSaved += 1;
          if ((WindowManagerDebugConfig.DEBUG_VISIBILITY) || (WindowManagerDebugConfig.DEBUG_ORIENTATION)) {
            Slog.v(TAG, "tokenMayBeDrawnExcludingSaved: " + localObject + " w=" + localWindowState + " numInteresting=" + ((AppWindowToken)localObject).numInterestingWindowsExcludingSaved + " freezingScreen=" + ((AppWindowToken)localObject).mAppAnimator.freezingScreen + " mAppFreezing=" + localWindowState.mAppFreezing);
          }
        }
        label1989:
        this.mService.mDisplayManagerInternal.setDisplayProperties(i2, this.mDisplayHasContent, this.mPreferredRefreshRate, this.mPreferredModeId, true);
        this.mService.getDisplayContentLocked(i2).stopDimmingIfNeeded();
        if (paramInt2 != 0) {
          updateAllDrawnLocked(localDisplayContent);
        }
        j += 1;
        paramInt3 = n;
        continue;
        if (paramInt3 != 0) {
          this.mService.mH.sendEmptyMessage(3);
        }
        this.mService.mDisplayManagerInternal.performTraversalInTransactionFromWindowManager();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  private boolean checkIsScreenWindow(ArraySet<AppWindowToken> paramArraySet)
  {
    if (paramArraySet.size() >= 2)
    {
      int i = paramArraySet.size() - 1;
      while (i >= 0)
      {
        AppWindowToken localAppWindowToken = (AppWindowToken)paramArraySet.valueAt(i);
        if (!localAppWindowToken.appFullscreen)
        {
          Slog.v(TAG, "checkIsScreenWindow =" + localAppWindowToken + " closingApps = " + this.mService.mLastFocus);
          return false;
        }
        i -= 1;
      }
    }
    return true;
  }
  
  private void createThumbnailAppAnimator(int paramInt1, AppWindowToken paramAppWindowToken, int paramInt2, int paramInt3)
  {
    if (paramAppWindowToken == null) {}
    for (AppWindowAnimator localAppWindowAnimator = null; (localAppWindowAnimator == null) || (localAppWindowAnimator.animation == null); localAppWindowAnimator = paramAppWindowToken.mAppAnimator) {
      return;
    }
    int i = paramAppWindowToken.mTask.mTaskId;
    Bitmap localBitmap = this.mService.mAppTransition.getAppTransitionThumbnailHeader(i);
    if ((localBitmap == null) || (localBitmap.getConfig() == Bitmap.Config.ALPHA_8))
    {
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
        Slog.d(TAG, "No thumbnail header bitmap for: " + i);
      }
      return;
    }
    Rect localRect = new Rect(0, 0, localBitmap.getWidth(), localBitmap.getHeight());
    for (;;)
    {
      try
      {
        Object localObject1 = this.mService.getDefaultDisplayContentLocked();
        Object localObject2 = ((DisplayContent)localObject1).getDisplay();
        localObject1 = ((DisplayContent)localObject1).getDisplayInfo();
        SurfaceControl localSurfaceControl = new SurfaceControl(this.mService.mFxSession, "thumbnail anim", localRect.width(), localRect.height(), -3, 4);
        localSurfaceControl.setLayerStack(((Display)localObject2).getLayerStack());
        if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
          Slog.i(TAG, "  THUMBNAIL " + localSurfaceControl + ": CREATE");
        }
        localObject2 = new Surface();
        ((Surface)localObject2).copyFrom(localSurfaceControl);
        Canvas localCanvas = ((Surface)localObject2).lockCanvas(localRect);
        localCanvas.drawBitmap(localBitmap, 0.0F, 0.0F, null);
        ((Surface)localObject2).unlockCanvasAndPost(localCanvas);
        ((Surface)localObject2).release();
        if (this.mService.mAppTransition.isNextThumbnailTransitionAspectScaled())
        {
          localObject2 = paramAppWindowToken.findMainWindow();
          if (localObject2 != null)
          {
            paramAppWindowToken = ((WindowState)localObject2).getContentFrameLw();
            if (localObject2 != null)
            {
              localObject1 = ((WindowState)localObject2).mContentInsets;
              paramAppWindowToken = this.mService.mAppTransition.createThumbnailAspectScaleAnimationLocked(paramAppWindowToken, (Rect)localObject1, localBitmap, i, this.mService.mCurConfiguration.uiMode, this.mService.mCurConfiguration.orientation);
              localAppWindowAnimator.thumbnailForceAboveLayer = Math.max(paramInt2, paramInt3);
              if (!this.mService.mAppTransition.isNextThumbnailTransitionScaleUp()) {
                break label554;
              }
              bool = false;
              localAppWindowAnimator.deferThumbnailDestruction = bool;
              paramAppWindowToken.restrictDuration(10000L);
              paramAppWindowToken.scaleCurrentDuration(this.mService.getTransitionAnimationScaleLocked());
              localAppWindowAnimator.thumbnail = localSurfaceControl;
              localAppWindowAnimator.thumbnailLayer = paramInt2;
              localAppWindowAnimator.thumbnailAnimation = paramAppWindowToken;
              this.mService.mAppTransition.getNextAppTransitionStartRect(i, this.mTmpStartRect);
            }
          }
          else
          {
            paramAppWindowToken = new Rect(0, 0, ((DisplayInfo)localObject1).appWidth, ((DisplayInfo)localObject1).appHeight);
            continue;
          }
        }
        else
        {
          paramAppWindowToken = this.mService.mAppTransition.createThumbnailScaleAnimationLocked(((DisplayInfo)localObject1).appWidth, ((DisplayInfo)localObject1).appHeight, paramInt1, localBitmap);
          continue;
        }
        localObject1 = null;
      }
      catch (Surface.OutOfResourcesException paramAppWindowToken)
      {
        Slog.e(TAG, "Can't allocate thumbnail/Canvas surface w=" + localRect.width() + " h=" + localRect.height(), paramAppWindowToken);
        localAppWindowAnimator.clearThumbnail();
        return;
      }
      continue;
      label554:
      boolean bool = true;
    }
  }
  
  private int handleAppTransitionReadyLocked(WindowList paramWindowList)
  {
    int k = this.mService.mOpeningApps.size();
    if (!transitionGoodToGo(k)) {
      return 0;
    }
    Trace.traceBegin(32L, "AppTransitionReady");
    if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
      Slog.v(TAG, "**** GOOD TO GO");
    }
    int j = this.mService.mAppTransition.getAppTransition();
    if (this.mService.mSkipAppTransitionAnimation) {
      j = -1;
    }
    this.mService.mSkipAppTransitionAnimation = false;
    this.mService.mNoAnimationNotifyOnTransitionFinished.clear();
    this.mService.mH.removeMessages(13);
    this.mService.rebuildAppWindowListLocked();
    this.mWallpaperMayChange = false;
    Object localObject1 = null;
    int i1 = -1;
    int m = 0;
    boolean bool1 = false;
    int i = 0;
    while (i < k)
    {
      ((AppWindowToken)this.mService.mOpeningApps.valueAt(i)).clearAnimatingFlags();
      i += 1;
    }
    DisplayContent localDisplayContent = this.mService.getDefaultDisplayContentLocked();
    if (((localDisplayContent.pendingLayoutChanges & 0x4) != 0) && (this.mWallpaperControllerLocked.adjustWallpaperWindows()))
    {
      this.mService.mLayersController.assignLayersLocked(paramWindowList);
      localDisplayContent.layoutNeeded = true;
    }
    WindowState localWindowState1 = this.mWallpaperControllerLocked.getLowerWallpaperTarget();
    WindowState localWindowState2 = this.mWallpaperControllerLocked.getUpperWallpaperTarget();
    boolean bool3 = false;
    boolean bool2 = false;
    AppWindowToken localAppWindowToken2;
    AppWindowToken localAppWindowToken1;
    int i2;
    label267:
    boolean bool4;
    boolean bool5;
    Object localObject3;
    label338:
    int n;
    if (localWindowState1 == null)
    {
      localAppWindowToken2 = null;
      localAppWindowToken1 = null;
      i2 = this.mService.mClosingApps.size();
      int i3 = this.mService.mOpeningApps.size();
      k = 0;
      if (k >= i2 + i3) {
        break label580;
      }
      if (k >= i2) {
        break label440;
      }
      localObject2 = (AppWindowToken)this.mService.mClosingApps.valueAt(k);
      if (localObject2 != localAppWindowToken1)
      {
        bool4 = bool3;
        bool5 = bool2;
        localObject3 = localObject2;
        if (localObject2 != localAppWindowToken2) {}
      }
      else
      {
        bool5 = true;
        localObject3 = localObject2;
        bool4 = bool3;
      }
      bool1 |= ((AppWindowToken)localObject3).voiceInteraction;
      if (!((AppWindowToken)localObject3).appFullscreen) {
        break label500;
      }
      localObject3 = ((AppWindowToken)localObject3).findMainWindow();
      localObject2 = localObject1;
      i = i1;
      n = m;
      if (localObject3 != null)
      {
        localObject2 = ((WindowState)localObject3).mAttrs;
        i = ((WindowState)localObject3).mLayer;
        n = 1;
      }
    }
    for (;;)
    {
      k += 1;
      bool3 = bool4;
      bool2 = bool5;
      localObject1 = localObject2;
      i1 = i;
      m = n;
      break label267;
      localAppWindowToken1 = localWindowState1.mAppToken;
      localAppWindowToken2 = localWindowState2.mAppToken;
      break;
      label440:
      localObject2 = (AppWindowToken)this.mService.mOpeningApps.valueAt(k - i2);
      if (localObject2 != localAppWindowToken1)
      {
        bool4 = bool3;
        bool5 = bool2;
        localObject3 = localObject2;
        if (localObject2 != localAppWindowToken2) {
          break label338;
        }
      }
      bool4 = true;
      bool5 = bool2;
      localObject3 = localObject2;
      break label338;
      label500:
      localObject2 = localObject1;
      i = i1;
      n = m;
      if (m == 0)
      {
        localObject3 = ((AppWindowToken)localObject3).findMainWindow();
        localObject2 = localObject1;
        i = i1;
        n = m;
        if (localObject3 != null)
        {
          localObject2 = localObject1;
          i = i1;
          n = m;
          if (((WindowState)localObject3).mLayer > i1)
          {
            localObject2 = ((WindowState)localObject3).mAttrs;
            i = ((WindowState)localObject3).mLayer;
            n = m;
          }
        }
      }
    }
    label580:
    i = maybeUpdateTransitToWallpaper(j, bool3, bool2, localWindowState1, localWindowState2);
    if (!this.mService.mPolicy.allowAppAnimationsLw())
    {
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
        Slog.v(TAG, "Animations disallowed by keyguard or dream.");
      }
      localObject1 = null;
    }
    processApplicationsAnimatingInPlace(i);
    this.mTmpLayerAndToken.token = null;
    handleClosingApps(i, (WindowManager.LayoutParams)localObject1, bool1, this.mTmpLayerAndToken);
    Object localObject2 = this.mTmpLayerAndToken.token;
    localObject1 = handleOpeningApps(i, (WindowManager.LayoutParams)localObject1, bool1, this.mTmpLayerAndToken.layer);
    this.mService.mAppTransition.setLastAppTransition(i, (AppWindowToken)localObject1, (AppWindowToken)localObject2);
    if (localObject1 == null)
    {
      localObject1 = null;
      if (localObject2 != null) {
        break label851;
      }
      localObject2 = null;
      label712:
      this.mService.mAppTransition.goodToGo((AppWindowAnimator)localObject1, (AppWindowAnimator)localObject2, this.mService.mOpeningApps, this.mService.mClosingApps);
      this.mService.mAppTransition.postAnimationCallback();
      this.mService.mAppTransition.clear();
      this.mService.mOpeningApps.clear();
      this.mService.mClosingApps.clear();
      localDisplayContent.layoutNeeded = true;
      if ((paramWindowList == this.mService.getDefaultWindowListLocked()) && (!this.mService.moveInputMethodWindowsIfNeededLocked(true))) {
        break label861;
      }
    }
    for (;;)
    {
      this.mService.updateFocusedWindowLocked(2, true);
      this.mService.mFocusMayChange = false;
      this.mService.notifyActivityDrawnForKeyguard();
      Trace.traceEnd(32L);
      return 3;
      localObject1 = ((AppWindowToken)localObject1).mAppAnimator;
      break;
      label851:
      localObject2 = ((AppWindowToken)localObject2).mAppAnimator;
      break label712;
      label861:
      this.mService.mLayersController.assignLayersLocked(paramWindowList);
    }
  }
  
  private void handleClosingApps(int paramInt, WindowManager.LayoutParams paramLayoutParams, boolean paramBoolean, LayerAndToken paramLayerAndToken)
  {
    int n = this.mService.mClosingApps.size();
    int i = 0;
    while (i < n)
    {
      AppWindowToken localAppWindowToken = (AppWindowToken)this.mService.mClosingApps.valueAt(i);
      localAppWindowToken.markSavedSurfaceExiting();
      Object localObject = localAppWindowToken.mAppAnimator;
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
        Slog.v(TAG, "Now closing app " + localAppWindowToken);
      }
      ((AppWindowAnimator)localObject).clearThumbnail();
      ((AppWindowAnimator)localObject).setNullAnimation();
      localAppWindowToken.inPendingTransaction = false;
      this.mService.setTokenVisibilityLocked(localAppWindowToken, paramLayoutParams, false, paramInt, false, paramBoolean);
      localAppWindowToken.updateReportedVisibilityLocked();
      localAppWindowToken.allDrawn = true;
      localAppWindowToken.deferClearAllDrawn = false;
      if ((localAppWindowToken.startingWindow == null) || (localAppWindowToken.startingWindow.mAnimatingExit)) {}
      int k;
      for (;;)
      {
        WindowAnimator localWindowAnimator = this.mService.mAnimator;
        localWindowAnimator.mAppWindowAnimating |= ((AppWindowAnimator)localObject).isAnimating();
        if (paramLayoutParams == null) {
          break label299;
        }
        k = -1;
        int j = 0;
        while (j < localAppWindowToken.windows.size())
        {
          localObject = (WindowState)localAppWindowToken.windows.get(j);
          int m = k;
          if (((WindowState)localObject).mWinAnimator.mAnimLayer > k) {
            m = ((WindowState)localObject).mWinAnimator.mAnimLayer;
          }
          j += 1;
          k = m;
        }
        this.mService.scheduleRemoveStartingWindowLocked(localAppWindowToken);
      }
      if ((paramLayerAndToken.token == null) || (k > paramLayerAndToken.layer))
      {
        paramLayerAndToken.token = localAppWindowToken;
        paramLayerAndToken.layer = k;
      }
      label299:
      if (this.mService.mAppTransition.isNextAppTransitionThumbnailDown()) {
        createThumbnailAppAnimator(paramInt, localAppWindowToken, 0, paramLayerAndToken.layer);
      }
      i += 1;
    }
  }
  
  private void handleNotObscuredLocked(WindowState paramWindowState, DisplayInfo paramDisplayInfo)
  {
    WindowManager.LayoutParams localLayoutParams = paramWindowState.mAttrs;
    int j = localLayoutParams.flags;
    boolean bool = paramWindowState.isDisplayedLw();
    int i = localLayoutParams.privateFlags;
    if ((bool) && (paramWindowState.isObscuringFullscreen(paramDisplayInfo)))
    {
      if (!this.mObscured) {
        this.mObsuringWindow = paramWindowState;
      }
      this.mObscured = true;
    }
    if ((paramWindowState.mHasSurface) && (bool))
    {
      if ((j & 0x80) != 0)
      {
        this.mHoldScreen = paramWindowState.mSession;
        this.mHoldScreenWindow = paramWindowState;
      }
      if ((!this.mSyswin) && (paramWindowState.mAttrs.screenBrightness >= 0.0F) && (this.mScreenBrightness < 0.0F)) {
        this.mScreenBrightness = paramWindowState.mAttrs.screenBrightness;
      }
      if ((!this.mSyswin) && (paramWindowState.mAttrs.buttonBrightness >= 0.0F) && (this.mButtonBrightness < 0.0F)) {
        this.mButtonBrightness = paramWindowState.mAttrs.buttonBrightness;
      }
      if ((!this.mSyswin) && (paramWindowState.mAttrs.userActivityTimeout >= 0L) && (this.mUserActivityTimeout < 0L)) {
        this.mUserActivityTimeout = paramWindowState.mAttrs.userActivityTimeout;
      }
      j = localLayoutParams.type;
      if ((j != 2008) && (j != 2010)) {
        break label354;
      }
      this.mSyswin = true;
      label234:
      paramDisplayInfo = paramWindowState.getDisplayContent();
      if ((paramDisplayInfo == null) || (!paramDisplayInfo.isDefaultDisplay)) {
        break label369;
      }
      if ((j == 2023) || ((localLayoutParams.privateFlags & 0x400) != 0)) {
        this.mObscureApplicationContentOnSecondaryDisplays = true;
      }
    }
    for (this.mDisplayHasContent = true;; this.mDisplayHasContent = true) {
      label354:
      label369:
      do
      {
        if ((this.mPreferredRefreshRate == 0.0F) && (paramWindowState.mAttrs.preferredRefreshRate != 0.0F)) {
          this.mPreferredRefreshRate = paramWindowState.mAttrs.preferredRefreshRate;
        }
        if ((this.mPreferredModeId == 0) && (paramWindowState.mAttrs.preferredDisplayModeId != 0)) {
          this.mPreferredModeId = paramWindowState.mAttrs.preferredDisplayModeId;
        }
        if ((0x40000 & i) != 0) {
          this.mSustainedPerformanceModeCurrent = true;
        }
        return;
        if ((localLayoutParams.privateFlags & 0x400) == 0) {
          break label234;
        }
        break;
      } while ((paramDisplayInfo == null) || ((this.mObscureApplicationContentOnSecondaryDisplays) && ((!this.mObscured) || (j != 2009))));
    }
  }
  
  private AppWindowToken handleOpeningApps(int paramInt1, WindowManager.LayoutParams paramLayoutParams, boolean paramBoolean, int paramInt2)
  {
    Object localObject1 = null;
    int i1 = this.mService.mOpeningApps.size();
    int j = 0;
    while (j < i1)
    {
      AppWindowToken localAppWindowToken = (AppWindowToken)this.mService.mOpeningApps.valueAt(j);
      Object localObject2 = localAppWindowToken.mAppAnimator;
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
        Slog.v(TAG, "Now opening app" + localAppWindowToken);
      }
      if (!((AppWindowAnimator)localObject2).usingTransferredAnimation)
      {
        ((AppWindowAnimator)localObject2).clearThumbnail();
        ((AppWindowAnimator)localObject2).setNullAnimation();
      }
      localAppWindowToken.inPendingTransaction = false;
      if (!this.mService.setTokenVisibilityLocked(localAppWindowToken, paramLayoutParams, true, paramInt1, false, paramBoolean)) {
        this.mService.mNoAnimationNotifyOnTransitionFinished.add(localAppWindowToken.token);
      }
      localAppWindowToken.updateReportedVisibilityLocked();
      localAppWindowToken.waitingToShow = false;
      ((AppWindowAnimator)localObject2).mAllAppWinAnimators.clear();
      int k = localAppWindowToken.allAppWindows.size();
      int i = 0;
      while (i < k)
      {
        ((AppWindowAnimator)localObject2).mAllAppWinAnimators.add(((WindowState)localAppWindowToken.allAppWindows.get(i)).mWinAnimator);
        i += 1;
      }
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i(TAG, ">>> OPEN TRANSACTION handleAppTransitionReadyLocked()");
      }
      SurfaceControl.openTransaction();
      try
      {
        this.mService.mAnimator.orAnimating(((AppWindowAnimator)localObject2).showAllWindowsLocked());
        SurfaceControl.closeTransaction();
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          Slog.i(TAG, "<<< CLOSE TRANSACTION handleAppTransitionReadyLocked()");
        }
        WindowAnimator localWindowAnimator = this.mService.mAnimator;
        localWindowAnimator.mAppWindowAnimating |= ((AppWindowAnimator)localObject2).isAnimating();
        int n = 0;
        localObject2 = localObject1;
        k = n;
        if (paramLayoutParams == null) {
          break label430;
        }
        i = -1;
        k = 0;
        while (k < localAppWindowToken.allAppWindows.size())
        {
          localObject2 = (WindowState)localAppWindowToken.allAppWindows.get(k);
          int m = i;
          if (((WindowState)localObject2).mWinAnimator.mAnimLayer > i) {
            m = ((WindowState)localObject2).mWinAnimator.mAnimLayer;
          }
          k += 1;
          i = m;
        }
        if (i <= 0) {
          break label430;
        }
      }
      finally
      {
        SurfaceControl.closeTransaction();
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          Slog.i(TAG, "<<< CLOSE TRANSACTION handleAppTransitionReadyLocked()");
        }
      }
      localObject2 = localAppWindowToken;
      k = i;
      label430:
      if (this.mService.mAppTransition.isNextAppTransitionThumbnailUp()) {
        createThumbnailAppAnimator(paramInt1, localAppWindowToken, k, paramInt2);
      }
      j += 1;
      localObject1 = localObject2;
    }
    return (AppWindowToken)localObject1;
  }
  
  private int maybeUpdateTransitToWallpaper(int paramInt, boolean paramBoolean1, boolean paramBoolean2, WindowState paramWindowState1, WindowState paramWindowState2)
  {
    WindowState localWindowState = this.mWallpaperControllerLocked.getWallpaperTarget();
    Object localObject;
    ArraySet localArraySet1;
    ArraySet localArraySet2;
    label200:
    int i;
    if (this.mWallpaperControllerLocked.isWallpaperTargetAnimating())
    {
      localObject = null;
      localArraySet1 = this.mService.mOpeningApps;
      localArraySet2 = this.mService.mClosingApps;
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
        Slog.v(TAG, "New wallpaper target=" + localWindowState + ", oldWallpaper=" + localObject + ", lower target=" + paramWindowState1 + ", upper target=" + paramWindowState2 + ", openingApps=" + localArraySet1 + ", closingApps=" + localArraySet2);
      }
      this.mService.mAnimateWallpaperWithTarget = false;
      if ((!paramBoolean2) || (!paramBoolean1)) {
        break label264;
      }
      if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
        Slog.v(TAG, "Wallpaper animation!");
      }
      switch (paramInt)
      {
      default: 
        i = paramInt;
        if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS)
        {
          Slog.v(TAG, "New transit: " + AppTransition.appTransitionToString(paramInt));
          i = paramInt;
        }
        break;
      }
    }
    label264:
    do
    {
      return i;
      localObject = localWindowState;
      break;
      paramInt = 14;
      break label200;
      paramInt = 15;
      break label200;
      if ((localObject == null) || (this.mService.mOpeningApps.isEmpty())) {}
      while ((localArraySet1.contains(((WindowState)localObject).mAppToken)) || (!localArraySet2.contains(((WindowState)localObject).mAppToken)) || (!checkIsScreenWindow(localArraySet2)))
      {
        if ((localWindowState == null) || (!localWindowState.isVisibleLw()) || (!localArraySet1.contains(localWindowState.mAppToken))) {
          break label431;
        }
        i = 13;
        if (!WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
          break;
        }
        Slog.v(TAG, "New transit into wallpaper: " + AppTransition.appTransitionToString(13));
        return 13;
      }
      i = 12;
    } while (!WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS);
    Slog.v(TAG, "New transit away from wallpaper: " + AppTransition.appTransitionToString(12));
    return 12;
    label431:
    this.mService.mAnimateWallpaperWithTarget = true;
    return paramInt;
  }
  
  private void performSurfacePlacementInner(boolean paramBoolean)
  {
    if (WindowManagerDebugConfig.DEBUG_WINDOW_TRACE) {
      Slog.v(TAG, "performSurfacePlacementInner: entry. Called by " + Debug.getCallers(3));
    }
    boolean bool = false;
    if (this.mService.mFocusMayChange)
    {
      this.mService.mFocusMayChange = false;
      bool = this.mService.updateFocusedWindowLocked(3, false);
    }
    int n = this.mService.mDisplayContents.size();
    int i = 0;
    while (i < n)
    {
      localObject1 = (DisplayContent)this.mService.mDisplayContents.valueAt(i);
      j = ((DisplayContent)localObject1).mExitingTokens.size() - 1;
      while (j >= 0)
      {
        ((WindowToken)((DisplayContent)localObject1).mExitingTokens.get(j)).hasVisible = false;
        j -= 1;
      }
      i += 1;
    }
    i = this.mService.mStackIdToStack.size() - 1;
    while (i >= 0)
    {
      localObject1 = ((TaskStack)this.mService.mStackIdToStack.valueAt(i)).mExitingAppTokens;
      j = ((AppTokenList)localObject1).size() - 1;
      while (j >= 0)
      {
        ((AppWindowToken)((AppTokenList)localObject1).get(j)).hasVisible = false;
        j -= 1;
      }
      i -= 1;
    }
    this.mHoldScreen = null;
    this.mHoldScreenWindow = null;
    this.mObsuringWindow = null;
    this.mScreenBrightness = -1.0F;
    this.mButtonBrightness = -1.0F;
    this.mUserActivityTimeout = -1L;
    this.mObscureApplicationContentOnSecondaryDisplays = false;
    this.mSustainedPerformanceModeCurrent = false;
    Object localObject1 = this.mService;
    ((WindowManagerService)localObject1).mTransactionSequence += 1;
    localObject1 = this.mService.getDefaultDisplayContentLocked();
    Object localObject4 = ((DisplayContent)localObject1).getDisplayInfo();
    i = ((DisplayInfo)localObject4).logicalWidth;
    int j = ((DisplayInfo)localObject4).logicalHeight;
    if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
      Slog.i(TAG, ">>> OPEN TRANSACTION performLayoutAndPlaceSurfaces");
    }
    SurfaceControl.openTransaction();
    try
    {
      applySurfaceChangesTransaction(paramBoolean, n, i, j);
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        Slog.wtf(TAG, "Unhandled exception in Window Manager", localRuntimeException);
        SurfaceControl.closeTransaction();
        if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
          Slog.i(TAG, "<<< CLOSE TRANSACTION performLayoutAndPlaceSurfaces");
        }
      }
    }
    finally
    {
      SurfaceControl.closeTransaction();
      if (!WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        break label760;
      }
      Slog.i(TAG, "<<< CLOSE TRANSACTION performLayoutAndPlaceSurfaces");
    }
    localObject4 = ((DisplayContent)localObject1).getWindowList();
    if (this.mService.mAppTransition.isReady())
    {
      ((DisplayContent)localObject1).pendingLayoutChanges |= handleAppTransitionReadyLocked((WindowList)localObject4);
      if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
        debugLayoutRepeats("after handleAppTransitionReadyLocked", ((DisplayContent)localObject1).pendingLayoutChanges);
      }
    }
    if ((!this.mService.mAnimator.mAppWindowAnimating) && (this.mService.mAppTransition.isRunning()))
    {
      ((DisplayContent)localObject1).pendingLayoutChanges |= this.mService.handleAnimatingStoppedAndTransitionLocked();
      if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
        debugLayoutRepeats("after handleAnimStopAndXitionLock", ((DisplayContent)localObject1).pendingLayoutChanges);
      }
    }
    if ((!this.mWallpaperForceHidingChanged) || (((DisplayContent)localObject1).pendingLayoutChanges != 0) || (this.mService.mAppTransition.isReady()))
    {
      this.mWallpaperForceHidingChanged = false;
      if (this.mWallpaperMayChange)
      {
        if (WindowManagerDebugConfig.DEBUG_WALLPAPER_LIGHT) {
          Slog.v(TAG, "Wallpaper may change!  Adjusting");
        }
        ((DisplayContent)localObject1).pendingLayoutChanges |= 0x4;
        if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
          debugLayoutRepeats("WallpaperMayChange", ((DisplayContent)localObject1).pendingLayoutChanges);
        }
      }
      paramBoolean = bool;
      if (this.mService.mFocusMayChange)
      {
        this.mService.mFocusMayChange = false;
        paramBoolean = bool;
        if (this.mService.updateFocusedWindowLocked(2, false))
        {
          paramBoolean = true;
          ((DisplayContent)localObject1).pendingLayoutChanges |= 0x8;
        }
      }
      if (this.mService.needsLayout())
      {
        ((DisplayContent)localObject1).pendingLayoutChanges |= 0x1;
        if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
          debugLayoutRepeats("mLayoutNeeded", ((DisplayContent)localObject1).pendingLayoutChanges);
        }
      }
      i = this.mService.mResizingWindows.size() - 1;
      label668:
      if (i < 0) {
        break label832;
      }
      localObject4 = (WindowState)this.mService.mResizingWindows.get(i);
      if (!((WindowState)localObject4).mAppFreezing) {
        break label796;
      }
    }
    for (;;)
    {
      i -= 1;
      break label668;
      label760:
      ((DisplayContent)localObject2).pendingLayoutChanges |= 0x1;
      if (!WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
        break;
      }
      debugLayoutRepeats("after animateAwayWallpaperLocked", ((DisplayContent)localObject2).pendingLayoutChanges);
      break;
      label796:
      if (localRuntimeException.mAppToken != null) {
        localRuntimeException.mAppToken.destroySavedSurfaces();
      }
      localRuntimeException.reportResized();
      this.mService.mResizingWindows.remove(i);
    }
    label832:
    if ((WindowManagerDebugConfig.DEBUG_ORIENTATION) && (this.mService.mDisplayFrozen)) {
      Slog.v(TAG, "With display frozen, orientationChangeComplete=" + this.mOrientationChangeComplete);
    }
    if (this.mOrientationChangeComplete)
    {
      if (this.mService.mWindowsFreezingScreen != 0)
      {
        this.mService.mWindowsFreezingScreen = 0;
        this.mService.mLastFinishedFreezeSource = this.mLastWindowFreezeSource;
        this.mService.mH.removeMessages(11);
      }
      this.mService.stopFreezingDisplayLocked();
    }
    i = 0;
    j = 0;
    int k = this.mService.mDestroySurface.size();
    Object localObject5;
    if (k > 0)
    {
      int m;
      do
      {
        m = k - 1;
        localObject5 = (WindowState)this.mService.mDestroySurface.get(m);
        ((WindowState)localObject5).mDestroying = false;
        if (this.mService.mInputMethodWindow == localObject5) {
          this.mService.mInputMethodWindow = null;
        }
        i = j;
        if (this.mWallpaperControllerLocked.isWallpaperTarget((WindowState)localObject5)) {
          i = 1;
        }
        ((WindowState)localObject5).destroyOrSaveSurface();
        k = m;
        j = i;
      } while (m > 0);
      this.mService.mDestroySurface.clear();
    }
    j = 0;
    Object localObject6;
    while (j < n)
    {
      localObject5 = ((DisplayContent)this.mService.mDisplayContents.valueAt(j)).mExitingTokens;
      k = ((ArrayList)localObject5).size() - 1;
      while (k >= 0)
      {
        localObject6 = (WindowToken)((ArrayList)localObject5).get(k);
        if (!((WindowToken)localObject6).hasVisible)
        {
          ((ArrayList)localObject5).remove(k);
          if (((WindowToken)localObject6).windowType == 2013) {
            this.mWallpaperControllerLocked.removeWallpaperToken((WindowToken)localObject6);
          }
        }
        k -= 1;
      }
      j += 1;
    }
    j = this.mService.mStackIdToStack.size() - 1;
    while (j >= 0)
    {
      localObject5 = ((TaskStack)this.mService.mStackIdToStack.valueAt(j)).mExitingAppTokens;
      k = ((AppTokenList)localObject5).size() - 1;
      if (k >= 0)
      {
        localObject6 = (AppWindowToken)((AppTokenList)localObject5).get(k);
        if ((((AppWindowToken)localObject6).hasVisible) || (this.mService.mClosingApps.contains(localObject6))) {}
        for (;;)
        {
          k -= 1;
          break;
          if ((!((AppWindowToken)localObject6).mIsExiting) || (((AppWindowToken)localObject6).allAppWindows.isEmpty()))
          {
            ((AppWindowToken)localObject6).mAppAnimator.clearAnimation();
            ((AppWindowToken)localObject6).mAppAnimator.animating = false;
            if ((WindowManagerDebugConfig.DEBUG_ADD_REMOVE) || (WindowManagerDebugConfig.DEBUG_TOKEN_MOVEMENT)) {
              Slog.v(TAG, "performLayout: App token exiting now removed" + localObject6);
            }
            ((AppWindowToken)localObject6).removeAppFromTaskLocked();
          }
        }
      }
      j -= 1;
    }
    if (i != 0)
    {
      ((DisplayContent)localObject2).pendingLayoutChanges |= 0x4;
      ((DisplayContent)localObject2).layoutNeeded = true;
    }
    i = 0;
    while (i < n)
    {
      localObject5 = (DisplayContent)this.mService.mDisplayContents.valueAt(i);
      if (((DisplayContent)localObject5).pendingLayoutChanges != 0) {
        ((DisplayContent)localObject5).layoutNeeded = true;
      }
      i += 1;
    }
    this.mService.mInputMonitor.updateInputWindowsLw(true);
    this.mService.setHoldScreenLocked(this.mHoldScreen);
    if (!this.mService.mDisplayFrozen)
    {
      if ((this.mScreenBrightness < 0.0F) || (this.mScreenBrightness > 1.0F))
      {
        this.mService.mPowerManagerInternal.setScreenBrightnessOverrideFromWindowManager(-1);
        if ((this.mButtonBrightness >= 0.0F) && (this.mButtonBrightness <= 1.0F)) {
          break label1863;
        }
        this.mService.mPowerManagerInternal.setButtonBrightnessOverrideFromWindowManager(-1);
        label1492:
        this.mService.mPowerManagerInternal.setUserActivityTimeoutOverrideFromWindowManager(this.mUserActivityTimeout);
      }
    }
    else
    {
      if (this.mSustainedPerformanceModeCurrent != this.mSustainedPerformanceModeEnabled)
      {
        this.mSustainedPerformanceModeEnabled = this.mSustainedPerformanceModeCurrent;
        localObject5 = this.mService.mPowerManagerInternal;
        if (!this.mSustainedPerformanceModeEnabled) {
          break label1883;
        }
        i = 1;
        label1543:
        ((PowerManagerInternal)localObject5).powerHint(6, i);
      }
      if (this.mService.mTurnOnScreen)
      {
        if ((this.mService.mAllowTheaterModeWakeFromLayout) || (Settings.Global.getInt(this.mService.mContext.getContentResolver(), "theater_mode_on", 0) == 0))
        {
          if ((WindowManagerDebugConfig.DEBUG_VISIBILITY) || (WindowManagerDebugConfig.DEBUG_POWER)) {
            Slog.v(TAG, "Turning screen on after layout!");
          }
          this.mService.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "android.server.wm:TURN_ON");
        }
        this.mService.mTurnOnScreen = false;
      }
      if (this.mUpdateRotation)
      {
        if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
          Slog.d(TAG, "Performing post-rotate rotation");
        }
        if (!this.mService.updateRotationUncheckedLocked(false)) {
          break label1888;
        }
        this.mService.mH.sendEmptyMessage(18);
      }
      label1684:
      if (this.mService.mWaitingForDrawnCallback != null) {
        break label1903;
      }
      if ((this.mOrientationChangeComplete) && (!((DisplayContent)localObject2).layoutNeeded)) {
        break label1896;
      }
      label1709:
      j = this.mService.mPendingRemove.size();
      if (j <= 0) {
        break label1979;
      }
      if (this.mService.mPendingRemoveTmp.length < j) {
        this.mService.mPendingRemoveTmp = new WindowState[j + 10];
      }
      this.mService.mPendingRemove.toArray(this.mService.mPendingRemoveTmp);
      this.mService.mPendingRemove.clear();
      localObject3 = new DisplayContentList();
      i = 0;
      label1789:
      if (i >= j) {
        break label1924;
      }
      localObject5 = this.mService.mPendingRemoveTmp[i];
      this.mService.removeWindowInnerLocked((WindowState)localObject5);
      localObject5 = ((WindowState)localObject5).getDisplayContent();
      if ((localObject5 != null) && (!((DisplayContentList)localObject3).contains(localObject5))) {
        break label1913;
      }
    }
    for (;;)
    {
      i += 1;
      break label1789;
      this.mService.mPowerManagerInternal.setScreenBrightnessOverrideFromWindowManager(toBrightnessOverride(this.mScreenBrightness));
      break;
      label1863:
      this.mService.mPowerManagerInternal.setButtonBrightnessOverrideFromWindowManager(toBrightnessOverride(this.mButtonBrightness));
      break label1492;
      label1883:
      i = 0;
      break label1543;
      label1888:
      this.mUpdateRotation = false;
      break label1684;
      label1896:
      if (this.mUpdateRotation) {
        break label1709;
      }
      label1903:
      this.mService.checkDrawnWindowsLocked();
      break label1709;
      label1913:
      ((DisplayContentList)localObject3).add(localObject5);
    }
    label1924:
    Object localObject3 = ((Iterable)localObject3).iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject5 = (DisplayContent)((Iterator)localObject3).next();
      this.mService.mLayersController.assignLayersLocked(((DisplayContent)localObject5).getWindowList());
      ((DisplayContent)localObject5).layoutNeeded = true;
    }
    label1979:
    i = this.mService.mDisplayContents.size() - 1;
    while (i >= 0)
    {
      ((DisplayContent)this.mService.mDisplayContents.valueAt(i)).checkForDeferredActions();
      i -= 1;
    }
    if (paramBoolean) {
      this.mService.mInputMonitor.updateInputWindowsLw(false);
    }
    this.mService.setFocusTaskRegionLocked();
    this.mService.enableScreenIfNeededLocked();
    this.mService.scheduleAnimationLocked();
    this.mService.mWindowPlacerLocked.destroyPendingSurfaces();
    if (WindowManagerDebugConfig.DEBUG_WINDOW_TRACE) {
      Slog.e(TAG, "performSurfacePlacementInner exit: animating=" + this.mService.mAnimator.isAnimating());
    }
  }
  
  private void performSurfacePlacementLoop()
  {
    if (this.mInLayout)
    {
      if (WindowManagerDebugConfig.DEBUG) {
        throw new RuntimeException("Recursive call!");
      }
      Slog.w(TAG, "performLayoutAndPlaceSurfacesLocked called while in layout. Callers=" + Debug.getCallers(3));
      return;
    }
    if (this.mService.mWaitingForConfig) {
      return;
    }
    if (!this.mService.mDisplayReady) {
      return;
    }
    Trace.traceBegin(32L, "wmLayout");
    this.mInLayout = true;
    boolean bool = false;
    if (!this.mService.mForceRemoves.isEmpty())
    {
      bool = true;
      while (!this.mService.mForceRemoves.isEmpty())
      {
        ??? = (WindowState)this.mService.mForceRemoves.remove(0);
        Slog.i(TAG, "Force removing: " + ???);
        this.mService.removeWindowInnerLocked((WindowState)???);
      }
      Slog.w(TAG, "Due to memory failure, waiting a bit for next layout");
    }
    label289:
    synchronized (new Object())
    {
      try
      {
        ???.wait(250L);
        try
        {
          performSurfacePlacementInner(bool);
          this.mInLayout = false;
          if (!this.mService.needsLayout()) {
            break label327;
          }
          int i = this.mLayoutRepeatCount + 1;
          this.mLayoutRepeatCount = i;
          if (i >= 6) {
            break label289;
          }
          requestTraversal();
        }
        catch (RuntimeException localRuntimeException)
        {
          for (;;)
          {
            this.mInLayout = false;
            Slog.wtf(TAG, "Unhandled exception while laying out windows", localRuntimeException);
            continue;
            this.mLayoutRepeatCount = 0;
            continue;
            this.mService.mH.removeMessages(19);
            this.mService.mH.sendEmptyMessage(19);
          }
        }
        if (this.mService.mWindowsChanged)
        {
          bool = this.mService.mWindowChangeListeners.isEmpty();
          if (!bool) {}
        }
        else
        {
          Trace.traceEnd(32L);
          return;
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;) {}
      }
    }
  }
  
  private void processApplicationsAnimatingInPlace(int paramInt)
  {
    if (paramInt == 17)
    {
      Object localObject1 = this.mService.findFocusedWindowLocked(this.mService.getDefaultDisplayContentLocked());
      if (localObject1 != null)
      {
        Object localObject2 = ((WindowState)localObject1).mAppToken;
        localObject1 = ((AppWindowToken)localObject2).mAppAnimator;
        if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
          Slog.v(TAG, "Now animating app in place " + localObject2);
        }
        ((AppWindowAnimator)localObject1).clearThumbnail();
        ((AppWindowAnimator)localObject1).setNullAnimation();
        this.mService.updateTokenInPlaceLocked((AppWindowToken)localObject2, paramInt);
        ((AppWindowToken)localObject2).updateReportedVisibilityLocked();
        ((AppWindowAnimator)localObject1).mAllAppWinAnimators.clear();
        int i = ((AppWindowToken)localObject2).allAppWindows.size();
        paramInt = 0;
        while (paramInt < i)
        {
          ((AppWindowAnimator)localObject1).mAllAppWinAnimators.add(((WindowState)((AppWindowToken)localObject2).allAppWindows.get(paramInt)).mWinAnimator);
          paramInt += 1;
        }
        localObject2 = this.mService.mAnimator;
        ((WindowAnimator)localObject2).mAppWindowAnimating |= ((AppWindowAnimator)localObject1).isAnimating();
        this.mService.mAnimator.orAnimating(((AppWindowAnimator)localObject1).showAllWindowsLocked());
      }
    }
  }
  
  private static int toBrightnessOverride(float paramFloat)
  {
    return (int)(255.0F * paramFloat);
  }
  
  private boolean transitionGoodToGo(int paramInt)
  {
    if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
      Slog.v(TAG, "Checking " + paramInt + " opening apps (frozen=" + this.mService.mDisplayFrozen + " timeout=" + this.mService.mAppTransition.isTimeout() + ")...");
    }
    Object localObject = this.mService.mAnimator.getScreenRotationAnimationLocked(0);
    int i = 3;
    if (!this.mService.mAppTransition.isTimeout())
    {
      if ((localObject != null) && (((ScreenRotationAnimation)localObject).isAnimating()) && (this.mService.rotationNeedsUpdateLocked()))
      {
        if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
          Slog.v(TAG, "Delaying app transition for screen rotation animation to finish");
        }
        return false;
      }
      int j = 0;
      if (j < paramInt)
      {
        localObject = (AppWindowToken)this.mService.mOpeningApps.valueAt(j);
        if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
          Slog.v(TAG, "Check opening app=" + localObject + ": allDrawn=" + ((AppWindowToken)localObject).allDrawn + " startingDisplayed=" + ((AppWindowToken)localObject).startingDisplayed + " startingMoved=" + ((AppWindowToken)localObject).startingMoved + " isRelaunching()=" + ((AppWindowToken)localObject).isRelaunching());
        }
        if (((AppWindowToken)localObject).isRelaunching()) {
          return false;
        }
        bool = ((AppWindowToken)localObject).allDrawn;
        ((AppWindowToken)localObject).restoreSavedSurfaces();
        if ((((AppWindowToken)localObject).allDrawn) || (((AppWindowToken)localObject).startingDisplayed))
        {
          label294:
          if (!((AppWindowToken)localObject).allDrawn) {
            break label331;
          }
          if (!bool) {
            break label326;
          }
          i = 2;
        }
        for (;;)
        {
          j += 1;
          break;
          if (((AppWindowToken)localObject).startingMoved) {
            break label294;
          }
          return false;
          label326:
          i = 0;
          continue;
          label331:
          i = 1;
        }
      }
      if (this.mService.mAppTransition.isFetchingAppTransitionsSpecs())
      {
        if (WindowManagerDebugConfig.DEBUG_APP_TRANSITIONS) {
          Slog.v(TAG, "isFetchingAppTransitionSpecs=true");
        }
        return false;
      }
      if (this.mWallpaperControllerLocked.isWallpaperVisible()) {}
      for (boolean bool = this.mWallpaperControllerLocked.wallpaperTransitionReady(); bool; bool = true)
      {
        this.mService.mH.obtainMessage(47, i, 0).sendToTarget();
        return true;
      }
      return false;
    }
    this.mService.mH.obtainMessage(47, 3, 0).sendToTarget();
    return true;
  }
  
  private void updateAllDrawnLocked(DisplayContent paramDisplayContent)
  {
    ArrayList localArrayList1 = paramDisplayContent.getStacks();
    int i = localArrayList1.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList2 = ((TaskStack)localArrayList1.get(i)).getTasks();
      int j = localArrayList2.size() - 1;
      while (j >= 0)
      {
        AppTokenList localAppTokenList = ((Task)localArrayList2.get(j)).mAppTokens;
        int k = localAppTokenList.size() - 1;
        if (k >= 0)
        {
          AppWindowToken localAppWindowToken = (AppWindowToken)localAppTokenList.get(k);
          int m;
          if (!localAppWindowToken.allDrawn)
          {
            m = localAppWindowToken.numInterestingWindows;
            if ((m > 0) && (localAppWindowToken.numDrawnWindows >= m))
            {
              if (Build.AUTO_TEST_ONEPLUS) {
                Slog.d("APP_LAUNCH", SystemClock.uptimeMillis() + " WMS: updateAllDrawnLocked: " + localAppWindowToken);
              }
              if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
                Slog.v(TAG, "allDrawn: " + localAppWindowToken + " interesting=" + m + " drawn=" + localAppWindowToken.numDrawnWindows);
              }
              localAppWindowToken.allDrawn = true;
              paramDisplayContent.layoutNeeded = true;
              this.mService.mH.obtainMessage(32, localAppWindowToken.token).sendToTarget();
            }
          }
          if (!localAppWindowToken.allDrawnExcludingSaved)
          {
            m = localAppWindowToken.numInterestingWindowsExcludingSaved;
            if ((m > 0) && (localAppWindowToken.numDrawnWindowsExclusingSaved >= m))
            {
              if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
                Slog.v(TAG, "allDrawnExcludingSaved: " + localAppWindowToken + " interesting=" + m + " drawn=" + localAppWindowToken.numDrawnWindowsExclusingSaved);
              }
              localAppWindowToken.allDrawnExcludingSaved = true;
              paramDisplayContent.layoutNeeded = true;
              if ((localAppWindowToken.isAnimatingInvisibleWithSavedSurface()) && (!this.mService.mFinishedEarlyAnim.contains(localAppWindowToken))) {
                break label376;
              }
            }
          }
          for (;;)
          {
            k -= 1;
            break;
            label376:
            this.mService.mFinishedEarlyAnim.add(localAppWindowToken);
          }
        }
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void continueLayout()
  {
    this.mDeferDepth -= 1;
    if (this.mDeferDepth <= 0) {
      performSurfacePlacement();
    }
  }
  
  boolean copyAnimToLayoutParamsLocked()
  {
    boolean bool = false;
    int i = this.mService.mAnimator.mBulkUpdateParams;
    if ((i & 0x1) != 0)
    {
      this.mUpdateRotation = true;
      bool = true;
    }
    if ((i & 0x2) != 0)
    {
      this.mWallpaperMayChange = true;
      bool = true;
    }
    if ((i & 0x4) != 0)
    {
      this.mWallpaperForceHidingChanged = true;
      bool = true;
    }
    if ((i & 0x8) == 0) {
      this.mOrientationChangeComplete = false;
    }
    for (;;)
    {
      if ((i & 0x10) != 0) {
        this.mService.mTurnOnScreen = true;
      }
      if ((i & 0x20) != 0) {
        this.mWallpaperActionPending = true;
      }
      return bool;
      this.mOrientationChangeComplete = true;
      this.mLastWindowFreezeSource = this.mService.mAnimator.mLastWindowFreezeSource;
      if (this.mService.mWindowsFreezingScreen != 0) {
        bool = true;
      }
    }
  }
  
  void debugLayoutRepeats(String paramString, int paramInt)
  {
    if (this.mLayoutRepeatCount >= 4) {
      Slog.v(TAG, "Layouts looping: " + paramString + ", mPendingLayoutChanges = 0x" + Integer.toHexString(paramInt));
    }
  }
  
  void deferLayout()
  {
    this.mDeferDepth += 1;
  }
  
  void destroyAfterTransaction(SurfaceControl paramSurfaceControl)
  {
    this.mPendingDestroyingSurfaces.add(paramSurfaceControl);
  }
  
  void destroyPendingSurfaces()
  {
    int i = this.mPendingDestroyingSurfaces.size() - 1;
    while (i >= 0)
    {
      ((SurfaceControl)this.mPendingDestroyingSurfaces.get(i)).destroy();
      i -= 1;
    }
    this.mPendingDestroyingSurfaces.clear();
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mTraversalScheduled=");
    paramPrintWriter.println(this.mTraversalScheduled);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mHoldScreenWindow=");
    paramPrintWriter.println(this.mHoldScreenWindow);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mObsuringWindow=");
    paramPrintWriter.println(this.mObsuringWindow);
  }
  
  boolean isInLayout()
  {
    return this.mInLayout;
  }
  
  final void performLayoutLockedInner(DisplayContent paramDisplayContent, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramDisplayContent.layoutNeeded) {
      return;
    }
    paramDisplayContent.layoutNeeded = false;
    WindowList localWindowList = paramDisplayContent.getWindowList();
    boolean bool1 = paramDisplayContent.isDefaultDisplay;
    Object localObject1 = paramDisplayContent.getDisplayInfo();
    int i = ((DisplayInfo)localObject1).logicalWidth;
    int j = ((DisplayInfo)localObject1).logicalHeight;
    if (this.mService.mInputConsumer != null) {
      this.mService.mInputConsumer.layout(i, j);
    }
    if (this.mService.mWallpaperInputConsumer != null) {
      this.mService.mWallpaperInputConsumer.layout(i, j);
    }
    int k = localWindowList.size();
    if (WindowManagerDebugConfig.DEBUG_LAYOUT)
    {
      Slog.v(TAG, "-------------------------------------");
      Slog.v(TAG, "performLayout: needed=" + paramDisplayContent.layoutNeeded + " dw=" + i + " dh=" + j);
    }
    this.mService.mPolicy.beginLayoutLw(bool1, i, j, this.mService.mRotation, this.mService.mCurConfiguration.uiMode);
    if (bool1)
    {
      this.mService.mSystemDecorLayer = this.mService.mPolicy.getSystemDecorLayerLw();
      this.mService.mScreenRect.set(0, 0, i, j);
    }
    this.mService.mPolicy.getContentRectLw(this.mTmpContentRect);
    paramDisplayContent.resize(this.mTmpContentRect);
    i = this.mService.mLayoutSeq + 1;
    int m = i;
    if (i < 0) {
      m = 0;
    }
    this.mService.mLayoutSeq = m;
    i = 0;
    j = -1;
    k -= 1;
    label362:
    int n;
    if (k >= 0)
    {
      localObject1 = (WindowState)localWindowList.get(k);
      int i1;
      if ((i == 0) || (!this.mService.mPolicy.canBeForceHidden((WindowManagerPolicy.WindowState)localObject1, ((WindowState)localObject1).mAttrs)))
      {
        bool1 = ((WindowState)localObject1).isGoneForLayoutLw();
        if ((WindowManagerDebugConfig.DEBUG_LAYOUT) && (!((WindowState)localObject1).mLayoutAttached)) {
          break label460;
        }
        if ((!bool1) || (!((WindowState)localObject1).mHaveFrame) || (((WindowState)localObject1).mLayoutNeeded)) {
          break label851;
        }
        if (!((WindowState)localObject1).isConfigChanged())
        {
          n = i;
          i1 = j;
          if (!((WindowState)localObject1).setReportResizeHints()) {}
        }
        else
        {
          if (!((WindowState)localObject1).isGoneForLayoutLw()) {
            break label785;
          }
          i1 = j;
          n = i;
        }
      }
      for (;;)
      {
        k -= 1;
        i = n;
        j = i1;
        break;
        bool1 = true;
        break label362;
        label460:
        Slog.v(TAG, "1ST PASS " + localObject1 + ": gone=" + bool1 + " mHaveFrame=" + ((WindowState)localObject1).mHaveFrame + " mLayoutAttached=" + ((WindowState)localObject1).mLayoutAttached + " screen changed=" + ((WindowState)localObject1).isConfigChanged());
        Object localObject2 = ((WindowState)localObject1).mAppToken;
        if (bool1)
        {
          str = TAG;
          localStringBuilder = new StringBuilder().append("  GONE: mViewVisibility=").append(((WindowState)localObject1).mViewVisibility).append(" mRelayoutCalled=").append(((WindowState)localObject1).mRelayoutCalled).append(" hidden=").append(((WindowState)localObject1).mRootToken.hidden).append(" hiddenRequested=");
          if (localObject2 != null) {}
          for (bool2 = ((AppWindowToken)localObject2).hiddenRequested;; bool2 = false)
          {
            Slog.v(str, bool2 + " mAttachedHidden=" + ((WindowState)localObject1).mAttachedHidden);
            break;
          }
        }
        String str = TAG;
        StringBuilder localStringBuilder = new StringBuilder().append("  VIS: mViewVisibility=").append(((WindowState)localObject1).mViewVisibility).append(" mRelayoutCalled=").append(((WindowState)localObject1).mRelayoutCalled).append(" hidden=").append(((WindowState)localObject1).mRootToken.hidden).append(" hiddenRequested=");
        if (localObject2 != null) {}
        for (boolean bool2 = ((AppWindowToken)localObject2).hiddenRequested;; bool2 = false)
        {
          Slog.v(str, bool2 + " mAttachedHidden=" + ((WindowState)localObject1).mAttachedHidden);
          break;
        }
        label785:
        if ((((WindowState)localObject1).mAttrs.privateFlags & 0x400) == 0)
        {
          n = i;
          i1 = j;
          if (((WindowState)localObject1).mHasSurface)
          {
            n = i;
            i1 = j;
            if (((WindowState)localObject1).mAppToken != null)
            {
              n = i;
              i1 = j;
              if (!((WindowState)localObject1).mAppToken.layoutConfigChanges) {}
            }
          }
        }
        else
        {
          label851:
          if (!((WindowState)localObject1).mLayoutAttached)
          {
            if (paramBoolean1) {
              ((WindowState)localObject1).mContentChanged = false;
            }
            if (((WindowState)localObject1).mAttrs.type == 2023) {
              i = 1;
            }
            ((WindowState)localObject1).mLayoutNeeded = false;
            ((WindowState)localObject1).prelayout();
            this.mService.mPolicy.layoutWindowLw((WindowManagerPolicy.WindowState)localObject1, null);
            ((WindowState)localObject1).mLayoutSeq = m;
            localObject2 = ((WindowState)localObject1).getTask();
            if (localObject2 != null) {
              paramDisplayContent.mDimLayerController.updateDimLayer((DimLayer.DimLayerUser)localObject2);
            }
            n = i;
            i1 = j;
            if (WindowManagerDebugConfig.DEBUG_LAYOUT)
            {
              Slog.v(TAG, "  LAYOUT: mFrame=" + ((WindowState)localObject1).mFrame + " mContainingFrame=" + ((WindowState)localObject1).mContainingFrame + " mDisplayFrame=" + ((WindowState)localObject1).mDisplayFrame);
              n = i;
              i1 = j;
            }
          }
          else
          {
            n = i;
            i1 = j;
            if (j < 0)
            {
              i1 = k;
              n = i;
            }
          }
        }
      }
    }
    k = 0;
    if (j >= 0)
    {
      paramDisplayContent = (WindowState)localWindowList.get(j);
      if (paramDisplayContent.mLayoutAttached)
      {
        if (WindowManagerDebugConfig.DEBUG_LAYOUT) {
          Slog.v(TAG, "2ND PASS " + paramDisplayContent + " mHaveFrame=" + paramDisplayContent.mHaveFrame + " mViewVisibility=" + paramDisplayContent.mViewVisibility + " mRelayoutCalled=" + paramDisplayContent.mRelayoutCalled);
        }
        if ((k != 0) && (this.mService.mPolicy.canBeForceHidden(paramDisplayContent, paramDisplayContent.mAttrs))) {
          n = k;
        }
      }
      for (;;)
      {
        j -= 1;
        k = n;
        break;
        if (((paramDisplayContent.mViewVisibility == 8) || (!paramDisplayContent.mRelayoutCalled)) && (paramDisplayContent.mHaveFrame))
        {
          n = k;
          if (!paramDisplayContent.mLayoutNeeded) {}
        }
        else
        {
          if (paramBoolean1) {
            paramDisplayContent.mContentChanged = false;
          }
          paramDisplayContent.mLayoutNeeded = false;
          paramDisplayContent.prelayout();
          this.mService.mPolicy.layoutWindowLw(paramDisplayContent, paramDisplayContent.mAttachedWindow);
          paramDisplayContent.mLayoutSeq = m;
          n = k;
          if (WindowManagerDebugConfig.DEBUG_LAYOUT)
          {
            Slog.v(TAG, "  LAYOUT: mFrame=" + paramDisplayContent.mFrame + " mContainingFrame=" + paramDisplayContent.mContainingFrame + " mDisplayFrame=" + paramDisplayContent.mDisplayFrame);
            n = k;
            continue;
            n = k;
            if (paramDisplayContent.mAttrs.type == 2023) {
              n = i;
            }
          }
        }
      }
    }
    this.mService.mInputMonitor.setUpdateInputWindowsNeededLw();
    if (paramBoolean2) {
      this.mService.mInputMonitor.updateInputWindowsLw(false);
    }
    this.mService.mPolicy.finishLayoutLw();
    this.mService.mH.sendEmptyMessage(41);
  }
  
  final void performSurfacePlacement()
  {
    if (this.mDeferDepth > 0) {
      return;
    }
    int i = 6;
    int j;
    do
    {
      this.mTraversalScheduled = false;
      performSurfacePlacementLoop();
      this.mService.mH.removeMessages(4);
      j = i - 1;
      if (!this.mTraversalScheduled) {
        break;
      }
      i = j;
    } while (j > 0);
    this.mWallpaperActionPending = false;
  }
  
  void requestTraversal()
  {
    if (!this.mTraversalScheduled)
    {
      this.mTraversalScheduled = true;
      this.mService.mH.sendEmptyMessage(4);
    }
  }
  
  private static final class LayerAndToken
  {
    public int layer;
    public AppWindowToken token;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/WindowSurfacePlacer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */