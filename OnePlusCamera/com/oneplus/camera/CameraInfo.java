package com.oneplus.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraCharacteristics.Key;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import android.util.SizeF;
import android.view.SurfaceHolder;
import com.oneplus.base.BasicBaseObject;
import com.oneplus.base.Device;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import com.oneplus.util.ListUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CameraInfo
  extends BasicBaseObject
{
  private static final boolean ENABLE_LOG = false;
  private static final int INVALID_PREVIEW_HEIGHT = 1200;
  private static final String INVALID_PREVIEW_SIZE = "1600x1200";
  private static final int INVALID_PREVIEW_WIDTH = 1600;
  public static final int LENS_FACING_BACK = 1;
  public static final int LENS_FACING_BACK_TELE = -2;
  public static final int LENS_FACING_BACK_WIDE = -1;
  public static final int LENS_FACING_EXTERNAL = 2;
  public static final int LENS_FACING_FRONT = 0;
  private static final String PREFERENCE_PREFIX = TAG + "_";
  private static final String PREF_HASH_CODE = "HashCode";
  private static final String PREF_ROM_BUILD_VERSION = "RomBuildVersion";
  private static final String PREF_VERSION = "Version";
  public static final PropertyKey<List<Integer>> PROP_AF_MODES;
  public static final PropertyKey<List<Integer>> PROP_AWB_MODES = new PropertyKey("AWBModes", List.class, CameraInfo.class, Collections.EMPTY_LIST);
  public static final PropertyKey<List<Integer>> PROP_CAPABILITIES;
  public static final PropertyKey<Rational> PROP_EV_STEP;
  public static final PropertyKey<Range<Integer>> PROP_EXPOSURE_COMP_RANGE;
  public static final PropertyKey<Range<Long>> PROP_EXPOSURE_TIME_RANGE;
  public static final PropertyKey<Integer> PROP_FACE_BEAUTY_DEFAULT_VALUE;
  public static final PropertyKey<Integer> PROP_FACE_BEAUTY_VALUE;
  public static final PropertyKey<List<Integer>> PROP_FACE_BEAUTY_VALUE_LIST;
  public static final PropertyKey<Boolean> PROP_FLASH_AVAILABLE;
  public static final PropertyKey<List<FlashMode>> PROP_FLASH_MODES;
  public static final PropertyKey<Range<Float>> PROP_FOCUS_RANGE;
  public static final PropertyKey<Integer> PROP_HARDWARE_LEVEL;
  public static final PropertyKey<Range<Integer>> PROP_ISO_RANGE;
  public static final PropertyKey<Boolean> PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED;
  public static final PropertyKey<Boolean> PROP_IS_BOKEH_SUPPORTED;
  public static final PropertyKey<Boolean> PROP_IS_BURST_CAPTURE_SUPPORTED;
  public static final PropertyKey<Boolean> PROP_IS_HIGH_VIDEO_FRAME_RATE_SUPPORTED;
  public static final PropertyKey<Boolean> PROP_IS_MIRROR_SUPPORTED;
  public static final PropertyKey<Boolean> PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED;
  public static final PropertyKey<Boolean> PROP_IS_WATERMARK_SUPPORTED;
  public static final PropertyKey<Integer> PROP_LENS_FACING;
  public static final PropertyKey<Integer> PROP_MAX_AE_COUNT;
  public static final PropertyKey<Integer> PROP_MAX_AF_COUNT;
  public static final PropertyKey<Float> PROP_MAX_DIGITAL_ZOOM;
  public static final PropertyKey<Integer> PROP_MAX_FACE_COUNT;
  public static final PropertyKey<List<Size>> PROP_PICTURE_SIZES;
  public static final PropertyKey<List<Integer>> PROP_SCENE_MODES;
  public static final PropertyKey<Integer> PROP_SENSOR_ORIENTATION;
  public static final PropertyKey<SizeF> PROP_SENSOR_PHYSICAL_SIZE;
  public static final PropertyKey<Size> PROP_SENSOR_PIXEL_SIZE_FULL;
  public static final PropertyKey<Rect> PROP_SENSOR_RECT;
  public static final PropertyKey<List<Size>> PROP_SURFACE_SIZES;
  public static final PropertyKey<List<Range<Integer>>> PROP_TARGET_FPS_RANGES;
  public static final PropertyKey<List<Size>> PROP_THUMBNAIL_SIZES;
  public static final PropertyKey<List<Size>> PROP_VIDEO_60FPS_SIZES;
  public static final PropertyKey<List<Size>> PROP_VIDEO_SIZES;
  private static final String TAG;
  private static final int VERSION = 17;
  private SharedPreferences m_CharsPreference;
  
  static
  {
    PROP_AF_MODES = new PropertyKey("AFModes", List.class, CameraInfo.class, Collections.EMPTY_LIST);
    PROP_CAPABILITIES = new PropertyKey("Capabilities", List.class, CameraInfo.class, Collections.EMPTY_LIST);
    PROP_EV_STEP = new PropertyKey("EVStep", Rational.class, CameraInfo.class, Rational.ZERO);
    PROP_EXPOSURE_COMP_RANGE = new PropertyKey("ExposureCompRange", Range.class, CameraInfo.class, new Range(Integer.valueOf(0), Integer.valueOf(0)));
    PROP_EXPOSURE_TIME_RANGE = new PropertyKey("ExposureTimeRange", Range.class, CameraInfo.class, new Range(Long.valueOf(0L), Long.valueOf(0L)));
    PROP_FACE_BEAUTY_DEFAULT_VALUE = new PropertyKey("FaceBeautyDefaultValue", Integer.class, CameraInfo.class, Integer.valueOf(0));
    PROP_FACE_BEAUTY_VALUE = new PropertyKey("FaceBeautyValue", Integer.class, CameraInfo.class, Integer.valueOf(0));
    PROP_FACE_BEAUTY_VALUE_LIST = new PropertyKey("FaceBeautyValueList", List.class, CameraInfo.class, Collections.EMPTY_LIST);
    PROP_FLASH_AVAILABLE = new PropertyKey("FlashAvailable", Boolean.class, CameraInfo.class, Boolean.valueOf(false));
    PROP_FLASH_MODES = new PropertyKey("FlashModes", List.class, CameraInfo.class, Arrays.asList(new FlashMode[] { FlashMode.OFF }));
    PROP_FOCUS_RANGE = new PropertyKey("FocusRange", Range.class, CameraInfo.class, new Range(Float.valueOf(0.0F), Float.valueOf(0.0F)));
    PROP_HARDWARE_LEVEL = new PropertyKey("HardwareLevel", Integer.class, CameraInfo.class, Integer.valueOf(0));
    PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED = new PropertyKey("IsBokehOriginalPictureSupported", Boolean.class, CameraInfo.class, 1, Boolean.valueOf(false));
    PROP_IS_BOKEH_SUPPORTED = new PropertyKey("IsBokehSupported", Boolean.class, CameraInfo.class, Boolean.valueOf(false));
    PROP_IS_BURST_CAPTURE_SUPPORTED = new PropertyKey("IsBurstCaptureSupported", Boolean.class, CameraInfo.class, Boolean.valueOf(true));
    PROP_IS_HIGH_VIDEO_FRAME_RATE_SUPPORTED = new PropertyKey("IsHighVideoFrameRateSupported", Boolean.class, CameraInfo.class, Boolean.valueOf(false));
    PROP_IS_MIRROR_SUPPORTED = new PropertyKey("IsMirrorSupported", Boolean.class, CameraInfo.class, Boolean.valueOf(false));
    PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED = new PropertyKey("IsStandaloneFaceBeautySupported", Boolean.class, CameraInfo.class, Boolean.valueOf(false));
    PROP_IS_WATERMARK_SUPPORTED = new PropertyKey("IsWatermarkSupported", Boolean.class, CameraInfo.class, Boolean.valueOf(false));
    PROP_ISO_RANGE = new PropertyKey("ISORange", Range.class, CameraInfo.class, new Range(Integer.valueOf(0), Integer.valueOf(0)));
    PROP_SENSOR_ORIENTATION = new PropertyKey("SensorOrientation", Integer.class, CameraInfo.class, Integer.valueOf(0));
    PROP_MAX_AE_COUNT = new PropertyKey("MaxAECount", Integer.class, CameraInfo.class, Integer.valueOf(0));
    PROP_MAX_AF_COUNT = new PropertyKey("MaxAFCount", Integer.class, CameraInfo.class, Integer.valueOf(0));
    PROP_MAX_DIGITAL_ZOOM = new PropertyKey("MaxDigitalZoom", Float.class, CameraInfo.class, Float.valueOf(0.0F));
    PROP_PICTURE_SIZES = new PropertyKey("PictureSizes", Size.class, CameraInfo.class, Collections.EMPTY_LIST);
    PROP_SCENE_MODES = new PropertyKey("SceneModes", List.class, CameraInfo.class, Collections.EMPTY_LIST);
    PROP_SENSOR_RECT = new PropertyKey("SensorRect", Rect.class, CameraInfo.class, new Rect());
    PROP_SENSOR_PIXEL_SIZE_FULL = new PropertyKey("SensorPixelSizeFull", Size.class, CameraInfo.class, new Size(0, 0));
    PROP_SENSOR_PHYSICAL_SIZE = new PropertyKey("SensorPhysicalSize", SizeF.class, CameraInfo.class, new SizeF(0.0F, 0.0F));
    PROP_SURFACE_SIZES = new PropertyKey("SurfaceSizes", List.class, CameraInfo.class, Collections.EMPTY_LIST);
    PROP_TARGET_FPS_RANGES = new PropertyKey("TargetFPSRanges", List.class, CameraInfo.class, Collections.EMPTY_LIST);
    PROP_VIDEO_SIZES = new PropertyKey("VideoSizes", List.class, CameraInfo.class, Collections.EMPTY_LIST);
    PROP_VIDEO_60FPS_SIZES = new PropertyKey("Video60FpsSizes", List.class, CameraInfo.class, Collections.EMPTY_LIST);
    PROP_MAX_FACE_COUNT = new PropertyKey("MaxFaceCount", Integer.class, CameraInfo.class, Integer.valueOf(0));
    PROP_LENS_FACING = new PropertyKey("LensFacing", Integer.class, CameraInfo.class, 2, Integer.valueOf(0));
    PROP_THUMBNAIL_SIZES = new PropertyKey("ThumbnailSizes", List.class, CameraInfo.class, Collections.EMPTY_LIST);
    TAG = CameraInfo.class.getSimpleName();
  }
  
  public CameraInfo(Context paramContext, CameraManager paramCameraManager, String paramString, int paramInt)
    throws CameraAccessException
  {
    int i = 0;
    String str1;
    String str2;
    if (paramInt > 1)
    {
      this.m_CharsPreference = paramContext.getSharedPreferences(PREFERENCE_PREFIX + paramString, 0);
      paramContext = Long.valueOf(this.m_CharsPreference.getLong("HashCode", 0L));
      str1 = this.m_CharsPreference.getString("RomBuildVersion", "");
      i = this.m_CharsPreference.getInt("Version", 0);
      str2 = Device.getSystemProperty("ro.build.date.YmdHM");
      if (i != 17)
      {
        Log.v(TAG, "cameraInfo() - Version incompatible: ", Integer.valueOf(i), " -> ", Integer.valueOf(17), ", re-write data");
        i = 1;
      }
    }
    else
    {
      if (i == 0) {
        break label247;
      }
      saveCameraInfo(paramCameraManager, paramString);
    }
    label247:
    while (paramInt > 1)
    {
      return;
      if (!str1.equals(str2))
      {
        Log.v(TAG, "cameraInfo() - Rom version incompatible: " + str1 + " -> " + str2 + ", re-write data");
        i = 1;
        break;
      }
      if (paramContext.longValue() != 0L)
      {
        if (loadCharacteristics(paramString) == paramContext.longValue()) {
          return;
        }
        Log.e(TAG, "cameraInfo() - Data is inconsistent, re-write data");
        i = 1;
        break;
      }
      i = 1;
      break;
    }
    Log.w(TAG, "cameraInfo() - Camera counts incorrect: " + paramInt + ", dont save prefs");
  }
  
  /* Error */
  private boolean getCameraCharacteristics(CameraManager paramCameraManager, String paramString)
  {
    // Byte code:
    //   0: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   3: ldc_w 433
    //   6: aload_2
    //   7: ldc_w 435
    //   10: invokestatic 438	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   13: aconst_null
    //   14: astore 16
    //   16: aload_1
    //   17: aload_2
    //   18: invokevirtual 443	android/hardware/camera2/CameraManager:getCameraCharacteristics	(Ljava/lang/String;)Landroid/hardware/camera2/CameraCharacteristics;
    //   21: astore_1
    //   22: aload_1
    //   23: ifnonnull +22 -> 45
    //   26: iconst_0
    //   27: ireturn
    //   28: astore_1
    //   29: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   32: ldc_w 445
    //   35: aload_1
    //   36: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   39: aload 16
    //   41: astore_1
    //   42: goto -20 -> 22
    //   45: aload_1
    //   46: getstatic 454	android/hardware/camera2/CameraCharacteristics:INFO_SUPPORTED_HARDWARE_LEVEL	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   49: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   52: checkcast 136	java/lang/Integer
    //   55: invokevirtual 462	java/lang/Integer:intValue	()I
    //   58: iconst_2
    //   59: if_icmpne +256 -> 315
    //   62: iconst_1
    //   63: istore_3
    //   64: iload_3
    //   65: ifeq +255 -> 320
    //   68: aload_0
    //   69: aload_2
    //   70: invokespecial 466	com/oneplus/camera/CameraInfo:getCameraParameters	(Ljava/lang/String;)Landroid/hardware/Camera$Parameters;
    //   73: astore_2
    //   74: aload_0
    //   75: getstatic 116	com/oneplus/camera/CameraInfo:PROP_AF_MODES	Lcom/oneplus/base/PropertyKey;
    //   78: aload_0
    //   79: aload_1
    //   80: getstatic 469	android/hardware/camera2/CameraCharacteristics:CONTROL_AF_AVAILABLE_MODES	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   83: invokespecial 473	com/oneplus/camera/CameraInfo:getIntListChars	(Landroid/hardware/camera2/CameraCharacteristics;Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/util/List;
    //   86: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   89: pop
    //   90: aload_0
    //   91: getstatic 112	com/oneplus/camera/CameraInfo:PROP_AWB_MODES	Lcom/oneplus/base/PropertyKey;
    //   94: aload_0
    //   95: aload_1
    //   96: getstatic 480	android/hardware/camera2/CameraCharacteristics:CONTROL_AWB_AVAILABLE_MODES	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   99: invokespecial 473	com/oneplus/camera/CameraInfo:getIntListChars	(Landroid/hardware/camera2/CameraCharacteristics;Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/util/List;
    //   102: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   105: pop
    //   106: aload_0
    //   107: getstatic 120	com/oneplus/camera/CameraInfo:PROP_CAPABILITIES	Lcom/oneplus/base/PropertyKey;
    //   110: aload_0
    //   111: aload_1
    //   112: getstatic 483	android/hardware/camera2/CameraCharacteristics:REQUEST_AVAILABLE_CAPABILITIES	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   115: invokespecial 473	com/oneplus/camera/CameraInfo:getIntListChars	(Landroid/hardware/camera2/CameraCharacteristics;Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/util/List;
    //   118: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   121: pop
    //   122: aload_0
    //   123: getstatic 130	com/oneplus/camera/CameraInfo:PROP_EV_STEP	Lcom/oneplus/base/PropertyKey;
    //   126: aload_1
    //   127: getstatic 486	android/hardware/camera2/CameraCharacteristics:CONTROL_AE_COMPENSATION_STEP	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   130: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   133: checkcast 124	android/util/Rational
    //   136: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   139: pop
    //   140: aload_0
    //   141: getstatic 145	com/oneplus/camera/CameraInfo:PROP_EXPOSURE_COMP_RANGE	Lcom/oneplus/base/PropertyKey;
    //   144: aload_1
    //   145: getstatic 489	android/hardware/camera2/CameraCharacteristics:CONTROL_AE_COMPENSATION_RANGE	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   148: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   151: checkcast 134	android/util/Range
    //   154: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   157: pop
    //   158: aload_0
    //   159: getstatic 154	com/oneplus/camera/CameraInfo:PROP_EXPOSURE_TIME_RANGE	Lcom/oneplus/base/PropertyKey;
    //   162: aload_1
    //   163: getstatic 492	android/hardware/camera2/CameraCharacteristics:SENSOR_INFO_EXPOSURE_TIME_RANGE	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   166: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   169: checkcast 134	android/util/Range
    //   172: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   175: pop
    //   176: aload_0
    //   177: getstatic 309	com/oneplus/camera/CameraInfo:PROP_MAX_FACE_COUNT	Lcom/oneplus/base/PropertyKey;
    //   180: aload_1
    //   181: getstatic 495	android/hardware/camera2/CameraCharacteristics:STATISTICS_INFO_MAX_FACE_COUNT	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   184: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   187: checkcast 136	java/lang/Integer
    //   190: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   193: pop
    //   194: aload_0
    //   195: getstatic 175	com/oneplus/camera/CameraInfo:PROP_FLASH_AVAILABLE	Lcom/oneplus/base/PropertyKey;
    //   198: aload_1
    //   199: getstatic 498	android/hardware/camera2/CameraCharacteristics:FLASH_INFO_AVAILABLE	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   202: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   205: checkcast 170	java/lang/Boolean
    //   208: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   211: pop
    //   212: aload_2
    //   213: ifnonnull +167 -> 380
    //   216: aload_0
    //   217: getstatic 175	com/oneplus/camera/CameraInfo:PROP_FLASH_AVAILABLE	Lcom/oneplus/base/PropertyKey;
    //   220: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   223: checkcast 170	java/lang/Boolean
    //   226: invokevirtual 505	java/lang/Boolean:booleanValue	()Z
    //   229: ifeq +151 -> 380
    //   232: new 507	java/util/ArrayList
    //   235: dup
    //   236: invokespecial 508	java/util/ArrayList:<init>	()V
    //   239: astore 16
    //   241: aload 16
    //   243: getstatic 183	com/oneplus/camera/FlashMode:OFF	Lcom/oneplus/camera/FlashMode;
    //   246: invokeinterface 511 2 0
    //   251: pop
    //   252: aload_1
    //   253: getstatic 514	android/hardware/camera2/CameraCharacteristics:CONTROL_AE_AVAILABLE_MODES	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   256: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   259: checkcast 516	[I
    //   262: astore 17
    //   264: iconst_0
    //   265: istore_3
    //   266: aload 17
    //   268: arraylength
    //   269: istore 4
    //   271: iload_3
    //   272: iload 4
    //   274: if_icmpge +93 -> 367
    //   277: aload 17
    //   279: iload_3
    //   280: iaload
    //   281: tableswitch	default:+27->308, 1:+44->325, 2:+58->339, 3:+72->353
    //   308: iload_3
    //   309: iconst_1
    //   310: iadd
    //   311: istore_3
    //   312: goto -41 -> 271
    //   315: iconst_0
    //   316: istore_3
    //   317: goto -253 -> 64
    //   320: aconst_null
    //   321: astore_2
    //   322: goto -248 -> 74
    //   325: aload 16
    //   327: getstatic 519	com/oneplus/camera/FlashMode:ON	Lcom/oneplus/camera/FlashMode;
    //   330: invokeinterface 511 2 0
    //   335: pop
    //   336: goto -28 -> 308
    //   339: aload 16
    //   341: getstatic 522	com/oneplus/camera/FlashMode:AUTO	Lcom/oneplus/camera/FlashMode;
    //   344: invokeinterface 511 2 0
    //   349: pop
    //   350: goto -42 -> 308
    //   353: aload 16
    //   355: getstatic 525	com/oneplus/camera/FlashMode:TORCH	Lcom/oneplus/camera/FlashMode;
    //   358: invokeinterface 511 2 0
    //   363: pop
    //   364: goto -56 -> 308
    //   367: aload_0
    //   368: getstatic 191	com/oneplus/camera/CameraInfo:PROP_FLASH_MODES	Lcom/oneplus/base/PropertyKey;
    //   371: aload 16
    //   373: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   376: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   379: pop
    //   380: aload_0
    //   381: getstatic 200	com/oneplus/camera/CameraInfo:PROP_FOCUS_RANGE	Lcom/oneplus/base/PropertyKey;
    //   384: aload_0
    //   385: aload_1
    //   386: invokespecial 533	com/oneplus/camera/CameraInfo:getFocusRange	(Landroid/hardware/camera2/CameraCharacteristics;)Landroid/util/Range;
    //   389: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   392: pop
    //   393: aload_0
    //   394: getstatic 204	com/oneplus/camera/CameraInfo:PROP_HARDWARE_LEVEL	Lcom/oneplus/base/PropertyKey;
    //   397: aload_1
    //   398: getstatic 454	android/hardware/camera2/CameraCharacteristics:INFO_SUPPORTED_HARDWARE_LEVEL	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   401: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   404: checkcast 136	java/lang/Integer
    //   407: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   410: pop
    //   411: aload_0
    //   412: getstatic 239	com/oneplus/camera/CameraInfo:PROP_ISO_RANGE	Lcom/oneplus/base/PropertyKey;
    //   415: aload_1
    //   416: getstatic 536	android/hardware/camera2/CameraCharacteristics:SENSOR_INFO_SENSITIVITY_RANGE	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   419: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   422: checkcast 134	android/util/Range
    //   425: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   428: pop
    //   429: aload_0
    //   430: getstatic 313	com/oneplus/camera/CameraInfo:PROP_LENS_FACING	Lcom/oneplus/base/PropertyKey;
    //   433: aload_1
    //   434: getstatic 539	android/hardware/camera2/CameraCharacteristics:LENS_FACING	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   437: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   440: checkcast 136	java/lang/Integer
    //   443: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   446: pop
    //   447: aload_0
    //   448: getstatic 247	com/oneplus/camera/CameraInfo:PROP_MAX_AE_COUNT	Lcom/oneplus/base/PropertyKey;
    //   451: aload_1
    //   452: getstatic 542	android/hardware/camera2/CameraCharacteristics:CONTROL_MAX_REGIONS_AE	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   455: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   458: checkcast 136	java/lang/Integer
    //   461: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   464: pop
    //   465: aload_0
    //   466: getstatic 251	com/oneplus/camera/CameraInfo:PROP_MAX_AF_COUNT	Lcom/oneplus/base/PropertyKey;
    //   469: aload_1
    //   470: getstatic 545	android/hardware/camera2/CameraCharacteristics:CONTROL_MAX_REGIONS_AF	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   473: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   476: checkcast 136	java/lang/Integer
    //   479: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   482: pop
    //   483: aload_0
    //   484: getstatic 255	com/oneplus/camera/CameraInfo:PROP_MAX_DIGITAL_ZOOM	Lcom/oneplus/base/PropertyKey;
    //   487: aload_1
    //   488: getstatic 548	android/hardware/camera2/CameraCharacteristics:SCALER_AVAILABLE_MAX_DIGITAL_ZOOM	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   491: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   494: checkcast 195	java/lang/Float
    //   497: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   500: pop
    //   501: aload_0
    //   502: getstatic 261	com/oneplus/camera/CameraInfo:PROP_PICTURE_SIZES	Lcom/oneplus/base/PropertyKey;
    //   505: aload_0
    //   506: aload_1
    //   507: invokespecial 552	com/oneplus/camera/CameraInfo:getPictureSizes	(Landroid/hardware/camera2/CameraCharacteristics;)Ljava/util/List;
    //   510: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   513: pop
    //   514: aload_0
    //   515: getstatic 265	com/oneplus/camera/CameraInfo:PROP_SCENE_MODES	Lcom/oneplus/base/PropertyKey;
    //   518: aload_0
    //   519: aload_1
    //   520: getstatic 555	android/hardware/camera2/CameraCharacteristics:CONTROL_AVAILABLE_SCENE_MODES	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   523: invokespecial 473	com/oneplus/camera/CameraInfo:getIntListChars	(Landroid/hardware/camera2/CameraCharacteristics;Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/util/List;
    //   526: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   529: pop
    //   530: aload_0
    //   531: getstatic 243	com/oneplus/camera/CameraInfo:PROP_SENSOR_ORIENTATION	Lcom/oneplus/base/PropertyKey;
    //   534: aload_1
    //   535: getstatic 558	android/hardware/camera2/CameraCharacteristics:SENSOR_ORIENTATION	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   538: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   541: checkcast 136	java/lang/Integer
    //   544: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   547: pop
    //   548: aload_0
    //   549: getstatic 289	com/oneplus/camera/CameraInfo:PROP_SENSOR_PHYSICAL_SIZE	Lcom/oneplus/base/PropertyKey;
    //   552: aload_1
    //   553: getstatic 561	android/hardware/camera2/CameraCharacteristics:SENSOR_INFO_PHYSICAL_SIZE	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   556: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   559: checkcast 284	android/util/SizeF
    //   562: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   565: pop
    //   566: aload_0
    //   567: getstatic 280	com/oneplus/camera/CameraInfo:PROP_SENSOR_PIXEL_SIZE_FULL	Lcom/oneplus/base/PropertyKey;
    //   570: aload_1
    //   571: getstatic 564	android/hardware/camera2/CameraCharacteristics:SENSOR_INFO_PIXEL_ARRAY_SIZE	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   574: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   577: checkcast 259	android/util/Size
    //   580: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   583: pop
    //   584: aload_0
    //   585: getstatic 273	com/oneplus/camera/CameraInfo:PROP_SENSOR_RECT	Lcom/oneplus/base/PropertyKey;
    //   588: aload_1
    //   589: getstatic 567	android/hardware/camera2/CameraCharacteristics:SENSOR_INFO_ACTIVE_ARRAY_SIZE	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   592: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   595: checkcast 269	android/graphics/Rect
    //   598: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   601: pop
    //   602: aload_2
    //   603: ifnonnull +16 -> 619
    //   606: aload_0
    //   607: getstatic 293	com/oneplus/camera/CameraInfo:PROP_SURFACE_SIZES	Lcom/oneplus/base/PropertyKey;
    //   610: aload_0
    //   611: aload_1
    //   612: invokespecial 570	com/oneplus/camera/CameraInfo:getSurfaceSizes	(Landroid/hardware/camera2/CameraCharacteristics;)Ljava/util/List;
    //   615: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   618: pop
    //   619: aload_0
    //   620: getstatic 297	com/oneplus/camera/CameraInfo:PROP_TARGET_FPS_RANGES	Lcom/oneplus/base/PropertyKey;
    //   623: aload_0
    //   624: aload_1
    //   625: invokespecial 573	com/oneplus/camera/CameraInfo:getTargetFPSRanges	(Landroid/hardware/camera2/CameraCharacteristics;)Ljava/util/List;
    //   628: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   631: pop
    //   632: aload_0
    //   633: getstatic 317	com/oneplus/camera/CameraInfo:PROP_THUMBNAIL_SIZES	Lcom/oneplus/base/PropertyKey;
    //   636: aload_0
    //   637: aload_1
    //   638: invokespecial 576	com/oneplus/camera/CameraInfo:getThumbnailSizes	(Landroid/hardware/camera2/CameraCharacteristics;)Ljava/util/List;
    //   641: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   644: pop
    //   645: aload_2
    //   646: ifnonnull +16 -> 662
    //   649: aload_0
    //   650: getstatic 301	com/oneplus/camera/CameraInfo:PROP_VIDEO_SIZES	Lcom/oneplus/base/PropertyKey;
    //   653: aload_0
    //   654: aload_1
    //   655: invokespecial 579	com/oneplus/camera/CameraInfo:getVideoSizes	(Landroid/hardware/camera2/CameraCharacteristics;)Ljava/util/List;
    //   658: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   661: pop
    //   662: aload_2
    //   663: ifnull +2196 -> 2859
    //   666: aload_2
    //   667: ldc_w 581
    //   670: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   673: astore_1
    //   674: aload_1
    //   675: ifnull +68 -> 743
    //   678: aload_1
    //   679: ldc_w 587
    //   682: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   685: ifeq +183 -> 868
    //   688: aload_0
    //   689: getstatic 313	com/oneplus/camera/CameraInfo:PROP_LENS_FACING	Lcom/oneplus/base/PropertyKey;
    //   692: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   695: checkcast 136	java/lang/Integer
    //   698: invokevirtual 462	java/lang/Integer:intValue	()I
    //   701: iconst_1
    //   702: if_icmpne +15 -> 717
    //   705: aload_0
    //   706: getstatic 313	com/oneplus/camera/CameraInfo:PROP_LENS_FACING	Lcom/oneplus/base/PropertyKey;
    //   709: iconst_m1
    //   710: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   713: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   716: pop
    //   717: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   720: new 327	java/lang/StringBuilder
    //   723: dup
    //   724: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   727: ldc_w 589
    //   730: invokevirtual 332	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   733: aload_1
    //   734: invokevirtual 332	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   737: invokevirtual 337	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   740: invokestatic 592	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   743: aload_2
    //   744: invokevirtual 596	android/hardware/Camera$Parameters:getSupportedPreviewSizes	()Ljava/util/List;
    //   747: astore_1
    //   748: new 507	java/util/ArrayList
    //   751: dup
    //   752: invokespecial 508	java/util/ArrayList:<init>	()V
    //   755: astore 16
    //   757: iconst_0
    //   758: istore 5
    //   760: iconst_0
    //   761: istore_3
    //   762: aload_1
    //   763: ifnull +148 -> 911
    //   766: iconst_0
    //   767: istore 4
    //   769: aload_1
    //   770: invokeinterface 599 1 0
    //   775: istore 6
    //   777: iload_3
    //   778: istore 5
    //   780: iload 4
    //   782: iload 6
    //   784: if_icmpge +127 -> 911
    //   787: aload_1
    //   788: iload 4
    //   790: invokeinterface 602 2 0
    //   795: checkcast 604	android/hardware/Camera$Size
    //   798: astore 17
    //   800: iload_3
    //   801: istore 5
    //   803: aload 17
    //   805: getfield 607	android/hardware/Camera$Size:width	I
    //   808: sipush 1600
    //   811: if_icmpne +20 -> 831
    //   814: iload_3
    //   815: istore 5
    //   817: aload 17
    //   819: getfield 610	android/hardware/Camera$Size:height	I
    //   822: sipush 1200
    //   825: if_icmpne +6 -> 831
    //   828: iconst_1
    //   829: istore 5
    //   831: aload 16
    //   833: new 259	android/util/Size
    //   836: dup
    //   837: aload 17
    //   839: getfield 607	android/hardware/Camera$Size:width	I
    //   842: aload 17
    //   844: getfield 610	android/hardware/Camera$Size:height	I
    //   847: invokespecial 278	android/util/Size:<init>	(II)V
    //   850: invokeinterface 511 2 0
    //   855: pop
    //   856: iload 4
    //   858: iconst_1
    //   859: iadd
    //   860: istore 4
    //   862: iload 5
    //   864: istore_3
    //   865: goto -88 -> 777
    //   868: aload_1
    //   869: ldc_w 612
    //   872: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   875: ifeq -158 -> 717
    //   878: aload_0
    //   879: getstatic 313	com/oneplus/camera/CameraInfo:PROP_LENS_FACING	Lcom/oneplus/base/PropertyKey;
    //   882: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   885: checkcast 136	java/lang/Integer
    //   888: invokevirtual 462	java/lang/Integer:intValue	()I
    //   891: iconst_1
    //   892: if_icmpne -175 -> 717
    //   895: aload_0
    //   896: getstatic 313	com/oneplus/camera/CameraInfo:PROP_LENS_FACING	Lcom/oneplus/base/PropertyKey;
    //   899: bipush -2
    //   901: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   904: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   907: pop
    //   908: goto -191 -> 717
    //   911: iload 5
    //   913: ifeq +91 -> 1004
    //   916: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   919: ldc_w 614
    //   922: invokestatic 426	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   925: iconst_0
    //   926: istore_3
    //   927: aload_1
    //   928: invokeinterface 599 1 0
    //   933: istore 4
    //   935: iload_3
    //   936: iload 4
    //   938: if_icmpge +66 -> 1004
    //   941: aload_1
    //   942: iload_3
    //   943: invokeinterface 602 2 0
    //   948: checkcast 604	android/hardware/Camera$Size
    //   951: astore 17
    //   953: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   956: new 327	java/lang/StringBuilder
    //   959: dup
    //   960: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   963: ldc_w 616
    //   966: invokevirtual 332	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   969: aload 17
    //   971: getfield 607	android/hardware/Camera$Size:width	I
    //   974: invokevirtual 421	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   977: ldc_w 618
    //   980: invokevirtual 332	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   983: aload 17
    //   985: getfield 610	android/hardware/Camera$Size:height	I
    //   988: invokevirtual 421	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   991: invokevirtual 337	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   994: invokestatic 592	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   997: iload_3
    //   998: iconst_1
    //   999: iadd
    //   1000: istore_3
    //   1001: goto -66 -> 935
    //   1004: aload_0
    //   1005: getstatic 293	com/oneplus/camera/CameraInfo:PROP_SURFACE_SIZES	Lcom/oneplus/base/PropertyKey;
    //   1008: aload 16
    //   1010: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1013: pop
    //   1014: aload_2
    //   1015: invokevirtual 621	android/hardware/Camera$Parameters:getSupportedVideoSizes	()Ljava/util/List;
    //   1018: astore_1
    //   1019: new 507	java/util/ArrayList
    //   1022: dup
    //   1023: invokespecial 508	java/util/ArrayList:<init>	()V
    //   1026: astore 16
    //   1028: aload_1
    //   1029: ifnull +63 -> 1092
    //   1032: iconst_0
    //   1033: istore_3
    //   1034: aload_1
    //   1035: invokeinterface 599 1 0
    //   1040: istore 4
    //   1042: iload_3
    //   1043: iload 4
    //   1045: if_icmpge +47 -> 1092
    //   1048: aload_1
    //   1049: iload_3
    //   1050: invokeinterface 602 2 0
    //   1055: checkcast 604	android/hardware/Camera$Size
    //   1058: astore 17
    //   1060: aload 16
    //   1062: new 259	android/util/Size
    //   1065: dup
    //   1066: aload 17
    //   1068: getfield 607	android/hardware/Camera$Size:width	I
    //   1071: aload 17
    //   1073: getfield 610	android/hardware/Camera$Size:height	I
    //   1076: invokespecial 278	android/util/Size:<init>	(II)V
    //   1079: invokeinterface 511 2 0
    //   1084: pop
    //   1085: iload_3
    //   1086: iconst_1
    //   1087: iadd
    //   1088: istore_3
    //   1089: goto -47 -> 1042
    //   1092: aload_0
    //   1093: getstatic 301	com/oneplus/camera/CameraInfo:PROP_VIDEO_SIZES	Lcom/oneplus/base/PropertyKey;
    //   1096: aload 16
    //   1098: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1101: pop
    //   1102: new 507	java/util/ArrayList
    //   1105: dup
    //   1106: invokespecial 508	java/util/ArrayList:<init>	()V
    //   1109: astore_1
    //   1110: aload_2
    //   1111: ldc_w 623
    //   1114: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   1117: astore 16
    //   1119: aload 16
    //   1121: ifnull +222 -> 1343
    //   1124: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   1127: new 327	java/lang/StringBuilder
    //   1130: dup
    //   1131: invokespecial 328	java/lang/StringBuilder:<init>	()V
    //   1134: ldc_w 625
    //   1137: invokevirtual 332	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1140: aload 16
    //   1142: invokevirtual 332	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1145: invokevirtual 337	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1148: invokestatic 403	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   1151: aload 16
    //   1153: ldc_w 627
    //   1156: invokevirtual 631	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   1159: astore 16
    //   1161: iconst_0
    //   1162: istore_3
    //   1163: aload 16
    //   1165: arraylength
    //   1166: istore 4
    //   1168: iload_3
    //   1169: iload 4
    //   1171: if_icmpge +172 -> 1343
    //   1174: aload 16
    //   1176: iload_3
    //   1177: aaload
    //   1178: astore 18
    //   1180: aload 18
    //   1182: ldc_w 633
    //   1185: invokevirtual 637	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   1188: istore 5
    //   1190: iload 5
    //   1192: ifge +6 -> 1198
    //   1195: goto +2095 -> 3290
    //   1198: aload 18
    //   1200: iconst_0
    //   1201: iload 5
    //   1203: invokevirtual 641	java/lang/String:substring	(II)Ljava/lang/String;
    //   1206: astore 17
    //   1208: aload 18
    //   1210: iload 5
    //   1212: iconst_1
    //   1213: iadd
    //   1214: invokevirtual 644	java/lang/String:substring	(I)Ljava/lang/String;
    //   1217: astore 18
    //   1219: aload_1
    //   1220: new 259	android/util/Size
    //   1223: dup
    //   1224: aload 17
    //   1226: invokestatic 647	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1229: aload 18
    //   1231: invokestatic 647	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1234: invokespecial 278	android/util/Size:<init>	(II)V
    //   1237: invokeinterface 511 2 0
    //   1242: pop
    //   1243: goto +2047 -> 3290
    //   1246: astore_1
    //   1247: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   1250: ldc_w 649
    //   1253: aload_1
    //   1254: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1257: aload_0
    //   1258: getstatic 305	com/oneplus/camera/CameraInfo:PROP_VIDEO_60FPS_SIZES	Lcom/oneplus/base/PropertyKey;
    //   1261: getstatic 106	java/util/Collections:EMPTY_LIST	Ljava/util/List;
    //   1264: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1267: pop
    //   1268: new 507	java/util/ArrayList
    //   1271: dup
    //   1272: invokespecial 508	java/util/ArrayList:<init>	()V
    //   1275: astore_1
    //   1276: aload_1
    //   1277: getstatic 183	com/oneplus/camera/FlashMode:OFF	Lcom/oneplus/camera/FlashMode;
    //   1280: invokeinterface 511 2 0
    //   1285: pop
    //   1286: aload_2
    //   1287: invokevirtual 652	android/hardware/Camera$Parameters:getSupportedFlashModes	()Ljava/util/List;
    //   1290: invokeinterface 658 1 0
    //   1295: astore 16
    //   1297: aload 16
    //   1299: invokeinterface 663 1 0
    //   1304: ifeq +99 -> 1403
    //   1307: aload 16
    //   1309: invokeinterface 667 1 0
    //   1314: checkcast 394	java/lang/String
    //   1317: astore 17
    //   1319: aload 17
    //   1321: ldc_w 669
    //   1324: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1327: ifeq +28 -> 1355
    //   1330: aload_1
    //   1331: getstatic 519	com/oneplus/camera/FlashMode:ON	Lcom/oneplus/camera/FlashMode;
    //   1334: invokeinterface 511 2 0
    //   1339: pop
    //   1340: goto -43 -> 1297
    //   1343: aload_0
    //   1344: getstatic 305	com/oneplus/camera/CameraInfo:PROP_VIDEO_60FPS_SIZES	Lcom/oneplus/base/PropertyKey;
    //   1347: aload_1
    //   1348: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1351: pop
    //   1352: goto -84 -> 1268
    //   1355: aload 17
    //   1357: ldc_w 671
    //   1360: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1363: ifeq +16 -> 1379
    //   1366: aload_1
    //   1367: getstatic 522	com/oneplus/camera/FlashMode:AUTO	Lcom/oneplus/camera/FlashMode;
    //   1370: invokeinterface 511 2 0
    //   1375: pop
    //   1376: goto -79 -> 1297
    //   1379: aload 17
    //   1381: ldc_w 673
    //   1384: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1387: ifeq -90 -> 1297
    //   1390: aload_1
    //   1391: getstatic 525	com/oneplus/camera/FlashMode:TORCH	Lcom/oneplus/camera/FlashMode;
    //   1394: invokeinterface 511 2 0
    //   1399: pop
    //   1400: goto -103 -> 1297
    //   1403: aload_0
    //   1404: getstatic 191	com/oneplus/camera/CameraInfo:PROP_FLASH_MODES	Lcom/oneplus/base/PropertyKey;
    //   1407: aload_1
    //   1408: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   1411: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1414: pop
    //   1415: getstatic 175	com/oneplus/camera/CameraInfo:PROP_FLASH_AVAILABLE	Lcom/oneplus/base/PropertyKey;
    //   1418: astore 16
    //   1420: aload_1
    //   1421: invokeinterface 599 1 0
    //   1426: iconst_1
    //   1427: if_icmple +532 -> 1959
    //   1430: iconst_1
    //   1431: istore 10
    //   1433: aload_0
    //   1434: aload 16
    //   1436: iload 10
    //   1438: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   1441: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1444: pop
    //   1445: aload_2
    //   1446: invokevirtual 676	android/hardware/Camera$Parameters:getSupportedSceneModes	()Ljava/util/List;
    //   1449: ldc_w 678
    //   1452: invokeinterface 681 2 0
    //   1457: ifeq +45 -> 1502
    //   1460: aload_0
    //   1461: getstatic 120	com/oneplus/camera/CameraInfo:PROP_CAPABILITIES	Lcom/oneplus/base/PropertyKey;
    //   1464: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   1467: checkcast 100	java/util/List
    //   1470: iconst_1
    //   1471: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1474: invokeinterface 681 2 0
    //   1479: ifne +23 -> 1502
    //   1482: aload_0
    //   1483: getstatic 120	com/oneplus/camera/CameraInfo:PROP_CAPABILITIES	Lcom/oneplus/base/PropertyKey;
    //   1486: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   1489: checkcast 100	java/util/List
    //   1492: iconst_1
    //   1493: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1496: invokeinterface 511 2 0
    //   1501: pop
    //   1502: iconst_0
    //   1503: istore_3
    //   1504: aload_2
    //   1505: ldc_w 683
    //   1508: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   1511: astore 17
    //   1513: aload_2
    //   1514: ldc_w 685
    //   1517: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   1520: astore_1
    //   1521: aload_2
    //   1522: ldc_w 687
    //   1525: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   1528: astore 16
    //   1530: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   1533: ldc_w 689
    //   1536: aload_1
    //   1537: ldc_w 691
    //   1540: aload 16
    //   1542: ldc_w 693
    //   1545: aload 17
    //   1547: invokestatic 696	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   1550: new 507	java/util/ArrayList
    //   1553: dup
    //   1554: invokespecial 508	java/util/ArrayList:<init>	()V
    //   1557: astore 18
    //   1559: aload 16
    //   1561: ifnull +11 -> 1572
    //   1564: aload 16
    //   1566: invokevirtual 699	java/lang/String:isEmpty	()Z
    //   1569: ifeq +396 -> 1965
    //   1572: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   1575: ldc_w 701
    //   1578: invokestatic 403	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   1581: iload_3
    //   1582: ifne +50 -> 1632
    //   1585: aload_0
    //   1586: getstatic 231	com/oneplus/camera/CameraInfo:PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED	Lcom/oneplus/base/PropertyKey;
    //   1589: iconst_0
    //   1590: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   1593: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1596: pop
    //   1597: aload_0
    //   1598: getstatic 166	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_VALUE_LIST	Lcom/oneplus/base/PropertyKey;
    //   1601: getstatic 106	java/util/Collections:EMPTY_LIST	Ljava/util/List;
    //   1604: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1607: pop
    //   1608: aload_0
    //   1609: getstatic 162	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_VALUE	Lcom/oneplus/base/PropertyKey;
    //   1612: iconst_0
    //   1613: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1616: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1619: pop
    //   1620: aload_0
    //   1621: getstatic 158	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_DEFAULT_VALUE	Lcom/oneplus/base/PropertyKey;
    //   1624: iconst_0
    //   1625: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1628: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1631: pop
    //   1632: aload_2
    //   1633: ldc_w 703
    //   1636: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   1639: astore 16
    //   1641: aload_2
    //   1642: ldc_w 705
    //   1645: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   1648: astore 17
    //   1650: aload 16
    //   1652: ifnull +11 -> 1663
    //   1655: aload 17
    //   1657: astore_1
    //   1658: aload 17
    //   1660: ifnonnull +21 -> 1681
    //   1663: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   1666: ldc_w 707
    //   1669: invokestatic 426	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   1672: ldc_w 709
    //   1675: astore 16
    //   1677: ldc_w 711
    //   1680: astore_1
    //   1681: aload_0
    //   1682: getstatic 200	com/oneplus/camera/CameraInfo:PROP_FOCUS_RANGE	Lcom/oneplus/base/PropertyKey;
    //   1685: new 134	android/util/Range
    //   1688: dup
    //   1689: aload 16
    //   1691: invokestatic 715	java/lang/Float:parseFloat	(Ljava/lang/String;)F
    //   1694: invokestatic 198	java/lang/Float:valueOf	(F)Ljava/lang/Float;
    //   1697: aload_1
    //   1698: invokestatic 715	java/lang/Float:parseFloat	(Ljava/lang/String;)F
    //   1701: invokestatic 198	java/lang/Float:valueOf	(F)Ljava/lang/Float;
    //   1704: invokespecial 143	android/util/Range:<init>	(Ljava/lang/Comparable;Ljava/lang/Comparable;)V
    //   1707: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1710: pop
    //   1711: aload_2
    //   1712: ldc_w 717
    //   1715: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   1718: astore_1
    //   1719: aload_2
    //   1720: ldc_w 719
    //   1723: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   1726: astore 16
    //   1728: aload_1
    //   1729: invokestatic 725	java/lang/Double:parseDouble	(Ljava/lang/String;)D
    //   1732: ldc2_w 726
    //   1735: dmul
    //   1736: invokestatic 733	java/lang/Math:round	(D)J
    //   1739: lstore 12
    //   1741: aload 16
    //   1743: invokestatic 725	java/lang/Double:parseDouble	(Ljava/lang/String;)D
    //   1746: ldc2_w 726
    //   1749: dmul
    //   1750: invokestatic 733	java/lang/Math:round	(D)J
    //   1753: lstore 14
    //   1755: aload_0
    //   1756: getstatic 154	com/oneplus/camera/CameraInfo:PROP_EXPOSURE_TIME_RANGE	Lcom/oneplus/base/PropertyKey;
    //   1759: new 134	android/util/Range
    //   1762: dup
    //   1763: lload 12
    //   1765: invokestatic 152	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   1768: lload 14
    //   1770: invokestatic 152	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   1773: invokespecial 143	android/util/Range:<init>	(Ljava/lang/Comparable;Ljava/lang/Comparable;)V
    //   1776: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1779: pop
    //   1780: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   1783: ldc_w 735
    //   1786: bipush 7
    //   1788: anewarray 737	java/lang/Object
    //   1791: dup
    //   1792: iconst_0
    //   1793: aload_1
    //   1794: aastore
    //   1795: dup
    //   1796: iconst_1
    //   1797: ldc_w 739
    //   1800: aastore
    //   1801: dup
    //   1802: iconst_2
    //   1803: aload 16
    //   1805: aastore
    //   1806: dup
    //   1807: iconst_3
    //   1808: ldc_w 741
    //   1811: aastore
    //   1812: dup
    //   1813: iconst_4
    //   1814: lload 12
    //   1816: invokestatic 152	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   1819: aastore
    //   1820: dup
    //   1821: iconst_5
    //   1822: ldc_w 739
    //   1825: aastore
    //   1826: dup
    //   1827: bipush 6
    //   1829: lload 14
    //   1831: invokestatic 152	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   1834: aastore
    //   1835: invokestatic 744	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V
    //   1838: aload_2
    //   1839: ldc_w 746
    //   1842: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   1845: ldc_w 627
    //   1848: invokevirtual 631	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   1851: astore_1
    //   1852: ldc_w 747
    //   1855: istore_3
    //   1856: ldc_w 748
    //   1859: istore 6
    //   1861: aload_1
    //   1862: arraylength
    //   1863: iconst_1
    //   1864: isub
    //   1865: istore 5
    //   1867: iload 5
    //   1869: iflt +455 -> 2324
    //   1872: aload_1
    //   1873: iload 5
    //   1875: aaload
    //   1876: astore 16
    //   1878: iload 6
    //   1880: istore 9
    //   1882: iload_3
    //   1883: istore 7
    //   1885: aload 16
    //   1887: ldc_w 750
    //   1890: invokevirtual 754	java/lang/String:matches	(Ljava/lang/String;)Z
    //   1893: ifeq +50 -> 1943
    //   1896: aload 16
    //   1898: iconst_3
    //   1899: invokevirtual 644	java/lang/String:substring	(I)Ljava/lang/String;
    //   1902: invokestatic 647	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1905: istore 8
    //   1907: iload_3
    //   1908: istore 4
    //   1910: iload 8
    //   1912: iload_3
    //   1913: if_icmpge +7 -> 1920
    //   1916: iload 8
    //   1918: istore 4
    //   1920: iload 6
    //   1922: istore 9
    //   1924: iload 4
    //   1926: istore 7
    //   1928: iload 8
    //   1930: iload 6
    //   1932: if_icmple +11 -> 1943
    //   1935: iload 4
    //   1937: istore 7
    //   1939: iload 8
    //   1941: istore 9
    //   1943: iload 5
    //   1945: iconst_1
    //   1946: isub
    //   1947: istore 5
    //   1949: iload 9
    //   1951: istore 6
    //   1953: iload 7
    //   1955: istore_3
    //   1956: goto -89 -> 1867
    //   1959: iconst_0
    //   1960: istore 10
    //   1962: goto -529 -> 1433
    //   1965: aload 16
    //   1967: ldc_w 627
    //   1970: invokevirtual 631	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   1973: astore 19
    //   1975: aload_1
    //   1976: astore 16
    //   1978: aload 19
    //   1980: ifnull +69 -> 2049
    //   1983: iconst_0
    //   1984: istore_3
    //   1985: aload_1
    //   1986: astore 16
    //   1988: iload_3
    //   1989: aload 19
    //   1991: arraylength
    //   1992: if_icmpge +57 -> 2049
    //   1995: aload_1
    //   1996: astore 16
    //   1998: aload_1
    //   1999: ifnonnull +22 -> 2021
    //   2002: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   2005: ldc_w 756
    //   2008: aload 19
    //   2010: iload_3
    //   2011: aaload
    //   2012: invokestatic 759	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   2015: aload 19
    //   2017: iload_3
    //   2018: aaload
    //   2019: astore 16
    //   2021: aload 18
    //   2023: aload 19
    //   2025: iload_3
    //   2026: aaload
    //   2027: invokestatic 647	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   2030: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2033: invokeinterface 511 2 0
    //   2038: pop
    //   2039: iload_3
    //   2040: iconst_1
    //   2041: iadd
    //   2042: istore_3
    //   2043: aload 16
    //   2045: astore_1
    //   2046: goto -61 -> 1985
    //   2049: getstatic 231	com/oneplus/camera/CameraInfo:PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED	Lcom/oneplus/base/PropertyKey;
    //   2052: astore_1
    //   2053: aload 17
    //   2055: ifnull +64 -> 2119
    //   2058: iconst_1
    //   2059: istore 10
    //   2061: aload_0
    //   2062: aload_1
    //   2063: iload 10
    //   2065: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   2068: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2071: pop
    //   2072: aload_0
    //   2073: getstatic 166	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_VALUE_LIST	Lcom/oneplus/base/PropertyKey;
    //   2076: aload 18
    //   2078: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2081: pop
    //   2082: aload_0
    //   2083: getstatic 162	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_VALUE	Lcom/oneplus/base/PropertyKey;
    //   2086: aload 16
    //   2088: invokestatic 647	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   2091: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2094: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2097: pop
    //   2098: aload_0
    //   2099: getstatic 158	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_DEFAULT_VALUE	Lcom/oneplus/base/PropertyKey;
    //   2102: aload 16
    //   2104: invokestatic 647	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   2107: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2110: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2113: pop
    //   2114: iconst_1
    //   2115: istore_3
    //   2116: goto -535 -> 1581
    //   2119: iconst_0
    //   2120: istore 10
    //   2122: goto -61 -> 2061
    //   2125: astore_1
    //   2126: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   2129: ldc_w 761
    //   2132: aload_1
    //   2133: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2136: iconst_0
    //   2137: ifne -505 -> 1632
    //   2140: aload_0
    //   2141: getstatic 231	com/oneplus/camera/CameraInfo:PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED	Lcom/oneplus/base/PropertyKey;
    //   2144: iconst_0
    //   2145: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   2148: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2151: pop
    //   2152: aload_0
    //   2153: getstatic 166	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_VALUE_LIST	Lcom/oneplus/base/PropertyKey;
    //   2156: getstatic 106	java/util/Collections:EMPTY_LIST	Ljava/util/List;
    //   2159: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2162: pop
    //   2163: aload_0
    //   2164: getstatic 162	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_VALUE	Lcom/oneplus/base/PropertyKey;
    //   2167: iconst_0
    //   2168: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2171: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2174: pop
    //   2175: aload_0
    //   2176: getstatic 158	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_DEFAULT_VALUE	Lcom/oneplus/base/PropertyKey;
    //   2179: iconst_0
    //   2180: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2183: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2186: pop
    //   2187: goto -555 -> 1632
    //   2190: astore_1
    //   2191: iconst_0
    //   2192: ifne +50 -> 2242
    //   2195: aload_0
    //   2196: getstatic 231	com/oneplus/camera/CameraInfo:PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED	Lcom/oneplus/base/PropertyKey;
    //   2199: iconst_0
    //   2200: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   2203: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2206: pop
    //   2207: aload_0
    //   2208: getstatic 166	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_VALUE_LIST	Lcom/oneplus/base/PropertyKey;
    //   2211: getstatic 106	java/util/Collections:EMPTY_LIST	Ljava/util/List;
    //   2214: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2217: pop
    //   2218: aload_0
    //   2219: getstatic 162	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_VALUE	Lcom/oneplus/base/PropertyKey;
    //   2222: iconst_0
    //   2223: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2226: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2229: pop
    //   2230: aload_0
    //   2231: getstatic 158	com/oneplus/camera/CameraInfo:PROP_FACE_BEAUTY_DEFAULT_VALUE	Lcom/oneplus/base/PropertyKey;
    //   2234: iconst_0
    //   2235: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2238: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2241: pop
    //   2242: aload_1
    //   2243: athrow
    //   2244: astore_1
    //   2245: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   2248: ldc_w 763
    //   2251: aload_1
    //   2252: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2255: aload_0
    //   2256: getstatic 200	com/oneplus/camera/CameraInfo:PROP_FOCUS_RANGE	Lcom/oneplus/base/PropertyKey;
    //   2259: new 134	android/util/Range
    //   2262: dup
    //   2263: fconst_0
    //   2264: invokestatic 198	java/lang/Float:valueOf	(F)Ljava/lang/Float;
    //   2267: ldc_w 764
    //   2270: invokestatic 198	java/lang/Float:valueOf	(F)Ljava/lang/Float;
    //   2273: invokespecial 143	android/util/Range:<init>	(Ljava/lang/Comparable;Ljava/lang/Comparable;)V
    //   2276: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2279: pop
    //   2280: goto -569 -> 1711
    //   2283: astore_1
    //   2284: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   2287: ldc_w 766
    //   2290: aload_1
    //   2291: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2294: aload_0
    //   2295: getstatic 154	com/oneplus/camera/CameraInfo:PROP_EXPOSURE_TIME_RANGE	Lcom/oneplus/base/PropertyKey;
    //   2298: new 134	android/util/Range
    //   2301: dup
    //   2302: ldc2_w 767
    //   2305: invokestatic 152	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   2308: ldc2_w 769
    //   2311: invokestatic 152	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   2314: invokespecial 143	android/util/Range:<init>	(Ljava/lang/Comparable;Ljava/lang/Comparable;)V
    //   2317: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2320: pop
    //   2321: goto -483 -> 1838
    //   2324: aload_0
    //   2325: getstatic 239	com/oneplus/camera/CameraInfo:PROP_ISO_RANGE	Lcom/oneplus/base/PropertyKey;
    //   2328: new 134	android/util/Range
    //   2331: dup
    //   2332: iload_3
    //   2333: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2336: iload 6
    //   2338: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2341: invokespecial 143	android/util/Range:<init>	(Ljava/lang/Comparable;Ljava/lang/Comparable;)V
    //   2344: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2347: pop
    //   2348: new 507	java/util/ArrayList
    //   2351: dup
    //   2352: invokespecial 508	java/util/ArrayList:<init>	()V
    //   2355: astore_1
    //   2356: aload_2
    //   2357: invokevirtual 773	android/hardware/Camera$Parameters:getSupportedWhiteBalance	()Ljava/util/List;
    //   2360: astore 16
    //   2362: aload 16
    //   2364: invokeinterface 599 1 0
    //   2369: iconst_1
    //   2370: isub
    //   2371: istore_3
    //   2372: iload_3
    //   2373: iflt +808 -> 3181
    //   2376: aload 16
    //   2378: iload_3
    //   2379: invokeinterface 602 2 0
    //   2384: checkcast 394	java/lang/String
    //   2387: astore 17
    //   2389: ldc_w 671
    //   2392: aload 17
    //   2394: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2397: ifeq +61 -> 2458
    //   2400: aload_1
    //   2401: iconst_1
    //   2402: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2405: invokeinterface 511 2 0
    //   2410: pop
    //   2411: iload_3
    //   2412: iconst_1
    //   2413: isub
    //   2414: istore_3
    //   2415: goto -43 -> 2372
    //   2418: astore_1
    //   2419: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   2422: ldc_w 775
    //   2425: aload_1
    //   2426: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2429: aload_0
    //   2430: getstatic 239	com/oneplus/camera/CameraInfo:PROP_ISO_RANGE	Lcom/oneplus/base/PropertyKey;
    //   2433: new 134	android/util/Range
    //   2436: dup
    //   2437: bipush 100
    //   2439: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2442: sipush 1600
    //   2445: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2448: invokespecial 143	android/util/Range:<init>	(Ljava/lang/Comparable;Ljava/lang/Comparable;)V
    //   2451: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2454: pop
    //   2455: goto -107 -> 2348
    //   2458: ldc_w 777
    //   2461: aload 17
    //   2463: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2466: ifeq +614 -> 3080
    //   2469: aload_1
    //   2470: bipush 6
    //   2472: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2475: invokeinterface 511 2 0
    //   2480: pop
    //   2481: goto -70 -> 2411
    //   2484: astore_1
    //   2485: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   2488: ldc_w 779
    //   2491: aload_1
    //   2492: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2495: aload_2
    //   2496: ldc_w 781
    //   2499: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   2502: ldc_w 627
    //   2505: invokevirtual 631	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   2508: astore_1
    //   2509: iconst_0
    //   2510: istore 11
    //   2512: iload 11
    //   2514: istore 10
    //   2516: aload_1
    //   2517: ldc_w 783
    //   2520: invokestatic 788	com/oneplus/util/ArrayUtils:contains	([Ljava/lang/Object;Ljava/lang/Object;)Z
    //   2523: ifeq +20 -> 2543
    //   2526: iload 11
    //   2528: istore 10
    //   2530: aload_1
    //   2531: ldc_w 790
    //   2534: invokestatic 788	com/oneplus/util/ArrayUtils:contains	([Ljava/lang/Object;Ljava/lang/Object;)Z
    //   2537: ifeq +6 -> 2543
    //   2540: iconst_1
    //   2541: istore 10
    //   2543: aload_0
    //   2544: getstatic 227	com/oneplus/camera/CameraInfo:PROP_IS_MIRROR_SUPPORTED	Lcom/oneplus/base/PropertyKey;
    //   2547: iload 10
    //   2549: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   2552: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2555: pop
    //   2556: aload_2
    //   2557: ldc_w 792
    //   2560: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   2563: ldc_w 627
    //   2566: invokevirtual 631	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   2569: ldc_w 794
    //   2572: invokestatic 788	com/oneplus/util/ArrayUtils:contains	([Ljava/lang/Object;Ljava/lang/Object;)Z
    //   2575: ifeq +58 -> 2633
    //   2578: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   2581: ldc_w 796
    //   2584: invokestatic 403	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   2587: aload_0
    //   2588: getstatic 265	com/oneplus/camera/CameraInfo:PROP_SCENE_MODES	Lcom/oneplus/base/PropertyKey;
    //   2591: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   2594: checkcast 100	java/util/List
    //   2597: sipush 10001
    //   2600: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2603: invokeinterface 681 2 0
    //   2608: ifne +25 -> 2633
    //   2611: aload_0
    //   2612: getstatic 265	com/oneplus/camera/CameraInfo:PROP_SCENE_MODES	Lcom/oneplus/base/PropertyKey;
    //   2615: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   2618: checkcast 100	java/util/List
    //   2621: sipush 10001
    //   2624: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2627: invokeinterface 511 2 0
    //   2632: pop
    //   2633: aload_2
    //   2634: ldc_w 792
    //   2637: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   2640: astore_1
    //   2641: iconst_0
    //   2642: istore 10
    //   2644: aload_1
    //   2645: ldc_w 627
    //   2648: invokevirtual 631	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   2651: ldc_w 798
    //   2654: invokestatic 788	com/oneplus/util/ArrayUtils:contains	([Ljava/lang/Object;Ljava/lang/Object;)Z
    //   2657: ifeq +15 -> 2672
    //   2660: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   2663: ldc_w 800
    //   2666: invokestatic 403	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   2669: iconst_1
    //   2670: istore 10
    //   2672: aload_0
    //   2673: getstatic 223	com/oneplus/camera/CameraInfo:PROP_IS_HIGH_VIDEO_FRAME_RATE_SUPPORTED	Lcom/oneplus/base/PropertyKey;
    //   2676: iload 10
    //   2678: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   2681: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2684: pop
    //   2685: aload_2
    //   2686: ldc_w 802
    //   2689: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   2692: astore_1
    //   2693: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   2696: ldc_w 804
    //   2699: aload_1
    //   2700: invokestatic 759	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   2703: ldc_w 806
    //   2706: aload_1
    //   2707: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2710: ifeq +525 -> 3235
    //   2713: iconst_0
    //   2714: istore 10
    //   2716: aload_0
    //   2717: getstatic 219	com/oneplus/camera/CameraInfo:PROP_IS_BURST_CAPTURE_SUPPORTED	Lcom/oneplus/base/PropertyKey;
    //   2720: iload 10
    //   2722: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   2725: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2728: pop
    //   2729: ldc_w 808
    //   2732: aload_2
    //   2733: ldc_w 810
    //   2736: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   2739: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2742: istore 10
    //   2744: aload_0
    //   2745: getstatic 215	com/oneplus/camera/CameraInfo:PROP_IS_BOKEH_SUPPORTED	Lcom/oneplus/base/PropertyKey;
    //   2748: iload 10
    //   2750: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   2753: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2756: pop
    //   2757: ldc_w 808
    //   2760: aload_2
    //   2761: ldc_w 812
    //   2764: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   2767: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2770: istore 11
    //   2772: aload_0
    //   2773: getstatic 211	com/oneplus/camera/CameraInfo:PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED	Lcom/oneplus/base/PropertyKey;
    //   2776: iload 11
    //   2778: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   2781: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2784: pop
    //   2785: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   2788: ldc_w 814
    //   2791: iload 10
    //   2793: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   2796: ldc_w 816
    //   2799: iload 11
    //   2801: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   2804: invokestatic 819	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   2807: ldc_w 808
    //   2810: aload_2
    //   2811: ldc_w 821
    //   2814: invokevirtual 585	android/hardware/Camera$Parameters:get	(Ljava/lang/String;)Ljava/lang/String;
    //   2817: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2820: istore 10
    //   2822: aload_0
    //   2823: getstatic 235	com/oneplus/camera/CameraInfo:PROP_IS_WATERMARK_SUPPORTED	Lcom/oneplus/base/PropertyKey;
    //   2826: iload 10
    //   2828: invokestatic 173	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   2831: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2834: pop
    //   2835: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   2838: astore_2
    //   2839: iload 10
    //   2841: ifne +428 -> 3269
    //   2844: ldc_w 823
    //   2847: astore_1
    //   2848: aload_2
    //   2849: ldc_w 825
    //   2852: aload_1
    //   2853: ldc_w 827
    //   2856: invokestatic 438	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   2859: aload_0
    //   2860: getstatic 116	com/oneplus/camera/CameraInfo:PROP_AF_MODES	Lcom/oneplus/base/PropertyKey;
    //   2863: aload_0
    //   2864: getstatic 116	com/oneplus/camera/CameraInfo:PROP_AF_MODES	Lcom/oneplus/base/PropertyKey;
    //   2867: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   2870: checkcast 100	java/util/List
    //   2873: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   2876: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2879: pop
    //   2880: aload_0
    //   2881: getstatic 112	com/oneplus/camera/CameraInfo:PROP_AWB_MODES	Lcom/oneplus/base/PropertyKey;
    //   2884: aload_0
    //   2885: getstatic 112	com/oneplus/camera/CameraInfo:PROP_AWB_MODES	Lcom/oneplus/base/PropertyKey;
    //   2888: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   2891: checkcast 100	java/util/List
    //   2894: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   2897: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2900: pop
    //   2901: aload_0
    //   2902: getstatic 120	com/oneplus/camera/CameraInfo:PROP_CAPABILITIES	Lcom/oneplus/base/PropertyKey;
    //   2905: aload_0
    //   2906: getstatic 120	com/oneplus/camera/CameraInfo:PROP_CAPABILITIES	Lcom/oneplus/base/PropertyKey;
    //   2909: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   2912: checkcast 100	java/util/List
    //   2915: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   2918: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2921: pop
    //   2922: aload_0
    //   2923: getstatic 261	com/oneplus/camera/CameraInfo:PROP_PICTURE_SIZES	Lcom/oneplus/base/PropertyKey;
    //   2926: aload_0
    //   2927: getstatic 261	com/oneplus/camera/CameraInfo:PROP_PICTURE_SIZES	Lcom/oneplus/base/PropertyKey;
    //   2930: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   2933: checkcast 100	java/util/List
    //   2936: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   2939: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2942: pop
    //   2943: aload_0
    //   2944: getstatic 265	com/oneplus/camera/CameraInfo:PROP_SCENE_MODES	Lcom/oneplus/base/PropertyKey;
    //   2947: aload_0
    //   2948: getstatic 265	com/oneplus/camera/CameraInfo:PROP_SCENE_MODES	Lcom/oneplus/base/PropertyKey;
    //   2951: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   2954: checkcast 100	java/util/List
    //   2957: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   2960: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2963: pop
    //   2964: aload_0
    //   2965: getstatic 293	com/oneplus/camera/CameraInfo:PROP_SURFACE_SIZES	Lcom/oneplus/base/PropertyKey;
    //   2968: aload_0
    //   2969: getstatic 293	com/oneplus/camera/CameraInfo:PROP_SURFACE_SIZES	Lcom/oneplus/base/PropertyKey;
    //   2972: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   2975: checkcast 100	java/util/List
    //   2978: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   2981: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   2984: pop
    //   2985: aload_0
    //   2986: getstatic 297	com/oneplus/camera/CameraInfo:PROP_TARGET_FPS_RANGES	Lcom/oneplus/base/PropertyKey;
    //   2989: aload_0
    //   2990: getstatic 297	com/oneplus/camera/CameraInfo:PROP_TARGET_FPS_RANGES	Lcom/oneplus/base/PropertyKey;
    //   2993: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   2996: checkcast 100	java/util/List
    //   2999: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   3002: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   3005: pop
    //   3006: aload_0
    //   3007: getstatic 317	com/oneplus/camera/CameraInfo:PROP_THUMBNAIL_SIZES	Lcom/oneplus/base/PropertyKey;
    //   3010: aload_0
    //   3011: getstatic 317	com/oneplus/camera/CameraInfo:PROP_THUMBNAIL_SIZES	Lcom/oneplus/base/PropertyKey;
    //   3014: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   3017: checkcast 100	java/util/List
    //   3020: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   3023: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   3026: pop
    //   3027: aload_0
    //   3028: getstatic 301	com/oneplus/camera/CameraInfo:PROP_VIDEO_SIZES	Lcom/oneplus/base/PropertyKey;
    //   3031: aload_0
    //   3032: getstatic 301	com/oneplus/camera/CameraInfo:PROP_VIDEO_SIZES	Lcom/oneplus/base/PropertyKey;
    //   3035: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   3038: checkcast 100	java/util/List
    //   3041: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   3044: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   3047: pop
    //   3048: aload_0
    //   3049: getstatic 305	com/oneplus/camera/CameraInfo:PROP_VIDEO_60FPS_SIZES	Lcom/oneplus/base/PropertyKey;
    //   3052: aload_0
    //   3053: getstatic 305	com/oneplus/camera/CameraInfo:PROP_VIDEO_60FPS_SIZES	Lcom/oneplus/base/PropertyKey;
    //   3056: invokevirtual 501	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   3059: checkcast 100	java/util/List
    //   3062: invokestatic 529	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   3065: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   3068: pop
    //   3069: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   3072: ldc_w 829
    //   3075: invokestatic 403	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   3078: iconst_1
    //   3079: ireturn
    //   3080: ldc_w 831
    //   3083: aload 17
    //   3085: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3088: ifeq +17 -> 3105
    //   3091: aload_1
    //   3092: iconst_5
    //   3093: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3096: invokeinterface 511 2 0
    //   3101: pop
    //   3102: goto -691 -> 2411
    //   3105: ldc_w 833
    //   3108: aload 17
    //   3110: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3113: ifeq +17 -> 3130
    //   3116: aload_1
    //   3117: iconst_3
    //   3118: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3121: invokeinterface 511 2 0
    //   3126: pop
    //   3127: goto -716 -> 2411
    //   3130: ldc_w 835
    //   3133: aload 17
    //   3135: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3138: ifeq +17 -> 3155
    //   3141: aload_1
    //   3142: iconst_2
    //   3143: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3146: invokeinterface 511 2 0
    //   3151: pop
    //   3152: goto -741 -> 2411
    //   3155: ldc_w 678
    //   3158: aload 17
    //   3160: invokevirtual 398	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3163: ifeq -752 -> 2411
    //   3166: aload_1
    //   3167: bipush 101
    //   3169: invokestatic 140	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3172: invokeinterface 511 2 0
    //   3177: pop
    //   3178: goto -767 -> 2411
    //   3181: aload_0
    //   3182: getstatic 112	com/oneplus/camera/CameraInfo:PROP_AWB_MODES	Lcom/oneplus/base/PropertyKey;
    //   3185: aload_1
    //   3186: invokevirtual 477	com/oneplus/camera/CameraInfo:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   3189: pop
    //   3190: goto -695 -> 2495
    //   3193: astore_1
    //   3194: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   3197: ldc_w 837
    //   3200: aload_1
    //   3201: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3204: goto -648 -> 2556
    //   3207: astore_1
    //   3208: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   3211: ldc_w 839
    //   3214: aload_1
    //   3215: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3218: goto -585 -> 2633
    //   3221: astore_1
    //   3222: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   3225: ldc_w 841
    //   3228: aload_1
    //   3229: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3232: goto -547 -> 2685
    //   3235: iconst_1
    //   3236: istore 10
    //   3238: goto -522 -> 2716
    //   3241: astore_1
    //   3242: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   3245: ldc_w 843
    //   3248: aload_1
    //   3249: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3252: goto -523 -> 2729
    //   3255: astore_1
    //   3256: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   3259: ldc_w 845
    //   3262: aload_1
    //   3263: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3266: goto -459 -> 2807
    //   3269: ldc_w 847
    //   3272: astore_1
    //   3273: goto -425 -> 2848
    //   3276: astore_1
    //   3277: getstatic 325	com/oneplus/camera/CameraInfo:TAG	Ljava/lang/String;
    //   3280: ldc_w 849
    //   3283: aload_1
    //   3284: invokestatic 448	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3287: goto -428 -> 2859
    //   3290: iload_3
    //   3291: iconst_1
    //   3292: iadd
    //   3293: istore_3
    //   3294: goto -2126 -> 1168
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	3297	0	this	CameraInfo
    //   0	3297	1	paramCameraManager	CameraManager
    //   0	3297	2	paramString	String
    //   63	3231	3	i	int
    //   269	1667	4	j	int
    //   758	1190	5	k	int
    //   775	1562	6	m	int
    //   1883	71	7	n	int
    //   1905	35	8	i1	int
    //   1880	70	9	i2	int
    //   1431	1806	10	bool1	boolean
    //   2510	290	11	bool2	boolean
    //   1739	76	12	l1	long
    //   1753	77	14	l2	long
    //   14	2363	16	localObject1	Object
    //   262	2897	17	localObject2	Object
    //   1178	899	18	localObject3	Object
    //   1973	51	19	arrayOfString	String[]
    // Exception table:
    //   from	to	target	type
    //   16	22	28	java/lang/Throwable
    //   1102	1119	1246	java/lang/Throwable
    //   1124	1161	1246	java/lang/Throwable
    //   1163	1168	1246	java/lang/Throwable
    //   1180	1190	1246	java/lang/Throwable
    //   1198	1243	1246	java/lang/Throwable
    //   1343	1352	1246	java/lang/Throwable
    //   1504	1559	2125	java/lang/Throwable
    //   1564	1572	2125	java/lang/Throwable
    //   1572	1581	2125	java/lang/Throwable
    //   1965	1975	2125	java/lang/Throwable
    //   1988	1995	2125	java/lang/Throwable
    //   2002	2015	2125	java/lang/Throwable
    //   2021	2039	2125	java/lang/Throwable
    //   2049	2053	2125	java/lang/Throwable
    //   2061	2114	2125	java/lang/Throwable
    //   1504	1559	2190	finally
    //   1564	1572	2190	finally
    //   1572	1581	2190	finally
    //   1965	1975	2190	finally
    //   1988	1995	2190	finally
    //   2002	2015	2190	finally
    //   2021	2039	2190	finally
    //   2049	2053	2190	finally
    //   2061	2114	2190	finally
    //   2126	2136	2190	finally
    //   1632	1650	2244	java/lang/Throwable
    //   1663	1672	2244	java/lang/Throwable
    //   1681	1711	2244	java/lang/Throwable
    //   1711	1838	2283	java/lang/Throwable
    //   1838	1852	2418	java/lang/Throwable
    //   1861	1867	2418	java/lang/Throwable
    //   1885	1907	2418	java/lang/Throwable
    //   2324	2348	2418	java/lang/Throwable
    //   2348	2372	2484	java/lang/Throwable
    //   2376	2411	2484	java/lang/Throwable
    //   2458	2481	2484	java/lang/Throwable
    //   3080	3102	2484	java/lang/Throwable
    //   3105	3127	2484	java/lang/Throwable
    //   3130	3152	2484	java/lang/Throwable
    //   3155	3178	2484	java/lang/Throwable
    //   3181	3190	2484	java/lang/Throwable
    //   2495	2509	3193	java/lang/Throwable
    //   2516	2526	3193	java/lang/Throwable
    //   2530	2540	3193	java/lang/Throwable
    //   2543	2556	3193	java/lang/Throwable
    //   2556	2633	3207	java/lang/Throwable
    //   2633	2641	3221	java/lang/Throwable
    //   2644	2669	3221	java/lang/Throwable
    //   2672	2685	3221	java/lang/Throwable
    //   2685	2713	3241	java/lang/Throwable
    //   2716	2729	3241	java/lang/Throwable
    //   2729	2807	3255	java/lang/Throwable
    //   2807	2839	3276	java/lang/Throwable
    //   2848	2859	3276	java/lang/Throwable
  }
  
  private Camera.Parameters getCameraParameters(String paramString)
  {
    try
    {
      paramString = Camera.open(Integer.parseInt(paramString));
      Camera.Parameters localParameters = paramString.getParameters();
      paramString.release();
      return localParameters;
    }
    catch (Throwable paramString)
    {
      Log.e(TAG, "getCameraParameters() - Error when get camera parameters", paramString);
    }
    return null;
  }
  
  private Range<Float> getFocusRange(CameraCharacteristics paramCameraCharacteristics)
  {
    Float localFloat = (Float)paramCameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
    paramCameraCharacteristics = localFloat;
    if (localFloat == null) {
      paramCameraCharacteristics = Float.valueOf(0.0F);
    }
    return new Range(Float.valueOf(0.0F), paramCameraCharacteristics);
  }
  
  private List<Integer> getIntListChars(CameraCharacteristics paramCameraCharacteristics, CameraCharacteristics.Key<int[]> paramKey)
  {
    paramCameraCharacteristics = (int[])paramCameraCharacteristics.get(paramKey);
    paramKey = new ArrayList();
    if (paramCameraCharacteristics == null) {
      paramKey.add(Integer.valueOf(0));
    }
    for (;;)
    {
      return paramKey;
      int i = 0;
      while (i < paramCameraCharacteristics.length)
      {
        paramKey.add(Integer.valueOf(paramCameraCharacteristics[i]));
        i += 1;
      }
    }
  }
  
  private List<Size> getPictureSizes(CameraCharacteristics paramCameraCharacteristics)
  {
    paramCameraCharacteristics = ((StreamConfigurationMap)paramCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(256);
    ArrayList localArrayList = new ArrayList();
    if (paramCameraCharacteristics == null) {
      localArrayList.add(new Size(0, 0));
    }
    for (;;)
    {
      return localArrayList;
      int i = 0;
      while (i < paramCameraCharacteristics.length)
      {
        localArrayList.add(paramCameraCharacteristics[i]);
        i += 1;
      }
    }
  }
  
  private List<Size> getSurfaceSizes(CameraCharacteristics paramCameraCharacteristics)
  {
    paramCameraCharacteristics = ((StreamConfigurationMap)paramCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(SurfaceHolder.class);
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    int k = 0;
    if (paramCameraCharacteristics == null)
    {
      localArrayList.add(new Size(0, 0));
      if (k != 0)
      {
        Log.w(TAG, "getSurfaceSizes() - invalidPreviewSizeFound[1600x1200], list all previewSize.");
        i = 0;
        while (i < paramCameraCharacteristics.length)
        {
          Log.d(TAG, "getSurfaceSizes() - Camera2 PROP_SURFACE_SIZES values[" + i + "] size:" + paramCameraCharacteristics[i].toString());
          i += 1;
        }
      }
    }
    else
    {
      int j = 0;
      for (;;)
      {
        k = i;
        if (j >= paramCameraCharacteristics.length) {
          break;
        }
        if ("1600x1200".equals(paramCameraCharacteristics[j].toString())) {
          i = 1;
        }
        localArrayList.add(paramCameraCharacteristics[j]);
        j += 1;
      }
    }
    return localArrayList;
  }
  
  private List<Range<Integer>> getTargetFPSRanges(CameraCharacteristics paramCameraCharacteristics)
  {
    paramCameraCharacteristics = (Range[])paramCameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
    ArrayList localArrayList = new ArrayList();
    if (paramCameraCharacteristics == null) {
      localArrayList.add(new Range(Integer.valueOf(0), Integer.valueOf(0)));
    }
    for (;;)
    {
      return localArrayList;
      int i = 0;
      while (i < paramCameraCharacteristics.length)
      {
        localArrayList.add(paramCameraCharacteristics[i]);
        i += 1;
      }
    }
  }
  
  private List<Size> getThumbnailSizes(CameraCharacteristics paramCameraCharacteristics)
  {
    paramCameraCharacteristics = (Size[])paramCameraCharacteristics.get(CameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES);
    ArrayList localArrayList = new ArrayList();
    if (paramCameraCharacteristics == null) {
      localArrayList.add(new Size(0, 0));
    }
    for (;;)
    {
      return localArrayList;
      int i = 0;
      while (i < paramCameraCharacteristics.length)
      {
        localArrayList.add(paramCameraCharacteristics[i]);
        i += 1;
      }
    }
  }
  
  private List<Size> getVideoSizes(CameraCharacteristics paramCameraCharacteristics)
  {
    paramCameraCharacteristics = ((StreamConfigurationMap)paramCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(MediaRecorder.class);
    ArrayList localArrayList = new ArrayList();
    if (paramCameraCharacteristics == null) {
      localArrayList.add(new Size(0, 0));
    }
    for (;;)
    {
      return localArrayList;
      int i = 0;
      while (i < paramCameraCharacteristics.length)
      {
        localArrayList.add(paramCameraCharacteristics[i]);
        i += 1;
      }
    }
  }
  
  private long loadCharacteristics(String paramString)
  {
    Log.v(TAG, "loadCharacteristics() - Start [" + paramString + "]");
    setReadOnly(PROP_AF_MODES, loadIntegerListFromPrefs(PROP_AF_MODES.name));
    long l1 = ListUtils.sumOfIntList((List)get(PROP_AF_MODES));
    setReadOnly(PROP_AWB_MODES, loadIntegerListFromPrefs(PROP_AWB_MODES.name));
    long l2 = ListUtils.sumOfIntList((List)get(PROP_AWB_MODES));
    setReadOnly(PROP_CAPABILITIES, loadIntegerListFromPrefs(PROP_CAPABILITIES.name));
    long l3 = ListUtils.sumOfIntList((List)get(PROP_CAPABILITIES));
    int i = this.m_CharsPreference.getInt(PROP_FACE_BEAUTY_DEFAULT_VALUE.name, 0);
    long l4 = i;
    setReadOnly(PROP_FACE_BEAUTY_DEFAULT_VALUE, Integer.valueOf(i));
    paramString = Rational.parseRational(this.m_CharsPreference.getString(PROP_EV_STEP.name, null));
    long l5 = paramString.hashCode();
    setReadOnly(PROP_EV_STEP, paramString);
    paramString = this.m_CharsPreference.getString(PROP_EXPOSURE_COMP_RANGE.name, null);
    long l6 = paramString.hashCode();
    paramString = paramString.split(",");
    paramString = new Range(Integer.valueOf(paramString[0]), Integer.valueOf(paramString[1]));
    setReadOnly(PROP_EXPOSURE_COMP_RANGE, paramString);
    paramString = this.m_CharsPreference.getString(PROP_EXPOSURE_TIME_RANGE.name, null);
    long l7 = paramString.hashCode();
    paramString = paramString.split(",");
    paramString = new Range(Long.valueOf(paramString[0]), Long.valueOf(paramString[1]));
    setReadOnly(PROP_EXPOSURE_TIME_RANGE, paramString);
    paramString = loadIntegerListFromPrefs(PROP_FACE_BEAUTY_VALUE_LIST.name);
    Collections.sort(paramString);
    setReadOnly(PROP_FACE_BEAUTY_VALUE_LIST, paramString);
    long l8 = ListUtils.sumOfIntList((List)get(PROP_FACE_BEAUTY_VALUE_LIST));
    i = this.m_CharsPreference.getInt(PROP_FACE_BEAUTY_VALUE.name, 0);
    long l9 = i;
    setReadOnly(PROP_FACE_BEAUTY_VALUE, Integer.valueOf(i));
    i = this.m_CharsPreference.getInt(PROP_MAX_FACE_COUNT.name, -1);
    long l10 = i;
    setReadOnly(PROP_MAX_FACE_COUNT, Integer.valueOf(i));
    paramString = Boolean.valueOf(this.m_CharsPreference.getBoolean(PROP_FLASH_AVAILABLE.name, Boolean.FALSE.booleanValue()));
    long l11 = paramString.hashCode();
    setReadOnly(PROP_FLASH_AVAILABLE, paramString);
    Object localObject1 = this.m_CharsPreference.getStringSet(PROP_FLASH_MODES.name, Collections.EMPTY_SET);
    l2 = 0L + l1 + l2 + l3 + l4 + l5 + l6 + l7 + l8 + l9 + l10 + l11 + ((Set)localObject1).hashCode();
    paramString = new ArrayList();
    localObject1 = ((Iterable)localObject1).iterator();
    String str;
    for (;;)
    {
      l1 = l2;
      if (((Iterator)localObject1).hasNext())
      {
        str = (String)((Iterator)localObject1).next();
        try
        {
          paramString.add((FlashMode)Enum.valueOf(FlashMode.class, str));
        }
        catch (Throwable localThrowable)
        {
          l1 = 0L;
        }
      }
    }
    setReadOnly(PROP_FLASH_MODES, Collections.unmodifiableList(paramString));
    paramString = this.m_CharsPreference.getString(PROP_FOCUS_RANGE.name, null);
    l2 = paramString.hashCode();
    paramString = paramString.split(",");
    paramString = new Range(Float.valueOf(paramString[0]), Float.valueOf(paramString[1]));
    setReadOnly(PROP_FOCUS_RANGE, paramString);
    i = this.m_CharsPreference.getInt(PROP_HARDWARE_LEVEL.name, -1);
    l3 = i;
    setReadOnly(PROP_HARDWARE_LEVEL, Integer.valueOf(i));
    paramString = Boolean.valueOf(this.m_CharsPreference.getBoolean(PROP_IS_BOKEH_SUPPORTED.name, false));
    l4 = paramString.hashCode();
    setReadOnly(PROP_IS_BOKEH_SUPPORTED, paramString);
    paramString = Boolean.valueOf(this.m_CharsPreference.getBoolean(PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED.name, false));
    l5 = paramString.hashCode();
    setReadOnly(PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED, paramString);
    paramString = Boolean.valueOf(this.m_CharsPreference.getBoolean(PROP_IS_BURST_CAPTURE_SUPPORTED.name, Boolean.TRUE.booleanValue()));
    l6 = paramString.hashCode();
    setReadOnly(PROP_IS_BURST_CAPTURE_SUPPORTED, paramString);
    paramString = Boolean.valueOf(this.m_CharsPreference.getBoolean(PROP_IS_MIRROR_SUPPORTED.name, Boolean.FALSE.booleanValue()));
    l7 = paramString.hashCode();
    setReadOnly(PROP_IS_MIRROR_SUPPORTED, paramString);
    paramString = Boolean.valueOf(this.m_CharsPreference.getBoolean(PROP_IS_HIGH_VIDEO_FRAME_RATE_SUPPORTED.name, Boolean.FALSE.booleanValue()));
    l8 = paramString.hashCode();
    setReadOnly(PROP_IS_HIGH_VIDEO_FRAME_RATE_SUPPORTED, paramString);
    paramString = Boolean.valueOf(this.m_CharsPreference.getBoolean(PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED.name, Boolean.FALSE.booleanValue()));
    l9 = paramString.hashCode();
    setReadOnly(PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED, paramString);
    paramString = Boolean.valueOf(this.m_CharsPreference.getBoolean(PROP_IS_WATERMARK_SUPPORTED.name, false));
    l10 = paramString.hashCode();
    setReadOnly(PROP_IS_WATERMARK_SUPPORTED, paramString);
    paramString = this.m_CharsPreference.getString(PROP_ISO_RANGE.name, null);
    l11 = paramString.hashCode();
    paramString = paramString.split(",");
    paramString = new Range(Integer.valueOf(paramString[0]), Integer.valueOf(paramString[1]));
    setReadOnly(PROP_ISO_RANGE, paramString);
    i = this.m_CharsPreference.getInt(PROP_LENS_FACING.name, 1);
    long l12 = i;
    setReadOnly(PROP_LENS_FACING, Integer.valueOf(i));
    i = this.m_CharsPreference.getInt(PROP_MAX_AE_COUNT.name, -1);
    long l13 = i;
    setReadOnly(PROP_MAX_AE_COUNT, Integer.valueOf(i));
    i = this.m_CharsPreference.getInt(PROP_MAX_AF_COUNT.name, -1);
    long l14 = i;
    setReadOnly(PROP_MAX_AF_COUNT, Integer.valueOf(i));
    float f = this.m_CharsPreference.getFloat(PROP_MAX_DIGITAL_ZOOM.name, -1.0F);
    l1 = ((float)(l1 + l2 + l3 + l4 + l5 + l6 + l7 + l8 + l9 + l10 + l11 + l12 + l13 + l14) + f);
    setReadOnly(PROP_MAX_DIGITAL_ZOOM, Float.valueOf(f));
    setReadOnly(PROP_PICTURE_SIZES, loadSizeListFromPrefs(PROP_PICTURE_SIZES.name));
    paramString = ((List)get(PROP_PICTURE_SIZES)).iterator();
    while (paramString.hasNext()) {
      l1 += ((Size)paramString.next()).hashCode();
    }
    setReadOnly(PROP_SCENE_MODES, loadIntegerListFromPrefs(PROP_SCENE_MODES.name));
    l2 = ListUtils.sumOfIntList((List)get(PROP_SCENE_MODES));
    i = this.m_CharsPreference.getInt(PROP_SENSOR_ORIENTATION.name, -1);
    l3 = i;
    setReadOnly(PROP_SENSOR_ORIENTATION, Integer.valueOf(i));
    paramString = SizeF.parseSizeF(this.m_CharsPreference.getString(PROP_SENSOR_PHYSICAL_SIZE.name, null));
    l4 = paramString.hashCode();
    setReadOnly(PROP_SENSOR_PHYSICAL_SIZE, paramString);
    paramString = Size.parseSize(this.m_CharsPreference.getString(PROP_SENSOR_PIXEL_SIZE_FULL.name, null));
    l5 = paramString.hashCode();
    setReadOnly(PROP_SENSOR_PIXEL_SIZE_FULL, paramString);
    paramString = Rect.unflattenFromString(this.m_CharsPreference.getString(PROP_SENSOR_RECT.name, null));
    l1 = l1 + l2 + l3 + l4 + l5 + paramString.hashCode();
    setReadOnly(PROP_SENSOR_RECT, paramString);
    setReadOnly(PROP_SURFACE_SIZES, loadSizeListFromPrefs(PROP_SURFACE_SIZES.name));
    paramString = ((List)get(PROP_SURFACE_SIZES)).iterator();
    while (paramString.hasNext()) {
      l1 += ((Size)paramString.next()).hashCode();
    }
    paramString = new ArrayList();
    Object localObject2 = this.m_CharsPreference.getStringSet(PROP_TARGET_FPS_RANGES.name, null);
    l2 = l1;
    if (localObject2 != null)
    {
      if (((Set)localObject2).isEmpty()) {
        l2 = l1;
      }
    }
    else
    {
      setReadOnly(PROP_TARGET_FPS_RANGES, paramString);
      setReadOnly(PROP_THUMBNAIL_SIZES, loadSizeListFromPrefs(PROP_THUMBNAIL_SIZES.name));
      paramString = ((List)get(PROP_THUMBNAIL_SIZES)).iterator();
      while (paramString.hasNext()) {
        l2 += ((Size)paramString.next()).hashCode();
      }
    }
    localObject2 = ((Iterable)localObject2).iterator();
    for (;;)
    {
      l2 = l1;
      if (!((Iterator)localObject2).hasNext()) {
        break;
      }
      str = (String)((Iterator)localObject2).next();
      String[] arrayOfString = str.split(",");
      paramString.add(new Range(Integer.valueOf(arrayOfString[0]), Integer.valueOf(arrayOfString[1])));
      l1 += str.hashCode();
    }
    setReadOnly(PROP_VIDEO_SIZES, loadSizeListFromPrefs(PROP_VIDEO_SIZES.name));
    paramString = ((List)get(PROP_VIDEO_SIZES)).iterator();
    while (paramString.hasNext()) {
      l2 += ((Size)paramString.next()).hashCode();
    }
    setReadOnly(PROP_VIDEO_60FPS_SIZES, loadSizeListFromPrefs(PROP_VIDEO_60FPS_SIZES.name));
    paramString = ((List)get(PROP_VIDEO_60FPS_SIZES)).iterator();
    while (paramString.hasNext()) {
      l2 += ((Size)paramString.next()).hashCode();
    }
    Log.v(TAG, "loadCharacteristics() - End, hash code: ", Long.toString(l2));
    return l2;
  }
  
  private List<Integer> loadIntegerListFromPrefs(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    paramString = this.m_CharsPreference.getStringSet(paramString, null);
    if (paramString == null) {
      localArrayList.add(Integer.valueOf(0));
    }
    for (;;)
    {
      return localArrayList;
      paramString = paramString.iterator();
      while (paramString.hasNext()) {
        localArrayList.add(Integer.valueOf((String)paramString.next()));
      }
    }
  }
  
  private List<Size> loadSizeListFromPrefs(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject = this.m_CharsPreference.getStringSet(paramString, null);
    int i = 0;
    int j = 0;
    if (localObject == null)
    {
      localArrayList.add(new Size(0, 0));
      if (j != 0)
      {
        Log.w(TAG, "loadSizeListFromPrefs() - invalidPreviewSizeFound[1600x1200], list all previewSize.");
        paramString = ((Iterable)localObject).iterator();
        while (paramString.hasNext())
        {
          localObject = (String)paramString.next();
          Log.d(TAG, "loadSizeListFromPrefs key: " + PROP_SURFACE_SIZES.name + " size:" + (String)localObject);
        }
      }
    }
    else
    {
      Iterator localIterator = ((Iterable)localObject).iterator();
      for (;;)
      {
        j = i;
        if (!localIterator.hasNext()) {
          break;
        }
        String str = (String)localIterator.next();
        if ((PROP_SURFACE_SIZES.name.equals(paramString)) && ("1600x1200".equals(str))) {
          i = 1;
        } else {
          localArrayList.add(Size.parseSize(str));
        }
      }
    }
    return localArrayList;
  }
  
  private void saveCharacteristics(CameraCharacteristics paramCameraCharacteristics)
  {
    Log.v(TAG, "saveCharacteristics() - Start");
    long l2 = 0L;
    paramCameraCharacteristics = Device.getSystemProperty("ro.build.date.YmdHM");
    SharedPreferences.Editor localEditor = this.m_CharsPreference.edit();
    localEditor.clear();
    Object localObject1 = (List)get(PROP_AF_MODES);
    if (((List)localObject1).size() > 0)
    {
      l2 = 0L + ListUtils.sumOfIntList((List)localObject1);
      localEditor.putStringSet(PROP_AF_MODES.name, ListUtils.toStringSet((List)localObject1));
    }
    localObject1 = (List)get(PROP_AWB_MODES);
    long l1 = l2;
    if (((List)localObject1).size() > 0)
    {
      l1 = l2 + ListUtils.sumOfIntList((List)localObject1);
      localEditor.putStringSet(PROP_AWB_MODES.name, ListUtils.toStringSet((List)localObject1));
    }
    localObject1 = (List)get(PROP_CAPABILITIES);
    l2 = l1;
    if (((List)localObject1).size() > 0)
    {
      l2 = l1 + ListUtils.sumOfIntList((List)localObject1);
      localEditor.putStringSet(PROP_CAPABILITIES.name, ListUtils.toStringSet((List)localObject1));
    }
    localObject1 = (Integer)get(PROP_FACE_BEAUTY_DEFAULT_VALUE);
    localEditor.putInt(PROP_FACE_BEAUTY_DEFAULT_VALUE.name, ((Integer)localObject1).intValue());
    l2 += ((Integer)localObject1).hashCode();
    localObject1 = (Rational)get(PROP_EV_STEP);
    l1 = l2;
    if (localObject1 != null)
    {
      localEditor.putString(PROP_EV_STEP.name, ((Rational)localObject1).toString());
      l1 = l2 + ((Rational)localObject1).hashCode();
    }
    localObject1 = (Range)get(PROP_EXPOSURE_COMP_RANGE);
    localObject1 = ((Range)localObject1).getLower() + "," + ((Range)localObject1).getUpper();
    localEditor.putString(PROP_EXPOSURE_COMP_RANGE.name, (String)localObject1);
    l2 = ((String)localObject1).hashCode();
    localObject1 = (Range)get(PROP_EXPOSURE_TIME_RANGE);
    localObject1 = ((Range)localObject1).getLower() + "," + ((Range)localObject1).getUpper();
    localEditor.putString(PROP_EXPOSURE_TIME_RANGE.name, (String)localObject1);
    l2 = l1 + l2 + ((String)localObject1).hashCode();
    localObject1 = (List)get(PROP_FACE_BEAUTY_VALUE_LIST);
    l1 = l2;
    if (((List)localObject1).size() > 0)
    {
      l1 = l2 + ListUtils.sumOfIntList((List)localObject1);
      localEditor.putStringSet(PROP_FACE_BEAUTY_VALUE_LIST.name, ListUtils.toStringSet((List)localObject1));
    }
    localObject1 = (Integer)get(PROP_FACE_BEAUTY_VALUE);
    localEditor.putInt(PROP_FACE_BEAUTY_VALUE.name, ((Integer)localObject1).intValue());
    l2 = ((Integer)localObject1).hashCode();
    localObject1 = (Integer)get(PROP_MAX_FACE_COUNT);
    localEditor.putInt(PROP_MAX_FACE_COUNT.name, ((Integer)localObject1).intValue());
    long l3 = ((Integer)localObject1).hashCode();
    localObject1 = (Boolean)get(PROP_FLASH_AVAILABLE);
    localEditor.putBoolean(PROP_FLASH_AVAILABLE.name, ((Boolean)localObject1).booleanValue());
    long l4 = ((Boolean)localObject1).hashCode();
    localObject1 = new HashSet();
    Object localObject2 = ((List)get(PROP_FLASH_MODES)).iterator();
    while (((Iterator)localObject2).hasNext()) {
      ((Set)localObject1).add(((FlashMode)((Iterator)localObject2).next()).toString());
    }
    localEditor.putStringSet(PROP_FLASH_MODES.name, (Set)localObject1);
    long l5 = ((Set)localObject1).hashCode();
    localObject1 = (Range)get(PROP_FOCUS_RANGE);
    localObject1 = ((Range)localObject1).getLower() + "," + ((Range)localObject1).getUpper();
    localEditor.putString(PROP_FOCUS_RANGE.name, (String)localObject1);
    long l6 = ((String)localObject1).hashCode();
    localObject1 = (Integer)get(PROP_HARDWARE_LEVEL);
    localEditor.putInt(PROP_HARDWARE_LEVEL.name, ((Integer)localObject1).intValue());
    long l7 = ((Integer)localObject1).hashCode();
    localObject1 = (Boolean)get(PROP_IS_BOKEH_SUPPORTED);
    localEditor.putBoolean(PROP_IS_BOKEH_SUPPORTED.name, ((Boolean)localObject1).booleanValue());
    long l8 = ((Boolean)localObject1).hashCode();
    localObject1 = (Boolean)get(PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED);
    localEditor.putBoolean(PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED.name, ((Boolean)localObject1).booleanValue());
    long l9 = ((Boolean)localObject1).hashCode();
    localObject1 = (Boolean)get(PROP_IS_BURST_CAPTURE_SUPPORTED);
    localEditor.putBoolean(PROP_IS_BURST_CAPTURE_SUPPORTED.name, ((Boolean)localObject1).booleanValue());
    long l10 = ((Boolean)localObject1).hashCode();
    localObject1 = (Boolean)get(PROP_IS_MIRROR_SUPPORTED);
    localEditor.putBoolean(PROP_IS_MIRROR_SUPPORTED.name, ((Boolean)localObject1).booleanValue());
    long l11 = ((Boolean)localObject1).hashCode();
    localObject1 = (Boolean)get(PROP_IS_HIGH_VIDEO_FRAME_RATE_SUPPORTED);
    localEditor.putBoolean(PROP_IS_HIGH_VIDEO_FRAME_RATE_SUPPORTED.name, ((Boolean)localObject1).booleanValue());
    long l12 = ((Boolean)localObject1).hashCode();
    localObject1 = (Boolean)get(PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED);
    localEditor.putBoolean(PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED.name, ((Boolean)localObject1).booleanValue());
    long l13 = ((Boolean)localObject1).hashCode();
    localObject1 = (Boolean)get(PROP_IS_WATERMARK_SUPPORTED);
    localEditor.putBoolean(PROP_IS_WATERMARK_SUPPORTED.name, ((Boolean)localObject1).booleanValue());
    long l14 = ((Boolean)localObject1).hashCode();
    localObject1 = (Range)get(PROP_ISO_RANGE);
    localObject1 = ((Range)localObject1).getLower() + "," + ((Range)localObject1).getUpper();
    localEditor.putString(PROP_ISO_RANGE.name, (String)localObject1);
    long l15 = ((String)localObject1).hashCode();
    localObject1 = (Integer)get(PROP_LENS_FACING);
    localEditor.putInt(PROP_LENS_FACING.name, ((Integer)localObject1).intValue());
    long l16 = ((Integer)localObject1).hashCode();
    localObject1 = (Integer)get(PROP_MAX_AE_COUNT);
    localEditor.putInt(PROP_MAX_AE_COUNT.name, ((Integer)localObject1).intValue());
    long l17 = ((Integer)localObject1).hashCode();
    localObject1 = (Integer)get(PROP_MAX_AF_COUNT);
    localEditor.putInt(PROP_MAX_AF_COUNT.name, ((Integer)localObject1).intValue());
    l2 = l1 + l2 + l3 + l4 + l5 + l6 + l7 + l8 + l9 + l10 + l11 + l12 + l13 + l14 + l15 + l16 + l17 + ((Integer)localObject1).hashCode();
    localObject1 = (Float)get(PROP_MAX_DIGITAL_ZOOM);
    l1 = l2;
    if (localObject1 != null)
    {
      localEditor.putFloat(PROP_MAX_DIGITAL_ZOOM.name, ((Float)localObject1).floatValue());
      l1 = ((float)l2 + ((Float)localObject1).floatValue());
    }
    localObject2 = (List)get(PROP_PICTURE_SIZES);
    l2 = l1;
    Object localObject3;
    if (((List)localObject2).size() > 0)
    {
      localObject1 = new HashSet();
      localObject2 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Size)((Iterator)localObject2).next();
        ((Set)localObject1).add(((Size)localObject3).toString());
        l1 += ((Size)localObject3).hashCode();
      }
      localEditor.putStringSet(PROP_PICTURE_SIZES.name, (Set)localObject1);
      l2 = l1;
    }
    localObject1 = (List)get(PROP_SCENE_MODES);
    l1 = l2;
    if (((List)localObject1).size() > 0)
    {
      l1 = l2 + ListUtils.sumOfIntList((List)localObject1);
      localEditor.putStringSet(PROP_SCENE_MODES.name, ListUtils.toStringSet((List)localObject1));
    }
    localObject1 = (Integer)get(PROP_SENSOR_ORIENTATION);
    localEditor.putInt(PROP_SENSOR_ORIENTATION.name, ((Integer)localObject1).intValue());
    l2 = ((Integer)localObject1).hashCode();
    localObject1 = (SizeF)get(PROP_SENSOR_PHYSICAL_SIZE);
    localObject2 = ((SizeF)localObject1).toString();
    localEditor.putString(PROP_SENSOR_PHYSICAL_SIZE.name, (String)localObject2);
    l3 = ((SizeF)localObject1).hashCode();
    localObject1 = (Size)get(PROP_SENSOR_PIXEL_SIZE_FULL);
    localObject2 = ((Size)localObject1).toString();
    localEditor.putString(PROP_SENSOR_PIXEL_SIZE_FULL.name, (String)localObject2);
    l4 = ((Size)localObject1).hashCode();
    localObject1 = (Rect)get(PROP_SENSOR_RECT);
    localObject2 = ((Rect)localObject1).flattenToString();
    localEditor.putString(PROP_SENSOR_RECT.name, (String)localObject2);
    l2 = l1 + l2 + l3 + l4 + ((Rect)localObject1).hashCode();
    localObject2 = (List)get(PROP_SURFACE_SIZES);
    l1 = l2;
    if (((List)localObject2).size() > 0)
    {
      int i = 0;
      localObject1 = new HashSet();
      localObject3 = ((Iterable)localObject2).iterator();
      Size localSize;
      for (l1 = l2; ((Iterator)localObject3).hasNext(); l1 += localSize.hashCode())
      {
        localSize = (Size)((Iterator)localObject3).next();
        if ("1600x1200".equals(localSize.toString())) {
          i = 1;
        }
        ((Set)localObject1).add(localSize.toString());
      }
      if (i != 0)
      {
        Log.w(TAG, "saveCameraCharacteristics() - invalidPreviewSizeFound[1600x1200], list all previewSize.");
        localObject2 = ((Iterable)localObject2).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (Size)((Iterator)localObject2).next();
          Log.d(TAG, "saveCameraCharacteristics() - PROP_SURFACE_SIZES WxH: " + ((Size)localObject3).toString());
        }
      }
      localEditor.putStringSet(PROP_SURFACE_SIZES.name, (Set)localObject1);
    }
    localObject2 = (List)get(PROP_TARGET_FPS_RANGES);
    l2 = l1;
    if (localObject2 != null)
    {
      l2 = l1;
      if (((List)localObject2).size() > 0)
      {
        localObject1 = new HashSet();
        localObject2 = ((Iterable)localObject2).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (Range)((Iterator)localObject2).next();
          localObject3 = ((Range)localObject3).getLower() + "," + ((Range)localObject3).getUpper();
          if (((Set)localObject1).add(localObject3)) {
            l1 += ((String)localObject3).hashCode();
          }
        }
        localEditor.putStringSet(PROP_TARGET_FPS_RANGES.name, (Set)localObject1);
        l2 = l1;
      }
    }
    localObject2 = (List)get(PROP_THUMBNAIL_SIZES);
    l1 = l2;
    if (((List)localObject2).size() > 0)
    {
      localObject1 = new HashSet();
      localObject2 = ((Iterable)localObject2).iterator();
      for (l1 = l2; ((Iterator)localObject2).hasNext(); l1 += ((Size)localObject3).hashCode())
      {
        localObject3 = (Size)((Iterator)localObject2).next();
        ((Set)localObject1).add(((Size)localObject3).toString());
      }
      localEditor.putStringSet(PROP_THUMBNAIL_SIZES.name, (Set)localObject1);
    }
    localObject2 = (List)get(PROP_VIDEO_SIZES);
    l2 = l1;
    if (((List)localObject2).size() > 0)
    {
      localObject1 = new HashSet();
      localObject2 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Size)((Iterator)localObject2).next();
        ((Set)localObject1).add(((Size)localObject3).toString());
        l1 += ((Size)localObject3).hashCode();
      }
      localEditor.putStringSet(PROP_VIDEO_SIZES.name, (Set)localObject1);
      l2 = l1;
    }
    localObject2 = (List)get(PROP_VIDEO_60FPS_SIZES);
    l1 = l2;
    if (((List)localObject2).size() > 0)
    {
      localObject1 = new HashSet();
      localObject2 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Size)((Iterator)localObject2).next();
        ((Set)localObject1).add(((Size)localObject3).toString());
        l2 += ((Size)localObject3).hashCode();
      }
      localEditor.putStringSet(PROP_VIDEO_60FPS_SIZES.name, (Set)localObject1);
      l1 = l2;
    }
    localEditor.putLong("HashCode", l1);
    localEditor.putString("RomBuildVersion", paramCameraCharacteristics);
    localEditor.putInt("Version", 17);
    localEditor.apply();
    Log.v(TAG, "saveCharacteristics() - End, hash code: ", Long.toString(l1));
  }
  
  public void saveCameraInfo(CameraManager paramCameraManager, String paramString)
    throws CameraAccessException
  {
    if (getCameraCharacteristics(paramCameraManager, paramString))
    {
      Log.d(TAG, "saveCameraInfo() - saveCharacteristics, id: " + paramString);
      saveCharacteristics(paramCameraManager.getCameraCharacteristics(paramString));
      return;
    }
    Log.w(TAG, "saveCameraInfo() - getCameraCharacteristics failed, dont save prefs");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */