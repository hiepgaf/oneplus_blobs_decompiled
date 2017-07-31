package com.oneplus.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;

public final class ViewUtils
{
  public static int convertDpToPx(Resources paramResources, float paramFloat)
  {
    return Math.round(paramResources.getDisplayMetrics().densityDpi / 160.0F * paramFloat);
  }
  
  public static View inflate(Context paramContext, int paramInt, ViewGroup paramViewGroup)
  {
    paramContext = View.inflate(paramContext, paramInt, paramViewGroup);
    if (paramViewGroup == null) {
      return paramContext;
    }
    return paramViewGroup.getChildAt(paramViewGroup.getChildCount() - 1);
  }
  
  public static void rotate(View paramView, float paramFloat, long paramLong)
  {
    rotate(paramView, paramFloat, paramLong, null);
  }
  
  public static void rotate(View paramView, float paramFloat, long paramLong, Interpolator paramInterpolator)
  {
    if (paramView == null) {
      return;
    }
    ViewPropertyAnimator localViewPropertyAnimator = paramView.animate();
    localViewPropertyAnimator.rotation(paramFloat);
    if (paramLong > 0L)
    {
      localViewPropertyAnimator.setDuration(paramLong);
      if (paramInterpolator != null) {
        localViewPropertyAnimator.setInterpolator(paramInterpolator);
      }
    }
    for (;;)
    {
      localViewPropertyAnimator.start();
      return;
      localViewPropertyAnimator.setDuration(0L);
      paramView.setRotation(paramFloat);
    }
  }
  
  public static void setHeight(View paramView, int paramInt)
  {
    if (paramView == null) {
      return;
    }
    paramView.getLayoutParams().height = paramInt;
    paramView.requestLayout();
  }
  
  public static void setMargins(View paramView, int paramInt)
  {
    setMargins(paramView, paramInt, paramInt, paramInt, paramInt);
  }
  
  public static void setMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramView == null) {
      return;
    }
    Object localObject = paramView.getLayoutParams();
    if ((localObject instanceof ViewGroup.MarginLayoutParams))
    {
      localObject = (ViewGroup.MarginLayoutParams)localObject;
      ((ViewGroup.MarginLayoutParams)localObject).leftMargin = paramInt1;
      ((ViewGroup.MarginLayoutParams)localObject).topMargin = paramInt2;
      ((ViewGroup.MarginLayoutParams)localObject).rightMargin = paramInt3;
      ((ViewGroup.MarginLayoutParams)localObject).bottomMargin = paramInt4;
      paramView.requestLayout();
    }
  }
  
  public static void setSize(View paramView, int paramInt1, int paramInt2)
  {
    if (paramView == null) {
      return;
    }
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    localLayoutParams.width = paramInt1;
    localLayoutParams.height = paramInt2;
    paramView.requestLayout();
  }
  
  public static void setVisibility(View paramView, boolean paramBoolean, long paramLong)
  {
    setVisibility(paramView, paramBoolean, paramLong, null, null);
  }
  
  public static void setVisibility(View paramView, boolean paramBoolean, long paramLong, Interpolator paramInterpolator)
  {
    setVisibility(paramView, paramBoolean, paramLong, paramInterpolator, null);
  }
  
  public static void setVisibility(final View paramView, boolean paramBoolean, long paramLong, Interpolator paramInterpolator, AnimationCompletedCallback paramAnimationCompletedCallback)
  {
    if (paramView == null) {
      return;
    }
    Object localObject2 = null;
    Object localObject1 = null;
    if (paramBoolean) {
      if (paramView.getVisibility() != 0)
      {
        if (paramLong >= 0L) {
          localObject1 = new AlphaAnimation(0.0F, 1.0F);
        }
        paramView.setVisibility(0);
      }
    }
    for (;;)
    {
      if (localObject1 != null)
      {
        ((Animation)localObject1).setDuration(paramLong);
        if (paramInterpolator != null) {
          ((Animation)localObject1).setInterpolator(paramInterpolator);
        }
        if (paramAnimationCompletedCallback != null) {
          ((Animation)localObject1).setAnimationListener(new Animation.AnimationListener()
          {
            public void onAnimationEnd(Animation paramAnonymousAnimation)
            {
              this.val$callback.onAnimationCompleted(paramView, false);
            }
            
            public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
            
            public void onAnimationStart(Animation paramAnonymousAnimation) {}
          });
        }
        paramView.startAnimation((Animation)localObject1);
      }
      return;
      return;
      if (paramView.getVisibility() != 0) {
        break;
      }
      localObject1 = localObject2;
      if (paramLong >= 0L) {
        localObject1 = new AlphaAnimation(1.0F, 0.0F);
      }
      paramView.setVisibility(4);
    }
  }
  
  public static void setWidth(View paramView, int paramInt)
  {
    if (paramView == null) {
      return;
    }
    paramView.getLayoutParams().width = paramInt;
    paramView.requestLayout();
  }
  
  public static abstract interface AnimationCompletedCallback
  {
    public abstract void onAnimationCompleted(View paramView, boolean paramBoolean);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/widget/ViewUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */