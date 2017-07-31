package com.android.server.policy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class RecentApplicationsBackground
  extends LinearLayout
{
  private static final String TAG = "RecentApplicationsBackground";
  private Drawable mBackground;
  private boolean mBackgroundSizeChanged;
  private Rect mTmp0 = new Rect();
  private Rect mTmp1 = new Rect();
  
  public RecentApplicationsBackground(Context paramContext)
  {
    this(paramContext, null);
    init();
  }
  
  public RecentApplicationsBackground(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private void getChildBounds(Rect paramRect)
  {
    paramRect.top = Integer.MAX_VALUE;
    paramRect.left = Integer.MAX_VALUE;
    paramRect.right = Integer.MIN_VALUE;
    paramRect.bottom = Integer.MIN_VALUE;
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() == 0)
      {
        paramRect.left = Math.min(paramRect.left, localView.getLeft());
        paramRect.top = Math.min(paramRect.top, localView.getTop());
        paramRect.right = Math.max(paramRect.right, localView.getRight());
        paramRect.bottom = Math.max(paramRect.bottom, localView.getBottom());
      }
      i += 1;
    }
  }
  
  private void init()
  {
    this.mBackground = getBackground();
    setBackgroundDrawable(null);
    setPadding(0, 0, 0, 0);
    setGravity(17);
  }
  
  public void draw(Canvas paramCanvas)
  {
    Drawable localDrawable = this.mBackground;
    if ((localDrawable != null) && (this.mBackgroundSizeChanged))
    {
      this.mBackgroundSizeChanged = false;
      Rect localRect1 = this.mTmp0;
      Rect localRect2 = this.mTmp1;
      this.mBackground.getPadding(localRect2);
      getChildBounds(localRect1);
      int i = localRect1.top;
      int j = localRect2.top;
      int k = localRect1.bottom;
      int m = localRect2.bottom;
      localDrawable.setBounds(0, i - j, getRight(), k + m);
    }
    this.mBackground.draw(paramCanvas);
    paramCanvas.drawARGB(191, 0, 0, 0);
    super.draw(paramCanvas);
  }
  
  protected void drawableStateChanged()
  {
    Drawable localDrawable = this.mBackground;
    if ((localDrawable != null) && (localDrawable.isStateful())) {
      localDrawable.setState(getDrawableState());
    }
    super.drawableStateChanged();
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.mBackground != null) {
      this.mBackground.jumpToCurrentState();
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mBackground.setCallback(this);
    setWillNotDraw(false);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mBackground.setCallback(null);
  }
  
  protected boolean setFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setWillNotDraw(false);
    if ((this.mLeft != paramInt1) || (this.mRight != paramInt3)) {}
    for (;;)
    {
      this.mBackgroundSizeChanged = true;
      do
      {
        return super.setFrame(paramInt1, paramInt2, paramInt3, paramInt4);
        if (this.mTop != paramInt2) {
          break;
        }
      } while (this.mBottom == paramInt4);
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if (paramDrawable != this.mBackground) {
      return super.verifyDrawable(paramDrawable);
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/RecentApplicationsBackground.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */