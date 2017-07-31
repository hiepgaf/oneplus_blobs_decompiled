package android.graphics.drawable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.FloatProperty;
import android.util.MathUtils;
import android.view.DisplayListCanvas;
import android.view.RenderNodeAnimator;
import android.view.animation.LinearInterpolator;

class RippleForeground
  extends RippleComponent
{
  private static final int BOUNDED_OPACITY_EXIT_DURATION = 400;
  private static final int BOUNDED_ORIGIN_EXIT_DURATION = 300;
  private static final int BOUNDED_RADIUS_EXIT_DURATION = 800;
  private static final TimeInterpolator DECELERATE_INTERPOLATOR;
  private static final TimeInterpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
  private static final float MAX_BOUNDED_RADIUS = 350.0F;
  private static final FloatProperty<RippleForeground> OPACITY = new FloatProperty("opacity")
  {
    public Float get(RippleForeground paramAnonymousRippleForeground)
    {
      return Float.valueOf(RippleForeground.-get0(paramAnonymousRippleForeground));
    }
    
    public void setValue(RippleForeground paramAnonymousRippleForeground, float paramAnonymousFloat)
    {
      RippleForeground.-set1(paramAnonymousRippleForeground, paramAnonymousFloat);
      paramAnonymousRippleForeground.invalidateSelf();
    }
  };
  private static final int OPACITY_ENTER_DURATION_FAST = 120;
  private static final int RIPPLE_ENTER_DELAY = 80;
  private static final FloatProperty<RippleForeground> TWEEN_ORIGIN;
  private static final FloatProperty<RippleForeground> TWEEN_RADIUS;
  private static final float WAVE_OPACITY_DECAY_VELOCITY = 3.0F;
  private static final float WAVE_TOUCH_DOWN_ACCELERATION = 1024.0F;
  private static final float WAVE_TOUCH_UP_ACCELERATION = 3400.0F;
  private final AnimatorListenerAdapter mAnimationListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      RippleForeground.-set0(RippleForeground.this, true);
    }
  };
  private float mBoundedRadius = 0.0F;
  private float mClampedStartingX;
  private float mClampedStartingY;
  private boolean mHasFinishedExit;
  private boolean mIsBounded;
  private float mOpacity = 1.0F;
  private CanvasProperty<Paint> mPropPaint;
  private CanvasProperty<Float> mPropRadius;
  private CanvasProperty<Float> mPropX;
  private CanvasProperty<Float> mPropY;
  private float mStartingX;
  private float mStartingY;
  private float mTargetX = 0.0F;
  private float mTargetY = 0.0F;
  private float mTweenRadius = 0.0F;
  private float mTweenX = 0.0F;
  private float mTweenY = 0.0F;
  
  static
  {
    DECELERATE_INTERPOLATOR = new LogDecelerateInterpolator(400.0F, 1.4F, 0.0F);
    TWEEN_RADIUS = new FloatProperty("tweenRadius")
    {
      public Float get(RippleForeground paramAnonymousRippleForeground)
      {
        return Float.valueOf(RippleForeground.-get1(paramAnonymousRippleForeground));
      }
      
      public void setValue(RippleForeground paramAnonymousRippleForeground, float paramAnonymousFloat)
      {
        RippleForeground.-set2(paramAnonymousRippleForeground, paramAnonymousFloat);
        paramAnonymousRippleForeground.invalidateSelf();
      }
    };
    TWEEN_ORIGIN = new FloatProperty("tweenOrigin")
    {
      public Float get(RippleForeground paramAnonymousRippleForeground)
      {
        return Float.valueOf(RippleForeground.-get2(paramAnonymousRippleForeground));
      }
      
      public void setValue(RippleForeground paramAnonymousRippleForeground, float paramAnonymousFloat)
      {
        RippleForeground.-set3(paramAnonymousRippleForeground, paramAnonymousFloat);
        RippleForeground.-set4(paramAnonymousRippleForeground, paramAnonymousFloat);
        paramAnonymousRippleForeground.invalidateSelf();
      }
    };
  }
  
  public RippleForeground(RippleDrawable paramRippleDrawable, Rect paramRect, float paramFloat1, float paramFloat2, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramRippleDrawable, paramRect, paramBoolean2);
    this.mIsBounded = paramBoolean1;
    this.mStartingX = paramFloat1;
    this.mStartingY = paramFloat2;
    if (paramBoolean1)
    {
      this.mBoundedRadius = ((float)(Math.random() * 350.0D * 0.1D) + 315.0F);
      return;
    }
    this.mBoundedRadius = 0.0F;
  }
  
  private void clampStartingPosition()
  {
    float f1 = this.mBounds.exactCenterX();
    float f2 = this.mBounds.exactCenterY();
    float f3 = this.mStartingX - f1;
    float f4 = this.mStartingY - f2;
    float f5 = this.mTargetRadius;
    if (f3 * f3 + f4 * f4 > f5 * f5)
    {
      double d = Math.atan2(f4, f3);
      this.mClampedStartingX = ((float)(Math.cos(d) * f5) + f1);
      this.mClampedStartingY = ((float)(Math.sin(d) * f5) + f2);
      return;
    }
    this.mClampedStartingX = this.mStartingX;
    this.mClampedStartingY = this.mStartingY;
  }
  
  private void computeBoundedTargetValues()
  {
    this.mTargetX = ((this.mClampedStartingX - this.mBounds.exactCenterX()) * 0.7F);
    this.mTargetY = ((this.mClampedStartingY - this.mBounds.exactCenterY()) * 0.7F);
    this.mTargetRadius = this.mBoundedRadius;
  }
  
  private float getCurrentRadius()
  {
    return MathUtils.lerp(0.0F, this.mTargetRadius, this.mTweenRadius);
  }
  
  private float getCurrentX()
  {
    return MathUtils.lerp(this.mClampedStartingX - this.mBounds.exactCenterX(), this.mTargetX, this.mTweenX);
  }
  
  private float getCurrentY()
  {
    return MathUtils.lerp(this.mClampedStartingY - this.mBounds.exactCenterY(), this.mTargetY, this.mTweenY);
  }
  
  private int getOpacityExitDuration()
  {
    return (int)(this.mOpacity * 1000.0F / 3.0F + 0.5F);
  }
  
  private int getRadiusExitDuration()
  {
    return (int)(Math.sqrt((this.mTargetRadius - getCurrentRadius()) / 4424.0F * this.mDensityScale) * 1000.0D + 0.5D);
  }
  
  protected RippleComponent.RenderNodeAnimatorSet createHardwareExit(Paint paramPaint)
  {
    int k;
    int j;
    if (this.mIsBounded)
    {
      computeBoundedTargetValues();
      k = 800;
      j = 300;
    }
    for (int i = 400;; i = getOpacityExitDuration())
    {
      float f1 = getCurrentX();
      float f2 = getCurrentY();
      float f3 = getCurrentRadius();
      paramPaint.setAlpha((int)(paramPaint.getAlpha() * this.mOpacity + 0.5F));
      this.mPropPaint = CanvasProperty.createPaint(paramPaint);
      this.mPropRadius = CanvasProperty.createFloat(f3);
      this.mPropX = CanvasProperty.createFloat(f1);
      this.mPropY = CanvasProperty.createFloat(f2);
      paramPaint = new RenderNodeAnimator(this.mPropRadius, this.mTargetRadius);
      paramPaint.setDuration(k);
      paramPaint.setInterpolator(DECELERATE_INTERPOLATOR);
      RenderNodeAnimator localRenderNodeAnimator1 = new RenderNodeAnimator(this.mPropX, this.mTargetX);
      localRenderNodeAnimator1.setDuration(j);
      localRenderNodeAnimator1.setInterpolator(DECELERATE_INTERPOLATOR);
      RenderNodeAnimator localRenderNodeAnimator2 = new RenderNodeAnimator(this.mPropY, this.mTargetY);
      localRenderNodeAnimator2.setDuration(j);
      localRenderNodeAnimator2.setInterpolator(DECELERATE_INTERPOLATOR);
      RenderNodeAnimator localRenderNodeAnimator3 = new RenderNodeAnimator(this.mPropPaint, 1, 0.0F);
      localRenderNodeAnimator3.setDuration(i);
      localRenderNodeAnimator3.setInterpolator(LINEAR_INTERPOLATOR);
      localRenderNodeAnimator3.addListener(this.mAnimationListener);
      RippleComponent.RenderNodeAnimatorSet localRenderNodeAnimatorSet = new RippleComponent.RenderNodeAnimatorSet();
      localRenderNodeAnimatorSet.add(paramPaint);
      localRenderNodeAnimatorSet.add(localRenderNodeAnimator3);
      localRenderNodeAnimatorSet.add(localRenderNodeAnimator1);
      localRenderNodeAnimatorSet.add(localRenderNodeAnimator2);
      return localRenderNodeAnimatorSet;
      k = getRadiusExitDuration();
      j = k;
    }
  }
  
  protected Animator createSoftwareEnter(boolean paramBoolean)
  {
    if (this.mIsBounded) {
      return null;
    }
    int i = (int)(Math.sqrt(this.mTargetRadius / 1024.0F * this.mDensityScale) * 1000.0D + 0.5D);
    ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(this, TWEEN_RADIUS, new float[] { 1.0F });
    localObjectAnimator1.setAutoCancel(true);
    localObjectAnimator1.setDuration(i);
    localObjectAnimator1.setInterpolator(LINEAR_INTERPOLATOR);
    localObjectAnimator1.setStartDelay(80L);
    ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(this, TWEEN_ORIGIN, new float[] { 1.0F });
    localObjectAnimator2.setAutoCancel(true);
    localObjectAnimator2.setDuration(i);
    localObjectAnimator2.setInterpolator(LINEAR_INTERPOLATOR);
    localObjectAnimator2.setStartDelay(80L);
    ObjectAnimator localObjectAnimator3 = ObjectAnimator.ofFloat(this, OPACITY, new float[] { 1.0F });
    localObjectAnimator3.setAutoCancel(true);
    localObjectAnimator3.setDuration(120L);
    localObjectAnimator3.setInterpolator(LINEAR_INTERPOLATOR);
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.play(localObjectAnimator2).with(localObjectAnimator1).with(localObjectAnimator3);
    return localAnimatorSet;
  }
  
  protected Animator createSoftwareExit()
  {
    int k;
    int j;
    if (this.mIsBounded)
    {
      computeBoundedTargetValues();
      k = 800;
      j = 300;
    }
    for (int i = 400;; i = getOpacityExitDuration())
    {
      ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(this, TWEEN_RADIUS, new float[] { 1.0F });
      localObjectAnimator1.setAutoCancel(true);
      localObjectAnimator1.setDuration(k);
      localObjectAnimator1.setInterpolator(DECELERATE_INTERPOLATOR);
      ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(this, TWEEN_ORIGIN, new float[] { 1.0F });
      localObjectAnimator2.setAutoCancel(true);
      localObjectAnimator2.setDuration(j);
      localObjectAnimator2.setInterpolator(DECELERATE_INTERPOLATOR);
      ObjectAnimator localObjectAnimator3 = ObjectAnimator.ofFloat(this, OPACITY, new float[] { 0.0F });
      localObjectAnimator3.setAutoCancel(true);
      localObjectAnimator3.setDuration(i);
      localObjectAnimator3.setInterpolator(LINEAR_INTERPOLATOR);
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.play(localObjectAnimator2).with(localObjectAnimator1).with(localObjectAnimator3);
      localAnimatorSet.addListener(this.mAnimationListener);
      return localAnimatorSet;
      k = getRadiusExitDuration();
      j = k;
    }
  }
  
  protected boolean drawHardware(DisplayListCanvas paramDisplayListCanvas)
  {
    paramDisplayListCanvas.drawCircle(this.mPropX, this.mPropY, this.mPropRadius, this.mPropPaint);
    return true;
  }
  
  protected boolean drawSoftware(Canvas paramCanvas, Paint paramPaint)
  {
    boolean bool2 = false;
    int i = paramPaint.getAlpha();
    int j = (int)(i * this.mOpacity + 0.5F);
    float f1 = getCurrentRadius();
    boolean bool1 = bool2;
    if (j > 0)
    {
      bool1 = bool2;
      if (f1 > 0.0F)
      {
        float f2 = getCurrentX();
        float f3 = getCurrentY();
        paramPaint.setAlpha(j);
        paramCanvas.drawCircle(f2, f3, f1, paramPaint);
        paramPaint.setAlpha(i);
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void getBounds(Rect paramRect)
  {
    int i = (int)this.mTargetX;
    int j = (int)this.mTargetY;
    int k = (int)this.mTargetRadius + 1;
    paramRect.set(i - k, j - k, i + k, j + k);
  }
  
  public boolean hasFinishedExit()
  {
    return this.mHasFinishedExit;
  }
  
  protected void jumpValuesToExit()
  {
    this.mOpacity = 0.0F;
    this.mTweenX = 1.0F;
    this.mTweenY = 1.0F;
    this.mTweenRadius = 1.0F;
  }
  
  public void move(float paramFloat1, float paramFloat2)
  {
    this.mStartingX = paramFloat1;
    this.mStartingY = paramFloat2;
    clampStartingPosition();
  }
  
  protected void onTargetRadiusChanged(float paramFloat)
  {
    clampStartingPosition();
  }
  
  private static final class LogDecelerateInterpolator
    implements TimeInterpolator
  {
    private final float mBase;
    private final float mDrift;
    private final float mOutputScale;
    private final float mTimeScale;
    
    public LogDecelerateInterpolator(float paramFloat1, float paramFloat2, float paramFloat3)
    {
      this.mBase = paramFloat1;
      this.mDrift = paramFloat3;
      this.mTimeScale = (1.0F / paramFloat2);
      this.mOutputScale = (1.0F / computeLog(1.0F));
    }
    
    private float computeLog(float paramFloat)
    {
      return 1.0F - (float)Math.pow(this.mBase, -paramFloat * this.mTimeScale) + this.mDrift * paramFloat;
    }
    
    public float getInterpolation(float paramFloat)
    {
      return computeLog(paramFloat) * this.mOutputScale;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/RippleForeground.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */