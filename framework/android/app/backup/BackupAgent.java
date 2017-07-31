package android.app.backup;

import android.app.IBackupAgent.Stub;
import android.app.QueuedWork;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import android.util.ArraySet;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;

public abstract class BackupAgent
  extends ContextWrapper
{
  private static final boolean DEBUG = false;
  private static final String TAG = "BackupAgent";
  public static final int TYPE_DIRECTORY = 2;
  public static final int TYPE_EOF = 0;
  public static final int TYPE_FILE = 1;
  public static final int TYPE_SYMLINK = 3;
  private final IBinder mBinder = new BackupServiceBinder(null).asBinder();
  Handler mHandler = null;
  
  public BackupAgent()
  {
    super(null);
  }
  
  private void applyXmlFiltersAndDoFullBackupForDomain(String paramString1, String paramString2, Map<String, Set<String>> paramMap, ArraySet<String> paramArraySet1, ArraySet<String> paramArraySet2, FullBackupDataOutput paramFullBackupDataOutput)
    throws IOException
  {
    if ((paramMap == null) || (paramMap.size() == 0)) {
      fullBackupFileTree(paramString1, paramString2, FullBackup.getBackupScheme(this).tokenToDirectoryPath(paramString2), paramArraySet1, paramArraySet2, paramFullBackupDataOutput);
    }
    for (;;)
    {
      return;
      if (paramMap.get(paramString2) != null)
      {
        paramMap = ((Set)paramMap.get(paramString2)).iterator();
        while (paramMap.hasNext()) {
          fullBackupFileTree(paramString1, paramString2, (String)paramMap.next(), paramArraySet1, paramArraySet2, paramFullBackupDataOutput);
        }
      }
    }
  }
  
  private boolean isFileEligibleForRestore(File paramFile)
    throws IOException
  {
    Object localObject2 = FullBackup.getBackupScheme(this);
    if (!((FullBackup.BackupScheme)localObject2).isFullBackupContentEnabled())
    {
      if (Log.isLoggable("BackupXmlParserLogging", 2)) {
        Log.v("BackupXmlParserLogging", "onRestoreFile \"" + paramFile.getCanonicalPath() + "\" : fullBackupContent not enabled for " + getPackageName());
      }
      return false;
    }
    String str = paramFile.getCanonicalPath();
    Object localObject1;
    try
    {
      localObject1 = ((FullBackup.BackupScheme)localObject2).maybeParseAndGetCanonicalIncludePaths();
      localObject2 = ((FullBackup.BackupScheme)localObject2).maybeParseAndGetCanonicalExcludePaths();
      if ((localObject2 != null) && (isFileSpecifiedInPathList(paramFile, (Collection)localObject2)))
      {
        if (Log.isLoggable("BackupXmlParserLogging", 2)) {
          Log.v("BackupXmlParserLogging", "onRestoreFile: \"" + str + "\": listed in" + " excludes; skipping.");
        }
        return false;
      }
    }
    catch (XmlPullParserException paramFile)
    {
      if (Log.isLoggable("BackupXmlParserLogging", 2)) {
        Log.v("BackupXmlParserLogging", "onRestoreFile \"" + str + "\" : Exception trying to parse fullBackupContent xml file!" + " Aborting onRestoreFile.", paramFile);
      }
      return false;
    }
    if ((localObject1 == null) || (((Map)localObject1).isEmpty())) {}
    boolean bool2;
    do
    {
      return true;
      boolean bool1 = false;
      localObject1 = ((Map)localObject1).values().iterator();
      do
      {
        bool2 = bool1;
        if (!((Iterator)localObject1).hasNext()) {
          break;
        }
        bool2 = bool1 | isFileSpecifiedInPathList(paramFile, (Set)((Iterator)localObject1).next());
        bool1 = bool2;
      } while (!bool2);
    } while (bool2);
    if (Log.isLoggable("BackupXmlParserLogging", 2)) {
      Log.v("BackupXmlParserLogging", "onRestoreFile: Trying to restore \"" + str + "\" but it isn't specified" + " in the included files; skipping.");
    }
    return false;
  }
  
  private boolean isFileSpecifiedInPathList(File paramFile, Collection<String> paramCollection)
    throws IOException
  {
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      String str = (String)paramCollection.next();
      File localFile = new File(str);
      if (localFile.isDirectory())
      {
        if (paramFile.isDirectory()) {
          return paramFile.equals(localFile);
        }
        return paramFile.getCanonicalPath().startsWith(str);
      }
      if (paramFile.equals(localFile)) {
        return true;
      }
    }
    return false;
  }
  
  private void waitForSharedPrefs()
  {
    Handler localHandler = getHandler();
    SharedPrefsSynchronizer localSharedPrefsSynchronizer = new SharedPrefsSynchronizer();
    localHandler.postAtFrontOfQueue(localSharedPrefsSynchronizer);
    try
    {
      localSharedPrefsSynchronizer.mLatch.await();
      return;
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  public void attach(Context paramContext)
  {
    attachBaseContext(paramContext);
  }
  
  public final void fullBackupFile(File paramFile, FullBackupDataOutput paramFullBackupDataOutput)
  {
    String str9 = null;
    Object localObject1 = getApplicationInfo();
    String str1;
    String str2;
    String str3;
    String str4;
    String str5;
    String str6;
    String str7;
    String str8;
    try
    {
      Object localObject2 = createCredentialProtectedStorageContext();
      str1 = ((Context)localObject2).getDataDir().getCanonicalPath();
      str2 = ((Context)localObject2).getFilesDir().getCanonicalPath();
      String str10 = ((Context)localObject2).getNoBackupFilesDir().getCanonicalPath();
      str3 = ((Context)localObject2).getDatabasePath("foo").getParentFile().getCanonicalPath();
      str4 = ((Context)localObject2).getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
      String str11 = ((Context)localObject2).getCacheDir().getCanonicalPath();
      String str12 = ((Context)localObject2).getCodeCacheDir().getCanonicalPath();
      localObject2 = createDeviceProtectedStorageContext();
      str5 = ((Context)localObject2).getDataDir().getCanonicalPath();
      str6 = ((Context)localObject2).getFilesDir().getCanonicalPath();
      String str13 = ((Context)localObject2).getNoBackupFilesDir().getCanonicalPath();
      str7 = ((Context)localObject2).getDatabasePath("foo").getParentFile().getCanonicalPath();
      str8 = ((Context)localObject2).getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
      String str14 = ((Context)localObject2).getCacheDir().getCanonicalPath();
      String str15 = ((Context)localObject2).getCodeCacheDir().getCanonicalPath();
      if (((ApplicationInfo)localObject1).nativeLibraryDir == null) {}
      for (localObject2 = null;; localObject2 = new File(((ApplicationInfo)localObject1).nativeLibraryDir).getCanonicalPath())
      {
        localObject1 = str9;
        if (Process.myUid() != 1000)
        {
          File localFile = getExternalFilesDir(null);
          localObject1 = str9;
          if (localFile != null) {
            localObject1 = localFile.getCanonicalPath();
          }
        }
        str9 = paramFile.getCanonicalPath();
        if ((!str9.startsWith(str11)) && (!str9.startsWith(str12)) && (!str9.startsWith(str10)) && (!str9.startsWith(str14)) && (!str9.startsWith(str15)) && (!str9.startsWith(str13)) && (!str9.startsWith((String)localObject2))) {
          break;
        }
        Log.w("BackupAgent", "lib, cache, code_cache, and no_backup files are not backed up");
        return;
      }
      if (!str9.startsWith(str3)) {
        break label371;
      }
    }
    catch (IOException paramFile)
    {
      Log.w("BackupAgent", "Unable to obtain canonical paths");
      return;
    }
    paramFile = "db";
    localObject1 = str3;
    for (;;)
    {
      FullBackup.backupToTar(getPackageName(), paramFile, null, (String)localObject1, str9, paramFullBackupDataOutput);
      return;
      label371:
      if (str9.startsWith(str4))
      {
        paramFile = "sp";
        localObject1 = str4;
      }
      else if (str9.startsWith(str2))
      {
        paramFile = "f";
        localObject1 = str2;
      }
      else if (str9.startsWith(str1))
      {
        paramFile = "r";
        localObject1 = str1;
      }
      else if (str9.startsWith(str7))
      {
        paramFile = "d_db";
        localObject1 = str7;
      }
      else if (str9.startsWith(str8))
      {
        paramFile = "d_sp";
        localObject1 = str8;
      }
      else if (str9.startsWith(str6))
      {
        paramFile = "d_f";
        localObject1 = str6;
      }
      else if (str9.startsWith(str5))
      {
        paramFile = "d_r";
        localObject1 = str5;
      }
      else
      {
        if ((localObject1 == null) || (!str9.startsWith((String)localObject1))) {
          break;
        }
        paramFile = "ef";
      }
    }
    Log.w("BackupAgent", "File " + str9 + " is in an unsupported location; skipping");
  }
  
  protected final void fullBackupFileTree(String paramString1, String paramString2, String paramString3, ArraySet<String> paramArraySet1, ArraySet<String> paramArraySet2, FullBackupDataOutput paramFullBackupDataOutput)
  {
    Object localObject2 = FullBackup.getBackupScheme(this).tokenToDirectoryPath(paramString2);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      if (paramString3 == null) {
        return;
      }
      localObject1 = paramString3;
    }
    localObject2 = new File(paramString3);
    if (((File)localObject2).exists())
    {
      paramString3 = new LinkedList();
      paramString3.add(localObject2);
      while (paramString3.size() > 0)
      {
        localObject2 = (File)paramString3.remove(0);
        try
        {
          Object localObject3 = Os.lstat(((File)localObject2).getPath());
          if ((!OsConstants.S_ISREG(((StructStat)localObject3).st_mode)) && (!OsConstants.S_ISDIR(((StructStat)localObject3).st_mode))) {
            continue;
          }
          String str = ((File)localObject2).getCanonicalPath();
          if (((paramArraySet1 != null) && (paramArraySet1.contains(str))) || ((paramArraySet2 != null) && (paramArraySet2.contains(str)))) {
            continue;
          }
          if (!OsConstants.S_ISDIR(((StructStat)localObject3).st_mode)) {
            break label296;
          }
          localObject3 = ((File)localObject2).listFiles();
          if (localObject3 == null) {
            break label296;
          }
          int i = 0;
          int j = localObject3.length;
          while (i < j)
          {
            paramString3.add(0, localObject3[i]);
            i += 1;
          }
        }
        catch (ErrnoException localErrnoException)
        {
          if (!Log.isLoggable("BackupXmlParserLogging", 2)) {
            continue;
          }
          Log.v("BackupXmlParserLogging", "Error scanning file " + localObject2 + " : " + localErrnoException);
        }
        catch (IOException localIOException) {}
        if (Log.isLoggable("BackupXmlParserLogging", 2))
        {
          Log.v("BackupXmlParserLogging", "Error canonicalizing path of " + localObject2);
          continue;
          label296:
          FullBackup.backupToTar(paramString1, paramString2, null, (String)localObject1, localIOException, paramFullBackupDataOutput);
        }
      }
    }
  }
  
  Handler getHandler()
  {
    if (this.mHandler == null) {
      this.mHandler = new Handler(Looper.getMainLooper());
    }
    return this.mHandler;
  }
  
  public abstract void onBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2)
    throws IOException;
  
  public final IBinder onBind()
  {
    return this.mBinder;
  }
  
  public void onCreate() {}
  
  public void onDestroy() {}
  
  public void onFullBackup(FullBackupDataOutput paramFullBackupDataOutput)
    throws IOException
  {
    Object localObject1 = FullBackup.getBackupScheme(this);
    if (!((FullBackup.BackupScheme)localObject1).isFullBackupContentEnabled()) {
      return;
    }
    for (;;)
    {
      try
      {
        Map localMap = ((FullBackup.BackupScheme)localObject1).maybeParseAndGetCanonicalIncludePaths();
        ArraySet localArraySet1 = ((FullBackup.BackupScheme)localObject1).maybeParseAndGetCanonicalExcludePaths();
        String str1 = getPackageName();
        localObject1 = getApplicationInfo();
        Object localObject2 = createCredentialProtectedStorageContext();
        String str2 = ((Context)localObject2).getDataDir().getCanonicalPath();
        String str3 = ((Context)localObject2).getFilesDir().getCanonicalPath();
        String str4 = ((Context)localObject2).getNoBackupFilesDir().getCanonicalPath();
        String str5 = ((Context)localObject2).getDatabasePath("foo").getParentFile().getCanonicalPath();
        String str6 = ((Context)localObject2).getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
        String str7 = ((Context)localObject2).getCacheDir().getCanonicalPath();
        localObject2 = ((Context)localObject2).getCodeCacheDir().getCanonicalPath();
        Object localObject3 = createDeviceProtectedStorageContext();
        String str8 = ((Context)localObject3).getDataDir().getCanonicalPath();
        String str9 = ((Context)localObject3).getFilesDir().getCanonicalPath();
        String str10 = ((Context)localObject3).getNoBackupFilesDir().getCanonicalPath();
        String str11 = ((Context)localObject3).getDatabasePath("foo").getParentFile().getCanonicalPath();
        String str12 = ((Context)localObject3).getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
        String str13 = ((Context)localObject3).getCacheDir().getCanonicalPath();
        localObject3 = ((Context)localObject3).getCodeCacheDir().getCanonicalPath();
        if (((ApplicationInfo)localObject1).nativeLibraryDir != null)
        {
          localObject1 = new File(((ApplicationInfo)localObject1).nativeLibraryDir).getCanonicalPath();
          ArraySet localArraySet2 = new ArraySet();
          localArraySet2.add(str3);
          localArraySet2.add(str4);
          localArraySet2.add(str5);
          localArraySet2.add(str6);
          localArraySet2.add(str7);
          localArraySet2.add(localObject2);
          localArraySet2.add(str9);
          localArraySet2.add(str10);
          localArraySet2.add(str11);
          localArraySet2.add(str12);
          localArraySet2.add(str13);
          localArraySet2.add(localObject3);
          if (localObject1 != null) {
            localArraySet2.add(localObject1);
          }
          applyXmlFiltersAndDoFullBackupForDomain(str1, "r", localMap, localArraySet1, localArraySet2, paramFullBackupDataOutput);
          localArraySet2.add(str2);
          applyXmlFiltersAndDoFullBackupForDomain(str1, "d_r", localMap, localArraySet1, localArraySet2, paramFullBackupDataOutput);
          localArraySet2.add(str8);
          localArraySet2.remove(str3);
          applyXmlFiltersAndDoFullBackupForDomain(str1, "f", localMap, localArraySet1, localArraySet2, paramFullBackupDataOutput);
          localArraySet2.add(str3);
          localArraySet2.remove(str9);
          applyXmlFiltersAndDoFullBackupForDomain(str1, "d_f", localMap, localArraySet1, localArraySet2, paramFullBackupDataOutput);
          localArraySet2.add(str9);
          localArraySet2.remove(str5);
          applyXmlFiltersAndDoFullBackupForDomain(str1, "db", localMap, localArraySet1, localArraySet2, paramFullBackupDataOutput);
          localArraySet2.add(str5);
          localArraySet2.remove(str11);
          applyXmlFiltersAndDoFullBackupForDomain(str1, "d_db", localMap, localArraySet1, localArraySet2, paramFullBackupDataOutput);
          localArraySet2.add(str11);
          localArraySet2.remove(str6);
          applyXmlFiltersAndDoFullBackupForDomain(str1, "sp", localMap, localArraySet1, localArraySet2, paramFullBackupDataOutput);
          localArraySet2.add(str6);
          localArraySet2.remove(str12);
          applyXmlFiltersAndDoFullBackupForDomain(str1, "d_sp", localMap, localArraySet1, localArraySet2, paramFullBackupDataOutput);
          localArraySet2.add(str12);
          if ((Process.myUid() != 1000) && (getExternalFilesDir(null) != null)) {
            applyXmlFiltersAndDoFullBackupForDomain(str1, "ef", localMap, localArraySet1, localArraySet2, paramFullBackupDataOutput);
          }
          return;
        }
      }
      catch (IOException|XmlPullParserException paramFullBackupDataOutput)
      {
        if (Log.isLoggable("BackupXmlParserLogging", 2)) {
          Log.v("BackupXmlParserLogging", "Exception trying to parse fullBackupContent xml file! Aborting full backup.", paramFullBackupDataOutput);
        }
        return;
      }
      localObject1 = null;
    }
  }
  
  public void onQuotaExceeded(long paramLong1, long paramLong2) {}
  
  public abstract void onRestore(BackupDataInput paramBackupDataInput, int paramInt, ParcelFileDescriptor paramParcelFileDescriptor)
    throws IOException;
  
  protected void onRestoreFile(ParcelFileDescriptor paramParcelFileDescriptor, long paramLong1, int paramInt, String paramString1, String paramString2, long paramLong2, long paramLong3)
    throws IOException
  {
    String str = FullBackup.getBackupScheme(this).tokenToDirectoryPath(paramString1);
    if (paramString1.equals("ef")) {
      paramLong2 = -1L;
    }
    if (str != null)
    {
      paramString1 = new File(str, paramString2);
      if (paramString1.getCanonicalPath().startsWith(str + File.separatorChar))
      {
        onRestoreFile(paramParcelFileDescriptor, paramLong1, paramString1, paramInt, paramLong2, paramLong3);
        return;
      }
    }
    FullBackup.restoreFile(paramParcelFileDescriptor, paramLong1, paramInt, paramLong2, paramLong3, null);
  }
  
  public void onRestoreFile(ParcelFileDescriptor paramParcelFileDescriptor, long paramLong1, File paramFile, int paramInt, long paramLong2, long paramLong3)
    throws IOException
  {
    if (isFileEligibleForRestore(paramFile)) {}
    for (;;)
    {
      FullBackup.restoreFile(paramParcelFileDescriptor, paramLong1, paramInt, paramLong2, paramLong3, paramFile);
      return;
      paramFile = null;
    }
  }
  
  public void onRestoreFinished() {}
  
  private class BackupServiceBinder
    extends IBackupAgent.Stub
  {
    private static final String TAG = "BackupServiceBinder";
    
    private BackupServiceBinder() {}
    
    /* Error */
    public void doBackup(ParcelFileDescriptor paramParcelFileDescriptor1, ParcelFileDescriptor paramParcelFileDescriptor2, ParcelFileDescriptor paramParcelFileDescriptor3, int paramInt, IBackupManager paramIBackupManager)
      throws RemoteException
    {
      // Byte code:
      //   0: invokestatic 37	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore 6
      //   5: new 39	android/app/backup/BackupDataOutput
      //   8: dup
      //   9: aload_2
      //   10: invokevirtual 45	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
      //   13: invokespecial 48	android/app/backup/BackupDataOutput:<init>	(Ljava/io/FileDescriptor;)V
      //   16: astore 8
      //   18: aload_0
      //   19: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   22: aload_1
      //   23: aload 8
      //   25: aload_3
      //   26: invokevirtual 52	android/app/backup/BackupAgent:onBackup	(Landroid/os/ParcelFileDescriptor;Landroid/app/backup/BackupDataOutput;Landroid/os/ParcelFileDescriptor;)V
      //   29: aload_0
      //   30: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   33: invokestatic 55	android/app/backup/BackupAgent:-wrap0	(Landroid/app/backup/BackupAgent;)V
      //   36: lload 6
      //   38: invokestatic 59	android/os/Binder:restoreCallingIdentity	(J)V
      //   41: aload 5
      //   43: iload 4
      //   45: lconst_0
      //   46: invokeinterface 65 4 0
      //   51: invokestatic 69	android/os/Binder:getCallingPid	()I
      //   54: invokestatic 74	android/os/Process:myPid	()I
      //   57: if_icmpeq +15 -> 72
      //   60: aload_1
      //   61: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   64: aload_2
      //   65: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   68: aload_3
      //   69: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   72: return
      //   73: astore 5
      //   75: goto -24 -> 51
      //   78: astore 8
      //   80: ldc 10
      //   82: new 82	java/lang/StringBuilder
      //   85: dup
      //   86: invokespecial 83	java/lang/StringBuilder:<init>	()V
      //   89: ldc 85
      //   91: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   94: aload_0
      //   95: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   98: invokevirtual 95	java/lang/Object:getClass	()Ljava/lang/Class;
      //   101: invokevirtual 101	java/lang/Class:getName	()Ljava/lang/String;
      //   104: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   107: ldc 103
      //   109: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   112: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   115: aload 8
      //   117: invokestatic 112	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   120: pop
      //   121: aload 8
      //   123: athrow
      //   124: astore 8
      //   126: aload_0
      //   127: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   130: invokestatic 55	android/app/backup/BackupAgent:-wrap0	(Landroid/app/backup/BackupAgent;)V
      //   133: lload 6
      //   135: invokestatic 59	android/os/Binder:restoreCallingIdentity	(J)V
      //   138: aload 5
      //   140: iload 4
      //   142: lconst_0
      //   143: invokeinterface 65 4 0
      //   148: invokestatic 69	android/os/Binder:getCallingPid	()I
      //   151: invokestatic 74	android/os/Process:myPid	()I
      //   154: if_icmpeq +15 -> 169
      //   157: aload_1
      //   158: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   161: aload_2
      //   162: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   165: aload_3
      //   166: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   169: aload 8
      //   171: athrow
      //   172: astore 8
      //   174: ldc 10
      //   176: new 82	java/lang/StringBuilder
      //   179: dup
      //   180: invokespecial 83	java/lang/StringBuilder:<init>	()V
      //   183: ldc 85
      //   185: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   188: aload_0
      //   189: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   192: invokevirtual 95	java/lang/Object:getClass	()Ljava/lang/Class;
      //   195: invokevirtual 101	java/lang/Class:getName	()Ljava/lang/String;
      //   198: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   201: ldc 103
      //   203: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   206: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   209: aload 8
      //   211: invokestatic 112	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   214: pop
      //   215: new 31	java/lang/RuntimeException
      //   218: dup
      //   219: aload 8
      //   221: invokespecial 115	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
      //   224: athrow
      //   225: astore 5
      //   227: goto -79 -> 148
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	230	0	this	BackupServiceBinder
      //   0	230	1	paramParcelFileDescriptor1	ParcelFileDescriptor
      //   0	230	2	paramParcelFileDescriptor2	ParcelFileDescriptor
      //   0	230	3	paramParcelFileDescriptor3	ParcelFileDescriptor
      //   0	230	4	paramInt	int
      //   0	230	5	paramIBackupManager	IBackupManager
      //   3	131	6	l	long
      //   16	8	8	localBackupDataOutput	BackupDataOutput
      //   78	44	8	localRuntimeException	RuntimeException
      //   124	46	8	localObject	Object
      //   172	48	8	localIOException	IOException
      // Exception table:
      //   from	to	target	type
      //   41	51	73	android/os/RemoteException
      //   18	29	78	java/lang/RuntimeException
      //   18	29	124	finally
      //   80	124	124	finally
      //   174	225	124	finally
      //   18	29	172	java/io/IOException
      //   138	148	225	android/os/RemoteException
    }
    
    /* Error */
    public void doFullBackup(ParcelFileDescriptor paramParcelFileDescriptor, int paramInt, IBackupManager paramIBackupManager)
    {
      // Byte code:
      //   0: invokestatic 37	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore 4
      //   5: aload_0
      //   6: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   9: invokestatic 55	android/app/backup/BackupAgent:-wrap0	(Landroid/app/backup/BackupAgent;)V
      //   12: aload_0
      //   13: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   16: new 120	android/app/backup/FullBackupDataOutput
      //   19: dup
      //   20: aload_1
      //   21: invokespecial 123	android/app/backup/FullBackupDataOutput:<init>	(Landroid/os/ParcelFileDescriptor;)V
      //   24: invokevirtual 127	android/app/backup/BackupAgent:onFullBackup	(Landroid/app/backup/FullBackupDataOutput;)V
      //   27: aload_0
      //   28: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   31: invokestatic 55	android/app/backup/BackupAgent:-wrap0	(Landroid/app/backup/BackupAgent;)V
      //   34: new 129	java/io/FileOutputStream
      //   37: dup
      //   38: aload_1
      //   39: invokevirtual 45	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
      //   42: invokespecial 130	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
      //   45: iconst_4
      //   46: newarray <illegal type>
      //   48: invokevirtual 134	java/io/FileOutputStream:write	([B)V
      //   51: lload 4
      //   53: invokestatic 59	android/os/Binder:restoreCallingIdentity	(J)V
      //   56: aload_3
      //   57: iload_2
      //   58: lconst_0
      //   59: invokeinterface 65 4 0
      //   64: invokestatic 69	android/os/Binder:getCallingPid	()I
      //   67: invokestatic 74	android/os/Process:myPid	()I
      //   70: if_icmpeq +7 -> 77
      //   73: aload_1
      //   74: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   77: return
      //   78: astore 6
      //   80: ldc 10
      //   82: ldc -120
      //   84: invokestatic 140	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   87: pop
      //   88: goto -37 -> 51
      //   91: astore_3
      //   92: goto -28 -> 64
      //   95: astore 6
      //   97: ldc 10
      //   99: new 82	java/lang/StringBuilder
      //   102: dup
      //   103: invokespecial 83	java/lang/StringBuilder:<init>	()V
      //   106: ldc -114
      //   108: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   111: aload_0
      //   112: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   115: invokevirtual 95	java/lang/Object:getClass	()Ljava/lang/Class;
      //   118: invokevirtual 101	java/lang/Class:getName	()Ljava/lang/String;
      //   121: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   124: ldc 103
      //   126: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   129: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   132: aload 6
      //   134: invokestatic 112	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   137: pop
      //   138: aload 6
      //   140: athrow
      //   141: astore 6
      //   143: aload_0
      //   144: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   147: invokestatic 55	android/app/backup/BackupAgent:-wrap0	(Landroid/app/backup/BackupAgent;)V
      //   150: new 129	java/io/FileOutputStream
      //   153: dup
      //   154: aload_1
      //   155: invokevirtual 45	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
      //   158: invokespecial 130	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
      //   161: iconst_4
      //   162: newarray <illegal type>
      //   164: invokevirtual 134	java/io/FileOutputStream:write	([B)V
      //   167: lload 4
      //   169: invokestatic 59	android/os/Binder:restoreCallingIdentity	(J)V
      //   172: aload_3
      //   173: iload_2
      //   174: lconst_0
      //   175: invokeinterface 65 4 0
      //   180: invokestatic 69	android/os/Binder:getCallingPid	()I
      //   183: invokestatic 74	android/os/Process:myPid	()I
      //   186: if_icmpeq +7 -> 193
      //   189: aload_1
      //   190: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   193: aload 6
      //   195: athrow
      //   196: astore 6
      //   198: ldc 10
      //   200: new 82	java/lang/StringBuilder
      //   203: dup
      //   204: invokespecial 83	java/lang/StringBuilder:<init>	()V
      //   207: ldc -114
      //   209: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   212: aload_0
      //   213: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   216: invokevirtual 95	java/lang/Object:getClass	()Ljava/lang/Class;
      //   219: invokevirtual 101	java/lang/Class:getName	()Ljava/lang/String;
      //   222: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   225: ldc 103
      //   227: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   230: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   233: aload 6
      //   235: invokestatic 112	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   238: pop
      //   239: new 31	java/lang/RuntimeException
      //   242: dup
      //   243: aload 6
      //   245: invokespecial 115	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
      //   248: athrow
      //   249: astore 7
      //   251: ldc 10
      //   253: ldc -120
      //   255: invokestatic 140	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   258: pop
      //   259: goto -92 -> 167
      //   262: astore_3
      //   263: goto -83 -> 180
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	266	0	this	BackupServiceBinder
      //   0	266	1	paramParcelFileDescriptor	ParcelFileDescriptor
      //   0	266	2	paramInt	int
      //   0	266	3	paramIBackupManager	IBackupManager
      //   3	165	4	l	long
      //   78	1	6	localIOException1	IOException
      //   95	44	6	localRuntimeException	RuntimeException
      //   141	53	6	localObject	Object
      //   196	48	6	localIOException2	IOException
      //   249	1	7	localIOException3	IOException
      // Exception table:
      //   from	to	target	type
      //   34	51	78	java/io/IOException
      //   56	64	91	android/os/RemoteException
      //   12	27	95	java/lang/RuntimeException
      //   12	27	141	finally
      //   97	141	141	finally
      //   198	249	141	finally
      //   12	27	196	java/io/IOException
      //   150	167	249	java/io/IOException
      //   172	180	262	android/os/RemoteException
    }
    
    /* Error */
    public void doMeasureFullBackup(int paramInt, IBackupManager paramIBackupManager)
    {
      // Byte code:
      //   0: invokestatic 37	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore_3
      //   4: new 120	android/app/backup/FullBackupDataOutput
      //   7: dup
      //   8: invokespecial 145	android/app/backup/FullBackupDataOutput:<init>	()V
      //   11: astore 5
      //   13: aload_0
      //   14: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   17: invokestatic 55	android/app/backup/BackupAgent:-wrap0	(Landroid/app/backup/BackupAgent;)V
      //   20: aload_0
      //   21: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   24: aload 5
      //   26: invokevirtual 127	android/app/backup/BackupAgent:onFullBackup	(Landroid/app/backup/FullBackupDataOutput;)V
      //   29: lload_3
      //   30: invokestatic 59	android/os/Binder:restoreCallingIdentity	(J)V
      //   33: aload_2
      //   34: iload_1
      //   35: aload 5
      //   37: invokevirtual 148	android/app/backup/FullBackupDataOutput:getSize	()J
      //   40: invokeinterface 65 4 0
      //   45: return
      //   46: astore 6
      //   48: ldc 10
      //   50: new 82	java/lang/StringBuilder
      //   53: dup
      //   54: invokespecial 83	java/lang/StringBuilder:<init>	()V
      //   57: ldc -106
      //   59: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   62: aload_0
      //   63: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   66: invokevirtual 95	java/lang/Object:getClass	()Ljava/lang/Class;
      //   69: invokevirtual 101	java/lang/Class:getName	()Ljava/lang/String;
      //   72: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   75: ldc 103
      //   77: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   80: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   83: aload 6
      //   85: invokestatic 112	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   88: pop
      //   89: aload 6
      //   91: athrow
      //   92: astore 6
      //   94: lload_3
      //   95: invokestatic 59	android/os/Binder:restoreCallingIdentity	(J)V
      //   98: aload_2
      //   99: iload_1
      //   100: aload 5
      //   102: invokevirtual 148	android/app/backup/FullBackupDataOutput:getSize	()J
      //   105: invokeinterface 65 4 0
      //   110: aload 6
      //   112: athrow
      //   113: astore 6
      //   115: ldc 10
      //   117: new 82	java/lang/StringBuilder
      //   120: dup
      //   121: invokespecial 83	java/lang/StringBuilder:<init>	()V
      //   124: ldc -106
      //   126: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   129: aload_0
      //   130: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   133: invokevirtual 95	java/lang/Object:getClass	()Ljava/lang/Class;
      //   136: invokevirtual 101	java/lang/Class:getName	()Ljava/lang/String;
      //   139: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   142: ldc 103
      //   144: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   147: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   150: aload 6
      //   152: invokestatic 112	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   155: pop
      //   156: new 31	java/lang/RuntimeException
      //   159: dup
      //   160: aload 6
      //   162: invokespecial 115	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
      //   165: athrow
      //   166: astore_2
      //   167: goto -57 -> 110
      //   170: astore_2
      //   171: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	172	0	this	BackupServiceBinder
      //   0	172	1	paramInt	int
      //   0	172	2	paramIBackupManager	IBackupManager
      //   3	92	3	l	long
      //   11	90	5	localFullBackupDataOutput	FullBackupDataOutput
      //   46	44	6	localRuntimeException	RuntimeException
      //   92	19	6	localObject	Object
      //   113	48	6	localIOException	IOException
      // Exception table:
      //   from	to	target	type
      //   20	29	46	java/lang/RuntimeException
      //   20	29	92	finally
      //   48	92	92	finally
      //   115	166	92	finally
      //   20	29	113	java/io/IOException
      //   98	110	166	android/os/RemoteException
      //   33	45	170	android/os/RemoteException
    }
    
    public void doQuotaExceeded(long paramLong1, long paramLong2)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        BackupAgent.this.onQuotaExceeded(paramLong1, paramLong2);
        return;
      }
      catch (Exception localException)
      {
        Log.d("BackupServiceBinder", "onQuotaExceeded(" + BackupAgent.this.getClass().getName() + ") threw", localException);
        throw localException;
      }
      finally
      {
        BackupAgent.-wrap0(BackupAgent.this);
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public void doRestore(ParcelFileDescriptor paramParcelFileDescriptor1, int paramInt1, ParcelFileDescriptor paramParcelFileDescriptor2, int paramInt2, IBackupManager paramIBackupManager)
      throws RemoteException
    {
      // Byte code:
      //   0: invokestatic 37	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore 6
      //   5: new 163	android/app/backup/BackupDataInput
      //   8: dup
      //   9: aload_1
      //   10: invokevirtual 45	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
      //   13: invokespecial 164	android/app/backup/BackupDataInput:<init>	(Ljava/io/FileDescriptor;)V
      //   16: astore 8
      //   18: aload_0
      //   19: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   22: aload 8
      //   24: iload_2
      //   25: aload_3
      //   26: invokevirtual 168	android/app/backup/BackupAgent:onRestore	(Landroid/app/backup/BackupDataInput;ILandroid/os/ParcelFileDescriptor;)V
      //   29: aload_0
      //   30: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   33: invokestatic 55	android/app/backup/BackupAgent:-wrap0	(Landroid/app/backup/BackupAgent;)V
      //   36: lload 6
      //   38: invokestatic 59	android/os/Binder:restoreCallingIdentity	(J)V
      //   41: aload 5
      //   43: iload 4
      //   45: lconst_0
      //   46: invokeinterface 65 4 0
      //   51: invokestatic 69	android/os/Binder:getCallingPid	()I
      //   54: invokestatic 74	android/os/Process:myPid	()I
      //   57: if_icmpeq +11 -> 68
      //   60: aload_1
      //   61: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   64: aload_3
      //   65: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   68: return
      //   69: astore 5
      //   71: goto -20 -> 51
      //   74: astore 8
      //   76: ldc 10
      //   78: new 82	java/lang/StringBuilder
      //   81: dup
      //   82: invokespecial 83	java/lang/StringBuilder:<init>	()V
      //   85: ldc -86
      //   87: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   90: aload_0
      //   91: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   94: invokevirtual 95	java/lang/Object:getClass	()Ljava/lang/Class;
      //   97: invokevirtual 101	java/lang/Class:getName	()Ljava/lang/String;
      //   100: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   103: ldc 103
      //   105: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   108: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   111: aload 8
      //   113: invokestatic 112	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   116: pop
      //   117: aload 8
      //   119: athrow
      //   120: astore 8
      //   122: aload_0
      //   123: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   126: invokestatic 55	android/app/backup/BackupAgent:-wrap0	(Landroid/app/backup/BackupAgent;)V
      //   129: lload 6
      //   131: invokestatic 59	android/os/Binder:restoreCallingIdentity	(J)V
      //   134: aload 5
      //   136: iload 4
      //   138: lconst_0
      //   139: invokeinterface 65 4 0
      //   144: invokestatic 69	android/os/Binder:getCallingPid	()I
      //   147: invokestatic 74	android/os/Process:myPid	()I
      //   150: if_icmpeq +11 -> 161
      //   153: aload_1
      //   154: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   157: aload_3
      //   158: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   161: aload 8
      //   163: athrow
      //   164: astore 8
      //   166: ldc 10
      //   168: new 82	java/lang/StringBuilder
      //   171: dup
      //   172: invokespecial 83	java/lang/StringBuilder:<init>	()V
      //   175: ldc -86
      //   177: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   180: aload_0
      //   181: getfield 16	android/app/backup/BackupAgent$BackupServiceBinder:this$0	Landroid/app/backup/BackupAgent;
      //   184: invokevirtual 95	java/lang/Object:getClass	()Ljava/lang/Class;
      //   187: invokevirtual 101	java/lang/Class:getName	()Ljava/lang/String;
      //   190: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   193: ldc 103
      //   195: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   198: invokevirtual 106	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   201: aload 8
      //   203: invokestatic 112	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   206: pop
      //   207: new 31	java/lang/RuntimeException
      //   210: dup
      //   211: aload 8
      //   213: invokespecial 115	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
      //   216: athrow
      //   217: astore 5
      //   219: goto -75 -> 144
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	222	0	this	BackupServiceBinder
      //   0	222	1	paramParcelFileDescriptor1	ParcelFileDescriptor
      //   0	222	2	paramInt1	int
      //   0	222	3	paramParcelFileDescriptor2	ParcelFileDescriptor
      //   0	222	4	paramInt2	int
      //   0	222	5	paramIBackupManager	IBackupManager
      //   3	127	6	l	long
      //   16	7	8	localBackupDataInput	BackupDataInput
      //   74	44	8	localRuntimeException	RuntimeException
      //   120	42	8	localObject	Object
      //   164	48	8	localIOException	IOException
      // Exception table:
      //   from	to	target	type
      //   41	51	69	android/os/RemoteException
      //   18	29	74	java/lang/RuntimeException
      //   18	29	120	finally
      //   76	120	120	finally
      //   166	217	120	finally
      //   18	29	164	java/io/IOException
      //   134	144	217	android/os/RemoteException
    }
    
    public void doRestoreFile(ParcelFileDescriptor paramParcelFileDescriptor, long paramLong1, int paramInt1, String paramString1, String paramString2, long paramLong2, long paramLong3, int paramInt2, IBackupManager paramIBackupManager)
      throws RemoteException
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        BackupAgent.this.onRestoreFile(paramParcelFileDescriptor, paramLong1, paramInt1, paramString1, paramString2, paramLong2, paramLong3);
        BackupAgent.-wrap0(BackupAgent.this);
        Binder.restoreCallingIdentity(l);
        try
        {
          paramIBackupManager.opComplete(paramInt2, 0L);
          if (Binder.getCallingPid() != Process.myPid()) {
            IoUtils.closeQuietly(paramParcelFileDescriptor);
          }
          return;
        }
        catch (RemoteException paramString1)
        {
          for (;;) {}
        }
        try
        {
          paramIBackupManager.opComplete(paramInt2, 0L);
          if (Binder.getCallingPid() != Process.myPid()) {
            IoUtils.closeQuietly(paramParcelFileDescriptor);
          }
          throw paramString1;
        }
        catch (RemoteException paramString2)
        {
          for (;;) {}
        }
      }
      catch (IOException paramString1)
      {
        Log.d("BackupServiceBinder", "onRestoreFile (" + BackupAgent.this.getClass().getName() + ") threw", paramString1);
        throw new RuntimeException(paramString1);
      }
      finally
      {
        BackupAgent.-wrap0(BackupAgent.this);
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void doRestoreFinished(int paramInt, IBackupManager paramIBackupManager)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        BackupAgent.this.onRestoreFinished();
        BackupAgent.-wrap0(BackupAgent.this);
        Binder.restoreCallingIdentity(l);
        try
        {
          paramIBackupManager.opComplete(paramInt, 0L);
          return;
        }
        catch (RemoteException paramIBackupManager)
        {
          return;
        }
        try
        {
          paramIBackupManager.opComplete(paramInt, 0L);
          throw ((Throwable)localObject);
        }
        catch (RemoteException paramIBackupManager)
        {
          for (;;) {}
        }
      }
      catch (Exception localException)
      {
        Log.d("BackupServiceBinder", "onRestoreFinished (" + BackupAgent.this.getClass().getName() + ") threw", localException);
        throw localException;
      }
      finally
      {
        BackupAgent.-wrap0(BackupAgent.this);
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void fail(String paramString)
    {
      BackupAgent.this.getHandler().post(new BackupAgent.FailRunnable(paramString));
    }
  }
  
  static class FailRunnable
    implements Runnable
  {
    private String mMessage;
    
    FailRunnable(String paramString)
    {
      this.mMessage = paramString;
    }
    
    public void run()
    {
      throw new IllegalStateException(this.mMessage);
    }
  }
  
  class SharedPrefsSynchronizer
    implements Runnable
  {
    public final CountDownLatch mLatch = new CountDownLatch(1);
    
    SharedPrefsSynchronizer() {}
    
    public void run()
    {
      QueuedWork.waitToFinish();
      this.mLatch.countDown();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/BackupAgent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */