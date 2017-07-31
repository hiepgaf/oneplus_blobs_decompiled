package com.aps;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Process;
import java.io.File;

public final class ax
{
  private Context a = null;
  private boolean b = true;
  private int c = 1270;
  private int d = 310;
  private int e = 4;
  private int f = 200;
  private int g = 1;
  private int h = 0;
  private int i = 0;
  private long j = 0L;
  private aw k = null;
  
  private ax(Context paramContext)
  {
    this.a = paramContext;
  }
  
  private static int a(byte[] paramArrayOfByte, int paramInt)
  {
    int m = 0;
    int n = 0;
    for (;;)
    {
      if (m >= 4) {
        return n;
      }
      n += ((paramArrayOfByte[(m + paramInt)] & 0xFF) << (m << 3));
      m += 1;
    }
  }
  
  /* Error */
  protected static ax a(Context paramContext)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: new 2	com/aps/ax
    //   6: dup
    //   7: aload_0
    //   8: invokespecial 54	com/aps/ax:<init>	(Landroid/content/Context;)V
    //   11: astore 5
    //   13: aload 5
    //   15: iconst_0
    //   16: putfield 41	com/aps/ax:h	I
    //   19: aload 5
    //   21: iconst_0
    //   22: putfield 43	com/aps/ax:i	I
    //   25: aload 5
    //   27: invokestatic 60	java/lang/System:currentTimeMillis	()J
    //   30: ldc2_w 61
    //   33: ladd
    //   34: ldc2_w 63
    //   37: ldiv
    //   38: ldc2_w 63
    //   41: lmul
    //   42: putfield 45	com/aps/ax:j	J
    //   45: new 66	java/io/FileInputStream
    //   48: dup
    //   49: new 68	java/io/File
    //   52: dup
    //   53: new 70	java/lang/StringBuilder
    //   56: dup
    //   57: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   60: aload_0
    //   61: invokestatic 74	com/aps/ax:b	(Landroid/content/Context;)Ljava/lang/String;
    //   64: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: getstatic 82	java/io/File:separator	Ljava/lang/String;
    //   70: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   73: ldc 84
    //   75: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   78: invokevirtual 88	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   81: invokespecial 91	java/io/File:<init>	(Ljava/lang/String;)V
    //   84: invokespecial 94	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   87: astore_0
    //   88: new 96	java/io/ByteArrayOutputStream
    //   91: dup
    //   92: invokespecial 97	java/io/ByteArrayOutputStream:<init>	()V
    //   95: astore 6
    //   97: bipush 32
    //   99: newarray <illegal type>
    //   101: astore 7
    //   103: aload_0
    //   104: aload 7
    //   106: invokevirtual 101	java/io/FileInputStream:read	([B)I
    //   109: istore_1
    //   110: iload_1
    //   111: iconst_m1
    //   112: if_icmpne +32 -> 144
    //   115: aload 6
    //   117: invokevirtual 104	java/io/ByteArrayOutputStream:flush	()V
    //   120: aload 6
    //   122: invokevirtual 108	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   125: astore 7
    //   127: aload 7
    //   129: ifnonnull +44 -> 173
    //   132: aload 6
    //   134: invokevirtual 111	java/io/ByteArrayOutputStream:close	()V
    //   137: aload_0
    //   138: invokevirtual 112	java/io/FileInputStream:close	()V
    //   141: aload 5
    //   143: areturn
    //   144: aload 6
    //   146: aload 7
    //   148: iconst_0
    //   149: iload_1
    //   150: invokevirtual 116	java/io/ByteArrayOutputStream:write	([BII)V
    //   153: goto -50 -> 103
    //   156: astore 6
    //   158: aload_0
    //   159: ifnull -18 -> 141
    //   162: aload_0
    //   163: invokevirtual 112	java/io/FileInputStream:close	()V
    //   166: aload 5
    //   168: areturn
    //   169: astore_0
    //   170: aload 5
    //   172: areturn
    //   173: aload 7
    //   175: arraylength
    //   176: bipush 22
    //   178: if_icmplt -46 -> 132
    //   181: aload 7
    //   183: iconst_0
    //   184: baload
    //   185: ifne +147 -> 332
    //   188: iconst_0
    //   189: istore_2
    //   190: aload 5
    //   192: iload_2
    //   193: putfield 29	com/aps/ax:b	Z
    //   196: aload 5
    //   198: aload 7
    //   200: iconst_1
    //   201: baload
    //   202: bipush 10
    //   204: imul
    //   205: bipush 10
    //   207: ishl
    //   208: putfield 31	com/aps/ax:c	I
    //   211: aload 5
    //   213: aload 7
    //   215: iconst_2
    //   216: baload
    //   217: bipush 10
    //   219: imul
    //   220: bipush 10
    //   222: ishl
    //   223: putfield 33	com/aps/ax:d	I
    //   226: aload 5
    //   228: aload 7
    //   230: iconst_3
    //   231: baload
    //   232: putfield 35	com/aps/ax:e	I
    //   235: aload 5
    //   237: aload 7
    //   239: iconst_4
    //   240: baload
    //   241: bipush 10
    //   243: imul
    //   244: putfield 37	com/aps/ax:f	I
    //   247: aload 5
    //   249: aload 7
    //   251: iconst_5
    //   252: baload
    //   253: putfield 39	com/aps/ax:g	I
    //   256: aload 7
    //   258: bipush 14
    //   260: invokestatic 119	com/aps/ax:b	([BI)J
    //   263: lstore_3
    //   264: aload 5
    //   266: getfield 45	com/aps/ax:j	J
    //   269: lload_3
    //   270: lsub
    //   271: ldc2_w 63
    //   274: lcmp
    //   275: iflt +62 -> 337
    //   278: iconst_1
    //   279: istore_1
    //   280: iload_1
    //   281: ifne -149 -> 132
    //   284: aload 5
    //   286: lload_3
    //   287: putfield 45	com/aps/ax:j	J
    //   290: aload 5
    //   292: aload 7
    //   294: bipush 6
    //   296: invokestatic 121	com/aps/ax:a	([BI)I
    //   299: putfield 41	com/aps/ax:h	I
    //   302: aload 5
    //   304: aload 7
    //   306: bipush 10
    //   308: invokestatic 121	com/aps/ax:a	([BI)I
    //   311: putfield 43	com/aps/ax:i	I
    //   314: goto -182 -> 132
    //   317: astore 5
    //   319: aload_0
    //   320: astore 6
    //   322: aload 5
    //   324: astore_0
    //   325: aload 6
    //   327: ifnonnull +19 -> 346
    //   330: aload_0
    //   331: athrow
    //   332: iconst_1
    //   333: istore_2
    //   334: goto -144 -> 190
    //   337: iconst_0
    //   338: istore_1
    //   339: goto -59 -> 280
    //   342: astore_0
    //   343: aload 5
    //   345: areturn
    //   346: aload 6
    //   348: invokevirtual 112	java/io/FileInputStream:close	()V
    //   351: goto -21 -> 330
    //   354: astore 5
    //   356: goto -26 -> 330
    //   359: astore_0
    //   360: goto -35 -> 325
    //   363: astore_0
    //   364: aconst_null
    //   365: astore_0
    //   366: goto -208 -> 158
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	369	0	paramContext	Context
    //   109	230	1	m	int
    //   189	145	2	bool	boolean
    //   263	24	3	l	long
    //   11	292	5	localax1	ax
    //   317	27	5	localax2	ax
    //   354	1	5	localException1	Exception
    //   1	144	6	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   156	1	6	localException2	Exception
    //   320	27	6	localContext	Context
    //   101	204	7	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   88	103	156	java/lang/Exception
    //   103	110	156	java/lang/Exception
    //   115	127	156	java/lang/Exception
    //   132	137	156	java/lang/Exception
    //   144	153	156	java/lang/Exception
    //   173	181	156	java/lang/Exception
    //   190	278	156	java/lang/Exception
    //   284	314	156	java/lang/Exception
    //   162	166	169	java/lang/Exception
    //   88	103	317	finally
    //   103	110	317	finally
    //   115	127	317	finally
    //   132	137	317	finally
    //   144	153	317	finally
    //   173	181	317	finally
    //   190	278	317	finally
    //   284	314	317	finally
    //   137	141	342	java/lang/Exception
    //   346	351	354	java/lang/Exception
    //   45	88	359	finally
    //   45	88	363	java/lang/Exception
  }
  
  private static byte[] a(long paramLong)
  {
    byte[] arrayOfByte = new byte[8];
    int m = 0;
    for (;;)
    {
      if (m >= 8) {
        return arrayOfByte;
      }
      arrayOfByte[m] = ((byte)(byte)(int)(paramLong >> (m << 3) & 0xFF));
      m += 1;
    }
  }
  
  private static long b(byte[] paramArrayOfByte, int paramInt)
  {
    paramInt = 0;
    int m = 0;
    for (;;)
    {
      if (paramInt >= 8) {
        return m;
      }
      m += ((paramArrayOfByte[(paramInt + 14)] & 0xFF) << (paramInt << 3));
      paramInt += 1;
    }
  }
  
  private static String b(Context paramContext)
  {
    File localFile = null;
    int m = 0;
    if (Process.myUid() == 1000) {}
    try
    {
      boolean bool = "mounted".equals(Environment.getExternalStorageState());
      m = bool;
    }
    catch (Exception localException)
    {
      label28:
      for (;;) {}
    }
    if (m != 0)
    {
      if (localFile != null) {}
    }
    else {
      for (;;)
      {
        return paramContext.getFilesDir().getPath();
        localFile = ah.a(paramContext);
        break;
        if (!ah.c()) {
          break label28;
        }
      }
    }
    return localFile.getPath();
  }
  
  private static byte[] c(int paramInt)
  {
    byte[] arrayOfByte = new byte[4];
    int m = 0;
    for (;;)
    {
      if (m >= 4) {
        return arrayOfByte;
      }
      arrayOfByte[m] = ((byte)(byte)(paramInt >> (m << 3)));
      m += 1;
    }
  }
  
  private void g()
  {
    long l = System.currentTimeMillis() + 28800000L;
    if (l - this.j <= 86400000L) {}
    for (int m = 1;; m = 0)
    {
      if (m == 0)
      {
        this.j = (l / 86400000L * 86400000L);
        this.h = 0;
        this.i = 0;
      }
      return;
    }
  }
  
  protected final void a(int paramInt)
  {
    g();
    if (paramInt >= 0) {}
    for (;;)
    {
      this.h = paramInt;
      return;
      paramInt = 0;
    }
  }
  
  protected final void a(aw paramaw)
  {
    this.k = paramaw;
  }
  
  protected final boolean a()
  {
    g();
    NetworkInfo localNetworkInfo = ((ConnectivityManager)this.a.getSystemService("connectivity")).getActiveNetworkInfo();
    if (localNetworkInfo == null) {}
    while (!localNetworkInfo.isConnected()) {
      return this.b;
    }
    if (localNetworkInfo.getType() != 1) {
      if (this.b) {
        break label75;
      }
    }
    label75:
    while (this.i >= this.d)
    {
      return false;
      if (!this.b) {}
      while (this.h >= this.c) {
        return false;
      }
      return true;
    }
    return true;
  }
  
  /* Error */
  protected final boolean a(String paramString)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: new 188	org/json/JSONObject
    //   5: dup
    //   6: aload_1
    //   7: invokespecial 189	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   10: astore_1
    //   11: aload_1
    //   12: ldc -66
    //   14: invokevirtual 193	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   17: ifne +335 -> 352
    //   20: aload_1
    //   21: ldc -62
    //   23: invokevirtual 193	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   26: ifne +347 -> 373
    //   29: aload_1
    //   30: ldc -60
    //   32: invokevirtual 193	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   35: istore 4
    //   37: iload 4
    //   39: ifne +445 -> 484
    //   42: iconst_0
    //   43: istore 4
    //   45: aload_0
    //   46: invokespecial 164	com/aps/ax:g	()V
    //   49: new 198	java/io/FileOutputStream
    //   52: dup
    //   53: new 68	java/io/File
    //   56: dup
    //   57: new 70	java/lang/StringBuilder
    //   60: dup
    //   61: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   64: aload_0
    //   65: getfield 27	com/aps/ax:a	Landroid/content/Context;
    //   68: invokestatic 74	com/aps/ax:b	(Landroid/content/Context;)Ljava/lang/String;
    //   71: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: getstatic 82	java/io/File:separator	Ljava/lang/String;
    //   77: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: ldc 84
    //   82: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   85: invokevirtual 88	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   88: invokespecial 91	java/io/File:<init>	(Ljava/lang/String;)V
    //   91: invokespecial 199	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   94: astore_1
    //   95: aload_0
    //   96: getfield 41	com/aps/ax:h	I
    //   99: invokestatic 201	com/aps/ax:c	(I)[B
    //   102: astore 5
    //   104: aload_0
    //   105: getfield 43	com/aps/ax:i	I
    //   108: invokestatic 201	com/aps/ax:c	(I)[B
    //   111: astore 6
    //   113: aload_0
    //   114: getfield 45	com/aps/ax:j	J
    //   117: invokestatic 203	com/aps/ax:a	(J)[B
    //   120: astore 7
    //   122: aload_0
    //   123: getfield 29	com/aps/ax:b	Z
    //   126: ifne +375 -> 501
    //   129: aload_1
    //   130: bipush 22
    //   132: newarray <illegal type>
    //   134: dup
    //   135: iconst_0
    //   136: iload_2
    //   137: i2b
    //   138: i2b
    //   139: bastore
    //   140: dup
    //   141: iconst_1
    //   142: aload_0
    //   143: getfield 31	com/aps/ax:c	I
    //   146: sipush 10240
    //   149: idiv
    //   150: i2b
    //   151: i2b
    //   152: bastore
    //   153: dup
    //   154: iconst_2
    //   155: aload_0
    //   156: getfield 33	com/aps/ax:d	I
    //   159: sipush 10240
    //   162: idiv
    //   163: i2b
    //   164: i2b
    //   165: bastore
    //   166: dup
    //   167: iconst_3
    //   168: aload_0
    //   169: getfield 35	com/aps/ax:e	I
    //   172: i2b
    //   173: i2b
    //   174: bastore
    //   175: dup
    //   176: iconst_4
    //   177: aload_0
    //   178: getfield 37	com/aps/ax:f	I
    //   181: bipush 10
    //   183: idiv
    //   184: i2b
    //   185: i2b
    //   186: bastore
    //   187: dup
    //   188: iconst_5
    //   189: aload_0
    //   190: getfield 39	com/aps/ax:g	I
    //   193: i2b
    //   194: i2b
    //   195: bastore
    //   196: dup
    //   197: bipush 6
    //   199: aload 5
    //   201: iconst_0
    //   202: baload
    //   203: i2b
    //   204: bastore
    //   205: dup
    //   206: bipush 7
    //   208: aload 5
    //   210: iconst_1
    //   211: baload
    //   212: i2b
    //   213: bastore
    //   214: dup
    //   215: bipush 8
    //   217: aload 5
    //   219: iconst_2
    //   220: baload
    //   221: i2b
    //   222: bastore
    //   223: dup
    //   224: bipush 9
    //   226: aload 5
    //   228: iconst_3
    //   229: baload
    //   230: i2b
    //   231: bastore
    //   232: dup
    //   233: bipush 10
    //   235: aload 6
    //   237: iconst_0
    //   238: baload
    //   239: i2b
    //   240: bastore
    //   241: dup
    //   242: bipush 11
    //   244: aload 6
    //   246: iconst_1
    //   247: baload
    //   248: i2b
    //   249: bastore
    //   250: dup
    //   251: bipush 12
    //   253: aload 6
    //   255: iconst_2
    //   256: baload
    //   257: i2b
    //   258: bastore
    //   259: dup
    //   260: bipush 13
    //   262: aload 6
    //   264: iconst_3
    //   265: baload
    //   266: i2b
    //   267: bastore
    //   268: dup
    //   269: bipush 14
    //   271: aload 7
    //   273: iconst_0
    //   274: baload
    //   275: i2b
    //   276: bastore
    //   277: dup
    //   278: bipush 15
    //   280: aload 7
    //   282: iconst_1
    //   283: baload
    //   284: i2b
    //   285: bastore
    //   286: dup
    //   287: bipush 16
    //   289: aload 7
    //   291: iconst_2
    //   292: baload
    //   293: i2b
    //   294: bastore
    //   295: dup
    //   296: bipush 17
    //   298: aload 7
    //   300: iconst_3
    //   301: baload
    //   302: i2b
    //   303: bastore
    //   304: dup
    //   305: bipush 18
    //   307: aload 7
    //   309: iconst_4
    //   310: baload
    //   311: i2b
    //   312: bastore
    //   313: dup
    //   314: bipush 19
    //   316: aload 7
    //   318: iconst_5
    //   319: baload
    //   320: i2b
    //   321: bastore
    //   322: dup
    //   323: bipush 20
    //   325: aload 7
    //   327: bipush 6
    //   329: baload
    //   330: i2b
    //   331: bastore
    //   332: dup
    //   333: bipush 21
    //   335: aload 7
    //   337: bipush 7
    //   339: baload
    //   340: i2b
    //   341: bastore
    //   342: invokevirtual 206	java/io/FileOutputStream:write	([B)V
    //   345: aload_1
    //   346: invokevirtual 207	java/io/FileOutputStream:close	()V
    //   349: iload 4
    //   351: ireturn
    //   352: aload_1
    //   353: ldc -66
    //   355: invokevirtual 211	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   358: ifne +206 -> 564
    //   361: iconst_0
    //   362: istore 4
    //   364: aload_0
    //   365: iload 4
    //   367: putfield 29	com/aps/ax:b	Z
    //   370: goto -350 -> 20
    //   373: aload_1
    //   374: ldc -62
    //   376: invokevirtual 211	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   379: istore_3
    //   380: aload_0
    //   381: iload_3
    //   382: bipush 127
    //   384: iand
    //   385: bipush 10
    //   387: imul
    //   388: bipush 10
    //   390: ishl
    //   391: putfield 31	com/aps/ax:c	I
    //   394: aload_0
    //   395: iload_3
    //   396: sipush 3968
    //   399: iand
    //   400: bipush 7
    //   402: ishr
    //   403: bipush 10
    //   405: imul
    //   406: bipush 10
    //   408: ishl
    //   409: putfield 33	com/aps/ax:d	I
    //   412: aload_0
    //   413: ldc -44
    //   415: iload_3
    //   416: iand
    //   417: bipush 12
    //   419: ishr
    //   420: putfield 35	com/aps/ax:e	I
    //   423: aload_0
    //   424: ldc -43
    //   426: iload_3
    //   427: iand
    //   428: bipush 19
    //   430: ishr
    //   431: bipush 10
    //   433: imul
    //   434: putfield 37	com/aps/ax:f	I
    //   437: aload_0
    //   438: iload_3
    //   439: ldc -42
    //   441: iand
    //   442: bipush 26
    //   444: ishr
    //   445: putfield 39	com/aps/ax:g	I
    //   448: aload_0
    //   449: getfield 39	com/aps/ax:g	I
    //   452: bipush 31
    //   454: if_icmpeq +20 -> 474
    //   457: aload_0
    //   458: getfield 47	com/aps/ax:k	Lcom/aps/aw;
    //   461: ifnull -432 -> 29
    //   464: aload_0
    //   465: getfield 47	com/aps/ax:k	Lcom/aps/aw;
    //   468: invokevirtual 218	com/aps/aw:a	()V
    //   471: goto -442 -> 29
    //   474: aload_0
    //   475: sipush 1500
    //   478: putfield 39	com/aps/ax:g	I
    //   481: goto -24 -> 457
    //   484: aload_1
    //   485: ldc -60
    //   487: invokevirtual 211	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   490: istore_3
    //   491: iload_3
    //   492: ifeq -450 -> 42
    //   495: iconst_1
    //   496: istore 4
    //   498: goto -453 -> 45
    //   501: iconst_1
    //   502: istore_2
    //   503: goto -374 -> 129
    //   506: astore_1
    //   507: iload 4
    //   509: ireturn
    //   510: astore_1
    //   511: aconst_null
    //   512: astore_1
    //   513: aload_1
    //   514: ifnull -165 -> 349
    //   517: aload_1
    //   518: invokevirtual 207	java/io/FileOutputStream:close	()V
    //   521: iload 4
    //   523: ireturn
    //   524: astore_1
    //   525: iload 4
    //   527: ireturn
    //   528: astore 5
    //   530: aconst_null
    //   531: astore_1
    //   532: aload_1
    //   533: ifnonnull +6 -> 539
    //   536: aload 5
    //   538: athrow
    //   539: aload_1
    //   540: invokevirtual 207	java/io/FileOutputStream:close	()V
    //   543: goto -7 -> 536
    //   546: astore_1
    //   547: goto -11 -> 536
    //   550: astore 5
    //   552: goto -20 -> 532
    //   555: astore 5
    //   557: goto -44 -> 513
    //   560: astore_1
    //   561: goto -519 -> 42
    //   564: iconst_1
    //   565: istore 4
    //   567: goto -203 -> 364
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	570	0	this	ax
    //   0	570	1	paramString	String
    //   1	502	2	m	int
    //   379	113	3	n	int
    //   35	531	4	bool	boolean
    //   102	125	5	arrayOfByte1	byte[]
    //   528	9	5	localObject1	Object
    //   550	1	5	localObject2	Object
    //   555	1	5	localException	Exception
    //   111	152	6	arrayOfByte2	byte[]
    //   120	216	7	arrayOfByte3	byte[]
    // Exception table:
    //   from	to	target	type
    //   345	349	506	java/lang/Exception
    //   45	95	510	java/lang/Exception
    //   517	521	524	java/lang/Exception
    //   45	95	528	finally
    //   539	543	546	java/lang/Exception
    //   95	129	550	finally
    //   129	345	550	finally
    //   95	129	555	java/lang/Exception
    //   129	345	555	java/lang/Exception
    //   2	20	560	java/lang/Exception
    //   20	29	560	java/lang/Exception
    //   29	37	560	java/lang/Exception
    //   352	361	560	java/lang/Exception
    //   364	370	560	java/lang/Exception
    //   373	457	560	java/lang/Exception
    //   457	471	560	java/lang/Exception
    //   474	481	560	java/lang/Exception
    //   484	491	560	java/lang/Exception
  }
  
  protected final int b()
  {
    return this.e;
  }
  
  protected final void b(int paramInt)
  {
    g();
    if (paramInt >= 0) {}
    for (;;)
    {
      this.i = paramInt;
      return;
      paramInt = 0;
    }
  }
  
  protected final int c()
  {
    return this.f;
  }
  
  protected final int d()
  {
    return this.g;
  }
  
  protected final int e()
  {
    g();
    return this.h;
  }
  
  protected final int f()
  {
    g();
    return this.i;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ax.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */