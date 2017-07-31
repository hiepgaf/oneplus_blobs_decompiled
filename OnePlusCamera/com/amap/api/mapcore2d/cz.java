package com.amap.api.mapcore2d;

import android.content.Context;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public class cz
{
  public static final String a = "/a/";
  static final String b = "b";
  static final String c = "c";
  static final String d = "d";
  public static final String e = "e";
  public static final String f = "f";
  
  static df a(Context paramContext, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return new dd(paramInt);
    case 1: 
      return new de(paramInt);
    }
    return new dc(paramInt);
  }
  
  public static Class<? extends dq> a(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return dl.class;
    case 1: 
      return dn.class;
    }
    return dk.class;
  }
  
  public static String a(Context paramContext, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramContext.getFilesDir().getAbsolutePath());
    localStringBuilder.append(a);
    localStringBuilder.append(paramString);
    return localStringBuilder.toString();
  }
  
  static void a(Context paramContext)
  {
    try
    {
      df localdf = a(paramContext, 2);
      if (localdf != null)
      {
        localdf.b(paramContext);
        return;
      }
      return;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  static void a(Context paramContext, final Throwable paramThrowable, final int paramInt, final String paramString1, final String paramString2)
  {
    try
    {
      ExecutorService localExecutorService = db.c();
      if (localExecutorService == null) {
        return;
      }
      if (!localExecutorService.isShutdown())
      {
        localExecutorService.submit(new Runnable()
        {
          public void run()
          {
            try
            {
              df localdf = cz.a(this.a, paramInt);
              if (localdf != null)
              {
                localdf.a(this.a, paramThrowable, paramString1, paramString2);
                return;
              }
              return;
            }
            catch (Throwable localThrowable)
            {
              localThrowable.printStackTrace();
            }
          }
        });
        return;
      }
    }
    catch (RejectedExecutionException paramContext) {}catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
      return;
    }
  }
  
  public static dq b(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return new dl();
    case 1: 
      return new dn();
    }
    return new dk();
  }
  
  static void b(Context paramContext)
  {
    try
    {
      ExecutorService localExecutorService = db.c();
      if (localExecutorService == null) {
        return;
      }
      if (!localExecutorService.isShutdown())
      {
        localExecutorService.submit(new Runnable()
        {
          /* Error */
          public void run()
          {
            // Byte code:
            //   0: aconst_null
            //   1: astore_3
            //   2: aconst_null
            //   3: astore 4
            //   5: aload_0
            //   6: getfield 16	com/amap/api/mapcore2d/cz$2:a	Landroid/content/Context;
            //   9: iconst_0
            //   10: invokestatic 28	com/amap/api/mapcore2d/cz:a	(Landroid/content/Context;I)Lcom/amap/api/mapcore2d/df;
            //   13: astore_1
            //   14: aload_0
            //   15: getfield 16	com/amap/api/mapcore2d/cz$2:a	Landroid/content/Context;
            //   18: iconst_1
            //   19: invokestatic 28	com/amap/api/mapcore2d/cz:a	(Landroid/content/Context;I)Lcom/amap/api/mapcore2d/df;
            //   22: astore_2
            //   23: aload_3
            //   24: astore 4
            //   26: aload_0
            //   27: getfield 16	com/amap/api/mapcore2d/cz$2:a	Landroid/content/Context;
            //   30: iconst_2
            //   31: invokestatic 28	com/amap/api/mapcore2d/cz:a	(Landroid/content/Context;I)Lcom/amap/api/mapcore2d/df;
            //   34: astore_3
            //   35: aload_3
            //   36: astore 4
            //   38: aload_1
            //   39: aload_0
            //   40: getfield 16	com/amap/api/mapcore2d/cz$2:a	Landroid/content/Context;
            //   43: invokevirtual 33	com/amap/api/mapcore2d/df:c	(Landroid/content/Context;)V
            //   46: aload_3
            //   47: astore 4
            //   49: aload_2
            //   50: aload_0
            //   51: getfield 16	com/amap/api/mapcore2d/cz$2:a	Landroid/content/Context;
            //   54: invokevirtual 33	com/amap/api/mapcore2d/df:c	(Landroid/content/Context;)V
            //   57: aload_3
            //   58: astore 4
            //   60: aload_3
            //   61: aload_0
            //   62: getfield 16	com/amap/api/mapcore2d/cz$2:a	Landroid/content/Context;
            //   65: invokevirtual 33	com/amap/api/mapcore2d/df:c	(Landroid/content/Context;)V
            //   68: aload_3
            //   69: astore 4
            //   71: aload_0
            //   72: getfield 16	com/amap/api/mapcore2d/cz$2:a	Landroid/content/Context;
            //   75: invokestatic 37	com/amap/api/mapcore2d/ej:a	(Landroid/content/Context;)V
            //   78: aload_3
            //   79: astore 4
            //   81: aload_0
            //   82: getfield 16	com/amap/api/mapcore2d/cz$2:a	Landroid/content/Context;
            //   85: invokestatic 40	com/amap/api/mapcore2d/ei:a	(Landroid/content/Context;)V
            //   88: aload_1
            //   89: ifnonnull +88 -> 177
            //   92: aload_2
            //   93: ifnonnull +91 -> 184
            //   96: aload_3
            //   97: ifnonnull +94 -> 191
            //   100: return
            //   101: astore_1
            //   102: aconst_null
            //   103: astore_2
            //   104: aconst_null
            //   105: astore_1
            //   106: aload_1
            //   107: ifnonnull +89 -> 196
            //   110: aload_2
            //   111: ifnonnull +92 -> 203
            //   114: aload 4
            //   116: ifnonnull +94 -> 210
            //   119: return
            //   120: astore_1
            //   121: aconst_null
            //   122: astore_3
            //   123: aconst_null
            //   124: astore 4
            //   126: aconst_null
            //   127: astore_2
            //   128: aload_1
            //   129: ldc 42
            //   131: ldc 44
            //   133: invokestatic 49	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
            //   136: aload 4
            //   138: ifnonnull +78 -> 216
            //   141: aload_3
            //   142: ifnonnull +82 -> 224
            //   145: aload_2
            //   146: ifnull -46 -> 100
            //   149: aload_2
            //   150: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   153: return
            //   154: astore_1
            //   155: aconst_null
            //   156: astore_3
            //   157: aconst_null
            //   158: astore 4
            //   160: aconst_null
            //   161: astore_2
            //   162: aload 4
            //   164: ifnonnull +67 -> 231
            //   167: aload_3
            //   168: ifnonnull +71 -> 239
            //   171: aload_2
            //   172: ifnonnull +74 -> 246
            //   175: aload_1
            //   176: athrow
            //   177: aload_1
            //   178: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   181: goto -89 -> 92
            //   184: aload_2
            //   185: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   188: goto -92 -> 96
            //   191: aload_3
            //   192: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   195: return
            //   196: aload_1
            //   197: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   200: goto -90 -> 110
            //   203: aload_2
            //   204: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   207: goto -93 -> 114
            //   210: aload 4
            //   212: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   215: return
            //   216: aload 4
            //   218: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   221: goto -80 -> 141
            //   224: aload_3
            //   225: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   228: goto -83 -> 145
            //   231: aload 4
            //   233: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   236: goto -69 -> 167
            //   239: aload_3
            //   240: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   243: goto -72 -> 171
            //   246: aload_2
            //   247: invokevirtual 51	com/amap/api/mapcore2d/df:c	()V
            //   250: goto -75 -> 175
            //   253: astore_2
            //   254: aload_1
            //   255: astore 4
            //   257: aconst_null
            //   258: astore_3
            //   259: aload_2
            //   260: astore_1
            //   261: aconst_null
            //   262: astore_2
            //   263: goto -101 -> 162
            //   266: astore 5
            //   268: aload_1
            //   269: astore 4
            //   271: aload_2
            //   272: astore_3
            //   273: aconst_null
            //   274: astore_2
            //   275: aload 5
            //   277: astore_1
            //   278: goto -116 -> 162
            //   281: astore 6
            //   283: aload_1
            //   284: astore 4
            //   286: aload_2
            //   287: astore 5
            //   289: aload_3
            //   290: astore_2
            //   291: aload 6
            //   293: astore_1
            //   294: aload 5
            //   296: astore_3
            //   297: goto -135 -> 162
            //   300: astore_1
            //   301: goto -139 -> 162
            //   304: astore_2
            //   305: aload_1
            //   306: astore 4
            //   308: aconst_null
            //   309: astore_3
            //   310: aload_2
            //   311: astore_1
            //   312: aconst_null
            //   313: astore_2
            //   314: goto -186 -> 128
            //   317: astore 5
            //   319: aload_1
            //   320: astore 4
            //   322: aload_2
            //   323: astore_3
            //   324: aconst_null
            //   325: astore_2
            //   326: aload 5
            //   328: astore_1
            //   329: goto -201 -> 128
            //   332: astore 6
            //   334: aload_1
            //   335: astore 4
            //   337: aload_2
            //   338: astore 5
            //   340: aload_3
            //   341: astore_2
            //   342: aload 6
            //   344: astore_1
            //   345: aload 5
            //   347: astore_3
            //   348: goto -220 -> 128
            //   351: astore_2
            //   352: aconst_null
            //   353: astore_2
            //   354: goto -248 -> 106
            //   357: astore_3
            //   358: goto -252 -> 106
            // Local variable table:
            //   start	length	slot	name	signature
            //   0	361	0	this	2
            //   13	76	1	localdf1	df
            //   101	1	1	localRejectedExecutionException1	RejectedExecutionException
            //   105	2	1	localObject1	Object
            //   120	9	1	localThrowable1	Throwable
            //   154	101	1	localObject2	Object
            //   260	34	1	localObject3	Object
            //   300	6	1	localObject4	Object
            //   311	34	1	localObject5	Object
            //   22	225	2	localdf2	df
            //   253	7	2	localObject6	Object
            //   262	29	2	localObject7	Object
            //   304	7	2	localThrowable2	Throwable
            //   313	29	2	localObject8	Object
            //   351	1	2	localRejectedExecutionException2	RejectedExecutionException
            //   353	1	2	localObject9	Object
            //   1	347	3	localObject10	Object
            //   357	1	3	localRejectedExecutionException3	RejectedExecutionException
            //   3	333	4	localObject11	Object
            //   266	10	5	localObject12	Object
            //   287	8	5	localObject13	Object
            //   317	10	5	localThrowable3	Throwable
            //   338	8	5	localObject14	Object
            //   281	11	6	localObject15	Object
            //   332	11	6	localThrowable4	Throwable
            // Exception table:
            //   from	to	target	type
            //   5	14	101	java/util/concurrent/RejectedExecutionException
            //   5	14	120	java/lang/Throwable
            //   5	14	154	finally
            //   14	23	253	finally
            //   26	35	266	finally
            //   38	46	281	finally
            //   49	57	281	finally
            //   60	68	281	finally
            //   71	78	281	finally
            //   81	88	281	finally
            //   128	136	300	finally
            //   14	23	304	java/lang/Throwable
            //   26	35	317	java/lang/Throwable
            //   38	46	332	java/lang/Throwable
            //   49	57	332	java/lang/Throwable
            //   60	68	332	java/lang/Throwable
            //   71	78	332	java/lang/Throwable
            //   81	88	332	java/lang/Throwable
            //   14	23	351	java/util/concurrent/RejectedExecutionException
            //   26	35	357	java/util/concurrent/RejectedExecutionException
            //   38	46	357	java/util/concurrent/RejectedExecutionException
            //   49	57	357	java/util/concurrent/RejectedExecutionException
            //   60	68	357	java/util/concurrent/RejectedExecutionException
            //   71	78	357	java/util/concurrent/RejectedExecutionException
            //   81	88	357	java/util/concurrent/RejectedExecutionException
          }
        });
        return;
      }
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "Log", "processLog");
      return;
    }
  }
  
  public static String c(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "";
    case 2: 
      return d;
    case 0: 
      return c;
    }
    return b;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */