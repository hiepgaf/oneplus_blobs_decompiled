package android.opengl;

public class EGLExt
{
  public static final int EGL_CONTEXT_FLAGS_KHR = 12540;
  public static final int EGL_CONTEXT_MAJOR_VERSION_KHR = 12440;
  public static final int EGL_CONTEXT_MINOR_VERSION_KHR = 12539;
  public static final int EGL_OPENGL_ES3_BIT_KHR = 64;
  
  static {}
  
  private static native void _nativeClassInit();
  
  public static native boolean eglPresentationTimeANDROID(EGLDisplay paramEGLDisplay, EGLSurface paramEGLSurface, long paramLong);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/EGLExt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */