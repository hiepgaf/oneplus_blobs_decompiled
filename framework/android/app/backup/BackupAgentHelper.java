package android.app.backup;

import android.os.ParcelFileDescriptor;
import java.io.IOException;

public class BackupAgentHelper
  extends BackupAgent
{
  static final String TAG = "BackupAgentHelper";
  BackupHelperDispatcher mDispatcher = new BackupHelperDispatcher();
  
  public void addHelper(String paramString, BackupHelper paramBackupHelper)
  {
    this.mDispatcher.addHelper(paramString, paramBackupHelper);
  }
  
  public BackupHelperDispatcher getDispatcher()
  {
    return this.mDispatcher;
  }
  
  public void onBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2)
    throws IOException
  {
    this.mDispatcher.performBackup(paramParcelFileDescriptor1, paramBackupDataOutput, paramParcelFileDescriptor2);
  }
  
  public void onRestore(BackupDataInput paramBackupDataInput, int paramInt, ParcelFileDescriptor paramParcelFileDescriptor)
    throws IOException
  {
    this.mDispatcher.performRestore(paramBackupDataInput, paramInt, paramParcelFileDescriptor);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/BackupAgentHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */