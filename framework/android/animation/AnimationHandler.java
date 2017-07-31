package android.animation;

import android.os.SystemClock;
import android.util.ArrayMap;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import java.util.ArrayList;

public class AnimationHandler
{
  public static final ThreadLocal<AnimationHandler> sAnimatorHandler = new ThreadLocal();
  private final ArrayList<AnimationFrameCallback> mAnimationCallbacks = new ArrayList();
  private final ArrayList<AnimationFrameCallback> mCommitCallbacks = new ArrayList();
  private final ArrayMap<AnimationFrameCallback, Long> mDelayedCallbackStartTime = new ArrayMap();
  private final Choreographer.FrameCallback mFrameCallback = new Choreographer.FrameCallback()
  {
    public void doFrame(long paramAnonymousLong)
    {
      AnimationHandler.-wrap2(AnimationHandler.this, AnimationHandler.-wrap0(AnimationHandler.this).getFrameTime());
      if (AnimationHandler.-get0(AnimationHandler.this).size() > 0) {
        AnimationHandler.-wrap0(AnimationHandler.this).postFrameCallback(this);
      }
    }
  };
  private boolean mListDirty = false;
  private AnimationFrameCallbackProvider mProvider;
  
  private void cleanUpList()
  {
    if (this.mListDirty)
    {
      int i = this.mAnimationCallbacks.size() - 1;
      while (i >= 0)
      {
        if (this.mAnimationCallbacks.get(i) == null) {
          this.mAnimationCallbacks.remove(i);
        }
        i -= 1;
      }
      this.mListDirty = false;
    }
  }
  
  private void commitAnimationFrame(AnimationFrameCallback paramAnimationFrameCallback, long paramLong)
  {
    if ((!this.mDelayedCallbackStartTime.containsKey(paramAnimationFrameCallback)) && (this.mCommitCallbacks.contains(paramAnimationFrameCallback)))
    {
      paramAnimationFrameCallback.commitAnimationFrame(paramLong);
      this.mCommitCallbacks.remove(paramAnimationFrameCallback);
    }
  }
  
  private void doAnimationFrame(long paramLong)
  {
    int j = this.mAnimationCallbacks.size();
    long l = SystemClock.uptimeMillis();
    int i = 0;
    if (i < j)
    {
      final AnimationFrameCallback localAnimationFrameCallback = (AnimationFrameCallback)this.mAnimationCallbacks.get(i);
      if (localAnimationFrameCallback == null) {}
      for (;;)
      {
        i += 1;
        break;
        if (isCallbackDue(localAnimationFrameCallback, l))
        {
          localAnimationFrameCallback.doAnimationFrame(paramLong);
          if (this.mCommitCallbacks.contains(localAnimationFrameCallback)) {
            getProvider().postCommitCallback(new Runnable()
            {
              public void run()
              {
                AnimationHandler.-wrap1(AnimationHandler.this, localAnimationFrameCallback, AnimationHandler.-wrap0(AnimationHandler.this).getFrameTime());
              }
            });
          }
        }
      }
    }
    cleanUpList();
  }
  
  public static int getAnimationCount()
  {
    AnimationHandler localAnimationHandler = (AnimationHandler)sAnimatorHandler.get();
    if (localAnimationHandler == null) {
      return 0;
    }
    return localAnimationHandler.getCallbackSize();
  }
  
  private int getCallbackSize()
  {
    int j = 0;
    int i = this.mAnimationCallbacks.size() - 1;
    while (i >= 0)
    {
      int k = j;
      if (this.mAnimationCallbacks.get(i) != null) {
        k = j + 1;
      }
      i -= 1;
      j = k;
    }
    return j;
  }
  
  public static long getFrameDelay()
  {
    return getInstance().getProvider().getFrameDelay();
  }
  
  public static AnimationHandler getInstance()
  {
    if (sAnimatorHandler.get() == null) {
      sAnimatorHandler.set(new AnimationHandler());
    }
    return (AnimationHandler)sAnimatorHandler.get();
  }
  
  private AnimationFrameCallbackProvider getProvider()
  {
    if (this.mProvider == null) {
      this.mProvider = new MyFrameCallbackProvider(null);
    }
    return this.mProvider;
  }
  
  private boolean isCallbackDue(AnimationFrameCallback paramAnimationFrameCallback, long paramLong)
  {
    Long localLong = (Long)this.mDelayedCallbackStartTime.get(paramAnimationFrameCallback);
    if (localLong == null) {
      return true;
    }
    if (localLong.longValue() < paramLong)
    {
      this.mDelayedCallbackStartTime.remove(paramAnimationFrameCallback);
      return true;
    }
    return false;
  }
  
  public static void setFrameDelay(long paramLong)
  {
    getInstance().getProvider().setFrameDelay(paramLong);
  }
  
  public void addAnimationFrameCallback(AnimationFrameCallback paramAnimationFrameCallback, long paramLong)
  {
    if (this.mAnimationCallbacks.size() == 0) {
      getProvider().postFrameCallback(this.mFrameCallback);
    }
    if (!this.mAnimationCallbacks.contains(paramAnimationFrameCallback)) {
      this.mAnimationCallbacks.add(paramAnimationFrameCallback);
    }
    if (paramLong > 0L) {
      this.mDelayedCallbackStartTime.put(paramAnimationFrameCallback, Long.valueOf(SystemClock.uptimeMillis() + paramLong));
    }
  }
  
  public void addOneShotCommitCallback(AnimationFrameCallback paramAnimationFrameCallback)
  {
    if (!this.mCommitCallbacks.contains(paramAnimationFrameCallback)) {
      this.mCommitCallbacks.add(paramAnimationFrameCallback);
    }
  }
  
  void autoCancelBasedOn(ObjectAnimator paramObjectAnimator)
  {
    int i = this.mAnimationCallbacks.size() - 1;
    if (i >= 0)
    {
      AnimationFrameCallback localAnimationFrameCallback = (AnimationFrameCallback)this.mAnimationCallbacks.get(i);
      if (localAnimationFrameCallback == null) {}
      for (;;)
      {
        i -= 1;
        break;
        if (paramObjectAnimator.shouldAutoCancel(localAnimationFrameCallback)) {
          ((Animator)this.mAnimationCallbacks.get(i)).cancel();
        }
      }
    }
  }
  
  public void removeCallback(AnimationFrameCallback paramAnimationFrameCallback)
  {
    this.mCommitCallbacks.remove(paramAnimationFrameCallback);
    this.mDelayedCallbackStartTime.remove(paramAnimationFrameCallback);
    int i = this.mAnimationCallbacks.indexOf(paramAnimationFrameCallback);
    if (i >= 0)
    {
      this.mAnimationCallbacks.set(i, null);
      this.mListDirty = true;
    }
  }
  
  public void setProvider(AnimationFrameCallbackProvider paramAnimationFrameCallbackProvider)
  {
    if (paramAnimationFrameCallbackProvider == null)
    {
      this.mProvider = new MyFrameCallbackProvider(null);
      return;
    }
    this.mProvider = paramAnimationFrameCallbackProvider;
  }
  
  static abstract interface AnimationFrameCallback
  {
    public abstract void commitAnimationFrame(long paramLong);
    
    public abstract void doAnimationFrame(long paramLong);
  }
  
  public static abstract interface AnimationFrameCallbackProvider
  {
    public abstract long getFrameDelay();
    
    public abstract long getFrameTime();
    
    public abstract void postCommitCallback(Runnable paramRunnable);
    
    public abstract void postFrameCallback(Choreographer.FrameCallback paramFrameCallback);
    
    public abstract void setFrameDelay(long paramLong);
  }
  
  private class MyFrameCallbackProvider
    implements AnimationHandler.AnimationFrameCallbackProvider
  {
    final Choreographer mChoreographer = Choreographer.getInstance();
    
    private MyFrameCallbackProvider() {}
    
    public long getFrameDelay()
    {
      return Choreographer.getFrameDelay();
    }
    
    public long getFrameTime()
    {
      return this.mChoreographer.getFrameTime();
    }
    
    public void postCommitCallback(Runnable paramRunnable)
    {
      this.mChoreographer.postCallback(3, paramRunnable, null);
    }
    
    public void postFrameCallback(Choreographer.FrameCallback paramFrameCallback)
    {
      this.mChoreographer.postFrameCallback(paramFrameCallback);
    }
    
    public void setFrameDelay(long paramLong)
    {
      Choreographer.setFrameDelay(paramLong);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/AnimationHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */