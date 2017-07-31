package com.oneplus.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.oneplus.base.Log;
import com.oneplus.util.GifDecoder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MovieDrawable
  extends Drawable
{
  private static final int BITMAP_REUSE_COUNT = 2;
  private static final long INTERVAL_UPDATE_MOVIE = 30L;
  private static final int MIN_DURATION_OF_MOVIE = 500;
  private static final int MSG_MOVIE_DECODED = 10000;
  private static final int MSG_UPDATE_DECODER = 10002;
  private static final int MSG_UPDATE_MOVIE = 10001;
  private static final int NEW_VALUE_FOR_ZERO_DURATION = 100;
  private static final String TAG = "MovieDrawable";
  private static final Executor m_BackgroundExecutor = Executors.newFixedThreadPool(2);
  private Bitmap m_BitmapForUI;
  private ArrayBlockingQueue<Bitmap> m_BitmapReuseQueue = null;
  private int m_BitmapsCreatedForReuseQueue = 0;
  private Canvas m_BufferCanvas;
  private final Rect m_BufferSrcRect = new Rect();
  private final Context m_Context;
  private int[] m_CopyScratch;
  private DecodeTask m_DecodeTask;
  private GifDecoder m_GifDecoder;
  private Handler m_Handler;
  private boolean m_IsStarted;
  private Movie m_Movie;
  private final Paint m_Paint = new Paint();
  private boolean m_ShouldPrintLogForFirstTime = true;
  private final Object m_Source;
  private long m_StartTime;
  private Bitmap m_Thumbnail;
  
  public MovieDrawable(Context paramContext, Uri paramUri)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("No context");
    }
    if (paramUri == null) {
      throw new IllegalArgumentException("No content URI");
    }
    this.m_Context = paramContext;
    this.m_Source = paramUri;
    this.m_Paint.setFilterBitmap(true);
    createHandler();
  }
  
  public MovieDrawable(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("No file path");
    }
    this.m_Context = null;
    this.m_Source = paramString;
    this.m_Paint.setFilterBitmap(true);
    createHandler();
  }
  
  private void addReusedBitmap(Bitmap paramBitmap)
  {
    if (!this.m_IsStarted)
    {
      Log.w("MovieDrawable", "addReusedBitmap() - quit already");
      return;
    }
    if (paramBitmap == null)
    {
      Log.w("MovieDrawable", "addReusedBitmap() - bitmap is null");
      return;
    }
    if (this.m_BitmapReuseQueue != null) {
      this.m_BitmapReuseQueue.offer(paramBitmap);
    }
  }
  
  private void clearReuseBitmapQueue()
  {
    if (this.m_BitmapReuseQueue != null) {
      this.m_BitmapReuseQueue.clear();
    }
    this.m_BitmapsCreatedForReuseQueue = 0;
  }
  
  private void createHandler()
  {
    this.m_Handler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        MovieDrawable.-wrap3(MovieDrawable.this, paramAnonymousMessage);
      }
    };
  }
  
  private void decodeGifAsync()
  {
    this.m_BitmapReuseQueue = new ArrayBlockingQueue(2);
    int k = this.m_GifDecoder.frameCount();
    int i = 0;
    for (;;)
    {
      Bitmap localBitmap;
      if (this.m_IsStarted)
      {
        l3 = SystemClock.elapsedRealtime();
        localBitmap = getABitmapForDecode(this.m_GifDecoder.getFrame(i));
        if (localBitmap != null) {
          break label116;
        }
      }
      label116:
      for (int j = 0; !this.m_IsStarted; j = 1)
      {
        if (localBitmap != null) {
          localBitmap.recycle();
        }
        Log.w("MovieDrawable", "decodeGifAsync() - time to quit,bitmap:" + localBitmap);
        clearReuseBitmapQueue();
        this.m_GifDecoder.release();
        Log.d("MovieDrawable", "decodeGifAsync() - end ");
        return;
      }
      long l2 = this.m_GifDecoder.geDuration(i);
      long l1 = l2;
      if (l2 <= 0L) {
        l1 = 100L;
      }
      if (j != 0) {
        Message.obtain(this.m_Handler, 10002, localBitmap).sendToTarget();
      }
      long l3 = SystemClock.elapsedRealtime() - l3;
      if (l1 - l3 > 0L)
      {
        l2 = l1 - l3;
        if (this.m_ShouldPrintLogForFirstTime)
        {
          Log.d("MovieDrawable", "decodeGifAsync() - sleep time: " + l2 + ",decode time:" + l3 + ",frame delay:" + l1 + ",index:" + i + ",count:" + k);
          this.m_ShouldPrintLogForFirstTime = false;
        }
      }
      try
      {
        Thread.sleep(l2);
        i = (i + 1) % k;
        continue;
        l2 = 0L;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          Log.w("MovieDrawable", "decodeGifAsync() - exception e: " + localInterruptedException);
        }
      }
    }
  }
  
  /* Error */
  private Movie decodeMovieAsync()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 7
    //   6: aconst_null
    //   7: astore 6
    //   9: aconst_null
    //   10: astore_3
    //   11: aload 6
    //   13: astore_2
    //   14: aload_0
    //   15: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   18: instanceof 270
    //   21: ifeq +29 -> 50
    //   24: aload 6
    //   26: astore_2
    //   27: aload_0
    //   28: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   31: checkcast 270	java/lang/String
    //   34: invokestatic 276	android/graphics/Movie:decodeFile	(Ljava/lang/String;)Landroid/graphics/Movie;
    //   37: astore_3
    //   38: aload_3
    //   39: astore_2
    //   40: ldc 30
    //   42: ldc_w 278
    //   45: invokestatic 227	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   48: aload_2
    //   49: areturn
    //   50: aload 6
    //   52: astore_2
    //   53: aload_0
    //   54: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   57: instanceof 280
    //   60: istore_1
    //   61: iload_1
    //   62: ifeq +170 -> 232
    //   65: aconst_null
    //   66: astore_3
    //   67: aconst_null
    //   68: astore_2
    //   69: aload_0
    //   70: getfield 132	com/oneplus/drawable/MovieDrawable:m_Context	Landroid/content/Context;
    //   73: invokevirtual 286	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   76: aload_0
    //   77: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   80: checkcast 280	android/net/Uri
    //   83: invokevirtual 292	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   86: astore 4
    //   88: aload 4
    //   90: astore_2
    //   91: aload 4
    //   93: astore_3
    //   94: aload 4
    //   96: invokestatic 296	android/graphics/Movie:decodeStream	(Ljava/io/InputStream;)Landroid/graphics/Movie;
    //   99: astore 8
    //   101: aload 8
    //   103: astore_3
    //   104: aload 7
    //   106: astore 5
    //   108: aload 4
    //   110: ifnull +12 -> 122
    //   113: aload 4
    //   115: invokevirtual 301	java/io/InputStream:close	()V
    //   118: aload 7
    //   120: astore 5
    //   122: aload_3
    //   123: astore_2
    //   124: aload 5
    //   126: ifnull -86 -> 40
    //   129: aload_3
    //   130: astore_2
    //   131: aload 5
    //   133: athrow
    //   134: astore_3
    //   135: ldc 30
    //   137: new 203	java/lang/StringBuilder
    //   140: dup
    //   141: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   144: ldc_w 303
    //   147: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   150: aload_0
    //   151: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   154: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   157: invokevirtual 217	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   160: aload_3
    //   161: invokestatic 307	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   164: goto -124 -> 40
    //   167: astore 5
    //   169: goto -47 -> 122
    //   172: astore_3
    //   173: aload_3
    //   174: athrow
    //   175: astore 4
    //   177: aload_3
    //   178: astore 5
    //   180: aload_2
    //   181: ifnull +10 -> 191
    //   184: aload_2
    //   185: invokevirtual 301	java/io/InputStream:close	()V
    //   188: aload_3
    //   189: astore 5
    //   191: aload 5
    //   193: ifnull +33 -> 226
    //   196: aload 6
    //   198: astore_2
    //   199: aload 5
    //   201: athrow
    //   202: aload_3
    //   203: astore 5
    //   205: aload_3
    //   206: aload 7
    //   208: if_acmpeq -17 -> 191
    //   211: aload 6
    //   213: astore_2
    //   214: aload_3
    //   215: aload 7
    //   217: invokevirtual 311	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   220: aload_3
    //   221: astore 5
    //   223: goto -32 -> 191
    //   226: aload 6
    //   228: astore_2
    //   229: aload 4
    //   231: athrow
    //   232: aload 6
    //   234: astore_2
    //   235: ldc 30
    //   237: new 203	java/lang/StringBuilder
    //   240: dup
    //   241: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   244: ldc_w 313
    //   247: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   250: aload_0
    //   251: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   254: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   257: invokevirtual 217	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   260: invokestatic 315	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   263: aload_3
    //   264: astore_2
    //   265: goto -225 -> 40
    //   268: astore 4
    //   270: aload_3
    //   271: astore_2
    //   272: aload 5
    //   274: astore_3
    //   275: goto -98 -> 177
    //   278: astore 7
    //   280: aload_3
    //   281: ifnonnull -79 -> 202
    //   284: aload 7
    //   286: astore 5
    //   288: goto -97 -> 191
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	291	0	this	MovieDrawable
    //   60	2	1	bool	boolean
    //   13	259	2	localObject1	Object
    //   10	120	3	localObject2	Object
    //   134	27	3	localThrowable1	Throwable
    //   172	99	3	localThrowable2	Throwable
    //   274	7	3	localObject3	Object
    //   86	28	4	localInputStream	java.io.InputStream
    //   175	55	4	localObject4	Object
    //   268	1	4	localObject5	Object
    //   1	131	5	localObject6	Object
    //   167	1	5	localThrowable3	Throwable
    //   178	109	5	localObject7	Object
    //   7	226	6	localObject8	Object
    //   4	212	7	localThrowable4	Throwable
    //   278	7	7	localThrowable5	Throwable
    //   99	3	8	localMovie	Movie
    // Exception table:
    //   from	to	target	type
    //   14	24	134	java/lang/Throwable
    //   27	38	134	java/lang/Throwable
    //   53	61	134	java/lang/Throwable
    //   131	134	134	java/lang/Throwable
    //   199	202	134	java/lang/Throwable
    //   214	220	134	java/lang/Throwable
    //   229	232	134	java/lang/Throwable
    //   235	263	134	java/lang/Throwable
    //   113	118	167	java/lang/Throwable
    //   69	88	172	java/lang/Throwable
    //   94	101	172	java/lang/Throwable
    //   173	175	175	finally
    //   69	88	268	finally
    //   94	101	268	finally
    //   184	188	278	java/lang/Throwable
  }
  
  private Bitmap getABitmapForDecode(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      return null;
    }
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    if (this.m_CopyScratch == null) {
      this.m_CopyScratch = new int[i * j];
    }
    Object localObject = null;
    if (this.m_BitmapsCreatedForReuseQueue < 2)
    {
      paramBitmap = paramBitmap.copy(paramBitmap.getConfig(), true);
      this.m_BitmapsCreatedForReuseQueue += 1;
      Log.d("MovieDrawable", "getABitmapForDecode() - create new bitmap. m_BitmapsCreatedForReuseQueue:" + this.m_BitmapsCreatedForReuseQueue + ",bitmap:" + paramBitmap);
      return paramBitmap;
    }
    try
    {
      Bitmap localBitmap = (Bitmap)this.m_BitmapReuseQueue.poll(1L, TimeUnit.MINUTES);
      localObject = localBitmap;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.w("MovieDrawable", "getABitmapForDecode() e = " + localException);
      }
      if (this.m_IsStarted) {
        break label209;
      }
      ((Bitmap)localObject).recycle();
      Log.d("MovieDrawable", "getABitmapForDecode() - time to quit,bitmap:" + localObject);
      return null;
      label209:
      paramBitmap.getPixels(this.m_CopyScratch, 0, i, 0, 0, i, j);
      ((Bitmap)localObject).setPixels(this.m_CopyScratch, 0, i, 0, 0, i, j);
    }
    if (localObject == null)
    {
      Log.w("MovieDrawable", "getABitmapForDecode() - get a null bitmap from queue.");
      return null;
    }
    return (Bitmap)localObject;
  }
  
  private void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return;
    case 10000: 
      onMovieDecoded((Movie)paramMessage.obj);
      return;
    case 10001: 
      updateForMovie();
      return;
    }
    updateForGifDecoder((Bitmap)paramMessage.obj);
  }
  
  /* Error */
  private boolean loadFile()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_0
    //   3: getfield 176	com/oneplus/drawable/MovieDrawable:m_GifDecoder	Lcom/oneplus/util/GifDecoder;
    //   6: ifnull +10 -> 16
    //   9: aload_0
    //   10: getfield 176	com/oneplus/drawable/MovieDrawable:m_GifDecoder	Lcom/oneplus/util/GifDecoder;
    //   13: invokevirtual 222	com/oneplus/util/GifDecoder:release	()V
    //   16: invokestatic 188	android/os/SystemClock:elapsedRealtime	()J
    //   19: lstore_3
    //   20: aload_0
    //   21: new 178	com/oneplus/util/GifDecoder
    //   24: dup
    //   25: invokespecial 377	com/oneplus/util/GifDecoder:<init>	()V
    //   28: putfield 176	com/oneplus/drawable/MovieDrawable:m_GifDecoder	Lcom/oneplus/util/GifDecoder;
    //   31: aload_0
    //   32: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   35: instanceof 270
    //   38: ifeq +63 -> 101
    //   41: aload_0
    //   42: getfield 176	com/oneplus/drawable/MovieDrawable:m_GifDecoder	Lcom/oneplus/util/GifDecoder;
    //   45: aload_0
    //   46: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   49: checkcast 270	java/lang/String
    //   52: invokevirtual 380	com/oneplus/util/GifDecoder:read	(Ljava/lang/String;)V
    //   55: iconst_1
    //   56: istore_2
    //   57: ldc 30
    //   59: new 203	java/lang/StringBuilder
    //   62: dup
    //   63: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   66: ldc_w 382
    //   69: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   72: invokestatic 188	android/os/SystemClock:elapsedRealtime	()J
    //   75: lload_3
    //   76: lsub
    //   77: invokevirtual 247	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   80: ldc_w 384
    //   83: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: aload_0
    //   87: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   90: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   93: invokevirtual 217	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   96: invokestatic 227	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   99: iload_2
    //   100: ireturn
    //   101: aload_0
    //   102: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   105: instanceof 280
    //   108: ifeq +226 -> 334
    //   111: aload_0
    //   112: getfield 132	com/oneplus/drawable/MovieDrawable:m_Context	Landroid/content/Context;
    //   115: invokevirtual 286	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   118: aload_0
    //   119: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   122: checkcast 280	android/net/Uri
    //   125: invokevirtual 292	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   128: astore 7
    //   130: new 386	java/io/ByteArrayOutputStream
    //   133: dup
    //   134: invokespecial 387	java/io/ByteArrayOutputStream:<init>	()V
    //   137: astore 8
    //   139: aconst_null
    //   140: astore 6
    //   142: aload 6
    //   144: astore 5
    //   146: sipush 1024
    //   149: newarray <illegal type>
    //   151: astore 9
    //   153: aload 6
    //   155: astore 5
    //   157: aload 7
    //   159: aload 9
    //   161: invokevirtual 390	java/io/InputStream:read	([B)I
    //   164: istore_1
    //   165: iload_1
    //   166: iconst_m1
    //   167: if_icmpeq +66 -> 233
    //   170: aload 6
    //   172: astore 5
    //   174: aload 8
    //   176: aload 9
    //   178: iconst_0
    //   179: iload_1
    //   180: invokevirtual 394	java/io/ByteArrayOutputStream:write	([BII)V
    //   183: goto -30 -> 153
    //   186: astore 6
    //   188: ldc 30
    //   190: new 203	java/lang/StringBuilder
    //   193: dup
    //   194: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   197: ldc_w 396
    //   200: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   203: aload 6
    //   205: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   208: invokevirtual 217	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   211: invokestatic 155	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   214: aload 7
    //   216: invokevirtual 301	java/io/InputStream:close	()V
    //   219: aload_0
    //   220: getfield 176	com/oneplus/drawable/MovieDrawable:m_GifDecoder	Lcom/oneplus/util/GifDecoder;
    //   223: aload 5
    //   225: invokevirtual 399	com/oneplus/util/GifDecoder:read	([B)V
    //   228: iconst_1
    //   229: istore_2
    //   230: goto -173 -> 57
    //   233: aload 6
    //   235: astore 5
    //   237: aload 8
    //   239: invokevirtual 400	java/io/ByteArrayOutputStream:close	()V
    //   242: aload 6
    //   244: astore 5
    //   246: aload 8
    //   248: invokevirtual 404	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   251: astore 6
    //   253: aload 6
    //   255: astore 5
    //   257: aload 7
    //   259: aload 6
    //   261: invokevirtual 390	java/io/InputStream:read	([B)I
    //   264: pop
    //   265: aload 6
    //   267: astore 5
    //   269: goto -55 -> 214
    //   272: astore 5
    //   274: ldc 30
    //   276: new 203	java/lang/StringBuilder
    //   279: dup
    //   280: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   283: ldc_w 406
    //   286: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   289: aload 5
    //   291: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   294: invokevirtual 217	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   297: invokestatic 315	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   300: goto -243 -> 57
    //   303: astore 6
    //   305: ldc 30
    //   307: new 203	java/lang/StringBuilder
    //   310: dup
    //   311: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   314: ldc_w 396
    //   317: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   320: aload 6
    //   322: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   325: invokevirtual 217	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   328: invokestatic 155	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   331: goto -112 -> 219
    //   334: ldc 30
    //   336: new 203	java/lang/StringBuilder
    //   339: dup
    //   340: invokespecial 204	java/lang/StringBuilder:<init>	()V
    //   343: ldc_w 408
    //   346: invokevirtual 210	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   349: aload_0
    //   350: getfield 134	com/oneplus/drawable/MovieDrawable:m_Source	Ljava/lang/Object;
    //   353: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   356: invokevirtual 217	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   359: invokestatic 315	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   362: goto -305 -> 57
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	365	0	this	MovieDrawable
    //   164	16	1	i	int
    //   1	229	2	bool	boolean
    //   19	57	3	l	long
    //   144	124	5	localObject1	Object
    //   272	18	5	localThrowable	Throwable
    //   140	31	6	localObject2	Object
    //   186	57	6	localException1	Exception
    //   251	15	6	arrayOfByte1	byte[]
    //   303	18	6	localException2	Exception
    //   128	130	7	localInputStream	java.io.InputStream
    //   137	110	8	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   151	26	9	arrayOfByte2	byte[]
    // Exception table:
    //   from	to	target	type
    //   146	153	186	java/lang/Exception
    //   157	165	186	java/lang/Exception
    //   174	183	186	java/lang/Exception
    //   237	242	186	java/lang/Exception
    //   246	253	186	java/lang/Exception
    //   257	265	186	java/lang/Exception
    //   20	55	272	java/lang/Throwable
    //   101	139	272	java/lang/Throwable
    //   146	153	272	java/lang/Throwable
    //   157	165	272	java/lang/Throwable
    //   174	183	272	java/lang/Throwable
    //   188	214	272	java/lang/Throwable
    //   214	219	272	java/lang/Throwable
    //   219	228	272	java/lang/Throwable
    //   237	242	272	java/lang/Throwable
    //   246	253	272	java/lang/Throwable
    //   257	265	272	java/lang/Throwable
    //   305	331	272	java/lang/Throwable
    //   334	362	272	java/lang/Throwable
    //   214	219	303	java/lang/Exception
  }
  
  private void onMovieDecoded(Movie paramMovie)
  {
    if (paramMovie == null) {
      return;
    }
    try
    {
      this.m_Movie = paramMovie;
      this.m_BitmapForUI = Bitmap.createBitmap(paramMovie.width(), paramMovie.height(), Bitmap.Config.ARGB_8888);
      this.m_BufferCanvas = new Canvas(this.m_BitmapForUI);
      this.m_BufferSrcRect.set(0, 0, paramMovie.width(), paramMovie.height());
      this.m_StartTime = SystemClock.elapsedRealtime();
      updateForMovie();
      return;
    }
    catch (Throwable paramMovie)
    {
      Log.e("MovieDrawable", "onMovieDecoded() - Failed.", paramMovie);
    }
  }
  
  private void updateForGifDecoder(Bitmap paramBitmap)
  {
    if (paramBitmap == null)
    {
      Log.w("MovieDrawable", "updateForGifDecoder() - bitmap is null.");
      return;
    }
    this.m_BitmapForUI = paramBitmap;
    if ((isVisible()) && (this.m_IsStarted))
    {
      invalidateSelf();
      return;
    }
    addReusedBitmap(this.m_BitmapForUI);
  }
  
  private void updateForMovie()
  {
    if ((isVisible()) && (this.m_Movie != null) && (this.m_IsStarted))
    {
      long l = SystemClock.elapsedRealtime() - this.m_StartTime;
      if (l > this.m_Movie.duration()) {
        break label72;
      }
      this.m_Movie.setTime((int)l);
    }
    for (;;)
    {
      invalidateSelf();
      this.m_Handler.sendEmptyMessageDelayed(10001, 30L);
      return;
      label72:
      this.m_StartTime = SystemClock.elapsedRealtime();
      this.m_Movie.setTime(0);
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.m_Movie != null)
    {
      this.m_BitmapForUI.eraseColor(0);
      this.m_Movie.draw(this.m_BufferCanvas, 0.0F, 0.0F);
      paramCanvas.drawBitmap(this.m_BitmapForUI, this.m_BufferSrcRect, getBounds(), this.m_Paint);
    }
    do
    {
      return;
      if ((this.m_GifDecoder != null) && (this.m_BitmapForUI != null) && (!this.m_BitmapForUI.isRecycled()))
      {
        this.m_BufferSrcRect.set(0, 0, this.m_BitmapForUI.getWidth(), this.m_BitmapForUI.getHeight());
        paramCanvas.drawBitmap(this.m_BitmapForUI, this.m_BufferSrcRect, getBounds(), this.m_Paint);
        addReusedBitmap(this.m_BitmapForUI);
        return;
      }
    } while (this.m_Thumbnail == null);
    this.m_BufferSrcRect.set(0, 0, this.m_Thumbnail.getWidth(), this.m_Thumbnail.getHeight());
    paramCanvas.drawBitmap(this.m_Thumbnail, this.m_BufferSrcRect, getBounds(), this.m_Paint);
  }
  
  public int getIntrinsicHeight()
  {
    if (this.m_BitmapForUI != null) {
      return this.m_BitmapForUI.getHeight();
    }
    if (this.m_Thumbnail != null) {
      return this.m_Thumbnail.getHeight();
    }
    return 0;
  }
  
  public int getIntrinsicWidth()
  {
    if (this.m_BitmapForUI != null) {
      return this.m_BitmapForUI.getWidth();
    }
    if (this.m_Thumbnail != null) {
      return this.m_Thumbnail.getWidth();
    }
    return 0;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public boolean isTheSameSource(Uri paramUri)
  {
    if (paramUri == null)
    {
      Log.w("MovieDrawable", "isTheSameSource() - uri is null.");
      return false;
    }
    return paramUri.equals(this.m_Source);
  }
  
  public boolean isTheSameSource(String paramString)
  {
    if (paramString == null)
    {
      Log.w("MovieDrawable", "isTheSameSource() - path is null.");
      return false;
    }
    return paramString.equals(this.m_Source);
  }
  
  public void setAlpha(int paramInt)
  {
    this.m_Paint.setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.m_Paint.setColorFilter(paramColorFilter);
  }
  
  public void setThumbnailBitmap(Bitmap paramBitmap)
  {
    if (this.m_Thumbnail != paramBitmap)
    {
      this.m_Thumbnail = paramBitmap;
      invalidateSelf();
    }
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (super.setVisible(paramBoolean1, paramBoolean2))
    {
      if (paramBoolean1) {
        break label25;
      }
      this.m_Handler.removeMessages(10001);
    }
    for (;;)
    {
      return false;
      label25:
      if ((this.m_Movie != null) && (this.m_IsStarted)) {
        updateForMovie();
      }
    }
  }
  
  public void start()
  {
    if (this.m_IsStarted) {
      return;
    }
    this.m_IsStarted = true;
    if (this.m_DecodeTask == null)
    {
      this.m_DecodeTask = new DecodeTask(null);
      m_BackgroundExecutor.execute(this.m_DecodeTask);
    }
    Log.d("MovieDrawable", "start()");
  }
  
  public void stop()
  {
    if (!this.m_IsStarted) {
      return;
    }
    this.m_IsStarted = false;
    this.m_DecodeTask = null;
    if (this.m_GifDecoder != null)
    {
      if (this.m_BitmapForUI == null) {
        break label59;
      }
      addReusedBitmap(this.m_BitmapForUI);
    }
    for (;;)
    {
      this.m_Handler.removeMessages(10001);
      Log.d("MovieDrawable", "stop()");
      return;
      label59:
      Log.w("MovieDrawable", "stop() - m_BitmapForUI is null.");
    }
  }
  
  private final class DecodeTask
    implements Runnable
  {
    private DecodeTask() {}
    
    public void run()
    {
      Movie localMovie = MovieDrawable.-wrap0(MovieDrawable.this);
      if (localMovie == null)
      {
        Log.e("MovieDrawable", "run() - movie is null");
        return;
      }
      int i = localMovie.duration();
      Log.d("MovieDrawable", "run() - movie duration is " + i);
      if (i <= 500)
      {
        if (MovieDrawable.-wrap1(MovieDrawable.this)) {
          MovieDrawable.-wrap2(MovieDrawable.this);
        }
        return;
      }
      Message.obtain(MovieDrawable.-get0(MovieDrawable.this), 10000, localMovie).sendToTarget();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/drawable/MovieDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */