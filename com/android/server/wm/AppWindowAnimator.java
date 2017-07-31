package com.android.server.wm;

import android.graphics.Matrix;
import android.os.Build;
import android.os.SystemClock;
import android.util.Slog;
import android.util.TimeUtils;
import android.view.Choreographer;
import android.view.SurfaceControl;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import java.io.PrintWriter;
import java.util.ArrayList;

public class AppWindowAnimator
{
  static final int PROLONG_ANIMATION_AT_END = 1;
  static final int PROLONG_ANIMATION_AT_START = 2;
  private static final int PROLONG_ANIMATION_DISABLED = 0;
  static final String TAG = "WindowManager";
  static final Animation sDummyAnimation = new DummyAnimation();
  boolean allDrawn;
  int animLayerAdjustment;
  boolean animating;
  Animation animation;
  boolean deferFinalFrameCleanup;
  boolean deferThumbnailDestruction;
  boolean freezingScreen;
  boolean hasTransformation;
  int lastFreezeDuration;
  ArrayList<WindowStateAnimator> mAllAppWinAnimators = new ArrayList();
  final WindowAnimator mAnimator;
  final AppWindowToken mAppToken;
  private boolean mClearProlongedAnimation;
  private int mProlongAnimation;
  final WindowManagerService mService;
  private boolean mSkipFirstFrame = false;
  private int mStackClip = 1;
  SurfaceControl thumbnail;
  Animation thumbnailAnimation;
  int thumbnailForceAboveLayer;
  int thumbnailLayer;
  int thumbnailTransactionSeq;
  final Transformation thumbnailTransformation = new Transformation();
  final Transformation transformation = new Transformation();
  boolean usingTransferredAnimation = false;
  boolean wasAnimating;
  
  public AppWindowAnimator(AppWindowToken paramAppWindowToken)
  {
    this.mAppToken = paramAppWindowToken;
    this.mService = paramAppWindowToken.service;
    this.mAnimator = this.mService.mAnimator;
  }
  
  private long getAnimationFrameTime(Animation paramAnimation, long paramLong)
  {
    if (this.mProlongAnimation == 2)
    {
      paramAnimation.setStartTime(paramLong);
      return 1L + paramLong;
    }
    return paramLong;
  }
  
  private long getStartTimeCorrection()
  {
    if (this.mSkipFirstFrame) {
      return -Choreographer.getInstance().getFrameIntervalNanos() / 1000000L;
    }
    return 0L;
  }
  
  private boolean stepAnimation(long paramLong)
  {
    if (this.animation == null) {
      return false;
    }
    this.transformation.clear();
    long l = getAnimationFrameTime(this.animation, paramLong);
    boolean bool2 = this.animation.getTransformation(l, this.transformation);
    boolean bool1 = bool2;
    if (!bool2)
    {
      if ((this.deferThumbnailDestruction) && (!this.deferFinalFrameCleanup)) {
        break label88;
      }
      this.deferFinalFrameCleanup = false;
      if (this.mProlongAnimation != 1) {
        break label99;
      }
      bool1 = true;
    }
    for (;;)
    {
      this.hasTransformation = bool1;
      return bool1;
      label88:
      this.deferFinalFrameCleanup = true;
      bool1 = true;
      continue;
      label99:
      setNullAnimation();
      clearThumbnail();
      bool1 = bool2;
      if (WindowManagerDebugConfig.DEBUG_ANIM)
      {
        Slog.v(TAG, "Finished animation in " + this.mAppToken + " @ " + paramLong);
        bool1 = bool2;
      }
    }
  }
  
  private void stepThumbnailAnimation(long paramLong)
  {
    this.thumbnailTransformation.clear();
    paramLong = getAnimationFrameTime(this.thumbnailAnimation, paramLong);
    this.thumbnailAnimation.getTransformation(paramLong, this.thumbnailTransformation);
    Object localObject = this.mAnimator.getScreenRotationAnimationLocked(0);
    boolean bool;
    if (localObject != null)
    {
      bool = ((ScreenRotationAnimation)localObject).isAnimating();
      if (bool) {
        this.thumbnailTransformation.postCompose(((ScreenRotationAnimation)localObject).getEnterTransformation());
      }
      localObject = this.mService.mTmpFloats;
      this.thumbnailTransformation.getMatrix().getValues((float[])localObject);
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        WindowManagerService.logSurface(this.thumbnail, "thumbnail", "POS " + localObject[2] + ", " + localObject[5]);
      }
      this.thumbnail.setPosition(localObject[2], localObject[5]);
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        WindowManagerService.logSurface(this.thumbnail, "thumbnail", "alpha=" + this.thumbnailTransformation.getAlpha() + " layer=" + this.thumbnailLayer + " matrix=[" + localObject[0] + "," + localObject[3] + "][" + localObject[1] + "," + localObject[4] + "]");
      }
      this.thumbnail.setAlpha(this.thumbnailTransformation.getAlpha());
      if (this.thumbnailForceAboveLayer <= 0) {
        break label336;
      }
      this.thumbnail.setLayer(this.thumbnailForceAboveLayer + 1);
    }
    for (;;)
    {
      this.thumbnail.setMatrix(localObject[0], localObject[3], localObject[1], localObject[4]);
      this.thumbnail.setWindowCrop(this.thumbnailTransformation.getClipRect());
      return;
      bool = false;
      break;
      label336:
      this.thumbnail.setLayer(this.thumbnailLayer + 5 - 4);
    }
  }
  
  public void clearAnimation()
  {
    if (this.animation != null) {
      this.animating = true;
    }
    clearThumbnail();
    setNullAnimation();
    if (this.mAppToken.deferClearAllDrawn) {
      this.mAppToken.clearAllDrawn();
    }
    this.mStackClip = 1;
  }
  
  public void clearThumbnail()
  {
    if (this.thumbnail != null)
    {
      this.thumbnail.hide();
      this.mService.mWindowPlacerLocked.destroyAfterTransaction(this.thumbnail);
      this.thumbnail = null;
    }
    this.deferThumbnailDestruction = false;
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString, boolean paramBoolean)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mAppToken=");
    paramPrintWriter.println(this.mAppToken);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mAnimator=");
    paramPrintWriter.println(this.mAnimator);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("freezingScreen=");
    paramPrintWriter.print(this.freezingScreen);
    paramPrintWriter.print(" allDrawn=");
    paramPrintWriter.print(this.allDrawn);
    paramPrintWriter.print(" animLayerAdjustment=");
    paramPrintWriter.println(this.animLayerAdjustment);
    if (this.lastFreezeDuration != 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("lastFreezeDuration=");
      TimeUtils.formatDuration(this.lastFreezeDuration, paramPrintWriter);
      paramPrintWriter.println();
    }
    if ((this.animating) || (this.animation != null))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("animating=");
      paramPrintWriter.println(this.animating);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("animation=");
      paramPrintWriter.println(this.animation);
    }
    if (this.hasTransformation)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("XForm: ");
      this.transformation.printShortString(paramPrintWriter);
      paramPrintWriter.println();
    }
    if (this.thumbnail != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("thumbnail=");
      paramPrintWriter.print(this.thumbnail);
      paramPrintWriter.print(" layer=");
      paramPrintWriter.println(this.thumbnailLayer);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("thumbnailAnimation=");
      paramPrintWriter.println(this.thumbnailAnimation);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("thumbnailTransformation=");
      paramPrintWriter.println(this.thumbnailTransformation.toShortString());
    }
    int i = 0;
    while (i < this.mAllAppWinAnimators.size())
    {
      WindowStateAnimator localWindowStateAnimator = (WindowStateAnimator)this.mAllAppWinAnimators.get(i);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("App Win Anim #");
      paramPrintWriter.print(i);
      paramPrintWriter.print(": ");
      paramPrintWriter.println(localWindowStateAnimator);
      i += 1;
    }
  }
  
  void endProlongedAnimation()
  {
    this.mProlongAnimation = 0;
  }
  
  int getStackClip()
  {
    return this.mStackClip;
  }
  
  public boolean isAnimating()
  {
    if (this.animation == null) {
      return this.mAppToken.inPendingTransaction;
    }
    return true;
  }
  
  public void setAnimation(Animation paramAnimation, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    if (WindowManagerService.localLOGV) {
      Slog.v(TAG, "Setting animation in " + this.mAppToken + ": " + paramAnimation + " wxh=" + paramInt1 + "x" + paramInt2 + " isVisible=" + this.mAppToken.isVisible());
    }
    this.animation = paramAnimation;
    this.animating = false;
    if (!paramAnimation.isInitialized()) {
      paramAnimation.initialize(paramInt1, paramInt2, paramInt1, paramInt2);
    }
    paramAnimation.restrictDuration(10000L);
    paramAnimation.scaleCurrentDuration(this.mService.getTransitionAnimationScaleLocked());
    paramInt2 = paramAnimation.getZAdjustment();
    paramInt1 = 0;
    if (paramInt2 == 1)
    {
      paramInt1 = 1000;
      if (this.animLayerAdjustment != paramInt1)
      {
        this.animLayerAdjustment = paramInt1;
        updateLayers();
      }
      this.transformation.clear();
      Transformation localTransformation = this.transformation;
      if (!this.mAppToken.isVisible()) {
        break label287;
      }
      paramInt1 = 1;
      label183:
      localTransformation.setAlpha(paramInt1);
      this.hasTransformation = true;
      this.mStackClip = paramInt3;
      this.mSkipFirstFrame = paramBoolean;
      if (!this.mAppToken.appFullscreen) {
        paramAnimation.setBackgroundColor(0);
      }
      if (!this.mClearProlongedAnimation) {
        break label292;
      }
      this.mProlongAnimation = 0;
    }
    for (;;)
    {
      paramInt1 = this.mAppToken.allAppWindows.size() - 1;
      while (paramInt1 >= 0)
      {
        ((WindowState)this.mAppToken.allAppWindows.get(paramInt1)).resetJustMovedInStack();
        paramInt1 -= 1;
      }
      if (paramInt2 != -1) {
        break;
      }
      paramInt1 = 64536;
      break;
      label287:
      paramInt1 = 0;
      break label183;
      label292:
      this.mClearProlongedAnimation = true;
    }
  }
  
  public void setDummyAnimation()
  {
    int i = 1;
    if (WindowManagerService.localLOGV) {
      Slog.v(TAG, "Setting dummy animation in " + this.mAppToken + " isVisible=" + this.mAppToken.isVisible());
    }
    this.animation = sDummyAnimation;
    this.hasTransformation = true;
    this.transformation.clear();
    Transformation localTransformation = this.transformation;
    if (this.mAppToken.isVisible()) {}
    for (;;)
    {
      localTransformation.setAlpha(i);
      return;
      i = 0;
    }
  }
  
  void setNullAnimation()
  {
    this.animation = null;
    this.usingTransferredAnimation = false;
  }
  
  boolean showAllWindowsLocked()
  {
    boolean bool = false;
    int j = this.mAllAppWinAnimators.size();
    int i = 0;
    while (i < j)
    {
      WindowStateAnimator localWindowStateAnimator = (WindowStateAnimator)this.mAllAppWinAnimators.get(i);
      if (WindowManagerDebugConfig.DEBUG_VISIBILITY) {
        Slog.v(TAG, "performing show on: " + localWindowStateAnimator);
      }
      localWindowStateAnimator.performShowLocked();
      bool |= localWindowStateAnimator.isAnimationSet();
      i += 1;
    }
    return bool;
  }
  
  void startProlongAnimation(int paramInt)
  {
    this.mProlongAnimation = paramInt;
    this.mClearProlongedAnimation = false;
  }
  
  boolean stepAnimationLocked(long paramLong, int paramInt)
  {
    if (this.mService.okToDisplay())
    {
      if (this.animation == sDummyAnimation) {
        return false;
      }
      if (((this.mAppToken.allDrawn) || (this.animating) || (this.mAppToken.startingDisplayed)) && (this.animation != null))
      {
        if (!this.animating)
        {
          if (Build.AUTO_TEST_ONEPLUS) {
            Slog.d("APP_LAUNCH", SystemClock.uptimeMillis() + " WMS: starting animation " + this.mAppToken);
          }
          if (WindowManagerDebugConfig.DEBUG_ANIM) {
            Slog.v(TAG, "Starting animation in " + this.mAppToken + " @ " + paramLong + " scale=" + this.mService.getTransitionAnimationScaleLocked() + " allDrawn=" + this.mAppToken.allDrawn + " animating=" + this.animating);
          }
          long l = getStartTimeCorrection();
          this.animation.setStartTime(paramLong + l);
          this.animating = true;
          if (this.thumbnail != null)
          {
            this.thumbnail.show();
            this.thumbnailAnimation.setStartTime(paramLong + l);
          }
          this.mSkipFirstFrame = false;
        }
        if (stepAnimation(paramLong))
        {
          if (this.thumbnail != null) {
            stepThumbnailAnimation(paramLong);
          }
          return true;
        }
      }
    }
    else if (this.animation != null)
    {
      this.animating = true;
      this.animation = null;
    }
    this.hasTransformation = false;
    if ((!this.animating) && (this.animation == null)) {
      return false;
    }
    this.mAnimator.setAppLayoutChanges(this, 8, "AppWindowToken", paramInt);
    clearAnimation();
    this.animating = false;
    if (this.animLayerAdjustment != 0)
    {
      this.animLayerAdjustment = 0;
      updateLayers();
    }
    if ((this.mService.mInputMethodTarget != null) && (this.mService.mInputMethodTarget.mAppToken == this.mAppToken)) {
      this.mService.moveInputMethodWindowsIfNeededLocked(true);
    }
    if (WindowManagerDebugConfig.DEBUG_ANIM) {
      Slog.v(TAG, "Animation done in " + this.mAppToken + ": reportedVisible=" + this.mAppToken.reportedVisible);
    }
    this.transformation.clear();
    int i = this.mAllAppWinAnimators.size();
    paramInt = 0;
    while (paramInt < i)
    {
      ((WindowStateAnimator)this.mAllAppWinAnimators.get(paramInt)).finishExit();
      paramInt += 1;
    }
    this.mService.mAppTransition.notifyAppTransitionFinishedLocked(this.mAppToken.token);
    return false;
  }
  
  void transferCurrentAnimation(AppWindowAnimator paramAppWindowAnimator, WindowStateAnimator paramWindowStateAnimator)
  {
    if (this.animation != null)
    {
      paramAppWindowAnimator.animation = this.animation;
      paramAppWindowAnimator.animating = this.animating;
      paramAppWindowAnimator.animLayerAdjustment = this.animLayerAdjustment;
      setNullAnimation();
      this.animLayerAdjustment = 0;
      paramAppWindowAnimator.updateLayers();
      updateLayers();
      paramAppWindowAnimator.usingTransferredAnimation = true;
    }
    if (paramWindowStateAnimator != null)
    {
      this.mAllAppWinAnimators.remove(paramWindowStateAnimator);
      paramAppWindowAnimator.mAllAppWinAnimators.add(paramWindowStateAnimator);
      paramAppWindowAnimator.hasTransformation = paramWindowStateAnimator.mAppAnimator.hasTransformation;
      if (!paramAppWindowAnimator.hasTransformation) {
        break label113;
      }
      paramAppWindowAnimator.transformation.set(paramWindowStateAnimator.mAppAnimator.transformation);
    }
    for (;;)
    {
      paramWindowStateAnimator.mAppAnimator = paramAppWindowAnimator;
      return;
      label113:
      paramAppWindowAnimator.transformation.clear();
    }
  }
  
  void updateLayers()
  {
    int j = this.mAppToken.allAppWindows.size();
    int k = this.animLayerAdjustment;
    this.thumbnailLayer = -1;
    WallpaperController localWallpaperController = this.mService.mWallpaperControllerLocked;
    int i = 0;
    if (i < j)
    {
      WindowState localWindowState = (WindowState)this.mAppToken.allAppWindows.get(i);
      WindowStateAnimator localWindowStateAnimator = localWindowState.mWinAnimator;
      localWindowStateAnimator.mAnimLayer = (localWindowState.mLayer + k);
      if (localWindowStateAnimator.mAnimLayer > this.thumbnailLayer) {
        this.thumbnailLayer = localWindowStateAnimator.mAnimLayer;
      }
      if (WindowManagerDebugConfig.DEBUG_LAYERS) {
        Slog.v(TAG, "Updating layer " + localWindowState + ": " + localWindowStateAnimator.mAnimLayer);
      }
      if ((localWindowState != this.mService.mInputMethodTarget) || (this.mService.mInputMethodTargetWaitingAnim)) {}
      for (;;)
      {
        localWallpaperController.setAnimLayerAdjustment(localWindowState, k);
        i += 1;
        break;
        this.mService.mLayersController.setInputMethodAnimLayerAdjustment(k);
      }
    }
  }
  
  static final class DummyAnimation
    extends Animation
  {
    public boolean getTransformation(long paramLong, Transformation paramTransformation)
    {
      return false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/AppWindowAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */