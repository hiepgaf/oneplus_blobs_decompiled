package android.opengl;

public class EGLConfig
  extends EGLObjectHandle
{
  private EGLConfig(long paramLong)
  {
    super(paramLong);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof EGLConfig)) {
      return false;
    }
    paramObject = (EGLConfig)paramObject;
    return getNativeHandle() == ((EGLConfig)paramObject).getNativeHandle();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/EGLConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */