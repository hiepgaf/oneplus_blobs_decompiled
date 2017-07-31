package com.oneplus.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class GestureScrollView
  extends ScrollView
{
  private GestureDetector m_GestureDetector;
  
  public GestureScrollView(Context paramContext)
  {
    super(paramContext);
  }
  
  public GestureScrollView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    super.onTouchEvent(paramMotionEvent);
    if (this.m_GestureDetector != null) {
      this.m_GestureDetector.handleTouchEvent(paramMotionEvent);
    }
    return true;
  }
  
  public void setGestureDetector(GestureDetector paramGestureDetector)
  {
    this.m_GestureDetector = paramGestureDetector;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/GestureScrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */