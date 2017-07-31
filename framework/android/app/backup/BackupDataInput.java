package android.app.backup;

import java.io.FileDescriptor;
import java.io.IOException;

public class BackupDataInput
{
  long mBackupReader;
  private EntityHeader mHeader = new EntityHeader(null);
  private boolean mHeaderReady;
  
  public BackupDataInput(FileDescriptor paramFileDescriptor)
  {
    if (paramFileDescriptor == null) {
      throw new NullPointerException();
    }
    this.mBackupReader = ctor(paramFileDescriptor);
    if (this.mBackupReader == 0L) {
      throw new RuntimeException("Native initialization failed with fd=" + paramFileDescriptor);
    }
  }
  
  private static native long ctor(FileDescriptor paramFileDescriptor);
  
  private static native void dtor(long paramLong);
  
  private native int readEntityData_native(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private native int readNextHeader_native(long paramLong, EntityHeader paramEntityHeader);
  
  private native int skipEntityData_native(long paramLong);
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      dtor(this.mBackupReader);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getDataSize()
  {
    if (this.mHeaderReady) {
      return this.mHeader.dataSize;
    }
    throw new IllegalStateException("Entity header not read");
  }
  
  public String getKey()
  {
    if (this.mHeaderReady) {
      return this.mHeader.key;
    }
    throw new IllegalStateException("Entity header not read");
  }
  
  public int readEntityData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.mHeaderReady)
    {
      paramInt1 = readEntityData_native(this.mBackupReader, paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt1 >= 0) {
        return paramInt1;
      }
      throw new IOException("result=0x" + Integer.toHexString(paramInt1));
    }
    throw new IllegalStateException("Entity header not read");
  }
  
  public boolean readNextHeader()
    throws IOException
  {
    int i = readNextHeader_native(this.mBackupReader, this.mHeader);
    if (i == 0)
    {
      this.mHeaderReady = true;
      return true;
    }
    if (i > 0)
    {
      this.mHeaderReady = false;
      return false;
    }
    this.mHeaderReady = false;
    throw new IOException("failed: 0x" + Integer.toHexString(i));
  }
  
  public void skipEntityData()
    throws IOException
  {
    if (this.mHeaderReady)
    {
      skipEntityData_native(this.mBackupReader);
      return;
    }
    throw new IllegalStateException("Entity header not read");
  }
  
  private static class EntityHeader
  {
    int dataSize;
    String key;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/BackupDataInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */