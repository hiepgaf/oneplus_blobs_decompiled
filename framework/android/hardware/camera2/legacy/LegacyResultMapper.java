package android.hardware.camera2.legacy;

import android.graphics.Rect;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.utils.ParamsUtils;
import android.location.Location;
import android.util.Log;
import android.util.Size;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LegacyResultMapper
{
  private static final boolean DEBUG = false;
  private static final String TAG = "LegacyResultMapper";
  private LegacyRequest mCachedRequest = null;
  private CameraMetadataNative mCachedResult = null;
  
  private static int convertLegacyAfMode(String paramString)
  {
    if (paramString == null)
    {
      Log.w("LegacyResultMapper", "convertLegacyAfMode - no AF mode, default to OFF");
      return 0;
    }
    if (paramString.equals("auto")) {
      return 1;
    }
    if (paramString.equals("continuous-picture")) {
      return 4;
    }
    if (paramString.equals("continuous-video")) {
      return 3;
    }
    if (paramString.equals("edof")) {
      return 5;
    }
    if (paramString.equals("macro")) {
      return 2;
    }
    if (paramString.equals("fixed")) {
      return 0;
    }
    if (paramString.equals("infinity")) {
      return 0;
    }
    Log.w("LegacyResultMapper", "convertLegacyAfMode - unknown mode " + paramString + " , ignoring");
    return 0;
  }
  
  private static int convertLegacyAwbMode(String paramString)
  {
    if (paramString == null) {
      return 1;
    }
    if (paramString.equals("auto")) {
      return 1;
    }
    if (paramString.equals("incandescent")) {
      return 2;
    }
    if (paramString.equals("fluorescent")) {
      return 3;
    }
    if (paramString.equals("warm-fluorescent")) {
      return 4;
    }
    if (paramString.equals("daylight")) {
      return 5;
    }
    if (paramString.equals("cloudy-daylight")) {
      return 6;
    }
    if (paramString.equals("twilight")) {
      return 7;
    }
    if (paramString.equals("shade")) {
      return 8;
    }
    Log.w("LegacyResultMapper", "convertAwbMode - unrecognized WB mode " + paramString);
    return 1;
  }
  
  private static CameraMetadataNative convertResultMetadata(LegacyRequest paramLegacyRequest)
  {
    Object localObject1 = paramLegacyRequest.characteristics;
    CaptureRequest localCaptureRequest = paramLegacyRequest.captureRequest;
    Object localObject2 = paramLegacyRequest.previewSize;
    Camera.Parameters localParameters = paramLegacyRequest.parameters;
    paramLegacyRequest = new CameraMetadataNative();
    Object localObject3 = (Rect)((CameraCharacteristics)localObject1).get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
    localObject2 = ParameterUtils.convertScalerCropRegion((Rect)localObject3, (Rect)localCaptureRequest.get(CaptureRequest.SCALER_CROP_REGION), (Size)localObject2, localParameters);
    paramLegacyRequest.set(CaptureResult.COLOR_CORRECTION_ABERRATION_MODE, (Integer)localCaptureRequest.get(CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE));
    mapAe(paramLegacyRequest, (CameraCharacteristics)localObject1, localCaptureRequest, (Rect)localObject3, (ParameterUtils.ZoomData)localObject2, localParameters);
    mapAf(paramLegacyRequest, (Rect)localObject3, (ParameterUtils.ZoomData)localObject2, localParameters);
    mapAwb(paramLegacyRequest, localParameters);
    int i = LegacyRequestMapper.filterSupportedCaptureIntent(((Integer)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.CONTROL_CAPTURE_INTENT, Integer.valueOf(1))).intValue());
    paramLegacyRequest.set(CaptureResult.CONTROL_CAPTURE_INTENT, Integer.valueOf(i));
    if (((Integer)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.CONTROL_MODE, Integer.valueOf(1))).intValue() == 2)
    {
      paramLegacyRequest.set(CaptureResult.CONTROL_MODE, Integer.valueOf(2));
      localObject3 = localParameters.getSceneMode();
      i = LegacyMetadataMapper.convertSceneModeFromLegacy((String)localObject3);
      if (i == -1) {
        break label454;
      }
      paramLegacyRequest.set(CaptureResult.CONTROL_SCENE_MODE, Integer.valueOf(i));
      label201:
      localObject3 = localParameters.getColorEffect();
      i = LegacyMetadataMapper.convertEffectModeFromLegacy((String)localObject3);
      if (i == -1) {
        break label501;
      }
      paramLegacyRequest.set(CaptureResult.CONTROL_EFFECT_MODE, Integer.valueOf(i));
      label230:
      if ((!localParameters.isVideoStabilizationSupported()) || (!localParameters.getVideoStabilization())) {
        break label548;
      }
      i = 1;
      label248:
      paramLegacyRequest.set(CaptureResult.CONTROL_VIDEO_STABILIZATION_MODE, Integer.valueOf(i));
      if ("infinity".equals(localParameters.getFocusMode())) {
        paramLegacyRequest.set(CaptureResult.LENS_FOCUS_DISTANCE, Float.valueOf(0.0F));
      }
      paramLegacyRequest.set(CaptureResult.LENS_FOCAL_LENGTH, Float.valueOf(localParameters.getFocalLength()));
      paramLegacyRequest.set(CaptureResult.REQUEST_PIPELINE_DEPTH, (Byte)((CameraCharacteristics)localObject1).get(CameraCharacteristics.REQUEST_PIPELINE_MAX_DEPTH));
      mapScaler(paramLegacyRequest, (ParameterUtils.ZoomData)localObject2, localParameters);
      paramLegacyRequest.set(CaptureResult.SENSOR_TEST_PATTERN_MODE, Integer.valueOf(0));
      paramLegacyRequest.set(CaptureResult.JPEG_GPS_LOCATION, (Location)localCaptureRequest.get(CaptureRequest.JPEG_GPS_LOCATION));
      paramLegacyRequest.set(CaptureResult.JPEG_ORIENTATION, (Integer)localCaptureRequest.get(CaptureRequest.JPEG_ORIENTATION));
      paramLegacyRequest.set(CaptureResult.JPEG_QUALITY, Byte.valueOf((byte)localParameters.getJpegQuality()));
      paramLegacyRequest.set(CaptureResult.JPEG_THUMBNAIL_QUALITY, Byte.valueOf((byte)localParameters.getJpegThumbnailQuality()));
      localObject1 = localParameters.getJpegThumbnailSize();
      if (localObject1 == null) {
        break label553;
      }
      paramLegacyRequest.set(CaptureResult.JPEG_THUMBNAIL_SIZE, ParameterUtils.convertSize((Camera.Size)localObject1));
    }
    for (;;)
    {
      paramLegacyRequest.set(CaptureResult.NOISE_REDUCTION_MODE, (Integer)localCaptureRequest.get(CaptureRequest.NOISE_REDUCTION_MODE));
      return paramLegacyRequest;
      paramLegacyRequest.set(CaptureResult.CONTROL_MODE, Integer.valueOf(1));
      break;
      label454:
      Log.w("LegacyResultMapper", "Unknown scene mode " + (String)localObject3 + " returned by camera HAL, setting to disabled.");
      paramLegacyRequest.set(CaptureResult.CONTROL_SCENE_MODE, Integer.valueOf(0));
      break label201;
      label501:
      Log.w("LegacyResultMapper", "Unknown effect mode " + (String)localObject3 + " returned by camera HAL, setting to off.");
      paramLegacyRequest.set(CaptureResult.CONTROL_EFFECT_MODE, Integer.valueOf(0));
      break label230;
      label548:
      i = 0;
      break label248;
      label553:
      Log.w("LegacyResultMapper", "Null thumbnail size received from parameters.");
    }
  }
  
  private static MeteringRectangle[] getMeteringRectangles(Rect paramRect, ParameterUtils.ZoomData paramZoomData, List<Camera.Area> paramList, String paramString)
  {
    paramString = new ArrayList();
    if (paramList != null)
    {
      paramList = paramList.iterator();
      while (paramList.hasNext()) {
        paramString.add(ParameterUtils.convertCameraAreaToActiveArrayRectangle(paramRect, paramZoomData, (Camera.Area)paramList.next()).toMetering());
      }
    }
    return (MeteringRectangle[])paramString.toArray(new MeteringRectangle[0]);
  }
  
  private static void mapAe(CameraMetadataNative paramCameraMetadataNative, CameraCharacteristics paramCameraCharacteristics, CaptureRequest paramCaptureRequest, Rect paramRect, ParameterUtils.ZoomData paramZoomData, Camera.Parameters paramParameters)
  {
    int i = LegacyMetadataMapper.convertAntiBandingModeOrDefault(paramParameters.getAntibanding());
    paramCameraMetadataNative.set(CaptureResult.CONTROL_AE_ANTIBANDING_MODE, Integer.valueOf(i));
    paramCameraMetadataNative.set(CaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION, Integer.valueOf(paramParameters.getExposureCompensation()));
    if (paramParameters.isAutoExposureLockSupported()) {}
    for (boolean bool = paramParameters.getAutoExposureLock();; bool = false)
    {
      paramCameraMetadataNative.set(CaptureResult.CONTROL_AE_LOCK, Boolean.valueOf(bool));
      paramCaptureRequest = (Boolean)paramCaptureRequest.get(CaptureRequest.CONTROL_AE_LOCK);
      if ((paramCaptureRequest != null) && (paramCaptureRequest.booleanValue() != bool)) {
        Log.w("LegacyResultMapper", "mapAe - android.control.aeLock was requested to " + paramCaptureRequest + " but resulted in " + bool);
      }
      mapAeAndFlashMode(paramCameraMetadataNative, paramCameraCharacteristics, paramParameters);
      if (paramParameters.getMaxNumMeteringAreas() > 0)
      {
        paramCameraCharacteristics = getMeteringRectangles(paramRect, paramZoomData, paramParameters.getMeteringAreas(), "AE");
        paramCameraMetadataNative.set(CaptureResult.CONTROL_AE_REGIONS, paramCameraCharacteristics);
      }
      return;
    }
  }
  
  private static void mapAeAndFlashMode(CameraMetadataNative paramCameraMetadataNative, CameraCharacteristics paramCameraCharacteristics, Camera.Parameters paramParameters)
  {
    int k = 0;
    int m;
    String str;
    int i;
    int j;
    Object localObject;
    if (((Boolean)paramCameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)).booleanValue())
    {
      paramCameraCharacteristics = null;
      m = 1;
      str = paramParameters.getFlashMode();
      i = m;
      j = k;
      localObject = paramCameraCharacteristics;
      if (str != null)
      {
        if (!str.equals("off")) {
          break label107;
        }
        localObject = paramCameraCharacteristics;
        j = k;
        i = m;
      }
    }
    for (;;)
    {
      paramCameraMetadataNative.set(CaptureResult.FLASH_STATE, localObject);
      paramCameraMetadataNative.set(CaptureResult.FLASH_MODE, Integer.valueOf(j));
      paramCameraMetadataNative.set(CaptureResult.CONTROL_AE_MODE, Integer.valueOf(i));
      return;
      paramCameraCharacteristics = Integer.valueOf(0);
      break;
      label107:
      if (str.equals("auto"))
      {
        i = 2;
        j = k;
        localObject = paramCameraCharacteristics;
      }
      else if (str.equals("on"))
      {
        j = 1;
        i = 3;
        localObject = Integer.valueOf(3);
      }
      else if (str.equals("red-eye"))
      {
        i = 4;
        j = k;
        localObject = paramCameraCharacteristics;
      }
      else if (str.equals("torch"))
      {
        j = 2;
        localObject = Integer.valueOf(3);
        i = m;
      }
      else
      {
        Log.w("LegacyResultMapper", "mapAeAndFlashMode - Ignoring unknown flash mode " + paramParameters.getFlashMode());
        i = m;
        j = k;
        localObject = paramCameraCharacteristics;
      }
    }
  }
  
  private static void mapAf(CameraMetadataNative paramCameraMetadataNative, Rect paramRect, ParameterUtils.ZoomData paramZoomData, Camera.Parameters paramParameters)
  {
    paramCameraMetadataNative.set(CaptureResult.CONTROL_AF_MODE, Integer.valueOf(convertLegacyAfMode(paramParameters.getFocusMode())));
    if (paramParameters.getMaxNumFocusAreas() > 0)
    {
      paramRect = getMeteringRectangles(paramRect, paramZoomData, paramParameters.getFocusAreas(), "AF");
      paramCameraMetadataNative.set(CaptureResult.CONTROL_AF_REGIONS, paramRect);
    }
  }
  
  private static void mapAwb(CameraMetadataNative paramCameraMetadataNative, Camera.Parameters paramParameters)
  {
    if (paramParameters.isAutoWhiteBalanceLockSupported()) {}
    for (boolean bool = paramParameters.getAutoWhiteBalanceLock();; bool = false)
    {
      paramCameraMetadataNative.set(CaptureResult.CONTROL_AWB_LOCK, Boolean.valueOf(bool));
      int i = convertLegacyAwbMode(paramParameters.getWhiteBalance());
      paramCameraMetadataNative.set(CaptureResult.CONTROL_AWB_MODE, Integer.valueOf(i));
      return;
    }
  }
  
  private static void mapScaler(CameraMetadataNative paramCameraMetadataNative, ParameterUtils.ZoomData paramZoomData, Camera.Parameters paramParameters)
  {
    paramCameraMetadataNative.set(CaptureResult.SCALER_CROP_REGION, paramZoomData.reportedCrop);
  }
  
  public CameraMetadataNative cachedConvertResultMetadata(LegacyRequest paramLegacyRequest, long paramLong)
  {
    if ((this.mCachedRequest != null) && (paramLegacyRequest.parameters.same(this.mCachedRequest.parameters)) && (paramLegacyRequest.captureRequest.equals(this.mCachedRequest.captureRequest))) {}
    CameraMetadataNative localCameraMetadataNative;
    for (paramLegacyRequest = new CameraMetadataNative(this.mCachedResult);; paramLegacyRequest = localCameraMetadataNative)
    {
      paramLegacyRequest.set(CaptureResult.SENSOR_TIMESTAMP, Long.valueOf(paramLong));
      return paramLegacyRequest;
      localCameraMetadataNative = convertResultMetadata(paramLegacyRequest);
      this.mCachedRequest = paramLegacyRequest;
      this.mCachedResult = new CameraMetadataNative(localCameraMetadataNative);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/LegacyResultMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */