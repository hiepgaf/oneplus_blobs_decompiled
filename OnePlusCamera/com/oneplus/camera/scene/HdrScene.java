package com.oneplus.camera.scene;

import android.graphics.drawable.Drawable;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.CameraActivity;

public final class HdrScene
  extends PhotoScene
{
  private static final long DURATION_CAPTURE_DELAY = 50L;
  private static final long DURATION_CAPTURE_DELAY_LONG = 100L;
  public static final String ID = "HDR";
  private static final double THRESHOLD_ACC_VALUE_DIFF_TO_DELAY_CAPTURE = 0.07999999821186066D;
  private static final double THRESHOLD_ACC_VALUE_DIFF_TO_DELAY_CAPTURE_LONG = 0.15000000596046448D;
  private final float[] m_AccelerometerValues = new float[3];
  private final PropertyChangedCallback<float[]> m_AccelerometerValuesChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<float[]> paramAnonymousPropertyKey, PropertyChangeEventArgs<float[]> paramAnonymousPropertyChangeEventArgs)
    {
      paramAnonymousPropertySource = (float[])paramAnonymousPropertyChangeEventArgs.getNewValue();
      double d1 = paramAnonymousPropertySource[0] - HdrScene.-get0(HdrScene.this)[0];
      double d2 = paramAnonymousPropertySource[1] - HdrScene.-get0(HdrScene.this)[1];
      double d3 = paramAnonymousPropertySource[2] - HdrScene.-get0(HdrScene.this)[2];
      HdrScene.-get0(HdrScene.this)[0] = paramAnonymousPropertySource[0];
      HdrScene.-get0(HdrScene.this)[1] = paramAnonymousPropertySource[1];
      HdrScene.-get0(HdrScene.this)[2] = paramAnonymousPropertySource[2];
      d1 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
      if (d1 >= 0.15000000596046448D)
      {
        HdrScene.this.setCaptureDelayTime(100L);
        return;
      }
      if (d1 >= 0.07999999821186066D)
      {
        HdrScene.this.setCaptureDelayTime(50L);
        return;
      }
      HdrScene.this.setCaptureDelayTime(0L);
    }
  };
  
  HdrScene(CameraActivity paramCameraActivity)
  {
    super(paramCameraActivity, "HDR", 18, 19);
  }
  
  public String getDisplayName()
  {
    return getCameraActivity().getString(2131558510);
  }
  
  public Drawable getImage(Scene.ImageUsage paramImageUsage)
  {
    switch (-getcom-oneplus-camera-scene-Scene$ImageUsageSwitchesValues()[paramImageUsage.ordinal()])
    {
    default: 
      return null;
    case 1: 
      return getCameraActivity().getDrawable(2130837791);
    }
    return getCameraActivity().getDrawable(2130838234);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/HdrScene.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */