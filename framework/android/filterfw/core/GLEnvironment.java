package android.filterfw.core;

import android.graphics.SurfaceTexture;
import android.media.MediaRecorder;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

public class GLEnvironment
{
  private int glEnvId;
  private boolean mManageContext = true;
  
  static
  {
    System.loadLibrary("filterfw");
  }
  
  public GLEnvironment()
  {
    nativeAllocate();
  }
  
  private GLEnvironment(NativeAllocatorTag paramNativeAllocatorTag) {}
  
  public static boolean isAnyContextActive()
  {
    return nativeIsAnyContextActive();
  }
  
  private native boolean nativeActivate();
  
  private native boolean nativeActivateSurfaceId(int paramInt);
  
  private native int nativeAddSurface(Surface paramSurface);
  
  private native int nativeAddSurfaceFromMediaRecorder(MediaRecorder paramMediaRecorder);
  
  private native int nativeAddSurfaceWidthHeight(Surface paramSurface, int paramInt1, int paramInt2);
  
  private native boolean nativeAllocate();
  
  private native boolean nativeDeactivate();
  
  private native boolean nativeDeallocate();
  
  private native boolean nativeDisconnectSurfaceMediaSource(MediaRecorder paramMediaRecorder);
  
  private native boolean nativeInitWithCurrentContext();
  
  private native boolean nativeInitWithNewContext();
  
  private native boolean nativeIsActive();
  
  private static native boolean nativeIsAnyContextActive();
  
  private native boolean nativeIsContextActive();
  
  private native boolean nativeRemoveSurfaceId(int paramInt);
  
  private native boolean nativeSetSurfaceTimestamp(long paramLong);
  
  private native boolean nativeSwapBuffers();
  
  public void activate()
  {
    if ((Looper.myLooper() != null) && (Looper.myLooper().equals(Looper.getMainLooper()))) {
      Log.e("FilterFramework", "Activating GL context in UI thread!");
    }
    if ((!this.mManageContext) || (nativeActivate())) {
      return;
    }
    throw new RuntimeException("Could not activate GLEnvironment!");
  }
  
  public void activateSurfaceWithId(int paramInt)
  {
    if (!nativeActivateSurfaceId(paramInt)) {
      throw new RuntimeException("Could not activate surface " + paramInt + "!");
    }
  }
  
  public void deactivate()
  {
    if ((!this.mManageContext) || (nativeDeactivate())) {
      return;
    }
    throw new RuntimeException("Could not deactivate GLEnvironment!");
  }
  
  protected void finalize()
    throws Throwable
  {
    tearDown();
  }
  
  public void initWithCurrentContext()
  {
    this.mManageContext = false;
    if (!nativeInitWithCurrentContext()) {
      throw new RuntimeException("Could not initialize GLEnvironment with current context!");
    }
  }
  
  public void initWithNewContext()
  {
    this.mManageContext = true;
    if (!nativeInitWithNewContext()) {
      throw new RuntimeException("Could not initialize GLEnvironment with new context!");
    }
  }
  
  public boolean isActive()
  {
    return nativeIsActive();
  }
  
  public boolean isContextActive()
  {
    return nativeIsContextActive();
  }
  
  public int registerSurface(Surface paramSurface)
  {
    int i = nativeAddSurface(paramSurface);
    if (i < 0) {
      throw new RuntimeException("Error registering surface " + paramSurface + "!");
    }
    return i;
  }
  
  public int registerSurfaceFromMediaRecorder(MediaRecorder paramMediaRecorder)
  {
    int i = nativeAddSurfaceFromMediaRecorder(paramMediaRecorder);
    if (i < 0) {
      throw new RuntimeException("Error registering surface from MediaRecorder" + paramMediaRecorder + "!");
    }
    return i;
  }
  
  public int registerSurfaceTexture(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    Surface localSurface = new Surface(paramSurfaceTexture);
    paramInt1 = nativeAddSurfaceWidthHeight(localSurface, paramInt1, paramInt2);
    localSurface.release();
    if (paramInt1 < 0) {
      throw new RuntimeException("Error registering surfaceTexture " + paramSurfaceTexture + "!");
    }
    return paramInt1;
  }
  
  public void setSurfaceTimestamp(long paramLong)
  {
    if (!nativeSetSurfaceTimestamp(paramLong)) {
      throw new RuntimeException("Could not set timestamp for current surface!");
    }
  }
  
  public void swapBuffers()
  {
    if (!nativeSwapBuffers()) {
      throw new RuntimeException("Error swapping EGL buffers!");
    }
  }
  
  public void tearDown()
  {
    try
    {
      if (this.glEnvId != -1)
      {
        nativeDeallocate();
        this.glEnvId = -1;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void unregisterSurfaceId(int paramInt)
  {
    if (!nativeRemoveSurfaceId(paramInt)) {
      throw new RuntimeException("Could not unregister surface " + paramInt + "!");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/GLEnvironment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */