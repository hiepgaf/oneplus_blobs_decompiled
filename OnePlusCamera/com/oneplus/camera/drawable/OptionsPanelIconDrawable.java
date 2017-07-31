package com.oneplus.camera.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class OptionsPanelIconDrawable
  extends Drawable
{
  private static final long ANIMATION_FPS = 10L;
  private static final Interpolator INTERPOLATOR_END = new DecelerateInterpolator(2.0F);
  private static String TAG = "OptionsPanelIconDrawable";
  private boolean m_AnimationRunning = false;
  private boolean m_AnimationStarted = false;
  private final Runnable m_AnimationUpdate = new Runnable()
  {
    public void run()
    {
      OptionsPanelIconDrawable.this.updateAnimation();
    }
  };
  private Drawable m_CurrentDrawables;
  private float m_Decelerate;
  private long m_Duration = 0L;
  private Drawable m_NextDrawable;
  private int m_OuterBoundHeight = 108;
  private float m_Progress;
  private long m_StartTime;
  
  public OptionsPanelIconDrawable(Drawable paramDrawable)
  {
    this.m_CurrentDrawables = paramDrawable;
  }
  
  private boolean isRunning()
  {
    return this.m_AnimationRunning;
  }
  
  private void setBounds(Drawable paramDrawable)
  {
    Rect localRect = getBounds();
    int i = localRect.left + (localRect.width() - paramDrawable.getIntrinsicWidth()) / 2;
    int j = localRect.top + (localRect.height() - paramDrawable.getIntrinsicHeight()) / 2;
    paramDrawable.setBounds(i, j, paramDrawable.getIntrinsicWidth() + i, paramDrawable.getIntrinsicHeight() + j);
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (isRunning())
    {
      paramCanvas.save();
      paramCanvas.translate(0.0F, this.m_OuterBoundHeight * this.m_Progress * this.m_Decelerate);
      this.m_CurrentDrawables.draw(paramCanvas);
      paramCanvas.restore();
      paramCanvas.save();
      paramCanvas.translate(0.0F, -this.m_OuterBoundHeight + this.m_OuterBoundHeight * this.m_Progress * this.m_Decelerate);
      this.m_NextDrawable.draw(paramCanvas);
      paramCanvas.restore();
      this.m_AnimationStarted = true;
      return;
    }
    if (this.m_AnimationStarted)
    {
      this.m_AnimationStarted = false;
      this.m_CurrentDrawables = this.m_NextDrawable;
      this.m_NextDrawable = null;
      this.m_StartTime = 0L;
    }
    setBounds(this.m_CurrentDrawables);
    paramCanvas.save();
    paramCanvas.translate(0.0F, 0.0F);
    this.m_CurrentDrawables.draw(paramCanvas);
    paramCanvas.restore();
  }
  
  public int getIntrinsicHeight()
  {
    return this.m_CurrentDrawables.getIntrinsicHeight();
  }
  
  public int getIntrinsicWidth()
  {
    return this.m_CurrentDrawables.getIntrinsicWidth();
  }
  
  public int getOpacity()
  {
    return this.m_CurrentDrawables.getOpacity();
  }
  
  public boolean isStateful()
  {
    return true;
  }
  
  public void scheduleSelf(Runnable paramRunnable, long paramLong)
  {
    this.m_AnimationRunning = true;
    if (this.m_StartTime == 0L) {
      this.m_StartTime = SystemClock.uptimeMillis();
    }
    super.scheduleSelf(paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_CurrentDrawables.setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.m_CurrentDrawables.setColorFilter(paramColorFilter);
  }
  
  public void startAnimation(Drawable paramDrawable, long paramLong)
  {
    if (paramDrawable == null) {
      return;
    }
    this.m_StartTime = 0L;
    this.m_Duration = paramLong;
    this.m_NextDrawable = paramDrawable;
    setBounds(this.m_NextDrawable);
    scheduleSelf(this.m_AnimationUpdate, SystemClock.uptimeMillis() + 10L);
    invalidateSelf();
  }
  
  protected void updateAnimation()
  {
    this.m_Progress = Math.min(1.0F, (float)(SystemClock.uptimeMillis() - this.m_StartTime) / (float)this.m_Duration);
    this.m_Decelerate = INTERPOLATOR_END.getInterpolation(this.m_Progress);
    if (this.m_Progress == 1.0F) {
      this.m_AnimationRunning = false;
    }
    if (isRunning()) {
      scheduleSelf(this.m_AnimationUpdate, SystemClock.uptimeMillis() + 10L);
    }
    invalidateSelf();
  }
  
  public void updateImageDrawable(Drawable paramDrawable)
  {
    if (!isRunning()) {
      this.m_CurrentDrawables = paramDrawable;
    }
    invalidateSelf();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/drawable/OptionsPanelIconDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */