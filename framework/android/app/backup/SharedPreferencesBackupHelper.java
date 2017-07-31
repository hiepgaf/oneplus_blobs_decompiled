package android.app.backup;

import android.app.QueuedWork;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import java.io.File;

public class SharedPreferencesBackupHelper
  extends FileBackupHelperBase
  implements BackupHelper
{
  private static final boolean DEBUG = false;
  private static final String TAG = "SharedPreferencesBackupHelper";
  private Context mContext;
  private String[] mPrefGroups;
  
  public SharedPreferencesBackupHelper(Context paramContext, String... paramVarArgs)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mPrefGroups = paramVarArgs;
  }
  
  public void performBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2)
  {
    Context localContext = this.mContext;
    QueuedWork.waitToFinish();
    String[] arrayOfString1 = this.mPrefGroups;
    int j = arrayOfString1.length;
    String[] arrayOfString2 = new String[j];
    int i = 0;
    while (i < j)
    {
      arrayOfString2[i] = localContext.getSharedPrefsFile(arrayOfString1[i]).getAbsolutePath();
      i += 1;
    }
    performBackup_checked(paramParcelFileDescriptor1, paramBackupDataOutput, paramParcelFileDescriptor2, arrayOfString2, arrayOfString1);
  }
  
  public void restoreEntity(BackupDataInputStream paramBackupDataInputStream)
  {
    Context localContext = this.mContext;
    String str = paramBackupDataInputStream.getKey();
    if (isKeyInList(str, this.mPrefGroups)) {
      writeFile(localContext.getSharedPrefsFile(str).getAbsoluteFile(), paramBackupDataInputStream);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/SharedPreferencesBackupHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */