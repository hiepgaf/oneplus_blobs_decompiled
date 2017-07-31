package com.amap.api.mapcore2d;

import android.os.Build.VERSION;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

public class eb
{
  private static ec a;
  private int b;
  private int c;
  private boolean d;
  private SSLContext e;
  private Proxy f;
  private volatile boolean g = false;
  private long h = -1L;
  private long i = 0L;
  private String j;
  private dy.a k;
  private HostnameVerifier l = new HostnameVerifier()
  {
    public boolean verify(String paramAnonymousString, SSLSession paramAnonymousSSLSession)
    {
      paramAnonymousString = HttpsURLConnection.getDefaultHostnameVerifier();
      if (paramAnonymousString.verify("*.amap.com", paramAnonymousSSLSession)) {}
      while (paramAnonymousString.verify("*.apilocate.amap.com", paramAnonymousSSLSession)) {
        return true;
      }
      return false;
    }
  };
  
  eb(int paramInt1, int paramInt2, Proxy paramProxy)
  {
    this(paramInt1, paramInt2, paramProxy, false);
  }
  
  eb(int paramInt1, int paramInt2, Proxy paramProxy, boolean paramBoolean)
  {
    this(paramInt1, paramInt2, paramProxy, paramBoolean, null);
  }
  
  eb(int paramInt1, int paramInt2, Proxy paramProxy, boolean paramBoolean, dy.a parama)
  {
    this.b = paramInt1;
    this.c = paramInt2;
    this.f = paramProxy;
    this.d = paramBoolean;
    this.k = parama;
    a();
    if (!paramBoolean) {
      return;
    }
    try
    {
      paramProxy = SSLContext.getInstance("TLS");
      paramProxy.init(null, null, null);
      this.e = paramProxy;
      return;
    }
    catch (Throwable paramProxy)
    {
      cy.a(paramProxy, "HttpUtil", "HttpUtil");
    }
  }
  
  /* Error */
  private eg a(HttpURLConnection paramHttpURLConnection)
    throws ck, IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 4
    //   6: aconst_null
    //   7: astore 9
    //   9: aload_1
    //   10: invokevirtual 98	java/net/HttpURLConnection:getHeaderFields	()Ljava/util/Map;
    //   13: astore 12
    //   15: aload_1
    //   16: invokevirtual 102	java/net/HttpURLConnection:getResponseCode	()I
    //   19: istore_2
    //   20: iload_2
    //   21: sipush 200
    //   24: if_icmpne +293 -> 317
    //   27: new 104	java/io/ByteArrayOutputStream
    //   30: dup
    //   31: invokespecial 105	java/io/ByteArrayOutputStream:<init>	()V
    //   34: astore 6
    //   36: aload_1
    //   37: invokevirtual 109	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
    //   40: astore 5
    //   42: new 111	java/io/PushbackInputStream
    //   45: dup
    //   46: aload 5
    //   48: iconst_2
    //   49: invokespecial 114	java/io/PushbackInputStream:<init>	(Ljava/io/InputStream;I)V
    //   52: astore_3
    //   53: aload 4
    //   55: astore 9
    //   57: iconst_2
    //   58: newarray <illegal type>
    //   60: astore 7
    //   62: aload 4
    //   64: astore 9
    //   66: aload_3
    //   67: aload 7
    //   69: invokevirtual 118	java/io/PushbackInputStream:read	([B)I
    //   72: pop
    //   73: aload 4
    //   75: astore 9
    //   77: aload_3
    //   78: aload 7
    //   80: invokevirtual 122	java/io/PushbackInputStream:unread	([B)V
    //   83: aload 7
    //   85: iconst_0
    //   86: baload
    //   87: istore_2
    //   88: iload_2
    //   89: bipush 31
    //   91: if_icmpeq +404 -> 495
    //   94: aload_3
    //   95: astore 4
    //   97: aload_3
    //   98: astore 7
    //   100: aload 4
    //   102: astore 10
    //   104: aload 5
    //   106: astore 8
    //   108: aload 6
    //   110: astore 11
    //   112: aload 4
    //   114: astore 9
    //   116: sipush 1024
    //   119: newarray <illegal type>
    //   121: astore 13
    //   123: aload_3
    //   124: astore 7
    //   126: aload 4
    //   128: astore 10
    //   130: aload 5
    //   132: astore 8
    //   134: aload 6
    //   136: astore 11
    //   138: aload 4
    //   140: astore 9
    //   142: aload 4
    //   144: aload 13
    //   146: invokevirtual 125	java/io/InputStream:read	([B)I
    //   149: istore_2
    //   150: iload_2
    //   151: iconst_m1
    //   152: if_icmpne +369 -> 521
    //   155: aload_3
    //   156: astore 7
    //   158: aload 4
    //   160: astore 10
    //   162: aload 5
    //   164: astore 8
    //   166: aload 6
    //   168: astore 11
    //   170: aload 4
    //   172: astore 9
    //   174: getstatic 127	com/amap/api/mapcore2d/eb:a	Lcom/amap/api/mapcore2d/ec;
    //   177: ifnonnull +375 -> 552
    //   180: aload_3
    //   181: astore 7
    //   183: aload 4
    //   185: astore 10
    //   187: aload 5
    //   189: astore 8
    //   191: aload 6
    //   193: astore 11
    //   195: aload 4
    //   197: astore 9
    //   199: new 129	com/amap/api/mapcore2d/eg
    //   202: dup
    //   203: invokespecial 130	com/amap/api/mapcore2d/eg:<init>	()V
    //   206: astore 13
    //   208: aload_3
    //   209: astore 7
    //   211: aload 4
    //   213: astore 10
    //   215: aload 5
    //   217: astore 8
    //   219: aload 6
    //   221: astore 11
    //   223: aload 4
    //   225: astore 9
    //   227: aload 13
    //   229: aload 6
    //   231: invokevirtual 134	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   234: putfield 137	com/amap/api/mapcore2d/eg:a	[B
    //   237: aload_3
    //   238: astore 7
    //   240: aload 4
    //   242: astore 10
    //   244: aload 5
    //   246: astore 8
    //   248: aload 6
    //   250: astore 11
    //   252: aload 4
    //   254: astore 9
    //   256: aload 13
    //   258: aload 12
    //   260: putfield 140	com/amap/api/mapcore2d/eg:b	Ljava/util/Map;
    //   263: aload_3
    //   264: astore 7
    //   266: aload 4
    //   268: astore 10
    //   270: aload 5
    //   272: astore 8
    //   274: aload 6
    //   276: astore 11
    //   278: aload 4
    //   280: astore 9
    //   282: aload 13
    //   284: aload_0
    //   285: getfield 142	com/amap/api/mapcore2d/eb:j	Ljava/lang/String;
    //   288: putfield 144	com/amap/api/mapcore2d/eg:c	Ljava/lang/String;
    //   291: aload 6
    //   293: ifnonnull +289 -> 582
    //   296: aload 5
    //   298: ifnonnull +306 -> 604
    //   301: aload_3
    //   302: ifnonnull +324 -> 626
    //   305: aload 4
    //   307: ifnonnull +338 -> 645
    //   310: aload_1
    //   311: ifnonnull +354 -> 665
    //   314: aload 13
    //   316: areturn
    //   317: aload 12
    //   319: ifnonnull +133 -> 452
    //   322: ldc -110
    //   324: astore_3
    //   325: new 90	com/amap/api/mapcore2d/ck
    //   328: dup
    //   329: new 148	java/lang/StringBuilder
    //   332: dup
    //   333: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   336: ldc -105
    //   338: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   341: aload_1
    //   342: invokevirtual 159	java/net/HttpURLConnection:getResponseMessage	()Ljava/lang/String;
    //   345: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   348: ldc -95
    //   350: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   353: iload_2
    //   354: invokevirtual 164	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   357: ldc -90
    //   359: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   362: aload_3
    //   363: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   366: ldc -88
    //   368: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   371: aload_0
    //   372: getfield 142	com/amap/api/mapcore2d/eb:j	Ljava/lang/String;
    //   375: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   378: invokevirtual 171	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   381: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   384: athrow
    //   385: astore 4
    //   387: aconst_null
    //   388: astore_3
    //   389: aconst_null
    //   390: astore 5
    //   392: aconst_null
    //   393: astore 6
    //   395: aload_3
    //   396: astore 7
    //   398: aload 9
    //   400: astore 10
    //   402: aload 5
    //   404: astore 8
    //   406: aload 6
    //   408: astore 11
    //   410: aload 4
    //   412: athrow
    //   413: astore_3
    //   414: aload 10
    //   416: astore 4
    //   418: aload 11
    //   420: astore 6
    //   422: aload 8
    //   424: astore 5
    //   426: aload 6
    //   428: ifnonnull +256 -> 684
    //   431: aload 5
    //   433: ifnonnull +273 -> 706
    //   436: aload 7
    //   438: ifnonnull +290 -> 728
    //   441: aload 4
    //   443: ifnonnull +307 -> 750
    //   446: aload_1
    //   447: ifnonnull +325 -> 772
    //   450: aload_3
    //   451: athrow
    //   452: aload 12
    //   454: ldc -80
    //   456: invokeinterface 182 2 0
    //   461: checkcast 184	java/util/List
    //   464: astore_3
    //   465: aload_3
    //   466: ifnonnull +6 -> 472
    //   469: goto +383 -> 852
    //   472: aload_3
    //   473: invokeinterface 187 1 0
    //   478: ifle +374 -> 852
    //   481: aload_3
    //   482: iconst_0
    //   483: invokeinterface 190 2 0
    //   488: checkcast 192	java/lang/String
    //   491: astore_3
    //   492: goto -167 -> 325
    //   495: aload 7
    //   497: iconst_1
    //   498: baload
    //   499: bipush -117
    //   501: if_icmpne -407 -> 94
    //   504: aload 4
    //   506: astore 9
    //   508: new 194	java/util/zip/GZIPInputStream
    //   511: dup
    //   512: aload_3
    //   513: invokespecial 197	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
    //   516: astore 4
    //   518: goto -421 -> 97
    //   521: aload_3
    //   522: astore 7
    //   524: aload 4
    //   526: astore 10
    //   528: aload 5
    //   530: astore 8
    //   532: aload 6
    //   534: astore 11
    //   536: aload 4
    //   538: astore 9
    //   540: aload 6
    //   542: aload 13
    //   544: iconst_0
    //   545: iload_2
    //   546: invokevirtual 201	java/io/ByteArrayOutputStream:write	([BII)V
    //   549: goto -426 -> 123
    //   552: aload_3
    //   553: astore 7
    //   555: aload 4
    //   557: astore 10
    //   559: aload 5
    //   561: astore 8
    //   563: aload 6
    //   565: astore 11
    //   567: aload 4
    //   569: astore 9
    //   571: getstatic 127	com/amap/api/mapcore2d/eb:a	Lcom/amap/api/mapcore2d/ec;
    //   574: invokeinterface 204 1 0
    //   579: goto -399 -> 180
    //   582: aload 6
    //   584: invokevirtual 207	java/io/ByteArrayOutputStream:close	()V
    //   587: goto -291 -> 296
    //   590: astore 6
    //   592: aload 6
    //   594: ldc -47
    //   596: ldc -45
    //   598: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   601: goto -305 -> 296
    //   604: aload 5
    //   606: invokevirtual 212	java/io/InputStream:close	()V
    //   609: goto -308 -> 301
    //   612: astore 5
    //   614: aload 5
    //   616: ldc -47
    //   618: ldc -45
    //   620: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   623: goto -322 -> 301
    //   626: aload_3
    //   627: invokevirtual 213	java/io/PushbackInputStream:close	()V
    //   630: goto -325 -> 305
    //   633: astore_3
    //   634: aload_3
    //   635: ldc -47
    //   637: ldc -45
    //   639: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   642: goto -337 -> 305
    //   645: aload 4
    //   647: invokevirtual 212	java/io/InputStream:close	()V
    //   650: goto -340 -> 310
    //   653: astore_3
    //   654: aload_3
    //   655: ldc -47
    //   657: ldc -45
    //   659: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   662: goto -352 -> 310
    //   665: aload_1
    //   666: invokevirtual 216	java/net/HttpURLConnection:disconnect	()V
    //   669: aload 13
    //   671: areturn
    //   672: astore_1
    //   673: aload_1
    //   674: ldc -47
    //   676: ldc -45
    //   678: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   681: aload 13
    //   683: areturn
    //   684: aload 6
    //   686: invokevirtual 207	java/io/ByteArrayOutputStream:close	()V
    //   689: goto -258 -> 431
    //   692: astore 6
    //   694: aload 6
    //   696: ldc -47
    //   698: ldc -45
    //   700: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   703: goto -272 -> 431
    //   706: aload 5
    //   708: invokevirtual 212	java/io/InputStream:close	()V
    //   711: goto -275 -> 436
    //   714: astore 5
    //   716: aload 5
    //   718: ldc -47
    //   720: ldc -45
    //   722: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   725: goto -289 -> 436
    //   728: aload 7
    //   730: invokevirtual 213	java/io/PushbackInputStream:close	()V
    //   733: goto -292 -> 441
    //   736: astore 5
    //   738: aload 5
    //   740: ldc -47
    //   742: ldc -45
    //   744: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   747: goto -306 -> 441
    //   750: aload 4
    //   752: invokevirtual 212	java/io/InputStream:close	()V
    //   755: goto -309 -> 446
    //   758: astore 4
    //   760: aload 4
    //   762: ldc -47
    //   764: ldc -45
    //   766: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   769: goto -323 -> 446
    //   772: aload_1
    //   773: invokevirtual 216	java/net/HttpURLConnection:disconnect	()V
    //   776: goto -326 -> 450
    //   779: astore_1
    //   780: aload_1
    //   781: ldc -47
    //   783: ldc -45
    //   785: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   788: goto -338 -> 450
    //   791: astore_3
    //   792: aconst_null
    //   793: astore 4
    //   795: aconst_null
    //   796: astore 5
    //   798: aconst_null
    //   799: astore 6
    //   801: goto -375 -> 426
    //   804: astore_3
    //   805: aconst_null
    //   806: astore 4
    //   808: aconst_null
    //   809: astore 5
    //   811: goto -385 -> 426
    //   814: astore_3
    //   815: aconst_null
    //   816: astore 4
    //   818: goto -392 -> 426
    //   821: astore 8
    //   823: aconst_null
    //   824: astore 4
    //   826: aload_3
    //   827: astore 7
    //   829: aload 8
    //   831: astore_3
    //   832: goto -406 -> 426
    //   835: astore 4
    //   837: aconst_null
    //   838: astore_3
    //   839: aconst_null
    //   840: astore 5
    //   842: goto -447 -> 395
    //   845: astore 4
    //   847: aconst_null
    //   848: astore_3
    //   849: goto -454 -> 395
    //   852: ldc -110
    //   854: astore_3
    //   855: goto -530 -> 325
    //   858: astore 4
    //   860: goto -465 -> 395
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	863	0	this	eb
    //   0	863	1	paramHttpURLConnection	HttpURLConnection
    //   19	527	2	m	int
    //   52	344	3	localObject1	Object
    //   413	38	3	localObject2	Object
    //   464	163	3	localObject3	Object
    //   633	2	3	localThrowable1	Throwable
    //   653	2	3	localThrowable2	Throwable
    //   791	1	3	localObject4	Object
    //   804	1	3	localObject5	Object
    //   814	13	3	localObject6	Object
    //   831	24	3	localObject7	Object
    //   4	302	4	localObject8	Object
    //   385	26	4	localIOException1	IOException
    //   416	335	4	localObject9	Object
    //   758	3	4	localThrowable3	Throwable
    //   793	32	4	localObject10	Object
    //   835	1	4	localIOException2	IOException
    //   845	1	4	localIOException3	IOException
    //   858	1	4	localIOException4	IOException
    //   40	565	5	localObject11	Object
    //   612	95	5	localThrowable4	Throwable
    //   714	3	5	localThrowable5	Throwable
    //   736	3	5	localThrowable6	Throwable
    //   796	45	5	localObject12	Object
    //   34	549	6	localObject13	Object
    //   590	95	6	localThrowable7	Throwable
    //   692	3	6	localThrowable8	Throwable
    //   799	1	6	localObject14	Object
    //   1	827	7	localObject15	Object
    //   106	456	8	localObject16	Object
    //   821	9	8	localObject17	Object
    //   7	563	9	localObject18	Object
    //   102	456	10	localObject19	Object
    //   110	456	11	localObject20	Object
    //   13	440	12	localMap	Map
    //   121	561	13	localObject21	Object
    // Exception table:
    //   from	to	target	type
    //   9	20	385	java/io/IOException
    //   27	36	385	java/io/IOException
    //   325	385	385	java/io/IOException
    //   452	465	385	java/io/IOException
    //   472	492	385	java/io/IOException
    //   116	123	413	finally
    //   142	150	413	finally
    //   174	180	413	finally
    //   199	208	413	finally
    //   227	237	413	finally
    //   256	263	413	finally
    //   282	291	413	finally
    //   410	413	413	finally
    //   540	549	413	finally
    //   571	579	413	finally
    //   582	587	590	java/lang/Throwable
    //   604	609	612	java/lang/Throwable
    //   626	630	633	java/lang/Throwable
    //   645	650	653	java/lang/Throwable
    //   665	669	672	java/lang/Throwable
    //   684	689	692	java/lang/Throwable
    //   706	711	714	java/lang/Throwable
    //   728	733	736	java/lang/Throwable
    //   750	755	758	java/lang/Throwable
    //   772	776	779	java/lang/Throwable
    //   9	20	791	finally
    //   27	36	791	finally
    //   325	385	791	finally
    //   452	465	791	finally
    //   472	492	791	finally
    //   36	42	804	finally
    //   42	53	814	finally
    //   57	62	821	finally
    //   66	73	821	finally
    //   77	83	821	finally
    //   508	518	821	finally
    //   36	42	835	java/io/IOException
    //   42	53	845	java/io/IOException
    //   57	62	858	java/io/IOException
    //   66	73	858	java/io/IOException
    //   77	83	858	java/io/IOException
    //   116	123	858	java/io/IOException
    //   142	150	858	java/io/IOException
    //   174	180	858	java/io/IOException
    //   199	208	858	java/io/IOException
    //   227	237	858	java/io/IOException
    //   256	263	858	java/io/IOException
    //   282	291	858	java/io/IOException
    //   508	518	858	java/io/IOException
    //   540	549	858	java/io/IOException
    //   571	579	858	java/io/IOException
  }
  
  static String a(Map<String, String> paramMap)
  {
    if (paramMap == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = paramMap.entrySet().iterator();
    if (!localIterator.hasNext()) {
      return localStringBuilder.toString();
    }
    paramMap = (Map.Entry)localIterator.next();
    String str = (String)paramMap.getKey();
    paramMap = (String)paramMap.getValue();
    if (paramMap != null) {
      label74:
      if (localStringBuilder.length() > 0) {
        break label116;
      }
    }
    for (;;)
    {
      localStringBuilder.append(URLEncoder.encode(str));
      localStringBuilder.append("=");
      localStringBuilder.append(URLEncoder.encode(paramMap));
      break;
      paramMap = "";
      break label74;
      label116:
      localStringBuilder.append("&");
    }
  }
  
  private void a()
  {
    try
    {
      this.j = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
      return;
    }
    catch (Throwable localThrowable)
    {
      cy.a(localThrowable, "HttpUrlUtil", "initCSID");
    }
  }
  
  public static void a(ec paramec)
  {
    a = paramec;
  }
  
  private void a(Map<String, String> paramMap, HttpURLConnection paramHttpURLConnection)
  {
    if (paramMap == null) {}
    try
    {
      paramHttpURLConnection.addRequestProperty("csid", this.j);
      paramHttpURLConnection.setConnectTimeout(this.b);
      paramHttpURLConnection.setReadTimeout(this.c);
      return;
      Iterator localIterator = paramMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        paramHttpURLConnection.addRequestProperty(str, (String)paramMap.get(str));
      }
    }
    catch (Throwable paramMap)
    {
      for (;;)
      {
        cy.a(paramMap, "HttpUrlUtil", "addHeaders");
      }
    }
  }
  
  /* Error */
  eg a(String paramString, Map<String, String> paramMap1, Map<String, String> paramMap2)
    throws ck
  {
    // Byte code:
    //   0: aload_3
    //   1: invokestatic 315	com/amap/api/mapcore2d/eb:a	(Ljava/util/Map;)Ljava/lang/String;
    //   4: astore_3
    //   5: new 317	java/lang/StringBuffer
    //   8: dup
    //   9: invokespecial 318	java/lang/StringBuffer:<init>	()V
    //   12: astore 4
    //   14: aload 4
    //   16: aload_1
    //   17: invokevirtual 321	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   20: pop
    //   21: aload_3
    //   22: ifnonnull +25 -> 47
    //   25: aload_0
    //   26: aload 4
    //   28: invokevirtual 322	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   31: aload_2
    //   32: iconst_0
    //   33: invokevirtual 325	com/amap/api/mapcore2d/eb:a	(Ljava/lang/String;Ljava/util/Map;Z)Ljava/net/HttpURLConnection;
    //   36: astore_1
    //   37: aload_1
    //   38: invokevirtual 328	java/net/HttpURLConnection:connect	()V
    //   41: aload_0
    //   42: aload_1
    //   43: invokespecial 330	com/amap/api/mapcore2d/eb:a	(Ljava/net/HttpURLConnection;)Lcom/amap/api/mapcore2d/eg;
    //   46: areturn
    //   47: aload 4
    //   49: ldc_w 332
    //   52: invokevirtual 321	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   55: aload_3
    //   56: invokevirtual 321	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   59: pop
    //   60: goto -35 -> 25
    //   63: astore_1
    //   64: new 90	com/amap/api/mapcore2d/ck
    //   67: dup
    //   68: ldc_w 334
    //   71: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   74: athrow
    //   75: astore_1
    //   76: new 90	com/amap/api/mapcore2d/ck
    //   79: dup
    //   80: ldc_w 336
    //   83: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   86: athrow
    //   87: astore_1
    //   88: new 90	com/amap/api/mapcore2d/ck
    //   91: dup
    //   92: ldc_w 338
    //   95: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   98: athrow
    //   99: astore_1
    //   100: new 90	com/amap/api/mapcore2d/ck
    //   103: dup
    //   104: ldc_w 340
    //   107: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   110: athrow
    //   111: astore_1
    //   112: new 90	com/amap/api/mapcore2d/ck
    //   115: dup
    //   116: ldc_w 342
    //   119: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   122: athrow
    //   123: astore_1
    //   124: new 90	com/amap/api/mapcore2d/ck
    //   127: dup
    //   128: ldc_w 344
    //   131: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   134: athrow
    //   135: astore_1
    //   136: new 90	com/amap/api/mapcore2d/ck
    //   139: dup
    //   140: ldc_w 346
    //   143: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   146: athrow
    //   147: astore_1
    //   148: aload_1
    //   149: athrow
    //   150: astore_1
    //   151: aload_1
    //   152: invokevirtual 349	java/lang/Throwable:printStackTrace	()V
    //   155: new 90	com/amap/api/mapcore2d/ck
    //   158: dup
    //   159: ldc_w 344
    //   162: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   165: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	166	0	this	eb
    //   0	166	1	paramString	String
    //   0	166	2	paramMap1	Map<String, String>
    //   0	166	3	paramMap2	Map<String, String>
    //   12	36	4	localStringBuffer	StringBuffer
    // Exception table:
    //   from	to	target	type
    //   0	21	63	java/net/ConnectException
    //   25	47	63	java/net/ConnectException
    //   47	60	63	java/net/ConnectException
    //   0	21	75	java/net/MalformedURLException
    //   25	47	75	java/net/MalformedURLException
    //   47	60	75	java/net/MalformedURLException
    //   0	21	87	java/net/UnknownHostException
    //   25	47	87	java/net/UnknownHostException
    //   47	60	87	java/net/UnknownHostException
    //   0	21	99	java/net/SocketException
    //   25	47	99	java/net/SocketException
    //   47	60	99	java/net/SocketException
    //   0	21	111	java/net/SocketTimeoutException
    //   25	47	111	java/net/SocketTimeoutException
    //   47	60	111	java/net/SocketTimeoutException
    //   0	21	123	java/io/InterruptedIOException
    //   25	47	123	java/io/InterruptedIOException
    //   47	60	123	java/io/InterruptedIOException
    //   0	21	135	java/io/IOException
    //   25	47	135	java/io/IOException
    //   47	60	135	java/io/IOException
    //   0	21	147	com/amap/api/mapcore2d/ck
    //   25	47	147	com/amap/api/mapcore2d/ck
    //   47	60	147	com/amap/api/mapcore2d/ck
    //   0	21	150	java/lang/Throwable
    //   25	47	150	java/lang/Throwable
    //   47	60	150	java/lang/Throwable
  }
  
  /* Error */
  eg a(String paramString, Map<String, String> paramMap, byte[] paramArrayOfByte)
    throws ck
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: aload_2
    //   3: iconst_1
    //   4: invokevirtual 325	com/amap/api/mapcore2d/eb:a	(Ljava/lang/String;Ljava/util/Map;Z)Ljava/net/HttpURLConnection;
    //   7: astore_1
    //   8: aload_3
    //   9: ifnonnull +13 -> 22
    //   12: aload_1
    //   13: invokevirtual 328	java/net/HttpURLConnection:connect	()V
    //   16: aload_0
    //   17: aload_1
    //   18: invokespecial 330	com/amap/api/mapcore2d/eb:a	(Ljava/net/HttpURLConnection;)Lcom/amap/api/mapcore2d/eg;
    //   21: areturn
    //   22: aload_3
    //   23: arraylength
    //   24: ifle -12 -> 12
    //   27: new 353	java/io/DataOutputStream
    //   30: dup
    //   31: aload_1
    //   32: invokevirtual 357	java/net/HttpURLConnection:getOutputStream	()Ljava/io/OutputStream;
    //   35: invokespecial 360	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   38: astore_2
    //   39: aload_2
    //   40: aload_3
    //   41: invokevirtual 362	java/io/DataOutputStream:write	([B)V
    //   44: aload_2
    //   45: invokevirtual 363	java/io/DataOutputStream:close	()V
    //   48: goto -36 -> 12
    //   51: astore_1
    //   52: aload_1
    //   53: invokevirtual 364	java/net/ConnectException:printStackTrace	()V
    //   56: new 90	com/amap/api/mapcore2d/ck
    //   59: dup
    //   60: ldc_w 334
    //   63: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   66: athrow
    //   67: astore_1
    //   68: aload_1
    //   69: invokevirtual 365	java/net/MalformedURLException:printStackTrace	()V
    //   72: new 90	com/amap/api/mapcore2d/ck
    //   75: dup
    //   76: ldc_w 336
    //   79: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   82: athrow
    //   83: astore_1
    //   84: aload_1
    //   85: invokevirtual 366	java/net/UnknownHostException:printStackTrace	()V
    //   88: new 90	com/amap/api/mapcore2d/ck
    //   91: dup
    //   92: ldc_w 338
    //   95: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   98: athrow
    //   99: astore_1
    //   100: aload_1
    //   101: invokevirtual 367	java/net/SocketException:printStackTrace	()V
    //   104: new 90	com/amap/api/mapcore2d/ck
    //   107: dup
    //   108: ldc_w 340
    //   111: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   114: athrow
    //   115: astore_1
    //   116: aload_1
    //   117: invokevirtual 368	java/net/SocketTimeoutException:printStackTrace	()V
    //   120: new 90	com/amap/api/mapcore2d/ck
    //   123: dup
    //   124: ldc_w 342
    //   127: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   130: athrow
    //   131: astore_1
    //   132: new 90	com/amap/api/mapcore2d/ck
    //   135: dup
    //   136: ldc_w 344
    //   139: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   142: athrow
    //   143: astore_1
    //   144: aload_1
    //   145: invokevirtual 369	java/io/IOException:printStackTrace	()V
    //   148: new 90	com/amap/api/mapcore2d/ck
    //   151: dup
    //   152: ldc_w 346
    //   155: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   158: athrow
    //   159: astore_1
    //   160: aload_1
    //   161: ldc -47
    //   163: ldc_w 371
    //   166: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   169: aload_1
    //   170: athrow
    //   171: astore_1
    //   172: aload_1
    //   173: ldc -47
    //   175: ldc_w 371
    //   178: invokestatic 87	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   181: new 90	com/amap/api/mapcore2d/ck
    //   184: dup
    //   185: ldc_w 344
    //   188: invokespecial 174	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   191: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	192	0	this	eb
    //   0	192	1	paramString	String
    //   0	192	2	paramMap	Map<String, String>
    //   0	192	3	paramArrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   0	8	51	java/net/ConnectException
    //   12	22	51	java/net/ConnectException
    //   22	48	51	java/net/ConnectException
    //   0	8	67	java/net/MalformedURLException
    //   12	22	67	java/net/MalformedURLException
    //   22	48	67	java/net/MalformedURLException
    //   0	8	83	java/net/UnknownHostException
    //   12	22	83	java/net/UnknownHostException
    //   22	48	83	java/net/UnknownHostException
    //   0	8	99	java/net/SocketException
    //   12	22	99	java/net/SocketException
    //   22	48	99	java/net/SocketException
    //   0	8	115	java/net/SocketTimeoutException
    //   12	22	115	java/net/SocketTimeoutException
    //   22	48	115	java/net/SocketTimeoutException
    //   0	8	131	java/io/InterruptedIOException
    //   12	22	131	java/io/InterruptedIOException
    //   22	48	131	java/io/InterruptedIOException
    //   0	8	143	java/io/IOException
    //   12	22	143	java/io/IOException
    //   22	48	143	java/io/IOException
    //   0	8	159	com/amap/api/mapcore2d/ck
    //   12	22	159	com/amap/api/mapcore2d/ck
    //   22	48	159	com/amap/api/mapcore2d/ck
    //   0	8	171	java/lang/Throwable
    //   12	22	171	java/lang/Throwable
    //   22	48	171	java/lang/Throwable
  }
  
  HttpURLConnection a(String paramString, Map<String, String> paramMap, boolean paramBoolean)
    throws IOException
  {
    Object localObject = null;
    cp.a();
    URL localURL = new URL(paramString);
    if (this.k == null)
    {
      paramString = (String)localObject;
      if (paramString == null) {
        break label91;
      }
      label30:
      if (this.d) {
        break label120;
      }
      paramString = (HttpURLConnection)paramString;
      label42:
      if (Build.VERSION.SDK != null) {
        break label153;
      }
    }
    for (;;)
    {
      a(paramMap, paramString);
      if (paramBoolean) {
        break label174;
      }
      paramString.setRequestMethod("GET");
      paramString.setDoInput(true);
      return paramString;
      paramString = this.k.a(this.f, localURL);
      break;
      label91:
      if (this.f == null)
      {
        paramString = localURL.openConnection();
        break label30;
      }
      paramString = localURL.openConnection(this.f);
      break label30;
      label120:
      paramString = (HttpsURLConnection)paramString;
      ((HttpsURLConnection)paramString).setSSLSocketFactory(this.e.getSocketFactory());
      ((HttpsURLConnection)paramString).setHostnameVerifier(this.l);
      break label42;
      label153:
      if (Build.VERSION.SDK_INT > 13) {
        paramString.setRequestProperty("Connection", "close");
      }
    }
    label174:
    paramString.setRequestMethod("POST");
    paramString.setUseCaches(false);
    paramString.setDoInput(true);
    paramString.setDoOutput(true);
    return paramString;
  }
  
  void a(long paramLong)
  {
    this.i = paramLong;
  }
  
  void a(String paramString, Map<String, String> paramMap1, Map<String, String> paramMap2, ea.a parama)
  {
    Object localObject5 = null;
    byte[] arrayOfByte = null;
    Object localObject7 = null;
    Object localObject6 = null;
    Object localObject2;
    Object localObject4;
    Object localObject1;
    Object localObject3;
    int i1;
    int m;
    if (parama != null)
    {
      localObject2 = localObject6;
      localObject4 = arrayOfByte;
      localObject1 = localObject7;
      localObject3 = localObject5;
      try
      {
        paramMap2 = a(paramMap2);
        localObject2 = localObject6;
        localObject4 = arrayOfByte;
        localObject1 = localObject7;
        localObject3 = localObject5;
        localStringBuffer = new StringBuffer();
        localObject2 = localObject6;
        localObject4 = arrayOfByte;
        localObject1 = localObject7;
        localObject3 = localObject5;
        localStringBuffer.append(paramString);
        if (paramMap2 != null) {
          break label316;
        }
        localObject2 = localObject6;
        localObject4 = arrayOfByte;
        localObject1 = localObject7;
        localObject3 = localObject5;
        paramString = a(localStringBuffer.toString(), paramMap1, false);
        localObject2 = paramString;
        localObject4 = arrayOfByte;
        localObject1 = paramString;
        localObject3 = localObject5;
        paramString.setRequestProperty("RANGE", "bytes=" + this.i + "-");
        localObject2 = paramString;
        localObject4 = arrayOfByte;
        localObject1 = paramString;
        localObject3 = localObject5;
        paramString.connect();
        localObject2 = paramString;
        localObject4 = arrayOfByte;
        localObject1 = paramString;
        localObject3 = localObject5;
        i1 = paramString.getResponseCode();
        if (i1 != 200) {
          break label392;
        }
        m = 0;
      }
      catch (Throwable paramString)
      {
        for (;;)
        {
          StringBuffer localStringBuffer;
          localObject1 = localObject2;
          localObject3 = localObject4;
          parama.a(paramString);
          if (localObject4 != null) {
            break;
          }
          if (localObject2 == null) {
            break label913;
          }
          try
          {
            ((HttpURLConnection)localObject2).disconnect();
            return;
          }
          catch (Throwable paramString)
          {
            cy.a(paramString, "HttpUrlUtil", "makeDownloadGetRequest");
            return;
          }
          m = 1;
          break label883;
          n = 1;
          break label894;
          localObject2 = paramString;
          localObject4 = arrayOfByte;
          localObject1 = paramString;
          localObject3 = localObject5;
          parama.a(new ck("网络异常原因：" + paramString.getResponseMessage() + " 网络异常状态码：" + i1));
        }
      }
      finally
      {
        if (localObject3 != null) {
          break label828;
        }
      }
      localObject2 = paramString;
      localObject4 = arrayOfByte;
      localObject1 = paramString;
      localObject3 = localObject5;
      paramMap1 = paramString.getInputStream();
      localObject2 = paramString;
      localObject4 = paramMap1;
      localObject1 = paramString;
      localObject3 = paramMap1;
      paramMap2 = new byte['Ѐ'];
      localObject2 = paramString;
      localObject4 = paramMap1;
      localObject1 = paramString;
      localObject3 = paramMap1;
      if (!Thread.interrupted()) {}
    }
    label316:
    label370:
    label392:
    label398:
    label404:
    label473:
    label478:
    label745:
    label828:
    label862:
    label883:
    label894:
    label905:
    label913:
    label915:
    label923:
    label927:
    for (;;)
    {
      localObject2 = paramString;
      localObject4 = paramMap1;
      localObject1 = paramString;
      localObject3 = paramMap1;
      int n;
      if (!this.g)
      {
        localObject2 = paramString;
        localObject4 = paramMap1;
        localObject1 = paramString;
        localObject3 = paramMap1;
        parama.c();
        break label905;
        localObject2 = localObject6;
        localObject4 = arrayOfByte;
        localObject1 = localObject7;
        localObject3 = localObject5;
        localStringBuffer.append("?").append(paramMap2);
        break;
        if (localObject1 != null) {
          break label862;
        }
        throw paramString;
        localObject2 = paramString;
        localObject4 = paramMap1;
        localObject1 = paramString;
        localObject3 = paramMap1;
        if (this.g) {
          continue;
        }
        localObject2 = paramString;
        localObject4 = paramMap1;
        localObject1 = paramString;
        localObject3 = paramMap1;
        n = paramMap1.read(paramMap2, 0, 1024);
        if (n <= 0) {
          continue;
        }
        localObject2 = paramString;
        localObject4 = paramMap1;
        localObject1 = paramString;
        localObject3 = paramMap1;
        if (this.h != -1L)
        {
          localObject2 = paramString;
          localObject4 = paramMap1;
          localObject1 = paramString;
          localObject3 = paramMap1;
          if (this.i < this.h) {
            break label923;
          }
          m = 1;
          break label915;
        }
        if (n != 1024)
        {
          localObject2 = paramString;
          localObject4 = paramMap1;
          localObject1 = paramString;
          localObject3 = paramMap1;
          arrayOfByte = new byte[n];
          localObject2 = paramString;
          localObject4 = paramMap1;
          localObject1 = paramString;
          localObject3 = paramMap1;
          System.arraycopy(paramMap2, 0, arrayOfByte, 0, n);
          localObject2 = paramString;
          localObject4 = paramMap1;
          localObject1 = paramString;
          localObject3 = paramMap1;
          parama.a(arrayOfByte, this.i);
        }
        for (;;)
        {
          localObject2 = paramString;
          localObject4 = paramMap1;
          localObject1 = paramString;
          localObject3 = paramMap1;
          long l1 = this.i;
          localObject2 = paramString;
          localObject4 = paramMap1;
          localObject1 = paramString;
          localObject3 = paramMap1;
          this.i = (n + l1);
          break;
          localObject2 = paramString;
          localObject4 = paramMap1;
          localObject1 = paramString;
          localObject3 = paramMap1;
          parama.a(paramMap2, this.i);
        }
      }
      localObject2 = paramString;
      localObject4 = paramMap1;
      localObject1 = paramString;
      localObject3 = paramMap1;
      parama.b();
      break label905;
      try
      {
        paramMap1.close();
      }
      catch (IOException paramMap1)
      {
        cy.a(paramMap1, "HttpUrlUtil", "makeDownloadGetRequest");
      }
      catch (Throwable paramMap1)
      {
        cy.a(paramMap1, "HttpUrlUtil", "makeDownloadGetRequest");
      }
      while (paramString != null)
      {
        try
        {
          paramString.disconnect();
          return;
        }
        catch (Throwable paramString)
        {
          cy.a(paramString, "HttpUrlUtil", "makeDownloadGetRequest");
          return;
        }
        try
        {
          ((InputStream)localObject4).close();
        }
        catch (IOException paramString)
        {
          cy.a(paramString, "HttpUrlUtil", "makeDownloadGetRequest");
        }
        catch (Throwable paramString)
        {
          cy.a(paramString, "HttpUrlUtil", "makeDownloadGetRequest");
        }
        break label370;
        try
        {
          ((InputStream)localObject3).close();
        }
        catch (IOException paramMap1)
        {
          cy.a(paramMap1, "HttpUrlUtil", "makeDownloadGetRequest");
        }
        catch (Throwable paramMap1)
        {
          cy.a(paramMap1, "HttpUrlUtil", "makeDownloadGetRequest");
        }
        break label473;
        try
        {
          ((HttpURLConnection)localObject1).disconnect();
        }
        catch (Throwable paramMap1)
        {
          cy.a(paramMap1, "HttpUrlUtil", "makeDownloadGetRequest");
        }
        break label478;
        if (i1 != 206) {
          break label398;
        }
        n = 0;
        if ((n & m) != 0) {
          break label404;
        }
        break;
        if (paramMap1 != null) {
          break label745;
        }
      }
      return;
      return;
      for (;;)
      {
        if (m != 0) {
          break label927;
        }
        break;
        m = 0;
      }
    }
  }
  
  void b(long paramLong)
  {
    this.h = paramLong;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/eb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */