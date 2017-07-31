package com.android.server.policy.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

public class OpGlobalActionIcon
  extends Drawable
{
  public static float ANIM_DURATION_SCALE = 1.0F;
  protected static final int ARC_LENGTH_END_1 = 45;
  protected static final int ARC_LENGTH_END_2 = 270;
  protected static final int ARC_LENGTH_START_1 = 270;
  protected static final int ARC_LENGTH_START_2 = 45;
  public static int ARC_ROTATE_DEGREE = 240;
  public static long ARC_SHIFT_DURATION = 600L;
  protected static final int ARC_SHIFT_POS_END_1 = 315;
  protected static final int ARC_SHIFT_POS_END_2 = 45;
  protected static final int ARC_SHIFT_POS_START_1 = 0;
  protected static final int ARC_SHIFT_POS_START_2 = 0;
  public static final long CIRCLE_ANIM_DELAYED_BY_ICON = 100L;
  protected static final boolean DBG = false;
  protected static final int ICON_SIZE = 75;
  protected static final int PAINT_COLOR = -1;
  protected static final int PAINT_STROKE_WIDTH = 9;
  private static final String TAG = "OpGlobalActionIcon";
  protected int ARC_START_POS = 315;
  protected int mArcLengthAnimValue;
  protected int mArcRotateValueTemp;
  protected int mArcStartAnimValue;
  protected int mArcStartShiftValue;
  protected int mArcStartValueTemp;
  protected int mArcTurnAroundAnimPos;
  protected int mCircleAlpha = 255;
  protected Context mContext = null;
  protected int mIconAnimProgress = 0;
  private int mIconResId = -1;
  protected boolean mIsAnimCircleDelayedByIcon = true;
  protected boolean mIsAnimationStarted = false;
  protected boolean mIsArcFadeIn = true;
  protected Paint mPaint;
  
  public OpGlobalActionIcon(int paramInt, Context paramContext)
  {
    this.mIconResId = paramInt;
    this.mContext = paramContext;
    initPaint();
  }
  
  private void initPaint()
  {
    this.mPaint = new Paint();
    this.mPaint.setAntiAlias(true);
    this.mPaint.setStyle(Paint.Style.STROKE);
    this.mPaint.setStrokeWidth(9.0F);
    this.mPaint.setColor(-1);
  }
  
  public void draw(Canvas paramCanvas)
  {
    Bitmap localBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), this.mIconResId);
    int i = 96 - (int)(this.mIconAnimProgress / 200.0F * 96.0F);
    int j = 96 - (int)(this.mIconAnimProgress / 200.0F * 96.0F);
    if ((i > 0) || (j > 0)) {
      paramCanvas.drawBitmap(Bitmap.createScaledBitmap(localBitmap, i, j, false), (int)(this.mIconAnimProgress / 200.0F * 96.0F / 2.0F) + 60, (int)(this.mIconAnimProgress / 200.0F * 96.0F / 2.0F) + 60, null);
    }
    if (this.mIsAnimationStarted) {
      drawCircle(paramCanvas);
    }
  }
  
  protected void drawCircle(Canvas paramCanvas)
  {
    this.mPaint.setAlpha(this.mCircleAlpha);
    Rect localRect = getBounds();
    paramCanvas.drawArc(new RectF(localRect.left + 75, localRect.top + 75, localRect.right - 75, localRect.bottom - 75), (this.ARC_START_POS + this.mArcStartShiftValue + this.mArcTurnAroundAnimPos) % 360, this.mArcLengthAnimValue, false, this.mPaint);
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void startAnimateConfirmed()
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofInt(new int[] { 0, 200 });
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OpGlobalActionIcon.this.mIsAnimationStarted = true;
        OpGlobalActionIcon.this.mIconAnimProgress = ((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue();
        OpGlobalActionIcon.this.invalidateSelf();
      }
    });
    localValueAnimator.setDuration((ANIM_DURATION_SCALE * 200.0F));
    localValueAnimator.setInterpolator(new AccelerateInterpolator());
    localValueAnimator.setStartDelay(400L);
    localValueAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator) {}
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        OpGlobalActionIcon.this.startCircleAnimation();
      }
    });
    localValueAnimator.start();
  }
  
  protected void startCircleAnimation()
  {
    final ValueAnimator localValueAnimator1 = ValueAnimator.ofInt(new int[] { 0, 315 });
    localValueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OpGlobalActionIcon.this.mArcStartAnimValue = Integer.parseInt(paramAnonymousValueAnimator.getAnimatedValue().toString());
        OpGlobalActionIcon.this.mArcStartShiftValue = (OpGlobalActionIcon.this.mArcStartValueTemp + OpGlobalActionIcon.this.mArcStartAnimValue);
        OpGlobalActionIcon.this.invalidateSelf();
      }
    });
    localValueAnimator1.setDuration(((float)ARC_SHIFT_DURATION * ANIM_DURATION_SCALE));
    localValueAnimator1.setInterpolator(new DecelerateInterpolator());
    if (!this.mIsAnimationStarted) {
      localValueAnimator1.setStartDelay(400L);
    }
    if (this.mIsAnimCircleDelayedByIcon) {
      localValueAnimator1.setStartDelay(100L);
    }
    localValueAnimator1.start();
    final ValueAnimator localValueAnimator2 = ValueAnimator.ofInt(new int[] { 0, 45 });
    localValueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OpGlobalActionIcon.this.mIsAnimationStarted = true;
        OpGlobalActionIcon.this.mArcStartAnimValue = Integer.parseInt(paramAnonymousValueAnimator.getAnimatedValue().toString());
        OpGlobalActionIcon.this.mArcStartShiftValue = (OpGlobalActionIcon.this.mArcStartValueTemp + OpGlobalActionIcon.this.mArcStartAnimValue);
        OpGlobalActionIcon.this.invalidateSelf();
      }
    });
    localValueAnimator2.setDuration(((float)ARC_SHIFT_DURATION * ANIM_DURATION_SCALE));
    localValueAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
    if (!this.mIsAnimationStarted) {
      localValueAnimator2.setStartDelay(400L);
    }
    localValueAnimator1.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        OpGlobalActionIcon.this.mArcStartValueTemp = (OpGlobalActionIcon.this.mArcStartShiftValue % 360);
        localValueAnimator2.setStartDelay(0L);
        localValueAnimator2.start();
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator) {}
    });
    localValueAnimator2.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        OpGlobalActionIcon.this.mArcStartValueTemp = (OpGlobalActionIcon.this.mArcStartShiftValue % 360);
        localValueAnimator1.setStartDelay(0L);
        localValueAnimator1.start();
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator) {}
    });
    final ValueAnimator localValueAnimator3 = ValueAnimator.ofInt(new int[] { 270, 45 });
    localValueAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OpGlobalActionIcon.this.mArcLengthAnimValue = Integer.parseInt(paramAnonymousValueAnimator.getAnimatedValue().toString());
        OpGlobalActionIcon.this.invalidateSelf();
      }
    });
    localValueAnimator3.setDuration(((float)ARC_SHIFT_DURATION * ANIM_DURATION_SCALE));
    localValueAnimator3.setInterpolator(new DecelerateInterpolator());
    if (!this.mIsAnimationStarted) {
      localValueAnimator3.setStartDelay(400L);
    }
    if (this.mIsAnimCircleDelayedByIcon) {
      localValueAnimator3.setStartDelay(100L);
    }
    localValueAnimator3.start();
    final ValueAnimator localValueAnimator4 = ValueAnimator.ofInt(new int[] { 45, 270 });
    localValueAnimator4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OpGlobalActionIcon.this.mIsAnimationStarted = true;
        OpGlobalActionIcon.this.mArcLengthAnimValue = Integer.parseInt(paramAnonymousValueAnimator.getAnimatedValue().toString());
        OpGlobalActionIcon.this.invalidateSelf();
      }
    });
    localValueAnimator4.setDuration(((float)ARC_SHIFT_DURATION * ANIM_DURATION_SCALE));
    localValueAnimator4.setInterpolator(new AccelerateDecelerateInterpolator());
    if (!this.mIsAnimationStarted) {
      localValueAnimator4.setStartDelay(400L);
    }
    if (this.mIsAnimCircleDelayedByIcon) {
      localValueAnimator4.setStartDelay(100L);
    }
    localValueAnimator1.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        localValueAnimator4.setStartDelay(0L);
        localValueAnimator4.start();
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator) {}
    });
    localValueAnimator2.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        localValueAnimator3.setStartDelay(0L);
        localValueAnimator3.start();
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator) {}
    });
    localValueAnimator1 = ValueAnimator.ofInt(new int[] { 0, ARC_ROTATE_DEGREE });
    localValueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OpGlobalActionIcon.this.mIsAnimationStarted = true;
        OpGlobalActionIcon.this.mArcTurnAroundAnimPos = (OpGlobalActionIcon.this.mArcRotateValueTemp + Integer.parseInt(paramAnonymousValueAnimator.getAnimatedValue().toString()));
        OpGlobalActionIcon.this.invalidateSelf();
      }
    });
    localValueAnimator1.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator) {}
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator)
      {
        OpGlobalActionIcon.this.mArcRotateValueTemp = (OpGlobalActionIcon.this.mArcTurnAroundAnimPos % 360);
        localValueAnimator1.setStartDelay(0L);
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator) {}
    });
    localValueAnimator1.setDuration((ANIM_DURATION_SCALE * 1000.0F));
    localValueAnimator1.setInterpolator(new LinearInterpolator());
    localValueAnimator1.setRepeatCount(-1);
    localValueAnimator1.setStartDelay(400L);
    if (this.mIsAnimCircleDelayedByIcon) {
      localValueAnimator1.setStartDelay(100L);
    }
    localValueAnimator1.start();
    if (this.mIsArcFadeIn)
    {
      localValueAnimator1 = ValueAnimator.ofInt(new int[] { 0, 255 });
      localValueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          OpGlobalActionIcon.this.mIsAnimationStarted = true;
          OpGlobalActionIcon.this.mCircleAlpha = Integer.parseInt(paramAnonymousValueAnimator.getAnimatedValue().toString());
          OpGlobalActionIcon.this.invalidateSelf();
        }
      });
      localValueAnimator1.setDuration((ANIM_DURATION_SCALE * 200.0F));
      localValueAnimator1.setInterpolator(new LinearInterpolator());
      localValueAnimator1.setStartDelay(400L);
      if (this.mIsAnimCircleDelayedByIcon) {
        localValueAnimator1.setStartDelay(100L);
      }
      if (this.mIsAnimCircleDelayedByIcon) {
        localValueAnimator1.start();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/ui/OpGlobalActionIcon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */