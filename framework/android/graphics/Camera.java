package android.graphics;

public class Camera
{
  private Matrix mMatrix;
  long native_instance;
  
  public Camera()
  {
    nativeConstructor();
  }
  
  private native void nativeApplyToCanvas(long paramLong);
  
  private native void nativeConstructor();
  
  private native void nativeDestructor();
  
  private native void nativeGetMatrix(long paramLong);
  
  public void applyToCanvas(Canvas paramCanvas)
  {
    if (paramCanvas.isHardwareAccelerated())
    {
      if (this.mMatrix == null) {
        this.mMatrix = new Matrix();
      }
      getMatrix(this.mMatrix);
      paramCanvas.concat(this.mMatrix);
      return;
    }
    nativeApplyToCanvas(paramCanvas.getNativeCanvasWrapper());
  }
  
  public native float dotWithNormal(float paramFloat1, float paramFloat2, float paramFloat3);
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      nativeDestructor();
      this.native_instance = 0L;
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public native float getLocationX();
  
  public native float getLocationY();
  
  public native float getLocationZ();
  
  public void getMatrix(Matrix paramMatrix)
  {
    nativeGetMatrix(paramMatrix.native_instance);
  }
  
  public native void restore();
  
  public native void rotate(float paramFloat1, float paramFloat2, float paramFloat3);
  
  public native void rotateX(float paramFloat);
  
  public native void rotateY(float paramFloat);
  
  public native void rotateZ(float paramFloat);
  
  public native void save();
  
  public native void setLocation(float paramFloat1, float paramFloat2, float paramFloat3);
  
  public native void translate(float paramFloat1, float paramFloat2, float paramFloat3);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/Camera.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */