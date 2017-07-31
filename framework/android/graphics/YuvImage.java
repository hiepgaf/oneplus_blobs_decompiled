package android.graphics;

import java.io.OutputStream;

public class YuvImage
{
  private static final int WORKING_COMPRESS_STORAGE = 4096;
  private byte[] mData;
  private int mFormat;
  private int mHeight;
  private int[] mStrides;
  private int mWidth;
  
  public YuvImage(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
  {
    if ((paramInt1 != 17) && (paramInt1 != 20)) {
      throw new IllegalArgumentException("only support ImageFormat.NV21 and ImageFormat.YUY2 for now");
    }
    if ((paramInt2 <= 0) || (paramInt3 <= 0)) {
      throw new IllegalArgumentException("width and height must large than 0");
    }
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("yuv cannot be null");
    }
    if (paramArrayOfInt == null) {}
    for (this.mStrides = calculateStrides(paramInt2, paramInt1);; this.mStrides = paramArrayOfInt)
    {
      this.mData = paramArrayOfByte;
      this.mFormat = paramInt1;
      this.mWidth = paramInt2;
      this.mHeight = paramInt3;
      return;
    }
  }
  
  private void adjustRectangle(Rect paramRect)
  {
    int j = paramRect.width();
    int k = paramRect.height();
    int i = j;
    if (this.mFormat == 17)
    {
      i = j & 0xFFFFFFFE;
      paramRect.left &= 0xFFFFFFFE;
      paramRect.top &= 0xFFFFFFFE;
      paramRect.right = (paramRect.left + i);
      paramRect.bottom = (paramRect.top + (k & 0xFFFFFFFE));
    }
    if (this.mFormat == 20)
    {
      paramRect.left &= 0xFFFFFFFE;
      paramRect.right = (paramRect.left + (i & 0xFFFFFFFE));
    }
  }
  
  private int[] calculateStrides(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 17) {
      return new int[] { paramInt1, paramInt1 };
    }
    if (paramInt2 == 20) {
      return new int[] { paramInt1 * 2 };
    }
    return null;
  }
  
  private static native boolean nativeCompressToJpeg(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt4, OutputStream paramOutputStream, byte[] paramArrayOfByte2);
  
  int[] calculateOffsets(int paramInt1, int paramInt2)
  {
    if (this.mFormat == 17) {
      return new int[] { this.mStrides[0] * paramInt2 + paramInt1, this.mHeight * this.mStrides[0] + paramInt2 / 2 * this.mStrides[1] + paramInt1 / 2 * 2 };
    }
    if (this.mFormat == 20) {
      return new int[] { this.mStrides[0] * paramInt2 + paramInt1 / 2 * 4 };
    }
    return null;
  }
  
  public boolean compressToJpeg(Rect paramRect, int paramInt, OutputStream paramOutputStream)
  {
    if (!new Rect(0, 0, this.mWidth, this.mHeight).contains(paramRect)) {
      throw new IllegalArgumentException("rectangle is not inside the image");
    }
    if ((paramInt < 0) || (paramInt > 100)) {
      throw new IllegalArgumentException("quality must be 0..100");
    }
    if (paramOutputStream == null) {
      throw new IllegalArgumentException("stream cannot be null");
    }
    adjustRectangle(paramRect);
    int[] arrayOfInt = calculateOffsets(paramRect.left, paramRect.top);
    return nativeCompressToJpeg(this.mData, this.mFormat, paramRect.width(), paramRect.height(), arrayOfInt, this.mStrides, paramInt, paramOutputStream, new byte['က']);
  }
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  public int[] getStrides()
  {
    return this.mStrides;
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  public byte[] getYuvData()
  {
    return this.mData;
  }
  
  public int getYuvFormat()
  {
    return this.mFormat;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/YuvImage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */