package android.media.effect.effects;

import android.filterpacks.imageproc.DuotoneFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;

public class DuotoneEffect
  extends SingleFilterEffect
{
  public DuotoneEffect(EffectContext paramEffectContext, String paramString)
  {
    super(paramEffectContext, paramString, DuotoneFilter.class, "image", "image", new Object[0]);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/effects/DuotoneEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */