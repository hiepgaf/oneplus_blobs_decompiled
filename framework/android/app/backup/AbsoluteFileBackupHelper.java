package android.app.backup;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import java.io.File;

public class AbsoluteFileBackupHelper
  extends FileBackupHelperBase
  implements BackupHelper
{
  private static final boolean DEBUG = false;
  private static final String TAG = "AbsoluteFileBackupHelper";
  Context mContext;
  String[] mFiles;
  
  public AbsoluteFileBackupHelper(Context paramContext, String... paramVarArgs)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mFiles = paramVarArgs;
  }
  
  public void performBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2)
  {
    performBackup_checked(paramParcelFileDescriptor1, paramBackupDataOutput, paramParcelFileDescriptor2, this.mFiles, this.mFiles);
  }
  
  public void restoreEntity(BackupDataInputStream paramBackupDataInputStream)
  {
    String str = paramBackupDataInputStream.getKey();
    if (isKeyInList(str, this.mFiles)) {
      writeFile(new File(str), paramBackupDataInputStream);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/AbsoluteFileBackupHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */