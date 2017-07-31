package android.os;

import android.util.Log;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MemoryFile
{
  private static final int PROT_READ = 1;
  private static final int PROT_WRITE = 2;
  private static String TAG = "MemoryFile";
  private long mAddress;
  private boolean mAllowPurging = false;
  private FileDescriptor mFD;
  private int mLength;
  
  public MemoryFile(String paramString, int paramInt)
    throws IOException
  {
    this.mLength = paramInt;
    if (paramInt >= 0)
    {
      this.mFD = native_open(paramString, paramInt);
      if (paramInt > 0) {
        this.mAddress = native_mmap(this.mFD, paramInt, 3);
      }
    }
    else
    {
      throw new IOException("Invalid length: " + paramInt);
    }
    this.mAddress = 0L;
  }
  
  public static int getSize(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    return native_get_size(paramFileDescriptor);
  }
  
  private boolean isClosed()
  {
    return !this.mFD.valid();
  }
  
  private boolean isDeactivated()
  {
    return this.mAddress == 0L;
  }
  
  private static native void native_close(FileDescriptor paramFileDescriptor);
  
  private static native int native_get_size(FileDescriptor paramFileDescriptor)
    throws IOException;
  
  private static native long native_mmap(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2)
    throws IOException;
  
  private static native void native_munmap(long paramLong, int paramInt)
    throws IOException;
  
  private static native FileDescriptor native_open(String paramString, int paramInt)
    throws IOException;
  
  private static native void native_pin(FileDescriptor paramFileDescriptor, boolean paramBoolean)
    throws IOException;
  
  private static native int native_read(FileDescriptor paramFileDescriptor, long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    throws IOException;
  
  private static native void native_write(FileDescriptor paramFileDescriptor, long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    throws IOException;
  
  /* Error */
  public boolean allowPurging(boolean paramBoolean)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 43	android/os/MemoryFile:mAllowPurging	Z
    //   6: istore_3
    //   7: iload_3
    //   8: iload_1
    //   9: if_icmpeq +26 -> 35
    //   12: aload_0
    //   13: getfield 49	android/os/MemoryFile:mFD	Ljava/io/FileDescriptor;
    //   16: astore 4
    //   18: iload_1
    //   19: ifeq +20 -> 39
    //   22: iconst_0
    //   23: istore_2
    //   24: aload 4
    //   26: iload_2
    //   27: invokestatic 102	android/os/MemoryFile:native_pin	(Ljava/io/FileDescriptor;Z)V
    //   30: aload_0
    //   31: iload_1
    //   32: putfield 43	android/os/MemoryFile:mAllowPurging	Z
    //   35: aload_0
    //   36: monitorexit
    //   37: iload_3
    //   38: ireturn
    //   39: iconst_1
    //   40: istore_2
    //   41: goto -17 -> 24
    //   44: astore 4
    //   46: aload_0
    //   47: monitorexit
    //   48: aload 4
    //   50: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	51	0	this	MemoryFile
    //   0	51	1	paramBoolean	boolean
    //   23	18	2	bool1	boolean
    //   6	32	3	bool2	boolean
    //   16	9	4	localFileDescriptor	FileDescriptor
    //   44	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	44	finally
    //   12	18	44	finally
    //   24	35	44	finally
  }
  
  public void close()
  {
    deactivate();
    if (!isClosed()) {
      native_close(this.mFD);
    }
  }
  
  void deactivate()
  {
    if (!isDeactivated()) {}
    try
    {
      native_munmap(this.mAddress, this.mLength);
      this.mAddress = 0L;
      return;
    }
    catch (IOException localIOException)
    {
      Log.e(TAG, localIOException.toString());
    }
  }
  
  protected void finalize()
  {
    if (!isClosed())
    {
      Log.e(TAG, "MemoryFile.finalize() called while ashmem still open");
      close();
    }
  }
  
  public FileDescriptor getFileDescriptor()
    throws IOException
  {
    return this.mFD;
  }
  
  public InputStream getInputStream()
  {
    return new MemoryInputStream(null);
  }
  
  public OutputStream getOutputStream()
  {
    return new MemoryOutputStream(null);
  }
  
  public boolean isPurgingAllowed()
  {
    return this.mAllowPurging;
  }
  
  public int length()
  {
    return this.mLength;
  }
  
  public int readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    if (isDeactivated()) {
      throw new IOException("Can't read from deactivated memory file.");
    }
    if ((paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length)) {}
    while ((paramInt3 < 0) || (paramInt3 > paramArrayOfByte.length - paramInt2) || (paramInt1 < 0) || (paramInt1 > this.mLength) || (paramInt3 > this.mLength - paramInt1)) {
      throw new IndexOutOfBoundsException();
    }
    return native_read(this.mFD, this.mAddress, paramArrayOfByte, paramInt1, paramInt2, paramInt3, this.mAllowPurging);
  }
  
  public void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    if (isDeactivated()) {
      throw new IOException("Can't write to deactivated memory file.");
    }
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length)) {}
    while ((paramInt3 < 0) || (paramInt3 > paramArrayOfByte.length - paramInt1) || (paramInt2 < 0) || (paramInt2 > this.mLength) || (paramInt3 > this.mLength - paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    native_write(this.mFD, this.mAddress, paramArrayOfByte, paramInt1, paramInt2, paramInt3, this.mAllowPurging);
  }
  
  private class MemoryInputStream
    extends InputStream
  {
    private int mMark = 0;
    private int mOffset = 0;
    private byte[] mSingleByte;
    
    private MemoryInputStream() {}
    
    public int available()
      throws IOException
    {
      if (this.mOffset >= MemoryFile.-get0(MemoryFile.this)) {
        return 0;
      }
      return MemoryFile.-get0(MemoryFile.this) - this.mOffset;
    }
    
    public void mark(int paramInt)
    {
      this.mMark = this.mOffset;
    }
    
    public boolean markSupported()
    {
      return true;
    }
    
    public int read()
      throws IOException
    {
      if (this.mSingleByte == null) {
        this.mSingleByte = new byte[1];
      }
      if (read(this.mSingleByte, 0, 1) != 1) {
        return -1;
      }
      return this.mSingleByte[0];
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if ((paramInt1 < 0) || (paramInt2 < 0)) {}
      while (paramInt1 + paramInt2 > paramArrayOfByte.length) {
        throw new IndexOutOfBoundsException();
      }
      paramInt2 = Math.min(paramInt2, available());
      if (paramInt2 < 1) {
        return -1;
      }
      paramInt1 = MemoryFile.this.readBytes(paramArrayOfByte, this.mOffset, paramInt1, paramInt2);
      if (paramInt1 > 0) {
        this.mOffset += paramInt1;
      }
      return paramInt1;
    }
    
    public void reset()
      throws IOException
    {
      this.mOffset = this.mMark;
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      long l = paramLong;
      if (this.mOffset + paramLong > MemoryFile.-get0(MemoryFile.this)) {
        l = MemoryFile.-get0(MemoryFile.this) - this.mOffset;
      }
      this.mOffset = ((int)(this.mOffset + l));
      return l;
    }
  }
  
  private class MemoryOutputStream
    extends OutputStream
  {
    private int mOffset = 0;
    private byte[] mSingleByte;
    
    private MemoryOutputStream() {}
    
    public void write(int paramInt)
      throws IOException
    {
      if (this.mSingleByte == null) {
        this.mSingleByte = new byte[1];
      }
      this.mSingleByte[0] = ((byte)paramInt);
      write(this.mSingleByte, 0, 1);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      MemoryFile.this.writeBytes(paramArrayOfByte, paramInt1, this.mOffset, paramInt2);
      this.mOffset += paramInt2;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/MemoryFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */