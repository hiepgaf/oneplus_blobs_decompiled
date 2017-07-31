package com.android.server.pm;

import android.content.pm.PackageParser.Package;
import android.os.FileUtils;
import android.util.AtomicFile;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

class PackageUsage
  extends AbstractStatsBase<Map<String, PackageParser.Package>>
{
  private static final String USAGE_FILE_MAGIC = "PACKAGE_USAGE__VERSION_";
  private static final String USAGE_FILE_MAGIC_VERSION_1 = "PACKAGE_USAGE__VERSION_1";
  private boolean mIsHistoricalPackageUsageAvailable = true;
  
  PackageUsage()
  {
    super("package-usage.list", "PackageUsage_DiskWriter", true);
  }
  
  private long parseAsLong(String paramString)
    throws IOException
  {
    try
    {
      long l = Long.parseLong(paramString);
      return l;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new IOException("Failed to parse " + paramString + " as a long.", localNumberFormatException);
    }
  }
  
  private String readLine(InputStream paramInputStream, StringBuffer paramStringBuffer)
    throws IOException
  {
    return readToken(paramInputStream, paramStringBuffer, '\n');
  }
  
  private String readToken(InputStream paramInputStream, StringBuffer paramStringBuffer, char paramChar)
    throws IOException
  {
    paramStringBuffer.setLength(0);
    for (;;)
    {
      char c = paramInputStream.read();
      if (c == '￿')
      {
        if (paramStringBuffer.length() == 0) {
          return null;
        }
        throw new IOException("Unexpected EOF");
      }
      if (c == paramChar) {
        return paramStringBuffer.toString();
      }
      paramStringBuffer.append((char)c);
    }
  }
  
  private void readVersion0LP(Map<String, PackageParser.Package> paramMap, InputStream paramInputStream, StringBuffer paramStringBuffer, String paramString)
    throws IOException
  {
    if (paramString != null)
    {
      String[] arrayOfString = paramString.split(" ");
      if (arrayOfString.length != 2) {
        throw new IOException("Failed to parse " + paramString + " as package-timestamp pair.");
      }
      paramString = (PackageParser.Package)paramMap.get(arrayOfString[0]);
      if (paramString == null) {}
      for (;;)
      {
        paramString = readLine(paramInputStream, paramStringBuffer);
        break;
        long l = parseAsLong(arrayOfString[1]);
        int i = 0;
        while (i < 8)
        {
          paramString.mLastPackageUsageTimeInMills[i] = l;
          i += 1;
        }
      }
    }
  }
  
  private void readVersion1LP(Map<String, PackageParser.Package> paramMap, InputStream paramInputStream, StringBuffer paramStringBuffer)
    throws IOException
  {
    Object localObject;
    String[] arrayOfString;
    do
    {
      localObject = readLine(paramInputStream, paramStringBuffer);
      if (localObject == null) {
        break;
      }
      arrayOfString = ((String)localObject).split(" ");
      if (arrayOfString.length != 9) {
        throw new IOException("Failed to parse " + (String)localObject + " as a timestamp array.");
      }
      localObject = (PackageParser.Package)paramMap.get(arrayOfString[0]);
    } while (localObject == null);
    int i = 0;
    while (i < 8)
    {
      ((PackageParser.Package)localObject).mLastPackageUsageTimeInMills[i] = parseAsLong(arrayOfString[(i + 1)]);
      i += 1;
    }
  }
  
  boolean isHistoricalPackageUsageAvailable()
  {
    return this.mIsHistoricalPackageUsageAvailable;
  }
  
  /* Error */
  protected void readInternal(Map<String, PackageParser.Package> paramMap)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 135	com/android/server/pm/PackageUsage:getFile	()Landroid/util/AtomicFile;
    //   4: astore_3
    //   5: aconst_null
    //   6: astore 4
    //   8: aconst_null
    //   9: astore_2
    //   10: aconst_null
    //   11: astore 5
    //   13: new 137	java/io/BufferedInputStream
    //   16: dup
    //   17: aload_3
    //   18: invokevirtual 143	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   21: invokespecial 146	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   24: astore_3
    //   25: new 65	java/lang/StringBuffer
    //   28: dup
    //   29: invokespecial 147	java/lang/StringBuffer:<init>	()V
    //   32: astore_2
    //   33: aload_0
    //   34: aload_3
    //   35: aload_2
    //   36: invokespecial 109	com/android/server/pm/PackageUsage:readLine	(Ljava/io/InputStream;Ljava/lang/StringBuffer;)Ljava/lang/String;
    //   39: astore 4
    //   41: aload 4
    //   43: ifnonnull +8 -> 51
    //   46: aload_3
    //   47: invokestatic 153	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   50: return
    //   51: ldc 12
    //   53: aload 4
    //   55: invokevirtual 157	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   58: ifeq +28 -> 86
    //   61: aload_0
    //   62: aload_1
    //   63: aload_3
    //   64: aload_2
    //   65: invokespecial 159	com/android/server/pm/PackageUsage:readVersion1LP	(Ljava/util/Map;Ljava/io/InputStream;Ljava/lang/StringBuffer;)V
    //   68: goto -22 -> 46
    //   71: astore_1
    //   72: aload_3
    //   73: astore_1
    //   74: aload_1
    //   75: astore_2
    //   76: aload_0
    //   77: iconst_0
    //   78: putfield 25	com/android/server/pm/PackageUsage:mIsHistoricalPackageUsageAvailable	Z
    //   81: aload_1
    //   82: invokestatic 153	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   85: return
    //   86: aload_0
    //   87: aload_1
    //   88: aload_3
    //   89: aload_2
    //   90: aload 4
    //   92: invokespecial 161	com/android/server/pm/PackageUsage:readVersion0LP	(Ljava/util/Map;Ljava/io/InputStream;Ljava/lang/StringBuffer;Ljava/lang/String;)V
    //   95: goto -49 -> 46
    //   98: astore_2
    //   99: aload_3
    //   100: astore_1
    //   101: aload_2
    //   102: astore_3
    //   103: aload_1
    //   104: astore_2
    //   105: ldc -93
    //   107: ldc -91
    //   109: aload_3
    //   110: invokestatic 171	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   113: pop
    //   114: aload_1
    //   115: invokestatic 153	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   118: return
    //   119: astore_1
    //   120: aload_2
    //   121: invokestatic 153	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   124: aload_1
    //   125: athrow
    //   126: astore_1
    //   127: aload_3
    //   128: astore_2
    //   129: goto -9 -> 120
    //   132: astore_1
    //   133: aload 5
    //   135: astore_1
    //   136: goto -62 -> 74
    //   139: astore_3
    //   140: aload 4
    //   142: astore_1
    //   143: goto -40 -> 103
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	146	0	this	PackageUsage
    //   0	146	1	paramMap	Map<String, PackageParser.Package>
    //   9	81	2	localObject1	Object
    //   98	4	2	localIOException1	IOException
    //   104	25	2	localObject2	Object
    //   4	124	3	localObject3	Object
    //   139	1	3	localIOException2	IOException
    //   6	135	4	str	String
    //   11	123	5	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   25	41	71	java/io/FileNotFoundException
    //   51	68	71	java/io/FileNotFoundException
    //   86	95	71	java/io/FileNotFoundException
    //   25	41	98	java/io/IOException
    //   51	68	98	java/io/IOException
    //   86	95	98	java/io/IOException
    //   13	25	119	finally
    //   76	81	119	finally
    //   105	114	119	finally
    //   25	41	126	finally
    //   51	68	126	finally
    //   86	95	126	finally
    //   13	25	132	java/io/FileNotFoundException
    //   13	25	139	java/io/IOException
  }
  
  protected void writeInternal(Map<String, PackageParser.Package> paramMap)
  {
    AtomicFile localAtomicFile = getFile();
    Object localObject1 = null;
    FileOutputStream localFileOutputStream;
    BufferedOutputStream localBufferedOutputStream;
    try
    {
      localFileOutputStream = localAtomicFile.startWrite();
      localObject1 = localFileOutputStream;
      localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
      localObject1 = localFileOutputStream;
      FileUtils.setPermissions(localAtomicFile.getBaseFile().getPath(), 416, 1000, 1032);
      localObject1 = localFileOutputStream;
      StringBuilder localStringBuilder = new StringBuilder();
      localObject1 = localFileOutputStream;
      localStringBuilder.append("PACKAGE_USAGE__VERSION_1");
      localObject1 = localFileOutputStream;
      localStringBuilder.append('\n');
      localObject1 = localFileOutputStream;
      localBufferedOutputStream.write(localStringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
      localObject1 = localFileOutputStream;
      paramMap = paramMap.values().iterator();
      for (;;)
      {
        localObject1 = localFileOutputStream;
        if (!paramMap.hasNext()) {
          break;
        }
        localObject1 = localFileOutputStream;
        Object localObject2 = (PackageParser.Package)paramMap.next();
        localObject1 = localFileOutputStream;
        if (((PackageParser.Package)localObject2).getLatestPackageUseTimeInMills() != 0L)
        {
          localObject1 = localFileOutputStream;
          localStringBuilder.setLength(0);
          localObject1 = localFileOutputStream;
          localStringBuilder.append(((PackageParser.Package)localObject2).packageName);
          localObject1 = localFileOutputStream;
          localObject2 = ((PackageParser.Package)localObject2).mLastPackageUsageTimeInMills;
          int i = 0;
          localObject1 = localFileOutputStream;
          int j = localObject2.length;
          while (i < j)
          {
            long l = localObject2[i];
            localObject1 = localFileOutputStream;
            localStringBuilder.append(' ');
            localObject1 = localFileOutputStream;
            localStringBuilder.append(l);
            i += 1;
          }
          localObject1 = localFileOutputStream;
          localStringBuilder.append('\n');
          localObject1 = localFileOutputStream;
          localBufferedOutputStream.write(localStringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
        }
      }
      localObject1 = localFileOutputStream;
    }
    catch (IOException paramMap)
    {
      if (localObject1 != null) {
        localAtomicFile.failWrite((FileOutputStream)localObject1);
      }
      Log.e("PackageManager", "Failed to write package usage times", paramMap);
      return;
    }
    localBufferedOutputStream.flush();
    localObject1 = localFileOutputStream;
    localAtomicFile.finishWrite(localFileOutputStream);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageUsage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */