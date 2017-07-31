package com.oneplus.camera.ui;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

abstract interface CameraPager
{
  public abstract int getCurrentItem();
  
  public abstract int getVisibility();
  
  public abstract boolean onTouchEvent(MotionEvent paramMotionEvent);
  
  public abstract void removeAllViews();
  
  public abstract void setAdapter(PagerAdapter paramPagerAdapter);
  
  public abstract void setCurrentItem(int paramInt);
  
  public abstract void setCurrentItem(int paramInt, boolean paramBoolean);
  
  public abstract void setOffscreenPageLimit(int paramInt);
  
  public abstract void setOnPageChangeListener(ViewPager.OnPageChangeListener paramOnPageChangeListener);
  
  public abstract void setOnTouchListener(View.OnTouchListener paramOnTouchListener);
  
  public abstract void setOverScrollMode(int paramInt);
  
  public abstract void setPageMargin(int paramInt);
  
  public abstract void setSwipeable(boolean paramBoolean);
  
  public abstract void setVisibility(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CameraPager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */