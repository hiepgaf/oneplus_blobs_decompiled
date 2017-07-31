package android.opengl;

public class EGLContext
  extends EGLObjectHandle
{
  private EGLContext(long paramLong)
  {
    super(paramLong);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof EGLContext)) {
      return false;
    }
    paramObject = (EGLContext)paramObject;
    return getNativeHandle() == ((EGLContext)paramObject).getNativeHandle();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/EGLContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */