package com.amap.api.mapcore2d;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import java.io.File;
import java.io.IOException;

public class ei
{
  private static String a(String paramString)
  {
    int i = 1;
    Object localObject3 = null;
    Object localObject1 = null;
    Object localObject2 = null;
    StringBuilder localStringBuilder = new StringBuilder();
    du localdu;
    label74:
    do
    {
      try
      {
        localdu = du.a(new File(paramString), 1, 1, 204800L);
        localObject2 = localdu;
        localObject3 = localdu;
        localObject1 = localdu;
        paramString = new File(paramString);
        if (paramString != null) {
          break label74;
        }
      }
      catch (IOException paramString)
      {
        for (;;)
        {
          int m;
          int j;
          String str;
          int k;
          localObject1 = localObject2;
          cy.a(paramString, "StatisticsManager", "getContent");
          if (localObject2 != null) {
            try
            {
              ((du)localObject2).close();
            }
            catch (Throwable paramString)
            {
              paramString.printStackTrace();
            }
          }
        }
      }
      catch (Throwable paramString)
      {
        for (;;)
        {
          localObject1 = localObject3;
          cy.a(paramString, "StatisticsManager", "getContent");
          if (localObject3 != null) {
            try
            {
              ((du)localObject3).close();
            }
            catch (Throwable paramString)
            {
              paramString.printStackTrace();
            }
          }
        }
      }
      finally
      {
        if (localObject1 != null) {
          break;
        }
      }
      return localStringBuilder.toString();
      localObject2 = localdu;
      localObject3 = localdu;
      localObject1 = localdu;
    } while (!paramString.exists());
    localObject2 = localdu;
    localObject3 = localdu;
    localObject1 = localdu;
    paramString = paramString.list();
    localObject2 = localdu;
    localObject3 = localdu;
    localObject1 = localdu;
    m = paramString.length;
    j = 0;
    label128:
    if (j < m)
    {
      str = paramString[j];
      k = i;
      localObject2 = localdu;
      localObject3 = localdu;
      localObject1 = localdu;
      if (str.contains(".0"))
      {
        localObject2 = localdu;
        localObject3 = localdu;
        localObject1 = localdu;
        str = cv.a(ek.a(localdu, str.split("\\.")[0]));
        if (i != 0) {
          break label259;
        }
        localObject2 = localdu;
        localObject3 = localdu;
        localObject1 = localdu;
        localStringBuilder.append(",");
      }
    }
    for (;;)
    {
      localObject2 = localdu;
      localObject3 = localdu;
      localObject1 = localdu;
      localStringBuilder.append("{\"log\":\"").append(str).append("\"}");
      k = i;
      j += 1;
      i = k;
      break label128;
      break;
      label259:
      i = 0;
    }
    for (;;)
    {
      throw paramString;
      try
      {
        localdu.close();
      }
      catch (Throwable paramString)
      {
        paramString.printStackTrace();
      }
      break;
      try
      {
        ((du)localObject1).close();
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
      }
    }
  }
  
  public static void a(Context paramContext)
  {
    try
    {
      if (e(paramContext))
      {
        a(paramContext, System.currentTimeMillis());
        paramContext = b(paramContext);
        if (!TextUtils.isEmpty(paramContext))
        {
          paramContext = new da(cv.c(cv.a(paramContext)), "6");
          dy.a().a(paramContext);
        }
      }
      else
      {
        return;
      }
      return;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "OfflineLocManager", "updateOfflineLocData");
    }
  }
  
  /* Error */
  private static void a(Context paramContext, long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 4
    //   6: aload 5
    //   8: astore_3
    //   9: new 18	java/io/File
    //   12: dup
    //   13: aload_0
    //   14: ldc -115
    //   16: invokestatic 146	com/amap/api/mapcore2d/cz:a	(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;
    //   19: invokespecial 21	java/io/File:<init>	(Ljava/lang/String;)V
    //   22: astore_0
    //   23: aload 5
    //   25: astore_3
    //   26: aload_0
    //   27: invokevirtual 150	java/io/File:getParentFile	()Ljava/io/File;
    //   30: invokevirtual 36	java/io/File:exists	()Z
    //   33: ifeq +31 -> 64
    //   36: aload 5
    //   38: astore_3
    //   39: new 152	java/io/FileOutputStream
    //   42: dup
    //   43: aload_0
    //   44: invokespecial 155	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   47: astore_0
    //   48: aload_0
    //   49: lload_1
    //   50: invokestatic 159	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   53: invokestatic 118	com/amap/api/mapcore2d/cv:a	(Ljava/lang/String;)[B
    //   56: invokevirtual 163	java/io/FileOutputStream:write	([B)V
    //   59: aload_0
    //   60: ifnonnull +58 -> 118
    //   63: return
    //   64: aload 5
    //   66: astore_3
    //   67: aload_0
    //   68: invokevirtual 150	java/io/File:getParentFile	()Ljava/io/File;
    //   71: invokevirtual 166	java/io/File:mkdirs	()Z
    //   74: pop
    //   75: goto -39 -> 36
    //   78: astore_3
    //   79: aload 4
    //   81: astore_0
    //   82: aload_3
    //   83: astore 4
    //   85: aload_0
    //   86: astore_3
    //   87: aload 4
    //   89: ldc -119
    //   91: ldc -88
    //   93: invokestatic 83	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   96: aload_0
    //   97: ifnull -34 -> 63
    //   100: aload_0
    //   101: invokevirtual 169	java/io/FileOutputStream:close	()V
    //   104: return
    //   105: astore_0
    //   106: aload_0
    //   107: invokevirtual 89	java/lang/Throwable:printStackTrace	()V
    //   110: return
    //   111: astore_0
    //   112: aload_3
    //   113: ifnonnull +16 -> 129
    //   116: aload_0
    //   117: athrow
    //   118: aload_0
    //   119: invokevirtual 169	java/io/FileOutputStream:close	()V
    //   122: return
    //   123: astore_0
    //   124: aload_0
    //   125: invokevirtual 89	java/lang/Throwable:printStackTrace	()V
    //   128: return
    //   129: aload_3
    //   130: invokevirtual 169	java/io/FileOutputStream:close	()V
    //   133: goto -17 -> 116
    //   136: astore_3
    //   137: aload_3
    //   138: invokevirtual 89	java/lang/Throwable:printStackTrace	()V
    //   141: goto -25 -> 116
    //   144: astore 4
    //   146: aload_0
    //   147: astore_3
    //   148: aload 4
    //   150: astore_0
    //   151: goto -39 -> 112
    //   154: astore 4
    //   156: goto -71 -> 85
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	159	0	paramContext	Context
    //   0	159	1	paramLong	long
    //   8	59	3	localObject1	Object
    //   78	5	3	localThrowable1	Throwable
    //   86	44	3	localContext1	Context
    //   136	2	3	localThrowable2	Throwable
    //   147	1	3	localContext2	Context
    //   4	84	4	localObject2	Object
    //   144	5	4	localObject3	Object
    //   154	1	4	localThrowable3	Throwable
    //   1	64	5	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   9	23	78	java/lang/Throwable
    //   26	36	78	java/lang/Throwable
    //   39	48	78	java/lang/Throwable
    //   67	75	78	java/lang/Throwable
    //   100	104	105	java/lang/Throwable
    //   9	23	111	finally
    //   26	36	111	finally
    //   39	48	111	finally
    //   67	75	111	finally
    //   87	96	111	finally
    //   118	122	123	java/lang/Throwable
    //   129	133	136	java/lang/Throwable
    //   48	59	144	finally
    //   48	59	154	java/lang/Throwable
  }
  
  private static String b(Context paramContext)
  {
    String str = a(cz.a(paramContext, cz.f));
    if (!TextUtils.isEmpty(str))
    {
      paramContext = f(paramContext);
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("{\"pinfo\":\"").append(paramContext).append("\",\"els\":[");
      localStringBuilder.append(str);
      localStringBuilder.append("]}");
      return localStringBuilder.toString();
    }
    return null;
  }
  
  private static int c(Context paramContext)
  {
    try
    {
      paramContext = new File(cz.a(paramContext, cz.f));
      if (paramContext.exists())
      {
        int i = paramContext.list().length;
        return i;
      }
      return 0;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "OfflineLocManager", "getFileNum");
    }
    return 0;
  }
  
  /* Error */
  private static long d(Context paramContext)
  {
    // Byte code:
    //   0: new 18	java/io/File
    //   3: dup
    //   4: aload_0
    //   5: ldc -115
    //   7: invokestatic 146	com/amap/api/mapcore2d/cz:a	(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;
    //   10: invokespecial 21	java/io/File:<init>	(Ljava/lang/String;)V
    //   13: astore 5
    //   15: aload 5
    //   17: invokevirtual 36	java/io/File:exists	()Z
    //   20: ifeq +49 -> 69
    //   23: new 190	java/io/FileInputStream
    //   26: dup
    //   27: aload 5
    //   29: invokespecial 191	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   32: astore_3
    //   33: aload_3
    //   34: astore_0
    //   35: aload_3
    //   36: invokevirtual 195	java/io/FileInputStream:available	()I
    //   39: newarray <illegal type>
    //   41: astore 4
    //   43: aload_3
    //   44: astore_0
    //   45: aload_3
    //   46: aload 4
    //   48: invokevirtual 199	java/io/FileInputStream:read	([B)I
    //   51: pop
    //   52: aload_3
    //   53: astore_0
    //   54: aload 4
    //   56: invokestatic 64	com/amap/api/mapcore2d/cv:a	([B)Ljava/lang/String;
    //   59: invokestatic 205	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   62: lstore_1
    //   63: aload_3
    //   64: ifnonnull +81 -> 145
    //   67: lload_1
    //   68: lreturn
    //   69: lconst_0
    //   70: lreturn
    //   71: astore 4
    //   73: aconst_null
    //   74: astore_3
    //   75: aload_3
    //   76: astore_0
    //   77: aload 4
    //   79: ldc -119
    //   81: ldc -49
    //   83: invokestatic 83	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   86: aload 5
    //   88: ifnonnull +11 -> 99
    //   91: aload_3
    //   92: ifnonnull +66 -> 158
    //   95: invokestatic 101	java/lang/System:currentTimeMillis	()J
    //   98: lreturn
    //   99: aload_3
    //   100: astore_0
    //   101: aload 5
    //   103: invokevirtual 36	java/io/File:exists	()Z
    //   106: ifeq -15 -> 91
    //   109: aload_3
    //   110: astore_0
    //   111: aload 5
    //   113: invokevirtual 210	java/io/File:delete	()Z
    //   116: pop
    //   117: goto -26 -> 91
    //   120: astore 4
    //   122: aload_3
    //   123: astore_0
    //   124: aload 4
    //   126: invokevirtual 89	java/lang/Throwable:printStackTrace	()V
    //   129: goto -38 -> 91
    //   132: astore 4
    //   134: aload_0
    //   135: astore_3
    //   136: aload 4
    //   138: astore_0
    //   139: aload_3
    //   140: ifnonnull +33 -> 173
    //   143: aload_0
    //   144: athrow
    //   145: aload_3
    //   146: invokevirtual 211	java/io/FileInputStream:close	()V
    //   149: lload_1
    //   150: lreturn
    //   151: astore_0
    //   152: aload_0
    //   153: invokevirtual 89	java/lang/Throwable:printStackTrace	()V
    //   156: lload_1
    //   157: lreturn
    //   158: aload_3
    //   159: invokevirtual 211	java/io/FileInputStream:close	()V
    //   162: goto -67 -> 95
    //   165: astore_0
    //   166: aload_0
    //   167: invokevirtual 89	java/lang/Throwable:printStackTrace	()V
    //   170: goto -75 -> 95
    //   173: aload_3
    //   174: invokevirtual 211	java/io/FileInputStream:close	()V
    //   177: goto -34 -> 143
    //   180: astore_3
    //   181: aload_3
    //   182: invokevirtual 89	java/lang/Throwable:printStackTrace	()V
    //   185: goto -42 -> 143
    //   188: astore_0
    //   189: aconst_null
    //   190: astore_3
    //   191: goto -52 -> 139
    //   194: astore 4
    //   196: goto -121 -> 75
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	199	0	paramContext	Context
    //   62	95	1	l	long
    //   32	142	3	localObject1	Object
    //   180	2	3	localThrowable1	Throwable
    //   190	1	3	localObject2	Object
    //   41	14	4	arrayOfByte	byte[]
    //   71	7	4	localThrowable2	Throwable
    //   120	5	4	localThrowable3	Throwable
    //   132	5	4	localObject3	Object
    //   194	1	4	localThrowable4	Throwable
    //   13	99	5	localFile	File
    // Exception table:
    //   from	to	target	type
    //   23	33	71	java/lang/Throwable
    //   101	109	120	java/lang/Throwable
    //   111	117	120	java/lang/Throwable
    //   35	43	132	finally
    //   45	52	132	finally
    //   54	63	132	finally
    //   77	86	132	finally
    //   101	109	132	finally
    //   111	117	132	finally
    //   124	129	132	finally
    //   145	149	151	java/lang/Throwable
    //   158	162	165	java/lang/Throwable
    //   173	177	180	java/lang/Throwable
    //   23	33	188	finally
    //   35	43	194	java/lang/Throwable
    //   45	52	194	java/lang/Throwable
    //   54	63	194	java/lang/Throwable
  }
  
  private static boolean e(Context paramContext)
  {
    try
    {
      if (cp.m(paramContext) != 1) {
        return false;
      }
      long l = d(paramContext);
      if (System.currentTimeMillis() - l > 604800000L) {
        break label64;
      }
      i = 1;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "StatisticsManager", "isUpdate");
      return false;
    }
    int i = c(paramContext);
    return i >= 100;
    while (i == 0)
    {
      return true;
      label64:
      i = 0;
    }
  }
  
  private static String f(Context paramContext)
  {
    return co.b(paramContext, cv.a(g(paramContext)));
  }
  
  private static String g(Context paramContext)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      localStringBuilder.append("\"key\":\"").append(cl.f(paramContext)).append("\",\"platform\":\"android\",\"diu\":\"").append(cp.q(paramContext)).append("\",\"mac\":\"").append(cp.i(paramContext)).append("\",\"tid\":\"").append(cp.f(paramContext)).append("\",\"manufacture\":\"").append(Build.MANUFACTURER).append("\",\"device\":\"").append(Build.DEVICE).append("\",\"sim\":\"").append(cp.r(paramContext)).append("\",\"pkg\":\"").append(cl.c(paramContext)).append("\",\"model\":\"").append(Build.MODEL).append("\",\"appversion\":\"").append(cl.d(paramContext)).append("\"");
      return localStringBuilder.toString();
    }
    catch (Throwable paramContext)
    {
      for (;;)
      {
        cy.a(paramContext, "CInfo", "getPublicJSONInfo");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ei.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */