package com.oneplus.camera.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.media.ImageUtils;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class YuvToBitmapWorker
{
  public static final int FLAG_ANTI_ALIAS = 1;
  private static final int MSG_CONVERT = 10000;
  private static final int MSG_QUIT = 10001;
  private static final boolean PROFILE = false;
  private static final long PROFILE_INTERVAL = 3000L;
  private static final String TAG = "YuvToBitmapWorker";
  private final Rect m_BitmapDestRect = new Rect();
  private final Paint m_BitmapScalePaint = new Paint();
  private final Rect m_BitmapSrcRect = new Rect();
  private Allocation m_BlurAllocation;
  private Bitmap m_BlurRgbaBitmap;
  private ScriptIntrinsicBlur m_BlurScript;
  private final int m_BufferCount;
  private final Context m_Context;
  private int m_ConversionCount;
  private long m_ConversionTime;
  private final Queue<Bitmap> m_ConvertedBitmaps;
  private final int m_Flags;
  private final Queue<Bitmap> m_FreeBitmaps;
  private volatile int m_InputHeight;
  private volatile int m_InputWidth;
  private volatile boolean m_IsActive = true;
  private long m_LastProfileTime;
  private Allocation m_NV21Allocation;
  private int m_NV21AllocationHeight;
  private int m_NV21AllocationWidth;
  private final Queue<byte[]> m_NV21Buffers;
  private final Runnable m_NotifyListenerRunnable = new Runnable()
  {
    public void run()
    {
      YuvToBitmapWorker.-wrap1(YuvToBitmapWorker.this);
    }
  };
  private volatile OnBitmapAvailableListener m_OnBitmapAvailableListener;
  private Handler m_OnBitmapAvailableListenerHandler;
  private volatile Bitmap.Config m_OutputConfig;
  private volatile int m_OutputHeight;
  private volatile int m_OutputWidth;
  private RenderScript m_RenderScript;
  private Handle m_RenderScriptHandle;
  private Allocation m_RgbaAllocation;
  private Bitmap m_RgbaBitmap;
  private final HandlerThread m_WorkerThread;
  private Handler m_WorkerThreadHandler;
  private ScriptIntrinsicYuvToRGB m_YuvToRgbaScript;
  
  public YuvToBitmapWorker(Context paramContext, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Bitmap.Config paramConfig, int paramInt6)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("No context.");
    }
    if (paramInt1 <= 0) {
      throw new IllegalArgumentException("Invalid buffer count : " + paramInt1 + ".");
    }
    if ((paramInt2 < 0) || (paramInt3 < 0)) {}
    while (((paramInt2 & 0x1) != 0) || ((paramInt3 & 0x1) != 0)) {
      throw new IllegalArgumentException("Invalid input size : " + paramInt2 + "x" + paramInt3 + ".");
    }
    if ((paramInt4 <= 0) || (paramInt5 <= 0)) {
      throw new IllegalArgumentException("Invalid output size : " + paramInt4 + "x" + paramInt5 + ".");
    }
    if (paramConfig == null) {
      throw new IllegalArgumentException("No output configuration.");
    }
    Log.w("YuvToBitmapWorker", "YuvToBitmapWorker() - Input size : " + paramInt2 + "x" + paramInt3);
    Log.w("YuvToBitmapWorker", "YuvToBitmapWorker() - Output size : " + paramInt4 + "x" + paramInt5 + ", config : " + paramConfig);
    this.m_BufferCount = paramInt1;
    this.m_Context = paramContext;
    this.m_InputWidth = paramInt2;
    this.m_InputHeight = paramInt3;
    this.m_OutputConfig = paramConfig;
    this.m_OutputWidth = paramInt4;
    this.m_OutputHeight = paramInt5;
    this.m_Flags = paramInt6;
    this.m_NV21Buffers = new ArrayDeque(paramInt1);
    this.m_FreeBitmaps = new ArrayBlockingQueue(paramInt1);
    this.m_ConvertedBitmaps = new ArrayBlockingQueue(paramInt1);
    paramInt2 = paramInt2 * paramInt3 * 3 / 2;
    while (paramInt1 > 0)
    {
      this.m_NV21Buffers.add(new byte[paramInt2]);
      this.m_FreeBitmaps.add(Bitmap.createBitmap(paramInt4, paramInt5, paramConfig));
      paramInt1 -= 1;
    }
    this.m_BitmapScalePaint.setFilterBitmap(true);
    this.m_WorkerThread = new HandlerThread("YUV-Bitmap worker thread", -4);
    this.m_WorkerThread.start();
    this.m_WorkerThreadHandler = new Handler(this.m_WorkerThread.getLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        default: 
          return;
        case 10000: 
          YuvToBitmapWorker.-wrap0(YuvToBitmapWorker.this, (byte[])paramAnonymousMessage.obj, paramAnonymousMessage.arg1, paramAnonymousMessage.arg2);
          return;
        }
        YuvToBitmapWorker.-wrap2(YuvToBitmapWorker.this);
      }
    };
  }
  
  /* Error */
  private void convert(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 173	com/oneplus/camera/media/YuvToBitmapWorker:m_InputWidth	I
    //   6: iload_2
    //   7: if_icmpne +11 -> 18
    //   10: aload_0
    //   11: getfield 175	com/oneplus/camera/media/YuvToBitmapWorker:m_InputHeight	I
    //   14: iload_3
    //   15: if_icmpeq +13 -> 28
    //   18: ldc 29
    //   20: ldc -20
    //   22: invokestatic 160	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   25: aload_0
    //   26: monitorexit
    //   27: return
    //   28: aload_0
    //   29: monitorexit
    //   30: aload_0
    //   31: getfield 238	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScript	Landroid/renderscript/RenderScript;
    //   34: ifnonnull +25 -> 59
    //   37: aload_0
    //   38: aload_0
    //   39: getfield 171	com/oneplus/camera/media/YuvToBitmapWorker:m_Context	Landroid/content/Context;
    //   42: invokestatic 244	com/oneplus/renderscript/RenderScriptManager:createRenderScript	(Landroid/content/Context;)Lcom/oneplus/base/Handle;
    //   45: putfield 246	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScriptHandle	Lcom/oneplus/base/Handle;
    //   48: aload_0
    //   49: aload_0
    //   50: getfield 246	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScriptHandle	Lcom/oneplus/base/Handle;
    //   53: invokestatic 250	com/oneplus/renderscript/RenderScriptManager:getRenderScript	(Lcom/oneplus/base/Handle;)Landroid/renderscript/RenderScript;
    //   56: putfield 238	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScript	Landroid/renderscript/RenderScript;
    //   59: aload_0
    //   60: getfield 252	com/oneplus/camera/media/YuvToBitmapWorker:m_NV21Allocation	Landroid/renderscript/Allocation;
    //   63: ifnull +11 -> 74
    //   66: aload_0
    //   67: getfield 254	com/oneplus/camera/media/YuvToBitmapWorker:m_NV21AllocationWidth	I
    //   70: iload_2
    //   71: if_icmpeq +259 -> 330
    //   74: aload_0
    //   75: getfield 252	com/oneplus/camera/media/YuvToBitmapWorker:m_NV21Allocation	Landroid/renderscript/Allocation;
    //   78: ifnull +10 -> 88
    //   81: aload_0
    //   82: getfield 252	com/oneplus/camera/media/YuvToBitmapWorker:m_NV21Allocation	Landroid/renderscript/Allocation;
    //   85: invokevirtual 259	android/renderscript/Allocation:destroy	()V
    //   88: new 261	android/renderscript/Type$Builder
    //   91: dup
    //   92: aload_0
    //   93: getfield 238	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScript	Landroid/renderscript/RenderScript;
    //   96: aload_0
    //   97: getfield 238	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScript	Landroid/renderscript/RenderScript;
    //   100: invokestatic 267	android/renderscript/Element:U8	(Landroid/renderscript/RenderScript;)Landroid/renderscript/Element;
    //   103: invokespecial 270	android/renderscript/Type$Builder:<init>	(Landroid/renderscript/RenderScript;Landroid/renderscript/Element;)V
    //   106: astore 5
    //   108: aload 5
    //   110: aload_1
    //   111: arraylength
    //   112: invokevirtual 274	android/renderscript/Type$Builder:setX	(I)Landroid/renderscript/Type$Builder;
    //   115: pop
    //   116: aload_0
    //   117: aload_0
    //   118: getfield 238	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScript	Landroid/renderscript/RenderScript;
    //   121: aload 5
    //   123: invokevirtual 278	android/renderscript/Type$Builder:create	()Landroid/renderscript/Type;
    //   126: iconst_1
    //   127: invokestatic 282	android/renderscript/Allocation:createTyped	(Landroid/renderscript/RenderScript;Landroid/renderscript/Type;I)Landroid/renderscript/Allocation;
    //   130: putfield 252	com/oneplus/camera/media/YuvToBitmapWorker:m_NV21Allocation	Landroid/renderscript/Allocation;
    //   133: aload_0
    //   134: iload_2
    //   135: putfield 254	com/oneplus/camera/media/YuvToBitmapWorker:m_NV21AllocationWidth	I
    //   138: aload_0
    //   139: iload_3
    //   140: putfield 284	com/oneplus/camera/media/YuvToBitmapWorker:m_NV21AllocationHeight	I
    //   143: aload_0
    //   144: getfield 286	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaAllocation	Landroid/renderscript/Allocation;
    //   147: ifnull +10 -> 157
    //   150: aload_0
    //   151: getfield 286	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaAllocation	Landroid/renderscript/Allocation;
    //   154: invokevirtual 259	android/renderscript/Allocation:destroy	()V
    //   157: aload_0
    //   158: getfield 288	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaBitmap	Landroid/graphics/Bitmap;
    //   161: ifnull +10 -> 171
    //   164: aload_0
    //   165: getfield 288	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaBitmap	Landroid/graphics/Bitmap;
    //   168: invokevirtual 291	android/graphics/Bitmap:recycle	()V
    //   171: aload_0
    //   172: iload_2
    //   173: iload_3
    //   174: getstatic 296	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   177: invokestatic 209	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   180: putfield 288	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaBitmap	Landroid/graphics/Bitmap;
    //   183: aload_0
    //   184: aload_0
    //   185: getfield 238	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScript	Landroid/renderscript/RenderScript;
    //   188: aload_0
    //   189: getfield 288	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaBitmap	Landroid/graphics/Bitmap;
    //   192: invokestatic 300	android/renderscript/Allocation:createFromBitmap	(Landroid/renderscript/RenderScript;Landroid/graphics/Bitmap;)Landroid/renderscript/Allocation;
    //   195: putfield 286	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaAllocation	Landroid/renderscript/Allocation;
    //   198: aload_0
    //   199: getfield 113	com/oneplus/camera/media/YuvToBitmapWorker:m_BitmapSrcRect	Landroid/graphics/Rect;
    //   202: iconst_0
    //   203: iconst_0
    //   204: iload_2
    //   205: iload_3
    //   206: invokevirtual 304	android/graphics/Rect:set	(IIII)V
    //   209: aload_0
    //   210: getfield 306	com/oneplus/camera/media/YuvToBitmapWorker:m_YuvToRgbaScript	Landroid/renderscript/ScriptIntrinsicYuvToRGB;
    //   213: ifnonnull +21 -> 234
    //   216: aload_0
    //   217: aload_0
    //   218: getfield 238	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScript	Landroid/renderscript/RenderScript;
    //   221: aload_0
    //   222: getfield 238	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScript	Landroid/renderscript/RenderScript;
    //   225: invokestatic 267	android/renderscript/Element:U8	(Landroid/renderscript/RenderScript;)Landroid/renderscript/Element;
    //   228: invokestatic 311	android/renderscript/ScriptIntrinsicYuvToRGB:create	(Landroid/renderscript/RenderScript;Landroid/renderscript/Element;)Landroid/renderscript/ScriptIntrinsicYuvToRGB;
    //   231: putfield 306	com/oneplus/camera/media/YuvToBitmapWorker:m_YuvToRgbaScript	Landroid/renderscript/ScriptIntrinsicYuvToRGB;
    //   234: aload_0
    //   235: getfield 252	com/oneplus/camera/media/YuvToBitmapWorker:m_NV21Allocation	Landroid/renderscript/Allocation;
    //   238: aload_1
    //   239: invokevirtual 315	android/renderscript/Allocation:copyFrom	([B)V
    //   242: aload_0
    //   243: getfield 306	com/oneplus/camera/media/YuvToBitmapWorker:m_YuvToRgbaScript	Landroid/renderscript/ScriptIntrinsicYuvToRGB;
    //   246: aload_0
    //   247: getfield 252	com/oneplus/camera/media/YuvToBitmapWorker:m_NV21Allocation	Landroid/renderscript/Allocation;
    //   250: invokevirtual 319	android/renderscript/ScriptIntrinsicYuvToRGB:setInput	(Landroid/renderscript/Allocation;)V
    //   253: aload_0
    //   254: getfield 306	com/oneplus/camera/media/YuvToBitmapWorker:m_YuvToRgbaScript	Landroid/renderscript/ScriptIntrinsicYuvToRGB;
    //   257: aload_0
    //   258: getfield 286	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaAllocation	Landroid/renderscript/Allocation;
    //   261: invokevirtual 322	android/renderscript/ScriptIntrinsicYuvToRGB:forEach	(Landroid/renderscript/Allocation;)V
    //   264: aload_0
    //   265: monitorenter
    //   266: aload_0
    //   267: getfield 173	com/oneplus/camera/media/YuvToBitmapWorker:m_InputWidth	I
    //   270: iload_2
    //   271: if_icmpeq +11 -> 282
    //   274: aload_0
    //   275: getfield 175	com/oneplus/camera/media/YuvToBitmapWorker:m_InputHeight	I
    //   278: iload_3
    //   279: if_icmpne +14 -> 293
    //   282: aload_0
    //   283: getfield 190	com/oneplus/camera/media/YuvToBitmapWorker:m_NV21Buffers	Ljava/util/Queue;
    //   286: aload_1
    //   287: invokeinterface 203 2 0
    //   292: pop
    //   293: aload_0
    //   294: monitorexit
    //   295: aload_0
    //   296: monitorenter
    //   297: aload_0
    //   298: getfield 195	com/oneplus/camera/media/YuvToBitmapWorker:m_FreeBitmaps	Ljava/util/Queue;
    //   301: invokeinterface 326 1 0
    //   306: checkcast 205	android/graphics/Bitmap
    //   309: astore_1
    //   310: aload_0
    //   311: monitorexit
    //   312: aload_1
    //   313: ifnonnull +38 -> 351
    //   316: ldc 29
    //   318: ldc_w 328
    //   321: invokestatic 160	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   324: return
    //   325: astore_1
    //   326: aload_0
    //   327: monitorexit
    //   328: aload_1
    //   329: athrow
    //   330: aload_0
    //   331: getfield 284	com/oneplus/camera/media/YuvToBitmapWorker:m_NV21AllocationHeight	I
    //   334: iload_3
    //   335: if_icmpeq -126 -> 209
    //   338: goto -264 -> 74
    //   341: astore_1
    //   342: aload_0
    //   343: monitorexit
    //   344: aload_1
    //   345: athrow
    //   346: astore_1
    //   347: aload_0
    //   348: monitorexit
    //   349: aload_1
    //   350: athrow
    //   351: aload_1
    //   352: invokevirtual 332	android/graphics/Bitmap:getWidth	()I
    //   355: iload_2
    //   356: if_icmpne +97 -> 453
    //   359: aload_1
    //   360: invokevirtual 335	android/graphics/Bitmap:getHeight	()I
    //   363: iload_3
    //   364: if_icmpne +89 -> 453
    //   367: aload_1
    //   368: invokevirtual 339	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
    //   371: getstatic 296	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   374: if_acmpne +79 -> 453
    //   377: aload_0
    //   378: getfield 286	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaAllocation	Landroid/renderscript/Allocation;
    //   381: aload_1
    //   382: invokevirtual 343	android/renderscript/Allocation:copyTo	(Landroid/graphics/Bitmap;)V
    //   385: aload_0
    //   386: monitorenter
    //   387: aload_1
    //   388: invokevirtual 332	android/graphics/Bitmap:getWidth	()I
    //   391: aload_0
    //   392: getfield 179	com/oneplus/camera/media/YuvToBitmapWorker:m_OutputWidth	I
    //   395: if_icmpne +293 -> 688
    //   398: aload_1
    //   399: invokevirtual 335	android/graphics/Bitmap:getHeight	()I
    //   402: aload_0
    //   403: getfield 181	com/oneplus/camera/media/YuvToBitmapWorker:m_OutputHeight	I
    //   406: if_icmpne +282 -> 688
    //   409: aload_1
    //   410: invokevirtual 339	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
    //   413: aload_0
    //   414: getfield 177	com/oneplus/camera/media/YuvToBitmapWorker:m_OutputConfig	Landroid/graphics/Bitmap$Config;
    //   417: if_acmpne +271 -> 688
    //   420: aload_0
    //   421: getfield 197	com/oneplus/camera/media/YuvToBitmapWorker:m_ConvertedBitmaps	Ljava/util/Queue;
    //   424: aload_1
    //   425: invokeinterface 203 2 0
    //   430: pop
    //   431: aload_0
    //   432: getfield 345	com/oneplus/camera/media/YuvToBitmapWorker:m_OnBitmapAvailableListenerHandler	Landroid/os/Handler;
    //   435: ifnull +15 -> 450
    //   438: aload_0
    //   439: getfield 345	com/oneplus/camera/media/YuvToBitmapWorker:m_OnBitmapAvailableListenerHandler	Landroid/os/Handler;
    //   442: aload_0
    //   443: getfield 119	com/oneplus/camera/media/YuvToBitmapWorker:m_NotifyListenerRunnable	Ljava/lang/Runnable;
    //   446: invokevirtual 351	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   449: pop
    //   450: aload_0
    //   451: monitorexit
    //   452: return
    //   453: iload_2
    //   454: i2f
    //   455: aload_1
    //   456: invokevirtual 332	android/graphics/Bitmap:getWidth	()I
    //   459: i2f
    //   460: fdiv
    //   461: fconst_2
    //   462: fdiv
    //   463: fstore 4
    //   465: fload 4
    //   467: fconst_1
    //   468: fcmpl
    //   469: ifle +205 -> 674
    //   472: aload_0
    //   473: getfield 183	com/oneplus/camera/media/YuvToBitmapWorker:m_Flags	I
    //   476: iconst_1
    //   477: iand
    //   478: ifeq +196 -> 674
    //   481: aload_0
    //   482: getfield 353	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurAllocation	Landroid/renderscript/Allocation;
    //   485: ifnull +17 -> 502
    //   488: aload_0
    //   489: getfield 353	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurAllocation	Landroid/renderscript/Allocation;
    //   492: invokevirtual 356	android/renderscript/Allocation:getType	()Landroid/renderscript/Type;
    //   495: invokevirtual 361	android/renderscript/Type:getX	()I
    //   498: iload_2
    //   499: if_icmpeq +158 -> 657
    //   502: aload_0
    //   503: getfield 353	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurAllocation	Landroid/renderscript/Allocation;
    //   506: ifnull +10 -> 516
    //   509: aload_0
    //   510: getfield 353	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurAllocation	Landroid/renderscript/Allocation;
    //   513: invokevirtual 259	android/renderscript/Allocation:destroy	()V
    //   516: aload_0
    //   517: iload_2
    //   518: iload_3
    //   519: getstatic 296	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   522: invokestatic 209	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   525: putfield 363	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurRgbaBitmap	Landroid/graphics/Bitmap;
    //   528: aload_0
    //   529: aload_0
    //   530: getfield 238	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScript	Landroid/renderscript/RenderScript;
    //   533: aload_0
    //   534: getfield 363	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurRgbaBitmap	Landroid/graphics/Bitmap;
    //   537: invokestatic 300	android/renderscript/Allocation:createFromBitmap	(Landroid/renderscript/RenderScript;Landroid/graphics/Bitmap;)Landroid/renderscript/Allocation;
    //   540: putfield 353	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurAllocation	Landroid/renderscript/Allocation;
    //   543: aload_0
    //   544: getfield 365	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurScript	Landroid/renderscript/ScriptIntrinsicBlur;
    //   547: ifnonnull +21 -> 568
    //   550: aload_0
    //   551: aload_0
    //   552: getfield 238	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScript	Landroid/renderscript/RenderScript;
    //   555: aload_0
    //   556: getfield 238	com/oneplus/camera/media/YuvToBitmapWorker:m_RenderScript	Landroid/renderscript/RenderScript;
    //   559: invokestatic 368	android/renderscript/Element:RGBA_8888	(Landroid/renderscript/RenderScript;)Landroid/renderscript/Element;
    //   562: invokestatic 373	android/renderscript/ScriptIntrinsicBlur:create	(Landroid/renderscript/RenderScript;Landroid/renderscript/Element;)Landroid/renderscript/ScriptIntrinsicBlur;
    //   565: putfield 365	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurScript	Landroid/renderscript/ScriptIntrinsicBlur;
    //   568: aload_0
    //   569: getfield 365	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurScript	Landroid/renderscript/ScriptIntrinsicBlur;
    //   572: fload 4
    //   574: invokevirtual 377	android/renderscript/ScriptIntrinsicBlur:setRadius	(F)V
    //   577: aload_0
    //   578: getfield 365	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurScript	Landroid/renderscript/ScriptIntrinsicBlur;
    //   581: aload_0
    //   582: getfield 286	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaAllocation	Landroid/renderscript/Allocation;
    //   585: invokevirtual 378	android/renderscript/ScriptIntrinsicBlur:setInput	(Landroid/renderscript/Allocation;)V
    //   588: aload_0
    //   589: getfield 365	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurScript	Landroid/renderscript/ScriptIntrinsicBlur;
    //   592: aload_0
    //   593: getfield 353	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurAllocation	Landroid/renderscript/Allocation;
    //   596: invokevirtual 379	android/renderscript/ScriptIntrinsicBlur:forEach	(Landroid/renderscript/Allocation;)V
    //   599: aload_0
    //   600: getfield 353	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurAllocation	Landroid/renderscript/Allocation;
    //   603: aload_0
    //   604: getfield 288	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaBitmap	Landroid/graphics/Bitmap;
    //   607: invokevirtual 343	android/renderscript/Allocation:copyTo	(Landroid/graphics/Bitmap;)V
    //   610: aload_0
    //   611: getfield 106	com/oneplus/camera/media/YuvToBitmapWorker:m_BitmapDestRect	Landroid/graphics/Rect;
    //   614: iconst_0
    //   615: iconst_0
    //   616: aload_1
    //   617: invokevirtual 332	android/graphics/Bitmap:getWidth	()I
    //   620: aload_1
    //   621: invokevirtual 335	android/graphics/Bitmap:getHeight	()I
    //   624: invokevirtual 304	android/graphics/Rect:set	(IIII)V
    //   627: new 381	android/graphics/Canvas
    //   630: dup
    //   631: aload_1
    //   632: invokespecial 383	android/graphics/Canvas:<init>	(Landroid/graphics/Bitmap;)V
    //   635: aload_0
    //   636: getfield 288	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaBitmap	Landroid/graphics/Bitmap;
    //   639: aload_0
    //   640: getfield 113	com/oneplus/camera/media/YuvToBitmapWorker:m_BitmapSrcRect	Landroid/graphics/Rect;
    //   643: aload_0
    //   644: getfield 106	com/oneplus/camera/media/YuvToBitmapWorker:m_BitmapDestRect	Landroid/graphics/Rect;
    //   647: aload_0
    //   648: getfield 111	com/oneplus/camera/media/YuvToBitmapWorker:m_BitmapScalePaint	Landroid/graphics/Paint;
    //   651: invokevirtual 387	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;Landroid/graphics/Rect;Landroid/graphics/Rect;Landroid/graphics/Paint;)V
    //   654: goto -269 -> 385
    //   657: aload_0
    //   658: getfield 353	com/oneplus/camera/media/YuvToBitmapWorker:m_BlurAllocation	Landroid/renderscript/Allocation;
    //   661: invokevirtual 356	android/renderscript/Allocation:getType	()Landroid/renderscript/Type;
    //   664: invokevirtual 390	android/renderscript/Type:getY	()I
    //   667: iload_3
    //   668: if_icmpeq -125 -> 543
    //   671: goto -169 -> 502
    //   674: aload_0
    //   675: getfield 286	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaAllocation	Landroid/renderscript/Allocation;
    //   678: aload_0
    //   679: getfield 288	com/oneplus/camera/media/YuvToBitmapWorker:m_RgbaBitmap	Landroid/graphics/Bitmap;
    //   682: invokevirtual 343	android/renderscript/Allocation:copyTo	(Landroid/graphics/Bitmap;)V
    //   685: goto -75 -> 610
    //   688: ldc 29
    //   690: ldc_w 392
    //   693: invokestatic 160	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   696: aload_1
    //   697: invokevirtual 291	android/graphics/Bitmap:recycle	()V
    //   700: goto -250 -> 450
    //   703: astore_1
    //   704: aload_0
    //   705: monitorexit
    //   706: aload_1
    //   707: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	708	0	this	YuvToBitmapWorker
    //   0	708	1	paramArrayOfByte	byte[]
    //   0	708	2	paramInt1	int
    //   0	708	3	paramInt2	int
    //   463	110	4	f	float
    //   106	16	5	localBuilder	android.renderscript.Type.Builder
    // Exception table:
    //   from	to	target	type
    //   2	18	325	finally
    //   18	25	325	finally
    //   266	282	341	finally
    //   282	293	341	finally
    //   297	310	346	finally
    //   387	450	703	finally
    //   688	700	703	finally
  }
  
  /* Error */
  private void notifyListener()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 115	com/oneplus/camera/media/YuvToBitmapWorker:m_IsActive	Z
    //   6: ifeq +24 -> 30
    //   9: aload_0
    //   10: getfield 394	com/oneplus/camera/media/YuvToBitmapWorker:m_OnBitmapAvailableListener	Lcom/oneplus/camera/media/YuvToBitmapWorker$OnBitmapAvailableListener;
    //   13: ifnull +17 -> 30
    //   16: aload_0
    //   17: getfield 197	com/oneplus/camera/media/YuvToBitmapWorker:m_ConvertedBitmaps	Ljava/util/Queue;
    //   20: invokeinterface 398 1 0
    //   25: istore_1
    //   26: iload_1
    //   27: ifeq +6 -> 33
    //   30: aload_0
    //   31: monitorexit
    //   32: return
    //   33: aload_0
    //   34: getfield 394	com/oneplus/camera/media/YuvToBitmapWorker:m_OnBitmapAvailableListener	Lcom/oneplus/camera/media/YuvToBitmapWorker$OnBitmapAvailableListener;
    //   37: aload_0
    //   38: invokeinterface 401 2 0
    //   43: goto -13 -> 30
    //   46: astore_2
    //   47: aload_0
    //   48: monitorexit
    //   49: aload_2
    //   50: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	51	0	this	YuvToBitmapWorker
    //   25	2	1	bool	boolean
    //   46	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	26	46	finally
    //   33	43	46	finally
  }
  
  private void quitWorkerThread()
  {
    Log.w("YuvToBitmapWorker", "quitWorkerThread()");
    this.m_RenderScriptHandle = Handle.close(this.m_RenderScriptHandle);
    this.m_RenderScript = null;
    if (this.m_RgbaBitmap != null)
    {
      this.m_RgbaBitmap.recycle();
      this.m_RgbaBitmap = null;
    }
    Looper.myLooper().quit();
  }
  
  public Bitmap acquireLastBitmap()
  {
    for (;;)
    {
      try
      {
        if (!this.m_IsActive) {
          break label94;
        }
        if (this.m_ConvertedBitmaps.size() <= 1) {
          break;
        }
        Bitmap localBitmap1 = (Bitmap)this.m_ConvertedBitmaps.remove();
        if (this.m_FreeBitmaps.size() < this.m_BufferCount) {
          this.m_FreeBitmaps.add(localBitmap1);
        } else {
          ((Bitmap)localObject).recycle();
        }
      }
      finally {}
    }
    Bitmap localBitmap2 = (Bitmap)this.m_ConvertedBitmaps.poll();
    return localBitmap2;
    label94:
    return null;
  }
  
  public Bitmap acquireNextBitmap()
  {
    try
    {
      if (this.m_IsActive)
      {
        Bitmap localBitmap = (Bitmap)this.m_ConvertedBitmaps.poll();
        return localBitmap;
      }
      return null;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void addMultiPlaneYuvFrame(ImagePlane[] paramArrayOfImagePlane)
  {
    try
    {
      if (!this.m_IsActive)
      {
        Log.e("YuvToBitmapWorker", "addMultiPlaneYuvFrame() - Worker has been closed");
        return;
      }
      if ((paramArrayOfImagePlane == null) || (paramArrayOfImagePlane.length != 3))
      {
        Log.e("YuvToBitmapWorker", "addMultiPlaneYuvFrame() - Invalid YUV planes");
        return;
      }
      if ((this.m_InputWidth <= 0) || (this.m_InputHeight <= 0))
      {
        Log.w("YuvToBitmapWorker", "addMultiPlaneYuvFrame() - Input size is 0, ignore");
        return;
      }
      byte[] arrayOfByte = (byte[])this.m_NV21Buffers.poll();
      if (arrayOfByte == null)
      {
        Log.w("YuvToBitmapWorker", "addMultiPlaneYuvFrame() - No available internal NV21 buffer");
        return;
      }
      YuvUtils.multiPlaneYuvToNV21(paramArrayOfImagePlane, arrayOfByte, this.m_InputWidth, this.m_InputHeight);
      Message.obtain(this.m_WorkerThreadHandler, 10000, this.m_InputWidth, this.m_InputHeight, arrayOfByte).sendToTarget();
      return;
    }
    finally {}
  }
  
  public void addNV21Frame(byte[] paramArrayOfByte)
  {
    try
    {
      if (!this.m_IsActive)
      {
        Log.e("YuvToBitmapWorker", "addNV21Frame() - Worker has been closed");
        return;
      }
      int i = this.m_InputWidth * this.m_InputHeight * 3 / 2;
      if (i <= 0)
      {
        Log.w("YuvToBitmapWorker", "addNV21Frame() - Input size is 0, ignore");
        return;
      }
      if ((paramArrayOfByte == null) || (paramArrayOfByte.length < i))
      {
        Log.e("YuvToBitmapWorker", "addNV21Frame() - Invalid YUV data size");
        return;
      }
      byte[] arrayOfByte = (byte[])this.m_NV21Buffers.poll();
      if (arrayOfByte == null)
      {
        Log.w("YuvToBitmapWorker", "addNV21Frame() - No available internal NV21 buffer");
        return;
      }
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, i);
      Message.obtain(this.m_WorkerThreadHandler, 10000, this.m_InputWidth, this.m_InputHeight, arrayOfByte).sendToTarget();
      return;
    }
    finally {}
  }
  
  public void addNV21Frame(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    for (;;)
    {
      try
      {
        if (!this.m_IsActive)
        {
          Log.e("YuvToBitmapWorker", "addNV21Frame() - Worker has been closed");
          return;
        }
        if ((paramInt1 != this.m_InputWidth) || (paramInt2 != this.m_InputHeight))
        {
          byte[] arrayOfByte = (byte[])this.m_NV21Buffers.poll();
          if (arrayOfByte == null)
          {
            Log.w("YuvToBitmapWorker", "addNV21Frame() - No available internal NV21 buffer");
            return;
          }
          if (ImageUtils.scaleNV21Image(paramArrayOfByte, paramInt1, paramInt2, arrayOfByte, this.m_InputWidth, this.m_InputHeight))
          {
            Message.obtain(this.m_WorkerThreadHandler, 10000, this.m_InputWidth, this.m_InputHeight, arrayOfByte).sendToTarget();
            return;
          }
          Log.e("YuvToBitmapWorker", "addNV21Frame() - Failed to scale NV21 image.");
          this.m_NV21Buffers.add(arrayOfByte);
          continue;
        }
        addNV21Frame(paramArrayOfByte);
      }
      finally {}
    }
  }
  
  public void close()
  {
    try
    {
      boolean bool = this.m_IsActive;
      if (!bool) {
        return;
      }
      this.m_IsActive = false;
      this.m_WorkerThreadHandler.sendMessageAtFrontOfQueue(Message.obtain(this.m_WorkerThreadHandler, 10001));
      this.m_OnBitmapAvailableListener = null;
      this.m_NV21Buffers.clear();
      this.m_FreeBitmaps.clear();
      this.m_ConvertedBitmaps.clear();
      return;
    }
    finally {}
  }
  
  public int getInputHeight()
  {
    return this.m_InputHeight;
  }
  
  public int getInputWidth()
  {
    return this.m_InputWidth;
  }
  
  public Bitmap.Config getOutputConfig()
  {
    return this.m_OutputConfig;
  }
  
  public int getOutputHeight()
  {
    return this.m_OutputHeight;
  }
  
  public int getOutputWidth()
  {
    return this.m_OutputWidth;
  }
  
  public void reconfigureInput(int paramInt1, int paramInt2)
  {
    try
    {
      if (!this.m_IsActive) {
        throw new RuntimeException("Access closed worker.");
      }
    }
    finally {}
    if ((paramInt1 < 0) || (paramInt2 < 0)) {}
    while (((paramInt1 & 0x1) != 0) || ((paramInt2 & 0x1) != 0)) {
      throw new IllegalArgumentException("Invalid input size : " + paramInt1 + "x" + paramInt2 + ".");
    }
    if (this.m_InputWidth == paramInt1)
    {
      i = this.m_InputHeight;
      if (i == paramInt2) {
        return;
      }
    }
    Log.w("YuvToBitmapWorker", "reconfigureInput() - Size : " + paramInt1 + "x" + paramInt2);
    int i = paramInt1 * paramInt2 * 3 / 2;
    this.m_InputWidth = paramInt1;
    this.m_InputHeight = paramInt2;
    this.m_NV21Buffers.clear();
    paramInt1 = this.m_BufferCount;
    while (paramInt1 > 0)
    {
      this.m_NV21Buffers.add(new byte[i]);
      paramInt1 -= 1;
    }
  }
  
  public void reconfigureOutput(int paramInt1, int paramInt2)
  {
    try
    {
      reconfigureOutput(paramInt1, paramInt2, this.m_OutputConfig);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void reconfigureOutput(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    try
    {
      if (!this.m_IsActive) {
        throw new RuntimeException("Access closed worker.");
      }
    }
    finally {}
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new IllegalArgumentException("Invalid output size : " + paramInt1 + "x" + paramInt2 + ".");
    }
    if (paramConfig == null) {
      throw new IllegalArgumentException("No output configuration.");
    }
    if ((this.m_OutputWidth == paramInt1) && (this.m_OutputHeight == paramInt2))
    {
      Bitmap.Config localConfig = this.m_OutputConfig;
      if (localConfig == paramConfig) {
        return;
      }
    }
    Log.w("YuvToBitmapWorker", "reconfigureOutput() - Size : " + paramInt1 + "x" + paramInt2 + ", config : " + paramConfig);
    this.m_OutputConfig = paramConfig;
    this.m_OutputWidth = paramInt1;
    this.m_OutputHeight = paramInt2;
    this.m_FreeBitmaps.clear();
    this.m_ConvertedBitmaps.clear();
    int i = this.m_BufferCount;
    while (i > 0)
    {
      this.m_FreeBitmaps.add(Bitmap.createBitmap(paramInt1, paramInt2, paramConfig));
      i -= 1;
    }
  }
  
  /* Error */
  public void releaseBitmap(Bitmap paramBitmap)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: ifnonnull +6 -> 9
    //   6: aload_0
    //   7: monitorexit
    //   8: return
    //   9: aload_0
    //   10: getfield 115	com/oneplus/camera/media/YuvToBitmapWorker:m_IsActive	Z
    //   13: ifeq +66 -> 79
    //   16: aload_0
    //   17: getfield 195	com/oneplus/camera/media/YuvToBitmapWorker:m_FreeBitmaps	Ljava/util/Queue;
    //   20: invokeinterface 422 1 0
    //   25: aload_0
    //   26: getfield 169	com/oneplus/camera/media/YuvToBitmapWorker:m_BufferCount	I
    //   29: if_icmpge +50 -> 79
    //   32: aload_1
    //   33: invokevirtual 332	android/graphics/Bitmap:getWidth	()I
    //   36: aload_0
    //   37: getfield 179	com/oneplus/camera/media/YuvToBitmapWorker:m_OutputWidth	I
    //   40: if_icmpne +39 -> 79
    //   43: aload_1
    //   44: invokevirtual 335	android/graphics/Bitmap:getHeight	()I
    //   47: aload_0
    //   48: getfield 181	com/oneplus/camera/media/YuvToBitmapWorker:m_OutputHeight	I
    //   51: if_icmpne +28 -> 79
    //   54: aload_1
    //   55: invokevirtual 339	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
    //   58: aload_0
    //   59: getfield 177	com/oneplus/camera/media/YuvToBitmapWorker:m_OutputConfig	Landroid/graphics/Bitmap$Config;
    //   62: if_acmpne +17 -> 79
    //   65: aload_0
    //   66: getfield 195	com/oneplus/camera/media/YuvToBitmapWorker:m_FreeBitmaps	Ljava/util/Queue;
    //   69: aload_1
    //   70: invokeinterface 203 2 0
    //   75: pop
    //   76: aload_0
    //   77: monitorexit
    //   78: return
    //   79: aload_1
    //   80: invokevirtual 291	android/graphics/Bitmap:recycle	()V
    //   83: goto -7 -> 76
    //   86: astore_1
    //   87: aload_0
    //   88: monitorexit
    //   89: aload_1
    //   90: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	91	0	this	YuvToBitmapWorker
    //   0	91	1	paramBitmap	Bitmap
    // Exception table:
    //   from	to	target	type
    //   9	76	86	finally
    //   79	83	86	finally
  }
  
  /* Error */
  public void setOnBitmapAvailableListener(OnBitmapAvailableListener paramOnBitmapAvailableListener, Handler paramHandler)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 115	com/oneplus/camera/media/YuvToBitmapWorker:m_IsActive	Z
    //   6: ifeq +17 -> 23
    //   9: aload_0
    //   10: aload_1
    //   11: putfield 394	com/oneplus/camera/media/YuvToBitmapWorker:m_OnBitmapAvailableListener	Lcom/oneplus/camera/media/YuvToBitmapWorker$OnBitmapAvailableListener;
    //   14: aload_2
    //   15: ifnull +11 -> 26
    //   18: aload_0
    //   19: aload_2
    //   20: putfield 345	com/oneplus/camera/media/YuvToBitmapWorker:m_OnBitmapAvailableListenerHandler	Landroid/os/Handler;
    //   23: aload_0
    //   24: monitorexit
    //   25: return
    //   26: new 347	android/os/Handler
    //   29: dup
    //   30: invokespecial 515	android/os/Handler:<init>	()V
    //   33: astore_2
    //   34: goto -16 -> 18
    //   37: astore_1
    //   38: aload_0
    //   39: monitorexit
    //   40: aload_1
    //   41: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	42	0	this	YuvToBitmapWorker
    //   0	42	1	paramOnBitmapAvailableListener	OnBitmapAvailableListener
    //   0	42	2	paramHandler	Handler
    // Exception table:
    //   from	to	target	type
    //   2	14	37	finally
    //   18	23	37	finally
    //   26	34	37	finally
  }
  
  public static abstract interface OnBitmapAvailableListener
  {
    public abstract void onBitmapAvailable(YuvToBitmapWorker paramYuvToBitmapWorker);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/YuvToBitmapWorker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */