package android.support.v4.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public abstract class RoundedBitmapDrawable
  extends Drawable
{
  private static final int DEFAULT_PAINT_FLAGS = 6;
  private boolean mApplyGravity = true;
  Bitmap mBitmap;
  private int mBitmapHeight;
  private BitmapShader mBitmapShader;
  private int mBitmapWidth;
  private float mCornerRadius;
  final Rect mDstRect = new Rect();
  final RectF mDstRectF = new RectF();
  private int mGravity = 119;
  private Paint mPaint = new Paint(6);
  private int mTargetDensity = 160;
  
  RoundedBitmapDrawable(Resources paramResources, Bitmap paramBitmap)
  {
    if (paramResources == null) {}
    for (;;)
    {
      this.mBitmap = paramBitmap;
      if (this.mBitmap != null) {
        break;
      }
      this.mBitmapHeight = -1;
      this.mBitmapWidth = -1;
      return;
      this.mTargetDensity = paramResources.getDisplayMetrics().densityDpi;
    }
    computeBitmapSize();
    this.mBitmapShader = new BitmapShader(this.mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
  }
  
  private void computeBitmapSize()
  {
    this.mBitmapWidth = this.mBitmap.getScaledWidth(this.mTargetDensity);
    this.mBitmapHeight = this.mBitmap.getScaledHeight(this.mTargetDensity);
  }
  
  private static boolean isGreaterThanZero(float paramFloat)
  {
    return Float.compare(paramFloat, 0.0F) > 0;
  }
  
  public void draw(Canvas paramCanvas)
  {
    Bitmap localBitmap = this.mBitmap;
    Paint localPaint;
    if (localBitmap != null)
    {
      updateDstRect();
      localPaint = this.mPaint;
      if (localPaint.getShader() != null) {
        paramCanvas.drawRoundRect(this.mDstRectF, this.mCornerRadius, this.mCornerRadius, localPaint);
      }
    }
    else
    {
      return;
    }
    paramCanvas.drawBitmap(localBitmap, null, this.mDstRect, localPaint);
  }
  
  public int getAlpha()
  {
    return this.mPaint.getAlpha();
  }
  
  public final Bitmap getBitmap()
  {
    return this.mBitmap;
  }
  
  public ColorFilter getColorFilter()
  {
    return this.mPaint.getColorFilter();
  }
  
  public float getCornerRadius()
  {
    return this.mCornerRadius;
  }
  
  public int getGravity()
  {
    return this.mGravity;
  }
  
  public int getIntrinsicHeight()
  {
    return this.mBitmapHeight;
  }
  
  public int getIntrinsicWidth()
  {
    return this.mBitmapWidth;
  }
  
  public int getOpacity()
  {
    Bitmap localBitmap;
    if (this.mGravity == 119)
    {
      localBitmap = this.mBitmap;
      if (localBitmap != null) {
        break label24;
      }
    }
    label24:
    while ((localBitmap.hasAlpha()) || (this.mPaint.getAlpha() < 255) || (isGreaterThanZero(this.mCornerRadius)))
    {
      return -3;
      return -3;
    }
    return -1;
  }
  
  public final Paint getPaint()
  {
    return this.mPaint;
  }
  
  void gravityCompatApply(int paramInt1, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean hasAntiAlias()
  {
    return this.mPaint.isAntiAlias();
  }
  
  public boolean hasMipMap()
  {
    throw new UnsupportedOperationException();
  }
  
  public void setAlpha(int paramInt)
  {
    if (paramInt == this.mPaint.getAlpha()) {
      return;
    }
    this.mPaint.setAlpha(paramInt);
    invalidateSelf();
  }
  
  public void setAntiAlias(boolean paramBoolean)
  {
    this.mPaint.setAntiAlias(paramBoolean);
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mPaint.setColorFilter(paramColorFilter);
    invalidateSelf();
  }
  
  public void setCornerRadius(float paramFloat)
  {
    if (!isGreaterThanZero(paramFloat)) {
      this.mPaint.setShader(null);
    }
    for (;;)
    {
      this.mCornerRadius = paramFloat;
      return;
      this.mPaint.setShader(this.mBitmapShader);
    }
  }
  
  public void setDither(boolean paramBoolean)
  {
    this.mPaint.setDither(paramBoolean);
    invalidateSelf();
  }
  
  public void setFilterBitmap(boolean paramBoolean)
  {
    this.mPaint.setFilterBitmap(paramBoolean);
    invalidateSelf();
  }
  
  public void setGravity(int paramInt)
  {
    if (this.mGravity == paramInt) {
      return;
    }
    this.mGravity = paramInt;
    this.mApplyGravity = true;
    invalidateSelf();
  }
  
  public void setMipMap(boolean paramBoolean)
  {
    throw new UnsupportedOperationException();
  }
  
  public void setTargetDensity(int paramInt)
  {
    if (this.mTargetDensity == paramInt) {
      return;
    }
    if (paramInt != 0)
    {
      this.mTargetDensity = paramInt;
      if (this.mBitmap != null) {
        break label37;
      }
    }
    for (;;)
    {
      invalidateSelf();
      return;
      paramInt = 160;
      break;
      label37:
      computeBitmapSize();
    }
  }
  
  public void setTargetDensity(Canvas paramCanvas)
  {
    setTargetDensity(paramCanvas.getDensity());
  }
  
  public void setTargetDensity(DisplayMetrics paramDisplayMetrics)
  {
    setTargetDensity(paramDisplayMetrics.densityDpi);
  }
  
  void updateDstRect()
  {
    if (!this.mApplyGravity) {
      return;
    }
    gravityCompatApply(this.mGravity, this.mBitmapWidth, this.mBitmapHeight, getBounds(), this.mDstRect);
    this.mDstRectF.set(this.mDstRect);
    this.mApplyGravity = false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/graphics/drawable/RoundedBitmapDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */