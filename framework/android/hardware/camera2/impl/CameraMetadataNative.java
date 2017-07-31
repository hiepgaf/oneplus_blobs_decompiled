package android.hardware.camera2.impl;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraCharacteristics.Key;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Key;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.CaptureResult.Key;
import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.MarshalRegistry;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.marshal.impl.MarshalQueryableArray;
import android.hardware.camera2.marshal.impl.MarshalQueryableBlackLevelPattern;
import android.hardware.camera2.marshal.impl.MarshalQueryableBoolean;
import android.hardware.camera2.marshal.impl.MarshalQueryableColorSpaceTransform;
import android.hardware.camera2.marshal.impl.MarshalQueryableEnum;
import android.hardware.camera2.marshal.impl.MarshalQueryableHighSpeedVideoConfiguration;
import android.hardware.camera2.marshal.impl.MarshalQueryableMeteringRectangle;
import android.hardware.camera2.marshal.impl.MarshalQueryableNativeByteToInteger;
import android.hardware.camera2.marshal.impl.MarshalQueryablePair;
import android.hardware.camera2.marshal.impl.MarshalQueryableParcelable;
import android.hardware.camera2.marshal.impl.MarshalQueryablePrimitive;
import android.hardware.camera2.marshal.impl.MarshalQueryableRange;
import android.hardware.camera2.marshal.impl.MarshalQueryableRect;
import android.hardware.camera2.marshal.impl.MarshalQueryableReprocessFormatsMap;
import android.hardware.camera2.marshal.impl.MarshalQueryableRggbChannelVector;
import android.hardware.camera2.marshal.impl.MarshalQueryableSize;
import android.hardware.camera2.marshal.impl.MarshalQueryableSizeF;
import android.hardware.camera2.marshal.impl.MarshalQueryableStreamConfiguration;
import android.hardware.camera2.marshal.impl.MarshalQueryableStreamConfigurationDuration;
import android.hardware.camera2.marshal.impl.MarshalQueryableString;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.HighSpeedVideoConfiguration;
import android.hardware.camera2.params.LensShadingMap;
import android.hardware.camera2.params.ReprocessFormatsMap;
import android.hardware.camera2.params.StreamConfiguration;
import android.hardware.camera2.params.StreamConfigurationDuration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.params.TonemapCurve;
import android.hardware.camera2.utils.TypeReference;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.ServiceSpecificException;
import android.util.Log;
import android.util.Size;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

public class CameraMetadataNative
  implements Parcelable
{
  private static final String CELLID_PROCESS = "CELLID";
  public static final Parcelable.Creator<CameraMetadataNative> CREATOR = new Parcelable.Creator()
  {
    public CameraMetadataNative createFromParcel(Parcel paramAnonymousParcel)
    {
      CameraMetadataNative localCameraMetadataNative = new CameraMetadataNative();
      localCameraMetadataNative.readFromParcel(paramAnonymousParcel);
      return localCameraMetadataNative;
    }
    
    public CameraMetadataNative[] newArray(int paramAnonymousInt)
    {
      return new CameraMetadataNative[paramAnonymousInt];
    }
  };
  private static final boolean DEBUG = false;
  private static final int FACE_LANDMARK_SIZE = 6;
  private static final String GPS_PROCESS = "GPS";
  public static final int NATIVE_JPEG_FORMAT = 33;
  public static final int NUM_TYPES = 6;
  private static final String TAG = "CameraMetadataJV";
  public static final int TYPE_BYTE = 0;
  public static final int TYPE_DOUBLE = 4;
  public static final int TYPE_FLOAT = 2;
  public static final int TYPE_INT32 = 1;
  public static final int TYPE_INT64 = 3;
  public static final int TYPE_RATIONAL = 5;
  private static final HashMap<Key<?>, GetCommand> sGetCommandMap = new HashMap();
  private static final HashMap<Key<?>, SetCommand> sSetCommandMap;
  private long mMetadataPtr;
  
  static
  {
    sGetCommandMap.put(CameraCharacteristics.SCALER_AVAILABLE_FORMATS.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap11(paramAnonymousCameraMetadataNative);
      }
    });
    sGetCommandMap.put(CaptureResult.STATISTICS_FACES.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap1(paramAnonymousCameraMetadataNative);
      }
    });
    sGetCommandMap.put(CaptureResult.STATISTICS_FACE_RECTANGLES.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap0(paramAnonymousCameraMetadataNative);
      }
    });
    sGetCommandMap.put(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap3(paramAnonymousCameraMetadataNative);
      }
    });
    sGetCommandMap.put(CameraCharacteristics.CONTROL_MAX_REGIONS_AE.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap13(paramAnonymousCameraMetadataNative, paramAnonymousKey);
      }
    });
    sGetCommandMap.put(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap13(paramAnonymousCameraMetadataNative, paramAnonymousKey);
      }
    });
    sGetCommandMap.put(CameraCharacteristics.CONTROL_MAX_REGIONS_AF.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap13(paramAnonymousCameraMetadataNative, paramAnonymousKey);
      }
    });
    sGetCommandMap.put(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_RAW.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap12(paramAnonymousCameraMetadataNative, paramAnonymousKey);
      }
    });
    sGetCommandMap.put(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap12(paramAnonymousCameraMetadataNative, paramAnonymousKey);
      }
    });
    sGetCommandMap.put(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC_STALLING.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap12(paramAnonymousCameraMetadataNative, paramAnonymousKey);
      }
    });
    sGetCommandMap.put(CaptureRequest.TONEMAP_CURVE.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap4(paramAnonymousCameraMetadataNative);
      }
    });
    sGetCommandMap.put(CaptureResult.JPEG_GPS_LOCATION.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap5(paramAnonymousCameraMetadataNative);
      }
    });
    sGetCommandMap.put(CaptureResult.STATISTICS_LENS_SHADING_CORRECTION_MAP.getNativeKey(), new GetCommand()
    {
      public <T> T getValue(CameraMetadataNative paramAnonymousCameraMetadataNative, CameraMetadataNative.Key<T> paramAnonymousKey)
      {
        return CameraMetadataNative.-wrap2(paramAnonymousCameraMetadataNative);
      }
    });
    sSetCommandMap = new HashMap();
    sSetCommandMap.put(CameraCharacteristics.SCALER_AVAILABLE_FORMATS.getNativeKey(), new SetCommand()
    {
      public <T> void setValue(CameraMetadataNative paramAnonymousCameraMetadataNative, T paramAnonymousT)
      {
        CameraMetadataNative.-wrap6(paramAnonymousCameraMetadataNative, (int[])paramAnonymousT);
      }
    });
    sSetCommandMap.put(CaptureResult.STATISTICS_FACE_RECTANGLES.getNativeKey(), new SetCommand()
    {
      public <T> void setValue(CameraMetadataNative paramAnonymousCameraMetadataNative, T paramAnonymousT)
      {
        CameraMetadataNative.-wrap7(paramAnonymousCameraMetadataNative, (Rect[])paramAnonymousT);
      }
    });
    sSetCommandMap.put(CaptureResult.STATISTICS_FACES.getNativeKey(), new SetCommand()
    {
      public <T> void setValue(CameraMetadataNative paramAnonymousCameraMetadataNative, T paramAnonymousT)
      {
        CameraMetadataNative.-wrap8(paramAnonymousCameraMetadataNative, (Face[])paramAnonymousT);
      }
    });
    sSetCommandMap.put(CaptureRequest.TONEMAP_CURVE.getNativeKey(), new SetCommand()
    {
      public <T> void setValue(CameraMetadataNative paramAnonymousCameraMetadataNative, T paramAnonymousT)
      {
        CameraMetadataNative.-wrap10(paramAnonymousCameraMetadataNative, (TonemapCurve)paramAnonymousT);
      }
    });
    sSetCommandMap.put(CaptureResult.JPEG_GPS_LOCATION.getNativeKey(), new SetCommand()
    {
      public <T> void setValue(CameraMetadataNative paramAnonymousCameraMetadataNative, T paramAnonymousT)
      {
        CameraMetadataNative.-wrap9(paramAnonymousCameraMetadataNative, (Location)paramAnonymousT);
      }
    });
    nativeClassInit();
    registerAllMarshalers();
  }
  
  public CameraMetadataNative()
  {
    this.mMetadataPtr = nativeAllocate();
    if (this.mMetadataPtr == 0L) {
      throw new OutOfMemoryError("Failed to allocate native CameraMetadata");
    }
  }
  
  public CameraMetadataNative(CameraMetadataNative paramCameraMetadataNative)
  {
    this.mMetadataPtr = nativeAllocateCopy(paramCameraMetadataNative);
    if (this.mMetadataPtr == 0L) {
      throw new OutOfMemoryError("Failed to allocate native CameraMetadata");
    }
  }
  
  private static boolean areValuesAllNull(Object... paramVarArgs)
  {
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      if (paramVarArgs[i] != null) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private void close()
  {
    nativeClose();
    this.mMetadataPtr = 0L;
  }
  
  public static <K> ArrayList<K> getAllVendorKeys(Class<K> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException();
    }
    return nativeGetAllVendorKeys(paramClass);
  }
  
  private int[] getAvailableFormats()
  {
    int[] arrayOfInt = (int[])getBase(CameraCharacteristics.SCALER_AVAILABLE_FORMATS);
    if (arrayOfInt != null)
    {
      int i = 0;
      while (i < arrayOfInt.length)
      {
        if (arrayOfInt[i] == 33) {
          arrayOfInt[i] = 256;
        }
        i += 1;
      }
    }
    return arrayOfInt;
  }
  
  private <T> T getBase(CameraCharacteristics.Key<T> paramKey)
  {
    return (T)getBase(paramKey.getNativeKey());
  }
  
  private <T> T getBase(CaptureRequest.Key<T> paramKey)
  {
    return (T)getBase(paramKey.getNativeKey());
  }
  
  private <T> T getBase(CaptureResult.Key<T> paramKey)
  {
    return (T)getBase(paramKey.getNativeKey());
  }
  
  private <T> T getBase(Key<T> paramKey)
  {
    byte[] arrayOfByte = readValues(paramKey.getTag());
    if (arrayOfByte == null) {
      return null;
    }
    return (T)getMarshalerForKey(paramKey).unmarshal(ByteBuffer.wrap(arrayOfByte).order(ByteOrder.nativeOrder()));
  }
  
  private Rect[] getFaceRectangles()
  {
    Rect[] arrayOfRect1 = (Rect[])getBase(CaptureResult.STATISTICS_FACE_RECTANGLES);
    if (arrayOfRect1 == null) {
      return null;
    }
    Rect[] arrayOfRect2 = new Rect[arrayOfRect1.length];
    int i = 0;
    while (i < arrayOfRect1.length)
    {
      arrayOfRect2[i] = new Rect(arrayOfRect1[i].left, arrayOfRect1[i].top, arrayOfRect1[i].right - arrayOfRect1[i].left, arrayOfRect1[i].bottom - arrayOfRect1[i].top);
      i += 1;
    }
    return arrayOfRect2;
  }
  
  private Face[] getFaces()
  {
    Object localObject2 = (Integer)get(CaptureResult.STATISTICS_FACE_DETECT_MODE);
    byte[] arrayOfByte = (byte[])get(CaptureResult.STATISTICS_FACE_SCORES);
    Rect[] arrayOfRect = (Rect[])get(CaptureResult.STATISTICS_FACE_RECTANGLES);
    int[] arrayOfInt1 = (int[])get(CaptureResult.STATISTICS_FACE_IDS);
    int[] arrayOfInt2 = (int[])get(CaptureResult.STATISTICS_FACE_LANDMARKS);
    if (areValuesAllNull(new Object[] { localObject2, arrayOfByte, arrayOfRect, arrayOfInt1, arrayOfInt2 })) {
      return null;
    }
    Object localObject1;
    if (localObject2 == null)
    {
      Log.w("CameraMetadataJV", "Face detect mode metadata is null, assuming the mode is SIMPLE");
      localObject1 = Integer.valueOf(1);
    }
    while ((arrayOfByte == null) || (arrayOfRect == null))
    {
      Log.w("CameraMetadataJV", "Expect face scores and rectangles to be non-null");
      return new Face[0];
      if (((Integer)localObject2).intValue() == 0) {
        return new Face[0];
      }
      localObject1 = localObject2;
      if (((Integer)localObject2).intValue() != 1)
      {
        localObject1 = localObject2;
        if (((Integer)localObject2).intValue() != 2)
        {
          Log.w("CameraMetadataJV", "Unknown face detect mode: " + localObject2);
          return new Face[0];
        }
      }
    }
    if (arrayOfByte.length != arrayOfRect.length) {
      Log.w("CameraMetadataJV", String.format("Face score size(%d) doesn match face rectangle size(%d)!", new Object[] { Integer.valueOf(arrayOfByte.length), Integer.valueOf(arrayOfRect.length) }));
    }
    int j = Math.min(arrayOfByte.length, arrayOfRect.length);
    localObject2 = localObject1;
    int i = j;
    if (((Integer)localObject1).intValue() == 2)
    {
      if ((arrayOfInt1 != null) && (arrayOfInt2 != null)) {
        break label370;
      }
      Log.w("CameraMetadataJV", "Expect face ids and landmarks to be non-null for FULL mode,fallback to SIMPLE mode");
      localObject2 = Integer.valueOf(1);
      i = j;
    }
    for (;;)
    {
      localObject1 = new ArrayList();
      if (((Integer)localObject2).intValue() != 1) {
        break;
      }
      j = 0;
      while (j < i)
      {
        if ((arrayOfByte[j] <= 100) && (arrayOfByte[j] >= 1)) {
          ((ArrayList)localObject1).add(new Face(arrayOfRect[j], arrayOfByte[j]));
        }
        j += 1;
      }
      label370:
      if ((arrayOfInt1.length != j) || (arrayOfInt2.length != j * 6)) {
        Log.w("CameraMetadataJV", String.format("Face id size(%d), or face landmark size(%d) don'tmatch face number(%d)!", new Object[] { Integer.valueOf(arrayOfInt1.length), Integer.valueOf(arrayOfInt2.length * 6), Integer.valueOf(j) }));
      }
      i = Math.min(Math.min(j, arrayOfInt1.length), arrayOfInt2.length / 6);
      localObject2 = localObject1;
    }
    j = 0;
    while (j < i)
    {
      if ((arrayOfByte[j] <= 100) && (arrayOfByte[j] >= 1) && (arrayOfInt1[j] >= 0))
      {
        localObject2 = new Point(arrayOfInt2[(j * 6)], arrayOfInt2[(j * 6 + 1)]);
        Point localPoint1 = new Point(arrayOfInt2[(j * 6 + 2)], arrayOfInt2[(j * 6 + 3)]);
        Point localPoint2 = new Point(arrayOfInt2[(j * 6 + 4)], arrayOfInt2[(j * 6 + 5)]);
        ((ArrayList)localObject1).add(new Face(arrayOfRect[j], arrayOfByte[j], arrayOfInt1[j], (Point)localObject2, localPoint1, localPoint2));
      }
      j += 1;
    }
    localObject2 = new Face[((ArrayList)localObject1).size()];
    ((ArrayList)localObject1).toArray((Object[])localObject2);
    return (Face[])localObject2;
  }
  
  private Location getGpsLocation()
  {
    Object localObject = (String)get(CaptureResult.JPEG_GPS_PROCESSING_METHOD);
    double[] arrayOfDouble = (double[])get(CaptureResult.JPEG_GPS_COORDINATES);
    Long localLong = (Long)get(CaptureResult.JPEG_GPS_TIMESTAMP);
    if (areValuesAllNull(new Object[] { localObject, arrayOfDouble, localLong })) {
      return null;
    }
    localObject = new Location(translateProcessToLocationProvider((String)localObject));
    if (localLong != null) {
      ((Location)localObject).setTime(localLong.longValue());
    }
    while (arrayOfDouble != null)
    {
      ((Location)localObject).setLatitude(arrayOfDouble[0]);
      ((Location)localObject).setLongitude(arrayOfDouble[1]);
      ((Location)localObject).setAltitude(arrayOfDouble[2]);
      return (Location)localObject;
      Log.w("CameraMetadataJV", "getGpsLocation - No timestamp for GPS location.");
    }
    Log.w("CameraMetadataJV", "getGpsLocation - No coordinates for GPS location");
    return (Location)localObject;
  }
  
  private LensShadingMap getLensShadingMap()
  {
    float[] arrayOfFloat = (float[])getBase(CaptureResult.STATISTICS_LENS_SHADING_MAP);
    Size localSize = (Size)get(CameraCharacteristics.LENS_INFO_SHADING_MAP_SIZE);
    if (arrayOfFloat == null) {
      return null;
    }
    if (localSize == null)
    {
      Log.w("CameraMetadataJV", "getLensShadingMap - Lens shading map size was null.");
      return null;
    }
    return new LensShadingMap(arrayOfFloat, localSize.getHeight(), localSize.getWidth());
  }
  
  private static <T> Marshaler<T> getMarshalerForKey(Key<T> paramKey)
  {
    return MarshalRegistry.getMarshaler(paramKey.getTypeReference(), getNativeType(paramKey.getTag()));
  }
  
  private <T> Integer getMaxNumOutputs(Key<T> paramKey)
  {
    int[] arrayOfInt = (int[])getBase(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_STREAMS);
    if (arrayOfInt == null) {
      return null;
    }
    if (paramKey.equals(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_RAW)) {
      return Integer.valueOf(arrayOfInt[0]);
    }
    if (paramKey.equals(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC)) {
      return Integer.valueOf(arrayOfInt[1]);
    }
    if (paramKey.equals(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC_STALLING)) {
      return Integer.valueOf(arrayOfInt[2]);
    }
    throw new AssertionError("Invalid key " + paramKey);
  }
  
  private <T> Integer getMaxRegions(Key<T> paramKey)
  {
    int[] arrayOfInt = (int[])getBase(CameraCharacteristics.CONTROL_MAX_REGIONS);
    if (arrayOfInt == null) {
      return null;
    }
    if (paramKey.equals(CameraCharacteristics.CONTROL_MAX_REGIONS_AE)) {
      return Integer.valueOf(arrayOfInt[0]);
    }
    if (paramKey.equals(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB)) {
      return Integer.valueOf(arrayOfInt[1]);
    }
    if (paramKey.equals(CameraCharacteristics.CONTROL_MAX_REGIONS_AF)) {
      return Integer.valueOf(arrayOfInt[2]);
    }
    throw new AssertionError("Invalid key " + paramKey);
  }
  
  public static int getNativeType(int paramInt)
  {
    return nativeGetTypeFromTag(paramInt);
  }
  
  private StreamConfigurationMap getStreamConfigurationMap()
  {
    StreamConfiguration[] arrayOfStreamConfiguration1 = (StreamConfiguration[])getBase(CameraCharacteristics.SCALER_AVAILABLE_STREAM_CONFIGURATIONS);
    StreamConfigurationDuration[] arrayOfStreamConfigurationDuration1 = (StreamConfigurationDuration[])getBase(CameraCharacteristics.SCALER_AVAILABLE_MIN_FRAME_DURATIONS);
    StreamConfigurationDuration[] arrayOfStreamConfigurationDuration2 = (StreamConfigurationDuration[])getBase(CameraCharacteristics.SCALER_AVAILABLE_STALL_DURATIONS);
    StreamConfiguration[] arrayOfStreamConfiguration2 = (StreamConfiguration[])getBase(CameraCharacteristics.DEPTH_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS);
    StreamConfigurationDuration[] arrayOfStreamConfigurationDuration3 = (StreamConfigurationDuration[])getBase(CameraCharacteristics.DEPTH_AVAILABLE_DEPTH_MIN_FRAME_DURATIONS);
    StreamConfigurationDuration[] arrayOfStreamConfigurationDuration4 = (StreamConfigurationDuration[])getBase(CameraCharacteristics.DEPTH_AVAILABLE_DEPTH_STALL_DURATIONS);
    HighSpeedVideoConfiguration[] arrayOfHighSpeedVideoConfiguration = (HighSpeedVideoConfiguration[])getBase(CameraCharacteristics.CONTROL_AVAILABLE_HIGH_SPEED_VIDEO_CONFIGURATIONS);
    ReprocessFormatsMap localReprocessFormatsMap = (ReprocessFormatsMap)getBase(CameraCharacteristics.SCALER_AVAILABLE_INPUT_OUTPUT_FORMATS_MAP);
    int[] arrayOfInt = (int[])getBase(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
    boolean bool2 = false;
    int i = 0;
    int j = arrayOfInt.length;
    for (;;)
    {
      boolean bool1 = bool2;
      if (i < j)
      {
        if (arrayOfInt[i] == 6) {
          bool1 = true;
        }
      }
      else {
        return new StreamConfigurationMap(arrayOfStreamConfiguration1, arrayOfStreamConfigurationDuration1, arrayOfStreamConfigurationDuration2, arrayOfStreamConfiguration2, arrayOfStreamConfigurationDuration3, arrayOfStreamConfigurationDuration4, arrayOfHighSpeedVideoConfiguration, localReprocessFormatsMap, bool1);
      }
      i += 1;
    }
  }
  
  public static int getTag(String paramString)
  {
    return nativeGetTagFromKey(paramString);
  }
  
  private <T> TonemapCurve getTonemapCurve()
  {
    float[] arrayOfFloat1 = (float[])getBase(CaptureRequest.TONEMAP_CURVE_RED);
    float[] arrayOfFloat2 = (float[])getBase(CaptureRequest.TONEMAP_CURVE_GREEN);
    float[] arrayOfFloat3 = (float[])getBase(CaptureRequest.TONEMAP_CURVE_BLUE);
    if (areValuesAllNull(new Object[] { arrayOfFloat1, arrayOfFloat2, arrayOfFloat3 })) {
      return null;
    }
    if ((arrayOfFloat1 == null) || (arrayOfFloat2 == null)) {}
    while (arrayOfFloat3 == null)
    {
      Log.w("CameraMetadataJV", "getTonemapCurve - missing tone curve components");
      return null;
    }
    return new TonemapCurve(arrayOfFloat1, arrayOfFloat2, arrayOfFloat3);
  }
  
  public static CameraMetadataNative move(CameraMetadataNative paramCameraMetadataNative)
  {
    CameraMetadataNative localCameraMetadataNative = new CameraMetadataNative();
    localCameraMetadataNative.swap(paramCameraMetadataNative);
    return localCameraMetadataNative;
  }
  
  private native long nativeAllocate();
  
  private native long nativeAllocateCopy(CameraMetadataNative paramCameraMetadataNative)
    throws NullPointerException;
  
  private static native void nativeClassInit();
  
  private synchronized native void nativeClose();
  
  private synchronized native void nativeDump()
    throws IOException;
  
  private static native ArrayList nativeGetAllVendorKeys(Class paramClass);
  
  private synchronized native int nativeGetEntryCount();
  
  private static native int nativeGetTagFromKey(String paramString)
    throws IllegalArgumentException;
  
  private static native int nativeGetTypeFromTag(int paramInt)
    throws IllegalArgumentException;
  
  private synchronized native boolean nativeIsEmpty();
  
  private synchronized native void nativeReadFromParcel(Parcel paramParcel);
  
  private synchronized native byte[] nativeReadValues(int paramInt);
  
  private static native int nativeSetupGlobalVendorTagDescriptor();
  
  private synchronized native void nativeSwap(CameraMetadataNative paramCameraMetadataNative)
    throws NullPointerException;
  
  private synchronized native void nativeWriteToParcel(Parcel paramParcel);
  
  private synchronized native void nativeWriteValues(int paramInt, byte[] paramArrayOfByte);
  
  private static void registerAllMarshalers()
  {
    int i = 0;
    MarshalQueryable[] arrayOfMarshalQueryable = new MarshalQueryable[20];
    arrayOfMarshalQueryable[0] = new MarshalQueryablePrimitive();
    arrayOfMarshalQueryable[1] = new MarshalQueryableEnum();
    arrayOfMarshalQueryable[2] = new MarshalQueryableArray();
    arrayOfMarshalQueryable[3] = new MarshalQueryableBoolean();
    arrayOfMarshalQueryable[4] = new MarshalQueryableNativeByteToInteger();
    arrayOfMarshalQueryable[5] = new MarshalQueryableRect();
    arrayOfMarshalQueryable[6] = new MarshalQueryableSize();
    arrayOfMarshalQueryable[7] = new MarshalQueryableSizeF();
    arrayOfMarshalQueryable[8] = new MarshalQueryableString();
    arrayOfMarshalQueryable[9] = new MarshalQueryableReprocessFormatsMap();
    arrayOfMarshalQueryable[10] = new MarshalQueryableRange();
    arrayOfMarshalQueryable[11] = new MarshalQueryablePair();
    arrayOfMarshalQueryable[12] = new MarshalQueryableMeteringRectangle();
    arrayOfMarshalQueryable[13] = new MarshalQueryableColorSpaceTransform();
    arrayOfMarshalQueryable[14] = new MarshalQueryableStreamConfiguration();
    arrayOfMarshalQueryable[15] = new MarshalQueryableStreamConfigurationDuration();
    arrayOfMarshalQueryable[16] = new MarshalQueryableRggbChannelVector();
    arrayOfMarshalQueryable[17] = new MarshalQueryableBlackLevelPattern();
    arrayOfMarshalQueryable[18] = new MarshalQueryableHighSpeedVideoConfiguration();
    arrayOfMarshalQueryable[19] = new MarshalQueryableParcelable();
    int j = arrayOfMarshalQueryable.length;
    while (i < j)
    {
      MarshalRegistry.registerMarshalQueryable(arrayOfMarshalQueryable[i]);
      i += 1;
    }
  }
  
  private boolean setAvailableFormats(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return false;
    }
    int[] arrayOfInt = new int[paramArrayOfInt.length];
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      arrayOfInt[i] = paramArrayOfInt[i];
      if (paramArrayOfInt[i] == 256) {
        arrayOfInt[i] = 33;
      }
      i += 1;
    }
    setBase(CameraCharacteristics.SCALER_AVAILABLE_FORMATS, arrayOfInt);
    return true;
  }
  
  private <T> void setBase(CameraCharacteristics.Key<T> paramKey, T paramT)
  {
    setBase(paramKey.getNativeKey(), paramT);
  }
  
  private <T> void setBase(CaptureRequest.Key<T> paramKey, T paramT)
  {
    setBase(paramKey.getNativeKey(), paramT);
  }
  
  private <T> void setBase(CaptureResult.Key<T> paramKey, T paramT)
  {
    setBase(paramKey.getNativeKey(), paramT);
  }
  
  private <T> void setBase(Key<T> paramKey, T paramT)
  {
    int i = paramKey.getTag();
    if (paramT == null)
    {
      writeValues(i, null);
      return;
    }
    paramKey = getMarshalerForKey(paramKey);
    byte[] arrayOfByte = new byte[paramKey.calculateMarshalSize(paramT)];
    paramKey.marshal(paramT, ByteBuffer.wrap(arrayOfByte).order(ByteOrder.nativeOrder()));
    writeValues(i, arrayOfByte);
  }
  
  private boolean setFaceRectangles(Rect[] paramArrayOfRect)
  {
    if (paramArrayOfRect == null) {
      return false;
    }
    Rect[] arrayOfRect = new Rect[paramArrayOfRect.length];
    int i = 0;
    while (i < arrayOfRect.length)
    {
      arrayOfRect[i] = new Rect(paramArrayOfRect[i].left, paramArrayOfRect[i].top, paramArrayOfRect[i].right + paramArrayOfRect[i].left, paramArrayOfRect[i].bottom + paramArrayOfRect[i].top);
      i += 1;
    }
    setBase(CaptureResult.STATISTICS_FACE_RECTANGLES, arrayOfRect);
    return true;
  }
  
  private boolean setFaces(Face[] paramArrayOfFace)
  {
    int n = 0;
    if (paramArrayOfFace == null) {
      return false;
    }
    int k = paramArrayOfFace.length;
    int i = 1;
    int i1 = paramArrayOfFace.length;
    int j = 0;
    if (j < i1)
    {
      localObject = paramArrayOfFace[j];
      if (localObject == null)
      {
        m = k - 1;
        Log.w("CameraMetadataJV", "setFaces - null face detected, skipping");
      }
      for (;;)
      {
        j += 1;
        k = m;
        break;
        m = k;
        if (((Face)localObject).getId() == -1)
        {
          i = 0;
          m = k;
        }
      }
    }
    Rect[] arrayOfRect = new Rect[k];
    byte[] arrayOfByte = new byte[k];
    Object localObject = null;
    int[] arrayOfInt = null;
    if (i != 0)
    {
      localObject = new int[k];
      arrayOfInt = new int[k * 6];
    }
    k = 0;
    int m = paramArrayOfFace.length;
    j = n;
    if (j < m)
    {
      Face localFace = paramArrayOfFace[j];
      if (localFace == null) {}
      for (;;)
      {
        j += 1;
        break;
        arrayOfRect[k] = localFace.getBounds();
        arrayOfByte[k] = ((byte)localFace.getScore());
        if (i != 0)
        {
          localObject[k] = localFace.getId();
          arrayOfInt[(k * 6 + 0)] = localFace.getLeftEyePosition().x;
          arrayOfInt[(k * 6 + 1)] = localFace.getLeftEyePosition().y;
          arrayOfInt[(k * 6 + 2)] = localFace.getRightEyePosition().x;
          arrayOfInt[(k * 6 + 3)] = localFace.getRightEyePosition().y;
          arrayOfInt[(k * 6 + 4)] = localFace.getMouthPosition().x;
          arrayOfInt[(k * 6 + 5)] = localFace.getMouthPosition().y;
        }
        k += 1;
      }
    }
    set(CaptureResult.STATISTICS_FACE_RECTANGLES, arrayOfRect);
    set(CaptureResult.STATISTICS_FACE_IDS, localObject);
    set(CaptureResult.STATISTICS_FACE_LANDMARKS, arrayOfInt);
    set(CaptureResult.STATISTICS_FACE_SCORES, arrayOfByte);
    return true;
  }
  
  private boolean setGpsLocation(Location paramLocation)
  {
    if (paramLocation == null) {
      return false;
    }
    double d1 = paramLocation.getLatitude();
    double d2 = paramLocation.getLongitude();
    double d3 = paramLocation.getAltitude();
    String str = translateLocationProviderToProcess(paramLocation.getProvider());
    long l = paramLocation.getTime();
    set(CaptureRequest.JPEG_GPS_TIMESTAMP, Long.valueOf(l));
    set(CaptureRequest.JPEG_GPS_COORDINATES, new double[] { d1, d2, d3 });
    if (str == null)
    {
      Log.w("CameraMetadataJV", "setGpsLocation - No process method, Location is not from a GPS or NETWORKprovider");
      return true;
    }
    setBase(CaptureRequest.JPEG_GPS_PROCESSING_METHOD, str);
    return true;
  }
  
  private <T> boolean setTonemapCurve(TonemapCurve paramTonemapCurve)
  {
    if (paramTonemapCurve == null) {
      return false;
    }
    float[][] arrayOfFloat = new float[3][];
    int i = 0;
    while (i <= 2)
    {
      arrayOfFloat[i] = new float[paramTonemapCurve.getPointCount(i) * 2];
      paramTonemapCurve.copyColorCurve(i, arrayOfFloat[i], 0);
      i += 1;
    }
    setBase(CaptureRequest.TONEMAP_CURVE_RED, arrayOfFloat[0]);
    setBase(CaptureRequest.TONEMAP_CURVE_GREEN, arrayOfFloat[1]);
    setBase(CaptureRequest.TONEMAP_CURVE_BLUE, arrayOfFloat[2]);
    return true;
  }
  
  public static void setupGlobalVendorTagDescriptor()
    throws ServiceSpecificException
  {
    int i = nativeSetupGlobalVendorTagDescriptor();
    if (i != 0) {
      throw new ServiceSpecificException(i, "Failure to set up global vendor tags");
    }
  }
  
  private static String translateLocationProviderToProcess(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    if (paramString.equals("gps")) {
      return "GPS";
    }
    if (paramString.equals("network")) {
      return "CELLID";
    }
    return null;
  }
  
  private static String translateProcessToLocationProvider(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    if (paramString.equals("GPS")) {
      return "gps";
    }
    if (paramString.equals("CELLID")) {
      return "network";
    }
    return null;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dumpToLog()
  {
    try
    {
      nativeDump();
      return;
    }
    catch (IOException localIOException)
    {
      Log.wtf("CameraMetadataJV", "Dump logging failed", localIOException);
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public <T> T get(CameraCharacteristics.Key<T> paramKey)
  {
    return (T)get(paramKey.getNativeKey());
  }
  
  public <T> T get(CaptureRequest.Key<T> paramKey)
  {
    return (T)get(paramKey.getNativeKey());
  }
  
  public <T> T get(CaptureResult.Key<T> paramKey)
  {
    return (T)get(paramKey.getNativeKey());
  }
  
  public <T> T get(Key<T> paramKey)
  {
    Preconditions.checkNotNull(paramKey, "key must not be null");
    GetCommand localGetCommand = (GetCommand)sGetCommandMap.get(paramKey);
    if (localGetCommand != null) {
      return (T)localGetCommand.getValue(this, paramKey);
    }
    return (T)getBase(paramKey);
  }
  
  public int getEntryCount()
  {
    return nativeGetEntryCount();
  }
  
  public long getNativeCameraMetadata()
  {
    return this.mMetadataPtr;
  }
  
  public boolean isEmpty()
  {
    return nativeIsEmpty();
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    nativeReadFromParcel(paramParcel);
  }
  
  public byte[] readValues(int paramInt)
  {
    return nativeReadValues(paramInt);
  }
  
  public <T> void set(CameraCharacteristics.Key<T> paramKey, T paramT)
  {
    set(paramKey.getNativeKey(), paramT);
  }
  
  public <T> void set(CaptureRequest.Key<T> paramKey, T paramT)
  {
    set(paramKey.getNativeKey(), paramT);
  }
  
  public <T> void set(CaptureResult.Key<T> paramKey, T paramT)
  {
    set(paramKey.getNativeKey(), paramT);
  }
  
  public <T> void set(Key<T> paramKey, T paramT)
  {
    SetCommand localSetCommand = (SetCommand)sSetCommandMap.get(paramKey);
    if (localSetCommand != null)
    {
      localSetCommand.setValue(this, paramT);
      return;
    }
    setBase(paramKey, paramT);
  }
  
  public void swap(CameraMetadataNative paramCameraMetadataNative)
  {
    nativeSwap(paramCameraMetadataNative);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    nativeWriteToParcel(paramParcel);
  }
  
  public void writeValues(int paramInt, byte[] paramArrayOfByte)
  {
    nativeWriteValues(paramInt, paramArrayOfByte);
  }
  
  public static class Key<T>
  {
    private boolean mHasTag;
    private final int mHash;
    private final String mName;
    private int mTag;
    private final Class<T> mType;
    private final TypeReference<T> mTypeReference;
    
    public Key(String paramString, TypeReference<T> paramTypeReference)
    {
      if (paramString == null) {
        throw new NullPointerException("Key needs a valid name");
      }
      if (paramTypeReference == null) {
        throw new NullPointerException("TypeReference needs to be non-null");
      }
      this.mName = paramString;
      this.mType = paramTypeReference.getRawType();
      this.mTypeReference = paramTypeReference;
      this.mHash = (this.mName.hashCode() ^ this.mTypeReference.hashCode());
    }
    
    public Key(String paramString, Class<T> paramClass)
    {
      if (paramString == null) {
        throw new NullPointerException("Key needs a valid name");
      }
      if (paramClass == null) {
        throw new NullPointerException("Type needs to be non-null");
      }
      this.mName = paramString;
      this.mType = paramClass;
      this.mTypeReference = TypeReference.createSpecializedTypeReference(paramClass);
      this.mHash = (this.mName.hashCode() ^ this.mTypeReference.hashCode());
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = false;
      if (this == paramObject) {
        return true;
      }
      if ((paramObject == null) || (hashCode() != paramObject.hashCode())) {
        return false;
      }
      if ((paramObject instanceof CaptureResult.Key)) {
        paramObject = ((CaptureResult.Key)paramObject).getNativeKey();
      }
      for (;;)
      {
        if (this.mName.equals(((Key)paramObject).mName)) {
          bool = this.mTypeReference.equals(((Key)paramObject).mTypeReference);
        }
        return bool;
        if ((paramObject instanceof CaptureRequest.Key))
        {
          paramObject = ((CaptureRequest.Key)paramObject).getNativeKey();
        }
        else if ((paramObject instanceof CameraCharacteristics.Key))
        {
          paramObject = ((CameraCharacteristics.Key)paramObject).getNativeKey();
        }
        else
        {
          if (!(paramObject instanceof Key)) {
            break;
          }
          paramObject = (Key)paramObject;
        }
      }
      return false;
    }
    
    public final String getName()
    {
      return this.mName;
    }
    
    public final int getTag()
    {
      if (!this.mHasTag)
      {
        this.mTag = CameraMetadataNative.getTag(this.mName);
        this.mHasTag = true;
      }
      return this.mTag;
    }
    
    public final Class<T> getType()
    {
      return this.mType;
    }
    
    public final TypeReference<T> getTypeReference()
    {
      return this.mTypeReference;
    }
    
    public final int hashCode()
    {
      return this.mHash;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/impl/CameraMetadataNative.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */