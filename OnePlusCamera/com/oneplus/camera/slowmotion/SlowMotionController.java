package com.oneplus.camera.slowmotion;

import android.content.ContentValues;
import android.content.Context;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Size;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CameraThread.VideoParams;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.camera.ModeController;
import com.oneplus.camera.VideoCaptureHandler;
import com.oneplus.camera.VideoCaptureState;
import com.oneplus.camera.io.VideoSaveTask;

final class SlowMotionController
  extends ModeController<SlowMotionUI>
  implements VideoCaptureHandler
{
  static final float SPEED_RATIO = 0.25F;
  private Handle m_CaptureHandlerHandle;
  private Handle m_RecordingTimeRatioHandle;
  private Handle m_VideoSnapshotDisableHandle;
  
  SlowMotionController(CameraThread paramCameraThread)
  {
    super("Slow-motion Controller", paramCameraThread);
  }
  
  private void onVideoCaptureStopped()
  {
    if (!isEntered()) {
      return;
    }
    if (getCamera() == null)
    {
      Log.e(this.TAG, "onVideoCaptureStopped() - No camera");
      return;
    }
  }
  
  protected void onCameraChanged(Camera paramCamera1, Camera paramCamera2)
  {
    super.onCameraChanged(paramCamera1, paramCamera2);
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
    this.m_VideoSnapshotDisableHandle = getCameraThread().disableVideoSnapshot();
    this.m_RecordingTimeRatioHandle = getCameraThread().setRecordingTimeRatio(4.0F);
    return true;
  }
  
  protected void onExit(int paramInt)
  {
    this.m_CaptureHandlerHandle = Handle.close(this.m_CaptureHandlerHandle);
    this.m_VideoSnapshotDisableHandle = Handle.close(this.m_VideoSnapshotDisableHandle);
    this.m_RecordingTimeRatioHandle = Handle.close(this.m_RecordingTimeRatioHandle);
    super.onExit(paramInt);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    getCameraThread().addCallback(CameraThread.PROP_VIDEO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<VideoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<VideoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)paramAnonymousPropertyChangeEventArgs.getOldValue()).ordinal()])
        {
        default: 
        case 1: 
          do
          {
            return;
          } while (paramAnonymousPropertyChangeEventArgs.getNewValue() == VideoCaptureState.CAPTURING);
        }
        SlowMotionController.-wrap0(SlowMotionController.this);
      }
    });
  }
  
  public boolean prepareCamcorderProfile(Camera paramCamera, MediaRecorder paramMediaRecorder, CameraThread.VideoParams paramVideoParams)
  {
    if (!isEntered())
    {
      Log.w(this.TAG, "prepareCamcorderProfile() - Not entered");
      return false;
    }
    paramVideoParams = CamcorderProfile.get(5);
    paramMediaRecorder.setOutputFormat(2);
    paramMediaRecorder.setVideoFrameRate(paramVideoParams.videoFrameRate);
    paramMediaRecorder.setCaptureRate(paramVideoParams.videoFrameRate / 0.25F);
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
  }
  
  public boolean saveVideo(CaptureHandle paramCaptureHandle, String paramString, Size paramSize, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt)
  {
    if (paramParcelFileDescriptor == null)
    {
      paramCaptureHandle = new SlowMotionVideoSaveTask(getContext(), paramCaptureHandle, paramString, paramSize);
      getCameraThread().saveMedia(paramCaptureHandle);
      return true;
    }
    return false;
  }
  
  private static class SlowMotionVideoSaveTask
    extends VideoSaveTask
  {
    public SlowMotionVideoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, String paramString, Size paramSize)
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
      paramContentValues.put("oneplus_flags", Long.valueOf(0x4 | l1));
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/slowmotion/SlowMotionController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */