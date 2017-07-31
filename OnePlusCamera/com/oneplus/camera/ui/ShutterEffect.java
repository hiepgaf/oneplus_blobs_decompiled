package com.oneplus.camera.ui;

import android.view.View;
import android.view.ViewPropertyAnimator;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CaptureEventArgs;
import com.oneplus.camera.PhotoCaptureState;
import com.oneplus.camera.UIComponent;

final class ShutterEffect
  extends UIComponent
{
  private View m_EffectFrame;
  
  ShutterEffect(CameraActivity paramCameraActivity)
  {
    super("Shutter Effect", paramCameraActivity, false);
  }
  
  private void onShutter()
  {
    this.m_EffectFrame.setAlpha(1.0F);
    this.m_EffectFrame.animate().alpha(0.0F).setDuration(200L).start();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_EffectFrame = localCameraActivity.findViewById(2131361953);
    this.m_EffectFrame.setAlpha(0.0F);
    this.m_EffectFrame.setVisibility(0);
    localCameraActivity.addHandler(CameraActivity.EVENT_SHUTTER, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
      {
        if ((ShutterEffect.this.getCameraActivity().get(CameraActivity.PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.CAPTURING) && (!((Boolean)ShutterEffect.this.getCameraActivity().get(CameraActivity.PROP_IS_BURST_PHOTO_ON_CAPTURE)).booleanValue())) {
          ShutterEffect.-wrap0(ShutterEffect.this);
        }
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ShutterEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */