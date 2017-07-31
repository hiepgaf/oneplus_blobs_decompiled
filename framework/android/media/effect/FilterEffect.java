package android.media.effect;

import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameManager;
import android.filterfw.format.ImageFormat;

public abstract class FilterEffect
  extends Effect
{
  protected EffectContext mEffectContext;
  private String mName;
  
  protected FilterEffect(EffectContext paramEffectContext, String paramString)
  {
    this.mEffectContext = paramEffectContext;
    this.mName = paramString;
  }
  
  protected void beginGLEffect()
  {
    this.mEffectContext.assertValidGLState();
    this.mEffectContext.saveGLState();
  }
  
  protected void endGLEffect()
  {
    this.mEffectContext.restoreGLState();
  }
  
  protected Frame frameFromTexture(int paramInt1, int paramInt2, int paramInt3)
  {
    Frame localFrame = getFilterContext().getFrameManager().newBoundFrame(ImageFormat.create(paramInt2, paramInt3, 3, 3), 100, paramInt1);
    localFrame.setTimestamp(-1L);
    return localFrame;
  }
  
  protected FilterContext getFilterContext()
  {
    return this.mEffectContext.mFilterContext;
  }
  
  public String getName()
  {
    return this.mName;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/FilterEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */