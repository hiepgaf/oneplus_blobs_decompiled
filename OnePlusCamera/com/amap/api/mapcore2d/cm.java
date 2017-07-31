package com.amap.api.mapcore2d;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class cm
{
  public static int a = -1;
  public static String b = "";
  
  /* Error */
  public static a a(Context paramContext, cu paramcu, String paramString, Map<String, String> paramMap)
  {
    // Byte code:
    //   0: new 6	com/amap/api/mapcore2d/cm$a
    //   3: dup
    //   4: invokespecial 41	com/amap/api/mapcore2d/cm$a:<init>	()V
    //   7: astore 7
    //   9: new 43	com/amap/api/mapcore2d/dy
    //   12: dup
    //   13: invokespecial 44	com/amap/api/mapcore2d/dy:<init>	()V
    //   16: astore 6
    //   18: aload 6
    //   20: new 20	com/amap/api/mapcore2d/cm$b
    //   23: dup
    //   24: aload_0
    //   25: aload_1
    //   26: aload_2
    //   27: aload_3
    //   28: invokespecial 47	com/amap/api/mapcore2d/cm$b:<init>	(Landroid/content/Context;Lcom/amap/api/mapcore2d/cu;Ljava/lang/String;Ljava/util/Map;)V
    //   31: iconst_1
    //   32: invokevirtual 50	com/amap/api/mapcore2d/dy:a	(Lcom/amap/api/mapcore2d/ee;Z)Lcom/amap/api/mapcore2d/eg;
    //   35: astore_2
    //   36: aload_2
    //   37: ifnonnull +181 -> 218
    //   40: aconst_null
    //   41: astore_1
    //   42: bipush 16
    //   44: newarray <illegal type>
    //   46: astore 6
    //   48: aload_1
    //   49: arraylength
    //   50: bipush 16
    //   52: isub
    //   53: newarray <illegal type>
    //   55: astore_3
    //   56: aload_1
    //   57: iconst_0
    //   58: aload 6
    //   60: iconst_0
    //   61: bipush 16
    //   63: invokestatic 56	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   66: aload_1
    //   67: bipush 16
    //   69: aload_3
    //   70: iconst_0
    //   71: aload_1
    //   72: arraylength
    //   73: bipush 16
    //   75: isub
    //   76: invokestatic 56	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   79: new 58	javax/crypto/spec/SecretKeySpec
    //   82: dup
    //   83: aload 6
    //   85: ldc 60
    //   87: invokespecial 63	javax/crypto/spec/SecretKeySpec:<init>	([BLjava/lang/String;)V
    //   90: astore 6
    //   92: ldc 65
    //   94: invokestatic 71	javax/crypto/Cipher:getInstance	(Ljava/lang/String;)Ljavax/crypto/Cipher;
    //   97: astore 8
    //   99: aload 8
    //   101: iconst_2
    //   102: aload 6
    //   104: new 73	javax/crypto/spec/IvParameterSpec
    //   107: dup
    //   108: invokestatic 78	com/amap/api/mapcore2d/cv:a	()[B
    //   111: invokespecial 81	javax/crypto/spec/IvParameterSpec:<init>	([B)V
    //   114: invokevirtual 85	javax/crypto/Cipher:init	(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V
    //   117: aload 8
    //   119: aload_3
    //   120: invokevirtual 89	javax/crypto/Cipher:doFinal	([B)[B
    //   123: invokestatic 92	com/amap/api/mapcore2d/cv:a	([B)Ljava/lang/String;
    //   126: astore 6
    //   128: aload_1
    //   129: astore_3
    //   130: aload 6
    //   132: astore_1
    //   133: aload_3
    //   134: ifnull +116 -> 250
    //   137: aload_1
    //   138: invokestatic 98	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   141: ifne +112 -> 253
    //   144: new 100	org/json/JSONObject
    //   147: dup
    //   148: aload_1
    //   149: invokespecial 103	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   152: astore 6
    //   154: aload 6
    //   156: ldc 105
    //   158: invokevirtual 109	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   161: istore 5
    //   163: iload 5
    //   165: ifne +96 -> 261
    //   168: aload 7
    //   170: areturn
    //   171: astore_1
    //   172: aload_1
    //   173: athrow
    //   174: astore_3
    //   175: aconst_null
    //   176: astore_2
    //   177: aconst_null
    //   178: astore_1
    //   179: aload 7
    //   181: aload_3
    //   182: invokevirtual 112	com/amap/api/mapcore2d/ck:a	()Ljava/lang/String;
    //   185: putfield 114	com/amap/api/mapcore2d/cm$a:a	Ljava/lang/String;
    //   188: aconst_null
    //   189: astore 6
    //   191: aload_1
    //   192: astore_3
    //   193: aload 6
    //   195: astore_1
    //   196: goto -63 -> 133
    //   199: astore_1
    //   200: new 34	com/amap/api/mapcore2d/ck
    //   203: dup
    //   204: ldc 116
    //   206: invokespecial 117	com/amap/api/mapcore2d/ck:<init>	(Ljava/lang/String;)V
    //   209: athrow
    //   210: astore_1
    //   211: aconst_null
    //   212: astore_2
    //   213: aconst_null
    //   214: astore_1
    //   215: goto -27 -> 188
    //   218: aload_2
    //   219: getfield 122	com/amap/api/mapcore2d/eg:a	[B
    //   222: astore_1
    //   223: goto -181 -> 42
    //   226: astore_3
    //   227: aconst_null
    //   228: astore_2
    //   229: aconst_null
    //   230: astore_1
    //   231: aload_3
    //   232: ldc 124
    //   234: ldc 126
    //   236: invokestatic 131	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   239: aconst_null
    //   240: astore 6
    //   242: aload_1
    //   243: astore_3
    //   244: aload 6
    //   246: astore_1
    //   247: goto -114 -> 133
    //   250: aload 7
    //   252: areturn
    //   253: aload_3
    //   254: invokestatic 92	com/amap/api/mapcore2d/cv:a	([B)Ljava/lang/String;
    //   257: astore_1
    //   258: goto -114 -> 144
    //   261: aload 6
    //   263: ldc 105
    //   265: invokevirtual 135	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   268: istore 4
    //   270: iload 4
    //   272: iconst_1
    //   273: if_icmpeq +35 -> 308
    //   276: iload 4
    //   278: ifeq +49 -> 327
    //   281: aload 6
    //   283: ldc -119
    //   285: invokevirtual 109	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   288: istore 5
    //   290: iload 5
    //   292: ifne +171 -> 463
    //   295: aload 6
    //   297: ldc -117
    //   299: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   302: ifne +188 -> 490
    //   305: aload 7
    //   307: areturn
    //   308: iconst_1
    //   309: putstatic 26	com/amap/api/mapcore2d/cm:a	I
    //   312: goto -31 -> 281
    //   315: astore_0
    //   316: aload_0
    //   317: ldc -112
    //   319: ldc 126
    //   321: invokestatic 131	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   324: aload 7
    //   326: areturn
    //   327: aload_2
    //   328: ifnonnull +51 -> 379
    //   331: ldc -110
    //   333: astore_2
    //   334: ldc -108
    //   336: astore_1
    //   337: aload_0
    //   338: aload_2
    //   339: aload_1
    //   340: aload 6
    //   342: invokevirtual 151	org/json/JSONObject:toString	()Ljava/lang/String;
    //   345: invokestatic 154	com/amap/api/mapcore2d/cv:a	(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   348: iconst_0
    //   349: putstatic 26	com/amap/api/mapcore2d/cm:a	I
    //   352: aload 6
    //   354: ldc -100
    //   356: invokevirtual 109	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   359: ifne +91 -> 450
    //   362: getstatic 26	com/amap/api/mapcore2d/cm:a	I
    //   365: ifne -84 -> 281
    //   368: aload 7
    //   370: getstatic 30	com/amap/api/mapcore2d/cm:b	Ljava/lang/String;
    //   373: putfield 114	com/amap/api/mapcore2d/cm$a:a	Ljava/lang/String;
    //   376: aload 7
    //   378: areturn
    //   379: aload_2
    //   380: getfield 158	com/amap/api/mapcore2d/eg:c	Ljava/lang/String;
    //   383: astore_1
    //   384: aload_2
    //   385: getfield 161	com/amap/api/mapcore2d/eg:b	Ljava/util/Map;
    //   388: ifnonnull +13 -> 401
    //   391: ldc -108
    //   393: astore_3
    //   394: aload_1
    //   395: astore_2
    //   396: aload_3
    //   397: astore_1
    //   398: goto -61 -> 337
    //   401: aload_2
    //   402: getfield 161	com/amap/api/mapcore2d/eg:b	Ljava/util/Map;
    //   405: ldc -93
    //   407: invokeinterface 169 2 0
    //   412: checkcast 171	java/util/List
    //   415: astore_2
    //   416: aload_2
    //   417: ifnonnull +6 -> 423
    //   420: goto +706 -> 1126
    //   423: aload_2
    //   424: invokeinterface 175 1 0
    //   429: ifle +697 -> 1126
    //   432: aload_2
    //   433: iconst_0
    //   434: invokeinterface 178 2 0
    //   439: checkcast 180	java/lang/String
    //   442: astore_3
    //   443: aload_1
    //   444: astore_2
    //   445: aload_3
    //   446: astore_1
    //   447: goto -110 -> 337
    //   450: aload 6
    //   452: ldc -100
    //   454: invokevirtual 184	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   457: putstatic 30	com/amap/api/mapcore2d/cm:b	Ljava/lang/String;
    //   460: goto -98 -> 362
    //   463: aload 7
    //   465: aload 6
    //   467: ldc -119
    //   469: invokevirtual 135	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   472: putfield 186	com/amap/api/mapcore2d/cm$a:b	I
    //   475: goto -180 -> 295
    //   478: astore_0
    //   479: aload_0
    //   480: ldc -112
    //   482: ldc -68
    //   484: invokestatic 131	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   487: goto -192 -> 295
    //   490: new 9	com/amap/api/mapcore2d/cm$a$a
    //   493: dup
    //   494: invokespecial 189	com/amap/api/mapcore2d/cm$a$a:<init>	()V
    //   497: astore_1
    //   498: aload_1
    //   499: iconst_0
    //   500: putfield 192	com/amap/api/mapcore2d/cm$a$a:a	Z
    //   503: aload_1
    //   504: iconst_0
    //   505: putfield 194	com/amap/api/mapcore2d/cm$a$a:b	Z
    //   508: aload 7
    //   510: aload_1
    //   511: putfield 198	com/amap/api/mapcore2d/cm$a:p	Lcom/amap/api/mapcore2d/cm$a$a;
    //   514: aload 6
    //   516: ldc -117
    //   518: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   521: astore_0
    //   522: aload_0
    //   523: ldc -52
    //   525: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   528: ifne +203 -> 731
    //   531: aload_0
    //   532: ldc -50
    //   534: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   537: ifne +229 -> 766
    //   540: aload_0
    //   541: ldc -48
    //   543: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   546: ifne +234 -> 780
    //   549: aload_0
    //   550: ldc -46
    //   552: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   555: ifne +239 -> 794
    //   558: aload_0
    //   559: ldc -44
    //   561: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   564: ifne +244 -> 808
    //   567: aload_0
    //   568: ldc -42
    //   570: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   573: ifne +249 -> 822
    //   576: aload_0
    //   577: ldc -40
    //   579: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   582: ifne +254 -> 836
    //   585: aload_0
    //   586: ldc -38
    //   588: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   591: ifne +259 -> 850
    //   594: aload_0
    //   595: ldc -36
    //   597: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   600: ifne +264 -> 864
    //   603: aload_0
    //   604: ldc -34
    //   606: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   609: ifne +269 -> 878
    //   612: aload_0
    //   613: ldc -32
    //   615: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   618: ifne +274 -> 892
    //   621: aload_0
    //   622: ldc -30
    //   624: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   627: ifne +294 -> 921
    //   630: aload_0
    //   631: ldc -28
    //   633: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   636: ifne +314 -> 950
    //   639: aload_0
    //   640: ldc -26
    //   642: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   645: ifne +319 -> 964
    //   648: aload_0
    //   649: ldc -24
    //   651: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   654: ifne +324 -> 978
    //   657: aload_0
    //   658: ldc -22
    //   660: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   663: ifne +344 -> 1007
    //   666: aload_0
    //   667: ldc -20
    //   669: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   672: ifne +349 -> 1021
    //   675: aload_0
    //   676: ldc -18
    //   678: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   681: ifne +354 -> 1035
    //   684: aload_0
    //   685: ldc -16
    //   687: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   690: ifne +374 -> 1064
    //   693: aload_0
    //   694: ldc -14
    //   696: invokestatic 142	com/amap/api/mapcore2d/cv:a	(Lorg/json/JSONObject;Ljava/lang/String;)Z
    //   699: ifeq +424 -> 1123
    //   702: aload_0
    //   703: ldc -14
    //   705: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   708: astore_0
    //   709: new 11	com/amap/api/mapcore2d/cm$a$b
    //   712: dup
    //   713: invokespecial 243	com/amap/api/mapcore2d/cm$a$b:<init>	()V
    //   716: astore_1
    //   717: aload_0
    //   718: aload_1
    //   719: invokestatic 246	com/amap/api/mapcore2d/cm:a	(Lorg/json/JSONObject;Lcom/amap/api/mapcore2d/cm$a$b;)V
    //   722: aload 7
    //   724: aload_1
    //   725: putfield 250	com/amap/api/mapcore2d/cm$a:v	Lcom/amap/api/mapcore2d/cm$a$b;
    //   728: aload 7
    //   730: areturn
    //   731: aload_1
    //   732: aload_0
    //   733: ldc -52
    //   735: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   738: ldc -4
    //   740: invokevirtual 184	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   743: iconst_0
    //   744: invokestatic 255	com/amap/api/mapcore2d/cm:a	(Ljava/lang/String;Z)Z
    //   747: putfield 192	com/amap/api/mapcore2d/cm$a$a:a	Z
    //   750: goto -219 -> 531
    //   753: astore_1
    //   754: aload_1
    //   755: ldc -112
    //   757: ldc_w 257
    //   760: invokestatic 131	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   763: goto -232 -> 531
    //   766: aload 7
    //   768: aload_0
    //   769: ldc -50
    //   771: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   774: putfield 260	com/amap/api/mapcore2d/cm$a:d	Lorg/json/JSONObject;
    //   777: goto -237 -> 540
    //   780: aload 7
    //   782: aload_0
    //   783: ldc -48
    //   785: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   788: putfield 263	com/amap/api/mapcore2d/cm$a:g	Lorg/json/JSONObject;
    //   791: goto -242 -> 549
    //   794: aload 7
    //   796: aload_0
    //   797: ldc -46
    //   799: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   802: putfield 266	com/amap/api/mapcore2d/cm$a:h	Lorg/json/JSONObject;
    //   805: goto -247 -> 558
    //   808: aload 7
    //   810: aload_0
    //   811: ldc -44
    //   813: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   816: putfield 269	com/amap/api/mapcore2d/cm$a:i	Lorg/json/JSONObject;
    //   819: goto -252 -> 567
    //   822: aload 7
    //   824: aload_0
    //   825: ldc -42
    //   827: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   830: putfield 272	com/amap/api/mapcore2d/cm$a:j	Lorg/json/JSONObject;
    //   833: goto -257 -> 576
    //   836: aload 7
    //   838: aload_0
    //   839: ldc -40
    //   841: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   844: putfield 275	com/amap/api/mapcore2d/cm$a:k	Lorg/json/JSONObject;
    //   847: goto -262 -> 585
    //   850: aload 7
    //   852: aload_0
    //   853: ldc -38
    //   855: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   858: putfield 278	com/amap/api/mapcore2d/cm$a:m	Lorg/json/JSONObject;
    //   861: goto -267 -> 594
    //   864: aload 7
    //   866: aload_0
    //   867: ldc -36
    //   869: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   872: putfield 281	com/amap/api/mapcore2d/cm$a:e	Lorg/json/JSONObject;
    //   875: goto -272 -> 603
    //   878: aload 7
    //   880: aload_0
    //   881: ldc -34
    //   883: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   886: putfield 284	com/amap/api/mapcore2d/cm$a:l	Lorg/json/JSONObject;
    //   889: goto -277 -> 612
    //   892: aload_0
    //   893: ldc -32
    //   895: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   898: astore_1
    //   899: new 17	com/amap/api/mapcore2d/cm$a$d
    //   902: dup
    //   903: invokespecial 285	com/amap/api/mapcore2d/cm$a$d:<init>	()V
    //   906: astore_2
    //   907: aload_1
    //   908: aload_2
    //   909: invokestatic 288	com/amap/api/mapcore2d/cm:a	(Lorg/json/JSONObject;Lcom/amap/api/mapcore2d/cm$a$d;)V
    //   912: aload 7
    //   914: aload_2
    //   915: putfield 292	com/amap/api/mapcore2d/cm$a:q	Lcom/amap/api/mapcore2d/cm$a$d;
    //   918: goto -297 -> 621
    //   921: aload_0
    //   922: ldc -30
    //   924: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   927: astore_1
    //   928: new 14	com/amap/api/mapcore2d/cm$a$c
    //   931: dup
    //   932: invokespecial 293	com/amap/api/mapcore2d/cm$a$c:<init>	()V
    //   935: astore_2
    //   936: aload_1
    //   937: aload_2
    //   938: invokestatic 296	com/amap/api/mapcore2d/cm:a	(Lorg/json/JSONObject;Lcom/amap/api/mapcore2d/cm$a$c;)V
    //   941: aload 7
    //   943: aload_2
    //   944: putfield 300	com/amap/api/mapcore2d/cm$a:r	Lcom/amap/api/mapcore2d/cm$a$c;
    //   947: goto -317 -> 630
    //   950: aload 7
    //   952: aload_0
    //   953: ldc -28
    //   955: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   958: putfield 303	com/amap/api/mapcore2d/cm$a:n	Lorg/json/JSONObject;
    //   961: goto -322 -> 639
    //   964: aload 7
    //   966: aload_0
    //   967: ldc -26
    //   969: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   972: putfield 306	com/amap/api/mapcore2d/cm$a:o	Lorg/json/JSONObject;
    //   975: goto -327 -> 648
    //   978: aload_0
    //   979: ldc -24
    //   981: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   984: astore_1
    //   985: new 11	com/amap/api/mapcore2d/cm$a$b
    //   988: dup
    //   989: invokespecial 243	com/amap/api/mapcore2d/cm$a$b:<init>	()V
    //   992: astore_2
    //   993: aload_1
    //   994: aload_2
    //   995: invokestatic 246	com/amap/api/mapcore2d/cm:a	(Lorg/json/JSONObject;Lcom/amap/api/mapcore2d/cm$a$b;)V
    //   998: aload 7
    //   1000: aload_2
    //   1001: putfield 309	com/amap/api/mapcore2d/cm$a:s	Lcom/amap/api/mapcore2d/cm$a$b;
    //   1004: goto -347 -> 657
    //   1007: aload 7
    //   1009: aload_0
    //   1010: ldc -22
    //   1012: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   1015: putfield 312	com/amap/api/mapcore2d/cm$a:f	Lorg/json/JSONObject;
    //   1018: goto -352 -> 666
    //   1021: aload 7
    //   1023: aload_0
    //   1024: ldc -20
    //   1026: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   1029: putfield 314	com/amap/api/mapcore2d/cm$a:c	Lorg/json/JSONObject;
    //   1032: goto -357 -> 675
    //   1035: aload_0
    //   1036: ldc -18
    //   1038: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   1041: astore_1
    //   1042: new 11	com/amap/api/mapcore2d/cm$a$b
    //   1045: dup
    //   1046: invokespecial 243	com/amap/api/mapcore2d/cm$a$b:<init>	()V
    //   1049: astore_2
    //   1050: aload_1
    //   1051: aload_2
    //   1052: invokestatic 246	com/amap/api/mapcore2d/cm:a	(Lorg/json/JSONObject;Lcom/amap/api/mapcore2d/cm$a$b;)V
    //   1055: aload 7
    //   1057: aload_2
    //   1058: putfield 317	com/amap/api/mapcore2d/cm$a:t	Lcom/amap/api/mapcore2d/cm$a$b;
    //   1061: goto -377 -> 684
    //   1064: aload_0
    //   1065: ldc -16
    //   1067: invokevirtual 202	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   1070: astore_1
    //   1071: new 11	com/amap/api/mapcore2d/cm$a$b
    //   1074: dup
    //   1075: invokespecial 243	com/amap/api/mapcore2d/cm$a$b:<init>	()V
    //   1078: astore_2
    //   1079: aload_1
    //   1080: aload_2
    //   1081: invokestatic 246	com/amap/api/mapcore2d/cm:a	(Lorg/json/JSONObject;Lcom/amap/api/mapcore2d/cm$a$b;)V
    //   1084: aload 7
    //   1086: aload_2
    //   1087: putfield 320	com/amap/api/mapcore2d/cm$a:u	Lcom/amap/api/mapcore2d/cm$a$b;
    //   1090: goto -397 -> 693
    //   1093: astore_3
    //   1094: aconst_null
    //   1095: astore_1
    //   1096: goto -865 -> 231
    //   1099: astore_3
    //   1100: goto -869 -> 231
    //   1103: astore_1
    //   1104: aconst_null
    //   1105: astore_1
    //   1106: goto -918 -> 188
    //   1109: astore_3
    //   1110: goto -922 -> 188
    //   1113: astore_3
    //   1114: aconst_null
    //   1115: astore_1
    //   1116: goto -937 -> 179
    //   1119: astore_3
    //   1120: goto -941 -> 179
    //   1123: aload 7
    //   1125: areturn
    //   1126: ldc -108
    //   1128: astore_3
    //   1129: aload_1
    //   1130: astore_2
    //   1131: aload_3
    //   1132: astore_1
    //   1133: goto -796 -> 337
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1136	0	paramContext	Context
    //   0	1136	1	paramcu	cu
    //   0	1136	2	paramString	String
    //   0	1136	3	paramMap	Map<String, String>
    //   268	9	4	i	int
    //   161	130	5	bool	boolean
    //   16	499	6	localObject	Object
    //   7	1117	7	locala	a
    //   97	21	8	localCipher	javax.crypto.Cipher
    // Exception table:
    //   from	to	target	type
    //   18	36	171	com/amap/api/mapcore2d/ck
    //   9	18	174	com/amap/api/mapcore2d/ck
    //   172	174	174	com/amap/api/mapcore2d/ck
    //   200	210	174	com/amap/api/mapcore2d/ck
    //   18	36	199	java/lang/Throwable
    //   9	18	210	javax/crypto/IllegalBlockSizeException
    //   18	36	210	javax/crypto/IllegalBlockSizeException
    //   172	174	210	javax/crypto/IllegalBlockSizeException
    //   200	210	210	javax/crypto/IllegalBlockSizeException
    //   9	18	226	java/lang/Throwable
    //   172	174	226	java/lang/Throwable
    //   200	210	226	java/lang/Throwable
    //   144	163	315	java/lang/Throwable
    //   261	270	315	java/lang/Throwable
    //   295	305	315	java/lang/Throwable
    //   308	312	315	java/lang/Throwable
    //   337	362	315	java/lang/Throwable
    //   362	376	315	java/lang/Throwable
    //   379	391	315	java/lang/Throwable
    //   401	416	315	java/lang/Throwable
    //   423	443	315	java/lang/Throwable
    //   450	460	315	java/lang/Throwable
    //   479	487	315	java/lang/Throwable
    //   490	531	315	java/lang/Throwable
    //   531	540	315	java/lang/Throwable
    //   540	549	315	java/lang/Throwable
    //   549	558	315	java/lang/Throwable
    //   558	567	315	java/lang/Throwable
    //   567	576	315	java/lang/Throwable
    //   576	585	315	java/lang/Throwable
    //   585	594	315	java/lang/Throwable
    //   594	603	315	java/lang/Throwable
    //   603	612	315	java/lang/Throwable
    //   612	621	315	java/lang/Throwable
    //   621	630	315	java/lang/Throwable
    //   630	639	315	java/lang/Throwable
    //   639	648	315	java/lang/Throwable
    //   648	657	315	java/lang/Throwable
    //   657	666	315	java/lang/Throwable
    //   666	675	315	java/lang/Throwable
    //   675	684	315	java/lang/Throwable
    //   684	693	315	java/lang/Throwable
    //   693	728	315	java/lang/Throwable
    //   754	763	315	java/lang/Throwable
    //   766	777	315	java/lang/Throwable
    //   780	791	315	java/lang/Throwable
    //   794	805	315	java/lang/Throwable
    //   808	819	315	java/lang/Throwable
    //   822	833	315	java/lang/Throwable
    //   836	847	315	java/lang/Throwable
    //   850	861	315	java/lang/Throwable
    //   864	875	315	java/lang/Throwable
    //   878	889	315	java/lang/Throwable
    //   892	918	315	java/lang/Throwable
    //   921	947	315	java/lang/Throwable
    //   950	961	315	java/lang/Throwable
    //   964	975	315	java/lang/Throwable
    //   978	1004	315	java/lang/Throwable
    //   1007	1018	315	java/lang/Throwable
    //   1021	1032	315	java/lang/Throwable
    //   1035	1061	315	java/lang/Throwable
    //   1064	1090	315	java/lang/Throwable
    //   281	290	478	java/lang/Throwable
    //   463	475	478	java/lang/Throwable
    //   731	750	753	java/lang/Throwable
    //   218	223	1093	java/lang/Throwable
    //   42	128	1099	java/lang/Throwable
    //   218	223	1103	javax/crypto/IllegalBlockSizeException
    //   42	128	1109	javax/crypto/IllegalBlockSizeException
    //   218	223	1113	com/amap/api/mapcore2d/ck
    //   42	128	1119	com/amap/api/mapcore2d/ck
  }
  
  public static String a(JSONObject paramJSONObject, String paramString)
    throws JSONException
  {
    if (paramJSONObject != null) {
      if (paramJSONObject.has(paramString)) {
        break label18;
      }
    }
    label18:
    while (paramJSONObject.getString(paramString).equals("[]"))
    {
      return "";
      return "";
    }
    return paramJSONObject.optString(paramString);
  }
  
  private static void a(JSONObject paramJSONObject, cm.a.b paramb)
  {
    if (paramb == null) {
      return;
    }
    try
    {
      String str1 = a(paramJSONObject, "m");
      String str2 = a(paramJSONObject, "u");
      String str3 = a(paramJSONObject, "v");
      paramJSONObject = a(paramJSONObject, "able");
      paramb.c = str1;
      paramb.b = str2;
      paramb.d = str3;
      paramb.a = a(paramJSONObject, false);
      return;
    }
    catch (Throwable paramJSONObject)
    {
      cy.a(paramJSONObject, "ConfigManager", "parsePluginEntity");
    }
  }
  
  private static void a(JSONObject paramJSONObject, cm.a.c paramc)
  {
    if (paramJSONObject == null) {
      return;
    }
    try
    {
      String str = a(paramJSONObject, "md5");
      paramJSONObject = a(paramJSONObject, "url");
      paramc.b = str;
      paramc.a = paramJSONObject;
      return;
    }
    catch (Throwable paramJSONObject)
    {
      cy.a(paramJSONObject, "ConfigManager", "parseSDKCoordinate");
    }
  }
  
  private static void a(JSONObject paramJSONObject, cm.a.d paramd)
  {
    if (paramJSONObject == null) {}
    for (;;)
    {
      return;
      try
      {
        String str1 = a(paramJSONObject, "md5");
        String str2 = a(paramJSONObject, "url");
        paramJSONObject = a(paramJSONObject, "sdkversion");
        if ((!TextUtils.isEmpty(str1)) && (!TextUtils.isEmpty(str2)) && (!TextUtils.isEmpty(paramJSONObject)))
        {
          paramd.a = str2;
          paramd.b = str1;
          paramd.c = paramJSONObject;
          return;
        }
      }
      catch (Throwable paramJSONObject)
      {
        cy.a(paramJSONObject, "ConfigManager", "parseSDKUpdate");
      }
    }
  }
  
  public static boolean a(String paramString, boolean paramBoolean)
  {
    boolean bool = true;
    try
    {
      paramString = URLDecoder.decode(paramString).split("/");
      int i = paramString[(paramString.length - 1)].charAt(4);
      paramBoolean = bool;
      if (i % 2 != 1) {
        paramBoolean = false;
      }
      return paramBoolean;
    }
    catch (Throwable paramString) {}
    return paramBoolean;
  }
  
  public static class a
  {
    public String a;
    public int b = -1;
    public JSONObject c;
    public JSONObject d;
    public JSONObject e;
    public JSONObject f;
    public JSONObject g;
    public JSONObject h;
    public JSONObject i;
    public JSONObject j;
    public JSONObject k;
    public JSONObject l;
    public JSONObject m;
    public JSONObject n;
    public JSONObject o;
    public a p;
    public d q;
    public c r;
    public b s;
    public b t;
    public b u;
    public b v;
    
    public static class a
    {
      public boolean a;
      public boolean b;
    }
    
    public static class b
    {
      public boolean a;
      public String b;
      public String c;
      public String d;
    }
    
    public static class c
    {
      public String a;
      public String b;
    }
    
    public static class d
    {
      public String a;
      public String b;
      public String c;
    }
  }
  
  static class b
    extends dz
  {
    private String f;
    private Map<String, String> g;
    
    b(Context paramContext, cu paramcu, String paramString, Map<String, String> paramMap)
    {
      super(paramcu);
      this.f = paramString;
      this.g = paramMap;
    }
    
    private Map<String, String> l()
    {
      Object localObject = cp.q(this.a);
      HashMap localHashMap;
      if (TextUtils.isEmpty((CharSequence)localObject))
      {
        localHashMap = new HashMap();
        localHashMap.put("authkey", this.f);
        localHashMap.put("plattype", "android");
        localHashMap.put("product", this.b.a());
        localHashMap.put("version", this.b.b());
        localHashMap.put("output", "json");
        localHashMap.put("androidversion", Build.VERSION.SDK_INT + "");
        localHashMap.put("deviceId", localObject);
        if (this.g != null) {
          break label203;
        }
        label137:
        if (Build.VERSION.SDK_INT >= 21) {
          break label228;
        }
        localObject = null;
        label147:
        if (TextUtils.isEmpty((CharSequence)localObject)) {
          break label281;
        }
      }
      for (;;)
      {
        localHashMap.put("abitype", localObject);
        localHashMap.put("ext", this.b.e());
        return localHashMap;
        localObject = cr.b(new StringBuilder((String)localObject).reverse().toString());
        break;
        label203:
        if (this.g.isEmpty()) {
          break label137;
        }
        localHashMap.putAll(this.g);
        break label137;
        try
        {
          label228:
          localObject = this.a.getApplicationInfo();
          Field localField = Class.forName(ApplicationInfo.class.getName()).getDeclaredField("primaryCpuAbi");
          localField.setAccessible(true);
          localObject = (String)localField.get(localObject);
        }
        catch (Throwable localThrowable)
        {
          cy.a(localThrowable, "ConfigManager", "getcpu");
          str = null;
        }
        break label147;
        label281:
        String str = Build.CPU_ABI;
      }
    }
    
    public byte[] a()
    {
      return null;
    }
    
    public byte[] b()
    {
      return cv.a(cv.a(l()));
    }
    
    protected String c()
    {
      return "3.0";
    }
    
    public Map<String, String> e()
    {
      return null;
    }
    
    public String g()
    {
      return "https://restapi.amap.com/v3/iasdkauth";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */