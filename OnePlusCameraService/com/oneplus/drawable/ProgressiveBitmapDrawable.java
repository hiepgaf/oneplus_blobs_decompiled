package com.oneplus.drawable;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import com.oneplus.base.SimpleRef;
import com.oneplus.cache.LruCache;
import com.oneplus.cache.MemoryBitmapLruCache;
import com.oneplus.io.StreamSource;
import com.oneplus.media.ImageUtils;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProgressiveBitmapDrawable
  extends Drawable
{
  private static final int BITMAP_REGION_DECODER_SIZE = 2;
  private static final long DURATION_FADE_IN_ANIMATION = 200L;
  private static final boolean ENABLE_SMOOTH_UPDATE = true;
  private static final int EXTRA_DECODING_PADDING = 2;
  private static final int FULL_SIZE = 4096;
  private static final int MAX_ACTIVE_TILE_COUNT = 64;
  private static final int MSG_BITMAP_DECODER_READY = 10001;
  private static final int MSG_INVALIDATE = 10000;
  private static final int MSG_UPDATE_BITMAP_INFO = 10002;
  private static final int MSG_UPDATE_BITMAP_TILE = 10010;
  private static final int MSG_UPDATE_FULL_SIZE_BITMAP = 10011;
  private static final boolean PRINT_TRACE_LOGS = false;
  private static final String TAG = "ProgressiveBitmapDrawable";
  private static final int TILE_SIZE = 1024;
  private static final Executor m_DecodingExecutors = Executors.newFixedThreadPool(8);
  private static final Executor m_ReleaseExecutors = Executors.newFixedThreadPool(1);
  private Bitmap.Config m_BitmapConfig;
  private BitmapDecoderInitTask m_BitmapDecoderInitTask;
  private BitmapRegionDecoder[] m_BitmapDecoders;
  private BitmapInfoDecodingTask m_BitmapInfoDecodingTask;
  private BitmapTilesDecodingTask[] m_BitmapTilesDecodingTasks;
  private Ref<Boolean> m_CancelStreamOpeningRef = new SimpleRef(Boolean.valueOf(false));
  private ContentResolver m_ContentResolver;
  private final RectF m_DestDrawingBounds = new RectF();
  private final Rect m_DrawingBounds = new Rect();
  private int m_EndTileX;
  private int m_EndTileY;
  private volatile Bitmap m_FullSizeBitmap;
  private volatile Handler m_Handler;
  private final Runnable m_InvalidateRunnable = new Runnable()
  {
    public void run()
    {
      ProgressiveBitmapDrawable.this.invalidateSelf();
    }
  };
  private boolean m_IsBitmapDecoderReady;
  private boolean m_IsHighQualityBitmapEnabled = true;
  private volatile boolean m_IsReleased;
  private volatile int m_Orientation;
  private volatile int m_OriginalHeight = -1;
  private final LruCache<Integer, Bitmap> m_OriginalTilesCache = new MemoryBitmapLruCache(67108864L);
  private volatile int m_OriginalWidth = -1;
  private Paint m_Paint;
  private int m_SampleSize = 1;
  private Object m_Source;
  private final Rect m_SrcDrawingBounds = new Rect();
  private int m_StartTileX;
  private int m_StartTileY;
  private Bitmap m_ThumbnailBitmap;
  private Queue<Integer> m_TileDecodingQueue;
  private LinkedList<Integer> m_TileUsageQueue;
  private Tile[][] m_Tiles;
  private boolean m_UseFullSizeBitmap;
  
  public ProgressiveBitmapDrawable(ContentResolver paramContentResolver, Uri paramUri, Bitmap.Config paramConfig, Bitmap paramBitmap)
  {
    this.m_ContentResolver = paramContentResolver;
    initialize(paramUri, paramConfig, paramBitmap);
  }
  
  public ProgressiveBitmapDrawable(StreamSource paramStreamSource, Bitmap.Config paramConfig, Bitmap paramBitmap)
  {
    initialize(paramStreamSource, paramConfig, paramBitmap);
  }
  
  public ProgressiveBitmapDrawable(String paramString, Bitmap.Config paramConfig, Bitmap paramBitmap)
  {
    initialize(paramString, paramConfig, paramBitmap);
  }
  
  private void calculateDecodeRect(Rect paramRect, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    switch (this.m_Orientation)
    {
    default: 
      paramRect.offsetTo(paramInt1 * paramInt3, paramInt2 * paramInt3);
      paramRect.right = Math.min(paramInt4, paramRect.left + paramInt3);
      paramRect.bottom = Math.min(paramInt5, paramRect.top + paramInt3);
      return;
    case 90: 
      paramRect.offsetTo(paramInt2 * paramInt3, paramInt5 - (paramInt1 + 1) * paramInt3);
      paramRect.right = Math.min(paramInt4, paramRect.left + paramInt3);
      paramRect.bottom = (paramRect.top + paramInt3);
      paramRect.top = Math.max(0, paramRect.top);
      return;
    case 270: 
      paramRect.offsetTo(paramInt4 - (paramInt2 + 1) * paramInt3, paramInt1 * paramInt3);
      paramRect.right = (paramRect.left + paramInt3);
      paramRect.bottom = Math.min(paramInt5, paramRect.top + paramInt3);
      paramRect.left = Math.max(0, paramRect.left);
      return;
    }
    paramRect.offsetTo(paramInt4 - (paramInt1 + 1) * paramInt3, paramInt5 - (paramInt2 + 1) * paramInt3);
    paramRect.right = (paramRect.left + paramInt3);
    paramRect.bottom = (paramRect.top + paramInt3);
    paramRect.left = Math.max(0, paramRect.left);
    paramRect.top = Math.max(0, paramRect.top);
  }
  
  private void cancelDecodingBitmapTiles()
  {
    if (this.m_BitmapTilesDecodingTasks != null) {}
    synchronized (this.m_TileDecodingQueue)
    {
      this.m_TileDecodingQueue.notifyAll();
      BitmapTilesDecodingTask[] arrayOfBitmapTilesDecodingTask = this.m_BitmapTilesDecodingTasks;
      int i = 0;
      int j = arrayOfBitmapTilesDecodingTask.length;
      while (i < j)
      {
        arrayOfBitmapTilesDecodingTask[i].isCancelled = true;
        i += 1;
      }
      this.m_BitmapTilesDecodingTasks = null;
      this.m_TileDecodingQueue = null;
      this.m_TileUsageQueue = null;
      this.m_Tiles = null;
      invalidateSelf();
      return;
    }
  }
  
  /* Error */
  private void decodeBitmapInfoAsync()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 8
    //   6: aconst_null
    //   7: astore 6
    //   9: aconst_null
    //   10: astore 7
    //   12: new 152	com/oneplus/base/SimpleRef
    //   15: dup
    //   16: iconst_0
    //   17: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   20: invokespecial 161	com/oneplus/base/SimpleRef:<init>	(Ljava/lang/Object;)V
    //   23: astore 9
    //   25: new 152	com/oneplus/base/SimpleRef
    //   28: dup
    //   29: iconst_0
    //   30: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   33: invokespecial 161	com/oneplus/base/SimpleRef:<init>	(Ljava/lang/Object;)V
    //   36: astore 10
    //   38: new 152	com/oneplus/base/SimpleRef
    //   41: dup
    //   42: iconst_0
    //   43: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   46: invokespecial 161	com/oneplus/base/SimpleRef:<init>	(Ljava/lang/Object;)V
    //   49: astore 11
    //   51: aload_0
    //   52: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   55: instanceof 264
    //   58: ifeq +229 -> 287
    //   61: new 266	android/graphics/BitmapFactory$Options
    //   64: dup
    //   65: invokespecial 267	android/graphics/BitmapFactory$Options:<init>	()V
    //   68: astore_2
    //   69: aload_2
    //   70: iconst_1
    //   71: putfield 270	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   74: aload_0
    //   75: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   78: checkcast 264	java/lang/String
    //   81: aload_2
    //   82: invokestatic 276	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   85: pop
    //   86: aload 11
    //   88: aload_0
    //   89: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   92: checkcast 264	java/lang/String
    //   95: invokestatic 282	com/oneplus/media/ImageUtils:decodeOrientation	(Ljava/lang/String;)I
    //   98: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   101: invokeinterface 287 2 0
    //   106: aload 11
    //   108: invokeinterface 291 1 0
    //   113: checkcast 257	java/lang/Integer
    //   116: invokevirtual 295	java/lang/Integer:intValue	()I
    //   119: bipush 90
    //   121: if_icmpeq +22 -> 143
    //   124: aload 11
    //   126: invokeinterface 291 1 0
    //   131: checkcast 257	java/lang/Integer
    //   134: invokevirtual 295	java/lang/Integer:intValue	()I
    //   137: sipush 270
    //   140: if_icmpne +85 -> 225
    //   143: aload 9
    //   145: aload_2
    //   146: getfield 298	android/graphics/BitmapFactory$Options:outHeight	I
    //   149: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   152: invokeinterface 287 2 0
    //   157: aload 10
    //   159: aload_2
    //   160: getfield 301	android/graphics/BitmapFactory$Options:outWidth	I
    //   163: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   166: invokeinterface 287 2 0
    //   171: aload_0
    //   172: getfield 303	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Handler	Landroid/os/Handler;
    //   175: sipush 10002
    //   178: aload 9
    //   180: invokeinterface 291 1 0
    //   185: checkcast 257	java/lang/Integer
    //   188: invokevirtual 295	java/lang/Integer:intValue	()I
    //   191: aload 10
    //   193: invokeinterface 291 1 0
    //   198: checkcast 257	java/lang/Integer
    //   201: invokevirtual 295	java/lang/Integer:intValue	()I
    //   204: iconst_1
    //   205: anewarray 240	java/lang/Object
    //   208: dup
    //   209: iconst_0
    //   210: aload 11
    //   212: invokeinterface 291 1 0
    //   217: aastore
    //   218: invokestatic 309	android/os/Message:obtain	(Landroid/os/Handler;IIILjava/lang/Object;)Landroid/os/Message;
    //   221: invokevirtual 312	android/os/Message:sendToTarget	()V
    //   224: return
    //   225: aload 9
    //   227: aload_2
    //   228: getfield 301	android/graphics/BitmapFactory$Options:outWidth	I
    //   231: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   234: invokeinterface 287 2 0
    //   239: aload 10
    //   241: aload_2
    //   242: getfield 298	android/graphics/BitmapFactory$Options:outHeight	I
    //   245: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   248: invokeinterface 287 2 0
    //   253: goto -82 -> 171
    //   256: astore_2
    //   257: aload_0
    //   258: getfield 163	com/oneplus/drawable/ProgressiveBitmapDrawable:m_CancelStreamOpeningRef	Lcom/oneplus/base/Ref;
    //   261: invokeinterface 291 1 0
    //   266: checkcast 154	java/lang/Boolean
    //   269: invokevirtual 316	java/lang/Boolean:booleanValue	()Z
    //   272: ifne -101 -> 171
    //   275: ldc 53
    //   277: ldc_w 318
    //   280: aload_2
    //   281: invokestatic 324	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   284: goto -113 -> 171
    //   287: aload_0
    //   288: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   291: instanceof 326
    //   294: istore_1
    //   295: iload_1
    //   296: ifeq +116 -> 412
    //   299: aconst_null
    //   300: astore_3
    //   301: aconst_null
    //   302: astore_2
    //   303: aload_0
    //   304: getfield 198	com/oneplus/drawable/ProgressiveBitmapDrawable:m_ContentResolver	Landroid/content/ContentResolver;
    //   307: aload_0
    //   308: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   311: checkcast 326	android/net/Uri
    //   314: invokevirtual 332	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   317: astore 4
    //   319: aload 4
    //   321: astore_2
    //   322: aload 4
    //   324: astore_3
    //   325: aload_0
    //   326: aload 4
    //   328: aload 9
    //   330: aload 10
    //   332: aload 11
    //   334: invokespecial 335	com/oneplus/drawable/ProgressiveBitmapDrawable:decodeBitmapInfoAsync	(Ljava/io/InputStream;Lcom/oneplus/base/Ref;Lcom/oneplus/base/Ref;Lcom/oneplus/base/Ref;)V
    //   337: aload 7
    //   339: astore_2
    //   340: aload 4
    //   342: ifnull +11 -> 353
    //   345: aload 4
    //   347: invokevirtual 340	java/io/InputStream:close	()V
    //   350: aload 7
    //   352: astore_2
    //   353: aload_2
    //   354: ifnull -183 -> 171
    //   357: aload_2
    //   358: athrow
    //   359: astore_2
    //   360: goto -7 -> 353
    //   363: astore_3
    //   364: aload_3
    //   365: athrow
    //   366: astore 4
    //   368: aload_3
    //   369: astore 5
    //   371: aload_2
    //   372: ifnull +10 -> 382
    //   375: aload_2
    //   376: invokevirtual 340	java/io/InputStream:close	()V
    //   379: aload_3
    //   380: astore 5
    //   382: aload 5
    //   384: ifnull +25 -> 409
    //   387: aload 5
    //   389: athrow
    //   390: aload_3
    //   391: astore 5
    //   393: aload_3
    //   394: aload_2
    //   395: if_acmpeq -13 -> 382
    //   398: aload_3
    //   399: aload_2
    //   400: invokevirtual 344	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   403: aload_3
    //   404: astore 5
    //   406: goto -24 -> 382
    //   409: aload 4
    //   411: athrow
    //   412: aload_0
    //   413: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   416: instanceof 346
    //   419: istore_1
    //   420: iload_1
    //   421: ifeq +118 -> 539
    //   424: aconst_null
    //   425: astore_3
    //   426: aconst_null
    //   427: astore_2
    //   428: aload_0
    //   429: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   432: checkcast 346	com/oneplus/io/StreamSource
    //   435: aload_0
    //   436: getfield 163	com/oneplus/drawable/ProgressiveBitmapDrawable:m_CancelStreamOpeningRef	Lcom/oneplus/base/Ref;
    //   439: invokeinterface 349 2 0
    //   444: astore 4
    //   446: aload 4
    //   448: astore_2
    //   449: aload 4
    //   451: astore_3
    //   452: aload_0
    //   453: aload 4
    //   455: aload 9
    //   457: aload 10
    //   459: aload 11
    //   461: invokespecial 335	com/oneplus/drawable/ProgressiveBitmapDrawable:decodeBitmapInfoAsync	(Ljava/io/InputStream;Lcom/oneplus/base/Ref;Lcom/oneplus/base/Ref;Lcom/oneplus/base/Ref;)V
    //   464: aload 8
    //   466: astore_2
    //   467: aload 4
    //   469: ifnull +11 -> 480
    //   472: aload 4
    //   474: invokevirtual 340	java/io/InputStream:close	()V
    //   477: aload 8
    //   479: astore_2
    //   480: aload_2
    //   481: ifnull -310 -> 171
    //   484: aload_2
    //   485: athrow
    //   486: astore_2
    //   487: goto -7 -> 480
    //   490: astore_3
    //   491: aload_3
    //   492: athrow
    //   493: astore 4
    //   495: aload_3
    //   496: astore 5
    //   498: aload_2
    //   499: ifnull +10 -> 509
    //   502: aload_2
    //   503: invokevirtual 340	java/io/InputStream:close	()V
    //   506: aload_3
    //   507: astore 5
    //   509: aload 5
    //   511: ifnull +25 -> 536
    //   514: aload 5
    //   516: athrow
    //   517: aload_3
    //   518: astore 5
    //   520: aload_3
    //   521: aload_2
    //   522: if_acmpeq -13 -> 509
    //   525: aload_3
    //   526: aload_2
    //   527: invokevirtual 344	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   530: aload_3
    //   531: astore 5
    //   533: goto -24 -> 509
    //   536: aload 4
    //   538: athrow
    //   539: ldc 53
    //   541: new 351	java/lang/StringBuilder
    //   544: dup
    //   545: invokespecial 352	java/lang/StringBuilder:<init>	()V
    //   548: ldc_w 354
    //   551: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   554: aload_0
    //   555: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   558: invokevirtual 361	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   561: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   564: invokestatic 368	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   567: return
    //   568: astore 4
    //   570: aload_3
    //   571: astore_2
    //   572: aload 6
    //   574: astore_3
    //   575: goto -80 -> 495
    //   578: astore 4
    //   580: aload_3
    //   581: astore_2
    //   582: aload 5
    //   584: astore_3
    //   585: goto -217 -> 368
    //   588: astore_2
    //   589: aload_3
    //   590: ifnonnull -200 -> 390
    //   593: aload_2
    //   594: astore 5
    //   596: goto -214 -> 382
    //   599: astore_2
    //   600: aload_3
    //   601: ifnonnull -84 -> 517
    //   604: aload_2
    //   605: astore 5
    //   607: goto -98 -> 509
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	610	0	this	ProgressiveBitmapDrawable
    //   294	127	1	bool	boolean
    //   68	174	2	localOptions	BitmapFactory.Options
    //   256	25	2	localThrowable1	Throwable
    //   302	56	2	localObject1	Object
    //   359	41	2	localThrowable2	Throwable
    //   427	58	2	localObject2	Object
    //   486	41	2	localThrowable3	Throwable
    //   571	11	2	localObject3	Object
    //   588	6	2	localThrowable4	Throwable
    //   599	6	2	localThrowable5	Throwable
    //   300	25	3	localObject4	Object
    //   363	41	3	localThrowable6	Throwable
    //   425	27	3	localObject5	Object
    //   490	81	3	localThrowable7	Throwable
    //   574	27	3	localObject6	Object
    //   317	29	4	localInputStream1	InputStream
    //   366	44	4	localObject7	Object
    //   444	29	4	localInputStream2	InputStream
    //   493	44	4	localObject8	Object
    //   568	1	4	localObject9	Object
    //   578	1	4	localObject10	Object
    //   1	605	5	localObject11	Object
    //   7	566	6	localObject12	Object
    //   10	341	7	localObject13	Object
    //   4	474	8	localObject14	Object
    //   23	433	9	localSimpleRef1	SimpleRef
    //   36	422	10	localSimpleRef2	SimpleRef
    //   49	411	11	localSimpleRef3	SimpleRef
    // Exception table:
    //   from	to	target	type
    //   51	143	256	java/lang/Throwable
    //   143	171	256	java/lang/Throwable
    //   225	253	256	java/lang/Throwable
    //   287	295	256	java/lang/Throwable
    //   357	359	256	java/lang/Throwable
    //   387	390	256	java/lang/Throwable
    //   398	403	256	java/lang/Throwable
    //   409	412	256	java/lang/Throwable
    //   412	420	256	java/lang/Throwable
    //   484	486	256	java/lang/Throwable
    //   514	517	256	java/lang/Throwable
    //   525	530	256	java/lang/Throwable
    //   536	539	256	java/lang/Throwable
    //   539	567	256	java/lang/Throwable
    //   345	350	359	java/lang/Throwable
    //   303	319	363	java/lang/Throwable
    //   325	337	363	java/lang/Throwable
    //   364	366	366	finally
    //   472	477	486	java/lang/Throwable
    //   428	446	490	java/lang/Throwable
    //   452	464	490	java/lang/Throwable
    //   491	493	493	finally
    //   428	446	568	finally
    //   452	464	568	finally
    //   303	319	578	finally
    //   325	337	578	finally
    //   375	379	588	java/lang/Throwable
    //   502	506	599	java/lang/Throwable
  }
  
  private void decodeBitmapInfoAsync(InputStream paramInputStream, Ref<Integer> paramRef1, Ref<Integer> paramRef2, Ref<Integer> paramRef3)
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inJustDecodeBounds = true;
    paramRef3.set(Integer.valueOf(ImageUtils.decodeOrientation(paramInputStream)));
    BitmapFactory.decodeStream(paramInputStream, null, localOptions);
    if ((((Integer)paramRef3.get()).intValue() == 90) || (((Integer)paramRef3.get()).intValue() == 270))
    {
      paramRef1.set(Integer.valueOf(localOptions.outHeight));
      paramRef2.set(Integer.valueOf(localOptions.outWidth));
      return;
    }
    paramRef1.set(Integer.valueOf(localOptions.outWidth));
    paramRef2.set(Integer.valueOf(localOptions.outHeight));
  }
  
  /* Error */
  private void decodeTilesAsync(BitmapTilesDecodingTask paramBitmapTilesDecodingTask)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 246	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:isCancelled	Z
    //   4: ifeq +4 -> 8
    //   7: return
    //   8: ldc 53
    //   10: ldc_w 379
    //   13: aload_1
    //   14: getfield 382	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:sampleSize	I
    //   17: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   20: invokestatic 386	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   23: new 266	android/graphics/BitmapFactory$Options
    //   26: dup
    //   27: invokespecial 267	android/graphics/BitmapFactory$Options:<init>	()V
    //   30: astore 13
    //   32: aload 13
    //   34: iconst_1
    //   35: putfield 389	android/graphics/BitmapFactory$Options:inPreferQualityOverSpeed	Z
    //   38: aload 13
    //   40: iconst_1
    //   41: putfield 392	android/graphics/BitmapFactory$Options:inDither	Z
    //   44: aload 13
    //   46: aload_0
    //   47: getfield 394	com/oneplus/drawable/ProgressiveBitmapDrawable:m_BitmapConfig	Landroid/graphics/Bitmap$Config;
    //   50: putfield 397	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   53: aload 13
    //   55: aload_1
    //   56: getfield 382	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:sampleSize	I
    //   59: putfield 400	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   62: aload_0
    //   63: getfield 208	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Orientation	I
    //   66: bipush 90
    //   68: if_icmpeq +13 -> 81
    //   71: aload_0
    //   72: getfield 208	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Orientation	I
    //   75: sipush 270
    //   78: if_icmpne +57 -> 135
    //   81: aload_0
    //   82: getfield 177	com/oneplus/drawable/ProgressiveBitmapDrawable:m_OriginalHeight	I
    //   85: istore_2
    //   86: aload_0
    //   87: getfield 188	com/oneplus/drawable/ProgressiveBitmapDrawable:m_OriginalWidth	I
    //   90: istore_3
    //   91: aload_1
    //   92: getfield 382	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:sampleSize	I
    //   95: sipush 1024
    //   98: imul
    //   99: istore 4
    //   101: aload_1
    //   102: getfield 246	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:isCancelled	Z
    //   105: ifeq +43 -> 148
    //   108: ldc 53
    //   110: ldc_w 379
    //   113: aload_1
    //   114: getfield 382	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:sampleSize	I
    //   117: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   120: ldc_w 402
    //   123: invokestatic 405	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   126: ldc 53
    //   128: ldc_w 407
    //   131: invokestatic 409	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   134: return
    //   135: aload_0
    //   136: getfield 188	com/oneplus/drawable/ProgressiveBitmapDrawable:m_OriginalWidth	I
    //   139: istore_2
    //   140: aload_0
    //   141: getfield 177	com/oneplus/drawable/ProgressiveBitmapDrawable:m_OriginalHeight	I
    //   144: istore_3
    //   145: goto -54 -> 91
    //   148: aload_1
    //   149: getfield 412	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:decodingQueue	Ljava/util/Queue;
    //   152: astore 8
    //   154: aload 8
    //   156: monitorenter
    //   157: aload_1
    //   158: getfield 412	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:decodingQueue	Ljava/util/Queue;
    //   161: invokeinterface 417 1 0
    //   166: checkcast 257	java/lang/Integer
    //   169: astore 14
    //   171: aload 14
    //   173: ifnonnull +29 -> 202
    //   176: aload_1
    //   177: getfield 412	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:decodingQueue	Ljava/util/Queue;
    //   180: invokevirtual 420	java/lang/Object:wait	()V
    //   183: aload 8
    //   185: monitorexit
    //   186: goto -85 -> 101
    //   189: astore_1
    //   190: ldc 53
    //   192: ldc_w 422
    //   195: aload_1
    //   196: invokestatic 324	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   199: goto -73 -> 126
    //   202: aload 14
    //   204: invokevirtual 295	java/lang/Integer:intValue	()I
    //   207: bipush 16
    //   209: iushr
    //   210: istore 5
    //   212: aload 14
    //   214: invokevirtual 295	java/lang/Integer:intValue	()I
    //   217: istore 6
    //   219: iload 6
    //   221: ldc_w 423
    //   224: iand
    //   225: istore 6
    //   227: aload 8
    //   229: monitorexit
    //   230: aconst_null
    //   231: astore 8
    //   233: aload_1
    //   234: getfield 382	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:sampleSize	I
    //   237: iconst_1
    //   238: if_icmpne +67 -> 305
    //   241: aload_0
    //   242: getfield 186	com/oneplus/drawable/ProgressiveBitmapDrawable:m_OriginalTilesCache	Lcom/oneplus/cache/LruCache;
    //   245: aload 14
    //   247: aconst_null
    //   248: lconst_0
    //   249: invokevirtual 428	com/oneplus/cache/LruCache:get	(Ljava/lang/Object;Ljava/lang/Object;J)Ljava/lang/Object;
    //   252: checkcast 430	android/graphics/Bitmap
    //   255: astore 9
    //   257: aload 9
    //   259: astore 8
    //   261: aload 9
    //   263: ifnull +42 -> 305
    //   266: aload_0
    //   267: getfield 303	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Handler	Landroid/os/Handler;
    //   270: sipush 10010
    //   273: iload 5
    //   275: iload 6
    //   277: iconst_2
    //   278: anewarray 240	java/lang/Object
    //   281: dup
    //   282: iconst_0
    //   283: aload_1
    //   284: aastore
    //   285: dup
    //   286: iconst_1
    //   287: aload 9
    //   289: aastore
    //   290: invokestatic 309	android/os/Message:obtain	(Landroid/os/Handler;IIILjava/lang/Object;)Landroid/os/Message;
    //   293: invokevirtual 312	android/os/Message:sendToTarget	()V
    //   296: goto -195 -> 101
    //   299: astore_1
    //   300: aload 8
    //   302: monitorexit
    //   303: aload_1
    //   304: athrow
    //   305: aload_1
    //   306: getfield 434	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:decoder	Landroid/graphics/BitmapRegionDecoder;
    //   309: ifnull +135 -> 444
    //   312: new 170	android/graphics/Rect
    //   315: dup
    //   316: invokespecial 171	android/graphics/Rect:<init>	()V
    //   319: astore 9
    //   321: aload_0
    //   322: aload 9
    //   324: iload 5
    //   326: iload 6
    //   328: iload 4
    //   330: iload_2
    //   331: iload_3
    //   332: invokespecial 436	com/oneplus/drawable/ProgressiveBitmapDrawable:calculateDecodeRect	(Landroid/graphics/Rect;IIIII)V
    //   335: aload_1
    //   336: getfield 434	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:decoder	Landroid/graphics/BitmapRegionDecoder;
    //   339: astore 10
    //   341: aload 10
    //   343: monitorenter
    //   344: aload_1
    //   345: getfield 246	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:isCancelled	Z
    //   348: ifeq +27 -> 375
    //   351: ldc 53
    //   353: ldc_w 379
    //   356: aload_1
    //   357: getfield 382	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:sampleSize	I
    //   360: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   363: ldc_w 438
    //   366: invokestatic 405	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   369: aload 10
    //   371: monitorexit
    //   372: goto -246 -> 126
    //   375: aload_1
    //   376: getfield 434	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:decoder	Landroid/graphics/BitmapRegionDecoder;
    //   379: aload 9
    //   381: aload 13
    //   383: invokevirtual 444	android/graphics/BitmapRegionDecoder:decodeRegion	(Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   386: astore 9
    //   388: aload 9
    //   390: astore 8
    //   392: aload 10
    //   394: monitorexit
    //   395: aload_1
    //   396: getfield 246	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:isCancelled	Z
    //   399: ifeq +618 -> 1017
    //   402: ldc 53
    //   404: ldc_w 379
    //   407: aload_1
    //   408: getfield 382	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:sampleSize	I
    //   411: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   414: ldc_w 402
    //   417: invokestatic 405	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   420: goto -294 -> 126
    //   423: astore 9
    //   425: ldc 53
    //   427: ldc_w 446
    //   430: aload 9
    //   432: invokestatic 386	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   435: goto -43 -> 392
    //   438: astore_1
    //   439: aload 10
    //   441: monitorexit
    //   442: aload_1
    //   443: athrow
    //   444: aload_0
    //   445: getfield 448	com/oneplus/drawable/ProgressiveBitmapDrawable:m_FullSizeBitmap	Landroid/graphics/Bitmap;
    //   448: ifnonnull +449 -> 897
    //   451: ldc 53
    //   453: ldc_w 450
    //   456: invokestatic 409	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   459: aload_0
    //   460: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   463: instanceof 264
    //   466: ifeq +43 -> 509
    //   469: aload_0
    //   470: aload_0
    //   471: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   474: checkcast 264	java/lang/String
    //   477: sipush 4096
    //   480: sipush 4096
    //   483: iconst_3
    //   484: getstatic 455	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   487: invokestatic 459	com/oneplus/media/ImageUtils:decodeBitmap	(Ljava/lang/String;IIILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   490: putfield 448	com/oneplus/drawable/ProgressiveBitmapDrawable:m_FullSizeBitmap	Landroid/graphics/Bitmap;
    //   493: aload_0
    //   494: getfield 448	com/oneplus/drawable/ProgressiveBitmapDrawable:m_FullSizeBitmap	Landroid/graphics/Bitmap;
    //   497: ifnonnull +400 -> 897
    //   500: ldc 53
    //   502: ldc_w 461
    //   505: invokestatic 368	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   508: return
    //   509: aload_0
    //   510: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   513: instanceof 326
    //   516: istore 7
    //   518: iload 7
    //   520: ifeq +182 -> 702
    //   523: aconst_null
    //   524: astore 11
    //   526: aconst_null
    //   527: astore 12
    //   529: aconst_null
    //   530: astore 9
    //   532: aconst_null
    //   533: astore 8
    //   535: aload_0
    //   536: getfield 198	com/oneplus/drawable/ProgressiveBitmapDrawable:m_ContentResolver	Landroid/content/ContentResolver;
    //   539: aload_0
    //   540: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   543: checkcast 326	android/net/Uri
    //   546: invokevirtual 332	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   549: astore 10
    //   551: aload 10
    //   553: astore 8
    //   555: aload 10
    //   557: astore 9
    //   559: aload_0
    //   560: aload 10
    //   562: sipush 4096
    //   565: sipush 4096
    //   568: iconst_3
    //   569: getstatic 455	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   572: invokestatic 464	com/oneplus/media/ImageUtils:decodeBitmap	(Ljava/io/InputStream;IIILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   575: putfield 448	com/oneplus/drawable/ProgressiveBitmapDrawable:m_FullSizeBitmap	Landroid/graphics/Bitmap;
    //   578: aload 12
    //   580: astore 8
    //   582: aload 10
    //   584: ifnull +12 -> 596
    //   587: aload 10
    //   589: invokevirtual 340	java/io/InputStream:close	()V
    //   592: aload 12
    //   594: astore 8
    //   596: aload 8
    //   598: ifnull -105 -> 493
    //   601: aload 8
    //   603: athrow
    //   604: astore 8
    //   606: aload_0
    //   607: aconst_null
    //   608: putfield 448	com/oneplus/drawable/ProgressiveBitmapDrawable:m_FullSizeBitmap	Landroid/graphics/Bitmap;
    //   611: ldc 53
    //   613: ldc_w 466
    //   616: aload 8
    //   618: invokestatic 324	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   621: goto -128 -> 493
    //   624: astore 8
    //   626: goto -30 -> 596
    //   629: astore 10
    //   631: aload 10
    //   633: athrow
    //   634: astore 11
    //   636: aload 8
    //   638: astore 9
    //   640: aload 10
    //   642: astore 8
    //   644: aload 11
    //   646: astore 10
    //   648: aload 8
    //   650: astore 11
    //   652: aload 9
    //   654: ifnull +12 -> 666
    //   657: aload 9
    //   659: invokevirtual 340	java/io/InputStream:close	()V
    //   662: aload 8
    //   664: astore 11
    //   666: aload 11
    //   668: ifnull +31 -> 699
    //   671: aload 11
    //   673: athrow
    //   674: aload 8
    //   676: astore 11
    //   678: aload 8
    //   680: aload 9
    //   682: if_acmpeq -16 -> 666
    //   685: aload 8
    //   687: aload 9
    //   689: invokevirtual 344	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   692: aload 8
    //   694: astore 11
    //   696: goto -30 -> 666
    //   699: aload 10
    //   701: athrow
    //   702: aload_0
    //   703: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   706: instanceof 346
    //   709: istore 7
    //   711: iload 7
    //   713: ifeq -220 -> 493
    //   716: aconst_null
    //   717: astore 11
    //   719: aconst_null
    //   720: astore 12
    //   722: aconst_null
    //   723: astore 9
    //   725: aconst_null
    //   726: astore 8
    //   728: aload_0
    //   729: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   732: checkcast 346	com/oneplus/io/StreamSource
    //   735: aload_0
    //   736: getfield 163	com/oneplus/drawable/ProgressiveBitmapDrawable:m_CancelStreamOpeningRef	Lcom/oneplus/base/Ref;
    //   739: invokeinterface 349 2 0
    //   744: astore 10
    //   746: aload 10
    //   748: astore 8
    //   750: aload 10
    //   752: astore 9
    //   754: aload_0
    //   755: aload 10
    //   757: sipush 4096
    //   760: sipush 4096
    //   763: iconst_3
    //   764: getstatic 455	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   767: invokestatic 464	com/oneplus/media/ImageUtils:decodeBitmap	(Ljava/io/InputStream;IIILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   770: putfield 448	com/oneplus/drawable/ProgressiveBitmapDrawable:m_FullSizeBitmap	Landroid/graphics/Bitmap;
    //   773: aload 12
    //   775: astore 8
    //   777: aload 10
    //   779: ifnull +12 -> 791
    //   782: aload 10
    //   784: invokevirtual 340	java/io/InputStream:close	()V
    //   787: aload 12
    //   789: astore 8
    //   791: aload 8
    //   793: ifnull -300 -> 493
    //   796: aload 8
    //   798: athrow
    //   799: astore 8
    //   801: aload_0
    //   802: aconst_null
    //   803: putfield 448	com/oneplus/drawable/ProgressiveBitmapDrawable:m_FullSizeBitmap	Landroid/graphics/Bitmap;
    //   806: ldc 53
    //   808: ldc_w 468
    //   811: aload 8
    //   813: invokestatic 324	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   816: goto -323 -> 493
    //   819: astore 8
    //   821: goto -30 -> 791
    //   824: astore 10
    //   826: aload 10
    //   828: athrow
    //   829: astore 11
    //   831: aload 8
    //   833: astore 9
    //   835: aload 10
    //   837: astore 8
    //   839: aload 11
    //   841: astore 10
    //   843: aload 8
    //   845: astore 11
    //   847: aload 9
    //   849: ifnull +12 -> 861
    //   852: aload 9
    //   854: invokevirtual 340	java/io/InputStream:close	()V
    //   857: aload 8
    //   859: astore 11
    //   861: aload 11
    //   863: ifnull +31 -> 894
    //   866: aload 11
    //   868: athrow
    //   869: aload 8
    //   871: astore 11
    //   873: aload 8
    //   875: aload 9
    //   877: if_acmpeq -16 -> 861
    //   880: aload 8
    //   882: aload 9
    //   884: invokevirtual 344	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   887: aload 8
    //   889: astore 11
    //   891: goto -30 -> 861
    //   894: aload 10
    //   896: athrow
    //   897: aload_1
    //   898: getfield 246	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:isCancelled	Z
    //   901: ifeq +24 -> 925
    //   904: ldc 53
    //   906: ldc_w 379
    //   909: aload_1
    //   910: getfield 382	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:sampleSize	I
    //   913: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   916: ldc_w 402
    //   919: invokestatic 405	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   922: goto -796 -> 126
    //   925: new 170	android/graphics/Rect
    //   928: dup
    //   929: invokespecial 171	android/graphics/Rect:<init>	()V
    //   932: astore 8
    //   934: aload_0
    //   935: getfield 448	com/oneplus/drawable/ProgressiveBitmapDrawable:m_FullSizeBitmap	Landroid/graphics/Bitmap;
    //   938: invokevirtual 471	android/graphics/Bitmap:getWidth	()I
    //   941: i2f
    //   942: iload_2
    //   943: aload_1
    //   944: getfield 382	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:sampleSize	I
    //   947: idiv
    //   948: i2f
    //   949: fdiv
    //   950: ldc_w 472
    //   953: fmul
    //   954: invokestatic 476	java/lang/Math:round	(F)I
    //   957: istore 4
    //   959: aload_0
    //   960: aload 8
    //   962: iload 5
    //   964: iload 6
    //   966: iload 4
    //   968: aload_0
    //   969: getfield 448	com/oneplus/drawable/ProgressiveBitmapDrawable:m_FullSizeBitmap	Landroid/graphics/Bitmap;
    //   972: invokevirtual 471	android/graphics/Bitmap:getWidth	()I
    //   975: aload_0
    //   976: getfield 448	com/oneplus/drawable/ProgressiveBitmapDrawable:m_FullSizeBitmap	Landroid/graphics/Bitmap;
    //   979: invokevirtual 479	android/graphics/Bitmap:getHeight	()I
    //   982: invokespecial 436	com/oneplus/drawable/ProgressiveBitmapDrawable:calculateDecodeRect	(Landroid/graphics/Rect;IIIII)V
    //   985: aload_0
    //   986: getfield 448	com/oneplus/drawable/ProgressiveBitmapDrawable:m_FullSizeBitmap	Landroid/graphics/Bitmap;
    //   989: aload 8
    //   991: getfield 215	android/graphics/Rect:left	I
    //   994: aload 8
    //   996: getfield 227	android/graphics/Rect:top	I
    //   999: aload 8
    //   1001: invokevirtual 482	android/graphics/Rect:width	()I
    //   1004: aload 8
    //   1006: invokevirtual 485	android/graphics/Rect:height	()I
    //   1009: invokestatic 489	android/graphics/Bitmap:createBitmap	(Landroid/graphics/Bitmap;IIII)Landroid/graphics/Bitmap;
    //   1012: astore 8
    //   1014: goto -619 -> 395
    //   1017: aload 8
    //   1019: astore 9
    //   1021: aload_0
    //   1022: getfield 208	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Orientation	I
    //   1025: ifeq +73 -> 1098
    //   1028: new 491	android/graphics/Matrix
    //   1031: dup
    //   1032: invokespecial 492	android/graphics/Matrix:<init>	()V
    //   1035: astore 9
    //   1037: aload 9
    //   1039: aload_0
    //   1040: getfield 208	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Orientation	I
    //   1043: i2f
    //   1044: invokevirtual 496	android/graphics/Matrix:postRotate	(F)Z
    //   1047: pop
    //   1048: aload 8
    //   1050: iconst_0
    //   1051: iconst_0
    //   1052: aload 8
    //   1054: invokevirtual 471	android/graphics/Bitmap:getWidth	()I
    //   1057: aload 8
    //   1059: invokevirtual 479	android/graphics/Bitmap:getHeight	()I
    //   1062: aload 9
    //   1064: iconst_0
    //   1065: invokestatic 499	android/graphics/Bitmap:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   1068: astore 9
    //   1070: aload_1
    //   1071: getfield 246	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:isCancelled	Z
    //   1074: ifeq +24 -> 1098
    //   1077: ldc 53
    //   1079: ldc_w 379
    //   1082: aload_1
    //   1083: getfield 382	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:sampleSize	I
    //   1086: invokestatic 260	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1089: ldc_w 402
    //   1092: invokestatic 405	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   1095: goto -969 -> 126
    //   1098: aload 9
    //   1100: ifnonnull +86 -> 1186
    //   1103: ldc 53
    //   1105: new 351	java/lang/StringBuilder
    //   1108: dup
    //   1109: invokespecial 352	java/lang/StringBuilder:<init>	()V
    //   1112: ldc_w 501
    //   1115: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1118: iload 5
    //   1120: invokevirtual 504	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1123: ldc_w 506
    //   1126: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1129: iload 6
    //   1131: invokevirtual 504	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1134: ldc_w 508
    //   1137: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1140: aload_0
    //   1141: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   1144: invokevirtual 361	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1147: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1150: invokestatic 368	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   1153: aload_0
    //   1154: getfield 303	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Handler	Landroid/os/Handler;
    //   1157: sipush 10010
    //   1160: iload 5
    //   1162: iload 6
    //   1164: iconst_2
    //   1165: anewarray 240	java/lang/Object
    //   1168: dup
    //   1169: iconst_0
    //   1170: aload_1
    //   1171: aastore
    //   1172: dup
    //   1173: iconst_1
    //   1174: aload 9
    //   1176: aastore
    //   1177: invokestatic 309	android/os/Message:obtain	(Landroid/os/Handler;IIILjava/lang/Object;)Landroid/os/Message;
    //   1180: invokevirtual 312	android/os/Message:sendToTarget	()V
    //   1183: goto -1082 -> 101
    //   1186: aload_1
    //   1187: getfield 382	com/oneplus/drawable/ProgressiveBitmapDrawable$BitmapTilesDecodingTask:sampleSize	I
    //   1190: iconst_1
    //   1191: if_icmpne -38 -> 1153
    //   1194: aload_0
    //   1195: getfield 186	com/oneplus/drawable/ProgressiveBitmapDrawable:m_OriginalTilesCache	Lcom/oneplus/cache/LruCache;
    //   1198: aload 14
    //   1200: aload 9
    //   1202: invokevirtual 512	com/oneplus/cache/LruCache:add	(Ljava/lang/Object;Ljava/lang/Object;)Z
    //   1205: pop
    //   1206: goto -53 -> 1153
    //   1209: astore 10
    //   1211: aload 11
    //   1213: astore 8
    //   1215: goto -372 -> 843
    //   1218: astore 10
    //   1220: aload 11
    //   1222: astore 8
    //   1224: goto -576 -> 648
    //   1227: astore 9
    //   1229: aload 8
    //   1231: ifnonnull -557 -> 674
    //   1234: aload 9
    //   1236: astore 11
    //   1238: goto -572 -> 666
    //   1241: astore 9
    //   1243: aload 8
    //   1245: ifnonnull -376 -> 869
    //   1248: aload 9
    //   1250: astore 11
    //   1252: goto -391 -> 861
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1255	0	this	ProgressiveBitmapDrawable
    //   0	1255	1	paramBitmapTilesDecodingTask	BitmapTilesDecodingTask
    //   85	863	2	i	int
    //   90	242	3	j	int
    //   99	868	4	k	int
    //   210	951	5	m	int
    //   217	946	6	n	int
    //   516	196	7	bool	boolean
    //   152	450	8	localObject1	Object
    //   604	13	8	localThrowable1	Throwable
    //   624	13	8	localThrowable2	Throwable
    //   642	155	8	localObject2	Object
    //   799	13	8	localThrowable3	Throwable
    //   819	13	8	localThrowable4	Throwable
    //   837	407	8	localObject3	Object
    //   255	134	9	localObject4	Object
    //   423	8	9	localThrowable5	Throwable
    //   530	671	9	localObject5	Object
    //   1227	8	9	localThrowable6	Throwable
    //   1241	8	9	localThrowable7	Throwable
    //   339	249	10	localObject6	Object
    //   629	12	10	localThrowable8	Throwable
    //   646	137	10	localObject7	Object
    //   824	12	10	localThrowable9	Throwable
    //   841	54	10	localObject8	Object
    //   1209	1	10	localObject9	Object
    //   1218	1	10	localObject10	Object
    //   524	1	11	localObject11	Object
    //   634	11	11	localObject12	Object
    //   650	68	11	localObject13	Object
    //   829	11	11	localObject14	Object
    //   845	406	11	localObject15	Object
    //   527	261	12	localObject16	Object
    //   30	352	13	localOptions	BitmapFactory.Options
    //   169	1030	14	localInteger	Integer
    // Exception table:
    //   from	to	target	type
    //   23	81	189	java/lang/Throwable
    //   81	91	189	java/lang/Throwable
    //   91	101	189	java/lang/Throwable
    //   101	126	189	java/lang/Throwable
    //   135	145	189	java/lang/Throwable
    //   148	157	189	java/lang/Throwable
    //   183	186	189	java/lang/Throwable
    //   227	230	189	java/lang/Throwable
    //   233	257	189	java/lang/Throwable
    //   266	296	189	java/lang/Throwable
    //   300	305	189	java/lang/Throwable
    //   305	344	189	java/lang/Throwable
    //   369	372	189	java/lang/Throwable
    //   392	395	189	java/lang/Throwable
    //   395	420	189	java/lang/Throwable
    //   439	444	189	java/lang/Throwable
    //   444	493	189	java/lang/Throwable
    //   493	508	189	java/lang/Throwable
    //   509	518	189	java/lang/Throwable
    //   606	621	189	java/lang/Throwable
    //   702	711	189	java/lang/Throwable
    //   801	816	189	java/lang/Throwable
    //   897	922	189	java/lang/Throwable
    //   925	1014	189	java/lang/Throwable
    //   1021	1070	189	java/lang/Throwable
    //   1070	1095	189	java/lang/Throwable
    //   1103	1153	189	java/lang/Throwable
    //   1153	1183	189	java/lang/Throwable
    //   1186	1206	189	java/lang/Throwable
    //   157	171	299	finally
    //   176	183	299	finally
    //   202	219	299	finally
    //   375	388	423	java/lang/Throwable
    //   344	369	438	finally
    //   375	388	438	finally
    //   425	435	438	finally
    //   601	604	604	java/lang/Throwable
    //   671	674	604	java/lang/Throwable
    //   685	692	604	java/lang/Throwable
    //   699	702	604	java/lang/Throwable
    //   587	592	624	java/lang/Throwable
    //   535	551	629	java/lang/Throwable
    //   559	578	629	java/lang/Throwable
    //   631	634	634	finally
    //   796	799	799	java/lang/Throwable
    //   866	869	799	java/lang/Throwable
    //   880	887	799	java/lang/Throwable
    //   894	897	799	java/lang/Throwable
    //   782	787	819	java/lang/Throwable
    //   728	746	824	java/lang/Throwable
    //   754	773	824	java/lang/Throwable
    //   826	829	829	finally
    //   728	746	1209	finally
    //   754	773	1209	finally
    //   535	551	1218	finally
    //   559	578	1218	finally
    //   657	662	1227	java/lang/Throwable
    //   852	857	1241	java/lang/Throwable
  }
  
  private void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    }
    for (;;)
    {
      return;
      onBitmapDecoderReady((BitmapRegionDecoder[])paramMessage.obj);
      return;
      invalidateSelf();
      return;
      Object[] arrayOfObject = (Object[])paramMessage.obj;
      onBitmapInfoUpdated(paramMessage.arg1, paramMessage.arg2, ((Integer)arrayOfObject[0]).intValue());
      return;
      arrayOfObject = (Object[])paramMessage.obj;
      if (this.m_BitmapTilesDecodingTasks != null)
      {
        BitmapTilesDecodingTask[] arrayOfBitmapTilesDecodingTask = this.m_BitmapTilesDecodingTasks;
        int j = arrayOfBitmapTilesDecodingTask.length;
        int i = 0;
        while (i < j)
        {
          if (arrayOfBitmapTilesDecodingTask[i] == arrayOfObject[0])
          {
            onBitmapTileUpdated(paramMessage.arg1, paramMessage.arg2, (Bitmap)arrayOfObject[1]);
            return;
          }
          i += 1;
        }
      }
    }
  }
  
  private void initialize(Object paramObject, Bitmap.Config paramConfig, Bitmap paramBitmap)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("No bitmap source.");
    }
    this.m_Source = paramObject;
    this.m_BitmapConfig = paramConfig;
    this.m_ThumbnailBitmap = paramBitmap;
    this.m_Paint = new Paint();
    this.m_Paint.setFilterBitmap(true);
    this.m_Handler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        ProgressiveBitmapDrawable.-wrap2(ProgressiveBitmapDrawable.this, paramAnonymousMessage);
      }
    };
  }
  
  /* Error */
  private void initializeBitmapDecoder()
  {
    // Byte code:
    //   0: ldc 53
    //   2: ldc_w 560
    //   5: invokestatic 409	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   8: invokestatic 566	android/os/SystemClock:elapsedRealtime	()J
    //   11: lstore_2
    //   12: iconst_2
    //   13: anewarray 440	android/graphics/BitmapRegionDecoder
    //   16: astore 9
    //   18: aload_0
    //   19: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   22: instanceof 264
    //   25: ifeq +32 -> 57
    //   28: iconst_0
    //   29: istore_1
    //   30: iload_1
    //   31: iconst_2
    //   32: if_icmpge +148 -> 180
    //   35: aload 9
    //   37: iload_1
    //   38: aload_0
    //   39: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   42: checkcast 264	java/lang/String
    //   45: iconst_1
    //   46: invokestatic 570	android/graphics/BitmapRegionDecoder:newInstance	(Ljava/lang/String;Z)Landroid/graphics/BitmapRegionDecoder;
    //   49: aastore
    //   50: iload_1
    //   51: iconst_1
    //   52: iadd
    //   53: istore_1
    //   54: goto -24 -> 30
    //   57: aload_0
    //   58: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   61: instanceof 326
    //   64: istore 4
    //   66: iload 4
    //   68: ifeq +244 -> 312
    //   71: iconst_0
    //   72: istore_1
    //   73: iload_1
    //   74: iconst_2
    //   75: if_icmpge +105 -> 180
    //   78: aconst_null
    //   79: astore 6
    //   81: aconst_null
    //   82: astore 5
    //   84: aload_0
    //   85: getfield 198	com/oneplus/drawable/ProgressiveBitmapDrawable:m_ContentResolver	Landroid/content/ContentResolver;
    //   88: aload_0
    //   89: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   92: checkcast 326	android/net/Uri
    //   95: invokevirtual 332	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   98: astore 7
    //   100: aload 7
    //   102: astore 5
    //   104: aload 7
    //   106: astore 6
    //   108: aload 9
    //   110: iload_1
    //   111: aload 7
    //   113: iconst_1
    //   114: invokestatic 573	android/graphics/BitmapRegionDecoder:newInstance	(Ljava/io/InputStream;Z)Landroid/graphics/BitmapRegionDecoder;
    //   117: aastore
    //   118: aload 7
    //   120: ifnull +8 -> 128
    //   123: aload 7
    //   125: invokevirtual 340	java/io/InputStream:close	()V
    //   128: aconst_null
    //   129: astore 5
    //   131: aload 5
    //   133: ifnull +412 -> 545
    //   136: aload 5
    //   138: athrow
    //   139: astore 5
    //   141: ldc 53
    //   143: new 351	java/lang/StringBuilder
    //   146: dup
    //   147: invokespecial 352	java/lang/StringBuilder:<init>	()V
    //   150: ldc_w 575
    //   153: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   156: aload_0
    //   157: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   160: invokevirtual 361	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   163: ldc_w 577
    //   166: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   172: invokestatic 409	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   175: aload_0
    //   176: iconst_1
    //   177: putfield 579	com/oneplus/drawable/ProgressiveBitmapDrawable:m_UseFullSizeBitmap	Z
    //   180: ldc 53
    //   182: new 351	java/lang/StringBuilder
    //   185: dup
    //   186: invokespecial 352	java/lang/StringBuilder:<init>	()V
    //   189: ldc_w 581
    //   192: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   195: invokestatic 566	android/os/SystemClock:elapsedRealtime	()J
    //   198: lload_2
    //   199: lsub
    //   200: invokevirtual 584	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   203: ldc_w 586
    //   206: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   209: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   212: invokestatic 409	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   215: aload_0
    //   216: getfield 303	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Handler	Landroid/os/Handler;
    //   219: astore 6
    //   221: aload_0
    //   222: getfield 579	com/oneplus/drawable/ProgressiveBitmapDrawable:m_UseFullSizeBitmap	Z
    //   225: ifne +268 -> 493
    //   228: aload 9
    //   230: astore 5
    //   232: aload 6
    //   234: sipush 10001
    //   237: aload 5
    //   239: invokestatic 589	android/os/Message:obtain	(Landroid/os/Handler;ILjava/lang/Object;)Landroid/os/Message;
    //   242: invokevirtual 312	android/os/Message:sendToTarget	()V
    //   245: return
    //   246: astore 5
    //   248: goto -117 -> 131
    //   251: astore 6
    //   253: aload 6
    //   255: athrow
    //   256: astore 7
    //   258: aload 6
    //   260: astore 8
    //   262: aload 5
    //   264: ifnull +12 -> 276
    //   267: aload 5
    //   269: invokevirtual 340	java/io/InputStream:close	()V
    //   272: aload 6
    //   274: astore 8
    //   276: aload 8
    //   278: ifnull +31 -> 309
    //   281: aload 8
    //   283: athrow
    //   284: aload 6
    //   286: astore 8
    //   288: aload 6
    //   290: aload 5
    //   292: if_acmpeq -16 -> 276
    //   295: aload 6
    //   297: aload 5
    //   299: invokevirtual 344	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   302: aload 6
    //   304: astore 8
    //   306: goto -30 -> 276
    //   309: aload 7
    //   311: athrow
    //   312: aload_0
    //   313: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   316: instanceof 346
    //   319: istore 4
    //   321: iload 4
    //   323: ifeq +139 -> 462
    //   326: iconst_0
    //   327: istore_1
    //   328: iload_1
    //   329: iconst_2
    //   330: if_icmpge -150 -> 180
    //   333: aconst_null
    //   334: astore 6
    //   336: aconst_null
    //   337: astore 5
    //   339: aload_0
    //   340: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   343: checkcast 346	com/oneplus/io/StreamSource
    //   346: aload_0
    //   347: getfield 163	com/oneplus/drawable/ProgressiveBitmapDrawable:m_CancelStreamOpeningRef	Lcom/oneplus/base/Ref;
    //   350: invokeinterface 349 2 0
    //   355: astore 7
    //   357: aload 7
    //   359: astore 5
    //   361: aload 7
    //   363: astore 6
    //   365: aload 9
    //   367: iload_1
    //   368: aload 7
    //   370: iconst_1
    //   371: invokestatic 573	android/graphics/BitmapRegionDecoder:newInstance	(Ljava/io/InputStream;Z)Landroid/graphics/BitmapRegionDecoder;
    //   374: aastore
    //   375: aload 7
    //   377: ifnull +8 -> 385
    //   380: aload 7
    //   382: invokevirtual 340	java/io/InputStream:close	()V
    //   385: aconst_null
    //   386: astore 5
    //   388: aload 5
    //   390: ifnull +176 -> 566
    //   393: aload 5
    //   395: athrow
    //   396: astore 5
    //   398: goto -10 -> 388
    //   401: astore 6
    //   403: aload 6
    //   405: athrow
    //   406: astore 7
    //   408: aload 6
    //   410: astore 8
    //   412: aload 5
    //   414: ifnull +12 -> 426
    //   417: aload 5
    //   419: invokevirtual 340	java/io/InputStream:close	()V
    //   422: aload 6
    //   424: astore 8
    //   426: aload 8
    //   428: ifnull +31 -> 459
    //   431: aload 8
    //   433: athrow
    //   434: aload 6
    //   436: astore 8
    //   438: aload 6
    //   440: aload 5
    //   442: if_acmpeq -16 -> 426
    //   445: aload 6
    //   447: aload 5
    //   449: invokevirtual 344	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   452: aload 6
    //   454: astore 8
    //   456: goto -30 -> 426
    //   459: aload 7
    //   461: athrow
    //   462: ldc 53
    //   464: new 351	java/lang/StringBuilder
    //   467: dup
    //   468: invokespecial 352	java/lang/StringBuilder:<init>	()V
    //   471: ldc_w 591
    //   474: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   477: aload_0
    //   478: getfield 262	com/oneplus/drawable/ProgressiveBitmapDrawable:m_Source	Ljava/lang/Object;
    //   481: invokevirtual 361	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   484: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   487: invokestatic 368	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   490: goto -310 -> 180
    //   493: aconst_null
    //   494: astore 5
    //   496: goto -264 -> 232
    //   499: astore 7
    //   501: aconst_null
    //   502: astore 8
    //   504: aload 6
    //   506: astore 5
    //   508: aload 8
    //   510: astore 6
    //   512: goto -104 -> 408
    //   515: astore 7
    //   517: aconst_null
    //   518: astore 8
    //   520: aload 6
    //   522: astore 5
    //   524: aload 8
    //   526: astore 6
    //   528: goto -270 -> 258
    //   531: astore 5
    //   533: aload 6
    //   535: ifnonnull -251 -> 284
    //   538: aload 5
    //   540: astore 8
    //   542: goto -266 -> 276
    //   545: iload_1
    //   546: iconst_1
    //   547: iadd
    //   548: istore_1
    //   549: goto -476 -> 73
    //   552: astore 5
    //   554: aload 6
    //   556: ifnonnull -122 -> 434
    //   559: aload 5
    //   561: astore 8
    //   563: goto -137 -> 426
    //   566: iload_1
    //   567: iconst_1
    //   568: iadd
    //   569: istore_1
    //   570: goto -242 -> 328
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	573	0	this	ProgressiveBitmapDrawable
    //   29	541	1	i	int
    //   11	188	2	l	long
    //   64	258	4	bool	boolean
    //   82	55	5	localObject1	Object
    //   139	1	5	localThrowable1	Throwable
    //   230	8	5	arrayOfBitmapRegionDecoder1	BitmapRegionDecoder[]
    //   246	52	5	localThrowable2	Throwable
    //   337	57	5	localObject2	Object
    //   396	52	5	localThrowable3	Throwable
    //   494	29	5	localObject3	Object
    //   531	8	5	localThrowable4	Throwable
    //   552	8	5	localThrowable5	Throwable
    //   79	154	6	localObject4	Object
    //   251	52	6	localThrowable6	Throwable
    //   334	30	6	localObject5	Object
    //   401	104	6	localThrowable7	Throwable
    //   510	45	6	localObject6	Object
    //   98	26	7	localInputStream1	InputStream
    //   256	54	7	localObject7	Object
    //   355	26	7	localInputStream2	InputStream
    //   406	54	7	localObject8	Object
    //   499	1	7	localObject9	Object
    //   515	1	7	localObject10	Object
    //   260	302	8	localThrowable8	Throwable
    //   16	350	9	arrayOfBitmapRegionDecoder2	BitmapRegionDecoder[]
    // Exception table:
    //   from	to	target	type
    //   18	28	139	java/lang/Throwable
    //   35	50	139	java/lang/Throwable
    //   57	66	139	java/lang/Throwable
    //   136	139	139	java/lang/Throwable
    //   281	284	139	java/lang/Throwable
    //   295	302	139	java/lang/Throwable
    //   309	312	139	java/lang/Throwable
    //   312	321	139	java/lang/Throwable
    //   393	396	139	java/lang/Throwable
    //   431	434	139	java/lang/Throwable
    //   445	452	139	java/lang/Throwable
    //   459	462	139	java/lang/Throwable
    //   462	490	139	java/lang/Throwable
    //   123	128	246	java/lang/Throwable
    //   84	100	251	java/lang/Throwable
    //   108	118	251	java/lang/Throwable
    //   253	256	256	finally
    //   380	385	396	java/lang/Throwable
    //   339	357	401	java/lang/Throwable
    //   365	375	401	java/lang/Throwable
    //   403	406	406	finally
    //   339	357	499	finally
    //   365	375	499	finally
    //   84	100	515	finally
    //   108	118	515	finally
    //   267	272	531	java/lang/Throwable
    //   417	422	552	java/lang/Throwable
  }
  
  private boolean isSameSource(Object paramObject)
  {
    return this.m_Source.equals(paramObject);
  }
  
  private void onBitmapDecoderReady(BitmapRegionDecoder[] paramArrayOfBitmapRegionDecoder)
  {
    if (this.m_IsReleased)
    {
      if (paramArrayOfBitmapRegionDecoder != null)
      {
        int i = 0;
        int j = paramArrayOfBitmapRegionDecoder.length;
        while (i < j)
        {
          BitmapRegionDecoder localBitmapRegionDecoder = paramArrayOfBitmapRegionDecoder[i];
          if (localBitmapRegionDecoder != null) {
            localBitmapRegionDecoder.recycle();
          }
          i += 1;
        }
      }
      return;
    }
    this.m_BitmapDecoders = paramArrayOfBitmapRegionDecoder;
    this.m_IsBitmapDecoderReady = true;
    invalidateSelf();
  }
  
  private void onBitmapInfoUpdated(int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.m_IsReleased) {
      return;
    }
    this.m_OriginalWidth = paramInt1;
    this.m_OriginalHeight = paramInt2;
    this.m_Orientation = paramInt3;
    cancelDecodingBitmapTiles();
    invalidateSelf();
  }
  
  private void onBitmapTileUpdated(int paramInt1, int paramInt2, Bitmap paramBitmap)
  {
    boolean bool = false;
    Tile localTile;
    if ((this.m_TileUsageQueue != null) && (this.m_TileUsageQueue.contains(Integer.valueOf(paramInt1 << 16 | paramInt2))))
    {
      localTile = this.m_Tiles[paramInt2][paramInt1];
      if (localTile.bitmap != null) {
        break label135;
      }
    }
    label135:
    for (int i = 1;; i = 0)
    {
      localTile.bitmap = paramBitmap;
      localTile.isDecoding = false;
      if (paramBitmap != null) {
        bool = true;
      }
      localTile.isValid = bool;
      if ((!localTile.isValid) || (paramInt1 < this.m_StartTileX) || (paramInt1 > this.m_EndTileX) || (paramInt2 < this.m_StartTileY) || (paramInt2 > this.m_EndTileY)) {
        break;
      }
      if (i != 0) {
        localTile.fadeInAnimationStartTime = SystemClock.elapsedRealtime();
      }
      invalidateSelf();
      return;
      return;
    }
    localTile.fadeInAnimationStartTime = 0L;
  }
  
  private void startDecodingBitmapTiles()
  {
    cancelDecodingBitmapTiles();
    if ((this.m_IsReleased) || (this.m_OriginalWidth <= 0)) {}
    while (this.m_OriginalHeight <= 0) {
      return;
    }
    int k = (int)Math.ceil(this.m_OriginalWidth / this.m_SampleSize / 1024.0D);
    int m = (int)Math.ceil(this.m_OriginalHeight / this.m_SampleSize / 1024.0D);
    this.m_TileDecodingQueue = new ConcurrentLinkedQueue();
    this.m_TileUsageQueue = new LinkedList();
    this.m_Tiles = new Tile[m][];
    int i = 0;
    while (i < m)
    {
      Tile[] arrayOfTile = new Tile[k];
      this.m_Tiles[i] = arrayOfTile;
      int j = k - 1;
      while (j >= 0)
      {
        arrayOfTile[j] = new Tile(null);
        j -= 1;
      }
      i += 1;
    }
    if (!this.m_UseFullSizeBitmap)
    {
      this.m_BitmapTilesDecodingTasks = new BitmapTilesDecodingTask[2];
      i = 0;
      while (i < 2)
      {
        this.m_BitmapTilesDecodingTasks[i] = new BitmapTilesDecodingTask(this.m_BitmapDecoders[i], this.m_SampleSize, this.m_TileDecodingQueue);
        m_DecodingExecutors.execute(this.m_BitmapTilesDecodingTasks[i]);
        i += 1;
      }
    }
    this.m_BitmapTilesDecodingTasks = new BitmapTilesDecodingTask[] { new BitmapTilesDecodingTask(null, this.m_SampleSize, this.m_TileDecodingQueue) };
    m_DecodingExecutors.execute(this.m_BitmapTilesDecodingTasks[0]);
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.m_IsReleased) {
      return;
    }
    ??? = paramCanvas.getClipBounds();
    Rect localRect = getBounds();
    this.m_DrawingBounds.set(localRect);
    this.m_DrawingBounds.left = Math.max(this.m_DrawingBounds.left, ((Rect)???).left);
    this.m_DrawingBounds.top = Math.max(this.m_DrawingBounds.top, ((Rect)???).top);
    this.m_DrawingBounds.right = Math.min(this.m_DrawingBounds.right, ((Rect)???).right);
    this.m_DrawingBounds.bottom = Math.min(this.m_DrawingBounds.bottom, ((Rect)???).bottom);
    if (this.m_DrawingBounds.isEmpty()) {
      return;
    }
    float f1 = (this.m_DrawingBounds.left - localRect.left) / localRect.width();
    float f2 = (this.m_DrawingBounds.top - localRect.top) / localRect.height();
    float f3 = (this.m_DrawingBounds.right - localRect.left) / localRect.width();
    float f4 = (this.m_DrawingBounds.bottom - localRect.top) / localRect.height();
    if (this.m_ThumbnailBitmap != null)
    {
      this.m_SrcDrawingBounds.set(Math.round(this.m_ThumbnailBitmap.getWidth() * f1), Math.round(this.m_ThumbnailBitmap.getHeight() * f2), Math.round(this.m_ThumbnailBitmap.getWidth() * f3), Math.round(this.m_ThumbnailBitmap.getHeight() * f4));
      paramCanvas.drawBitmap(this.m_ThumbnailBitmap, this.m_SrcDrawingBounds, this.m_DrawingBounds, this.m_Paint);
    }
    int i = 0;
    if (this.m_BitmapInfoDecodingTask == null)
    {
      this.m_BitmapInfoDecodingTask = new BitmapInfoDecodingTask(null);
      m_DecodingExecutors.execute(this.m_BitmapInfoDecodingTask);
      i = 1;
    }
    if (!this.m_IsBitmapDecoderReady)
    {
      if (this.m_BitmapDecoderInitTask == null)
      {
        this.m_BitmapDecoderInitTask = new BitmapDecoderInitTask(null);
        m_DecodingExecutors.execute(this.m_BitmapDecoderInitTask);
      }
      i = 1;
    }
    if (i != 0) {
      return;
    }
    if ((!this.m_IsHighQualityBitmapEnabled) || (this.m_OriginalWidth <= 0)) {}
    while (this.m_OriginalHeight <= 0) {
      return;
    }
    i = 1;
    if (!this.m_UseFullSizeBitmap) {
      i = ImageUtils.calculateSampleSize(this.m_OriginalWidth, this.m_OriginalHeight, localRect.width(), localRect.height());
    }
    if (this.m_SampleSize != i)
    {
      Log.v("ProgressiveBitmapDrawable", "draw() - Change sample size from ", Integer.valueOf(this.m_SampleSize), " to ", Integer.valueOf(i));
      cancelDecodingBitmapTiles();
      this.m_SampleSize = i;
    }
    if (this.m_BitmapTilesDecodingTasks == null) {
      startDecodingBitmapTiles();
    }
    int i1 = this.m_OriginalWidth / i;
    int i2 = this.m_OriginalHeight / i;
    this.m_SrcDrawingBounds.set(Math.round(i1 * f1), Math.round(i2 * f2), Math.round(i1 * f3), Math.round(i2 * f4));
    int j = (int)Math.ceil(i1 / 1024.0F) - 1;
    i = (int)Math.ceil(i2 / 1024.0F) - 1;
    this.m_StartTileX = ((int)Math.floor(this.m_SrcDrawingBounds.left / 1024));
    this.m_StartTileY = ((int)Math.floor(this.m_SrcDrawingBounds.top / 1024));
    this.m_EndTileX = Math.min(j, (int)Math.ceil(this.m_SrcDrawingBounds.right / 1024));
    this.m_EndTileY = Math.min(i, (int)Math.ceil(this.m_SrcDrawingBounds.bottom / 1024));
    int i4 = Math.max(0, this.m_StartTileX - 2);
    int i5 = Math.max(0, this.m_StartTileY - 2);
    int m = Math.min(j, this.m_EndTileX + 2);
    i = Math.min(i, this.m_EndTileY + 2);
    int i3 = Math.max((m - i4 + 1) * (i - i5 + 1), 64);
    label789:
    label868:
    int k;
    while (i >= i5)
    {
      j = m;
      if (j >= i4) {
        if ((i < this.m_StartTileY) || (i > this.m_EndTileY))
        {
          ??? = Integer.valueOf(j << 16 | i);
          ??? = this.m_Tiles[i][j];
          if ((((Tile)???).isValid) && (((Tile)???).bitmap == null) && (!((Tile)???).isDecoding)) {
            break label868;
          }
        }
      }
      for (;;)
      {
        j -= 1;
        break;
        if (j < this.m_StartTileX) {
          break label789;
        }
        if (j > this.m_EndTileX)
        {
          break label789;
          int n = 1;
          k = n;
          if (!this.m_TileUsageQueue.remove(???))
          {
            k = n;
            if (this.m_TileUsageQueue.size() >= i3)
            {
              int i6 = ((Integer)this.m_TileUsageQueue.getLast()).intValue();
              k = i6 >>> 16;
              i6 &= 0xFFFF;
              if ((i6 < this.m_StartTileY) || (i6 > this.m_EndTileY)) {}
              while ((k < this.m_StartTileX) || (k > this.m_EndTileX))
              {
                this.m_TileUsageQueue.removeLast();
                this.m_Tiles[i6][k].reset();
                break;
              }
              k = 0;
            }
          }
          if (k != 0)
          {
            this.m_TileUsageQueue.addFirst(???);
            synchronized (this.m_TileDecodingQueue)
            {
              this.m_TileDecodingQueue.add(???);
              this.m_TileDecodingQueue.notifyAll();
              ((Tile)???).isDecoding = true;
            }
          }
        }
      }
    }
    i = 0;
    this.m_SrcDrawingBounds.offsetTo(0, 0);
    j = this.m_EndTileY;
    while (j >= this.m_StartTileY)
    {
      k = this.m_EndTileX;
      if (k >= this.m_StartTileX)
      {
        ??? = Integer.valueOf(k << 16 | j);
        ??? = this.m_Tiles[j][k];
        ??? = ((Tile)???).bitmap;
        if (!((Tile)???).isDecoding) {
          if (??? != null) {
            if (((Tile)???).isValid) {}
          }
        }
        for (;;)
        {
          k -= 1;
          break;
          this.m_SrcDrawingBounds.right = ((Bitmap)???).getWidth();
          this.m_SrcDrawingBounds.bottom = ((Bitmap)???).getHeight();
          this.m_DestDrawingBounds.set(localRect.left + (int)(k * 1024.0F / i1 * localRect.width()), localRect.top + (int)(j * 1024.0F / i2 * localRect.height()), localRect.left + (int)((k + 1) * 1024.0F / i1 * localRect.width()), localRect.top + (int)((j + 1) * 1024.0F / i2 * localRect.height()));
          this.m_DestDrawingBounds.right = Math.min(this.m_DestDrawingBounds.right, localRect.right);
          this.m_DestDrawingBounds.bottom = Math.min(this.m_DestDrawingBounds.bottom, localRect.bottom);
          if (((Tile)???).fadeInAnimationStartTime > 0L)
          {
            f2 = (float)(SystemClock.elapsedRealtime() - ((Tile)???).fadeInAnimationStartTime) / 200.0F;
            f1 = f2;
            if (f2 >= 1.0F) {
              f1 = NaN.0F;
            }
            if (Float.isNaN(f1)) {
              break label1521;
            }
            i = this.m_Paint.getAlpha();
            this.m_Paint.setAlpha(Math.round(i * f1));
            paramCanvas.drawBitmap((Bitmap)???, this.m_SrcDrawingBounds, this.m_DestDrawingBounds, this.m_Paint);
            this.m_Paint.setAlpha(i);
            i = 1;
          }
          for (;;)
          {
            label1452:
            if (!this.m_TileUsageQueue.remove(???)) {
              for (;;)
              {
                if (this.m_TileUsageQueue.size() >= i3)
                {
                  m = ((Integer)this.m_TileUsageQueue.removeLast()).intValue();
                  this.m_Tiles[(m & 0xFFFF)][(m >>> 16)].reset();
                  continue;
                  f1 = NaN.0F;
                  break;
                  label1521:
                  paramCanvas.drawBitmap((Bitmap)???, this.m_SrcDrawingBounds, this.m_DestDrawingBounds, this.m_Paint);
                  ((Tile)???).fadeInAnimationStartTime = 0L;
                  break label1452;
                  synchronized (this.m_TileDecodingQueue)
                  {
                    this.m_TileDecodingQueue.add(???);
                    this.m_TileDecodingQueue.notifyAll();
                    ((Tile)???).isDecoding = true;
                  }
                  synchronized (this.m_TileDecodingQueue)
                  {
                    this.m_TileDecodingQueue.remove(???);
                    this.m_TileDecodingQueue.add(???);
                  }
                }
              }
            }
          }
          this.m_TileUsageQueue.addFirst(???);
        }
      }
      j -= 1;
    }
    if (i != 0) {
      scheduleSelf(this.m_InvalidateRunnable, SystemClock.uptimeMillis() + 50L);
    }
  }
  
  public int getIntrinsicHeight()
  {
    if (this.m_OriginalHeight > 0) {
      return this.m_OriginalHeight;
    }
    if (this.m_ThumbnailBitmap != null) {
      return this.m_ThumbnailBitmap.getHeight();
    }
    return 0;
  }
  
  public int getIntrinsicWidth()
  {
    if (this.m_OriginalWidth > 0) {
      return this.m_OriginalWidth;
    }
    if (this.m_ThumbnailBitmap != null) {
      return this.m_ThumbnailBitmap.getWidth();
    }
    return 0;
  }
  
  public int getOpacity()
  {
    return this.m_Paint.getAlpha();
  }
  
  public boolean isSameSource(Uri paramUri)
  {
    return isSameSource(paramUri);
  }
  
  public boolean isSameSource(String paramString)
  {
    return isSameSource(paramString);
  }
  
  public void release()
  {
    if (this.m_Handler.getLooper().getThread() != Thread.currentThread()) {
      throw new RuntimeException("Cannot called from another thread.");
    }
    if (this.m_IsReleased) {
      return;
    }
    this.m_IsReleased = true;
    this.m_ThumbnailBitmap = null;
    this.m_FullSizeBitmap = null;
    this.m_CancelStreamOpeningRef.set(Boolean.valueOf(true));
    cancelDecodingBitmapTiles();
    if (this.m_BitmapDecoders != null)
    {
      Log.v("ProgressiveBitmapDrawable", "release() - Release decoder");
      BitmapRegionDecoder[] arrayOfBitmapRegionDecoder = this.m_BitmapDecoders;
      int i = 0;
      int j = arrayOfBitmapRegionDecoder.length;
      while (i < j)
      {
        final BitmapRegionDecoder localBitmapRegionDecoder = arrayOfBitmapRegionDecoder[i];
        if (localBitmapRegionDecoder != null) {
          m_ReleaseExecutors.execute(new Runnable()
          {
            public void run()
            {
              synchronized (localBitmapRegionDecoder)
              {
                localBitmapRegionDecoder.recycle();
                return;
              }
            }
          });
        }
        i += 1;
      }
      this.m_BitmapDecoders = null;
    }
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_Paint.setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.m_Paint.setColorFilter(paramColorFilter);
  }
  
  public void setHighQualityBitmapEnabled(boolean paramBoolean)
  {
    if (this.m_IsHighQualityBitmapEnabled != paramBoolean)
    {
      Log.v("ProgressiveBitmapDrawable", "setHighQualityBitmapEnabled() - ", Boolean.valueOf(paramBoolean));
      this.m_IsHighQualityBitmapEnabled = paramBoolean;
      if (paramBoolean) {
        invalidateSelf();
      }
    }
  }
  
  public void setThumbnailBitmap(Bitmap paramBitmap)
  {
    this.m_ThumbnailBitmap = paramBitmap;
    invalidateSelf();
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (super.setVisible(paramBoolean1, paramBoolean2))
    {
      if (!paramBoolean1) {
        cancelDecodingBitmapTiles();
      }
      return true;
    }
    return false;
  }
  
  private final class BitmapDecoderInitTask
    implements Runnable
  {
    private BitmapDecoderInitTask() {}
    
    public void run()
    {
      ProgressiveBitmapDrawable.-wrap3(ProgressiveBitmapDrawable.this);
    }
  }
  
  private final class BitmapInfoDecodingTask
    implements Runnable
  {
    private BitmapInfoDecodingTask() {}
    
    public void run()
    {
      ProgressiveBitmapDrawable.-wrap0(ProgressiveBitmapDrawable.this);
    }
  }
  
  private final class BitmapTilesDecodingTask
    implements Runnable
  {
    public final BitmapRegionDecoder decoder;
    public final Queue<Integer> decodingQueue;
    public volatile boolean isCancelled;
    public final int sampleSize;
    
    public BitmapTilesDecodingTask(int paramInt, Queue<Integer> paramQueue)
    {
      this.decoder = paramInt;
      this.sampleSize = paramQueue;
      Queue localQueue;
      this.decodingQueue = localQueue;
    }
    
    public void run()
    {
      ProgressiveBitmapDrawable.-wrap1(ProgressiveBitmapDrawable.this, this);
    }
  }
  
  private static final class Tile
  {
    public volatile Bitmap bitmap;
    public volatile long fadeInAnimationStartTime;
    public volatile boolean isDecoding;
    public volatile boolean isValid = true;
    
    public void reset()
    {
      this.bitmap = null;
      this.isDecoding = false;
      this.isValid = true;
      this.fadeInAnimationStartTime = 0L;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/drawable/ProgressiveBitmapDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */