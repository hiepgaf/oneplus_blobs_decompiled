package android.media.effect.effects;

import android.filterpacks.imageproc.DocumentaryFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;

public class DocumentaryEffect
  extends SingleFilterEffect
{
  public DocumentaryEffect(EffectContext paramEffectContext, String paramString)
  {
    super(paramEffectContext, paramString, DocumentaryFilter.class, "image", "image", new Object[0]);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/effects/DocumentaryEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */