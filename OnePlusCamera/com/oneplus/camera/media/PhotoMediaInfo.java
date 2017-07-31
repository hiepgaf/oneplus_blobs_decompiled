package com.oneplus.camera.media;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.Rational;
import com.oneplus.base.Log;
import com.oneplus.database.CursorUtils;
import com.oneplus.io.FileUtils;
import java.util.Map;

public class PhotoMediaInfo
  extends MediaInfo
{
  public static final String DETAILS_APERTURE = "Aperture";
  public static final String DETAILS_CAMERA_MAKER = "CameraMaker";
  public static final String DETAILS_CAMERA_MODEL = "CameraModel";
  public static final String DETAILS_FLASH = "Flash";
  public static final String DETAILS_FOCAL_LENGTH = "FocalLength";
  public static final String DETAILS_ISO = "ISO";
  public static final String DETAILS_SHUTTER_SPEED = "ShutterSpeed";
  public static final String DETAILS_WHITE_BALANCE = "WhiteBalance";
  private static final String TAG = PhotoMediaInfo.class.getSimpleName();
  public static final int WHITE_BALANCE_AUTO = 0;
  public static final int WHITE_BALANCE_MANUAL = 1;
  private final int m_ActualHeight;
  private final int m_ActualWidth;
  private final int m_Orientation;
  private final long m_TakenTime;
  
  public PhotoMediaInfo(Uri paramUri, Cursor paramCursor)
  {
    super(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, paramCursor);
    if (paramCursor != null) {
      this.m_Orientation = CursorUtils.getInt(paramCursor, "orientation", 0);
    }
    for (this.m_TakenTime = CursorUtils.getLong(paramCursor, "datetaken", 0L);; this.m_TakenTime = 0L)
    {
      int j = super.getWidth();
      m = super.getHeight();
      int i;
      if (j != 0)
      {
        k = m;
        i = j;
        if (m != 0) {}
      }
      else
      {
        k = m;
        i = j;
        if (super.getFilePath() != null) {
          i = j;
        }
      }
      try
      {
        paramUri = new BitmapFactory.Options();
        i = j;
        paramUri.inJustDecodeBounds = true;
        i = j;
        BitmapFactory.decodeFile(super.getFilePath(), paramUri);
        i = j;
        j = paramUri.outWidth;
        i = j;
        k = paramUri.outHeight;
        i = j;
      }
      catch (Throwable paramUri)
      {
        for (;;)
        {
          Log.e("PhotoMediaInfo", "PhotoMediaInfo() - Fail to get photo size", paramUri);
          k = m;
        }
      }
      this.m_ActualWidth = i;
      this.m_ActualHeight = k;
      return;
      this.m_Orientation = 0;
    }
  }
  
  private boolean addToDetails(Map<String, Object> paramMap, String paramString, Object paramObject)
  {
    if ((paramMap != null) && (paramObject != null))
    {
      paramMap.put(paramString, paramObject);
      return true;
    }
    return false;
  }
  
  private Double toDouble(String paramString)
  {
    Object localObject = null;
    if (paramString == null) {
      return null;
    }
    try
    {
      if (paramString.indexOf('/') < 0) {
        return Double.valueOf(Double.parseDouble(paramString));
      }
      Rational localRational = toRational(paramString, false);
      paramString = (String)localObject;
      if (localRational != null)
      {
        double d = localRational.doubleValue();
        paramString = Double.valueOf(d);
      }
      return paramString;
    }
    catch (Throwable paramString) {}
    return null;
  }
  
  private Double toGeoCoordinate(Rational[] paramArrayOfRational)
  {
    if ((paramArrayOfRational == null) || (paramArrayOfRational.length == 0)) {
      return null;
    }
    try
    {
      double d1 = paramArrayOfRational[0].doubleValue();
      int j = 60;
      int i = 1;
      while (i < paramArrayOfRational.length)
      {
        double d2 = paramArrayOfRational[i].doubleValue();
        d1 += d2 / j;
        j *= 60;
        i += 1;
      }
      return Double.valueOf(d1);
    }
    catch (Throwable paramArrayOfRational)
    {
      return null;
    }
  }
  
  private Rational toRational(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      return null;
    }
    int k = paramString.indexOf('.');
    if (k >= 0) {}
    for (;;)
    {
      int i;
      try
      {
        d = Double.parseDouble(paramString);
        i = 1;
        j = paramString.length() - 1;
      }
      catch (Throwable paramString)
      {
        double d;
        int m;
        return null;
      }
      m = (int)(i * d + 0.5D);
      k = i;
      int j = m;
      if (paramBoolean)
      {
        k = i;
        j = m;
        if (m > 1)
        {
          k = i;
          j = m;
          if (m < i)
          {
            k = i / m;
            j = 1;
          }
        }
      }
      return new Rational(j, k);
      paramString = Rational.parseRational(paramString);
      return paramString;
      while (j > k)
      {
        i *= 10;
        j -= 1;
      }
    }
  }
  
  private Rational[] toRationals(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      return null;
    }
    paramString = paramString.split(",");
    Rational[] arrayOfRational = new Rational[paramString.length];
    int i = paramString.length - 1;
    while (i >= 0)
    {
      Rational localRational = toRational(paramString[i], paramBoolean);
      if (localRational == null) {
        return null;
      }
      arrayOfRational[i] = localRational;
      i -= 1;
    }
    return arrayOfRational;
  }
  
  /* Error */
  public Map<String, Object> getDetails(android.content.Context paramContext)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aconst_null
    //   3: astore 7
    //   5: aconst_null
    //   6: astore 8
    //   8: aload_0
    //   9: aload_1
    //   10: invokespecial 189	com/oneplus/camera/media/MediaInfo:getDetails	(Landroid/content/Context;)Ljava/util/Map;
    //   13: astore 4
    //   15: aload 4
    //   17: astore 6
    //   19: aload 4
    //   21: ifnonnull +12 -> 33
    //   24: new 191	java/util/Hashtable
    //   27: dup
    //   28: invokespecial 192	java/util/Hashtable:<init>	()V
    //   31: astore 6
    //   33: aload_0
    //   34: invokevirtual 196	com/oneplus/camera/media/PhotoMediaInfo:hasFilePath	()Z
    //   37: ifeq +343 -> 380
    //   40: aconst_null
    //   41: astore 5
    //   43: aconst_null
    //   44: astore 4
    //   46: aload_1
    //   47: invokevirtual 202	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   50: aload_0
    //   51: invokevirtual 206	com/oneplus/camera/media/PhotoMediaInfo:getContentUri	()Landroid/net/Uri;
    //   54: invokevirtual 212	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   57: astore_1
    //   58: aload_1
    //   59: astore 4
    //   61: aload_1
    //   62: astore 5
    //   64: aload_1
    //   65: invokestatic 218	com/oneplus/media/ImageUtils:readPhotoMetadata	(Ljava/io/InputStream;)Lcom/oneplus/media/PhotoMetadata;
    //   68: astore 9
    //   70: aload_1
    //   71: astore 4
    //   73: aload_1
    //   74: astore 5
    //   76: aload 9
    //   78: getstatic 224	com/oneplus/media/PhotoMetadata:PROP_FLASH_DATA	Lcom/oneplus/base/PropertyKey;
    //   81: invokeinterface 228 2 0
    //   86: checkcast 230	com/oneplus/media/FlashData
    //   89: astore 10
    //   91: aload 10
    //   93: ifnull +290 -> 383
    //   96: aload_1
    //   97: astore 4
    //   99: aload_1
    //   100: astore 5
    //   102: aload 10
    //   104: invokevirtual 233	com/oneplus/media/FlashData:isFlashFired	()Z
    //   107: istore_3
    //   108: aload_1
    //   109: astore 4
    //   111: aload_1
    //   112: astore 5
    //   114: aload_0
    //   115: aload 6
    //   117: ldc 17
    //   119: iload_3
    //   120: invokestatic 238	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   123: invokespecial 240	com/oneplus/camera/media/PhotoMediaInfo:addToDetails	(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Object;)Z
    //   126: pop
    //   127: aload_1
    //   128: astore 4
    //   130: aload_1
    //   131: astore 5
    //   133: aload_0
    //   134: aload 6
    //   136: ldc -14
    //   138: aload 9
    //   140: getstatic 245	com/oneplus/media/PhotoMetadata:PROP_LOCATION	Lcom/oneplus/base/PropertyKey;
    //   143: invokeinterface 228 2 0
    //   148: invokespecial 240	com/oneplus/camera/media/PhotoMediaInfo:addToDetails	(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Object;)Z
    //   151: pop
    //   152: aload_1
    //   153: astore 4
    //   155: aload_1
    //   156: astore 5
    //   158: aload_0
    //   159: aload 6
    //   161: ldc 8
    //   163: aload 9
    //   165: getstatic 248	com/oneplus/media/PhotoMetadata:PROP_APERTURE_VALUE	Lcom/oneplus/base/PropertyKey;
    //   168: invokeinterface 228 2 0
    //   173: invokespecial 240	com/oneplus/camera/media/PhotoMediaInfo:addToDetails	(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Object;)Z
    //   176: pop
    //   177: aload_1
    //   178: astore 4
    //   180: aload_1
    //   181: astore 5
    //   183: aload_0
    //   184: aload 6
    //   186: ldc 11
    //   188: aload 9
    //   190: getstatic 251	com/oneplus/media/PhotoMetadata:PROP_MAKE	Lcom/oneplus/base/PropertyKey;
    //   193: invokeinterface 228 2 0
    //   198: invokespecial 240	com/oneplus/camera/media/PhotoMediaInfo:addToDetails	(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Object;)Z
    //   201: pop
    //   202: aload_1
    //   203: astore 4
    //   205: aload_1
    //   206: astore 5
    //   208: aload_0
    //   209: aload 6
    //   211: ldc 14
    //   213: aload 9
    //   215: getstatic 254	com/oneplus/media/PhotoMetadata:PROP_MODEL	Lcom/oneplus/base/PropertyKey;
    //   218: invokeinterface 228 2 0
    //   223: invokespecial 240	com/oneplus/camera/media/PhotoMediaInfo:addToDetails	(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Object;)Z
    //   226: pop
    //   227: aload_1
    //   228: astore 4
    //   230: aload_1
    //   231: astore 5
    //   233: aload_0
    //   234: aload 6
    //   236: ldc 20
    //   238: aload 9
    //   240: getstatic 257	com/oneplus/media/PhotoMetadata:PROP_FOCAL_LENGTH	Lcom/oneplus/base/PropertyKey;
    //   243: invokeinterface 228 2 0
    //   248: invokespecial 240	com/oneplus/camera/media/PhotoMediaInfo:addToDetails	(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Object;)Z
    //   251: pop
    //   252: aload_1
    //   253: astore 4
    //   255: aload_1
    //   256: astore 5
    //   258: aload_0
    //   259: aload 6
    //   261: ldc 23
    //   263: aload 9
    //   265: getstatic 260	com/oneplus/media/PhotoMetadata:PROP_ISO	Lcom/oneplus/base/PropertyKey;
    //   268: invokeinterface 228 2 0
    //   273: invokespecial 240	com/oneplus/camera/media/PhotoMediaInfo:addToDetails	(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Object;)Z
    //   276: pop
    //   277: aload_1
    //   278: astore 4
    //   280: aload_1
    //   281: astore 5
    //   283: aload_0
    //   284: aload 6
    //   286: ldc 26
    //   288: aload 9
    //   290: getstatic 263	com/oneplus/media/PhotoMetadata:PROP_EXPOSURE_TIME	Lcom/oneplus/base/PropertyKey;
    //   293: invokeinterface 228 2 0
    //   298: invokespecial 240	com/oneplus/camera/media/PhotoMediaInfo:addToDetails	(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Object;)Z
    //   301: pop
    //   302: aload_1
    //   303: astore 4
    //   305: aload_1
    //   306: astore 5
    //   308: aload 9
    //   310: getstatic 266	com/oneplus/media/PhotoMetadata:PROP_WHITE_BALANCE	Lcom/oneplus/base/PropertyKey;
    //   313: invokeinterface 228 2 0
    //   318: getstatic 272	com/oneplus/media/PhotoMetadata$WhiteBalance:MANUAL	Lcom/oneplus/media/PhotoMetadata$WhiteBalance;
    //   321: if_acmpne +5 -> 326
    //   324: iconst_1
    //   325: istore_2
    //   326: aload_1
    //   327: astore 4
    //   329: aload_1
    //   330: astore 5
    //   332: aload_0
    //   333: aload 6
    //   335: ldc 29
    //   337: iload_2
    //   338: invokestatic 277	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   341: invokespecial 240	com/oneplus/camera/media/PhotoMediaInfo:addToDetails	(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Object;)Z
    //   344: pop
    //   345: aload 8
    //   347: astore 4
    //   349: aload_1
    //   350: ifnull +11 -> 361
    //   353: aload_1
    //   354: invokevirtual 282	java/io/InputStream:close	()V
    //   357: aload 8
    //   359: astore 4
    //   361: aload 4
    //   363: ifnull +17 -> 380
    //   366: aload 4
    //   368: athrow
    //   369: astore_1
    //   370: getstatic 50	com/oneplus/camera/media/PhotoMediaInfo:TAG	Ljava/lang/String;
    //   373: ldc_w 284
    //   376: aload_1
    //   377: invokestatic 125	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   380: aload 6
    //   382: areturn
    //   383: iconst_0
    //   384: istore_3
    //   385: goto -277 -> 108
    //   388: astore 4
    //   390: goto -29 -> 361
    //   393: astore_1
    //   394: aload_1
    //   395: athrow
    //   396: astore 5
    //   398: aload_1
    //   399: astore 7
    //   401: aload 4
    //   403: ifnull +11 -> 414
    //   406: aload 4
    //   408: invokevirtual 282	java/io/InputStream:close	()V
    //   411: aload_1
    //   412: astore 7
    //   414: aload 7
    //   416: ifnull +27 -> 443
    //   419: aload 7
    //   421: athrow
    //   422: aload_1
    //   423: astore 7
    //   425: aload_1
    //   426: aload 4
    //   428: if_acmpeq -14 -> 414
    //   431: aload_1
    //   432: aload 4
    //   434: invokevirtual 288	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   437: aload_1
    //   438: astore 7
    //   440: goto -26 -> 414
    //   443: aload 5
    //   445: athrow
    //   446: astore_1
    //   447: aload 5
    //   449: astore 4
    //   451: aload_1
    //   452: astore 5
    //   454: aload 7
    //   456: astore_1
    //   457: goto -59 -> 398
    //   460: astore 4
    //   462: aload_1
    //   463: ifnonnull -41 -> 422
    //   466: aload 4
    //   468: astore 7
    //   470: goto -56 -> 414
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	473	0	this	PhotoMediaInfo
    //   0	473	1	paramContext	android.content.Context
    //   1	337	2	i	int
    //   107	278	3	bool	boolean
    //   13	354	4	localObject1	Object
    //   388	45	4	localThrowable1	Throwable
    //   449	1	4	localObject2	Object
    //   460	7	4	localThrowable2	Throwable
    //   41	290	5	localContext1	android.content.Context
    //   396	52	5	localObject3	Object
    //   452	1	5	localContext2	android.content.Context
    //   17	364	6	localObject4	Object
    //   3	466	7	localObject5	Object
    //   6	352	8	localObject6	Object
    //   68	241	9	localPhotoMetadata	com.oneplus.media.PhotoMetadata
    //   89	14	10	localFlashData	com.oneplus.media.FlashData
    // Exception table:
    //   from	to	target	type
    //   366	369	369	java/lang/Throwable
    //   419	422	369	java/lang/Throwable
    //   431	437	369	java/lang/Throwable
    //   443	446	369	java/lang/Throwable
    //   353	357	388	java/lang/Throwable
    //   46	58	393	java/lang/Throwable
    //   64	70	393	java/lang/Throwable
    //   76	91	393	java/lang/Throwable
    //   102	108	393	java/lang/Throwable
    //   114	127	393	java/lang/Throwable
    //   133	152	393	java/lang/Throwable
    //   158	177	393	java/lang/Throwable
    //   183	202	393	java/lang/Throwable
    //   208	227	393	java/lang/Throwable
    //   233	252	393	java/lang/Throwable
    //   258	277	393	java/lang/Throwable
    //   283	302	393	java/lang/Throwable
    //   308	324	393	java/lang/Throwable
    //   332	345	393	java/lang/Throwable
    //   394	396	396	finally
    //   46	58	446	finally
    //   64	70	446	finally
    //   76	91	446	finally
    //   102	108	446	finally
    //   114	127	446	finally
    //   133	152	446	finally
    //   158	177	446	finally
    //   183	202	446	finally
    //   208	227	446	finally
    //   233	252	446	finally
    //   258	277	446	finally
    //   283	302	446	finally
    //   308	324	446	finally
    //   332	345	446	finally
    //   406	411	460	java/lang/Throwable
  }
  
  public int getHeight()
  {
    switch (this.m_Orientation)
    {
    default: 
      return this.m_ActualHeight;
    }
    return this.m_ActualWidth;
  }
  
  public int getOrientation()
  {
    return this.m_Orientation;
  }
  
  public long getTakenTime()
  {
    return this.m_TakenTime;
  }
  
  public int getWidth()
  {
    switch (this.m_Orientation)
    {
    default: 
      return this.m_ActualWidth;
    }
    return this.m_ActualHeight;
  }
  
  public boolean isAnimation()
  {
    return FileUtils.isAnimationFilePath(getFilePath());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/PhotoMediaInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */