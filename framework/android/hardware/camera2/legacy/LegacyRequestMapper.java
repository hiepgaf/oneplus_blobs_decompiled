package android.hardware.camera2.legacy;

import android.graphics.Rect;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Key;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.utils.ListUtils;
import android.hardware.camera2.utils.ParamsUtils;
import android.location.Location;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class LegacyRequestMapper
{
  private static final boolean DEBUG = false;
  private static final byte DEFAULT_JPEG_QUALITY = 85;
  private static final String TAG = "LegacyRequestMapper";
  
  private static boolean checkForCompleteGpsData(Location paramLocation)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramLocation != null)
    {
      bool1 = bool2;
      if (paramLocation.getProvider() != null)
      {
        bool1 = bool2;
        if (paramLocation.getTime() != 0L) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  private static String convertAeAntiBandingModeToLegacy(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return "off";
    case 1: 
      return "50hz";
    case 2: 
      return "60hz";
    }
    return "auto";
  }
  
  private static int[] convertAeFpsRangeToLegacy(Range<Integer> paramRange)
  {
    return new int[] { ((Integer)paramRange.getLower()).intValue() * 1000, ((Integer)paramRange.getUpper()).intValue() * 1000 };
  }
  
  private static String convertAwbModeToLegacy(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      Log.w("LegacyRequestMapper", "convertAwbModeToLegacy - unrecognized control.awbMode" + paramInt);
      return "auto";
    case 1: 
      return "auto";
    case 2: 
      return "incandescent";
    case 3: 
      return "fluorescent";
    case 4: 
      return "warm-fluorescent";
    case 5: 
      return "daylight";
    case 6: 
      return "cloudy-daylight";
    case 7: 
      return "twilight";
    }
    return "shade";
  }
  
  private static List<Camera.Area> convertMeteringRegionsToLegacy(Rect paramRect, ParameterUtils.ZoomData paramZoomData, MeteringRectangle[] paramArrayOfMeteringRectangle, int paramInt, String paramString)
  {
    if ((paramArrayOfMeteringRectangle == null) || (paramInt <= 0))
    {
      if (paramInt > 0) {
        return Arrays.asList(new Camera.Area[] { ParameterUtils.CAMERA_AREA_DEFAULT });
      }
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    int j = paramArrayOfMeteringRectangle.length;
    int i = 0;
    while (i < j)
    {
      MeteringRectangle localMeteringRectangle = paramArrayOfMeteringRectangle[i];
      if (localMeteringRectangle.getMeteringWeight() != 0) {
        localArrayList.add(localMeteringRectangle);
      }
      i += 1;
    }
    if (localArrayList.size() == 0)
    {
      Log.w("LegacyRequestMapper", "Only received metering rectangles with weight 0.");
      return Arrays.asList(new Camera.Area[] { ParameterUtils.CAMERA_AREA_DEFAULT });
    }
    j = Math.min(paramInt, localArrayList.size());
    paramArrayOfMeteringRectangle = new ArrayList(j);
    i = 0;
    while (i < j)
    {
      paramArrayOfMeteringRectangle.add(ParameterUtils.convertMeteringRectangleToLegacy(paramRect, (MeteringRectangle)localArrayList.get(i), paramZoomData).meteringArea);
      i += 1;
    }
    if (paramInt < localArrayList.size()) {
      Log.w("LegacyRequestMapper", "convertMeteringRegionsToLegacy - Too many requested " + paramString + " regions, ignoring all beyond the first " + paramInt);
    }
    return paramArrayOfMeteringRectangle;
  }
  
  public static void convertRequestMetadata(LegacyRequest paramLegacyRequest)
  {
    CameraCharacteristics localCameraCharacteristics = paramLegacyRequest.characteristics;
    CaptureRequest localCaptureRequest = paramLegacyRequest.captureRequest;
    Object localObject2 = paramLegacyRequest.previewSize;
    Camera.Parameters localParameters = paramLegacyRequest.parameters;
    Object localObject1 = (Rect)localCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
    localObject2 = ParameterUtils.convertScalerCropRegion((Rect)localObject1, (Rect)localCaptureRequest.get(CaptureRequest.SCALER_CROP_REGION), (Size)localObject2, localParameters);
    if (localParameters.isZoomSupported()) {
      localParameters.setZoom(((ParameterUtils.ZoomData)localObject2).zoomIndex);
    }
    int i = ((Integer)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE, Integer.valueOf(1))).intValue();
    if ((i != 1) && (i != 2)) {
      Log.w("LegacyRequestMapper", "convertRequestToMetadata - Ignoring unsupported colorCorrection.aberrationMode = " + i);
    }
    paramLegacyRequest = (Integer)localCaptureRequest.get(CaptureRequest.CONTROL_AE_ANTIBANDING_MODE);
    label392:
    boolean bool;
    if (paramLegacyRequest != null)
    {
      paramLegacyRequest = convertAeAntiBandingModeToLegacy(paramLegacyRequest.intValue());
      if (paramLegacyRequest != null) {
        localParameters.setAntibanding(paramLegacyRequest);
      }
      paramLegacyRequest = (MeteringRectangle[])localCaptureRequest.get(CaptureRequest.CONTROL_AE_REGIONS);
      if (localCaptureRequest.get(CaptureRequest.CONTROL_AWB_REGIONS) != null) {
        Log.w("LegacyRequestMapper", "convertRequestMetadata - control.awbRegions setting is not supported, ignoring value");
      }
      i = localParameters.getMaxNumMeteringAreas();
      paramLegacyRequest = convertMeteringRegionsToLegacy((Rect)localObject1, (ParameterUtils.ZoomData)localObject2, paramLegacyRequest, i, "AE");
      if (i > 0) {
        localParameters.setMeteringAreas(paramLegacyRequest);
      }
      paramLegacyRequest = (MeteringRectangle[])localCaptureRequest.get(CaptureRequest.CONTROL_AF_REGIONS);
      i = localParameters.getMaxNumFocusAreas();
      paramLegacyRequest = convertMeteringRegionsToLegacy((Rect)localObject1, (ParameterUtils.ZoomData)localObject2, paramLegacyRequest, i, "AF");
      if (i > 0) {
        localParameters.setFocusAreas(paramLegacyRequest);
      }
      paramLegacyRequest = (Range)localCaptureRequest.get(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE);
      if (paramLegacyRequest != null)
      {
        localObject2 = convertAeFpsRangeToLegacy(paramLegacyRequest);
        localObject1 = null;
        Iterator localIterator = localParameters.getSupportedPreviewFpsRange().iterator();
        do
        {
          paramLegacyRequest = (LegacyRequest)localObject1;
          if (!localIterator.hasNext()) {
            break;
          }
          paramLegacyRequest = (int[])localIterator.next();
          i = (int)Math.floor(paramLegacyRequest[0] / 1000.0D);
          j = (int)Math.ceil(paramLegacyRequest[1] / 1000.0D);
        } while ((localObject2[0] != i * 1000) || (localObject2[1] != j * 1000));
        if (paramLegacyRequest == null) {
          break label1333;
        }
        localParameters.setPreviewFpsRange(paramLegacyRequest[0], paramLegacyRequest[1]);
      }
      paramLegacyRequest = (Range)localCameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
      int j = ((Integer)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, Integer.valueOf(0))).intValue();
      i = j;
      if (!paramLegacyRequest.contains(Integer.valueOf(j)))
      {
        Log.w("LegacyRequestMapper", "convertRequestMetadata - control.aeExposureCompensation is out of range, ignoring value");
        i = 0;
      }
      localParameters.setExposureCompensation(i);
      paramLegacyRequest = (Boolean)getIfSupported(localCaptureRequest, CaptureRequest.CONTROL_AE_LOCK, Boolean.valueOf(false), localParameters.isAutoExposureLockSupported(), Boolean.valueOf(false));
      if (paramLegacyRequest != null) {
        localParameters.setAutoExposureLock(paramLegacyRequest.booleanValue());
      }
      mapAeAndFlashMode(localCaptureRequest, localParameters);
      paramLegacyRequest = LegacyMetadataMapper.convertAfModeToLegacy(((Integer)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.CONTROL_AF_MODE, Integer.valueOf(0))).intValue(), localParameters.getSupportedFocusModes());
      if (paramLegacyRequest != null) {
        localParameters.setFocusMode(paramLegacyRequest);
      }
      paramLegacyRequest = CaptureRequest.CONTROL_AWB_MODE;
      if (localParameters.getSupportedWhiteBalance() == null) {
        break label1384;
      }
      bool = true;
      label549:
      paramLegacyRequest = (Integer)getIfSupported(localCaptureRequest, paramLegacyRequest, Integer.valueOf(1), bool, Integer.valueOf(1));
      if (paramLegacyRequest != null) {
        localParameters.setWhiteBalance(convertAwbModeToLegacy(paramLegacyRequest.intValue()));
      }
      paramLegacyRequest = (Boolean)getIfSupported(localCaptureRequest, CaptureRequest.CONTROL_AWB_LOCK, Boolean.valueOf(false), localParameters.isAutoWhiteBalanceLockSupported(), Boolean.valueOf(false));
      if (paramLegacyRequest != null) {
        localParameters.setAutoWhiteBalanceLock(paramLegacyRequest.booleanValue());
      }
      i = filterSupportedCaptureIntent(((Integer)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.CONTROL_CAPTURE_INTENT, Integer.valueOf(1))).intValue());
      if (i == 3) {
        break label1389;
      }
      if (i != 4) {
        break label1394;
      }
      bool = true;
      label656:
      localParameters.setRecordingHint(bool);
      paramLegacyRequest = (Integer)getIfSupported(localCaptureRequest, CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, Integer.valueOf(0), localParameters.isVideoStabilizationSupported(), Integer.valueOf(0));
      if (paramLegacyRequest != null)
      {
        if (paramLegacyRequest.intValue() != 1) {
          break label1399;
        }
        bool = true;
        label701:
        localParameters.setVideoStabilization(bool);
      }
      bool = ListUtils.listContains(localParameters.getSupportedFocusModes(), "infinity");
      paramLegacyRequest = (Float)getIfSupported(localCaptureRequest, CaptureRequest.LENS_FOCUS_DISTANCE, Float.valueOf(0.0F), bool, Float.valueOf(0.0F));
      if ((paramLegacyRequest == null) || (paramLegacyRequest.floatValue() != 0.0F)) {
        Log.w("LegacyRequestMapper", "convertRequestToMetadata - Ignoring android.lens.focusDistance " + bool + ", only 0.0f is supported");
      }
      if (localParameters.getSupportedSceneModes() != null) {
        i = ((Integer)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.CONTROL_MODE, Integer.valueOf(1))).intValue();
      }
      switch (i)
      {
      default: 
        Log.w("LegacyRequestMapper", "Control mode " + i + " is unsupported, defaulting to AUTO");
        paramLegacyRequest = "auto";
        label871:
        localParameters.setSceneMode(paramLegacyRequest);
        if (localParameters.getSupportedColorEffects() != null)
        {
          i = ((Integer)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.CONTROL_EFFECT_MODE, Integer.valueOf(0))).intValue();
          paramLegacyRequest = LegacyMetadataMapper.convertEffectModeToLegacy(i);
          if (paramLegacyRequest != null) {
            localParameters.setColorEffect(paramLegacyRequest);
          }
        }
        else
        {
          label919:
          i = ((Integer)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.SENSOR_TEST_PATTERN_MODE, Integer.valueOf(0))).intValue();
          if (i != 0) {
            Log.w("LegacyRequestMapper", "convertRequestToMetadata - ignoring sensor.testPatternMode " + i + "; only OFF is supported");
          }
          paramLegacyRequest = (Location)localCaptureRequest.get(CaptureRequest.JPEG_GPS_LOCATION);
          if (paramLegacyRequest == null) {
            break label1539;
          }
          if (!checkForCompleteGpsData(paramLegacyRequest)) {
            break label1510;
          }
          localParameters.setGpsAltitude(paramLegacyRequest.getAltitude());
          localParameters.setGpsLatitude(paramLegacyRequest.getLatitude());
          localParameters.setGpsLongitude(paramLegacyRequest.getLongitude());
          localParameters.setGpsProcessingMethod(paramLegacyRequest.getProvider().toUpperCase());
          localParameters.setGpsTimestamp(paramLegacyRequest.getTime());
          label1045:
          paramLegacyRequest = (Integer)localCaptureRequest.get(CaptureRequest.JPEG_ORIENTATION);
          localObject1 = CaptureRequest.JPEG_ORIENTATION;
          if (paramLegacyRequest != null) {
            break label1547;
          }
          i = 0;
          label1068:
          localParameters.setRotation(((Integer)ParamsUtils.getOrDefault(localCaptureRequest, (CaptureRequest.Key)localObject1, Integer.valueOf(i))).intValue());
          localParameters.setJpegQuality(((Byte)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.JPEG_QUALITY, Byte.valueOf((byte)85))).byteValue() & 0xFF);
          localParameters.setJpegThumbnailQuality(((Byte)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.JPEG_THUMBNAIL_QUALITY, Byte.valueOf((byte)85))).byteValue() & 0xFF);
          paramLegacyRequest = localParameters.getSupportedJpegThumbnailSizes();
          if ((paramLegacyRequest != null) && (paramLegacyRequest.size() > 0))
          {
            localObject1 = (Size)localCaptureRequest.get(CaptureRequest.JPEG_THUMBNAIL_SIZE);
            if (localObject1 != null) {
              break label1555;
            }
            label1183:
            i = 0;
            label1185:
            if (i != 0) {
              Log.w("LegacyRequestMapper", "Invalid JPEG thumbnail size set " + localObject1 + ", skipping thumbnail...");
            }
            if ((localObject1 != null) && (i == 0)) {
              break label1577;
            }
            localParameters.setJpegThumbnailSize(0, 0);
          }
        }
        break;
      }
    }
    for (;;)
    {
      i = ((Integer)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.NOISE_REDUCTION_MODE, Integer.valueOf(1))).intValue();
      if ((i != 1) && (i != 2)) {
        Log.w("LegacyRequestMapper", "convertRequestToMetadata - Ignoring unsupported noiseReduction.mode = " + i);
      }
      return;
      paramLegacyRequest = (String)ListUtils.listSelectFirstFrom(localParameters.getSupportedAntibanding(), new String[] { "auto", "off", "50hz", "60hz" });
      break;
      label1333:
      Log.w("LegacyRequestMapper", "Unsupported FPS range set [" + localObject2[0] + "," + localObject2[1] + "]");
      break label392;
      label1384:
      bool = false;
      break label549;
      label1389:
      bool = true;
      break label656;
      label1394:
      bool = false;
      break label656;
      label1399:
      bool = false;
      break label701;
      i = ((Integer)ParamsUtils.getOrDefault(localCaptureRequest, CaptureRequest.CONTROL_SCENE_MODE, Integer.valueOf(0))).intValue();
      paramLegacyRequest = LegacyMetadataMapper.convertSceneModeToLegacy(i);
      if (paramLegacyRequest != null) {
        break label871;
      }
      paramLegacyRequest = "auto";
      Log.w("LegacyRequestMapper", "Skipping unknown requested scene mode: " + i);
      break label871;
      paramLegacyRequest = "auto";
      break label871;
      localParameters.setColorEffect("none");
      Log.w("LegacyRequestMapper", "Skipping unknown requested effect mode: " + i);
      break label919;
      label1510:
      Log.w("LegacyRequestMapper", "Incomplete GPS parameters provided in location " + paramLegacyRequest);
      break label1045;
      label1539:
      localParameters.removeGpsData();
      break label1045;
      label1547:
      i = paramLegacyRequest.intValue();
      break label1068;
      label1555:
      if (ParameterUtils.containsSize(paramLegacyRequest, ((Size)localObject1).getWidth(), ((Size)localObject1).getHeight())) {
        break label1183;
      }
      i = 1;
      break label1185;
      label1577:
      localParameters.setJpegThumbnailSize(((Size)localObject1).getWidth(), ((Size)localObject1).getHeight());
    }
  }
  
  static int filterSupportedCaptureIntent(int paramInt)
  {
    int i = paramInt;
    switch (paramInt)
    {
    }
    for (;;)
    {
      i = 1;
      Log.w("LegacyRequestMapper", "Unknown control.captureIntent value " + 1 + "; default to PREVIEW");
      return i;
      Log.w("LegacyRequestMapper", "Unsupported control.captureIntent value " + 1 + "; default to PREVIEW");
    }
  }
  
  private static <T> T getIfSupported(CaptureRequest paramCaptureRequest, CaptureRequest.Key<T> paramKey, T paramT1, boolean paramBoolean, T paramT2)
  {
    paramCaptureRequest = ParamsUtils.getOrDefault(paramCaptureRequest, paramKey, paramT1);
    if (!paramBoolean)
    {
      if (!Objects.equals(paramCaptureRequest, paramT2)) {
        Log.w("LegacyRequestMapper", paramKey.getName() + " is not supported; ignoring requested value " + paramCaptureRequest);
      }
      return null;
    }
    return paramCaptureRequest;
  }
  
  private static void mapAeAndFlashMode(CaptureRequest paramCaptureRequest, Camera.Parameters paramParameters)
  {
    int i = ((Integer)ParamsUtils.getOrDefault(paramCaptureRequest, CaptureRequest.FLASH_MODE, Integer.valueOf(0))).intValue();
    int j = ((Integer)ParamsUtils.getOrDefault(paramCaptureRequest, CaptureRequest.CONTROL_AE_MODE, Integer.valueOf(1))).intValue();
    List localList = paramParameters.getSupportedFlashModes();
    String str = null;
    if (ListUtils.listContains(localList, "off")) {
      str = "off";
    }
    if (j == 1) {
      if (i == 2) {
        if (ListUtils.listContains(localList, "torch")) {
          paramCaptureRequest = "torch";
        }
      }
    }
    for (;;)
    {
      if (paramCaptureRequest != null) {
        paramParameters.setFlashMode(paramCaptureRequest);
      }
      return;
      Log.w("LegacyRequestMapper", "mapAeAndFlashMode - Ignore flash.mode == TORCH;camera does not support it");
      paramCaptureRequest = str;
      continue;
      paramCaptureRequest = str;
      if (i == 1) {
        if (ListUtils.listContains(localList, "on"))
        {
          paramCaptureRequest = "on";
        }
        else
        {
          Log.w("LegacyRequestMapper", "mapAeAndFlashMode - Ignore flash.mode == SINGLE;camera does not support it");
          paramCaptureRequest = str;
          continue;
          if (j == 3)
          {
            if (ListUtils.listContains(localList, "on"))
            {
              paramCaptureRequest = "on";
            }
            else
            {
              Log.w("LegacyRequestMapper", "mapAeAndFlashMode - Ignore control.aeMode == ON_ALWAYS_FLASH;camera does not support it");
              paramCaptureRequest = str;
            }
          }
          else if (j == 2)
          {
            if (ListUtils.listContains(localList, "auto"))
            {
              paramCaptureRequest = "auto";
            }
            else
            {
              Log.w("LegacyRequestMapper", "mapAeAndFlashMode - Ignore control.aeMode == ON_AUTO_FLASH;camera does not support it");
              paramCaptureRequest = str;
            }
          }
          else
          {
            paramCaptureRequest = str;
            if (j == 4) {
              if (ListUtils.listContains(localList, "red-eye"))
              {
                paramCaptureRequest = "red-eye";
              }
              else
              {
                Log.w("LegacyRequestMapper", "mapAeAndFlashMode - Ignore control.aeMode == ON_AUTO_FLASH_REDEYE;camera does not support it");
                paramCaptureRequest = str;
              }
            }
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/LegacyRequestMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */