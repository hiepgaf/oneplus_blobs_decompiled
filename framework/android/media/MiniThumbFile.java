package android.media;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class MiniThumbFile
{
  public static final int BYTES_PER_MINTHUMB = 10000;
  private static final int HEADER_SIZE = 13;
  private static final int MINI_THUMB_DATA_FILE_VERSION = 3;
  private static final String TAG = "MiniThumbFile";
  private static final Hashtable<String, MiniThumbFile> sThumbFiles = new Hashtable();
  private ByteBuffer mBuffer;
  private FileChannel mChannel;
  private RandomAccessFile mMiniThumbFile;
  private Uri mUri;
  
  public MiniThumbFile(Uri paramUri)
  {
    this.mUri = paramUri;
    this.mBuffer = ByteBuffer.allocateDirect(10000);
  }
  
  public static MiniThumbFile instance(Uri paramUri)
  {
    try
    {
      String str = (String)paramUri.getPathSegments().get(1);
      MiniThumbFile localMiniThumbFile = (MiniThumbFile)sThumbFiles.get(str);
      paramUri = localMiniThumbFile;
      if (localMiniThumbFile == null)
      {
        paramUri = new MiniThumbFile(Uri.parse("content://media/external/" + str + "/media"));
        sThumbFiles.put(str, paramUri);
      }
      return paramUri;
    }
    finally {}
  }
  
  private RandomAccessFile miniThumbDataFile()
  {
    Object localObject;
    if (this.mMiniThumbFile == null)
    {
      removeOldFile();
      localObject = randomAccessFilePath(3);
      File localFile = new File((String)localObject).getParentFile();
      if ((!localFile.isDirectory()) && (!localFile.mkdirs())) {
        Log.e("MiniThumbFile", "Unable to create .thumbnails directory " + localFile.toString());
      }
      localObject = new File((String)localObject);
    }
    try
    {
      this.mMiniThumbFile = new RandomAccessFile((File)localObject, "rw");
      if (this.mMiniThumbFile != null) {
        this.mChannel = this.mMiniThumbFile.getChannel();
      }
      return this.mMiniThumbFile;
    }
    catch (IOException localIOException2)
    {
      for (;;)
      {
        try
        {
          this.mMiniThumbFile = new RandomAccessFile((File)localObject, "r");
        }
        catch (IOException localIOException1) {}
      }
    }
  }
  
  private String randomAccessFilePath(int paramInt)
  {
    String str = Environment.getExternalStorageDirectory().toString() + "/DCIM/.thumbnails";
    return str + "/.thumbdata" + paramInt + "-" + this.mUri.hashCode();
  }
  
  private void removeOldFile()
  {
    File localFile = new File(randomAccessFilePath(2));
    if (localFile.exists()) {}
    try
    {
      localFile.delete();
      return;
    }
    catch (SecurityException localSecurityException) {}
  }
  
  public static void reset()
  {
    try
    {
      Iterator localIterator = sThumbFiles.values().iterator();
      while (localIterator.hasNext()) {
        ((MiniThumbFile)localIterator.next()).deactivate();
      }
      sThumbFiles.clear();
    }
    finally {}
  }
  
  /* Error */
  public void deactivate()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 98	android/media/MiniThumbFile:mMiniThumbFile	Ljava/io/RandomAccessFile;
    //   6: astore_1
    //   7: aload_1
    //   8: ifnull +15 -> 23
    //   11: aload_0
    //   12: getfield 98	android/media/MiniThumbFile:mMiniThumbFile	Ljava/io/RandomAccessFile;
    //   15: invokevirtual 200	java/io/RandomAccessFile:close	()V
    //   18: aload_0
    //   19: aconst_null
    //   20: putfield 98	android/media/MiniThumbFile:mMiniThumbFile	Ljava/io/RandomAccessFile;
    //   23: aload_0
    //   24: monitorexit
    //   25: return
    //   26: astore_1
    //   27: aload_0
    //   28: monitorexit
    //   29: aload_1
    //   30: athrow
    //   31: astore_1
    //   32: goto -9 -> 23
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	this	MiniThumbFile
    //   6	2	1	localRandomAccessFile	RandomAccessFile
    //   26	4	1	localObject	Object
    //   31	1	1	localIOException	IOException
    // Exception table:
    //   from	to	target	type
    //   2	7	26	finally
    //   11	23	26	finally
    //   11	23	31	java/io/IOException
  }
  
  /* Error */
  public long getMagic(long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 206	android/media/MiniThumbFile:miniThumbDataFile	()Ljava/io/RandomAccessFile;
    //   6: astore 5
    //   8: aload 5
    //   10: ifnull +208 -> 218
    //   13: lload_1
    //   14: ldc2_w 207
    //   17: lmul
    //   18: lstore_3
    //   19: aconst_null
    //   20: astore 9
    //   22: aconst_null
    //   23: astore 10
    //   25: aconst_null
    //   26: astore 8
    //   28: aload 8
    //   30: astore 6
    //   32: aload 9
    //   34: astore 7
    //   36: aload 10
    //   38: astore 5
    //   40: aload_0
    //   41: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   44: invokevirtual 211	java/nio/ByteBuffer:clear	()Ljava/nio/Buffer;
    //   47: pop
    //   48: aload 8
    //   50: astore 6
    //   52: aload 9
    //   54: astore 7
    //   56: aload 10
    //   58: astore 5
    //   60: aload_0
    //   61: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   64: bipush 9
    //   66: invokevirtual 215	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   69: pop
    //   70: aload 8
    //   72: astore 6
    //   74: aload 9
    //   76: astore 7
    //   78: aload 10
    //   80: astore 5
    //   82: aload_0
    //   83: getfield 143	android/media/MiniThumbFile:mChannel	Ljava/nio/channels/FileChannel;
    //   86: lload_3
    //   87: ldc2_w 216
    //   90: iconst_1
    //   91: invokevirtual 223	java/nio/channels/FileChannel:lock	(JJZ)Ljava/nio/channels/FileLock;
    //   94: astore 8
    //   96: aload 8
    //   98: astore 6
    //   100: aload 8
    //   102: astore 7
    //   104: aload 8
    //   106: astore 5
    //   108: aload_0
    //   109: getfield 143	android/media/MiniThumbFile:mChannel	Ljava/nio/channels/FileChannel;
    //   112: aload_0
    //   113: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   116: lload_3
    //   117: invokevirtual 227	java/nio/channels/FileChannel:read	(Ljava/nio/ByteBuffer;J)I
    //   120: bipush 9
    //   122: if_icmpne +86 -> 208
    //   125: aload 8
    //   127: astore 6
    //   129: aload 8
    //   131: astore 7
    //   133: aload 8
    //   135: astore 5
    //   137: aload_0
    //   138: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   141: iconst_0
    //   142: invokevirtual 230	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   145: pop
    //   146: aload 8
    //   148: astore 6
    //   150: aload 8
    //   152: astore 7
    //   154: aload 8
    //   156: astore 5
    //   158: aload_0
    //   159: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   162: invokevirtual 233	java/nio/ByteBuffer:get	()B
    //   165: iconst_1
    //   166: if_icmpne +42 -> 208
    //   169: aload 8
    //   171: astore 6
    //   173: aload 8
    //   175: astore 7
    //   177: aload 8
    //   179: astore 5
    //   181: aload_0
    //   182: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   185: invokevirtual 237	java/nio/ByteBuffer:getLong	()J
    //   188: lstore_3
    //   189: aload 8
    //   191: ifnull +8 -> 199
    //   194: aload 8
    //   196: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   199: aload_0
    //   200: monitorexit
    //   201: lload_3
    //   202: lreturn
    //   203: astore 5
    //   205: goto -6 -> 199
    //   208: aload 8
    //   210: ifnull +8 -> 218
    //   213: aload 8
    //   215: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   218: aload_0
    //   219: monitorexit
    //   220: lconst_0
    //   221: lreturn
    //   222: astore 5
    //   224: goto -6 -> 218
    //   227: astore 7
    //   229: aload 6
    //   231: astore 5
    //   233: ldc 15
    //   235: new 69	java/lang/StringBuilder
    //   238: dup
    //   239: invokespecial 70	java/lang/StringBuilder:<init>	()V
    //   242: ldc -12
    //   244: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   247: lload_1
    //   248: invokevirtual 247	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   251: ldc -7
    //   253: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   256: aload 7
    //   258: invokevirtual 253	java/lang/RuntimeException:getClass	()Ljava/lang/Class;
    //   261: invokevirtual 256	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   264: invokevirtual 82	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   267: invokestatic 130	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   270: pop
    //   271: aload 6
    //   273: ifnull -55 -> 218
    //   276: aload 6
    //   278: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   281: goto -63 -> 218
    //   284: astore 5
    //   286: goto -68 -> 218
    //   289: astore 6
    //   291: aload 7
    //   293: astore 5
    //   295: ldc 15
    //   297: ldc_w 258
    //   300: aload 6
    //   302: invokestatic 262	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   305: pop
    //   306: aload 7
    //   308: ifnull -90 -> 218
    //   311: aload 7
    //   313: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   316: goto -98 -> 218
    //   319: astore 5
    //   321: goto -103 -> 218
    //   324: astore 6
    //   326: aload 5
    //   328: ifnull +8 -> 336
    //   331: aload 5
    //   333: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   336: aload 6
    //   338: athrow
    //   339: astore 5
    //   341: aload_0
    //   342: monitorexit
    //   343: aload 5
    //   345: athrow
    //   346: astore 5
    //   348: goto -12 -> 336
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	351	0	this	MiniThumbFile
    //   0	351	1	paramLong	long
    //   18	184	3	l	long
    //   6	174	5	localObject1	Object
    //   203	1	5	localIOException1	IOException
    //   222	1	5	localIOException2	IOException
    //   231	1	5	localObject2	Object
    //   284	1	5	localIOException3	IOException
    //   293	1	5	localObject3	Object
    //   319	13	5	localIOException4	IOException
    //   339	5	5	localObject4	Object
    //   346	1	5	localIOException5	IOException
    //   30	247	6	localFileLock1	java.nio.channels.FileLock
    //   289	12	6	localIOException6	IOException
    //   324	13	6	localObject5	Object
    //   34	142	7	localObject6	Object
    //   227	85	7	localRuntimeException	RuntimeException
    //   26	188	8	localFileLock2	java.nio.channels.FileLock
    //   20	55	9	localObject7	Object
    //   23	56	10	localObject8	Object
    // Exception table:
    //   from	to	target	type
    //   194	199	203	java/io/IOException
    //   213	218	222	java/io/IOException
    //   40	48	227	java/lang/RuntimeException
    //   60	70	227	java/lang/RuntimeException
    //   82	96	227	java/lang/RuntimeException
    //   108	125	227	java/lang/RuntimeException
    //   137	146	227	java/lang/RuntimeException
    //   158	169	227	java/lang/RuntimeException
    //   181	189	227	java/lang/RuntimeException
    //   276	281	284	java/io/IOException
    //   40	48	289	java/io/IOException
    //   60	70	289	java/io/IOException
    //   82	96	289	java/io/IOException
    //   108	125	289	java/io/IOException
    //   137	146	289	java/io/IOException
    //   158	169	289	java/io/IOException
    //   181	189	289	java/io/IOException
    //   311	316	319	java/io/IOException
    //   40	48	324	finally
    //   60	70	324	finally
    //   82	96	324	finally
    //   108	125	324	finally
    //   137	146	324	finally
    //   158	169	324	finally
    //   181	189	324	finally
    //   233	271	324	finally
    //   295	306	324	finally
    //   2	8	339	finally
    //   194	199	339	finally
    //   213	218	339	finally
    //   276	281	339	finally
    //   311	316	339	finally
    //   331	336	339	finally
    //   336	339	339	finally
    //   331	336	346	java/io/IOException
  }
  
  /* Error */
  public byte[] getMiniThumbFromFile(long paramLong, byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 206	android/media/MiniThumbFile:miniThumbDataFile	()Ljava/io/RandomAccessFile;
    //   6: astore 9
    //   8: aload 9
    //   10: ifnonnull +7 -> 17
    //   13: aload_0
    //   14: monitorexit
    //   15: aconst_null
    //   16: areturn
    //   17: lload_1
    //   18: ldc2_w 207
    //   21: lmul
    //   22: lstore 7
    //   24: aconst_null
    //   25: astore 13
    //   27: aconst_null
    //   28: astore 14
    //   30: aconst_null
    //   31: astore 12
    //   33: aload 12
    //   35: astore 10
    //   37: aload 13
    //   39: astore 11
    //   41: aload 14
    //   43: astore 9
    //   45: aload_0
    //   46: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   49: invokevirtual 211	java/nio/ByteBuffer:clear	()Ljava/nio/Buffer;
    //   52: pop
    //   53: aload 12
    //   55: astore 10
    //   57: aload 13
    //   59: astore 11
    //   61: aload 14
    //   63: astore 9
    //   65: aload_0
    //   66: getfield 143	android/media/MiniThumbFile:mChannel	Ljava/nio/channels/FileChannel;
    //   69: lload 7
    //   71: ldc2_w 207
    //   74: iconst_1
    //   75: invokevirtual 223	java/nio/channels/FileChannel:lock	(JJZ)Ljava/nio/channels/FileLock;
    //   78: astore 12
    //   80: aload 12
    //   82: astore 10
    //   84: aload 12
    //   86: astore 11
    //   88: aload 12
    //   90: astore 9
    //   92: aload_0
    //   93: getfield 143	android/media/MiniThumbFile:mChannel	Ljava/nio/channels/FileChannel;
    //   96: aload_0
    //   97: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   100: lload 7
    //   102: invokevirtual 227	java/nio/channels/FileChannel:read	(Ljava/nio/ByteBuffer;J)I
    //   105: istore 4
    //   107: iload 4
    //   109: bipush 13
    //   111: if_icmple +177 -> 288
    //   114: aload 12
    //   116: astore 10
    //   118: aload 12
    //   120: astore 11
    //   122: aload 12
    //   124: astore 9
    //   126: aload_0
    //   127: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   130: iconst_0
    //   131: invokevirtual 230	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   134: pop
    //   135: aload 12
    //   137: astore 10
    //   139: aload 12
    //   141: astore 11
    //   143: aload 12
    //   145: astore 9
    //   147: aload_0
    //   148: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   151: invokevirtual 233	java/nio/ByteBuffer:get	()B
    //   154: istore 5
    //   156: aload 12
    //   158: astore 10
    //   160: aload 12
    //   162: astore 11
    //   164: aload 12
    //   166: astore 9
    //   168: aload_0
    //   169: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   172: invokevirtual 237	java/nio/ByteBuffer:getLong	()J
    //   175: lstore 7
    //   177: aload 12
    //   179: astore 10
    //   181: aload 12
    //   183: astore 11
    //   185: aload 12
    //   187: astore 9
    //   189: aload_0
    //   190: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   193: invokevirtual 267	java/nio/ByteBuffer:getInt	()I
    //   196: istore 6
    //   198: iload 4
    //   200: iload 6
    //   202: bipush 13
    //   204: iadd
    //   205: if_icmplt +83 -> 288
    //   208: iload 6
    //   210: ifeq +78 -> 288
    //   213: lload 7
    //   215: lconst_0
    //   216: lcmp
    //   217: ifeq +71 -> 288
    //   220: iload 5
    //   222: iconst_1
    //   223: if_icmpne +65 -> 288
    //   226: aload 12
    //   228: astore 10
    //   230: aload 12
    //   232: astore 11
    //   234: aload 12
    //   236: astore 9
    //   238: aload_3
    //   239: arraylength
    //   240: iload 6
    //   242: if_icmplt +46 -> 288
    //   245: aload 12
    //   247: astore 10
    //   249: aload 12
    //   251: astore 11
    //   253: aload 12
    //   255: astore 9
    //   257: aload_0
    //   258: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   261: aload_3
    //   262: iconst_0
    //   263: iload 6
    //   265: invokevirtual 270	java/nio/ByteBuffer:get	([BII)Ljava/nio/ByteBuffer;
    //   268: pop
    //   269: aload 12
    //   271: ifnull +8 -> 279
    //   274: aload 12
    //   276: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   279: aload_0
    //   280: monitorexit
    //   281: aload_3
    //   282: areturn
    //   283: astore 9
    //   285: goto -6 -> 279
    //   288: aload 12
    //   290: ifnull +8 -> 298
    //   293: aload 12
    //   295: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   298: aload_0
    //   299: monitorexit
    //   300: aconst_null
    //   301: areturn
    //   302: astore_3
    //   303: goto -5 -> 298
    //   306: astore_3
    //   307: aload 10
    //   309: astore 9
    //   311: ldc 15
    //   313: new 69	java/lang/StringBuilder
    //   316: dup
    //   317: invokespecial 70	java/lang/StringBuilder:<init>	()V
    //   320: ldc_w 272
    //   323: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   326: lload_1
    //   327: invokevirtual 247	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   330: ldc -7
    //   332: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   335: aload_3
    //   336: invokevirtual 253	java/lang/RuntimeException:getClass	()Ljava/lang/Class;
    //   339: invokevirtual 256	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   342: invokevirtual 82	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   345: invokestatic 130	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   348: pop
    //   349: aload 10
    //   351: ifnull -53 -> 298
    //   354: aload 10
    //   356: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   359: goto -61 -> 298
    //   362: astore_3
    //   363: goto -65 -> 298
    //   366: astore_3
    //   367: aload 11
    //   369: astore 9
    //   371: ldc 15
    //   373: new 69	java/lang/StringBuilder
    //   376: dup
    //   377: invokespecial 70	java/lang/StringBuilder:<init>	()V
    //   380: ldc_w 274
    //   383: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   386: lload_1
    //   387: invokevirtual 247	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   390: ldc_w 276
    //   393: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   396: aload_3
    //   397: invokevirtual 256	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   400: invokevirtual 82	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   403: invokestatic 279	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   406: pop
    //   407: aload 11
    //   409: ifnull -111 -> 298
    //   412: aload 11
    //   414: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   417: goto -119 -> 298
    //   420: astore_3
    //   421: goto -123 -> 298
    //   424: astore_3
    //   425: aload 9
    //   427: ifnull +8 -> 435
    //   430: aload 9
    //   432: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   435: aload_3
    //   436: athrow
    //   437: astore_3
    //   438: aload_0
    //   439: monitorexit
    //   440: aload_3
    //   441: athrow
    //   442: astore 9
    //   444: goto -9 -> 435
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	447	0	this	MiniThumbFile
    //   0	447	1	paramLong	long
    //   0	447	3	paramArrayOfByte	byte[]
    //   105	101	4	i	int
    //   154	70	5	j	int
    //   196	68	6	k	int
    //   22	192	7	l	long
    //   6	250	9	localObject1	Object
    //   283	1	9	localIOException1	IOException
    //   309	122	9	localObject2	Object
    //   442	1	9	localIOException2	IOException
    //   35	320	10	localFileLock1	java.nio.channels.FileLock
    //   39	374	11	localObject3	Object
    //   31	263	12	localFileLock2	java.nio.channels.FileLock
    //   25	33	13	localObject4	Object
    //   28	34	14	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   274	279	283	java/io/IOException
    //   293	298	302	java/io/IOException
    //   45	53	306	java/lang/RuntimeException
    //   65	80	306	java/lang/RuntimeException
    //   92	107	306	java/lang/RuntimeException
    //   126	135	306	java/lang/RuntimeException
    //   147	156	306	java/lang/RuntimeException
    //   168	177	306	java/lang/RuntimeException
    //   189	198	306	java/lang/RuntimeException
    //   238	245	306	java/lang/RuntimeException
    //   257	269	306	java/lang/RuntimeException
    //   354	359	362	java/io/IOException
    //   45	53	366	java/io/IOException
    //   65	80	366	java/io/IOException
    //   92	107	366	java/io/IOException
    //   126	135	366	java/io/IOException
    //   147	156	366	java/io/IOException
    //   168	177	366	java/io/IOException
    //   189	198	366	java/io/IOException
    //   238	245	366	java/io/IOException
    //   257	269	366	java/io/IOException
    //   412	417	420	java/io/IOException
    //   45	53	424	finally
    //   65	80	424	finally
    //   92	107	424	finally
    //   126	135	424	finally
    //   147	156	424	finally
    //   168	177	424	finally
    //   189	198	424	finally
    //   238	245	424	finally
    //   257	269	424	finally
    //   311	349	424	finally
    //   371	407	424	finally
    //   2	8	437	finally
    //   274	279	437	finally
    //   293	298	437	finally
    //   354	359	437	finally
    //   412	417	437	finally
    //   430	435	437	finally
    //   435	437	437	finally
    //   430	435	442	java/io/IOException
  }
  
  /* Error */
  public void saveMiniThumbToFile(byte[] paramArrayOfByte, long paramLong1, long paramLong2)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 206	android/media/MiniThumbFile:miniThumbDataFile	()Ljava/io/RandomAccessFile;
    //   6: astore 9
    //   8: aload 9
    //   10: ifnonnull +6 -> 16
    //   13: aload_0
    //   14: monitorexit
    //   15: return
    //   16: lload_2
    //   17: ldc2_w 207
    //   20: lmul
    //   21: lstore 7
    //   23: aconst_null
    //   24: astore 12
    //   26: aconst_null
    //   27: astore 13
    //   29: aconst_null
    //   30: astore 14
    //   32: aconst_null
    //   33: astore 9
    //   35: aload_1
    //   36: ifnull +208 -> 244
    //   39: aload 12
    //   41: astore 10
    //   43: aload 13
    //   45: astore 11
    //   47: aload 14
    //   49: astore 9
    //   51: aload_1
    //   52: arraylength
    //   53: istore 6
    //   55: iload 6
    //   57: sipush 9987
    //   60: if_icmple +6 -> 66
    //   63: aload_0
    //   64: monitorexit
    //   65: return
    //   66: aload 12
    //   68: astore 10
    //   70: aload 13
    //   72: astore 11
    //   74: aload 14
    //   76: astore 9
    //   78: aload_0
    //   79: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   82: invokevirtual 211	java/nio/ByteBuffer:clear	()Ljava/nio/Buffer;
    //   85: pop
    //   86: aload 12
    //   88: astore 10
    //   90: aload 13
    //   92: astore 11
    //   94: aload 14
    //   96: astore 9
    //   98: aload_0
    //   99: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   102: iconst_1
    //   103: invokevirtual 284	java/nio/ByteBuffer:put	(B)Ljava/nio/ByteBuffer;
    //   106: pop
    //   107: aload 12
    //   109: astore 10
    //   111: aload 13
    //   113: astore 11
    //   115: aload 14
    //   117: astore 9
    //   119: aload_0
    //   120: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   123: lload 4
    //   125: invokevirtual 288	java/nio/ByteBuffer:putLong	(J)Ljava/nio/ByteBuffer;
    //   128: pop
    //   129: aload 12
    //   131: astore 10
    //   133: aload 13
    //   135: astore 11
    //   137: aload 14
    //   139: astore 9
    //   141: aload_0
    //   142: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   145: aload_1
    //   146: arraylength
    //   147: invokevirtual 291	java/nio/ByteBuffer:putInt	(I)Ljava/nio/ByteBuffer;
    //   150: pop
    //   151: aload 12
    //   153: astore 10
    //   155: aload 13
    //   157: astore 11
    //   159: aload 14
    //   161: astore 9
    //   163: aload_0
    //   164: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   167: aload_1
    //   168: invokevirtual 294	java/nio/ByteBuffer:put	([B)Ljava/nio/ByteBuffer;
    //   171: pop
    //   172: aload 12
    //   174: astore 10
    //   176: aload 13
    //   178: astore 11
    //   180: aload 14
    //   182: astore 9
    //   184: aload_0
    //   185: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   188: invokevirtual 297	java/nio/ByteBuffer:flip	()Ljava/nio/Buffer;
    //   191: pop
    //   192: aload 12
    //   194: astore 10
    //   196: aload 13
    //   198: astore 11
    //   200: aload 14
    //   202: astore 9
    //   204: aload_0
    //   205: getfield 143	android/media/MiniThumbFile:mChannel	Ljava/nio/channels/FileChannel;
    //   208: lload 7
    //   210: ldc2_w 207
    //   213: iconst_0
    //   214: invokevirtual 223	java/nio/channels/FileChannel:lock	(JJZ)Ljava/nio/channels/FileLock;
    //   217: astore_1
    //   218: aload_1
    //   219: astore 10
    //   221: aload_1
    //   222: astore 11
    //   224: aload_1
    //   225: astore 9
    //   227: aload_0
    //   228: getfield 143	android/media/MiniThumbFile:mChannel	Ljava/nio/channels/FileChannel;
    //   231: aload_0
    //   232: getfield 48	android/media/MiniThumbFile:mBuffer	Ljava/nio/ByteBuffer;
    //   235: lload 7
    //   237: invokevirtual 300	java/nio/channels/FileChannel:write	(Ljava/nio/ByteBuffer;J)I
    //   240: pop
    //   241: aload_1
    //   242: astore 9
    //   244: aload 9
    //   246: ifnull +8 -> 254
    //   249: aload 9
    //   251: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   254: aload_0
    //   255: monitorexit
    //   256: return
    //   257: astore_1
    //   258: goto -4 -> 254
    //   261: astore_1
    //   262: aload 10
    //   264: astore 9
    //   266: ldc 15
    //   268: new 69	java/lang/StringBuilder
    //   271: dup
    //   272: invokespecial 70	java/lang/StringBuilder:<init>	()V
    //   275: ldc_w 302
    //   278: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   281: lload_2
    //   282: invokevirtual 247	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   285: ldc_w 304
    //   288: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   291: aload_1
    //   292: invokevirtual 253	java/lang/RuntimeException:getClass	()Ljava/lang/Class;
    //   295: invokevirtual 256	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   298: invokevirtual 82	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   301: invokestatic 130	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   304: pop
    //   305: aload 10
    //   307: ifnull -53 -> 254
    //   310: aload 10
    //   312: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   315: goto -61 -> 254
    //   318: astore_1
    //   319: goto -65 -> 254
    //   322: astore_1
    //   323: aload 11
    //   325: astore 9
    //   327: ldc 15
    //   329: new 69	java/lang/StringBuilder
    //   332: dup
    //   333: invokespecial 70	java/lang/StringBuilder:<init>	()V
    //   336: ldc_w 302
    //   339: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   342: lload_2
    //   343: invokevirtual 247	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   346: ldc_w 306
    //   349: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   352: invokevirtual 82	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   355: aload_1
    //   356: invokestatic 308	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   359: pop
    //   360: aload 11
    //   362: astore 9
    //   364: aload_1
    //   365: athrow
    //   366: astore_1
    //   367: aload 9
    //   369: ifnull +8 -> 377
    //   372: aload 9
    //   374: invokevirtual 242	java/nio/channels/FileLock:release	()V
    //   377: aload_1
    //   378: athrow
    //   379: astore_1
    //   380: aload_0
    //   381: monitorexit
    //   382: aload_1
    //   383: athrow
    //   384: astore 9
    //   386: goto -9 -> 377
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	389	0	this	MiniThumbFile
    //   0	389	1	paramArrayOfByte	byte[]
    //   0	389	2	paramLong1	long
    //   0	389	4	paramLong2	long
    //   53	8	6	i	int
    //   21	215	7	l	long
    //   6	367	9	localObject1	Object
    //   384	1	9	localIOException	IOException
    //   41	270	10	localObject2	Object
    //   45	316	11	localObject3	Object
    //   24	169	12	localObject4	Object
    //   27	170	13	localObject5	Object
    //   30	171	14	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   249	254	257	java/io/IOException
    //   51	55	261	java/lang/RuntimeException
    //   78	86	261	java/lang/RuntimeException
    //   98	107	261	java/lang/RuntimeException
    //   119	129	261	java/lang/RuntimeException
    //   141	151	261	java/lang/RuntimeException
    //   163	172	261	java/lang/RuntimeException
    //   184	192	261	java/lang/RuntimeException
    //   204	218	261	java/lang/RuntimeException
    //   227	241	261	java/lang/RuntimeException
    //   310	315	318	java/io/IOException
    //   51	55	322	java/io/IOException
    //   78	86	322	java/io/IOException
    //   98	107	322	java/io/IOException
    //   119	129	322	java/io/IOException
    //   141	151	322	java/io/IOException
    //   163	172	322	java/io/IOException
    //   184	192	322	java/io/IOException
    //   204	218	322	java/io/IOException
    //   227	241	322	java/io/IOException
    //   51	55	366	finally
    //   78	86	366	finally
    //   98	107	366	finally
    //   119	129	366	finally
    //   141	151	366	finally
    //   163	172	366	finally
    //   184	192	366	finally
    //   204	218	366	finally
    //   227	241	366	finally
    //   266	305	366	finally
    //   327	360	366	finally
    //   364	366	366	finally
    //   2	8	379	finally
    //   249	254	379	finally
    //   310	315	379	finally
    //   372	377	379	finally
    //   377	379	379	finally
    //   372	377	384	java/io/IOException
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MiniThumbFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */