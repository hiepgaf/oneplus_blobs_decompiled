package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class DrawerLayout
  extends ViewGroup
  implements DrawerLayoutImpl
{
  private static final boolean ALLOW_EDGE_LOCK = false;
  private static final boolean CAN_HIDE_DESCENDANTS;
  private static final boolean CHILDREN_DISALLOW_INTERCEPT = true;
  private static final int DEFAULT_SCRIM_COLOR = -1728053248;
  static final DrawerLayoutCompatImpl IMPL = new DrawerLayoutCompatImplApi21();
  private static final int[] LAYOUT_ATTRS;
  public static final int LOCK_MODE_LOCKED_CLOSED = 1;
  public static final int LOCK_MODE_LOCKED_OPEN = 2;
  public static final int LOCK_MODE_UNLOCKED = 0;
  private static final int MIN_DRAWER_MARGIN = 64;
  private static final int MIN_FLING_VELOCITY = 400;
  private static final int PEEK_DELAY = 160;
  public static final int STATE_DRAGGING = 1;
  public static final int STATE_IDLE = 0;
  public static final int STATE_SETTLING = 2;
  private static final String TAG = "DrawerLayout";
  private static final float TOUCH_SLOP_SENSITIVITY = 1.0F;
  private final ChildAccessibilityDelegate mChildAccessibilityDelegate = new ChildAccessibilityDelegate();
  private boolean mChildrenCanceledTouch;
  private boolean mDisallowInterceptRequested;
  private boolean mDrawStatusBarBackground;
  private int mDrawerState;
  private boolean mFirstLayout = true;
  private boolean mInLayout;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private Object mLastInsets;
  private final ViewDragCallback mLeftCallback;
  private final ViewDragHelper mLeftDragger;
  private DrawerListener mListener;
  private int mLockModeLeft;
  private int mLockModeRight;
  private int mMinDrawerMargin;
  private final ViewDragCallback mRightCallback;
  private final ViewDragHelper mRightDragger;
  private int mScrimColor = -1728053248;
  private float mScrimOpacity;
  private Paint mScrimPaint = new Paint();
  private Drawable mShadowLeft;
  private Drawable mShadowRight;
  private Drawable mStatusBarBackground;
  private CharSequence mTitleLeft;
  private CharSequence mTitleRight;
  
  static
  {
    boolean bool = false;
    LAYOUT_ATTRS = new int[] { 16842931 };
    if (Build.VERSION.SDK_INT < 19) {}
    for (;;)
    {
      CAN_HIDE_DESCENDANTS = bool;
      if (Build.VERSION.SDK_INT >= 21) {
        break;
      }
      IMPL = new DrawerLayoutCompatImplBase();
      return;
      bool = true;
    }
  }
  
  public DrawerLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DrawerLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public DrawerLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setDescendantFocusability(262144);
    float f = getResources().getDisplayMetrics().density;
    this.mMinDrawerMargin = ((int)(64.0F * f + 0.5F));
    f *= 400.0F;
    this.mLeftCallback = new ViewDragCallback(3);
    this.mRightCallback = new ViewDragCallback(5);
    this.mLeftDragger = ViewDragHelper.create(this, 1.0F, this.mLeftCallback);
    this.mLeftDragger.setEdgeTrackingEnabled(1);
    this.mLeftDragger.setMinVelocity(f);
    this.mLeftCallback.setDragger(this.mLeftDragger);
    this.mRightDragger = ViewDragHelper.create(this, 1.0F, this.mRightCallback);
    this.mRightDragger.setEdgeTrackingEnabled(2);
    this.mRightDragger.setMinVelocity(f);
    this.mRightCallback.setDragger(this.mRightDragger);
    setFocusableInTouchMode(true);
    ViewCompat.setImportantForAccessibility(this, 1);
    ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
    ViewGroupCompat.setMotionEventSplittingEnabled(this, false);
    if (!ViewCompat.getFitsSystemWindows(this)) {
      return;
    }
    IMPL.configureApplyInsets(this);
    this.mStatusBarBackground = IMPL.getDefaultStatusBarBackground(paramContext);
  }
  
  private View findVisibleDrawer()
  {
    int i = 0;
    int j = getChildCount();
    if (i >= j) {
      return null;
    }
    View localView = getChildAt(i);
    if (!isDrawerView(localView)) {}
    while (!isDrawerVisible(localView))
    {
      i += 1;
      break;
    }
    return localView;
  }
  
  static String gravityToString(int paramInt)
  {
    if ((paramInt & 0x3) != 3)
    {
      if ((paramInt & 0x5) != 5) {
        return Integer.toHexString(paramInt);
      }
    }
    else {
      return "LEFT";
    }
    return "RIGHT";
  }
  
  private static boolean hasOpaqueBackground(View paramView)
  {
    paramView = paramView.getBackground();
    if (paramView == null) {
      return false;
    }
    return paramView.getOpacity() == -1;
  }
  
  private boolean hasPeekingDrawer()
  {
    int j = getChildCount();
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return false;
      }
      if (((LayoutParams)getChildAt(i).getLayoutParams()).isPeeking) {
        break;
      }
      i += 1;
    }
    return true;
  }
  
  private boolean hasVisibleDrawer()
  {
    return findVisibleDrawer() != null;
  }
  
  private static boolean includeChildForAccessibility(View paramView)
  {
    if (ViewCompat.getImportantForAccessibility(paramView) == 4) {}
    while (ViewCompat.getImportantForAccessibility(paramView) == 2) {
      return false;
    }
    return true;
  }
  
  private void updateChildrenImportantForAccessibility(View paramView, boolean paramBoolean)
  {
    int i = 0;
    int j = getChildCount();
    if (i >= j) {
      return;
    }
    View localView = getChildAt(i);
    if (paramBoolean) {
      label26:
      if (paramBoolean) {
        break label61;
      }
    }
    for (;;)
    {
      ViewCompat.setImportantForAccessibility(localView, 4);
      i += 1;
      break;
      if (isDrawerView(localView)) {
        break label26;
      }
      label61:
      do
      {
        ViewCompat.setImportantForAccessibility(localView, 1);
        break;
      } while (localView == paramView);
    }
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    super.addView(paramView, paramInt, paramLayoutParams);
    if (findOpenDrawer() != null) {
      ViewCompat.setImportantForAccessibility(paramView, 4);
    }
    for (;;)
    {
      if (!CAN_HIDE_DESCENDANTS) {
        break label42;
      }
      return;
      if (isDrawerView(paramView)) {
        break;
      }
      ViewCompat.setImportantForAccessibility(paramView, 1);
    }
    label42:
    ViewCompat.setAccessibilityDelegate(paramView, this.mChildAccessibilityDelegate);
  }
  
  void cancelChildViewTouch()
  {
    int i = 0;
    if (this.mChildrenCanceledTouch) {
      return;
    }
    long l = SystemClock.uptimeMillis();
    MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
    int j = getChildCount();
    for (;;)
    {
      if (i >= j)
      {
        localMotionEvent.recycle();
        this.mChildrenCanceledTouch = true;
        return;
      }
      getChildAt(i).dispatchTouchEvent(localMotionEvent);
      i += 1;
    }
  }
  
  boolean checkDrawerViewAbsoluteGravity(View paramView, int paramInt)
  {
    return (getDrawerViewAbsoluteGravity(paramView) & paramInt) == paramInt;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (!(paramLayoutParams instanceof LayoutParams)) {}
    while (!super.checkLayoutParams(paramLayoutParams)) {
      return false;
    }
    return true;
  }
  
  public void closeDrawer(int paramInt)
  {
    View localView = findDrawerWithGravity(paramInt);
    if (localView != null)
    {
      closeDrawer(localView);
      return;
    }
    throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(paramInt));
  }
  
  public void closeDrawer(View paramView)
  {
    if (isDrawerView(paramView))
    {
      if (this.mFirstLayout) {
        break label80;
      }
      if (checkDrawerViewAbsoluteGravity(paramView, 3)) {
        break label101;
      }
      this.mRightDragger.smoothSlideViewTo(paramView, getWidth(), paramView.getTop());
    }
    for (;;)
    {
      invalidate();
      return;
      throw new IllegalArgumentException("View " + paramView + " is not a sliding drawer");
      label80:
      paramView = (LayoutParams)paramView.getLayoutParams();
      paramView.onScreen = 0.0F;
      paramView.knownOpen = false;
      continue;
      label101:
      this.mLeftDragger.smoothSlideViewTo(paramView, -paramView.getWidth(), paramView.getTop());
    }
  }
  
  public void closeDrawers()
  {
    closeDrawers(false);
  }
  
  void closeDrawers(boolean paramBoolean)
  {
    int n = getChildCount();
    int j = 0;
    View localView;
    LayoutParams localLayoutParams;
    int k;
    for (int i = 0;; i = k)
    {
      if (j >= n)
      {
        this.mLeftCallback.removeCallbacks();
        this.mRightCallback.removeCallbacks();
        if (i != 0) {
          break label165;
        }
        return;
      }
      localView = getChildAt(j);
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (isDrawerView(localView)) {
        break;
      }
      k = i;
      j += 1;
    }
    label78:
    int m;
    if (!paramBoolean)
    {
      m = localView.getWidth();
      if (checkDrawerViewAbsoluteGravity(localView, 3)) {
        break label142;
      }
      i |= this.mRightDragger.smoothSlideViewTo(localView, getWidth(), localView.getTop());
    }
    for (;;)
    {
      localLayoutParams.isPeeking = false;
      m = i;
      break;
      m = i;
      if (!localLayoutParams.isPeeking) {
        break;
      }
      break label78;
      label142:
      i |= this.mLeftDragger.smoothSlideViewTo(localView, -m, localView.getTop());
    }
    label165:
    invalidate();
  }
  
  public void computeScroll()
  {
    int j = getChildCount();
    float f = 0.0F;
    int i = 0;
    for (;;)
    {
      if (i >= j)
      {
        this.mScrimOpacity = f;
        if ((this.mLeftDragger.continueSettling(true) | this.mRightDragger.continueSettling(true))) {
          break;
        }
        return;
      }
      f = Math.max(f, ((LayoutParams)getChildAt(i).getLayoutParams()).onScreen);
      i += 1;
    }
    ViewCompat.postInvalidateOnAnimation(this);
  }
  
  void dispatchOnDrawerClosed(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (!localLayoutParams.knownOpen) {
      return;
    }
    localLayoutParams.knownOpen = false;
    if (this.mListener == null) {}
    for (;;)
    {
      updateChildrenImportantForAccessibility(paramView, false);
      if (!hasWindowFocus()) {
        break;
      }
      paramView = getRootView();
      if (paramView == null) {
        break;
      }
      paramView.sendAccessibilityEvent(32);
      return;
      this.mListener.onDrawerClosed(paramView);
    }
  }
  
  void dispatchOnDrawerOpened(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams.knownOpen) {
      return;
    }
    localLayoutParams.knownOpen = true;
    if (this.mListener == null)
    {
      updateChildrenImportantForAccessibility(paramView, true);
      if (hasWindowFocus()) {
        break label60;
      }
    }
    for (;;)
    {
      paramView.requestFocus();
      return;
      this.mListener.onDrawerOpened(paramView);
      break;
      label60:
      sendAccessibilityEvent(32);
    }
  }
  
  void dispatchOnDrawerSlide(View paramView, float paramFloat)
  {
    if (this.mListener == null) {
      return;
    }
    this.mListener.onDrawerSlide(paramView, paramFloat);
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    int i1 = getHeight();
    boolean bool1 = isContentView(paramView);
    int i = 0;
    int j = getWidth();
    int n = paramCanvas.save();
    boolean bool2;
    if (!bool1)
    {
      k = 0;
      i = j;
      j = k;
      bool2 = super.drawChild(paramCanvas, paramView, paramLong);
      paramCanvas.restoreToCount(n);
      if ((this.mScrimOpacity > 0.0F) && (bool1)) {
        break label322;
      }
      if (this.mShadowLeft != null) {
        break label390;
      }
      label80:
      if (this.mShadowRight != null) {
        break label487;
      }
    }
    label149:
    label311:
    label322:
    label390:
    label487:
    while (!checkDrawerViewAbsoluteGravity(paramView, 5))
    {
      return bool2;
      int i2 = getChildCount();
      k = 0;
      if (k >= i2)
      {
        paramCanvas.clipRect(i, 0, j, getHeight());
        k = i;
        i = j;
        j = k;
        break;
      }
      View localView = getChildAt(k);
      if (localView == paramView)
      {
        m = j;
        j = i;
        i = m;
      }
      for (;;)
      {
        m = k + 1;
        k = i;
        i = j;
        j = k;
        k = m;
        break;
        if ((localView.getVisibility() != 0) || (!hasOpaqueBackground(localView)) || (!isDrawerView(localView))) {
          break label149;
        }
        if (localView.getHeight() >= i1)
        {
          if (!checkDrawerViewAbsoluteGravity(localView, 3))
          {
            m = localView.getLeft();
            if (m < j) {
              break label311;
            }
            m = i;
            i = j;
            j = m;
          }
        }
        else
        {
          m = i;
          i = j;
          j = m;
          continue;
        }
        m = localView.getRight();
        if (m <= i) {}
        for (;;)
        {
          m = i;
          i = j;
          j = m;
          break;
          i = m;
        }
        j = i;
        i = m;
      }
      k = (int)(((this.mScrimColor & 0xFF000000) >>> 24) * this.mScrimOpacity);
      m = this.mScrimColor;
      this.mScrimPaint.setColor(k << 24 | m & 0xFFFFFF);
      paramCanvas.drawRect(j, 0.0F, i, getHeight(), this.mScrimPaint);
      return bool2;
      if (!checkDrawerViewAbsoluteGravity(paramView, 3)) {
        break label80;
      }
      i = this.mShadowLeft.getIntrinsicWidth();
      j = paramView.getRight();
      k = this.mLeftDragger.getEdgeSize();
      f = Math.max(0.0F, Math.min(j / k, 1.0F));
      this.mShadowLeft.setBounds(j, paramView.getTop(), i + j, paramView.getBottom());
      this.mShadowLeft.setAlpha((int)(255.0F * f));
      this.mShadowLeft.draw(paramCanvas);
      return bool2;
    }
    i = this.mShadowRight.getIntrinsicWidth();
    j = paramView.getLeft();
    int k = getWidth();
    int m = this.mRightDragger.getEdgeSize();
    float f = Math.max(0.0F, Math.min((k - j) / m, 1.0F));
    this.mShadowRight.setBounds(j - i, paramView.getTop(), j, paramView.getBottom());
    this.mShadowRight.setAlpha((int)(255.0F * f));
    this.mShadowRight.draw(paramCanvas);
    return bool2;
  }
  
  View findDrawerWithGravity(int paramInt)
  {
    int i = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    int j = getChildCount();
    paramInt = 0;
    View localView;
    for (;;)
    {
      if (paramInt >= j) {
        return null;
      }
      localView = getChildAt(paramInt);
      if ((getDrawerViewAbsoluteGravity(localView) & 0x7) == (i & 0x7)) {
        break;
      }
      paramInt += 1;
    }
    return localView;
  }
  
  View findOpenDrawer()
  {
    int j = getChildCount();
    int i = 0;
    View localView;
    for (;;)
    {
      if (i >= j) {
        return null;
      }
      localView = getChildAt(i);
      if (((LayoutParams)localView.getLayoutParams()).knownOpen) {
        break;
      }
      i += 1;
    }
    return localView;
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-1, -1);
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (!(paramLayoutParams instanceof LayoutParams))
    {
      if (!(paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
        return new LayoutParams(paramLayoutParams);
      }
    }
    else {
      return new LayoutParams((LayoutParams)paramLayoutParams);
    }
    return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
  }
  
  public int getDrawerLockMode(int paramInt)
  {
    paramInt = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    if (paramInt != 3)
    {
      if (paramInt != 5) {
        return 0;
      }
    }
    else {
      return this.mLockModeLeft;
    }
    return this.mLockModeRight;
  }
  
  public int getDrawerLockMode(View paramView)
  {
    int i = getDrawerViewAbsoluteGravity(paramView);
    if (i != 3)
    {
      if (i != 5) {
        return 0;
      }
    }
    else {
      return this.mLockModeLeft;
    }
    return this.mLockModeRight;
  }
  
  @Nullable
  public CharSequence getDrawerTitle(int paramInt)
  {
    paramInt = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    if (paramInt != 3)
    {
      if (paramInt != 5) {
        return null;
      }
    }
    else {
      return this.mTitleLeft;
    }
    return this.mTitleRight;
  }
  
  int getDrawerViewAbsoluteGravity(View paramView)
  {
    return GravityCompat.getAbsoluteGravity(((LayoutParams)paramView.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(this));
  }
  
  float getDrawerViewOffset(View paramView)
  {
    return ((LayoutParams)paramView.getLayoutParams()).onScreen;
  }
  
  public Drawable getStatusBarBackgroundDrawable()
  {
    return this.mStatusBarBackground;
  }
  
  boolean isContentView(View paramView)
  {
    return ((LayoutParams)paramView.getLayoutParams()).gravity == 0;
  }
  
  public boolean isDrawerOpen(int paramInt)
  {
    View localView = findDrawerWithGravity(paramInt);
    if (localView == null) {
      return false;
    }
    return isDrawerOpen(localView);
  }
  
  public boolean isDrawerOpen(View paramView)
  {
    if (isDrawerView(paramView)) {
      return ((LayoutParams)paramView.getLayoutParams()).knownOpen;
    }
    throw new IllegalArgumentException("View " + paramView + " is not a drawer");
  }
  
  boolean isDrawerView(View paramView)
  {
    return (GravityCompat.getAbsoluteGravity(((LayoutParams)paramView.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(paramView)) & 0x7) != 0;
  }
  
  public boolean isDrawerVisible(int paramInt)
  {
    View localView = findDrawerWithGravity(paramInt);
    if (localView == null) {
      return false;
    }
    return isDrawerVisible(localView);
  }
  
  public boolean isDrawerVisible(View paramView)
  {
    if (isDrawerView(paramView))
    {
      if (((LayoutParams)paramView.getLayoutParams()).onScreen > 0.0F) {
        return true;
      }
    }
    else {
      throw new IllegalArgumentException("View " + paramView + " is not a drawer");
    }
    return false;
  }
  
  void moveDrawerToOffset(View paramView, float paramFloat)
  {
    float f = getDrawerViewOffset(paramView);
    int i = paramView.getWidth();
    int j = (int)(f * i);
    j = (int)(i * paramFloat) - j;
    i = j;
    if (!checkDrawerViewAbsoluteGravity(paramView, 3)) {
      i = -j;
    }
    paramView.offsetLeftAndRight(i);
    setDrawerViewOffset(paramView, paramFloat);
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
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (!this.mDrawStatusBarBackground) {}
    int i;
    do
    {
      do
      {
        return;
      } while (this.mStatusBarBackground == null);
      i = IMPL.getTopInset(this.mLastInsets);
    } while (i <= 0);
    this.mStatusBarBackground.setBounds(0, 0, getWidth(), i);
    this.mStatusBarBackground.draw(paramCanvas);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    boolean bool1 = this.mLeftDragger.shouldInterceptTouchEvent(paramMotionEvent);
    boolean bool2 = this.mRightDragger.shouldInterceptTouchEvent(paramMotionEvent);
    switch (i)
    {
    default: 
      i = 0;
      if (!(bool1 | bool2)) {
        break;
      }
    }
    label118:
    label140:
    while ((i != 0) || (hasPeekingDrawer()) || (this.mChildrenCanceledTouch))
    {
      return true;
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      this.mInitialMotionX = f1;
      this.mInitialMotionY = f2;
      if (this.mScrimOpacity > 0.0F)
      {
        paramMotionEvent = this.mLeftDragger.findTopChildUnder((int)f1, (int)f2);
        if (paramMotionEvent != null) {
          break label140;
        }
        i = 0;
      }
      for (;;)
      {
        this.mDisallowInterceptRequested = false;
        this.mChildrenCanceledTouch = false;
        break;
        i = 0;
        continue;
        if (!isContentView(paramMotionEvent)) {
          break label118;
        }
        i = 1;
      }
      if (!this.mLeftDragger.checkTouchSlop(3))
      {
        i = 0;
        break;
      }
      this.mLeftCallback.removeCallbacks();
      this.mRightCallback.removeCallbacks();
      i = 0;
      break;
      closeDrawers(true);
      this.mDisallowInterceptRequested = false;
      this.mChildrenCanceledTouch = false;
      i = 0;
      break;
    }
    return false;
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt != 4) {}
    while (!hasVisibleDrawer()) {
      return super.onKeyDown(paramInt, paramKeyEvent);
    }
    KeyEventCompat.startTracking(paramKeyEvent);
    return true;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt != 4) {
      return super.onKeyUp(paramInt, paramKeyEvent);
    }
    paramKeyEvent = findVisibleDrawer();
    if (paramKeyEvent == null) {}
    while (paramKeyEvent == null)
    {
      return false;
      if (getDrawerLockMode(paramKeyEvent) == 0) {
        closeDrawers();
      }
    }
    return true;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mInLayout = true;
    int k = paramInt3 - paramInt1;
    int m = getChildCount();
    paramInt3 = 0;
    if (paramInt3 >= m)
    {
      this.mInLayout = false;
      this.mFirstLayout = false;
      return;
    }
    View localView = getChildAt(paramInt3);
    LayoutParams localLayoutParams;
    int n;
    int i1;
    int i;
    float f;
    label126:
    int j;
    if (localView.getVisibility() != 8)
    {
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (isContentView(localView)) {
        break label244;
      }
      n = localView.getMeasuredWidth();
      i1 = localView.getMeasuredHeight();
      if (checkDrawerViewAbsoluteGravity(localView, 3)) {
        break label284;
      }
      i = k - (int)(n * localLayoutParams.onScreen);
      f = (k - i) / n;
      if (f == localLayoutParams.onScreen) {
        break label317;
      }
      j = 1;
      label140:
      switch (localLayoutParams.gravity & 0x70)
      {
      case 48: 
      default: 
        localView.layout(i, localLayoutParams.topMargin, n + i, i1 + localLayoutParams.topMargin);
        label209:
        if (j == 0)
        {
          label214:
          if (localLayoutParams.onScreen <= 0.0F) {
            break label456;
          }
          paramInt1 = 0;
          label226:
          if (localView.getVisibility() != paramInt1) {
            break label461;
          }
        }
        break;
      }
    }
    for (;;)
    {
      paramInt3 += 1;
      break;
      label244:
      localView.layout(localLayoutParams.leftMargin, localLayoutParams.topMargin, localLayoutParams.leftMargin + localView.getMeasuredWidth(), localLayoutParams.topMargin + localView.getMeasuredHeight());
      continue;
      label284:
      paramInt1 = -n;
      i = (int)(n * localLayoutParams.onScreen) + paramInt1;
      f = (n + i) / n;
      break label126;
      label317:
      j = 0;
      break label140;
      paramInt1 = paramInt4 - paramInt2;
      localView.layout(i, paramInt1 - localLayoutParams.bottomMargin - localView.getMeasuredHeight(), n + i, paramInt1 - localLayoutParams.bottomMargin);
      break label209;
      int i2 = paramInt4 - paramInt2;
      paramInt1 = (i2 - i1) / 2;
      if (paramInt1 >= localLayoutParams.topMargin) {
        if (paramInt1 + i1 > i2 - localLayoutParams.bottomMargin) {
          break label430;
        }
      }
      for (;;)
      {
        localView.layout(i, paramInt1, n + i, i1 + paramInt1);
        break;
        paramInt1 = localLayoutParams.topMargin;
        continue;
        label430:
        paramInt1 = i2 - localLayoutParams.bottomMargin - i1;
      }
      setDrawerViewOffset(localView, f);
      break label214;
      label456:
      paramInt1 = 4;
      break label226;
      label461:
      localView.setVisibility(paramInt1);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = 300;
    int n = 0;
    int i2 = View.MeasureSpec.getMode(paramInt1);
    int i1 = View.MeasureSpec.getMode(paramInt2);
    int m = View.MeasureSpec.getSize(paramInt1);
    int k = View.MeasureSpec.getSize(paramInt2);
    if (i2 != 1073741824) {}
    while (!isInEditMode())
    {
      throw new IllegalArgumentException("DrawerLayout must be measured with MeasureSpec.EXACTLY.");
      if (i1 == 1073741824)
      {
        i = k;
        setMeasuredDimension(m, i);
        if (this.mLastInsets != null) {
          break label175;
        }
      }
    }
    label82:
    for (int j = 0;; j = 1)
    {
      i1 = ViewCompat.getLayoutDirection(this);
      i2 = getChildCount();
      k = n;
      if (k < i2) {
        break label188;
      }
      return;
      j = m;
      if (i2 != Integer.MIN_VALUE) {
        if (i2 == 0) {
          break label157;
        }
      }
      label157:
      for (j = m;; j = 300)
      {
        if (i1 == Integer.MIN_VALUE) {
          break label165;
        }
        m = j;
        if (i1 == 0) {
          break;
        }
        i = k;
        m = j;
        break;
      }
      label165:
      i = k;
      m = j;
      break;
      label175:
      if (!ViewCompat.getFitsSystemWindows(this)) {
        break label82;
      }
    }
    label188:
    View localView = getChildAt(k);
    LayoutParams localLayoutParams;
    if (localView.getVisibility() != 8)
    {
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (j == 0) {}
      while (!isContentView(localView))
      {
        if (isDrawerView(localView)) {
          break label402;
        }
        throw new IllegalStateException("Child " + localView + " at index " + k + " does not have a valid layout_gravity - must be Gravity.LEFT, " + "Gravity.RIGHT or Gravity.NO_GRAVITY");
        n = GravityCompat.getAbsoluteGravity(localLayoutParams.gravity, i1);
        if (!ViewCompat.getFitsSystemWindows(localView)) {
          IMPL.applyMarginInsets(localLayoutParams, this.mLastInsets, n);
        } else {
          IMPL.dispatchChildInsets(localView, this.mLastInsets, n);
        }
      }
      localView.measure(View.MeasureSpec.makeMeasureSpec(m - localLayoutParams.leftMargin - localLayoutParams.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(i - localLayoutParams.topMargin - localLayoutParams.bottomMargin, 1073741824));
    }
    for (;;)
    {
      k += 1;
      break;
      label402:
      n = getDrawerViewAbsoluteGravity(localView) & 0x7;
      if ((n & 0x0) != 0) {
        break label473;
      }
      localView.measure(getChildMeasureSpec(paramInt1, this.mMinDrawerMargin + localLayoutParams.leftMargin + localLayoutParams.rightMargin, localLayoutParams.width), getChildMeasureSpec(paramInt2, localLayoutParams.topMargin + localLayoutParams.bottomMargin, localLayoutParams.height));
    }
    label473:
    throw new IllegalStateException("Child drawer has absolute gravity " + gravityToString(n) + " but this " + "DrawerLayout" + " already has a " + "drawer view along that edge");
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    if (paramParcelable.openDrawerGravity == 0) {}
    for (;;)
    {
      setDrawerLockMode(paramParcelable.lockModeLeft, 3);
      setDrawerLockMode(paramParcelable.lockModeRight, 5);
      return;
      View localView = findDrawerWithGravity(paramParcelable.openDrawerGravity);
      if (localView != null) {
        openDrawer(localView);
      }
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    View localView = findOpenDrawer();
    if (localView == null) {}
    for (;;)
    {
      localSavedState.lockModeLeft = this.mLockModeLeft;
      localSavedState.lockModeRight = this.mLockModeRight;
      return localSavedState;
      localSavedState.openDrawerGravity = ((LayoutParams)localView.getLayoutParams()).gravity;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mLeftDragger.processTouchEvent(paramMotionEvent);
    this.mRightDragger.processTouchEvent(paramMotionEvent);
    float f1;
    float f2;
    switch (paramMotionEvent.getAction() & 0xFF)
    {
    case 2: 
    default: 
      return true;
    case 0: 
      f1 = paramMotionEvent.getX();
      f2 = paramMotionEvent.getY();
      this.mInitialMotionX = f1;
      this.mInitialMotionY = f2;
      this.mDisallowInterceptRequested = false;
      this.mChildrenCanceledTouch = false;
      return true;
    case 1: 
      f2 = paramMotionEvent.getX();
      f1 = paramMotionEvent.getY();
      paramMotionEvent = this.mLeftDragger.findTopChildUnder((int)f2, (int)f1);
      boolean bool;
      if (paramMotionEvent == null) {
        bool = true;
      }
      for (;;)
      {
        closeDrawers(bool);
        this.mDisallowInterceptRequested = false;
        return true;
        if (!isContentView(paramMotionEvent)) {
          break;
        }
        f2 -= this.mInitialMotionX;
        f1 -= this.mInitialMotionY;
        int i = this.mLeftDragger.getTouchSlop();
        if (f2 * f2 + f1 * f1 < i * i)
        {
          paramMotionEvent = findOpenDrawer();
          if (paramMotionEvent == null) {
            bool = true;
          }
        }
        else
        {
          bool = true;
          continue;
        }
        if (getDrawerLockMode(paramMotionEvent) != 2) {
          bool = false;
        } else {
          bool = true;
        }
      }
    }
    closeDrawers(true);
    this.mDisallowInterceptRequested = false;
    this.mChildrenCanceledTouch = false;
    return true;
  }
  
  public void openDrawer(int paramInt)
  {
    View localView = findDrawerWithGravity(paramInt);
    if (localView != null)
    {
      openDrawer(localView);
      return;
    }
    throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(paramInt));
  }
  
  public void openDrawer(View paramView)
  {
    if (isDrawerView(paramView))
    {
      if (this.mFirstLayout) {
        break label85;
      }
      if (checkDrawerViewAbsoluteGravity(paramView, 3)) {
        break label112;
      }
      this.mRightDragger.smoothSlideViewTo(paramView, getWidth() - paramView.getWidth(), paramView.getTop());
    }
    for (;;)
    {
      invalidate();
      return;
      throw new IllegalArgumentException("View " + paramView + " is not a sliding drawer");
      label85:
      LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
      localLayoutParams.onScreen = 1.0F;
      localLayoutParams.knownOpen = true;
      updateChildrenImportantForAccessibility(paramView, true);
      continue;
      label112:
      this.mLeftDragger.smoothSlideViewTo(paramView, 0, paramView.getTop());
    }
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    super.requestDisallowInterceptTouchEvent(paramBoolean);
    this.mDisallowInterceptRequested = paramBoolean;
    if (!paramBoolean) {
      return;
    }
    closeDrawers(true);
  }
  
  public void requestLayout()
  {
    if (this.mInLayout) {
      return;
    }
    super.requestLayout();
  }
  
  public void setChildInsets(Object paramObject, boolean paramBoolean)
  {
    boolean bool = false;
    this.mLastInsets = paramObject;
    this.mDrawStatusBarBackground = paramBoolean;
    if (paramBoolean) {
      paramBoolean = bool;
    }
    for (;;)
    {
      setWillNotDraw(paramBoolean);
      requestLayout();
      return;
      paramBoolean = bool;
      if (getBackground() == null) {
        paramBoolean = true;
      }
    }
  }
  
  public void setDrawerListener(DrawerListener paramDrawerListener)
  {
    this.mListener = paramDrawerListener;
  }
  
  public void setDrawerLockMode(int paramInt)
  {
    setDrawerLockMode(paramInt, 3);
    setDrawerLockMode(paramInt, 5);
  }
  
  public void setDrawerLockMode(int paramInt1, int paramInt2)
  {
    paramInt2 = GravityCompat.getAbsoluteGravity(paramInt2, ViewCompat.getLayoutDirection(this));
    if (paramInt2 != 3)
    {
      if (paramInt2 == 5) {
        break label57;
      }
      if (paramInt1 != 0) {
        break label65;
      }
      switch (paramInt1)
      {
      }
    }
    label57:
    label65:
    Object localObject;
    do
    {
      do
      {
        return;
        this.mLockModeLeft = paramInt1;
        break;
        this.mLockModeRight = paramInt1;
        break;
        if (paramInt2 != 3) {}
        for (localObject = this.mRightDragger;; localObject = this.mLeftDragger)
        {
          ((ViewDragHelper)localObject).cancel();
          break;
        }
        localObject = findDrawerWithGravity(paramInt2);
      } while (localObject == null);
      openDrawer((View)localObject);
      return;
      localObject = findDrawerWithGravity(paramInt2);
    } while (localObject == null);
    closeDrawer((View)localObject);
  }
  
  public void setDrawerLockMode(int paramInt, View paramView)
  {
    if (isDrawerView(paramView))
    {
      setDrawerLockMode(paramInt, ((LayoutParams)paramView.getLayoutParams()).gravity);
      return;
    }
    throw new IllegalArgumentException("View " + paramView + " is not a " + "drawer with appropriate layout_gravity");
  }
  
  public void setDrawerShadow(int paramInt1, int paramInt2)
  {
    setDrawerShadow(getResources().getDrawable(paramInt1), paramInt2);
  }
  
  public void setDrawerShadow(Drawable paramDrawable, int paramInt)
  {
    paramInt = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    if ((paramInt & 0x3) != 3) {}
    while ((paramInt & 0x5) != 5)
    {
      return;
      this.mShadowLeft = paramDrawable;
      invalidate();
    }
    this.mShadowRight = paramDrawable;
    invalidate();
  }
  
  public void setDrawerTitle(int paramInt, CharSequence paramCharSequence)
  {
    paramInt = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    if (paramInt != 3)
    {
      if (paramInt == 5) {}
    }
    else
    {
      this.mTitleLeft = paramCharSequence;
      return;
    }
    this.mTitleRight = paramCharSequence;
  }
  
  void setDrawerViewOffset(View paramView, float paramFloat)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (paramFloat == localLayoutParams.onScreen) {
      return;
    }
    localLayoutParams.onScreen = paramFloat;
    dispatchOnDrawerSlide(paramView, paramFloat);
  }
  
  public void setScrimColor(int paramInt)
  {
    this.mScrimColor = paramInt;
    invalidate();
  }
  
  public void setStatusBarBackground(int paramInt)
  {
    if (paramInt == 0) {}
    for (Drawable localDrawable = null;; localDrawable = ContextCompat.getDrawable(getContext(), paramInt))
    {
      this.mStatusBarBackground = localDrawable;
      return;
    }
  }
  
  public void setStatusBarBackground(Drawable paramDrawable)
  {
    this.mStatusBarBackground = paramDrawable;
  }
  
  public void setStatusBarBackgroundColor(int paramInt)
  {
    this.mStatusBarBackground = new ColorDrawable(paramInt);
  }
  
  void updateDrawerState(int paramInt1, int paramInt2, View paramView)
  {
    int i = 2;
    int j = this.mLeftDragger.getViewDragState();
    int k = this.mRightDragger.getViewDragState();
    if (j == 1) {}
    label29:
    label41:
    label120:
    do
    {
      break label41;
      paramInt1 = 1;
      if (paramView == null) {}
      for (;;)
      {
        if (paramInt1 != this.mDrawerState) {
          break label120;
        }
        return;
        if (k == 1) {
          break;
        }
        paramInt1 = i;
        if (j == 2) {
          break label29;
        }
        paramInt1 = i;
        if (k == 2) {
          break label29;
        }
        paramInt1 = 0;
        break label29;
        if (paramInt2 == 0)
        {
          LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
          if (localLayoutParams.onScreen == 0.0F) {
            dispatchOnDrawerClosed(paramView);
          } else if (localLayoutParams.onScreen == 1.0F) {
            dispatchOnDrawerOpened(paramView);
          }
        }
      }
      this.mDrawerState = paramInt1;
    } while (this.mListener == null);
    this.mListener.onDrawerStateChanged(paramInt1);
  }
  
  class AccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    private final Rect mTmpRect = new Rect();
    
    AccessibilityDelegate() {}
    
    private void addChildrenForAccessibility(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat, ViewGroup paramViewGroup)
    {
      int i = 0;
      int j = paramViewGroup.getChildCount();
      if (i >= j) {
        return;
      }
      View localView = paramViewGroup.getChildAt(i);
      if (!DrawerLayout.includeChildForAccessibility(localView)) {}
      for (;;)
      {
        i += 1;
        break;
        paramAccessibilityNodeInfoCompat.addChild(localView);
      }
    }
    
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
    }
    
    public boolean dispatchPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      if (paramAccessibilityEvent.getEventType() != 32) {
        return super.dispatchPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
      }
      paramView = paramAccessibilityEvent.getText();
      paramAccessibilityEvent = DrawerLayout.this.findVisibleDrawer();
      if (paramAccessibilityEvent == null) {}
      for (;;)
      {
        return true;
        int i = DrawerLayout.this.getDrawerViewAbsoluteGravity(paramAccessibilityEvent);
        paramAccessibilityEvent = DrawerLayout.this.getDrawerTitle(i);
        if (paramAccessibilityEvent != null) {
          paramView.add(paramAccessibilityEvent);
        }
      }
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(DrawerLayout.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat;
      ViewParent localViewParent;
      if (!DrawerLayout.CAN_HIDE_DESCENDANTS)
      {
        localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain(paramAccessibilityNodeInfoCompat);
        super.onInitializeAccessibilityNodeInfo(paramView, localAccessibilityNodeInfoCompat);
        paramAccessibilityNodeInfoCompat.setSource(paramView);
        localViewParent = ViewCompat.getParentForAccessibility(paramView);
        if ((localViewParent instanceof View)) {
          break label84;
        }
      }
      for (;;)
      {
        copyNodeInfoNoChildren(paramAccessibilityNodeInfoCompat, localAccessibilityNodeInfoCompat);
        localAccessibilityNodeInfoCompat.recycle();
        addChildrenForAccessibility(paramAccessibilityNodeInfoCompat, (ViewGroup)paramView);
        for (;;)
        {
          paramAccessibilityNodeInfoCompat.setClassName(DrawerLayout.class.getName());
          paramAccessibilityNodeInfoCompat.setFocusable(false);
          paramAccessibilityNodeInfoCompat.setFocused(false);
          return;
          super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
        }
        label84:
        paramAccessibilityNodeInfoCompat.setParent((View)localViewParent);
      }
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      if (DrawerLayout.CAN_HIDE_DESCENDANTS) {}
      while (DrawerLayout.includeChildForAccessibility(paramView)) {
        return super.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
      }
      return false;
    }
  }
  
  final class ChildAccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    ChildAccessibilityDelegate() {}
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      if (DrawerLayout.includeChildForAccessibility(paramView)) {
        return;
      }
      paramAccessibilityNodeInfoCompat.setParent(null);
    }
  }
  
  static abstract interface DrawerLayoutCompatImpl
  {
    public abstract void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt);
    
    public abstract void configureApplyInsets(View paramView);
    
    public abstract void dispatchChildInsets(View paramView, Object paramObject, int paramInt);
    
    public abstract Drawable getDefaultStatusBarBackground(Context paramContext);
    
    public abstract int getTopInset(Object paramObject);
  }
  
  static class DrawerLayoutCompatImplApi21
    implements DrawerLayout.DrawerLayoutCompatImpl
  {
    public void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt)
    {
      DrawerLayoutCompatApi21.applyMarginInsets(paramMarginLayoutParams, paramObject, paramInt);
    }
    
    public void configureApplyInsets(View paramView)
    {
      DrawerLayoutCompatApi21.configureApplyInsets(paramView);
    }
    
    public void dispatchChildInsets(View paramView, Object paramObject, int paramInt)
    {
      DrawerLayoutCompatApi21.dispatchChildInsets(paramView, paramObject, paramInt);
    }
    
    public Drawable getDefaultStatusBarBackground(Context paramContext)
    {
      return DrawerLayoutCompatApi21.getDefaultStatusBarBackground(paramContext);
    }
    
    public int getTopInset(Object paramObject)
    {
      return DrawerLayoutCompatApi21.getTopInset(paramObject);
    }
  }
  
  static class DrawerLayoutCompatImplBase
    implements DrawerLayout.DrawerLayoutCompatImpl
  {
    public void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt) {}
    
    public void configureApplyInsets(View paramView) {}
    
    public void dispatchChildInsets(View paramView, Object paramObject, int paramInt) {}
    
    public Drawable getDefaultStatusBarBackground(Context paramContext)
    {
      return null;
    }
    
    public int getTopInset(Object paramObject)
    {
      return 0;
    }
  }
  
  public static abstract interface DrawerListener
  {
    public abstract void onDrawerClosed(View paramView);
    
    public abstract void onDrawerOpened(View paramView);
    
    public abstract void onDrawerSlide(View paramView, float paramFloat);
    
    public abstract void onDrawerStateChanged(int paramInt);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({3L, 5L, 8388611L, 8388613L})
  private static @interface EdgeGravity {}
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    public int gravity = 0;
    boolean isPeeking;
    boolean knownOpen;
    float onScreen;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      this(paramInt1, paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, DrawerLayout.LAYOUT_ATTRS);
      this.gravity = paramContext.getInt(0, 0);
      paramContext.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.gravity = paramLayoutParams.gravity;
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
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L})
  private static @interface LockMode {}
  
  protected static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public DrawerLayout.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DrawerLayout.SavedState(paramAnonymousParcel);
      }
      
      public DrawerLayout.SavedState[] newArray(int paramAnonymousInt)
      {
        return new DrawerLayout.SavedState[paramAnonymousInt];
      }
    };
    int lockModeLeft = 0;
    int lockModeRight = 0;
    int openDrawerGravity = 0;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.openDrawerGravity = paramParcel.readInt();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.openDrawerGravity);
    }
  }
  
  public static abstract class SimpleDrawerListener
    implements DrawerLayout.DrawerListener
  {
    public void onDrawerClosed(View paramView) {}
    
    public void onDrawerOpened(View paramView) {}
    
    public void onDrawerSlide(View paramView, float paramFloat) {}
    
    public void onDrawerStateChanged(int paramInt) {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L})
  private static @interface State {}
  
  private class ViewDragCallback
    extends ViewDragHelper.Callback
  {
    private final int mAbsGravity;
    private ViewDragHelper mDragger;
    private final Runnable mPeekRunnable = new Runnable()
    {
      public void run()
      {
        DrawerLayout.ViewDragCallback.this.peekDrawer();
      }
    };
    
    public ViewDragCallback(int paramInt)
    {
      this.mAbsGravity = paramInt;
    }
    
    private void closeOtherDrawer()
    {
      int i = 3;
      if (this.mAbsGravity != 3) {}
      View localView;
      for (;;)
      {
        localView = DrawerLayout.this.findDrawerWithGravity(i);
        if (localView != null) {
          break;
        }
        return;
        i = 5;
      }
      DrawerLayout.this.closeDrawer(localView);
    }
    
    private void peekDrawer()
    {
      int j = 0;
      int k = this.mDragger.getEdgeSize();
      int i;
      View localView;
      if (this.mAbsGravity != 3)
      {
        i = 0;
        if (i != 0) {
          break label55;
        }
        localView = DrawerLayout.this.findDrawerWithGravity(5);
        j = DrawerLayout.this.getWidth() - k;
        if (localView != null) {
          break label87;
        }
        return;
        break label91;
      }
      label55:
      label87:
      label91:
      label177:
      for (;;)
      {
        i = 1;
        break;
        localView = DrawerLayout.this.findDrawerWithGravity(3);
        if (localView == null) {}
        for (;;)
        {
          j += k;
          break;
          j = -localView.getWidth();
        }
        if (i == 0)
        {
          if ((i != 0) || (localView.getLeft() <= j)) {}
        }
        else {
          for (;;)
          {
            if (DrawerLayout.this.getDrawerLockMode(localView) != 0) {
              break label177;
            }
            DrawerLayout.LayoutParams localLayoutParams = (DrawerLayout.LayoutParams)localView.getLayoutParams();
            this.mDragger.smoothSlideViewTo(localView, j, localView.getTop());
            localLayoutParams.isPeeking = true;
            DrawerLayout.this.invalidate();
            closeOtherDrawer();
            DrawerLayout.this.cancelChildViewTouch();
            return;
            if (localView.getLeft() >= j) {
              break;
            }
          }
        }
      }
    }
    
    public int clampViewPositionHorizontal(View paramView, int paramInt1, int paramInt2)
    {
      if (!DrawerLayout.this.checkDrawerViewAbsoluteGravity(paramView, 3))
      {
        paramInt2 = DrawerLayout.this.getWidth();
        return Math.max(paramInt2 - paramView.getWidth(), Math.min(paramInt1, paramInt2));
      }
      return Math.max(-paramView.getWidth(), Math.min(paramInt1, 0));
    }
    
    public int clampViewPositionVertical(View paramView, int paramInt1, int paramInt2)
    {
      return paramView.getTop();
    }
    
    public int getViewHorizontalDragRange(View paramView)
    {
      if (!DrawerLayout.this.isDrawerView(paramView)) {
        return 0;
      }
      return paramView.getWidth();
    }
    
    public void onEdgeDragStarted(int paramInt1, int paramInt2)
    {
      View localView;
      if ((paramInt1 & 0x1) != 1)
      {
        localView = DrawerLayout.this.findDrawerWithGravity(5);
        if (localView != null) {
          break label33;
        }
      }
      label33:
      while (DrawerLayout.this.getDrawerLockMode(localView) != 0)
      {
        return;
        localView = DrawerLayout.this.findDrawerWithGravity(3);
        break;
      }
      this.mDragger.captureChildView(localView, paramInt2);
    }
    
    public boolean onEdgeLock(int paramInt)
    {
      return false;
    }
    
    public void onEdgeTouched(int paramInt1, int paramInt2)
    {
      DrawerLayout.this.postDelayed(this.mPeekRunnable, 160L);
    }
    
    public void onViewCaptured(View paramView, int paramInt)
    {
      ((DrawerLayout.LayoutParams)paramView.getLayoutParams()).isPeeking = false;
      closeOtherDrawer();
    }
    
    public void onViewDragStateChanged(int paramInt)
    {
      DrawerLayout.this.updateDrawerState(this.mAbsGravity, paramInt, this.mDragger.getCapturedView());
    }
    
    public void onViewPositionChanged(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt2 = paramView.getWidth();
      float f;
      if (!DrawerLayout.this.checkDrawerViewAbsoluteGravity(paramView, 3))
      {
        f = (DrawerLayout.this.getWidth() - paramInt1) / paramInt2;
        DrawerLayout.this.setDrawerViewOffset(paramView, f);
        if (f != 0.0F) {
          break label76;
        }
      }
      label76:
      for (paramInt1 = 4;; paramInt1 = 0)
      {
        paramView.setVisibility(paramInt1);
        DrawerLayout.this.invalidate();
        return;
        f = (paramInt2 + paramInt1) / paramInt2;
        break;
      }
    }
    
    public void onViewReleased(View paramView, float paramFloat1, float paramFloat2)
    {
      int j = 1;
      int k = 0;
      int i = 0;
      paramFloat2 = DrawerLayout.this.getDrawerViewOffset(paramView);
      int m = paramView.getWidth();
      if (!DrawerLayout.this.checkDrawerViewAbsoluteGravity(paramView, 3))
      {
        j = DrawerLayout.this.getWidth();
        if (paramFloat1 < 0.0F) {
          i = 1;
        }
        if ((i == 0) && ((paramFloat1 != 0.0F) || (paramFloat2 <= 0.5F))) {
          break label147;
        }
        i = j - m;
      }
      for (;;)
      {
        this.mDragger.settleCapturedViewAt(i, paramView.getTop());
        DrawerLayout.this.invalidate();
        return;
        if (paramFloat1 > 0.0F) {}
        for (;;)
        {
          i = k;
          if (j != 0) {
            break;
          }
          if (paramFloat1 == 0.0F)
          {
            i = k;
            if (paramFloat2 > 0.5F) {
              break;
            }
          }
          i = -m;
          break;
          j = 0;
        }
        label147:
        i = j;
      }
    }
    
    public void removeCallbacks()
    {
      DrawerLayout.this.removeCallbacks(this.mPeekRunnable);
    }
    
    public void setDragger(ViewDragHelper paramViewDragHelper)
    {
      this.mDragger = paramViewDragHelper;
    }
    
    public boolean tryCaptureView(View paramView, int paramInt)
    {
      if (!DrawerLayout.this.isDrawerView(paramView)) {}
      while ((!DrawerLayout.this.checkDrawerViewAbsoluteGravity(paramView, this.mAbsGravity)) || (DrawerLayout.this.getDrawerLockMode(paramView) != 0)) {
        return false;
      }
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/widget/DrawerLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */