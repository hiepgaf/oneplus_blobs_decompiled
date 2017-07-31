package com.amap.api.mapcore2d;

import android.content.Context;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ej
{
  private static boolean a = true;
  
  public static void a(Context paramContext)
  {
    try
    {
      if (g(paramContext))
      {
        Object localObject = new StringBuffer();
        ((StringBuffer)localObject).append(new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()));
        ((StringBuffer)localObject).append(" ");
        ((StringBuffer)localObject).append(UUID.randomUUID().toString());
        ((StringBuffer)localObject).append(" ");
        if (((StringBuffer)localObject).length() == 53)
        {
          localObject = cv.a(((StringBuffer)localObject).toString());
          paramContext = b(paramContext);
          byte[] arrayOfByte = new byte[localObject.length + paramContext.length];
          System.arraycopy(localObject, 0, arrayOfByte, 0, localObject.length);
          System.arraycopy(paramContext, 0, arrayOfByte, localObject.length, paramContext.length);
          paramContext = new da(cv.c(arrayOfByte), "2");
          dy.a().a(paramContext);
        }
      }
      else
      {
        return;
      }
      return;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "StatisticsManager", "updateStaticsData");
    }
  }
  
  /* Error */
  private static void a(Context paramContext, long paramLong)
  {
    // Byte code:
    //   0: new 108	java/io/File
    //   3: dup
    //   4: aload_0
    //   5: ldc 110
    //   7: invokestatic 115	com/amap/api/mapcore2d/cz:a	(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;
    //   10: invokespecial 116	java/io/File:<init>	(Ljava/lang/String;)V
    //   13: astore_0
    //   14: aload_0
    //   15: invokevirtual 120	java/io/File:getParentFile	()Ljava/io/File;
    //   18: invokevirtual 124	java/io/File:exists	()Z
    //   21: ifeq +30 -> 51
    //   24: new 126	java/io/FileOutputStream
    //   27: dup
    //   28: aload_0
    //   29: invokespecial 129	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   32: astore_3
    //   33: aload_3
    //   34: astore_0
    //   35: aload_3
    //   36: lload_1
    //   37: invokestatic 135	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   40: invokestatic 63	com/amap/api/mapcore2d/cv:a	(Ljava/lang/String;)[B
    //   43: invokevirtual 139	java/io/FileOutputStream:write	([B)V
    //   46: aload_3
    //   47: ifnonnull +76 -> 123
    //   50: return
    //   51: aload_0
    //   52: invokevirtual 120	java/io/File:getParentFile	()Ljava/io/File;
    //   55: invokevirtual 142	java/io/File:mkdirs	()Z
    //   58: pop
    //   59: goto -35 -> 24
    //   62: astore 4
    //   64: aconst_null
    //   65: astore_3
    //   66: aload_3
    //   67: astore_0
    //   68: aload 4
    //   70: invokevirtual 145	java/io/FileNotFoundException:printStackTrace	()V
    //   73: aload_3
    //   74: ifnull -24 -> 50
    //   77: aload_3
    //   78: invokevirtual 148	java/io/FileOutputStream:close	()V
    //   81: return
    //   82: astore_0
    //   83: aload_0
    //   84: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   87: return
    //   88: astore 4
    //   90: aconst_null
    //   91: astore_3
    //   92: aload_3
    //   93: astore_0
    //   94: aload 4
    //   96: invokevirtual 150	java/io/IOException:printStackTrace	()V
    //   99: aload_3
    //   100: ifnull -50 -> 50
    //   103: aload_3
    //   104: invokevirtual 148	java/io/FileOutputStream:close	()V
    //   107: return
    //   108: astore_0
    //   109: aload_0
    //   110: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   113: return
    //   114: astore_3
    //   115: aconst_null
    //   116: astore_0
    //   117: aload_0
    //   118: ifnonnull +16 -> 134
    //   121: aload_3
    //   122: athrow
    //   123: aload_3
    //   124: invokevirtual 148	java/io/FileOutputStream:close	()V
    //   127: return
    //   128: astore_0
    //   129: aload_0
    //   130: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   133: return
    //   134: aload_0
    //   135: invokevirtual 148	java/io/FileOutputStream:close	()V
    //   138: goto -17 -> 121
    //   141: astore_0
    //   142: aload_0
    //   143: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   146: goto -25 -> 121
    //   149: astore_3
    //   150: goto -33 -> 117
    //   153: astore 4
    //   155: goto -63 -> 92
    //   158: astore 4
    //   160: goto -94 -> 66
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	163	0	paramContext	Context
    //   0	163	1	paramLong	long
    //   32	72	3	localFileOutputStream	java.io.FileOutputStream
    //   114	10	3	localObject1	Object
    //   149	1	3	localObject2	Object
    //   62	7	4	localFileNotFoundException1	java.io.FileNotFoundException
    //   88	7	4	localIOException1	java.io.IOException
    //   153	1	4	localIOException2	java.io.IOException
    //   158	1	4	localFileNotFoundException2	java.io.FileNotFoundException
    // Exception table:
    //   from	to	target	type
    //   24	33	62	java/io/FileNotFoundException
    //   77	81	82	java/lang/Throwable
    //   24	33	88	java/io/IOException
    //   103	107	108	java/lang/Throwable
    //   24	33	114	finally
    //   123	127	128	java/lang/Throwable
    //   134	138	141	java/lang/Throwable
    //   35	46	149	finally
    //   68	73	149	finally
    //   94	99	149	finally
    //   35	46	153	java/io/IOException
    //   35	46	158	java/io/FileNotFoundException
  }
  
  private static byte[] a(Context paramContext, byte[] paramArrayOfByte)
  {
    try
    {
      paramContext = co.a(paramContext, paramArrayOfByte);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
    return null;
  }
  
  private static byte[] b(Context paramContext)
  {
    byte[] arrayOfByte1 = c(paramContext);
    byte[] arrayOfByte2 = e(paramContext);
    byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
    return a(paramContext, arrayOfByte3);
  }
  
  /* Error */
  private static byte[] c(Context paramContext)
  {
    // Byte code:
    //   0: new 163	java/io/ByteArrayOutputStream
    //   3: dup
    //   4: invokespecial 164	java/io/ByteArrayOutputStream:<init>	()V
    //   7: astore_1
    //   8: aload_1
    //   9: ldc -90
    //   11: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   14: aload_1
    //   15: ldc -85
    //   17: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   20: aload_1
    //   21: aload_0
    //   22: invokestatic 177	com/amap/api/mapcore2d/cp:q	(Landroid/content/Context;)Ljava/lang/String;
    //   25: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   28: aload_1
    //   29: aload_0
    //   30: invokestatic 180	com/amap/api/mapcore2d/cp:i	(Landroid/content/Context;)Ljava/lang/String;
    //   33: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   36: aload_1
    //   37: aload_0
    //   38: invokestatic 183	com/amap/api/mapcore2d/cp:f	(Landroid/content/Context;)Ljava/lang/String;
    //   41: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   44: aload_1
    //   45: getstatic 189	android/os/Build:MANUFACTURER	Ljava/lang/String;
    //   48: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   51: aload_1
    //   52: getstatic 192	android/os/Build:MODEL	Ljava/lang/String;
    //   55: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   58: aload_1
    //   59: getstatic 195	android/os/Build:DEVICE	Ljava/lang/String;
    //   62: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   65: aload_1
    //   66: aload_0
    //   67: invokestatic 198	com/amap/api/mapcore2d/cp:r	(Landroid/content/Context;)Ljava/lang/String;
    //   70: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   73: aload_1
    //   74: aload_0
    //   75: invokestatic 202	com/amap/api/mapcore2d/cl:c	(Landroid/content/Context;)Ljava/lang/String;
    //   78: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   81: aload_1
    //   82: aload_0
    //   83: invokestatic 205	com/amap/api/mapcore2d/cl:d	(Landroid/content/Context;)Ljava/lang/String;
    //   86: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   89: aload_1
    //   90: aload_0
    //   91: invokestatic 206	com/amap/api/mapcore2d/cl:f	(Landroid/content/Context;)Ljava/lang/String;
    //   94: invokestatic 169	com/amap/api/mapcore2d/cv:a	(Ljava/io/ByteArrayOutputStream;Ljava/lang/String;)V
    //   97: aload_1
    //   98: iconst_1
    //   99: newarray <illegal type>
    //   101: dup
    //   102: iconst_0
    //   103: iconst_0
    //   104: bastore
    //   105: invokevirtual 207	java/io/ByteArrayOutputStream:write	([B)V
    //   108: aload_1
    //   109: invokevirtual 211	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   112: astore_0
    //   113: aload_1
    //   114: invokevirtual 212	java/io/ByteArrayOutputStream:close	()V
    //   117: aload_0
    //   118: areturn
    //   119: astore_0
    //   120: aload_0
    //   121: ldc 94
    //   123: ldc -42
    //   125: invokestatic 101	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   128: aload_1
    //   129: invokevirtual 212	java/io/ByteArrayOutputStream:close	()V
    //   132: iconst_0
    //   133: newarray <illegal type>
    //   135: areturn
    //   136: astore_0
    //   137: aload_1
    //   138: invokevirtual 212	java/io/ByteArrayOutputStream:close	()V
    //   141: aload_0
    //   142: athrow
    //   143: astore_1
    //   144: aload_1
    //   145: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   148: aload_0
    //   149: areturn
    //   150: astore_0
    //   151: aload_0
    //   152: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   155: goto -23 -> 132
    //   158: astore_1
    //   159: aload_1
    //   160: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   163: goto -22 -> 141
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	166	0	paramContext	Context
    //   7	131	1	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   143	2	1	localThrowable1	Throwable
    //   158	2	1	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   8	113	119	java/lang/Throwable
    //   8	113	136	finally
    //   120	128	136	finally
    //   113	117	143	java/lang/Throwable
    //   128	132	150	java/lang/Throwable
    //   137	141	158	java/lang/Throwable
  }
  
  private static int d(Context paramContext)
  {
    try
    {
      paramContext = new File(cz.a(paramContext, cz.e));
      if (paramContext.exists())
      {
        int i = paramContext.list().length;
        return i;
      }
      return 0;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "StatisticsManager", "getFileNum");
    }
    return 0;
  }
  
  /* Error */
  private static byte[] e(Context paramContext)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 5
    //   8: aconst_null
    //   9: astore_3
    //   10: new 163	java/io/ByteArrayOutputStream
    //   13: dup
    //   14: invokespecial 164	java/io/ByteArrayOutputStream:<init>	()V
    //   17: astore 6
    //   19: aload_0
    //   20: getstatic 217	com/amap/api/mapcore2d/cz:e	Ljava/lang/String;
    //   23: invokestatic 115	com/amap/api/mapcore2d/cz:a	(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;
    //   26: astore 7
    //   28: aload 5
    //   30: astore_0
    //   31: new 108	java/io/File
    //   34: dup
    //   35: aload 7
    //   37: invokespecial 116	java/io/File:<init>	(Ljava/lang/String;)V
    //   40: iconst_1
    //   41: iconst_1
    //   42: ldc2_w 224
    //   45: invokestatic 230	com/amap/api/mapcore2d/du:a	(Ljava/io/File;IIJ)Lcom/amap/api/mapcore2d/du;
    //   48: astore 5
    //   50: aload 5
    //   52: astore_3
    //   53: aload 5
    //   55: astore 4
    //   57: aload 5
    //   59: astore_0
    //   60: new 108	java/io/File
    //   63: dup
    //   64: aload 7
    //   66: invokespecial 116	java/io/File:<init>	(Ljava/lang/String;)V
    //   69: astore 7
    //   71: aload 7
    //   73: ifnonnull +28 -> 101
    //   76: aload 5
    //   78: astore_3
    //   79: aload 5
    //   81: astore 4
    //   83: aload 5
    //   85: astore_0
    //   86: aload 6
    //   88: invokevirtual 211	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   91: astore 7
    //   93: aload 6
    //   95: ifnonnull +193 -> 288
    //   98: goto +302 -> 400
    //   101: aload 5
    //   103: astore_3
    //   104: aload 5
    //   106: astore 4
    //   108: aload 5
    //   110: astore_0
    //   111: aload 7
    //   113: invokevirtual 124	java/io/File:exists	()Z
    //   116: ifeq -40 -> 76
    //   119: aload 5
    //   121: astore_3
    //   122: aload 5
    //   124: astore 4
    //   126: aload 5
    //   128: astore_0
    //   129: aload 7
    //   131: invokevirtual 221	java/io/File:list	()[Ljava/lang/String;
    //   134: astore 7
    //   136: aload 5
    //   138: astore_3
    //   139: aload 5
    //   141: astore 4
    //   143: aload 5
    //   145: astore_0
    //   146: aload 7
    //   148: arraylength
    //   149: istore_2
    //   150: iload_1
    //   151: iload_2
    //   152: if_icmpge -76 -> 76
    //   155: aload 7
    //   157: iload_1
    //   158: aaload
    //   159: astore 8
    //   161: aload 5
    //   163: astore_3
    //   164: aload 5
    //   166: astore 4
    //   168: aload 5
    //   170: astore_0
    //   171: aload 8
    //   173: ldc -24
    //   175: invokevirtual 236	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   178: ifeq +32 -> 210
    //   181: aload 5
    //   183: astore_3
    //   184: aload 5
    //   186: astore 4
    //   188: aload 5
    //   190: astore_0
    //   191: aload 6
    //   193: aload 5
    //   195: aload 8
    //   197: ldc -18
    //   199: invokevirtual 242	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   202: iconst_0
    //   203: aaload
    //   204: invokestatic 247	com/amap/api/mapcore2d/ek:a	(Lcom/amap/api/mapcore2d/du;Ljava/lang/String;)[B
    //   207: invokevirtual 207	java/io/ByteArrayOutputStream:write	([B)V
    //   210: iload_1
    //   211: iconst_1
    //   212: iadd
    //   213: istore_1
    //   214: goto -64 -> 150
    //   217: astore 4
    //   219: aload_3
    //   220: astore_0
    //   221: aload 4
    //   223: ldc 94
    //   225: ldc -7
    //   227: invokestatic 101	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   230: aload 6
    //   232: ifnonnull +88 -> 320
    //   235: goto +173 -> 408
    //   238: astore_3
    //   239: aload 4
    //   241: astore_0
    //   242: aload_3
    //   243: ldc 94
    //   245: ldc -7
    //   247: invokestatic 101	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   250: aload 6
    //   252: ifnonnull +99 -> 351
    //   255: aload 4
    //   257: ifnull +155 -> 412
    //   260: aload 4
    //   262: invokevirtual 250	com/amap/api/mapcore2d/du:close	()V
    //   265: goto +147 -> 412
    //   268: astore_0
    //   269: aload_0
    //   270: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   273: goto +139 -> 412
    //   276: astore_3
    //   277: aload 6
    //   279: ifnonnull +88 -> 367
    //   282: aload_0
    //   283: ifnonnull +102 -> 385
    //   286: aload_3
    //   287: athrow
    //   288: aload 6
    //   290: invokevirtual 212	java/io/ByteArrayOutputStream:close	()V
    //   293: goto +107 -> 400
    //   296: astore_0
    //   297: aload_0
    //   298: invokevirtual 150	java/io/IOException:printStackTrace	()V
    //   301: goto +99 -> 400
    //   304: aload 5
    //   306: invokevirtual 250	com/amap/api/mapcore2d/du:close	()V
    //   309: aload 7
    //   311: areturn
    //   312: astore_0
    //   313: aload_0
    //   314: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   317: aload 7
    //   319: areturn
    //   320: aload 6
    //   322: invokevirtual 212	java/io/ByteArrayOutputStream:close	()V
    //   325: goto +83 -> 408
    //   328: astore_0
    //   329: aload_0
    //   330: invokevirtual 150	java/io/IOException:printStackTrace	()V
    //   333: goto +75 -> 408
    //   336: aload_3
    //   337: invokevirtual 250	com/amap/api/mapcore2d/du:close	()V
    //   340: goto +72 -> 412
    //   343: astore_0
    //   344: aload_0
    //   345: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   348: goto +64 -> 412
    //   351: aload 6
    //   353: invokevirtual 212	java/io/ByteArrayOutputStream:close	()V
    //   356: goto -101 -> 255
    //   359: astore_0
    //   360: aload_0
    //   361: invokevirtual 150	java/io/IOException:printStackTrace	()V
    //   364: goto -109 -> 255
    //   367: aload 6
    //   369: invokevirtual 212	java/io/ByteArrayOutputStream:close	()V
    //   372: goto -90 -> 282
    //   375: astore 4
    //   377: aload 4
    //   379: invokevirtual 150	java/io/IOException:printStackTrace	()V
    //   382: goto -100 -> 282
    //   385: aload_0
    //   386: invokevirtual 250	com/amap/api/mapcore2d/du:close	()V
    //   389: goto -103 -> 286
    //   392: astore_0
    //   393: aload_0
    //   394: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   397: goto -111 -> 286
    //   400: aload 5
    //   402: ifnonnull -98 -> 304
    //   405: aload 7
    //   407: areturn
    //   408: aload_3
    //   409: ifnonnull -73 -> 336
    //   412: iconst_0
    //   413: newarray <illegal type>
    //   415: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	416	0	paramContext	Context
    //   1	213	1	i	int
    //   149	4	2	j	int
    //   9	211	3	localdu1	du
    //   238	5	3	localThrowable	Throwable
    //   276	133	3	localObject1	Object
    //   3	184	4	localObject2	Object
    //   217	44	4	localIOException1	java.io.IOException
    //   375	3	4	localIOException2	java.io.IOException
    //   6	395	5	localdu2	du
    //   17	351	6	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   26	380	7	localObject3	Object
    //   159	37	8	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   31	50	217	java/io/IOException
    //   60	71	217	java/io/IOException
    //   86	93	217	java/io/IOException
    //   111	119	217	java/io/IOException
    //   129	136	217	java/io/IOException
    //   146	150	217	java/io/IOException
    //   171	181	217	java/io/IOException
    //   191	210	217	java/io/IOException
    //   31	50	238	java/lang/Throwable
    //   60	71	238	java/lang/Throwable
    //   86	93	238	java/lang/Throwable
    //   111	119	238	java/lang/Throwable
    //   129	136	238	java/lang/Throwable
    //   146	150	238	java/lang/Throwable
    //   171	181	238	java/lang/Throwable
    //   191	210	238	java/lang/Throwable
    //   260	265	268	java/lang/Throwable
    //   31	50	276	finally
    //   60	71	276	finally
    //   86	93	276	finally
    //   111	119	276	finally
    //   129	136	276	finally
    //   146	150	276	finally
    //   171	181	276	finally
    //   191	210	276	finally
    //   221	230	276	finally
    //   242	250	276	finally
    //   288	293	296	java/io/IOException
    //   304	309	312	java/lang/Throwable
    //   320	325	328	java/io/IOException
    //   336	340	343	java/lang/Throwable
    //   351	356	359	java/io/IOException
    //   367	372	375	java/io/IOException
    //   385	389	392	java/lang/Throwable
  }
  
  /* Error */
  private static long f(Context paramContext)
  {
    // Byte code:
    //   0: new 108	java/io/File
    //   3: dup
    //   4: aload_0
    //   5: ldc 110
    //   7: invokestatic 115	com/amap/api/mapcore2d/cz:a	(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;
    //   10: invokespecial 116	java/io/File:<init>	(Ljava/lang/String;)V
    //   13: astore 5
    //   15: aload 5
    //   17: invokevirtual 124	java/io/File:exists	()Z
    //   20: ifeq +49 -> 69
    //   23: new 253	java/io/FileInputStream
    //   26: dup
    //   27: aload 5
    //   29: invokespecial 254	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   32: astore_3
    //   33: aload_3
    //   34: astore_0
    //   35: aload_3
    //   36: invokevirtual 257	java/io/FileInputStream:available	()I
    //   39: newarray <illegal type>
    //   41: astore 4
    //   43: aload_3
    //   44: astore_0
    //   45: aload_3
    //   46: aload 4
    //   48: invokevirtual 261	java/io/FileInputStream:read	([B)I
    //   51: pop
    //   52: aload_3
    //   53: astore_0
    //   54: aload 4
    //   56: invokestatic 264	com/amap/api/mapcore2d/cv:a	([B)Ljava/lang/String;
    //   59: invokestatic 270	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   62: lstore_1
    //   63: aload_3
    //   64: ifnonnull +142 -> 206
    //   67: lload_1
    //   68: lreturn
    //   69: lconst_0
    //   70: lreturn
    //   71: astore 4
    //   73: aconst_null
    //   74: astore_3
    //   75: aload_3
    //   76: astore_0
    //   77: aload 4
    //   79: ldc 94
    //   81: ldc_w 272
    //   84: invokestatic 101	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   87: aload_3
    //   88: ifnonnull +131 -> 219
    //   91: lconst_0
    //   92: lreturn
    //   93: aload_3
    //   94: astore_0
    //   95: aload 4
    //   97: ldc 94
    //   99: ldc_w 272
    //   102: invokestatic 101	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   105: aload_3
    //   106: ifnull +162 -> 268
    //   109: aload_3
    //   110: invokevirtual 273	java/io/FileInputStream:close	()V
    //   113: lconst_0
    //   114: lreturn
    //   115: astore_0
    //   116: aload_0
    //   117: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   120: lconst_0
    //   121: lreturn
    //   122: astore 4
    //   124: aconst_null
    //   125: astore_3
    //   126: aload_3
    //   127: astore_0
    //   128: aload 4
    //   130: ldc 94
    //   132: ldc_w 272
    //   135: invokestatic 101	com/amap/api/mapcore2d/cy:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   138: aload 5
    //   140: ifnonnull +20 -> 160
    //   143: aload_3
    //   144: ifnull +124 -> 268
    //   147: aload_3
    //   148: invokevirtual 273	java/io/FileInputStream:close	()V
    //   151: lconst_0
    //   152: lreturn
    //   153: astore_0
    //   154: aload_0
    //   155: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   158: lconst_0
    //   159: lreturn
    //   160: aload_3
    //   161: astore_0
    //   162: aload 5
    //   164: invokevirtual 124	java/io/File:exists	()Z
    //   167: ifeq -24 -> 143
    //   170: aload_3
    //   171: astore_0
    //   172: aload 5
    //   174: invokevirtual 276	java/io/File:delete	()Z
    //   177: pop
    //   178: goto -35 -> 143
    //   181: astore 4
    //   183: aload_3
    //   184: astore_0
    //   185: aload 4
    //   187: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   190: goto -47 -> 143
    //   193: astore 4
    //   195: aload_0
    //   196: astore_3
    //   197: aload 4
    //   199: astore_0
    //   200: aload_3
    //   201: ifnonnull +31 -> 232
    //   204: aload_0
    //   205: athrow
    //   206: aload_3
    //   207: invokevirtual 273	java/io/FileInputStream:close	()V
    //   210: lload_1
    //   211: lreturn
    //   212: astore_0
    //   213: aload_0
    //   214: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   217: lload_1
    //   218: lreturn
    //   219: aload_3
    //   220: invokevirtual 273	java/io/FileInputStream:close	()V
    //   223: lconst_0
    //   224: lreturn
    //   225: astore_0
    //   226: aload_0
    //   227: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   230: lconst_0
    //   231: lreturn
    //   232: aload_3
    //   233: invokevirtual 273	java/io/FileInputStream:close	()V
    //   236: goto -32 -> 204
    //   239: astore_3
    //   240: aload_3
    //   241: invokevirtual 149	java/lang/Throwable:printStackTrace	()V
    //   244: goto -40 -> 204
    //   247: astore_0
    //   248: aconst_null
    //   249: astore_3
    //   250: goto -50 -> 200
    //   253: astore 4
    //   255: goto -129 -> 126
    //   258: astore 4
    //   260: goto -167 -> 93
    //   263: astore 4
    //   265: goto -190 -> 75
    //   268: lconst_0
    //   269: lreturn
    //   270: astore 4
    //   272: aconst_null
    //   273: astore_3
    //   274: goto -181 -> 93
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	277	0	paramContext	Context
    //   62	156	1	l	long
    //   32	201	3	localObject1	Object
    //   239	2	3	localThrowable1	Throwable
    //   249	25	3	localObject2	Object
    //   41	14	4	arrayOfByte	byte[]
    //   71	25	4	localFileNotFoundException1	java.io.FileNotFoundException
    //   122	7	4	localThrowable2	Throwable
    //   181	5	4	localThrowable3	Throwable
    //   193	5	4	localObject3	Object
    //   253	1	4	localThrowable4	Throwable
    //   258	1	4	localIOException1	java.io.IOException
    //   263	1	4	localFileNotFoundException2	java.io.FileNotFoundException
    //   270	1	4	localIOException2	java.io.IOException
    //   13	160	5	localFile	File
    // Exception table:
    //   from	to	target	type
    //   23	33	71	java/io/FileNotFoundException
    //   109	113	115	java/lang/Throwable
    //   23	33	122	java/lang/Throwable
    //   147	151	153	java/lang/Throwable
    //   162	170	181	java/lang/Throwable
    //   172	178	181	java/lang/Throwable
    //   35	43	193	finally
    //   45	52	193	finally
    //   54	63	193	finally
    //   77	87	193	finally
    //   95	105	193	finally
    //   128	138	193	finally
    //   162	170	193	finally
    //   172	178	193	finally
    //   185	190	193	finally
    //   206	210	212	java/lang/Throwable
    //   219	223	225	java/lang/Throwable
    //   232	236	239	java/lang/Throwable
    //   23	33	247	finally
    //   35	43	253	java/lang/Throwable
    //   45	52	253	java/lang/Throwable
    //   54	63	253	java/lang/Throwable
    //   35	43	258	java/io/IOException
    //   45	52	258	java/io/IOException
    //   54	63	258	java/io/IOException
    //   35	43	263	java/io/FileNotFoundException
    //   45	52	263	java/io/FileNotFoundException
    //   54	63	263	java/io/FileNotFoundException
    //   23	33	270	java/io/IOException
  }
  
  private static boolean g(Context paramContext)
  {
    int i;
    try
    {
      if (cp.m(paramContext) != 1) {
        return false;
      }
      if (!a) {
        break label80;
      }
      if (d(paramContext) < 100) {
        break label88;
      }
      long l1 = f(paramContext);
      l2 = new Date().getTime();
      if (l2 - l1 < 3600000L) {
        break label90;
      }
      i = 1;
    }
    catch (Throwable paramContext)
    {
      long l2;
      cy.a(paramContext, "StatisticsManager", "isUpdate");
    }
    a(paramContext, l2);
    a = false;
    return true;
    label80:
    return false;
    while (i == 0)
    {
      return false;
      label88:
      return false;
      label90:
      i = 0;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ej.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */