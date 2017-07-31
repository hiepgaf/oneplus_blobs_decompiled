package android.filterpacks.videosrc;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;

public class MediaSource
  extends Filter
{
  private static final int NEWFRAME_TIMEOUT = 100;
  private static final int NEWFRAME_TIMEOUT_REPEAT = 10;
  private static final int PREP_TIMEOUT = 100;
  private static final int PREP_TIMEOUT_REPEAT = 100;
  private static final String TAG = "MediaSource";
  private static final float[] mSourceCoords_0 = { 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F };
  private static final float[] mSourceCoords_180 = { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F };
  private static final float[] mSourceCoords_270 = { 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F };
  private static final float[] mSourceCoords_90 = { 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F };
  private boolean mCompleted;
  @GenerateFieldPort(hasDefault=true, name="context")
  private Context mContext = null;
  private ShaderProgram mFrameExtractor;
  private final String mFrameShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n";
  private boolean mGotSize;
  private int mHeight;
  private final boolean mLogVerbose = Log.isLoggable("MediaSource", 2);
  @GenerateFieldPort(hasDefault=true, name="loop")
  private boolean mLooping = true;
  private GLFrame mMediaFrame;
  private MediaPlayer mMediaPlayer;
  private boolean mNewFrameAvailable = false;
  @GenerateFieldPort(hasDefault=true, name="orientation")
  private int mOrientation = 0;
  private boolean mOrientationUpdated;
  private MutableFrameFormat mOutputFormat;
  private boolean mPaused;
  private boolean mPlaying;
  private boolean mPrepared;
  @GenerateFieldPort(hasDefault=true, name="sourceIsUrl")
  private boolean mSelectedIsUrl = false;
  @GenerateFieldPort(hasDefault=true, name="sourceAsset")
  private AssetFileDescriptor mSourceAsset = null;
  @GenerateFieldPort(hasDefault=true, name="sourceUrl")
  private String mSourceUrl = "";
  private SurfaceTexture mSurfaceTexture;
  @GenerateFieldPort(hasDefault=true, name="volume")
  private float mVolume = 0.0F;
  @GenerateFinalPort(hasDefault=true, name="waitForNewFrame")
  private boolean mWaitForNewFrame = true;
  private int mWidth;
  private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener()
  {
    public void onCompletion(MediaPlayer arg1)
    {
      if (MediaSource.-get1(MediaSource.this)) {
        Log.v("MediaSource", "MediaPlayer has completed playback");
      }
      synchronized (MediaSource.this)
      {
        MediaSource.-set0(MediaSource.this, true);
        return;
      }
    }
  };
  private SurfaceTexture.OnFrameAvailableListener onMediaFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener()
  {
    public void onFrameAvailable(SurfaceTexture arg1)
    {
      if (MediaSource.-get1(MediaSource.this)) {
        Log.v("MediaSource", "New frame from media player");
      }
      synchronized (MediaSource.this)
      {
        if (MediaSource.-get1(MediaSource.this)) {
          Log.v("MediaSource", "New frame: notify");
        }
        MediaSource.-set3(MediaSource.this, true);
        MediaSource.this.notify();
        if (MediaSource.-get1(MediaSource.this)) {
          Log.v("MediaSource", "New frame: notify done");
        }
        return;
      }
    }
  };
  private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener()
  {
    public void onPrepared(MediaPlayer arg1)
    {
      if (MediaSource.-get1(MediaSource.this)) {
        Log.v("MediaSource", "MediaPlayer is prepared");
      }
      synchronized (MediaSource.this)
      {
        MediaSource.-set4(MediaSource.this, true);
        MediaSource.this.notify();
        return;
      }
    }
  };
  private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener()
  {
    public void onVideoSizeChanged(MediaPlayer arg1, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (MediaSource.-get1(MediaSource.this)) {
        Log.v("MediaSource", "MediaPlayer sent dimensions: " + paramAnonymousInt1 + " x " + paramAnonymousInt2);
      }
      if (!MediaSource.-get0(MediaSource.this)) {
        if ((MediaSource.-get2(MediaSource.this) == 0) || (MediaSource.-get2(MediaSource.this) == 180)) {
          MediaSource.-get3(MediaSource.this).setDimensions(paramAnonymousInt1, paramAnonymousInt2);
        }
      }
      for (;;)
      {
        MediaSource.-set5(MediaSource.this, paramAnonymousInt1);
        MediaSource.-set2(MediaSource.this, paramAnonymousInt2);
        synchronized (MediaSource.this)
        {
          do
          {
            MediaSource.-set1(MediaSource.this, true);
            MediaSource.this.notify();
            return;
            MediaSource.-get3(MediaSource.this).setDimensions(paramAnonymousInt2, paramAnonymousInt1);
            break;
          } while ((MediaSource.-get3(MediaSource.this).getWidth() == paramAnonymousInt1) && (MediaSource.-get3(MediaSource.this).getHeight() == paramAnonymousInt2));
          Log.e("MediaSource", "Multiple video size change events received!");
        }
      }
    }
  };
  
  public MediaSource(String paramString)
  {
    super(paramString);
  }
  
  private void createFormats()
  {
    this.mOutputFormat = ImageFormat.create(3, 3);
  }
  
  private boolean setupMediaPlayer(boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        this.mPrepared = false;
        this.mGotSize = false;
        this.mPlaying = false;
        this.mPaused = false;
        this.mCompleted = false;
        this.mNewFrameAvailable = false;
        if (this.mLogVerbose) {
          Log.v("MediaSource", "Setting up playback.");
        }
        if (this.mMediaPlayer != null)
        {
          if (this.mLogVerbose) {
            Log.v("MediaSource", "Resetting existing MediaPlayer.");
          }
          this.mMediaPlayer.reset();
          if (this.mMediaPlayer != null) {
            break;
          }
          throw new RuntimeException("Unable to create a MediaPlayer!");
        }
      }
      finally {}
      if (this.mLogVerbose) {
        Log.v("MediaSource", "Creating new MediaPlayer.");
      }
      this.mMediaPlayer = new MediaPlayer();
    }
    if (paramBoolean) {
      for (;;)
      {
        try
        {
          if (this.mLogVerbose) {
            Log.v("MediaSource", "Setting MediaPlayer source to URI " + this.mSourceUrl);
          }
          if (this.mContext != null) {
            continue;
          }
          this.mMediaPlayer.setDataSource(this.mSourceUrl);
        }
        catch (IOException localIOException)
        {
          Surface localSurface;
          this.mMediaPlayer.release();
          this.mMediaPlayer = null;
          if (!paramBoolean) {
            break label508;
          }
          throw new RuntimeException(String.format("Unable to set MediaPlayer to URL %s!", new Object[] { this.mSourceUrl }), localIOException);
          if (!this.mLogVerbose) {
            continue;
          }
          Log.v("MediaSource", "Setting MediaPlayer source to asset " + this.mSourceAsset);
          this.mMediaPlayer.setDataSource(this.mSourceAsset.getFileDescriptor(), this.mSourceAsset.getStartOffset(), this.mSourceAsset.getLength());
          continue;
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          this.mMediaPlayer.release();
          this.mMediaPlayer = null;
          if (!paramBoolean) {
            continue;
          }
          throw new RuntimeException(String.format("Unable to set MediaPlayer to URL %s!", new Object[] { this.mSourceUrl }), localIllegalArgumentException);
          throw new RuntimeException(String.format("Unable to set MediaPlayer to asset %s!", new Object[] { this.mSourceAsset }), localIllegalArgumentException);
        }
        this.mMediaPlayer.setLooping(this.mLooping);
        this.mMediaPlayer.setVolume(this.mVolume, this.mVolume);
        localSurface = new Surface(this.mSurfaceTexture);
        this.mMediaPlayer.setSurface(localSurface);
        localSurface.release();
        this.mMediaPlayer.setOnVideoSizeChangedListener(this.onVideoSizeChangedListener);
        this.mMediaPlayer.setOnPreparedListener(this.onPreparedListener);
        this.mMediaPlayer.setOnCompletionListener(this.onCompletionListener);
        this.mSurfaceTexture.setOnFrameAvailableListener(this.onMediaFrameAvailableListener);
        if (this.mLogVerbose) {
          Log.v("MediaSource", "Preparing MediaPlayer.");
        }
        this.mMediaPlayer.prepareAsync();
        return true;
        this.mMediaPlayer.setDataSource(this.mContext, Uri.parse(this.mSourceUrl.toString()));
      }
    }
    label508:
    throw new RuntimeException(String.format("Unable to set MediaPlayer to asset %s!", new Object[] { this.mSourceAsset }), localIllegalArgumentException);
  }
  
  public void close(FilterContext paramFilterContext)
  {
    if (this.mMediaPlayer.isPlaying()) {
      this.mMediaPlayer.stop();
    }
    this.mPrepared = false;
    this.mGotSize = false;
    this.mPlaying = false;
    this.mPaused = false;
    this.mCompleted = false;
    this.mNewFrameAvailable = false;
    this.mMediaPlayer.release();
    this.mMediaPlayer = null;
    this.mSurfaceTexture.release();
    this.mSurfaceTexture = null;
    if (this.mLogVerbose) {
      Log.v("MediaSource", "MediaSource closed");
    }
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("MediaSource", "Parameter update");
    }
    if (paramString.equals("sourceUrl")) {
      if (isOpen())
      {
        if (this.mLogVerbose) {
          Log.v("MediaSource", "Opening new source URL");
        }
        if (this.mSelectedIsUrl) {
          setupMediaPlayer(this.mSelectedIsUrl);
        }
      }
    }
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return;
                if (!paramString.equals("sourceAsset")) {
                  break;
                }
              } while (!isOpen());
              if (this.mLogVerbose) {
                Log.v("MediaSource", "Opening new source FD");
              }
            } while (this.mSelectedIsUrl);
            setupMediaPlayer(this.mSelectedIsUrl);
            return;
            if (!paramString.equals("loop")) {
              break;
            }
          } while (!isOpen());
          this.mMediaPlayer.setLooping(this.mLooping);
          return;
          if (!paramString.equals("sourceIsUrl")) {
            break;
          }
        } while (!isOpen());
        if (this.mSelectedIsUrl) {
          if (this.mLogVerbose) {
            Log.v("MediaSource", "Opening new source URL");
          }
        }
        for (;;)
        {
          setupMediaPlayer(this.mSelectedIsUrl);
          return;
          if (this.mLogVerbose) {
            Log.v("MediaSource", "Opening new source Asset");
          }
        }
        if (!paramString.equals("volume")) {
          break;
        }
      } while (!isOpen());
      this.mMediaPlayer.setVolume(this.mVolume, this.mVolume);
      return;
    } while ((!paramString.equals("orientation")) || (!this.mGotSize));
    if ((this.mOrientation == 0) || (this.mOrientation == 180)) {
      this.mOutputFormat.setDimensions(this.mWidth, this.mHeight);
    }
    for (;;)
    {
      this.mOrientationUpdated = true;
      return;
      this.mOutputFormat.setDimensions(this.mHeight, this.mWidth);
    }
  }
  
  public void open(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose)
    {
      Log.v("MediaSource", "Opening MediaSource");
      if (!this.mSelectedIsUrl) {
        break label113;
      }
      Log.v("MediaSource", "Current URL is " + this.mSourceUrl);
    }
    for (;;)
    {
      this.mMediaFrame = ((GLFrame)paramFilterContext.getFrameManager().newBoundFrame(this.mOutputFormat, 104, 0L));
      this.mSurfaceTexture = new SurfaceTexture(this.mMediaFrame.getTextureId());
      if (setupMediaPlayer(this.mSelectedIsUrl)) {
        break;
      }
      throw new RuntimeException("Error setting up MediaPlayer!");
      label113:
      Log.v("MediaSource", "Current source is Asset!");
    }
  }
  
  /* Error */
  public void pauseVideo(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 343	android/filterfw/core/Filter:isOpen	()Z
    //   6: ifeq +32 -> 38
    //   9: iload_1
    //   10: ifeq +10 -> 20
    //   13: aload_0
    //   14: getfield 187	android/filterpacks/videosrc/MediaSource:mPaused	Z
    //   17: ifeq +29 -> 46
    //   20: iload_1
    //   21: ifne +17 -> 38
    //   24: aload_0
    //   25: getfield 187	android/filterpacks/videosrc/MediaSource:mPaused	Z
    //   28: ifeq +10 -> 38
    //   31: aload_0
    //   32: getfield 195	android/filterpacks/videosrc/MediaSource:mMediaPlayer	Landroid/media/MediaPlayer;
    //   35: invokevirtual 400	android/media/MediaPlayer:start	()V
    //   38: aload_0
    //   39: iload_1
    //   40: putfield 187	android/filterpacks/videosrc/MediaSource:mPaused	Z
    //   43: aload_0
    //   44: monitorexit
    //   45: return
    //   46: aload_0
    //   47: getfield 195	android/filterpacks/videosrc/MediaSource:mMediaPlayer	Landroid/media/MediaPlayer;
    //   50: invokevirtual 403	android/media/MediaPlayer:pause	()V
    //   53: goto -15 -> 38
    //   56: astore_2
    //   57: aload_0
    //   58: monitorexit
    //   59: aload_2
    //   60: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	61	0	this	MediaSource
    //   0	61	1	paramBoolean	boolean
    //   56	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	9	56	finally
    //   13	20	56	finally
    //   24	38	56	finally
    //   38	43	56	finally
    //   46	53	56	finally
  }
  
  protected void prepare(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("MediaSource", "Preparing MediaSource");
    }
    this.mFrameExtractor = new ShaderProgram(paramFilterContext, "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n");
    this.mFrameExtractor.setSourceRect(0.0F, 1.0F, 1.0F, -1.0F);
    createFormats();
  }
  
  public void process(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("MediaSource", "Processing new frame");
    }
    if (this.mMediaPlayer == null) {
      throw new NullPointerException("Unexpected null media player!");
    }
    if (this.mCompleted)
    {
      closeOutputPort("video");
      return;
    }
    int j;
    int i;
    label129:
    float[] arrayOfFloat2;
    if (!this.mPlaying)
    {
      j = 0;
      i = j;
      if (this.mLogVerbose)
      {
        Log.v("MediaSource", "Waiting for preparation to complete");
        i = j;
      }
      if ((this.mGotSize) && (this.mPrepared))
      {
        if (this.mLogVerbose) {
          Log.v("MediaSource", "Starting playback");
        }
        this.mMediaPlayer.start();
      }
    }
    else
    {
      if ((!this.mPaused) || (!this.mPlaying)) {
        break label565;
      }
      if (this.mOrientationUpdated)
      {
        float[] arrayOfFloat1 = new float[16];
        this.mSurfaceTexture.getTransformMatrix(arrayOfFloat1);
        arrayOfFloat2 = new float[16];
        switch (this.mOrientation)
        {
        case 0: 
        default: 
          Matrix.multiplyMM(arrayOfFloat2, 0, arrayOfFloat1, 0, mSourceCoords_0, 0);
        }
      }
    }
    for (;;)
    {
      if (this.mLogVerbose)
      {
        Log.v("MediaSource", "OrientationHint = " + this.mOrientation);
        Log.v("MediaSource", String.format("SetSourceRegion: %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f", new Object[] { Float.valueOf(arrayOfFloat2[4]), Float.valueOf(arrayOfFloat2[5]), Float.valueOf(arrayOfFloat2[0]), Float.valueOf(arrayOfFloat2[1]), Float.valueOf(arrayOfFloat2[12]), Float.valueOf(arrayOfFloat2[13]), Float.valueOf(arrayOfFloat2[8]), Float.valueOf(arrayOfFloat2[9]) }));
      }
      this.mFrameExtractor.setSourceRegion(arrayOfFloat2[4], arrayOfFloat2[5], arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat2[12], arrayOfFloat2[13], arrayOfFloat2[8], arrayOfFloat2[9]);
      this.mOrientationUpdated = false;
      paramFilterContext = paramFilterContext.getFrameManager().newFrame(this.mOutputFormat);
      this.mFrameExtractor.process(this.mMediaFrame, paramFilterContext);
      long l = this.mSurfaceTexture.getTimestamp();
      if (this.mLogVerbose) {
        Log.v("MediaSource", "Timestamp: " + l / 1.0E9D + " s");
      }
      paramFilterContext.setTimestamp(l);
      pushOutput("video", paramFilterContext);
      paramFilterContext.release();
      this.mPlaying = true;
      return;
      try
      {
        wait(100L);
        if (this.mCompleted)
        {
          closeOutputPort("video");
          return;
        }
      }
      catch (InterruptedException localInterruptedException1)
      {
        for (;;) {}
        j = i + 1;
        i = j;
      }
      if (j != 100) {
        break;
      }
      this.mMediaPlayer.release();
      throw new RuntimeException("MediaPlayer timed out while preparing!");
      label565:
      if (this.mWaitForNewFrame)
      {
        if (this.mLogVerbose) {
          Log.v("MediaSource", "Waiting for new frame");
        }
        i = 0;
        for (;;)
        {
          if (!this.mNewFrameAvailable)
          {
            if (i == 10)
            {
              if (this.mCompleted)
              {
                closeOutputPort("video");
                return;
              }
              throw new RuntimeException("Timeout waiting for new frame!");
            }
            try
            {
              wait(100L);
              i += 1;
            }
            catch (InterruptedException localInterruptedException2)
            {
              for (;;)
              {
                if (this.mLogVerbose) {
                  Log.v("MediaSource", "interrupted");
                }
              }
            }
          }
        }
        this.mNewFrameAvailable = false;
        if (this.mLogVerbose) {
          Log.v("MediaSource", "Got new frame");
        }
      }
      this.mSurfaceTexture.updateTexImage();
      this.mOrientationUpdated = true;
      break label129;
      Matrix.multiplyMM(arrayOfFloat2, 0, localInterruptedException2, 0, mSourceCoords_90, 0);
      continue;
      Matrix.multiplyMM(arrayOfFloat2, 0, localInterruptedException2, 0, mSourceCoords_180, 0);
      continue;
      Matrix.multiplyMM(arrayOfFloat2, 0, localInterruptedException2, 0, mSourceCoords_270, 0);
    }
  }
  
  public void setupPorts()
  {
    addOutputPort("video", ImageFormat.create(3, 3));
  }
  
  public void tearDown(FilterContext paramFilterContext)
  {
    if (this.mMediaFrame != null) {
      this.mMediaFrame.release();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/videosrc/MediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */