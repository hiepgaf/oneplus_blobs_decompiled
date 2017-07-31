package android.renderscript;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

public class RSSurfaceView
  extends SurfaceView
  implements SurfaceHolder.Callback
{
  private RenderScriptGL mRS;
  private SurfaceHolder mSurfaceHolder;
  
  public RSSurfaceView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public RSSurfaceView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private void init()
  {
    getHolder().addCallback(this);
  }
  
  public RenderScriptGL createRenderScriptGL(RenderScriptGL.SurfaceConfig paramSurfaceConfig)
  {
    paramSurfaceConfig = new RenderScriptGL(getContext(), paramSurfaceConfig);
    setRenderScriptGL(paramSurfaceConfig);
    return paramSurfaceConfig;
  }
  
  public void destroyRenderScriptGL()
  {
    try
    {
      this.mRS.destroy();
      this.mRS = null;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public RenderScriptGL getRenderScriptGL()
  {
    return this.mRS;
  }
  
  public void pause()
  {
    if (this.mRS != null) {
      this.mRS.pause();
    }
  }
  
  public void resume()
  {
    if (this.mRS != null) {
      this.mRS.resume();
    }
  }
  
  public void setRenderScriptGL(RenderScriptGL paramRenderScriptGL)
  {
    this.mRS = paramRenderScriptGL;
  }
  
  public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      if (this.mRS != null) {
        this.mRS.setSurface(paramSurfaceHolder, paramInt2, paramInt3);
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
    this.mSurfaceHolder = paramSurfaceHolder;
  }
  
  public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
  {
    try
    {
      if (this.mRS != null) {
        this.mRS.setSurface(null, 0, 0);
      }
      return;
    }
    finally
    {
      paramSurfaceHolder = finally;
      throw paramSurfaceHolder;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/RSSurfaceView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */