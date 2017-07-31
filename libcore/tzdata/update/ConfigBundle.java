package libcore.tzdata.update;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public final class ConfigBundle
{
  private static final int BUFFER_SIZE = 8192;
  public static final String CHECKSUMS_FILE_NAME = "checksums";
  public static final String ICU_DATA_FILE_NAME = "icu/icu_tzdata.dat";
  public static final String TZ_DATA_VERSION_FILE_NAME = "tzdata_version";
  public static final String ZONEINFO_FILE_NAME = "tzdata";
  private final byte[] bytes;
  
  public ConfigBundle(byte[] paramArrayOfByte)
  {
    this.bytes = paramArrayOfByte;
  }
  
  /* Error */
  static void extractZipSafely(java.io.InputStream paramInputStream, File paramFile, boolean paramBoolean)
    throws IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: iload_2
    //   2: invokestatic 42	libcore/tzdata/update/FileUtils:ensureDirectoriesExist	(Ljava/io/File;Z)V
    //   5: aconst_null
    //   6: astore 6
    //   8: aconst_null
    //   9: astore 4
    //   11: aconst_null
    //   12: astore 7
    //   14: new 44	java/util/zip/ZipInputStream
    //   17: dup
    //   18: aload_0
    //   19: invokespecial 47	java/util/zip/ZipInputStream:<init>	(Ljava/io/InputStream;)V
    //   22: astore 5
    //   24: sipush 8192
    //   27: newarray <illegal type>
    //   29: astore 10
    //   31: aload 5
    //   33: invokevirtual 51	java/util/zip/ZipInputStream:getNextEntry	()Ljava/util/zip/ZipEntry;
    //   36: astore_0
    //   37: aload_0
    //   38: ifnull +240 -> 278
    //   41: aload_1
    //   42: aload_0
    //   43: invokevirtual 57	java/util/zip/ZipEntry:getName	()Ljava/lang/String;
    //   46: invokestatic 61	libcore/tzdata/update/FileUtils:createSubFile	(Ljava/io/File;Ljava/lang/String;)Ljava/io/File;
    //   49: astore 11
    //   51: aload_0
    //   52: invokevirtual 65	java/util/zip/ZipEntry:isDirectory	()Z
    //   55: ifeq +44 -> 99
    //   58: aload 11
    //   60: iload_2
    //   61: invokestatic 42	libcore/tzdata/update/FileUtils:ensureDirectoriesExist	(Ljava/io/File;Z)V
    //   64: goto -33 -> 31
    //   67: astore_0
    //   68: aload 5
    //   70: astore 4
    //   72: aload_0
    //   73: athrow
    //   74: astore_1
    //   75: aload_0
    //   76: astore 5
    //   78: aload 4
    //   80: ifnull +11 -> 91
    //   83: aload 4
    //   85: invokevirtual 68	java/util/zip/ZipInputStream:close	()V
    //   88: aload_0
    //   89: astore 5
    //   91: aload 5
    //   93: ifnull +245 -> 338
    //   96: aload 5
    //   98: athrow
    //   99: aload 11
    //   101: invokevirtual 74	java/io/File:getParentFile	()Ljava/io/File;
    //   104: invokevirtual 77	java/io/File:exists	()Z
    //   107: ifne +12 -> 119
    //   110: aload 11
    //   112: invokevirtual 74	java/io/File:getParentFile	()Ljava/io/File;
    //   115: iload_2
    //   116: invokestatic 42	libcore/tzdata/update/FileUtils:ensureDirectoriesExist	(Ljava/io/File;Z)V
    //   119: aconst_null
    //   120: astore 8
    //   122: aconst_null
    //   123: astore 4
    //   125: aconst_null
    //   126: astore 9
    //   128: aconst_null
    //   129: astore 7
    //   131: new 79	java/io/FileOutputStream
    //   134: dup
    //   135: aload 11
    //   137: invokespecial 82	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   140: astore_0
    //   141: aload 5
    //   143: aload 10
    //   145: invokevirtual 86	java/util/zip/ZipInputStream:read	([B)I
    //   148: istore_3
    //   149: iload_3
    //   150: iconst_m1
    //   151: if_icmpeq +59 -> 210
    //   154: aload_0
    //   155: aload 10
    //   157: iconst_0
    //   158: iload_3
    //   159: invokevirtual 90	java/io/FileOutputStream:write	([BII)V
    //   162: goto -21 -> 141
    //   165: astore_1
    //   166: aload_1
    //   167: athrow
    //   168: astore 6
    //   170: aload_1
    //   171: astore 4
    //   173: aload 6
    //   175: astore_1
    //   176: aload 4
    //   178: astore 6
    //   180: aload_0
    //   181: ifnull +11 -> 192
    //   184: aload_0
    //   185: invokevirtual 91	java/io/FileOutputStream:close	()V
    //   188: aload 4
    //   190: astore 6
    //   192: aload 6
    //   194: ifnull +70 -> 264
    //   197: aload 6
    //   199: athrow
    //   200: astore_1
    //   201: aconst_null
    //   202: astore_0
    //   203: aload 5
    //   205: astore 4
    //   207: goto -132 -> 75
    //   210: aload_0
    //   211: invokevirtual 95	java/io/FileOutputStream:getFD	()Ljava/io/FileDescriptor;
    //   214: invokevirtual 100	java/io/FileDescriptor:sync	()V
    //   217: aload 8
    //   219: astore 4
    //   221: aload_0
    //   222: ifnull +11 -> 233
    //   225: aload_0
    //   226: invokevirtual 91	java/io/FileOutputStream:close	()V
    //   229: aload 8
    //   231: astore 4
    //   233: aload 4
    //   235: ifnull +31 -> 266
    //   238: aload 4
    //   240: athrow
    //   241: aload 4
    //   243: astore 6
    //   245: aload 4
    //   247: aload_0
    //   248: if_acmpeq -56 -> 192
    //   251: aload 4
    //   253: aload_0
    //   254: invokevirtual 104	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   257: aload 4
    //   259: astore 6
    //   261: goto -69 -> 192
    //   264: aload_1
    //   265: athrow
    //   266: iload_2
    //   267: ifeq -236 -> 31
    //   270: aload 11
    //   272: invokestatic 107	libcore/tzdata/update/FileUtils:makeWorldReadable	(Ljava/io/File;)V
    //   275: goto -244 -> 31
    //   278: aload 6
    //   280: astore_0
    //   281: aload 5
    //   283: ifnull +11 -> 294
    //   286: aload 5
    //   288: invokevirtual 68	java/util/zip/ZipInputStream:close	()V
    //   291: aload 6
    //   293: astore_0
    //   294: aload_0
    //   295: ifnull +45 -> 340
    //   298: aload_0
    //   299: athrow
    //   300: astore_0
    //   301: goto -7 -> 294
    //   304: astore 4
    //   306: aload_0
    //   307: ifnonnull +10 -> 317
    //   310: aload 4
    //   312: astore 5
    //   314: goto -223 -> 91
    //   317: aload_0
    //   318: astore 5
    //   320: aload_0
    //   321: aload 4
    //   323: if_acmpeq -232 -> 91
    //   326: aload_0
    //   327: aload 4
    //   329: invokevirtual 104	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   332: aload_0
    //   333: astore 5
    //   335: goto -244 -> 91
    //   338: aload_1
    //   339: athrow
    //   340: return
    //   341: astore_1
    //   342: aconst_null
    //   343: astore_0
    //   344: goto -269 -> 75
    //   347: astore_0
    //   348: aload 7
    //   350: astore 4
    //   352: goto -280 -> 72
    //   355: astore_1
    //   356: aload 9
    //   358: astore_0
    //   359: goto -183 -> 176
    //   362: astore_1
    //   363: goto -187 -> 176
    //   366: astore_1
    //   367: aload 7
    //   369: astore_0
    //   370: goto -204 -> 166
    //   373: astore 4
    //   375: goto -142 -> 233
    //   378: astore_0
    //   379: aload 4
    //   381: ifnonnull -140 -> 241
    //   384: aload_0
    //   385: astore 6
    //   387: goto -195 -> 192
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	390	0	paramInputStream	java.io.InputStream
    //   0	390	1	paramFile	File
    //   0	390	2	paramBoolean	boolean
    //   148	11	3	i	int
    //   9	249	4	localObject1	Object
    //   304	24	4	localThrowable1	Throwable
    //   350	1	4	localObject2	Object
    //   373	7	4	localThrowable2	Throwable
    //   22	312	5	localObject3	Object
    //   6	1	6	localObject4	Object
    //   168	6	6	localObject5	Object
    //   178	208	6	localObject6	Object
    //   12	356	7	localObject7	Object
    //   120	110	8	localObject8	Object
    //   126	231	9	localObject9	Object
    //   29	127	10	arrayOfByte	byte[]
    //   49	222	11	localFile	File
    // Exception table:
    //   from	to	target	type
    //   24	31	67	java/lang/Throwable
    //   31	37	67	java/lang/Throwable
    //   41	64	67	java/lang/Throwable
    //   99	119	67	java/lang/Throwable
    //   197	200	67	java/lang/Throwable
    //   238	241	67	java/lang/Throwable
    //   251	257	67	java/lang/Throwable
    //   264	266	67	java/lang/Throwable
    //   270	275	67	java/lang/Throwable
    //   72	74	74	finally
    //   141	149	165	java/lang/Throwable
    //   154	162	165	java/lang/Throwable
    //   210	217	165	java/lang/Throwable
    //   166	168	168	finally
    //   24	31	200	finally
    //   31	37	200	finally
    //   41	64	200	finally
    //   99	119	200	finally
    //   184	188	200	finally
    //   197	200	200	finally
    //   225	229	200	finally
    //   238	241	200	finally
    //   251	257	200	finally
    //   264	266	200	finally
    //   270	275	200	finally
    //   286	291	300	java/lang/Throwable
    //   83	88	304	java/lang/Throwable
    //   14	24	341	finally
    //   14	24	347	java/lang/Throwable
    //   131	141	355	finally
    //   141	149	362	finally
    //   154	162	362	finally
    //   210	217	362	finally
    //   131	141	366	java/lang/Throwable
    //   225	229	373	java/lang/Throwable
    //   184	188	378	java/lang/Throwable
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (ConfigBundle)paramObject;
    return Arrays.equals(this.bytes, ((ConfigBundle)paramObject).bytes);
  }
  
  public void extractTo(File paramFile)
    throws IOException
  {
    extractZipSafely(new ByteArrayInputStream(this.bytes), paramFile, true);
  }
  
  public byte[] getBundleBytes()
  {
    return this.bytes;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/libcore/tzdata/update/ConfigBundle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */