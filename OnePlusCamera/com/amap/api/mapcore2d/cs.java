package com.amap.api.mapcore2d;

import android.content.Context;
import android.os.Build.VERSION;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

public class cs
{
  private static String a()
  {
    Object localObject = null;
    try
    {
      String str = android.net.Proxy.getDefaultHost();
      localObject = str;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        cy.a(localThrowable, "ProxyUtil", "getDefHost");
      }
    }
    if (localObject != null) {
      return (String)localObject;
    }
    return "null";
  }
  
  public static String a(String paramString)
  {
    return cv.c(paramString);
  }
  
  public static java.net.Proxy a(Context paramContext)
  {
    try
    {
      if (Build.VERSION.SDK_INT < 11) {
        return b(paramContext);
      }
      paramContext = a(paramContext, new URI("http://restapi.amap.com"));
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "ProxyUtil", "getProxy");
    }
    return null;
  }
  
  private static java.net.Proxy a(Context paramContext, URI paramURI)
  {
    if (!c(paramContext)) {
      return null;
    }
    try
    {
      paramContext = ProxySelector.getDefault().select(paramURI);
      if ((paramContext != null) && (!paramContext.isEmpty()))
      {
        paramContext = (java.net.Proxy)paramContext.get(0);
        if (paramContext == null) {
          break label80;
        }
        paramURI = paramContext.type();
        Proxy.Type localType = Proxy.Type.DIRECT;
        if (paramURI == localType) {
          break label80;
        }
        return paramContext;
      }
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "ProxyUtil", "getProxySelectorCfg");
      return null;
    }
    return null;
    label80:
    return null;
  }
  
  private static boolean a(String paramString, int paramInt)
  {
    if (paramString == null) {}
    while ((paramString.length() <= 0) || (paramInt == -1)) {
      return false;
    }
    return true;
  }
  
  private static int b()
  {
    try
    {
      int i = android.net.Proxy.getDefaultPort();
      return i;
    }
    catch (Throwable localThrowable)
    {
      cy.a(localThrowable, "ProxyUtil", "getDefPort");
    }
    return -1;
  }
  
  /* Error */
  private static java.net.Proxy b(Context paramContext)
  {
    // Byte code:
    //   0: bipush 80
    //   2: istore_3
    //   3: iconst_m1
    //   4: istore_1
    //   5: iconst_0
    //   6: istore 4
    //   8: aload_0
    //   9: invokestatic 57	com/amap/api/mapcore2d/cs:c	(Landroid/content/Context;)Z
    //   12: ifne +5 -> 17
    //   15: aconst_null
    //   16: areturn
    //   17: ldc 107
    //   19: invokestatic 113	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   22: astore 6
    //   24: aload_0
    //   25: invokevirtual 119	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   28: astore 7
    //   30: aload 7
    //   32: aload 6
    //   34: aconst_null
    //   35: aconst_null
    //   36: aconst_null
    //   37: aconst_null
    //   38: invokevirtual 125	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   41: astore 6
    //   43: aload 6
    //   45: ifnonnull +40 -> 85
    //   48: iconst_m1
    //   49: istore_1
    //   50: aconst_null
    //   51: astore_0
    //   52: aload 6
    //   54: ifnonnull +299 -> 353
    //   57: iload_1
    //   58: istore_2
    //   59: aload_0
    //   60: iload_2
    //   61: invokestatic 127	com/amap/api/mapcore2d/cs:a	(Ljava/lang/String;I)Z
    //   64: ifeq -49 -> 15
    //   67: new 79	java/net/Proxy
    //   70: dup
    //   71: getstatic 130	java/net/Proxy$Type:HTTP	Ljava/net/Proxy$Type;
    //   74: aload_0
    //   75: iload_2
    //   76: invokestatic 136	java/net/InetSocketAddress:createUnresolved	(Ljava/lang/String;I)Ljava/net/InetSocketAddress;
    //   79: invokespecial 139	java/net/Proxy:<init>	(Ljava/net/Proxy$Type;Ljava/net/SocketAddress;)V
    //   82: astore_0
    //   83: aload_0
    //   84: areturn
    //   85: aload 6
    //   87: astore 8
    //   89: aload 6
    //   91: invokeinterface 144 1 0
    //   96: ifeq -48 -> 48
    //   99: aload 6
    //   101: astore 8
    //   103: aload 6
    //   105: aload 6
    //   107: ldc -110
    //   109: invokeinterface 150 2 0
    //   114: invokeinterface 154 2 0
    //   119: astore 7
    //   121: aload 7
    //   123: ifnonnull +6 -> 129
    //   126: goto +719 -> 845
    //   129: aload 6
    //   131: astore 8
    //   133: aload 7
    //   135: getstatic 160	java/util/Locale:US	Ljava/util/Locale;
    //   138: invokevirtual 164	java/lang/String:toLowerCase	(Ljava/util/Locale;)Ljava/lang/String;
    //   141: astore 7
    //   143: goto +702 -> 845
    //   146: aload 6
    //   148: astore 8
    //   150: aload 7
    //   152: ldc -90
    //   154: invokevirtual 170	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   157: ifeq +693 -> 850
    //   160: aload 6
    //   162: astore 8
    //   164: invokestatic 172	com/amap/api/mapcore2d/cs:a	()Ljava/lang/String;
    //   167: astore 9
    //   169: aload 6
    //   171: astore 8
    //   173: invokestatic 174	com/amap/api/mapcore2d/cs:b	()I
    //   176: istore_2
    //   177: iload_2
    //   178: istore_1
    //   179: aload 6
    //   181: astore 8
    //   183: aload 9
    //   185: invokestatic 178	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   188: ifeq +11 -> 199
    //   191: iconst_0
    //   192: istore_2
    //   193: aconst_null
    //   194: astore 9
    //   196: goto +666 -> 862
    //   199: aload 6
    //   201: astore 8
    //   203: aload 9
    //   205: ldc 24
    //   207: invokevirtual 182	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   210: istore 5
    //   212: iload 5
    //   214: ifeq +11 -> 225
    //   217: iconst_0
    //   218: istore_2
    //   219: aconst_null
    //   220: astore 9
    //   222: goto +640 -> 862
    //   225: iconst_1
    //   226: istore_2
    //   227: goto +635 -> 862
    //   230: aload 6
    //   232: astore 8
    //   234: ldc -72
    //   236: invokestatic 186	com/amap/api/mapcore2d/cs:a	(Ljava/lang/String;)Ljava/lang/String;
    //   239: astore 7
    //   241: aload 7
    //   243: astore_0
    //   244: goto +625 -> 869
    //   247: bipush 80
    //   249: istore_1
    //   250: goto +624 -> 874
    //   253: aload 6
    //   255: astore 8
    //   257: aload 7
    //   259: ldc -68
    //   261: invokevirtual 170	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   264: ifeq +591 -> 855
    //   267: aload 6
    //   269: astore 8
    //   271: invokestatic 172	com/amap/api/mapcore2d/cs:a	()Ljava/lang/String;
    //   274: astore 9
    //   276: aload 6
    //   278: astore 8
    //   280: invokestatic 174	com/amap/api/mapcore2d/cs:b	()I
    //   283: istore_2
    //   284: aload 6
    //   286: astore 8
    //   288: aload 9
    //   290: invokestatic 178	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   293: ifeq +11 -> 304
    //   296: iconst_0
    //   297: istore_1
    //   298: aconst_null
    //   299: astore 9
    //   301: goto +576 -> 877
    //   304: aload 6
    //   306: astore 8
    //   308: aload 9
    //   310: ldc 24
    //   312: invokevirtual 182	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   315: istore 5
    //   317: iload 5
    //   319: ifeq +11 -> 330
    //   322: iconst_0
    //   323: istore_1
    //   324: aconst_null
    //   325: astore 9
    //   327: goto +550 -> 877
    //   330: iconst_1
    //   331: istore_1
    //   332: goto +545 -> 877
    //   335: aload 6
    //   337: astore 8
    //   339: ldc -66
    //   341: invokestatic 186	com/amap/api/mapcore2d/cs:a	(Ljava/lang/String;)Ljava/lang/String;
    //   344: astore 7
    //   346: aload 7
    //   348: astore 9
    //   350: goto +531 -> 881
    //   353: aload 6
    //   355: invokeinterface 194 1 0
    //   360: iload_1
    //   361: istore_2
    //   362: goto -303 -> 59
    //   365: astore 6
    //   367: aload 6
    //   369: ldc 15
    //   371: ldc -60
    //   373: invokestatic 22	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   376: aload 6
    //   378: invokevirtual 199	java/lang/Throwable:printStackTrace	()V
    //   381: iload_1
    //   382: istore_2
    //   383: goto -324 -> 59
    //   386: astore 8
    //   388: aconst_null
    //   389: astore 7
    //   391: iconst_m1
    //   392: istore_1
    //   393: aconst_null
    //   394: astore 6
    //   396: aload 8
    //   398: ldc 15
    //   400: ldc -55
    //   402: invokestatic 22	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   405: aload_0
    //   406: invokestatic 207	com/amap/api/mapcore2d/cp:o	(Landroid/content/Context;)Ljava/lang/String;
    //   409: astore_0
    //   410: aload_0
    //   411: ifnonnull +49 -> 460
    //   414: aload 6
    //   416: astore_0
    //   417: iload_1
    //   418: istore_2
    //   419: aload 7
    //   421: ifnull -362 -> 59
    //   424: aload 7
    //   426: invokeinterface 194 1 0
    //   431: aload 6
    //   433: astore_0
    //   434: iload_1
    //   435: istore_2
    //   436: goto -377 -> 59
    //   439: astore_0
    //   440: aload_0
    //   441: ldc 15
    //   443: ldc -60
    //   445: invokestatic 22	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   448: aload_0
    //   449: invokevirtual 199	java/lang/Throwable:printStackTrace	()V
    //   452: aload 6
    //   454: astore_0
    //   455: iload_1
    //   456: istore_2
    //   457: goto -398 -> 59
    //   460: aload_0
    //   461: getstatic 160	java/util/Locale:US	Ljava/util/Locale;
    //   464: invokevirtual 164	java/lang/String:toLowerCase	(Ljava/util/Locale;)Ljava/lang/String;
    //   467: astore 8
    //   469: invokestatic 172	com/amap/api/mapcore2d/cs:a	()Ljava/lang/String;
    //   472: astore_0
    //   473: invokestatic 174	com/amap/api/mapcore2d/cs:b	()I
    //   476: istore_2
    //   477: aload 8
    //   479: ldc -90
    //   481: invokevirtual 210	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   484: iconst_m1
    //   485: if_icmpne +19 -> 504
    //   488: aload 8
    //   490: ldc -68
    //   492: invokevirtual 210	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   495: iconst_m1
    //   496: if_icmpne +50 -> 546
    //   499: iload_2
    //   500: istore_1
    //   501: goto -87 -> 414
    //   504: aload_0
    //   505: invokestatic 178	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   508: ifeq +9 -> 517
    //   511: iload 4
    //   513: istore_1
    //   514: goto +385 -> 899
    //   517: iload 4
    //   519: istore_1
    //   520: aload_0
    //   521: ldc 24
    //   523: invokevirtual 182	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   526: ifne +373 -> 899
    //   529: iconst_1
    //   530: istore_1
    //   531: aload_0
    //   532: astore 6
    //   534: goto +365 -> 899
    //   537: ldc -72
    //   539: invokestatic 186	com/amap/api/mapcore2d/cs:a	(Ljava/lang/String;)Ljava/lang/String;
    //   542: astore_0
    //   543: goto +363 -> 906
    //   546: aload_0
    //   547: invokestatic 178	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   550: ifeq +11 -> 561
    //   553: iconst_0
    //   554: istore_1
    //   555: aload 6
    //   557: astore_0
    //   558: goto +366 -> 924
    //   561: aload_0
    //   562: ldc 24
    //   564: invokevirtual 182	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   567: ifeq +369 -> 936
    //   570: iconst_0
    //   571: istore_1
    //   572: aload 6
    //   574: astore_0
    //   575: goto +349 -> 924
    //   578: ldc -66
    //   580: invokestatic 186	com/amap/api/mapcore2d/cs:a	(Ljava/lang/String;)Ljava/lang/String;
    //   583: astore_0
    //   584: goto +344 -> 928
    //   587: astore 7
    //   589: aconst_null
    //   590: astore 6
    //   592: aconst_null
    //   593: astore_0
    //   594: aload 6
    //   596: astore 8
    //   598: aload 7
    //   600: ldc 15
    //   602: ldc -44
    //   604: invokestatic 22	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   607: aload 6
    //   609: astore 8
    //   611: aload 7
    //   613: invokevirtual 199	java/lang/Throwable:printStackTrace	()V
    //   616: aload 6
    //   618: ifnonnull +8 -> 626
    //   621: iload_1
    //   622: istore_2
    //   623: goto -564 -> 59
    //   626: aload 6
    //   628: invokeinterface 194 1 0
    //   633: iload_1
    //   634: istore_2
    //   635: goto -576 -> 59
    //   638: astore 6
    //   640: aload 6
    //   642: ldc 15
    //   644: ldc -60
    //   646: invokestatic 22	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   649: aload 6
    //   651: invokevirtual 199	java/lang/Throwable:printStackTrace	()V
    //   654: goto -21 -> 633
    //   657: astore_0
    //   658: aconst_null
    //   659: astore 8
    //   661: aload 8
    //   663: ifnonnull +5 -> 668
    //   666: aload_0
    //   667: athrow
    //   668: aload 8
    //   670: invokeinterface 194 1 0
    //   675: goto -9 -> 666
    //   678: astore 6
    //   680: aload 6
    //   682: ldc 15
    //   684: ldc -60
    //   686: invokestatic 22	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   689: aload 6
    //   691: invokevirtual 199	java/lang/Throwable:printStackTrace	()V
    //   694: goto -28 -> 666
    //   697: astore_0
    //   698: aload_0
    //   699: ldc 15
    //   701: ldc -60
    //   703: invokestatic 22	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   706: aload_0
    //   707: invokevirtual 199	java/lang/Throwable:printStackTrace	()V
    //   710: aconst_null
    //   711: areturn
    //   712: astore_0
    //   713: goto -52 -> 661
    //   716: astore_0
    //   717: aload 7
    //   719: astore 8
    //   721: goto -60 -> 661
    //   724: astore 7
    //   726: aconst_null
    //   727: astore_0
    //   728: goto -134 -> 594
    //   731: astore 7
    //   733: aconst_null
    //   734: astore_0
    //   735: goto -141 -> 594
    //   738: astore 7
    //   740: aload 9
    //   742: astore_0
    //   743: goto -149 -> 594
    //   746: astore 7
    //   748: iload_2
    //   749: istore_1
    //   750: aconst_null
    //   751: astore_0
    //   752: goto -158 -> 594
    //   755: astore 7
    //   757: iload_2
    //   758: istore_1
    //   759: aload 9
    //   761: astore_0
    //   762: goto -168 -> 594
    //   765: astore 8
    //   767: aconst_null
    //   768: astore 9
    //   770: iconst_m1
    //   771: istore_1
    //   772: aload 6
    //   774: astore 7
    //   776: aload 9
    //   778: astore 6
    //   780: goto -384 -> 396
    //   783: astore 8
    //   785: aconst_null
    //   786: astore 9
    //   788: aload 6
    //   790: astore 7
    //   792: aload 9
    //   794: astore 6
    //   796: goto -400 -> 396
    //   799: astore 8
    //   801: aload 6
    //   803: astore 7
    //   805: aload 9
    //   807: astore 6
    //   809: goto -413 -> 396
    //   812: astore 8
    //   814: aconst_null
    //   815: astore 9
    //   817: iload_2
    //   818: istore_1
    //   819: aload 6
    //   821: astore 7
    //   823: aload 9
    //   825: astore 6
    //   827: goto -431 -> 396
    //   830: astore 8
    //   832: iload_2
    //   833: istore_1
    //   834: aload 6
    //   836: astore 7
    //   838: aload 9
    //   840: astore 6
    //   842: goto -446 -> 396
    //   845: aload 7
    //   847: ifnonnull -701 -> 146
    //   850: aload 7
    //   852: ifnonnull -599 -> 253
    //   855: iconst_m1
    //   856: istore_1
    //   857: aconst_null
    //   858: astore_0
    //   859: goto -807 -> 52
    //   862: iload_2
    //   863: ifeq -633 -> 230
    //   866: aload 9
    //   868: astore_0
    //   869: iload_1
    //   870: iconst_m1
    //   871: if_icmpeq -624 -> 247
    //   874: goto -822 -> 52
    //   877: iload_1
    //   878: ifeq -543 -> 335
    //   881: aload 9
    //   883: astore_0
    //   884: iload_3
    //   885: istore_1
    //   886: iload_2
    //   887: iconst_m1
    //   888: if_icmpeq -836 -> 52
    //   891: iload_2
    //   892: istore_1
    //   893: aload 9
    //   895: astore_0
    //   896: goto -844 -> 52
    //   899: iload_1
    //   900: ifeq -363 -> 537
    //   903: aload 6
    //   905: astore_0
    //   906: aload_0
    //   907: astore 6
    //   909: iload_3
    //   910: istore_1
    //   911: iload_2
    //   912: iconst_m1
    //   913: if_icmpeq -499 -> 414
    //   916: iload_2
    //   917: istore_1
    //   918: aload_0
    //   919: astore 6
    //   921: goto -507 -> 414
    //   924: iload_1
    //   925: ifeq -347 -> 578
    //   928: aload_0
    //   929: astore 6
    //   931: iload_3
    //   932: istore_1
    //   933: goto -519 -> 414
    //   936: iconst_1
    //   937: istore_1
    //   938: goto -14 -> 924
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	941	0	paramContext	Context
    //   4	934	1	i	int
    //   58	859	2	j	int
    //   2	930	3	k	int
    //   6	512	4	m	int
    //   210	108	5	bool	boolean
    //   22	332	6	localObject1	Object
    //   365	12	6	localThrowable1	Throwable
    //   394	233	6	localContext	Context
    //   638	12	6	localThrowable2	Throwable
    //   678	95	6	localThrowable3	Throwable
    //   778	152	6	localObject2	Object
    //   28	397	7	localObject3	Object
    //   587	131	7	localThrowable4	Throwable
    //   724	1	7	localThrowable5	Throwable
    //   731	1	7	localThrowable6	Throwable
    //   738	1	7	localThrowable7	Throwable
    //   746	1	7	localThrowable8	Throwable
    //   755	1	7	localThrowable9	Throwable
    //   774	77	7	localObject4	Object
    //   87	251	8	localObject5	Object
    //   386	11	8	localSecurityException1	SecurityException
    //   467	253	8	localObject6	Object
    //   765	1	8	localSecurityException2	SecurityException
    //   783	1	8	localSecurityException3	SecurityException
    //   799	1	8	localSecurityException4	SecurityException
    //   812	1	8	localSecurityException5	SecurityException
    //   830	1	8	localSecurityException6	SecurityException
    //   167	727	9	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   353	360	365	java/lang/Throwable
    //   30	43	386	java/lang/SecurityException
    //   424	431	439	java/lang/Throwable
    //   30	43	587	java/lang/Throwable
    //   626	633	638	java/lang/Throwable
    //   30	43	657	finally
    //   668	675	678	java/lang/Throwable
    //   59	83	697	java/lang/Throwable
    //   89	99	712	finally
    //   103	121	712	finally
    //   133	143	712	finally
    //   150	160	712	finally
    //   164	169	712	finally
    //   173	177	712	finally
    //   183	191	712	finally
    //   203	212	712	finally
    //   234	241	712	finally
    //   257	267	712	finally
    //   271	276	712	finally
    //   280	284	712	finally
    //   288	296	712	finally
    //   308	317	712	finally
    //   339	346	712	finally
    //   598	607	712	finally
    //   611	616	712	finally
    //   396	410	716	finally
    //   460	499	716	finally
    //   504	511	716	finally
    //   520	529	716	finally
    //   537	543	716	finally
    //   546	553	716	finally
    //   561	570	716	finally
    //   578	584	716	finally
    //   89	99	724	java/lang/Throwable
    //   103	121	724	java/lang/Throwable
    //   133	143	724	java/lang/Throwable
    //   150	160	724	java/lang/Throwable
    //   164	169	724	java/lang/Throwable
    //   173	177	724	java/lang/Throwable
    //   257	267	724	java/lang/Throwable
    //   271	276	724	java/lang/Throwable
    //   280	284	724	java/lang/Throwable
    //   183	191	731	java/lang/Throwable
    //   203	212	731	java/lang/Throwable
    //   234	241	738	java/lang/Throwable
    //   288	296	746	java/lang/Throwable
    //   308	317	746	java/lang/Throwable
    //   339	346	755	java/lang/Throwable
    //   89	99	765	java/lang/SecurityException
    //   103	121	765	java/lang/SecurityException
    //   133	143	765	java/lang/SecurityException
    //   150	160	765	java/lang/SecurityException
    //   164	169	765	java/lang/SecurityException
    //   173	177	765	java/lang/SecurityException
    //   257	267	765	java/lang/SecurityException
    //   271	276	765	java/lang/SecurityException
    //   280	284	765	java/lang/SecurityException
    //   183	191	783	java/lang/SecurityException
    //   203	212	783	java/lang/SecurityException
    //   234	241	799	java/lang/SecurityException
    //   288	296	812	java/lang/SecurityException
    //   308	317	812	java/lang/SecurityException
    //   339	346	830	java/lang/SecurityException
  }
  
  private static boolean c(Context paramContext)
  {
    return cp.m(paramContext) == 0;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */