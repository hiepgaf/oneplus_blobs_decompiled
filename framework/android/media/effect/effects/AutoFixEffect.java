package android.media.effect.effects;

import android.filterpacks.imageproc.AutoFixFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;

public class AutoFixEffect
  extends SingleFilterEffect
{
  public AutoFixEffect(EffectContext paramEffectContext, String paramString)
  {
    super(paramEffectContext, paramString, AutoFixFilter.class, "image", "image", new Object[0]);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/effects/AutoFixEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */