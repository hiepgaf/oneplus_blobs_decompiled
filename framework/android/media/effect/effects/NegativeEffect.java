package android.media.effect.effects;

import android.filterpacks.imageproc.NegativeFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;

public class NegativeEffect
  extends SingleFilterEffect
{
  public NegativeEffect(EffectContext paramEffectContext, String paramString)
  {
    super(paramEffectContext, paramString, NegativeFilter.class, "image", "image", new Object[0]);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/effects/NegativeEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */