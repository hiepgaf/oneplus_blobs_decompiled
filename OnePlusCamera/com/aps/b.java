package com.aps;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class b
{
  private String a = "AES/CBC/PKCS5Padding";
  private Cipher b = null;
  
  b()
  {
    try
    {
      SecretKeySpec localSecretKeySpec = new SecretKeySpec("#a@u!t*o(n)a&v^i".getBytes("UTF-8"), "AES");
      IvParameterSpec localIvParameterSpec = new IvParameterSpec("_a+m-a=p?a>p<s%3".getBytes("UTF-8"));
      this.b = Cipher.getInstance(this.a);
      this.b.init(2, localSecretKeySpec, localIvParameterSpec);
      return;
    }
    catch (Throwable localThrowable)
    {
      localThrowable = localThrowable;
      localThrowable.printStackTrace();
      t.a(localThrowable);
      return;
    }
    finally {}
  }
  
  public static String a(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null) {}
    try
    {
      paramArrayOfByte = com.amap.api.location.core.b.a(paramArrayOfByte);
      return paramArrayOfByte;
    }
    catch (Throwable paramArrayOfByte)
    {
      paramArrayOfByte.printStackTrace();
    }
    return "";
    return "";
  }
  
  /* Error */
  private byte[] a(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: iconst_0
    //   3: istore_2
    //   4: aload_1
    //   5: ifnonnull +5 -> 10
    //   8: aconst_null
    //   9: areturn
    //   10: aload_1
    //   11: invokevirtual 74	java/lang/String:length	()I
    //   14: ifeq -6 -> 8
    //   17: aload_1
    //   18: invokevirtual 74	java/lang/String:length	()I
    //   21: iconst_2
    //   22: irem
    //   23: ifne -15 -> 8
    //   26: aload_1
    //   27: invokevirtual 74	java/lang/String:length	()I
    //   30: iconst_2
    //   31: idiv
    //   32: newarray <illegal type>
    //   34: astore 4
    //   36: aload 4
    //   38: astore_3
    //   39: new 76	java/lang/StringBuilder
    //   42: dup
    //   43: invokespecial 77	java/lang/StringBuilder:<init>	()V
    //   46: astore 5
    //   48: aload 4
    //   50: astore_3
    //   51: iload_2
    //   52: aload_1
    //   53: invokevirtual 74	java/lang/String:length	()I
    //   56: if_icmplt +6 -> 62
    //   59: aload 4
    //   61: areturn
    //   62: aload 4
    //   64: astore_3
    //   65: aload 5
    //   67: iconst_0
    //   68: aload 5
    //   70: invokevirtual 78	java/lang/StringBuilder:length	()I
    //   73: invokevirtual 82	java/lang/StringBuilder:delete	(II)Ljava/lang/StringBuilder;
    //   76: pop
    //   77: aload 4
    //   79: astore_3
    //   80: aload 5
    //   82: ldc 84
    //   84: invokevirtual 88	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   87: pop
    //   88: aload 4
    //   90: astore_3
    //   91: aload 5
    //   93: aload_1
    //   94: iload_2
    //   95: iload_2
    //   96: iconst_2
    //   97: iadd
    //   98: invokevirtual 92	java/lang/String:substring	(II)Ljava/lang/String;
    //   101: invokevirtual 88	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   104: pop
    //   105: aload 4
    //   107: astore_3
    //   108: aload 5
    //   110: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   113: astore 6
    //   115: aload 4
    //   117: astore_3
    //   118: aload 4
    //   120: iload_2
    //   121: iconst_2
    //   122: idiv
    //   123: aload 6
    //   125: invokestatic 102	java/lang/Integer:decode	(Ljava/lang/String;)Ljava/lang/Integer;
    //   128: invokevirtual 105	java/lang/Integer:intValue	()I
    //   131: i2b
    //   132: i2b
    //   133: bastore
    //   134: iload_2
    //   135: iconst_2
    //   136: iadd
    //   137: istore_2
    //   138: goto -90 -> 48
    //   141: astore_1
    //   142: aload_1
    //   143: invokevirtual 57	java/lang/Throwable:printStackTrace	()V
    //   146: aload_1
    //   147: invokestatic 62	com/aps/t:a	(Ljava/lang/Throwable;)V
    //   150: aload_3
    //   151: areturn
    //   152: astore_1
    //   153: aload_1
    //   154: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	155	0	this	b
    //   0	155	1	paramString	String
    //   3	135	2	i	int
    //   1	150	3	localObject	Object
    //   34	85	4	arrayOfByte	byte[]
    //   46	63	5	localStringBuilder	StringBuilder
    //   113	11	6	str	String
    // Exception table:
    //   from	to	target	type
    //   26	36	141	java/lang/Throwable
    //   39	48	141	java/lang/Throwable
    //   51	59	141	java/lang/Throwable
    //   65	77	141	java/lang/Throwable
    //   80	88	141	java/lang/Throwable
    //   91	105	141	java/lang/Throwable
    //   108	115	141	java/lang/Throwable
    //   118	134	141	java/lang/Throwable
    //   26	36	152	finally
    //   39	48	152	finally
    //   51	59	152	finally
    //   65	77	152	finally
    //   80	88	152	finally
    //   91	105	152	finally
    //   108	115	152	finally
    //   118	134	152	finally
    //   142	150	152	finally
  }
  
  String a(String paramString1, String paramString2)
  {
    if (paramString1 == null) {}
    while (paramString1.length() == 0) {
      return null;
    }
    try
    {
      paramString1 = a(paramString1);
      paramString1 = new String(this.b.doFinal(paramString1), paramString2);
      return paramString1;
    }
    catch (Exception paramString1)
    {
      t.a(paramString1);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/b.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */