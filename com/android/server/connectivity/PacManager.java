package com.android.server.connectivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.net.IProxyCallback;
import com.android.net.IProxyCallback.Stub;
import com.android.net.IProxyPortListener.Stub;
import com.android.net.IProxyService;
import com.android.net.IProxyService.Stub;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class PacManager
{
  private static final String ACTION_PAC_REFRESH = "android.net.proxy.PAC_REFRESH";
  private static final String DEFAULT_DELAYS = "8 32 120 14400 43200";
  private static final int DELAY_1 = 0;
  private static final int DELAY_4 = 3;
  private static final int DELAY_LONG = 4;
  public static final String KEY_PROXY = "keyProxy";
  private static final long MAX_PAC_SIZE = 20000000L;
  public static final String PAC_PACKAGE = "com.android.pacprocessor";
  public static final String PAC_SERVICE = "com.android.pacprocessor.PacService";
  public static final String PAC_SERVICE_NAME = "com.android.net.IProxyService";
  public static final String PROXY_PACKAGE = "com.android.proxyhandler";
  public static final String PROXY_SERVICE = "com.android.proxyhandler.ProxyService";
  private static final String TAG = "PacManager";
  private AlarmManager mAlarmManager;
  private ServiceConnection mConnection;
  private Handler mConnectivityHandler;
  private Context mContext;
  private int mCurrentDelay;
  private String mCurrentPac;
  private boolean mHasDownloaded;
  private boolean mHasSentBroadcast;
  private int mLastPort;
  private final HandlerThread mNetThread = new HandlerThread("android.pacmanager", 0);
  private final Handler mNetThreadHandler;
  private Runnable mPacDownloader = new Runnable()
  {
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 14	com/android/server/connectivity/PacManager$1:this$0	Lcom/android/server/connectivity/PacManager;
      //   4: invokestatic 25	com/android/server/connectivity/PacManager:-get5	(Lcom/android/server/connectivity/PacManager;)Ljava/lang/Object;
      //   7: astore_3
      //   8: aload_3
      //   9: monitorenter
      //   10: getstatic 31	android/net/Uri:EMPTY	Landroid/net/Uri;
      //   13: aload_0
      //   14: getfield 14	com/android/server/connectivity/PacManager$1:this$0	Lcom/android/server/connectivity/PacManager;
      //   17: invokestatic 35	com/android/server/connectivity/PacManager:-get4	(Lcom/android/server/connectivity/PacManager;)Landroid/net/Uri;
      //   20: invokevirtual 39	android/net/Uri:equals	(Ljava/lang/Object;)Z
      //   23: istore_1
      //   24: iload_1
      //   25: ifeq +6 -> 31
      //   28: aload_3
      //   29: monitorexit
      //   30: return
      //   31: aload_0
      //   32: getfield 14	com/android/server/connectivity/PacManager$1:this$0	Lcom/android/server/connectivity/PacManager;
      //   35: invokestatic 35	com/android/server/connectivity/PacManager:-get4	(Lcom/android/server/connectivity/PacManager;)Landroid/net/Uri;
      //   38: invokestatic 43	com/android/server/connectivity/PacManager:-wrap1	(Landroid/net/Uri;)Ljava/lang/String;
      //   41: astore_2
      //   42: aload_3
      //   43: monitorexit
      //   44: aload_2
      //   45: ifnull +105 -> 150
      //   48: aload_0
      //   49: getfield 14	com/android/server/connectivity/PacManager$1:this$0	Lcom/android/server/connectivity/PacManager;
      //   52: invokestatic 25	com/android/server/connectivity/PacManager:-get5	(Lcom/android/server/connectivity/PacManager;)Ljava/lang/Object;
      //   55: astore_3
      //   56: aload_3
      //   57: monitorenter
      //   58: aload_2
      //   59: aload_0
      //   60: getfield 14	com/android/server/connectivity/PacManager$1:this$0	Lcom/android/server/connectivity/PacManager;
      //   63: invokestatic 47	com/android/server/connectivity/PacManager:-get0	(Lcom/android/server/connectivity/PacManager;)Ljava/lang/String;
      //   66: invokevirtual 50	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   69: ifne +12 -> 81
      //   72: aload_0
      //   73: getfield 14	com/android/server/connectivity/PacManager$1:this$0	Lcom/android/server/connectivity/PacManager;
      //   76: aload_2
      //   77: invokestatic 54	com/android/server/connectivity/PacManager:-wrap0	(Lcom/android/server/connectivity/PacManager;Ljava/lang/String;)Z
      //   80: pop
      //   81: aload_3
      //   82: monitorexit
      //   83: aload_0
      //   84: getfield 14	com/android/server/connectivity/PacManager$1:this$0	Lcom/android/server/connectivity/PacManager;
      //   87: iconst_1
      //   88: invokestatic 58	com/android/server/connectivity/PacManager:-set0	(Lcom/android/server/connectivity/PacManager;Z)Z
      //   91: pop
      //   92: aload_0
      //   93: getfield 14	com/android/server/connectivity/PacManager$1:this$0	Lcom/android/server/connectivity/PacManager;
      //   96: invokestatic 61	com/android/server/connectivity/PacManager:-wrap4	(Lcom/android/server/connectivity/PacManager;)V
      //   99: aload_0
      //   100: getfield 14	com/android/server/connectivity/PacManager$1:this$0	Lcom/android/server/connectivity/PacManager;
      //   103: invokestatic 64	com/android/server/connectivity/PacManager:-wrap2	(Lcom/android/server/connectivity/PacManager;)V
      //   106: return
      //   107: astore 4
      //   109: aconst_null
      //   110: astore_2
      //   111: ldc 66
      //   113: new 68	java/lang/StringBuilder
      //   116: dup
      //   117: invokespecial 69	java/lang/StringBuilder:<init>	()V
      //   120: ldc 71
      //   122: invokevirtual 75	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   125: aload 4
      //   127: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   130: invokevirtual 82	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   133: invokestatic 88	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   136: pop
      //   137: goto -95 -> 42
      //   140: astore_2
      //   141: aload_3
      //   142: monitorexit
      //   143: aload_2
      //   144: athrow
      //   145: astore_2
      //   146: aload_3
      //   147: monitorexit
      //   148: aload_2
      //   149: athrow
      //   150: aload_0
      //   151: getfield 14	com/android/server/connectivity/PacManager$1:this$0	Lcom/android/server/connectivity/PacManager;
      //   154: invokestatic 91	com/android/server/connectivity/PacManager:-wrap3	(Lcom/android/server/connectivity/PacManager;)V
      //   157: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	158	0	this	1
      //   23	2	1	bool	boolean
      //   41	70	2	str	String
      //   140	4	2	localObject1	Object
      //   145	4	2	localObject2	Object
      //   107	19	4	localIOException	IOException
      // Exception table:
      //   from	to	target	type
      //   31	42	107	java/io/IOException
      //   10	24	140	finally
      //   31	42	140	finally
      //   111	137	140	finally
      //   58	81	145	finally
    }
  };
  private PendingIntent mPacRefreshIntent;
  @GuardedBy("mProxyLock")
  private Uri mPacUrl = Uri.EMPTY;
  private ServiceConnection mProxyConnection;
  private final Object mProxyLock = new Object();
  private int mProxyMessage;
  @GuardedBy("mProxyLock")
  private IProxyService mProxyService;
  
  public PacManager(Context paramContext, Handler paramHandler, int paramInt)
  {
    this.mContext = paramContext;
    this.mLastPort = -1;
    this.mNetThread.start();
    this.mNetThreadHandler = new Handler(this.mNetThread.getLooper());
    this.mPacRefreshIntent = PendingIntent.getBroadcast(paramContext, 0, new Intent("android.net.proxy.PAC_REFRESH"), 0);
    paramContext.registerReceiver(new PacRefreshIntentReceiver(), new IntentFilter("android.net.proxy.PAC_REFRESH"));
    this.mConnectivityHandler = paramHandler;
    this.mProxyMessage = paramInt;
  }
  
  private void bind()
  {
    if (this.mContext == null)
    {
      Log.e("PacManager", "No context for binding");
      return;
    }
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.pacprocessor", "com.android.pacprocessor.PacService");
    if ((this.mProxyConnection != null) && (this.mConnection != null))
    {
      this.mNetThreadHandler.post(this.mPacDownloader);
      return;
    }
    this.mConnection = new ServiceConnection()
    {
      public void onServiceConnected(ComponentName arg1, IBinder paramAnonymousIBinder)
      {
        for (;;)
        {
          synchronized (PacManager.-get5(PacManager.this))
          {
            try
            {
              Log.d("PacManager", "Adding service com.android.net.IProxyService " + paramAnonymousIBinder.getInterfaceDescriptor());
              ServiceManager.addService("com.android.net.IProxyService", paramAnonymousIBinder);
              PacManager.-set3(PacManager.this, IProxyService.Stub.asInterface(paramAnonymousIBinder));
              if (PacManager.-get6(PacManager.this) == null)
              {
                Log.e("PacManager", "No proxy service");
                return;
              }
            }
            catch (RemoteException localRemoteException)
            {
              Log.e("PacManager", "Remote Exception", localRemoteException);
              continue;
            }
          }
          try
          {
            PacManager.-get6(PacManager.this).startPacSystem();
            PacManager.-get2(PacManager.this).post(PacManager.-get3(PacManager.this));
          }
          catch (RemoteException paramAnonymousIBinder)
          {
            for (;;)
            {
              Log.e("PacManager", "Unable to reach ProxyService - PAC will not be started", paramAnonymousIBinder);
            }
          }
        }
      }
      
      public void onServiceDisconnected(ComponentName arg1)
      {
        synchronized (PacManager.-get5(PacManager.this))
        {
          PacManager.-set3(PacManager.this, null);
          return;
        }
      }
    };
    this.mContext.bindService(localIntent, this.mConnection, 1073741829);
    localIntent = new Intent();
    localIntent.setClassName("com.android.proxyhandler", "com.android.proxyhandler.ProxyService");
    this.mProxyConnection = new ServiceConnection()
    {
      public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
      {
        paramAnonymousComponentName = IProxyCallback.Stub.asInterface(paramAnonymousIBinder);
        if (paramAnonymousComponentName != null) {}
        try
        {
          paramAnonymousComponentName.getProxyPort(new IProxyPortListener.Stub()
          {
            public void setProxyPort(int paramAnonymous2Int)
              throws RemoteException
            {
              if (PacManager.-get1(PacManager.this) != -1) {
                PacManager.-set1(PacManager.this, false);
              }
              PacManager.-set2(PacManager.this, paramAnonymous2Int);
              if (paramAnonymous2Int != -1)
              {
                Log.d("PacManager", "Local proxy is bound on " + paramAnonymous2Int);
                PacManager.-wrap4(PacManager.this);
                return;
              }
              Log.e("PacManager", "Received invalid port from Local Proxy, PAC will not be operational");
            }
          });
          return;
        }
        catch (RemoteException paramAnonymousComponentName)
        {
          paramAnonymousComponentName.printStackTrace();
        }
      }
      
      public void onServiceDisconnected(ComponentName paramAnonymousComponentName) {}
    };
    this.mContext.bindService(localIntent, this.mProxyConnection, 1073741829);
  }
  
  private static String get(Uri paramUri)
    throws IOException
  {
    paramUri = new URL(paramUri.toString()).openConnection(Proxy.NO_PROXY);
    long l1 = -1L;
    try
    {
      long l2 = Long.parseLong(paramUri.getHeaderField("Content-Length"));
      l1 = l2;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      ByteArrayOutputStream localByteArrayOutputStream;
      byte[] arrayOfByte;
      for (;;) {}
    }
    if (l1 > 20000000L) {
      throw new IOException("PAC too big: " + l1 + " bytes");
    }
    localByteArrayOutputStream = new ByteArrayOutputStream();
    arrayOfByte = new byte['Ѐ'];
    do
    {
      int i = paramUri.getInputStream().read(arrayOfByte);
      if (i == -1) {
        break;
      }
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    } while (localByteArrayOutputStream.size() <= 20000000L);
    throw new IOException("PAC too big");
    return localByteArrayOutputStream.toString();
  }
  
  private AlarmManager getAlarmManager()
  {
    if (this.mAlarmManager == null) {
      this.mAlarmManager = ((AlarmManager)this.mContext.getSystemService("alarm"));
    }
    return this.mAlarmManager;
  }
  
  private long getDownloadDelay(int paramInt)
  {
    String[] arrayOfString = getPacChangeDelay().split(" ");
    if (paramInt < arrayOfString.length) {
      return Long.parseLong(arrayOfString[paramInt]);
    }
    return 0L;
  }
  
  private int getNextDelay(int paramInt)
  {
    paramInt += 1;
    if (paramInt > 3) {
      return 3;
    }
    return paramInt;
  }
  
  private String getPacChangeDelay()
  {
    Object localObject = this.mContext.getContentResolver();
    String str = SystemProperties.get("conn.pac_change_delay", "8 32 120 14400 43200");
    localObject = Settings.Global.getString((ContentResolver)localObject, "pac_change_delay");
    if (localObject == null) {
      return str;
    }
    return (String)localObject;
  }
  
  private void longSchedule()
  {
    this.mCurrentDelay = 0;
    setDownloadIn(4);
  }
  
  private void reschedule()
  {
    this.mCurrentDelay = getNextDelay(this.mCurrentDelay);
    setDownloadIn(this.mCurrentDelay);
  }
  
  private void sendPacBroadcast(ProxyInfo paramProxyInfo)
  {
    this.mConnectivityHandler.sendMessage(this.mConnectivityHandler.obtainMessage(this.mProxyMessage, paramProxyInfo));
  }
  
  private void sendProxyIfNeeded()
  {
    try
    {
      if (this.mHasDownloaded)
      {
        int i = this.mLastPort;
        if (i != -1) {}
      }
      else
      {
        return;
      }
      if (!this.mHasSentBroadcast)
      {
        sendPacBroadcast(new ProxyInfo(this.mPacUrl, this.mLastPort));
        this.mHasSentBroadcast = true;
      }
      return;
    }
    finally {}
  }
  
  private boolean setCurrentProxyScript(String paramString)
  {
    if (this.mProxyService == null)
    {
      Log.e("PacManager", "setCurrentProxyScript: no proxy service");
      return false;
    }
    try
    {
      this.mProxyService.setPacFile(paramString);
      this.mCurrentPac = paramString;
      return true;
    }
    catch (RemoteException paramString)
    {
      for (;;)
      {
        Log.e("PacManager", "Unable to set PAC file", paramString);
      }
    }
  }
  
  private void setDownloadIn(int paramInt)
  {
    long l1 = getDownloadDelay(paramInt);
    long l2 = SystemClock.elapsedRealtime();
    getAlarmManager().set(3, 1000L * l1 + l2, this.mPacRefreshIntent);
  }
  
  private void unbind()
  {
    if (this.mConnection != null)
    {
      this.mContext.unbindService(this.mConnection);
      this.mConnection = null;
    }
    if (this.mProxyConnection != null)
    {
      this.mContext.unbindService(this.mProxyConnection);
      this.mProxyConnection = null;
    }
    this.mProxyService = null;
    this.mLastPort = -1;
  }
  
  public boolean setCurrentProxyScriptUrl(ProxyInfo arg1)
  {
    try
    {
      if (!Uri.EMPTY.equals(???.getPacFileUrl()))
      {
        if (???.getPacFileUrl().equals(this.mPacUrl))
        {
          int i = ???.getPort();
          if (i > 0) {
            return false;
          }
        }
        synchronized (this.mProxyLock)
        {
          this.mPacUrl = ???.getPacFileUrl();
          this.mCurrentDelay = 0;
          this.mHasSentBroadcast = false;
          this.mHasDownloaded = false;
          getAlarmManager().cancel(this.mPacRefreshIntent);
          bind();
          return true;
        }
      }
      getAlarmManager().cancel(this.mPacRefreshIntent);
    }
    finally {}
    synchronized (this.mProxyLock)
    {
      this.mPacUrl = Uri.EMPTY;
      this.mCurrentPac = null;
      ??? = this.mProxyService;
      if (??? != null) {}
      try
      {
        this.mProxyService.stopPacSystem();
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.w("PacManager", "Failed to stop PAC service", localRemoteException);
          unbind();
        }
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      finally
      {
        unbind();
      }
      return false;
    }
  }
  
  class PacRefreshIntentReceiver
    extends BroadcastReceiver
  {
    PacRefreshIntentReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      PacManager.-get2(PacManager.this).post(PacManager.-get3(PacManager.this));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/PacManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */