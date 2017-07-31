package android.support.v4.provider;

import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class RawDocumentFile
  extends DocumentFile
{
  private File mFile;
  
  RawDocumentFile(DocumentFile paramDocumentFile, File paramFile)
  {
    super(paramDocumentFile);
    this.mFile = paramFile;
  }
  
  private static boolean deleteContents(File paramFile)
  {
    paramFile = paramFile.listFiles();
    boolean bool1 = true;
    boolean bool2 = true;
    if (paramFile == null) {}
    int j;
    int i;
    do
    {
      return bool2;
      j = paramFile.length;
      i = 0;
      bool2 = bool1;
    } while (i >= j);
    File localFile = paramFile[i];
    if (!localFile.isDirectory()) {
      label43:
      if (!localFile.delete()) {
        break label69;
      }
    }
    for (;;)
    {
      i += 1;
      break;
      bool1 &= deleteContents(localFile);
      break label43;
      label69:
      Log.w("DocumentFile", "Failed to delete " + localFile);
      bool1 = false;
    }
  }
  
  private static String getTypeForName(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    if (i < 0) {}
    do
    {
      return "application/octet-stream";
      paramString = paramString.substring(i + 1).toLowerCase();
      paramString = MimeTypeMap.getSingleton().getMimeTypeFromExtension(paramString);
    } while (paramString == null);
    return paramString;
  }
  
  public boolean canRead()
  {
    return this.mFile.canRead();
  }
  
  public boolean canWrite()
  {
    return this.mFile.canWrite();
  }
  
  public DocumentFile createDirectory(String paramString)
  {
    paramString = new File(this.mFile, paramString);
    if (paramString.isDirectory()) {}
    while (paramString.mkdir()) {
      return new RawDocumentFile(this, paramString);
    }
    return null;
  }
  
  public DocumentFile createFile(String paramString1, String paramString2)
  {
    paramString1 = MimeTypeMap.getSingleton().getExtensionFromMimeType(paramString1);
    if (paramString1 == null) {}
    for (;;)
    {
      paramString1 = new File(this.mFile, paramString2);
      try
      {
        paramString1.createNewFile();
        paramString1 = new RawDocumentFile(this, paramString1);
        return paramString1;
      }
      catch (IOException paramString1)
      {
        Log.w("DocumentFile", "Failed to createFile: " + paramString1);
      }
      paramString2 = paramString2 + "." + paramString1;
    }
    return null;
  }
  
  public boolean delete()
  {
    deleteContents(this.mFile);
    return this.mFile.delete();
  }
  
  public boolean exists()
  {
    return this.mFile.exists();
  }
  
  public String getName()
  {
    return this.mFile.getName();
  }
  
  public String getType()
  {
    if (!this.mFile.isDirectory()) {
      return getTypeForName(this.mFile.getName());
    }
    return null;
  }
  
  public Uri getUri()
  {
    return Uri.fromFile(this.mFile);
  }
  
  public boolean isDirectory()
  {
    return this.mFile.isDirectory();
  }
  
  public boolean isFile()
  {
    return this.mFile.isFile();
  }
  
  public long lastModified()
  {
    return this.mFile.lastModified();
  }
  
  public long length()
  {
    return this.mFile.length();
  }
  
  public DocumentFile[] listFiles()
  {
    ArrayList localArrayList = new ArrayList();
    File[] arrayOfFile = this.mFile.listFiles();
    if (arrayOfFile == null) {}
    for (;;)
    {
      return (DocumentFile[])localArrayList.toArray(new DocumentFile[localArrayList.size()]);
      int j = arrayOfFile.length;
      int i = 0;
      while (i < j)
      {
        localArrayList.add(new RawDocumentFile(this, arrayOfFile[i]));
        i += 1;
      }
    }
  }
  
  public boolean renameTo(String paramString)
  {
    paramString = new File(this.mFile.getParentFile(), paramString);
    if (!this.mFile.renameTo(paramString)) {
      return false;
    }
    this.mFile = paramString;
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/provider/RawDocumentFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */