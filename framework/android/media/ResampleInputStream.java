package android.media;

import java.io.IOException;
import java.io.InputStream;

public final class ResampleInputStream
  extends InputStream
{
  private static final String TAG = "ResampleInputStream";
  private static final int mFirLength = 29;
  private byte[] mBuf;
  private int mBufCount;
  private InputStream mInputStream;
  private final byte[] mOneByte = new byte[1];
  private final int mRateIn;
  private final int mRateOut;
  
  static
  {
    System.loadLibrary("media_jni");
  }
  
  public ResampleInputStream(InputStream paramInputStream, int paramInt1, int paramInt2)
  {
    if (paramInt1 != paramInt2 * 2) {
      throw new IllegalArgumentException("only support 2:1 at the moment");
    }
    this.mInputStream = paramInputStream;
    this.mRateIn = 2;
    this.mRateOut = 1;
  }
  
  private static native void fir21(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3);
  
  public void close()
    throws IOException
  {
    try
    {
      if (this.mInputStream != null) {
        this.mInputStream.close();
      }
      return;
    }
    finally
    {
      this.mInputStream = null;
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    if (this.mInputStream != null)
    {
      close();
      throw new IllegalStateException("someone forgot to close ResampleInputStream");
    }
  }
  
  public int read()
    throws IOException
  {
    if (read(this.mOneByte, 0, 1) == 1) {
      return this.mOneByte[0] & 0xFF;
    }
    return -1;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.mInputStream == null) {
      throw new IllegalStateException("not open");
    }
    int i = (paramInt2 / 2 * this.mRateIn / this.mRateOut + 29) * 2;
    if (this.mBuf == null) {
      this.mBuf = new byte[i];
    }
    for (;;)
    {
      i = (this.mBufCount / 2 - 29) * this.mRateOut / this.mRateIn * 2;
      if (i > 0)
      {
        if (i < paramInt2) {}
        for (paramInt2 = i;; paramInt2 = paramInt2 / 2 * 2)
        {
          fir21(this.mBuf, 0, paramArrayOfByte, paramInt1, paramInt2 / 2);
          paramInt1 = this.mRateIn * paramInt2 / this.mRateOut;
          this.mBufCount -= paramInt1;
          if (this.mBufCount > 0) {
            System.arraycopy(this.mBuf, paramInt1, this.mBuf, 0, this.mBufCount);
          }
          return paramInt2;
          if (i <= this.mBuf.length) {
            break;
          }
          byte[] arrayOfByte = new byte[i];
          System.arraycopy(this.mBuf, 0, arrayOfByte, 0, this.mBufCount);
          this.mBuf = arrayOfByte;
          break;
        }
      }
      i = this.mInputStream.read(this.mBuf, this.mBufCount, this.mBuf.length - this.mBufCount);
      if (i == -1) {
        return -1;
      }
      this.mBufCount += i;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/ResampleInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */