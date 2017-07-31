package android.graphics;

public class PathMeasure
{
  public static final int POSITION_MATRIX_FLAG = 1;
  public static final int TANGENT_MATRIX_FLAG = 2;
  private Path mPath;
  private long native_instance;
  
  public PathMeasure()
  {
    this.mPath = null;
    this.native_instance = native_create(0L, false);
  }
  
  public PathMeasure(Path paramPath, boolean paramBoolean)
  {
    this.mPath = paramPath;
    if (paramPath != null) {}
    for (long l = paramPath.readOnlyNI();; l = 0L)
    {
      this.native_instance = native_create(l, paramBoolean);
      return;
    }
  }
  
  private static native long native_create(long paramLong, boolean paramBoolean);
  
  private static native void native_destroy(long paramLong);
  
  private static native float native_getLength(long paramLong);
  
  private static native boolean native_getMatrix(long paramLong1, float paramFloat, long paramLong2, int paramInt);
  
  private static native boolean native_getPosTan(long paramLong, float paramFloat, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2);
  
  private static native boolean native_getSegment(long paramLong1, float paramFloat1, float paramFloat2, long paramLong2, boolean paramBoolean);
  
  private static native boolean native_isClosed(long paramLong);
  
  private static native boolean native_nextContour(long paramLong);
  
  private static native void native_setPath(long paramLong1, long paramLong2, boolean paramBoolean);
  
  protected void finalize()
    throws Throwable
  {
    native_destroy(this.native_instance);
    this.native_instance = 0L;
  }
  
  public float getLength()
  {
    return native_getLength(this.native_instance);
  }
  
  public boolean getMatrix(float paramFloat, Matrix paramMatrix, int paramInt)
  {
    return native_getMatrix(this.native_instance, paramFloat, paramMatrix.native_instance, paramInt);
  }
  
  public boolean getPosTan(float paramFloat, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    if ((paramArrayOfFloat1 != null) && (paramArrayOfFloat1.length < 2)) {}
    while ((paramArrayOfFloat2 != null) && (paramArrayOfFloat2.length < 2)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return native_getPosTan(this.native_instance, paramFloat, paramArrayOfFloat1, paramArrayOfFloat2);
  }
  
  public boolean getSegment(float paramFloat1, float paramFloat2, Path paramPath, boolean paramBoolean)
  {
    float f2 = getLength();
    float f1 = paramFloat1;
    if (paramFloat1 < 0.0F) {
      f1 = 0.0F;
    }
    paramFloat1 = paramFloat2;
    if (paramFloat2 > f2) {
      paramFloat1 = f2;
    }
    if (f1 >= paramFloat1) {
      return false;
    }
    return native_getSegment(this.native_instance, f1, paramFloat1, paramPath.mutateNI(), paramBoolean);
  }
  
  public boolean isClosed()
  {
    return native_isClosed(this.native_instance);
  }
  
  public boolean nextContour()
  {
    return native_nextContour(this.native_instance);
  }
  
  public void setPath(Path paramPath, boolean paramBoolean)
  {
    this.mPath = paramPath;
    long l2 = this.native_instance;
    if (paramPath != null) {}
    for (long l1 = paramPath.readOnlyNI();; l1 = 0L)
    {
      native_setPath(l2, l1, paramBoolean);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/PathMeasure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */