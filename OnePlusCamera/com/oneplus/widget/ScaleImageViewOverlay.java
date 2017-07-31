package com.oneplus.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public abstract class ScaleImageViewOverlay
  extends View
{
  private Rect m_ImageBounds = new Rect();
  private ScaleImageView m_ScaleImageView;
  private ScaleImageView.StateCallback m_ScaleImageViewStateCallback = new ScaleImageView.StateCallback()
  {
    public void onAnimatingStateChanged(ScaleImageView paramAnonymousScaleImageView, boolean paramAnonymousBoolean)
    {
      ScaleImageViewOverlay.this.onImageAnimatingStateChanged(paramAnonymousBoolean);
    }
    
    public void onBoundsChanged(ScaleImageView paramAnonymousScaleImageView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
    {
      ScaleImageViewOverlay.-wrap0(ScaleImageViewOverlay.this, paramAnonymousScaleImageView, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
    }
    
    public void onLayoutChanged(ScaleImageView paramAnonymousScaleImageView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
    {
      ScaleImageViewOverlay.-wrap2(ScaleImageViewOverlay.this, paramAnonymousScaleImageView, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
    }
    
    public void onTargetBoundsChanged(ScaleImageView paramAnonymousScaleImageView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
    {
      ScaleImageViewOverlay.-wrap1(ScaleImageViewOverlay.this, paramAnonymousScaleImageView, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
    }
  };
  private Rect m_TargetImageBounds = new Rect();
  
  public ScaleImageViewOverlay(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public ScaleImageViewOverlay(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public ScaleImageViewOverlay(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private void onScaleImageBoundsChanged(ScaleImageView paramScaleImageView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int[] arrayOfInt1 = new int[2];
    int[] arrayOfInt2 = new int[2];
    getLocationInWindow(arrayOfInt1);
    paramScaleImageView.getLocationInWindow(arrayOfInt2);
    int i = arrayOfInt2[0] - arrayOfInt1[0];
    int j = arrayOfInt2[1] - arrayOfInt1[1];
    this.m_ImageBounds.set(paramInt1 + i, paramInt2 + j, paramInt3 + i, paramInt4 + j);
    onImageBoundsChanged(this.m_ImageBounds.left, this.m_ImageBounds.top, this.m_ImageBounds.right, this.m_ImageBounds.bottom);
  }
  
  private void onScaleImageTargetImageBoundsChanged(ScaleImageView paramScaleImageView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int[] arrayOfInt1 = new int[2];
    int[] arrayOfInt2 = new int[2];
    getLocationInWindow(arrayOfInt1);
    paramScaleImageView.getLocationInWindow(arrayOfInt2);
    int i = arrayOfInt2[0] - arrayOfInt1[0];
    int j = arrayOfInt2[1] - arrayOfInt1[1];
    this.m_TargetImageBounds.set(paramInt1 + i, paramInt2 + j, paramInt3 + i, paramInt4 + j);
    onTargetImageBoundsChanged(this.m_TargetImageBounds.left, this.m_TargetImageBounds.top, this.m_TargetImageBounds.right, this.m_TargetImageBounds.bottom);
  }
  
  private void onScaleImageViewLayoutChanged(ScaleImageView paramScaleImageView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    onImageLayoutChanged(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public Rect getImageBounds()
  {
    return new Rect(this.m_ImageBounds);
  }
  
  public ScaleImageView getScaleImageView()
  {
    return this.m_ScaleImageView;
  }
  
  public Rect getTargetImageBounds()
  {
    return new Rect(this.m_TargetImageBounds);
  }
  
  protected Rect mappingToScaleImageViewCoordinates(Rect paramRect)
  {
    if (this.m_ScaleImageView == null) {
      return null;
    }
    int[] arrayOfInt1 = new int[2];
    int[] arrayOfInt2 = new int[2];
    getLocationInWindow(arrayOfInt1);
    this.m_ScaleImageView.getLocationInWindow(arrayOfInt2);
    int i = arrayOfInt1[0];
    int j = arrayOfInt2[0];
    int k = arrayOfInt1[1];
    int m = arrayOfInt2[1];
    paramRect = new Rect(paramRect);
    paramRect.offset(i - j, k - m);
    return paramRect;
  }
  
  protected void onImageAnimatingStateChanged(boolean paramBoolean) {}
  
  protected void onImageBoundsChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  protected void onImageLayoutChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  protected void onTargetImageBoundsChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  protected void setImageBounds(Rect paramRect)
  {
    if (this.m_ScaleImageView == null) {
      return;
    }
    this.m_ScaleImageView.setImageBounds(mappingToScaleImageViewCoordinates(paramRect), false);
  }
  
  public void setScaleImageView(ScaleImageView paramScaleImageView)
  {
    if (this.m_ScaleImageView != null) {
      this.m_ScaleImageView.removeOnStateChangedCallback(this.m_ScaleImageViewStateCallback);
    }
    this.m_ScaleImageView = paramScaleImageView;
    this.m_ScaleImageView.addOnStateChangedCallback(this.m_ScaleImageViewStateCallback);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/widget/ScaleImageViewOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */