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
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.opengl.Matrix;
import android.util.Log;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CameraSource
  extends Filter
{
  private static final int NEWFRAME_TIMEOUT = 100;
  private static final int NEWFRAME_TIMEOUT_REPEAT = 10;
  private static final String TAG = "CameraSource";
  private static final String mFrameShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n";
  private static final float[] mSourceCoords = { 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F };
  private Camera mCamera;
  private GLFrame mCameraFrame;
  @GenerateFieldPort(hasDefault=true, name="id")
  private int mCameraId = 0;
  private Camera.Parameters mCameraParameters;
  private float[] mCameraTransform = new float[16];
  @GenerateFieldPort(hasDefault=true, name="framerate")
  private int mFps = 30;
  private ShaderProgram mFrameExtractor;
  @GenerateFieldPort(hasDefault=true, name="height")
  private int mHeight = 240;
  private final boolean mLogVerbose = Log.isLoggable("CameraSource", 2);
  private float[] mMappedCoords = new float[16];
  private boolean mNewFrameAvailable;
  private MutableFrameFormat mOutputFormat;
  private SurfaceTexture mSurfaceTexture;
  @GenerateFinalPort(hasDefault=true, name="waitForNewFrame")
  private boolean mWaitForNewFrame = true;
  @GenerateFieldPort(hasDefault=true, name="width")
  private int mWidth = 320;
  private SurfaceTexture.OnFrameAvailableListener onCameraFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener()
  {
    public void onFrameAvailable(SurfaceTexture arg1)
    {
      if (CameraSource.-get0(CameraSource.this)) {
        Log.v("CameraSource", "New frame from camera");
      }
      synchronized (CameraSource.this)
      {
        CameraSource.-set0(CameraSource.this, true);
        CameraSource.this.notify();
        return;
      }
    }
  };
  
  public CameraSource(String paramString)
  {
    super(paramString);
  }
  
  private void createFormats()
  {
    this.mOutputFormat = ImageFormat.create(this.mWidth, this.mHeight, 3, 3);
  }
  
  private int[] findClosestFpsRange(int paramInt, Camera.Parameters paramParameters)
  {
    Object localObject = paramParameters.getSupportedPreviewFpsRange();
    paramParameters = (int[])((List)localObject).get(0);
    Iterator localIterator = ((Iterable)localObject).iterator();
    while (localIterator.hasNext())
    {
      localObject = (int[])localIterator.next();
      if ((localObject[0] < paramInt * 1000) && (localObject[1] > paramInt * 1000) && (localObject[0] > paramParameters[0]) && (localObject[1] < paramParameters[1])) {
        paramParameters = (Camera.Parameters)localObject;
      }
    }
    if (this.mLogVerbose) {
      Log.v("CameraSource", "Requested fps: " + paramInt + ".Closest frame rate range: [" + paramParameters[0] / 1000.0D + "," + paramParameters[1] / 1000.0D + "]");
    }
    return paramParameters;
  }
  
  private int[] findClosestSize(int paramInt1, int paramInt2, Camera.Parameters paramParameters)
  {
    paramParameters = paramParameters.getSupportedPreviewSizes();
    int k = -1;
    int m = -1;
    int j = ((Camera.Size)paramParameters.get(0)).width;
    int i = ((Camera.Size)paramParameters.get(0)).height;
    paramParameters = paramParameters.iterator();
    while (paramParameters.hasNext())
    {
      Camera.Size localSize = (Camera.Size)paramParameters.next();
      int i1 = m;
      n = k;
      if (localSize.width <= paramInt1)
      {
        i1 = m;
        n = k;
        if (localSize.height <= paramInt2)
        {
          i1 = m;
          n = k;
          if (localSize.width >= k)
          {
            i1 = m;
            n = k;
            if (localSize.height >= m)
            {
              n = localSize.width;
              i1 = localSize.height;
            }
          }
        }
      }
      m = i1;
      k = n;
      if (localSize.width < j)
      {
        m = i1;
        k = n;
        if (localSize.height < i)
        {
          j = localSize.width;
          i = localSize.height;
          m = i1;
          k = n;
        }
      }
    }
    int n = m;
    m = k;
    if (k == -1)
    {
      m = j;
      n = i;
    }
    if (this.mLogVerbose) {
      Log.v("CameraSource", "Requested resolution: (" + paramInt1 + ", " + paramInt2 + "). Closest match: (" + m + ", " + n + ").");
    }
    return new int[] { m, n };
  }
  
  public void close(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("CameraSource", "Closing");
    }
    this.mCamera.release();
    this.mCamera = null;
    this.mSurfaceTexture.release();
    this.mSurfaceTexture = null;
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (paramString.equals("framerate"))
    {
      getCameraParameters();
      paramString = findClosestFpsRange(this.mFps, this.mCameraParameters);
      this.mCameraParameters.setPreviewFpsRange(paramString[0], paramString[1]);
      this.mCamera.setParameters(this.mCameraParameters);
    }
  }
  
  public Camera.Parameters getCameraParameters()
  {
    int i = 0;
    try
    {
      if (this.mCameraParameters == null)
      {
        if (this.mCamera == null)
        {
          this.mCamera = Camera.open(this.mCameraId);
          i = 1;
        }
        this.mCameraParameters = this.mCamera.getParameters();
        if (i != 0)
        {
          this.mCamera.release();
          this.mCamera = null;
        }
      }
      Object localObject1 = findClosestSize(this.mWidth, this.mHeight, this.mCameraParameters);
      this.mWidth = localObject1[0];
      this.mHeight = localObject1[1];
      this.mCameraParameters.setPreviewSize(this.mWidth, this.mHeight);
      localObject1 = findClosestFpsRange(this.mFps, this.mCameraParameters);
      this.mCameraParameters.setPreviewFpsRange(localObject1[0], localObject1[1]);
      localObject1 = this.mCameraParameters;
      return (Camera.Parameters)localObject1;
    }
    finally {}
  }
  
  public void open(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("CameraSource", "Opening");
    }
    this.mCamera = Camera.open(this.mCameraId);
    getCameraParameters();
    this.mCamera.setParameters(this.mCameraParameters);
    createFormats();
    this.mCameraFrame = ((GLFrame)paramFilterContext.getFrameManager().newBoundFrame(this.mOutputFormat, 104, 0L));
    this.mSurfaceTexture = new SurfaceTexture(this.mCameraFrame.getTextureId());
    try
    {
      this.mCamera.setPreviewTexture(this.mSurfaceTexture);
      this.mSurfaceTexture.setOnFrameAvailableListener(this.onCameraFrameAvailableListener);
      this.mNewFrameAvailable = false;
      this.mCamera.startPreview();
      return;
    }
    catch (IOException paramFilterContext)
    {
      throw new RuntimeException("Could not bind camera surface texture: " + paramFilterContext.getMessage() + "!");
    }
  }
  
  public void prepare(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("CameraSource", "Preparing");
    }
    this.mFrameExtractor = new ShaderProgram(paramFilterContext, "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n");
  }
  
  public void process(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("CameraSource", "Processing new frame");
    }
    if (this.mWaitForNewFrame)
    {
      while (!this.mNewFrameAvailable)
      {
        try
        {
          wait(100L);
        }
        catch (InterruptedException localInterruptedException) {}
        if (this.mLogVerbose) {
          Log.v("CameraSource", "Interrupted while waiting for new frame");
        }
      }
      this.mNewFrameAvailable = false;
      if (this.mLogVerbose) {
        Log.v("CameraSource", "Got new frame");
      }
    }
    this.mSurfaceTexture.updateTexImage();
    if (this.mLogVerbose) {
      Log.v("CameraSource", "Using frame extractor in thread: " + Thread.currentThread());
    }
    this.mSurfaceTexture.getTransformMatrix(this.mCameraTransform);
    Matrix.multiplyMM(this.mMappedCoords, 0, this.mCameraTransform, 0, mSourceCoords, 0);
    this.mFrameExtractor.setSourceRegion(this.mMappedCoords[0], this.mMappedCoords[1], this.mMappedCoords[4], this.mMappedCoords[5], this.mMappedCoords[8], this.mMappedCoords[9], this.mMappedCoords[12], this.mMappedCoords[13]);
    paramFilterContext = paramFilterContext.getFrameManager().newFrame(this.mOutputFormat);
    this.mFrameExtractor.process(this.mCameraFrame, paramFilterContext);
    long l = this.mSurfaceTexture.getTimestamp();
    if (this.mLogVerbose) {
      Log.v("CameraSource", "Timestamp: " + l / 1.0E9D + " s");
    }
    paramFilterContext.setTimestamp(l);
    pushOutput("video", paramFilterContext);
    paramFilterContext.release();
    if (this.mLogVerbose) {
      Log.v("CameraSource", "Done processing new frame");
    }
  }
  
  public void setCameraParameters(Camera.Parameters paramParameters)
  {
    try
    {
      paramParameters.setPreviewSize(this.mWidth, this.mHeight);
      this.mCameraParameters = paramParameters;
      if (isOpen()) {
        this.mCamera.setParameters(this.mCameraParameters);
      }
      return;
    }
    finally
    {
      paramParameters = finally;
      throw paramParameters;
    }
  }
  
  public void setupPorts()
  {
    addOutputPort("video", ImageFormat.create(3, 3));
  }
  
  public void tearDown(FilterContext paramFilterContext)
  {
    if (this.mCameraFrame != null) {
      this.mCameraFrame.release();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/videosrc/CameraSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */