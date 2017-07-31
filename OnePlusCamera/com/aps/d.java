package com.aps;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class d
{
  private static d a = null;
  private LinkedHashMap<String, List<a>> b = new LinkedHashMap();
  private p c = null;
  private long d = 0L;
  
  private d(Context paramContext)
  {
    if (paramContext == null) {}
    for (;;)
    {
      return;
      try
      {
        paramContext = b(paramContext);
        if (paramContext != null)
        {
          this.c = p.a(paramContext, 1, 1048576L);
          return;
        }
      }
      catch (Throwable paramContext) {}
    }
  }
  
  private double a(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    double d2 = 0.0D;
    int i = 0;
    double d3 = 0.0D;
    double d1 = 0.0D;
    for (;;)
    {
      if (i >= paramArrayOfDouble1.length) {
        return d1 / (Math.sqrt(d3) * Math.sqrt(d2));
      }
      d3 += paramArrayOfDouble1[i] * paramArrayOfDouble1[i];
      d2 += paramArrayOfDouble2[i] * paramArrayOfDouble2[i];
      d1 += paramArrayOfDouble1[i] * paramArrayOfDouble2[i];
      i += 1;
    }
  }
  
  private a a(String paramString1, StringBuilder paramStringBuilder, String paramString2, String paramString3)
  {
    Object localObject1 = null;
    Hashtable localHashtable1 = new Hashtable();
    Hashtable localHashtable2 = new Hashtable();
    Hashtable localHashtable3 = new Hashtable();
    Iterator localIterator1;
    int i;
    if (!paramString3.equals("mem"))
    {
      localIterator1 = null;
      i = 1;
      if (localIterator1 != null) {
        break label86;
      }
    }
    label51:
    label86:
    while (!localIterator1.hasNext())
    {
      localHashtable1.clear();
      localHashtable2.clear();
      localHashtable3.clear();
      return (a)localObject1;
      localIterator1 = this.b.entrySet().iterator();
      break;
    }
    Object localObject2;
    String str1;
    label137:
    int k;
    if (i == 0)
    {
      localObject2 = (Map.Entry)localIterator1.next();
      str1 = (String)((Map.Entry)localObject2).getKey();
      localObject2 = (List)((Map.Entry)localObject2).getValue();
      if (localObject1 != null) {
        break label185;
      }
      if (localObject2 == null) {
        break label187;
      }
      k = 0;
      if (k < ((List)localObject2).size()) {
        break label190;
      }
    }
    for (;;)
    {
      label162:
      break;
      localObject2 = (List)this.b.get(paramString1);
      str1 = paramString1;
      i = 0;
      break label137;
      label185:
      break label51;
      label187:
      break;
      label190:
      a locala = (a)((List)localObject2).get(k);
      if (TextUtils.isEmpty(locala.b())) {}
      label215:
      label257:
      label295:
      double d1;
      label457:
      label499:
      label524:
      label566:
      label572:
      label593:
      do
      {
        while (d1 <= 0.8500000238418579D)
        {
          do
          {
            k += 1;
            break;
          } while ((TextUtils.isEmpty(paramStringBuilder)) || (str1.indexOf(paramString2) == -1));
          int j;
          Object localObject3;
          if (!a(locala.b(), paramStringBuilder))
          {
            j = 0;
            a(locala.b(), localHashtable1);
            a(paramStringBuilder.toString(), localHashtable2);
            localHashtable3.clear();
            localObject3 = localHashtable1.keySet().iterator();
            if (((Iterator)localObject3).hasNext()) {
              break label457;
            }
            localObject3 = localHashtable2.keySet().iterator();
          }
          double[] arrayOfDouble1;
          double[] arrayOfDouble2;
          Iterator localIterator2;
          int m;
          for (;;)
          {
            if (!((Iterator)localObject3).hasNext())
            {
              localObject3 = localHashtable3.keySet();
              arrayOfDouble1 = new double[((Set)localObject3).size()];
              arrayOfDouble2 = new double[((Set)localObject3).size()];
              localIterator2 = ((Set)localObject3).iterator();
              m = 0;
              if (localIterator2.hasNext()) {
                break label499;
              }
              ((Set)localObject3).clear();
              d1 = a(arrayOfDouble1, arrayOfDouble2);
              if (paramString3.equals("mem")) {
                break label572;
              }
              if ((!paramString3.equals("db")) || (d1 <= 0.8500000238418579D)) {
                break label215;
              }
              localObject1 = locala;
              break;
              if (locala.a().g() > 300.0F)
              {
                j = 0;
                break label257;
              }
              j = 1;
              break label257;
              localHashtable3.put((String)((Iterator)localObject3).next(), "");
              break label295;
            }
            localHashtable3.put((String)((Iterator)localObject3).next(), "");
          }
          String str2 = (String)localIterator2.next();
          if (!localHashtable1.containsKey(str2))
          {
            d1 = 0.0D;
            arrayOfDouble1[m] = d1;
            if (localHashtable2.containsKey(str2)) {
              break label566;
            }
          }
          for (d1 = 0.0D;; d1 = 1.0D)
          {
            arrayOfDouble2[m] = d1;
            m += 1;
            break;
            d1 = 1.0D;
            break label524;
          }
          if (j != 0) {
            break label593;
          }
        }
        localObject1 = locala;
        break label162;
      } while (d1 <= 0.8500000238418579D);
      localObject1 = locala;
    }
  }
  
  /* Error */
  static d a(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 19	com/aps/d:a	Lcom/aps/d;
    //   6: ifnull +12 -> 18
    //   9: getstatic 19	com/aps/d:a	Lcom/aps/d;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: new 2	com/aps/d
    //   21: dup
    //   22: aload_0
    //   23: invokespecial 165	com/aps/d:<init>	(Landroid/content/Context;)V
    //   26: putstatic 19	com/aps/d:a	Lcom/aps/d;
    //   29: goto -20 -> 9
    //   32: astore_0
    //   33: ldc 2
    //   35: monitorexit
    //   36: aload_0
    //   37: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	38	0	paramContext	Context
    // Exception table:
    //   from	to	target	type
    //   3	9	32	finally
    //   9	13	32	finally
    //   18	29	32	finally
  }
  
  private void a(String paramString, Hashtable<String, String> paramHashtable)
  {
    int i = 0;
    paramHashtable.clear();
    paramString = paramString.split("#");
    int j = paramString.length;
    if (i >= j) {
      return;
    }
    Object localObject = paramString[i];
    if (((String)localObject).length() <= 0) {}
    for (;;)
    {
      i += 1;
      break;
      paramHashtable.put(localObject, "");
    }
  }
  
  private boolean a(String paramString1, String paramString2)
  {
    Hashtable localHashtable1 = new Hashtable();
    Hashtable localHashtable2 = new Hashtable();
    Hashtable localHashtable3 = new Hashtable();
    a(paramString2, localHashtable1);
    a(paramString1, localHashtable2);
    localHashtable3.clear();
    paramString1 = localHashtable1.keySet().iterator();
    if (!paramString1.hasNext()) {
      paramString1 = localHashtable2.keySet().iterator();
    }
    double[] arrayOfDouble;
    Iterator localIterator;
    int i;
    for (;;)
    {
      if (!paramString1.hasNext())
      {
        paramString1 = localHashtable3.keySet();
        paramString2 = new double[paramString1.size()];
        arrayOfDouble = new double[paramString1.size()];
        localIterator = paramString1.iterator();
        i = 0;
        if (localIterator.hasNext()) {
          break label211;
        }
        paramString1.clear();
        d1 = a(paramString2, arrayOfDouble);
        localHashtable1.clear();
        localHashtable2.clear();
        localHashtable3.clear();
        if (d1 <= 0.8500000238418579D) {
          break label277;
        }
        return true;
        localHashtable3.put((String)paramString1.next(), "");
        break;
      }
      localHashtable3.put((String)paramString1.next(), "");
    }
    label211:
    String str = (String)localIterator.next();
    if (!localHashtable1.containsKey(str))
    {
      d1 = 0.0D;
      label235:
      paramString2[i] = d1;
      if (localHashtable2.containsKey(str)) {
        break label272;
      }
    }
    label272:
    for (double d1 = 0.0D;; d1 = 1.0D)
    {
      arrayOfDouble[i] = d1;
      i += 1;
      break;
      d1 = 1.0D;
      break label235;
    }
    label277:
    return false;
  }
  
  private boolean a(String paramString, StringBuilder paramStringBuilder)
  {
    int i = paramString.indexOf(",access");
    if (i == -1) {}
    while (i < 17) {
      return false;
    }
    if (paramStringBuilder.indexOf(",access") != -1)
    {
      paramString = paramString.substring(i - 17, i);
      if (paramStringBuilder.toString().indexOf(paramString + ",access") == -1) {
        return false;
      }
    }
    else
    {
      return false;
    }
    return true;
  }
  
  private File b(Context paramContext)
  {
    if (!"mounted".equals(Environment.getExternalStorageState())) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramContext.getExternalCacheDir().getAbsolutePath()).append(File.separator);
    localStringBuilder.append("locationCache");
    return new File(localStringBuilder.toString());
  }
  
  private static void c()
  {
    a = null;
  }
  
  /* Error */
  c a(String paramString1, StringBuilder paramStringBuilder, String paramString2)
  {
    // Byte code:
    //   0: aload_3
    //   1: ldc 58
    //   3: invokevirtual 64	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   6: ifne +32 -> 38
    //   9: aload_1
    //   10: ifnonnull +40 -> 50
    //   13: aload_1
    //   14: ifnonnull +66 -> 80
    //   17: aload_1
    //   18: ifnonnull +92 -> 110
    //   21: aconst_null
    //   22: astore_3
    //   23: aload_3
    //   24: ifnull +168 -> 192
    //   27: aload_3
    //   28: astore_2
    //   29: aload_2
    //   30: ifnull +575 -> 605
    //   33: aload_2
    //   34: invokevirtual 146	com/aps/d$a:a	()Lcom/aps/c;
    //   37: areturn
    //   38: getstatic 227	com/aps/f:k	Z
    //   41: ifne -32 -> 9
    //   44: aload_0
    //   45: invokevirtual 229	com/aps/d:a	()V
    //   48: aconst_null
    //   49: areturn
    //   50: aload_1
    //   51: ldc -25
    //   53: invokevirtual 121	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   56: iconst_m1
    //   57: if_icmpeq -44 -> 13
    //   60: aload_0
    //   61: aload_1
    //   62: aload_2
    //   63: ldc -25
    //   65: aload_3
    //   66: invokespecial 233	com/aps/d:a	(Ljava/lang/String;Ljava/lang/StringBuilder;Ljava/lang/String;Ljava/lang/String;)Lcom/aps/d$a;
    //   69: astore_3
    //   70: aload_3
    //   71: ifnonnull +6 -> 77
    //   74: goto -51 -> 23
    //   77: goto -54 -> 23
    //   80: aload_1
    //   81: ldc -21
    //   83: invokevirtual 121	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   86: iconst_m1
    //   87: if_icmpeq -70 -> 17
    //   90: aload_0
    //   91: aload_1
    //   92: aload_2
    //   93: ldc -21
    //   95: aload_3
    //   96: invokespecial 233	com/aps/d:a	(Ljava/lang/String;Ljava/lang/StringBuilder;Ljava/lang/String;Ljava/lang/String;)Lcom/aps/d$a;
    //   99: astore_3
    //   100: aload_3
    //   101: ifnonnull +6 -> 107
    //   104: goto -81 -> 23
    //   107: goto -84 -> 23
    //   110: aload_1
    //   111: ldc -19
    //   113: invokevirtual 121	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   116: iconst_m1
    //   117: if_icmpeq -96 -> 21
    //   120: aload_3
    //   121: ldc 58
    //   123: invokevirtual 64	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   126: ifne +12 -> 138
    //   129: aconst_null
    //   130: astore_3
    //   131: aload_3
    //   132: ifnonnull +57 -> 189
    //   135: goto -112 -> 23
    //   138: aload_0
    //   139: getfield 31	com/aps/d:b	Ljava/util/LinkedHashMap;
    //   142: aload_1
    //   143: invokevirtual 105	java/util/LinkedHashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   146: checkcast 97	java/util/List
    //   149: astore_3
    //   150: aload_3
    //   151: ifnonnull +8 -> 159
    //   154: aconst_null
    //   155: astore_3
    //   156: goto -25 -> 131
    //   159: aload_3
    //   160: invokeinterface 101 1 0
    //   165: ifle -11 -> 154
    //   168: aload_3
    //   169: aload_3
    //   170: invokeinterface 101 1 0
    //   175: iconst_1
    //   176: isub
    //   177: invokeinterface 108 2 0
    //   182: checkcast 6	com/aps/d$a
    //   185: astore_3
    //   186: goto -55 -> 131
    //   189: goto -166 -> 23
    //   192: aload_2
    //   193: ifnonnull +155 -> 348
    //   196: aload_3
    //   197: astore 4
    //   199: aload_3
    //   200: astore 5
    //   202: aload_3
    //   203: astore 6
    //   205: new 129	java/lang/StringBuilder
    //   208: dup
    //   209: ldc -17
    //   211: invokespecial 240	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   214: astore 7
    //   216: aload_3
    //   217: astore 4
    //   219: aload_3
    //   220: astore 5
    //   222: aload_3
    //   223: astore 6
    //   225: aload_0
    //   226: getfield 33	com/aps/d:c	Lcom/aps/p;
    //   229: ifnonnull +141 -> 370
    //   232: aconst_null
    //   233: astore 8
    //   235: aload_3
    //   236: astore_2
    //   237: aload 8
    //   239: ifnull -210 -> 29
    //   242: aload_3
    //   243: astore 4
    //   245: aload_3
    //   246: astore 5
    //   248: aload_3
    //   249: astore 6
    //   251: aload 8
    //   253: invokeinterface 243 1 0
    //   258: invokeinterface 77 1 0
    //   263: astore 8
    //   265: aload_3
    //   266: astore_2
    //   267: aload 8
    //   269: ifnull -240 -> 29
    //   272: aload_3
    //   273: astore_2
    //   274: aload_3
    //   275: astore 4
    //   277: aload_3
    //   278: astore 5
    //   280: aload_3
    //   281: astore 6
    //   283: aload 8
    //   285: invokeinterface 83 1 0
    //   290: ifeq -261 -> 29
    //   293: aload_3
    //   294: astore 4
    //   296: aload_3
    //   297: astore 5
    //   299: aload_3
    //   300: astore 6
    //   302: aload 8
    //   304: invokeinterface 87 1 0
    //   309: checkcast 89	java/util/Map$Entry
    //   312: astore_2
    //   313: aload_3
    //   314: astore 4
    //   316: aload_3
    //   317: astore 5
    //   319: aload_3
    //   320: astore 6
    //   322: aload_0
    //   323: aload_2
    //   324: invokeinterface 92 1 0
    //   329: checkcast 60	java/lang/String
    //   332: aload 7
    //   334: invokevirtual 132	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   337: invokespecial 245	com/aps/d:a	(Ljava/lang/String;Ljava/lang/String;)Z
    //   340: ifne +52 -> 392
    //   343: aload_3
    //   344: astore_2
    //   345: goto +283 -> 628
    //   348: aload_3
    //   349: astore 4
    //   351: aload_3
    //   352: astore 5
    //   354: aload_3
    //   355: astore 6
    //   357: aload_2
    //   358: invokevirtual 246	java/lang/StringBuilder:length	()I
    //   361: ifeq -165 -> 196
    //   364: aload_2
    //   365: astore 7
    //   367: goto -151 -> 216
    //   370: aload_3
    //   371: astore 4
    //   373: aload_3
    //   374: astore 5
    //   376: aload_3
    //   377: astore 6
    //   379: aload_0
    //   380: getfield 33	com/aps/d:c	Lcom/aps/p;
    //   383: aload_1
    //   384: invokevirtual 249	com/aps/p:a	(Ljava/lang/String;)Ljava/util/Map;
    //   387: astore 8
    //   389: goto -154 -> 235
    //   392: aload_3
    //   393: astore 4
    //   395: aload_3
    //   396: astore 5
    //   398: aload_3
    //   399: astore 6
    //   401: new 148	com/aps/c
    //   404: dup
    //   405: new 251	org/json/JSONObject
    //   408: dup
    //   409: aload_2
    //   410: invokeinterface 95 1 0
    //   415: checkcast 60	java/lang/String
    //   418: invokespecial 252	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   421: invokespecial 255	com/aps/c:<init>	(Lorg/json/JSONObject;)V
    //   424: astore 9
    //   426: aload_3
    //   427: astore 4
    //   429: aload_3
    //   430: astore 5
    //   432: aload_3
    //   433: astore 6
    //   435: aload 9
    //   437: ldc 58
    //   439: invokevirtual 257	com/aps/c:g	(Ljava/lang/String;)V
    //   442: aload_3
    //   443: astore 4
    //   445: aload_3
    //   446: astore 5
    //   448: aload_3
    //   449: astore 6
    //   451: new 6	com/aps/d$a
    //   454: dup
    //   455: invokespecial 258	com/aps/d$a:<init>	()V
    //   458: astore_2
    //   459: aload_2
    //   460: aload 9
    //   462: invokevirtual 261	com/aps/d$a:a	(Lcom/aps/c;)V
    //   465: aload_2
    //   466: aload 7
    //   468: invokevirtual 132	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   471: invokevirtual 263	com/aps/d$a:a	(Ljava/lang/String;)V
    //   474: aload_0
    //   475: getfield 31	com/aps/d:b	Ljava/util/LinkedHashMap;
    //   478: ifnull +47 -> 525
    //   481: aload_1
    //   482: ifnull +155 -> 637
    //   485: aload_0
    //   486: getfield 31	com/aps/d:b	Ljava/util/LinkedHashMap;
    //   489: aload_1
    //   490: invokevirtual 264	java/util/LinkedHashMap:containsKey	(Ljava/lang/Object;)Z
    //   493: ifne +46 -> 539
    //   496: new 266	java/util/ArrayList
    //   499: dup
    //   500: invokespecial 267	java/util/ArrayList:<init>	()V
    //   503: astore_3
    //   504: aload_3
    //   505: aload_2
    //   506: invokeinterface 270 2 0
    //   511: pop
    //   512: aload_0
    //   513: getfield 31	com/aps/d:b	Ljava/util/LinkedHashMap;
    //   516: aload_1
    //   517: aload_3
    //   518: invokevirtual 271	java/util/LinkedHashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   521: pop
    //   522: goto +106 -> 628
    //   525: aload_0
    //   526: new 28	java/util/LinkedHashMap
    //   529: dup
    //   530: invokespecial 29	java/util/LinkedHashMap:<init>	()V
    //   533: putfield 31	com/aps/d:b	Ljava/util/LinkedHashMap;
    //   536: goto -55 -> 481
    //   539: aload_0
    //   540: getfield 31	com/aps/d:b	Ljava/util/LinkedHashMap;
    //   543: aload_1
    //   544: invokevirtual 105	java/util/LinkedHashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   547: checkcast 97	java/util/List
    //   550: astore_3
    //   551: aload_3
    //   552: ifnonnull +6 -> 558
    //   555: goto +85 -> 640
    //   558: aload_3
    //   559: aload_2
    //   560: invokeinterface 274 2 0
    //   565: ifne +75 -> 640
    //   568: aload_3
    //   569: iconst_0
    //   570: aload_2
    //   571: invokeinterface 277 3 0
    //   576: goto +64 -> 640
    //   579: aload_0
    //   580: getfield 31	com/aps/d:b	Ljava/util/LinkedHashMap;
    //   583: aload_1
    //   584: invokevirtual 280	java/util/LinkedHashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   587: pop
    //   588: aload_0
    //   589: getfield 31	com/aps/d:b	Ljava/util/LinkedHashMap;
    //   592: aload_1
    //   593: aload_3
    //   594: invokevirtual 271	java/util/LinkedHashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   597: pop
    //   598: goto +46 -> 644
    //   601: astore_1
    //   602: goto -573 -> 29
    //   605: aconst_null
    //   606: areturn
    //   607: astore_1
    //   608: aload 4
    //   610: astore_2
    //   611: goto -582 -> 29
    //   614: astore_1
    //   615: aload 5
    //   617: astore_2
    //   618: goto -589 -> 29
    //   621: astore_1
    //   622: aload 6
    //   624: astore_2
    //   625: goto -596 -> 29
    //   628: aload_2
    //   629: astore_3
    //   630: goto -365 -> 265
    //   633: astore_1
    //   634: goto -605 -> 29
    //   637: goto -608 -> 29
    //   640: aload_3
    //   641: ifnonnull -62 -> 579
    //   644: goto -16 -> 628
    //   647: astore_1
    //   648: goto -619 -> 29
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	651	0	this	d
    //   0	651	1	paramString1	String
    //   0	651	2	paramStringBuilder	StringBuilder
    //   0	651	3	paramString2	String
    //   197	412	4	str1	String
    //   200	416	5	str2	String
    //   203	420	6	str3	String
    //   214	253	7	localStringBuilder	StringBuilder
    //   233	155	8	localObject	Object
    //   424	37	9	localc	c
    // Exception table:
    //   from	to	target	type
    //   459	481	601	java/lang/Throwable
    //   485	522	601	java/lang/Throwable
    //   525	536	601	java/lang/Throwable
    //   539	551	601	java/lang/Throwable
    //   558	576	601	java/lang/Throwable
    //   579	598	601	java/lang/Throwable
    //   205	216	607	java/lang/Throwable
    //   225	232	607	java/lang/Throwable
    //   251	265	607	java/lang/Throwable
    //   283	293	607	java/lang/Throwable
    //   302	313	607	java/lang/Throwable
    //   322	343	607	java/lang/Throwable
    //   357	364	607	java/lang/Throwable
    //   379	389	607	java/lang/Throwable
    //   401	426	607	java/lang/Throwable
    //   435	442	607	java/lang/Throwable
    //   451	459	607	java/lang/Throwable
    //   205	216	614	java/io/IOException
    //   225	232	614	java/io/IOException
    //   251	265	614	java/io/IOException
    //   283	293	614	java/io/IOException
    //   302	313	614	java/io/IOException
    //   322	343	614	java/io/IOException
    //   357	364	614	java/io/IOException
    //   379	389	614	java/io/IOException
    //   401	426	614	java/io/IOException
    //   435	442	614	java/io/IOException
    //   451	459	614	java/io/IOException
    //   205	216	621	org/json/JSONException
    //   225	232	621	org/json/JSONException
    //   251	265	621	org/json/JSONException
    //   283	293	621	org/json/JSONException
    //   302	313	621	org/json/JSONException
    //   322	343	621	org/json/JSONException
    //   357	364	621	org/json/JSONException
    //   379	389	621	org/json/JSONException
    //   401	426	621	org/json/JSONException
    //   435	442	621	org/json/JSONException
    //   451	459	621	org/json/JSONException
    //   459	481	633	org/json/JSONException
    //   485	522	633	org/json/JSONException
    //   525	536	633	org/json/JSONException
    //   539	551	633	org/json/JSONException
    //   558	576	633	org/json/JSONException
    //   579	598	633	org/json/JSONException
    //   459	481	647	java/io/IOException
    //   485	522	647	java/io/IOException
    //   525	536	647	java/io/IOException
    //   539	551	647	java/io/IOException
    //   558	576	647	java/io/IOException
    //   579	598	647	java/io/IOException
  }
  
  void a()
  {
    this.d = 0L;
    this.b.clear();
  }
  
  void a(String paramString, c paramc, StringBuilder paramStringBuilder)
  {
    label35:
    label67:
    label74:
    Object localObject2;
    if (f.k)
    {
      if (!a(paramString, paramc)) {
        break label197;
      }
      if (paramc.i().equals("mem")) {
        break label198;
      }
      if (paramString != null) {
        break label199;
      }
      if (paramString != null) {
        break label308;
      }
      this.d = t.a();
      localObject1 = new a();
      paramc.g("mem");
      ((a)localObject1).a(paramc);
      if (paramStringBuilder != null) {
        break label330;
      }
      if (this.b == null) {
        break label342;
      }
      if (paramString == null) {
        break label356;
      }
      if (this.b.containsKey(paramString)) {
        break label357;
      }
      localObject2 = new ArrayList();
      ((List)localObject2).add(localObject1);
      this.b.put(paramString, localObject2);
      label119:
      if (paramStringBuilder != null) {
        break label428;
      }
      label123:
      label133:
      break label451;
    }
    try
    {
      paramStringBuilder = new StringBuilder("cell#");
      if (this.c != null) {
        break label438;
      }
      localObject1 = null;
    }
    catch (IOException paramString)
    {
      for (;;) {}
    }
    catch (Exception paramString)
    {
      label146:
      for (;;) {}
    }
    Object localObject1 = new HashMap();
    ((HashMap)localObject1).put(paramStringBuilder.toString(), paramc.u());
    paramc = this.c;
    if (paramc == null) {}
    label178:
    label191:
    label197:
    label198:
    label199:
    label264:
    label296:
    label308:
    label330:
    label342:
    label356:
    label357:
    label426:
    label428:
    label438:
    label451:
    label478:
    label669:
    label677:
    label682:
    label686:
    for (;;)
    {
      int i;
      if (this.b.size() <= 360)
      {
        return;
        a();
        return;
        return;
        return;
        if (!paramString.contains("wifi")) {
          break;
        }
        int j;
        if (!TextUtils.isEmpty(paramStringBuilder))
        {
          if (paramc.g() < 300.0F) {
            break label296;
          }
          localObject1 = paramStringBuilder.toString().split("#");
          int k = localObject1.length;
          i = 0;
          j = 0;
          if (i < k) {
            break label264;
          }
          if (j < 6) {
            break label35;
          }
          return;
        }
        return;
        if (!localObject1[i].contains(",")) {}
        for (;;)
        {
          i += 1;
          break;
          j += 1;
        }
        if (paramc.g() > 10.0F) {
          break label35;
        }
        return;
        if ((!paramString.contains("cell")) || (paramStringBuilder.indexOf(",") == -1)) {
          break label35;
        }
        return;
        ((a)localObject1).a(paramStringBuilder.toString());
        break label67;
        this.b = new LinkedHashMap();
        break label74;
        return;
        localObject2 = (List)this.b.get(paramString);
        if (localObject2 == null) {}
        for (;;)
        {
          if (localObject2 == null) {
            break label426;
          }
          this.b.remove(paramString);
          this.b.put(paramString, localObject2);
          break;
          if (!((List)localObject2).contains(localObject1)) {
            ((List)localObject2).add(0, localObject1);
          }
        }
        break label119;
        if (paramStringBuilder.length() == 0) {
          break label123;
        }
        break label133;
        localObject1 = this.c.a(paramString);
        break label669;
        if (((Map)localObject1).size() == 0) {
          break label146;
        }
        localObject2 = ((Map)localObject1).entrySet().iterator();
        break label677;
      }
      for (;;)
      {
        if (i == 0) {
          break label686;
        }
        ((Map)localObject1).put(paramStringBuilder.toString(), paramc.u());
        this.c.b(paramString, (Map)localObject1);
        break label178;
        this.c.b(paramString, (Map)localObject1);
        break label178;
        do
        {
          if (!((Iterator)localObject2).hasNext()) {
            break label682;
          }
          Map.Entry localEntry = (Map.Entry)((Iterator)localObject2).next();
          if (a((String)localEntry.getKey(), paramStringBuilder.toString()))
          {
            ((Map)localObject1).remove(localEntry.getKey());
            ((Map)localObject1).put(paramStringBuilder.toString(), paramc.u());
            this.c.b(paramString, (Map)localObject1);
            i = 0;
            break label478;
            paramString = this.b.entrySet().iterator();
            if ((paramString == null) || (!paramString.hasNext())) {
              break label191;
            }
            paramString = (String)((Map.Entry)paramString.next()).getKey();
            this.b.remove(paramString);
            return;
            if (localObject1 != null) {
              break;
            }
            break label146;
          }
        } while (localObject2 != null);
        i = 1;
      }
    }
  }
  
  boolean a(String paramString, c paramc)
  {
    if (paramString == null) {}
    while (paramc == null) {
      return false;
    }
    return (paramString.indexOf("#network") != -1) && (paramc.e() != 0.0D);
  }
  
  void b()
  {
    if (this.c == null) {}
    for (;;)
    {
      c();
      return;
      this.c.a();
    }
  }
  
  static class a
  {
    private c a = null;
    private String b = null;
    
    public c a()
    {
      return this.a;
    }
    
    public void a(c paramc)
    {
      this.a = paramc;
    }
    
    public void a(String paramString)
    {
      this.b = paramString.replace("##", "#");
    }
    
    public String b()
    {
      return this.b;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/d.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */