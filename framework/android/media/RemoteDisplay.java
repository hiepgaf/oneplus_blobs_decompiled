package android.media;

import android.os.Handler;
import android.view.Surface;
import dalvik.system.CloseGuard;

public final class RemoteDisplay
{
  public static final int DISPLAY_ERROR_CONNECTION_DROPPED = 2;
  public static final int DISPLAY_ERROR_UNKOWN = 1;
  public static final int DISPLAY_FLAG_SECURE = 1;
  private final CloseGuard mGuard = CloseGuard.get();
  private final Handler mHandler;
  private final Listener mListener;
  private final String mOpPackageName;
  private long mPtr;
  
  private RemoteDisplay(Listener paramListener, Handler paramHandler, String paramString)
  {
    this.mListener = paramListener;
    this.mHandler = paramHandler;
    this.mOpPackageName = paramString;
  }
  
  private void dispose(boolean paramBoolean)
  {
    if (this.mPtr != 0L) {
      if (this.mGuard != null)
      {
        if (!paramBoolean) {
          break label41;
        }
        this.mGuard.warnIfOpen();
      }
    }
    for (;;)
    {
      nativeDispose(this.mPtr);
      this.mPtr = 0L;
      return;
      label41:
      this.mGuard.close();
    }
  }
  
  public static RemoteDisplay listen(String paramString1, Listener paramListener, Handler paramHandler, String paramString2)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("iface must not be null");
    }
    if (paramListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    if (paramHandler == null) {
      throw new IllegalArgumentException("handler must not be null");
    }
    paramListener = new RemoteDisplay(paramListener, paramHandler, paramString2);
    paramListener.startListening(paramString1);
    return paramListener;
  }
  
  private native void nativeDispose(long paramLong);
  
  private native long nativeListen(String paramString1, String paramString2);
  
  private native void nativePause(long paramLong);
  
  private native void nativeResume(long paramLong);
  
  private void notifyDisplayConnected(final Surface paramSurface, final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        RemoteDisplay.-get0(RemoteDisplay.this).onDisplayConnected(paramSurface, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    });
  }
  
  private void notifyDisplayDisconnected()
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        RemoteDisplay.-get0(RemoteDisplay.this).onDisplayDisconnected();
      }
    });
  }
  
  private void notifyDisplayError(final int paramInt)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        RemoteDisplay.-get0(RemoteDisplay.this).onDisplayError(paramInt);
      }
    });
  }
  
  private void startListening(String paramString)
  {
    this.mPtr = nativeListen(paramString, this.mOpPackageName);
    if (this.mPtr == 0L) {
      throw new IllegalStateException("Could not start listening for remote display connection on \"" + paramString + "\"");
    }
    this.mGuard.open("dispose");
  }
  
  public void dispose()
  {
    dispose(false);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      dispose(true);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void pause()
  {
    nativePause(this.mPtr);
  }
  
  public void resume()
  {
    nativeResume(this.mPtr);
  }
  
  public static abstract interface Listener
  {
    public abstract void onDisplayConnected(Surface paramSurface, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract void onDisplayDisconnected();
    
    public abstract void onDisplayError(int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/RemoteDisplay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */