package com.oneplus.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import com.oneplus.drawable.ShadowTextDrawable;

public class TextImageOverlay
  implements ImageOverlay
{
  private final ShadowTextDrawable m_Drawable = new ShadowTextDrawable();
  
  public TextImageOverlay()
  {
    this("");
  }
  
  public TextImageOverlay(String paramString)
  {
    this.m_Drawable.setText(paramString);
  }
  
  public void apply(Bitmap paramBitmap, int paramInt)
  {
    if (paramBitmap == null) {
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
    this.m_Drawable.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setBounds(Rect paramRect)
  {
    if (paramRect == null) {
      return;
    }
    setBounds(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public void setText(String paramString)
  {
    this.m_Drawable.setText(paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/filter/TextImageOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */