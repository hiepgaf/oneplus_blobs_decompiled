package com.oneplus.camera.drawable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;

public class ShadowDrawable
  extends Drawable
{
  private static final int SHARED_BITMAP_BUFFER_SIZE = 640;
  private static final ThreadLocal<Bitmap> SHARED_DRAWABLE_BITMAP_BUFFER = new ThreadLocal();
  private static final ThreadLocal<Canvas> SHARED_DRAWABLE_BITMAP_BUFFER_CANVAS = new ThreadLocal();
  private static final int[] STYLED_ATTRS = { 16843105, 16843106, 16843107, 16843108 };
  private Rect m_ContentBounds = new Rect();
  private final Rect m_DestRect = new Rect();
  private final Drawable m_Drawable;
  private Rect m_NoRadiusShadowBounds = new Rect();
  private Bitmap m_OutputBuffer;
  private Canvas m_OutputBufferCanvas;
  private int m_PaddingBottom;
  private int m_PaddingEnd;
  private int m_PaddingStart;
  private int m_PaddingTop;
  private Rect m_ShadowBounds = new Rect();
  private int m_ShadowColor;
  private int m_ShadowDx;
  private int m_ShadowDy;
  private final Paint m_ShadowPaint = new Paint();
  private float m_ShadowRadius;
  private int m_ShadowRadiusCeiling;
  private final Rect m_SrcRect = new Rect();
  private Rect m_UnionBounds = new Rect();
  
  public ShadowDrawable(Context paramContext, int paramInt)
  {
    this(paramContext, paramContext.getDrawable(paramInt), 2131492904);
  }
  
  public ShadowDrawable(Context paramContext, int paramInt1, int paramInt2)
  {
    this(paramContext, paramContext.getDrawable(paramInt1), paramInt2);
  }
  
  public ShadowDrawable(Context paramContext, Drawable paramDrawable)
  {
    this(paramContext, paramDrawable, 2131492904);
  }
  
  public ShadowDrawable(Context paramContext, Drawable paramDrawable, int paramInt)
  {
    this.m_Drawable = paramDrawable;
    this.m_Drawable.setCallback(new Drawable.Callback()
    {
      public void invalidateDrawable(Drawable paramAnonymousDrawable)
      {
        ShadowDrawable.this.invalidateSelf();
      }
      
      public void scheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable, long paramAnonymousLong)
      {
        ShadowDrawable.this.scheduleSelf(paramAnonymousRunnable, paramAnonymousLong);
      }
      
      public void unscheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable)
      {
        ShadowDrawable.this.unscheduleSelf(paramAnonymousRunnable);
      }
    });
    if (paramInt != 0)
    {
      paramContext = paramContext.obtainStyledAttributes(paramInt, STYLED_ATTRS);
      this.m_ShadowColor = paramContext.getColor(0, 0);
      this.m_ShadowDx = Math.round(paramContext.getFloat(1, 0.0F));
      this.m_ShadowDy = Math.round(paramContext.getFloat(2, 0.0F));
      this.m_ShadowRadius = paramContext.getFloat(3, 0.0F);
      if (this.m_ShadowRadius < 0.0F) {
        this.m_ShadowRadius = 0.0F;
      }
      this.m_ShadowRadiusCeiling = ((int)Math.ceil(this.m_ShadowRadius));
      paramInt = Color.alpha(this.m_ShadowColor);
      if ((this.m_ShadowRadius <= 0.0F) || (paramInt <= 0)) {
        break label257;
      }
      paramContext = new BlurMaskFilter(this.m_ShadowRadius, BlurMaskFilter.Blur.NORMAL);
      this.m_ShadowPaint.setAlpha(paramInt);
      this.m_ShadowPaint.setMaskFilter(paramContext);
    }
    for (;;)
    {
      this.m_ShadowPaint.setAntiAlias(true);
      this.m_ShadowPaint.setFilterBitmap(true);
      return;
      label257:
      this.m_ShadowPaint.setAlpha(0);
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    this.m_ContentBounds.set(localRect.left + this.m_PaddingStart, localRect.top + this.m_PaddingTop, localRect.right - this.m_PaddingEnd, localRect.bottom - this.m_PaddingBottom);
    if ((this.m_ContentBounds.width() <= 0) || (this.m_ContentBounds.height() <= 0)) {
      return;
    }
    if (this.m_ShadowPaint.getAlpha() == 0)
    {
      this.m_Drawable.setBounds(this.m_ContentBounds);
      this.m_Drawable.draw(paramCanvas);
      return;
    }
    Bitmap localBitmap;
    Canvas localCanvas;
    if ((this.m_ContentBounds.width() <= 640) && (this.m_ContentBounds.height() <= 640)) {
      if (SHARED_DRAWABLE_BITMAP_BUFFER.get() != null)
      {
        ((Bitmap)SHARED_DRAWABLE_BITMAP_BUFFER.get()).eraseColor(0);
        localBitmap = (Bitmap)SHARED_DRAWABLE_BITMAP_BUFFER.get();
        localCanvas = (Canvas)SHARED_DRAWABLE_BITMAP_BUFFER_CANVAS.get();
        label172:
        this.m_Drawable.setBounds(0, 0, this.m_ContentBounds.width(), this.m_ContentBounds.height());
        this.m_Drawable.draw(localCanvas);
        this.m_ShadowBounds.set(this.m_ContentBounds);
        this.m_ShadowBounds.offset(this.m_ShadowDx, this.m_ShadowDy);
        this.m_NoRadiusShadowBounds.set(this.m_ShadowBounds);
        this.m_ShadowBounds.inset(-this.m_ShadowRadiusCeiling, -this.m_ShadowRadiusCeiling);
        this.m_UnionBounds.set(localRect);
        this.m_UnionBounds.union(this.m_ContentBounds);
        this.m_UnionBounds.union(this.m_ShadowBounds);
        if ((this.m_OutputBuffer != null) && (this.m_UnionBounds.width() <= this.m_OutputBuffer.getWidth())) {
          break label610;
        }
        label312:
        this.m_OutputBuffer = Bitmap.createBitmap(this.m_UnionBounds.width(), this.m_UnionBounds.height(), Bitmap.Config.ARGB_8888);
        this.m_OutputBufferCanvas = new Canvas(this.m_OutputBuffer);
      }
    }
    for (;;)
    {
      this.m_SrcRect.set(0, 0, this.m_ContentBounds.width(), this.m_ContentBounds.height());
      this.m_DestRect.set(this.m_NoRadiusShadowBounds);
      this.m_DestRect.offset(-this.m_UnionBounds.left, -this.m_UnionBounds.top);
      this.m_OutputBufferCanvas.drawBitmap(localBitmap.extractAlpha(), this.m_SrcRect, this.m_DestRect, this.m_ShadowPaint);
      this.m_DestRect.set(this.m_ContentBounds);
      this.m_DestRect.offset(-this.m_UnionBounds.left, -this.m_UnionBounds.top);
      this.m_OutputBufferCanvas.drawBitmap(localBitmap, this.m_SrcRect, this.m_DestRect, null);
      this.m_SrcRect.set(0, 0, this.m_UnionBounds.width(), this.m_UnionBounds.height());
      this.m_DestRect.set(this.m_UnionBounds);
      paramCanvas.drawBitmap(this.m_OutputBuffer, this.m_SrcRect, this.m_DestRect, null);
      return;
      SHARED_DRAWABLE_BITMAP_BUFFER.set(Bitmap.createBitmap(640, 640, Bitmap.Config.ARGB_8888));
      SHARED_DRAWABLE_BITMAP_BUFFER_CANVAS.set(new Canvas((Bitmap)SHARED_DRAWABLE_BITMAP_BUFFER.get()));
      break;
      localBitmap = Bitmap.createBitmap(this.m_ContentBounds.width(), this.m_ContentBounds.height(), Bitmap.Config.ARGB_8888);
      localCanvas = new Canvas(localBitmap);
      break label172;
      label610:
      if (this.m_UnionBounds.height() > this.m_OutputBuffer.getHeight()) {
        break label312;
      }
      this.m_OutputBuffer.eraseColor(0);
    }
  }
  
  public int getAlpha()
  {
    return this.m_Drawable.getAlpha();
  }
  
  public ColorFilter getColorFilter()
  {
    return this.m_Drawable.getColorFilter();
  }
  
  public int getIntrinsicHeight()
  {
    return this.m_Drawable.getIntrinsicHeight() + this.m_PaddingTop + this.m_PaddingBottom;
  }
  
  public int getIntrinsicWidth()
  {
    return this.m_Drawable.getIntrinsicWidth() + this.m_PaddingStart + this.m_PaddingEnd;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public int[] getState()
  {
    return this.m_Drawable.getState();
  }
  
  public boolean isStateful()
  {
    return this.m_Drawable.isStateful();
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    return this.m_Drawable.setLevel(paramInt);
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_Drawable.setAlpha(paramInt);
    this.m_ShadowPaint.setAlpha(Math.round(Color.alpha(this.m_ShadowColor) / 255.0F * paramInt));
  }
  
  public void setAutoMirrored(boolean paramBoolean)
  {
    this.m_Drawable.setAutoMirrored(paramBoolean);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.m_Drawable.setColorFilter(paramColorFilter);
  }
  
  public void setPaddings(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.m_PaddingStart = paramInt1;
    this.m_PaddingTop = paramInt2;
    this.m_PaddingEnd = paramInt3;
    this.m_PaddingBottom = paramInt4;
  }
  
  public boolean setState(int[] paramArrayOfInt)
  {
    return this.m_Drawable.setState(paramArrayOfInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/drawable/ShadowDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */