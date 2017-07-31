package android.graphics.drawable;

public abstract interface Animatable2
  extends Animatable
{
  public abstract void clearAnimationCallbacks();
  
  public abstract void registerAnimationCallback(AnimationCallback paramAnimationCallback);
  
  public abstract boolean unregisterAnimationCallback(AnimationCallback paramAnimationCallback);
  
  public static abstract class AnimationCallback
  {
    public void onAnimationEnd(Drawable paramDrawable) {}
    
    public void onAnimationStart(Drawable paramDrawable) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/Animatable2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */