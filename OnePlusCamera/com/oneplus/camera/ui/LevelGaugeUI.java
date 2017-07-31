package com.oneplus.camera.ui;

import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.OrientationManager;
import com.oneplus.base.OrientationManager.Callback;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.Settings;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.PhotoCaptureState;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.manual.ManualCaptureMode;

public class LevelGaugeUI
  extends UIComponent
{
  private static final long ANIMATION_DURATION = 30L;
  private static final int COLOR_HORIZON;
  private static final int MSG_UPDATE_ROTATION = 10000;
  public static final PropertyKey<Boolean> PROP_IS_VISIBLE = new PropertyKey("IsLevelGaugeUIVisible", Boolean.class, LevelGaugeUI.class, Boolean.valueOf(false));
  public static final String SETTINGS_KEY_REFERENCE_LINE = "HorizontalReferenceLine";
  private static final int TOLERANCE = 1;
  private static final int TOLERANCE_LEAVING = 2;
  private CaptureModeManager m_CaptureModeManager;
  private boolean m_IsAnimating;
  private boolean m_IsHorizontal;
  private View m_LevelGaugeBar;
  private RelativeLayout m_LevelGaugeContainer;
  private int m_Orientation;
  private OrientationManager.Callback m_OrientationCallback = new OrientationManager.Callback()
  {
    public void onOrientationChanged(int paramAnonymousInt)
    {
      LevelGaugeUI.-wrap0(LevelGaugeUI.this, paramAnonymousInt);
    }
    
    public void onRotationChanged(Rotation paramAnonymousRotation1, Rotation paramAnonymousRotation2) {}
  };
  private Handle m_OrientationCallbackHandle;
  private Handle m_OrientationManagerHandle;
  
  static
  {
    COLOR_HORIZON = Color.argb(255, 104, 184, 64);
    Settings.setGlobalDefaultValue("HorizontalReferenceLine", Boolean.valueOf(true));
  }
  
  public LevelGaugeUI(CameraActivity paramCameraActivity)
  {
    super("Level Gauge UI", paramCameraActivity, true);
  }
  
  private void onOrientationChanged(int paramInt)
  {
    if (paramInt < 0) {
      return;
    }
    this.m_Orientation = paramInt;
    if (!this.m_IsAnimating) {
      this.m_IsAnimating = HandlerUtils.sendMessage(this, 10000, 30L);
    }
  }
  
  private void updateVisibility()
  {
    setReadOnly(PROP_IS_VISIBLE, Boolean.valueOf(getSettings().getBoolean("HorizontalReferenceLine")));
    if (this.m_CaptureModeManager == null) {
      return;
    }
    int i;
    RelativeLayout localRelativeLayout;
    if ((PhotoCaptureState)getCameraActivity().get(CameraActivity.PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.READY)
    {
      i = 1;
      if ((((CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE) instanceof ManualCaptureMode)) && (i != 0) && (((Boolean)get(PROP_IS_VISIBLE)).booleanValue()) && (!((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_SIMPLE_UI_MODE_ENTERED)).booleanValue())) {
        break label177;
      }
      Handle.close(this.m_OrientationManagerHandle);
      Handle.close(this.m_OrientationCallbackHandle);
      i = 0;
      if (((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_SIMPLE_UI_MODE_ENTERED)).booleanValue()) {
        i = 1;
      }
      localRelativeLayout = this.m_LevelGaugeContainer;
      if (i == 0) {
        break label213;
      }
    }
    label177:
    label213:
    for (long l = 300L;; l = 0L)
    {
      setViewVisibility(localRelativeLayout, false, l, null);
      return;
      i = 0;
      break;
      this.m_OrientationManagerHandle = OrientationManager.startOrientationSensor(getCameraActivity());
      this.m_OrientationCallbackHandle = OrientationManager.setCallback(this.m_OrientationCallback, getHandler());
      setViewVisibility(this.m_LevelGaugeContainer, true);
      return;
    }
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
      this.m_IsAnimating = false;
    } while (this.m_LevelGaugeBar == null);
    int m = getRotation().getDeviceOrientation();
    int k = (int)this.m_LevelGaugeBar.getRotation();
    int i = Math.abs(m - this.m_Orientation);
    int j = i;
    if (i > 180) {
      j = 360 - i;
    }
    i = k;
    if (k < 0) {
      i = k + 360;
    }
    if ((j <= 1) || ((this.m_IsHorizontal) && (j <= 2)))
    {
      this.m_Orientation = m;
      this.m_LevelGaugeBar.setBackgroundColor(COLOR_HORIZON);
      this.m_IsHorizontal = true;
      m = -this.m_Orientation + 360;
      j = i;
      k = m;
      if (i != m)
      {
        j = i;
        k = m;
        if (Math.abs(m - i) > 180)
        {
          if (m <= i) {
            break label237;
          }
          k = m - 360;
          j = i;
        }
      }
    }
    for (;;)
    {
      if (Math.abs(j - k) >= 1) {
        break label250;
      }
      this.m_LevelGaugeBar.setRotation(k);
      return;
      this.m_LevelGaugeBar.setBackgroundResource(2131230831);
      this.m_IsHorizontal = false;
      break;
      label237:
      j = i - 360;
      k = m;
    }
    label250:
    this.m_LevelGaugeBar.setRotation((j + k) / 2);
    this.m_IsAnimating = HandlerUtils.sendMessage(this, 10000, 30L);
  }
  
  protected void onDeinitialize()
  {
    super.onDeinitialize();
    Handle.close(this.m_OrientationManagerHandle);
    Handle.close(this.m_OrientationCallbackHandle);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    if (this.m_CaptureModeManager != null) {
      this.m_CaptureModeManager.addCallback(CaptureModeManager.PROP_CAPTURE_MODE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CaptureMode> paramAnonymousPropertyKey, PropertyChangeEventArgs<CaptureMode> paramAnonymousPropertyChangeEventArgs)
        {
          LevelGaugeUI.-wrap1(LevelGaugeUI.this);
        }
      });
    }
    this.m_LevelGaugeContainer = ((RelativeLayout)localCameraActivity.findViewById(2131361987));
    this.m_LevelGaugeBar = this.m_LevelGaugeContainer.findViewById(2131361989);
    localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.RESUMING) {
          LevelGaugeUI.-wrap1(LevelGaugeUI.this);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_PHOTO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PhotoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<PhotoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        LevelGaugeUI.-wrap1(LevelGaugeUI.this);
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_SIMPLE_UI_MODE_ENTERED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        LevelGaugeUI.-wrap1(LevelGaugeUI.this);
      }
    });
    updateVisibility();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/LevelGaugeUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */