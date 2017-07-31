package com.oneplus.camera.ui;

import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import com.oneplus.base.Log;
import com.oneplus.camera.CameraActivity;

public class FlipBlurPreviewCoverProducer
  extends BlurPreviewCoverProducer
{
  private static final long ANIMATION_ROTATE_HALF_DURATION = 200L;
  private final Runnable m_OnFlipAnimationEndRunnable = new Runnable()
  {
    public void run()
    {
      FlipBlurPreviewCoverProducer.-wrap0(FlipBlurPreviewCoverProducer.this);
    }
  };
  
  public FlipBlurPreviewCoverProducer(CameraActivity paramCameraActivity)
  {
    super(paramCameraActivity);
  }
  
  private void onFlipAnimationEnd()
  {
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.READY_TO_OUT_ANIMATION);
  }
  
  protected void startInAnimation(boolean paramBoolean)
  {
    if (get(PROP_STATE) != PreviewCoverProducer.State.READY_TO_IN_ANIMATION) {
      return;
    }
    if (!paramBoolean)
    {
      this.m_OnFlipAnimationEndRunnable.run();
      return;
    }
    Log.v(this.TAG, "startInAnimation()");
    View localView1 = getPreviewCoverContainer();
    View localView2 = getPreviewCoverBackgroundView();
    final ImageView localImageView1 = getPreviewCoverBaseImageView();
    final ImageView localImageView2 = getPreviewCoverTopImageView();
    localView1.setVisibility(0);
    localView2.setVisibility(0);
    localImageView1.setVisibility(0);
    localView2.setAlpha(1.0F);
    localImageView1.setAlpha(1.0F);
    localImageView1.setRotationY(0.0F);
    localImageView1.setScaleX(1.0F);
    localImageView1.setScaleY(1.0F);
    localImageView1.animate().scaleX(0.6F).scaleY(0.6F).rotationY(90.0F).setDuration(200L).withEndAction(new Runnable()
    {
      public void run()
      {
        Log.v(FlipBlurPreviewCoverProducer.-get0(FlipBlurPreviewCoverProducer.this), "startInAnimation() - On first image animation end");
        localImageView1.setVisibility(4);
        localImageView2.setVisibility(0);
        localImageView2.setAlpha(1.0F);
        localImageView2.setRotationY(90.0F);
        localImageView2.setScaleX(0.6F);
        localImageView2.setScaleY(0.6F);
        localImageView2.animate().scaleX(1.0F).scaleY(1.0F).rotationY(180.0F).setDuration(200L).withEndAction(FlipBlurPreviewCoverProducer.-get1(FlipBlurPreviewCoverProducer.this)).start();
      }
    }).start();
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.IN_ANIMATION);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/FlipBlurPreviewCoverProducer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */