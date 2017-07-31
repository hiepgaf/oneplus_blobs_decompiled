package android.media.effect.effects;

import android.filterpacks.imageproc.BlackWhiteFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;

public class BlackWhiteEffect
  extends SingleFilterEffect
{
  public BlackWhiteEffect(EffectContext paramEffectContext, String paramString)
  {
    super(paramEffectContext, paramString, BlackWhiteFilter.class, "image", "image", new Object[0]);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/effects/BlackWhiteEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */