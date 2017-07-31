package com.oneplus.filter;

import android.graphics.Bitmap;
import android.graphics.Rect;

public abstract interface ImageOverlay
{
  public abstract void apply(Bitmap paramBitmap, int paramInt);
  
  public abstract void setAlpha(int paramInt);
  
  public abstract void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void setBounds(Rect paramRect);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/filter/ImageOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */