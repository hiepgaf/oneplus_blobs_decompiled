package com.oneplus.camera.io;

import android.content.ContentValues;
import android.content.Context;
import android.hardware.camera2.DngCreator;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import com.oneplus.base.Log;
import com.oneplus.camera.CameraCaptureEventArgs;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.camera.media.ImagePlane;
import com.oneplus.io.Path;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RawPhotoSaveTask
  extends MediaSaveTask
{
  protected final CameraCaptureEventArgs m_CaptureEventArgs;
  protected final Context m_Context;
  private DngCreator m_DngCreator;
  private Long m_MediaSize;
  private final String m_PictureId;
  private final long m_TakenTime;
  
  public RawPhotoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, CameraCaptureEventArgs paramCameraCaptureEventArgs, DngCreator paramDngCreator)
  {
    super(paramContext, paramCaptureHandle);
    this.m_Context = paramContext;
    this.m_DngCreator = paramDngCreator;
    if (paramCameraCaptureEventArgs != null)
    {
      this.m_CaptureEventArgs = paramCameraCaptureEventArgs.clone();
      this.m_PictureId = paramCameraCaptureEventArgs.getPictureId();
      this.m_TakenTime = paramCameraCaptureEventArgs.getTakenTime();
      return;
    }
    this.m_CaptureEventArgs = null;
    this.m_PictureId = null;
    this.m_TakenTime = System.currentTimeMillis();
  }
  
  public long getMediaSize()
  {
    if (this.m_MediaSize == null) {}
    try
    {
      this.m_MediaSize = Long.valueOf(this.m_CaptureEventArgs.getPicturePlanes()[0].getData().length);
      return this.m_MediaSize.longValue();
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        this.m_MediaSize = Long.valueOf(0L);
      }
    }
  }
  
  public String getPictureId()
  {
    return this.m_PictureId;
  }
  
  protected String onGenerateFilePath(boolean paramBoolean)
  {
    File localFile3 = new File(Path.combine(new String[] { getDcimPath(), "Camera" }));
    File localFile2;
    if ((localFile3.exists()) || (localFile3.mkdirs()))
    {
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
      File localFile1 = new File(localFile3, "IMG_" + localSimpleDateFormat.format(new Date(this.m_TakenTime)) + ".dng");
      localFile2 = localFile1;
      if (paramBoolean)
      {
        int i = 1;
        for (;;)
        {
          localFile2 = localFile1;
          if (!localFile1.exists()) {
            break;
          }
          localFile1 = new File(localFile3, "IMG_" + localSimpleDateFormat.format(new Date(this.m_TakenTime)) + "_" + String.format(Locale.US, "%02d", new Object[] { Integer.valueOf(i) }) + ".dng");
          i += 1;
        }
      }
    }
    else
    {
      Log.e(this.TAG, "onGenerateFilePath() - Fail to create " + localFile3.getAbsolutePath());
      return null;
    }
    Log.w(this.TAG, "onGenerateFilePath() - File path : " + localFile2);
    return localFile2.getAbsolutePath();
  }
  
  /* Error */
  protected Uri onInsertToMediaStore(String paramString, final ContentValues paramContentValues)
  {
    // Byte code:
    //   0: iconst_1
    //   1: anewarray 185	android/net/Uri
    //   4: astore_2
    //   5: aload_2
    //   6: iconst_0
    //   7: aconst_null
    //   8: aastore
    //   9: aload_2
    //   10: monitorenter
    //   11: aload_0
    //   12: getfield 25	com/oneplus/camera/io/RawPhotoSaveTask:m_Context	Landroid/content/Context;
    //   15: astore_3
    //   16: new 6	com/oneplus/camera/io/RawPhotoSaveTask$1
    //   19: dup
    //   20: aload_0
    //   21: aload_2
    //   22: invokespecial 188	com/oneplus/camera/io/RawPhotoSaveTask$1:<init>	(Lcom/oneplus/camera/io/RawPhotoSaveTask;[Landroid/net/Uri;)V
    //   25: astore 4
    //   27: aload_3
    //   28: iconst_1
    //   29: anewarray 83	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: aload_1
    //   35: aastore
    //   36: iconst_1
    //   37: anewarray 83	java/lang/String
    //   40: dup
    //   41: iconst_0
    //   42: ldc -66
    //   44: aastore
    //   45: aload 4
    //   47: invokestatic 196	android/media/MediaScannerConnection:scanFile	(Landroid/content/Context;[Ljava/lang/String;[Ljava/lang/String;Landroid/media/MediaScannerConnection$OnScanCompletedListener;)V
    //   50: aload_0
    //   51: getfield 160	com/oneplus/camera/io/RawPhotoSaveTask:TAG	Ljava/lang/String;
    //   54: ldc -58
    //   56: invokestatic 179	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   59: aload_2
    //   60: ldc2_w 199
    //   63: invokevirtual 203	java/lang/Object:wait	(J)V
    //   66: aload_0
    //   67: getfield 160	com/oneplus/camera/io/RawPhotoSaveTask:TAG	Ljava/lang/String;
    //   70: ldc -51
    //   72: invokestatic 179	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   75: aload_2
    //   76: iconst_0
    //   77: aaload
    //   78: astore_1
    //   79: aload_2
    //   80: monitorexit
    //   81: aload_1
    //   82: areturn
    //   83: astore_1
    //   84: aload_0
    //   85: getfield 160	com/oneplus/camera/io/RawPhotoSaveTask:TAG	Ljava/lang/String;
    //   88: ldc -49
    //   90: aload_1
    //   91: invokestatic 210	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   94: aload_2
    //   95: monitorexit
    //   96: aconst_null
    //   97: areturn
    //   98: astore_1
    //   99: aload_2
    //   100: monitorexit
    //   101: aload_1
    //   102: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	103	0	this	RawPhotoSaveTask
    //   0	103	1	paramString	String
    //   0	103	2	paramContentValues	ContentValues
    //   15	13	3	localContext	Context
    //   25	21	4	local1	1
    // Exception table:
    //   from	to	target	type
    //   50	75	83	java/lang/InterruptedException
    //   11	50	98	finally
    //   50	75	98	finally
    //   84	94	98	finally
  }
  
  protected boolean onPrepareGalleryDatabaseValues(String paramString, Uri paramUri, ContentValues paramContentValues)
  {
    return false;
  }
  
  protected boolean onPrepareMediaStoreValues(String paramString, ContentValues paramContentValues)
  {
    return true;
  }
  
  /* Error */
  protected boolean onSaveToFile(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aconst_null
    //   4: astore 10
    //   6: aconst_null
    //   7: astore 11
    //   9: aconst_null
    //   10: astore 9
    //   12: aload_0
    //   13: getfield 160	com/oneplus/camera/io/RawPhotoSaveTask:TAG	Ljava/lang/String;
    //   16: ldc -38
    //   18: invokestatic 179	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   21: new 81	java/io/File
    //   24: dup
    //   25: aload_1
    //   26: invokespecial 97	java/io/File:<init>	(Ljava/lang/String;)V
    //   29: astore 15
    //   31: aconst_null
    //   32: astore 6
    //   34: aconst_null
    //   35: astore_2
    //   36: aconst_null
    //   37: astore 12
    //   39: aconst_null
    //   40: astore 13
    //   42: aconst_null
    //   43: astore 14
    //   45: aconst_null
    //   46: astore 7
    //   48: aconst_null
    //   49: astore_3
    //   50: aconst_null
    //   51: astore 4
    //   53: new 220	java/io/FileOutputStream
    //   56: dup
    //   57: aload 15
    //   59: invokespecial 223	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   62: astore 5
    //   64: aload 13
    //   66: astore_3
    //   67: aload 14
    //   69: astore 4
    //   71: aload_0
    //   72: getfield 27	com/oneplus/camera/io/RawPhotoSaveTask:m_DngCreator	Landroid/hardware/camera2/DngCreator;
    //   75: ifnonnull +127 -> 202
    //   78: aload 13
    //   80: astore_3
    //   81: aload 14
    //   83: astore 4
    //   85: aload_0
    //   86: getfield 160	com/oneplus/camera/io/RawPhotoSaveTask:TAG	Ljava/lang/String;
    //   89: ldc -31
    //   91: invokestatic 171	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   94: aload 9
    //   96: astore_2
    //   97: aload 5
    //   99: ifnull +14 -> 113
    //   102: aload 6
    //   104: astore_1
    //   105: aload 5
    //   107: invokevirtual 228	java/io/FileOutputStream:close	()V
    //   110: aload 9
    //   112: astore_2
    //   113: aload_2
    //   114: ifnull +79 -> 193
    //   117: aload 7
    //   119: astore_3
    //   120: aload 6
    //   122: astore_1
    //   123: aload_2
    //   124: athrow
    //   125: astore_2
    //   126: aload_3
    //   127: astore_1
    //   128: aload_1
    //   129: astore_3
    //   130: aload_0
    //   131: getfield 160	com/oneplus/camera/io/RawPhotoSaveTask:TAG	Ljava/lang/String;
    //   134: new 111	java/lang/StringBuilder
    //   137: dup
    //   138: invokespecial 114	java/lang/StringBuilder:<init>	()V
    //   141: ldc -26
    //   143: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: aload 15
    //   148: invokevirtual 176	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   151: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   154: aload_2
    //   155: invokestatic 210	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   158: aload_1
    //   159: astore_3
    //   160: aload_0
    //   161: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   164: ifnull +12 -> 176
    //   167: aload_1
    //   168: astore_3
    //   169: aload_0
    //   170: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   173: invokevirtual 233	com/oneplus/camera/CameraCaptureEventArgs:recycle	()V
    //   176: aload_1
    //   177: ifnull +3 -> 180
    //   180: aload_0
    //   181: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   184: invokevirtual 236	com/oneplus/camera/CameraCaptureEventArgs:clearImagePlane	()V
    //   187: iconst_0
    //   188: ireturn
    //   189: astore_2
    //   190: goto -77 -> 113
    //   193: aload_0
    //   194: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   197: invokevirtual 236	com/oneplus/camera/CameraCaptureEventArgs:clearImagePlane	()V
    //   200: iconst_0
    //   201: ireturn
    //   202: aload 13
    //   204: astore_3
    //   205: aload 14
    //   207: astore 4
    //   209: aload_0
    //   210: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   213: ifnull +263 -> 476
    //   216: aload 13
    //   218: astore_3
    //   219: aload 14
    //   221: astore 4
    //   223: aload_0
    //   224: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   227: invokevirtual 240	com/oneplus/camera/CameraCaptureEventArgs:getPictureFormat	()I
    //   230: bipush 32
    //   232: if_icmpne +160 -> 392
    //   235: aload 13
    //   237: astore_3
    //   238: aload 14
    //   240: astore 4
    //   242: aload_0
    //   243: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   246: invokevirtual 62	com/oneplus/camera/CameraCaptureEventArgs:getPicturePlanes	()[Lcom/oneplus/camera/media/ImagePlane;
    //   249: iconst_0
    //   250: aaload
    //   251: invokevirtual 68	com/oneplus/camera/media/ImagePlane:getData	()[B
    //   254: astore_2
    //   255: aload_2
    //   256: astore_3
    //   257: aload_2
    //   258: astore 4
    //   260: new 242	java/io/ByteArrayOutputStream
    //   263: dup
    //   264: invokespecial 243	java/io/ByteArrayOutputStream:<init>	()V
    //   267: astore 6
    //   269: aload_2
    //   270: astore_3
    //   271: aload_2
    //   272: astore 4
    //   274: aload_0
    //   275: getfield 27	com/oneplus/camera/io/RawPhotoSaveTask:m_DngCreator	Landroid/hardware/camera2/DngCreator;
    //   278: aload 6
    //   280: aload_0
    //   281: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   284: invokevirtual 247	com/oneplus/camera/CameraCaptureEventArgs:getPictureSize	()Landroid/util/Size;
    //   287: aload_2
    //   288: invokestatic 253	java/nio/ByteBuffer:wrap	([B)Ljava/nio/ByteBuffer;
    //   291: lconst_0
    //   292: invokevirtual 259	android/hardware/camera2/DngCreator:writeByteBuffer	(Ljava/io/OutputStream;Landroid/util/Size;Ljava/nio/ByteBuffer;J)V
    //   295: aload_2
    //   296: astore_3
    //   297: aload_2
    //   298: astore 4
    //   300: aload 5
    //   302: aload 6
    //   304: invokevirtual 262	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   307: invokevirtual 266	java/io/FileOutputStream:write	([B)V
    //   310: aload_2
    //   311: astore_3
    //   312: aload_2
    //   313: astore 4
    //   315: aload_0
    //   316: getfield 160	com/oneplus/camera/io/RawPhotoSaveTask:TAG	Ljava/lang/String;
    //   319: new 111	java/lang/StringBuilder
    //   322: dup
    //   323: invokespecial 114	java/lang/StringBuilder:<init>	()V
    //   326: ldc_w 268
    //   329: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   332: aload_1
    //   333: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   336: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   339: invokestatic 179	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   342: aload 8
    //   344: astore 4
    //   346: aload 5
    //   348: ifnull +14 -> 362
    //   351: aload_2
    //   352: astore_1
    //   353: aload 5
    //   355: invokevirtual 228	java/io/FileOutputStream:close	()V
    //   358: aload 8
    //   360: astore 4
    //   362: aload 4
    //   364: ifnull +264 -> 628
    //   367: aload_2
    //   368: astore_3
    //   369: aload_2
    //   370: astore_1
    //   371: aload 4
    //   373: athrow
    //   374: astore_2
    //   375: aload_1
    //   376: astore_3
    //   377: aload_2
    //   378: astore_1
    //   379: aload_3
    //   380: ifnull +3 -> 383
    //   383: aload_0
    //   384: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   387: invokevirtual 236	com/oneplus/camera/CameraCaptureEventArgs:clearImagePlane	()V
    //   390: aload_1
    //   391: athrow
    //   392: aload 13
    //   394: astore_3
    //   395: aload 14
    //   397: astore 4
    //   399: aload_0
    //   400: getfield 160	com/oneplus/camera/io/RawPhotoSaveTask:TAG	Ljava/lang/String;
    //   403: new 111	java/lang/StringBuilder
    //   406: dup
    //   407: invokespecial 114	java/lang/StringBuilder:<init>	()V
    //   410: ldc_w 270
    //   413: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   416: aload_0
    //   417: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   420: invokevirtual 240	com/oneplus/camera/CameraCaptureEventArgs:getPictureFormat	()I
    //   423: invokevirtual 273	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   426: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   429: invokestatic 171	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   432: aload 10
    //   434: astore_2
    //   435: aload 5
    //   437: ifnull +14 -> 451
    //   440: aload 6
    //   442: astore_1
    //   443: aload 5
    //   445: invokevirtual 228	java/io/FileOutputStream:close	()V
    //   448: aload 10
    //   450: astore_2
    //   451: aload_2
    //   452: ifnull +15 -> 467
    //   455: aload 7
    //   457: astore_3
    //   458: aload 6
    //   460: astore_1
    //   461: aload_2
    //   462: athrow
    //   463: astore_2
    //   464: goto -13 -> 451
    //   467: aload_0
    //   468: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   471: invokevirtual 236	com/oneplus/camera/CameraCaptureEventArgs:clearImagePlane	()V
    //   474: iconst_0
    //   475: ireturn
    //   476: aload 13
    //   478: astore_3
    //   479: aload 14
    //   481: astore 4
    //   483: aload_0
    //   484: getfield 160	com/oneplus/camera/io/RawPhotoSaveTask:TAG	Ljava/lang/String;
    //   487: new 111	java/lang/StringBuilder
    //   490: dup
    //   491: invokespecial 114	java/lang/StringBuilder:<init>	()V
    //   494: ldc_w 275
    //   497: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   500: aload_1
    //   501: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   504: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   507: invokestatic 171	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   510: aload 11
    //   512: astore_2
    //   513: aload 5
    //   515: ifnull +14 -> 529
    //   518: aload 6
    //   520: astore_1
    //   521: aload 5
    //   523: invokevirtual 228	java/io/FileOutputStream:close	()V
    //   526: aload 11
    //   528: astore_2
    //   529: aload_2
    //   530: ifnull +15 -> 545
    //   533: aload 7
    //   535: astore_3
    //   536: aload 6
    //   538: astore_1
    //   539: aload_2
    //   540: athrow
    //   541: astore_2
    //   542: goto -13 -> 529
    //   545: aload_0
    //   546: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   549: invokevirtual 236	com/oneplus/camera/CameraCaptureEventArgs:clearImagePlane	()V
    //   552: iconst_0
    //   553: ireturn
    //   554: astore 4
    //   556: goto -194 -> 362
    //   559: astore_3
    //   560: aload 4
    //   562: astore 5
    //   564: aload_2
    //   565: astore_1
    //   566: aload_3
    //   567: astore_2
    //   568: aload_2
    //   569: athrow
    //   570: astore 4
    //   572: aload_2
    //   573: astore 6
    //   575: aload 5
    //   577: ifnull +13 -> 590
    //   580: aload_1
    //   581: astore_3
    //   582: aload 5
    //   584: invokevirtual 228	java/io/FileOutputStream:close	()V
    //   587: aload_2
    //   588: astore 6
    //   590: aload 6
    //   592: ifnull +31 -> 623
    //   595: aload_1
    //   596: astore_3
    //   597: aload 6
    //   599: athrow
    //   600: aload_2
    //   601: astore 6
    //   603: aload_2
    //   604: aload 5
    //   606: if_acmpeq -16 -> 590
    //   609: aload_1
    //   610: astore_3
    //   611: aload_2
    //   612: aload 5
    //   614: invokevirtual 279	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   617: aload_2
    //   618: astore 6
    //   620: goto -30 -> 590
    //   623: aload_1
    //   624: astore_3
    //   625: aload 4
    //   627: athrow
    //   628: aload_2
    //   629: ifnull +3 -> 632
    //   632: aload_0
    //   633: getfield 35	com/oneplus/camera/io/RawPhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   636: invokevirtual 236	com/oneplus/camera/CameraCaptureEventArgs:clearImagePlane	()V
    //   639: iconst_1
    //   640: ireturn
    //   641: astore 4
    //   643: aconst_null
    //   644: astore_2
    //   645: aload 12
    //   647: astore_1
    //   648: aload_3
    //   649: astore 5
    //   651: goto -79 -> 572
    //   654: astore 4
    //   656: aconst_null
    //   657: astore_2
    //   658: aload_3
    //   659: astore_1
    //   660: goto -88 -> 572
    //   663: astore_2
    //   664: aload 4
    //   666: astore_1
    //   667: goto -99 -> 568
    //   670: astore_2
    //   671: goto -543 -> 128
    //   674: astore 5
    //   676: aload_2
    //   677: ifnonnull -77 -> 600
    //   680: aload 5
    //   682: astore 6
    //   684: goto -94 -> 590
    //   687: astore_1
    //   688: goto -309 -> 379
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	691	0	this	RawPhotoSaveTask
    //   0	691	1	paramString	String
    //   35	89	2	localObject1	Object
    //   125	30	2	localThrowable1	Throwable
    //   189	1	2	localThrowable2	Throwable
    //   254	116	2	arrayOfByte	byte[]
    //   374	4	2	localObject2	Object
    //   434	28	2	localObject3	Object
    //   463	1	2	localThrowable3	Throwable
    //   512	28	2	localObject4	Object
    //   541	24	2	localThrowable4	Throwable
    //   567	91	2	localObject5	Object
    //   663	1	2	localThrowable5	Throwable
    //   670	7	2	localThrowable6	Throwable
    //   49	487	3	localObject6	Object
    //   559	8	3	localThrowable7	Throwable
    //   581	78	3	str	String
    //   51	431	4	localObject7	Object
    //   554	7	4	localThrowable8	Throwable
    //   570	56	4	localObject8	Object
    //   641	1	4	localObject9	Object
    //   654	11	4	localObject10	Object
    //   62	588	5	localObject11	Object
    //   674	7	5	localThrowable9	Throwable
    //   32	651	6	localObject12	Object
    //   46	488	7	localObject13	Object
    //   1	358	8	localObject14	Object
    //   10	101	9	localObject15	Object
    //   4	445	10	localObject16	Object
    //   7	520	11	localObject17	Object
    //   37	609	12	localObject18	Object
    //   40	437	13	localObject19	Object
    //   43	437	14	localObject20	Object
    //   29	118	15	localFile	File
    // Exception table:
    //   from	to	target	type
    //   123	125	125	java/lang/Throwable
    //   371	374	125	java/lang/Throwable
    //   461	463	125	java/lang/Throwable
    //   539	541	125	java/lang/Throwable
    //   105	110	189	java/lang/Throwable
    //   105	110	374	finally
    //   123	125	374	finally
    //   353	358	374	finally
    //   371	374	374	finally
    //   443	448	374	finally
    //   461	463	374	finally
    //   521	526	374	finally
    //   539	541	374	finally
    //   443	448	463	java/lang/Throwable
    //   521	526	541	java/lang/Throwable
    //   353	358	554	java/lang/Throwable
    //   53	64	559	java/lang/Throwable
    //   568	570	570	finally
    //   53	64	641	finally
    //   71	78	654	finally
    //   85	94	654	finally
    //   209	216	654	finally
    //   223	235	654	finally
    //   242	255	654	finally
    //   260	269	654	finally
    //   274	295	654	finally
    //   300	310	654	finally
    //   315	342	654	finally
    //   399	432	654	finally
    //   483	510	654	finally
    //   71	78	663	java/lang/Throwable
    //   85	94	663	java/lang/Throwable
    //   209	216	663	java/lang/Throwable
    //   223	235	663	java/lang/Throwable
    //   242	255	663	java/lang/Throwable
    //   260	269	663	java/lang/Throwable
    //   274	295	663	java/lang/Throwable
    //   300	310	663	java/lang/Throwable
    //   315	342	663	java/lang/Throwable
    //   399	432	663	java/lang/Throwable
    //   483	510	663	java/lang/Throwable
    //   597	600	670	java/lang/Throwable
    //   611	617	670	java/lang/Throwable
    //   625	628	670	java/lang/Throwable
    //   582	587	674	java/lang/Throwable
    //   130	158	687	finally
    //   160	167	687	finally
    //   169	176	687	finally
    //   582	587	687	finally
    //   597	600	687	finally
    //   611	617	687	finally
    //   625	628	687	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/io/RawPhotoSaveTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */