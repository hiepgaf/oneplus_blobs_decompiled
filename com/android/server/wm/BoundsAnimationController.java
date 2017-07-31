package com.android.server.wm;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Rect;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.WindowManagerInternal.AppTransitionListener;
import android.view.animation.LinearInterpolator;

public class BoundsAnimationController
{
  private static final boolean DEBUG = WindowManagerDebugConfig.DEBUG_ANIM;
  private static final int DEBUG_ANIMATION_SLOW_DOWN_FACTOR = 1;
  private static final boolean DEBUG_LOCAL = false;
  private static final String TAG = "WindowManager";
  private final AppTransition mAppTransition;
  private final AppTransitionNotifier mAppTransitionNotifier = new AppTransitionNotifier(null);
  private boolean mFinishAnimationAfterTransition = false;
  private final Handler mHandler;
  private ArrayMap<AnimateBoundsUser, BoundsAnimator> mRunningAnimations = new ArrayMap();
  
  BoundsAnimationController(AppTransition paramAppTransition, Handler paramHandler)
  {
    this.mHandler = paramHandler;
    this.mAppTransition = paramAppTransition;
    this.mAppTransition.registerListenerLocked(this.mAppTransitionNotifier);
  }
  
  void animateBounds(AnimateBoundsUser paramAnimateBoundsUser, Rect paramRect1, Rect paramRect2, int paramInt)
  {
    boolean bool1 = false;
    Rect localRect = paramRect2;
    if (paramRect2 == null)
    {
      localRect = new Rect();
      paramAnimateBoundsUser.getFullScreenBounds(localRect);
      bool1 = true;
    }
    paramRect2 = (BoundsAnimator)this.mRunningAnimations.get(paramAnimateBoundsUser);
    if (paramRect2 != null) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      if (DEBUG) {
        Slog.d(TAG, "animateBounds: target=" + paramAnimateBoundsUser + " from=" + paramRect1 + " to=" + localRect + " moveToFullscreen=" + bool1 + " replacing=" + bool2);
      }
      if (!bool2) {
        break label182;
      }
      if (!paramRect2.isAnimatingTo(localRect)) {
        break;
      }
      if (DEBUG) {
        Slog.d(TAG, "animateBounds: same destination as existing=" + paramRect2 + " ignoring...");
      }
      return;
    }
    paramRect2.cancel();
    label182:
    paramRect1 = new BoundsAnimator(paramAnimateBoundsUser, paramRect1, localRect, bool1, bool2);
    this.mRunningAnimations.put(paramAnimateBoundsUser, paramRect1);
    paramRect1.setFloatValues(new float[] { 0.0F, 1.0F });
    if (paramInt != -1) {}
    for (;;)
    {
      paramRect1.setDuration(paramInt * 1);
      paramRect1.setInterpolator(new LinearInterpolator());
      paramRect1.start();
      return;
      paramInt = 200;
    }
  }
  
  public static abstract interface AnimateBoundsUser
  {
    public abstract void getFullScreenBounds(Rect paramRect);
    
    public abstract void moveToFullscreen();
    
    public abstract void onAnimationEnd();
    
    public abstract void onAnimationStart();
    
    public abstract boolean setPinnedStackSize(Rect paramRect1, Rect paramRect2);
    
    public abstract boolean setSize(Rect paramRect);
  }
  
  private final class AppTransitionNotifier
    extends WindowManagerInternal.AppTransitionListener
    implements Runnable
  {
    private AppTransitionNotifier() {}
    
    private void animationFinished()
    {
      if (BoundsAnimationController.-get3(BoundsAnimationController.this))
      {
        BoundsAnimationController.-get4(BoundsAnimationController.this).removeCallbacks(this);
        BoundsAnimationController.-get4(BoundsAnimationController.this).post(this);
      }
    }
    
    public void onAppTransitionCancelledLocked()
    {
      animationFinished();
    }
    
    public void onAppTransitionFinishedLocked(IBinder paramIBinder)
    {
      animationFinished();
    }
    
    public void run()
    {
      int i = 0;
      while (i < BoundsAnimationController.-get5(BoundsAnimationController.this).size())
      {
        ((BoundsAnimationController.BoundsAnimator)BoundsAnimationController.-get5(BoundsAnimationController.this).valueAt(i)).onAnimationEnd(null);
        i += 1;
      }
    }
  }
  
  private final class BoundsAnimator
    extends ValueAnimator
    implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener
  {
    private final Rect mFrom;
    private final int mFrozenTaskHeight;
    private final int mFrozenTaskWidth;
    private final boolean mMoveToFullScreen;
    private final boolean mReplacement;
    private final BoundsAnimationController.AnimateBoundsUser mTarget;
    private final Rect mTmpRect = new Rect();
    private final Rect mTmpTaskBounds = new Rect();
    private final Rect mTo;
    private boolean mWillReplace;
    
    BoundsAnimator(BoundsAnimationController.AnimateBoundsUser paramAnimateBoundsUser, Rect paramRect1, Rect paramRect2, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.mTarget = paramAnimateBoundsUser;
      this.mFrom = paramRect1;
      this.mTo = paramRect2;
      this.mMoveToFullScreen = paramBoolean1;
      this.mReplacement = paramBoolean2;
      addUpdateListener(this);
      addListener(this);
      if (animatingToLargerSize())
      {
        this.mFrozenTaskWidth = this.mTo.width();
        this.mFrozenTaskHeight = this.mTo.height();
        return;
      }
      this.mFrozenTaskWidth = this.mFrom.width();
      this.mFrozenTaskHeight = this.mFrom.height();
    }
    
    private void finishAnimation()
    {
      if (BoundsAnimationController.-get0()) {
        Slog.d(BoundsAnimationController.-get1(), "finishAnimation: mTarget=" + this.mTarget + " callers" + Debug.getCallers(2));
      }
      if (!this.mWillReplace) {
        this.mTarget.onAnimationEnd();
      }
      removeListener(this);
      removeUpdateListener(this);
      BoundsAnimationController.-get5(BoundsAnimationController.this).remove(this.mTarget);
    }
    
    boolean animatingToLargerSize()
    {
      return this.mFrom.width() * this.mFrom.height() <= this.mTo.width() * this.mTo.height();
    }
    
    public void cancel()
    {
      this.mWillReplace = true;
      if (BoundsAnimationController.-get0()) {
        Slog.d(BoundsAnimationController.-get1(), "cancel: willReplace mTarget=" + this.mTarget);
      }
      super.cancel();
    }
    
    public boolean isAnimatingTo(Rect paramRect)
    {
      return this.mTo.equals(paramRect);
    }
    
    public void onAnimationCancel(Animator paramAnimator)
    {
      finishAnimation();
    }
    
    public void onAnimationEnd(Animator paramAnimator)
    {
      if (BoundsAnimationController.-get0()) {
        Slog.d(BoundsAnimationController.-get1(), "onAnimationEnd: mTarget=" + this.mTarget + " mMoveToFullScreen=" + this.mMoveToFullScreen + " mWillReplace=" + this.mWillReplace);
      }
      if ((!BoundsAnimationController.-get2(BoundsAnimationController.this).isRunning()) || (BoundsAnimationController.-get3(BoundsAnimationController.this)))
      {
        finishAnimation();
        this.mTarget.setPinnedStackSize(this.mTo, null);
        if ((this.mMoveToFullScreen) && (!this.mWillReplace)) {}
      }
      else
      {
        BoundsAnimationController.-set0(BoundsAnimationController.this, true);
        return;
      }
      this.mTarget.moveToFullscreen();
    }
    
    public void onAnimationRepeat(Animator paramAnimator) {}
    
    public void onAnimationStart(Animator paramAnimator)
    {
      if (BoundsAnimationController.-get0()) {
        Slog.d(BoundsAnimationController.-get1(), "onAnimationStart: mTarget=" + this.mTarget + " mReplacement=" + this.mReplacement);
      }
      BoundsAnimationController.-set0(BoundsAnimationController.this, false);
      if (!this.mReplacement) {
        this.mTarget.onAnimationStart();
      }
      if (animatingToLargerSize())
      {
        this.mTmpRect.set(this.mFrom.left, this.mFrom.top, this.mFrom.left + this.mFrozenTaskWidth, this.mFrom.top + this.mFrozenTaskHeight);
        this.mTarget.setPinnedStackSize(this.mFrom, this.mTmpRect);
      }
    }
    
    public void onAnimationUpdate(ValueAnimator paramValueAnimator)
    {
      float f1 = ((Float)paramValueAnimator.getAnimatedValue()).floatValue();
      float f2 = 1.0F - f1;
      this.mTmpRect.left = ((int)(this.mFrom.left * f2 + this.mTo.left * f1 + 0.5F));
      this.mTmpRect.top = ((int)(this.mFrom.top * f2 + this.mTo.top * f1 + 0.5F));
      this.mTmpRect.right = ((int)(this.mFrom.right * f2 + this.mTo.right * f1 + 0.5F));
      this.mTmpRect.bottom = ((int)(this.mFrom.bottom * f2 + this.mTo.bottom * f1 + 0.5F));
      if (BoundsAnimationController.-get0()) {
        Slog.d(BoundsAnimationController.-get1(), "animateUpdate: mTarget=" + this.mTarget + " mBounds=" + this.mTmpRect + " from=" + this.mFrom + " mTo=" + this.mTo + " value=" + f1 + " remains=" + f2);
      }
      this.mTmpTaskBounds.set(this.mTmpRect.left, this.mTmpRect.top, this.mTmpRect.left + this.mFrozenTaskWidth, this.mTmpRect.top + this.mFrozenTaskHeight);
      if (!this.mTarget.setPinnedStackSize(this.mTmpRect, this.mTmpTaskBounds)) {
        paramValueAnimator.cancel();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/BoundsAnimationController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */