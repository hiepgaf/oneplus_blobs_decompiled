package com.oneplus.camera.scene;

import android.graphics.drawable.Drawable;
import android.os.Message;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.State;
import com.oneplus.camera.CameraActivity;
import java.util.HashSet;
import java.util.Set;

public final class AutoHdrScene
  extends PhotoScene
{
  private static final long DURATION_CAPTURE_DELAY = 50L;
  private static final long DURATION_CAPTURE_DELAY_LONG = 100L;
  public static final String ID = "Auto-HDR";
  public static final int MSG_AUTO_HDR_STATUS_UPDATED = 10001;
  public static final PropertyKey<Boolean> PROP_IS_HDR_ACTIVE = new PropertyKey("IsHdrActive", Boolean.class, AutoHdrScene.class, Boolean.valueOf(false));
  private static final double THRESHOLD_ACC_VALUE_DIFF_TO_DELAY_CAPTURE = 0.07999999821186066D;
  private static final double THRESHOLD_ACC_VALUE_DIFF_TO_DELAY_CAPTURE_LONG = 0.15000000596046448D;
  private final float[] m_AccelerometerValues = new float[3];
  private final PropertyChangedCallback<float[]> m_AccelerometerValuesChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<float[]> paramAnonymousPropertyKey, PropertyChangeEventArgs<float[]> paramAnonymousPropertyChangeEventArgs)
    {
      paramAnonymousPropertySource = (float[])paramAnonymousPropertyChangeEventArgs.getNewValue();
      double d1 = paramAnonymousPropertySource[0] - AutoHdrScene.-get1(AutoHdrScene.this)[0];
      double d2 = paramAnonymousPropertySource[1] - AutoHdrScene.-get1(AutoHdrScene.this)[1];
      double d3 = paramAnonymousPropertySource[2] - AutoHdrScene.-get1(AutoHdrScene.this)[2];
      AutoHdrScene.-get1(AutoHdrScene.this)[0] = paramAnonymousPropertySource[0];
      AutoHdrScene.-get1(AutoHdrScene.this)[1] = paramAnonymousPropertySource[1];
      AutoHdrScene.-get1(AutoHdrScene.this)[2] = paramAnonymousPropertySource[2];
      if (((Boolean)AutoHdrScene.this.get(AutoHdrScene.PROP_IS_HDR_ACTIVE)).booleanValue())
      {
        d1 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
        if (d1 >= 0.15000000596046448D)
        {
          AutoHdrScene.this.setCaptureDelayTime(100L);
          return;
        }
        if (d1 >= 0.07999999821186066D)
        {
          AutoHdrScene.this.setCaptureDelayTime(50L);
          return;
        }
        AutoHdrScene.this.setCaptureDelayTime(0L);
        return;
      }
      AutoHdrScene.this.setCaptureDelayTime(0L);
    }
  };
  private final Set<Camera> m_BoundCameras = new HashSet();
  
  AutoHdrScene(CameraActivity paramCameraActivity)
  {
    super(paramCameraActivity, "Auto-HDR", 10001, 18);
  }
  
  public String getDisplayName()
  {
    return getCameraActivity().getString(2131558508);
  }
  
  public Drawable getImage(Scene.ImageUsage paramImageUsage)
  {
    switch (-getcom-oneplus-camera-scene-Scene$ImageUsageSwitchesValues()[paramImageUsage.ordinal()])
    {
    default: 
      return null;
    case 1: 
      return getCameraActivity().getDrawable(2130837782);
    }
    if (((Boolean)get(PROP_IS_HDR_ACTIVE)).booleanValue()) {
      return getCameraActivity().getDrawable(2130838234);
    }
    return null;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    if (((Boolean)paramMessage.obj).booleanValue())
    {
      setReadOnly(PROP_IS_HDR_ACTIVE, Boolean.valueOf(true));
      return;
    }
    setCaptureDelayTime(0L);
    setReadOnly(PROP_IS_HDR_ACTIVE, Boolean.valueOf(false));
  }
  
  public boolean needToShowProcessingDialog()
  {
    return ((Boolean)get(PROP_IS_HDR_ACTIVE)).booleanValue();
  }
  
  protected void onCameraChanged(final Camera paramCamera)
  {
    if (paramCamera == null)
    {
      disable();
      super.onCameraChanged(paramCamera);
      return;
    }
    if (!this.m_BoundCameras.contains(paramCamera))
    {
      this.m_BoundCameras.add(paramCamera);
      HandlerUtils.post(paramCamera, new Runnable()
      {
        public void run()
        {
          paramCamera.addCallback(Camera.PROP_AUTO_HDR_STATUS, new PropertyChangedCallback()
          {
            public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey<Boolean> paramAnonymous2PropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymous2PropertyChangeEventArgs)
            {
              Log.v(AutoHdrScene.-get0(AutoHdrScene.this), "onPropertyChanged() - PROP_AUTO_HDR_STATUS : " + paramAnonymous2PropertyChangeEventArgs.getNewValue());
              HandlerUtils.sendMessage(AutoHdrScene.this, 10001, 0, 0, paramAnonymous2PropertyChangeEventArgs.getNewValue());
            }
          });
          HandlerUtils.sendMessage(AutoHdrScene.this, 10001, 0, 0, paramCamera.get(Camera.PROP_AUTO_HDR_STATUS));
          paramCamera.addCallback(Camera.PROP_STATE, new PropertyChangedCallback()
          {
            public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey<Camera.State> paramAnonymous2PropertyKey, PropertyChangeEventArgs<Camera.State> paramAnonymous2PropertyChangeEventArgs)
            {
              if (paramAnonymous2PropertyChangeEventArgs.getNewValue() == Camera.State.CLOSED) {
                HandlerUtils.sendMessage(AutoHdrScene.this, 10001, 0, 0, Boolean.valueOf(false));
              }
            }
          });
        }
      });
    }
    super.onCameraChanged(paramCamera);
  }
  
  protected boolean onEnter(Scene paramScene, int paramInt)
  {
    if (!super.onEnter(paramScene, paramInt)) {
      return false;
    }
    getCameraActivity().addCallback(CameraActivity.PROP_ACCELEROMETER_VALUES, this.m_AccelerometerValuesChangedCB);
    setCaptureDelayTime(0L);
    return true;
  }
  
  protected void onExit(Scene paramScene, int paramInt)
  {
    getCameraActivity().removeCallback(CameraActivity.PROP_ACCELEROMETER_VALUES, this.m_AccelerometerValuesChangedCB);
    this.m_AccelerometerValues[0] = 0.0F;
    this.m_AccelerometerValues[1] = 0.0F;
    this.m_AccelerometerValues[2] = 0.0F;
    super.onExit(paramScene, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/AutoHdrScene.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */