package com.amap.api.mapcore2d;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Looper;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class df
{
  private cu a;
  private int b;
  private dv c;
  private du d;
  
  protected df(int paramInt)
  {
    this.b = paramInt;
  }
  
  public static String a(Context paramContext, cu paramcu)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      String str = cp.e(paramContext);
      localStringBuilder.append("\"sim\":\"").append(str).append("\",\"sdkversion\":\"").append(paramcu.c()).append("\",\"product\":\"").append(paramcu.a()).append("\",\"ed\":\"").append(paramcu.e()).append("\",\"nt\":\"").append(cp.c(paramContext)).append("\",\"np\":\"").append(cp.a(paramContext)).append("\",\"mnc\":\"").append(cp.b(paramContext)).append("\",\"ant\":\"").append(cp.d(paramContext)).append("\"");
      return localStringBuilder.toString();
    }
    catch (Throwable paramContext)
    {
      for (;;)
      {
        paramContext.printStackTrace();
      }
    }
  }
  
  private String a(Context paramContext, String paramString)
  {
    try
    {
      paramContext = co.e(paramContext, cv.a(paramString));
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
    return null;
  }
  
  private String a(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5)
  {
    paramString1 = new StringBuffer();
    paramString1.append(paramString2).append(",").append("\"timestamp\":\"");
    paramString1.append(paramString3);
    paramString1.append("\",\"et\":\"");
    paramString1.append(paramInt);
    paramString1.append("\",\"classname\":\"");
    paramString1.append(paramString4);
    paramString1.append("\",");
    paramString1.append("\"detail\":\"");
    paramString1.append(paramString5);
    paramString1.append("\"");
    return paramString1.toString();
  }
  
  private String a(List<? extends dq> paramList, Context paramContext)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{\"pinfo\":\"").append(g(paramContext)).append("\",\"els\":[");
    paramList = paramList.iterator();
    int i = 1;
    String str;
    do
    {
      if (!paramList.hasNext())
      {
        if (i != 0) {
          break;
        }
        localStringBuilder.append("]}");
        return localStringBuilder.toString();
      }
      paramContext = (dq)paramList.next();
      str = d(paramContext.b());
    } while (str == null);
    if (!"".equals(str))
    {
      paramContext = str + "||" + paramContext.c();
      if (i != 0) {
        break label164;
      }
      localStringBuilder.append(",");
    }
    for (;;)
    {
      localStringBuilder.append("{\"log\":\"").append(paramContext).append("\"}");
      break;
      break;
      label164:
      i = 0;
    }
    return null;
  }
  
  private void a(dp paramdp, int paramInt)
  {
    try
    {
      a(paramdp.a(2, cz.a(paramInt)), paramdp, paramInt);
      return;
    }
    catch (Throwable paramdp)
    {
      cy.a(paramdp, "LogProcessor", "processDeleteFail");
    }
  }
  
  private void a(dp paramdp, String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    dq localdq = cz.b(paramInt);
    localdq.a(0);
    localdq.b(paramString1);
    localdq.a(paramString2);
    paramdp.a(localdq);
  }
  
  private void a(List<? extends dq> paramList, dp paramdp, int paramInt)
  {
    if (paramList == null) {}
    for (;;)
    {
      return;
      if (paramList.size() > 0)
      {
        paramList = paramList.iterator();
        while (paramList.hasNext())
        {
          dq localdq = (dq)paramList.next();
          if (!b(localdq.b()))
          {
            localdq.a(2);
            paramdp.b(localdq);
          }
          else
          {
            paramdp.a(localdq.b(), localdq.getClass());
          }
        }
      }
    }
  }
  
  /* Error */
  private boolean a(Context paramContext, String paramString1, String paramString2, String paramString3, dp paramdp)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aconst_null
    //   4: astore 9
    //   6: aconst_null
    //   7: astore 7
    //   9: new 227	java/io/File
    //   12: dup
    //   13: aload_1
    //   14: aload_3
    //   15: invokestatic 229	com/amap/api/mapcore2d/cz:a	(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;
    //   18: invokespecial 231	java/io/File:<init>	(Ljava/lang/String;)V
    //   21: astore_1
    //   22: aload_1
    //   23: invokevirtual 234	java/io/File:exists	()Z
    //   26: ifeq +83 -> 109
    //   29: aload_1
    //   30: iconst_1
    //   31: iconst_1
    //   32: ldc2_w 235
    //   35: invokestatic 241	com/amap/api/mapcore2d/du:a	(Ljava/io/File;IIJ)Lcom/amap/api/mapcore2d/du;
    //   38: astore_1
    //   39: aload_1
    //   40: aload_0
    //   41: aload 5
    //   43: invokevirtual 244	com/amap/api/mapcore2d/df:a	(Lcom/amap/api/mapcore2d/dp;)Lcom/amap/api/mapcore2d/dv;
    //   46: invokevirtual 247	com/amap/api/mapcore2d/du:a	(Lcom/amap/api/mapcore2d/dv;)V
    //   49: aload_1
    //   50: aload_2
    //   51: invokevirtual 250	com/amap/api/mapcore2d/du:a	(Ljava/lang/String;)Lcom/amap/api/mapcore2d/du$b;
    //   54: astore_3
    //   55: aload_3
    //   56: ifnonnull +66 -> 122
    //   59: aload 4
    //   61: invokestatic 89	com/amap/api/mapcore2d/cv:a	(Ljava/lang/String;)[B
    //   64: astore 4
    //   66: aload_1
    //   67: aload_2
    //   68: invokevirtual 253	com/amap/api/mapcore2d/du:b	(Ljava/lang/String;)Lcom/amap/api/mapcore2d/du$a;
    //   71: astore 5
    //   73: aload 5
    //   75: iconst_0
    //   76: invokevirtual 258	com/amap/api/mapcore2d/du$a:a	(I)Ljava/io/OutputStream;
    //   79: astore_2
    //   80: aload_2
    //   81: aload 4
    //   83: invokevirtual 264	java/io/OutputStream:write	([B)V
    //   86: aload 5
    //   88: invokevirtual 266	com/amap/api/mapcore2d/du$a:a	()V
    //   91: aload_1
    //   92: invokevirtual 268	com/amap/api/mapcore2d/du:b	()V
    //   95: aload_2
    //   96: ifnonnull +169 -> 265
    //   99: aload_3
    //   100: ifnonnull +180 -> 280
    //   103: aload_1
    //   104: ifnonnull +191 -> 295
    //   107: iconst_1
    //   108: ireturn
    //   109: aload_1
    //   110: invokevirtual 271	java/io/File:mkdirs	()Z
    //   113: istore 6
    //   115: iload 6
    //   117: ifne -88 -> 29
    //   120: iconst_0
    //   121: ireturn
    //   122: aload_3
    //   123: ifnonnull +107 -> 230
    //   126: aload_1
    //   127: ifnonnull +118 -> 245
    //   130: iconst_0
    //   131: ireturn
    //   132: astore_3
    //   133: aconst_null
    //   134: astore_1
    //   135: aconst_null
    //   136: astore_2
    //   137: aload 7
    //   139: astore 4
    //   141: aload_3
    //   142: invokevirtual 272	java/io/IOException:printStackTrace	()V
    //   145: aload_2
    //   146: ifnonnull +169 -> 315
    //   149: aload 4
    //   151: ifnonnull +179 -> 330
    //   154: aload_1
    //   155: ifnonnull +191 -> 346
    //   158: iconst_0
    //   159: ireturn
    //   160: astore_2
    //   161: aconst_null
    //   162: astore_1
    //   163: aconst_null
    //   164: astore_3
    //   165: aload 8
    //   167: astore 4
    //   169: aload_2
    //   170: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   173: aload_3
    //   174: ifnonnull +192 -> 366
    //   177: aload 4
    //   179: ifnonnull +202 -> 381
    //   182: aload_1
    //   183: ifnull -25 -> 158
    //   186: aload_1
    //   187: invokevirtual 274	com/amap/api/mapcore2d/du:a	()Z
    //   190: ifne -32 -> 158
    //   193: aload_1
    //   194: invokevirtual 277	com/amap/api/mapcore2d/du:close	()V
    //   197: iconst_0
    //   198: ireturn
    //   199: astore_1
    //   200: aload_1
    //   201: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   204: iconst_0
    //   205: ireturn
    //   206: astore_2
    //   207: aconst_null
    //   208: astore_1
    //   209: aconst_null
    //   210: astore_3
    //   211: aload 9
    //   213: astore 4
    //   215: aload_3
    //   216: ifnonnull +181 -> 397
    //   219: aload 4
    //   221: ifnonnull +191 -> 412
    //   224: aload_1
    //   225: ifnonnull +203 -> 428
    //   228: aload_2
    //   229: athrow
    //   230: aload_3
    //   231: invokevirtual 280	com/amap/api/mapcore2d/du$b:close	()V
    //   234: goto -108 -> 126
    //   237: astore_2
    //   238: aload_2
    //   239: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   242: goto -116 -> 126
    //   245: aload_1
    //   246: invokevirtual 274	com/amap/api/mapcore2d/du:a	()Z
    //   249: ifne -119 -> 130
    //   252: aload_1
    //   253: invokevirtual 277	com/amap/api/mapcore2d/du:close	()V
    //   256: iconst_0
    //   257: ireturn
    //   258: astore_1
    //   259: aload_1
    //   260: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   263: iconst_0
    //   264: ireturn
    //   265: aload_2
    //   266: invokevirtual 281	java/io/OutputStream:close	()V
    //   269: goto -170 -> 99
    //   272: astore_2
    //   273: aload_2
    //   274: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   277: goto -178 -> 99
    //   280: aload_3
    //   281: invokevirtual 280	com/amap/api/mapcore2d/du$b:close	()V
    //   284: goto -181 -> 103
    //   287: astore_2
    //   288: aload_2
    //   289: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   292: goto -189 -> 103
    //   295: aload_1
    //   296: invokevirtual 274	com/amap/api/mapcore2d/du:a	()Z
    //   299: ifne -192 -> 107
    //   302: aload_1
    //   303: invokevirtual 277	com/amap/api/mapcore2d/du:close	()V
    //   306: iconst_1
    //   307: ireturn
    //   308: astore_1
    //   309: aload_1
    //   310: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   313: iconst_1
    //   314: ireturn
    //   315: aload_2
    //   316: invokevirtual 281	java/io/OutputStream:close	()V
    //   319: goto -170 -> 149
    //   322: astore_2
    //   323: aload_2
    //   324: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   327: goto -178 -> 149
    //   330: aload 4
    //   332: invokevirtual 280	com/amap/api/mapcore2d/du$b:close	()V
    //   335: goto -181 -> 154
    //   338: astore_2
    //   339: aload_2
    //   340: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   343: goto -189 -> 154
    //   346: aload_1
    //   347: invokevirtual 274	com/amap/api/mapcore2d/du:a	()Z
    //   350: ifne -192 -> 158
    //   353: aload_1
    //   354: invokevirtual 277	com/amap/api/mapcore2d/du:close	()V
    //   357: iconst_0
    //   358: ireturn
    //   359: astore_1
    //   360: aload_1
    //   361: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   364: iconst_0
    //   365: ireturn
    //   366: aload_3
    //   367: invokevirtual 281	java/io/OutputStream:close	()V
    //   370: goto -193 -> 177
    //   373: astore_2
    //   374: aload_2
    //   375: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   378: goto -201 -> 177
    //   381: aload 4
    //   383: invokevirtual 280	com/amap/api/mapcore2d/du$b:close	()V
    //   386: goto -204 -> 182
    //   389: astore_2
    //   390: aload_2
    //   391: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   394: goto -212 -> 182
    //   397: aload_3
    //   398: invokevirtual 281	java/io/OutputStream:close	()V
    //   401: goto -182 -> 219
    //   404: astore_3
    //   405: aload_3
    //   406: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   409: goto -190 -> 219
    //   412: aload 4
    //   414: invokevirtual 280	com/amap/api/mapcore2d/du$b:close	()V
    //   417: goto -193 -> 224
    //   420: astore_3
    //   421: aload_3
    //   422: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   425: goto -201 -> 224
    //   428: aload_1
    //   429: invokevirtual 274	com/amap/api/mapcore2d/du:a	()Z
    //   432: ifne -204 -> 228
    //   435: aload_1
    //   436: invokevirtual 277	com/amap/api/mapcore2d/du:close	()V
    //   439: goto -211 -> 228
    //   442: astore_1
    //   443: aload_1
    //   444: invokevirtual 83	java/lang/Throwable:printStackTrace	()V
    //   447: goto -219 -> 228
    //   450: astore_2
    //   451: aconst_null
    //   452: astore_3
    //   453: aload 9
    //   455: astore 4
    //   457: goto -242 -> 215
    //   460: astore_2
    //   461: aconst_null
    //   462: astore 5
    //   464: aload_3
    //   465: astore 4
    //   467: aload 5
    //   469: astore_3
    //   470: goto -255 -> 215
    //   473: astore 4
    //   475: aload_2
    //   476: astore 5
    //   478: aload 4
    //   480: astore_2
    //   481: aload_3
    //   482: astore 4
    //   484: aload 5
    //   486: astore_3
    //   487: goto -272 -> 215
    //   490: astore 5
    //   492: aload_2
    //   493: astore_3
    //   494: aload 5
    //   496: astore_2
    //   497: goto -282 -> 215
    //   500: astore_2
    //   501: goto -286 -> 215
    //   504: astore_2
    //   505: aconst_null
    //   506: astore_3
    //   507: aload 8
    //   509: astore 4
    //   511: goto -342 -> 169
    //   514: astore_2
    //   515: aconst_null
    //   516: astore 5
    //   518: aload_3
    //   519: astore 4
    //   521: aload 5
    //   523: astore_3
    //   524: goto -355 -> 169
    //   527: astore 4
    //   529: aload_2
    //   530: astore 5
    //   532: aload 4
    //   534: astore_2
    //   535: aload_3
    //   536: astore 4
    //   538: aload 5
    //   540: astore_3
    //   541: goto -372 -> 169
    //   544: astore_3
    //   545: aconst_null
    //   546: astore_2
    //   547: aload 7
    //   549: astore 4
    //   551: goto -410 -> 141
    //   554: astore 5
    //   556: aconst_null
    //   557: astore_2
    //   558: aload_3
    //   559: astore 4
    //   561: aload 5
    //   563: astore_3
    //   564: goto -423 -> 141
    //   567: astore 5
    //   569: aload_3
    //   570: astore 4
    //   572: aload 5
    //   574: astore_3
    //   575: goto -434 -> 141
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	578	0	this	df
    //   0	578	1	paramContext	Context
    //   0	578	2	paramString1	String
    //   0	578	3	paramString2	String
    //   0	578	4	paramString3	String
    //   0	578	5	paramdp	dp
    //   113	3	6	bool	boolean
    //   7	541	7	localObject1	Object
    //   1	507	8	localObject2	Object
    //   4	450	9	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   9	29	132	java/io/IOException
    //   29	39	132	java/io/IOException
    //   109	115	132	java/io/IOException
    //   9	29	160	java/lang/Throwable
    //   29	39	160	java/lang/Throwable
    //   109	115	160	java/lang/Throwable
    //   193	197	199	java/lang/Throwable
    //   9	29	206	finally
    //   29	39	206	finally
    //   109	115	206	finally
    //   230	234	237	java/lang/Throwable
    //   252	256	258	java/lang/Throwable
    //   265	269	272	java/lang/Throwable
    //   280	284	287	java/lang/Throwable
    //   302	306	308	java/lang/Throwable
    //   315	319	322	java/lang/Throwable
    //   330	335	338	java/lang/Throwable
    //   353	357	359	java/lang/Throwable
    //   366	370	373	java/lang/Throwable
    //   381	386	389	java/lang/Throwable
    //   397	401	404	java/lang/Throwable
    //   412	417	420	java/lang/Throwable
    //   435	439	442	java/lang/Throwable
    //   39	55	450	finally
    //   59	80	460	finally
    //   80	95	473	finally
    //   141	145	490	finally
    //   169	173	500	finally
    //   39	55	504	java/lang/Throwable
    //   59	80	514	java/lang/Throwable
    //   80	95	527	java/lang/Throwable
    //   39	55	544	java/io/IOException
    //   59	80	554	java/io/IOException
    //   80	95	567	java/io/IOException
  }
  
  public static boolean a(String[] paramArrayOfString, String paramString)
  {
    if (paramArrayOfString == null) {}
    while (paramString == null) {
      return false;
    }
    int j;
    int i;
    do
    {
      try
      {
        paramString = paramString.split("\n");
        j = paramString.length;
        i = 0;
      }
      catch (Throwable paramArrayOfString)
      {
        boolean bool;
        paramArrayOfString.printStackTrace();
        return false;
      }
      bool = b(paramArrayOfString, paramString[i].trim());
      if (!bool) {
        i += 1;
      } else {
        return true;
      }
    } while (i < j);
    return false;
  }
  
  private du b(Context paramContext, String paramString)
  {
    try
    {
      paramContext = new File(cz.a(paramContext, paramString));
      if (paramContext.exists()) {}
      boolean bool;
      do
      {
        return du.a(paramContext, 1, 1, 20480L);
        bool = paramContext.mkdirs();
      } while (bool);
      return null;
    }
    catch (IOException paramContext)
    {
      cy.a(paramContext, "LogProcessor", "initDiskLru");
      return null;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "LogProcessor", "initDiskLru");
    }
    return null;
  }
  
  public static String b(Throwable paramThrowable)
  {
    return cv.a(paramThrowable);
  }
  
  private boolean b(String paramString)
  {
    if (this.d != null) {}
    try
    {
      boolean bool = this.d.c(paramString);
      return bool;
    }
    catch (Throwable paramString)
    {
      cy.a(paramString, "LogUpdateProcessor", "deleteLogData");
    }
    return false;
    return false;
  }
  
  public static boolean b(String[] paramArrayOfString, String paramString)
  {
    if (paramArrayOfString == null) {}
    while (paramString == null) {
      return false;
    }
    for (;;)
    {
      int j;
      int i;
      try
      {
        j = paramArrayOfString.length;
        i = 0;
      }
      catch (Throwable paramArrayOfString)
      {
        String str;
        boolean bool;
        paramArrayOfString.printStackTrace();
        return false;
      }
      str = paramArrayOfString[i];
      paramString = paramString.trim();
      if ((paramString.startsWith("at ")) && (paramString.contains(str + ".")))
      {
        bool = paramString.endsWith(")");
        if (bool) {
          return true;
        }
      }
      while (i >= j)
      {
        return false;
        i += 1;
      }
    }
  }
  
  private int c(String paramString)
  {
    int i = 1;
    paramString = new da(cv.c(cv.a(paramString)));
    try
    {
      paramString = dy.a().a(paramString);
      if (paramString != null)
      {
        paramString = cv.a(paramString);
        try
        {
          paramString = new JSONObject(paramString);
          if (!paramString.has("code")) {
            return 0;
          }
          int j = paramString.getInt("code");
          return j;
        }
        catch (JSONException paramString)
        {
          cy.a(paramString, "LogProcessor", "processUpdate");
          return 1;
        }
      }
      return 0;
    }
    catch (ck paramString)
    {
      if (paramString.b() != 27) {}
      for (;;)
      {
        cy.a(paramString, "LogProcessor", "processUpdate");
        return i;
        i = 0;
      }
    }
  }
  
  private String c(Throwable paramThrowable)
  {
    return paramThrowable.toString();
  }
  
  private String d()
  {
    return cv.a(new Date().getTime());
  }
  
  public static String d(Context paramContext)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      localStringBuilder.append("\"key\":\"").append(cl.f(paramContext)).append("\",\"platform\":\"android\",\"diu\":\"").append(cp.q(paramContext)).append("\",\"pkg\":\"").append(cl.c(paramContext)).append("\",\"model\":\"").append(Build.MODEL).append("\",\"appname\":\"").append(cl.b(paramContext)).append("\",\"appversion\":\"").append(cl.d(paramContext)).append("\",\"sysversion\":\"").append(Build.VERSION.RELEASE).append("\",");
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
  
  /* Error */
  private String d(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 301	com/amap/api/mapcore2d/df:d	Lcom/amap/api/mapcore2d/du;
    //   4: ifnull +303 -> 307
    //   7: aload_0
    //   8: getfield 301	com/amap/api/mapcore2d/df:d	Lcom/amap/api/mapcore2d/du;
    //   11: aload_1
    //   12: invokevirtual 250	com/amap/api/mapcore2d/du:a	(Ljava/lang/String;)Lcom/amap/api/mapcore2d/du$b;
    //   15: astore_1
    //   16: aload_1
    //   17: ifnull +292 -> 309
    //   20: aload_1
    //   21: iconst_0
    //   22: invokevirtual 417	com/amap/api/mapcore2d/du$b:a	(I)Ljava/io/InputStream;
    //   25: astore_1
    //   26: new 419	java/io/ByteArrayOutputStream
    //   29: dup
    //   30: invokespecial 420	java/io/ByteArrayOutputStream:<init>	()V
    //   33: astore_3
    //   34: aload_3
    //   35: astore 5
    //   37: aload_1
    //   38: astore 4
    //   40: sipush 1024
    //   43: newarray <illegal type>
    //   45: astore 6
    //   47: aload_3
    //   48: astore 5
    //   50: aload_1
    //   51: astore 4
    //   53: aload_1
    //   54: aload 6
    //   56: invokevirtual 426	java/io/InputStream:read	([B)I
    //   59: istore_2
    //   60: iload_2
    //   61: iconst_m1
    //   62: if_icmpne +25 -> 87
    //   65: aload_3
    //   66: astore 5
    //   68: aload_1
    //   69: astore 4
    //   71: aload_3
    //   72: invokevirtual 430	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   75: invokestatic 346	com/amap/api/mapcore2d/cv:a	([B)Ljava/lang/String;
    //   78: astore 6
    //   80: aload_3
    //   81: ifnonnull +68 -> 149
    //   84: goto +216 -> 300
    //   87: aload_3
    //   88: astore 5
    //   90: aload_1
    //   91: astore 4
    //   93: aload_3
    //   94: aload 6
    //   96: iconst_0
    //   97: iload_2
    //   98: invokevirtual 433	java/io/ByteArrayOutputStream:write	([BII)V
    //   101: goto -54 -> 47
    //   104: astore 6
    //   106: aload_3
    //   107: astore 5
    //   109: aload_1
    //   110: astore 4
    //   112: aload 6
    //   114: ldc -71
    //   116: ldc_w 435
    //   119: invokestatic 192	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   122: aload_3
    //   123: ifnonnull +66 -> 189
    //   126: aload_1
    //   127: ifnonnull +82 -> 209
    //   130: aconst_null
    //   131: areturn
    //   132: astore_3
    //   133: aconst_null
    //   134: astore 5
    //   136: aconst_null
    //   137: astore_1
    //   138: aload 5
    //   140: ifnonnull +87 -> 227
    //   143: aload_1
    //   144: ifnonnull +106 -> 250
    //   147: aload_3
    //   148: athrow
    //   149: aload_3
    //   150: invokevirtual 436	java/io/ByteArrayOutputStream:close	()V
    //   153: goto +147 -> 300
    //   156: astore_3
    //   157: aload_3
    //   158: ldc -71
    //   160: ldc_w 438
    //   163: invokestatic 192	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   166: goto +134 -> 300
    //   169: aload_1
    //   170: invokevirtual 439	java/io/InputStream:close	()V
    //   173: aload 6
    //   175: areturn
    //   176: astore_1
    //   177: aload_1
    //   178: ldc -71
    //   180: ldc_w 441
    //   183: invokestatic 192	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   186: aload 6
    //   188: areturn
    //   189: aload_3
    //   190: invokevirtual 436	java/io/ByteArrayOutputStream:close	()V
    //   193: goto -67 -> 126
    //   196: astore_3
    //   197: aload_3
    //   198: ldc -71
    //   200: ldc_w 438
    //   203: invokestatic 192	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   206: goto -80 -> 126
    //   209: aload_1
    //   210: invokevirtual 439	java/io/InputStream:close	()V
    //   213: aconst_null
    //   214: areturn
    //   215: astore_1
    //   216: aload_1
    //   217: ldc -71
    //   219: ldc_w 441
    //   222: invokestatic 192	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   225: aconst_null
    //   226: areturn
    //   227: aload 5
    //   229: invokevirtual 436	java/io/ByteArrayOutputStream:close	()V
    //   232: goto -89 -> 143
    //   235: astore 4
    //   237: aload 4
    //   239: ldc -71
    //   241: ldc_w 438
    //   244: invokestatic 192	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   247: goto -104 -> 143
    //   250: aload_1
    //   251: invokevirtual 439	java/io/InputStream:close	()V
    //   254: goto -107 -> 147
    //   257: astore_1
    //   258: aload_1
    //   259: ldc -71
    //   261: ldc_w 441
    //   264: invokestatic 192	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   267: goto -120 -> 147
    //   270: astore_3
    //   271: aconst_null
    //   272: astore 5
    //   274: goto -136 -> 138
    //   277: astore_3
    //   278: aload 4
    //   280: astore_1
    //   281: goto -143 -> 138
    //   284: astore 6
    //   286: aconst_null
    //   287: astore_3
    //   288: aconst_null
    //   289: astore_1
    //   290: goto -184 -> 106
    //   293: astore 6
    //   295: aconst_null
    //   296: astore_3
    //   297: goto -191 -> 106
    //   300: aload_1
    //   301: ifnonnull -132 -> 169
    //   304: aload 6
    //   306: areturn
    //   307: aconst_null
    //   308: areturn
    //   309: aconst_null
    //   310: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	311	0	this	df
    //   0	311	1	paramString	String
    //   59	39	2	i	int
    //   33	90	3	localByteArrayOutputStream1	java.io.ByteArrayOutputStream
    //   132	18	3	localObject1	Object
    //   156	34	3	localIOException1	IOException
    //   196	2	3	localIOException2	IOException
    //   270	1	3	localObject2	Object
    //   277	1	3	localObject3	Object
    //   287	10	3	localObject4	Object
    //   38	73	4	str	String
    //   235	44	4	localIOException3	IOException
    //   35	238	5	localByteArrayOutputStream2	java.io.ByteArrayOutputStream
    //   45	50	6	localObject5	Object
    //   104	83	6	localThrowable1	Throwable
    //   284	1	6	localThrowable2	Throwable
    //   293	12	6	localThrowable3	Throwable
    // Exception table:
    //   from	to	target	type
    //   40	47	104	java/lang/Throwable
    //   53	60	104	java/lang/Throwable
    //   71	80	104	java/lang/Throwable
    //   93	101	104	java/lang/Throwable
    //   0	16	132	finally
    //   20	26	132	finally
    //   149	153	156	java/io/IOException
    //   169	173	176	java/io/IOException
    //   189	193	196	java/io/IOException
    //   209	213	215	java/io/IOException
    //   227	232	235	java/io/IOException
    //   250	254	257	java/io/IOException
    //   26	34	270	finally
    //   40	47	277	finally
    //   53	60	277	finally
    //   71	80	277	finally
    //   93	101	277	finally
    //   112	122	277	finally
    //   0	16	284	java/lang/Throwable
    //   20	26	284	java/lang/Throwable
    //   26	34	293	java/lang/Throwable
  }
  
  private void e(Context paramContext)
  {
    try
    {
      this.d = b(paramContext, a());
      return;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "LogProcessor", "LogUpDateProcessor");
    }
  }
  
  private List<cu> f(Context paramContext)
  {
    Object localObject3 = null;
    Object localObject2 = null;
    localObject1 = localObject3;
    try
    {
      Looper localLooper = Looper.getMainLooper();
      localObject1 = localObject3;
      localObject1 = localObject2;
      try
      {
        paramContext = new dr(paramContext, false).a();
        localObject1 = paramContext;
        return paramContext;
      }
      finally {}
      return (List<cu>)localObject1;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  private String g(Context paramContext)
  {
    try
    {
      String str = d(paramContext);
      if (!"".equals(str))
      {
        paramContext = co.b(paramContext, cv.a(str));
        return paramContext;
      }
      return null;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "LogProcessor", "getPublicInfo");
    }
    return null;
  }
  
  protected dv a(dp paramdp)
  {
    for (;;)
    {
      try
      {
        dv localdv = this.c;
        if (localdv == null) {
          continue;
        }
      }
      catch (Throwable paramdp)
      {
        paramdp.printStackTrace();
        continue;
      }
      return this.c;
      this.c = new a(paramdp);
    }
  }
  
  protected String a()
  {
    return cz.c(this.b);
  }
  
  protected String a(String paramString)
  {
    return cr.c(paramString);
  }
  
  protected String a(Throwable paramThrowable)
  {
    try
    {
      paramThrowable = b(paramThrowable);
      return paramThrowable;
    }
    catch (Throwable paramThrowable)
    {
      paramThrowable.printStackTrace();
    }
    return null;
  }
  
  protected abstract String a(List<cu> paramList);
  
  void a(Context paramContext, Throwable paramThrowable, String paramString1, String paramString2)
  {
    Object localObject = f(paramContext);
    if (localObject == null) {}
    while (((List)localObject).size() == 0) {
      return;
    }
    String str = a(paramThrowable);
    if (str == null) {}
    while ("".equals(str)) {
      return;
    }
    localObject = ((List)localObject).iterator();
    cu localcu;
    do
    {
      if (!((Iterator)localObject).hasNext())
      {
        if (str.contains("com.amap.api.col")) {
          break;
        }
        return;
      }
      localcu = (cu)((Iterator)localObject).next();
    } while (!a(localcu.f(), str));
    a(localcu, paramContext, paramThrowable, str.replaceAll("\n", "<br/>"), paramString1, paramString2);
    return;
    try
    {
      a(new cu.a("collection", "1.0", "AMap_collection_1.0").a(new String[] { "com.amap.api.collection" }).a(), paramContext, paramThrowable, str, paramString1, paramString2);
      return;
    }
    catch (ck paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  protected void a(cu paramcu)
  {
    this.a = paramcu;
  }
  
  void a(cu paramcu, Context paramContext, String arg3, String paramString2, String paramString3, String paramString4)
  {
    a(paramcu);
    Object localObject = d();
    String str1 = a(paramContext, paramcu);
    String str2 = cl.a(paramContext);
    if (??? == null) {}
    while ("".equals(???)) {
      return;
    }
    int i = b();
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramString3 == null)
    {
      if (paramString4 != null) {
        break label120;
      }
      label63:
      localStringBuilder.append(paramString2);
      paramString2 = a(paramString2);
      ??? = a(str2, str1, (String)localObject, i, ???, localStringBuilder.toString());
      if (??? != null) {
        break label149;
      }
    }
    label120:
    label149:
    while ("".equals(???))
    {
      return;
      localStringBuilder.append("class:").append(paramString3);
      break;
      localStringBuilder.append(" method:").append(paramString4).append("$").append("<br/>");
      break label63;
    }
    paramString3 = a(paramContext, ???);
    paramString4 = a();
    synchronized (Looper.getMainLooper())
    {
      localObject = new dp(paramContext);
      boolean bool = a(paramContext, paramString2, paramString4, paramString3, (dp)localObject);
      a((dp)localObject, paramcu.a(), paramString2, i, bool);
      return;
    }
  }
  
  void a(cu paramcu, Context paramContext, Throwable paramThrowable, String paramString1, String paramString2, String paramString3)
  {
    a(paramcu, paramContext, c(paramThrowable), paramString1, paramString2, paramString3);
  }
  
  protected abstract boolean a(Context paramContext);
  
  protected int b()
  {
    return this.b;
  }
  
  void b(Context paramContext)
  {
    Object localObject1 = f(paramContext);
    if (localObject1 == null) {}
    while (((List)localObject1).size() == 0) {
      return;
    }
    localObject1 = a((List)localObject1);
    if (localObject1 == null) {}
    while ("".equals(localObject1)) {
      return;
    }
    ??? = d();
    String str1 = a(paramContext, this.a);
    String str2 = cl.a(paramContext);
    int i = b();
    ??? = a(str2, str1, (String)???, i, "ANR", (String)localObject1);
    if (??? == null) {}
    while ("".equals(???)) {
      return;
    }
    localObject1 = a((String)localObject1);
    str1 = a(paramContext, (String)???);
    str2 = a();
    synchronized (Looper.getMainLooper())
    {
      dp localdp = new dp(paramContext);
      boolean bool = a(paramContext, (String)localObject1, str2, str1, localdp);
      a(localdp, this.a.a(), (String)localObject1, i, bool);
      return;
    }
  }
  
  void c()
  {
    try
    {
      if (this.d == null) {
        return;
      }
      if (!this.d.a())
      {
        this.d.close();
        return;
      }
    }
    catch (IOException localIOException)
    {
      cy.a(localIOException, "LogProcessor", "closeDiskLru");
      return;
    }
    catch (Throwable localThrowable)
    {
      cy.a(localThrowable, "LogProcessor", "closeDiskLru");
    }
  }
  
  void c(Context paramContext)
  {
    try
    {
      e(paramContext);
      if (a(paramContext)) {
        synchronized (Looper.getMainLooper())
        {
          dp localdp = new dp(paramContext);
          a(localdp, b());
          List localList = localdp.a(0, cz.a(b()));
          if (localList == null) {}
          while (localList.size() == 0) {
            return;
          }
          paramContext = a(localList, paramContext);
          if (paramContext != null)
          {
            if (c(paramContext) == 1) {}
          }
          else {
            return;
          }
          a(localList, localdp, b());
        }
      }
      return;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "LogProcessor", "processUpdateLog");
      return;
    }
  }
  
  class a
    implements dv
  {
    private dp b;
    
    a(dp paramdp)
    {
      this.b = paramdp;
    }
    
    public void a(String paramString)
    {
      try
      {
        this.b.b(paramString, cz.a(df.this.b()));
        return;
      }
      catch (Throwable paramString)
      {
        paramString.printStackTrace();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/df.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */