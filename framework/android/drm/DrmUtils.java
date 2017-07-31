package android.drm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DrmUtils
{
  public static ExtendedMetadataParser getExtendedMetadataParser(byte[] paramArrayOfByte)
  {
    return new ExtendedMetadataParser(paramArrayOfByte, null);
  }
  
  private static void quietlyDispose(InputStream paramInputStream)
  {
    if (paramInputStream != null) {}
    try
    {
      paramInputStream.close();
      return;
    }
    catch (IOException paramInputStream) {}
  }
  
  private static void quietlyDispose(OutputStream paramOutputStream)
  {
    if (paramOutputStream != null) {}
    try
    {
      paramOutputStream.close();
      return;
    }
    catch (IOException paramOutputStream) {}
  }
  
  static byte[] readBytes(File paramFile)
    throws IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream);
    paramFile = null;
    try
    {
      int i = localBufferedInputStream.available();
      if (i > 0)
      {
        paramFile = new byte[i];
        localBufferedInputStream.read(paramFile);
      }
      return paramFile;
    }
    finally
    {
      quietlyDispose(localBufferedInputStream);
      quietlyDispose(localFileInputStream);
    }
  }
  
  static byte[] readBytes(String paramString)
    throws IOException
  {
    return readBytes(new File(paramString));
  }
  
  static void removeFile(String paramString)
    throws IOException
  {
    new File(paramString).delete();
  }
  
  /* Error */
  static void writeToFile(String paramString, byte[] paramArrayOfByte)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aload_0
    //   3: ifnull +25 -> 28
    //   6: aload_1
    //   7: ifnull +21 -> 28
    //   10: new 71	java/io/FileOutputStream
    //   13: dup
    //   14: aload_0
    //   15: invokespecial 72	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   18: astore_0
    //   19: aload_0
    //   20: aload_1
    //   21: invokevirtual 76	java/io/FileOutputStream:write	([B)V
    //   24: aload_0
    //   25: invokestatic 78	android/drm/DrmUtils:quietlyDispose	(Ljava/io/OutputStream;)V
    //   28: return
    //   29: astore_1
    //   30: aload_2
    //   31: astore_0
    //   32: aload_0
    //   33: invokestatic 78	android/drm/DrmUtils:quietlyDispose	(Ljava/io/OutputStream;)V
    //   36: aload_1
    //   37: athrow
    //   38: astore_1
    //   39: goto -7 -> 32
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	42	0	paramString	String
    //   0	42	1	paramArrayOfByte	byte[]
    //   1	30	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	19	29	finally
    //   19	24	38	finally
  }
  
  public static class ExtendedMetadataParser
  {
    HashMap<String, String> mMap = new HashMap();
    
    private ExtendedMetadataParser(byte[] paramArrayOfByte)
    {
      int i = 0;
      while (i < paramArrayOfByte.length)
      {
        int j = readByte(paramArrayOfByte, i);
        int k = i + 1;
        i = readByte(paramArrayOfByte, k);
        k += 1;
        String str3 = readMultipleBytes(paramArrayOfByte, j, k);
        j = k + j;
        String str2 = readMultipleBytes(paramArrayOfByte, i, j);
        String str1 = str2;
        if (str2.equals(" ")) {
          str1 = "";
        }
        i = j + i;
        this.mMap.put(str3, str1);
      }
    }
    
    private int readByte(byte[] paramArrayOfByte, int paramInt)
    {
      return paramArrayOfByte[paramInt];
    }
    
    private String readMultipleBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      byte[] arrayOfByte = new byte[paramInt1];
      int j = paramInt2;
      int i = 0;
      while (j < paramInt2 + paramInt1)
      {
        arrayOfByte[i] = paramArrayOfByte[j];
        j += 1;
        i += 1;
      }
      return new String(arrayOfByte);
    }
    
    public String get(String paramString)
    {
      return (String)this.mMap.get(paramString);
    }
    
    public Iterator<String> iterator()
    {
      return this.mMap.values().iterator();
    }
    
    public Iterator<String> keyIterator()
    {
      return this.mMap.keySet().iterator();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/drm/DrmUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */