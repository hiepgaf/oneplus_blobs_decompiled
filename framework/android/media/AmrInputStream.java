package android.media;

import java.io.IOException;
import java.io.InputStream;

public final class AmrInputStream
  extends InputStream
{
  private static final int SAMPLES_PER_FRAME = 160;
  private static final String TAG = "AmrInputStream";
  private final byte[] mBuf = new byte['ŀ'];
  private int mBufIn = 0;
  private int mBufOut = 0;
  private long mGae;
  private InputStream mInputStream;
  private byte[] mOneByte = new byte[1];
  
  static
  {
    System.loadLibrary("media_jni");
  }
  
  public AmrInputStream(InputStream paramInputStream)
  {
    this.mInputStream = paramInputStream;
    this.mGae = GsmAmrEncoderNew();
    GsmAmrEncoderInitialize(this.mGae);
  }
  
  private static native void GsmAmrEncoderCleanup(long paramLong);
  
  private static native void GsmAmrEncoderDelete(long paramLong);
  
  private static native int GsmAmrEncoderEncode(long paramLong, byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2)
    throws IOException;
  
  private static native void GsmAmrEncoderInitialize(long paramLong);
  
  private static native long GsmAmrEncoderNew();
  
  /* Error */
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 45	android/media/AmrInputStream:mInputStream	Ljava/io/InputStream;
    //   4: ifnull +10 -> 14
    //   7: aload_0
    //   8: getfield 45	android/media/AmrInputStream:mInputStream	Ljava/io/InputStream;
    //   11: invokevirtual 64	java/io/InputStream:close	()V
    //   14: aload_0
    //   15: aconst_null
    //   16: putfield 45	android/media/AmrInputStream:mInputStream	Ljava/io/InputStream;
    //   19: aload_0
    //   20: getfield 51	android/media/AmrInputStream:mGae	J
    //   23: lconst_0
    //   24: lcmp
    //   25: ifeq +10 -> 35
    //   28: aload_0
    //   29: getfield 51	android/media/AmrInputStream:mGae	J
    //   32: invokestatic 66	android/media/AmrInputStream:GsmAmrEncoderCleanup	(J)V
    //   35: aload_0
    //   36: getfield 51	android/media/AmrInputStream:mGae	J
    //   39: lconst_0
    //   40: lcmp
    //   41: ifeq +10 -> 51
    //   44: aload_0
    //   45: getfield 51	android/media/AmrInputStream:mGae	J
    //   48: invokestatic 68	android/media/AmrInputStream:GsmAmrEncoderDelete	(J)V
    //   51: aload_0
    //   52: lconst_0
    //   53: putfield 51	android/media/AmrInputStream:mGae	J
    //   56: return
    //   57: astore_1
    //   58: aload_0
    //   59: lconst_0
    //   60: putfield 51	android/media/AmrInputStream:mGae	J
    //   63: aload_1
    //   64: athrow
    //   65: astore_1
    //   66: aload_0
    //   67: getfield 51	android/media/AmrInputStream:mGae	J
    //   70: lconst_0
    //   71: lcmp
    //   72: ifeq +10 -> 82
    //   75: aload_0
    //   76: getfield 51	android/media/AmrInputStream:mGae	J
    //   79: invokestatic 68	android/media/AmrInputStream:GsmAmrEncoderDelete	(J)V
    //   82: aload_0
    //   83: lconst_0
    //   84: putfield 51	android/media/AmrInputStream:mGae	J
    //   87: aload_1
    //   88: athrow
    //   89: astore_1
    //   90: aload_0
    //   91: lconst_0
    //   92: putfield 51	android/media/AmrInputStream:mGae	J
    //   95: aload_1
    //   96: athrow
    //   97: astore_1
    //   98: aload_0
    //   99: aconst_null
    //   100: putfield 45	android/media/AmrInputStream:mInputStream	Ljava/io/InputStream;
    //   103: aload_0
    //   104: getfield 51	android/media/AmrInputStream:mGae	J
    //   107: lconst_0
    //   108: lcmp
    //   109: ifeq +10 -> 119
    //   112: aload_0
    //   113: getfield 51	android/media/AmrInputStream:mGae	J
    //   116: invokestatic 66	android/media/AmrInputStream:GsmAmrEncoderCleanup	(J)V
    //   119: aload_0
    //   120: getfield 51	android/media/AmrInputStream:mGae	J
    //   123: lconst_0
    //   124: lcmp
    //   125: ifeq +10 -> 135
    //   128: aload_0
    //   129: getfield 51	android/media/AmrInputStream:mGae	J
    //   132: invokestatic 68	android/media/AmrInputStream:GsmAmrEncoderDelete	(J)V
    //   135: aload_0
    //   136: lconst_0
    //   137: putfield 51	android/media/AmrInputStream:mGae	J
    //   140: aload_1
    //   141: athrow
    //   142: astore_1
    //   143: aload_0
    //   144: lconst_0
    //   145: putfield 51	android/media/AmrInputStream:mGae	J
    //   148: aload_1
    //   149: athrow
    //   150: astore_1
    //   151: aload_0
    //   152: getfield 51	android/media/AmrInputStream:mGae	J
    //   155: lconst_0
    //   156: lcmp
    //   157: ifeq +10 -> 167
    //   160: aload_0
    //   161: getfield 51	android/media/AmrInputStream:mGae	J
    //   164: invokestatic 68	android/media/AmrInputStream:GsmAmrEncoderDelete	(J)V
    //   167: aload_0
    //   168: lconst_0
    //   169: putfield 51	android/media/AmrInputStream:mGae	J
    //   172: aload_1
    //   173: athrow
    //   174: astore_1
    //   175: aload_0
    //   176: lconst_0
    //   177: putfield 51	android/media/AmrInputStream:mGae	J
    //   180: aload_1
    //   181: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	182	0	this	AmrInputStream
    //   57	7	1	localObject1	Object
    //   65	23	1	localObject2	Object
    //   89	7	1	localObject3	Object
    //   97	44	1	localObject4	Object
    //   142	7	1	localObject5	Object
    //   150	23	1	localObject6	Object
    //   174	7	1	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   35	51	57	finally
    //   19	35	65	finally
    //   66	82	89	finally
    //   0	14	97	finally
    //   119	135	142	finally
    //   103	119	150	finally
    //   151	167	174	finally
  }
  
  protected void finalize()
    throws Throwable
  {
    if (this.mGae != 0L)
    {
      close();
      throw new IllegalStateException("someone forgot to close AmrInputStream");
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
    if (this.mGae == 0L) {
      throw new IllegalStateException("not open");
    }
    if (this.mBufOut >= this.mBufIn)
    {
      this.mBufOut = 0;
      this.mBufIn = 0;
      i = 0;
      while (i < 320)
      {
        int j = this.mInputStream.read(this.mBuf, i, 320 - i);
        if (j == -1) {
          return -1;
        }
        i += j;
      }
      this.mBufIn = GsmAmrEncoderEncode(this.mGae, this.mBuf, 0, this.mBuf, 0);
    }
    int i = paramInt2;
    if (paramInt2 > this.mBufIn - this.mBufOut) {
      i = this.mBufIn - this.mBufOut;
    }
    System.arraycopy(this.mBuf, this.mBufOut, paramArrayOfByte, paramInt1, i);
    this.mBufOut += i;
    return i;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AmrInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */