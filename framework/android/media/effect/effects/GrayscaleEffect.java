package android.media.effect.effects;

import android.filterpacks.imageproc.ToGrayFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;

public class GrayscaleEffect
  extends SingleFilterEffect
{
  public GrayscaleEffect(EffectContext paramEffectContext, String paramString)
  {
    super(paramEffectContext, paramString, ToGrayFilter.class, "image", "image", new Object[0]);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/effects/GrayscaleEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */