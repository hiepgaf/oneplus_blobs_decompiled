package com.oneplus.cache;

import android.content.Context;
import android.os.Message;
import android.os.SystemClock;
import android.util.Pair;
import com.oneplus.base.Log;
import java.io.File;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class DiskLruCache<TKey extends Serializable, TValue>
  extends AsyncLruCache<TKey, TValue>
{
  private static final int MAX_TEMP_FILE_STAYS_DURATION = 1000;
  private static final int MSG_BUILD_CACHE_FROM_SNAPSHOT = 10000;
  private static final int MSG_CLEAR_TEMP_FILES = 10010;
  private static final int MSG_CREATE_SNAPSHOT = 10001;
  private static final boolean PRINT_TRACE_LOGS = false;
  private static final String SNAPSHOT_FILE_NAME = "__snapshot";
  private static final int THRESHOLD_OP_COUNT_TO_CREATE_SNAPSHOT = 64;
  private final Map<TKey, File> m_CacheFiles = new Hashtable();
  private final File m_Directory;
  private volatile int m_NewOperationCount;
  private final File m_SnapshotFile;
  private final Map<TKey, Pair<File, Long>> m_TempFiles = new Hashtable();
  
  protected DiskLruCache(Context paramContext, String paramString, long paramLong)
  {
    super(paramLong);
    this.m_Directory = new File(paramContext.getCacheDir(), paramString);
    this.m_SnapshotFile = new File(this.m_Directory, "__snapshot");
    sendMessageToWorkerThread(Message.obtain(null, 10000));
  }
  
  /* Error */
  private void buildCacheFromSnapshot()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 80	com/oneplus/cache/AsyncLruCache:clear	()V
    //   4: aload_0
    //   5: getfield 59	com/oneplus/cache/DiskLruCache:m_Directory	Ljava/io/File;
    //   8: invokevirtual 84	java/io/File:exists	()Z
    //   11: ifne +15 -> 26
    //   14: aload_0
    //   15: getfield 59	com/oneplus/cache/DiskLruCache:m_Directory	Ljava/io/File;
    //   18: invokevirtual 87	java/io/File:mkdirs	()Z
    //   21: istore_2
    //   22: iload_2
    //   23: ifeq +62 -> 85
    //   26: new 89	java/util/HashSet
    //   29: dup
    //   30: invokespecial 90	java/util/HashSet:<init>	()V
    //   33: astore 10
    //   35: aload_0
    //   36: getfield 59	com/oneplus/cache/DiskLruCache:m_Directory	Ljava/io/File;
    //   39: invokevirtual 94	java/io/File:list	()[Ljava/lang/String;
    //   42: astore_3
    //   43: aload_3
    //   44: arraylength
    //   45: iconst_1
    //   46: isub
    //   47: istore_1
    //   48: iload_1
    //   49: iflt +109 -> 158
    //   52: aload_3
    //   53: iload_1
    //   54: aaload
    //   55: invokestatic 100	com/oneplus/io/Path:getFileName	(Ljava/lang/String;)Ljava/lang/String;
    //   58: astore 4
    //   60: ldc 21
    //   62: aload 4
    //   64: invokevirtual 106	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   67: ifne +11 -> 78
    //   70: aload 10
    //   72: aload 4
    //   74: invokevirtual 109	java/util/HashSet:add	(Ljava/lang/Object;)Z
    //   77: pop
    //   78: iload_1
    //   79: iconst_1
    //   80: isub
    //   81: istore_1
    //   82: goto -34 -> 48
    //   85: aload_0
    //   86: getfield 112	com/oneplus/cache/DiskLruCache:TAG	Ljava/lang/String;
    //   89: new 114	java/lang/StringBuilder
    //   92: dup
    //   93: invokespecial 115	java/lang/StringBuilder:<init>	()V
    //   96: ldc 117
    //   98: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   101: aload_0
    //   102: getfield 59	com/oneplus/cache/DiskLruCache:m_Directory	Ljava/io/File;
    //   105: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   108: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   111: invokestatic 134	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   114: return
    //   115: astore_3
    //   116: aload_0
    //   117: getfield 112	com/oneplus/cache/DiskLruCache:TAG	Ljava/lang/String;
    //   120: new 114	java/lang/StringBuilder
    //   123: dup
    //   124: invokespecial 115	java/lang/StringBuilder:<init>	()V
    //   127: ldc 117
    //   129: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   132: aload_0
    //   133: getfield 59	com/oneplus/cache/DiskLruCache:m_Directory	Ljava/io/File;
    //   136: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   139: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   142: aload_3
    //   143: invokestatic 137	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   146: return
    //   147: astore_3
    //   148: aload_0
    //   149: getfield 112	com/oneplus/cache/DiskLruCache:TAG	Ljava/lang/String;
    //   152: ldc -117
    //   154: aload_3
    //   155: invokestatic 137	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   158: aload_0
    //   159: getfield 61	com/oneplus/cache/DiskLruCache:m_SnapshotFile	Ljava/io/File;
    //   162: invokevirtual 84	java/io/File:exists	()Z
    //   165: istore_2
    //   166: iload_2
    //   167: ifeq +206 -> 373
    //   170: aconst_null
    //   171: astore 6
    //   173: aconst_null
    //   174: astore 8
    //   176: aconst_null
    //   177: astore 9
    //   179: aconst_null
    //   180: astore 5
    //   182: aconst_null
    //   183: astore 4
    //   185: aconst_null
    //   186: astore 7
    //   188: new 141	java/io/FileInputStream
    //   191: dup
    //   192: aload_0
    //   193: getfield 61	com/oneplus/cache/DiskLruCache:m_SnapshotFile	Ljava/io/File;
    //   196: invokespecial 144	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   199: astore_3
    //   200: new 146	java/io/ObjectInputStream
    //   203: dup
    //   204: aload_3
    //   205: invokespecial 149	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
    //   208: astore 5
    //   210: aload 5
    //   212: invokevirtual 153	java/io/ObjectInputStream:readInt	()I
    //   215: istore_1
    //   216: iload_1
    //   217: ifle +90 -> 307
    //   220: aload 5
    //   222: invokevirtual 157	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   225: checkcast 159	java/io/Serializable
    //   228: astore 4
    //   230: aload 5
    //   232: invokevirtual 157	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   235: checkcast 102	java/lang/String
    //   238: astore 7
    //   240: aload 10
    //   242: aload 7
    //   244: invokevirtual 162	java/util/HashSet:remove	(Ljava/lang/Object;)Z
    //   247: ifeq +53 -> 300
    //   250: new 48	java/io/File
    //   253: dup
    //   254: aload_0
    //   255: getfield 59	com/oneplus/cache/DiskLruCache:m_Directory	Ljava/io/File;
    //   258: aload 7
    //   260: invokespecial 57	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   263: astore 7
    //   265: aload 7
    //   267: invokevirtual 84	java/io/File:exists	()Z
    //   270: ifeq +30 -> 300
    //   273: aload_0
    //   274: getfield 44	com/oneplus/cache/DiskLruCache:m_CacheFiles	Ljava/util/Map;
    //   277: aload 4
    //   279: aload 7
    //   281: invokeinterface 168 3 0
    //   286: pop
    //   287: aload_0
    //   288: aload 4
    //   290: aload 4
    //   292: aload 7
    //   294: invokevirtual 172	java/io/File:length	()J
    //   297: invokevirtual 176	com/oneplus/cache/DiskLruCache:addEntryDirectly	(Ljava/lang/Object;Ljava/lang/Object;J)V
    //   300: iload_1
    //   301: iconst_1
    //   302: isub
    //   303: istore_1
    //   304: goto -88 -> 216
    //   307: aload 8
    //   309: astore 4
    //   311: aload 5
    //   313: ifnull +12 -> 325
    //   316: aload 5
    //   318: invokevirtual 179	java/io/ObjectInputStream:close	()V
    //   321: aload 8
    //   323: astore 4
    //   325: aload_3
    //   326: ifnull +7 -> 333
    //   329: aload_3
    //   330: invokevirtual 180	java/io/FileInputStream:close	()V
    //   333: aload 4
    //   335: astore_3
    //   336: aload_3
    //   337: ifnull +36 -> 373
    //   340: aload_3
    //   341: athrow
    //   342: astore_3
    //   343: aload_0
    //   344: getfield 112	com/oneplus/cache/DiskLruCache:TAG	Ljava/lang/String;
    //   347: new 114	java/lang/StringBuilder
    //   350: dup
    //   351: invokespecial 115	java/lang/StringBuilder:<init>	()V
    //   354: ldc -74
    //   356: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   359: aload_0
    //   360: getfield 61	com/oneplus/cache/DiskLruCache:m_SnapshotFile	Ljava/io/File;
    //   363: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   366: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   369: aload_3
    //   370: invokestatic 185	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   373: aload 10
    //   375: invokevirtual 188	java/util/HashSet:isEmpty	()Z
    //   378: ifne +228 -> 606
    //   381: aload 10
    //   383: invokeinterface 194 1 0
    //   388: astore_3
    //   389: aload_3
    //   390: invokeinterface 199 1 0
    //   395: ifeq +211 -> 606
    //   398: aload_3
    //   399: invokeinterface 202 1 0
    //   404: checkcast 102	java/lang/String
    //   407: astore 4
    //   409: new 48	java/io/File
    //   412: dup
    //   413: aload_0
    //   414: getfield 59	com/oneplus/cache/DiskLruCache:m_Directory	Ljava/io/File;
    //   417: aload 4
    //   419: invokespecial 57	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   422: invokevirtual 205	java/io/File:delete	()Z
    //   425: pop
    //   426: goto -37 -> 389
    //   429: astore 4
    //   431: goto -42 -> 389
    //   434: astore 4
    //   436: goto -111 -> 325
    //   439: astore 5
    //   441: aload 5
    //   443: astore_3
    //   444: aload 4
    //   446: ifnull -110 -> 336
    //   449: aload 4
    //   451: aload 5
    //   453: if_acmpeq -120 -> 333
    //   456: aload 4
    //   458: aload 5
    //   460: invokevirtual 209	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   463: aload 4
    //   465: astore_3
    //   466: goto -130 -> 336
    //   469: astore_3
    //   470: aload_3
    //   471: athrow
    //   472: astore 4
    //   474: aload_3
    //   475: astore 6
    //   477: aload 7
    //   479: ifnull +8 -> 487
    //   482: aload 7
    //   484: invokevirtual 179	java/io/ObjectInputStream:close	()V
    //   487: aload 6
    //   489: astore_3
    //   490: aload 5
    //   492: ifnull +8 -> 500
    //   495: aload 5
    //   497: invokevirtual 180	java/io/FileInputStream:close	()V
    //   500: aload_3
    //   501: astore 5
    //   503: aload 5
    //   505: ifnull +98 -> 603
    //   508: aload 5
    //   510: athrow
    //   511: astore_3
    //   512: aload_0
    //   513: getfield 112	com/oneplus/cache/DiskLruCache:TAG	Ljava/lang/String;
    //   516: new 114	java/lang/StringBuilder
    //   519: dup
    //   520: invokespecial 115	java/lang/StringBuilder:<init>	()V
    //   523: ldc -45
    //   525: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   528: aload_0
    //   529: getfield 61	com/oneplus/cache/DiskLruCache:m_SnapshotFile	Ljava/io/File;
    //   532: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   535: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   538: aload_3
    //   539: invokestatic 137	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   542: goto -169 -> 373
    //   545: astore 7
    //   547: aload 7
    //   549: astore_3
    //   550: aload 6
    //   552: ifnull -62 -> 490
    //   555: aload 6
    //   557: aload 7
    //   559: if_acmpeq -72 -> 487
    //   562: aload 6
    //   564: aload 7
    //   566: invokevirtual 209	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   569: aload 6
    //   571: astore_3
    //   572: goto -82 -> 490
    //   575: astore 6
    //   577: aload 6
    //   579: astore 5
    //   581: aload_3
    //   582: ifnull -79 -> 503
    //   585: aload_3
    //   586: aload 6
    //   588: if_acmpeq -88 -> 500
    //   591: aload_3
    //   592: aload 6
    //   594: invokevirtual 209	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   597: aload_3
    //   598: astore 5
    //   600: goto -97 -> 503
    //   603: aload 4
    //   605: athrow
    //   606: return
    //   607: astore_3
    //   608: aload 9
    //   610: astore 5
    //   612: aload 4
    //   614: astore 7
    //   616: aload_3
    //   617: astore 4
    //   619: goto -142 -> 477
    //   622: astore 8
    //   624: aload_3
    //   625: astore 5
    //   627: aload 4
    //   629: astore 7
    //   631: aload 8
    //   633: astore 4
    //   635: goto -158 -> 477
    //   638: astore 4
    //   640: aload 5
    //   642: astore 7
    //   644: aload_3
    //   645: astore 5
    //   647: goto -170 -> 477
    //   650: astore 4
    //   652: aload_3
    //   653: astore 5
    //   655: aload 4
    //   657: astore_3
    //   658: goto -188 -> 470
    //   661: astore 4
    //   663: aload 5
    //   665: astore 7
    //   667: aload_3
    //   668: astore 5
    //   670: aload 4
    //   672: astore_3
    //   673: goto -203 -> 470
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	676	0	this	DiskLruCache
    //   47	257	1	i	int
    //   21	146	2	bool	boolean
    //   42	11	3	arrayOfString	String[]
    //   115	28	3	localThrowable1	Throwable
    //   147	8	3	localThrowable2	Throwable
    //   199	142	3	localObject1	Object
    //   342	28	3	localEOFException	java.io.EOFException
    //   388	78	3	localObject2	Object
    //   469	6	3	localThrowable3	Throwable
    //   489	12	3	localObject3	Object
    //   511	28	3	localThrowable4	Throwable
    //   549	49	3	localObject4	Object
    //   607	46	3	localObject5	Object
    //   657	16	3	localObject6	Object
    //   58	360	4	localObject7	Object
    //   429	1	4	localThrowable5	Throwable
    //   434	30	4	localThrowable6	Throwable
    //   472	141	4	localObject8	Object
    //   617	17	4	localObject9	Object
    //   638	1	4	localObject10	Object
    //   650	6	4	localThrowable7	Throwable
    //   661	10	4	localThrowable8	Throwable
    //   180	137	5	localObjectInputStream	java.io.ObjectInputStream
    //   439	57	5	localThrowable9	Throwable
    //   501	168	5	localObject11	Object
    //   171	399	6	localThrowable10	Throwable
    //   575	18	6	localThrowable11	Throwable
    //   186	297	7	localObject12	Object
    //   545	20	7	localThrowable12	Throwable
    //   614	52	7	localObject13	Object
    //   174	148	8	localObject14	Object
    //   622	10	8	localObject15	Object
    //   177	432	9	localObject16	Object
    //   33	349	10	localHashSet	java.util.HashSet
    // Exception table:
    //   from	to	target	type
    //   4	22	115	java/lang/Throwable
    //   85	114	115	java/lang/Throwable
    //   35	48	147	java/lang/Throwable
    //   52	78	147	java/lang/Throwable
    //   158	166	342	java/io/EOFException
    //   316	321	342	java/io/EOFException
    //   329	333	342	java/io/EOFException
    //   340	342	342	java/io/EOFException
    //   456	463	342	java/io/EOFException
    //   482	487	342	java/io/EOFException
    //   495	500	342	java/io/EOFException
    //   508	511	342	java/io/EOFException
    //   562	569	342	java/io/EOFException
    //   591	597	342	java/io/EOFException
    //   603	606	342	java/io/EOFException
    //   409	426	429	java/lang/Throwable
    //   316	321	434	java/lang/Throwable
    //   329	333	439	java/lang/Throwable
    //   188	200	469	java/lang/Throwable
    //   470	472	472	finally
    //   158	166	511	java/lang/Throwable
    //   340	342	511	java/lang/Throwable
    //   456	463	511	java/lang/Throwable
    //   508	511	511	java/lang/Throwable
    //   562	569	511	java/lang/Throwable
    //   591	597	511	java/lang/Throwable
    //   603	606	511	java/lang/Throwable
    //   482	487	545	java/lang/Throwable
    //   495	500	575	java/lang/Throwable
    //   188	200	607	finally
    //   200	210	622	finally
    //   210	216	638	finally
    //   220	300	638	finally
    //   200	210	650	java/lang/Throwable
    //   210	216	661	java/lang/Throwable
    //   220	300	661	java/lang/Throwable
  }
  
  private void clearTempFiles()
  {
    synchronized (this.m_TempFiles)
    {
      boolean bool = this.m_TempFiles.isEmpty();
      if (bool) {
        return;
      }
      Map.Entry[] arrayOfEntry = (Map.Entry[])this.m_TempFiles.entrySet().toArray(new Map.Entry[0]);
      int i = arrayOfEntry.length - 1;
      for (;;)
      {
        if (i >= 0)
        {
          Pair localPair = (Pair)arrayOfEntry[i].getValue();
          long l1 = SystemClock.elapsedRealtime();
          long l2 = ((Long)localPair.second).longValue();
          if (l1 - l2 >= 1000L) {}
          try
          {
            this.m_TempFiles.remove(arrayOfEntry[i].getKey());
            ((File)localPair.first).delete();
            i -= 1;
          }
          catch (Throwable localThrowable)
          {
            for (;;)
            {
              Log.e(this.TAG, "clearTempFiles() - Fail to delete file " + localPair.first, localThrowable);
            }
          }
        }
      }
    }
    if (!this.m_TempFiles.isEmpty()) {
      sendMessageToWorkerThread(Message.obtain(null, 10010));
    }
  }
  
  /* Error */
  private void createSnapshot()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aconst_null
    //   4: astore 5
    //   6: new 262	java/util/ArrayList
    //   9: dup
    //   10: invokespecial 263	java/util/ArrayList:<init>	()V
    //   13: astore 9
    //   15: aload_0
    //   16: getfield 266	com/oneplus/cache/DiskLruCache:syncLock	Ljava/lang/Object;
    //   19: astore_2
    //   20: aload_2
    //   21: monitorenter
    //   22: aload_0
    //   23: invokevirtual 269	com/oneplus/cache/DiskLruCache:listKeys	()Ljava/util/Iterator;
    //   26: astore_3
    //   27: aload_3
    //   28: invokeinterface 199 1 0
    //   33: ifeq +28 -> 61
    //   36: aload 9
    //   38: aload_3
    //   39: invokeinterface 202 1 0
    //   44: checkcast 159	java/io/Serializable
    //   47: invokeinterface 272 2 0
    //   52: pop
    //   53: goto -26 -> 27
    //   56: astore_3
    //   57: aload_2
    //   58: monitorexit
    //   59: aload_3
    //   60: athrow
    //   61: aload_2
    //   62: monitorexit
    //   63: aload_0
    //   64: getfield 61	com/oneplus/cache/DiskLruCache:m_SnapshotFile	Ljava/io/File;
    //   67: invokevirtual 84	java/io/File:exists	()Z
    //   70: ifeq +13 -> 83
    //   73: aload_0
    //   74: getfield 61	com/oneplus/cache/DiskLruCache:m_SnapshotFile	Ljava/io/File;
    //   77: invokevirtual 205	java/io/File:delete	()Z
    //   80: ifeq +140 -> 220
    //   83: aconst_null
    //   84: astore 8
    //   86: aconst_null
    //   87: astore 4
    //   89: aconst_null
    //   90: astore 7
    //   92: aconst_null
    //   93: astore_3
    //   94: new 274	java/io/FileOutputStream
    //   97: dup
    //   98: aload_0
    //   99: getfield 61	com/oneplus/cache/DiskLruCache:m_SnapshotFile	Ljava/io/File;
    //   102: invokespecial 275	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   105: astore_2
    //   106: new 277	java/io/ObjectOutputStream
    //   109: dup
    //   110: aload_2
    //   111: invokespecial 280	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   114: astore 4
    //   116: aload 4
    //   118: aload 9
    //   120: invokeinterface 283 1 0
    //   125: invokevirtual 287	java/io/ObjectOutputStream:writeInt	(I)V
    //   128: aload 9
    //   130: invokeinterface 283 1 0
    //   135: iconst_1
    //   136: isub
    //   137: istore_1
    //   138: iload_1
    //   139: iflt +253 -> 392
    //   142: aload 9
    //   144: iload_1
    //   145: invokeinterface 291 2 0
    //   150: checkcast 159	java/io/Serializable
    //   153: astore 7
    //   155: aload_0
    //   156: getfield 44	com/oneplus/cache/DiskLruCache:m_CacheFiles	Ljava/util/Map;
    //   159: astore_3
    //   160: aload_3
    //   161: monitorenter
    //   162: aload_0
    //   163: getfield 44	com/oneplus/cache/DiskLruCache:m_CacheFiles	Ljava/util/Map;
    //   166: aload 7
    //   168: invokeinterface 293 2 0
    //   173: checkcast 48	java/io/File
    //   176: astore 8
    //   178: aload_3
    //   179: monitorexit
    //   180: aload 8
    //   182: ifnonnull +163 -> 345
    //   185: aload_0
    //   186: getfield 112	com/oneplus/cache/DiskLruCache:TAG	Ljava/lang/String;
    //   189: new 114	java/lang/StringBuilder
    //   192: dup
    //   193: invokespecial 115	java/lang/StringBuilder:<init>	()V
    //   196: ldc_w 295
    //   199: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   202: aload 7
    //   204: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   207: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   210: invokestatic 297	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   213: iload_1
    //   214: iconst_1
    //   215: isub
    //   216: istore_1
    //   217: goto -79 -> 138
    //   220: aload_0
    //   221: getfield 112	com/oneplus/cache/DiskLruCache:TAG	Ljava/lang/String;
    //   224: new 114	java/lang/StringBuilder
    //   227: dup
    //   228: invokespecial 115	java/lang/StringBuilder:<init>	()V
    //   231: ldc_w 299
    //   234: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   237: aload_0
    //   238: getfield 61	com/oneplus/cache/DiskLruCache:m_SnapshotFile	Ljava/io/File;
    //   241: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   244: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   247: invokestatic 134	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   250: return
    //   251: astore 6
    //   253: aload_3
    //   254: monitorexit
    //   255: aload 6
    //   257: athrow
    //   258: astore 5
    //   260: aload_2
    //   261: astore_3
    //   262: aload 5
    //   264: astore_2
    //   265: aload 4
    //   267: astore 6
    //   269: aload_3
    //   270: astore 4
    //   272: aload_2
    //   273: athrow
    //   274: astore_3
    //   275: aload_2
    //   276: astore 5
    //   278: aload 6
    //   280: ifnull +8 -> 288
    //   283: aload 6
    //   285: invokevirtual 300	java/io/ObjectOutputStream:close	()V
    //   288: aload 5
    //   290: astore_2
    //   291: aload 4
    //   293: ifnull +8 -> 301
    //   296: aload 4
    //   298: invokevirtual 301	java/io/FileOutputStream:close	()V
    //   301: aload_2
    //   302: astore 4
    //   304: aload 4
    //   306: ifnull +203 -> 509
    //   309: aload 4
    //   311: athrow
    //   312: astore_2
    //   313: aload_0
    //   314: getfield 112	com/oneplus/cache/DiskLruCache:TAG	Ljava/lang/String;
    //   317: new 114	java/lang/StringBuilder
    //   320: dup
    //   321: invokespecial 115	java/lang/StringBuilder:<init>	()V
    //   324: ldc_w 303
    //   327: invokevirtual 121	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   330: aload_0
    //   331: getfield 61	com/oneplus/cache/DiskLruCache:m_SnapshotFile	Ljava/io/File;
    //   334: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   337: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   340: aload_2
    //   341: invokestatic 137	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   344: return
    //   345: aload 4
    //   347: aload 7
    //   349: invokevirtual 307	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   352: aload 8
    //   354: ifnull +27 -> 381
    //   357: aload 8
    //   359: invokevirtual 84	java/io/File:exists	()Z
    //   362: ifeq +19 -> 381
    //   365: aload 4
    //   367: aload 8
    //   369: invokevirtual 310	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   372: invokestatic 100	com/oneplus/io/Path:getFileName	(Ljava/lang/String;)Ljava/lang/String;
    //   375: invokevirtual 307	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   378: goto -165 -> 213
    //   381: aload 4
    //   383: ldc_w 312
    //   386: invokevirtual 307	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   389: goto -176 -> 213
    //   392: aload 6
    //   394: astore_3
    //   395: aload 4
    //   397: ifnull +11 -> 408
    //   400: aload 4
    //   402: invokevirtual 300	java/io/ObjectOutputStream:close	()V
    //   405: aload 6
    //   407: astore_3
    //   408: aload_2
    //   409: ifnull +7 -> 416
    //   412: aload_2
    //   413: invokevirtual 301	java/io/FileOutputStream:close	()V
    //   416: aload_3
    //   417: astore_2
    //   418: aload_2
    //   419: ifnull +31 -> 450
    //   422: aload_2
    //   423: athrow
    //   424: astore 4
    //   426: aload 4
    //   428: astore_2
    //   429: aload_3
    //   430: ifnull -12 -> 418
    //   433: aload_3
    //   434: aload 4
    //   436: if_acmpeq -20 -> 416
    //   439: aload_3
    //   440: aload 4
    //   442: invokevirtual 209	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   445: aload_3
    //   446: astore_2
    //   447: goto -29 -> 418
    //   450: return
    //   451: astore 6
    //   453: aload 6
    //   455: astore_2
    //   456: aload 5
    //   458: ifnull -167 -> 291
    //   461: aload 5
    //   463: aload 6
    //   465: if_acmpeq -177 -> 288
    //   468: aload 5
    //   470: aload 6
    //   472: invokevirtual 209	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   475: aload 5
    //   477: astore_2
    //   478: goto -187 -> 291
    //   481: astore 5
    //   483: aload 5
    //   485: astore 4
    //   487: aload_2
    //   488: ifnull -184 -> 304
    //   491: aload_2
    //   492: aload 5
    //   494: if_acmpeq -193 -> 301
    //   497: aload_2
    //   498: aload 5
    //   500: invokevirtual 209	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   503: aload_2
    //   504: astore 4
    //   506: goto -202 -> 304
    //   509: aload_3
    //   510: athrow
    //   511: astore_3
    //   512: aload 8
    //   514: astore 4
    //   516: aload 7
    //   518: astore 6
    //   520: goto -242 -> 278
    //   523: astore_3
    //   524: aload_2
    //   525: astore 4
    //   527: aload 7
    //   529: astore 6
    //   531: goto -253 -> 278
    //   534: astore_2
    //   535: aload_3
    //   536: astore 6
    //   538: goto -266 -> 272
    //   541: astore 5
    //   543: aload_2
    //   544: astore 4
    //   546: aload_3
    //   547: astore 6
    //   549: aload 5
    //   551: astore_2
    //   552: goto -280 -> 272
    //   555: astore_3
    //   556: aload 4
    //   558: astore 6
    //   560: aload_2
    //   561: astore 4
    //   563: goto -285 -> 278
    //   566: astore_2
    //   567: goto -254 -> 313
    //   570: astore_3
    //   571: goto -163 -> 408
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	574	0	this	DiskLruCache
    //   137	80	1	i	int
    //   19	283	2	localObject1	Object
    //   312	101	2	localThrowable1	Throwable
    //   417	108	2	localObject2	Object
    //   534	10	2	localThrowable2	Throwable
    //   551	10	2	localThrowable3	Throwable
    //   566	1	2	localThrowable4	Throwable
    //   26	13	3	localIterator	java.util.Iterator
    //   56	4	3	localObject3	Object
    //   274	1	3	localObject5	Object
    //   394	116	3	localObject6	Object
    //   511	1	3	localObject7	Object
    //   523	24	3	localObject8	Object
    //   555	1	3	localObject9	Object
    //   570	1	3	localThrowable5	Throwable
    //   87	314	4	localObject10	Object
    //   424	17	4	localThrowable6	Throwable
    //   485	77	4	localObject11	Object
    //   4	1	5	localObject12	Object
    //   258	5	5	localThrowable7	Throwable
    //   276	200	5	localObject13	Object
    //   481	18	5	localThrowable8	Throwable
    //   541	9	5	localThrowable9	Throwable
    //   1	1	6	localObject14	Object
    //   251	5	6	localObject15	Object
    //   267	139	6	localObject16	Object
    //   451	20	6	localThrowable10	Throwable
    //   518	41	6	localObject17	Object
    //   90	438	7	localSerializable	Serializable
    //   84	429	8	localFile	File
    //   13	130	9	localArrayList	java.util.ArrayList
    // Exception table:
    //   from	to	target	type
    //   22	27	56	finally
    //   27	53	56	finally
    //   162	178	251	finally
    //   116	138	258	java/lang/Throwable
    //   142	162	258	java/lang/Throwable
    //   178	180	258	java/lang/Throwable
    //   185	213	258	java/lang/Throwable
    //   253	258	258	java/lang/Throwable
    //   345	352	258	java/lang/Throwable
    //   357	378	258	java/lang/Throwable
    //   381	389	258	java/lang/Throwable
    //   272	274	274	finally
    //   309	312	312	java/lang/Throwable
    //   468	475	312	java/lang/Throwable
    //   497	503	312	java/lang/Throwable
    //   509	511	312	java/lang/Throwable
    //   412	416	424	java/lang/Throwable
    //   283	288	451	java/lang/Throwable
    //   296	301	481	java/lang/Throwable
    //   94	106	511	finally
    //   106	116	523	finally
    //   94	106	534	java/lang/Throwable
    //   106	116	541	java/lang/Throwable
    //   116	138	555	finally
    //   142	162	555	finally
    //   178	180	555	finally
    //   185	213	555	finally
    //   253	258	555	finally
    //   345	352	555	finally
    //   357	378	555	finally
    //   381	389	555	finally
    //   422	424	566	java/lang/Throwable
    //   439	445	566	java/lang/Throwable
    //   400	405	570	java/lang/Throwable
  }
  
  private File generateFile()
  {
    char[] arrayOfChar = new char[16];
    File localFile;
    do
    {
      int i = arrayOfChar.length - 1;
      if (i >= 0)
      {
        int j = (int)(Math.random() * 36.0D);
        if (j < 10) {
          arrayOfChar[i] = ((char)(j + 48));
        }
        for (;;)
        {
          i -= 1;
          break;
          arrayOfChar[i] = ((char)(j - 10 + 97));
        }
      }
      localFile = new File(this.m_Directory, new String(arrayOfChar));
    } while (localFile.exists());
    return localFile;
  }
  
  private void increateNewOperationCount()
  {
    synchronized (this.syncLock)
    {
      this.m_NewOperationCount += 1;
      if (this.m_NewOperationCount >= 64)
      {
        this.m_NewOperationCount = 0;
        sendMessageToWorkerThread(Message.obtain(null, 10001));
      }
      return;
    }
  }
  
  public boolean add(TKey paramTKey, TValue paramTValue)
  {
    if (super.add(paramTKey, paramTValue))
    {
      increateNewOperationCount();
      return true;
    }
    return false;
  }
  
  protected Object addEntry(TKey paramTKey, TValue arg2)
  {
    Object localObject;
    synchronized (this.m_TempFiles)
    {
      localObject = (Pair)this.m_TempFiles.remove(paramTKey);
      if (this.m_TempFiles.isEmpty()) {
        removeWorkerThreadMessages(10010);
      }
      if (localObject == null) {
        break label97;
      }
    }
    synchronized (this.m_CacheFiles)
    {
      localObject = (File)this.m_CacheFiles.put(paramTKey, (File)((Pair)localObject).first);
      if (localObject != null) {
        ((File)localObject).delete();
      }
      return paramTKey;
      paramTKey = finally;
      throw paramTKey;
    }
    label97:
    Log.e(this.TAG, "addEntry() - No cache file for " + paramTKey);
    return paramTKey;
  }
  
  public void clear()
  {
    super.clear();
    increateNewOperationCount();
  }
  
  public void close()
  {
    sendMessageToWorkerThread(Message.obtain(null, 10001));
    super.close();
  }
  
  public void flush()
  {
    sendMessageToWorkerThread(Message.obtain(null, 10001));
  }
  
  protected TValue get(TKey paramTKey, Object paramObject, TValue paramTValue, long paramLong)
  {
    synchronized (this.m_CacheFiles)
    {
      paramObject = (File)this.m_CacheFiles.get(paramTKey);
      if (paramObject == null) {}
    }
    try
    {
      if (((File)paramObject).exists())
      {
        ??? = readFromFile(paramTKey, (File)paramObject, paramTValue);
        return (TValue)???;
        paramTKey = finally;
        throw paramTKey;
      }
      this.m_CacheFiles.remove(paramTKey);
      return paramTValue;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "get() - Fail to read " + paramTKey + " from " + paramObject, localThrowable);
      return paramTValue;
    }
    finally
    {
      increateNewOperationCount();
    }
  }
  
  protected long getSizeInBytes(TKey paramTKey, TValue arg2)
  {
    localFile = generateFile();
    try
    {
      writeToFile(paramTKey, ???, localFile);
      if ((!localFile.exists()) || (localFile.length() == 0L))
      {
        localFile.delete();
        Log.w(this.TAG, "getSizeInBytes() - No content in " + localFile);
        return -1L;
      }
    }
    catch (Throwable ???)
    {
      Log.e(this.TAG, "getSizeInBytes() - Fail to write value to file", ???);
      try
      {
        localFile.delete();
        return -1L;
      }
      catch (Throwable ???) {}
      for (;;)
      {
        long l;
        synchronized (this.m_TempFiles)
        {
          Pair localPair = (Pair)this.m_TempFiles.put(paramTKey, new Pair(localFile, Long.valueOf(SystemClock.elapsedRealtime())));
          if (localPair != null) {
            ((File)localPair.first).delete();
          }
        }
        sendMessageToWorkerThread(Message.obtain(null, 10010));
      }
    }
  }
  
  protected void handleWorkerThreadMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleWorkerThreadMessage(paramMessage);
      return;
    case 10000: 
      buildCacheFromSnapshot();
      return;
    case 10010: 
      clearTempFiles();
      return;
    }
    createSnapshot();
  }
  
  protected abstract TValue readFromFile(TKey paramTKey, File paramFile, TValue paramTValue)
    throws Exception;
  
  public boolean remove(TKey paramTKey)
  {
    if (super.remove(paramTKey))
    {
      increateNewOperationCount();
      return true;
    }
    return false;
  }
  
  protected void removeEntry(TKey paramTKey, Object arg2)
  {
    synchronized (this.m_CacheFiles)
    {
      paramTKey = (File)this.m_CacheFiles.remove(paramTKey);
      if (paramTKey == null) {
        return;
      }
    }
    try
    {
      if ((paramTKey.exists()) && (!paramTKey.delete())) {
        Log.e(this.TAG, "removeEntry() - Fail to delete " + paramTKey);
      }
      return;
    }
    catch (Throwable ???)
    {
      Log.e(this.TAG, "removeEntry() - Fail to delete " + paramTKey, (Throwable)???);
    }
  }
  
  protected abstract void writeToFile(TKey paramTKey, TValue paramTValue, File paramFile)
    throws Exception;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/cache/DiskLruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */