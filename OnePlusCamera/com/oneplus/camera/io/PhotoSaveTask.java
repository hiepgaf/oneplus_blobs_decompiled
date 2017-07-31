package com.oneplus.camera.io;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.location.Location;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.MediaStore.Images.Media;
import android.util.Rational;
import android.util.Size;
import com.oneplus.base.Log;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraCaptureEventArgs;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.camera.media.ImagePlane;
import com.oneplus.io.Path;
import com.oneplus.media.EncodedImage;
import com.oneplus.media.ImageUtils;
import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PhotoSaveTask
  extends MediaSaveTask
{
  private static final String INTENT_NEW_PICTURE = "com.oneplus.camera.intent.action.NEW_PICTURE";
  private static final int THUMBNAIL_SIZE = 200;
  private Bitmap m_Bitmap;
  protected final CameraCaptureEventArgs m_CaptureEventArgs;
  private final Context m_Context;
  private byte[] m_EncodedPicture;
  private Map<String, String> m_ExifTags;
  private Long m_MediaSize;
  private final String m_PictureId;
  private final long m_TakenTime;
  private YuvImage m_YuvImage;
  private Rect m_YuvImageBounds;
  
  public PhotoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, Bitmap paramBitmap, CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    this(paramContext, paramCaptureHandle, paramBitmap, new HashMap(), paramCameraCaptureEventArgs);
  }
  
  public PhotoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, Bitmap paramBitmap, Map<String, String> paramMap, CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    this(paramContext, paramCaptureHandle, paramCameraCaptureEventArgs);
    this.m_Bitmap = paramBitmap;
    this.m_ExifTags = paramMap;
  }
  
  public PhotoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, YuvImage paramYuvImage, Rect paramRect, Map<String, String> paramMap, CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    this(paramContext, paramCaptureHandle, paramCameraCaptureEventArgs);
    this.m_YuvImage = paramYuvImage;
    if (paramRect != null) {}
    for (paramContext = new Rect(paramRect);; paramContext = new Rect(0, 0, paramYuvImage.getWidth(), paramYuvImage.getHeight()))
    {
      this.m_YuvImageBounds = paramContext;
      this.m_ExifTags = paramMap;
      return;
    }
  }
  
  public PhotoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    super(paramContext, paramCaptureHandle);
    this.m_Context = paramContext;
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
  
  public PhotoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, byte[] paramArrayOfByte, CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    this(paramContext, paramCaptureHandle, paramArrayOfByte, null, paramCameraCaptureEventArgs);
  }
  
  public PhotoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, byte[] paramArrayOfByte, Map<String, String> paramMap, CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    this(paramContext, paramCaptureHandle, paramCameraCaptureEventArgs);
    this.m_EncodedPicture = paramArrayOfByte;
    this.m_ExifTags = paramMap;
  }
  
  private void fillToExif()
  {
    if (this.m_ExifTags == null) {
      return;
    }
    Object localObject1 = getSystemPropertyString("ro.product.manufacturer");
    if (localObject1 != null) {
      insertTags("Make", (String)localObject1);
    }
    localObject1 = getSystemPropertyString("ro.product.model");
    if (localObject1 != null) {
      insertTags("Model", (String)localObject1);
    }
    localObject1 = getSystemPropertyString("ro.build.description");
    if (localObject1 != null) {
      insertTags("Software", (String)localObject1);
    }
    localObject1 = getLocation();
    if (localObject1 == null) {
      return;
    }
    insertTags("GPSLatitude", toTagGPSFormat(((Location)localObject1).getLatitude()));
    insertTags("GPSLongitude", toTagGPSFormat(((Location)localObject1).getLongitude()));
    if (((Location)localObject1).hasAltitude()) {
      insertTags("GPSAltitude", String.valueOf(((Location)localObject1).getAltitude()));
    }
    Object localObject2;
    if (((Location)localObject1).getTime() > 0L)
    {
      localObject2 = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
      ((SimpleDateFormat)localObject2).setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    try
    {
      localObject2 = ((SimpleDateFormat)localObject2).format(Long.valueOf(((Location)localObject1).getTime())).split(" ");
      localObject1 = localObject2[0];
      localObject2 = localObject2[1];
      insertTags("GPSDateStamp", (String)localObject1);
      insertTags("GPSTimeStamp", (String)localObject2);
      Log.v(this.TAG, "fillToExif() - UTC date: ", localObject1, ", time: ", localObject2);
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "fillToExif() - Cannot insert location time", localThrowable);
    }
  }
  
  /* Error */
  private Size getPictureSize()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 6
    //   6: aconst_null
    //   7: astore_3
    //   8: aconst_null
    //   9: astore 7
    //   11: aconst_null
    //   12: astore_2
    //   13: aconst_null
    //   14: astore 5
    //   16: aload 5
    //   18: astore_1
    //   19: aload_0
    //   20: getfield 47	com/oneplus/camera/io/PhotoSaveTask:m_Bitmap	Landroid/graphics/Bitmap;
    //   23: ifnull +28 -> 51
    //   26: aload 5
    //   28: astore_1
    //   29: new 233	android/util/Size
    //   32: dup
    //   33: aload_0
    //   34: getfield 47	com/oneplus/camera/io/PhotoSaveTask:m_Bitmap	Landroid/graphics/Bitmap;
    //   37: invokevirtual 236	android/graphics/Bitmap:getWidth	()I
    //   40: aload_0
    //   41: getfield 47	com/oneplus/camera/io/PhotoSaveTask:m_Bitmap	Landroid/graphics/Bitmap;
    //   44: invokevirtual 237	android/graphics/Bitmap:getHeight	()I
    //   47: invokespecial 240	android/util/Size:<init>	(II)V
    //   50: areturn
    //   51: aload 5
    //   53: astore_1
    //   54: aload_0
    //   55: getfield 110	com/oneplus/camera/io/PhotoSaveTask:m_EncodedPicture	[B
    //   58: astore 8
    //   60: aload 8
    //   62: ifnull +130 -> 192
    //   65: aconst_null
    //   66: astore_3
    //   67: aconst_null
    //   68: astore_1
    //   69: new 242	java/io/ByteArrayInputStream
    //   72: dup
    //   73: aload_0
    //   74: getfield 110	com/oneplus/camera/io/PhotoSaveTask:m_EncodedPicture	[B
    //   77: invokespecial 245	java/io/ByteArrayInputStream:<init>	([B)V
    //   80: astore_2
    //   81: aload_2
    //   82: bipush 32
    //   84: invokestatic 251	com/oneplus/media/ImageUtils:decodeSize	(Ljava/io/InputStream;I)Landroid/util/Size;
    //   87: astore_1
    //   88: aload 7
    //   90: astore_3
    //   91: aload_2
    //   92: ifnull +10 -> 102
    //   95: aload_2
    //   96: invokevirtual 254	java/io/ByteArrayInputStream:close	()V
    //   99: aload 7
    //   101: astore_3
    //   102: aload_1
    //   103: astore_2
    //   104: aload_3
    //   105: ifnull +405 -> 510
    //   108: aload_3
    //   109: athrow
    //   110: astore_2
    //   111: aload_0
    //   112: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   115: ldc_w 256
    //   118: aload_2
    //   119: invokestatic 229	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   122: aload_1
    //   123: areturn
    //   124: astore_3
    //   125: goto -23 -> 102
    //   128: astore_2
    //   129: aload_2
    //   130: athrow
    //   131: astore 4
    //   133: aload_2
    //   134: astore_3
    //   135: aload 4
    //   137: astore_2
    //   138: aload_3
    //   139: astore 4
    //   141: aload_1
    //   142: ifnull +10 -> 152
    //   145: aload_1
    //   146: invokevirtual 254	java/io/ByteArrayInputStream:close	()V
    //   149: aload_3
    //   150: astore 4
    //   152: aload 4
    //   154: ifnull +33 -> 187
    //   157: aload 5
    //   159: astore_1
    //   160: aload 4
    //   162: athrow
    //   163: aload_3
    //   164: astore 4
    //   166: aload_3
    //   167: aload 6
    //   169: if_acmpeq -17 -> 152
    //   172: aload 5
    //   174: astore_1
    //   175: aload_3
    //   176: aload 6
    //   178: invokevirtual 260	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   181: aload_3
    //   182: astore 4
    //   184: goto -32 -> 152
    //   187: aload 5
    //   189: astore_1
    //   190: aload_2
    //   191: athrow
    //   192: aload 5
    //   194: astore_1
    //   195: aload_0
    //   196: getfield 54	com/oneplus/camera/io/PhotoSaveTask:m_YuvImage	Landroid/graphics/YuvImage;
    //   199: ifnull +28 -> 227
    //   202: aload 5
    //   204: astore_1
    //   205: new 233	android/util/Size
    //   208: dup
    //   209: aload_0
    //   210: getfield 54	com/oneplus/camera/io/PhotoSaveTask:m_YuvImage	Landroid/graphics/YuvImage;
    //   213: invokevirtual 67	android/graphics/YuvImage:getWidth	()I
    //   216: aload_0
    //   217: getfield 54	com/oneplus/camera/io/PhotoSaveTask:m_YuvImage	Landroid/graphics/YuvImage;
    //   220: invokevirtual 70	android/graphics/YuvImage:getHeight	()I
    //   223: invokespecial 240	android/util/Size:<init>	(II)V
    //   226: areturn
    //   227: aload 5
    //   229: astore_1
    //   230: aload_0
    //   231: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   234: ifnull +208 -> 442
    //   237: aload 5
    //   239: astore_1
    //   240: aload_0
    //   241: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   244: invokevirtual 262	com/oneplus/camera/CameraCaptureEventArgs:getPictureSize	()Landroid/util/Size;
    //   247: ifnull +263 -> 510
    //   250: aload 5
    //   252: astore_1
    //   253: aload_0
    //   254: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   257: invokevirtual 266	com/oneplus/camera/CameraCaptureEventArgs:getPicturePlanes	()[Lcom/oneplus/camera/media/ImagePlane;
    //   260: astore_2
    //   261: aload 5
    //   263: astore_1
    //   264: aload_0
    //   265: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   268: invokevirtual 269	com/oneplus/camera/CameraCaptureEventArgs:getPictureFormat	()I
    //   271: sipush 256
    //   274: if_icmpne +137 -> 411
    //   277: aload_2
    //   278: ifnull +133 -> 411
    //   281: aload 5
    //   283: astore_1
    //   284: aload_2
    //   285: arraylength
    //   286: iconst_1
    //   287: if_icmpne +124 -> 411
    //   290: aload 5
    //   292: astore_1
    //   293: aload_2
    //   294: iconst_0
    //   295: aaload
    //   296: invokevirtual 275	com/oneplus/camera/media/ImagePlane:getData	()[B
    //   299: astore_2
    //   300: aconst_null
    //   301: astore 4
    //   303: aconst_null
    //   304: astore_1
    //   305: new 242	java/io/ByteArrayInputStream
    //   308: dup
    //   309: aload_2
    //   310: invokespecial 245	java/io/ByteArrayInputStream:<init>	([B)V
    //   313: astore_2
    //   314: aload_2
    //   315: bipush 32
    //   317: invokestatic 251	com/oneplus/media/ImageUtils:decodeSize	(Ljava/io/InputStream;I)Landroid/util/Size;
    //   320: astore_1
    //   321: aload 6
    //   323: astore_3
    //   324: aload_2
    //   325: ifnull +10 -> 335
    //   328: aload_2
    //   329: invokevirtual 254	java/io/ByteArrayInputStream:close	()V
    //   332: aload 6
    //   334: astore_3
    //   335: aload_1
    //   336: astore_2
    //   337: aload_3
    //   338: ifnull +172 -> 510
    //   341: aload_3
    //   342: athrow
    //   343: astore_3
    //   344: goto -9 -> 335
    //   347: astore_2
    //   348: aload_2
    //   349: athrow
    //   350: astore 4
    //   352: aload_2
    //   353: astore_3
    //   354: aload 4
    //   356: astore_2
    //   357: aload_3
    //   358: astore 4
    //   360: aload_1
    //   361: ifnull +10 -> 371
    //   364: aload_1
    //   365: invokevirtual 254	java/io/ByteArrayInputStream:close	()V
    //   368: aload_3
    //   369: astore 4
    //   371: aload 4
    //   373: ifnull +33 -> 406
    //   376: aload 5
    //   378: astore_1
    //   379: aload 4
    //   381: athrow
    //   382: aload_3
    //   383: astore 4
    //   385: aload_3
    //   386: aload 6
    //   388: if_acmpeq -17 -> 371
    //   391: aload 5
    //   393: astore_1
    //   394: aload_3
    //   395: aload 6
    //   397: invokevirtual 260	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   400: aload_3
    //   401: astore 4
    //   403: goto -32 -> 371
    //   406: aload 5
    //   408: astore_1
    //   409: aload_2
    //   410: athrow
    //   411: aload 5
    //   413: astore_1
    //   414: new 233	android/util/Size
    //   417: dup
    //   418: aload_0
    //   419: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   422: invokevirtual 262	com/oneplus/camera/CameraCaptureEventArgs:getPictureSize	()Landroid/util/Size;
    //   425: invokevirtual 276	android/util/Size:getWidth	()I
    //   428: aload_0
    //   429: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   432: invokevirtual 262	com/oneplus/camera/CameraCaptureEventArgs:getPictureSize	()Landroid/util/Size;
    //   435: invokevirtual 277	android/util/Size:getHeight	()I
    //   438: invokespecial 240	android/util/Size:<init>	(II)V
    //   441: areturn
    //   442: aload 5
    //   444: astore_1
    //   445: aload_0
    //   446: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   449: ldc_w 279
    //   452: invokestatic 282	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   455: aconst_null
    //   456: areturn
    //   457: astore_2
    //   458: aload 4
    //   460: astore_1
    //   461: goto -104 -> 357
    //   464: astore 4
    //   466: aload_2
    //   467: astore_1
    //   468: aload 4
    //   470: astore_2
    //   471: goto -114 -> 357
    //   474: astore_3
    //   475: aload_2
    //   476: astore_1
    //   477: aload_3
    //   478: astore_2
    //   479: goto -131 -> 348
    //   482: astore_2
    //   483: aload_3
    //   484: astore_1
    //   485: aload 4
    //   487: astore_3
    //   488: goto -350 -> 138
    //   491: astore_3
    //   492: aload_2
    //   493: astore_1
    //   494: aload_3
    //   495: astore_2
    //   496: aload 4
    //   498: astore_3
    //   499: goto -361 -> 138
    //   502: astore_3
    //   503: aload_2
    //   504: astore_1
    //   505: aload_3
    //   506: astore_2
    //   507: goto -378 -> 129
    //   510: aload_2
    //   511: areturn
    //   512: astore 6
    //   514: aload_3
    //   515: ifnonnull -352 -> 163
    //   518: aload 6
    //   520: astore 4
    //   522: goto -370 -> 152
    //   525: astore 6
    //   527: aload_3
    //   528: ifnonnull -146 -> 382
    //   531: aload 6
    //   533: astore 4
    //   535: goto -164 -> 371
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	538	0	this	PhotoSaveTask
    //   18	487	1	localObject1	Object
    //   12	92	2	localObject2	Object
    //   110	9	2	localThrowable1	Throwable
    //   128	6	2	localThrowable2	Throwable
    //   137	200	2	localObject3	Object
    //   347	6	2	localThrowable3	Throwable
    //   356	54	2	localObject4	Object
    //   457	10	2	localObject5	Object
    //   470	9	2	localObject6	Object
    //   482	11	2	localObject7	Object
    //   495	16	2	localObject8	Object
    //   7	102	3	localObject9	Object
    //   124	1	3	localThrowable4	Throwable
    //   134	208	3	localObject10	Object
    //   343	1	3	localThrowable5	Throwable
    //   353	48	3	localObject11	Object
    //   474	10	3	localThrowable6	Throwable
    //   487	1	3	localObject12	Object
    //   491	4	3	localObject13	Object
    //   498	1	3	localObject14	Object
    //   502	26	3	localThrowable7	Throwable
    //   1	1	4	localObject15	Object
    //   131	5	4	localObject16	Object
    //   139	163	4	localObject17	Object
    //   350	5	4	localObject18	Object
    //   358	101	4	localObject19	Object
    //   464	33	4	localObject20	Object
    //   520	14	4	localObject21	Object
    //   14	429	5	localObject22	Object
    //   4	392	6	localThrowable8	Throwable
    //   512	7	6	localThrowable9	Throwable
    //   525	7	6	localThrowable10	Throwable
    //   9	91	7	localObject23	Object
    //   58	3	8	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   19	26	110	java/lang/Throwable
    //   29	51	110	java/lang/Throwable
    //   54	60	110	java/lang/Throwable
    //   108	110	110	java/lang/Throwable
    //   160	163	110	java/lang/Throwable
    //   175	181	110	java/lang/Throwable
    //   190	192	110	java/lang/Throwable
    //   195	202	110	java/lang/Throwable
    //   205	227	110	java/lang/Throwable
    //   230	237	110	java/lang/Throwable
    //   240	250	110	java/lang/Throwable
    //   253	261	110	java/lang/Throwable
    //   264	277	110	java/lang/Throwable
    //   284	290	110	java/lang/Throwable
    //   293	300	110	java/lang/Throwable
    //   341	343	110	java/lang/Throwable
    //   379	382	110	java/lang/Throwable
    //   394	400	110	java/lang/Throwable
    //   409	411	110	java/lang/Throwable
    //   414	442	110	java/lang/Throwable
    //   445	455	110	java/lang/Throwable
    //   95	99	124	java/lang/Throwable
    //   69	81	128	java/lang/Throwable
    //   129	131	131	finally
    //   328	332	343	java/lang/Throwable
    //   305	314	347	java/lang/Throwable
    //   348	350	350	finally
    //   305	314	457	finally
    //   314	321	464	finally
    //   314	321	474	java/lang/Throwable
    //   69	81	482	finally
    //   81	88	491	finally
    //   81	88	502	java/lang/Throwable
    //   145	149	512	java/lang/Throwable
    //   364	368	525	java/lang/Throwable
  }
  
  private String getSystemPropertyString(String paramString)
    throws IllegalArgumentException
  {
    if (this.m_Context == null) {
      return null;
    }
    try
    {
      Class localClass = this.m_Context.getClassLoader().loadClass("android.os.SystemProperties");
      paramString = (String)localClass.getMethod("get", new Class[] { String.class }).invoke(localClass, new Object[] { new String(paramString) });
      return paramString;
    }
    catch (Exception paramString)
    {
      return "";
    }
    catch (IllegalArgumentException paramString)
    {
      throw paramString;
    }
  }
  
  private void insertTags(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null)) {
      this.m_ExifTags.put(paramString1, paramString2);
    }
  }
  
  private String toTagGPSFormat(double paramDouble)
  {
    String str1 = Rational.NaN + "," + Rational.NaN + "," + Rational.NaN;
    if (Double.isNaN(paramDouble)) {
      return str1;
    }
    Object localObject = Double.toString(paramDouble);
    int k = ((String)localObject).indexOf('.');
    int i;
    String str2;
    if (k >= 0)
    {
      i = 1;
      str2 = str1;
    }
    for (;;)
    {
      int j;
      try
      {
        j = ((String)localObject).length() - 1;
      }
      catch (Throwable localThrowable)
      {
        return str2;
      }
      str2 = str1;
      localObject = new Rational((int)(i * paramDouble + 0.5D), i);
      str2 = str1;
      str1 = localObject + ",0/1,0/1";
      str2 = str1;
      Log.v(this.TAG, "toTagGPSFormat() - Converted value : ", str1);
      return str1;
      str2 = str1;
      localObject = Rational.parseRational((String)localObject);
      str2 = str1;
      str1 = localObject + ",0/1,0/1";
      continue;
      while ((j > k) && (i < 1000000))
      {
        i *= 10;
        j -= 1;
      }
    }
  }
  
  public byte[] getEncodedPicture()
  {
    return this.m_EncodedPicture;
  }
  
  public long getMediaSize()
  {
    if (this.m_MediaSize == null) {}
    try
    {
      if (this.m_Bitmap != null) {}
      for (this.m_MediaSize = Long.valueOf(this.m_Bitmap.getByteCount());; this.m_MediaSize = Long.valueOf(this.m_EncodedPicture.length))
      {
        return this.m_MediaSize.longValue();
        if (this.m_EncodedPicture == null) {
          break;
        }
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        this.m_MediaSize = Long.valueOf(0L);
        continue;
        if (this.m_YuvImage != null)
        {
          this.m_MediaSize = Long.valueOf(this.m_YuvImage.getWidth() * this.m_YuvImage.getHeight() * 3 / 2);
        }
        else if (this.m_CaptureEventArgs != null)
        {
          ImagePlane[] arrayOfImagePlane = this.m_CaptureEventArgs.getPicturePlanes();
          long l = 0L;
          int i = arrayOfImagePlane.length - 1;
          while (i >= 0)
          {
            l += arrayOfImagePlane[i].getData().length;
            i -= 1;
          }
          this.m_MediaSize = Long.valueOf(l);
        }
        else
        {
          this.m_MediaSize = Long.valueOf(0L);
        }
      }
    }
  }
  
  public String getPictureId()
  {
    return this.m_PictureId;
  }
  
  /* Error */
  public Bitmap getThumbnail()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aload_0
    //   6: getfield 392	com/oneplus/camera/io/PhotoSaveTask:m_Thumbnail	Landroid/graphics/Bitmap;
    //   9: ifnull +8 -> 17
    //   12: aload_0
    //   13: getfield 392	com/oneplus/camera/io/PhotoSaveTask:m_Thumbnail	Landroid/graphics/Bitmap;
    //   16: areturn
    //   17: aload_0
    //   18: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   21: ldc_w 394
    //   24: invokestatic 396	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   27: aconst_null
    //   28: astore 5
    //   30: aconst_null
    //   31: astore_2
    //   32: new 398	java/io/ByteArrayOutputStream
    //   35: dup
    //   36: invokespecial 399	java/io/ByteArrayOutputStream:<init>	()V
    //   39: astore_1
    //   40: aload_0
    //   41: getfield 47	com/oneplus/camera/io/PhotoSaveTask:m_Bitmap	Landroid/graphics/Bitmap;
    //   44: ifnull +115 -> 159
    //   47: aload_0
    //   48: getfield 47	com/oneplus/camera/io/PhotoSaveTask:m_Bitmap	Landroid/graphics/Bitmap;
    //   51: invokevirtual 236	android/graphics/Bitmap:getWidth	()I
    //   54: aload_0
    //   55: getfield 47	com/oneplus/camera/io/PhotoSaveTask:m_Bitmap	Landroid/graphics/Bitmap;
    //   58: invokevirtual 237	android/graphics/Bitmap:getHeight	()I
    //   61: sipush 200
    //   64: sipush 200
    //   67: iconst_1
    //   68: invokestatic 405	com/oneplus/util/SizeUtils:getRatioStretchedSize	(IIIIZ)Landroid/util/Size;
    //   71: astore_2
    //   72: aload_0
    //   73: aload_0
    //   74: getfield 47	com/oneplus/camera/io/PhotoSaveTask:m_Bitmap	Landroid/graphics/Bitmap;
    //   77: aload_2
    //   78: invokevirtual 276	android/util/Size:getWidth	()I
    //   81: aload_2
    //   82: invokevirtual 277	android/util/Size:getHeight	()I
    //   85: iconst_1
    //   86: invokestatic 409	android/graphics/Bitmap:createScaledBitmap	(Landroid/graphics/Bitmap;IIZ)Landroid/graphics/Bitmap;
    //   89: putfield 392	com/oneplus/camera/io/PhotoSaveTask:m_Thumbnail	Landroid/graphics/Bitmap;
    //   92: aload 4
    //   94: astore_2
    //   95: aload_1
    //   96: ifnull +10 -> 106
    //   99: aload_1
    //   100: invokevirtual 410	java/io/ByteArrayOutputStream:close	()V
    //   103: aload 4
    //   105: astore_2
    //   106: aload_2
    //   107: ifnull +303 -> 410
    //   110: aload_2
    //   111: athrow
    //   112: astore_1
    //   113: aload_0
    //   114: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   117: new 328	java/lang/StringBuilder
    //   120: dup
    //   121: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   124: ldc_w 412
    //   127: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   130: aload_0
    //   131: getfield 392	com/oneplus/camera/io/PhotoSaveTask:m_Thumbnail	Landroid/graphics/Bitmap;
    //   134: invokevirtual 339	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   137: invokevirtual 347	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   140: aload_1
    //   141: invokestatic 229	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   144: aload_0
    //   145: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   148: ldc_w 414
    //   151: invokestatic 396	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   154: aload_0
    //   155: getfield 392	com/oneplus/camera/io/PhotoSaveTask:m_Thumbnail	Landroid/graphics/Bitmap;
    //   158: areturn
    //   159: aload_0
    //   160: getfield 110	com/oneplus/camera/io/PhotoSaveTask:m_EncodedPicture	[B
    //   163: ifnull +59 -> 222
    //   166: aload_0
    //   167: aload_0
    //   168: getfield 110	com/oneplus/camera/io/PhotoSaveTask:m_EncodedPicture	[B
    //   171: sipush 200
    //   174: sipush 200
    //   177: invokestatic 418	com/oneplus/media/ImageUtils:decodeBitmap	([BII)Landroid/graphics/Bitmap;
    //   180: putfield 392	com/oneplus/camera/io/PhotoSaveTask:m_Thumbnail	Landroid/graphics/Bitmap;
    //   183: goto -91 -> 92
    //   186: astore_2
    //   187: aload_2
    //   188: athrow
    //   189: astore 4
    //   191: aload_2
    //   192: astore_3
    //   193: aload 4
    //   195: astore_2
    //   196: aload_3
    //   197: astore 4
    //   199: aload_1
    //   200: ifnull +10 -> 210
    //   203: aload_1
    //   204: invokevirtual 410	java/io/ByteArrayOutputStream:close	()V
    //   207: aload_3
    //   208: astore 4
    //   210: aload 4
    //   212: ifnull +196 -> 408
    //   215: aload 4
    //   217: athrow
    //   218: astore_1
    //   219: goto -106 -> 113
    //   222: aload_0
    //   223: getfield 54	com/oneplus/camera/io/PhotoSaveTask:m_YuvImage	Landroid/graphics/YuvImage;
    //   226: ifnull +38 -> 264
    //   229: aload_0
    //   230: getfield 54	com/oneplus/camera/io/PhotoSaveTask:m_YuvImage	Landroid/graphics/YuvImage;
    //   233: aload_0
    //   234: getfield 61	com/oneplus/camera/io/PhotoSaveTask:m_YuvImageBounds	Landroid/graphics/Rect;
    //   237: bipush 90
    //   239: aload_1
    //   240: invokevirtual 422	android/graphics/YuvImage:compressToJpeg	(Landroid/graphics/Rect;ILjava/io/OutputStream;)Z
    //   243: pop
    //   244: aload_0
    //   245: aload_1
    //   246: invokevirtual 425	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   249: sipush 200
    //   252: sipush 200
    //   255: invokestatic 418	com/oneplus/media/ImageUtils:decodeBitmap	([BII)Landroid/graphics/Bitmap;
    //   258: putfield 392	com/oneplus/camera/io/PhotoSaveTask:m_Thumbnail	Landroid/graphics/Bitmap;
    //   261: goto -169 -> 92
    //   264: aload_0
    //   265: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   268: ifnull +80 -> 348
    //   271: aload_0
    //   272: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   275: invokevirtual 269	com/oneplus/camera/CameraCaptureEventArgs:getPictureFormat	()I
    //   278: sipush 256
    //   281: if_icmpne +31 -> 312
    //   284: aload_0
    //   285: aload_0
    //   286: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   289: invokevirtual 266	com/oneplus/camera/CameraCaptureEventArgs:getPicturePlanes	()[Lcom/oneplus/camera/media/ImagePlane;
    //   292: iconst_0
    //   293: aaload
    //   294: invokevirtual 275	com/oneplus/camera/media/ImagePlane:getData	()[B
    //   297: sipush 200
    //   300: sipush 200
    //   303: invokestatic 418	com/oneplus/media/ImageUtils:decodeBitmap	([BII)Landroid/graphics/Bitmap;
    //   306: putfield 392	com/oneplus/camera/io/PhotoSaveTask:m_Thumbnail	Landroid/graphics/Bitmap;
    //   309: goto -217 -> 92
    //   312: aload_0
    //   313: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   316: new 328	java/lang/StringBuilder
    //   319: dup
    //   320: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   323: ldc_w 427
    //   326: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   329: aload_0
    //   330: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   333: invokevirtual 269	com/oneplus/camera/CameraCaptureEventArgs:getPictureFormat	()I
    //   336: invokevirtual 430	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   339: invokevirtual 347	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   342: invokestatic 432	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   345: goto -253 -> 92
    //   348: aload_0
    //   349: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   352: ldc_w 434
    //   355: invokestatic 432	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   358: goto -266 -> 92
    //   361: astore_2
    //   362: goto -256 -> 106
    //   365: astore_1
    //   366: aload_3
    //   367: ifnonnull +9 -> 376
    //   370: aload_1
    //   371: astore 4
    //   373: goto -163 -> 210
    //   376: aload_3
    //   377: astore 4
    //   379: aload_3
    //   380: aload_1
    //   381: if_acmpeq -171 -> 210
    //   384: aload_3
    //   385: aload_1
    //   386: invokevirtual 260	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   389: aload_3
    //   390: astore 4
    //   392: goto -182 -> 210
    //   395: astore_1
    //   396: aload_0
    //   397: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   400: ldc_w 414
    //   403: invokestatic 396	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   406: aload_1
    //   407: athrow
    //   408: aload_2
    //   409: athrow
    //   410: aload_0
    //   411: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   414: ldc_w 414
    //   417: invokestatic 396	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   420: goto -266 -> 154
    //   423: astore_1
    //   424: goto -28 -> 396
    //   427: astore_2
    //   428: aload 5
    //   430: astore_1
    //   431: goto -235 -> 196
    //   434: astore_3
    //   435: aload_2
    //   436: astore_1
    //   437: aload_3
    //   438: astore_2
    //   439: goto -252 -> 187
    //   442: astore_2
    //   443: goto -247 -> 196
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	446	0	this	PhotoSaveTask
    //   39	61	1	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   112	92	1	localThrowable1	Throwable
    //   218	28	1	localThrowable2	Throwable
    //   365	21	1	localThrowable3	Throwable
    //   395	12	1	localObject1	Object
    //   423	1	1	localObject2	Object
    //   430	7	1	localObject3	Object
    //   31	80	2	localObject4	Object
    //   186	6	2	localThrowable4	Throwable
    //   195	1	2	localObject5	Object
    //   361	48	2	localThrowable5	Throwable
    //   427	9	2	localObject6	Object
    //   438	1	2	localThrowable6	Throwable
    //   442	1	2	localObject7	Object
    //   1	389	3	localObject8	Object
    //   434	4	3	localThrowable7	Throwable
    //   3	101	4	localObject9	Object
    //   189	5	4	localObject10	Object
    //   197	194	4	localObject11	Object
    //   28	401	5	localObject12	Object
    // Exception table:
    //   from	to	target	type
    //   110	112	112	java/lang/Throwable
    //   40	92	186	java/lang/Throwable
    //   159	183	186	java/lang/Throwable
    //   222	261	186	java/lang/Throwable
    //   264	309	186	java/lang/Throwable
    //   312	345	186	java/lang/Throwable
    //   348	358	186	java/lang/Throwable
    //   187	189	189	finally
    //   215	218	218	java/lang/Throwable
    //   384	389	218	java/lang/Throwable
    //   408	410	218	java/lang/Throwable
    //   99	103	361	java/lang/Throwable
    //   203	207	365	java/lang/Throwable
    //   113	144	395	finally
    //   203	207	395	finally
    //   215	218	395	finally
    //   384	389	395	finally
    //   408	410	395	finally
    //   99	103	423	finally
    //   110	112	423	finally
    //   32	40	427	finally
    //   32	40	434	java/lang/Throwable
    //   40	92	442	finally
    //   159	183	442	finally
    //   222	261	442	finally
    //   264	309	442	finally
    //   312	345	442	finally
    //   348	358	442	finally
  }
  
  public boolean insertToMediaStore()
  {
    if (!super.insertToMediaStore()) {
      return false;
    }
    Uri localUri = getContentUri();
    if (localUri != null)
    {
      boolean bool = false;
      if ((this.m_Context instanceof CameraActivity)) {
        bool = ((CameraActivity)this.m_Context).isServiceMode();
      }
      if (!bool) {
        if (Build.VERSION.SDK_INT >= 24) {
          break label110;
        }
      }
    }
    label110:
    for (Object localObject = "android.hardware.action.NEW_PICTURE";; localObject = "com.oneplus.camera.intent.action.NEW_PICTURE")
    {
      localObject = new Intent((String)localObject);
      ((Intent)localObject).setData(localUri);
      if ((this.m_Context instanceof CameraActivity)) {
        ((Intent)localObject).putExtra("CameraActivity.InstanceId", ((CameraActivity)this.m_Context).getInstanceId());
      }
      this.m_Context.sendBroadcast((Intent)localObject);
      return true;
    }
  }
  
  protected String onGenerateFilePath(boolean paramBoolean)
  {
    File localFile = new File(Path.combine(new String[] { getDcimPath(), "Camera" }));
    String str1;
    String str2;
    Object localObject2;
    int i;
    if ((localFile.exists()) || (localFile.mkdirs()))
    {
      str1 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(this.m_TakenTime));
      str2 = getFilePathSuffix();
      if (str2 != null) {
        break label255;
      }
      localObject1 = "IMG_" + str1 + ".jpg";
      localObject1 = new File(localFile, (String)localObject1);
      localObject2 = localObject1;
      if (!paramBoolean) {
        break label349;
      }
      i = 1;
      label130:
      localObject2 = localObject1;
      if (!((File)localObject1).exists()) {
        break label349;
      }
      localObject1 = String.format(Locale.US, "%02d", new Object[] { Integer.valueOf(i) });
      if (str2 != null) {
        break label297;
      }
    }
    label255:
    label297:
    for (Object localObject1 = "IMG_" + str1 + "_" + (String)localObject1 + ".jpg";; localObject1 = "IMG_" + str1 + "_" + (String)localObject1 + "_" + str2 + ".jpg")
    {
      localObject1 = new File(localFile, (String)localObject1);
      i += 1;
      break label130;
      Log.e(this.TAG, "onGenerateFilePath() - Fail to create " + localFile.getAbsolutePath());
      return null;
      localObject1 = "IMG_" + str1 + "_" + str2 + ".jpg";
      break;
    }
    label349:
    Log.w(this.TAG, "onGenerateFilePath() - File path : " + localObject2);
    return ((File)localObject2).getAbsolutePath();
  }
  
  protected void onImageEncoded(EncodedImage paramEncodedImage) {}
  
  protected Uri onInsertToMediaStore(String paramString, ContentValues paramContentValues)
  {
    Log.v(this.TAG, "onInsertToMediaStore() - File path: ", paramString, ", prepared values: ", paramContentValues);
    paramString = this.m_Context.getContentResolver().acquireUnstableContentProviderClient(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    if (paramString != null) {
      try
      {
        paramContentValues = paramString.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, paramContentValues);
        return paramContentValues;
      }
      catch (Throwable paramContentValues)
      {
        Log.e(this.TAG, "onInsertToMediaStore() - Fail to insert", paramContentValues);
        return null;
      }
      finally
      {
        paramString.release();
      }
    }
    Log.e(this.TAG, "onInsertToMediaStore() - Fail to acquire client");
    return null;
  }
  
  protected boolean onPrepareGalleryDatabaseValues(String paramString, Uri paramUri, ContentValues paramContentValues)
  {
    if (getLensFacing() == Camera.LensFacing.FRONT)
    {
      paramContentValues.put("oneplus_flags", Integer.valueOf(1));
      return true;
    }
    return false;
  }
  
  protected boolean onPrepareMediaStoreValues(String paramString, ContentValues paramContentValues)
  {
    paramContentValues.put("title", Path.getFileNameWithoutExtension(paramString));
    paramContentValues.put("description", Path.getFileName(paramString));
    paramContentValues.put("mime_type", "image/jpeg");
    Object localObject = getLocation();
    if (localObject != null)
    {
      paramContentValues.put("latitude", Double.valueOf(((Location)localObject).getLatitude()));
      paramContentValues.put("longitude", Double.valueOf(((Location)localObject).getLongitude()));
      paramContentValues.put("datetaken", Long.valueOf(((Location)localObject).getTime()));
      Log.v(this.TAG, "onPrepareMediaStoreValues() - Taken time: ", Long.valueOf(((Location)localObject).getTime()));
    }
    localObject = getPictureSize();
    if (localObject != null)
    {
      paramContentValues.put("width", Integer.valueOf(((Size)localObject).getWidth()));
      paramContentValues.put("height", Integer.valueOf(((Size)localObject).getHeight()));
    }
    int i = 0;
    if (this.m_ExifTags != null)
    {
      localObject = (String)this.m_ExifTags.get("Orientation");
      if (localObject != null)
      {
        if (!((String)localObject).equals(Integer.toString(6))) {
          break label215;
        }
        i = 90;
        paramContentValues.put("orientation", Integer.valueOf(i));
      }
    }
    for (;;)
    {
      paramContentValues.put("_data", paramString);
      return true;
      label215:
      if (((String)localObject).equals(Integer.toString(3)))
      {
        i = 180;
        break;
      }
      if (!((String)localObject).equals(Integer.toString(8))) {
        break;
      }
      i = 270;
      break;
      paramContentValues.put("orientation", Integer.valueOf(ImageUtils.decodeOrientation(paramString)));
    }
  }
  
  /* Error */
  protected boolean onSaveToFile(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   4: ldc_w 660
    //   7: invokestatic 282	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   10: new 477	java/io/File
    //   13: dup
    //   14: aload_1
    //   15: invokespecial 489	java/io/File:<init>	(Ljava/lang/String;)V
    //   18: astore 10
    //   20: aconst_null
    //   21: astore_2
    //   22: aconst_null
    //   23: astore_3
    //   24: aconst_null
    //   25: astore 6
    //   27: aconst_null
    //   28: astore 7
    //   30: aconst_null
    //   31: astore 5
    //   33: aconst_null
    //   34: astore 8
    //   36: new 662	java/io/FileOutputStream
    //   39: dup
    //   40: aload 10
    //   42: invokespecial 665	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   45: astore 4
    //   47: aload_0
    //   48: getfield 47	com/oneplus/camera/io/PhotoSaveTask:m_Bitmap	Landroid/graphics/Bitmap;
    //   51: astore 5
    //   53: aload 5
    //   55: ifnull +207 -> 262
    //   58: aconst_null
    //   59: astore_3
    //   60: aconst_null
    //   61: astore 8
    //   63: aconst_null
    //   64: astore 9
    //   66: aconst_null
    //   67: astore 5
    //   69: new 398	java/io/ByteArrayOutputStream
    //   72: dup
    //   73: invokespecial 399	java/io/ByteArrayOutputStream:<init>	()V
    //   76: astore_2
    //   77: aload_0
    //   78: getfield 47	com/oneplus/camera/io/PhotoSaveTask:m_Bitmap	Landroid/graphics/Bitmap;
    //   81: getstatic 671	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   84: bipush 90
    //   86: aload_2
    //   87: invokevirtual 675	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   90: pop
    //   91: aload_2
    //   92: invokevirtual 425	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   95: astore 5
    //   97: aload 5
    //   99: astore_3
    //   100: aload 8
    //   102: astore 5
    //   104: aload_2
    //   105: ifnull +11 -> 116
    //   108: aload_2
    //   109: invokevirtual 410	java/io/ByteArrayOutputStream:close	()V
    //   112: aload 8
    //   114: astore 5
    //   116: aload_3
    //   117: astore_2
    //   118: aload 5
    //   120: ifnull +154 -> 274
    //   123: aload 5
    //   125: athrow
    //   126: astore_1
    //   127: aload 4
    //   129: astore_3
    //   130: aload_1
    //   131: athrow
    //   132: astore_2
    //   133: aload_1
    //   134: astore 4
    //   136: aload_3
    //   137: ifnull +10 -> 147
    //   140: aload_3
    //   141: invokevirtual 676	java/io/FileOutputStream:close	()V
    //   144: aload_1
    //   145: astore 4
    //   147: aload 4
    //   149: ifnull +853 -> 1002
    //   152: aload 4
    //   154: athrow
    //   155: astore_1
    //   156: aload_0
    //   157: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   160: new 328	java/lang/StringBuilder
    //   163: dup
    //   164: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   167: ldc_w 678
    //   170: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   173: aload 10
    //   175: invokevirtual 339	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   178: invokevirtual 347	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   181: aload_1
    //   182: invokestatic 229	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   185: aload_0
    //   186: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   189: ifnull +10 -> 199
    //   192: aload_0
    //   193: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   196: invokevirtual 681	com/oneplus/camera/CameraCaptureEventArgs:recycle	()V
    //   199: iconst_0
    //   200: ireturn
    //   201: astore 5
    //   203: goto -87 -> 116
    //   206: astore_2
    //   207: aload 5
    //   209: astore_1
    //   210: aload_2
    //   211: athrow
    //   212: astore 5
    //   214: aload_2
    //   215: astore_3
    //   216: aload 5
    //   218: astore_2
    //   219: aload_3
    //   220: astore 5
    //   222: aload_1
    //   223: ifnull +10 -> 233
    //   226: aload_1
    //   227: invokevirtual 410	java/io/ByteArrayOutputStream:close	()V
    //   230: aload_3
    //   231: astore 5
    //   233: aload 5
    //   235: ifnull +25 -> 260
    //   238: aload 5
    //   240: athrow
    //   241: aload_3
    //   242: astore 5
    //   244: aload_3
    //   245: aload_1
    //   246: if_acmpeq -13 -> 233
    //   249: aload_3
    //   250: aload_1
    //   251: invokevirtual 260	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   254: aload_3
    //   255: astore 5
    //   257: goto -24 -> 233
    //   260: aload_2
    //   261: athrow
    //   262: aload_0
    //   263: getfield 110	com/oneplus/camera/io/PhotoSaveTask:m_EncodedPicture	[B
    //   266: ifnull +53 -> 319
    //   269: aload_0
    //   270: getfield 110	com/oneplus/camera/io/PhotoSaveTask:m_EncodedPicture	[B
    //   273: astore_2
    //   274: aload_2
    //   275: ifnull +8 -> 283
    //   278: aload_2
    //   279: arraylength
    //   280: ifne +334 -> 614
    //   283: aload_0
    //   284: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   287: ldc_w 683
    //   290: invokestatic 432	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   293: aload 7
    //   295: astore_1
    //   296: aload 4
    //   298: ifnull +11 -> 309
    //   301: aload 4
    //   303: invokevirtual 676	java/io/FileOutputStream:close	()V
    //   306: aload 7
    //   308: astore_1
    //   309: aload_1
    //   310: ifnull +302 -> 612
    //   313: aload_1
    //   314: athrow
    //   315: astore_1
    //   316: goto -160 -> 156
    //   319: aload_0
    //   320: getfield 54	com/oneplus/camera/io/PhotoSaveTask:m_YuvImage	Landroid/graphics/YuvImage;
    //   323: astore 5
    //   325: aload 5
    //   327: ifnull +133 -> 460
    //   330: aconst_null
    //   331: astore_3
    //   332: aconst_null
    //   333: astore 8
    //   335: aconst_null
    //   336: astore 9
    //   338: aconst_null
    //   339: astore 5
    //   341: new 398	java/io/ByteArrayOutputStream
    //   344: dup
    //   345: invokespecial 399	java/io/ByteArrayOutputStream:<init>	()V
    //   348: astore_2
    //   349: aload_0
    //   350: getfield 54	com/oneplus/camera/io/PhotoSaveTask:m_YuvImage	Landroid/graphics/YuvImage;
    //   353: aload_0
    //   354: getfield 61	com/oneplus/camera/io/PhotoSaveTask:m_YuvImageBounds	Landroid/graphics/Rect;
    //   357: bipush 90
    //   359: aload_2
    //   360: invokevirtual 422	android/graphics/YuvImage:compressToJpeg	(Landroid/graphics/Rect;ILjava/io/OutputStream;)Z
    //   363: pop
    //   364: aload_2
    //   365: invokevirtual 425	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   368: astore 5
    //   370: aload 5
    //   372: astore_3
    //   373: aload 8
    //   375: astore 5
    //   377: aload_2
    //   378: ifnull +11 -> 389
    //   381: aload_2
    //   382: invokevirtual 410	java/io/ByteArrayOutputStream:close	()V
    //   385: aload 8
    //   387: astore 5
    //   389: aload_3
    //   390: astore_2
    //   391: aload 5
    //   393: ifnull -119 -> 274
    //   396: aload 5
    //   398: athrow
    //   399: astore 5
    //   401: goto -12 -> 389
    //   404: astore_2
    //   405: aload 5
    //   407: astore_1
    //   408: aload_2
    //   409: athrow
    //   410: astore 5
    //   412: aload_2
    //   413: astore_3
    //   414: aload 5
    //   416: astore_2
    //   417: aload_3
    //   418: astore 5
    //   420: aload_1
    //   421: ifnull +10 -> 431
    //   424: aload_1
    //   425: invokevirtual 410	java/io/ByteArrayOutputStream:close	()V
    //   428: aload_3
    //   429: astore 5
    //   431: aload 5
    //   433: ifnull +25 -> 458
    //   436: aload 5
    //   438: athrow
    //   439: aload_3
    //   440: astore 5
    //   442: aload_3
    //   443: aload_1
    //   444: if_acmpeq -13 -> 431
    //   447: aload_3
    //   448: aload_1
    //   449: invokevirtual 260	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   452: aload_3
    //   453: astore 5
    //   455: goto -24 -> 431
    //   458: aload_2
    //   459: athrow
    //   460: aload_0
    //   461: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   464: ifnull +91 -> 555
    //   467: aload_0
    //   468: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   471: invokevirtual 269	com/oneplus/camera/CameraCaptureEventArgs:getPictureFormat	()I
    //   474: sipush 256
    //   477: if_icmpne +19 -> 496
    //   480: aload_0
    //   481: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   484: invokevirtual 266	com/oneplus/camera/CameraCaptureEventArgs:getPicturePlanes	()[Lcom/oneplus/camera/media/ImagePlane;
    //   487: iconst_0
    //   488: aaload
    //   489: invokevirtual 275	com/oneplus/camera/media/ImagePlane:getData	()[B
    //   492: astore_2
    //   493: goto -219 -> 274
    //   496: aload_0
    //   497: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   500: new 328	java/lang/StringBuilder
    //   503: dup
    //   504: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   507: ldc_w 685
    //   510: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   513: aload_0
    //   514: getfield 87	com/oneplus/camera/io/PhotoSaveTask:m_CaptureEventArgs	Lcom/oneplus/camera/CameraCaptureEventArgs;
    //   517: invokevirtual 269	com/oneplus/camera/CameraCaptureEventArgs:getPictureFormat	()I
    //   520: invokevirtual 430	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   523: invokevirtual 347	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   526: invokestatic 432	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   529: aload_2
    //   530: astore_1
    //   531: aload 4
    //   533: ifnull +10 -> 543
    //   536: aload 4
    //   538: invokevirtual 676	java/io/FileOutputStream:close	()V
    //   541: aload_2
    //   542: astore_1
    //   543: aload_1
    //   544: ifnull +9 -> 553
    //   547: aload_1
    //   548: athrow
    //   549: astore_1
    //   550: goto -7 -> 543
    //   553: iconst_0
    //   554: ireturn
    //   555: aload_0
    //   556: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   559: new 328	java/lang/StringBuilder
    //   562: dup
    //   563: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   566: ldc_w 687
    //   569: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   572: aload_1
    //   573: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   576: invokevirtual 347	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   579: invokestatic 432	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   582: aload_3
    //   583: astore_1
    //   584: aload 4
    //   586: ifnull +10 -> 596
    //   589: aload 4
    //   591: invokevirtual 676	java/io/FileOutputStream:close	()V
    //   594: aload_3
    //   595: astore_1
    //   596: aload_1
    //   597: ifnull +9 -> 606
    //   600: aload_1
    //   601: athrow
    //   602: astore_1
    //   603: goto -7 -> 596
    //   606: iconst_0
    //   607: ireturn
    //   608: astore_1
    //   609: goto -300 -> 309
    //   612: iconst_0
    //   613: ireturn
    //   614: aconst_null
    //   615: astore 5
    //   617: aconst_null
    //   618: astore 7
    //   620: aconst_null
    //   621: astore 9
    //   623: aconst_null
    //   624: astore 8
    //   626: new 242	java/io/ByteArrayInputStream
    //   629: dup
    //   630: aload_2
    //   631: invokespecial 245	java/io/ByteArrayInputStream:<init>	([B)V
    //   634: astore_3
    //   635: aload_2
    //   636: invokestatic 691	com/oneplus/media/ImageUtils:isJfifHeader	([B)Z
    //   639: ifeq +226 -> 865
    //   642: aload_3
    //   643: invokestatic 697	com/oneplus/media/JfifImage:create	(Ljava/io/InputStream;)Lcom/oneplus/media/JfifImage;
    //   646: astore 8
    //   648: aload 8
    //   650: getstatic 703	com/oneplus/media/OnePlusXMP:KEY_CAPTURE_MODE	Lcom/oneplus/media/XMPPropertyKey;
    //   653: aload_0
    //   654: invokevirtual 707	com/oneplus/camera/io/PhotoSaveTask:getCaptureHandle	()Lcom/oneplus/camera/CaptureHandle;
    //   657: invokevirtual 713	com/oneplus/camera/CaptureHandle:getCaptureMode	()Lcom/oneplus/camera/capturemode/CaptureMode;
    //   660: getstatic 719	com/oneplus/camera/capturemode/CaptureMode:PROP_ID	Lcom/oneplus/base/PropertyKey;
    //   663: invokeinterface 722 2 0
    //   668: invokevirtual 726	com/oneplus/media/JfifImage:setXMPProperty	(Lcom/oneplus/media/XMPPropertyKey;Ljava/lang/Object;)V
    //   671: aconst_null
    //   672: astore_2
    //   673: aload_0
    //   674: invokevirtual 730	com/oneplus/camera/io/PhotoSaveTask:getSceneMode	()Ljava/lang/Integer;
    //   677: invokevirtual 733	java/lang/Integer:intValue	()I
    //   680: lookupswitch	default:+579->1259, 3:+589->1269, 11:+596->1276, 18:+582->1262, 10001:+120->800
    //   724: aload 8
    //   726: getstatic 736	com/oneplus/media/OnePlusXMP:KEY_SCENE	Lcom/oneplus/media/XMPPropertyKey;
    //   729: aload_2
    //   730: invokevirtual 726	com/oneplus/media/JfifImage:setXMPProperty	(Lcom/oneplus/media/XMPPropertyKey;Ljava/lang/Object;)V
    //   733: aload 8
    //   735: getstatic 739	com/oneplus/media/OnePlusXMP:KEY_IS_HDR_ACTIVE	Lcom/oneplus/media/XMPPropertyKey;
    //   738: aload_0
    //   739: invokevirtual 743	com/oneplus/camera/io/PhotoSaveTask:isHdrActive	()Ljava/lang/Boolean;
    //   742: invokevirtual 726	com/oneplus/media/JfifImage:setXMPProperty	(Lcom/oneplus/media/XMPPropertyKey;Ljava/lang/Object;)V
    //   745: aload_0
    //   746: invokevirtual 583	com/oneplus/camera/io/PhotoSaveTask:getLensFacing	()Lcom/oneplus/camera/Camera$LensFacing;
    //   749: getstatic 746	com/oneplus/camera/Camera$LensFacing:BACK	Lcom/oneplus/camera/Camera$LensFacing;
    //   752: if_acmpne +55 -> 807
    //   755: aload 8
    //   757: getstatic 749	com/oneplus/media/OnePlusXMP:KEY_LENS_FACING	Lcom/oneplus/media/XMPPropertyKey;
    //   760: ldc_w 751
    //   763: invokevirtual 726	com/oneplus/media/JfifImage:setXMPProperty	(Lcom/oneplus/media/XMPPropertyKey;Ljava/lang/Object;)V
    //   766: aload_0
    //   767: aload 8
    //   769: invokevirtual 753	com/oneplus/camera/io/PhotoSaveTask:onImageEncoded	(Lcom/oneplus/media/EncodedImage;)V
    //   772: aload 8
    //   774: aload 4
    //   776: invokevirtual 757	com/oneplus/media/JfifImage:save	(Ljava/io/OutputStream;)Z
    //   779: pop
    //   780: aload 7
    //   782: astore_2
    //   783: aload_3
    //   784: ifnull +10 -> 794
    //   787: aload_3
    //   788: invokevirtual 254	java/io/ByteArrayInputStream:close	()V
    //   791: aload 7
    //   793: astore_2
    //   794: aload_2
    //   795: ifnull +124 -> 919
    //   798: aload_2
    //   799: athrow
    //   800: ldc_w 759
    //   803: astore_2
    //   804: goto -80 -> 724
    //   807: aload_0
    //   808: invokevirtual 583	com/oneplus/camera/io/PhotoSaveTask:getLensFacing	()Lcom/oneplus/camera/Camera$LensFacing;
    //   811: getstatic 589	com/oneplus/camera/Camera$LensFacing:FRONT	Lcom/oneplus/camera/Camera$LensFacing;
    //   814: if_acmpne -48 -> 766
    //   817: aload 8
    //   819: getstatic 749	com/oneplus/media/OnePlusXMP:KEY_LENS_FACING	Lcom/oneplus/media/XMPPropertyKey;
    //   822: ldc_w 761
    //   825: invokevirtual 726	com/oneplus/media/JfifImage:setXMPProperty	(Lcom/oneplus/media/XMPPropertyKey;Ljava/lang/Object;)V
    //   828: goto -62 -> 766
    //   831: astore_2
    //   832: aload_3
    //   833: astore_1
    //   834: aload_2
    //   835: athrow
    //   836: astore 5
    //   838: aload_2
    //   839: astore_3
    //   840: aload 5
    //   842: astore_2
    //   843: aload_3
    //   844: astore 5
    //   846: aload_1
    //   847: ifnull +10 -> 857
    //   850: aload_1
    //   851: invokevirtual 254	java/io/ByteArrayInputStream:close	()V
    //   854: aload_3
    //   855: astore 5
    //   857: aload 5
    //   859: ifnull +58 -> 917
    //   862: aload 5
    //   864: athrow
    //   865: aload 4
    //   867: aload_2
    //   868: invokevirtual 764	java/io/FileOutputStream:write	([B)V
    //   871: goto -91 -> 780
    //   874: astore_2
    //   875: aload_3
    //   876: astore_1
    //   877: aload 5
    //   879: astore_3
    //   880: goto -37 -> 843
    //   883: astore_2
    //   884: goto -90 -> 794
    //   887: astore_1
    //   888: aload_3
    //   889: ifnonnull +9 -> 898
    //   892: aload_1
    //   893: astore 5
    //   895: goto -38 -> 857
    //   898: aload_3
    //   899: astore 5
    //   901: aload_3
    //   902: aload_1
    //   903: if_acmpeq -46 -> 857
    //   906: aload_3
    //   907: aload_1
    //   908: invokevirtual 260	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   911: aload_3
    //   912: astore 5
    //   914: goto -57 -> 857
    //   917: aload_2
    //   918: athrow
    //   919: aload_0
    //   920: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   923: new 328	java/lang/StringBuilder
    //   926: dup
    //   927: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   930: ldc_w 766
    //   933: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   936: aload_1
    //   937: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   940: invokevirtual 347	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   943: invokestatic 282	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   946: aload 6
    //   948: astore_2
    //   949: aload 4
    //   951: ifnull +11 -> 962
    //   954: aload 4
    //   956: invokevirtual 676	java/io/FileOutputStream:close	()V
    //   959: aload 6
    //   961: astore_2
    //   962: aload_2
    //   963: ifnull +41 -> 1004
    //   966: aload_2
    //   967: athrow
    //   968: astore_2
    //   969: goto -7 -> 962
    //   972: astore_3
    //   973: aload_1
    //   974: ifnonnull +9 -> 983
    //   977: aload_3
    //   978: astore 4
    //   980: goto -833 -> 147
    //   983: aload_1
    //   984: astore 4
    //   986: aload_1
    //   987: aload_3
    //   988: if_acmpeq -841 -> 147
    //   991: aload_1
    //   992: aload_3
    //   993: invokevirtual 260	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   996: aload_1
    //   997: astore 4
    //   999: goto -852 -> 147
    //   1002: aload_2
    //   1003: athrow
    //   1004: aload_0
    //   1005: getfield 49	com/oneplus/camera/io/PhotoSaveTask:m_ExifTags	Ljava/util/Map;
    //   1008: ifnull +7 -> 1015
    //   1011: aload_0
    //   1012: invokespecial 768	com/oneplus/camera/io/PhotoSaveTask:fillToExif	()V
    //   1015: aload_0
    //   1016: getfield 49	com/oneplus/camera/io/PhotoSaveTask:m_ExifTags	Ljava/util/Map;
    //   1019: ifnull +15 -> 1034
    //   1022: aload_0
    //   1023: getfield 49	com/oneplus/camera/io/PhotoSaveTask:m_ExifTags	Ljava/util/Map;
    //   1026: invokeinterface 771 1 0
    //   1031: ifeq +5 -> 1036
    //   1034: iconst_1
    //   1035: ireturn
    //   1036: new 773	android/media/ExifInterface
    //   1039: dup
    //   1040: aload_1
    //   1041: invokespecial 774	android/media/ExifInterface:<init>	(Ljava/lang/String;)V
    //   1044: astore_2
    //   1045: aload_0
    //   1046: getfield 49	com/oneplus/camera/io/PhotoSaveTask:m_ExifTags	Ljava/util/Map;
    //   1049: invokeinterface 778 1 0
    //   1054: invokeinterface 784 1 0
    //   1059: astore_3
    //   1060: aload_3
    //   1061: invokeinterface 789 1 0
    //   1066: ifeq +72 -> 1138
    //   1069: aload_3
    //   1070: invokeinterface 793 1 0
    //   1075: checkcast 795	java/util/Map$Entry
    //   1078: astore 4
    //   1080: aload_2
    //   1081: aload 4
    //   1083: invokeinterface 798 1 0
    //   1088: checkcast 166	java/lang/String
    //   1091: aload 4
    //   1093: invokeinterface 801 1 0
    //   1098: checkcast 166	java/lang/String
    //   1101: invokevirtual 804	android/media/ExifInterface:setAttribute	(Ljava/lang/String;Ljava/lang/String;)V
    //   1104: goto -44 -> 1060
    //   1107: astore_2
    //   1108: aload_0
    //   1109: getfield 213	com/oneplus/camera/io/PhotoSaveTask:TAG	Ljava/lang/String;
    //   1112: new 328	java/lang/StringBuilder
    //   1115: dup
    //   1116: invokespecial 329	java/lang/StringBuilder:<init>	()V
    //   1119: ldc_w 806
    //   1122: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1125: aload_1
    //   1126: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1129: invokevirtual 347	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1132: aload_2
    //   1133: invokestatic 229	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1136: iconst_0
    //   1137: ireturn
    //   1138: aload_2
    //   1139: invokevirtual 809	android/media/ExifInterface:saveAttributes	()V
    //   1142: goto -108 -> 1034
    //   1145: astore_2
    //   1146: aconst_null
    //   1147: astore_1
    //   1148: aload 5
    //   1150: astore_3
    //   1151: goto -1018 -> 133
    //   1154: astore_1
    //   1155: aload 8
    //   1157: astore_3
    //   1158: goto -1028 -> 130
    //   1161: astore_2
    //   1162: aload 9
    //   1164: astore_1
    //   1165: aload 5
    //   1167: astore_3
    //   1168: goto -325 -> 843
    //   1171: astore_2
    //   1172: aload 8
    //   1174: astore_1
    //   1175: goto -341 -> 834
    //   1178: astore_2
    //   1179: aload 9
    //   1181: astore_1
    //   1182: goto -765 -> 417
    //   1185: astore 5
    //   1187: aload_2
    //   1188: astore_1
    //   1189: aload 5
    //   1191: astore_2
    //   1192: goto -775 -> 417
    //   1195: astore_3
    //   1196: aload_2
    //   1197: astore_1
    //   1198: aload_3
    //   1199: astore_2
    //   1200: goto -792 -> 408
    //   1203: astore_2
    //   1204: aload 9
    //   1206: astore_1
    //   1207: goto -988 -> 219
    //   1210: astore 5
    //   1212: aload_2
    //   1213: astore_1
    //   1214: aload 5
    //   1216: astore_2
    //   1217: goto -998 -> 219
    //   1220: astore_3
    //   1221: aload_2
    //   1222: astore_1
    //   1223: aload_3
    //   1224: astore_2
    //   1225: goto -1015 -> 210
    //   1228: astore_2
    //   1229: aconst_null
    //   1230: astore_1
    //   1231: aload 4
    //   1233: astore_3
    //   1234: goto -1101 -> 133
    //   1237: astore_1
    //   1238: aload_3
    //   1239: ifnonnull -998 -> 241
    //   1242: aload_1
    //   1243: astore 5
    //   1245: goto -1012 -> 233
    //   1248: astore_1
    //   1249: aload_3
    //   1250: ifnonnull -811 -> 439
    //   1253: aload_1
    //   1254: astore 5
    //   1256: goto -825 -> 431
    //   1259: goto -535 -> 724
    //   1262: ldc_w 811
    //   1265: astore_2
    //   1266: goto -542 -> 724
    //   1269: ldc_w 813
    //   1272: astore_2
    //   1273: goto -549 -> 724
    //   1276: ldc_w 815
    //   1279: astore_2
    //   1280: goto -556 -> 724
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1283	0	this	PhotoSaveTask
    //   0	1283	1	paramString	String
    //   21	97	2	localObject1	Object
    //   132	1	2	localObject2	Object
    //   206	9	2	localThrowable1	Throwable
    //   218	173	2	localObject3	Object
    //   404	9	2	localThrowable2	Throwable
    //   416	388	2	localObject4	Object
    //   831	8	2	localThrowable3	Throwable
    //   842	26	2	localObject5	Object
    //   874	1	2	localObject6	Object
    //   883	35	2	localThrowable4	Throwable
    //   948	19	2	localObject7	Object
    //   968	35	2	localThrowable5	Throwable
    //   1044	37	2	localExifInterface	android.media.ExifInterface
    //   1107	32	2	localThrowable6	Throwable
    //   1145	1	2	localObject8	Object
    //   1161	1	2	localObject9	Object
    //   1171	1	2	localThrowable7	Throwable
    //   1178	10	2	localObject10	Object
    //   1191	9	2	localObject11	Object
    //   1203	10	2	localObject12	Object
    //   1216	9	2	localObject13	Object
    //   1228	1	2	localObject14	Object
    //   1265	15	2	str1	String
    //   23	889	3	localObject15	Object
    //   972	21	3	localThrowable8	Throwable
    //   1059	109	3	localObject16	Object
    //   1195	4	3	localThrowable9	Throwable
    //   1220	4	3	localThrowable10	Throwable
    //   1233	17	3	localObject17	Object
    //   45	1187	4	localObject18	Object
    //   31	93	5	localObject19	Object
    //   201	7	5	localThrowable11	Throwable
    //   212	5	5	localObject20	Object
    //   220	177	5	localObject21	Object
    //   399	7	5	localThrowable12	Throwable
    //   410	5	5	localObject22	Object
    //   418	198	5	localObject23	Object
    //   836	5	5	localObject24	Object
    //   844	322	5	localObject25	Object
    //   1185	5	5	localObject26	Object
    //   1210	5	5	localObject27	Object
    //   1243	12	5	str2	String
    //   25	935	6	localObject28	Object
    //   28	764	7	localObject29	Object
    //   34	1139	8	localJfifImage	com.oneplus.media.JfifImage
    //   64	1141	9	localObject30	Object
    //   18	156	10	localFile	File
    // Exception table:
    //   from	to	target	type
    //   47	53	126	java/lang/Throwable
    //   123	126	126	java/lang/Throwable
    //   238	241	126	java/lang/Throwable
    //   249	254	126	java/lang/Throwable
    //   260	262	126	java/lang/Throwable
    //   262	274	126	java/lang/Throwable
    //   278	283	126	java/lang/Throwable
    //   283	293	126	java/lang/Throwable
    //   319	325	126	java/lang/Throwable
    //   396	399	126	java/lang/Throwable
    //   436	439	126	java/lang/Throwable
    //   447	452	126	java/lang/Throwable
    //   458	460	126	java/lang/Throwable
    //   460	493	126	java/lang/Throwable
    //   496	529	126	java/lang/Throwable
    //   555	582	126	java/lang/Throwable
    //   798	800	126	java/lang/Throwable
    //   862	865	126	java/lang/Throwable
    //   906	911	126	java/lang/Throwable
    //   917	919	126	java/lang/Throwable
    //   919	946	126	java/lang/Throwable
    //   130	132	132	finally
    //   152	155	155	java/lang/Throwable
    //   991	996	155	java/lang/Throwable
    //   1002	1004	155	java/lang/Throwable
    //   108	112	201	java/lang/Throwable
    //   69	77	206	java/lang/Throwable
    //   210	212	212	finally
    //   313	315	315	java/lang/Throwable
    //   547	549	315	java/lang/Throwable
    //   600	602	315	java/lang/Throwable
    //   966	968	315	java/lang/Throwable
    //   381	385	399	java/lang/Throwable
    //   341	349	404	java/lang/Throwable
    //   408	410	410	finally
    //   536	541	549	java/lang/Throwable
    //   589	594	602	java/lang/Throwable
    //   301	306	608	java/lang/Throwable
    //   635	671	831	java/lang/Throwable
    //   673	724	831	java/lang/Throwable
    //   724	766	831	java/lang/Throwable
    //   766	780	831	java/lang/Throwable
    //   807	828	831	java/lang/Throwable
    //   865	871	831	java/lang/Throwable
    //   834	836	836	finally
    //   635	671	874	finally
    //   673	724	874	finally
    //   724	766	874	finally
    //   766	780	874	finally
    //   807	828	874	finally
    //   865	871	874	finally
    //   787	791	883	java/lang/Throwable
    //   850	854	887	java/lang/Throwable
    //   954	959	968	java/lang/Throwable
    //   140	144	972	java/lang/Throwable
    //   1036	1060	1107	java/lang/Throwable
    //   1060	1104	1107	java/lang/Throwable
    //   1138	1142	1107	java/lang/Throwable
    //   36	47	1145	finally
    //   36	47	1154	java/lang/Throwable
    //   626	635	1161	finally
    //   626	635	1171	java/lang/Throwable
    //   341	349	1178	finally
    //   349	370	1185	finally
    //   349	370	1195	java/lang/Throwable
    //   69	77	1203	finally
    //   77	97	1210	finally
    //   77	97	1220	java/lang/Throwable
    //   47	53	1228	finally
    //   108	112	1228	finally
    //   123	126	1228	finally
    //   226	230	1228	finally
    //   238	241	1228	finally
    //   249	254	1228	finally
    //   260	262	1228	finally
    //   262	274	1228	finally
    //   278	283	1228	finally
    //   283	293	1228	finally
    //   319	325	1228	finally
    //   381	385	1228	finally
    //   396	399	1228	finally
    //   424	428	1228	finally
    //   436	439	1228	finally
    //   447	452	1228	finally
    //   458	460	1228	finally
    //   460	493	1228	finally
    //   496	529	1228	finally
    //   555	582	1228	finally
    //   787	791	1228	finally
    //   798	800	1228	finally
    //   850	854	1228	finally
    //   862	865	1228	finally
    //   906	911	1228	finally
    //   917	919	1228	finally
    //   919	946	1228	finally
    //   226	230	1237	java/lang/Throwable
    //   424	428	1248	java/lang/Throwable
  }
  
  public void setEncodedPicture(byte[] paramArrayOfByte)
  {
    this.m_EncodedPicture = paramArrayOfByte;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/io/PhotoSaveTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */