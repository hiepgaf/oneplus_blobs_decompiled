package libcore.tzdata.update;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public final class FileUtils
{
  /* Error */
  public static long calculateChecksum(File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore_3
    //   5: new 17	java/util/zip/CRC32
    //   8: dup
    //   9: invokespecial 18	java/util/zip/CRC32:<init>	()V
    //   12: astore 6
    //   14: aconst_null
    //   15: astore 5
    //   17: aconst_null
    //   18: astore_2
    //   19: new 20	java/io/FileInputStream
    //   22: dup
    //   23: aload_0
    //   24: invokespecial 23	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   27: astore_0
    //   28: sipush 8196
    //   31: newarray <illegal type>
    //   33: astore_2
    //   34: aload_0
    //   35: aload_2
    //   36: invokevirtual 27	java/io/FileInputStream:read	([B)I
    //   39: istore_1
    //   40: iload_1
    //   41: iconst_m1
    //   42: if_icmpeq +46 -> 88
    //   45: aload 6
    //   47: aload_2
    //   48: iconst_0
    //   49: iload_1
    //   50: invokevirtual 31	java/util/zip/CRC32:update	([BII)V
    //   53: goto -19 -> 34
    //   56: astore_2
    //   57: aload_2
    //   58: athrow
    //   59: astore 4
    //   61: aload_2
    //   62: astore_3
    //   63: aload 4
    //   65: astore_2
    //   66: aload_3
    //   67: astore 4
    //   69: aload_0
    //   70: ifnull +10 -> 80
    //   73: aload_0
    //   74: invokevirtual 34	java/io/FileInputStream:close	()V
    //   77: aload_3
    //   78: astore 4
    //   80: aload 4
    //   82: ifnull +60 -> 142
    //   85: aload 4
    //   87: athrow
    //   88: aload 4
    //   90: astore_2
    //   91: aload_0
    //   92: ifnull +10 -> 102
    //   95: aload_0
    //   96: invokevirtual 34	java/io/FileInputStream:close	()V
    //   99: aload 4
    //   101: astore_2
    //   102: aload_2
    //   103: ifnull +41 -> 144
    //   106: aload_2
    //   107: athrow
    //   108: astore_2
    //   109: goto -7 -> 102
    //   112: astore_0
    //   113: aload_3
    //   114: ifnonnull +9 -> 123
    //   117: aload_0
    //   118: astore 4
    //   120: goto -40 -> 80
    //   123: aload_3
    //   124: astore 4
    //   126: aload_3
    //   127: aload_0
    //   128: if_acmpeq -48 -> 80
    //   131: aload_3
    //   132: aload_0
    //   133: invokevirtual 38	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   136: aload_3
    //   137: astore 4
    //   139: goto -59 -> 80
    //   142: aload_2
    //   143: athrow
    //   144: aload 6
    //   146: invokevirtual 42	java/util/zip/CRC32:getValue	()J
    //   149: lreturn
    //   150: astore_2
    //   151: aload 5
    //   153: astore_0
    //   154: goto -88 -> 66
    //   157: astore_2
    //   158: goto -92 -> 66
    //   161: astore_3
    //   162: aload_2
    //   163: astore_0
    //   164: aload_3
    //   165: astore_2
    //   166: goto -109 -> 57
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	169	0	paramFile	File
    //   39	11	1	i	int
    //   18	30	2	arrayOfByte	byte[]
    //   56	6	2	localThrowable1	Throwable
    //   65	42	2	localObject1	Object
    //   108	35	2	localThrowable2	Throwable
    //   150	1	2	localObject2	Object
    //   157	6	2	localObject3	Object
    //   165	1	2	localThrowable3	Throwable
    //   4	133	3	localObject4	Object
    //   161	4	3	localThrowable4	Throwable
    //   1	1	4	localObject5	Object
    //   59	5	4	localObject6	Object
    //   67	71	4	localObject7	Object
    //   15	137	5	localObject8	Object
    //   12	133	6	localCRC32	java.util.zip.CRC32
    // Exception table:
    //   from	to	target	type
    //   28	34	56	java/lang/Throwable
    //   34	40	56	java/lang/Throwable
    //   45	53	56	java/lang/Throwable
    //   57	59	59	finally
    //   95	99	108	java/lang/Throwable
    //   73	77	112	java/lang/Throwable
    //   19	28	150	finally
    //   28	34	157	finally
    //   34	40	157	finally
    //   45	53	157	finally
    //   19	28	161	java/lang/Throwable
  }
  
  public static File createSubFile(File paramFile, String paramString)
    throws IOException
  {
    File localFile = new File(paramFile, paramString).getCanonicalFile();
    if (!localFile.getPath().startsWith(paramFile.getCanonicalPath())) {
      throw new IOException(paramString + " must exist beneath " + paramFile + ". Canonicalized subpath: " + localFile);
    }
    return localFile;
  }
  
  public static void deleteRecursive(File paramFile)
    throws IOException
  {
    int i = 0;
    if (paramFile.isDirectory())
    {
      Object localObject = paramFile.listFiles();
      int j = localObject.length;
      if (i < j)
      {
        File localFile = localObject[i];
        if ((!localFile.isDirectory()) || (isSymlink(localFile))) {
          doDelete(localFile);
        }
        for (;;)
        {
          i += 1;
          break;
          deleteRecursive(localFile);
        }
      }
      localObject = paramFile.list();
      if (localObject.length != 0) {
        throw new IOException("Unable to delete files: " + Arrays.toString((Object[])localObject));
      }
    }
    doDelete(paramFile);
  }
  
  public static void doDelete(File paramFile)
    throws IOException
  {
    if (!paramFile.delete()) {
      throw new IOException("Unable to delete: " + paramFile);
    }
  }
  
  public static void ensureDirectoriesExist(File paramFile, boolean paramBoolean)
    throws IOException
  {
    LinkedList localLinkedList = new LinkedList();
    Object localObject = paramFile;
    File localFile;
    do
    {
      localLinkedList.addFirst(localObject);
      localFile = ((File)localObject).getParentFile();
      localObject = localFile;
    } while (localFile != null);
    localObject = localLinkedList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      localFile = (File)((Iterator)localObject).next();
      if (!localFile.exists())
      {
        if (!localFile.mkdir()) {
          throw new IOException("Unable to create directory: " + paramFile);
        }
        if (paramBoolean) {
          makeDirectoryWorldAccessible(localFile);
        }
      }
      else if (!localFile.isDirectory())
      {
        throw new IOException(localFile + " exists but is not a directory");
      }
    }
  }
  
  public static void ensureFileDoesNotExist(File paramFile)
    throws IOException
  {
    if (paramFile.exists())
    {
      if (!paramFile.isFile()) {
        throw new IOException(paramFile + " is not a file");
      }
      doDelete(paramFile);
    }
  }
  
  public static boolean filesExist(File paramFile, String... paramVarArgs)
    throws IOException
  {
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      if (!new File(paramFile, paramVarArgs[i]).exists()) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public static boolean isSymlink(File paramFile)
    throws IOException
  {
    String str = paramFile.getName();
    str = new File(paramFile.getParentFile().getCanonicalFile(), str).getPath();
    return !paramFile.getCanonicalPath().equals(str);
  }
  
  public static void makeDirectoryWorldAccessible(File paramFile)
    throws IOException
  {
    if (!paramFile.isDirectory()) {
      throw new IOException(paramFile + " must be a directory");
    }
    makeWorldReadable(paramFile);
    if (!paramFile.setExecutable(true, false)) {
      throw new IOException("Unable to make " + paramFile + " world-executable");
    }
  }
  
  public static void makeWorldReadable(File paramFile)
    throws IOException
  {
    if (!paramFile.setReadable(true, false)) {
      throw new IOException("Unable to make " + paramFile + " world-readable");
    }
  }
  
  /* Error */
  public static java.util.List<String> readLines(File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore_2
    //   4: new 20	java/io/FileInputStream
    //   7: dup
    //   8: aload_0
    //   9: invokespecial 23	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   12: astore_0
    //   13: aconst_null
    //   14: astore 4
    //   16: aconst_null
    //   17: astore_1
    //   18: new 198	java/io/BufferedReader
    //   21: dup
    //   22: new 200	java/io/InputStreamReader
    //   25: dup
    //   26: aload_0
    //   27: getstatic 206	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   30: invokespecial 209	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
    //   33: invokespecial 212	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   36: astore_0
    //   37: new 214	java/util/ArrayList
    //   40: dup
    //   41: invokespecial 215	java/util/ArrayList:<init>	()V
    //   44: astore 4
    //   46: aload_0
    //   47: invokevirtual 218	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   50: astore_1
    //   51: aload_1
    //   52: ifnull +41 -> 93
    //   55: aload 4
    //   57: aload_1
    //   58: invokeinterface 223 2 0
    //   63: pop
    //   64: goto -18 -> 46
    //   67: astore_1
    //   68: aload_1
    //   69: athrow
    //   70: astore_3
    //   71: aload_1
    //   72: astore_2
    //   73: aload_3
    //   74: astore_1
    //   75: aload_2
    //   76: astore_3
    //   77: aload_0
    //   78: ifnull +9 -> 87
    //   81: aload_0
    //   82: invokevirtual 224	java/io/BufferedReader:close	()V
    //   85: aload_2
    //   86: astore_3
    //   87: aload_3
    //   88: ifnull +57 -> 145
    //   91: aload_3
    //   92: athrow
    //   93: aload_3
    //   94: astore_1
    //   95: aload_0
    //   96: ifnull +9 -> 105
    //   99: aload_0
    //   100: invokevirtual 224	java/io/BufferedReader:close	()V
    //   103: aload_3
    //   104: astore_1
    //   105: aload_1
    //   106: ifnull +9 -> 115
    //   109: aload_1
    //   110: athrow
    //   111: astore_1
    //   112: goto -7 -> 105
    //   115: aload 4
    //   117: areturn
    //   118: astore_0
    //   119: aload_2
    //   120: ifnonnull +8 -> 128
    //   123: aload_0
    //   124: astore_3
    //   125: goto -38 -> 87
    //   128: aload_2
    //   129: astore_3
    //   130: aload_2
    //   131: aload_0
    //   132: if_acmpeq -45 -> 87
    //   135: aload_2
    //   136: aload_0
    //   137: invokevirtual 38	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   140: aload_2
    //   141: astore_3
    //   142: goto -55 -> 87
    //   145: aload_1
    //   146: athrow
    //   147: astore_1
    //   148: aload 4
    //   150: astore_0
    //   151: goto -76 -> 75
    //   154: astore_1
    //   155: goto -80 -> 75
    //   158: astore_2
    //   159: aload_1
    //   160: astore_0
    //   161: aload_2
    //   162: astore_1
    //   163: goto -95 -> 68
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	166	0	paramFile	File
    //   17	41	1	str	String
    //   67	5	1	localThrowable1	Throwable
    //   74	36	1	localObject1	Object
    //   111	35	1	localThrowable2	Throwable
    //   147	1	1	localObject2	Object
    //   154	6	1	localObject3	Object
    //   162	1	1	localThrowable3	Throwable
    //   3	138	2	localObject4	Object
    //   158	4	2	localThrowable4	Throwable
    //   1	1	3	localObject5	Object
    //   70	4	3	localObject6	Object
    //   76	66	3	localObject7	Object
    //   14	135	4	localArrayList	java.util.ArrayList
    // Exception table:
    //   from	to	target	type
    //   37	46	67	java/lang/Throwable
    //   46	51	67	java/lang/Throwable
    //   55	64	67	java/lang/Throwable
    //   68	70	70	finally
    //   99	103	111	java/lang/Throwable
    //   81	85	118	java/lang/Throwable
    //   18	37	147	finally
    //   37	46	154	finally
    //   46	51	154	finally
    //   55	64	154	finally
    //   18	37	158	java/lang/Throwable
  }
  
  public static void rename(File paramFile1, File paramFile2)
    throws IOException
  {
    ensureFileDoesNotExist(paramFile2);
    if (!paramFile1.renameTo(paramFile2)) {
      throw new IOException("Unable to rename " + paramFile1 + " to " + paramFile2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/libcore/tzdata/update/FileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */