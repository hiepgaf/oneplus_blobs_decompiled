package com.oneplus.camera;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.oneplus.base.AsyncHandlerObject;
import com.oneplus.base.Log;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.component.BasicComponent;
import com.oneplus.base.component.ComponentOwner;
import com.oneplus.camera.media.MediaType;

public abstract class CameraThreadComponent
  extends BasicComponent
  implements AsyncHandlerObject
{
  private static final int MSG_QUIT = -31607;
  private AsyncHandler m_AsyncHandler;
  private final CameraThread m_CameraThread;
  private final boolean m_HasAsyncHandler;
  private HandlerThread m_WorkerThread;
  
  protected CameraThreadComponent(String paramString, ComponentOwner paramComponentOwner, CameraThread paramCameraThread, boolean paramBoolean)
  {
    this(paramString, paramComponentOwner, paramCameraThread, paramBoolean, false);
  }
  
  protected CameraThreadComponent(String paramString, ComponentOwner paramComponentOwner, CameraThread paramCameraThread, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramString, paramComponentOwner, paramBoolean1);
    this.m_CameraThread = paramCameraThread;
    this.m_HasAsyncHandler = paramBoolean2;
  }
  
  protected CameraThreadComponent(String paramString, CameraThread paramCameraThread, boolean paramBoolean)
  {
    this(paramString, paramCameraThread, paramCameraThread, paramBoolean);
  }
  
  protected CameraThreadComponent(String paramString, CameraThread paramCameraThread, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramString, paramCameraThread, paramCameraThread, paramBoolean1, paramBoolean2);
  }
  
  public final Handler getAsyncHandler()
  {
    return this.m_AsyncHandler;
  }
  
  protected Camera getCamera()
  {
    return (Camera)this.m_CameraThread.get(CameraThread.PROP_CAMERA);
  }
  
  public final CameraThread getCameraThread()
  {
    return this.m_CameraThread;
  }
  
  public final Context getContext()
  {
    return this.m_CameraThread.getContext();
  }
  
  protected MediaType getMediaType()
  {
    return (MediaType)this.m_CameraThread.get(CameraThread.PROP_MEDIA_TYPE);
  }
  
  protected ScreenSize getScreenSize()
  {
    return (ScreenSize)this.m_CameraThread.get(CameraThread.PROP_SCREEN_SIZE);
  }
  
  protected void handleAsyncMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return;
    }
    this.m_AsyncHandler.release();
  }
  
  protected void onDeinitialize()
  {
    if (this.m_HasAsyncHandler) {
      this.m_AsyncHandler.sendEmptyMessage(33929);
    }
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    if (this.m_HasAsyncHandler)
    {
      this.m_WorkerThread = new HandlerThread(this.TAG + " worker thread", -4);
      this.m_WorkerThread.start();
      this.m_AsyncHandler = new AsyncHandler(this, this.m_WorkerThread.getLooper());
    }
  }
  
  private static final class AsyncHandler
    extends Handler
  {
    private Looper m_Looper;
    private volatile CameraThreadComponent m_Owner;
    private final String m_Tag;
    
    public AsyncHandler(CameraThreadComponent paramCameraThreadComponent, Looper paramLooper)
    {
      super();
      this.m_Looper = paramLooper;
      this.m_Owner = paramCameraThreadComponent;
      this.m_Tag = CameraThreadComponent.-get0(paramCameraThreadComponent);
    }
    
    public void handleMessage(Message paramMessage)
    {
      CameraThreadComponent localCameraThreadComponent = this.m_Owner;
      if (localCameraThreadComponent != null)
      {
        localCameraThreadComponent.handleAsyncMessage(paramMessage);
        return;
      }
      Log.e(this.m_Tag, "Owner released, drop message " + paramMessage.what);
    }
    
    public void release()
    {
      this.m_Looper.quit();
      this.m_Owner = null;
      this.m_Looper = null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraThreadComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */