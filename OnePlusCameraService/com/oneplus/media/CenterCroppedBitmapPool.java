package com.oneplus.media;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.MediaMetadataRetriever;

public class CenterCroppedBitmapPool
  extends BitmapPool
{
  public CenterCroppedBitmapPool(String paramString, long paramLong, Bitmap.Config paramConfig, int paramInt)
  {
    super(paramString, paramLong, paramConfig, paramInt);
  }
  
  public CenterCroppedBitmapPool(String paramString, long paramLong, Bitmap.Config paramConfig, int paramInt1, int paramInt2)
  {
    super(paramString, paramLong, paramConfig, paramInt1, paramInt2);
  }
  
  /* Error */
  protected Bitmap decodePhoto(android.content.ContentResolver paramContentResolver, android.net.Uri paramUri, int paramInt1, int paramInt2)
    throws Exception
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 9
    //   3: aconst_null
    //   4: astore 7
    //   6: aconst_null
    //   7: astore 8
    //   9: aconst_null
    //   10: astore 6
    //   12: aconst_null
    //   13: astore 5
    //   15: aload_1
    //   16: aload_2
    //   17: invokevirtual 24	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   20: astore_1
    //   21: aload_1
    //   22: astore 5
    //   24: aload_1
    //   25: astore 6
    //   27: aload_0
    //   28: invokevirtual 28	com/oneplus/media/CenterCroppedBitmapPool:preferQualityOverSpeed	()Z
    //   31: ifeq +76 -> 107
    //   34: aload_1
    //   35: astore 5
    //   37: aload_1
    //   38: astore 6
    //   40: aload_0
    //   41: invokevirtual 32	com/oneplus/media/CenterCroppedBitmapPool:getTargetConfig	()Landroid/graphics/Bitmap$Config;
    //   44: getstatic 38	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   47: if_acmpeq +60 -> 107
    //   50: aload_1
    //   51: astore 5
    //   53: aload_1
    //   54: astore 6
    //   56: aload_1
    //   57: iload_3
    //   58: iload 4
    //   60: aload_0
    //   61: invokevirtual 42	com/oneplus/media/CenterCroppedBitmapPool:getDecodeFlags	()I
    //   64: getstatic 38	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
    //   67: invokestatic 48	com/oneplus/media/ImageUtils:decodeCenterCropBitmap	(Ljava/io/InputStream;IIILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   70: aload_0
    //   71: invokevirtual 32	com/oneplus/media/CenterCroppedBitmapPool:getTargetConfig	()Landroid/graphics/Bitmap$Config;
    //   74: iconst_0
    //   75: invokevirtual 54	android/graphics/Bitmap:copy	(Landroid/graphics/Bitmap$Config;Z)Landroid/graphics/Bitmap;
    //   78: astore 9
    //   80: aload 8
    //   82: astore_2
    //   83: aload_1
    //   84: ifnull +10 -> 94
    //   87: aload_1
    //   88: invokevirtual 60	java/io/InputStream:close	()V
    //   91: aload 8
    //   93: astore_2
    //   94: aload_2
    //   95: ifnull +9 -> 104
    //   98: aload_2
    //   99: athrow
    //   100: astore_2
    //   101: goto -7 -> 94
    //   104: aload 9
    //   106: areturn
    //   107: aload_1
    //   108: astore 5
    //   110: aload_1
    //   111: astore 6
    //   113: aload_1
    //   114: iload_3
    //   115: iload 4
    //   117: aload_0
    //   118: invokevirtual 42	com/oneplus/media/CenterCroppedBitmapPool:getDecodeFlags	()I
    //   121: aload_0
    //   122: invokevirtual 32	com/oneplus/media/CenterCroppedBitmapPool:getTargetConfig	()Landroid/graphics/Bitmap$Config;
    //   125: invokestatic 48	com/oneplus/media/ImageUtils:decodeCenterCropBitmap	(Ljava/io/InputStream;IIILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   128: astore 8
    //   130: aload 9
    //   132: astore_2
    //   133: aload_1
    //   134: ifnull +10 -> 144
    //   137: aload_1
    //   138: invokevirtual 60	java/io/InputStream:close	()V
    //   141: aload 9
    //   143: astore_2
    //   144: aload_2
    //   145: ifnull +9 -> 154
    //   148: aload_2
    //   149: athrow
    //   150: astore_2
    //   151: goto -7 -> 144
    //   154: aload 8
    //   156: areturn
    //   157: astore_1
    //   158: aload_1
    //   159: athrow
    //   160: astore_2
    //   161: aload_1
    //   162: astore 6
    //   164: aload 5
    //   166: ifnull +11 -> 177
    //   169: aload 5
    //   171: invokevirtual 60	java/io/InputStream:close	()V
    //   174: aload_1
    //   175: astore 6
    //   177: aload 6
    //   179: ifnull +40 -> 219
    //   182: aload 6
    //   184: athrow
    //   185: astore 5
    //   187: aload_1
    //   188: ifnonnull +10 -> 198
    //   191: aload 5
    //   193: astore 6
    //   195: goto -18 -> 177
    //   198: aload_1
    //   199: astore 6
    //   201: aload_1
    //   202: aload 5
    //   204: if_acmpeq -27 -> 177
    //   207: aload_1
    //   208: aload 5
    //   210: invokevirtual 64	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   213: aload_1
    //   214: astore 6
    //   216: goto -39 -> 177
    //   219: aload_2
    //   220: athrow
    //   221: astore_2
    //   222: aload 6
    //   224: astore 5
    //   226: aload 7
    //   228: astore_1
    //   229: goto -68 -> 161
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	232	0	this	CenterCroppedBitmapPool
    //   0	232	1	paramContentResolver	android.content.ContentResolver
    //   0	232	2	paramUri	android.net.Uri
    //   0	232	3	paramInt1	int
    //   0	232	4	paramInt2	int
    //   13	157	5	localContentResolver	android.content.ContentResolver
    //   185	24	5	localThrowable	Throwable
    //   224	1	5	localObject1	Object
    //   10	213	6	localObject2	Object
    //   4	223	7	localObject3	Object
    //   7	148	8	localBitmap1	Bitmap
    //   1	141	9	localBitmap2	Bitmap
    // Exception table:
    //   from	to	target	type
    //   87	91	100	java/lang/Throwable
    //   137	141	150	java/lang/Throwable
    //   15	21	157	java/lang/Throwable
    //   27	34	157	java/lang/Throwable
    //   40	50	157	java/lang/Throwable
    //   56	80	157	java/lang/Throwable
    //   113	130	157	java/lang/Throwable
    //   158	160	160	finally
    //   169	174	185	java/lang/Throwable
    //   15	21	221	finally
    //   27	34	221	finally
    //   40	50	221	finally
    //   56	80	221	finally
    //   113	130	221	finally
  }
  
  protected Bitmap decodePhoto(String paramString, int paramInt1, int paramInt2)
    throws Exception
  {
    if ((preferQualityOverSpeed()) && (getTargetConfig() != Bitmap.Config.ARGB_8888)) {
      return ImageUtils.decodeCenterCropBitmap(paramString, paramInt1, paramInt2, getDecodeFlags(), Bitmap.Config.ARGB_8888).copy(getTargetConfig(), false);
    }
    return ImageUtils.decodeCenterCropBitmap(paramString, paramInt1, paramInt2, getDecodeFlags(), getTargetConfig());
  }
  
  /* Error */
  protected Bitmap decodeVideo(android.content.ContentResolver paramContentResolver, android.net.Uri paramUri, int paramInt1, int paramInt2)
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
    //   20: ldc 72
    //   22: invokevirtual 76	android/content/ContentResolver:openFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;)Landroid/os/ParcelFileDescriptor;
    //   25: astore_1
    //   26: aload_1
    //   27: astore 5
    //   29: aload_1
    //   30: astore 6
    //   32: new 78	android/media/MediaMetadataRetriever
    //   35: dup
    //   36: invokespecial 80	android/media/MediaMetadataRetriever:<init>	()V
    //   39: astore_2
    //   40: aload_2
    //   41: aload_1
    //   42: invokevirtual 86	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   45: invokevirtual 90	android/media/MediaMetadataRetriever:setDataSource	(Ljava/io/FileDescriptor;)V
    //   48: aload_2
    //   49: invokevirtual 94	android/media/MediaMetadataRetriever:getFrameAtTime	()Landroid/graphics/Bitmap;
    //   52: iload_3
    //   53: iload 4
    //   55: invokestatic 98	com/oneplus/media/ImageUtils:centerCropBitmap	(Landroid/graphics/Bitmap;II)Landroid/graphics/Bitmap;
    //   58: astore 6
    //   60: aload 8
    //   62: astore 5
    //   64: aload_1
    //   65: ifnull +11 -> 76
    //   68: aload_1
    //   69: invokevirtual 99	android/os/ParcelFileDescriptor:close	()V
    //   72: aload 8
    //   74: astore 5
    //   76: aload 5
    //   78: ifnull +26 -> 104
    //   81: aload 5
    //   83: athrow
    //   84: astore 5
    //   86: aload_2
    //   87: astore_1
    //   88: aload_1
    //   89: ifnull +7 -> 96
    //   92: aload_1
    //   93: invokevirtual 102	android/media/MediaMetadataRetriever:release	()V
    //   96: aload 5
    //   98: athrow
    //   99: astore 5
    //   101: goto -25 -> 76
    //   104: aload_2
    //   105: ifnull +7 -> 112
    //   108: aload_2
    //   109: invokevirtual 102	android/media/MediaMetadataRetriever:release	()V
    //   112: aload 6
    //   114: areturn
    //   115: astore_2
    //   116: aload 9
    //   118: astore_1
    //   119: aload_2
    //   120: athrow
    //   121: astore 7
    //   123: aload_2
    //   124: astore 6
    //   126: aload 7
    //   128: astore_2
    //   129: aload 6
    //   131: astore 7
    //   133: aload 5
    //   135: ifnull +12 -> 147
    //   138: aload 5
    //   140: invokevirtual 99	android/os/ParcelFileDescriptor:close	()V
    //   143: aload 6
    //   145: astore 7
    //   147: aload 7
    //   149: ifnull +31 -> 180
    //   152: aload 7
    //   154: athrow
    //   155: aload 6
    //   157: astore 7
    //   159: aload 6
    //   161: aload 5
    //   163: if_acmpeq -16 -> 147
    //   166: aload 6
    //   168: aload 5
    //   170: invokevirtual 64	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   173: aload 6
    //   175: astore 7
    //   177: goto -30 -> 147
    //   180: aload_2
    //   181: athrow
    //   182: astore_2
    //   183: aload 6
    //   185: astore 5
    //   187: aload 10
    //   189: astore_1
    //   190: aload 7
    //   192: astore 6
    //   194: goto -65 -> 129
    //   197: astore 6
    //   199: aload_1
    //   200: astore 5
    //   202: aload_2
    //   203: astore_1
    //   204: aload 6
    //   206: astore_2
    //   207: aload 7
    //   209: astore 6
    //   211: goto -82 -> 129
    //   214: astore 6
    //   216: aload_1
    //   217: astore 5
    //   219: aload_2
    //   220: astore_1
    //   221: aload 6
    //   223: astore_2
    //   224: goto -105 -> 119
    //   227: astore 5
    //   229: goto -141 -> 88
    //   232: astore 5
    //   234: aload 6
    //   236: ifnonnull -81 -> 155
    //   239: aload 5
    //   241: astore 7
    //   243: goto -96 -> 147
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	246	0	this	CenterCroppedBitmapPool
    //   0	246	1	paramContentResolver	android.content.ContentResolver
    //   0	246	2	paramUri	android.net.Uri
    //   0	246	3	paramInt1	int
    //   0	246	4	paramInt2	int
    //   16	66	5	localObject1	Object
    //   84	13	5	localObject2	Object
    //   99	70	5	localThrowable1	Throwable
    //   185	33	5	localObject3	Object
    //   227	1	5	localObject4	Object
    //   232	8	5	localThrowable2	Throwable
    //   13	180	6	localObject5	Object
    //   197	8	6	localObject6	Object
    //   209	1	6	localObject7	Object
    //   214	21	6	localThrowable3	Throwable
    //   1	1	7	localObject8	Object
    //   121	6	7	localObject9	Object
    //   131	111	7	localObject10	Object
    //   4	69	8	localObject11	Object
    //   10	107	9	localObject12	Object
    //   7	181	10	localObject13	Object
    // Exception table:
    //   from	to	target	type
    //   68	72	84	finally
    //   81	84	84	finally
    //   68	72	99	java/lang/Throwable
    //   18	26	115	java/lang/Throwable
    //   32	40	115	java/lang/Throwable
    //   119	121	121	finally
    //   18	26	182	finally
    //   32	40	182	finally
    //   40	60	197	finally
    //   40	60	214	java/lang/Throwable
    //   138	143	227	finally
    //   152	155	227	finally
    //   166	173	227	finally
    //   180	182	227	finally
    //   138	143	232	java/lang/Throwable
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
        paramString = ImageUtils.centerCropBitmap(localMediaMetadataRetriever.getFrameAtTime(), paramInt1, paramInt2);
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
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/CenterCroppedBitmapPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */