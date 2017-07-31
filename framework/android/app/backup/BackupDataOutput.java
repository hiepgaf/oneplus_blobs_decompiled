package android.app.backup;

import java.io.FileDescriptor;
import java.io.IOException;

public class BackupDataOutput
{
  long mBackupWriter;
  
  public BackupDataOutput(FileDescriptor paramFileDescriptor)
  {
    if (paramFileDescriptor == null) {
      throw new NullPointerException();
    }
    this.mBackupWriter = ctor(paramFileDescriptor);
    if (this.mBackupWriter == 0L) {
      throw new RuntimeException("Native initialization failed with fd=" + paramFileDescriptor);
    }
  }
  
  private static native long ctor(FileDescriptor paramFileDescriptor);
  
  private static native void dtor(long paramLong);
  
  private static native void setKeyPrefix_native(long paramLong, String paramString);
  
  private static native int writeEntityData_native(long paramLong, byte[] paramArrayOfByte, int paramInt);
  
  private static native int writeEntityHeader_native(long paramLong, String paramString, int paramInt);
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      dtor(this.mBackupWriter);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void setKeyPrefix(String paramString)
  {
    setKeyPrefix_native(this.mBackupWriter, paramString);
  }
  
  public int writeEntityData(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    paramInt = writeEntityData_native(this.mBackupWriter, paramArrayOfByte, paramInt);
    if (paramInt >= 0) {
      return paramInt;
    }
    throw new IOException("result=0x" + Integer.toHexString(paramInt));
  }
  
  public int writeEntityHeader(String paramString, int paramInt)
    throws IOException
  {
    paramInt = writeEntityHeader_native(this.mBackupWriter, paramString, paramInt);
    if (paramInt >= 0) {
      return paramInt;
    }
    throw new IOException("result=0x" + Integer.toHexString(paramInt));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/BackupDataOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */