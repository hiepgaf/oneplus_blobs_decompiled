package android.media;

import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Permission;

public class CamcorderProfile
{
  public static final int QUALITY_1080P = 6;
  public static final int QUALITY_2160P = 8;
  public static final int QUALITY_2k = 10008;
  public static final int QUALITY_480P = 4;
  public static final int QUALITY_4KDCI = 10001;
  public static final int QUALITY_720P = 5;
  public static final int QUALITY_CIF = 3;
  public static final int QUALITY_HIGH = 1;
  public static final int QUALITY_HIGH_SPEED_1080P = 2004;
  public static final int QUALITY_HIGH_SPEED_2160P = 2005;
  public static final int QUALITY_HIGH_SPEED_480P = 2002;
  public static final int QUALITY_HIGH_SPEED_4KDCI = 10006;
  public static final int QUALITY_HIGH_SPEED_720P = 2003;
  public static final int QUALITY_HIGH_SPEED_CIF = 10004;
  public static final int QUALITY_HIGH_SPEED_HIGH = 2001;
  private static final int QUALITY_HIGH_SPEED_LIST_END = 2005;
  private static final int QUALITY_HIGH_SPEED_LIST_START = 2000;
  public static final int QUALITY_HIGH_SPEED_LOW = 2000;
  public static final int QUALITY_HIGH_SPEED_VGA = 10005;
  private static final int QUALITY_LIST_END = 8;
  private static final int QUALITY_LIST_START = 0;
  public static final int QUALITY_LOW = 0;
  public static final int QUALITY_QCIF = 2;
  public static final int QUALITY_QHD = 10007;
  public static final int QUALITY_QVGA = 7;
  public static final int QUALITY_TIME_LAPSE_1080P = 1006;
  public static final int QUALITY_TIME_LAPSE_2160P = 1008;
  public static final int QUALITY_TIME_LAPSE_2k = 10010;
  public static final int QUALITY_TIME_LAPSE_480P = 1004;
  public static final int QUALITY_TIME_LAPSE_4KDCI = 10003;
  public static final int QUALITY_TIME_LAPSE_720P = 1005;
  public static final int QUALITY_TIME_LAPSE_CIF = 1003;
  public static final int QUALITY_TIME_LAPSE_HIGH = 1001;
  private static final int QUALITY_TIME_LAPSE_LIST_END = 1008;
  private static final int QUALITY_TIME_LAPSE_LIST_START = 1000;
  public static final int QUALITY_TIME_LAPSE_LOW = 1000;
  public static final int QUALITY_TIME_LAPSE_QCIF = 1002;
  public static final int QUALITY_TIME_LAPSE_QHD = 10009;
  public static final int QUALITY_TIME_LAPSE_QVGA = 1007;
  public static final int QUALITY_TIME_LAPSE_VGA = 10002;
  private static final int QUALITY_VENDOR_LIST_END = 10010;
  private static final int QUALITY_VENDOR_LIST_START = 10000;
  public static final int QUALITY_VGA = 10000;
  private static final String TAG = "CamcorderProfile";
  public int audioBitRate;
  public int audioChannels;
  public int audioCodec;
  public int audioSampleRate;
  public int duration;
  public int fileFormat;
  public int quality;
  public int videoBitRate;
  public int videoCodec;
  public int videoFrameHeight;
  public int videoFrameRate;
  public int videoFrameWidth;
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  private CamcorderProfile(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12)
  {
    this.duration = paramInt1;
    this.quality = paramInt2;
    this.fileFormat = paramInt3;
    this.videoCodec = paramInt4;
    this.videoBitRate = paramInt5;
    this.videoFrameRate = paramInt6;
    this.videoFrameWidth = paramInt7;
    this.videoFrameHeight = paramInt8;
    this.audioCodec = paramInt9;
    this.audioBitRate = paramInt10;
    this.audioSampleRate = paramInt11;
    this.audioChannels = paramInt12;
  }
  
  public static CamcorderProfile get(int paramInt)
  {
    int j = Camera.getNumberOfCameras();
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    int i = 0;
    while (i < j)
    {
      Camera.getCameraInfo(i, localCameraInfo);
      if (localCameraInfo.facing == 0) {
        return get(i, paramInt);
      }
      i += 1;
    }
    return null;
  }
  
  public static CamcorderProfile get(int paramInt1, int paramInt2)
  {
    if (((paramInt2 < 0) || (paramInt2 > 8)) && ((paramInt2 < 1000) || (paramInt2 > 1008)) && ((paramInt2 < 2000) || (paramInt2 > 2005)) && ((paramInt2 < 10000) || (paramInt2 > 10010))) {
      throw new IllegalArgumentException("Unsupported quality level: " + paramInt2);
    }
    CamcorderProfile localCamcorderProfile = native_get_camcorder_profile(paramInt1, paramInt2);
    if ((OpFeatures.isSupport(new int[] { 12 })) && (localCamcorderProfile.audioChannels > 0) && (localCamcorderProfile.audioSampleRate > 0))
    {
      Context localContext = ActivityThread.currentApplication().getApplicationContext();
      Log.d("CamcorderProfile", "Requesting perm[android.permission.RECORD_AUDIO] from " + localContext.getPackageName() + ".");
      if (!new Permission(localContext).requestPermissionAuto("android.permission.RECORD_AUDIO")) {
        return null;
      }
    }
    return localCamcorderProfile;
  }
  
  public static boolean hasProfile(int paramInt)
  {
    int j = Camera.getNumberOfCameras();
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    int i = 0;
    while (i < j)
    {
      Camera.getCameraInfo(i, localCameraInfo);
      if (localCameraInfo.facing == 0) {
        return hasProfile(i, paramInt);
      }
      i += 1;
    }
    return false;
  }
  
  public static boolean hasProfile(int paramInt1, int paramInt2)
  {
    return native_has_camcorder_profile(paramInt1, paramInt2);
  }
  
  private static final native CamcorderProfile native_get_camcorder_profile(int paramInt1, int paramInt2);
  
  private static final native boolean native_has_camcorder_profile(int paramInt1, int paramInt2);
  
  private static final native void native_init();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/CamcorderProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */