package com.amap.api.mapcore2d;

import android.content.Context;
import android.os.Environment;
import java.io.File;

class s
{
  private Context a;
  private az b = null;
  private String c = "/sdcard/Amap/RMap";
  private final int d = 128;
  
  public s(Context paramContext, boolean paramBoolean, am paramam)
  {
    this.a = paramContext;
    if (paramam != null)
    {
      if (paramBoolean == true) {
        break label55;
      }
      if (paramam.m != null) {
        break label67;
      }
      paramBoolean = bool;
    }
    while (paramBoolean)
    {
      return;
      return;
      label55:
      this.c = paramContext.getFilesDir().getPath();
      return;
      label67:
      paramBoolean = bool;
      if (!paramam.m.equals(""))
      {
        paramContext = new File(paramam.m);
        paramBoolean = paramContext.exists();
        if (paramBoolean) {}
        for (;;)
        {
          this.c = paramam.m;
          break;
          paramBoolean = paramContext.mkdirs();
        }
      }
    }
    this.c = a(this.a, this.c, paramam);
  }
  
  private int a(int paramInt1, int paramInt2)
  {
    return paramInt1 % 128 * 128 + paramInt2 % 128;
  }
  
  public static String a(Context paramContext, String paramString, am paramam)
  {
    if (Environment.getExternalStorageState().equals("mounted"))
    {
      paramContext = new File(cj.b(paramContext), paramam.b);
      if (!paramContext.exists()) {
        break label65;
      }
    }
    for (;;)
    {
      return paramContext.toString() + "/";
      return paramContext.getFilesDir().getPath();
      label65:
      paramContext.mkdir();
    }
  }
  
  private void a(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {}
    while (paramArrayOfByte.length != 4) {
      return;
    }
    int i = paramArrayOfByte[0];
    paramArrayOfByte[0] = ((byte)paramArrayOfByte[3]);
    paramArrayOfByte[3] = ((byte)i);
    i = paramArrayOfByte[1];
    paramArrayOfByte[1] = ((byte)paramArrayOfByte[2]);
    paramArrayOfByte[2] = ((byte)i);
  }
  
  private byte[] a(int paramInt)
  {
    return new byte[] { (byte)(byte)(paramInt & 0xFF), (byte)(byte)((0xFF00 & paramInt) >> 8), (byte)(byte)((0xFF0000 & paramInt) >> 16), (byte)(byte)((0xFF000000 & paramInt) >> 24) };
  }
  
  private String[] a(bp parambp, boolean paramBoolean)
  {
    int j = parambp.b / 128;
    int i = parambp.c / 128;
    j /= 10;
    i /= 10;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(this.c);
    localStringBuilder.append("/");
    localStringBuilder.append(parambp.d);
    localStringBuilder.append("/");
    localStringBuilder.append(j);
    localStringBuilder.append("/");
    localStringBuilder.append(i);
    localStringBuilder.append("/");
    if (paramBoolean) {}
    for (;;)
    {
      localStringBuilder.append(parambp.c());
      return new String[] { localStringBuilder.toString() + ".idx", localStringBuilder.toString() + ".dat" };
      File localFile = new File(localStringBuilder.toString());
      if (!localFile.exists()) {
        localFile.mkdirs();
      }
    }
  }
  
  private int b(byte[] paramArrayOfByte)
  {
    return paramArrayOfByte[0] & 0xFF | paramArrayOfByte[1] << 8 & 0xFF00 | paramArrayOfByte[2] << 16 & 0xFF0000 | paramArrayOfByte[3] << 24 & 0xFF000000;
  }
  
  /* Error */
  public int a(bp parambp)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: iconst_1
    //   3: invokespecial 132	com/amap/api/mapcore2d/s:a	(Lcom/amap/api/mapcore2d/bp;Z)[Ljava/lang/String;
    //   6: astore 6
    //   8: aload 6
    //   10: ifnonnull +5 -> 15
    //   13: iconst_m1
    //   14: ireturn
    //   15: aload 6
    //   17: arraylength
    //   18: iconst_2
    //   19: if_icmpne -6 -> 13
    //   22: aload 6
    //   24: iconst_0
    //   25: aaload
    //   26: ldc 46
    //   28: invokevirtual 52	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   31: ifne -18 -> 13
    //   34: aload 6
    //   36: aload 6
    //   38: arraylength
    //   39: anewarray 48	java/lang/String
    //   42: invokestatic 137	java/util/Arrays:equals	([Ljava/lang/Object;[Ljava/lang/Object;)Z
    //   45: ifne -32 -> 13
    //   48: new 40	java/io/File
    //   51: dup
    //   52: aload 6
    //   54: iconst_0
    //   55: aaload
    //   56: invokespecial 55	java/io/File:<init>	(Ljava/lang/String;)V
    //   59: astore 5
    //   61: aload 5
    //   63: invokevirtual 59	java/io/File:exists	()Z
    //   66: ifeq +168 -> 234
    //   69: aload_0
    //   70: aload_1
    //   71: getfield 110	com/amap/api/mapcore2d/bp:b	I
    //   74: aload_1
    //   75: getfield 112	com/amap/api/mapcore2d/bp:c	I
    //   78: invokespecial 139	com/amap/api/mapcore2d/s:a	(II)I
    //   81: istore_2
    //   82: iload_2
    //   83: iflt +153 -> 236
    //   86: new 141	java/io/RandomAccessFile
    //   89: dup
    //   90: aload 5
    //   92: ldc -113
    //   94: invokespecial 146	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   97: astore 5
    //   99: aload 5
    //   101: ifnull +154 -> 255
    //   104: iload_2
    //   105: iconst_4
    //   106: imul
    //   107: i2l
    //   108: lstore_3
    //   109: aload 5
    //   111: lload_3
    //   112: invokevirtual 150	java/io/RandomAccessFile:seek	(J)V
    //   115: iconst_4
    //   116: newarray <illegal type>
    //   118: astore 7
    //   120: aload 5
    //   122: aload 7
    //   124: iconst_0
    //   125: iconst_4
    //   126: invokevirtual 154	java/io/RandomAccessFile:read	([BII)I
    //   129: pop
    //   130: aload_0
    //   131: aload 7
    //   133: invokespecial 156	com/amap/api/mapcore2d/s:a	([B)V
    //   136: aload_0
    //   137: aload 7
    //   139: invokespecial 158	com/amap/api/mapcore2d/s:b	([B)I
    //   142: istore_2
    //   143: aload 5
    //   145: invokevirtual 161	java/io/RandomAccessFile:close	()V
    //   148: iload_2
    //   149: iflt +150 -> 299
    //   152: new 40	java/io/File
    //   155: dup
    //   156: aload 6
    //   158: iconst_1
    //   159: aaload
    //   160: invokespecial 55	java/io/File:<init>	(Ljava/lang/String;)V
    //   163: astore 5
    //   165: aload 5
    //   167: invokevirtual 59	java/io/File:exists	()Z
    //   170: ifeq +131 -> 301
    //   173: new 141	java/io/RandomAccessFile
    //   176: dup
    //   177: aload 5
    //   179: ldc -113
    //   181: invokespecial 146	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   184: astore 6
    //   186: aload 6
    //   188: ifnull +132 -> 320
    //   191: iload_2
    //   192: i2l
    //   193: lstore_3
    //   194: aload 6
    //   196: lload_3
    //   197: invokevirtual 150	java/io/RandomAccessFile:seek	(J)V
    //   200: aload 6
    //   202: aload 7
    //   204: iconst_0
    //   205: iconst_4
    //   206: invokevirtual 154	java/io/RandomAccessFile:read	([BII)I
    //   209: pop
    //   210: aload_0
    //   211: aload 7
    //   213: invokespecial 156	com/amap/api/mapcore2d/s:a	([B)V
    //   216: aload_0
    //   217: aload 7
    //   219: invokespecial 158	com/amap/api/mapcore2d/s:b	([B)I
    //   222: istore_2
    //   223: iload_2
    //   224: ifgt +126 -> 350
    //   227: aload 6
    //   229: invokevirtual 161	java/io/RandomAccessFile:close	()V
    //   232: iconst_m1
    //   233: ireturn
    //   234: iconst_m1
    //   235: ireturn
    //   236: iconst_m1
    //   237: ireturn
    //   238: astore 5
    //   240: aload 5
    //   242: ldc -93
    //   244: ldc -91
    //   246: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   249: aconst_null
    //   250: astore 5
    //   252: goto -153 -> 99
    //   255: iconst_m1
    //   256: ireturn
    //   257: astore 7
    //   259: aload 7
    //   261: ldc -93
    //   263: ldc -91
    //   265: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   268: goto -153 -> 115
    //   271: astore 8
    //   273: aload 8
    //   275: ldc -93
    //   277: ldc -91
    //   279: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   282: goto -152 -> 130
    //   285: astore 5
    //   287: aload 5
    //   289: ldc -93
    //   291: ldc -91
    //   293: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   296: goto -148 -> 148
    //   299: iconst_m1
    //   300: ireturn
    //   301: iconst_m1
    //   302: ireturn
    //   303: astore 5
    //   305: aload 5
    //   307: ldc -93
    //   309: ldc -91
    //   311: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   314: aconst_null
    //   315: astore 6
    //   317: goto -131 -> 186
    //   320: iconst_m1
    //   321: ireturn
    //   322: astore 5
    //   324: aload 5
    //   326: ldc -93
    //   328: ldc -91
    //   330: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   333: goto -133 -> 200
    //   336: astore 5
    //   338: aload 5
    //   340: ldc -93
    //   342: ldc -91
    //   344: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   347: goto -137 -> 210
    //   350: iload_2
    //   351: ldc -87
    //   353: if_icmpgt -126 -> 227
    //   356: iload_2
    //   357: newarray <illegal type>
    //   359: astore 5
    //   361: aload 6
    //   363: aload 5
    //   365: iconst_0
    //   366: iload_2
    //   367: invokevirtual 154	java/io/RandomAccessFile:read	([BII)I
    //   370: pop
    //   371: aload 6
    //   373: invokevirtual 161	java/io/RandomAccessFile:close	()V
    //   376: aload_0
    //   377: getfield 19	com/amap/api/mapcore2d/s:b	Lcom/amap/api/mapcore2d/az;
    //   380: ifnull +62 -> 442
    //   383: aload_0
    //   384: getfield 19	com/amap/api/mapcore2d/s:b	Lcom/amap/api/mapcore2d/az;
    //   387: aload 5
    //   389: aconst_null
    //   390: iconst_1
    //   391: aconst_null
    //   392: aload_1
    //   393: invokevirtual 118	com/amap/api/mapcore2d/bp:c	()Ljava/lang/String;
    //   396: invokevirtual 174	com/amap/api/mapcore2d/az:a	([B[BZLjava/util/List;Ljava/lang/String;)I
    //   399: ireturn
    //   400: astore_1
    //   401: aload_1
    //   402: ldc -93
    //   404: ldc -91
    //   406: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   409: iconst_m1
    //   410: ireturn
    //   411: astore 7
    //   413: aconst_null
    //   414: astore 5
    //   416: aload 7
    //   418: ldc -93
    //   420: ldc -91
    //   422: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   425: goto -54 -> 371
    //   428: astore 6
    //   430: aload 6
    //   432: ldc -93
    //   434: ldc -91
    //   436: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   439: goto -63 -> 376
    //   442: iconst_m1
    //   443: ireturn
    //   444: astore 7
    //   446: goto -30 -> 416
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	449	0	this	s
    //   0	449	1	parambp	bp
    //   81	286	2	i	int
    //   108	89	3	l	long
    //   59	119	5	localObject1	Object
    //   238	3	5	localFileNotFoundException1	java.io.FileNotFoundException
    //   250	1	5	localObject2	Object
    //   285	3	5	localThrowable1	Throwable
    //   303	3	5	localFileNotFoundException2	java.io.FileNotFoundException
    //   322	3	5	localIOException1	java.io.IOException
    //   336	3	5	localIOException2	java.io.IOException
    //   359	56	5	arrayOfByte1	byte[]
    //   6	366	6	localObject3	Object
    //   428	3	6	localIOException3	java.io.IOException
    //   118	100	7	arrayOfByte2	byte[]
    //   257	3	7	localIOException4	java.io.IOException
    //   411	6	7	localThrowable2	Throwable
    //   444	1	7	localThrowable3	Throwable
    //   271	3	8	localIOException5	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   86	99	238	java/io/FileNotFoundException
    //   109	115	257	java/io/IOException
    //   120	130	271	java/io/IOException
    //   143	148	285	java/lang/Throwable
    //   173	186	303	java/io/FileNotFoundException
    //   194	200	322	java/io/IOException
    //   200	210	336	java/io/IOException
    //   227	232	400	java/io/IOException
    //   356	361	411	java/lang/Throwable
    //   371	376	428	java/io/IOException
    //   361	371	444	java/lang/Throwable
  }
  
  public void a(az paramaz)
  {
    this.b = paramaz;
  }
  
  /* Error */
  public boolean a(byte[] paramArrayOfByte, bp parambp)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: ifnull +27 -> 30
    //   6: aload_1
    //   7: arraylength
    //   8: istore_3
    //   9: iload_3
    //   10: ifle +24 -> 34
    //   13: aload_0
    //   14: aload_2
    //   15: iconst_0
    //   16: invokespecial 132	com/amap/api/mapcore2d/s:a	(Lcom/amap/api/mapcore2d/bp;Z)[Ljava/lang/String;
    //   19: astore 10
    //   21: aload 10
    //   23: ifnonnull +15 -> 38
    //   26: aload_0
    //   27: monitorexit
    //   28: iconst_0
    //   29: ireturn
    //   30: aload_0
    //   31: monitorexit
    //   32: iconst_0
    //   33: ireturn
    //   34: aload_0
    //   35: monitorexit
    //   36: iconst_0
    //   37: ireturn
    //   38: aload 10
    //   40: arraylength
    //   41: iconst_2
    //   42: if_icmpne -16 -> 26
    //   45: aload 10
    //   47: iconst_0
    //   48: aaload
    //   49: ldc 46
    //   51: invokevirtual 52	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   54: ifne -28 -> 26
    //   57: aload 10
    //   59: aload 10
    //   61: arraylength
    //   62: anewarray 48	java/lang/String
    //   65: invokestatic 137	java/util/Arrays:equals	([Ljava/lang/Object;[Ljava/lang/Object;)Z
    //   68: ifne -42 -> 26
    //   71: new 40	java/io/File
    //   74: dup
    //   75: aload 10
    //   77: iconst_1
    //   78: aaload
    //   79: invokespecial 55	java/io/File:<init>	(Ljava/lang/String;)V
    //   82: astore 9
    //   84: aload 9
    //   86: invokevirtual 59	java/io/File:exists	()Z
    //   89: istore 4
    //   91: iload 4
    //   93: ifeq +192 -> 285
    //   96: new 141	java/io/RandomAccessFile
    //   99: dup
    //   100: aload 9
    //   102: ldc -78
    //   104: invokespecial 146	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   107: astore 9
    //   109: aload 9
    //   111: ifnull +224 -> 335
    //   114: aload_0
    //   115: iload_3
    //   116: invokespecial 180	com/amap/api/mapcore2d/s:a	(I)[B
    //   119: astore 11
    //   121: aload_0
    //   122: aload 11
    //   124: invokespecial 156	com/amap/api/mapcore2d/s:a	([B)V
    //   127: aload 9
    //   129: invokevirtual 184	java/io/RandomAccessFile:length	()J
    //   132: lstore 5
    //   134: aload 9
    //   136: lload 5
    //   138: invokevirtual 150	java/io/RandomAccessFile:seek	(J)V
    //   141: aload 9
    //   143: aload 11
    //   145: invokevirtual 187	java/io/RandomAccessFile:write	([B)V
    //   148: aload 9
    //   150: aload_1
    //   151: invokevirtual 187	java/io/RandomAccessFile:write	([B)V
    //   154: aload 9
    //   156: invokevirtual 161	java/io/RandomAccessFile:close	()V
    //   159: new 40	java/io/File
    //   162: dup
    //   163: aload 10
    //   165: iconst_0
    //   166: aaload
    //   167: invokespecial 55	java/io/File:<init>	(Ljava/lang/String;)V
    //   170: astore_1
    //   171: aload_1
    //   172: invokevirtual 59	java/io/File:exists	()Z
    //   175: istore 4
    //   177: iload 4
    //   179: ifeq +234 -> 413
    //   182: new 141	java/io/RandomAccessFile
    //   185: dup
    //   186: aload_1
    //   187: ldc -78
    //   189: invokespecial 146	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   192: astore_1
    //   193: aload_1
    //   194: ifnull +265 -> 459
    //   197: aload_1
    //   198: invokevirtual 184	java/io/RandomAccessFile:length	()J
    //   201: lstore 7
    //   203: lload 7
    //   205: lconst_0
    //   206: lcmp
    //   207: ifne +21 -> 228
    //   210: ldc -68
    //   212: newarray <illegal type>
    //   214: astore 9
    //   216: aload 9
    //   218: iconst_m1
    //   219: invokestatic 192	java/util/Arrays:fill	([BB)V
    //   222: aload_1
    //   223: aload 9
    //   225: invokevirtual 187	java/io/RandomAccessFile:write	([B)V
    //   228: aload_0
    //   229: aload_2
    //   230: getfield 110	com/amap/api/mapcore2d/bp:b	I
    //   233: aload_2
    //   234: getfield 112	com/amap/api/mapcore2d/bp:c	I
    //   237: invokespecial 139	com/amap/api/mapcore2d/s:a	(II)I
    //   240: istore_3
    //   241: iload_3
    //   242: iflt +252 -> 494
    //   245: iload_3
    //   246: iconst_4
    //   247: imul
    //   248: i2l
    //   249: lstore 7
    //   251: aload_1
    //   252: lload 7
    //   254: invokevirtual 150	java/io/RandomAccessFile:seek	(J)V
    //   257: lload 5
    //   259: l2i
    //   260: istore_3
    //   261: aload_0
    //   262: iload_3
    //   263: invokespecial 180	com/amap/api/mapcore2d/s:a	(I)[B
    //   266: astore_2
    //   267: aload_0
    //   268: aload_2
    //   269: invokespecial 156	com/amap/api/mapcore2d/s:a	([B)V
    //   272: aload_1
    //   273: aload_2
    //   274: invokevirtual 187	java/io/RandomAccessFile:write	([B)V
    //   277: aload_1
    //   278: invokevirtual 161	java/io/RandomAccessFile:close	()V
    //   281: aload_0
    //   282: monitorexit
    //   283: iconst_1
    //   284: ireturn
    //   285: aload 9
    //   287: invokevirtual 195	java/io/File:createNewFile	()Z
    //   290: istore 4
    //   292: iload 4
    //   294: ifne -198 -> 96
    //   297: aload_0
    //   298: monitorexit
    //   299: iconst_0
    //   300: ireturn
    //   301: astore 11
    //   303: aload 11
    //   305: ldc -93
    //   307: ldc -59
    //   309: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   312: iconst_0
    //   313: istore 4
    //   315: goto -23 -> 292
    //   318: astore 9
    //   320: aload 9
    //   322: ldc -93
    //   324: ldc -59
    //   326: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   329: aconst_null
    //   330: astore 9
    //   332: goto -223 -> 109
    //   335: aload_0
    //   336: monitorexit
    //   337: iconst_0
    //   338: ireturn
    //   339: astore 12
    //   341: aload 12
    //   343: ldc -93
    //   345: ldc -59
    //   347: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   350: lconst_0
    //   351: lstore 5
    //   353: goto -219 -> 134
    //   356: astore 12
    //   358: aload 12
    //   360: ldc -93
    //   362: ldc -59
    //   364: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   367: goto -226 -> 141
    //   370: astore_1
    //   371: aload_0
    //   372: monitorexit
    //   373: aload_1
    //   374: athrow
    //   375: astore 11
    //   377: aload 11
    //   379: ldc -93
    //   381: ldc -59
    //   383: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   386: goto -238 -> 148
    //   389: astore_1
    //   390: aload_1
    //   391: ldc -93
    //   393: ldc -59
    //   395: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   398: goto -244 -> 154
    //   401: astore_1
    //   402: aload_1
    //   403: ldc -93
    //   405: ldc -59
    //   407: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   410: goto -251 -> 159
    //   413: aload_1
    //   414: invokevirtual 195	java/io/File:createNewFile	()Z
    //   417: istore 4
    //   419: iload 4
    //   421: ifne -239 -> 182
    //   424: aload_0
    //   425: monitorexit
    //   426: iconst_0
    //   427: ireturn
    //   428: astore 9
    //   430: aload 9
    //   432: ldc -93
    //   434: ldc -59
    //   436: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   439: iconst_0
    //   440: istore 4
    //   442: goto -23 -> 419
    //   445: astore_1
    //   446: aload_1
    //   447: ldc -93
    //   449: ldc -59
    //   451: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   454: aconst_null
    //   455: astore_1
    //   456: goto -263 -> 193
    //   459: aload_0
    //   460: monitorexit
    //   461: iconst_0
    //   462: ireturn
    //   463: astore 9
    //   465: aload 9
    //   467: ldc -93
    //   469: ldc -59
    //   471: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   474: lconst_0
    //   475: lstore 7
    //   477: goto -274 -> 203
    //   480: astore 9
    //   482: aload 9
    //   484: ldc -93
    //   486: ldc -59
    //   488: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   491: goto -263 -> 228
    //   494: aload_1
    //   495: invokevirtual 161	java/io/RandomAccessFile:close	()V
    //   498: aload_0
    //   499: monitorexit
    //   500: iconst_0
    //   501: ireturn
    //   502: astore_1
    //   503: aload_1
    //   504: ldc -93
    //   506: ldc -59
    //   508: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   511: goto -13 -> 498
    //   514: astore_2
    //   515: aload_2
    //   516: ldc -93
    //   518: ldc -59
    //   520: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   523: goto -266 -> 257
    //   526: astore_2
    //   527: aload_2
    //   528: ldc -93
    //   530: ldc -59
    //   532: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   535: goto -258 -> 277
    //   538: astore_1
    //   539: aload_1
    //   540: ldc -93
    //   542: ldc -59
    //   544: invokestatic 168	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   547: goto -266 -> 281
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	550	0	this	s
    //   0	550	1	paramArrayOfByte	byte[]
    //   0	550	2	parambp	bp
    //   8	255	3	i	int
    //   89	352	4	bool	boolean
    //   132	220	5	l1	long
    //   201	275	7	l2	long
    //   82	204	9	localObject1	Object
    //   318	3	9	localThrowable1	Throwable
    //   330	1	9	localObject2	Object
    //   428	3	9	localIOException	java.io.IOException
    //   463	3	9	localThrowable2	Throwable
    //   480	3	9	localThrowable3	Throwable
    //   19	145	10	arrayOfString	String[]
    //   119	25	11	arrayOfByte	byte[]
    //   301	3	11	localThrowable4	Throwable
    //   375	3	11	localThrowable5	Throwable
    //   339	3	12	localThrowable6	Throwable
    //   356	3	12	localThrowable7	Throwable
    // Exception table:
    //   from	to	target	type
    //   285	292	301	java/lang/Throwable
    //   96	109	318	java/lang/Throwable
    //   127	134	339	java/lang/Throwable
    //   134	141	356	java/lang/Throwable
    //   6	9	370	finally
    //   13	21	370	finally
    //   38	91	370	finally
    //   96	109	370	finally
    //   114	127	370	finally
    //   127	134	370	finally
    //   134	141	370	finally
    //   141	148	370	finally
    //   148	154	370	finally
    //   154	159	370	finally
    //   159	177	370	finally
    //   182	193	370	finally
    //   197	203	370	finally
    //   210	222	370	finally
    //   222	228	370	finally
    //   228	241	370	finally
    //   251	257	370	finally
    //   261	272	370	finally
    //   272	277	370	finally
    //   277	281	370	finally
    //   285	292	370	finally
    //   303	312	370	finally
    //   320	329	370	finally
    //   341	350	370	finally
    //   358	367	370	finally
    //   377	386	370	finally
    //   390	398	370	finally
    //   402	410	370	finally
    //   413	419	370	finally
    //   430	439	370	finally
    //   446	454	370	finally
    //   465	474	370	finally
    //   482	491	370	finally
    //   494	498	370	finally
    //   503	511	370	finally
    //   515	523	370	finally
    //   527	535	370	finally
    //   539	547	370	finally
    //   141	148	375	java/lang/Throwable
    //   148	154	389	java/lang/Throwable
    //   154	159	401	java/lang/Throwable
    //   413	419	428	java/io/IOException
    //   182	193	445	java/lang/Throwable
    //   197	203	463	java/lang/Throwable
    //   222	228	480	java/lang/Throwable
    //   494	498	502	java/lang/Throwable
    //   251	257	514	java/lang/Throwable
    //   272	277	526	java/lang/Throwable
    //   277	281	538	java/lang/Throwable
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/s.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */