package android.os;

import android.system.ErrnoException;
import android.system.Os;
import android.system.StructStat;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.util.EmptyArray;

public class FileUtils
{
  private static final File[] EMPTY = new File[0];
  public static final int S_IRGRP = 32;
  public static final int S_IROTH = 4;
  public static final int S_IRUSR = 256;
  public static final int S_IRWXG = 56;
  public static final int S_IRWXO = 7;
  public static final int S_IRWXU = 448;
  public static final int S_IWGRP = 16;
  public static final int S_IWOTH = 2;
  public static final int S_IWUSR = 128;
  public static final int S_IXGRP = 8;
  public static final int S_IXOTH = 1;
  public static final int S_IXUSR = 64;
  private static final String TAG = "FileUtils";
  
  private static File buildFile(File paramFile, String paramString1, String paramString2)
  {
    if (TextUtils.isEmpty(paramString2)) {
      return new File(paramFile, paramString1);
    }
    return new File(paramFile, paramString1 + "." + paramString2);
  }
  
  public static File buildUniqueFile(File paramFile, String paramString)
    throws FileNotFoundException
  {
    int i = paramString.lastIndexOf('.');
    String str;
    if (i >= 0) {
      str = paramString.substring(0, i);
    }
    for (paramString = paramString.substring(i + 1);; paramString = null)
    {
      return buildUniqueFileWithExtension(paramFile, str, paramString);
      str = paramString;
    }
  }
  
  public static File buildUniqueFile(File paramFile, String paramString1, String paramString2)
    throws FileNotFoundException
  {
    paramString1 = splitFileName(paramString1, paramString2);
    return buildUniqueFileWithExtension(paramFile, paramString1[0], paramString1[1]);
  }
  
  private static File buildUniqueFileWithExtension(File paramFile, String paramString1, String paramString2)
    throws FileNotFoundException
  {
    File localFile = buildFile(paramFile, paramString1, paramString2);
    int j;
    for (int i = 0; localFile.exists(); i = j)
    {
      j = i + 1;
      if (i >= 32) {
        throw new FileNotFoundException("Failed to create unique file");
      }
      localFile = buildFile(paramFile, paramString1 + " (" + j + ")", paramString2);
    }
    return localFile;
  }
  
  public static String buildValidExtFilename(String paramString)
  {
    if ((TextUtils.isEmpty(paramString)) || (".".equals(paramString)) || ("..".equals(paramString))) {
      return "(invalid)";
    }
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    int i = 0;
    if (i < paramString.length())
    {
      char c = paramString.charAt(i);
      if (isValidExtFilenameChar(c)) {
        localStringBuilder.append(c);
      }
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append('_');
      }
    }
    trimFilename(localStringBuilder, 255);
    return localStringBuilder.toString();
  }
  
  public static String buildValidFatFilename(String paramString)
  {
    if ((TextUtils.isEmpty(paramString)) || (".".equals(paramString)) || ("..".equals(paramString))) {
      return "(invalid)";
    }
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    int i = 0;
    if (i < paramString.length())
    {
      char c = paramString.charAt(i);
      if (isValidFatFilenameChar(c)) {
        localStringBuilder.append(c);
      }
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append('_');
      }
    }
    trimFilename(localStringBuilder, 255);
    return localStringBuilder.toString();
  }
  
  /* Error */
  public static long checksumCrc32(File paramFile)
    throws FileNotFoundException, IOException
  {
    // Byte code:
    //   0: new 159	java/util/zip/CRC32
    //   3: dup
    //   4: invokespecial 160	java/util/zip/CRC32:<init>	()V
    //   7: astore 4
    //   9: aconst_null
    //   10: astore_3
    //   11: new 162	java/util/zip/CheckedInputStream
    //   14: dup
    //   15: new 164	java/io/FileInputStream
    //   18: dup
    //   19: aload_0
    //   20: invokespecial 167	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   23: aload 4
    //   25: invokespecial 170	java/util/zip/CheckedInputStream:<init>	(Ljava/io/InputStream;Ljava/util/zip/Checksum;)V
    //   28: astore_0
    //   29: sipush 128
    //   32: newarray <illegal type>
    //   34: astore_3
    //   35: aload_0
    //   36: aload_3
    //   37: invokevirtual 174	java/util/zip/CheckedInputStream:read	([B)I
    //   40: ifge -5 -> 35
    //   43: aload 4
    //   45: invokevirtual 178	java/util/zip/CRC32:getValue	()J
    //   48: lstore_1
    //   49: aload_0
    //   50: ifnull +7 -> 57
    //   53: aload_0
    //   54: invokevirtual 181	java/util/zip/CheckedInputStream:close	()V
    //   57: lload_1
    //   58: lreturn
    //   59: astore_0
    //   60: lload_1
    //   61: lreturn
    //   62: astore_0
    //   63: aload_3
    //   64: ifnull +7 -> 71
    //   67: aload_3
    //   68: invokevirtual 181	java/util/zip/CheckedInputStream:close	()V
    //   71: aload_0
    //   72: athrow
    //   73: astore_3
    //   74: goto -3 -> 71
    //   77: astore 4
    //   79: aload_0
    //   80: astore_3
    //   81: aload 4
    //   83: astore_0
    //   84: goto -21 -> 63
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	87	0	paramFile	File
    //   48	13	1	l	long
    //   10	58	3	arrayOfByte	byte[]
    //   73	1	3	localIOException	IOException
    //   80	1	3	localFile	File
    //   7	37	4	localCRC32	java.util.zip.CRC32
    //   77	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   53	57	59	java/io/IOException
    //   11	29	62	finally
    //   67	71	73	java/io/IOException
    //   29	35	77	finally
    //   35	49	77	finally
  }
  
  public static boolean contains(File paramFile1, File paramFile2)
  {
    if ((paramFile1 == null) || (paramFile2 == null)) {
      return false;
    }
    String str = paramFile1.getAbsolutePath();
    paramFile2 = paramFile2.getAbsolutePath();
    if (str.equals(paramFile2)) {
      return true;
    }
    paramFile1 = str;
    if (!str.endsWith("/")) {
      paramFile1 = str + "/";
    }
    return paramFile2.startsWith(paramFile1);
  }
  
  public static boolean contains(File[] paramArrayOfFile, File paramFile)
  {
    int j = paramArrayOfFile.length;
    int i = 0;
    while (i < j)
    {
      if (contains(paramArrayOfFile[i], paramFile)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  @Deprecated
  public static boolean copyFile(File paramFile1, File paramFile2)
  {
    try
    {
      copyFileOrThrow(paramFile1, paramFile2);
      return true;
    }
    catch (IOException paramFile1) {}
    return false;
  }
  
  /* Error */
  public static void copyFileOrThrow(File paramFile1, File paramFile2)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 5
    //   8: aconst_null
    //   9: astore_3
    //   10: new 164	java/io/FileInputStream
    //   13: dup
    //   14: aload_0
    //   15: invokespecial 167	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   18: astore_0
    //   19: aload_0
    //   20: aload_1
    //   21: invokestatic 211	android/os/FileUtils:copyToFileOrThrow	(Ljava/io/InputStream;Ljava/io/File;)V
    //   24: aload 4
    //   26: astore_1
    //   27: aload_0
    //   28: ifnull +10 -> 38
    //   31: aload_0
    //   32: invokevirtual 214	java/io/InputStream:close	()V
    //   35: aload 4
    //   37: astore_1
    //   38: aload_1
    //   39: ifnull +66 -> 105
    //   42: aload_1
    //   43: athrow
    //   44: astore_1
    //   45: goto -7 -> 38
    //   48: astore_1
    //   49: aload_3
    //   50: astore_0
    //   51: aload_1
    //   52: athrow
    //   53: astore_3
    //   54: aload_1
    //   55: astore_2
    //   56: aload_3
    //   57: astore_1
    //   58: aload_2
    //   59: astore_3
    //   60: aload_0
    //   61: ifnull +9 -> 70
    //   64: aload_0
    //   65: invokevirtual 214	java/io/InputStream:close	()V
    //   68: aload_2
    //   69: astore_3
    //   70: aload_3
    //   71: ifnull +32 -> 103
    //   74: aload_3
    //   75: athrow
    //   76: astore_0
    //   77: aload_2
    //   78: ifnonnull +8 -> 86
    //   81: aload_0
    //   82: astore_3
    //   83: goto -13 -> 70
    //   86: aload_2
    //   87: astore_3
    //   88: aload_2
    //   89: aload_0
    //   90: if_acmpeq -20 -> 70
    //   93: aload_2
    //   94: aload_0
    //   95: invokevirtual 218	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   98: aload_2
    //   99: astore_3
    //   100: goto -30 -> 70
    //   103: aload_1
    //   104: athrow
    //   105: return
    //   106: astore_1
    //   107: aload 5
    //   109: astore_0
    //   110: goto -52 -> 58
    //   113: astore_1
    //   114: goto -56 -> 58
    //   117: astore_1
    //   118: goto -67 -> 51
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	121	0	paramFile1	File
    //   0	121	1	paramFile2	File
    //   1	98	2	localFile1	File
    //   9	41	3	localObject1	Object
    //   53	4	3	localObject2	Object
    //   59	41	3	localFile2	File
    //   3	33	4	localObject3	Object
    //   6	102	5	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   31	35	44	java/lang/Throwable
    //   10	19	48	java/lang/Throwable
    //   51	53	53	finally
    //   64	68	76	java/lang/Throwable
    //   10	19	106	finally
    //   19	24	113	finally
    //   19	24	117	java/lang/Throwable
  }
  
  public static void copyPermissions(File paramFile1, File paramFile2)
    throws IOException
  {
    try
    {
      paramFile1 = Os.stat(paramFile1.getAbsolutePath());
      Os.chmod(paramFile2.getAbsolutePath(), paramFile1.st_mode);
      Os.chown(paramFile2.getAbsolutePath(), paramFile1.st_uid, paramFile1.st_gid);
      return;
    }
    catch (ErrnoException paramFile1)
    {
      throw paramFile1.rethrowAsIOException();
    }
  }
  
  @Deprecated
  public static boolean copyToFile(InputStream paramInputStream, File paramFile)
  {
    try
    {
      copyToFileOrThrow(paramInputStream, paramFile);
      return true;
    }
    catch (IOException paramInputStream) {}
    return false;
  }
  
  public static void copyToFileOrThrow(InputStream paramInputStream, File paramFile)
    throws IOException
  {
    if (paramFile.exists()) {
      paramFile.delete();
    }
    paramFile = new FileOutputStream(paramFile);
    try
    {
      byte[] arrayOfByte = new byte['က'];
      for (;;)
      {
        int i = paramInputStream.read(arrayOfByte);
        if (i < 0) {
          break;
        }
        paramFile.write(arrayOfByte, 0, i);
      }
      try
      {
        paramFile.getFD().sync();
        throw paramInputStream;
      }
      catch (IOException localIOException)
      {
        try
        {
          paramFile.getFD().sync();
          return;
          localIOException = localIOException;
        }
        catch (IOException paramInputStream)
        {
          for (;;) {}
        }
      }
    }
    finally
    {
      paramFile.flush();
    }
  }
  
  public static boolean deleteContents(File paramFile)
  {
    paramFile = paramFile.listFiles();
    boolean bool2 = true;
    boolean bool1 = true;
    if (paramFile != null)
    {
      int i = 0;
      int j = paramFile.length;
      for (;;)
      {
        bool2 = bool1;
        if (i >= j) {
          break;
        }
        File localFile = paramFile[i];
        bool2 = bool1;
        if (localFile.isDirectory()) {
          bool2 = bool1 & deleteContents(localFile);
        }
        bool1 = bool2;
        if (!localFile.delete())
        {
          Log.w("FileUtils", "Failed to delete " + localFile);
          bool1 = false;
        }
        i += 1;
      }
    }
    return bool2;
  }
  
  public static boolean deleteContentsAndDir(File paramFile)
  {
    if (deleteContents(paramFile)) {
      return paramFile.delete();
    }
    return false;
  }
  
  public static boolean deleteOlderFiles(File paramFile, int paramInt, long paramLong)
  {
    if ((paramInt < 0) || (paramLong < 0L)) {
      throw new IllegalArgumentException("Constraints must be positive or 0");
    }
    paramFile = paramFile.listFiles();
    if (paramFile == null) {
      return false;
    }
    Arrays.sort(paramFile, new Comparator()
    {
      public int compare(File paramAnonymousFile1, File paramAnonymousFile2)
      {
        return (int)(paramAnonymousFile2.lastModified() - paramAnonymousFile1.lastModified());
      }
    });
    boolean bool2;
    for (boolean bool1 = false; paramInt < paramFile.length; bool1 = bool2)
    {
      Object localObject = paramFile[paramInt];
      bool2 = bool1;
      if (System.currentTimeMillis() - ((File)localObject).lastModified() > paramLong)
      {
        bool2 = bool1;
        if (((File)localObject).delete())
        {
          Log.d("FileUtils", "Deleted old file " + localObject);
          bool2 = true;
        }
      }
      paramInt += 1;
    }
    return bool1;
  }
  
  public static int getUid(String paramString)
  {
    try
    {
      int i = Os.stat(paramString).st_uid;
      return i;
    }
    catch (ErrnoException paramString) {}
    return -1;
  }
  
  public static boolean isFilenameSafe(File paramFile)
  {
    return NoImagePreloadHolder.SAFE_FILENAME_PATTERN.matcher(paramFile.getPath()).matches();
  }
  
  public static boolean isValidExtFilename(String paramString)
  {
    if (paramString != null) {
      return paramString.equals(buildValidExtFilename(paramString));
    }
    return false;
  }
  
  private static boolean isValidExtFilenameChar(char paramChar)
  {
    switch (paramChar)
    {
    default: 
      return true;
    }
    return false;
  }
  
  public static boolean isValidFatFilename(String paramString)
  {
    if (paramString != null) {
      return paramString.equals(buildValidFatFilename(paramString));
    }
    return false;
  }
  
  private static boolean isValidFatFilenameChar(char paramChar)
  {
    if ((paramChar >= 0) && (paramChar <= '\037')) {
      return false;
    }
    switch (paramChar)
    {
    default: 
      return true;
    }
    return false;
  }
  
  public static File[] listFilesOrEmpty(File paramFile)
  {
    if (paramFile == null) {
      return EMPTY;
    }
    paramFile = paramFile.listFiles();
    if (paramFile != null) {
      return paramFile;
    }
    return EMPTY;
  }
  
  public static File[] listFilesOrEmpty(File paramFile, FilenameFilter paramFilenameFilter)
  {
    if (paramFile == null) {
      return EMPTY;
    }
    paramFile = paramFile.listFiles(paramFilenameFilter);
    if (paramFile != null) {
      return paramFile;
    }
    return EMPTY;
  }
  
  public static String[] listOrEmpty(File paramFile)
  {
    if (paramFile == null) {
      return EmptyArray.STRING;
    }
    paramFile = paramFile.list();
    if (paramFile != null) {
      return paramFile;
    }
    return EmptyArray.STRING;
  }
  
  public static File newFileOrNull(String paramString)
  {
    File localFile = null;
    if (paramString != null) {
      localFile = new File(paramString);
    }
    return localFile;
  }
  
  public static String readTextFile(File paramFile, int paramInt, String paramString)
    throws IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream);
    for (;;)
    {
      long l;
      try
      {
        l = paramFile.length();
        if (paramInt <= 0) {
          if ((l > 0L) && (paramInt == 0))
          {
            break label472;
            paramFile = new byte[i + 1];
            paramInt = localBufferedInputStream.read(paramFile);
            if (paramInt <= 0) {
              return "";
            }
            if (paramInt <= i)
            {
              paramFile = new String(paramFile, 0, paramInt);
              return paramFile;
            }
            if (paramString == null)
            {
              paramFile = new String(paramFile, 0, i);
              return paramFile;
            }
            paramFile = new String(paramFile, 0, i) + paramString;
            return paramFile;
          }
          else
          {
            if (paramInt < 0)
            {
              int j = 0;
              Object localObject2 = null;
              paramFile = null;
              i = j;
              if (localObject2 != null) {
                i = 1;
              }
              File localFile = paramFile;
              Object localObject1 = localObject2;
              if (localObject2 == null)
              {
                j = -paramInt;
                localObject1 = new byte[j];
              }
              int k = localBufferedInputStream.read((byte[])localObject1);
              paramFile = (File)localObject1;
              localObject2 = localFile;
              j = i;
              if (k == localObject1.length) {
                continue;
              }
              if ((localFile == null) && (k <= 0)) {
                return "";
              }
              if (localFile == null)
              {
                paramFile = new String((byte[])localObject1, 0, k);
                return paramFile;
              }
              if (k > 0)
              {
                i = 1;
                System.arraycopy(localFile, k, localFile, 0, localFile.length - k);
                System.arraycopy((byte[])localObject1, 0, localFile, localFile.length - k, k);
              }
              if ((paramString != null) && (i != 0))
              {
                paramFile = paramString + new String(localFile);
                return paramFile;
              }
              paramFile = new String(localFile);
              return paramFile;
            }
            paramFile = new ByteArrayOutputStream();
            paramString = new byte['Ѐ'];
            paramInt = localBufferedInputStream.read(paramString);
            if (paramInt > 0) {
              paramFile.write(paramString, 0, paramInt);
            }
            if (paramInt == paramString.length) {
              continue;
            }
            paramFile = paramFile.toString();
            return paramFile;
          }
        }
      }
      finally
      {
        localBufferedInputStream.close();
        localFileInputStream.close();
      }
      label472:
      int i = paramInt;
      if (l > 0L) {
        if (paramInt != 0)
        {
          i = paramInt;
          if (l >= paramInt) {}
        }
        else
        {
          i = (int)l;
        }
      }
    }
  }
  
  public static File rewriteAfterRename(File paramFile1, File paramFile2, File paramFile3)
  {
    if ((paramFile3 == null) || (paramFile1 == null)) {}
    while (paramFile2 == null) {
      return null;
    }
    if (contains(paramFile1, paramFile3)) {
      return new File(paramFile2, paramFile3.getAbsolutePath().substring(paramFile1.getAbsolutePath().length()));
    }
    return null;
  }
  
  public static String rewriteAfterRename(File paramFile1, File paramFile2, String paramString)
  {
    Object localObject = null;
    if (paramString == null) {
      return null;
    }
    paramFile2 = rewriteAfterRename(paramFile1, paramFile2, new File(paramString));
    paramFile1 = (File)localObject;
    if (paramFile2 != null) {
      paramFile1 = paramFile2.getAbsolutePath();
    }
    return paramFile1;
  }
  
  public static String[] rewriteAfterRename(File paramFile1, File paramFile2, String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {
      return null;
    }
    String[] arrayOfString = new String[paramArrayOfString.length];
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      arrayOfString[i] = rewriteAfterRename(paramFile1, paramFile2, paramArrayOfString[i]);
      i += 1;
    }
    return arrayOfString;
  }
  
  public static int setPermissions(File paramFile, int paramInt1, int paramInt2, int paramInt3)
  {
    return setPermissions(paramFile.getAbsolutePath(), paramInt1, paramInt2, paramInt3);
  }
  
  /* Error */
  public static int setPermissions(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: invokestatic 419	android/system/Os:fchmod	(Ljava/io/FileDescriptor;I)V
    //   5: iload_2
    //   6: ifge +7 -> 13
    //   9: iload_3
    //   10: iflt +9 -> 19
    //   13: aload_0
    //   14: iload_2
    //   15: iload_3
    //   16: invokestatic 423	android/system/Os:fchown	(Ljava/io/FileDescriptor;II)V
    //   19: iconst_0
    //   20: ireturn
    //   21: astore_0
    //   22: ldc 40
    //   24: new 63	java/lang/StringBuilder
    //   27: dup
    //   28: invokespecial 64	java/lang/StringBuilder:<init>	()V
    //   31: ldc_w 425
    //   34: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: aload_0
    //   38: invokevirtual 292	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   41: invokevirtual 74	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   44: invokestatic 428	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   47: pop
    //   48: aload_0
    //   49: getfield 431	android/system/ErrnoException:errno	I
    //   52: ireturn
    //   53: astore_0
    //   54: ldc 40
    //   56: new 63	java/lang/StringBuilder
    //   59: dup
    //   60: invokespecial 64	java/lang/StringBuilder:<init>	()V
    //   63: ldc_w 433
    //   66: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: aload_0
    //   70: invokevirtual 292	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   73: invokevirtual 74	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   76: invokestatic 428	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   79: pop
    //   80: aload_0
    //   81: getfield 431	android/system/ErrnoException:errno	I
    //   84: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	85	0	paramFileDescriptor	FileDescriptor
    //   0	85	1	paramInt1	int
    //   0	85	2	paramInt2	int
    //   0	85	3	paramInt3	int
    // Exception table:
    //   from	to	target	type
    //   0	5	21	android/system/ErrnoException
    //   13	19	53	android/system/ErrnoException
  }
  
  /* Error */
  public static int setPermissions(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: invokestatic 236	android/system/Os:chmod	(Ljava/lang/String;I)V
    //   5: iload_2
    //   6: ifge +7 -> 13
    //   9: iload_3
    //   10: iflt +9 -> 19
    //   13: aload_0
    //   14: iload_2
    //   15: iload_3
    //   16: invokestatic 246	android/system/Os:chown	(Ljava/lang/String;II)V
    //   19: iconst_0
    //   20: ireturn
    //   21: astore 4
    //   23: ldc 40
    //   25: new 63	java/lang/StringBuilder
    //   28: dup
    //   29: invokespecial 64	java/lang/StringBuilder:<init>	()V
    //   32: ldc_w 435
    //   35: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: aload_0
    //   39: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: ldc_w 437
    //   45: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: aload 4
    //   50: invokevirtual 292	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   53: invokevirtual 74	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   56: invokestatic 428	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   59: pop
    //   60: aload 4
    //   62: getfield 431	android/system/ErrnoException:errno	I
    //   65: ireturn
    //   66: astore 4
    //   68: ldc 40
    //   70: new 63	java/lang/StringBuilder
    //   73: dup
    //   74: invokespecial 64	java/lang/StringBuilder:<init>	()V
    //   77: ldc_w 439
    //   80: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: aload_0
    //   84: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   87: ldc_w 437
    //   90: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   93: aload 4
    //   95: invokevirtual 292	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   98: invokevirtual 74	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   101: invokestatic 428	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   104: pop
    //   105: aload 4
    //   107: getfield 431	android/system/ErrnoException:errno	I
    //   110: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	111	0	paramString	String
    //   0	111	1	paramInt1	int
    //   0	111	2	paramInt2	int
    //   0	111	3	paramInt3	int
    //   21	40	4	localErrnoException1	ErrnoException
    //   66	40	4	localErrnoException2	ErrnoException
    // Exception table:
    //   from	to	target	type
    //   0	5	21	android/system/ErrnoException
    //   13	19	66	android/system/ErrnoException
  }
  
  public static String[] splitFileName(String paramString1, String paramString2)
  {
    Object localObject2;
    if ("vnd.android.document/directory".equals(paramString1))
    {
      localObject1 = null;
      localObject2 = paramString2;
      paramString1 = (String)localObject1;
      if (localObject1 == null) {
        paramString1 = "";
      }
      return new String[] { localObject2, paramString1 };
    }
    int i = paramString2.lastIndexOf('.');
    String str2;
    String str1;
    if (i >= 0)
    {
      str2 = paramString2.substring(0, i);
      str1 = paramString2.substring(i + 1);
    }
    for (Object localObject1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str1.toLowerCase());; localObject1 = null)
    {
      Object localObject3 = localObject1;
      if (localObject1 == null) {
        localObject3 = "application/octet-stream";
      }
      String str3 = MimeTypeMap.getSingleton().getExtensionFromMimeType(paramString1);
      localObject1 = str1;
      localObject2 = str2;
      if (Objects.equals(paramString1, localObject3)) {
        break;
      }
      localObject1 = str1;
      localObject2 = str2;
      if (Objects.equals(str1, str3)) {
        break;
      }
      localObject1 = str3;
      localObject2 = paramString2;
      break;
      str2 = paramString2;
      str1 = null;
    }
  }
  
  public static void stringToFile(File paramFile, String paramString)
    throws IOException
  {
    stringToFile(paramFile.getAbsolutePath(), paramString);
  }
  
  public static void stringToFile(String paramString1, String paramString2)
    throws IOException
  {
    paramString1 = new FileWriter(paramString1);
    try
    {
      paramString1.write(paramString2);
      return;
    }
    finally
    {
      paramString1.close();
    }
  }
  
  public static boolean sync(FileOutputStream paramFileOutputStream)
  {
    if (paramFileOutputStream != null) {}
    try
    {
      paramFileOutputStream.getFD().sync();
      return true;
    }
    catch (IOException paramFileOutputStream) {}
    return false;
  }
  
  public static String trimFilename(String paramString, int paramInt)
  {
    paramString = new StringBuilder(paramString);
    trimFilename(paramString, paramInt);
    return paramString.toString();
  }
  
  private static void trimFilename(StringBuilder paramStringBuilder, int paramInt)
  {
    byte[] arrayOfByte = paramStringBuilder.toString().getBytes(StandardCharsets.UTF_8);
    if (arrayOfByte.length > paramInt)
    {
      while (arrayOfByte.length > paramInt - 3)
      {
        paramStringBuilder.deleteCharAt(paramStringBuilder.length() / 2);
        arrayOfByte = paramStringBuilder.toString().getBytes(StandardCharsets.UTF_8);
      }
      paramStringBuilder.insert(paramStringBuilder.length() / 2, "...");
    }
  }
  
  private static class NoImagePreloadHolder
  {
    public static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/FileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */