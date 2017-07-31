package com.oneplus.camera.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.os.Message;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.PathInterpolator;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.camera.BuildFlags;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.OPCameraActivity;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.VideoCaptureState;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.media.MediaType;

final class VideoHint
  extends UIComponent
{
  private static final int MESSAGE_HIDE_SCREEN_HINT = 1000;
  private CaptureModeManager m_CaptureMode;
  private boolean m_EnableHint = true;
  protected boolean m_HideVideoHintImage;
  private String m_Hint;
  private OnScreenHint m_OnScreenHint;
  private Rotation m_Rotation;
  private boolean m_Status;
  private MediaType m_Type;
  private VideoCaptureState m_VideoCaptureState;
  private View m_VideoHintImage;
  private Handle m_VideoOnScreenHint;
  
  VideoHint(CameraActivity paramCameraActivity)
  {
    super("Video Hint", paramCameraActivity, true);
  }
  
  private void updateVideoHint()
  {
    if ((this.m_EnableHint) && ((this.m_Rotation == Rotation.PORTRAIT) || (this.m_Rotation == Rotation.INVERSE_PORTRAIT)) && (this.m_Type == MediaType.VIDEO) && (this.m_Status) && (this.m_VideoCaptureState == VideoCaptureState.READY))
    {
      this.m_EnableHint = false;
      this.m_VideoHintImage.setVisibility(0);
      this.m_VideoHintImage.setAlpha(0.0F);
      this.m_VideoHintImage.setRotation(0.0F);
      this.m_VideoHintImage.animate().alpha(0.3F).setDuration(400L).setListener(new Animator.AnimatorListener()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator) {}
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          VideoHint.this.m_HideVideoHintImage = true;
          VideoHint.-get1(VideoHint.this).animate().rotation(-90.0F).setDuration(800L).setInterpolator(new PathInterpolator(0.6F, 0.0F, 0.4F, 1.0F)).setListener(new Animator.AnimatorListener()
          {
            public void onAnimationCancel(Animator paramAnonymous2Animator) {}
            
            public void onAnimationEnd(Animator paramAnonymous2Animator)
            {
              if (VideoHint.this.m_HideVideoHintImage)
              {
                VideoHint.this.m_HideVideoHintImage = false;
                VideoHint.-get1(VideoHint.this).animate().alpha(0.0F).setDuration(400L);
                return;
              }
              VideoHint.-get1(VideoHint.this).setVisibility(8);
            }
            
            public void onAnimationRepeat(Animator paramAnonymous2Animator) {}
            
            public void onAnimationStart(Animator paramAnonymous2Animator) {}
          });
        }
        
        public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
        
        public void onAnimationStart(Animator paramAnonymousAnimator) {}
      });
      if (BuildFlags.ROM_VERSION == 1)
      {
        if (!Handle.isValid(this.m_VideoOnScreenHint)) {
          break label150;
        }
        this.m_OnScreenHint.updateHint(this.m_VideoOnScreenHint, this.m_Hint, 0);
      }
      label150:
      do
      {
        return;
        if (this.m_OnScreenHint == null) {
          this.m_OnScreenHint = ((OnScreenHint)findComponent(OnScreenHint.class));
        }
      } while (this.m_OnScreenHint == null);
      this.m_VideoOnScreenHint = this.m_OnScreenHint.showHint(this.m_Hint, 1);
      HandlerUtils.sendMessage(this, 1000, 3000L);
      return;
    }
    HandlerUtils.removeMessages(this, 1000);
    this.m_VideoOnScreenHint = Handle.close(this.m_VideoOnScreenHint);
    this.m_VideoHintImage.animate().cancel();
    this.m_VideoHintImage.setVisibility(8);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    this.m_VideoOnScreenHint = Handle.close(this.m_VideoOnScreenHint);
  }
  
  protected void onDeinitialize()
  {
    this.m_VideoOnScreenHint = Handle.close(this.m_VideoOnScreenHint);
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_VideoHintImage = ((OPCameraActivity)localCameraActivity).getCaptureUIContainer().findViewById(2131362097);
    this.m_Type = ((MediaType)localCameraActivity.get(CameraActivity.PROP_MEDIA_TYPE));
    this.m_Status = ((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue();
    this.m_Rotation = getRotation();
    this.m_Hint = localCameraActivity.getString(2131558554);
    localCameraActivity.addCallback(CameraActivity.PROP_VIDEO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<VideoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<VideoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        VideoHint.-set3(VideoHint.this, (VideoCaptureState)paramAnonymousPropertyChangeEventArgs.getNewValue());
        VideoHint.-wrap0(VideoHint.this);
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_READY_TO_CAPTURE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        VideoHint.-set1(VideoHint.this, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
        VideoHint.-wrap0(VideoHint.this);
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_MEDIA_TYPE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<MediaType> paramAnonymousPropertyKey, PropertyChangeEventArgs<MediaType> paramAnonymousPropertyChangeEventArgs)
      {
        VideoHint.-set2(VideoHint.this, (MediaType)paramAnonymousPropertyChangeEventArgs.getNewValue());
        if ((paramAnonymousPropertyChangeEventArgs.getNewValue() == MediaType.VIDEO) && (VideoHint.-get0(VideoHint.this))) {
          VideoHint.-wrap0(VideoHint.this);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.RESUMING)
        {
          VideoHint.-set0(VideoHint.this, true);
          VideoHint.-wrap0(VideoHint.this);
        }
      }
    });
    this.m_CaptureMode = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    this.m_CaptureMode.addCallback(CaptureModeManager.PROP_CAPTURE_MODE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CaptureMode> paramAnonymousPropertyKey, PropertyChangeEventArgs<CaptureMode> paramAnonymousPropertyChangeEventArgs)
      {
        VideoHint.-wrap0(VideoHint.this);
      }
    });
  }
  
  protected void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    super.onRotationChanged(paramRotation1, paramRotation2);
    this.m_Rotation = paramRotation2;
    updateVideoHint();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/VideoHint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */