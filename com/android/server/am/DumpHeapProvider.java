package com.android.server.am;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.FileNotFoundException;

public class DumpHeapProvider
  extends ContentProvider
{
  static File sHeapDumpJavaFile;
  static final Object sLock = new Object();
  
  public static File getJavaFile()
  {
    synchronized (sLock)
    {
      File localFile = sHeapDumpJavaFile;
      return localFile;
    }
  }
  
  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    return 0;
  }
  
  public String getType(Uri paramUri)
  {
    return "application/octet-stream";
  }
  
  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    return null;
  }
  
  public boolean onCreate()
  {
    synchronized (sLock)
    {
      File localFile = new File(new File(Environment.getDataDirectory(), "system"), "heapdump");
      localFile.mkdir();
      sHeapDumpJavaFile = new File(localFile, "javaheap.bin");
      return true;
    }
  }
  
  public ParcelFileDescriptor openFile(Uri paramUri, String arg2)
    throws FileNotFoundException
  {
    synchronized (sLock)
    {
      if (Uri.decode(paramUri.getEncodedPath()).equals("/java"))
      {
        paramUri = ParcelFileDescriptor.open(sHeapDumpJavaFile, 268435456);
        return paramUri;
      }
      throw new FileNotFoundException("Invalid path for " + paramUri);
    }
  }
  
  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    return null;
  }
  
  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    return 0;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/DumpHeapProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */