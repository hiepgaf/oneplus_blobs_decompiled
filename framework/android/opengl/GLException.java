package android.opengl;

public class GLException
  extends RuntimeException
{
  private final int mError;
  
  public GLException(int paramInt)
  {
    super(getErrorString(paramInt));
    this.mError = paramInt;
  }
  
  public GLException(int paramInt, String paramString)
  {
    super(paramString);
    this.mError = paramInt;
  }
  
  private static String getErrorString(int paramInt)
  {
    String str2 = GLU.gluErrorString(paramInt);
    String str1 = str2;
    if (str2 == null) {
      str1 = "Unknown error 0x" + Integer.toHexString(paramInt);
    }
    return str1;
  }
  
  int getError()
  {
    return this.mError;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/GLException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */