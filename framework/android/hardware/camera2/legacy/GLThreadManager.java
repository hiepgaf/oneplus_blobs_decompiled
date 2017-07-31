package android.hardware.camera2.legacy;

import android.graphics.SurfaceTexture;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.Collection;

public class GLThreadManager
{
  private static final boolean DEBUG = false;
  private static final int MSG_ALLOW_FRAMES = 5;
  private static final int MSG_CLEANUP = 3;
  private static final int MSG_DROP_FRAMES = 4;
  private static final int MSG_NEW_CONFIGURATION = 1;
  private static final int MSG_NEW_FRAME = 2;
  private final String TAG;
  private CaptureCollector mCaptureCollector;
  private final CameraDeviceState mDeviceState;
  private final Handler.Callback mGLHandlerCb = new Handler.Callback()
  {
    private boolean mCleanup = false;
    private boolean mConfigured = false;
    private boolean mDroppingFrames = false;
    
    public boolean handleMessage(Message paramAnonymousMessage)
    {
      if (this.mCleanup) {
        return true;
      }
      for (;;)
      {
        try
        {
          switch (paramAnonymousMessage.what)
          {
          case 0: 
            Log.e(GLThreadManager.-get0(GLThreadManager.this), "Unhandled message " + paramAnonymousMessage.what + " on GLThread.");
            return true;
          }
        }
        catch (Exception paramAnonymousMessage)
        {
          Log.e(GLThreadManager.-get0(GLThreadManager.this), "Received exception on GL render thread: ", paramAnonymousMessage);
          GLThreadManager.-get2(GLThreadManager.this).setError(1);
          return true;
        }
        paramAnonymousMessage = (GLThreadManager.ConfigureHolder)paramAnonymousMessage.obj;
        GLThreadManager.-get3(GLThreadManager.this).cleanupEGLContext();
        GLThreadManager.-get3(GLThreadManager.this).configureSurfaces(paramAnonymousMessage.surfaces);
        GLThreadManager.-set0(GLThreadManager.this, (CaptureCollector)Preconditions.checkNotNull(paramAnonymousMessage.collector));
        paramAnonymousMessage.condition.open();
        this.mConfigured = true;
        return true;
        if (this.mDroppingFrames)
        {
          Log.w(GLThreadManager.-get0(GLThreadManager.this), "Ignoring frame.");
          return true;
        }
        if (!this.mConfigured) {
          Log.e(GLThreadManager.-get0(GLThreadManager.this), "Dropping frame, EGL context not configured!");
        }
        GLThreadManager.-get3(GLThreadManager.this).drawIntoSurfaces(GLThreadManager.-get1(GLThreadManager.this));
        return true;
        GLThreadManager.-get3(GLThreadManager.this).cleanupEGLContext();
        this.mCleanup = true;
        this.mConfigured = false;
        return true;
        this.mDroppingFrames = true;
        return true;
        this.mDroppingFrames = false;
        return true;
      }
      return true;
    }
  };
  private final RequestHandlerThread mGLHandlerThread;
  private final RequestThreadManager.FpsCounter mPrevCounter = new RequestThreadManager.FpsCounter("GL Preview Producer");
  private final SurfaceTextureRenderer mTextureRenderer;
  
  public GLThreadManager(int paramInt1, int paramInt2, CameraDeviceState paramCameraDeviceState)
  {
    this.mTextureRenderer = new SurfaceTextureRenderer(paramInt2);
    this.TAG = String.format("CameraDeviceGLThread-%d", new Object[] { Integer.valueOf(paramInt1) });
    this.mGLHandlerThread = new RequestHandlerThread(this.TAG, this.mGLHandlerCb);
    this.mDeviceState = paramCameraDeviceState;
  }
  
  public void allowNewFrames()
  {
    this.mGLHandlerThread.getHandler().sendEmptyMessage(5);
  }
  
  public SurfaceTexture getCurrentSurfaceTexture()
  {
    return this.mTextureRenderer.getSurfaceTexture();
  }
  
  public void ignoreNewFrames()
  {
    this.mGLHandlerThread.getHandler().sendEmptyMessage(4);
  }
  
  public void queueNewFrame()
  {
    Handler localHandler = this.mGLHandlerThread.getHandler();
    if (!localHandler.hasMessages(2))
    {
      localHandler.sendMessage(localHandler.obtainMessage(2));
      return;
    }
    Log.e(this.TAG, "GLThread dropping frame.  Not consuming frames quickly enough!");
  }
  
  public void quit()
  {
    Handler localHandler = this.mGLHandlerThread.getHandler();
    localHandler.sendMessageAtFrontOfQueue(localHandler.obtainMessage(3));
    this.mGLHandlerThread.quitSafely();
    try
    {
      this.mGLHandlerThread.join();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      Log.e(this.TAG, String.format("Thread %s (%d) interrupted while quitting.", new Object[] { this.mGLHandlerThread.getName(), Long.valueOf(this.mGLHandlerThread.getId()) }));
    }
  }
  
  public void setConfigurationAndWait(Collection<Pair<Surface, Size>> paramCollection, CaptureCollector paramCaptureCollector)
  {
    Preconditions.checkNotNull(paramCaptureCollector, "collector must not be null");
    Handler localHandler = this.mGLHandlerThread.getHandler();
    ConditionVariable localConditionVariable = new ConditionVariable(false);
    localHandler.sendMessage(localHandler.obtainMessage(1, 0, 0, new ConfigureHolder(localConditionVariable, paramCollection, paramCaptureCollector)));
    localConditionVariable.block();
  }
  
  public void start()
  {
    this.mGLHandlerThread.start();
  }
  
  public void waitUntilIdle()
  {
    this.mGLHandlerThread.waitUntilIdle();
  }
  
  public void waitUntilStarted()
  {
    this.mGLHandlerThread.waitUntilStarted();
  }
  
  private static class ConfigureHolder
  {
    public final CaptureCollector collector;
    public final ConditionVariable condition;
    public final Collection<Pair<Surface, Size>> surfaces;
    
    public ConfigureHolder(ConditionVariable paramConditionVariable, Collection<Pair<Surface, Size>> paramCollection, CaptureCollector paramCaptureCollector)
    {
      this.condition = paramConditionVariable;
      this.surfaces = paramCollection;
      this.collector = paramCaptureCollector;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/GLThreadManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */