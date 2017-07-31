package com.oneplus.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class FileUtils
{
  private static final Map<String, FileAccessInfo> FILE_ACCESS_TABLE = new HashMap();
  private static final boolean PRINT_FILE_LOCK_LOGS = false;
  private static final String TAG = "FileUtils";
  
  public static String findPeerFile(String paramString, List<String> paramList)
  {
    int i = paramString.lastIndexOf(".");
    if (i >= 0) {
      paramString = paramString.substring(0, i);
    }
    for (;;)
    {
      paramList = paramList.iterator();
      String str;
      File localFile;
      do
      {
        if (!paramList.hasNext()) {
          break;
        }
        str = (String)paramList.next();
        str = paramString + str;
        localFile = new File(str);
      } while ((!localFile.exists()) || (localFile.isDirectory()));
      return str;
    }
    return null;
  }
  
  private static File getFileAccessLockFile(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    String str = Path.getDirectoryPath(paramString);
    paramString = Path.getFileName(paramString);
    return new File(Path.combine(new String[] { str, "." + paramString + ".lock" }));
  }
  
  public static String getFileSizeDescription(long paramLong)
  {
    if (paramLong >= 1048576L) {
      return String.format(Locale.US, "%.2f MB", new Object[] { Double.valueOf(paramLong / 1048576.0D) });
    }
    if (paramLong >= 1024L) {
      return String.format(Locale.US, "%.2f KB", new Object[] { Double.valueOf(paramLong / 1024.0D) });
    }
    return Long.toString(paramLong) + " Bytes";
  }
  
  public static boolean isAnimationFilePath(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    return Path.getExtension(paramString).toLowerCase(Locale.US).equals(".gif");
  }
  
  public static boolean isImageFilePath(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    paramString = Path.getExtension(paramString).toLowerCase(Locale.US);
    if (paramString.equals(".jpg")) {}
    while ((paramString.equals(".jpeg")) || (paramString.equals(".png")) || (paramString.equals(".bmp")) || (paramString.equals(".wbmp")) || (paramString.equals(".gif")) || (paramString.equals(".dng"))) {
      return true;
    }
    return false;
  }
  
  public static boolean isRawFilePath(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    return Path.getExtension(paramString).toLowerCase(Locale.US).equals(".dng");
  }
  
  public static boolean isVideoFilePath(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    paramString = Path.getExtension(paramString).toLowerCase(Locale.US);
    if (paramString.equals(".mp4")) {}
    while ((paramString.equals(".mpg")) || (paramString.equals(".mpeg")) || (paramString.equals(".mov")) || (paramString.equals(".avi")) || (paramString.equals(".wmv")) || (paramString.equals(".3gp")) || (paramString.equals(".mkv"))) {
      return true;
    }
    return false;
  }
  
  /* Error */
  private static FileAccessInfo lockRead(String paramString, long paramLong)
    throws IOException
  {
    // Byte code:
    //   0: invokestatic 207	android/os/SystemClock:elapsedRealtime	()J
    //   3: lstore_3
    //   4: getstatic 42	com/oneplus/io/FileUtils:FILE_ACCESS_TABLE	Ljava/util/Map;
    //   7: astore 5
    //   9: aload 5
    //   11: monitorenter
    //   12: getstatic 42	com/oneplus/io/FileUtils:FILE_ACCESS_TABLE	Ljava/util/Map;
    //   15: aload_0
    //   16: invokeinterface 213 2 0
    //   21: checkcast 6	com/oneplus/io/FileUtils$FileAccessInfo
    //   24: astore 6
    //   26: aload 6
    //   28: ifnonnull +147 -> 175
    //   31: aload_0
    //   32: invokestatic 215	com/oneplus/io/FileUtils:getFileAccessLockFile	(Ljava/lang/String;)Ljava/io/File;
    //   35: astore 7
    //   37: new 217	java/io/RandomAccessFile
    //   40: dup
    //   41: aload 7
    //   43: invokevirtual 220	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   46: ldc -34
    //   48: invokespecial 225	java/io/RandomAccessFile:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   51: astore 6
    //   53: aload 6
    //   55: invokevirtual 229	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   58: astore 8
    //   60: aload 8
    //   62: invokevirtual 235	java/nio/channels/FileChannel:tryLock	()Ljava/nio/channels/FileLock;
    //   65: astore 8
    //   67: aload 8
    //   69: ifnull +50 -> 119
    //   72: new 6	com/oneplus/io/FileUtils$FileAccessInfo
    //   75: dup
    //   76: aload 7
    //   78: aload 6
    //   80: aload 8
    //   82: invokespecial 238	com/oneplus/io/FileUtils$FileAccessInfo:<init>	(Ljava/io/File;Ljava/io/RandomAccessFile;Ljava/nio/channels/FileLock;)V
    //   85: astore 7
    //   87: aload 7
    //   89: aload 7
    //   91: getfield 242	com/oneplus/io/FileUtils$FileAccessInfo:readerCount	I
    //   94: iconst_1
    //   95: iadd
    //   96: putfield 242	com/oneplus/io/FileUtils$FileAccessInfo:readerCount	I
    //   99: getstatic 42	com/oneplus/io/FileUtils:FILE_ACCESS_TABLE	Ljava/util/Map;
    //   102: aload_0
    //   103: aload 7
    //   105: invokeinterface 246 3 0
    //   110: pop
    //   111: aload 5
    //   113: monitorexit
    //   114: aload 7
    //   116: areturn
    //   117: astore 7
    //   119: aload 6
    //   121: invokevirtual 249	java/io/RandomAccessFile:close	()V
    //   124: aload 5
    //   126: monitorexit
    //   127: lload_1
    //   128: lconst_0
    //   129: lcmp
    //   130: ifle +77 -> 207
    //   133: invokestatic 207	android/os/SystemClock:elapsedRealtime	()J
    //   136: lload_3
    //   137: lsub
    //   138: lload_1
    //   139: lcmp
    //   140: iflt +67 -> 207
    //   143: new 199	java/io/IOException
    //   146: dup
    //   147: new 75	java/lang/StringBuilder
    //   150: dup
    //   151: invokespecial 76	java/lang/StringBuilder:<init>	()V
    //   154: ldc -5
    //   156: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: aload_0
    //   160: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   163: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   166: invokespecial 252	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   169: athrow
    //   170: astore 6
    //   172: goto -48 -> 124
    //   175: aload 6
    //   177: getfield 255	com/oneplus/io/FileUtils$FileAccessInfo:isWriting	Z
    //   180: ifne -56 -> 124
    //   183: aload 6
    //   185: aload 6
    //   187: getfield 242	com/oneplus/io/FileUtils$FileAccessInfo:readerCount	I
    //   190: iconst_1
    //   191: iadd
    //   192: putfield 242	com/oneplus/io/FileUtils$FileAccessInfo:readerCount	I
    //   195: aload 5
    //   197: monitorexit
    //   198: aload 6
    //   200: areturn
    //   201: astore_0
    //   202: aload 5
    //   204: monitorexit
    //   205: aload_0
    //   206: athrow
    //   207: ldc2_w 256
    //   210: invokestatic 263	java/lang/Thread:sleep	(J)V
    //   213: goto -209 -> 4
    //   216: astore 5
    //   218: goto -214 -> 4
    //   221: astore 7
    //   223: goto -104 -> 119
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	226	0	paramString	String
    //   0	226	1	paramLong	long
    //   3	134	3	l	long
    //   7	196	5	localMap	Map
    //   216	1	5	localThrowable1	Throwable
    //   24	96	6	localObject1	Object
    //   170	29	6	localThrowable2	Throwable
    //   35	80	7	localObject2	Object
    //   117	1	7	localThrowable3	Throwable
    //   221	1	7	localThrowable4	Throwable
    //   58	23	8	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   60	67	117	java/lang/Throwable
    //   72	87	117	java/lang/Throwable
    //   119	124	170	java/lang/Throwable
    //   12	26	201	finally
    //   31	60	201	finally
    //   60	67	201	finally
    //   72	87	201	finally
    //   87	111	201	finally
    //   119	124	201	finally
    //   175	195	201	finally
    //   207	213	216	java/lang/Throwable
    //   87	111	221	java/lang/Throwable
  }
  
  /* Error */
  private static FileAccessInfo lockWrite(String paramString, long paramLong)
    throws IOException
  {
    // Byte code:
    //   0: invokestatic 207	android/os/SystemClock:elapsedRealtime	()J
    //   3: lstore_3
    //   4: getstatic 42	com/oneplus/io/FileUtils:FILE_ACCESS_TABLE	Ljava/util/Map;
    //   7: astore 5
    //   9: aload 5
    //   11: monitorenter
    //   12: getstatic 42	com/oneplus/io/FileUtils:FILE_ACCESS_TABLE	Ljava/util/Map;
    //   15: aload_0
    //   16: invokeinterface 213 2 0
    //   21: checkcast 6	com/oneplus/io/FileUtils$FileAccessInfo
    //   24: ifnonnull +109 -> 133
    //   27: aload_0
    //   28: invokestatic 215	com/oneplus/io/FileUtils:getFileAccessLockFile	(Ljava/lang/String;)Ljava/io/File;
    //   31: astore 7
    //   33: new 217	java/io/RandomAccessFile
    //   36: dup
    //   37: aload 7
    //   39: invokevirtual 220	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   42: ldc -34
    //   44: invokespecial 225	java/io/RandomAccessFile:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   47: astore 6
    //   49: aload 6
    //   51: invokevirtual 229	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   54: astore 8
    //   56: aload 8
    //   58: invokevirtual 235	java/nio/channels/FileChannel:tryLock	()Ljava/nio/channels/FileLock;
    //   61: astore 9
    //   63: aload 9
    //   65: ifnull +63 -> 128
    //   68: aload 8
    //   70: lconst_0
    //   71: invokevirtual 269	java/nio/channels/FileChannel:truncate	(J)Ljava/nio/channels/FileChannel;
    //   74: pop
    //   75: new 6	com/oneplus/io/FileUtils$FileAccessInfo
    //   78: dup
    //   79: aload 7
    //   81: aload 6
    //   83: aload 9
    //   85: invokespecial 238	com/oneplus/io/FileUtils$FileAccessInfo:<init>	(Ljava/io/File;Ljava/io/RandomAccessFile;Ljava/nio/channels/FileLock;)V
    //   88: astore 7
    //   90: aload 7
    //   92: iconst_1
    //   93: putfield 255	com/oneplus/io/FileUtils$FileAccessInfo:isWriting	Z
    //   96: aload 7
    //   98: aload 7
    //   100: getfield 242	com/oneplus/io/FileUtils$FileAccessInfo:readerCount	I
    //   103: iconst_1
    //   104: iadd
    //   105: putfield 242	com/oneplus/io/FileUtils$FileAccessInfo:readerCount	I
    //   108: getstatic 42	com/oneplus/io/FileUtils:FILE_ACCESS_TABLE	Ljava/util/Map;
    //   111: aload_0
    //   112: aload 7
    //   114: invokeinterface 246 3 0
    //   119: pop
    //   120: aload 5
    //   122: monitorexit
    //   123: aload 7
    //   125: areturn
    //   126: astore 7
    //   128: aload 6
    //   130: invokevirtual 249	java/io/RandomAccessFile:close	()V
    //   133: aload 5
    //   135: monitorexit
    //   136: lload_1
    //   137: lconst_0
    //   138: lcmp
    //   139: ifle +51 -> 190
    //   142: invokestatic 207	android/os/SystemClock:elapsedRealtime	()J
    //   145: lload_3
    //   146: lsub
    //   147: lload_1
    //   148: lcmp
    //   149: iflt +41 -> 190
    //   152: new 199	java/io/IOException
    //   155: dup
    //   156: new 75	java/lang/StringBuilder
    //   159: dup
    //   160: invokespecial 76	java/lang/StringBuilder:<init>	()V
    //   163: ldc -5
    //   165: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   168: aload_0
    //   169: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   172: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   175: invokespecial 252	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   178: athrow
    //   179: astore 6
    //   181: goto -48 -> 133
    //   184: astore_0
    //   185: aload 5
    //   187: monitorexit
    //   188: aload_0
    //   189: athrow
    //   190: ldc2_w 256
    //   193: invokestatic 263	java/lang/Thread:sleep	(J)V
    //   196: goto -192 -> 4
    //   199: astore 5
    //   201: goto -197 -> 4
    //   204: astore 7
    //   206: goto -78 -> 128
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	209	0	paramString	String
    //   0	209	1	paramLong	long
    //   3	143	3	l	long
    //   7	179	5	localMap	Map
    //   199	1	5	localThrowable1	Throwable
    //   47	82	6	localRandomAccessFile	RandomAccessFile
    //   179	1	6	localThrowable2	Throwable
    //   31	93	7	localObject	Object
    //   126	1	7	localThrowable3	Throwable
    //   204	1	7	localThrowable4	Throwable
    //   54	15	8	localFileChannel	java.nio.channels.FileChannel
    //   61	23	9	localFileLock	FileLock
    // Exception table:
    //   from	to	target	type
    //   56	63	126	java/lang/Throwable
    //   68	90	126	java/lang/Throwable
    //   128	133	179	java/lang/Throwable
    //   12	56	184	finally
    //   56	63	184	finally
    //   68	90	184	finally
    //   90	120	184	finally
    //   128	133	184	finally
    //   190	196	199	java/lang/Throwable
    //   90	120	204	java/lang/Throwable
  }
  
  public static InputStream openLockedInputStream(String paramString, long paramLong)
    throws IOException
  {
    return openLockedInputStream(paramString, paramLong, 0);
  }
  
  public static InputStream openLockedInputStream(String paramString, long paramLong, int paramInt)
    throws IOException
  {
    lockRead(paramString, paramLong);
    try
    {
      LockedFileInputStream localLockedFileInputStream = new LockedFileInputStream(paramString);
      return localLockedFileInputStream;
    }
    catch (Throwable localThrowable)
    {
      unlockRead(paramString);
      throw localThrowable;
    }
  }
  
  public static OutputStream openLockedOutputStream(String paramString, long paramLong)
    throws IOException
  {
    return openLockedOutputStream(paramString, paramLong, 0);
  }
  
  public static OutputStream openLockedOutputStream(String paramString, long paramLong, int paramInt)
    throws IOException
  {
    lockWrite(paramString, paramLong);
    try
    {
      LockedFileOutputStream localLockedFileOutputStream = new LockedFileOutputStream(paramString);
      return localLockedFileOutputStream;
    }
    catch (Throwable localThrowable)
    {
      unlockWrite(paramString);
      throw localThrowable;
    }
  }
  
  public static void readFromFile(File paramFile, OutputStream paramOutputStream)
    throws IOException
  {
    readFromFile(paramFile.getAbsolutePath(), paramOutputStream);
  }
  
  /* Error */
  public static void readFromFile(String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 6
    //   8: aconst_null
    //   9: astore 5
    //   11: new 292	java/io/FileInputStream
    //   14: dup
    //   15: aload_0
    //   16: invokespecial 293	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   19: astore_0
    //   20: sipush 4096
    //   23: newarray <illegal type>
    //   25: astore 5
    //   27: aload_0
    //   28: aload 5
    //   30: invokevirtual 297	java/io/FileInputStream:read	([B)I
    //   33: istore_2
    //   34: iload_2
    //   35: ifle +21 -> 56
    //   38: aload_1
    //   39: aload 5
    //   41: iconst_0
    //   42: iload_2
    //   43: invokevirtual 303	java/io/OutputStream:write	([BII)V
    //   46: aload_0
    //   47: aload 5
    //   49: invokevirtual 297	java/io/FileInputStream:read	([B)I
    //   52: istore_2
    //   53: goto -19 -> 34
    //   56: aload 4
    //   58: astore_1
    //   59: aload_0
    //   60: ifnull +10 -> 70
    //   63: aload_0
    //   64: invokevirtual 304	java/io/FileInputStream:close	()V
    //   67: aload 4
    //   69: astore_1
    //   70: aload_1
    //   71: ifnull +76 -> 147
    //   74: aload_1
    //   75: athrow
    //   76: astore_1
    //   77: goto -7 -> 70
    //   80: astore_1
    //   81: aload 5
    //   83: astore_0
    //   84: aload_1
    //   85: athrow
    //   86: astore 4
    //   88: aload_1
    //   89: astore_3
    //   90: aload 4
    //   92: astore_1
    //   93: aload_3
    //   94: astore 4
    //   96: aload_0
    //   97: ifnull +10 -> 107
    //   100: aload_0
    //   101: invokevirtual 304	java/io/FileInputStream:close	()V
    //   104: aload_3
    //   105: astore 4
    //   107: aload 4
    //   109: ifnull +36 -> 145
    //   112: aload 4
    //   114: athrow
    //   115: astore_0
    //   116: aload_3
    //   117: ifnonnull +9 -> 126
    //   120: aload_0
    //   121: astore 4
    //   123: goto -16 -> 107
    //   126: aload_3
    //   127: astore 4
    //   129: aload_3
    //   130: aload_0
    //   131: if_acmpeq -24 -> 107
    //   134: aload_3
    //   135: aload_0
    //   136: invokevirtual 308	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   139: aload_3
    //   140: astore 4
    //   142: goto -35 -> 107
    //   145: aload_1
    //   146: athrow
    //   147: return
    //   148: astore_1
    //   149: aload 6
    //   151: astore_0
    //   152: goto -59 -> 93
    //   155: astore_1
    //   156: goto -63 -> 93
    //   159: astore_1
    //   160: goto -76 -> 84
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	163	0	paramString	String
    //   0	163	1	paramOutputStream	OutputStream
    //   33	20	2	i	int
    //   1	139	3	localOutputStream	OutputStream
    //   3	65	4	localObject1	Object
    //   86	5	4	localObject2	Object
    //   94	47	4	localObject3	Object
    //   9	73	5	arrayOfByte	byte[]
    //   6	144	6	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   63	67	76	java/lang/Throwable
    //   11	20	80	java/lang/Throwable
    //   84	86	86	finally
    //   100	104	115	java/lang/Throwable
    //   11	20	148	finally
    //   20	34	155	finally
    //   38	53	155	finally
    //   20	34	159	java/lang/Throwable
    //   38	53	159	java/lang/Throwable
  }
  
  private static final void unlockRead(String paramString)
  {
    FileAccessInfo localFileAccessInfo;
    synchronized (FILE_ACCESS_TABLE)
    {
      localFileAccessInfo = (FileAccessInfo)FILE_ACCESS_TABLE.get(paramString);
      if (localFileAccessInfo == null) {
        return;
      }
      localFileAccessInfo.readerCount -= 1;
      if (localFileAccessInfo.readerCount <= 0) {
        FILE_ACCESS_TABLE.remove(paramString);
      }
    }
    try
    {
      localFileAccessInfo.lock.close();
      try
      {
        localFileAccessInfo.openedLockFile.close();
        localFileAccessInfo.lockFile.delete();
        return;
        paramString = finally;
        throw paramString;
      }
      catch (Throwable paramString)
      {
        for (;;) {}
      }
    }
    catch (Throwable paramString)
    {
      for (;;) {}
    }
  }
  
  private static final void unlockWrite(String paramString)
  {
    FileAccessInfo localFileAccessInfo;
    synchronized (FILE_ACCESS_TABLE)
    {
      localFileAccessInfo = (FileAccessInfo)FILE_ACCESS_TABLE.get(paramString);
      if ((localFileAccessInfo != null) && (localFileAccessInfo.isWriting)) {
        FILE_ACCESS_TABLE.remove(paramString);
      }
    }
    try
    {
      localFileAccessInfo.lock.close();
      try
      {
        localFileAccessInfo.openedLockFile.close();
        localFileAccessInfo.lockFile.delete();
        return;
        return;
        paramString = finally;
        throw paramString;
      }
      catch (Throwable paramString)
      {
        for (;;) {}
      }
    }
    catch (Throwable paramString)
    {
      for (;;) {}
    }
  }
  
  public static void writeToFile(InputStream paramInputStream, File paramFile)
    throws IOException
  {
    writeToFile(paramInputStream, paramFile.getAbsolutePath());
  }
  
  /* Error */
  public static void writeToFile(InputStream paramInputStream, String paramString)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 6
    //   8: aconst_null
    //   9: astore 5
    //   11: new 336	java/io/FileOutputStream
    //   14: dup
    //   15: aload_1
    //   16: invokespecial 337	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   19: astore_1
    //   20: sipush 4096
    //   23: newarray <illegal type>
    //   25: astore 5
    //   27: aload_0
    //   28: aload 5
    //   30: invokevirtual 340	java/io/InputStream:read	([B)I
    //   33: istore_2
    //   34: iload_2
    //   35: ifle +21 -> 56
    //   38: aload_1
    //   39: aload 5
    //   41: iconst_0
    //   42: iload_2
    //   43: invokevirtual 341	java/io/FileOutputStream:write	([BII)V
    //   46: aload_0
    //   47: aload 5
    //   49: invokevirtual 340	java/io/InputStream:read	([B)I
    //   52: istore_2
    //   53: goto -19 -> 34
    //   56: aload 4
    //   58: astore_0
    //   59: aload_1
    //   60: ifnull +10 -> 70
    //   63: aload_1
    //   64: invokevirtual 342	java/io/FileOutputStream:close	()V
    //   67: aload 4
    //   69: astore_0
    //   70: aload_0
    //   71: ifnull +76 -> 147
    //   74: aload_0
    //   75: athrow
    //   76: astore_0
    //   77: goto -7 -> 70
    //   80: astore_1
    //   81: aload 5
    //   83: astore_0
    //   84: aload_1
    //   85: athrow
    //   86: astore 4
    //   88: aload_1
    //   89: astore_3
    //   90: aload 4
    //   92: astore_1
    //   93: aload_3
    //   94: astore 4
    //   96: aload_0
    //   97: ifnull +10 -> 107
    //   100: aload_0
    //   101: invokevirtual 342	java/io/FileOutputStream:close	()V
    //   104: aload_3
    //   105: astore 4
    //   107: aload 4
    //   109: ifnull +36 -> 145
    //   112: aload 4
    //   114: athrow
    //   115: astore_0
    //   116: aload_3
    //   117: ifnonnull +9 -> 126
    //   120: aload_0
    //   121: astore 4
    //   123: goto -16 -> 107
    //   126: aload_3
    //   127: astore 4
    //   129: aload_3
    //   130: aload_0
    //   131: if_acmpeq -24 -> 107
    //   134: aload_3
    //   135: aload_0
    //   136: invokevirtual 308	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   139: aload_3
    //   140: astore 4
    //   142: goto -35 -> 107
    //   145: aload_1
    //   146: athrow
    //   147: return
    //   148: astore_1
    //   149: aload 6
    //   151: astore_0
    //   152: goto -59 -> 93
    //   155: astore 4
    //   157: aload_1
    //   158: astore_0
    //   159: aload 4
    //   161: astore_1
    //   162: goto -69 -> 93
    //   165: astore_3
    //   166: aload_1
    //   167: astore_0
    //   168: aload_3
    //   169: astore_1
    //   170: goto -86 -> 84
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	173	0	paramInputStream	InputStream
    //   0	173	1	paramString	String
    //   33	20	2	i	int
    //   1	139	3	str	String
    //   165	4	3	localThrowable	Throwable
    //   3	65	4	localObject1	Object
    //   86	5	4	localObject2	Object
    //   94	47	4	localObject3	Object
    //   155	5	4	localObject4	Object
    //   9	73	5	arrayOfByte	byte[]
    //   6	144	6	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   63	67	76	java/lang/Throwable
    //   11	20	80	java/lang/Throwable
    //   84	86	86	finally
    //   100	104	115	java/lang/Throwable
    //   11	20	148	finally
    //   20	34	155	finally
    //   38	53	155	finally
    //   20	34	165	java/lang/Throwable
    //   38	53	165	java/lang/Throwable
  }
  
  private static final class FileAccessInfo
  {
    public volatile boolean isWriting;
    public final FileLock lock;
    public final File lockFile;
    public final RandomAccessFile openedLockFile;
    public volatile int readerCount;
    
    public FileAccessInfo(File paramFile, RandomAccessFile paramRandomAccessFile, FileLock paramFileLock)
    {
      this.lockFile = paramFile;
      this.openedLockFile = paramRandomAccessFile;
      this.lock = paramFileLock;
    }
  }
  
  private static final class LockedFileInputStream
    extends FileInputStream
  {
    private final String m_FilePath;
    private volatile boolean m_IsClosed;
    
    public LockedFileInputStream(String paramString)
      throws IOException
    {
      super();
      this.m_FilePath = paramString;
    }
    
    /* Error */
    public void close()
      throws IOException
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 27	com/oneplus/io/FileUtils$LockedFileInputStream:m_IsClosed	Z
      //   6: istore_1
      //   7: iload_1
      //   8: ifeq +6 -> 14
      //   11: aload_0
      //   12: monitorexit
      //   13: return
      //   14: aload_0
      //   15: iconst_1
      //   16: putfield 27	com/oneplus/io/FileUtils$LockedFileInputStream:m_IsClosed	Z
      //   19: aload_0
      //   20: monitorexit
      //   21: aload_0
      //   22: invokespecial 29	java/io/FileInputStream:close	()V
      //   25: aload_0
      //   26: getfield 19	com/oneplus/io/FileUtils$LockedFileInputStream:m_FilePath	Ljava/lang/String;
      //   29: invokestatic 32	com/oneplus/io/FileUtils:-wrap0	(Ljava/lang/String;)V
      //   32: return
      //   33: astore_2
      //   34: aload_0
      //   35: monitorexit
      //   36: aload_2
      //   37: athrow
      //   38: astore_2
      //   39: aload_2
      //   40: athrow
      //   41: astore_2
      //   42: aload_0
      //   43: getfield 19	com/oneplus/io/FileUtils$LockedFileInputStream:m_FilePath	Ljava/lang/String;
      //   46: invokestatic 32	com/oneplus/io/FileUtils:-wrap0	(Ljava/lang/String;)V
      //   49: aload_2
      //   50: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	51	0	this	LockedFileInputStream
      //   6	2	1	bool	boolean
      //   33	4	2	localObject1	Object
      //   38	2	2	localThrowable	Throwable
      //   41	9	2	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   2	7	33	finally
      //   14	19	33	finally
      //   21	25	38	java/lang/Throwable
      //   21	25	41	finally
      //   39	41	41	finally
    }
  }
  
  private static final class LockedFileOutputStream
    extends FileOutputStream
  {
    private final String m_FilePath;
    private volatile boolean m_IsClosed;
    
    public LockedFileOutputStream(String paramString)
      throws IOException
    {
      super();
      this.m_FilePath = paramString;
    }
    
    /* Error */
    public void close()
      throws IOException
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 27	com/oneplus/io/FileUtils$LockedFileOutputStream:m_IsClosed	Z
      //   6: istore_1
      //   7: iload_1
      //   8: ifeq +6 -> 14
      //   11: aload_0
      //   12: monitorexit
      //   13: return
      //   14: aload_0
      //   15: iconst_1
      //   16: putfield 27	com/oneplus/io/FileUtils$LockedFileOutputStream:m_IsClosed	Z
      //   19: aload_0
      //   20: monitorexit
      //   21: aload_0
      //   22: invokespecial 29	java/io/FileOutputStream:close	()V
      //   25: aload_0
      //   26: getfield 19	com/oneplus/io/FileUtils$LockedFileOutputStream:m_FilePath	Ljava/lang/String;
      //   29: invokestatic 32	com/oneplus/io/FileUtils:-wrap1	(Ljava/lang/String;)V
      //   32: return
      //   33: astore_2
      //   34: aload_0
      //   35: monitorexit
      //   36: aload_2
      //   37: athrow
      //   38: astore_2
      //   39: aload_2
      //   40: athrow
      //   41: astore_2
      //   42: aload_0
      //   43: getfield 19	com/oneplus/io/FileUtils$LockedFileOutputStream:m_FilePath	Ljava/lang/String;
      //   46: invokestatic 32	com/oneplus/io/FileUtils:-wrap1	(Ljava/lang/String;)V
      //   49: aload_2
      //   50: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	51	0	this	LockedFileOutputStream
      //   6	2	1	bool	boolean
      //   33	4	2	localObject1	Object
      //   38	2	2	localThrowable	Throwable
      //   41	9	2	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   2	7	33	finally
      //   14	19	33	finally
      //   21	25	38	java/lang/Throwable
      //   21	25	41	finally
      //   39	41	41	finally
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/FileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */