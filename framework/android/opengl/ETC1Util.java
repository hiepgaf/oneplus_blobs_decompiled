package android.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ETC1Util
{
  public static ETC1Texture compressTexture(Buffer paramBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(ETC1.getEncodedDataSize(paramInt1, paramInt2)).order(ByteOrder.nativeOrder());
    ETC1.encodeImage(paramBuffer, paramInt1, paramInt2, paramInt3, paramInt4, localByteBuffer);
    return new ETC1Texture(paramInt1, paramInt2, localByteBuffer);
  }
  
  public static ETC1Texture createTexture(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['က'];
    if (paramInputStream.read(arrayOfByte, 0, 16) != 16) {
      throw new IOException("Unable to read PKM file header.");
    }
    ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder());
    localByteBuffer.put(arrayOfByte, 0, 16).position(0);
    if (!ETC1.isValid(localByteBuffer)) {
      throw new IOException("Not a PKM file.");
    }
    int j = ETC1.getWidth(localByteBuffer);
    int k = ETC1.getHeight(localByteBuffer);
    int m = ETC1.getEncodedDataSize(j, k);
    localByteBuffer = ByteBuffer.allocateDirect(m).order(ByteOrder.nativeOrder());
    int i = 0;
    while (i < m)
    {
      int n = Math.min(arrayOfByte.length, m - i);
      if (paramInputStream.read(arrayOfByte, 0, n) != n) {
        throw new IOException("Unable to read PKM file data.");
      }
      localByteBuffer.put(arrayOfByte, 0, n);
      i += n;
    }
    localByteBuffer.position(0);
    return new ETC1Texture(j, k, localByteBuffer);
  }
  
  public static boolean isETC1Supported()
  {
    int[] arrayOfInt2 = new int[20];
    GLES10.glGetIntegerv(34466, arrayOfInt2, 0);
    int j = arrayOfInt2[0];
    int[] arrayOfInt1 = arrayOfInt2;
    if (j > arrayOfInt2.length) {
      arrayOfInt1 = new int[j];
    }
    GLES10.glGetIntegerv(34467, arrayOfInt1, 0);
    int i = 0;
    while (i < j)
    {
      if (arrayOfInt1[i] == 36196) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static void loadTexture(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, ETC1Texture paramETC1Texture)
  {
    if (paramInt4 != 6407) {
      throw new IllegalArgumentException("fallbackFormat must be GL_RGB");
    }
    if ((paramInt5 != 33635) && (paramInt5 != 5121)) {
      throw new IllegalArgumentException("Unsupported fallbackType");
    }
    int j = paramETC1Texture.getWidth();
    int k = paramETC1Texture.getHeight();
    paramETC1Texture = paramETC1Texture.getData();
    if (isETC1Supported())
    {
      GLES10.glCompressedTexImage2D(paramInt1, paramInt2, 36196, j, k, paramInt3, paramETC1Texture.remaining(), paramETC1Texture);
      return;
    }
    if (paramInt5 != 5121)
    {
      i = 1;
      if (i == 0) {
        break label169;
      }
    }
    label169:
    for (int i = 2;; i = 3)
    {
      int m = i * j;
      ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(m * k).order(ByteOrder.nativeOrder());
      ETC1.decodeImage(paramETC1Texture, localByteBuffer, j, k, i, m);
      GLES10.glTexImage2D(paramInt1, paramInt2, paramInt4, j, k, paramInt3, paramInt4, paramInt5, localByteBuffer);
      return;
      i = 0;
      break;
    }
  }
  
  public static void loadTexture(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, InputStream paramInputStream)
    throws IOException
  {
    loadTexture(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, createTexture(paramInputStream));
  }
  
  public static void writeTexture(ETC1Texture paramETC1Texture, OutputStream paramOutputStream)
    throws IOException
  {
    ByteBuffer localByteBuffer = paramETC1Texture.getData();
    int j = localByteBuffer.position();
    try
    {
      int i = paramETC1Texture.getWidth();
      int k = paramETC1Texture.getHeight();
      paramETC1Texture = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder());
      ETC1.formatHeader(paramETC1Texture, i, k);
      byte[] arrayOfByte = new byte['က'];
      paramETC1Texture.get(arrayOfByte, 0, 16);
      paramOutputStream.write(arrayOfByte, 0, 16);
      k = ETC1.getEncodedDataSize(i, k);
      i = 0;
      while (i < k)
      {
        int m = Math.min(arrayOfByte.length, k - i);
        localByteBuffer.get(arrayOfByte, 0, m);
        paramOutputStream.write(arrayOfByte, 0, m);
        i += m;
      }
      return;
    }
    finally
    {
      localByteBuffer.position(j);
    }
  }
  
  public static class ETC1Texture
  {
    private ByteBuffer mData;
    private int mHeight;
    private int mWidth;
    
    public ETC1Texture(int paramInt1, int paramInt2, ByteBuffer paramByteBuffer)
    {
      this.mWidth = paramInt1;
      this.mHeight = paramInt2;
      this.mData = paramByteBuffer;
    }
    
    public ByteBuffer getData()
    {
      return this.mData;
    }
    
    public int getHeight()
    {
      return this.mHeight;
    }
    
    public int getWidth()
    {
      return this.mWidth;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/ETC1Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */