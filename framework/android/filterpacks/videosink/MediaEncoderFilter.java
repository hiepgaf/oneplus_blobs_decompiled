package android.filterpacks.videosink;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GLEnvironment;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.filterfw.geometry.Point;
import android.filterfw.geometry.Quad;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.IOException;

public class MediaEncoderFilter
  extends Filter
{
  private static final int NO_AUDIO_SOURCE = -1;
  private static final String TAG = "MediaEncoderFilter";
  @GenerateFieldPort(hasDefault=true, name="audioSource")
  private int mAudioSource = -1;
  private boolean mCaptureTimeLapse = false;
  @GenerateFieldPort(hasDefault=true, name="errorListener")
  private MediaRecorder.OnErrorListener mErrorListener = null;
  @GenerateFieldPort(hasDefault=true, name="outputFileDescriptor")
  private FileDescriptor mFd = null;
  @GenerateFieldPort(hasDefault=true, name="framerate")
  private int mFps = 30;
  @GenerateFieldPort(hasDefault=true, name="height")
  private int mHeight = 0;
  @GenerateFieldPort(hasDefault=true, name="infoListener")
  private MediaRecorder.OnInfoListener mInfoListener = null;
  private long mLastTimeLapseFrameRealTimestampNs = 0L;
  private boolean mLogVerbose = Log.isLoggable("MediaEncoderFilter", 2);
  @GenerateFieldPort(hasDefault=true, name="maxDurationMs")
  private int mMaxDurationMs = 0;
  @GenerateFieldPort(hasDefault=true, name="maxFileSize")
  private long mMaxFileSize = 0L;
  private MediaRecorder mMediaRecorder;
  private int mNumFramesEncoded = 0;
  @GenerateFieldPort(hasDefault=true, name="orientationHint")
  private int mOrientationHint = 0;
  @GenerateFieldPort(hasDefault=true, name="outputFile")
  private String mOutputFile = new String("/sdcard/MediaEncoderOut.mp4");
  @GenerateFieldPort(hasDefault=true, name="outputFormat")
  private int mOutputFormat = 2;
  @GenerateFieldPort(hasDefault=true, name="recordingProfile")
  private CamcorderProfile mProfile = null;
  private ShaderProgram mProgram;
  @GenerateFieldPort(hasDefault=true, name="recording")
  private boolean mRecording = true;
  private boolean mRecordingActive = false;
  @GenerateFieldPort(hasDefault=true, name="recordingDoneListener")
  private OnRecordingDoneListener mRecordingDoneListener = null;
  private GLFrame mScreen;
  @GenerateFieldPort(hasDefault=true, name="inputRegion")
  private Quad mSourceRegion = new Quad(new Point(0.0F, 0.0F), new Point(1.0F, 0.0F), new Point(0.0F, 1.0F), new Point(1.0F, 1.0F));
  private int mSurfaceId;
  @GenerateFieldPort(hasDefault=true, name="timelapseRecordingIntervalUs")
  private long mTimeBetweenTimeLapseFrameCaptureUs = 0L;
  private long mTimestampNs = 0L;
  @GenerateFieldPort(hasDefault=true, name="videoEncoder")
  private int mVideoEncoder = 2;
  @GenerateFieldPort(hasDefault=true, name="width")
  private int mWidth = 0;
  
  public MediaEncoderFilter(String paramString)
  {
    super(paramString);
  }
  
  private void startRecording(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("MediaEncoderFilter", "Starting recording");
    }
    MutableFrameFormat localMutableFrameFormat = new MutableFrameFormat(2, 3);
    localMutableFrameFormat.setBytesPerSample(4);
    int j;
    if ((this.mWidth > 0) && (this.mHeight > 0))
    {
      i = 1;
      if ((this.mProfile != null) && (i == 0)) {
        break label170;
      }
      j = this.mWidth;
    }
    for (int i = this.mHeight;; i = this.mProfile.videoFrameHeight)
    {
      localMutableFrameFormat.setDimensions(j, i);
      this.mScreen = ((GLFrame)paramFilterContext.getFrameManager().newBoundFrame(localMutableFrameFormat, 101, 0L));
      this.mMediaRecorder = new MediaRecorder();
      updateMediaRecorderParams();
      try
      {
        this.mMediaRecorder.prepare();
        this.mMediaRecorder.start();
        if (this.mLogVerbose) {
          Log.v("MediaEncoderFilter", "Open: registering surface from Mediarecorder");
        }
        this.mSurfaceId = paramFilterContext.getGLEnvironment().registerSurfaceFromMediaRecorder(this.mMediaRecorder);
        this.mNumFramesEncoded = 0;
        this.mRecordingActive = true;
        return;
      }
      catch (Exception paramFilterContext)
      {
        throw new RuntimeException("Unknown Exception inMediaRecorder.prepare()!", paramFilterContext);
      }
      catch (IOException paramFilterContext)
      {
        throw new RuntimeException("IOException inMediaRecorder.prepare()!", paramFilterContext);
      }
      catch (IllegalStateException paramFilterContext)
      {
        throw paramFilterContext;
      }
      i = 0;
      break;
      label170:
      j = this.mProfile.videoFrameWidth;
    }
  }
  
  private void stopRecording(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("MediaEncoderFilter", "Stopping recording");
    }
    this.mRecordingActive = false;
    this.mNumFramesEncoded = 0;
    paramFilterContext = paramFilterContext.getGLEnvironment();
    if (this.mLogVerbose) {
      Log.v("MediaEncoderFilter", String.format("Unregistering surface %d", new Object[] { Integer.valueOf(this.mSurfaceId) }));
    }
    paramFilterContext.unregisterSurfaceId(this.mSurfaceId);
    try
    {
      this.mMediaRecorder.stop();
      this.mMediaRecorder.release();
      this.mMediaRecorder = null;
      this.mScreen.release();
      this.mScreen = null;
      if (this.mRecordingDoneListener != null) {
        this.mRecordingDoneListener.onRecordingDone();
      }
      return;
    }
    catch (RuntimeException paramFilterContext)
    {
      throw new MediaRecorderStopException("MediaRecorder.stop() failed!", paramFilterContext);
    }
  }
  
  private void updateMediaRecorderParams()
  {
    boolean bool = false;
    if (this.mTimeBetweenTimeLapseFrameCaptureUs > 0L) {
      bool = true;
    }
    this.mCaptureTimeLapse = bool;
    this.mMediaRecorder.setVideoSource(2);
    if ((!this.mCaptureTimeLapse) && (this.mAudioSource != -1)) {
      this.mMediaRecorder.setAudioSource(this.mAudioSource);
    }
    if (this.mProfile != null)
    {
      this.mMediaRecorder.setProfile(this.mProfile);
      this.mFps = this.mProfile.videoFrameRate;
      if ((this.mWidth > 0) && (this.mHeight > 0)) {
        this.mMediaRecorder.setVideoSize(this.mWidth, this.mHeight);
      }
    }
    for (;;)
    {
      this.mMediaRecorder.setOrientationHint(this.mOrientationHint);
      this.mMediaRecorder.setOnInfoListener(this.mInfoListener);
      this.mMediaRecorder.setOnErrorListener(this.mErrorListener);
      if (this.mFd != null) {
        this.mMediaRecorder.setOutputFile(this.mFd);
      }
      try
      {
        for (;;)
        {
          this.mMediaRecorder.setMaxFileSize(this.mMaxFileSize);
          this.mMediaRecorder.setMaxDuration(this.mMaxDurationMs);
          return;
          this.mMediaRecorder.setOutputFormat(this.mOutputFormat);
          this.mMediaRecorder.setVideoEncoder(this.mVideoEncoder);
          this.mMediaRecorder.setVideoSize(this.mWidth, this.mHeight);
          this.mMediaRecorder.setVideoFrameRate(this.mFps);
          break;
          this.mMediaRecorder.setOutputFile(this.mOutputFile);
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.w("MediaEncoderFilter", "Setting maxFileSize on MediaRecorder unsuccessful! " + localException.getMessage());
        }
      }
    }
  }
  
  private void updateSourceRegion()
  {
    Quad localQuad = new Quad();
    localQuad.p0 = this.mSourceRegion.p2;
    localQuad.p1 = this.mSourceRegion.p3;
    localQuad.p2 = this.mSourceRegion.p0;
    localQuad.p3 = this.mSourceRegion.p1;
    this.mProgram.setSourceRegion(localQuad);
  }
  
  public void close(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("MediaEncoderFilter", "Closing");
    }
    if (this.mRecordingActive) {
      stopRecording(paramFilterContext);
    }
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("MediaEncoderFilter", "Port " + paramString + " has been updated");
    }
    if (paramString.equals("recording")) {
      return;
    }
    if (paramString.equals("inputRegion"))
    {
      if (isOpen()) {
        updateSourceRegion();
      }
      return;
    }
    if ((isOpen()) && (this.mRecordingActive)) {
      throw new RuntimeException("Cannot change recording parameters when the filter is recording!");
    }
  }
  
  public void open(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("MediaEncoderFilter", "Opening");
    }
    updateSourceRegion();
    if (this.mRecording) {
      startRecording(paramFilterContext);
    }
  }
  
  public void prepare(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("MediaEncoderFilter", "Preparing");
    }
    this.mProgram = ShaderProgram.createIdentity(paramFilterContext);
    this.mRecordingActive = false;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    GLEnvironment localGLEnvironment = paramFilterContext.getGLEnvironment();
    Frame localFrame = pullInput("videoframe");
    if ((!this.mRecordingActive) && (this.mRecording)) {
      startRecording(paramFilterContext);
    }
    if ((!this.mRecordingActive) || (this.mRecording)) {}
    while (!this.mRecordingActive)
    {
      return;
      stopRecording(paramFilterContext);
    }
    if (this.mCaptureTimeLapse)
    {
      if (!skipFrameAndModifyTimestamp(localFrame.getTimestamp())) {}
    }
    else {
      this.mTimestampNs = localFrame.getTimestamp();
    }
    localGLEnvironment.activateSurfaceWithId(this.mSurfaceId);
    this.mProgram.process(localFrame, this.mScreen);
    localGLEnvironment.setSurfaceTimestamp(this.mTimestampNs);
    localGLEnvironment.swapBuffers();
    this.mNumFramesEncoded += 1;
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("videoframe", ImageFormat.create(3, 3));
  }
  
  public boolean skipFrameAndModifyTimestamp(long paramLong)
  {
    if (this.mNumFramesEncoded == 0)
    {
      this.mLastTimeLapseFrameRealTimestampNs = paramLong;
      this.mTimestampNs = paramLong;
      if (this.mLogVerbose) {
        Log.v("MediaEncoderFilter", "timelapse: FIRST frame, last real t= " + this.mLastTimeLapseFrameRealTimestampNs + ", setting t = " + this.mTimestampNs);
      }
      return false;
    }
    if ((this.mNumFramesEncoded >= 2) && (paramLong < this.mLastTimeLapseFrameRealTimestampNs + this.mTimeBetweenTimeLapseFrameCaptureUs * 1000L))
    {
      if (this.mLogVerbose) {
        Log.v("MediaEncoderFilter", "timelapse: skipping intermediate frame");
      }
      return true;
    }
    if (this.mLogVerbose) {
      Log.v("MediaEncoderFilter", "timelapse: encoding frame, Timestamp t = " + paramLong + ", last real t= " + this.mLastTimeLapseFrameRealTimestampNs + ", interval = " + this.mTimeBetweenTimeLapseFrameCaptureUs);
    }
    this.mLastTimeLapseFrameRealTimestampNs = paramLong;
    this.mTimestampNs += 1000000000L / this.mFps;
    if (this.mLogVerbose) {
      Log.v("MediaEncoderFilter", "timelapse: encoding frame, setting t = " + this.mTimestampNs + ", delta t = " + 1000000000L / this.mFps + ", fps = " + this.mFps);
    }
    return false;
  }
  
  public void tearDown(FilterContext paramFilterContext)
  {
    if (this.mMediaRecorder != null) {
      this.mMediaRecorder.release();
    }
    if (this.mScreen != null) {
      this.mScreen.release();
    }
  }
  
  public static abstract interface OnRecordingDoneListener
  {
    public abstract void onRecordingDone();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/videosink/MediaEncoderFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */