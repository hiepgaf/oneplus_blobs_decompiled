package com.amap.api.mapcore2d;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONObject;

public class cv
{
  static String a;
  
  static
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    for (;;)
    {
      if (i >= 80)
      {
        a = localStringBuilder.toString();
        return;
      }
      localStringBuilder.append("=");
      i += 1;
    }
  }
  
  public static String a(long paramLong)
  {
    try
    {
      String str = new SimpleDateFormat("yyyyMMdd HH:mm:ss:SSS", Locale.CHINA).format(new Date(paramLong));
      return str;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return null;
  }
  
  /* Error */
  public static String a(Throwable paramThrowable)
  {
    // Byte code:
    //   0: new 57	java/io/StringWriter
    //   3: dup
    //   4: invokespecial 58	java/io/StringWriter:<init>	()V
    //   7: astore_1
    //   8: new 60	java/io/PrintWriter
    //   11: dup
    //   12: aload_1
    //   13: invokespecial 63	java/io/PrintWriter:<init>	(Ljava/io/Writer;)V
    //   16: astore 5
    //   18: aload 5
    //   20: astore_3
    //   21: aload_1
    //   22: astore_2
    //   23: aload_0
    //   24: aload 5
    //   26: invokevirtual 66	java/lang/Throwable:printStackTrace	(Ljava/io/PrintWriter;)V
    //   29: aload 5
    //   31: astore_3
    //   32: aload_1
    //   33: astore_2
    //   34: aload_0
    //   35: invokevirtual 70	java/lang/Throwable:getCause	()Ljava/lang/Throwable;
    //   38: astore_0
    //   39: aload_0
    //   40: ifnonnull +20 -> 60
    //   43: aload 5
    //   45: astore_3
    //   46: aload_1
    //   47: astore_2
    //   48: aload_1
    //   49: invokevirtual 71	java/lang/Object:toString	()Ljava/lang/String;
    //   52: astore_0
    //   53: aload_1
    //   54: ifnonnull +70 -> 124
    //   57: goto +181 -> 238
    //   60: aload 5
    //   62: astore_3
    //   63: aload_1
    //   64: astore_2
    //   65: aload_0
    //   66: aload 5
    //   68: invokevirtual 66	java/lang/Throwable:printStackTrace	(Ljava/io/PrintWriter;)V
    //   71: aload 5
    //   73: astore_3
    //   74: aload_1
    //   75: astore_2
    //   76: aload_0
    //   77: invokevirtual 70	java/lang/Throwable:getCause	()Ljava/lang/Throwable;
    //   80: astore_0
    //   81: goto -42 -> 39
    //   84: astore 4
    //   86: aconst_null
    //   87: astore_0
    //   88: aconst_null
    //   89: astore_1
    //   90: aload_0
    //   91: astore_3
    //   92: aload_1
    //   93: astore_2
    //   94: aload 4
    //   96: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   99: aload_1
    //   100: ifnonnull +53 -> 153
    //   103: aload_0
    //   104: ifnonnull +64 -> 168
    //   107: aconst_null
    //   108: areturn
    //   109: astore_0
    //   110: aconst_null
    //   111: astore_3
    //   112: aconst_null
    //   113: astore_1
    //   114: aload_1
    //   115: ifnonnull +66 -> 181
    //   118: aload_3
    //   119: ifnonnull +77 -> 196
    //   122: aload_0
    //   123: athrow
    //   124: aload_1
    //   125: invokevirtual 76	java/io/Writer:close	()V
    //   128: goto +110 -> 238
    //   131: astore_1
    //   132: aload_1
    //   133: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   136: goto +102 -> 238
    //   139: aload 5
    //   141: invokevirtual 77	java/io/PrintWriter:close	()V
    //   144: aload_0
    //   145: areturn
    //   146: astore_1
    //   147: aload_1
    //   148: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   151: aload_0
    //   152: areturn
    //   153: aload_1
    //   154: invokevirtual 76	java/io/Writer:close	()V
    //   157: goto -54 -> 103
    //   160: astore_1
    //   161: aload_1
    //   162: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   165: goto -62 -> 103
    //   168: aload_0
    //   169: invokevirtual 77	java/io/PrintWriter:close	()V
    //   172: aconst_null
    //   173: areturn
    //   174: astore_0
    //   175: aload_0
    //   176: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   179: aconst_null
    //   180: areturn
    //   181: aload_1
    //   182: invokevirtual 76	java/io/Writer:close	()V
    //   185: goto -67 -> 118
    //   188: astore_1
    //   189: aload_1
    //   190: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   193: goto -75 -> 118
    //   196: aload_3
    //   197: invokevirtual 77	java/io/PrintWriter:close	()V
    //   200: goto -78 -> 122
    //   203: astore_1
    //   204: aload_1
    //   205: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   208: goto -86 -> 122
    //   211: astore_0
    //   212: aconst_null
    //   213: astore_3
    //   214: goto -100 -> 114
    //   217: astore_0
    //   218: aload_2
    //   219: astore_1
    //   220: goto -106 -> 114
    //   223: astore 4
    //   225: aconst_null
    //   226: astore_0
    //   227: goto -137 -> 90
    //   230: astore 4
    //   232: aload 5
    //   234: astore_0
    //   235: goto -145 -> 90
    //   238: aload 5
    //   240: ifnonnull -101 -> 139
    //   243: aload_0
    //   244: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	245	0	paramThrowable	Throwable
    //   7	118	1	localStringWriter1	java.io.StringWriter
    //   131	2	1	localThrowable1	Throwable
    //   146	8	1	localThrowable2	Throwable
    //   160	22	1	localThrowable3	Throwable
    //   188	2	1	localThrowable4	Throwable
    //   203	2	1	localThrowable5	Throwable
    //   219	1	1	localObject1	Object
    //   22	197	2	localStringWriter2	java.io.StringWriter
    //   20	194	3	localObject2	Object
    //   84	11	4	localThrowable6	Throwable
    //   223	1	4	localThrowable7	Throwable
    //   230	1	4	localThrowable8	Throwable
    //   16	223	5	localPrintWriter	java.io.PrintWriter
    // Exception table:
    //   from	to	target	type
    //   0	8	84	java/lang/Throwable
    //   0	8	109	finally
    //   124	128	131	java/lang/Throwable
    //   139	144	146	java/lang/Throwable
    //   153	157	160	java/lang/Throwable
    //   168	172	174	java/lang/Throwable
    //   181	185	188	java/lang/Throwable
    //   196	200	203	java/lang/Throwable
    //   8	18	211	finally
    //   23	29	217	finally
    //   34	39	217	finally
    //   48	53	217	finally
    //   65	71	217	finally
    //   76	81	217	finally
    //   94	99	217	finally
    //   8	18	223	java/lang/Throwable
    //   23	29	230	java/lang/Throwable
    //   34	39	230	java/lang/Throwable
    //   48	53	230	java/lang/Throwable
    //   65	71	230	java/lang/Throwable
    //   76	81	230	java/lang/Throwable
  }
  
  public static String a(Map<String, String> paramMap)
  {
    if (paramMap == null) {}
    while (paramMap.size() == 0) {
      return null;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 1;
    for (;;)
    {
      try
      {
        paramMap = paramMap.entrySet().iterator();
        boolean bool = paramMap.hasNext();
        if (bool) {
          continue;
        }
      }
      catch (Throwable paramMap)
      {
        Map.Entry localEntry;
        cy.a(paramMap, "Utils", "assembleParams");
        continue;
      }
      return localStringBuffer.toString();
      localEntry = (Map.Entry)paramMap.next();
      if (i == 0)
      {
        localStringBuffer.append("&").append((String)localEntry.getKey()).append("=").append((String)localEntry.getValue());
      }
      else
      {
        localStringBuffer.append((String)localEntry.getKey()).append("=").append((String)localEntry.getValue());
        i = 0;
      }
    }
  }
  
  public static String a(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {}
    while (paramArrayOfByte.length == 0) {
      return "";
    }
    try
    {
      String str = new String(paramArrayOfByte, "UTF-8");
      return str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return new String(paramArrayOfByte);
  }
  
  /* Error */
  static java.security.PublicKey a(Context paramContext)
    throws java.security.cert.CertificateException, java.security.spec.InvalidKeySpecException, java.security.NoSuchAlgorithmException, java.lang.NullPointerException, IOException
  {
    // Byte code:
    //   0: new 160	java/io/ByteArrayInputStream
    //   3: dup
    //   4: ldc -94
    //   6: invokestatic 168	com/amap/api/mapcore2d/cq:b	(Ljava/lang/String;)[B
    //   9: invokespecial 169	java/io/ByteArrayInputStream:<init>	([B)V
    //   12: astore_1
    //   13: aload_1
    //   14: astore_0
    //   15: ldc -85
    //   17: invokestatic 177	java/security/cert/CertificateFactory:getInstance	(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;
    //   20: astore_3
    //   21: aload_1
    //   22: astore_0
    //   23: ldc -77
    //   25: invokestatic 184	java/security/KeyFactory:getInstance	(Ljava/lang/String;)Ljava/security/KeyFactory;
    //   28: astore_2
    //   29: aload_1
    //   30: astore_0
    //   31: aload_3
    //   32: aload_1
    //   33: invokevirtual 188	java/security/cert/CertificateFactory:generateCertificate	(Ljava/io/InputStream;)Ljava/security/cert/Certificate;
    //   36: astore_3
    //   37: aload_3
    //   38: ifnonnull +6 -> 44
    //   41: goto +122 -> 163
    //   44: aload_2
    //   45: ifnull +118 -> 163
    //   48: aload_1
    //   49: astore_0
    //   50: aload_2
    //   51: new 190	java/security/spec/X509EncodedKeySpec
    //   54: dup
    //   55: aload_3
    //   56: invokevirtual 196	java/security/cert/Certificate:getPublicKey	()Ljava/security/PublicKey;
    //   59: invokeinterface 202 1 0
    //   64: invokespecial 203	java/security/spec/X509EncodedKeySpec:<init>	([B)V
    //   67: invokevirtual 207	java/security/KeyFactory:generatePublic	(Ljava/security/spec/KeySpec;)Ljava/security/PublicKey;
    //   70: astore_2
    //   71: aload_1
    //   72: ifnonnull +42 -> 114
    //   75: aload_2
    //   76: areturn
    //   77: astore_2
    //   78: aconst_null
    //   79: astore_1
    //   80: aload_1
    //   81: astore_0
    //   82: aload_2
    //   83: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   86: aload_1
    //   87: ifnonnull +40 -> 127
    //   90: aconst_null
    //   91: areturn
    //   92: astore_1
    //   93: aconst_null
    //   94: astore_0
    //   95: aload_0
    //   96: ifnonnull +44 -> 140
    //   99: aload_1
    //   100: athrow
    //   101: aload_1
    //   102: invokevirtual 210	java/io/InputStream:close	()V
    //   105: aconst_null
    //   106: areturn
    //   107: astore_0
    //   108: aload_0
    //   109: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   112: aconst_null
    //   113: areturn
    //   114: aload_1
    //   115: invokevirtual 210	java/io/InputStream:close	()V
    //   118: aload_2
    //   119: areturn
    //   120: astore_0
    //   121: aload_0
    //   122: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   125: aload_2
    //   126: areturn
    //   127: aload_1
    //   128: invokevirtual 210	java/io/InputStream:close	()V
    //   131: aconst_null
    //   132: areturn
    //   133: astore_0
    //   134: aload_0
    //   135: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   138: aconst_null
    //   139: areturn
    //   140: aload_0
    //   141: invokevirtual 210	java/io/InputStream:close	()V
    //   144: goto -45 -> 99
    //   147: astore_0
    //   148: aload_0
    //   149: invokevirtual 54	java/lang/Throwable:printStackTrace	()V
    //   152: goto -53 -> 99
    //   155: astore_1
    //   156: goto -61 -> 95
    //   159: astore_2
    //   160: goto -80 -> 80
    //   163: aload_1
    //   164: ifnonnull -63 -> 101
    //   167: aconst_null
    //   168: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	169	0	paramContext	Context
    //   12	75	1	localByteArrayInputStream	java.io.ByteArrayInputStream
    //   92	36	1	localObject1	Object
    //   155	9	1	localObject2	Object
    //   28	48	2	localObject3	Object
    //   77	49	2	localThrowable1	Throwable
    //   159	1	2	localThrowable2	Throwable
    //   20	36	3	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   0	13	77	java/lang/Throwable
    //   0	13	92	finally
    //   101	105	107	java/lang/Throwable
    //   114	118	120	java/lang/Throwable
    //   127	131	133	java/lang/Throwable
    //   140	144	147	java/lang/Throwable
    //   15	21	155	finally
    //   23	29	155	finally
    //   31	37	155	finally
    //   50	71	155	finally
    //   82	86	155	finally
    //   15	21	159	java/lang/Throwable
    //   23	29	159	java/lang/Throwable
    //   31	37	159	java/lang/Throwable
    //   50	71	159	java/lang/Throwable
  }
  
  public static void a(Context paramContext, String paramString1, String paramString2, String paramString3)
  {
    f(a);
    f("                                   鉴权错误信息                                  ");
    f(a);
    e("SHA1Package:" + cl.e(paramContext));
    e("key:" + cl.f(paramContext));
    e("csid:" + paramString1);
    e("gsid:" + paramString2);
    e("json:" + paramString3);
    f("                                                                               ");
    f("请仔细检查 SHA1Package与Key申请信息是否对应，Key是否删除，平台是否匹配                ");
    f("如若确认无误，仍存在问题，请将全部log信息提交到工单系统，多谢合作                       ");
    f(a);
  }
  
  public static void a(ByteArrayOutputStream paramByteArrayOutputStream, byte paramByte, byte[] paramArrayOfByte)
  {
    try
    {
      paramByteArrayOutputStream.write(new byte[] { (byte)paramByte });
      paramByte &= 0xFF;
      if ((paramByte < 255) && (paramByte > 0))
      {
        paramByteArrayOutputStream.write(paramArrayOfByte);
        return;
      }
    }
    catch (IOException paramByteArrayOutputStream)
    {
      cy.a(paramByteArrayOutputStream, "Utils", "writeField");
      return;
    }
    do
    {
      paramByteArrayOutputStream.write(paramArrayOfByte, 0, 255);
      return;
    } while (paramByte == 255);
  }
  
  public static void a(ByteArrayOutputStream paramByteArrayOutputStream, String paramString)
  {
    int i;
    if (!TextUtils.isEmpty(paramString))
    {
      i = paramString.length();
      if (i > 255) {
        break label48;
      }
    }
    for (;;)
    {
      a(paramByteArrayOutputStream, (byte)i, a(paramString));
      return;
      try
      {
        paramByteArrayOutputStream.write(new byte[] { 0 });
        return;
      }
      catch (IOException paramByteArrayOutputStream)
      {
        paramByteArrayOutputStream.printStackTrace();
        return;
      }
      label48:
      i = 255;
    }
  }
  
  public static boolean a(JSONObject paramJSONObject, String paramString)
  {
    if (paramJSONObject == null) {}
    while (!paramJSONObject.has(paramString)) {
      return false;
    }
    return true;
  }
  
  public static byte[] a()
  {
    int j = 0;
    try
    {
      String[] arrayOfString = new StringBuffer("16,16,18,77,15,911,121,77,121,911,38,77,911,99,86,67,611,96,48,77,84,911,38,67,021,301,86,67,611,98,48,77,511,77,48,97,511,58,48,97,511,84,501,87,511,96,48,77,221,911,38,77,121,37,86,67,25,301,86,67,021,96,86,67,021,701,86,67,35,56,86,67,611,37,221,87").reverse().toString().split(",");
      byte[] arrayOfByte = new byte[arrayOfString.length];
      int i = 0;
      if (i >= arrayOfString.length)
      {
        arrayOfString = new StringBuffer(new String(cq.b(new String(arrayOfByte)))).reverse().toString().split(",");
        arrayOfByte = new byte[arrayOfString.length];
        i = j;
      }
      for (;;)
      {
        if (i >= arrayOfString.length)
        {
          return arrayOfByte;
          arrayOfByte[i] = Byte.parseByte(arrayOfString[i]);
          i += 1;
          break;
        }
        arrayOfByte[i] = Byte.parseByte(arrayOfString[i]);
        i += 1;
      }
      return new byte[16];
    }
    catch (Throwable localThrowable)
    {
      cy.a(localThrowable, "Utils", "getIV");
    }
  }
  
  public static byte[] a(String paramString)
  {
    if (!TextUtils.isEmpty(paramString)) {}
    try
    {
      byte[] arrayOfByte = paramString.getBytes("UTF-8");
      return arrayOfByte;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return new byte[0];
    return paramString.getBytes();
  }
  
  public static String b(String paramString)
  {
    if (paramString != null)
    {
      paramString = cq.b(a(paramString));
      return (char)(paramString.length() % 26 + 65) + paramString;
    }
    return null;
  }
  
  public static byte[] b(byte[] paramArrayOfByte)
  {
    try
    {
      paramArrayOfByte = g(paramArrayOfByte);
      return paramArrayOfByte;
    }
    catch (Throwable paramArrayOfByte)
    {
      cy.a(paramArrayOfByte, "Utils", "gZip");
    }
    return new byte[0];
  }
  
  public static String c(String paramString)
  {
    if (paramString.length() >= 2) {
      return cq.a(paramString.substring(1));
    }
    return "";
  }
  
  /* Error */
  public static byte[] c(byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aload_0
    //   4: ifnonnull +5 -> 9
    //   7: aconst_null
    //   8: areturn
    //   9: aload_0
    //   10: arraylength
    //   11: ifeq -4 -> 7
    //   14: new 247	java/io/ByteArrayOutputStream
    //   17: dup
    //   18: invokespecial 324	java/io/ByteArrayOutputStream:<init>	()V
    //   21: astore_1
    //   22: new 326	java/util/zip/ZipOutputStream
    //   25: dup
    //   26: aload_1
    //   27: invokespecial 329	java/util/zip/ZipOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   30: astore 5
    //   32: aload_1
    //   33: astore_3
    //   34: aload 5
    //   36: astore_2
    //   37: aload 5
    //   39: new 331	java/util/zip/ZipEntry
    //   42: dup
    //   43: ldc_w 333
    //   46: invokespecial 334	java/util/zip/ZipEntry:<init>	(Ljava/lang/String;)V
    //   49: invokevirtual 338	java/util/zip/ZipOutputStream:putNextEntry	(Ljava/util/zip/ZipEntry;)V
    //   52: aload_1
    //   53: astore_3
    //   54: aload 5
    //   56: astore_2
    //   57: aload 5
    //   59: aload_0
    //   60: invokevirtual 339	java/util/zip/ZipOutputStream:write	([B)V
    //   63: aload_1
    //   64: astore_3
    //   65: aload 5
    //   67: astore_2
    //   68: aload 5
    //   70: invokevirtual 342	java/util/zip/ZipOutputStream:closeEntry	()V
    //   73: aload_1
    //   74: astore_3
    //   75: aload 5
    //   77: astore_2
    //   78: aload 5
    //   80: invokevirtual 345	java/util/zip/ZipOutputStream:finish	()V
    //   83: aload_1
    //   84: astore_3
    //   85: aload 5
    //   87: astore_2
    //   88: aload_1
    //   89: invokevirtual 348	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   92: astore_0
    //   93: aload 5
    //   95: ifnonnull +73 -> 168
    //   98: aload_1
    //   99: ifnonnull +90 -> 189
    //   102: aload_0
    //   103: areturn
    //   104: astore 4
    //   106: aconst_null
    //   107: astore_1
    //   108: aconst_null
    //   109: astore_0
    //   110: aload_1
    //   111: astore_3
    //   112: aload_0
    //   113: astore_2
    //   114: aload 4
    //   116: ldc 125
    //   118: ldc_w 350
    //   121: invokestatic 132	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   124: aload_0
    //   125: ifnonnull +82 -> 207
    //   128: aload 6
    //   130: astore_0
    //   131: aload_1
    //   132: ifnull -30 -> 102
    //   135: aload_1
    //   136: invokevirtual 351	java/io/ByteArrayOutputStream:close	()V
    //   139: aconst_null
    //   140: areturn
    //   141: astore_0
    //   142: aload_0
    //   143: ldc 125
    //   145: ldc_w 353
    //   148: invokestatic 132	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   151: aconst_null
    //   152: areturn
    //   153: astore_0
    //   154: aconst_null
    //   155: astore_1
    //   156: aconst_null
    //   157: astore_2
    //   158: aload_2
    //   159: ifnonnull +68 -> 227
    //   162: aload_1
    //   163: ifnonnull +84 -> 247
    //   166: aload_0
    //   167: athrow
    //   168: aload 5
    //   170: invokevirtual 354	java/util/zip/ZipOutputStream:close	()V
    //   173: goto -75 -> 98
    //   176: astore_2
    //   177: aload_2
    //   178: ldc 125
    //   180: ldc_w 356
    //   183: invokestatic 132	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   186: goto -88 -> 98
    //   189: aload_1
    //   190: invokevirtual 351	java/io/ByteArrayOutputStream:close	()V
    //   193: aload_0
    //   194: areturn
    //   195: astore_1
    //   196: aload_1
    //   197: ldc 125
    //   199: ldc_w 353
    //   202: invokestatic 132	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   205: aload_0
    //   206: areturn
    //   207: aload_0
    //   208: invokevirtual 354	java/util/zip/ZipOutputStream:close	()V
    //   211: goto -83 -> 128
    //   214: astore_0
    //   215: aload_0
    //   216: ldc 125
    //   218: ldc_w 356
    //   221: invokestatic 132	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   224: goto -96 -> 128
    //   227: aload_2
    //   228: invokevirtual 354	java/util/zip/ZipOutputStream:close	()V
    //   231: goto -69 -> 162
    //   234: astore_2
    //   235: aload_2
    //   236: ldc 125
    //   238: ldc_w 356
    //   241: invokestatic 132	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   244: goto -82 -> 162
    //   247: aload_1
    //   248: invokevirtual 351	java/io/ByteArrayOutputStream:close	()V
    //   251: goto -85 -> 166
    //   254: astore_1
    //   255: aload_1
    //   256: ldc 125
    //   258: ldc_w 353
    //   261: invokestatic 132	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   264: goto -98 -> 166
    //   267: astore_0
    //   268: aconst_null
    //   269: astore_2
    //   270: goto -112 -> 158
    //   273: astore_0
    //   274: aload_3
    //   275: astore_1
    //   276: goto -118 -> 158
    //   279: astore 4
    //   281: aconst_null
    //   282: astore_0
    //   283: goto -173 -> 110
    //   286: astore 4
    //   288: aload 5
    //   290: astore_0
    //   291: goto -181 -> 110
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	294	0	paramArrayOfByte	byte[]
    //   21	169	1	localByteArrayOutputStream1	ByteArrayOutputStream
    //   195	53	1	localThrowable1	Throwable
    //   254	2	1	localThrowable2	Throwable
    //   275	1	1	localObject1	Object
    //   36	123	2	localObject2	Object
    //   176	52	2	localThrowable3	Throwable
    //   234	2	2	localThrowable4	Throwable
    //   269	1	2	localObject3	Object
    //   33	242	3	localByteArrayOutputStream2	ByteArrayOutputStream
    //   104	11	4	localThrowable5	Throwable
    //   279	1	4	localThrowable6	Throwable
    //   286	1	4	localThrowable7	Throwable
    //   30	259	5	localZipOutputStream	java.util.zip.ZipOutputStream
    //   1	128	6	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   14	22	104	java/lang/Throwable
    //   135	139	141	java/lang/Throwable
    //   14	22	153	finally
    //   168	173	176	java/lang/Throwable
    //   189	193	195	java/lang/Throwable
    //   207	211	214	java/lang/Throwable
    //   227	231	234	java/lang/Throwable
    //   247	251	254	java/lang/Throwable
    //   22	32	267	finally
    //   37	52	273	finally
    //   57	63	273	finally
    //   68	73	273	finally
    //   78	83	273	finally
    //   88	93	273	finally
    //   114	124	273	finally
    //   22	32	279	java/lang/Throwable
    //   37	52	286	java/lang/Throwable
    //   57	63	286	java/lang/Throwable
    //   68	73	286	java/lang/Throwable
    //   78	83	286	java/lang/Throwable
    //   88	93	286	java/lang/Throwable
  }
  
  public static String d(String paramString)
  {
    int i = 0;
    try
    {
      if (!TextUtils.isEmpty(paramString))
      {
        Object localObject = paramString.split("&");
        Arrays.sort((Object[])localObject);
        StringBuffer localStringBuffer = new StringBuffer();
        int j = localObject.length;
        for (;;)
        {
          if (i >= j)
          {
            localObject = localStringBuffer.toString();
            if (((String)localObject).length() > 1) {
              break;
            }
            return paramString;
          }
          localStringBuffer.append(localObject[i]);
          localStringBuffer.append("&");
          i += 1;
        }
        localObject = (String)((String)localObject).subSequence(0, ((String)localObject).length() - 1);
        return (String)localObject;
      }
    }
    catch (Throwable localThrowable)
    {
      cy.a(localThrowable, "Utils", "sortParams");
      return paramString;
    }
    return "";
  }
  
  static String d(byte[] paramArrayOfByte)
  {
    try
    {
      paramArrayOfByte = f(paramArrayOfByte);
      return paramArrayOfByte;
    }
    catch (Throwable paramArrayOfByte)
    {
      cy.a(paramArrayOfByte, "Utils", "HexString");
    }
    return null;
  }
  
  static String e(byte[] paramArrayOfByte)
  {
    try
    {
      paramArrayOfByte = f(paramArrayOfByte);
      return paramArrayOfByte;
    }
    catch (Throwable paramArrayOfByte)
    {
      paramArrayOfByte.printStackTrace();
    }
    return null;
  }
  
  static void e(String paramString)
  {
    int i = 0;
    if (paramString.length() >= 78)
    {
      localObject = paramString.substring(0, 78);
      f("|" + (String)localObject + "|");
      e(paramString.substring(78));
      return;
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("|").append(paramString);
    for (;;)
    {
      if (i >= 78 - paramString.length())
      {
        ((StringBuilder)localObject).append("|");
        f(((StringBuilder)localObject).toString());
        return;
      }
      ((StringBuilder)localObject).append(" ");
      i += 1;
    }
  }
  
  public static String f(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i;
    if (paramArrayOfByte != null)
    {
      i = 0;
      if (i >= paramArrayOfByte.length) {
        return localStringBuilder.toString();
      }
    }
    else
    {
      return null;
    }
    String str = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
    if (str.length() != 1) {}
    for (;;)
    {
      localStringBuilder.append(str);
      i += 1;
      break;
      str = '0' + str;
    }
  }
  
  private static void f(String paramString)
  {
    Log.i("authErrLog", paramString);
  }
  
  /* Error */
  private static byte[] g(byte[] paramArrayOfByte)
    throws IOException, Throwable
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aload_0
    //   3: ifnull +44 -> 47
    //   6: new 247	java/io/ByteArrayOutputStream
    //   9: dup
    //   10: invokespecial 324	java/io/ByteArrayOutputStream:<init>	()V
    //   13: astore_2
    //   14: new 395	java/util/zip/GZIPOutputStream
    //   17: dup
    //   18: aload_2
    //   19: invokespecial 396	java/util/zip/GZIPOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   22: astore_1
    //   23: aload_1
    //   24: aload_0
    //   25: invokevirtual 397	java/util/zip/GZIPOutputStream:write	([B)V
    //   28: aload_1
    //   29: invokevirtual 398	java/util/zip/GZIPOutputStream:finish	()V
    //   32: aload_2
    //   33: invokevirtual 348	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   36: astore_0
    //   37: aload_1
    //   38: ifnonnull +33 -> 71
    //   41: aload_2
    //   42: ifnonnull +39 -> 81
    //   45: aload_0
    //   46: areturn
    //   47: aconst_null
    //   48: areturn
    //   49: astore_2
    //   50: aconst_null
    //   51: astore_0
    //   52: aload_2
    //   53: athrow
    //   54: astore_3
    //   55: aload_1
    //   56: astore_2
    //   57: aload_0
    //   58: astore_1
    //   59: aload_3
    //   60: astore_0
    //   61: aload_1
    //   62: ifnonnull +28 -> 90
    //   65: aload_2
    //   66: ifnonnull +34 -> 100
    //   69: aload_0
    //   70: athrow
    //   71: aload_1
    //   72: invokevirtual 399	java/util/zip/GZIPOutputStream:close	()V
    //   75: goto -34 -> 41
    //   78: astore_0
    //   79: aload_0
    //   80: athrow
    //   81: aload_2
    //   82: invokevirtual 351	java/io/ByteArrayOutputStream:close	()V
    //   85: aload_0
    //   86: areturn
    //   87: astore_0
    //   88: aload_0
    //   89: athrow
    //   90: aload_1
    //   91: invokevirtual 399	java/util/zip/GZIPOutputStream:close	()V
    //   94: goto -29 -> 65
    //   97: astore_0
    //   98: aload_0
    //   99: athrow
    //   100: aload_2
    //   101: invokevirtual 351	java/io/ByteArrayOutputStream:close	()V
    //   104: goto -35 -> 69
    //   107: astore_0
    //   108: aload_0
    //   109: athrow
    //   110: astore_0
    //   111: aconst_null
    //   112: astore_1
    //   113: aconst_null
    //   114: astore_2
    //   115: goto -54 -> 61
    //   118: astore_0
    //   119: aconst_null
    //   120: astore_1
    //   121: goto -60 -> 61
    //   124: astore_0
    //   125: goto -64 -> 61
    //   128: astore_3
    //   129: aconst_null
    //   130: astore_0
    //   131: aload_2
    //   132: astore_1
    //   133: aload_3
    //   134: astore_2
    //   135: goto -83 -> 52
    //   138: astore_0
    //   139: aload_2
    //   140: astore_3
    //   141: aload_0
    //   142: astore_2
    //   143: aload_1
    //   144: astore_0
    //   145: aload_3
    //   146: astore_1
    //   147: goto -95 -> 52
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	150	0	paramArrayOfByte	byte[]
    //   1	146	1	localObject1	Object
    //   13	29	2	localByteArrayOutputStream	ByteArrayOutputStream
    //   49	4	2	localThrowable1	Throwable
    //   56	87	2	localObject2	Object
    //   54	6	3	localObject3	Object
    //   128	6	3	localThrowable2	Throwable
    //   140	6	3	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   6	14	49	java/lang/Throwable
    //   52	54	54	finally
    //   71	75	78	java/lang/Throwable
    //   81	85	87	java/lang/Throwable
    //   90	94	97	java/lang/Throwable
    //   100	104	107	java/lang/Throwable
    //   6	14	110	finally
    //   14	23	118	finally
    //   23	37	124	finally
    //   14	23	128	java/lang/Throwable
    //   23	37	138	java/lang/Throwable
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */