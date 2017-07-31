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
import com.oneplus.io.FileUtils;

public class ThumbnailImageDrawable
  extends Drawable
{
  private static final int MAX_THUMBNAIL_IMAGE_SIDE = 1920;
  private static final int MAX_THUMBNAIL_IMAGE_SIDE_SMALL = 256;
  private int m_Alpha = 255;
  private Paint m_DummyPaint;
  private final String m_FilePath;
  private final Handler m_Handler;
  private boolean m_IsDecoding;
  private final int m_MediaType;
  private BitmapDrawable m_SmallThumbDrawable;
  private BitmapDrawable m_ThumbDrawable;
  private final boolean m_UseDummyColor;
  
  public ThumbnailImageDrawable(String paramString, int paramInt, boolean paramBoolean)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("No file path.");
    }
    this.m_FilePath = paramString;
    this.m_MediaType = paramInt;
    this.m_UseDummyColor = paramBoolean;
    this.m_Handler = new Handler();
  }
  
  private boolean checkBitmap()
  {
    if ((this.m_SmallThumbDrawable != null) || (this.m_ThumbDrawable != null)) {
      return true;
    }
    if (!this.m_IsDecoding)
    {
      this.m_IsDecoding = true;
      BitmapPool.DEFAULT_THUMBNAIL.decode(this.m_FilePath, this.m_MediaType, 1920, 1920, 2, new BitmapPool.Callback()
      {
        public void onBitmapDecoded(Handle paramAnonymousHandle, String paramAnonymousString, Bitmap paramAnonymousBitmap)
        {
          if (paramAnonymousBitmap != null)
          {
            ThumbnailImageDrawable.-set0(ThumbnailImageDrawable.this, false);
            ThumbnailImageDrawable.-set1(ThumbnailImageDrawable.this, null);
            ThumbnailImageDrawable.-set2(ThumbnailImageDrawable.this, new BitmapDrawable(paramAnonymousBitmap));
            ThumbnailImageDrawable.-get2(ThumbnailImageDrawable.this).setAlpha(ThumbnailImageDrawable.-get0(ThumbnailImageDrawable.this));
            ThumbnailImageDrawable.this.invalidateSelf();
          }
        }
      }, this.m_Handler);
      if ((this.m_ThumbDrawable == null) && (!FileUtils.isVideoFilePath(this.m_FilePath))) {
        break label95;
      }
    }
    while ((this.m_SmallThumbDrawable != null) || (this.m_ThumbDrawable != null))
    {
      return true;
      label95:
      BitmapPool.DEFAULT_THUMBNAIL_SMALL.decode(this.m_FilePath, this.m_MediaType, 256, 256, 2, new BitmapPool.Callback()
      {
        public void onBitmapDecoded(Handle paramAnonymousHandle, String paramAnonymousString, Bitmap paramAnonymousBitmap)
        {
          if ((paramAnonymousBitmap != null) && (ThumbnailImageDrawable.-get2(ThumbnailImageDrawable.this) == null))
          {
            ThumbnailImageDrawable.-set1(ThumbnailImageDrawable.this, new BitmapDrawable(paramAnonymousBitmap));
            ThumbnailImageDrawable.-get1(ThumbnailImageDrawable.this).setAlpha(ThumbnailImageDrawable.-get0(ThumbnailImageDrawable.this));
            ThumbnailImageDrawable.this.invalidateSelf();
          }
        }
      }, this.m_Handler);
    }
    return false;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (checkBitmap()) {
      if (this.m_ThumbDrawable != null)
      {
        this.m_ThumbDrawable.setBounds(getBounds());
        this.m_ThumbDrawable.draw(paramCanvas);
      }
    }
    while (!this.m_UseDummyColor)
    {
      do
      {
        return;
      } while (this.m_SmallThumbDrawable == null);
      this.m_SmallThumbDrawable.setBounds(getBounds());
      this.m_SmallThumbDrawable.draw(paramCanvas);
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
    if (this.m_ThumbDrawable != null) {
      return this.m_ThumbDrawable.getBitmap().getHeight();
    }
    if (this.m_SmallThumbDrawable != null) {
      return this.m_SmallThumbDrawable.getBitmap().getHeight();
    }
    return 0;
  }
  
  public int getIntrinsicWidth()
  {
    if (this.m_ThumbDrawable != null) {
      return this.m_ThumbDrawable.getBitmap().getWidth();
    }
    if (this.m_SmallThumbDrawable != null) {
      return this.m_SmallThumbDrawable.getBitmap().getWidth();
    }
    return 0;
  }
  
  public int getOpacity()
  {
    if (this.m_ThumbDrawable != null) {
      return this.m_ThumbDrawable.getOpacity();
    }
    if (this.m_SmallThumbDrawable != null) {
      return this.m_SmallThumbDrawable.getOpacity();
    }
    return 0;
  }
  
  public void setAlpha(int paramInt)
  {
    if (this.m_ThumbDrawable != null) {
      this.m_ThumbDrawable.setAlpha(paramInt);
    }
    if (this.m_SmallThumbDrawable != null) {
      this.m_SmallThumbDrawable.setAlpha(paramInt);
    }
    this.m_Alpha = paramInt;
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/ThumbnailImageDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */