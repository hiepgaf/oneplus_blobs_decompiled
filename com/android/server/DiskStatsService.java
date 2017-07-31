package com.android.server;

import android.content.Context;
import android.os.Binder;
import android.os.StatFs;
import java.io.File;
import java.io.PrintWriter;

public class DiskStatsService
  extends Binder
{
  private static final String TAG = "DiskStatsService";
  private final Context mContext;
  
  public DiskStatsService(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private void reportFreeSpace(File paramFile, String paramString, PrintWriter paramPrintWriter)
  {
    long l1;
    long l2;
    long l3;
    try
    {
      paramFile = new StatFs(paramFile.getPath());
      l1 = paramFile.getBlockSize();
      l2 = paramFile.getAvailableBlocks();
      l3 = paramFile.getBlockCount();
      if ((l1 <= 0L) || (l3 <= 0L)) {
        throw new IllegalArgumentException("Invalid stat: bsize=" + l1 + " avail=" + l2 + " total=" + l3);
      }
    }
    catch (IllegalArgumentException paramFile)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("-Error: ");
      paramPrintWriter.println(paramFile.toString());
      return;
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("-Free: ");
    paramPrintWriter.print(l2 * l1 / 1024L);
    paramPrintWriter.print("K / ");
    paramPrintWriter.print(l3 * l1 / 1024L);
    paramPrintWriter.print("K total = ");
    paramPrintWriter.print(100L * l2 / l3);
    paramPrintWriter.println("% free");
  }
  
  /* Error */
  protected void dump(java.io.FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 17	com/android/server/DiskStatsService:mContext	Landroid/content/Context;
    //   4: ldc 95
    //   6: ldc 8
    //   8: invokevirtual 101	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   11: sipush 512
    //   14: newarray <illegal type>
    //   16: astore 12
    //   18: iconst_0
    //   19: istore 4
    //   21: iload 4
    //   23: aload 12
    //   25: arraylength
    //   26: if_icmpge +20 -> 46
    //   29: aload 12
    //   31: iload 4
    //   33: iload 4
    //   35: i2b
    //   36: bastore
    //   37: iload 4
    //   39: iconst_1
    //   40: iadd
    //   41: istore 4
    //   43: goto -22 -> 21
    //   46: new 26	java/io/File
    //   49: dup
    //   50: invokestatic 107	android/os/Environment:getDataDirectory	()Ljava/io/File;
    //   53: ldc 109
    //   55: invokespecial 112	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   58: astore 11
    //   60: aconst_null
    //   61: astore_3
    //   62: aconst_null
    //   63: astore 9
    //   65: aconst_null
    //   66: astore 10
    //   68: invokestatic 118	android/os/SystemClock:uptimeMillis	()J
    //   71: lstore 5
    //   73: new 120	java/io/FileOutputStream
    //   76: dup
    //   77: aload 11
    //   79: invokespecial 123	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   82: astore_1
    //   83: aload_1
    //   84: aload 12
    //   86: invokevirtual 127	java/io/FileOutputStream:write	([B)V
    //   89: aload_1
    //   90: ifnull +7 -> 97
    //   93: aload_1
    //   94: invokevirtual 130	java/io/FileOutputStream:close	()V
    //   97: aload 10
    //   99: astore 9
    //   101: invokestatic 118	android/os/SystemClock:uptimeMillis	()J
    //   104: lstore 7
    //   106: aload 11
    //   108: invokevirtual 134	java/io/File:exists	()Z
    //   111: ifeq +9 -> 120
    //   114: aload 11
    //   116: invokevirtual 137	java/io/File:delete	()Z
    //   119: pop
    //   120: aload 9
    //   122: ifnull +116 -> 238
    //   125: aload_2
    //   126: ldc -117
    //   128: invokevirtual 68	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   131: aload_2
    //   132: aload 9
    //   134: invokevirtual 140	java/io/IOException:toString	()Ljava/lang/String;
    //   137: invokevirtual 74	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   140: aload_0
    //   141: invokestatic 107	android/os/Environment:getDataDirectory	()Ljava/io/File;
    //   144: ldc -114
    //   146: aload_2
    //   147: invokespecial 144	com/android/server/DiskStatsService:reportFreeSpace	(Ljava/io/File;Ljava/lang/String;Ljava/io/PrintWriter;)V
    //   150: aload_0
    //   151: invokestatic 147	android/os/Environment:getDownloadCacheDirectory	()Ljava/io/File;
    //   154: ldc -107
    //   156: aload_2
    //   157: invokespecial 144	com/android/server/DiskStatsService:reportFreeSpace	(Ljava/io/File;Ljava/lang/String;Ljava/io/PrintWriter;)V
    //   160: aload_0
    //   161: new 26	java/io/File
    //   164: dup
    //   165: ldc -105
    //   167: invokespecial 152	java/io/File:<init>	(Ljava/lang/String;)V
    //   170: ldc -102
    //   172: aload_2
    //   173: invokespecial 144	com/android/server/DiskStatsService:reportFreeSpace	(Ljava/io/File;Ljava/lang/String;Ljava/io/PrintWriter;)V
    //   176: invokestatic 159	android/os/storage/StorageManager:isFileEncryptedNativeOnly	()Z
    //   179: ifeq +9 -> 188
    //   182: aload_2
    //   183: ldc -95
    //   185: invokevirtual 74	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   188: return
    //   189: astore_1
    //   190: goto -93 -> 97
    //   193: astore_1
    //   194: aload 9
    //   196: astore_3
    //   197: aload_1
    //   198: astore 9
    //   200: aload_3
    //   201: ifnull -100 -> 101
    //   204: aload_3
    //   205: invokevirtual 130	java/io/FileOutputStream:close	()V
    //   208: aload_1
    //   209: astore 9
    //   211: goto -110 -> 101
    //   214: astore_3
    //   215: aload_1
    //   216: astore 9
    //   218: goto -117 -> 101
    //   221: astore_1
    //   222: aload_3
    //   223: astore_2
    //   224: aload_2
    //   225: ifnull +7 -> 232
    //   228: aload_2
    //   229: invokevirtual 130	java/io/FileOutputStream:close	()V
    //   232: aload_1
    //   233: athrow
    //   234: astore_2
    //   235: goto -3 -> 232
    //   238: aload_2
    //   239: ldc -93
    //   241: invokevirtual 68	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   244: aload_2
    //   245: lload 7
    //   247: lload 5
    //   249: lsub
    //   250: invokevirtual 81	java/io/PrintWriter:print	(J)V
    //   253: aload_2
    //   254: ldc -91
    //   256: invokevirtual 74	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   259: goto -119 -> 140
    //   262: astore_3
    //   263: aload_1
    //   264: astore_2
    //   265: aload_3
    //   266: astore_1
    //   267: goto -43 -> 224
    //   270: astore 9
    //   272: aload_1
    //   273: astore_3
    //   274: aload 9
    //   276: astore_1
    //   277: goto -80 -> 197
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	280	0	this	DiskStatsService
    //   0	280	1	paramFileDescriptor	java.io.FileDescriptor
    //   0	280	2	paramPrintWriter	PrintWriter
    //   0	280	3	paramArrayOfString	String[]
    //   19	23	4	i	int
    //   71	177	5	l1	long
    //   104	142	7	l2	long
    //   63	154	9	localObject1	Object
    //   270	5	9	localIOException	java.io.IOException
    //   66	32	10	localObject2	Object
    //   58	57	11	localFile	File
    //   16	69	12	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   93	97	189	java/io/IOException
    //   73	83	193	java/io/IOException
    //   204	208	214	java/io/IOException
    //   73	83	221	finally
    //   228	232	234	java/io/IOException
    //   83	89	262	finally
    //   83	89	270	java/io/IOException
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/DiskStatsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */