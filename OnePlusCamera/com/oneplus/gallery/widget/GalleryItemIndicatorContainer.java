package com.oneplus.gallery.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import com.oneplus.util.SizeUtils;

public class GalleryItemIndicatorContainer
  extends ViewGroup
{
  private int m_OriginalImageHeight;
  private int m_OriginalImageWidth;
  
  public GalleryItemIndicatorContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    int j;
    if ((this.m_OriginalImageWidth > 0) && (this.m_OriginalImageHeight > 0))
    {
      paramInt1 = paramInt3 - paramInt1;
      paramInt2 = paramInt4 - paramInt2;
      Size localSize = SizeUtils.getRatioStretchedSize(this.m_OriginalImageWidth, this.m_OriginalImageHeight, paramInt1, paramInt2, false);
      i = (paramInt1 - localSize.getWidth()) / 2;
      j = (paramInt2 - localSize.getHeight()) / 2;
      int m = localSize.getWidth();
      int k = localSize.getHeight();
      paramInt2 = Math.max(i, getPaddingLeft());
      paramInt1 = Math.max(j, getPaddingTop());
      paramInt3 = Math.min(i + m, paramInt3 - getPaddingRight());
      i = Math.min(j + k, paramInt4 - getPaddingBottom());
      paramInt4 = paramInt1;
      paramInt1 = i;
    }
    for (;;)
    {
      i = getChildCount() - 1;
      while (i >= 0)
      {
        getChildAt(i).layout(paramInt2, paramInt4, paramInt3, paramInt1);
        i -= 1;
      }
      j = paramInt1;
      i = paramInt2;
      paramInt1 = paramInt4;
      paramInt2 = j;
      paramInt4 = i;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    int j = paramInt1;
    int i = paramInt2;
    if (this.m_OriginalImageWidth > 0)
    {
      j = paramInt1;
      i = paramInt2;
      if (this.m_OriginalImageHeight > 0)
      {
        j = paramInt1;
        i = paramInt2;
        if (View.MeasureSpec.getMode(paramInt1) == 1073741824)
        {
          j = paramInt1;
          i = paramInt2;
          if (View.MeasureSpec.getMode(paramInt2) == 1073741824)
          {
            paramInt1 = View.MeasureSpec.getSize(paramInt1);
            paramInt2 = View.MeasureSpec.getSize(paramInt2);
            Size localSize = SizeUtils.getRatioStretchedSize(this.m_OriginalImageWidth, this.m_OriginalImageHeight, paramInt1, paramInt2, false);
            paramInt1 = Math.min(localSize.getWidth(), paramInt1 - getPaddingStart() - getPaddingEnd());
            paramInt2 = Math.min(localSize.getHeight(), paramInt2 - getPaddingTop() - getPaddingBottom());
            j = View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824);
            i = View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824);
          }
        }
      }
    }
    paramInt1 = getChildCount() - 1;
    while (paramInt1 >= 0)
    {
      getChildAt(paramInt1).measure(j, i);
      paramInt1 -= 1;
    }
  }
  
  public void setOriginalImageSize(int paramInt1, int paramInt2)
  {
    this.m_OriginalImageWidth = paramInt1;
    this.m_OriginalImageHeight = paramInt2;
    requestLayout();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/widget/GalleryItemIndicatorContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */