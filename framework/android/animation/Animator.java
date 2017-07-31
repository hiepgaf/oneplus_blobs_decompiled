package android.animation;

import android.content.res.ConstantState;
import java.util.ArrayList;

public abstract class Animator
  implements Cloneable
{
  public static final long DURATION_INFINITE = -1L;
  int mChangingConfigurations = 0;
  private AnimatorConstantState mConstantState;
  ArrayList<AnimatorListener> mListeners = null;
  ArrayList<AnimatorPauseListener> mPauseListeners = null;
  boolean mPaused = false;
  
  public void addListener(AnimatorListener paramAnimatorListener)
  {
    if (this.mListeners == null) {
      this.mListeners = new ArrayList();
    }
    this.mListeners.add(paramAnimatorListener);
  }
  
  public void addPauseListener(AnimatorPauseListener paramAnimatorPauseListener)
  {
    if (this.mPauseListeners == null) {
      this.mPauseListeners = new ArrayList();
    }
    this.mPauseListeners.add(paramAnimatorPauseListener);
  }
  
  public void appendChangingConfigurations(int paramInt)
  {
    this.mChangingConfigurations |= paramInt;
  }
  
  public boolean canReverse()
  {
    return false;
  }
  
  public void cancel() {}
  
  public Animator clone()
  {
    try
    {
      Animator localAnimator = (Animator)super.clone();
      if (this.mListeners != null) {
        localAnimator.mListeners = new ArrayList(this.mListeners);
      }
      if (this.mPauseListeners != null) {
        localAnimator.mPauseListeners = new ArrayList(this.mPauseListeners);
      }
      return localAnimator;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError();
    }
  }
  
  public ConstantState<Animator> createConstantState()
  {
    return new AnimatorConstantState(this);
  }
  
  public void end() {}
  
  public int getChangingConfigurations()
  {
    return this.mChangingConfigurations;
  }
  
  public abstract long getDuration();
  
  public TimeInterpolator getInterpolator()
  {
    return null;
  }
  
  public ArrayList<AnimatorListener> getListeners()
  {
    return this.mListeners;
  }
  
  public abstract long getStartDelay();
  
  public long getTotalDuration()
  {
    long l = getDuration();
    if (l == -1L) {
      return -1L;
    }
    return getStartDelay() + l;
  }
  
  public boolean isPaused()
  {
    return this.mPaused;
  }
  
  public abstract boolean isRunning();
  
  public boolean isStarted()
  {
    return isRunning();
  }
  
  public void pause()
  {
    if ((!isStarted()) || (this.mPaused)) {}
    for (;;)
    {
      return;
      this.mPaused = true;
      if (this.mPauseListeners != null)
      {
        ArrayList localArrayList = (ArrayList)this.mPauseListeners.clone();
        int j = localArrayList.size();
        int i = 0;
        while (i < j)
        {
          ((AnimatorPauseListener)localArrayList.get(i)).onAnimationPause(this);
          i += 1;
        }
      }
    }
  }
  
  public void removeAllListeners()
  {
    if (this.mListeners != null)
    {
      this.mListeners.clear();
      this.mListeners = null;
    }
    if (this.mPauseListeners != null)
    {
      this.mPauseListeners.clear();
      this.mPauseListeners = null;
    }
  }
  
  public void removeListener(AnimatorListener paramAnimatorListener)
  {
    if (this.mListeners == null) {
      return;
    }
    this.mListeners.remove(paramAnimatorListener);
    if (this.mListeners.size() == 0) {
      this.mListeners = null;
    }
  }
  
  public void removePauseListener(AnimatorPauseListener paramAnimatorPauseListener)
  {
    if (this.mPauseListeners == null) {
      return;
    }
    this.mPauseListeners.remove(paramAnimatorPauseListener);
    if (this.mPauseListeners.size() == 0) {
      this.mPauseListeners = null;
    }
  }
  
  public void resume()
  {
    if (this.mPaused)
    {
      this.mPaused = false;
      if (this.mPauseListeners != null)
      {
        ArrayList localArrayList = (ArrayList)this.mPauseListeners.clone();
        int j = localArrayList.size();
        int i = 0;
        while (i < j)
        {
          ((AnimatorPauseListener)localArrayList.get(i)).onAnimationResume(this);
          i += 1;
        }
      }
    }
  }
  
  public void reverse()
  {
    throw new IllegalStateException("Reverse is not supported");
  }
  
  public void setAllowRunningAsynchronously(boolean paramBoolean) {}
  
  public void setChangingConfigurations(int paramInt)
  {
    this.mChangingConfigurations = paramInt;
  }
  
  public abstract Animator setDuration(long paramLong);
  
  public abstract void setInterpolator(TimeInterpolator paramTimeInterpolator);
  
  public abstract void setStartDelay(long paramLong);
  
  public void setTarget(Object paramObject) {}
  
  public void setupEndValues() {}
  
  public void setupStartValues() {}
  
  public void start() {}
  
  private static class AnimatorConstantState
    extends ConstantState<Animator>
  {
    final Animator mAnimator;
    int mChangingConf;
    
    public AnimatorConstantState(Animator paramAnimator)
    {
      this.mAnimator = paramAnimator;
      Animator.-set0(this.mAnimator, this);
      this.mChangingConf = this.mAnimator.getChangingConfigurations();
    }
    
    public int getChangingConfigurations()
    {
      return this.mChangingConf;
    }
    
    public Animator newInstance()
    {
      Animator localAnimator = this.mAnimator.clone();
      Animator.-set0(localAnimator, this);
      return localAnimator;
    }
  }
  
  public static abstract interface AnimatorListener
  {
    public abstract void onAnimationCancel(Animator paramAnimator);
    
    public abstract void onAnimationEnd(Animator paramAnimator);
    
    public abstract void onAnimationRepeat(Animator paramAnimator);
    
    public abstract void onAnimationStart(Animator paramAnimator);
  }
  
  public static abstract interface AnimatorPauseListener
  {
    public abstract void onAnimationPause(Animator paramAnimator);
    
    public abstract void onAnimationResume(Animator paramAnimator);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/Animator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */