package android.support.v4.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewPager
  extends ViewGroup
{
  private static final int CLOSE_ENOUGH = 2;
  private static final Comparator<ItemInfo> COMPARATOR = new Comparator()
  {
    public int compare(ViewPager.ItemInfo paramAnonymousItemInfo1, ViewPager.ItemInfo paramAnonymousItemInfo2)
    {
      return paramAnonymousItemInfo1.position - paramAnonymousItemInfo2.position;
    }
  };
  private static final boolean DEBUG = false;
  private static final int DEFAULT_GUTTER_SIZE = 16;
  private static final int DEFAULT_OFFSCREEN_PAGES = 1;
  private static final int DRAW_ORDER_DEFAULT = 0;
  private static final int DRAW_ORDER_FORWARD = 1;
  private static final int DRAW_ORDER_REVERSE = 2;
  private static final int INVALID_POINTER = -1;
  private static final int[] LAYOUT_ATTRS = { 16842931 };
  private static final int MAX_SETTLE_DURATION = 600;
  private static final int MIN_DISTANCE_FOR_FLING = 25;
  private static final int MIN_FLING_VELOCITY = 400;
  public static final int SCROLL_STATE_DRAGGING = 1;
  public static final int SCROLL_STATE_IDLE = 0;
  public static final int SCROLL_STATE_SETTLING = 2;
  private static final String TAG = "ViewPager";
  private static final boolean USE_CACHE = false;
  private static final Interpolator sInterpolator = new Interpolator()
  {
    public float getInterpolation(float paramAnonymousFloat)
    {
      paramAnonymousFloat -= 1.0F;
      return paramAnonymousFloat * (paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat) + 1.0F;
    }
  };
  private static final ViewPositionComparator sPositionComparator = new ViewPositionComparator();
  private int mActivePointerId = -1;
  private PagerAdapter mAdapter;
  private OnAdapterChangeListener mAdapterChangeListener;
  private int mBottomPageBounds;
  private boolean mCalledSuper;
  private int mChildHeightMeasureSpec;
  private int mChildWidthMeasureSpec;
  private int mCloseEnough;
  private int mCurItem;
  private int mDecorChildCount;
  private int mDefaultGutterSize;
  private int mDrawingOrder;
  private ArrayList<View> mDrawingOrderedChildren;
  private final Runnable mEndScrollRunnable = new Runnable()
  {
    public void run()
    {
      ViewPager.this.setScrollState(0);
      ViewPager.this.populate();
    }
  };
  private int mExpectedAdapterCount;
  private long mFakeDragBeginTime;
  private boolean mFakeDragging;
  private boolean mFirstLayout = true;
  private float mFirstOffset = -3.4028235E38F;
  private int mFlingDistance;
  private int mGutterSize;
  private boolean mIgnoreGutter;
  private boolean mInLayout;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private OnPageChangeListener mInternalPageChangeListener;
  private boolean mIsBeingDragged;
  private boolean mIsUnableToDrag;
  private final ArrayList<ItemInfo> mItems = new ArrayList();
  private float mLastMotionX;
  private float mLastMotionY;
  private float mLastOffset = Float.MAX_VALUE;
  private EdgeEffectCompat mLeftEdge;
  private Drawable mMarginDrawable;
  private int mMaximumVelocity;
  private int mMinimumVelocity;
  private boolean mNeedCalculatePageOffsets = false;
  private PagerObserver mObserver;
  private int mOffscreenPageLimit = 1;
  private OnPageChangeListener mOnPageChangeListener;
  private int mPageMargin;
  private PageTransformer mPageTransformer;
  private boolean mPopulatePending;
  private Parcelable mRestoredAdapterState = null;
  private ClassLoader mRestoredClassLoader = null;
  private int mRestoredCurItem = -1;
  private EdgeEffectCompat mRightEdge;
  private int mScrollState = 0;
  private Scroller mScroller;
  private boolean mScrollingCacheEnabled;
  private Method mSetChildrenDrawingOrderEnabled;
  private final ItemInfo mTempItem = new ItemInfo();
  private final Rect mTempRect = new Rect();
  private int mTopPageBounds;
  private int mTouchSlop;
  private VelocityTracker mVelocityTracker;
  
  public ViewPager(Context paramContext)
  {
    super(paramContext);
    initViewPager();
  }
  
  public ViewPager(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViewPager();
  }
  
  private void calculatePageOffsets(ItemInfo paramItemInfo1, int paramInt, ItemInfo paramItemInfo2)
  {
    int k = this.mAdapter.getCount();
    int i = getClientWidth();
    float f2;
    label27:
    int m;
    float f3;
    if (i <= 0)
    {
      f2 = 0.0F;
      if (paramItemInfo2 != null) {
        break label159;
      }
      m = this.mItems.size();
      f3 = paramItemInfo1.offset;
      i = paramItemInfo1.position - 1;
      if (paramItemInfo1.position == 0) {
        break label507;
      }
      f1 = -3.4028235E38F;
      label61:
      this.mFirstOffset = f1;
      if (paramItemInfo1.position == k - 1) {
        break label516;
      }
    }
    int j;
    label159:
    label210:
    label237:
    label305:
    label339:
    label384:
    label450:
    label507:
    label516:
    for (float f1 = Float.MAX_VALUE;; f1 = paramItemInfo1.offset + paramItemInfo1.widthFactor - 1.0F)
    {
      this.mLastOffset = f1;
      j = paramInt - 1;
      f1 = f3;
      if (j >= 0) {
        break label532;
      }
      f1 = paramItemInfo1.offset + paramItemInfo1.widthFactor + f2;
      j = paramItemInfo1.position + 1;
      i = paramInt + 1;
      paramInt = j;
      if (i < m) {
        break label629;
      }
      this.mNeedCalculatePageOffsets = false;
      return;
      f2 = this.mPageMargin / i;
      break;
      i = paramItemInfo2.position;
      if (i >= paramItemInfo1.position)
      {
        if (i <= paramItemInfo1.position) {
          break label27;
        }
        j = this.mItems.size();
        f1 = paramItemInfo2.offset;
        j -= 1;
        i -= 1;
        if ((i >= paramItemInfo1.position) && (j >= 0))
        {
          paramItemInfo2 = (ItemInfo)this.mItems.get(j);
          if (i < paramItemInfo2.position) {
            break label450;
          }
        }
      }
      for (;;)
      {
        if (i <= paramItemInfo2.position)
        {
          f1 -= paramItemInfo2.widthFactor + f2;
          paramItemInfo2.offset = f1;
          i -= 1;
          break label210;
          break;
          f1 = paramItemInfo2.offset + paramItemInfo2.widthFactor + f2;
          j = 0;
          i += 1;
          if ((i <= paramItemInfo1.position) && (j < this.mItems.size()))
          {
            paramItemInfo2 = (ItemInfo)this.mItems.get(j);
            if (i > paramItemInfo2.position) {
              break label384;
            }
          }
          for (;;)
          {
            if (i >= paramItemInfo2.position)
            {
              paramItemInfo2.offset = f1;
              f1 += paramItemInfo2.widthFactor + f2;
              i += 1;
              break label305;
              break;
              if (j >= this.mItems.size() - 1) {
                continue;
              }
              j += 1;
              paramItemInfo2 = (ItemInfo)this.mItems.get(j);
              break label339;
            }
            f3 = this.mAdapter.getPageWidth(i);
            i += 1;
            f1 = f3 + f2 + f1;
          }
          if (j <= 0) {
            continue;
          }
          j -= 1;
          paramItemInfo2 = (ItemInfo)this.mItems.get(j);
          break label237;
        }
        f3 = this.mAdapter.getPageWidth(i);
        i -= 1;
        f1 -= f3 + f2;
      }
      f1 = paramItemInfo1.offset;
      break label61;
    }
    label532:
    paramItemInfo2 = (ItemInfo)this.mItems.get(j);
    label545:
    if (i <= paramItemInfo2.position)
    {
      f1 -= paramItemInfo2.widthFactor + f2;
      paramItemInfo2.offset = f1;
      if (paramItemInfo2.position == 0) {
        break label620;
      }
    }
    for (;;)
    {
      i -= 1;
      j -= 1;
      break;
      f1 -= this.mAdapter.getPageWidth(i) + f2;
      i -= 1;
      break label545;
      label620:
      this.mFirstOffset = f1;
    }
    label629:
    paramItemInfo1 = (ItemInfo)this.mItems.get(i);
    label642:
    if (paramInt >= paramItemInfo1.position) {
      if (paramItemInfo1.position == k - 1) {
        break label715;
      }
    }
    for (;;)
    {
      paramItemInfo1.offset = f1;
      f1 += paramItemInfo1.widthFactor + f2;
      paramInt += 1;
      i += 1;
      break;
      f1 = this.mAdapter.getPageWidth(paramInt) + f2 + f1;
      paramInt += 1;
      break label642;
      label715:
      this.mLastOffset = (paramItemInfo1.widthFactor + f1 - 1.0F);
    }
  }
  
  private void completeScroll(boolean paramBoolean)
  {
    int i;
    if (this.mScrollState != 2)
    {
      i = 0;
      if (i != 0) {
        break label48;
      }
    }
    int j;
    for (;;)
    {
      this.mPopulatePending = false;
      int k = 0;
      j = i;
      i = k;
      if (i < this.mItems.size()) {
        break label116;
      }
      if (j != 0) {
        break label155;
      }
      return;
      i = 1;
      break;
      label48:
      setScrollingCacheEnabled(false);
      this.mScroller.abortAnimation();
      j = getScrollX();
      k = getScrollY();
      int m = this.mScroller.getCurrX();
      int n = this.mScroller.getCurrY();
      if (j != m) {}
      while (k != n)
      {
        scrollTo(m, n);
        break;
      }
    }
    label116:
    ItemInfo localItemInfo = (ItemInfo)this.mItems.get(i);
    if (!localItemInfo.scrolling) {}
    for (;;)
    {
      i += 1;
      break;
      localItemInfo.scrolling = false;
      j = 1;
    }
    label155:
    if (!paramBoolean)
    {
      this.mEndScrollRunnable.run();
      return;
    }
    ViewCompat.postOnAnimation(this, this.mEndScrollRunnable);
  }
  
  private int determineTargetPage(int paramInt1, float paramFloat, int paramInt2, int paramInt3)
  {
    if (Math.abs(paramInt3) <= this.mFlingDistance) {
      if (paramInt1 >= this.mCurItem) {
        break label74;
      }
    }
    label74:
    for (float f = 0.6F;; f = 0.4F)
    {
      paramInt3 = (int)(f + (paramInt1 + paramFloat));
      for (;;)
      {
        if (this.mItems.size() > 0) {
          break label82;
        }
        return paramInt3;
        if (Math.abs(paramInt2) <= this.mMinimumVelocity) {
          break;
        }
        paramInt3 = paramInt1;
        if (paramInt2 <= 0) {
          paramInt3 = paramInt1 + 1;
        }
      }
    }
    label82:
    ItemInfo localItemInfo1 = (ItemInfo)this.mItems.get(0);
    ItemInfo localItemInfo2 = (ItemInfo)this.mItems.get(this.mItems.size() - 1);
    return Math.max(localItemInfo1.position, Math.min(paramInt3, localItemInfo2.position));
  }
  
  private void enableLayers(boolean paramBoolean)
  {
    int k = getChildCount();
    int i = 0;
    if (i >= k) {
      return;
    }
    if (!paramBoolean) {}
    for (int j = 0;; j = 2)
    {
      ViewCompat.setLayerType(getChildAt(i), j, null);
      i += 1;
      break;
    }
  }
  
  private void endDrag()
  {
    this.mIsBeingDragged = false;
    this.mIsUnableToDrag = false;
    if (this.mVelocityTracker == null) {
      return;
    }
    this.mVelocityTracker.recycle();
    this.mVelocityTracker = null;
  }
  
  private Rect getChildRectInPagerCoordinates(Rect paramRect, View paramView)
  {
    if (paramRect != null)
    {
      if (paramView == null) {
        break label65;
      }
      paramRect.left = paramView.getLeft();
      paramRect.right = paramView.getRight();
      paramRect.top = paramView.getTop();
      paramRect.bottom = paramView.getBottom();
    }
    for (paramView = paramView.getParent();; paramView = paramView.getParent())
    {
      if (!(paramView instanceof ViewGroup)) {}
      label65:
      while (paramView == this)
      {
        return paramRect;
        paramRect = new Rect();
        break;
        paramRect.set(0, 0, 0, 0);
        return paramRect;
      }
      paramView = (ViewGroup)paramView;
      paramRect.left += paramView.getLeft();
      paramRect.right += paramView.getRight();
      paramRect.top += paramView.getTop();
      paramRect.bottom += paramView.getBottom();
    }
  }
  
  private int getClientWidth()
  {
    return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
  }
  
  private ItemInfo infoForCurrentScrollPosition()
  {
    int i = getClientWidth();
    float f1;
    if (i <= 0)
    {
      f1 = 0.0F;
      if (i > 0) {
        break label65;
      }
    }
    float f4;
    float f3;
    int k;
    Object localObject;
    label65:
    for (float f2 = 0.0F;; f2 = this.mPageMargin / i)
    {
      j = 1;
      f4 = 0.0F;
      f3 = 0.0F;
      k = -1;
      localObject = null;
      i = 0;
      if (i < this.mItems.size()) {
        break label78;
      }
      return (ItemInfo)localObject;
      f1 = getScrollX() / i;
      break;
    }
    label78:
    ItemInfo localItemInfo = (ItemInfo)this.mItems.get(i);
    if (j != 0)
    {
      label97:
      f3 = localItemInfo.offset;
      f4 = localItemInfo.widthFactor;
      if (j == 0) {
        break label242;
      }
      label115:
      if (f1 >= f4 + f3 + f2) {
        break label251;
      }
    }
    label242:
    label251:
    for (int j = 1;; j = 0)
    {
      if ((j != 0) || (i == this.mItems.size() - 1)) {
        break label257;
      }
      k = localItemInfo.position;
      f4 = localItemInfo.widthFactor;
      j = 0;
      i += 1;
      localObject = localItemInfo;
      break;
      if (localItemInfo.position == k + 1) {
        break label97;
      }
      localItemInfo = this.mTempItem;
      localItemInfo.offset = (f4 + f3 + f2);
      localItemInfo.position = (k + 1);
      localItemInfo.widthFactor = this.mAdapter.getPageWidth(localItemInfo.position);
      i -= 1;
      break label97;
      if (f1 >= f3) {
        break label115;
      }
      return (ItemInfo)localObject;
    }
    label257:
    return localItemInfo;
  }
  
  private boolean isGutterDrag(float paramFloat1, float paramFloat2)
  {
    boolean bool2 = false;
    if (paramFloat1 < this.mGutterSize) {
      if (paramFloat2 <= 0.0F) {
        break label60;
      }
    }
    label60:
    for (int i = 1;; i = 0)
    {
      boolean bool1;
      if (i == 0)
      {
        bool1 = bool2;
        if (paramFloat1 > getWidth() - this.mGutterSize)
        {
          bool1 = bool2;
          if (paramFloat2 >= 0.0F) {}
        }
      }
      else
      {
        bool1 = true;
      }
      return bool1;
    }
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
      this.mLastMotionX = MotionEventCompat.getX(paramMotionEvent, i);
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, i);
      if (this.mVelocityTracker == null) {
        break;
      }
      this.mVelocityTracker.clear();
      return;
      i = 1;
    }
  }
  
  private boolean pageScrolled(int paramInt)
  {
    if (this.mItems.size() != 0)
    {
      ItemInfo localItemInfo = infoForCurrentScrollPosition();
      int j = getClientWidth();
      int k = this.mPageMargin;
      float f = this.mPageMargin / j;
      int i = localItemInfo.position;
      f = (paramInt / j - localItemInfo.offset) / (localItemInfo.widthFactor + f);
      paramInt = (int)((k + j) * f);
      this.mCalledSuper = false;
      onPageScrolled(i, f, paramInt);
      if (this.mCalledSuper) {
        return true;
      }
    }
    else
    {
      this.mCalledSuper = false;
      onPageScrolled(0, 0.0F, 0);
      if (this.mCalledSuper) {
        return false;
      }
      throw new IllegalStateException("onPageScrolled did not call superclass implementation");
    }
    throw new IllegalStateException("onPageScrolled did not call superclass implementation");
  }
  
  private boolean performDrag(float paramFloat)
  {
    int j = 1;
    boolean bool2 = false;
    boolean bool1 = false;
    float f1 = this.mLastMotionX;
    this.mLastMotionX = paramFloat;
    float f2 = getScrollX() + (f1 - paramFloat);
    int k = getClientWidth();
    paramFloat = k * this.mFirstOffset;
    f1 = k;
    float f3 = this.mLastOffset;
    ItemInfo localItemInfo1 = (ItemInfo)this.mItems.get(0);
    ItemInfo localItemInfo2 = (ItemInfo)this.mItems.get(this.mItems.size() - 1);
    int i;
    if (localItemInfo1.position == 0)
    {
      i = 1;
      if (localItemInfo2.position != this.mAdapter.getCount() - 1) {
        break label182;
      }
      f1 *= f3;
      label121:
      if (f2 >= paramFloat) {
        break label220;
      }
      if (i != 0) {
        break label198;
      }
    }
    for (;;)
    {
      this.mLastMotionX += paramFloat - (int)paramFloat;
      scrollTo((int)paramFloat, getScrollY());
      pageScrolled((int)paramFloat);
      return bool1;
      paramFloat = localItemInfo1.offset * k;
      i = 0;
      break;
      label182:
      f1 = localItemInfo2.offset * k;
      j = 0;
      break label121;
      label198:
      bool1 = this.mLeftEdge.onPull(Math.abs(paramFloat - f2) / k);
    }
    label220:
    if (f2 > f1) {
      if (j != 0) {
        break label245;
      }
    }
    label245:
    for (bool1 = bool2;; bool1 = this.mRightEdge.onPull(Math.abs(f2 - f1) / k))
    {
      paramFloat = f1;
      break;
      paramFloat = f2;
      break;
    }
  }
  
  private void recomputeScrollPosition(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    label4:
    ItemInfo localItemInfo;
    if (paramInt2 <= 0)
    {
      break label47;
      localItemInfo = infoForPosition(this.mCurItem);
      if (localItemInfo != null) {
        break label186;
      }
    }
    label47:
    label186:
    for (float f = 0.0F;; f = Math.min(localItemInfo.offset, this.mLastOffset))
    {
      paramInt1 = (int)(f * (paramInt1 - getPaddingLeft() - getPaddingRight()));
      if (paramInt1 != getScrollX()) {
        break label203;
      }
      return;
      if (this.mItems.isEmpty()) {
        break label4;
      }
      int i = getPaddingLeft();
      int j = getPaddingRight();
      int k = getPaddingLeft();
      int m = getPaddingRight();
      f = getScrollX() / (paramInt2 - k - m + paramInt4);
      paramInt2 = (int)((paramInt1 - i - j + paramInt3) * f);
      scrollTo(paramInt2, getScrollY());
      if (this.mScroller.isFinished()) {
        break;
      }
      paramInt3 = this.mScroller.getDuration();
      paramInt4 = this.mScroller.timePassed();
      localItemInfo = infoForPosition(this.mCurItem);
      this.mScroller.startScroll(paramInt2, 0, (int)(localItemInfo.offset * paramInt1), 0, paramInt3 - paramInt4);
      return;
    }
    label203:
    completeScroll(false);
    scrollTo(paramInt1, getScrollY());
  }
  
  private void removeNonDecorViews()
  {
    int i = 0;
    if (i >= getChildCount()) {
      return;
    }
    if (((LayoutParams)getChildAt(i).getLayoutParams()).isDecor) {}
    for (;;)
    {
      i += 1;
      break;
      removeViewAt(i);
      i -= 1;
    }
  }
  
  private void requestParentDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    ViewParent localViewParent = getParent();
    if (localViewParent == null) {
      return;
    }
    localViewParent.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  private void scrollToItem(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2)
  {
    ItemInfo localItemInfo = infoForPosition(paramInt1);
    int i;
    if (localItemInfo == null)
    {
      i = 0;
      if (paramBoolean1) {
        break label84;
      }
      if (paramBoolean2) {
        break label140;
      }
      label24:
      if (paramBoolean2) {
        break label160;
      }
    }
    for (;;)
    {
      completeScroll(false);
      scrollTo(i, 0);
      pageScrolled(i);
      for (;;)
      {
        return;
        float f = getClientWidth();
        i = (int)(Math.max(this.mFirstOffset, Math.min(localItemInfo.offset, this.mLastOffset)) * f);
        break;
        label84:
        smoothScrollTo(i, 0, paramInt2);
        if (!paramBoolean2) {}
        while ((paramBoolean2) && (this.mInternalPageChangeListener != null))
        {
          this.mInternalPageChangeListener.onPageSelected(paramInt1);
          return;
          if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageSelected(paramInt1);
          }
        }
      }
      label140:
      if (this.mOnPageChangeListener == null) {
        break label24;
      }
      this.mOnPageChangeListener.onPageSelected(paramInt1);
      break label24;
      label160:
      if (this.mInternalPageChangeListener != null) {
        this.mInternalPageChangeListener.onPageSelected(paramInt1);
      }
    }
  }
  
  private void setScrollState(int paramInt)
  {
    boolean bool = false;
    if (this.mScrollState != paramInt)
    {
      this.mScrollState = paramInt;
      if (this.mPageTransformer == null) {
        if (this.mOnPageChangeListener != null) {
          break label48;
        }
      }
    }
    else
    {
      return;
    }
    if (paramInt == 0) {}
    for (;;)
    {
      enableLayers(bool);
      break;
      bool = true;
    }
    label48:
    this.mOnPageChangeListener.onPageScrollStateChanged(paramInt);
  }
  
  private void setScrollingCacheEnabled(boolean paramBoolean)
  {
    if (this.mScrollingCacheEnabled == paramBoolean) {
      return;
    }
    this.mScrollingCacheEnabled = paramBoolean;
  }
  
  private void sortChildDrawingOrder()
  {
    int i = 0;
    if (this.mDrawingOrder == 0) {
      return;
    }
    int j;
    if (this.mDrawingOrderedChildren != null)
    {
      this.mDrawingOrderedChildren.clear();
      j = getChildCount();
    }
    for (;;)
    {
      if (i >= j)
      {
        Collections.sort(this.mDrawingOrderedChildren, sPositionComparator);
        return;
        this.mDrawingOrderedChildren = new ArrayList();
        break;
      }
      View localView = getChildAt(i);
      this.mDrawingOrderedChildren.add(localView);
      i += 1;
    }
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = paramArrayList.size();
    int k = getDescendantFocusability();
    if (k == 393216)
    {
      if (k == 262144) {
        break label120;
      }
      label31:
      if (!isFocusable()) {
        break label130;
      }
      if ((paramInt2 & 0x1) == 1) {
        break label131;
      }
    }
    for (;;)
    {
      if (paramArrayList == null)
      {
        return;
        label50:
        View localView = getChildAt(i);
        if (localView.getVisibility() != 0) {}
        for (;;)
        {
          i += 1;
          if (i < getChildCount()) {
            break label50;
          }
          break;
          ItemInfo localItemInfo = infoForChild(localView);
          if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem)) {
            localView.addFocusables(paramArrayList, paramInt1, paramInt2);
          }
        }
        label120:
        if (j == paramArrayList.size()) {
          break label31;
        }
        return;
        label130:
        return;
        label131:
        if ((isInTouchMode()) && (!isFocusableInTouchMode())) {
          return;
        }
      }
    }
    paramArrayList.add(this);
  }
  
  ItemInfo addNewItem(int paramInt1, int paramInt2)
  {
    ItemInfo localItemInfo = new ItemInfo();
    localItemInfo.position = paramInt1;
    localItemInfo.object = this.mAdapter.instantiateItem(this, paramInt1);
    localItemInfo.widthFactor = this.mAdapter.getPageWidth(paramInt1);
    if (paramInt2 < 0) {}
    while (paramInt2 >= this.mItems.size())
    {
      this.mItems.add(localItemInfo);
      return localItemInfo;
    }
    this.mItems.add(paramInt2, localItemInfo);
    return localItemInfo;
  }
  
  public void addTouchables(ArrayList<View> paramArrayList)
  {
    int i = 0;
    if (i >= getChildCount()) {
      return;
    }
    View localView = getChildAt(i);
    if (localView.getVisibility() != 0) {}
    for (;;)
    {
      i += 1;
      break;
      ItemInfo localItemInfo = infoForChild(localView);
      if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem)) {
        localView.addTouchables(paramArrayList);
      }
    }
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (checkLayoutParams(paramLayoutParams)) {}
    LayoutParams localLayoutParams;
    for (;;)
    {
      localLayoutParams = (LayoutParams)paramLayoutParams;
      localLayoutParams.isDecor |= paramView instanceof Decor;
      if (this.mInLayout) {
        break;
      }
      super.addView(paramView, paramInt, paramLayoutParams);
      return;
      paramLayoutParams = generateLayoutParams(paramLayoutParams);
    }
    if (localLayoutParams == null) {}
    while (!localLayoutParams.isDecor)
    {
      localLayoutParams.needsMeasure = true;
      addViewInLayout(paramView, paramInt, paramLayoutParams);
      return;
    }
    throw new IllegalStateException("Cannot add pager decor view during layout");
  }
  
  public boolean arrowScroll(int paramInt)
  {
    Object localObject1 = findFocus();
    Object localObject2;
    label34:
    label40:
    boolean bool;
    if (localObject1 != this)
    {
      if (localObject1 != null) {
        break label60;
      }
      localObject2 = FocusFinder.getInstance().findNextFocus(this, (View)localObject1, paramInt);
      if (localObject2 != null) {
        break label213;
      }
      if (paramInt != 17) {
        break label352;
      }
      bool = pageLeft();
    }
    for (;;)
    {
      if (bool) {
        break label383;
      }
      return bool;
      localObject1 = null;
      break;
      label60:
      localObject2 = ((View)localObject1).getParent();
      label67:
      if (!(localObject2 instanceof ViewGroup)) {}
      for (int i = 0;; i = 1)
      {
        if (i == 0) {
          break label107;
        }
        break;
        if (localObject2 != this)
        {
          localObject2 = ((ViewParent)localObject2).getParent();
          break label67;
        }
      }
      label107:
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append(localObject1.getClass().getSimpleName());
      for (localObject1 = ((View)localObject1).getParent();; localObject1 = ((ViewParent)localObject1).getParent())
      {
        if (!(localObject1 instanceof ViewGroup))
        {
          Log.e("ViewPager", "arrowScroll tried to find focus based on non-child current focused view " + ((StringBuilder)localObject2).toString());
          localObject1 = null;
          break;
        }
        ((StringBuilder)localObject2).append(" => ").append(localObject1.getClass().getSimpleName());
      }
      label213:
      if (localObject2 == localObject1) {
        break label34;
      }
      if (paramInt != 17)
      {
        if (paramInt != 66) {
          bool = false;
        }
      }
      else
      {
        i = getChildRectInPagerCoordinates(this.mTempRect, (View)localObject2).left;
        j = getChildRectInPagerCoordinates(this.mTempRect, (View)localObject1).left;
        if (localObject1 == null) {}
        while (i < j)
        {
          bool = ((View)localObject2).requestFocus();
          break;
        }
        bool = pageLeft();
        continue;
      }
      i = getChildRectInPagerCoordinates(this.mTempRect, (View)localObject2).left;
      int j = getChildRectInPagerCoordinates(this.mTempRect, (View)localObject1).left;
      if (localObject1 == null) {}
      while (i > j)
      {
        bool = ((View)localObject2).requestFocus();
        break;
      }
      bool = pageRight();
      continue;
      label352:
      if (paramInt == 1) {
        break label40;
      }
      if (paramInt == 66) {}
      while (paramInt == 2)
      {
        bool = pageRight();
        break;
      }
      bool = false;
    }
    label383:
    playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
    return bool;
  }
  
  public boolean beginFakeDrag()
  {
    if (!this.mIsBeingDragged)
    {
      this.mFakeDragging = true;
      setScrollState(1);
      this.mLastMotionX = 0.0F;
      this.mInitialMotionX = 0.0F;
      if (this.mVelocityTracker == null) {
        break label76;
      }
      this.mVelocityTracker.clear();
    }
    for (;;)
    {
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 0, 0.0F, 0.0F, 0);
      this.mVelocityTracker.addMovement(localMotionEvent);
      localMotionEvent.recycle();
      this.mFakeDragBeginTime = l;
      return true;
      return false;
      label76:
      this.mVelocityTracker = VelocityTracker.obtain();
    }
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
    while (!ViewCompat.canScrollHorizontally(paramView, -paramInt1))
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
    }
    return true;
  }
  
  public boolean canScrollHorizontally(int paramInt)
  {
    int i;
    int j;
    if (this.mAdapter != null)
    {
      i = getClientWidth();
      j = getScrollX();
      if (paramInt >= 0)
      {
        if (paramInt > 0) {
          break label45;
        }
        return false;
      }
    }
    else
    {
      return false;
    }
    if (j <= (int)(i * this.mFirstOffset)) {
      return false;
    }
    return true;
    label45:
    return j < (int)(i * this.mLastOffset);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (!(paramLayoutParams instanceof LayoutParams)) {}
    while (!super.checkLayoutParams(paramLayoutParams)) {
      return false;
    }
    return true;
  }
  
  public void computeScroll()
  {
    if (this.mScroller.isFinished()) {}
    while (!this.mScroller.computeScrollOffset())
    {
      completeScroll(true);
      return;
    }
    int i = getScrollX();
    int j = getScrollY();
    int k = this.mScroller.getCurrX();
    int m = this.mScroller.getCurrY();
    if (i != k)
    {
      scrollTo(k, m);
      if (!pageScrolled(k)) {
        break label87;
      }
    }
    for (;;)
    {
      ViewCompat.postInvalidateOnAnimation(this);
      return;
      if (j != m) {
        break;
      }
      continue;
      label87:
      this.mScroller.abortAnimation();
      scrollTo(0, m);
    }
  }
  
  void dataSetChanged()
  {
    int i2 = this.mAdapter.getCount();
    this.mExpectedAdapterCount = i2;
    int i;
    label35:
    int n;
    int m;
    if (this.mItems.size() >= this.mOffscreenPageLimit * 2 + 1)
    {
      i = 0;
      j = this.mCurItem;
      k = 0;
      n = 0;
      m = i;
      i = j;
      j = k;
      k = n;
      if (k < this.mItems.size()) {
        break label103;
      }
      if (j != 0) {
        break label374;
      }
    }
    for (;;)
    {
      Collections.sort(this.mItems, COMPARATOR);
      if (m != 0) {
        break label385;
      }
      return;
      if (this.mItems.size() >= i2) {
        break;
      }
      i = 1;
      break label35;
      label103:
      localObject = (ItemInfo)this.mItems.get(k);
      n = this.mAdapter.getItemPosition(((ItemInfo)localObject).object);
      int i1;
      if (n != -1)
      {
        if (n == -2) {
          break label215;
        }
        if (((ItemInfo)localObject).position != n) {
          break label329;
        }
        n = k;
        i1 = j;
        k = m;
        j = i;
        i = i1;
        m = n;
      }
      for (;;)
      {
        n = k;
        i1 = j;
        k = m + 1;
        j = i;
        i = i1;
        m = n;
        break;
        n = i;
        i1 = m;
        m = k;
        i = j;
        j = n;
        k = i1;
        continue;
        label215:
        this.mItems.remove(k);
        k -= 1;
        if (j != 0) {}
        for (;;)
        {
          this.mAdapter.destroyItem(this, ((ItemInfo)localObject).position, ((ItemInfo)localObject).object);
          if (this.mCurItem == ((ItemInfo)localObject).position) {
            break label295;
          }
          n = i;
          i1 = 1;
          m = k;
          i = j;
          j = n;
          k = i1;
          break;
          this.mAdapter.startUpdate(this);
          j = 1;
        }
        label295:
        m = Math.max(0, Math.min(this.mCurItem, i2 - 1));
        i = j;
        j = m;
        n = 1;
        m = k;
        k = n;
      }
      label329:
      if (((ItemInfo)localObject).position != this.mCurItem) {}
      for (;;)
      {
        ((ItemInfo)localObject).position = n;
        n = i;
        i1 = 1;
        m = k;
        i = j;
        j = n;
        k = i1;
        break;
        i = n;
      }
      label374:
      this.mAdapter.finishUpdate(this);
    }
    label385:
    int k = getChildCount();
    int j = 0;
    if (j >= k)
    {
      setCurrentItemInternal(i, false, true);
      requestLayout();
      return;
    }
    Object localObject = (LayoutParams)getChildAt(j).getLayoutParams();
    if (((LayoutParams)localObject).isDecor) {}
    for (;;)
    {
      j += 1;
      break;
      ((LayoutParams)localObject).widthFactor = 0.0F;
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (super.dispatchKeyEvent(paramKeyEvent)) {}
    while (executeKeyEvent(paramKeyEvent)) {
      return true;
    }
    return false;
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    int i;
    if (paramAccessibilityEvent.getEventType() != 4096)
    {
      int j = getChildCount();
      i = 0;
      if (i >= j) {
        return false;
      }
    }
    else
    {
      return super.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent);
    }
    View localView = getChildAt(i);
    if (localView.getVisibility() != 0) {}
    ItemInfo localItemInfo;
    do
    {
      i += 1;
      break;
      localItemInfo = infoForChild(localView);
    } while ((localItemInfo == null) || (localItemInfo.position != this.mCurItem) || (!localView.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent)));
    return true;
  }
  
  float distanceInfluenceForSnapDuration(float paramFloat)
  {
    return (float)Math.sin((float)((paramFloat - 0.5F) * 0.4712389167638204D));
  }
  
  public void draw(Canvas paramCanvas)
  {
    int j = 0;
    int i = 0;
    super.draw(paramCanvas);
    int k = ViewCompat.getOverScrollMode(this);
    if (k == 0)
    {
      if (!this.mLeftEdge.isFinished()) {
        break label91;
      }
      label30:
      if (!this.mRightEdge.isFinished()) {
        break label173;
      }
    }
    for (;;)
    {
      label40:
      if (i != 0) {
        break label266;
      }
      return;
      if (k != 1) {}
      for (;;)
      {
        this.mLeftEdge.finish();
        this.mRightEdge.finish();
        i = j;
        break label40;
        if (this.mAdapter != null) {
          if (this.mAdapter.getCount() > 1) {
            break;
          }
        }
      }
      label91:
      j = paramCanvas.save();
      i = getHeight() - getPaddingTop() - getPaddingBottom();
      k = getWidth();
      paramCanvas.rotate(270.0F);
      paramCanvas.translate(-i + getPaddingTop(), this.mFirstOffset * k);
      this.mLeftEdge.setSize(i, k);
      boolean bool = this.mLeftEdge.draw(paramCanvas) | false;
      paramCanvas.restoreToCount(j);
      break label30;
      label173:
      j = paramCanvas.save();
      k = getWidth();
      int m = getHeight();
      int n = getPaddingTop();
      int i1 = getPaddingBottom();
      paramCanvas.rotate(90.0F);
      paramCanvas.translate(-getPaddingTop(), -(this.mLastOffset + 1.0F) * k);
      this.mRightEdge.setSize(m - n - i1, k);
      bool |= this.mRightEdge.draw(paramCanvas);
      paramCanvas.restoreToCount(j);
    }
    label266:
    ViewCompat.postInvalidateOnAnimation(this);
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = this.mMarginDrawable;
    if (localDrawable == null) {}
    while (!localDrawable.isStateful()) {
      return;
    }
    localDrawable.setState(getDrawableState());
  }
  
  public void endFakeDrag()
  {
    if (this.mFakeDragging)
    {
      Object localObject = this.mVelocityTracker;
      ((VelocityTracker)localObject).computeCurrentVelocity(1000, this.mMaximumVelocity);
      int i = (int)VelocityTrackerCompat.getXVelocity((VelocityTracker)localObject, this.mActivePointerId);
      this.mPopulatePending = true;
      int j = getClientWidth();
      int k = getScrollX();
      localObject = infoForCurrentScrollPosition();
      setCurrentItemInternal(determineTargetPage(((ItemInfo)localObject).position, (k / j - ((ItemInfo)localObject).offset) / ((ItemInfo)localObject).widthFactor, i, (int)(this.mLastMotionX - this.mInitialMotionX)), true, true, i);
      endDrag();
      this.mFakeDragging = false;
      return;
    }
    throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
  }
  
  public boolean executeKeyEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() != 0) {}
    do
    {
      do
      {
        return false;
        switch (paramKeyEvent.getKeyCode())
        {
        default: 
          return false;
        case 21: 
          return arrowScroll(17);
        case 22: 
          return arrowScroll(66);
        }
      } while (Build.VERSION.SDK_INT < 11);
      if (KeyEventCompat.hasNoModifiers(paramKeyEvent)) {
        break;
      }
    } while (!KeyEventCompat.hasModifiers(paramKeyEvent, 1));
    return arrowScroll(1);
    return arrowScroll(2);
  }
  
  public void fakeDragBy(float paramFloat)
  {
    float f2;
    int i;
    float f1;
    Object localObject;
    ItemInfo localItemInfo;
    if (this.mFakeDragging)
    {
      this.mLastMotionX += paramFloat;
      f2 = getScrollX() - paramFloat;
      i = getClientWidth();
      paramFloat = i;
      float f4 = this.mFirstOffset;
      f1 = i;
      float f3 = this.mLastOffset;
      localObject = (ItemInfo)this.mItems.get(0);
      localItemInfo = (ItemInfo)this.mItems.get(this.mItems.size() - 1);
      if (((ItemInfo)localObject).position != 0) {
        break label206;
      }
      paramFloat *= f4;
      if (localItemInfo.position != this.mAdapter.getCount() - 1) {
        break label219;
      }
      f1 *= f3;
      label120:
      if (f2 >= paramFloat) {
        break label232;
      }
    }
    for (;;)
    {
      this.mLastMotionX += paramFloat - (int)paramFloat;
      scrollTo((int)paramFloat, getScrollY());
      pageScrolled((int)paramFloat);
      long l = SystemClock.uptimeMillis();
      localObject = MotionEvent.obtain(this.mFakeDragBeginTime, l, 2, this.mLastMotionX, 0.0F, 0);
      this.mVelocityTracker.addMovement((MotionEvent)localObject);
      ((MotionEvent)localObject).recycle();
      return;
      throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
      label206:
      paramFloat = ((ItemInfo)localObject).offset * i;
      break;
      label219:
      f1 = localItemInfo.offset * i;
      break label120;
      label232:
      if (f2 > f1) {
        paramFloat = f1;
      } else {
        paramFloat = f2;
      }
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
    return generateDefaultLayoutParams();
  }
  
  public PagerAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (this.mDrawingOrder != 2) {}
    for (;;)
    {
      return ((LayoutParams)((View)this.mDrawingOrderedChildren.get(paramInt2)).getLayoutParams()).childIndex;
      paramInt2 = paramInt1 - 1 - paramInt2;
    }
  }
  
  public int getCurrentItem()
  {
    return this.mCurItem;
  }
  
  public int getOffscreenPageLimit()
  {
    return this.mOffscreenPageLimit;
  }
  
  public int getPageMargin()
  {
    return this.mPageMargin;
  }
  
  ItemInfo infoForAnyChild(View paramView)
  {
    for (;;)
    {
      ViewParent localViewParent = paramView.getParent();
      if (localViewParent == this) {
        return infoForChild(paramView);
      }
      if (localViewParent == null) {}
      while (!(localViewParent instanceof View)) {
        return null;
      }
      paramView = (View)localViewParent;
    }
  }
  
  ItemInfo infoForChild(View paramView)
  {
    int i = 0;
    ItemInfo localItemInfo;
    for (;;)
    {
      if (i >= this.mItems.size()) {
        return null;
      }
      localItemInfo = (ItemInfo)this.mItems.get(i);
      if (this.mAdapter.isViewFromObject(paramView, localItemInfo.object)) {
        break;
      }
      i += 1;
    }
    return localItemInfo;
  }
  
  ItemInfo infoForPosition(int paramInt)
  {
    int i = 0;
    ItemInfo localItemInfo;
    for (;;)
    {
      if (i >= this.mItems.size()) {
        return null;
      }
      localItemInfo = (ItemInfo)this.mItems.get(i);
      if (localItemInfo.position == paramInt) {
        break;
      }
      i += 1;
    }
    return localItemInfo;
  }
  
  void initViewPager()
  {
    setWillNotDraw(false);
    setDescendantFocusability(262144);
    setFocusable(true);
    Context localContext = getContext();
    this.mScroller = new Scroller(localContext, sInterpolator);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(localContext);
    float f = localContext.getResources().getDisplayMetrics().density;
    this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(localViewConfiguration);
    this.mMinimumVelocity = ((int)(400.0F * f));
    this.mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
    this.mLeftEdge = new EdgeEffectCompat(localContext);
    this.mRightEdge = new EdgeEffectCompat(localContext);
    this.mFlingDistance = ((int)(25.0F * f));
    this.mCloseEnough = ((int)(2.0F * f));
    this.mDefaultGutterSize = ((int)(16.0F * f));
    ViewCompat.setAccessibilityDelegate(this, new MyAccessibilityDelegate());
    if (ViewCompat.getImportantForAccessibility(this) != 0) {
      return;
    }
    ViewCompat.setImportantForAccessibility(this, 1);
  }
  
  public boolean isFakeDragging()
  {
    return this.mFakeDragging;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mFirstLayout = true;
  }
  
  protected void onDetachedFromWindow()
  {
    removeCallbacks(this.mEndScrollRunnable);
    super.onDetachedFromWindow();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.mPageMargin <= 0) {
      break label12;
    }
    label12:
    while ((this.mMarginDrawable == null) || (this.mItems.size() <= 0) || (this.mAdapter == null)) {
      return;
    }
    int k = getScrollX();
    int m = getWidth();
    float f3 = this.mPageMargin / m;
    Object localObject = (ItemInfo)this.mItems.get(0);
    float f1 = ((ItemInfo)localObject).offset;
    int n = this.mItems.size();
    int i = ((ItemInfo)localObject).position;
    int i1 = ((ItemInfo)this.mItems.get(n - 1)).position;
    int j = 0;
    label117:
    label124:
    label134:
    float f4;
    float f2;
    if (i < i1)
    {
      if (i > ((ItemInfo)localObject).position) {
        break label243;
      }
      if (i == ((ItemInfo)localObject).position) {
        break label277;
      }
      f4 = this.mAdapter.getPageWidth(i);
      f2 = (f1 + f4) * m;
    }
    for (f1 += f4 + f3;; f1 = ((ItemInfo)localObject).offset + ((ItemInfo)localObject).widthFactor + f3)
    {
      if (this.mPageMargin + f2 > k)
      {
        this.mMarginDrawable.setBounds((int)f2, this.mTopPageBounds, (int)(this.mPageMargin + f2 + 0.5F), this.mBottomPageBounds);
        this.mMarginDrawable.draw(paramCanvas);
      }
      if (f2 > k + m) {
        break;
      }
      i += 1;
      break label117;
      break label12;
      label243:
      if (j >= n) {
        break label134;
      }
      localObject = this.mItems;
      j += 1;
      localObject = (ItemInfo)((ArrayList)localObject).get(j);
      break label124;
      label277:
      f2 = (((ItemInfo)localObject).offset + ((ItemInfo)localObject).widthFactor) * m;
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction() & 0xFF;
    if (i == 3) {}
    while (i == 1)
    {
      this.mIsBeingDragged = false;
      this.mIsUnableToDrag = false;
      this.mActivePointerId = -1;
      if (this.mVelocityTracker != null) {
        break;
      }
      return false;
    }
    if (i == 0) {
      switch (i)
      {
      default: 
        label88:
        if (this.mVelocityTracker == null) {
          break;
        }
      }
    }
    for (;;)
    {
      this.mVelocityTracker.addMovement(paramMotionEvent);
      return this.mIsBeingDragged;
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
      return false;
      if (!this.mIsBeingDragged)
      {
        if (!this.mIsUnableToDrag) {
          break;
        }
        return false;
      }
      return true;
      i = this.mActivePointerId;
      if (i == -1) {
        break label88;
      }
      i = MotionEventCompat.findPointerIndex(paramMotionEvent, i);
      float f2 = MotionEventCompat.getX(paramMotionEvent, i);
      float f1 = f2 - this.mLastMotionX;
      float f4 = Math.abs(f1);
      float f3 = MotionEventCompat.getY(paramMotionEvent, i);
      float f5 = Math.abs(f3 - this.mInitialMotionY);
      if ((f1 == 0.0F) || (isGutterDrag(this.mLastMotionX, f1)))
      {
        label218:
        if ((f4 <= this.mTouchSlop) || (0.5F * f4 <= f5)) {
          break label359;
        }
        this.mIsBeingDragged = true;
        requestParentDisallowInterceptTouchEvent(true);
        setScrollState(1);
        if (f1 <= 0.0F) {
          break label345;
        }
        f1 = this.mInitialMotionX + this.mTouchSlop;
        label273:
        this.mLastMotionX = f1;
        this.mLastMotionY = f3;
        setScrollingCacheEnabled(true);
      }
      while ((this.mIsBeingDragged) && (performDrag(f2)))
      {
        ViewCompat.postInvalidateOnAnimation(this);
        break;
        if (!canScroll(this, false, (int)f1, (int)f2, (int)f3)) {
          break label218;
        }
        this.mLastMotionX = f2;
        this.mLastMotionY = f3;
        this.mIsUnableToDrag = true;
        return false;
        label345:
        f1 = this.mInitialMotionX - this.mTouchSlop;
        break label273;
        label359:
        if (f5 > this.mTouchSlop) {
          this.mIsUnableToDrag = true;
        }
      }
      f1 = paramMotionEvent.getX();
      this.mInitialMotionX = f1;
      this.mLastMotionX = f1;
      f1 = paramMotionEvent.getY();
      this.mInitialMotionY = f1;
      this.mLastMotionY = f1;
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
      this.mIsUnableToDrag = false;
      this.mScroller.computeScrollOffset();
      if (this.mScrollState != 2) {}
      while (Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) <= this.mCloseEnough)
      {
        completeScroll(false);
        this.mIsBeingDragged = false;
        break;
      }
      this.mScroller.abortAnimation();
      this.mPopulatePending = false;
      populate();
      this.mIsBeingDragged = true;
      requestParentDisallowInterceptTouchEvent(true);
      setScrollState(1);
      break label88;
      onSecondaryPointerUp(paramMotionEvent);
      break label88;
      this.mVelocityTracker = VelocityTracker.obtain();
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i1 = getChildCount();
    int i3 = paramInt3 - paramInt1;
    int i2 = paramInt4 - paramInt2;
    paramInt2 = getPaddingLeft();
    paramInt1 = getPaddingTop();
    paramInt4 = getPaddingRight();
    paramInt3 = getPaddingBottom();
    int i4 = getScrollX();
    int i = 0;
    int m = 0;
    int j;
    if (m >= i1)
    {
      j = i3 - paramInt2 - paramInt4;
      paramInt4 = 0;
      if (paramInt4 < i1) {
        break label565;
      }
      this.mTopPageBounds = paramInt1;
      this.mBottomPageBounds = (i2 - paramInt3);
      this.mDecorChildCount = i;
      if (this.mFirstLayout) {
        break label722;
      }
    }
    for (;;)
    {
      this.mFirstLayout = false;
      return;
      View localView = getChildAt(m);
      if (localView.getVisibility() == 8)
      {
        j = i;
        k = paramInt1;
        paramInt1 = paramInt4;
        i = paramInt2;
        paramInt4 = k;
        paramInt2 = paramInt1;
        paramInt1 = j;
      }
      LayoutParams localLayoutParams;
      for (;;)
      {
        m += 1;
        j = i;
        i = paramInt1;
        paramInt1 = paramInt4;
        paramInt4 = paramInt2;
        paramInt2 = j;
        break;
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.isDecor) {
          break label221;
        }
        k = i;
        i = paramInt1;
        j = paramInt2;
        paramInt1 = k;
        paramInt2 = paramInt4;
        paramInt4 = i;
        i = j;
      }
      label221:
      j = localLayoutParams.gravity;
      int i5 = localLayoutParams.gravity;
      label282:
      int n;
      switch (j & 0x7)
      {
      case 2: 
      case 4: 
      default: 
        j = paramInt2;
        k = paramInt2;
        switch (i5 & 0x70)
        {
        default: 
          n = paramInt1;
          paramInt2 = paramInt1;
          paramInt1 = paramInt3;
          paramInt3 = n;
        }
        break;
      }
      for (;;)
      {
        j += i4;
        localView.layout(j, paramInt3, localView.getMeasuredWidth() + j, localView.getMeasuredHeight() + paramInt3);
        j = i + 1;
        i = paramInt2;
        paramInt3 = paramInt1;
        paramInt2 = paramInt4;
        paramInt1 = j;
        paramInt4 = i;
        i = k;
        break;
        k = localView.getMeasuredWidth();
        j = paramInt2;
        k += paramInt2;
        break label282;
        j = Math.max((i3 - localView.getMeasuredWidth()) / 2, paramInt2);
        k = paramInt2;
        break label282;
        k = localView.getMeasuredWidth();
        j = paramInt4 + localView.getMeasuredWidth();
        n = i3 - paramInt4 - k;
        paramInt4 = j;
        k = paramInt2;
        j = n;
        break label282;
        n = localView.getMeasuredHeight();
        paramInt2 = paramInt3;
        n += paramInt1;
        paramInt3 = paramInt1;
        paramInt1 = paramInt2;
        paramInt2 = n;
        continue;
        n = Math.max((i2 - localView.getMeasuredHeight()) / 2, paramInt1);
        paramInt2 = paramInt1;
        paramInt1 = paramInt3;
        paramInt3 = n;
        continue;
        n = i2 - paramInt3 - localView.getMeasuredHeight();
        i5 = localView.getMeasuredHeight();
        paramInt2 = paramInt1;
        paramInt1 = paramInt3 + i5;
        paramInt3 = n;
      }
      label565:
      localView = getChildAt(paramInt4);
      if (localView.getVisibility() == 8) {}
      ItemInfo localItemInfo;
      do
      {
        do
        {
          paramInt4 += 1;
          break;
          localLayoutParams = (LayoutParams)localView.getLayoutParams();
        } while (localLayoutParams.isDecor);
        localItemInfo = infoForChild(localView);
      } while (localItemInfo == null);
      float f = j;
      int k = (int)(localItemInfo.offset * f) + paramInt2;
      if (!localLayoutParams.needsMeasure) {}
      for (;;)
      {
        localView.layout(k, paramInt1, localView.getMeasuredWidth() + k, localView.getMeasuredHeight() + paramInt1);
        break;
        localLayoutParams.needsMeasure = false;
        f = j;
        localView.measure(View.MeasureSpec.makeMeasureSpec((int)(localLayoutParams.widthFactor * f), 1073741824), View.MeasureSpec.makeMeasureSpec(i2 - paramInt1 - paramInt3, 1073741824));
      }
      label722:
      scrollToItem(this.mCurItem, false, 0, false);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(getDefaultSize(0, paramInt1), getDefaultSize(0, paramInt2));
    paramInt1 = getMeasuredWidth();
    this.mGutterSize = Math.min(paramInt1 / 10, this.mDefaultGutterSize);
    paramInt1 = paramInt1 - getPaddingLeft() - getPaddingRight();
    paramInt2 = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    int i4 = getChildCount();
    int k = 0;
    int i;
    LayoutParams localLayoutParams;
    if (k >= i4)
    {
      this.mChildWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824);
      this.mChildHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824);
      this.mInLayout = true;
      populate();
      this.mInLayout = false;
      i = getChildCount();
      paramInt2 = 0;
      if (paramInt2 < i) {}
    }
    else
    {
      localView = getChildAt(k);
      int j;
      if (localView.getVisibility() == 8)
      {
        j = paramInt2;
        i = paramInt1;
      }
      for (;;)
      {
        k += 1;
        paramInt1 = i;
        paramInt2 = j;
        break;
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        i = paramInt1;
        j = paramInt2;
        if (localLayoutParams != null)
        {
          i = paramInt1;
          j = paramInt2;
          if (localLayoutParams.isDecor)
          {
            int n = localLayoutParams.gravity & 0x7;
            int m = localLayoutParams.gravity & 0x70;
            j = Integer.MIN_VALUE;
            i = Integer.MIN_VALUE;
            label239:
            label242:
            label248:
            label251:
            label261:
            int i1;
            label278:
            int i3;
            if (m == 48)
            {
              m = 1;
              if (n != 3) {
                break label362;
              }
              n = 1;
              if (m != 0) {
                break label374;
              }
              if (n != 0) {
                break label382;
              }
              if (localLayoutParams.width != -2) {
                break label390;
              }
              i1 = j;
              j = paramInt1;
              if (localLayoutParams.height != -2) {
                break label420;
              }
              int i2 = paramInt2;
              i3 = i;
              i = i2;
            }
            for (;;)
            {
              localView.measure(View.MeasureSpec.makeMeasureSpec(j, i1), View.MeasureSpec.makeMeasureSpec(i, i3));
              if (m != 0) {
                break label450;
              }
              i = paramInt1;
              j = paramInt2;
              if (n == 0) {
                break;
              }
              i = paramInt1 - localView.getMeasuredWidth();
              j = paramInt2;
              break;
              if (m == 80) {
                break label239;
              }
              m = 0;
              break label242;
              label362:
              if (n == 5) {
                break label248;
              }
              n = 0;
              break label251;
              label374:
              j = 1073741824;
              break label261;
              label382:
              i = 1073741824;
              break label261;
              label390:
              i1 = 1073741824;
              if (localLayoutParams.width == -1)
              {
                j = paramInt1;
                break label278;
              }
              j = localLayoutParams.width;
              break label278;
              label420:
              i3 = 1073741824;
              if (localLayoutParams.height == -1) {
                i = paramInt2;
              } else {
                i = localLayoutParams.height;
              }
            }
            label450:
            j = paramInt2 - localView.getMeasuredHeight();
            i = paramInt1;
          }
        }
      }
    }
    View localView = getChildAt(paramInt2);
    if (localView.getVisibility() == 8) {}
    for (;;)
    {
      paramInt2 += 1;
      break;
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (localLayoutParams == null) {}
      while (!localLayoutParams.isDecor)
      {
        float f = paramInt1;
        localView.measure(View.MeasureSpec.makeMeasureSpec((int)(localLayoutParams.widthFactor * f), 1073741824), this.mChildHeightMeasureSpec);
        break;
      }
    }
  }
  
  protected void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
  {
    if (this.mDecorChildCount <= 0)
    {
      if (this.mOnPageChangeListener != null) {
        break label323;
      }
      label14:
      if (this.mInternalPageChangeListener != null) {
        break label338;
      }
      label21:
      if (this.mPageTransformer != null) {
        break label353;
      }
    }
    for (;;)
    {
      this.mCalledSuper = true;
      return;
      int i1 = getScrollX();
      int i = getPaddingLeft();
      int j = getPaddingRight();
      int i2 = getWidth();
      int i3 = getChildCount();
      int m = 0;
      if (m >= i3) {
        break;
      }
      View localView = getChildAt(m);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      int k;
      int n;
      if (localLayoutParams.isDecor) {
        switch (localLayoutParams.gravity & 0x7)
        {
        case 2: 
        case 4: 
        default: 
          k = i;
          n = j;
          j = i;
          i = n;
          label160:
          k = k + i1 - localView.getLeft();
          if (k != 0) {
            break;
          }
        }
      }
      for (;;)
      {
        m += 1;
        k = i;
        i = j;
        j = k;
        break;
        k = i;
        i = j;
        j = k;
        continue;
        k = localView.getWidth();
        n = k + i;
        k = i;
        i = j;
        j = n;
        break label160;
        k = Math.max((i2 - localView.getMeasuredWidth()) / 2, i);
        n = i;
        i = j;
        j = n;
        break label160;
        k = i2 - j - localView.getMeasuredWidth();
        int i4 = localView.getMeasuredWidth();
        n = i;
        i = j + i4;
        j = n;
        break label160;
        localView.offsetLeftAndRight(k);
      }
      label323:
      this.mOnPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
      break label14;
      label338:
      this.mInternalPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
      break label21;
      label353:
      paramInt2 = getScrollX();
      i = getChildCount();
      paramInt1 = 0;
      while (paramInt1 < i)
      {
        localView = getChildAt(paramInt1);
        if (!((LayoutParams)localView.getLayoutParams()).isDecor)
        {
          paramFloat = (localView.getLeft() - paramInt2) / getClientWidth();
          this.mPageTransformer.transformPage(localView, paramFloat);
        }
        paramInt1 += 1;
      }
    }
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    int j = -1;
    int i = getChildCount();
    if ((paramInt & 0x2) == 0) {
      i -= 1;
    }
    for (int k = -1; i == j; k = 1)
    {
      return false;
      j = i;
      i = 0;
    }
    View localView = getChildAt(i);
    if (localView.getVisibility() != 0) {}
    ItemInfo localItemInfo;
    do
    {
      i += k;
      break;
      localItemInfo = infoForChild(localView);
    } while ((localItemInfo == null) || (localItemInfo.position != this.mCurItem) || (!localView.requestFocus(paramInt, paramRect)));
    return true;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof SavedState))
    {
      paramParcelable = (SavedState)paramParcelable;
      super.onRestoreInstanceState(paramParcelable.getSuperState());
      if (this.mAdapter == null)
      {
        this.mRestoredCurItem = paramParcelable.position;
        this.mRestoredAdapterState = paramParcelable.adapterState;
        this.mRestoredClassLoader = paramParcelable.loader;
      }
    }
    else
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    this.mAdapter.restoreState(paramParcelable.adapterState, paramParcelable.loader);
    setCurrentItemInternal(paramParcelable.position, false, true);
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.position = this.mCurItem;
    if (this.mAdapter == null) {
      return localSavedState;
    }
    localSavedState.adapterState = this.mAdapter.saveState();
    return localSavedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 == paramInt3) {
      return;
    }
    recomputeScrollPosition(paramInt1, paramInt3, this.mPageMargin, this.mPageMargin);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mFakeDragging)
    {
      if (paramMotionEvent.getAction() == 0) {
        break label25;
      }
      if (this.mAdapter != null) {
        break label34;
      }
    }
    label25:
    label34:
    while (this.mAdapter.getCount() == 0)
    {
      return false;
      return true;
      if (paramMotionEvent.getEdgeFlags() == 0) {
        break;
      }
      return false;
    }
    int i;
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.addMovement(paramMotionEvent);
      switch (paramMotionEvent.getAction() & 0xFF)
      {
      case 4: 
      default: 
        i = 0;
      }
    }
    for (;;)
    {
      if (i != 0) {
        break label649;
      }
      return true;
      this.mVelocityTracker = VelocityTracker.obtain();
      break;
      this.mScroller.abortAnimation();
      this.mPopulatePending = false;
      populate();
      float f1 = paramMotionEvent.getX();
      this.mInitialMotionX = f1;
      this.mLastMotionX = f1;
      f1 = paramMotionEvent.getY();
      this.mInitialMotionY = f1;
      this.mLastMotionY = f1;
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
      i = 0;
      continue;
      if (this.mIsBeingDragged) {}
      Object localObject;
      for (;;)
      {
        if (!this.mIsBeingDragged)
        {
          i = 0;
          break;
          i = MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId);
          f1 = MotionEventCompat.getX(paramMotionEvent, i);
          float f3 = Math.abs(f1 - this.mLastMotionX);
          float f2 = MotionEventCompat.getY(paramMotionEvent, i);
          float f4 = Math.abs(f2 - this.mLastMotionY);
          if ((f3 > this.mTouchSlop) && (f3 > f4))
          {
            this.mIsBeingDragged = true;
            requestParentDisallowInterceptTouchEvent(true);
            if (f1 - this.mInitialMotionX > 0.0F) {}
            for (f1 = this.mInitialMotionX + this.mTouchSlop;; f1 = this.mInitialMotionX - this.mTouchSlop)
            {
              this.mLastMotionX = f1;
              this.mLastMotionY = f2;
              setScrollState(1);
              setScrollingCacheEnabled(true);
              localObject = getParent();
              if (localObject == null) {
                break;
              }
              ((ViewParent)localObject).requestDisallowInterceptTouchEvent(true);
              break;
            }
          }
        }
      }
      boolean bool1 = performDrag(MotionEventCompat.getX(paramMotionEvent, MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId))) | false;
      continue;
      if (!this.mIsBeingDragged)
      {
        bool1 = false;
      }
      else
      {
        localObject = this.mVelocityTracker;
        ((VelocityTracker)localObject).computeCurrentVelocity(1000, this.mMaximumVelocity);
        int j = (int)VelocityTrackerCompat.getXVelocity((VelocityTracker)localObject, this.mActivePointerId);
        this.mPopulatePending = true;
        int m = getClientWidth();
        int n = getScrollX();
        localObject = infoForCurrentScrollPosition();
        setCurrentItemInternal(determineTargetPage(((ItemInfo)localObject).position, (n / m - ((ItemInfo)localObject).offset) / ((ItemInfo)localObject).widthFactor, j, (int)(MotionEventCompat.getX(paramMotionEvent, MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId)) - this.mInitialMotionX)), true, true, j);
        this.mActivePointerId = -1;
        endDrag();
        boolean bool2 = this.mLeftEdge.onRelease() | this.mRightEdge.onRelease();
        continue;
        if (!this.mIsBeingDragged)
        {
          bool2 = false;
        }
        else
        {
          scrollToItem(this.mCurItem, true, 0, false);
          this.mActivePointerId = -1;
          endDrag();
          bool2 = this.mLeftEdge.onRelease() | this.mRightEdge.onRelease();
          continue;
          int k = MotionEventCompat.getActionIndex(paramMotionEvent);
          this.mLastMotionX = MotionEventCompat.getX(paramMotionEvent, k);
          this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, k);
          k = 0;
          continue;
          onSecondaryPointerUp(paramMotionEvent);
          this.mLastMotionX = MotionEventCompat.getX(paramMotionEvent, MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId));
          k = 0;
        }
      }
    }
    label649:
    ViewCompat.postInvalidateOnAnimation(this);
    return true;
  }
  
  boolean pageLeft()
  {
    if (this.mCurItem <= 0) {
      return false;
    }
    setCurrentItem(this.mCurItem - 1, true);
    return true;
  }
  
  boolean pageRight()
  {
    if (this.mAdapter == null) {}
    while (this.mCurItem >= this.mAdapter.getCount() - 1) {
      return false;
    }
    setCurrentItem(this.mCurItem + 1, true);
    return true;
  }
  
  void populate()
  {
    populate(this.mCurItem);
  }
  
  void populate(int paramInt)
  {
    int j;
    int i3;
    int i1;
    int i2;
    label95:
    label109:
    label114:
    Object localObject4;
    if (this.mCurItem == paramInt)
    {
      localObject3 = null;
      j = 2;
      if (this.mAdapter == null) {
        break label226;
      }
      if (this.mPopulatePending) {
        break label231;
      }
      if (getWindowToken() == null) {
        break label236;
      }
      this.mAdapter.startUpdate(this);
      paramInt = this.mOffscreenPageLimit;
      i3 = Math.max(0, this.mCurItem - paramInt);
      i1 = this.mAdapter.getCount();
      i2 = Math.min(i1 - 1, paramInt + this.mCurItem);
      if (i1 != this.mExpectedAdapterCount) {
        break label237;
      }
      paramInt = 0;
      if (paramInt < this.mItems.size()) {
        break label346;
      }
      localObject1 = null;
      if (localObject1 == null) {
        break label400;
      }
      localObject4 = localObject1;
      label118:
      if (localObject4 != null) {
        break label419;
      }
      localObject3 = this.mAdapter;
      paramInt = this.mCurItem;
      if (localObject4 != null) {
        break label1108;
      }
    }
    label226:
    label231:
    label236:
    label237:
    label346:
    label400:
    label419:
    label432:
    label461:
    label488:
    label519:
    label555:
    label585:
    label590:
    label609:
    label785:
    label843:
    label863:
    label879:
    label884:
    label902:
    label1033:
    label1092:
    label1108:
    for (Object localObject1 = null;; localObject2 = ((ItemInfo)localObject4).object)
    {
      ((PagerAdapter)localObject3).setPrimaryItem(this, paramInt, localObject1);
      this.mAdapter.finishUpdate(this);
      int i = getChildCount();
      paramInt = 0;
      if (paramInt < i) {
        break label1118;
      }
      sortChildDrawingOrder();
      if (hasFocus()) {
        break label1202;
      }
      return;
      if (this.mCurItem >= paramInt) {}
      for (i = 17;; i = 66)
      {
        localObject3 = infoForPosition(this.mCurItem);
        this.mCurItem = paramInt;
        j = i;
        break;
      }
      sortChildDrawingOrder();
      return;
      sortChildDrawingOrder();
      return;
      return;
      try
      {
        localObject1 = getResources().getResourceName(getId());
        throw new IllegalStateException("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: " + this.mExpectedAdapterCount + ", found: " + i1 + " Pager id: " + (String)localObject1 + " Pager class: " + getClass() + " Problematic adapter: " + this.mAdapter.getClass());
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        for (;;)
        {
          localObject2 = Integer.toHexString(getId());
        }
      }
      localObject4 = (ItemInfo)this.mItems.get(paramInt);
      if (((ItemInfo)localObject4).position < this.mCurItem)
      {
        paramInt += 1;
        break label95;
      }
      localObject2 = localObject4;
      if (((ItemInfo)localObject4).position == this.mCurItem) {
        break label109;
      }
      localObject2 = null;
      break label109;
      if (i1 <= 0) {
        break label114;
      }
      localObject4 = addNewItem(this.mCurItem, paramInt);
      break label118;
      int n = paramInt - 1;
      int i4;
      float f3;
      int m;
      int k;
      Object localObject5;
      float f1;
      if (n < 0)
      {
        localObject2 = null;
        i4 = getClientWidth();
        if (i4 <= 0) {
          break label585;
        }
        f2 = 2.0F - ((ItemInfo)localObject4).widthFactor + getPaddingLeft() / i4;
        i = this.mCurItem;
        f3 = 0.0F;
        m = i - 1;
        k = paramInt;
        localObject5 = localObject2;
        if (m >= 0) {
          break label590;
        }
        f1 = ((ItemInfo)localObject4).widthFactor;
        paramInt = k + 1;
        if (f1 < 2.0F)
        {
          if (paramInt < this.mItems.size()) {
            break label863;
          }
          localObject2 = null;
          if (i4 <= 0) {
            break label879;
          }
        }
      }
      for (float f2 = getPaddingRight() / i4 + 2.0F;; f2 = 0.0F)
      {
        i = this.mCurItem;
        i += 1;
        if (i < i1) {
          break label884;
        }
        calculatePageOffsets((ItemInfo)localObject4, k, (ItemInfo)localObject3);
        break;
        localObject2 = (ItemInfo)this.mItems.get(n);
        break label432;
        f2 = 0.0F;
        break label461;
        if ((f3 < f2) || (m >= i3))
        {
          if (localObject5 != null) {
            break label785;
          }
          f1 = f3 + addNewItem(m, n + 1).widthFactor;
          i = k + 1;
          if (n >= 0) {
            break label843;
          }
          localObject2 = null;
          paramInt = n;
        }
        for (;;)
        {
          m -= 1;
          localObject5 = localObject2;
          n = paramInt;
          f3 = f1;
          k = i;
          break;
          if (localObject5 == null) {
            break label488;
          }
          localObject2 = localObject5;
          paramInt = n;
          f1 = f3;
          i = k;
          if (m == ((ItemInfo)localObject5).position)
          {
            localObject2 = localObject5;
            paramInt = n;
            f1 = f3;
            i = k;
            if (!((ItemInfo)localObject5).scrolling)
            {
              this.mItems.remove(n);
              this.mAdapter.destroyItem(this, m, ((ItemInfo)localObject5).object);
              paramInt = n - 1;
              i = k - 1;
              if (paramInt < 0)
              {
                localObject2 = null;
                f1 = f3;
              }
              else
              {
                localObject2 = (ItemInfo)this.mItems.get(paramInt);
                f1 = f3;
                continue;
                if (m != ((ItemInfo)localObject5).position) {
                  break label609;
                }
                f1 = f3 + ((ItemInfo)localObject5).widthFactor;
                paramInt = n - 1;
                if (paramInt < 0)
                {
                  localObject2 = null;
                  i = k;
                }
                else
                {
                  localObject2 = (ItemInfo)this.mItems.get(paramInt);
                  i = k;
                  continue;
                  localObject2 = (ItemInfo)this.mItems.get(n);
                  paramInt = n;
                }
              }
            }
          }
        }
        localObject2 = (ItemInfo)this.mItems.get(paramInt);
        break label519;
      }
      if ((f1 < f2) || (i <= i2))
      {
        if (localObject2 != null) {
          break label1033;
        }
        localObject2 = addNewItem(i, paramInt);
        paramInt += 1;
        f3 = ((ItemInfo)localObject2).widthFactor;
        if (paramInt < this.mItems.size()) {
          break label1092;
        }
      }
      for (localObject2 = null;; localObject2 = (ItemInfo)this.mItems.get(paramInt))
      {
        f1 += f3;
        do
        {
          i += 1;
          break;
          if (localObject2 == null) {
            break label555;
          }
        } while (i != ((ItemInfo)localObject2).position);
        while (((ItemInfo)localObject2).scrolling) {}
        this.mItems.remove(paramInt);
        this.mAdapter.destroyItem(this, i, ((ItemInfo)localObject2).object);
        if (paramInt >= this.mItems.size()) {}
        for (localObject2 = null;; localObject2 = (ItemInfo)this.mItems.get(paramInt)) {
          break;
        }
        if (i != ((ItemInfo)localObject2).position) {
          break label902;
        }
        f3 = ((ItemInfo)localObject2).widthFactor;
        paramInt += 1;
        if (paramInt >= this.mItems.size()) {}
        for (localObject2 = null;; localObject2 = (ItemInfo)this.mItems.get(paramInt))
        {
          f1 += f3;
          break;
        }
      }
    }
    label1118:
    Object localObject3 = getChildAt(paramInt);
    Object localObject2 = (LayoutParams)((View)localObject3).getLayoutParams();
    ((LayoutParams)localObject2).childIndex = paramInt;
    if (((LayoutParams)localObject2).isDecor) {}
    for (;;)
    {
      paramInt += 1;
      break;
      if (((LayoutParams)localObject2).widthFactor == 0.0F)
      {
        localObject3 = infoForChild((View)localObject3);
        if (localObject3 != null)
        {
          ((LayoutParams)localObject2).widthFactor = ((ItemInfo)localObject3).widthFactor;
          ((LayoutParams)localObject2).position = ((ItemInfo)localObject3).position;
        }
      }
    }
    label1202:
    localObject2 = findFocus();
    if (localObject2 == null)
    {
      localObject2 = null;
      label1216:
      if (localObject2 != null) {
        break label1269;
      }
      label1221:
      paramInt = 0;
      label1223:
      if (paramInt < getChildCount())
      {
        localObject2 = getChildAt(paramInt);
        localObject3 = infoForChild((View)localObject2);
        if (localObject3 != null) {
          break label1282;
        }
      }
    }
    for (;;)
    {
      paramInt += 1;
      break label1223;
      break;
      localObject2 = infoForAnyChild((View)localObject2);
      break label1216;
      label1269:
      if (((ItemInfo)localObject2).position != this.mCurItem) {
        break label1221;
      }
      return;
      label1282:
      if (((ItemInfo)localObject3).position == this.mCurItem) {
        if (((View)localObject2).requestFocus(j)) {
          break;
        }
      }
    }
  }
  
  public void removeView(View paramView)
  {
    if (!this.mInLayout)
    {
      super.removeView(paramView);
      return;
    }
    removeViewInLayout(paramView);
  }
  
  public void setAdapter(PagerAdapter paramPagerAdapter)
  {
    Object localObject;
    if (this.mAdapter == null)
    {
      localObject = this.mAdapter;
      this.mAdapter = paramPagerAdapter;
      this.mExpectedAdapterCount = 0;
      if (this.mAdapter != null) {
        break label141;
      }
      label30:
      if (this.mAdapterChangeListener != null) {
        break label269;
      }
    }
    label141:
    label219:
    label262:
    label269:
    while (localObject == paramPagerAdapter)
    {
      return;
      this.mAdapter.unregisterDataSetObserver(this.mObserver);
      this.mAdapter.startUpdate(this);
      int i = 0;
      for (;;)
      {
        if (i >= this.mItems.size())
        {
          this.mAdapter.finishUpdate(this);
          this.mItems.clear();
          removeNonDecorViews();
          this.mCurItem = 0;
          scrollTo(0, 0);
          break;
        }
        localObject = (ItemInfo)this.mItems.get(i);
        this.mAdapter.destroyItem(this, ((ItemInfo)localObject).position, ((ItemInfo)localObject).object);
        i += 1;
      }
      if (this.mObserver != null) {}
      for (;;)
      {
        this.mAdapter.registerDataSetObserver(this.mObserver);
        this.mPopulatePending = false;
        boolean bool = this.mFirstLayout;
        this.mFirstLayout = true;
        this.mExpectedAdapterCount = this.mAdapter.getCount();
        if (this.mRestoredCurItem >= 0) {
          break label219;
        }
        if (!bool) {
          break label262;
        }
        requestLayout();
        break;
        this.mObserver = new PagerObserver(null);
      }
      this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
      setCurrentItemInternal(this.mRestoredCurItem, false, true);
      this.mRestoredCurItem = -1;
      this.mRestoredAdapterState = null;
      this.mRestoredClassLoader = null;
      break label30;
      populate();
      break label30;
    }
    this.mAdapterChangeListener.onAdapterChanged((PagerAdapter)localObject, paramPagerAdapter);
  }
  
  void setChildrenDrawingOrderEnabledCompat(boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT < 7) {
      return;
    }
    if (this.mSetChildrenDrawingOrderEnabled != null) {}
    for (;;)
    {
      try
      {
        this.mSetChildrenDrawingOrderEnabled.invoke(this, new Object[] { Boolean.valueOf(paramBoolean) });
        return;
      }
      catch (Exception localException)
      {
        Log.e("ViewPager", "Error changing children drawing order", localException);
        return;
      }
      try
      {
        this.mSetChildrenDrawingOrderEnabled = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", new Class[] { Boolean.TYPE });
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        Log.e("ViewPager", "Can't find setChildrenDrawingOrderEnabled", localNoSuchMethodException);
      }
    }
  }
  
  public void setCurrentItem(int paramInt)
  {
    this.mPopulatePending = false;
    if (this.mFirstLayout) {}
    for (boolean bool = false;; bool = true)
    {
      setCurrentItemInternal(paramInt, bool, false);
      return;
    }
  }
  
  public void setCurrentItem(int paramInt, boolean paramBoolean)
  {
    this.mPopulatePending = false;
    setCurrentItemInternal(paramInt, paramBoolean, false);
  }
  
  void setCurrentItemInternal(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    setCurrentItemInternal(paramInt, paramBoolean1, paramBoolean2, 0);
  }
  
  void setCurrentItemInternal(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
  {
    boolean bool = false;
    if (this.mAdapter == null) {}
    while (this.mAdapter.getCount() <= 0)
    {
      setScrollingCacheEnabled(false);
      return;
    }
    label45:
    int i;
    if (paramBoolean2)
    {
      if (paramInt1 < 0) {
        break label134;
      }
      if (paramInt1 >= this.mAdapter.getCount()) {
        break label139;
      }
      i = this.mOffscreenPageLimit;
      if (paramInt1 <= this.mCurItem + i) {
        break label152;
      }
      label62:
      i = 0;
      label65:
      if (i < this.mItems.size()) {
        break label166;
      }
      label77:
      if (this.mCurItem != paramInt1) {
        break label191;
      }
    }
    label134:
    label139:
    label152:
    label166:
    label191:
    for (paramBoolean2 = bool;; paramBoolean2 = true)
    {
      if (this.mFirstLayout) {
        break label196;
      }
      populate(paramInt1);
      scrollToItem(paramInt1, paramBoolean1, paramInt2, paramBoolean2);
      return;
      if ((this.mCurItem != paramInt1) || (this.mItems.size() == 0)) {
        break;
      }
      setScrollingCacheEnabled(false);
      return;
      paramInt1 = 0;
      break label45;
      paramInt1 = this.mAdapter.getCount() - 1;
      break label45;
      if (paramInt1 < this.mCurItem - i) {
        break label62;
      }
      break label77;
      ((ItemInfo)this.mItems.get(i)).scrolling = true;
      i += 1;
      break label65;
    }
    label196:
    this.mCurItem = paramInt1;
    if (!paramBoolean2) {
      if (paramBoolean2) {
        break label234;
      }
    }
    for (;;)
    {
      requestLayout();
      return;
      if (this.mOnPageChangeListener == null) {
        break;
      }
      this.mOnPageChangeListener.onPageSelected(paramInt1);
      break;
      label234:
      if (this.mInternalPageChangeListener != null) {
        this.mInternalPageChangeListener.onPageSelected(paramInt1);
      }
    }
  }
  
  OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    OnPageChangeListener localOnPageChangeListener = this.mInternalPageChangeListener;
    this.mInternalPageChangeListener = paramOnPageChangeListener;
    return localOnPageChangeListener;
  }
  
  public void setOffscreenPageLimit(int paramInt)
  {
    if (paramInt >= 1) {}
    while (paramInt == this.mOffscreenPageLimit)
    {
      return;
      Log.w("ViewPager", "Requested offscreen page limit " + paramInt + " too small; defaulting to " + 1);
      paramInt = 1;
    }
    this.mOffscreenPageLimit = paramInt;
    populate();
  }
  
  void setOnAdapterChangeListener(OnAdapterChangeListener paramOnAdapterChangeListener)
  {
    this.mAdapterChangeListener = paramOnAdapterChangeListener;
  }
  
  public void setOnPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    this.mOnPageChangeListener = paramOnPageChangeListener;
  }
  
  public void setPageMargin(int paramInt)
  {
    int i = this.mPageMargin;
    this.mPageMargin = paramInt;
    int j = getWidth();
    recomputeScrollPosition(j, j, paramInt, i);
    requestLayout();
  }
  
  public void setPageMarginDrawable(int paramInt)
  {
    setPageMarginDrawable(getContext().getResources().getDrawable(paramInt));
  }
  
  public void setPageMarginDrawable(Drawable paramDrawable)
  {
    this.mMarginDrawable = paramDrawable;
    if (paramDrawable == null) {
      if (paramDrawable == null) {
        break label32;
      }
    }
    label32:
    for (boolean bool = false;; bool = true)
    {
      setWillNotDraw(bool);
      invalidate();
      return;
      refreshDrawableState();
      break;
    }
  }
  
  public void setPageTransformer(boolean paramBoolean, PageTransformer paramPageTransformer)
  {
    int j = 1;
    if (Build.VERSION.SDK_INT < 11) {
      return;
    }
    boolean bool1;
    boolean bool2;
    if (paramPageTransformer == null)
    {
      bool1 = false;
      if (this.mPageTransformer != null) {
        break label74;
      }
      bool2 = false;
      label29:
      if (bool1 != bool2) {
        break label80;
      }
    }
    label59:
    label74:
    label80:
    for (int i = 0;; i = 1)
    {
      this.mPageTransformer = paramPageTransformer;
      setChildrenDrawingOrderEnabledCompat(bool1);
      if (bool1) {
        break label85;
      }
      this.mDrawingOrder = 0;
      if (i == 0) {
        break label96;
      }
      populate();
      return;
      bool1 = true;
      break;
      bool2 = true;
      break label29;
    }
    label85:
    if (!paramBoolean) {}
    for (;;)
    {
      this.mDrawingOrder = j;
      break label59;
      label96:
      break;
      j = 2;
    }
  }
  
  void smoothScrollTo(int paramInt1, int paramInt2)
  {
    smoothScrollTo(paramInt1, paramInt2, 0);
  }
  
  void smoothScrollTo(int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    int j;
    int k;
    float f3;
    float f1;
    float f2;
    if (getChildCount() != 0)
    {
      i = getScrollX();
      j = getScrollY();
      k = paramInt1 - i;
      paramInt2 -= j;
      if (k == 0) {
        break label176;
      }
      setScrollingCacheEnabled(true);
      setScrollState(2);
      paramInt1 = getClientWidth();
      int m = paramInt1 / 2;
      f3 = Math.min(1.0F, Math.abs(k) * 1.0F / paramInt1);
      f1 = m;
      f2 = m;
      f3 = distanceInfluenceForSnapDuration(f3);
      paramInt3 = Math.abs(paramInt3);
      if (paramInt3 > 0) {
        break label195;
      }
      f1 = paramInt1;
      f2 = this.mAdapter.getPageWidth(this.mCurItem);
    }
    label176:
    label195:
    for (paramInt1 = (int)((Math.abs(k) / (f1 * f2 + this.mPageMargin) + 1.0F) * 100.0F);; paramInt1 = Math.round(Math.abs((f2 * f3 + f1) / paramInt3) * 1000.0F) * 4)
    {
      paramInt1 = Math.min(paramInt1, 600);
      this.mScroller.startScroll(i, j, k, paramInt2, paramInt1);
      ViewCompat.postInvalidateOnAnimation(this);
      return;
      setScrollingCacheEnabled(false);
      return;
      if (paramInt2 != 0) {
        break;
      }
      completeScroll(false);
      populate();
      setScrollState(0);
      return;
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if (super.verifyDrawable(paramDrawable)) {}
    while (paramDrawable == this.mMarginDrawable) {
      return true;
    }
    return false;
  }
  
  static abstract interface Decor {}
  
  static class ItemInfo
  {
    Object object;
    float offset;
    int position;
    boolean scrolling;
    float widthFactor;
  }
  
  public static class LayoutParams
    extends ViewGroup.LayoutParams
  {
    int childIndex;
    public int gravity;
    public boolean isDecor;
    boolean needsMeasure;
    int position;
    float widthFactor = 0.0F;
    
    public LayoutParams()
    {
      super(-1);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, ViewPager.LAYOUT_ATTRS);
      this.gravity = paramContext.getInteger(0, 48);
      paramContext.recycle();
    }
  }
  
  class MyAccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    MyAccessibilityDelegate() {}
    
    private boolean canScroll()
    {
      boolean bool = true;
      if (ViewPager.this.mAdapter == null) {}
      for (;;)
      {
        bool = false;
        do
        {
          return bool;
        } while (ViewPager.this.mAdapter.getCount() > 1);
      }
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(ViewPager.class.getName());
      paramView = AccessibilityRecordCompat.obtain();
      paramView.setScrollable(canScroll());
      if (paramAccessibilityEvent.getEventType() != 4096) {}
      while (ViewPager.this.mAdapter == null) {
        return;
      }
      paramView.setItemCount(ViewPager.this.mAdapter.getCount());
      paramView.setFromIndex(ViewPager.this.mCurItem);
      paramView.setToIndex(ViewPager.this.mCurItem);
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      paramAccessibilityNodeInfoCompat.setClassName(ViewPager.class.getName());
      paramAccessibilityNodeInfoCompat.setScrollable(canScroll());
      if (!ViewPager.this.canScrollHorizontally(1)) {}
      while (!ViewPager.this.canScrollHorizontally(-1))
      {
        return;
        paramAccessibilityNodeInfoCompat.addAction(4096);
      }
      paramAccessibilityNodeInfoCompat.addAction(8192);
    }
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      if (!super.performAccessibilityAction(paramView, paramInt, paramBundle)) {}
      switch (paramInt)
      {
      default: 
        return false;
        return true;
      case 4096: 
        if (!ViewPager.this.canScrollHorizontally(1)) {
          return false;
        }
        ViewPager.this.setCurrentItem(ViewPager.this.mCurItem + 1);
        return true;
      }
      if (!ViewPager.this.canScrollHorizontally(-1)) {
        return false;
      }
      ViewPager.this.setCurrentItem(ViewPager.this.mCurItem - 1);
      return true;
    }
  }
  
  static abstract interface OnAdapterChangeListener
  {
    public abstract void onAdapterChanged(PagerAdapter paramPagerAdapter1, PagerAdapter paramPagerAdapter2);
  }
  
  public static abstract interface OnPageChangeListener
  {
    public abstract void onPageScrollStateChanged(int paramInt);
    
    public abstract void onPageScrolled(int paramInt1, float paramFloat, int paramInt2);
    
    public abstract void onPageSelected(int paramInt);
  }
  
  public static abstract interface PageTransformer
  {
    public abstract void transformPage(View paramView, float paramFloat);
  }
  
  private class PagerObserver
    extends DataSetObserver
  {
    private PagerObserver() {}
    
    public void onChanged()
    {
      ViewPager.this.dataSetChanged();
    }
    
    public void onInvalidated()
    {
      ViewPager.this.dataSetChanged();
    }
  }
  
  public static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public ViewPager.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new ViewPager.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public ViewPager.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ViewPager.SavedState[paramAnonymousInt];
      }
    });
    Parcelable adapterState;
    ClassLoader loader;
    int position;
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super();
      if (paramClassLoader != null) {}
      for (;;)
      {
        this.position = paramParcel.readInt();
        this.adapterState = paramParcel.readParcelable(paramClassLoader);
        this.loader = paramClassLoader;
        return;
        paramClassLoader = getClass().getClassLoader();
      }
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.position);
      paramParcel.writeParcelable(this.adapterState, paramInt);
    }
  }
  
  public static class SimpleOnPageChangeListener
    implements ViewPager.OnPageChangeListener
  {
    public void onPageScrollStateChanged(int paramInt) {}
    
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {}
    
    public void onPageSelected(int paramInt) {}
  }
  
  static class ViewPositionComparator
    implements Comparator<View>
  {
    public int compare(View paramView1, View paramView2)
    {
      paramView1 = (ViewPager.LayoutParams)paramView1.getLayoutParams();
      paramView2 = (ViewPager.LayoutParams)paramView2.getLayoutParams();
      if (paramView1.isDecor == paramView2.isDecor) {
        return paramView1.position - paramView2.position;
      }
      if (!paramView1.isDecor) {
        return -1;
      }
      return 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/view/ViewPager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */