package com.oneplus.camera.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SwipeableViewPager
  extends ViewPager
  implements CameraPager
{
  private boolean mSwipeable = true;
  
  public SwipeableViewPager(Context paramContext)
  {
    super(paramContext);
  }
  
  public SwipeableViewPager(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mSwipeable) {
      return super.onInterceptTouchEvent(paramMotionEvent);
    }
    return false;
  }
  
  public void setSwipeable(boolean paramBoolean)
  {
    this.mSwipeable = paramBoolean;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/SwipeableViewPager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */