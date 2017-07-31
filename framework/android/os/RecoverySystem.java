package android.os;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;

public class RecoverySystem
{
  public static final File BLOCK_MAP_FILE = new File(RECOVERY_DIR, "block.map");
  private static File COMMAND_FILE;
  private static final File DEFAULT_KEYSTORE = new File("/system/etc/security/otacerts.zip");
  private static final File LAST_INSTALL_FILE;
  private static final String LAST_PREFIX = "last_";
  private static final File LOG_FILE;
  private static final int LOG_FILE_MAX_LENGTH = 65536;
  private static final long PUBLISH_PROGRESS_INTERVAL_MS = 500L;
  private static final File RECOVERY_DIR = new File("/cache/recovery");
  private static final String TAG = "RecoverySystem";
  public static final File UNCRYPT_PACKAGE_FILE = new File(RECOVERY_DIR, "uncrypt_file");
  public static final File UNCRYPT_STATUS_FILE = new File(RECOVERY_DIR, "uncrypt_status");
  private static PowerManager.WakeLock mScreenWakeLock;
  private static final Object sRequestLock = new Object();
  private final IRecoverySystem mService;
  
  static
  {
    LOG_FILE = new File(RECOVERY_DIR, "log");
    LAST_INSTALL_FILE = new File(RECOVERY_DIR, "last_install");
    COMMAND_FILE = new File(RECOVERY_DIR, "command");
  }
  
  public RecoverySystem()
  {
    this.mService = null;
  }
  
  public RecoverySystem(IRecoverySystem paramIRecoverySystem)
  {
    this.mService = paramIRecoverySystem;
  }
  
  private static void bootCommand(Context paramContext, String... paramVarArgs)
    throws IOException
  {
    LOG_FILE.delete();
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = paramVarArgs.length;
    while (i < j)
    {
      String str = paramVarArgs[i];
      if (!TextUtils.isEmpty(str))
      {
        localStringBuilder.append(str);
        localStringBuilder.append("\n");
      }
      i += 1;
    }
    ((RecoverySystem)paramContext.getSystemService("recovery")).rebootRecoveryWithCommand(localStringBuilder.toString());
    throw new IOException("Reboot failed (no permissions?)");
  }
  
  private static void bootCommand(String... paramVarArgs)
    throws IOException
  {
    RECOVERY_DIR.mkdirs();
    COMMAND_FILE.delete();
    FileWriter localFileWriter = new FileWriter(COMMAND_FILE);
    int i = 0;
    try
    {
      int j = paramVarArgs.length;
      while (i < j)
      {
        String str = paramVarArgs[i];
        if (!TextUtils.isEmpty(str))
        {
          localFileWriter.write(str);
          localFileWriter.write("\n");
        }
        i += 1;
      }
      return;
    }
    finally
    {
      localFileWriter.close();
    }
  }
  
  public static void cancelScheduledUpdate(Context paramContext)
    throws IOException
  {
    if (!((RecoverySystem)paramContext.getSystemService("recovery")).clearBcb()) {
      throw new IOException("cancel scheduled update failed");
    }
  }
  
  private boolean clearBcb()
  {
    try
    {
      boolean bool = this.mService.clearBcb();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  private static HashSet<X509Certificate> getTrustedCerts(File paramFile)
    throws IOException, GeneralSecurityException
  {
    HashSet localHashSet = new HashSet();
    Object localObject1 = paramFile;
    if (paramFile == null) {
      localObject1 = DEFAULT_KEYSTORE;
    }
    paramFile = new ZipFile((File)localObject1);
    try
    {
      localCertificateFactory = CertificateFactory.getInstance("X.509");
      Enumeration localEnumeration = paramFile.entries();
      if (localEnumeration.hasMoreElements()) {
        localObject1 = paramFile.getInputStream((ZipEntry)localEnumeration.nextElement());
      }
    }
    finally
    {
      try
      {
        for (;;)
        {
          CertificateFactory localCertificateFactory;
          localHashSet.add((X509Certificate)localCertificateFactory.generateCertificate((InputStream)localObject1));
          ((InputStream)localObject1).close();
        }
      }
      finally
      {
        ((InputStream)localObject2).close();
      }
      localObject2 = finally;
      paramFile.close();
    }
    paramFile.close();
    return localHashSet1;
  }
  
  public static String handleAftermath(Context paramContext)
  {
    Object localObject = null;
    try
    {
      str = FileUtils.readTextFile(LOG_FILE, -65536, "...\n");
      localObject = str;
    }
    catch (IOException localIOException1)
    {
      for (;;)
      {
        String str;
        Log.e("RecoverySystem", "Error reading recovery log", localIOException1);
      }
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      for (;;)
      {
        Log.i("RecoverySystem", "No recovery log file");
      }
    }
    if (localObject != null) {
      parseLastInstallLog(paramContext);
    }
    boolean bool = BLOCK_MAP_FILE.exists();
    if ((!bool) && (UNCRYPT_PACKAGE_FILE.exists())) {
      paramContext = null;
    }
    try
    {
      str = FileUtils.readTextFile(UNCRYPT_PACKAGE_FILE, 0, null);
      paramContext = str;
    }
    catch (IOException localIOException2)
    {
      for (;;)
      {
        int i;
        Log.e("RecoverySystem", "Error reading uncrypt file", localIOException2);
        continue;
        Log.e("RecoverySystem", "Can't delete: " + paramContext);
        continue;
        if (((!bool) || (!paramContext[i].equals(BLOCK_MAP_FILE.getName()))) && ((!bool) || (!paramContext[i].equals(UNCRYPT_PACKAGE_FILE.getName())))) {
          recursiveDelete(new File(RECOVERY_DIR, paramContext[i]));
        }
      }
    }
    if ((paramContext != null) && (paramContext.startsWith("/data")))
    {
      if (UNCRYPT_PACKAGE_FILE.delete()) {
        Log.i("RecoverySystem", "Deleted: " + paramContext);
      }
    }
    else
    {
      paramContext = RECOVERY_DIR.list();
      i = 0;
      for (;;)
      {
        if ((paramContext == null) || (i >= paramContext.length)) {
          return localObject;
        }
        if (!paramContext[i].startsWith("last_")) {
          break;
        }
        i += 1;
      }
    }
    return (String)localObject;
  }
  
  public static void installPackage(Context paramContext, File paramFile)
    throws IOException
  {
    installPackage(paramContext, paramFile, false);
  }
  
  public static void installPackage(Context paramContext, File paramFile, boolean paramBoolean)
    throws IOException
  {
    boolean bool;
    int i;
    String str1;
    for (;;)
    {
      synchronized (sRequestLock)
      {
        LOG_FILE.delete();
        UNCRYPT_PACKAGE_FILE.delete();
        localObject1 = paramFile.getCanonicalPath();
        paramFile = (File)localObject1;
        if (((String)localObject1).startsWith("/storage/emulated")) {
          paramFile = ((String)localObject1).replaceFirst("/storage/emulated", "/data/media");
        }
        Log.w("RecoverySystem", "!!! REBOOTING TO INSTALL " + paramFile + " !!!");
        localObject1 = new File("/cache").listFiles();
        if ((localObject1 == null) || (localObject1.length == 0))
        {
          Log.w("RecoverySystem", "!!! There is no any extra files in cache root directory !!!");
          bool = paramFile.endsWith("_s.zip");
          localObject1 = paramFile;
          if (!paramFile.startsWith("/data/")) {
            break label339;
          }
          if (!paramBoolean) {
            break;
          }
          if (BLOCK_MAP_FILE.exists()) {
            break label567;
          }
          Log.e("RecoverySystem", "Package claimed to have been processed but failed to find the block map file.");
          throw new IOException("Failed to find block map file");
        }
      }
      Log.i("RecoverySystem", "Deleting unuseful files in cache partition...");
      i = 0;
      int j = localObject1.length;
      if (i < j)
      {
        str1 = localObject1[i];
        if ((str1.isDirectory()) || (str1.delete())) {
          break label560;
        }
        Log.w("TAG", "!!! Delete " + str1.getName() + "failed !!!");
        break label560;
      }
    }
    Object localObject1 = new FileWriter(UNCRYPT_PACKAGE_FILE);
    for (;;)
    {
      try
      {
        ((FileWriter)localObject1).write(paramFile + "\n");
        ((FileWriter)localObject1).close();
        if ((!UNCRYPT_PACKAGE_FILE.setReadable(true, false)) || (!UNCRYPT_PACKAGE_FILE.setWritable(true, false))) {
          break label486;
        }
        BLOCK_MAP_FILE.delete();
        break label567;
      }
      finally
      {
        label339:
        ((FileWriter)localObject1).close();
      }
      str1 = "--update_package=" + (String)localObject1 + "\n";
      String str2 = "--locale=" + Locale.getDefault().toString() + "\n";
      localObject1 = str1 + str2;
      paramFile = (File)localObject1;
      if (bool) {
        paramFile = (String)localObject1 + "--security\n";
      }
      if (!((RecoverySystem)paramContext.getSystemService("recovery")).setupBcb(paramFile))
      {
        throw new IOException("Setup BCB failed");
        label486:
        Log.e("RecoverySystem", "Error setting permission for " + UNCRYPT_PACKAGE_FILE);
      }
      else
      {
        bootCommand(new String[] { str1, str2 });
        ((PowerManager)paramContext.getSystemService("power")).reboot("recovery-update");
        throw new IOException("Reboot failed (no permissions?)");
        label560:
        i += 1;
        break;
        label567:
        localObject1 = "@/cache/recovery/block.map";
      }
    }
  }
  
  /* Error */
  private static void parseLastInstallLog(Context paramContext)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 11
    //   3: aconst_null
    //   4: astore 10
    //   6: aconst_null
    //   7: astore 13
    //   9: aconst_null
    //   10: astore 12
    //   12: new 382	java/io/BufferedReader
    //   15: dup
    //   16: new 384	java/io/FileReader
    //   19: dup
    //   20: getstatic 71	android/os/RecoverySystem:LAST_INSTALL_FILE	Ljava/io/File;
    //   23: invokespecial 385	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   26: invokespecial 388	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   29: astore 9
    //   31: iconst_m1
    //   32: istore_2
    //   33: iconst_m1
    //   34: istore_3
    //   35: iconst_m1
    //   36: istore 5
    //   38: iconst_m1
    //   39: istore 4
    //   41: iconst_m1
    //   42: istore 6
    //   44: aload 9
    //   46: invokevirtual 391	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   49: astore 12
    //   51: aload 12
    //   53: ifnull +292 -> 345
    //   56: aload 12
    //   58: bipush 58
    //   60: invokevirtual 395	java/lang/String:indexOf	(I)I
    //   63: istore_1
    //   64: iload_1
    //   65: iconst_m1
    //   66: if_icmpeq -22 -> 44
    //   69: iload_1
    //   70: iconst_1
    //   71: iadd
    //   72: aload 12
    //   74: invokevirtual 399	java/lang/String:length	()I
    //   77: if_icmpge -33 -> 44
    //   80: aload 12
    //   82: iload_1
    //   83: iconst_1
    //   84: iadd
    //   85: invokevirtual 403	java/lang/String:substring	(I)Ljava/lang/String;
    //   88: invokevirtual 406	java/lang/String:trim	()Ljava/lang/String;
    //   91: astore 13
    //   93: aload 13
    //   95: invokestatic 412	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   98: lstore 7
    //   100: aload 12
    //   102: ldc_w 414
    //   105: invokevirtual 244	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   108: ifeq +120 -> 228
    //   111: lload 7
    //   113: ldc2_w 415
    //   116: ldiv
    //   117: invokestatic 422	java/lang/Math:toIntExact	(J)I
    //   120: istore_1
    //   121: aload 12
    //   123: ldc_w 424
    //   126: invokevirtual 244	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   129: ifeq +140 -> 269
    //   132: iload_1
    //   133: istore 5
    //   135: goto -91 -> 44
    //   138: astore 13
    //   140: ldc 36
    //   142: new 105	java/lang/StringBuilder
    //   145: dup
    //   146: invokespecial 106	java/lang/StringBuilder:<init>	()V
    //   149: ldc_w 426
    //   152: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   155: aload 12
    //   157: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: invokevirtual 130	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   163: invokestatic 270	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   166: pop
    //   167: goto -123 -> 44
    //   170: astore 10
    //   172: aload 9
    //   174: astore_0
    //   175: aload 10
    //   177: astore 9
    //   179: aload 9
    //   181: athrow
    //   182: astore 11
    //   184: aload 9
    //   186: astore 10
    //   188: aload 11
    //   190: astore 9
    //   192: aload 10
    //   194: astore 11
    //   196: aload_0
    //   197: ifnull +11 -> 208
    //   200: aload_0
    //   201: invokevirtual 427	java/io/BufferedReader:close	()V
    //   204: aload 10
    //   206: astore 11
    //   208: aload 11
    //   210: ifnull +272 -> 482
    //   213: aload 11
    //   215: athrow
    //   216: astore_0
    //   217: ldc 36
    //   219: ldc_w 429
    //   222: aload_0
    //   223: invokestatic 262	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   226: pop
    //   227: return
    //   228: lload 7
    //   230: invokestatic 422	java/lang/Math:toIntExact	(J)I
    //   233: istore_1
    //   234: goto -113 -> 121
    //   237: astore 13
    //   239: ldc 36
    //   241: new 105	java/lang/StringBuilder
    //   244: dup
    //   245: invokespecial 106	java/lang/StringBuilder:<init>	()V
    //   248: ldc_w 431
    //   251: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   254: aload 12
    //   256: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   259: invokevirtual 130	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   262: invokestatic 270	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   265: pop
    //   266: goto -222 -> 44
    //   269: aload 12
    //   271: ldc_w 433
    //   274: invokevirtual 244	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   277: ifeq +9 -> 286
    //   280: iload_1
    //   281: istore 4
    //   283: goto -239 -> 44
    //   286: aload 12
    //   288: ldc_w 435
    //   291: invokevirtual 244	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   294: ifeq +9 -> 303
    //   297: iload_1
    //   298: istore 6
    //   300: goto -256 -> 44
    //   303: aload 12
    //   305: ldc_w 437
    //   308: invokevirtual 244	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   311: ifeq +13 -> 324
    //   314: iload_2
    //   315: iconst_m1
    //   316: if_icmpne +197 -> 513
    //   319: iload_1
    //   320: istore_2
    //   321: goto -277 -> 44
    //   324: aload 12
    //   326: ldc_w 439
    //   329: invokevirtual 244	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   332: ifeq -288 -> 44
    //   335: iload_3
    //   336: iconst_m1
    //   337: if_icmpne +183 -> 520
    //   340: iload_1
    //   341: istore_3
    //   342: goto -298 -> 44
    //   345: iload 5
    //   347: iconst_m1
    //   348: if_icmpeq +12 -> 360
    //   351: aload_0
    //   352: ldc_w 441
    //   355: iload 5
    //   357: invokestatic 447	com/android/internal/logging/MetricsLogger:histogram	(Landroid/content/Context;Ljava/lang/String;I)V
    //   360: iload 4
    //   362: iconst_m1
    //   363: if_icmpeq +12 -> 375
    //   366: aload_0
    //   367: ldc_w 449
    //   370: iload 4
    //   372: invokestatic 447	com/android/internal/logging/MetricsLogger:histogram	(Landroid/content/Context;Ljava/lang/String;I)V
    //   375: iload 6
    //   377: iconst_m1
    //   378: if_icmpeq +12 -> 390
    //   381: aload_0
    //   382: ldc_w 451
    //   385: iload 6
    //   387: invokestatic 447	com/android/internal/logging/MetricsLogger:histogram	(Landroid/content/Context;Ljava/lang/String;I)V
    //   390: iload_2
    //   391: iconst_m1
    //   392: if_icmpeq +11 -> 403
    //   395: aload_0
    //   396: ldc_w 453
    //   399: iload_2
    //   400: invokestatic 447	com/android/internal/logging/MetricsLogger:histogram	(Landroid/content/Context;Ljava/lang/String;I)V
    //   403: iload_3
    //   404: iconst_m1
    //   405: if_icmpeq +11 -> 416
    //   408: aload_0
    //   409: ldc_w 455
    //   412: iload_3
    //   413: invokestatic 447	com/android/internal/logging/MetricsLogger:histogram	(Landroid/content/Context;Ljava/lang/String;I)V
    //   416: aload 11
    //   418: astore_0
    //   419: aload 9
    //   421: ifnull +11 -> 432
    //   424: aload 9
    //   426: invokevirtual 427	java/io/BufferedReader:close	()V
    //   429: aload 11
    //   431: astore_0
    //   432: aload_0
    //   433: ifnull +13 -> 446
    //   436: aload_0
    //   437: athrow
    //   438: astore_0
    //   439: goto -222 -> 217
    //   442: astore_0
    //   443: goto -11 -> 432
    //   446: return
    //   447: astore_0
    //   448: aload 10
    //   450: ifnonnull +9 -> 459
    //   453: aload_0
    //   454: astore 11
    //   456: goto -248 -> 208
    //   459: aload 10
    //   461: astore 11
    //   463: aload 10
    //   465: aload_0
    //   466: if_acmpeq -258 -> 208
    //   469: aload 10
    //   471: aload_0
    //   472: invokevirtual 459	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   475: aload 10
    //   477: astore 11
    //   479: goto -271 -> 208
    //   482: aload 9
    //   484: athrow
    //   485: astore 9
    //   487: aload 13
    //   489: astore_0
    //   490: goto -298 -> 192
    //   493: astore 9
    //   495: aload 12
    //   497: astore_0
    //   498: goto -319 -> 179
    //   501: astore 11
    //   503: aload 9
    //   505: astore_0
    //   506: aload 11
    //   508: astore 9
    //   510: goto -318 -> 192
    //   513: iload_2
    //   514: iload_1
    //   515: iadd
    //   516: istore_2
    //   517: goto -473 -> 44
    //   520: iload_3
    //   521: iload_1
    //   522: iadd
    //   523: istore_3
    //   524: goto -480 -> 44
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	527	0	paramContext	Context
    //   63	460	1	i	int
    //   32	485	2	j	int
    //   34	490	3	k	int
    //   39	332	4	m	int
    //   36	320	5	n	int
    //   42	344	6	i1	int
    //   98	131	7	l	long
    //   29	454	9	localObject1	Object
    //   485	1	9	localObject2	Object
    //   493	11	9	localThrowable1	Throwable
    //   508	1	9	localObject3	Object
    //   4	1	10	localObject4	Object
    //   170	6	10	localThrowable2	Throwable
    //   186	290	10	localObject5	Object
    //   1	1	11	localObject6	Object
    //   182	7	11	localObject7	Object
    //   194	284	11	localObject8	Object
    //   501	6	11	localObject9	Object
    //   10	486	12	str1	String
    //   7	87	13	str2	String
    //   138	1	13	localNumberFormatException	NumberFormatException
    //   237	251	13	localArithmeticException	ArithmeticException
    // Exception table:
    //   from	to	target	type
    //   93	100	138	java/lang/NumberFormatException
    //   44	51	170	java/lang/Throwable
    //   56	64	170	java/lang/Throwable
    //   69	93	170	java/lang/Throwable
    //   93	100	170	java/lang/Throwable
    //   100	121	170	java/lang/Throwable
    //   121	132	170	java/lang/Throwable
    //   140	167	170	java/lang/Throwable
    //   228	234	170	java/lang/Throwable
    //   239	266	170	java/lang/Throwable
    //   269	280	170	java/lang/Throwable
    //   286	297	170	java/lang/Throwable
    //   303	314	170	java/lang/Throwable
    //   324	335	170	java/lang/Throwable
    //   351	360	170	java/lang/Throwable
    //   366	375	170	java/lang/Throwable
    //   381	390	170	java/lang/Throwable
    //   395	403	170	java/lang/Throwable
    //   408	416	170	java/lang/Throwable
    //   179	182	182	finally
    //   200	204	216	java/io/IOException
    //   213	216	216	java/io/IOException
    //   469	475	216	java/io/IOException
    //   482	485	216	java/io/IOException
    //   100	121	237	java/lang/ArithmeticException
    //   228	234	237	java/lang/ArithmeticException
    //   424	429	438	java/io/IOException
    //   436	438	438	java/io/IOException
    //   424	429	442	java/lang/Throwable
    //   200	204	447	java/lang/Throwable
    //   12	31	485	finally
    //   12	31	493	java/lang/Throwable
    //   44	51	501	finally
    //   56	64	501	finally
    //   69	93	501	finally
    //   93	100	501	finally
    //   100	121	501	finally
    //   121	132	501	finally
    //   140	167	501	finally
    //   228	234	501	finally
    //   239	266	501	finally
    //   269	280	501	finally
    //   286	297	501	finally
    //   303	314	501	finally
    //   324	335	501	finally
    //   351	360	501	finally
    //   366	375	501	finally
    //   381	390	501	finally
    //   395	403	501	finally
    //   408	416	501	finally
  }
  
  public static void processPackage(Context paramContext, File paramFile, ProgressListener paramProgressListener)
    throws IOException
  {
    processPackage(paramContext, paramFile, paramProgressListener, null);
  }
  
  public static void processPackage(Context paramContext, File paramFile, final ProgressListener paramProgressListener, Handler paramHandler)
    throws IOException
  {
    String str = paramFile.getCanonicalPath();
    if (!str.startsWith("/data/")) {
      return;
    }
    mScreenWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(26, "RecoverySystem-screen");
    mScreenWakeLock.acquire(60000L);
    Log.i("RecoverySystem", "keep screen on up to 60 seconds for OTA follow");
    RecoverySystem localRecoverySystem = (RecoverySystem)paramContext.getSystemService("recovery");
    paramFile = null;
    if (paramProgressListener != null) {
      if (paramHandler == null) {
        break label110;
      }
    }
    for (;;)
    {
      paramFile = new IRecoverySystemProgressListener.Stub()
      {
        int lastProgress = 0;
        long lastPublishTime = System.currentTimeMillis();
        
        public void onProgress(final int paramAnonymousInt)
        {
          final long l = System.currentTimeMillis();
          this.val$progressHandler.post(new Runnable()
          {
            public void run()
            {
              if ((paramAnonymousInt > RecoverySystem.2.this.lastProgress) && (l - RecoverySystem.2.this.lastPublishTime > 500L)) {}
              for (;;)
              {
                if (paramAnonymousInt == 100) {
                  Log.i("RecoverySystem", "100% complete process the ota package , notify the result");
                }
                RecoverySystem.2.this.lastProgress = paramAnonymousInt;
                RecoverySystem.2.this.lastPublishTime = l;
                this.val$listener.onProgress(paramAnonymousInt);
                do
                {
                  return;
                } while (paramAnonymousInt != 100);
              }
            }
          });
        }
      };
      if (localRecoverySystem.uncrypt(str, paramFile)) {
        break;
      }
      throw new IOException("process package failed");
      label110:
      paramHandler = new Handler(paramContext.getMainLooper());
    }
  }
  
  private void rebootRecoveryWithCommand(String paramString)
  {
    try
    {
      this.mService.rebootRecoveryWithCommand(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public static void rebootWipeAb(Context paramContext, File paramFile, String paramString)
    throws IOException
  {
    String str = null;
    if (!TextUtils.isEmpty(paramString)) {
      str = "--reason=" + sanitizeArg(paramString);
    }
    paramFile = paramFile.getCanonicalPath();
    bootCommand(paramContext, new String[] { "--wipe_ab", "--wipe_package=" + paramFile, str, "--locale=" + Locale.getDefault().toString() });
  }
  
  public static void rebootWipeCache(Context paramContext)
    throws IOException
  {
    rebootWipeCache(paramContext, paramContext.getPackageName());
  }
  
  public static void rebootWipeCache(Context paramContext, String paramString)
    throws IOException
  {
    String str = null;
    if (!TextUtils.isEmpty(paramString)) {
      str = "--reason=" + sanitizeArg(paramString);
    }
    bootCommand(paramContext, new String[] { "--wipe_cache", str, "--locale=" + Locale.getDefault().toString() });
  }
  
  public static void rebootWipeUserData(Context paramContext)
    throws IOException
  {
    rebootWipeUserData(paramContext, false, paramContext.getPackageName(), false);
  }
  
  public static void rebootWipeUserData(Context paramContext, String paramString)
    throws IOException
  {
    rebootWipeUserData(paramContext, false, paramString, false);
  }
  
  public static void rebootWipeUserData(Context paramContext, boolean paramBoolean)
    throws IOException
  {
    rebootWipeUserData(paramContext, paramBoolean, paramContext.getPackageName(), false);
  }
  
  public static void rebootWipeUserData(Context paramContext, boolean paramBoolean1, String paramString, boolean paramBoolean2)
    throws IOException
  {
    Object localObject1 = (UserManager)paramContext.getSystemService("user");
    if ((!paramBoolean2) && (((UserManager)localObject1).hasUserRestriction("no_factory_reset"))) {
      throw new SecurityException("Wiping data is not allowed for this user.");
    }
    localObject1 = new ConditionVariable();
    Object localObject2 = new Intent("android.intent.action.MASTER_CLEAR_NOTIFICATION");
    ((Intent)localObject2).addFlags(268435456);
    paramContext.sendOrderedBroadcastAsUser((Intent)localObject2, UserHandle.SYSTEM, "android.permission.MASTER_CLEAR", new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        this.val$condition.open();
      }
    }, null, 0, null, null);
    ((ConditionVariable)localObject1).block();
    localObject1 = null;
    if (paramBoolean1) {
      localObject1 = "--shutdown_after";
    }
    localObject2 = null;
    if (!TextUtils.isEmpty(paramString)) {
      localObject2 = "--reason=" + sanitizeArg(paramString);
    }
    bootCommand(paramContext, new String[] { localObject1, "--wipe_data", localObject2, "--locale=" + Locale.getDefault().toString() });
  }
  
  private static void recursiveDelete(File paramFile)
  {
    if (paramFile.isDirectory())
    {
      String[] arrayOfString = paramFile.list();
      int i = 0;
      while ((arrayOfString != null) && (i < arrayOfString.length))
      {
        recursiveDelete(new File(paramFile, arrayOfString[i]));
        i += 1;
      }
    }
    if (!paramFile.delete())
    {
      Log.e("RecoverySystem", "Can't delete: " + paramFile);
      return;
    }
    Log.i("RecoverySystem", "Deleted: " + paramFile);
  }
  
  private static String sanitizeArg(String paramString)
  {
    return paramString.replace('\000', '?').replace('\n', '?');
  }
  
  public static void scheduleUpdateOnBoot(Context paramContext, File paramFile)
    throws IOException
  {
    String str = paramFile.getCanonicalPath();
    boolean bool = str.endsWith("_s.zip");
    paramFile = str;
    if (str.startsWith("/data/")) {
      paramFile = "@/cache/recovery/block.map";
    }
    paramFile = "--update_package=" + paramFile + "\n";
    str = "--locale=" + Locale.getDefault().toString() + "\n";
    str = paramFile + str;
    paramFile = str;
    if (bool) {
      paramFile = str + "--security\n";
    }
    if (!((RecoverySystem)paramContext.getSystemService("recovery")).setupBcb(paramFile)) {
      throw new IOException("schedule update on boot failed");
    }
  }
  
  private boolean setupBcb(String paramString)
  {
    try
    {
      boolean bool = this.mService.setupBcb(paramString);
      return bool;
    }
    catch (RemoteException paramString) {}
    return false;
  }
  
  private boolean uncrypt(String paramString, IRecoverySystemProgressListener paramIRecoverySystemProgressListener)
  {
    try
    {
      boolean bool = this.mService.uncrypt(paramString, paramIRecoverySystemProgressListener);
      return bool;
    }
    catch (RemoteException paramString) {}
    return false;
  }
  
  public static void verifyPackage(File paramFile1, ProgressListener paramProgressListener, File paramFile2)
    throws IOException, GeneralSecurityException
  {
    long l1 = paramFile1.length();
    final RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile1, "r");
    final long l2;
    try
    {
      l2 = System.currentTimeMillis();
      if (paramProgressListener != null) {
        paramProgressListener.onProgress(0);
      }
      localRandomAccessFile.seek(l1 - 6L);
      paramFile1 = new byte[6];
      localRandomAccessFile.readFully(paramFile1);
      if ((paramFile1[2] != -1) || (paramFile1[3] != -1)) {
        throw new SignatureException("no signature in file (no footer)");
      }
    }
    finally
    {
      localRandomAccessFile.close();
    }
    int k = paramFile1[4] & 0xFF | (paramFile1[5] & 0xFF) << 8;
    int j = paramFile1[0] & 0xFF | (paramFile1[1] & 0xFF) << 8;
    paramFile1 = new byte[k + 22];
    localRandomAccessFile.seek(l1 - (k + 22));
    localRandomAccessFile.readFully(paramFile1);
    if ((paramFile1[0] != 80) || (paramFile1[1] != 75)) {
      throw new SignatureException("no signature in file (bad footer)");
    }
    for (;;)
    {
      int i;
      if (i < paramFile1.length - 3)
      {
        if ((paramFile1[i] == 80) && (paramFile1[(i + 1)] == 75) && (paramFile1[(i + 2)] == 5) && (paramFile1[(i + 3)] == 6)) {
          throw new SignatureException("EOCD marker found after start of EOCD");
        }
      }
      else
      {
        PKCS7 localPKCS7 = new PKCS7(new ByteArrayInputStream(paramFile1, k + 22 - j, j));
        paramFile1 = localPKCS7.getCertificates();
        if ((paramFile1 == null) || (paramFile1.length == 0)) {
          throw new SignatureException("signature contains no certificates");
        }
        PublicKey localPublicKey = paramFile1[0].getPublicKey();
        paramFile1 = localPKCS7.getSignerInfos();
        if ((paramFile1 == null) || (paramFile1.length == 0)) {
          throw new SignatureException("signature contains no signedData");
        }
        SignerInfo localSignerInfo = paramFile1[0];
        j = 0;
        paramFile1 = paramFile2;
        if (paramFile2 == null) {
          paramFile1 = DEFAULT_KEYSTORE;
        }
        paramFile1 = getTrustedCerts(paramFile1).iterator();
        do
        {
          i = j;
          if (!paramFile1.hasNext()) {
            break;
          }
        } while (!((X509Certificate)paramFile1.next()).getPublicKey().equals(localPublicKey));
        i = 1;
        if (i == 0) {
          throw new SignatureException("signature doesn't match any trusted key");
        }
        localRandomAccessFile.seek(0L);
        paramFile1 = localPKCS7.verify(localSignerInfo, new InputStream()
        {
          int lastPercent = 0;
          long lastPublishTime = localRandomAccessFile;
          long soFar = 0L;
          long toRead = this.val$fileLen - l2 - 2L;
          
          public int read()
            throws IOException
          {
            throw new UnsupportedOperationException();
          }
          
          public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
            throws IOException
          {
            if (this.soFar >= this.toRead) {
              return -1;
            }
            if (Thread.currentThread().isInterrupted()) {
              return -1;
            }
            int i = paramAnonymousInt2;
            if (this.soFar + paramAnonymousInt2 > this.toRead) {
              i = (int)(this.toRead - this.soFar);
            }
            paramAnonymousInt1 = this.val$raf.read(paramAnonymousArrayOfByte, paramAnonymousInt1, i);
            this.soFar += paramAnonymousInt1;
            if (this.val$listenerForInner != null)
            {
              long l = System.currentTimeMillis();
              paramAnonymousInt2 = (int)(this.soFar * 100L / this.toRead);
              if ((paramAnonymousInt2 > this.lastPercent) && (l - this.lastPublishTime > 500L))
              {
                this.lastPercent = paramAnonymousInt2;
                this.lastPublishTime = l;
                this.val$listenerForInner.onProgress(this.lastPercent);
              }
            }
            return paramAnonymousInt1;
          }
        });
        boolean bool = Thread.interrupted();
        if (paramProgressListener != null) {
          paramProgressListener.onProgress(100);
        }
        if (bool) {
          throw new SignatureException("verification was interrupted");
        }
        if (paramFile1 == null) {
          throw new SignatureException("signature digest verification failed");
        }
        localRandomAccessFile.close();
        return;
        if ((paramFile1[2] != 5) || (paramFile1[3] != 6)) {
          break;
        }
        i = 4;
        continue;
      }
      i += 1;
    }
  }
  
  public static abstract interface ProgressListener
  {
    public abstract void onProgress(int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/RecoverySystem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */