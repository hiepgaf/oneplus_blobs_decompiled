package com.amap.api.mapcore2d;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public final class dx
{
  public static final Charset a = Charset.forName("US-ASCII");
  static final Charset b = Charset.forName("UTF-8");
  
  static void a(Closeable paramCloseable)
  {
    if (paramCloseable == null) {
      return;
    }
    try
    {
      paramCloseable.close();
      return;
    }
    catch (RuntimeException paramCloseable)
    {
      throw paramCloseable;
    }
    catch (Exception paramCloseable) {}
  }
  
  static void a(File paramFile)
    throws IOException
  {
    int i = 0;
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile != null)
    {
      int j = arrayOfFile.length;
      if (i < j) {}
    }
    else
    {
      throw new IOException("not a readable directory: " + paramFile);
    }
    paramFile = arrayOfFile[i];
    if (!paramFile.isDirectory()) {}
    for (;;)
    {
      if (!paramFile.delete()) {
        break label79;
      }
      i += 1;
      break;
      a(paramFile);
    }
    label79:
    throw new IOException("failed to delete file: " + paramFile);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/dx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */