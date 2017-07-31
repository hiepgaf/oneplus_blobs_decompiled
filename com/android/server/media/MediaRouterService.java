package com.android.server.media;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.IMediaRouterClient;
import android.media.IMediaRouterService.Stub;
import android.media.MediaRouterClientState;
import android.media.MediaRouterClientState.RouteInfo;
import android.media.RemoteDisplayState;
import android.media.RemoteDisplayState.RemoteDisplayInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.server.Watchdog;
import com.android.server.Watchdog.Monitor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class MediaRouterService
  extends IMediaRouterService.Stub
  implements Watchdog.Monitor
{
  static final long CONNECTED_TIMEOUT = 60000L;
  static final long CONNECTING_TIMEOUT = 5000L;
  private static final boolean DEBUG = Log.isLoggable("MediaRouterService", 3);
  private static final String TAG = "MediaRouterService";
  private final ArrayMap<IBinder, ClientRecord> mAllClientRecords = new ArrayMap();
  private final Context mContext;
  private int mCurrentUserId = -1;
  private final Object mLock = new Object();
  private final SparseArray<UserRecord> mUserRecords = new SparseArray();
  
  public MediaRouterService(Context paramContext)
  {
    this.mContext = paramContext;
    Watchdog.getInstance().addMonitor(this);
  }
  
  private void disposeClientLocked(ClientRecord paramClientRecord, boolean paramBoolean)
  {
    if (DEBUG)
    {
      if (!paramBoolean) {
        break label66;
      }
      Slog.d("MediaRouterService", paramClientRecord + ": Died!");
    }
    for (;;)
    {
      if ((paramClientRecord.mRouteTypes != 0) || (paramClientRecord.mActiveScan)) {
        paramClientRecord.mUserRecord.mHandler.sendEmptyMessage(3);
      }
      paramClientRecord.dispose();
      return;
      label66:
      Slog.d("MediaRouterService", paramClientRecord + ": Unregistered");
    }
  }
  
  private void disposeUserIfNeededLocked(UserRecord paramUserRecord)
  {
    if ((paramUserRecord.mUserId != this.mCurrentUserId) && (paramUserRecord.mClientRecords.isEmpty()))
    {
      if (DEBUG) {
        Slog.d("MediaRouterService", paramUserRecord + ": Disposed");
      }
      this.mUserRecords.remove(paramUserRecord.mUserId);
    }
  }
  
  private MediaRouterClientState getStateLocked(IMediaRouterClient paramIMediaRouterClient)
  {
    paramIMediaRouterClient = (ClientRecord)this.mAllClientRecords.get(paramIMediaRouterClient.asBinder());
    if (paramIMediaRouterClient != null) {
      return paramIMediaRouterClient.getState();
    }
    return null;
  }
  
  private void initializeClientLocked(ClientRecord paramClientRecord)
  {
    if (DEBUG) {
      Slog.d("MediaRouterService", paramClientRecord + ": Registered");
    }
  }
  
  private void initializeUserLocked(UserRecord paramUserRecord)
  {
    if (DEBUG) {
      Slog.d("MediaRouterService", paramUserRecord + ": Initialized");
    }
    if (paramUserRecord.mUserId == this.mCurrentUserId) {
      paramUserRecord.mHandler.sendEmptyMessage(1);
    }
  }
  
  private void registerClientLocked(IMediaRouterClient paramIMediaRouterClient, int paramInt1, String paramString, int paramInt2, boolean paramBoolean)
  {
    IBinder localIBinder = paramIMediaRouterClient.asBinder();
    int i;
    UserRecord localUserRecord1;
    if ((ClientRecord)this.mAllClientRecords.get(localIBinder) == null)
    {
      i = 0;
      UserRecord localUserRecord2 = (UserRecord)this.mUserRecords.get(paramInt2);
      localUserRecord1 = localUserRecord2;
      if (localUserRecord2 == null)
      {
        localUserRecord1 = new UserRecord(paramInt2);
        i = 1;
      }
      paramIMediaRouterClient = new ClientRecord(localUserRecord1, paramIMediaRouterClient, paramInt1, paramString, paramBoolean);
    }
    try
    {
      localIBinder.linkToDeath(paramIMediaRouterClient, 0);
      if (i != 0)
      {
        this.mUserRecords.put(paramInt2, localUserRecord1);
        initializeUserLocked(localUserRecord1);
      }
      localUserRecord1.mClientRecords.add(paramIMediaRouterClient);
      this.mAllClientRecords.put(localIBinder, paramIMediaRouterClient);
      initializeClientLocked(paramIMediaRouterClient);
      return;
    }
    catch (RemoteException paramIMediaRouterClient)
    {
      throw new RuntimeException("Media router client died prematurely.", paramIMediaRouterClient);
    }
  }
  
  private void requestSetVolumeLocked(IMediaRouterClient paramIMediaRouterClient, String paramString, int paramInt)
  {
    paramIMediaRouterClient = paramIMediaRouterClient.asBinder();
    paramIMediaRouterClient = (ClientRecord)this.mAllClientRecords.get(paramIMediaRouterClient);
    if (paramIMediaRouterClient != null) {
      paramIMediaRouterClient.mUserRecord.mHandler.obtainMessage(6, paramInt, 0, paramString).sendToTarget();
    }
  }
  
  private void requestUpdateVolumeLocked(IMediaRouterClient paramIMediaRouterClient, String paramString, int paramInt)
  {
    paramIMediaRouterClient = paramIMediaRouterClient.asBinder();
    paramIMediaRouterClient = (ClientRecord)this.mAllClientRecords.get(paramIMediaRouterClient);
    if (paramIMediaRouterClient != null) {
      paramIMediaRouterClient.mUserRecord.mHandler.obtainMessage(7, paramInt, 0, paramString).sendToTarget();
    }
  }
  
  private void setDiscoveryRequestLocked(IMediaRouterClient paramIMediaRouterClient, int paramInt, boolean paramBoolean)
  {
    paramIMediaRouterClient = paramIMediaRouterClient.asBinder();
    paramIMediaRouterClient = (ClientRecord)this.mAllClientRecords.get(paramIMediaRouterClient);
    if (paramIMediaRouterClient != null)
    {
      int i = paramInt;
      if (!paramIMediaRouterClient.mTrusted) {
        i = paramInt & 0xFFFFFFFB;
      }
      if ((paramIMediaRouterClient.mRouteTypes != i) || (paramIMediaRouterClient.mActiveScan != paramBoolean))
      {
        if (DEBUG) {
          Slog.d("MediaRouterService", paramIMediaRouterClient + ": Set discovery request, routeTypes=0x" + Integer.toHexString(i) + ", activeScan=" + paramBoolean);
        }
        paramIMediaRouterClient.mRouteTypes = i;
        paramIMediaRouterClient.mActiveScan = paramBoolean;
        paramIMediaRouterClient.mUserRecord.mHandler.sendEmptyMessage(3);
      }
    }
  }
  
  private void setSelectedRouteLocked(IMediaRouterClient paramIMediaRouterClient, String paramString, boolean paramBoolean)
  {
    paramIMediaRouterClient = (ClientRecord)this.mAllClientRecords.get(paramIMediaRouterClient.asBinder());
    if (paramIMediaRouterClient != null)
    {
      String str = paramIMediaRouterClient.mSelectedRouteId;
      if (!Objects.equals(paramString, str))
      {
        if (DEBUG) {
          Slog.d("MediaRouterService", paramIMediaRouterClient + ": Set selected route, routeId=" + paramString + ", oldRouteId=" + str + ", explicit=" + paramBoolean);
        }
        paramIMediaRouterClient.mSelectedRouteId = paramString;
        if (paramBoolean)
        {
          if (str != null) {
            paramIMediaRouterClient.mUserRecord.mHandler.obtainMessage(5, str).sendToTarget();
          }
          if ((paramString != null) && (paramIMediaRouterClient.mTrusted)) {
            paramIMediaRouterClient.mUserRecord.mHandler.obtainMessage(4, paramString).sendToTarget();
          }
        }
      }
    }
  }
  
  private void unregisterClientLocked(IMediaRouterClient paramIMediaRouterClient, boolean paramBoolean)
  {
    paramIMediaRouterClient = (ClientRecord)this.mAllClientRecords.remove(paramIMediaRouterClient.asBinder());
    if (paramIMediaRouterClient != null)
    {
      UserRecord localUserRecord = paramIMediaRouterClient.mUserRecord;
      localUserRecord.mClientRecords.remove(paramIMediaRouterClient);
      disposeClientLocked(paramIMediaRouterClient, paramBoolean);
      disposeUserIfNeededLocked(localUserRecord);
    }
  }
  
  private boolean validatePackageName(int paramInt, String paramString)
  {
    if (paramString != null)
    {
      String[] arrayOfString = this.mContext.getPackageManager().getPackagesForUid(paramInt);
      if (arrayOfString != null)
      {
        int i = arrayOfString.length;
        paramInt = 0;
        while (paramInt < i)
        {
          if (arrayOfString[paramInt].equals(paramString)) {
            return true;
          }
          paramInt += 1;
        }
      }
    }
    return false;
  }
  
  void clientDied(ClientRecord paramClientRecord)
  {
    synchronized (this.mLock)
    {
      unregisterClientLocked(paramClientRecord.mClient, true);
      return;
    }
  }
  
  public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump MediaRouterService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    paramPrintWriter.println("MEDIA ROUTER SERVICE (dumpsys media_router)");
    paramPrintWriter.println();
    paramPrintWriter.println("Global state");
    paramPrintWriter.println("  mCurrentUserId=" + this.mCurrentUserId);
    synchronized (this.mLock)
    {
      int j = this.mUserRecords.size();
      int i = 0;
      while (i < j)
      {
        paramArrayOfString = (UserRecord)this.mUserRecords.valueAt(i);
        paramPrintWriter.println();
        paramArrayOfString.dump(paramPrintWriter, "");
        i += 1;
      }
      return;
    }
  }
  
  /* Error */
  public MediaRouterClientState getState(IMediaRouterClient paramIMediaRouterClient)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 366	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 368
    //   11: invokespecial 370	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: invokestatic 374	android/os/Binder:clearCallingIdentity	()J
    //   18: lstore_2
    //   19: aload_0
    //   20: getfield 59	com/android/server/media/MediaRouterService:mLock	Ljava/lang/Object;
    //   23: astore 4
    //   25: aload 4
    //   27: monitorenter
    //   28: aload_0
    //   29: aload_1
    //   30: invokespecial 376	com/android/server/media/MediaRouterService:getStateLocked	(Landroid/media/IMediaRouterClient;)Landroid/media/MediaRouterClientState;
    //   33: astore_1
    //   34: aload 4
    //   36: monitorexit
    //   37: lload_2
    //   38: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   41: aload_1
    //   42: areturn
    //   43: astore_1
    //   44: aload 4
    //   46: monitorexit
    //   47: aload_1
    //   48: athrow
    //   49: astore_1
    //   50: lload_2
    //   51: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   54: aload_1
    //   55: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	56	0	this	MediaRouterService
    //   0	56	1	paramIMediaRouterClient	IMediaRouterClient
    //   18	33	2	l	long
    // Exception table:
    //   from	to	target	type
    //   28	34	43	finally
    //   19	28	49	finally
    //   34	37	49	finally
    //   44	49	49	finally
  }
  
  public void monitor()
  {
    Object localObject = this.mLock;
  }
  
  /* Error */
  public void registerClientAsUser(IMediaRouterClient paramIMediaRouterClient, String paramString, int paramInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 366	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 368
    //   11: invokespecial 370	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: invokestatic 339	android/os/Binder:getCallingUid	()I
    //   18: istore 5
    //   20: aload_0
    //   21: iload 5
    //   23: aload_2
    //   24: invokespecial 384	com/android/server/media/MediaRouterService:validatePackageName	(ILjava/lang/String;)Z
    //   27: ifne +14 -> 41
    //   30: new 386	java/lang/SecurityException
    //   33: dup
    //   34: ldc_w 388
    //   37: invokespecial 389	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   40: athrow
    //   41: invokestatic 331	android/os/Binder:getCallingPid	()I
    //   44: istore 4
    //   46: iload 4
    //   48: iload 5
    //   50: iload_3
    //   51: iconst_0
    //   52: iconst_1
    //   53: ldc_w 390
    //   56: aload_2
    //   57: invokestatic 396	android/app/ActivityManager:handleIncomingUser	(IIIZZLjava/lang/String;Ljava/lang/String;)I
    //   60: istore_3
    //   61: aload_0
    //   62: getfield 54	com/android/server/media/MediaRouterService:mContext	Landroid/content/Context;
    //   65: ldc_w 398
    //   68: invokevirtual 323	android/content/Context:checkCallingOrSelfPermission	(Ljava/lang/String;)I
    //   71: ifne +40 -> 111
    //   74: iconst_1
    //   75: istore 6
    //   77: invokestatic 374	android/os/Binder:clearCallingIdentity	()J
    //   80: lstore 7
    //   82: aload_0
    //   83: getfield 59	com/android/server/media/MediaRouterService:mLock	Ljava/lang/Object;
    //   86: astore 9
    //   88: aload 9
    //   90: monitorenter
    //   91: aload_0
    //   92: aload_1
    //   93: iload 4
    //   95: aload_2
    //   96: iload_3
    //   97: iload 6
    //   99: invokespecial 400	com/android/server/media/MediaRouterService:registerClientLocked	(Landroid/media/IMediaRouterClient;ILjava/lang/String;IZ)V
    //   102: aload 9
    //   104: monitorexit
    //   105: lload 7
    //   107: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   110: return
    //   111: iconst_0
    //   112: istore 6
    //   114: goto -37 -> 77
    //   117: astore_1
    //   118: aload 9
    //   120: monitorexit
    //   121: aload_1
    //   122: athrow
    //   123: astore_1
    //   124: lload 7
    //   126: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   129: aload_1
    //   130: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	131	0	this	MediaRouterService
    //   0	131	1	paramIMediaRouterClient	IMediaRouterClient
    //   0	131	2	paramString	String
    //   0	131	3	paramInt	int
    //   44	50	4	i	int
    //   18	31	5	j	int
    //   75	38	6	bool	boolean
    //   80	45	7	l	long
    // Exception table:
    //   from	to	target	type
    //   91	102	117	finally
    //   82	91	123	finally
    //   102	105	123	finally
    //   118	123	123	finally
  }
  
  /* Error */
  public void requestSetVolume(IMediaRouterClient paramIMediaRouterClient, String paramString, int paramInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 366	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 368
    //   11: invokespecial 370	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aload_2
    //   16: ifnonnull +14 -> 30
    //   19: new 366	java/lang/IllegalArgumentException
    //   22: dup
    //   23: ldc_w 403
    //   26: invokespecial 370	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   29: athrow
    //   30: invokestatic 374	android/os/Binder:clearCallingIdentity	()J
    //   33: lstore 4
    //   35: aload_0
    //   36: getfield 59	com/android/server/media/MediaRouterService:mLock	Ljava/lang/Object;
    //   39: astore 6
    //   41: aload 6
    //   43: monitorenter
    //   44: aload_0
    //   45: aload_1
    //   46: aload_2
    //   47: iload_3
    //   48: invokespecial 405	com/android/server/media/MediaRouterService:requestSetVolumeLocked	(Landroid/media/IMediaRouterClient;Ljava/lang/String;I)V
    //   51: aload 6
    //   53: monitorexit
    //   54: lload 4
    //   56: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   59: return
    //   60: astore_1
    //   61: aload 6
    //   63: monitorexit
    //   64: aload_1
    //   65: athrow
    //   66: astore_1
    //   67: lload 4
    //   69: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   72: aload_1
    //   73: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	this	MediaRouterService
    //   0	74	1	paramIMediaRouterClient	IMediaRouterClient
    //   0	74	2	paramString	String
    //   0	74	3	paramInt	int
    //   33	35	4	l	long
    // Exception table:
    //   from	to	target	type
    //   44	51	60	finally
    //   35	44	66	finally
    //   51	54	66	finally
    //   61	66	66	finally
  }
  
  /* Error */
  public void requestUpdateVolume(IMediaRouterClient paramIMediaRouterClient, String paramString, int paramInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 366	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 368
    //   11: invokespecial 370	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aload_2
    //   16: ifnonnull +14 -> 30
    //   19: new 366	java/lang/IllegalArgumentException
    //   22: dup
    //   23: ldc_w 403
    //   26: invokespecial 370	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   29: athrow
    //   30: invokestatic 374	android/os/Binder:clearCallingIdentity	()J
    //   33: lstore 4
    //   35: aload_0
    //   36: getfield 59	com/android/server/media/MediaRouterService:mLock	Ljava/lang/Object;
    //   39: astore 6
    //   41: aload 6
    //   43: monitorenter
    //   44: aload_0
    //   45: aload_1
    //   46: aload_2
    //   47: iload_3
    //   48: invokespecial 408	com/android/server/media/MediaRouterService:requestUpdateVolumeLocked	(Landroid/media/IMediaRouterClient;Ljava/lang/String;I)V
    //   51: aload 6
    //   53: monitorexit
    //   54: lload 4
    //   56: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   59: return
    //   60: astore_1
    //   61: aload 6
    //   63: monitorexit
    //   64: aload_1
    //   65: athrow
    //   66: astore_1
    //   67: lload 4
    //   69: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   72: aload_1
    //   73: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	this	MediaRouterService
    //   0	74	1	paramIMediaRouterClient	IMediaRouterClient
    //   0	74	2	paramString	String
    //   0	74	3	paramInt	int
    //   33	35	4	l	long
    // Exception table:
    //   from	to	target	type
    //   44	51	60	finally
    //   35	44	66	finally
    //   51	54	66	finally
    //   61	66	66	finally
  }
  
  /* Error */
  public void setDiscoveryRequest(IMediaRouterClient paramIMediaRouterClient, int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 366	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 368
    //   11: invokespecial 370	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: invokestatic 374	android/os/Binder:clearCallingIdentity	()J
    //   18: lstore 4
    //   20: aload_0
    //   21: getfield 59	com/android/server/media/MediaRouterService:mLock	Ljava/lang/Object;
    //   24: astore 6
    //   26: aload 6
    //   28: monitorenter
    //   29: aload_0
    //   30: aload_1
    //   31: iload_2
    //   32: iload_3
    //   33: invokespecial 411	com/android/server/media/MediaRouterService:setDiscoveryRequestLocked	(Landroid/media/IMediaRouterClient;IZ)V
    //   36: aload 6
    //   38: monitorexit
    //   39: lload 4
    //   41: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   44: return
    //   45: astore_1
    //   46: aload 6
    //   48: monitorexit
    //   49: aload_1
    //   50: athrow
    //   51: astore_1
    //   52: lload 4
    //   54: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   57: aload_1
    //   58: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	59	0	this	MediaRouterService
    //   0	59	1	paramIMediaRouterClient	IMediaRouterClient
    //   0	59	2	paramInt	int
    //   0	59	3	paramBoolean	boolean
    //   18	35	4	l	long
    // Exception table:
    //   from	to	target	type
    //   29	36	45	finally
    //   20	29	51	finally
    //   36	39	51	finally
    //   46	51	51	finally
  }
  
  /* Error */
  public void setSelectedRoute(IMediaRouterClient paramIMediaRouterClient, String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 366	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 368
    //   11: invokespecial 370	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: invokestatic 374	android/os/Binder:clearCallingIdentity	()J
    //   18: lstore 4
    //   20: aload_0
    //   21: getfield 59	com/android/server/media/MediaRouterService:mLock	Ljava/lang/Object;
    //   24: astore 6
    //   26: aload 6
    //   28: monitorenter
    //   29: aload_0
    //   30: aload_1
    //   31: aload_2
    //   32: iload_3
    //   33: invokespecial 414	com/android/server/media/MediaRouterService:setSelectedRouteLocked	(Landroid/media/IMediaRouterClient;Ljava/lang/String;Z)V
    //   36: aload 6
    //   38: monitorexit
    //   39: lload 4
    //   41: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   44: return
    //   45: astore_1
    //   46: aload 6
    //   48: monitorexit
    //   49: aload_1
    //   50: athrow
    //   51: astore_1
    //   52: lload 4
    //   54: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   57: aload_1
    //   58: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	59	0	this	MediaRouterService
    //   0	59	1	paramIMediaRouterClient	IMediaRouterClient
    //   0	59	2	paramString	String
    //   0	59	3	paramBoolean	boolean
    //   18	35	4	l	long
    // Exception table:
    //   from	to	target	type
    //   29	36	45	finally
    //   20	29	51	finally
    //   36	39	51	finally
    //   46	51	51	finally
  }
  
  void switchUser()
  {
    synchronized (this.mLock)
    {
      int i = ActivityManager.getCurrentUser();
      if (this.mCurrentUserId != i)
      {
        int j = this.mCurrentUserId;
        this.mCurrentUserId = i;
        UserRecord localUserRecord = (UserRecord)this.mUserRecords.get(j);
        if (localUserRecord != null)
        {
          localUserRecord.mHandler.sendEmptyMessage(2);
          disposeUserIfNeededLocked(localUserRecord);
        }
        localUserRecord = (UserRecord)this.mUserRecords.get(i);
        if (localUserRecord != null) {
          localUserRecord.mHandler.sendEmptyMessage(1);
        }
      }
      return;
    }
  }
  
  public void systemRunning()
  {
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.USER_SWITCHED");
    this.mContext.registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (paramAnonymousIntent.getAction().equals("android.intent.action.USER_SWITCHED")) {
          MediaRouterService.this.switchUser();
        }
      }
    }, localIntentFilter);
    switchUser();
  }
  
  /* Error */
  public void unregisterClient(IMediaRouterClient paramIMediaRouterClient)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: new 366	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc_w 368
    //   11: invokespecial 370	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: invokestatic 374	android/os/Binder:clearCallingIdentity	()J
    //   18: lstore_2
    //   19: aload_0
    //   20: getfield 59	com/android/server/media/MediaRouterService:mLock	Ljava/lang/Object;
    //   23: astore 4
    //   25: aload 4
    //   27: monitorenter
    //   28: aload_0
    //   29: aload_1
    //   30: iconst_0
    //   31: invokespecial 315	com/android/server/media/MediaRouterService:unregisterClientLocked	(Landroid/media/IMediaRouterClient;Z)V
    //   34: aload 4
    //   36: monitorexit
    //   37: lload_2
    //   38: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   41: return
    //   42: astore_1
    //   43: aload 4
    //   45: monitorexit
    //   46: aload_1
    //   47: athrow
    //   48: astore_1
    //   49: lload_2
    //   50: invokestatic 380	android/os/Binder:restoreCallingIdentity	(J)V
    //   53: aload_1
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	MediaRouterService
    //   0	55	1	paramIMediaRouterClient	IMediaRouterClient
    //   18	32	2	l	long
    // Exception table:
    //   from	to	target	type
    //   28	34	42	finally
    //   19	28	48	finally
    //   34	37	48	finally
    //   43	48	48	finally
  }
  
  final class ClientRecord
    implements IBinder.DeathRecipient
  {
    public boolean mActiveScan;
    public final IMediaRouterClient mClient;
    public final String mPackageName;
    public final int mPid;
    public int mRouteTypes;
    public String mSelectedRouteId;
    public final boolean mTrusted;
    public final MediaRouterService.UserRecord mUserRecord;
    
    public ClientRecord(MediaRouterService.UserRecord paramUserRecord, IMediaRouterClient paramIMediaRouterClient, int paramInt, String paramString, boolean paramBoolean)
    {
      this.mUserRecord = paramUserRecord;
      this.mClient = paramIMediaRouterClient;
      this.mPid = paramInt;
      this.mPackageName = paramString;
      this.mTrusted = paramBoolean;
    }
    
    public void binderDied()
    {
      MediaRouterService.this.clientDied(this);
    }
    
    public void dispose()
    {
      this.mClient.asBinder().unlinkToDeath(this, 0);
    }
    
    public void dump(PrintWriter paramPrintWriter, String paramString)
    {
      paramPrintWriter.println(paramString + this);
      paramString = paramString + "  ";
      paramPrintWriter.println(paramString + "mTrusted=" + this.mTrusted);
      paramPrintWriter.println(paramString + "mRouteTypes=0x" + Integer.toHexString(this.mRouteTypes));
      paramPrintWriter.println(paramString + "mActiveScan=" + this.mActiveScan);
      paramPrintWriter.println(paramString + "mSelectedRouteId=" + this.mSelectedRouteId);
    }
    
    MediaRouterClientState getState()
    {
      if (this.mTrusted) {
        return this.mUserRecord.mTrustedState;
      }
      return this.mUserRecord.mUntrustedState;
    }
    
    public String toString()
    {
      return "Client " + this.mPackageName + " (pid " + this.mPid + ")";
    }
  }
  
  static final class UserHandler
    extends Handler
    implements RemoteDisplayProviderWatcher.Callback, RemoteDisplayProviderProxy.Callback
  {
    private static final int MSG_CONNECTION_TIMED_OUT = 9;
    public static final int MSG_REQUEST_SET_VOLUME = 6;
    public static final int MSG_REQUEST_UPDATE_VOLUME = 7;
    public static final int MSG_SELECT_ROUTE = 4;
    public static final int MSG_START = 1;
    public static final int MSG_STOP = 2;
    public static final int MSG_UNSELECT_ROUTE = 5;
    private static final int MSG_UPDATE_CLIENT_STATE = 8;
    public static final int MSG_UPDATE_DISCOVERY_REQUEST = 3;
    private static final int PHASE_CONNECTED = 2;
    private static final int PHASE_CONNECTING = 1;
    private static final int PHASE_NOT_AVAILABLE = -1;
    private static final int PHASE_NOT_CONNECTED = 0;
    private static final int TIMEOUT_REASON_CONNECTION_LOST = 2;
    private static final int TIMEOUT_REASON_NOT_AVAILABLE = 1;
    private static final int TIMEOUT_REASON_WAITING_FOR_CONNECTED = 4;
    private static final int TIMEOUT_REASON_WAITING_FOR_CONNECTING = 3;
    private boolean mClientStateUpdateScheduled;
    private int mConnectionPhase = -1;
    private int mConnectionTimeoutReason;
    private long mConnectionTimeoutStartTime;
    private int mDiscoveryMode = 0;
    private RouteRecord mGloballySelectedRouteRecord;
    private final ArrayList<ProviderRecord> mProviderRecords = new ArrayList();
    private boolean mRunning;
    private final MediaRouterService mService;
    private final ArrayList<IMediaRouterClient> mTempClients = new ArrayList();
    private final MediaRouterService.UserRecord mUserRecord;
    private final RemoteDisplayProviderWatcher mWatcher;
    
    public UserHandler(MediaRouterService paramMediaRouterService, MediaRouterService.UserRecord paramUserRecord)
    {
      super(null, true);
      this.mService = paramMediaRouterService;
      this.mUserRecord = paramUserRecord;
      this.mWatcher = new RemoteDisplayProviderWatcher(MediaRouterService.-get0(paramMediaRouterService), this, this, this.mUserRecord.mUserId);
    }
    
    private void checkGloballySelectedRouteState()
    {
      if (this.mGloballySelectedRouteRecord == null)
      {
        this.mConnectionPhase = -1;
        updateConnectionTimeout(0);
        return;
      }
      int i;
      if ((this.mGloballySelectedRouteRecord.isValid()) && (this.mGloballySelectedRouteRecord.isEnabled()))
      {
        i = this.mConnectionPhase;
        this.mConnectionPhase = getConnectionPhase(this.mGloballySelectedRouteRecord.getStatus());
        if ((i >= 1) && (this.mConnectionPhase < 1)) {
          updateConnectionTimeout(2);
        }
      }
      else
      {
        updateConnectionTimeout(1);
        return;
      }
      switch (this.mConnectionPhase)
      {
      default: 
        updateConnectionTimeout(1);
        return;
      case 2: 
        if (i != 2) {
          Slog.i("MediaRouterService", "Connected to global route: " + this.mGloballySelectedRouteRecord);
        }
        updateConnectionTimeout(0);
        return;
      case 1: 
        if (i != 1) {
          Slog.i("MediaRouterService", "Connecting to global route: " + this.mGloballySelectedRouteRecord);
        }
        updateConnectionTimeout(4);
        return;
      }
      updateConnectionTimeout(3);
    }
    
    private void connectionTimedOut()
    {
      if ((this.mConnectionTimeoutReason == 0) || (this.mGloballySelectedRouteRecord == null))
      {
        Log.wtf("MediaRouterService", "Handled connection timeout for no reason.");
        return;
      }
      switch (this.mConnectionTimeoutReason)
      {
      }
      for (;;)
      {
        this.mConnectionTimeoutReason = 0;
        unselectGloballySelectedRoute();
        return;
        Slog.i("MediaRouterService", "Global route no longer available: " + this.mGloballySelectedRouteRecord);
        continue;
        Slog.i("MediaRouterService", "Global route connection lost: " + this.mGloballySelectedRouteRecord);
        continue;
        Slog.i("MediaRouterService", "Global route timed out while waiting for connection attempt to begin after " + (SystemClock.uptimeMillis() - this.mConnectionTimeoutStartTime) + " ms: " + this.mGloballySelectedRouteRecord);
        continue;
        Slog.i("MediaRouterService", "Global route timed out while connecting after " + (SystemClock.uptimeMillis() - this.mConnectionTimeoutStartTime) + " ms: " + this.mGloballySelectedRouteRecord);
      }
    }
    
    private int findProviderRecord(RemoteDisplayProviderProxy paramRemoteDisplayProviderProxy)
    {
      int j = this.mProviderRecords.size();
      int i = 0;
      while (i < j)
      {
        if (((ProviderRecord)this.mProviderRecords.get(i)).getProvider() == paramRemoteDisplayProviderProxy) {
          return i;
        }
        i += 1;
      }
      return -1;
    }
    
    private RouteRecord findRouteRecord(String paramString)
    {
      int j = this.mProviderRecords.size();
      int i = 0;
      while (i < j)
      {
        RouteRecord localRouteRecord = ((ProviderRecord)this.mProviderRecords.get(i)).findRouteByUniqueId(paramString);
        if (localRouteRecord != null) {
          return localRouteRecord;
        }
        i += 1;
      }
      return null;
    }
    
    private static int getConnectionPhase(int paramInt)
    {
      switch (paramInt)
      {
      case 4: 
      case 5: 
      default: 
        return -1;
      case 0: 
      case 6: 
        return 2;
      case 2: 
        return 1;
      }
      return 0;
    }
    
    private void requestSetVolume(String paramString, int paramInt)
    {
      if ((this.mGloballySelectedRouteRecord != null) && (paramString.equals(this.mGloballySelectedRouteRecord.getUniqueId()))) {
        this.mGloballySelectedRouteRecord.getProvider().setDisplayVolume(paramInt);
      }
    }
    
    private void requestUpdateVolume(String paramString, int paramInt)
    {
      if ((this.mGloballySelectedRouteRecord != null) && (paramString.equals(this.mGloballySelectedRouteRecord.getUniqueId()))) {
        this.mGloballySelectedRouteRecord.getProvider().adjustDisplayVolume(paramInt);
      }
    }
    
    private void scheduleUpdateClientState()
    {
      if (!this.mClientStateUpdateScheduled)
      {
        this.mClientStateUpdateScheduled = true;
        sendEmptyMessage(8);
      }
    }
    
    private void selectRoute(String paramString)
    {
      if ((paramString == null) || ((this.mGloballySelectedRouteRecord != null) && (paramString.equals(this.mGloballySelectedRouteRecord.getUniqueId())))) {}
      do
      {
        return;
        paramString = findRouteRecord(paramString);
      } while (paramString == null);
      unselectGloballySelectedRoute();
      Slog.i("MediaRouterService", "Selected global route:" + paramString);
      this.mGloballySelectedRouteRecord = paramString;
      checkGloballySelectedRouteState();
      paramString.getProvider().setSelectedDisplay(paramString.getDescriptorId());
      scheduleUpdateClientState();
    }
    
    private void start()
    {
      if (!this.mRunning)
      {
        this.mRunning = true;
        this.mWatcher.start();
      }
    }
    
    private void stop()
    {
      if (this.mRunning)
      {
        this.mRunning = false;
        unselectGloballySelectedRoute();
        this.mWatcher.stop();
      }
    }
    
    private void unselectGloballySelectedRoute()
    {
      if (this.mGloballySelectedRouteRecord != null)
      {
        Slog.i("MediaRouterService", "Unselected global route:" + this.mGloballySelectedRouteRecord);
        this.mGloballySelectedRouteRecord.getProvider().setSelectedDisplay(null);
        this.mGloballySelectedRouteRecord = null;
        checkGloballySelectedRouteState();
        scheduleUpdateClientState();
      }
    }
    
    private void unselectRoute(String paramString)
    {
      if ((paramString != null) && (this.mGloballySelectedRouteRecord != null) && (paramString.equals(this.mGloballySelectedRouteRecord.getUniqueId()))) {
        unselectGloballySelectedRoute();
      }
    }
    
    /* Error */
    private void updateClientState()
    {
      // Byte code:
      //   0: aload_0
      //   1: iconst_0
      //   2: putfield 236	com/android/server/media/MediaRouterService$UserHandler:mClientStateUpdateScheduled	Z
      //   5: aload_0
      //   6: getfield 115	com/android/server/media/MediaRouterService$UserHandler:mGloballySelectedRouteRecord	Lcom/android/server/media/MediaRouterService$UserHandler$RouteRecord;
      //   9: ifnull +64 -> 73
      //   12: aload_0
      //   13: getfield 115	com/android/server/media/MediaRouterService$UserHandler:mGloballySelectedRouteRecord	Lcom/android/server/media/MediaRouterService$UserHandler$RouteRecord;
      //   16: invokevirtual 217	com/android/server/media/MediaRouterService$UserHandler$RouteRecord:getUniqueId	()Ljava/lang/String;
      //   19: astore_3
      //   20: new 272	android/media/MediaRouterClientState
      //   23: dup
      //   24: invokespecial 273	android/media/MediaRouterClientState:<init>	()V
      //   27: astore 4
      //   29: aload 4
      //   31: aload_3
      //   32: putfield 277	android/media/MediaRouterClientState:globallySelectedRouteId	Ljava/lang/String;
      //   35: aload_0
      //   36: getfield 85	com/android/server/media/MediaRouterService$UserHandler:mProviderRecords	Ljava/util/ArrayList;
      //   39: invokevirtual 199	java/util/ArrayList:size	()I
      //   42: istore_2
      //   43: iconst_0
      //   44: istore_1
      //   45: iload_1
      //   46: iload_2
      //   47: if_icmpge +31 -> 78
      //   50: aload_0
      //   51: getfield 85	com/android/server/media/MediaRouterService$UserHandler:mProviderRecords	Ljava/util/ArrayList;
      //   54: iload_1
      //   55: invokevirtual 203	java/util/ArrayList:get	(I)Ljava/lang/Object;
      //   58: checkcast 13	com/android/server/media/MediaRouterService$UserHandler$ProviderRecord
      //   61: aload 4
      //   63: invokevirtual 281	com/android/server/media/MediaRouterService$UserHandler$ProviderRecord:appendClientState	(Landroid/media/MediaRouterClientState;)V
      //   66: iload_1
      //   67: iconst_1
      //   68: iadd
      //   69: istore_1
      //   70: goto -25 -> 45
      //   73: aconst_null
      //   74: astore_3
      //   75: goto -55 -> 20
      //   78: new 272	android/media/MediaRouterClientState
      //   81: dup
      //   82: invokespecial 273	android/media/MediaRouterClientState:<init>	()V
      //   85: astore 5
      //   87: aload 5
      //   89: aload_3
      //   90: putfield 277	android/media/MediaRouterClientState:globallySelectedRouteId	Ljava/lang/String;
      //   93: aload_3
      //   94: ifnull +18 -> 112
      //   97: aload 5
      //   99: getfield 284	android/media/MediaRouterClientState:routes	Ljava/util/ArrayList;
      //   102: aload 4
      //   104: aload_3
      //   105: invokevirtual 288	android/media/MediaRouterClientState:getRoute	(Ljava/lang/String;)Landroid/media/MediaRouterClientState$RouteInfo;
      //   108: invokevirtual 291	java/util/ArrayList:add	(Ljava/lang/Object;)Z
      //   111: pop
      //   112: aload_0
      //   113: getfield 93	com/android/server/media/MediaRouterService$UserHandler:mService	Lcom/android/server/media/MediaRouterService;
      //   116: invokestatic 295	com/android/server/media/MediaRouterService:-get1	(Lcom/android/server/media/MediaRouterService;)Ljava/lang/Object;
      //   119: astore_3
      //   120: aload_3
      //   121: monitorenter
      //   122: aload_0
      //   123: getfield 95	com/android/server/media/MediaRouterService$UserHandler:mUserRecord	Lcom/android/server/media/MediaRouterService$UserRecord;
      //   126: aload 4
      //   128: putfield 299	com/android/server/media/MediaRouterService$UserRecord:mTrustedState	Landroid/media/MediaRouterClientState;
      //   131: aload_0
      //   132: getfield 95	com/android/server/media/MediaRouterService$UserHandler:mUserRecord	Lcom/android/server/media/MediaRouterService$UserRecord;
      //   135: aload 5
      //   137: putfield 302	com/android/server/media/MediaRouterService$UserRecord:mUntrustedState	Landroid/media/MediaRouterClientState;
      //   140: aload_0
      //   141: getfield 95	com/android/server/media/MediaRouterService$UserHandler:mUserRecord	Lcom/android/server/media/MediaRouterService$UserRecord;
      //   144: getfield 305	com/android/server/media/MediaRouterService$UserRecord:mClientRecords	Ljava/util/ArrayList;
      //   147: invokevirtual 199	java/util/ArrayList:size	()I
      //   150: istore_2
      //   151: iconst_0
      //   152: istore_1
      //   153: iload_1
      //   154: iload_2
      //   155: if_icmpge +35 -> 190
      //   158: aload_0
      //   159: getfield 87	com/android/server/media/MediaRouterService$UserHandler:mTempClients	Ljava/util/ArrayList;
      //   162: aload_0
      //   163: getfield 95	com/android/server/media/MediaRouterService$UserHandler:mUserRecord	Lcom/android/server/media/MediaRouterService$UserRecord;
      //   166: getfield 305	com/android/server/media/MediaRouterService$UserRecord:mClientRecords	Ljava/util/ArrayList;
      //   169: iload_1
      //   170: invokevirtual 203	java/util/ArrayList:get	(I)Ljava/lang/Object;
      //   173: checkcast 307	com/android/server/media/MediaRouterService$ClientRecord
      //   176: getfield 311	com/android/server/media/MediaRouterService$ClientRecord:mClient	Landroid/media/IMediaRouterClient;
      //   179: invokevirtual 291	java/util/ArrayList:add	(Ljava/lang/Object;)Z
      //   182: pop
      //   183: iload_1
      //   184: iconst_1
      //   185: iadd
      //   186: istore_1
      //   187: goto -34 -> 153
      //   190: aload_3
      //   191: monitorexit
      //   192: aload_0
      //   193: getfield 87	com/android/server/media/MediaRouterService$UserHandler:mTempClients	Ljava/util/ArrayList;
      //   196: invokevirtual 199	java/util/ArrayList:size	()I
      //   199: istore_2
      //   200: iconst_0
      //   201: istore_1
      //   202: iload_1
      //   203: iload_2
      //   204: if_icmpge +43 -> 247
      //   207: aload_0
      //   208: getfield 87	com/android/server/media/MediaRouterService$UserHandler:mTempClients	Ljava/util/ArrayList;
      //   211: iload_1
      //   212: invokevirtual 203	java/util/ArrayList:get	(I)Ljava/lang/Object;
      //   215: checkcast 313	android/media/IMediaRouterClient
      //   218: invokeinterface 316 1 0
      //   223: iload_1
      //   224: iconst_1
      //   225: iadd
      //   226: istore_1
      //   227: goto -25 -> 202
      //   230: astore 4
      //   232: aload_3
      //   233: monitorexit
      //   234: aload 4
      //   236: athrow
      //   237: astore_3
      //   238: aload_0
      //   239: getfield 87	com/android/server/media/MediaRouterService$UserHandler:mTempClients	Ljava/util/ArrayList;
      //   242: invokevirtual 319	java/util/ArrayList:clear	()V
      //   245: aload_3
      //   246: athrow
      //   247: aload_0
      //   248: getfield 87	com/android/server/media/MediaRouterService$UserHandler:mTempClients	Ljava/util/ArrayList;
      //   251: invokevirtual 319	java/util/ArrayList:clear	()V
      //   254: return
      //   255: astore_3
      //   256: goto -33 -> 223
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	259	0	this	UserHandler
      //   44	183	1	i	int
      //   42	163	2	j	int
      //   237	9	3	localObject2	Object
      //   255	1	3	localRemoteException	RemoteException
      //   27	100	4	localMediaRouterClientState1	MediaRouterClientState
      //   230	5	4	localObject3	Object
      //   85	51	5	localMediaRouterClientState2	MediaRouterClientState
      // Exception table:
      //   from	to	target	type
      //   122	151	230	finally
      //   158	183	230	finally
      //   112	122	237	finally
      //   190	200	237	finally
      //   207	223	237	finally
      //   232	237	237	finally
      //   207	223	255	android/os/RemoteException
    }
    
    private void updateConnectionTimeout(int paramInt)
    {
      if (paramInt != this.mConnectionTimeoutReason)
      {
        if (this.mConnectionTimeoutReason != 0) {
          removeMessages(9);
        }
        this.mConnectionTimeoutReason = paramInt;
        this.mConnectionTimeoutStartTime = SystemClock.uptimeMillis();
      }
      switch (paramInt)
      {
      default: 
        return;
      case 1: 
      case 2: 
        sendEmptyMessage(9);
        return;
      case 3: 
        sendEmptyMessageDelayed(9, 5000L);
        return;
      }
      sendEmptyMessageDelayed(9, 60000L);
    }
    
    private void updateDiscoveryRequest()
    {
      int k = 0;
      int j = 0;
      for (;;)
      {
        synchronized (MediaRouterService.-get1(this.mService))
        {
          int m = this.mUserRecord.mClientRecords.size();
          i = 0;
          if (i < m)
          {
            MediaRouterService.ClientRecord localClientRecord = (MediaRouterService.ClientRecord)this.mUserRecord.mClientRecords.get(i);
            k |= localClientRecord.mRouteTypes;
            int n = localClientRecord.mActiveScan;
            j |= n;
            i += 1;
            continue;
          }
          if ((k & 0x4) == 0) {
            break label163;
          }
          if (j != 0)
          {
            i = 2;
            if (this.mDiscoveryMode == i) {
              break;
            }
            this.mDiscoveryMode = i;
            j = this.mProviderRecords.size();
            i = 0;
            if (i >= j) {
              break;
            }
            ((ProviderRecord)this.mProviderRecords.get(i)).getProvider().setDiscoveryMode(this.mDiscoveryMode);
            i += 1;
          }
        }
        int i = 1;
        continue;
        label163:
        i = 0;
      }
    }
    
    private void updateProvider(RemoteDisplayProviderProxy paramRemoteDisplayProviderProxy, RemoteDisplayState paramRemoteDisplayState)
    {
      int i = findProviderRecord(paramRemoteDisplayProviderProxy);
      if ((i >= 0) && (((ProviderRecord)this.mProviderRecords.get(i)).updateDescriptor(paramRemoteDisplayState)))
      {
        checkGloballySelectedRouteState();
        scheduleUpdateClientState();
      }
    }
    
    public void addProvider(RemoteDisplayProviderProxy paramRemoteDisplayProviderProxy)
    {
      paramRemoteDisplayProviderProxy.setCallback(this);
      paramRemoteDisplayProviderProxy.setDiscoveryMode(this.mDiscoveryMode);
      paramRemoteDisplayProviderProxy.setSelectedDisplay(null);
      ProviderRecord localProviderRecord = new ProviderRecord(paramRemoteDisplayProviderProxy);
      this.mProviderRecords.add(localProviderRecord);
      localProviderRecord.updateDescriptor(paramRemoteDisplayProviderProxy.getDisplayState());
      scheduleUpdateClientState();
    }
    
    public void dump(PrintWriter paramPrintWriter, String paramString)
    {
      paramPrintWriter.println(paramString + "Handler");
      String str2 = paramString + "  ";
      paramPrintWriter.println(str2 + "mRunning=" + this.mRunning);
      paramPrintWriter.println(str2 + "mDiscoveryMode=" + this.mDiscoveryMode);
      paramPrintWriter.println(str2 + "mGloballySelectedRouteRecord=" + this.mGloballySelectedRouteRecord);
      paramPrintWriter.println(str2 + "mConnectionPhase=" + this.mConnectionPhase);
      paramPrintWriter.println(str2 + "mConnectionTimeoutReason=" + this.mConnectionTimeoutReason);
      StringBuilder localStringBuilder = new StringBuilder().append(str2).append("mConnectionTimeoutStartTime=");
      if (this.mConnectionTimeoutReason != 0) {}
      for (String str1 = TimeUtils.formatUptime(this.mConnectionTimeoutStartTime);; str1 = "<n/a>")
      {
        paramPrintWriter.println(str1);
        this.mWatcher.dump(paramPrintWriter, paramString);
        int j = this.mProviderRecords.size();
        if (j == 0) {
          break;
        }
        int i = 0;
        while (i < j)
        {
          ((ProviderRecord)this.mProviderRecords.get(i)).dump(paramPrintWriter, paramString);
          i += 1;
        }
      }
      paramPrintWriter.println(str2 + "<no providers>");
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        start();
        return;
      case 2: 
        stop();
        return;
      case 3: 
        updateDiscoveryRequest();
        return;
      case 4: 
        selectRoute((String)paramMessage.obj);
        return;
      case 5: 
        unselectRoute((String)paramMessage.obj);
        return;
      case 6: 
        requestSetVolume((String)paramMessage.obj, paramMessage.arg1);
        return;
      case 7: 
        requestUpdateVolume((String)paramMessage.obj, paramMessage.arg1);
        return;
      case 8: 
        updateClientState();
        return;
      }
      connectionTimedOut();
    }
    
    public void onDisplayStateChanged(RemoteDisplayProviderProxy paramRemoteDisplayProviderProxy, RemoteDisplayState paramRemoteDisplayState)
    {
      updateProvider(paramRemoteDisplayProviderProxy, paramRemoteDisplayState);
    }
    
    public void removeProvider(RemoteDisplayProviderProxy paramRemoteDisplayProviderProxy)
    {
      int i = findProviderRecord(paramRemoteDisplayProviderProxy);
      if (i >= 0)
      {
        ((ProviderRecord)this.mProviderRecords.remove(i)).updateDescriptor(null);
        paramRemoteDisplayProviderProxy.setCallback(null);
        paramRemoteDisplayProviderProxy.setDiscoveryMode(0);
        checkGloballySelectedRouteState();
        scheduleUpdateClientState();
      }
    }
    
    static final class ProviderRecord
    {
      private RemoteDisplayState mDescriptor;
      private final RemoteDisplayProviderProxy mProvider;
      private final ArrayList<MediaRouterService.UserHandler.RouteRecord> mRoutes = new ArrayList();
      private final String mUniquePrefix;
      
      public ProviderRecord(RemoteDisplayProviderProxy paramRemoteDisplayProviderProxy)
      {
        this.mProvider = paramRemoteDisplayProviderProxy;
        this.mUniquePrefix = (paramRemoteDisplayProviderProxy.getFlattenedComponentName() + ":");
      }
      
      private String assignRouteUniqueId(String paramString)
      {
        return this.mUniquePrefix + paramString;
      }
      
      private int findRouteByDescriptorId(String paramString)
      {
        int j = this.mRoutes.size();
        int i = 0;
        while (i < j)
        {
          if (((MediaRouterService.UserHandler.RouteRecord)this.mRoutes.get(i)).getDescriptorId().equals(paramString)) {
            return i;
          }
          i += 1;
        }
        return -1;
      }
      
      public void appendClientState(MediaRouterClientState paramMediaRouterClientState)
      {
        int j = this.mRoutes.size();
        int i = 0;
        while (i < j)
        {
          paramMediaRouterClientState.routes.add(((MediaRouterService.UserHandler.RouteRecord)this.mRoutes.get(i)).getInfo());
          i += 1;
        }
      }
      
      public void dump(PrintWriter paramPrintWriter, String paramString)
      {
        paramPrintWriter.println(paramString + this);
        paramString = paramString + "  ";
        this.mProvider.dump(paramPrintWriter, paramString);
        int j = this.mRoutes.size();
        if (j != 0)
        {
          int i = 0;
          while (i < j)
          {
            ((MediaRouterService.UserHandler.RouteRecord)this.mRoutes.get(i)).dump(paramPrintWriter, paramString);
            i += 1;
          }
        }
        paramPrintWriter.println(paramString + "<no routes>");
      }
      
      public MediaRouterService.UserHandler.RouteRecord findRouteByUniqueId(String paramString)
      {
        int j = this.mRoutes.size();
        int i = 0;
        while (i < j)
        {
          MediaRouterService.UserHandler.RouteRecord localRouteRecord = (MediaRouterService.UserHandler.RouteRecord)this.mRoutes.get(i);
          if (localRouteRecord.getUniqueId().equals(paramString)) {
            return localRouteRecord;
          }
          i += 1;
        }
        return null;
      }
      
      public RemoteDisplayProviderProxy getProvider()
      {
        return this.mProvider;
      }
      
      public String getUniquePrefix()
      {
        return this.mUniquePrefix;
      }
      
      public String toString()
      {
        return "Provider " + this.mProvider.getFlattenedComponentName();
      }
      
      public boolean updateDescriptor(RemoteDisplayState paramRemoteDisplayState)
      {
        boolean bool3 = false;
        boolean bool1 = false;
        boolean bool2 = false;
        int j;
        int i;
        if (this.mDescriptor != paramRemoteDisplayState)
        {
          this.mDescriptor = paramRemoteDisplayState;
          j = 0;
          bool1 = bool3;
          i = j;
          if (paramRemoteDisplayState != null)
          {
            if (paramRemoteDisplayState.isValid())
            {
              paramRemoteDisplayState = paramRemoteDisplayState.displays;
              int m = paramRemoteDisplayState.size();
              j = 0;
              i = 0;
              bool1 = bool2;
              if (j >= m) {
                break label325;
              }
              RemoteDisplayState.RemoteDisplayInfo localRemoteDisplayInfo = (RemoteDisplayState.RemoteDisplayInfo)paramRemoteDisplayState.get(j);
              Object localObject = localRemoteDisplayInfo.id;
              int n = findRouteByDescriptorId((String)localObject);
              ArrayList localArrayList;
              int k;
              if (n < 0)
              {
                localObject = new MediaRouterService.UserHandler.RouteRecord(this, (String)localObject, assignRouteUniqueId((String)localObject));
                localArrayList = this.mRoutes;
                k = i + 1;
                localArrayList.add(i, localObject);
                ((MediaRouterService.UserHandler.RouteRecord)localObject).updateDescriptor(localRemoteDisplayInfo);
                bool1 = true;
                i = k;
              }
              for (;;)
              {
                j += 1;
                break;
                if (n < i)
                {
                  Slog.w("MediaRouterService", "Ignoring route descriptor with duplicate id: " + localRemoteDisplayInfo);
                }
                else
                {
                  localObject = (MediaRouterService.UserHandler.RouteRecord)this.mRoutes.get(n);
                  localArrayList = this.mRoutes;
                  k = i + 1;
                  Collections.swap(localArrayList, n, i);
                  bool1 |= ((MediaRouterService.UserHandler.RouteRecord)localObject).updateDescriptor(localRemoteDisplayInfo);
                  i = k;
                }
              }
            }
            Slog.w("MediaRouterService", "Ignoring invalid descriptor from media route provider: " + this.mProvider.getFlattenedComponentName());
            i = j;
            bool1 = bool3;
          }
        }
        label325:
        for (;;)
        {
          j = this.mRoutes.size() - 1;
          while (j >= i)
          {
            ((MediaRouterService.UserHandler.RouteRecord)this.mRoutes.remove(j)).updateDescriptor(null);
            bool1 = true;
            j -= 1;
          }
          return bool1;
        }
      }
    }
    
    static final class RouteRecord
    {
      private RemoteDisplayState.RemoteDisplayInfo mDescriptor;
      private final String mDescriptorId;
      private MediaRouterClientState.RouteInfo mImmutableInfo;
      private final MediaRouterClientState.RouteInfo mMutableInfo;
      private final MediaRouterService.UserHandler.ProviderRecord mProviderRecord;
      
      public RouteRecord(MediaRouterService.UserHandler.ProviderRecord paramProviderRecord, String paramString1, String paramString2)
      {
        this.mProviderRecord = paramProviderRecord;
        this.mDescriptorId = paramString1;
        this.mMutableInfo = new MediaRouterClientState.RouteInfo(paramString2);
      }
      
      private static String computeDescription(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        String str = paramRemoteDisplayInfo.description;
        paramRemoteDisplayInfo = str;
        if (TextUtils.isEmpty(str)) {
          paramRemoteDisplayInfo = null;
        }
        return paramRemoteDisplayInfo;
      }
      
      private static boolean computeEnabled(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        switch (paramRemoteDisplayInfo.status)
        {
        default: 
          return false;
        }
        return true;
      }
      
      private static String computeName(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        return paramRemoteDisplayInfo.name;
      }
      
      private static int computePlaybackStream(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        return 3;
      }
      
      private static int computePlaybackType(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        return 1;
      }
      
      private static int computePresentationDisplayId(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        int j = paramRemoteDisplayInfo.presentationDisplayId;
        int i = j;
        if (j < 0) {
          i = -1;
        }
        return i;
      }
      
      private static int computeStatusCode(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        switch (paramRemoteDisplayInfo.status)
        {
        default: 
          return 0;
        case 0: 
          return 4;
        case 2: 
          return 3;
        case 1: 
          return 5;
        case 3: 
          return 2;
        }
        return 6;
      }
      
      private static int computeSupportedTypes(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        return 7;
      }
      
      private static int computeVolume(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        int i = paramRemoteDisplayInfo.volume;
        int j = paramRemoteDisplayInfo.volumeMax;
        if (i < 0) {
          return 0;
        }
        if (i > j) {
          return j;
        }
        return i;
      }
      
      private static int computeVolumeHandling(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        switch (paramRemoteDisplayInfo.volumeHandling)
        {
        default: 
          return 0;
        }
        return 1;
      }
      
      private static int computeVolumeMax(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        int i = paramRemoteDisplayInfo.volumeMax;
        if (i > 0) {
          return i;
        }
        return 0;
      }
      
      public void dump(PrintWriter paramPrintWriter, String paramString)
      {
        paramPrintWriter.println(paramString + this);
        paramString = paramString + "  ";
        paramPrintWriter.println(paramString + "mMutableInfo=" + this.mMutableInfo);
        paramPrintWriter.println(paramString + "mDescriptorId=" + this.mDescriptorId);
        paramPrintWriter.println(paramString + "mDescriptor=" + this.mDescriptor);
      }
      
      public String getDescriptorId()
      {
        return this.mDescriptorId;
      }
      
      public MediaRouterClientState.RouteInfo getInfo()
      {
        if (this.mImmutableInfo == null) {
          this.mImmutableInfo = new MediaRouterClientState.RouteInfo(this.mMutableInfo);
        }
        return this.mImmutableInfo;
      }
      
      public RemoteDisplayProviderProxy getProvider()
      {
        return this.mProviderRecord.getProvider();
      }
      
      public MediaRouterService.UserHandler.ProviderRecord getProviderRecord()
      {
        return this.mProviderRecord;
      }
      
      public int getStatus()
      {
        return this.mMutableInfo.statusCode;
      }
      
      public String getUniqueId()
      {
        return this.mMutableInfo.id;
      }
      
      public boolean isEnabled()
      {
        return this.mMutableInfo.enabled;
      }
      
      public boolean isValid()
      {
        return this.mDescriptor != null;
      }
      
      public String toString()
      {
        return "Route " + this.mMutableInfo.name + " (" + this.mMutableInfo.id + ")";
      }
      
      public boolean updateDescriptor(RemoteDisplayState.RemoteDisplayInfo paramRemoteDisplayInfo)
      {
        boolean bool3 = false;
        boolean bool2 = false;
        boolean bool1 = bool3;
        if (this.mDescriptor != paramRemoteDisplayInfo)
        {
          this.mDescriptor = paramRemoteDisplayInfo;
          bool1 = bool3;
          if (paramRemoteDisplayInfo != null)
          {
            String str = computeName(paramRemoteDisplayInfo);
            bool1 = bool2;
            if (!Objects.equals(this.mMutableInfo.name, str))
            {
              this.mMutableInfo.name = str;
              bool1 = true;
            }
            str = computeDescription(paramRemoteDisplayInfo);
            if (!Objects.equals(this.mMutableInfo.description, str))
            {
              this.mMutableInfo.description = str;
              bool1 = true;
            }
            int i = computeSupportedTypes(paramRemoteDisplayInfo);
            if (this.mMutableInfo.supportedTypes != i)
            {
              this.mMutableInfo.supportedTypes = i;
              bool1 = true;
            }
            bool2 = computeEnabled(paramRemoteDisplayInfo);
            if (this.mMutableInfo.enabled != bool2)
            {
              this.mMutableInfo.enabled = bool2;
              bool1 = true;
            }
            i = computeStatusCode(paramRemoteDisplayInfo);
            if (this.mMutableInfo.statusCode != i)
            {
              this.mMutableInfo.statusCode = i;
              bool1 = true;
            }
            i = computePlaybackType(paramRemoteDisplayInfo);
            if (this.mMutableInfo.playbackType != i)
            {
              this.mMutableInfo.playbackType = i;
              bool1 = true;
            }
            i = computePlaybackStream(paramRemoteDisplayInfo);
            if (this.mMutableInfo.playbackStream != i)
            {
              this.mMutableInfo.playbackStream = i;
              bool1 = true;
            }
            i = computeVolume(paramRemoteDisplayInfo);
            if (this.mMutableInfo.volume != i)
            {
              this.mMutableInfo.volume = i;
              bool1 = true;
            }
            i = computeVolumeMax(paramRemoteDisplayInfo);
            if (this.mMutableInfo.volumeMax != i)
            {
              this.mMutableInfo.volumeMax = i;
              bool1 = true;
            }
            i = computeVolumeHandling(paramRemoteDisplayInfo);
            if (this.mMutableInfo.volumeHandling != i)
            {
              this.mMutableInfo.volumeHandling = i;
              bool1 = true;
            }
            i = computePresentationDisplayId(paramRemoteDisplayInfo);
            if (this.mMutableInfo.presentationDisplayId != i)
            {
              this.mMutableInfo.presentationDisplayId = i;
              bool1 = true;
            }
          }
        }
        if (bool1) {
          this.mImmutableInfo = null;
        }
        return bool1;
      }
    }
  }
  
  final class UserRecord
  {
    public final ArrayList<MediaRouterService.ClientRecord> mClientRecords = new ArrayList();
    public final MediaRouterService.UserHandler mHandler;
    public MediaRouterClientState mTrustedState;
    public MediaRouterClientState mUntrustedState;
    public final int mUserId;
    
    public UserRecord(int paramInt)
    {
      this.mUserId = paramInt;
      this.mHandler = new MediaRouterService.UserHandler(MediaRouterService.this, this);
    }
    
    public void dump(final PrintWriter paramPrintWriter, final String paramString)
    {
      paramPrintWriter.println(paramString + this);
      paramString = paramString + "  ";
      int j = this.mClientRecords.size();
      if (j != 0)
      {
        int i = 0;
        while (i < j)
        {
          ((MediaRouterService.ClientRecord)this.mClientRecords.get(i)).dump(paramPrintWriter, paramString);
          i += 1;
        }
      }
      paramPrintWriter.println(paramString + "<no clients>");
      paramPrintWriter.println(paramString + "State");
      paramPrintWriter.println(paramString + "mTrustedState=" + this.mTrustedState);
      paramPrintWriter.println(paramString + "mUntrustedState=" + this.mUntrustedState);
      if (!this.mHandler.runWithScissors(new Runnable()
      {
        public void run()
        {
          MediaRouterService.UserRecord.this.mHandler.dump(paramPrintWriter, paramString);
        }
      }, 1000L)) {
        paramPrintWriter.println(paramString + "<could not dump handler state>");
      }
    }
    
    public String toString()
    {
      return "User " + this.mUserId;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/media/MediaRouterService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */