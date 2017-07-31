package android.graphics;

public class PorterDuffXfermode
  extends Xfermode
{
  public final PorterDuff.Mode mode;
  
  public PorterDuffXfermode(PorterDuff.Mode paramMode)
  {
    this.mode = paramMode;
    this.native_instance = nativeCreateXfermode(paramMode.nativeInt);
  }
  
  private static native long nativeCreateXfermode(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/PorterDuffXfermode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */