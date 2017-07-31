package com.oneplus.util;

import android.graphics.RectF;

public class NormalizedRectF
{
  private static final RectF DEFAULT_NORMALIZED_RECT = new RectF(0.0F, 0.0F, 1.0F, 1.0F);
  private RectF m_NormalizedValue = new RectF(DEFAULT_NORMALIZED_RECT);
  
  public NormalizedRectF() {}
  
  public NormalizedRectF(RectF paramRectF)
  {
    setNormalizedValue(paramRectF);
  }
  
  public RectF getNormalizedValue()
  {
    return getNormalizedValue(CorrectionMode.CLIP);
  }
  
  public RectF getNormalizedValue(CorrectionMode paramCorrectionMode)
  {
    RectF localRectF = new RectF(this.m_NormalizedValue);
    if (paramCorrectionMode == null) {
      return localRectF;
    }
    return paramCorrectionMode.correct(localRectF, DEFAULT_NORMALIZED_RECT);
  }
  
  public RectF getValue(float paramFloat1, float paramFloat2)
  {
    return getValue(paramFloat1, paramFloat2, CorrectionMode.CLIP);
  }
  
  public RectF getValue(float paramFloat1, float paramFloat2, CorrectionMode paramCorrectionMode)
  {
    if ((paramFloat1 <= 0.0F) || (paramFloat2 <= 0.0F)) {
      return null;
    }
    RectF localRectF = new RectF(this.m_NormalizedValue.left * paramFloat1, this.m_NormalizedValue.top * paramFloat2, this.m_NormalizedValue.right * paramFloat1, this.m_NormalizedValue.bottom * paramFloat2);
    if (paramCorrectionMode == null) {
      return localRectF;
    }
    return paramCorrectionMode.correct(localRectF, new RectF(0.0F, 0.0F, paramFloat1, paramFloat2));
  }
  
  public boolean setNormalizedValue(RectF paramRectF)
  {
    RectF localRectF;
    if (paramRectF != null)
    {
      localRectF = paramRectF;
      if (!paramRectF.isEmpty()) {}
    }
    else
    {
      localRectF = DEFAULT_NORMALIZED_RECT;
    }
    if (localRectF.equals(this.m_NormalizedValue)) {
      return false;
    }
    this.m_NormalizedValue.set(localRectF);
    return true;
  }
  
  public boolean setValue(RectF paramRectF, float paramFloat1, float paramFloat2)
  {
    if ((paramRectF == null) || (paramRectF.isEmpty())) {
      return false;
    }
    if ((paramFloat1 <= 0.0F) || (paramFloat2 <= 0.0F)) {
      return false;
    }
    float f1 = paramRectF.left / paramFloat1;
    float f2 = paramRectF.top / paramFloat2;
    paramFloat1 = paramRectF.right / paramFloat1;
    paramFloat2 = paramRectF.bottom / paramFloat2;
    this.m_NormalizedValue.set(f1, f2, paramFloat1, paramFloat2);
    return true;
  }
  
  public static abstract class CorrectionMode
  {
    public static final CorrectionMode CENTER_SCALE = new CorrectionMode()
    {
      public RectF correct(RectF paramAnonymousRectF1, RectF paramAnonymousRectF2)
      {
        if ((paramAnonymousRectF1 == null) || (paramAnonymousRectF1.isEmpty())) {
          return paramAnonymousRectF1;
        }
        float f5 = paramAnonymousRectF1.centerX();
        float f4 = paramAnonymousRectF1.centerY();
        float f1 = 0.0F;
        if (paramAnonymousRectF1.left < paramAnonymousRectF2.left) {
          f1 = paramAnonymousRectF2.left - paramAnonymousRectF1.left;
        }
        float f2 = f1;
        if (paramAnonymousRectF1.right > paramAnonymousRectF2.right) {
          f2 = Math.max(f1, paramAnonymousRectF1.right - paramAnonymousRectF2.right);
        }
        f1 = 0.0F;
        if (paramAnonymousRectF1.top < paramAnonymousRectF2.top) {
          f1 = paramAnonymousRectF2.top - paramAnonymousRectF1.top;
        }
        float f3 = f1;
        if (paramAnonymousRectF1.bottom > paramAnonymousRectF2.bottom) {
          f3 = Math.max(f1, paramAnonymousRectF1.bottom - paramAnonymousRectF2.bottom);
        }
        if ((f2 > 0.0F) || (f3 > 0.0F))
        {
          f2 = Math.min((paramAnonymousRectF1.width() - 2.0F * f2) / paramAnonymousRectF1.width(), (paramAnonymousRectF1.height() - 2.0F * f3) / paramAnonymousRectF1.height());
          if ((f2 <= 0.0F) || (f2 > 1.0F)) {
            paramAnonymousRectF1.setEmpty();
          }
        }
        else
        {
          return paramAnonymousRectF1;
        }
        f1 = paramAnonymousRectF1.width() * f2;
        f2 = paramAnonymousRectF1.height() * f2;
        f3 = f5 - f1 / 2.0F;
        f4 -= f2 / 2.0F;
        paramAnonymousRectF1.set(f3, f4, f3 + f1, f4 + f2);
        return paramAnonymousRectF1;
      }
    };
    public static final CorrectionMode CLIP = new CorrectionMode()
    {
      public RectF correct(RectF paramAnonymousRectF1, RectF paramAnonymousRectF2)
      {
        if ((paramAnonymousRectF1 == null) || (paramAnonymousRectF1.isEmpty())) {
          return paramAnonymousRectF1;
        }
        float f1 = Math.max(paramAnonymousRectF2.left, paramAnonymousRectF1.left);
        float f2 = Math.max(paramAnonymousRectF2.top, paramAnonymousRectF1.top);
        float f3 = Math.min(paramAnonymousRectF2.right, paramAnonymousRectF1.right);
        float f4 = Math.min(paramAnonymousRectF2.bottom, paramAnonymousRectF1.bottom);
        if ((f1 > f3) || (f2 > f4))
        {
          paramAnonymousRectF1.setEmpty();
          return paramAnonymousRectF1;
        }
        paramAnonymousRectF1.set(f1, f2, f3, f4);
        return paramAnonymousRectF1;
      }
    };
    
    public abstract RectF correct(RectF paramRectF1, RectF paramRectF2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/util/NormalizedRectF.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */