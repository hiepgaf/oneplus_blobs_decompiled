package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SlidingPaneLayout
  extends ViewGroup
{
  private static final int DEFAULT_FADE_COLOR = -858993460;
  private static final int DEFAULT_OVERHANG_SIZE = 32;
  static final SlidingPanelLayoutImpl IMPL = new SlidingPanelLayoutImplJB();
  private static final int MIN_FLING_VELOCITY = 400;
  private static final String TAG = "SlidingPaneLayout";
  private boolean mCanSlide;
  private int mCoveredFadeColor;
  private final ViewDragHelper mDragHelper;
  private boolean mFirstLayout = true;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private boolean mIsUnableToDrag;
  private final int mOverhangSize;
  private PanelSlideListener mPanelSlideListener;
  private int mParallaxBy;
  private float mParallaxOffset;
  private final ArrayList<DisableLayerRunnable> mPostedRunnables = new ArrayList();
  private boolean mPreservedOpenState;
  private Drawable mShadowDrawableLeft;
  private Drawable mShadowDrawableRight;
  private float mSlideOffset;
  private int mSlideRange;
  private View mSlideableView;
  private int mSliderFadeColor = -858993460;
  private final Rect mTmpRect = new Rect();
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 17)
    {
      if (i < 16) {
        IMPL = new SlidingPanelLayoutImplBase();
      }
    }
    else
    {
      IMPL = new SlidingPanelLayoutImplJBMR1();
      return;
    }
  }
  
  public SlidingPaneLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SlidingPaneLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SlidingPaneLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    float f = paramContext.getResources().getDisplayMetrics().density;
    this.mOverhangSize = ((int)(32.0F * f + 0.5F));
    ViewConfiguration.get(paramContext);
    setWillNotDraw(false);
    ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
    ViewCompat.setImportantForAccessibility(this, 1);
    this.mDragHelper = ViewDragHelper.create(this, 0.5F, new DragHelperCallback(null));
    this.mDragHelper.setMinVelocity(f * 400.0F);
  }
  
  private boolean closePane(View paramView, int paramInt)
  {
    if (this.mFirstLayout) {}
    while (smoothSlideTo(0.0F, paramInt))
    {
      this.mPreservedOpenState = false;
      return true;
    }
    return false;
  }
  
  private void dimChildView(View paramView, float paramFloat, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if ((paramFloat <= 0.0F) || (paramInt == 0))
    {
      if (ViewCompat.getLayerType(paramView) != 0) {}
    }
    else
    {
      int i = (int)(((0xFF000000 & paramInt) >>> 24) * paramFloat);
      if (localLayoutParams.dimPaint != null)
      {
        localLayoutParams.dimPaint.setColorFilter(new PorterDuffColorFilter(i << 24 | 0xFFFFFF & paramInt, PorterDuff.Mode.SRC_OVER));
        if (ViewCompat.getLayerType(paramView) != 2) {
          break label106;
        }
      }
      for (;;)
      {
        invalidateChildRegion(paramView);
        return;
        localLayoutParams.dimPaint = new Paint();
        break;
        label106:
        ViewCompat.setLayerType(paramView, 2, localLayoutParams.dimPaint);
      }
    }
    if (localLayoutParams.dimPaint == null) {}
    for (;;)
    {
      paramView = new DisableLayerRunnable(paramView);
      this.mPostedRunnables.add(paramView);
      ViewCompat.postOnAnimation(this, paramView);
      return;
      localLayoutParams.dimPaint.setColorFilter(null);
    }
  }
  
  private void invalidateChildRegion(View paramView)
  {
    IMPL.invalidateChildRegion(this, paramView);
  }
  
  private boolean isLayoutRtlSupport()
  {
    boolean bool = true;
    if (ViewCompat.getLayoutDirection(this) != 1) {
      bool = false;
    }
    return bool;
  }
  
  private void onPanelDragged(int paramInt)
  {
    LayoutParams localLayoutParams;
    int i;
    label48:
    int j;
    if (this.mSlideableView != null)
    {
      boolean bool = isLayoutRtlSupport();
      localLayoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
      i = this.mSlideableView.getWidth();
      if (bool) {
        break label105;
      }
      if (bool) {
        break label117;
      }
      i = getPaddingLeft();
      if (bool) {
        break label125;
      }
      j = localLayoutParams.leftMargin;
      label59:
      this.mSlideOffset = ((paramInt - (i + j)) / this.mSlideRange);
      if (this.mParallaxBy != 0) {
        break label134;
      }
      label82:
      if (localLayoutParams.dimWhenOffset) {
        break label145;
      }
    }
    for (;;)
    {
      dispatchOnPanelSlide(this.mSlideableView);
      return;
      this.mSlideOffset = 0.0F;
      return;
      label105:
      paramInt = getWidth() - paramInt - i;
      break;
      label117:
      i = getPaddingRight();
      break label48;
      label125:
      j = localLayoutParams.rightMargin;
      break label59;
      label134:
      parallaxOtherViews(this.mSlideOffset);
      break label82;
      label145:
      dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
    }
  }
  
  private boolean openPane(View paramView, int paramInt)
  {
    if (this.mFirstLayout) {}
    while (smoothSlideTo(1.0F, paramInt))
    {
      this.mPreservedOpenState = true;
      return true;
    }
    return false;
  }
  
  private void parallaxOtherViews(float paramFloat)
  {
    boolean bool = isLayoutRtlSupport();
    Object localObject = (LayoutParams)this.mSlideableView.getLayoutParams();
    if (!((LayoutParams)localObject).dimWhenOffset) {}
    int i;
    int j;
    label72:
    for (;;)
    {
      i = 0;
      int m = getChildCount();
      j = 0;
      if (j < m) {
        break;
      }
      return;
      if (!bool) {}
      for (i = ((LayoutParams)localObject).leftMargin;; i = ((LayoutParams)localObject).rightMargin)
      {
        if (i > 0) {
          break label72;
        }
        i = 1;
        break;
      }
    }
    localObject = getChildAt(j);
    int k;
    if (localObject != this.mSlideableView)
    {
      k = (int)((1.0F - this.mParallaxOffset) * this.mParallaxBy);
      this.mParallaxOffset = paramFloat;
      k -= (int)((1.0F - paramFloat) * this.mParallaxBy);
      if (bool) {
        break label151;
      }
    }
    for (;;)
    {
      ((View)localObject).offsetLeftAndRight(k);
      if (i != 0) {
        break label159;
      }
      j += 1;
      break;
      label151:
      k = -k;
    }
    label159:
    if (!bool) {}
    for (float f = 1.0F - this.mParallaxOffset;; f = this.mParallaxOffset - 1.0F)
    {
      dimChildView((View)localObject, f, this.mCoveredFadeColor);
      break;
    }
  }
  
  private static boolean viewIsOpaque(View paramView)
  {
    if (!ViewCompat.isOpaque(paramView))
    {
      if (Build.VERSION.SDK_INT < 18)
      {
        paramView = paramView.getBackground();
        if (paramView != null) {
          break label30;
        }
        return false;
      }
    }
    else {
      return true;
    }
    return false;
    label30:
    return paramView.getOpacity() == -1;
  }
  
  protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    if (!(paramView instanceof ViewGroup)) {
      if (paramBoolean) {
        break label150;
      }
    }
    label40:
    label76:
    label150:
    do
    {
      return false;
      ViewGroup localViewGroup = (ViewGroup)paramView;
      int j = paramView.getScrollX();
      int k = paramView.getScrollY();
      int i = localViewGroup.getChildCount() - 1;
      View localView;
      if (i >= 0)
      {
        localView = localViewGroup.getChildAt(i);
        if (paramInt2 + j >= localView.getLeft()) {
          break label76;
        }
      }
      while ((paramInt2 + j >= localView.getRight()) || (paramInt3 + k < localView.getTop()) || (paramInt3 + k >= localView.getBottom()) || (!canScroll(localView, true, paramInt1, paramInt2 + j - localView.getLeft(), paramInt3 + k - localView.getTop())))
      {
        i -= 1;
        break label40;
        break;
      }
      return true;
      paramInt2 = paramInt1;
      if (!isLayoutRtlSupport()) {
        paramInt2 = -paramInt1;
      }
    } while (!ViewCompat.canScrollHorizontally(paramView, paramInt2));
    return true;
  }
  
  @Deprecated
  public boolean canSlide()
  {
    return this.mCanSlide;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (!(paramLayoutParams instanceof LayoutParams)) {}
    while (!super.checkLayoutParams(paramLayoutParams)) {
      return false;
    }
    return true;
  }
  
  public boolean closePane()
  {
    return closePane(this.mSlideableView, 0);
  }
  
  public void computeScroll()
  {
    if (!this.mDragHelper.continueSettling(true)) {
      return;
    }
    if (this.mCanSlide)
    {
      ViewCompat.postInvalidateOnAnimation(this);
      return;
    }
    this.mDragHelper.abort();
  }
  
  void dispatchOnPanelClosed(View paramView)
  {
    if (this.mPanelSlideListener == null) {}
    for (;;)
    {
      sendAccessibilityEvent(32);
      return;
      this.mPanelSlideListener.onPanelClosed(paramView);
    }
  }
  
  void dispatchOnPanelOpened(View paramView)
  {
    if (this.mPanelSlideListener == null) {}
    for (;;)
    {
      sendAccessibilityEvent(32);
      return;
      this.mPanelSlideListener.onPanelOpened(paramView);
    }
  }
  
  void dispatchOnPanelSlide(View paramView)
  {
    if (this.mPanelSlideListener == null) {
      return;
    }
    this.mPanelSlideListener.onPanelSlide(paramView, this.mSlideOffset);
  }
  
  public void draw(Canvas paramCanvas)
  {
    View localView = null;
    super.draw(paramCanvas);
    Drawable localDrawable;
    if (!isLayoutRtlSupport())
    {
      localDrawable = this.mShadowDrawableLeft;
      if (getChildCount() > 1) {
        break label44;
      }
      label29:
      if (localView != null) {
        break label54;
      }
    }
    label44:
    label54:
    while (localDrawable == null)
    {
      return;
      localDrawable = this.mShadowDrawableRight;
      break;
      localView = getChildAt(1);
      break label29;
    }
    int k = localView.getTop();
    int m = localView.getBottom();
    int n = localDrawable.getIntrinsicWidth();
    int i;
    int j;
    if (!isLayoutRtlSupport())
    {
      i = localView.getLeft();
      j = i - n;
    }
    for (;;)
    {
      localDrawable.setBounds(j, k, i, m);
      localDrawable.draw(paramCanvas);
      return;
      j = localView.getRight();
      i = j + n;
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = paramCanvas.save(2);
    if (!this.mCanSlide)
    {
      if (Build.VERSION.SDK_INT >= 11) {
        break label158;
      }
      if (localLayoutParams.dimWhenOffset) {
        break label170;
      }
      label39:
      if (paramView.isDrawingCacheEnabled()) {
        break label276;
      }
    }
    for (;;)
    {
      boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
      for (;;)
      {
        paramCanvas.restoreToCount(i);
        return bool;
        if ((localLayoutParams.slideable) || (this.mSlideableView == null)) {
          break;
        }
        paramCanvas.getClipBounds(this.mTmpRect);
        if (!isLayoutRtlSupport()) {
          this.mTmpRect.right = Math.min(this.mTmpRect.right, this.mSlideableView.getLeft());
        }
        for (;;)
        {
          paramCanvas.clipRect(this.mTmpRect);
          break;
          this.mTmpRect.left = Math.max(this.mTmpRect.left, this.mSlideableView.getRight());
        }
        label158:
        bool = super.drawChild(paramCanvas, paramView, paramLong);
        continue;
        label170:
        if (this.mSlideOffset <= 0.0F) {
          break label39;
        }
        if (paramView.isDrawingCacheEnabled()) {}
        Bitmap localBitmap;
        for (;;)
        {
          localBitmap = paramView.getDrawingCache();
          if (localBitmap != null) {
            break label249;
          }
          Log.e("SlidingPaneLayout", "drawChild: child view " + paramView + " returned null drawing cache");
          bool = super.drawChild(paramCanvas, paramView, paramLong);
          break;
          paramView.setDrawingCacheEnabled(true);
        }
        label249:
        paramCanvas.drawBitmap(localBitmap, paramView.getLeft(), paramView.getTop(), localLayoutParams.dimPaint);
        bool = false;
      }
      label276:
      paramView.setDrawingCacheEnabled(false);
    }
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams();
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (!(paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams(paramLayoutParams);
    }
    return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
  }
  
  public int getCoveredFadeColor()
  {
    return this.mCoveredFadeColor;
  }
  
  public int getParallaxDistance()
  {
    return this.mParallaxBy;
  }
  
  public int getSliderFadeColor()
  {
    return this.mSliderFadeColor;
  }
  
  boolean isDimmed(View paramView)
  {
    if (paramView != null)
    {
      paramView = (LayoutParams)paramView.getLayoutParams();
      if (this.mCanSlide) {
        break label23;
      }
    }
    label23:
    while ((!paramView.dimWhenOffset) || (this.mSlideOffset <= 0.0F))
    {
      return false;
      return false;
    }
    return true;
  }
  
  public boolean isOpen()
  {
    boolean bool = false;
    if (!this.mCanSlide) {}
    for (;;)
    {
      bool = true;
      do
      {
        return bool;
      } while (this.mSlideOffset != 1.0F);
    }
  }
  
  public boolean isSlideable()
  {
    return this.mCanSlide;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mFirstLayout = true;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mFirstLayout = true;
    int j = this.mPostedRunnables.size();
    int i = 0;
    for (;;)
    {
      if (i >= j)
      {
        this.mPostedRunnables.clear();
        return;
      }
      ((DisableLayerRunnable)this.mPostedRunnables.get(i)).run();
      i += 1;
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (this.mCanSlide) {}
    while (!this.mCanSlide)
    {
      this.mDragHelper.cancel();
      return super.onInterceptTouchEvent(paramMotionEvent);
      if ((i == 0) && (getChildCount() > 1))
      {
        View localView = getChildAt(1);
        if (localView != null)
        {
          if (this.mDragHelper.isViewUnder(localView, (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())) {}
          for (boolean bool = false;; bool = true)
          {
            this.mPreservedOpenState = bool;
            break;
          }
        }
      }
    }
    if (!this.mIsUnableToDrag) {
      label105:
      if (i != 3) {
        break label128;
      }
    }
    label128:
    while (i == 1)
    {
      this.mDragHelper.cancel();
      return false;
      if (i != 0) {
        break;
      }
      break label105;
    }
    switch (i)
    {
    case 1: 
    default: 
      i = 0;
      if (!this.mDragHelper.shouldInterceptTouchEvent(paramMotionEvent)) {
        break;
      }
    }
    while (i != 0)
    {
      return true;
      this.mIsUnableToDrag = false;
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      this.mInitialMotionX = f1;
      this.mInitialMotionY = f2;
      if (!this.mDragHelper.isViewUnder(this.mSlideableView, (int)f1, (int)f2)) {}
      while (!isDimmed(this.mSlideableView))
      {
        i = 0;
        break;
      }
      i = 1;
      break;
      f2 = paramMotionEvent.getX();
      f1 = paramMotionEvent.getY();
      f2 = Math.abs(f2 - this.mInitialMotionX);
      f1 = Math.abs(f1 - this.mInitialMotionY);
      if ((f2 > this.mDragHelper.getTouchSlop()) && (f1 > f2))
      {
        this.mDragHelper.cancel();
        this.mIsUnableToDrag = true;
        return false;
      }
      i = 0;
      break;
    }
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool = isLayoutRtlSupport();
    int k;
    if (!bool)
    {
      this.mDragHelper.setEdgeTrackingEnabled(1);
      k = paramInt3 - paramInt1;
      if (bool) {
        break label107;
      }
      paramInt1 = getPaddingLeft();
      label35:
      if (bool) {
        break label115;
      }
    }
    int n;
    int m;
    int i;
    label107:
    label115:
    for (paramInt4 = getPaddingRight();; paramInt4 = getPaddingLeft())
    {
      n = getPaddingTop();
      m = getChildCount();
      if (this.mFirstLayout) {
        break label124;
      }
      i = 0;
      paramInt3 = paramInt1;
      paramInt2 = paramInt1;
      paramInt1 = paramInt3;
      if (i < m) {
        break label156;
      }
      if (this.mFirstLayout) {
        break label473;
      }
      this.mFirstLayout = false;
      return;
      this.mDragHelper.setEdgeTrackingEnabled(2);
      break;
      paramInt1 = getPaddingRight();
      break label35;
    }
    label124:
    if (!this.mCanSlide) {}
    label131:
    for (float f = 0.0F;; f = 1.0F)
    {
      this.mSlideOffset = f;
      break;
      if (!this.mPreservedOpenState) {
        break label131;
      }
    }
    label156:
    View localView = getChildAt(i);
    LayoutParams localLayoutParams;
    int i1;
    label206:
    label211:
    int j;
    if (localView.getVisibility() != 8)
    {
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
      i1 = localView.getMeasuredWidth();
      if (localLayoutParams.slideable) {
        break label290;
      }
      if (this.mCanSlide) {
        break label427;
      }
      paramInt3 = 0;
      paramInt2 = paramInt1;
      if (bool) {
        break label454;
      }
      paramInt3 = paramInt2 - paramInt3;
      j = paramInt3 + i1;
    }
    for (;;)
    {
      localView.layout(paramInt3, n, j, localView.getMeasuredHeight() + n);
      paramInt3 = localView.getWidth() + paramInt1;
      paramInt1 = paramInt2;
      paramInt2 = paramInt3;
      for (;;)
      {
        i += 1;
        paramInt3 = paramInt1;
        paramInt1 = paramInt2;
        paramInt2 = paramInt3;
        break;
        paramInt3 = paramInt2;
        paramInt2 = paramInt1;
        paramInt1 = paramInt3;
      }
      label290:
      paramInt3 = localLayoutParams.leftMargin;
      j = localLayoutParams.rightMargin;
      j = Math.min(paramInt1, k - paramInt4 - this.mOverhangSize) - paramInt2 - (paramInt3 + j);
      this.mSlideRange = j;
      if (!bool)
      {
        paramInt3 = localLayoutParams.leftMargin;
        label346:
        if (paramInt2 + paramInt3 + j + i1 / 2 > k - paramInt4) {
          break label422;
        }
      }
      label422:
      for (paramBoolean = false;; paramBoolean = true)
      {
        localLayoutParams.dimWhenOffset = paramBoolean;
        j = (int)(j * this.mSlideOffset);
        paramInt2 += paramInt3 + j;
        this.mSlideOffset = (j / this.mSlideRange);
        paramInt3 = 0;
        break;
        paramInt3 = localLayoutParams.rightMargin;
        break label346;
      }
      label427:
      if (this.mParallaxBy == 0) {
        break label206;
      }
      paramInt3 = (int)((1.0F - this.mSlideOffset) * this.mParallaxBy);
      paramInt2 = paramInt1;
      break label211;
      label454:
      j = k - paramInt2 + paramInt3;
      paramInt3 = j - i1;
    }
    label473:
    if (!this.mCanSlide) {
      paramInt1 = 0;
    }
    for (;;)
    {
      if (paramInt1 >= m) {
        label550:
        for (;;)
        {
          updateObscuredViewsVisibility(this.mSlideableView);
          break;
          if (this.mParallaxBy == 0) {}
          for (;;)
          {
            if (!((LayoutParams)this.mSlideableView.getLayoutParams()).dimWhenOffset) {
              break label550;
            }
            dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
            break;
            parallaxOtherViews(this.mSlideOffset);
          }
        }
      }
      dimChildView(getChildAt(paramInt1), 0.0F, this.mSliderFadeColor);
      paramInt1 += 1;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int k = View.MeasureSpec.getMode(paramInt1);
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getMode(paramInt2);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    int m;
    int n;
    label78:
    boolean bool1;
    int i2;
    label107:
    int i1;
    float f1;
    if (k == 1073741824)
    {
      if (j == 0) {
        break label256;
      }
      m = i;
      n = j;
      paramInt1 = paramInt2;
      i = -1;
      switch (n)
      {
      default: 
        paramInt1 = 0;
        bool1 = false;
        i2 = m - getPaddingLeft() - getPaddingRight();
        int i3 = getChildCount();
        if (i3 <= 2)
        {
          this.mSlideableView = null;
          i1 = 0;
          paramInt2 = i2;
          j = paramInt1;
          f1 = 0.0F;
          paramInt1 = paramInt2;
          if (i1 < i3) {
            break label353;
          }
          if (!bool1) {
            break label673;
          }
          label137:
          i1 = i2 - this.mOverhangSize;
          k = 0;
          if (k < i3) {
            break label682;
          }
          label156:
          setMeasuredDimension(m, getPaddingTop() + j + getPaddingBottom());
          this.mCanSlide = bool1;
          if (this.mDragHelper.getViewDragState() != 0) {
            break label1079;
          }
        }
        break;
      }
    }
    label256:
    label353:
    label454:
    label486:
    label515:
    label518:
    label530:
    label576:
    label582:
    label596:
    label610:
    label623:
    label636:
    label652:
    label658:
    label673:
    label682:
    label728:
    label730:
    label741:
    label746:
    label776:
    label824:
    label839:
    label845:
    label869:
    label915:
    label973:
    label1019:
    label1031:
    label1079:
    while (bool1)
    {
      return;
      if (!isInEditMode()) {
        throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
      }
      paramInt1 = paramInt2;
      n = j;
      m = i;
      if (k == Integer.MIN_VALUE) {
        break;
      }
      paramInt1 = paramInt2;
      n = j;
      m = i;
      if (k != 0) {
        break;
      }
      m = 300;
      paramInt1 = paramInt2;
      n = j;
      break;
      if (!isInEditMode()) {
        throw new IllegalStateException("Height must not be UNSPECIFIED");
      }
      paramInt1 = paramInt2;
      n = j;
      m = i;
      if (j != 0) {
        break;
      }
      n = Integer.MIN_VALUE;
      paramInt1 = 300;
      m = i;
      break;
      paramInt1 = paramInt1 - getPaddingTop() - getPaddingBottom();
      i = paramInt1;
      break label78;
      i = paramInt1 - getPaddingTop() - getPaddingBottom();
      paramInt1 = 0;
      break label78;
      Log.e("SlidingPaneLayout", "onMeasure: More than two child views are not supported.");
      break label107;
      View localView = getChildAt(i1);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      float f2;
      boolean bool2;
      if (localView.getVisibility() != 8)
      {
        f2 = f1;
        if (localLayoutParams.weight > 0.0F)
        {
          f2 = f1 + localLayoutParams.weight;
          if (localLayoutParams.width == 0) {
            break label576;
          }
        }
        paramInt2 = localLayoutParams.leftMargin + localLayoutParams.rightMargin;
        if (localLayoutParams.width == -2) {
          break label582;
        }
        if (localLayoutParams.width == -1) {
          break label596;
        }
        paramInt2 = View.MeasureSpec.makeMeasureSpec(localLayoutParams.width, 1073741824);
        if (localLayoutParams.height == -2) {
          break label610;
        }
        if (localLayoutParams.height == -1) {
          break label623;
        }
        k = View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, 1073741824);
        localView.measure(paramInt2, k);
        k = localView.getMeasuredWidth();
        paramInt2 = localView.getMeasuredHeight();
        if (n == Integer.MIN_VALUE) {
          break label636;
        }
        paramInt2 = j;
        paramInt1 -= k;
        if (paramInt1 < 0) {
          break label652;
        }
        bool2 = false;
        localLayoutParams.slideable = bool2;
        bool1 = bool2 | bool1;
        if (localLayoutParams.slideable) {
          break label658;
        }
        f1 = f2;
        j = paramInt2;
      }
      for (;;)
      {
        i1 += 1;
        break;
        localLayoutParams.dimWhenOffset = false;
        continue;
        f1 = f2;
        continue;
        paramInt2 = View.MeasureSpec.makeMeasureSpec(i2 - paramInt2, Integer.MIN_VALUE);
        break label454;
        paramInt2 = View.MeasureSpec.makeMeasureSpec(i2 - paramInt2, 1073741824);
        break label454;
        k = View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
        break label486;
        k = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
        break label486;
        if (paramInt2 <= j) {
          break label515;
        }
        paramInt2 = Math.min(paramInt2, i);
        break label518;
        bool2 = true;
        break label530;
        this.mSlideableView = localView;
        f1 = f2;
        j = paramInt2;
      }
      if (f1 <= 0.0F) {
        break label156;
      }
      break label137;
      localView = getChildAt(k);
      int i4;
      if (localView.getVisibility() != 8)
      {
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localView.getVisibility() != 8)
        {
          if (localLayoutParams.width == 0) {
            break label824;
          }
          paramInt2 = 0;
          if (paramInt2 != 0) {
            break label839;
          }
          n = localView.getMeasuredWidth();
          if (bool1) {
            break label845;
          }
          if (localLayoutParams.weight > 0.0F)
          {
            if (localLayoutParams.width == 0) {
              break label973;
            }
            paramInt2 = View.MeasureSpec.makeMeasureSpec(localView.getMeasuredHeight(), 1073741824);
            if (bool1) {
              break label1031;
            }
            i4 = Math.max(0, paramInt1);
            localView.measure(View.MeasureSpec.makeMeasureSpec((int)(localLayoutParams.weight * i4 / f1) + n, 1073741824), paramInt2);
          }
        }
      }
      for (;;)
      {
        k += 1;
        break;
        if (localLayoutParams.weight <= 0.0F) {
          break label728;
        }
        paramInt2 = 1;
        break label730;
        n = 0;
        break label741;
        if (localView == this.mSlideableView) {
          break label746;
        }
        if (localLayoutParams.width < 0)
        {
          if (n > i1)
          {
            if (paramInt2 != 0) {
              break label915;
            }
            paramInt2 = View.MeasureSpec.makeMeasureSpec(localView.getMeasuredHeight(), 1073741824);
          }
          for (;;)
          {
            localView.measure(View.MeasureSpec.makeMeasureSpec(i1, 1073741824), paramInt2);
            break;
            if (localLayoutParams.weight <= 0.0F) {
              break;
            }
            break label869;
            if (localLayoutParams.height != -2)
            {
              if (localLayoutParams.height != -1) {
                paramInt2 = View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, 1073741824);
              }
            }
            else
            {
              paramInt2 = View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
              continue;
            }
            paramInt2 = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
          }
          if (localLayoutParams.height != -2)
          {
            if (localLayoutParams.height == -1) {
              break label1019;
            }
            paramInt2 = View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, 1073741824);
            break label776;
          }
          paramInt2 = View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
          break label776;
          paramInt2 = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
          break label776;
          i4 = localLayoutParams.leftMargin;
          i4 = i2 - (localLayoutParams.rightMargin + i4);
          int i5 = View.MeasureSpec.makeMeasureSpec(i4, 1073741824);
          if (n != i4) {
            localView.measure(i5, paramInt2);
          }
        }
      }
    }
    this.mDragHelper.abort();
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    if (!paramParcelable.isOpen) {
      closePane();
    }
    for (;;)
    {
      this.mPreservedOpenState = paramParcelable.isOpen;
      return;
      openPane();
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    if (!isSlideable()) {}
    for (boolean bool = this.mPreservedOpenState;; bool = isOpen())
    {
      localSavedState.isOpen = bool;
      return localSavedState;
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 == paramInt3) {
      return;
    }
    this.mFirstLayout = true;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mCanSlide)
    {
      this.mDragHelper.processTouchEvent(paramMotionEvent);
      switch (paramMotionEvent.getAction() & 0xFF)
      {
      }
    }
    for (;;)
    {
      return true;
      return super.onTouchEvent(paramMotionEvent);
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      this.mInitialMotionX = f1;
      this.mInitialMotionY = f2;
      continue;
      if (isDimmed(this.mSlideableView))
      {
        f1 = paramMotionEvent.getX();
        f2 = paramMotionEvent.getY();
        float f3 = f1 - this.mInitialMotionX;
        float f4 = f2 - this.mInitialMotionY;
        int i = this.mDragHelper.getTouchSlop();
        if ((f3 * f3 + f4 * f4 < i * i) && (this.mDragHelper.isViewUnder(this.mSlideableView, (int)f1, (int)f2))) {
          closePane(this.mSlideableView, 0);
        }
      }
    }
  }
  
  public boolean openPane()
  {
    return openPane(this.mSlideableView, 0);
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    boolean bool = false;
    super.requestChildFocus(paramView1, paramView2);
    if (isInTouchMode()) {}
    while (this.mCanSlide) {
      return;
    }
    if (paramView1 != this.mSlideableView) {}
    for (;;)
    {
      this.mPreservedOpenState = bool;
      return;
      bool = true;
    }
  }
  
  void setAllChildrenVisible()
  {
    int j = getChildCount();
    int i = 0;
    if (i >= j) {
      return;
    }
    View localView = getChildAt(i);
    if (localView.getVisibility() != 4) {}
    for (;;)
    {
      i += 1;
      break;
      localView.setVisibility(0);
    }
  }
  
  public void setCoveredFadeColor(int paramInt)
  {
    this.mCoveredFadeColor = paramInt;
  }
  
  public void setPanelSlideListener(PanelSlideListener paramPanelSlideListener)
  {
    this.mPanelSlideListener = paramPanelSlideListener;
  }
  
  public void setParallaxDistance(int paramInt)
  {
    this.mParallaxBy = paramInt;
    requestLayout();
  }
  
  @Deprecated
  public void setShadowDrawable(Drawable paramDrawable)
  {
    setShadowDrawableLeft(paramDrawable);
  }
  
  public void setShadowDrawableLeft(Drawable paramDrawable)
  {
    this.mShadowDrawableLeft = paramDrawable;
  }
  
  public void setShadowDrawableRight(Drawable paramDrawable)
  {
    this.mShadowDrawableRight = paramDrawable;
  }
  
  @Deprecated
  public void setShadowResource(int paramInt)
  {
    setShadowDrawable(getResources().getDrawable(paramInt));
  }
  
  public void setShadowResourceLeft(int paramInt)
  {
    setShadowDrawableLeft(getResources().getDrawable(paramInt));
  }
  
  public void setShadowResourceRight(int paramInt)
  {
    setShadowDrawableRight(getResources().getDrawable(paramInt));
  }
  
  public void setSliderFadeColor(int paramInt)
  {
    this.mSliderFadeColor = paramInt;
  }
  
  @Deprecated
  public void smoothSlideClosed()
  {
    closePane();
  }
  
  @Deprecated
  public void smoothSlideOpen()
  {
    openPane();
  }
  
  boolean smoothSlideTo(float paramFloat, int paramInt)
  {
    LayoutParams localLayoutParams;
    if (this.mCanSlide)
    {
      boolean bool = isLayoutRtlSupport();
      localLayoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
      if (bool) {
        break label79;
      }
      paramInt = getPaddingLeft();
    }
    label79:
    int i;
    int j;
    for (paramInt = (int)(localLayoutParams.leftMargin + paramInt + this.mSlideRange * paramFloat); !this.mDragHelper.smoothSlideViewTo(this.mSlideableView, paramInt, this.mSlideableView.getTop()); paramInt = (int)(getWidth() - (i + paramInt + this.mSlideRange * paramFloat + j)))
    {
      return false;
      return false;
      paramInt = getPaddingRight();
      i = localLayoutParams.rightMargin;
      j = this.mSlideableView.getWidth();
    }
    setAllChildrenVisible();
    ViewCompat.postInvalidateOnAnimation(this);
    return true;
  }
  
  void updateObscuredViewsVisibility(View paramView)
  {
    boolean bool = isLayoutRtlSupport();
    int i;
    int j;
    label31:
    int i4;
    int i5;
    int i6;
    label53:
    int k;
    int m;
    int n;
    int i1;
    label65:
    int i2;
    if (!bool)
    {
      i = getPaddingLeft();
      if (bool) {
        break label95;
      }
      j = getWidth() - getPaddingRight();
      i4 = getPaddingTop();
      i5 = getHeight();
      i6 = getPaddingBottom();
      if (paramView != null) {
        break label103;
      }
      k = 0;
      m = 0;
      n = 0;
      i1 = 0;
      int i7 = getChildCount();
      i2 = 0;
      if (i2 < i7) {
        break label137;
      }
    }
    label95:
    label103:
    label137:
    View localView;
    do
    {
      return;
      i = getWidth() - getPaddingRight();
      break;
      j = getPaddingLeft();
      break label31;
      if (!viewIsOpaque(paramView)) {
        break label53;
      }
      i1 = paramView.getLeft();
      n = paramView.getRight();
      m = paramView.getTop();
      k = paramView.getBottom();
      break label65;
      localView = getChildAt(i2);
    } while (localView == paramView);
    label159:
    int i9;
    label191:
    int i10;
    if (!bool)
    {
      i3 = i;
      int i8 = Math.max(i3, localView.getLeft());
      i9 = Math.max(i4, localView.getTop());
      if (bool) {
        break label250;
      }
      i3 = j;
      i3 = Math.min(i3, localView.getRight());
      i10 = Math.min(i5 - i6, localView.getBottom());
      if (i8 >= i1) {
        break label256;
      }
    }
    label225:
    for (int i3 = 0;; i3 = 4)
    {
      localView.setVisibility(i3);
      i2 += 1;
      break;
      i3 = j;
      break label159;
      label250:
      i3 = i;
      break label191;
      label256:
      if ((i9 < m) || (i3 > n) || (i10 > k)) {
        break label225;
      }
    }
  }
  
  class AccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    private final Rect mTmpRect = new Rect();
    
    AccessibilityDelegate() {}
    
    private void copyNodeInfoNoChildren(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat1, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat2)
    {
      Rect localRect = this.mTmpRect;
      paramAccessibilityNodeInfoCompat2.getBoundsInParent(localRect);
      paramAccessibilityNodeInfoCompat1.setBoundsInParent(localRect);
      paramAccessibilityNodeInfoCompat2.getBoundsInScreen(localRect);
      paramAccessibilityNodeInfoCompat1.setBoundsInScreen(localRect);
      paramAccessibilityNodeInfoCompat1.setVisibleToUser(paramAccessibilityNodeInfoCompat2.isVisibleToUser());
      paramAccessibilityNodeInfoCompat1.setPackageName(paramAccessibilityNodeInfoCompat2.getPackageName());
      paramAccessibilityNodeInfoCompat1.setClassName(paramAccessibilityNodeInfoCompat2.getClassName());
      paramAccessibilityNodeInfoCompat1.setContentDescription(paramAccessibilityNodeInfoCompat2.getContentDescription());
      paramAccessibilityNodeInfoCompat1.setEnabled(paramAccessibilityNodeInfoCompat2.isEnabled());
      paramAccessibilityNodeInfoCompat1.setClickable(paramAccessibilityNodeInfoCompat2.isClickable());
      paramAccessibilityNodeInfoCompat1.setFocusable(paramAccessibilityNodeInfoCompat2.isFocusable());
      paramAccessibilityNodeInfoCompat1.setFocused(paramAccessibilityNodeInfoCompat2.isFocused());
      paramAccessibilityNodeInfoCompat1.setAccessibilityFocused(paramAccessibilityNodeInfoCompat2.isAccessibilityFocused());
      paramAccessibilityNodeInfoCompat1.setSelected(paramAccessibilityNodeInfoCompat2.isSelected());
      paramAccessibilityNodeInfoCompat1.setLongClickable(paramAccessibilityNodeInfoCompat2.isLongClickable());
      paramAccessibilityNodeInfoCompat1.addAction(paramAccessibilityNodeInfoCompat2.getActions());
      paramAccessibilityNodeInfoCompat1.setMovementGranularities(paramAccessibilityNodeInfoCompat2.getMovementGranularities());
    }
    
    public boolean filter(View paramView)
    {
      return SlidingPaneLayout.this.isDimmed(paramView);
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(SlidingPaneLayout.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain(paramAccessibilityNodeInfoCompat);
      super.onInitializeAccessibilityNodeInfo(paramView, localAccessibilityNodeInfoCompat);
      copyNodeInfoNoChildren(paramAccessibilityNodeInfoCompat, localAccessibilityNodeInfoCompat);
      localAccessibilityNodeInfoCompat.recycle();
      paramAccessibilityNodeInfoCompat.setClassName(SlidingPaneLayout.class.getName());
      paramAccessibilityNodeInfoCompat.setSource(paramView);
      paramView = ViewCompat.getParentForAccessibility(paramView);
      if (!(paramView instanceof View)) {}
      int i;
      for (;;)
      {
        int j = SlidingPaneLayout.this.getChildCount();
        i = 0;
        if (i < j) {
          break;
        }
        return;
        paramAccessibilityNodeInfoCompat.setParent((View)paramView);
      }
      paramView = SlidingPaneLayout.this.getChildAt(i);
      if (filter(paramView)) {}
      for (;;)
      {
        i += 1;
        break;
        if (paramView.getVisibility() == 0)
        {
          ViewCompat.setImportantForAccessibility(paramView, 1);
          paramAccessibilityNodeInfoCompat.addChild(paramView);
        }
      }
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      if (filter(paramView)) {
        return false;
      }
      return super.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
    }
  }
  
  private class DisableLayerRunnable
    implements Runnable
  {
    final View mChildView;
    
    DisableLayerRunnable(View paramView)
    {
      this.mChildView = paramView;
    }
    
    public void run()
    {
      if (this.mChildView.getParent() != SlidingPaneLayout.this) {}
      for (;;)
      {
        SlidingPaneLayout.this.mPostedRunnables.remove(this);
        return;
        ViewCompat.setLayerType(this.mChildView, 0, null);
        SlidingPaneLayout.this.invalidateChildRegion(this.mChildView);
      }
    }
  }
  
  private class DragHelperCallback
    extends ViewDragHelper.Callback
  {
    private DragHelperCallback() {}
    
    public int clampViewPositionHorizontal(View paramView, int paramInt1, int paramInt2)
    {
      paramView = (SlidingPaneLayout.LayoutParams)SlidingPaneLayout.this.mSlideableView.getLayoutParams();
      if (!SlidingPaneLayout.this.isLayoutRtlSupport())
      {
        paramInt2 = SlidingPaneLayout.this.getPaddingLeft();
        paramInt2 = paramView.leftMargin + paramInt2;
        i = SlidingPaneLayout.this.mSlideRange;
        return Math.min(Math.max(paramInt1, paramInt2), i + paramInt2);
      }
      paramInt2 = SlidingPaneLayout.this.getWidth();
      int i = SlidingPaneLayout.this.getPaddingRight();
      paramInt2 -= paramView.rightMargin + i + SlidingPaneLayout.this.mSlideableView.getWidth();
      i = SlidingPaneLayout.this.mSlideRange;
      return Math.max(Math.min(paramInt1, paramInt2), paramInt2 - i);
    }
    
    public int clampViewPositionVertical(View paramView, int paramInt1, int paramInt2)
    {
      return paramView.getTop();
    }
    
    public int getViewHorizontalDragRange(View paramView)
    {
      return SlidingPaneLayout.this.mSlideRange;
    }
    
    public void onEdgeDragStarted(int paramInt1, int paramInt2)
    {
      SlidingPaneLayout.this.mDragHelper.captureChildView(SlidingPaneLayout.this.mSlideableView, paramInt2);
    }
    
    public void onViewCaptured(View paramView, int paramInt)
    {
      SlidingPaneLayout.this.setAllChildrenVisible();
    }
    
    public void onViewDragStateChanged(int paramInt)
    {
      if (SlidingPaneLayout.this.mDragHelper.getViewDragState() != 0) {
        return;
      }
      if (SlidingPaneLayout.this.mSlideOffset == 0.0F)
      {
        SlidingPaneLayout.this.updateObscuredViewsVisibility(SlidingPaneLayout.this.mSlideableView);
        SlidingPaneLayout.this.dispatchOnPanelClosed(SlidingPaneLayout.this.mSlideableView);
        SlidingPaneLayout.access$502(SlidingPaneLayout.this, false);
        return;
      }
      SlidingPaneLayout.this.dispatchOnPanelOpened(SlidingPaneLayout.this.mSlideableView);
      SlidingPaneLayout.access$502(SlidingPaneLayout.this, true);
    }
    
    public void onViewPositionChanged(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      SlidingPaneLayout.this.onPanelDragged(paramInt1);
      SlidingPaneLayout.this.invalidate();
    }
    
    public void onViewReleased(View paramView, float paramFloat1, float paramFloat2)
    {
      int k = 1;
      int i = 1;
      SlidingPaneLayout.LayoutParams localLayoutParams = (SlidingPaneLayout.LayoutParams)paramView.getLayoutParams();
      int j;
      if (!SlidingPaneLayout.this.isLayoutRtlSupport())
      {
        j = SlidingPaneLayout.this.getPaddingLeft();
        j = localLayoutParams.leftMargin + j;
        if (paramFloat1 <= 0.0F) {
          break label228;
        }
      }
      for (;;)
      {
        if (i == 0)
        {
          i = j;
          if (paramFloat1 == 0.0F)
          {
            i = j;
            if (SlidingPaneLayout.this.mSlideOffset <= 0.5F) {}
          }
        }
        else
        {
          i = j + SlidingPaneLayout.this.mSlideRange;
        }
        SlidingPaneLayout.this.mDragHelper.settleCapturedViewAt(i, paramView.getTop());
        SlidingPaneLayout.this.invalidate();
        return;
        i = SlidingPaneLayout.this.getPaddingRight();
        j = localLayoutParams.rightMargin + i;
        if (paramFloat1 < 0.0F) {}
        for (i = k;; i = 0)
        {
          if (i == 0)
          {
            i = j;
            if (paramFloat1 == 0.0F)
            {
              i = j;
              if (SlidingPaneLayout.this.mSlideOffset <= 0.5F) {}
            }
          }
          else
          {
            i = j + SlidingPaneLayout.this.mSlideRange;
          }
          j = SlidingPaneLayout.this.mSlideableView.getWidth();
          i = SlidingPaneLayout.this.getWidth() - i - j;
          break;
        }
        label228:
        i = 0;
      }
    }
    
    public boolean tryCaptureView(View paramView, int paramInt)
    {
      if (!SlidingPaneLayout.this.mIsUnableToDrag) {
        return ((SlidingPaneLayout.LayoutParams)paramView.getLayoutParams()).slideable;
      }
      return false;
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    private static final int[] ATTRS = { 16843137 };
    Paint dimPaint;
    boolean dimWhenOffset;
    boolean slideable;
    public float weight = 0.0F;
    
    public LayoutParams()
    {
      super(-1);
    }
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, ATTRS);
      this.weight = paramContext.getFloat(0, 0.0F);
      paramContext.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.weight = paramLayoutParams.weight;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
  }
  
  public static abstract interface PanelSlideListener
  {
    public abstract void onPanelClosed(View paramView);
    
    public abstract void onPanelOpened(View paramView);
    
    public abstract void onPanelSlide(View paramView, float paramFloat);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public SlidingPaneLayout.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new SlidingPaneLayout.SavedState(paramAnonymousParcel, null);
      }
      
      public SlidingPaneLayout.SavedState[] newArray(int paramAnonymousInt)
      {
        return new SlidingPaneLayout.SavedState[paramAnonymousInt];
      }
    };
    boolean isOpen;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      if (paramParcel.readInt() == 0) {}
      for (;;)
      {
        this.isOpen = bool;
        return;
        bool = true;
      }
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 0;
      super.writeToParcel(paramParcel, paramInt);
      if (!this.isOpen) {}
      for (paramInt = i;; paramInt = 1)
      {
        paramParcel.writeInt(paramInt);
        return;
      }
    }
  }
  
  public static class SimplePanelSlideListener
    implements SlidingPaneLayout.PanelSlideListener
  {
    public void onPanelClosed(View paramView) {}
    
    public void onPanelOpened(View paramView) {}
    
    public void onPanelSlide(View paramView, float paramFloat) {}
  }
  
  static abstract interface SlidingPanelLayoutImpl
  {
    public abstract void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView);
  }
  
  static class SlidingPanelLayoutImplBase
    implements SlidingPaneLayout.SlidingPanelLayoutImpl
  {
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      ViewCompat.postInvalidateOnAnimation(paramSlidingPaneLayout, paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
    }
  }
  
  static class SlidingPanelLayoutImplJB
    extends SlidingPaneLayout.SlidingPanelLayoutImplBase
  {
    private Method mGetDisplayList;
    private Field mRecreateDisplayList;
    
    SlidingPanelLayoutImplJB()
    {
      try
      {
        this.mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class[])null);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;)
        {
          try
          {
            this.mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
            this.mRecreateDisplayList.setAccessible(true);
            return;
          }
          catch (NoSuchFieldException localNoSuchFieldException)
          {
            Log.e("SlidingPaneLayout", "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", localNoSuchFieldException);
          }
          localNoSuchMethodException = localNoSuchMethodException;
          Log.e("SlidingPaneLayout", "Couldn't fetch getDisplayList method; dimming won't work right.", localNoSuchMethodException);
        }
      }
    }
    
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      if (this.mGetDisplayList == null) {}
      while (this.mRecreateDisplayList == null)
      {
        paramView.invalidate();
        return;
      }
      try
      {
        this.mRecreateDisplayList.setBoolean(paramView, true);
        this.mGetDisplayList.invoke(paramView, (Object[])null);
        super.invalidateChildRegion(paramSlidingPaneLayout, paramView);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.e("SlidingPaneLayout", "Error refreshing display list state", localException);
        }
      }
    }
  }
  
  static class SlidingPanelLayoutImplJBMR1
    extends SlidingPaneLayout.SlidingPanelLayoutImplBase
  {
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      ViewCompat.setLayerPaint(paramView, ((SlidingPaneLayout.LayoutParams)paramView.getLayoutParams()).dimPaint);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/widget/SlidingPaneLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */