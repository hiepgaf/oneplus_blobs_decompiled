package android.media;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import java.util.Arrays;
import java.util.HashMap;

public class CameraProfile
{
  public static final int QUALITY_HIGH = 2;
  public static final int QUALITY_LOW = 0;
  public static final int QUALITY_MEDIUM = 1;
  private static final HashMap<Integer, int[]> sCache = new HashMap();
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  private static int[] getImageEncodingQualityLevels(int paramInt)
  {
    int j = native_get_num_image_encoding_quality_levels(paramInt);
    if (j != 3) {
      throw new RuntimeException("Unexpected Jpeg encoding quality levels " + j);
    }
    int[] arrayOfInt = new int[j];
    int i = 0;
    while (i < j)
    {
      arrayOfInt[i] = native_get_image_encoding_quality_level(paramInt, i);
      i += 1;
    }
    Arrays.sort(arrayOfInt);
    return arrayOfInt;
  }
  
  public static int getJpegEncodingQualityParameter(int paramInt)
  {
    int j = Camera.getNumberOfCameras();
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    int i = 0;
    while (i < j)
    {
      Camera.getCameraInfo(i, localCameraInfo);
      if (localCameraInfo.facing == 0) {
        return getJpegEncodingQualityParameter(i, paramInt);
      }
      i += 1;
    }
    return 0;
  }
  
  public static int getJpegEncodingQualityParameter(int paramInt1, int paramInt2)
  {
    if ((paramInt2 < 0) || (paramInt2 > 2)) {
      throw new IllegalArgumentException("Unsupported quality level: " + paramInt2);
    }
    synchronized (sCache)
    {
      int[] arrayOfInt2 = (int[])sCache.get(Integer.valueOf(paramInt1));
      int[] arrayOfInt1 = arrayOfInt2;
      if (arrayOfInt2 == null)
      {
        arrayOfInt1 = getImageEncodingQualityLevels(paramInt1);
        sCache.put(Integer.valueOf(paramInt1), arrayOfInt1);
      }
      paramInt1 = arrayOfInt1[paramInt2];
      return paramInt1;
    }
  }
  
  private static final native int native_get_image_encoding_quality_level(int paramInt1, int paramInt2);
  
  private static final native int native_get_num_image_encoding_quality_levels(int paramInt);
  
  private static final native void native_init();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/CameraProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */