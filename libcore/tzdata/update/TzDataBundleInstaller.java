package libcore.tzdata.update;

import android.util.Slog;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public final class TzDataBundleInstaller
{
  static final String CURRENT_TZ_DATA_DIR_NAME = "current";
  static final String OLD_TZ_DATA_DIR_NAME = "old";
  static final String WORKING_DIR_NAME = "working";
  private final File installDir;
  private final String logTag;
  
  public TzDataBundleInstaller(String paramString, File paramFile)
  {
    this.logTag = paramString;
    this.installDir = paramFile;
  }
  
  private boolean checkBundleFilesExist(File paramFile)
    throws IOException
  {
    Slog.i(this.logTag, "Verifying bundle contents");
    return FileUtils.filesExist(paramFile, new String[] { "tzdata_version", "checksums", "tzdata", "icu/icu_tzdata.dat" });
  }
  
  private void deleteBestEffort(File paramFile)
  {
    if (paramFile.exists()) {}
    try
    {
      FileUtils.deleteRecursive(paramFile);
      return;
    }
    catch (IOException localIOException)
    {
      Slog.w(this.logTag, "Unable to delete " + paramFile, localIOException);
    }
  }
  
  private File unpackBundle(byte[] paramArrayOfByte, File paramFile)
    throws IOException
  {
    Slog.i(this.logTag, "Unpacking update content to: " + paramFile);
    new ConfigBundle(paramArrayOfByte).extractTo(paramFile);
    return paramFile;
  }
  
  private boolean verifySystemChecksums(File paramFile)
    throws IOException
  {
    Slog.i(this.logTag, "Verifying system file checksums");
    paramFile = FileUtils.readLines(new File(paramFile, "checksums")).iterator();
    while (paramFile.hasNext())
    {
      Object localObject = (String)paramFile.next();
      int i = ((String)localObject).indexOf(',');
      if ((i <= 0) || (i == ((String)localObject).length() - 1)) {
        throw new IOException("Bad checksum entry: " + (String)localObject);
      }
      long l1;
      try
      {
        l1 = Long.parseLong(((String)localObject).substring(0, i));
        localObject = new File(((String)localObject).substring(i + 1));
        if (!((File)localObject).exists())
        {
          Slog.i(this.logTag, "Failed checksum test for file: " + localObject + ": file not found");
          return false;
        }
      }
      catch (NumberFormatException paramFile)
      {
        throw new IOException("Invalid checksum value: " + (String)localObject);
      }
      long l2 = FileUtils.calculateChecksum((File)localObject);
      if (l2 != l1)
      {
        Slog.i(this.logTag, "Failed checksum test for file: " + localObject + ": required=" + l1 + ", actual=" + l2);
        return false;
      }
    }
    return true;
  }
  
  public boolean install(byte[] paramArrayOfByte)
    throws IOException
  {
    File localFile1 = new File(this.installDir, "old");
    if (localFile1.exists()) {
      FileUtils.deleteRecursive(localFile1);
    }
    File localFile2 = new File(this.installDir, "current");
    File localFile3 = new File(this.installDir, "working");
    Slog.i(this.logTag, "Applying time zone update");
    paramArrayOfByte = unpackBundle(paramArrayOfByte, localFile3);
    try
    {
      if (!checkBundleFilesExist(paramArrayOfByte))
      {
        Slog.i(this.logTag, "Update not applied: Bundle is missing files");
        return false;
      }
      if (verifySystemChecksums(paramArrayOfByte))
      {
        FileUtils.makeDirectoryWorldAccessible(paramArrayOfByte);
        if (localFile2.exists())
        {
          Slog.i(this.logTag, "Moving " + localFile2 + " to " + localFile1);
          FileUtils.rename(localFile2, localFile1);
        }
        Slog.i(this.logTag, "Moving " + paramArrayOfByte + " to " + localFile2);
        FileUtils.rename(paramArrayOfByte, localFile2);
        Slog.i(this.logTag, "Update applied: " + localFile2 + " successfully created");
        return true;
      }
      Slog.i(this.logTag, "Update not applied: System checksum did not match");
      return false;
    }
    finally
    {
      deleteBestEffort(localFile1);
      deleteBestEffort(paramArrayOfByte);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/libcore/tzdata/update/TzDataBundleInstaller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */