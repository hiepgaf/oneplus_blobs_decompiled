package com.oneplus.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import java.io.Serializable;

public class DiskBitmapLruCache<TKey extends Serializable>
  extends DiskLruCache<TKey, Bitmap>
{
  private static final long THRESHOLD_USE_TWO_PHASE_COMPRESSION = 4665600L;
  private final Bitmap.Config m_BitmapConfig;
  private final Bitmap.CompressFormat m_CompressFormat;
  
  public DiskBitmapLruCache(Context paramContext, String paramString, long paramLong)
  {
    this(paramContext, paramString, Bitmap.Config.RGB_565, Bitmap.CompressFormat.JPEG, paramLong);
  }
  
  public DiskBitmapLruCache(Context paramContext, String paramString, Bitmap.Config paramConfig, Bitmap.CompressFormat paramCompressFormat, long paramLong)
  {
    super(paramContext, paramString, paramLong);
    if (paramConfig == null) {
      throw new IllegalArgumentException("No bitmap configuration specified.");
    }
    if (paramCompressFormat == null) {
      throw new IllegalArgumentException("No bitmap compression format specified.");
    }
    this.m_BitmapConfig = paramConfig;
    this.m_CompressFormat = paramCompressFormat;
  }
  
  /* Error */
  protected Bitmap readFromFile(TKey paramTKey, java.io.File paramFile, Bitmap paramBitmap)
    throws java.lang.Exception
  {
    // Byte code:
    //   0: new 52	android/graphics/BitmapFactory$Options
    //   3: dup
    //   4: invokespecial 55	android/graphics/BitmapFactory$Options:<init>	()V
    //   7: astore 10
    //   9: aload 10
    //   11: aload_0
    //   12: getfield 42	com/oneplus/cache/DiskBitmapLruCache:m_BitmapConfig	Landroid/graphics/Bitmap$Config;
    //   15: putfield 58	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   18: aload 10
    //   20: iconst_1
    //   21: putfield 62	android/graphics/BitmapFactory$Options:inPreferQualityOverSpeed	Z
    //   24: aconst_null
    //   25: astore_1
    //   26: aconst_null
    //   27: astore 8
    //   29: aload_2
    //   30: invokevirtual 68	java/io/File:length	()J
    //   33: ldc2_w 69
    //   36: lcmp
    //   37: ifgt +171 -> 208
    //   40: aconst_null
    //   41: astore 6
    //   43: aconst_null
    //   44: astore 7
    //   46: aconst_null
    //   47: astore 9
    //   49: aconst_null
    //   50: astore_1
    //   51: new 72	java/io/FileInputStream
    //   54: dup
    //   55: aload_2
    //   56: invokespecial 75	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   59: astore 5
    //   61: aload_2
    //   62: invokevirtual 68	java/io/File:length	()J
    //   65: l2i
    //   66: istore 4
    //   68: aload 8
    //   70: astore_1
    //   71: iload 4
    //   73: i2l
    //   74: ldc2_w 69
    //   77: lcmp
    //   78: ifgt +35 -> 113
    //   81: iload 4
    //   83: newarray <illegal type>
    //   85: astore 9
    //   87: aload 8
    //   89: astore_1
    //   90: aload 5
    //   92: aload 9
    //   94: invokevirtual 79	java/io/FileInputStream:read	([B)I
    //   97: iload 4
    //   99: if_icmpne +14 -> 113
    //   102: aload 9
    //   104: iconst_0
    //   105: iload 4
    //   107: aload 10
    //   109: invokestatic 85	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   112: astore_1
    //   113: aload 7
    //   115: astore 6
    //   117: aload 5
    //   119: ifnull +12 -> 131
    //   122: aload 5
    //   124: invokevirtual 88	java/io/FileInputStream:close	()V
    //   127: aload 7
    //   129: astore 6
    //   131: aload 6
    //   133: ifnull +75 -> 208
    //   136: aload 6
    //   138: athrow
    //   139: astore 6
    //   141: goto -10 -> 131
    //   144: astore_2
    //   145: aload_2
    //   146: athrow
    //   147: astore 5
    //   149: aload_2
    //   150: astore_3
    //   151: aload 5
    //   153: astore_2
    //   154: aload_3
    //   155: astore 5
    //   157: aload_1
    //   158: ifnull +10 -> 168
    //   161: aload_1
    //   162: invokevirtual 88	java/io/FileInputStream:close	()V
    //   165: aload_3
    //   166: astore 5
    //   168: aload 5
    //   170: ifnull +36 -> 206
    //   173: aload 5
    //   175: athrow
    //   176: astore_1
    //   177: aload_3
    //   178: ifnonnull +9 -> 187
    //   181: aload_1
    //   182: astore 5
    //   184: goto -16 -> 168
    //   187: aload_3
    //   188: astore 5
    //   190: aload_3
    //   191: aload_1
    //   192: if_acmpeq -24 -> 168
    //   195: aload_3
    //   196: aload_1
    //   197: invokevirtual 92	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   200: aload_3
    //   201: astore 5
    //   203: goto -35 -> 168
    //   206: aload_2
    //   207: athrow
    //   208: aload_1
    //   209: astore 5
    //   211: aload_1
    //   212: ifnonnull +14 -> 226
    //   215: aload_2
    //   216: invokevirtual 96	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   219: aload 10
    //   221: invokestatic 100	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   224: astore 5
    //   226: aload 5
    //   228: ifnull +6 -> 234
    //   231: aload 5
    //   233: areturn
    //   234: aload_3
    //   235: areturn
    //   236: astore_2
    //   237: aload 9
    //   239: astore_1
    //   240: aload 6
    //   242: astore_3
    //   243: goto -89 -> 154
    //   246: astore_2
    //   247: aload 5
    //   249: astore_1
    //   250: aload 6
    //   252: astore_3
    //   253: goto -99 -> 154
    //   256: astore_2
    //   257: aload 5
    //   259: astore_1
    //   260: goto -115 -> 145
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	263	0	this	DiskBitmapLruCache
    //   0	263	1	paramTKey	TKey
    //   0	263	2	paramFile	java.io.File
    //   0	263	3	paramBitmap	Bitmap
    //   66	40	4	i	int
    //   59	64	5	localFileInputStream	java.io.FileInputStream
    //   147	5	5	localObject1	Object
    //   155	103	5	localObject2	Object
    //   41	96	6	localObject3	Object
    //   139	112	6	localThrowable	Throwable
    //   44	84	7	localObject4	Object
    //   27	61	8	localObject5	Object
    //   47	191	9	arrayOfByte	byte[]
    //   7	213	10	localOptions	android.graphics.BitmapFactory.Options
    // Exception table:
    //   from	to	target	type
    //   122	127	139	java/lang/Throwable
    //   51	61	144	java/lang/Throwable
    //   145	147	147	finally
    //   161	165	176	java/lang/Throwable
    //   51	61	236	finally
    //   61	68	246	finally
    //   81	87	246	finally
    //   90	113	246	finally
    //   61	68	256	java/lang/Throwable
    //   81	87	256	java/lang/Throwable
    //   90	113	256	java/lang/Throwable
  }
  
  /* Error */
  protected void writeToFile(TKey paramTKey, Bitmap paramBitmap, java.io.File paramFile)
    throws java.lang.Exception
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aconst_null
    //   4: astore 5
    //   6: aconst_null
    //   7: astore 7
    //   9: aconst_null
    //   10: astore_1
    //   11: new 112	java/io/FileOutputStream
    //   14: dup
    //   15: aload_3
    //   16: invokespecial 113	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   19: astore 4
    //   21: aload_2
    //   22: invokevirtual 117	android/graphics/Bitmap:getByteCount	()I
    //   25: i2l
    //   26: ldc2_w 8
    //   29: lcmp
    //   30: ifgt +38 -> 68
    //   33: aload_2
    //   34: aload_0
    //   35: getfield 44	com/oneplus/cache/DiskBitmapLruCache:m_CompressFormat	Landroid/graphics/Bitmap$CompressFormat;
    //   38: bipush 90
    //   40: aload 4
    //   42: invokevirtual 121	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   45: pop
    //   46: aload 5
    //   48: astore_1
    //   49: aload 4
    //   51: ifnull +11 -> 62
    //   54: aload 4
    //   56: invokevirtual 122	java/io/FileOutputStream:close	()V
    //   59: aload 5
    //   61: astore_1
    //   62: aload_1
    //   63: ifnull +177 -> 240
    //   66: aload_1
    //   67: athrow
    //   68: aconst_null
    //   69: astore 7
    //   71: aconst_null
    //   72: astore_3
    //   73: new 124	java/io/ByteArrayOutputStream
    //   76: dup
    //   77: invokespecial 125	java/io/ByteArrayOutputStream:<init>	()V
    //   80: astore_1
    //   81: aload_2
    //   82: aload_0
    //   83: getfield 44	com/oneplus/cache/DiskBitmapLruCache:m_CompressFormat	Landroid/graphics/Bitmap$CompressFormat;
    //   86: bipush 90
    //   88: aload_1
    //   89: invokevirtual 121	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   92: pop
    //   93: aload 4
    //   95: aload_1
    //   96: invokevirtual 129	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   99: invokevirtual 133	java/io/FileOutputStream:write	([B)V
    //   102: aload_1
    //   103: ifnull +7 -> 110
    //   106: aload_1
    //   107: invokevirtual 134	java/io/ByteArrayOutputStream:close	()V
    //   110: aconst_null
    //   111: astore_1
    //   112: aload_1
    //   113: ifnull -67 -> 46
    //   116: aload_1
    //   117: athrow
    //   118: astore_2
    //   119: aload 4
    //   121: astore_1
    //   122: aload_2
    //   123: athrow
    //   124: astore 4
    //   126: aload_2
    //   127: astore_3
    //   128: aload 4
    //   130: astore_2
    //   131: aload_3
    //   132: astore 4
    //   134: aload_1
    //   135: ifnull +10 -> 145
    //   138: aload_1
    //   139: invokevirtual 122	java/io/FileOutputStream:close	()V
    //   142: aload_3
    //   143: astore 4
    //   145: aload 4
    //   147: ifnull +91 -> 238
    //   150: aload 4
    //   152: athrow
    //   153: astore_1
    //   154: goto -42 -> 112
    //   157: astore_1
    //   158: aload_1
    //   159: athrow
    //   160: astore_2
    //   161: aload_1
    //   162: astore 5
    //   164: aload_3
    //   165: ifnull +10 -> 175
    //   168: aload_3
    //   169: invokevirtual 134	java/io/ByteArrayOutputStream:close	()V
    //   172: aload_1
    //   173: astore 5
    //   175: aload 5
    //   177: ifnull +25 -> 202
    //   180: aload 5
    //   182: athrow
    //   183: aload_1
    //   184: astore 5
    //   186: aload_1
    //   187: aload_3
    //   188: if_acmpeq -13 -> 175
    //   191: aload_1
    //   192: aload_3
    //   193: invokevirtual 92	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   196: aload_1
    //   197: astore 5
    //   199: goto -24 -> 175
    //   202: aload_2
    //   203: athrow
    //   204: astore_1
    //   205: goto -143 -> 62
    //   208: astore_1
    //   209: aload_3
    //   210: ifnonnull +9 -> 219
    //   213: aload_1
    //   214: astore 4
    //   216: goto -71 -> 145
    //   219: aload_3
    //   220: astore 4
    //   222: aload_3
    //   223: aload_1
    //   224: if_acmpeq -79 -> 145
    //   227: aload_3
    //   228: aload_1
    //   229: invokevirtual 92	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   232: aload_3
    //   233: astore 4
    //   235: goto -90 -> 145
    //   238: aload_2
    //   239: athrow
    //   240: return
    //   241: astore_2
    //   242: aload 7
    //   244: astore_1
    //   245: aload 6
    //   247: astore_3
    //   248: goto -117 -> 131
    //   251: astore_2
    //   252: goto -130 -> 122
    //   255: astore_2
    //   256: aconst_null
    //   257: astore_1
    //   258: aload 7
    //   260: astore_3
    //   261: goto -100 -> 161
    //   264: astore_2
    //   265: aconst_null
    //   266: astore 5
    //   268: aload_1
    //   269: astore_3
    //   270: aload 5
    //   272: astore_1
    //   273: goto -112 -> 161
    //   276: astore_2
    //   277: aload_1
    //   278: astore_3
    //   279: aload_2
    //   280: astore_1
    //   281: goto -123 -> 158
    //   284: astore_2
    //   285: aload 4
    //   287: astore_1
    //   288: aload 6
    //   290: astore_3
    //   291: goto -160 -> 131
    //   294: astore_3
    //   295: aload_1
    //   296: ifnonnull -113 -> 183
    //   299: aload_3
    //   300: astore 5
    //   302: goto -127 -> 175
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	305	0	this	DiskBitmapLruCache
    //   0	305	1	paramTKey	TKey
    //   0	305	2	paramBitmap	Bitmap
    //   0	305	3	paramFile	java.io.File
    //   19	101	4	localFileOutputStream	java.io.FileOutputStream
    //   124	5	4	localObject1	Object
    //   132	154	4	localObject2	Object
    //   4	297	5	localObject3	Object
    //   1	288	6	localObject4	Object
    //   7	252	7	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   21	46	118	java/lang/Throwable
    //   116	118	118	java/lang/Throwable
    //   180	183	118	java/lang/Throwable
    //   191	196	118	java/lang/Throwable
    //   202	204	118	java/lang/Throwable
    //   122	124	124	finally
    //   106	110	153	java/lang/Throwable
    //   73	81	157	java/lang/Throwable
    //   158	160	160	finally
    //   54	59	204	java/lang/Throwable
    //   138	142	208	java/lang/Throwable
    //   11	21	241	finally
    //   11	21	251	java/lang/Throwable
    //   73	81	255	finally
    //   81	102	264	finally
    //   81	102	276	java/lang/Throwable
    //   21	46	284	finally
    //   106	110	284	finally
    //   116	118	284	finally
    //   168	172	284	finally
    //   180	183	284	finally
    //   191	196	284	finally
    //   202	204	284	finally
    //   168	172	294	java/lang/Throwable
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/cache/DiskBitmapLruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */