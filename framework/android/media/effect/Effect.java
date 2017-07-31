package android.media.effect;

public abstract class Effect
{
  public abstract void apply(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract String getName();
  
  public abstract void release();
  
  public abstract void setParameter(String paramString, Object paramObject);
  
  public void setUpdateListener(EffectUpdateListener paramEffectUpdateListener) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/effect/Effect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */