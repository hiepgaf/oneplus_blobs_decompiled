package android.app.backup;

public abstract class BackupObserver
{
  public void backupFinished(int paramInt) {}
  
  public void onResult(String paramString, int paramInt) {}
  
  public void onUpdate(String paramString, BackupProgress paramBackupProgress) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/BackupObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */