package com.android.server.policy.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import com.android.server.policy.OpGlobalActions.ActionState;

public class OpGlobalActionsView
  extends FrameLayout
{
  private View mBgView = null;
  private AnimationSet mConfirmedAnimation = new AnimationSet(true);
  private OnViewTouchListener mOnViewTouchListener;
  private OnQuitListener mQuitListener;
  private AnimationSet mShowAnimation = new AnimationSet(true);
  
  public OpGlobalActionsView(Context paramContext)
  {
    super(paramContext);
    initAnimation();
  }
  
  public OpGlobalActionsView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initAnimation();
  }
  
  public OpGlobalActionsView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initAnimation();
  }
  
  private void initAnimation()
  {
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.0F, 0.8F);
    localAlphaAnimation.setDuration(300L);
    this.mShowAnimation.addAnimation(localAlphaAnimation);
    this.mShowAnimation.setFillAfter(true);
    this.mShowAnimation.setInterpolator(new DecelerateInterpolator());
    localAlphaAnimation = new AlphaAnimation(0.8F, 1.0F);
    localAlphaAnimation.setDuration(300L);
    this.mConfirmedAnimation.addAnimation(localAlphaAnimation);
    this.mConfirmedAnimation.setFillAfter(true);
    this.mConfirmedAnimation.setInterpolator(new DecelerateInterpolator());
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((paramKeyEvent.getKeyCode() == 24) || (paramKeyEvent.getKeyCode() == 25)) {
      return true;
    }
    if ((paramKeyEvent.getKeyCode() == 3) || (paramKeyEvent.getKeyCode() == 4)) {}
    while ((paramKeyEvent.getKeyCode() == 187) || (paramKeyEvent.getKeyCode() == 82))
    {
      if (paramKeyEvent.getAction() == 1) {
        removePowerView();
      }
      return true;
    }
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    return this.mOnViewTouchListener.OnTouch(paramMotionEvent);
  }
  
  public void removePowerView()
  {
    if (this.mQuitListener != null) {
      this.mQuitListener.OnQuit();
    }
  }
  
  public void setOnQuitListener(OnQuitListener paramOnQuitListener)
  {
    this.mQuitListener = paramOnQuitListener;
  }
  
  public void setOnViewTouchListener(OnViewTouchListener paramOnViewTouchListener)
  {
    this.mOnViewTouchListener = paramOnViewTouchListener;
  }
  
  public void startAnimate(OpGlobalActions.ActionState paramActionState)
  {
    if (this.mBgView == null) {
      this.mBgView = findViewById(84672517);
    }
    if (paramActionState == OpGlobalActions.ActionState.SHOWING) {
      this.mBgView.setAnimation(this.mShowAnimation);
    }
    while (paramActionState != OpGlobalActions.ActionState.CONFIRMED) {
      return;
    }
    this.mBgView.setAnimation(this.mConfirmedAnimation);
  }
  
  public static abstract interface OnQuitListener
  {
    public abstract void OnQuit();
  }
  
  public static abstract interface OnViewTouchListener
  {
    public abstract boolean OnTouch(MotionEvent paramMotionEvent);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/ui/OpGlobalActionsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */