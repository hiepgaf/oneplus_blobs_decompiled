package com.amap.api.mapcore2d;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class co
{
  public static String a()
  {
    Object localObject = null;
    try
    {
      String str = String.valueOf(System.currentTimeMillis());
      localObject = str;
      int i = str.length();
      localObject = str;
      str = str.substring(0, i - 2) + "1" + str.substring(i - 1);
      return str;
    }
    catch (Throwable localThrowable)
    {
      cy.a(localThrowable, "CInfo", "getTS");
    }
    return (String)localObject;
  }
  
  public static String a(Context paramContext)
  {
    try
    {
      a locala = new a(null);
      locala.d = cl.c(paramContext);
      locala.i = cl.d(paramContext);
      paramContext = a(paramContext, locala);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "CInfo", "InitXInfo");
    }
    return null;
  }
  
  private static String a(Context paramContext, a parama)
  {
    return cq.a(b(paramContext, parama));
  }
  
  @Deprecated
  public static String a(Context paramContext, cu paramcu, Map<String, String> paramMap, boolean paramBoolean)
  {
    try
    {
      paramContext = a(paramContext, b(paramContext, paramBoolean));
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "CInfo", "rsaLocClineInfo");
    }
    return null;
  }
  
  public static String a(Context paramContext, String paramString1, String paramString2)
  {
    try
    {
      paramContext = cl.e(paramContext);
      paramContext = cr.b(paramContext + ":" + paramString1.substring(0, paramString1.length() - 3) + ":" + paramString2);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "CInfo", "Scode");
    }
    return null;
  }
  
  public static void a(ByteArrayOutputStream paramByteArrayOutputStream, String paramString)
  {
    if (TextUtils.isEmpty(paramString))
    {
      cv.a(paramByteArrayOutputStream, (byte)0, new byte[0]);
      return;
    }
    if (paramString.getBytes().length <= 255) {}
    for (byte b = (byte)paramString.getBytes().length;; b = -1)
    {
      cv.a(paramByteArrayOutputStream, b, cv.a(paramString));
      return;
    }
  }
  
  private static byte[] a(Context paramContext, ByteArrayOutputStream paramByteArrayOutputStream)
    throws CertificateException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException
  {
    return c(paramContext, cv.b(paramByteArrayOutputStream.toByteArray()));
  }
  
  public static byte[] a(Context paramContext, boolean paramBoolean)
  {
    try
    {
      paramContext = b(paramContext, b(paramContext, paramBoolean));
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "CInfo", "getGZipXInfo");
    }
    return null;
  }
  
  public static byte[] a(Context paramContext, byte[] paramArrayOfByte)
    throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, NullPointerException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
  {
    Object localObject = KeyGenerator.getInstance("AES");
    if (localObject != null)
    {
      ((KeyGenerator)localObject).init(256);
      localObject = ((KeyGenerator)localObject).generateKey().getEncoded();
      paramContext = cv.a(paramContext);
      if (paramContext != null)
      {
        paramContext = cq.a((byte[])localObject, paramContext);
        paramArrayOfByte = cq.a((byte[])localObject, paramArrayOfByte);
        localObject = new byte[paramContext.length + paramArrayOfByte.length];
        System.arraycopy(paramContext, 0, localObject, 0, paramContext.length);
        System.arraycopy(paramArrayOfByte, 0, localObject, paramContext.length, paramArrayOfByte.length);
        return (byte[])localObject;
      }
    }
    else
    {
      return null;
    }
    return null;
  }
  
  private static a b(Context paramContext, boolean paramBoolean)
  {
    a locala = new a(null);
    locala.a = cp.q(paramContext);
    locala.b = cp.i(paramContext);
    String str = cp.f(paramContext);
    if (str != null)
    {
      locala.c = str;
      locala.d = cl.c(paramContext);
      locala.e = Build.MODEL;
      locala.f = Build.MANUFACTURER;
      locala.g = Build.DEVICE;
      locala.h = cl.b(paramContext);
      locala.i = cl.d(paramContext);
      locala.j = String.valueOf(Build.VERSION.SDK_INT);
      locala.k = cp.r(paramContext);
      locala.l = cp.p(paramContext);
      locala.m = (cp.m(paramContext) + "");
      locala.n = (cp.l(paramContext) + "");
      locala.o = cp.s(paramContext);
      locala.p = cp.k(paramContext);
      if (paramBoolean) {
        break label236;
      }
      locala.q = cp.h(paramContext);
      label192:
      if (paramBoolean) {
        break label246;
      }
    }
    label236:
    label246:
    for (locala.r = cp.g(paramContext);; locala.r = "")
    {
      if (paramBoolean) {
        break label256;
      }
      paramContext = cp.j(paramContext);
      locala.s = paramContext[0];
      locala.t = paramContext[1];
      return locala;
      str = "";
      break;
      locala.q = "";
      break label192;
    }
    label256:
    locala.s = "";
    locala.t = "";
    return locala;
  }
  
  public static String b(Context paramContext, byte[] paramArrayOfByte)
  {
    try
    {
      paramContext = d(paramContext, paramArrayOfByte);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "CInfo", "AESData");
    }
    return "";
  }
  
  /* Error */
  private static byte[] b(Context paramContext, a parama)
  {
    // Byte code:
    //   0: new 153	java/io/ByteArrayOutputStream
    //   3: dup
    //   4: invokespecial 312	java/io/ByteArrayOutputStream:<init>	()V
    //   7: astore_3
    //   8: aload_3
    //   9: astore_2
    //   10: aload_3
    //   11: aload_1
    //   12: getfield 209	com/amap/api/mapcore2d/co$a:a	Ljava/lang/String;
    //   15: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   18: aload_3
    //   19: astore_2
    //   20: aload_3
    //   21: aload_1
    //   22: getfield 213	com/amap/api/mapcore2d/co$a:b	Ljava/lang/String;
    //   25: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   28: aload_3
    //   29: astore_2
    //   30: aload_3
    //   31: aload_1
    //   32: getfield 218	com/amap/api/mapcore2d/co$a:c	Ljava/lang/String;
    //   35: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   38: aload_3
    //   39: astore_2
    //   40: aload_3
    //   41: aload_1
    //   42: getfield 73	com/amap/api/mapcore2d/co$a:d	Ljava/lang/String;
    //   45: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   48: aload_3
    //   49: astore_2
    //   50: aload_3
    //   51: aload_1
    //   52: getfield 225	com/amap/api/mapcore2d/co$a:e	Ljava/lang/String;
    //   55: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   58: aload_3
    //   59: astore_2
    //   60: aload_3
    //   61: aload_1
    //   62: getfield 230	com/amap/api/mapcore2d/co$a:f	Ljava/lang/String;
    //   65: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   68: aload_3
    //   69: astore_2
    //   70: aload_3
    //   71: aload_1
    //   72: getfield 236	com/amap/api/mapcore2d/co$a:g	Ljava/lang/String;
    //   75: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   78: aload_3
    //   79: astore_2
    //   80: aload_3
    //   81: aload_1
    //   82: getfield 241	com/amap/api/mapcore2d/co$a:h	Ljava/lang/String;
    //   85: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   88: aload_3
    //   89: astore_2
    //   90: aload_3
    //   91: aload_1
    //   92: getfield 78	com/amap/api/mapcore2d/co$a:i	Ljava/lang/String;
    //   95: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   98: aload_3
    //   99: astore_2
    //   100: aload_3
    //   101: aload_1
    //   102: getfield 252	com/amap/api/mapcore2d/co$a:j	Ljava/lang/String;
    //   105: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   108: aload_3
    //   109: astore_2
    //   110: aload_3
    //   111: aload_1
    //   112: getfield 258	com/amap/api/mapcore2d/co$a:k	Ljava/lang/String;
    //   115: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   118: aload_3
    //   119: astore_2
    //   120: aload_3
    //   121: aload_1
    //   122: getfield 264	com/amap/api/mapcore2d/co$a:l	Ljava/lang/String;
    //   125: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   128: aload_3
    //   129: astore_2
    //   130: aload_3
    //   131: aload_1
    //   132: getfield 275	com/amap/api/mapcore2d/co$a:m	Ljava/lang/String;
    //   135: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   138: aload_3
    //   139: astore_2
    //   140: aload_3
    //   141: aload_1
    //   142: getfield 280	com/amap/api/mapcore2d/co$a:n	Ljava/lang/String;
    //   145: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   148: aload_3
    //   149: astore_2
    //   150: aload_3
    //   151: aload_1
    //   152: getfield 286	com/amap/api/mapcore2d/co$a:o	Ljava/lang/String;
    //   155: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   158: aload_3
    //   159: astore_2
    //   160: aload_3
    //   161: aload_1
    //   162: getfield 290	com/amap/api/mapcore2d/co$a:p	Ljava/lang/String;
    //   165: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   168: aload_3
    //   169: astore_2
    //   170: aload_3
    //   171: aload_1
    //   172: getfield 294	com/amap/api/mapcore2d/co$a:q	Ljava/lang/String;
    //   175: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   178: aload_3
    //   179: astore_2
    //   180: aload_3
    //   181: aload_1
    //   182: getfield 298	com/amap/api/mapcore2d/co$a:r	Ljava/lang/String;
    //   185: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   188: aload_3
    //   189: astore_2
    //   190: aload_3
    //   191: aload_1
    //   192: getfield 303	com/amap/api/mapcore2d/co$a:s	Ljava/lang/String;
    //   195: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   198: aload_3
    //   199: astore_2
    //   200: aload_3
    //   201: aload_1
    //   202: getfield 306	com/amap/api/mapcore2d/co$a:t	Ljava/lang/String;
    //   205: invokestatic 314	com/amap/api/mapcore2d/co:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   208: aload_3
    //   209: astore_2
    //   210: aload_0
    //   211: aload_3
    //   212: invokestatic 316	com/amap/api/mapcore2d/co:a	(Landroid/content/Context;Ljava/io/ByteArrayOutputStream;)[B
    //   215: astore_0
    //   216: aload_3
    //   217: ifnonnull +5 -> 222
    //   220: aload_0
    //   221: areturn
    //   222: aload_3
    //   223: invokevirtual 319	java/io/ByteArrayOutputStream:close	()V
    //   226: aload_0
    //   227: areturn
    //   228: astore_1
    //   229: aload_1
    //   230: invokevirtual 322	java/lang/Throwable:printStackTrace	()V
    //   233: aload_0
    //   234: areturn
    //   235: astore_1
    //   236: aconst_null
    //   237: astore_0
    //   238: aload_0
    //   239: astore_2
    //   240: aload_1
    //   241: ldc 52
    //   243: ldc 83
    //   245: invokestatic 59	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   248: aload_0
    //   249: ifnonnull +5 -> 254
    //   252: aconst_null
    //   253: areturn
    //   254: aload_0
    //   255: invokevirtual 319	java/io/ByteArrayOutputStream:close	()V
    //   258: aconst_null
    //   259: areturn
    //   260: astore_0
    //   261: aload_0
    //   262: invokevirtual 322	java/lang/Throwable:printStackTrace	()V
    //   265: aconst_null
    //   266: areturn
    //   267: astore_0
    //   268: aconst_null
    //   269: astore_2
    //   270: aload_2
    //   271: ifnonnull +5 -> 276
    //   274: aload_0
    //   275: athrow
    //   276: aload_2
    //   277: invokevirtual 319	java/io/ByteArrayOutputStream:close	()V
    //   280: goto -6 -> 274
    //   283: astore_1
    //   284: aload_1
    //   285: invokevirtual 322	java/lang/Throwable:printStackTrace	()V
    //   288: goto -14 -> 274
    //   291: astore_0
    //   292: goto -22 -> 270
    //   295: astore_1
    //   296: aload_3
    //   297: astore_0
    //   298: goto -60 -> 238
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	301	0	paramContext	Context
    //   0	301	1	parama	a
    //   9	268	2	localObject	Object
    //   7	290	3	localByteArrayOutputStream	ByteArrayOutputStream
    // Exception table:
    //   from	to	target	type
    //   222	226	228	java/lang/Throwable
    //   0	8	235	java/lang/Throwable
    //   254	258	260	java/lang/Throwable
    //   0	8	267	finally
    //   276	280	283	java/lang/Throwable
    //   10	18	291	finally
    //   20	28	291	finally
    //   30	38	291	finally
    //   40	48	291	finally
    //   50	58	291	finally
    //   60	68	291	finally
    //   70	78	291	finally
    //   80	88	291	finally
    //   90	98	291	finally
    //   100	108	291	finally
    //   110	118	291	finally
    //   120	128	291	finally
    //   130	138	291	finally
    //   140	148	291	finally
    //   150	158	291	finally
    //   160	168	291	finally
    //   170	178	291	finally
    //   180	188	291	finally
    //   190	198	291	finally
    //   200	208	291	finally
    //   210	216	291	finally
    //   240	248	291	finally
    //   10	18	295	java/lang/Throwable
    //   20	28	295	java/lang/Throwable
    //   30	38	295	java/lang/Throwable
    //   40	48	295	java/lang/Throwable
    //   50	58	295	java/lang/Throwable
    //   60	68	295	java/lang/Throwable
    //   70	78	295	java/lang/Throwable
    //   80	88	295	java/lang/Throwable
    //   90	98	295	java/lang/Throwable
    //   100	108	295	java/lang/Throwable
    //   110	118	295	java/lang/Throwable
    //   120	128	295	java/lang/Throwable
    //   130	138	295	java/lang/Throwable
    //   140	148	295	java/lang/Throwable
    //   150	158	295	java/lang/Throwable
    //   160	168	295	java/lang/Throwable
    //   170	178	295	java/lang/Throwable
    //   180	188	295	java/lang/Throwable
    //   190	198	295	java/lang/Throwable
    //   200	208	295	java/lang/Throwable
    //   210	216	295	java/lang/Throwable
  }
  
  public static byte[] c(Context paramContext, byte[] paramArrayOfByte)
    throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, NullPointerException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
  {
    paramContext = cv.a(paramContext);
    if (paramArrayOfByte.length <= 117) {
      return cq.a(paramArrayOfByte, paramContext);
    }
    byte[] arrayOfByte = new byte[117];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, 117);
    paramContext = cq.a(arrayOfByte, paramContext);
    arrayOfByte = new byte[paramArrayOfByte.length + 128 - 117];
    System.arraycopy(paramContext, 0, arrayOfByte, 0, 128);
    System.arraycopy(paramArrayOfByte, 117, arrayOfByte, 128, paramArrayOfByte.length - 117);
    return arrayOfByte;
  }
  
  static String d(Context paramContext, byte[] paramArrayOfByte)
    throws InvalidKeyException, IOException, InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, CertificateException
  {
    paramContext = cv.b(a(paramContext, paramArrayOfByte));
    if (paramContext == null) {
      return "";
    }
    return cq.a(paramContext);
  }
  
  public static String e(Context paramContext, byte[] paramArrayOfByte)
  {
    try
    {
      paramContext = d(paramContext, paramArrayOfByte);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
    return "";
  }
  
  private static class a
  {
    String a;
    String b;
    String c;
    String d;
    String e;
    String f;
    String g;
    String h;
    String i;
    String j;
    String k;
    String l;
    String m;
    String n;
    String o;
    String p;
    String q;
    String r;
    String s;
    String t;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/co.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */