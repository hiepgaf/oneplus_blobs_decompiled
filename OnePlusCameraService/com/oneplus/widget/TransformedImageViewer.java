package com.oneplus.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.util.AttributeSet;
import com.oneplus.util.TransformedRect;

public class TransformedImageViewer
  extends ImageViewer
{
  private final Matrix m_InverseTransformMatrix = new Matrix();
  private Drawable m_OriginalImageDrawable;
  private final Drawable.Callback m_OriginalImageDrawableCallback = new Drawable.Callback()
  {
    public void invalidateDrawable(Drawable paramAnonymousDrawable)
    {
      TransformedImageViewer.this.onOriginalImageDrawableInvalidated(paramAnonymousDrawable);
    }
    
    public void scheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable, long paramAnonymousLong)
    {
      TransformedImageViewer.this.scheduleDrawable(paramAnonymousDrawable, paramAnonymousRunnable, paramAnonymousLong);
    }
    
    public void unscheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable)
    {
      TransformedImageViewer.this.unscheduleDrawable(paramAnonymousDrawable, paramAnonymousRunnable);
    }
  };
  private int m_OriginalImageHeight;
  private int m_OriginalImageWidth;
  private Drawable m_OriginalOverlayDrawable;
  private final RectF m_TempBoundingBox = new RectF();
  private final Rect m_TempImageBounds = new Rect();
  private final Matrix m_TransformMatrix = new Matrix();
  private TransformedDrawable m_TransformedDrawable;
  private final TransformedRect m_TransformedRect = new TransformedRect();
  
  public TransformedImageViewer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public Drawable getImageDrawable()
  {
    return this.m_OriginalImageDrawable;
  }
  
  public Matrix getImageTransformation()
  {
    return new Matrix(this.m_TransformMatrix);
  }
  
  public void getImageTransformation(Matrix paramMatrix)
  {
    paramMatrix.set(this.m_TransformMatrix);
  }
  
  public void getImageTransformation(TransformedRect paramTransformedRect)
  {
    paramTransformedRect.setTransformation(this.m_TransformMatrix);
  }
  
  public boolean getLocationOnOriginalImage(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2)
  {
    return getLocationOnOriginalImage(paramArrayOfFloat, 0, paramFloat1, paramFloat2);
  }
  
  public boolean getLocationOnOriginalImage(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2)
  {
    getImageBounds(this.m_TempImageBounds);
    getLocationOnImage(paramArrayOfFloat, paramInt, paramFloat1, paramFloat2);
    paramFloat1 = paramArrayOfFloat[paramInt] / this.m_TempImageBounds.width();
    paramFloat2 = paramArrayOfFloat[(paramInt + 1)] / this.m_TempImageBounds.height();
    this.m_TransformedRect.getBoundingBox(this.m_TempBoundingBox);
    paramArrayOfFloat[paramInt] = (this.m_TempBoundingBox.left + this.m_TempBoundingBox.width() * paramFloat1);
    paramArrayOfFloat[(paramInt + 1)] = (this.m_TempBoundingBox.top + this.m_TempBoundingBox.height() * paramFloat2);
    this.m_TransformedRect.mapToOriginalRectangle(paramArrayOfFloat, paramInt, paramArrayOfFloat, paramInt, 1);
    this.m_TransformedRect.getOriginalRect(this.m_TempBoundingBox);
    return this.m_TempBoundingBox.contains(paramArrayOfFloat[paramInt], paramArrayOfFloat[(paramInt + 1)]);
  }
  
  public int getOriginalIntrinsicImageHeight()
  {
    return this.m_OriginalImageHeight;
  }
  
  public int getOriginalIntrinsicImageWidth()
  {
    return this.m_OriginalImageWidth;
  }
  
  public Drawable getOverlayDrawable()
  {
    return this.m_OriginalOverlayDrawable;
  }
  
  public TransformedRect getTransformedRectWithOriginalImageSize()
  {
    TransformedRect localTransformedRect = new TransformedRect();
    getTransformedRectWithOriginalImageSize(localTransformedRect);
    return localTransformedRect;
  }
  
  public void getTransformedRectWithOriginalImageSize(TransformedRect paramTransformedRect)
  {
    paramTransformedRect.setOriginalRect(0.0F, 0.0F, this.m_OriginalImageWidth, this.m_OriginalImageHeight);
    paramTransformedRect.setTransformation(this.m_TransformedRect);
  }
  
  public void mapPointsFromOriginalImage(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, int paramInt3)
  {
    this.m_TransformMatrix.mapPoints(paramArrayOfFloat1, paramInt1, paramArrayOfFloat2, paramInt2, paramInt3);
  }
  
  public void mapPointsFromOriginalImage(PointF[] paramArrayOfPointF1, int paramInt1, PointF[] paramArrayOfPointF2, int paramInt2, int paramInt3)
  {
    float[] arrayOfFloat = new float[paramInt3 << 1];
    int i = 0;
    int j = 0;
    while (i < paramInt3)
    {
      int k = j + 1;
      arrayOfFloat[j] = paramArrayOfPointF2[(paramInt2 + i)].x;
      j = k + 1;
      arrayOfFloat[k] = paramArrayOfPointF2[(paramInt2 + i)].y;
      i += 1;
    }
    mapPointsFromOriginalImage(arrayOfFloat, 0, arrayOfFloat, 0, paramInt3);
    paramInt2 = 0;
    i = 0;
    while (paramInt2 < paramInt3)
    {
      j = i + 1;
      float f = arrayOfFloat[i];
      i = j + 1;
      paramArrayOfPointF1[(paramInt1 + paramInt2)] = new PointF(f, arrayOfFloat[j]);
      paramInt2 += 1;
    }
  }
  
  public void mapPointsToOriginalImage(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, int paramInt3)
  {
    this.m_InverseTransformMatrix.mapPoints(paramArrayOfFloat1, paramInt1, paramArrayOfFloat2, paramInt2, paramInt3);
  }
  
  public void mapPointsToOriginalImage(PointF[] paramArrayOfPointF1, int paramInt1, PointF[] paramArrayOfPointF2, int paramInt2, int paramInt3)
  {
    float[] arrayOfFloat = new float[paramInt3 << 1];
    int i = 0;
    int j = 0;
    while (i < paramInt3)
    {
      int k = j + 1;
      arrayOfFloat[j] = paramArrayOfPointF2[(paramInt2 + i)].x;
      j = k + 1;
      arrayOfFloat[k] = paramArrayOfPointF2[(paramInt2 + i)].y;
      i += 1;
    }
    mapPointsToOriginalImage(arrayOfFloat, 0, arrayOfFloat, 0, paramInt3);
    paramInt2 = 0;
    i = 0;
    while (paramInt2 < paramInt3)
    {
      j = i + 1;
      float f = arrayOfFloat[i];
      i = j + 1;
      paramArrayOfPointF1[(paramInt1 + paramInt2)] = new PointF(f, arrayOfFloat[j]);
      paramInt2 += 1;
    }
  }
  
  protected void onImageTransformationChanged(Matrix paramMatrix, boolean paramBoolean)
  {
    refreshImageBounds(paramBoolean);
  }
  
  protected void onOriginalImageDrawableInvalidated(Drawable paramDrawable)
  {
    int i = paramDrawable.getIntrinsicWidth();
    int j = paramDrawable.getIntrinsicHeight();
    if ((this.m_OriginalImageWidth != i) || (this.m_OriginalImageHeight != j))
    {
      this.m_OriginalImageWidth = i;
      this.m_OriginalImageHeight = j;
      this.m_TransformedRect.setOriginalRect(0.0F, 0.0F, i, j);
      onOriginalIntrinsicImageSizeChanged(i, j);
    }
    invalidate();
  }
  
  protected void onOriginalIntrinsicImageSizeChanged(int paramInt1, int paramInt2) {}
  
  public void setImageDrawable(Drawable paramDrawable, boolean paramBoolean)
  {
    if (this.m_OriginalImageDrawable == paramDrawable) {
      return;
    }
    if (this.m_OriginalImageDrawable != null) {
      this.m_OriginalImageDrawable.setCallback(null);
    }
    this.m_OriginalImageDrawable = paramDrawable;
    if (paramDrawable != null)
    {
      this.m_TransformedDrawable = new TransformedDrawable();
      onOriginalImageDrawableInvalidated(paramDrawable);
      paramDrawable.setCallback(this.m_OriginalImageDrawableCallback);
    }
    for (;;)
    {
      super.setImageDrawable(this.m_TransformedDrawable, paramBoolean);
      return;
      this.m_TransformedDrawable = null;
      this.m_OriginalImageWidth = 0;
      this.m_OriginalImageHeight = 0;
      onOriginalIntrinsicImageSizeChanged(0, 0);
    }
  }
  
  public void setImageTransformation(Matrix paramMatrix)
  {
    setImageTransformation(paramMatrix, false);
  }
  
  public void setImageTransformation(Matrix paramMatrix, boolean paramBoolean)
  {
    if ((paramMatrix == null) || (paramMatrix.isIdentity()))
    {
      if (this.m_TransformMatrix.isIdentity()) {
        return;
      }
      this.m_TransformMatrix.reset();
    }
    for (;;)
    {
      this.m_TransformMatrix.invert(this.m_InverseTransformMatrix);
      this.m_TransformedRect.setTransformation(this.m_TransformMatrix);
      onImageTransformationChanged(paramMatrix, paramBoolean);
      return;
      this.m_TransformMatrix.set(paramMatrix);
    }
  }
  
  public void setImageTransformation(TransformedRect paramTransformedRect)
  {
    setImageTransformation(paramTransformedRect, false);
  }
  
  public void setImageTransformation(TransformedRect paramTransformedRect, boolean paramBoolean)
  {
    if ((paramTransformedRect != null) && (paramTransformedRect.isTransformed())) {
      paramTransformedRect.getTransformation(this.m_TransformMatrix);
    }
    for (;;)
    {
      this.m_TransformMatrix.invert(this.m_InverseTransformMatrix);
      this.m_TransformedRect.setTransformation(this.m_TransformMatrix);
      onImageTransformationChanged(this.m_TransformMatrix, paramBoolean);
      return;
      if (this.m_TransformMatrix.isIdentity()) {
        return;
      }
      this.m_TransformMatrix.reset();
    }
  }
  
  public void setOverlayDrawable(Drawable paramDrawable)
  {
    if (this.m_OriginalOverlayDrawable == paramDrawable) {
      return;
    }
    this.m_OriginalOverlayDrawable = paramDrawable;
    invalidate();
  }
  
  private final class TransformedDrawable
    extends Drawable
  {
    private final Matrix m_Matrix = new Matrix();
    
    public TransformedDrawable() {}
    
    public void draw(Canvas paramCanvas)
    {
      if (TransformedImageViewer.-get0(TransformedImageViewer.this) == null) {
        return;
      }
      Rect localRect = getBounds();
      int i = paramCanvas.save();
      TransformedImageViewer.-get5(TransformedImageViewer.this).getBoundingBox(TransformedImageViewer.-get4(TransformedImageViewer.this));
      TransformedImageViewer.-get5(TransformedImageViewer.this).getTransformation(this.m_Matrix);
      this.m_Matrix.postTranslate(localRect.left - TransformedImageViewer.-get4(TransformedImageViewer.this).left, localRect.top - TransformedImageViewer.-get4(TransformedImageViewer.this).top);
      this.m_Matrix.postScale(localRect.width() / TransformedImageViewer.-get4(TransformedImageViewer.this).width(), localRect.height() / TransformedImageViewer.-get4(TransformedImageViewer.this).height(), localRect.left, localRect.top);
      paramCanvas.concat(this.m_Matrix);
      TransformedImageViewer.-get0(TransformedImageViewer.this).setBounds(0, 0, TransformedImageViewer.-get2(TransformedImageViewer.this), TransformedImageViewer.-get1(TransformedImageViewer.this));
      TransformedImageViewer.-get0(TransformedImageViewer.this).draw(paramCanvas);
      if (TransformedImageViewer.-get3(TransformedImageViewer.this) != null)
      {
        TransformedImageViewer.-get3(TransformedImageViewer.this).setBounds(0, 0, TransformedImageViewer.-get2(TransformedImageViewer.this), TransformedImageViewer.-get1(TransformedImageViewer.this));
        TransformedImageViewer.-get3(TransformedImageViewer.this).draw(paramCanvas);
      }
      paramCanvas.restoreToCount(i);
    }
    
    public int getIntrinsicHeight()
    {
      return Math.round(TransformedImageViewer.-get5(TransformedImageViewer.this).getHeight());
    }
    
    public int getIntrinsicWidth()
    {
      return Math.round(TransformedImageViewer.-get5(TransformedImageViewer.this).getWidth());
    }
    
    public int getOpacity()
    {
      if (TransformedImageViewer.-get0(TransformedImageViewer.this) != null) {
        return TransformedImageViewer.-get0(TransformedImageViewer.this).getOpacity();
      }
      return -3;
    }
    
    public void setAlpha(int paramInt)
    {
      if (TransformedImageViewer.-get0(TransformedImageViewer.this) != null) {
        TransformedImageViewer.-get0(TransformedImageViewer.this).setAlpha(paramInt);
      }
    }
    
    public void setColorFilter(ColorFilter paramColorFilter)
    {
      if (TransformedImageViewer.-get0(TransformedImageViewer.this) != null) {
        TransformedImageViewer.-get0(TransformedImageViewer.this).setColorFilter(paramColorFilter);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/widget/TransformedImageViewer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */