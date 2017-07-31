package com.oneplus.media;

import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.SerializeOptions;
import com.adobe.xmp.properties.XMPProperty;
import com.oneplus.base.Log;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JfifImage
  implements EncodedImage, XMPContainer, AutoCloseable
{
  private static final int APP1 = 225;
  private static final int EOI = 217;
  private static final int SOI = 216;
  private static final int SOS = 218;
  private static final String TAG = JfifImage.class.getSimpleName();
  private static final int XMP_BUFFER_SIZE_MAX = 65502;
  private static final String XMP_HEADER = "http://ns.adobe.com/xap/1.0/\000";
  private static final int XMP_HEADER_SIZE = "http://ns.adobe.com/xap/1.0/\000".length();
  private byte[] m_CompressedImageData;
  private volatile boolean m_IsClosed;
  private volatile boolean m_IsXMPModified;
  private List<JfifSegment> m_Segments = new ArrayList();
  private SimpleXMPContainer m_SimpleXMPContainer;
  private JfifSegment m_XMPSegment;
  
  /* Error */
  public static JfifImage create(android.graphics.Bitmap paramBitmap, int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore_3
    //   5: new 72	java/io/ByteArrayOutputStream
    //   8: dup
    //   9: invokespecial 73	java/io/ByteArrayOutputStream:<init>	()V
    //   12: astore_2
    //   13: aload_0
    //   14: getstatic 79	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   17: iload_1
    //   18: aload_2
    //   19: invokevirtual 85	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   22: pop
    //   23: aload_2
    //   24: invokevirtual 89	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   27: astore 4
    //   29: aload_2
    //   30: ifnull +7 -> 37
    //   33: aload_2
    //   34: invokevirtual 92	java/io/ByteArrayOutputStream:close	()V
    //   37: aconst_null
    //   38: astore_0
    //   39: aload_0
    //   40: ifnull +68 -> 108
    //   43: aload_0
    //   44: athrow
    //   45: astore_0
    //   46: getstatic 49	com/oneplus/media/JfifImage:TAG	Ljava/lang/String;
    //   49: ldc 94
    //   51: aload_0
    //   52: invokestatic 100	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   55: aconst_null
    //   56: areturn
    //   57: astore_0
    //   58: goto -19 -> 39
    //   61: astore_0
    //   62: aload_0
    //   63: athrow
    //   64: astore_2
    //   65: aload_0
    //   66: astore 4
    //   68: aload_3
    //   69: ifnull +10 -> 79
    //   72: aload_3
    //   73: invokevirtual 92	java/io/ByteArrayOutputStream:close	()V
    //   76: aload_0
    //   77: astore 4
    //   79: aload 4
    //   81: ifnull +25 -> 106
    //   84: aload 4
    //   86: athrow
    //   87: aload_0
    //   88: astore 4
    //   90: aload_0
    //   91: aload_3
    //   92: if_acmpeq -13 -> 79
    //   95: aload_0
    //   96: aload_3
    //   97: invokevirtual 104	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   100: aload_0
    //   101: astore 4
    //   103: goto -24 -> 79
    //   106: aload_2
    //   107: athrow
    //   108: aload 4
    //   110: ifnull +9 -> 119
    //   113: aload 4
    //   115: arraylength
    //   116: ifne +13 -> 129
    //   119: getstatic 49	com/oneplus/media/JfifImage:TAG	Ljava/lang/String;
    //   122: ldc 106
    //   124: invokestatic 109	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   127: aconst_null
    //   128: areturn
    //   129: aconst_null
    //   130: astore_2
    //   131: aconst_null
    //   132: astore_3
    //   133: new 111	java/io/ByteArrayInputStream
    //   136: dup
    //   137: aload 4
    //   139: invokespecial 114	java/io/ByteArrayInputStream:<init>	([B)V
    //   142: astore_0
    //   143: aload_0
    //   144: invokestatic 117	com/oneplus/media/JfifImage:create	(Ljava/io/InputStream;)Lcom/oneplus/media/JfifImage;
    //   147: astore_2
    //   148: aload_0
    //   149: ifnull +7 -> 156
    //   152: aload_0
    //   153: invokevirtual 118	java/io/ByteArrayInputStream:close	()V
    //   156: aconst_null
    //   157: astore_0
    //   158: aload_0
    //   159: ifnull +21 -> 180
    //   162: aload_0
    //   163: athrow
    //   164: astore_0
    //   165: getstatic 49	com/oneplus/media/JfifImage:TAG	Ljava/lang/String;
    //   168: ldc 120
    //   170: aload_0
    //   171: invokestatic 100	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   174: aconst_null
    //   175: areturn
    //   176: astore_0
    //   177: goto -19 -> 158
    //   180: aload_2
    //   181: areturn
    //   182: astore_0
    //   183: aload_0
    //   184: athrow
    //   185: astore_2
    //   186: aload_0
    //   187: astore 4
    //   189: aload_3
    //   190: ifnull +10 -> 200
    //   193: aload_3
    //   194: invokevirtual 118	java/io/ByteArrayInputStream:close	()V
    //   197: aload_0
    //   198: astore 4
    //   200: aload 4
    //   202: ifnull +25 -> 227
    //   205: aload 4
    //   207: athrow
    //   208: aload_0
    //   209: astore 4
    //   211: aload_0
    //   212: aload_3
    //   213: if_acmpeq -13 -> 200
    //   216: aload_0
    //   217: aload_3
    //   218: invokevirtual 104	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   221: aload_0
    //   222: astore 4
    //   224: goto -24 -> 200
    //   227: aload_2
    //   228: athrow
    //   229: astore 4
    //   231: aconst_null
    //   232: astore_0
    //   233: aload_2
    //   234: astore_3
    //   235: aload 4
    //   237: astore_2
    //   238: goto -52 -> 186
    //   241: astore_2
    //   242: aconst_null
    //   243: astore 4
    //   245: aload_0
    //   246: astore_3
    //   247: aload 4
    //   249: astore_0
    //   250: goto -64 -> 186
    //   253: astore_2
    //   254: aload_0
    //   255: astore_3
    //   256: aload_2
    //   257: astore_0
    //   258: goto -75 -> 183
    //   261: astore_2
    //   262: aconst_null
    //   263: astore_0
    //   264: aload 4
    //   266: astore_3
    //   267: goto -202 -> 65
    //   270: astore 4
    //   272: aconst_null
    //   273: astore_0
    //   274: aload_2
    //   275: astore_3
    //   276: aload 4
    //   278: astore_2
    //   279: goto -214 -> 65
    //   282: astore_0
    //   283: aload_2
    //   284: astore_3
    //   285: goto -223 -> 62
    //   288: astore_0
    //   289: goto -243 -> 46
    //   292: astore_3
    //   293: aload_0
    //   294: ifnonnull -207 -> 87
    //   297: aload_3
    //   298: astore 4
    //   300: goto -221 -> 79
    //   303: astore_0
    //   304: goto -139 -> 165
    //   307: astore_3
    //   308: aload_0
    //   309: ifnonnull -101 -> 208
    //   312: aload_3
    //   313: astore 4
    //   315: goto -115 -> 200
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	318	0	paramBitmap	android.graphics.Bitmap
    //   0	318	1	paramInt	int
    //   12	22	2	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   64	43	2	localObject1	Object
    //   130	51	2	localJfifImage	JfifImage
    //   185	49	2	localObject2	Object
    //   237	1	2	localObject3	Object
    //   241	1	2	localObject4	Object
    //   253	4	2	localThrowable1	Throwable
    //   261	14	2	localObject5	Object
    //   278	6	2	localObject6	Object
    //   4	281	3	localObject7	Object
    //   292	6	3	localThrowable2	Throwable
    //   307	6	3	localThrowable3	Throwable
    //   1	222	4	localObject8	Object
    //   229	7	4	localObject9	Object
    //   243	22	4	localObject10	Object
    //   270	7	4	localObject11	Object
    //   298	16	4	localObject12	Object
    // Exception table:
    //   from	to	target	type
    //   43	45	45	java/lang/Throwable
    //   33	37	57	java/lang/Throwable
    //   5	13	61	java/lang/Throwable
    //   62	64	64	finally
    //   162	164	164	java/lang/Throwable
    //   152	156	176	java/lang/Throwable
    //   133	143	182	java/lang/Throwable
    //   183	185	185	finally
    //   133	143	229	finally
    //   143	148	241	finally
    //   143	148	253	java/lang/Throwable
    //   5	13	261	finally
    //   13	29	270	finally
    //   13	29	282	java/lang/Throwable
    //   84	87	288	java/lang/Throwable
    //   95	100	288	java/lang/Throwable
    //   106	108	288	java/lang/Throwable
    //   72	76	292	java/lang/Throwable
    //   205	208	303	java/lang/Throwable
    //   216	221	303	java/lang/Throwable
    //   227	229	303	java/lang/Throwable
    //   193	197	307	java/lang/Throwable
  }
  
  public static JfifImage create(InputStream paramInputStream)
  {
    JfifImage localJfifImage = new JfifImage();
    if (localJfifImage.read(paramInputStream)) {
      return localJfifImage;
    }
    throw new IllegalStateException("Unsupported input stream to create a Jfif image");
  }
  
  private void extractXMPSegment()
  {
    Object localObject = this.m_Segments.iterator();
    while (((Iterator)localObject).hasNext())
    {
      JfifSegment localJfifSegment = (JfifSegment)((Iterator)localObject).next();
      if (isXMPSegment(localJfifSegment))
      {
        this.m_XMPSegment = localJfifSegment;
        localObject = new byte[findXMPContentEndIndex(localJfifSegment.data) - XMP_HEADER_SIZE];
        System.arraycopy(localJfifSegment.data, XMP_HEADER_SIZE, localObject, 0, localObject.length);
        try
        {
          this.m_SimpleXMPContainer = new SimpleXMPContainer(XMPMetaFactory.parseFromBuffer((byte[])localObject));
          return;
        }
        catch (Throwable localThrowable)
        {
          Log.e(TAG, "extractXMPSegment() - Fail to extract XMP segment", localThrowable);
          return;
        }
      }
    }
  }
  
  private int findXMPContentEndIndex(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length - 1;
    while (i >= 1)
    {
      if ((paramArrayOfByte[i] == 62) && (paramArrayOfByte[(i - 1)] != 63)) {
        return i + 1;
      }
      i -= 1;
    }
    return paramArrayOfByte.length;
  }
  
  private boolean isXMPSegment(JfifSegment paramJfifSegment)
  {
    if (paramJfifSegment == null) {
      return false;
    }
    if (paramJfifSegment.data.length < XMP_HEADER_SIZE) {
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[XMP_HEADER_SIZE];
      System.arraycopy(paramJfifSegment.data, 0, arrayOfByte, 0, XMP_HEADER_SIZE);
      boolean bool = new String(arrayOfByte, "UTF-8").equals("http://ns.adobe.com/xap/1.0/\000");
      if (bool) {
        return true;
      }
    }
    catch (Throwable paramJfifSegment)
    {
      Log.e(TAG, "isXMPSegment() - Fail to check XMP segment");
    }
    return false;
  }
  
  /* Error */
  private boolean read(InputStream paramInputStream)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 198	com/oneplus/media/JfifImage:m_IsClosed	Z
    //   4: ifeq +13 -> 17
    //   7: new 127	java/lang/IllegalStateException
    //   10: dup
    //   11: ldc -56
    //   13: invokespecial 132	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   16: athrow
    //   17: aload_0
    //   18: iconst_0
    //   19: putfield 202	com/oneplus/media/JfifImage:m_IsXMPModified	Z
    //   22: aconst_null
    //   23: astore 7
    //   25: aconst_null
    //   26: astore 8
    //   28: aconst_null
    //   29: astore 9
    //   31: aconst_null
    //   32: astore 10
    //   34: aconst_null
    //   35: astore 11
    //   37: aconst_null
    //   38: astore 6
    //   40: aconst_null
    //   41: astore 13
    //   43: aconst_null
    //   44: astore 14
    //   46: aconst_null
    //   47: astore 12
    //   49: new 204	com/oneplus/io/StreamState
    //   52: dup
    //   53: aload_1
    //   54: invokespecial 207	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   57: astore 5
    //   59: aload_1
    //   60: invokevirtual 211	java/io/InputStream:read	()I
    //   63: sipush 255
    //   66: if_icmpne +13 -> 79
    //   69: aload_1
    //   70: invokevirtual 211	java/io/InputStream:read	()I
    //   73: sipush 216
    //   76: if_icmpeq +99 -> 175
    //   79: getstatic 49	com/oneplus/media/JfifImage:TAG	Ljava/lang/String;
    //   82: ldc -43
    //   84: invokestatic 216	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   87: aload 13
    //   89: astore_1
    //   90: aload 5
    //   92: ifnull +11 -> 103
    //   95: aload 5
    //   97: invokevirtual 217	com/oneplus/io/StreamState:close	()V
    //   100: aload 13
    //   102: astore_1
    //   103: aload_1
    //   104: ifnull +21 -> 125
    //   107: aload_1
    //   108: athrow
    //   109: astore_1
    //   110: getstatic 49	com/oneplus/media/JfifImage:TAG	Ljava/lang/String;
    //   113: ldc -37
    //   115: aload_1
    //   116: invokestatic 100	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   119: iconst_0
    //   120: ireturn
    //   121: astore_1
    //   122: goto -19 -> 103
    //   125: iconst_0
    //   126: ireturn
    //   127: iload_3
    //   128: bipush 8
    //   130: ishl
    //   131: iload 4
    //   133: ior
    //   134: iconst_2
    //   135: isub
    //   136: istore_3
    //   137: iload_3
    //   138: newarray <illegal type>
    //   140: astore 12
    //   142: aload_1
    //   143: aload 12
    //   145: iconst_0
    //   146: iload_3
    //   147: invokevirtual 222	java/io/InputStream:read	([BII)I
    //   150: pop
    //   151: new 151	com/oneplus/media/JfifSegment
    //   154: dup
    //   155: iload_2
    //   156: aload 12
    //   158: invokespecial 225	com/oneplus/media/JfifSegment:<init>	(I[B)V
    //   161: astore 12
    //   163: aload_0
    //   164: getfield 66	com/oneplus/media/JfifImage:m_Segments	Ljava/util/List;
    //   167: aload 12
    //   169: invokeinterface 230 2 0
    //   174: pop
    //   175: aload_1
    //   176: invokevirtual 211	java/io/InputStream:read	()I
    //   179: istore_2
    //   180: iload_2
    //   181: iconst_m1
    //   182: if_icmpeq +217 -> 399
    //   185: iload_2
    //   186: sipush 255
    //   189: if_icmpeq +39 -> 228
    //   192: getstatic 49	com/oneplus/media/JfifImage:TAG	Ljava/lang/String;
    //   195: ldc -24
    //   197: invokestatic 216	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   200: aload 7
    //   202: astore_1
    //   203: aload 5
    //   205: ifnull +11 -> 216
    //   208: aload 5
    //   210: invokevirtual 217	com/oneplus/io/StreamState:close	()V
    //   213: aload 7
    //   215: astore_1
    //   216: aload_1
    //   217: ifnull +9 -> 226
    //   220: aload_1
    //   221: athrow
    //   222: astore_1
    //   223: goto -7 -> 216
    //   226: iconst_0
    //   227: ireturn
    //   228: aload_1
    //   229: invokevirtual 211	java/io/InputStream:read	()I
    //   232: istore_2
    //   233: iload_2
    //   234: sipush 255
    //   237: if_icmpeq -9 -> 228
    //   240: iload_2
    //   241: iconst_m1
    //   242: if_icmpne +39 -> 281
    //   245: getstatic 49	com/oneplus/media/JfifImage:TAG	Ljava/lang/String;
    //   248: ldc -22
    //   250: invokestatic 216	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   253: aload 8
    //   255: astore_1
    //   256: aload 5
    //   258: ifnull +11 -> 269
    //   261: aload 5
    //   263: invokevirtual 217	com/oneplus/io/StreamState:close	()V
    //   266: aload 8
    //   268: astore_1
    //   269: aload_1
    //   270: ifnull +9 -> 279
    //   273: aload_1
    //   274: athrow
    //   275: astore_1
    //   276: goto -7 -> 269
    //   279: iconst_0
    //   280: ireturn
    //   281: iload_2
    //   282: sipush 218
    //   285: if_icmpne +56 -> 341
    //   288: aload_1
    //   289: invokevirtual 237	java/io/InputStream:available	()I
    //   292: iconst_2
    //   293: isub
    //   294: istore_2
    //   295: aload_0
    //   296: iload_2
    //   297: newarray <illegal type>
    //   299: putfield 239	com/oneplus/media/JfifImage:m_CompressedImageData	[B
    //   302: aload_1
    //   303: aload_0
    //   304: getfield 239	com/oneplus/media/JfifImage:m_CompressedImageData	[B
    //   307: iconst_0
    //   308: iload_2
    //   309: invokevirtual 222	java/io/InputStream:read	([BII)I
    //   312: pop
    //   313: aload 9
    //   315: astore_1
    //   316: aload 5
    //   318: ifnull +11 -> 329
    //   321: aload 5
    //   323: invokevirtual 217	com/oneplus/io/StreamState:close	()V
    //   326: aload 9
    //   328: astore_1
    //   329: aload_1
    //   330: ifnull +9 -> 339
    //   333: aload_1
    //   334: athrow
    //   335: astore_1
    //   336: goto -7 -> 329
    //   339: iconst_1
    //   340: ireturn
    //   341: aload_1
    //   342: invokevirtual 211	java/io/InputStream:read	()I
    //   345: istore_3
    //   346: aload_1
    //   347: invokevirtual 211	java/io/InputStream:read	()I
    //   350: istore 4
    //   352: iload_3
    //   353: iconst_m1
    //   354: if_icmpeq +9 -> 363
    //   357: iload 4
    //   359: iconst_m1
    //   360: if_icmpne -233 -> 127
    //   363: getstatic 49	com/oneplus/media/JfifImage:TAG	Ljava/lang/String;
    //   366: ldc -15
    //   368: invokestatic 216	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   371: aload 10
    //   373: astore_1
    //   374: aload 5
    //   376: ifnull +11 -> 387
    //   379: aload 5
    //   381: invokevirtual 217	com/oneplus/io/StreamState:close	()V
    //   384: aload 10
    //   386: astore_1
    //   387: aload_1
    //   388: ifnull +9 -> 397
    //   391: aload_1
    //   392: athrow
    //   393: astore_1
    //   394: goto -7 -> 387
    //   397: iconst_0
    //   398: ireturn
    //   399: aload 11
    //   401: astore_1
    //   402: aload 5
    //   404: ifnull +11 -> 415
    //   407: aload 5
    //   409: invokevirtual 217	com/oneplus/io/StreamState:close	()V
    //   412: aload 11
    //   414: astore_1
    //   415: aload_1
    //   416: ifnull +77 -> 493
    //   419: aload_1
    //   420: athrow
    //   421: astore_1
    //   422: goto -7 -> 415
    //   425: astore 5
    //   427: aload 12
    //   429: astore_1
    //   430: aload 5
    //   432: athrow
    //   433: astore 7
    //   435: aload 5
    //   437: astore 6
    //   439: aload 7
    //   441: astore 5
    //   443: aload 6
    //   445: astore 7
    //   447: aload_1
    //   448: ifnull +11 -> 459
    //   451: aload_1
    //   452: invokevirtual 217	com/oneplus/io/StreamState:close	()V
    //   455: aload 6
    //   457: astore 7
    //   459: aload 7
    //   461: ifnull +29 -> 490
    //   464: aload 7
    //   466: athrow
    //   467: aload 6
    //   469: astore 7
    //   471: aload 6
    //   473: aload_1
    //   474: if_acmpeq -15 -> 459
    //   477: aload 6
    //   479: aload_1
    //   480: invokevirtual 104	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   483: aload 6
    //   485: astore 7
    //   487: goto -28 -> 459
    //   490: aload 5
    //   492: athrow
    //   493: iconst_1
    //   494: ireturn
    //   495: astore 5
    //   497: aload 14
    //   499: astore_1
    //   500: goto -57 -> 443
    //   503: astore 7
    //   505: aload 5
    //   507: astore_1
    //   508: aload 7
    //   510: astore 5
    //   512: goto -69 -> 443
    //   515: astore 6
    //   517: aload 5
    //   519: astore_1
    //   520: aload 6
    //   522: astore 5
    //   524: goto -94 -> 430
    //   527: astore_1
    //   528: goto -418 -> 110
    //   531: astore_1
    //   532: aload 6
    //   534: ifnonnull -67 -> 467
    //   537: aload_1
    //   538: astore 7
    //   540: goto -81 -> 459
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	543	0	this	JfifImage
    //   0	543	1	paramInputStream	InputStream
    //   155	154	2	i	int
    //   127	228	3	j	int
    //   131	230	4	k	int
    //   57	351	5	localStreamState	com.oneplus.io.StreamState
    //   425	11	5	localThrowable1	Throwable
    //   441	50	5	localObject1	Object
    //   495	11	5	localObject2	Object
    //   510	13	5	localObject3	Object
    //   38	446	6	localObject4	Object
    //   515	18	6	localThrowable2	Throwable
    //   23	191	7	localObject5	Object
    //   433	7	7	localObject6	Object
    //   445	41	7	localObject7	Object
    //   503	6	7	localObject8	Object
    //   538	1	7	localInputStream	InputStream
    //   26	241	8	localObject9	Object
    //   29	298	9	localObject10	Object
    //   32	353	10	localObject11	Object
    //   35	378	11	localObject12	Object
    //   47	381	12	localObject13	Object
    //   41	60	13	localObject14	Object
    //   44	454	14	localObject15	Object
    // Exception table:
    //   from	to	target	type
    //   107	109	109	java/lang/Throwable
    //   220	222	109	java/lang/Throwable
    //   273	275	109	java/lang/Throwable
    //   333	335	109	java/lang/Throwable
    //   391	393	109	java/lang/Throwable
    //   419	421	109	java/lang/Throwable
    //   95	100	121	java/lang/Throwable
    //   208	213	222	java/lang/Throwable
    //   261	266	275	java/lang/Throwable
    //   321	326	335	java/lang/Throwable
    //   379	384	393	java/lang/Throwable
    //   407	412	421	java/lang/Throwable
    //   49	59	425	java/lang/Throwable
    //   430	433	433	finally
    //   49	59	495	finally
    //   59	79	503	finally
    //   79	87	503	finally
    //   137	175	503	finally
    //   175	180	503	finally
    //   192	200	503	finally
    //   228	233	503	finally
    //   245	253	503	finally
    //   288	313	503	finally
    //   341	352	503	finally
    //   363	371	503	finally
    //   59	79	515	java/lang/Throwable
    //   79	87	515	java/lang/Throwable
    //   137	175	515	java/lang/Throwable
    //   175	180	515	java/lang/Throwable
    //   192	200	515	java/lang/Throwable
    //   228	233	515	java/lang/Throwable
    //   245	253	515	java/lang/Throwable
    //   288	313	515	java/lang/Throwable
    //   341	352	515	java/lang/Throwable
    //   363	371	515	java/lang/Throwable
    //   464	467	527	java/lang/Throwable
    //   477	483	527	java/lang/Throwable
    //   490	493	527	java/lang/Throwable
    //   451	455	531	java/lang/Throwable
  }
  
  private void updateXMPSegment()
  {
    Object localObject2;
    if ((this.m_SimpleXMPContainer != null) && (this.m_IsXMPModified))
    {
      localObject2 = this.m_SimpleXMPContainer.getXMPMeta();
      if (localObject2 != null) {}
    }
    else
    {
      return;
    }
    Object localObject1 = null;
    try
    {
      SerializeOptions localSerializeOptions = new SerializeOptions();
      localSerializeOptions.setUseCompactFormat(true);
      localSerializeOptions.setOmitPacketWrapper(true);
      localObject2 = XMPMetaFactory.serializeToBuffer((XMPMeta)localObject2, localSerializeOptions);
      localObject1 = localObject2;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(TAG, "serializeXMPMeta() - Error when serializing XMP", localThrowable);
      }
      if (localObject1.length <= 65502) {
        break label94;
      }
      return;
      label94:
      byte[] arrayOfByte = new byte[XMP_HEADER_SIZE + localObject1.length];
      System.arraycopy("http://ns.adobe.com/xap/1.0/\000".getBytes(), 0, arrayOfByte, 0, XMP_HEADER_SIZE);
      System.arraycopy(localObject1, 0, arrayOfByte, XMP_HEADER_SIZE, localObject1.length);
      localObject1 = this.m_XMPSegment;
      this.m_XMPSegment = new JfifSegment(225, arrayOfByte);
      if (localObject1 != null) {
        break label190;
      }
    }
    if ((localObject1 == null) || (localObject1.length <= 0)) {
      return;
    }
    if (((JfifSegment)this.m_Segments.get(0)).marker == 225) {}
    for (int i = 1;; i = 0)
    {
      this.m_Segments.add(i, this.m_XMPSegment);
      label190:
      this.m_IsXMPModified = false;
      return;
    }
  }
  
  public void clearXMPMeta()
  {
    if (this.m_SimpleXMPContainer == null) {
      return;
    }
    this.m_SimpleXMPContainer.clearXMPMeta();
    this.m_SimpleXMPContainer = null;
    if (this.m_XMPSegment != null)
    {
      this.m_Segments.remove(this.m_XMPSegment);
      this.m_XMPSegment = null;
    }
    this.m_IsXMPModified = true;
  }
  
  public void close()
    throws Exception
  {
    if (this.m_IsClosed) {
      return;
    }
    this.m_CompressedImageData = null;
    this.m_XMPSegment = null;
    this.m_Segments.clear();
    this.m_SimpleXMPContainer.clearXMPMeta();
    this.m_IsClosed = true;
  }
  
  public void deleteProperty(XMPPropertyKey paramXMPPropertyKey)
  {
    if ((this.m_SimpleXMPContainer == null) || (paramXMPPropertyKey == null)) {
      return;
    }
    this.m_SimpleXMPContainer.deleteProperty(paramXMPPropertyKey);
  }
  
  public byte[] getCompressedImageData()
  {
    if (this.m_IsClosed) {
      throw new IllegalStateException("JfifImage is closed");
    }
    return this.m_CompressedImageData;
  }
  
  public List<JfifSegment> getSegments()
  {
    if (this.m_IsClosed) {
      throw new IllegalStateException("JfifImage is closed");
    }
    return this.m_Segments;
  }
  
  public XMPMeta getXMPMeta()
  {
    if (this.m_SimpleXMPContainer == null)
    {
      extractXMPSegment();
      if (this.m_SimpleXMPContainer == null) {
        return null;
      }
    }
    return this.m_SimpleXMPContainer.getXMPMeta();
  }
  
  public XMPProperty getXMPProperty(XMPPropertyKey paramXMPPropertyKey)
  {
    if (paramXMPPropertyKey == null) {
      return null;
    }
    if (this.m_SimpleXMPContainer == null)
    {
      extractXMPSegment();
      if (this.m_SimpleXMPContainer == null) {
        return null;
      }
    }
    return this.m_SimpleXMPContainer.getXMPProperty(paramXMPPropertyKey);
  }
  
  public boolean isClosed()
  {
    return this.m_IsClosed;
  }
  
  public String registerXMPNamespace(String paramString1, String paramString2)
  {
    return this.m_SimpleXMPContainer.registerXMPNamespace(paramString1, paramString2);
  }
  
  public void replaceXMPMeta(XMPMeta paramXMPMeta)
  {
    if (this.m_SimpleXMPContainer == null)
    {
      if (paramXMPMeta != null)
      {
        this.m_SimpleXMPContainer = new SimpleXMPContainer(paramXMPMeta);
        this.m_IsXMPModified = true;
      }
      return;
    }
    this.m_SimpleXMPContainer.replaceXMPMeta(paramXMPMeta);
    this.m_IsXMPModified = true;
  }
  
  public boolean save(OutputStream paramOutputStream)
  {
    return write(paramOutputStream);
  }
  
  public void setXMPProperty(XMPPropertyKey paramXMPPropertyKey, Object paramObject)
  {
    if (paramXMPPropertyKey == null) {
      return;
    }
    if (this.m_SimpleXMPContainer == null)
    {
      extractXMPSegment();
      if (this.m_SimpleXMPContainer == null) {
        this.m_SimpleXMPContainer = new SimpleXMPContainer(null);
      }
    }
    if (paramObject == null)
    {
      this.m_SimpleXMPContainer.deleteProperty(paramXMPPropertyKey);
      return;
    }
    XMPProperty localXMPProperty = this.m_SimpleXMPContainer.getXMPProperty(paramXMPPropertyKey);
    if ((localXMPProperty != null) && (paramObject.equals(localXMPProperty.getValue()))) {
      return;
    }
    this.m_SimpleXMPContainer.setXMPProperty(paramXMPPropertyKey, paramObject);
    this.m_IsXMPModified = true;
  }
  
  public boolean write(OutputStream paramOutputStream)
  {
    if (this.m_IsClosed) {
      throw new IllegalStateException("JfifImage is closed");
    }
    updateXMPSegment();
    try
    {
      paramOutputStream.write(255);
      paramOutputStream.write(216);
      Iterator localIterator = this.m_Segments.iterator();
      while (localIterator.hasNext())
      {
        JfifSegment localJfifSegment = (JfifSegment)localIterator.next();
        paramOutputStream.write(255);
        paramOutputStream.write(localJfifSegment.marker);
        int i = localJfifSegment.data.length + 2;
        if (i > 0)
        {
          paramOutputStream.write(i >> 8 & 0xFF);
          paramOutputStream.write(i & 0xFF);
        }
        paramOutputStream.write(localJfifSegment.data);
      }
      paramOutputStream.write(255);
    }
    catch (Throwable paramOutputStream)
    {
      Log.e(TAG, "write() - Error to write image", paramOutputStream);
      return false;
    }
    paramOutputStream.write(218);
    if ((this.m_CompressedImageData != null) && (this.m_CompressedImageData.length > 0)) {
      paramOutputStream.write(this.m_CompressedImageData);
    }
    paramOutputStream.write(255);
    paramOutputStream.write(217);
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/JfifImage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */