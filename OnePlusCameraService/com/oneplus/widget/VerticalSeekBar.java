package com.oneplus.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar
  extends SeekBar
{
  public VerticalSeekBar(Context paramContext)
  {
    super(paramContext);
  }
  
  public VerticalSeekBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    try
    {
      paramCanvas.rotate(-90.0F);
      paramCanvas.translate(-getHeight(), 0.0F);
      super.onDraw(paramCanvas);
      return;
    }
    finally
    {
      paramCanvas = finally;
      throw paramCanvas;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    try
    {
      super.onMeasure(paramInt2, paramInt1);
      setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt2, paramInt1, paramInt4, paramInt3);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!isEnabled()) {
      return false;
    }
    switch (paramMotionEvent.getAction())
    {
    }
    for (;;)
    {
      return true;
      setProgress(getMax() - (int)(getMax() * paramMotionEvent.getY() / getHeight()));
    }
  }
  
  public void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.setPadding(paramInt4, paramInt1, paramInt2, paramInt3);
  }
  
  public void setProgress(int paramInt)
  {
    try
    {
      super.setProgress(paramInt);
      onSizeChanged(getWidth(), getHeight(), 0, 0);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/widget/VerticalSeekBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */