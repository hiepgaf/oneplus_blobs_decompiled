package com.oneplus.util;

import android.graphics.RectF;
import android.util.Size;
import android.util.SizeF;

public final class SizeUtils
{
  public static RectF getInnerRect(RectF paramRectF1, float paramFloat, RectF paramRectF2)
  {
    if ((paramRectF1 == null) || (paramRectF2 == null)) {}
    while (paramRectF2.isEmpty()) {
      return new RectF();
    }
    RectF localRectF = getMinOuterRect(paramRectF2, -paramFloat);
    paramFloat = Math.max(0.0F, Math.max(paramRectF1.left - localRectF.left, localRectF.right - paramRectF1.right));
    float f1 = Math.max(0.0F, Math.max(paramRectF1.top - localRectF.top, localRectF.bottom - paramRectF1.bottom));
    if ((paramFloat == 0.0F) && (f1 == 0.0F)) {
      return new RectF(paramRectF2);
    }
    f1 = Math.min((localRectF.width() - 2.0F * paramFloat) / localRectF.width(), (localRectF.height() - 2.0F * f1) / localRectF.height());
    paramFloat = paramRectF2.width() * f1;
    f1 = paramRectF2.height() * f1;
    float f2 = paramRectF2.centerX() - paramFloat / 2.0F;
    float f3 = paramRectF2.centerY() - f1 / 2.0F;
    return new RectF(f2, f3, f2 + paramFloat, f3 + f1);
  }
  
  public static RectF getMinOuterRect(RectF paramRectF, float paramFloat)
  {
    if ((paramRectF == null) || (paramRectF.isEmpty())) {
      return new RectF();
    }
    double d = paramFloat / 180.0F * 3.141592653589793D;
    SizeF localSizeF = new SizeF((float)(paramRectF.width() * Math.abs(Math.cos(d)) + paramRectF.height() * Math.abs(Math.sin(d))), (float)(paramRectF.height() * Math.abs(Math.cos(d)) + paramRectF.width() * Math.abs(Math.sin(d))));
    paramFloat = paramRectF.centerX() - localSizeF.getWidth() / 2.0F;
    float f = paramRectF.centerY() - localSizeF.getHeight() / 2.0F;
    return new RectF(paramFloat, f, paramFloat + localSizeF.getWidth(), f + localSizeF.getHeight());
  }
  
  public static Size getRatioCenterCroppedSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    SizeF localSizeF = getRatioCenterCroppedSize(paramInt1, paramInt2, paramInt3, paramInt4, true);
    return new Size(Math.round(localSizeF.getWidth()), Math.round(localSizeF.getHeight()));
  }
  
  public static SizeF getRatioCenterCroppedSize(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, boolean paramBoolean)
  {
    if ((paramFloat3 <= 0.0F) || (paramFloat4 <= 0.0F)) {}
    while ((paramFloat1 <= 0.0F) || (paramFloat2 <= 0.0F)) {
      return new SizeF(0.0F, 0.0F);
    }
    paramFloat3 = Math.max(paramFloat3 / paramFloat1, paramFloat4 / paramFloat2);
    if ((paramBoolean) && (paramFloat3 > 1.0F)) {
      return new SizeF(paramFloat1, paramFloat2);
    }
    return new SizeF(paramFloat1 * paramFloat3, paramFloat2 * paramFloat3);
  }
  
  public static Size getRatioStretchedSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    SizeF localSizeF = getRatioStretchedSize(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
    return new Size(Math.round(localSizeF.getWidth()), Math.round(localSizeF.getHeight()));
  }
  
  public static SizeF getRatioStretchedSize(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, boolean paramBoolean)
  {
    if ((paramFloat3 <= 0.0F) || (paramFloat4 <= 0.0F)) {}
    while ((paramFloat1 <= 0.0F) || (paramFloat2 <= 0.0F)) {
      return new SizeF(0.0F, 0.0F);
    }
    paramFloat3 = Math.min(paramFloat3 / paramFloat1, paramFloat4 / paramFloat2);
    if ((paramFloat3 > 1.0F) && (paramBoolean)) {
      return new SizeF(paramFloat1, paramFloat2);
    }
    return new SizeF(paramFloat1 * paramFloat3, paramFloat2 * paramFloat3);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/util/SizeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */