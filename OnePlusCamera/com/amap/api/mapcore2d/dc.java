package com.amap.api.mapcore2d;

import android.content.Context;
import android.os.Looper;

public class dc
  extends df
{
  private static boolean a = true;
  private String[] b = new String[10];
  private int c = 0;
  private boolean d = false;
  private int e = 0;
  
  protected dc(int paramInt)
  {
    super(paramInt);
  }
  
  private void b(String paramString)
  {
    try
    {
      if (this.c <= 9) {}
      for (;;)
      {
        this.b[this.c] = paramString;
        this.c += 1;
        return;
        this.c = 0;
      }
      return;
    }
    catch (Throwable paramString)
    {
      cy.a(paramString, "ANRWriter", "addData");
    }
  }
  
  private String d()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (;;)
    {
      try
      {
        i = this.c;
      }
      catch (Throwable localThrowable)
      {
        int j;
        cy.a(localThrowable, "ANRWriter", "getLogInfo");
        continue;
        if (i < 10) {
          continue;
        }
        int i = 0;
        continue;
      }
      j = this.c;
      if (i >= j)
      {
        return localStringBuilder.toString();
        if (i > 9) {
          continue;
        }
        localStringBuilder.append(this.b[i]);
        i += 1;
        continue;
      }
      localStringBuilder.append(this.b[i]);
      i += 1;
    }
  }
  
  /* Error */
  protected String a(java.util.List<cu> paramList)
  {
    // Byte code:
    //   0: new 66	java/io/File
    //   3: dup
    //   4: ldc 68
    //   6: invokespecial 70	java/io/File:<init>	(Ljava/lang/String;)V
    //   9: astore 4
    //   11: aload 4
    //   13: invokevirtual 74	java/io/File:exists	()Z
    //   16: ifeq +185 -> 201
    //   19: new 76	java/io/FileInputStream
    //   22: dup
    //   23: aload 4
    //   25: invokespecial 79	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   28: astore 4
    //   30: new 81	com/amap/api/mapcore2d/dw
    //   33: dup
    //   34: aload 4
    //   36: getstatic 86	com/amap/api/mapcore2d/dx:a	Ljava/nio/charset/Charset;
    //   39: invokespecial 89	com/amap/api/mapcore2d/dw:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
    //   42: astore 5
    //   44: iconst_0
    //   45: istore_2
    //   46: aload 5
    //   48: astore 7
    //   50: aload 4
    //   52: astore 6
    //   54: aload 5
    //   56: invokevirtual 91	com/amap/api/mapcore2d/dw:a	()Ljava/lang/String;
    //   59: astore 9
    //   61: aload 9
    //   63: astore 8
    //   65: aload 5
    //   67: astore 7
    //   69: aload 4
    //   71: astore 6
    //   73: aload 9
    //   75: ldc 93
    //   77: invokevirtual 97	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   80: ifne +138 -> 218
    //   83: aload 9
    //   85: astore 8
    //   87: aload 5
    //   89: astore 7
    //   91: aload 4
    //   93: astore 6
    //   95: aload 8
    //   97: ldc 99
    //   99: invokevirtual 103	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   102: ifne +641 -> 743
    //   105: iload_2
    //   106: istore_3
    //   107: iload_3
    //   108: istore_2
    //   109: iload_3
    //   110: ifeq -64 -> 46
    //   113: aload 5
    //   115: astore 7
    //   117: aload 4
    //   119: astore 6
    //   121: aload_0
    //   122: aload 8
    //   124: invokespecial 105	com/amap/api/mapcore2d/dc:b	(Ljava/lang/String;)V
    //   127: aload 5
    //   129: astore 7
    //   131: aload 4
    //   133: astore 6
    //   135: aload_0
    //   136: getfield 31	com/amap/api/mapcore2d/dc:e	I
    //   139: iconst_5
    //   140: if_icmpeq +42 -> 182
    //   143: aload 5
    //   145: astore 7
    //   147: aload 4
    //   149: astore 6
    //   151: aload_0
    //   152: getfield 29	com/amap/api/mapcore2d/dc:d	Z
    //   155: ifeq +86 -> 241
    //   158: aload 5
    //   160: astore 7
    //   162: aload 4
    //   164: astore 6
    //   166: aload_0
    //   167: aload_0
    //   168: getfield 31	com/amap/api/mapcore2d/dc:e	I
    //   171: iconst_1
    //   172: iadd
    //   173: putfield 31	com/amap/api/mapcore2d/dc:e	I
    //   176: iload_3
    //   177: istore_2
    //   178: goto -132 -> 46
    //   181: astore_1
    //   182: aload 5
    //   184: ifnonnull +279 -> 463
    //   187: aload 4
    //   189: ifnonnull +306 -> 495
    //   192: aload_0
    //   193: getfield 29	com/amap/api/mapcore2d/dc:d	Z
    //   196: ifne +262 -> 458
    //   199: aconst_null
    //   200: areturn
    //   201: aconst_null
    //   202: areturn
    //   203: aload 5
    //   205: astore 7
    //   207: aload 4
    //   209: astore 6
    //   211: aload 5
    //   213: invokevirtual 91	com/amap/api/mapcore2d/dw:a	()Ljava/lang/String;
    //   216: astore 8
    //   218: aload 5
    //   220: astore 7
    //   222: aload 4
    //   224: astore 6
    //   226: aload 8
    //   228: ldc 107
    //   230: invokevirtual 97	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   233: ifeq -30 -> 203
    //   236: iconst_1
    //   237: istore_2
    //   238: goto -151 -> 87
    //   241: aload 5
    //   243: astore 7
    //   245: aload 4
    //   247: astore 6
    //   249: aload_1
    //   250: invokeinterface 113 1 0
    //   255: astore 9
    //   257: iload_3
    //   258: istore_2
    //   259: aload 5
    //   261: astore 7
    //   263: aload 4
    //   265: astore 6
    //   267: aload 9
    //   269: invokeinterface 118 1 0
    //   274: ifeq -228 -> 46
    //   277: aload 5
    //   279: astore 7
    //   281: aload 4
    //   283: astore 6
    //   285: aload 9
    //   287: invokeinterface 122 1 0
    //   292: checkcast 124	com/amap/api/mapcore2d/cu
    //   295: astore 10
    //   297: aload 5
    //   299: astore 7
    //   301: aload 4
    //   303: astore 6
    //   305: aload_0
    //   306: aload 10
    //   308: invokevirtual 128	com/amap/api/mapcore2d/cu:f	()[Ljava/lang/String;
    //   311: aload 8
    //   313: invokestatic 131	com/amap/api/mapcore2d/dc:b	([Ljava/lang/String;Ljava/lang/String;)Z
    //   316: putfield 29	com/amap/api/mapcore2d/dc:d	Z
    //   319: aload 5
    //   321: astore 7
    //   323: aload 4
    //   325: astore 6
    //   327: aload_0
    //   328: getfield 29	com/amap/api/mapcore2d/dc:d	Z
    //   331: ifeq -74 -> 257
    //   334: aload 5
    //   336: astore 7
    //   338: aload 4
    //   340: astore 6
    //   342: aload_0
    //   343: aload 10
    //   345: invokevirtual 134	com/amap/api/mapcore2d/dc:a	(Lcom/amap/api/mapcore2d/cu;)V
    //   348: goto -91 -> 257
    //   351: astore_1
    //   352: aload 4
    //   354: astore_1
    //   355: aload 5
    //   357: astore 4
    //   359: aload 4
    //   361: ifnonnull +166 -> 527
    //   364: aload_1
    //   365: ifnull -173 -> 192
    //   368: aload_1
    //   369: invokevirtual 139	java/io/InputStream:close	()V
    //   372: goto -180 -> 192
    //   375: astore_1
    //   376: aload_1
    //   377: ldc 36
    //   379: ldc -115
    //   381: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   384: goto -192 -> 192
    //   387: astore 8
    //   389: aconst_null
    //   390: astore_1
    //   391: aconst_null
    //   392: astore 4
    //   394: aload_1
    //   395: astore 7
    //   397: aload 4
    //   399: astore 6
    //   401: aload 8
    //   403: ldc 36
    //   405: ldc -113
    //   407: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   410: aload_1
    //   411: ifnonnull +164 -> 575
    //   414: aload 4
    //   416: ifnull -224 -> 192
    //   419: aload 4
    //   421: invokevirtual 139	java/io/InputStream:close	()V
    //   424: goto -232 -> 192
    //   427: astore_1
    //   428: aload_1
    //   429: ldc 36
    //   431: ldc -115
    //   433: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   436: goto -244 -> 192
    //   439: astore_1
    //   440: aconst_null
    //   441: astore 7
    //   443: aconst_null
    //   444: astore 4
    //   446: aload 7
    //   448: ifnonnull +170 -> 618
    //   451: aload 4
    //   453: ifnonnull +201 -> 654
    //   456: aload_1
    //   457: athrow
    //   458: aload_0
    //   459: invokespecial 145	com/amap/api/mapcore2d/dc:d	()Ljava/lang/String;
    //   462: areturn
    //   463: aload 5
    //   465: invokevirtual 146	com/amap/api/mapcore2d/dw:close	()V
    //   468: goto -281 -> 187
    //   471: astore_1
    //   472: aload_1
    //   473: ldc 36
    //   475: ldc -108
    //   477: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   480: goto -293 -> 187
    //   483: astore_1
    //   484: aload_1
    //   485: ldc 36
    //   487: ldc -106
    //   489: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   492: goto -305 -> 187
    //   495: aload 4
    //   497: invokevirtual 139	java/io/InputStream:close	()V
    //   500: goto -308 -> 192
    //   503: astore_1
    //   504: aload_1
    //   505: ldc 36
    //   507: ldc -115
    //   509: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   512: goto -320 -> 192
    //   515: astore_1
    //   516: aload_1
    //   517: ldc 36
    //   519: ldc -104
    //   521: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   524: goto -332 -> 192
    //   527: aload 4
    //   529: invokevirtual 146	com/amap/api/mapcore2d/dw:close	()V
    //   532: goto -168 -> 364
    //   535: astore 4
    //   537: aload 4
    //   539: ldc 36
    //   541: ldc -108
    //   543: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   546: goto -182 -> 364
    //   549: astore 4
    //   551: aload 4
    //   553: ldc 36
    //   555: ldc -106
    //   557: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   560: goto -196 -> 364
    //   563: astore_1
    //   564: aload_1
    //   565: ldc 36
    //   567: ldc -104
    //   569: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   572: goto -380 -> 192
    //   575: aload_1
    //   576: invokevirtual 146	com/amap/api/mapcore2d/dw:close	()V
    //   579: goto -165 -> 414
    //   582: astore_1
    //   583: aload_1
    //   584: ldc 36
    //   586: ldc -108
    //   588: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   591: goto -177 -> 414
    //   594: astore_1
    //   595: aload_1
    //   596: ldc 36
    //   598: ldc -106
    //   600: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   603: goto -189 -> 414
    //   606: astore_1
    //   607: aload_1
    //   608: ldc 36
    //   610: ldc -104
    //   612: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   615: goto -423 -> 192
    //   618: aload 7
    //   620: invokevirtual 146	com/amap/api/mapcore2d/dw:close	()V
    //   623: goto -172 -> 451
    //   626: astore 5
    //   628: aload 5
    //   630: ldc 36
    //   632: ldc -108
    //   634: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   637: goto -186 -> 451
    //   640: astore 5
    //   642: aload 5
    //   644: ldc 36
    //   646: ldc -106
    //   648: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   651: goto -200 -> 451
    //   654: aload 4
    //   656: invokevirtual 139	java/io/InputStream:close	()V
    //   659: goto -203 -> 456
    //   662: astore 4
    //   664: aload 4
    //   666: ldc 36
    //   668: ldc -115
    //   670: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   673: goto -217 -> 456
    //   676: astore 4
    //   678: aload 4
    //   680: ldc 36
    //   682: ldc -104
    //   684: invokestatic 43	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   687: goto -231 -> 456
    //   690: astore_1
    //   691: aconst_null
    //   692: astore 7
    //   694: goto -248 -> 446
    //   697: astore_1
    //   698: aload 6
    //   700: astore 4
    //   702: goto -256 -> 446
    //   705: astore 8
    //   707: aconst_null
    //   708: astore_1
    //   709: goto -315 -> 394
    //   712: astore 8
    //   714: aload 5
    //   716: astore_1
    //   717: goto -323 -> 394
    //   720: astore_1
    //   721: aconst_null
    //   722: astore 4
    //   724: aconst_null
    //   725: astore_1
    //   726: goto -367 -> 359
    //   729: astore_1
    //   730: aconst_null
    //   731: astore 5
    //   733: aload 4
    //   735: astore_1
    //   736: aload 5
    //   738: astore 4
    //   740: goto -381 -> 359
    //   743: iconst_0
    //   744: istore_3
    //   745: goto -638 -> 107
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	748	0	this	dc
    //   0	748	1	paramList	java.util.List<cu>
    //   45	214	2	i	int
    //   106	639	3	j	int
    //   9	519	4	localObject1	Object
    //   535	3	4	localIOException1	java.io.IOException
    //   549	106	4	localThrowable1	Throwable
    //   662	3	4	localIOException2	java.io.IOException
    //   676	3	4	localThrowable2	Throwable
    //   700	39	4	localObject2	Object
    //   42	422	5	localdw	dw
    //   626	3	5	localIOException3	java.io.IOException
    //   640	75	5	localThrowable3	Throwable
    //   731	6	5	localObject3	Object
    //   52	647	6	localObject4	Object
    //   48	645	7	localObject5	Object
    //   63	249	8	localObject6	Object
    //   387	15	8	localIOException4	java.io.IOException
    //   705	1	8	localIOException5	java.io.IOException
    //   712	1	8	localIOException6	java.io.IOException
    //   59	227	9	localObject7	Object
    //   295	49	10	localcu	cu
    // Exception table:
    //   from	to	target	type
    //   54	61	181	java/io/EOFException
    //   73	83	181	java/io/EOFException
    //   95	105	181	java/io/EOFException
    //   121	127	181	java/io/EOFException
    //   135	143	181	java/io/EOFException
    //   151	158	181	java/io/EOFException
    //   166	176	181	java/io/EOFException
    //   211	218	181	java/io/EOFException
    //   226	236	181	java/io/EOFException
    //   249	257	181	java/io/EOFException
    //   267	277	181	java/io/EOFException
    //   285	297	181	java/io/EOFException
    //   305	319	181	java/io/EOFException
    //   327	334	181	java/io/EOFException
    //   342	348	181	java/io/EOFException
    //   54	61	351	java/io/FileNotFoundException
    //   73	83	351	java/io/FileNotFoundException
    //   95	105	351	java/io/FileNotFoundException
    //   121	127	351	java/io/FileNotFoundException
    //   135	143	351	java/io/FileNotFoundException
    //   151	158	351	java/io/FileNotFoundException
    //   166	176	351	java/io/FileNotFoundException
    //   211	218	351	java/io/FileNotFoundException
    //   226	236	351	java/io/FileNotFoundException
    //   249	257	351	java/io/FileNotFoundException
    //   267	277	351	java/io/FileNotFoundException
    //   285	297	351	java/io/FileNotFoundException
    //   305	319	351	java/io/FileNotFoundException
    //   327	334	351	java/io/FileNotFoundException
    //   342	348	351	java/io/FileNotFoundException
    //   368	372	375	java/io/IOException
    //   0	30	387	java/io/IOException
    //   419	424	427	java/io/IOException
    //   0	30	439	finally
    //   463	468	471	java/io/IOException
    //   463	468	483	java/lang/Throwable
    //   495	500	503	java/io/IOException
    //   495	500	515	java/lang/Throwable
    //   527	532	535	java/io/IOException
    //   527	532	549	java/lang/Throwable
    //   368	372	563	java/lang/Throwable
    //   575	579	582	java/io/IOException
    //   575	579	594	java/lang/Throwable
    //   419	424	606	java/lang/Throwable
    //   618	623	626	java/io/IOException
    //   618	623	640	java/lang/Throwable
    //   654	659	662	java/io/IOException
    //   654	659	676	java/lang/Throwable
    //   30	44	690	finally
    //   54	61	697	finally
    //   73	83	697	finally
    //   95	105	697	finally
    //   121	127	697	finally
    //   135	143	697	finally
    //   151	158	697	finally
    //   166	176	697	finally
    //   211	218	697	finally
    //   226	236	697	finally
    //   249	257	697	finally
    //   267	277	697	finally
    //   285	297	697	finally
    //   305	319	697	finally
    //   327	334	697	finally
    //   342	348	697	finally
    //   401	410	697	finally
    //   30	44	705	java/io/IOException
    //   54	61	712	java/io/IOException
    //   73	83	712	java/io/IOException
    //   95	105	712	java/io/IOException
    //   121	127	712	java/io/IOException
    //   135	143	712	java/io/IOException
    //   151	158	712	java/io/IOException
    //   166	176	712	java/io/IOException
    //   211	218	712	java/io/IOException
    //   226	236	712	java/io/IOException
    //   249	257	712	java/io/IOException
    //   267	277	712	java/io/IOException
    //   285	297	712	java/io/IOException
    //   305	319	712	java/io/IOException
    //   327	334	712	java/io/IOException
    //   342	348	712	java/io/IOException
    //   0	30	720	java/io/FileNotFoundException
    //   30	44	729	java/io/FileNotFoundException
  }
  
  protected boolean a(Context paramContext)
  {
    if (cp.m(paramContext) != 1) {}
    while (!a) {
      return false;
    }
    a = false;
    synchronized (Looper.getMainLooper())
    {
      paramContext = new ds(paramContext);
      dt localdt = paramContext.a();
      if (localdt != null)
      {
        if (!localdt.c()) {
          return false;
        }
      }
      else {
        return true;
      }
      localdt.c(false);
      paramContext.a(localdt);
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/dc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */