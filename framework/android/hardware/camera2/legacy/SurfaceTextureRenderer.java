package android.hardware.camera2.legacy;

import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Environment;
import android.os.SystemProperties;
import android.text.format.Time;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Surface;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SurfaceTextureRenderer
{
  private static final boolean DEBUG = false;
  private static final int EGL_COLOR_BITLENGTH = 8;
  private static final int EGL_RECORDABLE_ANDROID = 12610;
  private static final int FLIP_TYPE_BOTH = 3;
  private static final int FLIP_TYPE_HORIZONTAL = 1;
  private static final int FLIP_TYPE_NONE = 0;
  private static final int FLIP_TYPE_VERTICAL = 2;
  private static final int FLOAT_SIZE_BYTES = 4;
  private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
  private static final int GLES_VERSION = 2;
  private static final int GL_MATRIX_SIZE = 16;
  private static final String LEGACY_PERF_PROPERTY = "persist.camera.legacy_perf";
  private static final int PBUFFER_PIXEL_BYTES = 4;
  private static final String TAG = SurfaceTextureRenderer.class.getSimpleName();
  private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
  private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 20;
  private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
  private static final int VERTEX_POS_SIZE = 3;
  private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
  private static final int VERTEX_UV_SIZE = 2;
  private static final float[] sBothFlipTriangleVertices = { -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, -1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F };
  private static final float[] sHorizontalFlipTriangleVertices = { -1.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F };
  private static final float[] sRegularTriangleVertices = { -1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F };
  private static final float[] sVerticalFlipTriangleVertices = { -1.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, -1.0F, 0.0F, 1.0F, 1.0F, -1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F };
  private FloatBuffer mBothFlipTriangleVertices;
  private EGLConfig mConfigs;
  private List<EGLSurfaceHolder> mConversionSurfaces = new ArrayList();
  private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
  private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
  private final int mFacing;
  private FloatBuffer mHorizontalFlipTriangleVertices;
  private float[] mMVPMatrix = new float[16];
  private ByteBuffer mPBufferPixels;
  private PerfMeasurement mPerfMeasurer = null;
  private int mProgram;
  private FloatBuffer mRegularTriangleVertices;
  private float[] mSTMatrix = new float[16];
  private volatile SurfaceTexture mSurfaceTexture;
  private List<EGLSurfaceHolder> mSurfaces = new ArrayList();
  private int mTextureID = 0;
  private FloatBuffer mVerticalFlipTriangleVertices;
  private int maPositionHandle;
  private int maTextureHandle;
  private int muMVPMatrixHandle;
  private int muSTMatrixHandle;
  
  public SurfaceTextureRenderer(int paramInt)
  {
    this.mFacing = paramInt;
    this.mRegularTriangleVertices = ByteBuffer.allocateDirect(sRegularTriangleVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.mRegularTriangleVertices.put(sRegularTriangleVertices).position(0);
    this.mHorizontalFlipTriangleVertices = ByteBuffer.allocateDirect(sHorizontalFlipTriangleVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.mHorizontalFlipTriangleVertices.put(sHorizontalFlipTriangleVertices).position(0);
    this.mVerticalFlipTriangleVertices = ByteBuffer.allocateDirect(sVerticalFlipTriangleVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.mVerticalFlipTriangleVertices.put(sVerticalFlipTriangleVertices).position(0);
    this.mBothFlipTriangleVertices = ByteBuffer.allocateDirect(sBothFlipTriangleVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.mBothFlipTriangleVertices.put(sBothFlipTriangleVertices).position(0);
    android.opengl.Matrix.setIdentityM(this.mSTMatrix, 0);
  }
  
  private void addGlTimestamp(long paramLong)
  {
    if (this.mPerfMeasurer == null) {
      return;
    }
    this.mPerfMeasurer.addTimestamp(paramLong);
  }
  
  private void beginGlTiming()
  {
    if (this.mPerfMeasurer == null) {
      return;
    }
    this.mPerfMeasurer.startTimer();
  }
  
  private void checkEglError(String paramString)
  {
    int i = EGL14.eglGetError();
    if (i != 12288) {
      throw new IllegalStateException(paramString + ": EGL error: 0x" + Integer.toHexString(i));
    }
  }
  
  private void checkGlError(String paramString)
  {
    int i = GLES20.glGetError();
    if (i != 0) {
      throw new IllegalStateException(paramString + ": GLES20 error: 0x" + Integer.toHexString(i));
    }
  }
  
  private void clearState()
  {
    this.mSurfaces.clear();
    Iterator localIterator = this.mConversionSurfaces.iterator();
    while (localIterator.hasNext())
    {
      EGLSurfaceHolder localEGLSurfaceHolder = (EGLSurfaceHolder)localIterator.next();
      try
      {
        LegacyCameraDevice.disconnectSurface(localEGLSurfaceHolder.surface);
      }
      catch (LegacyExceptionUtils.BufferQueueAbandonedException localBufferQueueAbandonedException)
      {
        Log.w(TAG, "Surface abandoned, skipping...", localBufferQueueAbandonedException);
      }
    }
    this.mConversionSurfaces.clear();
    this.mPBufferPixels = null;
    if (this.mSurfaceTexture != null) {
      this.mSurfaceTexture.release();
    }
    this.mSurfaceTexture = null;
  }
  
  private void configureEGLContext()
  {
    this.mEGLDisplay = EGL14.eglGetDisplay(0);
    if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
      throw new IllegalStateException("No EGL14 display");
    }
    Object localObject = new int[2];
    if (!EGL14.eglInitialize(this.mEGLDisplay, (int[])localObject, 0, (int[])localObject, 1)) {
      throw new IllegalStateException("Cannot initialize EGL14");
    }
    localObject = new EGLConfig[1];
    int[] arrayOfInt = new int[1];
    EGLDisplay localEGLDisplay = this.mEGLDisplay;
    int i = localObject.length;
    EGL14.eglChooseConfig(localEGLDisplay, new int[] { 12324, 8, 12323, 8, 12322, 8, 12352, 4, 12610, 1, 12339, 5, 12344 }, 0, (EGLConfig[])localObject, 0, i, arrayOfInt, 0);
    checkEglError("eglCreateContext RGB888+recordable ES2");
    this.mConfigs = localObject[0];
    this.mEGLContext = EGL14.eglCreateContext(this.mEGLDisplay, localObject[0], EGL14.EGL_NO_CONTEXT, new int[] { 12440, 2, 12344 }, 0);
    checkEglError("eglCreateContext");
    if (this.mEGLContext == EGL14.EGL_NO_CONTEXT) {
      throw new IllegalStateException("No EGLContext could be made");
    }
  }
  
  private void configureEGLOutputSurfaces(Collection<EGLSurfaceHolder> paramCollection)
  {
    if ((paramCollection == null) || (paramCollection.size() == 0)) {
      throw new IllegalStateException("No Surfaces were provided to draw to");
    }
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      EGLSurfaceHolder localEGLSurfaceHolder = (EGLSurfaceHolder)paramCollection.next();
      localEGLSurfaceHolder.eglSurface = EGL14.eglCreateWindowSurface(this.mEGLDisplay, this.mConfigs, localEGLSurfaceHolder.surface, new int[] { 12344 }, 0);
      checkEglError("eglCreateWindowSurface");
    }
  }
  
  private void configureEGLPbufferSurfaces(Collection<EGLSurfaceHolder> paramCollection)
  {
    if ((paramCollection == null) || (paramCollection.size() == 0)) {
      throw new IllegalStateException("No Surfaces were provided to draw to");
    }
    int j = 0;
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      EGLSurfaceHolder localEGLSurfaceHolder = (EGLSurfaceHolder)paramCollection.next();
      int k = localEGLSurfaceHolder.width * localEGLSurfaceHolder.height;
      int i = j;
      if (k > j) {
        i = k;
      }
      j = localEGLSurfaceHolder.width;
      k = localEGLSurfaceHolder.height;
      localEGLSurfaceHolder.eglSurface = EGL14.eglCreatePbufferSurface(this.mEGLDisplay, this.mConfigs, new int[] { 12375, j, 12374, k, 12344 }, 0);
      checkEglError("eglCreatePbufferSurface");
      j = i;
    }
    this.mPBufferPixels = ByteBuffer.allocateDirect(j * 4).order(ByteOrder.nativeOrder());
  }
  
  private int createProgram(String paramString1, String paramString2)
  {
    int i = loadShader(35633, paramString1);
    if (i == 0) {
      return 0;
    }
    int j = loadShader(35632, paramString2);
    if (j == 0) {
      return 0;
    }
    int k = GLES20.glCreateProgram();
    checkGlError("glCreateProgram");
    if (k == 0) {
      Log.e(TAG, "Could not create program");
    }
    GLES20.glAttachShader(k, i);
    checkGlError("glAttachShader");
    GLES20.glAttachShader(k, j);
    checkGlError("glAttachShader");
    GLES20.glLinkProgram(k);
    paramString1 = new int[1];
    GLES20.glGetProgramiv(k, 35714, paramString1, 0);
    if (paramString1[0] != 1)
    {
      Log.e(TAG, "Could not link program: ");
      Log.e(TAG, GLES20.glGetProgramInfoLog(k));
      GLES20.glDeleteProgram(k);
      throw new IllegalStateException("Could not link program");
    }
    return k;
  }
  
  private void drawFrame(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2, int paramInt3)
  {
    checkGlError("onDrawFrame start");
    paramSurfaceTexture.getTransformMatrix(this.mSTMatrix);
    android.opengl.Matrix.setIdentityM(this.mMVPMatrix, 0);
    try
    {
      paramSurfaceTexture = LegacyCameraDevice.getTextureSize(paramSurfaceTexture);
      f1 = paramSurfaceTexture.getWidth();
      f2 = paramSurfaceTexture.getHeight();
      if ((f1 <= 0.0F) || (f2 <= 0.0F)) {
        throw new IllegalStateException("Illegal intermediate texture with dimension of 0");
      }
    }
    catch (LegacyExceptionUtils.BufferQueueAbandonedException paramSurfaceTexture)
    {
      throw new IllegalStateException("Surface abandoned, skipping drawFrame...", paramSurfaceTexture);
    }
    paramSurfaceTexture = new RectF(0.0F, 0.0F, f1, f2);
    RectF localRectF = new RectF(0.0F, 0.0F, paramInt1, paramInt2);
    android.graphics.Matrix localMatrix = new android.graphics.Matrix();
    localMatrix.setRectToRect(localRectF, paramSurfaceTexture, Matrix.ScaleToFit.CENTER);
    localMatrix.mapRect(localRectF);
    float f1 = paramSurfaceTexture.width() / localRectF.width();
    float f2 = paramSurfaceTexture.height() / localRectF.height();
    android.opengl.Matrix.scaleM(this.mMVPMatrix, 0, f1, f2, 1.0F);
    GLES20.glViewport(0, 0, paramInt1, paramInt2);
    GLES20.glUseProgram(this.mProgram);
    checkGlError("glUseProgram");
    GLES20.glActiveTexture(33984);
    GLES20.glBindTexture(36197, this.mTextureID);
    switch (paramInt3)
    {
    default: 
      paramSurfaceTexture = this.mRegularTriangleVertices;
    }
    for (;;)
    {
      paramSurfaceTexture.position(0);
      GLES20.glVertexAttribPointer(this.maPositionHandle, 3, 5126, false, 20, paramSurfaceTexture);
      checkGlError("glVertexAttribPointer maPosition");
      GLES20.glEnableVertexAttribArray(this.maPositionHandle);
      checkGlError("glEnableVertexAttribArray maPositionHandle");
      paramSurfaceTexture.position(3);
      GLES20.glVertexAttribPointer(this.maTextureHandle, 2, 5126, false, 20, paramSurfaceTexture);
      checkGlError("glVertexAttribPointer maTextureHandle");
      GLES20.glEnableVertexAttribArray(this.maTextureHandle);
      checkGlError("glEnableVertexAttribArray maTextureHandle");
      GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle, 1, false, this.mMVPMatrix, 0);
      GLES20.glUniformMatrix4fv(this.muSTMatrixHandle, 1, false, this.mSTMatrix, 0);
      GLES20.glDrawArrays(5, 0, 4);
      checkGlError("glDrawArrays");
      return;
      paramSurfaceTexture = this.mHorizontalFlipTriangleVertices;
      continue;
      paramSurfaceTexture = this.mVerticalFlipTriangleVertices;
      continue;
      paramSurfaceTexture = this.mBothFlipTriangleVertices;
    }
  }
  
  private void dumpGlTiming()
  {
    if (this.mPerfMeasurer == null) {
      return;
    }
    Object localObject1 = new File(Environment.getExternalStorageDirectory(), "CameraLegacy");
    if ((!((File)localObject1).exists()) && (!((File)localObject1).mkdirs()))
    {
      Log.e(TAG, "Failed to create directory for data dump");
      return;
    }
    localObject1 = new StringBuilder(((File)localObject1).getPath());
    ((StringBuilder)localObject1).append(File.separator);
    ((StringBuilder)localObject1).append("durations_");
    Object localObject2 = new Time();
    ((Time)localObject2).setToNow();
    ((StringBuilder)localObject1).append(((Time)localObject2).format2445());
    ((StringBuilder)localObject1).append("_S");
    localObject2 = this.mSurfaces.iterator();
    EGLSurfaceHolder localEGLSurfaceHolder;
    while (((Iterator)localObject2).hasNext())
    {
      localEGLSurfaceHolder = (EGLSurfaceHolder)((Iterator)localObject2).next();
      ((StringBuilder)localObject1).append(String.format("_%d_%d", new Object[] { Integer.valueOf(localEGLSurfaceHolder.width), Integer.valueOf(localEGLSurfaceHolder.height) }));
    }
    ((StringBuilder)localObject1).append("_C");
    localObject2 = this.mConversionSurfaces.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localEGLSurfaceHolder = (EGLSurfaceHolder)((Iterator)localObject2).next();
      ((StringBuilder)localObject1).append(String.format("_%d_%d", new Object[] { Integer.valueOf(localEGLSurfaceHolder.width), Integer.valueOf(localEGLSurfaceHolder.height) }));
    }
    ((StringBuilder)localObject1).append(".txt");
    this.mPerfMeasurer.dumpPerformanceData(((StringBuilder)localObject1).toString());
  }
  
  private void endGlTiming()
  {
    if (this.mPerfMeasurer == null) {
      return;
    }
    this.mPerfMeasurer.stopTimer();
  }
  
  private int getTextureId()
  {
    return this.mTextureID;
  }
  
  private void initializeGLState()
  {
    this.mProgram = createProgram("uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n", "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
    if (this.mProgram == 0) {
      throw new IllegalStateException("failed creating program");
    }
    this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
    checkGlError("glGetAttribLocation aPosition");
    if (this.maPositionHandle == -1) {
      throw new IllegalStateException("Could not get attrib location for aPosition");
    }
    this.maTextureHandle = GLES20.glGetAttribLocation(this.mProgram, "aTextureCoord");
    checkGlError("glGetAttribLocation aTextureCoord");
    if (this.maTextureHandle == -1) {
      throw new IllegalStateException("Could not get attrib location for aTextureCoord");
    }
    this.muMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uMVPMatrix");
    checkGlError("glGetUniformLocation uMVPMatrix");
    if (this.muMVPMatrixHandle == -1) {
      throw new IllegalStateException("Could not get attrib location for uMVPMatrix");
    }
    this.muSTMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uSTMatrix");
    checkGlError("glGetUniformLocation uSTMatrix");
    if (this.muSTMatrixHandle == -1) {
      throw new IllegalStateException("Could not get attrib location for uSTMatrix");
    }
    int[] arrayOfInt = new int[1];
    GLES20.glGenTextures(1, arrayOfInt, 0);
    this.mTextureID = arrayOfInt[0];
    GLES20.glBindTexture(36197, this.mTextureID);
    checkGlError("glBindTexture mTextureID");
    GLES20.glTexParameterf(36197, 10241, 9728.0F);
    GLES20.glTexParameterf(36197, 10240, 9729.0F);
    GLES20.glTexParameteri(36197, 10242, 33071);
    GLES20.glTexParameteri(36197, 10243, 33071);
    checkGlError("glTexParameter");
  }
  
  private int loadShader(int paramInt, String paramString)
  {
    int i = GLES20.glCreateShader(paramInt);
    checkGlError("glCreateShader type=" + paramInt);
    GLES20.glShaderSource(i, paramString);
    GLES20.glCompileShader(i);
    paramString = new int[1];
    GLES20.glGetShaderiv(i, 35713, paramString, 0);
    if (paramString[0] == 0)
    {
      Log.e(TAG, "Could not compile shader " + paramInt + ":");
      Log.e(TAG, " " + GLES20.glGetShaderInfoLog(i));
      GLES20.glDeleteShader(i);
      throw new IllegalStateException("Could not compile shader " + paramInt);
    }
    return i;
  }
  
  private void makeCurrent(EGLSurface paramEGLSurface)
  {
    EGL14.eglMakeCurrent(this.mEGLDisplay, paramEGLSurface, paramEGLSurface, this.mEGLContext);
    checkEglError("makeCurrent");
  }
  
  private void releaseEGLContext()
  {
    if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY)
    {
      EGL14.eglMakeCurrent(this.mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
      dumpGlTiming();
      Iterator localIterator;
      EGLSurfaceHolder localEGLSurfaceHolder;
      if (this.mSurfaces != null)
      {
        localIterator = this.mSurfaces.iterator();
        while (localIterator.hasNext())
        {
          localEGLSurfaceHolder = (EGLSurfaceHolder)localIterator.next();
          if (localEGLSurfaceHolder.eglSurface != null) {
            EGL14.eglDestroySurface(this.mEGLDisplay, localEGLSurfaceHolder.eglSurface);
          }
        }
      }
      if (this.mConversionSurfaces != null)
      {
        localIterator = this.mConversionSurfaces.iterator();
        while (localIterator.hasNext())
        {
          localEGLSurfaceHolder = (EGLSurfaceHolder)localIterator.next();
          if (localEGLSurfaceHolder.eglSurface != null) {
            EGL14.eglDestroySurface(this.mEGLDisplay, localEGLSurfaceHolder.eglSurface);
          }
        }
      }
      EGL14.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
      EGL14.eglReleaseThread();
      EGL14.eglTerminate(this.mEGLDisplay);
    }
    this.mConfigs = null;
    this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    this.mEGLContext = EGL14.EGL_NO_CONTEXT;
    clearState();
  }
  
  private void setupGlTiming()
  {
    if (PerfMeasurement.isGlTimingSupported())
    {
      Log.d(TAG, "Enabling GL performance measurement");
      this.mPerfMeasurer = new PerfMeasurement();
      return;
    }
    Log.d(TAG, "GL performance measurement not supported on this device");
    this.mPerfMeasurer = null;
  }
  
  private boolean swapBuffers(EGLSurface paramEGLSurface)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    boolean bool = EGL14.eglSwapBuffers(this.mEGLDisplay, paramEGLSurface);
    int i = EGL14.eglGetError();
    if (i == 12301) {
      throw new LegacyExceptionUtils.BufferQueueAbandonedException();
    }
    if (i != 12288) {
      throw new IllegalStateException("swapBuffers: EGL error: 0x" + Integer.toHexString(i));
    }
    return bool;
  }
  
  public void cleanupEGLContext()
  {
    releaseEGLContext();
  }
  
  public void configureSurfaces(Collection<Pair<Surface, Size>> paramCollection)
  {
    releaseEGLContext();
    if ((paramCollection == null) || (paramCollection.size() == 0))
    {
      Log.w(TAG, "No output surfaces configured for GL drawing.");
      return;
    }
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      Object localObject = (Pair)paramCollection.next();
      Surface localSurface = (Surface)((Pair)localObject).first;
      localObject = (Size)((Pair)localObject).second;
      EGLSurfaceHolder localEGLSurfaceHolder;
      try
      {
        localEGLSurfaceHolder = new EGLSurfaceHolder(null);
        localEGLSurfaceHolder.surface = localSurface;
        localEGLSurfaceHolder.width = ((Size)localObject).getWidth();
        localEGLSurfaceHolder.height = ((Size)localObject).getHeight();
        if (!LegacyCameraDevice.needsConversion(localSurface)) {
          break label146;
        }
        this.mConversionSurfaces.add(localEGLSurfaceHolder);
        LegacyCameraDevice.connectSurface(localSurface);
      }
      catch (LegacyExceptionUtils.BufferQueueAbandonedException localBufferQueueAbandonedException)
      {
        Log.w(TAG, "Surface abandoned, skipping configuration... ", localBufferQueueAbandonedException);
      }
      continue;
      label146:
      this.mSurfaces.add(localEGLSurfaceHolder);
    }
    configureEGLContext();
    if (this.mSurfaces.size() > 0) {
      configureEGLOutputSurfaces(this.mSurfaces);
    }
    if (this.mConversionSurfaces.size() > 0) {
      configureEGLPbufferSurfaces(this.mConversionSurfaces);
    }
    if (this.mSurfaces.size() > 0) {}
    for (paramCollection = ((EGLSurfaceHolder)this.mSurfaces.get(0)).eglSurface;; paramCollection = ((EGLSurfaceHolder)this.mConversionSurfaces.get(0)).eglSurface)
    {
      makeCurrent(paramCollection);
      initializeGLState();
      this.mSurfaceTexture = new SurfaceTexture(getTextureId());
      if (SystemProperties.getBoolean("persist.camera.legacy_perf", false)) {
        setupGlTiming();
      }
      return;
    }
  }
  
  public void drawIntoSurfaces(CaptureCollector paramCaptureCollector)
  {
    if (((this.mSurfaces == null) || (this.mSurfaces.size() == 0)) && ((this.mConversionSurfaces == null) || (this.mConversionSurfaces.size() == 0))) {
      return;
    }
    boolean bool = paramCaptureCollector.hasPendingPreviewCaptures();
    checkGlError("before updateTexImage");
    if (bool) {
      beginGlTiming();
    }
    this.mSurfaceTexture.updateTexImage();
    long l = this.mSurfaceTexture.getTimestamp();
    Pair localPair = paramCaptureCollector.previewCaptured(l);
    if (localPair == null)
    {
      if (bool) {
        endGlTiming();
      }
      return;
    }
    RequestHolder localRequestHolder = (RequestHolder)localPair.first;
    Object localObject2 = localRequestHolder.getHolderTargets();
    if (bool) {
      addGlTimestamp(l);
    }
    Object localObject1 = new ArrayList();
    try
    {
      localObject2 = LegacyCameraDevice.getSurfaceIds((Collection)localObject2);
      localObject1 = localObject2;
    }
    catch (LegacyExceptionUtils.BufferQueueAbandonedException localBufferQueueAbandonedException1)
    {
      SurfaceTexture localSurfaceTexture;
      int j;
      int k;
      int i;
      for (;;)
      {
        Log.w(TAG, "Surface abandoned, dropping frame. ", localBufferQueueAbandonedException1);
        localRequestHolder.setOutputAbandoned();
        continue;
        i = 0;
      }
      Iterator localIterator = this.mConversionSurfaces.iterator();
      while (localIterator.hasNext())
      {
        EGLSurfaceHolder localEGLSurfaceHolder2 = (EGLSurfaceHolder)localIterator.next();
        if (LegacyCameraDevice.containsSurfaceId(localEGLSurfaceHolder2.surface, (Collection)localObject1))
        {
          makeCurrent(localEGLSurfaceHolder2.eglSurface);
          localSurfaceTexture = this.mSurfaceTexture;
          j = localEGLSurfaceHolder2.width;
          k = localEGLSurfaceHolder2.height;
          if (this.mFacing == 0) {}
          for (i = 3;; i = 2)
          {
            drawFrame(localSurfaceTexture, j, k, i);
            this.mPBufferPixels.clear();
            GLES20.glReadPixels(0, 0, localEGLSurfaceHolder2.width, localEGLSurfaceHolder2.height, 6408, 5121, this.mPBufferPixels);
            checkGlError("glReadPixels");
            try
            {
              i = LegacyCameraDevice.detectSurfaceType(localEGLSurfaceHolder2.surface);
              LegacyCameraDevice.setSurfaceDimens(localEGLSurfaceHolder2.surface, localEGLSurfaceHolder2.width, localEGLSurfaceHolder2.height);
              LegacyCameraDevice.setNextTimestamp(localEGLSurfaceHolder2.surface, ((Long)localPair.second).longValue());
              LegacyCameraDevice.produceFrame(localEGLSurfaceHolder2.surface, this.mPBufferPixels.array(), localEGLSurfaceHolder2.width, localEGLSurfaceHolder2.height, i);
            }
            catch (LegacyExceptionUtils.BufferQueueAbandonedException localBufferQueueAbandonedException3)
            {
              Log.w(TAG, "Surface abandoned, dropping frame. ", localBufferQueueAbandonedException3);
              localRequestHolder.setOutputAbandoned();
            }
            break;
          }
        }
      }
      paramCaptureCollector.previewProduced();
      if (!bool) {
        return;
      }
      endGlTiming();
    }
    localObject2 = this.mSurfaces.iterator();
    for (;;)
    {
      if (!((Iterator)localObject2).hasNext()) {
        break label340;
      }
      EGLSurfaceHolder localEGLSurfaceHolder1 = (EGLSurfaceHolder)((Iterator)localObject2).next();
      if (LegacyCameraDevice.containsSurfaceId(localEGLSurfaceHolder1.surface, (Collection)localObject1)) {
        try
        {
          LegacyCameraDevice.setSurfaceDimens(localEGLSurfaceHolder1.surface, localEGLSurfaceHolder1.width, localEGLSurfaceHolder1.height);
          makeCurrent(localEGLSurfaceHolder1.eglSurface);
          LegacyCameraDevice.setNextTimestamp(localEGLSurfaceHolder1.surface, ((Long)localPair.second).longValue());
          localSurfaceTexture = this.mSurfaceTexture;
          j = localEGLSurfaceHolder1.width;
          k = localEGLSurfaceHolder1.height;
          if (this.mFacing != 0) {
            break;
          }
          i = 1;
          drawFrame(localSurfaceTexture, j, k, i);
          swapBuffers(localEGLSurfaceHolder1.eglSurface);
        }
        catch (LegacyExceptionUtils.BufferQueueAbandonedException localBufferQueueAbandonedException2)
        {
          Log.w(TAG, "Surface abandoned, dropping frame. ", localBufferQueueAbandonedException2);
          localRequestHolder.setOutputAbandoned();
        }
      }
    }
    label340:
  }
  
  public void flush()
  {
    Log.e(TAG, "Flush not yet implemented.");
  }
  
  public SurfaceTexture getSurfaceTexture()
  {
    return this.mSurfaceTexture;
  }
  
  private class EGLSurfaceHolder
  {
    EGLSurface eglSurface;
    int height;
    Surface surface;
    int width;
    
    private EGLSurfaceHolder() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/SurfaceTextureRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */