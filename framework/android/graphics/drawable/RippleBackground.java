package android.graphics.drawable;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.FloatProperty;
import android.view.DisplayListCanvas;
import android.view.RenderNodeAnimator;
import android.view.animation.LinearInterpolator;

class RippleBackground
  extends RippleComponent
{
  private static final TimeInterpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
  private static final BackgroundProperty OPACITY = new BackgroundProperty("opacity")
  {
    public Float get(RippleBackground paramAnonymousRippleBackground)
    {
      return Float.valueOf(RippleBackground.-get0(paramAnonymousRippleBackground));
    }
    
    public void setValue(RippleBackground paramAnonymousRippleBackground, float paramAnonymousFloat)
    {
      RippleBackground.-set0(paramAnonymousRippleBackground, paramAnonymousFloat);
      paramAnonymousRippleBackground.invalidateSelf();
    }
  };
  private static final int OPACITY_ENTER_DURATION = 600;
  private static final int OPACITY_ENTER_DURATION_FAST = 120;
  private static final int OPACITY_EXIT_DURATION = 480;
  private boolean mIsBounded;
  private float mOpacity = 0.0F;
  private CanvasProperty<Paint> mPropPaint;
  private CanvasProperty<Float> mPropRadius;
  private CanvasProperty<Float> mPropX;
  private CanvasProperty<Float> mPropY;
  
  public RippleBackground(RippleDrawable paramRippleDrawable, Rect paramRect, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramRippleDrawable, paramRect, paramBoolean2);
    this.mIsBounded = paramBoolean1;
  }
  
  protected RippleComponent.RenderNodeAnimatorSet createHardwareExit(Paint paramPaint)
  {
    RippleComponent.RenderNodeAnimatorSet localRenderNodeAnimatorSet = new RippleComponent.RenderNodeAnimatorSet();
    int j = paramPaint.getAlpha();
    paramPaint.setAlpha((int)(this.mOpacity * j + 0.5F));
    this.mPropPaint = CanvasProperty.createPaint(paramPaint);
    this.mPropRadius = CanvasProperty.createFloat(this.mTargetRadius);
    this.mPropX = CanvasProperty.createFloat(0.0F);
    this.mPropY = CanvasProperty.createFloat(0.0F);
    if (this.mIsBounded) {}
    for (int i = (int)((1.0F - this.mOpacity) * 120.0F);; i = 0)
    {
      paramPaint = new RenderNodeAnimator(this.mPropPaint, 1, 0.0F);
      paramPaint.setInterpolator(LINEAR_INTERPOLATOR);
      paramPaint.setDuration(480L);
      if (i > 0)
      {
        paramPaint.setStartDelay(i);
        paramPaint.setStartValue(j);
      }
      localRenderNodeAnimatorSet.add(paramPaint);
      if (i > 0)
      {
        paramPaint = new RenderNodeAnimator(this.mPropPaint, 1, j);
        paramPaint.setInterpolator(LINEAR_INTERPOLATOR);
        paramPaint.setDuration(i);
        localRenderNodeAnimatorSet.add(paramPaint);
      }
      return localRenderNodeAnimatorSet;
    }
  }
  
  protected Animator createSoftwareEnter(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 120;; i = 600)
    {
      i = (int)((1.0F - this.mOpacity) * i);
      ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this, OPACITY, new float[] { 1.0F });
      localObjectAnimator.setAutoCancel(true);
      localObjectAnimator.setDuration(i);
      localObjectAnimator.setInterpolator(LINEAR_INTERPOLATOR);
      return localObjectAnimator;
    }
  }
  
  protected Animator createSoftwareExit()
  {
    AnimatorSet localAnimatorSet = new AnimatorSet();
    Object localObject = ObjectAnimator.ofFloat(this, OPACITY, new float[] { 0.0F });
    ((ObjectAnimator)localObject).setInterpolator(LINEAR_INTERPOLATOR);
    ((ObjectAnimator)localObject).setDuration(480L);
    ((ObjectAnimator)localObject).setAutoCancel(true);
    localObject = localAnimatorSet.play((Animator)localObject);
    if (this.mIsBounded) {}
    for (int i = (int)((1.0F - this.mOpacity) * 120.0F);; i = 0)
    {
      if (i > 0)
      {
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this, OPACITY, new float[] { 1.0F });
        localObjectAnimator.setInterpolator(LINEAR_INTERPOLATOR);
        localObjectAnimator.setDuration(i);
        localObjectAnimator.setAutoCancel(true);
        ((AnimatorSet.Builder)localObject).after(localObjectAnimator);
      }
      return localAnimatorSet;
    }
  }
  
  protected boolean drawHardware(DisplayListCanvas paramDisplayListCanvas)
  {
    paramDisplayListCanvas.drawCircle(this.mPropX, this.mPropY, this.mPropRadius, this.mPropPaint);
    return true;
  }
  
  protected boolean drawSoftware(Canvas paramCanvas, Paint paramPaint)
  {
    boolean bool = false;
    int i = paramPaint.getAlpha();
    int j = (int)(i * this.mOpacity + 0.5F);
    if (j > 0)
    {
      paramPaint.setAlpha(j);
      paramCanvas.drawCircle(0.0F, 0.0F, this.mTargetRadius, paramPaint);
      paramPaint.setAlpha(i);
      bool = true;
    }
    return bool;
  }
  
  public boolean isVisible()
  {
    if (this.mOpacity <= 0.0F) {
      return isHardwareAnimating();
    }
    return true;
  }
  
  protected void jumpValuesToExit()
  {
    this.mOpacity = 0.0F;
  }
  
  private static abstract class BackgroundProperty
    extends FloatProperty<RippleBackground>
  {
    public BackgroundProperty(String paramString)
    {
      super();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/RippleBackground.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */