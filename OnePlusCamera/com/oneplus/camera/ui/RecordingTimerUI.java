package com.oneplus.camera.ui;

import android.content.res.Resources;
import android.os.Message;
import android.view.ViewStub;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.VideoCaptureState;
import com.oneplus.camera.widget.RotateRelativeLayout;
import java.util.Locale;

final class RecordingTimerUI
  extends UIComponent
{
  private static final int MSG_SHOW_RECORDING_TIMER = 10000;
  private RotateRelativeLayout m_BaseContainer;
  private SecondLayerBar m_SecondLayerBar;
  private TextView m_TimerTextView;
  
  RecordingTimerUI(CameraActivity paramCameraActivity)
  {
    super("Recording Timer", paramCameraActivity, true);
  }
  
  private String getRecordingTimerString(long paramLong)
  {
    long l1 = paramLong / 3600L;
    paramLong -= 3600L * l1;
    long l2 = paramLong / 60L;
    return String.format(Locale.US, "%02d:%02d:%02d", new Object[] { Long.valueOf(l1), Long.valueOf(l2), Long.valueOf(paramLong - l2 * 60L) });
  }
  
  private void hideRecordingTimer()
  {
    setViewVisibility(this.m_BaseContainer, false);
  }
  
  private void showRecordingTimer()
  {
    if (this.m_BaseContainer == null) {
      return;
    }
    if ((this.m_SecondLayerBar != null) && (((Boolean)this.m_SecondLayerBar.get(SecondLayerBar.PROP_IS_VISIBLE)).booleanValue())) {
      return;
    }
    Rotation localRotation = getRotation();
    this.m_BaseContainer.setRotation(localRotation);
    Resources localResources;
    RelativeLayout.LayoutParams localLayoutParams;
    if (this.m_TimerTextView != null)
    {
      localResources = getCameraActivity().getResources();
      localLayoutParams = (RelativeLayout.LayoutParams)this.m_TimerTextView.getLayoutParams();
      localLayoutParams.removeRule(10);
      localLayoutParams.removeRule(12);
      switch (-getcom-oneplus-base-RotationSwitchesValues()[localRotation.ordinal()])
      {
      default: 
        localLayoutParams.addRule(10);
        localLayoutParams.topMargin = localResources.getDimensionPixelOffset(2131296513);
      }
    }
    for (;;)
    {
      this.m_TimerTextView.requestLayout();
      setViewVisibility(this.m_BaseContainer, true);
      return;
      localLayoutParams.addRule(10);
      localLayoutParams.topMargin = localResources.getDimensionPixelOffset(2131296512);
      continue;
      localLayoutParams.addRule(10);
      localLayoutParams.topMargin = localResources.getDimensionPixelOffset(2131296512);
      continue;
      localLayoutParams.addRule(12);
      localLayoutParams.bottomMargin = localResources.getDimensionPixelOffset(2131296511);
    }
  }
  
  private void updateRecordingTimer(long paramLong)
  {
    if (this.m_TimerTextView != null) {
      this.m_TimerTextView.setText(getRecordingTimerString(paramLong));
    }
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    showRecordingTimer();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_BaseContainer = ((RotateRelativeLayout)((ViewStub)localCameraActivity.findViewById(2131361830)).inflate());
    this.m_TimerTextView = ((TextView)this.m_BaseContainer.findViewById(2131362051));
    localCameraActivity.addCallback(CameraActivity.PROP_ELAPSED_RECORDING_SECONDS, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Long> paramAnonymousPropertyKey, PropertyChangeEventArgs<Long> paramAnonymousPropertyChangeEventArgs)
      {
        RecordingTimerUI.-wrap2(RecordingTimerUI.this, ((Long)paramAnonymousPropertyChangeEventArgs.getNewValue()).longValue());
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_VIDEO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<VideoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<VideoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
        {
        default: 
          return;
        case 1: 
          if (paramAnonymousPropertyChangeEventArgs.getOldValue() != VideoCaptureState.RESUMING) {
            RecordingTimerUI.-wrap2(RecordingTimerUI.this, 0L);
          }
          RecordingTimerUI.-wrap1(RecordingTimerUI.this);
          return;
        }
        HandlerUtils.removeMessages(RecordingTimerUI.this, 10000);
        RecordingTimerUI.-wrap0(RecordingTimerUI.this);
      }
    });
    findComponent(SecondLayerBar.class, new ComponentSearchCallback()
    {
      public void onComponentFound(SecondLayerBar paramAnonymousSecondLayerBar)
      {
        RecordingTimerUI.-set0(RecordingTimerUI.this, paramAnonymousSecondLayerBar);
        paramAnonymousSecondLayerBar.addCallback(SecondLayerBar.PROP_IS_VISIBLE, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey<Boolean> paramAnonymous2PropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymous2PropertyChangeEventArgs)
          {
            if (((Boolean)paramAnonymous2PropertyChangeEventArgs.getNewValue()).booleanValue())
            {
              RecordingTimerUI.-wrap0(RecordingTimerUI.this);
              return;
            }
            switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)RecordingTimerUI.this.getCameraActivity().get(CameraActivity.PROP_VIDEO_CAPTURE_STATE)).ordinal()])
            {
            default: 
              return;
            }
            RecordingTimerUI.-wrap1(RecordingTimerUI.this);
          }
        });
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/RecordingTimerUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */