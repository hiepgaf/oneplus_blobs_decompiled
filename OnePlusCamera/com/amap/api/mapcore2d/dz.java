package com.amap.api.mapcore2d;

import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class dz
  extends ee
{
  protected Context a;
  protected cu b;
  
  public dz(Context paramContext, cu paramcu)
  {
    if (paramContext == null) {}
    for (;;)
    {
      this.b = paramcu;
      return;
      this.a = paramContext.getApplicationContext();
    }
  }
  
  private byte[] l()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      localByteArrayOutputStream.write(cv.a("PANDORA$"));
      localByteArrayOutputStream.write(new byte[] { 1 });
      localByteArrayOutputStream.write(new byte[] { 0 });
      byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
      try
      {
        localByteArrayOutputStream.close();
        return arrayOfByte;
      }
      catch (Throwable localThrowable1)
      {
        cy.a(localThrowable1, "BinaryRequest", "getBinaryHead");
        return arrayOfByte;
      }
      try
      {
        localThrowable2.close();
        throw ((Throwable)localObject);
      }
      catch (Throwable localThrowable3)
      {
        for (;;)
        {
          cy.a(localThrowable3, "BinaryRequest", "getBinaryHead");
        }
      }
    }
    catch (Throwable localThrowable4)
    {
      localThrowable4 = localThrowable4;
      cy.a(localThrowable4, "BinaryRequest", "getBinaryHead");
      try
      {
        localThrowable1.close();
        return null;
      }
      catch (Throwable localThrowable2)
      {
        for (;;)
        {
          cy.a(localThrowable2, "BinaryRequest", "getBinaryHead");
        }
      }
    }
    finally {}
  }
  
  private byte[] m()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      byte[] arrayOfByte = a();
      if (arrayOfByte == null) {}
      do
      {
        localByteArrayOutputStream.write(new byte[] { 0 });
        arrayOfByte = localByteArrayOutputStream.toByteArray();
        try
        {
          localByteArrayOutputStream.close();
          return arrayOfByte;
        }
        catch (Throwable localThrowable1)
        {
          try
          {
            localByteArrayOutputStream.close();
            return arrayOfByte;
          }
          catch (Throwable localThrowable2)
          {
            cy.a(localThrowable2, "BinaryRequest", "getRequestRawData");
            return arrayOfByte;
          }
          localThrowable1 = localThrowable1;
          cy.a(localThrowable1, "BinaryRequest", "getRequestRawData");
          return arrayOfByte;
        }
      } while (arrayOfByte.length == 0);
      localByteArrayOutputStream.write(new byte[] { 1 });
      localByteArrayOutputStream.write(a(arrayOfByte));
      localByteArrayOutputStream.write(arrayOfByte);
      arrayOfByte = localByteArrayOutputStream.toByteArray();
      try
      {
        localThrowable3.close();
        throw ((Throwable)localObject);
      }
      catch (Throwable localThrowable4)
      {
        for (;;)
        {
          cy.a(localThrowable4, "BinaryRequest", "getRequestRawData");
        }
      }
    }
    catch (Throwable localThrowable5)
    {
      localThrowable5 = localThrowable5;
      cy.a(localThrowable5, "BinaryRequest", "getRequestRawData");
      try
      {
        localThrowable2.close();
        return new byte[] { 0 };
      }
      catch (Throwable localThrowable3)
      {
        for (;;)
        {
          cy.a(localThrowable3, "BinaryRequest", "getRequestRawData");
        }
      }
    }
    finally {}
  }
  
  private byte[] n()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      byte[] arrayOfByte = b();
      if (arrayOfByte == null) {}
      do
      {
        localByteArrayOutputStream.write(new byte[] { 0 });
        arrayOfByte = localByteArrayOutputStream.toByteArray();
        try
        {
          localByteArrayOutputStream.close();
          return arrayOfByte;
        }
        catch (Throwable localThrowable1)
        {
          try
          {
            localByteArrayOutputStream.close();
            return arrayOfByte;
          }
          catch (Throwable localThrowable2)
          {
            cy.a(localThrowable2, "BinaryRequest", "getRequestEncryptData");
            return arrayOfByte;
          }
          localThrowable1 = localThrowable1;
          cy.a(localThrowable1, "BinaryRequest", "getRequestEncryptData");
          return arrayOfByte;
        }
      } while (arrayOfByte.length == 0);
      localByteArrayOutputStream.write(new byte[] { 1 });
      arrayOfByte = co.a(this.a, arrayOfByte);
      localByteArrayOutputStream.write(a(arrayOfByte));
      localByteArrayOutputStream.write(arrayOfByte);
      arrayOfByte = localByteArrayOutputStream.toByteArray();
      try
      {
        localThrowable3.close();
        throw ((Throwable)localObject);
      }
      catch (Throwable localThrowable4)
      {
        for (;;)
        {
          cy.a(localThrowable4, "BinaryRequest", "getRequestEncryptData");
        }
      }
    }
    catch (Throwable localThrowable5)
    {
      localThrowable5 = localThrowable5;
      cy.a(localThrowable5, "BinaryRequest", "getRequestEncryptData");
      try
      {
        localThrowable2.close();
        return new byte[] { 0 };
      }
      catch (Throwable localThrowable3)
      {
        for (;;)
        {
          cy.a(localThrowable3, "BinaryRequest", "getRequestEncryptData");
        }
      }
    }
    finally {}
  }
  
  public abstract byte[] a();
  
  protected byte[] a(byte[] paramArrayOfByte)
  {
    int j = paramArrayOfByte.length;
    int i = (byte)(j / 256);
    j = (byte)(j % 256);
    return new byte[] { (byte)i, (byte)j };
  }
  
  public final byte[] a_()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      localByteArrayOutputStream.write(l());
      localByteArrayOutputStream.write(h());
      localByteArrayOutputStream.write(m());
      localByteArrayOutputStream.write(n());
      byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
      try
      {
        localByteArrayOutputStream.close();
        return arrayOfByte;
      }
      catch (Throwable localThrowable1)
      {
        cy.a(localThrowable1, "BinaryRequest", "getEntityBytes");
        return arrayOfByte;
      }
      try
      {
        localThrowable2.close();
        throw ((Throwable)localObject);
      }
      catch (Throwable localThrowable3)
      {
        for (;;)
        {
          cy.a(localThrowable3, "BinaryRequest", "getEntityBytes");
        }
      }
    }
    catch (Throwable localThrowable4)
    {
      localThrowable4 = localThrowable4;
      cy.a(localThrowable4, "BinaryRequest", "getEntityBytes");
      try
      {
        localThrowable1.close();
        return null;
      }
      catch (Throwable localThrowable2)
      {
        for (;;)
        {
          cy.a(localThrowable2, "BinaryRequest", "getEntityBytes");
        }
      }
    }
    finally {}
  }
  
  public abstract byte[] b();
  
  protected String c()
  {
    return "2.1";
  }
  
  public boolean d()
  {
    return true;
  }
  
  public Map<String, String> f()
  {
    String str1 = cl.f(this.a);
    String str2 = co.a();
    String str3 = co.a(this.a, str2, "key=" + str1);
    HashMap localHashMap = new HashMap();
    localHashMap.put("ts", str2);
    localHashMap.put("key", str1);
    localHashMap.put("scode", str3);
    return localHashMap;
  }
  
  /* Error */
  public byte[] h()
  {
    // Byte code:
    //   0: new 30	java/io/ByteArrayOutputStream
    //   3: dup
    //   4: invokespecial 31	java/io/ByteArrayOutputStream:<init>	()V
    //   7: astore_1
    //   8: aload_1
    //   9: iconst_1
    //   10: newarray <illegal type>
    //   12: dup
    //   13: iconst_0
    //   14: iconst_3
    //   15: bastore
    //   16: invokevirtual 42	java/io/ByteArrayOutputStream:write	([B)V
    //   19: aload_0
    //   20: invokevirtual 136	com/amap/api/mapcore2d/dz:d	()Z
    //   23: ifne +83 -> 106
    //   26: aload_1
    //   27: iconst_2
    //   28: newarray <illegal type>
    //   30: dup
    //   31: iconst_0
    //   32: iconst_0
    //   33: bastore
    //   34: dup
    //   35: iconst_1
    //   36: iconst_0
    //   37: bastore
    //   38: invokevirtual 42	java/io/ByteArrayOutputStream:write	([B)V
    //   41: aload_0
    //   42: invokevirtual 138	com/amap/api/mapcore2d/dz:c	()Ljava/lang/String;
    //   45: invokestatic 38	com/amap/api/mapcore2d/cv:a	(Ljava/lang/String;)[B
    //   48: astore_2
    //   49: aload_2
    //   50: ifnonnull +103 -> 153
    //   53: aload_1
    //   54: iconst_2
    //   55: newarray <illegal type>
    //   57: dup
    //   58: iconst_0
    //   59: iconst_0
    //   60: bastore
    //   61: dup
    //   62: iconst_1
    //   63: iconst_0
    //   64: bastore
    //   65: invokevirtual 42	java/io/ByteArrayOutputStream:write	([B)V
    //   68: aload_0
    //   69: invokevirtual 141	com/amap/api/mapcore2d/dz:i	()Ljava/lang/String;
    //   72: invokestatic 38	com/amap/api/mapcore2d/cv:a	(Ljava/lang/String;)[B
    //   75: astore_2
    //   76: aload_2
    //   77: ifnonnull +105 -> 182
    //   80: aload_1
    //   81: iconst_2
    //   82: newarray <illegal type>
    //   84: dup
    //   85: iconst_0
    //   86: iconst_0
    //   87: bastore
    //   88: dup
    //   89: iconst_1
    //   90: iconst_0
    //   91: bastore
    //   92: invokevirtual 42	java/io/ByteArrayOutputStream:write	([B)V
    //   95: aload_1
    //   96: invokevirtual 45	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   99: astore_2
    //   100: aload_1
    //   101: invokevirtual 48	java/io/ByteArrayOutputStream:close	()V
    //   104: aload_2
    //   105: areturn
    //   106: aload_0
    //   107: getfield 23	com/amap/api/mapcore2d/dz:a	Landroid/content/Context;
    //   110: iconst_0
    //   111: invokestatic 144	com/amap/api/mapcore2d/co:a	(Landroid/content/Context;Z)[B
    //   114: astore_2
    //   115: aload_1
    //   116: aload_0
    //   117: aload_2
    //   118: invokevirtual 63	com/amap/api/mapcore2d/dz:a	([B)[B
    //   121: invokevirtual 42	java/io/ByteArrayOutputStream:write	([B)V
    //   124: aload_1
    //   125: aload_2
    //   126: invokevirtual 42	java/io/ByteArrayOutputStream:write	([B)V
    //   129: goto -88 -> 41
    //   132: astore_2
    //   133: aload_2
    //   134: ldc 50
    //   136: ldc -110
    //   138: invokestatic 57	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   141: aload_1
    //   142: invokevirtual 48	java/io/ByteArrayOutputStream:close	()V
    //   145: iconst_1
    //   146: newarray <illegal type>
    //   148: dup
    //   149: iconst_0
    //   150: iconst_0
    //   151: bastore
    //   152: areturn
    //   153: aload_2
    //   154: arraylength
    //   155: ifle -102 -> 53
    //   158: aload_1
    //   159: aload_0
    //   160: aload_2
    //   161: invokevirtual 63	com/amap/api/mapcore2d/dz:a	([B)[B
    //   164: invokevirtual 42	java/io/ByteArrayOutputStream:write	([B)V
    //   167: aload_1
    //   168: aload_2
    //   169: invokevirtual 42	java/io/ByteArrayOutputStream:write	([B)V
    //   172: goto -104 -> 68
    //   175: astore_2
    //   176: aload_1
    //   177: invokevirtual 48	java/io/ByteArrayOutputStream:close	()V
    //   180: aload_2
    //   181: athrow
    //   182: aload_2
    //   183: arraylength
    //   184: ifle -104 -> 80
    //   187: aload_1
    //   188: aload_0
    //   189: aload_2
    //   190: invokevirtual 63	com/amap/api/mapcore2d/dz:a	([B)[B
    //   193: invokevirtual 42	java/io/ByteArrayOutputStream:write	([B)V
    //   196: aload_1
    //   197: aload_2
    //   198: invokevirtual 42	java/io/ByteArrayOutputStream:write	([B)V
    //   201: goto -106 -> 95
    //   204: astore_1
    //   205: aload_1
    //   206: ldc 50
    //   208: ldc 75
    //   210: invokestatic 57	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   213: aload_2
    //   214: areturn
    //   215: astore_1
    //   216: aload_1
    //   217: ldc 50
    //   219: ldc 75
    //   221: invokestatic 57	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   224: goto -79 -> 145
    //   227: astore_1
    //   228: aload_1
    //   229: ldc 50
    //   231: ldc 75
    //   233: invokestatic 57	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   236: goto -56 -> 180
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	239	0	this	dz
    //   7	190	1	localByteArrayOutputStream	ByteArrayOutputStream
    //   204	2	1	localThrowable1	Throwable
    //   215	2	1	localThrowable2	Throwable
    //   227	2	1	localThrowable3	Throwable
    //   48	78	2	arrayOfByte1	byte[]
    //   132	37	2	localThrowable4	Throwable
    //   175	39	2	arrayOfByte2	byte[]
    // Exception table:
    //   from	to	target	type
    //   8	41	132	java/lang/Throwable
    //   41	49	132	java/lang/Throwable
    //   53	68	132	java/lang/Throwable
    //   68	76	132	java/lang/Throwable
    //   80	95	132	java/lang/Throwable
    //   95	100	132	java/lang/Throwable
    //   106	129	132	java/lang/Throwable
    //   153	172	132	java/lang/Throwable
    //   182	201	132	java/lang/Throwable
    //   8	41	175	finally
    //   41	49	175	finally
    //   53	68	175	finally
    //   68	76	175	finally
    //   80	95	175	finally
    //   95	100	175	finally
    //   106	129	175	finally
    //   133	141	175	finally
    //   153	172	175	finally
    //   182	201	175	finally
    //   100	104	204	java/lang/Throwable
    //   141	145	215	java/lang/Throwable
    //   176	180	227	java/lang/Throwable
  }
  
  public String i()
  {
    return String.format("platform=Android&sdkversion=%s&product=%s", new Object[] { this.b.c(), this.b.a() });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/dz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */