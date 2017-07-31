package com.oneplus.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Handler;
import android.util.Size;

public class SizeDrawable
  extends Drawable
  implements Drawable.Callback
{
  private static final Size EMPTY_SIZE = new Size(-1, -1);
  private Drawable m_Drawable;
  private Handler m_Handler;
  private Size m_Size = EMPTY_SIZE;
  
  public SizeDrawable()
  {
    this(EMPTY_SIZE);
  }
  
  public SizeDrawable(int paramInt)
  {
    this(EMPTY_SIZE, paramInt);
  }
  
  public SizeDrawable(int paramInt1, int paramInt2)
  {
    initialize(paramInt1, paramInt2, -1, null, null, null);
  }
  
  public SizeDrawable(int paramInt1, int paramInt2, int paramInt3)
  {
    initialize(paramInt1, paramInt2, paramInt3, null, null, null);
  }
  
  public SizeDrawable(int paramInt1, int paramInt2, Resources paramResources, Bitmap paramBitmap)
  {
    initialize(paramInt1, paramInt2, -1, paramResources, paramBitmap, null);
  }
  
  public SizeDrawable(int paramInt1, int paramInt2, Drawable paramDrawable)
  {
    initialize(paramInt1, paramInt2, -1, null, null, paramDrawable);
  }
  
  public SizeDrawable(Resources paramResources, Bitmap paramBitmap)
  {
    this(EMPTY_SIZE, paramResources, paramBitmap);
  }
  
  public SizeDrawable(Drawable paramDrawable)
  {
    this(EMPTY_SIZE, paramDrawable);
  }
  
  public SizeDrawable(Size paramSize)
  {
    Size localSize = paramSize;
    if (paramSize == null) {
      localSize = EMPTY_SIZE;
    }
    initialize(localSize.getWidth(), localSize.getHeight(), -1, null, null, null);
  }
  
  public SizeDrawable(Size paramSize, int paramInt)
  {
    Size localSize = paramSize;
    if (paramSize == null) {
      localSize = EMPTY_SIZE;
    }
    initialize(localSize.getWidth(), localSize.getHeight(), paramInt, null, null, null);
  }
  
  public SizeDrawable(Size paramSize, Resources paramResources, Bitmap paramBitmap)
  {
    Size localSize = paramSize;
    if (paramSize == null) {
      localSize = EMPTY_SIZE;
    }
    initialize(localSize.getWidth(), localSize.getHeight(), -1, paramResources, paramBitmap, null);
  }
  
  public SizeDrawable(Size paramSize, Drawable paramDrawable)
  {
    Size localSize = paramSize;
    if (paramSize == null) {
      localSize = EMPTY_SIZE;
    }
    initialize(localSize.getWidth(), localSize.getHeight(), -1, null, null, paramDrawable);
  }
  
  private void initialize(int paramInt1, int paramInt2, int paramInt3, Resources paramResources, Bitmap paramBitmap, Drawable paramDrawable)
  {
    this.m_Size = new Size(paramInt1, paramInt2);
    if (paramInt3 > 0) {
      this.m_Drawable = new ColorDrawable(paramInt3);
    }
    for (;;)
    {
      if (this.m_Drawable != null) {
        this.m_Drawable.setCallback(this);
      }
      return;
      if ((paramResources != null) && (paramBitmap != null)) {
        this.m_Drawable = new BitmapDrawable(paramResources, paramBitmap);
      } else if (paramDrawable != null) {
        this.m_Drawable = paramDrawable;
      }
    }
  }
  
  private void resetDrawable()
  {
    if (this.m_Drawable == null) {
      return;
    }
    this.m_Drawable.setCallback(null);
    this.m_Drawable = null;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.m_Drawable == null) {
      return;
    }
    this.m_Drawable.draw(paramCanvas);
  }
  
  public int getIntrinsicHeight()
  {
    return this.m_Size.getHeight();
  }
  
  public int getIntrinsicWidth()
  {
    return this.m_Size.getWidth();
  }
  
  public int getOpacity()
  {
    if (this.m_Drawable == null) {
      return 0;
    }
    return this.m_Drawable.getOpacity();
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (this.m_Drawable == null) {
      return;
    }
    invalidateSelf();
  }
  
  public void release()
  {
    reset();
    this.m_Handler = null;
  }
  
  public void reset()
  {
    this.m_Size = EMPTY_SIZE;
    resetDrawable();
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    if (this.m_Drawable == null) {
      return;
    }
    if (paramDrawable == this.m_Drawable) {
      this.m_Handler.postAtTime(paramRunnable, paramLong);
    }
  }
  
  public void setAlpha(int paramInt)
  {
    if (this.m_Drawable == null) {
      return;
    }
    this.m_Drawable.setAlpha(paramInt);
  }
  
  public void setBitmap(Resources paramResources, Bitmap paramBitmap)
  {
    resetDrawable();
    if (paramBitmap != null)
    {
      this.m_Drawable = new BitmapDrawable(paramResources, paramBitmap);
      this.m_Drawable.setCallback(this);
    }
    for (;;)
    {
      invalidateSelf();
      return;
      this.m_Drawable = null;
    }
  }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.m_Drawable == null) {
      return;
    }
    this.m_Drawable.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setBounds(Rect paramRect)
  {
    if (paramRect == null) {
      return;
    }
    setBounds(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public void setColor(int paramInt)
  {
    resetDrawable();
    this.m_Drawable = new ColorDrawable(paramInt);
    this.m_Drawable.setCallback(this);
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    if (this.m_Drawable == null) {
      return;
    }
    this.m_Drawable.setColorFilter(paramColorFilter);
  }
  
  public void setDrawable(Drawable paramDrawable)
  {
    resetDrawable();
    this.m_Drawable = paramDrawable;
    if (this.m_Drawable != null) {
      this.m_Drawable.setCallback(this);
    }
    invalidateSelf();
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    this.m_Size = new Size(paramInt1, paramInt2);
  }
  
  public void setSize(Size paramSize)
  {
    if (paramSize == null) {
      return;
    }
    this.m_Size = new Size(paramSize.getWidth(), paramSize.getHeight());
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    if (this.m_Drawable == null) {
      return;
    }
    if (paramDrawable == this.m_Drawable) {
      this.m_Handler.removeCallbacks(paramRunnable);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/drawable/SizeDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */