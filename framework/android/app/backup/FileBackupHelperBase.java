package android.app.backup;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;

class FileBackupHelperBase
{
  private static final String TAG = "FileBackupHelperBase";
  Context mContext;
  boolean mExceptionLogged;
  long mPtr = ctor();
  
  FileBackupHelperBase(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private static native long ctor();
  
  private static native void dtor(long paramLong);
  
  static void performBackup_checked(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    if (paramArrayOfString1.length == 0) {
      return;
    }
    int j = paramArrayOfString1.length;
    int i = 0;
    while (i < j)
    {
      String str = paramArrayOfString1[i];
      if (str.charAt(0) != '/') {
        throw new RuntimeException("files must have all absolute paths: " + str);
      }
      i += 1;
    }
    if (paramArrayOfString1.length != paramArrayOfString2.length) {
      throw new RuntimeException("files.length=" + paramArrayOfString1.length + " keys.length=" + paramArrayOfString2.length);
    }
    if (paramParcelFileDescriptor1 != null) {}
    for (paramParcelFileDescriptor1 = paramParcelFileDescriptor1.getFileDescriptor();; paramParcelFileDescriptor1 = null)
    {
      paramParcelFileDescriptor2 = paramParcelFileDescriptor2.getFileDescriptor();
      if (paramParcelFileDescriptor2 != null) {
        break;
      }
      throw new NullPointerException();
    }
    i = performBackup_native(paramParcelFileDescriptor1, paramBackupDataOutput.mBackupWriter, paramParcelFileDescriptor2, paramArrayOfString1, paramArrayOfString2);
    if (i != 0) {
      throw new RuntimeException("Backup failed 0x" + Integer.toHexString(i));
    }
  }
  
  private static native int performBackup_native(FileDescriptor paramFileDescriptor1, long paramLong, FileDescriptor paramFileDescriptor2, String[] paramArrayOfString1, String[] paramArrayOfString2);
  
  private static native int writeFile_native(long paramLong1, String paramString, long paramLong2);
  
  private static native int writeSnapshot_native(long paramLong, FileDescriptor paramFileDescriptor);
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      dtor(this.mPtr);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  boolean isKeyInList(String paramString, String[] paramArrayOfString)
  {
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfString[i].equals(paramString)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  boolean writeFile(File paramFile, BackupDataInputStream paramBackupDataInputStream)
  {
    paramFile.getParentFile().mkdirs();
    int i = writeFile_native(this.mPtr, paramFile.getAbsolutePath(), paramBackupDataInputStream.mData.mBackupReader);
    if ((i != 0) && (!this.mExceptionLogged))
    {
      Log.e("FileBackupHelperBase", "Failed restoring file '" + paramFile + "' for app '" + this.mContext.getPackageName() + "' result=0x" + Integer.toHexString(i));
      this.mExceptionLogged = true;
    }
    return i == 0;
  }
  
  public void writeNewStateDescription(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    writeSnapshot_native(this.mPtr, paramParcelFileDescriptor.getFileDescriptor());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/FileBackupHelperBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */