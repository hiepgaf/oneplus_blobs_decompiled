package android.media.effect.effects;

import android.filterpacks.imageproc.CropRectFilter;
import android.media.effect.EffectContext;
import android.media.effect.SizeChangeEffect;

public class CropEffect
  extends SizeChangeEffect
{
  public CropEffect(EffectContext paramEffectContext, String paramString)
  {
    super(paramEffectContext, paramString, CropRectFilter.class, "image", "image", new Object[0]);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/effects/CropEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */