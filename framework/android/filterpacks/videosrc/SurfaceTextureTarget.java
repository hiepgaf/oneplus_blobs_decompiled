package android.filterpacks.videosrc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GLEnvironment;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.filterfw.geometry.Point;
import android.filterfw.geometry.Quad;
import android.graphics.SurfaceTexture;
import android.util.Log;

public class SurfaceTextureTarget
  extends Filter
{
  private static final String TAG = "SurfaceTextureTarget";
  private final int RENDERMODE_CUSTOMIZE = 3;
  private final int RENDERMODE_FILL_CROP = 2;
  private final int RENDERMODE_FIT = 1;
  private final int RENDERMODE_STRETCH = 0;
  private float mAspectRatio = 1.0F;
  private boolean mLogVerbose = Log.isLoggable("SurfaceTextureTarget", 2);
  private ShaderProgram mProgram;
  private int mRenderMode = 1;
  @GenerateFieldPort(hasDefault=true, name="renderMode")
  private String mRenderModeString;
  private GLFrame mScreen;
  @GenerateFinalPort(name="height")
  private int mScreenHeight;
  @GenerateFinalPort(name="width")
  private int mScreenWidth;
  @GenerateFieldPort(hasDefault=true, name="sourceQuad")
  private Quad mSourceQuad = new Quad(new Point(0.0F, 1.0F), new Point(1.0F, 1.0F), new Point(0.0F, 0.0F), new Point(1.0F, 0.0F));
  private int mSurfaceId;
  @GenerateFinalPort(name="surfaceTexture")
  private SurfaceTexture mSurfaceTexture;
  @GenerateFieldPort(hasDefault=true, name="targetQuad")
  private Quad mTargetQuad = new Quad(new Point(0.0F, 0.0F), new Point(1.0F, 0.0F), new Point(0.0F, 1.0F), new Point(1.0F, 1.0F));
  
  public SurfaceTextureTarget(String paramString)
  {
    super(paramString);
  }
  
  private void updateTargetRect()
  {
    if (this.mLogVerbose) {
      Log.v("SurfaceTextureTarget", "updateTargetRect. Thread: " + Thread.currentThread());
    }
    float f2;
    if ((this.mScreenWidth > 0) && (this.mScreenHeight > 0) && (this.mProgram != null))
    {
      float f1 = this.mScreenWidth / this.mScreenHeight;
      f2 = f1 / this.mAspectRatio;
      if (this.mLogVerbose) {
        Log.v("SurfaceTextureTarget", "UTR. screen w = " + this.mScreenWidth + " x screen h = " + this.mScreenHeight + " Screen AR: " + f1 + ", frame AR: " + this.mAspectRatio + ", relative AR: " + f2);
      }
      if ((f2 == 1.0F) && (this.mRenderMode != 3))
      {
        this.mProgram.setTargetRect(0.0F, 0.0F, 1.0F, 1.0F);
        this.mProgram.setClearsOutput(false);
      }
    }
    else
    {
      return;
    }
    switch (this.mRenderMode)
    {
    }
    for (;;)
    {
      if (this.mLogVerbose) {
        Log.v("SurfaceTextureTarget", "UTR. quad: " + this.mTargetQuad);
      }
      this.mProgram.setTargetRegion(this.mTargetQuad);
      return;
      this.mTargetQuad.p0.set(0.0F, 0.0F);
      this.mTargetQuad.p1.set(1.0F, 0.0F);
      this.mTargetQuad.p2.set(0.0F, 1.0F);
      this.mTargetQuad.p3.set(1.0F, 1.0F);
      this.mProgram.setClearsOutput(false);
      continue;
      if (f2 > 1.0F)
      {
        this.mTargetQuad.p0.set(0.5F - 0.5F / f2, 0.0F);
        this.mTargetQuad.p1.set(0.5F / f2 + 0.5F, 0.0F);
        this.mTargetQuad.p2.set(0.5F - 0.5F / f2, 1.0F);
        this.mTargetQuad.p3.set(0.5F / f2 + 0.5F, 1.0F);
      }
      for (;;)
      {
        this.mProgram.setClearsOutput(true);
        break;
        this.mTargetQuad.p0.set(0.0F, 0.5F - 0.5F * f2);
        this.mTargetQuad.p1.set(1.0F, 0.5F - 0.5F * f2);
        this.mTargetQuad.p2.set(0.0F, 0.5F * f2 + 0.5F);
        this.mTargetQuad.p3.set(1.0F, 0.5F * f2 + 0.5F);
      }
      if (f2 > 1.0F)
      {
        this.mTargetQuad.p0.set(0.0F, 0.5F - 0.5F * f2);
        this.mTargetQuad.p1.set(1.0F, 0.5F - 0.5F * f2);
        this.mTargetQuad.p2.set(0.0F, 0.5F * f2 + 0.5F);
        this.mTargetQuad.p3.set(1.0F, 0.5F * f2 + 0.5F);
      }
      for (;;)
      {
        this.mProgram.setClearsOutput(true);
        break;
        this.mTargetQuad.p0.set(0.5F - 0.5F / f2, 0.0F);
        this.mTargetQuad.p1.set(0.5F / f2 + 0.5F, 0.0F);
        this.mTargetQuad.p2.set(0.5F - 0.5F / f2, 1.0F);
        this.mTargetQuad.p3.set(0.5F / f2 + 0.5F, 1.0F);
      }
      this.mProgram.setSourceRegion(this.mSourceQuad);
    }
  }
  
  public void close(FilterContext paramFilterContext)
  {
    try
    {
      if (this.mSurfaceId > 0)
      {
        paramFilterContext.getGLEnvironment().unregisterSurfaceId(this.mSurfaceId);
        this.mSurfaceId = -1;
      }
      return;
    }
    finally
    {
      paramFilterContext = finally;
      throw paramFilterContext;
    }
  }
  
  public void disconnect(FilterContext paramFilterContext)
  {
    try
    {
      if (this.mLogVerbose) {
        Log.v("SurfaceTextureTarget", "disconnect");
      }
      if (this.mSurfaceTexture == null)
      {
        Log.d("SurfaceTextureTarget", "SurfaceTexture is already null. Nothing to disconnect.");
        return;
      }
      this.mSurfaceTexture = null;
      if (this.mSurfaceId > 0)
      {
        paramFilterContext.getGLEnvironment().unregisterSurfaceId(this.mSurfaceId);
        this.mSurfaceId = -1;
      }
      return;
    }
    finally {}
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("SurfaceTextureTarget", "FPVU. Thread: " + Thread.currentThread());
    }
    updateRenderMode();
  }
  
  public void open(FilterContext paramFilterContext)
  {
    try
    {
      if (this.mSurfaceTexture == null)
      {
        Log.e("SurfaceTextureTarget", "SurfaceTexture is null!!");
        throw new RuntimeException("Could not register SurfaceTexture: " + this.mSurfaceTexture);
      }
    }
    finally {}
    this.mSurfaceId = paramFilterContext.getGLEnvironment().registerSurfaceTexture(this.mSurfaceTexture, this.mScreenWidth, this.mScreenHeight);
    if (this.mSurfaceId <= 0) {
      throw new RuntimeException("Could not register SurfaceTexture: " + this.mSurfaceTexture);
    }
  }
  
  public void prepare(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("SurfaceTextureTarget", "Prepare. Thread: " + Thread.currentThread());
    }
    this.mProgram = ShaderProgram.createIdentity(paramFilterContext);
    this.mProgram.setSourceRect(0.0F, 1.0F, 1.0F, -1.0F);
    this.mProgram.setClearColor(0.0F, 0.0F, 0.0F);
    updateRenderMode();
    MutableFrameFormat localMutableFrameFormat = new MutableFrameFormat(2, 3);
    localMutableFrameFormat.setBytesPerSample(4);
    localMutableFrameFormat.setDimensions(this.mScreenWidth, this.mScreenHeight);
    this.mScreen = ((GLFrame)paramFilterContext.getFrameManager().newBoundFrame(localMutableFrameFormat, 101, 0L));
  }
  
  /* Error */
  public void process(FilterContext paramFilterContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 169	android/filterpacks/videosrc/SurfaceTextureTarget:mSurfaceId	I
    //   6: istore_3
    //   7: iload_3
    //   8: ifgt +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_1
    //   15: invokevirtual 175	android/filterfw/core/FilterContext:getGLEnvironment	()Landroid/filterfw/core/GLEnvironment;
    //   18: astore 5
    //   20: aload_0
    //   21: ldc -1
    //   23: invokevirtual 259	android/filterfw/core/Filter:pullInput	(Ljava/lang/String;)Landroid/filterfw/core/Frame;
    //   26: astore 4
    //   28: iconst_0
    //   29: istore_3
    //   30: aload 4
    //   32: invokevirtual 265	android/filterfw/core/Frame:getFormat	()Landroid/filterfw/core/FrameFormat;
    //   35: invokevirtual 271	android/filterfw/core/FrameFormat:getWidth	()I
    //   38: i2f
    //   39: aload 4
    //   41: invokevirtual 265	android/filterfw/core/Frame:getFormat	()Landroid/filterfw/core/FrameFormat;
    //   44: invokevirtual 274	android/filterfw/core/FrameFormat:getHeight	()I
    //   47: i2f
    //   48: fdiv
    //   49: fstore_2
    //   50: fload_2
    //   51: aload_0
    //   52: getfield 72	android/filterpacks/videosrc/SurfaceTextureTarget:mAspectRatio	F
    //   55: fcmpl
    //   56: ifeq +70 -> 126
    //   59: aload_0
    //   60: getfield 80	android/filterpacks/videosrc/SurfaceTextureTarget:mLogVerbose	Z
    //   63: ifeq +54 -> 117
    //   66: ldc 8
    //   68: new 85	java/lang/StringBuilder
    //   71: dup
    //   72: invokespecial 87	java/lang/StringBuilder:<init>	()V
    //   75: ldc_w 276
    //   78: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   81: fload_2
    //   82: invokevirtual 121	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   85: ldc_w 278
    //   88: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: aload_0
    //   92: getfield 72	android/filterpacks/videosrc/SurfaceTextureTarget:mAspectRatio	F
    //   95: invokevirtual 121	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   98: ldc_w 280
    //   101: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   104: invokestatic 99	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   107: invokevirtual 102	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   110: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   113: invokestatic 110	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   116: pop
    //   117: aload_0
    //   118: fload_2
    //   119: putfield 72	android/filterpacks/videosrc/SurfaceTextureTarget:mAspectRatio	F
    //   122: aload_0
    //   123: invokespecial 282	android/filterpacks/videosrc/SurfaceTextureTarget:updateTargetRect	()V
    //   126: aload 4
    //   128: invokevirtual 265	android/filterfw/core/Frame:getFormat	()Landroid/filterfw/core/FrameFormat;
    //   131: invokevirtual 285	android/filterfw/core/FrameFormat:getTarget	()I
    //   134: iconst_3
    //   135: if_icmpeq +64 -> 199
    //   138: aload_1
    //   139: invokevirtual 242	android/filterfw/core/FilterContext:getFrameManager	()Landroid/filterfw/core/FrameManager;
    //   142: aload 4
    //   144: iconst_3
    //   145: invokevirtual 289	android/filterfw/core/FrameManager:duplicateFrameToTarget	(Landroid/filterfw/core/Frame;I)Landroid/filterfw/core/Frame;
    //   148: astore_1
    //   149: iconst_1
    //   150: istore_3
    //   151: aload 5
    //   153: aload_0
    //   154: getfield 169	android/filterpacks/videosrc/SurfaceTextureTarget:mSurfaceId	I
    //   157: invokevirtual 292	android/filterfw/core/GLEnvironment:activateSurfaceWithId	(I)V
    //   160: aload_0
    //   161: getfield 116	android/filterpacks/videosrc/SurfaceTextureTarget:mProgram	Landroid/filterfw/core/ShaderProgram;
    //   164: aload_1
    //   165: aload_0
    //   166: getfield 252	android/filterpacks/videosrc/SurfaceTextureTarget:mScreen	Landroid/filterfw/core/GLFrame;
    //   169: invokevirtual 297	android/filterfw/core/Program:process	(Landroid/filterfw/core/Frame;Landroid/filterfw/core/Frame;)V
    //   172: aload 5
    //   174: aload 4
    //   176: invokevirtual 301	android/filterfw/core/Frame:getTimestamp	()J
    //   179: invokevirtual 305	android/filterfw/core/GLEnvironment:setSurfaceTimestamp	(J)V
    //   182: aload 5
    //   184: invokevirtual 308	android/filterfw/core/GLEnvironment:swapBuffers	()V
    //   187: iload_3
    //   188: ifeq +8 -> 196
    //   191: aload_1
    //   192: invokevirtual 312	android/filterfw/core/Frame:release	()Landroid/filterfw/core/Frame;
    //   195: pop
    //   196: aload_0
    //   197: monitorexit
    //   198: return
    //   199: aload 4
    //   201: astore_1
    //   202: goto -51 -> 151
    //   205: astore_1
    //   206: aload_0
    //   207: monitorexit
    //   208: aload_1
    //   209: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	210	0	this	SurfaceTextureTarget
    //   0	210	1	paramFilterContext	FilterContext
    //   49	70	2	f	float
    //   6	182	3	i	int
    //   26	174	4	localFrame	Frame
    //   18	165	5	localGLEnvironment	GLEnvironment
    // Exception table:
    //   from	to	target	type
    //   2	7	205	finally
    //   14	28	205	finally
    //   30	117	205	finally
    //   117	126	205	finally
    //   126	149	205	finally
    //   151	187	205	finally
    //   191	196	205	finally
  }
  
  public void setupPorts()
  {
    try
    {
      if (this.mSurfaceTexture == null) {
        throw new RuntimeException("Null SurfaceTexture passed to SurfaceTextureTarget");
      }
    }
    finally {}
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
    if (this.mLogVerbose) {
      Log.v("SurfaceTextureTarget", "updateRenderMode. Thread: " + Thread.currentThread());
    }
    if (this.mRenderModeString != null)
    {
      if (!this.mRenderModeString.equals("stretch")) {
        break label65;
      }
      this.mRenderMode = 0;
    }
    for (;;)
    {
      updateTargetRect();
      return;
      label65:
      if (this.mRenderModeString.equals("fit"))
      {
        this.mRenderMode = 1;
      }
      else if (this.mRenderModeString.equals("fill_crop"))
      {
        this.mRenderMode = 2;
      }
      else
      {
        if (!this.mRenderModeString.equals("customize")) {
          break;
        }
        this.mRenderMode = 3;
      }
    }
    throw new RuntimeException("Unknown render mode '" + this.mRenderModeString + "'!");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/videosrc/SurfaceTextureTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */