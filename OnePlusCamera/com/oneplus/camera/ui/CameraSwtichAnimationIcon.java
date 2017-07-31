package com.oneplus.camera.ui;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;

final class CameraSwtichAnimationIcon
  extends UIComponent
{
  private ImageView m_AnimationIcon;
  private View m_AnimationIconContainer;
  private CaptureModeManager m_CaptureModeManager;
  private boolean m_IsTriggeredByCameraSwitching;
  private Camera.LensFacing m_OldLensFacing;
  private PreviewCover m_PreviewCover;
  PreviewCover.OnStateChangedListener m_PreviewCoverStateChangedListener = new PreviewCover.OnStateChangedListener()
  {
    public void onStateChanged(PreviewCover.UIState paramAnonymousUIState1, PreviewCover.UIState paramAnonymousUIState2)
    {
      if (CameraSwtichAnimationIcon.-get1(CameraSwtichAnimationIcon.this)) {
        CameraSwtichAnimationIcon.-wrap0(CameraSwtichAnimationIcon.this, paramAnonymousUIState1, paramAnonymousUIState2);
      }
    }
  };
  
  CameraSwtichAnimationIcon(CameraActivity paramCameraActivity)
  {
    super("CameraSwitchAnimationIcon", paramCameraActivity, true);
  }
  
  private void onPreviewCoverStateChanged(PreviewCover.UIState paramUIState1, PreviewCover.UIState paramUIState2)
  {
    switch (-getcom-oneplus-camera-ui-PreviewCover$UIStateSwitchesValues()[paramUIState2.ordinal()])
    {
    case 3: 
    default: 
      return;
    case 4: 
      this.m_AnimationIcon.setVisibility(0);
      if (this.m_OldLensFacing.equals(Camera.LensFacing.FRONT)) {
        this.m_AnimationIcon.setBackgroundResource(2130837662);
      }
      for (;;)
      {
        startIconAnimation(this.m_AnimationIcon);
        return;
        this.m_AnimationIcon.setBackgroundResource(2130837663);
      }
    case 2: 
      setViewVisibility(this.m_AnimationIcon, false, 200L, null);
      return;
    }
    setViewVisibility(this.m_AnimationIcon, false, 0L, null);
    this.m_IsTriggeredByCameraSwitching = false;
  }
  
  private AnimationDrawable startIconAnimation(final ImageView paramImageView)
  {
    if (paramImageView == null)
    {
      Log.w(this.TAG, "startIconAnimation() - View is null.");
      return null;
    }
    if (!(paramImageView.getBackground() instanceof AnimationDrawable)) {
      return null;
    }
    final AnimationDrawable localAnimationDrawable = (AnimationDrawable)paramImageView.getBackground();
    if (localAnimationDrawable.isRunning()) {
      localAnimationDrawable.stop();
    }
    localAnimationDrawable.start();
    localAnimationDrawable.setOneShot(true);
    int i = localAnimationDrawable.getNumberOfFrames();
    int j = localAnimationDrawable.getDuration(0);
    getHandler().postDelayed(new Runnable()
    {
      public void run()
      {
        localAnimationDrawable.stop();
        paramImageView.invalidate();
      }
    }, i * j);
    return localAnimationDrawable;
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    this.m_PreviewCover = ((PreviewCover)findComponent(PreviewCover.class));
    final CameraActivity localCameraActivity = getCameraActivity();
    this.m_AnimationIconContainer = localCameraActivity.findViewById(2131361833);
    if ((this.m_AnimationIconContainer instanceof ViewStub))
    {
      this.m_AnimationIconContainer = ((ViewStub)this.m_AnimationIconContainer).inflate();
      setViewVisibility(this.m_AnimationIconContainer, true, 0L, null);
      this.m_AnimationIcon = ((ImageView)this.m_AnimationIconContainer.findViewById(2131361913));
      addAutoRotateView(this.m_AnimationIcon);
    }
    localCameraActivity.addCallback(CameraActivity.PROP_IS_CAMERA_SWITCHING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue())
        {
          paramAnonymousPropertySource = (Camera)localCameraActivity.get(CameraActivity.PROP_CAMERA);
          if (paramAnonymousPropertySource != null)
          {
            CameraSwtichAnimationIcon.-set1(CameraSwtichAnimationIcon.this, (Camera.LensFacing)paramAnonymousPropertySource.get(Camera.PROP_LENS_FACING));
            CameraSwtichAnimationIcon.-set0(CameraSwtichAnimationIcon.this, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
          }
        }
        while ((CameraSwtichAnimationIcon.-get2(CameraSwtichAnimationIcon.this) == null) || (CameraSwtichAnimationIcon.-get2(CameraSwtichAnimationIcon.this).getPreviewCoverState(PreviewCover.Style.COLOR_BLACK) != PreviewCover.UIState.CLOSED)) {
          return;
        }
        CameraSwtichAnimationIcon.-set0(CameraSwtichAnimationIcon.this, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_RUNNING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue())
        {
          CameraSwtichAnimationIcon.-wrap1(CameraSwtichAnimationIcon.this, CameraSwtichAnimationIcon.-get0(CameraSwtichAnimationIcon.this), false, 0L, null);
          CameraSwtichAnimationIcon.-set0(CameraSwtichAnimationIcon.this, false);
        }
      }
    });
    if (this.m_CaptureModeManager != null) {
      this.m_CaptureModeManager.addCallback(CaptureModeManager.PROP_CAPTURE_MODE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CaptureMode> paramAnonymousPropertyKey, PropertyChangeEventArgs<CaptureMode> paramAnonymousPropertyChangeEventArgs)
        {
          if ((CameraSwtichAnimationIcon.-get2(CameraSwtichAnimationIcon.this) != null) && (CameraSwtichAnimationIcon.-get2(CameraSwtichAnimationIcon.this).getPreviewCoverState(PreviewCover.Style.COLOR_BLACK) == PreviewCover.UIState.OPENED)) {
            CameraSwtichAnimationIcon.-wrap1(CameraSwtichAnimationIcon.this, CameraSwtichAnimationIcon.-get0(CameraSwtichAnimationIcon.this), false, 0L, null);
          }
        }
      });
    }
    if (this.m_PreviewCover != null) {
      this.m_PreviewCover.addOnStateChangedListener(PreviewCover.Style.COLOR_BLACK, this.m_PreviewCoverStateChangedListener);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CameraSwtichAnimationIcon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */