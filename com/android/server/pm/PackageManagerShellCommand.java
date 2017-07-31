package com.android.server.pm;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.IIntentSender.Stub;
import android.content.Intent;
import android.content.Intent.CommandOptionHandler;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.IPackageInstaller;
import android.content.pm.IPackageManager;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller.Session;
import android.content.pm.PackageInstaller.SessionParams;
import android.content.pm.PackageItemInfo;
import android.content.pm.ParceledListSlice;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.PrintWriterPrinter;
import dalvik.system.DexFile;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;

class PackageManagerShellCommand
  extends ShellCommand
{
  boolean mBrief;
  boolean mComponents;
  final IPackageManager mInterface;
  private final WeakHashMap<String, Resources> mResourceCache = new WeakHashMap();
  int mTargetUser;
  
  PackageManagerShellCommand(PackageManagerService paramPackageManagerService)
  {
    this.mInterface = paramPackageManagerService;
  }
  
  private static String checkAbiArgument(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Missing ABI argument");
    }
    if ("-".equals(paramString)) {
      return paramString;
    }
    String[] arrayOfString = Build.SUPPORTED_ABIS;
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      if (arrayOfString[i].equals(paramString)) {
        return paramString;
      }
      i += 1;
    }
    throw new IllegalArgumentException("ABI " + paramString + " not supported on this device");
  }
  
  /* Error */
  private int doAbandonSession(int paramInt, boolean paramBoolean)
    throws RemoteException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 95	com/android/server/pm/PackageManagerShellCommand:getOutPrintWriter	()Ljava/io/PrintWriter;
    //   4: astore 5
    //   6: aconst_null
    //   7: astore_3
    //   8: new 97	android/content/pm/PackageInstaller$Session
    //   11: dup
    //   12: aload_0
    //   13: getfield 42	com/android/server/pm/PackageManagerShellCommand:mInterface	Landroid/content/pm/IPackageManager;
    //   16: invokeinterface 103 1 0
    //   21: iload_1
    //   22: invokeinterface 109 2 0
    //   27: invokespecial 112	android/content/pm/PackageInstaller$Session:<init>	(Landroid/content/pm/IPackageInstallerSession;)V
    //   30: astore 4
    //   32: aload 4
    //   34: invokevirtual 115	android/content/pm/PackageInstaller$Session:abandon	()V
    //   37: iload_2
    //   38: ifeq +10 -> 48
    //   41: aload 5
    //   43: ldc 117
    //   45: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   48: aload 4
    //   50: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   53: iconst_0
    //   54: ireturn
    //   55: astore 4
    //   57: aload_3
    //   58: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   61: aload 4
    //   63: athrow
    //   64: astore 5
    //   66: aload 4
    //   68: astore_3
    //   69: aload 5
    //   71: astore 4
    //   73: goto -16 -> 57
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	76	0	this	PackageManagerShellCommand
    //   0	76	1	paramInt	int
    //   0	76	2	paramBoolean	boolean
    //   7	62	3	localObject1	Object
    //   30	19	4	localSession	PackageInstaller.Session
    //   55	12	4	localObject2	Object
    //   71	1	4	localObject3	Object
    //   4	38	5	localPrintWriter	PrintWriter
    //   64	6	5	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   8	32	55	finally
    //   32	37	64	finally
    //   41	48	64	finally
  }
  
  private int doCommitSession(int paramInt, boolean paramBoolean)
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    Object localObject2 = null;
    try
    {
      localObject1 = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(paramInt));
      try
      {
        localObject2 = new LocalIntentReceiver(null);
        ((PackageInstaller.Session)localObject1).commit(((LocalIntentReceiver)localObject2).getIntentSender());
        localObject2 = ((LocalIntentReceiver)localObject2).getResult();
        paramInt = ((Intent)localObject2).getIntExtra("android.content.pm.extra.STATUS", 1);
        if (paramInt == 0) {
          if (paramBoolean) {
            localPrintWriter.println("Success");
          }
        }
        for (;;)
        {
          IoUtils.closeQuietly((AutoCloseable)localObject1);
          return paramInt;
          localPrintWriter.println("Failure [" + ((Intent)localObject2).getStringExtra("android.content.pm.extra.STATUS_MESSAGE") + "]");
        }
        IoUtils.closeQuietly((AutoCloseable)localObject1);
      }
      finally {}
    }
    finally
    {
      Object localObject1 = localObject3;
      Object localObject4 = localObject5;
    }
    throw ((Throwable)localObject3);
  }
  
  private int doCreateSession(PackageInstaller.SessionParams paramSessionParams, String paramString, int paramInt)
    throws RemoteException
  {
    int i = translateUserId(paramInt, "runInstallCreate");
    paramInt = i;
    if (i == -1)
    {
      paramInt = 0;
      paramSessionParams.installFlags |= 0x40;
    }
    return this.mInterface.getPackageInstaller().createSession(paramSessionParams, paramString, paramInt);
  }
  
  private void doListPermissions(ArrayList<String> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt1, int paramInt2)
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    int n = paramArrayList.size();
    int j = 0;
    while (j < n)
    {
      String str2 = (String)paramArrayList.get(j);
      String str1 = "";
      Object localObject2;
      int i;
      int k;
      label168:
      PermissionInfo localPermissionInfo;
      int m;
      if (paramBoolean1)
      {
        if (j > 0) {
          localPrintWriter.println("");
        }
        if (str2 == null) {
          break label429;
        }
        localObject1 = this.mInterface.getPermissionGroupInfo(str2, 0);
        if (!paramBoolean3) {
          break label254;
        }
        if (getResources((PackageItemInfo)localObject1) != null)
        {
          localPrintWriter.print(loadText((PackageItemInfo)localObject1, ((PermissionGroupInfo)localObject1).labelRes, ((PermissionGroupInfo)localObject1).nonLocalizedLabel) + ": ");
          str1 = "  ";
        }
      }
      else
      {
        localObject2 = this.mInterface.queryPermissionsByGroup((String)paramArrayList.get(j), 0).getList();
        int i1 = ((List)localObject2).size();
        i = 1;
        k = 0;
        if (k >= i1) {
          break label833;
        }
        localPermissionInfo = (PermissionInfo)((List)localObject2).get(k);
        if ((!paramBoolean1) || (str2 != null) || (localPermissionInfo.group == null)) {
          break label482;
        }
        m = i;
      }
      for (;;)
      {
        k += 1;
        i = m;
        break label168;
        localPrintWriter.print(((PermissionGroupInfo)localObject1).name + ": ");
        break;
        label254:
        localObject2 = new StringBuilder();
        if (paramBoolean2) {}
        for (str1 = "+ ";; str1 = "")
        {
          localPrintWriter.println(str1 + "group:" + ((PermissionGroupInfo)localObject1).name);
          if (!paramBoolean2) {
            break;
          }
          localPrintWriter.println("  package:" + ((PermissionGroupInfo)localObject1).packageName);
          if (getResources((PackageItemInfo)localObject1) == null) {
            break;
          }
          localPrintWriter.println("  label:" + loadText((PackageItemInfo)localObject1, ((PermissionGroupInfo)localObject1).labelRes, ((PermissionGroupInfo)localObject1).nonLocalizedLabel));
          localPrintWriter.println("  description:" + loadText((PackageItemInfo)localObject1, ((PermissionGroupInfo)localObject1).descriptionRes, ((PermissionGroupInfo)localObject1).nonLocalizedDescription));
          break;
        }
        label429:
        localObject1 = new StringBuilder();
        if ((!paramBoolean2) || (paramBoolean3)) {}
        for (str1 = "";; str1 = "+ ")
        {
          localPrintWriter.println(str1 + "ungrouped:");
          break;
        }
        label482:
        int i2 = localPermissionInfo.protectionLevel & 0xF;
        m = i;
        if (i2 >= paramInt1)
        {
          m = i;
          if (i2 <= paramInt2)
          {
            if (!paramBoolean3) {
              break label592;
            }
            if (i != 0) {
              i = 0;
            }
            for (;;)
            {
              if (getResources(localPermissionInfo) == null) {
                break label575;
              }
              localPrintWriter.print(loadText(localPermissionInfo, localPermissionInfo.labelRes, localPermissionInfo.nonLocalizedLabel));
              m = i;
              break;
              localPrintWriter.print(", ");
            }
            label575:
            localPrintWriter.print(localPermissionInfo.name);
            m = i;
          }
        }
      }
      label592:
      StringBuilder localStringBuilder = new StringBuilder().append(str1);
      if (paramBoolean2) {}
      for (Object localObject1 = "+ ";; localObject1 = "")
      {
        localPrintWriter.println((String)localObject1 + "permission:" + localPermissionInfo.name);
        m = i;
        if (!paramBoolean2) {
          break;
        }
        localPrintWriter.println(str1 + "  package:" + localPermissionInfo.packageName);
        if (getResources(localPermissionInfo) != null)
        {
          localPrintWriter.println(str1 + "  label:" + loadText(localPermissionInfo, localPermissionInfo.labelRes, localPermissionInfo.nonLocalizedLabel));
          localPrintWriter.println(str1 + "  description:" + loadText(localPermissionInfo, localPermissionInfo.descriptionRes, localPermissionInfo.nonLocalizedDescription));
        }
        localPrintWriter.println(str1 + "  protectionLevel:" + PermissionInfo.protectionToString(localPermissionInfo.protectionLevel));
        m = i;
        break;
      }
      label833:
      if (paramBoolean3) {
        localPrintWriter.println("");
      }
      j += 1;
    }
  }
  
  /* Error */
  private int doRemoveSplit(int paramInt, String paramString, boolean paramBoolean)
    throws RemoteException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 95	com/android/server/pm/PackageManagerShellCommand:getOutPrintWriter	()Ljava/io/PrintWriter;
    //   4: astore 7
    //   6: aconst_null
    //   7: astore 4
    //   9: aconst_null
    //   10: astore 6
    //   12: new 97	android/content/pm/PackageInstaller$Session
    //   15: dup
    //   16: aload_0
    //   17: getfield 42	com/android/server/pm/PackageManagerShellCommand:mInterface	Landroid/content/pm/IPackageManager;
    //   20: invokeinterface 103 1 0
    //   25: iload_1
    //   26: invokeinterface 109 2 0
    //   31: invokespecial 112	android/content/pm/PackageInstaller$Session:<init>	(Landroid/content/pm/IPackageInstallerSession;)V
    //   34: astore 5
    //   36: aload 5
    //   38: aload_2
    //   39: invokevirtual 292	android/content/pm/PackageInstaller$Session:removeSplit	(Ljava/lang/String;)V
    //   42: iload_3
    //   43: ifeq +10 -> 53
    //   46: aload 7
    //   48: ldc 117
    //   50: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   53: aload 5
    //   55: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   58: iconst_0
    //   59: ireturn
    //   60: astore 5
    //   62: aload 6
    //   64: astore_2
    //   65: aload_2
    //   66: astore 4
    //   68: aload 7
    //   70: new 74	java/lang/StringBuilder
    //   73: dup
    //   74: invokespecial 75	java/lang/StringBuilder:<init>	()V
    //   77: ldc_w 294
    //   80: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: aload 5
    //   85: invokevirtual 297	java/io/IOException:getMessage	()Ljava/lang/String;
    //   88: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: invokevirtual 87	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   94: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   97: aload_2
    //   98: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   101: iconst_1
    //   102: ireturn
    //   103: astore_2
    //   104: aload 4
    //   106: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   109: aload_2
    //   110: athrow
    //   111: astore_2
    //   112: aload 5
    //   114: astore 4
    //   116: goto -12 -> 104
    //   119: astore 4
    //   121: aload 5
    //   123: astore_2
    //   124: aload 4
    //   126: astore 5
    //   128: goto -63 -> 65
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	131	0	this	PackageManagerShellCommand
    //   0	131	1	paramInt	int
    //   0	131	2	paramString	String
    //   0	131	3	paramBoolean	boolean
    //   7	108	4	localObject1	Object
    //   119	6	4	localIOException1	java.io.IOException
    //   34	20	5	localSession	PackageInstaller.Session
    //   60	62	5	localIOException2	java.io.IOException
    //   126	1	5	localIOException3	java.io.IOException
    //   10	53	6	localObject2	Object
    //   4	65	7	localPrintWriter	PrintWriter
    // Exception table:
    //   from	to	target	type
    //   12	36	60	java/io/IOException
    //   12	36	103	finally
    //   68	97	103	finally
    //   36	42	111	finally
    //   46	53	111	finally
    //   36	42	119	java/io/IOException
    //   46	53	119	java/io/IOException
  }
  
  /* Error */
  private int doWriteSplit(int paramInt, String paramString1, long paramLong, String paramString2, boolean paramBoolean)
    throws RemoteException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 95	com/android/server/pm/PackageManagerShellCommand:getOutPrintWriter	()Ljava/io/PrintWriter;
    //   4: astore 17
    //   6: lload_3
    //   7: lconst_0
    //   8: lcmp
    //   9: ifgt +13 -> 22
    //   12: aload 17
    //   14: ldc_w 301
    //   17: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   20: iconst_1
    //   21: ireturn
    //   22: aload_2
    //   23: ifnull +12 -> 35
    //   26: ldc 60
    //   28: aload_2
    //   29: invokevirtual 66	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   32: ifeq +281 -> 313
    //   35: aload_0
    //   36: getfield 42	com/android/server/pm/PackageManagerShellCommand:mInterface	Landroid/content/pm/IPackageManager;
    //   39: invokeinterface 103 1 0
    //   44: iload_1
    //   45: invokeinterface 305 2 0
    //   50: astore 18
    //   52: aconst_null
    //   53: astore 11
    //   55: aconst_null
    //   56: astore 12
    //   58: aconst_null
    //   59: astore 14
    //   61: aconst_null
    //   62: astore 13
    //   64: aconst_null
    //   65: astore 9
    //   67: aconst_null
    //   68: astore 10
    //   70: aconst_null
    //   71: astore 15
    //   73: aconst_null
    //   74: astore 16
    //   76: new 97	android/content/pm/PackageInstaller$Session
    //   79: dup
    //   80: aload_0
    //   81: getfield 42	com/android/server/pm/PackageManagerShellCommand:mInterface	Landroid/content/pm/IPackageManager;
    //   84: invokeinterface 103 1 0
    //   89: iload_1
    //   90: invokeinterface 109 2 0
    //   95: invokespecial 112	android/content/pm/PackageInstaller$Session:<init>	(Landroid/content/pm/IPackageInstallerSession;)V
    //   98: astore_2
    //   99: aload_2
    //   100: astore 11
    //   102: aload 12
    //   104: astore 9
    //   106: new 307	com/android/internal/util/SizedInputStream
    //   109: dup
    //   110: aload_0
    //   111: invokevirtual 311	com/android/server/pm/PackageManagerShellCommand:getRawInputStream	()Ljava/io/InputStream;
    //   114: lload_3
    //   115: invokespecial 314	com/android/internal/util/SizedInputStream:<init>	(Ljava/io/InputStream;J)V
    //   118: astore 12
    //   120: aload 16
    //   122: astore 10
    //   124: aload 15
    //   126: astore 9
    //   128: aload_2
    //   129: aload 5
    //   131: lconst_0
    //   132: lload_3
    //   133: invokevirtual 318	android/content/pm/PackageInstaller$Session:openWrite	(Ljava/lang/String;JJ)Ljava/io/OutputStream;
    //   136: astore 5
    //   138: iconst_0
    //   139: istore_1
    //   140: aload 5
    //   142: astore 10
    //   144: aload 5
    //   146: astore 9
    //   148: ldc_w 319
    //   151: newarray <illegal type>
    //   153: astore 11
    //   155: aload 5
    //   157: astore 10
    //   159: aload 5
    //   161: astore 9
    //   163: aload 12
    //   165: aload 11
    //   167: invokevirtual 325	java/io/InputStream:read	([B)I
    //   170: istore 8
    //   172: iload 8
    //   174: iconst_m1
    //   175: if_icmpeq +148 -> 323
    //   178: iload_1
    //   179: iload 8
    //   181: iadd
    //   182: istore 7
    //   184: aload 5
    //   186: astore 10
    //   188: aload 5
    //   190: astore 9
    //   192: aload 5
    //   194: aload 11
    //   196: iconst_0
    //   197: iload 8
    //   199: invokevirtual 331	java/io/OutputStream:write	([BII)V
    //   202: iload 7
    //   204: istore_1
    //   205: aload 5
    //   207: astore 10
    //   209: aload 5
    //   211: astore 9
    //   213: aload 18
    //   215: getfield 337	android/content/pm/PackageInstaller$SessionInfo:sizeBytes	J
    //   218: lconst_0
    //   219: lcmp
    //   220: ifle -65 -> 155
    //   223: aload 5
    //   225: astore 10
    //   227: aload 5
    //   229: astore 9
    //   231: aload_2
    //   232: iload 8
    //   234: i2f
    //   235: aload 18
    //   237: getfield 337	android/content/pm/PackageInstaller$SessionInfo:sizeBytes	J
    //   240: l2f
    //   241: fdiv
    //   242: invokevirtual 341	android/content/pm/PackageInstaller$Session:addProgress	(F)V
    //   245: iload 7
    //   247: istore_1
    //   248: goto -93 -> 155
    //   251: astore 5
    //   253: aload 10
    //   255: astore 13
    //   257: aload_2
    //   258: astore 11
    //   260: aload 12
    //   262: astore 9
    //   264: aload 13
    //   266: astore 10
    //   268: aload 17
    //   270: new 74	java/lang/StringBuilder
    //   273: dup
    //   274: invokespecial 75	java/lang/StringBuilder:<init>	()V
    //   277: ldc_w 343
    //   280: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   283: aload 5
    //   285: invokevirtual 297	java/io/IOException:getMessage	()Ljava/lang/String;
    //   288: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   291: invokevirtual 87	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   294: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   297: aload 13
    //   299: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   302: aload 12
    //   304: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   307: aload_2
    //   308: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   311: iconst_1
    //   312: ireturn
    //   313: aload 17
    //   315: ldc_w 345
    //   318: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   321: iconst_1
    //   322: ireturn
    //   323: aload 5
    //   325: astore 10
    //   327: aload 5
    //   329: astore 9
    //   331: aload_2
    //   332: aload 5
    //   334: invokevirtual 349	android/content/pm/PackageInstaller$Session:fsync	(Ljava/io/OutputStream;)V
    //   337: iload 6
    //   339: ifeq +42 -> 381
    //   342: aload 5
    //   344: astore 10
    //   346: aload 5
    //   348: astore 9
    //   350: aload 17
    //   352: new 74	java/lang/StringBuilder
    //   355: dup
    //   356: invokespecial 75	java/lang/StringBuilder:<init>	()V
    //   359: ldc_w 351
    //   362: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   365: iload_1
    //   366: invokevirtual 354	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   369: ldc_w 356
    //   372: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   375: invokevirtual 87	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   378: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   381: aload 5
    //   383: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   386: aload 12
    //   388: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   391: aload_2
    //   392: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   395: iconst_0
    //   396: ireturn
    //   397: astore 5
    //   399: aconst_null
    //   400: astore_2
    //   401: aload 9
    //   403: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   406: aload 11
    //   408: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   411: aload_2
    //   412: invokestatic 128	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   415: aload 5
    //   417: athrow
    //   418: astore 5
    //   420: aload 11
    //   422: astore_2
    //   423: aload 9
    //   425: astore 11
    //   427: aload 10
    //   429: astore 9
    //   431: goto -30 -> 401
    //   434: astore 5
    //   436: aload 12
    //   438: astore 11
    //   440: goto -39 -> 401
    //   443: astore 5
    //   445: aconst_null
    //   446: astore_2
    //   447: aload 14
    //   449: astore 12
    //   451: goto -194 -> 257
    //   454: astore 5
    //   456: aload 14
    //   458: astore 12
    //   460: goto -203 -> 257
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	463	0	this	PackageManagerShellCommand
    //   0	463	1	paramInt	int
    //   0	463	2	paramString1	String
    //   0	463	3	paramLong	long
    //   0	463	5	paramString2	String
    //   0	463	6	paramBoolean	boolean
    //   182	64	7	i	int
    //   170	63	8	j	int
    //   65	365	9	localObject1	Object
    //   68	360	10	localObject2	Object
    //   53	386	11	localObject3	Object
    //   56	403	12	localObject4	Object
    //   62	236	13	localObject5	Object
    //   59	398	14	localObject6	Object
    //   71	54	15	localObject7	Object
    //   74	47	16	localObject8	Object
    //   4	347	17	localPrintWriter	PrintWriter
    //   50	186	18	localSessionInfo	android.content.pm.PackageInstaller.SessionInfo
    // Exception table:
    //   from	to	target	type
    //   128	138	251	java/io/IOException
    //   148	155	251	java/io/IOException
    //   163	172	251	java/io/IOException
    //   192	202	251	java/io/IOException
    //   213	223	251	java/io/IOException
    //   231	245	251	java/io/IOException
    //   331	337	251	java/io/IOException
    //   350	381	251	java/io/IOException
    //   76	99	397	finally
    //   106	120	418	finally
    //   268	297	418	finally
    //   128	138	434	finally
    //   148	155	434	finally
    //   163	172	434	finally
    //   192	202	434	finally
    //   213	223	434	finally
    //   231	245	434	finally
    //   331	337	434	finally
    //   350	381	434	finally
    //   76	99	443	java/io/IOException
    //   106	120	454	java/io/IOException
  }
  
  private Resources getResources(PackageItemInfo paramPackageItemInfo)
    throws RemoteException
  {
    Object localObject = (Resources)this.mResourceCache.get(paramPackageItemInfo.packageName);
    if (localObject != null) {
      return (Resources)localObject;
    }
    localObject = this.mInterface.getApplicationInfo(paramPackageItemInfo.packageName, 0, 0);
    AssetManager localAssetManager = new AssetManager();
    localAssetManager.addAssetPath(((ApplicationInfo)localObject).publicSourceDir);
    localObject = new Resources(localAssetManager, null, null);
    this.mResourceCache.put(paramPackageItemInfo.packageName, localObject);
    return (Resources)localObject;
  }
  
  private String loadText(PackageItemInfo paramPackageItemInfo, int paramInt, CharSequence paramCharSequence)
    throws RemoteException
  {
    if (paramCharSequence != null) {
      return paramCharSequence.toString();
    }
    if (paramInt != 0)
    {
      paramPackageItemInfo = getResources(paramPackageItemInfo);
      if (paramPackageItemInfo != null) {
        try
        {
          paramPackageItemInfo = paramPackageItemInfo.getString(paramInt);
          return paramPackageItemInfo;
        }
        catch (Resources.NotFoundException paramPackageItemInfo) {}
      }
    }
    return null;
  }
  
  private InstallParams makeInstallParams()
  {
    PackageInstaller.SessionParams localSessionParams = new PackageInstaller.SessionParams(1);
    InstallParams localInstallParams = new InstallParams(null);
    localInstallParams.sessionParams = localSessionParams;
    String str;
    for (;;)
    {
      str = getNextOption();
      if (str == null) {
        return localInstallParams;
      }
      if (str.equals("-l"))
      {
        localSessionParams.installFlags |= 0x1;
      }
      else if (str.equals("-r"))
      {
        localSessionParams.installFlags |= 0x2;
      }
      else if (str.equals("-i"))
      {
        localInstallParams.installerPackageName = getNextArg();
        if (localInstallParams.installerPackageName == null) {
          throw new IllegalArgumentException("Missing installer package");
        }
      }
      else if (str.equals("-t"))
      {
        localSessionParams.installFlags |= 0x4;
      }
      else if (str.equals("-s"))
      {
        localSessionParams.installFlags |= 0x8;
      }
      else if (str.equals("-f"))
      {
        localSessionParams.installFlags |= 0x10;
      }
      else if (str.equals("-d"))
      {
        localSessionParams.installFlags |= 0x80;
      }
      else if (str.equals("-g"))
      {
        localSessionParams.installFlags |= 0x100;
      }
      else if (str.equals("--dont-kill"))
      {
        localSessionParams.installFlags |= 0x1000;
      }
      else if (str.equals("--originating-uri"))
      {
        localSessionParams.originatingUri = Uri.parse(getNextArg());
      }
      else if (str.equals("--referrer"))
      {
        localSessionParams.referrerUri = Uri.parse(getNextArg());
      }
      else if (str.equals("-p"))
      {
        localSessionParams.mode = 2;
        localSessionParams.appPackageName = getNextArg();
        if (localSessionParams.appPackageName == null) {
          throw new IllegalArgumentException("Missing inherit package name");
        }
      }
      else if (str.equals("-S"))
      {
        localSessionParams.setSize(Long.parseLong(getNextArg()));
      }
      else if (str.equals("--abi"))
      {
        localSessionParams.abiOverride = checkAbiArgument(getNextArg());
      }
      else if (str.equals("--ephemeral"))
      {
        localSessionParams.installFlags |= 0x800;
      }
      else if (str.equals("--user"))
      {
        localInstallParams.userId = UserHandle.parseUserArg(getNextArgRequired());
      }
      else if (str.equals("--install-location"))
      {
        localSessionParams.installLocation = Integer.parseInt(getNextArg());
      }
      else if (str.equals("--force-uuid"))
      {
        localSessionParams.installFlags |= 0x200;
        localSessionParams.volumeUuid = getNextArg();
        if ("internal".equals(localSessionParams.volumeUuid)) {
          localSessionParams.volumeUuid = null;
        }
      }
      else
      {
        if (!str.equals("--force-sdk")) {
          break;
        }
        localSessionParams.installFlags |= 0x2000;
      }
    }
    throw new IllegalArgumentException("Unknown option " + str);
    return localInstallParams;
  }
  
  private Intent parseIntentAndUser()
    throws URISyntaxException
  {
    this.mTargetUser = -2;
    this.mBrief = false;
    this.mComponents = false;
    Intent localIntent = Intent.parseCommandArgs(this, new Intent.CommandOptionHandler()
    {
      public boolean handleOption(String paramAnonymousString, ShellCommand paramAnonymousShellCommand)
      {
        if ("--user".equals(paramAnonymousString))
        {
          PackageManagerShellCommand.this.mTargetUser = UserHandle.parseUserArg(paramAnonymousShellCommand.getNextArgRequired());
          return true;
        }
        if ("--brief".equals(paramAnonymousString))
        {
          PackageManagerShellCommand.this.mBrief = true;
          return true;
        }
        if ("--components".equals(paramAnonymousString))
        {
          PackageManagerShellCommand.this.mComponents = true;
          return true;
        }
        return false;
      }
    });
    this.mTargetUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), this.mTargetUser, false, false, null, null);
    return localIntent;
  }
  
  private void printResolveInfo(PrintWriterPrinter paramPrintWriterPrinter, String paramString, ResolveInfo paramResolveInfo, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) || (paramBoolean2))
    {
      ComponentName localComponentName;
      if (paramResolveInfo.activityInfo != null) {
        localComponentName = new ComponentName(paramResolveInfo.activityInfo.packageName, paramResolveInfo.activityInfo.name);
      }
      while (localComponentName != null)
      {
        if (!paramBoolean2) {
          paramPrintWriterPrinter.println(paramString + "priority=" + paramResolveInfo.priority + " preferredOrder=" + paramResolveInfo.preferredOrder + " match=0x" + Integer.toHexString(paramResolveInfo.match) + " specificIndex=" + paramResolveInfo.specificIndex + " isDefault=" + paramResolveInfo.isDefault);
        }
        paramPrintWriterPrinter.println(paramString + localComponentName.flattenToShortString());
        return;
        if (paramResolveInfo.serviceInfo != null) {
          localComponentName = new ComponentName(paramResolveInfo.serviceInfo.packageName, paramResolveInfo.serviceInfo.name);
        } else if (paramResolveInfo.providerInfo != null) {
          localComponentName = new ComponentName(paramResolveInfo.providerInfo.packageName, paramResolveInfo.providerInfo.name);
        } else {
          localComponentName = null;
        }
      }
    }
    paramResolveInfo.dump(paramPrintWriterPrinter, paramString);
  }
  
  private int runCompile()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    boolean bool2 = SystemProperties.getBoolean("dalvik.vm.usejitprofiles", false);
    boolean bool1 = false;
    int k = 0;
    int i = 0;
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    String str;
    for (;;)
    {
      str = getNextOption();
      if (str == null) {
        break label203;
      }
      if (str.equals("-a"))
      {
        k = 1;
      }
      else if (str.equals("-c"))
      {
        i = 1;
      }
      else if (str.equals("-f"))
      {
        bool1 = true;
      }
      else if (str.equals("-m"))
      {
        localObject1 = getNextArgRequired();
      }
      else if (str.equals("-r"))
      {
        localObject2 = getNextArgRequired();
      }
      else if (str.equals("--check-prof"))
      {
        localObject3 = getNextArgRequired();
      }
      else
      {
        if (!str.equals("--reset")) {
          break;
        }
        bool1 = true;
        i = 1;
        localObject2 = "install";
      }
    }
    localPrintWriter.println("Error: Unknown option: " + str);
    return 1;
    label203:
    if (localObject3 != null)
    {
      if (!"true".equals(localObject3)) {
        break label242;
      }
      bool2 = true;
    }
    while ((localObject1 != null) && (localObject2 != null))
    {
      localPrintWriter.println("Cannot use compilation filter (\"-m\") and compilation reason (\"-r\") at the same time");
      return 1;
      label242:
      if ("false".equals(localObject3))
      {
        bool2 = false;
      }
      else
      {
        localPrintWriter.println("Invalid value for \"--check-prof\". Expected \"true\" or \"false\".");
        return 1;
      }
    }
    if ((localObject1 == null) && (localObject2 == null))
    {
      localPrintWriter.println("Cannot run without any of compilation filter (\"-m\") and compilation reason (\"-r\") at the same time");
      return 1;
    }
    if (localObject1 != null)
    {
      if (!DexFile.isValidCompilerFilter((String)localObject1))
      {
        localPrintWriter.println("Error: \"" + (String)localObject1 + "\" is not a valid compilation filter.");
        return 1;
      }
      if (k == 0) {
        break label523;
      }
    }
    for (localObject2 = this.mInterface.getAllPackages();; localObject2 = Collections.singletonList(localObject2))
    {
      localObject3 = new ArrayList();
      localObject2 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        str = (String)((Iterator)localObject2).next();
        if (i != 0) {
          this.mInterface.clearApplicationProfileData(str);
        }
        if (!this.mInterface.performDexOptMode(str, bool2, (String)localObject1, bool1)) {
          ((List)localObject3).add(str);
        }
      }
      int n = -1;
      int j = 0;
      int m;
      for (;;)
      {
        m = n;
        if (j < PackageManagerServiceCompilerMapping.REASON_STRINGS.length)
        {
          if (PackageManagerServiceCompilerMapping.REASON_STRINGS[j].equals(localObject2)) {
            m = j;
          }
        }
        else
        {
          if (m != -1) {
            break;
          }
          localPrintWriter.println("Error: Unknown compilation reason: " + (String)localObject2);
          return 1;
        }
        j += 1;
      }
      localObject1 = PackageManagerServiceCompilerMapping.getCompilerFilterForReason(m);
      break;
      label523:
      localObject2 = getNextArg();
      if (localObject2 == null)
      {
        localPrintWriter.println("Error: package name not specified");
        return 1;
      }
    }
    if (((List)localObject3).isEmpty())
    {
      localPrintWriter.println("Success");
      return 0;
    }
    if (((List)localObject3).size() == 1)
    {
      localPrintWriter.println("Failure: package " + (String)((List)localObject3).get(0) + " could not be compiled");
      return 1;
    }
    localPrintWriter.print("Failure: the following packages could not be compiled: ");
    i = 1;
    localObject1 = ((Iterable)localObject3).iterator();
    if (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      if (i != 0) {
        i = 0;
      }
      for (;;)
      {
        localPrintWriter.print((String)localObject2);
        break;
        localPrintWriter.print(", ");
      }
    }
    localPrintWriter.println();
    return 1;
  }
  
  private int runDumpProfiles()
    throws RemoteException
  {
    String str = getNextArg();
    this.mInterface.dumpProfiles(str);
    return 0;
  }
  
  /* Error */
  private int runInstall()
    throws RemoteException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 95	com/android/server/pm/PackageManagerShellCommand:getOutPrintWriter	()Ljava/io/PrintWriter;
    //   4: astore 5
    //   6: aload_0
    //   7: invokespecial 734	com/android/server/pm/PackageManagerShellCommand:makeInstallParams	()Lcom/android/server/pm/PackageManagerShellCommand$InstallParams;
    //   10: astore 6
    //   12: aload_0
    //   13: invokevirtual 419	com/android/server/pm/PackageManagerShellCommand:getNextArg	()Ljava/lang/String;
    //   16: astore 7
    //   18: aload 6
    //   20: getfield 407	com/android/server/pm/PackageManagerShellCommand$InstallParams:sessionParams	Landroid/content/pm/PackageInstaller$SessionParams;
    //   23: getfield 175	android/content/pm/PackageInstaller$SessionParams:installFlags	I
    //   26: bipush 8
    //   28: iand
    //   29: ifeq +152 -> 181
    //   32: iconst_1
    //   33: istore_1
    //   34: aload 6
    //   36: getfield 407	com/android/server/pm/PackageManagerShellCommand$InstallParams:sessionParams	Landroid/content/pm/PackageInstaller$SessionParams;
    //   39: getfield 735	android/content/pm/PackageInstaller$SessionParams:sizeBytes	J
    //   42: lconst_0
    //   43: lcmp
    //   44: ifge +72 -> 116
    //   47: aload 7
    //   49: ifnull +67 -> 116
    //   52: new 737	java/io/File
    //   55: dup
    //   56: aload 7
    //   58: invokespecial 738	java/io/File:<init>	(Ljava/lang/String;)V
    //   61: astore 8
    //   63: aload 8
    //   65: invokevirtual 741	java/io/File:isFile	()Z
    //   68: ifeq +48 -> 116
    //   71: iload_1
    //   72: ifeq +144 -> 216
    //   75: new 743	android/content/pm/PackageParser$PackageLite
    //   78: dup
    //   79: aconst_null
    //   80: aload 8
    //   82: iconst_0
    //   83: invokestatic 749	android/content/pm/PackageParser:parseApkLite	(Ljava/io/File;I)Landroid/content/pm/PackageParser$ApkLite;
    //   86: aconst_null
    //   87: aconst_null
    //   88: aconst_null
    //   89: invokespecial 752	android/content/pm/PackageParser$PackageLite:<init>	(Ljava/lang/String;Landroid/content/pm/PackageParser$ApkLite;[Ljava/lang/String;[Ljava/lang/String;[I)V
    //   92: astore 8
    //   94: aload 6
    //   96: getfield 407	com/android/server/pm/PackageManagerShellCommand$InstallParams:sessionParams	Landroid/content/pm/PackageInstaller$SessionParams;
    //   99: aload 8
    //   101: iconst_0
    //   102: aload 6
    //   104: getfield 407	com/android/server/pm/PackageManagerShellCommand$InstallParams:sessionParams	Landroid/content/pm/PackageInstaller$SessionParams;
    //   107: getfield 482	android/content/pm/PackageInstaller$SessionParams:abiOverride	Ljava/lang/String;
    //   110: invokestatic 758	com/android/internal/content/PackageHelper:calculateInstalledSize	(Landroid/content/pm/PackageParser$PackageLite;ZLjava/lang/String;)J
    //   113: invokevirtual 475	android/content/pm/PackageInstaller$SessionParams:setSize	(J)V
    //   116: aload_0
    //   117: aload 6
    //   119: getfield 407	com/android/server/pm/PackageManagerShellCommand$InstallParams:sessionParams	Landroid/content/pm/PackageInstaller$SessionParams;
    //   122: aload 6
    //   124: getfield 422	com/android/server/pm/PackageManagerShellCommand$InstallParams:installerPackageName	Ljava/lang/String;
    //   127: aload 6
    //   129: getfield 497	com/android/server/pm/PackageManagerShellCommand$InstallParams:userId	I
    //   132: invokespecial 760	com/android/server/pm/PackageManagerShellCommand:doCreateSession	(Landroid/content/pm/PackageInstaller$SessionParams;Ljava/lang/String;I)I
    //   135: istore_3
    //   136: iconst_1
    //   137: istore_2
    //   138: aload 7
    //   140: ifnonnull +96 -> 236
    //   143: iload_2
    //   144: istore_1
    //   145: aload 6
    //   147: getfield 407	com/android/server/pm/PackageManagerShellCommand$InstallParams:sessionParams	Landroid/content/pm/PackageInstaller$SessionParams;
    //   150: getfield 735	android/content/pm/PackageInstaller$SessionParams:sizeBytes	J
    //   153: lconst_0
    //   154: lcmp
    //   155: ifne +81 -> 236
    //   158: iload_2
    //   159: istore_1
    //   160: aload 5
    //   162: ldc_w 762
    //   165: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   168: iconst_1
    //   169: ifeq +10 -> 179
    //   172: aload_0
    //   173: iload_3
    //   174: iconst_0
    //   175: invokespecial 764	com/android/server/pm/PackageManagerShellCommand:doAbandonSession	(IZ)I
    //   178: pop
    //   179: iconst_1
    //   180: ireturn
    //   181: iconst_0
    //   182: istore_1
    //   183: goto -149 -> 34
    //   186: astore 6
    //   188: aload 5
    //   190: new 74	java/lang/StringBuilder
    //   193: dup
    //   194: invokespecial 75	java/lang/StringBuilder:<init>	()V
    //   197: ldc_w 766
    //   200: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   203: aload 6
    //   205: invokevirtual 769	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   208: invokevirtual 87	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   211: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   214: iconst_1
    //   215: ireturn
    //   216: aload 6
    //   218: getfield 407	com/android/server/pm/PackageManagerShellCommand$InstallParams:sessionParams	Landroid/content/pm/PackageInstaller$SessionParams;
    //   221: aload 8
    //   223: invokevirtual 773	java/io/File:length	()J
    //   226: invokevirtual 475	android/content/pm/PackageInstaller$SessionParams:setSize	(J)V
    //   229: goto -113 -> 116
    //   232: astore 5
    //   234: iconst_1
    //   235: ireturn
    //   236: iload_2
    //   237: istore_1
    //   238: aload_0
    //   239: iload_3
    //   240: aload 7
    //   242: aload 6
    //   244: getfield 407	com/android/server/pm/PackageManagerShellCommand$InstallParams:sessionParams	Landroid/content/pm/PackageInstaller$SessionParams;
    //   247: getfield 735	android/content/pm/PackageInstaller$SessionParams:sizeBytes	J
    //   250: ldc_w 775
    //   253: iconst_0
    //   254: invokespecial 777	com/android/server/pm/PackageManagerShellCommand:doWriteSplit	(ILjava/lang/String;JLjava/lang/String;Z)I
    //   257: istore 4
    //   259: iload 4
    //   261: ifeq +20 -> 281
    //   264: iconst_1
    //   265: ifeq +10 -> 275
    //   268: aload_0
    //   269: iload_3
    //   270: iconst_0
    //   271: invokespecial 764	com/android/server/pm/PackageManagerShellCommand:doAbandonSession	(IZ)I
    //   274: pop
    //   275: iconst_1
    //   276: ireturn
    //   277: astore 5
    //   279: iconst_1
    //   280: ireturn
    //   281: iload_2
    //   282: istore_1
    //   283: aload_0
    //   284: iload_3
    //   285: iconst_0
    //   286: invokespecial 779	com/android/server/pm/PackageManagerShellCommand:doCommitSession	(IZ)I
    //   289: istore_2
    //   290: iload_2
    //   291: ifeq +20 -> 311
    //   294: iconst_1
    //   295: ifeq +10 -> 305
    //   298: aload_0
    //   299: iload_3
    //   300: iconst_0
    //   301: invokespecial 764	com/android/server/pm/PackageManagerShellCommand:doAbandonSession	(IZ)I
    //   304: pop
    //   305: iconst_1
    //   306: ireturn
    //   307: astore 5
    //   309: iconst_1
    //   310: ireturn
    //   311: iconst_0
    //   312: istore_1
    //   313: aload 5
    //   315: ldc 117
    //   317: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   320: iconst_0
    //   321: ifeq +10 -> 331
    //   324: aload_0
    //   325: iload_3
    //   326: iconst_0
    //   327: invokespecial 764	com/android/server/pm/PackageManagerShellCommand:doAbandonSession	(IZ)I
    //   330: pop
    //   331: iconst_0
    //   332: ireturn
    //   333: astore 5
    //   335: iconst_0
    //   336: ireturn
    //   337: astore 5
    //   339: iload_1
    //   340: ifeq +10 -> 350
    //   343: aload_0
    //   344: iload_3
    //   345: iconst_0
    //   346: invokespecial 764	com/android/server/pm/PackageManagerShellCommand:doAbandonSession	(IZ)I
    //   349: pop
    //   350: aload 5
    //   352: athrow
    //   353: astore 6
    //   355: goto -5 -> 350
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	358	0	this	PackageManagerShellCommand
    //   33	307	1	i	int
    //   137	154	2	j	int
    //   135	210	3	k	int
    //   257	3	4	m	int
    //   4	185	5	localPrintWriter	PrintWriter
    //   232	1	5	localException1	Exception
    //   277	1	5	localException2	Exception
    //   307	7	5	localException3	Exception
    //   333	1	5	localException4	Exception
    //   337	14	5	localObject1	Object
    //   10	136	6	localInstallParams	InstallParams
    //   186	57	6	localPackageParserException	android.content.pm.PackageParser.PackageParserException
    //   353	1	6	localException5	Exception
    //   16	225	7	str	String
    //   61	161	8	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   75	116	186	android/content/pm/PackageParser$PackageParserException
    //   75	116	186	java/io/IOException
    //   172	179	232	java/lang/Exception
    //   268	275	277	java/lang/Exception
    //   298	305	307	java/lang/Exception
    //   324	331	333	java/lang/Exception
    //   145	158	337	finally
    //   160	168	337	finally
    //   238	259	337	finally
    //   283	290	337	finally
    //   313	320	337	finally
    //   343	350	353	java/lang/Exception
  }
  
  private int runInstallAbandon()
    throws RemoteException
  {
    return doAbandonSession(Integer.parseInt(getNextArg()), true);
  }
  
  private int runInstallCommit()
    throws RemoteException
  {
    return doCommitSession(Integer.parseInt(getNextArg()), true);
  }
  
  private int runInstallCreate()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    InstallParams localInstallParams = makeInstallParams();
    int i = doCreateSession(localInstallParams.sessionParams, localInstallParams.installerPackageName, localInstallParams.userId);
    localPrintWriter.println("Success: created install session [" + i + "]");
    return 0;
  }
  
  private int runInstallRemove()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    int i = Integer.parseInt(getNextArg());
    String str = getNextArg();
    if (str == null)
    {
      localPrintWriter.println("Error: split name not specified");
      return 1;
    }
    return doRemoveSplit(i, str, true);
  }
  
  private int runInstallWrite()
    throws RemoteException
  {
    for (long l = -1L;; l = Long.parseLong(getNextArg()))
    {
      str = getNextOption();
      if (str == null) {
        break label66;
      }
      if (!str.equals("-S")) {
        break;
      }
    }
    throw new IllegalArgumentException("Unknown option: " + str);
    label66:
    int i = Integer.parseInt(getNextArg());
    String str = getNextArg();
    return doWriteSplit(i, getNextArg(), l, str, true);
  }
  
  private int runList()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    String str = getNextArg();
    if (str == null)
    {
      localPrintWriter.println("Error: didn't specify type of data to list");
      return -1;
    }
    if (str.equals("features")) {
      return runListFeatures();
    }
    if (str.equals("instrumentation")) {
      return runListInstrumentation();
    }
    if (str.equals("libraries")) {
      return runListLibraries();
    }
    if (str.equals("package")) {}
    while (str.equals("packages")) {
      return runListPackages(false);
    }
    if (str.equals("permission-groups")) {
      return runListPermissionGroups();
    }
    if (str.equals("permissions")) {
      return runListPermissions();
    }
    localPrintWriter.println("Error: unknown list type '" + str + "'");
    return -1;
  }
  
  private int runListFeatures()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    List localList = this.mInterface.getSystemAvailableFeatures().getList();
    Collections.sort(localList, new Comparator()
    {
      public int compare(FeatureInfo paramAnonymousFeatureInfo1, FeatureInfo paramAnonymousFeatureInfo2)
      {
        if (paramAnonymousFeatureInfo1.name == paramAnonymousFeatureInfo2.name) {
          return 0;
        }
        if (paramAnonymousFeatureInfo1.name == null) {
          return -1;
        }
        if (paramAnonymousFeatureInfo2.name == null) {
          return 1;
        }
        return paramAnonymousFeatureInfo1.name.compareTo(paramAnonymousFeatureInfo2.name);
      }
    });
    int i;
    int j;
    label47:
    FeatureInfo localFeatureInfo;
    if (localList != null)
    {
      i = localList.size();
      j = 0;
      if (j >= i) {
        break label163;
      }
      localFeatureInfo = (FeatureInfo)localList.get(j);
      localPrintWriter.print("feature:");
      if (localFeatureInfo.name == null) {
        break label129;
      }
      localPrintWriter.print(localFeatureInfo.name);
      if (localFeatureInfo.version > 0)
      {
        localPrintWriter.print("=");
        localPrintWriter.print(localFeatureInfo.version);
      }
      localPrintWriter.println();
    }
    for (;;)
    {
      j += 1;
      break label47;
      i = 0;
      break;
      label129:
      localPrintWriter.println("reqGlEsVersion=0x" + Integer.toHexString(localFeatureInfo.reqGlEsVersion));
    }
    label163:
    return 0;
  }
  
  private int runListInstrumentation()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    int i = 0;
    Object localObject1 = null;
    Object localObject2;
    try
    {
      for (;;)
      {
        localObject2 = getNextArg();
        if (localObject2 == null) {
          break label117;
        }
        if (((String)localObject2).equals("-f"))
        {
          i = 1;
        }
        else
        {
          if (((String)localObject2).charAt(0) == '-') {
            break;
          }
          localObject1 = localObject2;
        }
      }
      localPrintWriter.println("Error: Unknown option: " + (String)localObject2);
      return -1;
    }
    catch (RuntimeException localRuntimeException)
    {
      localPrintWriter.println("Error: " + localRuntimeException.toString());
      return -1;
    }
    label117:
    List localList = this.mInterface.queryInstrumentation(localRuntimeException, 0).getList();
    Collections.sort(localList, new Comparator()
    {
      public int compare(InstrumentationInfo paramAnonymousInstrumentationInfo1, InstrumentationInfo paramAnonymousInstrumentationInfo2)
      {
        return paramAnonymousInstrumentationInfo1.targetPackage.compareTo(paramAnonymousInstrumentationInfo2.targetPackage);
      }
    });
    if (localList != null) {}
    for (int j = localList.size();; j = 0)
    {
      int k = 0;
      while (k < j)
      {
        localObject2 = (InstrumentationInfo)localList.get(k);
        localPrintWriter.print("instrumentation:");
        if (i != 0)
        {
          localPrintWriter.print(((InstrumentationInfo)localObject2).sourceDir);
          localPrintWriter.print("=");
        }
        localPrintWriter.print(new ComponentName(((InstrumentationInfo)localObject2).packageName, ((InstrumentationInfo)localObject2).name).flattenToShortString());
        localPrintWriter.print(" (target=");
        localPrintWriter.print(((InstrumentationInfo)localObject2).targetPackage);
        localPrintWriter.println(")");
        k += 1;
      }
    }
    return 0;
  }
  
  private int runListLibraries()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    ArrayList localArrayList = new ArrayList();
    Object localObject = this.mInterface.getSystemSharedLibraryNames();
    int i = 0;
    while (i < localObject.length)
    {
      localArrayList.add(localObject[i]);
      i += 1;
    }
    Collections.sort(localArrayList, new Comparator()
    {
      public int compare(String paramAnonymousString1, String paramAnonymousString2)
      {
        if (paramAnonymousString1 == paramAnonymousString2) {
          return 0;
        }
        if (paramAnonymousString1 == null) {
          return -1;
        }
        if (paramAnonymousString2 == null) {
          return 1;
        }
        return paramAnonymousString1.compareTo(paramAnonymousString2);
      }
    });
    if (localArrayList != null) {}
    for (i = localArrayList.size();; i = 0)
    {
      int j = 0;
      while (j < i)
      {
        localObject = (String)localArrayList.get(j);
        localPrintWriter.print("library:");
        localPrintWriter.println((String)localObject);
        j += 1;
      }
    }
    return 0;
  }
  
  private int runListPackages(boolean paramBoolean)
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    int i2 = 0;
    int n = 0;
    int m = 0;
    int j = 0;
    int i = 0;
    int k = 0;
    int i1 = 0;
    try
    {
      String str1;
      for (;;)
      {
        str1 = getNextOption();
        if (str1 == null) {
          break label268;
        }
        if (str1.equals("-d")) {
          n = 1;
        } else if (str1.equals("-e")) {
          m = 1;
        } else if (str1.equals("-f")) {
          paramBoolean = true;
        } else if (str1.equals("-i")) {
          k = 1;
        } else if (!str1.equals("-l")) {
          if (str1.equals("-lf"))
          {
            paramBoolean = true;
          }
          else if (str1.equals("-s"))
          {
            j = 1;
          }
          else if (str1.equals("-u"))
          {
            i2 |= 0x2000;
          }
          else if (str1.equals("-3"))
          {
            i = 1;
          }
          else
          {
            if (!str1.equals("--user")) {
              break;
            }
            i1 = UserHandle.parseUserArg(getNextArgRequired());
          }
        }
      }
      localPrintWriter.println("Error: Unknown option: " + str1);
      return -1;
    }
    catch (RuntimeException localRuntimeException)
    {
      localPrintWriter.println("Error: " + localRuntimeException.toString());
      return -1;
    }
    label268:
    String str2 = getNextArg();
    List localList = this.mInterface.getInstalledPackages(i2, i1).getList();
    int i3 = localList.size();
    i1 = 0;
    if (i1 < i3)
    {
      PackageInfo localPackageInfo = (PackageInfo)localList.get(i1);
      if ((str2 == null) || (localPackageInfo.packageName.contains(str2)))
      {
        if ((localPackageInfo.applicationInfo.flags & 0x1) == 0) {
          break label384;
        }
        i2 = 1;
        label359:
        if ((n == 0) || (!localPackageInfo.applicationInfo.enabled)) {
          break label390;
        }
      }
      for (;;)
      {
        i1 += 1;
        break;
        label384:
        i2 = 0;
        break label359;
        label390:
        if (((m == 0) || (localPackageInfo.applicationInfo.enabled)) && ((j == 0) || (i2 != 0)) && ((i == 0) || (i2 == 0)))
        {
          localPrintWriter.print("package:");
          if (paramBoolean)
          {
            localPrintWriter.print(localPackageInfo.applicationInfo.sourceDir);
            localPrintWriter.print("=");
          }
          localPrintWriter.print(localPackageInfo.packageName);
          if (k != 0)
          {
            localPrintWriter.print("  installer=");
            localPrintWriter.print(this.mInterface.getInstallerPackageName(localPackageInfo.packageName));
          }
          localPrintWriter.println();
        }
      }
    }
    return 0;
  }
  
  private int runListPermissionGroups()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    List localList = this.mInterface.getAllPermissionGroups(0).getList();
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      PermissionGroupInfo localPermissionGroupInfo = (PermissionGroupInfo)localList.get(i);
      localPrintWriter.print("permission group:");
      localPrintWriter.println(localPermissionGroupInfo.name);
      i += 1;
    }
    return 0;
  }
  
  private int runListPermissions()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    boolean bool2 = false;
    boolean bool1 = false;
    int i = 0;
    boolean bool3 = false;
    int j = 0;
    for (;;)
    {
      localObject = getNextOption();
      if (localObject == null) {
        break label147;
      }
      if (((String)localObject).equals("-d"))
      {
        j = 1;
      }
      else if (((String)localObject).equals("-f"))
      {
        bool2 = true;
      }
      else if (((String)localObject).equals("-g"))
      {
        bool1 = true;
      }
      else if (((String)localObject).equals("-s"))
      {
        bool1 = true;
        bool2 = true;
        bool3 = true;
      }
      else
      {
        if (!((String)localObject).equals("-u")) {
          break;
        }
        i = 1;
      }
    }
    localPrintWriter.println("Error: Unknown option: " + (String)localObject);
    return 1;
    label147:
    Object localObject = new ArrayList();
    if (bool1)
    {
      List localList = this.mInterface.getAllPermissionGroups(0).getList();
      int m = localList.size();
      int k = 0;
      while (k < m)
      {
        ((ArrayList)localObject).add(((PermissionGroupInfo)localList.get(k)).name);
        k += 1;
      }
      ((ArrayList)localObject).add(null);
      if (j == 0) {
        break label308;
      }
      localPrintWriter.println("Dangerous Permissions:");
      localPrintWriter.println("");
      doListPermissions((ArrayList)localObject, bool1, bool2, bool3, 1, 1);
      if (i != 0)
      {
        localPrintWriter.println("Normal Permissions:");
        localPrintWriter.println("");
        doListPermissions((ArrayList)localObject, bool1, bool2, bool3, 0, 0);
      }
    }
    for (;;)
    {
      return 0;
      ((ArrayList)localObject).add(getNextArg());
      break;
      label308:
      if (i != 0)
      {
        localPrintWriter.println("Dangerous and Normal Permissions:");
        localPrintWriter.println("");
        doListPermissions((ArrayList)localObject, bool1, bool2, bool3, 0, 1);
      }
      else
      {
        localPrintWriter.println("All Permissions:");
        localPrintWriter.println("");
        doListPermissions((ArrayList)localObject, bool1, bool2, bool3, 55536, 10000);
      }
    }
  }
  
  /* Error */
  private int runQueryIntentActivities()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 951	com/android/server/pm/PackageManagerShellCommand:parseIntentAndUser	()Landroid/content/Intent;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield 42	com/android/server/pm/PackageManagerShellCommand:mInterface	Landroid/content/pm/IPackageManager;
    //   9: aload_2
    //   10: aconst_null
    //   11: iconst_0
    //   12: aload_0
    //   13: getfield 523	com/android/server/pm/PackageManagerShellCommand:mTargetUser	I
    //   16: invokeinterface 955 5 0
    //   21: invokevirtual 230	android/content/pm/ParceledListSlice:getList	()Ljava/util/List;
    //   24: astore_2
    //   25: aload_0
    //   26: invokevirtual 95	com/android/server/pm/PackageManagerShellCommand:getOutPrintWriter	()Ljava/io/PrintWriter;
    //   29: astore_3
    //   30: aload_2
    //   31: ifnull +12 -> 43
    //   34: aload_2
    //   35: invokeinterface 233 1 0
    //   40: ifgt +26 -> 66
    //   43: aload_3
    //   44: ldc_w 957
    //   47: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   50: iconst_0
    //   51: ireturn
    //   52: astore_2
    //   53: new 861	java/lang/RuntimeException
    //   56: dup
    //   57: aload_2
    //   58: invokevirtual 958	java/net/URISyntaxException:getMessage	()Ljava/lang/String;
    //   61: aload_2
    //   62: invokespecial 961	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   65: athrow
    //   66: aload_0
    //   67: getfield 527	com/android/server/pm/PackageManagerShellCommand:mComponents	Z
    //   70: ifne +95 -> 165
    //   73: aload_3
    //   74: aload_2
    //   75: invokeinterface 233 1 0
    //   80: invokevirtual 854	java/io/PrintWriter:print	(I)V
    //   83: aload_3
    //   84: ldc_w 963
    //   87: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   90: new 598	android/util/PrintWriterPrinter
    //   93: dup
    //   94: aload_3
    //   95: invokespecial 966	android/util/PrintWriterPrinter:<init>	(Ljava/io/PrintWriter;)V
    //   98: astore 4
    //   100: iconst_0
    //   101: istore_1
    //   102: iload_1
    //   103: aload_2
    //   104: invokeinterface 233 1 0
    //   109: if_icmpge -59 -> 50
    //   112: aload_3
    //   113: ldc_w 968
    //   116: invokevirtual 218	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   119: aload_3
    //   120: iload_1
    //   121: invokevirtual 854	java/io/PrintWriter:print	(I)V
    //   124: aload_3
    //   125: ldc_w 970
    //   128: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   131: aload_0
    //   132: aload 4
    //   134: ldc_w 972
    //   137: aload_2
    //   138: iload_1
    //   139: invokeinterface 234 2 0
    //   144: checkcast 552	android/content/pm/ResolveInfo
    //   147: aload_0
    //   148: getfield 525	com/android/server/pm/PackageManagerShellCommand:mBrief	Z
    //   151: aload_0
    //   152: getfield 527	com/android/server/pm/PackageManagerShellCommand:mComponents	Z
    //   155: invokespecial 974	com/android/server/pm/PackageManagerShellCommand:printResolveInfo	(Landroid/util/PrintWriterPrinter;Ljava/lang/String;Landroid/content/pm/ResolveInfo;ZZ)V
    //   158: iload_1
    //   159: iconst_1
    //   160: iadd
    //   161: istore_1
    //   162: goto -60 -> 102
    //   165: new 598	android/util/PrintWriterPrinter
    //   168: dup
    //   169: aload_3
    //   170: invokespecial 966	android/util/PrintWriterPrinter:<init>	(Ljava/io/PrintWriter;)V
    //   173: astore_3
    //   174: iconst_0
    //   175: istore_1
    //   176: iload_1
    //   177: aload_2
    //   178: invokeinterface 233 1 0
    //   183: if_icmpge -133 -> 50
    //   186: aload_0
    //   187: aload_3
    //   188: ldc -64
    //   190: aload_2
    //   191: iload_1
    //   192: invokeinterface 234 2 0
    //   197: checkcast 552	android/content/pm/ResolveInfo
    //   200: aload_0
    //   201: getfield 525	com/android/server/pm/PackageManagerShellCommand:mBrief	Z
    //   204: aload_0
    //   205: getfield 527	com/android/server/pm/PackageManagerShellCommand:mComponents	Z
    //   208: invokespecial 974	com/android/server/pm/PackageManagerShellCommand:printResolveInfo	(Landroid/util/PrintWriterPrinter;Ljava/lang/String;Landroid/content/pm/ResolveInfo;ZZ)V
    //   211: iload_1
    //   212: iconst_1
    //   213: iadd
    //   214: istore_1
    //   215: goto -39 -> 176
    //   218: astore_2
    //   219: new 861	java/lang/RuntimeException
    //   222: dup
    //   223: ldc_w 976
    //   226: aload_2
    //   227: invokespecial 961	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   230: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	231	0	this	PackageManagerShellCommand
    //   101	114	1	i	int
    //   4	31	2	localObject1	Object
    //   52	139	2	localURISyntaxException	URISyntaxException
    //   218	9	2	localRemoteException	RemoteException
    //   29	159	3	localObject2	Object
    //   98	35	4	localPrintWriterPrinter	PrintWriterPrinter
    // Exception table:
    //   from	to	target	type
    //   0	5	52	java/net/URISyntaxException
    //   5	30	218	android/os/RemoteException
    //   34	43	218	android/os/RemoteException
    //   43	50	218	android/os/RemoteException
    //   66	100	218	android/os/RemoteException
    //   102	158	218	android/os/RemoteException
    //   165	174	218	android/os/RemoteException
    //   176	211	218	android/os/RemoteException
  }
  
  /* Error */
  private int runQueryIntentReceivers()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 951	com/android/server/pm/PackageManagerShellCommand:parseIntentAndUser	()Landroid/content/Intent;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield 42	com/android/server/pm/PackageManagerShellCommand:mInterface	Landroid/content/pm/IPackageManager;
    //   9: aload_2
    //   10: aconst_null
    //   11: iconst_0
    //   12: aload_0
    //   13: getfield 523	com/android/server/pm/PackageManagerShellCommand:mTargetUser	I
    //   16: invokeinterface 980 5 0
    //   21: invokevirtual 230	android/content/pm/ParceledListSlice:getList	()Ljava/util/List;
    //   24: astore_2
    //   25: aload_0
    //   26: invokevirtual 95	com/android/server/pm/PackageManagerShellCommand:getOutPrintWriter	()Ljava/io/PrintWriter;
    //   29: astore_3
    //   30: aload_2
    //   31: ifnull +12 -> 43
    //   34: aload_2
    //   35: invokeinterface 233 1 0
    //   40: ifgt +26 -> 66
    //   43: aload_3
    //   44: ldc_w 982
    //   47: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   50: iconst_0
    //   51: ireturn
    //   52: astore_2
    //   53: new 861	java/lang/RuntimeException
    //   56: dup
    //   57: aload_2
    //   58: invokevirtual 958	java/net/URISyntaxException:getMessage	()Ljava/lang/String;
    //   61: aload_2
    //   62: invokespecial 961	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   65: athrow
    //   66: aload_0
    //   67: getfield 527	com/android/server/pm/PackageManagerShellCommand:mComponents	Z
    //   70: ifne +95 -> 165
    //   73: aload_3
    //   74: aload_2
    //   75: invokeinterface 233 1 0
    //   80: invokevirtual 854	java/io/PrintWriter:print	(I)V
    //   83: aload_3
    //   84: ldc_w 984
    //   87: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   90: new 598	android/util/PrintWriterPrinter
    //   93: dup
    //   94: aload_3
    //   95: invokespecial 966	android/util/PrintWriterPrinter:<init>	(Ljava/io/PrintWriter;)V
    //   98: astore 4
    //   100: iconst_0
    //   101: istore_1
    //   102: iload_1
    //   103: aload_2
    //   104: invokeinterface 233 1 0
    //   109: if_icmpge -59 -> 50
    //   112: aload_3
    //   113: ldc_w 986
    //   116: invokevirtual 218	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   119: aload_3
    //   120: iload_1
    //   121: invokevirtual 854	java/io/PrintWriter:print	(I)V
    //   124: aload_3
    //   125: ldc_w 970
    //   128: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   131: aload_0
    //   132: aload 4
    //   134: ldc_w 972
    //   137: aload_2
    //   138: iload_1
    //   139: invokeinterface 234 2 0
    //   144: checkcast 552	android/content/pm/ResolveInfo
    //   147: aload_0
    //   148: getfield 525	com/android/server/pm/PackageManagerShellCommand:mBrief	Z
    //   151: aload_0
    //   152: getfield 527	com/android/server/pm/PackageManagerShellCommand:mComponents	Z
    //   155: invokespecial 974	com/android/server/pm/PackageManagerShellCommand:printResolveInfo	(Landroid/util/PrintWriterPrinter;Ljava/lang/String;Landroid/content/pm/ResolveInfo;ZZ)V
    //   158: iload_1
    //   159: iconst_1
    //   160: iadd
    //   161: istore_1
    //   162: goto -60 -> 102
    //   165: new 598	android/util/PrintWriterPrinter
    //   168: dup
    //   169: aload_3
    //   170: invokespecial 966	android/util/PrintWriterPrinter:<init>	(Ljava/io/PrintWriter;)V
    //   173: astore_3
    //   174: iconst_0
    //   175: istore_1
    //   176: iload_1
    //   177: aload_2
    //   178: invokeinterface 233 1 0
    //   183: if_icmpge -133 -> 50
    //   186: aload_0
    //   187: aload_3
    //   188: ldc -64
    //   190: aload_2
    //   191: iload_1
    //   192: invokeinterface 234 2 0
    //   197: checkcast 552	android/content/pm/ResolveInfo
    //   200: aload_0
    //   201: getfield 525	com/android/server/pm/PackageManagerShellCommand:mBrief	Z
    //   204: aload_0
    //   205: getfield 527	com/android/server/pm/PackageManagerShellCommand:mComponents	Z
    //   208: invokespecial 974	com/android/server/pm/PackageManagerShellCommand:printResolveInfo	(Landroid/util/PrintWriterPrinter;Ljava/lang/String;Landroid/content/pm/ResolveInfo;ZZ)V
    //   211: iload_1
    //   212: iconst_1
    //   213: iadd
    //   214: istore_1
    //   215: goto -39 -> 176
    //   218: astore_2
    //   219: new 861	java/lang/RuntimeException
    //   222: dup
    //   223: ldc_w 976
    //   226: aload_2
    //   227: invokespecial 961	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   230: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	231	0	this	PackageManagerShellCommand
    //   101	114	1	i	int
    //   4	31	2	localObject1	Object
    //   52	139	2	localURISyntaxException	URISyntaxException
    //   218	9	2	localRemoteException	RemoteException
    //   29	159	3	localObject2	Object
    //   98	35	4	localPrintWriterPrinter	PrintWriterPrinter
    // Exception table:
    //   from	to	target	type
    //   0	5	52	java/net/URISyntaxException
    //   5	30	218	android/os/RemoteException
    //   34	43	218	android/os/RemoteException
    //   43	50	218	android/os/RemoteException
    //   66	100	218	android/os/RemoteException
    //   102	158	218	android/os/RemoteException
    //   165	174	218	android/os/RemoteException
    //   176	211	218	android/os/RemoteException
  }
  
  /* Error */
  private int runQueryIntentServices()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 951	com/android/server/pm/PackageManagerShellCommand:parseIntentAndUser	()Landroid/content/Intent;
    //   4: astore_2
    //   5: aload_0
    //   6: getfield 42	com/android/server/pm/PackageManagerShellCommand:mInterface	Landroid/content/pm/IPackageManager;
    //   9: aload_2
    //   10: aconst_null
    //   11: iconst_0
    //   12: aload_0
    //   13: getfield 523	com/android/server/pm/PackageManagerShellCommand:mTargetUser	I
    //   16: invokeinterface 990 5 0
    //   21: invokevirtual 230	android/content/pm/ParceledListSlice:getList	()Ljava/util/List;
    //   24: astore_2
    //   25: aload_0
    //   26: invokevirtual 95	com/android/server/pm/PackageManagerShellCommand:getOutPrintWriter	()Ljava/io/PrintWriter;
    //   29: astore_3
    //   30: aload_2
    //   31: ifnull +12 -> 43
    //   34: aload_2
    //   35: invokeinterface 233 1 0
    //   40: ifgt +26 -> 66
    //   43: aload_3
    //   44: ldc_w 992
    //   47: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   50: iconst_0
    //   51: ireturn
    //   52: astore_2
    //   53: new 861	java/lang/RuntimeException
    //   56: dup
    //   57: aload_2
    //   58: invokevirtual 958	java/net/URISyntaxException:getMessage	()Ljava/lang/String;
    //   61: aload_2
    //   62: invokespecial 961	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   65: athrow
    //   66: aload_0
    //   67: getfield 527	com/android/server/pm/PackageManagerShellCommand:mComponents	Z
    //   70: ifne +95 -> 165
    //   73: aload_3
    //   74: aload_2
    //   75: invokeinterface 233 1 0
    //   80: invokevirtual 854	java/io/PrintWriter:print	(I)V
    //   83: aload_3
    //   84: ldc_w 994
    //   87: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   90: new 598	android/util/PrintWriterPrinter
    //   93: dup
    //   94: aload_3
    //   95: invokespecial 966	android/util/PrintWriterPrinter:<init>	(Ljava/io/PrintWriter;)V
    //   98: astore 4
    //   100: iconst_0
    //   101: istore_1
    //   102: iload_1
    //   103: aload_2
    //   104: invokeinterface 233 1 0
    //   109: if_icmpge -59 -> 50
    //   112: aload_3
    //   113: ldc_w 996
    //   116: invokevirtual 218	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   119: aload_3
    //   120: iload_1
    //   121: invokevirtual 854	java/io/PrintWriter:print	(I)V
    //   124: aload_3
    //   125: ldc_w 970
    //   128: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   131: aload_0
    //   132: aload 4
    //   134: ldc_w 972
    //   137: aload_2
    //   138: iload_1
    //   139: invokeinterface 234 2 0
    //   144: checkcast 552	android/content/pm/ResolveInfo
    //   147: aload_0
    //   148: getfield 525	com/android/server/pm/PackageManagerShellCommand:mBrief	Z
    //   151: aload_0
    //   152: getfield 527	com/android/server/pm/PackageManagerShellCommand:mComponents	Z
    //   155: invokespecial 974	com/android/server/pm/PackageManagerShellCommand:printResolveInfo	(Landroid/util/PrintWriterPrinter;Ljava/lang/String;Landroid/content/pm/ResolveInfo;ZZ)V
    //   158: iload_1
    //   159: iconst_1
    //   160: iadd
    //   161: istore_1
    //   162: goto -60 -> 102
    //   165: new 598	android/util/PrintWriterPrinter
    //   168: dup
    //   169: aload_3
    //   170: invokespecial 966	android/util/PrintWriterPrinter:<init>	(Ljava/io/PrintWriter;)V
    //   173: astore_3
    //   174: iconst_0
    //   175: istore_1
    //   176: iload_1
    //   177: aload_2
    //   178: invokeinterface 233 1 0
    //   183: if_icmpge -133 -> 50
    //   186: aload_0
    //   187: aload_3
    //   188: ldc -64
    //   190: aload_2
    //   191: iload_1
    //   192: invokeinterface 234 2 0
    //   197: checkcast 552	android/content/pm/ResolveInfo
    //   200: aload_0
    //   201: getfield 525	com/android/server/pm/PackageManagerShellCommand:mBrief	Z
    //   204: aload_0
    //   205: getfield 527	com/android/server/pm/PackageManagerShellCommand:mComponents	Z
    //   208: invokespecial 974	com/android/server/pm/PackageManagerShellCommand:printResolveInfo	(Landroid/util/PrintWriterPrinter;Ljava/lang/String;Landroid/content/pm/ResolveInfo;ZZ)V
    //   211: iload_1
    //   212: iconst_1
    //   213: iadd
    //   214: istore_1
    //   215: goto -39 -> 176
    //   218: astore_2
    //   219: new 861	java/lang/RuntimeException
    //   222: dup
    //   223: ldc_w 976
    //   226: aload_2
    //   227: invokespecial 961	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   230: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	231	0	this	PackageManagerShellCommand
    //   101	114	1	i	int
    //   4	31	2	localObject1	Object
    //   52	139	2	localURISyntaxException	URISyntaxException
    //   218	9	2	localRemoteException	RemoteException
    //   29	159	3	localObject2	Object
    //   98	35	4	localPrintWriterPrinter	PrintWriterPrinter
    // Exception table:
    //   from	to	target	type
    //   0	5	52	java/net/URISyntaxException
    //   5	30	218	android/os/RemoteException
    //   34	43	218	android/os/RemoteException
    //   43	50	218	android/os/RemoteException
    //   66	100	218	android/os/RemoteException
    //   102	158	218	android/os/RemoteException
    //   165	174	218	android/os/RemoteException
    //   176	211	218	android/os/RemoteException
  }
  
  private int runRemoveSplit(String paramString1, String paramString2)
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    PackageInstaller.SessionParams localSessionParams = new PackageInstaller.SessionParams(2);
    localSessionParams.installFlags |= 0x2;
    localSessionParams.appPackageName = paramString1;
    k = doCreateSession(localSessionParams, null, -1);
    int j = 1;
    int i = j;
    try
    {
      int m = doRemoveSplit(k, paramString2, false);
      if (m != 0)
      {
        if (1 != 0) {}
        try
        {
          doAbandonSession(k, false);
          return 1;
        }
        catch (Exception paramString1)
        {
          return 1;
        }
      }
      i = j;
      j = doCommitSession(k, false);
      if (j != 0)
      {
        if (1 != 0) {}
        try
        {
          doAbandonSession(k, false);
          return 1;
        }
        catch (Exception paramString1)
        {
          return 1;
        }
      }
      i = 0;
      localPrintWriter.println("Success");
      if (0 != 0) {}
      try
      {
        doAbandonSession(k, false);
        return 0;
      }
      catch (Exception paramString1)
      {
        return 0;
      }
      try
      {
        doAbandonSession(k, false);
        throw paramString1;
      }
      catch (Exception paramString2)
      {
        for (;;) {}
      }
    }
    finally
    {
      if (i == 0) {}
    }
  }
  
  /* Error */
  private int runResolveActivity()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 951	com/android/server/pm/PackageManagerShellCommand:parseIntentAndUser	()Landroid/content/Intent;
    //   4: astore_1
    //   5: aload_0
    //   6: getfield 42	com/android/server/pm/PackageManagerShellCommand:mInterface	Landroid/content/pm/IPackageManager;
    //   9: aload_1
    //   10: aconst_null
    //   11: iconst_0
    //   12: aload_0
    //   13: getfield 523	com/android/server/pm/PackageManagerShellCommand:mTargetUser	I
    //   16: invokeinterface 1003 5 0
    //   21: astore_1
    //   22: aload_0
    //   23: invokevirtual 95	com/android/server/pm/PackageManagerShellCommand:getOutPrintWriter	()Ljava/io/PrintWriter;
    //   26: astore_2
    //   27: aload_1
    //   28: ifnonnull +26 -> 54
    //   31: aload_2
    //   32: ldc_w 1005
    //   35: invokevirtual 122	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   38: iconst_0
    //   39: ireturn
    //   40: astore_1
    //   41: new 861	java/lang/RuntimeException
    //   44: dup
    //   45: aload_1
    //   46: invokevirtual 958	java/net/URISyntaxException:getMessage	()Ljava/lang/String;
    //   49: aload_1
    //   50: invokespecial 961	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   53: athrow
    //   54: aload_0
    //   55: new 598	android/util/PrintWriterPrinter
    //   58: dup
    //   59: aload_2
    //   60: invokespecial 966	android/util/PrintWriterPrinter:<init>	(Ljava/io/PrintWriter;)V
    //   63: ldc -64
    //   65: aload_1
    //   66: aload_0
    //   67: getfield 525	com/android/server/pm/PackageManagerShellCommand:mBrief	Z
    //   70: aload_0
    //   71: getfield 527	com/android/server/pm/PackageManagerShellCommand:mComponents	Z
    //   74: invokespecial 974	com/android/server/pm/PackageManagerShellCommand:printResolveInfo	(Landroid/util/PrintWriterPrinter;Ljava/lang/String;Landroid/content/pm/ResolveInfo;ZZ)V
    //   77: iconst_0
    //   78: ireturn
    //   79: astore_1
    //   80: new 861	java/lang/RuntimeException
    //   83: dup
    //   84: ldc_w 976
    //   87: aload_1
    //   88: invokespecial 961	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   91: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	92	0	this	PackageManagerShellCommand
    //   4	24	1	localObject	Object
    //   40	26	1	localURISyntaxException	URISyntaxException
    //   79	9	1	localRemoteException	RemoteException
    //   26	34	2	localPrintWriter	PrintWriter
    // Exception table:
    //   from	to	target	type
    //   0	5	40	java/net/URISyntaxException
    //   5	27	79	android/os/RemoteException
    //   31	38	79	android/os/RemoteException
    //   54	77	79	android/os/RemoteException
  }
  
  private int runSetHomeActivity()
  {
    ComponentName localComponentName = null;
    PrintWriter localPrintWriter = getOutPrintWriter();
    for (int i = 0;; i = UserHandle.parseUserArg(getNextArgRequired()))
    {
      str = getNextOption();
      if (str == null) {
        break label69;
      }
      if (!str.equals("--user")) {
        break;
      }
    }
    localPrintWriter.println("Error: Unknown option: " + str);
    return 1;
    label69:
    String str = getNextArg();
    if (str != null) {
      localComponentName = ComponentName.unflattenFromString(str);
    }
    if (localComponentName == null)
    {
      localPrintWriter.println("Error: component name not specified or invalid");
      return 1;
    }
    try
    {
      this.mInterface.setHomeActivity(localComponentName, i);
      localPrintWriter.println("Success");
      return 0;
    }
    catch (Exception localException)
    {
      localPrintWriter.println(localException.toString());
    }
    return 1;
  }
  
  private int runSuspend(boolean paramBoolean)
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    for (int i = 0;; i = UserHandle.parseUserArg(getNextArgRequired()))
    {
      str = getNextOption();
      if (str == null) {
        break label67;
      }
      if (!str.equals("--user")) {
        break;
      }
    }
    localPrintWriter.println("Error: Unknown option: " + str);
    return 1;
    label67:
    String str = getNextArg();
    if (str == null)
    {
      localPrintWriter.println("Error: package name not specified");
      return 1;
    }
    try
    {
      this.mInterface.setPackagesSuspendedAsUser(new String[] { str }, paramBoolean, i);
      localPrintWriter.println("Package " + str + " new suspended state: " + this.mInterface.isPackageSuspendedForUser(str, i));
      return 0;
    }
    catch (RemoteException|IllegalArgumentException localRemoteException)
    {
      localPrintWriter.println(localRemoteException.toString());
    }
    return 1;
  }
  
  private int runUninstall()
    throws RemoteException
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    int i = 0;
    int j = -1;
    for (;;)
    {
      localObject1 = getNextOption();
      if (localObject1 == null) {
        break label89;
      }
      if (((String)localObject1).equals("-k"))
      {
        i |= 0x1;
      }
      else
      {
        if (!((String)localObject1).equals("--user")) {
          break;
        }
        j = UserHandle.parseUserArg(getNextArgRequired());
      }
    }
    localPrintWriter.println("Error: Unknown option: " + (String)localObject1);
    return 1;
    label89:
    Object localObject1 = getNextArg();
    if (localObject1 == null)
    {
      localPrintWriter.println("Error: package name not specified");
      return 1;
    }
    Object localObject2 = getNextArg();
    if (localObject2 != null) {
      return runRemoveSplit((String)localObject1, (String)localObject2);
    }
    int n = translateUserId(j, "runUninstall");
    int k;
    if (n == -1)
    {
      k = 0;
      j = i | 0x2;
      localObject2 = new LocalIntentReceiver(null);
      this.mInterface.getPackageInstaller().uninstall((String)localObject1, null, j, ((LocalIntentReceiver)localObject2).getIntentSender(), k);
      localObject1 = ((LocalIntentReceiver)localObject2).getResult();
      if (((Intent)localObject1).getIntExtra("android.content.pm.extra.STATUS", 1) == 0)
      {
        localPrintWriter.println("Success");
        return 0;
      }
    }
    else
    {
      localObject2 = this.mInterface.getPackageInfo((String)localObject1, 0, n);
      if (localObject2 == null)
      {
        localPrintWriter.println("Failure [not installed for " + n + "]");
        return 1;
      }
      if ((((PackageInfo)localObject2).applicationInfo.flags & 0x1) != 0) {}
      for (int m = 1;; m = 0)
      {
        j = i;
        k = n;
        if (m == 0) {
          break;
        }
        j = i | 0x4;
        k = n;
        break;
      }
    }
    localPrintWriter.println("Failure [" + ((Intent)localObject1).getStringExtra("android.content.pm.extra.STATUS_MESSAGE") + "]");
    return 1;
  }
  
  private int translateUserId(int paramInt, String paramString)
  {
    return ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramInt, true, true, paramString, "pm command");
  }
  
  public int onCommand(String paramString)
  {
    if (paramString == null) {
      return handleDefaultCommands(paramString);
    }
    PrintWriter localPrintWriter = getOutPrintWriter();
    try
    {
      if (paramString.equals("install")) {
        return runInstall();
      }
      if (paramString.equals("install-abandon")) {}
      while (paramString.equals("install-destroy")) {
        return runInstallAbandon();
      }
      if (paramString.equals("install-commit")) {
        return runInstallCommit();
      }
      if (paramString.equals("install-create")) {
        return runInstallCreate();
      }
      if (paramString.equals("install-remove")) {
        return runInstallRemove();
      }
      if (paramString.equals("install-write")) {
        return runInstallWrite();
      }
      if (paramString.equals("compile")) {
        return runCompile();
      }
      if (paramString.equals("dump-profiles")) {
        return runDumpProfiles();
      }
      if (paramString.equals("list")) {
        return runList();
      }
      if (paramString.equals("uninstall")) {
        return runUninstall();
      }
      if (paramString.equals("resolve-activity")) {
        return runResolveActivity();
      }
      if (paramString.equals("query-activities")) {
        return runQueryIntentActivities();
      }
      if (paramString.equals("query-services")) {
        return runQueryIntentServices();
      }
      if (paramString.equals("query-receivers")) {
        return runQueryIntentReceivers();
      }
      if (paramString.equals("suspend")) {
        return runSuspend(true);
      }
      if (paramString.equals("unsuspend")) {
        return runSuspend(false);
      }
      if (paramString.equals("set-home-activity")) {
        return runSetHomeActivity();
      }
      int i = handleDefaultCommands(paramString);
      return i;
    }
    catch (RemoteException paramString)
    {
      localPrintWriter.println("Remote exception: " + paramString);
    }
    return -1;
  }
  
  public void onHelp()
  {
    PrintWriter localPrintWriter = getOutPrintWriter();
    localPrintWriter.println("Package manager (package) commands:");
    localPrintWriter.println("  help");
    localPrintWriter.println("    Print this help text.");
    localPrintWriter.println("");
    localPrintWriter.println("  compile [-m MODE | -r REASON] [-f] [-c]");
    localPrintWriter.println("          [--reset] [--check-prof (true | false)] (-a | TARGET-PACKAGE)");
    localPrintWriter.println("    Trigger compilation of TARGET-PACKAGE or all packages if \"-a\".");
    localPrintWriter.println("    Options:");
    localPrintWriter.println("      -a: compile all packages");
    localPrintWriter.println("      -c: clear profile data before compiling");
    localPrintWriter.println("      -f: force compilation even if not needed");
    localPrintWriter.println("      -m: select compilation mode");
    localPrintWriter.println("          MODE is one of the dex2oat compiler filters:");
    localPrintWriter.println("            verify-none");
    localPrintWriter.println("            verify-at-runtime");
    localPrintWriter.println("            verify-profile");
    localPrintWriter.println("            interpret-only");
    localPrintWriter.println("            space-profile");
    localPrintWriter.println("            space");
    localPrintWriter.println("            speed-profile");
    localPrintWriter.println("            speed");
    localPrintWriter.println("            everything");
    localPrintWriter.println("      -r: select compilation reason");
    localPrintWriter.println("          REASON is one of:");
    int i = 0;
    while (i < PackageManagerServiceCompilerMapping.REASON_STRINGS.length)
    {
      localPrintWriter.println("            " + PackageManagerServiceCompilerMapping.REASON_STRINGS[i]);
      i += 1;
    }
    localPrintWriter.println("      --reset: restore package to its post-install state");
    localPrintWriter.println("      --check-prof (true | false): look at profiles when doing dexopt?");
    localPrintWriter.println("  list features");
    localPrintWriter.println("    Prints all features of the system.");
    localPrintWriter.println("  list instrumentation [-f] [TARGET-PACKAGE]");
    localPrintWriter.println("    Prints all test packages; optionally only those targeting TARGET-PACKAGE");
    localPrintWriter.println("    Options:");
    localPrintWriter.println("      -f: dump the name of the .apk file containing the test package");
    localPrintWriter.println("  list libraries");
    localPrintWriter.println("    Prints all system libraries.");
    localPrintWriter.println("  list packages [-f] [-d] [-e] [-s] [-3] [-i] [-u] [--user USER_ID] [FILTER]");
    localPrintWriter.println("    Prints all packages; optionally only those whose name contains");
    localPrintWriter.println("    the text in FILTER.");
    localPrintWriter.println("    Options:");
    localPrintWriter.println("      -f: see their associated file");
    localPrintWriter.println("      -d: filter to only show disabled packages");
    localPrintWriter.println("      -e: filter to only show enabled packages");
    localPrintWriter.println("      -s: filter to only show system packages");
    localPrintWriter.println("      -3: filter to only show third party packages");
    localPrintWriter.println("      -i: see the installer for the packages");
    localPrintWriter.println("      -u: also include uninstalled packages");
    localPrintWriter.println("  list permission-groups");
    localPrintWriter.println("    Prints all known permission groups.");
    localPrintWriter.println("  list permissions [-g] [-f] [-d] [-u] [GROUP]");
    localPrintWriter.println("    Prints all known permissions; optionally only those in GROUP.");
    localPrintWriter.println("    Options:");
    localPrintWriter.println("      -g: organize by group");
    localPrintWriter.println("      -f: print all information");
    localPrintWriter.println("      -s: short summary");
    localPrintWriter.println("      -d: only list dangerous permissions");
    localPrintWriter.println("      -u: list only the permissions users will see");
    localPrintWriter.println("  dump-profiles TARGET-PACKAGE");
    localPrintWriter.println("    Dumps method/class profile files to");
    localPrintWriter.println("    /data/misc/profman/TARGET-PACKAGE.txt");
    localPrintWriter.println("  resolve-activity [--brief] [--components] [--user USER_ID] INTENT");
    localPrintWriter.println("    Prints the activity that resolves to the given Intent.");
    localPrintWriter.println("  query-activities [--brief] [--components] [--user USER_ID] INTENT");
    localPrintWriter.println("    Prints all activities that can handle the given Intent.");
    localPrintWriter.println("  query-services [--brief] [--components] [--user USER_ID] INTENT");
    localPrintWriter.println("    Prints all services that can handle the given Intent.");
    localPrintWriter.println("  query-receivers [--brief] [--components] [--user USER_ID] INTENT");
    localPrintWriter.println("    Prints all broadcast receivers that can handle the given Intent.");
    localPrintWriter.println("  suspend [--user USER_ID] TARGET-PACKAGE");
    localPrintWriter.println("    Suspends the specified package (as user).");
    localPrintWriter.println("  unsuspend [--user USER_ID] TARGET-PACKAGE");
    localPrintWriter.println("    Unsuspends the specified package (as user).");
    localPrintWriter.println("  set-home-activity [--user USER_ID] TARGET-COMPONENT");
    localPrintWriter.println("    set the default home activity (aka launcher).");
    localPrintWriter.println();
    Intent.printIntentArgsHelp(localPrintWriter, "");
  }
  
  private static class InstallParams
  {
    String installerPackageName;
    PackageInstaller.SessionParams sessionParams;
    int userId = -1;
  }
  
  private static class LocalIntentReceiver
  {
    private IIntentSender.Stub mLocalSender = new IIntentSender.Stub()
    {
      public void send(int paramAnonymousInt, Intent paramAnonymousIntent, String paramAnonymousString1, IIntentReceiver paramAnonymousIIntentReceiver, String paramAnonymousString2, Bundle paramAnonymousBundle)
      {
        try
        {
          PackageManagerShellCommand.LocalIntentReceiver.-get0(PackageManagerShellCommand.LocalIntentReceiver.this).offer(paramAnonymousIntent, 5L, TimeUnit.SECONDS);
          return;
        }
        catch (InterruptedException paramAnonymousIntent)
        {
          throw new RuntimeException(paramAnonymousIntent);
        }
      }
    };
    private final SynchronousQueue<Intent> mResult = new SynchronousQueue();
    
    public IntentSender getIntentSender()
    {
      return new IntentSender(this.mLocalSender);
    }
    
    public Intent getResult()
    {
      try
      {
        Intent localIntent = (Intent)this.mResult.take();
        return localIntent;
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new RuntimeException(localInterruptedException);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageManagerShellCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */