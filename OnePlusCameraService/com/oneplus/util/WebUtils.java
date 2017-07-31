package com.oneplus.util;

import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

public class WebUtils
{
  public static final int HTTP_STATUS_OK = 200;
  private static final String TAG = WebUtils.class.getSimpleName();
  private static final String UTF8 = "UTF-8";
  
  public static WebResponse request(URL paramURL)
  {
    return request(paramURL, 0, null);
  }
  
  public static WebResponse request(URL paramURL, int paramInt)
  {
    return request(paramURL, null, paramInt, null);
  }
  
  public static WebResponse request(URL paramURL, int paramInt, Ref<Boolean> paramRef)
  {
    return request(paramURL, null, paramInt, paramRef);
  }
  
  public static WebResponse request(URL paramURL, Ref<Boolean> paramRef)
  {
    return request(paramURL, 0, paramRef);
  }
  
  public static WebResponse request(URL paramURL, OutputStream paramOutputStream)
  {
    return request(paramURL, paramOutputStream, 0, null);
  }
  
  /* Error */
  public static WebResponse request(URL paramURL, OutputStream paramOutputStream, int paramInt, Ref<Boolean> paramRef)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 11
    //   3: iconst_0
    //   4: istore 4
    //   6: aload 11
    //   8: astore 14
    //   10: iload 4
    //   12: iload_2
    //   13: if_icmpgt +12 -> 25
    //   16: aload_0
    //   17: invokevirtual 52	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   20: checkcast 54	java/net/HttpURLConnection
    //   23: astore 14
    //   25: iconst_0
    //   26: istore 7
    //   28: aload_1
    //   29: astore 15
    //   31: aload_1
    //   32: ifnonnull +15 -> 47
    //   35: new 56	java/io/ByteArrayOutputStream
    //   38: dup
    //   39: invokespecial 57	java/io/ByteArrayOutputStream:<init>	()V
    //   42: astore 15
    //   44: iconst_1
    //   45: istore 7
    //   47: aconst_null
    //   48: astore 11
    //   50: aconst_null
    //   51: astore_1
    //   52: aconst_null
    //   53: astore_0
    //   54: aconst_null
    //   55: astore 12
    //   57: iconst_m1
    //   58: istore 4
    //   60: aconst_null
    //   61: astore 13
    //   63: aconst_null
    //   64: astore 17
    //   66: iload 4
    //   68: istore 5
    //   70: aload 14
    //   72: ifnull +61 -> 133
    //   75: iconst_0
    //   76: istore 8
    //   78: aload 12
    //   80: astore_0
    //   81: aload_1
    //   82: astore 11
    //   84: aload 17
    //   86: astore 13
    //   88: iload 4
    //   90: istore 5
    //   92: iload 8
    //   94: iload_2
    //   95: if_icmpgt +33 -> 128
    //   98: aload_3
    //   99: ifnull +118 -> 217
    //   102: aload_3
    //   103: invokeinterface 63 1 0
    //   108: checkcast 65	java/lang/Boolean
    //   111: invokevirtual 69	java/lang/Boolean:booleanValue	()Z
    //   114: ifeq +103 -> 217
    //   117: iload 4
    //   119: istore 5
    //   121: aload 17
    //   123: astore 13
    //   125: aload_1
    //   126: astore 11
    //   128: aload 14
    //   130: invokevirtual 72	java/net/HttpURLConnection:disconnect	()V
    //   133: iload 7
    //   135: ifeq +8 -> 143
    //   138: aload 15
    //   140: invokevirtual 77	java/io/OutputStream:close	()V
    //   143: aload_3
    //   144: ifnull +603 -> 747
    //   147: aload_3
    //   148: invokeinterface 63 1 0
    //   153: checkcast 65	java/lang/Boolean
    //   156: invokevirtual 69	java/lang/Boolean:booleanValue	()Z
    //   159: istore 10
    //   161: new 6	com/oneplus/util/WebUtils$WebResponse
    //   164: dup
    //   165: aload 11
    //   167: aload_0
    //   168: aload 13
    //   170: iload 5
    //   172: iload 10
    //   174: aconst_null
    //   175: invokespecial 80	com/oneplus/util/WebUtils$WebResponse:<init>	([B[BLjava/lang/String;IZLcom/oneplus/util/WebUtils$WebResponse;)V
    //   178: areturn
    //   179: astore 12
    //   181: getstatic 19	com/oneplus/util/WebUtils:TAG	Ljava/lang/String;
    //   184: new 82	java/lang/StringBuilder
    //   187: dup
    //   188: invokespecial 83	java/lang/StringBuilder:<init>	()V
    //   191: ldc 85
    //   193: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   196: aload_0
    //   197: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   200: invokevirtual 95	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   203: aload 12
    //   205: invokestatic 101	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   208: iload 4
    //   210: iconst_1
    //   211: iadd
    //   212: istore 4
    //   214: goto -208 -> 6
    //   217: aload_1
    //   218: astore 12
    //   220: iload 4
    //   222: istore 6
    //   224: aload 14
    //   226: invokevirtual 105	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
    //   229: astore 11
    //   231: aload_1
    //   232: astore 12
    //   234: iload 4
    //   236: istore 6
    //   238: sipush 1024
    //   241: newarray <illegal type>
    //   243: astore 13
    //   245: aload_1
    //   246: astore 12
    //   248: iload 4
    //   250: istore 6
    //   252: aload 11
    //   254: aload 13
    //   256: invokevirtual 111	java/io/InputStream:read	([B)I
    //   259: istore 5
    //   261: iload 5
    //   263: iconst_m1
    //   264: if_icmpeq +29 -> 293
    //   267: aload_3
    //   268: ifnull +120 -> 388
    //   271: aload_1
    //   272: astore 12
    //   274: iload 4
    //   276: istore 6
    //   278: aload_3
    //   279: invokeinterface 63 1 0
    //   284: checkcast 65	java/lang/Boolean
    //   287: invokevirtual 69	java/lang/Boolean:booleanValue	()Z
    //   290: ifeq +98 -> 388
    //   293: aload_3
    //   294: ifnull +36 -> 330
    //   297: aload_1
    //   298: astore 11
    //   300: aload 17
    //   302: astore 13
    //   304: iload 4
    //   306: istore 5
    //   308: aload_1
    //   309: astore 12
    //   311: iload 4
    //   313: istore 6
    //   315: aload_3
    //   316: invokeinterface 63 1 0
    //   321: checkcast 65	java/lang/Boolean
    //   324: invokevirtual 69	java/lang/Boolean:booleanValue	()Z
    //   327: ifne -199 -> 128
    //   330: aload_1
    //   331: astore 11
    //   333: aload_1
    //   334: astore 12
    //   336: iload 4
    //   338: istore 6
    //   340: aload 15
    //   342: instanceof 56
    //   345: ifeq +20 -> 365
    //   348: aload_1
    //   349: astore 12
    //   351: iload 4
    //   353: istore 6
    //   355: aload 15
    //   357: checkcast 56	java/io/ByteArrayOutputStream
    //   360: invokevirtual 115	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   363: astore 11
    //   365: sipush 200
    //   368: istore 5
    //   370: aload 11
    //   372: astore 12
    //   374: iload 5
    //   376: istore 6
    //   378: aload 14
    //   380: invokevirtual 118	java/net/HttpURLConnection:getContentType	()Ljava/lang/String;
    //   383: astore 13
    //   385: goto -257 -> 128
    //   388: aload_1
    //   389: astore 12
    //   391: iload 4
    //   393: istore 6
    //   395: aload 15
    //   397: aload 13
    //   399: iconst_0
    //   400: iload 5
    //   402: invokevirtual 122	java/io/OutputStream:write	([BII)V
    //   405: goto -160 -> 245
    //   408: astore_1
    //   409: aload_3
    //   410: ifnull +30 -> 440
    //   413: aload 12
    //   415: astore 11
    //   417: aload 17
    //   419: astore 13
    //   421: iload 6
    //   423: istore 5
    //   425: aload_3
    //   426: invokeinterface 63 1 0
    //   431: checkcast 65	java/lang/Boolean
    //   434: invokevirtual 69	java/lang/Boolean:booleanValue	()Z
    //   437: ifne -309 -> 128
    //   440: iload 8
    //   442: iconst_1
    //   443: iadd
    //   444: istore 9
    //   446: aload 12
    //   448: astore_1
    //   449: iload 6
    //   451: istore 4
    //   453: iload 9
    //   455: istore 8
    //   457: iload 9
    //   459: iload_2
    //   460: if_icmple -379 -> 81
    //   463: aconst_null
    //   464: astore 19
    //   466: aconst_null
    //   467: astore 18
    //   469: aconst_null
    //   470: astore 13
    //   472: aconst_null
    //   473: astore 11
    //   475: new 56	java/io/ByteArrayOutputStream
    //   478: dup
    //   479: invokespecial 57	java/io/ByteArrayOutputStream:<init>	()V
    //   482: astore 16
    //   484: aload_0
    //   485: astore_1
    //   486: aload_0
    //   487: astore 13
    //   489: aload 14
    //   491: invokevirtual 125	java/net/HttpURLConnection:getErrorStream	()Ljava/io/InputStream;
    //   494: astore 11
    //   496: aload_0
    //   497: astore_1
    //   498: aload_0
    //   499: astore 13
    //   501: sipush 1024
    //   504: newarray <illegal type>
    //   506: astore 20
    //   508: aload_0
    //   509: astore_1
    //   510: aload_0
    //   511: astore 13
    //   513: aload 11
    //   515: aload 20
    //   517: invokevirtual 111	java/io/InputStream:read	([B)I
    //   520: istore 4
    //   522: iload 4
    //   524: iconst_m1
    //   525: if_icmpeq +88 -> 613
    //   528: aload_0
    //   529: astore_1
    //   530: aload_0
    //   531: astore 13
    //   533: aload 16
    //   535: aload 20
    //   537: iconst_0
    //   538: iload 4
    //   540: invokevirtual 126	java/io/ByteArrayOutputStream:write	([BII)V
    //   543: goto -35 -> 508
    //   546: astore 13
    //   548: aload 16
    //   550: astore 11
    //   552: aload_1
    //   553: astore_0
    //   554: aload 13
    //   556: athrow
    //   557: astore 16
    //   559: aload 13
    //   561: astore_1
    //   562: aload 11
    //   564: astore 13
    //   566: aload_1
    //   567: astore 11
    //   569: aload 16
    //   571: astore_1
    //   572: aload 11
    //   574: astore 16
    //   576: aload 13
    //   578: ifnull +12 -> 590
    //   581: aload 13
    //   583: invokevirtual 127	java/io/ByteArrayOutputStream:close	()V
    //   586: aload 11
    //   588: astore 16
    //   590: aload 16
    //   592: ifnull +149 -> 741
    //   595: aload 16
    //   597: athrow
    //   598: astore_1
    //   599: aload 12
    //   601: astore_1
    //   602: iload 6
    //   604: istore 4
    //   606: iload 9
    //   608: istore 8
    //   610: goto -529 -> 81
    //   613: aload_0
    //   614: astore_1
    //   615: aload_0
    //   616: astore 13
    //   618: aload 16
    //   620: invokevirtual 115	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   623: astore 11
    //   625: aload 11
    //   627: astore_1
    //   628: aload 11
    //   630: astore 13
    //   632: aload 14
    //   634: invokevirtual 131	java/net/HttpURLConnection:getResponseCode	()I
    //   637: istore 5
    //   639: aload 19
    //   641: astore 13
    //   643: aload 16
    //   645: ifnull +12 -> 657
    //   648: aload 16
    //   650: invokevirtual 127	java/io/ByteArrayOutputStream:close	()V
    //   653: aload 19
    //   655: astore 13
    //   657: aload 12
    //   659: astore_1
    //   660: aload 11
    //   662: astore_0
    //   663: iload 5
    //   665: istore 4
    //   667: iload 9
    //   669: istore 8
    //   671: aload 13
    //   673: ifnull -592 -> 81
    //   676: aload 13
    //   678: athrow
    //   679: astore_0
    //   680: aload 12
    //   682: astore_1
    //   683: aload 11
    //   685: astore_0
    //   686: iload 5
    //   688: istore 4
    //   690: iload 9
    //   692: istore 8
    //   694: goto -613 -> 81
    //   697: astore 13
    //   699: goto -42 -> 657
    //   702: astore 13
    //   704: aload 11
    //   706: ifnonnull +10 -> 716
    //   709: aload 13
    //   711: astore 16
    //   713: goto -123 -> 590
    //   716: aload 11
    //   718: astore 16
    //   720: aload 11
    //   722: aload 13
    //   724: if_acmpeq -134 -> 590
    //   727: aload 11
    //   729: aload 13
    //   731: invokevirtual 135	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   734: aload 11
    //   736: astore 16
    //   738: goto -148 -> 590
    //   741: aload_1
    //   742: athrow
    //   743: astore_1
    //   744: goto -601 -> 143
    //   747: iconst_0
    //   748: istore 10
    //   750: goto -589 -> 161
    //   753: astore_1
    //   754: aload 18
    //   756: astore 11
    //   758: goto -186 -> 572
    //   761: astore_1
    //   762: aload 13
    //   764: astore_0
    //   765: aload 18
    //   767: astore 11
    //   769: aload 16
    //   771: astore 13
    //   773: goto -201 -> 572
    //   776: astore 13
    //   778: goto -224 -> 554
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	781	0	paramURL	URL
    //   0	781	1	paramOutputStream	OutputStream
    //   0	781	2	paramInt	int
    //   0	781	3	paramRef	Ref<Boolean>
    //   4	685	4	i	int
    //   68	619	5	j	int
    //   222	381	6	k	int
    //   26	108	7	m	int
    //   76	617	8	n	int
    //   444	247	9	i1	int
    //   159	590	10	bool	boolean
    //   1	767	11	localObject1	Object
    //   55	24	12	localObject2	Object
    //   179	25	12	localThrowable1	Throwable
    //   218	463	12	localObject3	Object
    //   61	471	13	localObject4	Object
    //   546	14	13	localThrowable2	Throwable
    //   564	113	13	localObject5	Object
    //   697	1	13	localThrowable3	Throwable
    //   702	61	13	localThrowable4	Throwable
    //   771	1	13	localObject6	Object
    //   776	1	13	localThrowable5	Throwable
    //   8	625	14	localObject7	Object
    //   29	367	15	localObject8	Object
    //   482	67	16	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   557	13	16	localObject9	Object
    //   574	196	16	localObject10	Object
    //   64	354	17	localObject11	Object
    //   467	299	18	localObject12	Object
    //   464	190	19	localObject13	Object
    //   506	30	20	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   16	25	179	java/lang/Throwable
    //   224	231	408	java/lang/Throwable
    //   238	245	408	java/lang/Throwable
    //   252	261	408	java/lang/Throwable
    //   278	293	408	java/lang/Throwable
    //   315	330	408	java/lang/Throwable
    //   340	348	408	java/lang/Throwable
    //   355	365	408	java/lang/Throwable
    //   378	385	408	java/lang/Throwable
    //   395	405	408	java/lang/Throwable
    //   489	496	546	java/lang/Throwable
    //   501	508	546	java/lang/Throwable
    //   513	522	546	java/lang/Throwable
    //   533	543	546	java/lang/Throwable
    //   618	625	546	java/lang/Throwable
    //   632	639	546	java/lang/Throwable
    //   554	557	557	finally
    //   595	598	598	java/lang/Throwable
    //   727	734	598	java/lang/Throwable
    //   741	743	598	java/lang/Throwable
    //   676	679	679	java/lang/Throwable
    //   648	653	697	java/lang/Throwable
    //   581	586	702	java/lang/Throwable
    //   138	143	743	java/lang/Throwable
    //   475	484	753	finally
    //   489	496	761	finally
    //   501	508	761	finally
    //   513	522	761	finally
    //   533	543	761	finally
    //   618	625	761	finally
    //   632	639	761	finally
    //   475	484	776	java/lang/Throwable
  }
  
  public static WebResponse request(URL paramURL, OutputStream paramOutputStream, Ref<Boolean> paramRef)
  {
    return request(paramURL, paramOutputStream, 0, paramRef);
  }
  
  public static int requestContentLength(URL paramURL)
  {
    try
    {
      int i = ((HttpURLConnection)paramURL.openConnection()).getContentLength();
      return i;
    }
    catch (Throwable localThrowable)
    {
      Log.e(TAG, "request - Fail to connect to URL: " + paramURL, localThrowable);
    }
    return -1;
  }
  
  public static class WebResponse
  {
    private final String m_ContentType;
    private final byte[] m_Data;
    private final byte[] m_ErrorData;
    private final int m_HttpStatus;
    private boolean m_IsCanceled;
    
    private WebResponse(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString, int paramInt, boolean paramBoolean)
    {
      this.m_ErrorData = paramArrayOfByte2;
      this.m_Data = paramArrayOfByte1;
      this.m_ContentType = paramString;
      this.m_HttpStatus = paramInt;
      this.m_IsCanceled = paramBoolean;
    }
    
    public String getContentType()
    {
      return this.m_ContentType;
    }
    
    public String getDecodedString()
    {
      if (this.m_Data == null) {
        return null;
      }
      try
      {
        String str = URLDecoder.decode(new String(this.m_Data, "UTF-8"), "UTF-8");
        return str;
      }
      catch (Throwable localThrowable)
      {
        Log.e(WebUtils.-get0(), "getDecodedString() - Error to decode", localThrowable);
      }
      return null;
    }
    
    public String getErrorMessage()
    {
      if (this.m_ErrorData == null) {
        return null;
      }
      try
      {
        String str = new String(this.m_ErrorData, "UTF-8");
        return str;
      }
      catch (Throwable localThrowable)
      {
        Log.e(WebUtils.-get0(), "getErrorMessage() - Unsupported string format", localThrowable);
      }
      return null;
    }
    
    public int getHttpStatus()
    {
      return this.m_HttpStatus;
    }
    
    public byte[] getRawResponse()
    {
      return this.m_Data;
    }
    
    public String getString()
    {
      if (this.m_Data == null) {
        return null;
      }
      try
      {
        String str = new String(this.m_Data, "UTF-8");
        return str;
      }
      catch (Throwable localThrowable)
      {
        Log.e(WebUtils.-get0(), "getString() - Unsupported string format", localThrowable);
      }
      return null;
    }
    
    public boolean isCanceled()
    {
      return this.m_IsCanceled;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/util/WebUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */