package android.media;

import android.util.Size;
import java.nio.ByteBuffer;
import libcore.io.Memory;

class ImageUtils
{
  private static void directByteBufferCopy(ByteBuffer paramByteBuffer1, int paramInt1, ByteBuffer paramByteBuffer2, int paramInt2, int paramInt3)
  {
    Memory.memmove(paramByteBuffer2, paramInt2, paramByteBuffer1, paramInt1, paramInt3);
  }
  
  private static Size getEffectivePlaneSizeForImage(Image paramImage, int paramInt)
  {
    switch (paramImage.getFormat())
    {
    default: 
      throw new UnsupportedOperationException(String.format("Invalid image format %d", new Object[] { Integer.valueOf(paramImage.getFormat()) }));
    case 17: 
    case 35: 
    case 842094169: 
      if (paramInt == 0) {
        return new Size(paramImage.getWidth(), paramImage.getHeight());
      }
      return new Size(paramImage.getWidth() / 2, paramImage.getHeight() / 2);
    case 16: 
      if (paramInt == 0) {
        return new Size(paramImage.getWidth(), paramImage.getHeight());
      }
      return new Size(paramImage.getWidth(), paramImage.getHeight() / 2);
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 20: 
    case 32: 
    case 37: 
    case 38: 
    case 256: 
    case 538982489: 
    case 540422489: 
      return new Size(paramImage.getWidth(), paramImage.getHeight());
    }
    return new Size(0, 0);
  }
  
  public static int getEstimatedNativeAllocBytes(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    double d;
    switch (paramInt3)
    {
    default: 
      throw new UnsupportedOperationException(String.format("Invalid format specified %d", new Object[] { Integer.valueOf(paramInt3) }));
    case 256: 
    case 257: 
      d = 0.3D;
    }
    for (;;)
    {
      return (int)(paramInt1 * paramInt2 * d * paramInt4);
      d = 1.0D;
      continue;
      d = 1.25D;
      continue;
      d = 1.5D;
      continue;
      d = 2.0D;
      continue;
      d = 3.0D;
      continue;
      d = 4.0D;
    }
  }
  
  public static int getNumPlanesForFormat(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new UnsupportedOperationException(String.format("Invalid format specified %d", new Object[] { Integer.valueOf(paramInt) }));
    case 17: 
    case 35: 
    case 842094169: 
      return 3;
    case 16: 
      return 2;
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 20: 
    case 32: 
    case 36: 
    case 37: 
    case 38: 
    case 256: 
    case 257: 
    case 538982489: 
    case 540422489: 
    case 1144402265: 
      return 1;
    }
    return 0;
  }
  
  public static void imageCopy(Image paramImage1, Image paramImage2)
  {
    if ((paramImage1 == null) || (paramImage2 == null)) {
      throw new IllegalArgumentException("Images should be non-null");
    }
    if (paramImage1.getFormat() != paramImage2.getFormat()) {
      throw new IllegalArgumentException("Src and dst images should have the same format");
    }
    if ((paramImage1.getFormat() == 34) || (paramImage2.getFormat() == 34)) {
      throw new IllegalArgumentException("PRIVATE format images are not copyable");
    }
    if (paramImage1.getFormat() == 36) {
      throw new IllegalArgumentException("Copy of RAW_OPAQUE format has not been implemented");
    }
    if (!(paramImage2.getOwner() instanceof ImageWriter)) {
      throw new IllegalArgumentException("Destination image is not from ImageWriter. Only the images from ImageWriter are writable");
    }
    Object localObject1 = new Size(paramImage1.getWidth(), paramImage1.getHeight());
    Object localObject2 = new Size(paramImage2.getWidth(), paramImage2.getHeight());
    if (!((Size)localObject1).equals(localObject2)) {
      throw new IllegalArgumentException("source image size " + localObject1 + " is different" + " with " + "destination image size " + localObject2);
    }
    localObject1 = paramImage1.getPlanes();
    paramImage2 = paramImage2.getPlanes();
    int i = 0;
    if (i < localObject1.length)
    {
      int i3 = localObject1[i].getRowStride();
      int i4 = paramImage2[i].getRowStride();
      localObject2 = localObject1[i].getBuffer();
      ByteBuffer localByteBuffer = paramImage2[i].getBuffer();
      if (((ByteBuffer)localObject2).isDirect()) {}
      for (boolean bool = localByteBuffer.isDirect(); !bool; bool = false) {
        throw new IllegalArgumentException("Source and destination ByteBuffers must be direct byteBuffer!");
      }
      if (localObject1[i].getPixelStride() != paramImage2[i].getPixelStride()) {
        throw new IllegalArgumentException("Source plane image pixel stride " + localObject1[i].getPixelStride() + " must be same as destination image pixel stride " + paramImage2[i].getPixelStride());
      }
      int i5 = ((ByteBuffer)localObject2).position();
      ((ByteBuffer)localObject2).rewind();
      localByteBuffer.rewind();
      if (i3 == i4) {
        localByteBuffer.put((ByteBuffer)localObject2);
      }
      for (;;)
      {
        ((ByteBuffer)localObject2).position(i5);
        localByteBuffer.rewind();
        i += 1;
        break;
        int k = ((ByteBuffer)localObject2).position();
        int m = localByteBuffer.position();
        Size localSize = getEffectivePlaneSizeForImage(paramImage1, i);
        int n = localSize.getWidth() * localObject1[i].getPixelStride();
        int j = 0;
        while (j < localSize.getHeight())
        {
          int i1 = n;
          if (j == localSize.getHeight() - 1)
          {
            int i2 = ((ByteBuffer)localObject2).remaining() - k;
            i1 = n;
            if (n > i2) {
              i1 = i2;
            }
          }
          directByteBufferCopy((ByteBuffer)localObject2, k, localByteBuffer, m, i1);
          k += i3;
          m += i4;
          j += 1;
          n = i1;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/ImageUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */