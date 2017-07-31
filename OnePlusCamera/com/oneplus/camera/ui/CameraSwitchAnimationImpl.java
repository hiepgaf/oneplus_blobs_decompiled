package com.oneplus.camera.ui;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Message;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.OperationState;

public class CameraSwitchAnimationImpl
  extends BaseSwitchAnimationImpl
  implements CameraSwitchAnimation
{
  private static final long ANIMATION_FADE_OUT_DELAY = 0L;
  private static final long ANIMATION_FADE_OUT_DURATION = 150L;
  private static final long ANIMATION_ROTATE_HALF_DURATION = 200L;
  private static final int MSG_FADE_OUT = 100000;
  private boolean m_IsAnimationEnd;
  private boolean m_IsFirstPreviewFrameReceived;
  private RectF m_PreviewBounds;
  private View m_SwitchAnimationContainer;
  private ImageView[] m_SwitchAnimationImageViews;
  private Viewfinder m_Viewfinder;
  
  protected CameraSwitchAnimationImpl(CameraActivity paramCameraActivity)
  {
    super("Camera Switch Animation Impl", paramCameraActivity);
  }
  
  private void onFlipperAnimationEnd()
  {
    Log.v(this.TAG, "onFlipperAnimationEnd()");
    this.m_IsAnimationEnd = true;
    if (this.m_IsFirstPreviewFrameReceived) {
      startFadeOutAnimation();
    }
  }
  
  private void startFadeOutAnimation()
  {
    Log.v(this.TAG, "startFadeOutAnimation()");
    this.m_SwitchAnimationContainer.animate().alpha(0.0F).setDuration(150L).withEndAction(new Runnable()
    {
      public void run()
      {
        Log.v(CameraSwitchAnimationImpl.-get0(CameraSwitchAnimationImpl.this), "startFadeOutAnimation() - On fade out animation end");
        CameraSwitchAnimationImpl.this.reset();
      }
    }).start();
  }
  
  private void startFlipperAnimation()
  {
    Log.v(this.TAG, "startFlipperAnimation()");
    this.m_SwitchAnimationImageViews[0].setRotationY(0.0F);
    this.m_SwitchAnimationImageViews[0].setScaleX(1.0F);
    this.m_SwitchAnimationImageViews[0].setScaleY(1.0F);
    this.m_SwitchAnimationImageViews[0].animate().scaleX(0.6F).scaleY(0.6F).rotationY(90.0F).setDuration(200L).withEndAction(new Runnable()
    {
      public void run()
      {
        Log.v(CameraSwitchAnimationImpl.-get0(CameraSwitchAnimationImpl.this), "startFlipperAnimation() - On first image animation end");
        CameraSwitchAnimationImpl.-get1(CameraSwitchAnimationImpl.this)[0].setVisibility(8);
        CameraSwitchAnimationImpl.-get1(CameraSwitchAnimationImpl.this)[1].setVisibility(0);
        CameraSwitchAnimationImpl.-get1(CameraSwitchAnimationImpl.this)[1].setRotationY(90.0F);
        CameraSwitchAnimationImpl.-get1(CameraSwitchAnimationImpl.this)[1].setScaleX(0.6F);
        CameraSwitchAnimationImpl.-get1(CameraSwitchAnimationImpl.this)[1].setScaleY(0.6F);
        CameraSwitchAnimationImpl.-get1(CameraSwitchAnimationImpl.this)[1].animate().scaleX(1.0F).scaleY(1.0F).rotationY(180.0F).setDuration(200L).withEndAction(new Runnable()
        {
          public void run()
          {
            Log.v(CameraSwitchAnimationImpl.-get0(CameraSwitchAnimationImpl.this), "startFlipperAnimation() - On second image animation end");
            CameraSwitchAnimationImpl.-wrap0(CameraSwitchAnimationImpl.this);
          }
        }).start();
      }
    }).start();
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
    }
    do
    {
      return;
    } while (!this.m_IsAnimationEnd);
    startFadeOutAnimation();
  }
  
  protected void onFirstPreviewFrameReceived()
  {
    Log.v(this.TAG, "onFirstPreviewFrameReceived()");
    this.m_IsFirstPreviewFrameReceived = true;
    if (this.m_IsAnimationEnd) {
      HandlerUtils.sendMessage(this, 100000, 0L);
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_Viewfinder = ((Viewfinder)localCameraActivity.findComponent(Viewfinder.class));
    if (this.m_Viewfinder == null) {
      Log.w(this.TAG, "onInitialize() - Cannot find Viewfinder component");
    }
    this.m_SwitchAnimationContainer = localCameraActivity.findViewById(2131361909);
    this.m_SwitchAnimationImageViews = new ImageView[] { (ImageView)this.m_SwitchAnimationContainer.findViewById(2131361911), (ImageView)this.m_SwitchAnimationContainer.findViewById(2131361912) };
  }
  
  protected void onLastPreviewImageCreated(Bitmap[] paramArrayOfBitmap)
  {
    if (get(PROP_ANIMATION_STATE) == OperationState.STOPPED)
    {
      Log.w(this.TAG, "onLastPreviewImageCreated() - Already stopped");
      return;
    }
    this.m_SwitchAnimationImageViews[0].setImageBitmap(paramArrayOfBitmap[0]);
    RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)this.m_SwitchAnimationImageViews[0].getLayoutParams();
    localLayoutParams.topMargin = Math.round(this.m_PreviewBounds.top);
    localLayoutParams.width = Math.round(this.m_PreviewBounds.width());
    localLayoutParams.height = Math.round(this.m_PreviewBounds.height());
    this.m_SwitchAnimationImageViews[1].setImageBitmap(paramArrayOfBitmap[1]);
    paramArrayOfBitmap = (RelativeLayout.LayoutParams)this.m_SwitchAnimationImageViews[1].getLayoutParams();
    paramArrayOfBitmap.topMargin = Math.round(this.m_PreviewBounds.top);
    paramArrayOfBitmap.width = Math.round(this.m_PreviewBounds.width());
    paramArrayOfBitmap.height = Math.round(this.m_PreviewBounds.height());
    this.m_SwitchAnimationContainer.setVisibility(0);
    this.m_SwitchAnimationContainer.setAlpha(1.0F);
    this.m_SwitchAnimationImageViews[0].setVisibility(0);
    this.m_SwitchAnimationImageViews[1].setVisibility(8);
    startFlipperAnimation();
    setReadOnly(PROP_ANIMATION_STATE, OperationState.STARTED);
  }
  
  protected void onReset()
  {
    Log.v(this.TAG, "onReset()");
    HandlerUtils.removeMessages(this, 100000);
    this.m_IsAnimationEnd = false;
    this.m_IsFirstPreviewFrameReceived = false;
    this.m_SwitchAnimationContainer.setVisibility(8);
  }
  
  protected void onStarted()
  {
    if (this.m_Viewfinder != null) {
      this.m_PreviewBounds = ((RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CameraSwitchAnimationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */