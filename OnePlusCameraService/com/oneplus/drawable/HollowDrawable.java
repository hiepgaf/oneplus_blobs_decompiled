package com.oneplus.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;

public class HollowDrawable
  extends Drawable
{
  private final Drawable.Callback m_Callback = new Drawable.Callback()
  {
    public void invalidateDrawable(Drawable paramAnonymousDrawable)
    {
      HollowDrawable.this.invalidateSelf();
    }
    
    public void scheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable, long paramAnonymousLong)
    {
      HollowDrawable.this.scheduleSelf(paramAnonymousRunnable, paramAnonymousLong);
    }
    
    public void unscheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable)
    {
      HollowDrawable.this.unscheduleSelf(paramAnonymousRunnable);
    }
  };
  private final Drawable m_Drawable;
  private final RectF[] m_HollowBounds;
  private Paint m_HollowPaint;
  
  public HollowDrawable(Drawable paramDrawable, Rect... paramVarArgs)
  {
    this.m_Drawable = paramDrawable;
    paramDrawable.setCallback(this.m_Callback);
    this.m_HollowBounds = new RectF[paramVarArgs.length];
    int i = paramVarArgs.length - 1;
    while (i >= 0)
    {
      this.m_HollowBounds[i] = new RectF(paramVarArgs[i]);
      i -= 1;
    }
  }
  
  public HollowDrawable(Drawable paramDrawable, RectF... paramVarArgs)
  {
    this.m_Drawable = paramDrawable;
    paramDrawable.setCallback(this.m_Callback);
    this.m_HollowBounds = new RectF[paramVarArgs.length];
    int i = paramVarArgs.length - 1;
    while (i >= 0)
    {
      this.m_HollowBounds[i] = new RectF(paramVarArgs[i]);
      i -= 1;
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.m_HollowBounds.length == 0)
    {
      this.m_Drawable.draw(paramCanvas);
      return;
    }
    paramCanvas.saveLayer(null, null);
    this.m_Drawable.draw(paramCanvas);
    Rect localRect = getBounds();
    if (this.m_HollowPaint == null)
    {
      this.m_HollowPaint = new Paint();
      this.m_HollowPaint.setAntiAlias(true);
      this.m_HollowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      this.m_HollowPaint.setStyle(Paint.Style.FILL);
    }
    int i = this.m_HollowBounds.length - 1;
    while (i >= 0)
    {
      RectF localRectF = this.m_HollowBounds[i];
      float f1 = localRect.left;
      float f2 = localRectF.left;
      float f3 = localRect.top;
      float f4 = localRectF.top;
      float f5 = localRect.left;
      float f6 = localRectF.right;
      float f7 = localRect.top;
      paramCanvas.drawOval(f2 + f1, f4 + f3, f6 + f5, localRectF.bottom + f7, this.m_HollowPaint);
      i -= 1;
    }
    paramCanvas.restore();
  }
  
  public int getIntrinsicHeight()
  {
    return this.m_Drawable.getIntrinsicHeight();
  }
  
  public int getIntrinsicWidth()
  {
    return this.m_Drawable.getIntrinsicWidth();
  }
  
  public int getMinimumHeight()
  {
    return this.m_Drawable.getMinimumHeight();
  }
  
  public int getMinimumWidth()
  {
    return this.m_Drawable.getMinimumWidth();
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    this.m_Drawable.setBounds(paramRect);
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_Drawable.setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.m_Drawable.setColorFilter(paramColorFilter);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/drawable/HollowDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */