package android.app.backup;

import android.os.ParcelFileDescriptor;

public class FullBackupDataOutput
{
  private final BackupDataOutput mData;
  private long mSize;
  
  public FullBackupDataOutput()
  {
    this.mData = null;
    this.mSize = 0L;
  }
  
  public FullBackupDataOutput(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    this.mData = new BackupDataOutput(paramParcelFileDescriptor.getFileDescriptor());
  }
  
  public void addSize(long paramLong)
  {
    if (paramLong > 0L) {
      this.mSize += paramLong;
    }
  }
  
  public BackupDataOutput getData()
  {
    return this.mData;
  }
  
  public long getSize()
  {
    return this.mSize;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/FullBackupDataOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */