package android.app.backup;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Pair;

public class BackupManager
{
  public static final int ERROR_AGENT_FAILURE = -1003;
  public static final int ERROR_BACKUP_NOT_ALLOWED = -2001;
  public static final int ERROR_PACKAGE_NOT_FOUND = -2002;
  public static final int ERROR_TRANSPORT_ABORTED = -1000;
  public static final int ERROR_TRANSPORT_PACKAGE_REJECTED = -1002;
  public static final int ERROR_TRANSPORT_QUOTA_EXCEEDED = -1005;
  public static final String EXTRA_BACKUP_SERVICES_AVAILABLE = "backup_services_available";
  public static final int SUCCESS = 0;
  private static final String TAG = "BackupManager";
  private static IBackupManager sService;
  private Context mContext;
  
  public BackupManager(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private static void checkServiceBinder()
  {
    if (sService == null) {
      sService = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    }
  }
  
  public static void dataChanged(String paramString)
  {
    
    if (sService != null) {}
    try
    {
      sService.dataChanged(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      Log.e("BackupManager", "dataChanged(pkg) couldn't connect");
    }
  }
  
  public void backupNow()
  {
    
    if (sService != null) {}
    try
    {
      sService.backupNow();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BackupManager", "backupNow() couldn't connect");
    }
  }
  
  public RestoreSession beginRestoreSession()
  {
    Object localObject2 = null;
    checkServiceBinder();
    Object localObject1 = localObject2;
    if (sService != null) {}
    try
    {
      IRestoreSession localIRestoreSession = sService.beginRestoreSession(null, null);
      localObject1 = localObject2;
      if (localIRestoreSession != null) {
        localObject1 = new RestoreSession(this.mContext, localIRestoreSession);
      }
      return (RestoreSession)localObject1;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BackupManager", "beginRestoreSession() couldn't connect");
    }
    return null;
  }
  
  public void dataChanged()
  {
    
    if (sService != null) {}
    try
    {
      sService.dataChanged(this.mContext.getPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.d("BackupManager", "dataChanged() couldn't connect");
    }
  }
  
  public long getAvailableRestoreToken(String paramString)
  {
    
    if (sService != null) {
      try
      {
        long l = sService.getAvailableRestoreToken(paramString);
        return l;
      }
      catch (RemoteException paramString)
      {
        Log.e("BackupManager", "getAvailableRestoreToken() couldn't connect");
      }
    }
    return 0L;
  }
  
  public String getCurrentTransport()
  {
    
    if (sService != null) {
      try
      {
        String str = sService.getCurrentTransport();
        return str;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BackupManager", "getCurrentTransport() couldn't connect");
      }
    }
    return null;
  }
  
  public boolean isAppEligibleForBackup(String paramString)
  {
    
    if (sService != null) {
      try
      {
        boolean bool = sService.isAppEligibleForBackup(paramString);
        return bool;
      }
      catch (RemoteException paramString)
      {
        Log.e("BackupManager", "isAppEligibleForBackup(pkg) couldn't connect");
      }
    }
    return false;
  }
  
  public boolean isBackupEnabled()
  {
    
    if (sService != null) {
      try
      {
        boolean bool = sService.isBackupEnabled();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BackupManager", "isBackupEnabled() couldn't connect");
      }
    }
    return false;
  }
  
  public String[] listAllTransports()
  {
    
    if (sService != null) {
      try
      {
        String[] arrayOfString = sService.listAllTransports();
        return arrayOfString;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BackupManager", "listAllTransports() couldn't connect");
      }
    }
    return null;
  }
  
  public int requestBackup(String[] paramArrayOfString, BackupObserver paramBackupObserver)
  {
    
    if (sService != null)
    {
      if (paramBackupObserver == null) {}
      for (paramBackupObserver = null;; paramBackupObserver = new BackupObserverWrapper(this.mContext, paramBackupObserver)) {
        try
        {
          return sService.requestBackup(paramArrayOfString, paramBackupObserver);
        }
        catch (RemoteException paramArrayOfString)
        {
          Log.e("BackupManager", "requestBackup() couldn't connect");
        }
      }
    }
    return -1;
  }
  
  /* Error */
  public int requestRestore(RestoreObserver paramRestoreObserver)
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore_3
    //   2: invokestatic 66	android/app/backup/BackupManager:checkServiceBinder	()V
    //   5: iload_3
    //   6: istore 4
    //   8: getstatic 46	android/app/backup/BackupManager:sService	Landroid/app/backup/IBackupManager;
    //   11: ifnull +98 -> 109
    //   14: aconst_null
    //   15: astore 7
    //   17: aconst_null
    //   18: astore 6
    //   20: aconst_null
    //   21: astore 8
    //   23: aload 6
    //   25: astore 5
    //   27: getstatic 46	android/app/backup/BackupManager:sService	Landroid/app/backup/IBackupManager;
    //   30: aload_0
    //   31: getfield 42	android/app/backup/BackupManager:mContext	Landroid/content/Context;
    //   34: invokevirtual 101	android/content/Context:getPackageName	()Ljava/lang/String;
    //   37: aconst_null
    //   38: invokeinterface 88 3 0
    //   43: astore 9
    //   45: iload_3
    //   46: istore_2
    //   47: aload 8
    //   49: astore 5
    //   51: aload 9
    //   53: ifnull +40 -> 93
    //   56: aload 6
    //   58: astore 5
    //   60: new 90	android/app/backup/RestoreSession
    //   63: dup
    //   64: aload_0
    //   65: getfield 42	android/app/backup/BackupManager:mContext	Landroid/content/Context;
    //   68: aload 9
    //   70: invokespecial 93	android/app/backup/RestoreSession:<init>	(Landroid/content/Context;Landroid/app/backup/IRestoreSession;)V
    //   73: astore 6
    //   75: aload 6
    //   77: aload_0
    //   78: getfield 42	android/app/backup/BackupManager:mContext	Landroid/content/Context;
    //   81: invokevirtual 101	android/content/Context:getPackageName	()Ljava/lang/String;
    //   84: aload_1
    //   85: invokevirtual 151	android/app/backup/RestoreSession:restorePackage	(Ljava/lang/String;Landroid/app/backup/RestoreObserver;)I
    //   88: istore_2
    //   89: aload 6
    //   91: astore 5
    //   93: iload_2
    //   94: istore 4
    //   96: aload 5
    //   98: ifnull +11 -> 109
    //   101: aload 5
    //   103: invokevirtual 154	android/app/backup/RestoreSession:endRestoreSession	()V
    //   106: iload_2
    //   107: istore 4
    //   109: iload 4
    //   111: ireturn
    //   112: astore_1
    //   113: aload 7
    //   115: astore_1
    //   116: aload_1
    //   117: astore 5
    //   119: ldc 31
    //   121: ldc -100
    //   123: invokestatic 78	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   126: pop
    //   127: iload_3
    //   128: istore 4
    //   130: aload_1
    //   131: ifnull -22 -> 109
    //   134: aload_1
    //   135: invokevirtual 154	android/app/backup/RestoreSession:endRestoreSession	()V
    //   138: iconst_m1
    //   139: ireturn
    //   140: astore_1
    //   141: aload 5
    //   143: ifnull +8 -> 151
    //   146: aload 5
    //   148: invokevirtual 154	android/app/backup/RestoreSession:endRestoreSession	()V
    //   151: aload_1
    //   152: athrow
    //   153: astore_1
    //   154: aload 6
    //   156: astore 5
    //   158: goto -17 -> 141
    //   161: astore_1
    //   162: aload 6
    //   164: astore_1
    //   165: goto -49 -> 116
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	168	0	this	BackupManager
    //   0	168	1	paramRestoreObserver	RestoreObserver
    //   46	61	2	i	int
    //   1	127	3	j	int
    //   6	123	4	k	int
    //   25	132	5	localObject1	Object
    //   18	145	6	localRestoreSession	RestoreSession
    //   15	99	7	localObject2	Object
    //   21	27	8	localObject3	Object
    //   43	26	9	localIRestoreSession	IRestoreSession
    // Exception table:
    //   from	to	target	type
    //   27	45	112	android/os/RemoteException
    //   60	75	112	android/os/RemoteException
    //   27	45	140	finally
    //   60	75	140	finally
    //   119	127	140	finally
    //   75	89	153	finally
    //   75	89	161	android/os/RemoteException
  }
  
  public String selectBackupTransport(String paramString)
  {
    
    if (sService != null) {
      try
      {
        paramString = sService.selectBackupTransport(paramString);
        return paramString;
      }
      catch (RemoteException paramString)
      {
        Log.e("BackupManager", "selectBackupTransport() couldn't connect");
      }
    }
    return null;
  }
  
  public void setAutoRestore(boolean paramBoolean)
  {
    
    if (sService != null) {}
    try
    {
      sService.setAutoRestore(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BackupManager", "setAutoRestore() couldn't connect");
    }
  }
  
  public void setBackupEnabled(boolean paramBoolean)
  {
    
    if (sService != null) {}
    try
    {
      sService.setBackupEnabled(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BackupManager", "setBackupEnabled() couldn't connect");
    }
  }
  
  private class BackupObserverWrapper
    extends IBackupObserver.Stub
  {
    static final int MSG_FINISHED = 3;
    static final int MSG_RESULT = 2;
    static final int MSG_UPDATE = 1;
    final Handler mHandler;
    final BackupObserver mObserver;
    
    BackupObserverWrapper(Context paramContext, BackupObserver paramBackupObserver)
    {
      this.mHandler = new Handler(paramContext.getMainLooper())
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          switch (paramAnonymousMessage.what)
          {
          default: 
            Log.w("BackupManager", "Unknown message: " + paramAnonymousMessage);
            return;
          case 1: 
            paramAnonymousMessage = (Pair)paramAnonymousMessage.obj;
            BackupManager.BackupObserverWrapper.this.mObserver.onUpdate((String)paramAnonymousMessage.first, (BackupProgress)paramAnonymousMessage.second);
            return;
          case 2: 
            BackupManager.BackupObserverWrapper.this.mObserver.onResult((String)paramAnonymousMessage.obj, paramAnonymousMessage.arg1);
            return;
          }
          BackupManager.BackupObserverWrapper.this.mObserver.backupFinished(paramAnonymousMessage.arg1);
        }
      };
      this.mObserver = paramBackupObserver;
    }
    
    public void backupFinished(int paramInt)
    {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(3, paramInt, 0));
    }
    
    public void onResult(String paramString, int paramInt)
    {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(2, paramInt, 0, paramString));
    }
    
    public void onUpdate(String paramString, BackupProgress paramBackupProgress)
    {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(1, Pair.create(paramString, paramBackupProgress)));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/BackupManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */