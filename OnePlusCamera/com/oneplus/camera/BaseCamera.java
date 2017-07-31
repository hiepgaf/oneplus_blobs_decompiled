package com.oneplus.camera;

import android.content.Context;
import android.graphics.Rect;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import android.util.SizeF;
import com.oneplus.base.HandlerBaseObject;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import com.oneplus.util.AspectRatio;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseCamera
  extends HandlerBaseObject
  implements Camera
{
  private Context m_Context;
  private final Camera.HardwareLevel m_HardwareLevel;
  private final Camera.LensFacing m_LensFacing;
  private List<Integer> m_SceneModes;
  private final int m_SensorOrientation;
  private final SizeF m_SensorPhysicalSize;
  private final Size m_SensorPixelSize;
  private final Size m_SensorPixelSizeFull;
  
  public BaseCamera(Context paramContext, CameraInfo paramCameraInfo)
  {
    super(true);
    this.m_Context = paramContext;
    int i;
    Object localObject;
    int j;
    switch (((Integer)paramCameraInfo.get(CameraInfo.PROP_LENS_FACING)).intValue())
    {
    default: 
      throw new RuntimeException("Unknown lens facing : " + paramCameraInfo.get(CameraInfo.PROP_LENS_FACING));
    case 1: 
      this.m_LensFacing = Camera.LensFacing.BACK;
      i = 0;
      paramContext = (List)paramCameraInfo.get(CameraInfo.PROP_CAPABILITIES);
      if (paramContext.contains(Integer.valueOf(1)))
      {
        i = 1;
        setReadOnly(PROP_IS_MANUAL_CONTROL_SUPPORTED, Boolean.valueOf(true));
      }
      if (paramContext.contains(Integer.valueOf(3))) {
        setReadOnly(PROP_IS_RAW_CAPTURE_SUPPORTED, Boolean.valueOf(true));
      }
      setReadOnly(PROP_IS_BOKEH_SUPPORTED, (Boolean)paramCameraInfo.get(CameraInfo.PROP_IS_BOKEH_SUPPORTED));
      setReadOnly(PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED, (Boolean)paramCameraInfo.get(CameraInfo.PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED));
      setReadOnly(PROP_IS_BURST_CAPTURE_SUPPORTED, (Boolean)paramCameraInfo.get(CameraInfo.PROP_IS_BURST_CAPTURE_SUPPORTED));
      paramContext = (Rect)paramCameraInfo.get(CameraInfo.PROP_SENSOR_RECT);
      this.m_SensorPixelSize = new Size(paramContext.width(), paramContext.height());
      this.m_SensorPixelSizeFull = ((Size)paramCameraInfo.get(CameraInfo.PROP_SENSOR_PIXEL_SIZE_FULL));
      this.m_SensorPhysicalSize = ((SizeF)paramCameraInfo.get(CameraInfo.PROP_SENSOR_PHYSICAL_SIZE));
      paramContext = (List)paramCameraInfo.get(CameraInfo.PROP_SURFACE_SIZES);
      setReadOnly(PROP_PREVIEW_SIZES, paramContext);
      setReadOnly(PROP_PICTURE_SIZES, (List)paramCameraInfo.get(CameraInfo.PROP_PICTURE_SIZES));
      setReadOnly(PROP_VIDEO_SIZES, (List)paramCameraInfo.get(CameraInfo.PROP_VIDEO_SIZES));
      setReadOnly(PROP_VIDEO_60FPS_SIZES, (List)paramCameraInfo.get(CameraInfo.PROP_VIDEO_60FPS_SIZES));
      this.m_SensorOrientation = ((Integer)paramCameraInfo.get(CameraInfo.PROP_SENSOR_ORIENTATION)).intValue();
      setReadOnly(PROP_HAS_FLASH, (Boolean)paramCameraInfo.get(CameraInfo.PROP_FLASH_AVAILABLE));
      setReadOnly(PROP_FLASH_MODES, (List)paramCameraInfo.get(CameraInfo.PROP_FLASH_MODES));
      setReadOnly(PROP_MAX_AE_REGION_COUNT, (Integer)paramCameraInfo.get(CameraInfo.PROP_MAX_AE_COUNT));
      setReadOnly(PROP_MAX_AF_REGION_COUNT, (Integer)paramCameraInfo.get(CameraInfo.PROP_MAX_AF_COUNT));
      paramContext = (List)paramCameraInfo.get(CameraInfo.PROP_AF_MODES);
      localObject = new ArrayList();
      j = paramContext.size() - 1;
      if (j >= 0) {
        switch (((Integer)paramContext.get(j)).intValue())
        {
        }
      }
      break;
    case -1: 
    case -2: 
    case 0: 
      for (;;)
      {
        label464:
        j -= 1;
        break label464;
        this.m_LensFacing = Camera.LensFacing.BACK_WIDE;
        break;
        this.m_LensFacing = Camera.LensFacing.BACK_TELE;
        break;
        this.m_LensFacing = Camera.LensFacing.FRONT;
        break;
        ((List)localObject).add(FocusMode.NORMAL_AF);
        continue;
        if (!((List)localObject).contains(FocusMode.CONTINUOUS_AF))
        {
          ((List)localObject).add(FocusMode.CONTINUOUS_AF);
          continue;
          if (i != 0) {
            ((List)localObject).add(FocusMode.MANUAL);
          }
        }
      }
    }
    ((List)localObject).add(FocusMode.DISABLED);
    setReadOnly(PROP_FOCUS_MODES, Collections.unmodifiableList((List)localObject));
    this.m_SceneModes = ((List)paramCameraInfo.get(CameraInfo.PROP_SCENE_MODES));
    setReadOnly(PROP_SCENE_MODES, this.m_SceneModes);
    float f = ((Rational)paramCameraInfo.get(CameraInfo.PROP_EV_STEP)).floatValue();
    paramContext = (Range)paramCameraInfo.get(CameraInfo.PROP_EXPOSURE_COMP_RANGE);
    setReadOnly(PROP_EXPOSURE_COMPENSATION_RANGE, new Range(Float.valueOf(((Integer)paramContext.getLower()).intValue() * f), Float.valueOf(((Integer)paramContext.getUpper()).intValue() * f)));
    setReadOnly(PROP_EXPOSURE_COMPENSATION_STEP, Float.valueOf(f));
    f = ((Float)paramCameraInfo.get(CameraInfo.PROP_MAX_DIGITAL_ZOOM)).floatValue();
    setReadOnly(PROP_MAX_DIGITAL_ZOOM, Float.valueOf(f));
    switch (((Integer)paramCameraInfo.get(CameraInfo.PROP_HARDWARE_LEVEL)).intValue())
    {
    default: 
      this.m_HardwareLevel = Camera.HardwareLevel.LEGACY;
      setReadOnly(PROP_HARDWARE_LEVEL, this.m_HardwareLevel);
      Log.v(this.TAG, "BaseCamera() - Camera hardwareLevel: ", this.m_HardwareLevel);
      paramContext = (Range)paramCameraInfo.get(CameraInfo.PROP_FOCUS_RANGE);
      setReadOnly(PROP_FOCUS_RANGE, paramContext);
      Log.v(this.TAG, "BaseCamera() - Focus range: ", paramContext);
      paramContext = (Range)paramCameraInfo.get(CameraInfo.PROP_EXPOSURE_TIME_RANGE);
      setReadOnly(PROP_EXPOSURE_TIME_NANOS_RANGE, paramContext);
      Log.v(this.TAG, "BaseCamera() - Exposure time range: ", paramContext);
      paramContext = (List)paramCameraInfo.get(CameraInfo.PROP_AWB_MODES);
      setReadOnly(PROP_AWB_MODES, paramContext);
      Log.v(this.TAG, "BaseCamera() - AWB modes: ", paramContext);
      if (paramContext != null) {
        setReadOnly(PROP_IS_COLOR_TEMPERATURE_SUPPORTED, Boolean.valueOf(paramContext.contains(Integer.valueOf(101))));
      }
      paramContext = (Range)paramCameraInfo.get(CameraInfo.PROP_ISO_RANGE);
      setReadOnly(PROP_ISO_RANGE, paramContext);
      Log.v(this.TAG, "BaseCamera() - ISO range: ", paramContext);
      setReadOnly(PROP_IS_MIRROR_SUPPORTED, (Boolean)paramCameraInfo.get(CameraInfo.PROP_IS_MIRROR_SUPPORTED));
      setReadOnly(PROP_IS_HIGH_VIDEO_FRAME_RATE_SUPPORTED, (Boolean)paramCameraInfo.get(CameraInfo.PROP_IS_HIGH_VIDEO_FRAME_RATE_SUPPORTED));
      boolean bool = ((Boolean)paramCameraInfo.get(CameraInfo.PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED)).booleanValue();
      setReadOnly(PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED, Boolean.valueOf(bool));
      setReadOnly(PROP_FACE_BEAUTY_VALUE_LIST, (List)paramCameraInfo.get(CameraInfo.PROP_FACE_BEAUTY_VALUE_LIST));
      set(PROP_FACE_BEAUTY_VALUE, (Integer)paramCameraInfo.get(CameraInfo.PROP_FACE_BEAUTY_VALUE));
      setReadOnly(PROP_FACE_BEAUTY_DEFAULT_VALUE, (Integer)paramCameraInfo.get(CameraInfo.PROP_FACE_BEAUTY_DEFAULT_VALUE));
      localObject = this.TAG;
      if (!bool)
      {
        paramContext = "not ";
        label1128:
        Log.v((String)localObject, "BaseCamera() - Face beauty is ", paramContext, "standalone");
        Log.v(this.TAG, "BaseCamera() - Face beauty value list: ", paramCameraInfo.get(CameraInfo.PROP_FACE_BEAUTY_VALUE_LIST));
        Log.v(this.TAG, "BaseCamera() - Face beauty value: ", paramCameraInfo.get(CameraInfo.PROP_FACE_BEAUTY_VALUE));
        bool = ((Boolean)paramCameraInfo.get(CameraInfo.PROP_IS_WATERMARK_SUPPORTED)).booleanValue();
        setReadOnly(PROP_IS_WATERMARK_SUPPORTED, Boolean.valueOf(bool));
        paramCameraInfo = this.TAG;
        if (bool) {
          break label1311;
        }
      }
      break;
    }
    label1311:
    for (paramContext = " not ";; paramContext = " ")
    {
      Log.v(paramCameraInfo, "BaseCamera() - Watermark is", paramContext, "supported");
      enablePropertyLogs(PROP_AE_STATE, 1);
      enablePropertyLogs(PROP_CAPTURE_STATE, 1);
      enablePropertyLogs(PROP_FOCUS_MODE, 1);
      enablePropertyLogs(PROP_FOCUS_STATE, 1);
      enablePropertyLogs(PROP_IS_SCREEN_FLASH_NEEDED, 1);
      enablePropertyLogs(PROP_PREVIEW_STATE, 1);
      enablePropertyLogs(PROP_STATE, 1);
      return;
      this.m_HardwareLevel = Camera.HardwareLevel.FULL;
      break;
      this.m_HardwareLevel = Camera.HardwareLevel.LIMITED;
      break;
      paramContext = "";
      break label1128;
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_LENS_FACING) {
      return this.m_LensFacing;
    }
    if (paramPropertyKey == PROP_SENSOR_ORIENTATION) {
      return Integer.valueOf(this.m_SensorOrientation);
    }
    if (paramPropertyKey == PROP_SENSOR_PHYSICAL_SIZE) {
      return this.m_SensorPhysicalSize;
    }
    if (paramPropertyKey == PROP_SENSOR_RATIO) {
      return AspectRatio.get(this.m_SensorPixelSize);
    }
    if (paramPropertyKey == PROP_SENSOR_SIZE) {
      return this.m_SensorPixelSize;
    }
    if (paramPropertyKey == PROP_SENSOR_SIZE_FULL) {
      return this.m_SensorPixelSizeFull;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  public Context getContext()
  {
    return this.m_Context;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/BaseCamera.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */