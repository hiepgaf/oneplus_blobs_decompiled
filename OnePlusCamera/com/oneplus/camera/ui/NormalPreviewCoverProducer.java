package com.oneplus.camera.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.oneplus.base.Log;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;

public class NormalPreviewCoverProducer
  extends BasePreviewCoverProducer
{
  private static final long ANIMATION_FADE_OUT_DURATION = 200L;
  private final Runnable m_OnFadeOutAnimationEndRunnable = new Runnable()
  {
    public void run()
    {
      NormalPreviewCoverProducer.-wrap0(NormalPreviewCoverProducer.this);
    }
  };
  private final Runnable m_OnFadeOutAnimationStartRunnable = new Runnable()
  {
    public void run()
    {
      NormalPreviewCoverProducer.-wrap1(NormalPreviewCoverProducer.this);
    }
  };
  private final Runnable m_OnReverseFadeOutAnimationEndRunnable = new Runnable()
  {
    public void run()
    {
      NormalPreviewCoverProducer.-wrap2(NormalPreviewCoverProducer.this);
    }
  };
  private Viewfinder m_Viewfinder;
  
  public NormalPreviewCoverProducer(CameraActivity paramCameraActivity)
  {
    super(paramCameraActivity);
  }
  
  private void onFadeOutAnimationEnd()
  {
    View localView1 = getPreviewCoverContainer();
    View localView2 = getPreviewCoverBackgroundView();
    ImageView localImageView = getPreviewCoverBaseImageView();
    localView1.setVisibility(4);
    localView2.animate().cancel();
    localView2.setVisibility(4);
    localImageView.setVisibility(4);
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.STOPPED);
  }
  
  private void onFadeOutAnimationStart() {}
  
  private void onReverseFadeOutAnimationEnd()
  {
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.READY_TO_OUT_ANIMATION);
  }
  
  public boolean isAlphaBlending()
  {
    return false;
  }
  
  protected boolean preparePreviewCover()
  {
    Object localObject1 = getCameraActivity();
    if (this.m_Viewfinder == null) {
      this.m_Viewfinder = ((Viewfinder)((CameraActivity)localObject1).findComponent(Viewfinder.class));
    }
    if (this.m_Viewfinder == null)
    {
      Log.e(this.TAG, "preparePreviewCover() - No viewfinder");
      return false;
    }
    if ((Camera)((CameraActivity)localObject1).get(CameraActivity.PROP_CAMERA) == null)
    {
      Log.w(this.TAG, "preparePreviewCover() - No camera");
      return false;
    }
    RectF localRectF = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
    Object localObject2 = null;
    localObject1 = localObject2;
    if (this.m_Viewfinder != null)
    {
      localObject1 = localObject2;
      if (((Boolean)this.m_Viewfinder.get(Viewfinder.PROP_IS_DISPLAY_PREVIEW_FRAME_COPY_SUPPORTED)).booleanValue())
      {
        localObject1 = localObject2;
        if (localRectF.width() > 0.0F)
        {
          localObject1 = localObject2;
          if (localRectF.height() > 0.0F)
          {
            localObject2 = Bitmap.createBitmap((int)localRectF.width(), (int)localRectF.height(), Bitmap.Config.ARGB_8888);
            localObject1 = localObject2;
            if (!this.m_Viewfinder.copyDisplayPreviewFrame((Bitmap)localObject2, 0))
            {
              Log.e(this.TAG, "preparePreviewCover() - Cannot copy display preview frame");
              return false;
            }
          }
        }
      }
    }
    if (localObject1 == null)
    {
      Log.e(this.TAG, "preparePreviewCover() - Preview frame is null");
      return false;
    }
    localObject2 = getPreviewCoverBaseImageView();
    ((ImageView)localObject2).setImageBitmap((Bitmap)localObject1);
    localObject1 = (RelativeLayout.LayoutParams)((ImageView)localObject2).getLayoutParams();
    ((RelativeLayout.LayoutParams)localObject1).topMargin = Math.round(localRectF.top);
    ((RelativeLayout.LayoutParams)localObject1).width = Math.round(localRectF.width());
    ((RelativeLayout.LayoutParams)localObject1).height = Math.round(localRectF.height());
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.READY_TO_IN_ANIMATION);
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
    ImageView localImageView = getPreviewCoverBaseImageView();
    localView.animate().cancel();
    localImageView.animate().cancel();
    long l = ((1.0F - localImageView.getAlpha()) * 200.0F);
    localView.animate().alpha(1.0F).setDuration(l);
    localImageView.animate().alpha(1.0F).setDuration(l).withEndAction(this.m_OnReverseFadeOutAnimationEndRunnable);
  }
  
  protected void startInAnimation(boolean paramBoolean)
  {
    if (get(PROP_STATE) != PreviewCoverProducer.State.READY_TO_IN_ANIMATION) {
      return;
    }
    if (!paramBoolean)
    {
      setReadOnly(PROP_STATE, PreviewCoverProducer.State.READY_TO_OUT_ANIMATION);
      return;
    }
    Log.v(this.TAG, "startInAnimation()");
    View localView1 = getPreviewCoverContainer();
    View localView2 = getPreviewCoverBackgroundView();
    ImageView localImageView = getPreviewCoverBaseImageView();
    localView1.setVisibility(0);
    localView2.setVisibility(0);
    localImageView.setVisibility(0);
    localView2.setAlpha(1.0F);
    localImageView.setAlpha(1.0F);
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.IN_ANIMATION);
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.READY_TO_OUT_ANIMATION);
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
    ImageView localImageView = getPreviewCoverBaseImageView();
    localView.setAlpha(1.0F);
    localImageView.setAlpha(1.0F);
    localView.animate().alpha(0.0F).setDuration(200L);
    localImageView.animate().alpha(0.0F).setDuration(200L).withStartAction(this.m_OnFadeOutAnimationStartRunnable).withEndAction(this.m_OnFadeOutAnimationEndRunnable);
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.OUT_ANIMATION);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/NormalPreviewCoverProducer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */