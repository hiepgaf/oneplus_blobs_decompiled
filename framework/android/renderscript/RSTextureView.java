package android.renderscript;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;

public class RSTextureView
  extends TextureView
  implements TextureView.SurfaceTextureListener
{
  private RenderScriptGL mRS;
  private SurfaceTexture mSurfaceTexture;
  
  public RSTextureView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public RSTextureView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private void init()
  {
    setSurfaceTextureListener(this);
  }
  
  public RenderScriptGL createRenderScriptGL(RenderScriptGL.SurfaceConfig paramSurfaceConfig)
  {
    paramSurfaceConfig = new RenderScriptGL(getContext(), paramSurfaceConfig);
    setRenderScriptGL(paramSurfaceConfig);
    if (this.mSurfaceTexture != null) {
      this.mRS.setSurfaceTexture(this.mSurfaceTexture, getWidth(), getHeight());
    }
    return paramSurfaceConfig;
  }
  
  public void destroyRenderScriptGL()
  {
    this.mRS.destroy();
    this.mRS = null;
  }
  
  public RenderScriptGL getRenderScriptGL()
  {
    return this.mRS;
  }
  
  public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    this.mSurfaceTexture = paramSurfaceTexture;
    if (this.mRS != null) {
      this.mRS.setSurfaceTexture(this.mSurfaceTexture, paramInt1, paramInt2);
    }
  }
  
  public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
  {
    this.mSurfaceTexture = paramSurfaceTexture;
    if (this.mRS != null) {
      this.mRS.setSurfaceTexture(null, 0, 0);
    }
    return true;
  }
  
  public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    this.mSurfaceTexture = paramSurfaceTexture;
    if (this.mRS != null) {
      this.mRS.setSurfaceTexture(this.mSurfaceTexture, paramInt1, paramInt2);
    }
  }
  
  public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture)
  {
    this.mSurfaceTexture = paramSurfaceTexture;
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
    if (this.mSurfaceTexture != null) {
      this.mRS.setSurfaceTexture(this.mSurfaceTexture, getWidth(), getHeight());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/RSTextureView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */