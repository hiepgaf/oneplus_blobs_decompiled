package android.filterfw.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class FilterSurfaceView
  extends SurfaceView
  implements SurfaceHolder.Callback
{
  private static int STATE_ALLOCATED = 0;
  private static int STATE_CREATED = 1;
  private static int STATE_INITIALIZED = 2;
  private int mFormat;
  private GLEnvironment mGLEnv;
  private int mHeight;
  private SurfaceHolder.Callback mListener;
  private int mState = STATE_ALLOCATED;
  private int mSurfaceId = -1;
  private int mWidth;
  
  public FilterSurfaceView(Context paramContext)
  {
    super(paramContext);
    getHolder().addCallback(this);
  }
  
  public FilterSurfaceView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    getHolder().addCallback(this);
  }
  
  private void registerSurface()
  {
    this.mSurfaceId = this.mGLEnv.registerSurface(getHolder().getSurface());
    if (this.mSurfaceId < 0) {
      throw new RuntimeException("Could not register Surface: " + getHolder().getSurface() + " in FilterSurfaceView!");
    }
  }
  
  private void unregisterSurface()
  {
    if ((this.mGLEnv != null) && (this.mSurfaceId > 0)) {
      this.mGLEnv.unregisterSurfaceId(this.mSurfaceId);
    }
  }
  
  public void bindToListener(SurfaceHolder.Callback paramCallback, GLEnvironment paramGLEnvironment)
  {
    if (paramCallback == null) {
      try
      {
        throw new NullPointerException("Attempting to bind null filter to SurfaceView!");
      }
      finally {}
    }
    if ((this.mListener != null) && (this.mListener != paramCallback)) {
      throw new RuntimeException("Attempting to bind filter " + paramCallback + " to SurfaceView with another open " + "filter " + this.mListener + " attached already!");
    }
    this.mListener = paramCallback;
    if ((this.mGLEnv != null) && (this.mGLEnv != paramGLEnvironment)) {
      this.mGLEnv.unregisterSurfaceId(this.mSurfaceId);
    }
    this.mGLEnv = paramGLEnvironment;
    if (this.mState >= STATE_CREATED)
    {
      registerSurface();
      this.mListener.surfaceCreated(getHolder());
      if (this.mState == STATE_INITIALIZED) {
        this.mListener.surfaceChanged(getHolder(), this.mFormat, this.mWidth, this.mHeight);
      }
    }
  }
  
  public GLEnvironment getGLEnv()
  {
    try
    {
      GLEnvironment localGLEnvironment = this.mGLEnv;
      return localGLEnvironment;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int getSurfaceId()
  {
    try
    {
      int i = this.mSurfaceId;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      this.mFormat = paramInt1;
      this.mWidth = paramInt2;
      this.mHeight = paramInt3;
      this.mState = STATE_INITIALIZED;
      if (this.mListener != null) {
        this.mListener.surfaceChanged(paramSurfaceHolder, paramInt1, paramInt2, paramInt3);
      }
      return;
    }
    finally
    {
      paramSurfaceHolder = finally;
      throw paramSurfaceHolder;
    }
  }
  
  public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
  {
    try
    {
      this.mState = STATE_CREATED;
      if (this.mGLEnv != null) {
        registerSurface();
      }
      if (this.mListener != null) {
        this.mListener.surfaceCreated(paramSurfaceHolder);
      }
      return;
    }
    finally {}
  }
  
  public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
  {
    try
    {
      this.mState = STATE_ALLOCATED;
      if (this.mListener != null) {
        this.mListener.surfaceDestroyed(paramSurfaceHolder);
      }
      unregisterSurface();
      return;
    }
    finally {}
  }
  
  public void unbind()
  {
    try
    {
      this.mListener = null;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/FilterSurfaceView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */