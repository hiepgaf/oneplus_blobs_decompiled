package com.oneplus.gallery2.media;

import android.os.Handler;
import android.os.Message;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.BaseThread;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import com.oneplus.base.SimpleRef;
import com.oneplus.cache.Cache.RemovingPredication;
import com.oneplus.io.Path;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public abstract class ExternalMediaSource
  extends BaseMediaSource
{
  protected static final long DEFAULT_MEDIA_DATA_CACHE_CAPACITY = 67108864L;
  private static final String MEDIA_CACHE_STATE_FILE = "_STATE";
  private volatile long m_MediaDataCacheCapacity = 67108864L;
  private File m_MediaDataCacheDirectory;
  private volatile CachedMediaData m_MediaDataCacheHead;
  private final Object m_MediaDataCacheLock = new Object();
  private volatile long m_MediaDataCacheSize;
  private final Map<Serializable, CachedMediaData> m_MediaDataCacheTable = new HashMap();
  private volatile CachedMediaData m_MediaDataCacheTail;
  private volatile int m_MediaDataCacheTouchCount;
  private volatile WorkerThread m_WorkerThread;
  
  protected ExternalMediaSource(String paramString, BaseApplication paramBaseApplication)
  {
    this(paramString, paramBaseApplication, -1L);
  }
  
  protected ExternalMediaSource(String paramString, BaseApplication paramBaseApplication, long paramLong)
  {
    super(paramString, paramBaseApplication);
    if (paramLong <= 0L) {}
    for (int i = 1;; i = 0)
    {
      if (i == 0) {
        this.m_MediaDataCacheCapacity = paramLong;
      }
      return;
    }
  }
  
  private boolean checkMediaDataCacheSize()
  {
    for (;;)
    {
      label40:
      int j;
      label54:
      CachedMediaData localCachedMediaData2;
      synchronized (this.m_MediaDataCacheLock)
      {
        if (this.m_MediaDataCacheSize <= this.m_MediaDataCacheCapacity) {
          break label156;
        }
        i = 1;
        if (i == 0) {
          return true;
        }
        CachedMediaData localCachedMediaData1 = this.m_MediaDataCacheTail;
        i = 0;
        if (this.m_MediaDataCacheSize > this.m_MediaDataCacheCapacity) {
          break label168;
        }
        j = 1;
        if ((j != 0) || (localCachedMediaData1 == null)) {
          break label173;
        }
        localCachedMediaData2 = localCachedMediaData1.prevData;
        if (!onRemovingCachedMediaData(localCachedMediaData1)) {
          break label161;
        }
        this.m_MediaDataCacheSize -= localCachedMediaData1.getSize();
        removeCachedMediaDataDirectly(localCachedMediaData1);
        i += 1;
        break label161;
        label109:
        if (this.m_MediaDataCacheSize > this.m_MediaDataCacheCapacity)
        {
          i = 1;
          break label180;
          label126:
          return bool;
          label131:
          touchMediaDataCache();
        }
      }
      int i = 0;
      label156:
      label161:
      label168:
      label173:
      label180:
      while (i != 0)
      {
        bool = false;
        break label126;
        i = 0;
        break;
        Object localObject2 = localCachedMediaData2;
        break label40;
        j = 0;
        break label54;
        if (i > 0) {
          break label131;
        }
        break label109;
      }
      boolean bool = true;
    }
  }
  
  private File generateEmptyFile(File paramFile)
  {
    char[] arrayOfChar = new char[16];
    File localFile;
    do
    {
      int i = arrayOfChar.length;
      for (;;)
      {
        i -= 1;
        if (i < 0) {
          break;
        }
        int j = (int)(Math.random() * 36.0D);
        if (j >= 10) {
          arrayOfChar[i] = ((char)(char)(j - 10 + 97));
        } else {
          arrayOfChar[i] = ((char)(char)(j + 48));
        }
      }
      localFile = new File(paramFile, new String(arrayOfChar));
    } while (localFile.exists());
    return localFile;
  }
  
  private void onCachedMediaDataSizeChanged(CachedMediaData paramCachedMediaData, long paramLong1, long paramLong2)
  {
    if (paramLong1 < paramLong2) {}
    for (int i = 1; i == 0; i = 0) {
      return;
    }
    synchronized (this.m_MediaDataCacheLock)
    {
      if (this.m_MediaDataCacheTable.get(paramCachedMediaData.key) == paramCachedMediaData)
      {
        checkMediaDataCacheSize();
        return;
      }
      return;
    }
  }
  
  private void removeCachedMediaDataDirectly(CachedMediaData paramCachedMediaData)
  {
    if (this.m_MediaDataCacheTable.remove(paramCachedMediaData.key) != null)
    {
      if (this.m_MediaDataCacheHead == paramCachedMediaData) {
        break label62;
      }
      if (this.m_MediaDataCacheTail == paramCachedMediaData) {
        break label73;
      }
      label32:
      if (paramCachedMediaData.prevData != null) {
        break label84;
      }
      label39:
      if (paramCachedMediaData.nextData != null) {
        break label98;
      }
    }
    for (;;)
    {
      paramCachedMediaData.prevData = null;
      paramCachedMediaData.nextData = null;
      paramCachedMediaData.invalidate();
      return;
      return;
      label62:
      this.m_MediaDataCacheHead = paramCachedMediaData.nextData;
      break;
      label73:
      this.m_MediaDataCacheTail = paramCachedMediaData.prevData;
      break label32;
      label84:
      paramCachedMediaData.prevData.nextData = paramCachedMediaData.nextData;
      break label39;
      label98:
      paramCachedMediaData.nextData.prevData = paramCachedMediaData.prevData;
    }
  }
  
  private void setupMediaDataCache()
  {
    synchronized (this.m_MediaDataCacheLock)
    {
      Log.v(this.TAG, "setupMediaDataCache()");
      this.m_MediaDataCacheDirectory = onSetupMediaDataCacheDirectory(BaseApplication.current());
      if (this.m_MediaDataCacheDirectory != null)
      {
        if (this.m_MediaDataCacheDirectory.exists()) {
          break label77;
        }
        if (!this.m_MediaDataCacheDirectory.mkdir()) {
          break label126;
        }
      }
      label77:
      while (this.m_MediaDataCacheDirectory.isDirectory())
      {
        onSetupMediaDataCache(this.m_MediaDataCacheDirectory);
        return;
        Log.w(this.TAG, "setupMediaDataCache() - No directory");
        return;
      }
      Log.e(this.TAG, "setupMediaDataCache() - " + this.m_MediaDataCacheDirectory + " is not a directory");
      this.m_MediaDataCacheDirectory = null;
      return;
      label126:
      Log.e(this.TAG, "setupMediaDataCache() - Fail to create " + this.m_MediaDataCacheDirectory);
      this.m_MediaDataCacheDirectory = null;
      return;
    }
  }
  
  private void touchCachedMediaData(CachedMediaData paramCachedMediaData)
  {
    for (;;)
    {
      synchronized (this.m_MediaDataCacheLock)
      {
        if (this.m_MediaDataCacheHead != paramCachedMediaData)
        {
          if (this.m_MediaDataCacheHead == null)
          {
            if (this.m_MediaDataCacheTail == paramCachedMediaData) {
              break label88;
            }
            if (paramCachedMediaData.prevData != null) {
              break label99;
            }
            if (paramCachedMediaData.nextData != null) {
              break label113;
            }
            paramCachedMediaData.nextData = this.m_MediaDataCacheHead;
            paramCachedMediaData.prevData = null;
            this.m_MediaDataCacheHead = paramCachedMediaData;
            touchMediaDataCache();
          }
        }
        else {
          return;
        }
        this.m_MediaDataCacheHead.prevData = paramCachedMediaData;
      }
      label88:
      this.m_MediaDataCacheTail = paramCachedMediaData.prevData;
      continue;
      label99:
      paramCachedMediaData.prevData.nextData = paramCachedMediaData.nextData;
      continue;
      label113:
      paramCachedMediaData.nextData.prevData = paramCachedMediaData.prevData;
    }
  }
  
  private void touchMediaDataCache()
  {
    Object localObject6 = null;
    Object localObject3 = null;
    synchronized (this.m_MediaDataCacheLock)
    {
      File localFile;
      if (this.m_MediaDataCacheDirectory != null)
      {
        this.m_MediaDataCacheTouchCount += 1;
        if (this.m_MediaDataCacheTouchCount < 32) {
          break label154;
        }
        this.m_MediaDataCacheTouchCount = 0;
        localFile = new File(this.m_MediaDataCacheDirectory, "_STATE");
        boolean bool = localFile.exists();
        if (bool) {
          break label158;
        }
      }
      Object localObject1;
      label114:
      label154:
      label158:
      while (localFile.delete())
      {
        localObject1 = localObject6;
        try
        {
          localFileOutputStream = new FileOutputStream(localFile);
          try
          {
            localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
          }
          finally {}
        }
        finally
        {
          if (localObject1 == null) {}
        }
        try
        {
          localObjectOutputStream.writeInt(this.m_MediaDataCacheTable.size());
          localObject1 = this.m_MediaDataCacheHead;
          if (localObject1 == null) {
            break label337;
          }
          localObjectOutputStream.writeObject(((CachedMediaData)localObject1).key);
          localObjectOutputStream.writeUTF(Path.getFileName(((CachedMediaData)localObject1).m_File.getAbsolutePath()));
          localObject1 = ((CachedMediaData)localObject1).nextData;
          break label114;
          return;
        }
        finally
        {
          if (localObjectOutputStream != null) {
            break label284;
          }
          for (;;)
          {
            try
            {
              throw localThrowable3;
            }
            finally {}
            localObjectOutputStream.close();
            continue;
            do
            {
              Throwable localThrowable2 = localThrowable4;
              localFileOutputStream.close();
              break;
              localThrowable2 = localThrowable3;
              localThrowable3.addSuppressed(localThrowable4);
              break label219;
              localThrowable2 = localThrowable3;
              localFileOutputStream.close();
              break label224;
              localThrowable2 = localThrowable3;
              break label238;
              localThrowable2.addSuppressed(localThrowable3);
              break label238;
              if (localObjectOutputStream != null) {
                break label199;
              }
            } while (localFileOutputStream != null);
            break;
          }
          break label209;
          Object localObject5 = localObject7;
          break label219;
        }
        return;
      }
      Log.e(this.TAG, "touchMediaDataCache() - Fail to delete old state file " + localFile);
      return;
      return;
      label199:
      localObjectOutputStream.close();
      break label342;
      label209:
      if (localObject3 != null) {
        if (localObject3 == localThrowable4)
        {
          label219:
          if (localFileOutputStream != null) {
            break label314;
          }
          label224:
          localObject1 = localObject3;
          throw ((Throwable)localObject3);
          if (localObject1 != localObject4) {
            break label329;
          }
          try
          {
            label238:
            throw ((Throwable)localObject1);
          }
          catch (Throwable localThrowable1)
          {
            Log.e(this.TAG, "touchMediaDataCache() - Fail to save state to " + localFile, localThrowable1);
          }
        }
      }
    }
  }
  
  /* Error */
  protected CachedMediaData addFileToMediaDataCache(Serializable paramSerializable, File paramFile, boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 4
    //   3: aload_0
    //   4: getfield 62	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheLock	Ljava/lang/Object;
    //   7: astore 6
    //   9: aload 6
    //   11: monitorenter
    //   12: aload_0
    //   13: getfield 184	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheDirectory	Ljava/io/File;
    //   16: ifnull +129 -> 145
    //   19: aload_0
    //   20: aload_1
    //   21: invokevirtual 289	com/oneplus/gallery2/media/ExternalMediaSource:removeCachedMediaData	(Ljava/io/Serializable;)Z
    //   24: pop
    //   25: aload_2
    //   26: ifnull +134 -> 160
    //   29: aload_2
    //   30: invokevirtual 136	java/io/File:exists	()Z
    //   33: istore 5
    //   35: iload 5
    //   37: ifeq +128 -> 165
    //   40: aload_0
    //   41: aload_0
    //   42: getfield 184	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheDirectory	Ljava/io/File;
    //   45: invokespecial 291	com/oneplus/gallery2/media/ExternalMediaSource:generateEmptyFile	(Ljava/io/File;)Ljava/io/File;
    //   48: astore 7
    //   50: iload_3
    //   51: ifne +149 -> 200
    //   54: new 8	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData
    //   57: dup
    //   58: aload_0
    //   59: aload_1
    //   60: aload 7
    //   62: invokespecial 294	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:<init>	(Lcom/oneplus/gallery2/media/ExternalMediaSource;Ljava/io/Serializable;Ljava/io/File;)V
    //   65: astore 7
    //   67: aload_0
    //   68: getfield 153	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheHead	Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;
    //   71: ifnonnull +176 -> 247
    //   74: aload_0
    //   75: getfield 95	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheTail	Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;
    //   78: ifnull +187 -> 265
    //   81: aload 7
    //   83: aload_0
    //   84: getfield 153	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheHead	Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;
    //   87: invokestatic 161	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:access$3	(Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;)V
    //   90: aload_0
    //   91: aload 7
    //   93: putfield 153	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheHead	Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;
    //   96: aload_0
    //   97: getfield 67	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheTable	Ljava/util/Map;
    //   100: aload_1
    //   101: aload 7
    //   103: invokeinterface 298 3 0
    //   108: pop
    //   109: aload_0
    //   110: aload_0
    //   111: getfield 93	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheSize	J
    //   114: aload 7
    //   116: invokevirtual 107	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:getSize	()J
    //   119: ladd
    //   120: putfield 93	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheSize	J
    //   123: aload_0
    //   124: invokespecial 148	com/oneplus/gallery2/media/ExternalMediaSource:checkMediaDataCacheSize	()Z
    //   127: ifeq +147 -> 274
    //   130: aload_0
    //   131: invokespecial 113	com/oneplus/gallery2/media/ExternalMediaSource:touchMediaDataCache	()V
    //   134: aload 6
    //   136: monitorexit
    //   137: iload 4
    //   139: ifne +160 -> 299
    //   142: aload 7
    //   144: areturn
    //   145: aload_0
    //   146: getfield 78	com/oneplus/gallery2/media/ExternalMediaSource:TAG	Ljava/lang/String;
    //   149: ldc_w 300
    //   152: invokestatic 196	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   155: aload 6
    //   157: monitorexit
    //   158: aconst_null
    //   159: areturn
    //   160: aload 6
    //   162: monitorexit
    //   163: aconst_null
    //   164: areturn
    //   165: aload_0
    //   166: getfield 78	com/oneplus/gallery2/media/ExternalMediaSource:TAG	Ljava/lang/String;
    //   169: new 201	java/lang/StringBuilder
    //   172: dup
    //   173: ldc_w 302
    //   176: invokespecial 206	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   179: aload_2
    //   180: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   183: ldc_w 304
    //   186: invokevirtual 215	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   189: invokevirtual 219	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: invokestatic 222	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   195: aload 6
    //   197: monitorexit
    //   198: aconst_null
    //   199: areturn
    //   200: aload 7
    //   202: invokevirtual 268	java/io/File:delete	()Z
    //   205: pop
    //   206: aload_2
    //   207: aload 7
    //   209: invokevirtual 308	java/io/File:renameTo	(Ljava/io/File;)Z
    //   212: ifne +298 -> 510
    //   215: aload 7
    //   217: invokevirtual 311	java/io/File:createNewFile	()Z
    //   220: istore_3
    //   221: iload_3
    //   222: ifne -168 -> 54
    //   225: aload 6
    //   227: monitorexit
    //   228: aconst_null
    //   229: areturn
    //   230: astore_1
    //   231: aload_0
    //   232: getfield 78	com/oneplus/gallery2/media/ExternalMediaSource:TAG	Ljava/lang/String;
    //   235: ldc_w 313
    //   238: aload_1
    //   239: invokestatic 278	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   242: aload 6
    //   244: monitorexit
    //   245: aconst_null
    //   246: areturn
    //   247: aload_0
    //   248: getfield 153	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheHead	Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;
    //   251: aload 7
    //   253: invokestatic 159	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:access$2	(Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;)V
    //   256: goto -182 -> 74
    //   259: astore_1
    //   260: aload 6
    //   262: monitorexit
    //   263: aload_1
    //   264: athrow
    //   265: aload_0
    //   266: aload 7
    //   268: putfield 95	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheTail	Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;
    //   271: goto -190 -> 81
    //   274: aload_0
    //   275: aload_0
    //   276: getfield 93	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheSize	J
    //   279: aload 7
    //   281: invokevirtual 107	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:getSize	()J
    //   284: lsub
    //   285: putfield 93	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheSize	J
    //   288: aload_0
    //   289: aload 7
    //   291: invokespecial 110	com/oneplus/gallery2/media/ExternalMediaSource:removeCachedMediaDataDirectly	(Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;)V
    //   294: aload 6
    //   296: monitorexit
    //   297: aconst_null
    //   298: areturn
    //   299: new 315	java/io/FileInputStream
    //   302: dup
    //   303: aload_2
    //   304: invokespecial 316	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   307: astore 8
    //   309: aload 7
    //   311: invokevirtual 320	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:openOutputStream	()Ljava/io/OutputStream;
    //   314: astore 6
    //   316: sipush 4096
    //   319: newarray <illegal type>
    //   321: astore_2
    //   322: aload 8
    //   324: aload_2
    //   325: invokevirtual 324	java/io/FileInputStream:read	([B)I
    //   328: istore 4
    //   330: iload 4
    //   332: ifle +23 -> 355
    //   335: aload 6
    //   337: aload_2
    //   338: iconst_0
    //   339: iload 4
    //   341: invokevirtual 330	java/io/OutputStream:write	([BII)V
    //   344: aload 8
    //   346: aload_2
    //   347: invokevirtual 324	java/io/FileInputStream:read	([B)I
    //   350: istore 4
    //   352: goto -22 -> 330
    //   355: aload 6
    //   357: ifnonnull +54 -> 411
    //   360: aload 8
    //   362: ifnull +145 -> 507
    //   365: aload 8
    //   367: invokevirtual 331	java/io/FileInputStream:close	()V
    //   370: aload 7
    //   372: areturn
    //   373: astore_2
    //   374: aconst_null
    //   375: astore 6
    //   377: aload 6
    //   379: ifnull +113 -> 492
    //   382: aload 6
    //   384: aload_2
    //   385: if_acmpne +113 -> 498
    //   388: aload 6
    //   390: athrow
    //   391: astore_2
    //   392: aload_0
    //   393: getfield 78	com/oneplus/gallery2/media/ExternalMediaSource:TAG	Ljava/lang/String;
    //   396: ldc_w 333
    //   399: aload_2
    //   400: invokestatic 278	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   403: aload_0
    //   404: aload_1
    //   405: invokevirtual 289	com/oneplus/gallery2/media/ExternalMediaSource:removeCachedMediaData	(Ljava/io/Serializable;)Z
    //   408: pop
    //   409: aconst_null
    //   410: areturn
    //   411: aload 6
    //   413: invokevirtual 334	java/io/OutputStream:close	()V
    //   416: goto -56 -> 360
    //   419: astore 7
    //   421: aconst_null
    //   422: astore_2
    //   423: aload_2
    //   424: ifnull +39 -> 463
    //   427: aload_2
    //   428: aload 7
    //   430: if_acmpne +39 -> 469
    //   433: aload 8
    //   435: ifnonnull +46 -> 481
    //   438: aload_2
    //   439: astore 6
    //   441: aload_2
    //   442: athrow
    //   443: astore_2
    //   444: goto -67 -> 377
    //   447: astore_2
    //   448: aload 6
    //   450: ifnonnull +5 -> 455
    //   453: aload_2
    //   454: athrow
    //   455: aload 6
    //   457: invokevirtual 334	java/io/OutputStream:close	()V
    //   460: goto -7 -> 453
    //   463: aload 7
    //   465: astore_2
    //   466: goto -33 -> 433
    //   469: aload_2
    //   470: astore 6
    //   472: aload_2
    //   473: aload 7
    //   475: invokevirtual 283	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   478: goto -45 -> 433
    //   481: aload_2
    //   482: astore 6
    //   484: aload 8
    //   486: invokevirtual 331	java/io/FileInputStream:close	()V
    //   489: goto -51 -> 438
    //   492: aload_2
    //   493: astore 6
    //   495: goto -107 -> 388
    //   498: aload 6
    //   500: aload_2
    //   501: invokevirtual 283	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   504: goto -116 -> 388
    //   507: aload 7
    //   509: areturn
    //   510: iconst_0
    //   511: istore 4
    //   513: goto -459 -> 54
    //   516: astore 7
    //   518: goto -95 -> 423
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	521	0	this	ExternalMediaSource
    //   0	521	1	paramSerializable	Serializable
    //   0	521	2	paramFile	File
    //   0	521	3	paramBoolean	boolean
    //   1	511	4	i	int
    //   33	3	5	bool	boolean
    //   7	492	6	localObject1	Object
    //   48	323	7	localObject2	Object
    //   419	89	7	localThrowable	Throwable
    //   516	1	7	localObject3	Object
    //   307	178	8	localFileInputStream	FileInputStream
    // Exception table:
    //   from	to	target	type
    //   40	50	230	java/lang/Throwable
    //   54	67	230	java/lang/Throwable
    //   200	221	230	java/lang/Throwable
    //   12	25	259	finally
    //   29	35	259	finally
    //   40	50	259	finally
    //   54	67	259	finally
    //   67	74	259	finally
    //   74	81	259	finally
    //   81	137	259	finally
    //   145	158	259	finally
    //   160	163	259	finally
    //   165	198	259	finally
    //   200	221	259	finally
    //   225	228	259	finally
    //   231	245	259	finally
    //   247	256	259	finally
    //   260	263	259	finally
    //   265	271	259	finally
    //   274	297	259	finally
    //   299	309	373	finally
    //   365	370	373	finally
    //   388	391	391	java/lang/Throwable
    //   498	504	391	java/lang/Throwable
    //   309	316	419	finally
    //   411	416	419	finally
    //   441	443	443	finally
    //   472	478	443	finally
    //   484	489	443	finally
    //   316	330	447	finally
    //   335	352	447	finally
    //   453	455	516	finally
    //   455	460	516	finally
  }
  
  protected CachedMediaData getCachedMediaData(Serializable paramSerializable)
  {
    synchronized (this.m_MediaDataCacheLock)
    {
      paramSerializable = (CachedMediaData)this.m_MediaDataCacheTable.get(paramSerializable);
      if (paramSerializable == null) {
        return paramSerializable;
      }
      touchCachedMediaData(paramSerializable);
    }
  }
  
  protected final WorkerThread getWorkerThread()
  {
    return this.m_WorkerThread;
  }
  
  protected boolean handleWorkerThreadMessage(Message paramMessage)
  {
    return false;
  }
  
  protected final boolean isWorkerThread()
  {
    return this.m_WorkerThread == Thread.currentThread();
  }
  
  /* Error */
  protected File moveFileFromMediaDataCache(Serializable paramSerializable)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 62	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheLock	Ljava/lang/Object;
    //   4: astore 5
    //   6: aload 5
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 67	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheTable	Ljava/util/Map;
    //   13: aload_1
    //   14: invokeinterface 146 2 0
    //   19: checkcast 8	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData
    //   22: astore_1
    //   23: aload_1
    //   24: ifnull +51 -> 75
    //   27: ldc_w 353
    //   30: aconst_null
    //   31: invokestatic 357	java/io/File:createTempFile	(Ljava/lang/String;Ljava/lang/String;)Ljava/io/File;
    //   34: astore 6
    //   36: aload_1
    //   37: invokevirtual 107	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:getSize	()J
    //   40: lstore_2
    //   41: aload_1
    //   42: aload 6
    //   44: invokevirtual 360	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:moveFileTo	(Ljava/io/File;)Z
    //   47: istore 4
    //   49: iload 4
    //   51: ifeq +46 -> 97
    //   54: aload_0
    //   55: aload_0
    //   56: getfield 93	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheSize	J
    //   59: lload_2
    //   60: lsub
    //   61: putfield 93	com/oneplus/gallery2/media/ExternalMediaSource:m_MediaDataCacheSize	J
    //   64: aload_0
    //   65: aload_1
    //   66: invokespecial 110	com/oneplus/gallery2/media/ExternalMediaSource:removeCachedMediaDataDirectly	(Lcom/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData;)V
    //   69: aload 5
    //   71: monitorexit
    //   72: aload 6
    //   74: areturn
    //   75: aload 5
    //   77: monitorexit
    //   78: aconst_null
    //   79: areturn
    //   80: astore_1
    //   81: aload_0
    //   82: getfield 78	com/oneplus/gallery2/media/ExternalMediaSource:TAG	Ljava/lang/String;
    //   85: ldc_w 362
    //   88: aload_1
    //   89: invokestatic 278	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   92: aload 5
    //   94: monitorexit
    //   95: aconst_null
    //   96: areturn
    //   97: aload_0
    //   98: getfield 78	com/oneplus/gallery2/media/ExternalMediaSource:TAG	Ljava/lang/String;
    //   101: ldc_w 364
    //   104: invokestatic 222	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   107: aload 5
    //   109: monitorexit
    //   110: aconst_null
    //   111: areturn
    //   112: astore_1
    //   113: aload_0
    //   114: getfield 78	com/oneplus/gallery2/media/ExternalMediaSource:TAG	Ljava/lang/String;
    //   117: ldc_w 364
    //   120: aload_1
    //   121: invokestatic 278	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   124: aload 5
    //   126: monitorexit
    //   127: aconst_null
    //   128: areturn
    //   129: astore_1
    //   130: aload 5
    //   132: monitorexit
    //   133: aload_1
    //   134: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	135	0	this	ExternalMediaSource
    //   0	135	1	paramSerializable	Serializable
    //   40	20	2	l	long
    //   47	3	4	bool	boolean
    //   4	127	5	localObject	Object
    //   34	39	6	localFile	File
    // Exception table:
    //   from	to	target	type
    //   27	36	80	java/lang/Throwable
    //   41	49	112	java/lang/Throwable
    //   97	107	112	java/lang/Throwable
    //   9	23	129	finally
    //   27	36	129	finally
    //   36	41	129	finally
    //   41	49	129	finally
    //   54	72	129	finally
    //   75	78	129	finally
    //   81	95	129	finally
    //   97	107	129	finally
    //   107	110	129	finally
    //   113	127	129	finally
    //   130	133	129	finally
  }
  
  protected WorkerThread onCreateWorkerThread()
  {
    return new WorkerThread("Worker thread (" + this + ")");
  }
  
  protected void onDeinitialize()
  {
    onWorkerThreadStopping(this.m_WorkerThread);
    this.m_WorkerThread.release();
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_WorkerThread = onCreateWorkerThread();
    this.m_WorkerThread.startSync();
    onWorkerThreadStarted(this.m_WorkerThread);
  }
  
  protected boolean onRemovingCachedMediaData(CachedMediaData paramCachedMediaData)
  {
    boolean bool = false;
    if (!paramCachedMediaData.isAccessing()) {
      bool = true;
    }
    return bool;
  }
  
  protected void onSetupMediaDataCache(File paramFile)
  {
    Object localObject5 = null;
    Object localObject1 = null;
    HashSet localHashSet;
    int i;
    Object localObject7;
    if (this.m_MediaDataCacheDirectory != null)
    {
      localHashSet = new HashSet();
      try
      {
        paramFile = this.m_MediaDataCacheDirectory.list();
        i = paramFile.length;
        for (;;)
        {
          int j = i - 1;
          if (j < 0) {
            break;
          }
          localObject7 = Path.getFileName(paramFile[j]);
          i = j;
          if (!"_STATE".equals(localObject7))
          {
            localHashSet.add(localObject7);
            i = j;
          }
        }
        Log.w(this.TAG, "onSetupMediaDataCache() - No state file");
      }
      catch (Throwable paramFile)
      {
        Log.e(this.TAG, "onSetupMediaDataCache() - Fail to list files in " + this.m_MediaDataCacheDirectory.getAbsolutePath(), paramFile);
        localObject7 = new File(this.m_MediaDataCacheDirectory, "_STATE");
        if (((File)localObject7).exists()) {
          break label149;
        }
      }
      if (!localHashSet.isEmpty()) {}
    }
    else
    {
      label147:
      return;
      label149:
      paramFile = (File)localObject5;
    }
    for (;;)
    {
      FileInputStream localFileInputStream;
      Object localObject8;
      try
      {
        localFileInputStream = new FileInputStream((File)localObject7);
      }
      finally
      {
        label268:
        label327:
        label344:
        label349:
        if (paramFile == null) {}
      }
      try
      {
        paramFile = new ObjectInputStream(localFileInputStream);
        try
        {
          i = paramFile.readInt();
          if (i <= 0) {
            break label450;
          }
          Serializable localSerializable = (Serializable)paramFile.readObject();
          localObject8 = paramFile.readUTF();
          if ((this.m_MediaDataCacheTable.get(localSerializable) != null) || (!localHashSet.remove(localObject8))) {
            break label627;
          }
          localObject8 = new File(this.m_MediaDataCacheDirectory, (String)localObject8);
          if (!((File)localObject8).exists()) {
            break label627;
          }
          localObject8 = new CachedMediaData(localSerializable, (File)localObject8);
          if (this.m_MediaDataCacheHead != null) {
            break label399;
          }
          if (this.m_MediaDataCacheTail == null) {
            break label411;
          }
          ((CachedMediaData)localObject8).nextData = this.m_MediaDataCacheHead;
          this.m_MediaDataCacheHead = ((CachedMediaData)localObject8);
          this.m_MediaDataCacheSize += ((CachedMediaData)localObject8).getSize();
          this.m_MediaDataCacheTable.put(localSerializable, localObject8);
        }
        finally
        {
          if (paramFile != null) {
            break label502;
          }
        }
        try
        {
          throw ((Throwable)localObject2);
        }
        finally {}
      }
      finally
      {
        Object localObject4;
        continue;
      }
      if (localObject2 != null)
      {
        if (localObject2 != localThrowable2) {
          break label516;
        }
        if (localFileInputStream != null) {
          break label529;
        }
        paramFile = (File)localObject2;
        throw ((Throwable)localObject2);
        if (paramFile != localObject3) {
          break label546;
        }
      }
      for (;;)
      {
        try
        {
          throw paramFile;
        }
        catch (EOFException paramFile)
        {
          Log.w(this.TAG, "onSetupMediaDataCache() - Inconsistent data in state file " + localObject7, paramFile);
          break;
          this.m_MediaDataCacheHead.prevData = ((CachedMediaData)localObject8);
          break label268;
          this.m_MediaDataCacheTail = ((CachedMediaData)localObject8);
        }
        catch (Throwable paramFile)
        {
          label399:
          label411:
          Log.e(this.TAG, "onSetupMediaDataCache() - Fail to read state file " + localObject7, paramFile);
        }
        break;
        label450:
        Log.v(this.TAG, "onSetupMediaDataCache() - ", Integer.valueOf(this.m_MediaDataCacheTable.size()), " valid data in cache");
        if (paramFile == null) {}
        for (;;)
        {
          if (localFileInputStream == null) {
            break label500;
          }
          paramFile = localThrowable2;
          localFileInputStream.close();
          break;
          paramFile.close();
        }
        label500:
        break;
        label502:
        paramFile.close();
        break label327;
        localObject4 = localThrowable2;
        break label344;
        label516:
        paramFile = (File)localObject4;
        ((Throwable)localObject4).addSuppressed(localThrowable2);
        break label344;
        label529:
        paramFile = (File)localObject4;
        localFileInputStream.close();
        break label349;
        paramFile = (File)localObject4;
        continue;
        label546:
        paramFile.addSuppressed((Throwable)localObject4);
      }
      Log.v(this.TAG, "onSetupMediaDataCache() - Delete ", Integer.valueOf(localHashSet.size()), " extra files");
      paramFile = localHashSet.iterator();
      while (paramFile.hasNext())
      {
        localObject4 = (String)paramFile.next();
        try
        {
          new File(this.m_MediaDataCacheDirectory, (String)localObject4).delete();
        }
        catch (Throwable localThrowable1) {}
      }
      break label147;
      label627:
      i -= 1;
    }
  }
  
  protected abstract File onSetupMediaDataCacheDirectory(BaseApplication paramBaseApplication);
  
  protected void onWorkerThreadStarted(WorkerThread paramWorkerThread)
  {
    HandlerUtils.post(this.m_WorkerThread, new Runnable()
    {
      public void run()
      {
        ExternalMediaSource.this.setupMediaDataCache();
      }
    });
  }
  
  protected void onWorkerThreadStopping(WorkerThread paramWorkerThread) {}
  
  protected InputStream openCachedMediaDataInputStream(Serializable paramSerializable)
    throws IOException, InterruptedException
  {
    synchronized (this.m_MediaDataCacheLock)
    {
      paramSerializable = getCachedMediaData(paramSerializable);
      if (paramSerializable == null) {
        throw new IOException("Cached data does not exist");
      }
    }
    paramSerializable = paramSerializable.openInputStream();
    return paramSerializable;
  }
  
  protected int removeCachedMediaData(Cache.RemovingPredication<Serializable> paramRemovingPredication)
  {
    for (;;)
    {
      int i;
      synchronized (this.m_MediaDataCacheLock)
      {
        SimpleRef localSimpleRef = new SimpleRef(Boolean.valueOf(false));
        Object localObject1 = this.m_MediaDataCacheHead;
        i = 0;
        j = i;
        if (localObject1 == null) {
          break label130;
        }
        CachedMediaData localCachedMediaData = ((CachedMediaData)localObject1).nextData;
        if (!paramRemovingPredication.canRemove(((CachedMediaData)localObject1).key, localSimpleRef))
        {
          if (!((Boolean)localSimpleRef.get()).booleanValue()) {
            localObject1 = localCachedMediaData;
          }
        }
        else
        {
          this.m_MediaDataCacheSize -= ((CachedMediaData)localObject1).getSize();
          removeCachedMediaDataDirectly((CachedMediaData)localObject1);
          i += 1;
          continue;
          return j;
          touchMediaDataCache();
        }
      }
      int j = i;
      label130:
      if (j > 0) {}
    }
  }
  
  protected boolean removeCachedMediaData(Serializable paramSerializable)
  {
    synchronized (this.m_MediaDataCacheLock)
    {
      paramSerializable = (CachedMediaData)this.m_MediaDataCacheTable.get(paramSerializable);
      if (paramSerializable == null) {
        return false;
      }
      this.m_MediaDataCacheSize -= paramSerializable.getSize();
      removeCachedMediaDataDirectly(paramSerializable);
      touchMediaDataCache();
      return true;
    }
  }
  
  protected void runInWorkerThreadAndWait(Runnable paramRunnable)
    throws InterruptedException, InvocationTargetException
  {
    if (!isWorkerThread())
    {
      if (this.m_WorkerThread == null) {
        throw new RuntimeException("No worker thread");
      }
    }
    else
    {
      paramRunnable.run();
      return;
    }
    paramRunnable = new SyncRunnable(paramRunnable);
    Handler localHandler = this.m_WorkerThread.getHandler();
    if (localHandler == null) {}
    while (!localHandler.postAtFrontOfQueue(paramRunnable)) {
      throw new RuntimeException("Fail to post to worker thread");
    }
    paramRunnable.waitForCompletion();
  }
  
  protected final class CachedMediaData
  {
    public final Serializable key;
    private final File m_File;
    private volatile boolean m_IsValid = true;
    private volatile int m_ReadingCounter;
    private volatile long m_Size;
    private volatile int m_WritingCounter;
    private volatile CachedMediaData nextData;
    private volatile CachedMediaData prevData;
    
    CachedMediaData(Serializable paramSerializable, File paramFile)
    {
      this.key = paramSerializable;
      this.m_File = paramFile;
      this.m_Size = paramFile.length();
    }
    
    private void beginRead()
      throws IOException, InterruptedException
    {
      try
      {
        while (this.m_WritingCounter > 0) {
          wait();
        }
        if (!this.m_IsValid) {
          break label49;
        }
      }
      finally {}
      this.m_ReadingCounter += 1;
      ExternalMediaSource.this.touchCachedMediaData(this);
      return;
      label49:
      throw new IOException("Invalid cached data");
    }
    
    private void beginWrite()
      throws IOException, InterruptedException
    {
      try
      {
        if (this.m_WritingCounter > 0) {}
        while (this.m_ReadingCounter > 0)
        {
          wait();
          break;
        }
      }
      finally {}
      if (this.m_IsValid)
      {
        this.m_WritingCounter += 1;
        ExternalMediaSource.this.touchCachedMediaData(this);
        return;
      }
      throw new IOException("Invalid cached data");
    }
    
    private void completeRead()
    {
      try
      {
        this.m_ReadingCounter -= 1;
        if (this.m_ReadingCounter > 0) {}
        while (this.m_IsValid)
        {
          ExternalMediaSource.this.touchCachedMediaData(this);
          return;
          notify();
        }
        if (!isAccessing()) {}
      }
      finally {}
      for (;;)
      {
        return;
        if ((this.m_File.exists()) && (!this.m_File.delete())) {
          Log.w(ExternalMediaSource.this.TAG, "Fail to delete cached file " + this.m_File);
        }
      }
    }
    
    private void completeWrite()
    {
      try
      {
        this.m_WritingCounter -= 1;
        long l2 = this.m_Size;
        long l1;
        if (this.m_WritingCounter > 0) {
          l1 = l2;
        }
        while (this.m_IsValid)
        {
          ExternalMediaSource.this.touchCachedMediaData(this);
          if (l2 != l1) {
            ExternalMediaSource.this.onCachedMediaDataSizeChanged(this, l2, l1);
          }
          return;
          notify();
          l1 = this.m_File.length();
          this.m_Size = l1;
        }
        if (!isAccessing()) {}
      }
      finally {}
      for (;;)
      {
        return;
        if ((this.m_File.exists()) && (!this.m_File.delete())) {
          Log.w(ExternalMediaSource.this.TAG, "Fail to delete cached file " + this.m_File);
        }
      }
    }
    
    long getSize()
    {
      return this.m_Size;
    }
    
    /* Error */
    void invalidate()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: iconst_0
      //   4: putfield 36	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:m_IsValid	Z
      //   7: aload_0
      //   8: invokevirtual 99	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:isAccessing	()Z
      //   11: ifne +18 -> 29
      //   14: aload_0
      //   15: getfield 40	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:m_File	Ljava/io/File;
      //   18: invokevirtual 102	java/io/File:exists	()Z
      //   21: istore_1
      //   22: iload_1
      //   23: ifne +9 -> 32
      //   26: aload_0
      //   27: monitorexit
      //   28: return
      //   29: aload_0
      //   30: monitorexit
      //   31: return
      //   32: aload_0
      //   33: getfield 40	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:m_File	Ljava/io/File;
      //   36: invokevirtual 105	java/io/File:delete	()Z
      //   39: ifne -13 -> 26
      //   42: aload_0
      //   43: getfield 31	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:this$0	Lcom/oneplus/gallery2/media/ExternalMediaSource;
      //   46: invokestatic 108	com/oneplus/gallery2/media/ExternalMediaSource:access$1	(Lcom/oneplus/gallery2/media/ExternalMediaSource;)Ljava/lang/String;
      //   49: new 110	java/lang/StringBuilder
      //   52: dup
      //   53: ldc 112
      //   55: invokespecial 113	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
      //   58: aload_0
      //   59: getfield 40	com/oneplus/gallery2/media/ExternalMediaSource$CachedMediaData:m_File	Ljava/io/File;
      //   62: invokevirtual 117	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   65: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   68: invokestatic 127	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
      //   71: goto -45 -> 26
      //   74: astore_2
      //   75: aload_0
      //   76: monitorexit
      //   77: aload_2
      //   78: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	79	0	this	CachedMediaData
      //   21	2	1	bool	boolean
      //   74	4	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	22	74	finally
      //   32	71	74	finally
    }
    
    public boolean isAccessing()
    {
      if (this.m_ReadingCounter > 0) {}
      while (this.m_WritingCounter > 0) {
        return true;
      }
      return false;
    }
    
    public boolean isValid()
    {
      return this.m_IsValid;
    }
    
    boolean moveFileTo(File paramFile)
      throws IOException, InterruptedException
    {
      beginRead();
      for (;;)
      {
        try
        {
          boolean bool = this.m_File.renameTo(paramFile);
          if (!bool) {
            return bool;
          }
        }
        finally
        {
          completeRead();
        }
        invalidate();
      }
    }
    
    public InputStream openInputStream()
      throws IOException, InterruptedException
    {
      beginRead();
      try
      {
        FileInputStream local1 = new FileInputStream(this.m_File)
        {
          public void close()
            throws IOException
          {
            super.close();
            ExternalMediaSource.CachedMediaData.this.completeRead();
          }
        };
        return local1;
      }
      catch (Throwable localThrowable)
      {
        completeRead();
        if (!(localThrowable instanceof IOException)) {
          throw new IOException("Fail to open stream for cached data", localThrowable);
        }
        throw ((IOException)localThrowable);
      }
    }
    
    public OutputStream openOutputStream()
      throws IOException, InterruptedException
    {
      beginWrite();
      try
      {
        FileOutputStream local2 = new FileOutputStream(this.m_File)
        {
          public void close()
            throws IOException
          {
            super.close();
            ExternalMediaSource.CachedMediaData.this.completeWrite();
          }
        };
        return local2;
      }
      catch (Throwable localThrowable)
      {
        completeWrite();
        if (!(localThrowable instanceof IOException)) {
          throw new IOException("Fail to open stream for cached data", localThrowable);
        }
        throw ((IOException)localThrowable);
      }
    }
  }
  
  private static final class SyncRunnable
    implements Runnable
  {
    private final Runnable m_Action;
    private volatile Throwable m_Error;
    private volatile boolean m_IsRun;
    
    public SyncRunnable(Runnable paramRunnable)
    {
      this.m_Action = paramRunnable;
    }
    
    public void run()
    {
      try
      {
        this.m_Action.run();
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          try
          {
            this.m_IsRun = true;
            notifyAll();
            return;
          }
          finally {}
          localThrowable = localThrowable;
          this.m_Error = localThrowable;
        }
      }
    }
    
    public void waitForCompletion()
      throws InterruptedException, InvocationTargetException
    {
      try
      {
        if (this.m_IsRun) {}
        for (;;)
        {
          Throwable localThrowable = this.m_Error;
          if (localThrowable != null) {
            break;
          }
          return;
          wait();
        }
        throw new InvocationTargetException(this.m_Error);
      }
      finally {}
    }
  }
  
  public class WorkerThread
    extends BaseThread
  {
    private final Object m_StartLock = new Object();
    
    public WorkerThread(String paramString)
    {
      super(null, null);
    }
    
    protected void handleMessage(Message paramMessage)
    {
      if (ExternalMediaSource.this.handleWorkerThreadMessage(paramMessage)) {
        return;
      }
      super.handleMessage(paramMessage);
    }
    
    protected void onStarted()
    {
      super.onStarted();
      synchronized (this.m_StartLock)
      {
        this.m_StartLock.notifyAll();
        return;
      }
    }
    
    final void startSync()
    {
      synchronized (this.m_StartLock)
      {
        start();
        try
        {
          this.m_StartLock.wait();
          return;
        }
        catch (Throwable localThrowable)
        {
          throw new RuntimeException(localThrowable);
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/ExternalMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */