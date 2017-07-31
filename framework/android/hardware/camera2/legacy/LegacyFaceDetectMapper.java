package android.hardware.camera2.legacy;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.utils.ParamsUtils;
import android.util.Log;
import android.util.Size;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;

public class LegacyFaceDetectMapper
{
  private static final boolean DEBUG = false;
  private static String TAG = "LegacyFaceDetectMapper";
  private final Camera mCamera;
  private boolean mFaceDetectEnabled = false;
  private boolean mFaceDetectReporting = false;
  private boolean mFaceDetectScenePriority = false;
  private final boolean mFaceDetectSupported;
  private Camera.Face[] mFaces;
  private Camera.Face[] mFacesPrev;
  private final Object mLock = new Object();
  
  public LegacyFaceDetectMapper(Camera paramCamera, CameraCharacteristics paramCameraCharacteristics)
  {
    this.mCamera = ((Camera)Preconditions.checkNotNull(paramCamera, "camera must not be null"));
    Preconditions.checkNotNull(paramCameraCharacteristics, "characteristics must not be null");
    this.mFaceDetectSupported = ArrayUtils.contains((int[])paramCameraCharacteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES), 1);
    if (!this.mFaceDetectSupported) {
      return;
    }
    this.mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener()
    {
      public void onFaceDetection(Camera.Face[] paramAnonymousArrayOfFace, Camera arg2)
      {
        int i;
        if (paramAnonymousArrayOfFace == null) {
          i = 0;
        }
        synchronized (LegacyFaceDetectMapper.-get2(LegacyFaceDetectMapper.this))
        {
          if (LegacyFaceDetectMapper.-get1(LegacyFaceDetectMapper.this)) {
            LegacyFaceDetectMapper.-set0(LegacyFaceDetectMapper.this, paramAnonymousArrayOfFace);
          }
          while (i <= 0)
          {
            return;
            i = paramAnonymousArrayOfFace.length;
            break;
          }
          Log.d(LegacyFaceDetectMapper.-get0(), "onFaceDetection - Ignored some incoming faces sinceface detection was disabled");
        }
      }
    });
  }
  
  public void mapResultFaces(CameraMetadataNative paramCameraMetadataNative, LegacyRequest paramLegacyRequest)
  {
    Preconditions.checkNotNull(paramCameraMetadataNative, "result must not be null");
    Preconditions.checkNotNull(paramLegacyRequest, "legacyRequest must not be null");
    int i;
    boolean bool;
    Object localObject2;
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (this.mFaceDetectReporting)
        {
          i = 1;
          if (this.mFaceDetectReporting)
          {
            arrayOfFace = this.mFaces;
            bool = this.mFaceDetectScenePriority;
            localObject2 = this.mFacesPrev;
            this.mFacesPrev = arrayOfFace;
            Object localObject3 = paramLegacyRequest.characteristics;
            ??? = paramLegacyRequest.captureRequest;
            localObject2 = paramLegacyRequest.previewSize;
            Camera.Parameters localParameters = paramLegacyRequest.parameters;
            paramLegacyRequest = (Rect)((CameraCharacteristics)localObject3).get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            ??? = ParameterUtils.convertScalerCropRegion(paramLegacyRequest, (Rect)((CaptureRequest)???).get(CaptureRequest.SCALER_CROP_REGION), (Size)localObject2, localParameters);
            localObject2 = new ArrayList();
            if (arrayOfFace == null) {
              break;
            }
            int j = 0;
            int k = arrayOfFace.length;
            if (j >= k) {
              break;
            }
            localObject3 = arrayOfFace[j];
            if (localObject3 == null) {
              break label206;
            }
            ((List)localObject2).add(ParameterUtils.convertFaceFromLegacy((Camera.Face)localObject3, paramLegacyRequest, (ParameterUtils.ZoomData)???));
            j += 1;
            continue;
          }
        }
        else
        {
          i = 0;
          continue;
        }
        Camera.Face[] arrayOfFace = null;
      }
      label206:
      Log.w(TAG, "mapResultFaces - read NULL face from camera1 device");
    }
    paramCameraMetadataNative.set(CaptureResult.STATISTICS_FACES, (Face[])((List)localObject2).toArray(new Face[0]));
    paramCameraMetadataNative.set(CaptureResult.STATISTICS_FACE_DETECT_MODE, Integer.valueOf(i));
    if (bool) {
      paramCameraMetadataNative.set(CaptureResult.CONTROL_SCENE_MODE, Integer.valueOf(1));
    }
  }
  
  public void processFaceDetectMode(CaptureRequest arg1, Camera.Parameters paramParameters)
  {
    Preconditions.checkNotNull(???, "captureRequest must not be null");
    int i = ((Integer)ParamsUtils.getOrDefault(???, CaptureRequest.STATISTICS_FACE_DETECT_MODE, Integer.valueOf(0))).intValue();
    int j;
    if ((i == 0) || (this.mFaceDetectSupported))
    {
      j = ((Integer)ParamsUtils.getOrDefault(???, CaptureRequest.CONTROL_SCENE_MODE, Integer.valueOf(0))).intValue();
      if ((j == 1) && (!this.mFaceDetectSupported)) {
        break label133;
      }
    }
    switch (i)
    {
    default: 
      Log.w(TAG, "processFaceDetectMode - ignoring unknown statistics.faceDetectMode = " + i);
      return;
      Log.w(TAG, "processFaceDetectMode - Ignoring statistics.faceDetectMode; face detection is not available");
      return;
      Log.w(TAG, "processFaceDetectMode - ignoring control.sceneMode == FACE_PRIORITY; face detection is not available");
      return;
    case 2: 
      label133:
      Log.w(TAG, "processFaceDetectMode - statistics.faceDetectMode == FULL unsupported, downgrading to SIMPLE");
    }
    boolean bool;
    if (i == 0)
    {
      if (j != 1) {
        break label236;
      }
      bool = true;
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (bool != this.mFaceDetectEnabled)
        {
          if (bool)
          {
            this.mCamera.startFaceDetection();
            this.mFaceDetectEnabled = bool;
            if (j != 1) {
              break label262;
            }
            bool = true;
            this.mFaceDetectScenePriority = bool;
            if (i == 0) {
              break label268;
            }
            bool = true;
            this.mFaceDetectReporting = bool;
          }
        }
        else
        {
          return;
          bool = true;
          continue;
          label236:
          bool = false;
          continue;
        }
        this.mCamera.stopFaceDetection();
        this.mFaces = null;
      }
      label262:
      bool = false;
      continue;
      label268:
      bool = false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/LegacyFaceDetectMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */