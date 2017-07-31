package com.oneplus.util;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

public class TransformedRect
{
  private final Matrix m_InverseMatrix = new Matrix();
  private boolean m_IsTramsformationReady;
  private final Matrix m_Matrix = new Matrix();
  private final RectF m_OriginalRect = new RectF();
  private final float[] m_OriginalVertices = new float[8];
  private final float[] m_TransformedVertices = new float[8];
  
  public TransformedRect() {}
  
  public TransformedRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.m_OriginalRect.set(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    Geometry.convertRectToPoints(this.m_OriginalRect, this.m_OriginalVertices, 0);
  }
  
  public TransformedRect(Rect paramRect)
  {
    this.m_OriginalRect.set(paramRect);
    Geometry.convertRectToPoints(this.m_OriginalRect, this.m_OriginalVertices, 0);
  }
  
  public TransformedRect(RectF paramRectF)
  {
    this.m_OriginalRect.set(paramRectF);
    Geometry.convertRectToPoints(this.m_OriginalRect, this.m_OriginalVertices, 0);
  }
  
  public TransformedRect(TransformedRect paramTransformedRect)
  {
    this.m_OriginalRect.set(paramTransformedRect.m_OriginalRect);
    System.arraycopy(paramTransformedRect.m_OriginalVertices, 0, this.m_OriginalVertices, 0, 8);
    this.m_Matrix.set(paramTransformedRect.m_Matrix);
  }
  
  private static void convertXYPairsToPoints(PointF[] paramArrayOfPointF, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3)
  {
    while (paramInt3 > 0)
    {
      paramArrayOfPointF[paramInt1] = new PointF(paramArrayOfFloat[paramInt2], paramArrayOfFloat[(paramInt2 + 1)]);
      paramInt3 -= 1;
      paramInt2 += 2;
      paramInt1 += 1;
    }
  }
  
  private static float getBottom(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 0) {
      return NaN.0F;
    }
    float f1 = paramArrayOfFloat[((paramInt1 << 1) + 1)];
    paramInt1 += 1;
    while (paramInt2 > 1)
    {
      float f3 = paramArrayOfFloat[((paramInt1 << 1) + 1)];
      float f2 = f1;
      if (f3 > f1) {
        f2 = f3;
      }
      paramInt2 -= 1;
      paramInt1 += 1;
      f1 = f2;
    }
    return f1;
  }
  
  private static float getHeight(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 0) {
      return 0.0F;
    }
    float f4 = paramArrayOfFloat[((paramInt1 << 1) + 1)];
    float f1 = f4;
    paramInt1 += 1;
    while (paramInt2 > 1)
    {
      float f2 = paramArrayOfFloat[((paramInt1 << 1) + 1)];
      float f3 = f4;
      if (f2 < f4) {
        f3 = f2;
      }
      f4 = f1;
      if (f2 > f1) {
        f4 = f2;
      }
      paramInt2 -= 1;
      paramInt1 += 1;
      f1 = f4;
      f4 = f3;
    }
    return Math.abs(f1 - f4);
  }
  
  private static float getLeft(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 0) {
      return NaN.0F;
    }
    float f1 = paramArrayOfFloat[(paramInt1 << 1)];
    paramInt1 += 1;
    while (paramInt2 > 1)
    {
      float f3 = paramArrayOfFloat[(paramInt1 << 1)];
      float f2 = f1;
      if (f3 < f1) {
        f2 = f3;
      }
      paramInt2 -= 1;
      paramInt1 += 1;
      f1 = f2;
    }
    return f1;
  }
  
  private static float getRight(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 0) {
      return NaN.0F;
    }
    float f1 = paramArrayOfFloat[(paramInt1 << 1)];
    paramInt1 += 1;
    while (paramInt2 > 1)
    {
      float f3 = paramArrayOfFloat[(paramInt1 << 1)];
      float f2 = f1;
      if (f3 > f1) {
        f2 = f3;
      }
      paramInt2 -= 1;
      paramInt1 += 1;
      f1 = f2;
    }
    return f1;
  }
  
  private static float getTop(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 0) {
      return NaN.0F;
    }
    float f1 = paramArrayOfFloat[((paramInt1 << 1) + 1)];
    paramInt1 += 1;
    while (paramInt2 > 1)
    {
      float f3 = paramArrayOfFloat[((paramInt1 << 1) + 1)];
      float f2 = f1;
      if (f3 < f1) {
        f2 = f3;
      }
      paramInt2 -= 1;
      paramInt1 += 1;
      f1 = f2;
    }
    return f1;
  }
  
  private static float getWidth(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 0) {
      return 0.0F;
    }
    float f4 = paramArrayOfFloat[(paramInt1 << 1)];
    float f1 = f4;
    paramInt1 += 1;
    while (paramInt2 > 1)
    {
      float f2 = paramArrayOfFloat[(paramInt1 << 1)];
      float f3 = f4;
      if (f2 < f4) {
        f3 = f2;
      }
      float f5 = f1;
      if (f2 > f1) {
        f5 = f2;
      }
      paramInt2 -= 1;
      paramInt1 += 1;
      f4 = f3;
      f1 = f5;
    }
    return Math.abs(f1 - f4);
  }
  
  private void invalidate()
  {
    this.m_IsTramsformationReady = false;
  }
  
  private void transform()
  {
    if (this.m_IsTramsformationReady) {
      return;
    }
    this.m_Matrix.mapPoints(this.m_TransformedVertices, this.m_OriginalVertices);
    if (!this.m_Matrix.invert(this.m_InverseMatrix)) {
      this.m_InverseMatrix.reset();
    }
    this.m_IsTramsformationReady = true;
  }
  
  public boolean contains(float paramFloat1, float paramFloat2)
  {
    transform();
    float[] arrayOfFloat = new float[2];
    arrayOfFloat[0] = paramFloat1;
    arrayOfFloat[1] = paramFloat2;
    this.m_InverseMatrix.mapPoints(arrayOfFloat);
    paramFloat2 = arrayOfFloat[0];
    float f = arrayOfFloat[1];
    if (Geometry.areSimilarCoordinates(paramFloat2, this.m_OriginalRect.left))
    {
      paramFloat1 = this.m_OriginalRect.left;
      if (!Geometry.areSimilarCoordinates(f, this.m_OriginalRect.top)) {
        break label159;
      }
      paramFloat2 = this.m_OriginalRect.top;
    }
    for (;;)
    {
      if ((paramFloat1 < this.m_OriginalRect.left) || (paramFloat1 > this.m_OriginalRect.right) || (paramFloat2 < this.m_OriginalRect.top) || (paramFloat2 > this.m_OriginalRect.bottom)) {
        break label186;
      }
      return true;
      paramFloat1 = paramFloat2;
      if (!Geometry.areSimilarCoordinates(paramFloat2, this.m_OriginalRect.right)) {
        break;
      }
      paramFloat1 = this.m_OriginalRect.right;
      break;
      label159:
      paramFloat2 = f;
      if (Geometry.areSimilarCoordinates(f, this.m_OriginalRect.bottom)) {
        paramFloat2 = this.m_OriginalRect.bottom;
      }
    }
    label186:
    return false;
  }
  
  public boolean contains(PointF paramPointF)
  {
    return contains(paramPointF.x, paramPointF.y);
  }
  
  public boolean contains(RectF paramRectF)
  {
    float[] arrayOfFloat = new float[8];
    Geometry.convertRectToPoints(paramRectF, arrayOfFloat, 0);
    return containsAll(arrayOfFloat, 0, 4);
  }
  
  public boolean contains(TransformedRect paramTransformedRect)
  {
    if (paramTransformedRect == this) {
      return true;
    }
    paramTransformedRect.transform();
    return containsAll(paramTransformedRect.m_TransformedVertices, 0, 4);
  }
  
  public boolean containsAll(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    transform();
    float[] arrayOfFloat = new float[2];
    while (paramInt2 > 0)
    {
      arrayOfFloat[0] = paramArrayOfFloat[paramInt1];
      arrayOfFloat[1] = paramArrayOfFloat[(paramInt1 + 1)];
      this.m_InverseMatrix.mapPoints(arrayOfFloat);
      float f2 = arrayOfFloat[0];
      float f3 = arrayOfFloat[1];
      float f1;
      if (Geometry.areSimilarCoordinates(f2, this.m_OriginalRect.left))
      {
        f1 = this.m_OriginalRect.left;
        if (!Geometry.areSimilarCoordinates(f3, this.m_OriginalRect.top)) {
          break label157;
        }
        f2 = this.m_OriginalRect.top;
        label98:
        if ((f1 >= this.m_OriginalRect.left) && (f1 <= this.m_OriginalRect.right)) {
          break label188;
        }
      }
      label157:
      label188:
      while ((f2 < this.m_OriginalRect.top) || (f2 > this.m_OriginalRect.bottom))
      {
        return false;
        f1 = f2;
        if (!Geometry.areSimilarCoordinates(f2, this.m_OriginalRect.right)) {
          break;
        }
        f1 = this.m_OriginalRect.right;
        break;
        f2 = f3;
        if (!Geometry.areSimilarCoordinates(f3, this.m_OriginalRect.bottom)) {
          break label98;
        }
        f2 = this.m_OriginalRect.bottom;
        break label98;
      }
      paramInt2 -= 1;
      paramInt1 += 2;
    }
    return true;
  }
  
  public boolean containsAll(PointF[] paramArrayOfPointF, int paramInt1, int paramInt2)
  {
    transform();
    float[] arrayOfFloat = new float[2];
    while (paramInt2 > 0)
    {
      arrayOfFloat[0] = paramArrayOfPointF[paramInt1].x;
      arrayOfFloat[1] = paramArrayOfPointF[paramInt1].y;
      this.m_InverseMatrix.mapPoints(arrayOfFloat);
      float f2 = arrayOfFloat[0];
      float f3 = arrayOfFloat[1];
      float f1;
      if (Geometry.areSimilarCoordinates(f2, this.m_OriginalRect.left))
      {
        f1 = this.m_OriginalRect.left;
        if (!Geometry.areSimilarCoordinates(f3, this.m_OriginalRect.top)) {
          break label161;
        }
        f2 = this.m_OriginalRect.top;
        label102:
        if ((f1 >= this.m_OriginalRect.left) && (f1 <= this.m_OriginalRect.right)) {
          break label192;
        }
      }
      label161:
      label192:
      while ((f2 < this.m_OriginalRect.top) || (f2 > this.m_OriginalRect.bottom))
      {
        return false;
        f1 = f2;
        if (!Geometry.areSimilarCoordinates(f2, this.m_OriginalRect.right)) {
          break;
        }
        f1 = this.m_OriginalRect.right;
        break;
        f2 = f3;
        if (!Geometry.areSimilarCoordinates(f3, this.m_OriginalRect.bottom)) {
          break label102;
        }
        f2 = this.m_OriginalRect.bottom;
        break label102;
      }
      paramInt2 -= 1;
      paramInt1 += 1;
    }
    return true;
  }
  
  public float getBottom()
  {
    transform();
    return getBottom(this.m_TransformedVertices, 0, 4);
  }
  
  public RectF getBoundingBox()
  {
    RectF localRectF = new RectF();
    getBoundingBox(localRectF);
    return localRectF;
  }
  
  public void getBoundingBox(RectF paramRectF)
  {
    transform();
    Geometry.getBoundingBox(paramRectF, this.m_TransformedVertices, 0, 4);
  }
  
  public float getHeight()
  {
    transform();
    return getHeight(this.m_TransformedVertices, 0, 4);
  }
  
  public float getLeft()
  {
    transform();
    return getLeft(this.m_TransformedVertices, 0, 4);
  }
  
  public float getOriginalBottom()
  {
    return getBottom(this.m_OriginalVertices, 0, 4);
  }
  
  public float getOriginalHeight()
  {
    return getHeight(this.m_OriginalVertices, 0, 4);
  }
  
  public float getOriginalLeft()
  {
    return getLeft(this.m_OriginalVertices, 0, 4);
  }
  
  public RectF getOriginalRect()
  {
    return new RectF(this.m_OriginalRect);
  }
  
  public void getOriginalRect(RectF paramRectF)
  {
    paramRectF.set(this.m_OriginalRect);
  }
  
  public float getOriginalRight()
  {
    return getRight(this.m_OriginalVertices, 0, 4);
  }
  
  public float getOriginalTop()
  {
    return getTop(this.m_OriginalVertices, 0, 4);
  }
  
  public void getOriginalVertices(float[] paramArrayOfFloat, int paramInt)
  {
    System.arraycopy(this.m_OriginalVertices, 0, paramArrayOfFloat, paramInt, 8);
  }
  
  public void getOriginalVertices(PointF[] paramArrayOfPointF, int paramInt)
  {
    convertXYPairsToPoints(paramArrayOfPointF, paramInt, this.m_OriginalVertices, 0, 4);
  }
  
  public float getOriginalWidth()
  {
    return getWidth(this.m_OriginalVertices, 0, 4);
  }
  
  public float getRight()
  {
    transform();
    return getRight(this.m_TransformedVertices, 0, 4);
  }
  
  public float getTop()
  {
    transform();
    return getTop(this.m_TransformedVertices, 0, 4);
  }
  
  public Matrix getTransformation()
  {
    return new Matrix(this.m_Matrix);
  }
  
  public void getTransformation(Matrix paramMatrix)
  {
    paramMatrix.set(this.m_Matrix);
  }
  
  public void getVertices(float[] paramArrayOfFloat, int paramInt)
  {
    transform();
    System.arraycopy(this.m_TransformedVertices, 0, paramArrayOfFloat, paramInt, 8);
  }
  
  public void getVertices(PointF[] paramArrayOfPointF, int paramInt)
  {
    transform();
    convertXYPairsToPoints(paramArrayOfPointF, paramInt, this.m_TransformedVertices, 0, 4);
  }
  
  public float getWidth()
  {
    transform();
    return getWidth(this.m_TransformedVertices, 0, 4);
  }
  
  public boolean isTransformed()
  {
    return !this.m_Matrix.isIdentity();
  }
  
  public void mapFromOriginalRectangle(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, int paramInt3)
  {
    this.m_Matrix.mapPoints(paramArrayOfFloat1, paramInt1, paramArrayOfFloat2, paramInt2, paramInt3);
  }
  
  public void mapFromOriginalRectangle(PointF[] paramArrayOfPointF1, int paramInt1, PointF[] paramArrayOfPointF2, int paramInt2, int paramInt3)
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
    mapFromOriginalRectangle(arrayOfFloat, 0, arrayOfFloat, 0, paramInt3);
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
  
  public void mapToOriginalRectangle(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, int paramInt3)
  {
    transform();
    this.m_InverseMatrix.mapPoints(paramArrayOfFloat1, paramInt1, paramArrayOfFloat2, paramInt2, paramInt3);
  }
  
  public void mapToOriginalRectangle(PointF[] paramArrayOfPointF1, int paramInt1, PointF[] paramArrayOfPointF2, int paramInt2, int paramInt3)
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
    mapToOriginalRectangle(arrayOfFloat, 0, arrayOfFloat, 0, paramInt3);
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
  
  public void offsetOriginalRect(float paramFloat1, float paramFloat2)
  {
    this.m_OriginalRect.offset(paramFloat1, paramFloat2);
    Geometry.convertRectToPoints(this.m_OriginalRect, this.m_OriginalVertices, 0);
    invalidate();
  }
  
  public void offsetOriginalRectTo(float paramFloat1, float paramFloat2)
  {
    this.m_OriginalRect.offsetTo(paramFloat1, paramFloat2);
    Geometry.convertRectToPoints(this.m_OriginalRect, this.m_OriginalVertices, 0);
    invalidate();
  }
  
  public boolean postRotate(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (this.m_Matrix.postRotate(paramFloat1, paramFloat2, paramFloat3))
    {
      invalidate();
      return true;
    }
    return false;
  }
  
  public boolean postScale(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (this.m_Matrix.postScale(paramFloat1, paramFloat2, paramFloat3, paramFloat4))
    {
      invalidate();
      return true;
    }
    return false;
  }
  
  public boolean postTransform(Matrix paramMatrix)
  {
    if (this.m_Matrix.postConcat(paramMatrix))
    {
      invalidate();
      return true;
    }
    return false;
  }
  
  public boolean postTranslate(float paramFloat1, float paramFloat2)
  {
    if (this.m_Matrix.postTranslate(paramFloat1, paramFloat2))
    {
      invalidate();
      return true;
    }
    return false;
  }
  
  public boolean preRotate(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (this.m_Matrix.preRotate(paramFloat1, paramFloat2, paramFloat3))
    {
      invalidate();
      return true;
    }
    return false;
  }
  
  public boolean preScale(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (this.m_Matrix.preScale(paramFloat1, paramFloat2, paramFloat3, paramFloat4))
    {
      invalidate();
      return true;
    }
    return false;
  }
  
  public boolean preTransform(Matrix paramMatrix)
  {
    if (this.m_Matrix.preConcat(paramMatrix))
    {
      invalidate();
      return true;
    }
    return false;
  }
  
  public boolean preTranslate(float paramFloat1, float paramFloat2)
  {
    if (this.m_Matrix.preTranslate(paramFloat1, paramFloat2))
    {
      invalidate();
      return true;
    }
    return false;
  }
  
  public void resetTransformation()
  {
    this.m_Matrix.reset();
    invalidate();
  }
  
  public void setOriginalRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.m_OriginalRect.set(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    Geometry.convertRectToPoints(this.m_OriginalRect, this.m_OriginalVertices, 0);
    invalidate();
  }
  
  public void setOriginalRect(Rect paramRect)
  {
    this.m_OriginalRect.set(paramRect);
    Geometry.convertRectToPoints(this.m_OriginalRect, this.m_OriginalVertices, 0);
    invalidate();
  }
  
  public void setOriginalRect(RectF paramRectF)
  {
    this.m_OriginalRect.set(paramRectF);
    Geometry.convertRectToPoints(this.m_OriginalRect, this.m_OriginalVertices, 0);
    invalidate();
  }
  
  public void setTransformation(Matrix paramMatrix)
  {
    if (paramMatrix == null) {
      this.m_Matrix.reset();
    }
    for (;;)
    {
      invalidate();
      return;
      this.m_Matrix.set(paramMatrix);
    }
  }
  
  public void setTransformation(TransformedRect paramTransformedRect)
  {
    if (paramTransformedRect == this) {
      return;
    }
    if (paramTransformedRect == null) {
      this.m_Matrix.reset();
    }
    for (;;)
    {
      invalidate();
      return;
      this.m_Matrix.set(paramTransformedRect.m_Matrix);
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    RectF localRectF = getBoundingBox();
    localStringBuilder.append("{");
    localStringBuilder.append(this.m_OriginalRect);
    localStringBuilder.append(" -> ");
    localStringBuilder.append(localRectF);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/util/TransformedRect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */