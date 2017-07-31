package com.android.server.usage;

import android.app.usage.TimeSparseArray;
import android.os.Build.VERSION;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.TimeUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class UsageStatsDatabase
{
  static final int BACKUP_VERSION = 1;
  private static final String BAK_SUFFIX = ".bak";
  private static final String CHECKED_IN_SUFFIX = "-c";
  private static final int CURRENT_VERSION = 3;
  private static final boolean DEBUG = false;
  static final String KEY_USAGE_STATS = "usage_stats";
  private static final String TAG = "UsageStatsDatabase";
  private final UnixCalendar mCal;
  private boolean mFirstUpdate;
  private final File[] mIntervalDirs;
  private final Object mLock = new Object();
  private boolean mNewUpdate;
  private final TimeSparseArray<AtomicFile>[] mSortedStatFiles;
  private final File mVersionFile;
  
  public UsageStatsDatabase(File paramFile)
  {
    this.mIntervalDirs = new File[] { new File(paramFile, "daily"), new File(paramFile, "weekly"), new File(paramFile, "monthly"), new File(paramFile, "yearly") };
    this.mVersionFile = new File(paramFile, "version");
    this.mSortedStatFiles = new TimeSparseArray[this.mIntervalDirs.length];
    this.mCal = new UnixCalendar(0L);
  }
  
  /* Error */
  private void checkVersionAndBuildLocked()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aconst_null
    //   4: astore 7
    //   6: aload_0
    //   7: invokespecial 95	com/android/server/usage/UsageStatsDatabase:getBuildFingerprint	()Ljava/lang/String;
    //   10: astore 8
    //   12: aload_0
    //   13: iconst_1
    //   14: putfield 97	com/android/server/usage/UsageStatsDatabase:mFirstUpdate	Z
    //   17: aload_0
    //   18: iconst_1
    //   19: putfield 99	com/android/server/usage/UsageStatsDatabase:mNewUpdate	Z
    //   22: aconst_null
    //   23: astore_3
    //   24: aconst_null
    //   25: astore 4
    //   27: new 101	java/io/BufferedReader
    //   30: dup
    //   31: new 103	java/io/FileReader
    //   34: dup
    //   35: aload_0
    //   36: getfield 72	com/android/server/usage/UsageStatsDatabase:mVersionFile	Ljava/io/File;
    //   39: invokespecial 105	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   42: invokespecial 108	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   45: astore_2
    //   46: aload_2
    //   47: invokevirtual 111	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   50: invokestatic 117	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   53: istore_1
    //   54: aload_2
    //   55: invokevirtual 111	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   58: astore_3
    //   59: aload_3
    //   60: ifnull +8 -> 68
    //   63: aload_0
    //   64: iconst_0
    //   65: putfield 97	com/android/server/usage/UsageStatsDatabase:mFirstUpdate	Z
    //   68: aload 8
    //   70: aload_3
    //   71: invokevirtual 123	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   74: ifeq +8 -> 82
    //   77: aload_0
    //   78: iconst_0
    //   79: putfield 99	com/android/server/usage/UsageStatsDatabase:mNewUpdate	Z
    //   82: aload_2
    //   83: ifnull +7 -> 90
    //   86: aload_2
    //   87: invokevirtual 126	java/io/BufferedReader:close	()V
    //   90: aconst_null
    //   91: astore_2
    //   92: aload_2
    //   93: ifnull +160 -> 253
    //   96: aload_2
    //   97: athrow
    //   98: astore_2
    //   99: iconst_0
    //   100: istore_1
    //   101: iload_1
    //   102: iconst_3
    //   103: if_icmpeq +42 -> 145
    //   106: ldc 33
    //   108: new 128	java/lang/StringBuilder
    //   111: dup
    //   112: invokespecial 129	java/lang/StringBuilder:<init>	()V
    //   115: ldc -125
    //   117: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: iload_1
    //   121: invokevirtual 138	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   124: ldc -116
    //   126: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: iconst_3
    //   130: invokevirtual 138	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   133: invokevirtual 143	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   136: invokestatic 149	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   139: pop
    //   140: aload_0
    //   141: iload_1
    //   142: invokespecial 153	com/android/server/usage/UsageStatsDatabase:doUpgradeLocked	(I)V
    //   145: iload_1
    //   146: iconst_3
    //   147: if_icmpne +10 -> 157
    //   150: aload_0
    //   151: getfield 99	com/android/server/usage/UsageStatsDatabase:mNewUpdate	Z
    //   154: ifeq +220 -> 374
    //   157: aconst_null
    //   158: astore_3
    //   159: aconst_null
    //   160: astore 4
    //   162: new 155	java/io/BufferedWriter
    //   165: dup
    //   166: new 157	java/io/FileWriter
    //   169: dup
    //   170: aload_0
    //   171: getfield 72	com/android/server/usage/UsageStatsDatabase:mVersionFile	Ljava/io/File;
    //   174: invokespecial 158	java/io/FileWriter:<init>	(Ljava/io/File;)V
    //   177: invokespecial 161	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   180: astore_2
    //   181: aload_2
    //   182: iconst_3
    //   183: invokestatic 164	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   186: invokevirtual 168	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   189: aload_2
    //   190: ldc -86
    //   192: invokevirtual 168	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   195: aload_2
    //   196: aload 8
    //   198: invokevirtual 168	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   201: aload_2
    //   202: ldc -86
    //   204: invokevirtual 168	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   207: aload_2
    //   208: invokevirtual 173	java/io/BufferedWriter:flush	()V
    //   211: aload 7
    //   213: astore_3
    //   214: aload_2
    //   215: ifnull +10 -> 225
    //   218: aload_2
    //   219: invokevirtual 174	java/io/BufferedWriter:close	()V
    //   222: aload 7
    //   224: astore_3
    //   225: aload_3
    //   226: ifnull +148 -> 374
    //   229: aload_3
    //   230: athrow
    //   231: astore_2
    //   232: ldc 33
    //   234: ldc -80
    //   236: invokestatic 179	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   239: pop
    //   240: new 181	java/lang/RuntimeException
    //   243: dup
    //   244: aload_2
    //   245: invokespecial 184	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   248: athrow
    //   249: astore_2
    //   250: goto -158 -> 92
    //   253: goto -152 -> 101
    //   256: astore_2
    //   257: aload_2
    //   258: athrow
    //   259: astore_3
    //   260: aload_2
    //   261: astore 5
    //   263: aload 4
    //   265: ifnull +11 -> 276
    //   268: aload 4
    //   270: invokevirtual 126	java/io/BufferedReader:close	()V
    //   273: aload_2
    //   274: astore 5
    //   276: aload 5
    //   278: ifnull +27 -> 305
    //   281: aload 5
    //   283: athrow
    //   284: aload_2
    //   285: astore 5
    //   287: aload_2
    //   288: aload 4
    //   290: if_acmpeq -14 -> 276
    //   293: aload_2
    //   294: aload 4
    //   296: invokevirtual 187	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   299: aload_2
    //   300: astore 5
    //   302: goto -26 -> 276
    //   305: aload_3
    //   306: athrow
    //   307: astore_3
    //   308: goto -83 -> 225
    //   311: astore_3
    //   312: aload 4
    //   314: astore_2
    //   315: aload_3
    //   316: athrow
    //   317: astore 5
    //   319: aload_3
    //   320: astore 4
    //   322: aload 5
    //   324: astore_3
    //   325: aload 4
    //   327: astore 5
    //   329: aload_2
    //   330: ifnull +11 -> 341
    //   333: aload_2
    //   334: invokevirtual 174	java/io/BufferedWriter:close	()V
    //   337: aload 4
    //   339: astore 5
    //   341: aload 5
    //   343: ifnull +29 -> 372
    //   346: aload 5
    //   348: athrow
    //   349: aload 4
    //   351: astore 5
    //   353: aload 4
    //   355: aload_2
    //   356: if_acmpeq -15 -> 341
    //   359: aload 4
    //   361: aload_2
    //   362: invokevirtual 187	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   365: aload 4
    //   367: astore 5
    //   369: goto -28 -> 341
    //   372: aload_3
    //   373: athrow
    //   374: return
    //   375: astore 4
    //   377: aload_3
    //   378: astore_2
    //   379: aload 4
    //   381: astore_3
    //   382: aload 6
    //   384: astore 4
    //   386: goto -61 -> 325
    //   389: astore_3
    //   390: aload 6
    //   392: astore 4
    //   394: goto -69 -> 325
    //   397: astore_3
    //   398: goto -83 -> 315
    //   401: astore 5
    //   403: aconst_null
    //   404: astore_2
    //   405: aload_3
    //   406: astore 4
    //   408: aload 5
    //   410: astore_3
    //   411: goto -151 -> 260
    //   414: astore_3
    //   415: aconst_null
    //   416: astore 5
    //   418: aload_2
    //   419: astore 4
    //   421: aload 5
    //   423: astore_2
    //   424: goto -164 -> 260
    //   427: astore_3
    //   428: aload_2
    //   429: astore 4
    //   431: aload_3
    //   432: astore_2
    //   433: goto -176 -> 257
    //   436: astore_2
    //   437: goto -338 -> 99
    //   440: astore 4
    //   442: aload_2
    //   443: ifnonnull -159 -> 284
    //   446: aload 4
    //   448: astore 5
    //   450: goto -174 -> 276
    //   453: astore_2
    //   454: goto -222 -> 232
    //   457: astore_2
    //   458: aload 4
    //   460: ifnonnull -111 -> 349
    //   463: aload_2
    //   464: astore 5
    //   466: goto -125 -> 341
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	469	0	this	UsageStatsDatabase
    //   53	95	1	i	int
    //   45	52	2	localBufferedReader	java.io.BufferedReader
    //   98	1	2	localNumberFormatException1	NumberFormatException
    //   180	39	2	localBufferedWriter	java.io.BufferedWriter
    //   231	14	2	localIOException1	IOException
    //   249	1	2	localThrowable1	Throwable
    //   256	44	2	localThrowable2	Throwable
    //   314	119	2	localObject1	Object
    //   436	7	2	localNumberFormatException2	NumberFormatException
    //   453	1	2	localIOException2	IOException
    //   457	7	2	localThrowable3	Throwable
    //   23	207	3	localObject2	Object
    //   259	47	3	localObject3	Object
    //   307	1	3	localThrowable4	Throwable
    //   311	9	3	localThrowable5	Throwable
    //   324	58	3	localObject4	Object
    //   389	1	3	localObject5	Object
    //   397	9	3	localThrowable6	Throwable
    //   410	1	3	localObject6	Object
    //   414	1	3	localObject7	Object
    //   427	5	3	localThrowable7	Throwable
    //   25	341	4	localThrowable8	Throwable
    //   375	5	4	localObject8	Object
    //   384	46	4	localObject9	Object
    //   440	19	4	localThrowable9	Throwable
    //   261	40	5	localThrowable10	Throwable
    //   317	6	5	localObject10	Object
    //   327	41	5	localThrowable11	Throwable
    //   401	8	5	localObject11	Object
    //   416	49	5	localThrowable12	Throwable
    //   1	390	6	localObject12	Object
    //   4	219	7	localObject13	Object
    //   10	187	8	str	String
    // Exception table:
    //   from	to	target	type
    //   86	90	98	java/lang/NumberFormatException
    //   86	90	98	java/io/IOException
    //   96	98	98	java/lang/NumberFormatException
    //   96	98	98	java/io/IOException
    //   218	222	231	java/io/IOException
    //   229	231	231	java/io/IOException
    //   86	90	249	java/lang/Throwable
    //   27	46	256	java/lang/Throwable
    //   257	259	259	finally
    //   218	222	307	java/lang/Throwable
    //   162	181	311	java/lang/Throwable
    //   315	317	317	finally
    //   162	181	375	finally
    //   181	211	389	finally
    //   181	211	397	java/lang/Throwable
    //   27	46	401	finally
    //   46	59	414	finally
    //   63	68	414	finally
    //   68	82	414	finally
    //   46	59	427	java/lang/Throwable
    //   63	68	427	java/lang/Throwable
    //   68	82	427	java/lang/Throwable
    //   268	273	436	java/lang/NumberFormatException
    //   268	273	436	java/io/IOException
    //   281	284	436	java/lang/NumberFormatException
    //   281	284	436	java/io/IOException
    //   293	299	436	java/lang/NumberFormatException
    //   293	299	436	java/io/IOException
    //   305	307	436	java/lang/NumberFormatException
    //   305	307	436	java/io/IOException
    //   268	273	440	java/lang/Throwable
    //   333	337	453	java/io/IOException
    //   346	349	453	java/io/IOException
    //   359	365	453	java/io/IOException
    //   372	374	453	java/io/IOException
    //   333	337	457	java/lang/Throwable
  }
  
  private static void deleteDirectory(File paramFile)
  {
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile != null)
    {
      int i = 0;
      int j = arrayOfFile.length;
      if (i < j)
      {
        File localFile = arrayOfFile[i];
        if (!localFile.isDirectory()) {
          localFile.delete();
        }
        for (;;)
        {
          i += 1;
          break;
          deleteDirectory(localFile);
        }
      }
    }
    paramFile.delete();
  }
  
  private static void deleteDirectoryContents(File paramFile)
  {
    paramFile = paramFile.listFiles();
    int i = 0;
    int j = paramFile.length;
    while (i < j)
    {
      deleteDirectory(paramFile[i]);
      i += 1;
    }
  }
  
  private static IntervalStats deserializeIntervalStats(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = new DataInputStream(new ByteArrayInputStream(paramArrayOfByte));
    IntervalStats localIntervalStats = new IntervalStats();
    try
    {
      localIntervalStats.beginTime = paramArrayOfByte.readLong();
      UsageStatsXml.read(paramArrayOfByte, localIntervalStats);
      return localIntervalStats;
    }
    catch (IOException paramArrayOfByte)
    {
      Slog.d("UsageStatsDatabase", "DeSerializing IntervalStats Failed", paramArrayOfByte);
    }
    return null;
  }
  
  private void doUpgradeLocked(int paramInt)
  {
    if (paramInt < 2)
    {
      Slog.i("UsageStatsDatabase", "Deleting all usage stats files");
      paramInt = 0;
      while (paramInt < this.mIntervalDirs.length)
      {
        File[] arrayOfFile = this.mIntervalDirs[paramInt].listFiles();
        if (arrayOfFile != null)
        {
          int i = 0;
          int j = arrayOfFile.length;
          while (i < j)
          {
            arrayOfFile[i].delete();
            i += 1;
          }
        }
        paramInt += 1;
      }
    }
  }
  
  private String getBuildFingerprint()
  {
    return Build.VERSION.RELEASE + ";" + Build.VERSION.CODENAME + ";" + Build.VERSION.INCREMENTAL;
  }
  
  private static byte[] getIntervalStatsBytes(DataInputStream paramDataInputStream)
    throws IOException
  {
    int i = paramDataInputStream.readInt();
    byte[] arrayOfByte = new byte[i];
    paramDataInputStream.read(arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  private void indexFilesLocked()
  {
    FilenameFilter local1 = new FilenameFilter()
    {
      public boolean accept(File paramAnonymousFile, String paramAnonymousString)
      {
        return !paramAnonymousString.endsWith(".bak");
      }
    };
    int i = 0;
    while (i < this.mSortedStatFiles.length)
    {
      if (this.mSortedStatFiles[i] == null) {
        this.mSortedStatFiles[i] = new TimeSparseArray();
      }
      for (;;)
      {
        File[] arrayOfFile = this.mIntervalDirs[i].listFiles(local1);
        if (arrayOfFile != null)
        {
          int j = 0;
          int k = arrayOfFile.length;
          label67:
          if (j < k)
          {
            File localFile = arrayOfFile[j];
            AtomicFile localAtomicFile = new AtomicFile(localFile);
            try
            {
              this.mSortedStatFiles[i].put(UsageStatsXml.parseBeginTime(localAtomicFile), localAtomicFile);
              j += 1;
              break label67;
              this.mSortedStatFiles[i].clear();
            }
            catch (IOException localIOException)
            {
              for (;;)
              {
                Slog.e("UsageStatsDatabase", "failed to index file: " + localFile, localIOException);
              }
            }
          }
        }
      }
      i += 1;
    }
  }
  
  private IntervalStats mergeStats(IntervalStats paramIntervalStats1, IntervalStats paramIntervalStats2)
  {
    if (paramIntervalStats2 == null) {
      return paramIntervalStats1;
    }
    if (paramIntervalStats1 == null) {
      return null;
    }
    paramIntervalStats1.activeConfiguration = paramIntervalStats2.activeConfiguration;
    paramIntervalStats1.configurations.putAll(paramIntervalStats2.configurations);
    paramIntervalStats1.events = paramIntervalStats2.events;
    return paramIntervalStats1;
  }
  
  private static void pruneFilesOlderThan(File paramFile, long paramLong)
  {
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile != null)
    {
      int j = arrayOfFile.length;
      int i = 0;
      for (;;)
      {
        if (i < j)
        {
          paramFile = arrayOfFile[i];
          String str = paramFile.getPath();
          if (str.endsWith(".bak")) {
            paramFile = new File(str.substring(0, str.length() - ".bak".length()));
          }
          try
          {
            l = UsageStatsXml.parseBeginTime(paramFile);
            if (l < paramLong) {
              new AtomicFile(paramFile).delete();
            }
            i += 1;
          }
          catch (IOException localIOException)
          {
            for (;;)
            {
              long l = 0L;
            }
          }
        }
      }
    }
  }
  
  private static void sanitizeIntervalStatsForBackup(IntervalStats paramIntervalStats)
  {
    if (paramIntervalStats == null) {
      return;
    }
    paramIntervalStats.activeConfiguration = null;
    paramIntervalStats.configurations.clear();
    if (paramIntervalStats.events != null) {
      paramIntervalStats.events.clear();
    }
  }
  
  private static byte[] serializeIntervalStats(IntervalStats paramIntervalStats)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    try
    {
      localDataOutputStream.writeLong(paramIntervalStats.beginTime);
      UsageStatsXml.write(localDataOutputStream, paramIntervalStats);
      return localByteArrayOutputStream.toByteArray();
    }
    catch (IOException paramIntervalStats)
    {
      for (;;)
      {
        Slog.d("UsageStatsDatabase", "Serializing IntervalStats Failed", paramIntervalStats);
        localByteArrayOutputStream.reset();
      }
    }
  }
  
  private void writeIntervalStatsToStream(DataOutputStream paramDataOutputStream, AtomicFile paramAtomicFile)
    throws IOException
  {
    IntervalStats localIntervalStats = new IntervalStats();
    try
    {
      UsageStatsXml.read(paramAtomicFile, localIntervalStats);
      sanitizeIntervalStatsForBackup(localIntervalStats);
      paramAtomicFile = serializeIntervalStats(localIntervalStats);
      paramDataOutputStream.writeInt(paramAtomicFile.length);
      paramDataOutputStream.write(paramAtomicFile);
      return;
    }
    catch (IOException paramAtomicFile)
    {
      Slog.e("UsageStatsDatabase", "Failed to read usage stats file", paramAtomicFile);
      paramDataOutputStream.writeInt(0);
    }
  }
  
  void applyRestoredPayload(String paramString, byte[] paramArrayOfByte)
  {
    synchronized (this.mLock)
    {
      IntervalStats localIntervalStats1;
      IntervalStats localIntervalStats2;
      IntervalStats localIntervalStats3;
      if ("usage_stats".equals(paramString))
      {
        paramString = getLatestUsageStats(0);
        localIntervalStats1 = getLatestUsageStats(1);
        localIntervalStats2 = getLatestUsageStats(2);
        localIntervalStats3 = getLatestUsageStats(3);
      }
      try
      {
        paramArrayOfByte = new DataInputStream(new ByteArrayInputStream(paramArrayOfByte));
        int i = paramArrayOfByte.readInt();
        if ((i < 1) || (i > 1))
        {
          indexFilesLocked();
          return;
        }
        i = 0;
        while (i < this.mIntervalDirs.length)
        {
          deleteDirectoryContents(this.mIntervalDirs[i]);
          i += 1;
        }
        int j = paramArrayOfByte.readInt();
        i = 0;
        while (i < j)
        {
          putUsageStats(0, mergeStats(deserializeIntervalStats(getIntervalStatsBytes(paramArrayOfByte)), paramString));
          i += 1;
        }
        j = paramArrayOfByte.readInt();
        i = 0;
        while (i < j)
        {
          putUsageStats(1, mergeStats(deserializeIntervalStats(getIntervalStatsBytes(paramArrayOfByte)), localIntervalStats1));
          i += 1;
        }
        j = paramArrayOfByte.readInt();
        i = 0;
        while (i < j)
        {
          putUsageStats(2, mergeStats(deserializeIntervalStats(getIntervalStatsBytes(paramArrayOfByte)), localIntervalStats2));
          i += 1;
        }
        j = paramArrayOfByte.readInt();
        i = 0;
        while (i < j)
        {
          putUsageStats(3, mergeStats(deserializeIntervalStats(getIntervalStatsBytes(paramArrayOfByte)), localIntervalStats3));
          i += 1;
        }
      }
      catch (IOException paramString)
      {
        for (;;)
        {
          Slog.d("UsageStatsDatabase", "Failed to read data from input stream", paramString);
          indexFilesLocked();
        }
        paramString = finally;
        throw paramString;
      }
      finally
      {
        indexFilesLocked();
      }
      return;
    }
  }
  
  public boolean checkinDailyFiles(CheckinAction paramCheckinAction)
  {
    synchronized (this.mLock)
    {
      TimeSparseArray localTimeSparseArray = this.mSortedStatFiles[0];
      int k = localTimeSparseArray.size();
      int j = -1;
      int i = 0;
      boolean bool;
      while (i < k - 1)
      {
        bool = ((AtomicFile)localTimeSparseArray.valueAt(i)).getBaseFile().getPath().endsWith("-c");
        if (bool) {
          j = i;
        }
        i += 1;
      }
      i = j + 1;
      if (i == k - 1) {
        return true;
      }
      Object localObject2;
      try
      {
        localObject2 = new IntervalStats();
        j = i;
        while (j < k - 1)
        {
          UsageStatsXml.read((AtomicFile)localTimeSparseArray.valueAt(j), (IntervalStats)localObject2);
          bool = paramCheckinAction.checkin((IntervalStats)localObject2);
          if (!bool) {
            return false;
          }
          j += 1;
        }
        if (i >= k - 1) {
          break label294;
        }
      }
      catch (IOException paramCheckinAction)
      {
        Slog.e("UsageStatsDatabase", "Failed to check-in", paramCheckinAction);
        return false;
      }
      for (;;)
      {
        paramCheckinAction = (AtomicFile)localTimeSparseArray.valueAt(i);
        localObject2 = new File(paramCheckinAction.getBaseFile().getPath() + "-c");
        if (!paramCheckinAction.getBaseFile().renameTo((File)localObject2))
        {
          Slog.e("UsageStatsDatabase", "Failed to mark file " + paramCheckinAction.getBaseFile().getPath() + " as checked-in");
          return true;
        }
        localTimeSparseArray.setValueAt(i, new AtomicFile((File)localObject2));
        i += 1;
      }
      label294:
      return true;
    }
  }
  
  public int findBestFitBucket(long paramLong1, long paramLong2)
  {
    Object localObject1 = this.mLock;
    int j = -1;
    paramLong2 = Long.MAX_VALUE;
    try
    {
      int i = this.mSortedStatFiles.length - 1;
      while (i >= 0)
      {
        int m = this.mSortedStatFiles[i].closestIndexOnOrBefore(paramLong1);
        int n = this.mSortedStatFiles[i].size();
        int k = j;
        long l1 = paramLong2;
        if (m >= 0)
        {
          k = j;
          l1 = paramLong2;
          if (m < n)
          {
            long l2 = Math.abs(this.mSortedStatFiles[i].keyAt(m) - paramLong1);
            k = j;
            l1 = paramLong2;
            if (l2 < paramLong2)
            {
              l1 = l2;
              k = i;
            }
          }
        }
        i -= 1;
        j = k;
        paramLong2 = l1;
      }
      return j;
    }
    finally {}
  }
  
  byte[] getBackupPayload(String paramString)
  {
    synchronized (this.mLock)
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      if ("usage_stats".equals(paramString))
      {
        prune(System.currentTimeMillis());
        paramString = new DataOutputStream(localByteArrayOutputStream);
        try
        {
          paramString.writeInt(1);
          paramString.writeInt(this.mSortedStatFiles[0].size());
          int i = 0;
          while (i < this.mSortedStatFiles[0].size())
          {
            writeIntervalStatsToStream(paramString, (AtomicFile)this.mSortedStatFiles[0].valueAt(i));
            i += 1;
          }
          paramString.writeInt(this.mSortedStatFiles[1].size());
          i = 0;
          while (i < this.mSortedStatFiles[1].size())
          {
            writeIntervalStatsToStream(paramString, (AtomicFile)this.mSortedStatFiles[1].valueAt(i));
            i += 1;
          }
          paramString.writeInt(this.mSortedStatFiles[2].size());
          i = 0;
          while (i < this.mSortedStatFiles[2].size())
          {
            writeIntervalStatsToStream(paramString, (AtomicFile)this.mSortedStatFiles[2].valueAt(i));
            i += 1;
          }
          paramString.writeInt(this.mSortedStatFiles[3].size());
          i = 0;
          while (i < this.mSortedStatFiles[3].size())
          {
            writeIntervalStatsToStream(paramString, (AtomicFile)this.mSortedStatFiles[3].valueAt(i));
            i += 1;
          }
          paramString = localByteArrayOutputStream.toByteArray();
        }
        catch (IOException paramString)
        {
          Slog.d("UsageStatsDatabase", "Failed to write data to output stream", paramString);
          localByteArrayOutputStream.reset();
        }
      }
      return paramString;
    }
  }
  
  public IntervalStats getLatestUsageStats(int paramInt)
  {
    Object localObject1 = this.mLock;
    if (paramInt >= 0) {}
    int i;
    AtomicFile localAtomicFile;
    IntervalStats localIntervalStats;
    try
    {
      if (paramInt >= this.mIntervalDirs.length) {
        throw new IllegalArgumentException("Bad interval type " + paramInt);
      }
    }
    finally
    {
      throw ((Throwable)localObject2);
      i = this.mSortedStatFiles[paramInt].size();
      if (i != 0) {}
    }
    return null;
  }
  
  public void init(long paramLong)
  {
    int j;
    Object localObject3;
    for (;;)
    {
      synchronized (this.mLock)
      {
        File[] arrayOfFile = this.mIntervalDirs;
        j = arrayOfFile.length;
        i = 0;
        if (i >= j) {
          break;
        }
        localObject3 = arrayOfFile[i];
        ((File)localObject3).mkdirs();
        if (!((File)localObject3).exists()) {
          throw new IllegalStateException("Failed to create directory " + ((File)localObject3).getAbsolutePath());
        }
      }
      i += 1;
    }
    checkVersionAndBuildLocked();
    indexFilesLocked();
    TimeSparseArray[] arrayOfTimeSparseArray = this.mSortedStatFiles;
    int m = arrayOfTimeSparseArray.length;
    int i = 0;
    int n;
    if (i < m)
    {
      localObject3 = arrayOfTimeSparseArray[i];
      j = ((TimeSparseArray)localObject3).closestIndexOnOrAfter(paramLong);
      if (j < 0) {
        break label211;
      }
      n = ((TimeSparseArray)localObject3).size();
      int k = j;
      while (k < n)
      {
        ((AtomicFile)((TimeSparseArray)localObject3).valueAt(k)).delete();
        k += 1;
      }
    }
    for (;;)
    {
      if (j < n)
      {
        ((TimeSparseArray)localObject3).removeAt(j);
        j += 1;
      }
      else
      {
        label211:
        i += 1;
        break;
      }
    }
  }
  
  boolean isFirstUpdate()
  {
    return this.mFirstUpdate;
  }
  
  boolean isNewUpdate()
  {
    return this.mNewUpdate;
  }
  
  public void onTimeChanged(long paramLong)
  {
    for (;;)
    {
      StringBuilder localStringBuilder;
      int k;
      int m;
      int i;
      TimeSparseArray localTimeSparseArray;
      AtomicFile localAtomicFile;
      long l;
      synchronized (this.mLock)
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("Time changed by ");
        TimeUtils.formatDuration(paramLong, localStringBuilder);
        localStringBuilder.append(".");
        k = 0;
        m = 0;
        TimeSparseArray[] arrayOfTimeSparseArray = this.mSortedStatFiles;
        i = 0;
        int n = arrayOfTimeSparseArray.length;
        if (i < n)
        {
          localTimeSparseArray = arrayOfTimeSparseArray[i];
          int i1 = localTimeSparseArray.size();
          int j = 0;
          if (j < i1)
          {
            localAtomicFile = (AtomicFile)localTimeSparseArray.valueAt(j);
            l = localTimeSparseArray.keyAt(j) + paramLong;
            if (l < 0L)
            {
              k += 1;
              localAtomicFile.delete();
              j += 1;
            }
          }
        }
      }
      try
      {
        localAtomicFile.openRead().close();
        String str = Long.toString(l);
        Object localObject1 = str;
        if (localAtomicFile.getBaseFile().getName().endsWith("-c")) {
          localObject1 = str + "-c";
        }
        localObject1 = new File(localAtomicFile.getBaseFile().getParentFile(), (String)localObject1);
        m += 1;
        localAtomicFile.getBaseFile().renameTo((File)localObject1);
        continue;
        localObject2 = finally;
        throw ((Throwable)localObject2);
        localTimeSparseArray.clear();
        i += 1;
        continue;
        localStringBuilder.append(" files deleted: ").append(k);
        localStringBuilder.append(" files moved: ").append(m);
        Slog.i("UsageStatsDatabase", localStringBuilder.toString());
        indexFilesLocked();
        return;
      }
      catch (IOException localIOException)
      {
        for (;;) {}
      }
    }
  }
  
  public void prune(long paramLong)
  {
    synchronized (this.mLock)
    {
      this.mCal.setTimeInMillis(paramLong);
      this.mCal.addYears(-3);
      pruneFilesOlderThan(this.mIntervalDirs[3], this.mCal.getTimeInMillis());
      this.mCal.setTimeInMillis(paramLong);
      this.mCal.addMonths(-6);
      pruneFilesOlderThan(this.mIntervalDirs[2], this.mCal.getTimeInMillis());
      this.mCal.setTimeInMillis(paramLong);
      this.mCal.addWeeks(-4);
      pruneFilesOlderThan(this.mIntervalDirs[1], this.mCal.getTimeInMillis());
      this.mCal.setTimeInMillis(paramLong);
      this.mCal.addDays(-7);
      pruneFilesOlderThan(this.mIntervalDirs[0], this.mCal.getTimeInMillis());
      indexFilesLocked();
      return;
    }
  }
  
  public void putUsageStats(int paramInt, IntervalStats paramIntervalStats)
    throws IOException
  {
    if (paramIntervalStats == null) {
      return;
    }
    Object localObject = this.mLock;
    if (paramInt >= 0) {}
    AtomicFile localAtomicFile1;
    try
    {
      if (paramInt >= this.mIntervalDirs.length) {
        throw new IllegalArgumentException("Bad interval type " + paramInt);
      }
    }
    finally
    {
      throw paramIntervalStats;
      AtomicFile localAtomicFile2 = (AtomicFile)this.mSortedStatFiles[paramInt].get(paramIntervalStats.beginTime);
      localAtomicFile1 = localAtomicFile2;
      if (localAtomicFile2 == null)
      {
        localAtomicFile1 = new AtomicFile(new File(this.mIntervalDirs[paramInt], Long.toString(paramIntervalStats.beginTime)));
        this.mSortedStatFiles[paramInt].put(paramIntervalStats.beginTime, localAtomicFile1);
      }
      UsageStatsXml.write(localAtomicFile1, paramIntervalStats);
    }
  }
  
  public <T> List<T> queryUsageStats(int paramInt, long paramLong1, long paramLong2, StatCombiner<T> paramStatCombiner)
  {
    Object localObject = this.mLock;
    if (paramInt >= 0) {}
    IntervalStats localIntervalStats;
    ArrayList localArrayList;
    AtomicFile localAtomicFile;
    try
    {
      if (paramInt >= this.mIntervalDirs.length) {
        throw new IllegalArgumentException("Bad interval type " + paramInt);
      }
    }
    finally
    {
      throw paramStatCombiner;
      TimeSparseArray localTimeSparseArray = this.mSortedStatFiles[paramInt];
      if (paramLong2 <= paramLong1) {
        return null;
      }
      int i = localTimeSparseArray.closestIndexOnOrBefore(paramLong1);
      paramInt = i;
      if (i < 0) {
        paramInt = 0;
      }
      int j = localTimeSparseArray.closestIndexOnOrBefore(paramLong2);
      if (j < 0) {
        return null;
      }
      long l = localTimeSparseArray.keyAt(j);
      i = j;
      if (l == paramLong2)
      {
        j -= 1;
        i = j;
        if (j < 0) {
          return null;
        }
      }
      localIntervalStats = new IntervalStats();
      localArrayList = new ArrayList();
      if (paramInt <= i) {
        localAtomicFile = (AtomicFile)localTimeSparseArray.valueAt(paramInt);
      }
    }
    return localArrayList;
  }
  
  public static abstract interface CheckinAction
  {
    public abstract boolean checkin(IntervalStats paramIntervalStats);
  }
  
  static abstract interface StatCombiner<T>
  {
    public abstract void combine(IntervalStats paramIntervalStats, boolean paramBoolean, List<T> paramList);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usage/UsageStatsDatabase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */