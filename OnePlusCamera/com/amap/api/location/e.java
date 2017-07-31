package com.amap.api.location;

import android.location.LocationListener;
import android.os.Bundle;

class e
  implements LocationListener
{
  e(d paramd) {}
  
  /* Error */
  public void onLocationChanged(android.location.Location paramLocation)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aload_0
    //   6: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   9: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   12: iconst_1
    //   13: invokevirtual 33	com/amap/api/location/a:b	(Z)V
    //   16: aload_0
    //   17: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   20: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   23: invokestatic 39	java/lang/System:currentTimeMillis	()J
    //   26: putfield 43	com/amap/api/location/a:d	J
    //   29: aload_1
    //   30: ifnull +97 -> 127
    //   33: aload_3
    //   34: astore_2
    //   35: aload_1
    //   36: invokevirtual 49	android/location/Location:getLatitude	()D
    //   39: aload_1
    //   40: invokevirtual 52	android/location/Location:getLongitude	()D
    //   43: invokestatic 57	com/amap/api/location/core/c:a	(DD)Z
    //   46: ifne +150 -> 196
    //   49: aload_3
    //   50: astore_2
    //   51: new 59	com/amap/api/location/AMapLocation
    //   54: dup
    //   55: aload_1
    //   56: invokespecial 61	com/amap/api/location/AMapLocation:<init>	(Landroid/location/Location;)V
    //   59: astore_1
    //   60: new 63	android/os/Message
    //   63: dup
    //   64: invokespecial 64	android/os/Message:<init>	()V
    //   67: astore_2
    //   68: aload_2
    //   69: aload_1
    //   70: putfield 68	android/os/Message:obj	Ljava/lang/Object;
    //   73: aload_2
    //   74: bipush 100
    //   76: putfield 72	android/os/Message:what	I
    //   79: aload_0
    //   80: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   83: invokestatic 75	com/amap/api/location/d:b	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a$a;
    //   86: ifnonnull +390 -> 476
    //   89: aload_0
    //   90: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   93: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   96: iconst_1
    //   97: putfield 79	com/amap/api/location/a:c	Z
    //   100: aload_0
    //   101: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   104: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   107: invokestatic 39	java/lang/System:currentTimeMillis	()J
    //   110: putfield 43	com/amap/api/location/a:d	J
    //   113: aload_0
    //   114: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   117: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   120: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   123: ifnonnull +368 -> 491
    //   126: return
    //   127: new 63	android/os/Message
    //   130: dup
    //   131: invokespecial 64	android/os/Message:<init>	()V
    //   134: astore_1
    //   135: aload_1
    //   136: aconst_null
    //   137: putfield 68	android/os/Message:obj	Ljava/lang/Object;
    //   140: aload_1
    //   141: bipush 100
    //   143: putfield 72	android/os/Message:what	I
    //   146: aload_0
    //   147: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   150: invokestatic 75	com/amap/api/location/d:b	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a$a;
    //   153: ifnonnull +272 -> 425
    //   156: aload_0
    //   157: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   160: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   163: iconst_1
    //   164: putfield 79	com/amap/api/location/a:c	Z
    //   167: aload_0
    //   168: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   171: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   174: invokestatic 39	java/lang/System:currentTimeMillis	()J
    //   177: putfield 43	com/amap/api/location/a:d	J
    //   180: aload_0
    //   181: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   184: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   187: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   190: astore_1
    //   191: aload_1
    //   192: ifnonnull +248 -> 440
    //   195: return
    //   196: aload_3
    //   197: astore_2
    //   198: aload_1
    //   199: invokevirtual 52	android/location/Location:getLongitude	()D
    //   202: aload_1
    //   203: invokevirtual 49	android/location/Location:getLatitude	()D
    //   206: invokestatic 87	com/aps/u:a	(DD)[D
    //   209: astore 5
    //   211: aload_3
    //   212: astore_2
    //   213: new 59	com/amap/api/location/AMapLocation
    //   216: dup
    //   217: aload_1
    //   218: invokespecial 61	com/amap/api/location/AMapLocation:<init>	(Landroid/location/Location;)V
    //   221: astore_1
    //   222: aload_1
    //   223: aload 5
    //   225: iconst_1
    //   226: daload
    //   227: invokevirtual 91	com/amap/api/location/AMapLocation:setLatitude	(D)V
    //   230: aload_1
    //   231: aload 5
    //   233: iconst_0
    //   234: daload
    //   235: invokevirtual 94	com/amap/api/location/AMapLocation:setLongitude	(D)V
    //   238: goto -178 -> 60
    //   241: astore_3
    //   242: aload_1
    //   243: astore_2
    //   244: aload_3
    //   245: invokevirtual 97	java/lang/Exception:printStackTrace	()V
    //   248: new 63	android/os/Message
    //   251: dup
    //   252: invokespecial 64	android/os/Message:<init>	()V
    //   255: astore_2
    //   256: aload_2
    //   257: aload_1
    //   258: putfield 68	android/os/Message:obj	Ljava/lang/Object;
    //   261: aload_2
    //   262: bipush 100
    //   264: putfield 72	android/os/Message:what	I
    //   267: aload_0
    //   268: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   271: invokestatic 75	com/amap/api/location/d:b	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a$a;
    //   274: ifnonnull +253 -> 527
    //   277: aload_0
    //   278: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   281: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   284: iconst_1
    //   285: putfield 79	com/amap/api/location/a:c	Z
    //   288: aload_0
    //   289: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   292: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   295: invokestatic 39	java/lang/System:currentTimeMillis	()J
    //   298: putfield 43	com/amap/api/location/a:d	J
    //   301: aload_0
    //   302: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   305: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   308: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   311: ifnull +299 -> 610
    //   314: aload_0
    //   315: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   318: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   321: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   324: getfield 102	com/amap/api/location/c:a	Lcom/aps/k;
    //   327: ifnull +283 -> 610
    //   330: aload_0
    //   331: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   334: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   337: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   340: getfield 102	com/amap/api/location/c:a	Lcom/aps/k;
    //   343: aload_1
    //   344: invokeinterface 107 2 0
    //   349: return
    //   350: astore_1
    //   351: aload_1
    //   352: invokevirtual 108	java/lang/Throwable:printStackTrace	()V
    //   355: return
    //   356: astore_1
    //   357: new 63	android/os/Message
    //   360: dup
    //   361: invokespecial 64	android/os/Message:<init>	()V
    //   364: astore_3
    //   365: aload_3
    //   366: aload_2
    //   367: putfield 68	android/os/Message:obj	Ljava/lang/Object;
    //   370: aload_3
    //   371: bipush 100
    //   373: putfield 72	android/os/Message:what	I
    //   376: aload_0
    //   377: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   380: invokestatic 75	com/amap/api/location/d:b	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a$a;
    //   383: ifnonnull +159 -> 542
    //   386: aload_0
    //   387: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   390: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   393: iconst_1
    //   394: putfield 79	com/amap/api/location/a:c	Z
    //   397: aload_0
    //   398: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   401: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   404: invokestatic 39	java/lang/System:currentTimeMillis	()J
    //   407: putfield 43	com/amap/api/location/a:d	J
    //   410: aload_0
    //   411: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   414: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   417: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   420: ifnonnull +137 -> 557
    //   423: aload_1
    //   424: athrow
    //   425: aload_0
    //   426: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   429: invokestatic 75	com/amap/api/location/d:b	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a$a;
    //   432: aload_1
    //   433: invokevirtual 114	com/amap/api/location/a$a:sendMessage	(Landroid/os/Message;)Z
    //   436: pop
    //   437: goto -281 -> 156
    //   440: aload_0
    //   441: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   444: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   447: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   450: getfield 102	com/amap/api/location/c:a	Lcom/aps/k;
    //   453: ifnull -258 -> 195
    //   456: aload_0
    //   457: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   460: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   463: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   466: getfield 102	com/amap/api/location/c:a	Lcom/aps/k;
    //   469: aconst_null
    //   470: invokeinterface 107 2 0
    //   475: return
    //   476: aload_0
    //   477: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   480: invokestatic 75	com/amap/api/location/d:b	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a$a;
    //   483: aload_2
    //   484: invokevirtual 114	com/amap/api/location/a$a:sendMessage	(Landroid/os/Message;)Z
    //   487: pop
    //   488: goto -399 -> 89
    //   491: aload_0
    //   492: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   495: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   498: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   501: getfield 102	com/amap/api/location/c:a	Lcom/aps/k;
    //   504: ifnull +106 -> 610
    //   507: aload_0
    //   508: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   511: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   514: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   517: getfield 102	com/amap/api/location/c:a	Lcom/aps/k;
    //   520: aload_1
    //   521: invokeinterface 107 2 0
    //   526: return
    //   527: aload_0
    //   528: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   531: invokestatic 75	com/amap/api/location/d:b	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a$a;
    //   534: aload_2
    //   535: invokevirtual 114	com/amap/api/location/a$a:sendMessage	(Landroid/os/Message;)Z
    //   538: pop
    //   539: goto -262 -> 277
    //   542: aload_0
    //   543: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   546: invokestatic 75	com/amap/api/location/d:b	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a$a;
    //   549: aload_3
    //   550: invokevirtual 114	com/amap/api/location/a$a:sendMessage	(Landroid/os/Message;)Z
    //   553: pop
    //   554: goto -168 -> 386
    //   557: aload_0
    //   558: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   561: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   564: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   567: getfield 102	com/amap/api/location/c:a	Lcom/aps/k;
    //   570: ifnull -147 -> 423
    //   573: aload_0
    //   574: getfield 14	com/amap/api/location/e:a	Lcom/amap/api/location/d;
    //   577: invokestatic 27	com/amap/api/location/d:a	(Lcom/amap/api/location/d;)Lcom/amap/api/location/a;
    //   580: getfield 82	com/amap/api/location/a:b	Lcom/amap/api/location/c;
    //   583: getfield 102	com/amap/api/location/c:a	Lcom/aps/k;
    //   586: aload_2
    //   587: invokeinterface 107 2 0
    //   592: goto -169 -> 423
    //   595: astore_3
    //   596: aload_1
    //   597: astore_2
    //   598: aload_3
    //   599: astore_1
    //   600: goto -243 -> 357
    //   603: astore_3
    //   604: aload 4
    //   606: astore_1
    //   607: goto -365 -> 242
    //   610: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	611	0	this	e
    //   0	611	1	paramLocation	android.location.Location
    //   34	564	2	localObject1	Object
    //   1	211	3	localObject2	Object
    //   241	4	3	localException1	Exception
    //   364	186	3	localMessage	android.os.Message
    //   595	4	3	localObject3	Object
    //   603	1	3	localException2	Exception
    //   3	602	4	localObject4	Object
    //   209	23	5	arrayOfDouble	double[]
    // Exception table:
    //   from	to	target	type
    //   222	238	241	java/lang/Exception
    //   5	29	350	java/lang/Throwable
    //   60	89	350	java/lang/Throwable
    //   89	126	350	java/lang/Throwable
    //   127	156	350	java/lang/Throwable
    //   156	191	350	java/lang/Throwable
    //   248	277	350	java/lang/Throwable
    //   277	349	350	java/lang/Throwable
    //   357	386	350	java/lang/Throwable
    //   386	423	350	java/lang/Throwable
    //   423	425	350	java/lang/Throwable
    //   425	437	350	java/lang/Throwable
    //   440	475	350	java/lang/Throwable
    //   476	488	350	java/lang/Throwable
    //   491	526	350	java/lang/Throwable
    //   527	539	350	java/lang/Throwable
    //   542	554	350	java/lang/Throwable
    //   557	592	350	java/lang/Throwable
    //   35	49	356	finally
    //   51	60	356	finally
    //   198	211	356	finally
    //   213	222	356	finally
    //   244	248	356	finally
    //   222	238	595	finally
    //   35	49	603	java/lang/Exception
    //   51	60	603	java/lang/Exception
    //   198	211	603	java/lang/Exception
    //   213	222	603	java/lang/Exception
  }
  
  public void onProviderDisabled(String paramString) {}
  
  public void onProviderEnabled(String paramString) {}
  
  public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/e.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */