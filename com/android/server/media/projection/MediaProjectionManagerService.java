package com.android.server.media.projection;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjection.Stub;
import android.media.projection.IMediaProjectionCallback;
import android.media.projection.IMediaProjectionManager.Stub;
import android.media.projection.IMediaProjectionWatcherCallback;
import android.media.projection.MediaProjectionInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.server.SystemService;
import com.android.server.Watchdog;
import com.android.server.Watchdog.Monitor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

public final class MediaProjectionManagerService
  extends SystemService
  implements Watchdog.Monitor
{
  private static final String TAG = "MediaProjectionManagerService";
  private final AppOpsManager mAppOps;
  private final CallbackDelegate mCallbackDelegate;
  private final Context mContext;
  private final Map<IBinder, IBinder.DeathRecipient> mDeathEaters;
  private final Object mLock = new Object();
  private MediaRouter.RouteInfo mMediaRouteInfo;
  private final MediaRouter mMediaRouter;
  private final MediaRouterCallback mMediaRouterCallback;
  private MediaProjection mProjectionGrant;
  private IBinder mProjectionToken;
  
  public MediaProjectionManagerService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mDeathEaters = new ArrayMap();
    this.mCallbackDelegate = new CallbackDelegate();
    this.mAppOps = ((AppOpsManager)this.mContext.getSystemService("appops"));
    this.mMediaRouter = ((MediaRouter)this.mContext.getSystemService("media_router"));
    this.mMediaRouterCallback = new MediaRouterCallback(null);
    Watchdog.getInstance().addMonitor(this);
  }
  
  private void addCallback(final IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
  {
    IBinder.DeathRecipient local1 = new IBinder.DeathRecipient()
    {
      public void binderDied()
      {
        MediaProjectionManagerService.-wrap5(MediaProjectionManagerService.this, paramIMediaProjectionWatcherCallback);
      }
    };
    synchronized (this.mLock)
    {
      this.mCallbackDelegate.add(paramIMediaProjectionWatcherCallback);
      linkDeathRecipientLocked(paramIMediaProjectionWatcherCallback, local1);
      return;
    }
  }
  
  private void dispatchStart(MediaProjection paramMediaProjection)
  {
    this.mCallbackDelegate.dispatchStart(paramMediaProjection);
  }
  
  private void dispatchStop(MediaProjection paramMediaProjection)
  {
    this.mCallbackDelegate.dispatchStop(paramMediaProjection);
  }
  
  private void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("MEDIA PROJECTION MANAGER (dumpsys media_projection)");
    synchronized (this.mLock)
    {
      paramPrintWriter.println("Media Projection: ");
      if (this.mProjectionGrant != null)
      {
        this.mProjectionGrant.dump(paramPrintWriter);
        return;
      }
      paramPrintWriter.println("null");
    }
  }
  
  private MediaProjectionInfo getActiveProjectionInfo()
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mProjectionGrant;
      if (localObject2 == null) {
        return null;
      }
      localObject2 = this.mProjectionGrant.getProjectionInfo();
      return (MediaProjectionInfo)localObject2;
    }
  }
  
  private boolean isValidMediaProjection(IBinder paramIBinder)
  {
    synchronized (this.mLock)
    {
      if (this.mProjectionToken != null)
      {
        boolean bool = this.mProjectionToken.equals(paramIBinder);
        return bool;
      }
      return false;
    }
  }
  
  private void linkDeathRecipientLocked(IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback, IBinder.DeathRecipient paramDeathRecipient)
  {
    try
    {
      paramIMediaProjectionWatcherCallback = paramIMediaProjectionWatcherCallback.asBinder();
      paramIMediaProjectionWatcherCallback.linkToDeath(paramDeathRecipient, 0);
      this.mDeathEaters.put(paramIMediaProjectionWatcherCallback, paramDeathRecipient);
      return;
    }
    catch (RemoteException paramIMediaProjectionWatcherCallback)
    {
      Slog.e("MediaProjectionManagerService", "Unable to link to death for media projection monitoring callback", paramIMediaProjectionWatcherCallback);
    }
  }
  
  private void removeCallback(IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
  {
    synchronized (this.mLock)
    {
      unlinkDeathRecipientLocked(paramIMediaProjectionWatcherCallback);
      this.mCallbackDelegate.remove(paramIMediaProjectionWatcherCallback);
      return;
    }
  }
  
  private void startProjectionLocked(MediaProjection paramMediaProjection)
  {
    if (this.mProjectionGrant != null) {
      this.mProjectionGrant.stop();
    }
    if (this.mMediaRouteInfo != null) {
      this.mMediaRouter.getDefaultRoute().select();
    }
    this.mProjectionToken = paramMediaProjection.asBinder();
    this.mProjectionGrant = paramMediaProjection;
    dispatchStart(paramMediaProjection);
  }
  
  private void stopProjectionLocked(MediaProjection paramMediaProjection)
  {
    this.mProjectionToken = null;
    this.mProjectionGrant = null;
    dispatchStop(paramMediaProjection);
  }
  
  private static String typeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "TYPE_SCREEN_CAPTURE";
    case 1: 
      return "TYPE_MIRRORING";
    }
    return "TYPE_PRESENTATION";
  }
  
  private void unlinkDeathRecipientLocked(IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
  {
    paramIMediaProjectionWatcherCallback = paramIMediaProjectionWatcherCallback.asBinder();
    IBinder.DeathRecipient localDeathRecipient = (IBinder.DeathRecipient)this.mDeathEaters.remove(paramIMediaProjectionWatcherCallback);
    if (localDeathRecipient != null) {
      paramIMediaProjectionWatcherCallback.unlinkToDeath(localDeathRecipient, 0);
    }
  }
  
  public void monitor()
  {
    Object localObject = this.mLock;
  }
  
  public void onStart()
  {
    publishBinderService("media_projection", new BinderService(null), false);
    this.mMediaRouter.addCallback(4, this.mMediaRouterCallback, 8);
  }
  
  public void onSwitchUser(int paramInt)
  {
    this.mMediaRouter.rebindAsUser(paramInt);
    synchronized (this.mLock)
    {
      if (this.mProjectionGrant != null) {
        this.mProjectionGrant.stop();
      }
      return;
    }
  }
  
  private final class BinderService
    extends IMediaProjectionManager.Stub
  {
    private BinderService() {}
    
    private boolean checkPermission(String paramString1, String paramString2)
    {
      boolean bool = false;
      if (MediaProjectionManagerService.-get2(MediaProjectionManagerService.this).getPackageManager().checkPermission(paramString2, paramString1) == 0) {
        bool = true;
      }
      return bool;
    }
    
    public void addCallback(IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
    {
      if (MediaProjectionManagerService.-get2(MediaProjectionManagerService.this).checkCallingPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
        throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to add projection callbacks");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        MediaProjectionManagerService.-wrap3(MediaProjectionManagerService.this, paramIMediaProjectionWatcherCallback);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public IMediaProjection createProjection(int paramInt1, String paramString, int paramInt2, boolean paramBoolean)
    {
      if (MediaProjectionManagerService.-get2(MediaProjectionManagerService.this).checkCallingPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
        throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to grant projection permission");
      }
      if ((paramString == null) || (paramString.isEmpty())) {
        throw new IllegalArgumentException("package name must not be empty");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        paramString = new MediaProjectionManagerService.MediaProjection(MediaProjectionManagerService.this, paramInt2, paramInt1, paramString);
        if (paramBoolean) {
          MediaProjectionManagerService.-get0(MediaProjectionManagerService.this).setMode(46, paramString.uid, paramString.packageName, 0);
        }
        return paramString;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if ((MediaProjectionManagerService.-get2(MediaProjectionManagerService.this) == null) || (MediaProjectionManagerService.-get2(MediaProjectionManagerService.this).checkCallingOrSelfPermission("android.permission.DUMP") != 0))
      {
        paramPrintWriter.println("Permission Denial: can't dump MediaProjectionManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        MediaProjectionManagerService.-wrap4(MediaProjectionManagerService.this, paramPrintWriter);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public MediaProjectionInfo getActiveProjectionInfo()
    {
      if (MediaProjectionManagerService.-get2(MediaProjectionManagerService.this).checkCallingPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
        throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to add projection callbacks");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        MediaProjectionInfo localMediaProjectionInfo = MediaProjectionManagerService.-wrap0(MediaProjectionManagerService.this);
        return localMediaProjectionInfo;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public boolean hasProjectionPermission(int paramInt, String paramString)
    {
      // Byte code:
      //   0: iconst_1
      //   1: istore 4
      //   3: invokestatic 58	android/os/Binder:clearCallingIdentity	()J
      //   6: lstore 5
      //   8: iload 4
      //   10: istore_3
      //   11: aload_0
      //   12: aload_2
      //   13: ldc -101
      //   15: invokespecial 157	com/android/server/media/projection/MediaProjectionManagerService$BinderService:checkPermission	(Ljava/lang/String;Ljava/lang/String;)Z
      //   18: ifne +25 -> 43
      //   21: aload_0
      //   22: getfield 13	com/android/server/media/projection/MediaProjectionManagerService$BinderService:this$0	Lcom/android/server/media/projection/MediaProjectionManagerService;
      //   25: invokestatic 90	com/android/server/media/projection/MediaProjectionManagerService:-get0	(Lcom/android/server/media/projection/MediaProjectionManagerService;)Landroid/app/AppOpsManager;
      //   28: bipush 46
      //   30: iload_1
      //   31: aload_2
      //   32: invokevirtual 161	android/app/AppOpsManager:noteOpNoThrow	(IILjava/lang/String;)I
      //   35: istore_1
      //   36: iload_1
      //   37: ifne +13 -> 50
      //   40: iload 4
      //   42: istore_3
      //   43: lload 5
      //   45: invokestatic 66	android/os/Binder:restoreCallingIdentity	(J)V
      //   48: iload_3
      //   49: ireturn
      //   50: iconst_0
      //   51: istore_3
      //   52: goto -9 -> 43
      //   55: astore_2
      //   56: lload 5
      //   58: invokestatic 66	android/os/Binder:restoreCallingIdentity	(J)V
      //   61: aload_2
      //   62: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	63	0	this	BinderService
      //   0	63	1	paramInt	int
      //   0	63	2	paramString	String
      //   10	42	3	bool1	boolean
      //   1	40	4	bool2	boolean
      //   6	51	5	l	long
      // Exception table:
      //   from	to	target	type
      //   11	36	55	finally
    }
    
    public boolean isValidMediaProjection(IMediaProjection paramIMediaProjection)
    {
      return MediaProjectionManagerService.-wrap1(MediaProjectionManagerService.this, paramIMediaProjection.asBinder());
    }
    
    public void removeCallback(IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
    {
      if (MediaProjectionManagerService.-get2(MediaProjectionManagerService.this).checkCallingPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
        throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to remove projection callbacks");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        MediaProjectionManagerService.-wrap5(MediaProjectionManagerService.this, paramIMediaProjectionWatcherCallback);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void stopActiveProjection()
    {
      if (MediaProjectionManagerService.-get2(MediaProjectionManagerService.this).checkCallingPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
        throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to add projection callbacks");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        if (MediaProjectionManagerService.-get5(MediaProjectionManagerService.this) != null) {
          MediaProjectionManagerService.-get5(MediaProjectionManagerService.this).stop();
        }
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  private static class CallbackDelegate
  {
    private Map<IBinder, IMediaProjectionCallback> mClientCallbacks = new ArrayMap();
    private Handler mHandler = new Handler(Looper.getMainLooper(), null, true);
    private Object mLock = new Object();
    private Map<IBinder, IMediaProjectionWatcherCallback> mWatcherCallbacks = new ArrayMap();
    
    public void add(IMediaProjectionCallback paramIMediaProjectionCallback)
    {
      synchronized (this.mLock)
      {
        this.mClientCallbacks.put(paramIMediaProjectionCallback.asBinder(), paramIMediaProjectionCallback);
        return;
      }
    }
    
    public void add(IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
    {
      synchronized (this.mLock)
      {
        this.mWatcherCallbacks.put(paramIMediaProjectionWatcherCallback.asBinder(), paramIMediaProjectionWatcherCallback);
        return;
      }
    }
    
    public void dispatchStart(MediaProjectionManagerService.MediaProjection paramMediaProjection)
    {
      if (paramMediaProjection == null)
      {
        Slog.e("MediaProjectionManagerService", "Tried to dispatch start notification for a null media projection. Ignoring!");
        return;
      }
      synchronized (this.mLock)
      {
        Iterator localIterator = this.mWatcherCallbacks.values().iterator();
        if (localIterator.hasNext())
        {
          IMediaProjectionWatcherCallback localIMediaProjectionWatcherCallback = (IMediaProjectionWatcherCallback)localIterator.next();
          MediaProjectionInfo localMediaProjectionInfo = paramMediaProjection.getProjectionInfo();
          this.mHandler.post(new MediaProjectionManagerService.WatcherStartCallback(localMediaProjectionInfo, localIMediaProjectionWatcherCallback));
        }
      }
    }
    
    public void dispatchStop(MediaProjectionManagerService.MediaProjection paramMediaProjection)
    {
      if (paramMediaProjection == null)
      {
        Slog.e("MediaProjectionManagerService", "Tried to dispatch stop notification for a null media projection. Ignoring!");
        return;
      }
      Object localObject2;
      synchronized (this.mLock)
      {
        localIterator = this.mClientCallbacks.values().iterator();
        if (localIterator.hasNext())
        {
          localObject2 = (IMediaProjectionCallback)localIterator.next();
          this.mHandler.post(new MediaProjectionManagerService.ClientStopCallback((IMediaProjectionCallback)localObject2));
        }
      }
      Iterator localIterator = this.mWatcherCallbacks.values().iterator();
      while (localIterator.hasNext())
      {
        localObject2 = (IMediaProjectionWatcherCallback)localIterator.next();
        MediaProjectionInfo localMediaProjectionInfo = paramMediaProjection.getProjectionInfo();
        this.mHandler.post(new MediaProjectionManagerService.WatcherStopCallback(localMediaProjectionInfo, (IMediaProjectionWatcherCallback)localObject2));
      }
    }
    
    public void remove(IMediaProjectionCallback paramIMediaProjectionCallback)
    {
      synchronized (this.mLock)
      {
        this.mClientCallbacks.remove(paramIMediaProjectionCallback.asBinder());
        return;
      }
    }
    
    public void remove(IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
    {
      synchronized (this.mLock)
      {
        this.mWatcherCallbacks.remove(paramIMediaProjectionWatcherCallback.asBinder());
        return;
      }
    }
  }
  
  private static final class ClientStopCallback
    implements Runnable
  {
    private IMediaProjectionCallback mCallback;
    
    public ClientStopCallback(IMediaProjectionCallback paramIMediaProjectionCallback)
    {
      this.mCallback = paramIMediaProjectionCallback;
    }
    
    public void run()
    {
      try
      {
        this.mCallback.onStop();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("MediaProjectionManagerService", "Failed to notify media projection has stopped", localRemoteException);
      }
    }
  }
  
  private final class MediaProjection
    extends IMediaProjection.Stub
  {
    private IMediaProjectionCallback mCallback;
    private IBinder.DeathRecipient mDeathEater;
    private IBinder mToken;
    private int mType;
    public final String packageName;
    public final int uid;
    public final UserHandle userHandle;
    
    public MediaProjection(int paramInt1, int paramInt2, String paramString)
    {
      this.mType = paramInt1;
      this.uid = paramInt2;
      this.packageName = paramString;
      this.userHandle = new UserHandle(UserHandle.getUserId(paramInt2));
    }
    
    public int applyVirtualDisplayFlags(int paramInt)
    {
      if (this.mType == 0) {
        return paramInt & 0xFFFFFFF7 | 0x12;
      }
      if (this.mType == 1) {
        return paramInt & 0xFFFFFFEE | 0xA;
      }
      if (this.mType == 2) {
        return paramInt & 0xFFFFFFF7 | 0x13;
      }
      throw new RuntimeException("Unknown MediaProjection type");
    }
    
    public boolean canProjectAudio()
    {
      return (this.mType == 1) || (this.mType == 2);
    }
    
    public boolean canProjectSecureVideo()
    {
      return false;
    }
    
    public boolean canProjectVideo()
    {
      return (this.mType == 1) || (this.mType == 0);
    }
    
    public void dump(PrintWriter paramPrintWriter)
    {
      paramPrintWriter.println("(" + this.packageName + ", uid=" + this.uid + "): " + MediaProjectionManagerService.-wrap2(this.mType));
    }
    
    public MediaProjectionInfo getProjectionInfo()
    {
      return new MediaProjectionInfo(this.packageName, this.userHandle);
    }
    
    public void registerCallback(IMediaProjectionCallback paramIMediaProjectionCallback)
    {
      if (paramIMediaProjectionCallback == null) {
        throw new IllegalArgumentException("callback must not be null");
      }
      MediaProjectionManagerService.-get1(MediaProjectionManagerService.this).add(paramIMediaProjectionCallback);
    }
    
    public void start(final IMediaProjectionCallback paramIMediaProjectionCallback)
    {
      if (paramIMediaProjectionCallback == null) {
        throw new IllegalArgumentException("callback must not be null");
      }
      synchronized (MediaProjectionManagerService.-get3(MediaProjectionManagerService.this))
      {
        if (MediaProjectionManagerService.-wrap1(MediaProjectionManagerService.this, asBinder())) {
          throw new IllegalStateException("Cannot start already started MediaProjection");
        }
      }
      this.mCallback = paramIMediaProjectionCallback;
      registerCallback(this.mCallback);
      try
      {
        this.mToken = paramIMediaProjectionCallback.asBinder();
        this.mDeathEater = new IBinder.DeathRecipient()
        {
          public void binderDied()
          {
            MediaProjectionManagerService.-get1(MediaProjectionManagerService.this).remove(paramIMediaProjectionCallback);
            MediaProjectionManagerService.MediaProjection.this.stop();
          }
        };
        this.mToken.linkToDeath(this.mDeathEater, 0);
        MediaProjectionManagerService.-wrap6(MediaProjectionManagerService.this, this);
        return;
      }
      catch (RemoteException paramIMediaProjectionCallback)
      {
        Slog.w("MediaProjectionManagerService", "MediaProjectionCallbacks must be valid, aborting MediaProjection", paramIMediaProjectionCallback);
      }
    }
    
    public void stop()
    {
      synchronized (MediaProjectionManagerService.-get3(MediaProjectionManagerService.this))
      {
        if (!MediaProjectionManagerService.-wrap1(MediaProjectionManagerService.this, asBinder()))
        {
          Slog.w("MediaProjectionManagerService", "Attempted to stop inactive MediaProjection (uid=" + Binder.getCallingUid() + ", " + "pid=" + Binder.getCallingPid() + ")");
          return;
        }
        MediaProjectionManagerService.-wrap7(MediaProjectionManagerService.this, this);
        this.mToken.unlinkToDeath(this.mDeathEater, 0);
        this.mToken = null;
        unregisterCallback(this.mCallback);
        this.mCallback = null;
        return;
      }
    }
    
    public void unregisterCallback(IMediaProjectionCallback paramIMediaProjectionCallback)
    {
      if (paramIMediaProjectionCallback == null) {
        throw new IllegalArgumentException("callback must not be null");
      }
      MediaProjectionManagerService.-get1(MediaProjectionManagerService.this).remove(paramIMediaProjectionCallback);
    }
  }
  
  private class MediaRouterCallback
    extends MediaRouter.SimpleCallback
  {
    private MediaRouterCallback() {}
    
    public void onRouteSelected(MediaRouter paramMediaRouter, int paramInt, MediaRouter.RouteInfo paramRouteInfo)
    {
      paramMediaRouter = MediaProjectionManagerService.-get3(MediaProjectionManagerService.this);
      if ((paramInt & 0x4) != 0) {}
      try
      {
        MediaProjectionManagerService.-set0(MediaProjectionManagerService.this, paramRouteInfo);
        if (MediaProjectionManagerService.-get5(MediaProjectionManagerService.this) != null) {
          MediaProjectionManagerService.-get5(MediaProjectionManagerService.this).stop();
        }
        return;
      }
      finally
      {
        paramRouteInfo = finally;
        throw paramRouteInfo;
      }
    }
    
    public void onRouteUnselected(MediaRouter paramMediaRouter, int paramInt, MediaRouter.RouteInfo paramRouteInfo)
    {
      if (MediaProjectionManagerService.-get4(MediaProjectionManagerService.this) == paramRouteInfo) {
        MediaProjectionManagerService.-set0(MediaProjectionManagerService.this, null);
      }
    }
  }
  
  private static final class WatcherStartCallback
    implements Runnable
  {
    private IMediaProjectionWatcherCallback mCallback;
    private MediaProjectionInfo mInfo;
    
    public WatcherStartCallback(MediaProjectionInfo paramMediaProjectionInfo, IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
    {
      this.mInfo = paramMediaProjectionInfo;
      this.mCallback = paramIMediaProjectionWatcherCallback;
    }
    
    public void run()
    {
      try
      {
        this.mCallback.onStart(this.mInfo);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("MediaProjectionManagerService", "Failed to notify media projection has stopped", localRemoteException);
      }
    }
  }
  
  private static final class WatcherStopCallback
    implements Runnable
  {
    private IMediaProjectionWatcherCallback mCallback;
    private MediaProjectionInfo mInfo;
    
    public WatcherStopCallback(MediaProjectionInfo paramMediaProjectionInfo, IMediaProjectionWatcherCallback paramIMediaProjectionWatcherCallback)
    {
      this.mInfo = paramMediaProjectionInfo;
      this.mCallback = paramIMediaProjectionWatcherCallback;
    }
    
    public void run()
    {
      try
      {
        this.mCallback.onStop(this.mInfo);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("MediaProjectionManagerService", "Failed to notify media projection has stopped", localRemoteException);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/media/projection/MediaProjectionManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */