package android.graphics.drawable;

import android.animation.Animator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.DisplayListCanvas;
import android.view.RenderNodeAnimator;
import java.util.ArrayList;

abstract class RippleComponent
{
  protected final Rect mBounds;
  protected float mDensityScale;
  private final boolean mForceSoftware;
  private RenderNodeAnimatorSet mHardwareAnimator;
  private boolean mHasDisplayListCanvas;
  private boolean mHasMaxRadius;
  private boolean mHasPendingHardwareAnimator;
  private final RippleDrawable mOwner;
  private Animator mSoftwareAnimator;
  protected float mTargetRadius;
  
  public RippleComponent(RippleDrawable paramRippleDrawable, Rect paramRect, boolean paramBoolean)
  {
    this.mOwner = paramRippleDrawable;
    this.mBounds = paramRect;
    this.mForceSoftware = paramBoolean;
  }
  
  private void cancelSoftwareAnimations()
  {
    if (this.mSoftwareAnimator != null)
    {
      this.mSoftwareAnimator.cancel();
      this.mSoftwareAnimator = null;
    }
  }
  
  private void endHardwareAnimations()
  {
    if (this.mHardwareAnimator != null)
    {
      this.mHardwareAnimator.end();
      this.mHardwareAnimator = null;
    }
    if (this.mHasPendingHardwareAnimator)
    {
      this.mHasPendingHardwareAnimator = false;
      jumpValuesToExit();
    }
  }
  
  private void endSoftwareAnimations()
  {
    if (this.mSoftwareAnimator != null)
    {
      this.mSoftwareAnimator.end();
      this.mSoftwareAnimator = null;
    }
  }
  
  private static float getTargetRadius(Rect paramRect)
  {
    float f1 = paramRect.width() / 2.0F;
    float f2 = paramRect.height() / 2.0F;
    return (float)Math.sqrt(f1 * f1 + f2 * f2);
  }
  
  private void startPendingAnimation(DisplayListCanvas paramDisplayListCanvas, Paint paramPaint)
  {
    if (this.mHasPendingHardwareAnimator)
    {
      this.mHasPendingHardwareAnimator = false;
      this.mHardwareAnimator = createHardwareExit(new Paint(paramPaint));
      this.mHardwareAnimator.start(paramDisplayListCanvas);
      jumpValuesToExit();
    }
  }
  
  public void cancel()
  {
    cancelSoftwareAnimations();
    endHardwareAnimations();
  }
  
  protected abstract RenderNodeAnimatorSet createHardwareExit(Paint paramPaint);
  
  protected abstract Animator createSoftwareEnter(boolean paramBoolean);
  
  protected abstract Animator createSoftwareExit();
  
  public boolean draw(Canvas paramCanvas, Paint paramPaint)
  {
    if ((!this.mForceSoftware) && (paramCanvas.isHardwareAccelerated())) {}
    for (boolean bool = paramCanvas instanceof DisplayListCanvas;; bool = false)
    {
      if (this.mHasDisplayListCanvas != bool)
      {
        this.mHasDisplayListCanvas = bool;
        if (!bool) {
          endHardwareAnimations();
        }
      }
      if (!bool) {
        break;
      }
      DisplayListCanvas localDisplayListCanvas = (DisplayListCanvas)paramCanvas;
      startPendingAnimation(localDisplayListCanvas, paramPaint);
      if (this.mHardwareAnimator == null) {
        break;
      }
      return drawHardware(localDisplayListCanvas);
    }
    return drawSoftware(paramCanvas, paramPaint);
  }
  
  protected abstract boolean drawHardware(DisplayListCanvas paramDisplayListCanvas);
  
  protected abstract boolean drawSoftware(Canvas paramCanvas, Paint paramPaint);
  
  public void end()
  {
    endSoftwareAnimations();
    endHardwareAnimations();
  }
  
  public final void enter(boolean paramBoolean)
  {
    cancel();
    this.mSoftwareAnimator = createSoftwareEnter(paramBoolean);
    if (this.mSoftwareAnimator != null) {
      this.mSoftwareAnimator.start();
    }
  }
  
  public final void exit()
  {
    cancel();
    if (this.mHasDisplayListCanvas)
    {
      this.mHasPendingHardwareAnimator = true;
      invalidateSelf();
      return;
    }
    this.mSoftwareAnimator = createSoftwareExit();
    this.mSoftwareAnimator.start();
  }
  
  public void getBounds(Rect paramRect)
  {
    int i = (int)Math.ceil(this.mTargetRadius);
    paramRect.set(-i, -i, i, i);
  }
  
  protected final void invalidateSelf()
  {
    this.mOwner.invalidateSelf(false);
  }
  
  protected final boolean isHardwareAnimating()
  {
    if ((this.mHardwareAnimator == null) || (!this.mHardwareAnimator.isRunning())) {
      return this.mHasPendingHardwareAnimator;
    }
    return true;
  }
  
  protected abstract void jumpValuesToExit();
  
  public void onBoundsChange()
  {
    if (!this.mHasMaxRadius)
    {
      this.mTargetRadius = getTargetRadius(this.mBounds);
      onTargetRadiusChanged(this.mTargetRadius);
    }
  }
  
  protected final void onHotspotBoundsChanged()
  {
    if (!this.mHasMaxRadius)
    {
      float f1 = this.mBounds.width() / 2.0F;
      float f2 = this.mBounds.height() / 2.0F;
      onTargetRadiusChanged((float)Math.sqrt(f1 * f1 + f2 * f2));
    }
  }
  
  protected void onTargetRadiusChanged(float paramFloat) {}
  
  public final void setup(float paramFloat, int paramInt)
  {
    if (paramFloat >= 0.0F) {
      this.mHasMaxRadius = true;
    }
    for (this.mTargetRadius = paramFloat;; this.mTargetRadius = getTargetRadius(this.mBounds))
    {
      this.mDensityScale = (paramInt * 0.00625F);
      onTargetRadiusChanged(this.mTargetRadius);
      return;
    }
  }
  
  public static class RenderNodeAnimatorSet
  {
    private final ArrayList<RenderNodeAnimator> mAnimators = new ArrayList();
    
    public void add(RenderNodeAnimator paramRenderNodeAnimator)
    {
      this.mAnimators.add(paramRenderNodeAnimator);
    }
    
    public void cancel()
    {
      ArrayList localArrayList = this.mAnimators;
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        ((RenderNodeAnimator)localArrayList.get(i)).cancel();
        i += 1;
      }
    }
    
    public void clear()
    {
      this.mAnimators.clear();
    }
    
    public void end()
    {
      ArrayList localArrayList = this.mAnimators;
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        ((RenderNodeAnimator)localArrayList.get(i)).end();
        i += 1;
      }
    }
    
    public boolean isRunning()
    {
      ArrayList localArrayList = this.mAnimators;
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        if (((RenderNodeAnimator)localArrayList.get(i)).isRunning()) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public void start(DisplayListCanvas paramDisplayListCanvas)
    {
      if (paramDisplayListCanvas == null) {
        throw new IllegalArgumentException("Hardware canvas must be non-null");
      }
      ArrayList localArrayList = this.mAnimators;
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        RenderNodeAnimator localRenderNodeAnimator = (RenderNodeAnimator)localArrayList.get(i);
        localRenderNodeAnimator.setTarget(paramDisplayListCanvas);
        localRenderNodeAnimator.start();
        i += 1;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/RippleComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */