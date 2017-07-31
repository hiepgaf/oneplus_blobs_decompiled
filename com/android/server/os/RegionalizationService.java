package com.android.server.os;

import com.android.internal.os.IRegionalizationService.Stub;
import java.io.File;

public class RegionalizationService
  extends IRegionalizationService.Stub
{
  private static final String TAG = "RegionalizationService";
  
  private void deleteFiles(File paramFile, String paramString, boolean paramBoolean)
  {
    if (paramFile.isDirectory())
    {
      arrayOfString = paramFile.list();
      if (arrayOfString == null) {
        return;
      }
      i = 0;
      while (i < arrayOfString.length)
      {
        deleteFiles(new File(paramFile, arrayOfString[i]), paramString, paramBoolean);
        i += 1;
      }
      if (paramBoolean) {
        paramFile.delete();
      }
    }
    while ((!paramFile.isFile()) || ((!paramString.isEmpty()) && (!paramFile.getName().endsWith(paramString))))
    {
      String[] arrayOfString;
      int i;
      return;
    }
    paramFile.delete();
  }
  
  public boolean checkFileExists(String paramString)
  {
    paramString = new File(paramString);
    return (paramString != null) && (paramString.exists());
  }
  
  public void deleteFilesUnderDir(String paramString1, String paramString2, boolean paramBoolean)
  {
    paramString1 = new File(paramString1);
    if ((paramString1 != null) && (paramString1.exists()))
    {
      deleteFiles(paramString1, paramString2, paramBoolean);
      return;
    }
  }
  
  /* Error */
  public java.util.ArrayList<String> readFile(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: new 17	java/io/File
    //   3: dup
    //   4: aload_1
    //   5: invokespecial 53	java/io/File:<init>	(Ljava/lang/String;)V
    //   8: astore_1
    //   9: aload_1
    //   10: ifnull +165 -> 175
    //   13: aload_1
    //   14: invokevirtual 56	java/io/File:exists	()Z
    //   17: ifeq +158 -> 175
    //   20: aload_1
    //   21: invokevirtual 65	java/io/File:canRead	()Z
    //   24: ifeq +151 -> 175
    //   27: new 67	java/util/ArrayList
    //   30: dup
    //   31: invokespecial 68	java/util/ArrayList:<init>	()V
    //   34: astore 8
    //   36: aconst_null
    //   37: astore 4
    //   39: aconst_null
    //   40: astore 6
    //   42: aconst_null
    //   43: astore 7
    //   45: aconst_null
    //   46: astore_3
    //   47: aconst_null
    //   48: astore 5
    //   50: new 70	java/io/FileReader
    //   53: dup
    //   54: aload_1
    //   55: invokespecial 73	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   58: astore_1
    //   59: new 75	java/io/BufferedReader
    //   62: dup
    //   63: aload_1
    //   64: invokespecial 78	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   67: astore_3
    //   68: aload_3
    //   69: invokevirtual 81	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   72: astore 4
    //   74: aload 4
    //   76: ifnull +138 -> 214
    //   79: aload 4
    //   81: invokevirtual 84	java/lang/String:trim	()Ljava/lang/String;
    //   84: astore 4
    //   86: aload 4
    //   88: ifnull +126 -> 214
    //   91: aload_2
    //   92: invokestatic 89	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   95: ifne +82 -> 177
    //   98: aload 4
    //   100: aload_2
    //   101: invokevirtual 92	java/lang/String:matches	(Ljava/lang/String;)Z
    //   104: ifeq -36 -> 68
    //   107: aload 8
    //   109: aload 4
    //   111: invokevirtual 96	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   114: pop
    //   115: goto -47 -> 68
    //   118: astore 5
    //   120: aload_3
    //   121: astore_2
    //   122: aload_2
    //   123: astore_3
    //   124: aload_1
    //   125: astore 4
    //   127: ldc 8
    //   129: new 98	java/lang/StringBuilder
    //   132: dup
    //   133: invokespecial 99	java/lang/StringBuilder:<init>	()V
    //   136: ldc 101
    //   138: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: aload 5
    //   143: invokevirtual 108	java/io/IOException:getMessage	()Ljava/lang/String;
    //   146: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   149: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   152: invokestatic 117	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   155: pop
    //   156: aload_2
    //   157: ifnull +7 -> 164
    //   160: aload_2
    //   161: invokevirtual 120	java/io/BufferedReader:close	()V
    //   164: aload_1
    //   165: ifnull +7 -> 172
    //   168: aload_1
    //   169: invokevirtual 121	java/io/FileReader:close	()V
    //   172: aload 8
    //   174: areturn
    //   175: aconst_null
    //   176: areturn
    //   177: aload 8
    //   179: aload 4
    //   181: invokevirtual 96	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   184: pop
    //   185: goto -117 -> 68
    //   188: astore_2
    //   189: aload_1
    //   190: astore 4
    //   192: aload_2
    //   193: astore_1
    //   194: aload_3
    //   195: ifnull +7 -> 202
    //   198: aload_3
    //   199: invokevirtual 120	java/io/BufferedReader:close	()V
    //   202: aload 4
    //   204: ifnull +8 -> 212
    //   207: aload 4
    //   209: invokevirtual 121	java/io/FileReader:close	()V
    //   212: aload_1
    //   213: athrow
    //   214: aload_3
    //   215: ifnull +7 -> 222
    //   218: aload_3
    //   219: invokevirtual 120	java/io/BufferedReader:close	()V
    //   222: aload_1
    //   223: ifnull +7 -> 230
    //   226: aload_1
    //   227: invokevirtual 121	java/io/FileReader:close	()V
    //   230: aload 8
    //   232: areturn
    //   233: astore_1
    //   234: ldc 8
    //   236: new 98	java/lang/StringBuilder
    //   239: dup
    //   240: invokespecial 99	java/lang/StringBuilder:<init>	()V
    //   243: ldc 123
    //   245: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   248: aload_1
    //   249: invokevirtual 108	java/io/IOException:getMessage	()Ljava/lang/String;
    //   252: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   255: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   258: invokestatic 117	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   261: pop
    //   262: goto -32 -> 230
    //   265: astore_1
    //   266: ldc 8
    //   268: new 98	java/lang/StringBuilder
    //   271: dup
    //   272: invokespecial 99	java/lang/StringBuilder:<init>	()V
    //   275: ldc 123
    //   277: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   280: aload_1
    //   281: invokevirtual 108	java/io/IOException:getMessage	()Ljava/lang/String;
    //   284: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   287: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   290: invokestatic 117	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   293: pop
    //   294: aload 8
    //   296: areturn
    //   297: astore_2
    //   298: ldc 8
    //   300: new 98	java/lang/StringBuilder
    //   303: dup
    //   304: invokespecial 99	java/lang/StringBuilder:<init>	()V
    //   307: ldc 123
    //   309: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   312: aload_2
    //   313: invokevirtual 108	java/io/IOException:getMessage	()Ljava/lang/String;
    //   316: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   319: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   322: invokestatic 117	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   325: pop
    //   326: goto -114 -> 212
    //   329: astore_1
    //   330: goto -136 -> 194
    //   333: astore_2
    //   334: aload 7
    //   336: astore_3
    //   337: aload_1
    //   338: astore 4
    //   340: aload_2
    //   341: astore_1
    //   342: goto -148 -> 194
    //   345: astore_1
    //   346: aload 5
    //   348: astore_2
    //   349: aload_1
    //   350: astore 5
    //   352: aload 6
    //   354: astore_1
    //   355: goto -233 -> 122
    //   358: astore_3
    //   359: aload 5
    //   361: astore_2
    //   362: aload_3
    //   363: astore 5
    //   365: goto -243 -> 122
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	368	0	this	RegionalizationService
    //   0	368	1	paramString1	String
    //   0	368	2	paramString2	String
    //   46	291	3	localObject1	Object
    //   358	5	3	localIOException1	java.io.IOException
    //   37	302	4	str	String
    //   48	1	5	localObject2	Object
    //   118	229	5	localIOException2	java.io.IOException
    //   350	14	5	localObject3	Object
    //   40	313	6	localObject4	Object
    //   43	292	7	localObject5	Object
    //   34	261	8	localArrayList	java.util.ArrayList
    // Exception table:
    //   from	to	target	type
    //   68	74	118	java/io/IOException
    //   79	86	118	java/io/IOException
    //   91	115	118	java/io/IOException
    //   177	185	118	java/io/IOException
    //   68	74	188	finally
    //   79	86	188	finally
    //   91	115	188	finally
    //   177	185	188	finally
    //   218	222	233	java/io/IOException
    //   226	230	233	java/io/IOException
    //   160	164	265	java/io/IOException
    //   168	172	265	java/io/IOException
    //   198	202	297	java/io/IOException
    //   207	212	297	java/io/IOException
    //   50	59	329	finally
    //   127	156	329	finally
    //   59	68	333	finally
    //   50	59	345	java/io/IOException
    //   59	68	358	java/io/IOException
  }
  
  /* Error */
  public boolean writeFile(String paramString1, String paramString2, boolean paramBoolean)
  {
    // Byte code:
    //   0: new 17	java/io/File
    //   3: dup
    //   4: aload_1
    //   5: invokespecial 53	java/io/File:<init>	(Ljava/lang/String;)V
    //   8: astore 4
    //   10: aload 4
    //   12: ifnull +28 -> 40
    //   15: aload 4
    //   17: invokevirtual 56	java/io/File:exists	()Z
    //   20: ifeq +20 -> 40
    //   23: aload 4
    //   25: invokevirtual 136	java/io/File:canWrite	()Z
    //   28: ifeq +12 -> 40
    //   31: aload_2
    //   32: invokestatic 89	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   35: ifeq +7 -> 42
    //   38: iconst_0
    //   39: ireturn
    //   40: iconst_0
    //   41: ireturn
    //   42: aconst_null
    //   43: astore_1
    //   44: aconst_null
    //   45: astore 5
    //   47: new 138	java/io/FileWriter
    //   50: dup
    //   51: aload 4
    //   53: iload_3
    //   54: invokespecial 141	java/io/FileWriter:<init>	(Ljava/io/File;Z)V
    //   57: astore 4
    //   59: aload 4
    //   61: aload_2
    //   62: invokevirtual 144	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   65: aload 4
    //   67: ifnull +8 -> 75
    //   70: aload 4
    //   72: invokevirtual 145	java/io/FileWriter:close	()V
    //   75: iconst_1
    //   76: ireturn
    //   77: astore_1
    //   78: aload_1
    //   79: invokevirtual 148	java/io/IOException:printStackTrace	()V
    //   82: iconst_0
    //   83: ireturn
    //   84: astore 4
    //   86: aload 5
    //   88: astore_2
    //   89: aload_2
    //   90: astore_1
    //   91: aload 4
    //   93: invokevirtual 148	java/io/IOException:printStackTrace	()V
    //   96: aload_2
    //   97: ifnull +7 -> 104
    //   100: aload_2
    //   101: invokevirtual 145	java/io/FileWriter:close	()V
    //   104: iconst_0
    //   105: ireturn
    //   106: astore_1
    //   107: aload_1
    //   108: invokevirtual 148	java/io/IOException:printStackTrace	()V
    //   111: iconst_0
    //   112: ireturn
    //   113: astore_2
    //   114: aload_1
    //   115: ifnull +7 -> 122
    //   118: aload_1
    //   119: invokevirtual 145	java/io/FileWriter:close	()V
    //   122: aload_2
    //   123: athrow
    //   124: astore_1
    //   125: aload_1
    //   126: invokevirtual 148	java/io/IOException:printStackTrace	()V
    //   129: iconst_0
    //   130: ireturn
    //   131: astore_2
    //   132: aload 4
    //   134: astore_1
    //   135: goto -21 -> 114
    //   138: astore_1
    //   139: aload 4
    //   141: astore_2
    //   142: aload_1
    //   143: astore 4
    //   145: goto -56 -> 89
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	148	0	this	RegionalizationService
    //   0	148	1	paramString1	String
    //   0	148	2	paramString2	String
    //   0	148	3	paramBoolean	boolean
    //   8	63	4	localObject1	Object
    //   84	56	4	localIOException	java.io.IOException
    //   143	1	4	str	String
    //   45	42	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   70	75	77	java/io/IOException
    //   47	59	84	java/io/IOException
    //   100	104	106	java/io/IOException
    //   47	59	113	finally
    //   91	96	113	finally
    //   118	122	124	java/io/IOException
    //   59	65	131	finally
    //   59	65	138	java/io/IOException
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/os/RegionalizationService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */