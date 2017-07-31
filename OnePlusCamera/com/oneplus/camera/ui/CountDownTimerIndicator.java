package com.oneplus.camera.ui;

import android.graphics.RectF;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewStub;
import android.widget.TextView;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CountDownTimer;
import com.oneplus.camera.FlashController;
import com.oneplus.camera.OPCameraActivity;
import com.oneplus.camera.SmileCaptureController;
import com.oneplus.camera.UIComponent;

final class CountDownTimerIndicator
  extends UIComponent
{
  private static final long DURATION_TORCH_TIME_MILLIS = 500L;
  private static final long DURATION_TORCH_TIME_MILLIS_SHORT = 250L;
  private static final int MSG_TORCH_FLASHLIGHT = 10001;
  private View m_Container;
  private CountDownTimer m_CountDownTimer;
  private FlashController m_FlashController;
  private SmileCaptureController m_SmileCaptureController;
  private TextView m_TimerTextView;
  private Handle m_TorchFlashHandle;
  private Viewfinder m_Viewfinder;
  
  CountDownTimerIndicator(CameraActivity paramCameraActivity)
  {
    super("Count-down Timer Indicator", paramCameraActivity, true);
  }
  
  private void onCountDownTimerChanged(long paramLong)
  {
    if (!setupUI()) {
      return;
    }
    if (paramLong > 0L)
    {
      this.m_TimerTextView.setText(Long.toString(paramLong));
      this.m_TimerTextView.setVisibility(4);
      setViewVisibility(this.m_TimerTextView, true, 400L, null);
      if ((this.m_SmileCaptureController != null) && (((Boolean)this.m_SmileCaptureController.get(SmileCaptureController.PROP_IS_SMILE_CAPTURING)).booleanValue()))
      {
        Log.v(this.TAG, "onCountDownTimerChanged() - is smile capturing, don't flash.");
        return;
      }
      if (((Boolean)this.m_CountDownTimer.get(CountDownTimer.PROP_IS_COUNT_DOWN_FLASHLIGHT_ENABLED)).booleanValue())
      {
        if (paramLong <= 3L) {
          break label121;
        }
        torchFlashlight(500L);
      }
      label121:
      while ((paramLong != 2L) && (paramLong != 3L)) {
        return;
      }
      torchFlashlight(250L);
      HandlerUtils.sendMessage(this, 10001, 0, 0, Long.valueOf(250L), 500L);
      return;
    }
    this.m_TimerTextView.setText(null);
  }
  
  private void onCountDownTimerStarted()
  {
    if (!setupUI()) {
      return;
    }
    setViewVisibility(this.m_Container, true, 0L, null);
  }
  
  private void onCountDownTimerStopped()
  {
    this.m_TorchFlashHandle = Handle.close(this.m_TorchFlashHandle);
    HandlerUtils.removeMessages(this, 10001);
    if (this.m_TimerTextView != null) {
      this.m_TimerTextView.setText(null);
    }
    if (this.m_Container != null) {
      this.m_Container.setVisibility(8);
    }
  }
  
  private boolean setupUI()
  {
    if (this.m_TimerTextView != null) {
      return true;
    }
    this.m_Container = ((ViewStub)((OPCameraActivity)getCameraActivity()).findViewById(2131361955)).inflate();
    this.m_TimerTextView = ((TextView)this.m_Container.findViewById(2131361962));
    updateContainerBounds();
    rotateView(this.m_TimerTextView, getRotation(), 0L);
    return true;
  }
  
  private void torchFlashlight(long paramLong)
  {
    if (this.m_FlashController == null) {
      this.m_FlashController = ((FlashController)findComponent(FlashController.class));
    }
    if (this.m_FlashController != null)
    {
      Handle.close(this.m_TorchFlashHandle);
      this.m_TorchFlashHandle = this.m_FlashController.torchFlash(paramLong);
    }
  }
  
  private void updateContainerBounds()
  {
    if ((this.m_Container == null) || (this.m_Viewfinder == null)) {
      return;
    }
    RectF localRectF = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.m_Container.getLayoutParams();
    localMarginLayoutParams.width = ((int)(localRectF.width() + 0.5F));
    localMarginLayoutParams.height = ((int)(localRectF.height() + 0.5F));
    localMarginLayoutParams.setMarginStart((int)(localRectF.left + 0.5F));
    localMarginLayoutParams.topMargin = ((int)(localRectF.top + 0.5F));
    this.m_Container.requestLayout();
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    torchFlashlight(((Long)paramMessage.obj).longValue());
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_CountDownTimer = ((CountDownTimer)findComponent(CountDownTimer.class));
    this.m_SmileCaptureController = ((SmileCaptureController)findComponent(SmileCaptureController.class));
    this.m_Viewfinder = ((Viewfinder)findComponent(Viewfinder.class));
    if (this.m_CountDownTimer != null)
    {
      this.m_CountDownTimer.addCallback(CountDownTimer.PROP_IS_STARTED, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue())
          {
            CountDownTimerIndicator.-wrap1(CountDownTimerIndicator.this);
            return;
          }
          CountDownTimerIndicator.-wrap2(CountDownTimerIndicator.this);
        }
      });
      this.m_CountDownTimer.addCallback(CountDownTimer.PROP_REMAINING_SECONDS, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Long> paramAnonymousPropertyKey, PropertyChangeEventArgs<Long> paramAnonymousPropertyChangeEventArgs)
        {
          CountDownTimerIndicator.-wrap0(CountDownTimerIndicator.this, ((Long)paramAnonymousPropertyChangeEventArgs.getNewValue()).longValue());
        }
      });
    }
    if (this.m_Viewfinder != null) {
      this.m_Viewfinder.addCallback(Viewfinder.PROP_PREVIEW_BOUNDS, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<RectF> paramAnonymousPropertyKey, PropertyChangeEventArgs<RectF> paramAnonymousPropertyChangeEventArgs)
        {
          CountDownTimerIndicator.-wrap3(CountDownTimerIndicator.this);
        }
      });
    }
  }
  
  protected void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    super.onRotationChanged(paramRotation1, paramRotation2);
    rotateView(this.m_TimerTextView, paramRotation2, 0L);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CountDownTimerIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */