package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import java.util.Arrays;

public class ViewDragHelper
{
  private static final int BASE_SETTLE_DURATION = 256;
  public static final int DIRECTION_ALL = 3;
  public static final int DIRECTION_HORIZONTAL = 1;
  public static final int DIRECTION_VERTICAL = 2;
  public static final int EDGE_ALL = 15;
  public static final int EDGE_BOTTOM = 8;
  public static final int EDGE_LEFT = 1;
  public static final int EDGE_RIGHT = 2;
  private static final int EDGE_SIZE = 20;
  public static final int EDGE_TOP = 4;
  public static final int INVALID_POINTER = -1;
  private static final int MAX_SETTLE_DURATION = 600;
  public static final int STATE_DRAGGING = 1;
  public static final int STATE_IDLE = 0;
  public static final int STATE_SETTLING = 2;
  private static final String TAG = "ViewDragHelper";
  private static final Interpolator sInterpolator = new Interpolator()
  {
    public float getInterpolation(float paramAnonymousFloat)
    {
      paramAnonymousFloat -= 1.0F;
      return paramAnonymousFloat * (paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat) + 1.0F;
    }
  };
  private int mActivePointerId = -1;
  private final Callback mCallback;
  private View mCapturedView;
  private int mDragState;
  private int[] mEdgeDragsInProgress;
  private int[] mEdgeDragsLocked;
  private int mEdgeSize;
  private int[] mInitialEdgesTouched;
  private float[] mInitialMotionX;
  private float[] mInitialMotionY;
  private float[] mLastMotionX;
  private float[] mLastMotionY;
  private float mMaxVelocity;
  private float mMinVelocity;
  private final ViewGroup mParentView;
  private int mPointersDown;
  private boolean mReleaseInProgress;
  private ScrollerCompat mScroller;
  private final Runnable mSetIdleRunnable = new Runnable()
  {
    public void run()
    {
      ViewDragHelper.this.setDragState(0);
    }
  };
  private int mTouchSlop;
  private int mTrackingEdges;
  private VelocityTracker mVelocityTracker;
  
  private ViewDragHelper(Context paramContext, ViewGroup paramViewGroup, Callback paramCallback)
  {
    if (paramViewGroup != null)
    {
      if (paramCallback != null)
      {
        this.mParentView = paramViewGroup;
        this.mCallback = paramCallback;
        paramViewGroup = ViewConfiguration.get(paramContext);
        this.mEdgeSize = ((int)(paramContext.getResources().getDisplayMetrics().density * 20.0F + 0.5F));
        this.mTouchSlop = paramViewGroup.getScaledTouchSlop();
        this.mMaxVelocity = paramViewGroup.getScaledMaximumFlingVelocity();
        this.mMinVelocity = paramViewGroup.getScaledMinimumFlingVelocity();
        this.mScroller = ScrollerCompat.create(paramContext, sInterpolator);
      }
    }
    else {
      throw new IllegalArgumentException("Parent view may not be null");
    }
    throw new IllegalArgumentException("Callback may not be null");
  }
  
  private boolean checkNewEdgeDrag(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
  {
    paramFloat1 = Math.abs(paramFloat1);
    paramFloat2 = Math.abs(paramFloat2);
    if ((this.mInitialEdgesTouched[paramInt1] & paramInt2) != paramInt2) {}
    while (((this.mTrackingEdges & paramInt2) == 0) || ((this.mEdgeDragsLocked[paramInt1] & paramInt2) == paramInt2) || ((this.mEdgeDragsInProgress[paramInt1] & paramInt2) == paramInt2) || ((paramFloat1 <= this.mTouchSlop) && (paramFloat2 <= this.mTouchSlop))) {
      return false;
    }
    if ((paramFloat1 >= paramFloat2 * 0.5F) || (!this.mCallback.onEdgeLock(paramInt2))) {
      if ((this.mEdgeDragsInProgress[paramInt1] & paramInt2) == 0) {
        break label138;
      }
    }
    label138:
    while (paramFloat1 <= this.mTouchSlop)
    {
      return false;
      int[] arrayOfInt = this.mEdgeDragsLocked;
      arrayOfInt[paramInt1] |= paramInt2;
      return false;
    }
    return true;
  }
  
  private boolean checkTouchSlop(View paramView, float paramFloat1, float paramFloat2)
  {
    int i;
    int j;
    if (paramView != null)
    {
      if (this.mCallback.getViewHorizontalDragRange(paramView) > 0) {
        break label51;
      }
      i = 0;
      if (this.mCallback.getViewVerticalDragRange(paramView) > 0) {
        break label57;
      }
      j = 0;
      label32:
      if (i != 0) {
        break label63;
      }
    }
    for (;;)
    {
      if (i == 0)
      {
        if (j != 0) {
          break label110;
        }
        return false;
        return false;
        label51:
        i = 1;
        break;
        label57:
        j = 1;
        break label32;
        label63:
        if (j != 0) {
          return paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2 > this.mTouchSlop * this.mTouchSlop;
        }
      }
    }
    if (Math.abs(paramFloat1) > this.mTouchSlop) {
      return true;
    }
    return false;
    label110:
    return Math.abs(paramFloat2) > this.mTouchSlop;
  }
  
  private float clampMag(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f = Math.abs(paramFloat1);
    if (f < paramFloat2) {
      return 0.0F;
    }
    if (f > paramFloat3)
    {
      if (paramFloat1 > 0.0F) {
        return paramFloat3;
      }
      return -paramFloat3;
    }
    return paramFloat1;
  }
  
  private int clampMag(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = Math.abs(paramInt1);
    if (i >= paramInt2)
    {
      if (i <= paramInt3) {
        return paramInt1;
      }
    }
    else {
      return 0;
    }
    paramInt2 = paramInt3;
    if (paramInt1 <= 0) {
      paramInt2 = -paramInt3;
    }
    return paramInt2;
  }
  
  private void clearMotionHistory()
  {
    if (this.mInitialMotionX != null)
    {
      Arrays.fill(this.mInitialMotionX, 0.0F);
      Arrays.fill(this.mInitialMotionY, 0.0F);
      Arrays.fill(this.mLastMotionX, 0.0F);
      Arrays.fill(this.mLastMotionY, 0.0F);
      Arrays.fill(this.mInitialEdgesTouched, 0);
      Arrays.fill(this.mEdgeDragsInProgress, 0);
      Arrays.fill(this.mEdgeDragsLocked, 0);
      this.mPointersDown = 0;
      return;
    }
  }
  
  private void clearMotionHistory(int paramInt)
  {
    if (this.mInitialMotionX != null)
    {
      this.mInitialMotionX[paramInt] = 0.0F;
      this.mInitialMotionY[paramInt] = 0.0F;
      this.mLastMotionX[paramInt] = 0.0F;
      this.mLastMotionY[paramInt] = 0.0F;
      this.mInitialEdgesTouched[paramInt] = 0;
      this.mEdgeDragsInProgress[paramInt] = 0;
      this.mEdgeDragsLocked[paramInt] = 0;
      this.mPointersDown &= (1 << paramInt ^ 0xFFFFFFFF);
      return;
    }
  }
  
  private int computeAxisDuration(int paramInt1, int paramInt2, int paramInt3)
  {
    float f3;
    float f1;
    float f2;
    if (paramInt1 != 0)
    {
      int i = this.mParentView.getWidth();
      int j = i / 2;
      f3 = Math.min(1.0F, Math.abs(paramInt1) / i);
      f1 = j;
      f2 = j;
      f3 = distanceInfluenceForSnapDuration(f3);
      paramInt2 = Math.abs(paramInt2);
      if (paramInt2 > 0) {
        break label86;
      }
    }
    label86:
    for (paramInt1 = (int)((Math.abs(paramInt1) / paramInt3 + 1.0F) * 256.0F);; paramInt1 = Math.round(Math.abs((f3 * f2 + f1) / paramInt2) * 1000.0F) * 4)
    {
      return Math.min(paramInt1, 600);
      return 0;
    }
  }
  
  private int computeSettleDuration(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt3 = clampMag(paramInt3, (int)this.mMinVelocity, (int)this.mMaxVelocity);
    paramInt4 = clampMag(paramInt4, (int)this.mMinVelocity, (int)this.mMaxVelocity);
    int i = Math.abs(paramInt1);
    int j = Math.abs(paramInt2);
    int k = Math.abs(paramInt3);
    int m = Math.abs(paramInt4);
    int n = k + m;
    int i1 = i + j;
    float f1;
    if (paramInt3 == 0)
    {
      f1 = i / i1;
      if (paramInt4 != 0) {
        break label161;
      }
    }
    label161:
    for (float f2 = j / i1;; f2 = m / n)
    {
      paramInt1 = computeAxisDuration(paramInt1, paramInt3, this.mCallback.getViewHorizontalDragRange(paramView));
      paramInt2 = computeAxisDuration(paramInt2, paramInt4, this.mCallback.getViewVerticalDragRange(paramView));
      return (int)(f1 * paramInt1 + f2 * paramInt2);
      f1 = k / n;
      break;
    }
  }
  
  public static ViewDragHelper create(ViewGroup paramViewGroup, float paramFloat, Callback paramCallback)
  {
    paramViewGroup = create(paramViewGroup, paramCallback);
    paramViewGroup.mTouchSlop = ((int)(paramViewGroup.mTouchSlop * (1.0F / paramFloat)));
    return paramViewGroup;
  }
  
  public static ViewDragHelper create(ViewGroup paramViewGroup, Callback paramCallback)
  {
    return new ViewDragHelper(paramViewGroup.getContext(), paramViewGroup, paramCallback);
  }
  
  private void dispatchViewReleased(float paramFloat1, float paramFloat2)
  {
    this.mReleaseInProgress = true;
    this.mCallback.onViewReleased(this.mCapturedView, paramFloat1, paramFloat2);
    this.mReleaseInProgress = false;
    if (this.mDragState != 1) {
      return;
    }
    setDragState(0);
  }
  
  private float distanceInfluenceForSnapDuration(float paramFloat)
  {
    return (float)Math.sin((float)((paramFloat - 0.5F) * 0.4712389167638204D));
  }
  
  private void dragTo(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.mCapturedView.getLeft();
    int j = this.mCapturedView.getTop();
    if (paramInt3 == 0)
    {
      if (paramInt4 != 0) {
        break label81;
      }
      label27:
      if (paramInt3 == 0) {
        break label110;
      }
    }
    label81:
    label110:
    while (paramInt4 != 0)
    {
      this.mCallback.onViewPositionChanged(this.mCapturedView, paramInt1, paramInt2, paramInt1 - i, paramInt2 - j);
      return;
      paramInt1 = this.mCallback.clampViewPositionHorizontal(this.mCapturedView, paramInt1, paramInt3);
      this.mCapturedView.offsetLeftAndRight(paramInt1 - i);
      break;
      paramInt2 = this.mCallback.clampViewPositionVertical(this.mCapturedView, paramInt2, paramInt4);
      this.mCapturedView.offsetTopAndBottom(paramInt2 - j);
      break label27;
    }
  }
  
  private void ensureMotionHistorySizeForId(int paramInt)
  {
    float[] arrayOfFloat1;
    float[] arrayOfFloat2;
    float[] arrayOfFloat3;
    float[] arrayOfFloat4;
    int[] arrayOfInt1;
    int[] arrayOfInt2;
    int[] arrayOfInt3;
    if (this.mInitialMotionX == null)
    {
      arrayOfFloat1 = new float[paramInt + 1];
      arrayOfFloat2 = new float[paramInt + 1];
      arrayOfFloat3 = new float[paramInt + 1];
      arrayOfFloat4 = new float[paramInt + 1];
      arrayOfInt1 = new int[paramInt + 1];
      arrayOfInt2 = new int[paramInt + 1];
      arrayOfInt3 = new int[paramInt + 1];
      if (this.mInitialMotionX != null) {
        break label112;
      }
    }
    for (;;)
    {
      this.mInitialMotionX = arrayOfFloat1;
      this.mInitialMotionY = arrayOfFloat2;
      this.mLastMotionX = arrayOfFloat3;
      this.mLastMotionY = arrayOfFloat4;
      this.mInitialEdgesTouched = arrayOfInt1;
      this.mEdgeDragsInProgress = arrayOfInt2;
      this.mEdgeDragsLocked = arrayOfInt3;
      return;
      if (this.mInitialMotionX.length <= paramInt) {
        break;
      }
      return;
      label112:
      System.arraycopy(this.mInitialMotionX, 0, arrayOfFloat1, 0, this.mInitialMotionX.length);
      System.arraycopy(this.mInitialMotionY, 0, arrayOfFloat2, 0, this.mInitialMotionY.length);
      System.arraycopy(this.mLastMotionX, 0, arrayOfFloat3, 0, this.mLastMotionX.length);
      System.arraycopy(this.mLastMotionY, 0, arrayOfFloat4, 0, this.mLastMotionY.length);
      System.arraycopy(this.mInitialEdgesTouched, 0, arrayOfInt1, 0, this.mInitialEdgesTouched.length);
      System.arraycopy(this.mEdgeDragsInProgress, 0, arrayOfInt2, 0, this.mEdgeDragsInProgress.length);
      System.arraycopy(this.mEdgeDragsLocked, 0, arrayOfInt3, 0, this.mEdgeDragsLocked.length);
    }
  }
  
  private boolean forceSettleCapturedViewAt(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.mCapturedView.getLeft();
    int j = this.mCapturedView.getTop();
    paramInt1 -= i;
    paramInt2 -= j;
    if (paramInt1 != 0) {}
    while (paramInt2 != 0)
    {
      paramInt3 = computeSettleDuration(this.mCapturedView, paramInt1, paramInt2, paramInt3, paramInt4);
      this.mScroller.startScroll(i, j, paramInt1, paramInt2, paramInt3);
      setDragState(2);
      return true;
    }
    this.mScroller.abortAnimation();
    setDragState(0);
    return false;
  }
  
  private int getEdgesTouched(int paramInt1, int paramInt2)
  {
    int i = 0;
    if (paramInt1 >= this.mParentView.getLeft() + this.mEdgeSize)
    {
      if (paramInt2 < this.mParentView.getTop() + this.mEdgeSize) {
        break label73;
      }
      label34:
      if (paramInt1 > this.mParentView.getRight() - this.mEdgeSize) {
        break label80;
      }
    }
    for (;;)
    {
      if (paramInt2 > this.mParentView.getBottom() - this.mEdgeSize) {
        break label87;
      }
      return i;
      i = 1;
      break;
      label73:
      i |= 0x4;
      break label34;
      label80:
      i |= 0x2;
    }
    label87:
    return i | 0x8;
  }
  
  private void releaseViewForPointerUp()
  {
    this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxVelocity);
    dispatchViewReleased(clampMag(VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity), clampMag(VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity));
  }
  
  private void reportNewEdgeDrags(float paramFloat1, float paramFloat2, int paramInt)
  {
    int i = 0;
    if (!checkNewEdgeDrag(paramFloat1, paramFloat2, paramInt, 1))
    {
      if (checkNewEdgeDrag(paramFloat2, paramFloat1, paramInt, 4)) {
        break label60;
      }
      label25:
      if (checkNewEdgeDrag(paramFloat1, paramFloat2, paramInt, 2)) {
        break label69;
      }
      label36:
      if (checkNewEdgeDrag(paramFloat2, paramFloat1, paramInt, 8)) {
        break label78;
      }
    }
    for (;;)
    {
      if (i != 0) {
        break label88;
      }
      return;
      i = 1;
      break;
      label60:
      i |= 0x4;
      break label25;
      label69:
      i |= 0x2;
      break label36;
      label78:
      i |= 0x8;
    }
    label88:
    int[] arrayOfInt = this.mEdgeDragsInProgress;
    arrayOfInt[paramInt] |= i;
    this.mCallback.onEdgeDragStarted(i, paramInt);
  }
  
  private void saveInitialMotion(float paramFloat1, float paramFloat2, int paramInt)
  {
    ensureMotionHistorySizeForId(paramInt);
    float[] arrayOfFloat = this.mInitialMotionX;
    this.mLastMotionX[paramInt] = paramFloat1;
    arrayOfFloat[paramInt] = paramFloat1;
    arrayOfFloat = this.mInitialMotionY;
    this.mLastMotionY[paramInt] = paramFloat2;
    arrayOfFloat[paramInt] = paramFloat2;
    this.mInitialEdgesTouched[paramInt] = getEdgesTouched((int)paramFloat1, (int)paramFloat2);
    this.mPointersDown |= 1 << paramInt;
  }
  
  private void saveLastMotion(MotionEvent paramMotionEvent)
  {
    int j = MotionEventCompat.getPointerCount(paramMotionEvent);
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      int k = MotionEventCompat.getPointerId(paramMotionEvent, i);
      float f1 = MotionEventCompat.getX(paramMotionEvent, i);
      float f2 = MotionEventCompat.getY(paramMotionEvent, i);
      this.mLastMotionX[k] = f1;
      this.mLastMotionY[k] = f2;
      i += 1;
    }
  }
  
  public void abort()
  {
    cancel();
    if (this.mDragState != 2) {}
    for (;;)
    {
      setDragState(0);
      return;
      int i = this.mScroller.getCurrX();
      int j = this.mScroller.getCurrY();
      this.mScroller.abortAnimation();
      int k = this.mScroller.getCurrX();
      int m = this.mScroller.getCurrY();
      this.mCallback.onViewPositionChanged(this.mCapturedView, k, m, k - i, m - j);
    }
  }
  
  protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!(paramView instanceof ViewGroup)) {
      if (paramBoolean) {
        break label152;
      }
    }
    for (;;)
    {
      return false;
      ViewGroup localViewGroup = (ViewGroup)paramView;
      int j = paramView.getScrollX();
      int k = paramView.getScrollY();
      int i = localViewGroup.getChildCount() - 1;
      label40:
      View localView;
      if (i >= 0)
      {
        localView = localViewGroup.getChildAt(i);
        if (paramInt3 + j >= localView.getLeft()) {
          break label76;
        }
      }
      label76:
      while ((paramInt3 + j >= localView.getRight()) || (paramInt4 + k < localView.getTop()) || (paramInt4 + k >= localView.getBottom()) || (!canScroll(localView, true, paramInt1, paramInt2, paramInt3 + j - localView.getLeft(), paramInt4 + k - localView.getTop())))
      {
        i -= 1;
        break label40;
        break;
      }
      return true;
      label152:
      if (ViewCompat.canScrollHorizontally(paramView, -paramInt1)) {}
      while (ViewCompat.canScrollVertically(paramView, -paramInt2)) {
        return true;
      }
    }
  }
  
  public void cancel()
  {
    this.mActivePointerId = -1;
    clearMotionHistory();
    if (this.mVelocityTracker == null) {
      return;
    }
    this.mVelocityTracker.recycle();
    this.mVelocityTracker = null;
  }
  
  public void captureChildView(View paramView, int paramInt)
  {
    if (paramView.getParent() == this.mParentView)
    {
      this.mCapturedView = paramView;
      this.mActivePointerId = paramInt;
      this.mCallback.onViewCaptured(paramView, paramInt);
      setDragState(1);
      return;
    }
    throw new IllegalArgumentException("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (" + this.mParentView + ")");
  }
  
  public boolean checkTouchSlop(int paramInt)
  {
    int j = this.mInitialMotionX.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return false;
      }
      if (checkTouchSlop(paramInt, i)) {
        break;
      }
      i += 1;
    }
    return true;
  }
  
  public boolean checkTouchSlop(int paramInt1, int paramInt2)
  {
    int i;
    label27:
    float f1;
    float f2;
    if (isPointerDown(paramInt2))
    {
      if ((paramInt1 & 0x1) == 1) {
        break label74;
      }
      i = 0;
      if ((paramInt1 & 0x2) == 2) {
        break label80;
      }
      paramInt1 = 0;
      f1 = this.mLastMotionX[paramInt2] - this.mInitialMotionX[paramInt2];
      f2 = this.mLastMotionY[paramInt2] - this.mInitialMotionY[paramInt2];
      if (i != 0) {
        break label85;
      }
    }
    for (;;)
    {
      if (i == 0)
      {
        if (paramInt1 != 0) {
          break label133;
        }
        return false;
        return false;
        label74:
        i = 1;
        break;
        label80:
        paramInt1 = 1;
        break label27;
        label85:
        if (paramInt1 != 0) {
          return f1 * f1 + f2 * f2 > this.mTouchSlop * this.mTouchSlop;
        }
      }
    }
    if (Math.abs(f1) > this.mTouchSlop) {
      return true;
    }
    return false;
    label133:
    return Math.abs(f2) > this.mTouchSlop;
  }
  
  public boolean continueSettling(boolean paramBoolean)
  {
    if (this.mDragState != 2) {}
    while (this.mDragState != 2)
    {
      return false;
      boolean bool = this.mScroller.computeScrollOffset();
      int i = this.mScroller.getCurrX();
      int j = this.mScroller.getCurrY();
      int k = i - this.mCapturedView.getLeft();
      int m = j - this.mCapturedView.getTop();
      if (k == 0)
      {
        label70:
        if (m != 0) {
          break label131;
        }
        label75:
        if (k == 0) {
          break label143;
        }
        label80:
        this.mCallback.onViewPositionChanged(this.mCapturedView, i, j, k, m);
        label97:
        if (bool) {
          break label151;
        }
      }
      for (;;)
      {
        if (bool) {
          break label184;
        }
        if (paramBoolean) {
          break label186;
        }
        setDragState(0);
        break;
        this.mCapturedView.offsetLeftAndRight(k);
        break label70;
        label131:
        this.mCapturedView.offsetTopAndBottom(m);
        break label75;
        label143:
        if (m != 0) {
          break label80;
        }
        break label97;
        label151:
        if ((i == this.mScroller.getFinalX()) && (j == this.mScroller.getFinalY()))
        {
          this.mScroller.abortAnimation();
          bool = false;
        }
      }
      label184:
      continue;
      label186:
      this.mParentView.post(this.mSetIdleRunnable);
    }
    return true;
  }
  
  public View findTopChildUnder(int paramInt1, int paramInt2)
  {
    int i = this.mParentView.getChildCount();
    View localView;
    do
    {
      int j;
      do
      {
        do
        {
          do
          {
            j = i - 1;
            if (j < 0) {
              return null;
            }
            localView = this.mParentView.getChildAt(this.mCallback.getOrderedChildIndex(j));
            i = j;
          } while (paramInt1 < localView.getLeft());
          i = j;
        } while (paramInt1 >= localView.getRight());
        i = j;
      } while (paramInt2 < localView.getTop());
      i = j;
    } while (paramInt2 >= localView.getBottom());
    return localView;
  }
  
  public void flingCapturedView(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mReleaseInProgress)
    {
      this.mScroller.fling(this.mCapturedView.getLeft(), this.mCapturedView.getTop(), (int)VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), (int)VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId), paramInt1, paramInt3, paramInt2, paramInt4);
      setDragState(2);
      return;
    }
    throw new IllegalStateException("Cannot flingCapturedView outside of a call to Callback#onViewReleased");
  }
  
  public int getActivePointerId()
  {
    return this.mActivePointerId;
  }
  
  public View getCapturedView()
  {
    return this.mCapturedView;
  }
  
  public int getEdgeSize()
  {
    return this.mEdgeSize;
  }
  
  public float getMinVelocity()
  {
    return this.mMinVelocity;
  }
  
  public int getTouchSlop()
  {
    return this.mTouchSlop;
  }
  
  public int getViewDragState()
  {
    return this.mDragState;
  }
  
  public boolean isCapturedViewUnder(int paramInt1, int paramInt2)
  {
    return isViewUnder(this.mCapturedView, paramInt1, paramInt2);
  }
  
  public boolean isEdgeTouched(int paramInt)
  {
    int j = this.mInitialEdgesTouched.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return false;
      }
      if (isEdgeTouched(paramInt, i)) {
        break;
      }
      i += 1;
    }
    return true;
  }
  
  public boolean isEdgeTouched(int paramInt1, int paramInt2)
  {
    if (!isPointerDown(paramInt2)) {}
    while ((this.mInitialEdgesTouched[paramInt2] & paramInt1) == 0) {
      return false;
    }
    return true;
  }
  
  public boolean isPointerDown(int paramInt)
  {
    return (this.mPointersDown & 1 << paramInt) != 0;
  }
  
  public boolean isViewUnder(View paramView, int paramInt1, int paramInt2)
  {
    if (paramView != null) {
      if (paramInt1 >= paramView.getLeft()) {
        break label16;
      }
    }
    label16:
    while ((paramInt1 >= paramView.getRight()) || (paramInt2 < paramView.getTop()) || (paramInt2 >= paramView.getBottom()))
    {
      return false;
      return false;
    }
    return true;
  }
  
  public void processTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = 0;
    int j = 0;
    int m = MotionEventCompat.getActionMasked(paramMotionEvent);
    int k = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (m != 0)
    {
      if (this.mVelocityTracker == null) {
        break label92;
      }
      label30:
      this.mVelocityTracker.addMovement(paramMotionEvent);
    }
    label92:
    float f1;
    float f2;
    switch (m)
    {
    case 4: 
    default: 
    case 0: 
    case 5: 
      do
      {
        do
        {
          do
          {
            return;
            cancel();
            break;
            this.mVelocityTracker = VelocityTracker.obtain();
            break label30;
            f1 = paramMotionEvent.getX();
            f2 = paramMotionEvent.getY();
            i = MotionEventCompat.getPointerId(paramMotionEvent, 0);
            paramMotionEvent = findTopChildUnder((int)f1, (int)f2);
            saveInitialMotion(f1, f2, i);
            tryCaptureViewForDrag(paramMotionEvent, i);
            j = this.mInitialEdgesTouched[i];
          } while ((this.mTrackingEdges & j) == 0);
          this.mCallback.onEdgeTouched(j & this.mTrackingEdges, i);
          return;
          i = MotionEventCompat.getPointerId(paramMotionEvent, k);
          f1 = MotionEventCompat.getX(paramMotionEvent, k);
          f2 = MotionEventCompat.getY(paramMotionEvent, k);
          saveInitialMotion(f1, f2, i);
          if (this.mDragState == 0) {
            break label240;
          }
        } while (!isCapturedViewUnder((int)f1, (int)f2));
        tryCaptureViewForDrag(this.mCapturedView, i);
        return;
        tryCaptureViewForDrag(findTopChildUnder((int)f1, (int)f2), i);
        j = this.mInitialEdgesTouched[i];
      } while ((this.mTrackingEdges & j) == 0);
      this.mCallback.onEdgeTouched(j & this.mTrackingEdges, i);
      return;
    case 2: 
      if (this.mDragState != 1)
      {
        k = MotionEventCompat.getPointerCount(paramMotionEvent);
        i = j;
        if (i < k) {
          break label408;
        }
      }
      float f3;
      float f4;
      do
      {
        saveLastMotion(paramMotionEvent);
        return;
        i = MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId);
        f1 = MotionEventCompat.getX(paramMotionEvent, i);
        f2 = MotionEventCompat.getY(paramMotionEvent, i);
        i = (int)(f1 - this.mLastMotionX[this.mActivePointerId]);
        j = (int)(f2 - this.mLastMotionY[this.mActivePointerId]);
        dragTo(this.mCapturedView.getLeft() + i, this.mCapturedView.getTop() + j, i, j);
        saveLastMotion(paramMotionEvent);
        return;
        j = MotionEventCompat.getPointerId(paramMotionEvent, i);
        f1 = MotionEventCompat.getX(paramMotionEvent, i);
        f2 = MotionEventCompat.getY(paramMotionEvent, i);
        f3 = f1 - this.mInitialMotionX[j];
        f4 = f2 - this.mInitialMotionY[j];
        reportNewEdgeDrags(f3, f4, j);
      } while (this.mDragState == 1);
      View localView = findTopChildUnder((int)f1, (int)f2);
      if (!checkTouchSlop(localView, f3, f4)) {}
      for (;;)
      {
        i += 1;
        break;
        if (tryCaptureViewForDrag(localView, j)) {
          break label316;
        }
      }
    case 6: 
      j = MotionEventCompat.getPointerId(paramMotionEvent, k);
      if (this.mDragState != 1) {}
      while (j != this.mActivePointerId)
      {
        clearMotionHistory(j);
        return;
      }
      k = MotionEventCompat.getPointerCount(paramMotionEvent);
      if (i >= k) {}
      for (i = -1; i == -1; i = this.mActivePointerId)
      {
        releaseViewForPointerUp();
        break;
        m = MotionEventCompat.getPointerId(paramMotionEvent, i);
        if (m != this.mActivePointerId)
        {
          f1 = MotionEventCompat.getX(paramMotionEvent, i);
          f2 = MotionEventCompat.getY(paramMotionEvent, i);
          if (findTopChildUnder((int)f1, (int)f2) == this.mCapturedView) {
            break label632;
          }
        }
        while (!tryCaptureViewForDrag(this.mCapturedView, m))
        {
          i += 1;
          break;
        }
      }
    case 1: 
      label240:
      label316:
      label408:
      label632:
      if (this.mDragState != 1) {}
      for (;;)
      {
        cancel();
        return;
        releaseViewForPointerUp();
      }
    }
    if (this.mDragState != 1) {}
    for (;;)
    {
      cancel();
      return;
      dispatchViewReleased(0.0F, 0.0F);
    }
  }
  
  void setDragState(int paramInt)
  {
    this.mParentView.removeCallbacks(this.mSetIdleRunnable);
    if (this.mDragState == paramInt) {}
    do
    {
      return;
      this.mDragState = paramInt;
      this.mCallback.onViewDragStateChanged(paramInt);
    } while (this.mDragState != 0);
    this.mCapturedView = null;
  }
  
  public void setEdgeTrackingEnabled(int paramInt)
  {
    this.mTrackingEdges = paramInt;
  }
  
  public void setMinVelocity(float paramFloat)
  {
    this.mMinVelocity = paramFloat;
  }
  
  public boolean settleCapturedViewAt(int paramInt1, int paramInt2)
  {
    if (this.mReleaseInProgress) {
      return forceSettleCapturedViewAt(paramInt1, paramInt2, (int)VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), (int)VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId));
    }
    throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
  }
  
  public boolean shouldInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int j = MotionEventCompat.getActionMasked(paramMotionEvent);
    int i = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (j != 0)
    {
      if (this.mVelocityTracker == null) {
        break label93;
      }
      label24:
      this.mVelocityTracker.addMovement(paramMotionEvent);
      switch (j)
      {
      }
    }
    for (;;)
    {
      if (this.mDragState == 1) {
        break label613;
      }
      return false;
      cancel();
      break;
      label93:
      this.mVelocityTracker = VelocityTracker.obtain();
      break label24;
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      i = MotionEventCompat.getPointerId(paramMotionEvent, 0);
      saveInitialMotion(f1, f2, i);
      paramMotionEvent = findTopChildUnder((int)f1, (int)f2);
      if (paramMotionEvent != this.mCapturedView) {}
      for (;;)
      {
        j = this.mInitialEdgesTouched[i];
        if ((this.mTrackingEdges & j) == 0) {
          break;
        }
        this.mCallback.onEdgeTouched(j & this.mTrackingEdges, i);
        break;
        if (this.mDragState == 2) {
          tryCaptureViewForDrag(paramMotionEvent, i);
        }
      }
      j = MotionEventCompat.getPointerId(paramMotionEvent, i);
      f1 = MotionEventCompat.getX(paramMotionEvent, i);
      f2 = MotionEventCompat.getY(paramMotionEvent, i);
      saveInitialMotion(f1, f2, j);
      if (this.mDragState != 0)
      {
        if (this.mDragState == 2)
        {
          paramMotionEvent = findTopChildUnder((int)f1, (int)f2);
          if (paramMotionEvent == this.mCapturedView) {
            tryCaptureViewForDrag(paramMotionEvent, j);
          }
        }
      }
      else
      {
        i = this.mInitialEdgesTouched[j];
        if ((this.mTrackingEdges & i) != 0)
        {
          this.mCallback.onEdgeTouched(i & this.mTrackingEdges, j);
          continue;
          int k = MotionEventCompat.getPointerCount(paramMotionEvent);
          i = 0;
          if (i >= k) {
            label328:
            break label544;
          }
          label329:
          int m;
          float f3;
          float f4;
          View localView;
          label396:
          label399:
          label404:
          do
          {
            saveLastMotion(paramMotionEvent);
            break;
            m = MotionEventCompat.getPointerId(paramMotionEvent, i);
            f1 = MotionEventCompat.getX(paramMotionEvent, i);
            f2 = MotionEventCompat.getY(paramMotionEvent, i);
            f3 = f1 - this.mInitialMotionX[m];
            f4 = f2 - this.mInitialMotionY[m];
            localView = findTopChildUnder((int)f1, (int)f2);
            if (localView != null) {
              break label436;
            }
            j = 0;
            if (j != 0) {
              break label455;
            }
            reportNewEdgeDrags(f3, f4, m);
          } while (this.mDragState == 1);
          if (j == 0) {}
          for (;;)
          {
            i += 1;
            break;
            label436:
            if (!checkTouchSlop(localView, f3, f4)) {
              break label396;
            }
            j = 1;
            break label399;
            label455:
            int n = localView.getLeft();
            int i1 = (int)f3;
            i1 = this.mCallback.clampViewPositionHorizontal(localView, i1 + n, (int)f3);
            int i2 = localView.getTop();
            int i3 = (int)f4;
            i3 = this.mCallback.clampViewPositionVertical(localView, i3 + i2, (int)f4);
            int i4 = this.mCallback.getViewHorizontalDragRange(localView);
            int i5 = this.mCallback.getViewVerticalDragRange(localView);
            if (i4 == 0)
            {
              label544:
              if (i5 == 0) {
                break label329;
              }
              if (i5 <= 0) {
                break label404;
              }
              if (i3 == i2) {
                break label329;
              }
              break label404;
            }
            if (i4 <= 0) {
              break label404;
            }
            if (i1 == n) {
              break label328;
            }
            break label404;
            if (tryCaptureViewForDrag(localView, m)) {
              break label329;
            }
          }
          clearMotionHistory(MotionEventCompat.getPointerId(paramMotionEvent, i));
          continue;
          cancel();
        }
      }
    }
    label613:
    return true;
  }
  
  public boolean smoothSlideViewTo(View paramView, int paramInt1, int paramInt2)
  {
    this.mCapturedView = paramView;
    this.mActivePointerId = -1;
    boolean bool = forceSettleCapturedViewAt(paramInt1, paramInt2, 0, 0);
    if (bool) {}
    while ((this.mDragState != 0) || (this.mCapturedView == null)) {
      return bool;
    }
    this.mCapturedView = null;
    return bool;
  }
  
  boolean tryCaptureViewForDrag(View paramView, int paramInt)
  {
    if (paramView != this.mCapturedView) {
      if (paramView != null) {
        break label24;
      }
    }
    label24:
    while (!this.mCallback.tryCaptureView(paramView, paramInt))
    {
      return false;
      if (this.mActivePointerId != paramInt) {
        break;
      }
      return true;
    }
    this.mActivePointerId = paramInt;
    captureChildView(paramView, paramInt);
    return true;
  }
  
  public static abstract class Callback
  {
    public int clampViewPositionHorizontal(View paramView, int paramInt1, int paramInt2)
    {
      return 0;
    }
    
    public int clampViewPositionVertical(View paramView, int paramInt1, int paramInt2)
    {
      return 0;
    }
    
    public int getOrderedChildIndex(int paramInt)
    {
      return paramInt;
    }
    
    public int getViewHorizontalDragRange(View paramView)
    {
      return 0;
    }
    
    public int getViewVerticalDragRange(View paramView)
    {
      return 0;
    }
    
    public void onEdgeDragStarted(int paramInt1, int paramInt2) {}
    
    public boolean onEdgeLock(int paramInt)
    {
      return false;
    }
    
    public void onEdgeTouched(int paramInt1, int paramInt2) {}
    
    public void onViewCaptured(View paramView, int paramInt) {}
    
    public void onViewDragStateChanged(int paramInt) {}
    
    public void onViewPositionChanged(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    public void onViewReleased(View paramView, float paramFloat1, float paramFloat2) {}
    
    public abstract boolean tryCaptureView(View paramView, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/widget/ViewDragHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */