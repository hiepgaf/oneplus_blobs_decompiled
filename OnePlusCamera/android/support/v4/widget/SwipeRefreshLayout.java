package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

public class SwipeRefreshLayout
  extends ViewGroup
{
  private static final int ALPHA_ANIMATION_DURATION = 300;
  private static final int ANIMATE_TO_START_DURATION = 200;
  private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
  private static final int CIRCLE_BG_LIGHT = -328966;
  private static final int CIRCLE_DIAMETER = 40;
  private static final int CIRCLE_DIAMETER_LARGE = 56;
  private static final float DECELERATE_INTERPOLATION_FACTOR = 2.0F;
  public static final int DEFAULT = 1;
  private static final int DEFAULT_CIRCLE_TARGET = 64;
  private static final float DRAG_RATE = 0.5F;
  private static final int INVALID_POINTER = -1;
  public static final int LARGE = 0;
  private static final int[] LAYOUT_ATTRS = { 16842766 };
  private static final String LOG_TAG = SwipeRefreshLayout.class.getSimpleName();
  private static final int MAX_ALPHA = 255;
  private static final float MAX_PROGRESS_ANGLE = 0.8F;
  private static final int SCALE_DOWN_DURATION = 150;
  private static final int STARTING_PROGRESS_ALPHA = 76;
  private int mActivePointerId = -1;
  private Animation mAlphaMaxAnimation;
  private Animation mAlphaStartAnimation;
  private final Animation mAnimateToCorrectPosition = new Animation()
  {
    public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
    {
      if (SwipeRefreshLayout.this.mUsingCustomStart) {}
      for (int i = (int)SwipeRefreshLayout.this.mSpinnerFinalOffset;; i = (int)(SwipeRefreshLayout.this.mSpinnerFinalOffset - Math.abs(SwipeRefreshLayout.this.mOriginalOffsetTop)))
      {
        int j = SwipeRefreshLayout.this.mFrom;
        i = (int)((i - SwipeRefreshLayout.this.mFrom) * paramAnonymousFloat);
        int k = SwipeRefreshLayout.this.mCircleView.getTop();
        SwipeRefreshLayout.this.setTargetOffsetTopAndBottom(i + j - k, false);
        SwipeRefreshLayout.this.mProgress.setArrowScale(1.0F - paramAnonymousFloat);
        return;
      }
    }
  };
  private final Animation mAnimateToStartPosition = new Animation()
  {
    public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
    {
      SwipeRefreshLayout.this.moveToStart(paramAnonymousFloat);
    }
  };
  private int mCircleHeight;
  private CircleImageView mCircleView;
  private int mCircleViewIndex = -1;
  private int mCircleWidth;
  private int mCurrentTargetOffsetTop;
  private final DecelerateInterpolator mDecelerateInterpolator;
  protected int mFrom;
  private float mInitialDownY;
  private float mInitialMotionY;
  private boolean mIsBeingDragged;
  private OnRefreshListener mListener;
  private int mMediumAnimationDuration;
  private boolean mNotify;
  private boolean mOriginalOffsetCalculated = false;
  protected int mOriginalOffsetTop;
  private MaterialProgressDrawable mProgress;
  private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener()
  {
    public void onAnimationEnd(Animation paramAnonymousAnimation)
    {
      if (!SwipeRefreshLayout.this.mRefreshing)
      {
        SwipeRefreshLayout.this.mProgress.stop();
        SwipeRefreshLayout.this.mCircleView.setVisibility(8);
        SwipeRefreshLayout.this.setColorViewAlpha(255);
        if (SwipeRefreshLayout.this.mScale) {
          break label152;
        }
        SwipeRefreshLayout.this.setTargetOffsetTopAndBottom(SwipeRefreshLayout.this.mOriginalOffsetTop - SwipeRefreshLayout.this.mCurrentTargetOffsetTop, true);
      }
      for (;;)
      {
        SwipeRefreshLayout.access$802(SwipeRefreshLayout.this, SwipeRefreshLayout.this.mCircleView.getTop());
        return;
        SwipeRefreshLayout.this.mProgress.setAlpha(255);
        SwipeRefreshLayout.this.mProgress.start();
        if ((SwipeRefreshLayout.this.mNotify) && (SwipeRefreshLayout.this.mListener != null))
        {
          SwipeRefreshLayout.this.mListener.onRefresh();
          continue;
          label152:
          SwipeRefreshLayout.this.setAnimationProgress(0.0F);
        }
      }
    }
    
    public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
    
    public void onAnimationStart(Animation paramAnonymousAnimation) {}
  };
  private boolean mRefreshing = false;
  private boolean mReturningToStart;
  private boolean mScale;
  private Animation mScaleAnimation;
  private Animation mScaleDownAnimation;
  private Animation mScaleDownToStartAnimation;
  private float mSpinnerFinalOffset;
  private float mStartingScale;
  private View mTarget;
  private float mTotalDragDistance = -1.0F;
  private int mTouchSlop;
  private boolean mUsingCustomStart;
  
  public SwipeRefreshLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SwipeRefreshLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mTouchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    this.mMediumAnimationDuration = getResources().getInteger(17694721);
    setWillNotDraw(false);
    this.mDecelerateInterpolator = new DecelerateInterpolator(2.0F);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, LAYOUT_ATTRS);
    setEnabled(paramContext.getBoolean(0, true));
    paramContext.recycle();
    paramContext = getResources().getDisplayMetrics();
    this.mCircleWidth = ((int)(paramContext.density * 40.0F));
    this.mCircleHeight = ((int)(paramContext.density * 40.0F));
    createProgressView();
    ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    this.mSpinnerFinalOffset = (paramContext.density * 64.0F);
    this.mTotalDragDistance = this.mSpinnerFinalOffset;
  }
  
  private void animateOffsetToCorrectPosition(int paramInt, Animation.AnimationListener paramAnimationListener)
  {
    this.mFrom = paramInt;
    this.mAnimateToCorrectPosition.reset();
    this.mAnimateToCorrectPosition.setDuration(200L);
    this.mAnimateToCorrectPosition.setInterpolator(this.mDecelerateInterpolator);
    if (paramAnimationListener == null) {}
    for (;;)
    {
      this.mCircleView.clearAnimation();
      this.mCircleView.startAnimation(this.mAnimateToCorrectPosition);
      return;
      this.mCircleView.setAnimationListener(paramAnimationListener);
    }
  }
  
  private void animateOffsetToStartPosition(int paramInt, Animation.AnimationListener paramAnimationListener)
  {
    if (!this.mScale)
    {
      this.mFrom = paramInt;
      this.mAnimateToStartPosition.reset();
      this.mAnimateToStartPosition.setDuration(200L);
      this.mAnimateToStartPosition.setInterpolator(this.mDecelerateInterpolator);
      if (paramAnimationListener != null) {
        break label70;
      }
    }
    for (;;)
    {
      this.mCircleView.clearAnimation();
      this.mCircleView.startAnimation(this.mAnimateToStartPosition);
      return;
      startScaleDownReturnToStartAnimation(paramInt, paramAnimationListener);
      return;
      label70:
      this.mCircleView.setAnimationListener(paramAnimationListener);
    }
  }
  
  private void createProgressView()
  {
    this.mCircleView = new CircleImageView(getContext(), -328966, 20.0F);
    this.mProgress = new MaterialProgressDrawable(getContext(), this);
    this.mProgress.setBackgroundColor(-328966);
    this.mCircleView.setImageDrawable(this.mProgress);
    this.mCircleView.setVisibility(8);
    addView(this.mCircleView);
  }
  
  private void ensureTarget()
  {
    int i = 0;
    if (this.mTarget != null) {
      return;
    }
    View localView;
    while (i < getChildCount())
    {
      localView = getChildAt(i);
      if (!localView.equals(this.mCircleView)) {
        break;
      }
      i += 1;
    }
    return;
    this.mTarget = localView;
  }
  
  private float getMotionEventY(MotionEvent paramMotionEvent, int paramInt)
  {
    paramInt = MotionEventCompat.findPointerIndex(paramMotionEvent, paramInt);
    if (paramInt >= 0) {
      return MotionEventCompat.getY(paramMotionEvent, paramInt);
    }
    return -1.0F;
  }
  
  private boolean isAlphaUsedForScale()
  {
    return Build.VERSION.SDK_INT < 11;
  }
  
  private boolean isAnimationRunning(Animation paramAnimation)
  {
    if (paramAnimation == null) {}
    while ((!paramAnimation.hasStarted()) || (paramAnimation.hasEnded())) {
      return false;
    }
    return true;
  }
  
  private void moveToStart(float paramFloat)
  {
    setTargetOffsetTopAndBottom(this.mFrom + (int)((this.mOriginalOffsetTop - this.mFrom) * paramFloat) - this.mCircleView.getTop(), false);
  }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
  {
    int i = 0;
    int j = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (MotionEventCompat.getPointerId(paramMotionEvent, j) != this.mActivePointerId) {
      return;
    }
    if (j != 0) {}
    for (;;)
    {
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, i);
      return;
      i = 1;
    }
  }
  
  private void setAnimationProgress(float paramFloat)
  {
    if (!isAlphaUsedForScale())
    {
      ViewCompat.setScaleX(this.mCircleView, paramFloat);
      ViewCompat.setScaleY(this.mCircleView, paramFloat);
      return;
    }
    setColorViewAlpha((int)(255.0F * paramFloat));
  }
  
  private void setColorViewAlpha(int paramInt)
  {
    this.mCircleView.getBackground().setAlpha(paramInt);
    this.mProgress.setAlpha(paramInt);
  }
  
  private void setRefreshing(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mRefreshing == paramBoolean1) {
      return;
    }
    this.mNotify = paramBoolean2;
    ensureTarget();
    this.mRefreshing = paramBoolean1;
    if (!this.mRefreshing)
    {
      startScaleDownAnimation(this.mRefreshListener);
      return;
    }
    animateOffsetToCorrectPosition(this.mCurrentTargetOffsetTop, this.mRefreshListener);
  }
  
  private void setTargetOffsetTopAndBottom(int paramInt, boolean paramBoolean)
  {
    this.mCircleView.bringToFront();
    this.mCircleView.offsetTopAndBottom(paramInt);
    this.mCurrentTargetOffsetTop = this.mCircleView.getTop();
    if (!paramBoolean) {}
    while (Build.VERSION.SDK_INT >= 11) {
      return;
    }
    invalidate();
  }
  
  private Animation startAlphaAnimation(final int paramInt1, final int paramInt2)
  {
    if (!this.mScale) {}
    while (!isAlphaUsedForScale())
    {
      Animation local4 = new Animation()
      {
        public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
        {
          SwipeRefreshLayout.this.mProgress.setAlpha((int)(paramInt1 + (paramInt2 - paramInt1) * paramAnonymousFloat));
        }
      };
      local4.setDuration(300L);
      this.mCircleView.setAnimationListener(null);
      this.mCircleView.clearAnimation();
      this.mCircleView.startAnimation(local4);
      return local4;
    }
    return null;
  }
  
  private void startProgressAlphaMaxAnimation()
  {
    this.mAlphaMaxAnimation = startAlphaAnimation(this.mProgress.getAlpha(), 255);
  }
  
  private void startProgressAlphaStartAnimation()
  {
    this.mAlphaStartAnimation = startAlphaAnimation(this.mProgress.getAlpha(), 76);
  }
  
  private void startScaleDownAnimation(Animation.AnimationListener paramAnimationListener)
  {
    this.mScaleDownAnimation = new Animation()
    {
      public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
      {
        SwipeRefreshLayout.this.setAnimationProgress(1.0F - paramAnonymousFloat);
      }
    };
    this.mScaleDownAnimation.setDuration(150L);
    this.mCircleView.setAnimationListener(paramAnimationListener);
    this.mCircleView.clearAnimation();
    this.mCircleView.startAnimation(this.mScaleDownAnimation);
  }
  
  private void startScaleDownReturnToStartAnimation(int paramInt, Animation.AnimationListener paramAnimationListener)
  {
    this.mFrom = paramInt;
    if (!isAlphaUsedForScale())
    {
      this.mStartingScale = ViewCompat.getScaleX(this.mCircleView);
      this.mScaleDownToStartAnimation = new Animation()
      {
        public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
        {
          float f1 = SwipeRefreshLayout.this.mStartingScale;
          float f2 = -SwipeRefreshLayout.this.mStartingScale;
          SwipeRefreshLayout.this.setAnimationProgress(f1 + f2 * paramAnonymousFloat);
          SwipeRefreshLayout.this.moveToStart(paramAnonymousFloat);
        }
      };
      this.mScaleDownToStartAnimation.setDuration(150L);
      if (paramAnimationListener != null) {
        break label83;
      }
    }
    for (;;)
    {
      this.mCircleView.clearAnimation();
      this.mCircleView.startAnimation(this.mScaleDownToStartAnimation);
      return;
      this.mStartingScale = this.mProgress.getAlpha();
      break;
      label83:
      this.mCircleView.setAnimationListener(paramAnimationListener);
    }
  }
  
  private void startScaleUpAnimation(Animation.AnimationListener paramAnimationListener)
  {
    this.mCircleView.setVisibility(0);
    if (Build.VERSION.SDK_INT < 11)
    {
      this.mScaleAnimation = new Animation()
      {
        public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
        {
          SwipeRefreshLayout.this.setAnimationProgress(paramAnonymousFloat);
        }
      };
      this.mScaleAnimation.setDuration(this.mMediumAnimationDuration);
      if (paramAnimationListener != null) {
        break label76;
      }
    }
    for (;;)
    {
      this.mCircleView.clearAnimation();
      this.mCircleView.startAnimation(this.mScaleAnimation);
      return;
      this.mProgress.setAlpha(255);
      break;
      label76:
      this.mCircleView.setAnimationListener(paramAnimationListener);
    }
  }
  
  public boolean canChildScrollUp()
  {
    if (Build.VERSION.SDK_INT >= 14) {
      return ViewCompat.canScrollVertically(this.mTarget, -1);
    }
    if (!(this.mTarget instanceof AbsListView))
    {
      if (this.mTarget.getScrollY() <= 0) {
        return false;
      }
    }
    else
    {
      AbsListView localAbsListView = (AbsListView)this.mTarget;
      if (localAbsListView.getChildCount() <= 0) {}
      for (;;)
      {
        return false;
        if (localAbsListView.getFirstVisiblePosition() > 0) {}
        while (localAbsListView.getChildAt(0).getTop() < localAbsListView.getPaddingTop()) {
          return true;
        }
      }
    }
    return true;
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (this.mCircleViewIndex >= 0)
    {
      if (paramInt2 != paramInt1 - 1)
      {
        if (paramInt2 >= this.mCircleViewIndex) {
          break label31;
        }
        return paramInt2;
      }
    }
    else {
      return paramInt2;
    }
    return this.mCircleViewIndex;
    label31:
    return paramInt2 + 1;
  }
  
  public int getProgressCircleDiameter()
  {
    if (this.mCircleView == null) {
      return 0;
    }
    return this.mCircleView.getMeasuredHeight();
  }
  
  public boolean isRefreshing()
  {
    return this.mRefreshing;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    ensureTarget();
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (!this.mReturningToStart) {
      if (isEnabled()) {
        break label37;
      }
    }
    label37:
    while ((this.mReturningToStart) || (canChildScrollUp()) || (this.mRefreshing))
    {
      return false;
      if (i != 0) {
        break;
      }
      this.mReturningToStart = false;
      break;
    }
    switch (i)
    {
    }
    for (;;)
    {
      return this.mIsBeingDragged;
      setTargetOffsetTopAndBottom(this.mOriginalOffsetTop - this.mCircleView.getTop(), true);
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
      this.mIsBeingDragged = false;
      float f = getMotionEventY(paramMotionEvent, this.mActivePointerId);
      if (f == -1.0F) {
        return false;
      }
      this.mInitialDownY = f;
      continue;
      if (this.mActivePointerId != -1)
      {
        f = getMotionEventY(paramMotionEvent, this.mActivePointerId);
        if (f == -1.0F) {
          return false;
        }
      }
      else
      {
        Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
        return false;
      }
      if ((f - this.mInitialDownY > this.mTouchSlop) && (!this.mIsBeingDragged))
      {
        this.mInitialMotionY = (this.mInitialDownY + this.mTouchSlop);
        this.mIsBeingDragged = true;
        this.mProgress.setAlpha(76);
        continue;
        onSecondaryPointerUp(paramMotionEvent);
        continue;
        this.mIsBeingDragged = false;
        this.mActivePointerId = -1;
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt1 = getMeasuredWidth();
    paramInt2 = getMeasuredHeight();
    if (getChildCount() != 0) {
      if (this.mTarget == null) {
        break label137;
      }
    }
    while (this.mTarget != null)
    {
      View localView = this.mTarget;
      paramInt3 = getPaddingLeft();
      paramInt4 = getPaddingTop();
      localView.layout(paramInt3, paramInt4, paramInt1 - getPaddingLeft() - getPaddingRight() + paramInt3, paramInt2 - getPaddingTop() - getPaddingBottom() + paramInt4);
      paramInt2 = this.mCircleView.getMeasuredWidth();
      paramInt3 = this.mCircleView.getMeasuredHeight();
      this.mCircleView.layout(paramInt1 / 2 - paramInt2 / 2, this.mCurrentTargetOffsetTop, paramInt1 / 2 + paramInt2 / 2, this.mCurrentTargetOffsetTop + paramInt3);
      return;
      return;
      label137:
      ensureTarget();
    }
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    int i = 0;
    super.onMeasure(paramInt1, paramInt2);
    if (this.mTarget != null)
    {
      if (this.mTarget == null) {
        break label126;
      }
      this.mTarget.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), 1073741824));
      this.mCircleView.measure(View.MeasureSpec.makeMeasureSpec(this.mCircleWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mCircleHeight, 1073741824));
      if (!this.mUsingCustomStart) {
        break label127;
      }
      label103:
      this.mCircleViewIndex = -1;
      paramInt1 = i;
    }
    for (;;)
    {
      if (paramInt1 >= getChildCount())
      {
        return;
        ensureTarget();
        break;
        label126:
        return;
        label127:
        if (this.mOriginalOffsetCalculated) {
          break label103;
        }
        this.mOriginalOffsetCalculated = true;
        paramInt1 = -this.mCircleView.getMeasuredHeight();
        this.mOriginalOffsetTop = paramInt1;
        this.mCurrentTargetOffsetTop = paramInt1;
        break label103;
      }
      if (getChildAt(paramInt1) == this.mCircleView) {
        break label180;
      }
      paramInt1 += 1;
    }
    label180:
    this.mCircleViewIndex = paramInt1;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (!this.mReturningToStart) {
      if (isEnabled()) {
        break label35;
      }
    }
    label35:
    while ((this.mReturningToStart) || (canChildScrollUp()))
    {
      return false;
      if (i != 0) {
        break;
      }
      this.mReturningToStart = false;
      break;
    }
    float f2;
    float f1;
    switch (i)
    {
    case 4: 
    default: 
    case 0: 
    case 2: 
    case 5: 
    case 6: 
      for (;;)
      {
        return true;
        this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
        this.mIsBeingDragged = false;
        continue;
        i = MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId);
        if (i >= 0)
        {
          f2 = 0.5F * (MotionEventCompat.getY(paramMotionEvent, i) - this.mInitialMotionY);
          if (!this.mIsBeingDragged) {
            continue;
          }
          this.mProgress.showArrow(true);
          f1 = f2 / this.mTotalDragDistance;
          if (f1 < 0.0F) {
            return false;
          }
        }
        else
        {
          Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
          return false;
        }
        float f3 = Math.min(1.0F, Math.abs(f1));
        float f4 = (float)Math.max(f3 - 0.4D, 0.0D) * 5.0F / 3.0F;
        float f5 = Math.abs(f2);
        float f6 = this.mTotalDragDistance;
        label239:
        int j;
        if (!this.mUsingCustomStart)
        {
          f1 = this.mSpinnerFinalOffset;
          f5 = Math.max(0.0F, Math.min(f5 - f6, 2.0F * f1) / f1);
          f5 = (float)(f5 / 4.0F - Math.pow(f5 / 4.0F, 2.0D)) * 2.0F;
          i = this.mOriginalOffsetTop;
          j = (int)(f1 * f3 + f1 * f5 * 2.0F);
          if (this.mCircleView.getVisibility() != 0) {
            break label437;
          }
          label314:
          if (!this.mScale) {
            break label448;
          }
          label321:
          if (f2 >= this.mTotalDragDistance) {
            break label498;
          }
          if (this.mScale) {
            break label467;
          }
          label337:
          if (this.mProgress.getAlpha() > 76) {
            break label480;
          }
          label349:
          this.mProgress.setStartEndTrim(0.0F, Math.min(0.8F, 0.8F * f4));
          this.mProgress.setArrowScale(Math.min(1.0F, f4));
        }
        for (;;)
        {
          this.mProgress.setProgressRotation((0.4F * f4 - 0.25F + 2.0F * f5) * 0.5F);
          setTargetOffsetTopAndBottom(j + i - this.mCurrentTargetOffsetTop, true);
          break;
          f1 = this.mSpinnerFinalOffset - this.mOriginalOffsetTop;
          break label239;
          label437:
          this.mCircleView.setVisibility(0);
          break label314;
          label448:
          ViewCompat.setScaleX(this.mCircleView, 1.0F);
          ViewCompat.setScaleY(this.mCircleView, 1.0F);
          break label321;
          label467:
          setAnimationProgress(f2 / this.mTotalDragDistance);
          break label337;
          label480:
          if (isAnimationRunning(this.mAlphaStartAnimation)) {
            break label349;
          }
          startProgressAlphaStartAnimation();
          break label349;
          label498:
          if ((this.mProgress.getAlpha() < 255) && (!isAnimationRunning(this.mAlphaMaxAnimation))) {
            startProgressAlphaMaxAnimation();
          }
        }
        this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, MotionEventCompat.getActionIndex(paramMotionEvent));
        continue;
        onSecondaryPointerUp(paramMotionEvent);
      }
    }
    if (this.mActivePointerId != -1)
    {
      f1 = MotionEventCompat.getY(paramMotionEvent, MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId));
      f2 = this.mInitialMotionY;
      this.mIsBeingDragged = false;
      if ((f1 - f2) * 0.5F > this.mTotalDragDistance)
      {
        setRefreshing(true, true);
        this.mActivePointerId = -1;
        return false;
      }
    }
    else
    {
      if (i != 1) {}
      for (;;)
      {
        return false;
        Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
      }
    }
    this.mRefreshing = false;
    this.mProgress.setStartEndTrim(0.0F, 0.0F);
    paramMotionEvent = null;
    if (this.mScale) {}
    for (;;)
    {
      animateOffsetToStartPosition(this.mCurrentTargetOffsetTop, paramMotionEvent);
      this.mProgress.showArrow(false);
      break;
      paramMotionEvent = new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          if (SwipeRefreshLayout.this.mScale) {
            return;
          }
          SwipeRefreshLayout.this.startScaleDownAnimation(null);
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation) {}
      };
    }
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean) {}
  
  @Deprecated
  public void setColorScheme(int... paramVarArgs)
  {
    setColorSchemeResources(paramVarArgs);
  }
  
  public void setColorSchemeColors(int... paramVarArgs)
  {
    ensureTarget();
    this.mProgress.setColorSchemeColors(paramVarArgs);
  }
  
  public void setColorSchemeResources(int... paramVarArgs)
  {
    Resources localResources = getResources();
    int[] arrayOfInt = new int[paramVarArgs.length];
    int i = 0;
    for (;;)
    {
      if (i >= paramVarArgs.length)
      {
        setColorSchemeColors(arrayOfInt);
        return;
      }
      arrayOfInt[i] = localResources.getColor(paramVarArgs[i]);
      i += 1;
    }
  }
  
  public void setDistanceToTriggerSync(int paramInt)
  {
    this.mTotalDragDistance = paramInt;
  }
  
  public void setOnRefreshListener(OnRefreshListener paramOnRefreshListener)
  {
    this.mListener = paramOnRefreshListener;
  }
  
  @Deprecated
  public void setProgressBackgroundColor(int paramInt)
  {
    setProgressBackgroundColorSchemeResource(paramInt);
  }
  
  public void setProgressBackgroundColorSchemeColor(int paramInt)
  {
    this.mCircleView.setBackgroundColor(paramInt);
    this.mProgress.setBackgroundColor(paramInt);
  }
  
  public void setProgressBackgroundColorSchemeResource(int paramInt)
  {
    setProgressBackgroundColorSchemeColor(getResources().getColor(paramInt));
  }
  
  public void setProgressViewEndTarget(boolean paramBoolean, int paramInt)
  {
    this.mSpinnerFinalOffset = paramInt;
    this.mScale = paramBoolean;
    this.mCircleView.invalidate();
  }
  
  public void setProgressViewOffset(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    this.mScale = paramBoolean;
    this.mCircleView.setVisibility(8);
    this.mCurrentTargetOffsetTop = paramInt1;
    this.mOriginalOffsetTop = paramInt1;
    this.mSpinnerFinalOffset = paramInt2;
    this.mUsingCustomStart = true;
    this.mCircleView.invalidate();
  }
  
  public void setRefreshing(boolean paramBoolean)
  {
    if (!paramBoolean) {}
    while (this.mRefreshing == paramBoolean)
    {
      setRefreshing(paramBoolean, false);
      return;
    }
    this.mRefreshing = paramBoolean;
    if (this.mUsingCustomStart) {}
    for (int i = (int)this.mSpinnerFinalOffset;; i = (int)(this.mSpinnerFinalOffset + this.mOriginalOffsetTop))
    {
      setTargetOffsetTopAndBottom(i - this.mCurrentTargetOffsetTop, true);
      this.mNotify = false;
      startScaleUpAnimation(this.mRefreshListener);
      return;
    }
  }
  
  public void setSize(int paramInt)
  {
    DisplayMetrics localDisplayMetrics;
    int i;
    if (paramInt == 0)
    {
      localDisplayMetrics = getResources().getDisplayMetrics();
      if (paramInt == 0) {
        break label69;
      }
      i = (int)(localDisplayMetrics.density * 40.0F);
      this.mCircleWidth = i;
    }
    for (this.mCircleHeight = i;; this.mCircleHeight = i)
    {
      this.mCircleView.setImageDrawable(null);
      this.mProgress.updateSizes(paramInt);
      this.mCircleView.setImageDrawable(this.mProgress);
      return;
      if (paramInt == 1) {
        break;
      }
      return;
      label69:
      i = (int)(localDisplayMetrics.density * 56.0F);
      this.mCircleWidth = i;
    }
  }
  
  public static abstract interface OnRefreshListener
  {
    public abstract void onRefresh();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/widget/SwipeRefreshLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */