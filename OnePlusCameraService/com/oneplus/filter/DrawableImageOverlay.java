package com.oneplus.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class DrawableImageOverlay
  implements ImageOverlay
{
  private Drawable m_Drawable;
  
  public DrawableImageOverlay()
  {
    this(null);
  }
  
  public DrawableImageOverlay(Drawable paramDrawable)
  {
    this.m_Drawable = paramDrawable;
  }
  
  public void apply(Bitmap paramBitmap, int paramInt)
  {
    if ((this.m_Drawable == null) || (paramBitmap == null)) {
      return;
    }
    paramBitmap = new Canvas(paramBitmap);
    this.m_Drawable.draw(paramBitmap);
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_Drawable.setAlpha(paramInt);
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
  
  public void setDrawable(Drawable paramDrawable)
  {
    this.m_Drawable = paramDrawable;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/filter/DrawableImageOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */