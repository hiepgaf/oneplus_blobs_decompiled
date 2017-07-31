package com.oneplus.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Size;

public class ShadowTextRenderer
{
  private boolean m_HasStroke = false;
  private Size m_MaximumSize = new Size(-1, -1);
  private final Rect m_MeasuredTextBounds = new Rect();
  private final Paint m_StrokePaint;
  private String m_Text;
  private final Paint m_TextPaint = new Paint(1);
  private float m_TextSize;
  
  public ShadowTextRenderer()
  {
    this(null);
  }
  
  public ShadowTextRenderer(CharSequence paramCharSequence)
  {
    this.m_TextPaint.setStyle(Paint.Style.FILL);
    this.m_StrokePaint = new Paint(1);
    this.m_StrokePaint.setStyle(Paint.Style.STROKE);
    if (paramCharSequence != null) {
      str = paramCharSequence.toString();
    }
    this.m_Text = str;
  }
  
  private Typeface getTypefaceFromAttrs(String paramString)
  {
    return getTypefaceFromAttrs(paramString, 0);
  }
  
  private Typeface getTypefaceFromAttrs(String paramString, int paramInt)
  {
    if (paramString != null)
    {
      paramString = Typeface.create(paramString, paramInt);
      if (paramString != null) {
        return paramString;
      }
    }
    else
    {
      return Typeface.defaultFromStyle(paramInt);
    }
    return Typeface.SANS_SERIF;
  }
  
  private void measure()
  {
    if ((this.m_TextSize > 0.0F) && (this.m_MeasuredTextBounds.isEmpty()))
    {
      if ((this.m_Text != null) && (this.m_Text.length() != 0)) {}
    }
    else {
      return;
    }
    this.m_TextPaint.setTextSize(this.m_TextSize);
    this.m_StrokePaint.setTextSize(this.m_TextSize);
    measure(this.m_TextSize);
  }
  
  private void measure(float paramFloat)
  {
    this.m_TextPaint.getTextBounds(this.m_Text, 0, this.m_Text.length(), this.m_MeasuredTextBounds);
    if ((this.m_MaximumSize.getWidth() >= 0) && (this.m_MaximumSize.getHeight() >= 0) && (paramFloat > 12.0F) && (this.m_MeasuredTextBounds.width() > this.m_MaximumSize.getWidth()))
    {
      paramFloat -= 1.0F;
      this.m_TextPaint.setTextSize(paramFloat);
      this.m_StrokePaint.setTextSize(paramFloat);
      measure(paramFloat);
    }
  }
  
  public void draw(Canvas paramCanvas, float paramFloat1, float paramFloat2)
  {
    if (paramCanvas == null) {
      throw new IllegalArgumentException("Canvas could not be null");
    }
    if (this.m_Text == null) {
      return;
    }
    int i = this.m_Text.length();
    if (i == 0) {
      return;
    }
    measure();
    paramFloat2 += this.m_MeasuredTextBounds.height();
    paramCanvas.drawText(this.m_Text, 0, i, paramFloat1, paramFloat2, this.m_TextPaint);
    if (this.m_HasStroke) {
      paramCanvas.drawText(this.m_Text, 0, i, paramFloat1, paramFloat2, this.m_StrokePaint);
    }
  }
  
  public Rect getBounds()
  {
    measure();
    return new Rect(this.m_MeasuredTextBounds);
  }
  
  public void getBounds(Rect paramRect)
  {
    if (paramRect == null) {
      return;
    }
    measure();
    paramRect.set(this.m_MeasuredTextBounds);
  }
  
  public String getText()
  {
    return this.m_Text;
  }
  
  public float getTextSize()
  {
    return this.m_TextSize;
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_TextPaint.setAlpha(paramInt);
    this.m_StrokePaint.setAlpha(paramInt);
  }
  
  public void setColor(int paramInt)
  {
    this.m_TextPaint.setColor(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.m_TextPaint.setColorFilter(paramColorFilter);
  }
  
  public void setMaximumSize(int paramInt1, int paramInt2)
  {
    this.m_MaximumSize = new Size(paramInt1, paramInt2);
    this.m_MeasuredTextBounds.setEmpty();
  }
  
  public void setShadow(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt)
  {
    if (paramFloat1 <= 0.0F)
    {
      this.m_TextPaint.setShadowLayer(0.0F, 0.0F, 0.0F, -1);
      return;
    }
    this.m_TextPaint.setShadowLayer(paramFloat1, paramFloat2, paramFloat3, paramInt);
  }
  
  public void setStroke(boolean paramBoolean)
  {
    this.m_HasStroke = paramBoolean;
  }
  
  public void setText(CharSequence paramCharSequence)
  {
    String str = null;
    if (paramCharSequence != null) {
      str = paramCharSequence.toString();
    }
    this.m_Text = str;
    this.m_MeasuredTextBounds.setEmpty();
  }
  
  public void setTextAppearance(Context paramContext, int paramInt)
  {
    paramContext = paramContext.obtainStyledAttributes(paramInt, new int[] { 16842901, 16842903, 16842904, 16843105, 16843106, 16843107, 16843108, 16843692 });
    if (paramContext.hasValue(0))
    {
      this.m_TextSize = paramContext.getDimension(0, -1.0F);
      this.m_TextPaint.setTextSize(this.m_TextSize);
      this.m_StrokePaint.setTextSize(this.m_TextSize);
    }
    paramInt = 0;
    int i;
    if (paramContext.hasValue(2))
    {
      i = paramContext.getColor(2, -1);
      this.m_TextPaint.setColor(i);
    }
    if ((paramContext.hasValue(1)) || (paramContext.hasValue(7)))
    {
      paramInt = paramContext.getInt(1, -1);
      Typeface localTypeface = getTypefaceFromAttrs(paramContext.getString(7), paramInt);
      this.m_TextPaint.setTypeface(localTypeface);
      this.m_StrokePaint.setTypeface(localTypeface);
      paramInt = 1;
    }
    if ((paramContext.hasValue(3)) && (paramContext.hasValue(6)))
    {
      i = paramContext.getInt(3, -1);
      float f1 = paramContext.getFloat(6, -1.0F);
      float f2 = paramContext.getFloat(4, 0.0F);
      float f3 = paramContext.getFloat(5, 0.0F);
      this.m_TextPaint.setShadowLayer(f1, f2, f3, i);
    }
    if (paramInt != 0) {
      this.m_MeasuredTextBounds.setEmpty();
    }
    paramContext.recycle();
  }
  
  public void setTextSize(float paramFloat)
  {
    if (Math.abs(this.m_TextPaint.getTextSize() - paramFloat) >= 0.1D)
    {
      this.m_TextSize = paramFloat;
      this.m_TextPaint.setTextSize(paramFloat);
      this.m_StrokePaint.setTextSize(paramFloat);
      this.m_MeasuredTextBounds.setEmpty();
    }
  }
  
  public void setTypeface(Typeface paramTypeface)
  {
    this.m_TextPaint.setTypeface(paramTypeface);
    this.m_StrokePaint.setTypeface(paramTypeface);
    this.m_MeasuredTextBounds.setEmpty();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/widget/ShadowTextRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */