package android.media.projection;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.hardware.display.VirtualDisplay.Callback;
import android.media.AudioRecord;
import android.os.Handler;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Surface;
import java.util.Iterator;
import java.util.Map;

public final class MediaProjection
{
  private static final String TAG = "MediaProjection";
  private final Map<Callback, CallbackRecord> mCallbacks = new ArrayMap();
  private final Context mContext;
  private final IMediaProjection mImpl;
  
  public MediaProjection(Context paramContext, IMediaProjection paramIMediaProjection)
  {
    this.mContext = paramContext;
    this.mImpl = paramIMediaProjection;
    try
    {
      this.mImpl.start(new MediaProjectionCallback(null));
      return;
    }
    catch (RemoteException paramContext)
    {
      throw new RuntimeException("Failed to start media projection", paramContext);
    }
  }
  
  public AudioRecord createAudioRecord(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return null;
  }
  
  public VirtualDisplay createVirtualDisplay(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Surface paramSurface, VirtualDisplay.Callback paramCallback, Handler paramHandler)
  {
    return ((DisplayManager)this.mContext.getSystemService("display")).createVirtualDisplay(this, paramString, paramInt1, paramInt2, paramInt3, paramSurface, paramInt4, paramCallback, paramHandler);
  }
  
  public VirtualDisplay createVirtualDisplay(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, Surface paramSurface, VirtualDisplay.Callback paramCallback, Handler paramHandler)
  {
    DisplayManager localDisplayManager = (DisplayManager)this.mContext.getSystemService("display");
    if (paramBoolean) {}
    for (int i = 4;; i = 0) {
      return localDisplayManager.createVirtualDisplay(this, paramString, paramInt1, paramInt2, paramInt3, paramSurface, i | 0x10 | 0x2, paramCallback, paramHandler);
    }
  }
  
  public IMediaProjection getProjection()
  {
    return this.mImpl;
  }
  
  public void registerCallback(Callback paramCallback, Handler paramHandler)
  {
    if (paramCallback == null) {
      throw new IllegalArgumentException("callback should not be null");
    }
    Handler localHandler = paramHandler;
    if (paramHandler == null) {
      localHandler = new Handler();
    }
    this.mCallbacks.put(paramCallback, new CallbackRecord(paramCallback, localHandler));
  }
  
  public void stop()
  {
    try
    {
      this.mImpl.stop();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("MediaProjection", "Unable to stop projection", localRemoteException);
    }
  }
  
  public void unregisterCallback(Callback paramCallback)
  {
    if (paramCallback == null) {
      throw new IllegalArgumentException("callback should not be null");
    }
    this.mCallbacks.remove(paramCallback);
  }
  
  public static abstract class Callback
  {
    public void onStop() {}
  }
  
  private static final class CallbackRecord
  {
    private final MediaProjection.Callback mCallback;
    private final Handler mHandler;
    
    public CallbackRecord(MediaProjection.Callback paramCallback, Handler paramHandler)
    {
      this.mCallback = paramCallback;
      this.mHandler = paramHandler;
    }
    
    public void onStop()
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          MediaProjection.CallbackRecord.-get0(MediaProjection.CallbackRecord.this).onStop();
        }
      });
    }
  }
  
  private final class MediaProjectionCallback
    extends IMediaProjectionCallback.Stub
  {
    private MediaProjectionCallback() {}
    
    public void onStop()
    {
      Iterator localIterator = MediaProjection.-get0(MediaProjection.this).values().iterator();
      while (localIterator.hasNext()) {
        ((MediaProjection.CallbackRecord)localIterator.next()).onStop();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/projection/MediaProjection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */