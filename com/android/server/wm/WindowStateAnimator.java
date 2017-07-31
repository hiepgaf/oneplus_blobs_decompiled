package com.android.server.wm;

import android.app.ActivityManager.StackId;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Debug;
import android.os.RemoteException;
import android.os.Trace;
import android.util.ArraySet;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.IWindow;
import android.view.MagnificationSpec;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceControl;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerPolicy;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import com.android.server.am.OnePlusProcessManager;
import java.io.PrintWriter;
import java.util.ArrayList;

class WindowStateAnimator
{
  static final int COMMIT_DRAW_PENDING = 2;
  static final int DRAW_PENDING = 1;
  static final int HAS_DRAWN = 4;
  static final int NO_SURFACE = 0;
  static final long PENDING_TRANSACTION_FINISH_WAIT_TIME = 100L;
  static final int READY_TO_SHOW = 3;
  static final int STACK_CLIP_AFTER_ANIM = 0;
  static final int STACK_CLIP_BEFORE_ANIM = 1;
  static final int STACK_CLIP_NONE = 2;
  static final String TAG = "WindowManager";
  static final int WINDOW_FREEZE_LAYER = 2000000;
  float mAlpha = 0.0F;
  private int mAnimDx;
  private int mAnimDy;
  int mAnimLayer;
  private boolean mAnimateMove = false;
  boolean mAnimating;
  Animation mAnimation;
  boolean mAnimationIsEntrance;
  private boolean mAnimationStartDelayed;
  long mAnimationStartTime;
  final WindowAnimator mAnimator;
  AppWindowAnimator mAppAnimator;
  final WindowStateAnimator mAttachedWinAnimator;
  int mAttrType;
  Rect mClipRect = new Rect();
  final Context mContext;
  boolean mDeferMayMiss;
  private boolean mDestroyPreservedSurfaceUponRedraw;
  int mDrawState;
  float mDsDx = 1.0F;
  float mDsDy = 0.0F;
  float mDtDx = 0.0F;
  float mDtDy = 1.0F;
  boolean mEnterAnimationPending;
  boolean mEnteringAnimation;
  float mExtraHScale = 1.0F;
  float mExtraVScale = 1.0F;
  boolean mForceScaleUntilResize;
  boolean mHasClipRect;
  boolean mHasLocalTransformation;
  boolean mHasTransformation;
  boolean mHaveMatrix;
  final boolean mIsWallpaper;
  boolean mKeyguardGoingAwayAnimation;
  boolean mKeyguardGoingAwayWithWallpaper;
  float mLastAlpha = 0.0F;
  long mLastAnimationTime;
  Rect mLastClipRect = new Rect();
  float mLastDsDx = 1.0F;
  float mLastDsDy = 0.0F;
  float mLastDtDx = 0.0F;
  float mLastDtDy = 1.0F;
  Rect mLastFinalClipRect = new Rect();
  boolean mLastHidden;
  int mLastLayer;
  private final Rect mLastSystemDecorRect = new Rect();
  boolean mLocalAnimating;
  private WindowSurfaceController mPendingDestroySurface;
  final WindowManagerPolicy mPolicy;
  boolean mReportSurfaceResized;
  final WindowManagerService mService;
  final Session mSession;
  float mShownAlpha = 0.0F;
  int mStackClip = 1;
  WindowSurfaceController mSurfaceController;
  boolean mSurfaceDestroyDeferred;
  int mSurfaceFormat;
  boolean mSurfaceResized;
  private final Rect mSystemDecorRect = new Rect();
  Rect mTmpClipRect = new Rect();
  Rect mTmpFinalClipRect = new Rect();
  private final Rect mTmpSize = new Rect();
  Rect mTmpStackBounds = new Rect();
  final Transformation mTransformation = new Transformation();
  final WallpaperController mWallpaperControllerLocked;
  boolean mWasAnimating;
  final WindowState mWin;
  
  WindowStateAnimator(WindowState paramWindowState)
  {
    Object localObject1 = paramWindowState.mService;
    this.mService = ((WindowManagerService)localObject1);
    this.mAnimator = ((WindowManagerService)localObject1).mAnimator;
    this.mPolicy = ((WindowManagerService)localObject1).mPolicy;
    this.mContext = ((WindowManagerService)localObject1).mContext;
    localObject1 = paramWindowState.getDisplayContent();
    if (localObject1 != null)
    {
      localObject1 = ((DisplayContent)localObject1).getDisplayInfo();
      this.mAnimDx = ((DisplayInfo)localObject1).appWidth;
      this.mAnimDy = ((DisplayInfo)localObject1).appHeight;
      this.mWin = paramWindowState;
      if (paramWindowState.mAttachedWindow != null) {
        break label339;
      }
      localObject1 = null;
      label269:
      this.mAttachedWinAnimator = ((WindowStateAnimator)localObject1);
      if (paramWindowState.mAppToken != null) {
        break label350;
      }
    }
    label339:
    label350:
    for (localObject1 = localObject2;; localObject1 = paramWindowState.mAppToken.mAppAnimator)
    {
      this.mAppAnimator = ((AppWindowAnimator)localObject1);
      this.mSession = paramWindowState.mSession;
      this.mAttrType = paramWindowState.mAttrs.type;
      this.mIsWallpaper = paramWindowState.mIsWallpaper;
      this.mWallpaperControllerLocked = this.mService.mWallpaperControllerLocked;
      return;
      Slog.w(TAG, "WindowStateAnimator ctor: Display has been removed");
      break;
      localObject1 = paramWindowState.mAttachedWindow.mWinAnimator;
      break label269;
    }
  }
  
  private void adjustCropToStackBounds(WindowState paramWindowState, Rect paramRect1, Rect paramRect2, boolean paramBoolean)
  {
    Object localObject = paramWindowState.getDisplayContent();
    int k;
    if ((localObject == null) || (((DisplayContent)localObject).isDefaultDisplay))
    {
      localObject = paramWindowState.getTask();
      if ((localObject != null) && (((Task)localObject).cropWindowsToStackBounds()))
      {
        k = resolveStackClip();
        if ((!isAnimationSet()) || (k != 2)) {
          break label60;
        }
      }
    }
    else
    {
      return;
    }
    return;
    label60:
    if ((paramWindowState == (WindowState)this.mPolicy.getWinShowWhenLockedLw()) && (this.mPolicy.isKeyguardShowingOrOccluded())) {
      return;
    }
    localObject = ((Task)localObject).mStack;
    ((TaskStack)localObject).getDimBounds(this.mTmpStackBounds);
    Rect localRect = paramWindowState.getAttrs().surfaceInsets;
    int i;
    int j;
    if (paramBoolean)
    {
      i = (int)this.mSurfaceController.getX();
      if (!paramBoolean) {
        break label199;
      }
      j = (int)this.mSurfaceController.getY();
      label144:
      if ((!isAnimationSet()) || (k != 0)) {
        break label225;
      }
    }
    label199:
    label225:
    for (paramBoolean = true;; paramBoolean = this.mDestroyPreservedSurfaceUponRedraw)
    {
      if (!paramBoolean) {
        break label234;
      }
      paramRect2.set(this.mTmpStackBounds);
      return;
      i = paramWindowState.mFrame.left + this.mWin.mXOffset - localRect.left;
      break;
      j = paramWindowState.mFrame.top + this.mWin.mYOffset - localRect.top;
      break label144;
    }
    label234:
    if ((!ActivityManager.StackId.hasWindowShadow(((TaskStack)localObject).mStackId)) || (ActivityManager.StackId.isTaskResizeAllowed(((TaskStack)localObject).mStackId))) {}
    for (;;)
    {
      paramRect1.left = Math.max(0, Math.max(this.mTmpStackBounds.left, paramRect1.left + i) - i);
      paramRect1.top = Math.max(0, Math.max(this.mTmpStackBounds.top, paramRect1.top + j) - j);
      paramRect1.right = Math.max(0, Math.min(this.mTmpStackBounds.right, paramRect1.right + i) - i);
      paramRect1.bottom = Math.max(0, Math.min(this.mTmpStackBounds.bottom, paramRect1.bottom + j) - j);
      return;
      this.mTmpStackBounds.inset(-localRect.left, -localRect.top, -localRect.right, -localRect.bottom);
    }
  }
  
  private void applyFadeoutDuringKeyguardExitAnimation()
  {
    long l1 = this.mAnimation.getStartTime();
    long l2 = this.mAnimation.getDuration();
    long l3 = this.mLastAnimationTime - l1;
    long l4 = l2 - l3;
    if (l4 <= 0L) {
      return;
    }
    AnimationSet localAnimationSet = new AnimationSet(false);
    localAnimationSet.setDuration(l2);
    localAnimationSet.setStartTime(l1);
    localAnimationSet.addAnimation(this.mAnimation);
    Animation localAnimation = AnimationUtils.loadAnimation(this.mContext, 17432593);
    localAnimation.setDuration(l4);
    localAnimation.setStartOffset(l3);
    localAnimationSet.addAnimation(localAnimation);
    localAnimationSet.initialize(this.mWin.mFrame.width(), this.mWin.mFrame.height(), this.mAnimDx, this.mAnimDy);
    this.mAnimation = localAnimationSet;
  }
  
  private void calculateSurfaceBounds(WindowState paramWindowState, WindowManager.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams.flags & 0x4000) != 0)
    {
      this.mTmpSize.right = (this.mTmpSize.left + paramWindowState.mRequestedWidth);
      this.mTmpSize.bottom = (this.mTmpSize.top + paramWindowState.mRequestedHeight);
    }
    for (;;)
    {
      if (this.mTmpSize.width() < 1) {
        this.mTmpSize.right = (this.mTmpSize.left + 1);
      }
      if (this.mTmpSize.height() < 1) {
        this.mTmpSize.bottom = (this.mTmpSize.top + 1);
      }
      paramWindowState = this.mTmpSize;
      paramWindowState.left -= paramLayoutParams.surfaceInsets.left;
      paramWindowState = this.mTmpSize;
      paramWindowState.top -= paramLayoutParams.surfaceInsets.top;
      paramWindowState = this.mTmpSize;
      paramWindowState.right += paramLayoutParams.surfaceInsets.right;
      paramWindowState = this.mTmpSize;
      paramWindowState.bottom += paramLayoutParams.surfaceInsets.bottom;
      return;
      if (paramWindowState.isDragResizing())
      {
        if (paramWindowState.getResizeMode() == 0)
        {
          this.mTmpSize.left = 0;
          this.mTmpSize.top = 0;
        }
        paramWindowState = paramWindowState.getDisplayInfo();
        this.mTmpSize.right = (this.mTmpSize.left + paramWindowState.logicalWidth);
        this.mTmpSize.bottom = (this.mTmpSize.top + paramWindowState.logicalHeight);
      }
      else
      {
        this.mTmpSize.right = (this.mTmpSize.left + paramWindowState.mCompatFrame.width());
        this.mTmpSize.bottom = (this.mTmpSize.top + paramWindowState.mCompatFrame.height());
      }
    }
  }
  
  private void calculateSystemDecorRect()
  {
    int i = 0;
    WindowState localWindowState = this.mWin;
    Rect localRect = localWindowState.mDecorFrame;
    int m = localWindowState.mFrame.width();
    int n = localWindowState.mFrame.height();
    int j = localWindowState.mXOffset + localWindowState.mFrame.left;
    int k = localWindowState.mYOffset + localWindowState.mFrame.top;
    if ((localWindowState.isDockedResizing()) || ((localWindowState.isChildWindow()) && (localWindowState.mAttachedWindow.isDockedResizing())))
    {
      DisplayInfo localDisplayInfo = localWindowState.getDisplayContent().getDisplayInfo();
      this.mSystemDecorRect.set(0, 0, Math.max(m, localDisplayInfo.logicalWidth), Math.max(n, localDisplayInfo.logicalHeight));
      if ((!localWindowState.inFreeformWorkspace()) || (!localWindowState.isAnimatingLw())) {
        break label323;
      }
    }
    for (;;)
    {
      if (i != 0) {
        this.mSystemDecorRect.intersect(localRect.left - j, localRect.top - k, localRect.right - j, localRect.bottom - k);
      }
      if ((localWindowState.mEnforceSizeCompat) && (localWindowState.mInvGlobalScale != 1.0F))
      {
        float f = localWindowState.mInvGlobalScale;
        this.mSystemDecorRect.left = ((int)(this.mSystemDecorRect.left * f - 0.5F));
        this.mSystemDecorRect.top = ((int)(this.mSystemDecorRect.top * f - 0.5F));
        this.mSystemDecorRect.right = ((int)((this.mSystemDecorRect.right + 1) * f - 0.5F));
        this.mSystemDecorRect.bottom = ((int)((this.mSystemDecorRect.bottom + 1) * f - 0.5F));
      }
      return;
      this.mSystemDecorRect.set(0, 0, m, n);
      break;
      label323:
      i = 1;
    }
  }
  
  private long getAnimationFrameTime(Animation paramAnimation, long paramLong)
  {
    if (this.mAnimationStartDelayed)
    {
      paramAnimation.setStartTime(paramLong);
      return 1L + paramLong;
    }
    return paramLong;
  }
  
  private int resolveStackClip()
  {
    if ((this.mAppAnimator != null) && (this.mAppAnimator.animation != null)) {
      return this.mAppAnimator.getStackClip();
    }
    return this.mStackClip;
  }
  
  private boolean showSurfaceRobustlyLocked()
  {
    Object localObject = this.mWin.getTask();
    if ((localObject != null) && (ActivityManager.StackId.windowsAreScaleable(((Task)localObject).mStack.mStackId))) {
      this.mSurfaceController.forceScaleableInTransaction(true);
    }
    if (!this.mSurfaceController.showRobustlyInTransaction()) {
      return false;
    }
    if (this.mWin.mTurnOnScreen)
    {
      if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
        Slog.v(TAG, "Show surface turning screen on: " + this.mWin);
      }
      this.mWin.mTurnOnScreen = false;
      localObject = this.mAnimator;
      ((WindowAnimator)localObject).mBulkUpdateParams |= 0x10;
    }
    return true;
  }
  
  private boolean stepAnimation(long paramLong)
  {
    if ((this.mAnimation != null) && (this.mLocalAnimating))
    {
      paramLong = getAnimationFrameTime(this.mAnimation, paramLong);
      this.mTransformation.clear();
      boolean bool = this.mAnimation.getTransformation(paramLong, this.mTransformation);
      if ((this.mAnimationStartDelayed) && (this.mAnimationIsEntrance)) {
        this.mTransformation.setAlpha(0.0F);
      }
      return bool;
    }
    return false;
  }
  
  boolean applyAnimationLocked(int paramInt, boolean paramBoolean)
  {
    if ((this.mLocalAnimating) && (this.mAnimationIsEntrance == paramBoolean)) {}
    while (this.mKeyguardGoingAwayAnimation)
    {
      if ((this.mAnimation != null) && (this.mKeyguardGoingAwayAnimation) && (paramInt == 5)) {
        applyFadeoutDuringKeyguardExitAnimation();
      }
      return true;
    }
    Trace.traceBegin(32L, "WSA#applyAnimationLocked");
    int i;
    Animation localAnimation;
    int j;
    if (this.mService.okToDisplay())
    {
      int k = this.mPolicy.selectAnimationLw(this.mWin, paramInt);
      i = -1;
      localAnimation = null;
      if (k != 0) {
        if (k != -1)
        {
          localAnimation = AnimationUtils.loadAnimation(this.mContext, k);
          j = i;
          if (WindowManagerDebugConfig.DEBUG_ANIM) {
            Slog.v(TAG, "applyAnimation: win=" + this + " anim=" + k + " attr=0x" + Integer.toHexString(j) + " a=" + localAnimation + " transit=" + paramInt + " isEntrance=" + paramBoolean + " Callers " + Debug.getCallers(3));
          }
          if (localAnimation != null)
          {
            if (WindowManagerDebugConfig.DEBUG_ANIM) {
              WindowManagerService.logWithStack(TAG, "Loaded animation " + localAnimation + " for " + this);
            }
            setAnimation(localAnimation);
            this.mAnimationIsEntrance = paramBoolean;
          }
        }
      }
    }
    for (;;)
    {
      Trace.traceEnd(32L);
      if (this.mWin.mAttrs.type == 2011)
      {
        this.mService.adjustForImeIfNeeded(this.mWin.mDisplayContent);
        if (paramBoolean)
        {
          this.mWin.setDisplayLayoutNeeded();
          this.mService.mWindowPlacerLocked.requestTraversal();
        }
      }
      if (this.mAnimation == null) {
        break label440;
      }
      return true;
      localAnimation = null;
      j = i;
      break;
      switch (paramInt)
      {
      }
      for (;;)
      {
        j = i;
        if (i < 0) {
          break;
        }
        localAnimation = this.mService.mAppTransition.loadAnimationAttr(this.mWin.mAttrs, i);
        j = i;
        break;
        i = 0;
        continue;
        i = 1;
        continue;
        i = 2;
        continue;
        i = 3;
      }
      clearAnimation();
    }
    label440:
    return false;
  }
  
  void applyEnterAnimationLocked()
  {
    if (this.mWin.mSkipEnterAnimationForSeamlessReplacement) {
      return;
    }
    if (this.mEnterAnimationPending) {
      this.mEnterAnimationPending = false;
    }
    for (int i = 1;; i = 3)
    {
      applyAnimationLocked(i, true);
      if ((this.mService.mAccessibilityController != null) && (this.mWin.getDisplayId() == 0)) {
        this.mService.mAccessibilityController.onWindowTransitionLocked(this.mWin, i);
      }
      return;
    }
  }
  
  void applyMagnificationSpec(MagnificationSpec paramMagnificationSpec, Matrix paramMatrix)
  {
    int i = this.mWin.mAttrs.surfaceInsets.left;
    int j = this.mWin.mAttrs.surfaceInsets.top;
    if ((paramMagnificationSpec == null) || (paramMagnificationSpec.isNop())) {
      return;
    }
    float f = paramMagnificationSpec.scale;
    paramMatrix.postScale(f, f);
    paramMatrix.postTranslate(paramMagnificationSpec.offsetX, paramMagnificationSpec.offsetY);
    paramMatrix.postTranslate(-(i * f - i), -(j * f - j));
  }
  
  void calculateSurfaceWindowCrop(Rect paramRect1, Rect paramRect2)
  {
    WindowState localWindowState = this.mWin;
    Object localObject = localWindowState.getDisplayContent();
    if (localObject == null)
    {
      paramRect1.setEmpty();
      paramRect2.setEmpty();
      return;
    }
    localObject = ((DisplayContent)localObject).getDisplayInfo();
    if (WindowManagerDebugConfig.DEBUG_WINDOW_CROP) {
      Slog.d(TAG, "Updating crop win=" + localWindowState + " mLastCrop=" + this.mLastClipRect);
    }
    boolean bool2;
    boolean bool1;
    if (!localWindowState.isDefaultDisplay())
    {
      this.mSystemDecorRect.set(0, 0, localWindowState.mCompatFrame.width(), localWindowState.mCompatFrame.height());
      this.mSystemDecorRect.intersect(-localWindowState.mCompatFrame.left, -localWindowState.mCompatFrame.top, ((DisplayInfo)localObject).logicalWidth - localWindowState.mCompatFrame.left, ((DisplayInfo)localObject).logicalHeight - localWindowState.mCompatFrame.top);
      bool2 = localWindowState.isFrameFullscreen((DisplayInfo)localObject);
      if ((!localWindowState.isDragResizing()) || (localWindowState.getResizeMode() != 0)) {
        break label707;
      }
      bool1 = true;
      label195:
      if ((this.mHasClipRect) && (!bool2)) {
        break label712;
      }
      localObject = this.mSystemDecorRect;
      label213:
      paramRect1.set((Rect)localObject);
      if (WindowManagerDebugConfig.DEBUG_WINDOW_CROP) {
        Slog.d(TAG, "win=" + localWindowState + " Initial clip rect: " + paramRect1 + " mHasClipRect=" + this.mHasClipRect + " fullscreen=" + bool2);
      }
      if ((bool1) && (!localWindowState.isChildWindow())) {
        break label721;
      }
    }
    for (;;)
    {
      localObject = localWindowState.mAttrs;
      paramRect1.left -= ((WindowManager.LayoutParams)localObject).surfaceInsets.left;
      paramRect1.top -= ((WindowManager.LayoutParams)localObject).surfaceInsets.top;
      paramRect1.right += ((WindowManager.LayoutParams)localObject).surfaceInsets.right;
      paramRect1.bottom += ((WindowManager.LayoutParams)localObject).surfaceInsets.bottom;
      if ((this.mHasClipRect) && (bool2)) {
        paramRect1.intersect(this.mClipRect);
      }
      paramRect1.offset(((WindowManager.LayoutParams)localObject).surfaceInsets.left, ((WindowManager.LayoutParams)localObject).surfaceInsets.top);
      paramRect2.setEmpty();
      adjustCropToStackBounds(localWindowState, paramRect1, paramRect2, bool1);
      if (WindowManagerDebugConfig.DEBUG_WINDOW_CROP) {
        Slog.d(TAG, "win=" + localWindowState + " Clip rect after stack adjustment=" + paramRect1);
      }
      localWindowState.transformClipRectFromScreenToSurfaceSpace(paramRect1);
      if ((localWindowState.hasJustMovedInStack()) && (this.mLastClipRect.isEmpty()) && (!paramRect1.isEmpty())) {
        break label744;
      }
      return;
      if (localWindowState.mLayer >= this.mService.mSystemDecorLayer)
      {
        this.mSystemDecorRect.set(0, 0, localWindowState.mCompatFrame.width(), localWindowState.mCompatFrame.height());
        break;
      }
      if (localWindowState.mDecorFrame.isEmpty())
      {
        this.mSystemDecorRect.set(0, 0, localWindowState.mCompatFrame.width(), localWindowState.mCompatFrame.height());
        break;
      }
      if ((localWindowState.mAttrs.type == 2013) && (this.mAnimator.isAnimating()))
      {
        this.mTmpClipRect.set(this.mSystemDecorRect);
        calculateSystemDecorRect();
        this.mSystemDecorRect.union(this.mTmpClipRect);
        break;
      }
      calculateSystemDecorRect();
      if (!WindowManagerDebugConfig.DEBUG_WINDOW_CROP) {
        break;
      }
      Slog.d(TAG, "Applying decor to crop win=" + localWindowState + " mDecorFrame=" + localWindowState.mDecorFrame + " mSystemDecorRect=" + this.mSystemDecorRect);
      break;
      label707:
      bool1 = false;
      break label195;
      label712:
      localObject = this.mClipRect;
      break label213;
      label721:
      paramRect1.offset(localWindowState.mShownPosition.x, localWindowState.mShownPosition.y);
    }
    label744:
    paramRect1.setEmpty();
  }
  
  void cancelExitAnimationForNextAnimationLocked()
  {
    if (WindowManagerDebugConfig.DEBUG_ANIM) {
      Slog.d(TAG, "cancelExitAnimationForNextAnimationLocked: " + this.mWin);
    }
    if (this.mAnimation != null)
    {
      this.mAnimation.cancel();
      this.mAnimation = null;
      this.mLocalAnimating = false;
      this.mWin.destroyOrSaveSurface();
    }
  }
  
  public void clearAnimation()
  {
    if (this.mAnimation != null)
    {
      this.mAnimating = true;
      this.mLocalAnimating = false;
      this.mAnimation.cancel();
      this.mAnimation = null;
      this.mKeyguardGoingAwayAnimation = false;
      this.mKeyguardGoingAwayWithWallpaper = false;
      this.mStackClip = 1;
    }
  }
  
  boolean commitFinishDrawingLocked()
  {
    if ((WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) && (this.mWin.mAttrs.type == 3)) {
      Slog.i(TAG, "commitFinishDrawingLocked: " + this.mWin + " cur mDrawState=" + drawStateToString());
    }
    if ((this.mDrawState != 2) && (this.mDrawState != 3)) {
      return false;
    }
    if ((WindowManagerDebugConfig.DEBUG_SURFACE_TRACE) || (WindowManagerDebugConfig.DEBUG_ANIM)) {
      Slog.i(TAG, "commitFinishDrawingLocked: mDrawState=READY_TO_SHOW " + this.mSurfaceController);
    }
    this.mDrawState = 3;
    boolean bool = false;
    AppWindowToken localAppWindowToken = this.mWin.mAppToken;
    if ((localAppWindowToken == null) || (localAppWindowToken.allDrawn) || (this.mWin.mAttrs.type == 3)) {
      bool = performShowLocked();
    }
    return bool;
  }
  
  void computeShownFrameLocked()
  {
    boolean bool2 = this.mHasLocalTransformation;
    Object localObject3;
    label58:
    Object localObject6;
    Object localObject4;
    Object localObject5;
    label159:
    label170:
    label234:
    int i;
    boolean bool1;
    label264:
    label279:
    float f1;
    float f2;
    if ((this.mAttachedWinAnimator != null) && (this.mAttachedWinAnimator.mHasLocalTransformation))
    {
      localObject1 = this.mAttachedWinAnimator.mTransformation;
      if ((this.mAppAnimator == null) || (!this.mAppAnimator.hasTransformation)) {
        break label1036;
      }
      localObject3 = this.mAppAnimator.transformation;
      localObject6 = this.mWallpaperControllerLocked.getWallpaperTarget();
      localObject4 = localObject3;
      localObject5 = localObject1;
      if (this.mIsWallpaper)
      {
        localObject4 = localObject3;
        localObject5 = localObject1;
        if (localObject6 != null)
        {
          localObject4 = localObject3;
          localObject5 = localObject1;
          if (this.mService.mAnimateWallpaperWithTarget)
          {
            localObject4 = ((WindowState)localObject6).mWinAnimator;
            localObject2 = localObject1;
            if (((WindowStateAnimator)localObject4).mHasLocalTransformation)
            {
              localObject2 = localObject1;
              if (((WindowStateAnimator)localObject4).mAnimation != null)
              {
                if (!((WindowStateAnimator)localObject4).mAnimation.getDetachWallpaper()) {
                  break label1042;
                }
                localObject2 = localObject1;
              }
            }
            if (((WindowState)localObject6).mAppToken != null) {
              break label1103;
            }
            localObject1 = null;
            localObject4 = localObject3;
            localObject5 = localObject2;
            if (localObject1 != null)
            {
              localObject4 = localObject3;
              localObject5 = localObject2;
              if (((AppWindowAnimator)localObject1).hasTransformation)
              {
                localObject4 = localObject3;
                localObject5 = localObject2;
                if (((AppWindowAnimator)localObject1).animation != null)
                {
                  if (!((AppWindowAnimator)localObject1).animation.getDetachWallpaper()) {
                    break label1116;
                  }
                  localObject5 = localObject2;
                  localObject4 = localObject3;
                }
              }
            }
          }
        }
      }
      i = this.mWin.getDisplayId();
      localObject2 = this.mAnimator.getScreenRotationAnimationLocked(i);
      if (localObject2 == null) {
        break label1189;
      }
      bool1 = ((ScreenRotationAnimation)localObject2).isAnimating();
      this.mHasClipRect = false;
      if ((!bool2) && (localObject5 == null)) {
        break label1195;
      }
      localObject1 = this.mWin.mFrame;
      localObject3 = this.mService.mTmpFloats;
      localObject6 = this.mWin.mTmpMatrix;
      if ((!bool1) || (!((ScreenRotationAnimation)localObject2).isRotating())) {
        break label1234;
      }
      f1 = ((Rect)localObject1).width();
      f2 = ((Rect)localObject1).height();
      if ((f1 < 1.0F) || (f2 < 1.0F)) {
        break label1226;
      }
      ((Matrix)localObject6).setScale(2.0F / f1 + 1.0F, 2.0F / f2 + 1.0F, f1 / 2.0F, f2 / 2.0F);
      label366:
      ((Matrix)localObject6).postScale(this.mWin.mGlobalScale, this.mWin.mGlobalScale);
      if (bool2) {
        ((Matrix)localObject6).postConcat(this.mTransformation.getMatrix());
      }
      if (localObject5 != null) {
        ((Matrix)localObject6).postConcat(((Transformation)localObject5).getMatrix());
      }
      if (localObject4 != null) {
        ((Matrix)localObject6).postConcat(((Transformation)localObject4).getMatrix());
      }
      ((Matrix)localObject6).postTranslate(((Rect)localObject1).left + this.mWin.mXOffset, ((Rect)localObject1).top + this.mWin.mYOffset);
      if (bool1) {
        ((Matrix)localObject6).postConcat(((ScreenRotationAnimation)localObject2).getEnterTransformation().getMatrix());
      }
      if ((this.mService.mAccessibilityController != null) && (i == 0)) {
        applyMagnificationSpec(this.mService.mAccessibilityController.getMagnificationSpecForWindowLocked(this.mWin), (Matrix)localObject6);
      }
      this.mHaveMatrix = true;
      ((Matrix)localObject6).getValues((float[])localObject3);
      this.mDsDx = localObject3[0];
      this.mDtDx = localObject3[3];
      this.mDsDy = localObject3[1];
      this.mDtDy = localObject3[4];
      f1 = localObject3[2];
      f2 = localObject3[5];
      this.mWin.mShownPosition.set((int)f1, (int)f2);
      this.mShownAlpha = this.mAlpha;
      if ((!this.mService.mLimitedAlphaCompositing) || (!PixelFormat.formatHasAlpha(this.mWin.mAttrs.format)) || ((this.mWin.isIdentityMatrix(this.mDsDx, this.mDtDx, this.mDsDy, this.mDtDy)) && (f1 == ((Rect)localObject1).left) && (f2 == ((Rect)localObject1).top)))
      {
        if (bool2) {
          this.mShownAlpha *= this.mTransformation.getAlpha();
        }
        if (localObject5 != null) {
          this.mShownAlpha *= ((Transformation)localObject5).getAlpha();
        }
        if (localObject4 != null)
        {
          this.mShownAlpha *= ((Transformation)localObject4).getAlpha();
          if (((Transformation)localObject4).hasClipRect())
          {
            this.mClipRect.set(((Transformation)localObject4).getClipRect());
            this.mHasClipRect = true;
            if (this.mWin.layoutInParentFrame()) {
              this.mClipRect.offset(this.mWin.mContainingFrame.left - this.mWin.mFrame.left, this.mWin.mContainingFrame.top - this.mWin.mFrame.top);
            }
          }
        }
        if (bool1) {
          this.mShownAlpha *= ((ScreenRotationAnimation)localObject2).getEnterTransformation().getAlpha();
        }
      }
      if (((WindowManagerDebugConfig.DEBUG_SURFACE_TRACE) || (WindowManagerService.localLOGV)) && ((this.mShownAlpha == 1.0D) || (this.mShownAlpha == 0.0D)))
      {
        localObject3 = TAG;
        localObject6 = new StringBuilder().append("computeShownFrameLocked: Animating ").append(this).append(" mAlpha=").append(this.mAlpha).append(" self=");
        if (!bool2) {
          break label1242;
        }
        localObject1 = Float.valueOf(this.mTransformation.getAlpha());
        label930:
        localObject6 = ((StringBuilder)localObject6).append(localObject1).append(" attached=");
        if (localObject5 != null) {
          break label1250;
        }
        localObject1 = "null";
        label955:
        localObject5 = ((StringBuilder)localObject6).append(localObject1).append(" app=");
        if (localObject4 != null) {
          break label1263;
        }
        localObject1 = "null";
        label980:
        localObject4 = ((StringBuilder)localObject5).append(localObject1).append(" screen=");
        if (!bool1) {
          break label1276;
        }
      }
    }
    label1036:
    label1042:
    label1103:
    label1116:
    label1189:
    label1195:
    label1226:
    label1234:
    label1242:
    label1250:
    label1263:
    label1276:
    for (Object localObject1 = Float.valueOf(((ScreenRotationAnimation)localObject2).getEnterTransformation().getAlpha());; localObject1 = "null")
    {
      Slog.v((String)localObject3, localObject1);
      return;
      localObject1 = null;
      break;
      localObject3 = null;
      break label58;
      localObject1 = ((WindowStateAnimator)localObject4).mTransformation;
      localObject2 = localObject1;
      if (!WindowManagerDebugConfig.DEBUG_WALLPAPER) {
        break label159;
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        break label159;
      }
      Slog.v(TAG, "WP target attached xform: " + localObject1);
      localObject2 = localObject1;
      break label159;
      localObject1 = ((WindowState)localObject6).mAppToken.mAppAnimator;
      break label170;
      localObject1 = ((AppWindowAnimator)localObject1).transformation;
      localObject4 = localObject1;
      localObject5 = localObject2;
      if (!WindowManagerDebugConfig.DEBUG_WALLPAPER) {
        break label234;
      }
      localObject4 = localObject1;
      localObject5 = localObject2;
      if (localObject1 == null) {
        break label234;
      }
      Slog.v(TAG, "WP target app xform: " + localObject1);
      localObject4 = localObject1;
      localObject5 = localObject2;
      break label234;
      bool1 = false;
      break label264;
      if ((localObject4 != null) || (bool1)) {
        break label279;
      }
      if ((!this.mIsWallpaper) || (!this.mService.mWindowPlacerLocked.mWallpaperActionPending)) {
        break label1284;
      }
      return;
      ((Matrix)localObject6).reset();
      break label366;
      ((Matrix)localObject6).reset();
      break label366;
      localObject1 = "null";
      break label930;
      localObject1 = Float.valueOf(((Transformation)localObject5).getAlpha());
      break label955;
      localObject1 = Float.valueOf(((Transformation)localObject4).getAlpha());
      break label980;
    }
    label1284:
    if (this.mWin.isDragResizeChanged()) {
      return;
    }
    if (WindowManagerService.localLOGV) {
      Slog.v(TAG, "computeShownFrameLocked: " + this + " not attached, mAlpha=" + this.mAlpha);
    }
    Object localObject2 = null;
    localObject1 = localObject2;
    if (this.mService.mAccessibilityController != null)
    {
      localObject1 = localObject2;
      if (i == 0) {
        localObject1 = this.mService.mAccessibilityController.getMagnificationSpecForWindowLocked(this.mWin);
      }
    }
    if (localObject1 != null)
    {
      localObject2 = this.mWin.mFrame;
      localObject3 = this.mService.mTmpFloats;
      localObject4 = this.mWin.mTmpMatrix;
      ((Matrix)localObject4).setScale(this.mWin.mGlobalScale, this.mWin.mGlobalScale);
      ((Matrix)localObject4).postTranslate(((Rect)localObject2).left + this.mWin.mXOffset, ((Rect)localObject2).top + this.mWin.mYOffset);
      applyMagnificationSpec((MagnificationSpec)localObject1, (Matrix)localObject4);
      ((Matrix)localObject4).getValues((float[])localObject3);
      this.mHaveMatrix = true;
      this.mDsDx = localObject3[0];
      this.mDtDx = localObject3[3];
      this.mDsDy = localObject3[1];
      this.mDtDy = localObject3[4];
      f1 = localObject3[2];
      f2 = localObject3[5];
      this.mWin.mShownPosition.set((int)f1, (int)f2);
      this.mShownAlpha = this.mAlpha;
      return;
    }
    this.mWin.mShownPosition.set(this.mWin.mFrame.left, this.mWin.mFrame.top);
    if ((this.mWin.mXOffset != 0) || (this.mWin.mYOffset != 0)) {
      this.mWin.mShownPosition.offset(this.mWin.mXOffset, this.mWin.mYOffset);
    }
    this.mShownAlpha = this.mAlpha;
    this.mHaveMatrix = false;
    this.mDsDx = this.mWin.mGlobalScale;
    this.mDtDx = 0.0F;
    this.mDsDy = 0.0F;
    this.mDtDy = this.mWin.mGlobalScale;
  }
  
  WindowSurfaceController createSurfaceLocked()
  {
    WindowState localWindowState = this.mWin;
    if (localWindowState.hasSavedSurface())
    {
      if (WindowManagerDebugConfig.DEBUG_ANIM) {
        Slog.i(TAG, "createSurface: " + this + ": called when we had a saved surface");
      }
      localWindowState.restoreSavedSurface();
      return this.mSurfaceController;
    }
    if (this.mSurfaceController != null) {
      return this.mSurfaceController;
    }
    localWindowState.setHasSurface(false);
    if ((WindowManagerDebugConfig.DEBUG_ANIM) || (WindowManagerDebugConfig.DEBUG_ORIENTATION)) {
      Slog.i(TAG, "createSurface " + this + ": mDrawState=DRAW_PENDING");
    }
    this.mDrawState = 1;
    int i;
    WindowManager.LayoutParams localLayoutParams;
    int m;
    int n;
    if (localWindowState.mAppToken != null)
    {
      if (localWindowState.mAppToken.mAppAnimator.animation == null) {
        localWindowState.mAppToken.clearAllDrawn();
      }
    }
    else
    {
      this.mService.makeWindowFreezingScreenIfNeededLocked(localWindowState);
      i = 4;
      localLayoutParams = localWindowState.mAttrs;
      if (this.mService.isSecureLocked(localWindowState)) {
        i = 132;
      }
      this.mTmpSize.set(localWindowState.mFrame.left + localWindowState.mXOffset, localWindowState.mFrame.top + localWindowState.mYOffset, 0, 0);
      calculateSurfaceBounds(localWindowState, localLayoutParams);
      m = this.mTmpSize.width();
      n = this.mTmpSize.height();
      if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
        Slog.v(TAG, "Creating surface in session " + this.mSession.mSurfaceSession + " window " + this + " w=" + m + " h=" + n + " x=" + this.mTmpSize.left + " y=" + this.mTmpSize.top + " format=" + localLayoutParams.format + " flags=" + i);
      }
      this.mLastSystemDecorRect.set(0, 0, 0, 0);
      this.mHasClipRect = false;
      this.mClipRect.set(0, 0, 0, 0);
      this.mLastClipRect.set(0, 0, 0, 0);
      if (OnePlusProcessManager.isSupportFrozenApp()) {
        OnePlusProcessManager.resumeProcessByUID_out(localWindowState.mOwnerUid, "createSurfaceLocked");
      }
    }
    for (;;)
    {
      try
      {
        if ((localLayoutParams.flags & 0x1000000) != 0)
        {
          j = 1;
          break label1112;
          k = i;
          if (!PixelFormat.formatHasAlpha(localLayoutParams.format))
          {
            k = i;
            if (localLayoutParams.surfaceInsets.left == 0)
            {
              k = i;
              if (localLayoutParams.surfaceInsets.top == 0)
              {
                k = i;
                if (localLayoutParams.surfaceInsets.right == 0)
                {
                  k = i;
                  if (localLayoutParams.surfaceInsets.bottom == 0)
                  {
                    if (!localWindowState.isDragResizing()) {
                      continue;
                    }
                    k = i;
                  }
                }
              }
            }
          }
          this.mSurfaceController = new WindowSurfaceController(this.mSession.mSurfaceSession, localLayoutParams.getTitle().toString(), m, n, j, k, this);
          localWindowState.setHasSurface(true);
          if ((WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.SHOW_SURFACE_ALLOC)) {
            Slog.i(TAG, "  CREATE SURFACE " + this.mSurfaceController + " IN SESSION " + this.mSession.mSurfaceSession + ": pid=" + this.mSession.mPid + " format=" + localLayoutParams.format + " flags=0x" + Integer.toHexString(k) + " / " + this);
          }
          if (WindowManagerService.localLOGV) {
            Slog.v(TAG, "Got surface: " + this.mSurfaceController + ", set left=" + localWindowState.mFrame.left + " top=" + localWindowState.mFrame.top + ", animLayer=" + this.mAnimLayer);
          }
          if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS)
          {
            Slog.i(TAG, ">>> OPEN TRANSACTION createSurfaceLocked");
            WindowManagerService.logSurface(localWindowState, "CREATE pos=(" + localWindowState.mFrame.left + "," + localWindowState.mFrame.top + ") (" + m + "x" + n + "), layer=" + this.mAnimLayer + " HIDE", false);
          }
          if (WindowManagerDebugConfig.DEBUG_ONEPLUS) {
            WindowManagerService.logSurface(this.mWin, "CREATE pid=" + this.mSession.mPid + " format=" + localLayoutParams.format + " layer=" + this.mAnimLayer, false);
          }
          i = localWindowState.getDisplayContent().getDisplay().getLayerStack();
          this.mSurfaceController.setPositionAndLayer(this.mTmpSize.left, this.mTmpSize.top, i, this.mAnimLayer);
          this.mLastHidden = true;
          if (WindowManagerService.localLOGV) {
            Slog.v(TAG, "Created surface " + this);
          }
          return this.mSurfaceController;
          localWindowState.mAppToken.deferClearAllDrawn = true;
          break;
        }
        j = 0;
        break label1112;
        j = localLayoutParams.format;
        continue;
        int k = i | 0x400;
        continue;
        if (j == 0) {
          continue;
        }
      }
      catch (Exception localException)
      {
        Slog.e(TAG, "Exception creating surface", localException);
        this.mDrawState = 0;
        return null;
      }
      catch (Surface.OutOfResourcesException localOutOfResourcesException)
      {
        Slog.w(TAG, "OutOfResourcesException creating surface");
        this.mService.reclaimSomeSurfaceMemoryLocked(this, "create", true);
        this.mDrawState = 0;
        return null;
      }
      label1112:
      int j = -3;
    }
  }
  
  void deferTransactionUntilParentFrame(long paramLong)
  {
    if (!this.mWin.isChildWindow()) {
      return;
    }
    this.mSurfaceController.deferTransactionUntil(this.mWin.mAttachedWindow.mWinAnimator.mSurfaceController.getHandle(), paramLong);
  }
  
  void destroyDeferredSurfaceLocked()
  {
    try
    {
      if (this.mPendingDestroySurface != null)
      {
        if ((WindowManagerDebugConfig.DEBUG_ONEPLUS) || (WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.SHOW_SURFACE_ALLOC)) {
          WindowManagerService.logSurface(this.mWin, "DESTROY PENDING", true);
        }
        this.mPendingDestroySurface.destroyInTransaction();
        if (!this.mDestroyPreservedSurfaceUponRedraw) {
          this.mWallpaperControllerLocked.hideWallpapers(this.mWin);
        }
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        Slog.w(TAG, "Exception thrown when destroying Window " + this + " surface " + this.mPendingDestroySurface + " session " + this.mSession + ": " + localRuntimeException.toString());
      }
    }
    this.mSurfaceDestroyDeferred = false;
    this.mPendingDestroySurface = null;
    this.mDeferMayMiss = false;
  }
  
  void destroyPreservedSurfaceLocked()
  {
    if (!this.mDestroyPreservedSurfaceUponRedraw) {
      return;
    }
    destroyDeferredSurfaceLocked();
    this.mDestroyPreservedSurfaceUponRedraw = false;
  }
  
  void destroySurface()
  {
    try
    {
      if (this.mSurfaceController != null) {
        this.mSurfaceController.destroyInTransaction();
      }
      this.mWin.setHasSurface(false);
      this.mSurfaceController = null;
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        Slog.w(TAG, "Exception thrown when destroying surface " + this + " surface " + this.mSurfaceController + " session " + this.mSession + ": " + localRuntimeException);
        this.mWin.setHasSurface(false);
        this.mSurfaceController = null;
      }
    }
    finally
    {
      this.mWin.setHasSurface(false);
      this.mSurfaceController = null;
      this.mDrawState = 0;
    }
    this.mDrawState = 0;
  }
  
  void destroySurfaceLocked()
  {
    AppWindowToken localAppWindowToken = this.mWin.mAppToken;
    if ((localAppWindowToken != null) && (this.mWin == localAppWindowToken.startingWindow)) {
      localAppWindowToken.startingDisplayed = false;
    }
    this.mWin.clearHasSavedSurface();
    if (this.mSurfaceController == null) {
      return;
    }
    int i = this.mWin.mChildWindows.size();
    while ((!this.mDestroyPreservedSurfaceUponRedraw) && (i > 0))
    {
      i -= 1;
      ((WindowState)this.mWin.mChildWindows.get(i)).mAttachedHidden = true;
    }
    for (;;)
    {
      try
      {
        if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
          WindowManagerService.logWithStack(TAG, "Window " + this + " destroying surface " + this.mSurfaceController + ", session " + this.mSession);
        }
        if (!this.mSurfaceDestroyDeferred) {
          continue;
        }
        if ((this.mSurfaceController != null) && (this.mPendingDestroySurface != this.mSurfaceController))
        {
          if (this.mPendingDestroySurface != null)
          {
            if ((WindowManagerDebugConfig.DEBUG_ONEPLUS) || (WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.SHOW_SURFACE_ALLOC)) {
              WindowManagerService.logSurface(this.mWin, "DESTROY PENDING", true);
            }
            this.mPendingDestroySurface.destroyInTransaction();
          }
          this.mPendingDestroySurface = this.mSurfaceController;
        }
        if (!this.mDestroyPreservedSurfaceUponRedraw) {
          this.mWallpaperControllerLocked.hideWallpapers(this.mWin);
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        Slog.w(TAG, "Exception thrown when destroying Window " + this + " surface " + this.mSurfaceController + " session " + this.mSession + ": " + localRuntimeException.toString());
        continue;
      }
      this.mWin.setHasSurface(false);
      if (this.mSurfaceController != null) {
        this.mSurfaceController.setShown(false);
      }
      this.mSurfaceController = null;
      this.mDrawState = 0;
      return;
      if ((WindowManagerDebugConfig.DEBUG_ONEPLUS) || (WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.SHOW_SURFACE_ALLOC)) {
        WindowManagerService.logSurface(this.mWin, "DESTROY", true);
      }
      destroySurface();
      if (this.mDeferMayMiss)
      {
        Slog.e(TAG, "we may miss a defer destroy, so destroy deferred surface here " + this);
        destroyDeferredSurfaceLocked();
        this.mDeferMayMiss = false;
      }
    }
  }
  
  String drawStateToString()
  {
    switch (this.mDrawState)
    {
    default: 
      return Integer.toString(this.mDrawState);
    case 0: 
      return "NO_SURFACE";
    case 1: 
      return "DRAW_PENDING";
    case 2: 
      return "COMMIT_DRAW_PENDING";
    case 3: 
      return "READY_TO_SHOW";
    }
    return "HAS_DRAWN";
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString, boolean paramBoolean)
  {
    if ((this.mAnimating) || (this.mLocalAnimating) || (this.mAnimationIsEntrance) || (this.mAnimation != null))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mAnimating=");
      paramPrintWriter.print(this.mAnimating);
      paramPrintWriter.print(" mLocalAnimating=");
      paramPrintWriter.print(this.mLocalAnimating);
      paramPrintWriter.print(" mAnimationIsEntrance=");
      paramPrintWriter.print(this.mAnimationIsEntrance);
      paramPrintWriter.print(" mAnimation=");
      paramPrintWriter.print(this.mAnimation);
      paramPrintWriter.print(" mStackClip=");
      paramPrintWriter.println(this.mStackClip);
    }
    if ((this.mHasTransformation) || (this.mHasLocalTransformation))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("XForm: has=");
      paramPrintWriter.print(this.mHasTransformation);
      paramPrintWriter.print(" hasLocal=");
      paramPrintWriter.print(this.mHasLocalTransformation);
      paramPrintWriter.print(" ");
      this.mTransformation.printShortString(paramPrintWriter);
      paramPrintWriter.println();
    }
    if (this.mSurfaceController != null) {
      this.mSurfaceController.dump(paramPrintWriter, paramString, paramBoolean);
    }
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mDrawState=");
      paramPrintWriter.print(drawStateToString());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(" mLastHidden=");
      paramPrintWriter.println(this.mLastHidden);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSystemDecorRect=");
      this.mSystemDecorRect.printShortString(paramPrintWriter);
      paramPrintWriter.print(" last=");
      this.mLastSystemDecorRect.printShortString(paramPrintWriter);
      paramPrintWriter.print(" mHasClipRect=");
      paramPrintWriter.print(this.mHasClipRect);
      paramPrintWriter.print(" mLastClipRect=");
      this.mLastClipRect.printShortString(paramPrintWriter);
      if (!this.mLastFinalClipRect.isEmpty())
      {
        paramPrintWriter.print(" mLastFinalClipRect=");
        this.mLastFinalClipRect.printShortString(paramPrintWriter);
      }
      paramPrintWriter.println();
    }
    if (this.mPendingDestroySurface != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mPendingDestroySurface=");
      paramPrintWriter.println(this.mPendingDestroySurface);
    }
    if ((this.mSurfaceResized) || (this.mSurfaceDestroyDeferred))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSurfaceResized=");
      paramPrintWriter.print(this.mSurfaceResized);
      paramPrintWriter.print(" mSurfaceDestroyDeferred=");
      paramPrintWriter.println(this.mSurfaceDestroyDeferred);
    }
    if ((this.mShownAlpha != 1.0F) || (this.mAlpha != 1.0F)) {}
    for (;;)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mShownAlpha=");
      paramPrintWriter.print(this.mShownAlpha);
      paramPrintWriter.print(" mAlpha=");
      paramPrintWriter.print(this.mAlpha);
      paramPrintWriter.print(" mLastAlpha=");
      paramPrintWriter.println(this.mLastAlpha);
      do
      {
        if ((this.mHaveMatrix) || (this.mWin.mGlobalScale != 1.0F))
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mGlobalScale=");
          paramPrintWriter.print(this.mWin.mGlobalScale);
          paramPrintWriter.print(" mDsDx=");
          paramPrintWriter.print(this.mDsDx);
          paramPrintWriter.print(" mDtDx=");
          paramPrintWriter.print(this.mDtDx);
          paramPrintWriter.print(" mDsDy=");
          paramPrintWriter.print(this.mDsDy);
          paramPrintWriter.print(" mDtDy=");
          paramPrintWriter.println(this.mDtDy);
        }
        if (this.mAnimationStartDelayed)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mAnimationStartDelayed=");
          paramPrintWriter.print(this.mAnimationStartDelayed);
        }
        return;
      } while (this.mLastAlpha == 1.0F);
    }
  }
  
  void endDelayingAnimationStart()
  {
    this.mAnimationStartDelayed = false;
  }
  
  boolean finishDrawingLocked()
  {
    if (this.mWin.mAttrs.type == 3) {}
    for (int i = 1;; i = 0)
    {
      if ((WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) && (i != 0)) {
        Slog.v(TAG, "Finishing drawing window " + this.mWin + ": mDrawState=" + drawStateToString());
      }
      boolean bool = this.mWin.clearAnimatingWithSavedSurface();
      if (this.mDrawState == 1)
      {
        if ((WindowManagerDebugConfig.DEBUG_SURFACE_TRACE) || (WindowManagerDebugConfig.DEBUG_ANIM) || (WindowManagerDebugConfig.SHOW_TRANSACTIONS) || (WindowManagerDebugConfig.DEBUG_ORIENTATION)) {
          Slog.v(TAG, "finishDrawingLocked: mDrawState=COMMIT_DRAW_PENDING " + this.mWin + " in " + this.mSurfaceController);
        }
        if ((WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) && (i != 0)) {
          Slog.v(TAG, "Draw state now committed in " + this.mWin);
        }
        this.mDrawState = 2;
        bool = true;
      }
      return bool;
    }
  }
  
  void finishExit()
  {
    if (WindowManagerDebugConfig.DEBUG_ANIM) {
      Slog.v(TAG, "finishExit in " + this + ": exiting=" + this.mWin.mAnimatingExit + " remove=" + this.mWin.mRemoveOnExit + " windowAnimating=" + isWindowAnimationSet());
    }
    if (!this.mWin.mChildWindows.isEmpty())
    {
      WindowList localWindowList = new WindowList(this.mWin.mChildWindows);
      int i = localWindowList.size() - 1;
      while (i >= 0)
      {
        ((WindowState)localWindowList.get(i)).mWinAnimator.finishExit();
        i -= 1;
      }
    }
    if (this.mEnteringAnimation)
    {
      this.mEnteringAnimation = false;
      this.mService.requestTraversal();
      if (this.mWin.mAppToken != null) {}
    }
    try
    {
      this.mWin.mClient.dispatchWindowShown();
      if ((!isWindowAnimationSet()) && (this.mService.mAccessibilityController != null) && (this.mWin.getDisplayId() == 0)) {
        this.mService.mAccessibilityController.onSomeWindowResizedOrMovedLocked();
      }
      if (!this.mWin.mAnimatingExit) {
        return;
      }
      if (isWindowAnimationSet()) {
        return;
      }
      if ((WindowManagerService.localLOGV) || (WindowManagerDebugConfig.DEBUG_ADD_REMOVE)) {
        Slog.v(TAG, "Exit animation finished in " + this + ": remove=" + this.mWin.mRemoveOnExit);
      }
      this.mWin.mDestroying = true;
      boolean bool = hasSurface();
      if (bool) {
        hide("finishExit");
      }
      if (this.mWin.mAppToken != null) {
        this.mWin.mAppToken.destroySurfaces();
      }
      for (;;)
      {
        this.mWin.mAnimatingExit = false;
        this.mWallpaperControllerLocked.hideWallpapers(this.mWin);
        return;
        if (bool) {
          this.mService.mDestroySurface.add(this.mWin);
        }
        if (this.mWin.mRemoveOnExit)
        {
          this.mService.mPendingRemove.add(this.mWin);
          this.mWin.mRemoveOnExit = false;
        }
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  boolean getShown()
  {
    if (this.mSurfaceController != null) {
      return this.mSurfaceController.getShown();
    }
    return false;
  }
  
  boolean hasSurface()
  {
    if ((!this.mWin.hasSavedSurface()) && (this.mSurfaceController != null)) {
      return this.mSurfaceController.hasSurface();
    }
    return false;
  }
  
  void hide(String paramString)
  {
    if (!this.mLastHidden)
    {
      this.mLastHidden = true;
      if (this.mSurfaceController != null) {
        this.mSurfaceController.hideInTransaction(paramString);
      }
    }
  }
  
  boolean isAnimationSet()
  {
    if ((this.mAnimation != null) || ((this.mAttachedWinAnimator != null) && (this.mAttachedWinAnimator.mAnimation != null))) {
      return true;
    }
    if (this.mAppAnimator != null) {
      return this.mAppAnimator.isAnimating();
    }
    return false;
  }
  
  boolean isAnimationStarting()
  {
    return (isAnimationSet()) && (!this.mAnimating);
  }
  
  boolean isDummyAnimation()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mAppAnimator != null)
    {
      bool1 = bool2;
      if (this.mAppAnimator.animation == AppWindowAnimator.sDummyAnimation) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  boolean isWaitingForOpening()
  {
    if ((this.mService.mAppTransition.isTransitionSet()) && (isDummyAnimation())) {
      return this.mService.mOpeningApps.contains(this.mWin.mAppToken);
    }
    return false;
  }
  
  boolean isWindowAnimationSet()
  {
    return this.mAnimation != null;
  }
  
  void markPreservedSurfaceForDestroy()
  {
    if ((!this.mDestroyPreservedSurfaceUponRedraw) || (this.mService.mDestroyPreservedSurface.contains(this.mWin))) {
      return;
    }
    this.mService.mDestroyPreservedSurface.add(this.mWin);
  }
  
  boolean performShowLocked()
  {
    if (this.mWin.isHiddenFromUserLocked())
    {
      if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
        Slog.w(TAG, "hiding " + this.mWin + ", belonging to " + this.mWin.mOwnerUid);
      }
      this.mWin.hideLw(false);
      return false;
    }
    Object localObject;
    StringBuilder localStringBuilder;
    if ((WindowManagerDebugConfig.DEBUG_VISIBILITY) || ((WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) && (this.mWin.mAttrs.type == 3)))
    {
      localObject = TAG;
      localStringBuilder = new StringBuilder().append("performShow on ").append(this).append(": mDrawState=").append(drawStateToString()).append(" readyForDisplay=").append(this.mWin.isReadyForDisplayIgnoringKeyguard()).append(" starting=");
      if (this.mWin.mAttrs.type == 3)
      {
        bool = true;
        localStringBuilder = localStringBuilder.append(bool).append(" during animation: policyVis=").append(this.mWin.mPolicyVisibility).append(" attHidden=").append(this.mWin.mAttachedHidden).append(" tok.hiddenRequested=");
        if (this.mWin.mAppToken == null) {
          break label740;
        }
        bool = this.mWin.mAppToken.hiddenRequested;
        label241:
        localStringBuilder = localStringBuilder.append(bool).append(" tok.hidden=");
        if (this.mWin.mAppToken == null) {
          break label745;
        }
        bool = this.mWin.mAppToken.hidden;
        label276:
        localStringBuilder = localStringBuilder.append(bool).append(" animating=").append(this.mAnimating).append(" tok animating=");
        if (this.mAppAnimator == null) {
          break label750;
        }
        bool = this.mAppAnimator.animating;
        label318:
        Slog.v((String)localObject, bool + " Callers=" + Debug.getCallers(3));
      }
    }
    else
    {
      if ((this.mDrawState != 3) || (!this.mWin.isReadyForDisplayIgnoringKeyguard())) {
        break label840;
      }
      if ((WindowManagerDebugConfig.DEBUG_VISIBILITY) || ((WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) && (this.mWin.mAttrs.type == 3)))
      {
        localObject = TAG;
        localStringBuilder = new StringBuilder().append("Showing ").append(this).append(" during animation: policyVis=").append(this.mWin.mPolicyVisibility).append(" attHidden=").append(this.mWin.mAttachedHidden).append(" tok.hiddenRequested=");
        if (this.mWin.mAppToken == null) {
          break label755;
        }
        bool = this.mWin.mAppToken.hiddenRequested;
        label473:
        localStringBuilder = localStringBuilder.append(bool).append(" tok.hidden=");
        if (this.mWin.mAppToken == null) {
          break label760;
        }
        bool = this.mWin.mAppToken.hidden;
        label508:
        localStringBuilder = localStringBuilder.append(bool).append(" animating=").append(this.mAnimating).append(" tok animating=");
        if (this.mAppAnimator == null) {
          break label765;
        }
      }
    }
    label740:
    label745:
    label750:
    label755:
    label760:
    label765:
    for (boolean bool = this.mAppAnimator.animating;; bool = false)
    {
      Slog.v((String)localObject, bool);
      this.mService.enableScreenIfNeededLocked();
      applyEnterAnimationLocked();
      this.mLastAlpha = -1.0F;
      if ((WindowManagerDebugConfig.DEBUG_SURFACE_TRACE) || (WindowManagerDebugConfig.DEBUG_ANIM)) {
        Slog.v(TAG, "performShowLocked: mDrawState=HAS_DRAWN in " + this.mWin);
      }
      this.mDrawState = 4;
      this.mService.scheduleAnimationLocked();
      int i = this.mWin.mChildWindows.size();
      while (i > 0)
      {
        int j = i - 1;
        localObject = (WindowState)this.mWin.mChildWindows.get(j);
        i = j;
        if (((WindowState)localObject).mAttachedHidden)
        {
          ((WindowState)localObject).mAttachedHidden = false;
          i = j;
          if (((WindowState)localObject).mWinAnimator.mSurfaceController != null)
          {
            ((WindowState)localObject).mWinAnimator.performShowLocked();
            localObject = ((WindowState)localObject).getDisplayContent();
            i = j;
            if (localObject != null)
            {
              ((DisplayContent)localObject).layoutNeeded = true;
              i = j;
            }
          }
        }
      }
      bool = false;
      break;
      bool = false;
      break label241;
      bool = false;
      break label276;
      bool = false;
      break label318;
      bool = false;
      break label473;
      bool = false;
      break label508;
    }
    if ((this.mWin.mAttrs.type != 3) && (this.mWin.mAppToken != null)) {
      this.mWin.mAppToken.onFirstWindowDrawn(this.mWin, this);
    }
    if (this.mWin.mAttrs.type == 2011) {
      this.mWin.mDisplayContent.mDividerControllerLocked.resetImeHideRequested();
    }
    return true;
    label840:
    return false;
  }
  
  void prepareSurfaceLocked(boolean paramBoolean)
  {
    WindowState localWindowState = this.mWin;
    if (!hasSurface())
    {
      if (localWindowState.mOrientationChanging)
      {
        if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
          Slog.v(TAG, "Orientation change skips hidden " + localWindowState);
        }
        localWindowState.mOrientationChanging = false;
      }
      return;
    }
    if (isWaitingForOpening()) {
      return;
    }
    int j = 0;
    computeShownFrameLocked();
    setSurfaceBoundariesLocked(paramBoolean);
    label532:
    int i;
    if ((!this.mIsWallpaper) || (this.mWin.mWallpaperVisible))
    {
      if ((localWindowState.mAttachedHidden) || (!localWindowState.isOnScreen())) {
        break label656;
      }
      if ((this.mLastLayer == this.mAnimLayer) && (this.mLastAlpha == this.mShownAlpha)) {
        break label729;
      }
      j = 1;
      this.mLastAlpha = this.mShownAlpha;
      this.mLastLayer = this.mAnimLayer;
      this.mLastDsDx = this.mDsDx;
      this.mLastDtDx = this.mDtDx;
      this.mLastDsDy = this.mDsDy;
      this.mLastDtDy = this.mDtDy;
      localWindowState.mLastHScale = localWindowState.mHScale;
      localWindowState.mLastVScale = localWindowState.mVScale;
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        WindowManagerService.logSurface(localWindowState, "controller=" + this.mSurfaceController + "alpha=" + this.mShownAlpha + " layer=" + this.mAnimLayer + " matrix=[" + this.mDsDx + "*" + localWindowState.mHScale + "," + this.mDtDx + "*" + localWindowState.mVScale + "][" + this.mDsDy + "*" + localWindowState.mHScale + "," + this.mDtDy + "*" + localWindowState.mVScale + "]", false);
      }
      if ((this.mSurfaceController.prepareToShowInTransaction(this.mShownAlpha, this.mAnimLayer, this.mDsDx * localWindowState.mHScale * this.mExtraHScale, this.mDtDx * localWindowState.mVScale * this.mExtraVScale, this.mDsDy * localWindowState.mHScale * this.mExtraHScale, this.mDtDy * localWindowState.mVScale * this.mExtraVScale, paramBoolean)) && (this.mLastHidden) && (this.mDrawState == 4))
      {
        if (!showSurfaceRobustlyLocked()) {
          break label857;
        }
        markPreservedSurfaceForDestroy();
        this.mAnimator.requestRemovalOfReplacedWindows(localWindowState);
        this.mLastHidden = false;
        if (this.mIsWallpaper) {
          this.mWallpaperControllerLocked.dispatchWallpaperVisibility(localWindowState, true);
        }
        this.mAnimator.setPendingLayoutChanges(localWindowState.getDisplayId(), 8);
      }
      i = j;
      if (hasSurface())
      {
        localWindowState.mToken.hasVisible = true;
        i = j;
      }
      label552:
      if (i != 0) {
        if (localWindowState.mOrientationChanging)
        {
          if (localWindowState.isDrawnLw()) {
            break label866;
          }
          WindowAnimator localWindowAnimator = this.mAnimator;
          localWindowAnimator.mBulkUpdateParams &= 0xFFFFFFF7;
          this.mAnimator.mLastWindowFreezeSource = localWindowState;
          if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
            Slog.v(TAG, "Orientation continue waiting for draw in " + localWindowState);
          }
        }
      }
    }
    for (;;)
    {
      localWindowState.mToken.hasVisible = true;
      return;
      hide("prepareSurfaceLocked");
      i = j;
      break label552;
      label656:
      hide("prepareSurfaceLocked");
      this.mWallpaperControllerLocked.hideWallpapers(localWindowState);
      i = j;
      if (!localWindowState.mOrientationChanging) {
        break label552;
      }
      localWindowState.mOrientationChanging = false;
      i = j;
      if (!WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        break label552;
      }
      Slog.v(TAG, "Orientation change skips hidden " + localWindowState);
      i = j;
      break label552;
      label729:
      if ((this.mLastDsDx != this.mDsDx) || (this.mLastDtDx != this.mDtDx) || (this.mLastDsDy != this.mDsDy) || (this.mLastDtDy != this.mDtDy) || (localWindowState.mLastHScale != localWindowState.mHScale) || (localWindowState.mLastVScale != localWindowState.mVScale) || (this.mLastHidden)) {
        break;
      }
      if ((WindowManagerDebugConfig.DEBUG_ANIM) && (isAnimationSet())) {
        Slog.v(TAG, "prepareSurface: No changes in animation for " + this);
      }
      i = 1;
      break label552;
      label857:
      localWindowState.mOrientationChanging = false;
      break label532;
      label866:
      localWindowState.mOrientationChanging = false;
      if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
        Slog.v(TAG, "Orientation change complete in " + localWindowState);
      }
    }
  }
  
  void preserveSurfaceLocked()
  {
    if (this.mDestroyPreservedSurfaceUponRedraw)
    {
      this.mSurfaceDestroyDeferred = false;
      destroySurfaceLocked();
      this.mSurfaceDestroyDeferred = true;
      return;
    }
    if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
      WindowManagerService.logSurface(this.mWin, "SET FREEZE LAYER", false);
    }
    if (this.mSurfaceController != null) {
      this.mSurfaceController.setLayer(this.mAnimLayer + 1);
    }
    this.mDestroyPreservedSurfaceUponRedraw = true;
    this.mSurfaceDestroyDeferred = true;
    destroySurfaceLocked();
  }
  
  void reclaimSomeSurfaceMemory(String paramString, boolean paramBoolean)
  {
    this.mService.reclaimSomeSurfaceMemoryLocked(this, paramString, paramBoolean);
  }
  
  void seamlesslyRotateWindow(int paramInt1, int paramInt2)
  {
    WindowState localWindowState = this.mWin;
    if ((!localWindowState.isVisibleNow()) || (localWindowState.mIsWallpaper)) {
      return;
    }
    Rect localRect1 = this.mService.mTmpRect;
    Rect localRect2 = this.mService.mTmpRect2;
    RectF localRectF = this.mService.mTmpRectF;
    Matrix localMatrix = this.mService.mTmpTransform;
    float f1 = localWindowState.mFrame.left;
    float f2 = localWindowState.mFrame.top;
    float f3 = localWindowState.mFrame.width();
    float f4 = localWindowState.mFrame.height();
    this.mService.getDefaultDisplayContentLocked().getLogicalDisplayRect(localRect2);
    float f5 = localRect2.width();
    float f6 = localRect2.height();
    DisplayContent.createRotationMatrix(DisplayContent.deltaRotation(paramInt2, paramInt1), f1, f2, f5, f6, localMatrix);
    if ((localWindowState.isChildWindow()) && (this.mSurfaceController.getTransformToDisplayInverse()))
    {
      localRectF.set(f1, f2, f1 + f3, f2 + f4);
      localMatrix.mapRect(localRectF);
      localWindowState.mAttrs.x = ((int)localRectF.left - localWindowState.mAttachedWindow.mFrame.left);
      localWindowState.mAttrs.y = ((int)localRectF.top - localWindowState.mAttachedWindow.mFrame.top);
      localWindowState.mAttrs.width = ((int)Math.ceil(localRectF.width()));
      localWindowState.mAttrs.height = ((int)Math.ceil(localRectF.height()));
      localWindowState.setWindowScale(localWindowState.mRequestedWidth, localWindowState.mRequestedHeight);
      localWindowState.applyGravityAndUpdateFrame(localWindowState.mContainingFrame, localWindowState.mDisplayFrame);
      computeShownFrameLocked();
      setSurfaceBoundariesLocked(false);
      localRect1.set(0, 0, localWindowState.mRequestedWidth, localWindowState.mRequestedWidth + localWindowState.mRequestedHeight);
      this.mSurfaceController.setCropInTransaction(localRect1, false);
      return;
    }
    this.mService.markForSeamlessRotation(localWindowState, true);
    localMatrix.getValues(this.mService.mTmpFloats);
    f1 = this.mService.mTmpFloats[0];
    f2 = this.mService.mTmpFloats[3];
    f3 = this.mService.mTmpFloats[1];
    f4 = this.mService.mTmpFloats[4];
    f5 = this.mService.mTmpFloats[2];
    f6 = this.mService.mTmpFloats[5];
    this.mSurfaceController.setPositionInTransaction(f5, f6, false);
    this.mSurfaceController.setMatrixInTransaction(localWindowState.mHScale * f1, localWindowState.mVScale * f2, localWindowState.mHScale * f3, localWindowState.mVScale * f4, false);
  }
  
  public void setAnimation(Animation paramAnimation)
  {
    setAnimation(paramAnimation, -1L, 0);
  }
  
  public void setAnimation(Animation paramAnimation, int paramInt)
  {
    setAnimation(paramAnimation, -1L, paramInt);
  }
  
  public void setAnimation(Animation paramAnimation, long paramLong, int paramInt)
  {
    int i = 0;
    if (WindowManagerService.localLOGV) {
      Slog.v(TAG, "Setting animation in " + this + ": " + paramAnimation);
    }
    this.mAnimating = false;
    this.mLocalAnimating = false;
    this.mAnimation = paramAnimation;
    this.mAnimation.restrictDuration(10000L);
    this.mAnimation.scaleCurrentDuration(this.mService.getWindowAnimationScaleLocked());
    this.mTransformation.clear();
    paramAnimation = this.mTransformation;
    if (this.mLastHidden) {}
    for (;;)
    {
      paramAnimation.setAlpha(i);
      this.mHasLocalTransformation = true;
      this.mAnimationStartTime = paramLong;
      this.mStackClip = paramInt;
      return;
      i = 1;
    }
  }
  
  void setMoveAnimation(int paramInt1, int paramInt2)
  {
    setAnimation(AnimationUtils.loadAnimation(this.mContext, 17432753));
    this.mAnimDx = (this.mWin.mLastFrame.left - paramInt1);
    this.mAnimDy = (this.mWin.mLastFrame.top - paramInt2);
    this.mAnimateMove = true;
  }
  
  void setOpaqueLocked(boolean paramBoolean)
  {
    if (this.mSurfaceController == null) {
      return;
    }
    this.mSurfaceController.setOpaque(paramBoolean);
  }
  
  void setSecureLocked(boolean paramBoolean)
  {
    if (this.mSurfaceController == null) {
      return;
    }
    this.mSurfaceController.setSecure(paramBoolean);
  }
  
  void setSurfaceBoundariesLocked(boolean paramBoolean)
  {
    WindowState localWindowState = this.mWin;
    Task localTask = localWindowState.getTask();
    boolean bool1;
    label155:
    Object localObject;
    if ((!localWindowState.isResizedWhileNotDragResizing()) || (localWindowState.isGoneForLayoutLw()))
    {
      this.mTmpSize.set(localWindowState.mShownPosition.x, localWindowState.mShownPosition.y, 0, 0);
      calculateSurfaceBounds(localWindowState, localWindowState.getAttrs());
      this.mExtraHScale = 1.0F;
      this.mExtraVScale = 1.0F;
      boolean bool2 = this.mForceScaleUntilResize;
      boolean bool3 = localWindowState.mSeamlesslyRotated;
      if ((localWindowState.inPinnedWorkspace()) && (localWindowState.mRelayoutCalled) && (!localWindowState.mInRelayout)) {
        break label735;
      }
      this.mSurfaceResized = this.mSurfaceController.setSizeInTransaction(this.mTmpSize.width(), this.mTmpSize.height(), paramBoolean);
      if ((this.mForceScaleUntilResize) && (!this.mSurfaceResized)) {
        break label743;
      }
      bool1 = false;
      this.mForceScaleUntilResize = bool1;
      localObject = this.mService;
      if ((localWindowState.mSeamlesslyRotated) && (!this.mSurfaceResized)) {
        break label749;
      }
      bool1 = false;
      label185:
      ((WindowManagerService)localObject).markForSeamlessRotation(localWindowState, bool1);
      calculateSurfaceWindowCrop(this.mTmpClipRect, this.mTmpFinalClipRect);
      float f1 = this.mSurfaceController.getWidth();
      float f2 = this.mSurfaceController.getHeight();
      if (((localTask == null) || (!localTask.mStack.getForceScaleToCrop())) && (!this.mForceScaleUntilResize)) {
        break label755;
      }
      int i = localWindowState.getAttrs().surfaceInsets.left + localWindowState.getAttrs().surfaceInsets.right;
      int j = localWindowState.getAttrs().surfaceInsets.top + localWindowState.getAttrs().surfaceInsets.bottom;
      if (!this.mForceScaleUntilResize) {
        this.mSurfaceController.forceScaleableInTransaction(true);
      }
      this.mExtraHScale = ((this.mTmpClipRect.width() - i) / (f1 - i));
      this.mExtraVScale = ((this.mTmpClipRect.height() - j) / (f2 - j));
      j = (int)(this.mTmpSize.left - localWindowState.mAttrs.x * (1.0F - this.mExtraHScale));
      i = (int)(this.mTmpSize.top - localWindowState.mAttrs.y * (1.0F - this.mExtraVScale));
      j = (int)(j + localWindowState.getAttrs().surfaceInsets.left * (1.0F - this.mExtraHScale));
      i = (int)(i + localWindowState.getAttrs().surfaceInsets.top * (1.0F - this.mExtraVScale));
      this.mSurfaceController.setPositionInTransaction((float)Math.floor(j), (float)Math.floor(i), paramBoolean);
      this.mTmpClipRect.set(0, 0, (int)f1, (int)f2);
      this.mTmpFinalClipRect.setEmpty();
      this.mForceScaleUntilResize = true;
      label507:
      if (((bool2) && (!this.mForceScaleUntilResize)) || ((bool3) && (!localWindowState.mSeamlesslyRotated))) {
        break label790;
      }
    }
    for (;;)
    {
      localObject = this.mTmpClipRect;
      if (localWindowState.inPinnedWorkspace())
      {
        localObject = null;
        localTask.mStack.getDimBounds(this.mTmpFinalClipRect);
        this.mTmpFinalClipRect.inset(-localWindowState.mAttrs.surfaceInsets.left, -localWindowState.mAttrs.surfaceInsets.top, -localWindowState.mAttrs.surfaceInsets.right, -localWindowState.mAttrs.surfaceInsets.bottom);
      }
      if (!localWindowState.mSeamlesslyRotated)
      {
        updateSurfaceWindowCrop((Rect)localObject, this.mTmpFinalClipRect, paramBoolean);
        this.mSurfaceController.setMatrixInTransaction(this.mDsDx * localWindowState.mHScale * this.mExtraHScale, this.mDtDx * localWindowState.mVScale * this.mExtraVScale, this.mDsDy * localWindowState.mHScale * this.mExtraHScale, this.mDtDy * localWindowState.mVScale * this.mExtraVScale, paramBoolean);
      }
      if (this.mSurfaceResized)
      {
        this.mReportSurfaceResized = true;
        this.mAnimator.setPendingLayoutChanges(localWindowState.getDisplayId(), 4);
        localWindowState.applyDimLayerIfNeeded();
      }
      return;
      return;
      label735:
      this.mSurfaceResized = false;
      break;
      label743:
      bool1 = true;
      break label155;
      label749:
      bool1 = true;
      break label185;
      label755:
      if (localWindowState.mSeamlesslyRotated) {
        break label507;
      }
      this.mSurfaceController.setPositionInTransaction(this.mTmpSize.left, this.mTmpSize.top, paramBoolean);
      break label507;
      label790:
      this.mSurfaceController.setGeometryAppliesWithResizeInTransaction(true);
      this.mSurfaceController.forceScaleableInTransaction(false);
    }
  }
  
  void setTransparentRegionHintLocked(Region paramRegion)
  {
    if (this.mSurfaceController == null)
    {
      Slog.w(TAG, "setTransparentRegionHint: null mSurface after mHasSurface true");
      return;
    }
    this.mSurfaceController.setTransparentRegionHint(paramRegion);
  }
  
  void setWallpaperOffset(Point paramPoint)
  {
    WindowManager.LayoutParams localLayoutParams = this.mWin.getAttrs();
    int i = paramPoint.x - localLayoutParams.surfaceInsets.left;
    int j = paramPoint.y - localLayoutParams.surfaceInsets.top;
    try
    {
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i(TAG, ">>> OPEN TRANSACTION setWallpaperOffset");
      }
      SurfaceControl.openTransaction();
      this.mSurfaceController.setPositionInTransaction(this.mWin.mFrame.left + i, this.mWin.mFrame.top + j, false);
      calculateSurfaceWindowCrop(this.mTmpClipRect, this.mTmpFinalClipRect);
      updateSurfaceWindowCrop(this.mTmpClipRect, this.mTmpFinalClipRect, false);
      return;
    }
    catch (RuntimeException paramPoint)
    {
      Slog.w(TAG, "Error positioning surface of " + this.mWin + " pos=(" + i + "," + j + ")", paramPoint);
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i(TAG, "<<< CLOSE TRANSACTION setWallpaperOffset");
      }
    }
  }
  
  void startDelayingAnimationStart()
  {
    this.mAnimationStartDelayed = true;
  }
  
  boolean stepAnimationLocked(long paramLong)
  {
    this.mWasAnimating = this.mAnimating;
    DisplayContent localDisplayContent = this.mWin.getDisplayContent();
    label330:
    label370:
    label376:
    boolean bool;
    if ((localDisplayContent != null) && (this.mService.okToDisplay()))
    {
      Object localObject;
      if ((this.mWin.isDrawnLw()) && (this.mAnimation != null))
      {
        this.mHasTransformation = true;
        this.mHasLocalTransformation = true;
        if (!this.mLocalAnimating)
        {
          if (WindowManagerDebugConfig.DEBUG_ANIM) {
            Slog.v(TAG, "Starting animation in " + this + " @ " + paramLong + ": ww=" + this.mWin.mFrame.width() + " wh=" + this.mWin.mFrame.height() + " dx=" + this.mAnimDx + " dy=" + this.mAnimDy + " scale=" + this.mService.getWindowAnimationScaleLocked());
          }
          localObject = localDisplayContent.getDisplayInfo();
          if (!this.mAnimateMove) {
            break label330;
          }
          this.mAnimateMove = false;
          this.mAnimation.initialize(this.mWin.mFrame.width(), this.mWin.mFrame.height(), this.mAnimDx, this.mAnimDy);
          this.mAnimDx = ((DisplayInfo)localObject).appWidth;
          this.mAnimDy = ((DisplayInfo)localObject).appHeight;
          localObject = this.mAnimation;
          if (this.mAnimationStartTime == -1L) {
            break label370;
          }
        }
        for (long l = this.mAnimationStartTime;; l = paramLong)
        {
          ((Animation)localObject).setStartTime(l);
          this.mLocalAnimating = true;
          this.mAnimating = true;
          if ((this.mAnimation == null) || (!this.mLocalAnimating)) {
            break label376;
          }
          this.mLastAnimationTime = paramLong;
          if (!stepAnimation(paramLong)) {
            break label376;
          }
          return true;
          this.mAnimation.initialize(this.mWin.mFrame.width(), this.mWin.mFrame.height(), ((DisplayInfo)localObject).appWidth, ((DisplayInfo)localObject).appHeight);
          break;
        }
        if (WindowManagerDebugConfig.DEBUG_ANIM) {
          Slog.v(TAG, "Finished animation in " + this + " @ " + paramLong);
        }
      }
      this.mHasLocalTransformation = false;
      if (((!this.mLocalAnimating) || (this.mAnimationIsEntrance)) && (this.mAppAnimator != null) && (this.mAppAnimator.animation != null))
      {
        this.mAnimating = true;
        this.mHasTransformation = true;
        this.mTransformation.clear();
        return false;
      }
      if (this.mHasTransformation)
      {
        this.mAnimating = true;
        if ((!this.mAnimating) && (!this.mLocalAnimating)) {
          break label987;
        }
        if (WindowManagerDebugConfig.DEBUG_ANIM)
        {
          localObject = TAG;
          StringBuilder localStringBuilder = new StringBuilder().append("Animation done in ").append(this).append(": exiting=").append(this.mWin.mAnimatingExit).append(", reportedVisible=");
          if (this.mWin.mAppToken == null) {
            break label989;
          }
          bool = this.mWin.mAppToken.reportedVisible;
          label574:
          Slog.v((String)localObject, bool);
        }
        this.mAnimating = false;
        this.mKeyguardGoingAwayAnimation = false;
        this.mKeyguardGoingAwayWithWallpaper = false;
        this.mLocalAnimating = false;
        if (this.mAnimation != null)
        {
          this.mAnimation.cancel();
          this.mAnimation = null;
        }
        if (this.mAnimator.mWindowDetachedWallpaper == this.mWin) {
          this.mAnimator.mWindowDetachedWallpaper = null;
        }
        this.mAnimLayer = (this.mWin.mLayer + this.mService.mLayersController.getSpecialWindowAnimLayerAdjustment(this.mWin));
        if (WindowManagerDebugConfig.DEBUG_LAYERS) {
          Slog.v(TAG, "Stepping win " + this + " anim layer: " + this.mAnimLayer);
        }
        this.mHasTransformation = false;
        this.mHasLocalTransformation = false;
        this.mStackClip = 1;
        this.mWin.checkPolicyVisibilityChange();
        this.mTransformation.clear();
        if ((this.mDrawState != 4) || (this.mWin.mAttrs.type != 3) || (this.mWin.mAppToken == null) || (!this.mWin.mAppToken.firstWindowDrawn) || (this.mWin.mAppToken.startingData == null)) {
          break label995;
        }
        if (WindowManagerDebugConfig.DEBUG_STARTING_WINDOW) {
          Slog.v(TAG, "Finish starting " + this.mWin.mToken + ": first real window done animating");
        }
        this.mService.mFinishedStarting.add(this.mWin.mAppToken);
        this.mService.mH.sendEmptyMessage(7);
      }
    }
    for (;;)
    {
      finishExit();
      int i = this.mWin.getDisplayId();
      this.mAnimator.setPendingLayoutChanges(i, 8);
      if (WindowManagerDebugConfig.DEBUG_LAYOUT_REPEATS) {
        this.mService.mWindowPlacerLocked.debugLayoutRepeats("WindowStateAnimator", this.mAnimator.getPendingLayoutChanges(i));
      }
      if (this.mWin.mAppToken != null) {
        this.mWin.mAppToken.updateReportedVisibilityLocked();
      }
      return false;
      if (!isAnimationSet()) {
        break;
      }
      this.mAnimating = true;
      break;
      if (this.mAnimation == null) {
        break;
      }
      this.mAnimating = true;
      break;
      label987:
      return false;
      label989:
      bool = false;
      break label574;
      label995:
      if ((this.mAttrType == 2000) && (this.mWin.mPolicyVisibility) && (localDisplayContent != null)) {
        localDisplayContent.layoutNeeded = true;
      }
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("WindowStateAnimator{");
    localStringBuffer.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuffer.append(' ');
    localStringBuffer.append(this.mWin.mAttrs.getTitle());
    localStringBuffer.append('}');
    return localStringBuffer.toString();
  }
  
  boolean tryChangeFormatInPlaceLocked()
  {
    boolean bool = false;
    if (this.mSurfaceController == null) {
      return false;
    }
    WindowManager.LayoutParams localLayoutParams = this.mWin.getAttrs();
    int i;
    if ((localLayoutParams.flags & 0x1000000) != 0)
    {
      i = 1;
      if (i == 0) {
        break label69;
      }
      i = -3;
      label39:
      if (i != this.mSurfaceFormat) {
        break label82;
      }
      if (!PixelFormat.formatHasAlpha(localLayoutParams.format)) {
        break label77;
      }
    }
    for (;;)
    {
      setOpaqueLocked(bool);
      return true;
      i = 0;
      break;
      label69:
      i = localLayoutParams.format;
      break label39;
      label77:
      bool = true;
    }
    label82:
    return false;
  }
  
  void updateSurfaceWindowCrop(Rect paramRect1, Rect paramRect2, boolean paramBoolean)
  {
    if (WindowManagerDebugConfig.DEBUG_WINDOW_CROP) {
      Slog.d(TAG, "updateSurfaceWindowCrop: win=" + this.mWin + " clipRect=" + paramRect1 + " finalClipRect=" + paramRect2);
    }
    if (paramRect1 != null) {
      if (!paramRect1.equals(this.mLastClipRect))
      {
        this.mLastClipRect.set(paramRect1);
        this.mSurfaceController.setCropInTransaction(paramRect1, paramBoolean);
      }
    }
    for (;;)
    {
      if (!paramRect2.equals(this.mLastFinalClipRect))
      {
        this.mLastFinalClipRect.set(paramRect2);
        this.mSurfaceController.setFinalCropInTransaction(paramRect2);
        if ((this.mDestroyPreservedSurfaceUponRedraw) && (this.mPendingDestroySurface != null)) {
          this.mPendingDestroySurface.setFinalCropInTransaction(paramRect2);
        }
      }
      return;
      this.mSurfaceController.clearCropInTransaction(paramBoolean);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/WindowStateAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */