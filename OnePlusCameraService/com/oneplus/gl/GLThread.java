package com.oneplus.gl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Handler;
import android.view.Surface;
import com.oneplus.base.BaseThread;
import com.oneplus.base.BaseThread.ThreadStartCallback;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import java.util.Iterator;
import java.util.LinkedList;

public class GLThread
  extends BaseThread
{
  private static final int[] EGL_CONFIG_ATTRS_ARGB = { 12321, 8, 12324, 8, 12323, 8, 12322, 8, 12325, 16, 12344 };
  private static final int[] EGL_CONTEXT_ATTRS = { 12440, 2, 12344 };
  private static final int[] EGL_EMPTY_ATTRS = { 12344 };
  public static final int FLAG_ABANDON_CONTENT = 1;
  public static final int FLAG_WITH_DEFAULT_EGL_SURFACE = 2;
  public static final PropertyKey<Boolean> PROP_IS_EGL_CONTEXT_READY = new PropertyKey("IsEGLContextReady", Boolean.class, GLThread.class, Boolean.valueOf(false));
  private LinkedList<ActiveEglSurfaceHandle> m_ActiveEglSurfaceHandles;
  private Handle m_DefaultEglSurfaceHandle;
  private EGLConfig m_EglConfig;
  private EGLContext m_EglContext;
  private EGLDisplay m_EglDisplay;
  private LinkedList<EglSurfaceHandle> m_EglSurfaceHandles;
  
  public GLThread(String paramString, BaseThread.ThreadStartCallback paramThreadStartCallback, Handler paramHandler)
  {
    super(paramString, paramThreadStartCallback, paramHandler);
  }
  
  private boolean createEglSurface(EglSurfaceHandle paramEglSurfaceHandle)
  {
    if (paramEglSurfaceHandle.eglSurface != null) {
      return true;
    }
    EGLSurface localEGLSurface;
    if (paramEglSurfaceHandle.surface != null)
    {
      Log.v(this.TAG, "createEglSurface() - Create window surface for ", paramEglSurfaceHandle.surface);
      localEGLSurface = EGL14.eglCreateWindowSurface(this.m_EglDisplay, this.m_EglConfig, paramEglSurfaceHandle.surface, EGL_EMPTY_ATTRS, 0);
      if ((localEGLSurface != null) && (localEGLSurface != EGL14.EGL_NO_SURFACE)) {
        break label160;
      }
      Log.e(this.TAG, "createEglSurface() - Fail to create EGL surface");
      if (paramEglSurfaceHandle.surface == null) {
        break label154;
      }
    }
    label154:
    for (paramEglSurfaceHandle = "CreateWindowSurface";; paramEglSurfaceHandle = "CreatePbufferSurface")
    {
      checkEglError(paramEglSurfaceHandle);
      return false;
      Log.v(this.TAG, "createEglSurface() - Create pbuffer surface");
      int i = paramEglSurfaceHandle.width;
      int j = paramEglSurfaceHandle.height;
      localEGLSurface = EGL14.eglCreatePbufferSurface(this.m_EglDisplay, this.m_EglConfig, new int[] { 12375, i, 12374, j, 12344 }, 0);
      break;
    }
    label160:
    Log.w(this.TAG, "createEglSurface() - EGL surface : " + localEGLSurface + ", size : " + paramEglSurfaceHandle.width + "x" + paramEglSurfaceHandle.height);
    paramEglSurfaceHandle.eglSurface = localEGLSurface;
    return true;
  }
  
  private void destroyEglSurface(EglSurfaceHandle paramEglSurfaceHandle)
  {
    if (paramEglSurfaceHandle.eglSurface == null) {
      return;
    }
    restoreEglSurface(paramEglSurfaceHandle, 1);
    Log.w(this.TAG, "destroyEglSurface() - Destroy " + paramEglSurfaceHandle.eglSurface);
    EGL14.eglDestroySurface(this.m_EglDisplay, paramEglSurfaceHandle.eglSurface);
    paramEglSurfaceHandle.eglSurface = null;
  }
  
  private boolean makeCurrent(EglSurfaceHandle paramEglSurfaceHandle)
  {
    if (paramEglSurfaceHandle != null) {}
    for (EGLSurface localEGLSurface = paramEglSurfaceHandle.eglSurface; localEGLSurface == null; localEGLSurface = EGL14.EGL_NO_SURFACE)
    {
      Log.e(this.TAG, "makeCurrent() - No EGL surface for " + paramEglSurfaceHandle);
      return false;
    }
    if (localEGLSurface != EGL14.EGL_NO_SURFACE)
    {
      if (EGL14.eglMakeCurrent(this.m_EglDisplay, localEGLSurface, localEGLSurface, this.m_EglContext))
      {
        GLES20.glViewport(0, 0, paramEglSurfaceHandle.width, paramEglSurfaceHandle.height);
        return true;
      }
    }
    else if (EGL14.eglMakeCurrent(this.m_EglDisplay, localEGLSurface, localEGLSurface, EGL14.EGL_NO_CONTEXT)) {
      return true;
    }
    Log.e(this.TAG, "makeCurrent() - Fail to make current to " + paramEglSurfaceHandle);
    checkEglError("MakeCurrent");
    return false;
  }
  
  private void restoreEglSurface(ActiveEglSurfaceHandle paramActiveEglSurfaceHandle, int paramInt)
  {
    verifyAccess();
    if (this.m_ActiveEglSurfaceHandles.peekLast() == paramActiveEglSurfaceHandle) {}
    for (int i = 1; !this.m_ActiveEglSurfaceHandles.remove(paramActiveEglSurfaceHandle); i = 0) {
      return;
    }
    if (i == 0) {
      return;
    }
    if ((paramInt & 0x1) == 0) {
      swapBuffers(paramActiveEglSurfaceHandle.surfaceHandle);
    }
    paramActiveEglSurfaceHandle = (ActiveEglSurfaceHandle)this.m_ActiveEglSurfaceHandles.peekLast();
    if (paramActiveEglSurfaceHandle != null)
    {
      if (!makeCurrent(paramActiveEglSurfaceHandle.surfaceHandle)) {
        Log.e(this.TAG, "restoreEglSurface() - Fail to restore current EGL surface to " + paramActiveEglSurfaceHandle.surfaceHandle);
      }
      return;
    }
    EglContextManager.notifyEglContextDestroying();
    makeCurrent(null);
    setReadOnly(PROP_IS_EGL_CONTEXT_READY, Boolean.valueOf(false));
  }
  
  private void restoreEglSurface(EglSurfaceHandle paramEglSurfaceHandle, int paramInt)
  {
    Iterator localIterator = this.m_ActiveEglSurfaceHandles.iterator();
    while (localIterator.hasNext())
    {
      ActiveEglSurfaceHandle localActiveEglSurfaceHandle = (ActiveEglSurfaceHandle)localIterator.next();
      if (localActiveEglSurfaceHandle.surfaceHandle == paramEglSurfaceHandle) {
        restoreEglSurface(localActiveEglSurfaceHandle, paramInt);
      }
    }
  }
  
  private boolean swapBuffers(EglSurfaceHandle paramEglSurfaceHandle)
  {
    paramEglSurfaceHandle = paramEglSurfaceHandle.eglSurface;
    if (paramEglSurfaceHandle != null)
    {
      if (EGL14.eglSwapBuffers(this.m_EglDisplay, paramEglSurfaceHandle)) {
        return true;
      }
      Log.e(this.TAG, "swapBuffers() - Fail to swap buffers");
      checkEglError("SwapBuffers");
    }
    for (;;)
    {
      return false;
      Log.e(this.TAG, "swapBuffers() - No EGL surface to swap");
    }
  }
  
  public void checkEglError(String paramString)
  {
    int i = EGL14.eglGetError();
    if (i != 12288) {
      Log.e(this.TAG, "checkEglError() - Operation : " + paramString + ", error : (" + i + ") " + GLUtils.getEGLErrorString(i));
    }
  }
  
  public final boolean createEglContext()
  {
    return createEglContext(0);
  }
  
  public final boolean createEglContext(int paramInt)
  {
    verifyAccess();
    if (this.m_EglContext == null)
    {
      if (this.m_EglDisplay == null)
      {
        this.m_EglDisplay = EGL14.eglGetDisplay(0);
        Log.w(this.TAG, "createEglContext() - EGL display : " + this.m_EglDisplay);
      }
      Object localObject = new int[2];
      if (!EGL14.eglInitialize(this.m_EglDisplay, (int[])localObject, 0, (int[])localObject, 1))
      {
        Log.e(this.TAG, "createEglContext() - Fail to initialize EGL");
        return false;
      }
      Log.w(this.TAG, "createEglContext() - EGL version : " + localObject[0] + "." + localObject[1]);
      localObject = new EGLConfig[1];
      int[] arrayOfInt = new int[1];
      if (!EGL14.eglChooseConfig(this.m_EglDisplay, EGL_CONFIG_ATTRS_ARGB, 0, (EGLConfig[])localObject, 0, 1, arrayOfInt, 0))
      {
        Log.e(this.TAG, "createEglContext() - Fail to choose configuration");
        EGL14.eglTerminate(this.m_EglDisplay);
        return false;
      }
      this.m_EglConfig = localObject[0];
      Log.w(this.TAG, "createEglContext() - EGL config : " + this.m_EglConfig);
      this.m_EglContext = EGL14.eglCreateContext(this.m_EglDisplay, this.m_EglConfig, EGL14.EGL_NO_CONTEXT, EGL_CONTEXT_ATTRS, 0);
      Log.w(this.TAG, "createEglContext() - EGL context : " + this.m_EglContext);
      onEglContextCreated(this.m_EglContext);
    }
    if (((paramInt & 0x2) == 0) || (Handle.isValid(this.m_DefaultEglSurfaceHandle))) {}
    while (this.m_EglContext != null)
    {
      return true;
      if (this.m_EglContext != null)
      {
        Log.v(this.TAG, "createEglContext() - Create default EGL surface");
        this.m_DefaultEglSurfaceHandle = createEglSurface(null, 1, 1);
        setCurrentEglSurface(this.m_DefaultEglSurfaceHandle, 0);
      }
    }
    return false;
  }
  
  public final Handle createEglSurface(Surface paramSurface, int paramInt1, int paramInt2)
  {
    verifyAccess();
    if (this.m_EglContext == null)
    {
      Log.e(this.TAG, "createEglSurface() - No EGL context");
      return null;
    }
    if ((paramInt1 <= 0) || (paramInt2 <= 0))
    {
      Log.e(this.TAG, "createEglSurface() - Invalid size : " + paramInt1 + "x" + paramInt2);
      return null;
    }
    paramSurface = new EglSurfaceHandle(this, paramSurface, paramInt1, paramInt2);
    if (!createEglSurface(paramSurface)) {
      return null;
    }
    this.m_EglSurfaceHandles.add(paramSurface);
    return paramSurface;
  }
  
  public final void destroyEglContext()
  {
    verifyAccess();
    if (this.m_EglContext == null) {
      return;
    }
    onEglContextDestroying(this.m_EglContext);
    EglContextManager.notifyEglContextDestroying();
    if (!this.m_EglSurfaceHandles.isEmpty())
    {
      Log.w(this.TAG, "destroyEglContext() - Destroy all EGL surfaces");
      int i = this.m_EglSurfaceHandles.size() - 1;
      while (i >= 0)
      {
        localObject = (EglSurfaceHandle)this.m_EglSurfaceHandles.get(i);
        ((EglSurfaceHandle)localObject).close();
        destroyEglSurface((EglSurfaceHandle)localObject);
        i -= 1;
      }
      this.m_EglSurfaceHandles.clear();
    }
    this.m_ActiveEglSurfaceHandles.clear();
    Log.w(this.TAG, "destroyEglContext() - Destroy EGL context");
    EGL14.eglDestroyContext(this.m_EglDisplay, this.m_EglContext);
    Object localObject = this.m_EglContext;
    this.m_EglContext = null;
    this.m_EglConfig = null;
    EGL14.eglTerminate(this.m_EglDisplay);
    onEglContextDestroyed((EGLContext)localObject);
    setReadOnly(PROP_IS_EGL_CONTEXT_READY, Boolean.valueOf(false));
  }
  
  protected void onEglContextCreated(EGLContext paramEGLContext) {}
  
  protected void onEglContextDestroyed(EGLContext paramEGLContext) {}
  
  protected void onEglContextDestroying(EGLContext paramEGLContext) {}
  
  protected void onStarting()
  {
    super.onStarting();
    this.m_ActiveEglSurfaceHandles = new LinkedList();
    this.m_EglSurfaceHandles = new LinkedList();
  }
  
  public void run()
  {
    try
    {
      super.run();
      return;
    }
    finally
    {
      destroyEglContext();
    }
  }
  
  public final Handle setCurrentEglSurface(Handle paramHandle, int paramInt)
  {
    verifyAccess();
    if (paramHandle == null)
    {
      Log.e(this.TAG, "setCurrentEglSurface() - No EGL surface handle");
      return null;
    }
    if (!this.m_EglSurfaceHandles.contains(paramHandle))
    {
      Log.e(this.TAG, "setCurrentEglSurface() - Invalid surface handle");
      return null;
    }
    EglSurfaceHandle localEglSurfaceHandle = (EglSurfaceHandle)paramHandle;
    Object localObject = (ActiveEglSurfaceHandle)this.m_ActiveEglSurfaceHandles.peekLast();
    if ((localObject != null) && (((ActiveEglSurfaceHandle)localObject).surfaceHandle == paramHandle)) {
      return (Handle)localObject;
    }
    localObject = this.m_ActiveEglSurfaceHandles.iterator();
    while (((Iterator)localObject).hasNext())
    {
      ActiveEglSurfaceHandle localActiveEglSurfaceHandle = (ActiveEglSurfaceHandle)((Iterator)localObject).next();
      if (localActiveEglSurfaceHandle.surfaceHandle == paramHandle)
      {
        if (!makeCurrent(localEglSurfaceHandle)) {
          return null;
        }
        this.m_ActiveEglSurfaceHandles.remove(localActiveEglSurfaceHandle);
        this.m_ActiveEglSurfaceHandles.addLast(localActiveEglSurfaceHandle);
        return localActiveEglSurfaceHandle;
      }
    }
    if (!makeCurrent(localEglSurfaceHandle)) {
      return null;
    }
    paramHandle = new ActiveEglSurfaceHandle(localEglSurfaceHandle);
    this.m_ActiveEglSurfaceHandles.addLast(paramHandle);
    if (this.m_ActiveEglSurfaceHandles.size() == 1)
    {
      EglContextManager.notifyEglContextReady();
      setReadOnly(PROP_IS_EGL_CONTEXT_READY, Boolean.valueOf(true));
    }
    return paramHandle;
  }
  
  public boolean swapBuffers(Handle paramHandle)
  {
    verifyAccess();
    if (paramHandle == null)
    {
      Log.e(this.TAG, "swapBuffers() - No EGL surface");
      return false;
    }
    if (!(paramHandle instanceof EglSurfaceHandle))
    {
      Log.e(this.TAG, "swapBuffers() - Invalid EGL surface handle");
      return false;
    }
    paramHandle = (EglSurfaceHandle)paramHandle;
    if ((paramHandle.owner == this) && (Handle.isValid(paramHandle)))
    {
      ActiveEglSurfaceHandle localActiveEglSurfaceHandle = (ActiveEglSurfaceHandle)this.m_ActiveEglSurfaceHandles.peekLast();
      if ((localActiveEglSurfaceHandle == null) || (localActiveEglSurfaceHandle.surfaceHandle != paramHandle))
      {
        Log.w(this.TAG, "swapBuffers() - Not current EGL surface");
        return false;
      }
    }
    else
    {
      Log.e(this.TAG, "swapBuffers() - Invalid EGL surface handle");
      return false;
    }
    return swapBuffers(paramHandle);
  }
  
  private final class ActiveEglSurfaceHandle
    extends Handle
  {
    public final GLThread.EglSurfaceHandle surfaceHandle;
    
    public ActiveEglSurfaceHandle(GLThread.EglSurfaceHandle paramEglSurfaceHandle)
    {
      super();
      this.surfaceHandle = paramEglSurfaceHandle;
    }
    
    protected void onClose(int paramInt)
    {
      GLThread.-wrap1(GLThread.this, this, paramInt);
    }
  }
  
  private static final class EglSurfaceHandle
    extends Handle
  {
    public EGLSurface eglSurface;
    public int height;
    public final GLThread owner;
    public final Surface surface;
    public int width;
    
    public EglSurfaceHandle(GLThread paramGLThread, Surface paramSurface, int paramInt1, int paramInt2)
    {
      super();
      this.owner = paramGLThread;
      this.surface = paramSurface;
      this.width = paramInt1;
      this.height = paramInt2;
    }
    
    public void close()
    {
      closeDirectly();
    }
    
    protected void onClose(int paramInt)
    {
      GLThread.-wrap0(this.owner, this);
    }
    
    public String toString()
    {
      EGLSurface localEGLSurface = this.eglSurface;
      if (localEGLSurface != null) {
        return super.toString() + "{ EGLSurface = " + localEGLSurface + " }";
      }
      return super.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/GLThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */