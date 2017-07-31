package com.amap.api.mapcore2d;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class cr
{
  /* Error */
  public static String a(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aload_0
    //   3: invokestatic 16	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   6: ifne +191 -> 197
    //   9: new 18	java/io/File
    //   12: dup
    //   13: aload_0
    //   14: invokespecial 22	java/io/File:<init>	(Ljava/lang/String;)V
    //   17: astore_0
    //   18: aload_0
    //   19: invokevirtual 26	java/io/File:isFile	()Z
    //   22: ifne +5 -> 27
    //   25: aconst_null
    //   26: areturn
    //   27: aload_0
    //   28: invokevirtual 29	java/io/File:exists	()Z
    //   31: ifeq +164 -> 195
    //   34: sipush 2048
    //   37: newarray <illegal type>
    //   39: astore 4
    //   41: ldc 31
    //   43: invokestatic 37	java/security/MessageDigest:getInstance	(Ljava/lang/String;)Ljava/security/MessageDigest;
    //   46: astore 5
    //   48: new 39	java/io/FileInputStream
    //   51: dup
    //   52: aload_0
    //   53: invokespecial 42	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   56: astore_2
    //   57: aload_2
    //   58: astore_0
    //   59: aload_2
    //   60: aload 4
    //   62: invokevirtual 46	java/io/FileInputStream:read	([B)I
    //   65: istore_1
    //   66: iload_1
    //   67: iconst_m1
    //   68: if_icmpne +20 -> 88
    //   71: aload_2
    //   72: astore_0
    //   73: aload 5
    //   75: invokevirtual 50	java/security/MessageDigest:digest	()[B
    //   78: invokestatic 56	com/amap/api/mapcore2d/cv:d	([B)Ljava/lang/String;
    //   81: astore_3
    //   82: aload_2
    //   83: ifnonnull +36 -> 119
    //   86: aload_3
    //   87: areturn
    //   88: aload_2
    //   89: astore_0
    //   90: aload 5
    //   92: aload 4
    //   94: iconst_0
    //   95: iload_1
    //   96: invokevirtual 60	java/security/MessageDigest:update	([BII)V
    //   99: goto -42 -> 57
    //   102: astore_3
    //   103: aload_2
    //   104: astore_0
    //   105: aload_3
    //   106: ldc 31
    //   108: ldc 62
    //   110: invokestatic 67	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   113: aload_2
    //   114: ifnonnull +22 -> 136
    //   117: aconst_null
    //   118: areturn
    //   119: aload_2
    //   120: invokevirtual 71	java/io/FileInputStream:close	()V
    //   123: aload_3
    //   124: areturn
    //   125: astore_0
    //   126: aload_0
    //   127: ldc 31
    //   129: ldc 62
    //   131: invokestatic 67	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   134: aload_3
    //   135: areturn
    //   136: aload_2
    //   137: invokevirtual 71	java/io/FileInputStream:close	()V
    //   140: aconst_null
    //   141: areturn
    //   142: astore_0
    //   143: aload_0
    //   144: ldc 31
    //   146: ldc 62
    //   148: invokestatic 67	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   151: aconst_null
    //   152: areturn
    //   153: astore_0
    //   154: aload_3
    //   155: astore_2
    //   156: aload_2
    //   157: ifnonnull +5 -> 162
    //   160: aload_0
    //   161: athrow
    //   162: aload_2
    //   163: invokevirtual 71	java/io/FileInputStream:close	()V
    //   166: goto -6 -> 160
    //   169: astore_2
    //   170: aload_2
    //   171: ldc 31
    //   173: ldc 62
    //   175: invokestatic 67	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   178: goto -18 -> 160
    //   181: astore_3
    //   182: aload_0
    //   183: astore_2
    //   184: aload_3
    //   185: astore_0
    //   186: goto -30 -> 156
    //   189: astore_3
    //   190: aconst_null
    //   191: astore_2
    //   192: goto -89 -> 103
    //   195: aconst_null
    //   196: areturn
    //   197: aconst_null
    //   198: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	199	0	paramString	String
    //   65	31	1	i	int
    //   56	107	2	localObject1	Object
    //   169	2	2	localIOException	java.io.IOException
    //   183	9	2	str1	String
    //   1	86	3	str2	String
    //   102	53	3	localThrowable1	Throwable
    //   181	4	3	localObject2	Object
    //   189	1	3	localThrowable2	Throwable
    //   39	54	4	arrayOfByte	byte[]
    //   46	45	5	localMessageDigest	MessageDigest
    // Exception table:
    //   from	to	target	type
    //   59	66	102	java/lang/Throwable
    //   73	82	102	java/lang/Throwable
    //   90	99	102	java/lang/Throwable
    //   119	123	125	java/io/IOException
    //   136	140	142	java/io/IOException
    //   2	25	153	finally
    //   27	57	153	finally
    //   162	166	169	java/io/IOException
    //   59	66	181	finally
    //   73	82	181	finally
    //   90	99	181	finally
    //   105	113	181	finally
    //   2	25	189	java/lang/Throwable
    //   27	57	189	java/lang/Throwable
  }
  
  public static String a(byte[] paramArrayOfByte)
  {
    return cv.d(b(paramArrayOfByte));
  }
  
  public static byte[] a(byte[] paramArrayOfByte, String paramString)
  {
    try
    {
      paramString = MessageDigest.getInstance(paramString);
      paramString.update(paramArrayOfByte);
      paramArrayOfByte = paramString.digest();
      return paramArrayOfByte;
    }
    catch (Throwable paramArrayOfByte)
    {
      cy.a(paramArrayOfByte, "MD5", "getMd5Bytes1");
    }
    return null;
  }
  
  public static String b(String paramString)
  {
    if (paramString != null) {
      return cv.d(d(paramString));
    }
    return null;
  }
  
  private static byte[] b(byte[] paramArrayOfByte)
  {
    return a(paramArrayOfByte, "MD5");
  }
  
  public static String c(String paramString)
  {
    return cv.e(e(paramString));
  }
  
  public static byte[] d(String paramString)
  {
    try
    {
      paramString = f(paramString);
      return paramString;
    }
    catch (Throwable paramString)
    {
      cy.a(paramString, "MD5", "getMd5Bytes");
    }
    return new byte[0];
  }
  
  private static byte[] e(String paramString)
  {
    try
    {
      paramString = f(paramString);
      return paramString;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
    return new byte[0];
  }
  
  private static byte[] f(String paramString)
    throws NoSuchAlgorithmException, UnsupportedEncodingException
  {
    if (paramString != null)
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      localMessageDigest.update(cv.a(paramString));
      return localMessageDigest.digest();
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */