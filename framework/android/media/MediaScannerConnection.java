package android.media;

import android.content.ComponentName;
import android.content.ContentDebugUtils;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import java.util.Arrays;

public class MediaScannerConnection
  implements ServiceConnection
{
  private static final boolean DBG = Build.DEBUG_ONEPLUS | DBG_ALL;
  private static final boolean DBG_ALL = ContentDebugUtils.DBG_ALL;
  private static final boolean DBG_DUMP_STACK = ContentDebugUtils.DBG_DUMP_STACK;
  private static final boolean SAVE_DBG_MSG = ContentDebugUtils.SAVE_DBG_MSG;
  private static final String TAG = "MediaScannerConnection";
  private MediaScannerConnectionClient mClient;
  private boolean mConnected;
  private Context mContext;
  private final IMediaScannerListener.Stub mListener = new IMediaScannerListener.Stub()
  {
    public void scanCompleted(String paramAnonymousString, Uri paramAnonymousUri)
    {
      MediaScannerConnection.MediaScannerConnectionClient localMediaScannerConnectionClient = MediaScannerConnection.-get1(MediaScannerConnection.this);
      if ((localMediaScannerConnectionClient != null) && (paramAnonymousUri != null)) {
        localMediaScannerConnectionClient.onScanCompleted(paramAnonymousString, paramAnonymousUri);
      }
      while (!MediaScannerConnection.-get0()) {
        return;
      }
      Log.w("MediaScannerConnection", "scanCompleted: uri = " + paramAnonymousUri);
    }
  };
  private String mPackageName = null;
  private IMediaScannerService mService;
  
  public MediaScannerConnection(Context paramContext, MediaScannerConnectionClient paramMediaScannerConnectionClient)
  {
    this.mContext = paramContext;
    this.mClient = paramMediaScannerConnectionClient;
    paramContext = (Context)localObject;
    try
    {
      if (this.mContext != null) {
        paramContext = this.mContext.getPackageName();
      }
      this.mPackageName = paramContext;
      return;
    }
    catch (Exception paramContext) {}finally
    {
      if ((this.mPackageName == null) && (DBG)) {
        Log.i("MediaScannerConnection", "MediaScannerConnection init:", new Throwable());
      }
    }
  }
  
  public static void scanFile(Context paramContext, String[] paramArrayOfString1, String[] paramArrayOfString2, OnScanCompletedListener paramOnScanCompletedListener)
  {
    if ((DBG) && (paramArrayOfString1 != null) && (paramContext != null)) {
      Log.d("MediaScannerConnection", "scanFile: package[" + paramContext.getPackageName() + "], paths = " + Arrays.toString(paramArrayOfString1));
    }
    paramArrayOfString1 = new ClientProxy(paramArrayOfString1, paramArrayOfString2, paramOnScanCompletedListener);
    paramContext = new MediaScannerConnection(paramContext, paramArrayOfString1);
    paramArrayOfString1.mConnection = paramContext;
    paramContext.connect();
  }
  
  public void connect()
  {
    try
    {
      if (!this.mConnected)
      {
        if (DBG) {
          Log.v("MediaScannerConnection", "Connecting to Media Scanner from [" + this.mPackageName + "].");
        }
        Intent localIntent = new Intent(IMediaScannerService.class.getName());
        localIntent.setComponent(new ComponentName("com.android.providers.media", "com.android.providers.media.MediaScannerService"));
        this.mContext.bindService(localIntent, this, 1);
        this.mConnected = true;
      }
      return;
    }
    finally {}
  }
  
  public void disconnect()
  {
    try
    {
      if (this.mConnected) {
        if (DBG) {
          Log.v("MediaScannerConnection", "Disconnecting from Media Scanner with [" + this.mPackageName + "].");
        }
      }
      try
      {
        this.mContext.unbindService(this);
        if ((this.mClient instanceof ClientProxy)) {
          this.mClient = null;
        }
        this.mService = null;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        for (;;)
        {
          if (DBG) {
            Log.v("MediaScannerConnection", "disconnect failed: " + localIllegalArgumentException);
          }
        }
      }
      this.mConnected = false;
      return;
    }
    finally {}
  }
  
  /* Error */
  public boolean isConnected()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 181	android/media/MediaScannerConnection:mService	Landroid/media/IMediaScannerService;
    //   6: ifnull +12 -> 18
    //   9: aload_0
    //   10: getfield 134	android/media/MediaScannerConnection:mConnected	Z
    //   13: istore_1
    //   14: aload_0
    //   15: monitorexit
    //   16: iload_1
    //   17: ireturn
    //   18: iconst_0
    //   19: istore_1
    //   20: goto -6 -> 14
    //   23: astore_2
    //   24: aload_0
    //   25: monitorexit
    //   26: aload_2
    //   27: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	MediaScannerConnection
    //   13	7	1	bool	boolean
    //   23	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	14	23	finally
  }
  
  public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    if (DBG) {
      Log.v("MediaScannerConnection", "Connected to Media Scanner: " + paramComponentName.toShortString());
    }
    try
    {
      this.mService = IMediaScannerService.Stub.asInterface(paramIBinder);
      if ((this.mService != null) && (this.mClient != null)) {
        this.mClient.onMediaScannerConnected();
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void onServiceDisconnected(ComponentName paramComponentName)
  {
    if (DBG) {
      Log.v("MediaScannerConnection", "Disconnected from Media Scanner: " + paramComponentName.toShortString());
    }
    try
    {
      this.mService = null;
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  /* Error */
  public void scanFile(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 181	android/media/MediaScannerConnection:mService	Landroid/media/IMediaScannerService;
    //   6: ifnull +160 -> 166
    //   9: aload_0
    //   10: getfield 134	android/media/MediaScannerConnection:mConnected	Z
    //   13: istore_3
    //   14: iload_3
    //   15: ifeq +151 -> 166
    //   18: getstatic 40	android/media/MediaScannerConnection:DBG	Z
    //   21: ifeq +28 -> 49
    //   24: ldc 26
    //   26: new 99	java/lang/StringBuilder
    //   29: dup
    //   30: invokespecial 100	java/lang/StringBuilder:<init>	()V
    //   33: ldc -45
    //   35: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: aload_1
    //   39: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: invokevirtual 116	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   45: invokestatic 141	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   48: pop
    //   49: getstatic 63	android/media/MediaScannerConnection:SAVE_DBG_MSG	Z
    //   52: ifeq +31 -> 83
    //   55: aload_1
    //   56: ifnull +27 -> 83
    //   59: aload_1
    //   60: invokevirtual 216	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   63: ldc -38
    //   65: invokevirtual 222	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   68: ifne +21 -> 89
    //   71: aload_1
    //   72: invokevirtual 216	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   75: ldc -32
    //   77: invokevirtual 227	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   80: ifeq +9 -> 89
    //   83: getstatic 52	android/media/MediaScannerConnection:DBG_ALL	Z
    //   86: ifeq +19 -> 105
    //   89: aload_0
    //   90: getfield 78	android/media/MediaScannerConnection:mContext	Landroid/content/Context;
    //   93: ldc 26
    //   95: ldc -27
    //   97: aload_1
    //   98: aload_0
    //   99: getfield 71	android/media/MediaScannerConnection:mPackageName	Ljava/lang/String;
    //   102: invokestatic 233	android/content/ContentDebugUtils:saveDbgMsg	(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   105: aload_1
    //   106: ifnull +15 -> 121
    //   109: aload_1
    //   110: invokevirtual 216	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   113: ldc -21
    //   115: invokevirtual 227	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   118: ifeq +63 -> 181
    //   121: ldc 26
    //   123: new 99	java/lang/StringBuilder
    //   126: dup
    //   127: invokespecial 100	java/lang/StringBuilder:<init>	()V
    //   130: ldc -19
    //   132: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: aload_1
    //   136: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: ldc -17
    //   141: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   144: aload_0
    //   145: getfield 71	android/media/MediaScannerConnection:mPackageName	Ljava/lang/String;
    //   148: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: ldc -118
    //   153: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   156: invokevirtual 116	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   159: invokestatic 242	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   162: pop
    //   163: aload_0
    //   164: monitorexit
    //   165: return
    //   166: new 244	java/lang/IllegalStateException
    //   169: dup
    //   170: ldc -10
    //   172: invokespecial 247	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   175: athrow
    //   176: astore_1
    //   177: aload_0
    //   178: monitorexit
    //   179: aload_1
    //   180: athrow
    //   181: aload_0
    //   182: getfield 181	android/media/MediaScannerConnection:mService	Landroid/media/IMediaScannerService;
    //   185: aload_1
    //   186: aload_2
    //   187: aload_0
    //   188: getfield 76	android/media/MediaScannerConnection:mListener	Landroid/media/IMediaScannerListener$Stub;
    //   191: invokeinterface 251 4 0
    //   196: aload_0
    //   197: monitorexit
    //   198: return
    //   199: astore_2
    //   200: getstatic 40	android/media/MediaScannerConnection:DBG	Z
    //   203: ifeq -7 -> 196
    //   206: ldc 26
    //   208: new 99	java/lang/StringBuilder
    //   211: dup
    //   212: invokespecial 100	java/lang/StringBuilder:<init>	()V
    //   215: ldc -3
    //   217: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   220: aload_1
    //   221: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   224: invokevirtual 116	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   227: invokestatic 120	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   230: pop
    //   231: goto -35 -> 196
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	234	0	this	MediaScannerConnection
    //   0	234	1	paramString1	String
    //   0	234	2	paramString2	String
    //   13	2	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	14	176	finally
    //   18	49	176	finally
    //   49	55	176	finally
    //   59	83	176	finally
    //   83	89	176	finally
    //   89	105	176	finally
    //   109	121	176	finally
    //   121	163	176	finally
    //   166	176	176	finally
    //   181	196	176	finally
    //   200	231	176	finally
    //   18	49	199	android/os/RemoteException
    //   49	55	199	android/os/RemoteException
    //   59	83	199	android/os/RemoteException
    //   83	89	199	android/os/RemoteException
    //   89	105	199	android/os/RemoteException
    //   109	121	199	android/os/RemoteException
    //   121	163	199	android/os/RemoteException
    //   181	196	199	android/os/RemoteException
  }
  
  static class ClientProxy
    implements MediaScannerConnection.MediaScannerConnectionClient
  {
    final MediaScannerConnection.OnScanCompletedListener mClient;
    MediaScannerConnection mConnection;
    final String[] mMimeTypes;
    int mNextPath;
    final String[] mPaths;
    
    ClientProxy(String[] paramArrayOfString1, String[] paramArrayOfString2, MediaScannerConnection.OnScanCompletedListener paramOnScanCompletedListener)
    {
      this.mPaths = paramArrayOfString1;
      this.mMimeTypes = paramArrayOfString2;
      this.mClient = paramOnScanCompletedListener;
    }
    
    public void onMediaScannerConnected()
    {
      scanNextPath();
    }
    
    public void onScanCompleted(String paramString, Uri paramUri)
    {
      if (this.mClient != null) {
        this.mClient.onScanCompleted(paramString, paramUri);
      }
      scanNextPath();
    }
    
    void scanNextPath()
    {
      if (this.mNextPath >= this.mPaths.length)
      {
        this.mConnection.disconnect();
        this.mConnection = null;
        return;
      }
      if (this.mMimeTypes != null) {}
      for (String str = this.mMimeTypes[this.mNextPath];; str = null)
      {
        this.mConnection.scanFile(this.mPaths[this.mNextPath], str);
        this.mNextPath += 1;
        return;
      }
    }
  }
  
  public static abstract interface MediaScannerConnectionClient
    extends MediaScannerConnection.OnScanCompletedListener
  {
    public abstract void onMediaScannerConnected();
    
    public abstract void onScanCompleted(String paramString, Uri paramUri);
  }
  
  public static abstract interface OnScanCompletedListener
  {
    public abstract void onScanCompleted(String paramString, Uri paramUri);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaScannerConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */