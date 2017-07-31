package com.android.providers.settings;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import libcore.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

final class SettingsState
{
  @GuardedBy("mLock")
  private boolean mDirty;
  private final Handler mHandler;
  @GuardedBy("mLock")
  private final List<HistoricalOperation> mHistoricalOperations;
  @GuardedBy("mLock")
  public final int mKey;
  @GuardedBy("mLock")
  private long mLastNotWrittenMutationTimeMillis;
  private final Object mLock;
  @GuardedBy("mLock")
  private final int mMaxBytesPerAppPackage;
  @GuardedBy("mLock")
  private int mNextHistoricalOpIdx;
  @GuardedBy("mLock")
  private long mNextId;
  private final Setting mNullSetting = new Setting(this, null, null, null)
  {
    public boolean isNull()
    {
      return true;
    }
  };
  @GuardedBy("mLock")
  private final ArrayMap<String, Integer> mPackageToMemoryUsage;
  @GuardedBy("mLock")
  private final ArrayMap<String, Setting> mSettings = new ArrayMap();
  @GuardedBy("mLock")
  private final File mStatePersistFile;
  @GuardedBy("mLock")
  private int mVersion = -1;
  @GuardedBy("mLock")
  private boolean mWriteScheduled;
  
  public SettingsState(Object arg1, File paramFile, int paramInt1, int paramInt2, Looper paramLooper)
  {
    this.mLock = ???;
    this.mStatePersistFile = paramFile;
    this.mKey = paramInt1;
    this.mHandler = new MyHandler(paramLooper);
    if (paramInt2 == 20000)
    {
      this.mMaxBytesPerAppPackage = paramInt2;
      this.mPackageToMemoryUsage = new ArrayMap();
    }
    for (;;)
    {
      ??? = localObject;
      if (Build.IS_DEBUGGABLE) {
        ??? = new ArrayList(20);
      }
      this.mHistoricalOperations = ((List)???);
      synchronized (this.mLock)
      {
        readStateSyncLocked();
        return;
        this.mMaxBytesPerAppPackage = paramInt2;
        this.mPackageToMemoryUsage = null;
      }
    }
  }
  
  private void addHistoricalOperationLocked(String paramString, Setting paramSetting)
  {
    Setting localSetting = null;
    if (this.mHistoricalOperations == null) {
      return;
    }
    long l = SystemClock.elapsedRealtime();
    if (paramSetting != null) {
      localSetting = new Setting(paramSetting);
    }
    paramString = new HistoricalOperation(l, paramString, localSetting);
    if (this.mNextHistoricalOpIdx >= this.mHistoricalOperations.size()) {
      this.mHistoricalOperations.add(paramString);
    }
    for (;;)
    {
      this.mNextHistoricalOpIdx += 1;
      if (this.mNextHistoricalOpIdx >= 20) {
        this.mNextHistoricalOpIdx = 0;
      }
      return;
      this.mHistoricalOperations.set(this.mNextHistoricalOpIdx, paramString);
    }
  }
  
  private static String base64Decode(String paramString)
  {
    return fromBytes(Base64.decode(paramString, 0));
  }
  
  private static String base64Encode(String paramString)
  {
    return Base64.encodeToString(toBytes(paramString), 2);
  }
  
  /* Error */
  private void doWriteState()
  {
    // Byte code:
    //   0: new 158	android/util/AtomicFile
    //   3: dup
    //   4: aload_0
    //   5: getfield 77	com/android/providers/settings/SettingsState:mStatePersistFile	Ljava/io/File;
    //   8: invokespecial 161	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   11: astore 6
    //   13: aload_0
    //   14: getfield 75	com/android/providers/settings/SettingsState:mLock	Ljava/lang/Object;
    //   17: astore_3
    //   18: aload_3
    //   19: monitorenter
    //   20: aload_0
    //   21: getfield 73	com/android/providers/settings/SettingsState:mVersion	I
    //   24: istore_1
    //   25: new 63	android/util/ArrayMap
    //   28: dup
    //   29: aload_0
    //   30: getfield 66	com/android/providers/settings/SettingsState:mSettings	Landroid/util/ArrayMap;
    //   33: invokespecial 164	android/util/ArrayMap:<init>	(Landroid/util/ArrayMap;)V
    //   36: astore 7
    //   38: aload_0
    //   39: iconst_0
    //   40: putfield 166	com/android/providers/settings/SettingsState:mDirty	Z
    //   43: aload_0
    //   44: iconst_0
    //   45: putfield 168	com/android/providers/settings/SettingsState:mWriteScheduled	Z
    //   48: aload_3
    //   49: monitorexit
    //   50: aconst_null
    //   51: astore 4
    //   53: aconst_null
    //   54: astore_3
    //   55: aload 6
    //   57: invokevirtual 172	android/util/AtomicFile:startWrite	()Ljava/io/FileOutputStream;
    //   60: astore 5
    //   62: aload 5
    //   64: astore_3
    //   65: aload 5
    //   67: astore 4
    //   69: invokestatic 178	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
    //   72: astore 8
    //   74: aload 5
    //   76: astore_3
    //   77: aload 5
    //   79: astore 4
    //   81: aload 8
    //   83: aload 5
    //   85: getstatic 184	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   88: invokevirtual 190	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   91: invokeinterface 196 3 0
    //   96: aload 5
    //   98: astore_3
    //   99: aload 5
    //   101: astore 4
    //   103: aload 8
    //   105: ldc -58
    //   107: iconst_1
    //   108: invokeinterface 202 3 0
    //   113: aload 5
    //   115: astore_3
    //   116: aload 5
    //   118: astore 4
    //   120: aload 8
    //   122: aconst_null
    //   123: iconst_1
    //   124: invokestatic 208	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   127: invokeinterface 212 3 0
    //   132: aload 5
    //   134: astore_3
    //   135: aload 5
    //   137: astore 4
    //   139: aload 8
    //   141: aconst_null
    //   142: ldc -42
    //   144: invokeinterface 218 3 0
    //   149: pop
    //   150: aload 5
    //   152: astore_3
    //   153: aload 5
    //   155: astore 4
    //   157: aload 8
    //   159: aconst_null
    //   160: ldc -36
    //   162: iload_1
    //   163: invokestatic 225	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   166: invokeinterface 229 4 0
    //   171: pop
    //   172: aload 5
    //   174: astore_3
    //   175: aload 5
    //   177: astore 4
    //   179: aload 7
    //   181: invokevirtual 230	android/util/ArrayMap:size	()I
    //   184: istore_2
    //   185: iconst_0
    //   186: istore_1
    //   187: iload_1
    //   188: iload_2
    //   189: if_icmpge +71 -> 260
    //   192: aload 5
    //   194: astore_3
    //   195: aload 5
    //   197: astore 4
    //   199: aload 7
    //   201: iload_1
    //   202: invokevirtual 234	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   205: checkcast 14	com/android/providers/settings/SettingsState$Setting
    //   208: astore 9
    //   210: aload 5
    //   212: astore_3
    //   213: aload 5
    //   215: astore 4
    //   217: aload_0
    //   218: getfield 73	com/android/providers/settings/SettingsState:mVersion	I
    //   221: aload 8
    //   223: aload 9
    //   225: invokevirtual 237	com/android/providers/settings/SettingsState$Setting:getId	()Ljava/lang/String;
    //   228: aload 9
    //   230: invokevirtual 240	com/android/providers/settings/SettingsState$Setting:getName	()Ljava/lang/String;
    //   233: aload 9
    //   235: invokevirtual 243	com/android/providers/settings/SettingsState$Setting:getValue	()Ljava/lang/String;
    //   238: aload 9
    //   240: invokevirtual 246	com/android/providers/settings/SettingsState$Setting:getPackageName	()Ljava/lang/String;
    //   243: invokestatic 250	com/android/providers/settings/SettingsState:writeSingleSetting	(ILorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   246: iload_1
    //   247: iconst_1
    //   248: iadd
    //   249: istore_1
    //   250: goto -63 -> 187
    //   253: astore 4
    //   255: aload_3
    //   256: monitorexit
    //   257: aload 4
    //   259: athrow
    //   260: aload 5
    //   262: astore_3
    //   263: aload 5
    //   265: astore 4
    //   267: aload 8
    //   269: aconst_null
    //   270: ldc -42
    //   272: invokeinterface 253 3 0
    //   277: pop
    //   278: aload 5
    //   280: astore_3
    //   281: aload 5
    //   283: astore 4
    //   285: aload 8
    //   287: invokeinterface 256 1 0
    //   292: aload 5
    //   294: astore_3
    //   295: aload 5
    //   297: astore 4
    //   299: aload 6
    //   301: aload 5
    //   303: invokevirtual 260	android/util/AtomicFile:finishWrite	(Ljava/io/FileOutputStream;)V
    //   306: aload 5
    //   308: astore_3
    //   309: aload 5
    //   311: astore 4
    //   313: aload_0
    //   314: getfield 75	com/android/providers/settings/SettingsState:mLock	Ljava/lang/Object;
    //   317: astore 7
    //   319: aload 5
    //   321: astore_3
    //   322: aload 5
    //   324: astore 4
    //   326: aload 7
    //   328: monitorenter
    //   329: aload_0
    //   330: ldc_w 262
    //   333: aconst_null
    //   334: invokespecial 264	com/android/providers/settings/SettingsState:addHistoricalOperationLocked	(Ljava/lang/String;Lcom/android/providers/settings/SettingsState$Setting;)V
    //   337: aload 5
    //   339: astore_3
    //   340: aload 5
    //   342: astore 4
    //   344: aload 7
    //   346: monitorexit
    //   347: aload 5
    //   349: invokestatic 270	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   352: return
    //   353: astore 8
    //   355: aload 5
    //   357: astore_3
    //   358: aload 5
    //   360: astore 4
    //   362: aload 7
    //   364: monitorexit
    //   365: aload 5
    //   367: astore_3
    //   368: aload 5
    //   370: astore 4
    //   372: aload 8
    //   374: athrow
    //   375: astore 5
    //   377: aload_3
    //   378: astore 4
    //   380: ldc_w 272
    //   383: ldc_w 274
    //   386: aload 5
    //   388: invokestatic 280	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   391: pop
    //   392: aload_3
    //   393: astore 4
    //   395: aload 6
    //   397: aload_3
    //   398: invokevirtual 283	android/util/AtomicFile:failWrite	(Ljava/io/FileOutputStream;)V
    //   401: aload_3
    //   402: invokestatic 270	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   405: return
    //   406: astore_3
    //   407: aload 4
    //   409: invokestatic 270	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   412: aload_3
    //   413: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	414	0	this	SettingsState
    //   24	226	1	i	int
    //   184	6	2	j	int
    //   17	385	3	localObject1	Object
    //   406	7	3	localObject2	Object
    //   51	165	4	localObject3	Object
    //   253	5	4	localObject4	Object
    //   265	143	4	localObject5	Object
    //   60	309	5	localFileOutputStream	java.io.FileOutputStream
    //   375	12	5	localThrowable	Throwable
    //   11	385	6	localAtomicFile	android.util.AtomicFile
    //   36	327	7	localObject6	Object
    //   72	214	8	localXmlSerializer	XmlSerializer
    //   353	20	8	localObject7	Object
    //   208	31	9	localSetting	Setting
    // Exception table:
    //   from	to	target	type
    //   20	48	253	finally
    //   329	337	353	finally
    //   55	62	375	java/lang/Throwable
    //   69	74	375	java/lang/Throwable
    //   81	96	375	java/lang/Throwable
    //   103	113	375	java/lang/Throwable
    //   120	132	375	java/lang/Throwable
    //   139	150	375	java/lang/Throwable
    //   157	172	375	java/lang/Throwable
    //   179	185	375	java/lang/Throwable
    //   199	210	375	java/lang/Throwable
    //   217	246	375	java/lang/Throwable
    //   267	278	375	java/lang/Throwable
    //   285	292	375	java/lang/Throwable
    //   299	306	375	java/lang/Throwable
    //   313	319	375	java/lang/Throwable
    //   326	329	375	java/lang/Throwable
    //   344	347	375	java/lang/Throwable
    //   362	365	375	java/lang/Throwable
    //   372	375	375	java/lang/Throwable
    //   55	62	406	finally
    //   69	74	406	finally
    //   81	96	406	finally
    //   103	113	406	finally
    //   120	132	406	finally
    //   139	150	406	finally
    //   157	172	406	finally
    //   179	185	406	finally
    //   199	210	406	finally
    //   217	246	406	finally
    //   267	278	406	finally
    //   285	292	406	finally
    //   299	306	406	finally
    //   313	319	406	finally
    //   326	329	406	finally
    //   344	347	406	finally
    //   362	365	406	finally
    //   372	375	406	finally
    //   380	392	406	finally
    //   395	401	406	finally
  }
  
  private static String fromBytes(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramArrayOfByte.length / 2);
    int j = paramArrayOfByte.length;
    int i = 0;
    while (i < j - 1)
    {
      localStringBuffer.append((char)((paramArrayOfByte[i] & 0xFF) << 8 | paramArrayOfByte[(i + 1)] & 0xFF));
      i += 2;
    }
    return localStringBuffer.toString();
  }
  
  private String getValueAttribute(XmlPullParser paramXmlPullParser)
  {
    if (this.mVersion >= 121)
    {
      String str = paramXmlPullParser.getAttributeValue(null, "value");
      if (str != null) {
        return str;
      }
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "valueBase64");
      if (paramXmlPullParser != null) {
        return base64Decode(paramXmlPullParser);
      }
      return null;
    }
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "value");
    if ("null".equals(paramXmlPullParser)) {
      return null;
    }
    return paramXmlPullParser;
  }
  
  private boolean hasSettingLocked(String paramString)
  {
    boolean bool = false;
    if (this.mSettings.indexOfKey(paramString) >= 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isBinary(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    int i = 0;
    while (i < paramString.length())
    {
      int j = paramString.charAt(i);
      if ((j >= 32) && (j <= 55295)) {}
      for (j = 1;; j = 0)
      {
        if (j != 0) {
          break label68;
        }
        return true;
        if ((j >= 57344) && (j <= 65533)) {
          break;
        }
      }
      label68:
      i += 1;
    }
    return false;
  }
  
  private void parseSettingsLocked(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    this.mVersion = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "version"));
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4) && (paramXmlPullParser.getName().equals("setting")))
      {
        String str1 = paramXmlPullParser.getAttributeValue(null, "id");
        String str2 = paramXmlPullParser.getAttributeValue(null, "name");
        String str3 = getValueAttribute(paramXmlPullParser);
        String str4 = paramXmlPullParser.getAttributeValue(null, "package");
        this.mSettings.put(str2, new Setting(str2, str3, str4, str1));
      }
    }
  }
  
  private void parseStateLocked(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4) && (paramXmlPullParser.getName().equals("settings"))) {
        parseSettingsLocked(paramXmlPullParser);
      }
    }
  }
  
  /* Error */
  private void readStateSyncLocked()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 77	com/android/providers/settings/SettingsState:mStatePersistFile	Ljava/io/File;
    //   4: invokevirtual 378	java/io/File:exists	()Z
    //   7: ifne +42 -> 49
    //   10: ldc_w 272
    //   13: new 380	java/lang/StringBuilder
    //   16: dup
    //   17: invokespecial 381	java/lang/StringBuilder:<init>	()V
    //   20: ldc_w 383
    //   23: invokevirtual 386	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   26: aload_0
    //   27: getfield 77	com/android/providers/settings/SettingsState:mStatePersistFile	Ljava/io/File;
    //   30: invokevirtual 389	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   33: invokevirtual 390	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   36: invokestatic 394	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   39: pop
    //   40: aload_0
    //   41: ldc_w 396
    //   44: aconst_null
    //   45: invokespecial 264	com/android/providers/settings/SettingsState:addHistoricalOperationLocked	(Ljava/lang/String;Lcom/android/providers/settings/SettingsState$Setting;)V
    //   48: return
    //   49: new 158	android/util/AtomicFile
    //   52: dup
    //   53: aload_0
    //   54: getfield 77	com/android/providers/settings/SettingsState:mStatePersistFile	Ljava/io/File;
    //   57: invokespecial 161	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   60: invokevirtual 400	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   63: astore_1
    //   64: invokestatic 404	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   67: astore_2
    //   68: aload_2
    //   69: aload_1
    //   70: getstatic 184	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   73: invokevirtual 190	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   76: invokeinterface 408 3 0
    //   81: aload_0
    //   82: aload_2
    //   83: invokespecial 410	com/android/providers/settings/SettingsState:parseStateLocked	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   86: aload_1
    //   87: invokestatic 270	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   90: return
    //   91: astore_1
    //   92: new 380	java/lang/StringBuilder
    //   95: dup
    //   96: invokespecial 381	java/lang/StringBuilder:<init>	()V
    //   99: ldc_w 383
    //   102: invokevirtual 386	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   105: aload_0
    //   106: getfield 77	com/android/providers/settings/SettingsState:mStatePersistFile	Ljava/io/File;
    //   109: invokevirtual 389	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   112: invokevirtual 390	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   115: astore_1
    //   116: ldc_w 272
    //   119: aload_1
    //   120: invokestatic 412	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   123: pop
    //   124: ldc_w 272
    //   127: aload_1
    //   128: invokestatic 394	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   131: pop
    //   132: return
    //   133: astore_2
    //   134: new 380	java/lang/StringBuilder
    //   137: dup
    //   138: invokespecial 381	java/lang/StringBuilder:<init>	()V
    //   141: ldc_w 414
    //   144: invokevirtual 386	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   147: aload_0
    //   148: getfield 77	com/android/providers/settings/SettingsState:mStatePersistFile	Ljava/io/File;
    //   151: invokevirtual 389	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   154: invokevirtual 390	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   157: astore_3
    //   158: ldc_w 272
    //   161: aload_3
    //   162: invokestatic 412	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   165: pop
    //   166: new 416	java/lang/IllegalStateException
    //   169: dup
    //   170: aload_3
    //   171: aload_2
    //   172: invokespecial 419	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   175: athrow
    //   176: astore_2
    //   177: aload_1
    //   178: invokestatic 270	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   181: aload_2
    //   182: athrow
    //   183: astore_2
    //   184: new 380	java/lang/StringBuilder
    //   187: dup
    //   188: invokespecial 381	java/lang/StringBuilder:<init>	()V
    //   191: ldc_w 421
    //   194: invokevirtual 386	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   197: aload_0
    //   198: getfield 77	com/android/providers/settings/SettingsState:mStatePersistFile	Ljava/io/File;
    //   201: invokevirtual 389	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   204: invokevirtual 390	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   207: astore_3
    //   208: ldc_w 272
    //   211: aload_3
    //   212: invokestatic 412	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   215: pop
    //   216: new 374	java/io/File
    //   219: dup
    //   220: ldc_w 423
    //   223: invokespecial 426	java/io/File:<init>	(Ljava/lang/String;)V
    //   226: astore 4
    //   228: aload 4
    //   230: invokevirtual 378	java/io/File:exists	()Z
    //   233: ifeq +9 -> 242
    //   236: aload 4
    //   238: invokevirtual 429	java/io/File:delete	()Z
    //   241: pop
    //   242: new 416	java/lang/IllegalStateException
    //   245: dup
    //   246: aload_3
    //   247: aload_2
    //   248: invokespecial 419	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   251: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	252	0	this	SettingsState
    //   63	24	1	localFileInputStream	java.io.FileInputStream
    //   91	1	1	localFileNotFoundException	java.io.FileNotFoundException
    //   115	63	1	str1	String
    //   67	16	2	localXmlPullParser	XmlPullParser
    //   133	39	2	localIOException	IOException
    //   176	6	2	localObject	Object
    //   183	65	2	localXmlPullParserException	XmlPullParserException
    //   157	90	3	str2	String
    //   226	11	4	localFile	File
    // Exception table:
    //   from	to	target	type
    //   49	64	91	java/io/FileNotFoundException
    //   64	86	133	java/io/IOException
    //   64	86	176	finally
    //   134	176	176	finally
    //   184	242	176	finally
    //   242	252	176	finally
    //   64	86	183	org/xmlpull/v1/XmlPullParserException
  }
  
  private void scheduleWriteIfNeededLocked()
  {
    if (!this.mDirty)
    {
      this.mDirty = true;
      writeStateAsyncLocked();
    }
  }
  
  static void setValueAttribute(int paramInt, XmlSerializer paramXmlSerializer, String paramString)
    throws IOException
  {
    if (paramInt >= 121)
    {
      if (paramString == null) {
        return;
      }
      if (isBinary(paramString))
      {
        paramXmlSerializer.attribute(null, "valueBase64", base64Encode(paramString));
        return;
      }
      paramXmlSerializer.attribute(null, "value", paramString);
      return;
    }
    if (paramString == null)
    {
      paramXmlSerializer.attribute(null, "value", "null");
      return;
    }
    paramXmlSerializer.attribute(null, "value", paramString);
  }
  
  private static byte[] toBytes(String paramString)
  {
    byte[] arrayOfByte = new byte[paramString.length() * 2];
    int j = 0;
    int i = 0;
    while (i < paramString.length())
    {
      int k = paramString.charAt(i);
      int m = j + 1;
      arrayOfByte[j] = ((byte)(k >> 8));
      j = m + 1;
      arrayOfByte[m] = ((byte)k);
      i += 1;
    }
    return arrayOfByte;
  }
  
  private void updateMemoryUsagePerPackageLocked(String paramString1, String paramString2, String paramString3)
  {
    if (this.mMaxBytesPerAppPackage == -1) {
      return;
    }
    if ("android".equals(paramString1)) {
      return;
    }
    int i;
    if (paramString2 != null)
    {
      i = paramString2.length();
      if (paramString3 == null) {
        break label127;
      }
    }
    label127:
    for (int j = paramString3.length();; j = 0)
    {
      j -= i;
      paramString2 = (Integer)this.mPackageToMemoryUsage.get(paramString1);
      i = j;
      if (paramString2 != null) {
        i = j + paramString2.intValue();
      }
      i = Math.max(i, 0);
      if (i <= this.mMaxBytesPerAppPackage) {
        break label133;
      }
      throw new IllegalStateException("You are adding too many system settings. You should stop using system settings for app specific data package: " + paramString1);
      i = 0;
      break;
    }
    label133:
    this.mPackageToMemoryUsage.put(paramString1, Integer.valueOf(i));
  }
  
  static void writeSingleSetting(int paramInt, XmlSerializer paramXmlSerializer, String paramString1, String paramString2, String paramString3, String paramString4)
    throws IOException
  {
    if ((paramString1 == null) || (isBinary(paramString1)) || (paramString2 == null)) {}
    while ((isBinary(paramString2)) || (paramString4 == null) || (isBinary(paramString4))) {
      return;
    }
    paramXmlSerializer.startTag(null, "setting");
    paramXmlSerializer.attribute(null, "id", paramString1);
    paramXmlSerializer.attribute(null, "name", paramString2);
    setValueAttribute(paramInt, paramXmlSerializer, paramString3);
    paramXmlSerializer.attribute(null, "package", paramString4);
    paramXmlSerializer.endTag(null, "setting");
  }
  
  private void writeStateAsyncLocked()
  {
    long l = SystemClock.uptimeMillis();
    if (this.mWriteScheduled)
    {
      this.mHandler.removeMessages(1);
      if (l - this.mLastNotWrittenMutationTimeMillis >= 2000L)
      {
        this.mHandler.obtainMessage(1).sendToTarget();
        return;
      }
      l = Math.min(200L, Math.max(this.mLastNotWrittenMutationTimeMillis + 2000L - l, 0L));
      localMessage = this.mHandler.obtainMessage(1);
      this.mHandler.sendMessageDelayed(localMessage, l);
      return;
    }
    this.mLastNotWrittenMutationTimeMillis = l;
    Message localMessage = this.mHandler.obtainMessage(1);
    this.mHandler.sendMessageDelayed(localMessage, 200L);
    this.mWriteScheduled = true;
  }
  
  public boolean deleteSettingLocked(String paramString)
  {
    if ((!TextUtils.isEmpty(paramString)) && (hasSettingLocked(paramString)))
    {
      paramString = (Setting)this.mSettings.remove(paramString);
      updateMemoryUsagePerPackageLocked(Setting.-get0(paramString), Setting.-get1(paramString), null);
      addHistoricalOperationLocked("delete", paramString);
      scheduleWriteIfNeededLocked();
      return true;
    }
    return false;
  }
  
  public void destroyLocked(Runnable paramRunnable)
  {
    this.mHandler.removeMessages(1);
    if (paramRunnable != null)
    {
      if (this.mDirty)
      {
        this.mHandler.obtainMessage(1, paramRunnable).sendToTarget();
        return;
      }
      paramRunnable.run();
    }
  }
  
  public void dumpHistoricalOperations(PrintWriter paramPrintWriter)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mHistoricalOperations;
      if (localObject2 == null) {
        return;
      }
      paramPrintWriter.println("Historical operations");
      int m = this.mHistoricalOperations.size();
      int i = 0;
      while (i < m)
      {
        int k = this.mNextHistoricalOpIdx - 1 - i;
        int j = k;
        if (k < 0) {
          j = k + m;
        }
        localObject2 = (HistoricalOperation)this.mHistoricalOperations.get(j);
        paramPrintWriter.print(TimeUtils.formatForLogging(((HistoricalOperation)localObject2).mTimestamp));
        paramPrintWriter.print(" ");
        paramPrintWriter.print(((HistoricalOperation)localObject2).mOperation);
        if (((HistoricalOperation)localObject2).mSetting != null)
        {
          paramPrintWriter.print("  ");
          paramPrintWriter.print(((HistoricalOperation)localObject2).mSetting);
        }
        paramPrintWriter.println();
        i += 1;
      }
      paramPrintWriter.println();
      paramPrintWriter.println();
      return;
    }
  }
  
  public Setting getNullSetting()
  {
    return this.mNullSetting;
  }
  
  public Setting getSettingLocked(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return this.mNullSetting;
    }
    paramString = (Setting)this.mSettings.get(paramString);
    if (paramString != null) {
      return new Setting(paramString);
    }
    return this.mNullSetting;
  }
  
  public List<String> getSettingNamesLocked()
  {
    ArrayList localArrayList = new ArrayList();
    int j = this.mSettings.size();
    int i = 0;
    while (i < j)
    {
      localArrayList.add((String)this.mSettings.keyAt(i));
      i += 1;
    }
    return localArrayList;
  }
  
  public int getVersionLocked()
  {
    return this.mVersion;
  }
  
  public boolean insertSettingLocked(String paramString1, String paramString2, String paramString3)
  {
    if (TextUtils.isEmpty(paramString1)) {
      return false;
    }
    Setting localSetting = (Setting)this.mSettings.get(paramString1);
    if (localSetting != null) {}
    for (String str = Setting.-get1(localSetting);; str = null)
    {
      if (localSetting == null) {
        break label82;
      }
      if (localSetting.update(paramString2, paramString3)) {
        break;
      }
      return false;
    }
    for (paramString1 = localSetting;; paramString1 = localSetting)
    {
      addHistoricalOperationLocked("update", paramString1);
      updateMemoryUsagePerPackageLocked(paramString3, str, paramString2);
      scheduleWriteIfNeededLocked();
      return true;
      label82:
      localSetting = new Setting(paramString1, paramString2, paramString3);
      this.mSettings.put(paramString1, localSetting);
    }
  }
  
  public void onPackageRemovedLocked(String paramString)
  {
    int j = 0;
    int i = this.mSettings.size() - 1;
    if (i >= 0)
    {
      String str = (String)this.mSettings.keyAt(i);
      int k = j;
      if (!Settings.System.PUBLIC_SETTINGS.contains(str))
      {
        if (!Settings.System.PRIVATE_SETTINGS.contains(str)) {
          break label71;
        }
        k = j;
      }
      for (;;)
      {
        i -= 1;
        j = k;
        break;
        label71:
        k = j;
        if (paramString.equals(Setting.-get0((Setting)this.mSettings.valueAt(i))))
        {
          this.mSettings.removeAt(i);
          k = 1;
        }
      }
    }
    if (j != 0) {
      scheduleWriteIfNeededLocked();
    }
  }
  
  public void persistSyncLocked()
  {
    this.mHandler.removeMessages(1);
    doWriteState();
  }
  
  public void setVersionLocked(int paramInt)
  {
    if (paramInt == this.mVersion) {
      return;
    }
    this.mVersion = paramInt;
    scheduleWriteIfNeededLocked();
  }
  
  public boolean updateSettingLocked(String paramString1, String paramString2, String paramString3)
  {
    if (!hasSettingLocked(paramString1)) {
      return false;
    }
    return insertSettingLocked(paramString1, paramString2, paramString3);
  }
  
  private class HistoricalOperation
  {
    final String mOperation;
    final SettingsState.Setting mSetting;
    final long mTimestamp;
    
    public HistoricalOperation(long paramLong, String paramString, SettingsState.Setting paramSetting)
    {
      this.mTimestamp = paramLong;
      this.mOperation = paramString;
      this.mSetting = paramSetting;
    }
  }
  
  private final class MyHandler
    extends Handler
  {
    public MyHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      do
      {
        return;
        paramMessage = (Runnable)paramMessage.obj;
        SettingsState.-wrap0(SettingsState.this);
      } while (paramMessage == null);
      paramMessage.run();
    }
  }
  
  class Setting
  {
    private String id;
    private String name;
    private String packageName;
    private String value;
    
    public Setting(Setting paramSetting)
    {
      this.name = paramSetting.name;
      this.value = paramSetting.value;
      this.packageName = paramSetting.packageName;
      this.id = paramSetting.id;
    }
    
    public Setting(String paramString1, String paramString2, String paramString3)
    {
      long l = SettingsState.-get0(SettingsState.this);
      SettingsState.-set0(SettingsState.this, 1L + l);
      init(paramString1, paramString2, paramString3, String.valueOf(l));
    }
    
    public Setting(String paramString1, String paramString2, String paramString3, String paramString4)
    {
      SettingsState.-set0(SettingsState.this, Math.max(SettingsState.-get0(SettingsState.this), Long.valueOf(paramString4).longValue() + 1L));
      init(paramString1, paramString2, paramString3, paramString4);
    }
    
    private void init(String paramString1, String paramString2, String paramString3, String paramString4)
    {
      this.name = paramString1;
      this.value = paramString2;
      this.packageName = paramString3;
      this.id = paramString4;
    }
    
    public String getId()
    {
      return this.id;
    }
    
    public String getName()
    {
      return this.name;
    }
    
    public String getPackageName()
    {
      return this.packageName;
    }
    
    public String getValue()
    {
      return this.value;
    }
    
    public int getkey()
    {
      return SettingsState.this.mKey;
    }
    
    public boolean isNull()
    {
      return false;
    }
    
    public String toString()
    {
      return "Setting{name=" + this.value + " from " + this.packageName + "}";
    }
    
    public boolean update(String paramString1, String paramString2)
    {
      if (Objects.equal(paramString1, this.value)) {
        return false;
      }
      this.value = paramString1;
      this.packageName = paramString2;
      paramString1 = SettingsState.this;
      long l = SettingsState.-get0(paramString1);
      SettingsState.-set0(paramString1, 1L + l);
      this.id = String.valueOf(l);
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/providers/settings/SettingsState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */