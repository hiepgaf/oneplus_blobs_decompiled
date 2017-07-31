package android.opengl;

public class EGLDisplay
  extends EGLObjectHandle
{
  private EGLDisplay(long paramLong)
  {
    super(paramLong);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof EGLDisplay)) {
      return false;
    }
    paramObject = (EGLDisplay)paramObject;
    return getNativeHandle() == ((EGLDisplay)paramObject).getNativeHandle();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/EGLDisplay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */