package com.android.server.location;

import android.text.TextUtils;
import android.util.Log;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GpsXtraDownloader
{
  private static final int CONNECTION_TIMEOUT_MS = (int)TimeUnit.SECONDS.toMillis(30L);
  private static final boolean DEBUG = Log.isLoggable("GpsXtraDownloader", 3);
  private static final String DEFAULT_USER_AGENT = "Android";
  private static final long MAXIMUM_CONTENT_LENGTH_BYTES = 1000000L;
  private static final String TAG = "GpsXtraDownloader";
  private int mNextServerIndex;
  private final String mUserAgent;
  private final String[] mXtraServers;
  
  GpsXtraDownloader(Properties paramProperties)
  {
    int j = 0;
    String str2 = paramProperties.getProperty("XTRA_SERVER_1");
    String str3 = paramProperties.getProperty("XTRA_SERVER_2");
    String str1 = paramProperties.getProperty("XTRA_SERVER_3");
    if (str2 != null) {
      j = 1;
    }
    int i = j;
    if (str3 != null) {
      i = j + 1;
    }
    j = i;
    if (str1 != null) {
      j = i + 1;
    }
    paramProperties = paramProperties.getProperty("XTRA_USER_AGENT");
    if (TextUtils.isEmpty(paramProperties)) {}
    for (this.mUserAgent = "Android"; j == 0; this.mUserAgent = paramProperties)
    {
      Log.e("GpsXtraDownloader", "No XTRA servers were specified in the GPS configuration");
      this.mXtraServers = null;
      return;
    }
    this.mXtraServers = new String[j];
    if (str2 != null) {
      this.mXtraServers[0] = str2;
    }
    for (j = 1;; j = 0)
    {
      i = j;
      if (str3 != null)
      {
        this.mXtraServers[j] = str3;
        i = j + 1;
      }
      if (str1 != null)
      {
        paramProperties = this.mXtraServers;
        j = i + 1;
        paramProperties[i] = str1;
        i = j;
      }
      for (;;)
      {
        this.mNextServerIndex = new Random().nextInt(i);
        return;
      }
    }
  }
  
  /* Error */
  protected byte[] doDownload(String paramString)
  {
    // Byte code:
    //   0: getstatic 33	com/android/server/location/GpsXtraDownloader:DEBUG	Z
    //   3: ifeq +28 -> 31
    //   6: ldc 19
    //   8: new 101	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 102	java/lang/StringBuilder:<init>	()V
    //   15: ldc 104
    //   17: invokevirtual 108	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_1
    //   21: invokevirtual 108	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 112	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokestatic 115	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   30: pop
    //   31: aconst_null
    //   32: astore 4
    //   34: aconst_null
    //   35: astore_3
    //   36: new 117	java/net/URL
    //   39: dup
    //   40: aload_1
    //   41: invokespecial 120	java/net/URL:<init>	(Ljava/lang/String;)V
    //   44: invokevirtual 124	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   47: checkcast 126	java/net/HttpURLConnection
    //   50: astore 6
    //   52: aload 6
    //   54: astore_3
    //   55: aload 6
    //   57: astore 4
    //   59: aload 6
    //   61: ldc -128
    //   63: ldc -126
    //   65: invokevirtual 134	java/net/HttpURLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   68: aload 6
    //   70: astore_3
    //   71: aload 6
    //   73: astore 4
    //   75: aload 6
    //   77: ldc -120
    //   79: ldc -118
    //   81: invokevirtual 134	java/net/HttpURLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   84: aload 6
    //   86: astore_3
    //   87: aload 6
    //   89: astore 4
    //   91: aload 6
    //   93: getstatic 47	com/android/server/location/GpsXtraDownloader:CONNECTION_TIMEOUT_MS	I
    //   96: invokevirtual 142	java/net/HttpURLConnection:setConnectTimeout	(I)V
    //   99: aload 6
    //   101: astore_3
    //   102: aload 6
    //   104: astore 4
    //   106: aload 6
    //   108: invokevirtual 145	java/net/HttpURLConnection:connect	()V
    //   111: aload 6
    //   113: astore_3
    //   114: aload 6
    //   116: astore 4
    //   118: aload 6
    //   120: invokevirtual 149	java/net/HttpURLConnection:getResponseCode	()I
    //   123: istore_2
    //   124: iload_2
    //   125: sipush 200
    //   128: if_icmpeq +60 -> 188
    //   131: aload 6
    //   133: astore_3
    //   134: aload 6
    //   136: astore 4
    //   138: getstatic 33	com/android/server/location/GpsXtraDownloader:DEBUG	Z
    //   141: ifeq +35 -> 176
    //   144: aload 6
    //   146: astore_3
    //   147: aload 6
    //   149: astore 4
    //   151: ldc 19
    //   153: new 101	java/lang/StringBuilder
    //   156: dup
    //   157: invokespecial 102	java/lang/StringBuilder:<init>	()V
    //   160: ldc -105
    //   162: invokevirtual 108	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: iload_2
    //   166: invokevirtual 154	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   169: invokevirtual 112	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   172: invokestatic 115	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   175: pop
    //   176: aload 6
    //   178: ifnull +8 -> 186
    //   181: aload 6
    //   183: invokevirtual 157	java/net/HttpURLConnection:disconnect	()V
    //   186: aconst_null
    //   187: areturn
    //   188: aconst_null
    //   189: astore 7
    //   191: aconst_null
    //   192: astore 4
    //   194: aconst_null
    //   195: astore 8
    //   197: aconst_null
    //   198: astore_3
    //   199: aconst_null
    //   200: astore_1
    //   201: aload 6
    //   203: invokevirtual 161	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
    //   206: astore 5
    //   208: aload 5
    //   210: astore_1
    //   211: aload 5
    //   213: astore_3
    //   214: new 163	java/io/ByteArrayOutputStream
    //   217: dup
    //   218: invokespecial 164	java/io/ByteArrayOutputStream:<init>	()V
    //   221: astore 9
    //   223: aload 5
    //   225: astore_1
    //   226: aload 5
    //   228: astore_3
    //   229: sipush 1024
    //   232: newarray <illegal type>
    //   234: astore 10
    //   236: aload 5
    //   238: astore_1
    //   239: aload 5
    //   241: astore_3
    //   242: aload 5
    //   244: aload 10
    //   246: invokevirtual 170	java/io/InputStream:read	([B)I
    //   249: istore_2
    //   250: iload_2
    //   251: iconst_m1
    //   252: if_icmpeq +147 -> 399
    //   255: aload 5
    //   257: astore_1
    //   258: aload 5
    //   260: astore_3
    //   261: aload 9
    //   263: aload 10
    //   265: iconst_0
    //   266: iload_2
    //   267: invokevirtual 174	java/io/ByteArrayOutputStream:write	([BII)V
    //   270: aload 5
    //   272: astore_1
    //   273: aload 5
    //   275: astore_3
    //   276: aload 9
    //   278: invokevirtual 177	java/io/ByteArrayOutputStream:size	()I
    //   281: i2l
    //   282: ldc2_w 15
    //   285: lcmp
    //   286: ifle -50 -> 236
    //   289: aload 5
    //   291: astore_1
    //   292: aload 5
    //   294: astore_3
    //   295: getstatic 33	com/android/server/location/GpsXtraDownloader:DEBUG	Z
    //   298: ifeq +17 -> 315
    //   301: aload 5
    //   303: astore_1
    //   304: aload 5
    //   306: astore_3
    //   307: ldc 19
    //   309: ldc -77
    //   311: invokestatic 115	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   314: pop
    //   315: aload 8
    //   317: astore_1
    //   318: aload 5
    //   320: ifnull +18 -> 338
    //   323: aload 6
    //   325: astore_3
    //   326: aload 6
    //   328: astore 4
    //   330: aload 5
    //   332: invokevirtual 182	java/io/InputStream:close	()V
    //   335: aload 8
    //   337: astore_1
    //   338: aload_1
    //   339: ifnull +48 -> 387
    //   342: aload 6
    //   344: astore_3
    //   345: aload 6
    //   347: astore 4
    //   349: aload_1
    //   350: athrow
    //   351: astore_1
    //   352: aload_3
    //   353: astore 4
    //   355: getstatic 33	com/android/server/location/GpsXtraDownloader:DEBUG	Z
    //   358: ifeq +15 -> 373
    //   361: aload_3
    //   362: astore 4
    //   364: ldc 19
    //   366: ldc -72
    //   368: aload_1
    //   369: invokestatic 187	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   372: pop
    //   373: aload_3
    //   374: ifnull +7 -> 381
    //   377: aload_3
    //   378: invokevirtual 157	java/net/HttpURLConnection:disconnect	()V
    //   381: aconst_null
    //   382: areturn
    //   383: astore_1
    //   384: goto -46 -> 338
    //   387: aload 6
    //   389: ifnull +8 -> 397
    //   392: aload 6
    //   394: invokevirtual 157	java/net/HttpURLConnection:disconnect	()V
    //   397: aconst_null
    //   398: areturn
    //   399: aload 5
    //   401: astore_1
    //   402: aload 5
    //   404: astore_3
    //   405: aload 9
    //   407: invokevirtual 191	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   410: astore 8
    //   412: aload 7
    //   414: astore_1
    //   415: aload 5
    //   417: ifnull +18 -> 435
    //   420: aload 6
    //   422: astore_3
    //   423: aload 6
    //   425: astore 4
    //   427: aload 5
    //   429: invokevirtual 182	java/io/InputStream:close	()V
    //   432: aload 7
    //   434: astore_1
    //   435: aload_1
    //   436: ifnull +29 -> 465
    //   439: aload 6
    //   441: astore_3
    //   442: aload 6
    //   444: astore 4
    //   446: aload_1
    //   447: athrow
    //   448: astore_1
    //   449: aload 4
    //   451: ifnull +8 -> 459
    //   454: aload 4
    //   456: invokevirtual 157	java/net/HttpURLConnection:disconnect	()V
    //   459: aload_1
    //   460: athrow
    //   461: astore_1
    //   462: goto -27 -> 435
    //   465: aload 6
    //   467: ifnull +8 -> 475
    //   470: aload 6
    //   472: invokevirtual 157	java/net/HttpURLConnection:disconnect	()V
    //   475: aload 8
    //   477: areturn
    //   478: astore 5
    //   480: aload 5
    //   482: athrow
    //   483: astore 7
    //   485: aload 5
    //   487: astore 8
    //   489: aload_1
    //   490: ifnull +18 -> 508
    //   493: aload 6
    //   495: astore_3
    //   496: aload 6
    //   498: astore 4
    //   500: aload_1
    //   501: invokevirtual 182	java/io/InputStream:close	()V
    //   504: aload 5
    //   506: astore 8
    //   508: aload 8
    //   510: ifnull +43 -> 553
    //   513: aload 6
    //   515: astore_3
    //   516: aload 6
    //   518: astore 4
    //   520: aload 8
    //   522: athrow
    //   523: aload 5
    //   525: astore 8
    //   527: aload 5
    //   529: aload_1
    //   530: if_acmpeq -22 -> 508
    //   533: aload 6
    //   535: astore_3
    //   536: aload 6
    //   538: astore 4
    //   540: aload 5
    //   542: aload_1
    //   543: invokevirtual 195	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   546: aload 5
    //   548: astore 8
    //   550: goto -42 -> 508
    //   553: aload 6
    //   555: astore_3
    //   556: aload 6
    //   558: astore 4
    //   560: aload 7
    //   562: athrow
    //   563: astore 7
    //   565: aload_3
    //   566: astore_1
    //   567: aload 4
    //   569: astore 5
    //   571: goto -86 -> 485
    //   574: astore_1
    //   575: aload 5
    //   577: ifnonnull -54 -> 523
    //   580: aload_1
    //   581: astore 8
    //   583: goto -75 -> 508
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	586	0	this	GpsXtraDownloader
    //   0	586	1	paramString	String
    //   123	144	2	i	int
    //   35	531	3	localObject1	Object
    //   32	536	4	localObject2	Object
    //   206	222	5	localInputStream	java.io.InputStream
    //   478	69	5	localThrowable	Throwable
    //   569	7	5	localObject3	Object
    //   50	507	6	localHttpURLConnection	java.net.HttpURLConnection
    //   189	244	7	localObject4	Object
    //   483	78	7	localObject5	Object
    //   563	1	7	localObject6	Object
    //   195	387	8	localObject7	Object
    //   221	185	9	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   234	30	10	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   36	52	351	java/io/IOException
    //   59	68	351	java/io/IOException
    //   75	84	351	java/io/IOException
    //   91	99	351	java/io/IOException
    //   106	111	351	java/io/IOException
    //   118	124	351	java/io/IOException
    //   138	144	351	java/io/IOException
    //   151	176	351	java/io/IOException
    //   330	335	351	java/io/IOException
    //   349	351	351	java/io/IOException
    //   427	432	351	java/io/IOException
    //   446	448	351	java/io/IOException
    //   500	504	351	java/io/IOException
    //   520	523	351	java/io/IOException
    //   540	546	351	java/io/IOException
    //   560	563	351	java/io/IOException
    //   330	335	383	java/lang/Throwable
    //   36	52	448	finally
    //   59	68	448	finally
    //   75	84	448	finally
    //   91	99	448	finally
    //   106	111	448	finally
    //   118	124	448	finally
    //   138	144	448	finally
    //   151	176	448	finally
    //   330	335	448	finally
    //   349	351	448	finally
    //   355	361	448	finally
    //   364	373	448	finally
    //   427	432	448	finally
    //   446	448	448	finally
    //   500	504	448	finally
    //   520	523	448	finally
    //   540	546	448	finally
    //   560	563	448	finally
    //   427	432	461	java/lang/Throwable
    //   201	208	478	java/lang/Throwable
    //   214	223	478	java/lang/Throwable
    //   229	236	478	java/lang/Throwable
    //   242	250	478	java/lang/Throwable
    //   261	270	478	java/lang/Throwable
    //   276	289	478	java/lang/Throwable
    //   295	301	478	java/lang/Throwable
    //   307	315	478	java/lang/Throwable
    //   405	412	478	java/lang/Throwable
    //   480	483	483	finally
    //   201	208	563	finally
    //   214	223	563	finally
    //   229	236	563	finally
    //   242	250	563	finally
    //   261	270	563	finally
    //   276	289	563	finally
    //   295	301	563	finally
    //   307	315	563	finally
    //   405	412	563	finally
    //   500	504	574	java/lang/Throwable
  }
  
  byte[] downloadXtraData()
  {
    Object localObject1 = null;
    int i = this.mNextServerIndex;
    if (this.mXtraServers == null) {
      return null;
    }
    Object localObject2;
    do
    {
      localObject2 = localObject1;
      if (localObject1 != null) {
        break;
      }
      localObject2 = doDownload(this.mXtraServers[this.mNextServerIndex]);
      this.mNextServerIndex += 1;
      if (this.mNextServerIndex == this.mXtraServers.length) {
        this.mNextServerIndex = 0;
      }
      localObject1 = localObject2;
    } while (this.mNextServerIndex != i);
    return (byte[])localObject2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/GpsXtraDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */