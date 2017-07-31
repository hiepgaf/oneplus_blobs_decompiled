package android.graphics;

public class NinePatch
{
  private final Bitmap mBitmap;
  public long mNativeChunk;
  private Paint mPaint;
  private String mSrcName;
  
  public NinePatch(Bitmap paramBitmap, byte[] paramArrayOfByte)
  {
    this(paramBitmap, paramArrayOfByte, null);
  }
  
  public NinePatch(Bitmap paramBitmap, byte[] paramArrayOfByte, String paramString)
  {
    this.mBitmap = paramBitmap;
    this.mSrcName = paramString;
    this.mNativeChunk = validateNinePatchChunk(paramArrayOfByte);
  }
  
  public NinePatch(NinePatch paramNinePatch)
  {
    this.mBitmap = paramNinePatch.mBitmap;
    this.mSrcName = paramNinePatch.mSrcName;
    if (paramNinePatch.mPaint != null) {
      this.mPaint = new Paint(paramNinePatch.mPaint);
    }
    this.mNativeChunk = paramNinePatch.mNativeChunk;
  }
  
  public static native boolean isNinePatchChunk(byte[] paramArrayOfByte);
  
  private static native void nativeFinalize(long paramLong);
  
  private static native long nativeGetTransparentRegion(Bitmap paramBitmap, long paramLong, Rect paramRect);
  
  private static native long validateNinePatchChunk(byte[] paramArrayOfByte);
  
  public void draw(Canvas paramCanvas, Rect paramRect)
  {
    paramCanvas.drawPatch(this, paramRect, this.mPaint);
  }
  
  public void draw(Canvas paramCanvas, Rect paramRect, Paint paramPaint)
  {
    paramCanvas.drawPatch(this, paramRect, paramPaint);
  }
  
  public void draw(Canvas paramCanvas, RectF paramRectF)
  {
    paramCanvas.drawPatch(this, paramRectF, this.mPaint);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mNativeChunk != 0L)
      {
        nativeFinalize(this.mNativeChunk);
        this.mNativeChunk = 0L;
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public Bitmap getBitmap()
  {
    return this.mBitmap;
  }
  
  public int getDensity()
  {
    return this.mBitmap.mDensity;
  }
  
  public int getHeight()
  {
    return this.mBitmap.getHeight();
  }
  
  public String getName()
  {
    return this.mSrcName;
  }
  
  public Paint getPaint()
  {
    return this.mPaint;
  }
  
  public final Region getTransparentRegion(Rect paramRect)
  {
    long l = nativeGetTransparentRegion(this.mBitmap, this.mNativeChunk, paramRect);
    if (l != 0L) {
      return new Region(l);
    }
    return null;
  }
  
  public int getWidth()
  {
    return this.mBitmap.getWidth();
  }
  
  public final boolean hasAlpha()
  {
    return this.mBitmap.hasAlpha();
  }
  
  public void setPaint(Paint paramPaint)
  {
    this.mPaint = paramPaint;
  }
  
  public static class InsetStruct
  {
    public final Rect opticalRect;
    public final float outlineAlpha;
    public final float outlineRadius;
    public final Rect outlineRect;
    
    InsetStruct(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, float paramFloat1, int paramInt9, float paramFloat2)
    {
      this.opticalRect = new Rect(paramInt1, paramInt2, paramInt3, paramInt4);
      this.opticalRect.scale(paramFloat2);
      this.outlineRect = scaleInsets(paramInt5, paramInt6, paramInt7, paramInt8, paramFloat2);
      this.outlineRadius = (paramFloat1 * paramFloat2);
      this.outlineAlpha = (paramInt9 / 255.0F);
    }
    
    public static Rect scaleInsets(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat)
    {
      if (paramFloat == 1.0F) {
        return new Rect(paramInt1, paramInt2, paramInt3, paramInt4);
      }
      Rect localRect = new Rect();
      localRect.left = ((int)Math.ceil(paramInt1 * paramFloat));
      localRect.top = ((int)Math.ceil(paramInt2 * paramFloat));
      localRect.right = ((int)Math.ceil(paramInt3 * paramFloat));
      localRect.bottom = ((int)Math.ceil(paramInt4 * paramFloat));
      return localRect;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/NinePatch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */