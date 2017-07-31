package android.graphics;

public class TableMaskFilter
  extends MaskFilter
{
  private TableMaskFilter(long paramLong)
  {
    this.native_instance = paramLong;
  }
  
  public TableMaskFilter(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length < 256) {
      throw new RuntimeException("table.length must be >= 256");
    }
    this.native_instance = nativeNewTable(paramArrayOfByte);
  }
  
  public static TableMaskFilter CreateClipTable(int paramInt1, int paramInt2)
  {
    return new TableMaskFilter(nativeNewClip(paramInt1, paramInt2));
  }
  
  public static TableMaskFilter CreateGammaTable(float paramFloat)
  {
    return new TableMaskFilter(nativeNewGamma(paramFloat));
  }
  
  private static native long nativeNewClip(int paramInt1, int paramInt2);
  
  private static native long nativeNewGamma(float paramFloat);
  
  private static native long nativeNewTable(byte[] paramArrayOfByte);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/TableMaskFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */