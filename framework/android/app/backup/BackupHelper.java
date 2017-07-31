package android.app.backup;

import android.os.ParcelFileDescriptor;

public abstract interface BackupHelper
{
  public abstract void performBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2);
  
  public abstract void restoreEntity(BackupDataInputStream paramBackupDataInputStream);
  
  public abstract void writeNewStateDescription(ParcelFileDescriptor paramParcelFileDescriptor);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/BackupHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */