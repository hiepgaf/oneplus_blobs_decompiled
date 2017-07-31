package com.oneplus.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.oneplus.base.Log;
import java.util.ArrayList;
import java.util.List;

public class ImageViewer
  extends View
{
  private static final float AUTO_SCROLLING_ACCELERATION_BOUNCING = 80000.0F;
  private static final float AUTO_SCROLLING_ACCELERATION_DEFAULT = 14000.0F;
  private static final float BOUNCING_RATIO = 0.5F;
  private static final int DEBUG_FRAME_COLOR_ADJUSTED_BOUNDS = -65536;
  private static final int DEBUG_FRAME_COLOR_CURRENT_BOUNDS = -16711936;
  private static final int DEBUG_FRAME_COLOR_TARGET_BOUNDS = -256;
  private static final float DEBUG_FRAME_WIDTH = 6.0F;
  protected static final boolean DRAW_DEBUG_FRAMES = false;
  private static final long DURATION_IMAGE_BOUNDS_ANIMATION = 350L;
  public static final int GESTURE_FLAG_ALL = Integer.MAX_VALUE;
  public static final int GESTURE_FLAG_DOUBLE_TAP = 1;
  public static final int GESTURE_FLAG_SCALE = 8;
  public static final int GESTURE_FLAG_SCROLL = 2;
  public static final int GESTURE_FLAG_SCROLL_BY_MULTI_TOUCH = 4;
  private static final Interpolator IMAGE_BOUNDS_ANIMATION_INTERPOLATOR = new DecelerateInterpolator(2.0F);
  private static final float MIN_AUTO_SCROLLING_SPEED_VALUE = 10.0F;
  protected static final boolean PRINT_DEBUG_LOGS = false;
  private static final float SCALING_SPEED_RATIO = 1.0F;
  protected final String TAG = getClass().getSimpleName();
  private final Rect m_AdjustedUserImageBounds = new Rect();
  private final Runnable m_AnimateImageBoundsRunnable = new Runnable()
  {
    public void run()
    {
      ImageViewer.-set1(ImageViewer.this, false);
      ImageViewer.-wrap0(ImageViewer.this);
    }
  };
  private final Runnable m_AutoScrollingRunnable = new Runnable()
  {
    public void run()
    {
      ImageViewer.-set0(ImageViewer.this, false);
      ImageViewer.-wrap1(ImageViewer.this);
    }
  };
  private float m_AutoScrollingSpeedX;
  private float m_AutoScrollingSpeedXRatio;
  private float m_AutoScrollingSpeedY;
  private float m_AutoScrollingSpeedYRatio;
  private final Rect m_CurrentImageBounds = new Rect();
  private Paint m_DebugFramePaint;
  private int m_DisabledGestureFlags;
  private boolean m_FitToView = false;
  private final List<GestureCallback> m_GestureCallbacks = new ArrayList();
  private final GestureDetector m_GestureDetector = new GestureDetector(paramContext, new GestureDetector.OnGestureListener()
  {
    public boolean onDown(MotionEvent paramAnonymousMotionEvent)
    {
      return false;
    }
    
    public boolean onFling(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      return ImageViewer.this.onGestureFling(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, paramAnonymousFloat1, paramAnonymousFloat2, true);
    }
    
    public void onLongPress(MotionEvent paramAnonymousMotionEvent)
    {
      ImageViewer.this.onLongPress(paramAnonymousMotionEvent);
    }
    
    public boolean onScroll(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      return ImageViewer.this.onGestureScroll(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, paramAnonymousFloat1, paramAnonymousFloat2);
    }
    
    public void onShowPress(MotionEvent paramAnonymousMotionEvent) {}
    
    public boolean onSingleTapUp(MotionEvent paramAnonymousMotionEvent)
    {
      return ImageViewer.this.onSingleTapUp(paramAnonymousMotionEvent);
    }
  });
  private long m_ImageBoundsAnimationStartTime;
  private ImageBoundsType m_ImageBoundsType = ImageBoundsType.FIT_TO_VIEW;
  private Drawable m_ImageDrawable;
  private int m_ImageHeight;
  private int m_ImageWidth;
  private boolean m_IsAutoScrollingBouncingBottom;
  private boolean m_IsAutoScrollingBouncingLeft;
  private boolean m_IsAutoScrollingBouncingRight;
  private boolean m_IsAutoScrollingBouncingTop;
  private boolean m_IsAutoScrollingScheduled;
  private boolean m_IsAutoScrollingX;
  private boolean m_IsAutoScrollingY;
  private boolean m_IsBouncingEnabled = true;
  private boolean m_IsImageBoundsAnimationEnabled = true;
  private boolean m_IsImageBoundsAnimationScheduled;
  private boolean m_IsMovingByUser;
  private boolean m_IsScalingByUser;
  private boolean m_IsTouchEventCancelled;
  private long m_LastAutoScrollingTime;
  private float m_MaxRatio = 4.0F;
  private View.OnTouchListener m_OnTouchListener;
  private Drawable m_OverlayDrawable;
  private final ScaleGestureDetector m_ScaleGestureDetector;
  private final Rect m_SourceImageBounds = new Rect();
  private final List<StateCallback> m_StateCallbacks = new ArrayList();
  private final Rect m_TargetImageBounds = new Rect();
  
  public ImageViewer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.m_GestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener()
    {
      public boolean onDoubleTap(MotionEvent paramAnonymousMotionEvent)
      {
        return ImageViewer.this.onDoubleTap(paramAnonymousMotionEvent);
      }
      
      public boolean onDoubleTapEvent(MotionEvent paramAnonymousMotionEvent)
      {
        return false;
      }
      
      public boolean onSingleTapConfirmed(MotionEvent paramAnonymousMotionEvent)
      {
        return ImageViewer.this.onSingleTapConfirmed(paramAnonymousMotionEvent);
      }
    });
    this.m_ScaleGestureDetector = new ScaleGestureDetector(paramContext, new ScaleGestureDetector.OnScaleGestureListener()
    {
      private float m_PrevFocusX;
      private float m_PrevFocusY;
      
      public boolean onScale(ScaleGestureDetector paramAnonymousScaleGestureDetector)
      {
        float f1 = paramAnonymousScaleGestureDetector.getFocusX();
        float f2 = paramAnonymousScaleGestureDetector.getFocusY();
        float f3 = this.m_PrevFocusX;
        float f4 = this.m_PrevFocusY;
        this.m_PrevFocusX = f1;
        this.m_PrevFocusY = f2;
        return ImageViewer.this.onGestureScale(paramAnonymousScaleGestureDetector.getScaleFactor(), f1, f2, f1 - f3, f2 - f4);
      }
      
      public boolean onScaleBegin(ScaleGestureDetector paramAnonymousScaleGestureDetector)
      {
        if (ImageViewer.this.onGestureScaleBegin())
        {
          this.m_PrevFocusX = paramAnonymousScaleGestureDetector.getFocusX();
          this.m_PrevFocusY = paramAnonymousScaleGestureDetector.getFocusY();
          return true;
        }
        return false;
      }
      
      public void onScaleEnd(ScaleGestureDetector paramAnonymousScaleGestureDetector)
      {
        ImageViewer.this.onGestureScaleEnd();
      }
    });
    this.m_ScaleGestureDetector.setQuickScaleEnabled(false);
  }
  
  private void animateImageBounds()
  {
    if (this.m_ImageBoundsAnimationStartTime <= 0L) {
      return;
    }
    long l = SystemClock.elapsedRealtime() - this.m_ImageBoundsAnimationStartTime;
    int i;
    int j;
    if (l >= 350L)
    {
      i = 1;
      j = i;
      if (i == 0)
      {
        float f = IMAGE_BOUNDS_ANIMATION_INTERPOLATOR.getInterpolation((float)l / 350.0F);
        this.m_CurrentImageBounds.left = Math.round(this.m_SourceImageBounds.left + (this.m_TargetImageBounds.left - this.m_SourceImageBounds.left) * f);
        this.m_CurrentImageBounds.top = Math.round(this.m_SourceImageBounds.top + (this.m_TargetImageBounds.top - this.m_SourceImageBounds.top) * f);
        this.m_CurrentImageBounds.right = Math.round(this.m_SourceImageBounds.right + (this.m_TargetImageBounds.right - this.m_SourceImageBounds.right) * f);
        this.m_CurrentImageBounds.bottom = Math.round(this.m_SourceImageBounds.bottom + (this.m_TargetImageBounds.bottom - this.m_SourceImageBounds.bottom) * f);
        changeCurrentImageBounds(this.m_CurrentImageBounds);
        onImageBoundsAnimate(this.m_ImageBoundsAnimationStartTime, l, 350L, f);
        if (!this.m_CurrentImageBounds.equals(this.m_TargetImageBounds)) {
          break label272;
        }
        j = 1;
      }
    }
    for (;;)
    {
      if (j != 0)
      {
        this.m_ImageBoundsAnimationStartTime = 0L;
        this.m_CurrentImageBounds.set(this.m_TargetImageBounds);
        onImageBoundsAnimationCompleted();
      }
      invalidate();
      return;
      i = 0;
      break;
      label272:
      j = i;
      if (!scheduleImageBoundsAnimation()) {
        j = 1;
      }
    }
  }
  
  private void autoScrolling()
  {
    int i;
    int j;
    int k;
    int m;
    float f2;
    float f3;
    float f1;
    float f4;
    if ((this.m_LastAutoScrollingTime > 0L) && ((this.m_IsAutoScrollingX) || (this.m_IsAutoScrollingY)))
    {
      i = getPaddingLeft();
      j = getPaddingTop();
      k = getWidth() - getPaddingRight();
      m = getHeight() - getPaddingBottom();
      long l = SystemClock.elapsedRealtime();
      float f5 = (float)(l - this.m_LastAutoScrollingTime) / 1000.0F;
      if (!this.m_IsAutoScrollingX) {
        this.m_AutoScrollingSpeedX = 0.0F;
      }
      if (!this.m_IsAutoScrollingY) {
        this.m_AutoScrollingSpeedY = 0.0F;
      }
      f2 = 0.0F;
      f3 = 0.0F;
      if (this.m_IsAutoScrollingX)
      {
        f1 = 0.0F;
        if (!this.m_IsAutoScrollingBouncingLeft) {
          break label584;
        }
        if (this.m_TargetImageBounds.left <= this.m_AdjustedUserImageBounds.left) {
          break label571;
        }
        f2 = -80000.0F;
        f1 = f2;
        if (this.m_TargetImageBounds.left >= (i + k) / 2)
        {
          f1 = f2;
          if (this.m_AutoScrollingSpeedX > 0.0F)
          {
            this.m_AutoScrollingSpeedX = 0.0F;
            f1 = f2;
          }
        }
        f4 = this.m_AutoScrollingSpeedX * f5 + 0.5F * f1 * f5 * f5;
        this.m_AutoScrollingSpeedX += f1 * f5;
        f2 = f4;
        if (!this.m_IsAutoScrollingBouncingLeft)
        {
          if (!this.m_IsAutoScrollingBouncingRight) {
            break label717;
          }
          f2 = f4;
        }
      }
      label231:
      if (this.m_IsAutoScrollingY)
      {
        f1 = 0.0F;
        if (!this.m_IsAutoScrollingBouncingTop) {
          break label820;
        }
        if (this.m_TargetImageBounds.top <= this.m_AdjustedUserImageBounds.top) {
          break label785;
        }
        f3 = -80000.0F;
        f1 = f3;
        if (this.m_TargetImageBounds.top >= (j + m) / 2)
        {
          f1 = f3;
          if (this.m_AutoScrollingSpeedY > 0.0F)
          {
            this.m_AutoScrollingSpeedY = 0.0F;
            f1 = f3;
          }
        }
        label305:
        f4 = this.m_AutoScrollingSpeedY * f5 + 0.5F * f1 * f5 * f5;
        this.m_AutoScrollingSpeedY += f1 * f5;
        f3 = f4;
        if (!this.m_IsAutoScrollingBouncingTop)
        {
          if (!this.m_IsAutoScrollingBouncingBottom) {
            break label953;
          }
          f3 = f4;
        }
      }
      label358:
      this.m_LastAutoScrollingTime = l;
      this.m_TargetImageBounds.set(this.m_CurrentImageBounds);
      this.m_TargetImageBounds.offset(Math.round(f2), Math.round(f3));
      if (!this.m_IsAutoScrollingBouncingLeft) {
        break label1021;
      }
      if (this.m_TargetImageBounds.left <= this.m_AdjustedUserImageBounds.left)
      {
        this.m_TargetImageBounds.offset(this.m_AdjustedUserImageBounds.left - this.m_TargetImageBounds.left, 0);
        this.m_IsAutoScrollingX = false;
      }
      label442:
      if (!this.m_IsAutoScrollingBouncingTop) {
        break label1076;
      }
      if (this.m_TargetImageBounds.top <= this.m_AdjustedUserImageBounds.top)
      {
        this.m_TargetImageBounds.offset(0, this.m_AdjustedUserImageBounds.top - this.m_TargetImageBounds.top);
        this.m_IsAutoScrollingY = false;
      }
      label494:
      calculateAdjustedUserImageBounds(this.m_TargetImageBounds, this.m_AdjustedUserImageBounds);
      if ((!this.m_IsAutoScrollingBouncingLeft) && (!this.m_IsAutoScrollingBouncingRight)) {
        break label1131;
      }
      label520:
      if ((!this.m_IsAutoScrollingBouncingTop) && (!this.m_IsAutoScrollingBouncingBottom)) {
        break label1181;
      }
    }
    for (;;)
    {
      if (!scheduleAutoScrolling())
      {
        this.m_IsAutoScrollingX = false;
        this.m_IsAutoScrollingY = false;
      }
      moveImageBoundsByUser();
      if ((!this.m_IsAutoScrollingX) && (!this.m_IsAutoScrollingY)) {
        break label1231;
      }
      return;
      return;
      label571:
      this.m_IsAutoScrollingBouncingLeft = false;
      this.m_IsAutoScrollingX = false;
      break;
      label584:
      if (this.m_IsAutoScrollingBouncingRight)
      {
        if (this.m_TargetImageBounds.right < this.m_AdjustedUserImageBounds.right)
        {
          f2 = 80000.0F;
          f1 = f2;
          if (this.m_TargetImageBounds.right > (i + k) / 2) {
            break;
          }
          f1 = f2;
          if (this.m_AutoScrollingSpeedX >= 0.0F) {
            break;
          }
          this.m_AutoScrollingSpeedX = 0.0F;
          f1 = f2;
          break;
        }
        this.m_IsAutoScrollingBouncingRight = false;
        this.m_IsAutoScrollingX = false;
        break;
      }
      if (Math.abs(this.m_AutoScrollingSpeedX) < 10.0F)
      {
        this.m_IsAutoScrollingX = false;
        break;
      }
      if (this.m_AutoScrollingSpeedX > 0.0F)
      {
        f1 = -14000.0F * this.m_AutoScrollingSpeedXRatio;
        break;
      }
      f1 = 14000.0F * this.m_AutoScrollingSpeedXRatio;
      break;
      label717:
      if ((f1 < 0.0F) && (this.m_AutoScrollingSpeedX <= 0.0F))
      {
        this.m_IsAutoScrollingX = false;
        this.m_AutoScrollingSpeedX = 0.0F;
        f2 = f4;
        break label231;
      }
      f2 = f4;
      if (f1 <= 0.0F) {
        break label231;
      }
      f2 = f4;
      if (this.m_AutoScrollingSpeedX < 0.0F) {
        break label231;
      }
      this.m_IsAutoScrollingX = false;
      this.m_AutoScrollingSpeedX = 0.0F;
      f2 = f4;
      break label231;
      label785:
      f3 = this.m_AdjustedUserImageBounds.top - this.m_CurrentImageBounds.top;
      this.m_AutoScrollingSpeedY = 0.0F;
      this.m_IsAutoScrollingBouncingTop = false;
      this.m_IsAutoScrollingY = false;
      break label305;
      label820:
      if (this.m_IsAutoScrollingBouncingBottom)
      {
        if (this.m_TargetImageBounds.bottom < this.m_AdjustedUserImageBounds.bottom)
        {
          f3 = 80000.0F;
          f1 = f3;
          if (this.m_TargetImageBounds.bottom > (j + m) / 2) {
            break label305;
          }
          f1 = f3;
          if (this.m_AutoScrollingSpeedY >= 0.0F) {
            break label305;
          }
          this.m_AutoScrollingSpeedY = 0.0F;
          f1 = f3;
          break label305;
        }
        this.m_IsAutoScrollingBouncingBottom = false;
        this.m_IsAutoScrollingY = false;
        break label305;
      }
      if (Math.abs(this.m_AutoScrollingSpeedY) < 10.0F)
      {
        this.m_IsAutoScrollingY = false;
        break label305;
      }
      if (this.m_AutoScrollingSpeedY > 0.0F)
      {
        f1 = -14000.0F * this.m_AutoScrollingSpeedYRatio;
        break label305;
      }
      f1 = 14000.0F * this.m_AutoScrollingSpeedYRatio;
      break label305;
      label953:
      if ((f1 < 0.0F) && (this.m_AutoScrollingSpeedY <= 0.0F))
      {
        this.m_IsAutoScrollingY = false;
        this.m_AutoScrollingSpeedY = 0.0F;
        f3 = f4;
        break label358;
      }
      f3 = f4;
      if (f1 <= 0.0F) {
        break label358;
      }
      f3 = f4;
      if (this.m_AutoScrollingSpeedY < 0.0F) {
        break label358;
      }
      this.m_IsAutoScrollingY = false;
      this.m_AutoScrollingSpeedY = 0.0F;
      f3 = f4;
      break label358;
      label1021:
      if ((!this.m_IsAutoScrollingBouncingRight) || (this.m_TargetImageBounds.right < this.m_AdjustedUserImageBounds.right)) {
        break label442;
      }
      this.m_TargetImageBounds.offset(this.m_AdjustedUserImageBounds.right - this.m_TargetImageBounds.right, 0);
      this.m_IsAutoScrollingX = false;
      break label442;
      label1076:
      if ((!this.m_IsAutoScrollingBouncingBottom) || (this.m_TargetImageBounds.bottom < this.m_AdjustedUserImageBounds.bottom)) {
        break label494;
      }
      this.m_TargetImageBounds.offset(0, this.m_AdjustedUserImageBounds.bottom - this.m_TargetImageBounds.bottom);
      this.m_IsAutoScrollingY = false;
      break label494;
      label1131:
      if (this.m_TargetImageBounds.left > this.m_AdjustedUserImageBounds.left)
      {
        this.m_IsAutoScrollingBouncingLeft = true;
        break label520;
      }
      if (this.m_TargetImageBounds.right >= this.m_AdjustedUserImageBounds.right) {
        break label520;
      }
      this.m_IsAutoScrollingBouncingRight = true;
      break label520;
      label1181:
      if (this.m_TargetImageBounds.top > this.m_AdjustedUserImageBounds.top) {
        this.m_IsAutoScrollingBouncingTop = true;
      } else if (this.m_TargetImageBounds.bottom < this.m_AdjustedUserImageBounds.bottom) {
        this.m_IsAutoScrollingBouncingBottom = true;
      }
    }
    label1231:
    this.m_AutoScrollingSpeedX = 0.0F;
    this.m_AutoScrollingSpeedY = 0.0F;
    this.m_IsAutoScrollingX = false;
    this.m_IsAutoScrollingY = false;
    this.m_LastAutoScrollingTime = 0L;
    this.m_SourceImageBounds.set(this.m_CurrentImageBounds);
    this.m_TargetImageBounds.set(this.m_AdjustedUserImageBounds);
    onImageAutoScrollingCompleted();
    startImageBoundsAnimation();
  }
  
  private void calculateAdjustedUserImageBounds(int paramInt1, int paramInt2, Rect paramRect1, Rect paramRect2)
  {
    calculateAdjustedUserImageBounds(paramInt1, paramInt2, paramInt1 / 2.0F, paramInt2 / 2.0F, paramRect1, paramRect2);
  }
  
  private void calculateAdjustedUserImageBounds(Rect paramRect1, Rect paramRect2)
  {
    calculateAdjustedUserImageBounds(getWidth(), getHeight(), paramRect1, paramRect2);
  }
  
  private Rect calculateFitToViewBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rect localRect = new Rect();
    calculateFitToViewBounds(paramInt1, paramInt2, paramInt3, paramInt4, localRect);
    return localRect;
  }
  
  private void changeCurrentImageBounds(Rect paramRect)
  {
    if (paramRect.left < 0) {
      paramRect.left = paramRect.left;
    }
    this.m_CurrentImageBounds.set(paramRect);
    onImageBoundsChanged(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  private void changeImageBoundsType(ImageBoundsType paramImageBoundsType)
  {
    if (this.m_ImageBoundsType == paramImageBoundsType) {
      return;
    }
    ImageBoundsType localImageBoundsType = this.m_ImageBoundsType;
    this.m_ImageBoundsType = paramImageBoundsType;
    onImageBoundsTypeChanged(localImageBoundsType, paramImageBoundsType);
  }
  
  private boolean completeUserMoving()
  {
    if (!this.m_IsMovingByUser) {
      return false;
    }
    this.m_IsMovingByUser = false;
    this.m_IsScalingByUser = false;
    if (this.m_ImageBoundsType == ImageBoundsType.FIT_TO_VIEW)
    {
      onUserImageMovingCompleted();
      refreshImageBounds(false, true);
      return true;
    }
    if (this.m_LastAutoScrollingTime > 0L)
    {
      onUserImageMovingCompleted();
      return true;
    }
    int i = getWidth();
    int j = getHeight();
    Rect localRect = calculateFitToViewBounds(this.m_ImageWidth, this.m_ImageHeight, i, j);
    calculateAdjustedUserImageBounds(i, j, i / 2.0F, j / 2.0F, this.m_CurrentImageBounds, this.m_AdjustedUserImageBounds);
    this.m_TargetImageBounds.set(this.m_AdjustedUserImageBounds);
    if ((this.m_TargetImageBounds.equals(localRect)) && (fitImageToView(true))) {
      return true;
    }
    onUserImageMovingCompleted();
    startImageBoundsAnimation();
    return true;
  }
  
  private void getDisplayedImageCenter(Rect paramRect, int[] paramArrayOfInt, int paramInt)
  {
    float f1 = getPaddingLeft();
    float f2 = getPaddingTop();
    float f3 = getWidth() - getPaddingRight();
    float f4 = getHeight() - getPaddingBottom();
    paramArrayOfInt[paramInt] = Math.round((f1 + f3) / 2.0F - paramRect.left);
    paramArrayOfInt[(paramInt + 1)] = Math.round((f2 + f4) / 2.0F - paramRect.top);
  }
  
  private float getImageScaleRatio(Rect paramRect)
  {
    if (paramRect.isEmpty()) {
      return 0.0F;
    }
    return Math.min(paramRect.width() / this.m_ImageWidth, paramRect.height() / this.m_ImageHeight);
  }
  
  private void moveImageBoundsByUser()
  {
    this.m_SourceImageBounds.set(this.m_TargetImageBounds);
    changeCurrentImageBounds(this.m_TargetImageBounds);
  }
  
  private void refreshImageBounds(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.m_ImageDrawable == null) {
      return;
    }
    int i = this.m_ImageDrawable.getIntrinsicWidth();
    int j = this.m_ImageDrawable.getIntrinsicHeight();
    int k = this.m_ImageWidth;
    int m = this.m_ImageHeight;
    if ((this.m_ImageWidth == i) && (this.m_ImageHeight == j))
    {
      if (paramBoolean1) {
        invalidate();
      }
    }
    else
    {
      this.m_ImageWidth = i;
      this.m_ImageHeight = j;
      onIntrinsicImageSizeChanged(i, j);
    }
    if (this.m_ImageBoundsType == ImageBoundsType.FIT_TO_VIEW)
    {
      calculateFitToViewBounds(i, j, getWidth(), getHeight(), this.m_TargetImageBounds);
      calculateAdjustedUserImageBounds(this.m_TargetImageBounds, this.m_AdjustedUserImageBounds);
      if (this.m_TargetImageBounds.equals(this.m_AdjustedUserImageBounds))
      {
        if ((!paramBoolean2) || (this.m_CurrentImageBounds.isEmpty()))
        {
          cancelImageBoundsAnimation();
          changeCurrentImageBounds(this.m_TargetImageBounds);
          return;
        }
        startImageBoundsAnimation();
        return;
      }
      Log.v(this.TAG, "refreshImageBounds() - Fit-to-view is not supported");
      changeImageBoundsType(ImageBoundsType.USER);
    }
    int n = this.m_TargetImageBounds.centerX();
    int i1 = this.m_TargetImageBounds.centerY();
    float f1;
    float f2;
    if (k > 0)
    {
      f1 = this.m_TargetImageBounds.width() / k;
      if (m <= 0) {
        break label382;
      }
      f2 = this.m_TargetImageBounds.height() / m;
      label245:
      f2 = Math.min(f1, f2);
      if (f2 > 0.0F) {
        break label388;
      }
      f1 = 1.0F;
      label262:
      this.m_TargetImageBounds.left = 0;
      this.m_TargetImageBounds.top = 0;
      this.m_TargetImageBounds.right = Math.round(i * f1);
      this.m_TargetImageBounds.bottom = Math.round(j * f1);
      if (!this.m_CurrentImageBounds.isEmpty()) {
        break label409;
      }
      this.m_TargetImageBounds.offset(getWidth() / 2 - this.m_TargetImageBounds.centerX(), getHeight() / 2 - this.m_TargetImageBounds.centerY());
    }
    for (;;)
    {
      calculateAdjustedUserImageBounds(this.m_TargetImageBounds, this.m_AdjustedUserImageBounds);
      if (!this.m_IsMovingByUser) {
        break label439;
      }
      moveImageBoundsByUser();
      return;
      f1 = 1.0F;
      break;
      label382:
      f2 = 1.0F;
      break label245;
      label388:
      f1 = f2;
      if (f2 <= this.m_MaxRatio) {
        break label262;
      }
      f1 = this.m_MaxRatio;
      break label262;
      label409:
      this.m_TargetImageBounds.offset(n - this.m_TargetImageBounds.centerX(), i1 - this.m_TargetImageBounds.centerY());
    }
    label439:
    this.m_TargetImageBounds.set(this.m_AdjustedUserImageBounds);
    if (paramBoolean2)
    {
      cancelImageAutoScrolling();
      startImageBoundsAnimation();
      return;
    }
    cancelImageBoundsAnimation();
    cancelImageAutoScrolling();
    changeCurrentImageBounds(this.m_TargetImageBounds);
  }
  
  private boolean scheduleAutoScrolling()
  {
    if (this.m_IsAutoScrollingScheduled) {
      return true;
    }
    Handler localHandler = getHandler();
    if ((localHandler != null) && (localHandler.post(this.m_AutoScrollingRunnable)))
    {
      this.m_IsAutoScrollingScheduled = true;
      return true;
    }
    return false;
  }
  
  private boolean scheduleImageBoundsAnimation()
  {
    if (this.m_IsImageBoundsAnimationScheduled) {
      return true;
    }
    Handler localHandler = getHandler();
    if ((localHandler != null) && (localHandler.post(this.m_AnimateImageBoundsRunnable)))
    {
      this.m_IsImageBoundsAnimationScheduled = true;
      return true;
    }
    return false;
  }
  
  private boolean startImageAutoScrolling()
  {
    this.m_LastAutoScrollingTime = SystemClock.elapsedRealtime();
    float f = (float)Math.sqrt(this.m_AutoScrollingSpeedX * this.m_AutoScrollingSpeedX + this.m_AutoScrollingSpeedY * this.m_AutoScrollingSpeedY);
    this.m_AutoScrollingSpeedXRatio = (Math.abs(this.m_AutoScrollingSpeedX) / f);
    this.m_AutoScrollingSpeedYRatio = (Math.abs(this.m_AutoScrollingSpeedY) / f);
    boolean bool;
    if (this.m_AutoScrollingSpeedX != 0.0F)
    {
      bool = true;
      this.m_IsAutoScrollingX = bool;
      if (this.m_AutoScrollingSpeedY == 0.0F) {
        break label165;
      }
      bool = true;
      label85:
      this.m_IsAutoScrollingY = bool;
      if ((!this.m_IsAutoScrollingBouncingLeft) || (this.m_AutoScrollingSpeedX <= 0.0F)) {
        break label170;
      }
      this.m_AutoScrollingSpeedX = 0.0F;
      label111:
      if ((!this.m_IsAutoScrollingBouncingTop) || (this.m_AutoScrollingSpeedY <= 0.0F)) {
        break label194;
      }
    }
    for (this.m_AutoScrollingSpeedY = 0.0F;; this.m_AutoScrollingSpeedY = 0.0F) {
      label165:
      label170:
      label194:
      do
      {
        if ((!this.m_IsAutoScrollingX) && (!this.m_IsAutoScrollingY)) {
          break label218;
        }
        cancelImageBoundsAnimation();
        autoScrolling();
        onImageAutoScrollingStarted();
        return true;
        bool = false;
        break;
        bool = false;
        break label85;
        if ((!this.m_IsAutoScrollingBouncingRight) || (this.m_AutoScrollingSpeedX >= 0.0F)) {
          break label111;
        }
        this.m_AutoScrollingSpeedX = 0.0F;
        break label111;
      } while ((!this.m_IsAutoScrollingBouncingBottom) || (this.m_AutoScrollingSpeedY >= 0.0F));
    }
    label218:
    this.m_LastAutoScrollingTime = 0L;
    return false;
  }
  
  private void startImageBoundsAnimation()
  {
    if (!this.m_IsImageBoundsAnimationEnabled)
    {
      cancelImageBoundsAnimation();
      changeCurrentImageBounds(this.m_TargetImageBounds);
      return;
    }
    if (this.m_TargetImageBounds.equals(this.m_CurrentImageBounds)) {
      return;
    }
    this.m_ImageBoundsAnimationStartTime = SystemClock.elapsedRealtime();
    this.m_SourceImageBounds.set(this.m_CurrentImageBounds);
    animateImageBounds();
    onImageBoundsAnimationStarted(this.m_TargetImageBounds.left, this.m_TargetImageBounds.top, this.m_TargetImageBounds.right, this.m_TargetImageBounds.bottom);
  }
  
  private void startUserMoving()
  {
    if (this.m_IsMovingByUser) {
      return;
    }
    this.m_IsMovingByUser = true;
    cancelImageAutoScrolling();
    cancelImageBoundsAnimation();
    onUserImageMovingStarted();
  }
  
  public void addGestureCallback(GestureCallback paramGestureCallback)
  {
    this.m_GestureCallbacks.add(paramGestureCallback);
  }
  
  public void addStateCallback(StateCallback paramStateCallback)
  {
    this.m_StateCallbacks.add(paramStateCallback);
  }
  
  protected void calculateAdjustedUserImageBounds(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, Rect paramRect1, Rect paramRect2)
  {
    int i = getPaddingLeft();
    int j = getPaddingTop();
    int k = paramInt1 - getPaddingRight();
    int m = paramInt2 - getPaddingBottom();
    int n = k - i;
    int i1 = m - j;
    if ((!this.m_IsMovingByUser) && (n >= paramRect1.width()) && (i1 >= paramRect1.height()))
    {
      calculateFitToViewBounds(this.m_ImageWidth, this.m_ImageHeight, paramInt1, paramInt2, paramRect2);
      return;
    }
    paramInt1 = paramRect1.width();
    paramInt2 = paramRect1.height();
    float f2 = (paramFloat1 - paramRect1.left) / paramInt1;
    float f1 = (paramFloat2 - paramRect1.top) / paramInt2;
    paramRect2.set(paramRect1);
    paramInt1 = Math.round(this.m_ImageWidth * this.m_MaxRatio);
    float f3;
    if (paramRect2.width() > paramInt1)
    {
      paramRect2.left = 0;
      paramRect2.top = 0;
      paramRect2.right = paramInt1;
      paramRect2.bottom = Math.round(this.m_ImageHeight * this.m_MaxRatio);
      f3 = paramRect2.width();
      float f4 = paramRect2.height();
      paramRect2.offset(Math.round(paramFloat1 - f3 * f2), Math.round(paramFloat2 - f4 * f1));
    }
    if (n >= paramRect2.width())
    {
      f3 = paramRect2.width();
      paramRect2.offsetTo(paramRect2.left + Math.round(paramFloat1 - f3 * f2), paramRect2.top);
      if (i1 < paramRect2.height()) {
        break label375;
      }
      paramFloat1 = paramRect2.height();
      paramRect2.offsetTo(paramRect2.left, paramRect2.top + Math.round(paramFloat2 - paramFloat1 * f1));
    }
    label375:
    do
    {
      return;
      if (paramRect2.left > i)
      {
        paramRect2.offset(i - paramRect2.left, 0);
        break;
      }
      if (paramRect2.right >= k) {
        break;
      }
      paramRect2.offset(k - paramRect2.right, 0);
      break;
      if (paramRect2.top > j)
      {
        paramRect2.offset(0, j - paramRect2.top);
        return;
      }
    } while (paramRect2.bottom >= m);
    paramRect2.offset(0, m - paramRect2.bottom);
  }
  
  protected void calculateFitToViewBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rect paramRect)
  {
    int j = getPaddingLeft();
    int i = getPaddingTop();
    int m = getPaddingRight();
    int k = getPaddingBottom();
    paramInt3 -= j + m;
    paramInt4 -= i + k;
    if ((paramInt3 <= 0) || (paramInt4 <= 0)) {}
    while ((paramInt1 <= 0) || (paramInt2 <= 0))
    {
      paramRect.set(j, i, j, i);
      return;
    }
    float f = Math.min(paramInt3 / paramInt1, paramInt4 / paramInt2);
    paramInt1 = (int)(paramInt1 * f);
    paramInt2 = (int)(paramInt2 * f);
    paramInt3 = j + (paramInt3 - paramInt1) / 2;
    paramInt4 = i + (paramInt4 - paramInt2) / 2;
    paramRect.set(paramInt3, paramInt4, paramInt3 + paramInt1, paramInt4 + paramInt2);
  }
  
  protected void calculateMovingUserImageBounds(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, Rect paramRect1, boolean paramBoolean, Rect paramRect2, Rect paramRect3)
  {
    paramRect2.set(paramRect1);
    paramRect2.offset(paramInt1, paramInt2);
    calculateAdjustedUserImageBounds(getWidth(), getHeight(), paramFloat1, paramFloat2, paramRect2, paramRect3);
    int i;
    int j;
    if (this.m_IsBouncingEnabled)
    {
      i = 0;
      if (paramRect2.left > paramRect3.left)
      {
        i = 1;
        j = (int)(paramInt1 * 0.5F);
        if (paramRect2.top <= paramRect3.top) {
          break label153;
        }
        i = 1;
        paramInt1 = (int)(paramInt2 * 0.5F);
        label90:
        if (i != 0)
        {
          paramRect2.set(paramRect1);
          paramRect2.offset(j, paramInt1);
        }
        label110:
        if ((paramBoolean) && (!this.m_IsScalingByUser)) {
          break label191;
        }
      }
    }
    label153:
    label191:
    int m;
    do
    {
      return;
      j = paramInt1;
      if (paramRect2.right >= paramRect3.right) {
        break;
      }
      i = 1;
      j = (int)(paramInt1 * 0.5F);
      break;
      paramInt1 = paramInt2;
      if (paramRect2.bottom >= paramRect3.bottom) {
        break label90;
      }
      i = 1;
      paramInt1 = (int)(paramInt2 * 0.5F);
      break label90;
      paramRect2.set(paramRect3);
      break label110;
      paramInt1 = getPaddingLeft();
      paramInt2 = getPaddingTop();
      i = getPaddingRight();
      j = getPaddingBottom();
      int k = getWidth();
      m = getHeight();
      if (paramRect2.width() <= k - paramInt1 - i) {
        paramRect2.offsetTo(paramRect3.left, paramRect2.top);
      }
    } while (paramRect2.height() > m - paramInt2 - j);
    paramRect2.offsetTo(paramRect2.left, paramRect3.top);
  }
  
  protected void calculateMovingUserImageBounds(int paramInt1, int paramInt2, Rect paramRect1, boolean paramBoolean, Rect paramRect2, Rect paramRect3)
  {
    calculateMovingUserImageBounds(paramInt1, paramInt2, getWidth() / 2, getHeight() / 2, paramRect1, paramBoolean, paramRect2, paramRect3);
  }
  
  protected void calculateScalingUserImageBounds(float paramFloat1, float paramFloat2, float paramFloat3, Rect paramRect1, Rect paramRect2, Rect paramRect3)
  {
    int i = paramRect1.width();
    int j = paramRect1.height();
    float f1 = (paramFloat1 - paramRect1.left) / i;
    float f2 = (paramFloat2 - paramRect1.top) / j;
    paramRect2.left = 0;
    paramRect2.top = 0;
    paramRect2.right = Math.round(i * paramFloat3);
    paramRect2.bottom = Math.round(j * paramFloat3);
    paramFloat3 = paramRect2.width();
    float f3 = paramRect2.height();
    paramRect2.offset(Math.round(paramFloat1 - paramFloat3 * f1), Math.round(paramFloat2 - f3 * f2));
    calculateAdjustedUserImageBounds(getWidth(), getHeight(), paramFloat1, paramFloat2, paramRect2, paramRect3);
  }
  
  protected void cancelImageAutoScrolling()
  {
    if (this.m_LastAutoScrollingTime <= 0L) {
      return;
    }
    this.m_AutoScrollingSpeedX = 0.0F;
    this.m_AutoScrollingSpeedY = 0.0F;
    this.m_IsAutoScrollingX = false;
    this.m_IsAutoScrollingY = false;
    this.m_LastAutoScrollingTime = 0L;
    onImageAutoScrollingCompleted();
  }
  
  protected void cancelImageBoundsAnimation()
  {
    if (!this.m_IsImageBoundsAnimationScheduled) {
      return;
    }
    Handler localHandler = getHandler();
    if (localHandler != null) {
      localHandler.removeCallbacks(this.m_AnimateImageBoundsRunnable);
    }
    this.m_IsImageBoundsAnimationScheduled = false;
    onImageBoundsAnimationCompleted();
  }
  
  public void cancelUserImageMoving()
  {
    if (!this.m_IsMovingByUser) {
      return;
    }
    this.m_IsTouchEventCancelled = true;
    completeUserMoving();
  }
  
  public boolean changeToOriginalImageSize()
  {
    return changeToOriginalImageSize(true);
  }
  
  public boolean changeToOriginalImageSize(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((this.m_FitToView) || (this.m_IsMovingByUser) || (this.m_MaxRatio < 1.0F)) {
      return false;
    }
    if ((this.m_ImageWidth <= 0) || (this.m_ImageHeight <= 0)) {
      return false;
    }
    int i = getWidth();
    int j = getHeight();
    Rect localRect = calculateFitToViewBounds(this.m_ImageWidth, this.m_ImageHeight, i, j);
    if ((this.m_ImageWidth <= localRect.width()) || (this.m_ImageHeight <= localRect.height())) {
      return false;
    }
    cancelImageAutoScrolling();
    float f1;
    if (this.m_CurrentImageBounds.isEmpty()) {
      f1 = 0.5F;
    }
    for (float f2 = 0.5F;; f2 = (paramInt2 - this.m_CurrentImageBounds.top) / this.m_CurrentImageBounds.height())
    {
      this.m_TargetImageBounds.left = 0;
      this.m_TargetImageBounds.top = 0;
      this.m_TargetImageBounds.right = this.m_ImageWidth;
      this.m_TargetImageBounds.bottom = this.m_ImageHeight;
      float f3 = this.m_TargetImageBounds.left;
      float f4 = this.m_ImageWidth;
      float f5 = this.m_TargetImageBounds.top;
      float f6 = this.m_ImageHeight;
      this.m_TargetImageBounds.offset(Math.round(paramInt1 - (f3 + f4 * f1)), Math.round(paramInt2 - (f5 + f6 * f2)));
      calculateAdjustedUserImageBounds(i, j, paramInt1, paramInt2, this.m_TargetImageBounds, this.m_TargetImageBounds);
      if (!this.m_TargetImageBounds.equals(localRect)) {
        break;
      }
      return false;
      f1 = (paramInt1 - this.m_CurrentImageBounds.left) / this.m_CurrentImageBounds.width();
    }
    changeImageBoundsType(ImageBoundsType.USER);
    if (paramBoolean) {
      startImageBoundsAnimation();
    }
    for (;;)
    {
      return true;
      cancelImageBoundsAnimation();
      changeCurrentImageBounds(this.m_TargetImageBounds);
    }
  }
  
  public boolean changeToOriginalImageSize(boolean paramBoolean)
  {
    return changeToOriginalImageSize(getWidth() / 2, getHeight() / 2, paramBoolean);
  }
  
  public void changeToUserImageBounds()
  {
    changeImageBoundsType(ImageBoundsType.USER);
  }
  
  public void disableGestures(int paramInt)
  {
    this.m_DisabledGestureFlags |= paramInt;
  }
  
  public void enableGestures(int paramInt)
  {
    this.m_DisabledGestureFlags &= paramInt;
  }
  
  public boolean fitImageToView()
  {
    return fitImageToView(true);
  }
  
  public boolean fitImageToView(boolean paramBoolean)
  {
    if (this.m_ImageBoundsType == ImageBoundsType.FIT_TO_VIEW) {
      return true;
    }
    changeImageBoundsType(ImageBoundsType.FIT_TO_VIEW);
    if (!completeUserMoving()) {
      refreshImageBounds(false, paramBoolean);
    }
    return this.m_ImageBoundsType == ImageBoundsType.FIT_TO_VIEW;
  }
  
  public float getAdjustedImageScaleRatio()
  {
    return getImageScaleRatio(this.m_AdjustedUserImageBounds);
  }
  
  public Rect getAdjustedUserImageBounds()
  {
    Rect localRect = new Rect();
    getAdjustedUserImageBounds(localRect);
    return localRect;
  }
  
  public void getAdjustedUserImageBounds(Rect paramRect)
  {
    paramRect.set(this.m_AdjustedUserImageBounds);
  }
  
  public void getDisplayedAdjustedUserImageCenter(int[] paramArrayOfInt)
  {
    getDisplayedAdjustedUserImageCenter(paramArrayOfInt, 0);
  }
  
  public void getDisplayedAdjustedUserImageCenter(int[] paramArrayOfInt, int paramInt)
  {
    getDisplayedImageCenter(this.m_AdjustedUserImageBounds, paramArrayOfInt, paramInt);
  }
  
  public void getDisplayedImageCenter(int[] paramArrayOfInt)
  {
    getDisplayedImageCenter(paramArrayOfInt, 0);
  }
  
  public void getDisplayedImageCenter(int[] paramArrayOfInt, int paramInt)
  {
    getDisplayedImageCenter(this.m_CurrentImageBounds, paramArrayOfInt, paramInt);
  }
  
  public void getDisplayedTargetImageCenter(int[] paramArrayOfInt)
  {
    getDisplayedTargetImageCenter(paramArrayOfInt, 0);
  }
  
  public void getDisplayedTargetImageCenter(int[] paramArrayOfInt, int paramInt)
  {
    getDisplayedImageCenter(this.m_TargetImageBounds, paramArrayOfInt, paramInt);
  }
  
  public Rect getImageBounds()
  {
    Rect localRect = new Rect();
    getImageBounds(localRect);
    return localRect;
  }
  
  public void getImageBounds(Rect paramRect)
  {
    paramRect.set(this.m_CurrentImageBounds);
  }
  
  public ImageBoundsType getImageBoundsType()
  {
    return this.m_ImageBoundsType;
  }
  
  public Drawable getImageDrawable()
  {
    return this.m_ImageDrawable;
  }
  
  public float getImageScaleRatio()
  {
    return getImageScaleRatio(this.m_CurrentImageBounds);
  }
  
  public int getIntrinsicImageHeight()
  {
    return this.m_ImageHeight;
  }
  
  public int getIntrinsicImageWidth()
  {
    return this.m_ImageWidth;
  }
  
  public boolean getLocationOnImage(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2)
  {
    return getLocationOnImage(paramArrayOfFloat, 0, paramFloat1, paramFloat2);
  }
  
  public boolean getLocationOnImage(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2)
  {
    paramFloat1 -= this.m_CurrentImageBounds.left;
    paramFloat2 -= this.m_CurrentImageBounds.top;
    paramArrayOfFloat[paramInt] = paramFloat1;
    paramArrayOfFloat[(paramInt + 1)] = paramFloat2;
    return this.m_CurrentImageBounds.contains(Math.round(paramFloat1), Math.round(paramFloat2));
  }
  
  public float getMaxImageScaleRatio()
  {
    return this.m_MaxRatio;
  }
  
  public Drawable getOverlayDrawable()
  {
    return this.m_OverlayDrawable;
  }
  
  public Rect getTargetImageBounds()
  {
    Rect localRect = new Rect();
    getTargetImageBounds(localRect);
    return localRect;
  }
  
  public void getTargetImageBounds(Rect paramRect)
  {
    paramRect.set(this.m_TargetImageBounds);
  }
  
  public float getTargetImageScaleRatio()
  {
    return getImageScaleRatio(this.m_TargetImageBounds);
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (this.m_ImageDrawable != paramDrawable) {
      return;
    }
    refreshImageBounds(true, false);
  }
  
  public boolean isFitToViewOnly()
  {
    return this.m_FitToView;
  }
  
  public boolean isGestureEnabled(int paramInt)
  {
    boolean bool = false;
    if ((this.m_DisabledGestureFlags & paramInt) == 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isImageAutoScrolling()
  {
    return this.m_LastAutoScrollingTime > 0L;
  }
  
  public boolean isImageBoundsAnimating()
  {
    return this.m_ImageBoundsAnimationStartTime > 0L;
  }
  
  public boolean isImageBoundsAnimationEnabled()
  {
    return this.m_IsImageBoundsAnimationEnabled;
  }
  
  public boolean isImageMovingByUser()
  {
    return this.m_IsMovingByUser;
  }
  
  protected boolean moveImage(Rect paramRect, boolean paramBoolean)
  {
    if ((paramRect == null) || (this.m_IsMovingByUser)) {
      return false;
    }
    cancelImageAutoScrolling();
    cancelImageBoundsAnimation();
    if ((this.m_ImageBoundsType == ImageBoundsType.FIT_TO_VIEW) && (!calculateFitToViewBounds(this.m_ImageWidth, this.m_ImageHeight, getWidth(), getHeight()).equals(paramRect)))
    {
      if (this.m_FitToView) {
        return false;
      }
      changeImageBoundsType(ImageBoundsType.USER);
    }
    calculateAdjustedUserImageBounds(paramRect, this.m_AdjustedUserImageBounds);
    this.m_TargetImageBounds.set(this.m_AdjustedUserImageBounds);
    if (paramBoolean) {
      startImageBoundsAnimation();
    }
    for (;;)
    {
      return true;
      changeCurrentImageBounds(this.m_TargetImageBounds);
    }
  }
  
  protected boolean onDoubleTap(MotionEvent paramMotionEvent)
  {
    int i = this.m_GestureCallbacks.size() - 1;
    while (i >= 0)
    {
      if (((GestureCallback)this.m_GestureCallbacks.get(i)).onDoubleTap(this, paramMotionEvent)) {
        return true;
      }
      i -= 1;
    }
    if (!isGestureEnabled(1)) {
      return false;
    }
    if (this.m_ImageBoundsType == ImageBoundsType.FIT_TO_VIEW)
    {
      changeToOriginalImageSize(Math.round(paramMotionEvent.getX()), Math.round(paramMotionEvent.getY()), true);
      return true;
    }
    fitImageToView(true);
    return true;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if ((this.m_ImageDrawable == null) || (this.m_CurrentImageBounds.isEmpty())) {
      return;
    }
    this.m_ImageDrawable.setBounds(this.m_CurrentImageBounds);
    this.m_ImageDrawable.draw(paramCanvas);
    if (this.m_OverlayDrawable != null)
    {
      this.m_OverlayDrawable.setBounds(this.m_CurrentImageBounds);
      this.m_OverlayDrawable.draw(paramCanvas);
    }
  }
  
  protected void onDrawDebugFrames(Canvas paramCanvas)
  {
    if (this.m_DebugFramePaint == null)
    {
      this.m_DebugFramePaint = new Paint();
      this.m_DebugFramePaint.setStyle(Paint.Style.STROKE);
      this.m_DebugFramePaint.setStrokeWidth(6.0F);
      this.m_DebugFramePaint.setAntiAlias(true);
    }
    this.m_DebugFramePaint.setColor(-65536);
    paramCanvas.drawRect(this.m_AdjustedUserImageBounds, this.m_DebugFramePaint);
    this.m_DebugFramePaint.setColor(65280);
    paramCanvas.drawRect(this.m_TargetImageBounds, this.m_DebugFramePaint);
    this.m_DebugFramePaint.setColor(-16711936);
    paramCanvas.drawRect(this.m_CurrentImageBounds, this.m_DebugFramePaint);
  }
  
  protected boolean onGestureFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    int i = this.m_GestureCallbacks.size() - 1;
    while (i >= 0)
    {
      if (((GestureCallback)this.m_GestureCallbacks.get(i)).onFling(this, paramMotionEvent1, paramMotionEvent2, paramFloat1, paramFloat2)) {
        return true;
      }
      i -= 1;
    }
    if ((!this.m_IsMovingByUser) || (this.m_CurrentImageBounds.isEmpty())) {
      return false;
    }
    if (this.m_CurrentImageBounds.left > this.m_AdjustedUserImageBounds.left)
    {
      bool = true;
      this.m_IsAutoScrollingBouncingLeft = bool;
      if (this.m_CurrentImageBounds.top <= this.m_AdjustedUserImageBounds.top) {
        break label214;
      }
      bool = true;
      label120:
      this.m_IsAutoScrollingBouncingTop = bool;
      if (this.m_CurrentImageBounds.right >= this.m_AdjustedUserImageBounds.right) {
        break label220;
      }
      bool = true;
      label146:
      this.m_IsAutoScrollingBouncingRight = bool;
      if (this.m_CurrentImageBounds.bottom >= this.m_AdjustedUserImageBounds.bottom) {
        break label226;
      }
    }
    label214:
    label220:
    label226:
    for (boolean bool = true;; bool = false)
    {
      this.m_IsAutoScrollingBouncingBottom = bool;
      if ((!this.m_IsAutoScrollingBouncingLeft) || (!this.m_IsAutoScrollingBouncingTop) || (!this.m_IsAutoScrollingBouncingRight) || (!this.m_IsAutoScrollingBouncingBottom)) {
        break label232;
      }
      return false;
      bool = false;
      break;
      bool = false;
      break label120;
      bool = false;
      break label146;
    }
    label232:
    startUserMoving();
    i = getPaddingLeft();
    int j = getPaddingTop();
    int k = getPaddingRight();
    int m = getPaddingBottom();
    int n = getWidth();
    int i1 = getHeight();
    if ((paramBoolean) && (this.m_CurrentImageBounds.width() <= n - i - k))
    {
      this.m_AutoScrollingSpeedX = 0.0F;
      if ((!paramBoolean) || (this.m_CurrentImageBounds.height() > i1 - j - m)) {
        break label369;
      }
    }
    for (this.m_AutoScrollingSpeedY = 0.0F;; this.m_AutoScrollingSpeedY = paramFloat1)
    {
      if (!startImageAutoScrolling()) {
        break label400;
      }
      return true;
      float f;
      if (!this.m_IsAutoScrollingBouncingLeft)
      {
        f = paramFloat1;
        if (!this.m_IsAutoScrollingBouncingRight) {}
      }
      else
      {
        f = paramFloat1 * 0.5F;
      }
      this.m_AutoScrollingSpeedX = f;
      break;
      label369:
      if (!this.m_IsAutoScrollingBouncingTop)
      {
        paramFloat1 = paramFloat2;
        if (!this.m_IsAutoScrollingBouncingBottom) {}
      }
      else
      {
        paramFloat1 = paramFloat2 * 0.5F;
      }
    }
    label400:
    completeUserMoving();
    return false;
  }
  
  protected boolean onGestureScale(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    int i = this.m_GestureCallbacks.size() - 1;
    while (i >= 0)
    {
      if (((GestureCallback)this.m_GestureCallbacks.get(i)).onScale(this, paramFloat2, paramFloat3, paramFloat4, paramFloat5)) {
        return true;
      }
      i -= 1;
    }
    if (!this.m_IsMovingByUser) {
      return false;
    }
    if (!isGestureEnabled(8)) {
      return false;
    }
    this.m_ScaleGestureDetector.getPreviousSpan();
    this.m_ScaleGestureDetector.getCurrentSpan();
    if (isGestureEnabled(4)) {
      calculateMovingUserImageBounds(Math.round(paramFloat4), Math.round(paramFloat5), paramFloat2, paramFloat3, this.m_CurrentImageBounds, true, this.m_TargetImageBounds, this.m_AdjustedUserImageBounds);
    }
    for (;;)
    {
      calculateScalingUserImageBounds(paramFloat2, paramFloat3, paramFloat1 * 1.0F, this.m_TargetImageBounds, this.m_TargetImageBounds, this.m_AdjustedUserImageBounds);
      changeImageBoundsType(ImageBoundsType.USER);
      moveImageBoundsByUser();
      return true;
      this.m_TargetImageBounds.set(this.m_CurrentImageBounds);
    }
  }
  
  protected boolean onGestureScaleBegin()
  {
    int i = this.m_GestureCallbacks.size() - 1;
    while (i >= 0)
    {
      if (((GestureCallback)this.m_GestureCallbacks.get(i)).onScaleBegin(this)) {
        return true;
      }
      i -= 1;
    }
    if ((this.m_CurrentImageBounds.isEmpty()) || (this.m_FitToView)) {
      return false;
    }
    this.m_IsScalingByUser = true;
    startUserMoving();
    return true;
  }
  
  protected void onGestureScaleEnd()
  {
    int i = this.m_GestureCallbacks.size() - 1;
    while (i >= 0)
    {
      ((GestureCallback)this.m_GestureCallbacks.get(i)).onScaleEnd(this);
      i -= 1;
    }
  }
  
  protected boolean onGestureScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    int i = this.m_GestureCallbacks.size() - 1;
    while (i >= 0)
    {
      if (((GestureCallback)this.m_GestureCallbacks.get(i)).onScroll(this, paramMotionEvent1, paramMotionEvent2, paramFloat1, paramFloat2)) {
        return true;
      }
      i -= 1;
    }
    if ((this.m_CurrentImageBounds.isEmpty()) || (this.m_ImageBoundsType == ImageBoundsType.FIT_TO_VIEW)) {
      return false;
    }
    if (this.m_IsScalingByUser) {
      return false;
    }
    if (paramMotionEvent2.getPointerCount() == 1)
    {
      if (!isGestureEnabled(2)) {
        return false;
      }
    }
    else if (!isGestureEnabled(4)) {
      return false;
    }
    startUserMoving();
    calculateMovingUserImageBounds(Math.round(-paramFloat1), Math.round(-paramFloat2), this.m_CurrentImageBounds, true, this.m_TargetImageBounds, this.m_AdjustedUserImageBounds);
    moveImageBoundsByUser();
    return true;
  }
  
  protected void onImageAutoScrollingCompleted()
  {
    int i = this.m_StateCallbacks.size() - 1;
    while (i >= 0)
    {
      ((StateCallback)this.m_StateCallbacks.get(i)).onImageAutoScrollingCompleted(this);
      i -= 1;
    }
  }
  
  protected void onImageAutoScrollingStarted()
  {
    int i = this.m_StateCallbacks.size() - 1;
    while (i >= 0)
    {
      ((StateCallback)this.m_StateCallbacks.get(i)).onImageAutoScrollingStarted(this);
      i -= 1;
    }
  }
  
  protected void onImageBoundsAnimate(long paramLong1, long paramLong2, long paramLong3, float paramFloat) {}
  
  protected void onImageBoundsAnimationCompleted()
  {
    int i = this.m_StateCallbacks.size() - 1;
    while (i >= 0)
    {
      ((StateCallback)this.m_StateCallbacks.get(i)).onImageBoundsAnimationCompleted(this);
      i -= 1;
    }
  }
  
  protected void onImageBoundsAnimationStarted(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.m_StateCallbacks.size() - 1;
    while (i >= 0)
    {
      ((StateCallback)this.m_StateCallbacks.get(i)).onImageBoundsAnimationStarted(this, paramInt1, paramInt2, paramInt3, paramInt4);
      i -= 1;
    }
  }
  
  protected void onImageBoundsChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.m_StateCallbacks.size() - 1;
    while (i >= 0)
    {
      ((StateCallback)this.m_StateCallbacks.get(i)).onImageBoundsChanged(this, paramInt1, paramInt2, paramInt3, paramInt4);
      i -= 1;
    }
    invalidate();
  }
  
  protected void onImageBoundsTypeChanged(ImageBoundsType paramImageBoundsType1, ImageBoundsType paramImageBoundsType2)
  {
    int i = this.m_StateCallbacks.size() - 1;
    while (i >= 0)
    {
      ((StateCallback)this.m_StateCallbacks.get(i)).onImageBoundsTypeChanged(this, paramImageBoundsType1, paramImageBoundsType2);
      i -= 1;
    }
  }
  
  protected void onIntrinsicImageSizeChanged(int paramInt1, int paramInt2) {}
  
  protected void onLongPress(MotionEvent paramMotionEvent)
  {
    int i = this.m_GestureCallbacks.size() - 1;
    while (i >= 0)
    {
      if (((GestureCallback)this.m_GestureCallbacks.get(i)).onLongPress(this, paramMotionEvent)) {
        return;
      }
      i -= 1;
    }
  }
  
  protected boolean onSingleTapConfirmed(MotionEvent paramMotionEvent)
  {
    int i = this.m_GestureCallbacks.size() - 1;
    while (i >= 0)
    {
      if (((GestureCallback)this.m_GestureCallbacks.get(i)).onSingleTapConfirmed(this, paramMotionEvent)) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  protected boolean onSingleTapUp(MotionEvent paramMotionEvent)
  {
    int i = this.m_GestureCallbacks.size() - 1;
    while (i >= 0)
    {
      if (((GestureCallback)this.m_GestureCallbacks.get(i)).onSingleTapUp(this, paramMotionEvent)) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    refreshImageBounds(false, false);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getAction())
    {
    case 2: 
    default: 
      if (!this.m_IsTouchEventCancelled)
      {
        this.m_GestureDetector.onTouchEvent(paramMotionEvent);
        this.m_ScaleGestureDetector.onTouchEvent(paramMotionEvent);
      }
      break;
    }
    for (;;)
    {
      if (this.m_OnTouchListener != null) {
        this.m_OnTouchListener.onTouch(this, paramMotionEvent);
      }
      return true;
      this.m_IsTouchEventCancelled = false;
      cancelImageBoundsAnimation();
      this.m_GestureDetector.onTouchEvent(paramMotionEvent);
      this.m_ScaleGestureDetector.onTouchEvent(paramMotionEvent);
      continue;
      this.m_GestureDetector.onTouchEvent(paramMotionEvent);
      this.m_ScaleGestureDetector.onTouchEvent(paramMotionEvent);
      completeUserMoving();
    }
  }
  
  protected void onUserImageMovingCompleted()
  {
    int i = this.m_StateCallbacks.size() - 1;
    while (i >= 0)
    {
      ((StateCallback)this.m_StateCallbacks.get(i)).onUserImageMovingCompleted(this);
      i -= 1;
    }
  }
  
  protected void onUserImageMovingStarted()
  {
    int i = this.m_StateCallbacks.size() - 1;
    while (i >= 0)
    {
      ((StateCallback)this.m_StateCallbacks.get(i)).onUserImageMovingStarted(this);
      i -= 1;
    }
  }
  
  protected void refreshImageBounds(boolean paramBoolean)
  {
    refreshImageBounds(false, paramBoolean);
  }
  
  public void removeGestureCallback(GestureCallback paramGestureCallback)
  {
    this.m_GestureCallbacks.remove(paramGestureCallback);
  }
  
  public void removeStateCallback(StateCallback paramStateCallback)
  {
    this.m_StateCallbacks.remove(paramStateCallback);
  }
  
  public void setFitToViewOnly(boolean paramBoolean)
  {
    setFitToViewOnly(paramBoolean, true);
  }
  
  public void setFitToViewOnly(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.m_FitToView == paramBoolean1) {
      return;
    }
    this.m_FitToView = paramBoolean1;
    if (paramBoolean1)
    {
      fitImageToView(paramBoolean2);
      return;
    }
    refreshImageBounds(paramBoolean2);
  }
  
  public void setImageBoundsAnimationEnabled(boolean paramBoolean)
  {
    if (this.m_IsImageBoundsAnimationEnabled == paramBoolean) {
      return;
    }
    this.m_IsImageBoundsAnimationEnabled = paramBoolean;
  }
  
  public void setImageDrawable(Drawable paramDrawable)
  {
    setImageDrawable(paramDrawable, false);
  }
  
  public void setImageDrawable(Drawable paramDrawable, boolean paramBoolean)
  {
    if (this.m_ImageDrawable == paramDrawable) {
      return;
    }
    if (this.m_ImageDrawable != null) {
      this.m_ImageDrawable.setCallback(null);
    }
    this.m_ImageDrawable = paramDrawable;
    if (paramDrawable == null)
    {
      completeUserMoving();
      cancelImageBoundsAnimation();
      this.m_SourceImageBounds.setEmpty();
      this.m_TargetImageBounds.setEmpty();
      changeCurrentImageBounds(this.m_TargetImageBounds);
      return;
    }
    if (paramDrawable != null) {
      paramDrawable.setCallback(this);
    }
    if (!paramBoolean) {}
    refreshImageBounds(false, paramBoolean);
  }
  
  public void setMaxImageRatio(float paramFloat)
  {
    paramFloat = Math.max(1.0F, paramFloat);
    if ((Float.isNaN(paramFloat)) || (Float.isInfinite(paramFloat))) {
      throw new IllegalArgumentException("Invalid image ratio : " + paramFloat);
    }
    this.m_MaxRatio = paramFloat;
    refreshImageBounds(false, true);
  }
  
  public void setOnTouchListener(View.OnTouchListener paramOnTouchListener)
  {
    this.m_OnTouchListener = paramOnTouchListener;
  }
  
  public void setOverlayDrawable(Drawable paramDrawable)
  {
    if (this.m_OverlayDrawable == paramDrawable) {
      return;
    }
    this.m_OverlayDrawable = paramDrawable;
    invalidate();
  }
  
  public void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setPadding(paramInt1, paramInt2, paramInt3, paramInt4, true);
  }
  
  public void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    super.setPadding(paramInt1, paramInt2, paramInt3, paramInt4);
    refreshImageBounds(false, paramBoolean);
  }
  
  public void setPaddingRelative(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setPaddingRelative(paramInt1, paramInt2, paramInt3, paramInt4, true);
  }
  
  public void setPaddingRelative(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    super.setPaddingRelative(paramInt1, paramInt2, paramInt3, paramInt4);
    refreshImageBounds(false, paramBoolean);
  }
  
  public static abstract class GestureCallback
  {
    public boolean onDoubleTap(ImageViewer paramImageViewer, MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onFling(ImageViewer paramImageViewer, MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      return false;
    }
    
    public boolean onLongPress(ImageViewer paramImageViewer, MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onScale(ImageViewer paramImageViewer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      return false;
    }
    
    public boolean onScaleBegin(ImageViewer paramImageViewer)
    {
      return false;
    }
    
    public void onScaleEnd(ImageViewer paramImageViewer) {}
    
    public boolean onScroll(ImageViewer paramImageViewer, MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      return false;
    }
    
    public boolean onSingleTapConfirmed(ImageViewer paramImageViewer, MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onSingleTapUp(ImageViewer paramImageViewer, MotionEvent paramMotionEvent)
    {
      return false;
    }
  }
  
  public static enum ImageBoundsType
  {
    FIT_TO_VIEW,  USER;
  }
  
  public static abstract class StateCallback
  {
    public void onImageAutoScrollingCompleted(ImageViewer paramImageViewer) {}
    
    public void onImageAutoScrollingStarted(ImageViewer paramImageViewer) {}
    
    public void onImageBoundsAnimationCompleted(ImageViewer paramImageViewer) {}
    
    public void onImageBoundsAnimationStarted(ImageViewer paramImageViewer, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    public void onImageBoundsChanged(ImageViewer paramImageViewer, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    public void onImageBoundsTypeChanged(ImageViewer paramImageViewer, ImageViewer.ImageBoundsType paramImageBoundsType1, ImageViewer.ImageBoundsType paramImageBoundsType2) {}
    
    public void onUserImageMovingCompleted(ImageViewer paramImageViewer) {}
    
    public void onUserImageMovingStarted(ImageViewer paramImageViewer) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/widget/ImageViewer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */