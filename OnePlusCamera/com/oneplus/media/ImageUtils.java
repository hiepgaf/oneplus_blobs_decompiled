package com.oneplus.media;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.Image;
import android.media.Image.Plane;
import android.os.SystemClock;
import android.renderscript.Matrix4f;
import android.util.Size;
import com.oneplus.base.Log;
import com.oneplus.base.NativeLibrary;
import com.oneplus.base.Ref;
import com.oneplus.util.SizeUtils;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public final class ImageUtils
{
  public static final int FLAG_IGNORE_ERROR_LOG = 64;
  public static final int FLAG_IGNORE_ORIENTATION = 32;
  public static final int FLAG_MUTABLE = 8;
  public static final int FLAG_NO_EMBEDDED_THUMB = 2;
  public static final int FLAG_OPAQUE = 16;
  public static final int FLAG_PREFER_QUALITY_OVER_SPEED = 1;
  public static final int FLAG_USE_EMBEDDED_THUMB_ONLY = 4;
  private static final int LARGE_IMAGE_SIZE_THRESHOLD = 25000000;
  private static final Matrix4f MATRIX_RGBA_TO_YUV;
  public static final String[] PHOTO_EXIF_ATTRS = { "ApertureValue", "Copyright", "DateTime", "DateTimeDigitized", "DateTimeOriginal", "ExposureBiasValue", "ExposureProgram", "ExposureTime", "FNumber", "Flash", "FocalLength", "FocalLengthIn35mmFilm", "GPSAltitude", "GPSAltitudeRef", "GPSDateStamp", "GPSLatitude", "GPSLatitudeRef", "GPSLongitude", "GPSLongitudeRef", "GPSTimeStamp", "ISOSpeedRatings", "Make", "MakerNote", "Model", "ShutterSpeedValue", "WhiteBalance" };
  private static final String TAG = "ImageUtils";
  private static final long TIMEOUT_TO_WAIT_LOCKING_FILE = 20000L;
  private static final Paint m_BitmapFilterPaint;
  
  static
  {
    MATRIX_RGBA_TO_YUV = new Matrix4f(new float[] { 0.299F, -0.16874F, 0.5F, 0.0F, 0.587F, -0.33126F, -0.41869F, 0.0F, 0.114F, 0.5F, -0.08131F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F });
    m_BitmapFilterPaint = new Paint();
    m_BitmapFilterPaint.setFilterBitmap(true);
  }
  
  public static void bitmapToNV21(Bitmap paramBitmap, byte[] paramArrayOfByte)
  {
    if (paramBitmap == null) {
      throw new IllegalArgumentException("Input image is empty");
    }
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    int k = i * j * 3;
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("NV21 buffer is empty");
    }
    if (paramArrayOfByte.length < k) {
      throw new IllegalArgumentException("Invalid NV21 buffer, length : " + paramArrayOfByte.length + ", expect length : " + k);
    }
    ByteBuffer localByteBuffer = lockPixels(paramBitmap);
    byte[] arrayOfByte = new byte[localByteBuffer.capacity()];
    localByteBuffer.get(arrayOfByte);
    unlockPixels(paramBitmap);
    Log.d("ImageUtils", "bitmapToNV21() - rgba : ", new Object[] { Integer.valueOf(arrayOfByte.length), ", nv21 : ", Integer.valueOf(paramArrayOfByte.length), ", width : ", Integer.valueOf(i), ", height : ", Integer.valueOf(j) });
    long l = SystemClock.elapsedRealtime();
    if (NativeLibrary.load()) {
      rgbaToYuvaAndNv21a(arrayOfByte, paramArrayOfByte, i, j);
    }
    Log.d("ImageUtils", "bitmapToNV21() - RGBA to NV21, spent : ", Long.valueOf(SystemClock.elapsedRealtime() - l), " ms");
  }
  
  public static byte[] bitmapToNV21(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      throw new IllegalArgumentException("Input image is empty");
    }
    byte[] arrayOfByte = new byte[paramBitmap.getWidth() * paramBitmap.getHeight() * 3];
    bitmapToNV21(paramBitmap, arrayOfByte);
    return arrayOfByte;
  }
  
  public static int calculateSampleSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return calculateSampleSize(paramInt1, paramInt2, paramInt3, paramInt4, false);
  }
  
  public static int calculateSampleSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    int i = 2;
    paramInt1 >>= 1;
    int j = paramInt2 >> 1;
    paramInt2 = paramInt1;
    paramInt1 = j;
    while ((paramInt2 > paramInt3) || (paramInt1 > paramInt4))
    {
      i <<= 1;
      paramInt2 >>= 1;
      paramInt1 >>= 1;
    }
    if (paramBoolean) {
      return i;
    }
    return i >> 1;
  }
  
  public static Bitmap centerCropBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    paramInt1 = Math.min(i, paramInt1);
    paramInt2 = Math.min(j, paramInt2);
    if ((paramInt1 == i) && (paramInt2 == j)) {
      return paramBitmap;
    }
    float f = Math.min(i / paramInt1, j / paramInt2);
    int k = (int)(paramInt1 * f);
    int m = (int)(paramInt2 * f);
    Rect localRect = new Rect(0, 0, k, m);
    localRect.offsetTo((i - k) / 2, (j - m) / 2);
    Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, paramBitmap.getConfig());
    new Canvas(localBitmap).drawBitmap(paramBitmap, localRect, new Rect(0, 0, paramInt1, paramInt2), m_BitmapFilterPaint);
    return localBitmap;
  }
  
  /* Error */
  public static boolean checkAnimatable(InputStream paramInputStream, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 326	com/oneplus/media/ImageUtils:isGifHeader	(Ljava/io/InputStream;)Z
    //   4: ifne +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: aconst_null
    //   10: astore_2
    //   11: aconst_null
    //   12: astore 4
    //   14: new 328	com/oneplus/util/GifDecoder
    //   17: dup
    //   18: invokespecial 329	com/oneplus/util/GifDecoder:<init>	()V
    //   21: astore_3
    //   22: aload_3
    //   23: aload_0
    //   24: invokevirtual 333	com/oneplus/util/GifDecoder:read	(Ljava/io/InputStream;)V
    //   27: aload_3
    //   28: invokevirtual 336	com/oneplus/util/GifDecoder:frameCount	()I
    //   31: istore_1
    //   32: iload_1
    //   33: iconst_1
    //   34: if_icmple +13 -> 47
    //   37: aload_3
    //   38: ifnull +7 -> 45
    //   41: aload_3
    //   42: invokevirtual 339	com/oneplus/util/GifDecoder:release	()V
    //   45: iconst_1
    //   46: ireturn
    //   47: aload_3
    //   48: ifnull +7 -> 55
    //   51: aload_3
    //   52: invokevirtual 339	com/oneplus/util/GifDecoder:release	()V
    //   55: iconst_0
    //   56: ireturn
    //   57: astore_3
    //   58: aload 4
    //   60: astore_0
    //   61: aload_0
    //   62: astore_2
    //   63: ldc 32
    //   65: ldc_w 341
    //   68: aload_3
    //   69: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   72: aload_0
    //   73: ifnull -18 -> 55
    //   76: aload_0
    //   77: invokevirtual 339	com/oneplus/util/GifDecoder:release	()V
    //   80: iconst_0
    //   81: ireturn
    //   82: astore_0
    //   83: aload_2
    //   84: ifnull +7 -> 91
    //   87: aload_2
    //   88: invokevirtual 339	com/oneplus/util/GifDecoder:release	()V
    //   91: aload_0
    //   92: athrow
    //   93: astore_0
    //   94: aload_3
    //   95: astore_2
    //   96: goto -13 -> 83
    //   99: astore_2
    //   100: aload_3
    //   101: astore_0
    //   102: aload_2
    //   103: astore_3
    //   104: goto -43 -> 61
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	107	0	paramInputStream	InputStream
    //   0	107	1	paramInt	int
    //   10	86	2	localObject1	Object
    //   99	4	2	localThrowable1	Throwable
    //   21	31	3	localGifDecoder	com.oneplus.util.GifDecoder
    //   57	44	3	localThrowable2	Throwable
    //   103	1	3	localThrowable3	Throwable
    //   12	47	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   14	22	57	java/lang/Throwable
    //   14	22	82	finally
    //   63	72	82	finally
    //   22	32	93	finally
    //   22	32	99	java/lang/Throwable
  }
  
  public static void combineNV21Images(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    combineNV21Images(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramArrayOfByte3, paramInt3, paramInt4, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public static void combineNV21Images(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    if (NativeLibrary.load()) {
      combineNV21ImagesNative(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramArrayOfByte3, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8);
    }
  }
  
  private static native void combineNV21ImagesNative(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8);
  
  public static boolean copyExif(ExifInterface paramExifInterface1, ExifInterface paramExifInterface2, String[] paramArrayOfString)
  {
    if ((paramExifInterface1 == null) || (paramExifInterface2 == null)) {
      return false;
    }
    if (paramExifInterface1 == paramExifInterface2) {
      return true;
    }
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
      return true;
    }
    try
    {
      int i = paramArrayOfString.length - 1;
      while (i >= 0)
      {
        String str = paramExifInterface1.getAttribute(paramArrayOfString[i]);
        if (str != null) {
          paramExifInterface2.setAttribute(paramArrayOfString[i], str);
        }
        i -= 1;
      }
      return true;
    }
    catch (Throwable paramExifInterface1)
    {
      Log.e("ImageUtils", "copyExif() - Fail to copy EXIF", paramExifInterface1);
    }
    return false;
  }
  
  /* Error */
  public static boolean copyExif(ExifInterface paramExifInterface, java.io.File paramFile, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnull +7 -> 8
    //   4: aload_1
    //   5: ifnonnull +5 -> 10
    //   8: iconst_0
    //   9: ireturn
    //   10: aload_2
    //   11: ifnull +8 -> 19
    //   14: aload_2
    //   15: arraylength
    //   16: ifne +5 -> 21
    //   19: iconst_1
    //   20: ireturn
    //   21: new 357	android/media/ExifInterface
    //   24: dup
    //   25: aload_1
    //   26: invokevirtual 373	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   29: invokespecial 374	android/media/ExifInterface:<init>	(Ljava/lang/String;)V
    //   32: astore 9
    //   34: aconst_null
    //   35: astore 6
    //   37: aload_2
    //   38: arraylength
    //   39: iconst_1
    //   40: isub
    //   41: istore 5
    //   43: iload 5
    //   45: iflt +56 -> 101
    //   48: aload_0
    //   49: aload_2
    //   50: iload 5
    //   52: aaload
    //   53: invokevirtual 361	android/media/ExifInterface:getAttribute	(Ljava/lang/String;)Ljava/lang/String;
    //   56: astore 8
    //   58: aload 6
    //   60: astore 7
    //   62: aload 8
    //   64: ifnull +303 -> 367
    //   67: ldc 107
    //   69: aload_2
    //   70: iload 5
    //   72: aaload
    //   73: invokevirtual 378	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   76: ifeq +7 -> 83
    //   79: aload 8
    //   81: astore 6
    //   83: aload 9
    //   85: aload_2
    //   86: iload 5
    //   88: aaload
    //   89: aload 8
    //   91: invokevirtual 365	android/media/ExifInterface:setAttribute	(Ljava/lang/String;Ljava/lang/String;)V
    //   94: aload 6
    //   96: astore 7
    //   98: goto +269 -> 367
    //   101: aload 9
    //   103: invokevirtual 381	android/media/ExifInterface:saveAttributes	()V
    //   106: aload 6
    //   108: ifnull +242 -> 350
    //   111: aload 6
    //   113: invokestatic 387	java/lang/Double:parseDouble	(Ljava/lang/String;)D
    //   116: dstore_3
    //   117: aconst_null
    //   118: astore_2
    //   119: aconst_null
    //   120: astore 6
    //   122: aconst_null
    //   123: astore 8
    //   125: aconst_null
    //   126: astore 7
    //   128: new 389	java/io/RandomAccessFile
    //   131: dup
    //   132: aload_1
    //   133: ldc_w 391
    //   136: invokespecial 394	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   139: astore_0
    //   140: aload_0
    //   141: invokevirtual 398	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   144: astore_1
    //   145: new 400	com/oneplus/base/SimpleRef
    //   148: dup
    //   149: invokespecial 401	com/oneplus/base/SimpleRef:<init>	()V
    //   152: astore 7
    //   154: aload_1
    //   155: aload 7
    //   157: aconst_null
    //   158: invokestatic 405	com/oneplus/media/ImageUtils:findTiffHeader	(Ljava/nio/channels/SeekableByteChannel;Lcom/oneplus/base/Ref;Lcom/oneplus/base/Ref;)Z
    //   161: ifeq +97 -> 258
    //   164: ldc2_w 406
    //   167: dload_3
    //   168: dmul
    //   169: invokestatic 411	java/lang/Math:round	(D)J
    //   172: l2i
    //   173: istore 5
    //   175: ldc 32
    //   177: new 193	java/lang/StringBuilder
    //   180: dup
    //   181: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   184: ldc_w 413
    //   187: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   190: dload_3
    //   191: invokevirtual 416	java/lang/StringBuilder:append	(D)Ljava/lang/StringBuilder;
    //   194: ldc_w 418
    //   197: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   200: iload 5
    //   202: invokevirtual 203	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   205: ldc_w 420
    //   208: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   211: ldc_w 421
    //   214: invokevirtual 203	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   217: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   220: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   223: aload_1
    //   224: aload 7
    //   226: invokeinterface 428 1 0
    //   231: checkcast 266	java/lang/Long
    //   234: invokevirtual 431	java/lang/Long:longValue	()J
    //   237: invokevirtual 437	java/nio/channels/FileChannel:position	(J)Ljava/nio/channels/FileChannel;
    //   240: pop
    //   241: aload_1
    //   242: new 439	android/util/Rational
    //   245: dup
    //   246: iload 5
    //   248: ldc_w 421
    //   251: invokespecial 441	android/util/Rational:<init>	(II)V
    //   254: invokestatic 445	com/oneplus/media/ImageUtils:updateTiffExposureTime	(Ljava/nio/channels/SeekableByteChannel;Landroid/util/Rational;)Z
    //   257: pop
    //   258: aload 6
    //   260: astore_1
    //   261: aload_0
    //   262: ifnull +10 -> 272
    //   265: aload_0
    //   266: invokevirtual 448	java/io/RandomAccessFile:close	()V
    //   269: aload 6
    //   271: astore_1
    //   272: aload_1
    //   273: ifnull +77 -> 350
    //   276: aload_1
    //   277: athrow
    //   278: astore_0
    //   279: ldc 32
    //   281: ldc_w 367
    //   284: aload_0
    //   285: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   288: iconst_0
    //   289: ireturn
    //   290: astore_1
    //   291: goto -19 -> 272
    //   294: astore_1
    //   295: aload 7
    //   297: astore_0
    //   298: aload_1
    //   299: athrow
    //   300: astore 6
    //   302: aload_1
    //   303: astore_2
    //   304: aload 6
    //   306: astore_1
    //   307: aload_2
    //   308: astore 6
    //   310: aload_0
    //   311: ifnull +10 -> 321
    //   314: aload_0
    //   315: invokevirtual 448	java/io/RandomAccessFile:close	()V
    //   318: aload_2
    //   319: astore 6
    //   321: aload 6
    //   323: ifnull +25 -> 348
    //   326: aload 6
    //   328: athrow
    //   329: aload_2
    //   330: astore 6
    //   332: aload_2
    //   333: aload_0
    //   334: if_acmpeq -13 -> 321
    //   337: aload_2
    //   338: aload_0
    //   339: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   342: aload_2
    //   343: astore 6
    //   345: goto -24 -> 321
    //   348: aload_1
    //   349: athrow
    //   350: iconst_1
    //   351: ireturn
    //   352: astore_1
    //   353: aload 8
    //   355: astore_0
    //   356: goto -49 -> 307
    //   359: astore_1
    //   360: goto -53 -> 307
    //   363: astore_1
    //   364: goto -66 -> 298
    //   367: iload 5
    //   369: iconst_1
    //   370: isub
    //   371: istore 5
    //   373: aload 7
    //   375: astore 6
    //   377: goto -334 -> 43
    //   380: astore_0
    //   381: aload_2
    //   382: ifnonnull -53 -> 329
    //   385: aload_0
    //   386: astore 6
    //   388: goto -67 -> 321
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	391	0	paramExifInterface	ExifInterface
    //   0	391	1	paramFile	java.io.File
    //   0	391	2	paramArrayOfString	String[]
    //   116	75	3	d	double
    //   41	331	5	i	int
    //   35	235	6	localObject1	Object
    //   300	5	6	localObject2	Object
    //   308	79	6	localObject3	Object
    //   60	314	7	localObject4	Object
    //   56	298	8	str	String
    //   32	70	9	localExifInterface	ExifInterface
    // Exception table:
    //   from	to	target	type
    //   21	34	278	java/lang/Throwable
    //   37	43	278	java/lang/Throwable
    //   48	58	278	java/lang/Throwable
    //   67	79	278	java/lang/Throwable
    //   83	94	278	java/lang/Throwable
    //   101	106	278	java/lang/Throwable
    //   111	117	278	java/lang/Throwable
    //   276	278	278	java/lang/Throwable
    //   326	329	278	java/lang/Throwable
    //   337	342	278	java/lang/Throwable
    //   348	350	278	java/lang/Throwable
    //   265	269	290	java/lang/Throwable
    //   128	140	294	java/lang/Throwable
    //   298	300	300	finally
    //   128	140	352	finally
    //   140	258	359	finally
    //   140	258	363	java/lang/Throwable
    //   314	318	380	java/lang/Throwable
  }
  
  public static boolean copyExif(String paramString1, String paramString2, String[] paramArrayOfString)
  {
    if (paramArrayOfString != null) {}
    for (;;)
    {
      int i;
      try
      {
        if (paramArrayOfString.length == 0) {
          return true;
        }
        Object localObject2 = new ExifInterface(paramString1);
        Object localObject1 = new HashMap();
        i = paramArrayOfString.length - 1;
        if (i >= 0)
        {
          String str = ((ExifInterface)localObject2).getAttribute(paramArrayOfString[i]);
          if (str == null) {
            break label201;
          }
          ((HashMap)localObject1).put(paramArrayOfString[i], str);
          break label201;
        }
        if (((HashMap)localObject1).isEmpty()) {
          return true;
        }
        paramArrayOfString = new ExifInterface(paramString2);
        localObject1 = ((HashMap)localObject1).entrySet().iterator();
        if (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Map.Entry)((Iterator)localObject1).next();
          paramArrayOfString.setAttribute((String)((Map.Entry)localObject2).getKey(), (String)((Map.Entry)localObject2).getValue());
          continue;
        }
        paramArrayOfString.saveAttributes();
      }
      catch (Throwable paramArrayOfString)
      {
        Log.e("ImageUtils", "copyExif() - Fail to copy from '" + paramString1 + "' to '" + paramString2 + "'", paramArrayOfString);
        return false;
      }
      return true;
      return true;
      label201:
      i -= 1;
    }
  }
  
  public static Bitmap createBitmapFromRgbaBuffer(ByteBuffer paramByteBuffer1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ByteBuffer paramByteBuffer2, Bitmap paramBitmap)
  {
    if (paramByteBuffer1 == null) {
      return paramBitmap;
    }
    int i = paramInt1 * paramInt2 * 4;
    if ((paramByteBuffer2 == null) || (paramByteBuffer2.capacity() < i))
    {
      paramByteBuffer2 = ByteBuffer.allocate(i);
      removeArgbPaddings(paramByteBuffer1, paramInt1, paramInt2, paramInt3, paramInt4, paramByteBuffer2);
      if ((paramBitmap != null) && (paramBitmap.getWidth() == paramInt1)) {
        break label88;
      }
    }
    for (;;)
    {
      paramByteBuffer1 = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
      label88:
      do
      {
        paramByteBuffer1.copyPixelsFromBuffer(paramByteBuffer2);
        return paramByteBuffer1;
        paramByteBuffer2.position(0);
        break;
        paramByteBuffer1 = paramBitmap;
      } while (paramBitmap.getHeight() == paramInt2);
    }
  }
  
  public static Bitmap createBitmapFromRgbaImagePlane(Image paramImage, ByteBuffer paramByteBuffer, Bitmap paramBitmap)
  {
    if (paramImage == null) {
      return paramBitmap;
    }
    Object localObject = paramImage.getPlanes();
    if (localObject.length > 0)
    {
      localObject = localObject[0];
      return createBitmapFromRgbaBuffer(((Image.Plane)localObject).getBuffer(), paramImage.getWidth(), paramImage.getHeight(), ((Image.Plane)localObject).getPixelStride(), ((Image.Plane)localObject).getRowStride(), paramByteBuffer, paramBitmap);
    }
    return paramBitmap;
  }
  
  public static Bitmap createThumbnailImage(Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    if ((i <= paramInt1) && (j <= paramInt2)) {}
    for (Object localObject1 = paramBitmap;; localObject1 = Bitmap.createScaledBitmap(paramBitmap, ((Size)localObject1).getWidth(), ((Size)localObject1).getHeight(), true))
    {
      Object localObject2 = localObject1;
      if (localObject1 == paramBitmap) {
        localObject2 = paramBitmap.copy(Bitmap.Config.RGB_565, false);
      }
      return (Bitmap)localObject2;
      localObject1 = SizeUtils.getRatioStretchedSize(i, j, paramInt1, paramInt2, true);
    }
  }
  
  public static Bitmap createWithBackground(Bitmap paramBitmap)
  {
    return createWithBackground(paramBitmap, -16777216);
  }
  
  public static Bitmap createWithBackground(Bitmap paramBitmap, int paramInt)
  {
    if (paramBitmap == null) {
      return null;
    }
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    if (paramBitmap.getConfig() != null) {}
    for (Object localObject = paramBitmap.getConfig();; localObject = Bitmap.Config.ARGB_8888)
    {
      localObject = Bitmap.createBitmap(i, j, (Bitmap.Config)localObject);
      ((Bitmap)localObject).eraseColor(paramInt);
      new Canvas((Bitmap)localObject).drawBitmap(paramBitmap, 0.0F, 0.0F, null);
      return (Bitmap)localObject;
    }
  }
  
  public static Bitmap cropBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return cropBitmap(paramBitmap, new Rect(paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public static Bitmap cropBitmap(Bitmap paramBitmap, Rect paramRect)
  {
    if (paramRect.left < 0) {
      paramRect.left = 0;
    }
    if (paramRect.top < 0) {
      paramRect.top = 0;
    }
    if (paramRect.right > paramBitmap.getWidth()) {
      paramRect.right = paramBitmap.getWidth();
    }
    if (paramRect.bottom > paramBitmap.getHeight()) {
      paramRect.bottom = paramBitmap.getHeight();
    }
    int i = Math.abs(paramRect.width());
    int j = Math.abs(paramRect.height());
    Bitmap localBitmap = Bitmap.createBitmap(i, j, paramBitmap.getConfig());
    new Canvas(localBitmap).drawBitmap(paramBitmap, paramRect, new Rect(0, 0, i, j), m_BitmapFilterPaint);
    return localBitmap;
  }
  
  public static Bitmap decodeBitmap(InputStream paramInputStream, int paramInt1, int paramInt2, int paramInt3, Bitmap.Config paramConfig)
  {
    return decodeBitmap(paramInputStream, null, paramInt1, paramInt2, paramInt3, paramConfig, null);
  }
  
  public static Bitmap decodeBitmap(InputStream paramInputStream, int paramInt1, int paramInt2, int paramInt3, Bitmap.Config paramConfig, Ref<Boolean> paramRef)
  {
    return decodeBitmap(paramInputStream, null, paramInt1, paramInt2, paramInt3, paramConfig, paramRef);
  }
  
  public static Bitmap decodeBitmap(InputStream paramInputStream, int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return decodeBitmap(paramInputStream, paramInt1, paramInt2, 0, paramConfig);
  }
  
  /* Error */
  private static Bitmap decodeBitmap(InputStream paramInputStream, Integer paramInteger, int paramInt1, int paramInt2, int paramInt3, Bitmap.Config paramConfig, Ref<Boolean> paramRef)
  {
    // Byte code:
    //   0: iload 4
    //   2: bipush 64
    //   4: iand
    //   5: ifne +1439 -> 1444
    //   8: iconst_1
    //   9: istore 10
    //   11: iload 4
    //   13: bipush 16
    //   15: iand
    //   16: ifeq +1434 -> 1450
    //   19: iconst_1
    //   20: istore 11
    //   22: aload_0
    //   23: invokestatic 326	com/oneplus/media/ImageUtils:isGifHeader	(Ljava/io/InputStream;)Z
    //   26: istore 17
    //   28: aload_0
    //   29: invokestatic 609	com/oneplus/media/ImageUtils:isJfifHeader	(Ljava/io/InputStream;)Z
    //   32: istore 18
    //   34: aload 6
    //   36: ifnull +21 -> 57
    //   39: aload 6
    //   41: invokeinterface 428 1 0
    //   46: checkcast 611	java/lang/Boolean
    //   49: invokevirtual 614	java/lang/Boolean:booleanValue	()Z
    //   52: ifeq +5 -> 57
    //   55: aconst_null
    //   56: areturn
    //   57: aload_1
    //   58: astore 22
    //   60: aload_1
    //   61: ifnonnull +12 -> 73
    //   64: aload_0
    //   65: invokestatic 618	com/oneplus/media/ImageUtils:decodeOrientation	(Ljava/io/InputStream;)I
    //   68: invokestatic 234	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   71: astore 22
    //   73: aload 22
    //   75: invokevirtual 621	java/lang/Integer:intValue	()I
    //   78: bipush 90
    //   80: if_icmpeq +1376 -> 1456
    //   83: aload 22
    //   85: invokevirtual 621	java/lang/Integer:intValue	()I
    //   88: sipush 270
    //   91: if_icmpne +33 -> 124
    //   94: goto +1362 -> 1456
    //   97: aload 6
    //   99: ifnull +31 -> 130
    //   102: aload 6
    //   104: invokeinterface 428 1 0
    //   109: checkcast 611	java/lang/Boolean
    //   112: invokevirtual 614	java/lang/Boolean:booleanValue	()Z
    //   115: istore 19
    //   117: iload 19
    //   119: ifeq +11 -> 130
    //   122: aconst_null
    //   123: areturn
    //   124: iconst_0
    //   125: istore 12
    //   127: goto -30 -> 97
    //   130: aconst_null
    //   131: astore 24
    //   133: iconst_0
    //   134: istore 15
    //   136: iconst_0
    //   137: istore 16
    //   139: iconst_0
    //   140: istore 14
    //   142: iload 17
    //   144: ifeq +560 -> 704
    //   147: aconst_null
    //   148: astore 20
    //   150: aconst_null
    //   151: astore 23
    //   153: aconst_null
    //   154: astore 21
    //   156: aconst_null
    //   157: astore 24
    //   159: new 623	com/oneplus/io/StreamState
    //   162: dup
    //   163: aload_0
    //   164: invokespecial 625	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   167: astore_1
    //   168: aload_0
    //   169: invokestatic 631	android/graphics/Movie:decodeStream	(Ljava/io/InputStream;)Landroid/graphics/Movie;
    //   172: astore 26
    //   174: aload 26
    //   176: iconst_0
    //   177: invokevirtual 635	android/graphics/Movie:setTime	(I)Z
    //   180: pop
    //   181: aload 26
    //   183: invokevirtual 636	android/graphics/Movie:width	()I
    //   186: istore 13
    //   188: aload 26
    //   190: invokevirtual 637	android/graphics/Movie:height	()I
    //   193: istore 8
    //   195: aload 23
    //   197: astore 20
    //   199: aload_1
    //   200: ifnull +11 -> 211
    //   203: aload_1
    //   204: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   207: aload 23
    //   209: astore 20
    //   211: aload 20
    //   213: ifnull +89 -> 302
    //   216: aload 20
    //   218: athrow
    //   219: astore_0
    //   220: iload 10
    //   222: ifeq +12 -> 234
    //   225: ldc 32
    //   227: ldc_w 640
    //   230: aload_0
    //   231: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   234: aconst_null
    //   235: areturn
    //   236: astore 20
    //   238: goto -27 -> 211
    //   241: astore_1
    //   242: aload 24
    //   244: astore_0
    //   245: aload_1
    //   246: athrow
    //   247: astore 6
    //   249: aload_0
    //   250: astore 5
    //   252: aload 6
    //   254: astore_0
    //   255: aload_1
    //   256: astore 6
    //   258: aload 5
    //   260: ifnull +11 -> 271
    //   263: aload 5
    //   265: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   268: aload_1
    //   269: astore 6
    //   271: aload 6
    //   273: ifnull +27 -> 300
    //   276: aload 6
    //   278: athrow
    //   279: aload_1
    //   280: astore 6
    //   282: aload_1
    //   283: aload 5
    //   285: if_acmpeq -14 -> 271
    //   288: aload_1
    //   289: aload 5
    //   291: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   294: aload_1
    //   295: astore 6
    //   297: goto -26 -> 271
    //   300: aload_0
    //   301: athrow
    //   302: iconst_1
    //   303: istore 9
    //   305: iconst_1
    //   306: istore 15
    //   308: aconst_null
    //   309: astore_1
    //   310: aconst_null
    //   311: astore 21
    //   313: iload 13
    //   315: ifle +16 -> 331
    //   318: iload 8
    //   320: istore 4
    //   322: iload 13
    //   324: istore 7
    //   326: iload 8
    //   328: ifgt +261 -> 589
    //   331: aconst_null
    //   332: astore 20
    //   334: aconst_null
    //   335: astore 23
    //   337: aconst_null
    //   338: astore 24
    //   340: aconst_null
    //   341: astore 25
    //   343: new 623	com/oneplus/io/StreamState
    //   346: dup
    //   347: aload_0
    //   348: invokespecial 625	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   351: astore_1
    //   352: ldc 32
    //   354: ldc_w 642
    //   357: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   360: new 328	com/oneplus/util/GifDecoder
    //   363: dup
    //   364: invokespecial 329	com/oneplus/util/GifDecoder:<init>	()V
    //   367: astore 24
    //   369: aload 24
    //   371: aload_0
    //   372: invokevirtual 333	com/oneplus/util/GifDecoder:read	(Ljava/io/InputStream;)V
    //   375: aload 21
    //   377: astore_0
    //   378: iload 15
    //   380: istore 9
    //   382: iload 8
    //   384: istore 4
    //   386: iload 13
    //   388: istore 7
    //   390: aload 24
    //   392: invokevirtual 336	com/oneplus/util/GifDecoder:frameCount	()I
    //   395: ifle +97 -> 492
    //   398: aload 24
    //   400: iconst_0
    //   401: invokevirtual 646	com/oneplus/util/GifDecoder:getFrame	(I)Landroid/graphics/Bitmap;
    //   404: astore 25
    //   406: aload 21
    //   408: astore_0
    //   409: iload 15
    //   411: istore 9
    //   413: iload 8
    //   415: istore 4
    //   417: iload 13
    //   419: istore 7
    //   421: aload 25
    //   423: ifnull +69 -> 492
    //   426: aload 25
    //   428: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   431: istore 7
    //   433: aload 25
    //   435: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   438: istore 4
    //   440: aload 25
    //   442: aload 25
    //   444: invokevirtual 306	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
    //   447: iconst_1
    //   448: invokevirtual 541	android/graphics/Bitmap:copy	(Landroid/graphics/Bitmap$Config;Z)Landroid/graphics/Bitmap;
    //   451: astore_0
    //   452: iconst_0
    //   453: istore 9
    //   455: ldc 32
    //   457: new 193	java/lang/StringBuilder
    //   460: dup
    //   461: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   464: ldc_w 648
    //   467: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   470: iload 7
    //   472: invokevirtual 203	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   475: ldc_w 650
    //   478: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   481: iload 4
    //   483: invokevirtual 203	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   486: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   489: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   492: aload 24
    //   494: invokevirtual 339	com/oneplus/util/GifDecoder:release	()V
    //   497: aload 23
    //   499: astore 20
    //   501: aload_1
    //   502: ifnull +11 -> 513
    //   505: aload_1
    //   506: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   509: aload 23
    //   511: astore 20
    //   513: aload_0
    //   514: astore_1
    //   515: aload 20
    //   517: ifnull +72 -> 589
    //   520: aload 20
    //   522: athrow
    //   523: astore 20
    //   525: goto -12 -> 513
    //   528: astore_1
    //   529: aload 25
    //   531: astore_0
    //   532: aload_1
    //   533: athrow
    //   534: astore 6
    //   536: aload_0
    //   537: astore 5
    //   539: aload 6
    //   541: astore_0
    //   542: aload_1
    //   543: astore 6
    //   545: aload 5
    //   547: ifnull +11 -> 558
    //   550: aload 5
    //   552: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   555: aload_1
    //   556: astore 6
    //   558: aload 6
    //   560: ifnull +27 -> 587
    //   563: aload 6
    //   565: athrow
    //   566: aload_1
    //   567: astore 6
    //   569: aload_1
    //   570: aload 5
    //   572: if_acmpeq -14 -> 558
    //   575: aload_1
    //   576: aload 5
    //   578: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   581: aload_1
    //   582: astore 6
    //   584: goto -26 -> 558
    //   587: aload_0
    //   588: athrow
    //   589: iload 7
    //   591: iload 4
    //   593: aload 5
    //   595: invokestatic 310	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   598: astore_0
    //   599: iload 11
    //   601: ifeq +10 -> 611
    //   604: aload_0
    //   605: ldc_w 558
    //   608: invokevirtual 565	android/graphics/Bitmap:eraseColor	(I)V
    //   611: iload 9
    //   613: ifeq +69 -> 682
    //   616: aload 26
    //   618: new 312	android/graphics/Canvas
    //   621: dup
    //   622: aload_0
    //   623: invokespecial 314	android/graphics/Canvas:<init>	(Landroid/graphics/Bitmap;)V
    //   626: fconst_0
    //   627: fconst_0
    //   628: invokevirtual 654	android/graphics/Movie:draw	(Landroid/graphics/Canvas;FF)V
    //   631: goto +857 -> 1488
    //   634: iload 9
    //   636: iload 8
    //   638: iload_2
    //   639: iload_3
    //   640: iconst_1
    //   641: invokestatic 547	com/oneplus/util/SizeUtils:getRatioStretchedSize	(IIIIZ)Landroid/util/Size;
    //   644: astore_1
    //   645: aload_1
    //   646: invokevirtual 550	android/util/Size:getWidth	()I
    //   649: istore_3
    //   650: aload_1
    //   651: invokevirtual 551	android/util/Size:getHeight	()I
    //   654: istore 4
    //   656: iload 14
    //   658: istore_2
    //   659: aload 6
    //   661: ifnull +483 -> 1144
    //   664: aload 6
    //   666: invokeinterface 428 1 0
    //   671: checkcast 611	java/lang/Boolean
    //   674: invokevirtual 614	java/lang/Boolean:booleanValue	()Z
    //   677: ifeq +467 -> 1144
    //   680: aconst_null
    //   681: areturn
    //   682: aload_1
    //   683: ifnull +805 -> 1488
    //   686: new 312	android/graphics/Canvas
    //   689: dup
    //   690: aload_0
    //   691: invokespecial 314	android/graphics/Canvas:<init>	(Landroid/graphics/Bitmap;)V
    //   694: aload_1
    //   695: fconst_0
    //   696: fconst_0
    //   697: aconst_null
    //   698: invokevirtual 568	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;FFLandroid/graphics/Paint;)V
    //   701: goto +787 -> 1488
    //   704: new 656	android/graphics/BitmapFactory$Options
    //   707: dup
    //   708: invokespecial 657	android/graphics/BitmapFactory$Options:<init>	()V
    //   711: astore 26
    //   713: aload 26
    //   715: iconst_1
    //   716: putfield 661	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   719: aconst_null
    //   720: astore 20
    //   722: aconst_null
    //   723: astore 23
    //   725: aconst_null
    //   726: astore 21
    //   728: aconst_null
    //   729: astore 25
    //   731: new 623	com/oneplus/io/StreamState
    //   734: dup
    //   735: aload_0
    //   736: invokespecial 625	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   739: astore_1
    //   740: aload_0
    //   741: aconst_null
    //   742: aload 26
    //   744: invokestatic 666	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   747: pop
    //   748: aload 23
    //   750: astore 20
    //   752: aload_1
    //   753: ifnull +11 -> 764
    //   756: aload_1
    //   757: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   760: aload 23
    //   762: astore 20
    //   764: aload 20
    //   766: ifnull +125 -> 891
    //   769: aload 20
    //   771: athrow
    //   772: astore_1
    //   773: iload 10
    //   775: ifeq +12 -> 787
    //   778: ldc 32
    //   780: ldc_w 668
    //   783: aload_1
    //   784: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   787: aload 26
    //   789: getfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   792: istore 9
    //   794: aload 26
    //   796: getfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   799: istore 13
    //   801: iload 13
    //   803: istore 8
    //   805: iload 9
    //   807: istore 7
    //   809: iload 12
    //   811: ifeq +11 -> 822
    //   814: iload 13
    //   816: istore 7
    //   818: iload 9
    //   820: istore 8
    //   822: iload 7
    //   824: iload 8
    //   826: iload_2
    //   827: iload_3
    //   828: iconst_1
    //   829: invokestatic 547	com/oneplus/util/SizeUtils:getRatioStretchedSize	(IIIIZ)Landroid/util/Size;
    //   832: astore_1
    //   833: aload_1
    //   834: invokevirtual 550	android/util/Size:getWidth	()I
    //   837: istore_3
    //   838: aload_1
    //   839: invokevirtual 551	android/util/Size:getHeight	()I
    //   842: istore 9
    //   844: aload 26
    //   846: iload 7
    //   848: iload 8
    //   850: iload_3
    //   851: iload 9
    //   853: invokestatic 676	com/oneplus/media/ImageUtils:calculateSampleSize	(IIII)I
    //   856: putfield 679	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   859: aload 6
    //   861: ifnull +102 -> 963
    //   864: aload 6
    //   866: invokeinterface 428 1 0
    //   871: checkcast 611	java/lang/Boolean
    //   874: invokevirtual 614	java/lang/Boolean:booleanValue	()Z
    //   877: istore 17
    //   879: iload 17
    //   881: ifeq +82 -> 963
    //   884: aconst_null
    //   885: areturn
    //   886: astore 20
    //   888: goto -124 -> 764
    //   891: goto -104 -> 787
    //   894: astore 20
    //   896: aload 25
    //   898: astore_1
    //   899: aload 20
    //   901: athrow
    //   902: astore 23
    //   904: aload_1
    //   905: astore 21
    //   907: aload 23
    //   909: astore_1
    //   910: aload 20
    //   912: astore 23
    //   914: aload 21
    //   916: ifnull +12 -> 928
    //   919: aload 21
    //   921: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   924: aload 20
    //   926: astore 23
    //   928: aload 23
    //   930: ifnull +31 -> 961
    //   933: aload 23
    //   935: athrow
    //   936: aload 20
    //   938: astore 23
    //   940: aload 20
    //   942: aload 21
    //   944: if_acmpeq -16 -> 928
    //   947: aload 20
    //   949: aload 21
    //   951: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   954: aload 20
    //   956: astore 23
    //   958: goto -30 -> 928
    //   961: aload_1
    //   962: athrow
    //   963: aload 24
    //   965: astore_1
    //   966: aload 26
    //   968: getfield 679	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   971: iconst_1
    //   972: if_icmpne +30 -> 1002
    //   975: aload 24
    //   977: astore_1
    //   978: iload 7
    //   980: iload 8
    //   982: imul
    //   983: ldc 24
    //   985: if_icmplt +17 -> 1002
    //   988: aload_0
    //   989: iload_3
    //   990: iload 9
    //   992: iload 4
    //   994: aload 5
    //   996: aload 6
    //   998: invokestatic 682	com/oneplus/media/ImageUtils:decodeBitmapProgressively	(Ljava/io/InputStream;IIILandroid/graphics/Bitmap$Config;Lcom/oneplus/base/Ref;)Landroid/graphics/Bitmap;
    //   1001: astore_1
    //   1002: aload 6
    //   1004: ifnull +21 -> 1025
    //   1007: aload 6
    //   1009: invokeinterface 428 1 0
    //   1014: checkcast 611	java/lang/Boolean
    //   1017: invokevirtual 614	java/lang/Boolean:booleanValue	()Z
    //   1020: ifeq +5 -> 1025
    //   1023: aconst_null
    //   1024: areturn
    //   1025: aload_1
    //   1026: astore 20
    //   1028: iload 16
    //   1030: istore_2
    //   1031: aload_1
    //   1032: ifnonnull +84 -> 1116
    //   1035: aload 26
    //   1037: iconst_0
    //   1038: putfield 661	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   1041: iload 4
    //   1043: iconst_1
    //   1044: iand
    //   1045: ifeq +487 -> 1532
    //   1048: iconst_1
    //   1049: istore 17
    //   1051: aload 26
    //   1053: iload 17
    //   1055: putfield 685	android/graphics/BitmapFactory$Options:inPreferQualityOverSpeed	Z
    //   1058: aload 26
    //   1060: aload 5
    //   1062: putfield 688	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   1065: aload 26
    //   1067: iconst_1
    //   1068: putfield 691	android/graphics/BitmapFactory$Options:inDither	Z
    //   1071: iload 4
    //   1073: bipush 8
    //   1075: iand
    //   1076: ifeq +9 -> 1085
    //   1079: aload 26
    //   1081: iconst_1
    //   1082: putfield 694	android/graphics/BitmapFactory$Options:inMutable	Z
    //   1085: iload 15
    //   1087: istore_2
    //   1088: aload 26
    //   1090: getfield 679	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   1093: iconst_2
    //   1094: if_icmple +13 -> 1107
    //   1097: iload 15
    //   1099: istore_2
    //   1100: iload 18
    //   1102: ifeq +5 -> 1107
    //   1105: iconst_1
    //   1106: istore_2
    //   1107: aload_0
    //   1108: aconst_null
    //   1109: aload 26
    //   1111: invokestatic 666	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   1114: astore 20
    //   1116: iload 9
    //   1118: istore 4
    //   1120: aload 20
    //   1122: astore_0
    //   1123: aload 20
    //   1125: ifnonnull -466 -> 659
    //   1128: iload 10
    //   1130: ifeq +400 -> 1530
    //   1133: ldc 32
    //   1135: ldc_w 696
    //   1138: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   1141: goto +389 -> 1530
    //   1144: aload_0
    //   1145: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   1148: iload_3
    //   1149: if_icmpne +12 -> 1161
    //   1152: aload_0
    //   1153: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   1156: iload 4
    //   1158: if_icmpeq +114 -> 1272
    //   1161: new 700	android/graphics/Matrix
    //   1164: dup
    //   1165: invokespecial 701	android/graphics/Matrix:<init>	()V
    //   1168: astore_1
    //   1169: aload_0
    //   1170: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   1173: iload_3
    //   1174: if_icmpne +12 -> 1186
    //   1177: aload_0
    //   1178: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   1181: iload 4
    //   1183: if_icmpeq +30 -> 1213
    //   1186: iload 12
    //   1188: ifeq +97 -> 1285
    //   1191: aload_1
    //   1192: iload_3
    //   1193: i2f
    //   1194: aload_0
    //   1195: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   1198: i2f
    //   1199: fdiv
    //   1200: iload 4
    //   1202: i2f
    //   1203: aload_0
    //   1204: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   1207: i2f
    //   1208: fdiv
    //   1209: invokevirtual 705	android/graphics/Matrix:postScale	(FF)Z
    //   1212: pop
    //   1213: aload 22
    //   1215: invokevirtual 621	java/lang/Integer:intValue	()I
    //   1218: ifeq +14 -> 1232
    //   1221: aload_1
    //   1222: aload 22
    //   1224: invokevirtual 621	java/lang/Integer:intValue	()I
    //   1227: i2f
    //   1228: invokevirtual 709	android/graphics/Matrix:postRotate	(F)Z
    //   1231: pop
    //   1232: aload_0
    //   1233: iconst_0
    //   1234: iconst_0
    //   1235: aload_0
    //   1236: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   1239: aload_0
    //   1240: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   1243: aload_1
    //   1244: iconst_1
    //   1245: invokestatic 712	android/graphics/Bitmap:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   1248: astore_1
    //   1249: aload 6
    //   1251: ifnull +59 -> 1310
    //   1254: aload 6
    //   1256: invokeinterface 428 1 0
    //   1261: checkcast 611	java/lang/Boolean
    //   1264: invokevirtual 614	java/lang/Boolean:booleanValue	()Z
    //   1267: ifeq +43 -> 1310
    //   1270: aconst_null
    //   1271: areturn
    //   1272: aload_0
    //   1273: astore_1
    //   1274: aload 22
    //   1276: invokevirtual 621	java/lang/Integer:intValue	()I
    //   1279: ifeq -30 -> 1249
    //   1282: goto -121 -> 1161
    //   1285: aload_1
    //   1286: iload_3
    //   1287: i2f
    //   1288: aload_0
    //   1289: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   1292: i2f
    //   1293: fdiv
    //   1294: iload 4
    //   1296: i2f
    //   1297: aload_0
    //   1298: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   1301: i2f
    //   1302: fdiv
    //   1303: invokevirtual 705	android/graphics/Matrix:postScale	(FF)Z
    //   1306: pop
    //   1307: goto -94 -> 1213
    //   1310: iload_2
    //   1311: ifeq +13 -> 1324
    //   1314: iload 18
    //   1316: ifeq +8 -> 1324
    //   1319: aload_1
    //   1320: invokestatic 716	com/oneplus/media/ImageUtils:fillOuterPixels	(Landroid/graphics/Bitmap;)Z
    //   1323: pop
    //   1324: aload 6
    //   1326: ifnull +212 -> 1538
    //   1329: aload 6
    //   1331: invokeinterface 428 1 0
    //   1336: checkcast 611	java/lang/Boolean
    //   1339: invokevirtual 614	java/lang/Boolean:booleanValue	()Z
    //   1342: ifeq +196 -> 1538
    //   1345: aconst_null
    //   1346: areturn
    //   1347: aload 5
    //   1349: getstatic 67	android/graphics/Bitmap$Config:RGB_565	Landroid/graphics/Bitmap$Config;
    //   1352: if_acmpeq +196 -> 1548
    //   1355: aload_1
    //   1356: invokestatic 718	com/oneplus/media/ImageUtils:createWithBackground	(Landroid/graphics/Bitmap;)Landroid/graphics/Bitmap;
    //   1359: astore_0
    //   1360: aload_0
    //   1361: areturn
    //   1362: astore_1
    //   1363: goto -453 -> 910
    //   1366: astore 23
    //   1368: aload_1
    //   1369: astore 21
    //   1371: aload 23
    //   1373: astore_1
    //   1374: goto -464 -> 910
    //   1377: astore 20
    //   1379: goto -480 -> 899
    //   1382: astore_0
    //   1383: aload 20
    //   1385: astore_1
    //   1386: aload 24
    //   1388: astore 5
    //   1390: goto -848 -> 542
    //   1393: astore_0
    //   1394: aload_1
    //   1395: astore 5
    //   1397: aload 20
    //   1399: astore_1
    //   1400: goto -858 -> 542
    //   1403: astore 5
    //   1405: aload_1
    //   1406: astore_0
    //   1407: aload 5
    //   1409: astore_1
    //   1410: goto -878 -> 532
    //   1413: astore_0
    //   1414: aload 20
    //   1416: astore_1
    //   1417: aload 21
    //   1419: astore 5
    //   1421: goto -1166 -> 255
    //   1424: astore_0
    //   1425: aload_1
    //   1426: astore 5
    //   1428: aload 20
    //   1430: astore_1
    //   1431: goto -1176 -> 255
    //   1434: astore 5
    //   1436: aload_1
    //   1437: astore_0
    //   1438: aload 5
    //   1440: astore_1
    //   1441: goto -1196 -> 245
    //   1444: iconst_0
    //   1445: istore 10
    //   1447: goto -1436 -> 11
    //   1450: iconst_0
    //   1451: istore 11
    //   1453: goto -1431 -> 22
    //   1456: iconst_1
    //   1457: istore 12
    //   1459: goto -1362 -> 97
    //   1462: astore 5
    //   1464: aload_1
    //   1465: ifnonnull -1186 -> 279
    //   1468: aload 5
    //   1470: astore 6
    //   1472: goto -1201 -> 271
    //   1475: astore 5
    //   1477: aload_1
    //   1478: ifnonnull -912 -> 566
    //   1481: aload 5
    //   1483: astore 6
    //   1485: goto -927 -> 558
    //   1488: iload 4
    //   1490: istore 8
    //   1492: iload 7
    //   1494: istore 9
    //   1496: iload 12
    //   1498: ifeq -864 -> 634
    //   1501: iload 7
    //   1503: istore 8
    //   1505: iload 4
    //   1507: istore 9
    //   1509: goto -875 -> 634
    //   1512: astore_1
    //   1513: goto -740 -> 773
    //   1516: astore 21
    //   1518: aload 20
    //   1520: ifnonnull -584 -> 936
    //   1523: aload 21
    //   1525: astore 23
    //   1527: goto -599 -> 928
    //   1530: aconst_null
    //   1531: areturn
    //   1532: iconst_0
    //   1533: istore 17
    //   1535: goto -484 -> 1051
    //   1538: iload 11
    //   1540: ifeq +8 -> 1548
    //   1543: iload 18
    //   1545: ifeq -198 -> 1347
    //   1548: aload_1
    //   1549: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1550	0	paramInputStream	InputStream
    //   0	1550	1	paramInteger	Integer
    //   0	1550	2	paramInt1	int
    //   0	1550	3	paramInt2	int
    //   0	1550	4	paramInt3	int
    //   0	1550	5	paramConfig	Bitmap.Config
    //   0	1550	6	paramRef	Ref<Boolean>
    //   324	1178	7	i	int
    //   193	1311	8	j	int
    //   303	1205	9	k	int
    //   9	1437	10	m	int
    //   20	1519	11	n	int
    //   125	1372	12	i1	int
    //   186	629	13	i2	int
    //   140	517	14	i3	int
    //   134	964	15	i4	int
    //   137	892	16	i5	int
    //   26	1508	17	bool1	boolean
    //   32	1512	18	bool2	boolean
    //   115	3	19	bool3	boolean
    //   148	69	20	localObject1	Object
    //   236	1	20	localThrowable1	Throwable
    //   332	189	20	localObject2	Object
    //   523	1	20	localThrowable2	Throwable
    //   720	50	20	localObject3	Object
    //   886	1	20	localThrowable3	Throwable
    //   894	61	20	localThrowable4	Throwable
    //   1026	98	20	localObject4	Object
    //   1377	142	20	localThrowable5	Throwable
    //   154	1264	21	localInteger1	Integer
    //   1516	8	21	localThrowable6	Throwable
    //   58	1217	22	localInteger2	Integer
    //   151	610	23	localObject5	Object
    //   902	6	23	localObject6	Object
    //   912	45	23	localThrowable7	Throwable
    //   1366	6	23	localObject7	Object
    //   1525	1	23	localObject8	Object
    //   131	1256	24	localGifDecoder	com.oneplus.util.GifDecoder
    //   341	556	25	localBitmap	Bitmap
    //   172	938	26	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   22	34	219	java/lang/Throwable
    //   39	55	219	java/lang/Throwable
    //   64	73	219	java/lang/Throwable
    //   73	94	219	java/lang/Throwable
    //   102	117	219	java/lang/Throwable
    //   216	219	219	java/lang/Throwable
    //   276	279	219	java/lang/Throwable
    //   288	294	219	java/lang/Throwable
    //   300	302	219	java/lang/Throwable
    //   520	523	219	java/lang/Throwable
    //   563	566	219	java/lang/Throwable
    //   575	581	219	java/lang/Throwable
    //   587	589	219	java/lang/Throwable
    //   589	599	219	java/lang/Throwable
    //   604	611	219	java/lang/Throwable
    //   616	631	219	java/lang/Throwable
    //   634	656	219	java/lang/Throwable
    //   664	680	219	java/lang/Throwable
    //   686	701	219	java/lang/Throwable
    //   704	719	219	java/lang/Throwable
    //   778	787	219	java/lang/Throwable
    //   787	801	219	java/lang/Throwable
    //   822	859	219	java/lang/Throwable
    //   864	879	219	java/lang/Throwable
    //   966	975	219	java/lang/Throwable
    //   988	1002	219	java/lang/Throwable
    //   1007	1023	219	java/lang/Throwable
    //   1035	1041	219	java/lang/Throwable
    //   1051	1071	219	java/lang/Throwable
    //   1079	1085	219	java/lang/Throwable
    //   1088	1097	219	java/lang/Throwable
    //   1107	1116	219	java/lang/Throwable
    //   1133	1141	219	java/lang/Throwable
    //   1144	1161	219	java/lang/Throwable
    //   1161	1186	219	java/lang/Throwable
    //   1191	1213	219	java/lang/Throwable
    //   1213	1232	219	java/lang/Throwable
    //   1232	1249	219	java/lang/Throwable
    //   1254	1270	219	java/lang/Throwable
    //   1274	1282	219	java/lang/Throwable
    //   1285	1307	219	java/lang/Throwable
    //   1319	1324	219	java/lang/Throwable
    //   1329	1345	219	java/lang/Throwable
    //   1347	1360	219	java/lang/Throwable
    //   203	207	236	java/lang/Throwable
    //   159	168	241	java/lang/Throwable
    //   245	247	247	finally
    //   505	509	523	java/lang/Throwable
    //   343	352	528	java/lang/Throwable
    //   532	534	534	finally
    //   769	772	772	java/lang/Throwable
    //   756	760	886	java/lang/Throwable
    //   731	740	894	java/lang/Throwable
    //   899	902	902	finally
    //   731	740	1362	finally
    //   740	748	1366	finally
    //   740	748	1377	java/lang/Throwable
    //   343	352	1382	finally
    //   352	375	1393	finally
    //   390	406	1393	finally
    //   426	452	1393	finally
    //   455	492	1393	finally
    //   492	497	1393	finally
    //   352	375	1403	java/lang/Throwable
    //   390	406	1403	java/lang/Throwable
    //   426	452	1403	java/lang/Throwable
    //   455	492	1403	java/lang/Throwable
    //   492	497	1403	java/lang/Throwable
    //   159	168	1413	finally
    //   168	195	1424	finally
    //   168	195	1434	java/lang/Throwable
    //   263	268	1462	java/lang/Throwable
    //   550	555	1475	java/lang/Throwable
    //   933	936	1512	java/lang/Throwable
    //   947	954	1512	java/lang/Throwable
    //   961	963	1512	java/lang/Throwable
    //   919	924	1516	java/lang/Throwable
  }
  
  public static Bitmap decodeBitmap(String paramString, int paramInt1, int paramInt2)
  {
    return decodeBitmap(paramString, paramInt1, paramInt2, Bitmap.Config.RGB_565);
  }
  
  public static Bitmap decodeBitmap(String paramString, int paramInt1, int paramInt2, int paramInt3, Bitmap.Config paramConfig)
  {
    return decodeBitmap(paramString, paramInt1, paramInt2, paramInt3, paramConfig, null);
  }
  
  /* Error */
  public static Bitmap decodeBitmap(String paramString, int paramInt1, int paramInt2, int paramInt3, Bitmap.Config paramConfig, Ref<Boolean> paramRef)
  {
    // Byte code:
    //   0: iload_3
    //   1: bipush 64
    //   3: iand
    //   4: ifne +451 -> 455
    //   7: iconst_1
    //   8: istore 6
    //   10: iload_3
    //   11: iconst_4
    //   12: iand
    //   13: ifeq +448 -> 461
    //   16: iconst_1
    //   17: istore 7
    //   19: aconst_null
    //   20: astore 18
    //   22: aconst_null
    //   23: astore 13
    //   25: aconst_null
    //   26: astore 11
    //   28: aconst_null
    //   29: astore 20
    //   31: aconst_null
    //   32: astore 19
    //   34: aconst_null
    //   35: astore 12
    //   37: aload 12
    //   39: astore 10
    //   41: iload_3
    //   42: iconst_2
    //   43: iand
    //   44: ifne +404 -> 448
    //   47: iload 7
    //   49: ifne +25 -> 74
    //   52: aload 12
    //   54: astore 10
    //   56: iload_1
    //   57: sipush 256
    //   60: if_icmpgt +388 -> 448
    //   63: aload 12
    //   65: astore 10
    //   67: iload_2
    //   68: sipush 256
    //   71: if_icmpgt +377 -> 448
    //   74: aconst_null
    //   75: astore 17
    //   77: aconst_null
    //   78: astore 15
    //   80: aconst_null
    //   81: astore 16
    //   83: aconst_null
    //   84: astore 12
    //   86: aconst_null
    //   87: astore 10
    //   89: aload_0
    //   90: ldc2_w 35
    //   93: invokestatic 733	com/oneplus/io/FileUtils:openLockedInputStream	(Ljava/lang/String;J)Ljava/io/InputStream;
    //   96: astore 14
    //   98: aload 18
    //   100: astore 13
    //   102: aload 19
    //   104: astore 12
    //   106: new 357	android/media/ExifInterface
    //   109: dup
    //   110: aload_0
    //   111: invokespecial 374	android/media/ExifInterface:<init>	(Ljava/lang/String;)V
    //   114: astore 21
    //   116: aload 18
    //   118: astore 13
    //   120: aload 20
    //   122: astore 11
    //   124: aload 19
    //   126: astore 12
    //   128: aload 21
    //   130: invokevirtual 736	android/media/ExifInterface:hasThumbnail	()Z
    //   133: ifeq +468 -> 601
    //   136: aload 18
    //   138: astore 13
    //   140: aload 19
    //   142: astore 12
    //   144: aload 21
    //   146: ldc_w 738
    //   149: iconst_0
    //   150: invokevirtual 742	android/media/ExifInterface:getAttributeInt	(Ljava/lang/String;I)I
    //   153: invokestatic 745	com/oneplus/media/ImageUtils:exifOrientationToDegrees	(I)I
    //   156: invokestatic 234	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   159: astore 10
    //   161: aload 10
    //   163: astore 13
    //   165: aload 10
    //   167: astore 12
    //   169: aload 21
    //   171: invokevirtual 749	android/media/ExifInterface:getThumbnail	()[B
    //   174: astore 11
    //   176: aload 10
    //   178: astore 13
    //   180: aload 10
    //   182: astore 12
    //   184: new 656	android/graphics/BitmapFactory$Options
    //   187: dup
    //   188: invokespecial 657	android/graphics/BitmapFactory$Options:<init>	()V
    //   191: astore 18
    //   193: aload 10
    //   195: astore 13
    //   197: aload 10
    //   199: astore 12
    //   201: aload 18
    //   203: iconst_1
    //   204: putfield 661	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   207: aload 10
    //   209: astore 13
    //   211: aload 10
    //   213: astore 12
    //   215: aload 11
    //   217: iconst_0
    //   218: aload 11
    //   220: arraylength
    //   221: aload 18
    //   223: invokestatic 753	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   226: pop
    //   227: aload 10
    //   229: astore 13
    //   231: aload 10
    //   233: astore 12
    //   235: aload 10
    //   237: invokevirtual 621	java/lang/Integer:intValue	()I
    //   240: lookupswitch	default:+910->1150, 90:+227->467, 270:+227->467
    //   268: iload 7
    //   270: ifne +20 -> 290
    //   273: aload 10
    //   275: astore 13
    //   277: aload 10
    //   279: astore 12
    //   281: aload 18
    //   283: getfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   286: iload_1
    //   287: if_icmplt +289 -> 576
    //   290: aload 10
    //   292: astore 13
    //   294: aload 10
    //   296: astore 12
    //   298: aload 18
    //   300: iconst_0
    //   301: putfield 661	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   304: aload 10
    //   306: astore 13
    //   308: aload 10
    //   310: astore 12
    //   312: aload 18
    //   314: iconst_1
    //   315: putfield 691	android/graphics/BitmapFactory$Options:inDither	Z
    //   318: iload_3
    //   319: iconst_1
    //   320: iand
    //   321: ifeq +314 -> 635
    //   324: iconst_1
    //   325: istore 9
    //   327: aload 10
    //   329: astore 13
    //   331: aload 10
    //   333: astore 12
    //   335: aload 18
    //   337: iload 9
    //   339: putfield 685	android/graphics/BitmapFactory$Options:inPreferQualityOverSpeed	Z
    //   342: aload 10
    //   344: astore 13
    //   346: aload 10
    //   348: astore 12
    //   350: aload 18
    //   352: aload 4
    //   354: putfield 688	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   357: aload 10
    //   359: astore 13
    //   361: aload 10
    //   363: astore 12
    //   365: aload 11
    //   367: iconst_0
    //   368: aload 11
    //   370: arraylength
    //   371: aload 18
    //   373: invokestatic 753	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   376: astore 18
    //   378: aload 10
    //   380: astore 11
    //   382: aload 18
    //   384: ifnull +217 -> 601
    //   387: aload 10
    //   389: astore 13
    //   391: aload 10
    //   393: astore 12
    //   395: aload 10
    //   397: invokevirtual 621	java/lang/Integer:intValue	()I
    //   400: ifne +249 -> 649
    //   403: aload 10
    //   405: astore 13
    //   407: aload 10
    //   409: astore 12
    //   411: aload 18
    //   413: iload_1
    //   414: iload_2
    //   415: invokestatic 755	com/oneplus/media/ImageUtils:createThumbnailImage	(Landroid/graphics/Bitmap;II)Landroid/graphics/Bitmap;
    //   418: astore 15
    //   420: aload 16
    //   422: astore 11
    //   424: aload 14
    //   426: ifnull +12 -> 438
    //   429: aload 14
    //   431: invokevirtual 758	java/io/InputStream:close	()V
    //   434: aload 16
    //   436: astore 11
    //   438: aload 11
    //   440: ifnull +206 -> 646
    //   443: aload 11
    //   445: athrow
    //   446: astore 11
    //   448: iload 7
    //   450: ifeq +423 -> 873
    //   453: aconst_null
    //   454: areturn
    //   455: iconst_0
    //   456: istore 6
    //   458: goto -448 -> 10
    //   461: iconst_0
    //   462: istore 7
    //   464: goto -445 -> 19
    //   467: aload 10
    //   469: astore 13
    //   471: aload 10
    //   473: astore 12
    //   475: aload 18
    //   477: getfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   480: istore 8
    //   482: aload 10
    //   484: astore 13
    //   486: aload 10
    //   488: astore 12
    //   490: aload 18
    //   492: aload 18
    //   494: getfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   497: putfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   500: aload 10
    //   502: astore 13
    //   504: aload 10
    //   506: astore 12
    //   508: aload 18
    //   510: iload 8
    //   512: putfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   515: goto -247 -> 268
    //   518: astore 12
    //   520: aload 14
    //   522: astore 10
    //   524: aload 13
    //   526: astore 11
    //   528: aload 12
    //   530: athrow
    //   531: astore 13
    //   533: aload 10
    //   535: astore 14
    //   537: aload 12
    //   539: astore 10
    //   541: aload 10
    //   543: astore 12
    //   545: aload 14
    //   547: ifnull +12 -> 559
    //   550: aload 14
    //   552: invokevirtual 758	java/io/InputStream:close	()V
    //   555: aload 10
    //   557: astore 12
    //   559: aload 12
    //   561: ifnull +309 -> 870
    //   564: aload 12
    //   566: athrow
    //   567: astore 10
    //   569: aload 11
    //   571: astore 10
    //   573: goto -125 -> 448
    //   576: aload 10
    //   578: astore 13
    //   580: aload 10
    //   582: astore 12
    //   584: aload 18
    //   586: getfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   589: istore 8
    //   591: iload 8
    //   593: iload_2
    //   594: if_icmpge -304 -> 290
    //   597: aload 10
    //   599: astore 11
    //   601: aload 17
    //   603: astore 12
    //   605: aload 14
    //   607: ifnull +12 -> 619
    //   610: aload 14
    //   612: invokevirtual 758	java/io/InputStream:close	()V
    //   615: aload 17
    //   617: astore 12
    //   619: aload 11
    //   621: astore 10
    //   623: aload 12
    //   625: ifnull -177 -> 448
    //   628: aload 11
    //   630: astore 10
    //   632: aload 12
    //   634: athrow
    //   635: iconst_0
    //   636: istore 9
    //   638: goto -311 -> 327
    //   641: astore 11
    //   643: goto -205 -> 438
    //   646: aload 15
    //   648: areturn
    //   649: aload 10
    //   651: astore 13
    //   653: aload 10
    //   655: astore 12
    //   657: aload 10
    //   659: invokevirtual 621	java/lang/Integer:intValue	()I
    //   662: lookupswitch	default:+491->1153, 90:+136->798, 270:+136->798
    //   688: aload 10
    //   690: astore 13
    //   692: aload 10
    //   694: astore 12
    //   696: aload 18
    //   698: iload_1
    //   699: iload_2
    //   700: invokestatic 755	com/oneplus/media/ImageUtils:createThumbnailImage	(Landroid/graphics/Bitmap;II)Landroid/graphics/Bitmap;
    //   703: astore 11
    //   705: aload 10
    //   707: astore 13
    //   709: aload 10
    //   711: astore 12
    //   713: new 700	android/graphics/Matrix
    //   716: dup
    //   717: invokespecial 701	android/graphics/Matrix:<init>	()V
    //   720: astore 16
    //   722: aload 10
    //   724: astore 13
    //   726: aload 10
    //   728: astore 12
    //   730: aload 16
    //   732: aload 10
    //   734: invokevirtual 621	java/lang/Integer:intValue	()I
    //   737: i2f
    //   738: invokevirtual 709	android/graphics/Matrix:postRotate	(F)Z
    //   741: pop
    //   742: aload 10
    //   744: astore 13
    //   746: aload 10
    //   748: astore 12
    //   750: aload 11
    //   752: iconst_0
    //   753: iconst_0
    //   754: aload 11
    //   756: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   759: aload 11
    //   761: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   764: aload 16
    //   766: iconst_0
    //   767: invokestatic 712	android/graphics/Bitmap:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   770: astore 16
    //   772: aload 15
    //   774: astore 11
    //   776: aload 14
    //   778: ifnull +12 -> 790
    //   781: aload 14
    //   783: invokevirtual 758	java/io/InputStream:close	()V
    //   786: aload 15
    //   788: astore 11
    //   790: aload 11
    //   792: ifnull +31 -> 823
    //   795: aload 11
    //   797: athrow
    //   798: aload 10
    //   800: astore 13
    //   802: aload 10
    //   804: astore 12
    //   806: aload 18
    //   808: iload_2
    //   809: iload_1
    //   810: invokestatic 755	com/oneplus/media/ImageUtils:createThumbnailImage	(Landroid/graphics/Bitmap;II)Landroid/graphics/Bitmap;
    //   813: astore 11
    //   815: goto -110 -> 705
    //   818: astore 11
    //   820: goto -30 -> 790
    //   823: aload 16
    //   825: areturn
    //   826: astore 12
    //   828: goto -209 -> 619
    //   831: astore 14
    //   833: aload 10
    //   835: ifnonnull +10 -> 845
    //   838: aload 14
    //   840: astore 12
    //   842: goto -283 -> 559
    //   845: aload 10
    //   847: astore 12
    //   849: aload 10
    //   851: aload 14
    //   853: if_acmpeq -294 -> 559
    //   856: aload 10
    //   858: aload 14
    //   860: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   863: aload 10
    //   865: astore 12
    //   867: goto -308 -> 559
    //   870: aload 13
    //   872: athrow
    //   873: aconst_null
    //   874: astore 14
    //   876: aconst_null
    //   877: astore 15
    //   879: aconst_null
    //   880: astore 12
    //   882: aconst_null
    //   883: astore 11
    //   885: aload_0
    //   886: ldc2_w 35
    //   889: invokestatic 733	com/oneplus/io/FileUtils:openLockedInputStream	(Ljava/lang/String;J)Ljava/io/InputStream;
    //   892: astore 13
    //   894: aload 13
    //   896: astore 11
    //   898: aload 13
    //   900: astore 12
    //   902: aload 13
    //   904: aload 10
    //   906: iload_1
    //   907: iload_2
    //   908: iload_3
    //   909: aload 4
    //   911: aload 5
    //   913: invokestatic 600	com/oneplus/media/ImageUtils:decodeBitmap	(Ljava/io/InputStream;Ljava/lang/Integer;IIILandroid/graphics/Bitmap$Config;Lcom/oneplus/base/Ref;)Landroid/graphics/Bitmap;
    //   916: astore 5
    //   918: aload 5
    //   920: ifnonnull +47 -> 967
    //   923: iload 6
    //   925: ifeq +42 -> 967
    //   928: aload 13
    //   930: astore 11
    //   932: aload 13
    //   934: astore 12
    //   936: ldc 32
    //   938: new 193	java/lang/StringBuilder
    //   941: dup
    //   942: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   945: ldc_w 760
    //   948: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   951: aload_0
    //   952: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   955: ldc_w 495
    //   958: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   961: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   964: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   967: aload 15
    //   969: astore 4
    //   971: aload 13
    //   973: ifnull +12 -> 985
    //   976: aload 13
    //   978: invokevirtual 758	java/io/InputStream:close	()V
    //   981: aload 15
    //   983: astore 4
    //   985: aload 4
    //   987: ifnull +53 -> 1040
    //   990: aload 4
    //   992: athrow
    //   993: astore 4
    //   995: iload 6
    //   997: ifeq +36 -> 1033
    //   1000: ldc 32
    //   1002: new 193	java/lang/StringBuilder
    //   1005: dup
    //   1006: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   1009: ldc_w 760
    //   1012: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1015: aload_0
    //   1016: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1019: ldc_w 495
    //   1022: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1025: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1028: aload 4
    //   1030: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1033: aconst_null
    //   1034: areturn
    //   1035: astore 4
    //   1037: goto -52 -> 985
    //   1040: aload 5
    //   1042: areturn
    //   1043: astore 4
    //   1045: aload 4
    //   1047: athrow
    //   1048: astore 5
    //   1050: aload 4
    //   1052: astore 10
    //   1054: aload 11
    //   1056: ifnull +12 -> 1068
    //   1059: aload 11
    //   1061: invokevirtual 758	java/io/InputStream:close	()V
    //   1064: aload 4
    //   1066: astore 10
    //   1068: aload 10
    //   1070: ifnull +31 -> 1101
    //   1073: aload 10
    //   1075: athrow
    //   1076: aload 4
    //   1078: astore 10
    //   1080: aload 4
    //   1082: aload 11
    //   1084: if_acmpeq -16 -> 1068
    //   1087: aload 4
    //   1089: aload 11
    //   1091: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   1094: aload 4
    //   1096: astore 10
    //   1098: goto -30 -> 1068
    //   1101: aload 5
    //   1103: athrow
    //   1104: astore 5
    //   1106: aload 14
    //   1108: astore 4
    //   1110: aload 12
    //   1112: astore 11
    //   1114: goto -64 -> 1050
    //   1117: astore 13
    //   1119: aconst_null
    //   1120: astore 10
    //   1122: aload 12
    //   1124: astore 14
    //   1126: goto -585 -> 541
    //   1129: astore 13
    //   1131: aconst_null
    //   1132: astore 10
    //   1134: aload 12
    //   1136: astore 11
    //   1138: goto -597 -> 541
    //   1141: astore 12
    //   1143: aload 13
    //   1145: astore 11
    //   1147: goto -619 -> 528
    //   1150: goto -882 -> 268
    //   1153: goto -465 -> 688
    //   1156: astore 11
    //   1158: aload 4
    //   1160: ifnonnull -84 -> 1076
    //   1163: aload 11
    //   1165: astore 10
    //   1167: goto -99 -> 1068
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1170	0	paramString	String
    //   0	1170	1	paramInt1	int
    //   0	1170	2	paramInt2	int
    //   0	1170	3	paramInt3	int
    //   0	1170	4	paramConfig	Bitmap.Config
    //   0	1170	5	paramRef	Ref<Boolean>
    //   8	988	6	i	int
    //   17	446	7	j	int
    //   480	115	8	k	int
    //   325	312	9	bool	boolean
    //   39	517	10	localObject1	Object
    //   567	1	10	localThrowable1	Throwable
    //   571	595	10	localObject2	Object
    //   26	418	11	localObject3	Object
    //   446	1	11	localThrowable2	Throwable
    //   526	103	11	localObject4	Object
    //   641	1	11	localThrowable3	Throwable
    //   703	111	11	localObject5	Object
    //   818	1	11	localThrowable4	Throwable
    //   883	263	11	localObject6	Object
    //   1156	8	11	localThrowable5	Throwable
    //   35	472	12	localObject7	Object
    //   518	20	12	localThrowable6	Throwable
    //   543	262	12	localObject8	Object
    //   826	1	12	localThrowable7	Throwable
    //   840	295	12	localObject9	Object
    //   1141	1	12	localThrowable8	Throwable
    //   23	502	13	localObject10	Object
    //   531	1	13	localObject11	Object
    //   578	399	13	localObject12	Object
    //   1117	1	13	localObject13	Object
    //   1129	15	13	localObject14	Object
    //   96	686	14	localObject15	Object
    //   831	28	14	localThrowable9	Throwable
    //   874	251	14	localObject16	Object
    //   78	904	15	localBitmap	Bitmap
    //   81	743	16	localObject17	Object
    //   75	541	17	localObject18	Object
    //   20	787	18	localObject19	Object
    //   32	109	19	localObject20	Object
    //   29	92	20	localObject21	Object
    //   114	56	21	localExifInterface	ExifInterface
    // Exception table:
    //   from	to	target	type
    //   443	446	446	java/lang/Throwable
    //   632	635	446	java/lang/Throwable
    //   795	798	446	java/lang/Throwable
    //   106	116	518	java/lang/Throwable
    //   128	136	518	java/lang/Throwable
    //   144	161	518	java/lang/Throwable
    //   169	176	518	java/lang/Throwable
    //   184	193	518	java/lang/Throwable
    //   201	207	518	java/lang/Throwable
    //   215	227	518	java/lang/Throwable
    //   235	268	518	java/lang/Throwable
    //   281	290	518	java/lang/Throwable
    //   298	304	518	java/lang/Throwable
    //   312	318	518	java/lang/Throwable
    //   335	342	518	java/lang/Throwable
    //   350	357	518	java/lang/Throwable
    //   365	378	518	java/lang/Throwable
    //   395	403	518	java/lang/Throwable
    //   411	420	518	java/lang/Throwable
    //   475	482	518	java/lang/Throwable
    //   490	500	518	java/lang/Throwable
    //   508	515	518	java/lang/Throwable
    //   584	591	518	java/lang/Throwable
    //   657	688	518	java/lang/Throwable
    //   696	705	518	java/lang/Throwable
    //   713	722	518	java/lang/Throwable
    //   730	742	518	java/lang/Throwable
    //   750	772	518	java/lang/Throwable
    //   806	815	518	java/lang/Throwable
    //   528	531	531	finally
    //   564	567	567	java/lang/Throwable
    //   856	863	567	java/lang/Throwable
    //   870	873	567	java/lang/Throwable
    //   429	434	641	java/lang/Throwable
    //   781	786	818	java/lang/Throwable
    //   610	615	826	java/lang/Throwable
    //   550	555	831	java/lang/Throwable
    //   990	993	993	java/lang/Throwable
    //   1073	1076	993	java/lang/Throwable
    //   1087	1094	993	java/lang/Throwable
    //   1101	1104	993	java/lang/Throwable
    //   976	981	1035	java/lang/Throwable
    //   885	894	1043	java/lang/Throwable
    //   902	918	1043	java/lang/Throwable
    //   936	967	1043	java/lang/Throwable
    //   1045	1048	1048	finally
    //   885	894	1104	finally
    //   902	918	1104	finally
    //   936	967	1104	finally
    //   89	98	1117	finally
    //   106	116	1129	finally
    //   128	136	1129	finally
    //   144	161	1129	finally
    //   169	176	1129	finally
    //   184	193	1129	finally
    //   201	207	1129	finally
    //   215	227	1129	finally
    //   235	268	1129	finally
    //   281	290	1129	finally
    //   298	304	1129	finally
    //   312	318	1129	finally
    //   335	342	1129	finally
    //   350	357	1129	finally
    //   365	378	1129	finally
    //   395	403	1129	finally
    //   411	420	1129	finally
    //   475	482	1129	finally
    //   490	500	1129	finally
    //   508	515	1129	finally
    //   584	591	1129	finally
    //   657	688	1129	finally
    //   696	705	1129	finally
    //   713	722	1129	finally
    //   730	742	1129	finally
    //   750	772	1129	finally
    //   806	815	1129	finally
    //   89	98	1141	java/lang/Throwable
    //   1059	1064	1156	java/lang/Throwable
  }
  
  public static Bitmap decodeBitmap(String paramString, int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return decodeBitmap(paramString, paramInt1, paramInt2, 0, paramConfig);
  }
  
  public static Bitmap decodeBitmap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return decodeBitmap(paramArrayOfByte, paramInt1, paramInt2, Bitmap.Config.RGB_565);
  }
  
  /* Error */
  public static Bitmap decodeBitmap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, Bitmap.Config paramConfig)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aconst_null
    //   4: astore 5
    //   6: new 770	java/io/ByteArrayInputStream
    //   9: dup
    //   10: aload_0
    //   11: invokespecial 773	java/io/ByteArrayInputStream:<init>	([B)V
    //   14: astore_0
    //   15: aload_0
    //   16: iload_1
    //   17: iload_2
    //   18: iload_3
    //   19: aload 4
    //   21: invokestatic 606	com/oneplus/media/ImageUtils:decodeBitmap	(Ljava/io/InputStream;IIILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   24: astore 4
    //   26: aload_0
    //   27: ifnull +7 -> 34
    //   30: aload_0
    //   31: invokevirtual 774	java/io/ByteArrayInputStream:close	()V
    //   34: aconst_null
    //   35: astore_0
    //   36: aload_0
    //   37: ifnull +12 -> 49
    //   40: aload_0
    //   41: athrow
    //   42: astore_0
    //   43: aconst_null
    //   44: areturn
    //   45: astore_0
    //   46: goto -10 -> 36
    //   49: aload 4
    //   51: areturn
    //   52: astore_0
    //   53: aload_0
    //   54: athrow
    //   55: astore 4
    //   57: aload_0
    //   58: astore 6
    //   60: aload 5
    //   62: ifnull +11 -> 73
    //   65: aload 5
    //   67: invokevirtual 774	java/io/ByteArrayInputStream:close	()V
    //   70: aload_0
    //   71: astore 6
    //   73: aload 6
    //   75: ifnull +27 -> 102
    //   78: aload 6
    //   80: athrow
    //   81: aload_0
    //   82: astore 6
    //   84: aload_0
    //   85: aload 5
    //   87: if_acmpeq -14 -> 73
    //   90: aload_0
    //   91: aload 5
    //   93: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   96: aload_0
    //   97: astore 6
    //   99: goto -26 -> 73
    //   102: aload 4
    //   104: athrow
    //   105: astore 4
    //   107: aconst_null
    //   108: astore_0
    //   109: aload 6
    //   111: astore 5
    //   113: goto -56 -> 57
    //   116: astore 4
    //   118: aconst_null
    //   119: astore 6
    //   121: aload_0
    //   122: astore 5
    //   124: aload 6
    //   126: astore_0
    //   127: goto -70 -> 57
    //   130: astore 4
    //   132: aload_0
    //   133: astore 5
    //   135: aload 4
    //   137: astore_0
    //   138: goto -85 -> 53
    //   141: astore_0
    //   142: aconst_null
    //   143: areturn
    //   144: astore 5
    //   146: aload_0
    //   147: ifnonnull -66 -> 81
    //   150: aload 5
    //   152: astore 6
    //   154: goto -81 -> 73
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	157	0	paramArrayOfByte	byte[]
    //   0	157	1	paramInt1	int
    //   0	157	2	paramInt2	int
    //   0	157	3	paramInt3	int
    //   0	157	4	paramConfig	Bitmap.Config
    //   4	130	5	localObject1	Object
    //   144	7	5	localThrowable	Throwable
    //   1	152	6	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   40	42	42	java/lang/Throwable
    //   30	34	45	java/lang/Throwable
    //   6	15	52	java/lang/Throwable
    //   53	55	55	finally
    //   6	15	105	finally
    //   15	26	116	finally
    //   15	26	130	java/lang/Throwable
    //   78	81	141	java/lang/Throwable
    //   90	96	141	java/lang/Throwable
    //   102	105	141	java/lang/Throwable
    //   65	70	144	java/lang/Throwable
  }
  
  public static Bitmap decodeBitmap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return decodeBitmap(paramArrayOfByte, paramInt1, paramInt2, 0, paramConfig);
  }
  
  /* Error */
  private static Bitmap decodeBitmapProgressively(InputStream paramInputStream, int paramInt1, int paramInt2, int paramInt3, Bitmap.Config paramConfig, Ref<Boolean> paramRef)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 609	com/oneplus/media/ImageUtils:isJfifHeader	(Ljava/io/InputStream;)Z
    //   4: ifne +10 -> 14
    //   7: aload_0
    //   8: invokestatic 779	com/oneplus/media/ImageUtils:isPngHeader	(Ljava/io/InputStream;)Z
    //   11: ifeq +504 -> 515
    //   14: ldc 32
    //   16: ldc_w 781
    //   19: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   22: aconst_null
    //   23: astore 30
    //   25: aconst_null
    //   26: astore 27
    //   28: aconst_null
    //   29: astore 28
    //   31: aconst_null
    //   32: astore 21
    //   34: aconst_null
    //   35: astore 26
    //   37: aconst_null
    //   38: astore 24
    //   40: aconst_null
    //   41: astore 25
    //   43: aconst_null
    //   44: astore 29
    //   46: aconst_null
    //   47: astore 22
    //   49: new 623	com/oneplus/io/StreamState
    //   52: dup
    //   53: aload_0
    //   54: invokespecial 625	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   57: astore 23
    //   59: aload 27
    //   61: astore 21
    //   63: aload 28
    //   65: astore 22
    //   67: aload_0
    //   68: invokestatic 618	com/oneplus/media/ImageUtils:decodeOrientation	(Ljava/io/InputStream;)I
    //   71: istore 8
    //   73: aload 27
    //   75: astore 21
    //   77: aload 28
    //   79: astore 22
    //   81: ldc 32
    //   83: new 193	java/lang/StringBuilder
    //   86: dup
    //   87: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   90: ldc_w 783
    //   93: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   96: iload 8
    //   98: invokevirtual 203	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   101: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   104: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   107: aload 27
    //   109: astore 21
    //   111: aload 28
    //   113: astore 22
    //   115: aload_0
    //   116: iconst_1
    //   117: invokestatic 789	android/graphics/BitmapRegionDecoder:newInstance	(Ljava/io/InputStream;Z)Landroid/graphics/BitmapRegionDecoder;
    //   120: astore_0
    //   121: aload_0
    //   122: astore 21
    //   124: aload_0
    //   125: astore 22
    //   127: aload_0
    //   128: invokevirtual 790	android/graphics/BitmapRegionDecoder:getWidth	()I
    //   131: istore 15
    //   133: aload_0
    //   134: astore 21
    //   136: aload_0
    //   137: astore 22
    //   139: aload_0
    //   140: invokevirtual 791	android/graphics/BitmapRegionDecoder:getHeight	()I
    //   143: istore 16
    //   145: aload_0
    //   146: astore 21
    //   148: aload_0
    //   149: astore 22
    //   151: new 656	android/graphics/BitmapFactory$Options
    //   154: dup
    //   155: invokespecial 657	android/graphics/BitmapFactory$Options:<init>	()V
    //   158: astore 27
    //   160: aload_0
    //   161: astore 21
    //   163: aload_0
    //   164: astore 22
    //   166: aload 27
    //   168: iconst_0
    //   169: putfield 661	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   172: iload_3
    //   173: iconst_1
    //   174: iand
    //   175: ifeq +342 -> 517
    //   178: iconst_1
    //   179: istore 20
    //   181: aload_0
    //   182: astore 21
    //   184: aload_0
    //   185: astore 22
    //   187: aload 27
    //   189: iload 20
    //   191: putfield 685	android/graphics/BitmapFactory$Options:inPreferQualityOverSpeed	Z
    //   194: aload_0
    //   195: astore 21
    //   197: aload_0
    //   198: astore 22
    //   200: aload 27
    //   202: aload 4
    //   204: putfield 688	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   207: aload_0
    //   208: astore 21
    //   210: aload_0
    //   211: astore 22
    //   213: aload 27
    //   215: iconst_1
    //   216: putfield 691	android/graphics/BitmapFactory$Options:inDither	Z
    //   219: iload_3
    //   220: bipush 8
    //   222: iand
    //   223: ifeq +744 -> 967
    //   226: aload_0
    //   227: astore 21
    //   229: aload_0
    //   230: astore 22
    //   232: aload 27
    //   234: iconst_1
    //   235: putfield 694	android/graphics/BitmapFactory$Options:inMutable	Z
    //   238: goto +729 -> 967
    //   241: aload_0
    //   242: astore 21
    //   244: aload_0
    //   245: astore 22
    //   247: iload_2
    //   248: iload_1
    //   249: aload 4
    //   251: invokestatic 310	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   254: astore 4
    //   256: iload_2
    //   257: istore_3
    //   258: iload_1
    //   259: istore_2
    //   260: iload_3
    //   261: istore_1
    //   262: aload_0
    //   263: astore 21
    //   265: aload_0
    //   266: astore 22
    //   268: iload_1
    //   269: i2f
    //   270: iload 15
    //   272: i2f
    //   273: fdiv
    //   274: fstore 6
    //   276: aload_0
    //   277: astore 21
    //   279: aload_0
    //   280: astore 22
    //   282: iload_2
    //   283: i2f
    //   284: iload 16
    //   286: i2f
    //   287: fdiv
    //   288: fstore 7
    //   290: aload_0
    //   291: astore 21
    //   293: aload_0
    //   294: astore 22
    //   296: ldc_w 792
    //   299: fload 6
    //   301: fmul
    //   302: invokestatic 795	java/lang/Math:round	(F)I
    //   305: istore 13
    //   307: aload_0
    //   308: astore 21
    //   310: aload_0
    //   311: astore 22
    //   313: ldc_w 792
    //   316: fload 7
    //   318: fmul
    //   319: invokestatic 795	java/lang/Math:round	(F)I
    //   322: istore 14
    //   324: aload_0
    //   325: astore 21
    //   327: aload_0
    //   328: astore 22
    //   330: ldc 32
    //   332: new 193	java/lang/StringBuilder
    //   335: dup
    //   336: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   339: ldc_w 797
    //   342: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   345: iload 15
    //   347: invokevirtual 203	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   350: ldc_w 799
    //   353: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   356: iload 16
    //   358: invokevirtual 203	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   361: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   364: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   367: aload_0
    //   368: astore 21
    //   370: aload_0
    //   371: astore 22
    //   373: ldc 32
    //   375: new 193	java/lang/StringBuilder
    //   378: dup
    //   379: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   382: ldc_w 801
    //   385: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   388: iload_1
    //   389: invokevirtual 203	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   392: ldc_w 803
    //   395: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   398: iload_2
    //   399: invokevirtual 203	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   402: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   405: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   408: iconst_0
    //   409: istore_3
    //   410: goto +575 -> 985
    //   413: aload 5
    //   415: ifnull +141 -> 556
    //   418: aload_0
    //   419: astore 21
    //   421: aload_0
    //   422: astore 22
    //   424: aload 5
    //   426: invokeinterface 428 1 0
    //   431: checkcast 611	java/lang/Boolean
    //   434: invokevirtual 614	java/lang/Boolean:booleanValue	()Z
    //   437: istore 20
    //   439: iload 20
    //   441: ifeq +115 -> 556
    //   444: aload 25
    //   446: astore 4
    //   448: aload 23
    //   450: ifnull +12 -> 462
    //   453: aload 23
    //   455: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   458: aload 25
    //   460: astore 4
    //   462: aload 4
    //   464: ifnull +82 -> 546
    //   467: aload 4
    //   469: athrow
    //   470: astore 4
    //   472: aload_0
    //   473: astore 22
    //   475: ldc 32
    //   477: ldc_w 805
    //   480: aload 4
    //   482: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   485: aconst_null
    //   486: astore 5
    //   488: aload 5
    //   490: astore 4
    //   492: aload_0
    //   493: ifnull +11 -> 504
    //   496: aload_0
    //   497: invokevirtual 808	android/graphics/BitmapRegionDecoder:recycle	()V
    //   500: aload 5
    //   502: astore 4
    //   504: ldc 32
    //   506: ldc_w 810
    //   509: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   512: aload 4
    //   514: areturn
    //   515: aconst_null
    //   516: areturn
    //   517: iconst_0
    //   518: istore 20
    //   520: goto -339 -> 181
    //   523: aload_0
    //   524: astore 21
    //   526: aload_0
    //   527: astore 22
    //   529: iload_1
    //   530: iload_2
    //   531: aload 4
    //   533: invokestatic 310	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   536: astore 4
    //   538: goto -276 -> 262
    //   541: astore 4
    //   543: goto -81 -> 462
    //   546: aload_0
    //   547: ifnull +7 -> 554
    //   550: aload_0
    //   551: invokevirtual 808	android/graphics/BitmapRegionDecoder:recycle	()V
    //   554: aconst_null
    //   555: areturn
    //   556: iload 9
    //   558: sipush 1024
    //   561: imul
    //   562: istore 12
    //   564: iload 12
    //   566: sipush 1024
    //   569: iadd
    //   570: iconst_1
    //   571: isub
    //   572: istore 11
    //   574: iload 11
    //   576: istore 10
    //   578: iload 11
    //   580: iload 16
    //   582: if_icmplt +9 -> 591
    //   585: iload 16
    //   587: iconst_1
    //   588: isub
    //   589: istore 10
    //   591: aload_0
    //   592: astore 21
    //   594: aload_0
    //   595: astore 22
    //   597: aload_0
    //   598: new 295	android/graphics/Rect
    //   601: dup
    //   602: iload 17
    //   604: iload 12
    //   606: iload 8
    //   608: iload 10
    //   610: invokespecial 298	android/graphics/Rect:<init>	(IIII)V
    //   613: aload 27
    //   615: invokevirtual 814	android/graphics/BitmapRegionDecoder:decodeRegion	(Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   618: astore 28
    //   620: aload_0
    //   621: astore 21
    //   623: aload_0
    //   624: astore 22
    //   626: new 312	android/graphics/Canvas
    //   629: dup
    //   630: aload 4
    //   632: invokespecial 314	android/graphics/Canvas:<init>	(Landroid/graphics/Bitmap;)V
    //   635: astore 29
    //   637: aload_0
    //   638: astore 21
    //   640: aload_0
    //   641: astore 22
    //   643: new 295	android/graphics/Rect
    //   646: dup
    //   647: iconst_0
    //   648: iconst_0
    //   649: aload 28
    //   651: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   654: iconst_1
    //   655: isub
    //   656: aload 28
    //   658: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   661: iconst_1
    //   662: isub
    //   663: invokespecial 298	android/graphics/Rect:<init>	(IIII)V
    //   666: astore 30
    //   668: iload_3
    //   669: iload 13
    //   671: imul
    //   672: istore 18
    //   674: iload 9
    //   676: iload 14
    //   678: imul
    //   679: istore 19
    //   681: iload 13
    //   683: istore 12
    //   685: iload 14
    //   687: istore 11
    //   689: iload 8
    //   691: iload 15
    //   693: iconst_1
    //   694: isub
    //   695: if_icmpne +330 -> 1025
    //   698: iload_1
    //   699: iload_3
    //   700: iload 13
    //   702: imul
    //   703: isub
    //   704: istore 12
    //   706: goto +319 -> 1025
    //   709: aload_0
    //   710: astore 21
    //   712: aload_0
    //   713: astore 22
    //   715: aload 29
    //   717: aload 28
    //   719: aload 30
    //   721: new 295	android/graphics/Rect
    //   724: dup
    //   725: iload 18
    //   727: iload 19
    //   729: iload 18
    //   731: iload 12
    //   733: iadd
    //   734: iload 19
    //   736: iload 11
    //   738: iadd
    //   739: invokespecial 298	android/graphics/Rect:<init>	(IIII)V
    //   742: aconst_null
    //   743: invokevirtual 318	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;Landroid/graphics/Rect;Landroid/graphics/Rect;Landroid/graphics/Paint;)V
    //   746: iload 10
    //   748: iload 16
    //   750: iconst_1
    //   751: isub
    //   752: if_icmpne +51 -> 803
    //   755: iload 8
    //   757: iload 15
    //   759: iconst_1
    //   760: isub
    //   761: if_icmpne +51 -> 812
    //   764: aload 26
    //   766: astore 5
    //   768: aload 23
    //   770: ifnull +12 -> 782
    //   773: aload 23
    //   775: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   778: aload 26
    //   780: astore 5
    //   782: aload 5
    //   784: ifnull +128 -> 912
    //   787: aload 5
    //   789: athrow
    //   790: astore 4
    //   792: aload_0
    //   793: ifnull +7 -> 800
    //   796: aload_0
    //   797: invokevirtual 808	android/graphics/BitmapRegionDecoder:recycle	()V
    //   800: aload 4
    //   802: athrow
    //   803: iload 9
    //   805: iconst_1
    //   806: iadd
    //   807: istore 9
    //   809: goto -396 -> 413
    //   812: iload_3
    //   813: iconst_1
    //   814: iadd
    //   815: istore_3
    //   816: goto +169 -> 985
    //   819: astore 5
    //   821: goto -39 -> 782
    //   824: astore 4
    //   826: aload 22
    //   828: astore 5
    //   830: aload 21
    //   832: astore_0
    //   833: aload 4
    //   835: athrow
    //   836: astore 22
    //   838: aload 4
    //   840: astore 21
    //   842: aload 22
    //   844: astore 4
    //   846: aload 21
    //   848: astore 23
    //   850: aload 5
    //   852: ifnull +15 -> 867
    //   855: aload_0
    //   856: astore 22
    //   858: aload 5
    //   860: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   863: aload 21
    //   865: astore 23
    //   867: aload 23
    //   869: ifnull +37 -> 906
    //   872: aload_0
    //   873: astore 22
    //   875: aload 23
    //   877: athrow
    //   878: aload 21
    //   880: astore 23
    //   882: aload 21
    //   884: aload 5
    //   886: if_acmpeq -19 -> 867
    //   889: aload_0
    //   890: astore 22
    //   892: aload 21
    //   894: aload 5
    //   896: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   899: aload 21
    //   901: astore 23
    //   903: goto -36 -> 867
    //   906: aload_0
    //   907: astore 22
    //   909: aload 4
    //   911: athrow
    //   912: aload_0
    //   913: ifnull +7 -> 920
    //   916: aload_0
    //   917: invokevirtual 808	android/graphics/BitmapRegionDecoder:recycle	()V
    //   920: goto -416 -> 504
    //   923: astore 4
    //   925: aload 30
    //   927: astore_0
    //   928: aload 29
    //   930: astore 5
    //   932: aload 24
    //   934: astore 21
    //   936: goto -90 -> 846
    //   939: astore 4
    //   941: aload 23
    //   943: astore 5
    //   945: aload 21
    //   947: astore_0
    //   948: aload 24
    //   950: astore 21
    //   952: goto -106 -> 846
    //   955: astore 4
    //   957: aload 23
    //   959: astore 5
    //   961: aload 22
    //   963: astore_0
    //   964: goto -131 -> 833
    //   967: iload 8
    //   969: bipush 90
    //   971: if_icmpeq -730 -> 241
    //   974: iload 8
    //   976: sipush 270
    //   979: if_icmpne -456 -> 523
    //   982: goto -741 -> 241
    //   985: iload_3
    //   986: sipush 1024
    //   989: imul
    //   990: istore 17
    //   992: iload 17
    //   994: sipush 1024
    //   997: iadd
    //   998: iconst_1
    //   999: isub
    //   1000: istore 9
    //   1002: iload 9
    //   1004: istore 8
    //   1006: iload 9
    //   1008: iload 15
    //   1010: if_icmplt +9 -> 1019
    //   1013: iload 15
    //   1015: iconst_1
    //   1016: isub
    //   1017: istore 8
    //   1019: iconst_0
    //   1020: istore 9
    //   1022: goto -609 -> 413
    //   1025: iload 10
    //   1027: iload 16
    //   1029: iconst_1
    //   1030: isub
    //   1031: if_icmpne -322 -> 709
    //   1034: iload_2
    //   1035: iload 9
    //   1037: iload 14
    //   1039: imul
    //   1040: isub
    //   1041: istore 11
    //   1043: goto -334 -> 709
    //   1046: astore 4
    //   1048: goto -576 -> 472
    //   1051: astore 5
    //   1053: aload 21
    //   1055: ifnonnull -177 -> 878
    //   1058: aload 5
    //   1060: astore 23
    //   1062: goto -195 -> 867
    //   1065: astore 4
    //   1067: aload 22
    //   1069: astore_0
    //   1070: goto -278 -> 792
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1073	0	paramInputStream	InputStream
    //   0	1073	1	paramInt1	int
    //   0	1073	2	paramInt2	int
    //   0	1073	3	paramInt3	int
    //   0	1073	4	paramConfig	Bitmap.Config
    //   0	1073	5	paramRef	Ref<Boolean>
    //   274	26	6	f1	float
    //   288	29	7	f2	float
    //   71	947	8	i	int
    //   556	484	9	j	int
    //   576	456	10	k	int
    //   572	470	11	m	int
    //   562	172	12	n	int
    //   305	398	13	i1	int
    //   322	718	14	i2	int
    //   131	886	15	i3	int
    //   143	888	16	i4	int
    //   602	396	17	i5	int
    //   672	62	18	i6	int
    //   679	60	19	i7	int
    //   179	340	20	bool	boolean
    //   32	1022	21	localObject1	Object
    //   47	780	22	localObject2	Object
    //   836	7	22	localObject3	Object
    //   856	212	22	localInputStream	InputStream
    //   57	1004	23	localObject4	Object
    //   38	911	24	localObject5	Object
    //   41	418	25	localObject6	Object
    //   35	744	26	localObject7	Object
    //   26	588	27	localOptions	android.graphics.BitmapFactory.Options
    //   29	689	28	localBitmap	Bitmap
    //   44	885	29	localCanvas	Canvas
    //   23	903	30	localRect	Rect
    // Exception table:
    //   from	to	target	type
    //   467	470	470	java/lang/Throwable
    //   787	790	470	java/lang/Throwable
    //   453	458	541	java/lang/Throwable
    //   453	458	790	finally
    //   467	470	790	finally
    //   773	778	790	finally
    //   787	790	790	finally
    //   773	778	819	java/lang/Throwable
    //   49	59	824	java/lang/Throwable
    //   833	836	836	finally
    //   49	59	923	finally
    //   67	73	939	finally
    //   81	107	939	finally
    //   115	121	939	finally
    //   127	133	939	finally
    //   139	145	939	finally
    //   151	160	939	finally
    //   166	172	939	finally
    //   187	194	939	finally
    //   200	207	939	finally
    //   213	219	939	finally
    //   232	238	939	finally
    //   247	256	939	finally
    //   268	276	939	finally
    //   282	290	939	finally
    //   296	307	939	finally
    //   313	324	939	finally
    //   330	367	939	finally
    //   373	408	939	finally
    //   424	439	939	finally
    //   529	538	939	finally
    //   597	620	939	finally
    //   626	637	939	finally
    //   643	668	939	finally
    //   715	746	939	finally
    //   67	73	955	java/lang/Throwable
    //   81	107	955	java/lang/Throwable
    //   115	121	955	java/lang/Throwable
    //   127	133	955	java/lang/Throwable
    //   139	145	955	java/lang/Throwable
    //   151	160	955	java/lang/Throwable
    //   166	172	955	java/lang/Throwable
    //   187	194	955	java/lang/Throwable
    //   200	207	955	java/lang/Throwable
    //   213	219	955	java/lang/Throwable
    //   232	238	955	java/lang/Throwable
    //   247	256	955	java/lang/Throwable
    //   268	276	955	java/lang/Throwable
    //   282	290	955	java/lang/Throwable
    //   296	307	955	java/lang/Throwable
    //   313	324	955	java/lang/Throwable
    //   330	367	955	java/lang/Throwable
    //   373	408	955	java/lang/Throwable
    //   424	439	955	java/lang/Throwable
    //   529	538	955	java/lang/Throwable
    //   597	620	955	java/lang/Throwable
    //   626	637	955	java/lang/Throwable
    //   643	668	955	java/lang/Throwable
    //   715	746	955	java/lang/Throwable
    //   875	878	1046	java/lang/Throwable
    //   892	899	1046	java/lang/Throwable
    //   909	912	1046	java/lang/Throwable
    //   858	863	1051	java/lang/Throwable
    //   475	485	1065	finally
    //   858	863	1065	finally
    //   875	878	1065	finally
    //   892	899	1065	finally
    //   909	912	1065	finally
  }
  
  public static Bitmap decodeCenterCropBitmap(InputStream paramInputStream, int paramInt1, int paramInt2, int paramInt3, Bitmap.Config paramConfig)
  {
    return decodeCenterCropBitmap(paramInputStream, null, paramInt1, paramInt2, paramInt3, paramConfig);
  }
  
  public static Bitmap decodeCenterCropBitmap(InputStream paramInputStream, int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return decodeCenterCropBitmap(paramInputStream, paramInt1, paramInt2, 0, paramConfig);
  }
  
  /* Error */
  private static Bitmap decodeCenterCropBitmap(InputStream paramInputStream, Integer paramInteger, int paramInt1, int paramInt2, int paramInt3, Bitmap.Config paramConfig)
  {
    // Byte code:
    //   0: iload 4
    //   2: bipush 16
    //   4: iand
    //   5: ifeq +154 -> 159
    //   8: iconst_1
    //   9: istore 7
    //   11: iload 4
    //   13: bipush 64
    //   15: iand
    //   16: ifne +149 -> 165
    //   19: iconst_1
    //   20: istore 8
    //   22: aload_0
    //   23: invokestatic 326	com/oneplus/media/ImageUtils:isGifHeader	(Ljava/io/InputStream;)Z
    //   26: istore 15
    //   28: aload_0
    //   29: invokestatic 609	com/oneplus/media/ImageUtils:isJfifHeader	(Ljava/io/InputStream;)Z
    //   32: istore 16
    //   34: aload_1
    //   35: astore 17
    //   37: aload_1
    //   38: ifnonnull +12 -> 50
    //   41: aload_0
    //   42: invokestatic 618	com/oneplus/media/ImageUtils:decodeOrientation	(Ljava/io/InputStream;)I
    //   45: invokestatic 234	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   48: astore 17
    //   50: aload 17
    //   52: invokevirtual 621	java/lang/Integer:intValue	()I
    //   55: bipush 90
    //   57: if_icmpeq +778 -> 835
    //   60: aload 17
    //   62: invokevirtual 621	java/lang/Integer:intValue	()I
    //   65: sipush 270
    //   68: if_icmpne +103 -> 171
    //   71: goto +764 -> 835
    //   74: new 656	android/graphics/BitmapFactory$Options
    //   77: dup
    //   78: invokespecial 657	android/graphics/BitmapFactory$Options:<init>	()V
    //   81: astore 22
    //   83: aload 22
    //   85: iconst_1
    //   86: putfield 661	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   89: aconst_null
    //   90: astore 18
    //   92: aconst_null
    //   93: astore 20
    //   95: aconst_null
    //   96: astore 19
    //   98: aconst_null
    //   99: astore 21
    //   101: new 623	com/oneplus/io/StreamState
    //   104: dup
    //   105: aload_0
    //   106: invokespecial 625	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   109: astore_1
    //   110: aload_0
    //   111: aconst_null
    //   112: aload 22
    //   114: invokestatic 666	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   117: pop
    //   118: aload 20
    //   120: astore 18
    //   122: aload_1
    //   123: ifnull +11 -> 134
    //   126: aload_1
    //   127: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   130: aload 20
    //   132: astore 18
    //   134: aload 18
    //   136: ifnull +107 -> 243
    //   139: aload 18
    //   141: athrow
    //   142: astore_0
    //   143: iload 8
    //   145: ifeq +12 -> 157
    //   148: ldc 32
    //   150: ldc_w 822
    //   153: aload_0
    //   154: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   157: aconst_null
    //   158: areturn
    //   159: iconst_0
    //   160: istore 7
    //   162: goto -151 -> 11
    //   165: iconst_0
    //   166: istore 8
    //   168: goto -146 -> 22
    //   171: iconst_0
    //   172: istore 11
    //   174: goto -100 -> 74
    //   177: astore 18
    //   179: goto -45 -> 134
    //   182: astore_1
    //   183: aload 21
    //   185: astore_0
    //   186: aload_1
    //   187: athrow
    //   188: astore 17
    //   190: aload_0
    //   191: astore 5
    //   193: aload 17
    //   195: astore_0
    //   196: aload_1
    //   197: astore 17
    //   199: aload 5
    //   201: ifnull +11 -> 212
    //   204: aload 5
    //   206: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   209: aload_1
    //   210: astore 17
    //   212: aload 17
    //   214: ifnull +27 -> 241
    //   217: aload 17
    //   219: athrow
    //   220: aload_1
    //   221: astore 17
    //   223: aload_1
    //   224: aload 5
    //   226: if_acmpeq -14 -> 212
    //   229: aload_1
    //   230: aload 5
    //   232: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   235: aload_1
    //   236: astore 17
    //   238: goto -26 -> 212
    //   241: aload_0
    //   242: athrow
    //   243: aload 22
    //   245: getfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   248: istore 12
    //   250: aload 22
    //   252: getfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   255: istore 13
    //   257: iload 12
    //   259: ifle +8 -> 267
    //   262: iload 13
    //   264: ifgt +596 -> 860
    //   267: iload 8
    //   269: ifeq +589 -> 858
    //   272: ldc 32
    //   274: ldc_w 824
    //   277: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   280: goto +578 -> 858
    //   283: iload 12
    //   285: iload 9
    //   287: invokestatic 290	java/lang/Math:min	(II)I
    //   290: istore_3
    //   291: iload 13
    //   293: iload 10
    //   295: invokestatic 290	java/lang/Math:min	(II)I
    //   298: istore 9
    //   300: fconst_1
    //   301: iload_3
    //   302: i2f
    //   303: iload 12
    //   305: i2f
    //   306: fdiv
    //   307: iload 9
    //   309: i2f
    //   310: iload 13
    //   312: i2f
    //   313: fdiv
    //   314: invokestatic 827	java/lang/Math:max	(FF)F
    //   317: invokestatic 293	java/lang/Math:min	(FF)F
    //   320: fstore 6
    //   322: iload 12
    //   324: i2f
    //   325: fload 6
    //   327: fmul
    //   328: f2i
    //   329: istore 10
    //   331: iload 13
    //   333: i2f
    //   334: fload 6
    //   336: fmul
    //   337: f2i
    //   338: istore 11
    //   340: new 295	android/graphics/Rect
    //   343: dup
    //   344: invokespecial 828	android/graphics/Rect:<init>	()V
    //   347: astore_1
    //   348: aload 22
    //   350: iconst_0
    //   351: putfield 661	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   354: iload 4
    //   356: iconst_1
    //   357: iand
    //   358: ifeq +525 -> 883
    //   361: iconst_1
    //   362: istore 14
    //   364: aload 22
    //   366: iload 14
    //   368: putfield 685	android/graphics/BitmapFactory$Options:inPreferQualityOverSpeed	Z
    //   371: aload 22
    //   373: aload 5
    //   375: putfield 688	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   378: iload 4
    //   380: bipush 8
    //   382: iand
    //   383: ifeq +9 -> 392
    //   386: aload 22
    //   388: iconst_1
    //   389: putfield 694	android/graphics/BitmapFactory$Options:inMutable	Z
    //   392: iconst_0
    //   393: istore_2
    //   394: iload 15
    //   396: ifeq +209 -> 605
    //   399: aload_0
    //   400: invokestatic 631	android/graphics/Movie:decodeStream	(Ljava/io/InputStream;)Landroid/graphics/Movie;
    //   403: astore 18
    //   405: aload 18
    //   407: iconst_0
    //   408: invokevirtual 635	android/graphics/Movie:setTime	(I)Z
    //   411: pop
    //   412: iload 12
    //   414: iload 13
    //   416: aload 5
    //   418: invokestatic 310	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   421: astore_0
    //   422: aload 18
    //   424: new 312	android/graphics/Canvas
    //   427: dup
    //   428: aload_0
    //   429: invokespecial 314	android/graphics/Canvas:<init>	(Landroid/graphics/Bitmap;)V
    //   432: fconst_0
    //   433: fconst_0
    //   434: invokevirtual 654	android/graphics/Movie:draw	(Landroid/graphics/Canvas;FF)V
    //   437: iload_2
    //   438: ifne +71 -> 509
    //   441: aload_0
    //   442: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   445: i2f
    //   446: iload_3
    //   447: i2f
    //   448: fdiv
    //   449: aload_0
    //   450: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   453: i2f
    //   454: iload 9
    //   456: i2f
    //   457: fdiv
    //   458: invokestatic 293	java/lang/Math:min	(FF)F
    //   461: fstore 6
    //   463: iload_3
    //   464: i2f
    //   465: fload 6
    //   467: fmul
    //   468: f2i
    //   469: istore_2
    //   470: iload 9
    //   472: i2f
    //   473: fload 6
    //   475: fmul
    //   476: f2i
    //   477: istore 4
    //   479: aload_1
    //   480: iconst_0
    //   481: iconst_0
    //   482: iload_2
    //   483: iload 4
    //   485: invokevirtual 831	android/graphics/Rect:set	(IIII)V
    //   488: aload_1
    //   489: aload_0
    //   490: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   493: iload_2
    //   494: isub
    //   495: iconst_2
    //   496: idiv
    //   497: aload_0
    //   498: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   501: iload 4
    //   503: isub
    //   504: iconst_2
    //   505: idiv
    //   506: invokevirtual 302	android/graphics/Rect:offsetTo	(II)V
    //   509: iload_3
    //   510: iload 9
    //   512: aload 5
    //   514: invokestatic 310	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   517: astore 18
    //   519: iload 7
    //   521: ifeq +8 -> 529
    //   524: iload 16
    //   526: ifeq +259 -> 785
    //   529: new 312	android/graphics/Canvas
    //   532: dup
    //   533: aload 18
    //   535: invokespecial 314	android/graphics/Canvas:<init>	(Landroid/graphics/Bitmap;)V
    //   538: aload_0
    //   539: aload_1
    //   540: new 295	android/graphics/Rect
    //   543: dup
    //   544: iconst_0
    //   545: iconst_0
    //   546: iload_3
    //   547: iload 9
    //   549: invokespecial 298	android/graphics/Rect:<init>	(IIII)V
    //   552: getstatic 167	com/oneplus/media/ImageUtils:m_BitmapFilterPaint	Landroid/graphics/Paint;
    //   555: invokevirtual 318	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;Landroid/graphics/Rect;Landroid/graphics/Rect;Landroid/graphics/Paint;)V
    //   558: aload 17
    //   560: invokevirtual 621	java/lang/Integer:intValue	()I
    //   563: ifeq +317 -> 880
    //   566: new 700	android/graphics/Matrix
    //   569: dup
    //   570: invokespecial 701	android/graphics/Matrix:<init>	()V
    //   573: astore_0
    //   574: aload_0
    //   575: aload 17
    //   577: invokevirtual 621	java/lang/Integer:intValue	()I
    //   580: i2f
    //   581: invokevirtual 709	android/graphics/Matrix:postRotate	(F)Z
    //   584: pop
    //   585: aload 18
    //   587: iconst_0
    //   588: iconst_0
    //   589: aload 18
    //   591: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   594: aload 18
    //   596: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   599: aload_0
    //   600: iconst_0
    //   601: invokestatic 712	android/graphics/Bitmap:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   604: areturn
    //   605: iload_3
    //   606: iload 9
    //   608: imul
    //   609: iload 10
    //   611: iload 11
    //   613: imul
    //   614: iconst_2
    //   615: idiv
    //   616: if_icmpgt +142 -> 758
    //   619: iload 12
    //   621: i2f
    //   622: iload_3
    //   623: i2f
    //   624: fdiv
    //   625: iload 13
    //   627: i2f
    //   628: iload 9
    //   630: i2f
    //   631: fdiv
    //   632: invokestatic 293	java/lang/Math:min	(FF)F
    //   635: fstore 6
    //   637: iload_3
    //   638: i2f
    //   639: fload 6
    //   641: fmul
    //   642: f2i
    //   643: istore_2
    //   644: iload 9
    //   646: i2f
    //   647: fload 6
    //   649: fmul
    //   650: f2i
    //   651: istore 4
    //   653: aload_1
    //   654: iconst_0
    //   655: iconst_0
    //   656: iload_2
    //   657: iload 4
    //   659: invokevirtual 831	android/graphics/Rect:set	(IIII)V
    //   662: aload_1
    //   663: iload 12
    //   665: iload_2
    //   666: isub
    //   667: iconst_2
    //   668: idiv
    //   669: iload 13
    //   671: iload 4
    //   673: isub
    //   674: iconst_2
    //   675: idiv
    //   676: invokevirtual 302	android/graphics/Rect:offsetTo	(II)V
    //   679: aload_0
    //   680: iconst_1
    //   681: invokestatic 789	android/graphics/BitmapRegionDecoder:newInstance	(Ljava/io/InputStream;Z)Landroid/graphics/BitmapRegionDecoder;
    //   684: astore 18
    //   686: aload 18
    //   688: aload_1
    //   689: aload 22
    //   691: invokevirtual 814	android/graphics/BitmapRegionDecoder:decodeRegion	(Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   694: astore_0
    //   695: aload 18
    //   697: invokevirtual 808	android/graphics/BitmapRegionDecoder:recycle	()V
    //   700: aload_1
    //   701: iconst_0
    //   702: iconst_0
    //   703: invokevirtual 302	android/graphics/Rect:offsetTo	(II)V
    //   706: iconst_1
    //   707: istore_2
    //   708: goto -271 -> 437
    //   711: astore_0
    //   712: iload 8
    //   714: ifeq +12 -> 726
    //   717: ldc 32
    //   719: ldc_w 833
    //   722: aload_0
    //   723: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   726: aload 18
    //   728: invokevirtual 808	android/graphics/BitmapRegionDecoder:recycle	()V
    //   731: aconst_null
    //   732: areturn
    //   733: astore_0
    //   734: aload 18
    //   736: invokevirtual 808	android/graphics/BitmapRegionDecoder:recycle	()V
    //   739: aload_0
    //   740: athrow
    //   741: astore_0
    //   742: iload 8
    //   744: ifeq +12 -> 756
    //   747: ldc 32
    //   749: ldc_w 835
    //   752: aload_0
    //   753: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   756: aconst_null
    //   757: areturn
    //   758: aload 22
    //   760: iload 12
    //   762: iload 13
    //   764: iload 10
    //   766: iload 11
    //   768: invokestatic 676	com/oneplus/media/ImageUtils:calculateSampleSize	(IIII)I
    //   771: putfield 679	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   774: aload_0
    //   775: aconst_null
    //   776: aload 22
    //   778: invokestatic 666	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   781: astore_0
    //   782: goto -345 -> 437
    //   785: aload 5
    //   787: getstatic 67	android/graphics/Bitmap$Config:RGB_565	Landroid/graphics/Bitmap$Config;
    //   790: if_acmpeq -261 -> 529
    //   793: aload 18
    //   795: ldc_w 558
    //   798: invokevirtual 565	android/graphics/Bitmap:eraseColor	(I)V
    //   801: goto -272 -> 529
    //   804: astore_0
    //   805: aload 18
    //   807: astore_1
    //   808: aload 19
    //   810: astore 5
    //   812: goto -616 -> 196
    //   815: astore_0
    //   816: aload_1
    //   817: astore 5
    //   819: aload 18
    //   821: astore_1
    //   822: goto -626 -> 196
    //   825: astore 5
    //   827: aload_1
    //   828: astore_0
    //   829: aload 5
    //   831: astore_1
    //   832: goto -646 -> 186
    //   835: iconst_1
    //   836: istore 11
    //   838: goto -764 -> 74
    //   841: astore_0
    //   842: goto -699 -> 143
    //   845: astore 5
    //   847: aload_1
    //   848: ifnonnull -628 -> 220
    //   851: aload 5
    //   853: astore 17
    //   855: goto -643 -> 212
    //   858: aconst_null
    //   859: areturn
    //   860: iload_2
    //   861: istore 9
    //   863: iload_3
    //   864: istore 10
    //   866: iload 11
    //   868: ifeq -585 -> 283
    //   871: iload_3
    //   872: istore 9
    //   874: iload_2
    //   875: istore 10
    //   877: goto -594 -> 283
    //   880: aload 18
    //   882: areturn
    //   883: iconst_0
    //   884: istore 14
    //   886: goto -522 -> 364
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	889	0	paramInputStream	InputStream
    //   0	889	1	paramInteger	Integer
    //   0	889	2	paramInt1	int
    //   0	889	3	paramInt2	int
    //   0	889	4	paramInt3	int
    //   0	889	5	paramConfig	Bitmap.Config
    //   320	328	6	f	float
    //   9	511	7	i	int
    //   20	723	8	j	int
    //   285	588	9	k	int
    //   293	583	10	m	int
    //   172	695	11	n	int
    //   248	513	12	i1	int
    //   255	508	13	i2	int
    //   362	523	14	bool1	boolean
    //   26	369	15	bool2	boolean
    //   32	493	16	bool3	boolean
    //   35	26	17	localInteger	Integer
    //   188	6	17	localObject1	Object
    //   197	657	17	localObject2	Object
    //   90	50	18	localObject3	Object
    //   177	1	18	localThrowable	Throwable
    //   403	478	18	localObject4	Object
    //   96	713	19	localObject5	Object
    //   93	38	20	localObject6	Object
    //   99	85	21	localObject7	Object
    //   81	696	22	localOptions	android.graphics.BitmapFactory.Options
    // Exception table:
    //   from	to	target	type
    //   139	142	142	java/lang/Throwable
    //   126	130	177	java/lang/Throwable
    //   101	110	182	java/lang/Throwable
    //   186	188	188	finally
    //   686	695	711	java/lang/Throwable
    //   686	695	733	finally
    //   717	726	733	finally
    //   22	34	741	java/lang/Throwable
    //   41	50	741	java/lang/Throwable
    //   50	71	741	java/lang/Throwable
    //   74	89	741	java/lang/Throwable
    //   148	157	741	java/lang/Throwable
    //   243	257	741	java/lang/Throwable
    //   272	280	741	java/lang/Throwable
    //   283	322	741	java/lang/Throwable
    //   340	354	741	java/lang/Throwable
    //   364	378	741	java/lang/Throwable
    //   386	392	741	java/lang/Throwable
    //   399	437	741	java/lang/Throwable
    //   441	463	741	java/lang/Throwable
    //   479	509	741	java/lang/Throwable
    //   509	519	741	java/lang/Throwable
    //   529	605	741	java/lang/Throwable
    //   605	637	741	java/lang/Throwable
    //   653	686	741	java/lang/Throwable
    //   695	706	741	java/lang/Throwable
    //   726	731	741	java/lang/Throwable
    //   734	741	741	java/lang/Throwable
    //   758	782	741	java/lang/Throwable
    //   785	801	741	java/lang/Throwable
    //   101	110	804	finally
    //   110	118	815	finally
    //   110	118	825	java/lang/Throwable
    //   217	220	841	java/lang/Throwable
    //   229	235	841	java/lang/Throwable
    //   241	243	841	java/lang/Throwable
    //   204	209	845	java/lang/Throwable
  }
  
  public static Bitmap decodeCenterCropBitmap(String paramString, int paramInt1, int paramInt2)
  {
    return decodeCenterCropBitmap(paramString, paramInt1, paramInt2, Bitmap.Config.RGB_565);
  }
  
  /* Error */
  public static Bitmap decodeCenterCropBitmap(String paramString, int paramInt1, int paramInt2, int paramInt3, Bitmap.Config paramConfig)
  {
    // Byte code:
    //   0: iload_3
    //   1: bipush 64
    //   3: iand
    //   4: ifne +521 -> 525
    //   7: iconst_1
    //   8: istore 5
    //   10: iload_3
    //   11: iconst_4
    //   12: iand
    //   13: ifeq +518 -> 531
    //   16: iconst_1
    //   17: istore 6
    //   19: iload_3
    //   20: iconst_1
    //   21: iand
    //   22: ifeq +515 -> 537
    //   25: iconst_1
    //   26: istore 7
    //   28: aconst_null
    //   29: astore 20
    //   31: aconst_null
    //   32: astore 13
    //   34: aconst_null
    //   35: astore 11
    //   37: aconst_null
    //   38: astore 18
    //   40: aconst_null
    //   41: astore 19
    //   43: aconst_null
    //   44: astore 12
    //   46: aload 12
    //   48: astore 10
    //   50: iload_3
    //   51: iconst_2
    //   52: iand
    //   53: ifne +465 -> 518
    //   56: iload 6
    //   58: ifne +25 -> 83
    //   61: aload 12
    //   63: astore 10
    //   65: iload_1
    //   66: sipush 256
    //   69: if_icmpgt +449 -> 518
    //   72: aload 12
    //   74: astore 10
    //   76: iload_2
    //   77: sipush 256
    //   80: if_icmpgt +438 -> 518
    //   83: aconst_null
    //   84: astore 17
    //   86: aconst_null
    //   87: astore 15
    //   89: aconst_null
    //   90: astore 16
    //   92: aconst_null
    //   93: astore 12
    //   95: aconst_null
    //   96: astore 10
    //   98: aload_0
    //   99: ldc2_w 35
    //   102: invokestatic 733	com/oneplus/io/FileUtils:openLockedInputStream	(Ljava/lang/String;J)Ljava/io/InputStream;
    //   105: astore 14
    //   107: aload 18
    //   109: astore 12
    //   111: aload 19
    //   113: astore 13
    //   115: new 357	android/media/ExifInterface
    //   118: dup
    //   119: aload_0
    //   120: invokespecial 374	android/media/ExifInterface:<init>	(Ljava/lang/String;)V
    //   123: astore 21
    //   125: aload 20
    //   127: astore 11
    //   129: aload 18
    //   131: astore 12
    //   133: aload 19
    //   135: astore 13
    //   137: aload 21
    //   139: invokevirtual 736	android/media/ExifInterface:hasThumbnail	()Z
    //   142: ifeq +470 -> 612
    //   145: aload 18
    //   147: astore 12
    //   149: aload 19
    //   151: astore 13
    //   153: aload 21
    //   155: ldc_w 738
    //   158: iconst_0
    //   159: invokevirtual 742	android/media/ExifInterface:getAttributeInt	(Ljava/lang/String;I)I
    //   162: invokestatic 745	com/oneplus/media/ImageUtils:exifOrientationToDegrees	(I)I
    //   165: invokestatic 234	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   168: astore 10
    //   170: aload 10
    //   172: astore 12
    //   174: aload 10
    //   176: astore 13
    //   178: aload 21
    //   180: invokevirtual 749	android/media/ExifInterface:getThumbnail	()[B
    //   183: astore 18
    //   185: aload 10
    //   187: astore 12
    //   189: aload 10
    //   191: astore 13
    //   193: new 656	android/graphics/BitmapFactory$Options
    //   196: dup
    //   197: invokespecial 657	android/graphics/BitmapFactory$Options:<init>	()V
    //   200: astore 19
    //   202: aload 10
    //   204: astore 12
    //   206: aload 10
    //   208: astore 13
    //   210: aload 19
    //   212: iconst_1
    //   213: putfield 661	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   216: aload 10
    //   218: astore 12
    //   220: aload 10
    //   222: astore 13
    //   224: aload 18
    //   226: iconst_0
    //   227: aload 18
    //   229: arraylength
    //   230: aload 19
    //   232: invokestatic 753	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   235: pop
    //   236: aload 10
    //   238: astore 12
    //   240: aload 10
    //   242: astore 13
    //   244: aload 10
    //   246: invokevirtual 621	java/lang/Integer:intValue	()I
    //   249: bipush 90
    //   251: if_icmpeq +22 -> 273
    //   254: aload 10
    //   256: astore 12
    //   258: aload 10
    //   260: astore 13
    //   262: aload 10
    //   264: invokevirtual 621	java/lang/Integer:intValue	()I
    //   267: sipush 270
    //   270: if_icmpne +51 -> 321
    //   273: aload 10
    //   275: astore 12
    //   277: aload 10
    //   279: astore 13
    //   281: aload 19
    //   283: getfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   286: istore 8
    //   288: aload 10
    //   290: astore 12
    //   292: aload 10
    //   294: astore 13
    //   296: aload 19
    //   298: aload 19
    //   300: getfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   303: putfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   306: aload 10
    //   308: astore 12
    //   310: aload 10
    //   312: astore 13
    //   314: aload 19
    //   316: iload 8
    //   318: putfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   321: iload 6
    //   323: ifne +37 -> 360
    //   326: aload 10
    //   328: astore 12
    //   330: aload 10
    //   332: astore 13
    //   334: aload 19
    //   336: getfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   339: iload_1
    //   340: if_icmplt +203 -> 543
    //   343: aload 10
    //   345: astore 12
    //   347: aload 10
    //   349: astore 13
    //   351: aload 19
    //   353: getfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   356: iload_2
    //   357: if_icmplt +186 -> 543
    //   360: aload 10
    //   362: astore 12
    //   364: aload 10
    //   366: astore 13
    //   368: aload 19
    //   370: iconst_0
    //   371: putfield 661	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   374: aload 10
    //   376: astore 12
    //   378: aload 10
    //   380: astore 13
    //   382: aload 19
    //   384: iconst_1
    //   385: putfield 691	android/graphics/BitmapFactory$Options:inDither	Z
    //   388: iload_3
    //   389: iconst_1
    //   390: iand
    //   391: ifeq +255 -> 646
    //   394: iconst_1
    //   395: istore 9
    //   397: aload 10
    //   399: astore 12
    //   401: aload 10
    //   403: astore 13
    //   405: aload 19
    //   407: iload 9
    //   409: putfield 685	android/graphics/BitmapFactory$Options:inPreferQualityOverSpeed	Z
    //   412: aload 10
    //   414: astore 12
    //   416: aload 10
    //   418: astore 13
    //   420: aload 19
    //   422: aload 4
    //   424: putfield 688	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
    //   427: aload 10
    //   429: astore 12
    //   431: aload 10
    //   433: astore 13
    //   435: aload 18
    //   437: iconst_0
    //   438: aload 18
    //   440: arraylength
    //   441: aload 19
    //   443: invokestatic 753	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   446: astore 18
    //   448: aload 10
    //   450: astore 11
    //   452: aload 18
    //   454: ifnull +158 -> 612
    //   457: aload 10
    //   459: astore 12
    //   461: aload 10
    //   463: astore 13
    //   465: aload 10
    //   467: invokevirtual 621	java/lang/Integer:intValue	()I
    //   470: ifne +190 -> 660
    //   473: aload 10
    //   475: astore 12
    //   477: aload 10
    //   479: astore 13
    //   481: aload 18
    //   483: iload_1
    //   484: iload_2
    //   485: invokestatic 839	com/oneplus/media/ImageUtils:centerCropBitmap	(Landroid/graphics/Bitmap;II)Landroid/graphics/Bitmap;
    //   488: astore 15
    //   490: aload 16
    //   492: astore 11
    //   494: aload 14
    //   496: ifnull +12 -> 508
    //   499: aload 14
    //   501: invokevirtual 758	java/io/InputStream:close	()V
    //   504: aload 16
    //   506: astore 11
    //   508: aload 11
    //   510: ifnull +147 -> 657
    //   513: aload 11
    //   515: athrow
    //   516: astore 11
    //   518: iload 6
    //   520: ifeq +396 -> 916
    //   523: aconst_null
    //   524: areturn
    //   525: iconst_0
    //   526: istore 5
    //   528: goto -518 -> 10
    //   531: iconst_0
    //   532: istore 6
    //   534: goto -515 -> 19
    //   537: iconst_0
    //   538: istore 7
    //   540: goto -512 -> 28
    //   543: aload 10
    //   545: astore 11
    //   547: iload 7
    //   549: ifne +63 -> 612
    //   552: aload 10
    //   554: astore 11
    //   556: aload 10
    //   558: astore 12
    //   560: aload 10
    //   562: astore 13
    //   564: aload 19
    //   566: getfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   569: i2f
    //   570: ldc_w 840
    //   573: fmul
    //   574: iload_1
    //   575: i2f
    //   576: fcmpl
    //   577: iflt +35 -> 612
    //   580: aload 10
    //   582: astore 12
    //   584: aload 10
    //   586: astore 13
    //   588: aload 19
    //   590: getfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   593: istore 7
    //   595: iload 7
    //   597: i2f
    //   598: ldc_w 840
    //   601: fmul
    //   602: iload_2
    //   603: i2f
    //   604: fcmpl
    //   605: ifge -245 -> 360
    //   608: aload 10
    //   610: astore 11
    //   612: aload 17
    //   614: astore 12
    //   616: aload 14
    //   618: ifnull +12 -> 630
    //   621: aload 14
    //   623: invokevirtual 758	java/io/InputStream:close	()V
    //   626: aload 17
    //   628: astore 12
    //   630: aload 11
    //   632: astore 10
    //   634: aload 12
    //   636: ifnull -118 -> 518
    //   639: aload 11
    //   641: astore 10
    //   643: aload 12
    //   645: athrow
    //   646: iconst_0
    //   647: istore 9
    //   649: goto -252 -> 397
    //   652: astore 11
    //   654: goto -146 -> 508
    //   657: aload 15
    //   659: areturn
    //   660: aload 10
    //   662: astore 12
    //   664: aload 10
    //   666: astore 13
    //   668: aload 10
    //   670: invokevirtual 621	java/lang/Integer:intValue	()I
    //   673: lookupswitch	default:+522->1195, 90:+137->810, 270:+137->810
    //   700: aload 10
    //   702: astore 12
    //   704: aload 10
    //   706: astore 13
    //   708: aload 18
    //   710: iload_1
    //   711: iload_2
    //   712: invokestatic 839	com/oneplus/media/ImageUtils:centerCropBitmap	(Landroid/graphics/Bitmap;II)Landroid/graphics/Bitmap;
    //   715: astore 11
    //   717: aload 10
    //   719: astore 12
    //   721: aload 10
    //   723: astore 13
    //   725: new 700	android/graphics/Matrix
    //   728: dup
    //   729: invokespecial 701	android/graphics/Matrix:<init>	()V
    //   732: astore 16
    //   734: aload 10
    //   736: astore 12
    //   738: aload 10
    //   740: astore 13
    //   742: aload 16
    //   744: aload 10
    //   746: invokevirtual 621	java/lang/Integer:intValue	()I
    //   749: i2f
    //   750: invokevirtual 709	android/graphics/Matrix:postRotate	(F)Z
    //   753: pop
    //   754: aload 10
    //   756: astore 12
    //   758: aload 10
    //   760: astore 13
    //   762: aload 11
    //   764: iconst_0
    //   765: iconst_0
    //   766: aload 11
    //   768: invokevirtual 186	android/graphics/Bitmap:getWidth	()I
    //   771: aload 11
    //   773: invokevirtual 189	android/graphics/Bitmap:getHeight	()I
    //   776: aload 16
    //   778: iconst_0
    //   779: invokestatic 712	android/graphics/Bitmap:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   782: astore 16
    //   784: aload 15
    //   786: astore 11
    //   788: aload 14
    //   790: ifnull +12 -> 802
    //   793: aload 14
    //   795: invokevirtual 758	java/io/InputStream:close	()V
    //   798: aload 15
    //   800: astore 11
    //   802: aload 11
    //   804: ifnull +31 -> 835
    //   807: aload 11
    //   809: athrow
    //   810: aload 10
    //   812: astore 12
    //   814: aload 10
    //   816: astore 13
    //   818: aload 18
    //   820: iload_2
    //   821: iload_1
    //   822: invokestatic 839	com/oneplus/media/ImageUtils:centerCropBitmap	(Landroid/graphics/Bitmap;II)Landroid/graphics/Bitmap;
    //   825: astore 11
    //   827: goto -110 -> 717
    //   830: astore 11
    //   832: goto -30 -> 802
    //   835: aload 16
    //   837: areturn
    //   838: astore 12
    //   840: goto -210 -> 630
    //   843: astore 12
    //   845: aload 13
    //   847: astore 11
    //   849: aload 12
    //   851: athrow
    //   852: astore 13
    //   854: aload 10
    //   856: astore 14
    //   858: aload 12
    //   860: astore 10
    //   862: aload 10
    //   864: astore 12
    //   866: aload 14
    //   868: ifnull +12 -> 880
    //   871: aload 14
    //   873: invokevirtual 758	java/io/InputStream:close	()V
    //   876: aload 10
    //   878: astore 12
    //   880: aload 12
    //   882: ifnull +31 -> 913
    //   885: aload 12
    //   887: athrow
    //   888: aload 10
    //   890: astore 12
    //   892: aload 10
    //   894: aload 14
    //   896: if_acmpeq -16 -> 880
    //   899: aload 10
    //   901: aload 14
    //   903: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   906: aload 10
    //   908: astore 12
    //   910: goto -30 -> 880
    //   913: aload 13
    //   915: athrow
    //   916: aconst_null
    //   917: astore 14
    //   919: aconst_null
    //   920: astore 15
    //   922: aconst_null
    //   923: astore 12
    //   925: aconst_null
    //   926: astore 11
    //   928: aload_0
    //   929: ldc2_w 35
    //   932: invokestatic 733	com/oneplus/io/FileUtils:openLockedInputStream	(Ljava/lang/String;J)Ljava/io/InputStream;
    //   935: astore 13
    //   937: aload 13
    //   939: astore 11
    //   941: aload 13
    //   943: astore 12
    //   945: aload 13
    //   947: aload 10
    //   949: iload_1
    //   950: iload_2
    //   951: iload_3
    //   952: aload 4
    //   954: invokestatic 818	com/oneplus/media/ImageUtils:decodeCenterCropBitmap	(Ljava/io/InputStream;Ljava/lang/Integer;IIILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   957: astore 10
    //   959: aload 10
    //   961: ifnonnull +47 -> 1008
    //   964: iload 5
    //   966: ifeq +42 -> 1008
    //   969: aload 13
    //   971: astore 11
    //   973: aload 13
    //   975: astore 12
    //   977: ldc 32
    //   979: new 193	java/lang/StringBuilder
    //   982: dup
    //   983: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   986: ldc_w 842
    //   989: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   992: aload_0
    //   993: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   996: ldc_w 495
    //   999: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1002: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1005: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   1008: aload 15
    //   1010: astore 4
    //   1012: aload 13
    //   1014: ifnull +12 -> 1026
    //   1017: aload 13
    //   1019: invokevirtual 758	java/io/InputStream:close	()V
    //   1022: aload 15
    //   1024: astore 4
    //   1026: aload 4
    //   1028: ifnull +53 -> 1081
    //   1031: aload 4
    //   1033: athrow
    //   1034: astore 4
    //   1036: iload 5
    //   1038: ifeq +36 -> 1074
    //   1041: ldc 32
    //   1043: new 193	java/lang/StringBuilder
    //   1046: dup
    //   1047: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   1050: ldc_w 842
    //   1053: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1056: aload_0
    //   1057: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1060: ldc_w 495
    //   1063: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1066: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1069: aload 4
    //   1071: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1074: aconst_null
    //   1075: areturn
    //   1076: astore 4
    //   1078: goto -52 -> 1026
    //   1081: aload 10
    //   1083: areturn
    //   1084: astore 4
    //   1086: aload 4
    //   1088: athrow
    //   1089: astore 10
    //   1091: aload 4
    //   1093: astore 12
    //   1095: aload 11
    //   1097: ifnull +12 -> 1109
    //   1100: aload 11
    //   1102: invokevirtual 758	java/io/InputStream:close	()V
    //   1105: aload 4
    //   1107: astore 12
    //   1109: aload 12
    //   1111: ifnull +31 -> 1142
    //   1114: aload 12
    //   1116: athrow
    //   1117: aload 4
    //   1119: astore 12
    //   1121: aload 4
    //   1123: aload 11
    //   1125: if_acmpeq -16 -> 1109
    //   1128: aload 4
    //   1130: aload 11
    //   1132: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   1135: aload 4
    //   1137: astore 12
    //   1139: goto -30 -> 1109
    //   1142: aload 10
    //   1144: athrow
    //   1145: astore 10
    //   1147: aload 14
    //   1149: astore 4
    //   1151: aload 12
    //   1153: astore 11
    //   1155: goto -64 -> 1091
    //   1158: astore 13
    //   1160: aconst_null
    //   1161: astore 10
    //   1163: aload 12
    //   1165: astore 14
    //   1167: goto -305 -> 862
    //   1170: astore 13
    //   1172: aconst_null
    //   1173: astore 10
    //   1175: aload 12
    //   1177: astore 11
    //   1179: goto -317 -> 862
    //   1182: astore 12
    //   1184: aload 14
    //   1186: astore 10
    //   1188: aload 13
    //   1190: astore 11
    //   1192: goto -343 -> 849
    //   1195: goto -495 -> 700
    //   1198: astore 10
    //   1200: aload 11
    //   1202: astore 10
    //   1204: goto -686 -> 518
    //   1207: astore 14
    //   1209: aload 10
    //   1211: ifnonnull -323 -> 888
    //   1214: aload 14
    //   1216: astore 12
    //   1218: goto -338 -> 880
    //   1221: astore 11
    //   1223: aload 4
    //   1225: ifnonnull -108 -> 1117
    //   1228: aload 11
    //   1230: astore 12
    //   1232: goto -123 -> 1109
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1235	0	paramString	String
    //   0	1235	1	paramInt1	int
    //   0	1235	2	paramInt2	int
    //   0	1235	3	paramInt3	int
    //   0	1235	4	paramConfig	Bitmap.Config
    //   8	1029	5	i	int
    //   17	516	6	j	int
    //   26	570	7	k	int
    //   286	31	8	m	int
    //   395	253	9	bool	boolean
    //   48	1034	10	localObject1	Object
    //   1089	54	10	localObject2	Object
    //   1145	1	10	localObject3	Object
    //   1161	26	10	localObject4	Object
    //   1198	1	10	localThrowable1	Throwable
    //   1202	8	10	localObject5	Object
    //   35	479	11	localObject6	Object
    //   516	1	11	localThrowable2	Throwable
    //   545	95	11	localObject7	Object
    //   652	1	11	localThrowable3	Throwable
    //   715	111	11	localObject8	Object
    //   830	1	11	localThrowable4	Throwable
    //   847	354	11	localObject9	Object
    //   1221	8	11	localThrowable5	Throwable
    //   44	769	12	localObject10	Object
    //   838	1	12	localThrowable6	Throwable
    //   843	16	12	localThrowable7	Throwable
    //   864	312	12	localObject11	Object
    //   1182	1	12	localThrowable8	Throwable
    //   1216	15	12	localObject12	Object
    //   32	814	13	localObject13	Object
    //   852	62	13	localObject14	Object
    //   935	83	13	localInputStream	InputStream
    //   1158	1	13	localObject15	Object
    //   1170	19	13	localObject16	Object
    //   105	1080	14	localObject17	Object
    //   1207	8	14	localThrowable9	Throwable
    //   87	936	15	localBitmap	Bitmap
    //   90	746	16	localObject18	Object
    //   84	543	17	localObject19	Object
    //   38	781	18	localObject20	Object
    //   41	548	19	localOptions	android.graphics.BitmapFactory.Options
    //   29	97	20	localObject21	Object
    //   123	56	21	localExifInterface	ExifInterface
    // Exception table:
    //   from	to	target	type
    //   513	516	516	java/lang/Throwable
    //   643	646	516	java/lang/Throwable
    //   807	810	516	java/lang/Throwable
    //   499	504	652	java/lang/Throwable
    //   793	798	830	java/lang/Throwable
    //   621	626	838	java/lang/Throwable
    //   98	107	843	java/lang/Throwable
    //   849	852	852	finally
    //   1031	1034	1034	java/lang/Throwable
    //   1114	1117	1034	java/lang/Throwable
    //   1128	1135	1034	java/lang/Throwable
    //   1142	1145	1034	java/lang/Throwable
    //   1017	1022	1076	java/lang/Throwable
    //   928	937	1084	java/lang/Throwable
    //   945	959	1084	java/lang/Throwable
    //   977	1008	1084	java/lang/Throwable
    //   1086	1089	1089	finally
    //   928	937	1145	finally
    //   945	959	1145	finally
    //   977	1008	1145	finally
    //   98	107	1158	finally
    //   115	125	1170	finally
    //   137	145	1170	finally
    //   153	170	1170	finally
    //   178	185	1170	finally
    //   193	202	1170	finally
    //   210	216	1170	finally
    //   224	236	1170	finally
    //   244	254	1170	finally
    //   262	273	1170	finally
    //   281	288	1170	finally
    //   296	306	1170	finally
    //   314	321	1170	finally
    //   334	343	1170	finally
    //   351	360	1170	finally
    //   368	374	1170	finally
    //   382	388	1170	finally
    //   405	412	1170	finally
    //   420	427	1170	finally
    //   435	448	1170	finally
    //   465	473	1170	finally
    //   481	490	1170	finally
    //   564	580	1170	finally
    //   588	595	1170	finally
    //   668	700	1170	finally
    //   708	717	1170	finally
    //   725	734	1170	finally
    //   742	754	1170	finally
    //   762	784	1170	finally
    //   818	827	1170	finally
    //   115	125	1182	java/lang/Throwable
    //   137	145	1182	java/lang/Throwable
    //   153	170	1182	java/lang/Throwable
    //   178	185	1182	java/lang/Throwable
    //   193	202	1182	java/lang/Throwable
    //   210	216	1182	java/lang/Throwable
    //   224	236	1182	java/lang/Throwable
    //   244	254	1182	java/lang/Throwable
    //   262	273	1182	java/lang/Throwable
    //   281	288	1182	java/lang/Throwable
    //   296	306	1182	java/lang/Throwable
    //   314	321	1182	java/lang/Throwable
    //   334	343	1182	java/lang/Throwable
    //   351	360	1182	java/lang/Throwable
    //   368	374	1182	java/lang/Throwable
    //   382	388	1182	java/lang/Throwable
    //   405	412	1182	java/lang/Throwable
    //   420	427	1182	java/lang/Throwable
    //   435	448	1182	java/lang/Throwable
    //   465	473	1182	java/lang/Throwable
    //   481	490	1182	java/lang/Throwable
    //   564	580	1182	java/lang/Throwable
    //   588	595	1182	java/lang/Throwable
    //   668	700	1182	java/lang/Throwable
    //   708	717	1182	java/lang/Throwable
    //   725	734	1182	java/lang/Throwable
    //   742	754	1182	java/lang/Throwable
    //   762	784	1182	java/lang/Throwable
    //   818	827	1182	java/lang/Throwable
    //   885	888	1198	java/lang/Throwable
    //   899	906	1198	java/lang/Throwable
    //   913	916	1198	java/lang/Throwable
    //   871	876	1207	java/lang/Throwable
    //   1100	1105	1221	java/lang/Throwable
  }
  
  public static Bitmap decodeCenterCropBitmap(String paramString, int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return decodeCenterCropBitmap(paramString, paramInt1, paramInt2, 0, paramConfig);
  }
  
  public static Bitmap decodeCenterCropBitmap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return decodeCenterCropBitmap(paramArrayOfByte, paramInt1, paramInt2, Bitmap.Config.RGB_565);
  }
  
  /* Error */
  public static Bitmap decodeCenterCropBitmap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, Bitmap.Config paramConfig)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aconst_null
    //   4: astore 5
    //   6: new 770	java/io/ByteArrayInputStream
    //   9: dup
    //   10: aload_0
    //   11: invokespecial 773	java/io/ByteArrayInputStream:<init>	([B)V
    //   14: astore_0
    //   15: aload_0
    //   16: iload_1
    //   17: iload_2
    //   18: iload_3
    //   19: aload 4
    //   21: invokestatic 820	com/oneplus/media/ImageUtils:decodeCenterCropBitmap	(Ljava/io/InputStream;IIILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   24: astore 4
    //   26: aload_0
    //   27: ifnull +7 -> 34
    //   30: aload_0
    //   31: invokevirtual 774	java/io/ByteArrayInputStream:close	()V
    //   34: aconst_null
    //   35: astore_0
    //   36: aload_0
    //   37: ifnull +12 -> 49
    //   40: aload_0
    //   41: athrow
    //   42: astore_0
    //   43: aconst_null
    //   44: areturn
    //   45: astore_0
    //   46: goto -10 -> 36
    //   49: aload 4
    //   51: areturn
    //   52: astore_0
    //   53: aload_0
    //   54: athrow
    //   55: astore 4
    //   57: aload_0
    //   58: astore 6
    //   60: aload 5
    //   62: ifnull +11 -> 73
    //   65: aload 5
    //   67: invokevirtual 774	java/io/ByteArrayInputStream:close	()V
    //   70: aload_0
    //   71: astore 6
    //   73: aload 6
    //   75: ifnull +27 -> 102
    //   78: aload 6
    //   80: athrow
    //   81: aload_0
    //   82: astore 6
    //   84: aload_0
    //   85: aload 5
    //   87: if_acmpeq -14 -> 73
    //   90: aload_0
    //   91: aload 5
    //   93: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   96: aload_0
    //   97: astore 6
    //   99: goto -26 -> 73
    //   102: aload 4
    //   104: athrow
    //   105: astore 4
    //   107: aconst_null
    //   108: astore_0
    //   109: aload 6
    //   111: astore 5
    //   113: goto -56 -> 57
    //   116: astore 4
    //   118: aconst_null
    //   119: astore 6
    //   121: aload_0
    //   122: astore 5
    //   124: aload 6
    //   126: astore_0
    //   127: goto -70 -> 57
    //   130: astore 4
    //   132: aload_0
    //   133: astore 5
    //   135: aload 4
    //   137: astore_0
    //   138: goto -85 -> 53
    //   141: astore_0
    //   142: aconst_null
    //   143: areturn
    //   144: astore 5
    //   146: aload_0
    //   147: ifnonnull -66 -> 81
    //   150: aload 5
    //   152: astore 6
    //   154: goto -81 -> 73
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	157	0	paramArrayOfByte	byte[]
    //   0	157	1	paramInt1	int
    //   0	157	2	paramInt2	int
    //   0	157	3	paramInt3	int
    //   0	157	4	paramConfig	Bitmap.Config
    //   4	130	5	localObject1	Object
    //   144	7	5	localThrowable	Throwable
    //   1	152	6	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   40	42	42	java/lang/Throwable
    //   30	34	45	java/lang/Throwable
    //   6	15	52	java/lang/Throwable
    //   53	55	55	finally
    //   6	15	105	finally
    //   15	26	116	finally
    //   15	26	130	java/lang/Throwable
    //   78	81	141	java/lang/Throwable
    //   90	96	141	java/lang/Throwable
    //   102	105	141	java/lang/Throwable
    //   65	70	144	java/lang/Throwable
  }
  
  public static Bitmap decodeCenterCropBitmap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return decodeCenterCropBitmap(paramArrayOfByte, paramInt1, paramInt2, 0, paramConfig);
  }
  
  /* Error */
  public static int decodeOrientation(InputStream paramInputStream)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +13 -> 14
    //   4: ldc 32
    //   6: ldc_w 850
    //   9: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: iconst_0
    //   13: ireturn
    //   14: aconst_null
    //   15: astore 10
    //   17: aconst_null
    //   18: astore 8
    //   20: aconst_null
    //   21: astore 7
    //   23: aconst_null
    //   24: astore 11
    //   26: new 623	com/oneplus/io/StreamState
    //   29: dup
    //   30: aload_0
    //   31: invokespecial 625	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   34: astore 9
    //   36: new 400	com/oneplus/base/SimpleRef
    //   39: dup
    //   40: lconst_0
    //   41: invokestatic 269	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   44: invokespecial 853	com/oneplus/base/SimpleRef:<init>	(Ljava/lang/Object;)V
    //   47: astore 7
    //   49: new 400	com/oneplus/base/SimpleRef
    //   52: dup
    //   53: iconst_0
    //   54: invokestatic 856	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   57: invokespecial 853	com/oneplus/base/SimpleRef:<init>	(Ljava/lang/Object;)V
    //   60: astore 11
    //   62: aload_0
    //   63: aload 11
    //   65: invokestatic 860	com/oneplus/media/ImageUtils:isTiffHeader	(Ljava/io/InputStream;Lcom/oneplus/base/Ref;)Z
    //   68: istore_2
    //   69: iload_2
    //   70: ifeq +192 -> 262
    //   73: aload 7
    //   75: aload 9
    //   77: invokevirtual 863	com/oneplus/io/StreamState:getSavedStreamPosition	()J
    //   80: invokestatic 269	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   83: invokeinterface 865 2 0
    //   88: iload_2
    //   89: ifeq +342 -> 431
    //   92: aload 7
    //   94: invokeinterface 428 1 0
    //   99: checkcast 266	java/lang/Long
    //   102: invokevirtual 431	java/lang/Long:longValue	()J
    //   105: lstore_3
    //   106: aload 9
    //   108: invokevirtual 863	com/oneplus/io/StreamState:getSavedStreamPosition	()J
    //   111: lstore 5
    //   113: aconst_null
    //   114: astore 14
    //   116: aconst_null
    //   117: astore 11
    //   119: aconst_null
    //   120: astore 12
    //   122: aconst_null
    //   123: astore 7
    //   125: aconst_null
    //   126: astore 13
    //   128: new 867	com/oneplus/media/IfdEntryEnumerator
    //   131: dup
    //   132: aload_0
    //   133: lload_3
    //   134: lload 5
    //   136: lsub
    //   137: invokespecial 870	com/oneplus/media/IfdEntryEnumerator:<init>	(Ljava/io/InputStream;J)V
    //   140: astore_0
    //   141: aload_0
    //   142: invokevirtual 872	com/oneplus/media/IfdEntryEnumerator:read	()Z
    //   145: ifeq +178 -> 323
    //   148: aload_0
    //   149: invokevirtual 876	com/oneplus/media/IfdEntryEnumerator:currentIfd	()Lcom/oneplus/media/Ifd;
    //   152: getstatic 87	com/oneplus/media/Ifd:IFD_0	Lcom/oneplus/media/Ifd;
    //   155: if_acmpne +168 -> 323
    //   158: aload_0
    //   159: invokevirtual 879	com/oneplus/media/IfdEntryEnumerator:currentEntryId	()I
    //   162: sipush 274
    //   165: if_icmpne -24 -> 141
    //   168: aload_0
    //   169: invokevirtual 882	com/oneplus/media/IfdEntryEnumerator:getEntryDataInteger	()[I
    //   172: astore 7
    //   174: aload 7
    //   176: ifnull +147 -> 323
    //   179: aload 7
    //   181: arraylength
    //   182: ifle +141 -> 323
    //   185: aload 7
    //   187: iconst_0
    //   188: iaload
    //   189: invokestatic 745	com/oneplus/media/ImageUtils:exifOrientationToDegrees	(I)I
    //   192: istore_1
    //   193: aload 12
    //   195: astore 7
    //   197: aload_0
    //   198: ifnull +11 -> 209
    //   201: aload_0
    //   202: invokevirtual 883	com/oneplus/media/IfdEntryEnumerator:close	()V
    //   205: aload 12
    //   207: astore 7
    //   209: aload 7
    //   211: ifnull +80 -> 291
    //   214: aload 7
    //   216: athrow
    //   217: astore_0
    //   218: aload 9
    //   220: astore 8
    //   222: aload_0
    //   223: athrow
    //   224: astore 7
    //   226: aload_0
    //   227: astore 9
    //   229: aload 8
    //   231: ifnull +11 -> 242
    //   234: aload 8
    //   236: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   239: aload_0
    //   240: astore 9
    //   242: aload 9
    //   244: ifnull +247 -> 491
    //   247: aload 9
    //   249: athrow
    //   250: astore_0
    //   251: ldc 32
    //   253: ldc_w 885
    //   256: aload_0
    //   257: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   260: iconst_0
    //   261: ireturn
    //   262: aload_0
    //   263: invokestatic 609	com/oneplus/media/ImageUtils:isJfifHeader	(Ljava/io/InputStream;)Z
    //   266: ifeq +15 -> 281
    //   269: aload_0
    //   270: aload 7
    //   272: aload 11
    //   274: invokestatic 888	com/oneplus/media/ImageUtils:findTiffHeader	(Ljava/io/InputStream;Lcom/oneplus/base/Ref;Lcom/oneplus/base/Ref;)Z
    //   277: istore_2
    //   278: goto -190 -> 88
    //   281: iconst_0
    //   282: istore_2
    //   283: goto -195 -> 88
    //   286: astore 7
    //   288: goto -79 -> 209
    //   291: aload 8
    //   293: astore_0
    //   294: aload 9
    //   296: ifnull +11 -> 307
    //   299: aload 9
    //   301: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   304: aload 8
    //   306: astore_0
    //   307: aload_0
    //   308: ifnull +13 -> 321
    //   311: aload_0
    //   312: athrow
    //   313: astore_0
    //   314: goto -63 -> 251
    //   317: astore_0
    //   318: goto -11 -> 307
    //   321: iload_1
    //   322: ireturn
    //   323: aload 14
    //   325: astore 7
    //   327: aload_0
    //   328: ifnull +11 -> 339
    //   331: aload_0
    //   332: invokevirtual 883	com/oneplus/media/IfdEntryEnumerator:close	()V
    //   335: aload 14
    //   337: astore 7
    //   339: aload 7
    //   341: ifnull +90 -> 431
    //   344: aload 7
    //   346: athrow
    //   347: astore 7
    //   349: aconst_null
    //   350: astore_0
    //   351: aload 9
    //   353: astore 8
    //   355: goto -129 -> 226
    //   358: astore 7
    //   360: goto -21 -> 339
    //   363: astore 7
    //   365: aload 13
    //   367: astore_0
    //   368: aload 7
    //   370: athrow
    //   371: astore 10
    //   373: aload 7
    //   375: astore 8
    //   377: aload 10
    //   379: astore 7
    //   381: aload 8
    //   383: astore 10
    //   385: aload_0
    //   386: ifnull +11 -> 397
    //   389: aload_0
    //   390: invokevirtual 883	com/oneplus/media/IfdEntryEnumerator:close	()V
    //   393: aload 8
    //   395: astore 10
    //   397: aload 10
    //   399: ifnull +29 -> 428
    //   402: aload 10
    //   404: athrow
    //   405: aload 8
    //   407: astore 10
    //   409: aload 8
    //   411: aload_0
    //   412: if_acmpeq -15 -> 397
    //   415: aload 8
    //   417: aload_0
    //   418: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   421: aload 8
    //   423: astore 10
    //   425: goto -28 -> 397
    //   428: aload 7
    //   430: athrow
    //   431: aload 10
    //   433: astore_0
    //   434: aload 9
    //   436: ifnull +11 -> 447
    //   439: aload 9
    //   441: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   444: aload 10
    //   446: astore_0
    //   447: aload_0
    //   448: ifnull +46 -> 494
    //   451: aload_0
    //   452: athrow
    //   453: astore_0
    //   454: goto -7 -> 447
    //   457: astore 8
    //   459: aload_0
    //   460: ifnonnull +10 -> 470
    //   463: aload 8
    //   465: astore 9
    //   467: goto -225 -> 242
    //   470: aload_0
    //   471: astore 9
    //   473: aload_0
    //   474: aload 8
    //   476: if_acmpeq -234 -> 242
    //   479: aload_0
    //   480: aload 8
    //   482: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   485: aload_0
    //   486: astore 9
    //   488: goto -246 -> 242
    //   491: aload 7
    //   493: athrow
    //   494: iconst_0
    //   495: ireturn
    //   496: astore 9
    //   498: aconst_null
    //   499: astore_0
    //   500: aload 7
    //   502: astore 8
    //   504: aload 9
    //   506: astore 7
    //   508: goto -282 -> 226
    //   511: astore_0
    //   512: aload 11
    //   514: astore 8
    //   516: goto -294 -> 222
    //   519: astore 8
    //   521: aload 7
    //   523: astore_0
    //   524: aload 8
    //   526: astore 7
    //   528: aload 11
    //   530: astore 8
    //   532: goto -151 -> 381
    //   535: astore 7
    //   537: aload 11
    //   539: astore 8
    //   541: goto -160 -> 381
    //   544: astore 7
    //   546: goto -178 -> 368
    //   549: astore_0
    //   550: aload 8
    //   552: ifnonnull -147 -> 405
    //   555: aload_0
    //   556: astore 10
    //   558: goto -161 -> 397
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	561	0	paramInputStream	InputStream
    //   192	130	1	i	int
    //   68	215	2	bool	boolean
    //   105	29	3	l1	long
    //   111	24	5	l2	long
    //   21	194	7	localObject1	Object
    //   224	47	7	localRef	Ref
    //   286	1	7	localThrowable1	Throwable
    //   325	20	7	localObject2	Object
    //   347	1	7	localObject3	Object
    //   358	1	7	localThrowable2	Throwable
    //   363	11	7	localThrowable3	Throwable
    //   379	148	7	localObject4	Object
    //   535	1	7	localObject5	Object
    //   544	1	7	localThrowable4	Throwable
    //   18	404	8	localObject6	Object
    //   457	24	8	localThrowable5	Throwable
    //   502	13	8	localObject7	Object
    //   519	6	8	localObject8	Object
    //   530	21	8	localObject9	Object
    //   34	453	9	localObject10	Object
    //   496	9	9	localObject11	Object
    //   15	1	10	localObject12	Object
    //   371	7	10	localObject13	Object
    //   383	174	10	localObject14	Object
    //   24	514	11	localSimpleRef	com.oneplus.base.SimpleRef
    //   120	86	12	localObject15	Object
    //   126	240	13	localObject16	Object
    //   114	222	14	localObject17	Object
    // Exception table:
    //   from	to	target	type
    //   36	69	217	java/lang/Throwable
    //   73	88	217	java/lang/Throwable
    //   92	113	217	java/lang/Throwable
    //   214	217	217	java/lang/Throwable
    //   262	278	217	java/lang/Throwable
    //   344	347	217	java/lang/Throwable
    //   402	405	217	java/lang/Throwable
    //   415	421	217	java/lang/Throwable
    //   428	431	217	java/lang/Throwable
    //   222	224	224	finally
    //   247	250	250	java/lang/Throwable
    //   479	485	250	java/lang/Throwable
    //   491	494	250	java/lang/Throwable
    //   201	205	286	java/lang/Throwable
    //   311	313	313	java/lang/Throwable
    //   451	453	313	java/lang/Throwable
    //   299	304	317	java/lang/Throwable
    //   36	69	347	finally
    //   73	88	347	finally
    //   92	113	347	finally
    //   201	205	347	finally
    //   214	217	347	finally
    //   262	278	347	finally
    //   331	335	347	finally
    //   344	347	347	finally
    //   389	393	347	finally
    //   402	405	347	finally
    //   415	421	347	finally
    //   428	431	347	finally
    //   331	335	358	java/lang/Throwable
    //   128	141	363	java/lang/Throwable
    //   368	371	371	finally
    //   439	444	453	java/lang/Throwable
    //   234	239	457	java/lang/Throwable
    //   26	36	496	finally
    //   26	36	511	java/lang/Throwable
    //   128	141	519	finally
    //   141	174	535	finally
    //   179	193	535	finally
    //   141	174	544	java/lang/Throwable
    //   179	193	544	java/lang/Throwable
    //   389	393	549	java/lang/Throwable
  }
  
  /* Error */
  public static int decodeOrientation(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 6
    //   6: aconst_null
    //   7: astore_3
    //   8: aconst_null
    //   9: astore 5
    //   11: new 891	java/io/FileInputStream
    //   14: dup
    //   15: aload_0
    //   16: invokespecial 892	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   19: astore_2
    //   20: aload_2
    //   21: invokestatic 618	com/oneplus/media/ImageUtils:decodeOrientation	(Ljava/io/InputStream;)I
    //   24: istore_1
    //   25: aload 6
    //   27: astore_3
    //   28: aload_2
    //   29: ifnull +10 -> 39
    //   32: aload_2
    //   33: invokevirtual 893	java/io/FileInputStream:close	()V
    //   36: aload 6
    //   38: astore_3
    //   39: aload_3
    //   40: ifnull +38 -> 78
    //   43: aload_3
    //   44: athrow
    //   45: astore_2
    //   46: ldc 32
    //   48: new 193	java/lang/StringBuilder
    //   51: dup
    //   52: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   55: ldc_w 895
    //   58: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   61: aload_0
    //   62: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   65: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   68: aload_2
    //   69: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   72: iconst_0
    //   73: ireturn
    //   74: astore_3
    //   75: goto -36 -> 39
    //   78: iload_1
    //   79: ireturn
    //   80: astore_3
    //   81: aload 5
    //   83: astore_2
    //   84: aload_3
    //   85: athrow
    //   86: astore 5
    //   88: aload_3
    //   89: astore 4
    //   91: aload 5
    //   93: astore_3
    //   94: aload 4
    //   96: astore 5
    //   98: aload_2
    //   99: ifnull +11 -> 110
    //   102: aload_2
    //   103: invokevirtual 893	java/io/FileInputStream:close	()V
    //   106: aload 4
    //   108: astore 5
    //   110: aload 5
    //   112: ifnull +29 -> 141
    //   115: aload 5
    //   117: athrow
    //   118: aload 4
    //   120: astore 5
    //   122: aload 4
    //   124: aload_2
    //   125: if_acmpeq -15 -> 110
    //   128: aload 4
    //   130: aload_2
    //   131: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   134: aload 4
    //   136: astore 5
    //   138: goto -28 -> 110
    //   141: aload_3
    //   142: athrow
    //   143: astore 5
    //   145: aload_3
    //   146: astore_2
    //   147: aload 5
    //   149: astore_3
    //   150: goto -56 -> 94
    //   153: astore_3
    //   154: goto -60 -> 94
    //   157: astore_3
    //   158: goto -74 -> 84
    //   161: astore_2
    //   162: goto -116 -> 46
    //   165: astore_2
    //   166: aload 4
    //   168: ifnonnull -50 -> 118
    //   171: aload_2
    //   172: astore 5
    //   174: goto -64 -> 110
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	177	0	paramString	String
    //   24	55	1	i	int
    //   19	14	2	localFileInputStream	java.io.FileInputStream
    //   45	24	2	localThrowable1	Throwable
    //   83	64	2	localObject1	Object
    //   161	1	2	localThrowable2	Throwable
    //   165	7	2	localThrowable3	Throwable
    //   7	37	3	localObject2	Object
    //   74	1	3	localThrowable4	Throwable
    //   80	9	3	localThrowable5	Throwable
    //   93	57	3	localObject3	Object
    //   153	1	3	localObject4	Object
    //   157	1	3	localThrowable6	Throwable
    //   1	166	4	localObject5	Object
    //   9	73	5	localObject6	Object
    //   86	6	5	localObject7	Object
    //   96	41	5	localObject8	Object
    //   143	5	5	localObject9	Object
    //   172	1	5	localObject10	Object
    //   4	33	6	localObject11	Object
    // Exception table:
    //   from	to	target	type
    //   43	45	45	java/lang/Throwable
    //   32	36	74	java/lang/Throwable
    //   11	20	80	java/lang/Throwable
    //   84	86	86	finally
    //   11	20	143	finally
    //   20	25	153	finally
    //   20	25	157	java/lang/Throwable
    //   115	118	161	java/lang/Throwable
    //   128	134	161	java/lang/Throwable
    //   141	143	161	java/lang/Throwable
    //   102	106	165	java/lang/Throwable
  }
  
  public static Size decodeSize(InputStream paramInputStream)
  {
    return decodeSize(paramInputStream, null, 0);
  }
  
  public static Size decodeSize(InputStream paramInputStream, int paramInt)
  {
    return decodeSize(paramInputStream, null, paramInt);
  }
  
  public static Size decodeSize(InputStream paramInputStream, Ref<Integer> paramRef)
  {
    return decodeSize(paramInputStream, paramRef, 0);
  }
  
  /* Error */
  public static Size decodeSize(InputStream paramInputStream, Ref<Integer> paramRef, int paramInt)
  {
    // Byte code:
    //   0: iload_2
    //   1: bipush 64
    //   3: iand
    //   4: ifne +23 -> 27
    //   7: iconst_1
    //   8: istore_3
    //   9: aload_0
    //   10: ifnonnull +22 -> 32
    //   13: iload_3
    //   14: ifeq +11 -> 25
    //   17: ldc 32
    //   19: ldc_w 905
    //   22: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   25: aconst_null
    //   26: areturn
    //   27: iconst_0
    //   28: istore_3
    //   29: goto -20 -> 9
    //   32: aconst_null
    //   33: astore 9
    //   35: aconst_null
    //   36: astore 8
    //   38: new 623	com/oneplus/io/StreamState
    //   41: dup
    //   42: aload_0
    //   43: invokespecial 625	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   46: astore 7
    //   48: iconst_0
    //   49: istore 5
    //   51: iload_2
    //   52: bipush 32
    //   54: iand
    //   55: ifne +130 -> 185
    //   58: iconst_1
    //   59: istore 4
    //   61: aload_1
    //   62: ifnonnull +11 -> 73
    //   65: iload 5
    //   67: istore_2
    //   68: iload 4
    //   70: ifeq +27 -> 97
    //   73: aload_0
    //   74: invokestatic 618	com/oneplus/media/ImageUtils:decodeOrientation	(Ljava/io/InputStream;)I
    //   77: istore 6
    //   79: aload_1
    //   80: ifnull +261 -> 341
    //   83: aload_1
    //   84: iload 6
    //   86: invokestatic 234	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   89: invokeinterface 865 2 0
    //   94: goto +247 -> 341
    //   97: new 656	android/graphics/BitmapFactory$Options
    //   100: dup
    //   101: invokespecial 657	android/graphics/BitmapFactory$Options:<init>	()V
    //   104: astore_1
    //   105: aload_1
    //   106: iconst_1
    //   107: putfield 661	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   110: aload_0
    //   111: aconst_null
    //   112: aload_1
    //   113: invokestatic 666	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   116: pop
    //   117: aload_1
    //   118: getfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   121: ifle +121 -> 242
    //   124: aload_1
    //   125: getfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   128: ifle +114 -> 242
    //   131: iload_2
    //   132: ifeq +70 -> 202
    //   135: new 549	android/util/Size
    //   138: dup
    //   139: aload_1
    //   140: getfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   143: aload_1
    //   144: getfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   147: invokespecial 906	android/util/Size:<init>	(II)V
    //   150: astore_1
    //   151: aload 7
    //   153: ifnull +8 -> 161
    //   156: aload 7
    //   158: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   161: aconst_null
    //   162: astore_0
    //   163: aload_0
    //   164: ifnull +36 -> 200
    //   167: aload_0
    //   168: athrow
    //   169: astore_0
    //   170: iload_3
    //   171: ifeq +12 -> 183
    //   174: ldc 32
    //   176: ldc_w 908
    //   179: aload_0
    //   180: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   183: aconst_null
    //   184: areturn
    //   185: iconst_0
    //   186: istore 4
    //   188: goto -127 -> 61
    //   191: iconst_0
    //   192: istore_2
    //   193: goto -96 -> 97
    //   196: astore_0
    //   197: goto -34 -> 163
    //   200: aload_1
    //   201: areturn
    //   202: new 549	android/util/Size
    //   205: dup
    //   206: aload_1
    //   207: getfield 671	android/graphics/BitmapFactory$Options:outWidth	I
    //   210: aload_1
    //   211: getfield 674	android/graphics/BitmapFactory$Options:outHeight	I
    //   214: invokespecial 906	android/util/Size:<init>	(II)V
    //   217: astore_1
    //   218: aload 7
    //   220: ifnull +8 -> 228
    //   223: aload 7
    //   225: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   228: aconst_null
    //   229: astore_0
    //   230: aload_0
    //   231: ifnull +9 -> 240
    //   234: aload_0
    //   235: athrow
    //   236: astore_0
    //   237: goto -7 -> 230
    //   240: aload_1
    //   241: areturn
    //   242: aload 7
    //   244: ifnull +8 -> 252
    //   247: aload 7
    //   249: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   252: aconst_null
    //   253: astore_0
    //   254: aload_0
    //   255: ifnull +9 -> 264
    //   258: aload_0
    //   259: athrow
    //   260: astore_0
    //   261: goto -7 -> 254
    //   264: aconst_null
    //   265: areturn
    //   266: astore_0
    //   267: aload 8
    //   269: astore 7
    //   271: aload_0
    //   272: athrow
    //   273: astore_1
    //   274: aload_0
    //   275: astore 8
    //   277: aload 7
    //   279: ifnull +11 -> 290
    //   282: aload 7
    //   284: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   287: aload_0
    //   288: astore 8
    //   290: aload 8
    //   292: ifnull +27 -> 319
    //   295: aload 8
    //   297: athrow
    //   298: aload_0
    //   299: astore 8
    //   301: aload_0
    //   302: aload 7
    //   304: if_acmpeq -14 -> 290
    //   307: aload_0
    //   308: aload 7
    //   310: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   313: aload_0
    //   314: astore 8
    //   316: goto -26 -> 290
    //   319: aload_1
    //   320: athrow
    //   321: astore_1
    //   322: aconst_null
    //   323: astore_0
    //   324: aload 9
    //   326: astore 7
    //   328: goto -54 -> 274
    //   331: astore_1
    //   332: aconst_null
    //   333: astore_0
    //   334: goto -60 -> 274
    //   337: astore_0
    //   338: goto -67 -> 271
    //   341: iload 5
    //   343: istore_2
    //   344: iload 4
    //   346: ifeq -249 -> 97
    //   349: iload 6
    //   351: bipush 90
    //   353: if_icmpeq +11 -> 364
    //   356: iload 6
    //   358: sipush 270
    //   361: if_icmpne -170 -> 191
    //   364: iconst_1
    //   365: istore_2
    //   366: goto -269 -> 97
    //   369: astore_0
    //   370: goto -200 -> 170
    //   373: astore 7
    //   375: aload_0
    //   376: ifnonnull -78 -> 298
    //   379: aload 7
    //   381: astore 8
    //   383: goto -93 -> 290
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	386	0	paramInputStream	InputStream
    //   0	386	1	paramRef	Ref<Integer>
    //   0	386	2	paramInt	int
    //   8	163	3	i	int
    //   59	286	4	j	int
    //   49	293	5	k	int
    //   77	285	6	m	int
    //   46	281	7	localObject1	Object
    //   373	7	7	localThrowable	Throwable
    //   36	346	8	localObject2	Object
    //   33	292	9	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   167	169	169	java/lang/Throwable
    //   234	236	169	java/lang/Throwable
    //   258	260	169	java/lang/Throwable
    //   156	161	196	java/lang/Throwable
    //   223	228	236	java/lang/Throwable
    //   247	252	260	java/lang/Throwable
    //   38	48	266	java/lang/Throwable
    //   271	273	273	finally
    //   38	48	321	finally
    //   73	79	331	finally
    //   83	94	331	finally
    //   97	131	331	finally
    //   135	151	331	finally
    //   202	218	331	finally
    //   73	79	337	java/lang/Throwable
    //   83	94	337	java/lang/Throwable
    //   97	131	337	java/lang/Throwable
    //   135	151	337	java/lang/Throwable
    //   202	218	337	java/lang/Throwable
    //   295	298	369	java/lang/Throwable
    //   307	313	369	java/lang/Throwable
    //   319	321	369	java/lang/Throwable
    //   282	287	373	java/lang/Throwable
  }
  
  public static Size decodeSize(String paramString)
  {
    return decodeSize(paramString, null, 0);
  }
  
  public static Size decodeSize(String paramString, int paramInt)
  {
    return decodeSize(paramString, null, paramInt);
  }
  
  public static Size decodeSize(String paramString, Ref<Integer> paramRef)
  {
    return decodeSize(paramString, paramRef, 0);
  }
  
  /* Error */
  public static Size decodeSize(String paramString, Ref<Integer> paramRef, int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore_3
    //   5: aload_0
    //   6: ldc2_w 35
    //   9: invokestatic 733	com/oneplus/io/FileUtils:openLockedInputStream	(Ljava/lang/String;J)Ljava/io/InputStream;
    //   12: astore 5
    //   14: aload 5
    //   16: astore_3
    //   17: aload 5
    //   19: astore 4
    //   21: aload 5
    //   23: aload_1
    //   24: iload_2
    //   25: invokestatic 900	com/oneplus/media/ImageUtils:decodeSize	(Ljava/io/InputStream;Lcom/oneplus/base/Ref;I)Landroid/util/Size;
    //   28: astore 6
    //   30: aload 5
    //   32: ifnull +8 -> 40
    //   35: aload 5
    //   37: invokevirtual 758	java/io/InputStream:close	()V
    //   40: aconst_null
    //   41: astore_1
    //   42: aload_1
    //   43: ifnull +51 -> 94
    //   46: aload_1
    //   47: athrow
    //   48: astore_1
    //   49: iload_2
    //   50: bipush 64
    //   52: iand
    //   53: ifne +35 -> 88
    //   56: ldc 32
    //   58: new 193	java/lang/StringBuilder
    //   61: dup
    //   62: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   65: ldc_w 918
    //   68: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: aload_0
    //   72: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   75: ldc_w 495
    //   78: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   81: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: aload_1
    //   85: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   88: aconst_null
    //   89: areturn
    //   90: astore_1
    //   91: goto -49 -> 42
    //   94: aload 6
    //   96: areturn
    //   97: astore_1
    //   98: aload_1
    //   99: athrow
    //   100: astore 4
    //   102: aload_1
    //   103: astore 5
    //   105: aload_3
    //   106: ifnull +10 -> 116
    //   109: aload_3
    //   110: invokevirtual 758	java/io/InputStream:close	()V
    //   113: aload_1
    //   114: astore 5
    //   116: aload 5
    //   118: ifnull +25 -> 143
    //   121: aload 5
    //   123: athrow
    //   124: aload_1
    //   125: astore 5
    //   127: aload_1
    //   128: aload_3
    //   129: if_acmpeq -13 -> 116
    //   132: aload_1
    //   133: aload_3
    //   134: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   137: aload_1
    //   138: astore 5
    //   140: goto -24 -> 116
    //   143: aload 4
    //   145: athrow
    //   146: astore 5
    //   148: aconst_null
    //   149: astore_1
    //   150: aload 4
    //   152: astore_3
    //   153: aload 5
    //   155: astore 4
    //   157: goto -55 -> 102
    //   160: astore_3
    //   161: aload_1
    //   162: ifnonnull -38 -> 124
    //   165: aload_3
    //   166: astore 5
    //   168: goto -52 -> 116
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	171	0	paramString	String
    //   0	171	1	paramRef	Ref<Integer>
    //   0	171	2	paramInt	int
    //   4	149	3	localObject1	Object
    //   160	6	3	localThrowable1	Throwable
    //   1	19	4	localObject2	Object
    //   100	51	4	localObject3	Object
    //   155	1	4	localObject4	Object
    //   12	127	5	localObject5	Object
    //   146	8	5	localObject6	Object
    //   166	1	5	localThrowable2	Throwable
    //   28	67	6	localSize	Size
    // Exception table:
    //   from	to	target	type
    //   46	48	48	java/lang/Throwable
    //   121	124	48	java/lang/Throwable
    //   132	137	48	java/lang/Throwable
    //   143	146	48	java/lang/Throwable
    //   35	40	90	java/lang/Throwable
    //   5	14	97	java/lang/Throwable
    //   21	30	97	java/lang/Throwable
    //   98	100	100	finally
    //   5	14	146	finally
    //   21	30	146	finally
    //   109	113	160	java/lang/Throwable
  }
  
  /* Error */
  public static long decodeTakenTime(InputStream paramInputStream)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +13 -> 14
    //   4: ldc 32
    //   6: ldc_w 923
    //   9: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: lconst_0
    //   13: lreturn
    //   14: aconst_null
    //   15: astore 13
    //   17: aconst_null
    //   18: astore 12
    //   20: aconst_null
    //   21: astore 15
    //   23: new 623	com/oneplus/io/StreamState
    //   26: dup
    //   27: aload_0
    //   28: invokespecial 625	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   31: astore 14
    //   33: new 400	com/oneplus/base/SimpleRef
    //   36: dup
    //   37: lconst_0
    //   38: invokestatic 269	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   41: invokespecial 853	com/oneplus/base/SimpleRef:<init>	(Ljava/lang/Object;)V
    //   44: astore 12
    //   46: new 400	com/oneplus/base/SimpleRef
    //   49: dup
    //   50: iconst_0
    //   51: invokestatic 856	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   54: invokespecial 853	com/oneplus/base/SimpleRef:<init>	(Ljava/lang/Object;)V
    //   57: astore 15
    //   59: aload_0
    //   60: aload 15
    //   62: invokestatic 860	com/oneplus/media/ImageUtils:isTiffHeader	(Ljava/io/InputStream;Lcom/oneplus/base/Ref;)Z
    //   65: istore_3
    //   66: lconst_0
    //   67: lstore 8
    //   69: lconst_0
    //   70: lstore 10
    //   72: iload_3
    //   73: ifeq +187 -> 260
    //   76: aload 12
    //   78: aload 14
    //   80: invokevirtual 863	com/oneplus/io/StreamState:getSavedStreamPosition	()J
    //   83: invokestatic 269	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   86: invokeinterface 865 2 0
    //   91: lload 8
    //   93: lstore 4
    //   95: lload 10
    //   97: lstore 6
    //   99: iload_3
    //   100: ifeq +533 -> 633
    //   103: aload 12
    //   105: invokeinterface 428 1 0
    //   110: checkcast 266	java/lang/Long
    //   113: invokevirtual 431	java/lang/Long:longValue	()J
    //   116: lstore 4
    //   118: aload 14
    //   120: invokevirtual 863	com/oneplus/io/StreamState:getSavedStreamPosition	()J
    //   123: lstore 6
    //   125: aconst_null
    //   126: astore 16
    //   128: aconst_null
    //   129: astore 15
    //   131: aconst_null
    //   132: astore 17
    //   134: aconst_null
    //   135: astore 12
    //   137: new 867	com/oneplus/media/IfdEntryEnumerator
    //   140: dup
    //   141: aload_0
    //   142: lload 4
    //   144: lload 6
    //   146: lsub
    //   147: invokespecial 870	com/oneplus/media/IfdEntryEnumerator:<init>	(Ljava/io/InputStream;J)V
    //   150: astore_0
    //   151: lload 10
    //   153: lstore 6
    //   155: lload 8
    //   157: lstore 4
    //   159: aload_0
    //   160: invokevirtual 872	com/oneplus/media/IfdEntryEnumerator:read	()Z
    //   163: ifeq +420 -> 583
    //   166: invokestatic 925	com/oneplus/media/ImageUtils:-getcom-oneplus-media-IfdSwitchesValues	()[I
    //   169: aload_0
    //   170: invokevirtual 876	com/oneplus/media/IfdEntryEnumerator:currentIfd	()Lcom/oneplus/media/Ifd;
    //   173: invokevirtual 81	com/oneplus/media/Ifd:ordinal	()I
    //   176: iaload
    //   177: tableswitch	default:+587->764, 1:+23->200, 2:+206->383
    //   200: aload_0
    //   201: invokevirtual 879	com/oneplus/media/IfdEntryEnumerator:currentEntryId	()I
    //   204: ldc_w 926
    //   207: if_icmpne -48 -> 159
    //   210: aload_0
    //   211: invokevirtual 929	com/oneplus/media/IfdEntryEnumerator:getEntryDataString	()Ljava/lang/String;
    //   214: astore 12
    //   216: aload 12
    //   218: ifnull -59 -> 159
    //   221: aload 12
    //   223: invokevirtual 930	java/lang/String:isEmpty	()Z
    //   226: ifne -67 -> 159
    //   229: new 932	java/text/SimpleDateFormat
    //   232: dup
    //   233: ldc_w 934
    //   236: invokespecial 935	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
    //   239: astore 17
    //   241: aload 17
    //   243: aload 12
    //   245: invokevirtual 939	java/text/SimpleDateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
    //   248: invokevirtual 944	java/util/Date:getTime	()J
    //   251: lstore 8
    //   253: lload 8
    //   255: lstore 6
    //   257: goto -98 -> 159
    //   260: aload_0
    //   261: invokestatic 609	com/oneplus/media/ImageUtils:isJfifHeader	(Ljava/io/InputStream;)Z
    //   264: ifeq +15 -> 279
    //   267: aload_0
    //   268: aload 12
    //   270: aload 15
    //   272: invokestatic 888	com/oneplus/media/ImageUtils:findTiffHeader	(Ljava/io/InputStream;Lcom/oneplus/base/Ref;Lcom/oneplus/base/Ref;)Z
    //   275: istore_3
    //   276: goto -185 -> 91
    //   279: iconst_0
    //   280: istore_3
    //   281: goto -190 -> 91
    //   284: astore 12
    //   286: ldc 32
    //   288: ldc_w 946
    //   291: aload 12
    //   293: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   296: goto -137 -> 159
    //   299: astore 12
    //   301: aload 12
    //   303: athrow
    //   304: astore 15
    //   306: aload 12
    //   308: astore 13
    //   310: aload 15
    //   312: astore 12
    //   314: aload 13
    //   316: astore 15
    //   318: aload_0
    //   319: ifnull +11 -> 330
    //   322: aload_0
    //   323: invokevirtual 883	com/oneplus/media/IfdEntryEnumerator:close	()V
    //   326: aload 13
    //   328: astore 15
    //   330: aload 15
    //   332: ifnull +298 -> 630
    //   335: aload 15
    //   337: athrow
    //   338: astore_0
    //   339: aload 14
    //   341: astore 13
    //   343: aload_0
    //   344: athrow
    //   345: astore 12
    //   347: aload_0
    //   348: astore 14
    //   350: aload 13
    //   352: ifnull +11 -> 363
    //   355: aload 13
    //   357: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   360: aload_0
    //   361: astore 14
    //   363: aload 14
    //   365: ifnull +349 -> 714
    //   368: aload 14
    //   370: athrow
    //   371: astore_0
    //   372: ldc 32
    //   374: ldc_w 948
    //   377: aload_0
    //   378: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   381: lconst_0
    //   382: lreturn
    //   383: aload_0
    //   384: invokevirtual 879	com/oneplus/media/IfdEntryEnumerator:currentEntryId	()I
    //   387: bipush 29
    //   389: if_icmpne +97 -> 486
    //   392: aload_0
    //   393: invokevirtual 929	com/oneplus/media/IfdEntryEnumerator:getEntryDataString	()Ljava/lang/String;
    //   396: astore 12
    //   398: ldc 32
    //   400: new 193	java/lang/StringBuilder
    //   403: dup
    //   404: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   407: ldc_w 950
    //   410: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   413: aload 12
    //   415: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   418: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   421: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   424: aload 12
    //   426: ifnull -267 -> 159
    //   429: aload 12
    //   431: invokevirtual 930	java/lang/String:isEmpty	()Z
    //   434: ifne -275 -> 159
    //   437: new 932	java/text/SimpleDateFormat
    //   440: dup
    //   441: ldc_w 952
    //   444: invokespecial 935	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
    //   447: astore 17
    //   449: aload 17
    //   451: aload 12
    //   453: invokevirtual 939	java/text/SimpleDateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
    //   456: invokevirtual 944	java/util/Date:getTime	()J
    //   459: lstore 8
    //   461: lload 4
    //   463: lload 8
    //   465: ladd
    //   466: lstore 4
    //   468: goto -309 -> 159
    //   471: astore 12
    //   473: ldc 32
    //   475: ldc_w 954
    //   478: aload 12
    //   480: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   483: goto -324 -> 159
    //   486: aload_0
    //   487: invokevirtual 879	com/oneplus/media/IfdEntryEnumerator:currentEntryId	()I
    //   490: bipush 7
    //   492: if_icmpne -333 -> 159
    //   495: aload_0
    //   496: invokevirtual 958	com/oneplus/media/IfdEntryEnumerator:getEntryDataRational	()[Landroid/util/Rational;
    //   499: astore 12
    //   501: aload 12
    //   503: arraylength
    //   504: ifle -345 -> 159
    //   507: iconst_0
    //   508: istore_1
    //   509: lload 4
    //   511: lstore 8
    //   513: iload_1
    //   514: aload 12
    //   516: arraylength
    //   517: if_icmpge +33 -> 550
    //   520: aload 12
    //   522: iload_1
    //   523: aaload
    //   524: invokevirtual 961	android/util/Rational:getNumerator	()I
    //   527: istore_2
    //   528: iload_1
    //   529: ifne +258 -> 787
    //   532: lload 8
    //   534: iload_2
    //   535: sipush 3600
    //   538: imul
    //   539: sipush 1000
    //   542: imul
    //   543: i2l
    //   544: ladd
    //   545: lstore 4
    //   547: goto +229 -> 776
    //   550: ldc 32
    //   552: new 193	java/lang/StringBuilder
    //   555: dup
    //   556: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   559: ldc_w 963
    //   562: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   565: lload 8
    //   567: invokevirtual 966	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   570: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   573: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   576: lload 8
    //   578: lstore 4
    //   580: goto -421 -> 159
    //   583: aload 16
    //   585: astore 12
    //   587: aload_0
    //   588: ifnull +11 -> 599
    //   591: aload_0
    //   592: invokevirtual 883	com/oneplus/media/IfdEntryEnumerator:close	()V
    //   595: aload 16
    //   597: astore 12
    //   599: aload 12
    //   601: ifnull +32 -> 633
    //   604: aload 12
    //   606: athrow
    //   607: aload 13
    //   609: astore 15
    //   611: aload 13
    //   613: aload_0
    //   614: if_acmpeq -284 -> 330
    //   617: aload 13
    //   619: aload_0
    //   620: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   623: aload 13
    //   625: astore 15
    //   627: goto -297 -> 330
    //   630: aload 12
    //   632: athrow
    //   633: lload 4
    //   635: lconst_0
    //   636: lcmp
    //   637: ifle +29 -> 666
    //   640: aload 13
    //   642: astore_0
    //   643: aload 14
    //   645: ifnull +11 -> 656
    //   648: aload 14
    //   650: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   653: aload 13
    //   655: astore_0
    //   656: aload_0
    //   657: ifnull +20 -> 677
    //   660: aload_0
    //   661: athrow
    //   662: astore_0
    //   663: goto -291 -> 372
    //   666: lload 6
    //   668: lstore 4
    //   670: goto -30 -> 640
    //   673: astore_0
    //   674: goto -18 -> 656
    //   677: lload 4
    //   679: lreturn
    //   680: astore 13
    //   682: aload_0
    //   683: ifnonnull +10 -> 693
    //   686: aload 13
    //   688: astore 14
    //   690: goto -327 -> 363
    //   693: aload_0
    //   694: astore 14
    //   696: aload_0
    //   697: aload 13
    //   699: if_acmpeq -336 -> 363
    //   702: aload_0
    //   703: aload 13
    //   705: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   708: aload_0
    //   709: astore 14
    //   711: goto -348 -> 363
    //   714: aload 12
    //   716: athrow
    //   717: astore 14
    //   719: aconst_null
    //   720: astore_0
    //   721: aload 12
    //   723: astore 13
    //   725: aload 14
    //   727: astore 12
    //   729: goto -382 -> 347
    //   732: astore_0
    //   733: aload 15
    //   735: astore 13
    //   737: goto -394 -> 343
    //   740: astore 12
    //   742: aload 17
    //   744: astore_0
    //   745: aload 15
    //   747: astore 13
    //   749: goto -435 -> 314
    //   752: astore 13
    //   754: aload 12
    //   756: astore_0
    //   757: aload 13
    //   759: astore 12
    //   761: goto -460 -> 301
    //   764: goto -605 -> 159
    //   767: astore 12
    //   769: aload 15
    //   771: astore 13
    //   773: goto -459 -> 314
    //   776: iload_1
    //   777: iconst_1
    //   778: iadd
    //   779: istore_1
    //   780: lload 4
    //   782: lstore 8
    //   784: goto -271 -> 513
    //   787: iload_1
    //   788: iconst_1
    //   789: if_icmpne +20 -> 809
    //   792: lload 8
    //   794: iload_2
    //   795: bipush 60
    //   797: imul
    //   798: sipush 1000
    //   801: imul
    //   802: i2l
    //   803: ladd
    //   804: lstore 4
    //   806: goto -30 -> 776
    //   809: lload 8
    //   811: lstore 4
    //   813: iload_1
    //   814: iconst_2
    //   815: if_icmpne -39 -> 776
    //   818: lload 8
    //   820: iload_2
    //   821: sipush 1000
    //   824: imul
    //   825: i2l
    //   826: ladd
    //   827: lstore 4
    //   829: goto -53 -> 776
    //   832: astore 12
    //   834: aconst_null
    //   835: astore_0
    //   836: aload 14
    //   838: astore 13
    //   840: goto -493 -> 347
    //   843: astore 12
    //   845: goto -246 -> 599
    //   848: astore_0
    //   849: aload 13
    //   851: ifnonnull -244 -> 607
    //   854: aload_0
    //   855: astore 15
    //   857: goto -527 -> 330
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	860	0	paramInputStream	InputStream
    //   508	308	1	i	int
    //   527	298	2	j	int
    //   65	216	3	bool	boolean
    //   93	735	4	l1	long
    //   97	570	6	l2	long
    //   67	752	8	l3	long
    //   70	82	10	l4	long
    //   18	251	12	localObject1	Object
    //   284	8	12	localThrowable1	Throwable
    //   299	8	12	localThrowable2	Throwable
    //   312	1	12	localObject2	Object
    //   345	1	12	localObject3	Object
    //   396	56	12	str	String
    //   471	8	12	localThrowable3	Throwable
    //   499	229	12	localObject4	Object
    //   740	15	12	localObject5	Object
    //   759	1	12	localThrowable4	Throwable
    //   767	1	12	localObject6	Object
    //   832	1	12	localObject7	Object
    //   843	1	12	localThrowable5	Throwable
    //   15	639	13	localObject8	Object
    //   680	24	13	localThrowable6	Throwable
    //   723	25	13	localObject9	Object
    //   752	6	13	localThrowable7	Throwable
    //   771	79	13	localObject10	Object
    //   31	679	14	localObject11	Object
    //   717	120	14	localObject12	Object
    //   21	250	15	localSimpleRef	com.oneplus.base.SimpleRef
    //   304	7	15	localObject13	Object
    //   316	540	15	localObject14	Object
    //   126	470	16	localObject15	Object
    //   132	611	17	localSimpleDateFormat	java.text.SimpleDateFormat
    // Exception table:
    //   from	to	target	type
    //   241	253	284	java/lang/Throwable
    //   159	200	299	java/lang/Throwable
    //   200	216	299	java/lang/Throwable
    //   221	241	299	java/lang/Throwable
    //   286	296	299	java/lang/Throwable
    //   383	424	299	java/lang/Throwable
    //   429	449	299	java/lang/Throwable
    //   473	483	299	java/lang/Throwable
    //   486	507	299	java/lang/Throwable
    //   513	528	299	java/lang/Throwable
    //   550	576	299	java/lang/Throwable
    //   301	304	304	finally
    //   33	66	338	java/lang/Throwable
    //   76	91	338	java/lang/Throwable
    //   103	125	338	java/lang/Throwable
    //   260	276	338	java/lang/Throwable
    //   335	338	338	java/lang/Throwable
    //   604	607	338	java/lang/Throwable
    //   617	623	338	java/lang/Throwable
    //   630	633	338	java/lang/Throwable
    //   343	345	345	finally
    //   368	371	371	java/lang/Throwable
    //   702	708	371	java/lang/Throwable
    //   714	717	371	java/lang/Throwable
    //   449	461	471	java/lang/Throwable
    //   660	662	662	java/lang/Throwable
    //   648	653	673	java/lang/Throwable
    //   355	360	680	java/lang/Throwable
    //   23	33	717	finally
    //   23	33	732	java/lang/Throwable
    //   137	151	740	finally
    //   137	151	752	java/lang/Throwable
    //   159	200	767	finally
    //   200	216	767	finally
    //   221	241	767	finally
    //   241	253	767	finally
    //   286	296	767	finally
    //   383	424	767	finally
    //   429	449	767	finally
    //   449	461	767	finally
    //   473	483	767	finally
    //   486	507	767	finally
    //   513	528	767	finally
    //   550	576	767	finally
    //   33	66	832	finally
    //   76	91	832	finally
    //   103	125	832	finally
    //   260	276	832	finally
    //   322	326	832	finally
    //   335	338	832	finally
    //   591	595	832	finally
    //   604	607	832	finally
    //   617	623	832	finally
    //   630	633	832	finally
    //   591	595	843	java/lang/Throwable
    //   322	326	848	java/lang/Throwable
  }
  
  /* Error */
  public static long decodeTakenTime(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 7
    //   6: aconst_null
    //   7: astore 4
    //   9: aconst_null
    //   10: astore 6
    //   12: new 891	java/io/FileInputStream
    //   15: dup
    //   16: aload_0
    //   17: invokespecial 892	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   20: astore_3
    //   21: aload_3
    //   22: invokestatic 969	com/oneplus/media/ImageUtils:decodeTakenTime	(Ljava/io/InputStream;)J
    //   25: lstore_1
    //   26: aload 7
    //   28: astore 4
    //   30: aload_3
    //   31: ifnull +11 -> 42
    //   34: aload_3
    //   35: invokevirtual 893	java/io/FileInputStream:close	()V
    //   38: aload 7
    //   40: astore 4
    //   42: aload 4
    //   44: ifnull +40 -> 84
    //   47: aload 4
    //   49: athrow
    //   50: astore_3
    //   51: ldc 32
    //   53: new 193	java/lang/StringBuilder
    //   56: dup
    //   57: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   60: ldc_w 971
    //   63: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: aload_0
    //   67: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   70: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   73: aload_3
    //   74: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   77: lconst_0
    //   78: lreturn
    //   79: astore 4
    //   81: goto -39 -> 42
    //   84: lload_1
    //   85: lreturn
    //   86: astore 4
    //   88: aload 6
    //   90: astore_3
    //   91: aload 4
    //   93: athrow
    //   94: astore 6
    //   96: aload 4
    //   98: astore 5
    //   100: aload 6
    //   102: astore 4
    //   104: aload 5
    //   106: astore 6
    //   108: aload_3
    //   109: ifnull +11 -> 120
    //   112: aload_3
    //   113: invokevirtual 893	java/io/FileInputStream:close	()V
    //   116: aload 5
    //   118: astore 6
    //   120: aload 6
    //   122: ifnull +29 -> 151
    //   125: aload 6
    //   127: athrow
    //   128: aload 5
    //   130: astore 6
    //   132: aload 5
    //   134: aload_3
    //   135: if_acmpeq -15 -> 120
    //   138: aload 5
    //   140: aload_3
    //   141: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   144: aload 5
    //   146: astore 6
    //   148: goto -28 -> 120
    //   151: aload 4
    //   153: athrow
    //   154: astore 6
    //   156: aload 4
    //   158: astore_3
    //   159: aload 6
    //   161: astore 4
    //   163: goto -59 -> 104
    //   166: astore 4
    //   168: goto -64 -> 104
    //   171: astore 4
    //   173: goto -82 -> 91
    //   176: astore_3
    //   177: goto -126 -> 51
    //   180: astore_3
    //   181: aload 5
    //   183: ifnonnull -55 -> 128
    //   186: aload_3
    //   187: astore 6
    //   189: goto -69 -> 120
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	192	0	paramString	String
    //   25	60	1	l	long
    //   20	15	3	localFileInputStream	java.io.FileInputStream
    //   50	24	3	localThrowable1	Throwable
    //   90	69	3	localObject1	Object
    //   176	1	3	localThrowable2	Throwable
    //   180	7	3	localThrowable3	Throwable
    //   7	41	4	localObject2	Object
    //   79	1	4	localThrowable4	Throwable
    //   86	11	4	localThrowable5	Throwable
    //   102	60	4	localObject3	Object
    //   166	1	4	localObject4	Object
    //   171	1	4	localThrowable6	Throwable
    //   1	181	5	localObject5	Object
    //   10	79	6	localObject6	Object
    //   94	7	6	localObject7	Object
    //   106	41	6	localObject8	Object
    //   154	6	6	localObject9	Object
    //   187	1	6	localObject10	Object
    //   4	35	7	localObject11	Object
    // Exception table:
    //   from	to	target	type
    //   47	50	50	java/lang/Throwable
    //   34	38	79	java/lang/Throwable
    //   12	21	86	java/lang/Throwable
    //   91	94	94	finally
    //   12	21	154	finally
    //   21	26	166	finally
    //   21	26	171	java/lang/Throwable
    //   125	128	176	java/lang/Throwable
    //   138	144	176	java/lang/Throwable
    //   151	154	176	java/lang/Throwable
    //   112	116	180	java/lang/Throwable
  }
  
  private static int exifOrientationToDegrees(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 3: 
    case 4: 
      return 180;
    case 6: 
    case 7: 
      return 90;
    }
    return 270;
  }
  
  private static boolean fillOuterPixels(Bitmap paramBitmap)
  {
    if ((paramBitmap == null) || (paramBitmap.isRecycled())) {
      return false;
    }
    int m = paramBitmap.getWidth();
    int n = paramBitmap.getHeight();
    if ((m < 3) || (n < 3)) {
      return false;
    }
    ByteBuffer localByteBuffer = lockPixels(paramBitmap);
    try
    {
      int i = -getandroid-graphics-Bitmap$ConfigSwitchesValues()[paramBitmap.getConfig().ordinal()];
      switch (i)
      {
      default: 
        return false;
      }
      for (i = 2;; i = 4)
      {
        int i1 = m * i;
        byte[] arrayOfByte1 = new byte[i1];
        byte[] arrayOfByte2 = new byte[i];
        localByteBuffer.position(i1);
        localByteBuffer.get(arrayOfByte1);
        localByteBuffer.position(0);
        localByteBuffer.put(arrayOfByte1);
        localByteBuffer.position((n - 2) * i1);
        localByteBuffer.get(arrayOfByte1);
        localByteBuffer.put(arrayOfByte1);
        int k = 0;
        int j = 0;
        while (k < n)
        {
          localByteBuffer.position(j + i);
          localByteBuffer.get(arrayOfByte2);
          localByteBuffer.position(j);
          localByteBuffer.put(arrayOfByte2);
          localByteBuffer.position((m - 2) * i + j);
          localByteBuffer.get(arrayOfByte2);
          localByteBuffer.put(arrayOfByte2);
          k += 1;
          j += i1;
        }
      }
      return true;
    }
    finally
    {
      unlockPixels(paramBitmap);
    }
  }
  
  /* Error */
  public static boolean findTiffHeader(InputStream paramInputStream, Ref<Long> paramRef, Ref<Boolean> paramRef1)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +13 -> 14
    //   4: ldc 32
    //   6: ldc_w 980
    //   9: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: iconst_0
    //   13: ireturn
    //   14: aload_1
    //   15: ifnonnull +17 -> 32
    //   18: aload_2
    //   19: ifnonnull +13 -> 32
    //   22: ldc 32
    //   24: ldc_w 982
    //   27: invokestatic 985	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   30: iconst_0
    //   31: ireturn
    //   32: lconst_0
    //   33: lstore 6
    //   35: aconst_null
    //   36: astore 17
    //   38: aconst_null
    //   39: astore 12
    //   41: aconst_null
    //   42: astore 15
    //   44: aconst_null
    //   45: astore 11
    //   47: aconst_null
    //   48: astore 16
    //   50: aconst_null
    //   51: astore 14
    //   53: aconst_null
    //   54: astore 13
    //   56: new 987	com/oneplus/io/BufferedInputStream
    //   59: dup
    //   60: aload_0
    //   61: iconst_0
    //   62: invokespecial 990	com/oneplus/io/BufferedInputStream:<init>	(Ljava/io/InputStream;Z)V
    //   65: astore 10
    //   67: new 623	com/oneplus/io/StreamState
    //   70: dup
    //   71: aload_0
    //   72: invokespecial 625	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   75: astore 11
    //   77: sipush 4099
    //   80: newarray <illegal type>
    //   82: astore_0
    //   83: lload 6
    //   85: lconst_0
    //   86: lcmp
    //   87: ifne +106 -> 193
    //   90: aload 10
    //   92: aload_0
    //   93: iconst_0
    //   94: sipush 4096
    //   97: invokevirtual 993	com/oneplus/io/BufferedInputStream:read	([BII)I
    //   100: istore 4
    //   102: iload 4
    //   104: istore_3
    //   105: goto +411 -> 516
    //   108: iload 5
    //   110: iload_3
    //   111: iconst_3
    //   112: isub
    //   113: if_icmpge +143 -> 256
    //   116: aload_0
    //   117: iload 5
    //   119: aload_2
    //   120: invokestatic 996	com/oneplus/media/ImageUtils:isTiffHeader	([BILcom/oneplus/base/Ref;)Z
    //   123: ifeq +118 -> 241
    //   126: aload_1
    //   127: ifnull +20 -> 147
    //   130: aload_1
    //   131: aload 11
    //   133: invokevirtual 863	com/oneplus/io/StreamState:getSavedStreamPosition	()J
    //   136: lload 8
    //   138: ladd
    //   139: invokestatic 269	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   142: invokeinterface 865 2 0
    //   147: aload 15
    //   149: astore_0
    //   150: aload 11
    //   152: ifnull +11 -> 163
    //   155: aload 11
    //   157: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   160: aload 15
    //   162: astore_0
    //   163: aload 10
    //   165: ifnull +8 -> 173
    //   168: aload 10
    //   170: invokevirtual 997	com/oneplus/io/BufferedInputStream:close	()V
    //   173: aload_0
    //   174: astore_1
    //   175: aload_1
    //   176: ifnull +63 -> 239
    //   179: aload_1
    //   180: athrow
    //   181: astore_0
    //   182: ldc 32
    //   184: ldc_w 999
    //   187: aload_0
    //   188: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   191: iconst_0
    //   192: ireturn
    //   193: aload 10
    //   195: aload_0
    //   196: iconst_3
    //   197: sipush 4096
    //   200: invokevirtual 993	com/oneplus/io/BufferedInputStream:read	([BII)I
    //   203: istore 4
    //   205: iload 4
    //   207: iconst_3
    //   208: iadd
    //   209: istore_3
    //   210: goto +306 -> 516
    //   213: astore_0
    //   214: goto -51 -> 163
    //   217: astore_2
    //   218: aload_2
    //   219: astore_1
    //   220: aload_0
    //   221: ifnull -46 -> 175
    //   224: aload_0
    //   225: aload_2
    //   226: if_acmpeq -53 -> 173
    //   229: aload_0
    //   230: aload_2
    //   231: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   234: aload_0
    //   235: astore_1
    //   236: goto -61 -> 175
    //   239: iconst_1
    //   240: ireturn
    //   241: lload 8
    //   243: lconst_1
    //   244: ladd
    //   245: lstore 8
    //   247: iload 5
    //   249: iconst_1
    //   250: iadd
    //   251: istore 5
    //   253: goto -145 -> 108
    //   256: iload 4
    //   258: sipush 4096
    //   261: if_icmpge +61 -> 322
    //   264: aload 17
    //   266: astore_0
    //   267: aload 11
    //   269: ifnull +11 -> 280
    //   272: aload 11
    //   274: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   277: aload 17
    //   279: astore_0
    //   280: aload 10
    //   282: ifnull +8 -> 290
    //   285: aload 10
    //   287: invokevirtual 997	com/oneplus/io/BufferedInputStream:close	()V
    //   290: aload_0
    //   291: astore_1
    //   292: aload_1
    //   293: ifnull +27 -> 320
    //   296: aload_1
    //   297: athrow
    //   298: astore_2
    //   299: aload_2
    //   300: astore_1
    //   301: aload_0
    //   302: ifnull -10 -> 292
    //   305: aload_0
    //   306: aload_2
    //   307: if_acmpeq -17 -> 290
    //   310: aload_0
    //   311: aload_2
    //   312: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   315: aload_0
    //   316: astore_1
    //   317: goto -25 -> 292
    //   320: iconst_0
    //   321: ireturn
    //   322: iconst_0
    //   323: istore 4
    //   325: lload 8
    //   327: lstore 6
    //   329: iload 4
    //   331: iconst_3
    //   332: if_icmpge -249 -> 83
    //   335: aload_0
    //   336: iload 4
    //   338: aload_0
    //   339: iload_3
    //   340: iconst_3
    //   341: iload 4
    //   343: isub
    //   344: isub
    //   345: baload
    //   346: bastore
    //   347: iload 4
    //   349: iconst_1
    //   350: iadd
    //   351: istore 4
    //   353: goto -28 -> 325
    //   356: astore_0
    //   357: aload 13
    //   359: astore 11
    //   361: aload 16
    //   363: astore_2
    //   364: aload_0
    //   365: athrow
    //   366: astore_1
    //   367: aload_0
    //   368: astore 10
    //   370: aload 11
    //   372: ifnull +8 -> 380
    //   375: aload 11
    //   377: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   380: aload 10
    //   382: astore_0
    //   383: aload_2
    //   384: ifnull +7 -> 391
    //   387: aload_2
    //   388: invokevirtual 997	com/oneplus/io/BufferedInputStream:close	()V
    //   391: aload_0
    //   392: astore_2
    //   393: aload_2
    //   394: ifnull +61 -> 455
    //   397: aload_2
    //   398: athrow
    //   399: astore 11
    //   401: aload 11
    //   403: astore_0
    //   404: aload 10
    //   406: ifnull -23 -> 383
    //   409: aload 10
    //   411: aload 11
    //   413: if_acmpeq -33 -> 380
    //   416: aload 10
    //   418: aload 11
    //   420: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   423: aload 10
    //   425: astore_0
    //   426: goto -43 -> 383
    //   429: astore 10
    //   431: aload 10
    //   433: astore_2
    //   434: aload_0
    //   435: ifnull -42 -> 393
    //   438: aload_0
    //   439: aload 10
    //   441: if_acmpeq -50 -> 391
    //   444: aload_0
    //   445: aload 10
    //   447: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   450: aload_0
    //   451: astore_2
    //   452: goto -59 -> 393
    //   455: aload_1
    //   456: athrow
    //   457: astore_1
    //   458: aload 11
    //   460: astore_2
    //   461: aload 14
    //   463: astore 11
    //   465: aload 12
    //   467: astore 10
    //   469: goto -99 -> 370
    //   472: astore_1
    //   473: aload 10
    //   475: astore_2
    //   476: aload 14
    //   478: astore 11
    //   480: aload 12
    //   482: astore 10
    //   484: goto -114 -> 370
    //   487: astore_1
    //   488: aload 10
    //   490: astore_2
    //   491: aload 12
    //   493: astore 10
    //   495: goto -125 -> 370
    //   498: astore_0
    //   499: aload 10
    //   501: astore_2
    //   502: aload 13
    //   504: astore 11
    //   506: goto -142 -> 364
    //   509: astore_0
    //   510: aload 10
    //   512: astore_2
    //   513: goto -149 -> 364
    //   516: iconst_0
    //   517: istore 5
    //   519: lload 6
    //   521: lstore 8
    //   523: goto -415 -> 108
    //   526: astore_0
    //   527: goto -247 -> 280
    //   530: astore_0
    //   531: goto -349 -> 182
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	534	0	paramInputStream	InputStream
    //   0	534	1	paramRef	Ref<Long>
    //   0	534	2	paramRef1	Ref<Boolean>
    //   104	241	3	i	int
    //   100	252	4	j	int
    //   108	410	5	k	int
    //   33	487	6	l1	long
    //   136	106	8	localObject1	Object
    //   245	277	8	l2	long
    //   65	359	10	localObject2	Object
    //   429	17	10	localThrowable1	Throwable
    //   467	44	10	localObject3	Object
    //   45	331	11	localObject4	Object
    //   399	60	11	localThrowable2	Throwable
    //   463	42	11	localObject5	Object
    //   39	453	12	localObject6	Object
    //   54	449	13	localObject7	Object
    //   51	426	14	localObject8	Object
    //   42	119	15	localObject9	Object
    //   48	314	16	localObject10	Object
    //   36	242	17	localObject11	Object
    // Exception table:
    //   from	to	target	type
    //   179	181	181	java/lang/Throwable
    //   229	234	181	java/lang/Throwable
    //   296	298	181	java/lang/Throwable
    //   310	315	181	java/lang/Throwable
    //   155	160	213	java/lang/Throwable
    //   168	173	217	java/lang/Throwable
    //   285	290	298	java/lang/Throwable
    //   56	67	356	java/lang/Throwable
    //   364	366	366	finally
    //   375	380	399	java/lang/Throwable
    //   387	391	429	java/lang/Throwable
    //   56	67	457	finally
    //   67	77	472	finally
    //   77	83	487	finally
    //   90	102	487	finally
    //   116	126	487	finally
    //   130	147	487	finally
    //   193	205	487	finally
    //   67	77	498	java/lang/Throwable
    //   77	83	509	java/lang/Throwable
    //   90	102	509	java/lang/Throwable
    //   116	126	509	java/lang/Throwable
    //   130	147	509	java/lang/Throwable
    //   193	205	509	java/lang/Throwable
    //   272	277	526	java/lang/Throwable
    //   397	399	530	java/lang/Throwable
    //   416	423	530	java/lang/Throwable
    //   444	450	530	java/lang/Throwable
    //   455	457	530	java/lang/Throwable
  }
  
  public static boolean findTiffHeader(SeekableByteChannel paramSeekableByteChannel, Ref<Long> paramRef, Ref<Boolean> paramRef1)
  {
    if (paramSeekableByteChannel == null)
    {
      Log.e("ImageUtils", "findTiffHeader() - No channel");
      return false;
    }
    if ((paramRef == null) && (paramRef1 == null))
    {
      Log.w("ImageUtils", "findTiffHeader() - No reference to receive result");
      return false;
    }
    long l4 = 0L;
    l2 = -1L;
    long l1 = l2;
    try
    {
      long l3 = paramSeekableByteChannel.position();
      l1 = l3;
      l2 = l3;
      byte[] arrayOfByte = new byte['က'];
      l1 = l3;
      l2 = l3;
      ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
      if (l4 == 0L)
      {
        l1 = l3;
        l2 = l3;
        i = paramSeekableByteChannel.read(localByteBuffer);
        if (i > 0) {
          break label206;
        }
        if (l3 >= 0L) {}
        try
        {
          paramSeekableByteChannel.position(l3);
          return false;
        }
        catch (Throwable paramSeekableByteChannel)
        {
          Log.e("ImageUtils", "findTiffHeader() - Fail to restore channel position", paramSeekableByteChannel);
          return false;
        }
      }
      l1 = l3;
      l2 = l3;
      localByteBuffer.position(3);
      l1 = l3;
      l2 = l3;
      int i = paramSeekableByteChannel.read(localByteBuffer);
      if (i <= 0)
      {
        if (l3 >= 0L) {}
        try
        {
          paramSeekableByteChannel.position(l3);
          return false;
        }
        catch (Throwable paramSeekableByteChannel)
        {
          Log.e("ImageUtils", "findTiffHeader() - Fail to restore channel position", paramSeekableByteChannel);
          return false;
        }
      }
      label206:
      l1 = l3;
      l2 = l3;
      int j = localByteBuffer.position();
      i = 0;
      long l5 = l4;
      while (i < j - 3)
      {
        l1 = l3;
        l2 = l3;
        if (isTiffHeader(arrayOfByte, i, paramRef1))
        {
          if (paramRef != null)
          {
            l1 = l3;
            l2 = l3;
            paramRef.set(Long.valueOf(l3 + l5));
          }
          if (l3 >= 0L) {}
          try
          {
            paramSeekableByteChannel.position(l3);
            return true;
          }
          catch (Throwable paramSeekableByteChannel)
          {
            Log.e("ImageUtils", "findTiffHeader() - Fail to restore channel position", paramSeekableByteChannel);
            return true;
          }
        }
        l5 += 1L;
        i += 1;
      }
      i = 0;
      for (;;)
      {
        l4 = l5;
        if (i >= 3) {
          break;
        }
        arrayOfByte[i] = arrayOfByte[(j - (3 - i))];
        i += 1;
      }
      try
      {
        paramSeekableByteChannel.position(l2);
        throw paramRef;
      }
      catch (Throwable paramSeekableByteChannel)
      {
        for (;;)
        {
          Log.e("ImageUtils", "findTiffHeader() - Fail to restore channel position", paramSeekableByteChannel);
        }
      }
    }
    catch (Throwable paramRef)
    {
      l2 = l1;
      Log.e("ImageUtils", "findTiffHeader() - Unknown error", paramRef);
      if (l1 >= 0L) {}
      try
      {
        paramSeekableByteChannel.position(l1);
        return false;
      }
      catch (Throwable paramSeekableByteChannel)
      {
        Log.e("ImageUtils", "findTiffHeader() - Fail to restore channel position", paramSeekableByteChannel);
        return false;
      }
    }
    finally
    {
      if (l2 < 0L) {}
    }
  }
  
  /* Error */
  public static String getMimeType(InputStream paramInputStream)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +13 -> 14
    //   4: ldc 32
    //   6: ldc_w 1026
    //   9: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: aconst_null
    //   13: areturn
    //   14: aconst_null
    //   15: astore 4
    //   17: aconst_null
    //   18: astore_3
    //   19: new 623	com/oneplus/io/StreamState
    //   22: dup
    //   23: aload_0
    //   24: invokespecial 625	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   27: astore_2
    //   28: bipush 8
    //   30: newarray <illegal type>
    //   32: astore_3
    //   33: aload_0
    //   34: aload_3
    //   35: invokevirtual 1029	java/io/InputStream:read	([B)I
    //   38: istore_1
    //   39: iload_1
    //   40: bipush 8
    //   42: if_icmpge +37 -> 79
    //   45: aload_2
    //   46: ifnull +7 -> 53
    //   49: aload_2
    //   50: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   53: aconst_null
    //   54: astore_0
    //   55: aload_0
    //   56: ifnull +21 -> 77
    //   59: aload_0
    //   60: athrow
    //   61: astore_0
    //   62: ldc 32
    //   64: ldc_w 1031
    //   67: aload_0
    //   68: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   71: aconst_null
    //   72: areturn
    //   73: astore_0
    //   74: goto -19 -> 55
    //   77: aconst_null
    //   78: areturn
    //   79: aload_3
    //   80: invokestatic 1034	com/oneplus/media/ImageUtils:isJfifHeader	([B)Z
    //   83: ifeq +27 -> 110
    //   86: aload_2
    //   87: ifnull +7 -> 94
    //   90: aload_2
    //   91: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   94: aconst_null
    //   95: astore_0
    //   96: aload_0
    //   97: ifnull +9 -> 106
    //   100: aload_0
    //   101: athrow
    //   102: astore_0
    //   103: goto -7 -> 96
    //   106: ldc_w 1036
    //   109: areturn
    //   110: aload_3
    //   111: invokestatic 1038	com/oneplus/media/ImageUtils:isPngHeader	([B)Z
    //   114: ifeq +27 -> 141
    //   117: aload_2
    //   118: ifnull +7 -> 125
    //   121: aload_2
    //   122: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   125: aconst_null
    //   126: astore_0
    //   127: aload_0
    //   128: ifnull +9 -> 137
    //   131: aload_0
    //   132: athrow
    //   133: astore_0
    //   134: goto -7 -> 127
    //   137: ldc_w 1040
    //   140: areturn
    //   141: aload_3
    //   142: invokestatic 1042	com/oneplus/media/ImageUtils:isGifHeader	([B)Z
    //   145: ifeq +27 -> 172
    //   148: aload_2
    //   149: ifnull +7 -> 156
    //   152: aload_2
    //   153: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   156: aconst_null
    //   157: astore_0
    //   158: aload_0
    //   159: ifnull +9 -> 168
    //   162: aload_0
    //   163: athrow
    //   164: astore_0
    //   165: goto -7 -> 158
    //   168: ldc_w 1044
    //   171: areturn
    //   172: aload_2
    //   173: ifnull +7 -> 180
    //   176: aload_2
    //   177: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   180: aconst_null
    //   181: astore_0
    //   182: aload_0
    //   183: ifnull +9 -> 192
    //   186: aload_0
    //   187: athrow
    //   188: astore_0
    //   189: goto -7 -> 182
    //   192: aconst_null
    //   193: areturn
    //   194: astore_0
    //   195: aload_0
    //   196: athrow
    //   197: astore_2
    //   198: aload_0
    //   199: astore 4
    //   201: aload_3
    //   202: ifnull +10 -> 212
    //   205: aload_3
    //   206: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   209: aload_0
    //   210: astore 4
    //   212: aload 4
    //   214: ifnull +25 -> 239
    //   217: aload 4
    //   219: athrow
    //   220: aload_0
    //   221: astore 4
    //   223: aload_0
    //   224: aload_3
    //   225: if_acmpeq -13 -> 212
    //   228: aload_0
    //   229: aload_3
    //   230: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   233: aload_0
    //   234: astore 4
    //   236: goto -24 -> 212
    //   239: aload_2
    //   240: athrow
    //   241: astore_2
    //   242: aconst_null
    //   243: astore_0
    //   244: aload 4
    //   246: astore_3
    //   247: goto -49 -> 198
    //   250: astore 4
    //   252: aconst_null
    //   253: astore_0
    //   254: aload_2
    //   255: astore_3
    //   256: aload 4
    //   258: astore_2
    //   259: goto -61 -> 198
    //   262: astore_0
    //   263: aload_2
    //   264: astore_3
    //   265: goto -70 -> 195
    //   268: astore_0
    //   269: goto -207 -> 62
    //   272: astore_3
    //   273: aload_0
    //   274: ifnonnull -54 -> 220
    //   277: aload_3
    //   278: astore 4
    //   280: goto -68 -> 212
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	283	0	paramInputStream	InputStream
    //   38	5	1	i	int
    //   27	150	2	localStreamState	com.oneplus.io.StreamState
    //   197	43	2	localObject1	Object
    //   241	14	2	localObject2	Object
    //   258	6	2	localObject3	Object
    //   18	247	3	localObject4	Object
    //   272	6	3	localThrowable	Throwable
    //   15	230	4	localInputStream	InputStream
    //   250	7	4	localObject5	Object
    //   278	1	4	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   59	61	61	java/lang/Throwable
    //   100	102	61	java/lang/Throwable
    //   131	133	61	java/lang/Throwable
    //   162	164	61	java/lang/Throwable
    //   186	188	61	java/lang/Throwable
    //   49	53	73	java/lang/Throwable
    //   90	94	102	java/lang/Throwable
    //   121	125	133	java/lang/Throwable
    //   152	156	164	java/lang/Throwable
    //   176	180	188	java/lang/Throwable
    //   19	28	194	java/lang/Throwable
    //   195	197	197	finally
    //   19	28	241	finally
    //   28	39	250	finally
    //   79	86	250	finally
    //   110	117	250	finally
    //   141	148	250	finally
    //   28	39	262	java/lang/Throwable
    //   79	86	262	java/lang/Throwable
    //   110	117	262	java/lang/Throwable
    //   141	148	262	java/lang/Throwable
    //   217	220	268	java/lang/Throwable
    //   228	233	268	java/lang/Throwable
    //   239	241	268	java/lang/Throwable
    //   205	209	272	java/lang/Throwable
  }
  
  /* Error */
  public static String getMimeType(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: new 891	java/io/FileInputStream
    //   7: dup
    //   8: aload_0
    //   9: invokespecial 892	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   12: astore_1
    //   13: aload_1
    //   14: invokestatic 1046	com/oneplus/media/ImageUtils:getMimeType	(Ljava/io/InputStream;)Ljava/lang/String;
    //   17: astore_2
    //   18: aload_1
    //   19: ifnull +7 -> 26
    //   22: aload_1
    //   23: invokevirtual 893	java/io/FileInputStream:close	()V
    //   26: aconst_null
    //   27: astore_1
    //   28: aload_1
    //   29: ifnull +44 -> 73
    //   32: aload_1
    //   33: athrow
    //   34: astore_1
    //   35: ldc 32
    //   37: new 193	java/lang/StringBuilder
    //   40: dup
    //   41: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   44: ldc_w 1048
    //   47: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   50: aload_0
    //   51: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: ldc_w 495
    //   57: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   63: aload_1
    //   64: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   67: aconst_null
    //   68: areturn
    //   69: astore_1
    //   70: goto -42 -> 28
    //   73: aload_2
    //   74: areturn
    //   75: astore_1
    //   76: aload_1
    //   77: athrow
    //   78: astore_2
    //   79: aload_1
    //   80: astore 4
    //   82: aload_3
    //   83: ifnull +10 -> 93
    //   86: aload_3
    //   87: invokevirtual 893	java/io/FileInputStream:close	()V
    //   90: aload_1
    //   91: astore 4
    //   93: aload 4
    //   95: ifnull +25 -> 120
    //   98: aload 4
    //   100: athrow
    //   101: aload_1
    //   102: astore 4
    //   104: aload_1
    //   105: aload_3
    //   106: if_acmpeq -13 -> 93
    //   109: aload_1
    //   110: aload_3
    //   111: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   114: aload_1
    //   115: astore 4
    //   117: goto -24 -> 93
    //   120: aload_2
    //   121: athrow
    //   122: astore 4
    //   124: aconst_null
    //   125: astore_1
    //   126: aload_2
    //   127: astore_3
    //   128: aload 4
    //   130: astore_2
    //   131: goto -52 -> 79
    //   134: astore_2
    //   135: aconst_null
    //   136: astore 4
    //   138: aload_1
    //   139: astore_3
    //   140: aload 4
    //   142: astore_1
    //   143: goto -64 -> 79
    //   146: astore_2
    //   147: aload_1
    //   148: astore_3
    //   149: aload_2
    //   150: astore_1
    //   151: goto -75 -> 76
    //   154: astore_1
    //   155: goto -120 -> 35
    //   158: astore_3
    //   159: aload_1
    //   160: ifnonnull -59 -> 101
    //   163: aload_3
    //   164: astore 4
    //   166: goto -73 -> 93
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	169	0	paramString	String
    //   12	21	1	localFileInputStream	java.io.FileInputStream
    //   34	30	1	localThrowable1	Throwable
    //   69	1	1	localThrowable2	Throwable
    //   75	40	1	localThrowable3	Throwable
    //   125	26	1	localObject1	Object
    //   154	6	1	localThrowable4	Throwable
    //   1	73	2	str	String
    //   78	49	2	localObject2	Object
    //   130	1	2	localObject3	Object
    //   134	1	2	localObject4	Object
    //   146	4	2	localThrowable5	Throwable
    //   3	146	3	localObject5	Object
    //   158	6	3	localThrowable6	Throwable
    //   80	36	4	localThrowable7	Throwable
    //   122	7	4	localObject6	Object
    //   136	29	4	localThrowable8	Throwable
    // Exception table:
    //   from	to	target	type
    //   32	34	34	java/lang/Throwable
    //   22	26	69	java/lang/Throwable
    //   4	13	75	java/lang/Throwable
    //   76	78	78	finally
    //   4	13	122	finally
    //   13	18	134	finally
    //   13	18	146	java/lang/Throwable
    //   98	101	154	java/lang/Throwable
    //   109	114	154	java/lang/Throwable
    //   120	122	154	java/lang/Throwable
    //   86	90	158	java/lang/Throwable
  }
  
  /* Error */
  public static boolean isGifHeader(InputStream paramInputStream)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 6
    //   8: aconst_null
    //   9: astore 5
    //   11: new 623	com/oneplus/io/StreamState
    //   14: dup
    //   15: aload_0
    //   16: iconst_3
    //   17: invokespecial 1051	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;I)V
    //   20: astore_2
    //   21: iconst_3
    //   22: newarray <illegal type>
    //   24: astore 5
    //   26: aload_0
    //   27: aload 5
    //   29: invokevirtual 1029	java/io/InputStream:read	([B)I
    //   32: iconst_3
    //   33: if_icmpne +41 -> 74
    //   36: aload 5
    //   38: invokestatic 1042	com/oneplus/media/ImageUtils:isGifHeader	([B)Z
    //   41: istore_1
    //   42: aload 4
    //   44: astore_0
    //   45: aload_2
    //   46: ifnull +10 -> 56
    //   49: aload_2
    //   50: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   53: aload 4
    //   55: astore_0
    //   56: aload_0
    //   57: ifnull +26 -> 83
    //   60: aload_0
    //   61: athrow
    //   62: astore_0
    //   63: ldc 32
    //   65: ldc_w 1053
    //   68: aload_0
    //   69: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   72: iconst_0
    //   73: ireturn
    //   74: iconst_0
    //   75: istore_1
    //   76: goto -34 -> 42
    //   79: astore_0
    //   80: goto -24 -> 56
    //   83: iload_1
    //   84: ireturn
    //   85: astore_2
    //   86: aload 5
    //   88: astore_0
    //   89: aload_2
    //   90: athrow
    //   91: astore 4
    //   93: aload_2
    //   94: astore_3
    //   95: aload 4
    //   97: astore_2
    //   98: aload_3
    //   99: astore 4
    //   101: aload_0
    //   102: ifnull +10 -> 112
    //   105: aload_0
    //   106: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   109: aload_3
    //   110: astore 4
    //   112: aload 4
    //   114: ifnull +25 -> 139
    //   117: aload 4
    //   119: athrow
    //   120: aload_3
    //   121: astore 4
    //   123: aload_3
    //   124: aload_0
    //   125: if_acmpeq -13 -> 112
    //   128: aload_3
    //   129: aload_0
    //   130: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   133: aload_3
    //   134: astore 4
    //   136: goto -24 -> 112
    //   139: aload_2
    //   140: athrow
    //   141: astore_2
    //   142: aload 6
    //   144: astore_0
    //   145: goto -47 -> 98
    //   148: astore 4
    //   150: aload_2
    //   151: astore_0
    //   152: aload 4
    //   154: astore_2
    //   155: goto -57 -> 98
    //   158: astore_3
    //   159: aload_2
    //   160: astore_0
    //   161: aload_3
    //   162: astore_2
    //   163: goto -74 -> 89
    //   166: astore_0
    //   167: goto -104 -> 63
    //   170: astore_0
    //   171: aload_3
    //   172: ifnonnull -52 -> 120
    //   175: aload_0
    //   176: astore 4
    //   178: goto -66 -> 112
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	181	0	paramInputStream	InputStream
    //   41	43	1	bool	boolean
    //   20	30	2	localStreamState	com.oneplus.io.StreamState
    //   85	9	2	localThrowable1	Throwable
    //   97	43	2	localObject1	Object
    //   141	10	2	localObject2	Object
    //   154	9	2	localObject3	Object
    //   1	133	3	localObject4	Object
    //   158	14	3	localThrowable2	Throwable
    //   3	51	4	localObject5	Object
    //   91	5	4	localObject6	Object
    //   99	36	4	localObject7	Object
    //   148	5	4	localObject8	Object
    //   176	1	4	localInputStream	InputStream
    //   9	78	5	arrayOfByte	byte[]
    //   6	137	6	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   60	62	62	java/lang/Throwable
    //   49	53	79	java/lang/Throwable
    //   11	21	85	java/lang/Throwable
    //   89	91	91	finally
    //   11	21	141	finally
    //   21	42	148	finally
    //   21	42	158	java/lang/Throwable
    //   117	120	166	java/lang/Throwable
    //   128	133	166	java/lang/Throwable
    //   139	141	166	java/lang/Throwable
    //   105	109	170	java/lang/Throwable
  }
  
  public static boolean isGifHeader(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length < 3)) {
      return false;
    }
    return (paramArrayOfByte[0] == 71) && (paramArrayOfByte[1] == 73) && (paramArrayOfByte[2] == 70);
  }
  
  /* Error */
  public static boolean isJfifHeader(InputStream paramInputStream)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 6
    //   8: aconst_null
    //   9: astore 5
    //   11: new 623	com/oneplus/io/StreamState
    //   14: dup
    //   15: aload_0
    //   16: iconst_2
    //   17: invokespecial 1051	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;I)V
    //   20: astore_2
    //   21: iconst_2
    //   22: newarray <illegal type>
    //   24: astore 5
    //   26: aload_0
    //   27: aload 5
    //   29: invokevirtual 1029	java/io/InputStream:read	([B)I
    //   32: iconst_2
    //   33: if_icmpne +41 -> 74
    //   36: aload 5
    //   38: invokestatic 1034	com/oneplus/media/ImageUtils:isJfifHeader	([B)Z
    //   41: istore_1
    //   42: aload 4
    //   44: astore_0
    //   45: aload_2
    //   46: ifnull +10 -> 56
    //   49: aload_2
    //   50: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   53: aload 4
    //   55: astore_0
    //   56: aload_0
    //   57: ifnull +26 -> 83
    //   60: aload_0
    //   61: athrow
    //   62: astore_0
    //   63: ldc 32
    //   65: ldc_w 1055
    //   68: aload_0
    //   69: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   72: iconst_0
    //   73: ireturn
    //   74: iconst_0
    //   75: istore_1
    //   76: goto -34 -> 42
    //   79: astore_0
    //   80: goto -24 -> 56
    //   83: iload_1
    //   84: ireturn
    //   85: astore_2
    //   86: aload 5
    //   88: astore_0
    //   89: aload_2
    //   90: athrow
    //   91: astore 4
    //   93: aload_2
    //   94: astore_3
    //   95: aload 4
    //   97: astore_2
    //   98: aload_3
    //   99: astore 4
    //   101: aload_0
    //   102: ifnull +10 -> 112
    //   105: aload_0
    //   106: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   109: aload_3
    //   110: astore 4
    //   112: aload 4
    //   114: ifnull +25 -> 139
    //   117: aload 4
    //   119: athrow
    //   120: aload_3
    //   121: astore 4
    //   123: aload_3
    //   124: aload_0
    //   125: if_acmpeq -13 -> 112
    //   128: aload_3
    //   129: aload_0
    //   130: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   133: aload_3
    //   134: astore 4
    //   136: goto -24 -> 112
    //   139: aload_2
    //   140: athrow
    //   141: astore_2
    //   142: aload 6
    //   144: astore_0
    //   145: goto -47 -> 98
    //   148: astore 4
    //   150: aload_2
    //   151: astore_0
    //   152: aload 4
    //   154: astore_2
    //   155: goto -57 -> 98
    //   158: astore_3
    //   159: aload_2
    //   160: astore_0
    //   161: aload_3
    //   162: astore_2
    //   163: goto -74 -> 89
    //   166: astore_0
    //   167: goto -104 -> 63
    //   170: astore_0
    //   171: aload_3
    //   172: ifnonnull -52 -> 120
    //   175: aload_0
    //   176: astore 4
    //   178: goto -66 -> 112
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	181	0	paramInputStream	InputStream
    //   41	43	1	bool	boolean
    //   20	30	2	localStreamState	com.oneplus.io.StreamState
    //   85	9	2	localThrowable1	Throwable
    //   97	43	2	localObject1	Object
    //   141	10	2	localObject2	Object
    //   154	9	2	localObject3	Object
    //   1	133	3	localObject4	Object
    //   158	14	3	localThrowable2	Throwable
    //   3	51	4	localObject5	Object
    //   91	5	4	localObject6	Object
    //   99	36	4	localObject7	Object
    //   148	5	4	localObject8	Object
    //   176	1	4	localInputStream	InputStream
    //   9	78	5	arrayOfByte	byte[]
    //   6	137	6	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   60	62	62	java/lang/Throwable
    //   49	53	79	java/lang/Throwable
    //   11	21	85	java/lang/Throwable
    //   89	91	91	finally
    //   11	21	141	finally
    //   21	42	148	finally
    //   21	42	158	java/lang/Throwable
    //   117	120	166	java/lang/Throwable
    //   128	133	166	java/lang/Throwable
    //   139	141	166	java/lang/Throwable
    //   105	109	170	java/lang/Throwable
  }
  
  public static boolean isJfifHeader(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length < 2)) {
      return false;
    }
    return ((paramArrayOfByte[0] & 0xFF) == 255) && ((paramArrayOfByte[1] & 0xFF) == 216);
  }
  
  /* Error */
  public static boolean isPngHeader(InputStream paramInputStream)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 6
    //   8: aconst_null
    //   9: astore 5
    //   11: new 623	com/oneplus/io/StreamState
    //   14: dup
    //   15: aload_0
    //   16: bipush 8
    //   18: invokespecial 1051	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;I)V
    //   21: astore_2
    //   22: bipush 8
    //   24: newarray <illegal type>
    //   26: astore 5
    //   28: aload_0
    //   29: aload 5
    //   31: invokevirtual 1029	java/io/InputStream:read	([B)I
    //   34: bipush 8
    //   36: if_icmpne +41 -> 77
    //   39: aload 5
    //   41: invokestatic 1038	com/oneplus/media/ImageUtils:isPngHeader	([B)Z
    //   44: istore_1
    //   45: aload 4
    //   47: astore_0
    //   48: aload_2
    //   49: ifnull +10 -> 59
    //   52: aload_2
    //   53: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   56: aload 4
    //   58: astore_0
    //   59: aload_0
    //   60: ifnull +26 -> 86
    //   63: aload_0
    //   64: athrow
    //   65: astore_0
    //   66: ldc 32
    //   68: ldc_w 1057
    //   71: aload_0
    //   72: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   75: iconst_0
    //   76: ireturn
    //   77: iconst_0
    //   78: istore_1
    //   79: goto -34 -> 45
    //   82: astore_0
    //   83: goto -24 -> 59
    //   86: iload_1
    //   87: ireturn
    //   88: astore_2
    //   89: aload 5
    //   91: astore_0
    //   92: aload_2
    //   93: athrow
    //   94: astore 4
    //   96: aload_2
    //   97: astore_3
    //   98: aload 4
    //   100: astore_2
    //   101: aload_3
    //   102: astore 4
    //   104: aload_0
    //   105: ifnull +10 -> 115
    //   108: aload_0
    //   109: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   112: aload_3
    //   113: astore 4
    //   115: aload 4
    //   117: ifnull +25 -> 142
    //   120: aload 4
    //   122: athrow
    //   123: aload_3
    //   124: astore 4
    //   126: aload_3
    //   127: aload_0
    //   128: if_acmpeq -13 -> 115
    //   131: aload_3
    //   132: aload_0
    //   133: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   136: aload_3
    //   137: astore 4
    //   139: goto -24 -> 115
    //   142: aload_2
    //   143: athrow
    //   144: astore_2
    //   145: aload 6
    //   147: astore_0
    //   148: goto -47 -> 101
    //   151: astore 4
    //   153: aload_2
    //   154: astore_0
    //   155: aload 4
    //   157: astore_2
    //   158: goto -57 -> 101
    //   161: astore_3
    //   162: aload_2
    //   163: astore_0
    //   164: aload_3
    //   165: astore_2
    //   166: goto -74 -> 92
    //   169: astore_0
    //   170: goto -104 -> 66
    //   173: astore_0
    //   174: aload_3
    //   175: ifnonnull -52 -> 123
    //   178: aload_0
    //   179: astore 4
    //   181: goto -66 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	184	0	paramInputStream	InputStream
    //   44	43	1	bool	boolean
    //   21	32	2	localStreamState	com.oneplus.io.StreamState
    //   88	9	2	localThrowable1	Throwable
    //   100	43	2	localObject1	Object
    //   144	10	2	localObject2	Object
    //   157	9	2	localObject3	Object
    //   1	136	3	localObject4	Object
    //   161	14	3	localThrowable2	Throwable
    //   3	54	4	localObject5	Object
    //   94	5	4	localObject6	Object
    //   102	36	4	localObject7	Object
    //   151	5	4	localObject8	Object
    //   179	1	4	localInputStream	InputStream
    //   9	81	5	arrayOfByte	byte[]
    //   6	140	6	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   63	65	65	java/lang/Throwable
    //   52	56	82	java/lang/Throwable
    //   11	22	88	java/lang/Throwable
    //   92	94	94	finally
    //   11	22	144	finally
    //   22	45	151	finally
    //   22	45	161	java/lang/Throwable
    //   120	123	169	java/lang/Throwable
    //   131	136	169	java/lang/Throwable
    //   142	144	169	java/lang/Throwable
    //   108	112	173	java/lang/Throwable
  }
  
  public static boolean isPngHeader(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length < 8)) {
      return false;
    }
    if (((paramArrayOfByte[0] & 0xFF) == 137) && (paramArrayOfByte[1] == 80) && (paramArrayOfByte[2] == 78) && (paramArrayOfByte[3] == 71) && (paramArrayOfByte[4] == 13) && (paramArrayOfByte[5] == 10) && (paramArrayOfByte[6] == 26)) {
      return paramArrayOfByte[7] == 10;
    }
    return false;
  }
  
  /* Error */
  public static boolean isTiffHeader(InputStream paramInputStream, Ref<Boolean> paramRef)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: aconst_null
    //   7: astore 7
    //   9: aconst_null
    //   10: astore 6
    //   12: new 623	com/oneplus/io/StreamState
    //   15: dup
    //   16: aload_0
    //   17: iconst_4
    //   18: invokespecial 1051	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;I)V
    //   21: astore_3
    //   22: iconst_4
    //   23: newarray <illegal type>
    //   25: astore 6
    //   27: aload_0
    //   28: aload 6
    //   30: invokevirtual 1029	java/io/InputStream:read	([B)I
    //   33: iconst_4
    //   34: if_icmpne +42 -> 76
    //   37: aload 6
    //   39: aload_1
    //   40: invokestatic 1060	com/oneplus/media/ImageUtils:isTiffHeader	([BLcom/oneplus/base/Ref;)Z
    //   43: istore_2
    //   44: aload 5
    //   46: astore_0
    //   47: aload_3
    //   48: ifnull +10 -> 58
    //   51: aload_3
    //   52: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   55: aload 5
    //   57: astore_0
    //   58: aload_0
    //   59: ifnull +26 -> 85
    //   62: aload_0
    //   63: athrow
    //   64: astore_0
    //   65: ldc 32
    //   67: ldc_w 1062
    //   70: aload_0
    //   71: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   74: iconst_0
    //   75: ireturn
    //   76: iconst_0
    //   77: istore_2
    //   78: goto -34 -> 44
    //   81: astore_0
    //   82: goto -24 -> 58
    //   85: iload_2
    //   86: ireturn
    //   87: astore_1
    //   88: aload 6
    //   90: astore_0
    //   91: aload_1
    //   92: athrow
    //   93: astore 4
    //   95: aload_1
    //   96: astore_3
    //   97: aload 4
    //   99: astore_1
    //   100: aload_3
    //   101: astore 4
    //   103: aload_0
    //   104: ifnull +10 -> 114
    //   107: aload_0
    //   108: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   111: aload_3
    //   112: astore 4
    //   114: aload 4
    //   116: ifnull +25 -> 141
    //   119: aload 4
    //   121: athrow
    //   122: aload_3
    //   123: astore 4
    //   125: aload_3
    //   126: aload_0
    //   127: if_acmpeq -13 -> 114
    //   130: aload_3
    //   131: aload_0
    //   132: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   135: aload_3
    //   136: astore 4
    //   138: goto -24 -> 114
    //   141: aload_1
    //   142: athrow
    //   143: astore_1
    //   144: aload 7
    //   146: astore_0
    //   147: aload 4
    //   149: astore_3
    //   150: goto -50 -> 100
    //   153: astore_1
    //   154: aload_3
    //   155: astore_0
    //   156: aload 4
    //   158: astore_3
    //   159: goto -59 -> 100
    //   162: astore_1
    //   163: aload_3
    //   164: astore_0
    //   165: goto -74 -> 91
    //   168: astore_0
    //   169: goto -104 -> 65
    //   172: astore_0
    //   173: aload_3
    //   174: ifnonnull -52 -> 122
    //   177: aload_0
    //   178: astore 4
    //   180: goto -66 -> 114
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	183	0	paramInputStream	InputStream
    //   0	183	1	paramRef	Ref<Boolean>
    //   43	43	2	bool	boolean
    //   21	153	3	localObject1	Object
    //   1	1	4	localObject2	Object
    //   93	5	4	localObject3	Object
    //   101	78	4	localObject4	Object
    //   4	52	5	localObject5	Object
    //   10	79	6	arrayOfByte	byte[]
    //   7	138	7	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   62	64	64	java/lang/Throwable
    //   51	55	81	java/lang/Throwable
    //   12	22	87	java/lang/Throwable
    //   91	93	93	finally
    //   12	22	143	finally
    //   22	44	153	finally
    //   22	44	162	java/lang/Throwable
    //   119	122	168	java/lang/Throwable
    //   130	135	168	java/lang/Throwable
    //   141	143	168	java/lang/Throwable
    //   107	111	172	java/lang/Throwable
  }
  
  public static boolean isTiffHeader(byte[] paramArrayOfByte, int paramInt, Ref<Boolean> paramRef)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length < 4)) {
      return false;
    }
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    paramInt = i;
    if (i > paramArrayOfByte.length - 4) {
      paramInt = paramArrayOfByte.length - 4;
    }
    if (((paramArrayOfByte[paramInt] == 77) || (paramArrayOfByte[paramInt] == 73)) && (paramArrayOfByte[(paramInt + 1)] == paramArrayOfByte[paramInt])) {
      if (paramArrayOfByte[paramInt] == 77)
      {
        if ((paramArrayOfByte[(paramInt + 2)] == 0) && (paramArrayOfByte[(paramInt + 3)] == 42))
        {
          if (paramRef != null) {
            paramRef.set(Boolean.valueOf(false));
          }
          return true;
        }
      }
      else if ((paramArrayOfByte[(paramInt + 2)] == 42) && (paramArrayOfByte[(paramInt + 3)] == 0))
      {
        if (paramRef != null) {
          paramRef.set(Boolean.valueOf(true));
        }
        return true;
      }
    }
    return false;
  }
  
  public static boolean isTiffHeader(byte[] paramArrayOfByte, Ref<Boolean> paramRef)
  {
    return isTiffHeader(paramArrayOfByte, 0, paramRef);
  }
  
  public static ByteBuffer lockPixels(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      return null;
    }
    if (NativeLibrary.load()) {
      return nativeLockPixels(paramBitmap);
    }
    return null;
  }
  
  private static native ByteBuffer nativeLockPixels(Bitmap paramBitmap);
  
  private static native void nativeUnlockPixels(Bitmap paramBitmap);
  
  /* Error */
  public static EncodedImage parseImage(android.content.Context paramContext, android.net.Uri paramUri)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 6
    //   6: aload_0
    //   7: ifnull +7 -> 14
    //   10: aload_1
    //   11: ifnonnull +5 -> 16
    //   14: aconst_null
    //   15: areturn
    //   16: aconst_null
    //   17: astore 5
    //   19: aconst_null
    //   20: astore_3
    //   21: aconst_null
    //   22: astore_2
    //   23: aload_0
    //   24: invokevirtual 1077	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   27: aload_1
    //   28: invokevirtual 1083	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   31: astore_0
    //   32: aload_0
    //   33: astore_2
    //   34: aload_0
    //   35: astore_3
    //   36: aload_0
    //   37: invokestatic 1086	com/oneplus/media/ImageUtils:parseImage	(Ljava/io/InputStream;)Lcom/oneplus/media/EncodedImage;
    //   40: astore 7
    //   42: aload 7
    //   44: astore_2
    //   45: aload 6
    //   47: astore_3
    //   48: aload_0
    //   49: ifnull +10 -> 59
    //   52: aload_0
    //   53: invokevirtual 758	java/io/InputStream:close	()V
    //   56: aload 6
    //   58: astore_3
    //   59: aload_2
    //   60: astore_0
    //   61: aload_3
    //   62: ifnull +34 -> 96
    //   65: aload_3
    //   66: athrow
    //   67: astore_0
    //   68: ldc 32
    //   70: new 193	java/lang/StringBuilder
    //   73: dup
    //   74: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   77: ldc_w 1088
    //   80: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: aload_1
    //   84: invokevirtual 1091	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   87: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   90: aload_0
    //   91: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   94: aload_2
    //   95: astore_0
    //   96: aload_0
    //   97: areturn
    //   98: astore_3
    //   99: goto -40 -> 59
    //   102: astore_0
    //   103: aload_0
    //   104: athrow
    //   105: astore_3
    //   106: aload_0
    //   107: astore 4
    //   109: aload_2
    //   110: ifnull +10 -> 120
    //   113: aload_2
    //   114: invokevirtual 758	java/io/InputStream:close	()V
    //   117: aload_0
    //   118: astore 4
    //   120: aload 4
    //   122: ifnull +33 -> 155
    //   125: aload 5
    //   127: astore_2
    //   128: aload 4
    //   130: athrow
    //   131: aload_0
    //   132: astore 4
    //   134: aload_0
    //   135: aload 6
    //   137: if_acmpeq -17 -> 120
    //   140: aload 5
    //   142: astore_2
    //   143: aload_0
    //   144: aload 6
    //   146: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   149: aload_0
    //   150: astore 4
    //   152: goto -32 -> 120
    //   155: aload 5
    //   157: astore_2
    //   158: aload_3
    //   159: athrow
    //   160: astore_0
    //   161: aload_3
    //   162: astore_2
    //   163: aload_0
    //   164: astore_3
    //   165: aload 4
    //   167: astore_0
    //   168: goto -62 -> 106
    //   171: astore 6
    //   173: aload_0
    //   174: ifnonnull -43 -> 131
    //   177: aload 6
    //   179: astore 4
    //   181: goto -61 -> 120
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	184	0	paramContext	android.content.Context
    //   0	184	1	paramUri	android.net.Uri
    //   22	141	2	localObject1	Object
    //   20	46	3	localObject2	Object
    //   98	1	3	localThrowable1	Throwable
    //   105	57	3	localObject3	Object
    //   164	1	3	localContext	android.content.Context
    //   1	179	4	localObject4	Object
    //   17	139	5	localObject5	Object
    //   4	141	6	localThrowable2	Throwable
    //   171	7	6	localThrowable3	Throwable
    //   40	3	7	localEncodedImage	EncodedImage
    // Exception table:
    //   from	to	target	type
    //   65	67	67	java/lang/Throwable
    //   128	131	67	java/lang/Throwable
    //   143	149	67	java/lang/Throwable
    //   158	160	67	java/lang/Throwable
    //   52	56	98	java/lang/Throwable
    //   23	32	102	java/lang/Throwable
    //   36	42	102	java/lang/Throwable
    //   103	105	105	finally
    //   23	32	160	finally
    //   36	42	160	finally
    //   113	117	171	java/lang/Throwable
  }
  
  public static EncodedImage parseImage(InputStream paramInputStream)
  {
    if (paramInputStream == null) {
      return null;
    }
    JfifImage localJfifImage = null;
    if (isJfifHeader(paramInputStream)) {
      localJfifImage = JfifImage.create(paramInputStream);
    }
    return localJfifImage;
  }
  
  /* Error */
  public static EncodedImage parseImage(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aload_0
    //   6: ifnonnull +5 -> 11
    //   9: aconst_null
    //   10: areturn
    //   11: aconst_null
    //   12: astore 5
    //   14: aconst_null
    //   15: astore_2
    //   16: aconst_null
    //   17: astore 6
    //   19: new 891	java/io/FileInputStream
    //   22: dup
    //   23: aload_0
    //   24: invokespecial 892	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   27: astore_1
    //   28: aload_1
    //   29: invokestatic 1086	com/oneplus/media/ImageUtils:parseImage	(Ljava/io/InputStream;)Lcom/oneplus/media/EncodedImage;
    //   32: astore_2
    //   33: aload 4
    //   35: astore_3
    //   36: aload_1
    //   37: ifnull +10 -> 47
    //   40: aload_1
    //   41: invokevirtual 758	java/io/InputStream:close	()V
    //   44: aload 4
    //   46: astore_3
    //   47: aload_3
    //   48: ifnull +38 -> 86
    //   51: aload_3
    //   52: athrow
    //   53: astore_1
    //   54: ldc 32
    //   56: new 193	java/lang/StringBuilder
    //   59: dup
    //   60: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   63: ldc_w 1088
    //   66: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: aload_0
    //   70: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   73: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   76: aload_1
    //   77: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   80: aload_2
    //   81: areturn
    //   82: astore_3
    //   83: goto -36 -> 47
    //   86: aload_2
    //   87: areturn
    //   88: astore_2
    //   89: aload 6
    //   91: astore_1
    //   92: aload_2
    //   93: athrow
    //   94: astore 4
    //   96: aload_2
    //   97: astore_3
    //   98: aload 4
    //   100: astore_2
    //   101: aload_3
    //   102: astore 4
    //   104: aload_1
    //   105: ifnull +10 -> 115
    //   108: aload_1
    //   109: invokevirtual 758	java/io/InputStream:close	()V
    //   112: aload_3
    //   113: astore 4
    //   115: aload 4
    //   117: ifnull +25 -> 142
    //   120: aload 4
    //   122: athrow
    //   123: aload_3
    //   124: astore 4
    //   126: aload_3
    //   127: aload_1
    //   128: if_acmpeq -13 -> 115
    //   131: aload_3
    //   132: aload_1
    //   133: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   136: aload_3
    //   137: astore 4
    //   139: goto -24 -> 115
    //   142: aload_2
    //   143: athrow
    //   144: astore 4
    //   146: aload_2
    //   147: astore_1
    //   148: aload 4
    //   150: astore_2
    //   151: goto -50 -> 101
    //   154: astore_2
    //   155: goto -54 -> 101
    //   158: astore_2
    //   159: goto -67 -> 92
    //   162: astore_1
    //   163: aload 5
    //   165: astore_2
    //   166: goto -112 -> 54
    //   169: astore_1
    //   170: aload_3
    //   171: ifnonnull -48 -> 123
    //   174: aload_1
    //   175: astore 4
    //   177: goto -62 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	180	0	paramString	String
    //   27	14	1	localFileInputStream	java.io.FileInputStream
    //   53	24	1	localThrowable1	Throwable
    //   91	57	1	localObject1	Object
    //   162	1	1	localThrowable2	Throwable
    //   169	6	1	localThrowable3	Throwable
    //   15	72	2	localEncodedImage	EncodedImage
    //   88	9	2	localThrowable4	Throwable
    //   100	51	2	localObject2	Object
    //   154	1	2	localObject3	Object
    //   158	1	2	localThrowable5	Throwable
    //   165	1	2	localObject4	Object
    //   1	51	3	localObject5	Object
    //   82	1	3	localThrowable6	Throwable
    //   97	74	3	localObject6	Object
    //   3	42	4	localObject7	Object
    //   94	5	4	localObject8	Object
    //   102	36	4	localObject9	Object
    //   144	5	4	localObject10	Object
    //   175	1	4	localObject11	Object
    //   12	152	5	localObject12	Object
    //   17	73	6	localObject13	Object
    // Exception table:
    //   from	to	target	type
    //   51	53	53	java/lang/Throwable
    //   40	44	82	java/lang/Throwable
    //   19	28	88	java/lang/Throwable
    //   92	94	94	finally
    //   19	28	144	finally
    //   28	33	154	finally
    //   28	33	158	java/lang/Throwable
    //   120	123	162	java/lang/Throwable
    //   131	136	162	java/lang/Throwable
    //   142	144	162	java/lang/Throwable
    //   108	112	169	java/lang/Throwable
  }
  
  /* Error */
  public static PhotoMetadata readPhotoMetadata(InputStream paramInputStream)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +13 -> 14
    //   4: ldc 32
    //   6: ldc_w 1102
    //   9: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: aconst_null
    //   13: areturn
    //   14: aconst_null
    //   15: astore 4
    //   17: aconst_null
    //   18: astore_3
    //   19: new 623	com/oneplus/io/StreamState
    //   22: dup
    //   23: aload_0
    //   24: invokespecial 625	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   27: astore_2
    //   28: new 400	com/oneplus/base/SimpleRef
    //   31: dup
    //   32: lconst_0
    //   33: invokestatic 269	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   36: invokespecial 853	com/oneplus/base/SimpleRef:<init>	(Ljava/lang/Object;)V
    //   39: astore_3
    //   40: new 400	com/oneplus/base/SimpleRef
    //   43: dup
    //   44: iconst_0
    //   45: invokestatic 856	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   48: invokespecial 853	com/oneplus/base/SimpleRef:<init>	(Ljava/lang/Object;)V
    //   51: astore 4
    //   53: aload_0
    //   54: aload 4
    //   56: invokestatic 860	com/oneplus/media/ImageUtils:isTiffHeader	(Ljava/io/InputStream;Lcom/oneplus/base/Ref;)Z
    //   59: istore_1
    //   60: iload_1
    //   61: ifeq +79 -> 140
    //   64: aload_3
    //   65: aload_2
    //   66: invokevirtual 863	com/oneplus/io/StreamState:getSavedStreamPosition	()J
    //   69: invokestatic 269	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   72: invokeinterface 865 2 0
    //   77: iload_1
    //   78: ifeq +91 -> 169
    //   81: aload_0
    //   82: aload_3
    //   83: invokeinterface 428 1 0
    //   88: checkcast 266	java/lang/Long
    //   91: invokevirtual 431	java/lang/Long:longValue	()J
    //   94: aload_2
    //   95: invokevirtual 863	com/oneplus/io/StreamState:getSavedStreamPosition	()J
    //   98: lsub
    //   99: invokevirtual 1106	java/io/InputStream:skip	(J)J
    //   102: pop2
    //   103: new 1108	com/oneplus/media/ExifMetadata
    //   106: dup
    //   107: aload_0
    //   108: invokespecial 1109	com/oneplus/media/ExifMetadata:<init>	(Ljava/io/InputStream;)V
    //   111: astore_3
    //   112: aload_2
    //   113: ifnull +7 -> 120
    //   116: aload_2
    //   117: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   120: aconst_null
    //   121: astore_0
    //   122: aload_0
    //   123: ifnull +44 -> 167
    //   126: aload_0
    //   127: athrow
    //   128: astore_0
    //   129: ldc 32
    //   131: ldc_w 1111
    //   134: aload_0
    //   135: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   138: aconst_null
    //   139: areturn
    //   140: aload_0
    //   141: invokestatic 609	com/oneplus/media/ImageUtils:isJfifHeader	(Ljava/io/InputStream;)Z
    //   144: ifeq +14 -> 158
    //   147: aload_0
    //   148: aload_3
    //   149: aload 4
    //   151: invokestatic 888	com/oneplus/media/ImageUtils:findTiffHeader	(Ljava/io/InputStream;Lcom/oneplus/base/Ref;Lcom/oneplus/base/Ref;)Z
    //   154: istore_1
    //   155: goto -78 -> 77
    //   158: iconst_0
    //   159: istore_1
    //   160: goto -83 -> 77
    //   163: astore_0
    //   164: goto -42 -> 122
    //   167: aload_3
    //   168: areturn
    //   169: aload_2
    //   170: ifnull +7 -> 177
    //   173: aload_2
    //   174: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   177: aconst_null
    //   178: astore_0
    //   179: aload_0
    //   180: ifnull +56 -> 236
    //   183: aload_0
    //   184: athrow
    //   185: astore_0
    //   186: goto -7 -> 179
    //   189: astore_0
    //   190: aload_0
    //   191: athrow
    //   192: astore_2
    //   193: aload_0
    //   194: astore 4
    //   196: aload_3
    //   197: ifnull +10 -> 207
    //   200: aload_3
    //   201: invokevirtual 638	com/oneplus/io/StreamState:close	()V
    //   204: aload_0
    //   205: astore 4
    //   207: aload 4
    //   209: ifnull +25 -> 234
    //   212: aload 4
    //   214: athrow
    //   215: aload_0
    //   216: astore 4
    //   218: aload_0
    //   219: aload_3
    //   220: if_acmpeq -13 -> 207
    //   223: aload_0
    //   224: aload_3
    //   225: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   228: aload_0
    //   229: astore 4
    //   231: goto -24 -> 207
    //   234: aload_2
    //   235: athrow
    //   236: aconst_null
    //   237: areturn
    //   238: astore_2
    //   239: aconst_null
    //   240: astore_0
    //   241: aload 4
    //   243: astore_3
    //   244: goto -51 -> 193
    //   247: astore 4
    //   249: aconst_null
    //   250: astore_0
    //   251: aload_2
    //   252: astore_3
    //   253: aload 4
    //   255: astore_2
    //   256: goto -63 -> 193
    //   259: astore_0
    //   260: aload_2
    //   261: astore_3
    //   262: goto -72 -> 190
    //   265: astore_0
    //   266: goto -137 -> 129
    //   269: astore_3
    //   270: aload_0
    //   271: ifnonnull -56 -> 215
    //   274: aload_3
    //   275: astore 4
    //   277: goto -70 -> 207
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	280	0	paramInputStream	InputStream
    //   59	101	1	bool	boolean
    //   27	147	2	localStreamState	com.oneplus.io.StreamState
    //   192	43	2	localObject1	Object
    //   238	14	2	localObject2	Object
    //   255	6	2	localObject3	Object
    //   18	244	3	localObject4	Object
    //   269	6	3	localThrowable	Throwable
    //   15	227	4	localObject5	Object
    //   247	7	4	localObject6	Object
    //   275	1	4	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   126	128	128	java/lang/Throwable
    //   183	185	128	java/lang/Throwable
    //   116	120	163	java/lang/Throwable
    //   173	177	185	java/lang/Throwable
    //   19	28	189	java/lang/Throwable
    //   190	192	192	finally
    //   19	28	238	finally
    //   28	60	247	finally
    //   64	77	247	finally
    //   81	112	247	finally
    //   140	155	247	finally
    //   28	60	259	java/lang/Throwable
    //   64	77	259	java/lang/Throwable
    //   81	112	259	java/lang/Throwable
    //   140	155	259	java/lang/Throwable
    //   212	215	265	java/lang/Throwable
    //   223	228	265	java/lang/Throwable
    //   234	236	265	java/lang/Throwable
    //   200	204	269	java/lang/Throwable
  }
  
  /* Error */
  public static PhotoMetadata readPhotoMetadata(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aconst_null
    //   3: astore_2
    //   4: new 891	java/io/FileInputStream
    //   7: dup
    //   8: aload_0
    //   9: invokespecial 892	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   12: astore_0
    //   13: aload_0
    //   14: invokestatic 1114	com/oneplus/media/ImageUtils:readPhotoMetadata	(Ljava/io/InputStream;)Lcom/oneplus/media/PhotoMetadata;
    //   17: astore_1
    //   18: aload_0
    //   19: ifnull +7 -> 26
    //   22: aload_0
    //   23: invokevirtual 893	java/io/FileInputStream:close	()V
    //   26: aconst_null
    //   27: astore_0
    //   28: aload_0
    //   29: ifnull +20 -> 49
    //   32: aload_0
    //   33: athrow
    //   34: astore_0
    //   35: ldc 32
    //   37: ldc_w 1116
    //   40: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   43: aconst_null
    //   44: areturn
    //   45: astore_0
    //   46: goto -18 -> 28
    //   49: aload_1
    //   50: areturn
    //   51: astore_0
    //   52: aload_0
    //   53: athrow
    //   54: astore_1
    //   55: aload_0
    //   56: astore_3
    //   57: aload_2
    //   58: ifnull +9 -> 67
    //   61: aload_2
    //   62: invokevirtual 893	java/io/FileInputStream:close	()V
    //   65: aload_0
    //   66: astore_3
    //   67: aload_3
    //   68: ifnull +22 -> 90
    //   71: aload_3
    //   72: athrow
    //   73: aload_0
    //   74: astore_3
    //   75: aload_0
    //   76: aload_2
    //   77: if_acmpeq -10 -> 67
    //   80: aload_0
    //   81: aload_2
    //   82: invokevirtual 452	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   85: aload_0
    //   86: astore_3
    //   87: goto -20 -> 67
    //   90: aload_1
    //   91: athrow
    //   92: astore_3
    //   93: aconst_null
    //   94: astore_0
    //   95: aload_1
    //   96: astore_2
    //   97: aload_3
    //   98: astore_1
    //   99: goto -44 -> 55
    //   102: astore_1
    //   103: aconst_null
    //   104: astore_3
    //   105: aload_0
    //   106: astore_2
    //   107: aload_3
    //   108: astore_0
    //   109: goto -54 -> 55
    //   112: astore_1
    //   113: aload_0
    //   114: astore_2
    //   115: aload_1
    //   116: astore_0
    //   117: goto -65 -> 52
    //   120: astore_0
    //   121: goto -86 -> 35
    //   124: astore_2
    //   125: aload_0
    //   126: ifnonnull -53 -> 73
    //   129: aload_2
    //   130: astore_3
    //   131: goto -64 -> 67
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	134	0	paramString	String
    //   1	49	1	localPhotoMetadata	PhotoMetadata
    //   54	42	1	localObject1	Object
    //   98	1	1	localObject2	Object
    //   102	1	1	localObject3	Object
    //   112	4	1	localThrowable1	Throwable
    //   3	112	2	localObject4	Object
    //   124	6	2	localThrowable2	Throwable
    //   56	31	3	str	String
    //   92	6	3	localObject5	Object
    //   104	27	3	localThrowable3	Throwable
    // Exception table:
    //   from	to	target	type
    //   32	34	34	java/lang/Throwable
    //   22	26	45	java/lang/Throwable
    //   4	13	51	java/lang/Throwable
    //   52	54	54	finally
    //   4	13	92	finally
    //   13	18	102	finally
    //   13	18	112	java/lang/Throwable
    //   71	73	120	java/lang/Throwable
    //   80	85	120	java/lang/Throwable
    //   90	92	120	java/lang/Throwable
    //   61	65	124	java/lang/Throwable
  }
  
  public static void removeArgbPaddings(ByteBuffer paramByteBuffer1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ByteBuffer paramByteBuffer2)
  {
    int m = paramByteBuffer1.position();
    int n = paramByteBuffer2.position();
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      try
      {
        throw new IllegalArgumentException("Invalid size : " + paramInt1 + "x" + paramInt2);
      }
      finally
      {
        paramByteBuffer1.position(m);
        paramByteBuffer2.position(n);
      }
    }
    if (paramInt3 < 4) {
      throw new IllegalArgumentException("Invalid pixel stride : " + paramInt3);
    }
    if (paramInt4 < paramInt3 * paramInt1) {
      throw new IllegalArgumentException("Invalid row stride : " + paramInt4);
    }
    if (paramInt3 == 4)
    {
      paramInt1 *= 4;
      if (paramInt4 == paramInt1) {
        paramByteBuffer2.put(paramByteBuffer1);
      }
      for (;;)
      {
        paramByteBuffer1.position(m);
        paramByteBuffer2.position(n);
        return;
        arrayOfByte = new byte[paramInt1];
        paramInt1 = 0;
        while (paramInt1 < paramInt2)
        {
          paramByteBuffer1.position(paramInt1 * paramInt4);
          paramByteBuffer1.get(arrayOfByte);
          paramByteBuffer2.put(arrayOfByte);
          paramInt1 += 1;
        }
      }
    }
    byte[] arrayOfByte = new byte[4];
    int i = 0;
    for (;;)
    {
      int k;
      int j;
      if (k < paramInt1)
      {
        paramByteBuffer1.position(j);
        paramByteBuffer1.get(arrayOfByte);
        paramByteBuffer2.put(arrayOfByte);
        k += 1;
        j += paramInt3;
      }
      else
      {
        i += 1;
        if (i >= paramInt2) {
          break;
        }
        j = i * paramInt4;
        k = 0;
      }
    }
  }
  
  private static native void rgbaToYuvaAndNv21a(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2);
  
  public static Bitmap rotate(Bitmap paramBitmap, int paramInt)
  {
    int i = paramInt % 360;
    paramInt = i;
    if (i < 0) {
      paramInt = i + 360;
    }
    if ((paramBitmap == null) || (paramInt == 0)) {
      return paramBitmap;
    }
    if ((paramBitmap.getConfig() == Bitmap.Config.ARGB_8888) && (paramInt == 90) && (NativeLibrary.load()))
    {
      paramInt = paramBitmap.getWidth();
      i = paramBitmap.getHeight();
      int j = paramInt * i * 4;
      localObject = ByteBuffer.allocateDirect(j).asIntBuffer();
      IntBuffer localIntBuffer = ByteBuffer.allocateDirect(j).asIntBuffer();
      Bitmap localBitmap = Bitmap.createBitmap(i, paramInt, paramBitmap.getConfig());
      paramBitmap.copyPixelsToBuffer((Buffer)localObject);
      rotateRgbaImage90((IntBuffer)localObject, paramInt, i, localIntBuffer);
      localBitmap.copyPixelsFromBuffer(localIntBuffer);
      return localBitmap;
    }
    Object localObject = new Matrix();
    ((Matrix)localObject).postRotate(paramInt);
    return Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), (Matrix)localObject, false);
  }
  
  public static byte[] rotateNV21Image(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    switch (paramInt3)
    {
    default: 
      throw new IllegalArgumentException("Invalid rotation degrees : " + paramInt3 + ".");
    case 0: 
      return paramArrayOfByte;
    }
    byte[] arrayOfByte = new byte[paramInt1 * paramInt2 * 3 / 2];
    if (NativeLibrary.load()) {
      rotateNV21Image90(paramArrayOfByte, paramInt1, paramInt2, arrayOfByte);
    }
    return arrayOfByte;
  }
  
  private static native boolean rotateNV21Image90(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2);
  
  private static native boolean rotateRgbaImage90(IntBuffer paramIntBuffer1, int paramInt1, int paramInt2, IntBuffer paramIntBuffer2);
  
  public static boolean scaleNV21Image(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 % 2 != 0) || (paramInt4 % 2 != 0))
    {
      Log.w("ImageUtils", "scaleNV21Image() - scaledWidthL " + paramInt3 + " or scaledHeight: " + paramInt4 + " is not divisible by 2");
      return false;
    }
    if ((paramInt1 % 2 != 0) || (paramInt2 % 2 != 0))
    {
      Log.w("ImageUtils", "scaleNV21Image() - srcWidth " + paramInt1 + " or srcHeight: " + paramInt2 + " is not divisible by 2");
      return false;
    }
    if ((paramInt1 == paramInt3) && (paramInt2 == paramInt4))
    {
      System.arraycopy(paramArrayOfByte1, 0, paramArrayOfByte2, 0, paramInt3 * paramInt4 * 3 / 2);
      return true;
    }
    if (paramArrayOfByte2.length < paramInt3 * paramInt4 * 3 / 2)
    {
      Log.w("ImageUtils", "scaleNV21Image() - Invalid YUV data size");
      return false;
    }
    if (NativeLibrary.load()) {
      return scaleNV21ImageNative(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
    }
    return false;
  }
  
  public static byte[] scaleNV21Image(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt1 == paramInt3) && (paramInt2 == paramInt4)) {
      return paramArrayOfByte;
    }
    if ((paramInt3 % 2 != 0) || (paramInt4 % 2 != 0)) {
      throw new RuntimeException("scaledWidthL " + paramInt3 + " or scaledHeight: " + paramInt4 + " is not divisible by 2");
    }
    byte[] arrayOfByte = new byte[paramInt3 * paramInt4 * 3 / 2];
    if (NativeLibrary.load()) {
      scaleNV21ImageNative(paramArrayOfByte, paramInt1, paramInt2, arrayOfByte, paramInt3, paramInt4);
    }
    return arrayOfByte;
  }
  
  private static native boolean scaleNV21ImageNative(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4);
  
  public static void unlockPixels(Bitmap paramBitmap)
  {
    if (NativeLibrary.load()) {
      nativeUnlockPixels(paramBitmap);
    }
  }
  
  /* Error */
  private static boolean updateTiffExposureTime(SeekableByteChannel paramSeekableByteChannel, android.util.Rational paramRational)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +13 -> 14
    //   4: ldc 32
    //   6: ldc_w 1185
    //   9: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: iconst_0
    //   13: ireturn
    //   14: aload_1
    //   15: ifnonnull +13 -> 28
    //   18: ldc 32
    //   20: ldc_w 1187
    //   23: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   26: iconst_0
    //   27: ireturn
    //   28: ldc2_w 1003
    //   31: lstore 7
    //   33: lload 7
    //   35: lstore 5
    //   37: aload_0
    //   38: invokeinterface 1008 1 0
    //   43: lstore 9
    //   45: lload 9
    //   47: lstore 5
    //   49: lload 9
    //   51: lstore 7
    //   53: bipush 12
    //   55: newarray <illegal type>
    //   57: astore 13
    //   59: lload 9
    //   61: lstore 5
    //   63: lload 9
    //   65: lstore 7
    //   67: aload 13
    //   69: invokestatic 1011	java/nio/ByteBuffer:wrap	([B)Ljava/nio/ByteBuffer;
    //   72: astore 14
    //   74: lload 9
    //   76: lstore 5
    //   78: lload 9
    //   80: lstore 7
    //   82: aload 14
    //   84: iconst_4
    //   85: invokevirtual 512	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   88: pop
    //   89: lload 9
    //   91: lstore 5
    //   93: lload 9
    //   95: lstore 7
    //   97: aload_0
    //   98: aload 14
    //   100: invokeinterface 1014 2 0
    //   105: istore_2
    //   106: iload_2
    //   107: bipush 8
    //   109: if_icmpge +33 -> 142
    //   112: lload 9
    //   114: lconst_0
    //   115: lcmp
    //   116: iflt +12 -> 128
    //   119: aload_0
    //   120: lload 9
    //   122: invokeinterface 1017 3 0
    //   127: pop
    //   128: iconst_0
    //   129: ireturn
    //   130: astore_0
    //   131: ldc 32
    //   133: ldc_w 1189
    //   136: aload_0
    //   137: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   140: iconst_0
    //   141: ireturn
    //   142: aload 13
    //   144: iconst_4
    //   145: baload
    //   146: bipush 73
    //   148: if_icmpne +204 -> 352
    //   151: aload 13
    //   153: iconst_5
    //   154: baload
    //   155: bipush 73
    //   157: if_icmpne +195 -> 352
    //   160: lload 9
    //   162: lstore 5
    //   164: lload 9
    //   166: lstore 7
    //   168: aload 14
    //   170: getstatic 1195	java/nio/ByteOrder:LITTLE_ENDIAN	Ljava/nio/ByteOrder;
    //   173: invokevirtual 1199	java/nio/ByteBuffer:order	(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
    //   176: pop
    //   177: lload 9
    //   179: lstore 5
    //   181: lload 9
    //   183: lstore 7
    //   185: aload 14
    //   187: iconst_0
    //   188: invokevirtual 512	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   191: pop
    //   192: lload 9
    //   194: lstore 5
    //   196: lload 9
    //   198: lstore 7
    //   200: aload 14
    //   202: invokevirtual 1135	java/nio/ByteBuffer:asIntBuffer	()Ljava/nio/IntBuffer;
    //   205: astore 15
    //   207: lload 9
    //   209: lstore 5
    //   211: lload 9
    //   213: lstore 7
    //   215: aload 15
    //   217: iconst_2
    //   218: invokevirtual 1202	java/nio/IntBuffer:position	(I)Ljava/nio/Buffer;
    //   221: pop
    //   222: lload 9
    //   224: lstore 5
    //   226: lload 9
    //   228: lstore 7
    //   230: aload_0
    //   231: lload 9
    //   233: aload 15
    //   235: invokevirtual 1204	java/nio/IntBuffer:get	()I
    //   238: i2l
    //   239: ldc2_w 1205
    //   242: land
    //   243: ladd
    //   244: invokeinterface 1017 3 0
    //   249: pop
    //   250: lload 9
    //   252: lstore 5
    //   254: lload 9
    //   256: lstore 7
    //   258: aload 14
    //   260: iconst_0
    //   261: invokevirtual 512	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   264: pop
    //   265: lload 9
    //   267: lstore 5
    //   269: lload 9
    //   271: lstore 7
    //   273: aload 14
    //   275: invokevirtual 1210	java/nio/ByteBuffer:asShortBuffer	()Ljava/nio/ShortBuffer;
    //   278: astore 16
    //   280: lload 9
    //   282: lstore 5
    //   284: lload 9
    //   286: lstore 7
    //   288: getstatic 87	com/oneplus/media/Ifd:IFD_0	Lcom/oneplus/media/Ifd;
    //   291: astore 13
    //   293: lconst_0
    //   294: lstore 11
    //   296: lload 9
    //   298: lstore 5
    //   300: lload 9
    //   302: lstore 7
    //   304: aload 14
    //   306: bipush 10
    //   308: invokevirtual 512	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   311: pop
    //   312: lload 9
    //   314: lstore 5
    //   316: lload 9
    //   318: lstore 7
    //   320: aload_0
    //   321: aload 14
    //   323: invokeinterface 1014 2 0
    //   328: istore_2
    //   329: iload_2
    //   330: iconst_2
    //   331: if_icmpge +133 -> 464
    //   334: lload 9
    //   336: lconst_0
    //   337: lcmp
    //   338: iflt +12 -> 350
    //   341: aload_0
    //   342: lload 9
    //   344: invokeinterface 1017 3 0
    //   349: pop
    //   350: iconst_0
    //   351: ireturn
    //   352: aload 13
    //   354: iconst_4
    //   355: baload
    //   356: bipush 77
    //   358: if_icmpne +64 -> 422
    //   361: aload 13
    //   363: iconst_5
    //   364: baload
    //   365: bipush 77
    //   367: if_icmpne +55 -> 422
    //   370: lload 9
    //   372: lstore 5
    //   374: lload 9
    //   376: lstore 7
    //   378: aload 14
    //   380: getstatic 1213	java/nio/ByteOrder:BIG_ENDIAN	Ljava/nio/ByteOrder;
    //   383: invokevirtual 1199	java/nio/ByteBuffer:order	(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
    //   386: pop
    //   387: goto -210 -> 177
    //   390: astore_1
    //   391: lload 5
    //   393: lstore 7
    //   395: ldc 32
    //   397: ldc_w 1215
    //   400: aload_1
    //   401: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   404: lload 5
    //   406: lconst_0
    //   407: lcmp
    //   408: iflt +12 -> 420
    //   411: aload_0
    //   412: lload 5
    //   414: invokeinterface 1017 3 0
    //   419: pop
    //   420: iconst_0
    //   421: ireturn
    //   422: lload 9
    //   424: lconst_0
    //   425: lcmp
    //   426: iflt +12 -> 438
    //   429: aload_0
    //   430: lload 9
    //   432: invokeinterface 1017 3 0
    //   437: pop
    //   438: iconst_0
    //   439: ireturn
    //   440: astore_0
    //   441: ldc 32
    //   443: ldc_w 1189
    //   446: aload_0
    //   447: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   450: iconst_0
    //   451: ireturn
    //   452: astore_0
    //   453: ldc 32
    //   455: ldc_w 1189
    //   458: aload_0
    //   459: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   462: iconst_0
    //   463: ireturn
    //   464: lload 9
    //   466: lstore 5
    //   468: lload 9
    //   470: lstore 7
    //   472: aload 16
    //   474: iconst_5
    //   475: invokevirtual 1218	java/nio/ShortBuffer:position	(I)Ljava/nio/Buffer;
    //   478: pop
    //   479: lload 9
    //   481: lstore 5
    //   483: lload 9
    //   485: lstore 7
    //   487: aload 16
    //   489: invokevirtual 1221	java/nio/ShortBuffer:get	()S
    //   492: ldc_w 1222
    //   495: iand
    //   496: istore_2
    //   497: iload_2
    //   498: ifle +584 -> 1082
    //   501: lload 9
    //   503: lstore 5
    //   505: lload 9
    //   507: lstore 7
    //   509: aload 14
    //   511: iconst_0
    //   512: invokevirtual 512	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   515: pop
    //   516: lload 9
    //   518: lstore 5
    //   520: lload 9
    //   522: lstore 7
    //   524: aload_0
    //   525: aload 14
    //   527: invokeinterface 1014 2 0
    //   532: istore_3
    //   533: iload_3
    //   534: bipush 12
    //   536: if_icmpge +33 -> 569
    //   539: lload 9
    //   541: lconst_0
    //   542: lcmp
    //   543: iflt +12 -> 555
    //   546: aload_0
    //   547: lload 9
    //   549: invokeinterface 1017 3 0
    //   554: pop
    //   555: iconst_0
    //   556: ireturn
    //   557: astore_0
    //   558: ldc 32
    //   560: ldc_w 1189
    //   563: aload_0
    //   564: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   567: iconst_0
    //   568: ireturn
    //   569: lload 9
    //   571: lstore 5
    //   573: lload 9
    //   575: lstore 7
    //   577: aload 16
    //   579: iconst_0
    //   580: invokevirtual 1218	java/nio/ShortBuffer:position	(I)Ljava/nio/Buffer;
    //   583: pop
    //   584: iload_2
    //   585: iconst_1
    //   586: isub
    //   587: istore_3
    //   588: lload 9
    //   590: lstore 5
    //   592: lload 9
    //   594: lstore 7
    //   596: aload 16
    //   598: invokevirtual 1221	java/nio/ShortBuffer:get	()S
    //   601: ldc_w 1222
    //   604: iand
    //   605: istore 4
    //   607: lload 9
    //   609: lstore 5
    //   611: lload 9
    //   613: lstore 7
    //   615: aload 16
    //   617: invokevirtual 1221	java/nio/ShortBuffer:get	()S
    //   620: ldc_w 1222
    //   623: iand
    //   624: istore_2
    //   625: iload 4
    //   627: ldc_w 1223
    //   630: if_icmpne +384 -> 1014
    //   633: iload_2
    //   634: bipush 10
    //   636: if_icmpeq +8 -> 644
    //   639: iload_2
    //   640: iconst_5
    //   641: if_icmpne +310 -> 951
    //   644: lload 9
    //   646: lstore 5
    //   648: lload 9
    //   650: lstore 7
    //   652: aload 15
    //   654: iconst_2
    //   655: invokevirtual 1202	java/nio/IntBuffer:position	(I)Ljava/nio/Buffer;
    //   658: pop
    //   659: lload 9
    //   661: lstore 5
    //   663: lload 9
    //   665: lstore 7
    //   667: aload 15
    //   669: invokevirtual 1204	java/nio/IntBuffer:get	()I
    //   672: i2l
    //   673: ldc2_w 1205
    //   676: land
    //   677: lstore 11
    //   679: lload 9
    //   681: lstore 5
    //   683: lload 9
    //   685: lstore 7
    //   687: ldc 32
    //   689: new 193	java/lang/StringBuilder
    //   692: dup
    //   693: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   696: ldc_w 1225
    //   699: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   702: lload 11
    //   704: invokevirtual 966	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   707: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   710: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   713: lload 9
    //   715: lstore 5
    //   717: lload 9
    //   719: lstore 7
    //   721: aload 14
    //   723: bipush 10
    //   725: invokevirtual 512	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   728: pop
    //   729: lload 9
    //   731: lstore 5
    //   733: lload 9
    //   735: lstore 7
    //   737: aload 16
    //   739: iconst_5
    //   740: invokevirtual 1218	java/nio/ShortBuffer:position	(I)Ljava/nio/Buffer;
    //   743: pop
    //   744: lload 9
    //   746: lstore 5
    //   748: lload 9
    //   750: lstore 7
    //   752: aload 16
    //   754: bipush 10
    //   756: invokevirtual 1228	java/nio/ShortBuffer:put	(S)Ljava/nio/ShortBuffer;
    //   759: pop
    //   760: lload 9
    //   762: lstore 5
    //   764: lload 9
    //   766: lstore 7
    //   768: aload_0
    //   769: aload_0
    //   770: invokeinterface 1008 1 0
    //   775: ldc2_w 1229
    //   778: lsub
    //   779: invokeinterface 1017 3 0
    //   784: pop
    //   785: lload 9
    //   787: lstore 5
    //   789: lload 9
    //   791: lstore 7
    //   793: aload_0
    //   794: aload 14
    //   796: invokeinterface 1233 2 0
    //   801: pop
    //   802: lload 9
    //   804: lstore 5
    //   806: lload 9
    //   808: lstore 7
    //   810: aload 14
    //   812: iconst_4
    //   813: invokevirtual 512	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   816: pop
    //   817: lload 9
    //   819: lstore 5
    //   821: lload 9
    //   823: lstore 7
    //   825: aload 15
    //   827: iconst_1
    //   828: invokevirtual 1202	java/nio/IntBuffer:position	(I)Ljava/nio/Buffer;
    //   831: pop
    //   832: lload 9
    //   834: lstore 5
    //   836: lload 9
    //   838: lstore 7
    //   840: aload 15
    //   842: aload_1
    //   843: invokevirtual 961	android/util/Rational:getNumerator	()I
    //   846: invokevirtual 1236	java/nio/IntBuffer:put	(I)Ljava/nio/IntBuffer;
    //   849: pop
    //   850: lload 9
    //   852: lstore 5
    //   854: lload 9
    //   856: lstore 7
    //   858: aload 15
    //   860: aload_1
    //   861: invokevirtual 1239	android/util/Rational:getDenominator	()I
    //   864: invokevirtual 1236	java/nio/IntBuffer:put	(I)Ljava/nio/IntBuffer;
    //   867: pop
    //   868: lload 9
    //   870: lstore 5
    //   872: lload 9
    //   874: lstore 7
    //   876: aload_0
    //   877: lload 9
    //   879: lload 11
    //   881: ladd
    //   882: invokeinterface 1017 3 0
    //   887: pop
    //   888: lload 9
    //   890: lstore 5
    //   892: lload 9
    //   894: lstore 7
    //   896: aload_0
    //   897: aload 14
    //   899: invokeinterface 1233 2 0
    //   904: pop
    //   905: lload 9
    //   907: lstore 5
    //   909: lload 9
    //   911: lstore 7
    //   913: ldc 32
    //   915: ldc_w 1241
    //   918: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   921: lload 9
    //   923: lconst_0
    //   924: lcmp
    //   925: iflt +12 -> 937
    //   928: aload_0
    //   929: lload 9
    //   931: invokeinterface 1017 3 0
    //   936: pop
    //   937: iconst_1
    //   938: ireturn
    //   939: astore_0
    //   940: ldc 32
    //   942: ldc_w 1189
    //   945: aload_0
    //   946: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   949: iconst_1
    //   950: ireturn
    //   951: lload 9
    //   953: lstore 5
    //   955: lload 9
    //   957: lstore 7
    //   959: ldc 32
    //   961: new 193	java/lang/StringBuilder
    //   964: dup
    //   965: invokespecial 194	java/lang/StringBuilder:<init>	()V
    //   968: ldc_w 1243
    //   971: invokevirtual 200	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   974: iload_2
    //   975: invokevirtual 203	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   978: invokevirtual 209	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   981: invokestatic 698	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   984: lload 9
    //   986: lconst_0
    //   987: lcmp
    //   988: iflt +12 -> 1000
    //   991: aload_0
    //   992: lload 9
    //   994: invokeinterface 1017 3 0
    //   999: pop
    //   1000: iconst_0
    //   1001: ireturn
    //   1002: astore_0
    //   1003: ldc 32
    //   1005: ldc_w 1189
    //   1008: aload_0
    //   1009: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1012: iconst_0
    //   1013: ireturn
    //   1014: iload_3
    //   1015: istore_2
    //   1016: iload 4
    //   1018: ldc_w 1244
    //   1021: if_icmpne -524 -> 497
    //   1024: lload 9
    //   1026: lstore 5
    //   1028: iload_3
    //   1029: istore_2
    //   1030: lload 9
    //   1032: lstore 7
    //   1034: aload 13
    //   1036: getstatic 87	com/oneplus/media/Ifd:IFD_0	Lcom/oneplus/media/Ifd;
    //   1039: if_acmpne -542 -> 497
    //   1042: lload 9
    //   1044: lstore 5
    //   1046: lload 9
    //   1048: lstore 7
    //   1050: aload 15
    //   1052: iconst_2
    //   1053: invokevirtual 1202	java/nio/IntBuffer:position	(I)Ljava/nio/Buffer;
    //   1056: pop
    //   1057: lload 9
    //   1059: lstore 5
    //   1061: lload 9
    //   1063: lstore 7
    //   1065: aload 15
    //   1067: invokevirtual 1204	java/nio/IntBuffer:get	()I
    //   1070: i2l
    //   1071: ldc2_w 1205
    //   1074: land
    //   1075: lstore 11
    //   1077: iload_3
    //   1078: istore_2
    //   1079: goto -582 -> 497
    //   1082: lload 9
    //   1084: lstore 5
    //   1086: lload 9
    //   1088: lstore 7
    //   1090: invokestatic 925	com/oneplus/media/ImageUtils:-getcom-oneplus-media-IfdSwitchesValues	()[I
    //   1093: aload 13
    //   1095: invokevirtual 81	com/oneplus/media/Ifd:ordinal	()I
    //   1098: iaload
    //   1099: tableswitch	default:+213->1312, 3:+51->1150
    //   1116: lload 9
    //   1118: lstore 5
    //   1120: lload 9
    //   1122: lstore 7
    //   1124: ldc 32
    //   1126: ldc_w 1246
    //   1129: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   1132: lload 9
    //   1134: lconst_0
    //   1135: lcmp
    //   1136: iflt +12 -> 1148
    //   1139: aload_0
    //   1140: lload 9
    //   1142: invokeinterface 1017 3 0
    //   1147: pop
    //   1148: iconst_0
    //   1149: ireturn
    //   1150: lload 11
    //   1152: lconst_0
    //   1153: lcmp
    //   1154: ifle +55 -> 1209
    //   1157: lload 9
    //   1159: lstore 5
    //   1161: lload 9
    //   1163: lstore 7
    //   1165: ldc 32
    //   1167: ldc_w 1248
    //   1170: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   1173: lload 9
    //   1175: lstore 5
    //   1177: lload 9
    //   1179: lstore 7
    //   1181: aload_0
    //   1182: lload 9
    //   1184: lload 11
    //   1186: ladd
    //   1187: invokeinterface 1017 3 0
    //   1192: pop
    //   1193: lload 9
    //   1195: lstore 5
    //   1197: lload 9
    //   1199: lstore 7
    //   1201: getstatic 80	com/oneplus/media/Ifd:EXIF	Lcom/oneplus/media/Ifd;
    //   1204: astore 13
    //   1206: goto -910 -> 296
    //   1209: lload 9
    //   1211: lstore 5
    //   1213: lload 9
    //   1215: lstore 7
    //   1217: ldc 32
    //   1219: ldc_w 1250
    //   1222: invokestatic 423	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   1225: lload 9
    //   1227: lconst_0
    //   1228: lcmp
    //   1229: iflt +12 -> 1241
    //   1232: aload_0
    //   1233: lload 9
    //   1235: invokeinterface 1017 3 0
    //   1240: pop
    //   1241: iconst_0
    //   1242: ireturn
    //   1243: astore_0
    //   1244: ldc 32
    //   1246: ldc_w 1189
    //   1249: aload_0
    //   1250: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1253: iconst_0
    //   1254: ireturn
    //   1255: astore_0
    //   1256: ldc 32
    //   1258: ldc_w 1189
    //   1261: aload_0
    //   1262: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1265: iconst_0
    //   1266: ireturn
    //   1267: astore_0
    //   1268: ldc 32
    //   1270: ldc_w 1189
    //   1273: aload_0
    //   1274: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1277: goto -857 -> 420
    //   1280: astore_1
    //   1281: lload 7
    //   1283: lconst_0
    //   1284: lcmp
    //   1285: iflt +12 -> 1297
    //   1288: aload_0
    //   1289: lload 7
    //   1291: invokeinterface 1017 3 0
    //   1296: pop
    //   1297: aload_1
    //   1298: athrow
    //   1299: astore_0
    //   1300: ldc 32
    //   1302: ldc_w 1189
    //   1305: aload_0
    //   1306: invokestatic 345	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1309: goto -12 -> 1297
    //   1312: goto -196 -> 1116
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1315	0	paramSeekableByteChannel	SeekableByteChannel
    //   0	1315	1	paramRational	android.util.Rational
    //   105	974	2	i	int
    //   532	546	3	j	int
    //   605	417	4	k	int
    //   35	1177	5	l1	long
    //   31	1259	7	l2	long
    //   43	1191	9	l3	long
    //   294	891	11	l4	long
    //   57	1148	13	localObject	Object
    //   72	826	14	localByteBuffer	ByteBuffer
    //   205	861	15	localIntBuffer	IntBuffer
    //   278	475	16	localShortBuffer	java.nio.ShortBuffer
    // Exception table:
    //   from	to	target	type
    //   119	128	130	java/lang/Throwable
    //   37	45	390	java/lang/Throwable
    //   53	59	390	java/lang/Throwable
    //   67	74	390	java/lang/Throwable
    //   82	89	390	java/lang/Throwable
    //   97	106	390	java/lang/Throwable
    //   168	177	390	java/lang/Throwable
    //   185	192	390	java/lang/Throwable
    //   200	207	390	java/lang/Throwable
    //   215	222	390	java/lang/Throwable
    //   230	250	390	java/lang/Throwable
    //   258	265	390	java/lang/Throwable
    //   273	280	390	java/lang/Throwable
    //   288	293	390	java/lang/Throwable
    //   304	312	390	java/lang/Throwable
    //   320	329	390	java/lang/Throwable
    //   378	387	390	java/lang/Throwable
    //   472	479	390	java/lang/Throwable
    //   487	497	390	java/lang/Throwable
    //   509	516	390	java/lang/Throwable
    //   524	533	390	java/lang/Throwable
    //   577	584	390	java/lang/Throwable
    //   596	607	390	java/lang/Throwable
    //   615	625	390	java/lang/Throwable
    //   652	659	390	java/lang/Throwable
    //   667	679	390	java/lang/Throwable
    //   687	713	390	java/lang/Throwable
    //   721	729	390	java/lang/Throwable
    //   737	744	390	java/lang/Throwable
    //   752	760	390	java/lang/Throwable
    //   768	785	390	java/lang/Throwable
    //   793	802	390	java/lang/Throwable
    //   810	817	390	java/lang/Throwable
    //   825	832	390	java/lang/Throwable
    //   840	850	390	java/lang/Throwable
    //   858	868	390	java/lang/Throwable
    //   876	888	390	java/lang/Throwable
    //   896	905	390	java/lang/Throwable
    //   913	921	390	java/lang/Throwable
    //   959	984	390	java/lang/Throwable
    //   1034	1042	390	java/lang/Throwable
    //   1050	1057	390	java/lang/Throwable
    //   1065	1077	390	java/lang/Throwable
    //   1090	1116	390	java/lang/Throwable
    //   1124	1132	390	java/lang/Throwable
    //   1165	1173	390	java/lang/Throwable
    //   1181	1193	390	java/lang/Throwable
    //   1201	1206	390	java/lang/Throwable
    //   1217	1225	390	java/lang/Throwable
    //   429	438	440	java/lang/Throwable
    //   341	350	452	java/lang/Throwable
    //   546	555	557	java/lang/Throwable
    //   928	937	939	java/lang/Throwable
    //   991	1000	1002	java/lang/Throwable
    //   1232	1241	1243	java/lang/Throwable
    //   1139	1148	1255	java/lang/Throwable
    //   411	420	1267	java/lang/Throwable
    //   37	45	1280	finally
    //   53	59	1280	finally
    //   67	74	1280	finally
    //   82	89	1280	finally
    //   97	106	1280	finally
    //   168	177	1280	finally
    //   185	192	1280	finally
    //   200	207	1280	finally
    //   215	222	1280	finally
    //   230	250	1280	finally
    //   258	265	1280	finally
    //   273	280	1280	finally
    //   288	293	1280	finally
    //   304	312	1280	finally
    //   320	329	1280	finally
    //   378	387	1280	finally
    //   395	404	1280	finally
    //   472	479	1280	finally
    //   487	497	1280	finally
    //   509	516	1280	finally
    //   524	533	1280	finally
    //   577	584	1280	finally
    //   596	607	1280	finally
    //   615	625	1280	finally
    //   652	659	1280	finally
    //   667	679	1280	finally
    //   687	713	1280	finally
    //   721	729	1280	finally
    //   737	744	1280	finally
    //   752	760	1280	finally
    //   768	785	1280	finally
    //   793	802	1280	finally
    //   810	817	1280	finally
    //   825	832	1280	finally
    //   840	850	1280	finally
    //   858	868	1280	finally
    //   876	888	1280	finally
    //   896	905	1280	finally
    //   913	921	1280	finally
    //   959	984	1280	finally
    //   1034	1042	1280	finally
    //   1050	1057	1280	finally
    //   1065	1077	1280	finally
    //   1090	1116	1280	finally
    //   1124	1132	1280	finally
    //   1165	1173	1280	finally
    //   1181	1193	1280	finally
    //   1201	1206	1280	finally
    //   1217	1225	1280	finally
    //   1288	1297	1299	java/lang/Throwable
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/ImageUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */