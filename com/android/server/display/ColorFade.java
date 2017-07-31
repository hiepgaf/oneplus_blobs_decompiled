package com.android.server.display;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.display.DisplayManagerInternal.DisplayTransactionListener;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.util.Slog;
import android.view.DisplayInfo;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import com.android.server.LocalServices;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import libcore.io.Streams;

final class ColorFade
{
  private static final int COLOR_FADE_LAYER = 1073741825;
  static boolean DEBUG = false;
  private static final int DEJANK_FRAMES = 3;
  public static final int MODE_COOL_DOWN = 1;
  public static final int MODE_FADE = 2;
  public static final int MODE_WARM_UP = 0;
  private static final String TAG = "ColorFade";
  private boolean mCreatedResources;
  private int mDisplayHeight;
  private final int mDisplayId;
  private int mDisplayLayerStack;
  private final DisplayManagerInternal mDisplayManagerInternal;
  private int mDisplayWidth;
  private EGLConfig mEglConfig;
  private EGLContext mEglContext;
  private EGLDisplay mEglDisplay;
  private EGLSurface mEglSurface;
  private final int[] mGLBuffers = new int[2];
  private int mGammaLoc;
  private int mMode;
  private int mOpacityLoc;
  private boolean mPrepared;
  private int mProgram;
  private final float[] mProjMatrix = new float[16];
  private int mProjMatrixLoc;
  private int mSaturationLoc;
  private int mScaleLoc;
  private Surface mSurface;
  private float mSurfaceAlpha;
  private SurfaceControl mSurfaceControl;
  private NaturalSurfaceLayout mSurfaceLayout;
  private SurfaceSession mSurfaceSession;
  private boolean mSurfaceVisible;
  private final FloatBuffer mTexCoordBuffer = createNativeFloatBuffer(8);
  private int mTexCoordLoc;
  private final float[] mTexMatrix = new float[16];
  private int mTexMatrixLoc;
  private final int[] mTexNames = new int[1];
  private boolean mTexNamesGenerated;
  private int mTexUnitLoc;
  private final FloatBuffer mVertexBuffer = createNativeFloatBuffer(8);
  private int mVertexLoc;
  
  public ColorFade(int paramInt)
  {
    this.mDisplayId = paramInt;
    this.mDisplayManagerInternal = ((DisplayManagerInternal)LocalServices.getService(DisplayManagerInternal.class));
  }
  
  private boolean attachEglContext()
  {
    if (this.mEglSurface == null) {
      return false;
    }
    if (!EGL14.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext))
    {
      logEglError("eglMakeCurrent");
      return false;
    }
    return true;
  }
  
  /* Error */
  private boolean captureScreenshotTextureAndSetViewport()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 131	com/android/server/display/ColorFade:attachEglContext	()Z
    //   4: ifne +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: aload_0
    //   10: getfield 133	com/android/server/display/ColorFade:mTexNamesGenerated	Z
    //   13: ifne +33 -> 46
    //   16: iconst_1
    //   17: aload_0
    //   18: getfield 83	com/android/server/display/ColorFade:mTexNames	[I
    //   21: iconst_0
    //   22: invokestatic 139	android/opengl/GLES20:glGenTextures	(I[II)V
    //   25: ldc -116
    //   27: invokestatic 144	com/android/server/display/ColorFade:checkGlErrors	(Ljava/lang/String;)Z
    //   30: istore_1
    //   31: iload_1
    //   32: ifeq +9 -> 41
    //   35: aload_0
    //   36: invokespecial 147	com/android/server/display/ColorFade:detachEglContext	()V
    //   39: iconst_0
    //   40: ireturn
    //   41: aload_0
    //   42: iconst_1
    //   43: putfield 133	com/android/server/display/ColorFade:mTexNamesGenerated	Z
    //   46: new 149	android/graphics/SurfaceTexture
    //   49: dup
    //   50: aload_0
    //   51: getfield 83	com/android/server/display/ColorFade:mTexNames	[I
    //   54: iconst_0
    //   55: iaload
    //   56: invokespecial 151	android/graphics/SurfaceTexture:<init>	(I)V
    //   59: astore_2
    //   60: new 153	android/view/Surface
    //   63: dup
    //   64: aload_2
    //   65: invokespecial 156	android/view/Surface:<init>	(Landroid/graphics/SurfaceTexture;)V
    //   68: astore_3
    //   69: iconst_0
    //   70: invokestatic 162	android/view/SurfaceControl:getBuiltInDisplay	(I)Landroid/os/IBinder;
    //   73: aload_3
    //   74: invokestatic 166	android/view/SurfaceControl:screenshot	(Landroid/os/IBinder;Landroid/view/Surface;)V
    //   77: aload_2
    //   78: invokevirtual 169	android/graphics/SurfaceTexture:updateTexImage	()V
    //   81: aload_2
    //   82: aload_0
    //   83: getfield 85	com/android/server/display/ColorFade:mTexMatrix	[F
    //   86: invokevirtual 173	android/graphics/SurfaceTexture:getTransformMatrix	([F)V
    //   89: aload_3
    //   90: invokevirtual 176	android/view/Surface:release	()V
    //   93: aload_2
    //   94: invokevirtual 177	android/graphics/SurfaceTexture:release	()V
    //   97: aload_0
    //   98: getfield 97	com/android/server/display/ColorFade:mTexCoordBuffer	Ljava/nio/FloatBuffer;
    //   101: iconst_0
    //   102: fconst_0
    //   103: invokevirtual 183	java/nio/FloatBuffer:put	(IF)Ljava/nio/FloatBuffer;
    //   106: pop
    //   107: aload_0
    //   108: getfield 97	com/android/server/display/ColorFade:mTexCoordBuffer	Ljava/nio/FloatBuffer;
    //   111: iconst_1
    //   112: fconst_0
    //   113: invokevirtual 183	java/nio/FloatBuffer:put	(IF)Ljava/nio/FloatBuffer;
    //   116: pop
    //   117: aload_0
    //   118: getfield 97	com/android/server/display/ColorFade:mTexCoordBuffer	Ljava/nio/FloatBuffer;
    //   121: iconst_2
    //   122: fconst_0
    //   123: invokevirtual 183	java/nio/FloatBuffer:put	(IF)Ljava/nio/FloatBuffer;
    //   126: pop
    //   127: aload_0
    //   128: getfield 97	com/android/server/display/ColorFade:mTexCoordBuffer	Ljava/nio/FloatBuffer;
    //   131: iconst_3
    //   132: fconst_1
    //   133: invokevirtual 183	java/nio/FloatBuffer:put	(IF)Ljava/nio/FloatBuffer;
    //   136: pop
    //   137: aload_0
    //   138: getfield 97	com/android/server/display/ColorFade:mTexCoordBuffer	Ljava/nio/FloatBuffer;
    //   141: iconst_4
    //   142: fconst_1
    //   143: invokevirtual 183	java/nio/FloatBuffer:put	(IF)Ljava/nio/FloatBuffer;
    //   146: pop
    //   147: aload_0
    //   148: getfield 97	com/android/server/display/ColorFade:mTexCoordBuffer	Ljava/nio/FloatBuffer;
    //   151: iconst_5
    //   152: fconst_1
    //   153: invokevirtual 183	java/nio/FloatBuffer:put	(IF)Ljava/nio/FloatBuffer;
    //   156: pop
    //   157: aload_0
    //   158: getfield 97	com/android/server/display/ColorFade:mTexCoordBuffer	Ljava/nio/FloatBuffer;
    //   161: bipush 6
    //   163: fconst_1
    //   164: invokevirtual 183	java/nio/FloatBuffer:put	(IF)Ljava/nio/FloatBuffer;
    //   167: pop
    //   168: aload_0
    //   169: getfield 97	com/android/server/display/ColorFade:mTexCoordBuffer	Ljava/nio/FloatBuffer;
    //   172: bipush 7
    //   174: fconst_0
    //   175: invokevirtual 183	java/nio/FloatBuffer:put	(IF)Ljava/nio/FloatBuffer;
    //   178: pop
    //   179: iconst_0
    //   180: iconst_0
    //   181: aload_0
    //   182: getfield 185	com/android/server/display/ColorFade:mDisplayWidth	I
    //   185: aload_0
    //   186: getfield 187	com/android/server/display/ColorFade:mDisplayHeight	I
    //   189: invokestatic 191	android/opengl/GLES20:glViewport	(IIII)V
    //   192: aload_0
    //   193: fconst_0
    //   194: aload_0
    //   195: getfield 185	com/android/server/display/ColorFade:mDisplayWidth	I
    //   198: i2f
    //   199: fconst_0
    //   200: aload_0
    //   201: getfield 187	com/android/server/display/ColorFade:mDisplayHeight	I
    //   204: i2f
    //   205: ldc -64
    //   207: fconst_1
    //   208: invokespecial 196	com/android/server/display/ColorFade:ortho	(FFFFFF)V
    //   211: aload_0
    //   212: invokespecial 147	com/android/server/display/ColorFade:detachEglContext	()V
    //   215: iconst_1
    //   216: ireturn
    //   217: astore 4
    //   219: aload_3
    //   220: invokevirtual 176	android/view/Surface:release	()V
    //   223: aload_2
    //   224: invokevirtual 177	android/graphics/SurfaceTexture:release	()V
    //   227: aload 4
    //   229: athrow
    //   230: astore_2
    //   231: aload_0
    //   232: invokespecial 147	com/android/server/display/ColorFade:detachEglContext	()V
    //   235: aload_2
    //   236: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	237	0	this	ColorFade
    //   30	2	1	bool	boolean
    //   59	165	2	localSurfaceTexture	android.graphics.SurfaceTexture
    //   230	6	2	localObject1	Object
    //   68	152	3	localSurface	Surface
    //   217	11	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   69	89	217	finally
    //   9	31	230	finally
    //   41	46	230	finally
    //   46	69	230	finally
    //   89	211	230	finally
    //   219	230	230	finally
  }
  
  private static boolean checkGlErrors(String paramString)
  {
    return checkGlErrors(paramString, true);
  }
  
  private static boolean checkGlErrors(String paramString, boolean paramBoolean)
  {
    for (boolean bool = false;; bool = true)
    {
      int i = GLES20.glGetError();
      if (i == 0) {
        break;
      }
      if (paramBoolean) {
        Slog.e("ColorFade", paramString + " failed: error " + i, new Throwable());
      }
    }
    return bool;
  }
  
  private boolean createEglContext()
  {
    int[] arrayOfInt;
    if (this.mEglDisplay == null)
    {
      this.mEglDisplay = EGL14.eglGetDisplay(0);
      if (this.mEglDisplay == EGL14.EGL_NO_DISPLAY)
      {
        logEglError("eglGetDisplay");
        return false;
      }
      arrayOfInt = new int[2];
      if (!EGL14.eglInitialize(this.mEglDisplay, arrayOfInt, 0, arrayOfInt, 1))
      {
        this.mEglDisplay = null;
        logEglError("eglInitialize");
        return false;
      }
    }
    if (this.mEglConfig == null)
    {
      arrayOfInt = new int[1];
      EGLConfig[] arrayOfEGLConfig = new EGLConfig[1];
      EGLDisplay localEGLDisplay = this.mEglDisplay;
      int i = arrayOfEGLConfig.length;
      if (!EGL14.eglChooseConfig(localEGLDisplay, new int[] { 12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12344 }, 0, arrayOfEGLConfig, 0, i, arrayOfInt, 0))
      {
        logEglError("eglChooseConfig");
        return false;
      }
      this.mEglConfig = arrayOfEGLConfig[0];
    }
    if (this.mEglContext == null)
    {
      this.mEglContext = EGL14.eglCreateContext(this.mEglDisplay, this.mEglConfig, EGL14.EGL_NO_CONTEXT, new int[] { 12440, 2, 12344 }, 0);
      if (this.mEglContext == null)
      {
        logEglError("eglCreateContext");
        return false;
      }
    }
    return true;
  }
  
  private boolean createEglSurface()
  {
    if (this.mEglSurface == null)
    {
      this.mEglSurface = EGL14.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, this.mSurface, new int[] { 12344 }, 0);
      if (this.mEglSurface == null)
      {
        logEglError("eglCreateWindowSurface");
        return false;
      }
    }
    return true;
  }
  
  private static FloatBuffer createNativeFloatBuffer(int paramInt)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(paramInt * 4);
    localByteBuffer.order(ByteOrder.nativeOrder());
    return localByteBuffer.asFloatBuffer();
  }
  
  /* Error */
  private boolean createSurface()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 292	com/android/server/display/ColorFade:mSurfaceSession	Landroid/view/SurfaceSession;
    //   4: ifnonnull +14 -> 18
    //   7: aload_0
    //   8: new 294	android/view/SurfaceSession
    //   11: dup
    //   12: invokespecial 295	android/view/SurfaceSession:<init>	()V
    //   15: putfield 292	com/android/server/display/ColorFade:mSurfaceSession	Landroid/view/SurfaceSession;
    //   18: invokestatic 298	android/view/SurfaceControl:openTransaction	()V
    //   21: aload_0
    //   22: getfield 300	com/android/server/display/ColorFade:mSurfaceControl	Landroid/view/SurfaceControl;
    //   25: astore_2
    //   26: aload_2
    //   27: ifnonnull +120 -> 147
    //   30: aload_0
    //   31: getfield 302	com/android/server/display/ColorFade:mMode	I
    //   34: iconst_2
    //   35: if_icmpne +117 -> 152
    //   38: ldc_w 303
    //   41: istore_1
    //   42: aload_0
    //   43: new 158	android/view/SurfaceControl
    //   46: dup
    //   47: aload_0
    //   48: getfield 292	com/android/server/display/ColorFade:mSurfaceSession	Landroid/view/SurfaceSession;
    //   51: ldc 24
    //   53: aload_0
    //   54: getfield 185	com/android/server/display/ColorFade:mDisplayWidth	I
    //   57: aload_0
    //   58: getfield 187	com/android/server/display/ColorFade:mDisplayHeight	I
    //   61: iconst_m1
    //   62: iload_1
    //   63: invokespecial 306	android/view/SurfaceControl:<init>	(Landroid/view/SurfaceSession;Ljava/lang/String;IIII)V
    //   66: putfield 300	com/android/server/display/ColorFade:mSurfaceControl	Landroid/view/SurfaceControl;
    //   69: aload_0
    //   70: getfield 300	com/android/server/display/ColorFade:mSurfaceControl	Landroid/view/SurfaceControl;
    //   73: aload_0
    //   74: getfield 308	com/android/server/display/ColorFade:mDisplayLayerStack	I
    //   77: invokevirtual 311	android/view/SurfaceControl:setLayerStack	(I)V
    //   80: aload_0
    //   81: getfield 300	com/android/server/display/ColorFade:mSurfaceControl	Landroid/view/SurfaceControl;
    //   84: aload_0
    //   85: getfield 185	com/android/server/display/ColorFade:mDisplayWidth	I
    //   88: aload_0
    //   89: getfield 187	com/android/server/display/ColorFade:mDisplayHeight	I
    //   92: invokevirtual 315	android/view/SurfaceControl:setSize	(II)V
    //   95: aload_0
    //   96: new 153	android/view/Surface
    //   99: dup
    //   100: invokespecial 316	android/view/Surface:<init>	()V
    //   103: putfield 262	com/android/server/display/ColorFade:mSurface	Landroid/view/Surface;
    //   106: aload_0
    //   107: getfield 262	com/android/server/display/ColorFade:mSurface	Landroid/view/Surface;
    //   110: aload_0
    //   111: getfield 300	com/android/server/display/ColorFade:mSurfaceControl	Landroid/view/SurfaceControl;
    //   114: invokevirtual 320	android/view/Surface:copyFrom	(Landroid/view/SurfaceControl;)V
    //   117: aload_0
    //   118: new 6	com/android/server/display/ColorFade$NaturalSurfaceLayout
    //   121: dup
    //   122: aload_0
    //   123: getfield 109	com/android/server/display/ColorFade:mDisplayManagerInternal	Landroid/hardware/display/DisplayManagerInternal;
    //   126: aload_0
    //   127: getfield 99	com/android/server/display/ColorFade:mDisplayId	I
    //   130: aload_0
    //   131: getfield 300	com/android/server/display/ColorFade:mSurfaceControl	Landroid/view/SurfaceControl;
    //   134: invokespecial 323	com/android/server/display/ColorFade$NaturalSurfaceLayout:<init>	(Landroid/hardware/display/DisplayManagerInternal;ILandroid/view/SurfaceControl;)V
    //   137: putfield 325	com/android/server/display/ColorFade:mSurfaceLayout	Lcom/android/server/display/ColorFade$NaturalSurfaceLayout;
    //   140: aload_0
    //   141: getfield 325	com/android/server/display/ColorFade:mSurfaceLayout	Lcom/android/server/display/ColorFade$NaturalSurfaceLayout;
    //   144: invokevirtual 328	com/android/server/display/ColorFade$NaturalSurfaceLayout:onDisplayTransaction	()V
    //   147: invokestatic 331	android/view/SurfaceControl:closeTransaction	()V
    //   150: iconst_1
    //   151: ireturn
    //   152: sipush 1028
    //   155: istore_1
    //   156: goto -114 -> 42
    //   159: astore_2
    //   160: ldc 24
    //   162: ldc_w 333
    //   165: aload_2
    //   166: invokestatic 228	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   169: pop
    //   170: invokestatic 331	android/view/SurfaceControl:closeTransaction	()V
    //   173: iconst_0
    //   174: ireturn
    //   175: astore_2
    //   176: invokestatic 331	android/view/SurfaceControl:closeTransaction	()V
    //   179: aload_2
    //   180: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	181	0	this	ColorFade
    //   41	115	1	i	int
    //   25	2	2	localSurfaceControl	SurfaceControl
    //   159	7	2	localOutOfResourcesException	android.view.Surface.OutOfResourcesException
    //   175	5	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   30	38	159	android/view/Surface$OutOfResourcesException
    //   42	69	159	android/view/Surface$OutOfResourcesException
    //   21	26	175	finally
    //   30	38	175	finally
    //   42	69	175	finally
    //   69	147	175	finally
    //   160	170	175	finally
  }
  
  private void destroyEglSurface()
  {
    if (this.mEglSurface != null)
    {
      if (!EGL14.eglDestroySurface(this.mEglDisplay, this.mEglSurface)) {
        logEglError("eglDestroySurface");
      }
      this.mEglSurface = null;
    }
  }
  
  private void destroyGLBuffers()
  {
    GLES20.glDeleteBuffers(2, this.mGLBuffers, 0);
    checkGlErrors("glDeleteBuffers");
  }
  
  private void destroyGLShaders()
  {
    GLES20.glDeleteProgram(this.mProgram);
    checkGlErrors("glDeleteProgram");
  }
  
  private void destroyScreenshotTexture()
  {
    if (this.mTexNamesGenerated)
    {
      this.mTexNamesGenerated = false;
      GLES20.glDeleteTextures(1, this.mTexNames, 0);
      checkGlErrors("glDeleteTextures");
    }
  }
  
  private void destroySurface()
  {
    if (this.mSurfaceControl != null)
    {
      this.mSurfaceLayout.dispose();
      this.mSurfaceLayout = null;
      SurfaceControl.openTransaction();
    }
    try
    {
      this.mSurfaceControl.destroy();
      this.mSurface.release();
      SurfaceControl.closeTransaction();
      this.mSurfaceControl = null;
      this.mSurfaceVisible = false;
      this.mSurfaceAlpha = 0.0F;
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
    }
  }
  
  private void detachEglContext()
  {
    if (this.mEglDisplay != null) {
      EGL14.eglMakeCurrent(this.mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
    }
  }
  
  private void drawFaded(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (DEBUG) {
      Slog.d("ColorFade", "drawFaded: opacity=" + paramFloat1 + ", gamma=" + paramFloat2 + ", saturation=" + paramFloat3 + ", scale=" + paramFloat4);
    }
    GLES20.glUseProgram(this.mProgram);
    GLES20.glUniformMatrix4fv(this.mProjMatrixLoc, 1, false, this.mProjMatrix, 0);
    GLES20.glUniformMatrix4fv(this.mTexMatrixLoc, 1, false, this.mTexMatrix, 0);
    GLES20.glUniform1f(this.mOpacityLoc, paramFloat1);
    GLES20.glUniform1f(this.mGammaLoc, paramFloat2);
    GLES20.glUniform1f(this.mSaturationLoc, paramFloat3);
    GLES20.glUniform1f(this.mScaleLoc, paramFloat4);
    GLES20.glActiveTexture(33984);
    GLES20.glBindTexture(36197, this.mTexNames[0]);
    GLES20.glBindBuffer(34962, this.mGLBuffers[0]);
    GLES20.glEnableVertexAttribArray(this.mVertexLoc);
    GLES20.glVertexAttribPointer(this.mVertexLoc, 2, 5126, false, 0, 0);
    GLES20.glBindBuffer(34962, this.mGLBuffers[1]);
    GLES20.glEnableVertexAttribArray(this.mTexCoordLoc);
    GLES20.glVertexAttribPointer(this.mTexCoordLoc, 2, 5126, false, 0, 0);
    GLES20.glDrawArrays(6, 0, 4);
    GLES20.glBindTexture(36197, 0);
    GLES20.glBindBuffer(34962, 0);
  }
  
  private boolean initGLBuffers()
  {
    setQuad(this.mVertexBuffer, 0.0F, 0.0F, this.mDisplayWidth, this.mDisplayHeight);
    GLES20.glBindTexture(36197, this.mTexNames[0]);
    GLES20.glTexParameteri(36197, 10240, 9728);
    GLES20.glTexParameteri(36197, 10241, 9728);
    GLES20.glTexParameteri(36197, 10242, 33071);
    GLES20.glTexParameteri(36197, 10243, 33071);
    GLES20.glBindTexture(36197, 0);
    GLES20.glGenBuffers(2, this.mGLBuffers, 0);
    GLES20.glBindBuffer(34962, this.mGLBuffers[0]);
    GLES20.glBufferData(34962, this.mVertexBuffer.capacity() * 4, this.mVertexBuffer, 35044);
    GLES20.glBindBuffer(34962, this.mGLBuffers[1]);
    GLES20.glBufferData(34962, this.mTexCoordBuffer.capacity() * 4, this.mTexCoordBuffer, 35044);
    GLES20.glBindBuffer(34962, 0);
    return true;
  }
  
  private boolean initGLShaders(Context paramContext)
  {
    int i = loadShader(paramContext, 17825796, 35633);
    int j = loadShader(paramContext, 17825795, 35632);
    GLES20.glReleaseShaderCompiler();
    if ((i == 0) || (j == 0)) {
      return false;
    }
    this.mProgram = GLES20.glCreateProgram();
    GLES20.glAttachShader(this.mProgram, i);
    GLES20.glAttachShader(this.mProgram, j);
    GLES20.glDeleteShader(i);
    GLES20.glDeleteShader(j);
    GLES20.glLinkProgram(this.mProgram);
    this.mVertexLoc = GLES20.glGetAttribLocation(this.mProgram, "position");
    this.mTexCoordLoc = GLES20.glGetAttribLocation(this.mProgram, "uv");
    this.mProjMatrixLoc = GLES20.glGetUniformLocation(this.mProgram, "proj_matrix");
    this.mTexMatrixLoc = GLES20.glGetUniformLocation(this.mProgram, "tex_matrix");
    this.mOpacityLoc = GLES20.glGetUniformLocation(this.mProgram, "opacity");
    this.mGammaLoc = GLES20.glGetUniformLocation(this.mProgram, "gamma");
    this.mSaturationLoc = GLES20.glGetUniformLocation(this.mProgram, "saturation");
    this.mScaleLoc = GLES20.glGetUniformLocation(this.mProgram, "scale");
    this.mTexUnitLoc = GLES20.glGetUniformLocation(this.mProgram, "texUnit");
    GLES20.glUseProgram(this.mProgram);
    GLES20.glUniform1i(this.mTexUnitLoc, 0);
    GLES20.glUseProgram(0);
    return true;
  }
  
  private int loadShader(Context paramContext, int paramInt1, int paramInt2)
  {
    paramContext = readFile(paramContext, paramInt1);
    int i = GLES20.glCreateShader(paramInt2);
    GLES20.glShaderSource(i, paramContext);
    GLES20.glCompileShader(i);
    paramContext = new int[1];
    GLES20.glGetShaderiv(i, 35713, paramContext, 0);
    paramInt1 = i;
    if (paramContext[0] == 0)
    {
      Slog.e("ColorFade", "Could not compile shader " + i + ", " + paramInt2 + ":");
      Slog.e("ColorFade", GLES20.glGetShaderSource(i));
      Slog.e("ColorFade", GLES20.glGetShaderInfoLog(i));
      GLES20.glDeleteShader(i);
      paramInt1 = 0;
    }
    return paramInt1;
  }
  
  private static void logEglError(String paramString)
  {
    Slog.e("ColorFade", paramString + " failed: error " + EGL14.eglGetError(), new Throwable());
  }
  
  private void ortho(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    this.mProjMatrix[0] = (2.0F / (paramFloat2 - paramFloat1));
    this.mProjMatrix[1] = 0.0F;
    this.mProjMatrix[2] = 0.0F;
    this.mProjMatrix[3] = 0.0F;
    this.mProjMatrix[4] = 0.0F;
    this.mProjMatrix[5] = (2.0F / (paramFloat4 - paramFloat3));
    this.mProjMatrix[6] = 0.0F;
    this.mProjMatrix[7] = 0.0F;
    this.mProjMatrix[8] = 0.0F;
    this.mProjMatrix[9] = 0.0F;
    this.mProjMatrix[10] = (-2.0F / (paramFloat6 - paramFloat5));
    this.mProjMatrix[11] = 0.0F;
    this.mProjMatrix[12] = (-(paramFloat2 + paramFloat1) / (paramFloat2 - paramFloat1));
    this.mProjMatrix[13] = (-(paramFloat4 + paramFloat3) / (paramFloat4 - paramFloat3));
    this.mProjMatrix[14] = (-(paramFloat6 + paramFloat5) / (paramFloat6 - paramFloat5));
    this.mProjMatrix[15] = 1.0F;
  }
  
  private String readFile(Context paramContext, int paramInt)
  {
    try
    {
      paramContext = new String(Streams.readFully(new InputStreamReader(paramContext.getResources().openRawResource(paramInt))));
      return paramContext;
    }
    catch (IOException paramContext)
    {
      Slog.e("ColorFade", "Unrecognized shader " + Integer.toString(paramInt));
      throw new RuntimeException(paramContext);
    }
  }
  
  private static void setQuad(FloatBuffer paramFloatBuffer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (DEBUG) {
      Slog.d("ColorFade", "setQuad: x=" + paramFloat1 + ", y=" + paramFloat2 + ", w=" + paramFloat3 + ", h=" + paramFloat4);
    }
    paramFloatBuffer.put(0, paramFloat1);
    paramFloatBuffer.put(1, paramFloat2);
    paramFloatBuffer.put(2, paramFloat1);
    paramFloatBuffer.put(3, paramFloat2 + paramFloat4);
    paramFloatBuffer.put(4, paramFloat1 + paramFloat3);
    paramFloatBuffer.put(5, paramFloat2 + paramFloat4);
    paramFloatBuffer.put(6, paramFloat1 + paramFloat3);
    paramFloatBuffer.put(7, paramFloat2);
  }
  
  private boolean showSurface(float paramFloat)
  {
    if ((!this.mSurfaceVisible) || (this.mSurfaceAlpha != paramFloat)) {
      SurfaceControl.openTransaction();
    }
    try
    {
      this.mSurfaceControl.setLayer(1073741825);
      this.mSurfaceControl.setAlpha(paramFloat);
      this.mSurfaceControl.show();
      SurfaceControl.closeTransaction();
      this.mSurfaceVisible = true;
      this.mSurfaceAlpha = paramFloat;
      return true;
    }
    finally
    {
      SurfaceControl.closeTransaction();
    }
  }
  
  public void dismiss()
  {
    if (DEBUG) {
      Slog.d("ColorFade", "dismiss");
    }
    if (this.mPrepared)
    {
      dismissResources();
      destroySurface();
      this.mPrepared = false;
    }
  }
  
  public void dismissResources()
  {
    if (DEBUG) {
      Slog.d("ColorFade", "dismissResources");
    }
    if (this.mCreatedResources) {
      attachEglContext();
    }
    try
    {
      destroyScreenshotTexture();
      destroyGLShaders();
      destroyGLBuffers();
      destroyEglSurface();
      detachEglContext();
      GLES20.glFlush();
      this.mCreatedResources = false;
      return;
    }
    finally
    {
      detachEglContext();
    }
  }
  
  public boolean draw(float paramFloat)
  {
    if (DEBUG) {
      Slog.d("ColorFade", "drawFrame: level=" + paramFloat);
    }
    if (!this.mPrepared) {
      return false;
    }
    if (this.mMode == 2) {
      return showSurface(1.0F - paramFloat);
    }
    if (!attachEglContext()) {
      return false;
    }
    try
    {
      GLES20.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      GLES20.glClear(16384);
      double d1 = 1.0F - paramFloat;
      double d2 = Math.cos(3.141592653589793D * d1);
      if (d2 < 0.0D) {}
      for (int i = -1;; i = 1)
      {
        double d3 = i;
        float f1 = (float)-Math.pow(d1, 2.0D);
        paramFloat = (float)Math.pow(paramFloat, 4.0D);
        float f2 = (float)((-Math.pow(d1, 2.0D) + 1.0D) * 0.1D + 0.9D);
        drawFaded(f1 + 1.0F, 1.0F / (float)((0.5D * d3 * Math.pow(d2, 2.0D) + 0.5D) * 0.9D + 0.1D), paramFloat, f2);
        boolean bool = checkGlErrors("drawFrame");
        if (!bool) {
          break;
        }
        return false;
      }
      EGL14.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
      return showSurface(1.0F);
    }
    finally
    {
      detachEglContext();
    }
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println();
    paramPrintWriter.println("Color Fade State:");
    paramPrintWriter.println("  mPrepared=" + this.mPrepared);
    paramPrintWriter.println("  mMode=" + this.mMode);
    paramPrintWriter.println("  mDisplayLayerStack=" + this.mDisplayLayerStack);
    paramPrintWriter.println("  mDisplayWidth=" + this.mDisplayWidth);
    paramPrintWriter.println("  mDisplayHeight=" + this.mDisplayHeight);
    paramPrintWriter.println("  mSurfaceVisible=" + this.mSurfaceVisible);
    paramPrintWriter.println("  mSurfaceAlpha=" + this.mSurfaceAlpha);
  }
  
  public boolean prepare(Context paramContext, int paramInt)
  {
    if (DEBUG) {
      Slog.d("ColorFade", "prepare: mode=" + paramInt);
    }
    this.mMode = paramInt;
    DisplayInfo localDisplayInfo = this.mDisplayManagerInternal.getDisplayInfo(this.mDisplayId);
    this.mDisplayLayerStack = localDisplayInfo.layerStack;
    this.mDisplayWidth = localDisplayInfo.getNaturalWidth();
    this.mDisplayHeight = localDisplayInfo.getNaturalHeight();
    if ((createSurface()) && (createEglContext()) && (createEglSurface())) {}
    for (boolean bool = captureScreenshotTextureAndSetViewport(); !bool; bool = false)
    {
      dismiss();
      return false;
    }
    if (!attachEglContext()) {
      return false;
    }
    try
    {
      if ((!initGLShaders(paramContext)) || (!initGLBuffers()) || (checkGlErrors("prepare")))
      {
        detachEglContext();
        dismiss();
        return false;
      }
      detachEglContext();
      this.mCreatedResources = true;
      this.mPrepared = true;
      if (paramInt == 1)
      {
        paramInt = 0;
        while (paramInt < 3)
        {
          draw(1.0F);
          paramInt += 1;
        }
      }
      return true;
    }
    finally
    {
      detachEglContext();
    }
  }
  
  private static final class NaturalSurfaceLayout
    implements DisplayManagerInternal.DisplayTransactionListener
  {
    private final int mDisplayId;
    private final DisplayManagerInternal mDisplayManagerInternal;
    private SurfaceControl mSurfaceControl;
    
    public NaturalSurfaceLayout(DisplayManagerInternal paramDisplayManagerInternal, int paramInt, SurfaceControl paramSurfaceControl)
    {
      this.mDisplayManagerInternal = paramDisplayManagerInternal;
      this.mDisplayId = paramInt;
      this.mSurfaceControl = paramSurfaceControl;
      this.mDisplayManagerInternal.registerDisplayTransactionListener(this);
    }
    
    public void dispose()
    {
      try
      {
        this.mSurfaceControl = null;
        this.mDisplayManagerInternal.unregisterDisplayTransactionListener(this);
        return;
      }
      finally {}
    }
    
    public void onDisplayTransaction()
    {
      for (;;)
      {
        try
        {
          Object localObject1 = this.mSurfaceControl;
          if (localObject1 == null) {
            return;
          }
          localObject1 = this.mDisplayManagerInternal.getDisplayInfo(this.mDisplayId);
          int i = ((DisplayInfo)localObject1).rotation;
          switch (i)
          {
          default: 
            return;
          }
        }
        finally {}
        this.mSurfaceControl.setPosition(0.0F, 0.0F);
        this.mSurfaceControl.setMatrix(1.0F, 0.0F, 0.0F, 1.0F);
        continue;
        this.mSurfaceControl.setPosition(0.0F, ((DisplayInfo)localObject2).logicalHeight);
        this.mSurfaceControl.setMatrix(0.0F, -1.0F, 1.0F, 0.0F);
        continue;
        this.mSurfaceControl.setPosition(((DisplayInfo)localObject2).logicalWidth, ((DisplayInfo)localObject2).logicalHeight);
        this.mSurfaceControl.setMatrix(-1.0F, 0.0F, 0.0F, -1.0F);
        continue;
        this.mSurfaceControl.setPosition(((DisplayInfo)localObject2).logicalWidth, 0.0F);
        this.mSurfaceControl.setMatrix(0.0F, 1.0F, -1.0F, 0.0F);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/ColorFade.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */