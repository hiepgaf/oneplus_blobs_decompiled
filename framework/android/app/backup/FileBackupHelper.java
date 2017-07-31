package android.app.backup;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import java.io.File;

public class FileBackupHelper
  extends FileBackupHelperBase
  implements BackupHelper
{
  private static final boolean DEBUG = false;
  private static final String TAG = "FileBackupHelper";
  Context mContext;
  String[] mFiles;
  File mFilesDir;
  
  public FileBackupHelper(Context paramContext, String... paramVarArgs)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mFilesDir = paramContext.getFilesDir();
    this.mFiles = paramVarArgs;
  }
  
  public void performBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2)
  {
    String[] arrayOfString1 = this.mFiles;
    File localFile = this.mContext.getFilesDir();
    int j = arrayOfString1.length;
    String[] arrayOfString2 = new String[j];
    int i = 0;
    while (i < j)
    {
      arrayOfString2[i] = new File(localFile, arrayOfString1[i]).getAbsolutePath();
      i += 1;
    }
    performBackup_checked(paramParcelFileDescriptor1, paramBackupDataOutput, paramParcelFileDescriptor2, arrayOfString2, arrayOfString1);
  }
  
  public void restoreEntity(BackupDataInputStream paramBackupDataInputStream)
  {
    String str = paramBackupDataInputStream.getKey();
    if (isKeyInList(str, this.mFiles)) {
      writeFile(new File(this.mFilesDir, str), paramBackupDataInputStream);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/FileBackupHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */