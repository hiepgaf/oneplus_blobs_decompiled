package android.opengl;

import java.io.Writer;
import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.opengles.GL;

public class GLDebugHelper
{
  public static final int CONFIG_CHECK_GL_ERROR = 1;
  public static final int CONFIG_CHECK_THREAD = 2;
  public static final int CONFIG_LOG_ARGUMENT_NAMES = 4;
  public static final int ERROR_WRONG_THREAD = 28672;
  
  public static EGL wrap(EGL paramEGL, int paramInt, Writer paramWriter)
  {
    Object localObject = paramEGL;
    if (paramWriter != null) {
      localObject = new EGLLogWrapper(paramEGL, paramInt, paramWriter);
    }
    return (EGL)localObject;
  }
  
  public static GL wrap(GL paramGL, int paramInt, Writer paramWriter)
  {
    if (paramInt != 0) {
      paramGL = new GLErrorWrapper(paramGL, paramInt);
    }
    for (;;)
    {
      if (paramWriter != null)
      {
        if ((paramInt & 0x4) != 0) {}
        for (boolean bool = true;; bool = false) {
          return new GLLogWrapper(paramGL, paramWriter, bool);
        }
      }
      return paramGL;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/GLDebugHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */