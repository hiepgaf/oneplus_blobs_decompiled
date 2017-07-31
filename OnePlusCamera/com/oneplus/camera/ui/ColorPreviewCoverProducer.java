package com.oneplus.camera.ui;

import android.os.Handler;
import android.view.View;
import android.view.ViewPropertyAnimator;
import com.oneplus.base.Log;
import com.oneplus.camera.CameraActivity;

public class ColorPreviewCoverProducer
  extends BasePreviewCoverProducer
{
  private static final long ANIMATION_DURATION_MIN = 100L;
  private static final long ANIMATION_FADE_IN_DURATION = 100L;
  private static final long ANIMATION_FADE_OUT_DURATION = 200L;
  private int m_Color;
  private final Runnable m_OnFadeInAnimationEndRunnable = new Runnable()
  {
    public void run()
    {
      ColorPreviewCoverProducer.-wrap1(ColorPreviewCoverProducer.this);
    }
  };
  private final Runnable m_OnFadeOutAnimationEndRunnable = new Runnable()
  {
    public void run()
    {
      ColorPreviewCoverProducer.-wrap2(ColorPreviewCoverProducer.this);
    }
  };
  private final Runnable m_OnReverseFadeOutAnimationEndRunnable = new Runnable()
  {
    public void run()
    {
      ColorPreviewCoverProducer.-wrap3(ColorPreviewCoverProducer.this);
    }
  };
  
  public ColorPreviewCoverProducer(CameraActivity paramCameraActivity, int paramInt)
  {
    super(paramCameraActivity);
    this.m_Color = paramInt;
  }
  
  private void onFadeInAnimationEnd()
  {
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.READY_TO_OUT_ANIMATION);
  }
  
  private void onFadeOutAnimationEnd()
  {
    View localView1 = getPreviewCoverContainer();
    View localView2 = getPreviewCoverBackgroundView();
    localView1.setVisibility(4);
    localView2.animate().cancel();
    localView2.setVisibility(4);
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.STOPPED);
  }
  
  private void onReverseFadeOutAnimationEnd()
  {
    Log.v(this.TAG, "onReverseFadeOutAnimationEnd()");
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.READY_TO_OUT_ANIMATION);
  }
  
  public boolean isAlphaBlending()
  {
    return true;
  }
  
  protected boolean preparePreviewCover()
  {
    getPreviewCoverBackgroundView().setBackgroundColor(this.m_Color);
    getHandler().post(new Runnable()
    {
      public void run()
      {
        if (ColorPreviewCoverProducer.this.get(ColorPreviewCoverProducer.PROP_STATE) == PreviewCoverProducer.State.PREPARING)
        {
          ColorPreviewCoverProducer.-wrap0(ColorPreviewCoverProducer.this, ColorPreviewCoverProducer.PROP_STATE, PreviewCoverProducer.State.READY_TO_IN_ANIMATION);
          return;
        }
        Log.w(ColorPreviewCoverProducer.-get0(ColorPreviewCoverProducer.this), "preparePreviewCover() - Current state is " + ColorPreviewCoverProducer.this.get(ColorPreviewCoverProducer.PROP_STATE) + ", keep current state");
      }
    });
    return true;
  }
  
  protected void reverseInAnimation() {}
  
  protected void reverseOutAnimation()
  {
    if (get(PROP_STATE) != PreviewCoverProducer.State.OUT_ANIMATION) {
      return;
    }
    Log.v(this.TAG, "reverseOutAnimation()");
    View localView = getPreviewCoverBackgroundView();
    localView.animate().cancel();
    long l2 = ((1.0F - localView.getAlpha()) * 200.0F);
    long l1 = l2;
    if (l2 < 100L) {
      l1 = 100L;
    }
    localView.animate().alpha(1.0F).setDuration(l1).withEndAction(this.m_OnReverseFadeOutAnimationEndRunnable);
  }
  
  protected void startInAnimation(boolean paramBoolean)
  {
    if (get(PROP_STATE) != PreviewCoverProducer.State.READY_TO_IN_ANIMATION) {
      return;
    }
    if (!paramBoolean)
    {
      this.m_OnFadeInAnimationEndRunnable.run();
      return;
    }
    Log.v(this.TAG, "startInAnimation()");
    View localView1 = getPreviewCoverContainer();
    View localView2 = getPreviewCoverBackgroundView();
    localView1.setVisibility(0);
    localView1.setAlpha(1.0F);
    localView2.setVisibility(0);
    localView2.setAlpha(0.0F);
    localView2.animate().alpha(1.0F).setDuration(100L).withEndAction(this.m_OnFadeInAnimationEndRunnable);
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.IN_ANIMATION);
  }
  
  protected void startOutAnimation(boolean paramBoolean)
  {
    if (get(PROP_STATE) != PreviewCoverProducer.State.READY_TO_OUT_ANIMATION) {
      return;
    }
    if (!paramBoolean)
    {
      this.m_OnFadeOutAnimationEndRunnable.run();
      return;
    }
    Log.v(this.TAG, "startOutAnimation()");
    View localView = getPreviewCoverBackgroundView();
    localView.setAlpha(1.0F);
    localView.animate().alpha(0.0F).setDuration(200L).withEndAction(this.m_OnFadeOutAnimationEndRunnable);
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.OUT_ANIMATION);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ColorPreviewCoverProducer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */