package com.oneplus.gl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.view.Surface;
import com.oneplus.base.Log;

public class ExternalOESTexture
  extends Texture
{
  private final Object m_Lock = new Object();
  private Surface m_Surface;
  private volatile SurfaceTexture m_SurfaceTexture;
  private int m_TextureId;
  
  public ExternalOESTexture()
  {
    this(false);
  }
  
  public ExternalOESTexture(boolean paramBoolean)
  {
    super(36197);
    if ((paramBoolean) && (isEglContextReady())) {
      getObjectId();
    }
  }
  
  public int getObjectId()
  {
    throwIfNotAccessible();
    if (this.m_TextureId <= 0)
    {
      this.m_TextureId = createNativeTexture();
      if (this.m_TextureId <= 0) {
        break label87;
      }
      GLES20.glBindTexture(36197, this.m_TextureId);
      GLES20.glTexParameteri(36197, 10242, 33071);
      GLES20.glTexParameteri(36197, 10243, 33071);
      GLES20.glTexParameteri(36197, 10241, 9729);
      GLES20.glTexParameteri(36197, 10240, 9729);
      GLES20.glBindTexture(36197, 0);
    }
    for (;;)
    {
      return this.m_TextureId;
      label87:
      EglContextManager.throwEglError("Fail to create texture");
    }
  }
  
  public Surface getSurface()
  {
    if (this.m_Surface == null) {
      this.m_Surface = new Surface(getSurfaceTexture());
    }
    return this.m_Surface;
  }
  
  public SurfaceTexture getSurfaceTexture()
  {
    synchronized (this.m_Lock)
    {
      if (isDependencyThread()) {
        getObjectId();
      }
      int i;
      do
      {
        SurfaceTexture localSurfaceTexture = this.m_SurfaceTexture;
        return localSurfaceTexture;
        i = this.m_TextureId;
      } while (i == 0);
      this.m_SurfaceTexture = new SurfaceTexture(i);
      Log.v(this.TAG, "getSurfaceTexture() - Create SurfaceTexture : ", this.m_SurfaceTexture);
    }
  }
  
  public long getTimestamp()
  {
    if (this.m_SurfaceTexture != null) {
      return this.m_SurfaceTexture.getTimestamp();
    }
    return 0L;
  }
  
  public void getTransformMatrix(float[] paramArrayOfFloat)
  {
    if (this.m_SurfaceTexture != null)
    {
      this.m_SurfaceTexture.getTransformMatrix(paramArrayOfFloat);
      return;
    }
    Matrix.setIdentityM(paramArrayOfFloat, 0);
  }
  
  public boolean isAvailable()
  {
    boolean bool = true;
    if (isDependencyThread())
    {
      if (this.m_TextureId <= 0) {
        bool = isEglContextReady();
      }
      return bool;
    }
    return this.m_SurfaceTexture != null;
  }
  
  protected void onEglContextDestroying()
  {
    if (this.m_TextureId > 0)
    {
      if (this.m_Surface != null)
      {
        this.m_Surface.release();
        this.m_Surface = null;
      }
      if (this.m_SurfaceTexture != null)
      {
        this.m_SurfaceTexture.release();
        this.m_SurfaceTexture = null;
      }
      this.m_TextureId = 0;
    }
    super.onEglContextDestroying();
  }
  
  protected void onRelease()
  {
    releaseInternalResources();
    super.onRelease();
  }
  
  public ExternalOESTexture refresh()
  {
    throwIfNotAccessible();
    SurfaceTexture localSurfaceTexture = getSurfaceTexture();
    if (EglContextManager.isGLProfilingEnabled()) {}
    for (long l = SystemClock.elapsedRealtime();; l = 0L)
    {
      localSurfaceTexture.updateTexImage();
      if (EglContextManager.isGLProfilingEnabled())
      {
        l = SystemClock.elapsedRealtime() - l;
        if (l > 20L) {
          Log.w(this.TAG, "refresh() - Take " + l + " ms to call updateTexImage(), SurfaceTexture : " + localSurfaceTexture);
        }
      }
      return this;
    }
  }
  
  public void releaseInternalResources()
  {
    verifyAccess();
    synchronized (this.m_Lock)
    {
      if (this.m_TextureId > 0)
      {
        if (this.m_Surface != null)
        {
          this.m_Surface.release();
          this.m_Surface = null;
        }
        if (this.m_SurfaceTexture != null)
        {
          this.m_SurfaceTexture.release();
          this.m_SurfaceTexture = null;
        }
        destroyNativeTexture(this.m_TextureId);
        this.m_TextureId = 0;
      }
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/ExternalOESTexture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */