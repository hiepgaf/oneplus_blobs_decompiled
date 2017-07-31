package com.android.server;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.LocalSocketAddress.Namespace;
import android.os.IRecoverySystem.Stub;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.Slog;
import java.io.IOException;

public final class RecoverySystemService
  extends SystemService
{
  private static final boolean DEBUG = false;
  private static final String INIT_SERVICE_CLEAR_BCB = "init.svc.clear-bcb";
  private static final String INIT_SERVICE_SETUP_BCB = "init.svc.setup-bcb";
  private static final String INIT_SERVICE_UNCRYPT = "init.svc.uncrypt";
  private static final int SOCKET_CONNECTION_MAX_RETRY = 30;
  private static final String TAG = "RecoverySystemService";
  private static final String UNCRYPT_SOCKET = "uncrypt";
  private static final Object sRequestLock = new Object();
  private Context mContext;
  
  public RecoverySystemService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
  }
  
  public void onStart()
  {
    publishBinderService("recovery", new BinderService(null));
  }
  
  private final class BinderService
    extends IRecoverySystem.Stub
  {
    private BinderService() {}
    
    private boolean checkAndWaitForUncryptService()
    {
      int i = 0;
      for (;;)
      {
        if (i < 30)
        {
          String str1 = SystemProperties.get("init.svc.uncrypt");
          String str2 = SystemProperties.get("init.svc.setup-bcb");
          String str3 = SystemProperties.get("init.svc.clear-bcb");
          if ((!"running".equals(str1)) && (!"running".equals(str2))) {}
          for (boolean bool = "running".equals(str3); !bool; bool = true) {
            return true;
          }
          try
          {
            Thread.sleep(1000L);
            i += 1;
          }
          catch (InterruptedException localInterruptedException)
          {
            for (;;)
            {
              Slog.w("RecoverySystemService", "Interrupted:", localInterruptedException);
            }
          }
        }
      }
      return false;
    }
    
    private LocalSocket connectService()
    {
      LocalSocket localLocalSocket = new LocalSocket();
      int k = 0;
      int i = 0;
      for (;;)
      {
        int j = k;
        if (i < 30) {}
        try
        {
          localLocalSocket.connect(new LocalSocketAddress("uncrypt", LocalSocketAddress.Namespace.RESERVED));
          j = 1;
          if (j == 0)
          {
            Slog.e("RecoverySystemService", "Timed out connecting to uncrypt socket");
            return null;
          }
        }
        catch (IOException localIOException)
        {
          try
          {
            Thread.sleep(1000L);
            i += 1;
          }
          catch (InterruptedException localInterruptedException)
          {
            for (;;)
            {
              Slog.w("RecoverySystemService", "Interrupted:", localInterruptedException);
            }
          }
        }
      }
      return localLocalSocket;
    }
    
    /* Error */
    private boolean setupOrClearBcb(boolean paramBoolean, String paramString)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/android/server/RecoverySystemService$BinderService:this$0	Lcom/android/server/RecoverySystemService;
      //   4: invokestatic 98	com/android/server/RecoverySystemService:-get0	(Lcom/android/server/RecoverySystemService;)Landroid/content/Context;
      //   7: ldc 100
      //   9: aconst_null
      //   10: invokevirtual 106	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
      //   13: aload_0
      //   14: invokespecial 108	com/android/server/RecoverySystemService$BinderService:checkAndWaitForUncryptService	()Z
      //   17: ifne +13 -> 30
      //   20: ldc 54
      //   22: ldc 110
      //   24: invokestatic 92	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   27: pop
      //   28: iconst_0
      //   29: ireturn
      //   30: iload_1
      //   31: ifeq +31 -> 62
      //   34: ldc 112
      //   36: ldc 114
      //   38: invokestatic 117	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
      //   41: aload_0
      //   42: invokespecial 119	com/android/server/RecoverySystemService$BinderService:connectService	()Landroid/net/LocalSocket;
      //   45: astore 10
      //   47: aload 10
      //   49: ifnonnull +23 -> 72
      //   52: ldc 54
      //   54: ldc 121
      //   56: invokestatic 92	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   59: pop
      //   60: iconst_0
      //   61: ireturn
      //   62: ldc 112
      //   64: ldc 123
      //   66: invokestatic 117	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
      //   69: goto -28 -> 41
      //   72: aconst_null
      //   73: astore 5
      //   75: aconst_null
      //   76: astore 9
      //   78: aconst_null
      //   79: astore 6
      //   81: aconst_null
      //   82: astore 7
      //   84: aconst_null
      //   85: astore 8
      //   87: new 125	java/io/DataInputStream
      //   90: dup
      //   91: aload 10
      //   93: invokevirtual 129	android/net/LocalSocket:getInputStream	()Ljava/io/InputStream;
      //   96: invokespecial 132	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
      //   99: astore 4
      //   101: new 134	java/io/DataOutputStream
      //   104: dup
      //   105: aload 10
      //   107: invokevirtual 138	android/net/LocalSocket:getOutputStream	()Ljava/io/OutputStream;
      //   110: invokespecial 141	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
      //   113: astore 5
      //   115: iload_1
      //   116: ifeq +23 -> 139
      //   119: aload 5
      //   121: aload_2
      //   122: invokevirtual 145	java/lang/String:length	()I
      //   125: invokevirtual 149	java/io/DataOutputStream:writeInt	(I)V
      //   128: aload 5
      //   130: aload_2
      //   131: invokevirtual 153	java/io/DataOutputStream:writeBytes	(Ljava/lang/String;)V
      //   134: aload 5
      //   136: invokevirtual 156	java/io/DataOutputStream:flush	()V
      //   139: aload 4
      //   141: invokevirtual 159	java/io/DataInputStream:readInt	()I
      //   144: istore_3
      //   145: aload 5
      //   147: iconst_0
      //   148: invokevirtual 149	java/io/DataOutputStream:writeInt	(I)V
      //   151: iload_3
      //   152: bipush 100
      //   154: if_icmpne +67 -> 221
      //   157: new 161	java/lang/StringBuilder
      //   160: dup
      //   161: invokespecial 162	java/lang/StringBuilder:<init>	()V
      //   164: ldc -92
      //   166: invokevirtual 168	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   169: astore 6
      //   171: iload_1
      //   172: ifeq +43 -> 215
      //   175: ldc -86
      //   177: astore_2
      //   178: ldc 54
      //   180: aload 6
      //   182: aload_2
      //   183: invokevirtual 168	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   186: ldc -84
      //   188: invokevirtual 168	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   191: invokevirtual 176	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   194: invokestatic 179	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   197: pop
      //   198: aload 4
      //   200: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   203: aload 5
      //   205: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   208: aload 10
      //   210: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   213: iconst_1
      //   214: ireturn
      //   215: ldc -69
      //   217: astore_2
      //   218: goto -40 -> 178
      //   221: ldc 54
      //   223: new 161	java/lang/StringBuilder
      //   226: dup
      //   227: invokespecial 162	java/lang/StringBuilder:<init>	()V
      //   230: ldc -67
      //   232: invokevirtual 168	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   235: iload_3
      //   236: invokevirtual 192	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   239: invokevirtual 176	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   242: invokestatic 92	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   245: pop
      //   246: aload 4
      //   248: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   251: aload 5
      //   253: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   256: aload 10
      //   258: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   261: iconst_0
      //   262: ireturn
      //   263: astore 7
      //   265: aload 8
      //   267: astore 4
      //   269: aload 9
      //   271: astore_2
      //   272: aload_2
      //   273: astore 5
      //   275: aload 4
      //   277: astore 6
      //   279: ldc 54
      //   281: ldc -62
      //   283: aload 7
      //   285: invokestatic 196	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   288: pop
      //   289: aload_2
      //   290: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   293: aload 4
      //   295: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   298: aload 10
      //   300: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   303: iconst_0
      //   304: ireturn
      //   305: astore_2
      //   306: aload 5
      //   308: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   311: aload 6
      //   313: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   316: aload 10
      //   318: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   321: aload_2
      //   322: athrow
      //   323: astore_2
      //   324: aload 4
      //   326: astore 5
      //   328: aload 7
      //   330: astore 6
      //   332: goto -26 -> 306
      //   335: astore_2
      //   336: aload 5
      //   338: astore 6
      //   340: aload 4
      //   342: astore 5
      //   344: goto -38 -> 306
      //   347: astore 7
      //   349: aload 4
      //   351: astore_2
      //   352: aload 8
      //   354: astore 4
      //   356: goto -84 -> 272
      //   359: astore 7
      //   361: aload 4
      //   363: astore_2
      //   364: aload 5
      //   366: astore 4
      //   368: goto -96 -> 272
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	371	0	this	BinderService
      //   0	371	1	paramBoolean	boolean
      //   0	371	2	paramString	String
      //   144	92	3	i	int
      //   99	268	4	localObject1	Object
      //   73	292	5	localObject2	Object
      //   79	260	6	localObject3	Object
      //   82	1	7	localObject4	Object
      //   263	66	7	localIOException1	IOException
      //   347	1	7	localIOException2	IOException
      //   359	1	7	localIOException3	IOException
      //   85	268	8	localObject5	Object
      //   76	194	9	localObject6	Object
      //   45	272	10	localLocalSocket	LocalSocket
      // Exception table:
      //   from	to	target	type
      //   87	101	263	java/io/IOException
      //   87	101	305	finally
      //   279	289	305	finally
      //   101	115	323	finally
      //   119	139	335	finally
      //   139	151	335	finally
      //   157	171	335	finally
      //   178	198	335	finally
      //   221	246	335	finally
      //   101	115	347	java/io/IOException
      //   119	139	359	java/io/IOException
      //   139	151	359	java/io/IOException
      //   157	171	359	java/io/IOException
      //   178	198	359	java/io/IOException
      //   221	246	359	java/io/IOException
    }
    
    public boolean clearBcb()
    {
      synchronized ()
      {
        boolean bool = setupOrClearBcb(false, null);
        return bool;
      }
    }
    
    public void rebootRecoveryWithCommand(String paramString)
    {
      synchronized ()
      {
        boolean bool = setupOrClearBcb(true, paramString);
        if (!bool) {
          return;
        }
        ((PowerManager)RecoverySystemService.-get0(RecoverySystemService.this).getSystemService("power")).reboot("recovery");
        return;
      }
    }
    
    public boolean setupBcb(String paramString)
    {
      synchronized ()
      {
        boolean bool = setupOrClearBcb(true, paramString);
        return bool;
      }
    }
    
    /* Error */
    public boolean uncrypt(String paramString, android.os.IRecoverySystemProgressListener paramIRecoverySystemProgressListener)
    {
      // Byte code:
      //   0: invokestatic 201	com/android/server/RecoverySystemService:-get1	()Ljava/lang/Object;
      //   3: astore 10
      //   5: aload 10
      //   7: monitorenter
      //   8: aload_0
      //   9: getfield 13	com/android/server/RecoverySystemService$BinderService:this$0	Lcom/android/server/RecoverySystemService;
      //   12: invokestatic 98	com/android/server/RecoverySystemService:-get0	(Lcom/android/server/RecoverySystemService;)Landroid/content/Context;
      //   15: ldc 100
      //   17: aconst_null
      //   18: invokevirtual 106	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
      //   21: aload_0
      //   22: invokespecial 108	com/android/server/RecoverySystemService$BinderService:checkAndWaitForUncryptService	()Z
      //   25: ifne +16 -> 41
      //   28: ldc 54
      //   30: ldc 110
      //   32: invokestatic 92	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   35: pop
      //   36: aload 10
      //   38: monitorexit
      //   39: iconst_0
      //   40: ireturn
      //   41: getstatic 230	android/os/RecoverySystem:UNCRYPT_PACKAGE_FILE	Ljava/io/File;
      //   44: invokevirtual 235	java/io/File:delete	()Z
      //   47: pop
      //   48: aconst_null
      //   49: astore 6
      //   51: aconst_null
      //   52: astore 8
      //   54: aconst_null
      //   55: astore 9
      //   57: aconst_null
      //   58: astore 7
      //   60: new 237	java/io/FileWriter
      //   63: dup
      //   64: getstatic 230	android/os/RecoverySystem:UNCRYPT_PACKAGE_FILE	Ljava/io/File;
      //   67: invokespecial 240	java/io/FileWriter:<init>	(Ljava/io/File;)V
      //   70: astore 5
      //   72: aload 5
      //   74: new 161	java/lang/StringBuilder
      //   77: dup
      //   78: invokespecial 162	java/lang/StringBuilder:<init>	()V
      //   81: aload_1
      //   82: invokevirtual 168	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   85: ldc -14
      //   87: invokevirtual 168	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   90: invokevirtual 176	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   93: invokevirtual 245	java/io/FileWriter:write	(Ljava/lang/String;)V
      //   96: aload 8
      //   98: astore_1
      //   99: aload 5
      //   101: ifnull +11 -> 112
      //   104: aload 5
      //   106: invokevirtual 248	java/io/FileWriter:close	()V
      //   109: aload 8
      //   111: astore_1
      //   112: aload_1
      //   113: ifnull +117 -> 230
      //   116: aload_1
      //   117: athrow
      //   118: astore_1
      //   119: ldc 54
      //   121: new 161	java/lang/StringBuilder
      //   124: dup
      //   125: invokespecial 162	java/lang/StringBuilder:<init>	()V
      //   128: ldc -6
      //   130: invokevirtual 168	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   133: getstatic 230	android/os/RecoverySystem:UNCRYPT_PACKAGE_FILE	Ljava/io/File;
      //   136: invokevirtual 253	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   139: ldc -1
      //   141: invokevirtual 168	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   144: invokevirtual 176	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   147: aload_1
      //   148: invokestatic 196	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   151: pop
      //   152: aload 10
      //   154: monitorexit
      //   155: iconst_0
      //   156: ireturn
      //   157: astore_1
      //   158: goto -46 -> 112
      //   161: astore_2
      //   162: aload 7
      //   164: astore_1
      //   165: aload_2
      //   166: athrow
      //   167: astore 6
      //   169: aload_2
      //   170: astore 5
      //   172: aload 6
      //   174: astore_2
      //   175: aload 5
      //   177: astore 6
      //   179: aload_1
      //   180: ifnull +11 -> 191
      //   183: aload_1
      //   184: invokevirtual 248	java/io/FileWriter:close	()V
      //   187: aload 5
      //   189: astore 6
      //   191: aload 6
      //   193: ifnull +35 -> 228
      //   196: aload 6
      //   198: athrow
      //   199: aload 5
      //   201: astore 6
      //   203: aload 5
      //   205: aload_1
      //   206: if_acmpeq -15 -> 191
      //   209: aload 5
      //   211: aload_1
      //   212: invokevirtual 259	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
      //   215: aload 5
      //   217: astore 6
      //   219: goto -28 -> 191
      //   222: astore_1
      //   223: aload 10
      //   225: monitorexit
      //   226: aload_1
      //   227: athrow
      //   228: aload_2
      //   229: athrow
      //   230: ldc 112
      //   232: ldc 73
      //   234: invokestatic 117	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
      //   237: aload_0
      //   238: invokespecial 119	com/android/server/RecoverySystemService$BinderService:connectService	()Landroid/net/LocalSocket;
      //   241: astore 11
      //   243: aload 11
      //   245: ifnonnull +16 -> 261
      //   248: ldc 54
      //   250: ldc 121
      //   252: invokestatic 92	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   255: pop
      //   256: aload 10
      //   258: monitorexit
      //   259: iconst_0
      //   260: ireturn
      //   261: aconst_null
      //   262: astore 5
      //   264: aconst_null
      //   265: astore 9
      //   267: aconst_null
      //   268: astore 6
      //   270: aconst_null
      //   271: astore 8
      //   273: aconst_null
      //   274: astore 7
      //   276: new 125	java/io/DataInputStream
      //   279: dup
      //   280: aload 11
      //   282: invokevirtual 129	android/net/LocalSocket:getInputStream	()Ljava/io/InputStream;
      //   285: invokespecial 132	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
      //   288: astore_1
      //   289: new 134	java/io/DataOutputStream
      //   292: dup
      //   293: aload 11
      //   295: invokevirtual 138	android/net/LocalSocket:getOutputStream	()Ljava/io/OutputStream;
      //   298: invokespecial 141	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
      //   301: astore 5
      //   303: ldc_w 260
      //   306: istore_3
      //   307: aload_1
      //   308: invokevirtual 159	java/io/DataInputStream:readInt	()I
      //   311: istore 4
      //   313: iload 4
      //   315: iload_3
      //   316: if_icmpne +10 -> 326
      //   319: iload_3
      //   320: ldc_w 260
      //   323: if_icmpne -16 -> 307
      //   326: iload 4
      //   328: istore_3
      //   329: iload 4
      //   331: iflt +144 -> 475
      //   334: iload 4
      //   336: bipush 100
      //   338: if_icmpgt +137 -> 475
      //   341: ldc 54
      //   343: new 161	java/lang/StringBuilder
      //   346: dup
      //   347: invokespecial 162	java/lang/StringBuilder:<init>	()V
      //   350: ldc_w 262
      //   353: invokevirtual 168	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   356: iload 4
      //   358: invokevirtual 192	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   361: invokevirtual 176	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   364: invokestatic 179	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   367: pop
      //   368: aload_2
      //   369: ifnull +11 -> 380
      //   372: aload_2
      //   373: iload 4
      //   375: invokeinterface 267 2 0
      //   380: iload 4
      //   382: bipush 100
      //   384: if_icmpne -77 -> 307
      //   387: ldc 54
      //   389: ldc_w 269
      //   392: invokestatic 179	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   395: pop
      //   396: aload 5
      //   398: iconst_0
      //   399: invokevirtual 149	java/io/DataOutputStream:writeInt	(I)V
      //   402: aload_1
      //   403: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   406: aload 5
      //   408: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   411: aload 11
      //   413: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   416: aload 10
      //   418: monitorexit
      //   419: iconst_1
      //   420: ireturn
      //   421: astore 6
      //   423: ldc 54
      //   425: ldc_w 271
      //   428: invokestatic 273	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   431: pop
      //   432: goto -52 -> 380
      //   435: astore 7
      //   437: aload 5
      //   439: astore_2
      //   440: aload_1
      //   441: astore 5
      //   443: aload_2
      //   444: astore 6
      //   446: ldc 54
      //   448: ldc_w 275
      //   451: aload 7
      //   453: invokestatic 196	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   456: pop
      //   457: aload_1
      //   458: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   461: aload_2
      //   462: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   465: aload 11
      //   467: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   470: aload 10
      //   472: monitorexit
      //   473: iconst_0
      //   474: ireturn
      //   475: ldc 54
      //   477: new 161	java/lang/StringBuilder
      //   480: dup
      //   481: invokespecial 162	java/lang/StringBuilder:<init>	()V
      //   484: ldc -67
      //   486: invokevirtual 168	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   489: iload 4
      //   491: invokevirtual 192	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   494: invokevirtual 176	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   497: invokestatic 92	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   500: pop
      //   501: aload 5
      //   503: iconst_0
      //   504: invokevirtual 149	java/io/DataOutputStream:writeInt	(I)V
      //   507: aload_1
      //   508: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   511: aload 5
      //   513: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   516: aload 11
      //   518: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   521: aload 10
      //   523: monitorexit
      //   524: iconst_0
      //   525: ireturn
      //   526: astore_1
      //   527: aload 5
      //   529: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   532: aload 6
      //   534: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   537: aload 11
      //   539: invokestatic 185	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   542: aload_1
      //   543: athrow
      //   544: astore_2
      //   545: aload_1
      //   546: astore 5
      //   548: aload 8
      //   550: astore 6
      //   552: aload_2
      //   553: astore_1
      //   554: goto -27 -> 527
      //   557: astore_2
      //   558: aload 5
      //   560: astore 6
      //   562: aload_1
      //   563: astore 5
      //   565: aload_2
      //   566: astore_1
      //   567: goto -40 -> 527
      //   570: astore 5
      //   572: aload 9
      //   574: astore_1
      //   575: aload 7
      //   577: astore_2
      //   578: aload 5
      //   580: astore 7
      //   582: goto -142 -> 440
      //   585: astore 5
      //   587: aload 7
      //   589: astore_2
      //   590: aload 5
      //   592: astore 7
      //   594: goto -154 -> 440
      //   597: astore_2
      //   598: aload 9
      //   600: astore_1
      //   601: aload 6
      //   603: astore 5
      //   605: goto -430 -> 175
      //   608: astore_2
      //   609: aload 5
      //   611: astore_1
      //   612: aload 6
      //   614: astore 5
      //   616: goto -441 -> 175
      //   619: astore_2
      //   620: aload 5
      //   622: astore_1
      //   623: goto -458 -> 165
      //   626: astore_1
      //   627: goto -508 -> 119
      //   630: astore_1
      //   631: aload 5
      //   633: ifnonnull -434 -> 199
      //   636: aload_1
      //   637: astore 6
      //   639: goto -448 -> 191
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	642	0	this	BinderService
      //   0	642	1	paramString	String
      //   0	642	2	paramIRecoverySystemProgressListener	android.os.IRecoverySystemProgressListener
      //   306	23	3	i	int
      //   311	179	4	j	int
      //   70	494	5	localObject1	Object
      //   570	9	5	localIOException1	IOException
      //   585	6	5	localIOException2	IOException
      //   603	29	5	localObject2	Object
      //   49	1	6	localObject3	Object
      //   167	6	6	localObject4	Object
      //   177	92	6	localObject5	Object
      //   421	1	6	localRemoteException	android.os.RemoteException
      //   444	194	6	localObject6	Object
      //   58	217	7	localObject7	Object
      //   435	141	7	localIOException3	IOException
      //   580	13	7	localObject8	Object
      //   52	497	8	localObject9	Object
      //   55	544	9	localObject10	Object
      //   3	519	10	localObject11	Object
      //   241	297	11	localLocalSocket	LocalSocket
      // Exception table:
      //   from	to	target	type
      //   104	109	118	java/io/IOException
      //   116	118	118	java/io/IOException
      //   104	109	157	java/lang/Throwable
      //   60	72	161	java/lang/Throwable
      //   165	167	167	finally
      //   8	36	222	finally
      //   41	48	222	finally
      //   104	109	222	finally
      //   116	118	222	finally
      //   119	152	222	finally
      //   183	187	222	finally
      //   196	199	222	finally
      //   209	215	222	finally
      //   228	230	222	finally
      //   230	243	222	finally
      //   248	256	222	finally
      //   402	416	222	finally
      //   457	470	222	finally
      //   507	521	222	finally
      //   527	544	222	finally
      //   372	380	421	android/os/RemoteException
      //   307	313	435	java/io/IOException
      //   341	368	435	java/io/IOException
      //   372	380	435	java/io/IOException
      //   387	402	435	java/io/IOException
      //   423	432	435	java/io/IOException
      //   475	507	435	java/io/IOException
      //   276	289	526	finally
      //   446	457	526	finally
      //   289	303	544	finally
      //   307	313	557	finally
      //   341	368	557	finally
      //   372	380	557	finally
      //   387	402	557	finally
      //   423	432	557	finally
      //   475	507	557	finally
      //   276	289	570	java/io/IOException
      //   289	303	585	java/io/IOException
      //   60	72	597	finally
      //   72	96	608	finally
      //   72	96	619	java/lang/Throwable
      //   183	187	626	java/io/IOException
      //   196	199	626	java/io/IOException
      //   209	215	626	java/io/IOException
      //   228	230	626	java/io/IOException
      //   183	187	630	java/lang/Throwable
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/RecoverySystemService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */