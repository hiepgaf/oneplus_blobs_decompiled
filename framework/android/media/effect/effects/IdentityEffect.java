package android.media.effect.effects;

import android.filterfw.core.Frame;
import android.media.effect.EffectContext;
import android.media.effect.FilterEffect;

public class IdentityEffect
  extends FilterEffect
{
  public IdentityEffect(EffectContext paramEffectContext, String paramString)
  {
    super(paramEffectContext, paramString);
  }
  
  public void apply(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    beginGLEffect();
    Frame localFrame1 = frameFromTexture(paramInt1, paramInt2, paramInt3);
    Frame localFrame2 = frameFromTexture(paramInt4, paramInt2, paramInt3);
    localFrame2.setDataFromFrame(localFrame1);
    localFrame1.release();
    localFrame2.release();
    endGLEffect();
  }
  
  public void release() {}
  
  public void setParameter(String paramString, Object paramObject)
  {
    throw new IllegalArgumentException("Unknown parameter " + paramString + " for IdentityEffect!");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/effects/IdentityEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */