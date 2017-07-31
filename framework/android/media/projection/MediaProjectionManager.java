package android.media.projection;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Log;
import java.util.Map;

public final class MediaProjectionManager
{
  public static final String EXTRA_APP_TOKEN = "android.media.projection.extra.EXTRA_APP_TOKEN";
  public static final String EXTRA_MEDIA_PROJECTION = "android.media.projection.extra.EXTRA_MEDIA_PROJECTION";
  private static final String TAG = "MediaProjectionManager";
  public static final int TYPE_MIRRORING = 1;
  public static final int TYPE_PRESENTATION = 2;
  public static final int TYPE_SCREEN_CAPTURE = 0;
  private Map<Callback, CallbackDelegate> mCallbacks;
  private Context mContext;
  private IMediaProjectionManager mService;
  
  public MediaProjectionManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mService = IMediaProjectionManager.Stub.asInterface(ServiceManager.getService("media_projection"));
    this.mCallbacks = new ArrayMap();
  }
  
  public void addCallback(Callback paramCallback, Handler paramHandler)
  {
    if (paramCallback == null) {
      throw new IllegalArgumentException("callback must not be null");
    }
    paramHandler = new CallbackDelegate(paramCallback, paramHandler);
    this.mCallbacks.put(paramCallback, paramHandler);
    try
    {
      this.mService.addCallback(paramHandler);
      return;
    }
    catch (RemoteException paramCallback)
    {
      Log.e("MediaProjectionManager", "Unable to add callbacks to MediaProjection service", paramCallback);
    }
  }
  
  public Intent createScreenCaptureIntent()
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.systemui", "com.android.systemui.media.MediaProjectionPermissionActivity");
    return localIntent;
  }
  
  public MediaProjectionInfo getActiveProjectionInfo()
  {
    try
    {
      MediaProjectionInfo localMediaProjectionInfo = this.mService.getActiveProjectionInfo();
      return localMediaProjectionInfo;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("MediaProjectionManager", "Unable to get the active projection info", localRemoteException);
    }
    return null;
  }
  
  public MediaProjection getMediaProjection(int paramInt, Intent paramIntent)
  {
    if ((paramInt != -1) || (paramIntent == null)) {
      return null;
    }
    paramIntent = paramIntent.getIBinderExtra("android.media.projection.extra.EXTRA_MEDIA_PROJECTION");
    if (paramIntent == null) {
      return null;
    }
    return new MediaProjection(this.mContext, IMediaProjection.Stub.asInterface(paramIntent));
  }
  
  public void removeCallback(Callback paramCallback)
  {
    if (paramCallback == null) {
      throw new IllegalArgumentException("callback must not be null");
    }
    paramCallback = (CallbackDelegate)this.mCallbacks.remove(paramCallback);
    if (paramCallback != null) {}
    try
    {
      this.mService.removeCallback(paramCallback);
      return;
    }
    catch (RemoteException paramCallback)
    {
      Log.e("MediaProjectionManager", "Unable to add callbacks to MediaProjection service", paramCallback);
    }
  }
  
  public void stopActiveProjection()
  {
    try
    {
      this.mService.stopActiveProjection();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("MediaProjectionManager", "Unable to stop the currently active media projection", localRemoteException);
    }
  }
  
  public static abstract class Callback
  {
    public abstract void onStart(MediaProjectionInfo paramMediaProjectionInfo);
    
    public abstract void onStop(MediaProjectionInfo paramMediaProjectionInfo);
  }
  
  private static final class CallbackDelegate
    extends IMediaProjectionWatcherCallback.Stub
  {
    private MediaProjectionManager.Callback mCallback;
    private Handler mHandler;
    
    public CallbackDelegate(MediaProjectionManager.Callback paramCallback, Handler paramHandler)
    {
      this.mCallback = paramCallback;
      paramCallback = paramHandler;
      if (paramHandler == null) {
        paramCallback = new Handler();
      }
      this.mHandler = paramCallback;
    }
    
    public void onStart(final MediaProjectionInfo paramMediaProjectionInfo)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          MediaProjectionManager.CallbackDelegate.-get0(MediaProjectionManager.CallbackDelegate.this).onStart(paramMediaProjectionInfo);
        }
      });
    }
    
    public void onStop(final MediaProjectionInfo paramMediaProjectionInfo)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          MediaProjectionManager.CallbackDelegate.-get0(MediaProjectionManager.CallbackDelegate.this).onStop(paramMediaProjectionInfo);
        }
      });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/projection/MediaProjectionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */