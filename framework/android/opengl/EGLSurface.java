package android.opengl;

public class EGLSurface
  extends EGLObjectHandle
{
  private EGLSurface(long paramLong)
  {
    super(paramLong);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof EGLSurface)) {
      return false;
    }
    paramObject = (EGLSurface)paramObject;
    return getNativeHandle() == ((EGLSurface)paramObject).getNativeHandle();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/EGLSurface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */