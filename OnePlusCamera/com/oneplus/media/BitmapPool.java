package com.oneplus.media;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.io.FileUtils;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class BitmapPool
{
  public static final BitmapPool DEFAULT_THUMBNAIL = new BitmapPool("ThumbnailBitmapPool", 67108864L, 16777216L, Bitmap.Config.ARGB_8888, 1, 0);
  public static final BitmapPool DEFAULT_THUMBNAIL_SMALL = new BitmapPool("SmallThumbnailBitmapPool", 33554432L, 16777216L, Bitmap.Config.RGB_565, 1, 4);
  private static final long DURATION_PERIODIC_CALLBACK = 100L;
  public static final int FLAG_ASYNC = 1;
  public static final int FLAG_NO_EMBEDDED_THUMB = 16;
  public static final int FLAG_OPAQUE = 8;
  public static final int FLAG_PERIODIC_CALLBACK = 64;
  public static final int FLAG_PREFER_QUALITY_OVER_SPEED = 4;
  public static final int FLAG_URGENT = 2;
  public static final int FLAG_USE_EMBEDDED_THUMB_ONLY = 32;
  private static final long MAX_DECODING_TIME = 3000L;
  public static final int MEDIA_TYPE_PHOTO = 1;
  public static final int MEDIA_TYPE_VIDEO = 3;
  private static final boolean PRINT_TRACE_LOG = false;
  private final String TAG;
  private final Semaphore VIDEO_DECODE_SEMAPHORE = new Semaphore(1);
  private final List<Handle> m_ActivateHandles = new ArrayList();
  private final Bitmap.Config m_BitmapConfig;
  private final Hashtable<Object, BitmapInfo> m_BitmapInfos = new Hashtable();
  private volatile BitmapInfo m_BitmapQueueHead;
  private volatile BitmapInfo m_BitmapQueueTail;
  private final boolean m_CanUseEmbeddedThumbnail;
  private final long m_Capacity;
  private volatile ContentResolver m_ContentResolver;
  private volatile long m_CurrentSize;
  private volatile Executor m_DecodingExecutor;
  private final Runnable m_DecodingRunnable = new Runnable()
  {
    public void run()
    {
      synchronized (BitmapPool.-get1(BitmapPool.this))
      {
        BitmapPool.BitmapInfo localBitmapInfo = (BitmapPool.BitmapInfo)BitmapPool.-get2(BitmapPool.this).pollFirst();
        if (localBitmapInfo != null) {
          BitmapPool.-wrap2(BitmapPool.this, localBitmapInfo);
        }
        return;
      }
    }
  };
  private final int m_DecodingThreadCount;
  private final long m_IdleCapacity;
  private final boolean m_IsPeriodicCallbacksNeeded;
  private final Object m_Lock = new Object();
  private final boolean m_Opaque;
  private final LinkedList<BitmapInfo> m_PendingDecodingQueue = new LinkedList();
  private final List<PeriodicCallbackHandler> m_PeriodicHandlers;
  private final boolean m_PreferQualityOverSpeed;
  private final boolean m_UseEmbeddedThumbnailOnly;
  
  public BitmapPool(String paramString, long paramLong1, long paramLong2, Bitmap.Config paramConfig, int paramInt1, int paramInt2)
  {
    if (paramLong1 < 0L) {
      throw new IllegalArgumentException("Invalid capacity : " + paramLong1 + ".");
    }
    if (paramLong2 < 0L) {
      throw new IllegalArgumentException("Invalid idle capacity : " + paramLong2 + ".");
    }
    if (paramConfig == null) {
      throw new IllegalArgumentException("No bitmap configuration");
    }
    if (paramInt1 <= 0) {
      throw new IllegalArgumentException("Invalid decoding count : " + paramInt1 + ".");
    }
    if (paramString != null)
    {
      this.TAG = paramString;
      this.m_Capacity = paramLong1;
      this.m_IdleCapacity = Math.min(paramLong2, paramLong1);
      if ((paramInt2 & 0x40) == 0) {
        break label357;
      }
      bool1 = true;
      label241:
      this.m_IsPeriodicCallbacksNeeded = bool1;
      this.m_BitmapConfig = paramConfig;
      this.m_DecodingThreadCount = paramInt1;
      if ((paramInt2 & 0x8) == 0) {
        break label363;
      }
      bool1 = true;
      label270:
      this.m_Opaque = bool1;
      if ((paramInt2 & 0x4) == 0) {
        break label369;
      }
      bool1 = true;
      label286:
      this.m_PreferQualityOverSpeed = bool1;
      if ((paramInt2 & 0x10) != 0) {
        break label375;
      }
      bool1 = true;
      label303:
      this.m_CanUseEmbeddedThumbnail = bool1;
      if ((paramInt2 & 0x20) == 0) {
        break label381;
      }
    }
    label357:
    label363:
    label369:
    label375:
    label381:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.m_UseEmbeddedThumbnailOnly = bool1;
      if ((this.m_CanUseEmbeddedThumbnail) || (!this.m_UseEmbeddedThumbnailOnly)) {
        break label387;
      }
      throw new IllegalArgumentException("Cannot use both FLAG_NO_EMBEDDED_THUMB and FLAG_USE_EMBEDDED_THUMB_ONLY flags.");
      paramString = "BitmapPool";
      break;
      bool1 = false;
      break label241;
      bool1 = false;
      break label270;
      bool1 = false;
      break label286;
      bool1 = false;
      break label303;
    }
    label387:
    if (this.m_IsPeriodicCallbacksNeeded)
    {
      this.m_PeriodicHandlers = new ArrayList();
      return;
    }
    this.m_PeriodicHandlers = null;
  }
  
  public BitmapPool(String paramString, long paramLong, Bitmap.Config paramConfig, int paramInt)
  {
    this(paramString, paramLong, paramConfig, paramInt, 0);
  }
  
  public BitmapPool(String paramString, long paramLong, Bitmap.Config paramConfig, int paramInt1, int paramInt2)
  {
    this(paramString, paramLong, paramLong, paramConfig, paramInt1, paramInt2);
  }
  
  private void addToQueue(BitmapInfo paramBitmapInfo)
  {
    if (this.m_BitmapQueueHead != null)
    {
      paramBitmapInfo.next = this.m_BitmapQueueHead;
      this.m_BitmapQueueHead.previous = paramBitmapInfo;
    }
    if (this.m_BitmapQueueTail == null) {
      this.m_BitmapQueueTail = paramBitmapInfo;
    }
    this.m_BitmapQueueHead = paramBitmapInfo;
  }
  
  private void callOnBitmapDecoded(final DecodingHandle paramDecodingHandle, boolean paramBoolean)
  {
    if (paramDecodingHandle.callback == null) {
      return;
    }
    final BitmapInfo localBitmapInfo = paramDecodingHandle.bitmapInfo;
    final Object localObject1;
    if ((localBitmapInfo != null) && (localBitmapInfo.isValid))
    {
      ??? = localBitmapInfo.bitmap;
      localObject1 = ???;
      if (??? != null) {
        if (paramDecodingHandle.maxWidth >= ((Bitmap)???).getWidth())
        {
          localObject1 = ???;
          if (paramDecodingHandle.maxHeight >= ((Bitmap)???).getHeight()) {}
        }
        else
        {
          localObject1 = ImageUtils.createThumbnailImage((Bitmap)???, paramDecodingHandle.maxWidth, paramDecodingHandle.maxHeight);
        }
      }
      ??? = paramDecodingHandle.callbackHandler;
      if ((??? == null) || ((!paramBoolean) && (((Handler)???).getLooper().getThread() == Thread.currentThread()))) {
        break label150;
      }
      localObject1 = new Runnable()
      {
        public void run()
        {
          synchronized (BitmapPool.-get1(BitmapPool.this))
          {
            if (localBitmapInfo.isValid)
            {
              if (!(localBitmapInfo.source instanceof String)) {
                break label72;
              }
              paramDecodingHandle.callback.onBitmapDecoded(paramDecodingHandle, (String)localBitmapInfo.source, localObject1);
            }
            label72:
            while (!(localBitmapInfo.source instanceof Uri))
            {
              paramDecodingHandle.bitmapInfo = null;
              return;
            }
            paramDecodingHandle.callback.onBitmapDecoded(paramDecodingHandle, (Uri)localBitmapInfo.source, localObject1);
          }
        }
      };
      if (paramDecodingHandle.periodicHandler == null) {
        ((Handler)???).post((Runnable)localObject1);
      }
    }
    else
    {
      return;
    }
    paramDecodingHandle.periodicHandler.schedule((Runnable)localObject1);
    return;
    label150:
    synchronized (this.m_Lock)
    {
      if (localBitmapInfo.isValid)
      {
        if (!(localBitmapInfo.source instanceof String)) {
          break label204;
        }
        paramDecodingHandle.callback.onBitmapDecoded(paramDecodingHandle, (String)localBitmapInfo.source, (Bitmap)localObject1);
      }
      label204:
      while (!(localBitmapInfo.source instanceof Uri))
      {
        paramDecodingHandle.bitmapInfo = null;
        return;
      }
      paramDecodingHandle.callback.onBitmapDecoded(paramDecodingHandle, (Uri)localBitmapInfo.source, (Bitmap)localObject1);
    }
  }
  
  private void cancelDecoding(DecodingHandle paramDecodingHandle)
  {
    synchronized (this.m_Lock)
    {
      BitmapInfo localBitmapInfo = paramDecodingHandle.bitmapInfo;
      if ((localBitmapInfo != null) && (localBitmapInfo.isValid) && (localBitmapInfo.isDecoding))
      {
        if ((localBitmapInfo.decodingHandles.remove(paramDecodingHandle)) && (localBitmapInfo.decodingHandles.isEmpty()))
        {
          localBitmapInfo.isValid = false;
          this.m_BitmapInfos.remove(localBitmapInfo.source);
          this.m_PendingDecodingQueue.remove(localBitmapInfo);
          removeFromQueue(localBitmapInfo);
        }
      }
      else {
        return;
      }
      return;
    }
  }
  
  private void checkDecodingExecutor()
  {
    if (this.m_DecodingExecutor != null) {
      return;
    }
    synchronized (this.m_Lock)
    {
      if (this.m_DecodingExecutor == null) {
        this.m_DecodingExecutor = Executors.newFixedThreadPool(this.m_DecodingThreadCount);
      }
      return;
    }
  }
  
  private void deactivate(Handle paramHandle)
  {
    synchronized (this.m_Lock)
    {
      if ((this.m_ActivateHandles.remove(paramHandle)) && (this.m_ActivateHandles.isEmpty()))
      {
        Log.v(this.TAG, "deactivate()");
        shrink(this.m_IdleCapacity);
      }
      return;
    }
  }
  
  private Handle decode(Context paramContext, Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Callback paramCallback, Handler paramHandler)
  {
    if (paramObject == null)
    {
      Log.e(this.TAG, "decode() - No media source");
      return null;
    }
    if (((paramObject instanceof Uri)) && (paramContext == null))
    {
      Log.e(this.TAG, "decode() - No context");
      return null;
    }
    if ((paramInt2 == 0) || (paramInt3 == 0))
    {
      Log.e(this.TAG, "decode() - Invalid size : " + paramInt2 + "x" + paramInt3);
      return null;
    }
    if (paramCallback == null) {
      Log.v(this.TAG, "decode() - No call-back");
    }
    int i = paramInt2;
    if (paramInt2 < 0) {
      i = Integer.MAX_VALUE;
    }
    paramInt2 = paramInt3;
    if (paramInt3 < 0) {
      paramInt2 = Integer.MAX_VALUE;
    }
    for (;;)
    {
      synchronized (this.m_Lock)
      {
        localBitmapInfo = (BitmapInfo)this.m_BitmapInfos.get(paramObject);
        if (localBitmapInfo != null)
        {
          removeFromQueue(localBitmapInfo);
          if (localBitmapInfo.isDecoding)
          {
            if ((localBitmapInfo.maxTargetlWidth >= i) && (localBitmapInfo.maxTargetHeight >= paramInt2))
            {
              paramContext = new DecodingHandle(localBitmapInfo, i, paramInt2, paramCallback, paramHandler);
              localBitmapInfo.decodingHandles.add(paramContext);
              addToQueue(localBitmapInfo);
              if (((paramInt4 & 0x2) != 0) && (this.m_PendingDecodingQueue.remove(localBitmapInfo))) {
                this.m_PendingDecodingQueue.addFirst(localBitmapInfo);
              }
              this.m_DecodingExecutor.execute(this.m_DecodingRunnable);
              return paramContext;
            }
            localBitmapInfo.isValid = false;
            this.m_BitmapInfos.remove(paramObject);
          }
        }
        else
        {
          if (localBitmapInfo != null) {
            break label561;
          }
          localBitmapInfo = new BitmapInfo(paramObject, paramInt1);
          localBitmapInfo.maxTargetlWidth = i;
          localBitmapInfo.maxTargetHeight = paramInt2;
          this.m_BitmapInfos.put(paramObject, localBitmapInfo);
          addToQueue(localBitmapInfo);
          paramCallback = new DecodingHandle(localBitmapInfo, i, paramInt2, paramCallback, paramHandler);
          localBitmapInfo.decodingHandles.add(paramCallback);
          checkDecodingExecutor();
          if ((paramInt4 & 0x2) != 0) {
            break label575;
          }
          this.m_PendingDecodingQueue.addLast(localBitmapInfo);
          this.m_DecodingExecutor.execute(this.m_DecodingRunnable);
          if (((paramObject instanceof Uri)) && (this.m_ContentResolver == null)) {
            this.m_ContentResolver = paramContext.getApplicationContext().getContentResolver();
          }
          return paramCallback;
        }
        Bitmap localBitmap = localBitmapInfo.bitmap;
        if ((localBitmap != null) && (localBitmapInfo.maxTargetlWidth >= i) && (localBitmapInfo.maxTargetHeight >= paramInt2))
        {
          paramContext = new DecodingHandle(localBitmapInfo, i, paramInt2, paramCallback, paramHandler);
          addToQueue(localBitmapInfo);
          if ((paramInt4 & 0x1) != 0)
          {
            bool = true;
            callOnBitmapDecoded(paramContext, bool);
            return paramContext;
          }
          boolean bool = false;
          continue;
        }
        localBitmapInfo.isValid = false;
        this.m_BitmapInfos.remove(paramObject);
        if (localBitmap == null) {
          continue;
        }
        updateCurrentSize(-localBitmap.getByteCount());
      }
      label561:
      BitmapInfo localBitmapInfo = new BitmapInfo(localBitmapInfo);
      continue;
      label575:
      this.m_PendingDecodingQueue.addFirst(localBitmapInfo);
    }
  }
  
  /* Error */
  private void decodeBitmap(BitmapInfo paramBitmapInfo)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 101	com/oneplus/media/BitmapPool:m_Lock	Ljava/lang/Object;
    //   4: astore 8
    //   6: aload 8
    //   8: monitorenter
    //   9: aload_1
    //   10: getfield 269	com/oneplus/media/BitmapPool$BitmapInfo:isValid	Z
    //   13: istore_3
    //   14: iload_3
    //   15: ifne +7 -> 22
    //   18: aload 8
    //   20: monitorexit
    //   21: return
    //   22: aload 8
    //   24: monitorexit
    //   25: aload_1
    //   26: getfield 333	com/oneplus/media/BitmapPool$BitmapInfo:source	Ljava/lang/Object;
    //   29: astore 13
    //   31: aconst_null
    //   32: astore 12
    //   34: aconst_null
    //   35: astore 11
    //   37: aload 13
    //   39: instanceof 335
    //   42: ifeq +195 -> 237
    //   45: aload 13
    //   47: checkcast 335	java/lang/String
    //   50: astore 12
    //   52: aconst_null
    //   53: astore 9
    //   55: aload 9
    //   57: astore 10
    //   59: aload_1
    //   60: getfield 471	com/oneplus/media/BitmapPool$BitmapInfo:renainingDecodingTime	J
    //   63: lconst_0
    //   64: lcmp
    //   65: ifle +71 -> 136
    //   68: invokestatic 477	android/os/SystemClock:elapsedRealtime	()J
    //   71: lstore 4
    //   73: aload 9
    //   75: astore 10
    //   77: aload_1
    //   78: getfield 480	com/oneplus/media/BitmapPool$BitmapInfo:mediaType	I
    //   81: istore_2
    //   82: aload 9
    //   84: astore 8
    //   86: iload_2
    //   87: tableswitch	default:+25->112, 1:+197->284, 2:+29->116, 3:+261->348
    //   112: aload 9
    //   114: astore 8
    //   116: aload 8
    //   118: astore 10
    //   120: aload 8
    //   122: ifnonnull +14 -> 136
    //   125: aload_0
    //   126: getfield 238	com/oneplus/media/BitmapPool:m_UseEmbeddedThumbnailOnly	Z
    //   129: ifeq +519 -> 648
    //   132: aload 8
    //   134: astore 10
    //   136: aload_0
    //   137: getfield 101	com/oneplus/media/BitmapPool:m_Lock	Ljava/lang/Object;
    //   140: astore 8
    //   142: aload 8
    //   144: monitorenter
    //   145: aload_1
    //   146: getfield 269	com/oneplus/media/BitmapPool$BitmapInfo:isValid	Z
    //   149: ifeq +653 -> 802
    //   152: aload 10
    //   154: ifnull +582 -> 736
    //   157: aload_0
    //   158: aload 10
    //   160: invokevirtual 459	android/graphics/Bitmap:getByteCount	()I
    //   163: i2l
    //   164: invokespecial 462	com/oneplus/media/BitmapPool:updateCurrentSize	(J)V
    //   167: aload_1
    //   168: iconst_0
    //   169: putfield 347	com/oneplus/media/BitmapPool$BitmapInfo:isDecoding	Z
    //   172: aload_1
    //   173: aload 10
    //   175: putfield 273	com/oneplus/media/BitmapPool$BitmapInfo:bitmap	Landroid/graphics/Bitmap;
    //   178: aload_1
    //   179: getfield 350	com/oneplus/media/BitmapPool$BitmapInfo:decodingHandles	Ljava/util/List;
    //   182: invokeinterface 360 1 0
    //   187: ifne +595 -> 782
    //   190: aload_1
    //   191: getfield 350	com/oneplus/media/BitmapPool$BitmapInfo:decodingHandles	Ljava/util/List;
    //   194: invokeinterface 483 1 0
    //   199: iconst_1
    //   200: isub
    //   201: istore_2
    //   202: iload_2
    //   203: iflt +570 -> 773
    //   206: aload_0
    //   207: aload_1
    //   208: getfield 350	com/oneplus/media/BitmapPool$BitmapInfo:decodingHandles	Ljava/util/List;
    //   211: iload_2
    //   212: invokeinterface 486 2 0
    //   217: checkcast 18	com/oneplus/media/BitmapPool$DecodingHandle
    //   220: iconst_0
    //   221: invokespecial 456	com/oneplus/media/BitmapPool:callOnBitmapDecoded	(Lcom/oneplus/media/BitmapPool$DecodingHandle;Z)V
    //   224: iload_2
    //   225: iconst_1
    //   226: isub
    //   227: istore_2
    //   228: goto -26 -> 202
    //   231: astore_1
    //   232: aload 8
    //   234: monitorexit
    //   235: aload_1
    //   236: athrow
    //   237: aload 13
    //   239: instanceof 341
    //   242: ifeq +13 -> 255
    //   245: aload 13
    //   247: checkcast 341	android/net/Uri
    //   250: astore 11
    //   252: goto -200 -> 52
    //   255: aload_0
    //   256: getfield 216	com/oneplus/media/BitmapPool:TAG	Ljava/lang/String;
    //   259: new 186	java/lang/StringBuilder
    //   262: dup
    //   263: invokespecial 187	java/lang/StringBuilder:<init>	()V
    //   266: ldc_w 488
    //   269: invokevirtual 193	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   272: aload 13
    //   274: invokevirtual 491	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   277: invokevirtual 202	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   280: invokestatic 395	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   283: return
    //   284: aload 12
    //   286: ifnull +26 -> 312
    //   289: aload 9
    //   291: astore 10
    //   293: aload_0
    //   294: aload 12
    //   296: aload_1
    //   297: getfield 410	com/oneplus/media/BitmapPool$BitmapInfo:maxTargetlWidth	I
    //   300: aload_1
    //   301: getfield 413	com/oneplus/media/BitmapPool$BitmapInfo:maxTargetHeight	I
    //   304: invokevirtual 495	com/oneplus/media/BitmapPool:decodePhoto	(Ljava/lang/String;II)Landroid/graphics/Bitmap;
    //   307: astore 8
    //   309: goto -193 -> 116
    //   312: aload 9
    //   314: astore 8
    //   316: aload 11
    //   318: ifnull -202 -> 116
    //   321: aload 9
    //   323: astore 10
    //   325: aload_0
    //   326: aload_0
    //   327: getfield 444	com/oneplus/media/BitmapPool:m_ContentResolver	Landroid/content/ContentResolver;
    //   330: aload 11
    //   332: aload_1
    //   333: getfield 410	com/oneplus/media/BitmapPool$BitmapInfo:maxTargetlWidth	I
    //   336: aload_1
    //   337: getfield 413	com/oneplus/media/BitmapPool$BitmapInfo:maxTargetHeight	I
    //   340: invokevirtual 498	com/oneplus/media/BitmapPool:decodePhoto	(Landroid/content/ContentResolver;Landroid/net/Uri;II)Landroid/graphics/Bitmap;
    //   343: astore 8
    //   345: goto -229 -> 116
    //   348: aload 9
    //   350: astore 10
    //   352: aload_0
    //   353: getfield 164	com/oneplus/media/BitmapPool:VIDEO_DECODE_SEMAPHORE	Ljava/util/concurrent/Semaphore;
    //   356: invokevirtual 501	java/util/concurrent/Semaphore:tryAcquire	()Z
    //   359: istore_3
    //   360: iload_3
    //   361: ifeq +129 -> 490
    //   364: aload 12
    //   366: ifnull +72 -> 438
    //   369: aload_0
    //   370: aload 12
    //   372: aload_1
    //   373: getfield 410	com/oneplus/media/BitmapPool$BitmapInfo:maxTargetlWidth	I
    //   376: aload_1
    //   377: getfield 413	com/oneplus/media/BitmapPool$BitmapInfo:maxTargetHeight	I
    //   380: invokevirtual 504	com/oneplus/media/BitmapPool:decodeVideo	(Ljava/lang/String;II)Landroid/graphics/Bitmap;
    //   383: astore 8
    //   385: aload 8
    //   387: astore 10
    //   389: aload_0
    //   390: getfield 164	com/oneplus/media/BitmapPool:VIDEO_DECODE_SEMAPHORE	Ljava/util/concurrent/Semaphore;
    //   393: invokevirtual 507	java/util/concurrent/Semaphore:release	()V
    //   396: goto -280 -> 116
    //   399: astore 8
    //   401: aload_0
    //   402: getfield 216	com/oneplus/media/BitmapPool:TAG	Ljava/lang/String;
    //   405: new 186	java/lang/StringBuilder
    //   408: dup
    //   409: invokespecial 187	java/lang/StringBuilder:<init>	()V
    //   412: ldc_w 509
    //   415: invokevirtual 193	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   418: aload 13
    //   420: invokevirtual 491	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   423: invokevirtual 202	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   426: aload 8
    //   428: invokestatic 512	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   431: aload 10
    //   433: astore 8
    //   435: goto -319 -> 116
    //   438: aload 9
    //   440: astore 8
    //   442: aload 11
    //   444: ifnull -59 -> 385
    //   447: aload_0
    //   448: aload_0
    //   449: getfield 444	com/oneplus/media/BitmapPool:m_ContentResolver	Landroid/content/ContentResolver;
    //   452: aload 11
    //   454: aload_1
    //   455: getfield 410	com/oneplus/media/BitmapPool$BitmapInfo:maxTargetlWidth	I
    //   458: aload_1
    //   459: getfield 413	com/oneplus/media/BitmapPool$BitmapInfo:maxTargetHeight	I
    //   462: invokevirtual 514	com/oneplus/media/BitmapPool:decodeVideo	(Landroid/content/ContentResolver;Landroid/net/Uri;II)Landroid/graphics/Bitmap;
    //   465: astore 8
    //   467: goto -82 -> 385
    //   470: astore 8
    //   472: aload 9
    //   474: astore 10
    //   476: aload_0
    //   477: getfield 164	com/oneplus/media/BitmapPool:VIDEO_DECODE_SEMAPHORE	Ljava/util/concurrent/Semaphore;
    //   480: invokevirtual 507	java/util/concurrent/Semaphore:release	()V
    //   483: aload 9
    //   485: astore 10
    //   487: aload 8
    //   489: athrow
    //   490: aload 9
    //   492: astore 10
    //   494: aload_0
    //   495: getfield 101	com/oneplus/media/BitmapPool:m_Lock	Ljava/lang/Object;
    //   498: astore 8
    //   500: aload 9
    //   502: astore 10
    //   504: aload 8
    //   506: monitorenter
    //   507: aload_1
    //   508: getfield 269	com/oneplus/media/BitmapPool$BitmapInfo:isValid	Z
    //   511: ifeq +113 -> 624
    //   514: aload_0
    //   515: getfield 105	com/oneplus/media/BitmapPool:m_PendingDecodingQueue	Ljava/util/LinkedList;
    //   518: aload_1
    //   519: invokevirtual 425	java/util/LinkedList:addFirst	(Ljava/lang/Object;)V
    //   522: aload_0
    //   523: getfield 105	com/oneplus/media/BitmapPool:m_PendingDecodingQueue	Ljava/util/LinkedList;
    //   526: invokevirtual 515	java/util/LinkedList:size	()I
    //   529: iconst_1
    //   530: if_icmple +62 -> 592
    //   533: aload_0
    //   534: getfield 105	com/oneplus/media/BitmapPool:m_PendingDecodingQueue	Ljava/util/LinkedList;
    //   537: invokevirtual 519	java/util/LinkedList:iterator	()Ljava/util/Iterator;
    //   540: astore 10
    //   542: aload 10
    //   544: invokeinterface 524 1 0
    //   549: ifeq +43 -> 592
    //   552: aload 10
    //   554: invokeinterface 527 1 0
    //   559: checkcast 12	com/oneplus/media/BitmapPool$BitmapInfo
    //   562: astore 14
    //   564: aload 14
    //   566: getfield 480	com/oneplus/media/BitmapPool$BitmapInfo:mediaType	I
    //   569: iconst_1
    //   570: if_icmpne -28 -> 542
    //   573: aload_0
    //   574: getfield 105	com/oneplus/media/BitmapPool:m_PendingDecodingQueue	Ljava/util/LinkedList;
    //   577: aload 14
    //   579: invokevirtual 364	java/util/LinkedList:remove	(Ljava/lang/Object;)Z
    //   582: pop
    //   583: aload_0
    //   584: getfield 105	com/oneplus/media/BitmapPool:m_PendingDecodingQueue	Ljava/util/LinkedList;
    //   587: aload 14
    //   589: invokevirtual 425	java/util/LinkedList:addFirst	(Ljava/lang/Object;)V
    //   592: invokestatic 477	android/os/SystemClock:elapsedRealtime	()J
    //   595: lstore 6
    //   597: aload_1
    //   598: aload_1
    //   599: getfield 471	com/oneplus/media/BitmapPool$BitmapInfo:renainingDecodingTime	J
    //   602: lload 6
    //   604: lload 4
    //   606: lsub
    //   607: lsub
    //   608: putfield 471	com/oneplus/media/BitmapPool$BitmapInfo:renainingDecodingTime	J
    //   611: aload_0
    //   612: getfield 370	com/oneplus/media/BitmapPool:m_DecodingExecutor	Ljava/util/concurrent/Executor;
    //   615: aload_0
    //   616: getfield 179	com/oneplus/media/BitmapPool:m_DecodingRunnable	Ljava/lang/Runnable;
    //   619: invokeinterface 430 2 0
    //   624: aload 9
    //   626: astore 10
    //   628: aload 8
    //   630: monitorexit
    //   631: return
    //   632: astore 14
    //   634: aload 9
    //   636: astore 10
    //   638: aload 8
    //   640: monitorexit
    //   641: aload 9
    //   643: astore 10
    //   645: aload 14
    //   647: athrow
    //   648: invokestatic 477	android/os/SystemClock:elapsedRealtime	()J
    //   651: lstore 6
    //   653: aload_1
    //   654: aload_1
    //   655: getfield 471	com/oneplus/media/BitmapPool$BitmapInfo:renainingDecodingTime	J
    //   658: lload 6
    //   660: lload 4
    //   662: lsub
    //   663: lsub
    //   664: putfield 471	com/oneplus/media/BitmapPool$BitmapInfo:renainingDecodingTime	J
    //   667: aload 8
    //   669: astore 10
    //   671: aload_1
    //   672: getfield 471	com/oneplus/media/BitmapPool$BitmapInfo:renainingDecodingTime	J
    //   675: lconst_0
    //   676: lcmp
    //   677: ifgt -541 -> 136
    //   680: aload_0
    //   681: getfield 216	com/oneplus/media/BitmapPool:TAG	Ljava/lang/String;
    //   684: new 186	java/lang/StringBuilder
    //   687: dup
    //   688: invokespecial 187	java/lang/StringBuilder:<init>	()V
    //   691: ldc_w 509
    //   694: invokevirtual 193	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   697: aload 13
    //   699: invokevirtual 491	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   702: ldc_w 529
    //   705: invokevirtual 193	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   708: invokevirtual 202	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   711: invokestatic 395	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   714: ldc2_w 30
    //   717: invokestatic 532	java/lang/Thread:sleep	(J)V
    //   720: aload 8
    //   722: astore 9
    //   724: goto -669 -> 55
    //   727: astore 9
    //   729: aload 8
    //   731: astore 9
    //   733: goto -678 -> 55
    //   736: aload_0
    //   737: getfield 216	com/oneplus/media/BitmapPool:TAG	Ljava/lang/String;
    //   740: new 186	java/lang/StringBuilder
    //   743: dup
    //   744: invokespecial 187	java/lang/StringBuilder:<init>	()V
    //   747: ldc_w 509
    //   750: invokevirtual 193	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   753: aload 13
    //   755: invokevirtual 491	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   758: invokevirtual 202	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   761: invokestatic 395	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   764: goto -597 -> 167
    //   767: astore_1
    //   768: aload 8
    //   770: monitorexit
    //   771: aload_1
    //   772: athrow
    //   773: aload_1
    //   774: getfield 350	com/oneplus/media/BitmapPool$BitmapInfo:decodingHandles	Ljava/util/List;
    //   777: invokeinterface 535 1 0
    //   782: aload 10
    //   784: ifnonnull +18 -> 802
    //   787: aload_0
    //   788: getfield 174	com/oneplus/media/BitmapPool:m_BitmapInfos	Ljava/util/Hashtable;
    //   791: aload 13
    //   793: invokevirtual 363	java/util/Hashtable:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   796: pop
    //   797: aload_0
    //   798: aload_1
    //   799: invokespecial 367	com/oneplus/media/BitmapPool:removeFromQueue	(Lcom/oneplus/media/BitmapPool$BitmapInfo;)V
    //   802: aload 8
    //   804: monitorexit
    //   805: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	806	0	this	BitmapPool
    //   0	806	1	paramBitmapInfo	BitmapInfo
    //   81	147	2	i	int
    //   13	348	3	bool	boolean
    //   71	590	4	l1	long
    //   595	64	6	l2	long
    //   399	28	8	localThrowable	Throwable
    //   433	33	8	localObject2	Object
    //   470	18	8	localObject3	Object
    //   498	305	8	localObject4	Object
    //   53	670	9	localObject5	Object
    //   727	1	9	localInterruptedException	InterruptedException
    //   731	1	9	localObject6	Object
    //   57	726	10	localObject7	Object
    //   35	418	11	localUri	Uri
    //   32	339	12	str	String
    //   29	763	13	localObject8	Object
    //   562	26	14	localBitmapInfo	BitmapInfo
    //   632	14	14	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   9	14	231	finally
    //   77	82	399	java/lang/Throwable
    //   293	309	399	java/lang/Throwable
    //   325	345	399	java/lang/Throwable
    //   352	360	399	java/lang/Throwable
    //   389	396	399	java/lang/Throwable
    //   476	483	399	java/lang/Throwable
    //   487	490	399	java/lang/Throwable
    //   494	500	399	java/lang/Throwable
    //   504	507	399	java/lang/Throwable
    //   628	631	399	java/lang/Throwable
    //   638	641	399	java/lang/Throwable
    //   645	648	399	java/lang/Throwable
    //   369	385	470	finally
    //   447	467	470	finally
    //   507	542	632	finally
    //   542	592	632	finally
    //   592	624	632	finally
    //   714	720	727	java/lang/InterruptedException
    //   145	152	767	finally
    //   157	167	767	finally
    //   167	202	767	finally
    //   206	224	767	finally
    //   736	764	767	finally
    //   773	782	767	finally
    //   787	802	767	finally
  }
  
  private Bitmap getCachedBitmap(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    synchronized (this.m_Lock)
    {
      paramObject = (BitmapInfo)this.m_BitmapInfos.get(paramObject);
      if (paramObject != null)
      {
        removeFromQueue((BitmapInfo)paramObject);
        addToQueue((BitmapInfo)paramObject);
        if (!((BitmapInfo)paramObject).isDecoding)
        {
          paramObject = ((BitmapInfo)paramObject).bitmap;
          return (Bitmap)paramObject;
        }
      }
      return null;
    }
  }
  
  private void invalidate(Object paramObject)
  {
    if (paramObject == null) {
      return;
    }
    synchronized (this.m_Lock)
    {
      BitmapInfo localBitmapInfo = (BitmapInfo)this.m_BitmapInfos.get(paramObject);
      if (localBitmapInfo == null) {
        return;
      }
      localBitmapInfo.isValid = false;
      this.m_BitmapInfos.remove(paramObject);
      removeFromQueue(localBitmapInfo);
      if (localBitmapInfo.isDecoding)
      {
        this.m_PendingDecodingQueue.remove(localBitmapInfo);
        localBitmapInfo = new BitmapInfo(localBitmapInfo);
        this.m_BitmapInfos.put(paramObject, localBitmapInfo);
        addToQueue(localBitmapInfo);
        this.m_PendingDecodingQueue.addLast(localBitmapInfo);
        this.m_DecodingExecutor.execute(this.m_DecodingRunnable);
      }
      while (localBitmapInfo.bitmap == null) {
        return;
      }
      updateCurrentSize(-localBitmapInfo.bitmap.getByteCount());
    }
  }
  
  private void removeFromQueue(BitmapInfo paramBitmapInfo)
  {
    if (this.m_BitmapQueueHead == paramBitmapInfo) {
      this.m_BitmapQueueHead = paramBitmapInfo.next;
    }
    if (this.m_BitmapQueueTail == paramBitmapInfo) {
      this.m_BitmapQueueTail = paramBitmapInfo.previous;
    }
    if (paramBitmapInfo.previous != null) {
      paramBitmapInfo.previous.next = paramBitmapInfo.next;
    }
    if (paramBitmapInfo.next != null) {
      paramBitmapInfo.next.previous = paramBitmapInfo.previous;
    }
    paramBitmapInfo.previous = null;
    paramBitmapInfo.next = null;
  }
  
  private void updateCurrentSize(long paramLong)
  {
    if (this.m_ActivateHandles.isEmpty()) {}
    for (long l = this.m_IdleCapacity;; l = this.m_Capacity)
    {
      updateCurrentSize(l, paramLong);
      return;
    }
  }
  
  /* Error */
  private void updateCurrentSize(long paramLong1, long paramLong2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 101	com/oneplus/media/BitmapPool:m_Lock	Ljava/lang/Object;
    //   4: astore 9
    //   6: aload 9
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 543	com/oneplus/media/BitmapPool:m_CurrentSize	J
    //   13: lstore 5
    //   15: aload_0
    //   16: aload_0
    //   17: getfield 543	com/oneplus/media/BitmapPool:m_CurrentSize	J
    //   20: lload_3
    //   21: ladd
    //   22: putfield 543	com/oneplus/media/BitmapPool:m_CurrentSize	J
    //   25: aload_0
    //   26: getfield 257	com/oneplus/media/BitmapPool:m_BitmapQueueTail	Lcom/oneplus/media/BitmapPool$BitmapInfo;
    //   29: astore 7
    //   31: aload_0
    //   32: getfield 543	com/oneplus/media/BitmapPool:m_CurrentSize	J
    //   35: lload_1
    //   36: lcmp
    //   37: ifle +93 -> 130
    //   40: aload 7
    //   42: ifnull +88 -> 130
    //   45: aload 7
    //   47: getfield 255	com/oneplus/media/BitmapPool$BitmapInfo:previous	Lcom/oneplus/media/BitmapPool$BitmapInfo;
    //   50: astore 8
    //   52: aload 7
    //   54: getfield 347	com/oneplus/media/BitmapPool$BitmapInfo:isDecoding	Z
    //   57: ifne +53 -> 110
    //   60: aload 7
    //   62: getfield 273	com/oneplus/media/BitmapPool$BitmapInfo:bitmap	Landroid/graphics/Bitmap;
    //   65: ifnull +45 -> 110
    //   68: aload_0
    //   69: getfield 543	com/oneplus/media/BitmapPool:m_CurrentSize	J
    //   72: lstore_3
    //   73: aload_0
    //   74: aload_0
    //   75: getfield 543	com/oneplus/media/BitmapPool:m_CurrentSize	J
    //   78: aload 7
    //   80: getfield 273	com/oneplus/media/BitmapPool$BitmapInfo:bitmap	Landroid/graphics/Bitmap;
    //   83: invokevirtual 459	android/graphics/Bitmap:getByteCount	()I
    //   86: i2l
    //   87: lsub
    //   88: putfield 543	com/oneplus/media/BitmapPool:m_CurrentSize	J
    //   91: aload_0
    //   92: getfield 174	com/oneplus/media/BitmapPool:m_BitmapInfos	Ljava/util/Hashtable;
    //   95: aload 7
    //   97: getfield 333	com/oneplus/media/BitmapPool$BitmapInfo:source	Ljava/lang/Object;
    //   100: invokevirtual 363	java/util/Hashtable:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   103: pop
    //   104: aload_0
    //   105: aload 7
    //   107: invokespecial 367	com/oneplus/media/BitmapPool:removeFromQueue	(Lcom/oneplus/media/BitmapPool$BitmapInfo;)V
    //   110: aload 8
    //   112: astore 7
    //   114: goto -83 -> 31
    //   117: astore 7
    //   119: aload 7
    //   121: athrow
    //   122: astore 7
    //   124: aload 9
    //   126: monitorexit
    //   127: aload 7
    //   129: athrow
    //   130: aload 9
    //   132: monitorexit
    //   133: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	134	0	this	BitmapPool
    //   0	134	1	paramLong1	long
    //   0	134	3	paramLong2	long
    //   13	1	5	l	long
    //   29	84	7	localObject1	Object
    //   117	3	7	localObject2	Object
    //   122	6	7	localObject3	Object
    //   50	61	8	localBitmapInfo	BitmapInfo
    //   4	127	9	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   52	110	117	finally
    //   9	31	122	finally
    //   31	40	122	finally
    //   45	52	122	finally
    //   119	122	122	finally
  }
  
  public final Handle activate()
  {
    Handle local2 = new Handle("ActivateBitmapPool")
    {
      protected void onClose(int paramAnonymousInt)
      {
        BitmapPool.-wrap1(BitmapPool.this, this);
      }
    };
    synchronized (this.m_Lock)
    {
      this.m_ActivateHandles.add(local2);
      if (this.m_ActivateHandles.size() == 1) {
        Log.v(this.TAG, "activate()");
      }
      return local2;
    }
  }
  
  public boolean canUseEmbeddedThumbnail()
  {
    return this.m_CanUseEmbeddedThumbnail;
  }
  
  public Handle decode(Context paramContext, Uri paramUri, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Callback paramCallback, Handler paramHandler)
  {
    return decode(paramContext, paramUri, paramInt1, paramInt2, paramInt3, paramInt4, paramCallback, paramHandler);
  }
  
  public Handle decode(Context paramContext, Uri paramUri, int paramInt1, int paramInt2, int paramInt3, Callback paramCallback, Handler paramHandler)
  {
    return decode(null, paramUri, paramInt1, paramInt2, paramInt3, 0, paramCallback, paramHandler);
  }
  
  public Handle decode(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Callback paramCallback, Handler paramHandler)
  {
    int i = paramInt1;
    if (paramInt1 == 0) {
      if (!FileUtils.isVideoFilePath(paramString)) {
        break label35;
      }
    }
    label35:
    for (i = 3;; i = 1) {
      return decode(null, paramString, i, paramInt2, paramInt3, paramInt4, paramCallback, paramHandler);
    }
  }
  
  public Handle decode(String paramString, int paramInt1, int paramInt2, int paramInt3, Callback paramCallback, Handler paramHandler)
  {
    return decode(paramString, paramInt1, paramInt2, paramInt3, 0, paramCallback, paramHandler);
  }
  
  /* Error */
  protected Bitmap decodePhoto(ContentResolver paramContentResolver, Uri paramUri, int paramInt1, int paramInt2)
    throws Exception
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aconst_null
    //   4: astore 10
    //   6: aconst_null
    //   7: astore 7
    //   9: aconst_null
    //   10: astore 9
    //   12: aconst_null
    //   13: astore 6
    //   15: aconst_null
    //   16: astore 5
    //   18: aload_1
    //   19: aload_2
    //   20: invokevirtual 577	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   23: astore_1
    //   24: aload_1
    //   25: astore 5
    //   27: aload_1
    //   28: astore 6
    //   30: aload_0
    //   31: getfield 234	com/oneplus/media/BitmapPool:m_PreferQualityOverSpeed	Z
    //   34: ifeq +125 -> 159
    //   37: aload_1
    //   38: astore 5
    //   40: aload_1
    //   41: astore 6
    //   43: aload_1
    //   44: iload_3
    //   45: iload 4
    //   47: aload_0
    //   48: invokevirtual 580	com/oneplus/media/BitmapPool:getDecodeFlags	()I
    //   51: getstatic 140	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   54: invokestatic 583	com/oneplus/media/ImageUtils:decodeBitmap	(Ljava/io/InputStream;IIILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   57: astore 10
    //   59: aload_1
    //   60: astore 5
    //   62: aload_1
    //   63: astore 6
    //   65: aload_0
    //   66: getfield 228	com/oneplus/media/BitmapPool:m_BitmapConfig	Landroid/graphics/Bitmap$Config;
    //   69: astore_2
    //   70: aload_1
    //   71: astore 5
    //   73: aload_1
    //   74: astore 6
    //   76: getstatic 140	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   79: astore 11
    //   81: aload_2
    //   82: aload 11
    //   84: if_acmpne +30 -> 114
    //   87: aload 9
    //   89: astore_2
    //   90: aload_1
    //   91: ifnull +10 -> 101
    //   94: aload_1
    //   95: invokevirtual 588	java/io/InputStream:close	()V
    //   98: aload 9
    //   100: astore_2
    //   101: aload_2
    //   102: ifnull +9 -> 111
    //   105: aload_2
    //   106: athrow
    //   107: astore_2
    //   108: goto -7 -> 101
    //   111: aload 10
    //   113: areturn
    //   114: aload_1
    //   115: astore 5
    //   117: aload_1
    //   118: astore 6
    //   120: aload 10
    //   122: aload_0
    //   123: getfield 228	com/oneplus/media/BitmapPool:m_BitmapConfig	Landroid/graphics/Bitmap$Config;
    //   126: iconst_0
    //   127: invokevirtual 592	android/graphics/Bitmap:copy	(Landroid/graphics/Bitmap$Config;Z)Landroid/graphics/Bitmap;
    //   130: astore 9
    //   132: aload 8
    //   134: astore_2
    //   135: aload_1
    //   136: ifnull +10 -> 146
    //   139: aload_1
    //   140: invokevirtual 588	java/io/InputStream:close	()V
    //   143: aload 8
    //   145: astore_2
    //   146: aload_2
    //   147: ifnull +9 -> 156
    //   150: aload_2
    //   151: athrow
    //   152: astore_2
    //   153: goto -7 -> 146
    //   156: aload 9
    //   158: areturn
    //   159: aload_1
    //   160: astore 5
    //   162: aload_1
    //   163: astore 6
    //   165: aload_1
    //   166: iload_3
    //   167: iload 4
    //   169: aload_0
    //   170: invokevirtual 580	com/oneplus/media/BitmapPool:getDecodeFlags	()I
    //   173: aload_0
    //   174: getfield 228	com/oneplus/media/BitmapPool:m_BitmapConfig	Landroid/graphics/Bitmap$Config;
    //   177: invokestatic 583	com/oneplus/media/ImageUtils:decodeBitmap	(Ljava/io/InputStream;IIILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   180: astore 8
    //   182: aload 10
    //   184: astore_2
    //   185: aload_1
    //   186: ifnull +10 -> 196
    //   189: aload_1
    //   190: invokevirtual 588	java/io/InputStream:close	()V
    //   193: aload 10
    //   195: astore_2
    //   196: aload_2
    //   197: ifnull +9 -> 206
    //   200: aload_2
    //   201: athrow
    //   202: astore_2
    //   203: goto -7 -> 196
    //   206: aload 8
    //   208: areturn
    //   209: astore_1
    //   210: aload_1
    //   211: athrow
    //   212: astore_2
    //   213: aload_1
    //   214: astore 6
    //   216: aload 5
    //   218: ifnull +11 -> 229
    //   221: aload 5
    //   223: invokevirtual 588	java/io/InputStream:close	()V
    //   226: aload_1
    //   227: astore 6
    //   229: aload 6
    //   231: ifnull +40 -> 271
    //   234: aload 6
    //   236: athrow
    //   237: astore 5
    //   239: aload_1
    //   240: ifnonnull +10 -> 250
    //   243: aload 5
    //   245: astore 6
    //   247: goto -18 -> 229
    //   250: aload_1
    //   251: astore 6
    //   253: aload_1
    //   254: aload 5
    //   256: if_acmpeq -27 -> 229
    //   259: aload_1
    //   260: aload 5
    //   262: invokevirtual 596	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   265: aload_1
    //   266: astore 6
    //   268: goto -39 -> 229
    //   271: aload_2
    //   272: athrow
    //   273: astore_2
    //   274: aload 6
    //   276: astore 5
    //   278: aload 7
    //   280: astore_1
    //   281: goto -68 -> 213
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	284	0	this	BitmapPool
    //   0	284	1	paramContentResolver	ContentResolver
    //   0	284	2	paramUri	Uri
    //   0	284	3	paramInt1	int
    //   0	284	4	paramInt2	int
    //   16	206	5	localContentResolver	ContentResolver
    //   237	24	5	localThrowable	Throwable
    //   276	1	5	localObject1	Object
    //   13	262	6	localObject2	Object
    //   7	272	7	localObject3	Object
    //   1	206	8	localBitmap1	Bitmap
    //   10	147	9	localBitmap2	Bitmap
    //   4	190	10	localBitmap3	Bitmap
    //   79	4	11	localConfig	Bitmap.Config
    // Exception table:
    //   from	to	target	type
    //   94	98	107	java/lang/Throwable
    //   139	143	152	java/lang/Throwable
    //   189	193	202	java/lang/Throwable
    //   18	24	209	java/lang/Throwable
    //   30	37	209	java/lang/Throwable
    //   43	59	209	java/lang/Throwable
    //   65	70	209	java/lang/Throwable
    //   76	81	209	java/lang/Throwable
    //   120	132	209	java/lang/Throwable
    //   165	182	209	java/lang/Throwable
    //   210	212	212	finally
    //   221	226	237	java/lang/Throwable
    //   18	24	273	finally
    //   30	37	273	finally
    //   43	59	273	finally
    //   65	70	273	finally
    //   76	81	273	finally
    //   120	132	273	finally
    //   165	182	273	finally
  }
  
  protected Bitmap decodePhoto(String paramString, int paramInt1, int paramInt2)
    throws Exception
  {
    if (this.m_PreferQualityOverSpeed)
    {
      paramString = ImageUtils.decodeBitmap(paramString, paramInt1, paramInt2, getDecodeFlags(), Bitmap.Config.ARGB_8888);
      if (this.m_BitmapConfig == Bitmap.Config.ARGB_8888) {
        return paramString;
      }
      return paramString.copy(this.m_BitmapConfig, false);
    }
    return ImageUtils.decodeBitmap(paramString, paramInt1, paramInt2, getDecodeFlags(), this.m_BitmapConfig);
  }
  
  /* Error */
  protected Bitmap decodeVideo(ContentResolver paramContentResolver, Uri paramUri, int paramInt1, int paramInt2)
    throws Exception
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 8
    //   6: aconst_null
    //   7: astore 10
    //   9: aconst_null
    //   10: astore 9
    //   12: aconst_null
    //   13: astore 6
    //   15: aconst_null
    //   16: astore 5
    //   18: aload_1
    //   19: aload_2
    //   20: ldc_w 602
    //   23: invokevirtual 606	android/content/ContentResolver:openFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;)Landroid/os/ParcelFileDescriptor;
    //   26: astore_1
    //   27: aload_1
    //   28: astore 5
    //   30: aload_1
    //   31: astore 6
    //   33: new 608	android/media/MediaMetadataRetriever
    //   36: dup
    //   37: invokespecial 609	android/media/MediaMetadataRetriever:<init>	()V
    //   40: astore_2
    //   41: aload_2
    //   42: aload_1
    //   43: invokevirtual 615	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   46: invokevirtual 619	android/media/MediaMetadataRetriever:setDataSource	(Ljava/io/FileDescriptor;)V
    //   49: aload_2
    //   50: invokevirtual 623	android/media/MediaMetadataRetriever:getFrameAtTime	()Landroid/graphics/Bitmap;
    //   53: astore 5
    //   55: aload 5
    //   57: ifnull +68 -> 125
    //   60: aload 5
    //   62: iload_3
    //   63: iload 4
    //   65: invokestatic 294	com/oneplus/media/ImageUtils:createThumbnailImage	(Landroid/graphics/Bitmap;II)Landroid/graphics/Bitmap;
    //   68: astore 6
    //   70: aload 8
    //   72: astore 5
    //   74: aload_1
    //   75: ifnull +11 -> 86
    //   78: aload_1
    //   79: invokevirtual 624	android/os/ParcelFileDescriptor:close	()V
    //   82: aload 8
    //   84: astore 5
    //   86: aload 5
    //   88: ifnull +26 -> 114
    //   91: aload 5
    //   93: athrow
    //   94: astore 5
    //   96: aload_2
    //   97: astore_1
    //   98: aload_1
    //   99: ifnull +7 -> 106
    //   102: aload_1
    //   103: invokevirtual 625	android/media/MediaMetadataRetriever:release	()V
    //   106: aload 5
    //   108: athrow
    //   109: astore 5
    //   111: goto -25 -> 86
    //   114: aload_2
    //   115: ifnull +7 -> 122
    //   118: aload_2
    //   119: invokevirtual 625	android/media/MediaMetadataRetriever:release	()V
    //   122: aload 6
    //   124: areturn
    //   125: aload_1
    //   126: ifnull +7 -> 133
    //   129: aload_1
    //   130: invokevirtual 624	android/os/ParcelFileDescriptor:close	()V
    //   133: aconst_null
    //   134: astore_1
    //   135: aload_1
    //   136: ifnull +9 -> 145
    //   139: aload_1
    //   140: athrow
    //   141: astore_1
    //   142: goto -7 -> 135
    //   145: aload_2
    //   146: ifnull +7 -> 153
    //   149: aload_2
    //   150: invokevirtual 625	android/media/MediaMetadataRetriever:release	()V
    //   153: aconst_null
    //   154: areturn
    //   155: astore_2
    //   156: aload 9
    //   158: astore_1
    //   159: aload_2
    //   160: athrow
    //   161: astore 7
    //   163: aload_2
    //   164: astore 6
    //   166: aload 7
    //   168: astore_2
    //   169: aload 6
    //   171: astore 7
    //   173: aload 5
    //   175: ifnull +12 -> 187
    //   178: aload 5
    //   180: invokevirtual 624	android/os/ParcelFileDescriptor:close	()V
    //   183: aload 6
    //   185: astore 7
    //   187: aload 7
    //   189: ifnull +31 -> 220
    //   192: aload 7
    //   194: athrow
    //   195: aload 6
    //   197: astore 7
    //   199: aload 6
    //   201: aload 5
    //   203: if_acmpeq -16 -> 187
    //   206: aload 6
    //   208: aload 5
    //   210: invokevirtual 596	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   213: aload 6
    //   215: astore 7
    //   217: goto -30 -> 187
    //   220: aload_2
    //   221: athrow
    //   222: astore_2
    //   223: aload 6
    //   225: astore 5
    //   227: aload 10
    //   229: astore_1
    //   230: aload 7
    //   232: astore 6
    //   234: goto -65 -> 169
    //   237: astore 6
    //   239: aload_1
    //   240: astore 5
    //   242: aload_2
    //   243: astore_1
    //   244: aload 6
    //   246: astore_2
    //   247: aload 7
    //   249: astore 6
    //   251: goto -82 -> 169
    //   254: astore 6
    //   256: aload_1
    //   257: astore 5
    //   259: aload_2
    //   260: astore_1
    //   261: aload 6
    //   263: astore_2
    //   264: goto -105 -> 159
    //   267: astore 5
    //   269: goto -171 -> 98
    //   272: astore 5
    //   274: aload 6
    //   276: ifnonnull -81 -> 195
    //   279: aload 5
    //   281: astore 7
    //   283: goto -96 -> 187
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	286	0	this	BitmapPool
    //   0	286	1	paramContentResolver	ContentResolver
    //   0	286	2	paramUri	Uri
    //   0	286	3	paramInt1	int
    //   0	286	4	paramInt2	int
    //   16	76	5	localObject1	Object
    //   94	13	5	localObject2	Object
    //   109	100	5	localThrowable1	Throwable
    //   225	33	5	localObject3	Object
    //   267	1	5	localObject4	Object
    //   272	8	5	localThrowable2	Throwable
    //   13	220	6	localObject5	Object
    //   237	8	6	localObject6	Object
    //   249	1	6	localObject7	Object
    //   254	21	6	localThrowable3	Throwable
    //   1	1	7	localObject8	Object
    //   161	6	7	localObject9	Object
    //   171	111	7	localObject10	Object
    //   4	79	8	localObject11	Object
    //   10	147	9	localObject12	Object
    //   7	221	10	localObject13	Object
    // Exception table:
    //   from	to	target	type
    //   78	82	94	finally
    //   91	94	94	finally
    //   129	133	94	finally
    //   139	141	94	finally
    //   78	82	109	java/lang/Throwable
    //   129	133	141	java/lang/Throwable
    //   18	27	155	java/lang/Throwable
    //   33	41	155	java/lang/Throwable
    //   159	161	161	finally
    //   18	27	222	finally
    //   33	41	222	finally
    //   41	55	237	finally
    //   60	70	237	finally
    //   41	55	254	java/lang/Throwable
    //   60	70	254	java/lang/Throwable
    //   178	183	267	finally
    //   192	195	267	finally
    //   206	213	267	finally
    //   220	222	267	finally
    //   178	183	272	java/lang/Throwable
  }
  
  protected Bitmap decodeVideo(String paramString, int paramInt1, int paramInt2)
    throws Exception
  {
    Object localObject3 = null;
    try
    {
      MediaMetadataRetriever localMediaMetadataRetriever = new MediaMetadataRetriever();
      if (paramString == null) {
        break label54;
      }
    }
    finally
    {
      try
      {
        localMediaMetadataRetriever.setDataSource(paramString);
        paramString = ImageUtils.createThumbnailImage(localMediaMetadataRetriever.getFrameAtTime(), paramInt1, paramInt2);
        if (localMediaMetadataRetriever != null) {
          localMediaMetadataRetriever.release();
        }
        return paramString;
      }
      finally
      {
        paramString = (String)localObject1;
        Object localObject2 = localObject4;
      }
      localObject1 = finally;
      paramString = (String)localObject3;
    }
    paramString.release();
    label54:
    throw ((Throwable)localObject1);
  }
  
  public Bitmap getCachedBitmap(Uri paramUri)
  {
    return getCachedBitmap(paramUri);
  }
  
  public Bitmap getCachedBitmap(String paramString)
  {
    return getCachedBitmap(paramString);
  }
  
  public int getDecodeFlags()
  {
    int j = 0;
    if (this.m_PreferQualityOverSpeed) {
      j = 1;
    }
    int i;
    if (!this.m_CanUseEmbeddedThumbnail) {
      i = j | 0x2;
    }
    for (;;)
    {
      j = i;
      if (this.m_Opaque) {
        j = i | 0x10;
      }
      return j;
      i = j;
      if (this.m_UseEmbeddedThumbnailOnly) {
        i = j | 0x4;
      }
    }
  }
  
  public Bitmap.Config getTargetConfig()
  {
    return this.m_BitmapConfig;
  }
  
  public void invalidate(Uri paramUri)
  {
    invalidate(paramUri);
  }
  
  public void invalidate(String paramString)
  {
    invalidate(paramString);
  }
  
  public boolean preferQualityOverSpeed()
  {
    return this.m_PreferQualityOverSpeed;
  }
  
  public void shrink(long paramLong)
  {
    updateCurrentSize(paramLong, 0L);
  }
  
  public boolean useEmbeddedThumbnailOnly()
  {
    return this.m_UseEmbeddedThumbnailOnly;
  }
  
  private static final class BitmapInfo
  {
    public volatile Bitmap bitmap;
    public final List<BitmapPool.DecodingHandle> decodingHandles = new ArrayList();
    public volatile boolean isDecoding = true;
    public volatile boolean isValid = true;
    public volatile int maxTargetHeight;
    public volatile int maxTargetlWidth;
    public final int mediaType;
    public volatile BitmapInfo next;
    public volatile BitmapInfo previous;
    public volatile long renainingDecodingTime = 3000L;
    public final Object source;
    
    public BitmapInfo(BitmapInfo paramBitmapInfo)
    {
      this.source = paramBitmapInfo.source;
      this.mediaType = paramBitmapInfo.mediaType;
      this.maxTargetlWidth = paramBitmapInfo.maxTargetlWidth;
      this.maxTargetHeight = paramBitmapInfo.maxTargetHeight;
      int i = 0;
      int j = paramBitmapInfo.decodingHandles.size();
      while (i < j)
      {
        this.decodingHandles.add(((BitmapPool.DecodingHandle)paramBitmapInfo.decodingHandles.get(i)).changeBitmapInfo(this));
        i += 1;
      }
    }
    
    public BitmapInfo(Object paramObject, int paramInt)
    {
      this.source = paramObject;
      this.mediaType = paramInt;
    }
  }
  
  public static abstract class Callback
  {
    public void onBitmapDecoded(Handle paramHandle, Uri paramUri, Bitmap paramBitmap) {}
    
    public void onBitmapDecoded(Handle paramHandle, String paramString, Bitmap paramBitmap) {}
  }
  
  private final class DecodingHandle
    extends Handle
  {
    public volatile BitmapPool.BitmapInfo bitmapInfo;
    public final BitmapPool.Callback callback;
    public final Handler callbackHandler;
    public final int maxHeight;
    public final int maxWidth;
    public final BitmapPool.PeriodicCallbackHandler periodicHandler;
    
    public DecodingHandle(BitmapPool.BitmapInfo paramBitmapInfo, int paramInt1, int paramInt2, BitmapPool.Callback paramCallback, Handler paramHandler)
    {
      super();
      this.bitmapInfo = paramBitmapInfo;
      this.callback = paramCallback;
      this.callbackHandler = paramHandler;
      this.maxWidth = paramInt1;
      this.maxHeight = paramInt2;
      if ((BitmapPool.-get0(BitmapPool.this)) && (paramHandler != null))
      {
        paramInt1 = BitmapPool.-get3(BitmapPool.this).size() - 1;
        while (paramInt1 >= 0)
        {
          paramBitmapInfo = (BitmapPool.PeriodicCallbackHandler)BitmapPool.-get3(BitmapPool.this).get(paramInt1);
          if (paramBitmapInfo.isSameLooper(paramHandler))
          {
            this.periodicHandler = paramBitmapInfo;
            return;
          }
          paramInt1 -= 1;
        }
        paramBitmapInfo = new BitmapPool.PeriodicCallbackHandler(BitmapPool.this, paramHandler.getLooper());
        BitmapPool.-get3(BitmapPool.this).add(paramBitmapInfo);
        this.periodicHandler = paramBitmapInfo;
        return;
      }
      this.periodicHandler = null;
    }
    
    public DecodingHandle changeBitmapInfo(BitmapPool.BitmapInfo paramBitmapInfo)
    {
      this.bitmapInfo = paramBitmapInfo;
      return this;
    }
    
    protected void onClose(int paramInt)
    {
      BitmapPool.-wrap0(BitmapPool.this, this);
    }
  }
  
  private final class PeriodicCallbackHandler
    extends Handler
  {
    private final Runnable m_DispatchRunnable = new Runnable()
    {
      public void run()
      {
        BitmapPool.PeriodicCallbackHandler.-wrap0(BitmapPool.PeriodicCallbackHandler.this);
      }
    };
    private volatile boolean m_IsScheduled;
    private final Looper m_Looper;
    private final LinkedList<Runnable> m_PendingRunnables = new LinkedList();
    
    public PeriodicCallbackHandler(Looper paramLooper)
    {
      super();
      this.m_Looper = paramLooper;
    }
    
    private void dispatch()
    {
      try
      {
        this.m_IsScheduled = false;
        boolean bool = this.m_PendingRunnables.isEmpty();
        if (bool) {
          return;
        }
        Runnable[] arrayOfRunnable = (Runnable[])this.m_PendingRunnables.toArray(new Runnable[this.m_PendingRunnables.size()]);
        this.m_PendingRunnables.clear();
        int i = 0;
        int j = arrayOfRunnable.length;
        while (i < j)
        {
          arrayOfRunnable[i].run();
          i += 1;
        }
        return;
      }
      finally {}
    }
    
    public boolean isSameLooper(Handler paramHandler)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (paramHandler != null)
      {
        bool1 = bool2;
        if (paramHandler.getLooper() == this.m_Looper) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public void schedule(Runnable paramRunnable)
    {
      try
      {
        this.m_PendingRunnables.add(paramRunnable);
        boolean bool = this.m_IsScheduled;
        if (bool) {
          return;
        }
        this.m_IsScheduled = true;
        postDelayed(this.m_DispatchRunnable, 100L);
        return;
      }
      finally {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/BitmapPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */