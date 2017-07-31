package com.oneplus.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import com.oneplus.widget.ShadowTextRenderer;

public class ShadowTextDrawable
  extends Drawable
{
  private final ShadowTextRenderer m_Renderer = new ShadowTextRenderer();
  private final Rect m_TextBounds = new Rect();
  
  public ShadowTextDrawable() {}
  
  public ShadowTextDrawable(CharSequence paramCharSequence)
  {
    this.m_Renderer.setText(paramCharSequence);
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    this.m_Renderer.getBounds(this.m_TextBounds);
    if (this.m_TextBounds.isEmpty()) {
      return;
    }
    int i = localRect.left;
    int j = (localRect.width() - this.m_TextBounds.width()) / 2;
    int k = localRect.top;
    int m = (localRect.height() - this.m_TextBounds.height()) / 2;
    this.m_Renderer.draw(paramCanvas, i + j, k + m);
  }
  
  public int getIntrinsicHeight()
  {
    this.m_Renderer.getBounds(this.m_TextBounds);
    return this.m_TextBounds.height();
  }
  
  public int getIntrinsicWidth()
  {
    this.m_Renderer.getBounds(this.m_TextBounds);
    return this.m_TextBounds.width();
  }
  
  public int getOpacity()
  {
    return 1;
  }
  
  public void getTextBounds(Rect paramRect)
  {
    this.m_Renderer.getBounds(this.m_TextBounds);
    paramRect.set(this.m_TextBounds);
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_Renderer.setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.m_Renderer.setColorFilter(paramColorFilter);
  }
  
  public void setShadow(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt)
  {
    this.m_Renderer.setShadow(paramFloat1, paramFloat2, paramFloat3, paramInt);
  }
  
  public void setText(CharSequence paramCharSequence)
  {
    this.m_Renderer.setText(paramCharSequence);
  }
  
  public void setTextAppearance(Context paramContext, int paramInt)
  {
    this.m_Renderer.setTextAppearance(paramContext, paramInt);
  }
  
  public void setTextColor(int paramInt)
  {
    this.m_Renderer.setColor(paramInt);
  }
  
  public void setTextSize(float paramFloat)
  {
    this.m_Renderer.setTextSize(paramFloat);
  }
  
  public void setTypeface(Typeface paramTypeface)
  {
    this.m_Renderer.setTypeface(paramTypeface);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/drawable/ShadowTextDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */