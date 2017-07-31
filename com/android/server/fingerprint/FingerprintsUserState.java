package com.android.server.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.Fingerprint;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class FingerprintsUserState
{
  private static final String ATTR_DEVICE_ID = "deviceId";
  private static final String ATTR_FINGER_ID = "fingerId";
  private static final String ATTR_GROUP_ID = "groupId";
  private static final String ATTR_NAME = "name";
  private static final String FINGERPRINT_FILE = "settings_fingerprint.xml";
  private static final String TAG = "FingerprintState";
  private static final String TAG_FINGERPRINT = "fingerprint";
  private static final String TAG_FINGERPRINTS = "fingerprints";
  private final Context mCtx;
  private final File mFile;
  @GuardedBy("this")
  private final ArrayList<Fingerprint> mFingerprints = new ArrayList();
  private final Runnable mWriteStateRunnable = new Runnable()
  {
    public void run()
    {
      FingerprintsUserState.-wrap0(FingerprintsUserState.this);
    }
  };
  
  public FingerprintsUserState(Context paramContext, int paramInt)
  {
    this.mFile = getFileForUser(paramInt);
    this.mCtx = paramContext;
    try
    {
      readStateSyncLocked();
      return;
    }
    finally
    {
      paramContext = finally;
      throw paramContext;
    }
  }
  
  /* Error */
  private void doWriteState()
  {
    // Byte code:
    //   0: ldc 25
    //   2: ldc 77
    //   4: invokestatic 83	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   7: pop
    //   8: new 85	android/util/AtomicFile
    //   11: dup
    //   12: aload_0
    //   13: getfield 69	com/android/server/fingerprint/FingerprintsUserState:mFile	Ljava/io/File;
    //   16: invokespecial 88	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   19: astore 6
    //   21: aload_0
    //   22: monitorenter
    //   23: aload_0
    //   24: aload_0
    //   25: getfield 59	com/android/server/fingerprint/FingerprintsUserState:mFingerprints	Ljava/util/ArrayList;
    //   28: invokespecial 92	com/android/server/fingerprint/FingerprintsUserState:getCopy	(Ljava/util/ArrayList;)Ljava/util/ArrayList;
    //   31: astore 7
    //   33: aload_0
    //   34: monitorexit
    //   35: aconst_null
    //   36: astore 4
    //   38: aconst_null
    //   39: astore_3
    //   40: aload 6
    //   42: invokevirtual 96	android/util/AtomicFile:startWrite	()Ljava/io/FileOutputStream;
    //   45: astore 5
    //   47: aload 5
    //   49: astore_3
    //   50: aload 5
    //   52: astore 4
    //   54: invokestatic 102	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
    //   57: astore 8
    //   59: aload 5
    //   61: astore_3
    //   62: aload 5
    //   64: astore 4
    //   66: aload 8
    //   68: aload 5
    //   70: ldc 104
    //   72: invokeinterface 110 3 0
    //   77: aload 5
    //   79: astore_3
    //   80: aload 5
    //   82: astore 4
    //   84: aload 8
    //   86: ldc 112
    //   88: iconst_1
    //   89: invokeinterface 116 3 0
    //   94: aload 5
    //   96: astore_3
    //   97: aload 5
    //   99: astore 4
    //   101: aload 8
    //   103: aconst_null
    //   104: iconst_1
    //   105: invokestatic 122	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   108: invokeinterface 126 3 0
    //   113: aload 5
    //   115: astore_3
    //   116: aload 5
    //   118: astore 4
    //   120: aload 8
    //   122: aconst_null
    //   123: ldc 31
    //   125: invokeinterface 130 3 0
    //   130: pop
    //   131: aload 5
    //   133: astore_3
    //   134: aload 5
    //   136: astore 4
    //   138: aload 7
    //   140: invokevirtual 134	java/util/ArrayList:size	()I
    //   143: istore_2
    //   144: iconst_0
    //   145: istore_1
    //   146: iload_1
    //   147: iload_2
    //   148: if_icmpge +175 -> 323
    //   151: aload 5
    //   153: astore_3
    //   154: aload 5
    //   156: astore 4
    //   158: aload 7
    //   160: iload_1
    //   161: invokevirtual 138	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   164: checkcast 140	android/hardware/fingerprint/Fingerprint
    //   167: astore 9
    //   169: aload 5
    //   171: astore_3
    //   172: aload 5
    //   174: astore 4
    //   176: aload 8
    //   178: aconst_null
    //   179: ldc 28
    //   181: invokeinterface 130 3 0
    //   186: pop
    //   187: aload 5
    //   189: astore_3
    //   190: aload 5
    //   192: astore 4
    //   194: aload 8
    //   196: aconst_null
    //   197: ldc 13
    //   199: aload 9
    //   201: invokevirtual 143	android/hardware/fingerprint/Fingerprint:getFingerId	()I
    //   204: invokestatic 149	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   207: invokeinterface 153 4 0
    //   212: pop
    //   213: aload 5
    //   215: astore_3
    //   216: aload 5
    //   218: astore 4
    //   220: aload 8
    //   222: aconst_null
    //   223: ldc 19
    //   225: aload 9
    //   227: invokevirtual 157	android/hardware/fingerprint/Fingerprint:getName	()Ljava/lang/CharSequence;
    //   230: invokeinterface 162 1 0
    //   235: invokeinterface 153 4 0
    //   240: pop
    //   241: aload 5
    //   243: astore_3
    //   244: aload 5
    //   246: astore 4
    //   248: aload 8
    //   250: aconst_null
    //   251: ldc 16
    //   253: aload 9
    //   255: invokevirtual 165	android/hardware/fingerprint/Fingerprint:getGroupId	()I
    //   258: invokestatic 149	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   261: invokeinterface 153 4 0
    //   266: pop
    //   267: aload 5
    //   269: astore_3
    //   270: aload 5
    //   272: astore 4
    //   274: aload 8
    //   276: aconst_null
    //   277: ldc 10
    //   279: aload 9
    //   281: invokevirtual 169	android/hardware/fingerprint/Fingerprint:getDeviceId	()J
    //   284: invokestatic 174	java/lang/Long:toString	(J)Ljava/lang/String;
    //   287: invokeinterface 153 4 0
    //   292: pop
    //   293: aload 5
    //   295: astore_3
    //   296: aload 5
    //   298: astore 4
    //   300: aload 8
    //   302: aconst_null
    //   303: ldc 28
    //   305: invokeinterface 177 3 0
    //   310: pop
    //   311: iload_1
    //   312: iconst_1
    //   313: iadd
    //   314: istore_1
    //   315: goto -169 -> 146
    //   318: astore_3
    //   319: aload_0
    //   320: monitorexit
    //   321: aload_3
    //   322: athrow
    //   323: aload 5
    //   325: astore_3
    //   326: aload 5
    //   328: astore 4
    //   330: aload 8
    //   332: aconst_null
    //   333: ldc 31
    //   335: invokeinterface 177 3 0
    //   340: pop
    //   341: aload 5
    //   343: astore_3
    //   344: aload 5
    //   346: astore 4
    //   348: aload 8
    //   350: invokeinterface 180 1 0
    //   355: aload 5
    //   357: astore_3
    //   358: aload 5
    //   360: astore 4
    //   362: aload 6
    //   364: aload 5
    //   366: invokevirtual 184	android/util/AtomicFile:finishWrite	(Ljava/io/FileOutputStream;)V
    //   369: aload 5
    //   371: invokestatic 190	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   374: return
    //   375: astore 5
    //   377: aload_3
    //   378: astore 4
    //   380: ldc 25
    //   382: ldc -64
    //   384: aload 5
    //   386: invokestatic 196	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   389: pop
    //   390: aload_3
    //   391: astore 4
    //   393: aload 6
    //   395: aload_3
    //   396: invokevirtual 199	android/util/AtomicFile:failWrite	(Ljava/io/FileOutputStream;)V
    //   399: aload_3
    //   400: astore 4
    //   402: new 201	java/lang/IllegalStateException
    //   405: dup
    //   406: ldc -53
    //   408: aload 5
    //   410: invokespecial 206	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   413: athrow
    //   414: astore_3
    //   415: aload 4
    //   417: invokestatic 190	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   420: aload_3
    //   421: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	422	0	this	FingerprintsUserState
    //   145	170	1	i	int
    //   143	6	2	j	int
    //   39	257	3	localObject1	Object
    //   318	4	3	localObject2	Object
    //   325	75	3	localObject3	Object
    //   414	7	3	localObject4	Object
    //   36	380	4	localObject5	Object
    //   45	325	5	localFileOutputStream	java.io.FileOutputStream
    //   375	34	5	localThrowable	Throwable
    //   19	375	6	localAtomicFile	android.util.AtomicFile
    //   31	128	7	localArrayList	ArrayList
    //   57	292	8	localXmlSerializer	org.xmlpull.v1.XmlSerializer
    //   167	113	9	localFingerprint	Fingerprint
    // Exception table:
    //   from	to	target	type
    //   23	33	318	finally
    //   40	47	375	java/lang/Throwable
    //   54	59	375	java/lang/Throwable
    //   66	77	375	java/lang/Throwable
    //   84	94	375	java/lang/Throwable
    //   101	113	375	java/lang/Throwable
    //   120	131	375	java/lang/Throwable
    //   138	144	375	java/lang/Throwable
    //   158	169	375	java/lang/Throwable
    //   176	187	375	java/lang/Throwable
    //   194	213	375	java/lang/Throwable
    //   220	241	375	java/lang/Throwable
    //   248	267	375	java/lang/Throwable
    //   274	293	375	java/lang/Throwable
    //   300	311	375	java/lang/Throwable
    //   330	341	375	java/lang/Throwable
    //   348	355	375	java/lang/Throwable
    //   362	369	375	java/lang/Throwable
    //   40	47	414	finally
    //   54	59	414	finally
    //   66	77	414	finally
    //   84	94	414	finally
    //   101	113	414	finally
    //   120	131	414	finally
    //   138	144	414	finally
    //   158	169	414	finally
    //   176	187	414	finally
    //   194	213	414	finally
    //   220	241	414	finally
    //   248	267	414	finally
    //   274	293	414	finally
    //   300	311	414	finally
    //   330	341	414	finally
    //   348	355	414	finally
    //   362	369	414	finally
    //   380	390	414	finally
    //   393	399	414	finally
    //   402	414	414	finally
  }
  
  private ArrayList<Fingerprint> getCopy(ArrayList<Fingerprint> paramArrayList)
  {
    ArrayList localArrayList = new ArrayList(paramArrayList.size());
    int i = 0;
    while (i < paramArrayList.size())
    {
      Fingerprint localFingerprint = (Fingerprint)paramArrayList.get(i);
      localArrayList.add(new Fingerprint(localFingerprint.getName(), localFingerprint.getGroupId(), localFingerprint.getFingerId(), localFingerprint.getDeviceId()));
      i += 1;
    }
    return localArrayList;
  }
  
  private static File getFileForUser(int paramInt)
  {
    return new File(Environment.getUserSystemDirectory(paramInt), "settings_fingerprint.xml");
  }
  
  private String getUniqueName()
  {
    int i = 1;
    for (;;)
    {
      String str = this.mCtx.getString(17039872, new Object[] { Integer.valueOf(i) });
      if (isUnique(str)) {
        return str;
      }
      i += 1;
    }
  }
  
  private boolean isUnique(String paramString)
  {
    Iterator localIterator = this.mFingerprints.iterator();
    while (localIterator.hasNext()) {
      if (((Fingerprint)localIterator.next()).getName().equals(paramString)) {
        return false;
      }
    }
    return true;
  }
  
  private void parseFingerprintsLocked(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4) && (paramXmlPullParser.getName().equals("fingerprint")))
      {
        String str1 = paramXmlPullParser.getAttributeValue(null, "name");
        String str2 = paramXmlPullParser.getAttributeValue(null, "groupId");
        String str3 = paramXmlPullParser.getAttributeValue(null, "fingerId");
        String str4 = paramXmlPullParser.getAttributeValue(null, "deviceId");
        this.mFingerprints.add(new Fingerprint(str1, Integer.parseInt(str2), Integer.parseInt(str3), Integer.parseInt(str4)));
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
      if ((j != 3) && (j != 4) && (paramXmlPullParser.getName().equals("fingerprints"))) {
        parseFingerprintsLocked(paramXmlPullParser);
      }
    }
  }
  
  /* Error */
  private void readStateSyncLocked()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 69	com/android/server/fingerprint/FingerprintsUserState:mFile	Ljava/io/File;
    //   4: invokevirtual 297	java/io/File:exists	()Z
    //   7: ifne +4 -> 11
    //   10: return
    //   11: new 299	java/io/FileInputStream
    //   14: dup
    //   15: aload_0
    //   16: getfield 69	com/android/server/fingerprint/FingerprintsUserState:mFile	Ljava/io/File;
    //   19: invokespecial 300	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   22: astore_1
    //   23: invokestatic 304	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   26: astore_2
    //   27: aload_2
    //   28: aload_1
    //   29: aconst_null
    //   30: invokeinterface 308 3 0
    //   35: aload_0
    //   36: aload_2
    //   37: invokespecial 310	com/android/server/fingerprint/FingerprintsUserState:parseStateLocked	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   40: aload_1
    //   41: invokestatic 190	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   44: return
    //   45: astore_1
    //   46: ldc 25
    //   48: ldc_w 312
    //   51: invokestatic 315	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   54: pop
    //   55: return
    //   56: astore_2
    //   57: new 201	java/lang/IllegalStateException
    //   60: dup
    //   61: new 317	java/lang/StringBuilder
    //   64: dup
    //   65: invokespecial 318	java/lang/StringBuilder:<init>	()V
    //   68: ldc_w 320
    //   71: invokevirtual 324	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: aload_0
    //   75: getfield 69	com/android/server/fingerprint/FingerprintsUserState:mFile	Ljava/io/File;
    //   78: invokevirtual 327	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   81: invokevirtual 328	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: aload_2
    //   85: invokespecial 206	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   88: athrow
    //   89: astore_2
    //   90: aload_1
    //   91: invokestatic 190	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   94: aload_2
    //   95: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	96	0	this	FingerprintsUserState
    //   22	19	1	localFileInputStream	java.io.FileInputStream
    //   45	46	1	localFileNotFoundException	java.io.FileNotFoundException
    //   26	11	2	localXmlPullParser	XmlPullParser
    //   56	29	2	localXmlPullParserException	XmlPullParserException
    //   89	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   11	23	45	java/io/FileNotFoundException
    //   23	40	56	org/xmlpull/v1/XmlPullParserException
    //   23	40	56	java/io/IOException
    //   23	40	89	finally
    //   57	89	89	finally
  }
  
  private void scheduleWriteStateLocked()
  {
    AsyncTask.execute(this.mWriteStateRunnable);
  }
  
  public void addFingerprint(int paramInt1, int paramInt2)
  {
    try
    {
      Slog.d("FingerprintState", "addFingerprint");
      this.mFingerprints.add(new Fingerprint(getUniqueName(), paramInt2, paramInt1, 0L));
      scheduleWriteStateLocked();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public List<Fingerprint> getFingerprints()
  {
    try
    {
      ArrayList localArrayList = getCopy(this.mFingerprints);
      return localArrayList;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public void removeFingerprint(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: iconst_0
    //   3: istore_2
    //   4: iload_2
    //   5: aload_0
    //   6: getfield 59	com/android/server/fingerprint/FingerprintsUserState:mFingerprints	Ljava/util/ArrayList;
    //   9: invokevirtual 134	java/util/ArrayList:size	()I
    //   12: if_icmpge +34 -> 46
    //   15: aload_0
    //   16: getfield 59	com/android/server/fingerprint/FingerprintsUserState:mFingerprints	Ljava/util/ArrayList;
    //   19: iload_2
    //   20: invokevirtual 138	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   23: checkcast 140	android/hardware/fingerprint/Fingerprint
    //   26: invokevirtual 143	android/hardware/fingerprint/Fingerprint:getFingerId	()I
    //   29: iload_1
    //   30: if_icmpne +19 -> 49
    //   33: aload_0
    //   34: getfield 59	com/android/server/fingerprint/FingerprintsUserState:mFingerprints	Ljava/util/ArrayList;
    //   37: iload_2
    //   38: invokevirtual 349	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   41: pop
    //   42: aload_0
    //   43: invokespecial 342	com/android/server/fingerprint/FingerprintsUserState:scheduleWriteStateLocked	()V
    //   46: aload_0
    //   47: monitorexit
    //   48: return
    //   49: iload_2
    //   50: iconst_1
    //   51: iadd
    //   52: istore_2
    //   53: goto -49 -> 4
    //   56: astore_3
    //   57: aload_0
    //   58: monitorexit
    //   59: aload_3
    //   60: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	61	0	this	FingerprintsUserState
    //   0	61	1	paramInt	int
    //   3	50	2	i	int
    //   56	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	46	56	finally
  }
  
  /* Error */
  public void renameFingerprint(int paramInt, CharSequence paramCharSequence)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: iconst_0
    //   3: istore_3
    //   4: iload_3
    //   5: aload_0
    //   6: getfield 59	com/android/server/fingerprint/FingerprintsUserState:mFingerprints	Ljava/util/ArrayList;
    //   9: invokevirtual 134	java/util/ArrayList:size	()I
    //   12: if_icmpge +70 -> 82
    //   15: aload_0
    //   16: getfield 59	com/android/server/fingerprint/FingerprintsUserState:mFingerprints	Ljava/util/ArrayList;
    //   19: iload_3
    //   20: invokevirtual 138	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   23: checkcast 140	android/hardware/fingerprint/Fingerprint
    //   26: invokevirtual 143	android/hardware/fingerprint/Fingerprint:getFingerId	()I
    //   29: iload_1
    //   30: if_icmpne +55 -> 85
    //   33: aload_0
    //   34: getfield 59	com/android/server/fingerprint/FingerprintsUserState:mFingerprints	Ljava/util/ArrayList;
    //   37: iload_3
    //   38: invokevirtual 138	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   41: checkcast 140	android/hardware/fingerprint/Fingerprint
    //   44: astore 4
    //   46: aload_0
    //   47: getfield 59	com/android/server/fingerprint/FingerprintsUserState:mFingerprints	Ljava/util/ArrayList;
    //   50: iload_3
    //   51: new 140	android/hardware/fingerprint/Fingerprint
    //   54: dup
    //   55: aload_2
    //   56: aload 4
    //   58: invokevirtual 165	android/hardware/fingerprint/Fingerprint:getGroupId	()I
    //   61: aload 4
    //   63: invokevirtual 143	android/hardware/fingerprint/Fingerprint:getFingerId	()I
    //   66: aload 4
    //   68: invokevirtual 169	android/hardware/fingerprint/Fingerprint:getDeviceId	()J
    //   71: invokespecial 212	android/hardware/fingerprint/Fingerprint:<init>	(Ljava/lang/CharSequence;IIJ)V
    //   74: invokevirtual 355	java/util/ArrayList:set	(ILjava/lang/Object;)Ljava/lang/Object;
    //   77: pop
    //   78: aload_0
    //   79: invokespecial 342	com/android/server/fingerprint/FingerprintsUserState:scheduleWriteStateLocked	()V
    //   82: aload_0
    //   83: monitorexit
    //   84: return
    //   85: iload_3
    //   86: iconst_1
    //   87: iadd
    //   88: istore_3
    //   89: goto -85 -> 4
    //   92: astore_2
    //   93: aload_0
    //   94: monitorexit
    //   95: aload_2
    //   96: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	FingerprintsUserState
    //   0	97	1	paramInt	int
    //   0	97	2	paramCharSequence	CharSequence
    //   3	86	3	i	int
    //   44	23	4	localFingerprint	Fingerprint
    // Exception table:
    //   from	to	target	type
    //   4	82	92	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/fingerprint/FingerprintsUserState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */