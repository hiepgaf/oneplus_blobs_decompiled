package com.oneplus.camera.timelapse;

import android.content.ContentValues;
import android.content.Context;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Size;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.Rotation;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CameraThread.VideoParams;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.camera.ModeController;
import com.oneplus.camera.VideoCaptureHandler;
import com.oneplus.camera.io.VideoSaveTask;
import com.oneplus.camera.media.Resolution;

final class TimelapseController
  extends ModeController<TimelapseUI>
  implements VideoCaptureHandler
{
  static final float SPEED_RATIO = 6.0F;
  private Handle m_CaptureHandlerHandle;
  
  TimelapseController(CameraThread paramCameraThread)
  {
    super("Time-lapse Controller", paramCameraThread);
  }
  
  protected void onCameraChanged(Camera paramCamera1, Camera paramCamera2)
  {
    super.onCameraChanged(paramCamera1, paramCamera2);
    if (isEntered())
    {
      if (paramCamera1 != null) {
        paramCamera1.set(Camera.PROP_IS_TIME_LAPSE_MODE, Boolean.valueOf(false));
      }
      if (paramCamera2 != null) {
        paramCamera2.set(Camera.PROP_IS_TIME_LAPSE_MODE, Boolean.valueOf(true));
      }
    }
  }
  
  protected boolean onEnter(int paramInt)
  {
    if (!super.onEnter(paramInt)) {
      return false;
    }
    this.m_CaptureHandlerHandle = getCameraThread().setVideoCaptureHandler(this, 0);
    if (!Handle.isValid(this.m_CaptureHandlerHandle))
    {
      Log.e(this.TAG, "onEnter() - Fail to set capture handler");
      return false;
    }
    Camera localCamera = getCamera();
    if (localCamera != null) {
      localCamera.set(Camera.PROP_IS_TIME_LAPSE_MODE, Boolean.valueOf(true));
    }
    return true;
  }
  
  protected void onExit(int paramInt)
  {
    this.m_CaptureHandlerHandle = Handle.close(this.m_CaptureHandlerHandle);
    Camera localCamera = getCamera();
    if (localCamera != null) {
      localCamera.set(Camera.PROP_IS_TIME_LAPSE_MODE, Boolean.valueOf(false));
    }
    super.onExit(paramInt);
  }
  
  public boolean prepareCamcorderProfile(Camera paramCamera, MediaRecorder paramMediaRecorder, CameraThread.VideoParams paramVideoParams)
  {
    if (!isEntered())
    {
      Log.w(this.TAG, "prepareCamcorderProfile() - Not entered");
      return false;
    }
    paramVideoParams = paramVideoParams.resolution;
    if (paramVideoParams.is4kVideo()) {
      paramVideoParams = CamcorderProfile.get(1008);
    }
    for (;;)
    {
      paramMediaRecorder.setOutputFormat(2);
      paramMediaRecorder.setVideoFrameRate(paramVideoParams.videoFrameRate);
      paramMediaRecorder.setCaptureRate(paramVideoParams.videoFrameRate / 6.0F);
      paramMediaRecorder.setVideoSize(paramVideoParams.videoFrameWidth, paramVideoParams.videoFrameHeight);
      paramMediaRecorder.setVideoEncodingBitRate(paramVideoParams.videoBitRate);
      paramMediaRecorder.setVideoEncoder(paramVideoParams.videoCodec);
      int j = ((Rotation)getCameraThread().get(CameraThread.PROP_CAPTURE_ROTATION)).getDeviceOrientation() - Rotation.LANDSCAPE.getDeviceOrientation();
      int i = j;
      if (paramCamera.get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT) {
        i = -j;
      }
      j = i;
      if (i < 0) {
        j = i + 360;
      }
      Log.v(this.TAG, "prepareCamcorderProfile() - Orientation : ", Integer.valueOf(j));
      paramMediaRecorder.setOrientationHint(j);
      return true;
      if (paramVideoParams.is1080pVideo())
      {
        paramVideoParams = CamcorderProfile.get(1006);
      }
      else if (paramVideoParams.is720pVideo())
      {
        paramVideoParams = CamcorderProfile.get(1005);
      }
      else
      {
        if (!paramVideoParams.isMmsVideo()) {
          break;
        }
        paramVideoParams = CamcorderProfile.get(1002);
      }
    }
    Log.e(this.TAG, "prepareCamcorderProfile() - Unknown resolution : " + paramVideoParams);
    throw new RuntimeException("Unknown resolution : " + paramVideoParams);
  }
  
  public boolean saveVideo(CaptureHandle paramCaptureHandle, String paramString, Size paramSize, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt)
  {
    if (paramParcelFileDescriptor == null)
    {
      paramCaptureHandle = new TimelapseVideoSaveTask(getContext(), paramCaptureHandle, paramString, paramSize);
      getCameraThread().saveMedia(paramCaptureHandle);
      return true;
    }
    return false;
  }
  
  private static class TimelapseVideoSaveTask
    extends VideoSaveTask
  {
    public TimelapseVideoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, String paramString, Size paramSize)
    {
      super(paramCaptureHandle, paramString, paramSize);
    }
    
    protected boolean onPrepareGalleryDatabaseValues(String paramString, Uri paramUri, ContentValues paramContentValues)
    {
      long l2 = 0L;
      long l1 = l2;
      if (super.onPrepareGalleryDatabaseValues(paramString, paramUri, paramContentValues))
      {
        paramString = paramContentValues.getAsLong("oneplus_flags");
        l1 = l2;
        if (paramString != null) {
          l1 = paramString.longValue();
        }
      }
      paramContentValues.put("oneplus_flags", Long.valueOf(0x8 | l1));
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/timelapse/TimelapseController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */