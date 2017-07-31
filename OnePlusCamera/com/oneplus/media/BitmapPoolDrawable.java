package com.oneplus.media;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import com.oneplus.base.Handle;

public class BitmapPoolDrawable
  extends Drawable
{
  private int m_Alpha = 255;
  private final BitmapPool m_BitmapPool;
  private Paint m_DummyPaint;
  private final String m_FilePath;
  private final Handler m_Handler;
  private BitmapDrawable m_InternalDrawable;
  private boolean m_IsDecoding;
  private final int m_MaxHeight;
  private final int m_MaxWidth;
  private final int m_MediaType;
  private final boolean m_UseDummyColor;
  
  public BitmapPoolDrawable(BitmapPool paramBitmapPool, String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (paramBitmapPool == null) {
      throw new IllegalArgumentException("No bitmap pool.");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("No file path.");
    }
    this.m_BitmapPool = paramBitmapPool;
    this.m_FilePath = paramString;
    this.m_MaxWidth = paramInt2;
    this.m_MaxHeight = paramInt3;
    this.m_MediaType = paramInt1;
    this.m_UseDummyColor = paramBoolean;
    this.m_Handler = new Handler();
  }
  
  private boolean checkBitmap()
  {
    if (this.m_InternalDrawable != null) {
      return true;
    }
    if (!this.m_IsDecoding)
    {
      this.m_IsDecoding = true;
      this.m_BitmapPool.decode(this.m_FilePath, this.m_MediaType, this.m_MaxWidth, this.m_MaxHeight, new BitmapPool.Callback()
      {
        public void onBitmapDecoded(Handle paramAnonymousHandle, String paramAnonymousString, Bitmap paramAnonymousBitmap)
        {
          if (paramAnonymousBitmap != null)
          {
            BitmapPoolDrawable.-set1(BitmapPoolDrawable.this, false);
            BitmapPoolDrawable.-set0(BitmapPoolDrawable.this, new BitmapDrawable(paramAnonymousBitmap));
            BitmapPoolDrawable.-get1(BitmapPoolDrawable.this).setAlpha(BitmapPoolDrawable.-get0(BitmapPoolDrawable.this));
            BitmapPoolDrawable.this.invalidateSelf();
          }
        }
      }, this.m_Handler);
    }
    return this.m_InternalDrawable != null;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (checkBitmap())
    {
      this.m_InternalDrawable.setBounds(getBounds());
      this.m_InternalDrawable.draw(paramCanvas);
    }
    while (!this.m_UseDummyColor) {
      return;
    }
    if (this.m_DummyPaint == null)
    {
      this.m_DummyPaint = new Paint();
      this.m_DummyPaint.setColor(Color.argb(255, 80, 80, 80));
    }
    paramCanvas.drawRect(getBounds(), this.m_DummyPaint);
  }
  
  public int getIntrinsicHeight()
  {
    if (this.m_InternalDrawable != null) {
      return this.m_InternalDrawable.getBitmap().getHeight();
    }
    return 0;
  }
  
  public int getIntrinsicWidth()
  {
    if (this.m_InternalDrawable != null) {
      return this.m_InternalDrawable.getBitmap().getWidth();
    }
    return 0;
  }
  
  public int getOpacity()
  {
    if (this.m_InternalDrawable != null) {
      return this.m_InternalDrawable.getOpacity();
    }
    return 0;
  }
  
  public void setAlpha(int paramInt)
  {
    if (this.m_InternalDrawable != null)
    {
      this.m_InternalDrawable.setAlpha(paramInt);
      return;
    }
    this.m_Alpha = paramInt;
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/BitmapPoolDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */