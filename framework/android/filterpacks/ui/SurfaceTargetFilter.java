package android.filterpacks.ui;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GLEnvironment;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.util.Log;
import android.view.Surface;

public class SurfaceTargetFilter
  extends Filter
{
  private static final String TAG = "SurfaceRenderFilter";
  private final int RENDERMODE_FILL_CROP = 2;
  private final int RENDERMODE_FIT = 1;
  private final int RENDERMODE_STRETCH = 0;
  private float mAspectRatio = 1.0F;
  private GLEnvironment mGlEnv;
  private boolean mLogVerbose = Log.isLoggable("SurfaceRenderFilter", 2);
  private ShaderProgram mProgram;
  private int mRenderMode = 1;
  @GenerateFieldPort(hasDefault=true, name="renderMode")
  private String mRenderModeString;
  private GLFrame mScreen;
  @GenerateFieldPort(name="oheight")
  private int mScreenHeight;
  @GenerateFieldPort(name="owidth")
  private int mScreenWidth;
  @GenerateFinalPort(name="surface")
  private Surface mSurface;
  private int mSurfaceId = -1;
  
  public SurfaceTargetFilter(String paramString)
  {
    super(paramString);
  }
  
  private void registerSurface()
  {
    this.mSurfaceId = this.mGlEnv.registerSurface(this.mSurface);
    if (this.mSurfaceId < 0) {
      throw new RuntimeException("Could not register Surface: " + this.mSurface);
    }
  }
  
  private void unregisterSurface()
  {
    if (this.mSurfaceId > 0) {
      this.mGlEnv.unregisterSurfaceId(this.mSurfaceId);
    }
  }
  
  private void updateTargetRect()
  {
    float f;
    if ((this.mScreenWidth > 0) && (this.mScreenHeight > 0) && (this.mProgram != null)) {
      f = this.mScreenWidth / this.mScreenHeight / this.mAspectRatio;
    }
    switch (this.mRenderMode)
    {
    default: 
      return;
    case 0: 
      this.mProgram.setTargetRect(0.0F, 0.0F, 1.0F, 1.0F);
      return;
    case 1: 
      if (f > 1.0F)
      {
        this.mProgram.setTargetRect(0.5F - 0.5F / f, 0.0F, 1.0F / f, 1.0F);
        return;
      }
      this.mProgram.setTargetRect(0.0F, 0.5F - 0.5F * f, 1.0F, f);
      return;
    }
    if (f > 1.0F)
    {
      this.mProgram.setTargetRect(0.0F, 0.5F - 0.5F * f, 1.0F, f);
      return;
    }
    this.mProgram.setTargetRect(0.5F - 0.5F / f, 0.0F, 1.0F / f, 1.0F);
  }
  
  public void close(FilterContext paramFilterContext)
  {
    unregisterSurface();
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    this.mScreen.setViewport(0, 0, this.mScreenWidth, this.mScreenHeight);
    updateTargetRect();
  }
  
  public void open(FilterContext paramFilterContext)
  {
    registerSurface();
  }
  
  public void prepare(FilterContext paramFilterContext)
  {
    this.mGlEnv = paramFilterContext.getGLEnvironment();
    this.mProgram = ShaderProgram.createIdentity(paramFilterContext);
    this.mProgram.setSourceRect(0.0F, 1.0F, 1.0F, -1.0F);
    this.mProgram.setClearsOutput(true);
    this.mProgram.setClearColor(0.0F, 0.0F, 0.0F);
    MutableFrameFormat localMutableFrameFormat = ImageFormat.create(this.mScreenWidth, this.mScreenHeight, 3, 3);
    this.mScreen = ((GLFrame)paramFilterContext.getFrameManager().newBoundFrame(localMutableFrameFormat, 101, 0L));
    updateRenderMode();
  }
  
  public void process(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("SurfaceRenderFilter", "Starting frame processing");
    }
    Frame localFrame = pullInput("frame");
    int i = 0;
    float f = localFrame.getFormat().getWidth() / localFrame.getFormat().getHeight();
    if (f != this.mAspectRatio)
    {
      if (this.mLogVerbose) {
        Log.v("SurfaceRenderFilter", "New aspect ratio: " + f + ", previously: " + this.mAspectRatio);
      }
      this.mAspectRatio = f;
      updateTargetRect();
    }
    if (this.mLogVerbose) {
      Log.v("SurfaceRenderFilter", "Got input format: " + localFrame.getFormat());
    }
    if (localFrame.getFormat().getTarget() != 3)
    {
      paramFilterContext = paramFilterContext.getFrameManager().duplicateFrameToTarget(localFrame, 3);
      i = 1;
    }
    for (;;)
    {
      this.mGlEnv.activateSurfaceWithId(this.mSurfaceId);
      this.mProgram.process(paramFilterContext, this.mScreen);
      this.mGlEnv.swapBuffers();
      if (i != 0) {
        paramFilterContext.release();
      }
      return;
      paramFilterContext = localFrame;
    }
  }
  
  public void setupPorts()
  {
    if (this.mSurface == null) {
      throw new RuntimeException("NULL Surface passed to SurfaceTargetFilter");
    }
    addMaskedInputPort("frame", ImageFormat.create(3));
  }
  
  public void tearDown(FilterContext paramFilterContext)
  {
    if (this.mScreen != null) {
      this.mScreen.release();
    }
  }
  
  public void updateRenderMode()
  {
    if (this.mRenderModeString != null)
    {
      if (!this.mRenderModeString.equals("stretch")) {
        break label29;
      }
      this.mRenderMode = 0;
    }
    for (;;)
    {
      updateTargetRect();
      return;
      label29:
      if (this.mRenderModeString.equals("fit"))
      {
        this.mRenderMode = 1;
      }
      else
      {
        if (!this.mRenderModeString.equals("fill_crop")) {
          break;
        }
        this.mRenderMode = 2;
      }
    }
    throw new RuntimeException("Unknown render mode '" + this.mRenderModeString + "'!");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/ui/SurfaceTargetFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */