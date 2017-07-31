package android.animation;

import java.util.List;

public abstract interface Keyframes
  extends Cloneable
{
  public abstract Keyframes clone();
  
  public abstract List<Keyframe> getKeyframes();
  
  public abstract Class getType();
  
  public abstract Object getValue(float paramFloat);
  
  public abstract void invalidateCache();
  
  public abstract void setEvaluator(TypeEvaluator paramTypeEvaluator);
  
  public static abstract interface FloatKeyframes
    extends Keyframes
  {
    public abstract float getFloatValue(float paramFloat);
  }
  
  public static abstract interface IntKeyframes
    extends Keyframes
  {
    public abstract int getIntValue(float paramFloat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/Keyframes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */