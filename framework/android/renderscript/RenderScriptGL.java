package android.renderscript;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceHolder;

public class RenderScriptGL
  extends RenderScript
{
  int mHeight;
  SurfaceConfig mSurfaceConfig;
  int mWidth;
  
  public RenderScriptGL(Context paramContext, SurfaceConfig paramSurfaceConfig)
  {
    super(paramContext);
    this.mSurfaceConfig = new SurfaceConfig(paramSurfaceConfig);
    int i = paramContext.getApplicationInfo().targetSdkVersion;
    this.mWidth = 0;
    this.mHeight = 0;
    long l = nDeviceCreate();
    int j = paramContext.getResources().getDisplayMetrics().densityDpi;
    this.mContext = nContextCreateGL(l, 0, i, this.mSurfaceConfig.mColorMin, this.mSurfaceConfig.mColorPref, this.mSurfaceConfig.mAlphaMin, this.mSurfaceConfig.mAlphaPref, this.mSurfaceConfig.mDepthMin, this.mSurfaceConfig.mDepthPref, this.mSurfaceConfig.mStencilMin, this.mSurfaceConfig.mStencilPref, this.mSurfaceConfig.mSamplesMin, this.mSurfaceConfig.mSamplesPref, this.mSurfaceConfig.mSamplesQ, j);
    if (this.mContext == 0L) {
      throw new RSDriverException("Failed to create RS context.");
    }
    this.mMessageThread = new RenderScript.MessageThread(this);
    this.mMessageThread.start();
  }
  
  public void bindProgramFragment(ProgramFragment paramProgramFragment)
  {
    validate();
    nContextBindProgramFragment((int)safeID(paramProgramFragment));
  }
  
  public void bindProgramRaster(ProgramRaster paramProgramRaster)
  {
    validate();
    nContextBindProgramRaster((int)safeID(paramProgramRaster));
  }
  
  public void bindProgramStore(ProgramStore paramProgramStore)
  {
    validate();
    nContextBindProgramStore((int)safeID(paramProgramStore));
  }
  
  public void bindProgramVertex(ProgramVertex paramProgramVertex)
  {
    validate();
    nContextBindProgramVertex((int)safeID(paramProgramVertex));
  }
  
  public void bindRootScript(Script paramScript)
  {
    validate();
    nContextBindRootScript((int)safeID(paramScript));
  }
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  public void pause()
  {
    validate();
    nContextPause();
  }
  
  public void resume()
  {
    validate();
    nContextResume();
  }
  
  public void setSurface(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2)
  {
    validate();
    Surface localSurface = null;
    if (paramSurfaceHolder != null) {
      localSurface = paramSurfaceHolder.getSurface();
    }
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    nContextSetSurface(paramInt1, paramInt2, localSurface);
  }
  
  public void setSurfaceTexture(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    validate();
    Surface localSurface = null;
    if (paramSurfaceTexture != null) {
      localSurface = new Surface(paramSurfaceTexture);
    }
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    nContextSetSurface(paramInt1, paramInt2, localSurface);
  }
  
  public static class SurfaceConfig
  {
    int mAlphaMin = 0;
    int mAlphaPref = 0;
    int mColorMin = 8;
    int mColorPref = 8;
    int mDepthMin = 0;
    int mDepthPref = 0;
    int mSamplesMin = 1;
    int mSamplesPref = 1;
    float mSamplesQ = 1.0F;
    int mStencilMin = 0;
    int mStencilPref = 0;
    
    public SurfaceConfig() {}
    
    public SurfaceConfig(SurfaceConfig paramSurfaceConfig)
    {
      this.mDepthMin = paramSurfaceConfig.mDepthMin;
      this.mDepthPref = paramSurfaceConfig.mDepthPref;
      this.mStencilMin = paramSurfaceConfig.mStencilMin;
      this.mStencilPref = paramSurfaceConfig.mStencilPref;
      this.mColorMin = paramSurfaceConfig.mColorMin;
      this.mColorPref = paramSurfaceConfig.mColorPref;
      this.mAlphaMin = paramSurfaceConfig.mAlphaMin;
      this.mAlphaPref = paramSurfaceConfig.mAlphaPref;
      this.mSamplesMin = paramSurfaceConfig.mSamplesMin;
      this.mSamplesPref = paramSurfaceConfig.mSamplesPref;
      this.mSamplesQ = paramSurfaceConfig.mSamplesQ;
    }
    
    private void validateRange(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((paramInt1 < paramInt3) || (paramInt1 > paramInt4)) {
        throw new RSIllegalArgumentException("Minimum value provided out of range.");
      }
      if (paramInt2 < paramInt1) {
        throw new RSIllegalArgumentException("preferred must be >= Minimum.");
      }
    }
    
    public void setAlpha(int paramInt1, int paramInt2)
    {
      validateRange(paramInt1, paramInt2, 0, 8);
      this.mAlphaMin = paramInt1;
      this.mAlphaPref = paramInt2;
    }
    
    public void setColor(int paramInt1, int paramInt2)
    {
      validateRange(paramInt1, paramInt2, 5, 8);
      this.mColorMin = paramInt1;
      this.mColorPref = paramInt2;
    }
    
    public void setDepth(int paramInt1, int paramInt2)
    {
      validateRange(paramInt1, paramInt2, 0, 24);
      this.mDepthMin = paramInt1;
      this.mDepthPref = paramInt2;
    }
    
    public void setSamples(int paramInt1, int paramInt2, float paramFloat)
    {
      validateRange(paramInt1, paramInt2, 1, 32);
      if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
        throw new RSIllegalArgumentException("Quality out of 0-1 range.");
      }
      this.mSamplesMin = paramInt1;
      this.mSamplesPref = paramInt2;
      this.mSamplesQ = paramFloat;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/RenderScriptGL.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */