package android.media;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.IOException;

public class ThumbnailUtils
{
  private static final int MAX_NUM_PIXELS_MICRO_THUMBNAIL = 19200;
  private static final int MAX_NUM_PIXELS_THUMBNAIL = 196608;
  private static final int OPTIONS_NONE = 0;
  public static final int OPTIONS_RECYCLE_INPUT = 2;
  private static final int OPTIONS_SCALE_UP = 1;
  private static final String TAG = "ThumbnailUtils";
  public static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;
  public static final int TARGET_SIZE_MINI_THUMBNAIL = 320;
  private static final int UNCONSTRAINED = -1;
  
  private static void closeSilently(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    if (paramParcelFileDescriptor == null) {
      return;
    }
    try
    {
      paramParcelFileDescriptor.close();
      return;
    }
    catch (Throwable paramParcelFileDescriptor) {}
  }
  
  private static int computeInitialSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
  {
    double d1 = paramOptions.outWidth;
    double d2 = paramOptions.outHeight;
    int i;
    if (paramInt2 == -1)
    {
      i = 1;
      if (paramInt1 != -1) {
        break label60;
      }
    }
    label60:
    for (int j = 128;; j = (int)Math.min(Math.floor(d1 / paramInt1), Math.floor(d2 / paramInt1)))
    {
      if (j >= i) {
        break label84;
      }
      return i;
      i = (int)Math.ceil(Math.sqrt(d1 * d2 / paramInt2));
      break;
    }
    label84:
    if ((paramInt2 == -1) && (paramInt1 == -1)) {
      return 1;
    }
    if (paramInt1 == -1) {
      return i;
    }
    return j;
  }
  
  private static int computeSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
  {
    int i = computeInitialSampleSize(paramOptions, paramInt1, paramInt2);
    if (i <= 8)
    {
      paramInt1 = 1;
      for (;;)
      {
        paramInt2 = paramInt1;
        if (paramInt1 >= i) {
          break;
        }
        paramInt1 <<= 1;
      }
    }
    paramInt2 = (i + 7) / 8 * 8;
    return paramInt2;
  }
  
  /* Error */
  public static Bitmap createImageThumbnail(String paramString, int paramInt)
  {
    // Byte code:
    //   0: iload_1
    //   1: iconst_1
    //   2: if_icmpne +186 -> 188
    //   5: iconst_1
    //   6: istore_3
    //   7: iload_3
    //   8: ifeq +185 -> 193
    //   11: sipush 320
    //   14: istore_2
    //   15: iload_3
    //   16: ifeq +183 -> 199
    //   19: ldc 12
    //   21: istore_3
    //   22: new 6	android/media/ThumbnailUtils$SizedThumbnailBitmap
    //   25: dup
    //   26: aconst_null
    //   27: invokespecial 80	android/media/ThumbnailUtils$SizedThumbnailBitmap:<init>	(Landroid/media/ThumbnailUtils$SizedThumbnailBitmap;)V
    //   30: astore 7
    //   32: aconst_null
    //   33: astore 5
    //   35: aload_0
    //   36: invokestatic 86	android/media/MediaFile:getFileType	(Ljava/lang/String;)Landroid/media/MediaFile$MediaFileType;
    //   39: astore 8
    //   41: aload 5
    //   43: astore 6
    //   45: aload 8
    //   47: ifnull +43 -> 90
    //   50: aload 8
    //   52: getfield 91	android/media/MediaFile$MediaFileType:fileType	I
    //   55: bipush 31
    //   57: if_icmpeq +18 -> 75
    //   60: aload 5
    //   62: astore 6
    //   64: aload 8
    //   66: getfield 91	android/media/MediaFile$MediaFileType:fileType	I
    //   69: invokestatic 95	android/media/MediaFile:isRawImageFileType	(I)Z
    //   72: ifeq +18 -> 90
    //   75: aload_0
    //   76: iload_2
    //   77: iload_3
    //   78: aload 7
    //   80: invokestatic 99	android/media/ThumbnailUtils:createThumbnailFromEXIF	(Ljava/lang/String;IILandroid/media/ThumbnailUtils$SizedThumbnailBitmap;)V
    //   83: aload 7
    //   85: getfield 103	android/media/ThumbnailUtils$SizedThumbnailBitmap:mBitmap	Landroid/graphics/Bitmap;
    //   88: astore 6
    //   90: aload 6
    //   92: astore 5
    //   94: aload 6
    //   96: ifnonnull +180 -> 276
    //   99: aconst_null
    //   100: astore 9
    //   102: aconst_null
    //   103: astore 5
    //   105: aconst_null
    //   106: astore 8
    //   108: new 105	java/io/FileInputStream
    //   111: dup
    //   112: aload_0
    //   113: invokespecial 108	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   116: astore 7
    //   118: aload 7
    //   120: invokevirtual 112	java/io/FileInputStream:getFD	()Ljava/io/FileDescriptor;
    //   123: astore 5
    //   125: new 46	android/graphics/BitmapFactory$Options
    //   128: dup
    //   129: invokespecial 113	android/graphics/BitmapFactory$Options:<init>	()V
    //   132: astore 8
    //   134: aload 8
    //   136: iconst_1
    //   137: putfield 116	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   140: aload 8
    //   142: iconst_1
    //   143: putfield 120	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   146: aload 5
    //   148: aconst_null
    //   149: aload 8
    //   151: invokestatic 126	android/graphics/BitmapFactory:decodeFileDescriptor	(Ljava/io/FileDescriptor;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   154: pop
    //   155: aload 8
    //   157: getfield 129	android/graphics/BitmapFactory$Options:mCancel	Z
    //   160: ifne +16 -> 176
    //   163: aload 8
    //   165: getfield 49	android/graphics/BitmapFactory$Options:outWidth	I
    //   168: istore 4
    //   170: iload 4
    //   172: iconst_m1
    //   173: if_icmpne +33 -> 206
    //   176: aload 7
    //   178: ifnull +8 -> 186
    //   181: aload 7
    //   183: invokevirtual 130	java/io/FileInputStream:close	()V
    //   186: aconst_null
    //   187: areturn
    //   188: iconst_0
    //   189: istore_3
    //   190: goto -183 -> 7
    //   193: bipush 96
    //   195: istore_2
    //   196: goto -181 -> 15
    //   199: sipush 19200
    //   202: istore_3
    //   203: goto -181 -> 22
    //   206: aload 8
    //   208: getfield 52	android/graphics/BitmapFactory$Options:outHeight	I
    //   211: iconst_m1
    //   212: if_icmpeq -36 -> 176
    //   215: aload 8
    //   217: aload 8
    //   219: iload_2
    //   220: iload_3
    //   221: invokestatic 132	android/media/ThumbnailUtils:computeSampleSize	(Landroid/graphics/BitmapFactory$Options;II)I
    //   224: putfield 116	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   227: aload 8
    //   229: iconst_0
    //   230: putfield 120	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   233: aload 8
    //   235: iconst_0
    //   236: putfield 135	android/graphics/BitmapFactory$Options:inDither	Z
    //   239: aload 8
    //   241: getstatic 141	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   244: putfield 144	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   247: aload 5
    //   249: aconst_null
    //   250: aload 8
    //   252: invokestatic 126	android/graphics/BitmapFactory:decodeFileDescriptor	(Ljava/io/FileDescriptor;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   255: astore 5
    //   257: aload 5
    //   259: astore_0
    //   260: aload_0
    //   261: astore 5
    //   263: aload 7
    //   265: ifnull +11 -> 276
    //   268: aload 7
    //   270: invokevirtual 130	java/io/FileInputStream:close	()V
    //   273: aload_0
    //   274: astore 5
    //   276: aload 5
    //   278: astore_0
    //   279: iload_1
    //   280: iconst_3
    //   281: if_icmpne +14 -> 295
    //   284: aload 5
    //   286: bipush 96
    //   288: bipush 96
    //   290: iconst_2
    //   291: invokestatic 148	android/media/ThumbnailUtils:extractThumbnail	(Landroid/graphics/Bitmap;III)Landroid/graphics/Bitmap;
    //   294: astore_0
    //   295: aload_0
    //   296: areturn
    //   297: astore_0
    //   298: ldc 22
    //   300: ldc -106
    //   302: aload_0
    //   303: invokestatic 156	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   306: pop
    //   307: aconst_null
    //   308: areturn
    //   309: astore 5
    //   311: ldc 22
    //   313: ldc -106
    //   315: aload 5
    //   317: invokestatic 156	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   320: pop
    //   321: aload_0
    //   322: astore 5
    //   324: goto -48 -> 276
    //   327: astore 5
    //   329: aload 8
    //   331: astore 7
    //   333: aload 5
    //   335: astore 8
    //   337: aload 7
    //   339: astore 5
    //   341: ldc 22
    //   343: new 158	java/lang/StringBuilder
    //   346: dup
    //   347: invokespecial 159	java/lang/StringBuilder:<init>	()V
    //   350: ldc -95
    //   352: invokevirtual 165	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   355: aload_0
    //   356: invokevirtual 165	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   359: ldc -89
    //   361: invokevirtual 165	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   364: invokevirtual 171	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   367: aload 8
    //   369: invokestatic 156	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   372: pop
    //   373: aload 6
    //   375: astore 5
    //   377: aload 7
    //   379: ifnull -103 -> 276
    //   382: aload 7
    //   384: invokevirtual 130	java/io/FileInputStream:close	()V
    //   387: aload 6
    //   389: astore 5
    //   391: goto -115 -> 276
    //   394: astore_0
    //   395: ldc 22
    //   397: ldc -106
    //   399: aload_0
    //   400: invokestatic 156	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   403: pop
    //   404: aload 6
    //   406: astore 5
    //   408: goto -132 -> 276
    //   411: astore 7
    //   413: aload 9
    //   415: astore_0
    //   416: aload_0
    //   417: astore 5
    //   419: ldc 22
    //   421: ldc -106
    //   423: aload 7
    //   425: invokestatic 156	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   428: pop
    //   429: aload 6
    //   431: astore 5
    //   433: aload_0
    //   434: ifnull -158 -> 276
    //   437: aload_0
    //   438: invokevirtual 130	java/io/FileInputStream:close	()V
    //   441: aload 6
    //   443: astore 5
    //   445: goto -169 -> 276
    //   448: astore_0
    //   449: ldc 22
    //   451: ldc -106
    //   453: aload_0
    //   454: invokestatic 156	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   457: pop
    //   458: aload 6
    //   460: astore 5
    //   462: goto -186 -> 276
    //   465: astore_0
    //   466: aload 5
    //   468: ifnull +8 -> 476
    //   471: aload 5
    //   473: invokevirtual 130	java/io/FileInputStream:close	()V
    //   476: aload_0
    //   477: athrow
    //   478: astore 5
    //   480: ldc 22
    //   482: ldc -106
    //   484: aload 5
    //   486: invokestatic 156	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   489: pop
    //   490: goto -14 -> 476
    //   493: astore_0
    //   494: aload 7
    //   496: astore 5
    //   498: goto -32 -> 466
    //   501: astore 5
    //   503: aload 7
    //   505: astore_0
    //   506: aload 5
    //   508: astore 7
    //   510: goto -94 -> 416
    //   513: astore 8
    //   515: goto -178 -> 337
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	518	0	paramString	String
    //   0	518	1	paramInt	int
    //   14	206	2	i	int
    //   6	215	3	j	int
    //   168	6	4	k	int
    //   33	252	5	localObject1	Object
    //   309	7	5	localIOException1	IOException
    //   322	1	5	str	String
    //   327	7	5	localOutOfMemoryError1	OutOfMemoryError
    //   339	133	5	localObject2	Object
    //   478	7	5	localIOException2	IOException
    //   496	1	5	localIOException3	IOException
    //   501	6	5	localIOException4	IOException
    //   43	416	6	localObject3	Object
    //   30	353	7	localObject4	Object
    //   411	93	7	localIOException5	IOException
    //   508	1	7	localObject5	Object
    //   39	329	8	localObject6	Object
    //   513	1	8	localOutOfMemoryError2	OutOfMemoryError
    //   100	314	9	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   181	186	297	java/io/IOException
    //   268	273	309	java/io/IOException
    //   108	118	327	java/lang/OutOfMemoryError
    //   382	387	394	java/io/IOException
    //   108	118	411	java/io/IOException
    //   437	441	448	java/io/IOException
    //   108	118	465	finally
    //   341	373	465	finally
    //   419	429	465	finally
    //   471	476	478	java/io/IOException
    //   118	170	493	finally
    //   206	257	493	finally
    //   118	170	501	java/io/IOException
    //   206	257	501	java/io/IOException
    //   118	170	513	java/lang/OutOfMemoryError
    //   206	257	513	java/lang/OutOfMemoryError
  }
  
  private static void createThumbnailFromEXIF(String paramString, int paramInt1, int paramInt2, SizedThumbnailBitmap paramSizedThumbnailBitmap)
  {
    if (paramString == null) {
      return;
    }
    BitmapFactory.Options localOptions2 = null;
    for (;;)
    {
      try
      {
        Object localObject = new ExifInterface(paramString);
        BitmapFactory.Options localOptions3;
        int i;
        Log.w("ThumbnailUtils", localIOException1);
      }
      catch (IOException localIOException1)
      {
        try
        {
          localObject = ((ExifInterface)localObject).getThumbnail();
          localOptions2 = new BitmapFactory.Options();
          localOptions3 = new BitmapFactory.Options();
          i = 0;
          if (localObject != null)
          {
            localOptions3.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray((byte[])localObject, 0, localObject.length, localOptions3);
            localOptions3.inSampleSize = computeSampleSize(localOptions3, paramInt1, paramInt2);
            i = localOptions3.outWidth / localOptions3.inSampleSize;
          }
          localOptions2.inJustDecodeBounds = true;
          BitmapFactory.decodeFile(paramString, localOptions2);
          localOptions2.inSampleSize = computeSampleSize(localOptions2, paramInt1, paramInt2);
          paramInt1 = localOptions2.outWidth / localOptions2.inSampleSize;
          if ((localObject == null) || (i < paramInt1)) {
            break;
          }
          paramInt1 = localOptions3.outWidth;
          paramInt2 = localOptions3.outHeight;
          localOptions3.inJustDecodeBounds = false;
          paramSizedThumbnailBitmap.mBitmap = BitmapFactory.decodeByteArray((byte[])localObject, 0, localObject.length, localOptions3);
          if (paramSizedThumbnailBitmap.mBitmap != null)
          {
            paramSizedThumbnailBitmap.mThumbnailData = ((byte[])localObject);
            paramSizedThumbnailBitmap.mThumbnailWidth = paramInt1;
            paramSizedThumbnailBitmap.mThumbnailHeight = paramInt2;
          }
          return;
        }
        catch (IOException localIOException2)
        {
          BitmapFactory.Options localOptions1;
          for (;;) {}
        }
        localIOException1 = localIOException1;
      }
      localOptions1 = localOptions2;
    }
    localOptions2.inJustDecodeBounds = false;
    paramSizedThumbnailBitmap.mBitmap = BitmapFactory.decodeFile(paramString, localOptions2);
  }
  
  /* Error */
  public static Bitmap createVideoThumbnail(String paramString, int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: new 207	android/media/MediaMetadataRetriever
    //   6: dup
    //   7: invokespecial 208	android/media/MediaMetadataRetriever:<init>	()V
    //   10: astore 6
    //   12: aload 6
    //   14: aload_0
    //   15: invokevirtual 211	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   18: aload 6
    //   20: ldc2_w 212
    //   23: invokevirtual 217	android/media/MediaMetadataRetriever:getFrameAtTime	(J)Landroid/graphics/Bitmap;
    //   26: astore_0
    //   27: aload 6
    //   29: invokevirtual 220	android/media/MediaMetadataRetriever:release	()V
    //   32: aload_0
    //   33: ifnonnull +61 -> 94
    //   36: aconst_null
    //   37: areturn
    //   38: astore 5
    //   40: goto -8 -> 32
    //   43: astore_0
    //   44: aload 6
    //   46: invokevirtual 220	android/media/MediaMetadataRetriever:release	()V
    //   49: aload 5
    //   51: astore_0
    //   52: goto -20 -> 32
    //   55: astore_0
    //   56: aload 5
    //   58: astore_0
    //   59: goto -27 -> 32
    //   62: astore_0
    //   63: aload 6
    //   65: invokevirtual 220	android/media/MediaMetadataRetriever:release	()V
    //   68: aload 5
    //   70: astore_0
    //   71: goto -39 -> 32
    //   74: astore_0
    //   75: aload 5
    //   77: astore_0
    //   78: goto -46 -> 32
    //   81: astore_0
    //   82: aload 6
    //   84: invokevirtual 220	android/media/MediaMetadataRetriever:release	()V
    //   87: aload_0
    //   88: athrow
    //   89: astore 5
    //   91: goto -4 -> 87
    //   94: iload_1
    //   95: iconst_1
    //   96: if_icmpne +62 -> 158
    //   99: aload_0
    //   100: invokevirtual 226	android/graphics/Bitmap:getWidth	()I
    //   103: istore_1
    //   104: aload_0
    //   105: invokevirtual 229	android/graphics/Bitmap:getHeight	()I
    //   108: istore_3
    //   109: iload_1
    //   110: iload_3
    //   111: invokestatic 233	java/lang/Math:max	(II)I
    //   114: istore 4
    //   116: aload_0
    //   117: astore 5
    //   119: iload 4
    //   121: sipush 512
    //   124: if_icmple +31 -> 155
    //   127: ldc -22
    //   129: iload 4
    //   131: i2f
    //   132: fdiv
    //   133: fstore_2
    //   134: aload_0
    //   135: iload_1
    //   136: i2f
    //   137: fload_2
    //   138: fmul
    //   139: invokestatic 238	java/lang/Math:round	(F)I
    //   142: iload_3
    //   143: i2f
    //   144: fload_2
    //   145: fmul
    //   146: invokestatic 238	java/lang/Math:round	(F)I
    //   149: iconst_1
    //   150: invokestatic 242	android/graphics/Bitmap:createScaledBitmap	(Landroid/graphics/Bitmap;IIZ)Landroid/graphics/Bitmap;
    //   153: astore 5
    //   155: aload 5
    //   157: areturn
    //   158: aload_0
    //   159: astore 5
    //   161: iload_1
    //   162: iconst_3
    //   163: if_icmpne -8 -> 155
    //   166: aload_0
    //   167: bipush 96
    //   169: bipush 96
    //   171: iconst_2
    //   172: invokestatic 148	android/media/ThumbnailUtils:extractThumbnail	(Landroid/graphics/Bitmap;III)Landroid/graphics/Bitmap;
    //   175: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	paramString	String
    //   0	176	1	paramInt	int
    //   133	12	2	f	float
    //   108	35	3	i	int
    //   114	16	4	j	int
    //   1	1	5	localObject1	Object
    //   38	38	5	localRuntimeException1	RuntimeException
    //   89	1	5	localRuntimeException2	RuntimeException
    //   117	43	5	localObject2	Object
    //   10	73	6	localMediaMetadataRetriever	MediaMetadataRetriever
    // Exception table:
    //   from	to	target	type
    //   27	32	38	java/lang/RuntimeException
    //   12	27	43	java/lang/RuntimeException
    //   44	49	55	java/lang/RuntimeException
    //   12	27	62	java/lang/IllegalArgumentException
    //   63	68	74	java/lang/RuntimeException
    //   12	27	81	finally
    //   82	87	89	java/lang/RuntimeException
  }
  
  public static Bitmap extractThumbnail(Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    return extractThumbnail(paramBitmap, paramInt1, paramInt2, 0);
  }
  
  public static Bitmap extractThumbnail(Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramBitmap == null) {
      return null;
    }
    if (paramBitmap.getWidth() < paramBitmap.getHeight()) {}
    for (float f = paramInt1 / paramBitmap.getWidth();; f = paramInt2 / paramBitmap.getHeight())
    {
      Matrix localMatrix = new Matrix();
      localMatrix.setScale(f, f);
      return transform(localMatrix, paramBitmap, paramInt1, paramInt2, paramInt3 | 0x1);
    }
  }
  
  private static Bitmap makeBitmap(int paramInt1, int paramInt2, Uri paramUri, ContentResolver paramContentResolver, ParcelFileDescriptor paramParcelFileDescriptor, BitmapFactory.Options paramOptions)
  {
    ParcelFileDescriptor localParcelFileDescriptor1 = paramParcelFileDescriptor;
    ParcelFileDescriptor localParcelFileDescriptor2;
    if (paramParcelFileDescriptor == null) {
      localParcelFileDescriptor2 = paramParcelFileDescriptor;
    }
    try
    {
      localParcelFileDescriptor1 = makeInputStream(paramUri, paramContentResolver);
      if (localParcelFileDescriptor1 == null) {
        return null;
      }
      paramUri = paramOptions;
      if (paramOptions == null)
      {
        localParcelFileDescriptor2 = localParcelFileDescriptor1;
        paramParcelFileDescriptor = localParcelFileDescriptor1;
        paramUri = new BitmapFactory.Options();
      }
      localParcelFileDescriptor2 = localParcelFileDescriptor1;
      paramParcelFileDescriptor = localParcelFileDescriptor1;
      paramContentResolver = localParcelFileDescriptor1.getFileDescriptor();
      localParcelFileDescriptor2 = localParcelFileDescriptor1;
      paramParcelFileDescriptor = localParcelFileDescriptor1;
      paramUri.inSampleSize = 1;
      localParcelFileDescriptor2 = localParcelFileDescriptor1;
      paramParcelFileDescriptor = localParcelFileDescriptor1;
      paramUri.inJustDecodeBounds = true;
      localParcelFileDescriptor2 = localParcelFileDescriptor1;
      paramParcelFileDescriptor = localParcelFileDescriptor1;
      BitmapFactory.decodeFileDescriptor(paramContentResolver, null, paramUri);
      localParcelFileDescriptor2 = localParcelFileDescriptor1;
      paramParcelFileDescriptor = localParcelFileDescriptor1;
      if (!paramUri.mCancel)
      {
        localParcelFileDescriptor2 = localParcelFileDescriptor1;
        paramParcelFileDescriptor = localParcelFileDescriptor1;
        int i = paramUri.outWidth;
        if (i != -1) {
          break label153;
        }
      }
      label153:
      do
      {
        return null;
        localParcelFileDescriptor2 = localParcelFileDescriptor1;
        paramParcelFileDescriptor = localParcelFileDescriptor1;
      } while (paramUri.outHeight == -1);
      localParcelFileDescriptor2 = localParcelFileDescriptor1;
      paramParcelFileDescriptor = localParcelFileDescriptor1;
      paramUri.inSampleSize = computeSampleSize(paramUri, paramInt1, paramInt2);
      localParcelFileDescriptor2 = localParcelFileDescriptor1;
      paramParcelFileDescriptor = localParcelFileDescriptor1;
      paramUri.inJustDecodeBounds = false;
      localParcelFileDescriptor2 = localParcelFileDescriptor1;
      paramParcelFileDescriptor = localParcelFileDescriptor1;
      paramUri.inDither = false;
      localParcelFileDescriptor2 = localParcelFileDescriptor1;
      paramParcelFileDescriptor = localParcelFileDescriptor1;
      paramUri.inPreferredConfig = Bitmap.Config.ARGB_8888;
      localParcelFileDescriptor2 = localParcelFileDescriptor1;
      paramParcelFileDescriptor = localParcelFileDescriptor1;
      paramUri = BitmapFactory.decodeFileDescriptor(paramContentResolver, null, paramUri);
      return paramUri;
    }
    catch (OutOfMemoryError paramUri)
    {
      paramParcelFileDescriptor = localParcelFileDescriptor2;
      Log.e("ThumbnailUtils", "Got oom exception ", paramUri);
      return null;
    }
    finally
    {
      closeSilently(paramParcelFileDescriptor);
    }
  }
  
  private static ParcelFileDescriptor makeInputStream(Uri paramUri, ContentResolver paramContentResolver)
  {
    try
    {
      paramUri = paramContentResolver.openFileDescriptor(paramUri, "r");
      return paramUri;
    }
    catch (IOException paramUri) {}
    return null;
  }
  
  private static Bitmap transform(Matrix paramMatrix, Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    if ((paramInt3 & 0x1) != 0)
    {
      i = 1;
      if ((paramInt3 & 0x2) == 0) {
        break label198;
      }
    }
    int j;
    Object localObject;
    label198:
    for (paramInt3 = 1;; paramInt3 = 0)
    {
      int k = paramBitmap.getWidth() - paramInt1;
      j = paramBitmap.getHeight() - paramInt2;
      if ((i != 0) || ((k >= 0) && (j >= 0))) {
        break label204;
      }
      paramMatrix = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
      localObject = new Canvas(paramMatrix);
      i = Math.max(0, k / 2);
      j = Math.max(0, j / 2);
      Rect localRect = new Rect(i, j, Math.min(paramInt1, paramBitmap.getWidth()) + i, Math.min(paramInt2, paramBitmap.getHeight()) + j);
      i = (paramInt1 - localRect.width()) / 2;
      j = (paramInt2 - localRect.height()) / 2;
      ((Canvas)localObject).drawBitmap(paramBitmap, localRect, new Rect(i, j, paramInt1 - i, paramInt2 - j), null);
      if (paramInt3 != 0) {
        paramBitmap.recycle();
      }
      ((Canvas)localObject).setBitmap(null);
      return paramMatrix;
      i = 0;
      break;
    }
    label204:
    float f1 = paramBitmap.getWidth();
    float f2 = paramBitmap.getHeight();
    if (f1 / f2 > paramInt1 / paramInt2)
    {
      f1 = paramInt2 / f2;
      if ((f1 < 0.9F) || (f1 > 1.0F))
      {
        paramMatrix.setScale(f1, f1);
        if (paramMatrix == null) {
          break label405;
        }
      }
    }
    label405:
    for (paramMatrix = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), paramMatrix, true);; paramMatrix = paramBitmap)
    {
      if ((paramInt3 != 0) && (paramMatrix != paramBitmap)) {
        paramBitmap.recycle();
      }
      i = Math.max(0, paramMatrix.getWidth() - paramInt1);
      j = Math.max(0, paramMatrix.getHeight() - paramInt2);
      localObject = Bitmap.createBitmap(paramMatrix, i / 2, j / 2, paramInt1, paramInt2);
      if ((localObject != paramMatrix) && ((paramInt3 != 0) || (paramMatrix != paramBitmap))) {
        paramMatrix.recycle();
      }
      return (Bitmap)localObject;
      paramMatrix = null;
      break;
      f1 = paramInt1 / f1;
      if ((f1 < 0.9F) || (f1 > 1.0F))
      {
        paramMatrix.setScale(f1, f1);
        break;
      }
      paramMatrix = null;
      break;
    }
  }
  
  private static class SizedThumbnailBitmap
  {
    public Bitmap mBitmap;
    public byte[] mThumbnailData;
    public int mThumbnailHeight;
    public int mThumbnailWidth;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/ThumbnailUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */