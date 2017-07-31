package android.opengl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback2;
import android.view.SurfaceView;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceView
  extends SurfaceView
  implements SurfaceHolder.Callback2
{
  public static final int DEBUG_CHECK_GL_ERROR = 1;
  public static final int DEBUG_LOG_GL_CALLS = 2;
  private static final boolean LOG_ATTACH_DETACH = false;
  private static final boolean LOG_EGL = false;
  private static final boolean LOG_PAUSE_RESUME = false;
  private static final boolean LOG_RENDERER = false;
  private static final boolean LOG_RENDERER_DRAW_FRAME = false;
  private static final boolean LOG_SURFACE = false;
  private static final boolean LOG_THREADS = false;
  public static final int RENDERMODE_CONTINUOUSLY = 1;
  public static final int RENDERMODE_WHEN_DIRTY = 0;
  private static final String TAG = "GLSurfaceView";
  private static final GLThreadManager sGLThreadManager = new GLThreadManager(null);
  private int mDebugFlags;
  private boolean mDetached;
  private EGLConfigChooser mEGLConfigChooser;
  private int mEGLContextClientVersion;
  private EGLContextFactory mEGLContextFactory;
  private EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
  private GLThread mGLThread;
  private GLWrapper mGLWrapper;
  private boolean mPreserveEGLContextOnPause;
  private Renderer mRenderer;
  private final WeakReference<GLSurfaceView> mThisWeakRef = new WeakReference(this);
  
  public GLSurfaceView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public GLSurfaceView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private void checkRenderThreadState()
  {
    if (this.mGLThread != null) {
      throw new IllegalStateException("setRenderer has already been called for this instance.");
    }
  }
  
  private void init()
  {
    getHolder().addCallback(this);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mGLThread != null) {
        this.mGLThread.requestExitAndWait();
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getDebugFlags()
  {
    return this.mDebugFlags;
  }
  
  public boolean getPreserveEGLContextOnPause()
  {
    return this.mPreserveEGLContextOnPause;
  }
  
  public int getRenderMode()
  {
    return this.mGLThread.getRenderMode();
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if ((this.mDetached) && (this.mRenderer != null))
    {
      int i = 1;
      if (this.mGLThread != null) {
        i = this.mGLThread.getRenderMode();
      }
      this.mGLThread = new GLThread(this.mThisWeakRef);
      if (i != 1) {
        this.mGLThread.setRenderMode(i);
      }
      this.mGLThread.start();
    }
    this.mDetached = false;
  }
  
  protected void onDetachedFromWindow()
  {
    if (this.mGLThread != null) {
      this.mGLThread.requestExitAndWait();
    }
    this.mDetached = true;
    super.onDetachedFromWindow();
  }
  
  public void onPause()
  {
    this.mGLThread.onPause();
  }
  
  public void onResume()
  {
    this.mGLThread.onResume();
  }
  
  public void queueEvent(Runnable paramRunnable)
  {
    this.mGLThread.queueEvent(paramRunnable);
  }
  
  public void requestRender()
  {
    this.mGLThread.requestRender();
  }
  
  public void setDebugFlags(int paramInt)
  {
    this.mDebugFlags = paramInt;
  }
  
  public void setEGLConfigChooser(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    setEGLConfigChooser(new ComponentSizeChooser(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
  }
  
  public void setEGLConfigChooser(EGLConfigChooser paramEGLConfigChooser)
  {
    checkRenderThreadState();
    this.mEGLConfigChooser = paramEGLConfigChooser;
  }
  
  public void setEGLConfigChooser(boolean paramBoolean)
  {
    setEGLConfigChooser(new SimpleEGLConfigChooser(paramBoolean));
  }
  
  public void setEGLContextClientVersion(int paramInt)
  {
    checkRenderThreadState();
    this.mEGLContextClientVersion = paramInt;
  }
  
  public void setEGLContextFactory(EGLContextFactory paramEGLContextFactory)
  {
    checkRenderThreadState();
    this.mEGLContextFactory = paramEGLContextFactory;
  }
  
  public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory paramEGLWindowSurfaceFactory)
  {
    checkRenderThreadState();
    this.mEGLWindowSurfaceFactory = paramEGLWindowSurfaceFactory;
  }
  
  public void setGLWrapper(GLWrapper paramGLWrapper)
  {
    this.mGLWrapper = paramGLWrapper;
  }
  
  public void setPreserveEGLContextOnPause(boolean paramBoolean)
  {
    this.mPreserveEGLContextOnPause = paramBoolean;
  }
  
  public void setRenderMode(int paramInt)
  {
    this.mGLThread.setRenderMode(paramInt);
  }
  
  public void setRenderer(Renderer paramRenderer)
  {
    checkRenderThreadState();
    if (this.mEGLConfigChooser == null) {
      this.mEGLConfigChooser = new SimpleEGLConfigChooser(true);
    }
    if (this.mEGLContextFactory == null) {
      this.mEGLContextFactory = new DefaultContextFactory(null);
    }
    if (this.mEGLWindowSurfaceFactory == null) {
      this.mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory(null);
    }
    this.mRenderer = paramRenderer;
    this.mGLThread = new GLThread(this.mThisWeakRef);
    this.mGLThread.start();
  }
  
  public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mGLThread.onWindowResize(paramInt2, paramInt3);
  }
  
  public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
  {
    this.mGLThread.surfaceCreated();
  }
  
  public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
  {
    this.mGLThread.surfaceDestroyed();
  }
  
  public void surfaceRedrawNeeded(SurfaceHolder paramSurfaceHolder)
  {
    if (this.mGLThread != null) {
      this.mGLThread.requestRenderAndWait();
    }
  }
  
  private abstract class BaseConfigChooser
    implements GLSurfaceView.EGLConfigChooser
  {
    protected int[] mConfigSpec = filterConfigSpec(paramArrayOfInt);
    
    public BaseConfigChooser(int[] paramArrayOfInt) {}
    
    private int[] filterConfigSpec(int[] paramArrayOfInt)
    {
      if ((GLSurfaceView.-get2(GLSurfaceView.this) != 2) && (GLSurfaceView.-get2(GLSurfaceView.this) != 3)) {
        return paramArrayOfInt;
      }
      int i = paramArrayOfInt.length;
      int[] arrayOfInt = new int[i + 2];
      System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, i - 1);
      arrayOfInt[(i - 1)] = 12352;
      if (GLSurfaceView.-get2(GLSurfaceView.this) == 2) {
        arrayOfInt[i] = 4;
      }
      for (;;)
      {
        arrayOfInt[(i + 1)] = 12344;
        return arrayOfInt;
        arrayOfInt[i] = 64;
      }
    }
    
    public EGLConfig chooseConfig(EGL10 paramEGL10, EGLDisplay paramEGLDisplay)
    {
      int[] arrayOfInt = new int[1];
      if (!paramEGL10.eglChooseConfig(paramEGLDisplay, this.mConfigSpec, null, 0, arrayOfInt)) {
        throw new IllegalArgumentException("eglChooseConfig failed");
      }
      int i = arrayOfInt[0];
      if (i <= 0) {
        throw new IllegalArgumentException("No configs match configSpec");
      }
      EGLConfig[] arrayOfEGLConfig = new EGLConfig[i];
      if (!paramEGL10.eglChooseConfig(paramEGLDisplay, this.mConfigSpec, arrayOfEGLConfig, i, arrayOfInt)) {
        throw new IllegalArgumentException("eglChooseConfig#2 failed");
      }
      paramEGL10 = chooseConfig(paramEGL10, paramEGLDisplay, arrayOfEGLConfig);
      if (paramEGL10 == null) {
        throw new IllegalArgumentException("No config chosen");
      }
      return paramEGL10;
    }
    
    abstract EGLConfig chooseConfig(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLConfig[] paramArrayOfEGLConfig);
  }
  
  private class ComponentSizeChooser
    extends GLSurfaceView.BaseConfigChooser
  {
    protected int mAlphaSize;
    protected int mBlueSize;
    protected int mDepthSize;
    protected int mGreenSize;
    protected int mRedSize;
    protected int mStencilSize;
    private int[] mValue = new int[1];
    
    public ComponentSizeChooser(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      super(new int[] { 12324, paramInt1, 12323, paramInt2, 12322, paramInt3, 12321, paramInt4, 12325, paramInt5, 12326, paramInt6, 12344 });
      this.mRedSize = paramInt1;
      this.mGreenSize = paramInt2;
      this.mBlueSize = paramInt3;
      this.mAlphaSize = paramInt4;
      this.mDepthSize = paramInt5;
      this.mStencilSize = paramInt6;
    }
    
    private int findConfigAttrib(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig, int paramInt1, int paramInt2)
    {
      if (paramEGL10.eglGetConfigAttrib(paramEGLDisplay, paramEGLConfig, paramInt1, this.mValue)) {
        return this.mValue[0];
      }
      return paramInt2;
    }
    
    public EGLConfig chooseConfig(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLConfig[] paramArrayOfEGLConfig)
    {
      int j = paramArrayOfEGLConfig.length;
      int i = 0;
      while (i < j)
      {
        EGLConfig localEGLConfig = paramArrayOfEGLConfig[i];
        int k = findConfigAttrib(paramEGL10, paramEGLDisplay, localEGLConfig, 12325, 0);
        int m = findConfigAttrib(paramEGL10, paramEGLDisplay, localEGLConfig, 12326, 0);
        if ((k >= this.mDepthSize) && (m >= this.mStencilSize))
        {
          k = findConfigAttrib(paramEGL10, paramEGLDisplay, localEGLConfig, 12324, 0);
          m = findConfigAttrib(paramEGL10, paramEGLDisplay, localEGLConfig, 12323, 0);
          int n = findConfigAttrib(paramEGL10, paramEGLDisplay, localEGLConfig, 12322, 0);
          int i1 = findConfigAttrib(paramEGL10, paramEGLDisplay, localEGLConfig, 12321, 0);
          if ((k == this.mRedSize) && (m == this.mGreenSize) && (n == this.mBlueSize) && (i1 == this.mAlphaSize)) {
            return localEGLConfig;
          }
        }
        i += 1;
      }
      return null;
    }
  }
  
  private class DefaultContextFactory
    implements GLSurfaceView.EGLContextFactory
  {
    private int EGL_CONTEXT_CLIENT_VERSION = 12440;
    
    private DefaultContextFactory() {}
    
    public EGLContext createContext(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig)
    {
      int[] arrayOfInt = new int[3];
      arrayOfInt[0] = this.EGL_CONTEXT_CLIENT_VERSION;
      arrayOfInt[1] = GLSurfaceView.-get2(GLSurfaceView.this);
      arrayOfInt[2] = 12344;
      EGLContext localEGLContext = EGL10.EGL_NO_CONTEXT;
      if (GLSurfaceView.-get2(GLSurfaceView.this) != 0) {}
      for (;;)
      {
        return paramEGL10.eglCreateContext(paramEGLDisplay, paramEGLConfig, localEGLContext, arrayOfInt);
        arrayOfInt = null;
      }
    }
    
    public void destroyContext(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLContext paramEGLContext)
    {
      if (!paramEGL10.eglDestroyContext(paramEGLDisplay, paramEGLContext))
      {
        Log.e("DefaultContextFactory", "display:" + paramEGLDisplay + " context: " + paramEGLContext);
        GLSurfaceView.EglHelper.throwEglException("eglDestroyContex", paramEGL10.eglGetError());
      }
    }
  }
  
  private static class DefaultWindowSurfaceFactory
    implements GLSurfaceView.EGLWindowSurfaceFactory
  {
    public EGLSurface createWindowSurface(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig, Object paramObject)
    {
      try
      {
        paramEGL10 = paramEGL10.eglCreateWindowSurface(paramEGLDisplay, paramEGLConfig, paramObject, null);
        return paramEGL10;
      }
      catch (IllegalArgumentException paramEGL10)
      {
        Log.e("GLSurfaceView", "eglCreateWindowSurface", paramEGL10);
      }
      return null;
    }
    
    public void destroySurface(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLSurface paramEGLSurface)
    {
      paramEGL10.eglDestroySurface(paramEGLDisplay, paramEGLSurface);
    }
  }
  
  public static abstract interface EGLConfigChooser
  {
    public abstract EGLConfig chooseConfig(EGL10 paramEGL10, EGLDisplay paramEGLDisplay);
  }
  
  public static abstract interface EGLContextFactory
  {
    public abstract EGLContext createContext(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig);
    
    public abstract void destroyContext(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLContext paramEGLContext);
  }
  
  public static abstract interface EGLWindowSurfaceFactory
  {
    public abstract EGLSurface createWindowSurface(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig, Object paramObject);
    
    public abstract void destroySurface(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLSurface paramEGLSurface);
  }
  
  private static class EglHelper
  {
    EGL10 mEgl;
    EGLConfig mEglConfig;
    EGLContext mEglContext;
    EGLDisplay mEglDisplay;
    EGLSurface mEglSurface;
    private WeakReference<GLSurfaceView> mGLSurfaceViewWeakRef;
    
    public EglHelper(WeakReference<GLSurfaceView> paramWeakReference)
    {
      this.mGLSurfaceViewWeakRef = paramWeakReference;
    }
    
    private void destroySurfaceImp()
    {
      if ((this.mEglSurface != null) && (this.mEglSurface != EGL10.EGL_NO_SURFACE))
      {
        this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        GLSurfaceView localGLSurfaceView = (GLSurfaceView)this.mGLSurfaceViewWeakRef.get();
        if (localGLSurfaceView != null) {
          GLSurfaceView.-get4(localGLSurfaceView).destroySurface(this.mEgl, this.mEglDisplay, this.mEglSurface);
        }
        this.mEglSurface = null;
      }
    }
    
    public static String formatEglError(String paramString, int paramInt)
    {
      return paramString + " failed: " + EGLLogWrapper.getErrorString(paramInt);
    }
    
    public static void logEglErrorAsWarning(String paramString1, String paramString2, int paramInt)
    {
      Log.w(paramString1, formatEglError(paramString2, paramInt));
    }
    
    private void throwEglException(String paramString)
    {
      throwEglException(paramString, this.mEgl.eglGetError());
    }
    
    public static void throwEglException(String paramString, int paramInt)
    {
      throw new RuntimeException(formatEglError(paramString, paramInt));
    }
    
    GL createGL()
    {
      GL localGL2 = this.mEglContext.getGL();
      GLSurfaceView localGLSurfaceView = (GLSurfaceView)this.mGLSurfaceViewWeakRef.get();
      Object localObject = localGL2;
      if (localGLSurfaceView != null)
      {
        GL localGL1 = localGL2;
        if (GLSurfaceView.-get5(localGLSurfaceView) != null) {
          localGL1 = GLSurfaceView.-get5(localGLSurfaceView).wrap(localGL2);
        }
        localObject = localGL1;
        if ((GLSurfaceView.-get0(localGLSurfaceView) & 0x3) != 0)
        {
          int i = 0;
          localObject = null;
          if ((GLSurfaceView.-get0(localGLSurfaceView) & 0x1) != 0) {
            i = 1;
          }
          if ((GLSurfaceView.-get0(localGLSurfaceView) & 0x2) != 0) {
            localObject = new GLSurfaceView.LogWriter();
          }
          localObject = GLDebugHelper.wrap(localGL1, i, (Writer)localObject);
        }
      }
      return (GL)localObject;
    }
    
    public boolean createSurface()
    {
      if (this.mEgl == null) {
        throw new RuntimeException("egl not initialized");
      }
      if (this.mEglDisplay == null) {
        throw new RuntimeException("eglDisplay not initialized");
      }
      if (this.mEglConfig == null) {
        throw new RuntimeException("mEglConfig not initialized");
      }
      destroySurfaceImp();
      GLSurfaceView localGLSurfaceView = (GLSurfaceView)this.mGLSurfaceViewWeakRef.get();
      if (localGLSurfaceView != null) {}
      for (this.mEglSurface = GLSurfaceView.-get4(localGLSurfaceView).createWindowSurface(this.mEgl, this.mEglDisplay, this.mEglConfig, localGLSurfaceView.getHolder()); (this.mEglSurface == null) || (this.mEglSurface == EGL10.EGL_NO_SURFACE); this.mEglSurface = null)
      {
        if (this.mEgl.eglGetError() == 12299) {
          Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
        }
        return false;
      }
      if (!this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext))
      {
        logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", this.mEgl.eglGetError());
        return false;
      }
      return true;
    }
    
    public void destroySurface()
    {
      destroySurfaceImp();
    }
    
    public void finish()
    {
      if (this.mEglContext != null)
      {
        GLSurfaceView localGLSurfaceView = (GLSurfaceView)this.mGLSurfaceViewWeakRef.get();
        if (localGLSurfaceView != null) {
          GLSurfaceView.-get3(localGLSurfaceView).destroyContext(this.mEgl, this.mEglDisplay, this.mEglContext);
        }
        this.mEglContext = null;
      }
      if (this.mEglDisplay != null)
      {
        this.mEgl.eglTerminate(this.mEglDisplay);
        this.mEglDisplay = null;
      }
    }
    
    public void start()
    {
      this.mEgl = ((EGL10)EGLContext.getEGL());
      this.mEglDisplay = this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
      if (this.mEglDisplay == EGL10.EGL_NO_DISPLAY) {
        throw new RuntimeException("eglGetDisplay failed");
      }
      Object localObject = new int[2];
      if (!this.mEgl.eglInitialize(this.mEglDisplay, (int[])localObject)) {
        throw new RuntimeException("eglInitialize failed");
      }
      localObject = (GLSurfaceView)this.mGLSurfaceViewWeakRef.get();
      if (localObject == null) {
        this.mEglConfig = null;
      }
      for (this.mEglContext = null;; this.mEglContext = GLSurfaceView.-get3((GLSurfaceView)localObject).createContext(this.mEgl, this.mEglDisplay, this.mEglConfig))
      {
        if ((this.mEglContext == null) || (this.mEglContext == EGL10.EGL_NO_CONTEXT))
        {
          this.mEglContext = null;
          throwEglException("createContext");
        }
        this.mEglSurface = null;
        return;
        this.mEglConfig = GLSurfaceView.-get1((GLSurfaceView)localObject).chooseConfig(this.mEgl, this.mEglDisplay);
      }
    }
    
    public int swap()
    {
      if (!this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface)) {
        return this.mEgl.eglGetError();
      }
      return 12288;
    }
  }
  
  static class GLThread
    extends Thread
  {
    private GLSurfaceView.EglHelper mEglHelper;
    private ArrayList<Runnable> mEventQueue = new ArrayList();
    private boolean mExited;
    private boolean mFinishedCreatingEglSurface;
    private WeakReference<GLSurfaceView> mGLSurfaceViewWeakRef;
    private boolean mHasSurface;
    private boolean mHaveEglContext;
    private boolean mHaveEglSurface;
    private int mHeight = 0;
    private boolean mPaused;
    private boolean mRenderComplete;
    private int mRenderMode = 1;
    private boolean mRequestPaused;
    private boolean mRequestRender = true;
    private boolean mShouldExit;
    private boolean mShouldReleaseEglContext;
    private boolean mSizeChanged = true;
    private boolean mSurfaceIsBad;
    private boolean mWaitingForSurface;
    private boolean mWantRenderNotification = false;
    private int mWidth = 0;
    
    GLThread(WeakReference<GLSurfaceView> paramWeakReference)
    {
      this.mGLSurfaceViewWeakRef = paramWeakReference;
    }
    
    /* Error */
    private void guardedRun()
      throws InterruptedException
    {
      // Byte code:
      //   0: aload_0
      //   1: new 73	android/opengl/GLSurfaceView$EglHelper
      //   4: dup
      //   5: aload_0
      //   6: getfield 64	android/opengl/GLSurfaceView$GLThread:mGLSurfaceViewWeakRef	Ljava/lang/ref/WeakReference;
      //   9: invokespecial 75	android/opengl/GLSurfaceView$EglHelper:<init>	(Ljava/lang/ref/WeakReference;)V
      //   12: putfield 77	android/opengl/GLSurfaceView$GLThread:mEglHelper	Landroid/opengl/GLSurfaceView$EglHelper;
      //   15: aload_0
      //   16: iconst_0
      //   17: putfield 79	android/opengl/GLSurfaceView$GLThread:mHaveEglContext	Z
      //   20: aload_0
      //   21: iconst_0
      //   22: putfield 81	android/opengl/GLSurfaceView$GLThread:mHaveEglSurface	Z
      //   25: aload_0
      //   26: iconst_0
      //   27: putfield 62	android/opengl/GLSurfaceView$GLThread:mWantRenderNotification	Z
      //   30: aconst_null
      //   31: astore 24
      //   33: iconst_0
      //   34: istore 5
      //   36: iconst_0
      //   37: istore 6
      //   39: iconst_0
      //   40: istore 7
      //   42: iconst_0
      //   43: istore_3
      //   44: iconst_0
      //   45: istore 4
      //   47: iconst_0
      //   48: istore 9
      //   50: iconst_0
      //   51: istore_1
      //   52: iconst_0
      //   53: istore_2
      //   54: iconst_0
      //   55: istore 10
      //   57: iconst_0
      //   58: istore 11
      //   60: aconst_null
      //   61: astore 22
      //   63: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   66: astore 25
      //   68: aload 25
      //   70: monitorenter
      //   71: iload_3
      //   72: istore 13
      //   74: iload_1
      //   75: istore 8
      //   77: iload_2
      //   78: istore_1
      //   79: aload_0
      //   80: getfield 87	android/opengl/GLSurfaceView$GLThread:mShouldExit	Z
      //   83: istore 21
      //   85: iload 21
      //   87: ifeq +34 -> 121
      //   90: aload 25
      //   92: monitorexit
      //   93: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   96: astore 22
      //   98: aload 22
      //   100: monitorenter
      //   101: aload_0
      //   102: invokespecial 90	android/opengl/GLSurfaceView$GLThread:stopEglSurfaceLocked	()V
      //   105: aload_0
      //   106: invokespecial 93	android/opengl/GLSurfaceView$GLThread:stopEglContextLocked	()V
      //   109: aload 22
      //   111: monitorexit
      //   112: return
      //   113: astore 23
      //   115: aload 22
      //   117: monitorexit
      //   118: aload 23
      //   120: athrow
      //   121: aload_0
      //   122: getfield 50	android/opengl/GLSurfaceView$GLThread:mEventQueue	Ljava/util/ArrayList;
      //   125: invokevirtual 97	java/util/ArrayList:isEmpty	()Z
      //   128: ifne +93 -> 221
      //   131: aload_0
      //   132: getfield 50	android/opengl/GLSurfaceView$GLThread:mEventQueue	Ljava/util/ArrayList;
      //   135: iconst_0
      //   136: invokevirtual 101	java/util/ArrayList:remove	(I)Ljava/lang/Object;
      //   139: checkcast 103	java/lang/Runnable
      //   142: astore 23
      //   144: iload 9
      //   146: istore 20
      //   148: iload 10
      //   150: istore 18
      //   152: iload 13
      //   154: istore_3
      //   155: iload 11
      //   157: istore 19
      //   159: iload 8
      //   161: istore 10
      //   163: iload 6
      //   165: istore 8
      //   167: iload 5
      //   169: istore 6
      //   171: iload_1
      //   172: istore 14
      //   174: aload 25
      //   176: monitorexit
      //   177: aload 23
      //   179: ifnull +660 -> 839
      //   182: aload 23
      //   184: invokeinterface 106 1 0
      //   189: aconst_null
      //   190: astore 22
      //   192: iload 14
      //   194: istore_2
      //   195: iload 6
      //   197: istore 5
      //   199: iload 8
      //   201: istore 6
      //   203: iload 10
      //   205: istore_1
      //   206: iload 19
      //   208: istore 11
      //   210: iload 18
      //   212: istore 10
      //   214: iload 20
      //   216: istore 9
      //   218: goto -155 -> 63
      //   221: iconst_0
      //   222: istore 21
      //   224: aload_0
      //   225: getfield 108	android/opengl/GLSurfaceView$GLThread:mPaused	Z
      //   228: aload_0
      //   229: getfield 110	android/opengl/GLSurfaceView$GLThread:mRequestPaused	Z
      //   232: if_icmpeq +23 -> 255
      //   235: aload_0
      //   236: getfield 110	android/opengl/GLSurfaceView$GLThread:mRequestPaused	Z
      //   239: istore 21
      //   241: aload_0
      //   242: aload_0
      //   243: getfield 110	android/opengl/GLSurfaceView$GLThread:mRequestPaused	Z
      //   246: putfield 108	android/opengl/GLSurfaceView$GLThread:mPaused	Z
      //   249: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   252: invokevirtual 115	android/opengl/GLSurfaceView$GLThreadManager:notifyAll	()V
      //   255: iload_1
      //   256: istore_3
      //   257: aload_0
      //   258: getfield 117	android/opengl/GLSurfaceView$GLThread:mShouldReleaseEglContext	Z
      //   261: ifeq +18 -> 279
      //   264: aload_0
      //   265: invokespecial 90	android/opengl/GLSurfaceView$GLThread:stopEglSurfaceLocked	()V
      //   268: aload_0
      //   269: invokespecial 93	android/opengl/GLSurfaceView$GLThread:stopEglContextLocked	()V
      //   272: aload_0
      //   273: iconst_0
      //   274: putfield 117	android/opengl/GLSurfaceView$GLThread:mShouldReleaseEglContext	Z
      //   277: iconst_1
      //   278: istore_3
      //   279: iload 13
      //   281: istore 12
      //   283: iload 13
      //   285: ifeq +14 -> 299
      //   288: aload_0
      //   289: invokespecial 90	android/opengl/GLSurfaceView$GLThread:stopEglSurfaceLocked	()V
      //   292: aload_0
      //   293: invokespecial 93	android/opengl/GLSurfaceView$GLThread:stopEglContextLocked	()V
      //   296: iconst_0
      //   297: istore 12
      //   299: iload 21
      //   301: ifeq +14 -> 315
      //   304: aload_0
      //   305: getfield 81	android/opengl/GLSurfaceView$GLThread:mHaveEglSurface	Z
      //   308: ifeq +7 -> 315
      //   311: aload_0
      //   312: invokespecial 90	android/opengl/GLSurfaceView$GLThread:stopEglSurfaceLocked	()V
      //   315: iload 21
      //   317: ifeq +39 -> 356
      //   320: aload_0
      //   321: getfield 79	android/opengl/GLSurfaceView$GLThread:mHaveEglContext	Z
      //   324: ifeq +32 -> 356
      //   327: aload_0
      //   328: getfield 64	android/opengl/GLSurfaceView$GLThread:mGLSurfaceViewWeakRef	Ljava/lang/ref/WeakReference;
      //   331: invokevirtual 123	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
      //   334: checkcast 6	android/opengl/GLSurfaceView
      //   337: astore 23
      //   339: aload 23
      //   341: ifnonnull +337 -> 678
      //   344: iconst_0
      //   345: istore 21
      //   347: iload 21
      //   349: ifne +7 -> 356
      //   352: aload_0
      //   353: invokespecial 93	android/opengl/GLSurfaceView$GLThread:stopEglContextLocked	()V
      //   356: aload_0
      //   357: getfield 125	android/opengl/GLSurfaceView$GLThread:mHasSurface	Z
      //   360: ifne +10 -> 370
      //   363: aload_0
      //   364: getfield 127	android/opengl/GLSurfaceView$GLThread:mWaitingForSurface	Z
      //   367: ifeq +321 -> 688
      //   370: aload_0
      //   371: getfield 125	android/opengl/GLSurfaceView$GLThread:mHasSurface	Z
      //   374: ifeq +21 -> 395
      //   377: aload_0
      //   378: getfield 127	android/opengl/GLSurfaceView$GLThread:mWaitingForSurface	Z
      //   381: ifeq +14 -> 395
      //   384: aload_0
      //   385: iconst_0
      //   386: putfield 127	android/opengl/GLSurfaceView$GLThread:mWaitingForSurface	Z
      //   389: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   392: invokevirtual 115	android/opengl/GLSurfaceView$GLThreadManager:notifyAll	()V
      //   395: iload 8
      //   397: istore 13
      //   399: iload 8
      //   401: ifeq +22 -> 423
      //   404: aload_0
      //   405: iconst_0
      //   406: putfield 62	android/opengl/GLSurfaceView$GLThread:mWantRenderNotification	Z
      //   409: iconst_0
      //   410: istore 13
      //   412: aload_0
      //   413: iconst_1
      //   414: putfield 129	android/opengl/GLSurfaceView$GLThread:mRenderComplete	Z
      //   417: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   420: invokevirtual 115	android/opengl/GLSurfaceView$GLThreadManager:notifyAll	()V
      //   423: iload_3
      //   424: istore 14
      //   426: iload 5
      //   428: istore 16
      //   430: iload 6
      //   432: istore 17
      //   434: iload 7
      //   436: istore 18
      //   438: iload 4
      //   440: istore 15
      //   442: aload_0
      //   443: invokespecial 132	android/opengl/GLSurfaceView$GLThread:readyToDraw	()Z
      //   446: ifeq +357 -> 803
      //   449: iload_3
      //   450: istore_1
      //   451: iload 5
      //   453: istore_2
      //   454: aload_0
      //   455: getfield 79	android/opengl/GLSurfaceView$GLThread:mHaveEglContext	Z
      //   458: ifne +12 -> 470
      //   461: iload_3
      //   462: ifeq +288 -> 750
      //   465: iconst_0
      //   466: istore_1
      //   467: iload 5
      //   469: istore_2
      //   470: iload 6
      //   472: istore_3
      //   473: iload 7
      //   475: istore 5
      //   477: iload 4
      //   479: istore 8
      //   481: aload_0
      //   482: getfield 79	android/opengl/GLSurfaceView$GLThread:mHaveEglContext	Z
      //   485: ifeq +21 -> 506
      //   488: aload_0
      //   489: getfield 81	android/opengl/GLSurfaceView$GLThread:mHaveEglSurface	Z
      //   492: ifeq +295 -> 787
      //   495: iload 4
      //   497: istore 8
      //   499: iload 7
      //   501: istore 5
      //   503: iload 6
      //   505: istore_3
      //   506: iload_1
      //   507: istore 14
      //   509: iload_2
      //   510: istore 16
      //   512: iload_3
      //   513: istore 17
      //   515: iload 5
      //   517: istore 18
      //   519: iload 8
      //   521: istore 15
      //   523: aload_0
      //   524: getfield 81	android/opengl/GLSurfaceView$GLThread:mHaveEglSurface	Z
      //   527: ifeq +276 -> 803
      //   530: iload_3
      //   531: istore 15
      //   533: iload 8
      //   535: istore 16
      //   537: iload 10
      //   539: istore 17
      //   541: aload_0
      //   542: getfield 52	android/opengl/GLSurfaceView$GLThread:mSizeChanged	Z
      //   545: ifeq +31 -> 576
      //   548: iconst_1
      //   549: istore 16
      //   551: aload_0
      //   552: getfield 54	android/opengl/GLSurfaceView$GLThread:mWidth	I
      //   555: istore 17
      //   557: aload_0
      //   558: getfield 56	android/opengl/GLSurfaceView$GLThread:mHeight	I
      //   561: istore 11
      //   563: aload_0
      //   564: iconst_1
      //   565: putfield 62	android/opengl/GLSurfaceView$GLThread:mWantRenderNotification	Z
      //   568: iconst_1
      //   569: istore 15
      //   571: aload_0
      //   572: iconst_0
      //   573: putfield 52	android/opengl/GLSurfaceView$GLThread:mSizeChanged	Z
      //   576: aload_0
      //   577: iconst_0
      //   578: putfield 58	android/opengl/GLSurfaceView$GLThread:mRequestRender	Z
      //   581: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   584: invokevirtual 115	android/opengl/GLSurfaceView$GLThreadManager:notifyAll	()V
      //   587: iload_1
      //   588: istore 14
      //   590: iload_2
      //   591: istore 6
      //   593: iload 15
      //   595: istore 8
      //   597: iload 5
      //   599: istore 7
      //   601: iload 13
      //   603: istore 10
      //   605: aload 22
      //   607: astore 23
      //   609: iload 11
      //   611: istore 19
      //   613: iload 12
      //   615: istore_3
      //   616: iload 16
      //   618: istore 4
      //   620: iload 17
      //   622: istore 18
      //   624: iload 9
      //   626: istore 20
      //   628: aload_0
      //   629: getfield 62	android/opengl/GLSurfaceView$GLThread:mWantRenderNotification	Z
      //   632: ifeq -458 -> 174
      //   635: iconst_1
      //   636: istore 20
      //   638: iload_1
      //   639: istore 14
      //   641: iload_2
      //   642: istore 6
      //   644: iload 15
      //   646: istore 8
      //   648: iload 5
      //   650: istore 7
      //   652: iload 13
      //   654: istore 10
      //   656: aload 22
      //   658: astore 23
      //   660: iload 11
      //   662: istore 19
      //   664: iload 12
      //   666: istore_3
      //   667: iload 16
      //   669: istore 4
      //   671: iload 17
      //   673: istore 18
      //   675: goto -501 -> 174
      //   678: aload 23
      //   680: invokestatic 136	android/opengl/GLSurfaceView:-get6	(Landroid/opengl/GLSurfaceView;)Z
      //   683: istore 21
      //   685: goto -338 -> 347
      //   688: aload_0
      //   689: getfield 81	android/opengl/GLSurfaceView$GLThread:mHaveEglSurface	Z
      //   692: ifeq +7 -> 699
      //   695: aload_0
      //   696: invokespecial 90	android/opengl/GLSurfaceView$GLThread:stopEglSurfaceLocked	()V
      //   699: aload_0
      //   700: iconst_1
      //   701: putfield 127	android/opengl/GLSurfaceView$GLThread:mWaitingForSurface	Z
      //   704: aload_0
      //   705: iconst_0
      //   706: putfield 138	android/opengl/GLSurfaceView$GLThread:mSurfaceIsBad	Z
      //   709: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   712: invokevirtual 115	android/opengl/GLSurfaceView$GLThreadManager:notifyAll	()V
      //   715: goto -345 -> 370
      //   718: astore 22
      //   720: aload 25
      //   722: monitorexit
      //   723: aload 22
      //   725: athrow
      //   726: astore 23
      //   728: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   731: astore 22
      //   733: aload 22
      //   735: monitorenter
      //   736: aload_0
      //   737: invokespecial 90	android/opengl/GLSurfaceView$GLThread:stopEglSurfaceLocked	()V
      //   740: aload_0
      //   741: invokespecial 93	android/opengl/GLSurfaceView$GLThread:stopEglContextLocked	()V
      //   744: aload 22
      //   746: monitorexit
      //   747: aload 23
      //   749: athrow
      //   750: aload_0
      //   751: getfield 77	android/opengl/GLSurfaceView$GLThread:mEglHelper	Landroid/opengl/GLSurfaceView$EglHelper;
      //   754: invokevirtual 141	android/opengl/GLSurfaceView$EglHelper:start	()V
      //   757: aload_0
      //   758: iconst_1
      //   759: putfield 79	android/opengl/GLSurfaceView$GLThread:mHaveEglContext	Z
      //   762: iconst_1
      //   763: istore_2
      //   764: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   767: invokevirtual 115	android/opengl/GLSurfaceView$GLThreadManager:notifyAll	()V
      //   770: iload_3
      //   771: istore_1
      //   772: goto -302 -> 470
      //   775: astore 22
      //   777: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   780: aload_0
      //   781: invokevirtual 145	android/opengl/GLSurfaceView$GLThreadManager:releaseEglContextLocked	(Landroid/opengl/GLSurfaceView$GLThread;)V
      //   784: aload 22
      //   786: athrow
      //   787: aload_0
      //   788: iconst_1
      //   789: putfield 81	android/opengl/GLSurfaceView$GLThread:mHaveEglSurface	Z
      //   792: iconst_1
      //   793: istore_3
      //   794: iconst_1
      //   795: istore 5
      //   797: iconst_1
      //   798: istore 8
      //   800: goto -294 -> 506
      //   803: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   806: invokevirtual 148	android/opengl/GLSurfaceView$GLThreadManager:wait	()V
      //   809: iload 14
      //   811: istore_1
      //   812: iload 16
      //   814: istore 5
      //   816: iload 17
      //   818: istore 6
      //   820: iload 18
      //   822: istore 7
      //   824: iload 13
      //   826: istore 8
      //   828: iload 12
      //   830: istore 13
      //   832: iload 15
      //   834: istore 4
      //   836: goto -757 -> 79
      //   839: iload 8
      //   841: istore 12
      //   843: iload 8
      //   845: ifeq +38 -> 883
      //   848: aload_0
      //   849: getfield 77	android/opengl/GLSurfaceView$GLThread:mEglHelper	Landroid/opengl/GLSurfaceView$EglHelper;
      //   852: invokevirtual 151	android/opengl/GLSurfaceView$EglHelper:createSurface	()Z
      //   855: ifeq +301 -> 1156
      //   858: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   861: astore 22
      //   863: aload 22
      //   865: monitorenter
      //   866: aload_0
      //   867: iconst_1
      //   868: putfield 153	android/opengl/GLSurfaceView$GLThread:mFinishedCreatingEglSurface	Z
      //   871: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   874: invokevirtual 115	android/opengl/GLSurfaceView$GLThreadManager:notifyAll	()V
      //   877: aload 22
      //   879: monitorexit
      //   880: iconst_0
      //   881: istore 12
      //   883: iload 7
      //   885: istore 8
      //   887: aload 24
      //   889: astore 25
      //   891: iload 7
      //   893: ifeq +18 -> 911
      //   896: aload_0
      //   897: getfield 77	android/opengl/GLSurfaceView$GLThread:mEglHelper	Landroid/opengl/GLSurfaceView$EglHelper;
      //   900: invokevirtual 157	android/opengl/GLSurfaceView$EglHelper:createGL	()Ljavax/microedition/khronos/opengles/GL;
      //   903: checkcast 159	javax/microedition/khronos/opengles/GL10
      //   906: astore 25
      //   908: iconst_0
      //   909: istore 8
      //   911: iload 6
      //   913: istore 13
      //   915: iload 6
      //   917: ifeq +56 -> 973
      //   920: aload_0
      //   921: getfield 64	android/opengl/GLSurfaceView$GLThread:mGLSurfaceViewWeakRef	Ljava/lang/ref/WeakReference;
      //   924: invokevirtual 123	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
      //   927: checkcast 6	android/opengl/GLSurfaceView
      //   930: astore 22
      //   932: aload 22
      //   934: ifnull +339 -> 1273
      //   937: ldc2_w 160
      //   940: ldc -93
      //   942: invokestatic 169	android/os/Trace:traceBegin	(JLjava/lang/String;)V
      //   945: aload 22
      //   947: invokestatic 173	android/opengl/GLSurfaceView:-get7	(Landroid/opengl/GLSurfaceView;)Landroid/opengl/GLSurfaceView$Renderer;
      //   950: aload 25
      //   952: aload_0
      //   953: getfield 77	android/opengl/GLSurfaceView$GLThread:mEglHelper	Landroid/opengl/GLSurfaceView$EglHelper;
      //   956: getfield 177	android/opengl/GLSurfaceView$EglHelper:mEglConfig	Ljavax/microedition/khronos/egl/EGLConfig;
      //   959: invokeinterface 182 3 0
      //   964: ldc2_w 160
      //   967: invokestatic 186	android/os/Trace:traceEnd	(J)V
      //   970: goto +303 -> 1273
      //   973: iload 4
      //   975: istore 15
      //   977: iload 4
      //   979: ifeq +53 -> 1032
      //   982: aload_0
      //   983: getfield 64	android/opengl/GLSurfaceView$GLThread:mGLSurfaceViewWeakRef	Ljava/lang/ref/WeakReference;
      //   986: invokevirtual 123	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
      //   989: checkcast 6	android/opengl/GLSurfaceView
      //   992: astore 22
      //   994: aload 22
      //   996: ifnull +283 -> 1279
      //   999: ldc2_w 160
      //   1002: ldc -68
      //   1004: invokestatic 169	android/os/Trace:traceBegin	(JLjava/lang/String;)V
      //   1007: aload 22
      //   1009: invokestatic 173	android/opengl/GLSurfaceView:-get7	(Landroid/opengl/GLSurfaceView;)Landroid/opengl/GLSurfaceView$Renderer;
      //   1012: aload 25
      //   1014: iload 18
      //   1016: iload 19
      //   1018: invokeinterface 191 4 0
      //   1023: ldc2_w 160
      //   1026: invokestatic 186	android/os/Trace:traceEnd	(J)V
      //   1029: goto +250 -> 1279
      //   1032: aload_0
      //   1033: getfield 64	android/opengl/GLSurfaceView$GLThread:mGLSurfaceViewWeakRef	Ljava/lang/ref/WeakReference;
      //   1036: invokevirtual 123	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
      //   1039: checkcast 6	android/opengl/GLSurfaceView
      //   1042: astore 22
      //   1044: aload 22
      //   1046: ifnull +29 -> 1075
      //   1049: ldc2_w 160
      //   1052: ldc -63
      //   1054: invokestatic 169	android/os/Trace:traceBegin	(JLjava/lang/String;)V
      //   1057: aload 22
      //   1059: invokestatic 173	android/opengl/GLSurfaceView:-get7	(Landroid/opengl/GLSurfaceView;)Landroid/opengl/GLSurfaceView$Renderer;
      //   1062: aload 25
      //   1064: invokeinterface 196 2 0
      //   1069: ldc2_w 160
      //   1072: invokestatic 186	android/os/Trace:traceEnd	(J)V
      //   1075: aload_0
      //   1076: getfield 77	android/opengl/GLSurfaceView$GLThread:mEglHelper	Landroid/opengl/GLSurfaceView$EglHelper;
      //   1079: invokevirtual 200	android/opengl/GLSurfaceView$EglHelper:swap	()I
      //   1082: istore_1
      //   1083: iload_3
      //   1084: istore 16
      //   1086: iload_1
      //   1087: lookupswitch	default:+198->1285, 12288:+201->1288, 12302:+297->1384
      //   1112: ldc -55
      //   1114: ldc -53
      //   1116: iload_1
      //   1117: invokestatic 207	android/opengl/GLSurfaceView$EglHelper:logEglErrorAsWarning	(Ljava/lang/String;Ljava/lang/String;I)V
      //   1120: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   1123: astore 22
      //   1125: aload 22
      //   1127: monitorenter
      //   1128: aload_0
      //   1129: iconst_1
      //   1130: putfield 138	android/opengl/GLSurfaceView$GLThread:mSurfaceIsBad	Z
      //   1133: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   1136: invokevirtual 115	android/opengl/GLSurfaceView$GLThreadManager:notifyAll	()V
      //   1139: aload 22
      //   1141: monitorexit
      //   1142: iload_3
      //   1143: istore 16
      //   1145: goto +143 -> 1288
      //   1148: astore 23
      //   1150: aload 22
      //   1152: monitorexit
      //   1153: aload 23
      //   1155: athrow
      //   1156: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   1159: astore 22
      //   1161: aload 22
      //   1163: monitorenter
      //   1164: aload_0
      //   1165: iconst_1
      //   1166: putfield 153	android/opengl/GLSurfaceView$GLThread:mFinishedCreatingEglSurface	Z
      //   1169: aload_0
      //   1170: iconst_1
      //   1171: putfield 138	android/opengl/GLSurfaceView$GLThread:mSurfaceIsBad	Z
      //   1174: invokestatic 85	android/opengl/GLSurfaceView:-get8	()Landroid/opengl/GLSurfaceView$GLThreadManager;
      //   1177: invokevirtual 115	android/opengl/GLSurfaceView$GLThreadManager:notifyAll	()V
      //   1180: aload 22
      //   1182: monitorexit
      //   1183: iload 14
      //   1185: istore_2
      //   1186: iload 6
      //   1188: istore 5
      //   1190: iload 8
      //   1192: istore 6
      //   1194: iload 10
      //   1196: istore_1
      //   1197: aload 23
      //   1199: astore 22
      //   1201: iload 19
      //   1203: istore 11
      //   1205: iload 18
      //   1207: istore 10
      //   1209: iload 20
      //   1211: istore 9
      //   1213: goto -1150 -> 63
      //   1216: astore 23
      //   1218: aload 22
      //   1220: monitorexit
      //   1221: aload 23
      //   1223: athrow
      //   1224: astore 22
      //   1226: ldc2_w 160
      //   1229: invokestatic 186	android/os/Trace:traceEnd	(J)V
      //   1232: aload 22
      //   1234: athrow
      //   1235: astore 22
      //   1237: ldc2_w 160
      //   1240: invokestatic 186	android/os/Trace:traceEnd	(J)V
      //   1243: aload 22
      //   1245: athrow
      //   1246: astore 22
      //   1248: ldc2_w 160
      //   1251: invokestatic 186	android/os/Trace:traceEnd	(J)V
      //   1254: aload 22
      //   1256: athrow
      //   1257: astore 23
      //   1259: aload 22
      //   1261: monitorexit
      //   1262: aload 23
      //   1264: athrow
      //   1265: astore 23
      //   1267: aload 22
      //   1269: monitorexit
      //   1270: aload 23
      //   1272: athrow
      //   1273: iconst_0
      //   1274: istore 13
      //   1276: goto -303 -> 973
      //   1279: iconst_0
      //   1280: istore 15
      //   1282: goto -250 -> 1032
      //   1285: goto -173 -> 1112
      //   1288: iload 14
      //   1290: istore_2
      //   1291: iload 13
      //   1293: istore 5
      //   1295: iload 12
      //   1297: istore 6
      //   1299: iload 8
      //   1301: istore 7
      //   1303: iload 10
      //   1305: istore_1
      //   1306: aload 23
      //   1308: astore 22
      //   1310: aload 25
      //   1312: astore 24
      //   1314: iload 19
      //   1316: istore 11
      //   1318: iload 16
      //   1320: istore_3
      //   1321: iload 15
      //   1323: istore 4
      //   1325: iload 18
      //   1327: istore 10
      //   1329: iload 20
      //   1331: istore 9
      //   1333: iload 20
      //   1335: ifeq -1272 -> 63
      //   1338: iconst_1
      //   1339: istore_1
      //   1340: iconst_0
      //   1341: istore 9
      //   1343: iload 14
      //   1345: istore_2
      //   1346: iload 13
      //   1348: istore 5
      //   1350: iload 12
      //   1352: istore 6
      //   1354: iload 8
      //   1356: istore 7
      //   1358: aload 23
      //   1360: astore 22
      //   1362: aload 25
      //   1364: astore 24
      //   1366: iload 19
      //   1368: istore 11
      //   1370: iload 16
      //   1372: istore_3
      //   1373: iload 15
      //   1375: istore 4
      //   1377: iload 18
      //   1379: istore 10
      //   1381: goto -1318 -> 63
      //   1384: iconst_1
      //   1385: istore 16
      //   1387: goto -99 -> 1288
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	1390	0	this	GLThread
      //   51	1289	1	i	int
      //   53	1293	2	j	int
      //   43	1330	3	k	int
      //   45	1331	4	m	int
      //   34	1315	5	n	int
      //   37	1316	6	i1	int
      //   40	1317	7	i2	int
      //   75	1280	8	i3	int
      //   48	1294	9	i4	int
      //   55	1325	10	i5	int
      //   58	1311	11	i6	int
      //   281	1070	12	i7	int
      //   72	1275	13	i8	int
      //   172	1172	14	i9	int
      //   440	934	15	i10	int
      //   428	958	16	i11	int
      //   432	385	17	i12	int
      //   150	1228	18	i13	int
      //   157	1210	19	i14	int
      //   146	1188	20	i15	int
      //   83	601	21	bool	boolean
      //   61	596	22	localGLThreadManager1	GLSurfaceView.GLThreadManager
      //   718	6	22	localObject1	Object
      //   731	14	22	localGLThreadManager2	GLSurfaceView.GLThreadManager
      //   775	10	22	localRuntimeException	RuntimeException
      //   1224	9	22	localObject3	Object
      //   1235	9	22	localObject4	Object
      //   1246	22	22	localObject5	Object
      //   1308	53	22	localObject6	Object
      //   113	6	23	localObject7	Object
      //   142	537	23	localObject8	Object
      //   726	22	23	localObject9	Object
      //   1148	50	23	localObject10	Object
      //   1216	6	23	localObject11	Object
      //   1257	6	23	localObject12	Object
      //   1265	94	23	localObject13	Object
      //   31	1334	24	localObject14	Object
      //   66	1297	25	localObject15	Object
      // Exception table:
      //   from	to	target	type
      //   101	109	113	finally
      //   79	85	718	finally
      //   121	144	718	finally
      //   224	255	718	finally
      //   257	277	718	finally
      //   288	296	718	finally
      //   304	315	718	finally
      //   320	339	718	finally
      //   352	356	718	finally
      //   356	370	718	finally
      //   370	395	718	finally
      //   404	409	718	finally
      //   412	423	718	finally
      //   442	449	718	finally
      //   454	461	718	finally
      //   481	495	718	finally
      //   523	530	718	finally
      //   541	548	718	finally
      //   551	568	718	finally
      //   571	576	718	finally
      //   576	587	718	finally
      //   628	635	718	finally
      //   678	685	718	finally
      //   688	699	718	finally
      //   699	715	718	finally
      //   750	757	718	finally
      //   757	762	718	finally
      //   764	770	718	finally
      //   777	787	718	finally
      //   787	792	718	finally
      //   803	809	718	finally
      //   63	71	726	finally
      //   90	93	726	finally
      //   174	177	726	finally
      //   182	189	726	finally
      //   720	726	726	finally
      //   848	866	726	finally
      //   877	880	726	finally
      //   896	908	726	finally
      //   920	932	726	finally
      //   964	970	726	finally
      //   982	994	726	finally
      //   1023	1029	726	finally
      //   1032	1044	726	finally
      //   1069	1075	726	finally
      //   1075	1083	726	finally
      //   1112	1128	726	finally
      //   1139	1142	726	finally
      //   1150	1156	726	finally
      //   1156	1164	726	finally
      //   1180	1183	726	finally
      //   1218	1224	726	finally
      //   1226	1235	726	finally
      //   1237	1246	726	finally
      //   1248	1257	726	finally
      //   1259	1265	726	finally
      //   750	757	775	java/lang/RuntimeException
      //   866	877	1148	finally
      //   1164	1180	1216	finally
      //   937	964	1224	finally
      //   999	1023	1235	finally
      //   1049	1069	1246	finally
      //   1128	1139	1257	finally
      //   736	744	1265	finally
    }
    
    private boolean readyToDraw()
    {
      boolean bool2 = true;
      boolean bool1;
      if ((this.mPaused) || (!this.mHasSurface) || (this.mSurfaceIsBad)) {
        bool1 = false;
      }
      do
      {
        do
        {
          return bool1;
          if ((this.mWidth <= 0) || (this.mHeight <= 0)) {
            break;
          }
          bool1 = bool2;
        } while (this.mRequestRender);
        bool1 = bool2;
      } while (this.mRenderMode == 1);
      return false;
    }
    
    private void stopEglContextLocked()
    {
      if (this.mHaveEglContext)
      {
        this.mEglHelper.finish();
        this.mHaveEglContext = false;
        GLSurfaceView.-get8().releaseEglContextLocked(this);
      }
    }
    
    private void stopEglSurfaceLocked()
    {
      if (this.mHaveEglSurface)
      {
        this.mHaveEglSurface = false;
        this.mEglHelper.destroySurface();
      }
    }
    
    public boolean ableToDraw()
    {
      if ((this.mHaveEglContext) && (this.mHaveEglSurface)) {
        return readyToDraw();
      }
      return false;
    }
    
    public int getRenderMode()
    {
      synchronized ()
      {
        int i = this.mRenderMode;
        return i;
      }
    }
    
    public void onPause()
    {
      synchronized ()
      {
        this.mRequestPaused = true;
        GLSurfaceView.-get8().notifyAll();
        for (;;)
        {
          if (!this.mExited)
          {
            boolean bool = this.mPaused;
            if (!bool) {}
          }
          else
          {
            return;
          }
          try
          {
            GLSurfaceView.-get8().wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            Thread.currentThread().interrupt();
          }
        }
      }
    }
    
    public void onResume()
    {
      synchronized ()
      {
        this.mRequestPaused = false;
        this.mRequestRender = true;
        this.mRenderComplete = false;
        GLSurfaceView.-get8().notifyAll();
        for (;;)
        {
          if ((!this.mExited) && (this.mPaused))
          {
            boolean bool = this.mRenderComplete;
            if (!bool) {}
          }
          else
          {
            return;
          }
          try
          {
            GLSurfaceView.-get8().wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            Thread.currentThread().interrupt();
          }
        }
      }
    }
    
    public void onWindowResize(int paramInt1, int paramInt2)
    {
      synchronized ()
      {
        this.mWidth = paramInt1;
        this.mHeight = paramInt2;
        this.mSizeChanged = true;
        this.mRequestRender = true;
        this.mRenderComplete = false;
        Thread localThread = Thread.currentThread();
        if (localThread == this) {
          return;
        }
        GLSurfaceView.-get8().notifyAll();
        for (;;)
        {
          boolean bool;
          if (!this.mExited)
          {
            bool = this.mPaused;
            if (!bool) {
              break label74;
            }
          }
          label74:
          do
          {
            do
            {
              return;
            } while (this.mRenderComplete);
            bool = ableToDraw();
          } while (!bool);
          try
          {
            GLSurfaceView.-get8().wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            Thread.currentThread().interrupt();
          }
        }
      }
    }
    
    public void queueEvent(Runnable paramRunnable)
    {
      if (paramRunnable == null) {
        throw new IllegalArgumentException("r must not be null");
      }
      synchronized (GLSurfaceView.-get8())
      {
        this.mEventQueue.add(paramRunnable);
        GLSurfaceView.-get8().notifyAll();
        return;
      }
    }
    
    public void requestExitAndWait()
    {
      synchronized ()
      {
        this.mShouldExit = true;
        GLSurfaceView.-get8().notifyAll();
        for (;;)
        {
          boolean bool = this.mExited;
          if (!bool) {
            try
            {
              GLSurfaceView.-get8().wait();
            }
            catch (InterruptedException localInterruptedException)
            {
              Thread.currentThread().interrupt();
            }
          }
        }
      }
    }
    
    public void requestReleaseEglContextLocked()
    {
      this.mShouldReleaseEglContext = true;
      GLSurfaceView.-get8().notifyAll();
    }
    
    public void requestRender()
    {
      synchronized ()
      {
        this.mRequestRender = true;
        GLSurfaceView.-get8().notifyAll();
        return;
      }
    }
    
    public void requestRenderAndWait()
    {
      synchronized ()
      {
        Thread localThread = Thread.currentThread();
        if (localThread == this) {
          return;
        }
        this.mWantRenderNotification = true;
        this.mRequestRender = true;
        this.mRenderComplete = false;
        GLSurfaceView.-get8().notifyAll();
        for (;;)
        {
          boolean bool;
          if (!this.mExited)
          {
            bool = this.mPaused;
            if (!bool) {
              break label58;
            }
          }
          label58:
          do
          {
            do
            {
              return;
            } while (this.mRenderComplete);
            bool = ableToDraw();
          } while (!bool);
          try
          {
            GLSurfaceView.-get8().wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            Thread.currentThread().interrupt();
          }
        }
      }
    }
    
    public void run()
    {
      setName("GLThread " + getId());
      try
      {
        guardedRun();
        GLSurfaceView.-get8().threadExiting(this);
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException = localInterruptedException;
        GLSurfaceView.-get8().threadExiting(this);
        return;
      }
      finally
      {
        localObject = finally;
        GLSurfaceView.-get8().threadExiting(this);
        throw ((Throwable)localObject);
      }
    }
    
    public void setRenderMode(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 1)) {
        throw new IllegalArgumentException("renderMode");
      }
      synchronized (GLSurfaceView.-get8())
      {
        this.mRenderMode = paramInt;
        GLSurfaceView.-get8().notifyAll();
        return;
      }
    }
    
    public void surfaceCreated()
    {
      synchronized ()
      {
        this.mHasSurface = true;
        this.mFinishedCreatingEglSurface = false;
        GLSurfaceView.-get8().notifyAll();
        for (;;)
        {
          boolean bool;
          if (this.mWaitingForSurface)
          {
            bool = this.mFinishedCreatingEglSurface;
            if (!bool) {
              break label41;
            }
          }
          label41:
          do
          {
            return;
            bool = this.mExited;
          } while (bool);
          try
          {
            GLSurfaceView.-get8().wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            Thread.currentThread().interrupt();
          }
        }
      }
    }
    
    public void surfaceDestroyed()
    {
      synchronized ()
      {
        this.mHasSurface = false;
        GLSurfaceView.-get8().notifyAll();
        for (;;)
        {
          if (!this.mWaitingForSurface)
          {
            boolean bool = this.mExited;
            if (!bool) {}
          }
          else
          {
            return;
          }
          try
          {
            GLSurfaceView.-get8().wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            Thread.currentThread().interrupt();
          }
        }
      }
    }
  }
  
  private static class GLThreadManager
  {
    private static String TAG = "GLThreadManager";
    
    public void releaseEglContextLocked(GLSurfaceView.GLThread paramGLThread)
    {
      notifyAll();
    }
    
    public void threadExiting(GLSurfaceView.GLThread paramGLThread)
    {
      try
      {
        GLSurfaceView.GLThread.-set0(paramGLThread, true);
        notifyAll();
        return;
      }
      finally
      {
        paramGLThread = finally;
        throw paramGLThread;
      }
    }
  }
  
  public static abstract interface GLWrapper
  {
    public abstract GL wrap(GL paramGL);
  }
  
  static class LogWriter
    extends Writer
  {
    private StringBuilder mBuilder = new StringBuilder();
    
    private void flushBuilder()
    {
      if (this.mBuilder.length() > 0)
      {
        Log.v("GLSurfaceView", this.mBuilder.toString());
        this.mBuilder.delete(0, this.mBuilder.length());
      }
    }
    
    public void close()
    {
      flushBuilder();
    }
    
    public void flush()
    {
      flushBuilder();
    }
    
    public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      int i = 0;
      if (i < paramInt2)
      {
        char c = paramArrayOfChar[(paramInt1 + i)];
        if (c == '\n') {
          flushBuilder();
        }
        for (;;)
        {
          i += 1;
          break;
          this.mBuilder.append(c);
        }
      }
    }
  }
  
  public static abstract interface Renderer
  {
    public abstract void onDrawFrame(GL10 paramGL10);
    
    public abstract void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2);
    
    public abstract void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig);
  }
  
  private class SimpleEGLConfigChooser
    extends GLSurfaceView.ComponentSizeChooser
  {
    public SimpleEGLConfigChooser(boolean paramBoolean) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/GLSurfaceView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */