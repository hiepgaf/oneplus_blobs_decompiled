package android.support.v4.util;

import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AtomicFile
{
  private final File mBackupName;
  private final File mBaseName;
  
  public AtomicFile(File paramFile)
  {
    this.mBaseName = paramFile;
    this.mBackupName = new File(paramFile.getPath() + ".bak");
  }
  
  static boolean sync(FileOutputStream paramFileOutputStream)
  {
    if (paramFileOutputStream == null) {}
    for (;;)
    {
      return true;
      try
      {
        paramFileOutputStream.getFD().sync();
      }
      catch (IOException paramFileOutputStream) {}
    }
    return false;
  }
  
  public void delete()
  {
    this.mBaseName.delete();
    this.mBackupName.delete();
  }
  
  public void failWrite(FileOutputStream paramFileOutputStream)
  {
    if (paramFileOutputStream == null) {
      return;
    }
    sync(paramFileOutputStream);
    try
    {
      paramFileOutputStream.close();
      this.mBaseName.delete();
      this.mBackupName.renameTo(this.mBaseName);
      return;
    }
    catch (IOException paramFileOutputStream)
    {
      Log.w("AtomicFile", "failWrite: Got exception:", paramFileOutputStream);
    }
  }
  
  public void finishWrite(FileOutputStream paramFileOutputStream)
  {
    if (paramFileOutputStream == null) {
      return;
    }
    sync(paramFileOutputStream);
    try
    {
      paramFileOutputStream.close();
      this.mBackupName.delete();
      return;
    }
    catch (IOException paramFileOutputStream)
    {
      Log.w("AtomicFile", "finishWrite: Got exception:", paramFileOutputStream);
    }
  }
  
  public File getBaseFile()
  {
    return this.mBaseName;
  }
  
  public FileInputStream openRead()
    throws FileNotFoundException
  {
    if (!this.mBackupName.exists()) {}
    for (;;)
    {
      return new FileInputStream(this.mBaseName);
      this.mBaseName.delete();
      this.mBackupName.renameTo(this.mBaseName);
    }
  }
  
  public byte[] readFully()
    throws IOException
  {
    int i = 0;
    FileInputStream localFileInputStream = openRead();
    try
    {
      Object localObject1 = new byte[localFileInputStream.available()];
      for (;;)
      {
        int j = localFileInputStream.read((byte[])localObject1, i, localObject1.length - i);
        if (j <= 0) {
          break;
        }
        j = i + j;
        int k = localFileInputStream.available();
        i = j;
        if (k > localObject1.length - j)
        {
          byte[] arrayOfByte = new byte[k + j];
          System.arraycopy(localObject1, 0, arrayOfByte, 0, j);
          localObject1 = arrayOfByte;
          i = j;
        }
      }
      return (byte[])localObject1;
    }
    finally
    {
      localFileInputStream.close();
    }
  }
  
  public FileOutputStream startWrite()
    throws IOException
  {
    if (!this.mBaseName.exists()) {}
    for (;;)
    {
      try
      {
        FileOutputStream localFileOutputStream1 = new FileOutputStream(this.mBaseName);
        return localFileOutputStream1;
      }
      catch (FileNotFoundException localFileNotFoundException1)
      {
        if (!this.mBaseName.getParentFile().mkdir()) {
          continue;
        }
        try
        {
          FileOutputStream localFileOutputStream2 = new FileOutputStream(this.mBaseName);
          return localFileOutputStream2;
        }
        catch (FileNotFoundException localFileNotFoundException2)
        {
          throw new IOException("Couldn't create " + this.mBaseName);
        }
        throw new IOException("Couldn't create directory " + this.mBaseName);
      }
      if (this.mBackupName.exists()) {
        this.mBaseName.delete();
      } else if (!this.mBaseName.renameTo(this.mBackupName)) {
        Log.w("AtomicFile", "Couldn't rename file " + this.mBaseName + " to backup file " + this.mBackupName);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/AtomicFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */