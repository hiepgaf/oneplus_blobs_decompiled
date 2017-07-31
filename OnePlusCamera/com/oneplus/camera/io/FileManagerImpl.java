package com.oneplus.camera.io;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import android.text.TextUtils;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.component.ComponentState;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.CameraThreadComponent;
import com.oneplus.camera.media.MediaEventArgs;
import com.oneplus.camera.media.MediaListManager;
import com.oneplus.io.FileUtils;
import com.oneplus.io.Path;
import com.oneplus.media.ImageUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class FileManagerImpl
  extends CameraThreadComponent
  implements FileManager
{
  private static final int DB_INSERT_QUEUE_THRESHOLD = 8;
  private static final int DECODE_FACTOR = 2;
  private static final int KEEP_ALIVE_TIME = 1;
  private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
  private static final long MEDIA_SAVING_QUEUE_CAPACITY = 201326592L;
  private static final int MESSAGE_ADD_BITMAP = 1005;
  private static final int MESSAGE_DELETE_MEDIA = 1002;
  private static final int MESSAGE_GET_BITMAP = 1004;
  private static final int MESSAGE_INSERT_MEDIA = 1001;
  private static final int MESSAGE_LOAD_IMAGES = 1003;
  private static final int MESSAGE_SAVE_MEDIA = 1000;
  private static final int MSG_UPDATE_MEDIA_SAVING_QUEUE_SIZE = 10010;
  private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
  private boolean insertToggle = true;
  private Handler m_DecodeBitmapHandler;
  private DecodeBitmapThread m_DecodeBitmapThread = null;
  private LinkedBlockingDeque<BitmapArgs> m_DecodeQueue = new LinkedBlockingDeque();
  private ThreadPoolExecutor m_DecodeThreadPool;
  private BlockingQueue<Runnable> m_DecodeWorkQueue;
  private final File m_DefaultFolder = null;
  private Handler m_FileHandler;
  private final List<File> m_FileList = new ArrayList();
  private FileManageerThread m_FileThread = null;
  private ConcurrentMap<String, Uri> m_FileUris = new ConcurrentHashMap();
  private ConcurrentLinkedQueue<MediaSaveTask> m_InsertQueue = new ConcurrentLinkedQueue();
  private long m_MediaSavingQueueSize;
  private ConcurrentLinkedQueue<MediaSaveTask> m_SaveQueue = new ConcurrentLinkedQueue();
  
  FileManagerImpl(CameraThread paramCameraThread)
  {
    super("File manager", paramCameraThread, true);
    enableEventLogs(EVENT_MEDIA_FILES_RESET, 256);
  }
  
  public static int calculateInSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
  {
    int k = paramOptions.outHeight;
    int m = paramOptions.outWidth;
    int j = 1;
    int i = 1;
    if ((k > paramInt2) || (m > paramInt1))
    {
      k /= 2;
      m /= 2;
      for (;;)
      {
        if (k / i <= paramInt2)
        {
          j = i;
          if (m / i <= paramInt1) {
            break;
          }
        }
        i *= 2;
      }
    }
    return j;
  }
  
  /* Error */
  private List<File> getFiles()
  {
    // Byte code:
    //   0: new 199	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 201	java/util/ArrayList:<init>	()V
    //   7: astore 6
    //   9: getstatic 236	android/provider/MediaStore$Images$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   12: astore 10
    //   14: getstatic 239	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   17: astore 7
    //   19: iconst_2
    //   20: anewarray 241	java/lang/String
    //   23: astore 8
    //   25: aload 8
    //   27: iconst_0
    //   28: ldc -13
    //   30: aastore
    //   31: aload 8
    //   33: iconst_1
    //   34: ldc -11
    //   36: aastore
    //   37: iconst_1
    //   38: anewarray 241	java/lang/String
    //   41: astore 9
    //   43: aload 9
    //   45: iconst_0
    //   46: new 247	java/lang/StringBuilder
    //   49: dup
    //   50: invokespecial 248	java/lang/StringBuilder:<init>	()V
    //   53: ldc -6
    //   55: invokevirtual 254	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   58: aload_0
    //   59: getfield 102	com/oneplus/camera/io/FileManagerImpl:m_DefaultFolder	Ljava/io/File;
    //   62: invokevirtual 260	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   65: invokevirtual 254	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   68: ldc -6
    //   70: invokevirtual 254	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   73: invokevirtual 263	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   76: aastore
    //   77: aconst_null
    //   78: astore 4
    //   80: aconst_null
    //   81: astore_3
    //   82: aload_0
    //   83: invokevirtual 267	com/oneplus/camera/io/FileManagerImpl:getContext	()Landroid/content/Context;
    //   86: invokevirtual 273	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   89: aload 10
    //   91: aload 8
    //   93: ldc_w 275
    //   96: aload 9
    //   98: aconst_null
    //   99: invokevirtual 281	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   102: astore 5
    //   104: aload 5
    //   106: astore_3
    //   107: aload 5
    //   109: astore 4
    //   111: aload 5
    //   113: ldc -11
    //   115: invokeinterface 287 2 0
    //   120: istore_1
    //   121: aload 5
    //   123: astore_3
    //   124: aload 5
    //   126: astore 4
    //   128: aload 5
    //   130: ldc -13
    //   132: invokeinterface 287 2 0
    //   137: istore_2
    //   138: aload 5
    //   140: ifnull +526 -> 666
    //   143: aload 5
    //   145: astore_3
    //   146: aload 5
    //   148: astore 4
    //   150: aload 5
    //   152: invokeinterface 291 1 0
    //   157: ifeq +509 -> 666
    //   160: aload 5
    //   162: astore_3
    //   163: aload 5
    //   165: astore 4
    //   167: aload 5
    //   169: iload_1
    //   170: invokeinterface 295 2 0
    //   175: astore 11
    //   177: aload 5
    //   179: astore_3
    //   180: aload 5
    //   182: astore 4
    //   184: aload_0
    //   185: getfield 117	com/oneplus/camera/io/FileManagerImpl:m_FileUris	Ljava/util/concurrent/ConcurrentMap;
    //   188: aload 11
    //   190: aload 10
    //   192: new 247	java/lang/StringBuilder
    //   195: dup
    //   196: invokespecial 248	java/lang/StringBuilder:<init>	()V
    //   199: ldc_w 297
    //   202: invokevirtual 254	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   205: aload 5
    //   207: iload_2
    //   208: invokeinterface 301 2 0
    //   213: invokevirtual 304	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   216: invokevirtual 263	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   219: invokestatic 310	android/net/Uri:withAppendedPath	(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   222: invokeinterface 316 3 0
    //   227: pop
    //   228: aload 5
    //   230: astore_3
    //   231: aload 5
    //   233: astore 4
    //   235: new 256	java/io/File
    //   238: dup
    //   239: aload 11
    //   241: invokespecial 319	java/io/File:<init>	(Ljava/lang/String;)V
    //   244: astore 12
    //   246: aload 5
    //   248: astore_3
    //   249: aload 5
    //   251: astore 4
    //   253: aload 12
    //   255: invokevirtual 322	java/io/File:exists	()Z
    //   258: ifeq +355 -> 613
    //   261: aload 5
    //   263: astore_3
    //   264: aload 5
    //   266: astore 4
    //   268: aload 12
    //   270: invokevirtual 326	java/io/File:length	()J
    //   273: lconst_0
    //   274: lcmp
    //   275: ifeq +338 -> 613
    //   278: aload 5
    //   280: astore_3
    //   281: aload 5
    //   283: astore 4
    //   285: aload 6
    //   287: aload 12
    //   289: invokeinterface 332 2 0
    //   294: pop
    //   295: goto -152 -> 143
    //   298: astore 5
    //   300: aload_3
    //   301: astore 4
    //   303: aload_0
    //   304: getfield 336	com/oneplus/camera/io/FileManagerImpl:TAG	Ljava/lang/String;
    //   307: aload 5
    //   309: invokevirtual 337	java/lang/Exception:toString	()Ljava/lang/String;
    //   312: invokestatic 343	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   315: aload_3
    //   316: astore 4
    //   318: aload_3
    //   319: ifnull +12 -> 331
    //   322: aload_3
    //   323: invokeinterface 346 1 0
    //   328: aload_3
    //   329: astore 4
    //   331: aload 4
    //   333: astore_3
    //   334: aload_0
    //   335: invokevirtual 267	com/oneplus/camera/io/FileManagerImpl:getContext	()Landroid/content/Context;
    //   338: invokevirtual 273	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   341: aload 7
    //   343: aload 8
    //   345: ldc_w 275
    //   348: aload 9
    //   350: aconst_null
    //   351: invokevirtual 281	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   354: astore 5
    //   356: aload 5
    //   358: astore_3
    //   359: aload 5
    //   361: astore 4
    //   363: aload 5
    //   365: ldc -11
    //   367: invokeinterface 287 2 0
    //   372: istore_1
    //   373: aload 5
    //   375: astore_3
    //   376: aload 5
    //   378: astore 4
    //   380: aload 5
    //   382: ldc -13
    //   384: invokeinterface 287 2 0
    //   389: istore_2
    //   390: aload 5
    //   392: ifnull +350 -> 742
    //   395: aload 5
    //   397: astore_3
    //   398: aload 5
    //   400: astore 4
    //   402: aload 5
    //   404: invokeinterface 291 1 0
    //   409: ifeq +333 -> 742
    //   412: aload 5
    //   414: astore_3
    //   415: aload 5
    //   417: astore 4
    //   419: aload 5
    //   421: iload_1
    //   422: invokeinterface 295 2 0
    //   427: astore 8
    //   429: aload 5
    //   431: astore_3
    //   432: aload 5
    //   434: astore 4
    //   436: aload_0
    //   437: getfield 117	com/oneplus/camera/io/FileManagerImpl:m_FileUris	Ljava/util/concurrent/ConcurrentMap;
    //   440: aload 8
    //   442: aload 7
    //   444: new 247	java/lang/StringBuilder
    //   447: dup
    //   448: invokespecial 248	java/lang/StringBuilder:<init>	()V
    //   451: ldc_w 297
    //   454: invokevirtual 254	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   457: aload 5
    //   459: iload_2
    //   460: invokeinterface 301 2 0
    //   465: invokevirtual 304	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   468: invokevirtual 263	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   471: invokestatic 310	android/net/Uri:withAppendedPath	(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   474: invokeinterface 316 3 0
    //   479: pop
    //   480: aload 5
    //   482: astore_3
    //   483: aload 5
    //   485: astore 4
    //   487: new 256	java/io/File
    //   490: dup
    //   491: aload 8
    //   493: invokespecial 319	java/io/File:<init>	(Ljava/lang/String;)V
    //   496: astore 9
    //   498: aload 5
    //   500: astore_3
    //   501: aload 5
    //   503: astore 4
    //   505: aload 9
    //   507: invokevirtual 322	java/io/File:exists	()Z
    //   510: ifeq +179 -> 689
    //   513: aload 5
    //   515: astore_3
    //   516: aload 5
    //   518: astore 4
    //   520: aload 9
    //   522: invokevirtual 326	java/io/File:length	()J
    //   525: lconst_0
    //   526: lcmp
    //   527: ifeq +162 -> 689
    //   530: aload 5
    //   532: astore_3
    //   533: aload 5
    //   535: astore 4
    //   537: aload 6
    //   539: aload 9
    //   541: invokeinterface 332 2 0
    //   546: pop
    //   547: goto -152 -> 395
    //   550: astore 5
    //   552: aload_3
    //   553: astore 4
    //   555: aload_0
    //   556: getfield 336	com/oneplus/camera/io/FileManagerImpl:TAG	Ljava/lang/String;
    //   559: aload 5
    //   561: invokevirtual 337	java/lang/Exception:toString	()Ljava/lang/String;
    //   564: invokestatic 343	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   567: aload_3
    //   568: ifnull +9 -> 577
    //   571: aload_3
    //   572: invokeinterface 346 1 0
    //   577: aload 6
    //   579: new 8	com/oneplus/camera/io/FileManagerImpl$1
    //   582: dup
    //   583: aload_0
    //   584: invokespecial 349	com/oneplus/camera/io/FileManagerImpl$1:<init>	(Lcom/oneplus/camera/io/FileManagerImpl;)V
    //   587: invokestatic 355	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
    //   590: aload_0
    //   591: getfield 336	com/oneplus/camera/io/FileManagerImpl:TAG	Ljava/lang/String;
    //   594: ldc_w 357
    //   597: aload 6
    //   599: invokeinterface 360 1 0
    //   604: invokestatic 366	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   607: invokestatic 370	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   610: aload 6
    //   612: areturn
    //   613: aload 5
    //   615: astore_3
    //   616: aload 5
    //   618: astore 4
    //   620: aload_0
    //   621: getfield 336	com/oneplus/camera/io/FileManagerImpl:TAG	Ljava/lang/String;
    //   624: new 247	java/lang/StringBuilder
    //   627: dup
    //   628: invokespecial 248	java/lang/StringBuilder:<init>	()V
    //   631: ldc_w 372
    //   634: invokevirtual 254	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   637: aload 11
    //   639: invokevirtual 254	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   642: invokevirtual 263	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   645: invokestatic 343	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   648: goto -505 -> 143
    //   651: astore_3
    //   652: aload 4
    //   654: ifnull +10 -> 664
    //   657: aload 4
    //   659: invokeinterface 346 1 0
    //   664: aload_3
    //   665: athrow
    //   666: aload 5
    //   668: astore 4
    //   670: aload 5
    //   672: ifnull -341 -> 331
    //   675: aload 5
    //   677: invokeinterface 346 1 0
    //   682: aload 5
    //   684: astore 4
    //   686: goto -355 -> 331
    //   689: aload 5
    //   691: astore_3
    //   692: aload 5
    //   694: astore 4
    //   696: aload_0
    //   697: getfield 336	com/oneplus/camera/io/FileManagerImpl:TAG	Ljava/lang/String;
    //   700: new 247	java/lang/StringBuilder
    //   703: dup
    //   704: invokespecial 248	java/lang/StringBuilder:<init>	()V
    //   707: ldc_w 374
    //   710: invokevirtual 254	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   713: aload 8
    //   715: invokevirtual 254	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   718: invokevirtual 263	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   721: invokestatic 343	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   724: goto -329 -> 395
    //   727: astore_3
    //   728: aload 4
    //   730: ifnull +10 -> 740
    //   733: aload 4
    //   735: invokeinterface 346 1 0
    //   740: aload_3
    //   741: athrow
    //   742: aload 5
    //   744: ifnull -167 -> 577
    //   747: aload 5
    //   749: invokeinterface 346 1 0
    //   754: goto -177 -> 577
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	757	0	this	FileManagerImpl
    //   120	302	1	i	int
    //   137	323	2	j	int
    //   81	535	3	localObject1	Object
    //   651	14	3	localObject2	Object
    //   691	1	3	localObject3	Object
    //   727	14	3	localObject4	Object
    //   78	656	4	localObject5	Object
    //   102	180	5	localCursor1	android.database.Cursor
    //   298	10	5	localException1	Exception
    //   354	180	5	localCursor2	android.database.Cursor
    //   550	198	5	localException2	Exception
    //   7	604	6	localArrayList	ArrayList
    //   17	426	7	localUri1	Uri
    //   23	691	8	localObject6	Object
    //   41	499	9	localObject7	Object
    //   12	179	10	localUri2	Uri
    //   175	463	11	str	String
    //   244	44	12	localFile	File
    // Exception table:
    //   from	to	target	type
    //   82	104	298	java/lang/Exception
    //   111	121	298	java/lang/Exception
    //   128	138	298	java/lang/Exception
    //   150	160	298	java/lang/Exception
    //   167	177	298	java/lang/Exception
    //   184	228	298	java/lang/Exception
    //   235	246	298	java/lang/Exception
    //   253	261	298	java/lang/Exception
    //   268	278	298	java/lang/Exception
    //   285	295	298	java/lang/Exception
    //   620	648	298	java/lang/Exception
    //   334	356	550	java/lang/Exception
    //   363	373	550	java/lang/Exception
    //   380	390	550	java/lang/Exception
    //   402	412	550	java/lang/Exception
    //   419	429	550	java/lang/Exception
    //   436	480	550	java/lang/Exception
    //   487	498	550	java/lang/Exception
    //   505	513	550	java/lang/Exception
    //   520	530	550	java/lang/Exception
    //   537	547	550	java/lang/Exception
    //   696	724	550	java/lang/Exception
    //   82	104	651	finally
    //   111	121	651	finally
    //   128	138	651	finally
    //   150	160	651	finally
    //   167	177	651	finally
    //   184	228	651	finally
    //   235	246	651	finally
    //   253	261	651	finally
    //   268	278	651	finally
    //   285	295	651	finally
    //   303	315	651	finally
    //   620	648	651	finally
    //   334	356	727	finally
    //   363	373	727	finally
    //   380	390	727	finally
    //   402	412	727	finally
    //   419	429	727	finally
    //   436	480	727	finally
    //   487	498	727	finally
    //   505	513	727	finally
    //   520	530	727	finally
    //   537	547	727	finally
    //   555	567	727	finally
    //   696	724	727	finally
  }
  
  private boolean isImage(String paramString)
  {
    return FileUtils.isImageFilePath(paramString);
  }
  
  private boolean isVideo(String paramString)
  {
    return FileUtils.isVideoFilePath(paramString);
  }
  
  private boolean notifyCameraThread(final EventKey<EventArgs> paramEventKey, final EventArgs paramEventArgs)
  {
    HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        FileManagerImpl.-wrap6(FileManagerImpl.this, paramEventKey, paramEventArgs);
      }
    });
  }
  
  private boolean notifyCameraThread(final EventKey<MediaEventArgs> paramEventKey, MediaSaveTask paramMediaSaveTask, boolean paramBoolean)
  {
    HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        FileManagerImpl.-wrap6(FileManagerImpl.this, paramEventKey, this.val$args);
      }
    });
  }
  
  private boolean notifyCameraThread(final EventKey<MediaEventArgs> paramEventKey, final String paramString)
  {
    HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        FileManagerImpl.-wrap6(FileManagerImpl.this, paramEventKey, new MediaEventArgs(null, null, 0, paramString, null, null));
      }
    });
  }
  
  private void notifyDecode()
  {
    if ((this.m_SaveQueue.size() != 0) || (this.m_InsertQueue.size() != 0) || (this.m_DecodeBitmapHandler.hasMessages(1004))) {
      return;
    }
    this.m_DecodeBitmapHandler.sendMessage(Message.obtain(this.m_DecodeBitmapHandler, 1004));
  }
  
  private void updateMediaSavingQueueSize(long paramLong)
  {
    if (paramLong == 0L) {
      return;
    }
    if (!isDependencyThread())
    {
      HandlerUtils.sendMessage(this, 10010, Long.valueOf(paramLong));
      return;
    }
    long l = this.m_MediaSavingQueueSize;
    this.m_MediaSavingQueueSize += paramLong;
    Log.v(this.TAG, "updateMediaSavingQueueSize() - ", FileUtils.getFileSizeDescription(l), " -> ", FileUtils.getFileSizeDescription(this.m_MediaSavingQueueSize));
    notifyPropertyChanged(PROP_SAVING_QUEUE_SIZE, Long.valueOf(l), Long.valueOf(this.m_MediaSavingQueueSize));
    PropertyKey localPropertyKey = PROP_IS_SAVING_QUEUE_FULL;
    if (this.m_MediaSavingQueueSize >= 201326592L) {}
    for (boolean bool = true;; bool = false)
    {
      setReadOnly(localPropertyKey, Boolean.valueOf(bool));
      return;
    }
  }
  
  public Bitmap decodeBitmap(String paramString, int paramInt1, int paramInt2)
  {
    ScreenSize localScreenSize = getScreenSize();
    paramInt1 = localScreenSize.getWidth();
    paramInt2 = localScreenSize.getHeight();
    if ((paramInt1 == 0) || (paramInt2 == 0))
    {
      Log.e(this.TAG, "decodeBitmap() - BitmapFactory.decodeFile failed, screenWidth or screenHeight is 0");
      return null;
    }
    if (paramInt1 >= paramInt2) {}
    for (;;)
    {
      paramString = ImageUtils.decodeBitmap(paramString, paramInt1, paramInt1, Bitmap.Config.RGB_565);
      if (paramString == null) {
        Log.e(this.TAG, "decodeBitmap() - BitmapFactory.decodeFile failed");
      }
      return paramString;
      paramInt1 = paramInt2;
    }
  }
  
  public void deleteFile(String paramString)
  {
    if ((!TextUtils.isEmpty(paramString)) && (isRunningOrInitializing())) {
      this.m_FileHandler.sendMessage(Message.obtain(this.m_FileHandler, 1002, paramString));
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_SAVING_QUEUE_SIZE) {
      return Long.valueOf(this.m_MediaSavingQueueSize);
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  public void getBitmap(String paramString, int paramInt1, int paramInt2, FileManager.PhotoCallback paramPhotoCallback, int paramInt3)
  {
    Log.d(this.TAG, " InsertQueue size " + this.m_InsertQueue.size() + " position " + paramInt3);
    if (paramInt3 == 1)
    {
      Log.d(this.TAG, "getBitmap: clear & get");
      this.m_DecodeQueue.clear();
      this.m_DecodeQueue.addFirst(new BitmapArgs(paramInt3, paramString, paramInt1 / 2, paramInt2 / 2, paramPhotoCallback));
      notifyDecode();
      return;
    }
    if ((paramInt3 == DecodeBitmapThread.-get0(this.m_DecodeBitmapThread)) || (this.m_InsertQueue.size() > 0))
    {
      Log.d(this.TAG, "getBitmap: now");
      this.m_DecodeQueue.addFirst(new BitmapArgs(paramInt3, paramString, paramInt1 / 2, paramInt2 / 2, paramPhotoCallback));
      notifyDecode();
      return;
    }
    Log.d(this.TAG, "getBitmap: later");
    this.m_DecodeQueue.add(new BitmapArgs(paramInt3, paramString, paramInt1 / 2, paramInt2 / 2, paramPhotoCallback));
    notifyDecode();
  }
  
  public Uri getFileUri(String paramString)
  {
    return (Uri)this.m_FileUris.get(paramString);
  }
  
  public List<File> getMediaFiles()
  {
    return new ArrayList(this.m_FileList);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    updateMediaSavingQueueSize(((Long)paramMessage.obj).longValue());
  }
  
  public boolean isBusy()
  {
    if (this.m_FileHandler == null) {
      return false;
    }
    if (this.m_DecodeBitmapHandler == null) {
      return false;
    }
    if (!this.m_FileHandler.hasMessages(1000)) {
      return this.m_DecodeBitmapHandler.hasMessages(1004);
    }
    return true;
  }
  
  public boolean isFileSaving()
  {
    if (this.m_FileHandler == null) {
      return false;
    }
    return this.m_FileHandler.hasMessages(1000);
  }
  
  protected void onDeinitialize()
  {
    super.onDeinitialize();
    if ((this.m_FileThread.isHandling()) || (this.m_FileThread.hasMessage())) {}
    for (;;)
    {
      this.m_DecodeBitmapThread.quitSafely();
      this.m_FileList.clear();
      this.m_FileUris.clear();
      this.m_DecodeQueue.clear();
      return;
      this.m_InsertQueue.clear();
      this.m_SaveQueue.clear();
      this.m_FileThread.quitSafely();
    }
  }
  
  protected void onInitialize()
  {
    this.m_FileThread = new FileManageerThread("save media thread");
    this.m_FileThread.start();
    this.m_FileHandler = this.m_FileThread.getHandler();
    this.m_DecodeBitmapThread = new DecodeBitmapThread("decode bitmap thread");
    this.m_DecodeBitmapThread.start();
    this.m_DecodeBitmapHandler = this.m_DecodeBitmapThread.getHandler();
    scanFiles();
    this.m_DecodeWorkQueue = new LinkedBlockingQueue();
    this.m_DecodeThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, 1L, KEEP_ALIVE_TIME_UNIT, this.m_DecodeWorkQueue);
  }
  
  public void pauseInsert()
  {
    this.insertToggle = false;
  }
  
  public void resumeInsert()
  {
    this.insertToggle = true;
    Log.d(this.TAG, "  resumeInsert   " + this.m_DecodeQueue.size());
    int j = this.m_DecodeQueue.size();
    int i = 0;
    while (i < j - 2 - 1)
    {
      this.m_DecodeQueue.pollLast();
      i += 1;
    }
    if (this.m_InsertQueue.size() > 0) {
      this.m_FileHandler.sendMessage(Message.obtain(this.m_FileHandler, 1001));
    }
  }
  
  public Handle saveMedia(MediaSaveTask paramMediaSaveTask, int paramInt)
  {
    verifyAccess();
    if ((paramMediaSaveTask != null) && (isRunningOrInitializing()))
    {
      updateMediaSavingQueueSize(paramMediaSaveTask.getMediaSize());
      this.m_SaveQueue.add(paramMediaSaveTask);
      this.m_FileHandler.sendMessage(Message.obtain(this.m_FileHandler, 1000));
    }
    return null;
  }
  
  public void scanFiles() {}
  
  public void setCurrent(int paramInt)
  {
    DecodeBitmapThread.-set0(this.m_DecodeBitmapThread, paramInt);
  }
  
  private class BitmapArgs
  {
    private int mHeight;
    private String m_Path;
    private int m_Position;
    private int m_Width;
    private FileManager.PhotoCallback m_callback;
    
    BitmapArgs(int paramInt1, String paramString, int paramInt2, int paramInt3, FileManager.PhotoCallback paramPhotoCallback)
    {
      this.m_Position = paramInt1;
      this.m_Path = paramString;
      this.m_Width = paramInt2;
      this.mHeight = paramInt3;
      this.m_callback = paramPhotoCallback;
    }
  }
  
  class DecodeBitmapThread
    extends HandlerThread
  {
    private static final int OFFSET = 2;
    final String TAG = DecodeBitmapThread.class.getSimpleName();
    private int m_Current;
    private Handler m_Handler;
    
    public DecodeBitmapThread(String paramString)
    {
      super();
    }
    
    private boolean checkInterrupt(int paramInt)
    {
      Log.d(this.TAG, "InsertQueue size " + FileManagerImpl.-get8(FileManagerImpl.this).size() + "  position:  " + paramInt);
      return (paramInt > this.m_Current + 2) || (paramInt < Math.max(1, this.m_Current - 2));
    }
    
    public Handler getHandler()
    {
      return this.m_Handler;
    }
    
    public void start()
    {
      super.start();
      this.m_Handler = new Handler(getLooper())
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          switch (paramAnonymousMessage.what)
          {
          }
          for (;;)
          {
            return;
            paramAnonymousMessage = (MediaSaveTask)paramAnonymousMessage.obj;
            FileManagerImpl.-wrap3(FileManagerImpl.this, FileManagerImpl.EVENT_MEDIA_FILE_ADDED, paramAnonymousMessage, true);
            return;
            paramAnonymousMessage = (FileManagerImpl.BitmapArgs)FileManagerImpl.-get2(FileManagerImpl.this).poll();
            if (paramAnonymousMessage == null) {
              Log.d(FileManagerImpl.DecodeBitmapThread.this.TAG, "MESSAGE_GET_BITMAP args == null");
            }
            while (FileManagerImpl.-get2(FileManagerImpl.this).size() > 0)
            {
              FileManagerImpl.-get1(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get1(FileManagerImpl.this), 1004));
              return;
              String str = FileManagerImpl.BitmapArgs.-get1(paramAnonymousMessage);
              FileManager.PhotoCallback localPhotoCallback = FileManagerImpl.BitmapArgs.-get4(paramAnonymousMessage);
              int i = FileManagerImpl.BitmapArgs.-get2(paramAnonymousMessage);
              int j = FileManagerImpl.BitmapArgs.-get3(paramAnonymousMessage);
              int k = FileManagerImpl.BitmapArgs.-get0(paramAnonymousMessage);
              boolean bool2 = FileManagerImpl.-wrap0(FileManagerImpl.this, str);
              if (bool2) {}
              for (boolean bool1 = false; FileManagerImpl.DecodeBitmapThread.-wrap0(FileManagerImpl.DecodeBitmapThread.this, i); bool1 = true)
              {
                Log.d(FileManagerImpl.DecodeBitmapThread.this.TAG, "checkInterrupt before decode : position: " + i + " m_Current: " + FileManagerImpl.DecodeBitmapThread.-get0(FileManagerImpl.DecodeBitmapThread.this));
                localPhotoCallback.onBitmapLoad(null, bool1, true);
                if (FileManagerImpl.-get2(FileManagerImpl.this).size() > 0) {
                  FileManagerImpl.-get1(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get1(FileManagerImpl.this), 1004));
                }
                return;
              }
              if (bool2) {}
              for (paramAnonymousMessage = FileManagerImpl.this.decodeBitmap(str, j, k); FileManagerImpl.DecodeBitmapThread.-wrap0(FileManagerImpl.DecodeBitmapThread.this, i); paramAnonymousMessage = ThumbnailUtils.createVideoThumbnail(str, 2))
              {
                Log.d(FileManagerImpl.DecodeBitmapThread.this.TAG, "checkInterrupt after decode : position: " + i + " m_Current: " + FileManagerImpl.DecodeBitmapThread.-get0(FileManagerImpl.DecodeBitmapThread.this));
                localPhotoCallback.onBitmapLoad(null, bool1, true);
                if (FileManagerImpl.-get2(FileManagerImpl.this).size() > 0) {
                  FileManagerImpl.-get1(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get1(FileManagerImpl.this), 1004));
                }
                return;
              }
              Log.d(FileManagerImpl.DecodeBitmapThread.this.TAG, ": " + i + " bitmap: " + paramAnonymousMessage + " path: " + str);
              localPhotoCallback.onBitmapLoad(paramAnonymousMessage, bool1, false);
            }
          }
        }
      };
    }
  }
  
  class FileManageerThread
    extends HandlerThread
  {
    private final String TAG = "FileManagerThread";
    private Handler m_Handler;
    private boolean m_Handling;
    
    public FileManageerThread(String paramString)
    {
      super();
    }
    
    public Handler getHandler()
    {
      return this.m_Handler;
    }
    
    public boolean hasMessage()
    {
      if ((!FileManagerImpl.-get4(FileManagerImpl.this).hasMessages(1000)) && (!FileManagerImpl.-get4(FileManagerImpl.this).hasMessages(1001)) && (!FileManagerImpl.-get4(FileManagerImpl.this).hasMessages(1002))) {
        return FileManagerImpl.-get4(FileManagerImpl.this).hasMessages(1003);
      }
      return true;
    }
    
    public boolean isHandling()
    {
      return this.m_Handling;
    }
    
    public void start()
    {
      super.start();
      this.m_Handler = new Handler(getLooper())
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          FileManagerImpl.FileManageerThread.-set0(FileManagerImpl.FileManageerThread.this, true);
          switch (paramAnonymousMessage.what)
          {
          }
          for (;;)
          {
            if ((!FileManagerImpl.FileManageerThread.this.hasMessage()) && ((FileManagerImpl.this.get(FileManagerImpl.PROP_STATE) == ComponentState.RELEASING) || (FileManagerImpl.this.get(FileManagerImpl.PROP_STATE) == ComponentState.RELEASED))) {
              FileManagerImpl.-get6(FileManagerImpl.this).quitSafely();
            }
            FileManagerImpl.FileManageerThread.-set0(FileManagerImpl.FileManageerThread.this, false);
            return;
            Log.d("FileManagerThread", "MESSAGE_SAVE_MEDIA  " + FileManagerImpl.-get9(FileManagerImpl.this).size());
            if ((FileManagerImpl.-get0(FileManagerImpl.this)) && (FileManagerImpl.-get8(FileManagerImpl.this).size() >= 8))
            {
              Log.v("FileManagerThread", "Too many pending DB insertion, insert to DB first");
              FileManagerImpl.-get4(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get4(FileManagerImpl.this), 1001));
              FileManagerImpl.-get4(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get4(FileManagerImpl.this), 1000));
            }
            else
            {
              paramAnonymousMessage = (MediaSaveTask)FileManagerImpl.-get9(FileManagerImpl.this).poll();
              if (paramAnonymousMessage == null)
              {
                Log.d("FileManagerThread", "no file save task");
                if (FileManagerImpl.-get8(FileManagerImpl.this).size() > 0) {
                  FileManagerImpl.-get4(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get4(FileManagerImpl.this), 1001));
                }
              }
              else
              {
                if (paramAnonymousMessage.saveMediaToFile())
                {
                  Log.d("FileManagerThread", "MESSAGE_SAVE_MEDIA  success " + FileManagerImpl.-get9(FileManagerImpl.this).size());
                  FileManagerImpl.-wrap3(FileManagerImpl.this, FileManagerImpl.EVENT_MEDIA_FILE_SAVED, paramAnonymousMessage, false);
                  FileManagerImpl.-get8(FileManagerImpl.this).add(paramAnonymousMessage);
                  FileManagerImpl.-get4(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get4(FileManagerImpl.this), 1001));
                }
                while (FileManagerImpl.-get9(FileManagerImpl.this).isEmpty())
                {
                  FileManagerImpl.this.getCameraThread().checkRemainingMediaCount();
                  break;
                  FileManagerImpl.-wrap7(FileManagerImpl.this, -paramAnonymousMessage.getMediaSize());
                  if (FileManagerImpl.-get8(FileManagerImpl.this).size() > 0) {
                    FileManagerImpl.-get4(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get4(FileManagerImpl.this), 1001));
                  }
                  FileManagerImpl.-wrap3(FileManagerImpl.this, FileManagerImpl.EVENT_MEDIA_SAVE_FAILED, paramAnonymousMessage, false);
                }
                Log.d("FileManagerThread", "MESSAGE_INSERT_MEDIA  " + FileManagerImpl.-get0(FileManagerImpl.this) + "  " + FileManagerImpl.-get8(FileManagerImpl.this).size());
                if (FileManagerImpl.-get0(FileManagerImpl.this)) {
                  if ((FileManagerImpl.-get9(FileManagerImpl.this).size() > 0) && (FileManagerImpl.-get8(FileManagerImpl.this).size() < 8))
                  {
                    Log.v("FileManagerThread", "Save file first");
                    FileManagerImpl.-get4(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get4(FileManagerImpl.this), 1000));
                  }
                  else
                  {
                    paramAnonymousMessage = (MediaSaveTask)FileManagerImpl.-get8(FileManagerImpl.this).poll();
                    if (paramAnonymousMessage == null)
                    {
                      Log.d("FileManagerThread", "no file save task");
                      if (FileManagerImpl.-get9(FileManagerImpl.this).size() > 0) {
                        FileManagerImpl.-get4(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get4(FileManagerImpl.this), 1000));
                      }
                    }
                    else
                    {
                      if (paramAnonymousMessage.insertToMediaStore())
                      {
                        str = paramAnonymousMessage.getFilePath();
                        if ((FileManagerImpl.-wrap0(FileManagerImpl.this, str)) || (FileManagerImpl.-wrap1(FileManagerImpl.this, str))) {
                          MediaListManager.notifyFileAdded(str, paramAnonymousMessage.getCreatedTime());
                        }
                        FileManagerImpl.-get7(FileManagerImpl.this).put(str, paramAnonymousMessage.getContentUri());
                        FileManagerImpl.-wrap3(FileManagerImpl.this, FileManagerImpl.EVENT_MEDIA_SAVED, paramAnonymousMessage, false);
                        label849:
                        FileManagerImpl.-wrap7(FileManagerImpl.this, -paramAnonymousMessage.getMediaSize());
                        if (FileManagerImpl.-get9(FileManagerImpl.this).size() <= 0) {
                          break label994;
                        }
                        FileManagerImpl.-get4(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get4(FileManagerImpl.this), 1000));
                      }
                      while ((FileManagerImpl.-get2(FileManagerImpl.this).size() > 0) && (FileManagerImpl.-get8(FileManagerImpl.this).size() == 0))
                      {
                        FileManagerImpl.-get1(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get1(FileManagerImpl.this), 1004));
                        break;
                        FileManagerImpl.-wrap3(FileManagerImpl.this, FileManagerImpl.EVENT_MEDIA_SAVE_FAILED, paramAnonymousMessage, false);
                        break label849;
                        label994:
                        if (FileManagerImpl.-get8(FileManagerImpl.this).size() > 0) {
                          FileManagerImpl.-get4(FileManagerImpl.this).sendMessage(Message.obtain(FileManagerImpl.-get4(FileManagerImpl.this), 1001));
                        }
                      }
                      String str = (String)paramAnonymousMessage.obj;
                      File localFile = new File(str);
                      if (localFile.exists())
                      {
                        if (FileManagerImpl.-wrap0(FileManagerImpl.this, str))
                        {
                          paramAnonymousMessage = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                          label1085:
                          Log.v("FileManagerThread", "handleMessage() - Delete file: ", localFile.getAbsolutePath());
                          if (localFile.delete())
                          {
                            MediaListManager.notifyFileDeleted(str);
                            Log.v("FileManagerThread", "handleMessage() - Delete count: ", Integer.valueOf(FileManagerImpl.this.getContext().getContentResolver().delete(paramAnonymousMessage, "_data= ?", new String[] { str })));
                          }
                          if (MediaStore.Images.Media.EXTERNAL_CONTENT_URI.equals(paramAnonymousMessage))
                          {
                            paramAnonymousMessage = Path.getDirectoryPath(str) + "/" + Path.getFileNameWithoutExtension(str) + ".dng";
                            localFile = new File(paramAnonymousMessage);
                            if (localFile.exists())
                            {
                              Log.v("FileManagerThread", "handleMessage() - Delete raw file: " + paramAnonymousMessage);
                              if (!localFile.delete()) {
                                break label1323;
                              }
                              Log.v("FileManagerThread", "handleMessage() - Delete raw file count: ", Integer.valueOf(FileManagerImpl.this.getContext().getContentResolver().delete(MediaStore.Files.getContentUri("external"), "_data= ?", new String[] { paramAnonymousMessage })));
                            }
                          }
                        }
                        for (;;)
                        {
                          FileManagerImpl.-wrap4(FileManagerImpl.this, FileManagerImpl.EVENT_MEDIA_FILE_DELETED, str);
                          FileManagerImpl.this.getCameraThread().checkRemainingMediaCount();
                          break;
                          paramAnonymousMessage = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                          break label1085;
                          label1323:
                          Log.w("FileManagerThread", "handleMessage() - delete raw file failed");
                        }
                        FileManagerImpl.-get5(FileManagerImpl.this).clear();
                        FileManagerImpl.-get7(FileManagerImpl.this).clear();
                        if (FileManagerImpl.-get3(FileManagerImpl.this).exists())
                        {
                          FileManagerImpl.-get5(FileManagerImpl.this).addAll(FileManagerImpl.-wrap5(FileManagerImpl.this));
                          FileManagerImpl.-wrap2(FileManagerImpl.this, FileManagerImpl.EVENT_MEDIA_FILES_RESET, EventArgs.EMPTY);
                        }
                        else
                        {
                          FileManagerImpl.-wrap2(FileManagerImpl.this, FileManagerImpl.EVENT_MEDIA_FILES_RESET, EventArgs.EMPTY);
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      };
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/io/FileManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */