package android.hardware.camera2;

import android.graphics.Rect;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.impl.CameraMetadataNative.Key;
import android.hardware.camera2.impl.PublicKey;
import android.hardware.camera2.impl.SyntheticKey;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.RggbChannelVector;
import android.hardware.camera2.params.TonemapCurve;
import android.hardware.camera2.utils.HashCodeHelpers;
import android.hardware.camera2.utils.TypeReference;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public final class CaptureRequest
  extends CameraMetadata<Key<?>>
  implements Parcelable
{
  @PublicKey
  public static final Key<Boolean> BLACK_LEVEL_LOCK = new Key("android.blackLevel.lock", Boolean.TYPE);
  @PublicKey
  public static final Key<Integer> COLOR_CORRECTION_ABERRATION_MODE;
  @PublicKey
  public static final Key<RggbChannelVector> COLOR_CORRECTION_GAINS;
  @PublicKey
  public static final Key<Integer> COLOR_CORRECTION_MODE;
  @PublicKey
  public static final Key<ColorSpaceTransform> COLOR_CORRECTION_TRANSFORM;
  @PublicKey
  public static final Key<Integer> CONTROL_AE_ANTIBANDING_MODE;
  @PublicKey
  public static final Key<Integer> CONTROL_AE_EXPOSURE_COMPENSATION;
  @PublicKey
  public static final Key<Boolean> CONTROL_AE_LOCK;
  @PublicKey
  public static final Key<Integer> CONTROL_AE_MODE;
  @PublicKey
  public static final Key<Integer> CONTROL_AE_PRECAPTURE_TRIGGER;
  @PublicKey
  public static final Key<MeteringRectangle[]> CONTROL_AE_REGIONS;
  @PublicKey
  public static final Key<Range<Integer>> CONTROL_AE_TARGET_FPS_RANGE;
  @PublicKey
  public static final Key<Integer> CONTROL_AF_MODE;
  @PublicKey
  public static final Key<MeteringRectangle[]> CONTROL_AF_REGIONS;
  @PublicKey
  public static final Key<Integer> CONTROL_AF_TRIGGER;
  @PublicKey
  public static final Key<Boolean> CONTROL_AWB_LOCK;
  @PublicKey
  public static final Key<Integer> CONTROL_AWB_MODE;
  @PublicKey
  public static final Key<MeteringRectangle[]> CONTROL_AWB_REGIONS;
  @PublicKey
  public static final Key<Integer> CONTROL_CAPTURE_INTENT;
  @PublicKey
  public static final Key<Integer> CONTROL_EFFECT_MODE;
  @PublicKey
  public static final Key<Integer> CONTROL_MODE;
  @PublicKey
  public static final Key<Integer> CONTROL_POST_RAW_SENSITIVITY_BOOST;
  @PublicKey
  public static final Key<Integer> CONTROL_SCENE_MODE;
  @PublicKey
  public static final Key<Integer> CONTROL_VIDEO_STABILIZATION_MODE;
  public static final Parcelable.Creator<CaptureRequest> CREATOR = new Parcelable.Creator()
  {
    public CaptureRequest createFromParcel(Parcel paramAnonymousParcel)
    {
      CaptureRequest localCaptureRequest = new CaptureRequest(null, null);
      CaptureRequest.-wrap0(localCaptureRequest, paramAnonymousParcel);
      return localCaptureRequest;
    }
    
    public CaptureRequest[] newArray(int paramAnonymousInt)
    {
      return new CaptureRequest[paramAnonymousInt];
    }
  };
  @PublicKey
  public static final Key<Integer> EDGE_MODE;
  @PublicKey
  public static final Key<Integer> FLASH_MODE;
  @PublicKey
  public static final Key<Integer> HOT_PIXEL_MODE;
  public static final Key<double[]> JPEG_GPS_COORDINATES;
  @PublicKey
  @SyntheticKey
  public static final Key<Location> JPEG_GPS_LOCATION;
  public static final Key<String> JPEG_GPS_PROCESSING_METHOD;
  public static final Key<Long> JPEG_GPS_TIMESTAMP;
  @PublicKey
  public static final Key<Integer> JPEG_ORIENTATION;
  @PublicKey
  public static final Key<Byte> JPEG_QUALITY;
  @PublicKey
  public static final Key<Byte> JPEG_THUMBNAIL_QUALITY;
  @PublicKey
  public static final Key<Size> JPEG_THUMBNAIL_SIZE;
  public static final Key<Boolean> LED_TRANSMIT;
  @PublicKey
  public static final Key<Float> LENS_APERTURE;
  @PublicKey
  public static final Key<Float> LENS_FILTER_DENSITY;
  @PublicKey
  public static final Key<Float> LENS_FOCAL_LENGTH;
  @PublicKey
  public static final Key<Float> LENS_FOCUS_DISTANCE;
  @PublicKey
  public static final Key<Integer> LENS_OPTICAL_STABILIZATION_MODE;
  @PublicKey
  public static final Key<Integer> NOISE_REDUCTION_MODE;
  @PublicKey
  public static final Key<Float> REPROCESS_EFFECTIVE_EXPOSURE_FACTOR = new Key("android.reprocess.effectiveExposureFactor", Float.TYPE);
  public static final Key<Integer> REQUEST_ID;
  @PublicKey
  public static final Key<Rect> SCALER_CROP_REGION;
  @PublicKey
  public static final Key<Long> SENSOR_EXPOSURE_TIME;
  @PublicKey
  public static final Key<Long> SENSOR_FRAME_DURATION;
  @PublicKey
  public static final Key<Integer> SENSOR_SENSITIVITY;
  @PublicKey
  public static final Key<int[]> SENSOR_TEST_PATTERN_DATA;
  @PublicKey
  public static final Key<Integer> SENSOR_TEST_PATTERN_MODE;
  @PublicKey
  public static final Key<Integer> SHADING_MODE;
  @PublicKey
  public static final Key<Integer> STATISTICS_FACE_DETECT_MODE;
  @PublicKey
  public static final Key<Boolean> STATISTICS_HOT_PIXEL_MAP_MODE;
  @PublicKey
  public static final Key<Integer> STATISTICS_LENS_SHADING_MAP_MODE;
  @PublicKey
  @SyntheticKey
  public static final Key<TonemapCurve> TONEMAP_CURVE;
  public static final Key<float[]> TONEMAP_CURVE_BLUE;
  public static final Key<float[]> TONEMAP_CURVE_GREEN;
  public static final Key<float[]> TONEMAP_CURVE_RED;
  @PublicKey
  public static final Key<Float> TONEMAP_GAMMA;
  @PublicKey
  public static final Key<Integer> TONEMAP_MODE;
  @PublicKey
  public static final Key<Integer> TONEMAP_PRESET_CURVE;
  private boolean mIsPartOfCHSRequestList = false;
  private boolean mIsReprocess;
  private int mReprocessableSessionId;
  private final CameraMetadataNative mSettings;
  private final HashSet<Surface> mSurfaceSet;
  private Object mUserTag;
  
  static
  {
    COLOR_CORRECTION_MODE = new Key("android.colorCorrection.mode", Integer.TYPE);
    COLOR_CORRECTION_TRANSFORM = new Key("android.colorCorrection.transform", ColorSpaceTransform.class);
    COLOR_CORRECTION_GAINS = new Key("android.colorCorrection.gains", RggbChannelVector.class);
    COLOR_CORRECTION_ABERRATION_MODE = new Key("android.colorCorrection.aberrationMode", Integer.TYPE);
    CONTROL_AE_ANTIBANDING_MODE = new Key("android.control.aeAntibandingMode", Integer.TYPE);
    CONTROL_AE_EXPOSURE_COMPENSATION = new Key("android.control.aeExposureCompensation", Integer.TYPE);
    CONTROL_AE_LOCK = new Key("android.control.aeLock", Boolean.TYPE);
    CONTROL_AE_MODE = new Key("android.control.aeMode", Integer.TYPE);
    CONTROL_AE_REGIONS = new Key("android.control.aeRegions", MeteringRectangle[].class);
    CONTROL_AE_TARGET_FPS_RANGE = new Key("android.control.aeTargetFpsRange", new TypeReference() {});
    CONTROL_AE_PRECAPTURE_TRIGGER = new Key("android.control.aePrecaptureTrigger", Integer.TYPE);
    CONTROL_AF_MODE = new Key("android.control.afMode", Integer.TYPE);
    CONTROL_AF_REGIONS = new Key("android.control.afRegions", MeteringRectangle[].class);
    CONTROL_AF_TRIGGER = new Key("android.control.afTrigger", Integer.TYPE);
    CONTROL_AWB_LOCK = new Key("android.control.awbLock", Boolean.TYPE);
    CONTROL_AWB_MODE = new Key("android.control.awbMode", Integer.TYPE);
    CONTROL_AWB_REGIONS = new Key("android.control.awbRegions", MeteringRectangle[].class);
    CONTROL_CAPTURE_INTENT = new Key("android.control.captureIntent", Integer.TYPE);
    CONTROL_EFFECT_MODE = new Key("android.control.effectMode", Integer.TYPE);
    CONTROL_MODE = new Key("android.control.mode", Integer.TYPE);
    CONTROL_SCENE_MODE = new Key("android.control.sceneMode", Integer.TYPE);
    CONTROL_VIDEO_STABILIZATION_MODE = new Key("android.control.videoStabilizationMode", Integer.TYPE);
    CONTROL_POST_RAW_SENSITIVITY_BOOST = new Key("android.control.postRawSensitivityBoost", Integer.TYPE);
    EDGE_MODE = new Key("android.edge.mode", Integer.TYPE);
    FLASH_MODE = new Key("android.flash.mode", Integer.TYPE);
    HOT_PIXEL_MODE = new Key("android.hotPixel.mode", Integer.TYPE);
    JPEG_GPS_LOCATION = new Key("android.jpeg.gpsLocation", Location.class);
    JPEG_GPS_COORDINATES = new Key("android.jpeg.gpsCoordinates", double[].class);
    JPEG_GPS_PROCESSING_METHOD = new Key("android.jpeg.gpsProcessingMethod", String.class);
    JPEG_GPS_TIMESTAMP = new Key("android.jpeg.gpsTimestamp", Long.TYPE);
    JPEG_ORIENTATION = new Key("android.jpeg.orientation", Integer.TYPE);
    JPEG_QUALITY = new Key("android.jpeg.quality", Byte.TYPE);
    JPEG_THUMBNAIL_QUALITY = new Key("android.jpeg.thumbnailQuality", Byte.TYPE);
    JPEG_THUMBNAIL_SIZE = new Key("android.jpeg.thumbnailSize", Size.class);
    LENS_APERTURE = new Key("android.lens.aperture", Float.TYPE);
    LENS_FILTER_DENSITY = new Key("android.lens.filterDensity", Float.TYPE);
    LENS_FOCAL_LENGTH = new Key("android.lens.focalLength", Float.TYPE);
    LENS_FOCUS_DISTANCE = new Key("android.lens.focusDistance", Float.TYPE);
    LENS_OPTICAL_STABILIZATION_MODE = new Key("android.lens.opticalStabilizationMode", Integer.TYPE);
    NOISE_REDUCTION_MODE = new Key("android.noiseReduction.mode", Integer.TYPE);
    REQUEST_ID = new Key("android.request.id", Integer.TYPE);
    SCALER_CROP_REGION = new Key("android.scaler.cropRegion", Rect.class);
    SENSOR_EXPOSURE_TIME = new Key("android.sensor.exposureTime", Long.TYPE);
    SENSOR_FRAME_DURATION = new Key("android.sensor.frameDuration", Long.TYPE);
    SENSOR_SENSITIVITY = new Key("android.sensor.sensitivity", Integer.TYPE);
    SENSOR_TEST_PATTERN_DATA = new Key("android.sensor.testPatternData", int[].class);
    SENSOR_TEST_PATTERN_MODE = new Key("android.sensor.testPatternMode", Integer.TYPE);
    SHADING_MODE = new Key("android.shading.mode", Integer.TYPE);
    STATISTICS_FACE_DETECT_MODE = new Key("android.statistics.faceDetectMode", Integer.TYPE);
    STATISTICS_HOT_PIXEL_MAP_MODE = new Key("android.statistics.hotPixelMapMode", Boolean.TYPE);
    STATISTICS_LENS_SHADING_MAP_MODE = new Key("android.statistics.lensShadingMapMode", Integer.TYPE);
    TONEMAP_CURVE_BLUE = new Key("android.tonemap.curveBlue", float[].class);
    TONEMAP_CURVE_GREEN = new Key("android.tonemap.curveGreen", float[].class);
    TONEMAP_CURVE_RED = new Key("android.tonemap.curveRed", float[].class);
    TONEMAP_CURVE = new Key("android.tonemap.curve", TonemapCurve.class);
    TONEMAP_MODE = new Key("android.tonemap.mode", Integer.TYPE);
    TONEMAP_GAMMA = new Key("android.tonemap.gamma", Float.TYPE);
    TONEMAP_PRESET_CURVE = new Key("android.tonemap.presetCurve", Integer.TYPE);
    LED_TRANSMIT = new Key("android.led.transmit", Boolean.TYPE);
  }
  
  private CaptureRequest()
  {
    this.mSettings = new CameraMetadataNative();
    this.mSurfaceSet = new HashSet();
    this.mIsReprocess = false;
    this.mReprocessableSessionId = -1;
  }
  
  private CaptureRequest(CaptureRequest paramCaptureRequest)
  {
    this.mSettings = new CameraMetadataNative(paramCaptureRequest.mSettings);
    this.mSurfaceSet = ((HashSet)paramCaptureRequest.mSurfaceSet.clone());
    this.mIsReprocess = paramCaptureRequest.mIsReprocess;
    this.mIsPartOfCHSRequestList = paramCaptureRequest.mIsPartOfCHSRequestList;
    this.mReprocessableSessionId = paramCaptureRequest.mReprocessableSessionId;
    this.mUserTag = paramCaptureRequest.mUserTag;
  }
  
  private CaptureRequest(CameraMetadataNative paramCameraMetadataNative, boolean paramBoolean, int paramInt)
  {
    this.mSettings = CameraMetadataNative.move(paramCameraMetadataNative);
    this.mSurfaceSet = new HashSet();
    this.mIsReprocess = paramBoolean;
    if (paramBoolean)
    {
      if (paramInt == -1) {
        throw new IllegalArgumentException("Create a reprocess capture request with an invalid session ID: " + paramInt);
      }
      this.mReprocessableSessionId = paramInt;
      return;
    }
    this.mReprocessableSessionId = -1;
  }
  
  private boolean equals(CaptureRequest paramCaptureRequest)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramCaptureRequest != null)
    {
      bool1 = bool2;
      if (Objects.equals(this.mUserTag, paramCaptureRequest.mUserTag))
      {
        bool1 = bool2;
        if (this.mSurfaceSet.equals(paramCaptureRequest.mSurfaceSet))
        {
          bool1 = bool2;
          if (this.mSettings.equals(paramCaptureRequest.mSettings))
          {
            bool1 = bool2;
            if (this.mIsReprocess == paramCaptureRequest.mIsReprocess)
            {
              bool1 = bool2;
              if (this.mReprocessableSessionId == paramCaptureRequest.mReprocessableSessionId) {
                bool1 = true;
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  private void readFromParcel(Parcel paramParcel)
  {
    boolean bool = false;
    this.mSettings.readFromParcel(paramParcel);
    this.mSurfaceSet.clear();
    Parcelable[] arrayOfParcelable = paramParcel.readParcelableArray(Surface.class.getClassLoader());
    if (arrayOfParcelable == null) {
      return;
    }
    int j = arrayOfParcelable.length;
    int i = 0;
    while (i < j)
    {
      Surface localSurface = (Surface)arrayOfParcelable[i];
      this.mSurfaceSet.add(localSurface);
      i += 1;
    }
    if (paramParcel.readInt() == 0) {}
    for (;;)
    {
      this.mIsReprocess = bool;
      this.mReprocessableSessionId = -1;
      return;
      bool = true;
    }
  }
  
  public boolean containsTarget(Surface paramSurface)
  {
    return this.mSurfaceSet.contains(paramSurface);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof CaptureRequest)) {
      return equals((CaptureRequest)paramObject);
    }
    return false;
  }
  
  public <T> T get(Key<T> paramKey)
  {
    return (T)this.mSettings.get(paramKey);
  }
  
  protected Class<Key<?>> getKeyClass()
  {
    return (Class)Key.class;
  }
  
  public List<Key<?>> getKeys()
  {
    return super.getKeys();
  }
  
  public CameraMetadataNative getNativeCopy()
  {
    return new CameraMetadataNative(this.mSettings);
  }
  
  protected <T> T getProtected(Key<?> paramKey)
  {
    return (T)this.mSettings.get(paramKey);
  }
  
  public int getReprocessableSessionId()
  {
    if ((!this.mIsReprocess) || (this.mReprocessableSessionId == -1)) {
      throw new IllegalStateException("Getting the reprocessable session ID for a non-reprocess capture request is illegal.");
    }
    return this.mReprocessableSessionId;
  }
  
  public Object getTag()
  {
    return this.mUserTag;
  }
  
  public Collection<Surface> getTargets()
  {
    return Collections.unmodifiableCollection(this.mSurfaceSet);
  }
  
  public int hashCode()
  {
    return HashCodeHelpers.hashCodeGeneric(new Object[] { this.mSettings, this.mSurfaceSet, this.mUserTag });
  }
  
  public boolean isPartOfCRequestList()
  {
    return this.mIsPartOfCHSRequestList;
  }
  
  public boolean isReprocess()
  {
    return this.mIsReprocess;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    this.mSettings.writeToParcel(paramParcel, paramInt);
    paramParcel.writeParcelableArray((Surface[])this.mSurfaceSet.toArray(new Surface[this.mSurfaceSet.size()]), paramInt);
    if (this.mIsReprocess) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
    }
  }
  
  public static final class Builder
  {
    private final CaptureRequest mRequest;
    
    public Builder(CameraMetadataNative paramCameraMetadataNative, boolean paramBoolean, int paramInt)
    {
      this.mRequest = new CaptureRequest(paramCameraMetadataNative, paramBoolean, paramInt, null);
    }
    
    public void addTarget(Surface paramSurface)
    {
      CaptureRequest.-get1(this.mRequest).add(paramSurface);
    }
    
    public CaptureRequest build()
    {
      return new CaptureRequest(this.mRequest, null, null);
    }
    
    public <T> T get(CaptureRequest.Key<T> paramKey)
    {
      return (T)CaptureRequest.-get0(this.mRequest).get(paramKey);
    }
    
    public boolean isEmpty()
    {
      return CaptureRequest.-get0(this.mRequest).isEmpty();
    }
    
    public void removeTarget(Surface paramSurface)
    {
      CaptureRequest.-get1(this.mRequest).remove(paramSurface);
    }
    
    public <T> void set(CaptureRequest.Key<T> paramKey, T paramT)
    {
      CaptureRequest.-get0(this.mRequest).set(paramKey, paramT);
    }
    
    public void setPartOfCHSRequestList(boolean paramBoolean)
    {
      CaptureRequest.-set0(this.mRequest, paramBoolean);
    }
    
    public void setTag(Object paramObject)
    {
      CaptureRequest.-set1(this.mRequest, paramObject);
    }
  }
  
  public static final class Key<T>
  {
    private final CameraMetadataNative.Key<T> mKey;
    
    Key(CameraMetadataNative.Key<?> paramKey)
    {
      this.mKey = paramKey;
    }
    
    public Key(String paramString, TypeReference<T> paramTypeReference)
    {
      this.mKey = new CameraMetadataNative.Key(paramString, paramTypeReference);
    }
    
    public Key(String paramString, Class<T> paramClass)
    {
      this.mKey = new CameraMetadataNative.Key(paramString, paramClass);
    }
    
    public final boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Key)) {
        return ((Key)paramObject).mKey.equals(this.mKey);
      }
      return false;
    }
    
    public String getName()
    {
      return this.mKey.getName();
    }
    
    public CameraMetadataNative.Key<T> getNativeKey()
    {
      return this.mKey;
    }
    
    public final int hashCode()
    {
      return this.mKey.hashCode();
    }
    
    public String toString()
    {
      return String.format("CaptureRequest.Key(%s)", new Object[] { this.mKey.getName() });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/CaptureRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */