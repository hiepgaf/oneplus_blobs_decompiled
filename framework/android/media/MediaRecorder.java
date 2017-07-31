package android.media;

import android.app.ActivityThread;
import android.app.Application;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Permission;
import android.view.Surface;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;

public class MediaRecorder
{
  public static final int MEDIA_ERROR_SERVER_DIED = 100;
  public static final int MEDIA_RECORDER_ERROR_UNKNOWN = 1;
  public static final int MEDIA_RECORDER_INFO_MAX_DURATION_REACHED = 800;
  public static final int MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED = 801;
  public static final int MEDIA_RECORDER_INFO_UNKNOWN = 1;
  public static final int MEDIA_RECORDER_TRACK_INFO_COMPLETION_STATUS = 1000;
  public static final int MEDIA_RECORDER_TRACK_INFO_DATA_KBYTES = 1009;
  public static final int MEDIA_RECORDER_TRACK_INFO_DURATION_MS = 1003;
  public static final int MEDIA_RECORDER_TRACK_INFO_ENCODED_FRAMES = 1005;
  public static final int MEDIA_RECORDER_TRACK_INFO_INITIAL_DELAY_MS = 1007;
  public static final int MEDIA_RECORDER_TRACK_INFO_LIST_END = 2000;
  public static final int MEDIA_RECORDER_TRACK_INFO_LIST_START = 1000;
  public static final int MEDIA_RECORDER_TRACK_INFO_MAX_CHUNK_DUR_MS = 1004;
  public static final int MEDIA_RECORDER_TRACK_INFO_PROGRESS_IN_TIME = 1001;
  public static final int MEDIA_RECORDER_TRACK_INFO_START_OFFSET_MS = 1008;
  public static final int MEDIA_RECORDER_TRACK_INFO_TYPE = 1002;
  public static final int MEDIA_RECORDER_TRACK_INTER_CHUNK_TIME_MS = 1006;
  private static final String TAG = "MediaRecorder";
  private EventHandler mEventHandler;
  private FileDescriptor mFd;
  private long mNativeContext;
  private OnErrorListener mOnErrorListener;
  private OnInfoListener mOnInfoListener;
  private String mPath;
  private Surface mSurface;
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  public MediaRecorder()
  {
    Object localObject = Looper.myLooper();
    if (localObject != null) {
      this.mEventHandler = new EventHandler(this, (Looper)localObject);
    }
    for (;;)
    {
      localObject = ActivityThread.currentPackageName();
      native_setup(new WeakReference(this), (String)localObject, ActivityThread.currentOpPackageName());
      return;
      localObject = Looper.getMainLooper();
      if (localObject != null) {
        this.mEventHandler = new EventHandler(this, (Looper)localObject);
      } else {
        this.mEventHandler = null;
      }
    }
  }
  
  private native void _prepare()
    throws IllegalStateException, IOException;
  
  private native void _setOutputFile(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2)
    throws IllegalStateException, IOException;
  
  public static final int getAudioSourceMax()
  {
    return 9;
  }
  
  public static boolean isSystemOnlyAudioSource(int paramInt)
  {
    switch (paramInt)
    {
    case 8: 
    default: 
      return true;
    }
    return false;
  }
  
  private final native void native_finalize();
  
  private static final native void native_init();
  
  private native void native_reset();
  
  private final native void native_setInputSurface(Surface paramSurface);
  
  private final native void native_setup(Object paramObject, String paramString1, String paramString2)
    throws IllegalStateException;
  
  private static void postEventFromNative(Object paramObject1, int paramInt1, int paramInt2, int paramInt3, Object paramObject2)
  {
    paramObject1 = (MediaRecorder)((WeakReference)paramObject1).get();
    if (paramObject1 == null) {
      return;
    }
    if (((MediaRecorder)paramObject1).mEventHandler != null)
    {
      paramObject2 = ((MediaRecorder)paramObject1).mEventHandler.obtainMessage(paramInt1, paramInt2, paramInt3, paramObject2);
      ((MediaRecorder)paramObject1).mEventHandler.sendMessage((Message)paramObject2);
    }
  }
  
  private static int requestPermission(String paramString)
  {
    if (!OpFeatures.isSupport(new int[] { 12 })) {
      return 0;
    }
    try
    {
      boolean bool = new Permission(ActivityThread.currentApplication().getApplicationContext()).requestPermissionAuto(paramString);
      if (bool) {
        return 0;
      }
      return -1;
    }
    catch (Exception localException)
    {
      Log.e("MediaRecorder", "request " + paramString + " fail");
      localException.printStackTrace();
    }
    return -1;
  }
  
  private native void setParameter(String paramString);
  
  protected void finalize()
  {
    native_finalize();
  }
  
  public native int getMaxAmplitude()
    throws IllegalStateException;
  
  public native Surface getSurface();
  
  public native void pause()
    throws IllegalStateException;
  
  public void prepare()
    throws IllegalStateException, IOException
  {
    RandomAccessFile localRandomAccessFile;
    if (this.mPath != null) {
      localRandomAccessFile = new RandomAccessFile(this.mPath, "rws");
    }
    for (;;)
    {
      try
      {
        _setOutputFile(localRandomAccessFile.getFD(), 0L, 0L);
        localRandomAccessFile.close();
        _prepare();
        return;
      }
      finally
      {
        localRandomAccessFile.close();
      }
      if (this.mFd == null) {
        break;
      }
      _setOutputFile(this.mFd, 0L, 0L);
    }
    throw new IOException("No valid output file");
  }
  
  public native void release();
  
  public void reset()
  {
    native_reset();
    this.mEventHandler.removeCallbacksAndMessages(null);
  }
  
  public native void resume()
    throws IllegalStateException;
  
  public void setAudioChannels(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Number of channels is not positive");
    }
    setParameter("audio-param-number-of-channels=" + paramInt);
  }
  
  public native void setAudioEncoder(int paramInt)
    throws IllegalStateException;
  
  public void setAudioEncodingBitRate(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Audio encoding bit rate is not positive");
    }
    setParameter("audio-param-encoding-bitrate=" + paramInt);
  }
  
  public void setAudioSamplingRate(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Audio sampling rate is not positive");
    }
    setParameter("audio-param-sampling-rate=" + paramInt);
  }
  
  public native void setAudioSource(int paramInt)
    throws IllegalStateException;
  
  public void setAuxiliaryOutputFile(FileDescriptor paramFileDescriptor)
  {
    Log.w("MediaRecorder", "setAuxiliaryOutputFile(FileDescriptor) is no longer supported.");
  }
  
  public void setAuxiliaryOutputFile(String paramString)
  {
    Log.w("MediaRecorder", "setAuxiliaryOutputFile(String) is no longer supported.");
  }
  
  @Deprecated
  public native void setCamera(Camera paramCamera);
  
  public void setCaptureRate(double paramDouble)
  {
    setParameter("time-lapse-enable=1");
    setParameter("time-lapse-fps=" + paramDouble);
  }
  
  public void setInputSurface(Surface paramSurface)
  {
    if (!(paramSurface instanceof MediaCodec.PersistentSurface)) {
      throw new IllegalArgumentException("not a PersistentSurface");
    }
    native_setInputSurface(paramSurface);
  }
  
  public void setLocation(float paramFloat1, float paramFloat2)
  {
    int i = (int)(paramFloat1 * 10000.0F + 0.5D);
    int j = (int)(paramFloat2 * 10000.0F + 0.5D);
    if ((i > 900000) || (i < -900000)) {
      throw new IllegalArgumentException("Latitude: " + paramFloat1 + " out of range.");
    }
    if ((j > 1800000) || (j < -1800000)) {
      throw new IllegalArgumentException("Longitude: " + paramFloat2 + " out of range");
    }
    setParameter("param-geotag-latitude=" + i);
    setParameter("param-geotag-longitude=" + j);
  }
  
  public native void setMaxDuration(int paramInt)
    throws IllegalArgumentException;
  
  public native void setMaxFileSize(long paramLong)
    throws IllegalArgumentException;
  
  public void setOnErrorListener(OnErrorListener paramOnErrorListener)
  {
    this.mOnErrorListener = paramOnErrorListener;
  }
  
  public void setOnInfoListener(OnInfoListener paramOnInfoListener)
  {
    this.mOnInfoListener = paramOnInfoListener;
  }
  
  public void setOrientationHint(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 90) && (paramInt != 180) && (paramInt != 270)) {
      throw new IllegalArgumentException("Unsupported angle: " + paramInt);
    }
    setParameter("video-param-rotation-angle-degrees=" + paramInt);
  }
  
  public void setOutputFile(FileDescriptor paramFileDescriptor)
    throws IllegalStateException
  {
    this.mPath = null;
    this.mFd = paramFileDescriptor;
  }
  
  public void setOutputFile(String paramString)
    throws IllegalStateException
  {
    this.mFd = null;
    this.mPath = paramString;
  }
  
  public native void setOutputFormat(int paramInt)
    throws IllegalStateException;
  
  public void setPreviewDisplay(Surface paramSurface)
  {
    this.mSurface = paramSurface;
  }
  
  public void setProfile(CamcorderProfile paramCamcorderProfile)
  {
    setOutputFormat(paramCamcorderProfile.fileFormat);
    setVideoFrameRate(paramCamcorderProfile.videoFrameRate);
    setVideoSize(paramCamcorderProfile.videoFrameWidth, paramCamcorderProfile.videoFrameHeight);
    setVideoEncodingBitRate(paramCamcorderProfile.videoBitRate);
    setVideoEncoder(paramCamcorderProfile.videoCodec);
    if ((paramCamcorderProfile.quality >= 1000) && (paramCamcorderProfile.quality <= 1008)) {}
    while ((paramCamcorderProfile.quality >= 10002) && (paramCamcorderProfile.quality <= 10003)) {
      return;
    }
    setAudioEncodingBitRate(paramCamcorderProfile.audioBitRate);
    setAudioChannels(paramCamcorderProfile.audioChannels);
    setAudioSamplingRate(paramCamcorderProfile.audioSampleRate);
    setAudioEncoder(paramCamcorderProfile.audioCodec);
  }
  
  public native void setVideoEncoder(int paramInt)
    throws IllegalStateException;
  
  public void setVideoEncodingBitRate(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Video encoding bit rate is not positive");
    }
    setParameter("video-param-encoding-bitrate=" + paramInt);
  }
  
  public void setVideoEncodingProfileLevel(int paramInt1, int paramInt2)
  {
    if (paramInt1 <= 0) {
      throw new IllegalArgumentException("Video encoding profile is not positive");
    }
    if (paramInt2 <= 0) {
      throw new IllegalArgumentException("Video encoding level is not positive");
    }
    setParameter("video-param-encoder-profile=" + paramInt1);
    setParameter("video-param-encoder-level=" + paramInt2);
  }
  
  public native void setVideoFrameRate(int paramInt)
    throws IllegalStateException;
  
  public native void setVideoSize(int paramInt1, int paramInt2)
    throws IllegalStateException;
  
  public native void setVideoSource(int paramInt)
    throws IllegalStateException;
  
  public native void start()
    throws IllegalStateException;
  
  public native void stop()
    throws IllegalStateException;
  
  public final class AudioEncoder
  {
    public static final int AAC = 3;
    public static final int AAC_ELD = 5;
    public static final int AMR_NB = 1;
    public static final int AMR_WB = 2;
    public static final int DEFAULT = 0;
    public static final int EVRC = 10;
    public static final int HE_AAC = 4;
    public static final int LPCM = 12;
    public static final int QCELP = 11;
    public static final int VORBIS = 6;
    
    private AudioEncoder() {}
  }
  
  public final class AudioSource
  {
    public static final int AUDIO_SOURCE_INVALID = -1;
    public static final int CAMCORDER = 5;
    public static final int DEFAULT = 0;
    public static final int HOTWORD = 1999;
    public static final int MIC = 1;
    public static final int RADIO_TUNER = 1998;
    public static final int REMOTE_SUBMIX = 8;
    public static final int UNPROCESSED = 9;
    public static final int VOICE_CALL = 4;
    public static final int VOICE_COMMUNICATION = 7;
    public static final int VOICE_DOWNLINK = 3;
    public static final int VOICE_RECOGNITION = 6;
    public static final int VOICE_UPLINK = 2;
    
    private AudioSource() {}
  }
  
  private class EventHandler
    extends Handler
  {
    private static final int MEDIA_RECORDER_EVENT_ERROR = 1;
    private static final int MEDIA_RECORDER_EVENT_INFO = 2;
    private static final int MEDIA_RECORDER_EVENT_LIST_END = 99;
    private static final int MEDIA_RECORDER_EVENT_LIST_START = 1;
    private static final int MEDIA_RECORDER_TRACK_EVENT_ERROR = 100;
    private static final int MEDIA_RECORDER_TRACK_EVENT_INFO = 101;
    private static final int MEDIA_RECORDER_TRACK_EVENT_LIST_END = 1000;
    private static final int MEDIA_RECORDER_TRACK_EVENT_LIST_START = 100;
    private MediaRecorder mMediaRecorder;
    
    public EventHandler(MediaRecorder paramMediaRecorder, Looper paramLooper)
    {
      super();
      this.mMediaRecorder = paramMediaRecorder;
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (MediaRecorder.-get0(this.mMediaRecorder) == 0L)
      {
        Log.w("MediaRecorder", "mediarecorder went away with unhandled events");
        return;
      }
      switch (paramMessage.what)
      {
      default: 
        Log.e("MediaRecorder", "Unknown message type " + paramMessage.what);
        return;
      case 1: 
      case 100: 
        if (MediaRecorder.-get1(MediaRecorder.this) != null) {
          MediaRecorder.-get1(MediaRecorder.this).onError(this.mMediaRecorder, paramMessage.arg1, paramMessage.arg2);
        }
        return;
      }
      if (MediaRecorder.-get2(MediaRecorder.this) != null) {
        MediaRecorder.-get2(MediaRecorder.this).onInfo(this.mMediaRecorder, paramMessage.arg1, paramMessage.arg2);
      }
    }
  }
  
  public static abstract interface OnErrorListener
  {
    public abstract void onError(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2);
  }
  
  public static abstract interface OnInfoListener
  {
    public abstract void onInfo(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2);
  }
  
  public final class OutputFormat
  {
    public static final int AAC_ADIF = 5;
    public static final int AAC_ADTS = 6;
    public static final int AMR_NB = 3;
    public static final int AMR_WB = 4;
    public static final int DEFAULT = 0;
    public static final int MPEG_4 = 2;
    public static final int OUTPUT_FORMAT_MPEG2TS = 8;
    public static final int OUTPUT_FORMAT_RTP_AVP = 7;
    public static final int QCP = 20;
    public static final int RAW_AMR = 3;
    public static final int THREE_GPP = 1;
    public static final int WAVE = 21;
    public static final int WEBM = 9;
    
    private OutputFormat() {}
  }
  
  public final class VideoEncoder
  {
    public static final int DEFAULT = 0;
    public static final int H263 = 1;
    public static final int H264 = 2;
    public static final int HEVC = 5;
    public static final int MPEG_4_SP = 3;
    public static final int VP8 = 4;
    
    private VideoEncoder() {}
  }
  
  public final class VideoSource
  {
    public static final int CAMERA = 1;
    public static final int DEFAULT = 0;
    public static final int SURFACE = 2;
    
    private VideoSource() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaRecorder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */