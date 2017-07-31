package com.oneplus.camera;

import android.media.MediaRecorder;
import android.os.ParcelFileDescriptor;
import android.util.Size;

public abstract interface VideoCaptureHandler
{
  public abstract boolean prepareCamcorderProfile(Camera paramCamera, MediaRecorder paramMediaRecorder, CameraThread.VideoParams paramVideoParams);
  
  public abstract boolean saveVideo(CaptureHandle paramCaptureHandle, String paramString, Size paramSize, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/VideoCaptureHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */