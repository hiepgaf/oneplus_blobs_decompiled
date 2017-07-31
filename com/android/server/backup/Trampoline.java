package com.android.server.backup;

import android.app.backup.IBackupManager.Stub;
import android.app.backup.IBackupObserver;
import android.app.backup.IFullBackupRestoreObserver;
import android.app.backup.IRestoreSession;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemProperties;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class Trampoline
  extends IBackupManager.Stub
{
  static final String BACKUP_DISABLE_PROPERTY = "ro.backup.disable";
  static final String BACKUP_SUPPRESS_FILENAME = "backup-suppress";
  static final boolean DEBUG_TRAMPOLINE = false;
  static final String TAG = "BackupManagerService";
  final Context mContext;
  final boolean mGlobalDisable;
  volatile BackupManagerService mService;
  final File mSuppressFile;
  
  public Trampoline(Context paramContext)
  {
    this.mContext = paramContext;
    paramContext = new File(Environment.getDataDirectory(), "backup");
    paramContext.mkdirs();
    this.mSuppressFile = new File(paramContext, "backup-suppress");
    this.mGlobalDisable = SystemProperties.getBoolean("ro.backup.disable", false);
  }
  
  public void acknowledgeFullBackupOrRestore(int paramInt, boolean paramBoolean, String paramString1, String paramString2, IFullBackupRestoreObserver paramIFullBackupRestoreObserver)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.acknowledgeFullBackupOrRestore(paramInt, paramBoolean, paramString1, paramString2, paramIFullBackupRestoreObserver);
    }
  }
  
  public void agentConnected(String paramString, IBinder paramIBinder)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.agentConnected(paramString, paramIBinder);
    }
  }
  
  public void agentDisconnected(String paramString)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.agentDisconnected(paramString);
    }
  }
  
  public void backupNow()
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.backupNow();
    }
  }
  
  boolean beginFullBackup(FullBackupJob paramFullBackupJob)
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      return localBackupManagerService.beginFullBackup(paramFullBackupJob);
    }
    return false;
  }
  
  public IRestoreSession beginRestoreSession(String paramString1, String paramString2)
    throws RemoteException
  {
    IRestoreSession localIRestoreSession = null;
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localIRestoreSession = localBackupManagerService.beginRestoreSession(paramString1, paramString2);
    }
    return localIRestoreSession;
  }
  
  public void clearBackupData(String paramString1, String paramString2)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.clearBackupData(paramString1, paramString2);
    }
  }
  
  public void dataChanged(String paramString)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.dataChanged(paramString);
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "BackupManagerService");
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null)
    {
      localBackupManagerService.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      return;
    }
    paramPrintWriter.println("Inactive");
  }
  
  void endFullBackup()
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.endFullBackup();
    }
  }
  
  public void fullBackup(ParcelFileDescriptor paramParcelFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6, boolean paramBoolean7, String[] paramArrayOfString)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.fullBackup(paramParcelFileDescriptor, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5, paramBoolean6, paramBoolean7, paramArrayOfString);
    }
  }
  
  public void fullRestore(ParcelFileDescriptor paramParcelFileDescriptor)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.fullRestore(paramParcelFileDescriptor);
    }
  }
  
  public void fullTransportBackup(String[] paramArrayOfString)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.fullTransportBackup(paramArrayOfString);
    }
  }
  
  public long getAvailableRestoreToken(String paramString)
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      return localBackupManagerService.getAvailableRestoreToken(paramString);
    }
    return 0L;
  }
  
  public Intent getConfigurationIntent(String paramString)
    throws RemoteException
  {
    Intent localIntent = null;
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localIntent = localBackupManagerService.getConfigurationIntent(paramString);
    }
    return localIntent;
  }
  
  public String getCurrentTransport()
    throws RemoteException
  {
    String str = null;
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      str = localBackupManagerService.getCurrentTransport();
    }
    return str;
  }
  
  public Intent getDataManagementIntent(String paramString)
    throws RemoteException
  {
    Intent localIntent = null;
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localIntent = localBackupManagerService.getDataManagementIntent(paramString);
    }
    return localIntent;
  }
  
  public String getDataManagementLabel(String paramString)
    throws RemoteException
  {
    String str = null;
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      str = localBackupManagerService.getDataManagementLabel(paramString);
    }
    return str;
  }
  
  public String getDestinationString(String paramString)
    throws RemoteException
  {
    String str = null;
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      str = localBackupManagerService.getDestinationString(paramString);
    }
    return str;
  }
  
  public String[] getTransportWhitelist()
  {
    String[] arrayOfString = null;
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      arrayOfString = localBackupManagerService.getTransportWhitelist();
    }
    return arrayOfString;
  }
  
  public boolean hasBackupPassword()
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      return localBackupManagerService.hasBackupPassword();
    }
    return false;
  }
  
  /* Error */
  public void initialize(int paramInt)
  {
    // Byte code:
    //   0: iload_1
    //   1: ifne +49 -> 50
    //   4: aload_0
    //   5: getfield 58	com/android/server/backup/Trampoline:mGlobalDisable	Z
    //   8: ifeq +12 -> 20
    //   11: ldc 17
    //   13: ldc -94
    //   15: invokestatic 168	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   18: pop
    //   19: return
    //   20: aload_0
    //   21: monitorenter
    //   22: aload_0
    //   23: getfield 50	com/android/server/backup/Trampoline:mSuppressFile	Ljava/io/File;
    //   26: invokevirtual 171	java/io/File:exists	()Z
    //   29: ifne +22 -> 51
    //   32: aload_0
    //   33: new 67	com/android/server/backup/BackupManagerService
    //   36: dup
    //   37: aload_0
    //   38: getfield 31	com/android/server/backup/Trampoline:mContext	Landroid/content/Context;
    //   41: aload_0
    //   42: invokespecial 174	com/android/server/backup/BackupManagerService:<init>	(Landroid/content/Context;Lcom/android/server/backup/Trampoline;)V
    //   45: putfield 65	com/android/server/backup/Trampoline:mService	Lcom/android/server/backup/BackupManagerService;
    //   48: aload_0
    //   49: monitorexit
    //   50: return
    //   51: ldc 17
    //   53: new 176	java/lang/StringBuilder
    //   56: dup
    //   57: invokespecial 177	java/lang/StringBuilder:<init>	()V
    //   60: ldc -77
    //   62: invokevirtual 183	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   65: iload_1
    //   66: invokevirtual 186	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   69: invokevirtual 189	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   72: invokestatic 168	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   75: pop
    //   76: goto -28 -> 48
    //   79: astore_2
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_2
    //   83: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	84	0	this	Trampoline
    //   0	84	1	paramInt	int
    //   79	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   22	48	79	finally
    //   51	76	79	finally
  }
  
  public boolean isAppEligibleForBackup(String paramString)
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      return localBackupManagerService.isAppEligibleForBackup(paramString);
    }
    return false;
  }
  
  public boolean isBackupEnabled()
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      return localBackupManagerService.isBackupEnabled();
    }
    return false;
  }
  
  public boolean isBackupServiceActive(int paramInt)
  {
    boolean bool = false;
    if (paramInt == 0) {
      try
      {
        BackupManagerService localBackupManagerService = this.mService;
        if (localBackupManagerService != null) {
          bool = true;
        }
        return bool;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    return false;
  }
  
  public String[] listAllTransports()
    throws RemoteException
  {
    String[] arrayOfString = null;
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      arrayOfString = localBackupManagerService.listAllTransports();
    }
    return arrayOfString;
  }
  
  public void opComplete(int paramInt, long paramLong)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.opComplete(paramInt, paramLong);
    }
  }
  
  public int requestBackup(String[] paramArrayOfString, IBackupObserver paramIBackupObserver)
    throws RemoteException
  {
    Integer localInteger = null;
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localInteger = Integer.valueOf(localBackupManagerService.requestBackup(paramArrayOfString, paramIBackupObserver));
    }
    return localInteger.intValue();
  }
  
  public void restoreAtInstall(String paramString, int paramInt)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.restoreAtInstall(paramString, paramInt);
    }
  }
  
  public String selectBackupTransport(String paramString)
    throws RemoteException
  {
    String str = null;
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      str = localBackupManagerService.selectBackupTransport(paramString);
    }
    return str;
  }
  
  public void setAutoRestore(boolean paramBoolean)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.setAutoRestore(paramBoolean);
    }
  }
  
  public void setBackupEnabled(boolean paramBoolean)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.setBackupEnabled(paramBoolean);
    }
  }
  
  public boolean setBackupPassword(String paramString1, String paramString2)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      return localBackupManagerService.setBackupPassword(paramString1, paramString2);
    }
    return false;
  }
  
  public void setBackupProvisioned(boolean paramBoolean)
    throws RemoteException
  {
    BackupManagerService localBackupManagerService = this.mService;
    if (localBackupManagerService != null) {
      localBackupManagerService.setBackupProvisioned(paramBoolean);
    }
  }
  
  /* Error */
  public void setBackupServiceActive(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: invokestatic 249	android/os/Binder:getCallingUid	()I
    //   3: istore_3
    //   4: iload_3
    //   5: sipush 1000
    //   8: if_icmpeq +17 -> 25
    //   11: iload_3
    //   12: ifeq +13 -> 25
    //   15: new 251	java/lang/SecurityException
    //   18: dup
    //   19: ldc -3
    //   21: invokespecial 255	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   24: athrow
    //   25: aload_0
    //   26: getfield 58	com/android/server/backup/Trampoline:mGlobalDisable	Z
    //   29: ifeq +12 -> 41
    //   32: ldc 17
    //   34: ldc -94
    //   36: invokestatic 168	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   39: pop
    //   40: return
    //   41: iload_1
    //   42: ifne +94 -> 136
    //   45: aload_0
    //   46: monitorenter
    //   47: iload_2
    //   48: aload_0
    //   49: iload_1
    //   50: invokevirtual 257	com/android/server/backup/Trampoline:isBackupServiceActive	(I)Z
    //   53: if_icmpeq +81 -> 134
    //   56: new 176	java/lang/StringBuilder
    //   59: dup
    //   60: invokespecial 177	java/lang/StringBuilder:<init>	()V
    //   63: ldc_w 259
    //   66: invokevirtual 183	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: astore 5
    //   71: iload_2
    //   72: ifeq +65 -> 137
    //   75: ldc_w 261
    //   78: astore 4
    //   80: ldc 17
    //   82: aload 5
    //   84: aload 4
    //   86: invokevirtual 183	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   89: ldc_w 263
    //   92: invokevirtual 183	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: iload_1
    //   96: invokevirtual 186	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   99: invokevirtual 189	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   102: invokestatic 168	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   105: pop
    //   106: iload_2
    //   107: ifeq +38 -> 145
    //   110: aload_0
    //   111: new 67	com/android/server/backup/BackupManagerService
    //   114: dup
    //   115: aload_0
    //   116: getfield 31	com/android/server/backup/Trampoline:mContext	Landroid/content/Context;
    //   119: aload_0
    //   120: invokespecial 174	com/android/server/backup/BackupManagerService:<init>	(Landroid/content/Context;Lcom/android/server/backup/Trampoline;)V
    //   123: putfield 65	com/android/server/backup/Trampoline:mService	Lcom/android/server/backup/BackupManagerService;
    //   126: aload_0
    //   127: getfield 50	com/android/server/backup/Trampoline:mSuppressFile	Ljava/io/File;
    //   130: invokevirtual 266	java/io/File:delete	()Z
    //   133: pop
    //   134: aload_0
    //   135: monitorexit
    //   136: return
    //   137: ldc_w 268
    //   140: astore 4
    //   142: goto -62 -> 80
    //   145: aload_0
    //   146: aconst_null
    //   147: putfield 65	com/android/server/backup/Trampoline:mService	Lcom/android/server/backup/BackupManagerService;
    //   150: aload_0
    //   151: getfield 50	com/android/server/backup/Trampoline:mSuppressFile	Ljava/io/File;
    //   154: invokevirtual 271	java/io/File:createNewFile	()Z
    //   157: pop
    //   158: goto -24 -> 134
    //   161: astore 4
    //   163: ldc 17
    //   165: ldc_w 273
    //   168: invokestatic 276	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   171: pop
    //   172: goto -38 -> 134
    //   175: astore 4
    //   177: aload_0
    //   178: monitorexit
    //   179: aload 4
    //   181: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	182	0	this	Trampoline
    //   0	182	1	paramInt	int
    //   0	182	2	paramBoolean	boolean
    //   3	9	3	i	int
    //   78	63	4	str	String
    //   161	1	4	localIOException	java.io.IOException
    //   175	5	4	localObject	Object
    //   69	14	5	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   150	158	161	java/io/IOException
    //   47	71	175	finally
    //   80	106	175	finally
    //   110	134	175	finally
    //   145	150	175	finally
    //   150	158	175	finally
    //   163	172	175	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/backup/Trampoline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */