package com.oneplus.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

public class RotateAnimationDrawable
  extends Drawable
{
  private static final float DEGREE_INTERVAL_RESETTING = 20.0F;
  private static final long TIME_INTERVAL_RESETTING = 33L;
  private final Drawable m_BaseDrawable;
  private float m_CurrentDegrees;
  private final long m_DegreeInterval;
  private boolean m_IsResetting;
  private boolean m_IsStarted;
  private final long m_TimeInterval;
  private final Runnable m_UpdateRotationRunnable = new Runnable()
  {
    public void run()
    {
      if (RotateAnimationDrawable.-get2(RotateAnimationDrawable.this))
      {
        RotateAnimationDrawable localRotateAnimationDrawable = RotateAnimationDrawable.this;
        RotateAnimationDrawable.-set0(localRotateAnimationDrawable, RotateAnimationDrawable.-get0(localRotateAnimationDrawable) - 20.0F);
        if (RotateAnimationDrawable.-get0(RotateAnimationDrawable.this) <= 0.0F)
        {
          RotateAnimationDrawable.-set0(RotateAnimationDrawable.this, 0.0F);
          RotateAnimationDrawable.-set1(RotateAnimationDrawable.this, false);
        }
      }
      for (;;)
      {
        RotateAnimationDrawable.this.invalidateSelf();
        return;
        RotateAnimationDrawable.this.scheduleSelf(this, SystemClock.uptimeMillis() + 33L);
        continue;
        RotateAnimationDrawable.-set0(RotateAnimationDrawable.this, (RotateAnimationDrawable.-get0(RotateAnimationDrawable.this) + (float)RotateAnimationDrawable.-get1(RotateAnimationDrawable.this)) % 360.0F);
        if (Math.abs(RotateAnimationDrawable.-get0(RotateAnimationDrawable.this)) <= 0.01D) {
          RotateAnimationDrawable.-set0(RotateAnimationDrawable.this, 0.0F);
        }
        RotateAnimationDrawable.this.scheduleSelf(this, SystemClock.uptimeMillis() + RotateAnimationDrawable.-get3(RotateAnimationDrawable.this));
      }
    }
  };
  
  public RotateAnimationDrawable(Drawable paramDrawable, float paramFloat, int paramInt)
  {
    if (paramDrawable == null) {
      throw new IllegalArgumentException("No base drawable");
    }
    if (paramFloat < 0.0F) {
      throw new IllegalArgumentException("Invalid frame rate : " + paramFloat);
    }
    if (paramInt == 0) {
      throw new IllegalArgumentException("Invalid frame count : " + paramInt);
    }
    this.m_BaseDrawable = paramDrawable;
    this.m_DegreeInterval = (360 / paramInt);
    if (paramFloat > 0.0F) {}
    for (long l = (1000.0F / paramFloat);; l = -1L)
    {
      this.m_TimeInterval = l;
      return;
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    paramCanvas.save();
    paramCanvas.rotate(this.m_CurrentDegrees, localRect.centerX(), localRect.centerY());
    this.m_BaseDrawable.setBounds(localRect);
    this.m_BaseDrawable.draw(paramCanvas);
    paramCanvas.restore();
  }
  
  public int getIntrinsicHeight()
  {
    return this.m_BaseDrawable.getIntrinsicHeight();
  }
  
  public int getIntrinsicWidth()
  {
    return this.m_BaseDrawable.getIntrinsicWidth();
  }
  
  public int getOpacity()
  {
    return this.m_BaseDrawable.getOpacity();
  }
  
  public void reset()
  {
    if (this.m_IsResetting) {
      return;
    }
    this.m_IsResetting = true;
    scheduleSelf(this.m_UpdateRotationRunnable, SystemClock.uptimeMillis() + 33L);
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_BaseDrawable.setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.m_BaseDrawable.setColorFilter(paramColorFilter);
  }
  
  public void start()
  {
    if (this.m_IsStarted) {
      return;
    }
    this.m_IsResetting = false;
    this.m_IsStarted = true;
    scheduleSelf(this.m_UpdateRotationRunnable, SystemClock.uptimeMillis() + this.m_TimeInterval);
  }
  
  public void stop()
  {
    if (!this.m_IsStarted) {
      return;
    }
    this.m_IsStarted = false;
    unscheduleSelf(this.m_UpdateRotationRunnable);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/drawable/RotateAnimationDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */