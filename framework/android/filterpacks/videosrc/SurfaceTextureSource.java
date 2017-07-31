package android.filterpacks.videosrc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
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
import android.opengl.Matrix;
import android.os.ConditionVariable;
import android.util.Log;

public class SurfaceTextureSource
  extends Filter
{
  private static final String TAG = "SurfaceTextureSource";
  private static final boolean mLogVerbose = Log.isLoggable("SurfaceTextureSource", 2);
  private static final float[] mSourceCoords = { 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F };
  @GenerateFieldPort(hasDefault=true, name="closeOnTimeout")
  private boolean mCloseOnTimeout = false;
  private boolean mFirstFrame;
  private ShaderProgram mFrameExtractor;
  private float[] mFrameTransform = new float[16];
  @GenerateFieldPort(name="height")
  private int mHeight;
  private float[] mMappedCoords = new float[16];
  private GLFrame mMediaFrame;
  private ConditionVariable mNewFrameAvailable = new ConditionVariable();
  private MutableFrameFormat mOutputFormat;
  private final String mRenderShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n";
  @GenerateFinalPort(name="sourceListener")
  private SurfaceTextureSourceListener mSourceListener;
  private SurfaceTexture mSurfaceTexture;
  @GenerateFieldPort(hasDefault=true, name="waitForNewFrame")
  private boolean mWaitForNewFrame = true;
  @GenerateFieldPort(hasDefault=true, name="waitTimeout")
  private int mWaitTimeout = 1000;
  @GenerateFieldPort(name="width")
  private int mWidth;
  private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener()
  {
    public void onFrameAvailable(SurfaceTexture paramAnonymousSurfaceTexture)
    {
      if (SurfaceTextureSource.-get0()) {
        Log.v("SurfaceTextureSource", "New frame from SurfaceTexture");
      }
      SurfaceTextureSource.-get1(SurfaceTextureSource.this).open();
    }
  };
  
  public SurfaceTextureSource(String paramString)
  {
    super(paramString);
  }
  
  private void createFormats()
  {
    this.mOutputFormat = ImageFormat.create(this.mWidth, this.mHeight, 3, 3);
  }
  
  public void close(FilterContext paramFilterContext)
  {
    if (mLogVerbose) {
      Log.v("SurfaceTextureSource", "SurfaceTextureSource closed");
    }
    this.mSourceListener.onSurfaceTextureSourceReady(null);
    this.mSurfaceTexture.release();
    this.mSurfaceTexture = null;
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if ((paramString.equals("width")) || (paramString.equals("height"))) {
      this.mOutputFormat.setDimensions(this.mWidth, this.mHeight);
    }
  }
  
  public void open(FilterContext paramFilterContext)
  {
    if (mLogVerbose) {
      Log.v("SurfaceTextureSource", "Opening SurfaceTextureSource");
    }
    this.mSurfaceTexture = new SurfaceTexture(this.mMediaFrame.getTextureId());
    this.mSurfaceTexture.setOnFrameAvailableListener(this.onFrameAvailableListener);
    this.mSourceListener.onSurfaceTextureSourceReady(this.mSurfaceTexture);
    this.mFirstFrame = true;
  }
  
  protected void prepare(FilterContext paramFilterContext)
  {
    if (mLogVerbose) {
      Log.v("SurfaceTextureSource", "Preparing SurfaceTextureSource");
    }
    createFormats();
    this.mMediaFrame = ((GLFrame)paramFilterContext.getFrameManager().newBoundFrame(this.mOutputFormat, 104, 0L));
    this.mFrameExtractor = new ShaderProgram(paramFilterContext, "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n");
  }
  
  public void process(FilterContext paramFilterContext)
  {
    if (mLogVerbose) {
      Log.v("SurfaceTextureSource", "Processing new frame");
    }
    if ((this.mWaitForNewFrame) || (this.mFirstFrame))
    {
      if (this.mWaitTimeout != 0)
      {
        if (!this.mNewFrameAvailable.block(this.mWaitTimeout))
        {
          if (!this.mCloseOnTimeout) {
            throw new RuntimeException("Timeout waiting for new frame");
          }
          if (mLogVerbose) {
            Log.v("SurfaceTextureSource", "Timeout waiting for a new frame. Closing.");
          }
          closeOutputPort("video");
        }
      }
      else {
        this.mNewFrameAvailable.block();
      }
      this.mNewFrameAvailable.close();
      this.mFirstFrame = false;
    }
    this.mSurfaceTexture.updateTexImage();
    this.mSurfaceTexture.getTransformMatrix(this.mFrameTransform);
    Matrix.multiplyMM(this.mMappedCoords, 0, this.mFrameTransform, 0, mSourceCoords, 0);
    this.mFrameExtractor.setSourceRegion(this.mMappedCoords[0], this.mMappedCoords[1], this.mMappedCoords[4], this.mMappedCoords[5], this.mMappedCoords[8], this.mMappedCoords[9], this.mMappedCoords[12], this.mMappedCoords[13]);
    paramFilterContext = paramFilterContext.getFrameManager().newFrame(this.mOutputFormat);
    this.mFrameExtractor.process(this.mMediaFrame, paramFilterContext);
    paramFilterContext.setTimestamp(this.mSurfaceTexture.getTimestamp());
    pushOutput("video", paramFilterContext);
    paramFilterContext.release();
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
  
  public static abstract interface SurfaceTextureSourceListener
  {
    public abstract void onSurfaceTextureSourceReady(SurfaceTexture paramSurfaceTexture);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/videosrc/SurfaceTextureSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */