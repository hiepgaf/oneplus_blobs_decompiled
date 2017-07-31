package com.android.server.policy.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;

public class OpGlobalActionIconReboot
  extends OpGlobalActionIcon
{
  protected static final int ARC_LENGTH_END_1 = 90;
  protected static final int ARC_LENGTH_END_2 = 293;
  protected static final int ARC_LENGTH_START_1 = 293;
  protected static final int ARC_LENGTH_START_2 = 90;
  protected static final int ARC_SHIFT_POS_END_1 = 270;
  protected static final int ARC_SHIFT_POS_END_2 = 90;
  protected static final int ARC_SHIFT_POS_START_1 = 0;
  protected static final int ARC_SHIFT_POS_START_2 = 0;
  public static final String TAG = "GlobalActionIconReboot";
  private int ARC_START_POS = 22;
  private int mTriangleAnimProgress = 0;
  private Paint mTrianglePaint;
  
  public OpGlobalActionIconReboot(int paramInt, Context paramContext)
  {
    super(paramInt, paramContext);
    this.mIsAnimCircleDelayedByIcon = false;
    this.mIsArcFadeIn = false;
    initTrianglePaint();
  }
  
  private void initTrianglePaint()
  {
    this.mTrianglePaint = new Paint();
    this.mTrianglePaint.setAntiAlias(true);
    this.mTrianglePaint.setStyle(Paint.Style.FILL);
    this.mTrianglePaint.setStrokeWidth(9.0F);
    this.mTrianglePaint.setColor(-1);
  }
  
  public void draw(Canvas paramCanvas)
  {
    Object localObject = getBounds();
    localObject = new RectF(((Rect)localObject).left + 75, ((Rect)localObject).top + 75, ((Rect)localObject).right - 75, ((Rect)localObject).bottom - 75);
    if (!this.mIsAnimationStarted)
    {
      paramCanvas.drawArc((RectF)localObject, this.ARC_START_POS, 293.0F, false, this.mPaint);
      localObject = new Path();
      ((Path)localObject).moveTo(111.0F, 105.0F);
      ((Path)localObject).lineTo(144.0F, 70.0F);
      ((Path)localObject).lineTo(144.0F, 105.0F);
      ((Path)localObject).close();
      paramCanvas.drawPath((Path)localObject, this.mTrianglePaint);
      return;
    }
    if (this.mTriangleAnimProgress < 200)
    {
      localObject = new Path();
      ((Path)localObject).moveTo(this.mTriangleAnimProgress / 200.0F / 2.0F * 33.0F + 111.0F, 105.0F);
      ((Path)localObject).lineTo(144.0F, this.mTriangleAnimProgress / 200.0F / 2.0F * 33.0F + 70.0F);
      ((Path)localObject).lineTo(144.0F, 105.0F);
      ((Path)localObject).close();
      paramCanvas.drawPath((Path)localObject, this.mTrianglePaint);
    }
    drawCircle(paramCanvas);
  }
  
  protected void drawCircle(Canvas paramCanvas)
  {
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
        OpGlobalActionIconReboot.this.mIsAnimationStarted = true;
        OpGlobalActionIconReboot.-set0(OpGlobalActionIconReboot.this, ((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue());
      }
    });
    localValueAnimator.setDuration((ANIM_DURATION_SCALE * 200.0F));
    localValueAnimator.setInterpolator(new DecelerateInterpolator());
    localValueAnimator.setStartDelay(400L);
    localValueAnimator.start();
    startCircleAnimation();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/ui/OpGlobalActionIconReboot.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */