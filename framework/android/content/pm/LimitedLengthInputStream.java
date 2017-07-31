package android.content.pm;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class LimitedLengthInputStream
  extends FilterInputStream
{
  private final long mEnd;
  private long mOffset;
  
  public LimitedLengthInputStream(InputStream paramInputStream, long paramLong1, long paramLong2)
    throws IOException
  {
    super(paramInputStream);
    if (paramInputStream == null) {
      throw new IOException("in == null");
    }
    if (paramLong1 < 0L) {
      throw new IOException("offset < 0");
    }
    if (paramLong2 < 0L) {
      throw new IOException("length < 0");
    }
    if (paramLong2 > Long.MAX_VALUE - paramLong1) {
      throw new IOException("offset + length > Long.MAX_VALUE");
    }
    this.mEnd = (paramLong1 + paramLong2);
    skip(paramLong1);
    this.mOffset = paramLong1;
  }
  
  public int read()
    throws IOException
  {
    try
    {
      long l1 = this.mOffset;
      long l2 = this.mEnd;
      if (l1 >= l2) {
        return -1;
      }
      this.mOffset += 1L;
      int i = super.read();
      return i;
    }
    finally {}
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.mOffset >= this.mEnd) {
      return -1;
    }
    Arrays.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
    if (this.mOffset > Long.MAX_VALUE - paramInt2) {
      throw new IOException("offset out of bounds: " + this.mOffset + " + " + paramInt2);
    }
    int i = paramInt2;
    if (this.mOffset + paramInt2 > this.mEnd) {
      i = (int)(this.mEnd - this.mOffset);
    }
    paramInt1 = super.read(paramArrayOfByte, paramInt1, i);
    this.mOffset += paramInt1;
    return paramInt1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/LimitedLengthInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */