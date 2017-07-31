package android.graphics;

public class BitmapShader
  extends Shader
{
  public final Bitmap mBitmap;
  private Shader.TileMode mTileX;
  private Shader.TileMode mTileY;
  
  public BitmapShader(Bitmap paramBitmap, Shader.TileMode paramTileMode1, Shader.TileMode paramTileMode2)
  {
    this.mBitmap = paramBitmap;
    this.mTileX = paramTileMode1;
    this.mTileY = paramTileMode2;
    init(nativeCreate(paramBitmap, paramTileMode1.nativeInt, paramTileMode2.nativeInt));
  }
  
  private static native long nativeCreate(Bitmap paramBitmap, int paramInt1, int paramInt2);
  
  protected Shader copy()
  {
    BitmapShader localBitmapShader = new BitmapShader(this.mBitmap, this.mTileX, this.mTileY);
    copyLocalMatrix(localBitmapShader);
    return localBitmapShader;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/BitmapShader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */