package com.android.server.usage;

import android.os.Environment;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.IndentingPrintWriter;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class AppIdleHistory
{
  static final String APP_IDLE_FILENAME = "app_idle_stats.xml";
  private static final String ATTR_ELAPSED_IDLE = "elapsedIdleTime";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_SCREEN_IDLE = "screenIdleTime";
  private static final int FLAG_LAST_STATE = 2;
  private static final int FLAG_PARTIAL_ACTIVE = 1;
  private static final int HISTORY_SIZE = 100;
  private static final long ONE_MINUTE = 60000L;
  private static final long PERIOD_DURATION = 3600000L;
  private static final String TAG = "AppIdleHistory";
  private static final String TAG_PACKAGE = "package";
  private static final String TAG_PACKAGES = "packages";
  private long mElapsedDuration;
  private long mElapsedSnapshot;
  private long mElapsedTimeThreshold;
  private SparseArray<ArrayMap<String, PackageHistory>> mIdleHistory = new SparseArray();
  private long mLastPeriod = 0L;
  private boolean mScreenOn;
  private long mScreenOnDuration;
  private long mScreenOnSnapshot;
  private long mScreenOnTimeThreshold;
  private final File mStorageDir;
  
  AppIdleHistory(long paramLong)
  {
    this(Environment.getDataSystemDirectory(), paramLong);
  }
  
  AppIdleHistory(File paramFile, long paramLong)
  {
    this.mElapsedSnapshot = paramLong;
    this.mScreenOnSnapshot = paramLong;
    this.mStorageDir = paramFile;
    readScreenOnTimeLocked();
  }
  
  private long getElapsedTimeLocked(long paramLong)
  {
    return paramLong - this.mElapsedSnapshot + this.mElapsedDuration;
  }
  
  private PackageHistory getPackageHistoryLocked(ArrayMap<String, PackageHistory> paramArrayMap, String paramString, long paramLong)
  {
    PackageHistory localPackageHistory2 = (PackageHistory)paramArrayMap.get(paramString);
    PackageHistory localPackageHistory1 = localPackageHistory2;
    if (localPackageHistory2 == null)
    {
      localPackageHistory1 = new PackageHistory(null);
      localPackageHistory1.lastUsedElapsedTime = getElapsedTimeLocked(paramLong);
      localPackageHistory1.lastUsedScreenTime = getScreenOnTimeLocked(paramLong);
      paramArrayMap.put(paramString, localPackageHistory1);
    }
    return localPackageHistory1;
  }
  
  private File getUserFile(int paramInt)
  {
    return new File(new File(new File(this.mStorageDir, "users"), Integer.toString(paramInt)), "app_idle_stats.xml");
  }
  
  private ArrayMap<String, PackageHistory> getUserHistoryLocked(int paramInt)
  {
    ArrayMap localArrayMap2 = (ArrayMap)this.mIdleHistory.get(paramInt);
    ArrayMap localArrayMap1 = localArrayMap2;
    if (localArrayMap2 == null)
    {
      localArrayMap1 = new ArrayMap();
      this.mIdleHistory.put(paramInt, localArrayMap1);
      readAppIdleTimesLocked(paramInt, localArrayMap1);
    }
    return localArrayMap1;
  }
  
  private boolean hasPassedThresholdsLocked(PackageHistory paramPackageHistory, long paramLong)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramPackageHistory.lastUsedScreenTime <= getScreenOnTimeLocked(paramLong) - this.mScreenOnTimeThreshold)
    {
      bool1 = bool2;
      if (paramPackageHistory.lastUsedElapsedTime <= getElapsedTimeLocked(paramLong) - this.mElapsedTimeThreshold) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  /* Error */
  private void readAppIdleTimesLocked(int paramInt, ArrayMap<String, PackageHistory> paramArrayMap)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aconst_null
    //   4: astore 5
    //   6: new 161	android/util/AtomicFile
    //   9: dup
    //   10: aload_0
    //   11: iload_1
    //   12: invokespecial 163	com/android/server/usage/AppIdleHistory:getUserFile	(I)Ljava/io/File;
    //   15: invokespecial 166	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   18: invokevirtual 170	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   21: astore 7
    //   23: aload 7
    //   25: astore 5
    //   27: aload 7
    //   29: astore 6
    //   31: invokestatic 176	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   34: astore 8
    //   36: aload 7
    //   38: astore 5
    //   40: aload 7
    //   42: astore 6
    //   44: aload 8
    //   46: aload 7
    //   48: getstatic 182	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   51: invokevirtual 187	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   54: invokeinterface 193 3 0
    //   59: aload 7
    //   61: astore 5
    //   63: aload 7
    //   65: astore 6
    //   67: aload 8
    //   69: invokeinterface 197 1 0
    //   74: istore_3
    //   75: iload_3
    //   76: iconst_2
    //   77: if_icmpeq +8 -> 85
    //   80: iload_3
    //   81: iconst_1
    //   82: if_icmpne -23 -> 59
    //   85: iload_3
    //   86: iconst_2
    //   87: if_icmpeq +42 -> 129
    //   90: aload 7
    //   92: astore 5
    //   94: aload 7
    //   96: astore 6
    //   98: ldc 37
    //   100: new 199	java/lang/StringBuilder
    //   103: dup
    //   104: invokespecial 200	java/lang/StringBuilder:<init>	()V
    //   107: ldc -54
    //   109: invokevirtual 206	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: iload_1
    //   113: invokevirtual 209	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   116: invokevirtual 211	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   119: invokestatic 217	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   122: pop
    //   123: aload 7
    //   125: invokestatic 223	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   128: return
    //   129: aload 7
    //   131: astore 5
    //   133: aload 7
    //   135: astore 6
    //   137: aload 8
    //   139: invokeinterface 226 1 0
    //   144: ldc 43
    //   146: invokevirtual 232	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   149: istore 4
    //   151: iload 4
    //   153: ifne +9 -> 162
    //   156: aload 7
    //   158: invokestatic 223	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   161: return
    //   162: aload 7
    //   164: astore 5
    //   166: aload 7
    //   168: astore 6
    //   170: aload 8
    //   172: invokeinterface 197 1 0
    //   177: istore_3
    //   178: iload_3
    //   179: iconst_1
    //   180: if_icmpeq +177 -> 357
    //   183: iload_3
    //   184: iconst_2
    //   185: if_icmpne -23 -> 162
    //   188: aload 7
    //   190: astore 5
    //   192: aload 7
    //   194: astore 6
    //   196: aload 8
    //   198: invokeinterface 226 1 0
    //   203: ldc 40
    //   205: invokevirtual 232	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   208: ifeq -46 -> 162
    //   211: aload 7
    //   213: astore 5
    //   215: aload 7
    //   217: astore 6
    //   219: aload 8
    //   221: aconst_null
    //   222: ldc 17
    //   224: invokeinterface 236 3 0
    //   229: astore 9
    //   231: aload 7
    //   233: astore 5
    //   235: aload 7
    //   237: astore 6
    //   239: new 6	com/android/server/usage/AppIdleHistory$PackageHistory
    //   242: dup
    //   243: aconst_null
    //   244: invokespecial 103	com/android/server/usage/AppIdleHistory$PackageHistory:<init>	(Lcom/android/server/usage/AppIdleHistory$PackageHistory;)V
    //   247: astore 10
    //   249: aload 7
    //   251: astore 5
    //   253: aload 7
    //   255: astore 6
    //   257: aload 10
    //   259: aload 8
    //   261: aconst_null
    //   262: ldc 14
    //   264: invokeinterface 236 3 0
    //   269: invokestatic 242	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   272: putfield 108	com/android/server/usage/AppIdleHistory$PackageHistory:lastUsedElapsedTime	J
    //   275: aload 7
    //   277: astore 5
    //   279: aload 7
    //   281: astore 6
    //   283: aload 10
    //   285: aload 8
    //   287: aconst_null
    //   288: ldc 20
    //   290: invokeinterface 236 3 0
    //   295: invokestatic 242	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   298: putfield 114	com/android/server/usage/AppIdleHistory$PackageHistory:lastUsedScreenTime	J
    //   301: aload 7
    //   303: astore 5
    //   305: aload 7
    //   307: astore 6
    //   309: aload_2
    //   310: aload 9
    //   312: aload 10
    //   314: invokevirtual 118	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   317: pop
    //   318: goto -156 -> 162
    //   321: astore_2
    //   322: aload 5
    //   324: astore 6
    //   326: ldc 37
    //   328: new 199	java/lang/StringBuilder
    //   331: dup
    //   332: invokespecial 200	java/lang/StringBuilder:<init>	()V
    //   335: ldc -54
    //   337: invokevirtual 206	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   340: iload_1
    //   341: invokevirtual 209	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   344: invokevirtual 211	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   347: invokestatic 217	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   350: pop
    //   351: aload 5
    //   353: invokestatic 223	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   356: return
    //   357: aload 7
    //   359: invokestatic 223	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   362: return
    //   363: astore_2
    //   364: aload 6
    //   366: invokestatic 223	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   369: aload_2
    //   370: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	371	0	this	AppIdleHistory
    //   0	371	1	paramInt	int
    //   0	371	2	paramArrayMap	ArrayMap<String, PackageHistory>
    //   74	112	3	i	int
    //   149	3	4	bool	boolean
    //   4	348	5	localObject1	Object
    //   1	364	6	localObject2	Object
    //   21	337	7	localFileInputStream	java.io.FileInputStream
    //   34	252	8	localXmlPullParser	org.xmlpull.v1.XmlPullParser
    //   229	82	9	str	String
    //   247	66	10	localPackageHistory	PackageHistory
    // Exception table:
    //   from	to	target	type
    //   6	23	321	java/io/IOException
    //   6	23	321	org/xmlpull/v1/XmlPullParserException
    //   31	36	321	java/io/IOException
    //   31	36	321	org/xmlpull/v1/XmlPullParserException
    //   44	59	321	java/io/IOException
    //   44	59	321	org/xmlpull/v1/XmlPullParserException
    //   67	75	321	java/io/IOException
    //   67	75	321	org/xmlpull/v1/XmlPullParserException
    //   98	123	321	java/io/IOException
    //   98	123	321	org/xmlpull/v1/XmlPullParserException
    //   137	151	321	java/io/IOException
    //   137	151	321	org/xmlpull/v1/XmlPullParserException
    //   170	178	321	java/io/IOException
    //   170	178	321	org/xmlpull/v1/XmlPullParserException
    //   196	211	321	java/io/IOException
    //   196	211	321	org/xmlpull/v1/XmlPullParserException
    //   219	231	321	java/io/IOException
    //   219	231	321	org/xmlpull/v1/XmlPullParserException
    //   239	249	321	java/io/IOException
    //   239	249	321	org/xmlpull/v1/XmlPullParserException
    //   257	275	321	java/io/IOException
    //   257	275	321	org/xmlpull/v1/XmlPullParserException
    //   283	301	321	java/io/IOException
    //   283	301	321	org/xmlpull/v1/XmlPullParserException
    //   309	318	321	java/io/IOException
    //   309	318	321	org/xmlpull/v1/XmlPullParserException
    //   6	23	363	finally
    //   31	36	363	finally
    //   44	59	363	finally
    //   67	75	363	finally
    //   98	123	363	finally
    //   137	151	363	finally
    //   170	178	363	finally
    //   196	211	363	finally
    //   219	231	363	finally
    //   239	249	363	finally
    //   257	275	363	finally
    //   283	301	363	finally
    //   309	318	363	finally
    //   326	351	363	finally
  }
  
  private void readScreenOnTimeLocked()
  {
    Object localObject = getScreenOnTimeFile();
    if (((File)localObject).exists()) {}
    try
    {
      localObject = new BufferedReader(new FileReader((File)localObject));
      this.mScreenOnDuration = Long.parseLong(((BufferedReader)localObject).readLine());
      this.mElapsedDuration = Long.parseLong(((BufferedReader)localObject).readLine());
      ((BufferedReader)localObject).close();
      return;
    }
    catch (IOException|NumberFormatException localIOException) {}
    writeScreenOnTimeLocked();
    return;
  }
  
  private void shiftHistoryToNow(ArrayMap<String, PackageHistory> paramArrayMap, long paramLong)
  {
    paramLong /= 3600000L;
    if ((this.mLastPeriod != 0L) && (this.mLastPeriod < paramLong) && (paramLong - this.mLastPeriod < 99L))
    {
      int k = (int)(paramLong - this.mLastPeriod);
      int m = this.mIdleHistory.size();
      int i = 0;
      while (i < m)
      {
        paramArrayMap = ((ArrayMap)this.mIdleHistory.valueAt(i)).values().iterator();
        while (paramArrayMap.hasNext())
        {
          PackageHistory localPackageHistory = (PackageHistory)paramArrayMap.next();
          System.arraycopy(localPackageHistory.recent, k, localPackageHistory.recent, 0, 100 - k);
          int j = 0;
          while (j < k)
          {
            localPackageHistory.recent[(100 - j - 1)] = ((byte)(localPackageHistory.recent[(100 - k - 1)] & 0x2));
            j += 1;
          }
        }
        i += 1;
      }
    }
    this.mLastPeriod = paramLong;
  }
  
  private void writeScreenOnTimeLocked()
  {
    AtomicFile localAtomicFile = new AtomicFile(getScreenOnTimeFile());
    Object localObject = null;
    try
    {
      FileOutputStream localFileOutputStream = localAtomicFile.startWrite();
      localObject = localFileOutputStream;
      localFileOutputStream.write((Long.toString(this.mScreenOnDuration) + "\n" + Long.toString(this.mElapsedDuration) + "\n").getBytes());
      localObject = localFileOutputStream;
      localAtomicFile.finishWrite(localFileOutputStream);
      return;
    }
    catch (IOException localIOException)
    {
      localAtomicFile.failWrite((FileOutputStream)localObject);
    }
  }
  
  public void clearUsageLocked(String paramString, int paramInt)
  {
    getUserHistoryLocked(paramInt).remove(paramString);
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter, int paramInt)
  {
    paramIndentingPrintWriter.println("Package idle stats:");
    paramIndentingPrintWriter.increaseIndent();
    ArrayMap localArrayMap = (ArrayMap)this.mIdleHistory.get(paramInt);
    long l1 = SystemClock.elapsedRealtime();
    long l2 = getElapsedTimeLocked(l1);
    long l3 = getScreenOnTimeLocked(l1);
    if (localArrayMap == null) {
      return;
    }
    int j = localArrayMap.size();
    int i = 0;
    if (i < j)
    {
      String str = (String)localArrayMap.keyAt(i);
      Object localObject = (PackageHistory)localArrayMap.valueAt(i);
      paramIndentingPrintWriter.print("package=" + str);
      paramIndentingPrintWriter.print(" lastUsedElapsed=");
      TimeUtils.formatDuration(l2 - ((PackageHistory)localObject).lastUsedElapsedTime, paramIndentingPrintWriter);
      paramIndentingPrintWriter.print(" lastUsedScreenOn=");
      TimeUtils.formatDuration(l3 - ((PackageHistory)localObject).lastUsedScreenTime, paramIndentingPrintWriter);
      localObject = new StringBuilder().append(" idle=");
      if (isIdleLocked(str, paramInt, l1)) {}
      for (str = "y";; str = "n")
      {
        paramIndentingPrintWriter.print(str);
        paramIndentingPrintWriter.println();
        i += 1;
        break;
      }
    }
    paramIndentingPrintWriter.println();
    paramIndentingPrintWriter.print("totalElapsedTime=");
    TimeUtils.formatDuration(getElapsedTimeLocked(l1), paramIndentingPrintWriter);
    paramIndentingPrintWriter.println();
    paramIndentingPrintWriter.print("totalScreenOnTime=");
    TimeUtils.formatDuration(getScreenOnTimeLocked(l1), paramIndentingPrintWriter);
    paramIndentingPrintWriter.println();
    paramIndentingPrintWriter.decreaseIndent();
  }
  
  public void dumpHistory(IndentingPrintWriter paramIndentingPrintWriter, int paramInt)
  {
    ArrayMap localArrayMap = (ArrayMap)this.mIdleHistory.get(paramInt);
    long l = SystemClock.elapsedRealtime();
    if (localArrayMap == null) {
      return;
    }
    int k = localArrayMap.size();
    int i = 0;
    if (i < k)
    {
      String str = (String)localArrayMap.keyAt(i);
      Object localObject = ((PackageHistory)localArrayMap.valueAt(i)).recent;
      int j = 0;
      if (j < 100)
      {
        if (localObject[j] == 0) {}
        for (char c = '.';; c = 'A')
        {
          paramIndentingPrintWriter.print(c);
          j += 1;
          break;
        }
      }
      StringBuilder localStringBuilder = new StringBuilder().append(" idle=");
      if (isIdleLocked(str, paramInt, l)) {}
      for (localObject = "y";; localObject = "n")
      {
        paramIndentingPrintWriter.print((String)localObject);
        paramIndentingPrintWriter.print("  " + str);
        paramIndentingPrintWriter.println();
        i += 1;
        break;
      }
    }
  }
  
  File getScreenOnTimeFile()
  {
    return new File(this.mStorageDir, "screen_on_time");
  }
  
  public long getScreenOnTimeLocked(long paramLong)
  {
    long l2 = this.mScreenOnDuration;
    long l1 = l2;
    if (this.mScreenOn) {
      l1 = l2 + (paramLong - this.mScreenOnSnapshot);
    }
    return l1;
  }
  
  public boolean isIdleLocked(String paramString, int paramInt, long paramLong)
  {
    paramString = getPackageHistoryLocked(getUserHistoryLocked(paramInt), paramString, paramLong);
    if (paramString == null) {
      return false;
    }
    return hasPassedThresholdsLocked(paramString, paramLong);
  }
  
  public void onUserRemoved(int paramInt)
  {
    this.mIdleHistory.remove(paramInt);
  }
  
  public void reportUsageLocked(String paramString, int paramInt, long paramLong)
  {
    ArrayMap localArrayMap = getUserHistoryLocked(paramInt);
    paramString = getPackageHistoryLocked(localArrayMap, paramString, paramLong);
    shiftHistoryToNow(localArrayMap, paramLong);
    paramString.lastUsedElapsedTime = (this.mElapsedDuration + (paramLong - this.mElapsedSnapshot));
    paramString.lastUsedScreenTime = getScreenOnTimeLocked(paramLong);
    paramString.recent[99] = 3;
  }
  
  public void setIdle(String paramString, int paramInt, long paramLong)
  {
    ArrayMap localArrayMap = getUserHistoryLocked(paramInt);
    paramString = getPackageHistoryLocked(localArrayMap, paramString, paramLong);
    shiftHistoryToNow(localArrayMap, paramLong);
    paramString = paramString.recent;
    paramString[99] = ((byte)(paramString[99] & 0xFFFFFFFD));
  }
  
  public void setIdleLocked(String paramString, int paramInt, boolean paramBoolean, long paramLong)
  {
    paramString = getPackageHistoryLocked(getUserHistoryLocked(paramInt), paramString, paramLong);
    paramString.lastUsedElapsedTime = (getElapsedTimeLocked(paramLong) - this.mElapsedTimeThreshold);
    long l = getScreenOnTimeLocked(paramLong);
    if (paramBoolean) {}
    for (paramLong = this.mScreenOnTimeThreshold;; paramLong = 0L)
    {
      paramString.lastUsedScreenTime = (l - paramLong - 1000L);
      return;
    }
  }
  
  public void setThresholds(long paramLong1, long paramLong2)
  {
    this.mElapsedTimeThreshold = paramLong1;
    this.mScreenOnTimeThreshold = paramLong2;
  }
  
  public void updateDisplayLocked(boolean paramBoolean, long paramLong)
  {
    if (paramBoolean == this.mScreenOn) {
      return;
    }
    this.mScreenOn = paramBoolean;
    if (this.mScreenOn)
    {
      this.mScreenOnSnapshot = paramLong;
      return;
    }
    this.mScreenOnDuration += paramLong - this.mScreenOnSnapshot;
    this.mElapsedDuration += paramLong - this.mElapsedSnapshot;
    this.mElapsedSnapshot = paramLong;
  }
  
  public void writeAppIdleDurationsLocked()
  {
    long l = SystemClock.elapsedRealtime();
    this.mElapsedDuration += l - this.mElapsedSnapshot;
    this.mElapsedSnapshot = l;
    writeScreenOnTimeLocked();
  }
  
  public void writeAppIdleTimesLocked(int paramInt)
  {
    Object localObject1 = null;
    AtomicFile localAtomicFile = new AtomicFile(getUserFile(paramInt));
    try
    {
      FileOutputStream localFileOutputStream = localAtomicFile.startWrite();
      localObject1 = localFileOutputStream;
      Object localObject2 = new BufferedOutputStream(localFileOutputStream);
      localObject1 = localFileOutputStream;
      FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.setOutput((OutputStream)localObject2, StandardCharsets.UTF_8.name());
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.startTag(null, "packages");
      localObject1 = localFileOutputStream;
      localObject2 = getUserHistoryLocked(paramInt);
      localObject1 = localFileOutputStream;
      int j = ((ArrayMap)localObject2).size();
      int i = 0;
      while (i < j)
      {
        localObject1 = localFileOutputStream;
        String str = (String)((ArrayMap)localObject2).keyAt(i);
        localObject1 = localFileOutputStream;
        PackageHistory localPackageHistory = (PackageHistory)((ArrayMap)localObject2).valueAt(i);
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.startTag(null, "package");
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "name", str);
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "elapsedIdleTime", Long.toString(localPackageHistory.lastUsedElapsedTime));
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "screenIdleTime", Long.toString(localPackageHistory.lastUsedScreenTime));
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.endTag(null, "package");
        i += 1;
      }
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.endTag(null, "packages");
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.endDocument();
      localObject1 = localFileOutputStream;
      localAtomicFile.finishWrite(localFileOutputStream);
      return;
    }
    catch (Exception localException)
    {
      localAtomicFile.failWrite((FileOutputStream)localObject1);
      Slog.e("AppIdleHistory", "Error writing app idle file for user " + paramInt);
    }
  }
  
  private static class PackageHistory
  {
    long lastUsedElapsedTime;
    long lastUsedScreenTime;
    final byte[] recent = new byte[100];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usage/AppIdleHistory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */